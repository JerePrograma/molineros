"""PostgreSQL 9.6 fixture test. Never applies DDL to the operational database.

Reference: lecturas_vigentes_sql_test.py (extract actual Java SQL), and the
isolated fixture contract of auditoria_secuencia_sql_test.py. All fixture
rows are synthetic. The supplied catalog export contains definitions only.
Credentials must come from process environment. The database is preserved.
"""
import argparse
import hashlib
import json
import os
from pathlib import Path
import re
import sys
from datetime import datetime, timezone

import psycopg2
from psycopg2 import sql

ROOT = Path(__file__).resolve().parents[2]
FORWARD = ROOT / 'docs/sql/compras/20261102_normalizar_afiliado_requerimiento.sql'
REVERSE = ROOT / 'docs/sql/compras/20261102_normalizar_afiliado_requerimiento_reversion.sql'
JAVA = ROOT / 'ext-impl/src/ar/com/ospim/compras/requerimientos/service/EditarRequerimientoCompraServiceImpl.java'
PREFIX = 'compras_afiliado_normalizacion_test_20261102_'
COPIES = ['afiliado_id_ospim', 'afiliado_nombre', 'afiliado_apellido',
          'afiliado_documento_tipo', 'afiliado_documento_nro', 'afiliado_direccion',
          'afiliado_localidad', 'afiliado_provincia', 'afiliado_celular',
          'afiliado_telefono', 'afiliado_email']
CHECKS = []


def sha(data):
    return hashlib.sha256(data).hexdigest()


def equal(label, expected, actual):
    if expected != actual:
        raise AssertionError(label + ': values differ (no row contents logged)')
    CHECKS.append(label)


def one(cur, query, values=None):
    cur.execute(query, values)
    return cur.fetchone()[0]


def adapt(path, expected_sha, database):
    data = path.read_bytes()
    equal(path.name + ' source SHA256', expected_sha, sha(data))
    token = "current_database() IS DISTINCT FROM 'devmolineros'"
    source = data.decode('latin-1')
    equal(path.name + ' single database guard', 1, source.count(token))
    replacement = "current_database() IS DISTINCT FROM '" + database + "'"
    adapted = source.replace(token, replacement)
    equal(path.name + ' adapter reversibility', source, adapted.replace(replacement, token))
    return adapted, {'source_sha256': sha(data), 'adapted_sha256': sha(adapted.encode('latin-1')),
                     'transform': 'one literal current_database guard; host unchanged'}


def connect(database, readonly=False):
    con = psycopg2.connect(host='12.1.1.12', port=5432, user=os.environ.get('PGUSER', 'postgres'),
                           password=os.environ['PGPASSWORD'], dbname=database, connect_timeout=8,
                           options='-c statement_timeout=15000 -c lock_timeout=2000')
    con.autocommit = True
    with con.cursor() as cur:
        equal('connection host ' + database, '12.1.1.12', one(cur, 'SELECT host(inet_server_addr())'))
        equal('connection database ' + database, database, one(cur, 'SELECT current_database()'))
        if readonly:
            cur.execute('SET default_transaction_read_only=on')
        else:
            if database != 'postgres' and not re.fullmatch(PREFIX + r'[a-z0-9]{6}', database):
                raise AssertionError('fixture database guard')
        cur.execute('SET search_path=pg_catalog,public')
    return con


def qualified(item):
    return sql.Identifier(item['schema'], item['name'])


