# Trabajo Final - Ingenieria de Software Aplicada

Entrega de una aplicacion e-commerce generada con JHipster, pruebas automatizadas, despliegue Docker/ELK, cliente Ionic PWA y pipeline Jenkins para publicar la imagen en Docker Hub.

## Componentes

- `jdl/e-commerce-monolith.jdl`: modelo JDL base del proyecto.
- `backend/`: aplicacion JHipster monolitica Spring Boot 3, React, JWT, PostgreSQL, Liquibase y Cypress.
- `mobile/`: aplicacion Ionic/Angular PWA que consume la API de productos del backend y mantiene cache offline.
- `docker/`: stack de despliegue con app, PostgreSQL, Elasticsearch, Logstash y Kibana.
- `Jenkinsfile`: pipeline CI/CD para build, tests, imagen Docker y push a Docker Hub.

## Comandos rapidos

Desde la raiz del proyecto se puede ejecutar cada punto de la entrega con `make`:

```bash
make help          # lista todos los comandos disponibles
make preflight     # valida archivos principales
make backend-test  # tests unitarios
make e2e           # tests Cypress
make mobile-build  # build Ionic/PWA
make docker-build  # imagen Docker local
make deploy        # app + PostgreSQL + ELK
make logs          # logs del stack completo
make elk-logs      # logs de Elasticsearch, Logstash y Kibana
make ci-local      # verificacion local completa
```

Tambien hay alias por requerimiento: `make req-jhipster`, `make req-unit`, `make req-e2e`, `make req-docker`, `make req-elk`, `make req-mobile`, `make req-pwa` y `make req-jenkins`.

## Cumplimiento de requerimientos

1. **Aplicacion JHipster basada en modelo JDL**

   - Modelo JDL: `jdl/e-commerce-monolith.jdl`
   - Backend JHipster generado: `backend/`
   - Entidades principales: `backend/src/main/java/com/example/store/domain/`
   - Comando de verificacion: `make req-jhipster`

2. **Dos tests unitarios**

   - Test unitario de producto: `backend/src/test/java/com/example/store/domain/ProductTest.java`
   - Test unitario de carrito: `backend/src/test/java/com/example/store/domain/ShoppingCartTest.java`
   - Comando de ejecucion: `make req-unit`

3. **Tres tests E2E en Cypress**

   - Login por API: `backend/src/test/javascript/cypress/e2e/auth-api-login.cy.ts`
   - Creacion de producto por API: `backend/src/test/javascript/cypress/e2e/product-create.cy.ts`
   - Flujo de carrito luego de login por API: `backend/src/test/javascript/cypress/e2e/cart-flow.cy.ts`
   - Comando de ejecucion: `make req-e2e`

4. **Deploy en Docker**

   - Dockerfile de la aplicacion: `backend/Dockerfile`
   - Stack de deploy: `docker/docker-compose.yml`
   - Comando de ejecucion: `make req-docker`

5. **Servidor de logs ELK en Docker**

   - Elasticsearch, Logstash y Kibana: `docker/docker-compose.yml`
   - Pipeline de Logstash: `docker/logstash.conf`
   - Configuracion de envio de logs desde la app: variables `JHIPSTER_LOGGING_LOGSTASH_HOST` y `JHIPSTER_LOGGING_LOGSTASH_PORT` en `docker/docker-compose.yml`
   - Comando de ejecucion y logs: `make req-elk`

6. **Aplicacion Ionic que consume una API del proyecto JHipster**

   - Aplicacion mobile: `mobile/`
   - Servicio que consume la API: `mobile/src/app/services/api.service.ts`
   - Pantalla de productos: `mobile/src/app/pages/products/`
   - Comando de ejecucion: `make req-mobile`

7. **Ionic convertido en PWA con funcionamiento offline**

   - Manifest PWA: `mobile/src/manifest.webmanifest`
   - Service worker habilitado: `mobile/angular.json`
   - Cache offline de assets y productos: `mobile/ngsw-config.json`
   - Comando de ejecucion: `make req-pwa`

8. **Servidor de integracion continua Jenkins con imagen DockerHub**
   - Pipeline Jenkins: `Jenkinsfile`
   - Stages incluidos: checkout, build backend, tests unitarios, Cypress, Docker build y Docker push.
   - Credencial esperada: `dockerhub-creds`
   - Variable esperada: `DOCKERHUB_USER`
   - Comando de verificacion: `make req-jenkins`

## Backend

```bash
cd backend
./mvnw test
```

Para ejecutar los E2E:

```bash
cd backend
npm ci
npm run cy:run
```

Los tests Cypress incluyen login por API (`/api/authenticate`), creacion de producto por API y flujo de carrito.

## Mobile PWA

```bash
cd mobile
npm install
npm run build:prod
```

La app Ionic consume `/api/products`, registra service worker en produccion y cachea productos para funcionar sin conexion.

## Docker + ELK

```bash
docker compose -f docker/docker-compose.yml up -d
```

Servicios principales:

- App: `http://localhost:8080`
- Kibana: `http://localhost:5601`
- Elasticsearch: `http://localhost:9200`
- Logstash TCP JSON: `localhost:5000`

## Jenkins / Docker Hub

Configurar en Jenkins:

- Variable `DOCKERHUB_USER`.
- Credencial `dockerhub-creds` de tipo username/password.

El pipeline ejecuta checkout, preflight, build backend, tests unitarios, Cypress, Docker build y Docker push.
