@echo off
REM ===========================================================================
REM  BIZFLOW Technologies - secure administrator bootstrap
REM
REM  Creates the FIRST administrator account by collecting credentials with
REM  masked input and handing them to the existing Spring Boot bootstrap
REM  runner. It adds no account creation logic of its own.
REM
REM    scripts\create-admin.cmd
REM
REM  There is no default account and no default password anywhere in this
REM  project. If an administrator already exists the backend ignores these
REM  values, and this script reports that and changes nothing.
REM
REM  The password is never echoed, never written to a file, never passed as a
REM  command line argument, and never set with SETX, so it is never stored in
REM  the shell history, the user environment or the repository.
REM
REM  All masked input and process handling lives in create-admin.ps1, which is
REM  invoked below. CMD cannot mask keystrokes on its own, and SET /P cannot
REM  switch the console to hidden echo.
REM ===========================================================================
setlocal

set "SCRIPT_DIR=%~dp0"

echo.
echo ==========================================
echo  BIZFLOW Technologies
echo  Secure Administrator Bootstrap
echo ==========================================
echo.
echo This creates the INITIAL administrator account.
echo.
echo IMPORTANT:
echo   - No default credentials are used.
echo   - Only possible while no administrator exists.
echo   - The temporary password MUST be changed after first login.
echo   - Existing administrators are never reset or modified.
echo.

powershell -NoProfile -ExecutionPolicy Bypass -File "%SCRIPT_DIR%create-admin.ps1" %*
set "EXITCODE=%ERRORLEVEL%"

echo.
if "%EXITCODE%"=="0" (
  echo   Done.
) else (
  echo   No changes were made. Exit code: %EXITCODE%
)
echo.

endlocal & exit /b %EXITCODE%
