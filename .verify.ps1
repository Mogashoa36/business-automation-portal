# Parses create-admin.ps1 and reports any syntax error. Does not run it.
$path = 'C:\Users\User\Downloads\Systems\company\scripts\create-admin.ps1'
$errors = $null
$tokens = $null
[void][System.Management.Automation.Language.Parser]::ParseFile($path, [ref]$tokens, [ref]$errors)
if ($errors -and $errors.Count -gt 0) {
    Write-Output "SYNTAX ERRORS: $($errors.Count)"
    $errors | ForEach-Object { Write-Output ("  line " + $_.Extent.StartLineNumber + ": " + $_.Message) }
} else {
    Write-Output "SYNTAX OK"
}

# Confirms no secret-bearing construct is used anywhere in the script.
$content = Get-Content -LiteralPath $path -Raw
$forbidden = @{
    'setx'                   = 'persists to the user environment'
    'Out-File'               = 'writes to disk'
    'Set-Content'            = 'writes to disk'
    'Add-Content'            = 'writes to disk'
    'WriteAllText'           = 'writes to disk'
    'ConvertFrom-SecureString' = 'could persist the secret'
    'Export-Clixml'          = 'could persist the secret'
    '.env'                   = 'could write a credential file'
    'application.properties' = 'could write a credential file'
}
$hits = 0
foreach ($key in $forbidden.Keys) {
    if ($content -match [regex]::Escape($key)) {
        Write-Output ("FORBIDDEN TOKEN FOUND: " + $key + " (" + $forbidden[$key] + ")")
        $hits++
    }
}
Write-Output ("FORBIDDEN TOKEN COUNT: " + $hits)
