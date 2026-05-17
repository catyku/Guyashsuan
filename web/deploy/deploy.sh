#!/bin/bash
# 古雅軒法律事務所 — 一鍵部署腳本
# 使用方式: ./deploy.sh [build|up|down|logs|restart]

set -e

COMPOSE_FILE="docker-compose.yaml"
PROJECT_NAME="guyahsuan"

case "${1:-up}" in
  build)
    echo "🔨 Building WAR package..."
    cd "$(dirname "$0")/.."
    mvn clean package -DskipTests -q
    echo "✅ WAR package built: target/sas.war"
    cp target/sas.war deploy/sas.war
    echo "✅ Copied to deploy/sas.war"
    ;;

  up)
    echo "🚀 Starting GUYAHSUAN Law Office..."
    docker compose -f deploy/${COMPOSE_FILE} --project-name ${PROJECT_NAME} up -d
    echo ""
    echo "⏳ Waiting for MariaDB to be ready..."
    sleep 15
    echo "✅ Services started!"
    echo "   前台: http://localhost:${APP_PORT:-8092}/"
    echo "   後台: http://localhost:${APP_PORT:-8092}/admin/"
    ;;

  down)
    echo "🛑 Stopping GUYAHSUAN Law Office..."
    docker compose -f deploy/${COMPOSE_FILE} --project-name ${PROJECT_NAME} down
    echo "✅ Services stopped."
    ;;

  logs)
    docker compose -f deploy/${COMPOSE_FILE} --project-name ${PROJECT_NAME} logs -f ${2:-}
    ;;

  restart)
    echo "🔄 Restarting GUYAHSUAN Law Office..."
    docker compose -f deploy/${COMPOSE_FILE} --project-name ${PROJECT_NAME} restart
    echo "✅ Services restarted."
    ;;

  status)
    docker compose -f deploy/${COMPOSE_FILE} --project-name ${PROJECT_NAME} ps
    ;;

  *)
    echo "Usage: $0 {build|up|down|logs|restart|status}"
    echo ""
    echo "  build   - Build WAR package from source"
    echo "  up      - Start all services (default)"
    echo "  down    - Stop all services"
    echo "  logs    - Follow logs (optional: service name)"
    echo "  restart - Restart all services"
    echo "  status  - Show service status"
    exit 1
    ;;
esac