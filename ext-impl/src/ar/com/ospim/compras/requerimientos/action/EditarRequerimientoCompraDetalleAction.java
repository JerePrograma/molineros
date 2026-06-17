package ar.com.ospim.compras.requerimientos.action;

import ar.com.ospim.compras.WebKeysCompras;
import ar.com.ospim.compras.beans.CompraArticulo;
import ar.com.ospim.compras.requerimientos.service.EditarRequerimientoCompraServiceUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import java.util.ArrayList;
import java.util.List;

public class EditarRequerimientoCompraDetalleAction extends PortletAction {

    private static final String STRUTS_ACTION_EDITAR_REQUERIMIENTO =
            "/compras/editar_requerimiento";

    private static final String STRUTS_ACTION_ALTA_ARTICULO_POPUP =
            "/compras/alta_articulo_popup";

    private static final String STRUTS_ACTION_LISTAR_ARTICULOS_SECTOR =
            "/compras/listar_articulos_sector";

    private static final String FORWARD_ALTA_ARTICULO_POPUP =
            "portlet.compras.alta_articulo_popup";

    private static final String FORWARD_ARTICULOS_SECTOR =
            "portlet.compras.articulos_sector";

    private static final String ARTICULOS_COMPRA =
            "ARTICULOS_COMPRA";

    private final RequerimientoCompraDetalleHelper detalleHelper =
            new RequerimientoCompraDetalleHelper();

