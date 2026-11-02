import json
import os
import re
from pathlib import Path
import psycopg2

ROOT = Path(__file__).resolve().parents[2]
SOURCE = ROOT / 'ext-impl/src/ar/com/ospim/compras/requerimientos/service/BusquedaRequerimientoCompraServiceImpl.java'
SQL = {}
for name, expression in re.findall(r'private static final String (SQL_\w+)\s*=\s*(.*?);', SOURCE.read_text(encoding='latin-1'), re.S):
    SQL[name] = ''.join(json.loads(token) if token.startswith('"') else SQL[token]
                        for token in re.findall(r'"(?:\\.|[^"\\])*"|SQL_\w+', expression))


def execute(cursor, query, params=()):
    cursor.execute(query.replace('%', '%%').replace('?', '%s'), params)
    return [dict(zip([column[0] for column in cursor.description], row)) for row in cursor.fetchall()]


def equal(label, expected, actual):
    if expected != actual:
        raise AssertionError(label + ': expected=' + repr(expected) + ' actual=' + repr(actual))


def affiliate_fixture(integrante=0, missing=False):
    # Old copied values remain deliberately different from both authoritative rows.
    return """WITH fixture_requerimiento AS (
        SELECT 41 AS id_requerimiento,now()::timestamp AS alta_fecha,'fixture'::varchar AS alta_usr,
        NULL::timestamp AS modi_fecha,NULL::varchar AS modi_usr,NULL::timestamp AS baja_fecha,NULL::varchar AS baja_usr,
        '20000000001'::varchar AS afiliado_cuil_titular,""" + str(int(integrante)) + """ AS afiliado_int,
        'Nombre anterior'::varchar AS afiliado_nombre,'Apellido anterior'::varchar AS afiliado_apellido,
        10 AS id_sector,100 AS cargo_ospim,0 AS cargo_tercerizadora,NULL::varchar AS id_tercerizadora,
        FALSE AS recupero,FALSE AS surge,FALSE AS legales,'Evento conservado'::text AS observaciones,
        NULL::text AS observacion_interna,NULL::varchar AS motivo_baja,1 AS estado),
        fixture_afiliado AS (SELECT '20000000001'::varchar AS cuil_titular,0 AS inte,100 AS id_ospim,
        ?::varchar AS nombre,'Actual'::varchar AS apellido,'DNI'::varchar AS documento_tipo,'123'::varchar AS docu_numero,
        NULL::varchar AS email,1 AS id_seccional,NULL::timestamp AS baja_fecha WHERE """ + ('FALSE' if missing else 'TRUE') + """
        UNION ALL SELECT '20000000001',1,101,'Segundo','Vigente','DNI','456',NULL,1,NULL),
        fixture_sector AS (SELECT 10 AS id_sector,'SECTOR'::varchar AS descripcion,TRUE AS requiere_afiliado),
        fixture_seccional AS (SELECT 1 AS id_seccional,'Seccional'::varchar AS descripcion),
        fixture_domicilio AS (SELECT '20000000001'::varchar AS cuil_titular,0 AS inte,'P'::varchar AS domi_tipo,
        'fixture'::varchar AS alta_usr,NULL::timestamp AS baja_fecha,1 AS id_domicilio,
        'Calle vigente'::varchar AS calle,'10'::varchar AS numero,NULL::varchar AS piso,NULL::varchar AS depto,
        NULL::varchar AS cod_area_celular,NULL::varchar AS celular,NULL::varchar AS cod_area_telefono,
        NULL::varchar AS telefono,1 AS localidad,1 AS provincia),
        fixture_localidad AS (SELECT 1 AS id_localidad,'Localidad'::varchar AS detalle),
        fixture_provincia AS (SELECT 1 AS id_provincia,'Provincia'::varchar AS detalle) """


def replace_affiliate(query):
    for source, fixture in [('compras.requerimiento r', 'fixture_requerimiento r'),
                            ('compras.sector_requerimiento', 'fixture_sector'),
                            ('public.afiliado', 'fixture_afiliado'), ('public.seccional', 'fixture_seccional'),
                            ('public.afi_domicilio', 'fixture_domicilio'), ('public.localidad', 'fixture_localidad'),
                            ('public.provincia', 'fixture_provincia')]:
        query = query.replace(source, fixture)
    return query


def detail_fixture(prestacion=101, medicamento=1, tipo='NOMENCLADOR', missing=False):
    return """WITH fixture_detalle AS (SELECT 17 AS id_detalle,41 AS id_requerimiento,
        '""" + tipo + """'::varchar AS tipo_item,1 AS id_tipo_prestacion,""" + str(int(prestacion)) + """ AS id_prestacion,
        """ + str(int(medicamento)) + """ AS id_medicamento,3 AS cantidad,12::numeric AS precio_unitario_estimado,
        36::numeric AS precio_total_estimado,1 AS id_prestador,'Evento'::text AS observaciones,
        'Descripcion copiada'::varchar AS descripcion_nomenclador,NULL::timestamp AS baja_fecha),
        fixture_tipo AS (SELECT 1 AS id_tipo_prestacion,'Tipo'::varchar AS descripcion),
        fixture_nomenclador AS (SELECT 101 AS id_prestacion,8 AS id_tipo_nomenclador,'ACTUAL'::varchar AS codigo,
        ?::varchar AS descripcion WHERE """ + ('FALSE' if missing else 'TRUE') + """
        UNION ALL SELECT 202,9,'SEGUNDO','Otra prestacion'),
        fixture_medicamentos AS (SELECT 1 AS id_medicamento,10 AS troquel,'Nombre'::varchar AS nombre,
        ?::varchar AS presentacion UNION ALL SELECT 2,20,'Otro','Envase'),
        fixture_prestador AS (SELECT 1 AS id_prestador,'123'::varchar AS cuit,'Prestador vigente'::varchar AS descripcion) """


