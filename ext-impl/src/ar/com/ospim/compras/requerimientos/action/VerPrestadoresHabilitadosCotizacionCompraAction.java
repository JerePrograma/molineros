package ar.com.ospim.compras.requerimientos.action;

import ar.com.ospim.compras.WebKeysCompras;
import ar.com.ospim.compras.requerimientos.beans.PrestadorCotizacion;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompra;
import ar.com.ospim.compras.requerimientos.service.BusquedaRequerimientoCompraServiceUtil;
import ar.com.ospim.compras.requerimientos.service.NotificarCotizacionPrestadorServiceUtil;
import ar.com.ospim.util.PermissionUtil;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

import java.util.ArrayList;
import java.util.List;

import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * Vista previa de prestadores habilitados para el requerimiento informado.
 */
public class VerPrestadoresHabilitadosCotizacionCompraAction
        extends PortletAction {

    private static final Log _log =
            LogFactoryUtil.getLog(
                    VerPrestadoresHabilitadosCotizacionCompraAction.class
            );

    public ActionForward render(
            ActionMapping mapping,
            ActionForm form,
            PortletConfig portletConfig,
            RenderRequest renderRequest,
            RenderResponse renderResponse) throws Exception {

        validarPermiso(
                PortalUtil.getUser(renderRequest)
        );

        int idRequerimientoCompra =
                ParamUtil.getInteger(
                        renderRequest,
                        WebKeysCompras.PARAM_ID_REQUERIMIENTO_COMPRA,
                        0
                );

        RequerimientoCompra requerimiento =
                validarRequerimiento(
                        idRequerimientoCompra
                );

        List<PrestadorCotizacion> prestadores =
                new ArrayList<PrestadorCotizacion>();
        String error = null;

        try {
            prestadores =
                    NotificarCotizacionPrestadorServiceUtil
                            .listarPrestadoresCandidatos(
                                    idRequerimientoCompra
                            );
        } catch (Exception e) {
            _log.error(
                    "No se pudieron consultar los prestadores habilitados "
                            + "para cotizar el requerimiento informado.",
                    e
            );

            error =
                    "No se pudieron consultar los prestadores habilitados. "
                            + "Intente nuevamente.";
        }

        renderRequest.setAttribute(
                WebKeysCompras.REQUERIMIENTO_COMPRA_EN_VIEW,
                requerimiento
        );

        renderRequest.setAttribute(
                WebKeysCompras.PRESTADORES_HABILITADOS_COTIZACION,
                prestadores
        );

        renderRequest.setAttribute(
                WebKeysCompras.ERROR_PRESTADORES_HABILITADOS_COTIZACION,
                error
        );

        return mapping.findForward(
                WebKeysCompras
                        .FORWARD_COMPRAS_PRESTADORES_HABILITADOS_COTIZACION
        );
    }

    private RequerimientoCompra validarRequerimiento(
            int idRequerimientoCompra) throws Exception {

        if (idRequerimientoCompra <= 0) {
            throw new Exception(
                    "Debe informar el requerimiento de compra."
            );
        }

        RequerimientoCompra requerimiento =
                BusquedaRequerimientoCompraServiceUtil
                        .getRequerimientoCompra(
                                idRequerimientoCompra
                        );

        if (requerimiento == null
                || !requerimiento.puedeEnviarACotizar()) {

            throw new Exception(
                    "El requerimiento informado no admite consultar "
                            + "prestadores habilitados para cotizar."
            );
        }

        return requerimiento;
    }

    private void validarPermiso(User user) throws Exception {
        if (user == null
                || !PermissionUtil.userContainsRole(
                        user,
                        WebKeysCompras.ROL_COTIZAR_COMPRAS
                )) {

            throw new Exception(
                    "No posee permisos para consultar prestadores "
                            + "habilitados para cotizar."
            );
        }
    }
}
