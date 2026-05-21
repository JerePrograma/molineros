package ar.com.ospim.compras.action;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.compras.WebKeysCompras;
import ar.com.ospim.compras.beans.RequerimientoCompra;
import ar.com.ospim.compras.beans.RequerimientoCompraItem;
import ar.com.ospim.compras.service.BusquedaRequerimientoCompraServiceUtil;
import ar.com.ospim.compras.service.EditarRequerimientoCompraServiceUtil;
import ar.com.ospim.util.PermissionUtil;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

public class EditarRequerimientoCompraAction extends PortletAction {

    private static Log _log = LogFactoryUtil.getLog(EditarRequerimientoCompraAction.class);

    public void processAction(ActionMapping mapping, ActionForm form,
                              PortletConfig portletConfig, ActionRequest actionRequest,
                              ActionResponse actionResponse) throws Exception {

        String cmd = ParamUtil.getString(actionRequest, Constants.CMD, null);
        int idRequerimientoCompra = ParamUtil.getInteger(actionRequest, "id_requerimiento_compra", 0);

        try {
            User user = PortalUtil.getUser(actionRequest);
            String usuario = user != null ? user.getScreenName() : "sistema";

            validarPermisoABM(user);

            if (Constants.ADD.equals(cmd) || Constants.UPDATE.equals(cmd)) {
                RequerimientoCompra requerimiento = getRequerimientoFromRequest(actionRequest);
                validarCabecera(requerimiento);

                idRequerimientoCompra =
                        EditarRequerimientoCompraServiceUtil.guardarRequerimientoCompra(requerimiento, usuario);

                actionRequest.setAttribute(
                        WebKeysCompras.ID_REQUERIMIENTO_COMPRA_EN_EDICION,
                        Integer.valueOf(idRequerimientoCompra)
                );

                SessionMessages.add(actionRequest, "requerimiento-compra-guardado");
                setForward(actionRequest, "portlet.compras.editar_requerimiento");
                return;
            }

            if ("addItem".equals(cmd) || "updateItem".equals(cmd)) {
                RequerimientoCompraItem item = getItemFromRequest(actionRequest);
                validarItem(item);

                EditarRequerimientoCompraServiceUtil.guardarItem(item, usuario);

                actionRequest.setAttribute(
                        WebKeysCompras.ID_REQUERIMIENTO_COMPRA_EN_EDICION,
                        Integer.valueOf(item.getIdRequerimientoCompra())
                );

                SessionMessages.add(actionRequest, "requerimiento-compra-item-guardado");
                setForward(actionRequest, "portlet.compras.editar_requerimiento");
                return;
            }

            if ("deleteItem".equals(cmd)) {
                int idItem = ParamUtil.getInteger(actionRequest, "id_item", 0);
                if (idItem <= 0) {
                    throw new Exception("Debe informar el item a borrar.");
                }

                EditarRequerimientoCompraServiceUtil.borrarItem(idItem, usuario);

                actionRequest.setAttribute(
                        WebKeysCompras.ID_REQUERIMIENTO_COMPRA_EN_EDICION,
                        Integer.valueOf(idRequerimientoCompra)
                );

                SessionMessages.add(actionRequest, "requerimiento-compra-item-borrado");
                setForward(actionRequest, "portlet.compras.editar_requerimiento");
                return;
            }

            if (Constants.DELETE.equals(cmd)) {
                if (idRequerimientoCompra <= 0) {
                    throw new Exception("Debe informar el requerimiento de compra a borrar.");
                }

                EditarRequerimientoCompraServiceUtil.borrarRequerimientoCompra(idRequerimientoCompra, usuario);
                SessionMessages.add(actionRequest, "requerimiento-compra-borrado");
                setForward(actionRequest, "portlet.compras.view");
                return;
            }

            setForward(actionRequest, "portlet.compras.editar_requerimiento");
        } catch (Exception e) {
            _log.error(e);
            SessionErrors.add(actionRequest, e.getClass().getName());
            actionRequest.setAttribute(WebKeysCompras.ERROR_PARA_ALERT, e.getMessage());
            setForward(actionRequest, "portlet.compras.editar_requerimiento");
        }
    }

