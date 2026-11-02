"""QA12 only: real seven function bodies against session TEMP relations, always rollback.

Reference: lecturas_vigentes_sql_test.py literal extraction; no SQL rule is rewritten.
Default is static preparation. A human-reviewed execution flag and source hashes
are required before opening a connection. No operational migration is executed.
"""
import argparse
import hashlib
import json
import os
from pathlib import Path
import re
from datetime import date, datetime, timezone

ROOT = Path(__file__).resolve().parents[2]
EVID = Path(r'C:\Users\Programador 2\.codex\visualizations\2026\10\30\01a18d18-13e2-7f92-9bd5-1feb77f35fa3')
BASE = ROOT / '.codex/artifacts/normalizacion-afiliado-20261102/baseline-metadata.json'
ACTUAL = EVID / 'normalizacion_descripcion_prestador_20261102/captura_RO_descripcion_prestador_20261102T195708714000Z/resultado_RO_descripcion_prestador_sin_PII.json'
CANON_RECEIPT = EVID / 'normalizacion_descripcion_prestador_20261102/canon_views_TEMP_en_devmolineros_20261102.json'
FORWARD = ROOT / 'docs/sql/compras/20261102_normalizar_descripcion_prestador_presupuesto.sql'
REVERSE = ROOT / 'docs/sql/compras/20261102_normalizar_descripcion_prestador_presupuesto_reversion.sql'
JAVA = ROOT / 'ext-impl/src/ar/com/ospim/compras/requerimientos/service/BusquedaRequerimientoCompraServiceImpl.java'
KEYS = [('registrar_requerimiento_orden_medica', 11), ('registrar_requerimiento_presupuesto', 11),
        ('registrar_requerimiento_presupuesto', 15), ('get_documento_requerimiento', 3),
        ('listar_documentos_requerimiento', 2), ('listar_ordenes_medicas_requerimiento', 1),
        ('listar_presupuestos_prestador', 2)]
RELATIONS = {'compras.requerimiento_presupuesto_consulta': 'pg_temp.npp_consulta',
             'compras.requerimiento_presupuesto': 'pg_temp.npp_presupuesto',
             'compras.requerimiento_cotizacion_prestador': 'pg_temp.npp_rcp',
             'compras.sector_requerimiento': 'pg_temp.npp_sector',
             'compras.requerimiento': 'pg_temp.npp_req', 'public.prestador': 'pg_temp.npp_prestador',
             'informacion_afip.empresa': 'pg_temp.npp_empresa'}
CHECKS = []
ADAPTATIONS = []


def sha(raw):
    return hashlib.sha256(raw).hexdigest()


def check(label, condition):
    if not condition:
        raise AssertionError(label + ': no coincide (sin valores de filas)')
    CHECKS.append(label)


def load(path, expected=None):
    raw = path.read_bytes()
    if expected:
        check('hash ' + path.name, sha(raw) == expected)
    return raw, json.loads(raw)


def fragments(source, prefix):
    return [m.group(2).strip() for m in re.finditer(
        r'EXECUTE\s+(\$' + prefix + r'_[0-6]\$)(.*?)\1\s*;', source, re.S)]


def adapt(source, functions=True):
    mapping = dict(RELATIONS)
    if functions:
        for name, nargs in KEYS:
            mapping['compras.' + name] = 'pg_temp.' + name
    # Preserve SQL string literals, including advisory-lock namespace and messages.
    pieces = re.split(r"('(?:''|[^'])*')", source)
    for i in range(0, len(pieces), 2):
        for old, new in sorted(mapping.items(), key=lambda x: -len(x[0])):
            pieces[i] = re.sub(r'(?<![\w])' + re.escape(old) + r'(?![\w])', new, pieces[i])
    result = ''.join(pieces)
    back = re.split(r"('(?:''|[^'])*')", result)
    for i in range(0, len(back), 2):
        for old, new in sorted(mapping.items(), key=lambda x: -len(x[1])):
            back[i] = re.sub(r'(?<![\w])' + re.escape(new) + r'(?![\w])', old, back[i])
    check('adaptacion reversible solo identificadores', ''.join(back) == source)
    remaining = set(re.findall(r'\bcompras\.([a-z_]+)', ''.join(pieces[::2])))
    check('solo auxiliares instalados permitidos', remaining <= {'normalizar_sector', 'buscar_empresas_cotizacion'})
    ADAPTATIONS.append({'source_sha256_utf8': sha(source.encode('utf-8')),
                        'adapted_sha256_utf8': sha(result.encode('utf-8')),
                        'operation': 'identificadores fijos; literales/reglas preservados'})
    return result


