package ar.com.ospim.compras.requerimientos.helper;

import ar.com.ospim.compras.WebKeysCompras;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompra;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraComparativa;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraComparativaDetalle;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraDetalle;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraOrden;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraPresupuesto;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraReclamoPrestacional;
import ar.com.ospim.compras.requerimientos.service.BusquedaRequerimientoCompraServiceUtil;
import ar.com.ospim.compras.requerimientos.service.RequerimientoCompraComparativaServiceUtil;
import ar.com.ospim.compras.requerimientos.service.RequerimientoCompraOrdenServiceUtil;
import ar.com.ospim.compras.requerimientos.service.RequerimientoCompraOrdenServiceUtil.Transaccion;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Adapta la validacion de adjudicaciones de EditarRequerimientoCompraHelper y
 * la carga de importes/fechas de RequerimientoCompraComparativaHelper.
 * La emision posterior al guardado sigue EditarOrdenesPagoOspimAction.
 */
public class RequerimientoCompraOrdenHelper {

    private static final Log _log = LogFactoryUtil.getLog(RequerimientoCompraOrdenHelper.class);

    public RequerimientoCompraOrden preparar(int id, String nombreUsuario)
            throws Exception {
        RequerimientoCompraOrden orden = prepararParaGuardar(id, nombreUsuario);
        completarDatosConsulta(orden, orden.getRequerimiento());
        return orden;
    }

    /** Prepara los datos propios para validar y guardar sin consultar padrones de presentacion. */
    public RequerimientoCompraOrden prepararParaGuardar(int id, String nombreUsuario)
            throws Exception {
        return prepararParaGuardar(null, id, nombreUsuario);
    }

    private RequerimientoCompraOrden prepararParaGuardar(Transaccion tx,int id,String nombreUsuario)
            throws Exception {
        RequerimientoCompra r = cargarRequerimiento(id,tx);
        RequerimientoCompraOrden existente = tx == null ? obtenerOrden(id) : tx.obtenerOrden(id);
        if (existente != null) {
            if (tx != null && r.getEstado() != estado(tx,"ORDEN_COMPRA")
                    && r.getEstado() != estado(tx,"AUTORIZADO")) throw new IllegalArgumentException("La Orden de Compra no esta disponible en el estado vigente.");
            if (tx != null && tx.obtenerRequerimientoOrigenOrden(existente.getIdOrdenCompra()) != id)
                throw new IllegalStateException("La cotizacion de origen no corresponde al requerimiento.");
            existente.setRequerimiento(r);
            return existente;
        }
        if (tx == null ? !WebKeysCompras.esAutorizado(r.getEstado()) : r.getEstado() != estado(tx,"AUTORIZADO")) {
            throw new IllegalArgumentException(
                    "El requerimiento debe estar AUTORIZADO para Crear Orden de Compra.");
        }
        validarDetalles(r, esEmpresa(r,tx), tx);
        RequerimientoCompraOrden orden = new RequerimientoCompraOrden();
        orden.setIdRequerimientoCompra(id);
        orden.setRequerimiento(r);
        orden.setFechaEmision(tx == null ? new Date() : tx.getFechaActual());
        orden.setIva(BigDecimal.ZERO);
        orden.setIibb(BigDecimal.ZERO);
        orden.setConfeccionadoPor(texto(nombreUsuario, 250, false, "Confeccionado por"));
        orden.setSolicitante(texto(r.getAltaUsr(), 250, false, "Solicitante"));
        orden.setObservaciones(texto(r.getObservaciones(), 4000, false, "Observaciones"));
        if (esEmpresa(r,tx)) {
            prepararEmpresa(r, orden, tx);
        } else {
            prepararPrestador(r, orden, tx);
        }
        return orden;
    }

    public RequerimientoCompraOrden crear(int id, Map<String, String> entrada,
            String usuario) throws Exception {
        Transaccion tx = abrirTransaccion();
        try {
            RequerimientoCompraOrden resultado = crear(tx,id,entrada,usuario,false);
            tx.commit(); return resultado;
        } catch (Exception e) { revertir(tx,e); throw e; }
        finally { tx.cerrar(); }
    }

