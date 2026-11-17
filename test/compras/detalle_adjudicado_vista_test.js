/* Ejecuta la funcion real de tabla con ramas JSP simuladas, sin navegador. */
var fs = require('fs');
var vm = require('vm');
var assert = require('assert');
var path = require('path');
var repo = path.resolve(__dirname, '../..');
var jsp = fs.readFileSync(path.join(repo, 'ext-web/docroot/html/portlet/compras/requerimientos/partials/requerimiento_compra_detalle_scripts_base_componente.jsp'), 'latin1');
var start = jsp.indexOf('    function <portlet:namespace />renderDetallesCompra()');
var end = jsp.indexOf('    jQuery(function()', start);
assert(start >= 0 && end > start, 'Funcion de tabla localizada');
var render = jsp.substring(start, end).replace(/<portlet:namespace \/>/g, 't_');
render = render.replace(/<%=([\s\S]*?)%>/g, function(all, expr) {
    if (expr.indexOf('detalleColspan') >= 0) return '8';
    if (expr.indexOf('themeDisplay') >= 0) return '/theme';
    if (expr.indexOf('puedeABMDetalle ? "Corregir"') >= 0) return 'Editar';
    return "' + (" + expr.replace(/^\s+|\s+$/g, '') + ") + '";
});
render = render.replace(/<%([\s\S]*?)%>/g, '$1');
function run(adjudicados, editable) {
    var rows = [];
    var originales = [
        {id:'11', codigoItem:'A', descripcionItem:'Prestacion A', cantidad:'10', precioUnitario:'100.00', precioTotal:'1000.00', observaciones:''},
        {id:'12', codigoItem:'B', descripcionItem:'Prestacion B', cantidad:'5', precioUnitario:'200.00', precioTotal:'1000.00', observaciones:''}
    ];
    var before = JSON.stringify(originales);
    var context = {
        puedeVerCotizacionDetalle:true, puedeCotizarDetalle:editable, puedeABMDetalle:false,
        puedeEliminarDetalle:false, puedeCorregirItemDetalle:false, mostrarTipoCotizacionDetalle:false,
        requerimientoPersistidoDetalle:true,
        t_detallesCompra:originales, t_detalleAdjudicadoVista:adjudicados,
        t_actualizarVisibilidadColumnasDetalleCompra:function(){},
        t_esDetalleMedicamentosFarmaciaCompra:function(){return false;},
        t_detalleEscapeHtml:function(s){return s == null ? '' : String(s);},
        jQuery:function(selector) {
            if (selector === '#t_detalle_body') return {
                empty:function(){ rows.length = 0; }, append:function(s){ rows.push(s); }
            };
            return {val:function(){return this;},bind:function(){return this;},append:function(){return this;}};
        }
    };
    vm.runInNewContext(render + '\nt_renderDetallesCompra();', context);
    assert.strictEqual(JSON.stringify(originales), before, 'Render no modifica modelo que se envia al servidor');
    return rows;
}
var vista = {'11':{cantidad:'7',precioUnitario:'120.00',precioTotal:'840.00'},
             '12':{cantidad:'0',precioUnitario:'0.00',precioTotal:'0.00'}};
var rows = run(vista, false);
assert(rows[0].indexOf('<td>7</td>') >= 0 && rows[0].indexOf('<td>120.00</td>') >= 0 && rows[0].indexOf('<td>840.00</td>') >= 0, 'A muestra cantidades y unitario adjudicados');
assert(rows[1].indexOf('<td>0</td>') >= 0 && rows[1].indexOf('<td>0.00</td>') >= 0, 'B conserva fila y muestra ceros');
rows = run({}, false);
assert(rows[0].indexOf('<td>10</td>') >= 0 && rows[0].indexOf('<td>100.00</td>') >= 0, 'Sin proyeccion conserva originales');
rows = run({}, true);
assert(rows[0].indexOf('<td>10</td>') >= 0 && rows[0].indexOf('1000.00</span>') >= 0, 'Cotizacion editable conserva cantidades y subtotal originales');
assert(jsp.indexOf('!puedeABMDetalle && !puedeCotizarDetalle && detalleAdjudicadoVista != null') >= 0, 'Mapa de consulta no se publica en edicion');
assert(jsp.indexOf('cantidad: \'<%= jsDetalleCompra(detalle.getCantidadString()) %>\'') >= 0, 'Modelo serializable conserva fuente original de cantidad');
console.log('DETALLE_ADJUDICADO_TABLA_OK; funcion real con ramas JSP simuladas, sin navegador ni JSP desplegado');
