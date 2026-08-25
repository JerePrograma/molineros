# MOLINEROS - INSTRUCCIONES OBLIGATORIAS PARA CODEX

## 0. ALCANCE Y PRIORIDAD

Este archivo define las reglas permanentes del repositorio Molineros.

Codex debe leerlo antes de inspeccionar, modificar, compilar, desplegar, probar,
hacer commit o publicar cambios.

Si una tarea pide algo incompatible con estas reglas, Codex debe detenerse,
explicar el conflicto y solicitar confirmacion expresa antes de continuar.

No interpretar el silencio como autorizacion para ampliar alcance, modernizar,
refactorizar o modificar contratos legacy.

## 1. REPOSITORIO, RUTA Y RAMA OBLIGATORIOS

Repositorio:

    https://github.com/JerePrograma/molineros.git

Ruta local unica autorizada:

    C:\wsmolineros\ext

Rama de trabajo y publicacion:

    main
    origin/main

Antes de cualquier inspeccion Git o modificacion ejecutar:

    Set-Location -LiteralPath 'C:\wsmolineros\ext'

    $root = (git rev-parse --show-toplevel).Trim()
    $origin = (git remote get-url origin).Trim()

    if ($LASTEXITCODE -ne 0 -or
        $root -ne 'C:/wsmolineros/ext' -or
        $origin -ne 'https://github.com/JerePrograma/molineros.git') {
        throw "Repositorio o ruta incorrectos. No modificar archivos ni remotos."
    }

No operar sobre otro clon, worktree, ruta, repositorio o remoto.

No crear ramas ni pull requests salvo pedido expreso.

## 2. BASELINE GIT Y PRESERVACION DE CAMBIOS LOCALES

Antes de modificar archivos ejecutar:

    git status --short
    git branch --show-current
    git rev-parse HEAD
    git log -1 --oneline
    git fetch origin --prune
    git switch main
    git merge --ff-only origin/main

Si existen cambios locales:

- preservarlos;
- identificar las rutas exactas;
- no descartarlos;
- no sobrescribirlos;
- no ocultarlos;
- detenerse si interfieren con la tarea.

Prohibido:

    git reset --hard
    git clean -fd
    git checkout -- .
    git restore .
    git stash
    git rebase
    git push --force
    git push --force-with-lease
    git add .
    git add -A

No usar operaciones destructivas equivalentes.

## 3. PRINCIPIO LEGACY FIRST

Molineros es una aplicacion legacy. La compatibilidad existente tiene prioridad
sobre modernizacion, preferencias personales o patrones contemporaneos.

Mantener compatibilidad con:

- Java 8 como runtime;
- sintaxis aceptada por el source/target real del build Ant legacy;
- Liferay 5.2;
- JSP legacy;
- Struts existente;
- Tiles existente;
- JavaScript ES5;
- jQuery legacy;
- PostgreSQL y funciones existentes;
- ISO-8859-1 sin BOM.

No introducir:

- lambdas;
- streams;
- Optional;
- var;
- records;
- switch expressions;
- text blocks;
- diamond operator si el build actual no lo acepta;
- try-with-resources si el build actual no lo acepta;
- fetch;
- Promises;
- async/await;
- arrow functions;
- let o const;
- template literals;
- frameworks nuevos;
- dependencias nuevas;
- APIs modernas de Liferay;
- cambios generales de arquitectura.

Antes de usar una construccion Java o JavaScript, confirmar que ya existe en el
proyecto y que el build focalizado la acepta.

## 4. REGLA OBLIGATORIA DE REFERENCIA Y ADAPTACION

No crear una solucion desde cero cuando exista un patron legacy equivalente.

Antes de crear o redisenar cualquier:

- clase;
- metodo;
- helper;
- service;
- service impl;
- Action;
- bean o DTO;
- JSP;
- bloque visual;
- funcion JavaScript;
- validacion;
- consulta;
- funcion PostgreSQL;
- correo;
- PDF;
- adjunto;
- flujo Document Library;
- boton;
- icono;
- tabla;
- mensaje;
- navegacion;
- forward;
- control de permisos;