    private RequerimientoCompraOrden crear(Transaccion tx,int id,Map<String,String> entrada,String usuario,boolean directa) throws Exception {
        String auditoria=texto(usuario,100,true,"Usuario de auditoria");
        RequerimientoCompraOrden orden = prepararParaGuardar(tx,id,usuario);
        if (orden.getIdOrdenCompra() > 0) {
            if(orden.getRequerimiento().getEstado()==estado(tx,"AUTORIZADO")) {
                RequerimientoCompra r=orden.getRequerimiento();boolean empresa=esEmpresa(r,tx);
                validarDetalles(r,empresa,tx);
                RequerimientoCompraOrden preparacion=new RequerimientoCompraOrden();
                if(empresa)prepararEmpresa(r,preparacion,tx);else prepararPrestador(r,preparacion,tx);
                if(resolverPresupuestoOrigen(tx,r,empresa)!=tx.obtenerPresupuestoOrigenOrden(orden.getIdOrdenCompra()))
                    throw new IllegalStateException("La cotizacion de origen no corresponde a la adjudicacion vigente.");
                validarCobertura(r,orden);
                if(tx.confirmarEstado(id,estado(tx,"AUTORIZADO"),estado(tx,"ORDEN_COMPRA"),auditoria)!=1)
                    throw new IllegalStateException("El requerimiento fue modificado por otro proceso.");
            }
            return orden;
        }
        if (esEmpresa(orden.getRequerimiento(),tx) && (!directa || entrada != null)) {
            if (entrada == null) {
                throw new IllegalArgumentException("No se recibieron los datos de la Orden de Compra.");
            }
            completarPrecios(orden, entrada,tx);
            if (entrada.containsKey("iva")) {
                orden.setIva(importeEntrada(entrada.get("iva"), false, "IVA",tx));
            }
            if (entrada.containsKey("iibb")) {
                orden.setIibb(importeEntrada(entrada.get("iibb"), false, "IIBB",tx));
            }
            if (entrada.containsKey("condicion")) {
                orden.setCondicion(texto(entrada.get("condicion"), 1000, false, "Condicion"));
            }
            if (entrada.containsKey("forma_pago")) {
                orden.setFormaPago(texto(entrada.get("forma_pago"), 250, false, "Forma de pago"));
            }
            if (entrada.containsKey("plazo_pago")) {
                orden.setPlazoPago(texto(entrada.get("plazo_pago"), 250, false, "Plazo de pago"));
            }
            if (entrada.containsKey("entrega")) {
                orden.setEntrega(texto(entrada.get("entrega"), 1000, false, "Entrega"));
            }
            if (entrada.containsKey("observaciones")) {
                orden.setObservaciones(texto(entrada.get("observaciones"), 4000, false, "Observaciones"));
            }
        }
        return emitir(tx, orden, usuario);
    }

    public RequerimientoCompraOrden crearDirecta(int id, String nombreUsuario,
            String usuario) throws Exception {
        texto(nombreUsuario,250,false,"Confeccionado por");
        Transaccion tx=abrirTransaccion();
        try{RequerimientoCompraOrden resultado=crear(tx,id,null,usuario,true);tx.commit();return resultado;}
        catch(Exception e){revertir(tx,e);throw e;}finally{tx.cerrar();}
    }

    public boolean requiereCompletarDatos(RequerimientoCompraOrden orden) {
        return requiereCompletarDatos(orden,orden != null && orden.getRequerimiento() != null && orden.getRequerimiento().esSectorSinCotizacionPrestador());
    }

    private boolean requiereCompletarDatos(RequerimientoCompraOrden orden,boolean empresa) {
        if (orden != null && empresa) {
            for (RequerimientoCompraDetalle d : orden.getDetalles()) {
                if (d == null || d.getPrecioUnitarioEstimado() == null) {
                    return true;
                }
            }
        }
        return false;
    }

    private RequerimientoCompraOrden emitir(Transaccion tx,RequerimientoCompraOrden orden,
            String usuario) throws Exception {
        String auditoria = texto(usuario, 100, true, "Usuario de auditoria");
        if (orden.getIdOrdenCompra() > 0) {
            return orden;
        }
        if (requiereCompletarDatos(orden,esEmpresa(orden.getRequerimiento(),tx))) {
            throw new IllegalArgumentException(
                    "Debe completar los precios unitarios de la Orden de Compra antes de emitirla.");
        }
        orden.setConfeccionadoPor(auditoria);
        validarImportes(orden,tx);
        orden.setAltaUsr(auditoria);
        int presupuesto = resolverPresupuestoOrigen(tx,orden.getRequerimiento(),esEmpresa(orden.getRequerimiento(),tx));
        tx.insertar(orden,presupuesto,auditoria);
        RequerimientoCompraOrden guardada = tx.obtenerOrden(orden.getIdRequerimientoCompra());
        validarCobertura(orden.getRequerimiento(),guardada);
        if (tx.confirmarEstado(orden.getIdRequerimientoCompra(),estado(tx,"AUTORIZADO"),estado(tx,"ORDEN_COMPRA"),auditoria) != 1)
            throw new IllegalStateException("El requerimiento fue modificado por otro proceso.");
        if (guardada == null || guardada.getIdOrdenCompra() <= 0
                || guardada.getIdRequerimientoCompra() != orden.getIdRequerimientoCompra()) {
            throw new IllegalStateException("No se pudo confirmar la Orden de Compra.");
        }
        guardada.setRequerimiento(orden.getRequerimiento());
        return guardada;
    }

