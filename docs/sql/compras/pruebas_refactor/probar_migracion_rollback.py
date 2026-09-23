"""Ensayo exclusivo de FASE 1 contra desarrollo. Siempre revierte la transaccion.
No envia mails, no crea RP ni archivos DL y no utiliza secuencias existentes.
Requiere psycopg2 en el entorno de pruebas. No guarda ni imprime credenciales.
"""
from pathlib import Path
import sys
import re
import hashlib
import subprocess
from datetime import datetime
from pglast import parse_sql,ast
from pglast.stream import RawStream
import xml.etree.ElementTree as ET
from urllib.parse import urlparse
import psycopg2

ROOT = Path(__file__).resolve().parents[4]
CONFIG = Path('C:/apache-tomcat-8.5.23/webapps/ROOT/WEB-INF/classes/c3p0-config.xml')
if not CONFIG.is_file():
    print('PENDIENTE: no existe la configuracion desplegada de Tomcat.')
    sys.exit(2)
cfg = {e.get('name'): e.text for e in ET.fromstring(CONFIG.read_bytes()).findall("named-config[@name='portal-ospim']/property")}
uri = urlparse(cfg['jdbcUrl'].replace('jdbc:', '', 1))
if '@' in uri.hostname or '@' in uri.path:
    print('PENDIENTE: configuracion sin resolver.')
    sys.exit(2)
con = psycopg2.connect(host=uri.hostname, port=uri.port, dbname=uri.path[1:], user=cfg['user'], password=cfg['password'], connect_timeout=8)
con.autocommit = False
cur = con.cursor()
checks = 0
cur.execute('SELECT current_database(),version()')
entorno = cur.fetchone()
if entorno[0] != 'devmolineros':
    con.rollback()
    con.close()
    raise RuntimeError('Este runner solo admite la base local de desarrollo devmolineros')
print('INICIO',datetime.now().isoformat(),entorno)
print('HEAD',subprocess.check_output(['git','rev-parse','HEAD'],cwd=str(ROOT),text=True).strip())
print('Solo FASE 1. FASE 2 no se carga ni se ejecuta.')

def comprobar(sql, parametros=None, esperado=True):
    global checks
    cur.execute(sql, parametros)
    obtenido = cur.fetchone()[0]
    if obtenido != esperado:
        raise AssertionError('Verificacion %d: resultado inesperado' % (checks + 1))
    checks += 1