Codex debe localizar una implementacion analoga real y nombrarla de forma
explicita.

Orden obligatorio de busqueda:

1. Implementacion equivalente ya existente en el mismo modulo.
2. Implementacion equivalente en Liquidaciones.
3. Implementacion equivalente en Autorizaciones.

Rutas principales de referencia:

    ext-impl/src/ar/com/ospim/liquidaciones
    ext-web/docroot/html/portlet/liquidaciones
    ext-impl/src/ar/com/ospim/autorizaciones
    ext-web/docroot/html/portlet/autorizaciones

No auditar esos modulos completos.

Usar busqueda focalizada por concepto, clase, metodo, parametro, etiqueta,
mensaje o comportamiento requerido.

Antes de la primera modificacion, registrar:

    REFERENCIA LEGACY ELEGIDA
    Ruta exacta:
    Clase/JSP/metodo/funcion:
    Comportamiento reutilizado:
    Diferencias necesarias:
    Motivo por el que aplica:

Una implementacion nueva no debe comenzar hasta identificar al menos una
referencia concreta de Liquidaciones o Autorizaciones para validar estructura,
estilo, validaciones o presentacion.

Si no existe una referencia razonablemente equivalente:

- no inventar una arquitectura;
- no ampliar la busqueda de forma indiscriminada;
- detenerse;
- informar que no se encontro patron;
- solicitar una decision al usuario.

Adaptar el patron. No copiarlo ciegamente.

No crear dependencias runtime entre modulos solo para reutilizar codigo. Reusar
la convencion y adaptar el minimo necesario dentro del modulo destino, salvo que
la dependencia ya exista y sea parte del contrato vigente.

## 5. ALCANCE MINIMO Y PROHIBICION DE SOBREINGENIERIA

Analizar y modificar solo:

- archivos nombrados por la tarea;
- callers directos indispensables;
- contratos directos;
- tests focalizados;
- dependencias directas necesarias para compilar o probar.

No hacer:

- auditorias generales;
- cleanup;
- modernizacion;
- renombrados generales;
- refactorizaciones preventivas;
- extraccion de abstracciones para un solo caso;
- nuevas capas sin necesidad demostrada;
- nuevos helpers genericos para un unico caller;
- reformateo completo de archivos;
- correccion de warnings ajenos;
- cambios esteticos no solicitados;
- cambios en modulos no relacionados.

No inspeccionar Struts, Tiles o configuraciones globales salvo que exista una
incompatibilidad directa y demostrada con la ruta afectada.

El cambio correcto es el diff minimo que resuelve el caso completo y conserva
todo comportamiento no relacionado.

## 6. FRONTERAS DE CAPAS

Respetar la arquitectura ya consolidada del modulo afectado.

Para codigo nuevo o modificado en Compras:

- JSP: presentacion y composicion legacy;
- JavaScript: comportamiento de interfaz ES5;
- Action: adaptacion HTTP, permisos y coordinacion;
- Helper: validacion y reglas funcionales;
- ServiceUtil: delegacion;
- ServiceImpl: JDBC, conexion, CALL y mapeo;
- PostgreSQL: persistencia y operaciones atomicas cuando el contrato vigente
  ya usa funciones.

No agregar SQL a JSP, JavaScript, Action o Helper.

No agregar reglas funcionales nuevas dentro de ServiceImpl si existe la
separacion Action/Helper.

No mover logica entre capas como parte de una correccion puntual salvo que la
tarea lo exija expresamente.

Conservar firmas, parametros, IDs, atributos de request o sesion, URLs,
struts_action, forwards, nombres de botones y contratos publicos.

## 7. ESTRUCTURA Y ESTETICA LEGACY

Toda interfaz nueva o modificada debe partir de una pantalla analoga de
Liquidaciones o Autorizaciones.

Antes de escribir HTML, JSP, CSS o JavaScript indicar la vista de referencia
exacta.

Priorizar reutilizacion de:

