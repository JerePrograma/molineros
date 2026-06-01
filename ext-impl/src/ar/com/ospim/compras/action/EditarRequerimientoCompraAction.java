package ar.com.ospim.compras.action;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.afiliados.beans.Afiliado;
import ar.com.ospim.afiliados.services.BusquedaAfiliadoServiceUtil;
import ar.com.ospim.compras.WebKeysCompras;
import ar.com.ospim.compras.beans.RequerimientoCompra;
import ar.com.ospim.compras.beans.CompraArticulo;
import ar.com.ospim.compras.beans.RequerimientoCompraDetalle;
import ar.com.ospim.compras.beans.RequerimientoCompraSector;
import ar.com.ospim.compras.service.BusquedaRequerimientoCompraServiceUtil;
import ar.com.ospim.compras.service.EditarRequerimientoCompraServiceUtil;
import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.util.PermissionUtil;

import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

public class EditarRequerimientoCompraAction extends PortletAction {

    private static final String FORWARD_ALTA_ARTICULO_POPUP = "portlet.compras.alta_articulo_popup";
    private static final boolean DEBUG_ARTICULOS_COMPRA = true;

    private static final String ARTICULOS_COMPRA =
            "ARTICULOS_COMPRA";

    private static class ValidacionCompraException extends Exception {

        private final String campo;

        public ValidacionCompraException(String campo, String message) {
            super(message);
            this.campo = campo;
        }

        public String getCampo() {
            return campo;
        }
    }

