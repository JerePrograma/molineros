import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ar.com.ospim.autorizaciones.beans.Nomenclador;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompra;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraPresupuesto;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraSector;
import ar.com.ospim.compras.requerimientos.beans.PrestadorCotizacion;
import ar.com.ospim.global.beans.Empresa;
import ar.com.ospim.compras.requerimientos.beans.SectorNomencladorCompra;
import ar.com.ospim.compras.requerimientos.service.BusquedaRequerimientoCompraServiceImpl;
import ar.com.ospim.compras.requerimientos.service.BusquedaRequerimientoCompraServiceUtil;

/** Solo frontera de lectura DB. Helpers, ServiceUtil y reglas siguen siendo reales. */
public class ComprasFuenteDatosFixture extends BusquedaRequerimientoCompraServiceImpl {
    public final Map<String,String> configuracion = new HashMap<String,String>();
    public final Map<String,Integer> lecturas = new HashMap<String,Integer>();
    public final Map<Integer,RequerimientoCompra> requerimientos = new HashMap<Integer,RequerimientoCompra>();
    public final Map<Integer,Nomenclador> canonicos = new HashMap<Integer,Nomenclador>();
    public final List<SectorNomencladorCompra> asociaciones = new ArrayList<SectorNomencladorCompra>();
    public final Map<Integer,RequerimientoCompraPresupuesto> documentos = new HashMap<Integer,RequerimientoCompraPresupuesto>();
    public final List<PrestadorCotizacion> prestadores = new ArrayList<PrestadorCotizacion>();
    public final List<Empresa> empresas = new ArrayList<Empresa>();
    public final List<Map<String,Object>> catalogoSectores = new ArrayList<Map<String,Object>>();
    public final List<RequerimientoCompraSector> sectores = new ArrayList<RequerimientoCompraSector>();
    public final List<Connection> conexiones = new ArrayList<Connection>();
    public boolean fallarLectura;
    public Connection ultimaConexion;
    public int lecturasRequerimiento;
    public int lecturasCanonico;
    public int lecturasAsociaciones;

    public void instalar() throws Exception {
        Field field = BusquedaRequerimientoCompraServiceUtil.class.getDeclaredField("instance");
        field.setAccessible(true);
        field.set(null,this);
    }

