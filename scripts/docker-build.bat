@echo off
setlocal enabledelayedexpansion

set "TAG=%~1"
if "%TAG%"=="" set "TAG=latest"

echo ==========================================
echo   Build Docker Images (tag: %TAG%)
echo ==========================================

cd /d "%~dp0.."

for %%s in (gateway user-service merchant-service order-service payment-service notification-service delivery-service search-service) do (
    echo.
    echo --- Building takeout-%%s:%TAG% ---
    docker build -t "takeout-%%s:%TAG%" "./takeout-platform/%%s"
    if !errorlevel! neq 0 (
        echo [ERROR] takeout-%%s build failed!
        pause
        exit /b 1
    )
)

echo.
echo --- Building takeout-frontend:%TAG% ---
docker build -t "takeout-frontend:%TAG%" "./takeout-frontend"

echo.
echo ==========================================
echo   All images built!
echo.
echo   Start:   docker-compose up -d
echo   Status:  docker-compose ps
echo ==========================================
pause
