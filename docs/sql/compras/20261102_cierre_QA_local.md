# Cierre conjunto de Compras en QA local - 02/11/2026

**IMPLEMENTADO Y DESPLEGADO EN QA; MITIGACION HTTP APLICADA; UI MANUAL PENDIENTE; RETIRO SQL NO CERTIFICADO.**
No se declara cierre total, Java exclusivo ni aptitud de produccion. La publicacion final de este documento y los cuatro commits de codigo consta en el recibo Git externo indicado abajo.

## Repositorio, alcance y preservacion

- Repositorio: https://github.com/JerePrograma/molineros.git.
- Ruta unica: `C:/wsmolineros/ext`; rama `main`, publicacion `origin/main`.
- HEAD inicial de la tarea original: `e0094bd2d721483d5e552ac5863201e2ae683ee7`.
- HEAD inicial de este cierre del 02/11: `2876dfbe491369fcfa711d5e4f941a9b30e6999f`.
- SHA de producto compilado, probado y desplegado: `1063a791c83410b451c7127cc239e6aa427e7b92`.
- Commits conjuntos: `1b6fe524` (lecturas/reasignacion), `e49a4dda` (guardia QA), `da041aff` (secuencia estricta), `1063a791` (snapshots de edicion).
- El cambio posterior de cierre contiene exclusivamente estos dos documentos. El SHA definitivo local/remoto y los exit codes de commit/push estan en `git_publicacion_FINAL_C_20261102.json`, externo al repositorio.
- Se coordino con el chat "Normalizar informacion de Compras" por autorizacion humana expresa. Fuente congelada antes del empaquetado; una sola coordinacion de runtime por raiz.
- Este cierre no accedio a produccion. QA PostgreSQL es `12.1.1.12:5432/devmolineros`; Tomcat es Windows LOCAL `C:/apache-tomcat-8.5.23`. Se preservo el informe cronologico del 30/10 y su evidencia, sin reescribir sus antecedentes.
- El trabajo local previo del usuario ya integrado se conserva. Este cierre comenzo limpio; el documento de certificacion posterior se preservo durante los commits del otro chat. Sin stash, reset, clean, rebase, force-push, ramas ni PR.

## Referencias y comportamiento

Referencia JDBC: `ext-impl/src/ar/com/ospim/liquidaciones/services/EditarLiquidacionServiceImpl.java`, `getComprobantesLiquidaciones(..., Connection)`, lineas 550-605. Conserva la conexion recibida y cierra recursos propios. Referencia de validacion: `liquidaciones/ordenespago/action/EditarOrdenesPagoOspimAction.java:163`; presentacion `html/portlet/liquidaciones/ordenes_pago/editar_orden_pago_ospim.jsp:66`. Para fechas se reutiliza la regla de `Autorizaciones/ValidarReclamo` y los Actions/JSP existentes. Los archivos de build/deploy existentes determinan las rutas de los dos JAR y de los descriptores extension.

Las pantallas y nuevos exports leen afiliado, nomenclador, medicamento y proveedor vigentes. La edicion utiliza lectores persistidos separados: modificar una cantidad no sustituye codigo, descripcion, tipo, troquel ni nombre historicos. Se reprodujeron ocho fallos antes del arreglo y cero despues. La reasignacion se admite solo en borrador y confirma identidad e historial en la misma TX, manteniendo adjuntos. No se incorporaron dependencias, cache de Compras, arquitectura nueva ni sintaxis moderna.

Se conservan atomicidad, bloqueos, permisos, tokens, estado autoritativo, idempotencia y contratos publicos; Java 8 con source/target 1.5, Liferay 5.2, JSP/Struts/Tiles/ES5. Los documentos ya emitidos permanecen almacenados. No hubo envios SMTP, adjudicaciones ni pagos de prueba.

## Artefactos y despliegue real

Tres compilaciones configuradas y dos targets JAR existentes: **cinco exit codes 0**. El paquete verifica 97 fuentes (68 Java y 29 web), 108 clases del alcance y 78 clases de fuentes sin cambios iguales al congelado previo, sin deriva. Las 2.940 clases de ambos JAR son major 49. Las 2.966 entradas no Manifest coinciden con el output nativo; no se superpusieron clases viejas.