    public void cargarConfiguracionPrueba() {
        configuracion.put("COMPRAS_COMPARATIVA_PAGO","30;45;60");
        configuracion.put("COMPRAS_COMPARATIVA_PLAZO","24/48;48/72");
        configuracion.put("COMPRAS_COMPARATIVA_VALIDEZ","24;48");
        configuracion.put("COMPRAS_COMPARATIVA_ENVIO","Obra Social;Beneficiario;Delegación");
        configuracion.put("COMPRAS_COMPARATIVA_DEFAULTS","PAGO=45;PLAZO=48/72;VALIDEZ=48;ENVIO=Beneficiario");
        configuracion.put("COMPRAS_DOCUMENTOS_USUARIO_REACTIVACION","sistema");
        configuracion.put("COMPRAS_ESTADOS_IDS","PENDIENTE=41;A_COTIZAR=42;COTIZADO=43;RECLAMO_RP=44;ORDEN_COMPRA=45;PARA_AUTORIZAR=46;AUTORIZADO=47;ANULADO=48");
        configuracion.put("COMPRAS_ESTADOS_DESCRIPCIONES","41=PENDIENTE PRUEBA;42=A COTIZAR PRUEBA;43=COTIZADO PRUEBA;44=RP PRUEBA;45=OC PRUEBA;46=PARA AUTORIZAR PRUEBA;47=AUTORIZADO PRUEBA;48=ANULADO PRUEBA");
        configuracion.put("COMPRAS_TRANSICIONES_ESTADO","41=42,46;42=43;43=44;46=47;47=45");
        configuracion.put("COMPRAS_ESTADOS_ENVIO","PENDIENTE=PENDIENTE;PROCESANDO=PROCESANDO;ENVIADO=ENVIADO;COTIZADO=COTIZADO;ERROR=ERROR;EMAIL_INVALIDO=EMAIL_INVALIDO");
        configuracion.put("COMPRAS_ROLES","VIEW=rol_view_prueba;ABM=rol_abm_prueba;ANULAR=rol_anular_prueba;COTIZAR=rol_cotizar_prueba;AUTORIZAR=rol_autorizar_prueba");
        configuracion.put("COMPRAS_LIMITES","PRESUPUESTOS_POR_CARGA=7;PRESTADORES_ENVIADOS=23;DL_EXTENSION=11;DL_TITULO=97;RECETA_ORDEN_MEDICA=100");
        configuracion.put("COMPRAS_SECTORES_RP","PRESTACIONES MEDICAS=MEDICA_PRUEBA;FARMACIA=FARMACIA_PRUEBA");
        configuracion.put("COMPRAS_SECTORES_DETALLE_OBSERVACION","LEGALES;ODONTOLOGIA");
        configuracion.put("COMPRAS_SECTORES_SIN_COTIZACION_PRESTADOR","LEGALES");
        configuracion.put("COMPRAS_SECTORES_BUSQUEDA_SIN_ACENTOS","FARMACIA");
        configuracion.put("COMPRAS_FILTROS_NOMENCLADOR","FARMACIA=6;LEGALES=0");
        configuracion.put("COMPRAS_SECTORES_DESCRIPCION_VISIBLE","PRESTACIONES MEDICAS=Prestaciones de prueba");
        configuracion.put("COMPRAS_TIPO_NOMENCLADOR_FARMACIA","6");
        configuracion.put("COMPRAS_CONCEPTOS_AUTOMATICOS","2=1001;9=0");
        configuracion.put("COMPRAS_RUBROS_CONCEPTO_AUTOMATICO","2=8;9=0");
        configuracion.put("COMPRAS_CANTIDAD_CONCEPTO_AUTOMATICO","2");
        configuracion.put("COMPRAS_RP_RECUPERABLE_SUR","11");
        configuracion.put("COMPRAS_RP_NO_RECUPERABLE","12");
        configuracion.put("COMPRAS_RP_RECUPERABLE_INTEGRACION","13");
        configuracion.put("COMPRAS_RP_ESTADO_PRESTACION_INICIAL","0");
        configuracion.put("COMPRAS_RP_TIPO_PRESTACION_INICIAL","1");
        configuracion.put("COMPRAS_RP_MAX_OBSERVACION","753");
        configuracion.put("COMPRAS_RP_FRECUENCIA_INICIAL","FRECUENCIA_PRUEBA");
        configuracion.put("COMPRAS_RP_COMPROBANTE_INICIAL","OTR");
        configuracion.put("COMPRAS_RP_DESCRIPCION_NO_RECUPERABLE","Descripcion prueba");
        configuracion.put("COMPRAS_RP_VIGENCIA_CONTEXTO_MILLIS","2700000");
        configuracion.put("COMPRAS_DL_GRUPO_LEGACY","12345");
        configuracion.put("COMPRAS_RP_SUCURSAL_INICIAL","0007");
        for (String operacion : new String[]{"ENVIAR_AUTORIZAR","CONSULTAR_RP","CORREGIR_ITEM","EDITAR_ESTRUCTURA","ELIMINAR_DETALLE","EDITAR_SURGE","EDITAR_COTIZACION","ADMINISTRAR_PRESUPUESTOS","VER_COTIZACION","VER_PRESUPUESTOS","ENVIAR_COTIZAR","REINTENTAR_NOTIFICACIONES","ANULAR","SOLO_LECTURA"}) {
            configuracion.put("COMPRAS_ESTADOS_"+operacion,"41;43");
        }
    }

    public int lecturas(String clave) {
        Integer cantidad=lecturas.get(clave);
        return cantidad==null ? 0 : cantidad.intValue();
    }

