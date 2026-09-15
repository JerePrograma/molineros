// Pruebas ES5 con jjs de JDK 8 y las clases compiladas por el build configurado.
// Argumentos: directorio temporal, modelo PMO, script de conexion opcional.
// La integracion SQL redirige SOLO las cuatro tablas Admifarm a pg_temp.
// Las transacciones, sentencias, parametros y validaciones productivas se ejecutan completas.
var H = Java.type('ar.com.ospim.farmaciaOspim.helper.VademecumAdmifarmHelper');
var I = Java.type('ar.com.ospim.farmaciaOspim.beans.ImportacionVademecumAdmifarm');
var R = Java.type('ar.com.ospim.farmaciaOspim.beans.ImportacionVademecumAdmifarm.Registro');
var Reporte = Java.type('ar.com.ospim.farmaciaOspim.reportes.GeneraVademecumAdmifarmXLS');
var Servicio = Java.type('ar.com.ospim.farmaciaOspim.services.VademecumAdmifarmServiceImpl');
var Decimal = Java.type('java.math.BigDecimal');
var Lista = Java.type('java.util.ArrayList');
var File = Java.type('java.io.File');
var HSSFWorkbook = Java.type('org.apache.poi.hssf.usermodel.HSSFWorkbook');
var FileOutputStream = Java.type('java.io.FileOutputStream');
var Timestamp = Java.type('java.sql.Timestamp');
var pruebas = 0;
var directorio = String(arguments[0]);
function verificar(condicion, nombre) {
    if (!condicion) { throw new Error('FALLO: ' + nombre); }
    pruebas++;
    print('OK: ' + nombre);
}
function rechaza(accion, texto, nombre) {
    var rechazado = false;
    try { accion(); } catch (e) { rechazado = String(e).indexOf(texto) >= 0; }
    verificar(rechazado, nombre);
}
function fila(id, nombre, tipo) {
    var datos = tipo == 'ampliado' ? [nombre, 'PRESENTACION', 'ACCION', 'DROGA', 'LAB', 'VENTA'] : [nombre, 'DROGA', 'PRESENTACION', 'ACCION', 'LAB'];
    return new R(new Decimal(String(id)), Java.to(datos, 'java.lang.String[]'));
}
function lista(filas) {
    var resultado = new Lista();
    for (var i = 0; i < filas.length; i++) { resultado.add(filas[i]); }
    return resultado;
}
function excel(nombre, tipo, filas, formula) {
    var wb = new HSSFWorkbook();
    try {
        var sh = wb.createSheet('Vademecum');
        var cols = I.getColumnas(tipo);
        var encabezado = sh.createRow(0);
        for (var c = 0; c < cols.length; c++) { encabezado.createCell(c).setCellValue(String(cols[c])); }
        for (var f = 0; f < filas.length; f++) {
            var row = sh.createRow(f + 1);
            row.createCell(0).setCellValue(String(filas[f].getRegistro().toPlainString()));
            for (var c = 0; c < filas[f].getValores().length; c++) { row.createCell(c + 1).setCellValue(String(filas[f].getValores()[c])); }
        }
        sh.createRow(filas.length + 2); // Fila final vacia: no incrementa el conteo.
        if (formula) { sh.getRow(1).getCell(1).setCellFormula('1+1'); }
        var file = new File(directorio, nombre);
        var out = new FileOutputStream(file);
        try { wb.write(out); } finally { out.close(); }
        return file;
    } finally { wb.close(); }
}
var antes = lista([fila('1.0', 'IGUAL', 'pmo'), fila('2', 'ANTERIOR', 'pmo'), fila('3', 'BAJA', 'pmo')]);
var ahora = lista([fila('4', 'ALTA', 'pmo'), fila('2', 'NUEVO', 'pmo'), fila('1', 'IGUAL', 'pmo')]);
var cambios = H.comparar(antes, ahora);
verificar(cambios.getAltas().size() == 1 && cambios.getBajas().size() == 1 && cambios.getModificadosDespues().size() == 1 && cambios.getSinCambios() == 1, 'altas bajas modificaciones y sin cambios por clave numerica');
verificar(String(cambios.getBajas().get(0).getRegistro()) == '3', 'baja conserva el registro anterior');
verificar(String(cambios.getModificadosAntes().get(0).getValores()[0]) == 'ANTERIOR', 'modificacion conserva ambos valores');
verificar(H.comparar(new Lista(), ahora).getAltas().size() == 3, 'primera carga sin historico');
verificar(H.comparar(ahora, ahora).getSinCambios() == 3, 'misma carga sin novedades');
rechaza(function() { H.indexar(lista([fila('1', 'A', 'pmo'), fila('1.0', 'B', 'pmo')])); }, 'repetido', 'duplicados equivalentes 1 y 1.0');
var valido = excel('valido.xls', 'pmo', [fila('12345678901234567890', '=TEXTO', 'pmo')], false);
var lectura = H.leerArchivo(valido, 'valido.xls', 'pmo');
verificar(lectura.size() == 1 && String(lectura.get(0).getRegistro().toPlainString()) == '12345678901234567890', 'XLS preserva codigo largo y omite fila vacia');
rechaza(function() { H.leerArchivo(valido, 'valido.csv', 'pmo'); }, 'formato', 'extension invalida');
rechaza(function() { H.leerArchivo(valido, 'valido.xlsx', 'pmo'); }, 'formato Excel', 'contenido no corresponde a extension');
rechaza(function() { H.leerArchivo(valido, 'valido.xls', 'otro'); }, 'tipo', 'tipo fuera de lista permitida');
var formulas = excel('formulas.xls', 'pmo', [fila('1', 'X', 'pmo')], true);
rechaza(function() { H.leerArchivo(formulas, 'formulas.xls', 'pmo'); }, 'formula', 'rechazo de formulas');
var duplicados = excel('duplicados.xls', 'pmo', [fila('1', 'A', 'pmo'), fila('1.0', 'B', 'pmo')], false);
rechaza(function() { H.leerArchivo(duplicados, 'duplicados.xls', 'pmo'); }, 'repetido', 'duplicado del archivo no se omite');
var vacio = excel('vacio.xls', 'pmo', [], false);
rechaza(function() { H.leerArchivo(vacio, 'vacio.xls', 'pmo'); }, 'no contiene registros', 'archivo sin datos no provoca bajas masivas');
rechaza(function() { H.leerArchivo(valido, 'valido.xls', 'ampliado'); }, 'tipo_venta', 'PMO no se importa como Ampliado');
rechaza(function() { H.validarPermiso(null); }, 'permisos', 'usuario no autenticado rechazado');
var reales = H.leerArchivo(new File(String(arguments[1])), String(arguments[1]), 'pmo');
verificar(reales.size() == 2112, 'modelo PMO real: 2112 registros');
verificar(String(reales.get(0).getValores()[1]) == 'acenocumarol' && String(reales.get(0).getValores()[2]) == '1 mg comp.x 20', 'encabezados PMO reales mapean monodroga y presentacion sin invertir');
var imp = new I(); imp.setFecha(Timestamp.valueOf('2026-09-14 10:00:00.123456')); imp.setAnteriores(antes); imp.setRegistros(ahora);
var reporte = Reporte.generar('pmo', imp);
var descarga = new File(directorio, 'descarga.xls');
try {
    verificar(reporte.getNumberOfSheets() == 6 && reporte.getSheet('Bajas').getLastRowNum() == 1, 'descarga incluye historico y detalle de novedades');
    var out = new FileOutputStream(descarga); try { reporte.write(out); } finally { out.close(); }
} finally { reporte.close(); }
verificar(H.comparar(ahora, H.leerArchivo(descarga, 'descarga.xls', 'pmo')).getSinCambios() == 3, 'descarga puede reimportarse sin cambiar datos');
// Prueba del nonce con una sesion aislada, sin desactivar controles de servidor.
var atributos = {};
var PS = Java.type('javax.portlet.PortletSession');
var Proxy = Java.type('java.lang.reflect.Proxy');
var Handler = Java.type('java.lang.reflect.InvocationHandler');
var sesion = Proxy.newProxyInstance(PS.class.getClassLoader(), Java.to([PS.class], 'java.lang.Class[]'), new Handler({invoke: function(p, m, a) {
    var n = String(m.getName());
    if (n == 'getAttribute') { return atributos[String(a[0])] || null; }
    if (n == 'setAttribute') { atributos[String(a[0])] = a[1]; return null; }
    if (n == 'removeAttribute') { delete atributos[String(a[0])]; return null; }
    return null;
}}));
var token = H.obtenerToken(sesion, 99, 'pmo');
H.consumirToken(sesion, 99, 'pmo', token);
rechaza(function() { H.consumirToken(sesion, 99, 'pmo', token); }, 'ya fue enviado', 'doble envio rechazado');
var token = H.obtenerToken(sesion, 99, 'pmo');
rechaza(function() { H.consumirToken(sesion, 100, 'pmo', token); }, 'vencio', 'token no pertenece a otro usuario');
if (arguments.length >= 3) {
    load(String(arguments[2]));
    var con = conectar();
    var CH = Java.type('ar.com.ospim.util.ConnectionHelper');
    var campo = CH.class.getDeclaredField('datasource'); campo.setAccessible(true);
    var original = campo.get(null);
    var Connection = Java.type('java.sql.Connection');
    var Statement = Java.type('java.sql.Statement');
    var PDS = Java.type('com.mchange.v2.c3p0.PooledDataSource');
    function sqlTemporal(sql) { return String(sql).replace(/public\.vademecum_admifarm_/g, 'pg_temp.vademecum_admifarm_'); }
    function invocar(obj, metodo, args) {
        try { return metodo["invoke(java.lang.Object,java.lang.Object[])"](obj, args == null ? Java.to([], "java.lang.Object[]") : args); }
        catch (e) { if (e.getCause && e.getCause() != null) { throw e.getCause(); } throw e; }
    }
    function scalar(sql) { var s = con.createStatement(); try { var r = s.executeQuery(sql); try { r.next(); return String(r.getString(1)); } finally { r.close(); } } finally { s.close(); } }
    try {
        var scanner = new (Java.type('java.util.Scanner'))(new File('sql-scripts/tablas/vademecum_admifarm.sql'), 'ISO-8859-1');
        var schemaSQL;
        try { scanner.useDelimiter('\\A'); schemaSQL = String(scanner.next()); } finally { scanner.close(); }
        var st = con.createStatement();
        try {
            st.execute('CREATE TEMP TABLE vademecum_prueba_contexto (id integer)');
            st.execute(sqlTemporal(schemaSQL));
            st.execute(sqlTemporal(schemaSQL));
        } finally { st.close(); }
        verificar(scalar("SELECT count(*) FROM information_schema.columns WHERE table_schema LIKE 'pg_temp_%' AND table_name='vademecum_admifarm_ampliado_historico'") == '8', 'SQL suministrado crea tablas y puede aplicarse dos veces');
        var proxyConexion = Proxy.newProxyInstance(Connection.class.getClassLoader(), Java.to([Connection.class], 'java.lang.Class[]'), new Handler({invoke: function(p,m,a) {
            var n = String(m.getName());
            if (n == 'close') { return null; } // La conexion temporal pertenece a esta prueba.
            if (n == 'prepareStatement') { a[0] = sqlTemporal(a[0]); }
            var resultado = invocar(con,m,a);
            if (n == 'createStatement') {
                return Proxy.newProxyInstance(Statement.class.getClassLoader(), Java.to([Statement.class], 'java.lang.Class[]'), new Handler({invoke: function(p2,m2,a2) {
                    if (String(m2.getName()).indexOf('execute') == 0 && a2 != null && a2.length > 0 && a2[0] instanceof Java.type('java.lang.String')) { a2[0] = sqlTemporal(a2[0]); }
                    return invocar(resultado,m2,a2);
                }}));
            }
            return resultado;
        }}));
        var pool = Proxy.newProxyInstance(PDS.class.getClassLoader(), Java.to([PDS.class], 'java.lang.Class[]'), new Handler({invoke: function(p,m,a) {
            if (String(m.getName()) == 'getConnection') { return proxyConexion; }
            if (String(m.getName()) == 'getNumBusyConnections') { return new (Java.type('java.lang.Integer'))(0); }
            return null;
        }}));
        campo.set(null, pool);
        var srv = new Servicio();
        var primera = srv.importar('pmo', antes);
        var segunda = srv.importar('pmo', ahora);
        verificar(srv.getImportaciones('pmo').size() == 2, 'PostgreSQL conserva dos historicos completos');
        verificar(segunda.getFecha().after(primera.getFecha()), 'fechas de importacion distintas y ordenadas');
        var recuperada = srv.getImportacion('pmo', primera.getFecha());
        verificar(H.comparar(antes, recuperada.getRegistros()).getSinCambios() == 3, 'descarga antigua inmutable tras nueva carga');
        var recuperada = srv.getImportacion('pmo', segunda.getFecha());
        verificar(H.comparar(recuperada.getAnteriores(), recuperada.getRegistros()).getBajas().size() == 1, 'descarga compara con el inmediato historico anterior');
        verificar(scalar('SELECT count(*) FROM pg_temp.vademecum_admifarm_pmo WHERE registro=3') == '0', 'registro de baja ya no esta vigente');
        var st = con.createStatement(); try { st.execute("ALTER TABLE pg_temp.vademecum_admifarm_pmo ADD CHECK (nombre <> 'RECHAZAR')"); } finally { st.close(); }
        var fallo = false;
        try { srv.importar('pmo', lista([fila('9', 'RECHAZAR', 'pmo')])); } catch(e) { fallo = true; }
        verificar(fallo && srv.getImportaciones('pmo').size() == 2 && scalar('SELECT count(*) FROM pg_temp.vademecum_admifarm_pmo') == '3', 'rollback restaura vigente e historico ante fallo posterior al DELETE');
        var st = con.createStatement(); try { st.execute("INSERT INTO pg_temp.vademecum_admifarm_ampliado (registro,nombre) VALUES (90,'VIGENTE INICIAL')"); } finally { st.close(); }
        var ampliado = srv.importar('ampliado', lista([fila('91','NUEVO','ampliado')]));
        verificar(srv.getImportaciones('ampliado').size() == 2 && ampliado.getAnteriores().size() == 1, 'primera carga respalda vigente inicial sin historico');
        verificar(srv.getImportaciones('pmo').size() == 2, 'Ampliado no modifica historicos PMO');
        rechaza(function() { srv.getImportacion('pmo', Timestamp.valueOf('2000-01-01 00:00:00')); }, 'no existe', 'descarga de fecha inexistente rechazada');
        print('SQL: solo tablas temporales; conexion cerrada al finalizar.');
    } finally {
        campo.set(null, original);
        con.close();
    }
}
print('PRUEBAS_APROBADAS=' + pruebas);
