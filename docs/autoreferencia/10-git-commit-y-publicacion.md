# Git, commit y publicacion

## Inicio

```powershell
Set-Location -LiteralPath 'C:\devmolineros\ext'
git rev-parse --show-toplevel
git remote get-url origin
git status --short
git branch --show-current
git rev-parse HEAD
git log -1 --oneline
git fetch origin --prune
git switch main
git merge --ff-only origin/main
```

Detenerse si raiz/remoto no coinciden o si cambios locales interfieren.

## Staging y commit

```powershell
git add -- <ruta-1>
git add -- <ruta-2>
git diff --cached --check
git diff --cached --stat
git commit
```

Mensaje en espanol, con resumen y detalle verificable.

## Publicacion

```powershell
git fetch origin --prune
git merge --no-edit origin/main
git push origin main:main
```

Usar merge solo si `origin/main` avanzo y no puede hacerse fast-forward. No rebase ni force-push.

## Verificacion

```powershell
git rev-parse main
git rev-parse origin/main
git merge-base --is-ancestor <commit-creado> origin/main
git status --short --branch
```

Los SHA deben coincidir. Si hay rechazo o conflicto no trivial, detenerse e informar.
