var assert = require('assert');
var fs = require('fs');
var path = require('path');
var vm = require('vm');
var jsp = fs.readFileSync(path.resolve(__dirname, '../../ext-web/docroot/html/portlet/compras/requerimientos/partials/requerimiento_compra_acciones_componente.jsp'), 'latin1');
var script = jsp.slice(jsp.indexOf('function <portlet:namespace />crearOrdenCompraDirecta'), jsp.indexOf('var <portlet:namespace />popupComparativaCompra'));
script = script.replace(/<portlet:namespace \/>/g, 'comprasTest_').replace(/<%= botoneraOrdenCompraURL.toString\(\) %>/g, '/orden-compra/formulario');
function escenario(fallaNavegacion) {
    var estado = { navegaciones: 0, avisos: 0, envios: 0 };
    var location = {};
    Object.defineProperty(location, 'href', { set: function(url) {
        estado.navegaciones++;
        if (fallaNavegacion) { throw new Error('fallo de navegacion'); }
        estado.url = url;
    }});
    var contexto = {
        window: { location: location },
        document: { getElementById: function() { return { submit: function() { estado.envios++; }}; }},
        alert: function() { estado.avisos++; }
    };
    vm.createContext(contexto);
    vm.runInContext(script, contexto);
    estado.crear = contexto.comprasTest_crearOrdenCompraDirecta;
    return estado;
}
var caso = escenario(false);
var boton = { disabled: false, value: 'Crear Orden de Compra' };
assert.strictEqual(caso.crear(boton), false);
assert.strictEqual(caso.navegaciones, 1);
assert.strictEqual(caso.url, '/orden-compra/formulario');
assert.strictEqual(caso.envios, 0);
assert.strictEqual(boton.disabled, true);
assert.strictEqual(boton.value, 'Abriendo...');
caso.crear(boton);
assert.strictEqual(caso.navegaciones, 1);
caso = escenario(false);
assert.strictEqual(caso.crear({disabled: true}), false);
assert.strictEqual(caso.navegaciones, 0);
assert.strictEqual(caso.crear(null), false);
caso = escenario(true);
boton = { disabled: false, value: 'Crear Orden de Compra' };
assert.strictEqual(caso.crear(boton), false);
assert.strictEqual(caso.navegaciones, 1);
assert.strictEqual(caso.envios, 0);
assert.strictEqual(boton.disabled, false);
assert.strictEqual(boton.value, 'Crear Orden de Compra');
assert.strictEqual(caso.avisos, 1);
console.log('ORDEN_COMPRA_FORMULARIO_UI_JS_OK checks=16');
