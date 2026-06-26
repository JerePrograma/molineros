Set-StrictMode -Version 2.0
$ErrorActionPreference = "Stop"

$Repo = "C:\devmolineros\ext"
$Tomcat = "C:\apache-tomcat-8.5.23"

$ExpectedSha1 = "9ADD058589D5D85ADEB625859BF2C5EEAAEDF12D"
$ExpectedSize = 521157

$SourceJar = Join-Path $Repo "lib\development\mail.jar"
$RuntimeJar = Join-Path $Tomcat "lib\ext\mail.jar"

$ToolsDir = Join-Path $Repo "tools"
$SyncScript = Join-Path $ToolsDir "sync-javamail-runtime.ps1"
$BuildWrapper = Join-Path $ToolsDir "ant-with-javamail.ps1"
$Report = Join-Path $ToolsDir "javamail-permanent-setup.txt"

$Utf8NoBom = New-Object System.Text.UTF8Encoding -ArgumentList $false

function Get-Sha1 {
    param(
        [Parameter(Mandatory = $true)]
        [string]$Path
    )

    if (-not (Test-Path $Path)) {
        return $null
    }

    return (Get-FileHash -Path $Path -Algorithm SHA1).Hash.ToUpper()
}

function Assert-JavaMail147 {
    param(
        [Parameter(Mandatory = $true)]
        [string]$Path,

        [Parameter(Mandatory = $true)]
        [string]$Description
    )

    if (-not (Test-Path $Path)) {
        throw ("No existe {0}: {1}" -f $Description, $Path)
    }

    $sha1 = Get-Sha1 -Path $Path
    $size = (Get-Item $Path).Length

    if ($sha1 -ne $ExpectedSha1 -or $size -ne $ExpectedSize) {
        throw (
            "{0} no corresponde a JavaMail 1.4.7. SHA-1={1}, tamano={2}." -f
            $Description,
            $sha1,
            $size
        )
    }
}

function Assert-PowerShellFile {
    param(
        [Parameter(Mandatory = $true)]
        [string]$Path
    )

    $tokens = $null
    $errors = $null

    [System.Management.Automation.Language.Parser]::ParseFile(
        $Path,
        [ref]$tokens,
        [ref]$errors
    ) | Out-Null

    if ($errors.Count -gt 0) {
        $details = ($errors | ForEach-Object { $_.Message }) -join " | "
        throw ("El script generado no es valido: {0}. Errores: {1}" -f $Path, $details)
    }
}

Write-Host ""
Write-Host "============================================================"
Write-Host "1. Validacion de JavaMail 1.4.7"
Write-Host "============================================================"

foreach ($requiredPath in @($Repo, $Tomcat, $SourceJar, $RuntimeJar)) {
    if (-not (Test-Path $requiredPath)) {
        throw ("No existe la ruta requerida: {0}" -f $requiredPath)
    }
}

Assert-JavaMail147 -Path $SourceJar -Description "el mail.jar del repositorio"
Assert-JavaMail147 -Path $RuntimeJar -Description "el mail.jar de Tomcat"

$sourceSha1 = Get-Sha1 -Path $SourceJar
$runtimeSha1 = Get-Sha1 -Path $RuntimeJar

Write-Host ("Repositorio: {0}" -f $SourceJar)
Write-Host ("SHA-1: {0}" -f $sourceSha1)
Write-Host ""
Write-Host ("Runtime: {0}" -f $RuntimeJar)
Write-Host ("SHA-1: {0}" -f $runtimeSha1)

[System.IO.Directory]::CreateDirectory($ToolsDir) | Out-Null

Write-Host ""
Write-Host "============================================================"
Write-Host "2. Creacion del sincronizador"
Write-Host "============================================================"

$SyncContent = @'
Set-StrictMode -Version 2.0
$ErrorActionPreference = "Stop"

$Repo = "C:\devmolineros\ext"
$Tomcat = "C:\apache-tomcat-8.5.23"

$ExpectedSha1 = "9ADD058589D5D85ADEB625859BF2C5EEAAEDF12D"
$ExpectedSize = 521157

$SourceJar = Join-Path $Repo "lib\development\mail.jar"
$RuntimeJar = Join-Path $Tomcat "lib\ext\mail.jar"
$StartupBat = Join-Path $Tomcat "bin\startup.bat"

function Get-Sha1 {
    param([string]$Path)

    if (-not (Test-Path $Path)) {
        return $null
    }

    return (Get-FileHash -Path $Path -Algorithm SHA1).Hash.ToUpper()
}

function Test-JavaMail147 {
    param([string]$Path)

    if (-not (Test-Path $Path)) {
        return $false
    }

    $sha1 = Get-Sha1 -Path $Path
    $size = (Get-Item $Path).Length

    return ($sha1 -eq $ExpectedSha1 -and $size -eq $ExpectedSize)
}

