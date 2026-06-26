# Estructura del proyecto

La raiz del repositorio funciona como orquestador de la entrega. Las aplicaciones reales viven en carpetas dedicadas.

```text
IngSoftAplicada/
├── README.md
├── Makefile
├── Jenkinsfile
├── jdl/
│   └── e-commerce-monolith.jdl
├── docs/
├── backend/
├── mobile/
├── docker/
└── archive/
    └── legacy-root-jhipster/
```

## Fuente de verdad

- `backend/`: aplicacion JHipster oficial de la entrega.
- `mobile/`: app Ionic/Angular PWA.
- `docker/`: despliegue de app, PostgreSQL y ELK.
- `jdl/`: modelo usado para generar el backend.
- `docs/`: documentacion y evidencia.
- `archive/legacy-root-jhipster/`: copia historica de una app JHipster que estaba en la raiz. No se usa para ejecutar la entrega.

## Comandos

Usar siempre los comandos de la raiz:

```bash
make help
make preflight
make backend-test
make e2e
make mobile-build
make docker-build
make deploy
```