def install_baseline(con, metadata, auxiliary, resume=False):
    with con.cursor() as cur:
        if not resume:
            equal('empty fixture schema', None, one(cur, "SELECT to_regnamespace('compras')"))
        cur.execute('CREATE SCHEMA IF NOT EXISTS compras')
        cur.execute('CREATE SCHEMA IF NOT EXISTS autorizaciones')
        cur.execute('SET check_function_bodies=off')
        for seq in metadata['sequences']:
            name = seq['schema'] + '.' + seq['name']
            kind = one(cur, 'SELECT relkind FROM pg_class WHERE oid=to_regclass(%s)', (name,)) if one(cur, 'SELECT to_regclass(%s)', (name,)) else None
            if kind is None:
                cur.execute(sql.SQL('CREATE SEQUENCE {}').format(qualified(seq)))
            else:
                equal('resume sequence kind ' + name, 'S', kind)
        for table in metadata['reference_tables'] + metadata['tables']:
            reference = table in metadata['reference_tables']
            columns = []
            for column in table['columns']:
                suffix = '' if reference else ((' NOT NULL' if column['not_null'] else '') +
                           (' DEFAULT ' + column['default'] if column['default'] is not None else ''))
                columns.append(sql.SQL('{} {}' + suffix).format(sql.Identifier(column['name']), sql.SQL(column['type'])))
            name = table['schema'] + '.' + table['name']
            if one(cur, 'SELECT to_regclass(%s)', (name,)) is None:
                cur.execute(sql.SQL('CREATE TABLE {} ({})').format(qualified(table), sql.SQL(',').join(columns)))
            else:
                cur.execute('SELECT attname,format_type(atttypid,atttypmod) FROM pg_attribute WHERE attrelid=%s::regclass AND attnum>0 AND NOT attisdropped ORDER BY attnum', (name,))
                equal('resume table columns ' + name, [(c['name'],c['type']) for c in table['columns']], cur.fetchall())
        for table in metadata['reference_tables'] + metadata['tables']:
            for constraint in table['constraints']:
                if constraint['kind'] != 'f':
                    existing = one(cur, 'SELECT pg_get_constraintdef(oid,TRUE) FROM pg_constraint WHERE conrelid=%s::regclass AND conname=%s', (table['schema']+'.'+table['name'],constraint['name'])) if one(cur, 'SELECT EXISTS(SELECT 1 FROM pg_constraint WHERE conrelid=%s::regclass AND conname=%s)', (table['schema']+'.'+table['name'],constraint['name'])) else None
                    if existing is None:
                        cur.execute(sql.SQL('ALTER TABLE {} ADD CONSTRAINT {} {}').format(
                            qualified(table), sql.Identifier(constraint['name']), sql.SQL(constraint['definition'])))
                    else:
                        equal('resume constraint ' + constraint['name'], constraint['definition'], existing)
        for table in metadata['tables']:
            for constraint in table['constraints']:
                if constraint['kind'] == 'f':
                    if not one(cur, 'SELECT EXISTS(SELECT 1 FROM pg_constraint WHERE conrelid=%s::regclass AND conname=%s)', (table['schema']+'.'+table['name'],constraint['name'])):
                        cur.execute(sql.SQL('ALTER TABLE {} ADD CONSTRAINT {} {}').format(
                            qualified(table), sql.Identifier(constraint['name']), sql.SQL(constraint['definition'])))
                    else:
                        equal('resume FK ' + constraint['name'], constraint['definition'], one(cur, 'SELECT pg_get_constraintdef(oid,TRUE) FROM pg_constraint WHERE conrelid=%s::regclass AND conname=%s', (table['schema']+'.'+table['name'],constraint['name'])))
            for column in table['columns']:
                match = re.fullmatch(r"nextval\('([^']+)'::regclass\)", column['default'] or '')
                if match:
                    cur.execute(sql.SQL('ALTER SEQUENCE {} OWNED BY {}.{}').format(
                        sql.Identifier(*match.group(1).split('.')), qualified(table), sql.Identifier(column['name'])))
            constraint_names = [c['name'] for c in table['constraints'] if c['kind'] in ('p', 'u')]
            for index in table['indexes']:
                if index['name'] not in constraint_names:
                    existing = one(cur, 'SELECT to_regclass(%s)', (table['schema']+'.'+index['name'],))
                    if existing is None:
                        cur.execute(index['definition'])
                    else:
                        equal('resume index ' + index['name'], index['definition'], one(cur, 'SELECT pg_get_indexdef(%s::regclass)', (existing,)))
        for item in auxiliary['types'] + metadata['types']:
            columns = [sql.SQL('{} {}').format(sql.Identifier(c['name']), sql.SQL(c['type'])) for c in item['columns']]
            name = item['schema']+'.'+item['name']
            if one(cur, 'SELECT to_regtype(%s)', (name,)) is None:
                cur.execute(sql.SQL('CREATE TYPE {} AS ({})').format(qualified(item), sql.SQL(',').join(columns)))
            else:
                cur.execute('SELECT attname,format_type(atttypid,atttypmod) FROM pg_attribute WHERE attrelid=%s::regclass AND attnum>0 AND NOT attisdropped ORDER BY attnum', (name,))
                equal('resume type ' + name, [(c['name'],c['type']) for c in item['columns']], cur.fetchall())
        for function in metadata['functions']:
            cur.execute('SELECT replace(pg_get_functiondef(p.oid),E\'\\r\\n\',E\'\\n\') FROM pg_proc p WHERE p.pronamespace=%s::regnamespace AND p.proname=%s AND pg_get_function_identity_arguments(p.oid)=%s', (function['schema'],function['name'],function['args']))
            existing = cur.fetchone()
            if existing is None:
                cur.execute(function['definition'])
            else:
                equal('resume function ' + function['name'] + ':' + str(function['nargs']), function['definition'].replace('\r\n','\n'), existing[0])
        for view in metadata['views']:
            if one(cur, 'SELECT to_regclass(%s)', (view['schema']+'.'+view['name'],)) is None:
                cur.execute(sql.SQL('CREATE VIEW {} AS {}').format(qualified(view), sql.SQL(view['definition'])))
            else:
                equal('resume view ' + view['name'], view['definition'], one(cur, 'SELECT pg_get_viewdef(%s::regclass,TRUE)', (view['schema']+'.'+view['name'],)))
        for trigger in metadata['triggers']:
            if not one(cur, 'SELECT EXISTS(SELECT 1 FROM pg_trigger WHERE tgrelid=%s::regclass AND tgname=%s)', (trigger['schema']+'.'+trigger['table'],trigger['name'])):
                cur.execute(trigger['definition'])
            else:
                equal('resume trigger ' + trigger['name'], trigger['definition'], one(cur, 'SELECT pg_get_triggerdef(oid,TRUE) FROM pg_trigger WHERE tgrelid=%s::regclass AND tgname=%s', (trigger['schema']+'.'+trigger['table'],trigger['name'])))
        cur.execute('SET check_function_bodies=on')