    /** Conserva importes aprobados; completa precios ausentes y edita condiciones. */
    public RequerimientoCompraOrden actualizar(int idRequerimiento, int idOrden,
            int version, Map<String, String> entrada, String usuario) throws Exception {
        Transaccion tx=abrirTransaccion();
        try { RequerimientoCompraOrden resultado=actualizar(tx,idRequerimiento,idOrden,version,entrada,usuario);tx.commit();return resultado;
        }catch(Exception e){revertir(tx,e);throw e;}finally{tx.cerrar();}
    }

    private RequerimientoCompraOrden actualizar(Transaccion tx,int idRequerimiento,int idOrden,int version,Map<String,String> entrada,String usuario) throws Exception {
        String auditoria = texto(usuario, 100, true, "Usuario de auditoria");
        RequerimientoCompraOrden actual = prepararParaGuardar(tx,idRequerimiento, usuario);
        if (actual.getRequerimiento().getEstado() != estado(tx,"ORDEN_COMPRA")
                || actual.getIdOrdenCompra() <= 0 || actual.getIdOrdenCompra() != idOrden
                || actual.getIdRequerimientoCompra() != idRequerimiento) {
            throw new IllegalArgumentException("La Orden de Compra emitida ya no esta disponible.");
        }
        if (version < 0 || version == Integer.MAX_VALUE
                || version != actual.getVersionEdicion()) {
            throw new IllegalArgumentException(
                    "La Orden de Compra fue modificada por otro usuario. Actualice la pantalla.");
        }
        if (entrada == null) {
            throw new IllegalArgumentException("No se recibieron las condiciones de la Orden de Compra.");
        }
        RequerimientoCompraOrden cambio = new RequerimientoCompraOrden();
        cambio.setIdOrdenCompra(idOrden);
        cambio.setIdRequerimientoCompra(idRequerimiento);
        cambio.setVersionEdicion(version);
        cambio.setCondicion(texto(entrada.get("condicion"), 1000, false, "Condicion"));
        cambio.setFormaPago(texto(entrada.get("forma_pago"), 250, false, "Forma de pago"));
        cambio.setPlazoPago(texto(entrada.get("plazo_pago"), 250, false, "Plazo de pago"));
        cambio.setEntrega(texto(entrada.get("entrega"), 1000, false, "Entrega"));
        cambio.setObservaciones(texto(entrada.get("observaciones"), 4000, false, "Observaciones"));
        if (esEmpresa(actual.getRequerimiento(),tx)) {
            RequerimientoCompraOrden completa = new RequerimientoCompraOrden();
            completa.setRequerimiento(actual.getRequerimiento());
            completa.setIva(actual.getIva());
            completa.setIibb(actual.getIibb());
            for (RequerimientoCompraDetalle original : actual.getDetalles()) {
                RequerimientoCompraDetalle copia = copiarDetalle(original);
                copia.setPrecioUnitarioEstimado(original.getPrecioUnitarioEstimado());
                completa.getDetalles().add(copia);
            }
            completarPrecios(completa, entrada,tx);
            validarImportes(completa,tx);
            for (int i = 0; i < actual.getDetalles().size(); i++) {
                if (actual.getDetalles().get(i).getPrecioUnitarioEstimado() == null) {
                    cambio.getDetalles().add(completa.getDetalles().get(i));
                }
            }
        }
        if(tx.actualizar(cambio,auditoria)!=1)throw new IllegalArgumentException("La Orden de Compra fue modificada por otro usuario. Actualice la pantalla.");
        for(RequerimientoCompraDetalle d:cambio.getDetalles()) {
            if(tx.completarPrecio(idOrden,d.getIdInt(),d.getPrecioUnitarioEstimado())!=1)
                throw new IllegalArgumentException("El precio pendiente fue modificado. Actualice la pantalla.");
        }
        RequerimientoCompraOrden guardada = tx.obtenerOrden(idRequerimiento);
        if (guardada == null || guardada.getIdOrdenCompra() != idOrden
                || guardada.getIdRequerimientoCompra() != idRequerimiento
                || guardada.getVersionEdicion() != version + 1) {
            throw new IllegalStateException("No se pudo confirmar la modificacion de la Orden de Compra.");
        }
        guardada.setRequerimiento(actual.getRequerimiento());
        return guardada;
    }

    private RequerimientoCompra cargarRequerimiento(int id,Transaccion tx) throws Exception {
        if (id <= 0) {
            throw new IllegalArgumentException("Debe informar el requerimiento de compra.");
        }
        RequerimientoCompra r = tx == null ? obtenerRequerimiento(id) : tx.bloquearYObtenerRequerimiento(id);
        if (r == null || r.getIdRequerimientoCompra() != id || !r.isActivo()) {
            throw new IllegalArgumentException("El requerimiento ya no esta disponible.");
        }
        if (tx == null) r.setDetalles(obtenerDetalles(id));
        else {
            Map<String,Object> sector= r.getIdSector()==null?null:tx.obtenerSector(r.getIdSector().intValue());
            if(sector==null || !Boolean.TRUE.equals(sector.get("activo")) || sector.get("baja")!=null)
                throw new IllegalArgumentException("El sector del requerimiento ya no esta disponible.");
        }
        return r;
    }

