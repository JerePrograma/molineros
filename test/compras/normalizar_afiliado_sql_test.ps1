param(
    [Parameter(Mandatory=$true)][ValidatePattern('^compras_afiliado_normalizacion_test_20261102_[a-z0-9]{6}$')][string]$Database,
    [Parameter(Mandatory=$true)][string]$MetadataPath,
    [Parameter(Mandatory=$true)][ValidatePattern('^[a-fA-F0-9]{64}$')][string]$MetadataSha256,
    [Parameter(Mandatory=$true)][string]$AuxiliaryMetadataPath,
    [Parameter(Mandatory=$true)][ValidatePattern('^[a-fA-F0-9]{64}$')][string]$AuxiliarySha256,
    [Parameter(Mandatory=$true)][ValidatePattern('^[a-fA-F0-9]{64}$')][string]$ForwardSha256,
    [Parameter(Mandatory=$true)][ValidatePattern('^[a-fA-F0-9]{64}$')][string]$ReverseSha256,
    [Parameter(Mandatory=$true)][string]$ReceiptPath,
    [switch]$ResumeFixture
)
$ErrorActionPreference = 'Stop'
Set-Location -LiteralPath 'C:\wsmolineros\ext'
$repoRoot = (git rev-parse --show-toplevel).Trim()
$repoOrigin = (git remote get-url origin).Trim()
if ($LASTEXITCODE -ne 0 -or $repoRoot -ne 'C:/wsmolineros/ext' -or $repoOrigin -ne 'https://github.com/JerePrograma/molineros.git') { throw 'Repositorio o ruta incorrectos.' }
$initialHead = (git rev-parse HEAD).Trim()
if ($LASTEXITCODE -ne 0) { throw 'No se pudo registrar HEAD inicial.' }
if ((git branch --show-current).Trim() -ne 'main' -or $LASTEXITCODE -ne 0) { throw 'La rama debe ser main.' }
if ([string]::IsNullOrEmpty($env:PGPASSWORD)) { throw 'PGPASSWORD debe estar preparado en el entorno del proceso; no pasarlo por argumentos ni guardarlo.' }

