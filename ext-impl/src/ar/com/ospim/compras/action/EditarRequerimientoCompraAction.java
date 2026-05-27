package ar.com.ospim.compras.action;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

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

    private static final Log _log = LogFactoryUtil.getLog(EditarRequerimientoCompraAction.class);

    public void processAction(ActionMapping mapping, ActionForm form,
                              PortletConfig portletConfig, ActionRequest actionRequest,
                              ActionResponse actionResponse) throws Exception {

        String cmd = ParamUtil.getString(actionRequest, Constants.CMD, null);
        int idRequerimientoCompra = ParamUtil.getInteger(actionRequest, "id_requerimiento_compra", 0);

        try {
            User user = PortalUtil.getUser(actionRequest);
            String usuario = getUsuario(user);

            if ("saveAll".equals(cmd)) {
                validarPermisoABM(user);

                RequerimientoCompra requerimiento = getRequerimientoFromRequest(actionRequest);

                if (requerimiento.getIdRequerimientoCompra() > 0) {
                    RequerimientoCompra existente =
                            validarRequerimientoEditable(requerimiento.getIdRequerimientoCompra());

                    requerimiento.setIdEstado(existente.getIdEstado());
                } else {
                    requerimiento.setIdEstado(WebKeysCompras.ESTADO_BORRADOR);
                }

                prepararRequerimientoParaGuardar(requerimiento);
                validarCabecera(requerimiento);

                idRequerimientoCompra =
                        EditarRequerimientoCompraServiceUtil.guardarRequerimientoCompra(requerimiento, usuario);

                _log.info("COMPRAS saveAll detalle_count="
                        + ParamUtil.getString(actionRequest, "detalle_count", "[NO VIENE]")
                        + " deleted="
                        + ParamUtil.getString(actionRequest, "detalle_deleted_ids", "[NO VIENE]"));

                guardarDetallesDesdeRequest(actionRequest, idRequerimientoCompra, usuario);

                setIdRequerimientoEnRequest(actionRequest, actionResponse, idRequerimientoCompra);

                SessionMessages.add(actionRequest, "requerimiento-compra-guardado");
                setForward(actionRequest, WebKeysCompras.FORWARD_COMPRAS_EDITAR_REQUERIMIENTO);

                return;
            }

            if (Constants.ADD.equals(cmd) || Constants.UPDATE.equals(cmd)) {
                validarPermisoABM(user);

                RequerimientoCompra requerimiento = getRequerimientoFromRequest(actionRequest);

                if (requerimiento.getIdRequerimientoCompra() > 0) {
                    RequerimientoCompra existente =
                            validarRequerimientoEditable(requerimiento.getIdRequerimientoCompra());

                    requerimiento.setIdEstado(existente.getIdEstado());
                } else {
                    requerimiento.setIdEstado(WebKeysCompras.ESTADO_BORRADOR);
                }

                prepararRequerimientoParaGuardar(requerimiento);
                validarCabecera(requerimiento);

                idRequerimientoCompra =
                        EditarRequerimientoCompraServiceUtil.guardarRequerimientoCompra(requerimiento, usuario);

                setIdRequerimientoEnRequest(actionRequest, actionResponse, idRequerimientoCompra);

                SessionMessages.add(actionRequest, "requerimiento-compra-guardado");
                setForward(actionRequest, WebKeysCompras.FORWARD_COMPRAS_EDITAR_REQUERIMIENTO);

                return;
            }

            if ("addItem".equals(cmd) || "updateItem".equals(cmd)) {
                validarPermisoABM(user);

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
                validarPermisoABM(user);

                int idDetalle = getIdDetalleFromRequest(actionRequest);

                if (idDetalle <= 0) {
                    throw new Exception("Debe informar el detalle a borrar.");
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
                validarPermisoAnular(user);

                if (idRequerimientoCompra <= 0) {
                    throw new Exception("Debe informar el requerimiento de compra a anular.");
                }

                validarRequerimientoPuedeAnular(idRequerimientoCompra);

                EditarRequerimientoCompraServiceUtil.cambiarEstado(
                        idRequerimientoCompra,
                        WebKeysCompras.ESTADO_ANULADO,
                        usuario
                );

                SessionMessages.add(actionRequest, "requerimiento-compra-anulado");
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
                requerimiento = new RequerimientoCompra();
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

    private void cargarCatalogos(RenderRequest request) throws Exception {
        request.setAttribute(
                WebKeysCompras.ESTADOS_REQUERIMIENTO,
                BusquedaRequerimientoCompraServiceUtil.listarEstados()
        );

        request.setAttribute(
                WebKeysCompras.SECTORES_REQUERIMIENTO,
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

    private void validarPermisoAnular(User user) throws Exception {
        if (user == null) {
            throw new Exception("No se pudo determinar el usuario actual.");
        }

        if (!PermissionUtil.userContainsRole(user, WebKeysCompras.ROL_ANULAR_COMPRAS)
                && !PermissionUtil.userContainsRole(user, WebKeysCompras.ROL_ABM_COMPRAS)) {
            throw new Exception("No posee permisos para anular requerimientos de compras.");
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

    private void prepararRequerimientoParaGuardar(RequerimientoCompra requerimiento) throws Exception {
        if (requerimiento == null) {
            return;
        }

        if (requerimiento.getIdSector() != null && requerimiento.getIdSector().intValue() > 0) {
            RequerimientoCompraSector sector =
                    BusquedaRequerimientoCompraServiceUtil.getSector(requerimiento.getIdSector().intValue());

            if (sector != null) {
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
            throw new Exception("Debe informar el sector.");
        }

        validarPorcentaje(requerimiento.getCargoOspim(), "Cargo OSPIM");
        validarPorcentaje(requerimiento.getCargoTercerizadora(), "Cargo tercerizadora");

        int cargoOspim = requerimiento.getCargoOspim() != null ? requerimiento.getCargoOspim().intValue() : 0;
        int cargoTercerizadora = requerimiento.getCargoTercerizadora() != null
                ? requerimiento.getCargoTercerizadora().intValue()
                : 0;

        if (cargoOspim + cargoTercerizadora > 100) {
            throw new Exception("La suma de cargos no puede superar 100.");
        }

        if (cargoTercerizadora > 0
                && WebKeysCompras.isEmpty(requerimiento.getIdTercerizadora())) {
            throw new Exception("Debe informar la tercerizadora cuando su cargo es mayor a cero.");
        }

        if (requerimiento.isRequiereAfiliado() && !requerimiento.tieneAfiliadoInformado()) {
            throw new Exception("Debe informar el afiliado del requerimiento.");
        }
    }

    private void validarDetalle(RequerimientoCompraDetalle detalle) throws Exception {
        if (detalle == null) {
            throw new Exception("Debe informar el detalle del requerimiento.");
        }

        if (detalle.getIdRequerimientoCompra() <= 0) {
            throw new Exception("Debe guardar primero la cabecera del requerimiento.");
        }

        if (WebKeysCompras.isEmpty(detalle.getArticulo())) {
            throw new Exception("Debe informar el articulo.");
        }

        if (detalle.getCantidad() == null || detalle.getCantidad().intValue() <= 0) {
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

    private void validarPorcentaje(Integer value, String label) throws Exception {
        int parsed = value != null ? value.intValue() : 0;

        if (parsed < 0 || parsed > 100) {
            throw new Exception(label + " debe estar entre 0 y 100.");
        }
    }

    private RequerimientoCompra getRequerimientoFromRequest(ActionRequest request) {
        RequerimientoCompra requerimiento = new RequerimientoCompra();

        requerimiento.setIdRequerimientoCompra(ParamUtil.getInteger(request, "id_requerimiento_compra", 0));

        int idSector = ParamUtil.getInteger(request, "id_sector", 0);

        if (idSector <= 0) {
            idSector = ParamUtil.getInteger(request, "sector_id", 0);
        }

        requerimiento.setIdSector(idSector > 0 ? Integer.valueOf(idSector) : null);
        requerimiento.setAfiliadoCuilTitular(ParamUtil.getString(request, "afiliado_cuil_titular", null));

        int afiliadoInt = ParamUtil.getInteger(request, "afiliado_int", -1);
        requerimiento.setAfiliadoInt(afiliadoInt >= 0 ? Integer.valueOf(afiliadoInt) : null);

        requerimiento.setCargoOspim(Integer.valueOf(ParamUtil.getInteger(request, "cargo_ospim", 0)));
        requerimiento.setCargoTercerizadora(Integer.valueOf(ParamUtil.getInteger(request, "cargo_tercerizadora", 0)));

        String idTercerizadora = ParamUtil.getString(request, "id_tercerizadora", null);

        if (!WebKeysCompras.isEmpty(idTercerizadora)) {
            requerimiento.setIdTercerizadora(idTercerizadora.trim());
        } else {
            requerimiento.setIdTercerizadora(null);
        }

        requerimiento.setRecupero(ParamUtil.getBoolean(request, "recupero", false));
        requerimiento.setObservaciones(ParamUtil.getString(request, "observaciones", null));

        return requerimiento;
    }

    private RequerimientoCompraDetalle getDetalleFromRequest(ActionRequest request) {
        RequerimientoCompraDetalle detalle = new RequerimientoCompraDetalle();

        int idDetalle = getIdDetalleFromRequest(request);

        detalle.setId(idDetalle > 0 ? Integer.valueOf(idDetalle) : null);
        detalle.setIdRequerimientoCompra(ParamUtil.getInteger(request, "id_requerimiento_compra", 0));
        detalle.setArticulo(ParamUtil.getString(request, "articulo", null));
        detalle.setCantidad(Integer.valueOf(ParamUtil.getInteger(request, "cantidad", 1)));

        detalle.setPrecioUnitarioEstimado(parseBigDecimalNullable(
                ParamUtil.getString(request, "precio_unitario_estimado", null)));

        detalle.setPrecioTotalEstimado(parseBigDecimalNullable(
                ParamUtil.getString(request, "precio_total_estimado", null)));

        detalle.setObservaciones(ParamUtil.getString(request, "observaciones_detalle", null));

        return detalle;
    }

    private int getIdDetalleFromRequest(ActionRequest request) {
        return ParamUtil.getInteger(request, "id_detalle", 0);
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

    private void guardarDetallesDesdeRequest(ActionRequest request, int idRequerimientoCompra, String usuario) throws Exception {
        if (idRequerimientoCompra <= 0) {
            throw new Exception("Debe guardar primero la cabecera del requerimiento.");
        }

        String deletedIds = ParamUtil.getString(request, "detalle_deleted_ids", "");

        Set<Integer> borrados = new HashSet<Integer>();

        if (!WebKeysCompras.isEmpty(deletedIds)) {
            String[] ids = deletedIds.split(",");

            for (int i = 0; i < ids.length; i++) {
                try {
                    int idDetalleBorrado = Integer.parseInt(ids[i].trim());

                    if (idDetalleBorrado > 0 && !borrados.contains(Integer.valueOf(idDetalleBorrado))) {
                        EditarRequerimientoCompraServiceUtil.borrarDetalle(idDetalleBorrado, usuario);
                        borrados.add(Integer.valueOf(idDetalleBorrado));
                    }
                } catch (Exception ignored) {
                }
            }
        }

        int count = ParamUtil.getInteger(request, "detalle_count", 0);

        _log.info("COMPRAS guardarDetallesDesdeRequest count=" + count);

        for (int i = 0; i < count; i++) {
            String prefix = "detalle_" + i + "_";

            String articulo = ParamUtil.getString(request, prefix + "articulo", null);

            if (WebKeysCompras.isEmpty(articulo)) {
                continue;
            }

            int idDetalle = ParamUtil.getInteger(request, prefix + "id", 0);

            if (idDetalle > 0 && borrados.contains(Integer.valueOf(idDetalle))) {
                continue;
            }

            RequerimientoCompraDetalle detalle = new RequerimientoCompraDetalle();

            detalle.setId(idDetalle > 0 ? Integer.valueOf(idDetalle) : null);
            detalle.setIdRequerimientoCompra(idRequerimientoCompra);
            detalle.setArticulo(articulo);
            detalle.setCantidad(Integer.valueOf(ParamUtil.getInteger(request, prefix + "cantidad", 1)));

            detalle.setPrecioUnitarioEstimado(parseBigDecimalNullable(
                    ParamUtil.getString(request, prefix + "precio_unitario_estimado", null)
            ));

            detalle.setPrecioTotalEstimado(parseBigDecimalNullable(
                    ParamUtil.getString(request, prefix + "precio_total_estimado", null)
            ));

            detalle.setObservaciones(ParamUtil.getString(request, prefix + "observaciones", null));

            validarDetalle(detalle);

            EditarRequerimientoCompraServiceUtil.guardarDetalle(detalle, usuario);
        }
    }
}
