# Ahorrix (versión pública)

Ahorrix vigila el precio de productos de tiendas en línea y avisa cuando bajan. Este repositorio publica una parte del proyecto: el acceso de usuarios y el registro de productos de Éxito, Alkosto y Mattelsa. El proyecto completo se entregó a la Universidad Católica Luis Amigó como Proyecto Integrador 2026.

## Qué incluye

- Registro e inicio de sesión con contraseñas cifradas (BCrypt) y tokens JWT.
- Vigilar un producto: pegas el enlace de Éxito, Alkosto o Mattelsa y el backend lee el nombre y el precio con Jsoup.
- Lista de tus productos con el precio inicial y el precio actual, y botón para dejar de vigilar.
- 30 pruebas automáticas (JUnit 5) del lector de precios, de las tiendas y de los tokens.

## Qué no incluye

El monitoreo nocturno, las alertas por correo, el precio objetivo, el historial con gráfica, el asistente, las categorías automáticas y el panel de administración quedan en la versión completa.

## Tecnologías

Java 21, Spring Boot 3.3.1, Spring Security, MongoDB 7, Jsoup 1.18.1, Angular 21 y TypeScript.

## Cómo ejecutarlo

Necesitas Java 21, Node.js 22 o superior y Docker.

1. Base de datos:

```
docker run -d --name mongo -p 27017:27017 mongo:7
```

2. Backend, en el puerto 8081:

```
cd backend
./mvnw spring-boot:run
```

En Windows usa `mvnw.cmd spring-boot:run`. Para fijar la clave de los tokens, copia `secrets.example.properties` como `secrets.properties` y cambia el valor. Si no lo haces, el backend usa una clave temporal y las sesiones se pierden al reiniciar.

3. Frontend, en el puerto 4200:

```
cd frontend
npm install
npm start
```

Abre http://localhost:4200, crea una cuenta y pega el enlace de un producto de Éxito, Alkosto o Mattelsa. El lector busca el precio en los datos estructurados de la página (JSON-LD o `itemprop=price`). Si la página construye el precio con JavaScript, responde que no pudo leerlo.

## Pruebas

```
cd backend
./mvnw test
```

## API

| Método | Ruta | Descripción |
| --- | --- | --- |
| POST | `/api/auth/register` | Crea la cuenta y devuelve el token |
| POST | `/api/auth/login` | Inicia sesión y devuelve el token |
| GET | `/api/productos` | Lista los productos del usuario |
| POST | `/api/productos` | Agrega un producto a partir de su enlace |
| DELETE | `/api/productos/{id}` | Deja de vigilar un producto |

## Autor

Kevin Dair Royo Cuesta, estudiante de Tecnología en Desarrollo de Software, Universidad Católica Luis Amigó.

## Derechos

Todos los derechos reservados. 
