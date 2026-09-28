@echo off
REM ---------------------------------------------------------------------------
REM Stops the backend and frontend started by scripts\start-all.cmd.
REM Only processes listening on the two dev ports are touched.
REM ---------------------------------------------------------------------------
setlocal

set "STOPPED=0"

for %%P in (8080 4200) do (
  for /f "tokens=* delims=" %%I in ('powershell -NoProfile -Command "Get-NetTCPConnection -LocalPort %%P -State Listen -ErrorAction SilentlyContinue | Select-Object -ExpandProperty OwningProcess -Unique" 2^>nul') do (
    if not "%%I"=="" (
      taskkill /F /T /PID %%I >nul 2>&1
      if errorlevel 1 (
        echo   Could not stop process %%I on port %%P - try running this script as administrator.
      ) else (
        echo   Stopped process %%I on port %%P
        set /a STOPPED+=1
      )
    )
  )
)

if "%STOPPED%"=="0" (
  echo   Nothing was running on ports 8080 or 4200.
) else (
  echo.
  echo   Done.
)

endlocal
