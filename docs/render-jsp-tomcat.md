# Render de JSP grandes en el entorno local

## Entorno y alcance

- Java: `C:\Program Files\Java\jdk1.8.0_251`.
- Tomcat: `C:\apache-tomcat-8.5.23`.
- Ant: `C:\wsmolineros\tools\apache-ant-1.10.17`.
- Portal: `http://127.0.0.1:8080/web/guest`.
- Repositorio: `C:\wsmolineros\ext`, rama `main`.

Este ajuste pertenece al Tomcat local. El archivo de configuracion no forma
parte del repositorio y no se instala automaticamente mediante un push o un
build Java. Esta guia permite reproducir el cambio sin versionar configuraciones
completas de la maquina ni credenciales.

## Defecto y referencia legacy

Jasper puede fallar al compilar una vista con el mensaje:

```text
The code of method _jspService(HttpServletRequest, HttpServletResponse)
is exceeding the 65535 bytes limit
```

El limite corresponde al bytecode de un metodo Java. Aumentar el heap no lo
amplia. El tamano del archivo `.java` generado y el numero de llamadas a
`out.write` sirven como diagnostico, pero no representan el tamano del bytecode.

Vista observada:
`ext-web/docroot/html/portlet/autorizaciones/reclamos_prestacionales/view_reclamo.jsp`.

Referencia de composicion conservada:
`ext-web/docroot/html/portlet/autorizaciones/reclamos_prestacionales/editar_reclamosprestacionales_entry.jsp`,
bloque `liferay-util:include` de `view_reclamo.jsp`, con parametro `cmd`.

La correccion de configuracion conserva JSP, HTML, estilos, parametros,
namespace, permisos, validaciones y contexto del reclamo.

## Configuracion de Jasper

En `C:\apache-tomcat-8.5.23\conf\web.xml`, dentro del servlet cuyo nombre es
`jsp` y cuya clase es `org.apache.jasper.servlet.JspServlet`, agregar una unica
vez el siguiente parametro, junto a los demas `init-param` y antes de
`load-on-startup`:

```xml
<init-param>
    <param-name>mappedfile</param-name>
    <param-value>false</param-value>
</init-param>
```

El valor predeterminado `true` genera una instruccion por linea de texto
estatico para facilitar el seguimiento del Java generado. `false` permite
agrupar ese texto y reducir las instrucciones conservando su contenido.
Su alcance comprende los JSP compilados con esta configuracion de Tomcat.
No elimina el limite de la JVM ni garantiza que cualquier JSP grande compile.

Guardar explicitamente como ISO-8859-1 sin BOM. En la configuracion validada,
todos los bytes son ASCII, compatibles tambien con la declaracion XML UTF-8
existente. Si otro entorno contiene texto no ASCII, revisar la coherencia entre
bytes y declaracion XML antes de aplicar el cambio.

## Aplicacion y verificacion

1. Registrar fecha/hora, HEAD, estado inicial de la vista y segmento inicial del
   log `C:\apache-tomcat-8.5.23\logs\portalmolineros.log`.
2. Reproducir el defecto en una sesion autenticada del portal y comprobar que el
   JSP desplegado coincide con el fuente esperado.
3. Conservar una copia de respaldo del XML antes de modificarlo. Comparar el
   resultado: solo deben agregarse las cuatro lineas del parametro.
4. Ejecutar las acciones configuradas `Detener Tomcat` e `Iniciar Tomcat`.
   Comprobar tambien que termine la JVM anterior: el cierre de 8080 por si solo
   no prueba que el proceso Java haya finalizado. No iniciar una segunda
   instancia ni finalizar procesos por la fuerza sin autorizacion expresa.
5. Con el portal disponible, recargar la vista y comprobar que Jasper genere su
   clase, que aparezcan los controles esperados y que el log no repita el error.
6. Verificar URL, requests/respuestas, consola y estado final. Una respuesta
   HTTP 200 del portal o un build Java exitoso no prueban por si solos el render
   de un JSP incluido. Probar persistencia cuando la prueba funcional la requiera.

