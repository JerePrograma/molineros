param([ValidatePattern('^[A-Za-z0-9_-]+$')][string]$EvidenceName = 'offline')
$ErrorActionPreference = 'Stop'
Set-Location -LiteralPath 'C:\wsmolineros\ext'
$root = (git rev-parse --show-toplevel).Trim()
$origin = (git remote get-url origin).Trim()
if ($LASTEXITCODE -ne 0 -or $root -ne 'C:/wsmolineros/ext' -or $origin -ne 'https://github.com/JerePrograma/molineros.git') { throw 'Repositorio o ruta incorrectos.' }
$repoPath = 'C:\wsmolineros\ext'
$initialHead = (& git rev-parse HEAD).Trim()
if ($LASTEXITCODE -ne 0) { throw 'No se pudo registrar HEAD inicial.' }
$artifactPath = Join-Path $repoPath ('output\fechas-requerimiento-qa\' + $EvidenceName)
$classOutput = Join-Path $artifactPath 'classes'
$latinEncoding = [System.Text.Encoding]::GetEncoding(28591)
New-Item -ItemType Directory -Path $classOutput -Force | Out-Null
# Referencia: orden_medica_permiso_test.ps1/reclamo_prestacional_navegacion_test.ps1.
# Solo compila en output; usa JDK8 y source/target1.5 del harness legacy existente.
$classpathEntries = @($classOutput, (Join-Path $repoPath 'ext-impl\build\classes'), (Join-Path $repoPath 'ext-impl\classes'), (Join-Path $repoPath 'ext-service\classes'), (Join-Path $repoPath 'ext-web\docroot\WEB-INF\classes'))
foreach ($folder in @('lib\global', 'lib\portal', 'lib\development', 'ext-lib\global', 'modules', 'ext-web\docroot\WEB-INF\lib')) {
    $classpathEntries += @(Get-ChildItem -LiteralPath (Join-Path $repoPath $folder) -Filter '*.jar' -ErrorAction SilentlyContinue | Where-Object { $_.Length -gt 10 -and $_.Name -ne 'ecj.jar' } | ForEach-Object { $_.FullName })
}
$classpathEntries += 'C:\apache-tomcat-8.5.23\lib\ecj-4.6.3.jar'
$classpath = $classpathEntries -join ';'
$sources = @(
    'ext-impl\src\ar\com\ospim\compras\requerimientos\documentos\DocumentoLibraryComprasHelper.java',
    'ext-impl\src\ar\com\ospim\compras\requerimientos\helper\RequerimientoCompraComparativaHelper.java',
    'ext-impl\src\ar\com\ospim\compras\requerimientos\action\EditarRequerimientoCompraAction.java',
    'test\compras\ComprasFechasRequerimientoTest.java'
) | ForEach-Object { Join-Path $repoPath $_ }
[System.IO.File]::WriteAllLines((Join-Path $artifactPath 'context.txt'), @(
    ('Inicio UTC: ' + [DateTime]::UtcNow.ToString('yyyy-MM-dd HH:mm:ss')),
    ('HEAD inicial: ' + $initialHead),
    'Estado inicial: parsers productivos de adjuntos/presupuestos y validaciones publicas; fecha de hoy en timezone JVM.',
    'Log inicial: proceso Java aislado; sin DB, red, Tomcat, UI ni persistencia.',
    'Alcance: ayer/hoy/manana, futuro mismo anio, cambio mes/anio, bisiesto, formato y campos opcionales.'
), $latinEncoding)
# Regresion de orden de efectos: rechazar el adjunto antes de guardar cabecera.
# Check estatico acotado; no demuestra persistencia ni reemplaza la prueba UI.
$actionSource = [System.IO.File]::ReadAllText((Join-Path $repoPath 'ext-impl\src\ar\com\ospim\compras\requerimientos\action\EditarRequerimientoCompraAction.java'), $latinEncoding)
$methodStart = $actionSource.IndexOf('private int guardarCabeceraRequerimiento(')
$methodEnd = $actionSource.IndexOf('private void guardarAdjudicacionInformada(', $methodStart)
if ($methodStart -lt 0 -or $methodEnd -lt 0) { throw 'No se encontro el metodo de guardado de cabecera.' }
$methodSource = $actionSource.Substring($methodStart, $methodEnd - $methodStart)
$editionSource = $methodSource.Substring($methodSource.IndexOf('EDICION'))
$validationIndex = $editionSource.IndexOf('validarOrdenesMedicasDesdeRequest(')
$saveIndex = $editionSource.IndexOf('.guardarRequerimientoCompra(')
$flagIndex = $editionSource.IndexOf('contextoGuardado.persistenciaInvocada = true;')
$attachIndex = $editionSource.IndexOf('.agregarOrdenesMedicasRequerimientoPendiente(')
$orderValid = $validationIndex -ge 0 -and $validationIndex -lt $flagIndex -and $flagIndex -lt $saveIndex -and $saveIndex -lt $attachIndex
[System.IO.File]::WriteAllLines((Join-Path $artifactPath 'orden-validaciones.log'), @(
    ('PREVALIDACION_ANTES_GUARDADO_EDICION=' + $orderValid),
    'Check estatico del orden de llamadas; no usa DB ni ejecuta el Action.'
), $latinEncoding)
if (-not $orderValid) { throw 'La edicion debe validar los adjuntos antes de guardar la cabecera.' }
Write-Output 'OK PREVALIDACION_ANTES_GUARDADO_EDICION (estatico)'
$sourcepath = @('ext-impl\src', 'ext-service\src', 'ext-web\docroot\WEB-INF\src') | ForEach-Object { Join-Path $repoPath $_ }
$compileOutput = @(& 'C:\Program Files\Java\jdk1.8.0_251\bin\javac.exe' '-encoding' 'ISO-8859-1' '-source' '1.5' '-target' '1.5' '-classpath' $classpath '-sourcepath' ($sourcepath -join ';') '-d' $classOutput @sources 2>&1 | ForEach-Object { $_.ToString() })
$compileExit = $LASTEXITCODE
[System.IO.File]::WriteAllLines((Join-Path $artifactPath 'javac.log'), ($compileOutput + ('javac_exit=' + $compileExit)), $latinEncoding)
if ($compileExit -ne 0) { $compileOutput | Write-Output; throw ('javac fallo: ' + $compileExit) }
$runOutput = @(& 'C:\Program Files\Java\jdk1.8.0_251\bin\java.exe' '-Djava.awt.headless=true' '-cp' $classpath 'ComprasFechasRequerimientoTest' 2>&1 | ForEach-Object { $_.ToString() })
$runExit = $LASTEXITCODE
[System.IO.File]::WriteAllLines((Join-Path $artifactPath 'report.txt'), ($runOutput + ('java_exit=' + $runExit)), $latinEncoding)
$runOutput | Write-Output
Write-Output ('javac_exit=' + $compileExit + ' java_exit=' + $runExit)
if ($runExit -ne 0) { throw ('harness fallo: ' + $runExit) }