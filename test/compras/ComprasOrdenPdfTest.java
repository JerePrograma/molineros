import ar.com.ospim.compras.requerimientos.beans.*;
import ar.com.ospim.compras.requerimientos.helper.RequerimientoCompraOrdenPdfHelper;
import java.math.BigDecimal;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;
import java.io.FileOutputStream;
import com.lowagie.text.pdf.PdfReader;

public class ComprasOrdenPdfTest {
    public static void main(String[] args) throws Exception {
        for (int caso = 0; caso < 4; caso++) {
            RequerimientoCompraOrden orden = fixture(caso);
            byte[] pdf = new RequerimientoCompraOrdenPdfHelper().generar(orden);
            if (pdf.length < 1000 || pdf[0] != '%' || pdf[1] != 'P') throw new Exception("PDF invalido");
            PdfReader reader = new PdfReader(pdf);
            int paginas;
            try { paginas = reader.getNumberOfPages(); } finally { reader.close(); }
            if (caso < 2 && paginas != 1) throw new Exception("Formato normal no ocupa una pagina");
            if (caso > 1 && paginas <= 1) throw new Exception("Falta paginacion de campos o detalles largos");
            FileOutputStream out = new FileOutputStream(args[0] + "/" + (caso == 0 ? "sistemas" : caso == 1 ? "medica" : caso == 2 ? "multipagina" : "campos-largos") + ".pdf");
            try { out.write(pdf); } finally { out.close(); }
            System.out.println("PDF_OK caso=" + caso + " bytes=" + pdf.length + " paginas=" + paginas);
        }
        RequerimientoCompraOrden invalida = fixture(0);
        invalida.setDetalles(new ArrayList<RequerimientoCompraDetalle>());
        try { new RequerimientoCompraOrdenPdfHelper().generar(invalida); throw new Exception("Acepto detalle vacio"); }
        catch (IllegalArgumentException expected) { System.out.println("DETALLES_VACIOS_RECHAZADOS"); }
        invalida = fixture(0);
        invalida.setMoneda("EUR");
        try { new RequerimientoCompraOrdenPdfHelper().generar(invalida); throw new Exception("Acepto moneda invalida"); }
        catch (IllegalArgumentException expected) { System.out.println("MONEDA_INVALIDA_RECHAZADA"); }
        invalida = fixture(0); invalida.setIdOrdenCompra(0);
        try { new RequerimientoCompraOrdenPdfHelper().generar(invalida); throw new Exception("Acepto borrador"); }
        catch (IllegalArgumentException expected) { System.out.println("BORRADOR_RECHAZADO"); }
    }
    private static RequerimientoCompraOrden fixture(int caso) {
        RequerimientoCompra r = new RequerimientoCompra();
        r.setId(Integer.valueOf(123)); r.setEstado(5);
        r.setSectorDescripcion(caso == 0 ? "SISTEMAS" : "PRESTACIONES MEDICAS");
        r.setRequiereAfiliado(caso > 0); r.setAfiliadoNombre("AFILIADO"); r.setAfiliadoApellido("DE PRUEBA");
        r.setAfiliadoDocumento("Documento de prueba"); r.setAfiliadoLocalidad("Localidad de prueba");
        r.setAfiliadoIdOspim(Integer.valueOf(1234));
        RequerimientoCompraOrden o = new RequerimientoCompraOrden();
        o.setIdOrdenCompra(100 + caso); o.setIdRequerimientoCompra(123); o.setFechaEmision(new Date(1792713600000L));
        o.setRequerimiento(r); o.setMoneda(caso == 0 ? "USD" : "ARS");
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
            d.setDescripcionItem(caso == 0 ? "Equipo y servicios de prueba" : "Prestacion medica de prueba con descripcion extensa para verificar la lectura completa y el crecimiento de la fila de detalle.");
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
        o.setDetalles(detalles);
        return o;
    }
}
