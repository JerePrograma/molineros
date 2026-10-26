import ar.com.ospim.afiliados.beans.Afiliado;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompra;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraOrden;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraPresupuesto;
import ar.com.ospim.compras.requerimientos.service.RequerimientoCompraOrdenServiceImpl;
import ar.com.ospim.global.beans.Domicilio;
import ar.com.ospim.global.beans.ContactoElectronico;
import ar.com.empresas.beans.Contacto;
import ar.com.ospim.global.beans.Empresa;
import ar.com.ospim.global.beans.Localidad;
import ar.com.ospim.global.beans.Provincia;
import ar.com.ospim.global.beans.Telefono;
import ar.com.ospim.liquidaciones.beans.Prestador;
import java.util.ArrayList;
import java.util.List;

/* Prueba el enriquecimiento productivo con beans reales y las fronteras de
 * consulta legacy. No reemplaza mapeos ni agrega datos a las tablas de OC. */
public class ComprasOrdenConsultaTest {
    private static int checks;
    public static void main(String[] args) throws Exception {
        afiliado(); empresaOpcional(); fuentesAusentesYClaves(); telefonoFallback();
        System.out.println("COMPRAS_ORDEN_CONSULTA_OK checks=" + checks);
    }
    private static void afiliado() throws Exception {
        Consulta servicio = new Consulta();
        RequerimientoCompra r = requerimiento("Prestaciones Medicas");
        r.setRequiereAfiliado(true); r.setAfiliadoCuilTitular("20123456789"); r.setAfiliadoInt(Integer.valueOf(1));
        r.setAfiliadoNombreApellido("ALIAS NOMBRE ANTERIOR"); r.setAfiliadoDocumento("ALIAS DOCUMENTO ANTERIOR");
        servicio.afiliado.setId_ospim(4321); servicio.afiliado.setNombre("Ana"); servicio.afiliado.setApellido("Prueba");
        servicio.afiliado.setDocumento_tipo("DNI"); servicio.afiliado.setDocu_numero("12345678");
        servicio.afiliado.setId_parentesco(5); servicio.afiliado.setEmail("  ");
        servicio.titular.setEmail("titular@example.invalid");
        Domicilio domicilio = new Domicilio(); domicilio.setCalle("Calle de prueba"); domicilio.setNumero("123");
        domicilio.setPiso("2"); domicilio.setDepto("A"); domicilio.setLocalidad(new Localidad(1,"Moron"));
        domicilio.setProvincia(new Provincia(1,"Buenos Aires")); domicilio.setCod_area_celular("11"); domicilio.setCelular("55550000");
        Domicilio anterior = new Domicilio(); anterior.setCalle("DOMICILIO_DADO_BAJA"); anterior.setBaja_fecha(new java.util.Date());
        servicio.domicilios.add(null); servicio.domicilios.add(anterior); servicio.domicilios.add(domicilio);
        Telefono anteriorTel = new Telefono("54", "11", "TELEFONO_BAJA", ""); anteriorTel.setBajaFecha(new java.util.Date());
        servicio.telefonos.add(null); servicio.telefonos.add(anteriorTel);
        servicio.telefonos.add(new Telefono("54", "11", "44443333", "7"));
        servicio.telefonos.add(new Telefono("54", "11", "44443333", "7"));
        servicio.prestador.setContacto("Contacto padron"); servicio.prestador.setDomicilio(domicilio);
        List<ContactoElectronico> emails = new ArrayList<ContactoElectronico>();
        ContactoElectronico emailBaja = new ContactoElectronico(1, ContactoElectronico.Tipo.EMAIL, "baja@example.invalid", "");
        emailBaja.setBajaFecha(new java.util.Date()); emails.add(null); emails.add(emailBaja);
        emails.add(new ContactoElectronico(2, ContactoElectronico.Tipo.EMAIL, "prestador@example.invalid", ""));
        servicio.prestador.setContactosElectronicos(emails);
        servicio.prestador.setTelefonos(servicio.telefonos); servicio.origen.setIdPrestador(Integer.valueOf(30));
        RequerimientoCompraOrden orden = orden(r);
        servicio.completarDatosConsulta(orden, r);
        igual("nombre desde padron", "Ana", r.getAfiliadoNombre());
        igual("nombre visible invalida alias viejo", "Prueba, Ana", r.getAfiliadoNombreApellidoVisible());
        igual("documento visible invalida alias viejo", "DNI 12345678", r.getAfiliadoDocumentoVisible());
        igual("apellido desde padron", "Prueba", r.getAfiliadoApellido());
        igual("documento desde padron", "12345678", r.getAfiliadoDocumentoNro());
        igual("id ospim desde padron", Integer.valueOf(4321), r.getAfiliadoIdOspim());
        igual("plan desde componente existente", "Plan actual", orden.getPlan());
        igual("email fallback titular", "titular@example.invalid", r.getAfiliadoEmail());
        contiene("direccion completa", r.getAfiliadoDireccion(), "Calle de prueba 123 Piso 2 Dto. A");
        igual("localidad desde domicilio", "Moron", r.getAfiliadoLocalidad());
        igual("provincia desde domicilio", "Buenos Aires", r.getAfiliadoProvincia());
        igual("celular desde domicilio", "11 55550000", r.getAfiliadoCelular());
        igual("telefono sin duplicados", "54 11 44443333 Int. 7", r.getAfiliadoTelefono());
        igual("telefono dependiente usa titular", Integer.valueOf(0), Integer.valueOf(servicio.inteTelefono));
        igual("domicilio fallback titular", Integer.valueOf(0), Integer.valueOf(servicio.inteDomicilio));
        igual("contacto prestador desde padron", "Contacto padron", orden.getProveedorContacto());
        igual("telefono prestador excluye bajas y nulos", "54 11 44443333 Int. 7", orden.getProveedorTelefono());
        igual("email prestador excluye bajas y nulos", "prestador@example.invalid", orden.getProveedorEmail());
        contiene("domicilio prestador desde padron", orden.getProveedorDomicilio(), "Moron Buenos Aires");
        servicio.afiliado.setNombre("Nombre vigente"); servicio.afiliado.setEmail("propio@example.invalid");
        servicio.completarDatosConsulta(orden, r);
        igual("reconsulta refleja padron vigente", "Nombre vigente", r.getAfiliadoNombre());
        igual("email propio tiene prioridad", "propio@example.invalid", r.getAfiliadoEmail());
        RequerimientoCompra ajeno = requerimiento("Prestaciones Medicas"); ajeno.setIdRequerimientoCompra(99);
        try { servicio.completarDatosConsulta(orden, ajeno); throw new AssertionError("Requerimiento ajeno aceptado"); }
        catch (IllegalArgumentException esperado) { checks++; }
    }
    private static void empresaOpcional() throws Exception {
        Consulta servicio = new Consulta(); RequerimientoCompra r = requerimiento("Sistemas");
        RequerimientoCompraOrden orden = orden(r); servicio.completarDatosConsulta(orden, r);
        igual("Sistemas no consulta afiliado", Integer.valueOf(0), Integer.valueOf(servicio.consultasAfiliado));
        igual("CUIT ausente no exige empresa", Integer.valueOf(0), Integer.valueOf(servicio.consultasEmpresa));
        igual("contacto ausente sigue opcional", null, orden.getProveedorContacto());
        servicio.origen.setEmpresaCuit("30700000001"); servicio.origen.setEmpresaSucursal("1");
        List<Domicilio> domicilios = new ArrayList<Domicilio>(); Domicilio d = new Domicilio();
        d.setCalle("Domicilio empresa"); d.setNumero("10"); domicilios.add(d); servicio.empresa.setDomicilios(domicilios);
        Domicilio dadoBaja = new Domicilio(); dadoBaja.setCalle("DADO_BAJA"); dadoBaja.setBaja_fecha(new java.util.Date());
        domicilios.add(null); domicilios.add(dadoBaja);
        List<Contacto> contactos = new ArrayList<Contacto>(); Contacto contacto = new Contacto();
        contacto.setNombreApe("Compras Empresa"); contacto.setTelefono(new Telefono("54", "11", "77778888", ""));
        contacto.setContacto(new ContactoElectronico(3, ContactoElectronico.Tipo.EMAIL, "empresa@example.invalid", ""));
        contactos.add(contacto); Contacto viejo = new Contacto(); viejo.setNombreApe("CONTACTO_BAJA");
        viejo.setBajaFecha(new java.util.Date()); contactos.add(viejo); servicio.empresa.setContactos(contactos);
        servicio.completarDatosConsulta(orden, r);
        igual("empresa por CUIT y sucursal", Integer.valueOf(1), Integer.valueOf(servicio.consultasEmpresa));
        contiene("domicilio empresa cruzado", orden.getProveedorDomicilio(), "Domicilio empresa 10");
        igual("empresa excluye domicilio dado de baja", Boolean.FALSE, Boolean.valueOf(orden.getProveedorDomicilio().indexOf("DADO_BAJA") >= 0));
        igual("empresa excluye contacto dado de baja", "Compras Empresa", orden.getProveedorContacto());
        contiene("empresa telefono cruzado", orden.getProveedorTelefono(), "54 11 77778888");
        contiene("empresa email cruzado", orden.getProveedorEmail(), "empresa@example.invalid");
    }
    private static void fuentesAusentesYClaves() throws Exception {
        Consulta servicio = new Consulta(); RequerimientoCompra r = requerimiento("Prestaciones Medicas");
        r.setRequiereAfiliado(true); r.setAfiliadoCuilTitular("20123456789"); r.setAfiliadoInt(Integer.valueOf(1));
        r.setAfiliadoNombre("Nombre previo"); r.setAfiliadoEmail("mail-preexistente@example.invalid");
        r.setAfiliadoNombreApellido("ALIAS ORIGINAL"); r.setAfiliadoDocumento("DOCUMENTO ORIGINAL");
        RequerimientoCompraOrden orden = orden(r); orden.setPlan("Plan previo");
        servicio.afiliado = null; servicio.completarDatosConsulta(orden, r);
        igual("sin padron conserva nombre", "Nombre previo", r.getAfiliadoNombre());
        igual("sin padron conserva alias nombre", "ALIAS ORIGINAL", r.getAfiliadoNombreApellido());
        igual("sin padron conserva alias documento", "DOCUMENTO ORIGINAL", r.getAfiliadoDocumento());
        igual("sin padron conserva plan", "Plan previo", orden.getPlan());
        servicio = new Consulta(); servicio.titular = null;
        servicio.completarDatosConsulta(orden, r);
        igual("sin email opcional conserva valor previo", "mail-preexistente@example.invalid", r.getAfiliadoEmail());
        servicio.afiliado.setCuil_titular("20999999999");
        rechazaClave(servicio, orden, r, "afiliado CUIL ajeno");
        servicio.afiliado.setCuil_titular("20123456789"); servicio.afiliado.setInte(2);
        rechazaClave(servicio, orden, r, "afiliado integrante ajeno");
        servicio.afiliado.setInte(1); servicio.origen.setIdPrestador(Integer.valueOf(30));
        servicio.prestador.setId_prestador(99);
        rechazaClave(servicio, orden, r, "prestador ajeno al origen");
        servicio = new Consulta(); r = requerimiento("Sistemas"); orden = orden(r);
        servicio.origen.setEmpresaCuit("30700000001"); servicio.origen.setEmpresaSucursal("1");
        servicio.empresa.setCuit("30700000002");
        rechazaClave(servicio, orden, r, "empresa CUIT ajeno");
        servicio.empresa.setCuit("30700000001"); servicio.empresa.setSucursal("2");
        rechazaClave(servicio, orden, r, "empresa sucursal ajena");
    }
    private static void telefonoFallback() throws Exception {
        Consulta servicio = new Consulta(); RequerimientoCompra r = requerimiento("Prestaciones Medicas");
        r.setRequiereAfiliado(true); r.setAfiliadoCuilTitular("20123456789"); r.setAfiliadoInt(Integer.valueOf(1));
        servicio.afiliado.setId_parentesco(ar.com.ospim.afiliados.WebKeysAfiliados.PARENTESCO_DEFAULT);
        servicio.telefonosIndividual = new ArrayList<Telefono>();
        Telefono baja = new Telefono("54", "11", "BAJA", ""); baja.setBajaFecha(new java.util.Date());
        servicio.telefonosIndividual.add(baja); servicio.telefonos.add(new Telefono("54", "11", "ACTUAL", ""));
        servicio.completarDatosConsulta(orden(r), r);
        igual("todos telefonos propios de baja usa titular", "54 11 ACTUAL", r.getAfiliadoTelefono());
        igual("fallback telefonos consulta titular", Integer.valueOf(0), Integer.valueOf(servicio.inteTelefono));
    }
    private static void rechazaClave(Consulta servicio, RequerimientoCompraOrden orden,
            RequerimientoCompra r, String caso) throws Exception {
        try { servicio.completarDatosConsulta(orden, r); throw new AssertionError("Acepto " + caso); }
        catch (IllegalArgumentException esperado) { checks++; }
    }
    private static RequerimientoCompra requerimiento(String sector) {
        RequerimientoCompra r = new RequerimientoCompra(); r.setIdRequerimientoCompra(40); r.setSectorDescripcion(sector); return r;
    }
    private static RequerimientoCompraOrden orden(RequerimientoCompra r) {
        RequerimientoCompraOrden o = new RequerimientoCompraOrden(); o.setIdRequerimientoCompra(40); o.setRequerimiento(r); return o;
    }
    private static class Consulta extends RequerimientoCompraOrdenServiceImpl {
        Afiliado afiliado = new Afiliado() { public String getNombrePlan() { return "Plan actual"; } };
        Afiliado titular = new Afiliado();
        List<Domicilio> domicilios = new ArrayList<Domicilio>();
        List<Telefono> telefonos = new ArrayList<Telefono>();
        List<Telefono> telefonosIndividual;
        Prestador prestador = new Prestador(); Empresa empresa = new Empresa();
        RequerimientoCompraPresupuesto origen = new RequerimientoCompraPresupuesto();
        int inteTelefono = -1; int inteDomicilio = -1; int consultasAfiliado; int consultasEmpresa;
        Consulta() {
            afiliado.setCuil_titular("20123456789"); afiliado.setInte(1);
            titular.setCuil_titular("20123456789"); titular.setInte(0);
            prestador.setId_prestador(30); empresa.setCuit("30700000001"); empresa.setSucursal("1");
        }
        protected Afiliado buscarAfiliadoOpcional(String cuil, int inte) { return titular; }
        protected Afiliado buscarAfiliado(String cuil, int inte) { consultasAfiliado++; return inte == 0 ? titular : afiliado; }
        protected List<Domicilio> buscarDomicilios(String cuil, int inte) {
            inteDomicilio = inte; return inte == 0 ? domicilios : new ArrayList<Domicilio>();
        }
        protected List<Telefono> buscarTelefonos(String cuil, int inte) { inteTelefono = inte; return inte != 0 && telefonosIndividual != null ? telefonosIndividual : telefonos; }
        protected Prestador buscarPrestador(int id) { if (id != 30) throw new AssertionError("Prestador de origen incorrecto"); return prestador; }
        protected Empresa buscarEmpresa(String cuit, String sucursal) {
            igual("CUIT de origen", "30700000001", cuit); igual("sucursal de origen", "1", sucursal); consultasEmpresa++; return empresa;
        }
        protected RequerimientoCompraPresupuesto obtenerOrigenProveedor(RequerimientoCompraOrden orden) { return origen; }
    }
    private static void contiene(String caso, String texto, String esperado) {
        checks++; if (texto == null || texto.indexOf(esperado) < 0) throw new AssertionError(caso + ": " + texto);
    }
    private static void igual(String caso, Object esperado, Object actual) {
        checks++; if (esperado == null ? actual != null : !esperado.equals(actual)) throw new AssertionError(caso + ": " + actual);
    }
}
