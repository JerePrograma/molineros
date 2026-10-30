param([ValidatePattern('^[A-Za-z0-9_-]+$')][string]$EvidenceName = 'offline')
$ErrorActionPreference = 'Stop'
Set-Location -LiteralPath 'C:\wsmolineros\ext'
$root = (git rev-parse --show-toplevel).Trim()
$origin = (git remote get-url origin).Trim()
if ($LASTEXITCODE -ne 0 -or $root -ne 'C:/wsmolineros/ext' -or $origin -ne 'https://github.com/JerePrograma/molineros.git') { throw 'Repositorio o ruta incorrectos.' }
$repoPath = 'C:\wsmolineros\ext'
$initialHead = (& git rev-parse HEAD).Trim()
if ($LASTEXITCODE -ne 0) { throw 'No se pudo registrar HEAD inicial.' }
$artifactPath = Join-Path $repoPath ('output\asociaciones-configuracion-qa\' + $EvidenceName)
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
    'test\compras\ComprasReservaRpJavaTest.java',
    'test\compras\ComprasDuplicadoJavaTest.java',
    'test\compras\ComprasCorreccionJavaTransaccionTest.java',
    'test\compras\ComprasAsociacionesConfiguracionTest.java'
) | ForEach-Object { Join-Path $repoPath $_ }
[System.IO.File]::WriteAllLines((Join-Path $artifactPath 'context.txt'), @(
    ('Inicio UTC: ' + [DateTime]::UtcNow.ToString('yyyy-MM-dd HH:mm:ss')),
    ('HEAD inicial: ' + $initialHead),
    'Estado inicial: asociaciones generales/especificas y catalogos aislados, sin cambios DB.',
    'Log inicial: proceso Java aislado; sin DB, red, Tomcat ni UI.',
    'Alcance: AND/OR, configuracion, historicos, cabecera, detalle, cotizacion, estados, anulacion y atomicidad Helper/JDBC real con conexion simulada.'
), $latinEncoding)
$sourcepath = @('ext-impl\src', 'ext-service\src', 'ext-web\docroot\WEB-INF\src') | ForEach-Object { Join-Path $repoPath $_ }
$compileOutput = @(& 'C:\Program Files\Java\jdk1.8.0_251\bin\javac.exe' '-encoding' 'ISO-8859-1' '-source' '1.5' '-target' '1.5' '-classpath' $classpath '-sourcepath' ($sourcepath -join ';') '-d' $classOutput @sources 2>&1 | ForEach-Object { $_.ToString() })
$compileExit = $LASTEXITCODE
[System.IO.File]::WriteAllLines((Join-Path $artifactPath 'javac.log'), ($compileOutput + ('javac_exit=' + $compileExit)), $latinEncoding)
if ($compileExit -ne 0) { $compileOutput | Write-Output; throw ('javac fallo: ' + $compileExit) }
$runOutput = @(& 'C:\Program Files\Java\jdk1.8.0_251\bin\java.exe' '-Djava.awt.headless=true' '-Dfile.encoding=UTF-8' '-cp' $classpath 'ComprasAsociacionesConfiguracionTest' 2>&1 | ForEach-Object { $_.ToString() })
$runExit = $LASTEXITCODE
[System.IO.File]::WriteAllLines((Join-Path $artifactPath 'report.txt'), ($runOutput + ('java_exit=' + $runExit)), $latinEncoding)
$runOutput | Write-Output
Write-Output ('javac_exit=' + $compileExit + ' java_exit=' + $runExit)
if ($runExit -ne 0) { throw ('harness fallo: ' + $runExit) }

$correccionOutput = @(& 'C:\Program Files\Java\jdk1.8.0_251\bin\java.exe' '-Djava.awt.headless=true' '-cp' $classpath 'ComprasCorreccionJavaTransaccionTest' 2>&1 | ForEach-Object { $_.ToString() })
$correccionExit = $LASTEXITCODE
[System.IO.File]::WriteAllLines((Join-Path $artifactPath 'correccion-report.txt'), ($correccionOutput + ('java_exit=' + $correccionExit)), $latinEncoding)
$correccionOutput | Write-Output
Write-Output ('correccion_java_exit=' + $correccionExit)
if ($correccionExit -ne 0) { throw ('harness correccion fallo: ' + $correccionExit) }
$duplicadoOutput = @(& 'C:\Program Files\Java\jdk1.8.0_251\bin\java.exe' '-Djava.awt.headless=true' '-cp' $classpath 'ComprasDuplicadoJavaTest' 2>&1 | ForEach-Object { $_.ToString() })
$duplicadoExit = $LASTEXITCODE
[System.IO.File]::WriteAllLines((Join-Path $artifactPath 'duplicado-report.txt'), ($duplicadoOutput + ('java_exit=' + $duplicadoExit)), $latinEncoding)
$duplicadoOutput | Write-Output
Write-Output ('duplicado_java_exit=' + $duplicadoExit)
if ($duplicadoExit -ne 0) { throw ('harness duplicado fallo: ' + $duplicadoExit) }

$reservaOutput = @(& 'C:\Program Files\Java\jdk1.8.0_251\bin\java.exe' '-Djava.awt.headless=true' '-cp' $classpath 'ComprasReservaRpJavaTest' 2>&1 | ForEach-Object { $_.ToString() })
$reservaExit = $LASTEXITCODE
[System.IO.File]::WriteAllLines((Join-Path $artifactPath 'reserva-rp-report.txt'), ($reservaOutput + ('java_exit=' + $reservaExit)), $latinEncoding)
$reservaOutput | Write-Output
Write-Output ('reserva_rp_java_exit=' + $reservaExit)
if ($reservaExit -ne 0) { throw ('harness reserva RP fallo: ' + $reservaExit) }
