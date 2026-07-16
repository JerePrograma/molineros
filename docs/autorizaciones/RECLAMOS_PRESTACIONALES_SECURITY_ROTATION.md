# Rotación de credenciales AppMobile

## Motivo

`ClienteAppMobile.java` conserva credenciales históricas embebidas. Aunque el
flujo reparado usa configuración externa, esos valores deben considerarse
comprometidos por haber estado versionados.

## Orden seguro

1. crear nuevas credenciales en AppMobile;
2. cargar las nuevas claves en QA:
   - `APP_BACKOFFICE_API_KEY`;
   - `APP_BACKOFFICE_EMAIL`;
   - `APP_BACKOFFICE_PASSWORD`;
3. validar login y actualización `AN` desde QA;
4. cargar las claves en producción sin retirar todavía las anteriores;
5. desplegar el cliente configurable;
6. validar outbox y llamadas externas;
7. retirar o revocar las credenciales históricas;
8. eliminar literales del código legacy;
9. buscar consumidores restantes;
10. considerar reescritura de historial coordinada.

## Validación

```bash
bash scripts/reclamos/verificar_secretos_appmobile.sh
bash scripts/reclamos/validar_release_reclamos.sh
```

El segundo comando debe ejecutarse sin override.

## Búsquedas mínimas

```bash
grep -R -n 'ClienteAppMobile.obtenerToken' ext-impl/src
grep -R -n 'APP_BACKOFFICE_' ext-impl/src classes
git grep -n -E 'API_KEY|PASSWORD|integraciones@' -- ext-impl/src
```

Revisar manualmente resultados para evitar publicar valores en tickets o logs.

## Historial Git

Eliminar el valor del archivo actual no lo elimina de commits anteriores.
Antes de reescribir historial:

- coordinar con todos los clones y ramas;
- realizar backup;
- rotar primero las credenciales;
- documentar el commit de corte;
- invalidar caches o artefactos publicados;
- forzar reclonado cuando corresponda.

La rotación es obligatoria incluso si se limpia el historial.