    public void processAction(ActionMapping mapping, ActionForm form,
                              PortletConfig portletConfig, ActionRequest actionRequest,
                              ActionResponse actionResponse) throws Exception {

        String cmd = ParamUtil.getString(actionRequest, Constants.CMD, null);
        String strutsAction = ParamUtil.getString(actionRequest, "struts_action", "");

        int idRequerimientoCompra =
                ParamUtil.getInteger(actionRequest, "id_requerimiento_compra", 0);

        boolean accionPopupArticulo =
                "saveArticuloPopup".equals(cmd)
                        || "/compras/alta_articulo_popup".equals(strutsAction);

        try {
            User user = PortalUtil.getUser(actionRequest);
            String usuario = getUsuario(user);

            if ("saveArticuloPopup".equals(cmd)) {
                validarPermisoABM(user);

                int idSector = parseEnteroConDefault(
                        actionRequest,
                        "id_sector",
                        "Sector del articulo",
                        0
                );

                String descripcion = ParamUtil.getString(
                        actionRequest,
                        "articulo_descripcion",
                        null
                );

                String callback = sanitizarCallback(
                        ParamUtil.getString(actionRequest, "callback", "")
                );

                validarArticuloPopup(idSector, descripcion);

                int idArticulo = EditarRequerimientoCompraServiceUtil.guardarArticulo(
                        null,
                        Integer.valueOf(idSector),
                        descripcion
                );

                CompraArticulo articulo =
                        EditarRequerimientoCompraServiceUtil.getArticulo(idArticulo);

                actionResponse.setRenderParameter("struts_action", "/compras/alta_articulo_popup");
                actionResponse.setRenderParameter("callback", callback);
                actionResponse.setRenderParameter("id_sector", String.valueOf(idSector));
                actionResponse.setRenderParameter("articulo_guardado", "true");
                actionResponse.setRenderParameter("id_articulo_guardado", String.valueOf(idArticulo));

                if (articulo != null) {
                    actionResponse.setRenderParameter(
                            "articulo_descripcion_guardada",
                            articulo.getDescripcion()
                    );
                } else {
                    actionResponse.setRenderParameter(
                            "articulo_descripcion_guardada",
                            descripcion
                    );
                }

                setForward(actionRequest, FORWARD_ALTA_ARTICULO_POPUP);

                return;
            }

            if ("saveArticulo".equals(cmd)) {
                validarPermisoABM(user);

                int idArticulo = parseEnteroConDefault(
                        actionRequest,
                        "id_articulo",
                        "Articulo",
                        0
                );

                int idSector = parseEnteroConDefault(
                        actionRequest,
                        "id_sector",
                        "Sector del articulo",
                        0
                );

                String descripcion = ParamUtil.getString(
                        actionRequest,
                        "articulo_descripcion",
                        null
                );

                validarArticuloPopup(idSector, descripcion);

                EditarRequerimientoCompraServiceUtil.guardarArticulo(
                        idArticulo > 0 ? Integer.valueOf(idArticulo) : null,
                        Integer.valueOf(idSector),
                        descripcion
                );

                if (idRequerimientoCompra > 0) {
                    actionResponse.setRenderParameter(
                            "id_requerimiento_compra",
                            String.valueOf(idRequerimientoCompra)
                    );
                }

                SessionMessages.add(actionRequest, "requerimiento-compra-articulo-guardado");
                setForward(actionRequest, WebKeysCompras.FORWARD_COMPRAS_EDITAR_REQUERIMIENTO);

                return;
            }

            if ("deleteArticulo".equals(cmd)) {
                validarPermisoABM(user);

                int idArticulo = parseEnteroConDefault(
                        actionRequest,
                        "id_articulo",
                        "Articulo",
                        0
                );

                if (idArticulo <= 0) {
                    errorCampo("id_articulo", "Debe informar el articulo a borrar.");
                }

                EditarRequerimientoCompraServiceUtil.borrarArticulo(idArticulo);

                if (idRequerimientoCompra > 0) {
                    actionResponse.setRenderParameter(
                            "id_requerimiento_compra",
                            String.valueOf(idRequerimientoCompra)
                    );
                }

                SessionMessages.add(actionRequest, "requerimiento-compra-articulo-borrado");
                setForward(actionRequest, WebKeysCompras.FORWARD_COMPRAS_EDITAR_REQUERIMIENTO);

                return;
            }

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
                        EditarRequerimientoCompraServiceUtil.guardarRequerimientoCompra(
                                requerimiento,
                                usuario
                        );

                guardarDetallesDesdeRequest(actionRequest, idRequerimientoCompra, usuario);

                setIdRequerimientoEnRequest(
                        actionRequest,
                        actionResponse,
                        idRequerimientoCompra
                );

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
                        EditarRequerimientoCompraServiceUtil.guardarRequerimientoCompra(
                                requerimiento,
                                usuario
                        );

                setIdRequerimientoEnRequest(
                        actionRequest,
                        actionResponse,
                        idRequerimientoCompra
                );

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

                setIdRequerimientoEnRequest(
                        actionRequest,
                        actionResponse,
                        detalle.getIdRequerimientoCompra()
                );

                SessionMessages.add(actionRequest, "requerimiento-compra-item-guardado");
                setForward(actionRequest, WebKeysCompras.FORWARD_COMPRAS_EDITAR_REQUERIMIENTO);

                return;
            }

            if ("deleteItem".equals(cmd)) {
                validarPermisoABM(user);

                int idDetalle = getIdDetalleFromRequest(actionRequest);

                if (idDetalle <= 0) {
                    errorCampo("id_detalle", "Debe informar el detalle a borrar.");
                }

                if (idRequerimientoCompra <= 0) {
                    errorCampo(
                            "id_requerimiento_compra",
                            "Debe informar el requerimiento de compra."
                    );
                }

                validarRequerimientoEditable(idRequerimientoCompra);

                EditarRequerimientoCompraServiceUtil.borrarDetalle(idDetalle, usuario);

                setIdRequerimientoEnRequest(
                        actionRequest,
                        actionResponse,
                        idRequerimientoCompra
                );

                SessionMessages.add(actionRequest, "requerimiento-compra-item-borrado");
                setForward(actionRequest, WebKeysCompras.FORWARD_COMPRAS_EDITAR_REQUERIMIENTO);

                return;
            }

            if (Constants.DELETE.equals(cmd)) {
                validarPermisoAnular(user);

                if (idRequerimientoCompra <= 0) {
                    errorCampo(
                            "id_requerimiento_compra",
                            "Debe informar el requerimiento de compra a anular."
                    );
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
                actionResponse.setRenderParameter(
                        "id_requerimiento_compra",
                        String.valueOf(idRequerimientoCompra)
                );
            }

            setForward(actionRequest, WebKeysCompras.FORWARD_COMPRAS_EDITAR_REQUERIMIENTO);
        } catch (Exception e) {
            String mensaje = e.getMessage();

            if (WebKeysCompras.isEmpty(mensaje)) {
                mensaje = "No se pudo procesar el requerimiento de compra.";
            }

            if (accionPopupArticulo) {
                String callback = sanitizarCallback(
                        ParamUtil.getString(actionRequest, "callback", "")
                );

                String idSector = ParamUtil.getString(actionRequest, "id_sector", "");

                String descripcionArticulo = ParamUtil.getString(
                        actionRequest,
                        "articulo_descripcion",
                        ParamUtil.getString(actionRequest, "articulo", "")
                );

                actionResponse.setRenderParameter(
                        "struts_action",
                        "/compras/alta_articulo_popup"
                );

                actionResponse.setRenderParameter(
                        "callback",
                        callback
                );

                actionResponse.setRenderParameter(
                        "id_sector",
                        idSector
                );

                actionResponse.setRenderParameter(
                        "articulo",
                        descripcionArticulo
                );

                actionResponse.setRenderParameter(
                        "articulo_error",
                        mensaje
                );

                actionResponse.setRenderParameter(
                        "articulo_guardado",
                        "false"
                );

                setForward(actionRequest, FORWARD_ALTA_ARTICULO_POPUP);

                return;
            }

            SessionErrors.add(actionRequest, "requerimiento-compra-error");
            actionRequest.setAttribute(WebKeysCompras.ERROR_PARA_ALERT, mensaje);

            if (e instanceof ValidacionCompraException) {
                ValidacionCompraException validacion = (ValidacionCompraException) e;
                actionRequest.setAttribute(
                        WebKeysCompras.ERROR_CAMPO_COMPRA,
                        validacion.getCampo()
                );
            }

            if (idRequerimientoCompra > 0) {
                actionResponse.setRenderParameter(
                        "id_requerimiento_compra",
                        String.valueOf(idRequerimientoCompra)
                );
            }

            setForward(actionRequest, WebKeysCompras.FORWARD_COMPRAS_EDITAR_REQUERIMIENTO);
        }
    }