- fieldset y legend existentes;
- block-labels;
- lfr-table;
- taglibs Liferay existentes;
- portlet:namespace;
- clases CSS existentes;
- iconos ya usados por el portal;
- botones y orden visual existentes;
- patrones de popup, selector, grilla y mensaje existentes.

No introducir frameworks CSS.

No introducir componentes visuales modernos aislados.

No usar Flexbox o Grid como reemplazo general si la pantalla de referencia no
los utiliza.

No crear una estetica propia para Compras cuando ya existe una convencion en
Liquidaciones o Autorizaciones.

No deformar pantallas existentes para reutilizar un componente.

Mantener:

- anchos;
- alineaciones;
- espaciados;
- etiquetas;
- orden de campos;
- comportamiento de botones;
- estados disabled y readonly;
- navegacion por teclado razonable;
- namespace Liferay;
- compatibilidad con el navegador del entorno legacy.

## 8. VALIDACIONES Y SEGURIDAD

La validacion de servidor es autoritativa.

La validacion JavaScript solo mejora experiencia; no reemplaza la validacion de
servidor.

Preservar y verificar:

- permisos;
- roles;
- identidad del usuario;
- token o nonce;
- prevencion de doble envio;
- estado vigente;
- concurrencia;
- transacciones;
- compensacion de documentos;
- validacion de parametros;
- fail-closed;
- AJAX y contratos legacy;
- idempotencia existente.

No:

- ocultar excepciones;
- usar catch vacios;
- devolver exito falso;
- hardcodear emails, usuarios o contrasenas;
- saltar validaciones para que una prueba pase;
- modificar datos de base como atajo;
- exponer stack traces al usuario;
- registrar secretos o datos sensibles.

Diferenciar errores de:

- datos;
- validacion;
- permisos;
- persistencia;
- SMTP;
- Document Library;
- infraestructura;
- concurrencia.

## 9. CODIFICACION OBLIGATORIA

Todo archivo de texto creado o modificado debe guardarse explicitamente como:

    ISO-8859-1
    sin BOM

No depender del encoding por defecto de editor, PowerShell, Java, Python o IDE.

Antes del commit verificar por cada archivo de texto:

1. decodifica como ISO-8859-1;
2. no tiene BOM UTF-8;
3. no tiene BOM UTF-16 LE;
4. no tiene BOM UTF-16 BE;
5. no contiene los caracteres U+00C3 o U+00C2 usados comunmente en mojibake;
6. no contiene U+FFFD;
7. tildes y enies se leen correctamente;
8. el diff no muestra recodificacion masiva.

Este AGENTS.md usa deliberadamente solo caracteres ASCII para que sus bytes sean
validos tanto al leerlos como texto ASCII como al guardarlo en ISO-8859-1.

## 10. BUILD, DEPLOY Y TOMCAT

Usar exclusivamente el entorno configurado para Molineros:

    JAVA_HOME=C:\Program Files\Java\jdk1.8.0_251
    ANT_HOME=C:\wsmolineros\tools\apache-ant-1.10.17
    CATALINA_HOME=C:\apache-tomcat-8.5.23
    CATALINA_BASE=C:\apache-tomcat-8.5.23

No ejecutar:

    ant clean

No eliminar automaticamente:

    C:\apache-tomcat-8.5.23\logs
    C:\apache-tomcat-8.5.23\temp
    C:\apache-tomcat-8.5.23\work
    C:\apache-tomcat-8.5.23\webapps\ROOT

Antes de iniciar Tomcat confirmar que 8080 no tenga un listener.

Si 8080 esta ocupado, identificar PID, ejecutable y CommandLine.

No iniciar una segunda instancia.

No matar procesos a ciegas.

Para detener Tomcat usar shutdown.bat y no force-kill, salvo autorizacion
expresa.

Usar las acciones configuradas:

- Diagnostico Molineros;
- Build Java focalizado;
- Estado Tomcat;
- Iniciar Tomcat;
- Detener Tomcat;
- Ver log;
- Errores recientes;
- Abrir portal.

No inventar comandos de build.

## 11. PRUEBAS OBLIGATORIAS

