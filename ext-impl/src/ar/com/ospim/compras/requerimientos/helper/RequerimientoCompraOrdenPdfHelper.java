package ar.com.ospim.compras.requerimientos.helper;

import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompra;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraDetalle;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraOrden;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

public class RequerimientoCompraOrdenPdfHelper {

    private static final Log _log = LogFactoryUtil.getLog(
            RequerimientoCompraOrdenPdfHelper.class);
    private static final String REPORTE = "jasper/compras/orden_compra.jrxml";

    public byte[] generar(RequerimientoCompraOrden orden) throws Exception {
        if (orden == null || orden.getIdOrdenCompra() <= 0
                || orden.getIdRequerimientoCompra() <= 0
                || orden.getFechaEmision() == null
                || orden.getRequerimiento() == null
                || orden.getDetalles() == null || orden.getDetalles().isEmpty()) {
            throw new IllegalArgumentException(
                    "La orden de compra no contiene los datos necesarios para imprimir.");
        }
        if (!"ARS".equals(orden.getMoneda()) && !"USD".equals(orden.getMoneda())) {
            throw new IllegalArgumentException("La orden de compra no tiene una moneda válida.");
        }
        RequerimientoCompra requerimiento = orden.getRequerimiento();
        SimpleDateFormat fechas = new SimpleDateFormat("dd/MM/yyyy");
        DecimalFormat importes = new DecimalFormat("#,##0.00",
                new DecimalFormatSymbols(new Locale("es", "AR")));
        Map<String, Object> parametros = new HashMap<String, Object>();
        parametros.put("LOGO", getClass().getClassLoader().getResource("jasper/logo_negro.jpg"));
        parametros.put("NUMERO", String.valueOf(orden.getIdOrdenCompra()));
        parametros.put("FECHA", fechas.format(orden.getFechaEmision()));
        parametros.put("REQUERIMIENTO", String.valueOf(orden.getIdRequerimientoCompra()));
        parametros.put("SECTOR", texto(requerimiento.getSectorDescripcionVisible()));
        parametros.put("MONEDA", orden.getMoneda());
        parametros.put("PROVEEDOR", "Proveedor: " + texto(orden.getProveedor())
                + "\nCUIT: " + texto(orden.getProveedorCuit())
                + "\nDomicilio: " + texto(orden.getProveedorDomicilio()));
        parametros.put("CONTACTO", "Contacto: " + texto(orden.getProveedorContacto())
                + "\nTeléfono: " + texto(orden.getProveedorTelefono())
                + "\nCorreo: " + texto(orden.getProveedorEmail())
                + "\nCondición: " + texto(orden.getCondicion()));
        parametros.put("PAGO", "Forma de pago: " + texto(orden.getFormaPago())
                + "\nPlazo de pago: " + texto(orden.getPlazoPago()));
        parametros.put("SOLICITUD", "Solicitante: " + texto(orden.getSolicitante()));
        parametros.put("ES_MEDICA", Boolean.valueOf(!requerimiento.esSectorSinCotizacionPrestador()));
        parametros.put("AFILIADO", "Afiliado: " + texto(requerimiento.getAfiliadoNombreApellidoVisible())
                + "\nDocumento: " + texto(requerimiento.getAfiliadoDocumentoVisible())
                + "\nN° afiliado: " + texto(requerimiento.getAfiliadoIdOspimString())
                + "\nLocalidad: " + texto(requerimiento.getAfiliadoLocalidadVisible()));
        parametros.put("DATOS_MEDICOS", "Plan: " + texto(orden.getPlan())
                + "\nReclamo prestacional: " + (orden.getIdReclamoPrestacional() > 0
                        ? String.valueOf(orden.getIdReclamoPrestacional()) : "No informado")
                + "\nFecha orden médica: " + (orden.getFechaOrdenMedica() != null
                        ? fechas.format(orden.getFechaOrdenMedica()) : "No informada")
                + "\nFecha recepción: " + (orden.getFechaRecepcion() != null
                        ? fechas.format(orden.getFechaRecepcion()) : "No informada"));
        parametros.put("ENTREGA", "Entrega: " + texto(orden.getEntrega()));
        parametros.put("OBSERVACIONES", "Observaciones: " + texto(orden.getObservaciones()));
        parametros.put("CONFECCION", "Confeccionado por: " + texto(orden.getConfeccionadoPor())
                + "\nTeléfono de Compras: " + texto(orden.getTelefonoCompras()));
        List<Map<String, Object>> filas = new ArrayList<Map<String, Object>>();
        BigDecimal subtotal = BigDecimal.ZERO;
        for (RequerimientoCompraDetalle detalle : orden.getDetalles()) {
            if (detalle == null || detalle.getCantidadInt() <= 0
                    || detalle.getPrecioUnitarioEstimado() == null
                    || detalle.getPrecioUnitarioEstimado().signum() < 0
                    || detalle.getPrecioTotalEstimado() == null) {
                throw new IllegalArgumentException("La orden de compra contiene un detalle incompleto.");
            }
            Map<String, Object> fila = new HashMap<String, Object>();
            String descripcion = texto(detalle.getDescripcionItemVisible());
            if (detalle.getCodigoItemVisible().length() > 0) {
                descripcion = detalle.getCodigoItemVisible() + " - " + descripcion;
            }
            if (detalle.getObservacionesVisible().length() > 0) {
                descripcion += "\n" + detalle.getObservacionesVisible();
            }
            fila.put("cantidad", detalle.getCantidadString());
            fila.put("descripcion", descripcion);
            fila.put("unitario", importes.format(detalle.getPrecioUnitarioEstimado()));
            fila.put("importe", importes.format(detalle.getPrecioTotalEstimado()));
            filas.add(fila);
            subtotal = subtotal.add(detalle.getPrecioTotalEstimado());
        }
        parametros.put("SUBTOTAL", importes.format(subtotal));
        parametros.put("IVA", orden.getIva() != null ? importes.format(orden.getIva()) : "No informado");
        parametros.put("IIBB", orden.getIibb() != null ? importes.format(orden.getIibb()) : "No informado");
        parametros.put("TOTAL", importes.format(orden.getTotal()));
        InputStream in = null;
        try {
            in = getClass().getClassLoader().getResourceAsStream(REPORTE);
            if (in == null) {
                throw new IllegalStateException("No se encontró el reporte de orden de compra.");
            }
            JasperPrint print = JasperFillManager.fillReport(
                    JasperCompileManager.compileReport(JRXmlLoader.load(in)),
                    parametros, new JRMapCollectionDataSource(filas));
            byte[] pdf = JasperExportManager.exportReportToPdf(print);
            if (pdf == null || pdf.length == 0) {
                throw new IllegalStateException("La orden de compra PDF quedó vacía.");
            }
            return pdf;
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    _log.warn("No se pudo cerrar el reporte de orden de compra.", e);
                }
            }
        }
    }

    private String texto(String valor) {
        return valor != null && valor.trim().length() > 0 ? valor.trim() : "No informado";
    }
}
