param(
    [string]$RepoRoot = "C:\devmolineros\ext"
)

Set-StrictMode -Version 2.0
$ErrorActionPreference = "Stop"

function Stop-WithError {
    param([string]$Message)

    throw $Message
}

function Get-OccurrenceCount {
    param(
        [string]$Text,
        [string]$Value
    )

    if ([string]::IsNullOrEmpty($Value)) {
        return 0
    }

    $count = 0
    $index = 0

    while ($true) {
        $index = $Text.IndexOf(
            $Value,
            $index,
            [System.StringComparison]::Ordinal
        )

        if ($index -lt 0) {
            break
        }

        $count++
        $index += $Value.Length
    }

    return $count
}

function Read-TextDocument {
    param([string]$Path)

    $bytes = [System.IO.File]::ReadAllBytes($Path)
    $encoding = $null
    $preambleLength = 0
    $preamble = New-Object byte[] 0

    if (
        $bytes.Length -ge 3 -and
        $bytes[0] -eq 0xEF -and
        $bytes[1] -eq 0xBB -and
        $bytes[2] -eq 0xBF
    ) {
        $encoding = New-Object System.Text.UTF8Encoding($false, $true)
        $preambleLength = 3
        $preamble = [byte[]](0xEF, 0xBB, 0xBF)
    }
    elseif (
        $bytes.Length -ge 2 -and
        $bytes[0] -eq 0xFF -and
        $bytes[1] -eq 0xFE
    ) {
        $encoding = New-Object System.Text.UnicodeEncoding(
            $false,
            $false,
            $true
        )
        $preambleLength = 2
        $preamble = [byte[]](0xFF, 0xFE)
    }
    elseif (
        $bytes.Length -ge 2 -and
        $bytes[0] -eq 0xFE -and
        $bytes[1] -eq 0xFF
    ) {
        $encoding = New-Object System.Text.UnicodeEncoding(
            $true,
            $false,
            $true
        )
        $preambleLength = 2
        $preamble = [byte[]](0xFE, 0xFF)
    }
    else {
        $strictUtf8 = New-Object System.Text.UTF8Encoding($false, $true)

        try {
            [void]$strictUtf8.GetString($bytes)
            $encoding = $strictUtf8
        }
        catch {
            $encoding = [System.Text.Encoding]::GetEncoding(
                1252,
                [System.Text.EncoderFallback]::ExceptionFallback,
                [System.Text.DecoderFallback]::ExceptionFallback
            )
        }
    }

    $text = $encoding.GetString(
        $bytes,
        $preambleLength,
        $bytes.Length - $preambleLength
    )

    $newLine = "`n"

    if ($text.Contains("`r`n")) {
        $newLine = "`r`n"
    }

    $normalized = $text.Replace("`r`n", "`n").Replace("`r", "`n")

    return [PSCustomObject]@{
        Path = $Path
        Encoding = $encoding
        Preamble = $preamble
        NewLine = $newLine
        Text = $normalized
    }
}

function Get-DocumentBytes {
    param($Document)

    $textToWrite = $Document.Text

    if ($Document.NewLine -eq "`r`n") {
        $textToWrite = $textToWrite.Replace("`n", "`r`n")
    }

    $body = $Document.Encoding.GetBytes($textToWrite)
    $preamble = $Document.Preamble
    $result = New-Object byte[] ($preamble.Length + $body.Length)

    if ($preamble.Length -gt 0) {
        [System.Array]::Copy(
            $preamble,
            0,
            $result,
            0,
            $preamble.Length
        )
    }

    [System.Array]::Copy(
        $body,
        0,
        $result,
        $preamble.Length,
        $body.Length
    )

    return $result
}

function Replace-ExactOnce {
    param(
        $Document,
        [string]$OldValue,
        [string]$NewValue,
        [string]$Description
    )

    $count = Get-OccurrenceCount -Text $Document.Text -Value $OldValue

    if ($count -ne 1) {
        $message = (
            "{0}: se esperaba exactamente una coincidencia, " +
            "pero se encontraron {1} en {2}."
        ) -f $Description, $count, $Document.Path

        Stop-WithError $message
    }

    $Document.Text = $Document.Text.Replace($OldValue, $NewValue)

    Write-Host ("[OK] " + $Description) -ForegroundColor Green
}

function Assert-Contains {
    param(
        [string]$Text,
        [string]$Expected,
        [string]$Description
    )

    if (-not $Text.Contains($Expected)) {
        Stop-WithError (
            "Validacion fallida: " + $Description
        )
    }
}

function Assert-NotContains {
    param(
        [string]$Text,
        [string]$Unexpected,
        [string]$Description
    )

    if ($Text.Contains($Unexpected)) {
        Stop-WithError (
            "Validacion fallida: " + $Description
        )
    }
}

