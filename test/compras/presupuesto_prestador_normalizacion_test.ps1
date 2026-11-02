param([ValidatePattern('^[A-Za-z0-9_-]+$')][string]$EvidenceName = 'offline', [switch]$Baseline)
$ErrorActionPreference = 'Stop'
Set-Location -LiteralPath 'C:\wsmolineros\ext'
$root = (git rev-parse --show-toplevel).Trim()
$origin = (git remote get-url origin).Trim()
if ($LASTEXITCODE -ne 0 -or $root -ne 'C:/wsmolineros/ext' -or $origin -ne 'https://github.com/JerePrograma/molineros.git') { throw 'Repositorio o ruta incorrectos.' }
$repoPath = 'C:\wsmolineros\ext'
$initialHead = (& git rev-parse HEAD).Trim()
if ($LASTEXITCODE -ne 0) { throw 'No se pudo registrar HEAD inicial.' }
$artifactPath = Join-Path $repoPath ('output\presupuesto-prestador-normalizacion-qa\' + $EvidenceName)
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
    'test\compras\fixtures\configuracion\ConfiguracionComprasDatosFixture.java',
    'test\compras\fixtures\efectos-transaccion\ar\com\ospim\util\ConnectionHelper.java',
    'ext-impl\src\ar\com\ospim\compras\WebKeysCompras.java',
    'ext-impl\src\ar\com\ospim\compras\requerimientos\beans\SectorNomencladorCompra.java',
    'ext-impl\src\ar\com\ospim\compras\requerimientos\helper\NomencladorCompraBusquedaHelper.java',
    'ext-impl\src\ar\com\ospim\compras\requerimientos\helper\ConfiguracionComprasHelper.java',
    'ext-impl\src\ar\com\ospim\compras\requerimientos\service\BusquedaRequerimientoCompraServiceImpl.java',
    'ext-impl\src\ar\com\ospim\compras\requerimientos\service\BusquedaRequerimientoCompraServiceUtil.java',
    'ext-impl\src\ar\com\ospim\compras\requerimientos\beans\ReclamoPrestacionalCompraContexto.java',
    'ext-impl\src\ar\com\ospim\compras\requerimientos\helper\ReclamoPrestacionalCompraPrecargaHelper.java',
    'ext-impl\src\ar\com\ospim\compras\requerimientos\helper\EditarRequerimientoCompraHelper.java',
    'ext-impl\src\ar\com\ospim\compras\requerimientos\service\EditarRequerimientoCompraServiceImpl.java',
    'ext-impl\src\ar\com\ospim\compras\requerimientos\service\EditarRequerimientoCompraServiceUtil.java',
    'ext-impl\src\ar\com\ospim\compras\requerimientos\helper\NotificarCotizacionPrestadorHelper.java',
    'ext-impl\src\ar\com\ospim\compras\requerimientos\documentos\DocumentoLibraryComprasHelper.java',
    'ext-impl\src\ar\com\ospim\compras\requerimientos\helper\RequerimientoCompraReclamoPrestacionalHelper.java',
    'ext-impl\src\ar\com\ospim\compras\requerimientos\service\RequerimientoCompraReclamoPrestacionalServiceImpl.java',
    'ext-impl\src\ar\com\ospim\compras\requerimientos\service\RequerimientoCompraReclamoPrestacionalServiceUtil.java',
    'ext-impl\src\ar\com\ospim\compras\requerimientos\service\RequerimientoCompraReclamoPrestacionalTransaccion.java',
    'ext-impl\src\ar\com\ospim\autorizaciones\action\EditarReclamosEntryAction.java',



    'test\compras\ComprasPresupuestoPrestadorLecturaTest.java',
    'test\compras\ComprasPresupuestoPrestadorInsercionTest.java'
) | ForEach-Object { Join-Path $repoPath $_ }
[System.IO.File]::WriteAllLines((Join-Path $artifactPath 'context.txt'), @(
    ('Inicio UTC: ' + [DateTime]::UtcNow.ToString('yyyy-MM-dd HH:mm:ss')),
    ('HEAD inicial: ' + $initialHead),
    $(if ($Baseline) { 'Estado baseline: columna descripcion_prestador fisica aun usada y auditoria INSERT en bind17.' } else { 'Estado actual: presupuesto sin copia fisica proveedor; proyeccion por ID y auditoria bind16.' }),
    'Log inicial: proceso Java aislado; sin DB, red, Tomcat ni UI.',
    'Alcance: INSERT16, lector por ID, lock solo rp con snapshot Empresa raw, metadatos y conexion caller.'
), $latinEncoding)
$sourcepath = @('ext-impl\src', 'ext-service\src', 'ext-web\docroot\WEB-INF\src') | ForEach-Object { Join-Path $repoPath $_ }
$ErrorActionPreference = 'Continue' # Registrar stderr nativo y validar exit code.
$compileOutput = @(& 'C:\Program Files\Java\jdk1.8.0_251\bin\javac.exe' '-encoding' 'ISO-8859-1' '-source' '1.5' '-target' '1.5' '-classpath' $classpath '-sourcepath' ($sourcepath -join ';') '-d' $classOutput @sources 2>&1 | ForEach-Object { $_.ToString() })
$compileExit = $LASTEXITCODE
$ErrorActionPreference = 'Stop'
[System.IO.File]::WriteAllLines((Join-Path $artifactPath 'javac.log'), ($compileOutput + ('javac_exit=' + $compileExit)), $latinEncoding)
if ($compileExit -ne 0) { $compileOutput | Write-Output; throw ('javac fallo: ' + $compileExit) }
$failedRuns = 0
foreach ($testClass in @('ComprasPresupuestoPrestadorLecturaTest','ComprasPresupuestoPrestadorInsercionTest')) {
    $ErrorActionPreference = 'Continue'
    $runOutput = @(& 'C:\Program Files\Java\jdk1.8.0_251\bin\java.exe' '-Djava.awt.headless=true' '-Dfile.encoding=UTF-8' '-cp' $classpath $testClass 2>&1 | ForEach-Object { $_.ToString() })
    $runExit = $LASTEXITCODE
    $ErrorActionPreference = 'Stop'
    [System.IO.File]::WriteAllLines((Join-Path $artifactPath ($testClass + '.log')), ($runOutput + ('java_exit=' + $runExit)), $latinEncoding)
    $runOutput | Write-Output
    Write-Output ($testClass + ' java_exit=' + $runExit)
    if ($runExit -ne 0) { $failedRuns++ }
}
Write-Output ('javac_exit=' + $compileExit + ' failed_test_processes=' + $failedRuns)
if ($failedRuns -ne 0) { throw ('Contratos de presupuesto incumplidos; procesos fallidos=' + $failedRuns) }