| Artefacto instalado | SHA-256 |
| --- | --- |
| ext-service.jar | `70d2096db21ea6c7ca21193d5607e0958949253fb4387241c65c56265140cded` |
| ext-impl.jar | `346ef3da13fbc7ff09177bb7b75f7a5a2526b8f86d686c5b260504db4c91006f` |
| ZIP de 29 archivos web | `1dc16be894830556c2cc2bd21160b73941d8664f54befe75aeda243eeb71e325` |

Instalador V2 `c867b07c...`: Preview 0 y Apply 0; **31 respaldos y 31 copias verificadas**, con Tomcat completamente detenido. `struts-config.xml` y `tiles-defs.xml` del repositorio se copian a los nombres `*-ext.xml`; los descriptores BASE, core Liferay, web.xml, pools, system y ausencia del wizard se preservan. El manifiesto final confirma 31/31 destinos y nueve protecciones, con el unico cambio posterior autorizado de propiedades del ensayo D al perfil C.

Paradas de PID 92840 y 19692: shutdown configurado, exit 0 y proceso/puerto ausentes. Sin force-kill. Arranque final configurado: exit 0, `TOMCAT_OK HTTP=200`, PID **95924**, JDK8/base esperada/listener8080. No hubo segunda instancia.

## Latencia y estado HTTP final

A/B/C/D se ejecutaron en QA. D predefinia dos regiones de cache ya existentes del portal: produjo 16/16 respuestas publicas en 125-196ms de mediana, pero tres stacks conservaron una espera FinderCache de UserGroups. Por eso **D no quedo como solucion final**.

Se revirtio D al prefijo exacto y se dejo la mitigacion C: `value.object.finder.cache.enabled=false`, 41 bytes de sufijo. Propiedades finales SHA `d21fa32384d5eabeddcffc4e540122a7434f9c62b20b4e0a568ec85375647210`; prefijo `5eae14cd...` intacto. El XML de ensayo `7017e343...` queda conservado INACTIVO, sin la propiedad que lo selecciona. EntityCache no se modifica; Compras sigue leyendo configuracion DB sin cache.

C_FINAL: **16/16 HTTP200, 0 timeouts**, warmup 4453/3094ms; medianas secuencial 3078ms, concurrencia2 3023.5ms y concurrencia4 3960.5ms; maximos 4125/3250/5156ms. Tres stacks validos: **0 Mutex/Finder/Group/Entity cache waits observados**. Esto acredita una mitigacion de las esperas observadas en rutas publicas; no demuestra causa raiz universal ni rendimiento de UI autenticada. La latencia de 3-5s sigue siendo una limitacion.

Los pools locales conservan maximo combinado25/minimo6: principal2/2/10, OSPIM2/2/10, Lportal2/2/3 y reportes0/0/2. URLs QA12 verificadas sin exportar cuerpos ni credenciales. Las muestras de PostgreSQL no encontraron bloqueadores ni transacciones locales ociosas abiertas. Todas las conexiones de diagnostico se cerraron.

## SQL final y certificacion de escritores

La migracion exacta `20261102_auditoria_reasignacion_afiliado.sql`, SHA `6168fa6e6870fe22846505310250004cc483691a50b7af66398e248a174fef2d`, se aplico en QA y se repitio: **46/46 verificaciones**, ocho columnas, dos constraints, secuencia propia/default exclusivo, repeticion fisicamente idempotente. No se modificaron funciones ni triggers.

POST C_FINAL: **43/43 verificaciones RO**, 104 funciones, cinco triggers habilitados, 68 configuraciones, 22 asociaciones y siete canonicos capturados identicos al baseline; SHA global `3206fef506c0e3bd53e3d4ce4cffdac58356dd5252e8c566ef5e5d7bb6b60a2c`. Historial0, marca QA0, casos nuevos0; conexion cerrada 17:10:55.227UTC.

`compras.sector_nomenclador` se conserva. Diabetes rubro8 selecciona tipo15; Insumos rubro6 mantiene tipo10. Los tres canonicos nuevos de tipo15 son 11507/11508/11509; el gasto automatico Medicas es 11503/tipo3. QA no contiene 1388/445/446: no se invento su verificacion real ni se consulto produccion para completarla. Las correcciones externas ya autorizadas se conservan. Las regresiones de historicos son pruebas focales, no una prueba UI de esos IDs ausentes.