    public ActionForward render(ActionMapping mapping, ActionForm form,
                                PortletConfig portletConfig, RenderRequest renderRequest,
                                RenderResponse renderResponse) throws Exception {

        String strutsAction = ParamUtil.getString(renderRequest, "struts_action", "");

        if ("/compras/alta_articulo_popup".equals(strutsAction)) {
            return mapping.findForward(FORWARD_ALTA_ARTICULO_POPUP);
        }

        try {
            int idRequerimientoCompra =
                    ParamUtil.getInteger(renderRequest, "id_requerimiento_compra", 0);

            Object idAttr =
                    renderRequest.getAttribute(WebKeysCompras.ID_REQUERIMIENTO_COMPRA_EN_EDICION);

            if (idRequerimientoCompra == 0 && idAttr instanceof Integer) {
                idRequerimientoCompra = ((Integer) idAttr).intValue();
            }

            RequerimientoCompra requerimiento;

            if (idRequerimientoCompra > 0) {
                requerimiento =
                        BusquedaRequerimientoCompraServiceUtil.getRequerimientoCompra(
                                idRequerimientoCompra
                        );

                if (requerimiento == null) {
                    throw new Exception("No se encontro el requerimiento de compra informado.");
                }
            } else {
                requerimiento = new RequerimientoCompra();
            }

            cargarCatalogos(renderRequest, requerimiento);

            boolean soloLectura = esModoSoloLectura(renderRequest);

            if (soloLectura) {
                cargarAfiliadoRequerimiento(renderRequest, requerimiento);
            } else {
                renderRequest.removeAttribute(WebKeysCompras.AFILIADO_REQUERIMIENTO_COMPRA);
            }

            renderRequest.setAttribute(
                    WebKeysCompras.SOLO_LECTURA_ATTR,
                    Boolean.valueOf(soloLectura)
            );

            renderRequest.setAttribute(
                    WebKeysCompras.REQUERIMIENTO_COMPRA_EN_EDICION,
                    requerimiento
            );

            renderRequest.setAttribute(
                    WebKeysCompras.ITEMS_REQUERIMIENTO_COMPRA_EN_EDICION,
                    requerimiento.getDetalles()
            );
        } catch (Exception e) {
            String mensaje = e.getMessage();

            if (WebKeysCompras.isEmpty(mensaje)) {
                mensaje = "No se pudo cargar el requerimiento de compra.";
            }

            renderRequest.setAttribute(WebKeysCompras.ERROR_PARA_ALERT, mensaje);
        }

        return mapping.findForward(WebKeysCompras.FORWARD_COMPRAS_EDITAR_REQUERIMIENTO);
    }

    private boolean esModoSoloLectura(RenderRequest renderRequest) {
        String strutsAction = ParamUtil.getString(renderRequest, "struts_action", "");
        String modo = ParamUtil.getString(renderRequest, "modo", "");

        return "/compras/ver_requerimiento".equals(strutsAction)
                || "ver".equalsIgnoreCase(modo);
    }

