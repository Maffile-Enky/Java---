@echo off
setlocal enabledelayedexpansion

set "PLATFORM_DIR=%~dp0..\takeout-platform"
set "LOG_DIR=%~dp0..\logs"
set "JAVA_OPTS=-Xms128m -Xmx256m"

if not exist "%LOG_DIR%" mkdir "%LOG_DIR%"

echo ==========================================
echo   Takeout Platform - Local Dev Mode
echo   Profile: local (connect to localhost)
echo ==========================================
echo.

java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERROR] java not found. Please install JDK 21.
    pause
    exit /b 1
)

echo [CHECK] Testing infrastructure...
powershell -NoProfile -Command "$t=New-Object System.Net.Sockets.TcpClient; try{$t.Connect('localhost',8848);$t.Close();exit 0}catch{exit 1}" >nul 2>&1
if %errorlevel% equ 0 (echo   [OK]   Nacos) else (echo   [WARN] Nacos not responding)
powershell -NoProfile -Command "$t=New-Object System.Net.Sockets.TcpClient; try{$t.Connect('localhost',3306);$t.Close();exit 0}catch{exit 1}" >nul 2>&1
if %errorlevel% equ 0 (echo   [OK]   MySQL) else (echo   [WARN] MySQL not responding)
powershell -NoProfile -Command "$t=New-Object System.Net.Sockets.TcpClient; try{$t.Connect('localhost',6379);$t.Close();exit 0}catch{exit 1}" >nul 2>&1
if %errorlevel% equ 0 (echo   [OK]   Redis) else (echo   [WARN] Redis not responding)
powershell -NoProfile -Command "$t=New-Object System.Net.Sockets.TcpClient; try{$t.Connect('localhost',5672);$t.Close();exit 0}catch{exit 1}" >nul 2>&1
if %errorlevel% equ 0 (echo   [OK]   RabbitMQ) else (echo   [WARN] RabbitMQ not responding - payment/delivery/notification may fail)
echo.

echo [STOP] Stopping previous services...
for %%p in (9999 8081 8083 8084 8085 8086 8087 8088) do (
    for /f "tokens=5" %%a in ('netstat -ano 2^>nul ^| findstr :%%p ^| findstr LISTENING') do (
        taskkill /PID %%a /F >nul 2>&1
        if !errorlevel! equ 0 echo   Stopped port %%p
    )
)
timeout /t 2 /nobreak >nul

echo.
echo [START] Starting services ...
echo.

call :launch gateway 9999 gateway\target\gateway-1.0.0-SNAPSHOT.jar
call :launch user-service 8081 user-service\target\user-service-1.0.0-SNAPSHOT.jar
call :launch merchant-service 8083 merchant-service\target\merchant-service-1.0.0-SNAPSHOT.jar
call :launch order-service 8084 order-service\target\order-service-1.0.0-SNAPSHOT.jar
call :launch payment-service 8085 payment-service\target\payment-service-1.0.0-SNAPSHOT.jar
call :launch notification-service 8086 notification-service\target\notification-service-1.0.0-SNAPSHOT.jar
call :launch delivery-service 8087 delivery-service\target\delivery-service-1.0.0-SNAPSHOT.jar
call :launch search-service 8088 search-service\target\search-service-1.0.0-SNAPSHOT.jar

echo.
echo ==========================================
echo   Done! Check logs\ folder for output.
echo   Gateway: http://localhost:9999
echo   Stop:    scripts\stop-local.bat
echo ==========================================
pause
exit /b 0

:launch
set "SNAME=%~1"
set "SPORT=%~2"
set "SJAR=%~3"
set "SPATH=%PLATFORM_DIR%\%SJAR%"
if not exist "%SPATH%" (
    echo   [SKIP] %SNAME% - JAR missing, run scripts\build.bat first
    goto :eof
)
echo   %SNAME% ^(%SPORT%^)
start "" /MIN cmd /c "java %JAVA_OPTS% -jar "%SPATH%" --spring.profiles.active=local > "%LOG_DIR%\%SNAME%.log" 2>&1"
goto :eof