    private void validarDetalles(RequerimientoCompra r,boolean empresa,Transaccion tx) throws Exception {
        if (!r.tieneDetalles()) {
            throw new IllegalArgumentException("El requerimiento no tiene detalles activos.");
        }
        Set<Integer> ids = new HashSet<Integer>();
        for (RequerimientoCompraDetalle d : r.getDetalles()) {
            if (d == null || d.getIdInt() <= 0
                    || d.getIdRequerimientoCompra() != r.getIdRequerimientoCompra()
                    || !ids.add(d.getId()) || d.getCantidadInt() <= 0
                    || (d.getDescripcionItemVisible().trim().length() == 0
                        && !(empresa
                            && d.esObservacion()
                            && (tx == null || ConfiguracionComprasHelper.resolverSectorDetalleObservacion(r.getSectorDescripcion(),tx.getConfiguracionCompra("COMPRAS_SECTORES_DETALLE_OBSERVACION")))
                            && d.getObservacionesVisible().trim().length() > 0))) {
                throw new IllegalArgumentException(
                        "Los detalles del requerimiento estan incompletos. Actualice la pantalla.");
            }
        }
    }

    private void prepararEmpresa(RequerimientoCompra r,
            RequerimientoCompraOrden orden,Transaccion tx) throws Exception {
        RequerimientoCompraPresupuesto adjudicada = null;
        List<RequerimientoCompraPresupuesto> cotizaciones =
                tx == null ? obtenerCotizacionesEmpresa(r.getIdRequerimientoCompra()) : tx.listarDocumentos(r.getIdRequerimientoCompra(),RequerimientoCompraPresupuesto.TIPO_DOCUMENTO_COTIZACION_EMPRESA);
        for (int i = 0; cotizaciones != null && i < cotizaciones.size(); i++) {
            RequerimientoCompraPresupuesto c = cotizaciones.get(i);
            if (c != null && c.isActivo() && c.isCotizacionEmpresa()
                    && c.isEmpresaAdjudicada()) {
                if (adjudicada != null) {
                    throw new IllegalArgumentException(
                            "Debe existir exactamente una Empresa adjudicada.");
                }
                adjudicada = c;
            }
        }
        if (adjudicada == null || adjudicada.getIdRequerimiento() == null
                || adjudicada.getIdRequerimiento().intValue()
                != r.getIdRequerimientoCompra()) {
            throw new IllegalArgumentException(
                    "Debe seleccionar una Empresa adjudicada con cotizacion activa del requerimiento.");
        }
        if(tx==null)new PresupuestoCompraHelper().validarDatosEmpresaCotizacion(adjudicada.getEmpresaCuit(),adjudicada.getEmpresaSucursal(),adjudicada.getDescripcionEmpresa(),1);
        else validarEmpresa(tx,adjudicada);
        orden.setProveedor(texto(adjudicada.getDescripcionEmpresa(), 250, true, "Proveedor"));
        orden.setProveedorCuit(texto(adjudicada.getEmpresaCuit(), 20, false, "CUIT proveedor"));
        List<RequerimientoCompraDetalle> detalles = new ArrayList<RequerimientoCompraDetalle>();
        for (RequerimientoCompraDetalle original : r.getDetalles()) {
            RequerimientoCompraDetalle d = copiarDetalle(original);
            if (original.getPrecioUnitarioEstimado() != null
                    && original.getPrecioTotalEstimado() != null
                    && original.getPrecioUnitarioEstimado().multiply(
                            new BigDecimal(original.getCantidadInt())).compareTo(
                                    original.getPrecioTotalEstimado()) != 0) {
                throw new IllegalArgumentException(
                        "El subtotal del requerimiento no coincide con la cantidad y el precio unitario.");
            }
            d.setPrecioUnitarioEstimado(original.getPrecioUnitarioEstimado());
            detalles.add(d);
        }
        orden.setDetalles(detalles);
    }

