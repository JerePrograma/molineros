package ar.com.ospim.requerimientos_compras.action;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.requerimientos_compras.WebKeysRequerimientosCompras;
import ar.com.ospim.requerimientos_compras.beans.RequerimientoCompra;
import ar.com.ospim.requerimientos_compras.beans.RequerimientoCompraItem;
import ar.com.ospim.requerimientos_compras.service.BusquedaRequerimientoCompraServiceUtil;
import ar.com.ospim.requerimientos_compras.service.EditarRequerimientoCompraServiceUtil;

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

            if (Constants.ADD.equals(cmd) || Constants.UPDATE.equals(cmd)) {
                RequerimientoCompra requerimiento = getRequerimientoFromRequest(actionRequest);
                idRequerimientoCompra = EditarRequerimientoCompraServiceUtil.guardarRequerimientoCompra(requerimiento, usuario);
                actionRequest.setAttribute(WebKeysRequerimientosCompras.ID_REQUERIMIENTO_COMPRA_EN_EDICION, Integer.valueOf(idRequerimientoCompra));
                SessionMessages.add(actionRequest, "requerimiento-compra-guardado");
                setForward(actionRequest, "portlet.requerimientos_compras.editar_requerimiento");
                return;
            }

            if ("addItem".equals(cmd) || "updateItem".equals(cmd)) {
                RequerimientoCompraItem item = getItemFromRequest(actionRequest);
                EditarRequerimientoCompraServiceUtil.guardarItem(item, usuario);
                actionRequest.setAttribute(WebKeysRequerimientosCompras.ID_REQUERIMIENTO_COMPRA_EN_EDICION, Integer.valueOf(item.getIdRequerimientoCompra()));
                SessionMessages.add(actionRequest, "requerimiento-compra-item-guardado");
                setForward(actionRequest, "portlet.requerimientos_compras.editar_requerimiento");
                return;
            }

            if ("deleteItem".equals(cmd)) {
                int idItem = ParamUtil.getInteger(actionRequest, "id_item", 0);
                EditarRequerimientoCompraServiceUtil.borrarItem(idItem, usuario);
                actionRequest.setAttribute(WebKeysRequerimientosCompras.ID_REQUERIMIENTO_COMPRA_EN_EDICION, Integer.valueOf(idRequerimientoCompra));
                SessionMessages.add(actionRequest, "requerimiento-compra-item-borrado");
                setForward(actionRequest, "portlet.requerimientos_compras.editar_requerimiento");
                return;
            }

            if (Constants.DELETE.equals(cmd)) {
                EditarRequerimientoCompraServiceUtil.borrarRequerimientoCompra(idRequerimientoCompra, usuario);
                SessionMessages.add(actionRequest, "requerimiento-compra-borrado");
                setForward(actionRequest, "portlet.requerimientos_compras.view");
                return;
            }

            setForward(actionRequest, "portlet.requerimientos_compras.editar_requerimiento");
        } catch (Exception e) {
            _log.error(e);
            SessionErrors.add(actionRequest, e.getClass().getName());
            actionRequest.setAttribute(WebKeysRequerimientosCompras.ERROR_PARA_ALERT, e.getMessage());
            setForward(actionRequest, "portlet.requerimientos_compras.editar_requerimiento");
        }
    }

    public ActionForward render(ActionMapping mapping, ActionForm form,
                                PortletConfig portletConfig, RenderRequest renderRequest,
                                RenderResponse renderResponse) throws Exception {

        try {
            int idRequerimientoCompra = ParamUtil.getInteger(renderRequest, "id_requerimiento_compra", 0);

            Object idAttr = renderRequest.getAttribute(WebKeysRequerimientosCompras.ID_REQUERIMIENTO_COMPRA_EN_EDICION);
            if (idRequerimientoCompra == 0 && idAttr instanceof Integer) {
                idRequerimientoCompra = ((Integer) idAttr).intValue();
            }

            RequerimientoCompra requerimiento = null;
            if (idRequerimientoCompra > 0) {
                requerimiento = BusquedaRequerimientoCompraServiceUtil.getRequerimientoCompra(idRequerimientoCompra);
            } else {
                requerimiento = new RequerimientoCompra();
                try {
                    HttpServletRequest httpServletRequest = PortalUtil.getHttpServletRequest(renderRequest);
                    HttpSession session = httpServletRequest.getSession();
                    requerimiento.setSolicitanteUsr(PortalUtil.getUser(renderRequest).getScreenName());
                } catch (Exception ignored) {
                }
            }

            renderRequest.setAttribute(WebKeysRequerimientosCompras.REQUERIMIENTO_COMPRA_EN_EDICION, requerimiento);
        } catch (Exception e) {
            _log.error(e);
            renderRequest.setAttribute(WebKeysRequerimientosCompras.ERROR_PARA_ALERT, e.getMessage());
        }

        return mapping.findForward("portlet.requerimientos_compras.editar_requerimiento");
    }

    private RequerimientoCompra getRequerimientoFromRequest(ActionRequest request) {
        RequerimientoCompra requerimiento = new RequerimientoCompra();

        requerimiento.setIdRequerimientoCompra(ParamUtil.getInteger(request, "id_requerimiento_compra", 0));

        int sectorId = ParamUtil.getInteger(request, "sector_id", 0);
        requerimiento.setSectorId(sectorId > 0 ? Integer.valueOf(sectorId) : null);

        requerimiento.setSolicitanteUsr(ParamUtil.getString(request, "solicitante_usr", null));
        requerimiento.setEntidad(ParamUtil.getString(request, "entidad", null));
        requerimiento.setPrioridad(ParamUtil.getInteger(request, "prioridad", WebKeysRequerimientosCompras.PRIORIDAD_MEDIA));
        requerimiento.setFechaNecesidad(parseDate(ParamUtil.getString(request, "fecha_necesidad", null)));
        requerimiento.setMotivo(ParamUtil.getString(request, "motivo", null));
        requerimiento.setObservaciones(ParamUtil.getString(request, "observaciones", null));
        requerimiento.setImporteEstimadoTotal(parseBigDecimal(ParamUtil.getString(request, "importe_estimado_total", "0")));

        int idOrdenCompra = ParamUtil.getInteger(request, "id_orden_compra", 0);
        requerimiento.setIdOrdenCompra(idOrdenCompra > 0 ? Integer.valueOf(idOrdenCompra) : null);

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
        try {
            if (value == null || value.trim().length() == 0) {
                return null;
            }
            return new SimpleDateFormat("dd/MM/yyyy").parse(value.trim());
        } catch (Exception e) {
            return null;
        }
    }

    private BigDecimal parseBigDecimal(String value) {
        try {
            if (value == null || value.trim().length() == 0) {
                return BigDecimal.ZERO;
            }
            return new BigDecimal(value.replace(",", ".").trim());
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }
}
