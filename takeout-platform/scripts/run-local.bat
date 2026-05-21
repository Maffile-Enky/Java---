@echo off
setlocal enabledelayedexpansion

:: =============================================
:: Local Development Startup Script
:: Starts all services with local profile
:: =============================================

set "PLATFORM_DIR=%~dp0..\takeout-platform"
set "LOG_DIR=%~dp0..\logs"
set "JAVA_OPTS=-Xms128m -Xmx256m"

:: Common JVM args to override Nacos remote config for local dev
:: These command-line args have highest priority in Spring Boot
set "LOCAL_OVERRIDES=--spring.data.redis.host=localhost --spring.data.redis.port=6379 --spring.data.redis.password= --spring.cloud.nacos.config.enabled=false --spring.config.import="

if not exist "%LOG_DIR%" mkdir "%LOG_DIR%"

:: Check Java
java -version >nul 2>&1
if %ERRORLEVEL% neq 0 (
    echo [ERROR] Java not found. Please install JDK 21.
    pause
    exit /b 1
)

:: Check MySQL
powershell -Command "try { $tcp = New-Object System.Net.Sockets.TcpClient; $tcp.Connect('localhost', 3306); $tcp.Close(); exit 0 } catch { exit 1 }" >nul 2>&1
if %ERRORLEVEL% neq 0 (
    echo [ERROR] MySQL not running on localhost:3306
    pause
    exit /b 1
)

:: Check Redis
powershell -Command "try { $tcp = New-Object System.Net.Sockets.TcpClient; $tcp.Connect('localhost', 6379); $tcp.Close(); exit 0 } catch { exit 1 }" >nul 2>&1
if %ERRORLEVEL% neq 0 (
    echo [ERROR] Redis not running on localhost:6379
    pause
    exit /b 1
)

:: Check Nacos
powershell -Command "try { $tcp = New-Object System.Net.Sockets.TcpClient; $tcp.Connect('localhost', 8848); $tcp.Close(); exit 0 } catch { exit 1 }" >nul 2>&1
if %ERRORLEVEL% neq 0 (
    echo [WARN] Nacos not running on localhost:8848 - services will run without discovery
)

echo.
echo =============================================
echo  Starting all services with local profile
echo =============================================
echo.

:: Stop any previous instances
echo Stopping previous instances...
for %%p in (9999 8081 8083 8084 8085 8086 8087 8088) do (
    for /f "tokens=5" %%a in ('netstat -ano ^| findstr ":%%p " ^| findstr "LISTENING" 2^>nul') do (
        taskkill /PID %%a /F >nul 2>&1
    )
)
timeout /t 2 /nobreak >nul

:: Launch services
call :launch gateway 9999 gateway\target\gateway-1.0.0-SNAPSHOT.jar
call :launch user-service 8081 user-service\target\user-service-1.0.0-SNAPSHOT.jar "--spring.datasource.url=jdbc:mysql://localhost:3306/takeout_user?useUnicode=true^&characterEncoding=utf-8^&useSSL=false^&serverTimezone=Asia/Shanghai^&allowPublicKeyRetrieval=true"
call :launch merchant-service 8083 merchant-service\target\merchant-service-1.0.0-SNAPSHOT.jar "--spring.datasource.url=jdbc:mysql://localhost:3306/takeout_merchant?useUnicode=true^&characterEncoding=utf-8^&useSSL=false^&serverTimezone=Asia/Shanghai^&allowPublicKeyRetrieval=true"
call :launch order-service 8084 order-service\target\order-service-1.0.0-SNAPSHOT.jar "--spring.datasource.url=jdbc:mysql://localhost:3306/takeout_order?useUnicode=true^&characterEncoding=utf-8^&useSSL=false^&serverTimezone=Asia/Shanghai^&allowPublicKeyRetrieval=true --spring.rabbitmq.host=localhost --spring.rabbitmq.username=guest --spring.rabbitmq.password=guest"
call :launch payment-service 8085 payment-service\target\payment-service-1.0.0-SNAPSHOT.jar "--spring.datasource.url=jdbc:mysql://localhost:3306/takeout_payment?useUnicode=true^&characterEncoding=utf-8^&useSSL=false^&serverTimezone=Asia/Shanghai^&allowPublicKeyRetrieval=true --spring.rabbitmq.host=localhost --spring.rabbitmq.username=guest --spring.rabbitmq.password=guest"
call :launch notification-service 8086 notification-service\target\notification-service-1.0.0-SNAPSHOT.jar "--spring.datasource.url=jdbc:mysql://localhost:3306/takeout_notification?useUnicode=true^&characterEncoding=utf-8^&useSSL=false^&serverTimezone=Asia/Shanghai^&allowPublicKeyRetrieval=true --spring.rabbitmq.host=localhost --spring.rabbitmq.username=guest --spring.rabbitmq.password=guest"
call :launch delivery-service 8087 delivery-service\target\delivery-service-1.0.0-SNAPSHOT.jar "--spring.datasource.url=jdbc:mysql://localhost:3306/takeout_delivery?useUnicode=true^&characterEncoding=utf-8^&useSSL=false^&serverTimezone=Asia/Shanghai^&allowPublicKeyRetrieval=true --spring.rabbitmq.host=localhost --spring.rabbitmq.username=guest --spring.rabbitmq.password=guest"
call :launch search-service 8088 search-service\target\search-service-1.0.0-SNAPSHOT.jar "--spring.datasource.url=jdbc:mysql://localhost:3306/takeout_search?useUnicode=true^&characterEncoding=utf-8^&useSSL=false^&serverTimezone=Asia/Shanghai^&allowPublicKeyRetrieval=true"

echo.
echo All services launched! Check logs in: %LOG_DIR%
echo Gateway: http://localhost:9999
echo.
pause
exit /b 0

:launch
set "NAME=%~1"
set "PORT=%~2"
set "JAR=%PLATFORM_DIR%\%~3"
set "EXTRA_ARGS=%~4"
set "LOG_FILE=%LOG_DIR%\%NAME%.log"

if not exist "%JAR%" (
    echo [SKIP] %NAME% - JAR not found: %JAR%
    exit /b 0
)

echo Starting %NAME% on port %PORT%...
start "" /MIN cmd /c "java %JAVA_OPTS% -jar "%JAR%" --spring.profiles.active=local --server.port=%PORT% --spring.cloud.nacos.discovery.server-addr=localhost:8848 --spring.datasource.username=root --spring.datasource.password=123456 %LOCAL_OVERRIDES% %EXTRA_ARGS% > "%LOG_FILE%" 2>&1"
echo   [OK] %NAME% starting...
exit /b 0
