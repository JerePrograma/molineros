param([string]$EvidenceName = 'offline')
$ErrorActionPreference = 'Stop'
$repoPath = [IO.Path]::GetFullPath((Join-Path $PSScriptRoot '..\..'))
$artifactPath = Join-Path $repoPath ('output\comparativa-sincronizacion-qa\' + $EvidenceName)
$classOutput = Join-Path $artifactPath 'classes'
$latinEncoding = [Text.Encoding]::GetEncoding(28591)
$initialHead = (& git -C $repoPath rev-parse HEAD).Trim()
if ($LASTEXITCODE -ne 0) { throw 'No se pudo registrar HEAD inicial.' }
New-Item -ItemType Directory -Path $classOutput -Force | Out-Null
# Adaptado de orden_compra_test.ps1: Java 8, source/target 1.5 y mismas dependencias legacy.
$classpathEntries = @($classOutput, (Join-Path $repoPath 'ext-impl\classes'), (Join-Path $repoPath 'ext-service\classes'), (Join-Path $repoPath 'ext-web\docroot\WEB-INF\classes'))
foreach ($folder in @('lib\global', 'lib\portal', 'lib\development', 'ext-lib\global', 'modules', 'ext-web\docroot\WEB-INF\lib')) {
    $classpathEntries += @(Get-ChildItem -LiteralPath (Join-Path $repoPath $folder) -Filter '*.jar' -ErrorAction SilentlyContinue | Where-Object { $_.Length -gt 10 -and $_.Name -ne 'ecj.jar' } | ForEach-Object { $_.FullName })
}
$classpathEntries += 'C:\apache-tomcat-8.5.23\lib\ecj-4.6.3.jar'
$classpath = $classpathEntries -join ';'
$sources = @(
    'ext-impl\src\ar\com\ospim\compras\requerimientos\documentos\DocumentoLibraryComprasHelper.java',
    'ext-impl\src\ar\com\ospim\compras\requerimientos\helper\RequerimientoCompraReclamoPrestacionalHelper.java',
    'ext-impl\src\ar\com\ospim\servlets\PdfServlet.java',
    'test\compras\ComprasComparativaSincronizacionTest.java'
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
    'Estado inicial: imprimir comparativa genera un PDF nuevo sin renovar el adjunto ya persistido del RP.',
    ('Log inicial: ' + $initialLog),
    'Pruebas: plantilla Jasper real, cantidades erroneas/corregidas, metadata distinta, importes/impuestos, multipagina, DL con fixtures, permisos e idempotencia. Sin DB externa.'
), $latinEncoding)
$sourcepath = @('ext-impl\src', 'ext-service\src', 'ext-web\docroot\WEB-INF\src') | ForEach-Object { Join-Path $repoPath $_ }
$compileOutput = @(& 'C:\Program Files\Java\jdk1.8.0_251\bin\javac.exe' '-encoding' 'ISO-8859-1' '-source' '1.5' '-target' '1.5' '-classpath' $classpath '-sourcepath' ($sourcepath -join ';') '-d' $classOutput @sources 2>&1 | ForEach-Object { $_.ToString() })
$compileExit = $LASTEXITCODE
[IO.File]::WriteAllLines((Join-Path $artifactPath 'javac.log'), ($compileOutput + ('javac_exit=' + $compileExit)), $latinEncoding)
if ($compileExit -ne 0) { $compileOutput | Write-Output; throw ('javac fallo: ' + $compileExit) }
$runOutput = @(& 'C:\Program Files\Java\jdk1.8.0_251\bin\java.exe' '-Djava.awt.headless=true' '-cp' $classpath 'ComprasComparativaSincronizacionTest' $artifactPath 2>&1 | ForEach-Object { $_.ToString() })
$runExit = $LASTEXITCODE
[IO.File]::WriteAllLines((Join-Path $artifactPath 'ComprasComparativaSincronizacionTest.log'), ($runOutput + ('java_exit=' + $runExit)), $latinEncoding)
$runOutput | Write-Output
if ($runExit -ne 0) { throw ('ComprasComparativaSincronizacionTest fallo: ' + $runExit) }
Write-Output ('javac_exit=' + $compileExit + ' java_exit=' + $runExit)