# Referencias reales: afiliado_referencias_sql_test.ps1 y auditoria_secuencia_test.py.
# El Python conserva la fixture sintetica y adapta solo el literal del nombre de DB.
$repoPath = 'C:\wsmolineros\ext'
$latinEncoding = [System.Text.Encoding]::GetEncoding(28591)
$metadataFullPath = (Resolve-Path -LiteralPath $MetadataPath).Path
$auxiliaryFullPath = (Resolve-Path -LiteralPath $AuxiliaryMetadataPath).Path
$forwardPath = Join-Path $repoPath 'docs\sql\compras\20261102_normalizar_afiliado_requerimiento.sql'
$reversePath = Join-Path $repoPath 'docs\sql\compras\20261102_normalizar_afiliado_requerimiento_reversion.sql'
$testPath = Join-Path $repoPath 'test\compras\normalizar_afiliado_sql_test.py'
$receiptFullPath = [System.IO.Path]::GetFullPath($ReceiptPath)
$contextPath = $receiptFullPath + '.context.txt'
if ((Test-Path -LiteralPath $receiptFullPath) -or (Test-Path -LiteralPath $contextPath)) { throw 'El recibo o contexto ya existe; usar otra ruta para conservar la evidencia anterior.' }
$expectedMetadata = $MetadataSha256.ToLowerInvariant()
$expectedAuxiliary = $AuxiliarySha256.ToLowerInvariant()
$expectedForward = $ForwardSha256.ToLowerInvariant()
$expectedReverse = $ReverseSha256.ToLowerInvariant()
foreach ($inputFile in @(
    @($metadataFullPath, $expectedMetadata),
    @($auxiliaryFullPath, $expectedAuxiliary),
    @($forwardPath, $expectedForward),
    @($reversePath, $expectedReverse)
)) {
    if ((Get-FileHash -LiteralPath $inputFile[0] -Algorithm SHA256).Hash.ToLowerInvariant() -ne $inputFile[1]) { throw ('SHA256 distinto para ' + [System.IO.Path]::GetFileName($inputFile[0]) + '; no conectar.') }
}
$expectedTest = (Get-FileHash -LiteralPath $testPath -Algorithm SHA256).Hash.ToLowerInvariant()
$pythonExecutable = (Get-Command python -CommandType Application -ErrorAction Stop | Select-Object -First 1).Source
$receiptDirectory = [System.IO.Path]::GetDirectoryName($receiptFullPath)
[System.IO.Directory]::CreateDirectory($receiptDirectory) | Out-Null
[System.IO.File]::WriteAllLines($contextPath, @(
    ('Inicio UTC: ' + [DateTime]::UtcNow.ToString('yyyy-MM-dd HH:mm:ss')),
    ('HEAD inicial: ' + $initialHead),
    ('Estado inicial: ' + $(if ($ResumeFixture) { 'fixture sintetica preservada; verificar y completar la instalacion anterior.' } else { 'fixture sintetica nueva; crear desde metadatos sin filas operativas.' })),
    'Log inicial: prueba PostgreSQL aislada; nunca aplicar DDL en devmolineros.',
    ('Database fixture: ' + $Database),
    ('Metadata SHA256: ' + $expectedMetadata),
    ('Auxiliary metadata SHA256: ' + $expectedAuxiliary),
    ('Forward SHA256: ' + $expectedForward),
    ('Reverse SHA256: ' + $expectedReverse),
    ('Test SHA256: ' + $expectedTest)
), $latinEncoding)
$pythonArguments = @(
    '--database', $Database,
    '--metadata', $metadataFullPath,
    '--metadata-sha256', $expectedMetadata,
    '--auxiliary-metadata', $auxiliaryFullPath,
    '--auxiliary-sha256', $expectedAuxiliary,
    '--forward-sha256', $expectedForward,
    '--reverse-sha256', $expectedReverse,
    '--receipt', $receiptFullPath
)
if ($ResumeFixture) { $pythonArguments += '--resume-fixture' }
$ErrorActionPreference = 'Continue' # Registrar el exit code nativo sin ocultar errores.
$runOutput = @(& $pythonExecutable $testPath @pythonArguments 2>&1 | ForEach-Object { $_.ToString() })
$pythonExit = $LASTEXITCODE
$ErrorActionPreference = 'Stop'
$runOutput | Write-Output
Write-Output ('python_exit=' + $pythonExit)
if ($pythonExit -ne 0) { throw ('Prueba SQL fallida: ' + $pythonExit + '. Conservar fixture y recibo para diagnostico.') }
if (-not (Test-Path -LiteralPath $receiptFullPath -PathType Leaf)) { throw 'La prueba no produjo recibo.' }
$receipt = [System.IO.File]::ReadAllText($receiptFullPath, $latinEncoding) | ConvertFrom-Json
if ($receipt.status -ne 'passed' -or $receipt.database -ne $Database -or $receipt.host -ne '12.1.1.12' -or
    $receipt.connections_closed -ne $true -or $receipt.fixture_preserved -ne $true -or
    $receipt.resumed_existing_fixture -ne [bool]$ResumeFixture -or $receipt.check_count -le 0 -or
    $receipt.metadata_sha256 -ne $expectedMetadata -or $receipt.auxiliary_metadata_sha256 -ne $expectedAuxiliary -or
    $receipt.forward.source_sha256 -ne $expectedForward -or $receipt.reverse.source_sha256 -ne $expectedReverse -or
    $receipt.test_sha256 -ne $expectedTest) { throw 'Recibo incompleto o distinto del contrato solicitado.' }
Write-Output ('recibo_validado=True checks=' + $receipt.check_count + ' connections_closed=True fixture_preserved=True')
