import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompra;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraComparativa;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraComparativaDetalle;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraDetalle;
import ar.com.ospim.compras.requerimientos.documentos.DocumentoLibraryComprasHelper;
import ar.com.ospim.compras.requerimientos.helper.RequerimientoCompraComparativaHelper;
import ar.com.ospim.servlets.PdfServlet;
import com.liferay.documentlibrary.service.DLLocalService;
import com.liferay.documentlibrary.service.DLLocalServiceUtil;
import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.util.PortalClassLoaderUtil;
import com.liferay.portal.security.permission.PermissionChecker;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portlet.documentlibrary.NoSuchFileEntryException;
import com.liferay.portlet.documentlibrary.model.DLFileEntry;
import com.liferay.portlet.documentlibrary.model.DLFolder;
import com.liferay.portlet.documentlibrary.service.DLFileEntryLocalService;
import com.liferay.portlet.documentlibrary.service.DLFileEntryLocalServiceUtil;
import com.liferay.portlet.documentlibrary.service.DLFolderLocalService;
import com.liferay.portlet.documentlibrary.service.DLFolderLocalServiceUtil;
import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.pdfbox.pdmodel.PDDocument;
import org.pdfbox.util.PDFTextStripper;

/** Fixture offline: PDF Jasper real y servicios DL sustituidos solo en el test. */
public class ComprasComparativaSincronizacionTest {
    private static int pruebas;
    private static final long ID_DOCUMENTO = 5006796L;
    private static final long ID_CARPETA = 212306L;
    private static final long ID_GRUPO = 10136L;
    private static final long ID_USUARIO = 501L;
    private static final long ID_COMPANIA = 1L;
    private static final String NOMBRE = "DLFE-930965.pdf";
    private static final String TITULO = "68180-ComparativaCompra_1376";

    public static void main(String[] args) throws Exception {
        Fixture normal = fixture(2, 3, false);
        byte[] correcto = generar(normal);
        byte[] erroneo = generar(fixture(2, 3, true));
        comprobarImportes(correcto, erroneo);
        escribir(args[0], "comparativa-corregida.pdf", correcto);
        escribir(args[0], "comparativa-cantidades-erroneas.pdf", erroneo);

        byte[] otraMetadata = metadata(correcto, "D:20261027235959");
        verificar(!Arrays.equals(correcto, otraMetadata), "El fixture debe cambiar los bytes.");
        verificar(DocumentoLibraryComprasHelper.mismoContenidoComparativa(correcto, otraMetadata),
                "Una fecha de creacion diferente no debe generar otra version.");
        aprobado("METADATA_DISTINTA_MISMO_CONTENIDO");
        verificar(!DocumentoLibraryComprasHelper.mismoContenidoComparativa(erroneo, correcto),
                "No detecto la correccion de los subtotales elevados al cuadrado.");
        aprobado("SUBTOTALES_CUADRADOS_CORREGIDOS");

        Fixture cambio = fixture(2, 3, false);
        cambio.comparativas.get(0).getDetalles().get(1).setCantidad(Integer.valueOf(2));
        diferente(correcto, cambio, "CANTIDAD_CAMBIADA");
        cambio = fixture(2, 3, false);
        cambio.comparativas.get(0).getDetalles().get(1).setImporteUnitario(new BigDecimal("5710878.22"));
        diferente(correcto, cambio, "PRECIO_UNITARIO_CAMBIADO");
        cambio = fixture(2, 3, false);
        cambio.comparativas.get(0).setIva(new BigDecimal("1234.56"));
        diferente(correcto, cambio, "IVA_CAMBIADO");
        cambio = fixture(2, 3, false);
        cambio.comparativas.get(0).setIibb(new BigDecimal("12.34"));
        diferente(correcto, cambio, "IIBB_CAMBIADO");
        cambio = fixture(2, 3, false);
        cambio.comparativas.get(0).setFormaPago(Integer.valueOf(30));
        diferente(correcto, cambio, "FORMA_PAGO_CAMBIADA");

        Fixture largo = fixture(60, 5, false);
        byte[] multipagina = generar(largo);
        PdfReader paginas = new PdfReader(multipagina);
        try { verificar(paginas.getNumberOfPages() > 1, "El fixture debe tener varias paginas."); }
        finally { paginas.close(); }
        largo.comparativas.get(4).getDetalles().get(59).setImporteUnitario(new BigDecimal("4321.10"));
        verificar(!DocumentoLibraryComprasHelper.mismoContenidoComparativa(multipagina, generar(largo)),
                "No detecto un cambio en el ultimo proveedor y ultimo item multipagina.");
        aprobado("CAMBIO_EN_ULTIMO_ITEM_MULTIPAGINA");
        escribir(args[0], "comparativa-multipagina.pdf", multipagina);

        rechazarComparacion(new byte[] {1, 2, 3}, correcto, "PDF_ANTERIOR_INVALIDO");
        rechazarComparacion(correcto, new byte[] {1, 2, 3}, "PDF_NUEVO_INVALIDO");
        rechazarComparacion(null, correcto, "PDF_ANTERIOR_NULO");
        rechazarComparacion(correcto, pdfVacio(), "PDF_SIN_TEXTO");
        probarActualizacion(erroneo, correcto, otraMetadata);
        System.out.println("COMPARATIVA_SINCRONIZACION_OK pruebas=" + pruebas);
    }

