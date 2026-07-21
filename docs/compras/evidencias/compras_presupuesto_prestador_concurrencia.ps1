param(
    [Parameter(Mandatory = $true)]
    [string]$PsqlPath,

    [Parameter(Mandatory = $true)]
    [string]$Database,

    [string]$DatabaseUser = "postgres",
    [string]$DatabaseHost = "localhost",
    [int]$DatabasePort = 5432
)

$ErrorActionPreference = "Stop"
$testToken = "compras_concurrencia_" + [Guid]::NewGuid().ToString("N")
$tempRoot = [System.IO.Path]::GetFullPath(
    [System.IO.Path]::GetTempPath()
)
$tempDirectory = [System.IO.Path]::GetFullPath(
    (Join-Path $tempRoot $testToken)
)
$workerOneSql = Join-Path $tempDirectory "worker_one.sql"
$workerTwoSql = Join-Path $tempDirectory "worker_two.sql"
$requestId = $null
$processOne = $null
$processTwo = $null
$contractPassed = $false
$escapedToken = $testToken.Replace("'", "''")

function Invoke-Psql {
    param(
        [Parameter(Mandatory = $true)]
        [string]$Sql
    )

    $arguments = @(
        "-X",
        "-v", "ON_ERROR_STOP=1",
        "-h", $DatabaseHost,
        "-p", [string]$DatabasePort,
        "-U", $DatabaseUser,
        "-d", $Database,
        "-At",
        "-c", $Sql
    )

    $output = & $PsqlPath @arguments 2>&1
    if ($LASTEXITCODE -ne 0) {
        throw ($output -join [Environment]::NewLine)
    }

    return ($output -join [Environment]::NewLine).Trim()
}

function ConvertTo-ProcessArgument {
    param(
        [Parameter(Mandatory = $true)]
        [string]$Value
    )

    if ($Value.IndexOf('"') -ge 0) {
        throw "Un argumento de psql contiene comillas no admitidas."
    }

    return '"' + $Value + '"'
}

function Start-PsqlWorker {
    param(
        [Parameter(Mandatory = $true)]
        [string]$SqlPath
    )

    $workerArguments = @(
        "-X",
        "-v", "ON_ERROR_STOP=1",
        "-h", $DatabaseHost,
        "-p", [string]$DatabasePort,
        "-U", $DatabaseUser,
        "-d", $Database,
        "-f", $SqlPath
    )

    $startInfo = New-Object System.Diagnostics.ProcessStartInfo
    $startInfo.FileName = $PsqlPath
    $startInfo.Arguments = ($workerArguments | ForEach-Object {
        ConvertTo-ProcessArgument -Value $_
    }) -join " "
    $startInfo.UseShellExecute = $false
    $startInfo.CreateNoWindow = $true
    $startInfo.RedirectStandardOutput = $true
    $startInfo.RedirectStandardError = $true

    $process = New-Object System.Diagnostics.Process
    $process.StartInfo = $startInfo

    if (!$process.Start()) {
        throw "No se pudo iniciar el worker psql."
    }

    return $process
}

New-Item -ItemType Directory -Path $tempDirectory | Out-Null

