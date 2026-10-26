import ar.com.ospim.compras.requerimientos.beans.*;
import ar.com.ospim.compras.requerimientos.helper.RequerimientoCompraOrdenPdfHelper;
import java.math.BigDecimal;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;
import java.io.FileOutputStream;
import java.io.ByteArrayInputStream;
import org.pdfbox.pdmodel.PDDocument;
import org.pdfbox.util.PDFTextStripper;
import com.lowagie.text.pdf.PdfReader;

public class ComprasOrdenPdfTest {
    public static void main(String[] args) throws Exception {
        String[] nombres = new String[] {"sistemas", "medica", "multipagina", "campos-largos",
                "sistemas-sin-importes", "detalle-sin-importe", "ceros-informados", "medica-pesos"};
        for (int caso = 0; caso < nombres.length; caso++) {
            RequerimientoCompraOrden orden = fixture(caso);
            byte[] pdf = new RequerimientoCompraOrdenPdfHelper().generar(orden);
            if (pdf.length < 1000 || pdf[0] != '%' || pdf[1] != 'P') throw new Exception("PDF invalido");
            comprobarPesos(pdf);
            PdfReader reader = new PdfReader(pdf);
            int paginas;
            try { paginas = reader.getNumberOfPages(); } finally { reader.close(); }
            if (caso != 2 && caso != 3 && paginas != 1) throw new Exception("Formato normal no ocupa una pagina");
            if ((caso == 2 || caso == 3) && paginas <= 1) throw new Exception("Falta paginacion de campos o detalles largos");
            FileOutputStream out = new FileOutputStream(args[0] + "/" + nombres[caso] + ".pdf");
            try { out.write(pdf); } finally { out.close(); }
            System.out.println("PDF_OK caso=" + caso + " bytes=" + pdf.length + " paginas=" + paginas);
        }
        RequerimientoCompraOrden monedaVacia = fixture(4);
        monedaVacia.setMoneda("  ");
        if (!"ARS".equals(monedaVacia.getMoneda())) throw new Exception("La moneda legacy vacia debe usar pesos.");
        if (new RequerimientoCompraOrdenPdfHelper().generar(monedaVacia).length < 1000) {
            throw new Exception("No genero la orden en pesos con moneda legacy vacia.");
        }
        System.out.println("MONEDA_LEGACY_VACIA_USA_PESOS");
        RequerimientoCompraOrden impuestoDesconocido = fixture(0);
        impuestoDesconocido.setIva(null);
        if (impuestoDesconocido.getTotal() != null) {
            throw new Exception("El impuesto desconocido no debe convertirse en cero.");
        }
        if (new RequerimientoCompraOrdenPdfHelper().generar(impuestoDesconocido).length < 1000) {
            throw new Exception("No genero la orden con impuesto sin informar.");
        }
        System.out.println("IMPUESTO_DESCONOCIDO_ACEPTADO_SIN_TOTAL");
        RequerimientoCompraOrden invalida = fixture(0);
        invalida.setDetalles(new ArrayList<RequerimientoCompraDetalle>());
        try { new RequerimientoCompraOrdenPdfHelper().generar(invalida); throw new Exception("Acepto detalle vacio"); }
        catch (IllegalArgumentException expected) { System.out.println("DETALLES_VACIOS_RECHAZADOS"); }
        invalida = fixture(0);
        try { invalida.setMoneda("EUR"); throw new Exception("Acepto moneda distinta de pesos"); }
        catch (IllegalArgumentException expected) { System.out.println("MONEDA_AJENA_RECHAZADA"); }
        invalida = fixture(0); invalida.setIva(new BigDecimal("-1"));
        rechazar(invalida, "IMPUESTO_NEGATIVO");
        invalida = fixture(0); invalida.getDetalles().get(0).setPrecioUnitarioEstimado(new BigDecimal("-1"));
        rechazar(invalida, "PRECIO_NEGATIVO");
        invalida = fixture(0); invalida.getDetalles().get(0).setPrecioTotalEstimado(new BigDecimal("-1"));
        rechazar(invalida, "TOTAL_DETALLE_NEGATIVO");
        invalida = fixture(0); invalida.getDetalles().get(0).setPrecioTotalEstimado(BigDecimal.ONE);
        rechazar(invalida, "TOTAL_DETALLE_INCONSISTENTE");
        invalida = fixture(0); invalida.getDetalles().get(0).setPrecioUnitarioEstimado(null);
        invalida.getDetalles().get(0).setPrecioTotalEstimado(BigDecimal.ONE);
        rechazar(invalida, "PAR_FINANCIERO_INCOMPLETO");
        invalida = fixture(0); invalida.setIdOrdenCompra(0);
        try { new RequerimientoCompraOrdenPdfHelper().generar(invalida); throw new Exception("Acepto borrador"); }
        catch (IllegalArgumentException expected) { System.out.println("BORRADOR_RECHAZADO"); }
    }
    private static void comprobarPesos(byte[] pdf) throws Exception {
        PDDocument documento = PDDocument.load(new ByteArrayInputStream(pdf));
        try {
            String texto = new PDFTextStripper().getText(documento).replaceAll("\\s+", " ");
            if (texto.indexOf("Pesos (ARS)") < 0 || texto.indexOf("USD") >= 0) {
                throw new Exception("El PDF debe mostrar siempre importes en Pesos (ARS).");
            }
        } finally {
            documento.close();
        }
    }
    private static void rechazar(RequerimientoCompraOrden orden, String nombre) throws Exception {
        try { new RequerimientoCompraOrdenPdfHelper().generar(orden); throw new Exception("Acepto " + nombre); }
        catch (IllegalArgumentException expected) { System.out.println(nombre + "_RECHAZADO"); }
    }
    private static RequerimientoCompraOrden fixture(int caso) {
        RequerimientoCompra r = new RequerimientoCompra();
        r.setId(Integer.valueOf(123)); r.setEstado(5);
        r.setSectorDescripcion(caso == 0 || caso == 4 || caso == 6 ? "SISTEMAS" : "PRESTACIONES MEDICAS");
        r.setRequiereAfiliado(caso != 0 && caso != 4 && caso != 6); r.setAfiliadoNombre("AFILIADO"); r.setAfiliadoApellido("DE PRUEBA");
        r.setAfiliadoDocumento("Documento de prueba"); r.setAfiliadoLocalidad("Localidad de prueba");
        r.setAfiliadoIdOspim(Integer.valueOf(1234));
        RequerimientoCompraOrden o = new RequerimientoCompraOrden();
        o.setIdOrdenCompra(100 + caso); o.setIdRequerimientoCompra(123); o.setFechaEmision(new Date(1792713600000L));
        o.setRequerimiento(r); o.setMoneda("ARS");
        o.setProveedor("PROVEEDOR DE PRUEBA"); o.setProveedorCuit("CUIT de prueba");
        o.setProveedorDomicilio("Domicilio de prueba"); o.setProveedorContacto("Contacto de prueba");
        o.setProveedorEmail("compras@example.invalid"); o.setProveedorTelefono("Telefono de prueba");
        o.setCondicion("Condicion registrada"); o.setFormaPago("Forma de pago registrada"); o.setPlazoPago("Plazo registrado");
        o.setEntrega("Entrega registrada en el requerimiento"); o.setSolicitante("Sector solicitante");
        o.setConfeccionadoPor("Usuario de prueba"); o.setTelefonoCompras("Telefono interno de prueba");
        o.setPlan("Plan de prueba"); o.setIdReclamoPrestacional(456);
        o.setFechaOrdenMedica(new Date(1792713600000L)); o.setFechaRecepcion(new Date(1792713600000L));
        o.setObservaciones("Datos de ejemplo exclusivamente para verificar el formato. Sin valor de orden de compra.");
        o.setIva(new BigDecimal("123.45")); o.setIibb(new BigDecimal("12.34"));
        List<RequerimientoCompraDetalle> detalles = new ArrayList<RequerimientoCompraDetalle>();
        for (int i = 0; i < (caso == 2 ? 60 : 3); i++) {
            RequerimientoCompraDetalle d = new RequerimientoCompraDetalle();
            d.setCantidad(Integer.valueOf(i + 1)); d.setPrecioUnitarioEstimado(new BigDecimal("1234.56"));
            d.setDescripcionItem(caso == 0 || caso == 4 || caso == 6 ? "Equipo y servicios de prueba" : "Prestacion medica de prueba con descripcion extensa para verificar la lectura completa y el crecimiento de la fila de detalle.");
            d.setObservaciones(caso == 2 ? "Observacion extensa para asegurar que el texto continua dentro de la columna, mantiene los valores unitarios y no pierde la separacion visual entre renglones." : "Observacion de prueba");
            detalles.add(d);
        }
        if (caso == 3) {
            String largo = "";
            for (int j = 0; j < 12; j++) largo += "Dato extenso de prueba ";
            o.setProveedor(largo); o.setProveedorDomicilio(largo); o.setProveedorContacto(largo);
            o.setProveedorEmail(largo); o.setCondicion(largo); o.setFormaPago(largo); o.setSolicitante(largo);
            o.setEntrega(largo); o.setObservaciones(largo + largo);
        }
        if (caso == 4) {
            o.setMoneda(null); o.setIva(null); o.setIibb(null);
            o.setCondicion(null); o.setFormaPago(null); o.setPlazoPago(null); o.setEntrega(null);
            o.setProveedorDomicilio(null); o.setProveedorContacto(null); o.setProveedorEmail(null);
            o.setProveedorTelefono(null); o.setObservaciones(null); o.setTelefonoCompras(null);
            for (RequerimientoCompraDetalle d : detalles) {
                d.setPrecioUnitarioEstimado(null); d.setPrecioTotalEstimado(null);
            }
        } else if (caso == 5) {
            detalles.get(1).setPrecioUnitarioEstimado(null);
            detalles.get(1).setPrecioTotalEstimado(null);
        } else if (caso == 6) {
            o.setIva(BigDecimal.ZERO); o.setIibb(BigDecimal.ZERO);
            for (RequerimientoCompraDetalle d : detalles) d.setPrecioUnitarioEstimado(BigDecimal.ZERO);
        } else if (caso == 7) {
            o.setMoneda(null);
        }
        o.setDetalles(detalles);
        if ((caso == 4 || caso == 5) && o.getTotal() != null) {
            throw new IllegalStateException("El total desconocido no debe sumar solo los importes presentes.");
        }
        if (caso == 6 && (o.getTotal() == null || o.getTotal().signum() != 0)) {
            throw new IllegalStateException("El cero informado debe conservarse.");
        }
        return o;
    }
}