    private static void comprobarImportes(byte[] correcto, byte[] erroneo) throws Exception {
        String actual = texto(correcto).replaceAll("\\s+", "");
        String anterior = texto(erroneo).replaceAll("\\s+", "");
        String[] cuadrados = {"32.614.138.952.641,00", "36.044.942.017.536,00", "37.465.294.392.100,00"};
        String[] unitarios = {"5.710.879,00", "6.003.744,00", "6.120.890,00"};
        for (int i = 0; i < cuadrados.length; i++) {
            verificar(anterior.indexOf(cuadrados[i]) >= 0, "No reprodujo el subtotal erroneo " + cuadrados[i]);
            verificar(actual.indexOf(cuadrados[i]) < 0, "El PDF corregido conserva un subtotal erroneo.");
            verificar(actual.indexOf(unitarios[i]) >= 0, "El PDF corregido perdio el importe unitario.");
        }
        aprobado("JASPER_REAL_REPRODUCE_CASO_1376");
    }

    private static void diferente(byte[] original, Fixture cambio, String caso) throws Exception {
        verificar(!DocumentoLibraryComprasHelper.mismoContenidoComparativa(original, generar(cambio)),
                "No detecto " + caso);
        aprobado(caso);
    }

    private static byte[] generar(Fixture fixture) throws Exception {
        RequerimientoCompraComparativaHelper helper = new RequerimientoCompraComparativaHelper();
        for (RequerimientoCompraComparativa c : fixture.comparativas) helper.calcular(c);
        Map<String, Map<String, ?>> proveedores = new java.util.LinkedHashMap<String, Map<String, ?>>();
        List<Map<String, ?>> filas = helper.filasReportePdf(fixture.requerimiento, fixture.comparativas, proveedores);
        HashMap<String, Object> parametros = new HashMap<String, Object>();
        parametros.put("REQUERIMIENTO", "1376");
        parametros.put("PROVEEDORES", proveedores);
        Method metodo = PdfServlet.class.getDeclaredMethod("crearPdfComparativaCompra",
                new Class[] {List.class, Map.class, HashMap.class});
        metodo.setAccessible(true);
        try {
            return (byte[]) metodo.invoke(new PdfServlet(), new Object[] {filas, proveedores, parametros});
        } catch (InvocationTargetException e) {
            Throwable causa = e.getCause();
            if (causa instanceof Exception) throw (Exception) causa;
            throw e;
        }
    }