try:
    cur.execute("SET LOCAL statement_timeout='120s'")
    cur.execute("SET LOCAL lock_timeout='5s'")
    sql = (ROOT/'docs/sql/compras/20260923_refactor_compras_fase1_expand.sql').read_text(encoding='latin1')
    # Equivale al batch psql con ON_ERROR_STOP: el primer error interrumpe,
    # finally revierte toda la transaccion. Nunca se envia COMMIT al servidor.
    assert sql.rstrip().endswith('COMMIT;')
    assert len(re.findall(r'^COMMIT;$',sql,re.M)) == 1
    sql = sql[:sql.rfind('COMMIT;')].replace('BEGIN;\n','',1)
    tablas = ['requerimiento','requerimiento_detalle','requerimiento_presupuesto',
              'requerimiento_cotizacion_prestador','requerimiento_pedido_cotizacion',
              'requerimiento_reclamo_prestacional']

    def resumen():
        datos = {}
        for tabla in tablas:
            cur.execute("SELECT count(*), COALESCE(sum(('x'||substr(md5(row_to_json(t)::text),1,16))::bit(64)::bigint::numeric),0), "
                        "COALESCE(sum(('x'||substr(md5(row_to_json(t)::text),17,16))::bit(64)::bigint::numeric),0) FROM compras."+tabla+' t')
            datos[tabla] = cur.fetchone()
        return datos

    def permisos():
        cur.execute("SELECT p.oid::regprocedure::text,p.proowner, a.grantee,a.privilege_type,a.is_grantable FROM pg_proc p "
                    "CROSS JOIN LATERAL aclexplode(COALESCE(p.proacl,acldefault('f',p.proowner))) a "
                    "WHERE p.pronamespace='compras'::regnamespace ORDER BY 1,2,3,4,5")
        datos = {}
        for fila in cur.fetchall(): datos.setdefault(fila[0],[]).append(fila[1:])
        return datos

    def secuencias():
        cur.execute("SELECT relname FROM pg_class WHERE relnamespace='compras'::regnamespace AND relkind='S' ORDER BY relname")
        datos = {}
        for nombre in [r[0] for r in cur.fetchall()]:
            cur.execute('SELECT last_value,is_called FROM compras."'+nombre.replace('"','""')+'"')
            datos[nombre] = cur.fetchone()
        return datos

    def externos():
        cur.execute("SELECT pg_get_functiondef(to_regprocedure('autorizaciones.busca_nomenclador_prest_med_compras(integer,character varying,integer,character varying,boolean,character varying)'))")
        funcion = cur.fetchone()[0]
        cur.execute("SELECT md5(COALESCE(string_agg(row_to_json(s)::text,'' ORDER BY row_to_json(s)::text),'')) FROM public.system_config s")
        return (funcion,cur.fetchone()[0])

    contenido_antes, permisos_antes, secuencias_antes, externos_antes = resumen(), permisos(), secuencias(), externos()
    cur.execute("SELECT count(*) FROM pg_class WHERE relnamespace='compras'::regnamespace AND relkind='r'")
    tablas_antes = cur.fetchone()[0]
    cur.execute(sql)
    assert resumen() == contenido_antes, 'Cambios en datos historicos'
    assert secuencias() == secuencias_antes, 'Se consumieron secuencias'
    assert externos() == externos_antes, 'Cambios externos'
    permisos_despues = permisos()
    assert all(permisos_despues.get(k)==v for k,v in permisos_antes.items()), 'Cambios de propietario/EXECUTE'
    checks += 4
    for tabla, resumen_tabla in contenido_antes.items():
        print('CONSERVADO',tabla,'filas',resumen_tabla[0],'huellas iguales')
    # Verificar cuerpos de contratos contra el canonico, sin ejecutarlo.
    canonico=(ROOT/'ext-impl/src/ar/com/ospim/compras/sql/compras_schema.sql').read_text(encoding='latin1')
    esperadas={}
    for node in parse_sql(canonico):
        f=node.stmt
        if not isinstance(f,ast.CreateFunctionStmt): continue
        args=[RawStream()(p.argType).split('(')[0] for p in (f.parameters or []) if p.mode.value not in ('o','t')]
        firma='compras.'+f.funcname[-1].sval+'('+','.join(args)+')'
        cuerpo=next(o.arg[0].sval for o in f.options if o.defname=='as')
        esperadas[firma]=hashlib.md5(cuerpo.encode('utf-8')).hexdigest()
    for firma,huella in esperadas.items():
        cur.execute("SELECT md5(convert_to(replace(prosrc,E'\\r\\n',E'\\n'),'UTF8')) FROM pg_proc WHERE oid=to_regprocedure(%s)",(firma,))
        actual=cur.fetchone()
        assert actual is None or actual[0]==huella,'Cuerpo divergente: '+firma
        checks+=1
    # Las dos configuraciones existentes solo reciben campos nuevos.
    # Repeticion antes de los fixtures: los contratos iguales no se eliminan.
    cur.execute("SELECT oid::regprocedure::text,oid FROM pg_proc WHERE pronamespace='compras'::regnamespace ORDER BY 1")
    oids = cur.fetchall()
    cur.execute(sql)
    cur.execute("SELECT oid::regprocedure::text,oid FROM pg_proc WHERE pronamespace='compras'::regnamespace ORDER BY 1")
    assert cur.fetchall() == oids, 'La repeticion cambio OIDs de funciones'
    assert resumen() == contenido_antes
    checks += 2
    # Identidades de prueba explicitas, sin nextval ni alteracion de contadores.
    cur.execute("SELECT LEAST(COALESCE(min(id_requerimiento),0),0)-1 FROM compras.requerimiento")
    req = cur.fetchone()[0]
    cur.execute("SELECT LEAST(COALESCE(min(id_detalle),0),0)-1 FROM compras.requerimiento_detalle")
    det = cur.fetchone()[0]
    cur.execute("INSERT INTO compras.requerimiento(id_requerimiento,estado,id_sector,cargo_ospim,cargo_tercerizadora,recupero,surge,alta_usr) VALUES (%s,1,3,0,100,false,false,'prueba_rollback')", (req,))
    comprobar("SELECT count(*)=10 FROM pg_class c JOIN pg_namespace n ON n.oid=c.relnamespace WHERE n.nspname='compras' AND c.relkind='r'")
    comprobar("SELECT cargo_ospim=0 AND recupero AND NOT surge FROM compras.get_requerimiento(%s)", (req,))
    comprobar("SELECT count(*)=6 FROM compras.listar_estados_requerimiento()")
    comprobar("SELECT descripcion='A COTIZAR' AND descripcion_visual='ENVIADO A COTIZAR' FROM compras.estado_requerimiento WHERE id_estado=2")
    comprobar("SELECT count(*)=2 FROM compras.tipo_prestacion_tipo_nomenclador WHERE id_tipo_prestacion=6 AND id_tipo_nomenclador IN(10,14)")
    comprobar("SELECT count(*)=3 FROM information_schema.columns WHERE table_schema='compras' AND table_name='requerimiento' AND column_name IN('recupero','cargo_ospim','afiliado_nombre')")
    comprobar('SELECT recupero=false AND cargo_tercerizadora=100 FROM compras.requerimiento WHERE id_requerimiento=%s',(req,))
    # Invocar los contratos sobre filas reales sin imprimir datos personales.
    cur.execute('SELECT id_requerimiento FROM compras.requerimiento ORDER BY id_requerimiento')
    ids = [row[0] for row in cur.fetchall()]
    for idreq in ids:
        for funcion in ['get_requerimiento','get_requerimiento_detalle','get_requerimiento_detalle_clasificado','get_requerimiento_compra_pdf','listar_ordenes_medicas_requerimiento']:
            cur.execute('SELECT * FROM compras.' + funcion + '(%s)', (idreq,))
            cur.fetchall()
            checks += 1
        cur.execute('SELECT * FROM compras.listar_documentos_requerimiento(%s,NULL::integer)', (idreq,))
        cur.fetchall()
        checks += 1
    # Total manual y automatico con el trigger real.
    cur.execute("INSERT INTO compras.requerimiento_detalle(id_detalle,id_requerimiento,tipo_item,cantidad,precio_unitario_estimado,alta_usr) VALUES(%s,%s,'OBSERVACION',2,5,'prueba_rollback')", (det,req))
    comprobar('SELECT precio_total_estimado=10 FROM compras.requerimiento_detalle WHERE id_detalle=%s', (det,))
    cur.execute('UPDATE compras.requerimiento_detalle SET precio_total_estimado=7 WHERE id_detalle=%s', (det,))
    cur.execute('UPDATE compras.requerimiento_detalle SET precio_unitario_estimado=5 WHERE id_detalle=%s', (det,))
    comprobar('SELECT precio_total_estimado=7 FROM compras.requerimiento_detalle WHERE id_detalle=%s', (det,))
    cur.execute('UPDATE compras.requerimiento_detalle SET cantidad=3 WHERE id_detalle=%s', (det,))
    comprobar('SELECT precio_total_estimado=15 FROM compras.requerimiento_detalle WHERE id_detalle=%s', (det,))
    cur.execute('UPDATE compras.requerimiento_detalle SET cantidad=4,precio_total_estimado=8 WHERE id_detalle=%s', (det,))
    comprobar('SELECT precio_total_estimado=8 FROM compras.requerimiento_detalle WHERE id_detalle=%s', (det,))
    for porcentaje in [0,1,50,100]:
        for surge in [False,True]:
            cur.execute('UPDATE compras.requerimiento SET cargo_tercerizadora=%s,surge=%s WHERE id_requerimiento=%s', (porcentaje,surge,req))
            comprobar('SELECT cargo_ospim=%s AND recupero=%s AND surge=%s FROM compras.get_requerimiento(%s)', (100-porcentaje,porcentaje>0,surge,req))
    cur.execute('SELECT compras.anular_requerimiento(%s,%s,%s)', (req,'prueba','prueba_rollback'))
    comprobar("SELECT estado=99 AND baja_fecha IS NOT NULL AND baja_usr='prueba_rollback' AND modi_usr='prueba_rollback' FROM compras.requerimiento WHERE id_requerimiento=%s", (req,))
    # Una precondicion critica falla y el savepoint revierte el intento completo.
    cur.execute('SAVEPOINT precondicion')
    cur.execute('UPDATE compras.requerimiento SET cargo_tercerizadora=101 WHERE id_requerimiento=%s',(req,))
    try:
        cur.execute(sql)
        raise AssertionError('La fase 1 acepto un cargo fuera de rango')
    except psycopg2.Error as error:
        assert 'Cargos fuera de rango' in error.diag.message_primary
        checks += 1
    cur.execute('ROLLBACK TO SAVEPOINT precondicion')
    comprobar('SELECT count(*)=6 FROM compras.estado_requerimiento')
    # Prueba negativa del control POST: solo altera un fixture dentro del savepoint.
    # La version productiva del archivo nunca contiene este UPDATE inyectado.
    cur.execute('SAVEPOINT conservacion_negativa')
    prueba = sql.replace('DO $catalogos$',
        "UPDATE compras.requerimiento SET observaciones='control_post' WHERE id_requerimiento="+str(req)+";\nDO $catalogos$",1)
    try:
        cur.execute(prueba)
        raise AssertionError('El control POST no detecto la modificacion del fixture')
    except psycopg2.Error as error:
        assert 'Cambio de contenido, contadores' in error.diag.message_primary
        checks += 1
    cur.execute('ROLLBACK TO SAVEPOINT conservacion_negativa')
    print('OK: %d controles SQL; se ejecuta ROLLBACK a continuacion.' % checks)
finally:
    con.rollback()
    if 'contenido_antes' in globals():
        assert resumen()==contenido_antes, 'Rollback no restauro contenido'
        assert secuencias()==secuencias_antes, 'Rollback: secuencias divergentes'
        assert permisos()==permisos_antes, 'Rollback: contratos/permisos divergentes'
        assert externos()==externos_antes, 'Rollback: objetos externos divergentes'
        cur.execute("SELECT count(*) FROM pg_class WHERE relnamespace='compras'::regnamespace AND relkind='r'")
        assert cur.fetchone()[0]==tablas_antes
        print('POST ROLLBACK: contenido, secuencias, permisos, objetos externos y %d tablas originales verificados.'%tablas_antes)
    con.rollback()
    cur.close()
    con.close()
    print('ROLLBACK efectuado; no se publicaron cambios en DB.')
