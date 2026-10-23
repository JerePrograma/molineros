var assert = require('assert');
var fs = require('fs');
var path = require('path');
var vm = require('vm');
var jsp = fs.readFileSync(path.resolve(__dirname, '../../ext-web/docroot/html/portlet/compras/requerimientos/partials/requerimiento_compra_acciones_componente.jsp'), 'latin1');
var script = jsp.slice(jsp.indexOf('function <portlet:namespace />crearOrdenCompraDirecta'), jsp.indexOf('var <portlet:namespace />popupComparativaCompra'));
script = script.replace(/<portlet:namespace \/>/g, 'comprasTest_').replace(/<%= botoneraOrdenCompraFormId %>/g, 'orderForm');
function escenario(sinFormulario, fallaEnvio) {
    var estado = { envios: 0, avisos: 0 };
    var contexto = {
        document: { getElementById: function(id) {
            assert.strictEqual(id, 'orderForm');
            return sinFormulario ? null : { submit: function() {
                estado.envios++;
                if (fallaEnvio) { throw new Error('fallo de transporte'); }
            }};
        }},
        alert: function() { estado.avisos++; }
    };
    vm.createContext(contexto);
    vm.runInContext(script, contexto);
    estado.crear = contexto.comprasTest_crearOrdenCompraDirecta;
    return estado;
}
var caso = escenario(false, false);
var boton = { disabled: false, value: 'Crear Orden de Compra' };
assert.strictEqual(caso.crear(boton), false);
assert.strictEqual(caso.envios, 1);
assert.strictEqual(boton.disabled, true);
assert.strictEqual(boton.value, 'Generando...');
caso.crear(boton);
assert.strictEqual(caso.envios, 1);
caso = escenario(false, false);
assert.strictEqual(caso.crear({disabled: true}), false);
assert.strictEqual(caso.envios, 0);
assert.strictEqual(caso.crear(null), false);
caso = escenario(true, false);
boton = { disabled: false, value: 'Crear Orden de Compra' };
assert.strictEqual(caso.crear(boton), false);
assert.strictEqual(caso.envios, 0);
assert.strictEqual(boton.disabled, false);
caso = escenario(false, true);
boton = { disabled: false, value: 'Crear Orden de Compra' };
assert.strictEqual(caso.crear(boton), false);
assert.strictEqual(caso.envios, 1);
assert.strictEqual(boton.disabled, false);
assert.strictEqual(boton.value, 'Crear Orden de Compra');
assert.strictEqual(caso.avisos, 1);
console.log('ORDEN_COMPRA_DIRECTA_UI_JS_OK checks=16');
