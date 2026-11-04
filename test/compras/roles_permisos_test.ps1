param([string]$EvidencePath='', [switch]$ReproOnly)
$ErrorActionPreference='Stop'
$repoPath=[IO.Path]::GetFullPath((Join-Path $PSScriptRoot '..\..'))
Set-Location -LiteralPath 'C:\wsmolineros\ext'
$root=(git rev-parse --show-toplevel).Trim()
$origin=(git remote get-url origin).Trim()
if($LASTEXITCODE -ne 0 -or $root -ne 'C:/wsmolineros/ext' -or $origin -ne 'https://github.com/JerePrograma/molineros.git' -or $repoPath -ne 'C:\wsmolineros\ext') {throw 'Repositorio o ruta incorrectos. No modificar archivos ni remotos.'}
$initialHead=(& git rev-parse HEAD).Trim()
if($LASTEXITCODE -ne 0) {throw 'No se pudo registrar HEAD inicial.'}
if($EvidencePath -eq '') {$EvidencePath=Join-Path ([IO.Path]::GetTempPath()) ('molineros-compras-roles-'+[DateTime]::UtcNow.ToString('yyyyMMdd-HHmmss-fff'))}
$artifactPath=[IO.Path]::GetFullPath($EvidencePath)
if($artifactPath -eq $repoPath -or $artifactPath.StartsWith($repoPath+'\',[StringComparison]::OrdinalIgnoreCase)) {throw 'La evidencia debe guardarse fuera del repositorio.'}
$latinEncoding=[Text.Encoding]::GetEncoding(28591)
$classOutput=Join-Path $artifactPath 'classes'
[IO.Directory]::CreateDirectory($classOutput) | Out-Null
# Patron existente de configuracion_test.ps1: JDK8, source/target1.5 y reglas reales.
$classpathEntries=@($classOutput,(Join-Path $repoPath 'ext-impl\classes'),(Join-Path $repoPath 'ext-service\classes'),(Join-Path $repoPath 'ext-web\docroot\WEB-INF\classes'))
foreach($folder in @('lib\global','lib\portal','lib\development','ext-lib\global','ext-lib\portal','ext-lib\development','modules','ext-web\docroot\WEB-INF\lib')) {
 $fullFolder=Join-Path $repoPath $folder
 if(Test-Path -LiteralPath $fullFolder) {$classpathEntries+=@(Get-ChildItem -LiteralPath $fullFolder -Filter '*.jar' | Where-Object {$_.Length -gt 10 -and $_.Name -ne 'ecj.jar'} | ForEach-Object {$_.FullName})}
}
$classpathEntries+='C:\apache-tomcat-8.5.23\lib\servlet-api.jar'
$classpathEntries+='C:\apache-tomcat-8.5.23\webapps\ROOT\WEB-INF\lib\portal-impl.jar'
$classpath=$classpathEntries -join ';'
$sources=@(
 'ext-impl\src\ar\com\ospim\util\PermissionUtil.java',
 'ext-impl\src\ar\com\ospim\compras\requerimientos\helper\ConfiguracionComprasHelper.java',
 'ext-impl\src\ar\com\ospim\compras\WebKeysCompras.java',
 'ext-impl\src\ar\com\ospim\compras\requerimientos\beans\RequerimientoCompra.java',
 'ext-impl\src\ar\com\ospim\compras\requerimientos\service\BusquedaRequerimientoCompraServiceUtil.java',
 'ext-impl\src\ar\com\ospim\compras\requerimientos\service\BusquedaRequerimientoCompraServiceImpl.java',
 'ext-impl\src\ar\com\ospim\compras\requerimientos\action\BuscarRequerimientosComprasAction.java',
 'ext-impl\src\ar\com\ospim\compras\requerimientos\action\VerRequerimientoCompraAction.java',
 'ext-impl\src\ar\com\ospim\compras\requerimientos\action\EditarRequerimientoCompraAction.java',
 'ext-impl\src\ar\com\ospim\compras\requerimientos\action\CambiarEstadoRequerimientoCompraAction.java',
 'ext-impl\src\ar\com\ospim\compras\requerimientos\helper\ExportarRequerimientosCompraHelper.java',
 'test\compras\fixtures\datos\ComprasFuenteDatosFixture.java',
 'test\compras\ComprasRolesPermisosTest.java'
) | ForEach-Object {Join-Path $repoPath $_}
$sourcepath=@('ext-impl\src','ext-service\src','ext-web\docroot\WEB-INF\src') | ForEach-Object {Join-Path $repoPath $_}
[IO.File]::WriteAllLines((Join-Path $artifactPath 'contexto.txt'),@(
 ('Inicio Argentina: '+[TimeZoneInfo]::ConvertTimeBySystemTimeZoneId([DateTime]::UtcNow,'Argentina Standard Time').ToString('yyyy-MM-dd HH:mm:ss')),
 ('HEAD inicial: '+$initialHead),
 ('Estado funcional inicial: validadores productivos de cuatro Actions y helper; ReproOnly='+$ReproOnly),
 'Log inicial: proceso Java aislado; sin Tomcat, DB, red, correo ni UI.',
 'Frontera de datos simulada en ServiceImpl; PermissionUtil y configuracion son productivos.',
 'No verifica persistencia, navegacion ni permisos efectivos de una sesion autenticada.'
),$latinEncoding)
$ErrorActionPreference='Continue' # Registrar stderr nativo; comprobar exit code.
$compileOutput=@(& 'C:\Program Files\Java\jdk1.8.0_251\bin\javac.exe' '-encoding' 'ISO-8859-1' '-source' '1.5' '-target' '1.5' '-classpath' $classpath '-sourcepath' ($sourcepath -join ';') '-d' $classOutput @sources 2>&1 | ForEach-Object {$_.ToString()})
$compileExit=$LASTEXITCODE
$ErrorActionPreference='Stop'
[IO.File]::WriteAllLines((Join-Path $artifactPath 'javac.log'),($compileOutput+('javac_exit='+$compileExit)),$latinEncoding)
$compileOutput | Write-Output
if($compileExit -ne 0) {throw ('Compilacion roles fallo: '+$compileExit)}
$mode=if($ReproOnly) {'repro'} else {'regresion'}
$ErrorActionPreference='Continue' # Registrar stderr nativo; comprobar exit code.
$runOutput=@(& 'C:\Program Files\Java\jdk1.8.0_251\bin\java.exe' '-Djava.awt.headless=true' '-cp' $classpath 'ComprasRolesPermisosTest' $mode 2>&1 | ForEach-Object {$_.ToString()})
$runExit=$LASTEXITCODE
$ErrorActionPreference='Stop'
[IO.File]::WriteAllLines((Join-Path $artifactPath 'resultado.log'),($runOutput+('java_exit='+$runExit)),$latinEncoding)
$runOutput | Write-Output
Write-Output ('javac_exit='+$compileExit+' java_exit='+$runExit)
Write-Output ('Evidencia: '+$artifactPath)
if($runExit -ne 0) {throw ('Prueba roles fallo: '+$runExit)}
