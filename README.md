# Trabajo Final - Ingeniería de Software Aplicada

Repositorio base para una entrega JHipster e-commerce con CI/CD, Docker/ELK, Cypress e Ionic PWA.

## Estado de los requisitos

| Requisito | Archivo(s) principal(es) | Estado |
| --- | --- | --- |
| App JHipster desde JDL | `jdl/e-commerce-monolith.jdl` | Modelo listo para generar la app monolítica `store`. |
| 2 tests unitarios | `backend/src/test/java/com/example/store/domain/ProductTest.java`, `backend/src/test/java/com/example/store/domain/ShoppingCartTest.java` | Tests explícitos contra entidades de dominio generadas. |
| 3 tests E2E Cypress con login API | `backend/cypress/e2e/*.cy.js`, `backend/cypress/support/commands.js` | Tres specs visibles; `cy.loginByApi` llama a `/api/authenticate`. |
| Deploy Docker | `backend/Dockerfile`, `docker/docker-compose.yml` | Imagen Spring Boot por Dockerfile y stack local con Postgres. |
| ELK en Docker | `docker/docker-compose.yml`, `docker/logstash.conf` | Elasticsearch, Logstash y Kibana; la app usa variables `JHIPSTER_LOGGING_LOGSTASH_*`. |
| Ionic consume API | `mobile/src/app/services/api.service.ts`, `mobile/src/app/pages/products/*` | Consume productos/categorías desde API JHipster. |
| PWA offline | `mobile/ngsw-config.json`, `products.page.ts` | Cache de assets/API y fallback local de productos. |
| Jenkins + Docker Hub | `Jenkinsfile` | Build, unit tests, E2E, Docker build y push a Docker Hub. |

## 1) Generar backend real desde el JDL

El JDL define `baseName store` y `packageName com.example.store`; por eso los tests unitarios incluidos ya apuntan a `com.example.store.domain`.

```bash
mkdir -p backend
cd backend
jhipster jdl ../jdl/e-commerce-monolith.jdl
```

> Si JHipster pregunta si debe sobrescribir archivos existentes, revisar los cambios antes de aceptar para conservar `backend/Dockerfile` y los tests agregados en `backend/src/test/java/com/example/store/domain`.

## 2) Ejecutar tests locales

```bash
cd backend
./mvnw test
npm ci
npm run cy:run
```

## 3) Construir imagen Docker

```bash
cd backend
./mvnw -DskipTests clean package
docker build -t local/store-app:latest .
```

## 4) Levantar app + PostgreSQL + ELK

```bash
docker compose -f docker/docker-compose.yml up -d
```

Servicios esperados:

- App: http://localhost:8080
- Elasticsearch: http://localhost:9200
- Kibana: http://localhost:5601
- Logstash TCP JSON: `localhost:5000`

## 5) Ionic / PWA offline

El cliente mínimo consume la API JHipster desde `http://localhost:8080/api` por defecto. Para apuntar a otro host durante una demo, guardar en el navegador:

```js
localStorage.setItem('api-base-url', 'https://mi-api.example.com/api')
```

La pantalla de productos guarda el último catálogo en `localStorage` y lo muestra si la API no responde.

## 6) Jenkins / Docker Hub

Configurar en Jenkins:

- variable: `DOCKERHUB_USER`
- credencial `dockerhub-creds` de tipo username/password

La pipeline valida primero que existan `backend/pom.xml`, `backend/package.json` y `backend/Dockerfile`; después ejecuta build, unit tests, Cypress, Docker build y push.
