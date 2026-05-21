@echo off
setlocal enabledelayedexpansion

echo Stopping local services...

for %%p in (9999 8081 8083 8084 8085 8086 8087 8088) do (
    for /f "tokens=5" %%a in ('netstat -ano 2^>nul ^| findstr :%%p ^| findstr LISTENING') do (
        taskkill /PID %%a /F >nul 2>&1
        if !errorlevel! equ 0 echo   Stopped port %%p PID:%%a
    )
)

echo Done.
