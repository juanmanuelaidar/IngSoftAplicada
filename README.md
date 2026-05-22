# Trabajo Final - Ingeniería de Software Aplicada

Este repositorio contiene **base de entrega** (JDL, tests de referencia, Docker/ELK, Ionic y Jenkins), pero para ejecutar CI en verde primero hay que generar la app JHipster real dentro de `backend/`.

## 1) Generar backend real desde JDL

```bash
npm i -g generator-jhipster
jhipster jdl jdl/e-commerce-monolith.jdl
```

> El `Jenkinsfile` ahora tiene un stage **Preflight** que falla explícitamente si faltan `backend/pom.xml` o `backend/package.json`.

## 2) Integrar los tests de referencia

Copiar/ajustar según el código generado:

- Unit tests:
  - `backend/src/test/java/com/example/shop/ProductTest.java`
  - `backend/src/test/java/com/example/shop/ShoppingCartTest.java`
- Cypress:
  - `backend/cypress/support/commands.js`
  - `backend/cypress/e2e/auth-api-login.cy.js`
  - `backend/cypress/e2e/product-create.cy.js`
  - `backend/cypress/e2e/cart-flow.cy.js`

## 3) Ejecutar tests localmente

```bash
cd backend
./mvnw test
npm ci
npm run cy:run
```

## 4) Infra Docker + ELK

```bash
docker compose -f docker/docker-compose.yml up -d
```

## 5) Jenkins / Docker Hub

Configurar en Jenkins:

- variable: `DOCKERHUB_USER`
- credencial: `dockerhub-creds` (username/password)

Luego ejecutar pipeline para build, tests, build/push Docker.