    public ActionForward render(ActionMapping mapping, ActionForm form,
                                PortletConfig portletConfig, RenderRequest renderRequest,
                                RenderResponse renderResponse) throws Exception {

        try {
            int idRequerimientoCompra = ParamUtil.getInteger(renderRequest, "id_requerimiento_compra", 0);

            Object idAttr = renderRequest.getAttribute(WebKeysCompras.ID_REQUERIMIENTO_COMPRA_EN_EDICION);
            if (idRequerimientoCompra == 0 && idAttr instanceof Integer) {
                idRequerimientoCompra = ((Integer) idAttr).intValue();
            }

            RequerimientoCompra requerimiento;

            if (idRequerimientoCompra > 0) {
                requerimiento = BusquedaRequerimientoCompraServiceUtil.getRequerimientoCompra(idRequerimientoCompra);
            } else {
                requerimiento = new RequerimientoCompra();

                User user = PortalUtil.getUser(renderRequest);
                if (user != null) {
                    requerimiento.setSolicitanteUsr(user.getScreenName());
                }
            }

            renderRequest.setAttribute(WebKeysCompras.REQUERIMIENTO_COMPRA_EN_EDICION, requerimiento);
        } catch (Exception e) {
            _log.error(e);
            renderRequest.setAttribute(WebKeysCompras.ERROR_PARA_ALERT, e.getMessage());
        }

        return mapping.findForward("portlet.compras.editar_requerimiento");
    }

    private void validarPermisoABM(User user) throws Exception {
        if (user == null) {
            throw new Exception("No se pudo determinar el usuario actual.");
        }

        if (!PermissionUtil.userContainsRole(user, WebKeysCompras.ROL_ABM_COMPRAS)) {
            throw new Exception("No posee permisos para administrar requerimientos de compras.");
        }
    }

    private void validarCabecera(RequerimientoCompra requerimiento) throws Exception {
        if (requerimiento == null) {
            throw new Exception("Debe informar el requerimiento de compra.");
        }

        if (isEmpty(requerimiento.getSolicitanteUsr())) {
            throw new Exception("Debe informar el solicitante.");
        }

        if (isEmpty(requerimiento.getMotivo()) && isEmpty(requerimiento.getDetalleRequerimiento())) {
            throw new Exception("Debe informar el detalle del requerimiento.");
        }

        if (requerimiento.getPrioridad() <= 0) {
            throw new Exception("Debe informar la prioridad.");
        }
    }

    private void validarItem(RequerimientoCompraItem item) throws Exception {
        if (item == null) {
            throw new Exception("Debe informar el item del requerimiento.");
        }

        if (item.getIdRequerimientoCompra() <= 0) {
            throw new Exception("Debe guardar primero la cabecera del requerimiento.");
        }

        if (isEmpty(item.getDescripcion())) {
            throw new Exception("Debe informar la descripcion del item.");
        }

        if (item.getCantidad() == null || item.getCantidad().compareTo(BigDecimal.ZERO) <= 0) {
            throw new Exception("La cantidad del item debe ser mayor a cero.");
        }

        if (item.getImporteEstimado() == null || item.getImporteEstimado().compareTo(BigDecimal.ZERO) < 0) {
            throw new Exception("El importe estimado del item no puede ser negativo.");
        }
    }

