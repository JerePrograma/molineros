"""Controles estaticos. No conecta a DB ni sustituye pruebas de integracion.
Requiere pglast solo en el entorno de verificacion, no en la aplicacion.
"""
from pathlib import Path
import re
import subprocess
from pglast import parse_sql, ast

ROOT = Path(__file__).resolve().parents[4]
BASE = Path(__file__).resolve().parent
SCHEMA = 'ext-impl/src/ar/com/ospim/compras/sql/compras_schema.sql'
MIGRACION = 'docs/sql/compras/20260923_refactor_compras_fase1_expand.sql'
CLEANUP = 'docs/sql/compras/20260923_refactor_compras_fase2_cleanup.sql'
schema = (ROOT / SCHEMA).read_text(encoding='latin1')
migracion = (ROOT / MIGRACION).read_text(encoding='latin1')
cleanup = (ROOT / CLEANUP).read_text(encoding='latin1')
original = subprocess.check_output(['git', 'show', '308990e77a909ff49a88f47b33d0b36db2f7519d:' + SCHEMA], cwd=str(ROOT)).decode('latin1')
verificaciones = 0

def verificar(condicion, caso):
    global verificaciones
    if not condicion:
        raise AssertionError(caso)
    verificaciones += 1

def funciones(sql):
    try:
        return [sql[x.stmt_location:x.stmt_location+x.stmt_len].strip()
                for x in parse_sql(sql) if isinstance(x.stmt,ast.CreateFunctionStmt)
                and x.stmt.funcname[0].sval=='compras']
    except Exception:
        return [m.group(0) for m in re.finditer(r'CREATE (?:OR REPLACE )?FUNCTION compras\.\w+\(.*?AS (\$[A-Za-z_0-9]*\$).*?\1', sql, re.S | re.I)]

def nombre(f):
    return parse_sql(f)[0].stmt.funcname[-1].sval

def clave(f):
    node = parse_sql(f)[0].stmt
    return (nombre(f), tuple(str(p.argType) for p in (node.parameters or []) if p.mode.value not in ('o','t')))

for sql in (schema, migracion, cleanup):
    verificar(len(parse_sql(sql)) > 0, 'DDL parseable')
    for f in funciones(sql):
        if 'LANGUAGE sql' in f:
            parse_sql(next(x.arg[0].sval for x in parse_sql(f)[0].stmt.options if x.defname=='as'))

tablas = {s.stmt.relation.relname: s.stmt for s in parse_sql(schema)
          if isinstance(s.stmt, ast.CreateStmt) and s.stmt.relation.schemaname == 'compras'}
verificar(len(tablas) == 10, 'Diez tablas')
eliminadas = {
    'requerimiento': ['afiliado_id_ospim','afiliado_nombre','afiliado_apellido','afiliado_documento_tipo','afiliado_documento_nro','afiliado_direccion','afiliado_localidad','afiliado_provincia','afiliado_celular','afiliado_telefono','afiliado_email','cargo_ospim','recupero'],
    'requerimiento_detalle': ['id_tipo_nomenclador','codigo_nomenclador','descripcion_nomenclador'],
    'requerimiento_presupuesto': ['descripcion_prestador','descripcion_empresa']
}
for tabla, cols in eliminadas.items():
    actuales = {n.colname for n in tablas[tabla].tableElts if isinstance(n, ast.ColumnDef)}
    for col in cols:
        verificar(col not in actuales, 'Eliminar columna fisica ' + col)
        verificar("('%s','%s')" % (tabla,col) in cleanup, 'Retiro diferido de ' + col)
        verificar("('%s','%s')" % (tabla,col) in migracion, 'Retencion fisica requerida de ' + col)