$RepoRoot = [System.IO.Path]::GetFullPath($RepoRoot)
$PackageRoot = Split-Path -Parent $MyInvocation.MyCommand.Path

$sourceEditor = Join-Path `
    $PackageRoot `
    "ext-web\docroot\html\portlet\compras\requerimientos\partials\_detalle_editor.jsp"

$sourceView = Join-Path `
    $PackageRoot `
    "ext-web\docroot\html\portlet\autorizaciones\reclamos_prestacionales\view_reclamo.js"

if (-not (Test-Path -LiteralPath (Join-Path $RepoRoot ".git"))) {
    Stop-WithError (
        "No se encontro el repositorio Git en: " + $RepoRoot
    )
}

if (-not (Test-Path -LiteralPath $sourceEditor)) {
    Stop-WithError (
        "Falta el archivo de reemplazo: " + $sourceEditor
    )
}

if (-not (Test-Path -LiteralPath $sourceView)) {
    Stop-WithError (
        "Falta el archivo de reemplazo: " + $sourceView
    )
}

Push-Location $RepoRoot

try {
    $trackedChanges = @(
        git status --porcelain --untracked-files=no
    )

    if ($LASTEXITCODE -ne 0) {
        Stop-WithError "No se pudo ejecutar git status."
    }

    if ($trackedChanges.Count -gt 0) {
        Write-Host "Se detectaron cambios rastreados previos:" `
            -ForegroundColor Red

        $trackedChanges | ForEach-Object {
            Write-Host $_
        }

        Stop-WithError (
            "Abortado para no mezclar ni sobrescribir cambios existentes."
        )
    }

    $expectedHeadBlobs = [ordered]@{
        "ext-web/docroot/html/portlet/compras/requerimientos/partials/_detalle_editor.jsp" = "f9bf72a5195d9bd292213ecaf7b6a7d4d9bb7ecd"
        "ext-web/docroot/html/portlet/compras/requerimientos/partials/_detalle_tabla.jsp" = "612bbd4cbfc70692b5802b7142c3668d3894f3c5"
        "ext-web/docroot/html/portlet/compras/requerimientos/partials/_detalle_modelo.jsp" = "f39c99da6490a688402010ee49d3a3007ac8bb0e"
        "ext-web/docroot/html/portlet/compras/requerimientos/partials/_detalle_scripts_comunes.jsp" = "47b2e515f2dddd1b43bbdf3e484cfe31dff7531f"
        "ext-web/docroot/html/portlet/compras/requerimientos/partials/_detalle_scripts_editable.jsp" = "902959f917ea2c7e69b275b30af06368f16d7496"
        "ext-web/docroot/html/portlet/autorizaciones/reclamos_prestacionales/view_reclamo.js" = "fe6e0bc509e3efc5adae1734b63802af4801b20a"
        "ext-impl/src/ar/com/ospim/test/ComprasRequerimientosUiContractTest.java" = "34496bf608927678b142a932d55a4d5c67dc7a1a"
    }

    foreach ($relativePath in $expectedHeadBlobs.Keys) {
        $targetPath = Join-Path $RepoRoot $relativePath

        if (-not (Test-Path -LiteralPath $targetPath)) {
            Stop-WithError (
                "No existe el archivo esperado: " + $relativePath
            )
        }

        $actualBlob = (
            git rev-parse ("HEAD:" + $relativePath)
        ).Trim()

        if ($LASTEXITCODE -ne 0) {
            Stop-WithError (
                "No se pudo calcular el blob de HEAD para: " +
                $relativePath
            )
        }

        if ($actualBlob -ne $expectedHeadBlobs[$relativePath]) {
            Stop-WithError (
                "La version local no coincide con el main esperado." +
                "`nArchivo: " + $relativePath +
                "`nEsperado: " + $expectedHeadBlobs[$relativePath] +
                "`nActual:   " + $actualBlob
            )
        }
    }

    $replacementEditorBlob = (
        git hash-object -- $sourceEditor
    ).Trim()

    if ($replacementEditorBlob -ne "ebb4d42269bd3afbc30a299a4167f48204a8282c") {
        Stop-WithError (
            "El _detalle_editor.jsp del paquete esta alterado."
        )
    }

    $replacementViewBlob = (
        git hash-object -- $sourceView
    ).Trim()

    if ($replacementViewBlob -ne "096cb48eb221d9e785a646c576f838a8e98362ab") {
        Stop-WithError (
            "El view_reclamo.js del paquete esta alterado."
        )
    }

    $paths = [ordered]@{
        Editor = "ext-web/docroot/html/portlet/compras/requerimientos/partials/_detalle_editor.jsp"
        Tabla = "ext-web/docroot/html/portlet/compras/requerimientos/partials/_detalle_tabla.jsp"
        Modelo = "ext-web/docroot/html/portlet/compras/requerimientos/partials/_detalle_modelo.jsp"
        Comunes = "ext-web/docroot/html/portlet/compras/requerimientos/partials/_detalle_scripts_comunes.jsp"
        Editable = "ext-web/docroot/html/portlet/compras/requerimientos/partials/_detalle_scripts_editable.jsp"
        View = "ext-web/docroot/html/portlet/autorizaciones/reclamos_prestacionales/view_reclamo.js"
        Test = "ext-impl/src/ar/com/ospim/test/ComprasRequerimientosUiContractTest.java"
    }

    $documents = [ordered]@{
        Tabla = Read-TextDocument (
            Join-Path $RepoRoot $paths["Tabla"]
        )
        Modelo = Read-TextDocument (
            Join-Path $RepoRoot $paths["Modelo"]
        )
        Comunes = Read-TextDocument (
            Join-Path $RepoRoot $paths["Comunes"]
        )
        Editable = Read-TextDocument (
            Join-Path $RepoRoot $paths["Editable"]
        )
        Test = Read-TextDocument (
            Join-Path $RepoRoot $paths["Test"]
        )
    }

    Replace-ExactOnce `
        -Document $documents.Tabla `
        -OldValue "        <th>Tipo</th>`n" `
        -NewValue "" `
        -Description "Quitar la columna Tipo del listado"

    $oldModelo = @'