def constants():
    found = {}
    for name, expression in re.findall(r'private static final String (SQL_\w+)\s*=\s*(.*?);', JAVA.read_text(encoding='latin-1'), re.S):
        tokens = re.findall(r'"(?:\\.|[^"\\])*"|SQL_\w+', expression)
        found[name] = ''.join(json.loads(t) if t.startswith('"') else found[t] for t in tokens)
    check('dos constantes nativas disponibles', all(k in found for k in ['SQL_DOCUMENTO_BASE', 'SQL_BLOQUEAR_DOCUMENTO']))
    return found


def query(cur, statement, values=()):
    cur.execute(statement, values)
    return cur.fetchall()


def native(cur, statement, values):
    cur.execute(adapt(statement, False).replace('%', '%%').replace('?', '%s'), values)
    return [dict(zip([c[0] for c in cur.description], row)) for row in cur.fetchall()]


def save(path, value):
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_bytes((json.dumps(value, ensure_ascii=True, indent=2) + '\n').encode('latin-1'))


def prepare(args):
    base_raw, base = load(BASE, '879c320a0aa88831ec4cd38d98e547b81142a93be204803b276c56271b7142b3')
    actual_raw, actual = load(ACTUAL, '42e7ab58fae40fa264171a4f1e9e5bd57fa50b4ed3a4bf34b01cd4a098720142')
    fw = FORWARD.read_bytes(); rv = REVERSE.read_bytes()
    if args.execute_reviewed:
        check('pin forward revisado', bool(args.forward_sha) and sha(fw) == args.forward_sha)
        check('pin reverse revisado', bool(args.reverse_sha) and sha(rv) == args.reverse_sha)
        check('pin lector nativo revisado', bool(args.native_sha) and sha(JAVA.read_bytes()) == args.native_sha)
    old = {k: next(f for f in base['functions'] if (f['name'], f['nargs']) == k) for k in KEYS}
    installed = {(f['proname'], f['pronargs']): f['md5_lf'] for f in actual['results']['funciones_actuales_solo_firmas_hashes']}
    check('siete cuerpos baseline siguen instalados en recibo42e7', all(installed[k] == old[k]['md5_lf'] for k in KEYS))
    new = fragments(fw.decode('latin-1'), 'def')
    check('siete definiciones nuevas extraidas sin ejecutar migracion', len(new) == 7)
    view_match = re.search(r'EXECUTE\s+(\$querycreate\$)(.*?)\1\s*;', fw.decode('latin-1'), re.S)
    check('vista22 nueva extraida de migracion', view_match is not None)
    reverse_old = fragments(rv.decode('latin-1'), 'old')
    check('tres writers reverse verificables', len(reverse_old) == 3)
    for i in range(3):
        check('writer reverse igual baseline ' + str(i), reverse_old[i].rstrip(';').strip() == old[KEYS[i]]['definition'].rstrip(';').strip())
    table = next(t for t in base['tables'] if t['name'] == 'requerimiento_presupuesto')
    check('shape baseline22', len(table['columns']) == 22)
    return base, old, new, view_match.group(2).strip(), table, {
        'baseline_sha256': sha(base_raw), 'actual_metadata_sha256': sha(actual_raw),
        'forward_sha256': sha(fw), 'reverse_sha256': sha(rv), 'native_Java_sha256': sha(JAVA.read_bytes()),
        'canonical_metadata_already_executed_by_root': {
            'path': str(CANON_RECEIPT), 'sha256': sha(CANON_RECEIPT.read_bytes()),
            'query22_md5': '099173d3ffea37f61109578b54c06a77', 'OC_md5': '2d4abcf36abcd2f30cc4d5276b352123',
            'not_reexecuted_here': True}, 'operational_migrations_not_executed': True}


