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