# Glosario de dominio

| Termino | Definicion | Evidencia o ambiguedad |
|---|---|---|
| Afiliado | Persona cubierta y consultada por procesos prestacionales. | Comprobado en acciones y endpoints. |
| Reclamo prestacional | Registro vinculado a prestaciones y autorizaciones. | Comprobado en vistas. |
| Prestacion | Item asistencial editable, autorizable o rechazable. | Comprobado en botones de edicion. |
| Requerimiento | Solicitud que puede originar procesos de Compras. | Inferido por contexto compartido; validar clases. |
| Compra | Proceso administrativo integrado con reclamos. | Comprobado por `WebKeysCompras`. |
| Presupuesto | Oferta o archivo asociado a un prestador. | Inferido; pendiente mapa completo. |
| Prestador | Proveedor de servicios o cotizaciones. | Inferido por modulo Compras. |
| Adjudicacion | Seleccion de prestador para un requerimiento. | Inferido; validar reglas. |
| Sector | Area organizativa del requerimiento. | Inferido; validar catalogo. |
| Surge | Origen o clasificacion del requerimiento. | Ambiguo; pendiente definicion funcional. |
| Recupero | Recuperacion economica asociada al caso. | Inferido. |
| OSPIM | Organizacion identificada por paquetes `ar.com.ospim`. | Comprobado como namespace; expansion legal pendiente. |
| Tercerizadora | Entidad externa vinculada a afiliados o cargos. | Comprobado en actions; semantica exacta pendiente. |
| Estado | Fase del proceso. | Cada modulo puede tener catalogo distinto. |
