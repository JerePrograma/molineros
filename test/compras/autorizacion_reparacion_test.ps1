param(
    [Parameter(Mandatory = $true)][string]$TestDatabase,
    [string]$TestHost = '127.0.0.1',
    [ValidateRange(1, 65535)][int]$TestPort = 55447,
    [string]$TestUser = 'postgres',
    [string]$EvidenceName = 'offline'
)
$ErrorActionPreference = 'Stop'
if ($TestDatabase -notmatch '^compras_autorizacion_test_[a-z0-9_]+$') {
    throw 'Se requiere una base vacia aislada compras_autorizacion_test_*.'
}
$repoPath = [System.IO.Path]::GetFullPath((Join-Path $PSScriptRoot '..\..'))
$artifactPath = Join-Path $repoPath ('output\autorizacion-reparacion-qa\' + $EvidenceName)
$latinEncoding = [System.Text.Encoding]::GetEncoding(28591)
$psql = 'C:\Program Files\PostgreSQL\17\bin\psql.exe'
if (-not (Test-Path -LiteralPath $psql)) { throw 'No se encontro el cliente PostgreSQL 17 configurado para las pruebas aisladas.' }
$initialHead = (& git -C $repoPath rev-parse HEAD).Trim()
if ($LASTEXITCODE -ne 0) { throw 'No se pudo registrar HEAD inicial.' }
New-Item -ItemType Directory -Path $artifactPath -Force | Out-Null
[System.IO.File]::WriteAllLines((Join-Path $artifactPath 'context.txt'), @(
    ('Inicio local: ' + (Get-Date -Format o)),
    ('HEAD inicial: ' + $initialHead),
    'Estado inicial: autorizacion 20261013 rechaza Sistemas 1->6; el test reproduce el RAISE de linea 32.',
    'Log inicial: prueba SQL aislada; no se usa el portal ni su base operativa.',
    'Prueba: fixture sintetico, OC/validador reales 20261023, generica del schema, autorizacion real 20261013.',
    'El runner no crea bases ni inicia o detiene PostgreSQL; exige una base vacia preparada externamente.'
), $latinEncoding)
$ordenFixture = [System.IO.File]::ReadAllText((Join-Path $PSScriptRoot 'orden_compra_test.sql'), $latinEncoding)
$firstInclude = [regex]::Match($ordenFixture, '(?m)^\\ir ')
if (-not $firstInclude.Success) { throw 'No se encontro el limite del fixture de OC.' }
$setup = $ordenFixture.Substring(0, $firstInclude.Index).Replace('compras_orden_test_%', 'compras_autorizacion_test_%')
$schema = [System.IO.File]::ReadAllText((Join-Path $repoPath 'ext-impl\src\ar\com\ospim\compras\sql\compras_schema.sql'), $latinEncoding)
$generic = [regex]::Match($schema, '(?s)CREATE FUNCTION compras\.cambiar_estado_requerimiento\(.*?LANGUAGE plpgsql;')
$authorizationSource = [System.IO.File]::ReadAllText((Join-Path $repoPath 'docs\sql\compras\20261013_autorizacion_requerimiento.sql'), $latinEncoding)
$authorization = [regex]::Match($authorizationSource, '(?s)CREATE OR REPLACE FUNCTION compras\.cambiar_estado_autorizacion_requerimiento\(.*?LANGUAGE plpgsql;')
if (-not $generic.Success -or -not $authorization.Success) { throw 'No se encontraron las funciones reales del fixture.' }
$ocPath = (Join-Path $repoPath 'docs\sql\compras\20261023_orden_compra_formulario.sql').Replace('\', '/')
$testPath = (Join-Path $PSScriptRoot 'autorizacion_reparacion_test.sql').Replace('\', '/')
$setup += "`r`n\i '$ocPath'`r`n" + $generic.Value + "`r`n" + $authorization.Value + "`r`n\i '$testPath'`r`n"
$fixturePath = Join-Path $artifactPath 'fixture.sql'
[System.IO.File]::WriteAllText($fixturePath, $setup, $latinEncoding)
$previousClientEncoding = $env:PGCLIENTENCODING
try {
    $env:PGCLIENTENCODING = 'LATIN1'
    $testOutput = @(& $psql '-X' '-v' 'ON_ERROR_STOP=1' '-h' $TestHost '-p' $TestPort '-U' $TestUser '-d' $TestDatabase '-f' $fixturePath 2>&1 | ForEach-Object { $_.ToString() })
    $testExit = $LASTEXITCODE
    [System.IO.File]::WriteAllLines((Join-Path $artifactPath 'report.txt'), ($testOutput + ('psql_exit=' + $testExit)), $latinEncoding)
    $testOutput | Write-Output
    Write-Output ('psql_exit=' + $testExit)
    if ($testExit -ne 0) { throw ('Prueba SQL fallo: ' + $testExit) }
} finally {
    $env:PGCLIENTENCODING = $previousClientEncoding
}