def replace_detail(query):
    for source, fixture in [('compras.requerimiento_detalle', 'fixture_detalle'),
                            ('compras.tipo_prestacion', 'fixture_tipo'), ('autorizaciones.nomenclador', 'fixture_nomenclador'),
                            ('public.medicamentos', 'fixture_medicamentos'), ('public.prestador', 'fixture_prestador')]:
        query = query.replace(source, fixture)
    return query


def validate_qa_readers(cursor):
    # Actual QA records are read only; only counts and column contracts are reported.
    req = execute(cursor, "SELECT id_requerimiento FROM compras.requerimiento ORDER BY id_requerimiento DESC LIMIT 1")[0]['id_requerimiento']
    doc = execute(cursor, "SELECT id_requerimiento_presupuesto,id_requerimiento,id_prestador FROM compras.requerimiento_presupuesto WHERE baja_fecha IS NULL ORDER BY id_requerimiento_presupuesto LIMIT 1")
    document = doc[0] if doc else {'id_requerimiento_presupuesto': 0, 'id_requerimiento': req, 'id_prestador': 0}
    affiliate = execute(cursor, "SELECT afiliado_cuil_titular,afiliado_int,id_requerimiento FROM compras.requerimiento WHERE afiliado_cuil_titular IS NOT NULL AND afiliado_int IS NOT NULL ORDER BY id_requerimiento DESC LIMIT 1")[0]
    det_req = execute(cursor, "SELECT id_requerimiento FROM compras.requerimiento_detalle WHERE baja_fecha IS NULL ORDER BY id_detalle DESC LIMIT 1")[0]['id_requerimiento']
    args = {
        'SQL_AFILIADO_VIGENTE': [affiliate['afiliado_cuil_titular'], affiliate['afiliado_int']],
        'SQL_BUSCAR_REQUERIMIENTOS_FILTRADOS': [None] * 11 + [99],
        'SQL_BUSCAR_REQUERIMIENTOS': [None] * 11 + [99],
        'SQL_BUSCAR_REQUERIMIENTOS_INICIALES': [99, 5, 0],
        'SQL_GET_REQUERIMIENTO': [req], 'SQL_GET_REQUERIMIENTO_DETALLE': [det_req],
        'SQL_LISTAR_TIPOS_PRESTACION': [], 'SQL_GET_SECTOR': [1],
        'SQL_BUSCAR_EMPRESAS_COTIZACION': ['00000000000', None, None, 5],
        'SQL_BUSCAR_EMPRESAS_COTIZACION_RAPIDA': ['00000000000', None, None, 5],
        'SQL_LISTAR_PRESUPUESTOS': [document['id_requerimiento'], 1],
        'SQL_GET_PRESUPUESTO': [document['id_requerimiento_presupuesto'], document['id_requerimiento'], 1],
        'SQL_LISTAR_COTIZACIONES_EMPRESA': [document['id_requerimiento'], 3],
        'SQL_GET_COTIZACION_EMPRESA': [document['id_requerimiento_presupuesto'], document['id_requerimiento'], 3],
        'SQL_GET_ORDEN_MEDICA': [document['id_requerimiento']],
        'SQL_BUSCAR_ITEMS_HISTORICOS_AFILIADO': [affiliate['afiliado_cuil_titular'], affiliate['afiliado_int'], 1, 0, 5],
        'SQL_TIENE_SITUACION_MEDICA_VIGENTE': [affiliate['afiliado_cuil_titular'], affiliate['afiliado_int']],
        'SQL_LISTAR_PRESTADORES_ADJUDICADOS': [det_req],
        'SQL_LISTAR_PRESTADORES_ADJUDICADOS_BATCH': ['{' + str(det_req) + '}', 99],
        'SQL_LISTAR_PRESUPUESTOS_PRESTADOR': [document['id_requerimiento'], document['id_prestador'] or 0],
        'SQL_GET_PEDIDO_COTIZACION_PRESTADOR': [document['id_requerimiento'], document['id_prestador'] or 0, 'ENVIADO', 'COTIZADO'],
        'SQL_GET_REQUERIMIENTO_PDF': [det_req]
    }
    for name, params in args.items():
        query = SQL[name]
        cursor.execute('EXPLAIN (COSTS FALSE) ' + query.replace('%', '%%').replace('?', '%s'), params)
        cursor.fetchall()
        if name == 'SQL_AFILIADO_VIGENTE':
            # Read-only verification plans the exact lock, then reads without taking it.
            query = query.replace('FOR SHARE OF a', '')
        if name == 'SQL_BUSCAR_EMPRESAS_COTIZACION':
            # This legacy trimmed full-table selector exceeds 15s in QA, as did its old function.
            # LIMIT 0 validates the real column/bind contract without repeating the expensive scan.
            query = 'SELECT * FROM (' + query + ') bounded_contract LIMIT 0'
        rows = execute(cursor, query, params)
        print('QA_READER_OK ' + name + ' rows=' + str(len(rows)) + ' columns=' + str(len(cursor.description)))
    equal('PDF aliases retained', 43, len(cursor.description))
    print('QA_READERS_OK count=' + str(len(args)) + '; affiliate lock planned only; nonrapid company selector LIMIT 0')


