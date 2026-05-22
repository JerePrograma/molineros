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
import ar.com.ospim.compras.beans.RequerimientoCompraDetalle;
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
                RequerimientoCompraDetalle detalle = getDetalleFromRequest(actionRequest);
                validarDetalle(detalle);

                EditarRequerimientoCompraServiceUtil.guardarDetalle(detalle, usuario);

                actionRequest.setAttribute(
                        WebKeysCompras.ID_REQUERIMIENTO_COMPRA_EN_EDICION,
                        Integer.valueOf(detalle.getIdRequerimientoCompra())
                );

                SessionMessages.add(actionRequest, "requerimiento-compra-item-guardado");
                setForward(actionRequest, "portlet.compras.editar_requerimiento");
                return;
            }

            if ("deleteItem".equals(cmd)) {
                int idDetalle = ParamUtil.getInteger(actionRequest, "id_requerimiento_detalle", 0);
                if (idDetalle <= 0) {
                    idDetalle = ParamUtil.getInteger(actionRequest, "id_item", 0);
                }

                if (idDetalle <= 0) {
                    throw new Exception("Debe informar el renglon a borrar.");
                }

                EditarRequerimientoCompraServiceUtil.borrarDetalle(idDetalle, usuario);

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
                    requerimiento.setSolicitanteNombre(user.getFullName());
                }
            }

            renderRequest.setAttribute(WebKeysCompras.REQUERIMIENTO_COMPRA_EN_EDICION, requerimiento);
            renderRequest.setAttribute(WebKeysCompras.ITEMS_REQUERIMIENTO_COMPRA_EN_EDICION, requerimiento.getDetalles());
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

        if (requerimiento.getIdSector() == null || requerimiento.getIdSector().intValue() <= 0) {
            throw new Exception("Debe informar el sector solicitante.");
        }

        if (isEmpty(requerimiento.getSolicitanteUsr())) {
            throw new Exception("Debe informar el solicitante.");
        }

        if (isEmpty(requerimiento.getDescripcion())) {
            throw new Exception("Debe informar la descripcion del requerimiento.");
        }

        if (requerimiento.isRequiereAfiliado()) {
            if (isEmpty(requerimiento.getAfiliadoCuilTitular()) || requerimiento.getAfiliadoInte() == null) {
                throw new Exception("Debe informar el afiliado del requerimiento.");
            }
        }
    }

    private void validarDetalle(RequerimientoCompraDetalle detalle) throws Exception {
        if (detalle == null) {
            throw new Exception("Debe informar el renglon del requerimiento.");
        }

        if (detalle.getIdRequerimientoCompra() <= 0) {
            throw new Exception("Debe guardar primero la cabecera del requerimiento.");
        }

        if (isEmpty(detalle.getArticulo())) {
            throw new Exception("Debe informar el articulo.");
        }

        if (detalle.getCantidad() == null || detalle.getCantidad().compareTo(BigDecimal.ZERO) <= 0) {
            throw new Exception("La cantidad debe ser mayor a cero.");
        }

        if (detalle.getPrecioUnitarioEstimado() != null
                && detalle.getPrecioUnitarioEstimado().compareTo(BigDecimal.ZERO) < 0) {
            throw new Exception("El precio unitario estimado no puede ser negativo.");
        }

        if (detalle.getPrecioTotalEstimado() != null
                && detalle.getPrecioTotalEstimado().compareTo(BigDecimal.ZERO) < 0) {
            throw new Exception("El precio total estimado no puede ser negativo.");
        }
    }

    private RequerimientoCompra getRequerimientoFromRequest(ActionRequest request) {
        RequerimientoCompra requerimiento = new RequerimientoCompra();

        requerimiento.setIdRequerimientoCompra(ParamUtil.getInteger(request, "id_requerimiento_compra", 0));
        requerimiento.setNumero(ParamUtil.getInteger(request, "numero", 0));

        int idEstado = ParamUtil.getInteger(request, "id_estado", WebKeysCompras.ESTADO_BORRADOR);
        if (idEstado <= 0) {
            idEstado = ParamUtil.getInteger(request, "estado", WebKeysCompras.ESTADO_BORRADOR);
        }
        requerimiento.setIdEstado(idEstado);

        int idSector = ParamUtil.getInteger(request, "id_sector", 0);
        if (idSector <= 0) {
            idSector = ParamUtil.getInteger(request, "sector_id", 0);
        }
        requerimiento.setIdSector(idSector > 0 ? Integer.valueOf(idSector) : null);

        requerimiento.setRequiereAfiliado(ParamUtil.getBoolean(request, "requiere_afiliado", false));
        requerimiento.setFechaSolicitud(parseDate(ParamUtil.getString(request, "fecha_solicitud", null)));

        requerimiento.setSolicitanteUsr(ParamUtil.getString(request, "solicitante_usr", null));
        requerimiento.setSolicitanteNombre(ParamUtil.getString(request, "solicitante_nombre", null));

        requerimiento.setAfiliadoCuilTitular(ParamUtil.getString(request, "afiliado_cuil_titular", null));

        int afiliadoInte = ParamUtil.getInteger(request, "afiliado_inte", -1);
        requerimiento.setAfiliadoInte(afiliadoInte >= 0 ? Integer.valueOf(afiliadoInte) : null);

        requerimiento.setDescripcion(ParamUtil.getString(request, "descripcion", null));
        requerimiento.setObservaciones(ParamUtil.getString(request, "observaciones", null));

        return requerimiento;
    }

    private RequerimientoCompraDetalle getDetalleFromRequest(ActionRequest request) {
        RequerimientoCompraDetalle detalle = new RequerimientoCompraDetalle();

        int idDetalle = ParamUtil.getInteger(request, "id_requerimiento_detalle", 0);
        if (idDetalle <= 0) {
            idDetalle = ParamUtil.getInteger(request, "id_item", 0);
        }

        detalle.setIdRequerimientoDetalle(idDetalle);
        detalle.setIdRequerimientoCompra(ParamUtil.getInteger(request, "id_requerimiento_compra", 0));
        detalle.setRenglon(ParamUtil.getInteger(request, "renglon", 0));
        detalle.setTipoArticulo(ParamUtil.getString(request, "tipo_articulo", null));

        String articulo = ParamUtil.getString(request, "articulo", null);
        if (isEmpty(articulo)) {
            articulo = ParamUtil.getString(request, "item_descripcion", null);
        }
        detalle.setArticulo(articulo);

        detalle.setCantidad(parseBigDecimal(ParamUtil.getString(request, "cantidad",
                ParamUtil.getString(request, "item_cantidad", "1"))));

        detalle.setUnidadMedida(ParamUtil.getString(request, "unidad_medida",
                ParamUtil.getString(request, "item_unidad_medida", null)));

        detalle.setPrecioUnitarioEstimado(parseBigDecimalNullable(
                ParamUtil.getString(request, "precio_unitario_estimado",
                        ParamUtil.getString(request, "item_importe_estimado", null))));

        detalle.setPrecioTotalEstimado(parseBigDecimalNullable(
                ParamUtil.getString(request, "precio_total_estimado", null)));

        detalle.setObservaciones(ParamUtil.getString(request, "item_observaciones",
                ParamUtil.getString(request, "observaciones_detalle", null)));

        return detalle;
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
        BigDecimal parsed = parseBigDecimalNullable(value);
        return parsed != null ? parsed : BigDecimal.ZERO;
    }

    private BigDecimal parseBigDecimalNullable(String value) {
        try {
            if (value == null || value.trim().length() == 0) {
                return null;
            }

            return new BigDecimal(value.replace(".", "").replace(",", ".").trim());
        } catch (Exception e) {
            return null;
        }
    }

    private boolean isEmpty(String value) {
        return value == null || value.trim().length() == 0;
    }
}
