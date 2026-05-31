# Implementación paso a paso

## 1) Generar backend JHipster desde JDL

```bash
mkdir -p backend
cd backend
jhipster jdl ../jdl/e-commerce-monolith.jdl
```

La aplicación esperada se llama `store` y usa el paquete `com.example.store`.

## 2) Verificar tests unitarios propios

Tests incluidos:

- `backend/src/test/java/com/example/store/domain/ProductTest.java`
- `backend/src/test/java/com/example/store/domain/ShoppingCartTest.java`

Ejecutar:

```bash
cd backend
./mvnw test
```

## 3) Verificar E2E Cypress

Tests incluidos:

- `backend/cypress/e2e/auth-api-login.cy.js`
- `backend/cypress/e2e/product-create.cy.js`
- `backend/cypress/e2e/cart-flow.cy.js`

El comando `cy.loginByApi` usa `/api/authenticate` y guarda el JWT para el resto de pruebas.

Ejecución:

```bash
cd backend
npm ci
npm run cy:run
```

## 4) Construir y correr Docker + ELK

Construir imagen:

```bash
cd backend
./mvnw -DskipTests clean package
docker build -t local/store-app:latest .
```

Levantar stack:

```bash
cd ..
DOCKERHUB_USER=local IMAGE_TAG=latest docker compose -f docker/docker-compose.yml up -d
```

URLs:

- App: http://localhost:8080
- Kibana: http://localhost:5601
- Elasticsearch: http://localhost:9200

## 5) Validar logs en ELK

La app se conecta a Logstash por variables de entorno:

- `JHIPSTER_LOGGING_LOGSTASH_ENABLED=true`
- `JHIPSTER_LOGGING_LOGSTASH_HOST=logstash`
- `JHIPSTER_LOGGING_LOGSTASH_PORT=5000`

En Kibana, crear un data view sobre `store-logs-*` y generar tráfico en la app para ver eventos.

## 6) Ionic + PWA offline

La pantalla de productos consume la API y guarda cache local. Para demo offline:

1. Abrir la app Ionic con backend encendido.
2. Entrar a Productos para cargar catálogo.
3. Apagar backend o cortar red.
4. Recargar Productos y verificar que aparece el mensaje offline con productos cacheados.

## 7) Jenkins + Docker Hub

Variables requeridas:

- `DOCKERHUB_USER`
- credential `dockerhub-creds`

Stages principales:

1. Checkout
2. Preflight (`backend/pom.xml`, `backend/package.json`, `backend/Dockerfile`)
3. Build Backend
4. Unit Tests
5. E2E Cypress
6. Docker Build
7. Docker Push
