// Regresion Ampliado: formatos sin encabezado y compatibilidad con la descarga.
// Argumentos: directorio temporal y archivo Ampliado real opcional.
var H = Java.type('ar.com.ospim.farmaciaOspim.helper.VademecumAdmifarmHelper');
var I = Java.type('ar.com.ospim.farmaciaOspim.beans.ImportacionVademecumAdmifarm');
var Reporte = Java.type('ar.com.ospim.farmaciaOspim.reportes.GeneraVademecumAdmifarmXLS');
var File = Java.type('java.io.File');
var Out = Java.type('java.io.FileOutputStream');
var HSSF = Java.type('org.apache.poi.hssf.usermodel.HSSFWorkbook');
var XSSF = Java.type('org.apache.poi.xssf.usermodel.XSSFWorkbook');
var Timestamp = Java.type('java.sql.Timestamp');
var directorio = String(arguments[0]);
var aprobadas = 0;
function verificar(condicion, nombre) {
    if (!condicion) { throw new Error('FALLO: ' + nombre); }
    aprobadas++;
    print('OK: ' + nombre);
}
function rechaza(accion, mensaje, nombre) {
    var rechazado = false;
    try { accion(); } catch (e) { rechazado = String(e).indexOf(mensaje) >= 0; }
    verificar(rechazado, nombre);
}
function archivo(nombre, libro, filas) {
    try {
        var hoja = libro.createSheet('Hoja 1');
        for (var f = 0; f < filas.length; f++) {
            var fila = hoja.createRow(f);
            for (var c = 0; c < filas[f].length; c++) {
                var celda = fila.createCell(c);
                if (typeof filas[f][c] == 'number') { celda.setCellValue(filas[f][c]); }
                else { celda.setCellValue(String(filas[f][c])); }
            }
        }
        var destino = new File(directorio, nombre);
        var salida = new Out(destino);
        try { libro.write(salida); } finally { salida.close(); }
        return destino;
    } finally { libro.close(); }
}
var filas = [
    [53398, 'SINTROM', 'acenocumarol', '1 mg comp.x 20', 'Anticoagulante', 'Siegfried', ''],
    ['21646', 'ACEMUK', 'acetilcisteina', '600 mg tab.ef', 'Mucolitico', 'Siegfried', 'fuera de 310']
];
var xls = archivo('ampliado-sin-encabezado.xls', new HSSF(), filas);
var leidos = H.leerArchivo(xls, xls.getName(), 'ampliado');
verificar(leidos.size() == 2 && String(leidos.get(0).getRegistro().stripTrailingZeros().toPlainString()) == '53398', 'XLS incluye la primera fila sin encabezado');
var valores = leidos.get(0).getValores();
verificar(String(valores[0]) == 'SINTROM' && String(valores[1]) == '1 mg comp.x 20' && String(valores[2]) == 'Anticoagulante' && String(valores[3]) == 'acenocumarol' && String(valores[4]) == 'Siegfried', 'Ampliado conserva el orden real de las columnas A-F');
verificar(String(leidos.get(1).getValores()[5]) == 'fuera de 310', 'columna G se conserva completa en tipo_venta');
filas[0][0] = '53398';
var xlsx = archivo('ampliado-sin-encabezado.xlsx', new XSSF(), filas);
verificar(H.comparar(leidos, H.leerArchivo(xlsx, xlsx.getName(), 'ampliado')).getSinCambios() == 2, 'XLSX sin encabezado admite codigo almacenado como texto');
var importacion = new I();
importacion.setFecha(Timestamp.valueOf('2026-09-15 10:00:00.123456'));
importacion.setRegistros(leidos);
var libro = Reporte.generar('ampliado', importacion);
var descargado = new File(directorio, 'ampliado-con-encabezado.xls');
try {
    var salida = new Out(descargado);
    try { libro.write(salida); } finally { salida.close(); }
} finally { libro.close(); }
verificar(H.comparar(leidos, H.leerArchivo(descargado, descargado.getName(), 'ampliado')).getSinCambios() == 2, 'Ampliado descargado mantiene lectura por encabezados');
rechaza(function() { H.leerArchivo(xlsx, xlsx.getName(), 'pmo'); }, 'Encabezado no reconocido', 'PMO conserva su validacion de encabezados');
var sinCodigo = archivo('ampliado-primera-fila-sin-codigo.xls', new HSSF(), [['', 'NOMBRE', 'DROGA', 'PRESENTACION', 'ACCION', 'LAB', '']]);
rechaza(function() { H.leerArchivo(sinCodigo, sinCodigo.getName(), 'ampliado'); }, 'Filas: 1.', 'primera fila sin codigo se informa y no se omite');
var extra = archivo('ampliado-columna-extra.xls', new HSSF(), [[53398, 'NOMBRE', 'DROGA', 'PRESENTACION', 'ACCION', 'LAB', '', 'DATO SIN DESTINO']]);
rechaza(function() { H.leerArchivo(extra, extra.getName(), 'ampliado'); }, 'columna sin destino', 'datos fuera de A-G se rechazan');
if (arguments.length >= 2) {
    var real = new File(String(arguments[1]));
    rechaza(function() { H.leerArchivo(real, real.getName(), 'ampliado'); }, 'Hay 2 filas sin registro numerico valido. Filas: 1349, 2769.', 'modelo real supera lectura posicional e informa los dos codigos vacios');
}
print('PRUEBAS_AMPLIADO_APROBADAS=' + aprobadas);
