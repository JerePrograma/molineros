import java.lang.reflect.*;
import java.sql.Connection;
import java.util.*;
import ar.com.ospim.compras.requerimientos.beans.*;
import ar.com.ospim.compras.requerimientos.helper.ConfiguracionComprasHelper;

/** Reglas reales; solo ServiceImpl recibe datos DB simulados. */
public class ComprasConfiguracionTest {
    private interface Scenario { void run() throws Exception; }
    private static int passed;
    private static int failed;
    private static void test(String name,Scenario scenario) {
        try {scenario.run();passed++;System.out.println("PASS "+name);}
        catch(Throwable error) {failed++;System.out.println("FAIL "+name+": "+error);}
    }
    private static void check(boolean value,String message) {if(!value) throw new AssertionError(message);}
    private static void reject(Scenario action) throws Exception {
        try {action.run();} catch(IllegalStateException expected) {return;}
        throw new AssertionError("Configuracion invalida fue aceptada.");
    }
    private static ComprasFuenteDatosFixture source() throws Exception {
        ComprasFuenteDatosFixture fixture=new ComprasFuenteDatosFixture();fixture.cargarConfiguracionPrueba();fixture.instalar();return fixture;
    }
    public static void main(String[] args) throws Exception {
        test("relectura DB por operacion sin cache",new Scenario(){public void run() throws Exception{
            ComprasFuenteDatosFixture db=source();check(ConfiguracionComprasHelper.getMaxPresupuestosPorCarga()==7,"limite inicial");
            db.configuracion.put("COMPRAS_LIMITES","PRESUPUESTOS_POR_CARGA=19;PRESTADORES_ENVIADOS=23;DL_EXTENSION=11;DL_TITULO=97");
            check(ConfiguracionComprasHelper.getMaxPresupuestosPorCarga()==19,"valor cacheado");check(db.lecturas("COMPRAS_LIMITES")==2,"lectura no ocurre en cada llamada");
        }});
        test("clave ausente nula vacia y espacios detienen operacion",new Scenario(){public void run() throws Exception{
            final ComprasFuenteDatosFixture db=source();
            for(String value:new String[]{null,"","   "}) {db.configuracion.put("OBLIGATORIA",value);reject(new Scenario(){public void run(){ConfiguracionComprasHelper.leerObligatoria("OBLIGATORIA");}});}
            reject(new Scenario(){public void run(){ConfiguracionComprasHelper.leerObligatoria("NO_EXISTE");}});
        }});
        test("fallo DB informa no disponible y conserva causa",new Scenario(){public void run() throws Exception{
            ComprasFuenteDatosFixture db=source();db.fallarLectura=true;
            try {ConfiguracionComprasHelper.getRolAbmCompras();throw new AssertionError("fallo DB aceptado");}
            catch(IllegalStateException expected) {check(expected.getMessage().indexOf("COMPRAS_ROLES")>=0,"sin clave");check(expected.getCause()!=null,"causa perdida");}
        }});
        test("mapas usan claves exactas y conservan orden",new Scenario(){public void run() throws Exception{
            source();Map<String,String> values=ConfiguracionComprasHelper.parsearPares("MAPA","1=uno;10=diez;100=cien");
            check(values.size()==3&&"uno".equals(values.get("1"))&&"diez".equals(values.get("10")),"claves por subcadena");
            check(new ArrayList<String>(values.keySet()).equals(Arrays.asList(new String[]{"1","10","100"})),"orden perdido");
        }});
        test("mapas rechazan claves duplicadas y formato incompleto",new Scenario(){public void run() throws Exception{
            source();for(final String value:new String[]{"a=1;a=2"," a =1;a=2","a==1","a=","=1","a=1;","a=1;;b=2","sinseparador"}) {
                reject(new Scenario(){public void run(){ConfiguracionComprasHelper.parsearPares("MAPA",value);}});
            }
        }});
        test("enteros validan formato rango y cero por contrato",new Scenario(){public void run() throws Exception{
            source();for(final String value:new String[]{"+1","-1","1.5","1;2","2147483648","0"}) {
                reject(new Scenario(){public void run(){ConfiguracionComprasHelper.parsearEntero("NUMERO",value,false);}});
            }
            check(ConfiguracionComprasHelper.parsearEntero("NUMERO","0",true)==0,"cero permitido rechazado");
        }});
        test("listas enteras exactas y validacion completa tras coincidencia",new Scenario(){public void run() throws Exception{
            source();check(!ConfiguracionComprasHelper.contieneEntero("LISTA","11;21",1),"pertenencia por subcadena");
            check(ConfiguracionComprasHelper.contieneEntero("LISTA","11;1",1),"token exacto ausente");
            reject(new Scenario(){public void run(){ConfiguracionComprasHelper.contieneEntero("LISTA","1;invalido",1);}});
        }});
        test("sectores resuelven entradas con acentos y listas exactas",new Scenario(){public void run() throws Exception{
            source();check("MEDICA_PRUEBA".equals(ConfiguracionComprasHelper.getSectorReclamoPrestacional("Prestaciones M\u00e9dicas")),"normalizacion entrada");
            check(ConfiguracionComprasHelper.esSectorDetalleObservacion("Legales"),"sector configurado ausente");
            check(!ConfiguracionComprasHelper.esSectorDetalleObservacion("Legales Extra"),"sector por subcadena");
            check(ConfiguracionComprasHelper.compararBusquedaSinAcentos("Farmacia"),"busqueda configurada");
        }});
        test("sectores mal normalizados en DB son rechazados",new Scenario(){public void run() throws Exception{
            ComprasFuenteDatosFixture db=source();db.configuracion.put("COMPRAS_SECTORES_RP","farmacia=FARMACIA");
            reject(new Scenario(){public void run(){ConfiguracionComprasHelper.getSectoresReclamoPrestacional();}});
        }});
        test("filtros sectoriales preservan cero y ausencia opcional",new Scenario(){public void run() throws Exception{
            source();check(ConfiguracionComprasHelper.getFiltroTipoNomenclador("FARMACIA").intValue()==6,"filtro configurado");
            check(ConfiguracionComprasHelper.getFiltroTipoNomenclador("LEGALES").intValue()==0,"cero perdido");
            check(ConfiguracionComprasHelper.getFiltroTipoNomenclador("SECTOR NO CONFIGURADO")==null,"ausencia opcional cambio");
        }});
        test("filtro malformado ajeno no se ignora tras coincidencia",new Scenario(){public void run() throws Exception{
            ComprasFuenteDatosFixture db=source();db.configuracion.put("COMPRAS_FILTROS_NOMENCLADOR","FARMACIA=6;LEGALES=no_numero");
            reject(new Scenario(){public void run(){ConfiguracionComprasHelper.getFiltroTipoNomenclador("FARMACIA");}});
        }});
        test("roles vienen de DB y se reflejan sin reinicio",new Scenario(){public void run() throws Exception{
            ComprasFuenteDatosFixture db=source();check("rol_view_prueba".equals(ConfiguracionComprasHelper.getRolViewCompras()),"rol view");
            check("rol_abm_prueba".equals(ConfiguracionComprasHelper.getRolAbmCompras()),"rol abm");
            check("rol_anular_prueba".equals(ConfiguracionComprasHelper.getRolAnularCompras()),"rol anular");
            check("rol_cotizar_prueba".equals(ConfiguracionComprasHelper.getRolCotizarCompras()),"rol cotizar");
            check("rol_autorizar_prueba".equals(ConfiguracionComprasHelper.getRolAutorizarCompras()),"rol autorizar");
            db.configuracion.put("COMPRAS_ROLES","ABM=otro_rol");check("otro_rol".equals(ConfiguracionComprasHelper.getRolAbmCompras()),"rol cacheado");
        }});
        test("rol requerido ausente o duplicado falla cerrado",new Scenario(){public void run() throws Exception{
            ComprasFuenteDatosFixture db=source();db.configuracion.put("COMPRAS_ROLES","VIEW=lectura");reject(new Scenario(){public void run(){ConfiguracionComprasHelper.getRolAbmCompras();}});
            db.configuracion.put("COMPRAS_ROLES","ABM=a;ABM=b");reject(new Scenario(){public void run(){ConfiguracionComprasHelper.getRolAbmCompras();}});
        }});
        test("IDs de estado y descripciones son configurables",new Scenario(){public void run() throws Exception{
            source();check(ConfiguracionComprasHelper.getEstadoPendiente()==41&&ConfiguracionComprasHelper.getEstadoCotizado()==43,"IDs fijos");
            check(ConfiguracionComprasHelper.getEstadoACotizar()==42&&ConfiguracionComprasHelper.getEstadoReclamoRP()==44,"IDs intermedios");
            check(ConfiguracionComprasHelper.getEstadoOrdenCompra()==45&&ConfiguracionComprasHelper.getEstadoParaAutorizar()==46&&ConfiguracionComprasHelper.getEstadoAutorizado()==47&&ConfiguracionComprasHelper.getEstadoAnulado()==48,"IDs finales");
            check(ConfiguracionComprasHelper.listarEstados().size()==8&&ConfiguracionComprasHelper.esEstadoValido(43)&&!ConfiguracionComprasHelper.esEstadoValido(3),"catalogo fijo");
            check("COTIZADO PRUEBA".equals(ConfiguracionComprasHelper.getEstadoDescripcion(43)),"descripcion fija");
            ComprasFuenteDatosFixture db=source();db.configuracion.put("COMPRAS_ESTADOS_DESCRIPCIONES","41=Pendiente;041=Alias duplicado");
            reject(new Scenario(){public void run(){ConfiguracionComprasHelper.getEstadoDescripcion(41);}});
            db.configuracion.put("COMPRAS_ESTADOS_DESCRIPCIONES","41=Pendiente;malformado=Otro");
            reject(new Scenario(){public void run(){ConfiguracionComprasHelper.getEstadoDescripcion(41);}});
        }});
        test("estado requerido ausente o duplicado falla",new Scenario(){public void run() throws Exception{
            ComprasFuenteDatosFixture db=source();db.configuracion.put("COMPRAS_ESTADOS_IDS","COTIZADO=43");reject(new Scenario(){public void run(){ConfiguracionComprasHelper.getEstadoPendiente();}});
            db.configuracion.put("COMPRAS_ESTADOS_IDS","PENDIENTE=41;PENDIENTE=42");reject(new Scenario(){public void run(){ConfiguracionComprasHelper.getEstadoPendiente();}});
            db.configuracion.put("COMPRAS_ESTADOS_IDS","PENDIENTE=41;COTIZADO=041");reject(new Scenario(){public void run(){ConfiguracionComprasHelper.getEstadoPendiente();}});
        }});
        test("transiciones respetan configuracion y excluyen misma o desconocida",new Scenario(){public void run() throws Exception{
            source();check(ConfiguracionComprasHelper.validarTransicionEstado(41,42),"transicion permitida rechazada");
            check(!ConfiguracionComprasHelper.validarTransicionEstado(41,41),"auto transicion");
            check(!ConfiguracionComprasHelper.validarTransicionEstado(41,44),"salto no configurado");
            check(!ConfiguracionComprasHelper.validarTransicionEstado(999,42),"estado desconocido aceptado");
        }});
        test("transicion malformada no permite coincidencia parcial",new Scenario(){public void run() throws Exception{
            ComprasFuenteDatosFixture db=source();db.configuracion.put("COMPRAS_TRANSICIONES_ESTADO","41=42;43=no_numero");
            reject(new Scenario(){public void run(){ConfiguracionComprasHelper.validarTransicionEstado(41,42);}});
        }});
        test("permisos por estado usan tokens exactos y cambian en caliente",new Scenario(){public void run() throws Exception{
            ComprasFuenteDatosFixture db=source();db.configuracion.put("COMPRAS_ESTADOS_CORREGIR_ITEM","43;141");
            check(ConfiguracionComprasHelper.puedeCorregirItem(43)&&!ConfiguracionComprasHelper.puedeCorregirItem(41),"lista por subcadena");
            db.configuracion.put("COMPRAS_ESTADOS_CORREGIR_ITEM","41");check(ConfiguracionComprasHelper.puedeCorregirItem(41),"estado cacheado");
        }});
        test("concepto automatico sectorial cero explicito y relectura",new Scenario(){public void run() throws Exception{
            ComprasFuenteDatosFixture db=source();check(ConfiguracionComprasHelper.getConceptoAutomatico(2).intValue()==1001,"concepto fijo");
            check(ConfiguracionComprasHelper.getConceptoAutomatico(9)==null,"cero no deshabilita");
            db.configuracion.put("COMPRAS_CONCEPTOS_AUTOMATICOS","2=2002;9=0");check(ConfiguracionComprasHelper.getConceptoAutomatico(2).intValue()==2002,"concepto cacheado");
            reject(new Scenario(){public void run(){ConfiguracionComprasHelper.getConceptoAutomatico(999);}});
        }});
        test("rubro automatico y cantidad validan ausencia y formato",new Scenario(){public void run() throws Exception{
            ComprasFuenteDatosFixture db=source();check(ConfiguracionComprasHelper.getRubroConceptoAutomatico(2).intValue()==8&&ConfiguracionComprasHelper.getRubroConceptoAutomatico(9)==null,"rubro configurable");
            check(ConfiguracionComprasHelper.getCantidadConceptoAutomatico()==2,"cantidad fija");db.configuracion.put("COMPRAS_CANTIDAD_CONCEPTO_AUTOMATICO","0");
            reject(new Scenario(){public void run(){ConfiguracionComprasHelper.getCantidadConceptoAutomatico();}});
        }});
        test("defaults RP limites y Document Library salen de DB",new Scenario(){public void run() throws Exception{
            source();check(ConfiguracionComprasHelper.getRecuperableSur()==11&&ConfiguracionComprasHelper.getNoRecuperable()==12&&ConfiguracionComprasHelper.getRecuperableIntegracion()==13,"recuperables fijos");
            check(ConfiguracionComprasHelper.getEstadoPrestacionInicialRP()==0&&ConfiguracionComprasHelper.getTipoPrestacionInicialRP()==1,"defaults numericos");
            check(ConfiguracionComprasHelper.getMaxObservacionRP()==753&&"FRECUENCIA_PRUEBA".equals(ConfiguracionComprasHelper.getFrecuenciaInicialRP()),"default RP fijo");
            check("OTR".equals(ConfiguracionComprasHelper.getComprobanteInicialRP())&&"Descripcion prueba".equals(ConfiguracionComprasHelper.getDescripcionNoRecuperableRP()),"default texto");
            check(ConfiguracionComprasHelper.getMaxPrestadoresEnviados()==23&&ConfiguracionComprasHelper.getMaxExtensionDocumento()==11&&ConfiguracionComprasHelper.getMaxTituloDocumento()==97&&ConfiguracionComprasHelper.getGrupoDocumentLibraryLegacy()==12345L,"limites fijos");
        }});
        test("vigencia soporta long valido y rechaza formato cero y overflow",new Scenario(){public void run() throws Exception{
            ComprasFuenteDatosFixture db=source();db.configuracion.put("COMPRAS_RP_VIGENCIA_CONTEXTO_MILLIS","3000000000");check(ConfiguracionComprasHelper.getVigenciaContextoMillis()==3000000000L,"vigencia truncada");
            for(final String value:new String[]{"0","-1","1.5","9223372036854775808"}) {db.configuracion.put("COMPRAS_RP_VIGENCIA_CONTEXTO_MILLIS",value);reject(new Scenario(){public void run(){ConfiguracionComprasHelper.getVigenciaContextoMillis();}});}
            db.configuracion.put("COMPRAS_DL_GRUPO_LEGACY","3000000000");check(ConfiguracionComprasHelper.getGrupoDocumentLibraryLegacy()==3000000000L,"grupo DL truncado a int");
            for(final String value:new String[]{"0","-1","9223372036854775808"}) {db.configuracion.put("COMPRAS_DL_GRUPO_LEGACY",value);reject(new Scenario(){public void run(){ConfiguracionComprasHelper.getGrupoDocumentLibraryLegacy();}});}
        }});
        test("sucursal inicial conserva ceros y rechaza texto",new Scenario(){public void run() throws Exception{
            ComprasFuenteDatosFixture db=source();check("0007".equals(ConfiguracionComprasHelper.getSucursalComprobanteInicialRP()),"ceros perdidos");
            db.configuracion.put("COMPRAS_RP_SUCURSAL_INICIAL","7A");reject(new Scenario(){public void run(){ConfiguracionComprasHelper.getSucursalComprobanteInicialRP();}});
        }});
        test("lectura transaccional conserva conexion y rechaza null",new Scenario(){public void run() throws Exception{
            ComprasFuenteDatosFixture db=source();Connection con=(Connection)Proxy.newProxyInstance(Connection.class.getClassLoader(),new Class[]{Connection.class},new InvocationHandler(){public Object invoke(Object p,Method m,Object[] a){if(m.getReturnType()==Boolean.TYPE)return Boolean.FALSE;return null;}});
            check(ConfiguracionComprasHelper.getConceptoAutomatico(2,con).intValue()==1001&&db.ultimaConexion==con,"conexion sustituida");
            try {ConfiguracionComprasHelper.leerObligatoria("COMPRAS_LIMITES",null);throw new AssertionError("conexion null aceptada");}catch(IllegalArgumentException expected) { }
        }});
        test("DTO default mantiene pendiente configurado y una lectura requerida",new Scenario(){public void run() throws Exception{
            ComprasFuenteDatosFixture db=source();RequerimientoCompra primero=new RequerimientoCompra();
            check(primero.getEstado()==41,"constructor perdio pendiente");check(db.lecturas("COMPRAS_ESTADOS_IDS")==1,"lecturas extra constructor");
            primero.getEstado();primero.getId();primero.setIdSector(Integer.valueOf(2));check(db.lecturas("COMPRAS_ESTADOS_IDS")==1,"lecturas extra getters simples");
            RequerimientoCompra mapeado=RequerimientoCompra.paraMapeo();mapeado.setEstado(43);
            check(mapeado.getEstado()==43&&db.lecturas("COMPRAS_ESTADOS_IDS")==1,"factory mapeo consulta DB o pierde estado persistido");
            RequerimientoCompra segundo=new RequerimientoCompra(Integer.valueOf(10));check(segundo.getEstado()==41&&db.lecturas("COMPRAS_ESTADOS_IDS")==2,"constructor ID consulta duplicada");
            db.configuracion.put("COMPRAS_ESTADOS_IDS","PENDIENTE=91");check(new RequerimientoCompra().getEstado()==91,"constructor cacheado");
        }});
        test("DTO sin configuracion requerida no cae a estado cero",new Scenario(){public void run() throws Exception{
            ComprasFuenteDatosFixture db=source();db.configuracion.remove("COMPRAS_ESTADOS_IDS");reject(new Scenario(){public void run(){new RequerimientoCompra();}});
        }});
        test("descripcion visible usa mapa y fallback textual existente",new Scenario(){public void run() throws Exception{
            source();check("Prestaciones de prueba".equals(ConfiguracionComprasHelper.getSectorDescripcionVisible("Prestaciones Medicas")),"mapa visible");
            check("OTRO SECTOR".equals(ConfiguracionComprasHelper.getSectorDescripcionVisible("Otro Sector")),"fallback textual alterado");
        }});
        test("cantidad inicial de detalle relee 1 a 3 y rechaza ausencia o invalido",new Scenario(){public void run() throws Exception{
            final ComprasFuenteDatosFixture db=source();db.configuracion.put("COMPRAS_CANTIDAD_DETALLE_INICIAL","1");check(ConfiguracionComprasHelper.getCantidadDetalleInicial()==1,"default inicial distinto");
            db.configuracion.put("COMPRAS_CANTIDAD_DETALLE_INICIAL","3");check(ConfiguracionComprasHelper.getCantidadDetalleInicial()==3&&db.lecturas("COMPRAS_CANTIDAD_DETALLE_INICIAL")==2,"cantidad cacheada o lectura extra");
            for(String value:new String[]{null,"","0","-1","uno","2147483648"}){db.configuracion.put("COMPRAS_CANTIDAD_DETALLE_INICIAL",value);reject(new Scenario(){public void run(){ConfiguracionComprasHelper.getCantidadDetalleInicial();}});}
            check(new RequerimientoCompraDetalle().getCantidad().intValue()==1,"constructor DTO cambio contrato o consulta DB");
        }});
        System.out.println("RESULT passed="+passed+" failed="+failed);
        if(failed>0) System.exit(1);
    }
}