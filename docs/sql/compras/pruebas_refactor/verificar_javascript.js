/* Prueba aislada de JS. El reemplazo de expresiones JSP no es un render de UI. */
var fs = require('fs');
var path = require('path');
var vm = require('vm');
var root = path.resolve(__dirname, '../../../..');
var carpeta = path.join(root, 'ext-web/docroot/html/portlet/compras/requerimientos/partials');
var nombres = ['requerimiento_compra_detalle_scripts_base_componente.jsp',
    'requerimiento_compra_detalle_scripts_edicion_componente.jsp',
    'requerimiento_compra_scripts_edicion_afiliado_componente.jsp'];
var edit = '';
var checks = 0;
for (var i = 0; i < nombres.length; i++) {
    var texto = fs.readFileSync(path.join(carpeta, nombres[i]), 'latin1');
    texto = texto.replace(/<% if \(puedeCotizarDetalle\) \{ %>([\s\S]*?)<% \} else \{ %>[\s\S]*?<% \} %>/g, '$1')
        .replace(/<%--[\s\S]*?--%>/g, '')
        .replace(/<portlet:namespace\s*\/>/g, 'ns_')
        .replace(/<%=[\s\S]*?%>/g, '0')
        .replace(/<%[\s\S]*?%>/g, '');
    var fragmentos = texto.match(/<script[^>]*>[\s\S]*?<\/script>/g) || [];
    for (var j = 0; j < fragmentos.length; j++) {
        var script = fragmentos[j].replace(/^<script[^>]*>/, '').replace(/<\/script>$/, '');
        new vm.Script(script, {filename:nombres[i]});
        checks++;
        if (nombres[i].indexOf('scripts_edicion_componente') >= 0) { edit += script; }
    }
}
function extraer(nombre) {
    var inicio = edit.indexOf('function ns_' + nombre + '(');
    if (inicio < 0) { throw new Error('Funcion ausente: ' + nombre); }
    var llave = edit.indexOf('{', inicio);
    var profundidad = 1;
    var fin = llave + 1;
    while (profundidad && fin < edit.length) {
        if (edit.charAt(fin) == '{') { profundidad++; }
        if (edit.charAt(fin) == '}') { profundidad--; }
        fin++;
    }
    return edit.substring(inicio, fin);
}
var contexto = vm.createContext({
    ns_tiposPrestacionDetalleCache:[{id:'6', idSector:'2', nomencladores:[10,14]},
        {id:'77',idSector:'2',nomencladores:[44]}, {id:'1',idSector:'1',nomencladores:[9]}],
    ns_getIdSectorTipoPrestacionDetalle:function() { return '2'; },
    ns_obtenerIdTipoPrestacionDetalle:function() { return '6'; },
    ns_esTipoPrestacionDetalleValidoParaSector:function(id) { return id=='6' || id=='77'; }
});
vm.runInContext(extraer('esNomencladorValidoParaTipoPrestacionDetalle') + '\n' + extraer('esTipoNomencladorPrestacionesMedicas'), contexto);
var casos = ["ns_esNomencladorValidoParaTipoPrestacionDetalle(10,'6')",
    "ns_esNomencladorValidoParaTipoPrestacionDetalle(14,'6')",
    "ns_esNomencladorValidoParaTipoPrestacionDetalle(44,'77')",
    "!ns_esNomencladorValidoParaTipoPrestacionDetalle(9,'6')",
    "!ns_esNomencladorValidoParaTipoPrestacionDetalle(9,'1')",
    "ns_esTipoNomencladorPrestacionesMedicas(44)",
    "!ns_esTipoNomencladorPrestacionesMedicas(9)"];
for (var k = 0; k < casos.length; k++) {
    if (!vm.runInContext(casos[k], contexto)) { throw new Error(casos[k]); }
    checks++;
}
console.log('OK: ' + checks + ' controles JS aislados; no acredita UI ni compilacion JSP.');
