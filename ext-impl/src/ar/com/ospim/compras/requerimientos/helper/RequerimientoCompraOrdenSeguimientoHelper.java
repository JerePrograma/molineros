package ar.com.ospim.compras.requerimientos.helper;

import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompra;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraOrden;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraOrdenSeguimiento;
import ar.com.ospim.compras.requerimientos.service.RequerimientoCompraOrdenSeguimientoServiceUtil;
import ar.com.ospim.compras.requerimientos.service.RequerimientoCompraOrdenSeguimientoServiceUtil.Transaccion;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/** Adapta cargarSuministro/guardarSuministro del helper de OC y su version propia. */
public class RequerimientoCompraOrdenSeguimientoHelper {
    private static final Log _log = LogFactoryUtil.getLog(RequerimientoCompraOrdenSeguimientoHelper.class);

    public RequerimientoCompraOrdenSeguimiento cargar(RequerimientoCompraOrden orden)
            throws Exception {
        if (orden == null || orden.getIdOrdenCompra() <= 0
                || orden.getRequerimiento() == null
                || !orden.getRequerimiento().isActivo()
                || !orden.getRequerimiento().isOrdenCompra()
                || orden.getIdRequerimientoCompra() <= 0
                || orden.getIdRequerimientoCompra()
                    != orden.getRequerimiento().getIdRequerimientoCompra()) {
            throw new IllegalArgumentException("La Orden de Compra emitida ya no esta disponible.");
        }
        RequerimientoCompraOrdenSeguimiento datos = obtener(orden.getIdOrdenCompra());
        if (datos == null) {
            datos = new RequerimientoCompraOrdenSeguimiento();
            datos.setIdOrdenCompra(orden.getIdOrdenCompra());
        } else {
            validarCargado(datos, orden.getIdOrdenCompra());
        }
        return datos;
    }

    public RequerimientoCompraOrdenSeguimiento guardar(int idRequerimiento,
            int idOrden, int version, Map<String, String> entrada, String usuario)
            throws Exception {
        if (idRequerimiento <= 0 || idOrden <= 0 || version < 0
                || version == Integer.MAX_VALUE) {
            throw new IllegalArgumentException("No se pudo identificar el seguimiento de la OC.");
        }
        String auditoria = texto(usuario, 100, true, "Usuario de auditoria");
        RequerimientoCompraOrdenSeguimiento cambio = prepararEntrada(entrada);
        cambio.setIdOrdenCompra(idOrden);
        cambio.setVersionEdicion(version);
        Transaccion tx = abrirTransaccion();
        try {
            RequerimientoCompra requerimiento = tx.bloquearYObtenerRequerimiento(idRequerimiento);
            if (requerimiento == null || !requerimiento.isActivo()
                    || requerimiento.getIdRequerimientoCompra() != idRequerimiento) {
                throw new IllegalArgumentException("El requerimiento ya no esta disponible.");
            }
            Map<String, Object> sector = requerimiento.getIdSector() == null ? null
                    : tx.obtenerSector(requerimiento.getIdSector().intValue());
            if (sector == null || !Boolean.TRUE.equals(sector.get("activo"))
                    || sector.get("baja") != null) {
                throw new IllegalArgumentException("El sector del requerimiento ya no esta disponible.");
            }
            int estadoOrden = ConfiguracionComprasHelper.resolverEstado("ORDEN_COMPRA",
                    tx.getConfiguracionCompra(ConfiguracionComprasHelper.CONFIG_ESTADOS_IDS));
            RequerimientoCompraOrden orden = tx.obtenerOrden(idRequerimiento);
            if (requerimiento.getEstado() != estadoOrden || orden == null
                    || orden.getIdOrdenCompra() != idOrden
                    || orden.getIdRequerimientoCompra() != idRequerimiento) {
                throw new IllegalArgumentException("La Orden de Compra emitida ya no esta disponible.");
            }
            RequerimientoCompraOrdenSeguimiento actual = tx.obtener(idOrden);
            if (actual != null) validarCargado(actual, idOrden);
            if (version != (actual == null ? 0 : actual.getVersionEdicion())) {
                throw conflicto();
            }
            int filas = actual == null ? tx.insertar(cambio, auditoria)
                    : tx.actualizar(cambio, auditoria);
            if (filas != 1) throw conflicto();
            RequerimientoCompraOrdenSeguimiento guardado = tx.obtener(idOrden);
            if (guardado == null || guardado.getIdOrdenCompra() != idOrden
                    || guardado.getVersionEdicion() != version + 1) {
                throw new IllegalStateException("No se pudo confirmar el guardado del seguimiento de la OC.");
            }
            validarCargado(guardado, idOrden);
            tx.commit();
            return guardado;
        } catch (Exception e) {
            try { tx.rollback(); }
            catch (Exception rollback) { _log.error("No se pudo revertir el seguimiento de la OC.", rollback); }
            throw e;
        } finally { tx.cerrar(); }
    }