verificar(sum(len(c) for c in eliminadas.values()) == 18, 'Dieciocho columnas')
verificar('surge' in {n.colname for n in tablas['requerimiento'].tableElts if isinstance(n, ast.ColumnDef)}, 'SURGE fisico')
verificar('precio_total_estimado' in {n.colname for n in tablas['requerimiento_detalle'].tableElts if isinstance(n, ast.ColumnDef)}, 'Total fisico')
verificar(not re.search(r'\bCHECK\s*\(', schema, re.I), 'Sin CHECKs historicos')
triggers = [s.stmt.trigname for s in parse_sql(schema) if isinstance(s.stmt, ast.CreateTrigStmt)]
verificar(triggers == ['trg_compras_detalle_calcular_total'], 'Solo trigger de total')
verificar(not re.search(r'DROP\s+[^;]+\bCASCADE\b', migracion, re.I), 'Drops sin CASCADE')

objetivo = {nombre(f): f for f in funciones(schema)}
por_firma = {clave(f):f for f in funciones(schema)}
antes = funciones((BASE / 'funciones_instaladas_antes.sql').read_text(encoding='latin1'))
atomicas = ['reservar_notificacion_cotizacion_prestador','finalizar_notificacion_cotizacion_prestador',
            'registrar_pedido_cotizacion_documento','guardar_cotizacion_requerimiento','finalizar_cotizacion_requerimiento',
            'registrar_requerimiento_presupuesto','baja_requerimiento_presupuesto','reactivar_requerimiento_presupuesto',
            'registrar_requerimiento_orden_medica','reservar_reclamo_prestacional','finalizar_reclamo_prestacional',
            'marcar_error_reclamo_prestacional','liberar_reserva_reclamo_prestacional','bloquear_requerimiento_reclamo_prestacional']
for f in antes:
    n = nombre(f)
    if n not in atomicas:
        continue
    for patron in (r'\bFOR UPDATE\b', r'\bON CONFLICT\b', r'\bpg_advisory_xact_lock\b', r'\btoken_reserva\b'):
        verificar(len(re.findall(patron, f, re.I)) == len(re.findall(patron, por_firma[clave(f)], re.I)), n + ': ' + patron)
base = objetivo['requerimiento_base']
verificar('(r.cargo_tercerizadora > 0)' in base and '(100 - r.cargo_tercerizadora)' in base, 'Cargos derivados')
verificar('r.surge' in base, 'SURGE sin derivacion')
for n in ['get_requerimiento_detalle','get_requerimiento_detalle_clasificado','buscar_requerimientos','buscar_items_historicos_afiliado','buscar_items_historicos_afiliado_clasificado']:
    verificar(any(nombre(f)==n and 'autorizaciones.nomenclador n' in f for f in por_firma.values()), n + ': maestro actual')
for n in ['listar_documentos_requerimiento','get_documento_requerimiento','listar_ordenes_medicas_requerimiento','listar_presupuestos_prestador']:
    verificar('RETURNS TABLE' in objetivo[n] and 'e.sucursal = rp.empresa_sucursal' in objetivo[n], n + ': contrato y clave empresa')

def externos(sql):
    return [sql[s.stmt_location:s.stmt_location+s.stmt_len].strip()
            for s in parse_sql(sql) if isinstance(s.stmt,ast.CreateFunctionStmt)
            and s.stmt.funcname[0].sval != 'compras']
verificar(not externos(schema), 'Canonico sin funciones externas')
verificar('INSERT INTO public.system_config' not in schema, 'Canonico sin configuracion global')
paths = subprocess.check_output(['git','diff','--name-only'],cwd=str(ROOT)).decode().splitlines()
for path in paths:
    verificar('/compras/' in path or path.startswith('docs/sql/compras/'), 'Alcance: ' + path)
    data = (ROOT/path).read_bytes()
    verificar(not data.startswith((b'\xef\xbb\xbf',b'\xff\xfe',b'\xfe\xff')), 'Sin BOM: ' + path)
    texto = data.decode('latin1')
    verificar('\u00c3' not in texto and '\u00c2' not in texto and '\ufffd' not in texto, 'Sin mojibake: ' + path)