No eliminar `logs`, `temp`, `work` ni `webapps/ROOT`. Un reinicio puede requerir
recuperar la sesion de usuario. No almacenar credenciales ni tokens de sesion
como parte de la evidencia.

## Reversion

Conservar los cambios posteriores que pudiera tener `web.xml`. Para revertir
solo este ajuste, retirar el bloque `mappedfile=false` agregado y repetir la
parada y el arranque controlados. Comparar con el respaldo antes de reemplazar
un archivo completo. El valor predeterminado vuelve a ser `true` y el defecto
original puede reaparecer.

## Compatibilidad de los eventos del editor

La carga real identifico jQuery 1.2.6 y un error adicional:
`TypeError: jQuery(...).on is not a function`.

Los tres eventos de fecha de edicion se registran mediante `.change()` para
dia/mes y `.blur()` para anio en el script final de
`datos_edicion_prestacion.jsp`. Se retiran los registros `.on()` de
`view_reclamo.jsp`. La funcion existente
`actualizarAfiliadoPorFechaPrestacionEdicion` y sus parametros se conservan.

El fragmento vuelve a registrar los eventos sobre sus propios controles despues
de cada carga AJAX mediante `.load()`. Esta ubicacion evita enlazarlos solo a
los elementos de la primera carga, que el editor reemplaza posteriormente.

Referencias adaptadas: `view_reclamo.jsp`, eventos de fecha de alta y farmacia;
y `liquidaciones/comprobantes/comprobantes_general_edit.jsp`, eventos de dia y
mes de recepcion del comprobante.

## Resultado verificado el 2026-09-07

- Build Java focalizado aprobado en ext-service, ext-impl y ext-web con Java 8.
- Jasper compilo los JSP modificados en Tomcat 8.5.23.
- `_jspService` de `view_reclamo.jsp`: 43954 bytes de bytecode, inferior a 65536.
- Java generado final: 29676 lineas y 1398 llamadas `out.write`; inicialmente
  habia 34859 lineas y 6437 llamadas, y la clase no compilaba.
- Recarga personal en Chrome: HTTP 200; cabecera, afiliado, prestaciones,
  revision, dictamen y datos de OP renderizados.
- Editor abierto por AJAX; un manejador por evento al reabrirlo.
- Cambios de dia, mes y salida del campo anio: consultas de afiliado con la
  fecha correspondiente y HTTP 200. Sin errores nuevos de consola en estas
  pruebas. Se cancelo la edicion de prueba sin guardar el reclamo.
- Persistencia de un reclamo nuevo y build completo: no probados en esta tarea.

El log tambien registro `Forward does not exist` en llamadas auxiliares y un
`NullPointerException` de `AgendadoJava30minScheduler` durante el arranque.
No reaparecio el error de 65535 bytes en las cargas verificadas. Los otros
mensajes quedan como limitaciones observadas; no se declara validacion integral
ni aptitud para produccion a partir de estas pruebas focalizadas.

La parada ordenada cerro los conectores, pero la JVM anterior quedo retenida
por hilos no daemon de Quartz. Se finalizo exclusivamente ese proceso despues
de obtener autorizacion expresa y luego se inicio una sola instancia. No se
cambiaron los componentes de Quartz ni la politica permanente de parada.

## SQL de Compras

`ext-impl/src/ar/com/ospim/compras/sql/compras_schema.sql` conserva su
codificacion ISO-8859-1 y ya no incluye el metacomando de psql `\encoding LATIN1`.
Al cargarlo con un cliente SQL, seleccionar explicitamente la codificacion
correcta. Para ejecutarlo con psql, configurar `PGCLIENTENCODING=LATIN1` en esa
sesion de ejecucion. La retirada del metacomando no convierte el archivo a UTF-8.
No ejecutar la instalacion completa sobre una base existente como prueba del
render JSP.

## Fuentes

- [Apache Tomcat 8.5 - Jasper](https://tomcat.apache.org/tomcat-8.5-doc/jasper-howto.html).
- [Java 8 - Code attribute](https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-4.7.3).
- [PostgreSQL 9.6 - Character set support](https://www.postgresql.org/docs/9.6/multibyte.html).
