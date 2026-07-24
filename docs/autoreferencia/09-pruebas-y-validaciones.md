# Pruebas y validaciones

## Minimos obligatorios

```powershell
git diff --check
git status --short
git diff --stat
git diff -- <archivos-modificados>
```

## Build comprobado

El build raiz usa Ant y su target por defecto es `deploy`. Existen targets `war` y `war-qa`. No ejecutar un target sin revisar `build-parent.xml`, propiedades requeridas, efectos sobre `ext-web/tmp` y secretos de entorno.

## Tests

No se verifico remotamente una suite focalizada ni su comando. Buscar tests directos del paquete o modulo antes de afirmar cobertura.

## Reporte

Separar:
- aprobadas;
- fallidas;
- fallos preexistentes;
- no ejecutadas;
- limitaciones del entorno.

No ocultar fallos ni declarar disponibilidad productiva sin build y smoke tests reales.
