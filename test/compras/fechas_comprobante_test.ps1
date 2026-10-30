param([string]$EvidencePath='')
$ErrorActionPreference='Stop'
$repoPath=[IO.Path]::GetFullPath((Join-Path $PSScriptRoot '..\..'))
Set-Location -LiteralPath 'C:\wsmolineros\ext'
$root=(git rev-parse --show-toplevel).Trim()
$origin=(git remote get-url origin).Trim()
if($LASTEXITCODE -ne 0 -or $root -ne 'C:/wsmolineros/ext' -or $origin -ne 'https://github.com/JerePrograma/molineros.git' -or $repoPath -ne 'C:\wsmolineros\ext') {
 throw 'Repositorio o ruta incorrectos. No modificar archivos ni remotos.'
}
$initialHead=(& git rev-parse HEAD).Trim()
if($LASTEXITCODE -ne 0) {throw 'No se pudo registrar HEAD inicial.'}
$initialStatus=@(& git status --short)
if($LASTEXITCODE -ne 0) {throw 'No se pudo registrar estado inicial.'}
if($EvidencePath -eq '') {
 $EvidencePath=Join-Path ([IO.Path]::GetTempPath()) ('molineros-compras-fechas-'+[DateTime]::UtcNow.ToString('yyyyMMdd-HHmmss-fff'))
}
$artifactPath=[IO.Path]::GetFullPath($EvidencePath)
if($artifactPath -eq $repoPath -or $artifactPath.StartsWith($repoPath+'\',[StringComparison]::OrdinalIgnoreCase)) {throw 'La evidencia debe guardarse fuera del repositorio.'}
$latinEncoding=[Text.Encoding]::GetEncoding(28591)
$classOutput=Join-Path $artifactPath 'classes'
$apiOutput=Join-Path $artifactPath 'classes-api-real'
$emptySources=Join-Path $artifactPath 'sin-fuentes-implicitas'
foreach($folder in @($classOutput,$apiOutput,$emptySources)) {[IO.Directory]::CreateDirectory($folder) | Out-Null}
$classpathEntries=@((Join-Path $repoPath 'ext-impl\classes'),(Join-Path $repoPath 'ext-service\classes'),(Join-Path $repoPath 'ext-web\docroot\WEB-INF\classes'))
foreach($folder in @('lib\global','lib\portal','lib\development','ext-lib\global','ext-lib\portal','ext-lib\development','modules','ext-web\docroot\WEB-INF\lib')) {
 $fullFolder=Join-Path $repoPath $folder
 if(Test-Path -LiteralPath $fullFolder) {
  $classpathEntries+=@(Get-ChildItem -LiteralPath $fullFolder -Filter '*.jar' | Where-Object {$_.Length -gt 10 -and $_.Name -ne 'ecj.jar'} | ForEach-Object {$_.FullName})
 }
}
$classpathEntries+='C:\apache-tomcat-8.5.23\lib\servlet-api.jar'
$classpathEntries+='C:\apache-tomcat-8.5.23\webapps\ROOT\WEB-INF\lib\portal-impl.jar'
$productionSources=@(
 'ext-impl\src\ar\com\ospim\autorizaciones\action\EditarPrestacionReclamoAction.java',
 'ext-impl\src\ar\com\ospim\autorizaciones\action\ValidarReclamo.java',
 'ext-impl\src\ar\com\ospim\autorizaciones\beans\PrestacionesReclamo.java',
 'ext-impl\src\ar\com\ospim\util\DateUtils.java'
) | ForEach-Object {Join-Path $repoPath $_}
[IO.File]::WriteAllLines((Join-Path $artifactPath 'contexto.txt'),(@(
 ('Inicio Argentina: '+[TimeZoneInfo]::ConvertTimeBySystemTimeZoneId([DateTime]::UtcNow,'Argentina Standard Time').ToString('yyyy-MM-dd HH:mm:ss')),
 ('HEAD inicial: '+$initialHead),
 'Estado funcional inicial: prueba aislada del Action de edicion, validacion de propagacion de fechas y consentimientos Compras.',
 'Log inicial: offline, no usa Tomcat. HTTP/contexto y datos DB simulados; Helper de seleccion, ParamUtil, SessionErrors, bean y DateUtils reales.',
 'Sin DB, red, documentos ni correo. No demuestra UI, permisos reales ni end-to-end.',
 'Compilacion API real sin fixtures antes de compilar y ejecutar la regresion focalizada.',
 'Estado Git inicial:'
)+$initialStatus),$latinEncoding)
$apiClasspath=(@($apiOutput)+$classpathEntries) -join ';'
$productionSourcepath=@('ext-impl\src','ext-service\src','ext-web\docroot\WEB-INF\src') | ForEach-Object {Join-Path $repoPath $_}
$ErrorActionPreference='Continue' # stderr nativo se registra; manda el exit code.
$apiCompile=@(& 'C:\Program Files\Java\jdk1.8.0_251\bin\javac.exe' '-encoding' 'ISO-8859-1' '-source' '1.5' '-target' '1.5' '-classpath' $apiClasspath '-sourcepath' ($productionSourcepath -join ';') '-d' $apiOutput @productionSources 2>&1 | ForEach-Object {$_.ToString()})
$apiExit=$LASTEXITCODE
$ErrorActionPreference='Stop'
[IO.File]::WriteAllLines((Join-Path $artifactPath 'javac-api-real.log'),($apiCompile+('javac_exit='+$apiExit)),$latinEncoding)
$apiCompile | Write-Output
if($apiExit -ne 0) {throw ('Compilacion contra API real fallo: '+$apiExit)}
$fixtures=@(Get-ChildItem -LiteralPath (Join-Path $PSScriptRoot 'fixtures\fechas-comprobante') -Recurse -Filter '*.java' | ForEach-Object {$_.FullName})
$sources=$fixtures+$productionSources+@(Join-Path $PSScriptRoot 'fixtures\datos\ComprasFuenteDatosFixture.java')+@(Join-Path $PSScriptRoot 'ComprasFechasComprobanteTest.java')
$classpath=(@($classOutput,$apiOutput)+$classpathEntries) -join ';'
$ErrorActionPreference='Continue' # stderr nativo se registra; manda el exit code.
$compileOutput=@(& 'C:\Program Files\Java\jdk1.8.0_251\bin\javac.exe' '-encoding' 'ISO-8859-1' '-source' '1.5' '-target' '1.5' '-classpath' $classpath '-sourcepath' $emptySources '-d' $classOutput @sources 2>&1 | ForEach-Object {$_.ToString()})
$compileExit=$LASTEXITCODE
$ErrorActionPreference='Stop'
[IO.File]::WriteAllLines((Join-Path $artifactPath 'javac.log'),($compileOutput+('javac_exit='+$compileExit)),$latinEncoding)
$compileOutput | Write-Output
if($compileExit -ne 0) {throw ('Compilacion de regresion fallo: '+$compileExit)}
$ErrorActionPreference='Continue' # stderr nativo se registra; manda el exit code.
$runOutput=@(& 'C:\Program Files\Java\jdk1.8.0_251\bin\java.exe' '-Djava.awt.headless=true' '-cp' $classpath 'ComprasFechasComprobanteTest' 2>&1 | ForEach-Object {$_.ToString()})
$runExit=$LASTEXITCODE
$ErrorActionPreference='Stop'
[IO.File]::WriteAllLines((Join-Path $artifactPath 'regresion.log'),($runOutput+('java_exit='+$runExit)),$latinEncoding)
$runOutput | Write-Output
Write-Output ('javac_api_real_exit='+$apiExit+' javac_exit='+$compileExit+' java_exit='+$runExit)
Write-Output ('Evidencia: '+$artifactPath)
if($runExit -ne 0) {throw ('Regresion de fechas fallo: '+$runExit)}