    private void cargarCatalogos(RenderRequest request,
                                 RequerimientoCompra requerimiento) throws Exception {

        request.setAttribute(
                WebKeysCompras.ESTADOS_REQUERIMIENTO,
                BusquedaRequerimientoCompraServiceUtil.listarEstados()
        );

        request.setAttribute(
                WebKeysCompras.SECTORES_REQUERIMIENTO,
                BusquedaRequerimientoCompraServiceUtil.listarSectores()
        );

        List<CompraArticulo> articulos =
                EditarRequerimientoCompraServiceUtil.listarArticulos(
                        null,
                        null
                );

        request.setAttribute(
                ARTICULOS_COMPRA,
                articulos
        );
    }

    private void cargarAfiliadoRequerimiento(RenderRequest renderRequest,
                                             RequerimientoCompra requerimiento) {

        renderRequest.removeAttribute(WebKeysCompras.AFILIADO_REQUERIMIENTO_COMPRA);

        if (requerimiento == null || !requerimiento.tieneAfiliadoInformado()) {
            return;
        }

        try {
            List<Afiliado> afiliados =
                    BusquedaAfiliadoServiceUtil.getBusquedaAfiliadosComponente(
                            requerimiento.getAfiliadoCuilTitular(),
                            requerimiento.getAfiliadoIntString(),
                            null,
                            null,
                            0,
                            null,
                            null,
                            WebKeysGlobal.ID_DEFAULT_ENTIDAD,
                            0,
                            0,
                            new BigDecimal(0)
                    );

            if (afiliados != null && afiliados.size() == 1) {
                renderRequest.setAttribute(
                        WebKeysCompras.AFILIADO_REQUERIMIENTO_COMPRA,
                        afiliados.get(0)
                );
            }
        } catch (Exception e) {
        }
    }

    private void setIdRequerimientoEnRequest(ActionRequest request,
                                             ActionResponse response,
                                             int idRequerimientoCompra) {

        request.setAttribute(
                WebKeysCompras.ID_REQUERIMIENTO_COMPRA_EN_EDICION,
                Integer.valueOf(idRequerimientoCompra)
        );

        response.setRenderParameter(
                "id_requerimiento_compra",
                String.valueOf(idRequerimientoCompra)
        );
    }

    private String getUsuario(User user) {
        return user != null ? user.getScreenName() : "sistema";
    }

    private void validarPermisoABM(User user) throws Exception {
        if (user == null) {
            errorCampo("usuario", "No se pudo determinar el usuario actual.");
        }

        if (!PermissionUtil.userContainsRole(user, WebKeysCompras.ROL_ABM_COMPRAS)) {
            errorCampo(
                    "permisos",
                    "No posee permisos para administrar requerimientos de compras."
            );
        }
    }

    private void validarPermisoAnular(User user) throws Exception {
        if (user == null) {
            errorCampo("usuario", "No se pudo determinar el usuario actual.");
        }

        if (!PermissionUtil.userContainsRole(user, WebKeysCompras.ROL_ANULAR_COMPRAS)
                && !PermissionUtil.userContainsRole(user, WebKeysCompras.ROL_ABM_COMPRAS)) {

            errorCampo(
                    "permisos",
                    "No posee permisos para anular requerimientos de compras."
            );
        }
    }

    private RequerimientoCompra validarRequerimientoEditable(int idRequerimientoCompra)
            throws Exception {

        if (idRequerimientoCompra <= 0) {
            return null;
        }

        RequerimientoCompra requerimiento =
                BusquedaRequerimientoCompraServiceUtil.getRequerimientoCompra(
                        idRequerimientoCompra
                );

        if (requerimiento == null) {
            errorCampo(
                    "id_requerimiento_compra",
                    "No se encontro el requerimiento de compra informado. ID recibido: "
                            + idRequerimientoCompra + "."
            );
        }

        if (!requerimiento.isEditable()) {
            errorCampo(
                    "estado",
                    "Solo se pueden editar requerimientos en estado Borrador. Estado actual: "
                            + requerimiento.getEstadoDescripcionVisible() + "."
            );
        }

        return requerimiento;
    }

