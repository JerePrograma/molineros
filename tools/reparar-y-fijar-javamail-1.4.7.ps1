Set-StrictMode -Version 2.0
$ErrorActionPreference = "Stop"

$Repo = "C:\devmolineros\ext"
$Tomcat = "C:\apache-tomcat-8.5.23"

$ExpectedSha1 = "9ADD058589D5D85ADEB625859BF2C5EEAAEDF12D"
$ExpectedSize = 521157
$DownloadUrl = "https://repo1.maven.org/maven2/javax/mail/mail/1.4.7/mail-1.4.7.jar"

$RepositoryJar = Join-Path $Repo "lib\development\mail.jar"
$RuntimeJar = Join-Path $Tomcat "lib\ext\mail.jar"
$KnownDownloadedJar = Join-Path $Tomcat "logs\javamail-correccion-20260622-165340\mail-1.4.7.jar"

$ToolsDir = Join-Path $Repo "tools"
$SyncScript = Join-Path $ToolsDir "sync-javamail-runtime.ps1"
$BuildWrapper = Join-Path $ToolsDir "ant-with-javamail.ps1"

$Timestamp = Get-Date -Format "yyyyMMdd-HHmmss"
$BackupDirectory = Join-Path $Tomcat ("backup\javamail-permanent-" + $Timestamp)
$TemporaryJar = Join-Path $env:TEMP ("mail-1.4.7-" + $Timestamp + ".jar")
$Report = Join-Path $ToolsDir "javamail-permanent-setup.txt"

$Utf8NoBom = New-Object System.Text.UTF8Encoding -ArgumentList $false

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

function Assert-JavaMail147 {
    param(
        [string]$Path,
        [string]$Description
    )

    if (-not (Test-JavaMail147 -Path $Path)) {
        $sha1 = Get-Sha1 -Path $Path
        $size = if (Test-Path $Path) { (Get-Item $Path).Length } else { $null }

        throw (
            "{0} no corresponde a JavaMail 1.4.7. Ruta={1}, SHA-1={2}, tamano={3}." -f
            $Description,
            $Path,
            $sha1,
            $size
        )
    }
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
    $startupBat = Join-Path $Tomcat "bin\startup.bat"
    $listener = Get-Port8080Listener

    if ($null -ne $listener) {
        Write-Host ("Tomcat ya escucha en 8080. PID: {0}" -f $listener.OwningProcess)
        return
    }

    if (-not (Test-Path $startupBat)) {
        throw ("No existe startup.bat: {0}" -f $startupBat)
    }

    Write-Host "Iniciando Tomcat..."

    $startupProcess = Start-Process `
        -FilePath "cmd.exe" `
        -ArgumentList @("/c", ('"' + $startupBat + '"')) `
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

function Assert-PowerShellFile {
    param([string]$Path)

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
Write-Host "1. Diagnostico actual"
Write-Host "============================================================"

foreach ($requiredDirectory in @($Repo, $Tomcat)) {
    if (-not (Test-Path $requiredDirectory)) {
        throw ("No existe la carpeta requerida: {0}" -f $requiredDirectory)
    }
}

[System.IO.Directory]::CreateDirectory($ToolsDir) | Out-Null

$runtimeBefore = Get-Sha1 -Path $RuntimeJar
$repositoryBefore = Get-Sha1 -Path $RepositoryJar

Write-Host ("Runtime SHA-1 actual: {0}" -f $runtimeBefore)
Write-Host ("Repositorio SHA-1 actual: {0}" -f $repositoryBefore)

Write-Host ""
Write-Host "============================================================"
Write-Host "2. Seleccion de fuente JavaMail 1.4.7"
Write-Host "============================================================"

$SourceJar = $null

if (Test-JavaMail147 -Path $RuntimeJar) {
    $SourceJar = $RuntimeJar
    Write-Host "Se usara la copia valida actualmente instalada en Tomcat."
} elseif (Test-JavaMail147 -Path $KnownDownloadedJar) {
    $SourceJar = $KnownDownloadedJar
    Write-Host "Se usara la copia validada descargada anteriormente."
} elseif (Test-JavaMail147 -Path $RepositoryJar) {
    $SourceJar = $RepositoryJar
    Write-Host "Se usara la copia valida del repositorio."
} else {
    Write-Host "No se encontro una copia valida local. Descargando JavaMail 1.4.7..."

    [Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12

    Invoke-WebRequest `
        -Uri $DownloadUrl `
        -OutFile $TemporaryJar `
        -UseBasicParsing

    $SourceJar = $TemporaryJar
}

Assert-JavaMail147 -Path $SourceJar -Description "la fuente seleccionada"

