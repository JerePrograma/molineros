param([string]$EvidenceName = 'offline', [string]$TestDatabase = '', [ValidateRange(1, 65535)][int]$TestPort = 5432)
$ErrorActionPreference = 'Stop'
$repoPath = [System.IO.Path]::GetFullPath((Join-Path $PSScriptRoot '..\..'))
$artifactPath = Join-Path $repoPath ('output\orden-compra-qa\' + $EvidenceName)
$classOutput = Join-Path $artifactPath 'classes'
$latinEncoding = [System.Text.Encoding]::GetEncoding(28591)
$initialHead = (& git -C $repoPath rev-parse HEAD).Trim()
if ($LASTEXITCODE -ne 0) { throw 'No se pudo registrar HEAD inicial.' }
New-Item -ItemType Directory -Path $classOutput -Force | Out-Null
# Misma sintaxis/source/target del harness primera_carga_test.ps1.
# Se omiten placeholders de la copia local y se usa el compilador de Tomcat existente.
$classpathEntries = @($classOutput, (Join-Path $repoPath 'ext-impl\classes'), (Join-Path $repoPath 'ext-service\classes'), (Join-Path $repoPath 'ext-web\docroot\WEB-INF\classes'))
foreach ($folder in @('lib\global', 'lib\portal', 'lib\development', 'ext-lib\global', 'modules', 'ext-web\docroot\WEB-INF\lib')) {
    $classpathEntries += @(Get-ChildItem -LiteralPath (Join-Path $repoPath $folder) -Filter '*.jar' -ErrorAction SilentlyContinue | Where-Object { $_.Length -gt 10 -and $_.Name -ne 'ecj.jar' } | ForEach-Object { $_.FullName })
}
$classpathEntries += 'C:\apache-tomcat-8.5.23\lib\ecj-4.6.3.jar'
$classpath = $classpathEntries -join ';'
$sources = @(
    'ext-impl\src\ar\com\ospim\compras\WebKeysCompras.java',
    'ext-impl\src\ar\com\ospim\compras\requerimientos\action\CambiarEstadoRequerimientoCompraAction.java',
    'ext-impl\src\ar\com\ospim\compras\requerimientos\action\RequerimientoCompraOrdenAction.java',
    'ext-impl\src\ar\com\ospim\compras\requerimientos\action\RequerimientoCompraRenderActionUtil.java',
    'ext-impl\src\ar\com\ospim\compras\requerimientos\beans\RequerimientoCompraOrden.java',
    'ext-impl\src\ar\com\ospim\compras\requerimientos\helper\RequerimientoCompraOrdenHelper.java',
    'ext-impl\src\ar\com\ospim\compras\requerimientos\helper\RequerimientoCompraOrdenPdfHelper.java',
    'ext-impl\src\ar\com\ospim\compras\requerimientos\service\RequerimientoCompraOrdenServiceImpl.java',
    'ext-impl\src\ar\com\ospim\compras\requerimientos\service\RequerimientoCompraOrdenServiceUtil.java',
    'ext-impl\src\ar\com\ospim\servlets\PdfServlet.java',
    'test\compras\ComprasOrdenHelperTest.java',
    'test\compras\ComprasOrdenEdicionJdbcTest.java',
    'test\compras\ComprasOrdenPdfTest.java'
) | ForEach-Object { Join-Path $repoPath $_ }
[System.IO.File]::WriteAllLines((Join-Path $artifactPath 'context.txt'), @(
    ('Inicio UTC: ' + [DateTime]::UtcNow.ToString('yyyy-MM-dd HH:mm:ss')),
    ('HEAD inicial: ' + $initialHead),
    'Estado inicial: OC emitida de solo consulta; edicion limitada de condiciones y observaciones.',
    'Log inicial: sin Tomcat ni UI; JDBC opcional solo en una base aislada de fixtures.',
    'Pruebas: Java8/source1.5, helper con fixtures, Jasper real y JDBC aislado opcional.'
), $latinEncoding)
$sourcepath = @('ext-impl\src', 'ext-service\src', 'ext-web\docroot\WEB-INF\src') | ForEach-Object { Join-Path $repoPath $_ }
$compileOutput = @(& 'C:\Program Files\Java\jdk1.8.0_251\bin\javac.exe' '-encoding' 'ISO-8859-1' '-source' '1.5' '-target' '1.5' '-classpath' $classpath '-sourcepath' ($sourcepath -join ';') '-d' $classOutput @sources 2>&1 | ForEach-Object { $_.ToString() })
$compileExit = $LASTEXITCODE
[System.IO.File]::WriteAllLines((Join-Path $artifactPath 'javac.log'), ($compileOutput + ('javac_exit=' + $compileExit)), $latinEncoding)
if ($compileExit -ne 0) { $compileOutput | Write-Output; throw ('javac fallo: ' + $compileExit) }
foreach ($test in @('ComprasOrdenHelperTest', 'ComprasOrdenPdfTest')) {
    $runOutput = @(& 'C:\Program Files\Java\jdk1.8.0_251\bin\java.exe' '-Djava.awt.headless=true' '-cp' $classpath $test $artifactPath 2>&1 | ForEach-Object { $_.ToString() })
    $runExit = $LASTEXITCODE
    [System.IO.File]::WriteAllLines((Join-Path $artifactPath ($test + '.log')), ($runOutput + ('java_exit=' + $runExit)), $latinEncoding)
    $runOutput | Write-Output
    if ($runExit -ne 0) { throw ($test + ' fallo: ' + $runExit) }
}
if ($TestDatabase.Length -gt 0) {
    if ($TestDatabase -notmatch '^compras_orden_test_edicion_[a-z0-9_]+$') {
        throw 'El test JDBC requiere una base aislada compras_orden_test_edicion_*.'
    }
    $runOutput = @(& 'C:\Program Files\Java\jdk1.8.0_251\bin\java.exe' '-Djava.awt.headless=true' ('-Dcompras.test.port=' + $TestPort) '-cp' $classpath 'ComprasOrdenEdicionJdbcTest' $TestDatabase 2>&1 | ForEach-Object { $_.ToString() })
    $runExit = $LASTEXITCODE
    [System.IO.File]::WriteAllLines((Join-Path $artifactPath 'ComprasOrdenEdicionJdbcTest.log'), ($runOutput + ('java_exit=' + $runExit)), $latinEncoding)
    $runOutput | Write-Output
    if ($runExit -ne 0) { throw ('ComprasOrdenEdicionJdbcTest fallo: ' + $runExit) }
}
Write-Output ('javac_exit=' + $compileExit + ' tests_exit=0')