def seed(con):
    with con.cursor() as cur:
        cur.execute("INSERT INTO public.afiliado(cuil_titular,inte,id_ospim,nombre,apellido,documento_tipo,docu_numero,id_seccional,email,baja_fecha) VALUES "
                    "('20000000001',0,100,'Actual','Fixture','DNI','101',1,'fixture@example.invalid',NULL),"
                    "('20000000001',1,101,'Integrante','Fixture','DNI','102',1,NULL,NULL),"
                    "('20000000002',0,200,'Baja','Fixture','DNI','103',1,NULL,now())")
        cur.execute("INSERT INTO public.localidad(id_localidad,detalle) VALUES(1,'Localidad fixture')")
        cur.execute("INSERT INTO public.provincia(id_provincia,detalle) VALUES(1,'Provincia fixture')")
        cur.execute("INSERT INTO public.seccional(id_seccional,descripcion) VALUES(1,'Seccional fixture')")
        cur.execute("INSERT INTO public.afi_domicilio(cuil_titular,inte,vigen_desde,alta_fecha,alta_usr,domi_tipo,id_domicilio,calle,numero,localidad,provincia,cod_area_celular,celular) VALUES"
                    "('20000000001',0,now(),now(),'fixture','P',1,'Calle actual','10',1,1,'11','123'),"
                    "('20000000001',1,now(),now(),'fixture','P',2,'Calle integrante','20',1,1,NULL,NULL),"
                    "('20000000002',0,now(),now(),'fixture','P',3,'Calle baja','30',1,1,NULL,NULL)")
        cur.execute("INSERT INTO compras.sector_requerimiento(id_sector,descripcion,requiere_afiliado) VALUES(1,'SISTEMAS',FALSE),(2,'RRHH',FALSE)")
        cur.execute("INSERT INTO compras.requerimiento(id_requerimiento,id_sector,afiliado_cuil_titular,afiliado_int,afiliado_id_ospim,afiliado_nombre,afiliado_direccion,observaciones,observacion_interna) VALUES"
                    "(1001,1,'20000000001',0,999,'Copia anterior','Direccion anterior','Evento original','Interna original'),"
                    "(1002,1,'20000000002',0,998,'Baja anterior','Direccion baja anterior','Evento baja','Interna baja'),"
                    "(1003,1,NULL,NULL,NULL,NULL,NULL,'Sin afiliado',NULL)")
        cur.execute("UPDATE compras.requerimiento SET baja_fecha=now(),baja_usr='fixture' WHERE id_requerimiento=1002")
        cur.execute("INSERT INTO compras.requerimiento_detalle(id_requerimiento,tipo_item,id_medicamento,troquel,nombre_medicamento,cantidad,precio_unitario_estimado,observaciones) VALUES(1001,'MEDICAMENTO',17,222,'EVENTO MEDICAMENTO',3,12,'Detalle original')")
        cur.execute("INSERT INTO compras.requerimiento_presupuesto(id_requerimiento,dl_group_id,dl_folder_id,dl_file_entry_id,dl_file_uuid,nombre_original,nombre_persistido,titulo) VALUES(1001,7,8,900,'uuid-fixture','emitido.pdf','emitido.pdf','Emitido fixture')")
        cur.execute("INSERT INTO compras.requerimiento_afiliado_historial(id_requerimiento,afiliado_cuil_titular_anterior,afiliado_int_anterior,afiliado_cuil_titular_nuevo,afiliado_int_nuevo,modi_usr) VALUES(1001,NULL,NULL,'20000000001',0,'fixture')")


