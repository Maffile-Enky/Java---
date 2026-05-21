@echo off
setlocal

cd /d "%~dp0..\takeout-platform"

echo ==========================================
echo   Build All Services
echo ==========================================

call mvn clean package -DskipTests -pl common/common-core,common/common-web,common/common-security,common/common-redis,common/common-mq,common/common-feign,gateway,user-service,merchant-service,order-service,payment-service,notification-service,delivery-service,search-service -am

if %errorlevel% neq 0 (
    echo.
    echo [ERROR] Build failed!
    pause
    exit /b 1
)

echo.
echo ==========================================
echo   Build done!
echo.
echo   Local run:  scripts\run-local.bat
echo   Docker:     scripts\docker-build.bat
echo ==========================================
pause