**Retiro SQL NO certificado:** 58 pruebas PostgreSQL reales en una base fixture conservada reproducen cinco contraejemplos sin triggers. Siguen disponibles firmas SQL y DML directo que omiten gasto, duplicidad, total, auditoria de baja o baja de vinculo RP. Dos roles login superusuarios conservan permisos amplios; faltan clientes/jobs/mantenimiento externo e ingreso coordinado Java. Por eso no se retiro ninguno de los cinco triggers ni de las seis funciones de efectos. Matriz, hashes instalados y contratos concretos estan en `20261102_certificacion_escritores_QA.md`.

## Pruebas, fallos y pendientes

| Verificacion | Resultado y alcance |
| --- | --- |
| Build configurado 3compile +2jar | Todos0; JDK8/source-target1.5; no ant clean. |
| Snapshot cantidad NOM/MED | Antes32checks/8fallos; despues32/0; JDBC simulado con lectores/Helper/UPDATE reales. |
| Reasignacion | 30/30; auditoria y rollback con frontera JDBC simulada. |
| Core/asociaciones/correccion/duplicados/RP | 169/169; 37+70+33+29, fronteras simuladas. |
| Efectos TX | 24/24, JDBC simulado. |
| Lecturas Java/primera carga | 21 y2177 comprobaciones aprobadas. |
| Lecturas SQL reales QA | 24 SELECT/33 aserciones, solo lectura. |
| Secuencia auditoria | 36 verificaciones PostgreSQL reales y siete casos negativos. |
| Migracion auditoria QA | 46/46, avance y repeticion con commits reales. |
| Escritores SQL | 40+18 pruebas reales; resultado negativo para retiro, conexiones cerradas. |
| Fechas y comprobantes seccion9/064 | 20/20 sobre1063; compilaciones y runner0. Rechazo atomico antes de mutar destinos, mismo dia, bajas, NULL/futuro y consentimientos separados OTR/global. Sin UI. |
| HTTP C_FINAL / PG / jstack | Procesos0,16/16HTTP200, tres stacks0, conexiones cerradas; rutas publicas solamente. |
| SQL POST C_FINAL | 43/43, sin escrituras ni casos nuevos. |
| UI final | **PRUEBA UI MANUAL PENDIENTE**, 24 casos,0PASS. |

La herramienta de control se detuvo porque no pudo determinar con certeza la URL actual de Brave en Windows. No se reintento ni se sustituyo por otro mecanismo despues del STOP. Una captura previa de un borrador mostraba seleccion de Diabetes, sin detalles agregados y en runtime anterior: no acredita el paquete final. No hubo escrituras de negocio UI de raiz ni se utilizaron los originales como descartables. Faltan los flujos autenticados completos de alta/edicion/reasignacion/adjuntos/RP/comprobantes/permisos/OC y persistencia final. No se hicieron correos reales ni adjudicaciones/pagos por efecto colateral.

Fallos preservados: primer instalador fallo MAXPATH tras cuatro respaldos y cero copias; V2 cambia solamente la ruta de respaldos y luego paso. El capturador del primer arranque espero EOF heredado y cerro al detener D, sin matar procesos. La lectura exclusiva del hash de stdout/stderr final fallo por un handle abierto; se completo mediante snapshot compartido estable, sin modificar logs. Los cuatro mensajes GRAVE de la regresion de fechas son rechazos esperados offline. La fixture preexistente `comparativa_sincronizacion_test` sigue desactualizada en COMPRAS_ESTADOS_IDS/@IP@; no se presenta como aprobada. NPE de scheduler131, CNFE de loader y PluginPackageException aparecen tambien en antecedentes y no se atribuyen al cambio sin evidencia adicional. No se ocultaron ni corrigieron modulos ajenos.

## Comandos y evidencia

Comandos configurados: `ant -f ext-service/build.xml compile`, `ant -f ext-impl/build.xml compile`, `ant -f ext-web/build.xml compile`, y targets `jar` de los dos primeros (todos0). Scripts de parada/arranque configurados (todos0); instalador original1 antes de copias, V2Preview/Apply0; modos de configuracion C/D aplicados/revertidos0; Python de SQL/HTTP/PG y runner fechas finales0. `git diff --check`, staging, commit, fetch, push y verificaciones exactas se registran con su resultado en el recibo final Git.