    private void prepararPrestador(RequerimientoCompra r,
            RequerimientoCompraOrden orden,Transaccion tx) throws Exception {
        if (!r.tieneCotizacionCompletaConPrestadorUnico()) {
            throw new IllegalArgumentException(
                    "Todos los detalles deben tener una cotizacion completa con el mismo prestador adjudicado.");
        }
        RequerimientoCompraReclamoPrestacional relacion = tx==null?obtenerRelacionRp(r.getIdRequerimientoCompra()):null;
        Map<String,Object> crudo = tx==null?null:tx.obtenerRelacionRp(r.getIdRequerimientoCompra());
        if (tx==null ? (relacion==null || !relacion.isVinculado() || relacion.getIdRequerimientoCompra()!=r.getIdRequerimientoCompra())
                : (crudo==null || !"VINCULADO".equals(crudo.get("estado")) || ((Integer)crudo.get("id")).intValue()<=0 || ((Integer)crudo.get("rp_id")).intValue()<=0 || crudo.get("baja")!=null)) {
            throw new IllegalArgumentException(
                    "El requerimiento debe tener un Reclamo Prestacional vinculado.");
        }
        orden.setIdReclamoPrestacional(tx==null?relacion.getIdReclamoPrestacionalInt():((Integer)crudo.get("id")).intValue());
        if(tx!=null)validarCotizacionActual(tx,r);
        List<RequerimientoCompraComparativa> comparativas = tx==null?obtenerComparativas(r.getIdRequerimientoCompra(),r.getIdPrestadorAdjudicadoInt()):tx.listarComparativas(r.getIdRequerimientoCompra(),r.getIdPrestadorAdjudicadoInt());
        if (comparativas == null || comparativas.size() != 1) {
            throw new IllegalArgumentException(
                    "Debe guardar la comparativa completa del prestador adjudicado.");
        }
        RequerimientoCompraComparativa c = comparativas.get(0);
        if (c == null || c.getIdComparativa() <= 0
                || c.getIdRequerimiento() != r.getIdRequerimientoCompra()
                || c.getIdPrestador() != r.getIdPrestadorAdjudicadoInt()
                || c.getDetalles() == null) {
            throw new IllegalArgumentException("La comparativa adjudicada no es valida.");
        }
        Map<Integer, RequerimientoCompraComparativaDetalle> precios =
                new HashMap<Integer, RequerimientoCompraComparativaDetalle>();
        for (RequerimientoCompraComparativaDetalle d : c.getDetalles()) {
            if (d == null || d.getIdDetalle() <= 0 || d.getIdPrestacion() <= 0
                    || precios.put(Integer.valueOf(d.getIdPrestacion()), d) != null) {
                throw new IllegalArgumentException("La comparativa adjudicada tiene detalles invalidos.");
            }
        }
        List<RequerimientoCompraDetalle> detalles = new ArrayList<RequerimientoCompraDetalle>();
        String proveedor = null;
        String cuit = null;
        for (RequerimientoCompraDetalle original : r.getDetalles()) {
            String nombreActual = texto(original.getPrestadorRazonSocial(), 250, true, "Proveedor");
            String cuitActual = texto(original.getPrestadorCuit(), 20, false, "CUIT proveedor");
            if (proveedor != null && (!proveedor.equals(nombreActual) || !cuit.equals(cuitActual))) {
                throw new IllegalArgumentException("Los datos del prestador adjudicado no coinciden.");
            }
            proveedor = nombreActual;
            cuit = cuitActual;
            RequerimientoCompraComparativaDetalle cotizado =
                    precios.remove(Integer.valueOf(original.getIdPrestacionInt()));
            if (cotizado == null || cotizado.getCantidad() == null
                    || cotizado.getCantidad().intValue() <= 0) {
                throw new IllegalArgumentException(
                        "La comparativa adjudicada debe cotizar todos los detalles del requerimiento.");
            }
            if (cotizado.getImporteUnitario() == null) {
                throw new IllegalArgumentException(
                        "La comparativa adjudicada debe informar todos los precios unitarios.");
            }
            validarImporte(cotizado.getImporteUnitario(), "Precio unitario adjudicado",tx);
            RequerimientoCompraDetalle d = copiarDetalle(original);
            d.setCantidad(cotizado.getCantidad());
            d.setPrecioUnitarioEstimado(cotizado.getImporteUnitario());
            detalles.add(d);
        }
        if (!precios.isEmpty()) {
            throw new IllegalArgumentException(
                    "La comparativa contiene prestaciones ajenas al requerimiento.");
        }
        orden.setProveedor(proveedor);
        orden.setProveedorCuit(cuit);
        orden.setDetalles(detalles);
        orden.setIva(impuesto(c.getIva(), "IVA adjudicado",tx));
        orden.setIibb(impuesto(c.getIibb(), "IIBB adjudicado",tx));
        orden.setFormaPago(c.getFormaPago() == null ? null : c.getFormaPago() + " dias");
        orden.setPlazoPago(orden.getFormaPago());
        orden.setEntrega(unir(c.getPlazoEntrega(), c.getEnvio()));
        orden.setCondicion(c.getValidezPresupuesto() == null ? null
                : "Validez del presupuesto: " + c.getValidezPresupuesto() + " horas");
        orden.setFechaOrdenMedica(fechaOrdenMedica(r.getIdRequerimientoCompra(),tx));
        validarImportes(orden,tx);
    }