    private static Fixture fixture(int cantidadItems, int cantidadProveedores, boolean cantidadErronea) {
        Fixture fixture = new Fixture();
        RequerimientoCompra r = new RequerimientoCompra();
        r.setId(Integer.valueOf(1376));
        r.setEstado(6);
        List<RequerimientoCompraDetalle> detalles = new ArrayList<RequerimientoCompraDetalle>();
        for (int i = 0; i < cantidadItems; i++) {
            RequerimientoCompraDetalle d = new RequerimientoCompraDetalle();
            d.setId(Integer.valueOf(413 + i));
            d.setIdPrestacion(Integer.valueOf(10609 + i));
            d.setCodigoItem("COD" + i);
            d.setDescripcionItem("Item de prueba " + i);
            d.setCantidad(Integer.valueOf(1));
            d.setIdPrestador(Integer.valueOf(89638));
            detalles.add(d);
        }
        r.setDetalles(detalles);
        fixture.requerimiento = r;
        String[] importes = {"5710879.00", "6003744.00", "6120890.00", "1234.56", "3210.99"};
        for (int p = 0; p < cantidadProveedores; p++) {
            RequerimientoCompraComparativa c = new RequerimientoCompraComparativa();
            c.setIdComparativa(34 + p);
            c.setIdRequerimiento(1376);
            c.setIdPrestador(p == 0 ? 89638 : 2200 + p);
            c.setPrestador("PROVEEDOR DE PRUEBA " + p);
            c.setFechaPresupuesto(new Date(1793066400000L));
            c.setFormaPago(Integer.valueOf(45));
            c.setPlazoEntrega("48/72");
            c.setValidezPresupuesto(Integer.valueOf(48));
            c.setEnvio("Beneficiario");
            for (int i = 0; i < cantidadItems; i++) {
                RequerimientoCompraComparativaDetalle d = new RequerimientoCompraComparativaDetalle();
                d.setIdDetalle(1000 + p * cantidadItems + i);
                d.setIdPrestacion(10609 + i);
                d.setCodigo("COD" + i);
                d.setPrestacion("Item de prueba " + i);
                BigDecimal unitario = i == 0 ? BigDecimal.ZERO : new BigDecimal(importes[p]);
                d.setImporteUnitario(unitario);
                d.setCantidad(Integer.valueOf(i == 0 ? 0 : cantidadErronea ? unitario.intValue() : 1));
                c.getDetalles().add(d);
            }
            fixture.comparativas.add(c);
        }
        return fixture;
    }