    private void validarRequerimientoPuedeAnular(int idRequerimientoCompra)
            throws Exception {

        RequerimientoCompra requerimiento =
                BusquedaRequerimientoCompraServiceUtil.getRequerimientoCompra(
                        idRequerimientoCompra
                );

        if (requerimiento == null) {
            errorCampo(
                    "id_requerimiento_compra",
                    "No se encontro el requerimiento de compra informado. ID recibido: "
                            + idRequerimientoCompra + "."
            );
        }

        if (!requerimiento.puedeAnular()) {
            errorCampo(
                    "estado",
                    "El requerimiento no puede anularse en su estado actual. Estado actual: "
                            + requerimiento.getEstadoDescripcionVisible() + "."
            );
        }
    }

    private void prepararRequerimientoParaGuardar(RequerimientoCompra requerimiento)
            throws Exception {

        if (requerimiento == null) {
            return;
        }

        if (requerimiento.getIdSector() != null
                && requerimiento.getIdSector().intValue() > 0) {

            RequerimientoCompraSector sector =
                    BusquedaRequerimientoCompraServiceUtil.getSector(
                            requerimiento.getIdSector().intValue()
                    );

            if (sector == null) {
                errorCampo(
                        "sector_id",
                        "Sector: el sector seleccionado no existe o no esta disponible. ID recibido: "
                                + requerimiento.getIdSector() + "."
                );
            }

            requerimiento.setSectorDescripcion(sector.getDescripcion());
            requerimiento.setRequiereAfiliado(sector.isRequiereAfiliado());
        }
    }

    private void validarCabecera(RequerimientoCompra requerimiento) throws Exception {
        if (requerimiento == null) {
            errorCampo("requerimiento", "Debe informar el requerimiento de compra.");
        }

        if (requerimiento.getIdSector() == null
                || requerimiento.getIdSector().intValue() <= 0) {

            errorCampo("sector_id", "Sector: debe seleccionar un sector.");
        }

        validarPorcentaje(requerimiento.getCargoOspim(), "Cargo OSPIM");
        validarPorcentaje(requerimiento.getCargoTercerizadora(), "Cargo tercerizadora");

        int cargoOspim = requerimiento.getCargoOspim() != null
                ? requerimiento.getCargoOspim().intValue()
                : 0;

        int cargoTercerizadora = requerimiento.getCargoTercerizadora() != null
                ? requerimiento.getCargoTercerizadora().intValue()
                : 0;

        int sumaCargos = cargoOspim + cargoTercerizadora;

        if (sumaCargos > 100) {
            errorCampo(
                    "cargo_tercerizadora",
                    "Cargos: la suma de Cargo OSPIM (" + cargoOspim
                            + ") y Cargo tercerizadora (" + cargoTercerizadora
                            + ") es " + sumaCargos + ". No puede superar 100."
            );
        }

        boolean usaTercerizadora =
                !(cargoOspim == 100 && cargoTercerizadora == 0);

        if (usaTercerizadora) {
            requerimiento.setRecupero(true);

            if (WebKeysCompras.isEmpty(requerimiento.getIdTercerizadora())) {
                errorCampo(
                        "id_tercerizadora",
                        "Tercerizadora: debe seleccionar un afiliado con tercerizadora cuando la distribución de cargos no es OSPIM 100% / Tercerizadora 0%."
                );
            }
        } else {
            requerimiento.setRecupero(false);
            requerimiento.setIdTercerizadora(null);
        }

        if (requerimiento.isRequiereAfiliado()) {
            if (WebKeysCompras.isEmpty(requerimiento.getAfiliadoCuilTitular())) {
                errorCampo(
                        "afiliado_cuil_titular",
                        "Afiliado: debe seleccionar un afiliado. Falta CUIL titular."
                );
            }

            if (requerimiento.getAfiliadoInt() == null) {
                errorCampo(
                        "afiliado_int",
                        "Afiliado: debe seleccionar un afiliado. Falta integrante."
                );
            }
        }
    }

    private void validarDetalle(RequerimientoCompraDetalle detalle) throws Exception {
        validarDetalle(detalle, "Detalle");
    }

