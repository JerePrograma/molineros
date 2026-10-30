import ar.com.ospim.autorizaciones.beans.Nomenclador;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraSector;
import ar.com.ospim.compras.requerimientos.beans.TipoPrestacionCompra;
import ar.com.ospim.compras.requerimientos.beans.SectorNomencladorCompra;
import ar.com.ospim.compras.requerimientos.helper.NomencladorCompraBusquedaHelper;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class ComprasAsociacionesConfiguracionTest {
    private static int pruebas;
    public static void main(String[] args) throws Exception {
        List<RequerimientoCompraSector> sectores = new ArrayList<RequerimientoCompraSector>();
        sectores.add(new RequerimientoCompraSector(Integer.valueOf(2)));
        List<TipoPrestacionCompra> tipos = new ArrayList<TipoPrestacionCompra>();
        TipoPrestacionCompra diabetes = new TipoPrestacionCompra();
        diabetes.setId(Integer.valueOf(8)); diabetes.setIdSector(Integer.valueOf(2));
        tipos.add(diabetes);
        TipoPrestacionCompra insumos = new TipoPrestacionCompra();
        insumos.setId(Integer.valueOf(6)); insumos.setIdSector(Integer.valueOf(2));
        tipos.add(insumos);
        List<SectorNomencladorCompra> filas = new ArrayList<SectorNomencladorCompra>();
        filas.add(fila(2, null, Integer.valueOf(3), null, null));
        filas.add(fila(2, Integer.valueOf(8), Integer.valueOf(15), null, null));
        filas.add(fila(2, Integer.valueOf(6), Integer.valueOf(10), null, null));
        List<SectorNomencladorCompra> efectiva = NomencladorCompraBusquedaHelper.resolverConfiguracion(
                2, Integer.valueOf(8), sectores, tipos, filas);
        comprobar(efectiva.size() == 1 && efectiva.get(0).getIdTipoNomenclador().intValue() == 15,
                "La especifica reemplaza la general, tipo15 es dato");
        comprobar(NomencladorCompraBusquedaHelper.esNomencladorConfigurado(efectiva, nom(500,15,3,"ABC")),
                "Tipo15 configurado se acepta sin whitelist");
        comprobar(!NomencladorCompraBusquedaHelper.esNomencladorConfigurado(efectiva, nom(11452,10,3,"9100045")),
                "Nueva seleccion tipo10 no salta politica especifica");
        efectiva = NomencladorCompraBusquedaHelper.resolverConfiguracion(
                2, Integer.valueOf(6), sectores, tipos, filas);
        comprobar(NomencladorCompraBusquedaHelper.esNomencladorConfigurado(efectiva,nom(11452,10,3,"9100045")),
                "Insumos conserva10 por configuracion propia");
        efectiva = NomencladorCompraBusquedaHelper.resolverConfiguracion(2,null,sectores,tipos,filas);
        comprobar(efectiva.size()==1 && efectiva.get(0).getIdTipoNomenclador().intValue()==3,
                "Rubro nulo resuelve solo generales");
        List<SectorNomencladorCompra> criterios = new ArrayList<SectorNomencladorCompra>();
        criterios.add(fila(2,null,Integer.valueOf(3),Integer.valueOf(6),"431003"));
        criterios.add(fila(2,null,Integer.valueOf(10),null,"9100045"));
        comprobar(!NomencladorCompraBusquedaHelper.esNomencladorConfigurado(criterios,nom(100,3,3,"431003")),
                "AND en fila requiere marca y tipo y codigo");
        comprobar(NomencladorCompraBusquedaHelper.esNomencladorConfigurado(criterios,nom(11452,10,3,"9100045")),
                "OR entre filas acepta segundo criterio");
        comprobar(!NomencladorCompraBusquedaHelper.esNomencladorConfigurado(criterios,nom(11452,10,3,"910004")),
                "Codigo se compara exacto sin subcadenas");
        filas.add(fila(2,null,Integer.valueOf(0),null,null));
        efectiva=NomencladorCompraBusquedaHelper.resolverConfiguracion(2,Integer.valueOf(8),sectores,tipos,filas);
        comprobar(efectiva.size()==1,"General invalida no afecta especifica efectiva");
        debeRechazar(2,null,sectores,tipos,filas,"General efectiva invalida rechazada");
        filas.remove(filas.size()-1);
        filas.set(1,fila(2,Integer.valueOf(8),Integer.valueOf(14),null,null));
        efectiva=NomencladorCompraBusquedaHelper.resolverConfiguracion(2,Integer.valueOf(8),sectores,tipos,filas);
        comprobar(efectiva.get(0).getIdTipoNomenclador().intValue()==14,"Cambio de datos se relee sin cache");
        debeRechazar(2,Integer.valueOf(99),sectores,tipos,filas,"Rubro ajeno o inexistente rechazado");
        debeRechazar(99,null,sectores,tipos,filas,"Sector no disponible rechazado");
        debeRechazar(2,null,sectores,tipos,null,"Fallo de lectura rechazado");
        debeRechazar(2,null,sectores,tipos,new ArrayList<SectorNomencladorCompra>(),"Config vacia rechazada");
        filas.add(fila(2,Integer.valueOf(8),null,null," "));
        debeRechazar(2,Integer.valueOf(8),sectores,tipos,filas,"Codigo malformado rechaza toda operacion");
        List<SectorNomencladorCompra> todas=new ArrayList<SectorNomencladorCompra>();
        todas.add(fila(2,null,Integer.valueOf(10),null,null));
        todas.add(fila(2,null,null,null,null));
        boolean rechazo=false;
        try { NomencladorCompraBusquedaHelper.esNomencladorConfigurado(todas,nom(11452,10,3,"9100045")); }
        catch(Exception e) { rechazo=true; }
        comprobar(rechazo,"Fila posterior invalida impide exito anticipado");
        List<SectorNomencladorCompra> catalogoInexistente = new ArrayList<SectorNomencladorCompra>();
        SectorNomencladorCompra sinCatalogo = fila(2, null, Integer.valueOf(15), null, null);
        java.lang.reflect.Field idCatalogo = SectorNomencladorCompra.class.getDeclaredField("idTipoNomencladorCatalogo");
        idCatalogo.setAccessible(true); idCatalogo.set(sinCatalogo,null); catalogoInexistente.add(sinCatalogo);
        debeRechazar(2,null,sectores,tipos,catalogoInexistente,"Tipo positivo ausente del catalogo se rechaza");
        probarHistoricosYJdbc();
        List<Map<String,Object>> catalogo=new ArrayList<Map<String,Object>>();
        String[] nombres={"PRESTACIONES MEDICAS","LEGALES","OTROS","RRHH","SISTEMAS"};
        for(int i=0;i<nombres.length;i++){
            Map<String,Object> f=new HashMap<String,Object>();f.put("id",Integer.valueOf(i+1));f.put("descripcion",nombres[i]);
            f.put("activo",Boolean.TRUE);f.put("baja",null);f.put("requiere_afiliado",Boolean.FALSE);catalogo.add(f);
        }
        List<RequerimientoCompraSector> visibles=NomencladorCompraBusquedaHelper.filtrarSectoresSeleccionables(catalogo,"LEGALES;OTROS");
        comprobar(visibles.size()==3&&"RRHH".equals(visibles.get(1).getDescripcion()),"Selector de sector aplica exclusiones DB y conserva orden del catalogo");
        comprobar(NomencladorCompraBusquedaHelper.filtrarSectoresSeleccionables(catalogo,"LEGALES").size()==4,
                "Selector relee cambio de exclusiones sin cache ni whitelist fija");
        catalogo.get(0).put("activo",Boolean.FALSE);catalogo.get(3).put("baja",new java.util.Date(1));
        comprobar(NomencladorCompraBusquedaHelper.filtrarSectoresSeleccionables(catalogo,"LEGALES;OTROS").size()==1,
                "Selector excluye sectores inactivos y dados de baja en Java");
        boolean rechazoSelector=false;try{NomencladorCompraBusquedaHelper.filtrarSectoresSeleccionables(catalogo,"LEGALES;;OTROS");}catch(Exception esperada){rechazoSelector=true;}
        comprobar(rechazoSelector,"Exclusiones malformadas bloquean selector sin habilitar todo");
        rechazoSelector=false;try{NomencladorCompraBusquedaHelper.filtrarSectoresSeleccionables(catalogo,"LEGALLES;OTROS");}catch(ar.com.ospim.compras.requerimientos.helper.ConfiguracionComprasHelper.ConfiguracionInvalidaException esperada){rechazoSelector=true;}
        comprobar(rechazoSelector,"Sector excluido inexistente se rechaza tipado antes de habilitar todo el catalogo");
        System.out.println("APROBADAS="+pruebas+"; alcance=Java aislado sin DB, UI ni persistencia historica");
    }

    private static void probarHistoricosYJdbc() throws Exception {
        ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraDetalle origen =
                new ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraDetalle();
        origen.setTipoItem("NOMENCLADOR"); origen.setIdTipoPrestacion(Integer.valueOf(8));
        origen.setId(Integer.valueOf(445)); origen.setIdPrestacion(Integer.valueOf(10415));
        origen.setIdTipoNomenclador(Integer.valueOf(3));
        origen.setCodigoNomenclador("101002");
        origen.setDescripcionNomenclador("GASTOS ADMINIST. PRESTACIONES MEDICAS");
        ar.com.ospim.compras.requerimientos.helper.ReclamoPrestacionalCompraPrecargaHelper
                .validarReferenciaPersistida(origen,nom(10415,3,3,"101002"));
        comprobar(true,"Historico445 conserva10415 tipo3 sin matriz nuevas selecciones");
        origen.setId(Integer.valueOf(446)); origen.setIdPrestacion(Integer.valueOf(11452));
        origen.setIdTipoNomenclador(Integer.valueOf(10));
        origen.setCodigoNomenclador("9100045"); origen.setDescripcionNomenclador("INSUMOS VARIOS");
        ar.com.ospim.compras.requerimientos.helper.ReclamoPrestacionalCompraPrecargaHelper
                .validarReferenciaPersistida(origen,nom(11452,10,3,"9100045"));
        comprobar(true,"Historico446 conserva11452 tipo10 aunque nuevas seleccionestipo15");
        origen.setIdPrestacion(Integer.valueOf(2573));origen.setIdTipoNomenclador(Integer.valueOf(3));
        ar.com.ospim.compras.requerimientos.helper.ReclamoPrestacionalCompraPrecargaHelper
                .validarReferenciaPersistida(origen,nom(2573,3,3,"9100045"));
        comprobar(true,"Correccionexternaautorizada446 tipo3 valida integridad actual");
        boolean rechazado=false;
        try { ar.com.ospim.compras.requerimientos.helper.ReclamoPrestacionalCompraPrecargaHelper
                .validarReferenciaPersistida(origen,nom(11452,10,3,"9100045")); }
        catch(Exception e){rechazado=true;}
        comprobar(rechazado,"Codigo compartido no permite sustituir identidad canonica");
        Nomenclador inactivo=nom(2573,3,3,"9100045");inactivo.setBaja_fecha(new java.sql.Date(1));
        rechazado=false;
        try {ar.com.ospim.compras.requerimientos.helper.ReclamoPrestacionalCompraPrecargaHelper
                .validarReferenciaPersistida(origen,inactivo);}catch(Exception e){rechazado=true;}
        comprobar(rechazado,"Historico no permite canonico inactivo");
        ar.com.ospim.compras.requerimientos.beans.ReclamoPrestacionalCompraContexto contexto=
                new ar.com.ospim.compras.requerimientos.beans.ReclamoPrestacionalCompraContexto(
                        1388,"20111111112",Integer.valueOf(0),"prueba",1,"nonce");
        Map<Integer,Integer> identidades=new HashMap<Integer,Integer>();
        identidades.put(Integer.valueOf(1),Integer.valueOf(445));
        identidades.put(Integer.valueOf(2),Integer.valueOf(446));
        contexto.definirDetallesOrigen(identidades);identidades.put(Integer.valueOf(1),Integer.valueOf(999));
        comprobar(contexto.getIdDetalleOrigen(1).intValue()==445,"Fila temporal1 conserva detalle445 con copia defensiva");
        comprobar(contexto.getIdDetalleOrigen(445)==null,"idRegistro no se trata como idDetalle");
        final List<String> valores=new ArrayList<String>();valores.add("uno");
        final boolean[] cierres=new boolean[3];final boolean[] fallo=new boolean[1];
        java.sql.Connection con=(java.sql.Connection)Proxy.newProxyInstance(
                ComprasAsociacionesConfiguracionTest.class.getClassLoader(),new Class[]{java.sql.Connection.class},
                new InvocationHandler(){public Object invoke(Object proxy,Method method,Object[] args)throws Throwable{
            if("close".equals(method.getName())){cierres[0]=true;return null;}
            if("prepareStatement".equals(method.getName())){
                if(!"SELECT valor FROM public.system_config WHERE id = ?".equals(args[0]))throw new Exception("SQLinesperado");
                return Proxy.newProxyInstance(ComprasAsociacionesConfiguracionTest.class.getClassLoader(),
                    new Class[]{java.sql.PreparedStatement.class},new InvocationHandler(){
                    public Object invoke(Object proxy,Method method,Object[] args)throws Throwable{
                        if("setString".equals(method.getName()))return null;
                        if("close".equals(method.getName())){cierres[1]=true;return null;}
                        if("executeQuery".equals(method.getName())){
                            if(fallo[0])throw new java.sql.SQLException("Lecturafallida simulada");
                            return Proxy.newProxyInstance(ComprasAsociacionesConfiguracionTest.class.getClassLoader(),
                                new Class[]{ResultSet.class},new InvocationHandler(){private int fila=-1;
                                public Object invoke(Object proxy,Method method,Object[] args)throws Throwable{
                                    if("next".equals(method.getName()))return Boolean.valueOf(++fila<valores.size());
                                    if("getString".equals(method.getName()))return valores.get(fila);
                                    if("close".equals(method.getName())){cierres[2]=true;return null;}
                                    throw new UnsupportedOperationException(method.getName());
                                }});
                        }
                        throw new UnsupportedOperationException(method.getName());
                    }});
            }
            throw new UnsupportedOperationException(method.getName());
        }});
        ar.com.ospim.compras.requerimientos.service.BusquedaRequerimientoCompraServiceImpl servicio=
                new ar.com.ospim.compras.requerimientos.service.BusquedaRequerimientoCompraServiceImpl();
        comprobar("uno".equals(servicio.getConfiguracionCompra("COMPRAS_PRUEBA",con)),"LectorJDBC exacto devuelve valorcrudo");
        comprobar(!cierres[0] && cierres[1] && cierres[2],"Lector cierra recursos y conserva conexionTXcaller");
        valores.set(0,"dos");
        comprobar("dos".equals(servicio.getConfiguracionCompra("COMPRAS_PRUEBA",con)),"CambioDBsimulado se refleja sin cache");
        valores.clear();comprobar(servicio.getConfiguracionCompra("COMPRAS_PRUEBA",con)==null,"Claveausente conserva null");
        valores.add(null);comprobar(servicio.getConfiguracionCompra("COMPRAS_PRUEBA",con)==null,"ValorSQLnull conserva null");
        valores.add("duplicado");rechazado=false;
        try{servicio.getConfiguracionCompra("COMPRAS_PRUEBA",con);}catch(java.sql.SQLException e){rechazado=true;}
        comprobar(rechazado,"Clave duplicada se rechaza sin inventarvalor");
        fallo[0]=true;rechazado=false;
        try{servicio.getConfiguracionCompra("COMPRAS_PRUEBA",con);}catch(java.sql.SQLException e){rechazado=true;}
        comprobar(rechazado && !cierres[0],"FalloJDBC propaga y no cierra conexion delcaller");
    }

    private static void debeRechazar(int sector,Integer tipo,List<RequerimientoCompraSector> sectores,
            List<TipoPrestacionCompra> tipos,List<SectorNomencladorCompra> filas,String nombre) throws Exception {
        boolean rechazo=false;
        try { NomencladorCompraBusquedaHelper.resolverConfiguracion(sector,tipo,sectores,tipos,filas); }
        catch(Exception e) { rechazo=true; }
        comprobar(rechazo,nombre);
    }
    private static void comprobar(boolean condicion,String nombre) throws Exception {
        if(!condicion) throw new Exception("FALLO: "+nombre);
        pruebas++; System.out.println("OK "+nombre);
    }
    private static Nomenclador nom(int id,int tipo,int marca,String codigo) {
        Nomenclador n=new Nomenclador();n.setId_prestacion(id);n.setId_tipo_nomenclador(tipo);
        n.setMarcaReintegroLiquidacion(marca);n.setCodigo(codigo);return n;
    }
    private static SectorNomencladorCompra fila(int sector,Integer rubro,Integer tipo,Integer marca,String codigo)
            throws Exception {
        final Map<String,Object> valores=new HashMap<String,Object>();
        valores.put("id_sector",Integer.valueOf(sector));valores.put("id_tipo_prestacion",rubro);
        valores.put("id_tipo_nomenclador",tipo);valores.put("tipo_nomenclador_catalogo",tipo);valores.put("marca_rein_liq",marca);
        valores.put("codigo_nomenclador",codigo);
        ResultSet rs=(ResultSet)Proxy.newProxyInstance(ComprasAsociacionesConfiguracionTest.class.getClassLoader(),
                new Class[]{ResultSet.class},new InvocationHandler(){
            private boolean nulo;
            public Object invoke(Object proxy,Method method,Object[] args) throws Throwable {
                if("wasNull".equals(method.getName())) return Boolean.valueOf(nulo);
                Object valor=valores.get((String)args[0]);nulo=valor==null;
                if("getInt".equals(method.getName())) return valor!=null ? valor : Integer.valueOf(0);
                if("getString".equals(method.getName())) return valor;
                throw new UnsupportedOperationException(method.getName());
            }
        });
        return SectorNomencladorCompra.getMapping(rs);
    }
}