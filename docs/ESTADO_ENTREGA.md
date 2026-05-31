# Estado real de la entrega

Este documento enumera qué evidencia existe en el repositorio para cada requisito y qué se debe ejecutar para defender la entrega.

## Implementado en este repositorio

1. **JHipster + JDL**: `jdl/e-commerce-monolith.jdl` define una app monolítica `store`, JWT, PostgreSQL, React, Maven, Cypress y entidades de e-commerce.
2. **2 tests unitarios propios**:
   - `backend/src/test/java/com/example/store/domain/ProductTest.java`
   - `backend/src/test/java/com/example/store/domain/ShoppingCartTest.java`
3. **3 tests E2E Cypress explícitos**:
   - `backend/cypress/e2e/auth-api-login.cy.js`
   - `backend/cypress/e2e/product-create.cy.js`
   - `backend/cypress/e2e/cart-flow.cy.js`
4. **Login por API en Cypress**: `backend/cypress/support/commands.js` implementa `cy.loginByApi` contra `/api/authenticate`.
5. **Docker deploy**: `backend/Dockerfile` crea la imagen runnable del jar Spring Boot y `docker/docker-compose.yml` ejecuta la app con PostgreSQL.
6. **ELK**: `docker/docker-compose.yml` levanta Elasticsearch, Logstash y Kibana; la app queda configurada con `JHIPSTER_LOGGING_LOGSTASH_ENABLED=true`, host `logstash` y puerto `5000`.
7. **Ionic + API**: `mobile/src/app/services/api.service.ts` consume `/api/products` y `/api/product-categories`.
8. **PWA offline**: `mobile/ngsw-config.json` cachea assets/API y `products.page.ts` usa fallback con `localStorage`.
9. **Jenkins + Docker Hub**: `Jenkinsfile` ejecuta preflight, build, unit tests, Cypress, Docker build y push.

## Validación obligatoria antes de entregar

```bash
# backend generado desde JDL
cd backend
./mvnw test
npm ci
npm run cy:run

# imagen Docker
./mvnw -DskipTests clean package
docker build -t local/store-app:latest .

# infraestructura
cd ..
docker compose -f docker/docker-compose.yml up -d

# ionic pwa, dentro de la app Ionic completa
cd mobile
npm ci
npm run build
```

## Nota importante

El repositorio ahora incluye piezas concretas para cerrar los puntos marcados como débiles: tests unitarios propios, tests Cypress explícitos, Dockerfile para que `docker build backend` funcione, variables de Logstash correctas para contenedores y documentación actualizada. La ejecución final depende de generar/versionar el backend JHipster completo dentro de `backend/`.
