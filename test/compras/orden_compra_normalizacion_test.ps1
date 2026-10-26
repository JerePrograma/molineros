param(
    [Parameter(Mandatory = $true)][string]$TestDatabase,
    [ValidateRange(1, 65535)][int]$TestPort = 55449,
    [string]$EvidenceName = 'normalizacion',
    [switch]$SqlOnly
)
$ErrorActionPreference = 'Stop'
if ($TestDatabase -notmatch '^compras_orden_test_edicion_[a-z0-9_]+$') {
    throw 'Se requiere una base vacia aislada compras_orden_test_edicion_*.'
}
$repoPath = [System.IO.Path]::GetFullPath((Join-Path $PSScriptRoot '..\..'))
$artifactPath = Join-Path $repoPath ('output\orden-compra-qa\' + $EvidenceName)
$latinEncoding = [System.Text.Encoding]::GetEncoding(28591)
$psql = 'C:\Program Files\PostgreSQL\17\bin\psql.exe'
New-Item -ItemType Directory -Path $artifactPath -Force | Out-Null
$initialHead = (& git -C $repoPath rev-parse HEAD).Trim()
if ($LASTEXITCODE -ne 0) { throw 'No se pudo registrar HEAD inicial.' }
[System.IO.File]::WriteAllLines((Join-Path $artifactPath 'normalizacion-context.txt'), @(
    ('Inicio America/Buenos_Aires: ' + [DateTimeOffset]::UtcNow.ToOffset([TimeSpan]::FromHours(-3)).ToString('o')),
    ('HEAD inicial: ' + $initialHead),
    ('Base aislada: ' + $TestDatabase + ' Puerto: ' + $TestPort),
    'Estado inicial: OC anterior con moneda/IVA/precios NOT NULL; fuentes y snapshots sinteticos.',
    'Log inicial: sin portal; migracion real dos veces y JDBC productivo con creacion, consulta y concurrencia.',
    'El runner exige base vacia; no crea bases ni inicia o detiene PostgreSQL.'
), $latinEncoding)
$fixture = [System.IO.File]::ReadAllText((Join-Path $PSScriptRoot 'orden_compra_test.sql'), $latinEncoding)
$firstInclude = [regex]::Match($fixture, '(?m)^\\ir ')
if (-not $firstInclude.Success) { throw 'No se encontro el inicio de la instalacion OC del fixture historico.' }
$setup = $fixture.Substring(0, $firstInclude.Index).Replace('compras_orden_test_%', 'compras_orden_test_edicion_%')
$testPath = (Join-Path $PSScriptRoot 'orden_compra_normalizacion_test.sql').Replace('\', '/')
$setup += "`r`n\i '$testPath'`r`n"
$fixturePath = Join-Path $artifactPath 'normalizacion-fixture.sql'
[System.IO.File]::WriteAllText($fixturePath, $setup, $latinEncoding)
$previousEncoding = $env:PGCLIENTENCODING
try {
    $env:PGCLIENTENCODING = 'LATIN1'
    $result = @(& $psql '-X' '-v' 'ON_ERROR_STOP=1' '-h' '127.0.0.1' '-p' $TestPort '-U' 'postgres' '-d' $TestDatabase '-f' $fixturePath 2>&1 | ForEach-Object { $_.ToString() })
    $sqlExit = $LASTEXITCODE
    [System.IO.File]::WriteAllLines((Join-Path $artifactPath 'normalizacion-sql.log'), ($result + ('psql_exit=' + $sqlExit)), $latinEncoding)
    $result | Write-Output
    if ($sqlExit -ne 0) { throw ('SQL normalizacion fallo: ' + $sqlExit) }
} finally {
    $env:PGCLIENTENCODING = $previousEncoding
}
if (-not $SqlOnly) {
    & (Join-Path $PSScriptRoot 'orden_compra_test.ps1') -EvidenceName $EvidenceName -TestDatabase $TestDatabase -TestPort $TestPort
}
