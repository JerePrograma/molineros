$ErrorActionPreference = "Stop"

$project = "C:\devmolineros"
$tomcat  = "C:\apache-tomcat-8.5.23"

cd $project

Write-Host "Buscando JSP/assets modificados..." -ForegroundColor Cyan

$tracked = git diff --name-only --diff-filter=ACMRT HEAD -- ext-web/docroot
$untracked = git ls-files --others --exclude-standard -- ext-web/docroot

$files = @($tracked) + @($untracked) |
  Where-Object {
    $_ -match '\.(jsp|jspf|css|js|png|jpg|jpeg|gif|svg|ico)$'
  } |
  Sort-Object -Unique

if (-not $files -or $files.Count -eq 0) {
  Write-Host "No hay JSP/assets modificados para copiar." -ForegroundColor Yellow
  exit 0
}

foreach ($file in $files) {
  $relative = $file -replace '^ext-web/docroot/', ''
  $src = Join-Path $project ($file -replace '/', '\')
  $dst = Join-Path "$tomcat\webapps\ROOT" ($relative -replace '/', '\')

  New-Item -ItemType Directory -Force -Path (Split-Path $dst) | Out-Null
  Copy-Item $src $dst -Force

  Write-Host "COPIADO: $relative" -ForegroundColor Green
}

Write-Host "Fast deploy JSP/assets terminado. No se reinició Tomcat." -ForegroundColor Cyan