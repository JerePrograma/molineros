param(
    [string]$EvidenceName = 'offline',
    [string]$ServiceSnapshotPath = ''
)
$ErrorActionPreference = 'Stop'
$repoPath = [System.IO.Path]::GetFullPath((Join-Path $PSScriptRoot '..\..'))
$artifactPath = Join-Path $repoPath ('.codex\artifacts\compras-primera-carga-20261023\' + $EvidenceName)
$classOutput = Join-Path $artifactPath 'classes'
$latinEncoding = [System.Text.Encoding]::GetEncoding(28591)
$initialHead = (& git -C $repoPath rev-parse HEAD).Trim()
if ($LASTEXITCODE -ne 0) { throw 'No se pudo registrar HEAD inicial.' }
New-Item -ItemType Directory -Path $classOutput -Force | Out-Null
$classpath = @($classOutput, (Join-Path $repoPath 'ext-impl\classes'), (Join-Path $repoPath 'ext-service\classes'), (Join-Path $repoPath 'ext-web\docroot\WEB-INF\classes'), (Join-Path $repoPath 'lib\global\*'), (Join-Path $repoPath 'lib\portal\*'), (Join-Path $repoPath 'lib\development\*')) -join ';'
$sources = @(
    (Join-Path $PSScriptRoot 'fixtures\primera-carga\ar\com\ospim\util\ConnectionHelper.java'),
    (Join-Path $repoPath 'ext-impl\src\ar\com\ospim\compras\requerimientos\beans\RequerimientoCompra.java'),
    (Join-Path $repoPath 'ext-impl\src\ar\com\ospim\compras\requerimientos\beans\RequerimientoCompraFiltro.java'),
    (Join-Path $repoPath 'ext-impl\src\ar\com\ospim\compras\requerimientos\service\BusquedaRequerimientoCompraServiceImpl.java'),
    (Join-Path $repoPath 'ext-impl\src\ar\com\ospim\compras\requerimientos\service\BusquedaRequerimientoCompraServiceUtil.java'),
    (Join-Path $PSScriptRoot 'ComprasPrimeraCargaTest.java')
)
if ($ServiceSnapshotPath -ne '') {
    $sources[3] = Join-Path $ServiceSnapshotPath 'BusquedaRequerimientoCompraServiceImpl.java'
    $sources[4] = Join-Path $ServiceSnapshotPath 'BusquedaRequerimientoCompraServiceUtil.java'
}
[System.IO.File]::WriteAllLines((Join-Path $artifactPath 'context.txt'), @(
    ('Inicio UTC: ' + [DateTime]::UtcNow.ToString('yyyy-MM-dd HH:mm:ss')),
    ('HEAD inicial: ' + $initialHead),
    'Estado: prueba aislada de ServiceUtil y ServiceImpl productivos; JDBC simulado.',
    'Log inicial: no usa Tomcat, drivers JDBC, red, DB ni UI autenticada.',
    'No demuestra tiempos ni planes de PostgreSQL.'
), $latinEncoding)
$compileOutput = @(& 'C:\Program Files\Java\jdk1.8.0_251\bin\javac.exe' '-encoding' 'ISO-8859-1' '-source' '1.5' '-target' '1.5' '-classpath' $classpath '-d' $classOutput @sources 2>&1 | ForEach-Object { $_.ToString() })
$compileExit = $LASTEXITCODE
[System.IO.File]::WriteAllLines((Join-Path $artifactPath 'javac.log'), ($compileOutput + ('javac_exit=' + $compileExit)), $latinEncoding)
if ($compileExit -ne 0) { $compileOutput | Write-Output; throw ('javac fallo: ' + $compileExit) }
$runOutput = @(& 'C:\Program Files\Java\jdk1.8.0_251\bin\java.exe' '-cp' $classpath 'ComprasPrimeraCargaTest' 2>&1 | ForEach-Object { $_.ToString() })
$runExit = $LASTEXITCODE
[System.IO.File]::WriteAllLines((Join-Path $artifactPath 'report.txt'), ($runOutput + ('java_exit=' + $runExit)), $latinEncoding)
$compileOutput | Write-Output
$runOutput | Write-Output
Write-Output ('javac_exit=' + $compileExit + ' java_exit=' + $runExit)
if ($runExit -ne 0) { throw ('harness fallo: ' + $runExit) }
