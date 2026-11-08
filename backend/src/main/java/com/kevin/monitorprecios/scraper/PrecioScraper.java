package com.kevin.monitorprecios.scraper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kevin.monitorprecios.scraper.Lectura.Estado;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PrecioScraper {

    private final ObjectMapper mapper = new ObjectMapper();

    public Lectura leer(String url) {
        try {
            Document pagina = Jsoup.connect(url).userAgent("Mozilla/5.0").timeout(10000).get();
            return interpretar(pagina);
        } catch (HttpStatusException e) {
            if (e.getStatusCode() == 404 || e.getStatusCode() == 410) {
                log.info("El producto ya no existe en {} (HTTP {})", url, e.getStatusCode());
                return Lectura.sinPrecio(Estado.RETIRADO);
            }
            log.warn("No se pudo leer {}: HTTP {}", url, e.getStatusCode());
        } catch (Exception e) {
            log.warn("No se pudo leer el precio de {}: {}", url, e.getMessage());
        }
        return Lectura.sinPrecio(Estado.SIN_DATOS);
    }

    Lectura interpretar(Document pagina) {
        BigDecimal precio = null;
        String nombre = "";
        List<String> disponibilidad = new ArrayList<>();

        for (Element script : pagina.select("script[type=application/ld+json]")) {
            JsonNode json = leerJson(script.html());
            if (json == null) {
                continue;
            }
            if (precio == null) {
                precio = precioDelJson(json);
            }
            if (nombre.isEmpty()) {
                nombre = nombreDelJson(json);
            }
            for (JsonNode valor : json.findValues("availability")) {
                agregar(disponibilidad, valor.asText());
            }
        }

        for (Element etiqueta : pagina.select("[itemprop=availability]")) {
            agregar(disponibilidad, etiqueta.hasAttr("href") ? etiqueta.attr("href") : etiqueta.attr("content"));
        }

        if (todos(disponibilidad, "outofstock", "soldout")) {
            return Lectura.sinPrecio(Estado.AGOTADO);
        }
        if (todos(disponibilidad, "discontinued")) {
            return Lectura.sinPrecio(Estado.RETIRADO);
        }

        if (precio == null) {
            precio = aNumero(pagina.select("meta[itemprop=price]").attr("content"));
        }

        if (nombre.isEmpty()) {
            nombre = nombreDeLaPagina(pagina);
        }

        if (precio != null && precio.signum() > 0) {
            return new Lectura(Estado.DISPONIBLE, precio, limpiarNombre(nombre));
        }
        return Lectura.sinPrecio(Estado.SIN_DATOS);
    }

    private String nombreDelJson(JsonNode json) {
        if (json.isArray()) {
            for (JsonNode elemento : json) {
                String nombre = nombreDelJson(elemento);
                if (!nombre.isEmpty()) {
                    return nombre;
                }
            }
            return "";
        }

        if (json.path("@type").toString().contains("Product")) {
            return json.path("name").asText("");
        }

        JsonNode grafo = json.path("@graph");
        return grafo.isArray() ? nombreDelJson(grafo) : "";
    }

    private String nombreDeLaPagina(Document pagina) {
        String titulo = pagina.select("meta[property=og:title]").attr("content");
        return titulo.isBlank() ? pagina.title() : titulo;
    }

    private String limpiarNombre(String texto) {
        String limpio = texto.replaceAll("\\s+", " ").trim();
        limpio = limpio.replaceFirst("(?i)\\s*[|\\-–]\\s*(éxito|exito|alkosto|mattelsa)[^|]*$", "");
        if (limpio.length() > 120) {
            limpio = limpio.substring(0, 120).trim();
        }
        return limpio;
    }

    private JsonNode leerJson(String texto) {
        try {
            return mapper.readTree(texto);
        } catch (Exception e) {
            return null;
        }
    }

    private BigDecimal precioDelJson(JsonNode json) {
        JsonNode precio = json.findValue("price");
        if (precio == null) {
            return null;
        }
        return aNumero(precio.asText());
    }

    private BigDecimal aNumero(String texto) {
        if (texto == null || texto.isBlank()) {
            return null;
        }
        try {
            return new BigDecimal(texto.trim().replace(",", "."));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private void agregar(List<String> lista, String valor) {
        if (!valor.isBlank()) {
            lista.add(valor);
        }
    }

    private boolean todos(List<String> valores, String... palabras) {
        if (valores.isEmpty()) {
            return false;
        }
        for (String valor : valores) {
            String texto = valor.toLowerCase();
            boolean coincide = false;
            for (String palabra : palabras) {
                if (texto.contains(palabra)) {
                    coincide = true;
                }
            }
            if (!coincide) {
                return false;
            }
        }
        return true;
    }
}