def snapshot(con):
    with con.cursor() as cur:
        # Hash synthetic rows only. All snapshots are compared in memory.
        values = {}
        for table in ['requerimiento_presupuesto', 'requerimiento_detalle', 'requerimiento_afiliado_historial']:
            values[table] = one(cur, "SELECT md5(COALESCE(string_agg(row_to_json(r)::text,E'\\n' ORDER BY row_to_json(r)::text),'')) FROM compras." + table + ' r')
        values['requirements'] = one(cur, "SELECT md5(string_agg((to_jsonb(r)" + ''.join("-'" + c + "'" for c in COPIES) + ")::text,E'\\n' ORDER BY id_requerimiento)) FROM compras.requerimiento r")
        values['cuil_index_oid'] = one(cur, "SELECT to_regclass('compras.ix_compras_requerimiento_afiliado')::oid")
        values['base_type_oid'] = one(cur, "SELECT 'compras.requerimiento_base_row'::regtype::oid")
        values['functions'] = one(cur, "SELECT md5(string_agg(p.oid::text||':'||p.xmin::text||':'||pg_get_functiondef(p.oid),E'\\n' ORDER BY p.oid)) FROM pg_proc p JOIN pg_namespace n ON n.oid=p.pronamespace WHERE n.nspname='compras'")
        values['columns'] = one(cur, "SELECT count(*) FROM pg_attribute WHERE attrelid='compras.requerimiento'::regclass AND attnum>0 AND NOT attisdropped")
        values['triggers'] = one(cur, "SELECT count(*) FROM pg_trigger t JOIN pg_proc p ON p.oid=t.tgfoid JOIN pg_namespace n ON n.oid=p.pronamespace WHERE NOT t.tgisinternal AND n.nspname='compras'")
        return values


def rejected_migration(con, label, preparation, migration, expected='P0001'):
    before = snapshot(con)
    with con.cursor() as cur:
        cur.execute('BEGIN')
        cur.execute(preparation)
        try:
            cur.execute(migration)
        except psycopg2.Error as exc:
            equal(label + ' SQLSTATE', expected, exc.pgcode)
            con.rollback()
            # autocommit connections need an explicit ROLLBACK for explicit BEGIN.
            cur.execute('ROLLBACK')
        else:
            raise AssertionError(label + ' unexpectedly accepted')
    equal(label + ' complete rollback', before, snapshot(con))


def rejected_write(con, label, query, params, expected='23503'):
    with con.cursor() as cur:
        try:
            cur.execute(query, params)
        except psycopg2.Error as exc:
            equal(label + ' SQLSTATE', expected, exc.pgcode)
        else:
            raise AssertionError(label + ' unexpectedly accepted')


def java_sql():
    data = JAVA.read_bytes()
    source = data.decode('latin-1')
    section = source[source.index('private int guardarRequerimientoCompra(Connection'):source.index('public int registrarOrdenMedica')]
    expressions = re.findall(r'stmt=con.prepareStatement\((.*?)\);', section, re.S)
    equal('actual Java writer statement count', 2, len(expressions))
    variables = re.search(r'String columnasAfiliado\s*=\s*actualizarAfiliado\s*\?\s*("[^"]*")\s*:\s*("[^"]*")', section, re.S)
    if not variables:
        raise AssertionError('actual Java affiliate columns expression not found')
    reassigned = json.loads(variables.group(1))

    def expression(value, affiliate):
        tokens = re.findall(r'"(?:\\.|[^"\\])*"|columnasAfiliado', value)
        rest = re.sub(r'"(?:\\.|[^"\\])*"|columnasAfiliado|[+\s]', '', value)
        equal('actual Java expression contains only literals and selected variable', '', rest)
        return ''.join(affiliate if t == 'columnasAfiliado' else json.loads(t) for t in tokens)
    insert = expression(expressions[0], '')
    update = expression(expressions[1], '')
    reassign = expression(expressions[1], reassigned)
    for label, query, count in [('insert', insert, 13), ('update', update, 10), ('reassignment', reassign, 12)]:
        equal('actual Java ' + label + ' bindings', count, query.count('?'))
    return insert.replace('?', '%s'), update.replace('?', '%s'), reassign.replace('?', '%s'), sha(data)