Evidencia externa: `C:/Users/Programador 2/.codex/visualizations/2026/10/30/01a18d18-13e2-7f92-9bd5-1feb77f35fa3/cierre_20261102/`.

| Evidencia | SHA-256 |
| --- | --- |
| entrega_FINAL_1063a791_20261102T164258533139Z/manifest_paquete_FINAL.json | `972017df8175471d75a90c7a57825e6110151540e3ecd33bf406e47db72ff5fe` |
| manifest_runtime_FINAL_C_1063_20261102.json | `d464577ecc4d745f5ddfe3a81d6b5979148ede1f5d1e753a862c1e6c00048b39` |
| http_benchmark_FinalC_95924_20261102.json | `d2b7a2cbcddc865ec301c0352d9684e4143a26eaa1a15369d479d4fee68d7649` |
| latencia_DB_QA12_FinalC_95924_20261102.json | `dbe77d3dd9442916168695aee1fcedaab638ef0cb2389d70a5c1d2cef2810342` |
| manifest_verificador_RO_final_QA12_20261102_POST_C_FINAL.json | `d6ef58efd070a8504c2f9c5b7523bfe8e9aaa9d316865191145ea09641e781d7` |
| verificacion_RO_final_20261102T171049424000Z/resultado_sin_PII.json | `f513fc498d9416ce3ee91441fc0923c799ac694c04828adbce315f636b237617` |
| UI_final_PENDIENTE_por_control_20261102.json | `d9105974491fd765170c0ccdbe7af49e909aaf58ff55a61fde81f1d5459227e9` |
| manifest_certificacion_escritores_20261102.json | `e41da720bcb1c100c5cabefa8813e86842e9f75ce386dbd1cacb72ec8660f513` |

El recibo de 31 copias/respaldos esta en `C:/wsmolineros/ext/output/qa-cierre-20261102/deploy1063/instalacion_inmutable_20261102165405801/resultado.json`, SHA `2b20373a448c78e642fa40c790de52f6818ba077d3329c5272cbec89f036c283`. La evidencia de fechas actualizada esta en la carpeta hermana `f20-1063-1102`, recibo SHA `e8b4c194daf0f21f50678bf9030fe25463ba36e854a40a3a24b4b412655b118d`.

## Archivos incorporados por los commits del 02/11

Cada ruta siguiente fue auditada como ISO-8859-1 sin BOM, sin U+00C3/U+00C2/U+FFFD, revisada sin recodificacion masiva. El metodo lee bytes, comprueba prefijos BOM, decodifica Latin-1 y verifica indicadores por archivo; hashes/resultados estan en `auditoria_encoding_FINAL_20261102.json`. Los archivos de producto no cambiaron despues del build.