    private RequerimientoCompraDetalle copiarDetalle(RequerimientoCompraDetalle original) {
        RequerimientoCompraDetalle d = new RequerimientoCompraDetalle(original.getId());
        d.setIdRequerimientoCompra(original.getIdRequerimientoCompra());
        d.setTipoItem(original.getTipoItem());
        d.setIdTipoPrestacion(original.getIdTipoPrestacion());
        d.setIdPrestacion(original.getIdPrestacion());
        d.setCodigoItem(original.getCodigoItemVisible());
        String descripcion = original.getDescripcionItemVisible();
        if (original.getObservacionesVisible().length() > 0) {
            descripcion += "\n" + original.getObservacionesVisible();
        }
        d.setDescripcionItem(descripcion);
        d.setCantidad(original.getCantidad());
        d.setIdPrestador(original.getIdPrestador());
        d.setPrestadorCuit(original.getPrestadorCuit());
        d.setPrestadorRazonSocial(original.getPrestadorRazonSocial());
        return d;
    }

    private void completarPrecios(RequerimientoCompraOrden orden,
            Map<String, String> entrada,Transaccion tx) throws Exception {
        Set<String> campos = new HashSet<String>();
        for (RequerimientoCompraDetalle d : orden.getDetalles()) {
            String campo = "precio_" + d.getIdInt();
            campos.add(campo);
            if (d.getPrecioUnitarioEstimado() == null) {
                d.setPrecioUnitarioEstimado(importeEntrada(entrada.get(campo), true,
                        "Precio unitario del detalle " + d.getIdInt(),tx));
            }
        }
        for (String campo : entrada.keySet()) {
            if (campo != null && campo.startsWith("precio_") && !campos.contains(campo)) {
                throw new IllegalArgumentException(
                        "Los detalles de la Orden de Compra cambiaron. Actualice la pantalla.");
            }
        }
    }

    private BigDecimal importeEntrada(String valor, boolean obligatorio, String campo,Transaccion tx) throws Exception {
        String s = valor == null ? "" : valor.trim();
        if (s.length() == 0 && !obligatorio) {
            return BigDecimal.ZERO;
        }
        if (s.length() > 18 || !s.matches("[0-9]+([.,][0-9]{1,2})?")) {
            throw new IllegalArgumentException(campo
                    + ": use un importe sin miles y hasta dos decimales.");
        }
        BigDecimal importe = new BigDecimal(s.replace(',', '.'));
        validarImporte(importe, campo,tx);
        return importe.setScale(2);
    }

    private void validarImportes(RequerimientoCompraOrden orden,Transaccion tx) throws Exception {
        if (orden.getDetalles() == null || orden.getDetalles().isEmpty()) {
            throw new IllegalArgumentException("La Orden de Compra no tiene detalles.");
        }
        for (RequerimientoCompraDetalle d : orden.getDetalles()) {
            if (d.getCantidadInt() <= 0) {
                throw new IllegalArgumentException("La cantidad debe ser mayor a cero.");
            }
            validarImporte(d.getPrecioUnitarioEstimado(), "Precio unitario",tx);
            validarImporte(d.getPrecioTotalEstimado(), "Subtotal",tx);
            if (d.getPrecioUnitarioEstimado() != null) {
                validarImporte(d.getPrecioUnitarioEstimado().multiply(
                        new BigDecimal(d.getCantidadInt())), "Subtotal calculado",tx);
            }
        }
        validarImporte(orden.getIva(), "IVA",tx);
        validarImporte(orden.getIibb(), "IIBB",tx);
        validarImporte(orden.getTotal(), "Total",tx);
    }

    private BigDecimal impuesto(BigDecimal valor, String campo,Transaccion tx) throws Exception {
        validarImporte(valor, campo,tx);
        return valor;
    }

    private void validarImporte(BigDecimal valor, String campo,Transaccion tx) throws Exception {
        BigDecimal maximo=tx==null?ConfiguracionComprasHelper.getMaxImporteOrdenCompra():ConfiguracionComprasHelper.resolverMaxImporteOrdenCompra(tx.getConfiguracionCompra(ConfiguracionComprasHelper.CONFIG_OC_MAX_IMPORTE));
        if (valor != null && (valor.scale() > 2
                || valor.compareTo(maximo) > 0
                || valor.compareTo(BigDecimal.ZERO) < 0)) {
            throw new IllegalArgumentException(campo + ": importe fuera de rango.");
        }
    }

    private String texto(String valor, int maximo, boolean obligatorio, String campo) {
        String s = valor == null ? "" : valor.trim();
        if (s.length() > maximo) {
            throw new IllegalArgumentException(campo + ": admite hasta " + maximo + " caracteres.");
        }
        if (obligatorio && s.length() == 0) {
            throw new IllegalArgumentException("Debe informar " + campo + ".");
        }
        return s;
    }

