param([ValidatePattern('^[A-Za-z0-9_-]+$')][string]$EvidenceName = 'offline', [switch]$Baseline)
$ErrorActionPreference = 'Stop'
Set-Location -LiteralPath 'C:\wsmolineros\ext'
$root = (git rev-parse --show-toplevel).Trim()
$origin = (git remote get-url origin).Trim()
if ($LASTEXITCODE -ne 0 -or $root -ne 'C:/wsmolineros/ext' -or $origin -ne 'https://github.com/JerePrograma/molineros.git') { throw 'Repositorio o ruta incorrectos.' }
$repoPath = 'C:\wsmolineros\ext'
$initialHead = (& git rev-parse HEAD).Trim()
if ($LASTEXITCODE -ne 0) { throw 'No se pudo registrar HEAD inicial.' }
$artifactPath = Join-Path $repoPath ('output\comparativa-recuperacion-qa\' + $EvidenceName)
$classOutput = Join-Path $artifactPath 'classes'
$latinEncoding = [Text.Encoding]::GetEncoding(28591)
New-Item -ItemType Directory -Path $classOutput -Force | Out-Null
# Referencia: detalle_snapshot_edicion_test.ps1; JDK8/source-target1.5 y classpath legacy.
$classpathEntries = @($classOutput, (Join-Path $repoPath 'ext-impl\build\classes'), (Join-Path $repoPath 'ext-impl\classes'), (Join-Path $repoPath 'ext-service\classes'), (Join-Path $repoPath 'ext-web\docroot\WEB-INF\classes'))
foreach ($folder in @('lib\global','lib\portal','lib\development','ext-lib\global','modules','ext-web\docroot\WEB-INF\lib')) {
    $classpathEntries += @(Get-ChildItem -LiteralPath (Join-Path $repoPath $folder) -Filter '*.jar' -ErrorAction SilentlyContinue | Where-Object { $_.Length -gt 10 -and $_.Name -ne 'ecj.jar' } | ForEach-Object { $_.FullName })
}
$classpath = $classpathEntries -join ';'
$sources = @(
    'ext-impl\src\ar\com\ospim\compras\requerimientos\beans\RequerimientoCompraComparativa.java',
    'ext-impl\src\ar\com\ospim\compras\requerimientos\beans\RequerimientoCompraComparativaDetalle.java',
    'ext-impl\src\ar\com\ospim\compras\requerimientos\helper\RequerimientoCompraComparativaHelper.java',
    'test\compras\ComprasComparativaRecuperacionTest.java'
) | ForEach-Object { Join-Path $repoPath $_ }
[IO.File]::WriteAllLines((Join-Path $artifactPath 'context.txt'), @(
    ('Inicio UTC: ' + [DateTime]::UtcNow.ToString('yyyy-MM-dd HH:mm:ss')),
    ('HEAD inicial: ' + $initialHead),
    'Estado inicial: una prestacion guardada fuera del requerimiento impide cargar la comparativa.',
    'Log inicial: proceso Java offline sin DB, red, Tomcat ni UI.',
    'Alcance: lectura estricta y recuperacion explicita de comparativa; preservar precios y pertenencia.'
), $latinEncoding)
$sourcepath = @('ext-impl\src','ext-service\src','ext-web\docroot\WEB-INF\src') | ForEach-Object { Join-Path $repoPath $_ }
$ErrorActionPreference = 'Continue'
$compiled = @(& 'C:\Program Files\Java\jdk1.8.0_251\bin\javac.exe' '-encoding' 'ISO-8859-1' '-source' '1.5' '-target' '1.5' '-classpath' $classpath '-sourcepath' ($sourcepath -join ';') '-d' $classOutput @sources 2>&1 | ForEach-Object { $_.ToString() })
$compileExit = $LASTEXITCODE
$ErrorActionPreference = 'Stop'
[IO.File]::WriteAllLines((Join-Path $artifactPath 'javac.log'), ($compiled + ('javac_exit=' + $compileExit)), $latinEncoding)
if ($compileExit -ne 0) { $compiled | Write-Output; throw ('javac fallo: ' + $compileExit) }
$testArgs = @('-Djava.awt.headless=true','-cp',$classpath,'ComprasComparativaRecuperacionTest')
if ($Baseline) { $testArgs += 'baseline' }
$ErrorActionPreference = 'Continue'
$result = @(& 'C:\Program Files\Java\jdk1.8.0_251\bin\java.exe' @testArgs 2>&1 | ForEach-Object { $_.ToString() })
$runExit = $LASTEXITCODE
$ErrorActionPreference = 'Stop'
[IO.File]::WriteAllLines((Join-Path $artifactPath 'report.txt'), ($result + ('java_exit=' + $runExit)), $latinEncoding)
$result | Write-Output
if ($runExit -ne 0) { throw ('ComprasComparativaRecuperacionTest fallo: ' + $runExit) }
Write-Output ('javac_exit=' + $compileExit + ' java_exit=' + $runExit)