    private void validarDetalle(RequerimientoCompraDetalle detalle, String contexto)
            throws Exception {

        if (detalle == null) {
            errorCampo(contexto, contexto + ": debe informar el detalle del requerimiento.");
        }

        if (detalle.getIdRequerimientoCompra() <= 0) {
            errorCampo(
                    contexto,
                    contexto + ": debe guardar primero la cabecera del requerimiento."
            );
        }

        if (detalle.getIdArticulo() == null
                || detalle.getIdArticulo().intValue() <= 0) {

            errorCampo(
                    contexto + " - id_articulo",
                    contexto + ": debe seleccionar un articulo."
            );
        }

        if (detalle.getCantidad() == null
                || detalle.getCantidad().intValue() <= 0) {

            errorCampo(
                    contexto + " - cantidad",
                    contexto + ": la Cantidad debe ser mayor a cero."
            );
        }

        if (detalle.getPrecioUnitarioEstimado() != null
                && detalle.getPrecioUnitarioEstimado().compareTo(BigDecimal.ZERO) < 0) {

            errorCampo(
                    contexto + " - precio_unitario_estimado",
                    contexto + ": el Precio unitario estimado no puede ser negativo."
            );
        }

        if (detalle.getPrecioTotalEstimado() != null
                && detalle.getPrecioTotalEstimado().compareTo(BigDecimal.ZERO) < 0) {

            errorCampo(
                    contexto + " - precio_total_estimado",
                    contexto + ": el Precio total estimado no puede ser negativo."
            );
        }
    }

    private void validarArticuloPopup(int idSector, String descripcion) throws Exception {
        if (idSector <= 0) {
            errorCampo("id_sector", "Debe informar el sector del articulo.");
        }

        if (WebKeysCompras.isEmpty(descripcion)) {
            errorCampo("articulo_descripcion", "Debe informar la descripcion del articulo.");
        }
    }

    private void validarPorcentaje(Integer value, String label) throws Exception {
        int parsed = value != null ? value.intValue() : 0;

        if (parsed < 0 || parsed > 100) {
            errorCampo(
                    label,
                    label + ": debe estar entre 0 y 100. Valor recibido: " + parsed + "."
            );
        }
    }

