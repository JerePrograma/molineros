# Health check de outbox AppMobile

## Ejecución

Con URL completa:

```bash
DATABASE_URL='postgresql://usuario:password@host:5432/base' \
  bash scripts/reclamos/verificar_outbox_appmobile.sh
```

Con variables PostgreSQL estándar:

```bash
PGHOST=host \
PGPORT=5432 \
PGDATABASE=base \
PGUSER=usuario \
PGPASSWORD=password \
  bash scripts/reclamos/verificar_outbox_appmobile.sh
```

## Umbrales

```text
PENDING_MAX_MINUTES=10
MAX_ATTEMPTS=5
```

Ejemplo:

```bash
PENDING_MAX_MINUTES=15 \
MAX_ATTEMPTS=8 \
DATABASE_URL="$DATABASE_URL" \
  bash scripts/reclamos/verificar_outbox_appmobile.sh
```

## Códigos de salida

- `0`: saludable;
- `1`: existen pendientes vencidos respecto del umbral;
- `2`: condición crítica o error técnico.

Condiciones críticas:

- tabla ausente;
- `psql` no disponible;
- consulta fallida;
- lease vencido;
- cantidad de intentos igual o superior al umbral;
- parámetros no numéricos.

## Salida

El script imprime una sola línea de métricas:

```text
outbox pending=0 processing=0 processed=120 overdue=0 expired_leases=0 excessive_attempts=0 oldest_pending_minutes=0
```

La salida puede integrarse con:

- cron y correo;
- Nagios/Icinga;
- Zabbix;
- scripts de systemd;
- recolectores de logs;
- pipelines de despliegue.

## Recomendación de frecuencia

Ejecutar cada cinco minutos. El dispatcher procesa cada minuto; un umbral de 10
minutos evita alertar por un único fallo transitorio y detecta acumulación antes
de que el backoff alcance una hora.

## Seguridad

- no usar `set -x` con `DATABASE_URL` o `PGPASSWORD`;
- no imprimir credenciales en logs;
- utilizar un usuario de monitoreo con `SELECT` sobre la tabla;
- no otorgar permisos de escritura al health check;
- proteger el archivo o mecanismo que inyecta credenciales.
