param([string]$EvidenceName = 'offline')
$ErrorActionPreference = 'Stop'
$repoPath = [System.IO.Path]::GetFullPath((Join-Path $PSScriptRoot '..\..'))
$artifactPath = Join-Path $repoPath ('output\autorizador-permisos-qa\' + $EvidenceName)
$classOutput = Join-Path $artifactPath 'classes'
$latinEncoding = [System.Text.Encoding]::GetEncoding(28591)
$initialHead = (& git -C $repoPath rev-parse HEAD).Trim()
if ($LASTEXITCODE -ne 0) { throw 'No se pudo registrar HEAD inicial.' }
New-Item -ItemType Directory -Path $classOutput -Force | Out-Null
$classpathEntries = @($classOutput, (Join-Path $repoPath 'ext-impl\classes'), (Join-Path $repoPath 'ext-service\classes'), (Join-Path $repoPath 'ext-web\docroot\WEB-INF\classes'))
foreach ($folder in @('lib\global', 'lib\portal', 'lib\development', 'ext-lib\global', 'modules', 'ext-web\docroot\WEB-INF\lib')) {
    $classpathEntries += @(Get-ChildItem -LiteralPath (Join-Path $repoPath $folder) -Filter '*.jar' -ErrorAction SilentlyContinue | Where-Object { $_.Length -gt 10 -and $_.Name -ne 'ecj.jar' } | ForEach-Object { $_.FullName })
}
$classpath = $classpathEntries -join ';'
$sources = @(
    'ext-impl\src\ar\com\ospim\compras\WebKeysCompras.java',
    'ext-impl\src\ar\com\ospim\compras\requerimientos\beans\RequerimientoCompra.java',
    'ext-impl\src\ar\com\ospim\compras\requerimientos\beans\RequerimientoCompraEstado.java',
    'test\compras\ComprasAutorizadorPermisosTest.java'
) | ForEach-Object { Join-Path $repoPath $_ }
$sourcepath = Join-Path $repoPath 'ext-impl\src'
[System.IO.File]::WriteAllLines((Join-Path $artifactPath 'context.txt'), @(
    ('Inicio local: ' + (Get-Date -Format o)),
    ('HEAD inicial: ' + $initialHead),
    'Estado inicial: observacion interna rechazada en ANULADO; transicion a AUTORIZADO desde PARA AUTORIZAR.',
    'Log inicial: no hay directorio de logs Tomcat local. Sin base ni UI autenticada.',
    'Prueba: clases Java productivas con Java 8 y reglas de estado.'
), $latinEncoding)
$compileOutput = @(& 'C:\Program Files\Java\jdk1.8.0_251\bin\javac.exe' '-encoding' 'ISO-8859-1' '-source' '1.5' '-target' '1.5' '-classpath' $classpath '-sourcepath' $sourcepath '-d' $classOutput @sources 2>&1 | ForEach-Object { $_.ToString() })
$compileExit = $LASTEXITCODE
[System.IO.File]::WriteAllLines((Join-Path $artifactPath 'javac.log'), ($compileOutput + ('javac_exit=' + $compileExit)), $latinEncoding)
if ($compileExit -ne 0) { $compileOutput | Write-Output; throw ('javac fallo: ' + $compileExit) }
$runOutput = @(& 'C:\Program Files\Java\jdk1.8.0_251\bin\java.exe' '-cp' $classpath 'ComprasAutorizadorPermisosTest' 2>&1 | ForEach-Object { $_.ToString() })
$runExit = $LASTEXITCODE
[System.IO.File]::WriteAllLines((Join-Path $artifactPath 'report.txt'), ($runOutput + ('java_exit=' + $runExit)), $latinEncoding)
$runOutput | Write-Output
Write-Output ('javac_exit=' + $compileExit + ' java_exit=' + $runExit)
if ($runExit -ne 0) { throw ('test fallo: ' + $runExit) }
