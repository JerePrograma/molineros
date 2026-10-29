/* Regression: execute actual JSP functions with controlled dates and DOM.
 * Reference: validar_codigos_detalle_test.js (source + vm).
 * Run: node test/compras/fechas_requerimiento_test.js
 * No browser, persistence or complete application build is exercised. */
var fs = require('fs');
var path = require('path');
var vm = require('vm');
var assert = require('assert');
var root = path.resolve(__dirname, '../..');
var folder = path.join(root, 'ext-web/docroot/html/portlet/compras/requerimientos');
var passed = 0;
var failed = 0;

function read(name) {
    return fs.readFileSync(path.join(folder, name), 'latin1');
}
function extract(source, name) {
    var start = source.indexOf('function <portlet:namespace />' + name + '(');
    assert(start >= 0, 'Missing function ' + name);
    source = source.slice(start).replace(/<portlet:namespace \/>/g, 'p_')
            .replace(/<%=[\s\S]*?%>/g, 'ADD').replace(/<%[\s\S]*?%>/g, '');
    var open = source.indexOf('{');
    var depth = 0, quote = '', escaped = false, line = false, block = false;
    for (var i = open; i < source.length; i++) {
        var c = source.charAt(i), next = source.charAt(i + 1);
        if (line) { if (c === '\n') { line = false; } continue; }
        if (block) { if (c === '*' && next === '/') { block = false; i++; } continue; }
        if (quote) {
            if (escaped) { escaped = false; }
            else if (c === '\\') { escaped = true; }
            else if (c === quote) { quote = ''; }
            continue;
        }
        if (c === '/' && next === '/') { line = true; i++; continue; }
        if (c === '/' && next === '*') { block = true; i++; continue; }
        if (c === '"' || c === "'") { quote = c; continue; }
        if (c === '{') { depth++; }
        if (c === '}' && --depth === 0) { return source.slice(0, i + 1); }
    }
    throw new Error('Incomplete function ' + name);
}
function fixedDate(now) {
    function ControlledDate(year, month, day) {
        if (!arguments.length) { return new Date(now.getTime()); }
        return new Date(year, month, day);
    }
    return ControlledDate;
}
function control(value, state) {
    return {value: String(value), focus: function() { state.focused++; }};
}
function environment(dates, now) {
    var state = {alerts: [], focused: 0, sent: 0};
    var rows = dates.map(function(date) {
        return {day: control(date[2], state), month: control(date[1], state),
            year: control(date[0], state), hidden: {value: 'old'},
            file: {value: 'orden.pdf', focus: function() { state.focused++; }}};
    });
    var firstHidden = {value: 'old'};
    var count = {value: '0'};
    function collection(values) {
        return {length: values.length, each: function(callback) {
            for (var i = 0; i < values.length; i++) {
                if (callback.call(values[i], i) === false) { break; }
            }
        }};
    }
    function jquery(selector) {
        if (selector && typeof selector === 'object') {
            return {attr: function() { return selector.suffix; }, find: function(key) {
                var property = key.indexOf('fecha-dia') >= 0 ? 'day'
                        : key.indexOf('fecha-mes') >= 0 ? 'month'
                        : key.indexOf('fecha-anio') >= 0 ? 'year'
                        : key.indexOf('fecha-valor') >= 0 ? 'hidden' : 'file';
                return {get: function() { return selector[property]; }};
            }};
        }
        if (selector.indexOf('tr.orden-medica-activa') >= 0) { return collection(rows); }
        if (selector.indexOf('.comparativa-fecha') >= 0) { return collection(rows); }
        var match = /#p_fecha(Dia|Mes|Anio)?_(\d+)/.exec(selector);
        assert(match, 'Unknown selector ' + selector);
        var property = !match[1] ? 'hidden' : {Dia: 'day', Mes: 'month', Anio: 'year'}[match[1]];
        var field = rows[parseInt(match[2], 10)][property];
        return {val: function(value) {
            if (arguments.length) { field.value = value; }
            return field.value;
        }, focus: field.focus};
    }
    jquery.trim = function(value) { return String(value == null ? '' : value).trim(); };
    rows.forEach(function(row, index) { row.suffix = '_' + index; });
    var ctx = {Date: fixedDate(now), jQuery: jquery,
        alert: function(message) { state.alerts.push(message); },
        document: {getElementById: function(id) {
            return id === 'p_fecha_orden_medica_hidden' ? firstHidden : count;
        }}, p_actualizarContratoFilasOrdenMedica: function() {},
        p_validarTamanoOrdenMedica: function() { return true; }};
    vm.createContext(ctx);
    var save = read('partials/requerimiento_compra_scripts_edicion_guardado_componente.jsp');
    ['ordenMedicaDosDigitos', 'validarOrdenMedicaAlta'].forEach(function(name) {
        vm.runInContext(extract(save, name), ctx, {filename: name + '.jsp.js'});
    });
    vm.runInContext(extract(read('partials/requerimiento_compra_orden_medica_carga_componente.jsp'),
            'sincronizarFechaOrdenMedicaFila'), ctx);
    vm.runInContext(extract(read('requerimiento_compra_comparativa.jsp'),
            'actualizarFechaComparativa'), ctx);
    return {ctx: ctx, state: state, rows: rows, firstHidden: firstHidden, count: count,
        row: function(index) { return jquery(rows[index]); }};
}
function check(name, fn) {
    try { fn(); passed++; process.stdout.write('PASS ' + name + '\n'); }
    catch (error) { failed++; process.stdout.write('FAIL ' + name + ': ' + error.message + '\n'); }
}
var now = new Date(2026, 9, 29, 23, 59, 59);
[
    {name: 'today', date: [2026, 9, 29], valid: true},
    {name: 'past', date: [2025, 11, 31], valid: true},
    {name: 'tomorrow', date: [2026, 9, 30], valid: false},
    {name: 'future_month', date: [2026, 10, 1], valid: false},
    {name: 'future_year', date: [2027, 0, 1], valid: false}
].forEach(function(test) {
    check('adjunto_submit_' + test.name, function() {
        var env = environment([test.date], now);
        assert.strictEqual(env.ctx.p_validarOrdenMedicaAlta({}), test.valid);
        assert.strictEqual(env.state.alerts.length, test.valid ? 0 : 1);
        if (!test.valid) {
            assert(env.state.alerts[0].indexOf('hoy') >= 0);
            assert.strictEqual(env.rows[0].day.value, String(test.date[2]));
        }
    });
    check('adjunto_change_' + test.name, function() {
        var env = environment([test.date], now);
        assert.strictEqual(env.ctx.p_sincronizarFechaOrdenMedicaFila(env.row(0)), test.valid);
        assert.strictEqual(env.state.alerts.length, test.valid ? 0 : 1);
    });
    check('presupuesto_' + test.name, function() {
        var env = environment([test.date], now);
        assert.strictEqual(env.ctx.p_actualizarFechaComparativa(), test.valid);
        assert.strictEqual(env.state.alerts.length, test.valid ? 0 : 1);
        if (!test.valid) {
            assert(env.state.alerts[0].indexOf('hoy') >= 0);
            assert.strictEqual(env.rows[0].day.value, String(test.date[2]));
        }
    });
});
check('adjunto_today_at_midnight', function() {
    var env = environment([[2026, 9, 29]], new Date(2026, 9, 29, 0, 0, 0));
    assert.strictEqual(env.ctx.p_validarOrdenMedicaAlta({}), true);
    assert.strictEqual(env.firstHidden.value, '2026-10-29');
});
check('adjunto_second_future_rejected', function() {
    var env = environment([[2026, 9, 28], [2026, 9, 30]], now);
    assert.strictEqual(env.ctx.p_validarOrdenMedicaAlta({}), false);
    assert(env.state.alerts[0].indexOf('2') >= 0);
});
check('adjunto_impossible_date_still_rejected', function() {
    var env = environment([[2026, 1, 31]], now);
    assert.strictEqual(env.ctx.p_validarOrdenMedicaAlta({}), false);
    assert(env.state.alerts[0].indexOf('no existe') >= 0);
});
check('presupuesto_optional_empty_preserved', function() {
    var env = environment([['', '', '']], now);
    assert.strictEqual(env.ctx.p_actualizarFechaComparativa(), true);
    assert.strictEqual(env.rows[0].hidden.value, '');
    assert.strictEqual(env.state.alerts.length, 0);
});
function uploadEnvironment(valid) {
    var source = extract(read('requerimiento_compra_documentos.jsp'), 'uploadPresupuestoRequerimientoCompra');
    var state = {sent: 0, validated: 0};
    var ctx = {document: {getElementById: function() { return {}; }}, alert: function() {},
        jQuery: function(selector) {
            if (selector.indexOf('presupuesto_0_id_prestador') >= 0) {
                return {val: function() { return '123'; }};
            }
            if (selector.indexOf('presupuesto_0') >= 0) {
                return {val: function() { return 'presupuesto.pdf'; }};
            }
            return {find: function() {
                return {val: function() { return '123'; }, attr: function() { return false; }};
            }};
        }, p_actualizarFechaComparativa: function() { state.validated++; return valid; },
        p_enviarOperacionCompra: function() { state.sent++; return true; },
        p_reindexarFilasPresupuesto: function() {},
        p_validarTamanoArchivoPresupuesto: function() { return true; }};
    vm.createContext(ctx);
    vm.runInContext(source, ctx);
    return {ctx: ctx, state: state};
}
check('presupuesto_submit_gate_before_send', function() {
    var env = uploadEnvironment(false);
    assert.strictEqual(env.ctx.p_uploadPresupuestoRequerimientoCompra(), false);
    assert.strictEqual(env.state.validated, 1);
    assert.strictEqual(env.state.sent, 0);
});
check('presupuesto_submit_allowed_date_still_sends', function() {
    var env = uploadEnvironment(true);
    assert.strictEqual(env.ctx.p_uploadPresupuestoRequerimientoCompra(), true);
    assert.strictEqual(env.state.validated, 1);
    assert.strictEqual(env.state.sent, 1);
});
check('presupuesto_submit_missing_validator_fails_closed', function() {
    var env = uploadEnvironment(true);
    delete env.ctx.p_actualizarFechaComparativa;
    assert.strictEqual(env.ctx.p_uploadPresupuestoRequerimientoCompra(), false);
    assert.strictEqual(env.state.sent, 0);
});
process.stdout.write('RESULT ' + passed + ' passed, ' + failed + ' failed\n');
if (failed) { process.exitCode = 1; }
