# Preguntas abiertas

| Pregunta | Importancia | Evidencia revisada | Proximo paso |
|---|---|---|---|
| Donde se fija formalmente Liferay 5.2 y Java 8? | Alta para build y APIs. | `build.xml`, JSP y Struts. | Revisar properties de build y librerias. |
| Esta comprobado PostgreSQL 9.6? | Alta para SQL y drivers. | No se encontro evidencia remota suficiente. | Revisar dependencias y configuracion sin exponer conexiones. |
| Cual es el comando de tests focalizados? | Alta para validacion. | Build Ant detectado; suite no localizada. | Mapear directorios de tests y targets. |
| Cual es el mapa completo de Compras? | Alta para futuros cambios. | Integracion en `view_reclamo.jsp`. | Revisar paquetes `ar.com.ospim.compras` y JSP de requerimientos. |
| Que significa exactamente `surge`? | Media funcional. | Nombre usado en pantallas compartidas. | Revisar entidad, catalogo y usuarios funcionales. |
| Cuales directorios adicionales son generados? | Media. | `ext-web/tmp` comprobado. | Revisar ignores y targets Ant. |
| Cuales son las definitions Tiles de reclamos? | Alta si cambia navegacion. | Struts comprobado. | Localizar definition exacta antes de editar. |
