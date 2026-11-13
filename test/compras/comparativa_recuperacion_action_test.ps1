param([ValidatePattern('^[A-Za-z0-9_-]+$')][string]$EvidenceName = 'offline')
$ErrorActionPreference = 'Stop'
$repoPath = [IO.Path]::GetFullPath((Join-Path $PSScriptRoot '..\..'))
Set-Location -LiteralPath $repoPath
$root = (git rev-parse --show-toplevel).Trim()
$origin = (git remote get-url origin).Trim()
if ($LASTEXITCODE -ne 0 -or $root -ne 'C:/wsmolineros/ext' -or $origin -ne 'https://github.com/JerePrograma/molineros.git') { throw 'Repositorio o ruta incorrectos.' }
$artifactPath = Join-Path $repoPath ('output\comparativa-recuperacion-action-qa\' + $EvidenceName)
$classOutput = Join-Path $artifactPath 'classes'
New-Item -ItemType Directory -Path $classOutput -Force | Out-Null
$latin = [Text.Encoding]::GetEncoding(28591)
[IO.File]::WriteAllLines((Join-Path $artifactPath 'context.txt'), @(
    ('Inicio Argentina: ' + [DateTimeOffset]::UtcNow.ToOffset([TimeSpan]::FromHours(-3)).ToString('o')),
    ('HEAD inicial: ' + (& git rev-parse HEAD).Trim()),
    'Estado inicial: reproduccion de huerfanos confirmada por el harness de recuperacion y bajas cotizadas.',
    'Log inicial: sin DB, red, Tomcat ni UI; proceso Java aislado.',
    'Referencia: ComprasOrdenSeguimientoActionTest y orden_seguimiento_test.ps1; permisos y token reales.',
    'Alcance: reparar comparativa; contexto, actor, confirmacion, roles configurados, nonce y errores.'
), $latin)
$classpathEntries = @($classOutput, (Join-Path $repoPath 'ext-impl\classes'), (Join-Path $repoPath 'ext-service\classes'), (Join-Path $repoPath 'ext-web\docroot\WEB-INF\classes'))
foreach ($folder in @('lib\global','lib\portal','lib\development','ext-lib\global','modules','ext-web\docroot\WEB-INF\lib')) {
    $classpathEntries += @(Get-ChildItem -LiteralPath (Join-Path $repoPath $folder) -Filter '*.jar' -ErrorAction SilentlyContinue | Where-Object { $_.Length -gt 10 -and $_.Name -ne 'ecj.jar' } | ForEach-Object { $_.FullName })
}
$classpath = $classpathEntries -join ';'
$sources = @(
    'ext-impl\src\ar\com\ospim\compras\requerimientos\action\RequerimientoCompraComparativaAction.java',
    'ext-impl\src\ar\com\ospim\compras\requerimientos\action\RequerimientoCompraGuardadoToken.java',
    'ext-impl\src\ar\com\ospim\compras\requerimientos\helper\RequerimientoCompraComparativaHelper.java',
    'test\compras\fixtures\datos\ComprasFuenteDatosFixture.java',
    'test\compras\ComprasComparativaRecuperacionActionTest.java'
) | ForEach-Object { Join-Path $repoPath $_ }
$sourcepath = @('ext-impl\src','ext-service\src','ext-web\docroot\WEB-INF\src') | ForEach-Object { Join-Path $repoPath $_ }
$ErrorActionPreference = 'Continue'
$compiled = @(& 'C:\Program Files\Java\jdk1.8.0_251\bin\javac.exe' '-encoding' 'ISO-8859-1' '-source' '1.5' '-target' '1.5' '-classpath' $classpath '-sourcepath' ($sourcepath -join ';') '-d' $classOutput @sources 2>&1 | ForEach-Object { $_.ToString() })
$compileExit = $LASTEXITCODE
$ErrorActionPreference = 'Stop'
[IO.File]::WriteAllLines((Join-Path $artifactPath 'javac.log'), ($compiled + ('javac_exit=' + $compileExit)), $latin)
if ($compileExit -ne 0) { $compiled | Write-Output; throw ('javac fallo: ' + $compileExit) }
$ErrorActionPreference = 'Continue'
$result = @(& 'C:\Program Files\Java\jdk1.8.0_251\bin\java.exe' '-Djava.awt.headless=true' '-cp' $classpath 'ComprasComparativaRecuperacionActionTest' 2>&1 | ForEach-Object { $_.ToString() })
$runExit = $LASTEXITCODE
$ErrorActionPreference = 'Stop'
[IO.File]::WriteAllLines((Join-Path $artifactPath 'ComprasComparativaRecuperacionActionTest.log'), ($result + ('java_exit=' + $runExit)), $latin)
$result | Write-Output
Write-Output ('javac_exit=' + $compileExit + ' java_exit=' + $runExit)
if ($runExit -ne 0) { throw ('ComprasComparativaRecuperacionActionTest fallo: ' + $runExit) }