def temp_relations(cur):
    # CTAS WITH NO DATA keeps used column types and copies no rows/defaults/sequences.
    for name, source, columns, pk in [
        ('npp_req', 'compras.requerimiento', 'id_requerimiento,estado,id_sector,afiliado_cuil_titular,afiliado_int,baja_fecha', 'id_requerimiento'),
        ('npp_rcp', 'compras.requerimiento_cotizacion_prestador', 'id_requerimiento,id_prestador,estado_envio,modi_fecha,modi_usr', 'id_requerimiento,id_prestador'),
        ('npp_sector', 'compras.sector_requerimiento', 'id_sector,descripcion', 'id_sector'),
        ('npp_prestador', 'public.prestador', 'id_prestador,descripcion,cuit,baja_fecha', 'id_prestador'),
        ('npp_empresa', 'informacion_afip.empresa', 'cuit,sucursal,razon_soc', 'cuit,sucursal')]:
        cur.execute('CREATE TEMP TABLE ' + name + ' ON COMMIT DROP AS SELECT ' + columns + ' FROM ' + source + ' WITH NO DATA')
        cur.execute('ALTER TABLE pg_temp.' + name + ' ADD PRIMARY KEY (' + pk + ')')
    cur.execute('CREATE TEMP SEQUENCE npp_id_seq')
    cur.execute('CREATE TEMP TABLE npp_presupuesto (LIKE compras.requerimiento_presupuesto INCLUDING DEFAULTS INCLUDING INDEXES) ON COMMIT DROP')
    cur.execute("ALTER TABLE pg_temp.npp_presupuesto ALTER COLUMN id_requerimiento_presupuesto SET DEFAULT nextval('pg_temp.npp_id_seq'::regclass)")
    # Mandatory before FIRST INSERT: not one inherited sequence may reference compras/public.
    refs = query(cur, """SELECT s.relpersistence,s.relnamespace=pg_my_temp_schema()
        FROM pg_attrdef d JOIN pg_depend dep ON dep.classid='pg_attrdef'::regclass AND dep.objid=d.oid
        JOIN pg_class s ON dep.refclassid='pg_class'::regclass AND dep.refobjid=s.oid AND s.relkind='S'
        WHERE d.adrelid IN(SELECT oid FROM pg_class WHERE relnamespace=pg_my_temp_schema())""")
    check('antesprimerINSERT todas secuencias son TEMP', refs and all(r == ('t', True) for r in refs))
    check('PK y UNIQUE budget clonadas', query(cur, "SELECT count(*) FROM pg_constraint WHERE conrelid='pg_temp.npp_presupuesto'::regclass AND contype IN('p','u')")[0][0] == 2)
    initial_columns = query(cur, "SELECT count(*) FROM pg_attribute WHERE attrelid='pg_temp.npp_presupuesto'::regclass AND attnum>0 AND NOT attisdropped")[0][0]
    check('clone previo o posterior normalizacion', initial_columns in (21, 22))
    if initial_columns == 21:
        check('reponer copia solo en TEMP', query(cur, "SELECT relpersistence='t' AND relnamespace=pg_my_temp_schema() FROM pg_class WHERE oid='pg_temp.npp_presupuesto'::regclass")[0][0])
        cur.execute('ALTER TABLE pg_temp.npp_presupuesto ADD COLUMN descripcion_prestador varchar(500)')
    check('tabla temporal inicial22', query(cur, "SELECT count(*) FROM pg_attribute WHERE attrelid='pg_temp.npp_presupuesto'::regclass AND attnum>0 AND NOT attisdropped")[0][0] == 22)
    cur.execute("INSERT INTO pg_temp.npp_sector VALUES(1,'PRESTACIONES MEDICAS'),(2,'SISTEMAS')")
    cur.execute("INSERT INTO pg_temp.npp_req VALUES(930001,2,1,NULL,NULL,NULL),(930002,1,1,'QA_TEMP_ONLY',0,NULL),(930003,1,2,NULL,NULL,NULL),(930004,1,1,'QA_TEMP_ONLY',0,NULL),(930005,1,1,NULL,NULL,NULL)")
    cur.execute("INSERT INTO pg_temp.npp_prestador VALUES(501,'Maestro temporal','0',NULL)")
    cur.execute("INSERT INTO pg_temp.npp_rcp(id_requerimiento,id_prestador,estado_envio) VALUES(930001,501,'ENVIADO'),(930005,501,'ENVIADO')")


