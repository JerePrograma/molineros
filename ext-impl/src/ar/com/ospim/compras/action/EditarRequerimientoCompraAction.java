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
import ar.com.ospim.compras.beans.RequerimientoCompraSector;
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
            String usuario = getUsuario(user);

            validarPermisoABM(user);

            if (Constants.ADD.equals(cmd) || Constants.UPDATE.equals(cmd)) {
                RequerimientoCompra requerimiento = getRequerimientoFromRequest(actionRequest);

                if (requerimiento.getIdRequerimientoCompra() > 0) {
                    RequerimientoCompra existente =
                            validarRequerimientoEditable(requerimiento.getIdRequerimientoCompra());

                    requerimiento.setIdEstado(existente.getIdEstado());
                } else {
                    requerimiento.setIdEstado(WebKeysCompras.ESTADO_BORRADOR);
                }

                prepararRequerimientoParaGuardar(requerimiento, user);
                validarCabecera(requerimiento);

                idRequerimientoCompra =
                        EditarRequerimientoCompraServiceUtil.guardarRequerimientoCompra(requerimiento, usuario);

                setIdRequerimientoEnRequest(actionRequest, actionResponse, idRequerimientoCompra);

                SessionMessages.add(actionRequest, "requerimiento-compra-guardado");
                setForward(actionRequest, WebKeysCompras.FORWARD_COMPRAS_EDITAR_REQUERIMIENTO);

                return;
            }

            if ("addItem".equals(cmd) || "updateItem".equals(cmd)) {
                RequerimientoCompraDetalle detalle = getDetalleFromRequest(actionRequest);

                validarRequerimientoEditable(detalle.getIdRequerimientoCompra());
                validarDetalle(detalle);

                EditarRequerimientoCompraServiceUtil.guardarDetalle(detalle, usuario);

                setIdRequerimientoEnRequest(actionRequest, actionResponse, detalle.getIdRequerimientoCompra());

                SessionMessages.add(actionRequest, "requerimiento-compra-item-guardado");
                setForward(actionRequest, WebKeysCompras.FORWARD_COMPRAS_EDITAR_REQUERIMIENTO);

                return;
            }

            if ("deleteItem".equals(cmd)) {
                int idDetalle = getIdDetalleFromRequest(actionRequest);

                if (idDetalle <= 0) {
                    throw new Exception("Debe informar el renglon a borrar.");
                }

                if (idRequerimientoCompra <= 0) {
                    throw new Exception("Debe informar el requerimiento de compra.");
                }

                validarRequerimientoEditable(idRequerimientoCompra);

                EditarRequerimientoCompraServiceUtil.borrarDetalle(idDetalle, usuario);

                setIdRequerimientoEnRequest(actionRequest, actionResponse, idRequerimientoCompra);

                SessionMessages.add(actionRequest, "requerimiento-compra-item-borrado");
                setForward(actionRequest, WebKeysCompras.FORWARD_COMPRAS_EDITAR_REQUERIMIENTO);

                return;
            }

            if (Constants.DELETE.equals(cmd)) {
                if (idRequerimientoCompra <= 0) {
                    throw new Exception("Debe informar el requerimiento de compra a borrar.");
                }

                validarRequerimientoPuedeAnular(idRequerimientoCompra);

                EditarRequerimientoCompraServiceUtil.borrarRequerimientoCompra(idRequerimientoCompra, usuario);

                SessionMessages.add(actionRequest, "requerimiento-compra-borrado");
                setForward(actionRequest, WebKeysCompras.FORWARD_COMPRAS_VIEW);

                return;
            }

            if (idRequerimientoCompra > 0) {
                actionResponse.setRenderParameter("id_requerimiento_compra", String.valueOf(idRequerimientoCompra));
            }

            setForward(actionRequest, WebKeysCompras.FORWARD_COMPRAS_EDITAR_REQUERIMIENTO);
        } catch (Exception e) {
            _log.error(e);

            SessionErrors.add(actionRequest, e.getClass().getName());
            actionRequest.setAttribute(WebKeysCompras.ERROR_PARA_ALERT, e.getMessage());

            if (idRequerimientoCompra > 0) {
                actionResponse.setRenderParameter("id_requerimiento_compra", String.valueOf(idRequerimientoCompra));
            }

            setForward(actionRequest, WebKeysCompras.FORWARD_COMPRAS_EDITAR_REQUERIMIENTO);
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

                if (requerimiento == null) {
                    throw new Exception("No se encontro el requerimiento de compra informado.");
                }
            } else {
                requerimiento = crearRequerimientoNuevo(renderRequest);
            }

            cargarCatalogos(renderRequest);

            boolean soloLectura = esModoSoloLectura(renderRequest);

            renderRequest.setAttribute(WebKeysCompras.SOLO_LECTURA_ATTR, Boolean.valueOf(soloLectura));
            renderRequest.setAttribute(WebKeysCompras.REQUERIMIENTO_COMPRA_EN_EDICION, requerimiento);
            renderRequest.setAttribute(WebKeysCompras.ITEMS_REQUERIMIENTO_COMPRA_EN_EDICION, requerimiento.getDetalles());
        } catch (Exception e) {
            _log.error(e);
            renderRequest.setAttribute(WebKeysCompras.ERROR_PARA_ALERT, e.getMessage());
        }

        return mapping.findForward(WebKeysCompras.FORWARD_COMPRAS_EDITAR_REQUERIMIENTO);
    }

    private boolean esModoSoloLectura(RenderRequest renderRequest) {
        String strutsAction = ParamUtil.getString(renderRequest, "struts_action", "");
        String modo = ParamUtil.getString(renderRequest, "modo", "");

        return "/compras/ver_requerimiento".equals(strutsAction)
                || "ver".equalsIgnoreCase(modo);
    }

    private RequerimientoCompra crearRequerimientoNuevo(RenderRequest renderRequest) throws Exception {
        RequerimientoCompra requerimiento = new RequerimientoCompra();

        requerimiento.setFechaSolicitud(new Date());

        User user = PortalUtil.getUser(renderRequest);

        if (user != null) {
            requerimiento.setSolicitanteUsr(user.getScreenName());
            requerimiento.setSolicitanteNombre(user.getFullName());
        }

        return requerimiento;
    }

    private void cargarCatalogos(RenderRequest request) throws Exception {
        request.setAttribute(
                WebKeysCompras.ESTADOS_REQUERIMIENTO_COMPRA,
                BusquedaRequerimientoCompraServiceUtil.listarEstados()
        );

        request.setAttribute(
                WebKeysCompras.SECTORES_REQUERIMIENTO_COMPRA,
                BusquedaRequerimientoCompraServiceUtil.listarSectores()
        );
    }

    private void setIdRequerimientoEnRequest(ActionRequest request, ActionResponse response, int idRequerimientoCompra) {
        request.setAttribute(
                WebKeysCompras.ID_REQUERIMIENTO_COMPRA_EN_EDICION,
                Integer.valueOf(idRequerimientoCompra)
        );

        response.setRenderParameter("id_requerimiento_compra", String.valueOf(idRequerimientoCompra));
    }

    private String getUsuario(User user) {
        return user != null ? user.getScreenName() : "sistema";
    }

    private void validarPermisoABM(User user) throws Exception {
        if (user == null) {
            throw new Exception("No se pudo determinar el usuario actual.");
        }

        if (!PermissionUtil.userContainsRole(user, WebKeysCompras.ROL_ABM_COMPRAS)) {
            throw new Exception("No posee permisos para administrar requerimientos de compras.");
        }
    }

    private RequerimientoCompra validarRequerimientoEditable(int idRequerimientoCompra) throws Exception {
        if (idRequerimientoCompra <= 0) {
            return null;
        }

        RequerimientoCompra requerimiento =
                BusquedaRequerimientoCompraServiceUtil.getRequerimientoCompra(idRequerimientoCompra);

        if (requerimiento == null) {
            throw new Exception("No se encontro el requerimiento de compra informado.");
        }

        if (!requerimiento.isEditable()) {
            throw new Exception("Solo se pueden editar requerimientos en estado Borrador.");
        }

        return requerimiento;
    }

    private void validarRequerimientoPuedeAnular(int idRequerimientoCompra) throws Exception {
        RequerimientoCompra requerimiento =
                BusquedaRequerimientoCompraServiceUtil.getRequerimientoCompra(idRequerimientoCompra);

        if (requerimiento == null) {
            throw new Exception("No se encontro el requerimiento de compra informado.");
        }

        if (!requerimiento.puedeAnular()) {
            throw new Exception("El requerimiento no puede anularse en su estado actual.");
        }
    }

    private void prepararRequerimientoParaGuardar(RequerimientoCompra requerimiento, User user) throws Exception {
        if (requerimiento == null) {
            return;
        }

        if (requerimiento.getFechaSolicitud() == null) {
            requerimiento.setFechaSolicitud(new Date());
        }

        if (WebKeysCompras.isEmpty(requerimiento.getSolicitanteUsr()) && user != null) {
            requerimiento.setSolicitanteUsr(user.getScreenName());
        }

        if (WebKeysCompras.isEmpty(requerimiento.getSolicitanteNombre()) && user != null) {
            requerimiento.setSolicitanteNombre(user.getFullName());
        }

        if (requerimiento.getIdSector() != null && requerimiento.getIdSector().intValue() > 0) {
            RequerimientoCompraSector sector =
                    BusquedaRequerimientoCompraServiceUtil.getSector(requerimiento.getIdSector().intValue());

            if (sector != null) {
                requerimiento.setSectorCodigo(sector.getCodigo());
                requerimiento.setSectorDescripcion(sector.getDescripcion());
                requerimiento.setRequiereAfiliado(sector.isRequiereAfiliado());
            }
        }
    }

    private void validarCabecera(RequerimientoCompra requerimiento) throws Exception {
        if (requerimiento == null) {
            throw new Exception("Debe informar el requerimiento de compra.");
        }

        if (requerimiento.getIdSector() == null || requerimiento.getIdSector().intValue() <= 0) {
            throw new Exception("Debe informar el sector solicitante.");
        }

        if (WebKeysCompras.isEmpty(requerimiento.getSolicitanteUsr())) {
            throw new Exception("Debe informar el solicitante.");
        }

        if (WebKeysCompras.isEmpty(requerimiento.getDescripcion())) {
            throw new Exception("Debe informar la descripcion del requerimiento.");
        }

        if (requerimiento.isRequiereAfiliado() && !requerimiento.tieneAfiliadoInformado()) {
            throw new Exception("Debe informar el afiliado del requerimiento.");
        }
    }

    private void validarDetalle(RequerimientoCompraDetalle detalle) throws Exception {
        if (detalle == null) {
            throw new Exception("Debe informar el renglon del requerimiento.");
        }

        if (detalle.getIdRequerimientoCompra() <= 0) {
            throw new Exception("Debe guardar primero la cabecera del requerimiento.");
        }

        if (WebKeysCompras.isEmpty(detalle.getArticulo())) {
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

        int idDetalle = getIdDetalleFromRequest(request);

        detalle.setIdRequerimientoDetalle(idDetalle);
        detalle.setIdRequerimientoCompra(ParamUtil.getInteger(request, "id_requerimiento_compra", 0));
        detalle.setRenglon(ParamUtil.getInteger(request, "renglon", 0));
        detalle.setTipoArticulo(ParamUtil.getString(request, "tipo_articulo", null));
        detalle.setArticulo(ParamUtil.getString(request, "articulo", null));
        detalle.setCantidad(parseBigDecimal(ParamUtil.getString(request, "cantidad", "1")));
        detalle.setUnidadMedida(ParamUtil.getString(request, "unidad_medida", null));

        detalle.setPrecioUnitarioEstimado(parseBigDecimalNullable(
                ParamUtil.getString(request, "precio_unitario_estimado", null)));

        detalle.setPrecioTotalEstimado(parseBigDecimalNullable(
                ParamUtil.getString(request, "precio_total_estimado", null)));

        detalle.setObservaciones(ParamUtil.getString(request, "observaciones_detalle", null));

        return detalle;
    }

    private int getIdDetalleFromRequest(ActionRequest request) {
        return ParamUtil.getInteger(request, "id_requerimiento_detalle", 0);
    }

    private Date parseDate(String value) {
        if (WebKeysCompras.isEmpty(value)) {
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
            if (WebKeysCompras.isEmpty(value)) {
                return null;
            }

            String clean = value.trim().replace(" ", "");

            if (clean.indexOf(',') >= 0) {
                clean = clean.replace(".", "").replace(",", ".");
            }

            return new BigDecimal(clean);
        } catch (Exception e) {
            return null;
        }
    }
}