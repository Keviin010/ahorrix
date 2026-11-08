package com.kevin.monitorprecios.scraper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;

class PrecioScraperTest {

    private final PrecioScraper scraper = new PrecioScraper();

    private Lectura leer(String html) {
        Document pagina = Jsoup.parse(html);
        return scraper.interpretar(pagina);
    }

    private String jsonLd(String contenido) {
        return "<html><head><script type=\"application/ld+json\">" + contenido + "</script></head><body></body></html>";
    }

    @Test
    void leeElPrecioDelJsonLd() {
        Lectura lectura = leer(jsonLd("{\"@type\":\"Product\",\"offers\":{\"price\":2500000,\"availability\":\"https://schema.org/InStock\"}}"));

        assertEquals(Lectura.Estado.DISPONIBLE, lectura.estado());
        assertEquals(new BigDecimal("2500000"), lectura.precio());
    }

    @Test
    void leeUnPrecioEscritoComoTexto() {
        Lectura lectura = leer(jsonLd("{\"offers\":{\"price\":\"1899000\"}}"));

        assertEquals(Lectura.Estado.DISPONIBLE, lectura.estado());
        assertEquals(new BigDecimal("1899000"), lectura.precio());
    }

    @Test
    void aceptaLaComaComoSeparadorDecimal() {
        Lectura lectura = leer(jsonLd("{\"offers\":{\"price\":\"1999,90\"}}"));

        assertEquals(new BigDecimal("1999.90"), lectura.precio());
    }

    @Test
    void usaElMetaDePrecioSiNoHayJsonLd() {
        Lectura lectura = leer("<html><body><meta itemprop=\"price\" content=\"349900\"></body></html>");

        assertEquals(Lectura.Estado.DISPONIBLE, lectura.estado());
        assertEquals(new BigDecimal("349900"), lectura.precio());
    }

    @Test
    void ignoraUnJsonLdRotoYUsaElMeta() {
        String html = "<html><head><script type=\"application/ld+json\">{esto no es json</script></head>"
                + "<body><meta itemprop=\"price\" content=\"120000\"></body></html>";

        Lectura lectura = leer(html);

        assertEquals(Lectura.Estado.DISPONIBLE, lectura.estado());
        assertEquals(new BigDecimal("120000"), lectura.precio());
    }

    @Test
    void sinPrecioDevuelveSinDatos() {
        Lectura lectura = leer("<html><body><p>Hola</p></body></html>");

        assertEquals(Lectura.Estado.SIN_DATOS, lectura.estado());
        assertNull(lectura.precio());
    }

    @Test
    void unPrecioEnCeroDevuelveSinDatos() {
        Lectura lectura = leer(jsonLd("{\"offers\":{\"price\":0}}"));

        assertEquals(Lectura.Estado.SIN_DATOS, lectura.estado());
    }

    @Test
    void unPrecioQueNoEsNumeroDevuelveSinDatos() {
        Lectura lectura = leer(jsonLd("{\"offers\":{\"price\":\"consultar\"}}"));

        assertEquals(Lectura.Estado.SIN_DATOS, lectura.estado());
    }

    @Test
    void unProductoAgotadoEnElJsonLdSeDetecta() {
        Lectura lectura = leer(jsonLd("{\"offers\":{\"price\":2500000,\"availability\":\"https://schema.org/OutOfStock\"}}"));

        assertEquals(Lectura.Estado.AGOTADO, lectura.estado());
        assertNull(lectura.precio());
    }

    @Test
    void unProductoAgotadoEnElMicrodatoSeDetecta() {
        String html = "<html><body><link itemprop=\"availability\" href=\"http://schema.org/SoldOut\"></body></html>";

        assertEquals(Lectura.Estado.AGOTADO, leer(html).estado());
    }

    @Test
    void unProductoDescontinuadoSeMarcaComoRetirado() {
        Lectura lectura = leer(jsonLd("{\"offers\":{\"price\":900000,\"availability\":\"https://schema.org/Discontinued\"}}"));

        assertEquals(Lectura.Estado.RETIRADO, lectura.estado());
    }

    @Test
    void siUnaOfertaSigueDisponibleNoSeMarcaComoAgotado() {
        String json = "{\"offers\":[{\"price\":500000,\"availability\":\"https://schema.org/InStock\"},"
                + "{\"price\":520000,\"availability\":\"https://schema.org/OutOfStock\"}]}";

        Lectura lectura = leer(jsonLd(json));

        assertEquals(Lectura.Estado.DISPONIBLE, lectura.estado());
        assertEquals(new BigDecimal("500000"), lectura.precio());
    }

    @Test
    void unaPaginaVaciaDevuelveSinDatos() {
        assertEquals(Lectura.Estado.SIN_DATOS, leer("").estado());
    }

    @Test
    void leeElNombreOriginalDelProductoEnElJsonLd() {
        Lectura lectura = leer(jsonLd("{\"@type\":\"Product\",\"name\":\"Cama Doble Semiortopédica\",\"offers\":{\"price\":1469900}}"));

        assertEquals("Cama Doble Semiortopédica", lectura.nombre());
    }

    @Test
    void leeElNombreDentroDeUnGrafo() {
        String json = "{\"@graph\":[{\"@type\":\"Organization\",\"name\":\"Alkosto\"},"
                + "{\"@type\":\"Product\",\"name\":\"Nevera No Frost 300 L\",\"offers\":{\"price\":1800000}}]}";

        Lectura lectura = leer(jsonLd(json));

        assertEquals("Nevera No Frost 300 L", lectura.nombre());
    }

    @Test
    void sinJsonLdUsaElTituloDeLaPaginaYQuitaElNombreDeLaTienda() {
        String html = "<html><head><meta property=\"og:title\" content=\"Camiseta oversize negra | Mattelsa\"></head>"
                + "<body><meta itemprop=\"price\" content=\"89900\"></body></html>";

        Lectura lectura = leer(html);

        assertEquals("Camiseta oversize negra", lectura.nombre());
    }

    @Test
    void sinNingunNombreDevuelveElNombreVacio() {
        Lectura lectura = leer("<html><body><meta itemprop=\"price\" content=\"89900\"></body></html>");

        assertEquals("", lectura.nombre());
    }

    @Test
    void unNombreMuyLargoSeRecorta() {
        String largo = "Televisor ".repeat(30);

        Lectura lectura = leer(jsonLd("{\"@type\":\"Product\",\"name\":\"" + largo + "\",\"offers\":{\"price\":2500000}}"));

        assertTrue(lectura.nombre().length() <= 120);
    }

    @Test
    void sinConexionLeerDevuelveSinDatos() {
        Lectura lectura = scraper.leer("esto-no-es-una-url");

        assertEquals(Lectura.Estado.SIN_DATOS, lectura.estado());
    }
}
