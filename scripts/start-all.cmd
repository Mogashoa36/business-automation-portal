@echo off
REM ---------------------------------------------------------------------------
REM Starts the backend and the Angular dev server together in the background.
REM
REM   Frontend : http://localhost:4200
REM   Backend  : http://localhost:8080/api
REM   Logs     : .logs\backend.log and .logs\frontend.log
REM
REM Stop both again with:  scripts\stop-all.cmd
REM
REM For live output, run scripts\start-backend.cmd or scripts\start-frontend.cmd
REM in a terminal of their own instead.
REM ---------------------------------------------------------------------------
setlocal
set "ROOT=%~dp0.."
set "LOGS=%ROOT%\.logs"
set "BACKEND_LOG=%LOGS%\backend.log"
set "FRONTEND_LOG=%LOGS%\frontend.log"
set "BACKEND_PORT=8080"
set "FRONTEND_PORT=4200"

if not exist "%LOGS%" mkdir "%LOGS%"

echo.
echo   Business Automation Company - starting both services
echo   -----------------------------------------------
echo.

for %%P in (%BACKEND_PORT% %FRONTEND_PORT%) do (
  call :portInUse %%P
  if errorlevel 1 exit /b 1
)

REM Reuses the single-service scripts so the start commands live in one place.
powershell -NoProfile -ExecutionPolicy Bypass -Command "Start-Process -FilePath 'cmd.exe' -WorkingDirectory '%ROOT%' -WindowStyle Hidden -ArgumentList '/c', 'call \"%~dp0start-backend.cmd\" > \"%BACKEND_LOG%\" 2>&1'; Start-Process -FilePath 'cmd.exe' -WorkingDirectory '%ROOT%' -WindowStyle Hidden -ArgumentList '/c', 'call \"%~dp0start-frontend.cmd\" > \"%FRONTEND_LOG%\" 2>&1'"

if errorlevel 1 (
  echo   Failed to launch the services.
  exit /b 1
)

echo   Backend  : http://localhost:%BACKEND_PORT%/api
echo   Frontend : http://localhost:%FRONTEND_PORT%
echo   Logs     : .logs\backend.log   .logs\frontend.log
echo.
echo   The first backend start can take around 30 seconds, and the frontend needs
echo   a moment to compile. Open the logs above if anything looks stuck.
echo   Run scripts\stop-all.cmd to stop both.
echo.
endlocal
exit /b 0

REM Returns errorlevel 1 when the given port is already listening, 0 when it is free.
:portInUse
powershell -NoProfile -Command "if (Get-NetTCPConnection -LocalPort %~1 -State Listen -ErrorAction SilentlyContinue) { exit 1 } else { exit 0 }" >nul 2>&1
if errorlevel 1 (
  echo   Port %~1 is already in use. Stop whatever is using it, or run:
  echo     scripts\stop-all.cmd
  exit /b 1
)
exit /b 0
