<#
.SYNOPSIS
    Secure console helper for scripts\create-admin.cmd.

.DESCRIPTION
    Collects the initial administrator's details with masked password input,
    then hands them to the EXISTING Spring Boot bootstrap runner by starting
    the backend the same way scripts\start-backend.cmd does.

    This script deliberately implements no account creation logic. The
    authoritative rules live in the backend:

        PasswordPolicy       12-100 characters, upper, lower and a digit
        AdminUser            username 3-60 chars, letters/digits/._-
        AdminUserBootstrap   creates only when zero administrators exist,
                             always sets mustChangePassword = true

    The checks below mirror those rules purely so the developer gets an
    immediate, clear message. The backend re-validates everything regardless
    and remains the security boundary; if the two ever disagree, the backend
    wins and this script simply fails first with a clear explanation.

.NOTES
    Security:
      - The password is read with Read-Host -AsSecureString, so it is never
        echoed to the console.
      - It is never written to disk, never logged, never echoed, and never
        placed in a command line argument (where it would be visible to any
        other process on the machine).
      - It exists only in this process's memory, and only for as long as the
        backend takes to start.
      - SETX is never used, so nothing is written to the user environment.
#>

$ErrorActionPreference = 'Stop'

# Mirrors company.backend.security.PasswordPolicy
$MinPasswordLength = 12
$MaxPasswordLength = 100

# Mirrors company.backend.entity.AdminUser
$MinUsernameLength = 3
$MaxUsernameLength = 60

# Log lines AdminUserBootstrap writes. Read only, and only to report the
# outcome; the backend's own admin-exists check is never bypassed. The runner
# logs nothing when it skips, which is itself the "already exists" signal.
$SuccessMarker   = 'Created the first administrator'
$PolicyRejected  = 'bootstrap password was rejected'
$InvalidUsername = 'not a valid username'
$NoCredentials   = 'no bootstrap credentials are configured'

function Write-Fail([string]$Message) {
    # Deliberately never interpolates the password into a message.
    Write-Host ''
    Write-Host "ERROR: $Message" -ForegroundColor Red
}

function Read-Required {
    <#
        Prompts until a non-empty value is given.
    #>
    param([string]$Prompt, [string]$ErrorText)

    while ($true) {
        $value = Read-Host $Prompt
        if ([string]::IsNullOrWhiteSpace($value)) {
            Write-Fail $ErrorText
            continue
        }
        return $value.Trim()
    }
}

function Test-Username {
    param([string]$Value)

    if ($Value.Length -lt $MinUsernameLength -or $Value.Length -gt $MaxUsernameLength) {
        return "Username must be $MinUsernameLength to $MaxUsernameLength characters."
    }
    foreach ($char in $Value.ToCharArray()) {
        if (-not ([char]::IsLetterOrDigit($char)) -and $char -ne '.' -and $char -ne '_' -and $char -ne '-') {
            return 'Usernames may only contain letters, numbers, dots, underscores and hyphens.'
        }
    }
    return $null
}

function Test-Email {
    # Deliberately permissive. The backend only requires a non-empty value at
    # bootstrap, so anything stricter here would reject input the server
    # accepts.
    param([string]$Value)

    if ($Value -notmatch '^[^\s@]+@[^\s@]+\.[^\s@]+$') {
        return 'Enter a valid email address, for example name@example.com.'
    }
    return $null
}

