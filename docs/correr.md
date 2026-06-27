# Como correr el proyecto

## 1. Levantar backend/API, base de datos y ELK

Desde la raiz:

```bash
make deploy
```

Abre:

- API/JHipster: http://localhost:8080
- Kibana: http://localhost:5601

Logs:

```bash
make logs
make elk-logs
```

Bajar contenedores:

```bash
make down
```

## 2. Levantar el frontend principal Ionic

En otra terminal:

```bash
cd mobile
npm install
npm start
```

Abrir:

```text
http://127.0.0.1:8100
```

Ionic consume la API JHipster en `http://localhost:8080`.

## 3. Validar toda la entrega

```bash
make ci-local
```

Corre tests unitarios, Cypress E2E, build Ionic/PWA y build Docker.

## 4. Validar por requisito

```bash
make req-jhipster  # 1. JHipster + JDL
make req-unit      # 2. Tests unitarios
make req-e2e       # 3. Cypress E2E
make req-docker    # 4. Imagen Docker
make req-elk       # 5. ELK en Docker
make req-mobile    # 6. Ionic consume API
make req-pwa       # 7. Ionic PWA/offline
make req-jenkins   # 8. Jenkins + DockerHub
```

## 5. Comandos frecuentes

```bash
make help
make backend-test
make e2e
make mobile-build
make docker-build
```
