import java.lang.reflect.*;
import java.sql.Connection;
import com.mchange.v2.c3p0.PooledDataSource;
import ar.com.ospim.util.ConnectionHelper;
import ar.com.ospim.compras.requerimientos.service.BusquedaRequerimientoCompraServiceImpl;
import ar.com.ospim.compras.requerimientos.service.BusquedaRequerimientoCompraServiceUtil;
import java.util.*;
import ar.com.ospim.compras.requerimientos.beans.*;
import ar.com.ospim.compras.requerimientos.helper.ConfiguracionComprasHelper;
import ar.com.ospim.util.PermissionUtil;
import com.liferay.portal.model.Role;
import com.liferay.portal.model.User;
import com.liferay.portal.model.impl.RoleImpl;
import com.liferay.portal.model.impl.UserImpl;

/** Reglas reales; JDBC bloqueado para constantes y datos simulados para configuracion operativa. */
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
    /* Referencia: ComprasOrdenSuministroTest.Accion; roles del usuario aislados. */
    private static User usuarioConRoles(String[] nombres) {
        final List<Role> roles=nombres==null?null:new ArrayList<Role>();
        for(int i=0;nombres!=null&&i<nombres.length;i++) {
            if(nombres[i]==null) {roles.add(null);continue;}
            Role rol=new RoleImpl();rol.setName(nombres[i]);roles.add(rol);
        }
        return new UserImpl(){public List<Role> getRoles(){return roles;}};
    }
    private static int accesosJdbc;
    private static BusquedaRequerimientoCompraServiceImpl fuenteReal() throws Exception {
        BusquedaRequerimientoCompraServiceImpl service = new BusquedaRequerimientoCompraServiceImpl();
        Field field = BusquedaRequerimientoCompraServiceUtil.class.getDeclaredField("instance");
        field.setAccessible(true);
        field.set(null, service);
        return service;
    }
    private static Connection conexionProhibida() {
        return (Connection) Proxy.newProxyInstance(Connection.class.getClassLoader(), new Class[]{Connection.class},
                new InvocationHandler(){public Object invoke(Object proxy, Method method, Object[] args){
                    accesosJdbc++;
                    throw new AssertionError("La constante intento usar JDBC: " + method.getName());
                }});
    }
    public static void main(String[] args) throws Exception {
        // Referencia: ComprasOrdenSuministroJdbcTest; se sustituye solo la frontera JDBC.
        Field datasource = ConnectionHelper.class.getDeclaredField("datasource");
        datasource.setAccessible(true);
        Object anterior = datasource.get(null);
        PooledDataSource bloqueada = (PooledDataSource) Proxy.newProxyInstance(PooledDataSource.class.getClassLoader(),
                new Class[]{PooledDataSource.class}, new InvocationHandler(){
                    public Object invoke(Object proxy, Method method, Object[] values){
                        accesosJdbc++;
                        throw new AssertionError("La constante intento abrir DB: " + method.getName());
                    }
                });
        datasource.set(null, bloqueada);
        try {
        test("clave ausente nula vacia y espacios detienen operacion",new Scenario(){public void run() throws Exception{
            final ComprasFuenteDatosFixture db=source();
            for(String value:new String[]{null,"","   "}) {db.configuracion.put("OBLIGATORIA",value);reject(new Scenario(){public void run(){ConfiguracionComprasHelper.leerObligatoria("OBLIGATORIA");}});}
            reject(new Scenario(){public void run(){ConfiguracionComprasHelper.leerObligatoria("NO_EXISTE");}});
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
        test("configuracion operativa relee DB por operacion sin cache",new Scenario(){public void run() throws Exception{
            ComprasFuenteDatosFixture db=source();check(ConfiguracionComprasHelper.getConceptoAutomatico(2).intValue()==1001,"concepto inicial");
            db.configuracion.put("COMPRAS_CONCEPTOS_AUTOMATICOS","2=2002;9=0");
            check(ConfiguracionComprasHelper.getConceptoAutomatico(2).intValue()==2002,"valor cacheado");check(db.lecturas("COMPRAS_CONCEPTOS_AUTOMATICOS")==2,"lectura no ocurre en cada llamada");
        }});
        test("fallo DB operativo informa no disponible y conserva causa",new Scenario(){public void run() throws Exception{
            ComprasFuenteDatosFixture db=source();db.fallarLectura=true;
            try {ConfiguracionComprasHelper.getConceptoAutomatico(2);throw new AssertionError("fallo DB aceptado");}
            catch(IllegalStateException expected) {check(expected.getMessage().indexOf("COMPRAS_CONCEPTOS_AUTOMATICOS")>=0,"sin clave");check(expected.getCause()!=null,"causa perdida");}
        }});
        test("sectores y filtros conservan reglas legacy sin DB",new Scenario(){public void run() throws Exception{
            fuenteReal();check("PRESTACIONES MEDICAS".equals(ConfiguracionComprasHelper.getSectorReclamoPrestacional("Prestaciones M\u00e9dicas")),"normalizacion entrada");
            check(ConfiguracionComprasHelper.esSectorDetalleObservacion("Legales"),"sector legacy ausente");
            check(!ConfiguracionComprasHelper.esSectorDetalleObservacion("Legales Extra"),"sector por subcadena");
            check(ConfiguracionComprasHelper.compararBusquedaSinAcentos("Prestaciones Medicas")&&!ConfiguracionComprasHelper.compararBusquedaSinAcentos("Farmacia"),"busqueda sectorial");
            check(ConfiguracionComprasHelper.getFiltroTipoNomenclador("FARMACIA").intValue()==9&&ConfiguracionComprasHelper.getTipoNomencladorFarmacia()==9,"filtro farmacia");
            check(ConfiguracionComprasHelper.getFiltroTipoNomenclador("PRESTACIONES MEDICAS").intValue()==0,"cero perdido");
            check(ConfiguracionComprasHelper.getFiltroTipoNomenclador("DISCAPACIDAD").intValue()==8&&ConfiguracionComprasHelper.getFiltroTipoNomenclador("ODONTOLOGIA").intValue()==1,"filtros sectoriales");
            check(ConfiguracionComprasHelper.getFiltroTipoNomenclador("SECTOR NO CONFIGURADO")==null,"ausencia opcional cambio");
        }});
        test("parser sectorial rechaza tokens mal normalizados",new Scenario(){public void run() throws Exception{
            reject(new Scenario(){public void run(){ConfiguracionComprasHelper.resolverSectorDetalleObservacion("Farmacia","farmacia");}});
        }});
        test("roles estructurales conservan nombres legacy sin DB",new Scenario(){public void run() throws Exception{
            fuenteReal();check("VIEW_COMPRAS".equals(ConfiguracionComprasHelper.getRolViewCompras()),"rol view");
            check("ABM_COMPRAS".equals(ConfiguracionComprasHelper.getRolAbmCompras()),"rol abm");
            check("ANULAR_COMPRAS".equals(ConfiguracionComprasHelper.getRolAnularCompras()),"rol anular");
            check("COTIZAR_COMPRAS".equals(ConfiguracionComprasHelper.getRolCotizarCompras()),"rol cotizar");
            check("AUTORIZAR_COMPRAS".equals(ConfiguracionComprasHelper.getRolAutorizarCompras()),"rol autorizar");
            check("GESTION_COMPRAS".equals(ConfiguracionComprasHelper.getRolGestionCompras()),"fallback gestion");
        }});
        test("roles de Compras toleran alias y mayusculas sin aceptar otros nombres",new Scenario(){public void run() throws Exception{
            fuenteReal();
            String[] codigos={"VIEW","ABM","ANULAR","COTIZAR","AUTORIZAR","GESTION"};
            String[][] variantes={
                {"VIEW_COMPRAS","view_compras","ViEw_CoMpRaS","COMPRAS_VIEW","compras_view","CoMpRaS_vIeW"},
                {"ABM_COMPRAS","abm_compras","AbM_cOmPrAs","COMPRAS_ABM","compras_abm","CoMpRaS_aBm"},
                {"ANULAR_COMPRAS","anular_compras","AnUlAr_CoMpRaS","COMPRAS_ANULAR","compras_anular","CoMpRaS_aNuLaR"},
                {"COTIZAR_COMPRAS","cotizar_compras","CoTiZaR_cOmPrAs","COMPRAS_COTIZAR","compras_cotizar","CoMpRaS_cOtIzAr"},
                {"AUTORIZAR_COMPRAS","autorizar_compras","AuToRiZaR_cOmPrAs","COMPRAS_AUTORIZAR","compras_autorizar","CoMpRaS_aUtOrIzAr"},
                {"GESTION_COMPRAS","gestion_compras","GeStIoN_cOmPrAs","COMPRAS_GESTION","compras_gestion","CoMpRaS_gEsTiOn"}
            };
            Locale original=Locale.getDefault();int aceptados=0;int rechazados=0;
            try {
                Locale[] idiomas={Locale.ENGLISH,new Locale("tr","TR")};
                for(int idioma=0;idioma<idiomas.length;idioma++) {
                    Locale.setDefault(idiomas[idioma]);
                    for(int configurado=0;configurado<variantes[0].length;configurado++) {
                        String[] esperados={ConfiguracionComprasHelper.getRolViewCompras(),ConfiguracionComprasHelper.getRolAbmCompras(),ConfiguracionComprasHelper.getRolAnularCompras(),ConfiguracionComprasHelper.getRolCotizarCompras(),ConfiguracionComprasHelper.getRolAutorizarCompras(),ConfiguracionComprasHelper.getRolGestionCompras()};
                        for(int rol=0;rol<codigos.length;rol++) {
                            String caso=codigos[rol]+" locale="+idiomas[idioma]+" configurado="+configurado;
                            check(variantes[rol][0].equals(esperados[rol]),caso+" getter no conserva nombre legacy");
                            check(PermissionUtil.rolesEquivalentes(variantes[rol][configurado],esperados[rol]),caso+" alias rechazado");
                            for(int asignado=0;asignado<variantes[rol].length;asignado++) {
                                check(PermissionUtil.userContainsRole(usuarioConRoles(new String[]{variantes[rol][asignado]}),variantes[rol][configurado]),caso+" asignado="+asignado);aceptados++;
                            }
                            for(int otro=0;otro<codigos.length;otro++) {
                                if(otro==rol)continue;
                                check(!PermissionUtil.userContainsRole(usuarioConRoles(new String[]{variantes[otro][0]}),variantes[rol][configurado]),caso+" acepto otro rol "+codigos[otro]);rechazados++;
                            }
                            String[] ajenos={variantes[rol][0]+"_OTRO","OTRO_"+variantes[rol][0],"COMPRAS_"+codigos[rol]+"_OTRO"};
                            for(int otro=0;otro<ajenos.length;otro++) {check(!PermissionUtil.userContainsRole(usuarioConRoles(new String[]{ajenos[otro]}),variantes[rol][configurado]),caso+" acepto nombre distinto "+ajenos[otro]);rechazados++;}
                            check(!PermissionUtil.userContainsRole(usuarioConRoles(new String[0]),esperados[rol]),caso+" acepto usuario sin rol");rechazados++;
                        }
                    }
                }
            } finally {Locale.setDefault(original);}
            System.out.println("COMPRAS_ROLES_ALIAS_MATRIX_OK roles=6 variantes=6 locales=2 aceptados="+aceptados+" rechazados="+rechazados);
        }});
        test("alias de Compras no se extienden a otras familias ni roles desconocidos",new Scenario(){public void run(){
            Locale original=Locale.getDefault();
            try {
                Locale[] idiomas={Locale.ENGLISH,new Locale("tr","TR")};
                for(int idioma=0;idioma<idiomas.length;idioma++) {
                    Locale.setDefault(idiomas[idioma]);
                    String[][] distintos={{"VIEW_LIQUIDACIONES","LIQUIDACIONES_VIEW"},{"ABM_AUTORIZACIONES","AUTORIZACIONES_ABM"},{"OTRO_COMPRAS","COMPRAS_OTRO"},{"SUPER_COMPRAS","COMPRAS_SUPER"},{"VIEW_COMPRAS"," COMPRAS_VIEW"},{"VIEW_COMPRAS","COMPRAS_VIEW "}};
                    for(int i=0;i<distintos.length;i++) {
                        check(!PermissionUtil.userContainsRole(usuarioConRoles(new String[]{distintos[i][0]}),distintos[i][1]),"acepto alias ajeno "+distintos[i][1]);
                        if(i<4) {check(!PermissionUtil.userContainsRole(usuarioConRoles(new String[]{distintos[i][1]}),distintos[i][0]),"acepto alias ajeno inverso "+distintos[i][0]);}
                        // RoleImpl.getName() recorta espacios; probar el comparador DL con el nombre sin modelo.
                        else {check(!PermissionUtil.rolesEquivalentes(distintos[i][1],distintos[i][0]),"acepto espacios en alias "+distintos[i][1]);}
                    }
                    check(PermissionUtil.userContainsRole(usuarioConRoles(new String[]{"Rol_Compras_Personalizado"}),"ROL_COMPRAS_PERSONALIZADO"),"rol personalizado exacto cambio");
                    check(PermissionUtil.userContainsRole(usuarioConRoles(new String[]{"ABM_Liquidaciones"}),"abm_liquidaciones"),"comparacion legacy de otra familia cambio");
                }
            } finally {Locale.setDefault(original);}
        }});
        test("comparacion de roles rechaza entradas nulas y vacias",new Scenario(){public void run(){
            User user=usuarioConRoles(new String[]{"VIEW_COMPRAS"});
            check(!PermissionUtil.userContainsRole(null,"VIEW_COMPRAS"),"usuario null aceptado");
            check(!PermissionUtil.userContainsRole(user,null),"rol null aceptado");
            check(!PermissionUtil.userContainsRole(user,""),"rol vacio aceptado");
            check(!PermissionUtil.userContainsRole(usuarioConRoles(null),"VIEW_COMPRAS"),"lista null aceptada");
            check(!PermissionUtil.userContainsRole(usuarioConRoles(new String[]{null}),"VIEW_COMPRAS"),"rol null en lista aceptado");
        }});
        test("parsers de roles y estados rechazan ausencia y duplicados",new Scenario(){public void run() throws Exception{
            reject(new Scenario(){public void run(){ConfiguracionComprasHelper.parsearPares("COMPRAS_ROLES","ABM=a;ABM=b");}});
            reject(new Scenario(){public void run(){ConfiguracionComprasHelper.parsearPares("COMPRAS_ROLES","ABM=");}});
            for(final String value:new String[]{"COTIZADO=43","PENDIENTE=41;PENDIENTE=42","PENDIENTE=41;COTIZADO=041"}) {
                reject(new Scenario(){public void run(){ConfiguracionComprasHelper.resolverEstado("PENDIENTE",value);}});
            }
        }});
        test("IDs descripciones y permisos conservan contrato persistido sin DB",new Scenario(){public void run() throws Exception{
            fuenteReal();check(ConfiguracionComprasHelper.getEstadoPendiente()==1&&ConfiguracionComprasHelper.getEstadoCotizado()==3,"IDs iniciales");
            check(ConfiguracionComprasHelper.getEstadoACotizar()==2&&ConfiguracionComprasHelper.getEstadoReclamoRP()==4,"IDs intermedios");
            check(ConfiguracionComprasHelper.getEstadoOrdenCompra()==5&&ConfiguracionComprasHelper.getEstadoParaAutorizar()==6&&ConfiguracionComprasHelper.getEstadoAutorizado()==7&&ConfiguracionComprasHelper.getEstadoAnulado()==99,"IDs finales");
            check(ConfiguracionComprasHelper.listarEstados().size()==8&&ConfiguracionComprasHelper.esEstadoValido(3)&&!ConfiguracionComprasHelper.esEstadoValido(43),"catalogo legacy");
            check("COTIZADO".equals(ConfiguracionComprasHelper.getEstadoDescripcion(3))&&"".equals(ConfiguracionComprasHelper.getEstadoDescripcion(43)),"descripciones");
            check(ConfiguracionComprasHelper.puedeCorregirItem(1)&&ConfiguracionComprasHelper.puedeCorregirItem(3),"correccion rechazada");
            check(!ConfiguracionComprasHelper.puedeCorregirItem(43)&&!ConfiguracionComprasHelper.puedeCorregirItem(141),"pertenencia por subcadena");
            check(ConfiguracionComprasHelper.puedeAdministrarPresupuestos(2)&&!ConfiguracionComprasHelper.puedeAdministrarPresupuestos(3),"administracion ampliada");
        }});
        test("transiciones legacy excluyen misma desconocida y saltos sin DB",new Scenario(){public void run() throws Exception{
            fuenteReal();check(ConfiguracionComprasHelper.validarTransicionEstado(1,2)&&ConfiguracionComprasHelper.validarTransicionEstado(1,99),"transicion permitida rechazada");
            check(!ConfiguracionComprasHelper.validarTransicionEstado(1,1)&&!ConfiguracionComprasHelper.validarTransicionEstado(1,4),"salto no permitido");
            check(!ConfiguracionComprasHelper.validarTransicionEstado(999,2),"estado desconocido aceptado");
            reject(new Scenario(){public void run(){ConfiguracionComprasHelper.resolverTransicionEstado(41,42,"41=42;43=no_numero","PENDIENTE=41;A_COTIZAR=42;COTIZADO=43");}});
        }});
        test("concepto automatico sectorial cero explicito y relectura",new Scenario(){public void run() throws Exception{
            ComprasFuenteDatosFixture db=source();check(ConfiguracionComprasHelper.getConceptoAutomatico(2).intValue()==1001,"concepto fijo");
            check(ConfiguracionComprasHelper.getConceptoAutomatico(9)==null,"cero no deshabilita");
            db.configuracion.put("COMPRAS_CONCEPTOS_AUTOMATICOS","2=2002;9=0");check(ConfiguracionComprasHelper.getConceptoAutomatico(2).intValue()==2002,"concepto cacheado");
            reject(new Scenario(){public void run(){ConfiguracionComprasHelper.getConceptoAutomatico(999);}});
        }});
        test("rubro operativo valida relacion y cantidad legacy permanece fija",new Scenario(){public void run() throws Exception{
            ComprasFuenteDatosFixture db=source();check(ConfiguracionComprasHelper.getRubroConceptoAutomatico(2).intValue()==8&&ConfiguracionComprasHelper.getRubroConceptoAutomatico(9)==null,"rubro configurable");
            db.configuracion.put("COMPRAS_RUBROS_CONCEPTO_AUTOMATICO","2=no_numero");
            reject(new Scenario(){public void run(){ConfiguracionComprasHelper.getRubroConceptoAutomatico(2);}});
            fuenteReal();check(ConfiguracionComprasHelper.getCantidadConceptoAutomatico()==1,"cantidad legacy");
            reject(new Scenario(){public void run(){ConfiguracionComprasHelper.resolverCantidadConceptoAutomatico("0");}});
        }});
        test("defaults RP y limites conservan valores legacy sin DB",new Scenario(){public void run() throws Exception{
            fuenteReal();check(ConfiguracionComprasHelper.getRecuperableSur()==1&&ConfiguracionComprasHelper.getNoRecuperable()==2&&ConfiguracionComprasHelper.getRecuperableIntegracion()==3,"recuperables legacy");
            check(ConfiguracionComprasHelper.getEstadoPrestacionInicialRP()==0&&ConfiguracionComprasHelper.getTipoPrestacionInicialRP()==1,"defaults numericos");
            check(ConfiguracionComprasHelper.getMaxObservacionRP()==200&&"UNICA".equals(ConfiguracionComprasHelper.getFrecuenciaInicialRP()),"default RP");
            check("OTR".equals(ConfiguracionComprasHelper.getComprobanteInicialRP())&&"NO ES RECUPERABLE".equals(ConfiguracionComprasHelper.getDescripcionNoRecuperableRP()),"default texto");
            check("000".equals(ConfiguracionComprasHelper.getSucursalComprobanteInicialRP())&&ConfiguracionComprasHelper.getVigenciaContextoMillis()==7200000L,"sucursal y vigencia");
            check(ConfiguracionComprasHelper.getMaxPrestadoresEnviados()==500&&ConfiguracionComprasHelper.getMaxExtensionDocumento()==20&&ConfiguracionComprasHelper.getMaxTituloDocumento()==240,"limites");
            check(ConfiguracionComprasHelper.getMaxPresupuestosPorCarga()==10&&ConfiguracionComprasHelper.getRequerimientosPorPagina()==50,"limites carga");
        }});
        test("grupo DL operativo soporta long y rechaza formato cero y overflow",new Scenario(){public void run() throws Exception{
            ComprasFuenteDatosFixture db=source();db.configuracion.put("COMPRAS_DL_GRUPO_LEGACY","3000000000");check(ConfiguracionComprasHelper.getGrupoDocumentLibraryLegacy()==3000000000L,"grupo DL truncado a int");
            for(final String value:new String[]{"0","-1","1.5","9223372036854775808"}) {db.configuracion.put("COMPRAS_DL_GRUPO_LEGACY",value);reject(new Scenario(){public void run(){ConfiguracionComprasHelper.getGrupoDocumentLibraryLegacy();}});}
        }});
        test("lectura transaccional conserva conexion y rechaza null",new Scenario(){public void run() throws Exception{
            ComprasFuenteDatosFixture db=source();Connection con=(Connection)Proxy.newProxyInstance(Connection.class.getClassLoader(),new Class[]{Connection.class},new InvocationHandler(){public Object invoke(Object p,Method m,Object[] a){if(m.getReturnType()==Boolean.TYPE)return Boolean.FALSE;return null;}});
            check(ConfiguracionComprasHelper.getConceptoAutomatico(2,con).intValue()==1001&&db.ultimaConexion==con,"conexion sustituida");
            try {ConfiguracionComprasHelper.leerObligatoria("COMPRAS_LIMITES",null);throw new AssertionError("conexion null aceptada");}catch(IllegalArgumentException expected) { }
        }});
        test("DTO pendiente cantidad inicial y mapeo conservan contrato sin DB",new Scenario(){public void run() throws Exception{
            fuenteReal();RequerimientoCompra primero=new RequerimientoCompra();
            check(primero.getEstado()==1,"constructor perdio pendiente");
            primero.getEstado();primero.getId();primero.setIdSector(Integer.valueOf(2));
            RequerimientoCompra mapeado=RequerimientoCompra.paraMapeo();mapeado.setEstado(43);
            check(mapeado.getEstado()==43,"factory mapeo pierde estado persistido");
            check(new RequerimientoCompra(Integer.valueOf(10)).getEstado()==1,"constructor ID perdio pendiente");
            check(ConfiguracionComprasHelper.getCantidadDetalleInicial()==1&&new RequerimientoCompraDetalle().getCantidad().intValue()==1,"cantidad inicial");
        }});
        test("descripcion visible legacy conserva acentos y fallback textual sin DB",new Scenario(){public void run() throws Exception{
            fuenteReal();check("PRESTACIONES M\u00c9DICAS".equals(ConfiguracionComprasHelper.getSectorDescripcionVisible("Prestaciones Medicas")),"descripcion visible");
            check("ODONTOLOG\u00cdA".equals(ConfiguracionComprasHelper.getSectorDescripcionVisible("Odontologia")),"acento odontologia");
            check("OTRO SECTOR".equals(ConfiguracionComprasHelper.getSectorDescripcionVisible("Otro Sector")),"fallback textual alterado");
        }});
        test("56 variables retiradas conservan contrato mediante ambas APIs sin tocar JDBC",new Scenario(){public void run() throws Exception{
            BusquedaRequerimientoCompraServiceImpl service=fuenteReal();
            Connection con=conexionProhibida();
            // Valores de la migracion original: verifican preservacion del comportamiento instalado.
            String[][] esperados={
                {"COMPRAS_CANTIDAD_CONCEPTO_AUTOMATICO","1"},
                {"COMPRAS_CANTIDAD_DETALLE_INICIAL","1"},
                {"COMPRAS_CONTACTO_PRIORIDADES_EMAIL","E=1"},
                {"COMPRAS_CONTACTO_TIPOS_EMAIL","E"},
                {"COMPRAS_ENVIO_ESTADOS_FINALES","ENVIADO;ERROR;EMAIL_INVALIDO"},
                {"COMPRAS_ENVIO_ESTADOS_REINTENTO","PENDIENTE;ERROR;EMAIL_INVALIDO"},
                {"COMPRAS_ESTADOS_ADMINISTRAR_PRESUPUESTOS","2"},
                {"COMPRAS_ESTADOS_ANULAR","1;2;3"},
                {"COMPRAS_ESTADOS_CONSULTAR_RP","3;4;6;7;5"},
                {"COMPRAS_ESTADOS_CORREGIR_ITEM","1;2;3"},
                {"COMPRAS_ESTADOS_DESCRIPCIONES","1=PENDIENTE;2=ENVIADO A COTIZAR;3=COTIZADO;4=RECLAMO (RP);6=PARA AUTORIZAR;7=AUTORIZADO;5=ORDEN DE COMPRA;99=ANULADO"},
                {"COMPRAS_ESTADOS_EDITAR_COTIZACION","2"},
                {"COMPRAS_ESTADOS_EDITAR_ESTRUCTURA","1"},
                {"COMPRAS_ESTADOS_EDITAR_OBSERVACION_INTERNA","1;2;3;4;5;6;7;99"},
                {"COMPRAS_ESTADOS_EDITAR_SURGE","1;2"},
                {"COMPRAS_ESTADOS_ELIMINAR_DETALLE","1;2"},
                {"COMPRAS_ESTADOS_ENVIAR_AUTORIZAR","4"},
                {"COMPRAS_ESTADOS_ENVIAR_COTIZAR","1"},
                {"COMPRAS_ESTADOS_ENVIO","PENDIENTE=PENDIENTE;PROCESANDO=PROCESANDO;ENVIADO=ENVIADO;COTIZADO=COTIZADO;ERROR=ERROR;EMAIL_INVALIDO=EMAIL_INVALIDO"},
                {"COMPRAS_ESTADOS_IDS","PENDIENTE=1;A_COTIZAR=2;COTIZADO=3;RECLAMO_RP=4;ORDEN_COMPRA=5;PARA_AUTORIZAR=6;AUTORIZADO=7;ANULADO=99"},
                {"COMPRAS_ESTADOS_NOTIFICAR_PRESTADORES","1;2"},
                {"COMPRAS_ESTADOS_NOTIFICAR_TODOS","1"},
                {"COMPRAS_ESTADOS_PRESTADORES_ENVIADOS","2;3;4;5;6;7;99"},
                {"COMPRAS_ESTADOS_REINTENTAR_NOTIFICACIONES","2"},
                {"COMPRAS_ESTADOS_SOLO_LECTURA","3;4;6;7;5;99"},
                {"COMPRAS_ESTADOS_VER_COTIZACION","2;3;4;6;7;5;99"},
                {"COMPRAS_ESTADOS_VER_PRESUPUESTOS","2;3;4;6;7;5;99"},
                {"COMPRAS_FILTROS_NOMENCLADOR","FARMACIA=9;DISCAPACIDAD=8;ODONTOLOGIA=1;PRESTACIONES MEDICAS=0"},
                {"COMPRAS_LIMITES","PRESUPUESTOS_POR_CARGA=10;PRESTADORES_ENVIADOS=500;DL_EXTENSION=20;DL_TITULO=240;ITEMS_HISTORICOS_AFILIADO=20;ORDENES_MEDICAS_POR_CARGA=20;REQUERIMIENTOS_POR_PAGINA=50;RECETA_ORDEN_MEDICA=100"},
                {"COMPRAS_MAX_ERROR_NOTIFICACION","4000"},
                {"COMPRAS_OC_MAX_IMPORTE","99999999999999.99"},
                {"COMPRAS_ROLES","VIEW=VIEW_COMPRAS;ABM=ABM_COMPRAS;ANULAR=ANULAR_COMPRAS;COTIZAR=COTIZAR_COMPRAS;AUTORIZAR=AUTORIZAR_COMPRAS"},
                {"COMPRAS_RP_COMPROBANTE_INICIAL","OTR"},
                {"COMPRAS_RP_DESCRIPCION_NO_RECUPERABLE","NO ES RECUPERABLE"},
                {"COMPRAS_RP_ERROR_DEFECTO","Error de vinculacion no especificado."},
                {"COMPRAS_RP_ESTADO_PRESTACION_INICIAL","0"},
                {"COMPRAS_RP_FRECUENCIA_INICIAL","UNICA"},
                {"COMPRAS_RP_MAX_ERROR","2000"},
                {"COMPRAS_RP_MAX_OBSERVACION","200"},
                {"COMPRAS_RP_NO_RECUPERABLE","2"},
                {"COMPRAS_RP_RECUPERABLE_INTEGRACION","3"},
                {"COMPRAS_RP_RECUPERABLE_SUR","1"},
                {"COMPRAS_RP_SUCURSAL_INICIAL","000"},
                {"COMPRAS_RP_TIPO_PRESTACION_INICIAL","1"},
                {"COMPRAS_RP_VIGENCIA_CONTEXTO_MILLIS","7200000"},
                {"COMPRAS_RUBROS_INSUMOS","6"},
                {"COMPRAS_RUBROS_PROTESIS","3;4;5"},
                {"COMPRAS_SECTORES_BUSQUEDA_SIN_ACENTOS","PRESTACIONES MEDICAS"},
                {"COMPRAS_SECTORES_DESCRIPCION_VISIBLE","PRESTACIONES MEDICAS=PRESTACIONES MÉDICAS;ODONTOLOGIA=ODONTOLOGÍA"},
                {"COMPRAS_SECTORES_DETALLE_OBSERVACION","RRHH;LEGALES;SISTEMAS;OTROS"},
                {"COMPRAS_SECTORES_NO_SELECCIONABLES","LEGALES;OTROS"},
                {"COMPRAS_SECTORES_RP","PRESTACIONES MEDICAS=PRESTACIONES MEDICAS;DISCAPACIDAD=DISCAPACIDAD;FARMACIA=FARMACIA;ODONTOLOGIA=ODONTOLOGIA;LEGALES=LEGALES"},
                {"COMPRAS_SECTORES_SIN_COTIZACION_PRESTADOR","RRHH;SISTEMAS"},
                {"COMPRAS_SECTOR_MEDICO","PRESTACIONES MEDICAS"},
                {"COMPRAS_TIPO_NOMENCLADOR_FARMACIA","9"},
                {"COMPRAS_TRANSICIONES_ESTADO","1=2,99;2=3,99;3=4,99;4=6;6=7"}
            };
            for(int i=0;i<esperados.length;i++) {
                String clave=esperados[i][0], valor=esperados[i][1];
                check(valor.equals(ConfiguracionComprasHelper.getValorFijo(clave)),clave+" constante distinta");
                check(valor.equals(service.getConfiguracionCompra(clave)),clave+" API sin conexion");
                check(valor.equals(service.getConfiguracionCompra(clave,con)),clave+" API transaccional");
                check(valor.equals(BusquedaRequerimientoCompraServiceUtil.getConfiguracionCompra(clave)),clave+" ServiceUtil sin conexion");
                check(valor.equals(BusquedaRequerimientoCompraServiceUtil.getConfiguracionCompra(clave,con)),clave+" ServiceUtil transaccional");
                check(valor.equals(ConfiguracionComprasHelper.leerObligatoria(clave)),clave+" Helper sin conexion");
                check(valor.equals(ConfiguracionComprasHelper.leerObligatoria(clave,con)),clave+" Helper transaccional");
            }
            check(accesosJdbc==0,"las constantes tocaron DB");
        }});
        test("11 configuraciones operativas y claves ajenas conservan lectura SQL",new Scenario(){public void run() throws Exception{
            String[] claves={"COMPRAS_COMPARATIVA_DEFAULTS","COMPRAS_COMPARATIVA_ENVIO","COMPRAS_COMPARATIVA_PAGO","COMPRAS_COMPARATIVA_PLAZO","COMPRAS_COMPARATIVA_VALIDEZ","COMPRAS_CONCEPTOS_AUTOMATICOS","COMPRAS_DL_GRUPO_LEGACY","COMPRAS_DOCUMENTOS_USUARIO_REACTIVACION","COMPRAS_EXCEPCIONES_PRESTADOR_REQUERIMIENTO","COMPRAS_RUBROS_CONCEPTO_AUTOMATICO","COMPRAS_USUARIO_DEFECTO_NOTIFICACION","COMPRAS_REFACTOR_CONFIG_VERSION","AJENA"};
            ComprasFuenteDatosFixture db=source();
            for(int i=0;i<claves.length;i++) {
                String clave=claves[i];
                check(ConfiguracionComprasHelper.getValorFijo(clave)==null,clave+" dejo de ser operativa");
                db.configuracion.put(clave,"primero");check("primero".equals(ConfiguracionComprasHelper.leerObligatoria(clave)),clave+" valor inicial");
                db.configuracion.put(clave,"segundo");check("segundo".equals(ConfiguracionComprasHelper.leerObligatoria(clave)),clave+" valor cacheado");
                check(db.lecturas(clave)==2,clave+" no relee DB");
            }
        }});
        check(accesosJdbc==0,"Hubo acceso JDBC durante las pruebas de constantes.");
        } finally {datasource.set(null, anterior);}
        System.out.println("RESULT passed="+passed+" failed="+failed);
        if(failed>0) System.exit(1);
    }
}