Primero reproducir el defecto. Despues modificar.

Antes de probar registrar:

- fecha y hora de inicio;
- HEAD;
- estado funcional inicial;
- segmento inicial del log.

Ejecutar pruebas focalizadas del codigo afectado.

Cuando corresponda, ejecutar navegacion manual real en:

    http://127.0.0.1:8080/web/guest

Usar una sesion autenticada ya preparada por el usuario.

No almacenar credenciales en:

- AGENTS.md;
- .codex;
- scripts;
- prompts persistentes;
- archivos del repositorio;
- logs;
- commits.

Una prueba UI solo puede declararse aprobada si Codex ejecuto personalmente la
navegacion y verifico:

- URL;
- controles;
- requests;
- respuesta;
- persistencia;
- estado final;
- consola;
- log de backend.

Si otra persona ejecuta los clics, informar que fue una ejecucion externa.

Si no hay control de navegador, indicar:

    PRUEBA UI MANUAL PENDIENTE

No afirmar end-to-end sin evidencia end-to-end.

## 12. PROTOCOLO ANTES DE EDITAR

Antes de modificar una sola linea, Codex debe producir un resumen operativo con:

    Alcance exacto:
    Defecto reproducido:
    Archivos candidatos:
    Referencia Liquidaciones/Autorizaciones:
    Contrato que debe preservarse:
    Diff minimo previsto:
    Pruebas previstas:
    Riesgos:

Si la causa no esta demostrada, no modificar por especulacion.

## 13. REVISION DESPUES DEL CAMBIO

Ejecutar como minimo:

    git diff --check
    git status --short
    git diff --stat
    git diff -- <rutas-exactas-modificadas>

Leer el diff completo.

Comprobar:

- ausencia de cambios accidentales;
- ausencia de recodificacion;
- ausencia de debug temporal;
- ausencia de imports innecesarios;
- ausencia de hardcodes;
- ausencia de bypasses;
- preservacion de contratos;
- correspondencia con la referencia legacy elegida.

Ejecutar nuevamente las pruebas afectadas despues de cualquier merge o ajuste.

## 14. COMMIT Y PUSH

Agregar solo rutas exactas:

    git add -- <ruta-exacta-1> <ruta-exacta-2>

Revisar:

    git status --short
    git diff --cached --check
    git diff --cached --stat
    git diff --cached

Crear un commit detallado en espanol.

Antes del push:

    git fetch origin --prune

Si origin/main avanzo, integrar mediante merge. Nunca rebase.

Si hay conflicto no trivial, detenerse.

Publicar:

    git push origin main:main

Verificar:

    git rev-parse main
    git rev-parse origin/main
    git merge-base --is-ancestor <commit-creado> origin/main
    git status --short --branch

main y origin/main deben finalizar en el mismo SHA.

Nunca usar force-push.

## 15. INFORME FINAL OBLIGATORIO

Informar:

- repositorio;
- ruta local;
- rama;
- HEAD inicial;
- HEAD final;
- referencia legacy utilizada;
- archivos modificados;
- motivo preciso por archivo;
- comportamiento corregido;
- comportamiento no tocado;
- encoding y metodo de verificacion por archivo;
- comandos ejecutados;
- exit code de cada comando;
- pruebas aprobadas;
- pruebas fallidas;
- fallos preexistentes;
- pruebas no ejecutadas;
- limitaciones del entorno;
- commit;
- push;
- SHA local;
- SHA remoto;
- riesgos residuales.

No afirmar que el cambio esta listo para produccion sin build completo y smoke
tests reales.

## 16. REGLA FINAL

Priorizar siempre:

1. evidencia;
2. compatibilidad legacy;
3. referencia real en Liquidaciones o Autorizaciones;
4. cambio minimo;
5. validacion focalizada;
6. preservacion de comportamiento;
7. trazabilidad.

No priorizar velocidad aparente sobre consistencia.

No hacer que una pantalla pase ocultando un defecto.

No inventar soluciones cuando el repositorio ya contiene el patron correcto.