try {
    $fixtureSql = @"
INSERT INTO compras.requerimiento (estado, id_sector, alta_usr)
VALUES (1, 7, '$escapedToken');

INSERT INTO compras.requerimiento_detalle (
    id_requerimiento, tipo_item, cantidad, observaciones, alta_usr
)
SELECT id_requerimiento, 'OBSERVACION', 1, 'Concurrencia', '$escapedToken'
  FROM compras.requerimiento
 WHERE alta_usr = '$escapedToken';

INSERT INTO compras.requerimiento_cotizacion_prestador (
    id_requerimiento, id_prestador, estado_envio, intentos,
    email_destino, fecha_envio, alta_usr
)
SELECT id_requerimiento, id_requerimiento + 100000000,
       'ENVIADO', 1, 'concurrencia@example.invalid',
       clock_timestamp(), '$escapedToken'
  FROM compras.requerimiento
 WHERE alta_usr = '$escapedToken';

SELECT compras.cambiar_estado_requerimiento(
    id_requerimiento, 2, '$escapedToken'
)
  FROM compras.requerimiento
 WHERE alta_usr = '$escapedToken';

SELECT id_requerimiento
  FROM compras.requerimiento
 WHERE alta_usr = '$escapedToken';
"@

    $fixtureOutput = Invoke-Psql -Sql $fixtureSql
    $requestId = [int](($fixtureOutput -split "`r?`n")[-1])
    $providerId = $requestId + 100000000
    $fileEntryBase = 7000000000 + ([long]$requestId * 2)

    $workerOne = @"
\set ON_ERROR_STOP on
SET lock_timeout = '10s';
SET statement_timeout = '20s';
BEGIN;
SELECT compras.registrar_requerimiento_presupuesto(
    $requestId, $providerId, 1, 0, $fileEntryBase,
    'concurrencia-1', 'concurrencia-1.pdf', 'concurrencia-1.pdf',
    'Concurrencia 1', 'Prestador concurrente', '$escapedToken'
);
SELECT pg_sleep(2);
COMMIT;
"@

    $workerTwo = @"
\set ON_ERROR_STOP on
SET lock_timeout = '10s';
SET statement_timeout = '20s';
BEGIN;
SELECT compras.registrar_requerimiento_presupuesto(
    $requestId, $providerId, 1, 0, $($fileEntryBase + 1),
    'concurrencia-2', 'concurrencia-2.pdf', 'concurrencia-2.pdf',
    'Concurrencia 2', 'Prestador concurrente', '$escapedToken'
);
COMMIT;
"@

    [System.IO.File]::WriteAllText($workerOneSql, $workerOne)
    [System.IO.File]::WriteAllText($workerTwoSql, $workerTwo)

    $processOne = Start-PsqlWorker -SqlPath $workerOneSql

    Start-Sleep -Milliseconds 250

    $processTwo = Start-PsqlWorker -SqlPath $workerTwoSql

    if (!$processOne.WaitForExit(30000)) {
        throw "El worker uno excedio el limite de 30 segundos."
    }

    if (!$processTwo.WaitForExit(30000)) {
        throw "El worker dos excedio el limite de 30 segundos."
    }

    $workerOneOutput = $processOne.StandardOutput.ReadToEnd()
    $workerTwoOutput = $processTwo.StandardOutput.ReadToEnd()
    $workerOneError = $processOne.StandardError.ReadToEnd()
    $workerTwoError = $processTwo.StandardError.ReadToEnd()

    $successfulWorkers = @(
        $processOne.ExitCode,
        $processTwo.ExitCode
    ) | Where-Object { $_ -eq 0 }

    if ($successfulWorkers.Count -ne 1) {
        $details = @(
            "worker_one exit=$($processOne.ExitCode)",
            $workerOneOutput,
            $workerOneError,
            "worker_two exit=$($processTwo.ExitCode)",
            $workerTwoOutput,
            $workerTwoError
        ) -join [Environment]::NewLine
        throw "Se esperaba exactamente un alta exitosa.`n$details"
    }

    $failedWorkerError = if ($processOne.ExitCode -ne 0) {
        $workerOneError
    }
    else {
        $workerTwoError
    }

    if ($failedWorkerError -notmatch "no se encuentra ENVIADO" -and
            $failedWorkerError -notmatch
                    "ux_compras_presupuesto_requerimiento_prestador_activo") {
        throw "El worker perdedor fallo por una causa inesperada: $failedWorkerError"
    }

    $verificationSql = @"
SELECT count(*)::text || '|' || min(rcp.estado_envio)
  FROM compras.requerimiento_presupuesto rp
  JOIN compras.requerimiento_cotizacion_prestador rcp
    ON rcp.id_requerimiento = rp.id_requerimiento
   AND rcp.id_prestador = rp.id_prestador
 WHERE rp.id_requerimiento = $requestId
   AND rp.id_prestador = $providerId
   AND rp.baja_fecha IS NULL;
"@
    $verification = Invoke-Psql -Sql $verificationSql
    if ($verification -ne "1|COTIZADO") {
        throw "Estado final inesperado: [$verification]"
    }

    $contractPassed = $true
}
finally {
    foreach ($workerProcess in @($processOne, $processTwo)) {
        if ($null -ne $workerProcess -and !$workerProcess.HasExited) {
            $workerProcess.Kill()
            $workerProcess.WaitForExit(5000) | Out-Null
        }
    }

    if ($null -ne $requestId) {
        $cleanupSql = @"
DELETE FROM compras.requerimiento_presupuesto
 WHERE id_requerimiento = $requestId;
DELETE FROM compras.requerimiento_detalle
 WHERE id_requerimiento = $requestId;
DELETE FROM compras.requerimiento_cotizacion_prestador
 WHERE id_requerimiento = $requestId;
DELETE FROM compras.requerimiento
 WHERE id_requerimiento = $requestId
   AND alta_usr = '$escapedToken';
"@
        Invoke-Psql -Sql $cleanupSql | Out-Null
    }

    if ($contractPassed -and (Test-Path -LiteralPath $tempDirectory)) {
        $resolvedTempDirectory =
                (Resolve-Path -LiteralPath $tempDirectory).Path
        $insideTempRoot = $resolvedTempDirectory.StartsWith(
            $tempRoot,
            [StringComparison]::OrdinalIgnoreCase
        )
        $expectedTempLeaf =
                [IO.Path]::GetFileName($resolvedTempDirectory) -eq $testToken

        if (!$insideTempRoot -or !$expectedTempLeaf) {

            throw "Directorio temporal inesperado: $resolvedTempDirectory"
        }

        Remove-Item -LiteralPath $tempDirectory -Recurse -Force
    }
    elseif (Test-Path -LiteralPath $tempDirectory) {
        Write-Warning "Se conservaron diagnosticos en $tempDirectory"
    }
}

Write-Output "CONTRATO_COMPRAS_PRESUPUESTO_CONCURRENCIA_OK"
