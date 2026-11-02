import argparse
import hashlib
import json
import os
import re
from pathlib import Path

import psycopg2
from psycopg2 import sql


ROOT = Path(__file__).resolve().parents[2]
SOURCE = ROOT / 'docs/sql/compras/20261102_auditoria_reasignacion_afiliado.sql'
PREFIX = 'compras_auditoria_secuencia_test_20261102_'
MARKER = 'Fixture sintetico exclusivo de auditoria de secuencia Compras 20261102'
OWNED = 'requerimiento_afiliado_historial_id_historial_seq'
FOREIGN = 'secuencia_ajena_fixture'
TABLE = 'compras.requerimiento_afiliado_historial'
checks = 0


def require(condition, message):
    global checks
    if not condition:
        raise AssertionError(message)
    checks += 1


def scalar(cursor, query, params=()):
    cursor.execute(query, params)
    return cursor.fetchone()[0]


def snapshot(cursor):
    cursor.execute("SELECT oid,pg_get_expr(adbin,adrelid) FROM pg_attrdef WHERE adrelid=%s::regclass "
                   "AND adnum=(SELECT attnum FROM pg_attribute WHERE attrelid=%s::regclass "
                   "AND attname='id_historial')", (TABLE, TABLE))
    default = cursor.fetchone()
    cursor.execute("SELECT s.relname,d.refobjid::regclass::text,d.refobjsubid,d.deptype "
                   "FROM pg_depend d JOIN pg_class s ON s.oid=d.objid "
                   "WHERE d.classid='pg_class'::regclass AND d.refclassid='pg_class'::regclass "
                   "AND s.oid=ANY(ARRAY[%s::regclass,%s::regclass]) AND d.deptype='a' ORDER BY s.relname",
                   ('compras.' + OWNED, 'compras.' + FOREIGN))
    owners = cursor.fetchall()
    states = []
    for name in (OWNED, FOREIGN):
        cursor.execute(sql.SQL('SELECT last_value,is_called FROM {}.{}').format(sql.Identifier('compras'), sql.Identifier(name)))
        states.append(cursor.fetchone())
    return (default, owners, states)


def apply(cursor, migration):
    try:
        cursor.execute(migration)
    except Exception:
        cursor.execute('ROLLBACK')
        raise


def rejects(cursor, migration, case):
    before = snapshot(cursor)
    try:
        apply(cursor, migration)
    except psycopg2.Error as error:
        require(error.pgcode == 'P0001', case + ': no fue un rechazo del validador')
        require('secuencia' in str(error).lower(), case + ': motivo de rechazo diferente')
        require(snapshot(cursor) == before, case + ': se reparo o avanzo una secuencia/default/ownership')
        print('RECHAZO_SIN_REPARACION_OK ' + case)
        return
    raise AssertionError('DEFECTO_REPRODUCIDO: el validador acepto ' + case)


def prepare(cursor, migration):
    exists = scalar(cursor, "SELECT EXISTS(SELECT 1 FROM pg_namespace WHERE nspname='compras')")
    if exists:
        require(scalar(cursor, "SELECT obj_description(oid,'pg_namespace') FROM pg_namespace WHERE nspname='compras'") == MARKER,
                'El esquema compras no tiene el marcador sintetico esperado; no modificar')
    else:
        cursor.execute('CREATE SCHEMA compras')
        cursor.execute('COMMENT ON SCHEMA compras IS %s', (MARKER,))
        cursor.execute('CREATE TABLE compras.requerimiento(id_requerimiento INTEGER PRIMARY KEY)')
    if scalar(cursor, 'SELECT to_regclass(%s)', (TABLE,)) is None:
        apply(cursor, migration)
    require(scalar(cursor, 'SELECT to_regclass(%s)', ('compras.' + OWNED,)) is not None,
            'La secuencia propia sintetica no existe')
    # Estas alteraciones preparan un caso sintetico de la base exclusiva.
    # La migracion bajo prueba nunca repara ni consume secuencias existentes.
    cursor.execute(sql.SQL('ALTER SEQUENCE {}.{} OWNED BY compras.requerimiento_afiliado_historial.id_historial')
                   .format(sql.Identifier('compras'), sql.Identifier(OWNED)))
    cursor.execute("ALTER TABLE compras.requerimiento_afiliado_historial ALTER COLUMN id_historial "
                   "SET DEFAULT nextval('compras.requerimiento_afiliado_historial_id_historial_seq'::regclass)")
    if scalar(cursor, 'SELECT to_regclass(%s)', ('compras.' + FOREIGN,)) is None:
        cursor.execute(sql.SQL('CREATE SEQUENCE {}.{}').format(sql.Identifier('compras'), sql.Identifier(FOREIGN)))


