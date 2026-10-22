/* Regression test: actual JSP functions with controlled DOM/services.
 * Reference: test/compras/exportar_requerimientos_test.js (source + vm).
 * Run: node test/compras/validar_codigos_detalle_test.js
 * No browser, DB, backend persistence or complete product build is exercised. */
var fs = require('fs');
var path = require('path');
var vm = require('vm');
var assert = require('assert');
var root = path.resolve(__dirname, '../..');
var folder = path.join(root, 'ext-web/docroot/html/portlet/compras/requerimientos/partials');
var results = [];

function read(name) {
    return fs.readFileSync(path.join(folder, name), 'latin1');
}
function extract(source, name) {
    var marker = 'function <portlet:namespace />' + name + '(';
    var start = source.indexOf(marker);
    if (start < 0) {
        marker = 'function <portlet:namespace />' + name + '(\r\n';
        start = source.indexOf(marker);
    }
    assert(start >= 0, 'Missing function ' + name);
    var open = source.indexOf('{', start);
    var depth = 0;
    var quote = '';
    var escaped = false;
    var line = false;
    var block = false;
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
        if (c === "'" || c === '"') { quote = c; continue; }
        if (c === '{') { depth++; }
        if (c === '}') {
            depth--;
            if (!depth) {
                return jspBranches(source.slice(start, i + 1)
                        .replace(/<portlet:namespace \/>/g, 'p_')
                        .replace(/<%=[\s\S]*?%>/g, 'true'));
            }
        }
    }
    throw new Error('Incomplete function ' + name);
}
function jspBranches(s) {
    var stack = [], active = true, previous = 0, result = '', match;
    var re = /<%([\s\S]*?)%>/g;
    while ((match = re.exec(s))) {
        if (active) { result += s.slice(previous, match.index); }
        var text = match[1].trim();
        if (/^if\s*\(puedeCotizarDetalle\)\s*\{$/.test(text)) {
            stack.push({active: active, condition: false});
            active = false;
        } else if (/^\}\s*else\s*\{$/.test(text)) {
            assert(stack.length, 'JSP else without condition');
            active = stack[stack.length - 1].active && !stack[stack.length - 1].condition;
        } else if (text === '}') {
            assert(stack.length, 'JSP close without condition');
            active = stack.pop().active;
        } else {
            throw new Error('Unhandled test JSP conditional: ' + text);
        }
        previous = re.lastIndex;
    }
    if (active) { result += s.slice(previous); }
    assert.strictEqual(stack.length, 0);
    return result;
}
function detail(code, id, type) {
    return {
        id: id || '', tipoItem: 'NOMENCLADOR', idPrestacion: id || '11482',
        idTipoNomenclador: type || '14', idTipoPrestacion: '4',
        codigoNomenclador: code, descripcionNomenclador: 'DESCRIPCION',
        codigoItem: code, descripcionItem: 'DESCRIPCION', cantidad: '1',
        observaciones: '', precioUnitario: '', precioTotal: '', idPrestador: ''
    };
}
function setup(existing, candidate, editIndex, persisted, correction) {
    var state = {alerts: [], posts: [], renders: 0, lock: false, hidden: []};
    var form = {elements: {}};
    var jq = function(selector) {
        return {
            length: 1,
            val: function() { return String(selector).indexOf('detalle_edit_index') >= 0 ? String(editIndex) : ''; },
            attr: function() { return false; },
            focus: function() { return this; },
            find: function() {
                return {each: function(callback) {
                    var checks = state.checks || [];
                    for (var i = 0; i < checks.length; i++) {
                        if (callback.call(checks[i], i, checks[i]) === false) { break; }
                    }
                    return this;
                }};
            }
        };
    };
    jq.trim = function(v) { return String(v == null ? '' : v).trim(); };
    jq.inArray = function(v, values) { return values.indexOf(v); };
    var ctx = {
        jQuery: jq, alert: function(s) { state.alerts.push(s); },
        confirm: function() { return true; },
        p_detallesCompra: existing,
        document: {getElementById: function() { return form; }},
        p_detalleAccionEnCurso: false, p_detalleModoCorreccion: !!correction,
        p_requerimientoPersistidoDetalle: !!persisted,
        p_leerDetalleEditor: function() { return candidate; },
        p_esTipoPrestacionDetalleValidoParaSector: function() { return true; },
        p_validarTipoNomencladorDetalleSeleccionado: function() { return true; },
        p_setDetalleAccionEnCurso: function(v) { state.lock = v; ctx.p_detalleAccionEnCurso = v; },
        p_postDetalleServidor: function(cmd, value) { state.posts.push({cmd: cmd, detalle: value}); return false; },
        p_limpiarEditorDetalle: function() {},
        p_limpiarPayloadDetallesCompra: function() { state.hidden = []; form.elements = {}; },
        p_crearHiddenDetalle: function(name, value) {
            state.hidden.push({name: name, value: value});
            form.elements['p_' + name] = {value: value == null ? '' : value};
            return true;
        },
        p_detalleDeletedIds: [], p_idPrestadorAdjudicado: '', p_prestadorAdjudicado: '',
        p_renderDetallesCompra: function() { state.renders++; },
        p_liberarDetalleAccion: function() { ctx.p_detalleAccionEnCurso = false; state.lock = false; return false; }
    };
    ctx.window = ctx;
    vm.createContext(ctx);
    var base = read('requerimiento_compra_detalle_scripts_base_componente.jsp');
    ['detalleValue', 'obtenerCodigoDetalleCompra', 'validarCodigoDetalleUnico'].forEach(function(name) {
        if (base.indexOf('function <portlet:namespace />' + name + '(') >= 0) {
            vm.runInContext(extract(base, name), ctx, {filename: name + '.jsp.js'});
        }
    });
    var edit = read('requerimiento_compra_detalle_scripts_edicion_componente.jsp');
    ['esEnteroPositivo', 'validarDetalleEditor', 'agregarOActualizarDetalle', 'serializarDetallesCompras', 'quitarDetalleEnPantalla'].forEach(function(name) {
        vm.runInContext(extract(edit, name), ctx, {filename: name + '.jsp.js'});
    });
    return {ctx: ctx, state: state};
}
function historical(existing, selected, persisted) {
    var env = setup(existing, null, -1, persisted, false);
    var ctx = env.ctx;
    ctx.p_itemsHistoricosAfiliado = selected.map(function(d) {
        return {
            idPrestacion: d.idPrestacion, idTipoNomenclador: d.idTipoNomenclador,
            idTipoPrestacion: d.idTipoPrestacion,
            codigo: d.codigoNomenclador, descripcion: d.descripcionNomenclador
        };
    });
    env.state.checks = selected.map(function(d, i) { return {value: String(i), disabled: false, checked: true}; });
    ctx.p_getIdSectorTipoPrestacionDetalle = function() { return '2'; };
    ctx.p_tiposPrestacionDetalleCache = [{idSector: '2'}];
    ctx.p_descripcionTipoPrestacionHistoricoAfiliado = function() { return 'PROTESIS'; };
    ctx.p_validarTipoNomencladorResultadoDetalle = function() { return true; };
    ctx.p_postItemsHistoricosAfiliadoServidor = function(values) { env.state.posts.push({cmd: 'historicos', detalles: values}); return false; };
    ctx.p_renderItemsHistoricosAfiliado = function() {};
    var source = read('requerimiento_compra_scripts_edicion_afiliado_componente.jsp');
    ['claveItemHistoricoAfiliado', 'clavesDetallesActuales', 'crearDetalleDesdeItemHistoricoAfiliado',
        'obtenerDetallesHistoricosSeleccionados', 'agregarItemsHistoricosSeleccionados'].forEach(function(name) {
        vm.runInContext(extract(source, name), ctx, {filename: name + '.jsp.js'});
    });
    return env;
}
function check(name, fn) {
    fn();
    results.push({case: name, status: 'PASS'});
    process.stdout.write('PASS ' + name + '\n');
}

[
    {name: 'manual_add_duplicate', index: -1, persisted: false},
    {name: 'persisted_add_duplicate', index: -1, persisted: true},
    {name: 'manual_edit_other_duplicate', index: 1, persisted: false},
    {name: 'persisted_edit_other_duplicate', index: 1, persisted: true},
    {name: 'correction_other_duplicate', index: 1, persisted: true, correction: true}
].forEach(function(c) {
    check(c.name, function() {
        var list = [detail('070204', '9001'), detail('123456', '9002')];
        var snapshot = JSON.stringify(list);
        var env = setup(list, detail('070204', '9002', '10'), c.index, c.persisted, c.correction);
        env.ctx.p_agregarOActualizarDetalle();
        assert.strictEqual(JSON.stringify(list), snapshot);
        assert.strictEqual(env.state.posts.length, 0);
        assert.strictEqual(env.state.renders, 0);
        assert.strictEqual(env.state.lock, false);
        assert(env.state.alerts.length > 0);
        assert(env.state.alerts[0].indexOf('070204') >= 0);
    });
});
[false, true].forEach(function(persisted) {
    check('edit_own_same_code_' + persisted, function() {
        var candidate = detail('070204', '9001');
        candidate.cantidad = '2';
        var list = [detail('070204', '9001')];
        var env = setup(list, candidate, 0, persisted, false);
        env.ctx.p_agregarOActualizarDetalle();
        assert.strictEqual(env.state.alerts.length, 0);
        if (persisted) {
            assert.strictEqual(env.state.posts.length, 1);
            assert.strictEqual(env.state.posts[0].cmd, 'updateItem');
            assert.strictEqual(env.state.posts[0].detalle.cantidad, '2');
        } else {
            assert.strictEqual(list.length, 1);
            assert.strictEqual(list[0].cantidad, '2');
        }
    });
    check('manual_unique_add_' + persisted, function() {
        var list = [detail('070204', '9001')];
        var env = setup(list, detail('9100050', '11486'), -1, persisted, false);
        env.ctx.p_agregarOActualizarDetalle();
        assert.strictEqual(env.state.alerts.length, 0);
        assert.strictEqual(persisted ? env.state.posts.length : list.length, persisted ? 1 : 2);
    });
});
check('correction_unique_among_remaining_historical_duplicates', function() {
    var list = [detail('070204', '9001'), detail('070204', '9002'), detail('070204', '9003')];
    var env = setup(list, detail('9100050', '9002'), 1, true, true);
    env.ctx.p_agregarOActualizarDetalle();
    assert.strictEqual(env.state.posts.length, 1);
    assert.strictEqual(env.state.posts[0].cmd, 'corregirItem');
    assert.strictEqual(env.state.alerts.length, 0);
});
check('normalization_trim_case', function() {
    var env = setup([], null, -1, false, false);
    assert.strictEqual(env.ctx.p_obtenerCodigoDetalleCompra(detail(' ab001 ')), 'AB001');
});
check('normalization_zeroes_preserved', function() {
    var env = setup([], null, -1, false, false);
    assert.strictEqual(env.ctx.p_obtenerCodigoDetalleCompra(detail(' 070204 ')), '070204');
    assert.strictEqual(env.ctx.p_obtenerCodigoDetalleCompra(detail('70204')), '70204');
    assert.strictEqual(env.ctx.p_validarCodigoDetalleUnico(detail('070204'), -1, [detail('70204')]), true);
});
check('same_code_different_identifier_type_and_category', function() {
    var env = setup([], null, -1, false, false);
    var candidate = detail(' abc01 ', '9002', '10'); candidate.idTipoPrestacion = '6';
    assert.strictEqual(env.ctx.p_validarCodigoDetalleUnico(candidate, -1, [detail('ABC01', '9001', '14')]), false);
    assert.strictEqual(env.state.alerts.length, 1);
});
check('fallback_codigo_item', function() {
    var env = setup([], null, -1, false, false);
    var candidate = detail(''); candidate.codigoItem = ' x01 ';
    assert.strictEqual(env.ctx.p_obtenerCodigoDetalleCompra(candidate), 'X01');
    candidate.codigoNomenclador = '   ';
    assert.strictEqual(env.ctx.p_obtenerCodigoDetalleCompra(candidate), 'X01');
});
check('null_empty_and_observations_not_codes', function() {
    var env = setup([], null, -1, false, false);
    assert.strictEqual(env.ctx.p_obtenerCodigoDetalleCompra(null), '');
    assert.strictEqual(env.ctx.p_obtenerCodigoDetalleCompra(detail('')), '');
    var obs = detail('070204'); obs.tipoItem = 'OBSERVACION';
    assert.strictEqual(env.ctx.p_obtenerCodigoDetalleCompra(obs), '');
    assert.strictEqual(env.ctx.p_validarCodigoDetalleUnico(obs, -1, [detail('070204')]), true);
});
check('legacy_medicine_code_duplicate', function() {
    var env = setup([], null, -1, false, false);
    var medicine = detail(''); medicine.tipoItem = 'MEDICAMENTO'; medicine.codigoItem = '070204';
    assert.strictEqual(env.ctx.p_validarCodigoDetalleUnico(detail('070204'), -1, [medicine]), false);
});
check('own_index_excluded_not_another_same_identifier', function() {
    var candidate = detail('070204', '9001');
    var env = setup([candidate], null, -1, false, false);
    assert.strictEqual(env.ctx.p_validarCodigoDetalleUnico(candidate, 0), true);
    env.ctx.p_detallesCompra.push(detail('070204', '9001'));
    assert.strictEqual(env.ctx.p_validarCodigoDetalleUnico(candidate, 0), false);
});
[false, true].forEach(function(persisted) {
    check('history_existing_code_' + persisted, function() {
        var list = [detail('070204', '9001')], before = JSON.stringify(list);
        var env = historical(list, [detail('070204', '9002', '10')], persisted);
        env.ctx.p_agregarItemsHistoricosSeleccionados();
        assert.strictEqual(JSON.stringify(list), before);
        assert.strictEqual(env.state.posts.length, 0);
        assert(env.state.alerts.length > 0);
    });
    check('history_batch_duplicate_atomic_' + persisted, function() {
        var list = [];
        var env = historical(list, [detail('9100046', '11482'), detail('Ab001', '9001'), detail(' ab001 ', '9002', '10')], persisted);
        env.ctx.p_agregarItemsHistoricosSeleccionados();
        assert.strictEqual(list.length, 0);
        assert.strictEqual(env.state.posts.length, 0);
        assert(env.state.alerts.length > 0);
    });
    check('history_unique_batch_' + persisted, function() {
        var list = [detail('070204', '9001')];
        var env = historical(list, [detail('9100046', '11482'), detail('9100050', '11486', '10')], persisted);
        env.ctx.p_agregarItemsHistoricosSeleccionados();
        assert.strictEqual(env.state.alerts.length, 0);
        if (persisted) {
            assert.strictEqual(list.length, 1);
            assert.strictEqual(env.state.posts.length, 1);
            assert.strictEqual(env.state.posts[0].detalles.length, 2);
        } else {
            assert.strictEqual(list.length, 3);
        }
    });
});
check('serialize_duplicates_blocked_before_hidden_payload', function() {
    var env = setup([detail('070204', '9001'), detail('070204', '9002')], null, -1, false, false);
    assert.strictEqual(env.ctx.p_serializarDetallesCompras(), false);
    assert.strictEqual(env.state.hidden.length, 0);
    assert(env.state.alerts.length > 0);
});
check('serialize_unique_allowed', function() {
    var env = setup([detail('070204', '9001'), detail('9100050', '11486')], null, -1, false, false);
    assert.strictEqual(env.ctx.p_serializarDetallesCompras(), true);
    assert(env.state.hidden.length > 2);
    assert.strictEqual(env.state.alerts.length, 0);
});
check('serialize_observations_without_codes_allowed', function() {
    var a = detail(''); a.tipoItem = 'OBSERVACION'; a.observaciones = 'NOTA';
    var b = detail(''); b.tipoItem = 'OBSERVACION'; b.observaciones = 'OTRA NOTA';
    var env = setup([a, b], null, -1, false, false);
    assert.strictEqual(env.ctx.p_serializarDetallesCompras(), true);
    assert.strictEqual(env.state.alerts.length, 0);
});
check('reserved_object_key_code_compared_as_text', function() {
    var env = setup([], null, -1, false, false);
    assert.strictEqual(env.ctx.p_validarCodigoDetalleUnico(detail('__proto__'), -1, [detail('__PROTO__')]), false);
    assert.strictEqual(env.ctx.p_validarCodigoDetalleUnico(detail('constructor'), -1, [detail('CONSTRUCTOR')]), false);
});
check('remove_row_frees_code_for_new_add', function() {
    var list = [detail('070204', '9001')];
    var env = setup(list, detail('070204', '9002'), -1, false, false);
    env.ctx.p_quitarDetalleEnPantalla(0);
    assert.strictEqual(list.length, 0);
    env.ctx.p_agregarOActualizarDetalle();
    assert.strictEqual(list.length, 1);
    assert.strictEqual(list[0].codigoNomenclador, '070204');
    assert.strictEqual(env.state.alerts.length, 0);
});
check('quantity_two_allowed_as_single_row', function() {
    var list = [], candidate = detail('9100050', '11486');
    candidate.cantidad = '2';
    var env = setup(list, candidate, -1, false, false);
    env.ctx.p_agregarOActualizarDetalle();
    assert.strictEqual(list.length, 1);
    assert.strictEqual(list[0].cantidad, '2');
    assert.strictEqual(env.state.alerts.length, 0);
});
check('duplicate_validation_never_merges_or_mutates_quantity', function() {
    var a = detail('070204', '9001'); a.cantidad = '2';
    var candidate = detail('070204', '9002'); candidate.cantidad = '5';
    var env = setup([a], candidate, -1, false, false);
    var before = JSON.stringify({existing: env.ctx.p_detallesCompra, candidate: candidate});
    assert.strictEqual(env.ctx.p_validarCodigoDetalleUnico(candidate, -1), false);
    assert.strictEqual(JSON.stringify({existing: env.ctx.p_detallesCompra, candidate: candidate}), before);
});
[null, undefined].forEach(function(missing) {
    check('history_missing_collection_' + String(missing), function() {
        var env = historical(missing, [detail('9100050', '11486')], true);
        assert.doesNotThrow(function() { env.ctx.p_agregarItemsHistoricosSeleccionados(); });
        assert.strictEqual(env.state.posts.length, 0);
        assert.strictEqual(env.state.renders, 0);
        assert(env.state.alerts.length > 0);
    });
});
process.stdout.write('TOTAL=' + results.length + '\n');
