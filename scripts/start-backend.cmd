@echo off
REM ---------------------------------------------------------------------------
REM Starts the Spring Boot backend on http://localhost:8080
REM
REM Run this from anywhere; paths are resolved relative to this script.
REM Press Ctrl+C in the window to stop it.
REM ---------------------------------------------------------------------------
setlocal
cd /d "%~dp0..\backend"

echo.
echo   Business Automation Company - backend
echo   -------------------------------------
echo   API      : http://localhost:8080/api
echo   Check    : http://localhost:8080/api/portfolio
echo   Stopping : press Ctrl+C
echo.

call mvnw.cmd spring-boot:run %*

endlocal