int detalleColspan =
        6
        + (puedeVerCotizacionDetalle ? 2 : 0)
        + (puedeABMDetalle ? 1 : 0);
'@

    $newModelo = @'
int detalleColspan =
        5
        + (puedeVerCotizacionDetalle ? 2 : 0)
        + (puedeABMDetalle ? 1 : 0);
'@

    Replace-ExactOnce `
        -Document $documents.Modelo `
        -OldValue $oldModelo `
        -NewValue $newModelo `
        -Description "Ajustar el colspan luego de quitar Tipo"

    Replace-ExactOnce `
        -Document $documents.Comunes `
        -OldValue "            html += '<td>' + <portlet:namespace />detalleEscapeHtml(detalle.tipoItem) + '</td>';`n" `
        -NewValue "" `
        -Description "Quitar la celda Tipo del render"

    $oldEditable = @'
        var tipoLabel = '-';

        if (tipoItem == 'MEDICAMENTO') {
            tipoLabel = 'Medicamento';
        } else if (tipoItem == 'NOMENCLADOR') {
            tipoLabel = 'Nomenclador';
        }

        jQuery('#<portlet:namespace />detalle_tipo_item_label').text(tipoLabel);
'@

    Replace-ExactOnce `
        -Document $documents.Editable `
        -OldValue $oldEditable `
        -NewValue "" `
        -Description "Quitar el controlador de la etiqueta Tipo"

    Replace-ExactOnce `
        -Document $documents.Test `
        -OldValue "fe6e0bc509e3efc5adae1734b63802af4801b20a" `
        -NewValue "096cb48eb221d9e785a646c576f838a8e98362ab" `
        -Description "Actualizar el hash contractual de view_reclamo.js"

    $editorReplacementText = (
        Read-TextDocument $sourceEditor
    ).Text

    $viewReplacementText = (
        Read-TextDocument $sourceView
    ).Text

    Assert-Contains `
        -Text $editorReplacementText `
        -Expected 'id="<portlet:namespace />detalle_tipo_item"' `
        -Description "debe conservarse el tipo tecnico oculto"

    Assert-NotContains `
        -Text $editorReplacementText `
        -Unexpected "detalle_tipo_item_label" `
        -Description "el editor no debe mostrar Tipo"

    Assert-NotContains `
        -Text $documents.Tabla.Text `
        -Unexpected "<th>Tipo</th>" `
        -Description "la tabla no debe mostrar Tipo"

    Assert-Contains `
        -Text $documents.Modelo.Text `
        -Expected "int detalleColspan =`n        5" `
        -Description "el colspan base debe ser 5"

    Assert-NotContains `
        -Text $documents.Comunes.Text `
        -Unexpected "detalleEscapeHtml(detalle.tipoItem)" `
        -Description "el render no debe crear la celda Tipo"

    Assert-NotContains `
        -Text $documents.Editable.Text `
        -Unexpected "detalle_tipo_item_label" `
        -Description "el JavaScript editable no debe buscar la etiqueta eliminada"

    Assert-Contains `
        -Text $viewReplacementText `
        -Expected "return sector == 'FARMACIA' && tipoPedido != 'EXCEPCION';" `
        -Description "Farmacia no EXCEPCION debe usar medicamentos"

    Assert-Contains `
        -Text $viewReplacementText `
        -Expected "sector == 'FARMACIA' && tipoPedido == 'EXCEPCION'" `
        -Description "Farmacia EXCEPCION debe usar Codigo Presentado"

    Assert-Contains `
        -Text $viewReplacementText `
        -Expected 'tipoNomencladorSeguimiento_filtro").val("9")' `
        -Description "Farmacia EXCEPCION debe usar nomenclador 9"

    $manejarCount = Get-OccurrenceCount `
        -Text $viewReplacementText `
        -Value "function manejarTipoSector(){"

    if ($manejarCount -ne 1) {
        Stop-WithError (
            "El view_reclamo.js debe contener una sola funcion " +
            "manejarTipoSector(). Coincidencias: " +
            $manejarCount
        )
    }

    $backupRoot = Join-Path `
        $RepoRoot `
        (
            "_backup_cambio_tipo_farmacia_" +
            (Get-Date -Format "yyyyMMdd_HHmmss")
        )

    foreach ($relativePath in $paths.Values) {
        $sourcePath = Join-Path $RepoRoot $relativePath
        $backupPath = Join-Path $backupRoot $relativePath
        $backupDirectory = Split-Path -Parent $backupPath

        [System.IO.Directory]::CreateDirectory(
            $backupDirectory
        ) | Out-Null

        Copy-Item `
            -LiteralPath $sourcePath `
            -Destination $backupPath `
            -Force
    }

    Write-Host (
        "Respaldo creado en: " + $backupRoot
    ) -ForegroundColor Cyan

    $pendingWrites = New-Object System.Collections.ArrayList

    foreach ($key in $documents.Keys) {
        $document = $documents[$key]
        $bytes = Get-DocumentBytes $document

        [void]$pendingWrites.Add(
            [PSCustomObject]@{
                Path = $document.Path
                Bytes = $bytes
            }
        )
    }

    [void]$pendingWrites.Add(
        [PSCustomObject]@{
            Path = Join-Path $RepoRoot $paths["Editor"]
            Bytes = [System.IO.File]::ReadAllBytes($sourceEditor)
        }
    )

    [void]$pendingWrites.Add(
        [PSCustomObject]@{
            Path = Join-Path $RepoRoot $paths["View"]
            Bytes = [System.IO.File]::ReadAllBytes($sourceView)
        }
    )

    foreach ($write in $pendingWrites) {
        [System.IO.File]::WriteAllBytes(
            $write.Path,
            $write.Bytes
        )
    }

    $finalEditorBlob = (
        git hash-object -- $paths["Editor"]
    ).Trim()

    $finalViewBlob = (
        git hash-object -- $paths["View"]
    ).Trim()

    if ($finalEditorBlob -ne "ebb4d42269bd3afbc30a299a4167f48204a8282c") {
        Stop-WithError (
            "El editor final no coincide con el archivo validado."
        )
    }

    if ($finalViewBlob -ne "096cb48eb221d9e785a646c576f838a8e98362ab") {
        Stop-WithError (
            "El view final no coincide con el archivo validado."
        )
    }

    $diffCheck = git diff --check 2>&1

    if ($LASTEXITCODE -ne 0) {
        Write-Host $diffCheck -ForegroundColor Red
        Stop-WithError (
            "git diff --check encontro errores. " +
            "El respaldo esta disponible en: " +
            $backupRoot
        )
    }

    $nodeCommand = Get-Command node -ErrorAction SilentlyContinue

    if ($nodeCommand -ne $null) {
        node --check $paths["View"]

        if ($LASTEXITCODE -ne 0) {
            Stop-WithError (
                "node --check fallo para view_reclamo.js. " +
                "El respaldo esta disponible en: " +
                $backupRoot
            )
        }

        Write-Host "[OK] node --check view_reclamo.js" `
            -ForegroundColor Green
    }
    else {
        Write-Host (
            "[AVISO] Node.js no esta instalado; " +
            "se omitio node --check."
        ) -ForegroundColor Yellow
    }

    Write-Host ""
    Write-Host "CAMBIOS APLICADOS Y VALIDADOS" `
        -ForegroundColor Green

    Write-Host (
        "Respaldo: " + $backupRoot
    ) -ForegroundColor Cyan

    Write-Host ""
    git status --short
    git diff --stat
}
catch {
    Write-Host ""
    Write-Host "NO SE COMPLETO LA OPERACION" `
        -ForegroundColor Red

    Write-Host $_.Exception.Message `
        -ForegroundColor Red

    exit 1
}
finally {
    Pop-Location
}
