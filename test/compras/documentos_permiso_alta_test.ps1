param([ValidatePattern('^[A-Za-z0-9_-]+$')][string]$EvidenceName = 'offline', [string]$EvidencePath = '')
$ErrorActionPreference = 'Stop'
Set-Location -LiteralPath 'C:\wsmolineros\ext'
$root = (git rev-parse --show-toplevel).Trim()
$origin = (git remote get-url origin).Trim()
if ($LASTEXITCODE -ne 0 -or $root -ne 'C:/wsmolineros/ext' -or $origin -ne 'https://github.com/JerePrograma/molineros.git') { throw 'Repositorio o ruta incorrectos.' }
$repoPath = 'C:\wsmolineros\ext'
$initialHead = (& git rev-parse HEAD).Trim()
if ($LASTEXITCODE -ne 0) { throw 'No se pudo registrar HEAD inicial.' }
$artifactPath = Join-Path $repoPath ('output\documentos-permiso-alta-qa\' + $EvidenceName)
if ($EvidencePath.Length -gt 0) {
    $artifactPath = [IO.Path]::GetFullPath($EvidencePath)
    if ($artifactPath -eq $repoPath -or $artifactPath.StartsWith($repoPath + '\', [StringComparison]::OrdinalIgnoreCase)) { throw 'La evidencia indicada debe quedar fuera del repositorio.' }
}
$classOutput = Join-Path $artifactPath 'classes'
$latinEncoding = [System.Text.Encoding]::GetEncoding(28591)
New-Item -ItemType Directory -Path $classOutput -Force | Out-Null
# Mismo patron de orden_medica_permiso_test.ps1: Java8 y source/target1.5.
$classpathEntries = @($classOutput, (Join-Path $repoPath 'ext-impl\classes'), (Join-Path $repoPath 'ext-service\classes'), (Join-Path $repoPath 'ext-web\docroot\WEB-INF\classes'))
foreach ($folder in @('lib\global', 'lib\portal', 'lib\development', 'ext-lib\global', 'modules', 'ext-web\docroot\WEB-INF\lib')) {
    $classpathEntries += @(Get-ChildItem -LiteralPath (Join-Path $repoPath $folder) -Filter '*.jar' -ErrorAction SilentlyContinue | Where-Object { $_.Length -gt 10 -and $_.Name -ne 'ecj.jar' } | ForEach-Object { $_.FullName })
}
$classpathEntries += 'C:\apache-tomcat-8.5.23\lib\ecj-4.6.3.jar'
$classpath = $classpathEntries -join ';'
$sources = @(
    'test\compras\fixtures\configuracion\ConfiguracionComprasDatosFixture.java',
    'ext-impl\src\ar\com\ospim\compras\requerimientos\documentos\DocumentoLibraryComprasHelper.java',
    'ext-impl\src\ar\com\ospim\compras\requerimientos\helper\PresupuestoCompraHelper.java',
    'test\compras\ComprasDocumentosPermisoAltaTest.java'
) | ForEach-Object { Join-Path $repoPath $_ }
[System.IO.File]::WriteAllLines((Join-Path $artifactPath 'context.txt'), @(
    ('Inicio UTC: ' + [DateTime]::UtcNow.ToString('yyyy-MM-dd HH:mm:ss')),
    ('HEAD inicial: ' + $initialHead),
    'Estado inicial: cuatro creadores reales de Compras, entradas preparadas y servicios simulados.',
    'Log inicial: proceso Java aislado; no se usa DB, red, Tomcat ni UI.',
    'Alcance: VIEW individual ABM; UPDATE solo comparativa; preservacion, idempotencia y compensacion.'
), $latinEncoding)
$sourcepath = @('ext-impl\src', 'ext-service\src', 'ext-web\docroot\WEB-INF\src') | ForEach-Object { Join-Path $repoPath $_ }
$ErrorActionPreference = 'Continue' # Registrar stderr nativo y comprobar exit code.
$compileOutput = @(& 'C:\Program Files\Java\jdk1.8.0_251\bin\javac.exe' '-encoding' 'ISO-8859-1' '-source' '1.5' '-target' '1.5' '-classpath' $classpath '-sourcepath' ($sourcepath -join ';') '-d' $classOutput @sources 2>&1 | ForEach-Object { $_.ToString() })
$compileExit = $LASTEXITCODE
$ErrorActionPreference = 'Stop'
[System.IO.File]::WriteAllLines((Join-Path $artifactPath 'javac.log'), ($compileOutput + ('javac_exit=' + $compileExit)), $latinEncoding)
if ($compileExit -ne 0) { $compileOutput | Write-Output; throw ('javac fallo: ' + $compileExit) }
$ErrorActionPreference = 'Continue' # Registrar stderr nativo y comprobar exit code.
$runOutput = @(& 'C:\Program Files\Java\jdk1.8.0_251\bin\java.exe' '-Djava.awt.headless=true' '-Dfile.encoding=UTF-8' '-cp' $classpath 'ComprasDocumentosPermisoAltaTest' $artifactPath 2>&1 | ForEach-Object { $_.ToString() })
$runExit = $LASTEXITCODE
$ErrorActionPreference = 'Stop'
[System.IO.File]::WriteAllLines((Join-Path $artifactPath 'report.txt'), ($runOutput + ('java_exit=' + $runExit)), $latinEncoding)
$compileOutput | Write-Output
$runOutput | Write-Output
Write-Output ('javac_exit=' + $compileExit + ' java_exit=' + $runExit)
if ($runExit -ne 0) { throw ('harness fallo: ' + $runExit) }