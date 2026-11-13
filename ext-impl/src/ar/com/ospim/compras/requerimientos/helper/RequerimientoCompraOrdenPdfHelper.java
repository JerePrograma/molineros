package ar.com.ospim.compras.requerimientos.helper;

import ar.com.ospim.compras.WebKeysCompras;
import ar.com.ospim.global.WebKeysGlobal;
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
        if (orden == null || orden.getIdOrdenCompra() <= 0) {
            throw new IllegalArgumentException("No hay una orden de compra emitida para imprimir.");
        }
        return generarDocumento(orden, false);
    }

    /** Adapta el reporte como adjunto de autorizacion sin emitir ni guardar una OC. */
    public byte[] generarParaAutorizacion(RequerimientoCompraOrden orden) throws Exception {
        if (orden == null || orden.getIdOrdenCompra() != 0
                || orden.getRequerimiento() == null
                || orden.getIdRequerimientoCompra() != orden.getRequerimiento().getIdRequerimientoCompra()
                || !orden.getRequerimiento().isActivo()
                || !WebKeysCompras.esParaAutorizar(orden.getRequerimiento().getEstado())) {
            throw new IllegalArgumentException("La orden previa no esta disponible para autorizar.");
        }
        return generarDocumento(orden, true);
    }

    private byte[] generarDocumento(RequerimientoCompraOrden orden,
            boolean paraAutorizacion) throws Exception {
        if (orden.getIdRequerimientoCompra() <= 0
                || orden.getFechaEmision() == null
                || orden.getRequerimiento() == null
                || orden.getDetalles() == null || orden.getDetalles().isEmpty()) {
            throw new IllegalArgumentException(
                    "La orden de compra no contiene los datos necesarios para imprimir.");
        }
        if (!RequerimientoCompraOrden.MONEDA_PESOS.equals(orden.getMoneda())) {
            throw new IllegalArgumentException("La orden de compra no tiene una moneda válida.");
        }
        if ((orden.getIva() != null && orden.getIva().signum() < 0)
                || (orden.getIibb() != null && orden.getIibb().signum() < 0)) {
            throw new IllegalArgumentException("La orden de compra contiene impuestos negativos.");
        }
        RequerimientoCompra requerimiento = orden.getRequerimiento();
        SimpleDateFormat fechas = new SimpleDateFormat("dd/MM/yyyy");
        DecimalFormat importes = new DecimalFormat("#,##0.00",
                new DecimalFormatSymbols(new Locale("es", "AR")));
        Map<String, Object> parametros = new HashMap<String, Object>();
        parametros.put("LOGO", getClass().getClassLoader().getResource("jasper/logo_negro.jpg"));
        parametros.put("NUMERO", paraAutorizacion ? "PENDIENTE DE EMISION"
                : new DecimalFormat("00000000").format(orden.getIdOrdenCompra()));
        parametros.put("ESTADO", paraAutorizacion ? "PARA AUTORIZAR - SIN EMITIR" : "EMITIDA");
        parametros.put("FECHA_TIPO", paraAutorizacion ? "Fecha del documento: " : "Fecha de emision: ");
        parametros.put("FECHA", fechas.format(orden.getFechaEmision()));
        parametros.put("REQUERIMIENTO", String.valueOf(orden.getIdRequerimientoCompra()));
        parametros.put("SECTOR", texto(requerimiento.getSectorDescripcionVisible()));
        parametros.put("MONEDA", "Pesos (ARS)");
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
                + "\nDomicilio: " + texto(requerimiento.getAfiliadoDireccionVisible())
                + "\nLocalidad: " + texto(requerimiento.getAfiliadoLocalidadVisible())
                + "\nProvincia: " + texto(requerimiento.getAfiliadoProvinciaVisible())
                + "\nTeléfono: " + texto(requerimiento.getAfiliadoTelefonoVisible())
                + "\nCelular: " + texto(requerimiento.getAfiliadoCelularVisible())
                + "\nCorreo: " + texto(requerimiento.getAfiliadoEmailVisible()));
        parametros.put("DATOS_MEDICOS", "Plan: " + texto(orden.getPlan())
                + "\nReclamo prestacional: " + (orden.getIdReclamoPrestacional() > 0
                        ? String.valueOf(orden.getIdReclamoPrestacional()) : "")
                + "\nFecha orden médica: " + (orden.getFechaOrdenMedica() != null
                        ? fechas.format(orden.getFechaOrdenMedica()) : "")
                + "\nFecha recepción: " + (orden.getFechaRecepcion() != null
                        ? fechas.format(orden.getFechaRecepcion()) : ""));
        parametros.put("ENTREGA", "Fecha estimada de entrega confirmada por el proveedor: "
                + (paraAutorizacion ? "Pendiente de confirmacion"
                        : orden.getFechaEstimadaEntrega() == null ? ""
                                : fechas.format(orden.getFechaEstimadaEntrega()))
                + "\nEntrega: " + texto(orden.getEntrega()));
        parametros.put("OBSERVACIONES", "Observaciones: " + texto(orden.getObservaciones()));
        parametros.put("CONFECCION", "Confeccionado por: " + texto(orden.getConfeccionadoPor())
                + "\nTeléfono de Compras: " + texto(orden.getTelefonoCompras()));
        String cuitOspim = WebKeysGlobal.CUIT_OSPIM;
        parametros.put("ORG_CUIT", cuitOspim.substring(0, 2) + "-"
                + cuitOspim.substring(2, 10) + "-" + cuitOspim.substring(10));
        parametros.put("FECHA_RECEPCION", orden.getFechaRecepcion() == null ? ""
                : fechas.format(orden.getFechaRecepcion()));
        parametros.put("RP_NUMERO", orden.getIdReclamoPrestacional() > 0
                ? String.valueOf(orden.getIdReclamoPrestacional()) : "");
        parametros.put("CONFECCIONADO_POR", texto(orden.getConfeccionadoPor()));
        parametros.put("PROVEEDOR_NOMBRE", texto(orden.getProveedor()));
        parametros.put("PROVEEDOR_CUIT", texto(orden.getProveedorCuit()));
        parametros.put("PROVEEDOR_CONTACTO", texto(orden.getProveedorContacto()));
        parametros.put("PROVEEDOR_TELEFONO", texto(orden.getProveedorTelefono()));
        parametros.put("PROVEEDOR_EMAIL", texto(orden.getProveedorEmail()));
        parametros.put("PROVEEDOR_DOMICILIO", texto(orden.getProveedorDomicilio()));
        parametros.put("FORMA_PAGO", texto(orden.getFormaPago()));
        parametros.put("PLAZO_PAGO", texto(orden.getPlazoPago()));
        parametros.put("CONTACTO_EMISOR", texto(orden.getTelefonoCompras()));
        parametros.put("CONDICION", texto(orden.getCondicion()));
        parametros.put("SOLICITANTE_NOMBRE", texto(orden.getSolicitante()));
        parametros.put("AFILIADO_NOMBRE", texto(requerimiento.getAfiliadoNombreApellidoVisible()));
        String numeroAfiliado = texto(requerimiento.getAfiliadoIdOspimString());
        String documentoAfiliado = texto(requerimiento.getAfiliadoDocumentoVisible());
        parametros.put("AFILIADO_IDENTIFICACION", numeroAfiliado
                + (numeroAfiliado.length() > 0 && documentoAfiliado.length() > 0 ? " / " : "")
                + documentoAfiliado);
        String domicilioAfiliado = texto(requerimiento.getAfiliadoDireccionVisible());
        String localidadAfiliado = texto(requerimiento.getAfiliadoLocalidadVisible());
        parametros.put("AFILIADO_DOMICILIO", domicilioAfiliado
                + (domicilioAfiliado.length() > 0 && localidadAfiliado.length() > 0 ? " - " : "")
                + localidadAfiliado);
        parametros.put("AFILIADO_PLAN", texto(orden.getPlan()));
        String telefonoAfiliado = texto(requerimiento.getAfiliadoTelefonoVisible());
        String celularAfiliado = texto(requerimiento.getAfiliadoCelularVisible());
        parametros.put("AFILIADO_TELEFONOS", telefonoAfiliado
                + (telefonoAfiliado.length() > 0 && celularAfiliado.length() > 0 ? " / " : "")
                + celularAfiliado);
        parametros.put("AFILIADO_PROVINCIA", texto(requerimiento.getAfiliadoProvinciaVisible()));
        parametros.put("AFILIADO_EMAIL", texto(requerimiento.getAfiliadoEmailVisible()));
        parametros.put("FECHA_ORDEN_MEDICA", orden.getFechaOrdenMedica() == null ? ""
                : fechas.format(orden.getFechaOrdenMedica()));
        parametros.put("FECHA_ENTREGA", paraAutorizacion ? "Pendiente de confirmacion"
                : orden.getFechaEstimadaEntrega() == null ? ""
                        : fechas.format(orden.getFechaEstimadaEntrega()));
        parametros.put("ENTREGA_TEXTO", texto(orden.getEntrega()));
        parametros.put("OBSERVACIONES_TEXTO", texto(orden.getObservaciones()));
        List<Map<String, Object>> filas = new ArrayList<Map<String, Object>>();
        BigDecimal subtotal = BigDecimal.ZERO;
        boolean subtotalInformado = true;
        for (RequerimientoCompraDetalle detalle : orden.getDetalles()) {
            if (detalle == null || detalle.getCantidadInt() <= 0) {
                throw new IllegalArgumentException("La orden de compra contiene un detalle incompleto.");
            }
            BigDecimal unitario = detalle.getPrecioUnitarioEstimado();
            BigDecimal totalDetalle = detalle.getPrecioTotalEstimado();
            if ((paraAutorizacion || requerimiento.esSectorSinCotizacionPrestador())
                    && (unitario == null || totalDetalle == null)) {
                throw new IllegalArgumentException(
                        "Debe completar los precios de la Orden de Compra antes de imprimir.");
            }
            if ((unitario == null) != (totalDetalle == null)
                    || (unitario != null && unitario.signum() < 0)
                    || (totalDetalle != null && totalDetalle.signum() < 0)
                    || (unitario != null && totalDetalle != null
                            && totalDetalle.compareTo(unitario.multiply(
                                    new BigDecimal(detalle.getCantidadInt()))) != 0)) {
                throw new IllegalArgumentException("La orden de compra contiene importes de detalle inconsistentes.");
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
            fila.put("unitario", importe(unitario, importes));
            fila.put("importe", importe(totalDetalle, importes));
            filas.add(fila);
            if (totalDetalle == null) {
                subtotalInformado = false;
            } else {
                subtotal = subtotal.add(totalDetalle);
            }
        }
        if (paraAutorizacion && (!subtotalInformado || orden.getTotal() == null
                || orden.getTotal().compareTo(subtotal.add(orden.getIva()).add(orden.getIibb())) != 0)) {
            throw new IllegalArgumentException("La orden previa contiene un total inconsistente.");
        }
        parametros.put("SUBTOTAL", importe(subtotalInformado ? subtotal : null, importes));
        parametros.put("IVA", importe(orden.getIva(), importes));
        parametros.put("IIBB", importe(orden.getIibb(), importes));
        parametros.put("TOTAL", importe(orden.getTotal(), importes));
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

    private String importe(BigDecimal valor, DecimalFormat formato) {
        return valor != null ? formato.format(valor) : "";
    }

    private String texto(String valor) {
        return valor != null && valor.trim().length() > 0 ? valor.trim() : "";
    }
}