verificar(not any(p.endswith(('.jrxml','.jasper')) for p in paths), 'Plantillas PDF intactas')
print('OK: %d verificaciones estaticas; no acredita ejecucion SQL ni UI.' % verificaciones)

# Estrategia de despliegue: distinguir DDL de definiciones de funciones runtime.
statements = parse_sql(migracion)
creadas = [n.stmt.relation.relname for n in statements if isinstance(n.stmt,ast.CreateStmt)]
verificar(set(creadas)=={'estado_requerimiento','tipo_prestacion_tipo_nomenclador'} and len(creadas)==2, 'Solo dos tablas nuevas en expand')
for n in statements:
    stmt = n.stmt
    if isinstance(stmt,(ast.InsertStmt,ast.UpdateStmt,ast.DeleteStmt)):
        verificar(stmt.relation.schemaname=='compras' and stmt.relation.relname in ['estado_requerimiento','sector_requerimiento','tipo_prestacion','tipo_prestacion_tipo_nomenclador'], 'DML limitado a configuracion')
    verificar(not isinstance(stmt,(ast.DropStmt,ast.TruncateStmt)), 'Sin DROP/TRUNCATE directo en expand')
for patron in [r'DROP\s+(TABLE|SCHEMA|TRIGGER|INDEX)\b',r'DROP\s+COLUMN\b',r'\bTRUNCATE\b']:
    verificar(not re.search(patron,migracion,re.I), 'Expand preservador: '+patron)
retornos = re.findall(r'DROP FUNCTION (compras\.\w+\([^;]+?\)|compras\.\w+\(\)) RESTRICT;',migracion)
verificar(len(retornos)==9, 'Nueve excepciones exactas por retorno incompatible')
verificar(migracion.count('IF recrear THEN')==9, 'Recreacion solo cuando cambia retorno')
verificar(migracion.count('IF recrear AND propietario IS NOT NULL THEN')==9, 'Owner y ACL conservados')
verificar(migracion.count('NOT VALID;')==5 and migracion.count('VALIDATE CONSTRAINT %I')==5, 'Cinco FKs internas comprobadas y validadas')
for tabla in ['requerimiento','requerimiento_detalle','requerimiento_presupuesto','requerimiento_cotizacion_prestador','requerimiento_pedido_cotizacion','requerimiento_reclamo_prestacional']:
    verificar('PRE '+tabla+':' in migracion and 'POST '+tabla+':' in migracion, 'Contadores pre/post '+tabla)
verificar('actual IS DISTINCT FROM current_setting' in migracion, 'Conservacion aborta ante diferencia')
verificar("compras.cleanup_autorizado" in cleanup, 'Cleanup bloqueado hasta QA')
verificar('dependencia' in cleanup.lower() and 'refobjsubid=atributo' in cleanup, 'Precondiciones por columna')
verificar('DROP FUNCTION ' in cleanup and 'caller IS NOT NULL' in cleanup, 'Precondiciones por funcion')
verificar('DROP INDEX compras.ix_compras_requerimiento_afiliado_id_ospim RESTRICT' in cleanup, 'Indice pospuesto')
verificar('DROP TRIGGER trg_compras_requerimiento_completar_baja' in cleanup, 'Trigger de baja pospuesto')
verificar(not any(isinstance(n.stmt,ast.CreateFunctionStmt) for n in parse_sql(cleanup)), 'Cleanup no redefine runtime')
retirado=(ROOT/'docs/sql/compras/20260922_refactor_normalizacion_compras.sql').read_text(encoding='latin1')
verificar('RAISE EXCEPTION' in retirado and len(parse_sql(retirado))==1, 'Incremental anterior inhabilitado')
print('OK TOTAL: %d verificaciones estaticas, incluyendo separacion de fases.' % verificaciones)
