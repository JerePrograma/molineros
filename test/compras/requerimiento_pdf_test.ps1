param([string]$EvidenceName = 'offline', [string]$JasperPath = '', [string]$CompileTemplate = '', [string]$CompiledReport = '', [string]$ReferenceJasperPath = '')
$ErrorActionPreference = 'Stop'
$repoPath = [IO.Path]::GetFullPath((Join-Path $PSScriptRoot '..\..'))
$artifactPath = Join-Path $repoPath ('output\requerimiento-pdf-qa\' + $EvidenceName)
$classOutput = Join-Path $artifactPath 'classes'
$latinEncoding = [Text.Encoding]::GetEncoding(28591)
$initialHead = (& git -C $repoPath rev-parse HEAD).Trim()
if ($LASTEXITCODE -ne 0) { throw 'No se pudo registrar HEAD inicial.' }
New-Item -ItemType Directory -Path $classOutput -Force | Out-Null
if ($JasperPath.Length -eq 0) { $JasperPath = Join-Path $repoPath 'ext-web\docroot\WEB-INF\classes\jasper\compras\requerimiento_compra.jasper' }
# Adaptado del harness orden_compra_test.ps1; Java8 y source/target legacy.
$classpathEntries = @($classOutput)
foreach ($folder in @('lib\global', 'lib\portal', 'lib\development', 'ext-lib\global', 'modules', 'ext-web\docroot\WEB-INF\lib')) {
    $classpathEntries += @(Get-ChildItem -LiteralPath (Join-Path $repoPath $folder) -Filter '*.jar' -ErrorAction SilentlyContinue | Where-Object { $_.Length -gt 10 -and $_.Name -ne 'ecj.jar' } | ForEach-Object { $_.FullName })
}
$classpathEntries += 'C:\apache-tomcat-8.5.23\lib\ecj-4.6.3.jar'
$classpath = $classpathEntries -join ';'
[IO.File]::WriteAllLines((Join-Path $artifactPath 'context.txt'), @(
    ('Inicio Argentina: ' + [TimeZoneInfo]::ConvertTimeBySystemTimeZoneId([DateTime]::UtcNow, 'Argentina Standard Time').ToString('yyyy-MM-dd HH:mm:ss')),
    ('HEAD inicial: ' + $initialHead),
    'Estado inicial: PDF omite la empresa adjudicada del requerimiento de Sistemas.',
    'Log inicial: no disponible; no existe carpeta de logs de Tomcat y 8080 sin listener al inicio de la tarea.',
    ('Jasper real probado: ' + $JasperPath),
    'Pruebas: fixtures de Sistemas, sin empresa, fiscal diferido, denominacion larga, Medica, RRHH y multipagina.'
), $latinEncoding)
$compileOutput = @(& 'C:\Program Files\Java\jdk1.8.0_251\bin\javac.exe' '-encoding' 'ISO-8859-1' '-source' '1.5' '-target' '1.5' '-classpath' $classpath '-d' $classOutput (Join-Path $repoPath 'test\compras\ComprasRequerimientoPdfTest.java') 2>&1 | ForEach-Object { $_.ToString() })
$compileExit = $LASTEXITCODE
[IO.File]::WriteAllLines((Join-Path $artifactPath 'javac.log'), ($compileOutput + ('javac_exit=' + $compileExit)), $latinEncoding)
if ($compileExit -ne 0) { $compileOutput | Write-Output; throw ('javac fallo: ' + $compileExit) }
if ($CompileTemplate.Length -gt 0) {
    if ($CompiledReport.Length -eq 0) { $CompiledReport = [IO.Path]::ChangeExtension($CompileTemplate, '.jasper') }
    $runArgs = @('--compile', $CompileTemplate, $CompiledReport)
} else {
    $runArgs = @($JasperPath, $artifactPath)
    if ($ReferenceJasperPath.Length -gt 0) { $runArgs += $ReferenceJasperPath }
}
$runOutput = @(& 'C:\Program Files\Java\jdk1.8.0_251\bin\java.exe' '-Djava.awt.headless=true' '-cp' $classpath 'ComprasRequerimientoPdfTest' @runArgs 2>&1 | ForEach-Object { $_.ToString() })
$runExit = $LASTEXITCODE
[IO.File]::WriteAllLines((Join-Path $artifactPath 'ComprasRequerimientoPdfTest.log'), ($runOutput + ('java_exit=' + $runExit)), $latinEncoding)
$runOutput | Write-Output
if ($runExit -ne 0) { throw ('ComprasRequerimientoPdfTest fallo: ' + $runExit) }
Write-Output ('javac_exit=' + $compileExit + ' java_exit=' + $runExit)