function Get-SecurePassword {
    <#
        Reads a password twice with masked input and returns it as a plain
        string for the environment variable.

        The conversion is unavoidable: the backend reads a plain environment
        variable. The plaintext is held in this process only, is never echoed,
        and goes out of scope as soon as the backend has started.
    #>
    while ($true) {
        $first  = Read-Host 'Password' -AsSecureString
        $second = Read-Host 'Confirm password' -AsSecureString

        $a = [System.Net.NetworkCredential]::new('', $first).Password
        $b = [System.Net.NetworkCredential]::new('', $second).Password

        if ($a -cne $b) {
            # The values themselves are never printed, only the reason.
            Write-Fail 'Passwords do not match.'
            $a = $null
            $b = $null
            Write-Host 'Please try again.' -ForegroundColor Yellow
            continue
        }

        if ($a.Length -lt $MinPasswordLength) {
            Write-Fail "Password must be at least $MinPasswordLength characters long."
            $a = $null
            continue
        }
        if ($a.Length -gt $MaxPasswordLength) {
            Write-Fail "Password must be $MaxPasswordLength characters or fewer."
            $a = $null
            continue
        }
        if ($a -notmatch '[A-Z]') {
            Write-Fail 'Password must contain at least one uppercase letter.'
            $a = $null
            continue
        }
        if ($a -notmatch '[a-z]') {
            Write-Fail 'Password must contain at least one lowercase letter.'
            $a = $null
            continue
        }
        if ($a -notmatch '[0-9]') {
            Write-Fail 'Password must contain at least one number.'
            $a = $null
            continue
        }

        return $a
    }
}

function Stop-Backend {
    param([int]$Id)

    if ($Id -le 0) { return }
    try {
        # The backend is a Maven wrapper that spawns a JVM, so the whole tree
        # has to go or a stray java process keeps the port.
        & taskkill.exe /PID $Id /T /F 2>&1 | Out-Null
    }
    catch {
        # Already gone, which is the outcome we wanted anyway.
    }
}

# --------------------------------------------------------------------------
# 1. Collect the details
# --------------------------------------------------------------------------

$username = Read-Required -Prompt 'Username' -ErrorText 'Username is required.'

while ($true) {
    $problem = Test-Username -Value $username
    if (-not $problem) { break }
    Write-Fail $problem
    $username = Read-Required -Prompt 'Username' -ErrorText 'Username is required.'
}

$email = Read-Required -Prompt 'Email' -ErrorText 'Email is required.'

while ($true) {
    $problem = Test-Email -Value $email
    if (-not $problem) { break }
    Write-Fail $problem
    $email = Read-Required -Prompt 'Email' -ErrorText 'Email is required.'
}

$displayName = Read-Required -Prompt 'Display name' -ErrorText 'Display name is required.'

$password = Get-SecurePassword

# --------------------------------------------------------------------------
# 2. Hand the values to the existing backend
# --------------------------------------------------------------------------

# Scoped to this process only. Nothing is written to the user or machine
# environment, so the secret disappears with this script. SETX is deliberately
# never used.
$env:APP_ADMIN_BOOTSTRAP_USERNAME     = $username
$env:APP_ADMIN_BOOTSTRAP_EMAIL        = $email
$env:APP_ADMIN_BOOTSTRAP_DISPLAY_NAME = $displayName
$env:APP_ADMIN_BOOTSTRAP_PASSWORD     = $password

# Drop the local copy as early as possible. The environment copy is what the
# child process inherits, and this variable is no longer needed.
$password = $null

$repoRoot   = Split-Path -Parent $PSScriptRoot
$backendDir = Join-Path $repoRoot 'backend'
$logFile    = Join-Path $env:TEMP ('bizflow-bootstrap-' + [guid]::NewGuid().ToString('N') + '.log')

Write-Host ''
Write-Host 'Starting the backend to run the existing bootstrap...'
Write-Host 'This takes around 30 seconds. The password is not shown or logged.'
Write-Host ''

$backend  = $null
$exitCode = 1