Write-Host ("Fuente: {0}" -f $SourceJar)
Write-Host ("SHA-1: {0}" -f (Get-Sha1 -Path $SourceJar))

$runtimeNeedsUpdate = -not (Test-JavaMail147 -Path $RuntimeJar)
$repositoryNeedsUpdate = -not (Test-JavaMail147 -Path $RepositoryJar)
$tomcatWasRunning = @(Get-TomcatProcesses).Count -gt 0

Write-Host ("Actualizar runtime: {0}" -f $runtimeNeedsUpdate)
Write-Host ("Actualizar repositorio: {0}" -f $repositoryNeedsUpdate)
Write-Host ("Tomcat estaba ejecutandose: {0}" -f $tomcatWasRunning)

Write-Host ""
Write-Host "============================================================"
Write-Host "3. Respaldo y reparacion"
Write-Host "============================================================"

[System.IO.Directory]::CreateDirectory($BackupDirectory) | Out-Null

$runtimeBackup = Join-Path $BackupDirectory "mail-runtime-anterior.jar"
$repositoryBackup = Join-Path $BackupDirectory "mail-development-anterior.jar"

if (Test-Path $RuntimeJar) {
    Copy-Item -Path $RuntimeJar -Destination $runtimeBackup -Force
}

if (Test-Path $RepositoryJar) {
    Copy-Item -Path $RepositoryJar -Destination $repositoryBackup -Force
}

try {
    if ($runtimeNeedsUpdate) {
        Stop-TomcatSafely

        [System.IO.Directory]::CreateDirectory(
            [System.IO.Path]::GetDirectoryName($RuntimeJar)
        ) | Out-Null

        Copy-Item -Path $SourceJar -Destination $RuntimeJar -Force
        Assert-JavaMail147 -Path $RuntimeJar -Description "el mail.jar de Tomcat"

        Clear-TomcatCaches
    }

    if ($repositoryNeedsUpdate) {
        [System.IO.Directory]::CreateDirectory(
            [System.IO.Path]::GetDirectoryName($RepositoryJar)
        ) | Out-Null

        Copy-Item -Path $SourceJar -Destination $RepositoryJar -Force
        Assert-JavaMail147 -Path $RepositoryJar -Description "el mail.jar del repositorio"
    }

    if ($tomcatWasRunning -and $runtimeNeedsUpdate) {
        Start-TomcatSafely
    }
} catch {
    $originalError = $_

    try {
        Stop-TomcatSafely
    } catch {
        Write-Warning ("No se pudo detener Tomcat durante el rollback: {0}" -f $_.Exception.Message)
    }

    if (Test-Path $runtimeBackup) {
        Copy-Item -Path $runtimeBackup -Destination $RuntimeJar -Force
    }

    if (Test-Path $repositoryBackup) {
        Copy-Item -Path $repositoryBackup -Destination $RepositoryJar -Force
    }

    Clear-TomcatCaches

    if ($tomcatWasRunning) {
        try {
            Start-TomcatSafely
        } catch {
            Write-Warning ("El rollback termino, pero Tomcat no pudo reiniciarse: {0}" -f $_.Exception.Message)
        }
    }

    throw $originalError
}

Assert-JavaMail147 -Path $RuntimeJar -Description "el mail.jar final de Tomcat"
Assert-JavaMail147 -Path $RepositoryJar -Description "el mail.jar final del repositorio"

Write-Host ("Runtime reparado: {0}" -f (Get-Sha1 -Path $RuntimeJar))
Write-Host ("Repositorio reparado: {0}" -f (Get-Sha1 -Path $RepositoryJar))
Write-Host ("Backup: {0}" -f $BackupDirectory)

Write-Host ""
Write-Host "============================================================"
Write-Host "4. Creacion del sincronizador permanente"
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

    return (
        (Get-Sha1 -Path $Path) -eq $ExpectedSha1 -and
        (Get-Item $Path).Length -eq $ExpectedSize
    )
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

function Stop-Tomcat {
    foreach ($process in @(Get-TomcatProcesses)) {
        Write-Host ("Deteniendo Tomcat PID {0}..." -f $process.ProcessId)
        Stop-Process -Id $process.ProcessId -Force -ErrorAction Stop
    }

    Start-Sleep -Seconds 3
}