def main():
    # Explicit, required destination and secret in environment; never written to logs/files.
    connection = psycopg2.connect(host=os.environ['MOLINEROS_QA_HOST'], dbname=os.environ['MOLINEROS_QA_DATABASE'],
                                  user=os.environ['MOLINEROS_QA_USER'], password=os.environ['MOLINEROS_QA_PASSWORD'],
                                  connect_timeout=8, application_name='codex_compras_lecturas_prueba_select',
                                  options='-c default_transaction_read_only=on -c statement_timeout=15000 -c lock_timeout=2000')
    try:
        cursor = connection.cursor()
        equal('read-only connection', 'on', execute(cursor, 'SHOW transaction_read_only')[0]['transaction_read_only'])
        for name, query in SQL.items():
            if re.search(r'compras\.[a-z_]+\s*\(', query):
                raise AssertionError('Application function remains: ' + name)
        query = replace_affiliate(SQL['SQL_GET_REQUERIMIENTO'])
        row = execute(cursor, affiliate_fixture() + query, ['Nombre vigente', 41])[0]
        equal('current master name', 'Nombre vigente', row['afiliado_nombre'])
        equal('event retained', 'Evento conservado', row['observaciones'])
        row = execute(cursor, affiliate_fixture() + query, ['Nombre corregido', 41])[0]
        equal('master edit changes display', 'Nombre corregido', row['afiliado_nombre'])
        row = execute(cursor, affiliate_fixture(integrante=1) + query, ['Nombre vigente', 41])[0]
        equal('only reference change resolves second affiliate', 'Segundo', row['afiliado_nombre'])
        equal('dependent document follows reference', '456', row['afiliado_documento_nro'])
        row = execute(cursor, affiliate_fixture() + query, [None, 41])[0]
        equal('master NULL does not restore copy', None, row['afiliado_nombre'])
        row = execute(cursor, affiliate_fixture(missing=True) + query, ['Nombre vigente', 41])[0]
        equal('missing master retains requirement', 41, row['id'])
        equal('missing master explicit', '[AFILIADO NO RESUELTO]', row['afiliado_nombre_apellido'])
        equal('missing master not old copy', None, row['afiliado_nombre'])
        query = replace_detail(SQL['SQL_GET_REQUERIMIENTO_DETALLE'])
        row = execute(cursor, detail_fixture() + query, ['Prestacion vigente', 'Caja', 41])[0]
        equal('current detail description', 'Prestacion vigente', row['descripcion_nomenclador'])
        equal('event quantity retained', 3, row['cantidad'])
        equal('event price retained', 36, row['precio_total_estimado'])
        row = execute(cursor, detail_fixture(prestacion=202) + query, ['Prestacion vigente', 'Caja', 41])[0]
        equal('only service ID change replaces description', 'Otra prestacion', row['descripcion_nomenclador'])
        equal('only service ID change replaces code', 'SEGUNDO', row['codigo_nomenclador'])
        equal('only service ID change replaces catalog type', 9, row['id_tipo_nomenclador'])
        row = execute(cursor, detail_fixture() + query, [None, 'Caja', 41])[0]
        equal('detail NULL clears copy', None, row['descripcion_nomenclador'])
        row = execute(cursor, detail_fixture(missing=True) + query, ['Prestacion vigente', 'Caja', 41])[0]
        equal('missing service explicit', '[PRESTACION NO RESUELTA]', row['descripcion_item'])
        row = execute(cursor, detail_fixture(tipo='MEDICAMENTO') + query, ['Prestacion vigente', 'Caja', 41])[0]
        equal('medication current display', 'Nombre Caja', row['nombre_medicamento'])
        row = execute(cursor, detail_fixture(tipo='MEDICAMENTO', medicamento=2) + query, ['Prestacion vigente', 'Caja', 41])[0]
        equal('only medication ID change updates all dependent display', 'Otro Envase', row['nombre_medicamento'])
        equal('only medication ID change updates troquel', 20, row['troquel'])
        validate_qa_readers(cursor)
        print('COMPRAS_LECTURAS_VIGENTES_SQL_OK assertions=22; actual PostgreSQL SELECT with VALUES fixtures; no writes')
    finally:
        connection.rollback()
        connection.close()


if __name__ == '__main__':
    main()