try {
    if (-not (Test-Path (Join-Path $backendDir 'mvnw.cmd'))) {
        Write-Fail "Could not find the backend at $backendDir."
        exit 1
    }

    # StartProcess inherits this process's environment, which is how the
    # bootstrap values reach the backend. Reusing the Maven wrapper keeps the
    # startup identical to scripts\start-backend.cmd.
    $backend = Start-Process -FilePath 'cmd.exe' `
        -ArgumentList '/c', 'mvnw.cmd spring-boot:run' `
        -WorkingDirectory $backendDir `
        -RedirectStandardOutput $logFile `
        -RedirectStandardError "$logFile.err" `
        -NoNewWindow `
        -PassThru

    # The ApplicationRunner fires during startup, so watching the log is enough
    # to know the outcome. No long wait for a healthy server is needed.
    $deadline = (Get-Date).AddSeconds(180)
    $decided  = $false

    while ((Get-Date) -lt $deadline) {
        Start-Sleep -Seconds 2

        $output = ''
        if (Test-Path $logFile) {
            $output = Get-Content -LiteralPath $logFile -Raw -ErrorAction SilentlyContinue
        }
        if (-not $output -and (Test-Path "$logFile.err")) {
            $output = Get-Content -LiteralPath "$logFile.err" -Raw -ErrorAction SilentlyContinue
        }
        if (-not $output) {
            # A failed start never reaches the runner.
            if ($backend.HasExited) { break }
            continue
        }

        if ($output -match [regex]::Escape($SuccessMarker)) {
            Write-Host ''
            Write-Host '  Administrator created.' -ForegroundColor Green
            Write-Host "  Username: $username"
            $exitCode = 0
            $decided  = $true
            break
        }
        if ($output -match [regex]::Escape($PolicyRejected)) {
            Write-Host ''
            Write-Fail 'The backend rejected the password against its policy. No administrator was created.'
            $decided = $true
            break
        }
        if ($output -match [regex]::Escape($InvalidUsername)) {
            Write-Host ''
            Write-Fail 'The backend rejected the username. No administrator was created.'
            $decided = $true
            break
        }
        if ($output -match [regex]::Escape($NoCredentials)) {
            Write-Host ''
            Write-Fail 'The backend did not receive the bootstrap values. No administrator was created.'
            $decided = $true
            break
        }

        if ($backend.HasExited) { break }
    }

    if (-not $decided) {
        Write-Host ''
        if ($backend.HasExited) {
            Write-Fail 'The backend exited before the bootstrap ran. Check the output above.'
        }
        else {
            # The runner logs nothing when it skips, so reaching the deadline
            # with no success marker means an administrator already exists.
            # The backend decided that, not this script.
            Write-Host '  An administrator account already exists.' -ForegroundColor Yellow
            Write-Host '  The bootstrap process will not modify or reset existing'
            Write-Host '  administrators, and these values were ignored.'
            Write-Host ''
            Write-Host '  No changes were made.'
            $exitCode = 0
        }
    }
}
catch {
    Write-Fail "The bootstrap could not be completed: $($_.Exception.Message)"
    $exitCode = 1
}
finally {
    # Stop the backend so the port is free and the secret is not left running
    # in a process that inherited it.
    if ($backend -and -not $backend.HasExited) {
        Write-Host ''
        Write-Host 'Stopping the bootstrap backend...'
        Stop-Backend -Id $backend.Id
    }

    # Remove the inherited values from this environment immediately.
    Remove-Item Env:\APP_ADMIN_BOOTSTRAP_PASSWORD -ErrorAction SilentlyContinue
    Remove-Item Env:\APP_ADMIN_BOOTSTRAP_USERNAME -ErrorAction SilentlyContinue
    Remove-Item Env:\APP_ADMIN_BOOTSTRAP_EMAIL -ErrorAction SilentlyContinue
    Remove-Item Env:\APP_ADMIN_BOOTSTRAP_DISPLAY_NAME -ErrorAction SilentlyContinue

    # The log can contain the username, so it is removed rather than left in
    # the temp directory. It never contains the password.
    Remove-Item -LiteralPath $logFile -Force -ErrorAction SilentlyContinue
    Remove-Item -LiteralPath "$logFile.err" -Force -ErrorAction SilentlyContinue
}

if ($exitCode -eq 0) {
    Write-Host ''
    Write-Host '=========================================='
    Write-Host ' Administrator bootstrap completed'
    Write-Host '=========================================='
    Write-Host ''
    Write-Host 'Next steps:'
    Write-Host ''
    Write-Host '1. Start/open the BIZFLOW frontend.'
    Write-Host '2. Open:'
    Write-Host '   http://localhost:4200/admin'
    Write-Host '3. Sign in using your bootstrap username.'
    Write-Host '4. You will be required to change the temporary password.'
    Write-Host '5. Use the new permanent password for future sign-ins.'
    Write-Host ''
    Write-Host 'The bootstrap password should not be reused as the'
    Write-Host 'permanent password. To start normally afterwards, use:'
    Write-Host '   scripts\start-all.cmd'
    Write-Host ''
}

exit $exitCode