    public void processAction(ActionMapping mapping,
                              ActionForm form,
                              PortletConfig portletConfig,
                              ActionRequest actionRequest,
                              ActionResponse actionResponse) throws Exception {

        String cmd = detalleHelper.getParametroTrim(actionRequest, Constants.CMD);

        int idRequerimientoCompra = 0;

        try {
            idRequerimientoCompra =
                    detalleHelper.getIdRequerimientoCompraFromRequest(actionRequest);
        } catch (Exception e) {
            idRequerimientoCompra = 0;
        }

        boolean accionPopupArticulo = "saveArticuloPopup".equals(cmd);

        try {
            User user = PortalUtil.getUser(actionRequest);

            detalleHelper.validarPermisoABM(user);

            String usuario = detalleHelper.getUsuario(user);

            if ("saveArticuloPopup".equals(cmd)) {
                CompraArticulo articulo =
                        detalleHelper.guardarArticuloDesdeRequest(
                                actionRequest,
                                usuario
                        );

                String callback = detalleHelper.sanitizarCallback(
                        detalleHelper.getParametroTrim(actionRequest, "callback")
                );

                int idSector = articulo != null && articulo.getIdSector() != null
                        ? articulo.getIdSector().intValue()
                        : detalleHelper.parseEnteroConDefault(
                        actionRequest,
                        "id_sector",
                        "Sector del articulo",
                        0
                );

                actionResponse.setRenderParameter(
                        "struts_action",
                        STRUTS_ACTION_ALTA_ARTICULO_POPUP
                );

                actionResponse.setRenderParameter(
                        "callback",
                        callback
                );

                actionResponse.setRenderParameter(
                        "id_sector",
                        String.valueOf(idSector)
                );

                actionResponse.setRenderParameter(
                        "articulo_guardado",
                        "true"
                );

                actionResponse.setRenderParameter(
                        "id_articulo_guardado",
                        articulo != null && articulo.getId() != null
                                ? String.valueOf(articulo.getId())
                                : ""
                );

                actionResponse.setRenderParameter(
                        "articulo_descripcion_guardada",
                        articulo != null ? articulo.getDescripcion() : ""
                );

                setForward(
                        actionRequest,
                        FORWARD_ALTA_ARTICULO_POPUP
                );

                return;
            }

            if ("saveArticulo".equals(cmd)) {
                detalleHelper.guardarArticuloDesdeRequest(
                        actionRequest,
                        usuario
                );

                SessionMessages.add(
                        actionRequest,
                        "requerimiento-compra-articulo-guardado"
                );

                detalleHelper.setRenderEdicion(
                        actionResponse,
                        idRequerimientoCompra
                );

                setForward(
                        actionRequest,
                        WebKeysCompras.FORWARD_COMPRAS_EDITAR_REQUERIMIENTO
                );

                return;
            }

            if ("deleteArticulo".equals(cmd)) {
                detalleHelper.borrarArticuloDesdeRequest(
                        actionRequest
                );

                SessionMessages.add(
                        actionRequest,
                        "requerimiento-compra-articulo-borrado"
                );

                detalleHelper.setRenderEdicion(
                        actionResponse,
                        idRequerimientoCompra
                );

                setForward(
                        actionRequest,
                        WebKeysCompras.FORWARD_COMPRAS_EDITAR_REQUERIMIENTO
                );

                return;
            }

            if ("addItem".equals(cmd) || "updateItem".equals(cmd)) {
                detalleHelper.guardarDetalleDesdeRequest(
                        actionRequest,
                        actionResponse,
                        usuario
                );

                SessionMessages.add(
                        actionRequest,
                        "requerimiento-compra-item-guardado"
                );

                setForward(
                        actionRequest,
                        WebKeysCompras.FORWARD_COMPRAS_EDITAR_REQUERIMIENTO
                );

                return;
            }

            if ("deleteItem".equals(cmd)) {
                detalleHelper.borrarDetalleDesdeRequest(
                        actionRequest,
                        actionResponse,
                        usuario
                );

                SessionMessages.add(
                        actionRequest,
                        "requerimiento-compra-item-borrado"
                );

                setForward(
                        actionRequest,
                        WebKeysCompras.FORWARD_COMPRAS_EDITAR_REQUERIMIENTO
                );

                return;
            }

            detalleHelper.setRenderEdicion(
                    actionResponse,
                    idRequerimientoCompra
            );

            setForward(
                    actionRequest,
                    WebKeysCompras.FORWARD_COMPRAS_EDITAR_REQUERIMIENTO
            );

        } catch (Exception e) {
            String mensaje = e.getMessage();

            if (WebKeysCompras.isEmpty(mensaje)) {
                mensaje = "No se pudo procesar el detalle del requerimiento de compra.";
            }

            if (accionPopupArticulo) {
                String callback = detalleHelper.sanitizarCallback(
                        detalleHelper.getParametroTrim(actionRequest, "callback")
                );

                String idSector = detalleHelper.getParametroTrim(actionRequest, "id_sector");
                String descripcionArticulo =
                        detalleHelper.getParametroTrim(actionRequest, "articulo_descripcion");

                if (WebKeysCompras.isEmpty(descripcionArticulo)) {
                    descripcionArticulo =
                            detalleHelper.getParametroTrim(actionRequest, "articulo");
                }

                actionResponse.setRenderParameter(
                        "struts_action",
                        STRUTS_ACTION_ALTA_ARTICULO_POPUP
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

                setForward(
                        actionRequest,
                        FORWARD_ALTA_ARTICULO_POPUP
                );

                return;
            }

            SessionErrors.add(
                    actionRequest,
                    "requerimiento-compra-error"
            );

            actionRequest.setAttribute(
                    WebKeysCompras.ERROR_PARA_ALERT,
                    mensaje
            );

            if (e instanceof RequerimientoCompraDetalleHelper.ValidacionCompraException) {
                RequerimientoCompraDetalleHelper.ValidacionCompraException validacion =
                        (RequerimientoCompraDetalleHelper.ValidacionCompraException) e;

                actionRequest.setAttribute(
                        WebKeysCompras.ERROR_CAMPO_COMPRA,
                        validacion.getCampo()
                );
            }

            detalleHelper.setRenderEdicion(
                    actionResponse,
                    idRequerimientoCompra
            );

            actionResponse.setRenderParameter(
                    "compras_error",
                    "true"
            );

            actionResponse.setRenderParameter(
                    "compras_operacion",
                    cmd != null ? cmd : ""
            );

            setForward(
                    actionRequest,
                    WebKeysCompras.FORWARD_COMPRAS_EDITAR_REQUERIMIENTO
            );
        }
    }

    public ActionForward render(ActionMapping mapping,
                                ActionForm form,
                                PortletConfig portletConfig,
                                RenderRequest renderRequest,
                                RenderResponse renderResponse) throws Exception {

        try {
            User user = PortalUtil.getUser(renderRequest);

            detalleHelper.validarPermisoABM(user);

            String strutsAction = ParamUtil.getString(renderRequest, "struts_action", "");

            if (STRUTS_ACTION_ALTA_ARTICULO_POPUP.equals(strutsAction)) {
                return mapping.findForward(FORWARD_ALTA_ARTICULO_POPUP);
            }

            if (STRUTS_ACTION_LISTAR_ARTICULOS_SECTOR.equals(strutsAction)) {
                cargarArticulosSector(renderRequest);

                return mapping.findForward(FORWARD_ARTICULOS_SECTOR);
            }

            return mapping.findForward(
                    WebKeysCompras.FORWARD_COMPRAS_EDITAR_REQUERIMIENTO
            );
        } catch (Exception e) {
            String mensaje = e.getMessage();

            if (WebKeysCompras.isEmpty(mensaje)) {
                mensaje = "No posee permisos para administrar detalles de compras.";
            }

            renderRequest.setAttribute(
                    WebKeysCompras.ERROR_PARA_ALERT,
                    mensaje
            );

            return mapping.findForward(WebKeysCompras.FORWARD_COMPRAS_ERROR);
        }
    }

    private void cargarArticulosSector(RenderRequest request) throws Exception {
        int idSector = ParamUtil.getInteger(request, "sector_id", 0);

        List<CompraArticulo> articulos = new ArrayList<CompraArticulo>();

        if (idSector > 0) {
            articulos =
                    EditarRequerimientoCompraServiceUtil.listarArticulos(
                            Integer.valueOf(idSector),
                            null
                    );
        }

        request.setAttribute(
                ARTICULOS_COMPRA,
                articulos
        );

        request.setAttribute(
                "ID_SECTOR_ARTICULOS_COMPRA",
                String.valueOf(idSector)
        );
    }
}