def exercise(cur, old, new, view, table, sql_native):
    seq = [9012345600]
    def metadata():
        seq[0] += 1
        return [9012345678, 9012345679, seq[0], ' uuid-temp ', ' temporal.pdf ', ' persistido.pdf ', ' Titulo temporal ']
    def call(key, values):
        types = [v.strip().split(' ', 1)[1] for v in old[key]['args'].split(',')]
        check('aridad ' + key[0], len(values) == len(types))
        cur.execute('SELECT * FROM pg_temp.' + key[0] + '(' + ','.join('%s::' + t for t in types) + ')', values)
        names = [c[0] for c in cur.description]
        return names, cur.fetchall()
    def writer11(req=930001, prov=501, **change):
        v = [req, prov] + metadata() + ['COPIA OBSOLETA NO PERSISTIR', ' qa ']
        for i, value in change.items(): v[int(i)] = value
        return call(KEYS[1], v)[1][0][0]
    def om(req=930002, receta=' a b 1 ', fecha=date(2026, 1, 2), **change):
        v = [req] + metadata() + [fecha, receta, ' qa ']
        for i, value in change.items(): v[int(i)] = value
        return call(KEYS[0], v)[1][0][0]
    def empresa(cuit=None, **change):
        v = [930003, 3, None, cuit, None, ' Empresa libre TEMP '] + metadata() + [None, ' qa ']
        for i, value in change.items(): v[int(i)] = value
        return call(KEYS[2], v)[1][0][0]
    def reject(label, action):
        before = query(cur, 'SELECT count(*) FROM pg_temp.npp_presupuesto')[0][0]
        cur.execute('SAVEPOINT npp_reject')
        try:
            action()
        except Exception as error:
            state = getattr(error, 'pgcode', None)
            cur.execute('ROLLBACK TO SAVEPOINT npp_reject')
            check(label + ' rechazo funcional', state == 'P0001')
        else:
            cur.execute('ROLLBACK TO SAVEPOINT npp_reject')
            raise AssertionError(label + ': operacion invalida aceptada')
        check(label + ' no inserta parcialmente', query(cur, 'SELECT count(*) FROM pg_temp.npp_presupuesto')[0][0] == before)
        cur.execute('RELEASE SAVEPOINT npp_reject')

    cur.execute('SAVEPOINT npp_baseline')
    for key in KEYS: cur.execute(adapt(old[key]['definition']))
    legacy = writer11()
    names, rows = call(KEYS[3], [legacy, 930001, 1])
    check('baseline reader22 y copia persistida', len(names) == 22 and rows[0][names.index('descripcion_prestador')] == 'COPIA OBSOLETA NO PERSISTIR')
    cur.execute('ROLLBACK TO SAVEPOINT npp_baseline')
    cur.execute('RELEASE SAVEPOINT npp_baseline')
    cur.execute('ALTER TABLE pg_temp.npp_presupuesto DROP COLUMN descripcion_prestador')
    check('tabla fisica TEMP reducida21', query(cur, "SELECT count(*) FROM pg_attribute WHERE attrelid='pg_temp.npp_presupuesto'::regclass AND attnum>0 AND NOT attisdropped")[0][0] == 21)
    cur.execute(adapt(view, False))
    for definition in new: cur.execute(adapt(definition))
    check('siete funciones solo TEMP', query(cur, 'SELECT count(*) FROM pg_proc WHERE pronamespace=pg_my_temp_schema()')[0][0] == 7)
    provider = writer11()
    metadata_actual = query(cur, 'SELECT dl_group_id,dl_folder_id,dl_file_uuid,nombre_original,nombre_persistido,titulo,alta_usr FROM pg_temp.npp_presupuesto WHERE id_requerimiento_presupuesto=%s', [provider])[0]
    check('writer metadata completa bigints y trim', metadata_actual == (9012345678, 9012345679, 'uuid-temp', 'temporal.pdf', 'persistido.pdf', 'Titulo temporal', 'qa'))
    check('writer11 estado COTIZADO', query(cur, 'SELECT estado_envio FROM pg_temp.npp_rcp WHERE id_requerimiento=930001 AND id_prestador=501')[0][0] == 'COTIZADO')
    reject('repeticion proveedor', writer11)
    reject('estado requerimiento incorrecto', lambda: writer11(930005))
    reject('identidad proveedor invalida', lambda: writer11(prov=0))
    reject('metadata DL invalida', lambda: writer11(**{'2': 0}))
    cur.execute('SAVEPOINT npp_duplicate_active')
    cur.execute("UPDATE pg_temp.npp_rcp SET estado_envio='ENVIADO' WHERE id_requerimiento=930001")
    reject('presupuesto activo duplicado', writer11)
    cur.execute('ROLLBACK TO SAVEPOINT npp_duplicate_active')
    cur.execute('RELEASE SAVEPOINT npp_duplicate_active')
    order = om()
    check('OM receta y fecha', query(cur, 'SELECT numero_receta,fecha_documento FROM pg_temp.npp_presupuesto WHERE id_requerimiento_presupuesto=%s', [order])[0] == ('AB1', date(2026, 1, 2)))
    reject('OM receta duplicada otro requerimiento', lambda: om(930004, 'AB1'))
    reject('OM estado incorrecto', lambda: om(930001))
    reject('OM fecha obligatoria', lambda: om(fecha=None))
    optional = om(receta=None, **{'7': '', '10': ''})
    check('OM opcional titulo fallback y auditoria legacy', query(cur, 'SELECT numero_receta,titulo,alta_usr FROM pg_temp.npp_presupuesto WHERE id_requerimiento_presupuesto=%s', [optional])[0] == (None, 'temporal.pdf', 'sistema'))
    company = empresa()
    check('Empresa libre preservada', query(cur, 'SELECT descripcion_empresa,id_prestador FROM pg_temp.npp_presupuesto WHERE id_requerimiento_presupuesto=%s', [company])[0] == ('Empresa libre TEMP', None))
    reject('Empresa no acepta proveedor', lambda: empresa(**{'2': 501}))
    reject('Empresa tipo incorrecto', lambda: empresa(**{'1': 1}))
    reject('Empresa CUIT invalido', lambda: empresa(cuit='0'))
    empresa(cuit='20999999990')
    reject('Empresa CUIT duplicado', lambda: empresa(cuit='20999999990'))
    expected_names = [c['name'] for c in table['columns']]
    for key, values in [(KEYS[3], [provider, 930001, 1]), (KEYS[4], [930001, 1]), (KEYS[5], [930002]), (KEYS[6], [930001, 501])]:
        names, rows = call(key, values)
        check('reader22 aliases orden ' + key[0], names == expected_names and rows)
    raw = 'stra\u00dfe i e\u0301 \U0001f600 ' + 'descripcion larga ' * 4
    cur.execute('UPDATE pg_temp.npp_prestador SET descripcion=%s,baja_fecha=now() WHERE id_prestador=501', [raw])
    names, rows = call(KEYS[3], [provider, 930001, 1])
    check('Unicode raw largo y maestro baja refrescado', rows[0][names.index('descripcion_prestador')] == raw)
    names, rows = call(KEYS[3], [order, 930002, 2])
    check('OM alias proveedor NULL', rows[0][names.index('descripcion_prestador')] is None)
    names, rows = call(KEYS[3], [company, 930003, 3])
    check('Empresa alias proveedor NULL y texto libre', rows[0][names.index('descripcion_prestador')] is None and rows[0][names.index('descripcion_empresa')] == 'Empresa libre TEMP')
    cur.execute('DELETE FROM pg_temp.npp_prestador WHERE id_prestador=501')
    names, rows = call(KEYS[3], [provider, 930001, 1])
    check('legacy missing master igual marcador nativo', rows[0][names.index('descripcion_prestador')] == '[PRESTADOR NO RESUELTO]')
    normal = native(cur, sql_native['SQL_DOCUMENTO_BASE'] + ' WHERE rp.id_requerimiento_presupuesto=?', [provider])[0]
    locked = native(cur, sql_native['SQL_BLOQUEAR_DOCUMENTO'], [930001, provider])[0]
    check('native marker y lock real PG', normal['descripcion_prestador'] == locked['descripcion_prestador'] == '[PRESTADOR NO RESUELTO]')
    check('lock22 parse FOR UPDATE OF rp', len(locked) == 22)
    cur.execute("INSERT INTO pg_temp.npp_prestador VALUES(501,'','0',NULL)")
    names, rows = call(KEYS[3], [provider, 930001, 1])
    check('descripcion vacia raw', rows[0][names.index('descripcion_prestador')] == '')
    cur.execute("UPDATE pg_temp.npp_presupuesto SET empresa_cuit='20999999991',empresa_sucursal='0001',descripcion_empresa='SNAPSHOT RAW' WHERE id_requerimiento_presupuesto=%s", [company])
    cur.execute("INSERT INTO pg_temp.npp_empresa VALUES('20999999991','0001','MAESTRO DISTINTO')")
    normal = native(cur, sql_native['SQL_DOCUMENTO_BASE'] + ' WHERE rp.id_requerimiento_presupuesto=?', [company])[0]
    locked = native(cur, sql_native['SQL_BLOQUEAR_DOCUMENTO'], [930003, company])[0]
    check('native UI Empresa deriva y lock conserva RAW', normal['descripcion_empresa'] == 'MAESTRO DISTINTO' and locked['descripcion_empresa'] == 'SNAPSHOT RAW')


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument('--execute-reviewed', action='store_true')
    ap.add_argument('--forward-sha')
    ap.add_argument('--reverse-sha')
    ap.add_argument('--native-sha')
    ap.add_argument('--evidence', type=Path, default=ROOT / 'output/presupuesto-prestador-temporal-sql-qa')
    args = ap.parse_args()
    base, old, new, view, table, sources = prepare(args)
    # Validate every identifier-only adaptation before any possible connection.
    for key in KEYS:
        adapt(old[key]['definition'])
    for definition in new:
        adapt(definition)
    adapt(view, False)
    sql_native = constants()
    for key in ('SQL_DOCUMENTO_BASE', 'SQL_BLOQUEAR_DOCUMENTO'):
        adapt(sql_native[key], False)
    report = {'utc': datetime.now(timezone.utc).isoformat(), 'status': 'PREPARADO_NO_EJECUTADO', 'sources': sources,
              'permanent_business_writes': False, 'new_database_or_schema': False, 'mode': 'TEMP-functional-only-canon-not-reexecuted',
              'limitations': ['No concurrencia entre sesiones: TEMP no es visible desde otra conexion.',
                              'Legacy Empresa identificada con sucursal positiva no ejercitada; se evita AFIP delegado con sucursalNULL.',
                              'No aplica las migraciones operativas ni acredita permisos/DDL permanentes/UI.']}
    con = None
    failed = False
    try:
        if args.execute_reviewed:
            import psycopg2
            con = psycopg2.connect(host='12.1.1.12', hostaddr='12.1.1.12', port=5432, dbname='devmolineros',
                                   user=os.environ['PGUSER'], password=os.environ['PGPASSWORD'], connect_timeout=8,
                                   application_name='compras_presupuesto_TEMP_revisado',
                                   options='-c statement_timeout=15000 -c lock_timeout=2000')
            con.set_session(readonly=False, autocommit=False)
            cur = con.cursor()
            cur.execute('SET LOCAL search_path=pg_catalog,public')
            context = query(cur, "SELECT host(inet_server_addr()),inet_server_port(),current_database(),current_setting('server_encoding'),current_setting('server_version_num')")[0]
            check('guard antes de TEMP/INSERT', context == ('12.1.1.12', 5432, 'devmolineros', 'UTF8', '90615'))
            report['context'] = list(context)
            temp_relations(cur)
            exercise(cur, old, new, view, table, sql_native)
            report['status'] = 'APROBADO_TEMP_ROLLBACK_PENDIENTE'
    except Exception as error:
        report['status'] = 'FALLO_SIN_EXITO_TOTAL'
        report['error_type'] = type(error).__name__
        report['SQLSTATE'] = getattr(error, 'pgcode', None)
        failed = True
    finally:
        if con is not None:
            try:
                con.rollback()
                report['rollback_confirmed'] = True
                cur = con.cursor()
                remaining = query(cur, 'SELECT (SELECT count(*) FROM pg_class WHERE relnamespace=pg_my_temp_schema())+(SELECT count(*) FROM pg_proc WHERE pronamespace=pg_my_temp_schema())')[0][0]
                check('objetos TEMP destruidos por rollback', remaining == 0)
                report['temp_objects_absent_after_rollback'] = remaining == 0
                con.rollback()
                if report['status'] == 'APROBADO_TEMP_ROLLBACK_PENDIENTE': report['status'] = 'APROBADO_TEMP_ROLLBACK_CERRADO'
            except Exception as cleanup_error:
                report['status'] = 'FALLO_CLEANUP_NO_EXITO_TOTAL'
                report['cleanup_error_type'] = type(cleanup_error).__name__
                failed = True
            finally:
                try:
                    con.close()
                    report['connection_closed'] = True
                except Exception as close_error:
                    report['status'] = 'FALLO_CIERRE_NO_EXITO_TOTAL'
                    report['close_error_type'] = type(close_error).__name__
                    failed = True
        report['checks'] = CHECKS
        report['checks_count'] = len(CHECKS)
        report['adaptations'] = ADAPTATIONS
        save(args.evidence / 'resultado_TEMP_presupuesto_prestador.json', report)
    print(json.dumps({'status': report['status'], 'checks': len(CHECKS), 'evidence': str(args.evidence)}, ensure_ascii=True))
    if failed:
        raise SystemExit('FALLO TEMP; revisar metadatos del recibo, sin mensajes SQL sensibles.')


if __name__ == '__main__':
    main()
