var fs = require('fs');
var vm = require('vm');
var assert = require('assert');
var source = fs.readFileSync(
    'ext-web/docroot/html/portlet/compras/requerimientos/requerimiento_compra_listado.jsp', 'latin1');
var script = source.substring(source.indexOf('<script type="text/javascript">') + 31, source.lastIndexOf('</script>'));
script = script.replace(/<portlet:namespace\s*\/>/g, 'ns_').replace(/<%=[\s\S]*?%>/g, '');
var checks = 0;
function check(value, label) { assert.ok(value, label); checks++; }
function tab() {
    var controls = {};
    var pending = [];
    function item(key) {
        if (!controls[key]) controls[key] = {value: '', attrs: {}, text: ''};
        var data = controls[key];
        return {
            length: 1,
            val: function(v) { if (arguments.length) { data.value = v; return this; } return data.value; },
            text: function(v) { if (arguments.length) { data.text = v; return this; } return data.text; },
            attr: function(k,v) { if (arguments.length > 1) { data.attrs[k]=v; return this; } return data.attrs[k]; },
            removeAttr: function(k) { delete data.attrs[k]; return this; },
            show: function() { return this; }, hide: function() { return this; },
            each: function() { return this; },
            load: function(url,callback) { pending.push({url:url,callback:callback}); return this; }
        };
    }
    function jq(key) {
        if (typeof key === 'function') return; // No ejecutar la inicializacion externa del portal.
        return item(key);
    }
    jq.trim = function(v) { return (v == null ? '' : String(v)).replace(/^\s+|\s+$/g, ''); };
    var context = {jQuery:jq, document:{getElementById:function(){return null;}},
        window:{location:{}}, alert:function(){}, encodeURIComponent:encodeURIComponent};
    vm.createContext(context);
    vm.runInContext(script, context);
    context.ns_validarFiltroBusqueda = function(){return true;};
    context.ns_sincronizarTercerizadoraFiltro = function(){return jq('#ns_id_tercerizadora_filtro').val();};
    context.ns_limpiarFechasAltaFiltro = function(){};
    context.ns_desbloquearTercerizadoraFiltro = function(){};
    context.ns_sincronizarAfiliadoFiltro = function(){};
    return {c:context, jq:jq, pending:pending, finish:function(index, token, status){
        if (status === 'success') jq('#ns_busquedaRequerimientosDiv .compras-exportacion-token').val(token);
        pending[index].callback('',status);
    }};
}
var a = tab();
a.c.ns_exportarRequerimientos();
check(a.jq('#ns_descargaRequerimientos').attr('src') === undefined, 'sin busqueda no descarga');
a.jq('#ns_estado').val('1');
a.c.ns_buscarRequerimientos();
check(a.jq('#ns_exportar').attr('disabled') === 'disabled', 'busqueda en curso bloquea exportar');
a.finish(0,'token-A','success');
a.jq('#ns_estado').val('99');
a.c.ns_exportarRequerimientos();
check(a.jq('#ns_descargaRequerimientos').attr('src').indexOf('token-A') >= 0, 'edicion sin buscar conserva token');
check(a.jq('#ns_descargaRequerimientos').attr('src').indexOf('estado=') < 0, 'no exporta controles editados');
a.c.ns_limpiarTodosCamposFiltro();
a.c.ns_exportarRequerimientos();
check(a.jq('#ns_descargaRequerimientos').attr('src').indexOf('token-A') >= 0, 'Limpiar sin Buscar conserva resultado');
check(a.pending.length === 1, 'Limpiar no inicia otra busqueda');

var b = tab();
b.c.ns_buscarRequerimientos(); b.finish(0,'token-B','success'); b.c.ns_exportarRequerimientos();
check(b.jq('#ns_descargaRequerimientos').attr('src').indexOf('token-B') >= 0, 'pestana B independiente');
check(a.c.ns_tokenExportacion === 'token-A', 'pestana A independiente');

a.c.ns_buscarRequerimientos(); a.c.ns_buscarRequerimientos();
a.finish(2,'respuesta-nueva','success');
check(a.jq('#ns_exportar').attr('disabled') === 'disabled', 'queda otra busqueda pendiente');
a.finish(1,'respuesta-antigua-que-quedo-visible','success');
a.c.ns_exportarRequerimientos();
check(a.jq('#ns_descargaRequerimientos').attr('src').indexOf('respuesta-antigua-que-quedo-visible') >= 0,
      'fuera de orden exporta exactamente respuesta visible');
a.c.ns_buscarRequerimientos(); a.finish(3,null,'error');
check(a.jq('#ns_exportar').attr('disabled') === 'disabled', 'error AJAX bloquea');
a.c.ns_buscarRequerimientos(); a.finish(4,'','success');
check(a.jq('#ns_exportar').attr('disabled') === 'disabled', 'HTML de error sin token bloquea');
a.c.ns_buscarRequerimientos(); a.finish(5,'busqueda-vacia-valida','success');
check(a.jq('#ns_exportar').attr('disabled') === undefined, 'cero resultados exitosos exportables');
check(source.indexOf('!estadoForzadoActivo\n                        && ExportarRequerimientosCompraHelper.puedeConsultar(user)') >= 0,
      'guard de Requerimientos independiente de alta');
console.log('EXPORTAR_REQUERIMIENTOS_UI_SIMULADA_OK checks=' + checks + ' (JavaScript real; DOM/AJAX simulado)');