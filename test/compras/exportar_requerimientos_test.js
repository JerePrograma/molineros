var fs = require('fs');
var vm = require('vm');
var assert = require('assert');
var source = fs.readFileSync(
    'ext-web/docroot/html/portlet/compras/requerimientos/requerimiento_compra_listado.jsp', 'latin1');
var script = source.substring(source.indexOf('<script type="text/javascript">') + 31, source.lastIndexOf('</script>'));
script = script.replace(/<portlet:namespace\s*\/>/g, 'ns_').replace(/<%=[\s\S]*?%>/g, '');
var checks = 0;
function check(value, label) { assert.ok(value, label); checks++; }
function tab(runReady) {
    var controls = {};
    var pending = [];
    var ready = [];
    function item(key) {
        if (!controls[key]) controls[key] = {value: '', attrs: {}, text: '', events: {}};
        var data = controls[key];
        function bind(name, handler) {
            key.split(',').forEach(function(selector) {
                selector = selector.trim();
                item(selector);
                if (!controls[selector].events[name]) controls[selector].events[name] = [];
                controls[selector].events[name].push(handler);
            });
            return element;
        }
        var element = {
            length: 1,
            val: function(v) { if (arguments.length) { data.value = v; return this; } return data.value; },
            text: function(v) { if (arguments.length) { data.text = v; return this; } return data.text; },
            attr: function(k,v) { if (arguments.length > 1) { data.attrs[k]=v; return this; } return data.attrs[k]; },
            removeAttr: function(k) { delete data.attrs[k]; return this; },
            show: function() { data.visible = true; return this; },
            hide: function() { data.visible = false; return this; },
            each: function() { return this; },
            click: function(handler) { return bind('click', handler); },
            change: function(handler) { return bind('change', handler); },
            keyup: function(handler) { return bind('keyup', handler); },
            keypress: function(handler) { return bind('keypress', handler); },
            load: function(url,callback) { pending.push({url:url,callback:callback}); return this; }
        };
        return element;
    }
    function jq(key) {
        if (typeof key === 'function') { ready.push(key); return; }
        return item(key);
    }
    jq.trim = function(v) { return (v == null ? '' : String(v)).replace(/^\s+|\s+$/g, ''); };
    var context = {jQuery:jq, document:{getElementById:function(){return null;}},
        window:{location:{}}, alert:function(){}, encodeURIComponent:encodeURIComponent};
    vm.createContext(context);
    vm.runInContext(script, context);
    context.ns_validarFiltroBusqueda = function(){return true;};
    context.ns_aplicarAntecedentesAfiliado = function(){};
    if (!runReady) {
        context.ns_sincronizarTercerizadoraFiltro = function(){return jq('#ns_id_tercerizadora_filtro').val();};
        context.ns_limpiarFechasAltaFiltro = function(){};
        context.ns_desbloquearTercerizadoraFiltro = function(){};
        context.ns_sincronizarAfiliadoFiltro = function(){};
    }
    var result = {c:context, jq:jq, pending:pending, controls:controls,
        trigger:function(key, name, event) {
            var handlers = (controls[key] && controls[key].events[name]) || [];
            var returned;
            handlers.forEach(function(handler) { returned = handler.call(item(key), event || {}); });
            return returned;
        },
        finish:function(index, token, status) {
            if (status === 'success') jq('#ns_busquedaRequerimientosDiv .compras-exportacion-token').val(token);
            pending[index].callback('',status);
        }};
    if (runReady) ready.forEach(function(callback) { callback(); });
    return result;
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
var paginado = tab();
paginado.jq('#ns_id_requerimiento_compra').val('123');
paginado.jq('#ns_estado').val('3');
paginado.jq('#ns_sector_id').val('7');
paginado.c.ns_buscarRequerimientos(3);
check(paginado.pending[0].url.indexOf('&pagina=3') >= 0, 'selector solicita pagina 3');
check(paginado.pending[0].url.indexOf('&id_requerimiento_compra=123') >= 0, 'pagina conserva ID');
check(paginado.pending[0].url.indexOf('&estado=3') >= 0, 'pagina conserva estado');
check(paginado.pending[0].url.indexOf('&sector_id=7') >= 0, 'pagina conserva sector');
check(paginado.jq('#ns_exportar').attr('disabled') === 'disabled', 'pagina en curso bloquea exportar');
paginado.finish(0, 'token-pagina-3', 'success');
paginado.c.ns_exportarRequerimientos();
check(paginado.jq('#ns_descargaRequerimientos').attr('src').indexOf('token-pagina-3') >= 0, 'pagina publica token vigente');
check(paginado.jq('#ns_descargaRequerimientos').attr('src').indexOf('pagina=') < 0, 'exportacion no recibe pagina');
paginado.c.ns_buscarRequerimientos();
check(paginado.pending[1].url.indexOf('&pagina=1') >= 0, 'nueva busqueda vuelve a primera pagina');
paginado.c.ns_buscarRequerimientos(-2);
check(paginado.pending[2].url.indexOf('&pagina=1') >= 0, 'pagina negativa vuelve a primera');
paginado.c.ns_buscarRequerimientos('invalida');
check(paginado.pending[3].url.indexOf('&pagina=1') >= 0, 'pagina invalida vuelve a primera');
var manual = tab(true);
check(manual.pending.length === 0, 'inicializacion no realiza consultas AJAX');
check(manual.controls['#ns_buscando'].visible === false, 'indicador de busqueda oculto al iniciar');
var filterValues = {
    estado:'3', sector_id:'7', surge:'true', id_tercerizadora_combo:' tercero ',
    id_requerimiento_compra:'123', cuil:' 20000000001 ', inte:'2', tipoDoc:'DNI',
    nroDoc:'12345678', apellido:'Apellido', nombre:'Nombre', id_seccional:'8',
    fechaAltaDesdeDia:'1', fechaAltaDesdeMes:'9', fechaAltaDesdeAnio:'2026',
    fechaAltaHastaDia:'26', fechaAltaHastaMes:'9', fechaAltaHastaAnio:'2026'
};
Object.keys(filterValues).forEach(function(id) {
    manual.jq('#ns_' + id).val(filterValues[id]);
    manual.trigger('#ns_' + id, 'change');
    manual.trigger('#ns_' + id, 'keyup', {which:65});
    check(manual.pending.length === 0, 'editar filtro ' + id + ' no consulta');
});
check(manual.jq('#ns_afiliado_cuil_titular').val() === '20000000001', 'change/keyup sincronizan CUIL');
check(manual.jq('#ns_afiliado_int').val() === '2', 'change/keyup sincronizan integrante');
check(manual.jq('#ns_afiliado_tipo_doc').val() === 'DNI', 'change/keyup sincronizan tipo documento');
check(manual.jq('#ns_afiliado_nro_doc').val() === '12345678', 'change/keyup sincronizan documento');
check(manual.jq('#ns_afiliado_apellido').val() === 'Apellido', 'change/keyup sincronizan apellido');
check(manual.jq('#ns_afiliado_nombre').val() === 'Nombre', 'change/keyup sincronizan nombre');
check(manual.jq('#ns_afiliado_id_seccional').val() === '8', 'change/keyup sincronizan seccional');
check(manual.jq('#ns_id_tercerizadora_filtro').val() === 'TERCERO', 'change sincroniza tercerizadora');
['id_requerimiento_compra','cuil','inte','tipoDoc','nroDoc','apellido','nombre'].forEach(function(id) {
    check(manual.trigger('#ns_' + id, 'keypress', {which:13}) === false, 'Enter en ' + id + ' evita submit');
    check(manual.pending.length === 0, 'Enter en ' + id + ' no consulta');
});
check(manual.trigger('#ns_nombre', 'keypress', {which:65}) === true, 'otras teclas siguen permitidas');
manual.trigger('#ns_buscar', 'click');
check(manual.pending.length === 1, 'boton Buscar realiza exactamente una consulta');
check(manual.controls['#ns_buscando'].visible === true, 'boton Buscar muestra indicador');
var manualUrl = manual.pending[0].url;
var expectedParams = {
    pagina:'1', estado:'3', sector_id:'7', surge:'true', id_tercerizadora:'TERCERO',
    id_requerimiento_compra:'123', afiliado_cuil_titular:'20000000001', afiliado_int:'2',
    afiliado_tipo_doc:'DNI', afiliado_nro_doc:'12345678', afiliado_apellido:'Apellido',
    afiliado_nombre:'Nombre', afiliado_id_seccional:'8',
    fechaAltaDesdeDia:'1', fechaAltaDesdeMes:'9', fechaAltaDesdeAnio:'2026',
    fechaAltaHastaDia:'26', fechaAltaHastaMes:'9', fechaAltaHastaAnio:'2026'
};
Object.keys(expectedParams).forEach(function(name) {
    check(manualUrl.indexOf('&' + name + '=' + encodeURIComponent(expectedParams[name])) >= 0,
          'Buscar envia valor actual de ' + name);
});
manual.finish(0, 'token-busqueda-manual', 'success');
check(manual.controls['#ns_buscando'].visible === false, 'respuesta oculta indicador');
manual.jq('#ns_estado').val('9');
manual.trigger('#ns_estado', 'change');
check(manual.pending.length === 1, 'editar tras buscar no repite consulta');
check(manual.c.ns_tokenExportacion === 'token-busqueda-manual', 'editar mantiene token del resultado visible');
manual.c.ns_limpiarTodosCamposFiltro();
check(manual.pending.length === 1, 'Limpiar con eventos registrados no consulta');
check(manual.jq('#ns_fechaAltaDesdeDia').val() === '', 'Limpiar limpia fecha desde');
check(manual.jq('#ns_fechaAltaHastaAnio').val() === '', 'Limpiar limpia fecha hasta');
check(manual.jq('#ns_id_tercerizadora_filtro').val() === '', 'Limpiar limpia tercerizadora');
check(manual.jq('#ns_afiliado_cuil_titular').val() === '', 'Limpiar limpia afiliado sincronizado');
console.log('EXPORTAR_REQUERIMIENTOS_UI_SIMULADA_OK checks=' + checks + ' (JavaScript real; DOM/AJAX simulado)');