| Ruta | Motivo preciso |
| --- | --- |
| `docs/sql/compras/20261102_auditoria_reasignacion_afiliado.sql` | Tabla de historial: QA12 estricto, ocho columnas, FK/PK, secuencia propia y default exclusivo; avance y repeticion controlados. |
| `ext-impl/src/ar/com/ospim/compras/requerimientos/action/EditarRequerimientoCompraAction.java` | Adaptar los datos de identidad y el resultado HTTP al coordinador de edicion, preservando permisos y token. |
| `ext-impl/src/ar/com/ospim/compras/requerimientos/helper/EditarRequerimientoCompraHelper.java` | Reasignar afiliado solamente en borrador, validando estado e identidad vigente bajo el mismo bloqueo y transaccion. |
| `ext-impl/src/ar/com/ospim/compras/requerimientos/service/BusquedaRequerimientoCompraServiceImpl.java` | Lecturas parametrizadas de maestros vigentes; lectores separados del evento persistido para editar sin reescribir snapshots. |
| `ext-impl/src/ar/com/ospim/compras/requerimientos/service/BusquedaRequerimientoCompraServiceUtil.java` | Delegar lectores vigentes y persistidos conservando Connection y beans existentes. |
| `ext-impl/src/ar/com/ospim/compras/requerimientos/service/EditarRequerimientoCompraServiceImpl.java` | Persistir reasignacion e historial atomicamente, bloquear nuevo afiliado FOR SHARE y usar lectores persistidos dentro de la TX. |
| `ext-impl/src/ar/com/ospim/compras/requerimientos/service/EditarRequerimientoCompraServiceUtil.java` | Delegar la edicion y la reasignacion sin nuevas reglas ni apertura lateral de conexion. |
| `ext-impl/src/ar/com/ospim/compras/requerimientos/service/RequerimientoCompraComparativaServiceImpl.java` | Componer lecturas de comparativa desde referencias vigentes manteniendo los valores transaccionales. |
| `ext-impl/src/ar/com/ospim/compras/requerimientos/service/RequerimientoCompraOrdenServiceImpl.java` | Componer lecturas OC con referencias actuales; los documentos ya emitidos no se regeneran. |
| `ext-impl/src/ar/com/ospim/compras/requerimientos/service/RequerimientoCompraReclamoPrestacionalServiceImpl.java` | Leer cabecera/detalles de RP desde sus referencias y contratos JDBC vigentes. |
| `ext-web/docroot/html/portlet/compras/requerimientos/partials/requerimiento_compra_afiliado_editable_componente.jsp` | Habilitar seleccion de identidad en alta/borrador con controles legacy existentes; el contacto sigue siendo una capacidad distinta. |
| `test/compras/ComprasCorreccionJavaTransaccionTest.java` | Adaptar fixture/regresion del contrato directo, conservando misma conexion y datos persistidos separados de presentacion. |
| `test/compras/ComprasDetalleSnapshotEdicionTest.java` | Regresion por lectores/Helper/UPDATE reales con frontera JDBC simulada: NOM/MED, NULL, maestro ausente y tipo incompatible. |
| `test/compras/ComprasLecturasOrdenRpTest.java` | Regresion de contratos de lectura y referencias vigentes, con fronteras claramente identificadas. |
| `test/compras/ComprasLecturasVigentesTest.java` | Regresion de contratos de lectura y referencias vigentes, con fronteras claramente identificadas. |
| `test/compras/ComprasOrdenHelperTest.java` | Adaptar fixture/regresion de OC al contrato de referencias actuales; conserva importes y documentos. |
| `test/compras/ComprasOrdenJavaTransaccionTest.java` | Adaptar fixture/regresion de OC al contrato de referencias actuales; conserva importes y documentos. |
| `test/compras/ComprasPrimeraCargaTest.java` | Verificar paginacion y busquedas iniciales con los nuevos SELECT parametrizados. |
| `test/compras/ComprasReasignacionAfiliadoTest.java` | Regresion focal de reasignacion, estado, auditoria, rollback y conservacion de adjuntos. |
| `test/compras/auditoria_secuencia_test.py` | Prueba PostgreSQL real de propiedad/default de secuencia, repeticion y siete rechazos de precondicion. |
| `test/compras/detalle_snapshot_edicion_test.ps1` | Runner focal de preservacion de snapshots, fuente/target 1.5 y evidencia aislada. |
| `test/compras/fixtures/datos/ComprasFuenteDatosFixture.java` | Adaptar fixture/regresion del contrato directo, conservando misma conexion y datos persistidos separados de presentacion. |
| `test/compras/fixtures/efectos-transaccion/ar/com/ospim/compras/requerimientos/service/BusquedaRequerimientoCompraServiceUtil.java` | Adaptar el stub de lecturas TX al contrato persistido, verificando la conexion recibida sin abrir DB. |
| `test/compras/fixtures/lecturas-orden-rp/ar/com/ospim/util/ConnectionHelper.java` | Frontera JDBC de prueba que verifica conexion esperada e impide aperturas reales de DB. |
| `test/compras/lecturas_orden_rp_test.ps1` | Regresion de contratos de lectura y referencias vigentes, con fronteras claramente identificadas. |
| `test/compras/lecturas_vigentes_sql_test.py` | Ejecutar SELECT reales PostgreSQL: maestros actuales para presentar y snapshots persistidos para editar. |
| `test/compras/lecturas_vigentes_test.ps1` | Regresion de contratos de lectura y referencias vigentes, con fronteras claramente identificadas. |
| `test/compras/reasignacion_afiliado_test.ps1` | Regresion focal de reasignacion, estado, auditoria, rollback y conservacion de adjuntos. |
| `docs/sql/compras/20261102_certificacion_escritores_QA.md` | Matriz y evidencia real del impedimento para retirar efectos SQL. |
| `docs/sql/compras/20261102_cierre_QA_local.md` | Este informe: estado real implementado, desplegado, pruebas, errores y pendientes. |