    private Date fechaOrdenMedica(int id,Transaccion tx) throws Exception {
        Date fecha = null;
        List<RequerimientoCompraPresupuesto> documentos = tx==null?obtenerOrdenesMedicas(id):tx.listarOrdenesMedicas(id);
        for (int i = 0; documentos != null && i < documentos.size(); i++) {
            RequerimientoCompraPresupuesto d = documentos.get(i);
            if (d != null && d.isActivo() && d.getIdRequerimiento() != null
                    && d.getIdRequerimiento().intValue() == id && d.getFechaDocumento() != null) {
                if (fecha != null && !fecha.equals(d.getFechaDocumento())) {
                    return null;
                }
                fecha = d.getFechaDocumento();
            }
        }
        return fecha;
    }

    private String unir(String a, String b) {
        String primero = texto(a, 1000, false, "Entrega");
        String segundo = texto(b, 1000, false, "Envio");
        String entrega = primero + (primero.length() > 0 && segundo.length() > 0 ? " - " : "") + segundo;
        return entrega.length() > 0 ? entrega : null;
    }

    private int estado(Transaccion tx,String codigo)throws Exception {
        return ConfiguracionComprasHelper.resolverEstado(codigo,tx.getConfiguracionCompra(ConfiguracionComprasHelper.CONFIG_ESTADOS_IDS));
    }
    private boolean esEmpresa(RequerimientoCompra r,Transaccion tx)throws Exception {
        return tx==null?r.esSectorSinCotizacionPrestador():ConfiguracionComprasHelper.resolverSectorSinCotizacionPrestador(r.getSectorDescripcion(),tx.getConfiguracionCompra("COMPRAS_SECTORES_SIN_COTIZACION_PRESTADOR"));
    }
    private void validarEmpresa(Transaccion tx,RequerimientoCompraPresupuesto p)throws Exception {
        String sucursal=WebKeysCompras.trimToNull(p.getEmpresaSucursal());
        new PresupuestoCompraHelper().validarDatosEmpresaCotizacion(p.getEmpresaCuit(),p.getEmpresaSucursal(),p.getDescripcionEmpresa(),1,
                sucursal==null?null:tx.buscarEmpresa(WebKeysCompras.trimToNull(p.getEmpresaCuit()),sucursal));
    }
    public int obtenerPresupuestoOrigenConsulta(int id)throws Exception {
        Transaccion tx=abrirTransaccion();
        try{RequerimientoCompra r=cargarRequerimiento(id,tx);int presupuesto=resolverPresupuestoOrigen(tx,r,esEmpresa(r,tx));tx.commit();return presupuesto;}
        catch(Exception e){revertir(tx,e);throw e;}finally{tx.cerrar();}
    }
    private int resolverPresupuestoOrigen(Transaccion tx,RequerimientoCompra r,boolean empresa)throws Exception {
        int tipo=empresa?RequerimientoCompraPresupuesto.TIPO_DOCUMENTO_COTIZACION_EMPRESA:RequerimientoCompraPresupuesto.TIPO_DOCUMENTO_PRESUPUESTO;
        RequerimientoCompraPresupuesto elegido=null;
        for(RequerimientoCompraPresupuesto p:tx.listarDocumentos(r.getIdRequerimientoCompra(),tipo)) {
            if(p==null || !p.isActivo() || p.getIdRequerimiento()==null || p.getIdRequerimiento().intValue()!=r.getIdRequerimientoCompra())continue;
            if(empresa?!p.isEmpresaAdjudicada():(p.getIdPrestador()==null || p.getIdPrestador().intValue()!=r.getIdPrestadorAdjudicadoInt()))continue;
            if(elegido!=null)throw new IllegalArgumentException("Debe existir una cotizacion adjudicada unica del requerimiento.");elegido=p;
        }
        if(elegido==null || elegido.getIdRequerimientoPresupuesto()==null || elegido.getIdRequerimientoPresupuesto().intValue()<=0)
            throw new IllegalArgumentException("Debe existir una cotizacion adjudicada unica del requerimiento.");
        if(empresa)validarEmpresa(tx,elegido);
        return elegido.getIdRequerimientoPresupuesto().intValue();
    }
    private void validarCotizacionActual(Transaccion tx,RequerimientoCompra r)throws Exception {
        resolverPresupuestoOrigen(tx,r,false);
        for(RequerimientoCompraDetalle d:r.getDetalles()) {
            BigDecimal unitario=d.getPrecioUnitarioEstimado();BigDecimal total=d.getPrecioTotalEstimadoInformado();
            if(d.getIdPrestadorInt()<=0 || d.getIdPrestadorInt()!=r.getIdPrestadorAdjudicadoInt() || d.getCantidadInt()<=0 || unitario==null || total==null
                    || unitario.signum()<0 || total.signum()<0 || total.compareTo(unitario.multiply(new BigDecimal(d.getCantidadInt())).setScale(2,BigDecimal.ROUND_HALF_UP))!=0)
                throw new IllegalArgumentException("La Orden de Compra requiere un prestador unico y detalles cotizados completos.");
        }
    }
    private void validarCobertura(RequerimientoCompra r,RequerimientoCompraOrden orden) {
        if(orden==null || orden.getDetalles()==null || orden.getDetalles().isEmpty())throw new IllegalStateException("La Orden de Compra no tiene detalles.");
        Set<Integer> ids=new HashSet<Integer>();for(RequerimientoCompraDetalle d:r.getDetalles())ids.add(d.getId());
        for(RequerimientoCompraDetalle d:orden.getDetalles())if(d==null || !ids.remove(d.getId()) || d.getDescripcionItemVisible().trim().length()==0)
            throw new IllegalStateException("El detalle de la Orden de Compra no coincide con el requerimiento.");
        if(!ids.isEmpty())throw new IllegalStateException("La Orden de Compra no conserva todos los detalles del requerimiento.");
    }
    protected Transaccion abrirTransaccion()throws Exception{return RequerimientoCompraOrdenServiceUtil.abrirTransaccion();}
    private void revertir(Transaccion tx,Exception original) {
        try{tx.rollback();}catch(Exception e){_log.error("No se pudo revertir la Orden de Compra.",e);}
    }
    public RequerimientoCompraOrden crear(RequerimientoCompraOrden entrada,String usuario)throws Exception {
        if(entrada==null)throw new IllegalArgumentException("No se recibio la Orden de Compra.");
        return crear(entrada.getIdRequerimientoCompra(),entradaOrden(entrada),usuario);
    }
    public RequerimientoCompraOrden actualizar(RequerimientoCompraOrden entrada,String usuario)throws Exception {
        if(entrada==null)throw new IllegalArgumentException("No se recibio la Orden de Compra.");
        return actualizar(entrada.getIdRequerimientoCompra(),entrada.getIdOrdenCompra(),entrada.getVersionEdicion(),entradaOrden(entrada),usuario);
    }
    private Map<String,String> entradaOrden(RequerimientoCompraOrden orden) {
        Map<String,String> f=new HashMap<String,String>();f.put("condicion",orden.getCondicion());f.put("forma_pago",orden.getFormaPago());f.put("plazo_pago",orden.getPlazoPago());f.put("entrega",orden.getEntrega());f.put("observaciones",orden.getObservaciones());
        if(orden.getIva()!=null)f.put("iva",orden.getIva().toPlainString());if(orden.getIibb()!=null)f.put("iibb",orden.getIibb().toPlainString());
        if(orden.getDetalles()!=null)for(RequerimientoCompraDetalle d:orden.getDetalles())if(d!=null && d.getPrecioUnitarioEstimado()!=null)f.put("precio_"+d.getIdInt(),d.getPrecioUnitarioEstimado().toPlainString());return f;
    }

