# Scripts operativos de Reclamos Prestacionales

## Gate de release

Ejecutar desde la raíz del repositorio:

```bash
bash scripts/reclamos/validar_release_reclamos.sh
```

El script:

- compila y ejecuta los siete contratos independientes de Liferay;
- comprueba los cuatro assets `p0-4`;
- verifica la estructura de la migración de outbox;
- comprueba que el autenticador reparado usa configuración externa;
- rechaza AJAX síncrono en los parches nuevos;
- rechaza referencias operativas obsoletas a `p0-2`;
- bloquea el release si detecta credenciales literales en
  `ClienteAppMobile.java`.

## Bloqueo actual conocido

El cliente AppMobile legacy todavía conserva secretos históricos en el archivo
fuente. Aunque la baja reparada y el worker de outbox ya no usan ese
autenticador, esos valores deben:

1. rotarse en AppMobile;
2. reemplazarse por configuración externa;
3. eliminarse del archivo y, cuando corresponda, del historial Git;
4. verificarse antes de producción.

El gate termina con error mientras exista ese riesgo.

## Override no productivo

Para ejecutar solamente las comprobaciones técnicas restantes en un entorno de
desarrollo o QA puede utilizarse:

```bash
ALLOW_LEGACY_APPMOBILE_SECRETS=1 \
  bash scripts/reclamos/validar_release_reclamos.sh
```

Este override:

- no corrige el secreto;
- no autoriza producción;
- no debe incorporarse a scripts de despliegue;
- debe quedar registrado si se usa en una validación formal.

## Alcance del gate

El resultado exitoso tampoco reemplaza:

- migración real de la base objetivo;
- configuración de las cuatro claves AppMobile;
- compilación completa de Liferay;
- inspección del WAR;
- smoke tests funcionales;
- verificación de idempotencia de `AN`;
- monitoreo de outbox y logs.

El procedimiento completo está en:

```text
docs/autorizaciones/RECLAMOS_PRESTACIONALES_P0_DEPLOY.md
docs/autorizaciones/RECLAMOS_APPMOBILE_OUTBOX_OPERACION.md
```
