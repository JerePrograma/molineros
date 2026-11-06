param([string]$EvidencePath = '', [string]$TestDatabase = '', [ValidateRange(1,65535)][int]$TestPort = 55451)
$ErrorActionPreference = 'Stop'
$repoPath = [IO.Path]::GetFullPath((Join-Path $PSScriptRoot '..\..'))
Set-Location -LiteralPath $repoPath
$root = (git rev-parse --show-toplevel).Trim()
$origin = (git remote get-url origin).Trim()
if ($LASTEXITCODE -ne 0 -or $root -ne 'C:/wsmolineros/ext' -or $origin -ne 'https://github.com/JerePrograma/molineros.git') { throw 'Repositorio o ruta incorrectos.' }
if ($TestDatabase.Length -gt 0 -and ($TestDatabase -notmatch '^compras_orden_test_edicion_[a-z0-9_]+$' -or $TestPort -eq 5432)) { throw 'Se requiere base y puerto exclusivos de pruebas aisladas.' }
if ($EvidencePath -eq '') { $EvidencePath = Join-Path $repoPath '.codex\artifacts\orden-seguimiento-20261106' }
$classOutput = Join-Path $EvidencePath 'classes'
New-Item -ItemType Directory -Path $classOutput -Force | Out-Null
$latin = [Text.Encoding]::GetEncoding(28591)
[IO.File]::WriteAllLines((Join-Path $EvidencePath 'context.txt'), @(
    ('Inicio Argentina: ' + [DateTimeOffset]::UtcNow.ToOffset([TimeSpan]::FromHours(-3)).ToString('o')),
    ('HEAD inicial: ' + (& git rev-parse HEAD).Trim()),
    'Estado inicial: seguimiento manual nuevo sin tabla contable; fecha entrega propia obligatoria al emitir.',
    'Log inicial: sin Tomcat ni UI; offline y JDBC opcional en fixture aislado.',
    ('Base aislada: ' + $TestDatabase + ' Puerto: ' + $TestPort),
    'Patron: orden_compra_test.ps1, Java8/source1.5, classpath legacy; sin dependencias nuevas.'
), $latin)
$classpathEntries = @($classOutput, (Join-Path $repoPath 'ext-impl\classes'), (Join-Path $repoPath 'ext-service\classes'), (Join-Path $repoPath 'ext-web\docroot\WEB-INF\classes'))
foreach ($folder in @('lib\global','lib\portal','lib\development','ext-lib\global','modules','ext-web\docroot\WEB-INF\lib')) {
    $classpathEntries += @(Get-ChildItem -LiteralPath (Join-Path $repoPath $folder) -Filter '*.jar' -ErrorAction SilentlyContinue | Where-Object { $_.Length -gt 10 -and $_.Name -ne 'ecj.jar' } | ForEach-Object { $_.FullName })
}
$classpath = $classpathEntries -join ';'
$sources = @(
 'ext-impl\src\ar\com\ospim\compras\requerimientos\beans\RequerimientoCompraOrden.java',
 'ext-impl\src\ar\com\ospim\compras\requerimientos\beans\RequerimientoCompraOrdenSeguimiento.java',
 'ext-impl\src\ar\com\ospim\compras\requerimientos\helper\RequerimientoCompraOrdenHelper.java',
 'ext-impl\src\ar\com\ospim\compras\requerimientos\helper\RequerimientoCompraOrdenSeguimientoHelper.java',
 'ext-impl\src\ar\com\ospim\compras\requerimientos\service\RequerimientoCompraOrdenServiceImpl.java',
 'ext-impl\src\ar\com\ospim\compras\requerimientos\service\RequerimientoCompraOrdenServiceUtil.java',
 'ext-impl\src\ar\com\ospim\compras\requerimientos\service\RequerimientoCompraOrdenSeguimientoServiceImpl.java',
 'ext-impl\src\ar\com\ospim\compras\requerimientos\service\RequerimientoCompraOrdenSeguimientoServiceUtil.java',
 'ext-impl\src\ar\com\ospim\compras\requerimientos\action\RequerimientoCompraOrdenAction.java',
 'test\compras\fixtures\datos\ComprasFuenteDatosFixture.java',
 'test\compras\ComprasOrdenSeguimientoTest.java',
 'test\compras\ComprasOrdenSeguimientoActionTest.java',
 'test\compras\ComprasOrdenSeguimientoJdbcTest.java'
) | ForEach-Object { Join-Path $repoPath $_ }
$sourcepath = @('ext-impl\src','ext-service\src','ext-web\docroot\WEB-INF\src') | ForEach-Object { Join-Path $repoPath $_ }
$compiled = @(& 'C:\Program Files\Java\jdk1.8.0_251\bin\javac.exe' '-encoding' 'ISO-8859-1' '-source' '1.5' '-target' '1.5' '-classpath' $classpath '-sourcepath' ($sourcepath -join ';') '-d' $classOutput @sources 2>&1 | ForEach-Object { $_.ToString() })
$compileExit = $LASTEXITCODE
[IO.File]::WriteAllLines((Join-Path $EvidencePath 'javac.log'), ($compiled + ('javac_exit=' + $compileExit)), $latin)
if ($compileExit -ne 0) { $compiled | Write-Output; throw ('javac fallo: ' + $compileExit) }
$tests = @('ComprasOrdenSeguimientoTest','ComprasOrdenSeguimientoActionTest')
if ($TestDatabase.Length -gt 0) { $tests += 'ComprasOrdenSeguimientoJdbcTest' }
foreach ($test in $tests) {
    $runArgs = @('-Djava.awt.headless=true', ('-Dcompras.test.port=' + $TestPort), '-cp', $classpath, $test)
    if ($test -eq 'ComprasOrdenSeguimientoJdbcTest') { $runArgs += $TestDatabase }
    $result = @(& 'C:\Program Files\Java\jdk1.8.0_251\bin\java.exe' @runArgs 2>&1 | ForEach-Object { $_.ToString() })
    $runExit = $LASTEXITCODE
    [IO.File]::WriteAllLines((Join-Path $EvidencePath ($test + '.log')), ($result + ('java_exit=' + $runExit)), $latin)
    $result | Write-Output
    if ($runExit -ne 0) { throw ($test + ' fallo: ' + $runExit) }
}
Write-Output 'ORDEN_SEGUIMIENTO_QA_OK javac_exit=0 tests_exit=0'