    private RequerimientoCompra getRequerimientoFromRequest(ActionRequest request) {
        RequerimientoCompra requerimiento = new RequerimientoCompra();

        requerimiento.setIdRequerimientoCompra(ParamUtil.getInteger(request, "id_requerimiento_compra", 0));
        requerimiento.setNumero(ParamUtil.getInteger(request, "numero", 0));

        requerimiento.setAfiliado(ParamUtil.getString(request, "afiliado", null));
        requerimiento.setDni(ParamUtil.getString(request, "dni", null));

        int sectorId = ParamUtil.getInteger(request, "sector_id", 0);
        requerimiento.setSectorId(sectorId > 0 ? Integer.valueOf(sectorId) : null);
        requerimiento.setSectorDescripcion(ParamUtil.getString(request, "sector_descripcion", null));

        requerimiento.setSolicitanteUsr(ParamUtil.getString(request, "solicitante_usr", null));
        requerimiento.setEntidad(ParamUtil.getString(request, "entidad", null));
        requerimiento.setPrioridad(
                ParamUtil.getInteger(request, "prioridad", WebKeysCompras.PRIORIDAD_MEDIA)
        );

        requerimiento.setFechaSolicitud(parseDate(ParamUtil.getString(request, "fecha_solicitud", null)));
        requerimiento.setFechaNecesidad(parseDate(ParamUtil.getString(request, "fecha_necesidad", null)));
        requerimiento.setFechaPedidoCotizacion(parseDate(ParamUtil.getString(request, "fecha_pedido_cotizacion", null)));

        requerimiento.setDetalleRequerimiento(ParamUtil.getString(request, "detalle_requerimiento", null));
        requerimiento.setMotivo(ParamUtil.getString(request, "motivo", null));
        requerimiento.setObservaciones(ParamUtil.getString(request, "observaciones", null));

        requerimiento.setPedidosPresupuestos(ParamUtil.getString(request, "pedidos_presupuestos", null));
        requerimiento.setComparativa(ParamUtil.getString(request, "comparativa", null));

        requerimiento.setRpNumero(parseIntegerNullable(ParamUtil.getString(request, "rp_numero", null)));
        requerimiento.setRpObservacion(ParamUtil.getString(request, "rp_observacion", null));

        Integer ordenCompraNumero = parseIntegerNullable(ParamUtil.getString(request, "orden_compra_numero", null));
        if (ordenCompraNumero == null) {
            ordenCompraNumero = parseIntegerNullable(ParamUtil.getString(request, "id_orden_compra", null));
        }

        requerimiento.setIdOrdenCompra(ordenCompraNumero);
        requerimiento.setOrdenCompraNumero(ordenCompraNumero);
        requerimiento.setOrdenCompraObservacion(ParamUtil.getString(request, "orden_compra_observacion", null));

        requerimiento.setCargoOspim(ParamUtil.getString(request, "cargo_ospim", null));
        requerimiento.setCargoEnsalud(ParamUtil.getString(request, "cargo_ensalud", null));
        requerimiento.setRecupero(parseBooleanNullable(ParamUtil.getString(request, "recupero", null)));
        requerimiento.setCotizado(parseBooleanNullable(ParamUtil.getString(request, "cotizado", null)));

        requerimiento.setLocalidad(ParamUtil.getString(request, "localidad", null));
        requerimiento.setProvincia(ParamUtil.getString(request, "provincia", null));

        requerimiento.setImporteEstimadoTotal(
                parseBigDecimal(ParamUtil.getString(request, "importe_estimado_total", "0"))
        );

        return requerimiento;
    }

    private RequerimientoCompraItem getItemFromRequest(ActionRequest request) {
        RequerimientoCompraItem item = new RequerimientoCompraItem();

        item.setIdItem(ParamUtil.getInteger(request, "id_item", 0));
        item.setIdRequerimientoCompra(ParamUtil.getInteger(request, "id_requerimiento_compra", 0));
        item.setDescripcion(ParamUtil.getString(request, "item_descripcion", null));
        item.setCantidad(parseBigDecimal(ParamUtil.getString(request, "item_cantidad", "0")));
        item.setUnidadMedida(ParamUtil.getString(request, "item_unidad_medida", null));
        item.setImporteEstimado(parseBigDecimal(ParamUtil.getString(request, "item_importe_estimado", "0")));
        item.setObservaciones(ParamUtil.getString(request, "item_observaciones", null));

        return item;
    }

    private Date parseDate(String value) {
        if (value == null || value.trim().length() == 0) {
            return null;
        }

        String clean = value.trim();

        try {
            return new SimpleDateFormat("dd/MM/yyyy").parse(clean);
        } catch (Exception ignored) {
        }

        try {
            return new SimpleDateFormat("yyyy-MM-dd").parse(clean);
        } catch (Exception ignored) {
        }

        return null;
    }

    private BigDecimal parseBigDecimal(String value) {
        try {
            if (value == null || value.trim().length() == 0) {
                return BigDecimal.ZERO;
            }

            return new BigDecimal(value.replace(".", "").replace(",", ".").trim());
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    private Integer parseIntegerNullable(String value) {
        try {
            if (value == null || value.trim().length() == 0) {
                return null;
            }

            String clean = value.trim().replace(".", "").replace(",", "");
            int parsed = Integer.parseInt(clean);

            return parsed > 0 ? Integer.valueOf(parsed) : null;
        } catch (Exception e) {
            return null;
        }
    }

    private Boolean parseBooleanNullable(String value) {
        if (value == null || value.trim().length() == 0 || "0".equals(value.trim())) {
            return null;
        }

        String clean = value.trim().toUpperCase();

        if ("SI".equals(clean) || "S".equals(clean) || "TRUE".equals(clean) || "1".equals(clean)) {
            return Boolean.TRUE;
        }

        if ("NO".equals(clean) || "N".equals(clean) || "FALSE".equals(clean) || "2".equals(clean)) {
            return Boolean.FALSE;
        }

        return null;
    }

    private boolean isEmpty(String value) {
        return value == null || value.trim().length() == 0;
    }
}