function Start-Tomcat {
    $startupBat = Join-Path $Tomcat "bin\startup.bat"

    $process = Start-Process `
        -FilePath "cmd.exe" `
        -ArgumentList @("/c", ('"' + $startupBat + '"')) `
        -WorkingDirectory (Join-Path $Tomcat "bin") `
        -Wait `
        -PassThru `
        -NoNewWindow

    if ($process.ExitCode -ne 0) {
        throw ("startup.bat termino con codigo {0}." -f $process.ExitCode)
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
$backupDirectory = Join-Path $Tomcat ("backup\javamail-auto-" + (Get-Date -Format "yyyyMMdd-HHmmss"))
$backupJar = Join-Path $backupDirectory "mail-runtime-anterior.jar"

[System.IO.Directory]::CreateDirectory($backupDirectory) | Out-Null

if (Test-Path $RuntimeJar) {
    Copy-Item -Path $RuntimeJar -Destination $backupJar -Force
}

try {
    Stop-Tomcat

    Copy-Item -Path $SourceJar -Destination $RuntimeJar -Force

    if (-not (Test-JavaMail147 -Path $RuntimeJar)) {
        throw "La restauracion de JavaMail 1.4.7 en Tomcat fallo."
    }

    foreach ($directory in @(
        (Join-Path $Tomcat "work\Catalina"),
        (Join-Path $Tomcat "temp")
    )) {
        if (Test-Path $directory) {
            Get-ChildItem -Path $directory -Force -ErrorAction SilentlyContinue |
                Remove-Item -Recurse -Force -ErrorAction SilentlyContinue
        }
    }

    if ($wasRunning) {
        Start-Tomcat
    }

    Write-Host "JavaMail 1.4.7 fue restaurado en Tomcat."
    Write-Host ("Backup: {0}" -f $backupDirectory)
} catch {
    $originalError = $_

    if (Test-Path $backupJar) {
        Copy-Item -Path $backupJar -Destination $RuntimeJar -Force
    }

    if ($wasRunning) {
        Start-Tomcat
    }

    throw $originalError
}
'@

[System.IO.File]::WriteAllText($SyncScript, $SyncContent, $Utf8NoBom)
Assert-PowerShellFile -Path $SyncScript

Write-Host ("Creado: {0}" -f $SyncScript)

Write-Host ""
Write-Host "============================================================"
Write-Host "5. Creacion del wrapper de Ant"
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
    if ($null -eq $Targets -or $Targets.Count -eq 0) {
        Write-Host "Ejecutando: ant"
        & ant
    } else {
        Write-Host ("Ejecutando: ant {0}" -f ($Targets -join " "))
        & ant @Targets
    }

    if ($LASTEXITCODE -ne 0) {
        throw ("Ant termino con codigo {0}." -f $LASTEXITCODE)
    }

    & powershell.exe `
        -NoProfile `
        -ExecutionPolicy Bypass `
        -File $SyncScript

    if ($LASTEXITCODE -ne 0) {
        throw ("La sincronizacion de JavaMail termino con codigo {0}." -f $LASTEXITCODE)
    }

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
Write-Host "6. Verificacion y Git"
Write-Host "============================================================"

& powershell.exe `
    -NoProfile `
    -ExecutionPolicy Bypass `
    -File $SyncScript

if ($LASTEXITCODE -ne 0) {
    throw ("El sincronizador termino con codigo {0}." -f $LASTEXITCODE)
}

Push-Location $Repo

try {
    if ($null -ne (Get-Command git -ErrorAction SilentlyContinue)) {
        & git add -f -- `
            "lib/development/mail.jar" `
            "tools/sync-javamail-runtime.ps1" `
            "tools/ant-with-javamail.ps1"

        if ($LASTEXITCODE -ne 0) {
            throw "No se pudieron agregar los archivos al indice Git."
        }

        Write-Host "Estado Git:"
        & git status --short -- `
            "lib/development/mail.jar" `
            "tools/sync-javamail-runtime.ps1" `
            "tools/ant-with-javamail.ps1"
    }
} finally {
    Pop-Location
}

$reportLines = @(
    "Fecha: $(Get-Date -Format 'yyyy-MM-dd HH:mm:ss')"
    "Runtime: $RuntimeJar"
    "Runtime SHA-1: $(Get-Sha1 -Path $RuntimeJar)"
    "Repositorio: $RepositoryJar"
    "Repositorio SHA-1: $(Get-Sha1 -Path $RepositoryJar)"
    "Sincronizador: $SyncScript"
    "Wrapper Ant: $BuildWrapper"
    "Backup: $BackupDirectory"
)

[System.IO.File]::WriteAllLines($Report, $reportLines, $Utf8NoBom)

Write-Host ""
Write-Host "============================================================"
Write-Host "CORRECCION PERMANENTE COMPLETADA"
Write-Host "============================================================"
Write-Host ("Informe: {0}" -f $Report)
Write-Host ""
Write-Host "Para futuros builds usa:"
Write-Host ".\tools\ant-with-javamail.ps1 clean build"