    protected RequerimientoCompraOrdenSeguimiento prepararEntrada(Map<String, String> entrada) {
        if (entrada == null) {
            throw new IllegalArgumentException("No se recibieron los datos del seguimiento de la OC.");
        }
        RequerimientoCompraOrdenSeguimiento datos = new RequerimientoCompraOrdenSeguimiento();
        datos.setCalificacionProveedor(opcion(entrada.get("seg_calificacion"),
                new String[] {"APTO", "PENDIENTE_ENTREGA", "NO_RESPONDE"}, "Calificacion del proveedor"));
        datos.setObservacionesProveedor(texto(entrada.get("seg_observaciones"), 4000, false, "Observaciones del proveedor"));
        datos.setFacturaIngresada(opcion(entrada.get("seg_factura_ingresada"),
                new String[] {"SI", "NO"}, "Factura ingresada"));
        datos.setNumeroFactura(texto(entrada.get("seg_numero_factura"), 100, false, "Numero de factura"));
        datos.setFechaFactura(fecha(entrada.get("seg_fecha_factura")));
        datos.setEstadoLiquidacion(opcion(entrada.get("seg_estado_liquidacion"),
                new String[] {"PENDIENTE", "LIQUIDADA", "NO_CORRESPONDE"}, "Estado de liquidacion"));
        String auditada = texto(entrada.get("seg_liquidacion_auditada"), 5, false, "Liquidacion auditada");
        if (auditada.length() > 0 && !"true".equals(auditada) && !"false".equals(auditada)) {
            throw new IllegalArgumentException("La indicacion de liquidacion auditada no es valida.");
        }
        datos.setLiquidacionAuditada("true".equals(auditada));
        datos.setEstadoPago(opcion(entrada.get("seg_estado_pago"),
                new String[] {"PENDIENTE_PAGO", "PAGADO", "NO_CORRESPONDE"}, "Estado de pago"));
        datos.setIdLiquidacion(identificador(entrada.get("seg_id_liquidacion"), "ID de liquidacion"));
        datos.setIdOrdenPago(identificador(entrada.get("seg_id_orden_pago"), "ID de orden de pago"));
        validarAuditoriaLiquidacion(datos);
        return datos;
    }

    private void validarCargado(RequerimientoCompraOrdenSeguimiento datos, int idOrden) {
        if (datos.getIdOrdenCompra() != idOrden || datos.getVersionEdicion() <= 0) {
            throw new IllegalStateException("El seguimiento guardado no corresponde a la OC.");
        }
        opcion(datos.getCalificacionProveedor(), new String[] {"APTO", "PENDIENTE_ENTREGA", "NO_RESPONDE"}, "Calificacion del proveedor");
        texto(datos.getObservacionesProveedor(), 4000, false, "Observaciones del proveedor");
        opcion(datos.getFacturaIngresada(), new String[] {"SI", "NO"}, "Factura ingresada");
        texto(datos.getNumeroFactura(), 100, false, "Numero de factura");
        opcion(datos.getEstadoLiquidacion(), new String[] {"PENDIENTE", "LIQUIDADA", "NO_CORRESPONDE"}, "Estado de liquidacion");
        opcion(datos.getEstadoPago(), new String[] {"PENDIENTE_PAGO", "PAGADO", "NO_CORRESPONDE"}, "Estado de pago");
        if ((datos.getIdLiquidacion() != null && datos.getIdLiquidacion().intValue() <= 0)
                || (datos.getIdOrdenPago() != null && datos.getIdOrdenPago().intValue() <= 0)) {
            throw new IllegalStateException("Las referencias del seguimiento guardado no son validas.");
        }
        validarAuditoriaLiquidacion(datos);
    }

    private void validarAuditoriaLiquidacion(RequerimientoCompraOrdenSeguimiento datos) {
        if (datos.isLiquidacionAuditada() && !"LIQUIDADA".equals(datos.getEstadoLiquidacion())) {
            throw new IllegalArgumentException("Solo se puede marcar como auditada una liquidacion liquidada.");
        }
    }

    private String texto(String entrada, int maximo, boolean obligatorio, String etiqueta) {
        String valor = entrada == null ? "" : entrada.trim();
        if ((obligatorio && valor.length() == 0) || valor.length() > maximo) {
            throw new IllegalArgumentException(etiqueta + (valor.length() == 0
                    ? " es obligatorio." : " excede la longitud permitida."));
        }
        return valor;
    }

    private String opcion(String entrada, String[] permitidas, String etiqueta) {
        String valor = texto(entrada, 25, false, etiqueta);
        if (valor.length() == 0) return valor;
        for (String permitida : permitidas) if (permitida.equals(valor)) return valor;
        throw new IllegalArgumentException(etiqueta + " no es valido.");
    }

    private Integer identificador(String entrada, String etiqueta) {
        String valor = texto(entrada, 10, false, etiqueta);
        if (valor.length() == 0) return null;
        if (valor.matches("[0-9]+")) {
            try {
                int id = Integer.parseInt(valor);
                if (id > 0) return Integer.valueOf(id);
            } catch (NumberFormatException invalido) {
                throw new IllegalArgumentException(etiqueta + " debe ser un entero positivo.");
            }
        }
        throw new IllegalArgumentException(etiqueta + " debe ser un entero positivo.");
    }

    private Date fecha(String entrada) {
        String valor = texto(entrada, 10, false, "Fecha de factura");
        if (valor.length() == 0) return null;
        if (!valor.matches("[0-9]{2}/[0-9]{2}/[0-9]{4}")) {
            throw new IllegalArgumentException("La fecha de factura debe tener formato dd/MM/yyyy.");
        }
        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
        formato.setLenient(false);
        try { return formato.parse(valor); }
        catch (ParseException invalida) { throw new IllegalArgumentException("La fecha de factura no es valida."); }
    }

    private IllegalArgumentException conflicto() {
        return new IllegalArgumentException("El seguimiento fue modificado por otro usuario. Actualice la pantalla.");
    }

    protected RequerimientoCompraOrdenSeguimiento obtener(int idOrden) throws Exception {
        return RequerimientoCompraOrdenSeguimientoServiceUtil.obtener(idOrden);
    }
    protected Transaccion abrirTransaccion() throws Exception {
        return RequerimientoCompraOrdenSeguimientoServiceUtil.abrirTransaccion();
    }
}