function Get-TomcatProcesses {
    $escapedTomcat = [regex]::Escape($Tomcat)

    return @(
        Get-CimInstance Win32_Process -ErrorAction SilentlyContinue |
        Where-Object {
            $_.Name -in @("java.exe", "javaw.exe") -and
            $null -ne $_.CommandLine -and
            $_.CommandLine -match "org\.apache\.catalina\.startup\.Bootstrap" -and
            $_.CommandLine -match $escapedTomcat
        }
    )
}

function Get-Port8080Listener {
    return Get-NetTCPConnection -LocalPort 8080 -State Listen -ErrorAction SilentlyContinue |
        Select-Object -First 1
}

function Stop-TomcatSafely {
    $processes = @(Get-TomcatProcesses)

    if ($processes.Count -eq 0) {
        $listener = Get-Port8080Listener

        if ($null -ne $listener) {
            throw (
                "El puerto 8080 esta ocupado por el PID {0}, pero no corresponde al Tomcat esperado." -f
                $listener.OwningProcess
            )
        }

        return
    }

    foreach ($process in $processes) {
        Write-Host ("Deteniendo Tomcat PID {0}..." -f $process.ProcessId)
        Stop-Process -Id $process.ProcessId -Force -ErrorAction Stop
    }

    $limit = (Get-Date).AddSeconds(30)

    do {
        Start-Sleep -Seconds 1
        $remaining = @(Get-TomcatProcesses)
    } while ($remaining.Count -gt 0 -and (Get-Date) -lt $limit)

    if ($remaining.Count -gt 0) {
        throw "No fue posible detener completamente Tomcat."
    }

    $listener = Get-Port8080Listener

    if ($null -ne $listener) {
        throw ("El puerto 8080 continua ocupado por el PID {0}." -f $listener.OwningProcess)
    }
}

function Start-TomcatSafely {
    $listener = Get-Port8080Listener

    if ($null -ne $listener) {
        Write-Host ("Tomcat ya escucha en 8080. PID: {0}" -f $listener.OwningProcess)
        return
    }

    if (-not (Test-Path $StartupBat)) {
        throw ("No existe startup.bat: {0}" -f $StartupBat)
    }

    Write-Host "Iniciando Tomcat..."

    $startupProcess = Start-Process `
        -FilePath "cmd.exe" `
        -ArgumentList @("/c", ('"' + $StartupBat + '"')) `
        -WorkingDirectory (Join-Path $Tomcat "bin") `
        -Wait `
        -PassThru `
        -NoNewWindow

    if ($startupProcess.ExitCode -ne 0) {
        throw ("startup.bat termino con codigo {0}." -f $startupProcess.ExitCode)
    }

    $limit = (Get-Date).AddMinutes(4)
    $listener = $null

    do {
        Start-Sleep -Seconds 3
        $listener = Get-Port8080Listener
    } while ($null -eq $listener -and (Get-Date) -lt $limit)

    if ($null -eq $listener) {
        throw "Tomcat no comenzo a escuchar en 8080 dentro de cuatro minutos."
    }

    Write-Host ("Tomcat iniciado. PID: {0}" -f $listener.OwningProcess)
}

function Clear-TomcatCaches {
    foreach ($directory in @(
        (Join-Path $Tomcat "work\Catalina"),
        (Join-Path $Tomcat "temp")
    )) {
        if (Test-Path $directory) {
            Write-Host ("Limpiando: {0}" -f $directory)

            Get-ChildItem -Path $directory -Force -ErrorAction SilentlyContinue |
                Remove-Item -Recurse -Force -ErrorAction SilentlyContinue
        }
    }
}

if (-not (Test-JavaMail147 -Path $SourceJar)) {
    throw ("El JavaMail versionado no corresponde a 1.4.7: {0}" -f $SourceJar)
}

if (Test-JavaMail147 -Path $RuntimeJar) {
    Write-Host "JavaMail 1.4.7 ya esta instalado en Tomcat."
    exit 0
}

$wasRunning = @(Get-TomcatProcesses).Count -gt 0
$timestamp = Get-Date -Format "yyyyMMdd-HHmmss"
$backupDirectory = Join-Path $Tomcat ("backup\javamail-auto-" + $timestamp)
$backupJar = Join-Path $backupDirectory "mail-runtime-anterior.jar"
$runtimeExisted = Test-Path $RuntimeJar

[System.IO.Directory]::CreateDirectory($backupDirectory) | Out-Null

