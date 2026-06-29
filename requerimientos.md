# Requerimientos de entrega

Este archivo indica donde esta implementado cada requerimiento del proyecto y que comandos usar para validarlo.

## 1. Aplicacion JHipster basada en JDL

La aplicacion JHipster esta en:

- `backend/`

El modelo JDL esta en:

- `jdl/e-commerce-monolith.jdl`

La logica principal generada por JHipster esta en:

- `backend/src/main/java/com/example/store/domain/`
- `backend/src/main/java/com/example/store/repository/`
- `backend/src/main/java/com/example/store/service/`
- `backend/src/main/java/com/example/store/web/rest/`

Comando de validacion:

```bash
make req-jhipster
```

## 2. Dos tests unitarios

Los tests unitarios estan en:

- `backend/src/test/java/com/example/store/domain/ProductTest.java`
- `backend/src/test/java/com/example/store/domain/ShoppingCartTest.java`

Que prueban:

- `ProductTest`: igualdad por id, relacion con categoria y validaciones de negocio de producto.
- `ShoppingCartTest`: igualdad por id, relacion con cliente, relacion con ordenes y campos requeridos del carrito.

Comando de validacion:

```bash
make backend-test
```

## 3. Tres tests E2E en Cypress

Los tests Cypress estan en:

- `backend/src/test/javascript/cypress/e2e/auth-api-login.cy.ts`
- `backend/src/test/javascript/cypress/e2e/product-create.cy.ts`
- `backend/src/test/javascript/cypress/e2e/cart-flow.cy.ts`

El helper de login por API esta en:

- `backend/src/test/javascript/cypress/support/commands.ts`

Comando de validacion:

```bash
make e2e
```

## APIs consumidas por Cypress

Los tests Cypress consumen estas APIs/rutas:

### Resumen por test

| Test Cypress | Que consume | Donde esta implementado |
| --- | --- | --- |
| `auth-api-login.cy.ts` | `POST /api/authenticate` | `backend/src/main/java/com/example/store/web/rest/AuthenticateController.java` |
| `product-create.cy.ts` | `POST /api/authenticate` y `POST /api/products` | `backend/src/main/java/com/example/store/web/rest/AuthenticateController.java` y `backend/src/main/java/com/example/store/web/rest/ProductResource.java` |
| `cart-flow.cy.ts` | `POST /api/authenticate` y ruta web `/shopping-cart` | `backend/src/main/java/com/example/store/web/rest/AuthenticateController.java` y `backend/src/main/webapp/app/entities/shopping-cart/` |

### Login por API

Archivo:

- `backend/src/test/javascript/cypress/support/commands.ts`

Implementacion backend:

- `backend/src/main/java/com/example/store/web/rest/AuthenticateController.java`

Endpoint:

```http
POST /api/authenticate
```

Body:

```json
{
  "username": "admin",
  "password": "admin"
}
```

Que valida:

- Que la API responda `200`.
- Que devuelva un JWT en `id_token`.
- Que el token se guarde en `localStorage` como `jhi-authenticationToken`.

Tests que usan este login:

- `auth-api-login.cy.ts`
- `product-create.cy.ts`
- `cart-flow.cy.ts`

### Creacion de producto por API

Archivo:

- `backend/src/test/javascript/cypress/e2e/product-create.cy.ts`

Implementacion backend:

- `backend/src/main/java/com/example/store/web/rest/ProductResource.java`

Endpoint:

```http
POST /api/products
```

Header:

```http
Authorization: Bearer <jwt>
```

Body:

```json
{
  "name": "Teclado-<timestamp>",
  "price": 150.0,
  "stock": 20
}
```

Que valida:

- Que la API responda `201 Created`.
- Que el producto creado tenga el mismo nombre enviado.

### Pantalla de carritos

Archivo:

- `backend/src/test/javascript/cypress/e2e/cart-flow.cy.ts`

Implementacion frontend:

- `backend/src/main/webapp/app/entities/shopping-cart/`

Ruta web:

```http
GET /shopping-cart
```

Que valida:

- Que el usuario pueda entrar autenticado.
- Que la URL final sea `/shopping-cart`.
- Que se renderice el encabezado `ShoppingCart`.

## 4. Deploy en Docker

Archivos principales:

- `Dockerfile`
- `docker/docker-compose.yml`
- `backend/target/store-0.0.1-SNAPSHOT.jar` generado al compilar

Comandos:

```bash
make docker-build
make deploy
```

La app queda disponible en:

```text
http://localhost:8080
```

Health check:

```text
http://localhost:8080/management/health
```

## 5. Servidor de logs ELK en Docker

Configuracion:

- `docker/docker-compose.yml`
- `docker/logstash.conf`

Servicios:

- `elasticsearch`
- `logstash`
- `kibana`

La app envia logs a Logstash usando estas variables en Docker:

- `JHIPSTER_LOGGING_LOGSTASH_HOST=logstash`
- `JHIPSTER_LOGGING_LOGSTASH_PORT=5000`

Kibana queda disponible en:

```text
http://localhost:5601
```

Indice esperado en Elasticsearch:

```text
store-logs-YYYY.MM.DD
```

Comandos:

```bash
make deploy
make elk-logs
```

## 6. Aplicacion Ionic que consume API JHipster

La aplicacion Ionic esta en:

- `mobile/`

Consume la API JHipster desde:

```text
http://localhost:8080
```

Archivos principales:

- `mobile/src/app/`
- `mobile/src/app/services/`
- `mobile/src/app/pages/`

Comandos:

```bash
cd mobile
npm install
npm start
```

URL local:

```text
http://127.0.0.1:8100
```

## 7. Ionic convertido en PWA offline

Configuracion PWA:

- `mobile/angular.json`
- `mobile/ngsw-config.json`
- `mobile/src/manifest.webmanifest`
- `mobile/src/app/app.module.ts`

Build de produccion:

```bash
make mobile-build
```

El build genera el service worker para funcionamiento offline.

## 8. Integracion continua Jenkins + DockerHub

Archivo:

- `Jenkinsfile`

Que hace:

- Lee el repositorio.
- Ejecuta validaciones/build.
- Construye imagen Docker.
- Publica la imagen con `docker push`.
- Usa credenciales `dockerhub-creds`.

Comando de validacion local:

```bash
make req-jenkins
```

## Comandos generales

```bash
make preflight
make backend-test
make e2e
make mobile-build
make docker-build
make deploy
make req-jenkins
```