    private static byte[] metadata(byte[] original, String fecha) throws Exception {
        PdfReader reader = new PdfReader(original);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            PdfStamper stamper = new PdfStamper(reader, out);
            HashMap info = new HashMap(reader.getInfo());
            info.put("CreationDate", fecha);
            info.put("ModDate", fecha);
            stamper.setMoreInfo(info);
            stamper.close();
        } finally { reader.close(); }
        return out.toByteArray();
    }

    private static byte[] pdfVacio() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document documento = new Document();
        com.lowagie.text.pdf.PdfWriter.getInstance(documento, out);
        documento.open();
        documento.add(new Paragraph(" "));
        documento.close();
        return out.toByteArray();
    }

    private static void rechazarComparacion(byte[] anterior, byte[] nuevo, String caso) throws Exception {
        boolean rechazado = false;
        try { DocumentoLibraryComprasHelper.mismoContenidoComparativa(anterior, nuevo); }
        catch (Exception expected) { rechazado = true; }
        verificar(rechazado, "Acepto " + caso);
        aprobado(caso);
    }

    private static void probarActualizacion(byte[] anterior, byte[] nuevo, byte[] mismaMetadata) throws Exception {
        ClassLoader loaderAnterior = PortalClassLoaderUtil.getClassLoader();
        PortalClassLoaderUtil.setClassLoader(ComprasComparativaSincronizacionTest.class.getClassLoader());
        BibliotecaFake fake = new BibliotecaFake(anterior);
        Object servicioArchivo = reemplazarServicio(DLFileEntryLocalServiceUtil.class,
                proxy(DLFileEntryLocalService.class, fake));
        Object servicioCarpeta = reemplazarServicio(DLFolderLocalServiceUtil.class,
                proxy(DLFolderLocalService.class, fake));
        Object servicioContenido = reemplazarServicio(DLLocalServiceUtil.class,
                proxy(DLLocalService.class, fake));
        try {
            verificar(actualizar(nuevo, fake), "No actualizo el PDF con subtotales erroneos.");
            verificar(fake.actualizaciones == 1, "La correccion debe escribir una sola version.");
            verificar(Arrays.equals(nuevo, fake.contenido), "No persistio el PDF recien generado.");
            verificar(fake.documento.getFileEntryId() == ID_DOCUMENTO
                    && fake.documento.getFolderId() == ID_CARPETA
                    && NOMBRE.equals(fake.documento.getName())
                    && TITULO.equals(fake.documento.getTitle()), "Cambio la identidad del documento.");
            verificar(fake.ultimoNombre.equals(NOMBRE) && fake.ultimoTitulo.equals(TITULO),
                    "La API no recibio el nombre y titulo originales.");
            aprobado("ACTUALIZA_MISMO_DOCUMENTO_SIN_ALTA");

            fake.permiteActualizar = false;
            fake.permisos.clear();
            verificar(!actualizar(mismaMetadata, fake), "Reimprimir valores iguales debe ser idempotente.");
            verificar(fake.actualizaciones == 1, "Genero una version nueva sin cambios de contenido.");
            verificar(!fake.permisos.contains("UPDATE"), "Exigio UPDATE para imprimir contenido sin cambios.");
            aprobado("REIMPRESION_IGUAL_NO_ESCRIBE_NI_EXIGE_UPDATE");

            fake.permiteVer = false;
            int lecturas = fake.lecturas;
            rechazarActualizacion(anterior, fake, "SIN_VIEW_NO_LEE_CONTENIDO");
            verificar(fake.lecturas == lecturas && fake.actualizaciones == 1,
                    "Leyo o escribio el archivo sin VIEW.");
            fake.permiteVer = true;
            rechazarActualizacion(anterior, fake, "SIN_UPDATE_NO_REEMPLAZA");
            verificar(fake.actualizaciones == 1 && Arrays.equals(nuevo, fake.contenido),
                    "Modifico el documento sin UPDATE.");

            fake.permiteActualizar = true;
            fake.existeDocumento = false;
            rechazarActualizacion(anterior, fake, "DOCUMENTO_AUSENTE_NO_CREA_OTRO");
            verificar(fake.altas == 0 && fake.actualizaciones == 1, "Creo otro documento al faltar el original.");
            fake.existeDocumento = true;
            fake.fallaActualizacion = true;
            rechazarActualizacion(anterior, fake, "ERROR_DL_SE_PROPAGA");
            verificar(Arrays.equals(nuevo, fake.contenido) && fake.actualizaciones == 1,
                    "Un fallo de actualizacion altero el contenido previo.");

            fake.fallaActualizacion = false;
            fake.alterarIdentidad = true;
            boolean identidadRechazada = false;
            try { actualizar(anterior, fake); }
            catch (Exception expected) {
                identidadRechazada = expected.getMessage() != null
                        && expected.getMessage().toLowerCase().indexOf("identidad") >= 0;
            }
            verificar(fake.identidadAlterada
                    && fake.documento.getFileEntryId() != ID_DOCUMENTO
                    && !TITULO.equals(fake.documento.getTitle()),
                    "El fixture no altero la identidad del mismo objeto retornado.");
            verificar(identidadRechazada,
                    "No detecto que DL modifico y retorno el mismo objeto con otra identidad.");
            aprobado("IDENTIDAD_MUTADA_EN_MISMO_OBJETO_RECHAZADA");
        } finally {
            reemplazarServicio(DLFileEntryLocalServiceUtil.class, servicioArchivo);
            reemplazarServicio(DLFolderLocalServiceUtil.class, servicioCarpeta);
            reemplazarServicio(DLLocalServiceUtil.class, servicioContenido);
            PortalClassLoaderUtil.setClassLoader(loaderAnterior);
        }
    }

    private static boolean actualizar(byte[] pdf, BibliotecaFake fake) throws Exception {
        ServiceContext contexto = new ServiceContext();
        contexto.setCompanyId(ID_COMPANIA);
        contexto.setScopeGroupId(ID_GRUPO);
        contexto.setUserId(ID_USUARIO);
        PermissionChecker checker = (PermissionChecker) proxy(PermissionChecker.class, fake.permiso);
        return DocumentoLibraryComprasHelper.actualizarComparativaReclamoPrestacional(
                1376, 68180, pdf, contexto, checker);
    }

    private static void rechazarActualizacion(byte[] pdf, BibliotecaFake fake, String caso) throws Exception {
        boolean rechazado = false;
        try { actualizar(pdf, fake); } catch (Exception expected) { rechazado = true; }
        verificar(rechazado, "No propago " + caso);
        aprobado(caso);
    }

    private static Object reemplazarServicio(Class tipo, Object servicio) throws Exception {
        Field campo = tipo.getDeclaredField("_service");
        campo.setAccessible(true);
        Object anterior = campo.get(null);
        campo.set(null, servicio);
        return anterior;
    }

    private static Object proxy(Class tipo, InvocationHandler handler) {
        return Proxy.newProxyInstance(tipo.getClassLoader(), new Class[] {tipo}, handler);
    }

    private static final class BibliotecaFake implements InvocationHandler {
        private byte[] contenido;
        private boolean permiteVer = true;
        private boolean permiteActualizar = true;
        private boolean existeDocumento = true;
        private boolean fallaActualizacion;
        private boolean alterarIdentidad;
        private boolean identidadAlterada;
        private int actualizaciones;
        private int altas;
        private int lecturas;
        private double version = 1D;
        private String ultimoNombre;
        private String ultimoTitulo;
        private final List<String> permisos = new ArrayList<String>();
        private final DLFileEntry documento;
        private final DLFolder carpeta;
        private final InvocationHandler permiso;

        private BibliotecaFake(byte[] inicial) {
            contenido = inicial;
            documento = (DLFileEntry) proxy(DLFileEntry.class, new InvocationHandler() {
                public Object invoke(Object obj, Method method, Object[] args) {
                    String nombre = method.getName();
                    if ("getFileEntryId".equals(nombre) || "getPrimaryKey".equals(nombre)) return Long.valueOf(identidadAlterada ? ID_DOCUMENTO + 1L : ID_DOCUMENTO);
                    if ("getFolderId".equals(nombre)) return Long.valueOf(ID_CARPETA);
                    if ("getGroupId".equals(nombre)) return Long.valueOf(ID_GRUPO);
                    if ("getCompanyId".equals(nombre)) return Long.valueOf(ID_COMPANIA);
                    if ("getUserId".equals(nombre)) return Long.valueOf(ID_USUARIO);
                    if ("getName".equals(nombre)) return NOMBRE;
                    if ("getTitle".equals(nombre)) return identidadAlterada ? TITULO + "-alterado" : TITULO;
                    if ("getDescription".equals(nombre)) return "Comparativa de prueba";
                    if ("getExtraSettings".equals(nombre)) return "";
                    if ("getUuid".equals(nombre)) return "uuid-comparativa-prueba";
                    if ("getSize".equals(nombre)) return Integer.valueOf(contenido.length);
                    if ("getVersion".equals(nombre)) return Double.valueOf(version);
                    return valorDefecto(method.getReturnType());
                }
            });
            carpeta = (DLFolder) proxy(DLFolder.class, new InvocationHandler() {
                public Object invoke(Object obj, Method method, Object[] args) {
                    String nombre = method.getName();
                    if ("getFolderId".equals(nombre) || "getPrimaryKey".equals(nombre)) return Long.valueOf(ID_CARPETA);
                    if ("getGroupId".equals(nombre)) return Long.valueOf(ID_GRUPO);
                    if ("getCompanyId".equals(nombre)) return Long.valueOf(ID_COMPANIA);
                    if ("getName".equals(nombre)) return "ReclamosPrestacionales";
                    return valorDefecto(method.getReturnType());
                }
            });
            permiso = new InvocationHandler() {
                public Object invoke(Object obj, Method method, Object[] args) {
                    String nombre = method.getName();
                    if ("getUserId".equals(nombre)) return Long.valueOf(ID_USUARIO);
                    if ("getCompanyId".equals(nombre)) return Long.valueOf(ID_COMPANIA);
                    if ("hasOwnerPermission".equals(nombre)) return Boolean.FALSE;
                    if ("hasPermission".equals(nombre)) {
                        String accion = String.valueOf(args[args.length - 1]);
                        permisos.add(accion);
                        return Boolean.valueOf("VIEW".equals(accion) ? permiteVer
                                : "UPDATE".equals(accion) && permiteActualizar);
                    }
                    return valorDefecto(method.getReturnType());
                }
            };
        }

        public Object invoke(Object obj, Method method, Object[] args) throws Throwable {
            String nombre = method.getName();
            if ("getFolder".equals(nombre)) return carpeta;
            if ("getFileEntryByTitle".equals(nombre)) {
                verificar(((Long) args[0]).longValue() == ID_CARPETA, "Busco otra carpeta.");
                verificar((TITULO + ".pdf").equals(args[1]), "Busco otro titulo.");
                if (!existeDocumento) throw new NoSuchFileEntryException("Fixture sin documento");
                return documento;
            }
            if ("getFileEntry".equals(nombre) || "getDLFileEntry".equals(nombre)) return documento;
            if ("getFileAsStream".equals(nombre)) {
                lecturas++;
                return new ByteArrayInputStream(contenido);
            }
            if ("updateFileEntry".equals(nombre)) {
                if (fallaActualizacion) throw new SystemException("FALLO_DL_SIMULADO");
                verificar(((Long) args[1]).longValue() == ID_CARPETA, "Actualizo otra carpeta.");
                ultimoNombre = String.valueOf(args[3]);
                ultimoTitulo = String.valueOf(args[5]);
                byte[] recibido = null;
                for (int i = 0; i < args.length; i++) if (args[i] instanceof byte[]) recibido = (byte[]) args[i];
                verificar(recibido != null, "No uso la API de bytes indicada para el fixture.");
                contenido = recibido;
                version += 0.1D;
                actualizaciones++;
                if (alterarIdentidad) identidadAlterada = true;
                return documento;
            }
            if (nombre.startsWith("add")) {
                altas++;
                throw new AssertionError("La sincronizacion no debe crear documentos: " + nombre);
            }
            throw new AssertionError("Llamada no prevista al servicio DL: " + nombre);
        }
    }

    private static Object valorDefecto(Class tipo) {
        if (tipo == Boolean.TYPE) return Boolean.FALSE;
        if (tipo == Long.TYPE) return Long.valueOf(0L);
        if (tipo == Integer.TYPE) return Integer.valueOf(0);
        if (tipo == Double.TYPE) return Double.valueOf(0D);
        return null;
    }

    private static String texto(byte[] pdf) throws Exception {
        PDDocument documento = PDDocument.load(new ByteArrayInputStream(pdf));
        try { return new PDFTextStripper().getText(documento); }
        finally { documento.close(); }
    }

    private static void escribir(String ruta, String nombre, byte[] contenido) throws Exception {
        FileOutputStream out = new FileOutputStream(ruta + "/" + nombre);
        try { out.write(contenido); } finally { out.close(); }
    }

    private static void verificar(boolean condicion, String mensaje) throws Exception {
        if (!condicion) throw new Exception(mensaje);
    }

    private static void aprobado(String caso) {
        pruebas++;
        System.out.println(caso + "_OK");
    }

    private static final class Fixture {
        private RequerimientoCompra requerimiento;
        private List<RequerimientoCompraComparativa> comparativas = new ArrayList<RequerimientoCompraComparativa>();
    }
}