    private RequerimientoCompra getRequerimientoFromRequest(ActionRequest request)
            throws Exception {

        RequerimientoCompra requerimiento = new RequerimientoCompra();

        requerimiento.setIdRequerimientoCompra(
                parseEnteroConDefault(
                        request,
                        "id_requerimiento_compra",
                        "ID del requerimiento",
                        0
                )
        );

        int idSector = parseEnteroConDefault(request, "id_sector", "Sector", 0);

        if (idSector <= 0) {
            idSector = parseEnteroConDefault(request, "sector_id", "Sector", 0);
        }

        requerimiento.setIdSector(idSector > 0 ? Integer.valueOf(idSector) : null);

        requerimiento.setAfiliadoCuilTitular(
                WebKeysCompras.trimToNull(
                        ParamUtil.getString(request, "afiliado_cuil_titular", null)
                )
        );

        String afiliadoIntRaw = getParametroTrim(request, "afiliado_int");

        if (WebKeysCompras.isEmpty(afiliadoIntRaw) || "-1".equals(afiliadoIntRaw)) {
            requerimiento.setAfiliadoInt(null);
        } else {
            requerimiento.setAfiliadoInt(
                    parseEnteroOpcional(request, "afiliado_int", "Afiliado - integrante")
            );
        }

        requerimiento.setCargoOspim(
                parsePorcentajeDesdeRequest(request, "cargo_ospim", "Cargo OSPIM")
        );

        requerimiento.setCargoTercerizadora(
                parsePorcentajeDesdeRequest(
                        request,
                        "cargo_tercerizadora",
                        "Cargo tercerizadora"
                )
        );

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

    private RequerimientoCompraDetalle getDetalleFromRequest(ActionRequest request)
            throws Exception {

        RequerimientoCompraDetalle detalle = new RequerimientoCompraDetalle();

        int idDetalle = getIdDetalleFromRequest(request);

        detalle.setId(idDetalle > 0 ? Integer.valueOf(idDetalle) : null);

        detalle.setIdRequerimientoCompra(
                parseEnteroConDefault(
                        request,
                        "id_requerimiento_compra",
                        "ID del requerimiento",
                        0
                )
        );

        int idArticulo = parseEnteroConDefault(
                request,
                "id_articulo",
                "Articulo",
                0
        );

        detalle.setIdArticulo(idArticulo > 0 ? Integer.valueOf(idArticulo) : null);

        detalle.setCantidad(
                parseCantidadDesdeRequest(request, "cantidad", "Cantidad")
        );

        detalle.setPrecioUnitarioEstimado(
                parseBigDecimalNullable(
                        ParamUtil.getString(request, "precio_unitario_estimado", null),
                        "Precio unitario estimado"
                )
        );

        detalle.setPrecioTotalEstimado(
                parseBigDecimalNullable(
                        ParamUtil.getString(request, "precio_total_estimado", null),
                        "Precio total estimado"
                )
        );

        detalle.setObservaciones(
                ParamUtil.getString(request, "observaciones_detalle", null)
        );

        return detalle;
    }

    private int getIdDetalleFromRequest(ActionRequest request) throws Exception {
        return parseEnteroConDefault(request, "id_detalle", "ID del detalle", 0);
    }

    private void guardarDetallesDesdeRequest(ActionRequest request,
                                             int idRequerimientoCompra,
                                             String usuario) throws Exception {

        if (idRequerimientoCompra <= 0) {
            errorCampo(
                    "id_requerimiento_compra",
                    "Debe guardar primero la cabecera del requerimiento."
            );
        }

        String deletedIds = ParamUtil.getString(request, "detalle_deleted_ids", "");

        Set<Integer> borrados = new HashSet<Integer>();

        if (!WebKeysCompras.isEmpty(deletedIds)) {
            String[] ids = deletedIds.split(",");

            for (int i = 0; i < ids.length; i++) {
                String rawId = ids[i] != null ? ids[i].trim() : "";

                if (WebKeysCompras.isEmpty(rawId)) {
                    continue;
                }

                if (!rawId.matches("^[0-9]+$")) {
                    errorCampo(
                            "detalle_deleted_ids",
                            "Detalle a borrar: ID invalido recibido: '" + rawId + "'."
                    );
                }

                int idDetalleBorrado = Integer.parseInt(rawId);

                if (idDetalleBorrado > 0
                        && !borrados.contains(Integer.valueOf(idDetalleBorrado))) {

                    EditarRequerimientoCompraServiceUtil.borrarDetalle(
                            idDetalleBorrado,
                            usuario
                    );

                    borrados.add(Integer.valueOf(idDetalleBorrado));
                }
            }
        }

        int count = parseEnteroConDefault(
                request,
                "detalle_count",
                "Cantidad de detalles",
                0
        );

        for (int i = 0; i < count; i++) {
            String prefix = "detalle_" + i + "_";
            String contexto = "Detalle #" + (i + 1);

            if (filaDetalleVacia(request, prefix)) {
                continue;
            }

            int idDetalle = parseEnteroConDefault(
                    request,
                    prefix + "id",
                    contexto + " - ID",
                    0
            );

            if (idDetalle > 0 && borrados.contains(Integer.valueOf(idDetalle))) {
                continue;
            }

            int idArticulo = parseEnteroConDefault(
                    request,
                    prefix + "id_articulo",
                    contexto + " - Articulo",
                    0
            );

            RequerimientoCompraDetalle detalle = new RequerimientoCompraDetalle();

            detalle.setId(idDetalle > 0 ? Integer.valueOf(idDetalle) : null);
            detalle.setIdRequerimientoCompra(idRequerimientoCompra);
            detalle.setIdArticulo(idArticulo > 0 ? Integer.valueOf(idArticulo) : null);

            detalle.setCantidad(
                    parseCantidadDesdeRequest(
                            request,
                            prefix + "cantidad",
                            contexto + " - Cantidad"
                    )
            );

            detalle.setPrecioUnitarioEstimado(
                    parseBigDecimalNullable(
                            ParamUtil.getString(
                                    request,
                                    prefix + "precio_unitario_estimado",
                                    null
                            ),
                            contexto + " - Precio unitario estimado"
                    )
            );

            detalle.setPrecioTotalEstimado(
                    parseBigDecimalNullable(
                            ParamUtil.getString(
                                    request,
                                    prefix + "precio_total_estimado",
                                    null
                            ),
                            contexto + " - Precio total estimado"
                    )
            );

            detalle.setObservaciones(
                    ParamUtil.getString(request, prefix + "observaciones", null)
            );

            validarDetalle(detalle, contexto);

            EditarRequerimientoCompraServiceUtil.guardarDetalle(detalle, usuario);
        }
    }

    private boolean filaDetalleVacia(ActionRequest request, String prefix) {
        return WebKeysCompras.isEmpty(getParametroTrim(request, prefix + "id"))
                && WebKeysCompras.isEmpty(getParametroTrim(request, prefix + "id_articulo"))
                && WebKeysCompras.isEmpty(getParametroTrim(request, prefix + "cantidad"))
                && WebKeysCompras.isEmpty(
                getParametroTrim(request, prefix + "precio_unitario_estimado")
        )
                && WebKeysCompras.isEmpty(
                getParametroTrim(request, prefix + "precio_total_estimado")
        )
                && WebKeysCompras.isEmpty(getParametroTrim(request, prefix + "observaciones"));
    }

    private void errorCampo(String campo, String mensaje)
            throws ValidacionCompraException {

        throw new ValidacionCompraException(campo, mensaje);
    }

    private String getParametroTrim(ActionRequest request, String nombre) {
        String value = ParamUtil.getString(request, nombre, null);

        if (value == null) {
            return "";
        }

        return value.trim();
    }

    private Integer parseEnteroOpcional(ActionRequest request,
                                        String nombre,
                                        String label)
            throws ValidacionCompraException {

        String value = getParametroTrim(request, nombre);

        if (WebKeysCompras.isEmpty(value)) {
            return null;
        }

        if (!value.matches("^[0-9]+$")) {
            errorCampo(
                    nombre,
                    label + ": el valor ingresado '" + value
                            + "' no es un numero entero valido."
            );
        }

        try {
            return Integer.valueOf(Integer.parseInt(value));
        } catch (Exception e) {
            errorCampo(
                    nombre,
                    label + ": el valor ingresado '" + value
                            + "' esta fuera del rango permitido."
            );
        }

        return null;
    }

    private int parseEnteroConDefault(ActionRequest request,
                                      String nombre,
                                      String label,
                                      int defaultValue)
            throws ValidacionCompraException {

        Integer parsed = parseEnteroOpcional(request, nombre, label);

        if (parsed == null) {
            return defaultValue;
        }

        return parsed.intValue();
    }

    private Integer parsePorcentajeDesdeRequest(ActionRequest request,
                                                String nombre,
                                                String label)
            throws ValidacionCompraException {

        String value = getParametroTrim(request, nombre);

        if (WebKeysCompras.isEmpty(value)) {
            value = "0";
        }

        if (!value.matches("^[0-9]+$")) {
            errorCampo(
                    nombre,
                    label + ": debe ser un numero entero entre 0 y 100. Valor recibido: '"
                            + value + "'."
            );
        }

        int parsed;

        try {
            parsed = Integer.parseInt(value);
        } catch (Exception e) {
            errorCampo(
                    nombre,
                    label + ": el valor ingresado '" + value
                            + "' esta fuera del rango permitido."
            );
            return null;
        }

        if (parsed < 0 || parsed > 100) {
            errorCampo(
                    nombre,
                    label + ": debe estar entre 0 y 100. Valor recibido: " + parsed + "."
            );
        }

        return Integer.valueOf(parsed);
    }

    private Integer parseCantidadDesdeRequest(ActionRequest request,
                                              String nombre,
                                              String label)
            throws ValidacionCompraException {

        String value = getParametroTrim(request, nombre);

        if (WebKeysCompras.isEmpty(value)) {
            errorCampo(nombre, label + ": debe informar una cantidad.");
        }

        if (!value.matches("^[0-9]+$")) {
            errorCampo(
                    nombre,
                    label + ": debe ser un numero entero mayor a cero. Valor recibido: '"
                            + value + "'."
            );
        }

        int parsed;

        try {
            parsed = Integer.parseInt(value);
        } catch (Exception e) {
            errorCampo(
                    nombre,
                    label + ": el valor ingresado '" + value
                            + "' esta fuera del rango permitido."
            );
            return null;
        }

        if (parsed <= 0) {
            errorCampo(
                    nombre,
                    label + ": debe ser mayor a cero. Valor recibido: " + parsed + "."
            );
        }

        return Integer.valueOf(parsed);
    }

    private BigDecimal parseBigDecimalNullable(String value, String label)
            throws ValidacionCompraException {

        if (WebKeysCompras.isEmpty(value)) {
            return null;
        }

        String original = value.trim();
        String clean = original.replace(" ", "");

        if (clean.indexOf(',') >= 0) {
            clean = clean.replace(".", "").replace(",", ".");
        }

        if (!clean.matches("^-?[0-9]+(\\.[0-9]+)?$")) {
            errorCampo(
                    label,
                    label + ": importe invalido. Valor recibido: '" + original
                            + "'. Use formatos como 1234.56 o 1.234,56."
            );
        }

        try {
            return new BigDecimal(clean);
        } catch (Exception e) {
            errorCampo(label, label + ": no se pudo interpretar el importe '" + original + "'.");
        }

        return null;
    }

    private String sanitizarCallback(String callback) {
        if (callback == null) {
            return "";
        }

        callback = callback.trim();

        if (!callback.matches("[A-Za-z0-9_]+")) {
            return "";
        }

        return callback;
    }
}