try {
    Stop-TomcatSafely

    if ($runtimeExisted) {
        Copy-Item -Path $RuntimeJar -Destination $backupJar -Force
    }

    [System.IO.Directory]::CreateDirectory(
        [System.IO.Path]::GetDirectoryName($RuntimeJar)
    ) | Out-Null

    Copy-Item -Path $SourceJar -Destination $RuntimeJar -Force

    if (-not (Test-JavaMail147 -Path $RuntimeJar)) {
        throw "La copia de JavaMail 1.4.7 a Tomcat no quedo valida."
    }

    Clear-TomcatCaches

    if ($wasRunning) {
        Start-TomcatSafely
    }

    Write-Host "JavaMail 1.4.7 fue restaurado en Tomcat."
    Write-Host ("Runtime: {0}" -f $RuntimeJar)
    Write-Host ("Backup: {0}" -f $backupDirectory)
} catch {
    $originalError = $_

    try {
        Stop-TomcatSafely
    } catch {
        Write-Warning ("No se pudo detener Tomcat durante el rollback: {0}" -f $_.Exception.Message)
    }

    if (Test-Path $backupJar) {
        Copy-Item -Path $backupJar -Destination $RuntimeJar -Force
    } elseif (-not $runtimeExisted) {
        Remove-Item -Path $RuntimeJar -Force -ErrorAction SilentlyContinue
    }

    Clear-TomcatCaches

    if ($wasRunning) {
        try {
            Start-TomcatSafely
        } catch {
            Write-Warning ("El rollback termino, pero Tomcat no pudo reiniciarse: {0}" -f $_.Exception.Message)
        }
    }

    throw $originalError
}
'@

[System.IO.File]::WriteAllText($SyncScript, $SyncContent, $Utf8NoBom)
Assert-PowerShellFile -Path $SyncScript

Write-Host ("Creado: {0}" -f $SyncScript)

Write-Host ""
Write-Host "============================================================"
Write-Host "3. Creacion del wrapper de Ant"
Write-Host "============================================================"

$WrapperContent = @'
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
'@

[System.IO.File]::WriteAllText($BuildWrapper, $WrapperContent, $Utf8NoBom)
Assert-PowerShellFile -Path $BuildWrapper

Write-Host ("Creado: {0}" -f $BuildWrapper)

Write-Host ""
Write-Host "============================================================"
Write-Host "4. Prueba del sincronizador"
Write-Host "============================================================"

& powershell.exe `
    -NoProfile `
    -ExecutionPolicy Bypass `
    -File $SyncScript

if ($LASTEXITCODE -ne 0) {
    throw ("El sincronizador termino con codigo {0}." -f $LASTEXITCODE)
}

Write-Host ""
Write-Host "============================================================"
Write-Host "5. Preparacion en Git"
Write-Host "============================================================"

$gitAvailable = $null -ne (Get-Command git -ErrorAction SilentlyContinue)

if ($gitAvailable) {
    Push-Location $Repo

    try {
        & git rev-parse --is-inside-work-tree *> $null

        if ($LASTEXITCODE -eq 0) {
            & git add -f -- `
                "lib/development/mail.jar" `
                "tools/sync-javamail-runtime.ps1" `
                "tools/ant-with-javamail.ps1"

            if ($LASTEXITCODE -ne 0) {
                throw "No se pudieron agregar los archivos al indice Git."
            }

            Write-Host "Archivos agregados al indice Git:"
            & git status --short -- `
                "lib/development/mail.jar" `
                "tools/sync-javamail-runtime.ps1" `
                "tools/ant-with-javamail.ps1"
        } else {
            Write-Warning "La carpeta no parece ser un repositorio Git."
        }
    } finally {
        Pop-Location
    }
} else {
    Write-Warning "Git no esta disponible en PATH."
}

$reportLines = @(
    "Fecha: $(Get-Date -Format 'yyyy-MM-dd HH:mm:ss')"
    "Repositorio: $Repo"
    "Tomcat: $Tomcat"
    "JavaMail repositorio: $SourceJar"
    "JavaMail repositorio SHA-1: $(Get-Sha1 -Path $SourceJar)"
    "JavaMail runtime: $RuntimeJar"
    "JavaMail runtime SHA-1: $(Get-Sha1 -Path $RuntimeJar)"
    "Sincronizador: $SyncScript"
    "Wrapper Ant: $BuildWrapper"
)

[System.IO.File]::WriteAllLines($Report, $reportLines, $Utf8NoBom)

Write-Host ""
Write-Host "============================================================"
Write-Host "CONFIGURACION PERMANENTE COMPLETADA"
Write-Host "============================================================"
Write-Host ("Informe: {0}" -f $Report)
Write-Host ""
Write-Host "Para futuros builds usa:"
Write-Host ".\tools\ant-with-javamail.ps1 clean build"
Write-Host ""
Write-Host "Sustitui 'clean build' por los targets Ant reales del proyecto."