    protected void completarDatosConsulta(RequerimientoCompraOrden orden,
            RequerimientoCompra requerimiento) throws Exception {
        RequerimientoCompraOrdenServiceUtil.completarDatosConsulta(orden, requerimiento);
    }

    protected RequerimientoCompra obtenerRequerimiento(int id) throws Exception {
        return BusquedaRequerimientoCompraServiceUtil.getRequerimientoCompra(id);
    }

    protected List<RequerimientoCompraDetalle> obtenerDetalles(int id) throws Exception {
        return BusquedaRequerimientoCompraServiceUtil.getDetalles(id);
    }

    protected RequerimientoCompraOrden obtenerOrden(int id) throws Exception {
        return RequerimientoCompraOrdenServiceUtil.obtenerPorRequerimiento(id);
    }

    protected RequerimientoCompraOrden guardarOrden(RequerimientoCompraOrden orden,
            String usuario) throws Exception {
        return RequerimientoCompraOrdenServiceUtil.crear(orden, usuario);
    }

    protected RequerimientoCompraOrden actualizarOrden(RequerimientoCompraOrden orden,
            String usuario) throws Exception {
        return RequerimientoCompraOrdenServiceUtil.actualizar(orden, usuario);
    }

    protected List<RequerimientoCompraPresupuesto> obtenerCotizacionesEmpresa(int id)
            throws Exception {
        return BusquedaRequerimientoCompraServiceUtil.listarCotizacionesEmpresa(id);
    }

    protected List<RequerimientoCompraComparativa> obtenerComparativas(int id,
            int idPrestador) throws Exception {
        return RequerimientoCompraComparativaServiceUtil.listar(id, idPrestador);
    }

    protected RequerimientoCompraReclamoPrestacional obtenerRelacionRp(int id)
            throws Exception {
        return new RequerimientoCompraReclamoPrestacionalHelper().obtenerPorRequerimiento(id);
    }

    protected List<RequerimientoCompraPresupuesto> obtenerOrdenesMedicas(int id)
            throws Exception {
        return BusquedaRequerimientoCompraServiceUtil.listarOrdenesMedicas(id);
    }
}
