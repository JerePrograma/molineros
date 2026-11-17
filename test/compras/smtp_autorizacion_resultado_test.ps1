param([string]$EvidenceName = 'smtp-offline')
$ErrorActionPreference = 'Stop'
$repoPath = [IO.Path]::GetFullPath((Join-Path $PSScriptRoot '..\..'))
Set-Location -LiteralPath $repoPath
$root = (git rev-parse --show-toplevel).Trim()
$origin = (git remote get-url origin).Trim()
if ($LASTEXITCODE -ne 0 -or $root -ne 'C:/wsmolineros/ext' -or $origin -ne 'https://github.com/JerePrograma/molineros.git') { throw 'Repositorio o ruta incorrectos.' }
$artifactPath = Join-Path $repoPath ('output\autorizacion-email-qa\' + $EvidenceName)
$classOutput = Join-Path $artifactPath 'classes'
$latinEncoding = [Text.Encoding]::GetEncoding(28591)
New-Item -ItemType Directory -Path $classOutput -Force | Out-Null
New-Item -ItemType Directory -Path (Join-Path $classOutput 'META-INF') -Force | Out-Null
# Patron autorizacion_email_test.ps1: dependencias y source/target legacy; fixture aislada primero.
$classpathEntries = @($classOutput, (Join-Path $repoPath 'ext-impl\classes'), (Join-Path $repoPath 'ext-service\classes'), (Join-Path $repoPath 'ext-web\docroot\WEB-INF\classes'))
foreach ($folder in @('lib\global', 'lib\portal', 'lib\development', 'ext-lib\global', 'modules', 'ext-web\docroot\WEB-INF\lib')) {
    $classpathEntries += @(Get-ChildItem -LiteralPath (Join-Path $repoPath $folder) -Filter '*.jar' -ErrorAction SilentlyContinue | Where-Object { $_.Length -gt 10 -and $_.Name -ne 'ecj.jar' } | ForEach-Object { $_.FullName })
}
$classpath = $classpathEntries -join ';'
$sources = @(
    'test\compras\fixtures\smtp\ReportesServiceUtil.java',
    'ext-impl\src\ar\com\ospim\compras\requerimientos\helper\CotizacionPrestadorMailHelper.java',
    'test\compras\ComprasAutorizacionSmtpResultadoTest.java',
    'test\compras\ComprasAutorizacionSmtpTransporteTest.java'
) | ForEach-Object { Join-Path $repoPath $_ }
[IO.File]::WriteAllLines((Join-Path $artifactPath 'context.txt'), @(
    ('Inicio Argentina: ' + [TimeZoneInfo]::ConvertTimeBySystemTimeZoneId([DateTime]::UtcNow, 'Argentina Standard Time').ToString('yyyy-MM-dd HH:mm:ss')),
    ('HEAD inicial: ' + (git rev-parse HEAD)),
    'Estado funcional inicial: SMTP instalado no neutralizado; este runner JAMAS lo acciona.',
    'Prueba: clasificador con excepciones JavaMail reales y helper/MIME/sendMessage/catch reales con Provider captura sintetico.',
    'Log inicial de backend: no usado por fixture offline; JVM aislada, sin Tomcat ni DB.',
    'Fronteras: ReportesServiceUtil fixture sintetica; Provider no usa sockets; SecurityManager bloquea red solo dentro de JVM prueba.',
    'No demuestra protocolo SMTP, STARTTLS real, JDBC ni UI.'
), $latinEncoding)
[IO.File]::WriteAllBytes((Join-Path $classOutput 'META-INF\javamail.providers'), [IO.File]::ReadAllBytes((Join-Path $repoPath 'test\compras\fixtures\smtp\javamail.providers')))
$ErrorActionPreference = 'Continue'
$compileOutput = @(& 'C:\Program Files\Java\jdk1.8.0_251\bin\javac.exe' '-encoding' 'ISO-8859-1' '-source' '1.5' '-target' '1.5' '-classpath' $classpath '-sourcepath' (Join-Path $repoPath 'test\compras\fixtures\smtp') '-d' $classOutput @sources 2>&1 | ForEach-Object { $_.ToString() })
$compileExit = $LASTEXITCODE
$ErrorActionPreference = 'Stop'
[IO.File]::WriteAllLines((Join-Path $artifactPath 'javac.log'), ($compileOutput + ('javac_exit=' + $compileExit)), $latinEncoding)
if ($compileExit -ne 0) { $compileOutput | Write-Output; throw ('javac fallo: ' + $compileExit) }
foreach ($testName in @('ComprasAutorizacionSmtpResultadoTest', 'ComprasAutorizacionSmtpTransporteTest')) {
    $ErrorActionPreference = 'Continue'
    $runOutput = @(& 'C:\Program Files\Java\jdk1.8.0_251\bin\java.exe' '-Djava.awt.headless=true' '-cp' $classpath $testName 2>&1 | ForEach-Object { $_.ToString() })
    $runExit = $LASTEXITCODE
    $ErrorActionPreference = 'Stop'
    [IO.File]::WriteAllLines((Join-Path $artifactPath ($testName + '.log')), ($runOutput + ('java_exit=' + $runExit)), $latinEncoding)
    $runOutput | Write-Output
    Write-Output ($testName + '_exit=' + $runExit)
    if ($runExit -ne 0) { throw ($testName + ' fallo: ' + $runExit) }
}
