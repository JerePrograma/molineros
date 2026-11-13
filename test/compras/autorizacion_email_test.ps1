param([string]$EvidenceName = 'offline')
$ErrorActionPreference = 'Stop'
$repoPath = [IO.Path]::GetFullPath((Join-Path $PSScriptRoot '..\..'))
Set-Location -LiteralPath $repoPath
$root = (git rev-parse --show-toplevel).Trim()
$origin = (git remote get-url origin).Trim()
if ($LASTEXITCODE -ne 0 -or $root -ne 'C:/wsmolineros/ext' -or $origin -ne 'https://github.com/JerePrograma/molineros.git') { throw 'Repositorio o ruta incorrectos.' }
$artifactPath = Join-Path $repoPath ('output\autorizacion-email-qa\' + $EvidenceName)
$classOutput = Join-Path $artifactPath 'classes'
$latinEncoding = [Text.Encoding]::GetEncoding(28591)
$initialHead = (& git -C $repoPath rev-parse HEAD).Trim()
if ($LASTEXITCODE -ne 0) { throw 'No se pudo registrar HEAD inicial.' }
New-Item -ItemType Directory -Path $classOutput -Force | Out-Null
# Adaptado de comparativa_sincronizacion_test.ps1: mismas dependencias y compatibilidad Java legacy.
$classpathEntries = @($classOutput, (Join-Path $repoPath 'ext-impl\classes'), (Join-Path $repoPath 'ext-service\classes'), (Join-Path $repoPath 'ext-web\docroot\WEB-INF\classes'))
foreach ($folder in @('lib\global', 'lib\portal', 'lib\development', 'ext-lib\global', 'modules', 'ext-web\docroot\WEB-INF\lib')) {
    $classpathEntries += @(Get-ChildItem -LiteralPath (Join-Path $repoPath $folder) -Filter '*.jar' -ErrorAction SilentlyContinue | Where-Object { $_.Length -gt 10 -and $_.Name -ne 'ecj.jar' } | ForEach-Object { $_.FullName })
}
$classpathEntries += 'C:\apache-tomcat-8.5.23\lib\ecj-4.6.3.jar'
$classpath = $classpathEntries -join ';'
$sources = @(
    'test\compras\fixtures\configuracion\ConfiguracionComprasDatosFixture.java',
    'ext-impl\src\ar\com\ospim\compras\requerimientos\helper\CotizacionPrestadorMailHelper.java',
    'ext-impl\src\ar\com\ospim\compras\requerimientos\helper\NotificarAutorizacionCompraHelper.java',
    'ext-impl\src\ar\com\ospim\compras\requerimientos\action\CambiarEstadoRequerimientoCompraAction.java',
    'test\compras\ComprasAutorizacionEmailTest.java',
    'test\compras\ComprasAutorizadorPermisosTest.java'
) | ForEach-Object { Join-Path $repoPath $_ }
$initialLog = 'Sin log de Tomcat disponible; pruebas offline sin iniciar el servidor.'
$logPath = 'C:\apache-tomcat-8.5.23\logs'
if (Test-Path -LiteralPath $logPath) {
    $recentLog = Get-ChildItem -LiteralPath $logPath -File -Filter '*.log' | Sort-Object LastWriteTime -Descending | Select-Object -First 1
    if ($null -ne $recentLog) { $initialLog = 'Log existente no utilizado por el fixture offline: ' + $recentLog.FullName }
}
[IO.File]::WriteAllLines((Join-Path $artifactPath 'context.txt'), @(
    ('Inicio UTC: ' + [DateTime]::UtcNow.ToString('yyyy-MM-dd HH:mm:ss')),
    ('Inicio Argentina: ' + [TimeZoneInfo]::ConvertTimeBySystemTimeZoneId([DateTime]::UtcNow, 'Argentina Standard Time').ToString('yyyy-MM-dd HH:mm:ss')),
    ('HEAD inicial: ' + $initialHead),
    'Estado inicial: la transicion se confirma antes del correo; se revisan separadores finales, cuerpo sin usuario y todos los presupuestos cargados.',
    ('Log inicial: ' + $initialLog),
    'Pruebas: MIME real con pedido, comparativa, ficha RP, tres ordenes medicas y dos presupuestos originales sin OC; separadores finales y configuracion estricta/QA; recuperacion productiva de presupuestos con identidad, sitio, VIEW y version DL; cuerpo sin usuario; incidencias en log posterior commit como Liquidaciones; limites y fallos DL/persistencia/SMTP y doble peticion.',
    'Fronteras de datos, DL, persistencia y SMTP simuladas; sin DB, red, correo real, Tomcat ni UI.'
), $latinEncoding)
$sourcepath = @('ext-impl\src', 'ext-service\src', 'ext-web\docroot\WEB-INF\src') | ForEach-Object { Join-Path $repoPath $_ }
$ErrorActionPreference = 'Continue'
$compileOutput = @(& 'C:\Program Files\Java\jdk1.8.0_251\bin\javac.exe' '-encoding' 'ISO-8859-1' '-source' '1.5' '-target' '1.5' '-classpath' $classpath '-sourcepath' ($sourcepath -join ';') '-d' $classOutput @sources 2>&1 | ForEach-Object { $_.ToString() })
$compileExit = $LASTEXITCODE
$ErrorActionPreference = 'Stop'
[IO.File]::WriteAllLines((Join-Path $artifactPath 'javac.log'), ($compileOutput + ('javac_exit=' + $compileExit)), $latinEncoding)
if ($compileExit -ne 0) { $compileOutput | Write-Output; throw ('javac fallo: ' + $compileExit) }
$ErrorActionPreference = 'Continue'
$runOutput = @(& 'C:\Program Files\Java\jdk1.8.0_251\bin\java.exe' '-Djava.awt.headless=true' '-cp' $classpath 'ComprasAutorizacionEmailTest' 2>&1 | ForEach-Object { $_.ToString() })
$runExit = $LASTEXITCODE
$ErrorActionPreference = 'Stop'
[IO.File]::WriteAllLines((Join-Path $artifactPath 'ComprasAutorizacionEmailTest.log'), ($runOutput + ('java_exit=' + $runExit)), $latinEncoding)
$runOutput | Write-Output
Write-Output ('javac_exit=' + $compileExit + ' java_exit=' + $runExit)
if ($runExit -ne 0) { throw ('ComprasAutorizacionEmailTest fallo: ' + $runExit) }
$ErrorActionPreference = 'Continue'
$permissionsOutput = @(& 'C:\Program Files\Java\jdk1.8.0_251\bin\java.exe' '-Djava.awt.headless=true' '-cp' $classpath 'ComprasAutorizadorPermisosTest' 2>&1 | ForEach-Object { $_.ToString() })
$permissionsExit = $LASTEXITCODE
$ErrorActionPreference = 'Stop'
[IO.File]::WriteAllLines((Join-Path $artifactPath 'ComprasAutorizadorPermisosTest.log'), ($permissionsOutput + ('java_exit=' + $permissionsExit)), $latinEncoding)
$permissionsOutput | Write-Output
Write-Output ('permisos_java_exit=' + $permissionsExit)
if ($permissionsExit -ne 0) { throw ('ComprasAutorizadorPermisosTest fallo: ' + $permissionsExit) }