def run_tests(con, forward, reverse, metadata):
    with con.cursor() as cur:
        seeded = one(cur, 'SELECT count(*) FROM compras.requerimiento')
    if seeded == 0:
        seed(con)
    else:
        equal('resume synthetic baseline requirement count', 3, seeded)
    before = snapshot(con)
    equal('baseline physical copies reproduced', 32, before['columns'])
    with con.cursor() as cur:
        equal('baseline stale copied name reproduced', 'Copia anterior', one(cur, 'SELECT afiliado_nombre FROM compras.requerimiento_base() WHERE id=1001'))
    rejected_migration(con, 'partial physical schema', 'ALTER TABLE compras.requerimiento DROP COLUMN afiliado_email', forward)
    rejected_migration(con, 'retained schema drift', 'ALTER TABLE compras.requerimiento ALTER COLUMN cargo_ospim DROP DEFAULT', forward)
    rejected_migration(con, 'unknown local CHECK', 'ALTER TABLE compras.requerimiento ADD CONSTRAINT fixture_unknown CHECK(afiliado_nombre IS NULL OR length(afiliado_nombre)>0)', forward)
    rejected_migration(con, 'unknown local index', 'CREATE INDEX fixture_unknown_idx ON compras.requerimiento(afiliado_nombre)', forward)
    rejected_migration(con, 'unknown external view', 'CREATE VIEW public.fixture_dependency AS SELECT afiliado_nombre FROM compras.requerimiento', forward, '2BP01')
    rejected_migration(con, 'partial affiliate key', "UPDATE compras.requerimiento SET afiliado_int=NULL WHERE id_requerimiento=1001", forward)
    rejected_migration(con, 'nonexistent affiliate key', "UPDATE compras.requerimiento SET afiliado_cuil_titular='29999999999' WHERE id_requerimiento=1001", forward)
    rejected_migration(con, 'unreferenced free affiliate data', "UPDATE compras.requerimiento SET afiliado_nombre='Libre fixture' WHERE id_requerimiento=1003", forward)
    changed = next(f['definition'] for f in metadata['functions'] if f['name']=='normalizar_usuario')
    changed = changed.replace('AS $function$', 'AS $function$\n-- fixture drift', 1)
    rejected_migration(con, 'function body drift', changed, forward)
    with con.cursor() as cur:
        cur.execute(forward)
    after = snapshot(con)
    for key in ['requirements', 'requerimiento_detalle', 'requerimiento_presupuesto', 'requerimiento_afiliado_historial', 'cuil_index_oid', 'base_type_oid', 'triggers']:
        equal('forward preserves ' + key, before[key], after[key])
    equal('physical retained column count', 21, after['columns'])
    with con.cursor() as cur:
        equal('eleven copies absent', 0, one(cur, "SELECT count(*) FROM pg_attribute WHERE attrelid='compras.requerimiento'::regclass AND NOT attisdropped AND attname=ANY(%s)", (COPIES,)))
        equal('both identity keys retained', 2, one(cur, "SELECT count(*) FROM pg_attribute WHERE attrelid='compras.requerimiento'::regclass AND NOT attisdropped AND attname IN('afiliado_cuil_titular','afiliado_int')"))
        equal('all 104 functions retained', 104, one(cur, "SELECT count(*) FROM pg_proc WHERE pronamespace='compras'::regnamespace"))
        cur.execute('SELECT * FROM compras.requerimiento_base() WHERE id=1001')
        equal('base output aliases 34', [c['name'] for c in metadata['types'][0]['columns']], [d[0] for d in cur.description])
        row = dict(zip([d[0] for d in cur.description], cur.fetchone()))
        equal('base current affiliate name', 'Actual', row['afiliado_nombre'])
        equal('base current affiliate address', 'Calle actual 10', row['afiliado_direccion'])
        equal('base current affiliate id', 100, row['afiliado_id_ospim'])
        equal('soft-deleted master remains resolvable', 'Baja', one(cur, 'SELECT afiliado_nombre FROM compras.requerimiento_base() WHERE id=1002'))
        equal('without affiliate remains valid', None, one(cur, 'SELECT afiliado_nombre FROM compras.requerimiento_base() WHERE id=1003'))
        cur.execute('SELECT * FROM compras.get_requerimiento_compra_pdf(1001)')
        equal('PDF output aliases 43', 43, len(cur.description))
        cur.fetchall()
        # Repeating must not recompile functions or rewrite requirement rows.
        cur.execute(forward)
    equal('forward physical idempotence', after, snapshot(con))
    insert, update, reassign, java_sha = java_sql()
    params = [1, 1, '20000000001', 0, 100, 0, None, False, False, False, 'Java insert', 'fixture', 'Java internal']
    with con.cursor() as cur:
        java_id = one(cur, insert, params)
        equal('Java 13-bind insert reference', '20000000001', one(cur, 'SELECT afiliado_cuil_titular FROM compras.requerimiento WHERE id_requerimiento=%s', (java_id,)))
        values = [90, 10, None, False, False, 'Java update', 'Java internal update', 'fixture', java_id, 1]
        equal('Java 10-bind update', java_id, one(cur, update, values))
        values_reassign = values[:8] + ['20000000001', 1, java_id, 1]
        equal('Java 12-bind reassignment', java_id, one(cur, reassign, values_reassign))
        equal('Java reassignment current master', 'Integrante', one(cur, 'SELECT afiliado_nombre FROM compras.requerimiento_base() WHERE id=%s', (java_id,)))
        cur.execute(reassign, values[:8] + ['20000000002', 0, java_id, 2])
        equal('Java compare-and-set stale state rejects', [], cur.fetchall())
    rejected_write(con, 'MATCH FULL partial-null key', insert, params[:3] + [None] + params[4:])
    rejected_write(con, 'nonexistent master key', insert, params[:2] + ['29999999999', 0] + params[4:])
    with con.cursor() as cur:
        no_affiliate = one(cur, insert, params[:2] + [None, None] + params[4:])
        equal('Java no-affiliate key pair valid', None, one(cur, 'SELECT afiliado_int FROM compras.requerimiento WHERE id_requerimiento=%s', (no_affiliate,)))
        baja_id = one(cur, insert, params[:2] + ['20000000002', 0] + params[4:])
        equal('Java baja master key valid', 'Baja', one(cur, 'SELECT afiliado_nombre FROM compras.requerimiento_base() WHERE id=%s', (baja_id,)))
    # Real legacy 23/24 overloads retain signatures and ignore demographic arguments.
    args = [None, '20000000001', 0, 777, 'IGNORED', 'IGNORED', 'XXX', 'XXX', 'IGNORED', 'IGNORED', 'IGNORED', 'IGNORED', 'IGNORED', 'IGNORED', 1, 100, 0, None, False, False, False, 'Legacy fixture', 'fixture']
    with con.cursor() as cur:
        legacy_id = one(cur, 'SELECT compras.guardar_requerimiento(' + ','.join(['%s'] * 23) + ')', args)
        equal('legacy 23 ignores copied name', 'Actual', one(cur, 'SELECT afiliado_nombre FROM compras.requerimiento_base() WHERE id=%s', (legacy_id,)))
        wrapper_id = one(cur, 'SELECT compras.guardar_requerimiento(' + ','.join(['%s'] * 24) + ')', args + ['Legacy internal'])
        equal('legacy wrapper 24 internal observation', 'Legacy internal', one(cur, 'SELECT observacion_interna FROM compras.requerimiento WHERE id_requerimiento=%s', (wrapper_id,)))
    rejected_write(con, 'legacy 23 affiliate immutable', 'SELECT compras.guardar_requerimiento(' + ','.join(['%s'] * 23) + ')', [legacy_id, '20000000001', 1] + args[3:], 'P0001')
    preserved = snapshot(con)
    with con.cursor() as cur:
        cur.execute("UPDATE public.afiliado SET nombre='Actual reversa' WHERE cuil_titular='20000000001' AND inte=0")
        cur.execute(reverse)
        equal('reverse reconstructs CURRENT master', 'Actual reversa', one(cur, 'SELECT afiliado_nombre FROM compras.requerimiento WHERE id_requerimiento=1001'))
        equal('reverse reconstructs baja master', 'Baja', one(cur, 'SELECT afiliado_nombre FROM compras.requerimiento WHERE id_requerimiento=1002'))
    reversed_state = snapshot(con)
    equal('reverse restores 32 columns', 32, reversed_state['columns'])
    for key in ['requirements', 'requerimiento_detalle', 'requerimiento_presupuesto', 'requerimiento_afiliado_historial', 'cuil_index_oid', 'base_type_oid', 'triggers']:
        equal('reverse preserves ' + key, preserved[key], reversed_state[key])
    with con.cursor() as cur:
        cur.execute(reverse)
    equal('reverse physical idempotence', reversed_state, snapshot(con))
    with con.cursor() as cur:
        cur.execute(forward)
    equal('re-advance column count', 21, snapshot(con)['columns'])
    return java_sha


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument('--database', required=True)
    parser.add_argument('--metadata', type=Path, required=True)
    parser.add_argument('--metadata-sha256', required=True)
    parser.add_argument('--auxiliary-metadata', type=Path, required=True)
    parser.add_argument('--auxiliary-sha256', required=True)
    parser.add_argument('--forward-sha256', required=True)
    parser.add_argument('--reverse-sha256', required=True)
    parser.add_argument('--receipt', type=Path, required=True)
    parser.add_argument('--resume-fixture', action='store_true', help='Only resume this previously created, preserved fixture after a failed test; never recreates it.')
    args = parser.parse_args()
    if not re.fullmatch(PREFIX + r'[a-z0-9]{6}', args.database):
        raise AssertionError('exact fixture prefix and suffix required')
    data = args.metadata.read_bytes()
    equal('metadata SHA256', args.metadata_sha256, sha(data))
    metadata = json.loads(data.decode('latin-1'))
    auxiliary_data = args.auxiliary_metadata.read_bytes()
    equal('auxiliary metadata SHA256', args.auxiliary_sha256, sha(auxiliary_data))
    auxiliary = json.loads(auxiliary_data.decode('latin-1'))
    forward, forward_info = adapt(FORWARD, args.forward_sha256, args.database)
    reverse, reverse_info = adapt(REVERSE, args.reverse_sha256, args.database)
    receipt = {'started_utc': datetime.now(timezone.utc).isoformat(), 'database': args.database,
               'host': '12.1.1.12', 'metadata_sha256': sha(data), 'forward': forward_info, 'reverse': reverse_info,
               'test_sha256': sha(Path(__file__).read_bytes()), 'fixture_preserved': True,
               'auxiliary_metadata_sha256': sha(auxiliary_data), 'resumed_existing_fixture': args.resume_fixture}
    con = None
    try:
        if not args.resume_fixture:
            admin = connect('postgres')
            try:
                with admin.cursor() as cur:
                    equal('new fixture does not already exist', False, one(cur, 'SELECT EXISTS(SELECT 1 FROM pg_database WHERE datname=%s)', (args.database,)))
                    cur.execute(sql.SQL("CREATE DATABASE {} WITH TEMPLATE template0 ENCODING 'UTF8' LC_COLLATE 'es_AR.UTF-8' LC_CTYPE 'es_AR.UTF-8'").format(sql.Identifier(args.database)))
            finally:
                admin.close()
        con = connect(args.database)
        install_baseline(con, metadata, auxiliary, args.resume_fixture)
        receipt['java_source_sha256'] = run_tests(con, forward, reverse, metadata)
        receipt['status'] = 'passed'
    except Exception as exc:
        receipt['status'] = 'failed'
        receipt['error_type'] = type(exc).__name__
        receipt['sqlstate'] = getattr(exc, 'pgcode', None)
        # Do not persist PostgreSQL diagnostics containing synthetic or operational row values.
        raise
    finally:
        if con is not None:
            con.close()
        receipt['connections_closed'] = True
        receipt['checks'] = CHECKS
        receipt['check_count'] = len(CHECKS)
        receipt['finished_utc'] = datetime.now(timezone.utc).isoformat()
        args.receipt.parent.mkdir(parents=True, exist_ok=True)
        args.receipt.write_bytes((json.dumps(receipt, indent=2, ensure_ascii=True) + '\n').encode('latin-1'))
        print('status=' + receipt.get('status', 'unknown') + ' checks=' + str(len(CHECKS)) + ' connections_closed=True fixture_preserved=True')


if __name__ == '__main__':
    main()