def main():
    parser = argparse.ArgumentParser(description='Prueba de secuencia de auditoria en UNA base fixture ya creada')
    parser.add_argument('--expected-source-sha256', required=True)
    args = parser.parse_args()
    host = os.environ['MOLINEROS_QA_HOST']
    database = os.environ['MOLINEROS_AUDITORIA_TEST_DATABASE']
    require(host == '12.1.1.12', 'Host incorrecto: solo QA12')
    require(re.fullmatch(re.escape(PREFIX) + r'[a-z0-9_]+', database) is not None and len(database) <= 63,
            'La prueba exige una base fixture exclusiva con prefijo exacto')
    raw = SOURCE.read_bytes()
    require(hashlib.sha256(raw).hexdigest() == args.expected_source_sha256.lower(), 'Hash fuente diferente al registrado')
    original = raw.decode('latin-1')
    guard = "current_database() IS DISTINCT FROM 'devmolineros'"
    require(original.count(guard) == 1, 'Se exige exactamente un guard de base para el adaptador')
    require("host(inet_server_addr()) IS DISTINCT FROM '12.1.1.12'" in original, 'Guard host ausente')
    adapted = original.replace(guard, "current_database() IS DISTINCT FROM '" + database + "'")
    require(adapted.replace("current_database() IS DISTINCT FROM '" + database + "'", guard) == original,
            'El adaptador modifico algo mas que el literal de base')
    print('FUENTE_SHA256=' + hashlib.sha256(raw).hexdigest())
    print('ADAPTADA_SHA256=' + hashlib.sha256(adapted.encode('latin-1')).hexdigest())
    print('ADAPTADOR=1 literal current_database; host y esquema compras intactos')
    connection = None
    try:
        connection = psycopg2.connect(host=host, port=5432, dbname=database,
                                      user=os.environ['MOLINEROS_QA_USER'], password=os.environ['MOLINEROS_QA_PASSWORD'],
                                      connect_timeout=8, application_name='codex_compras_auditoria_secuencia_fixture',
                                      options='-c statement_timeout=15000 -c lock_timeout=2000')
        connection.autocommit = True
        cursor = connection.cursor()
        cursor.execute("SELECT host(inet_server_addr()),current_database(),current_setting('server_version_num')::integer")
        actual_host, actual_database, version = cursor.fetchone()
        require(actual_host == host and actual_database == database, 'Destino efectivo diferente del fixture autorizado')
        require(90600 <= version < 100000, 'Se exige PostgreSQL 9.6 para esta prueba legacy')
        print('CONTEXTO=' + json.dumps({'host': actual_host, 'database': actual_database, 'version_num': version, 'schema': 'compras'}))
        prepare(cursor, adapted)
        before = snapshot(cursor)
        apply(cursor, adapted)
        require(snapshot(cursor) == before, 'El esquema correcto consumio/modifico secuencias o default')
        apply(cursor, adapted)
        require(snapshot(cursor) == before, 'La repeticion no fue idempotente')
        print('CORRECTA_Y_REPETICION_SIN_AVANCE_OK')
        cursor.execute('SET search_path=compras,public,pg_catalog')
        caller_path = scalar(cursor, 'SHOW search_path')
        before = snapshot(cursor)
        apply(cursor, adapted)
        require(snapshot(cursor) == before, 'El search_path atipico altero el caso serial valido')
        require(scalar(cursor, 'SHOW search_path') == caller_path, 'El SET LOCAL filtro el search_path del caller')
        print('SERIAL_VALIDO_SEARCH_PATH_ATIPICO_OK')
        cursor.execute("ALTER TABLE compras.requerimiento_afiliado_historial ALTER COLUMN id_historial "
                       "SET DEFAULT nextval('compras.secuencia_ajena_fixture'::regclass)")
        rejects(cursor, adapted, 'OWNED_BY_PROPIA_DEFAULT_AJENO')
        cursor.execute("ALTER TABLE compras.requerimiento_afiliado_historial ALTER COLUMN id_historial "
                       "SET DEFAULT nextval('compras.requerimiento_afiliado_historial_id_historial_seq'::regclass)")
        cursor.execute(sql.SQL('ALTER SEQUENCE {}.{} OWNED BY NONE').format(sql.Identifier('compras'), sql.Identifier(OWNED)))
        rejects(cursor, adapted, 'SIN_OWNED_BY_DEFAULT_PROPIO')
        cursor.execute("ALTER TABLE compras.requerimiento_afiliado_historial ALTER COLUMN id_historial "
                       "SET DEFAULT nextval('compras.secuencia_ajena_fixture'::regclass)")
        rejects(cursor, adapted, 'SIN_OWNED_BY_DEFAULT_AJENO')
        cursor.execute(sql.SQL('ALTER SEQUENCE {}.{} OWNED BY compras.requerimiento_afiliado_historial.id_historial')
                       .format(sql.Identifier('compras'), sql.Identifier(OWNED)))
        cursor.execute("ALTER TABLE compras.requerimiento_afiliado_historial ALTER COLUMN id_historial "
                       "SET DEFAULT nextval('compras.requerimiento_afiliado_historial_id_historial_seq'::regclass) + 1")
        rejects(cursor, adapted, 'OWNED_BY_PROPIA_DEFAULT_TRANSFORMADO')
        cursor.execute("ALTER TABLE compras.requerimiento_afiliado_historial ALTER COLUMN id_historial SET DEFAULT 1")
        rejects(cursor, adapted, 'OWNED_BY_PROPIA_DEFAULT_CONSTANTE_SIN_DEPENDENCIA')
        cursor.execute("ALTER TABLE compras.requerimiento_afiliado_historial ALTER COLUMN id_historial "
                       "SET DEFAULT nextval('compras.requerimiento_afiliado_historial_id_historial_seq'::regclass) "
                       "+ nextval('compras.secuencia_ajena_fixture'::regclass)")
        rejects(cursor, adapted, 'OWNED_BY_PROPIA_DEFAULT_DOS_SECUENCIAS')
        cursor.execute("ALTER TABLE compras.requerimiento_afiliado_historial ALTER COLUMN id_historial "
                       "SET DEFAULT nextval('compras.requerimiento_afiliado_historial_id_historial_seq'::regclass)")
        cursor.execute(sql.SQL('ALTER SEQUENCE {}.{} OWNED BY compras.requerimiento_afiliado_historial.id_historial')
                       .format(sql.Identifier('compras'), sql.Identifier(FOREIGN)))
        rejects(cursor, adapted, 'DOS_SECUENCIAS_OWNED_BY_MISMA_COLUMNA')
        cursor.execute(sql.SQL('ALTER SEQUENCE {}.{} OWNED BY NONE').format(sql.Identifier('compras'), sql.Identifier(FOREIGN)))
        cursor.execute("ALTER TABLE compras.requerimiento_afiliado_historial ALTER COLUMN id_historial "
                       "SET DEFAULT nextval('compras.requerimiento_afiliado_historial_id_historial_seq'::regclass)")
        before = snapshot(cursor)
        apply(cursor, adapted)
        require(snapshot(cursor) == before, 'Caso final correcto cambio metadatos/secuencias')
        print('COMPRAS_AUDITORIA_SECUENCIA_OK checks=' + str(checks) + '; fixture preservado; sin datos operativos')
    finally:
        if connection is not None:
            connection.close()
        print('CONEXION_FIXTURE_CERRADA')


if __name__ == '__main__':
    main()
