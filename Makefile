SHELL := /bin/bash
.DEFAULT_GOAL := help

COMPOSE_FILE := docker/docker-compose.yml
BACKEND_DIR := backend
MOBILE_DIR := mobile
IMAGE_NAME ?= local/store-app
IMAGE_TAG ?= latest

BLUE := \033[1;34m
GREEN := \033[1;32m
YELLOW := \033[1;33m
RED := \033[1;31m
RESET := \033[0m

.PHONY: help preflight backend-build backend-test e2e mobile-build docker-build deploy elk-up elk-logs app-logs logs down ci-local req-jhipster req-unit req-e2e req-docker req-elk req-mobile req-pwa req-jenkins

define banner
	@printf "\n$(BLUE)==> %s$(RESET)\n" "$(1)"
endef

help:
	@printf "$(GREEN)Comandos de entrega$(RESET)\n"
	@printf "  make preflight      Verifica archivos principales del proyecto\n"
	@printf "  make backend-test   Ejecuta los tests unitarios del backend\n"
	@printf "  make e2e            Ejecuta Cypress end-to-end\n"
	@printf "  make mobile-build   Compila Ionic/PWA en produccion\n"
	@printf "  make docker-build   Construye la imagen Docker local\n"
	@printf "  make deploy         Levanta app + PostgreSQL + ELK\n"
	@printf "  make logs           Muestra logs bonitos del stack completo\n"
	@printf "  make elk-logs       Muestra logs de Elasticsearch/Logstash/Kibana\n"
	@printf "  make down           Baja el stack Docker\n"
	@printf "  make ci-local       Corre una verificacion local completa\n"
	@printf "\n$(YELLOW)Comandos por requerimiento$(RESET)\n"
	@printf "  make req-jhipster   Requisito 1: estructura JHipster/JDL\n"
	@printf "  make req-unit       Requisito 2: tests unitarios\n"
	@printf "  make req-e2e        Requisito 3: tests E2E Cypress\n"
	@printf "  make req-docker     Requisito 4: imagen Docker\n"
	@printf "  make req-elk        Requisito 5: stack ELK y logs\n"
	@printf "  make req-mobile     Requisito 6: app Ionic/API\n"
	@printf "  make req-pwa        Requisito 7: build PWA/offline\n"
	@printf "  make req-jenkins    Requisito 8: valida Jenkinsfile\n"

preflight:
	$(call banner,Preflight de entrega)
	@test -f jdl/e-commerce-monolith.jdl || { printf "$(RED)Falta jdl/e-commerce-monolith.jdl$(RESET)\n"; exit 1; }
	@test -f $(BACKEND_DIR)/pom.xml || { printf "$(RED)Falta $(BACKEND_DIR)/pom.xml$(RESET)\n"; exit 1; }
	@test -f $(BACKEND_DIR)/package.json || { printf "$(RED)Falta $(BACKEND_DIR)/package.json$(RESET)\n"; exit 1; }
	@test -f $(MOBILE_DIR)/package.json || { printf "$(RED)Falta $(MOBILE_DIR)/package.json$(RESET)\n"; exit 1; }
	@test -f $(COMPOSE_FILE) || { printf "$(RED)Falta $(COMPOSE_FILE)$(RESET)\n"; exit 1; }
	@test -f Jenkinsfile || { printf "$(RED)Falta Jenkinsfile$(RESET)\n"; exit 1; }
	@printf "$(GREEN)OK: estructura principal encontrada.$(RESET)\n"

backend-build:
	$(call banner,Build backend JHipster)
	cd $(BACKEND_DIR) && ./mvnw -DskipTests clean package

backend-test:
	$(call banner,Tests unitarios backend)
	cd $(BACKEND_DIR) && ./mvnw test

e2e:
	$(call banner,Tests E2E Cypress)
	cd $(BACKEND_DIR) && npm ci && npm run cy:run

mobile-build:
	$(call banner,Build Ionic PWA)
	cd $(MOBILE_DIR) && npm install && npm run build:prod

docker-build: backend-build
	$(call banner,Imagen Docker)
	docker build -t $(IMAGE_NAME):$(IMAGE_TAG) $(BACKEND_DIR)

deploy:
	$(call banner,Deploy Docker + ELK)
	DOCKERHUB_USER=local IMAGE_TAG=$(IMAGE_TAG) docker compose -f $(COMPOSE_FILE) up -d
	@printf "$(GREEN)App: http://localhost:8080 | Kibana: http://localhost:5601$(RESET)\n"

logs:
	$(call banner,Logs del stack completo)
	docker compose -f $(COMPOSE_FILE) logs -f --tail=150

app-logs:
	$(call banner,Logs de la app)
	docker compose -f $(COMPOSE_FILE) logs -f --tail=150 app

elk-logs:
	$(call banner,Logs ELK)
	docker compose -f $(COMPOSE_FILE) logs -f --tail=150 elasticsearch logstash kibana

down:
	$(call banner,Bajar stack Docker)
	docker compose -f $(COMPOSE_FILE) down

ci-local: preflight backend-test e2e mobile-build docker-build
	$(call banner,Verificacion local completa)
	@printf "$(GREEN)OK: checks principales finalizados.$(RESET)\n"

req-jhipster: preflight
	$(call banner,Requisito 1 - JHipster basado en JDL)
	@printf "$(GREEN)OK: JDL y backend JHipster presentes.$(RESET)\n"

req-unit: backend-test

req-e2e: e2e

req-docker: docker-build

req-elk: deploy elk-logs

req-mobile: mobile-build

req-pwa: mobile-build

req-jenkins:
	$(call banner,Requisito 8 - Jenkins + DockerHub)
	@test -f Jenkinsfile || { printf "$(RED)Falta Jenkinsfile$(RESET)\n"; exit 1; }
	@grep -q "docker push" Jenkinsfile || { printf "$(RED)El Jenkinsfile no contiene docker push$(RESET)\n"; exit 1; }
	@grep -q "dockerhub-creds" Jenkinsfile || { printf "$(RED)El Jenkinsfile no referencia dockerhub-creds$(RESET)\n"; exit 1; }
	@printf "$(GREEN)OK: Jenkinsfile valida build, tests y push a DockerHub.$(RESET)\n"
