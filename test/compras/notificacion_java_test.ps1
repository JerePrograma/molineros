param([string]$EvidencePath = '')
$ErrorActionPreference = 'Stop'
$repoPath = [IO.Path]::GetFullPath((Join-Path $PSScriptRoot '..\..'))
Set-Location -LiteralPath $repoPath
$root = (git rev-parse --show-toplevel).Trim()
$origin = (git remote get-url origin).Trim()
if ($LASTEXITCODE -ne 0 -or $root -ne 'C:/wsmolineros/ext' -or $origin -ne 'https://github.com/JerePrograma/molineros.git') { throw 'Repositorio o ruta incorrectos.' }
$initialHead = (& git rev-parse HEAD).Trim()
if ($EvidencePath -eq '') { $EvidencePath = Join-Path $repoPath '.codex\artifacts\compras-notificacion-java-20261030' }
$classOutput = Join-Path $EvidencePath 'classes'
New-Item -ItemType Directory -Path $classOutput -Force | Out-Null
$latin = [Text.Encoding]::GetEncoding(28591)
$classpathEntries = @($classOutput, (Join-Path $repoPath 'ext-impl\classes'), (Join-Path $repoPath 'ext-service\classes'), (Join-Path $repoPath 'ext-web\docroot\WEB-INF\classes'))
foreach ($folder in @('lib\global', 'lib\portal', 'lib\development', 'ext-lib\global', 'modules', 'ext-web\docroot\WEB-INF\lib')) {
    $classpathEntries += @(Get-ChildItem -LiteralPath (Join-Path $repoPath $folder) -Filter '*.jar' -ErrorAction SilentlyContinue | Where-Object { $_.Length -gt 10 -and $_.Name -ne 'ecj.jar' } | ForEach-Object { $_.FullName })
}
$classpath = $classpathEntries -join ';'
$fixturePath = Join-Path $PSScriptRoot 'fixtures\notificacion-java'
$sources = @(
    (Join-Path $fixturePath 'ar\com\ospim\util\ConnectionHelper.java'),
    (Join-Path $fixturePath 'ar\com\ospim\compras\requerimientos\service\BusquedaRequerimientoCompraServiceUtil.java'),
    (Join-Path $repoPath 'ext-impl\src\ar\com\ospim\compras\requerimientos\beans\PrestadorCotizacion.java'),
    (Join-Path $repoPath 'ext-impl\src\ar\com\ospim\compras\requerimientos\beans\ReservaCotizacionPrestador.java'),
    (Join-Path $repoPath 'ext-impl\src\ar\com\ospim\compras\requerimientos\service\NotificarCotizacionPrestadorServiceImpl.java'),
    (Join-Path $repoPath 'ext-impl\src\ar\com\ospim\compras\requerimientos\service\NotificarCotizacionPrestadorServiceUtil.java'),
    (Join-Path $repoPath 'ext-impl\src\ar\com\ospim\compras\requerimientos\helper\ConfiguracionComprasHelper.java'),
    (Join-Path $repoPath 'ext-impl\src\ar\com\ospim\compras\requerimientos\helper\NotificarCotizacionPrestadorHelper.java'),
    (Join-Path $PSScriptRoot 'ComprasNotificacionJavaTest.java')
)
[IO.File]::WriteAllLines((Join-Path $EvidencePath 'context.txt'), @(
    ('Inicio UTC: ' + [DateTime]::UtcNow.ToString('yyyy-MM-dd HH:mm:ss')),
    ('Inicio Argentina: ' + [TimeZoneInfo]::ConvertTimeBySystemTimeZoneId([DateTime]::UtcNow, 'Argentina Standard Time').ToString('yyyy-MM-dd HH:mm:ss')),
    ('HEAD: ' + $initialHead),
    'Estado inicial: reserva/finalizacion/pedido dependian de decisiones SQL; prechecks Java fuera de TX.',
    'Log inicial: fixture JDBC de carreras concurrentes; sin Tomcat, red, DB, SMTP, DL ni UI reales.',
    'Adaptado de primera_carga_test.ps1: Java8, source/target1.5 y clases reales de persistencia.'
), $latin)
$compileOutput = @(& 'C:\Program Files\Java\jdk1.8.0_251\bin\javac.exe' '-encoding' 'ISO-8859-1' '-source' '1.5' '-target' '1.5' '-classpath' $classpath '-sourcepath' $fixturePath '-d' $classOutput @sources 2>&1 | ForEach-Object { $_.ToString() })
$compileExit = $LASTEXITCODE
[IO.File]::WriteAllLines((Join-Path $EvidencePath 'javac.log'), ($compileOutput + ('javac_exit=' + $compileExit)), $latin)
if ($compileExit -ne 0) { $compileOutput | Write-Output; throw ('javac fallo: ' + $compileExit) }
$runOutput = @(& 'C:\Program Files\Java\jdk1.8.0_251\bin\java.exe' '-cp' $classpath 'ComprasNotificacionJavaTest' 2>&1 | ForEach-Object { $_.ToString() })
$runExit = $LASTEXITCODE
[IO.File]::WriteAllLines((Join-Path $EvidencePath 'ComprasNotificacionJavaTest.log'), ($runOutput + ('java_exit=' + $runExit)), $latin)
$runOutput | Write-Output
Write-Output ('javac_exit=' + $compileExit + ' java_exit=' + $runExit)
if ($runExit -ne 0) { throw ('Prueba JDBC fallo: ' + $runExit) }
