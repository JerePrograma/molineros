param([string]$EvidencePath = '')
$ErrorActionPreference = 'Stop'
Set-Location -LiteralPath 'C:\wsmolineros\ext'
$root = (git rev-parse --show-toplevel).Trim()
$origin = (git remote get-url origin).Trim()
if ($LASTEXITCODE -ne 0 -or $root -ne 'C:/wsmolineros/ext' -or $origin -ne 'https://github.com/JerePrograma/molineros.git') { throw 'Repositorio o ruta incorrectos.' }
$repoPath = 'C:\wsmolineros\ext'
$initialHead = (& git rev-parse HEAD).Trim()
if ($LASTEXITCODE -ne 0) { throw 'No se pudo registrar HEAD inicial.' }
if ($EvidencePath -eq '') { $EvidencePath = Join-Path ([IO.Path]::GetTempPath()) ('compras-detalle-adjudicado-' + [DateTime]::UtcNow.ToString('yyyyMMdd-HHmmss-fff')) }
$artifactPath = $EvidencePath
$classOutput = Join-Path $artifactPath 'classes'
$latinEncoding = [System.Text.Encoding]::GetEncoding(28591)
New-Item -ItemType Directory -Path $classOutput -Force | Out-Null
# Referencia: documentos_permiso_alta_test.ps1; JDK8 y source/target1.5 legacy.
$classpathEntries = @($classOutput, (Join-Path $repoPath 'ext-impl\build\classes'), (Join-Path $repoPath 'ext-impl\classes'), (Join-Path $repoPath 'ext-service\classes'), (Join-Path $repoPath 'ext-web\docroot\WEB-INF\classes'))
foreach ($folder in @('lib\global', 'lib\portal', 'lib\development', 'ext-lib\global', 'modules', 'ext-web\docroot\WEB-INF\lib')) {
    $classpathEntries += @(Get-ChildItem -LiteralPath (Join-Path $repoPath $folder) -Filter '*.jar' -ErrorAction SilentlyContinue | Where-Object { $_.Length -gt 10 -and $_.Name -ne 'ecj.jar' } | ForEach-Object { $_.FullName })
}
$classpath = $classpathEntries -join ';'
$sources = @(
    'ext-impl\src\ar\com\ospim\compras\WebKeysCompras.java',
    'ext-impl\src\ar\com\ospim\compras\requerimientos\beans\RequerimientoCompra.java',
    'ext-impl\src\ar\com\ospim\compras\requerimientos\beans\RequerimientoCompraDetalle.java',
    'ext-impl\src\ar\com\ospim\compras\requerimientos\helper\ConfiguracionComprasHelper.java',
    'ext-impl\src\ar\com\ospim\compras\requerimientos\helper\EditarRequerimientoCompraHelper.java',
    'ext-impl\src\ar\com\ospim\compras\requerimientos\helper\RequerimientoCompraComparativaHelper.java',
    'ext-impl\src\ar\com\ospim\compras\requerimientos\helper\RequerimientoCompraOrdenHelper.java',
    'ext-impl\src\ar\com\ospim\compras\requerimientos\helper\ReclamoPrestacionalCompraPrecargaHelper.java',
    'ext-impl\src\ar\com\ospim\compras\requerimientos\action\RequerimientoCompraRenderActionUtil.java',
    'ext-impl\src\ar\com\ospim\compras\requerimientos\service\RequerimientoCompraComparativaServiceImpl.java',
    'ext-impl\src\ar\com\ospim\compras\requerimientos\service\RequerimientoCompraComparativaServiceUtil.java',
    'test\compras\fixtures\configuracion\ConfiguracionComprasDatosFixture.java',
    'test\compras\ComprasDetalleAdjudicadoVistaTest.java'
) | ForEach-Object { Join-Path $repoPath $_ }
[System.IO.File]::WriteAllLines((Join-Path $artifactPath 'context.txt'), @(
    ('Inicio UTC: ' + [DateTime]::UtcNow.ToString('yyyy-MM-dd HH:mm:ss')),
    ('HEAD inicial: ' + $initialHead),
    'Estado inicial: detalle muestra cantidad original; fuente adjudicada separada en comparativa.',
    'Log inicial: proceso Java aislado; sin DB, red, Tomcat ni UI.',
    'Alcance: proyeccion de vista, ceros, precision, errores, duplicados y entradas originales RP/OC/PDF. Sin DB ni UI.'
), $latinEncoding)
$sourcepath = @('ext-impl\src', 'ext-service\src', 'ext-web\docroot\WEB-INF\src') | ForEach-Object { Join-Path $repoPath $_ }
$compileOutput = @(& 'C:\Program Files\Java\jdk1.8.0_251\bin\javac.exe' '-encoding' 'ISO-8859-1' '-source' '1.5' '-target' '1.5' '-classpath' $classpath '-sourcepath' ($sourcepath -join ';') '-d' $classOutput @sources 2>&1 | ForEach-Object { $_.ToString() })
$compileExit = $LASTEXITCODE
[System.IO.File]::WriteAllLines((Join-Path $artifactPath 'javac.log'), ($compileOutput + ('javac_exit=' + $compileExit)), $latinEncoding)
if ($compileExit -ne 0) { $compileOutput | Write-Output; throw ('javac fallo: ' + $compileExit) }
$runOutput = @(& 'C:\Program Files\Java\jdk1.8.0_251\bin\java.exe' '-Djava.awt.headless=true' '-cp' $classpath 'ComprasDetalleAdjudicadoVistaTest' 2>&1 | ForEach-Object { $_.ToString() })
$runExit = $LASTEXITCODE
[IO.File]::WriteAllLines((Join-Path $artifactPath 'detalle-adjudicado-report.txt'), ($runOutput + ('java_exit=' + $runExit)), $latinEncoding)
$runOutput | Write-Output
Write-Output ('javac_exit=' + $compileExit + ' java_exit=' + $runExit)
if ($runExit -ne 0) { throw ('harness detalle adjudicado fallo: ' + $runExit) }
