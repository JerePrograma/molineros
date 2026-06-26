param(
    [Parameter(ValueFromRemainingArguments = $true)]
    [string[]]$Targets
)

Set-StrictMode -Version 2.0
$ErrorActionPreference = "Stop"

$Repo = "C:\devmolineros\ext"
$SyncScript = Join-Path $Repo "tools\sync-javamail-runtime.ps1"

if (-not (Test-Path $SyncScript)) {
    throw ("No existe el sincronizador JavaMail: {0}" -f $SyncScript)
}

Push-Location $Repo

try {
    Write-Host ""

    if ($null -eq $Targets -or $Targets.Count -eq 0) {
        Write-Host "Ejecutando: ant"
        & ant
    } else {
        Write-Host ("Ejecutando: ant {0}" -f ($Targets -join " "))
        & ant @Targets
    }

    $antExitCode = $LASTEXITCODE

    if ($antExitCode -ne 0) {
        throw ("Ant termino con codigo {0}." -f $antExitCode)
    }

    Write-Host ""
    Write-Host "Build terminado. Verificando JavaMail de Tomcat..."

    & powershell.exe `
        -NoProfile `
        -ExecutionPolicy Bypass `
        -File $SyncScript

    $syncExitCode = $LASTEXITCODE

    if ($syncExitCode -ne 0) {
        throw ("La sincronizacion de JavaMail termino con codigo {0}." -f $syncExitCode)
    }

    Write-Host ""
    Write-Host "Build y verificacion JavaMail completados."
} finally {
    Pop-Location
}