    public String getConfiguracionCompra(String clave) throws Exception {
        lecturas.put(clave,Integer.valueOf(lecturas(clave)+1));
        if (fallarLectura) throw new SQLException("Fallo de lectura simulado por el test.");
        return configuracion.get(clave);
    }

    public String getConfiguracionCompra(String clave, Connection con) throws Exception {
        ultimaConexion=con;
        conexiones.add(con);
        return getConfiguracionCompra(clave);
    }

    public RequerimientoCompra getRequerimientoCompra(int id) throws Exception {
        lecturasRequerimiento++;
        return requerimientos.get(Integer.valueOf(id));
    }

    public Nomenclador obtenerNomencladorCanonico(int id) throws Exception {
        lecturasCanonico++;
        return canonicos.get(Integer.valueOf(id));
    }

    public List<SectorNomencladorCompra> getConfiguracionNomenclador(int sector,Integer rubro) throws Exception {
        lecturasAsociaciones++;
        return new ArrayList<SectorNomencladorCompra>(asociaciones);
    }

    public RequerimientoCompra getRequerimientoCompra(Connection con,int id) throws Exception {
        conexiones.add(con);lecturasRequerimiento++;
        return requerimientos.get(Integer.valueOf(id));
    }
    public RequerimientoCompra getCabeceraRequerimiento(Connection con,int id) throws Exception {
        return getRequerimientoCompra(con,id);
    }
    public RequerimientoCompraPresupuesto bloquearDocumento(Connection con,int idRequerimiento,int idDocumento) throws Exception {
        conexiones.add(con);RequerimientoCompraPresupuesto p=documentos.get(Integer.valueOf(idDocumento));
        return p!=null&&p.getIdRequerimiento().intValue()==idRequerimiento?p:null;
    }
    public List<RequerimientoCompraPresupuesto> listarDocumentos(Connection con,int idRequerimiento,int tipoDocumento) throws Exception {
        conexiones.add(con);List<RequerimientoCompraPresupuesto> result=new ArrayList<RequerimientoCompraPresupuesto>();
        for(RequerimientoCompraPresupuesto p:documentos.values()) {
            if(p.getIdRequerimiento().intValue()==idRequerimiento&&p.getTipoDocumento().intValue()==tipoDocumento&&p.isActivo())result.add(p);
        }
        return result;
    }
    public List<Empresa> buscarEmpresasCotizacion(Connection con,String cuit,String descripcion,String sucursal,int limite) throws Exception {
        conexiones.add(con);return new ArrayList<Empresa>(empresas);
    }
    public List<Map<String,Object>> listarCatalogoSectores(Connection con) throws Exception {
        conexiones.add(con);
        if(!catalogoSectores.isEmpty())return new ArrayList<Map<String,Object>>(catalogoSectores);
        List<Map<String,Object>> result=new ArrayList<Map<String,Object>>();
        for(RequerimientoCompraSector sector:sectores){Map<String,Object> row=new HashMap<String,Object>();row.put("id",sector.getId());row.put("descripcion",sector.getDescripcion());row.put("activo",Boolean.TRUE);row.put("baja",null);result.add(row);}
        return result;
    }
    public List<RequerimientoCompraSector> listarSectores(Connection con) throws Exception {
        conexiones.add(con);return new ArrayList<RequerimientoCompraSector>(sectores);
    }

    public List<PrestadorCotizacion> listarPrestadoresEnviados(Connection con,int req,int limite) throws Exception {
        conexiones.add(con);return new ArrayList<PrestadorCotizacion>(prestadores);
    }
    public List<PrestadorCotizacion> listarPrestadoresEnviados(int idRequerimiento,int limite) throws Exception {
        return new ArrayList<PrestadorCotizacion>(prestadores);
    }
    public List<RequerimientoCompraPresupuesto> listarCotizacionesEmpresa(int idRequerimiento) throws Exception {
        return listarDocumentos(null,idRequerimiento,RequerimientoCompraPresupuesto.TIPO_DOCUMENTO_COTIZACION_EMPRESA);
    }
}