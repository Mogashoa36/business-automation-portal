@echo off
REM ---------------------------------------------------------------------------
REM Starts the Angular dev server on http://localhost:4200
REM
REM The app calls the backend at the URL in src/environments/environment.ts.
REM Press Ctrl+C in the window to stop it.
REM ---------------------------------------------------------------------------
setlocal
cd /d "%~dp0..\frontend\business-automation-portal"

echo.
echo   Business Automation Company - frontend
echo   -------------------------------------
echo   Site     : http://localhost:4200
echo   Stopping : press Ctrl+C
echo.

if not exist "node_modules" (
  echo   node_modules not found - installing dependencies first...
  call npm install
  if errorlevel 1 (
    echo   npm install failed.
    exit /b 1
  )
  echo.
)

call npm start

endlocal
