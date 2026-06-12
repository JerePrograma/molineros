package ar.com.ospim.prestadores.action;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.liquidaciones.services.PrestadorServiceUtil;
import ar.com.ospim.prestadores.WebKeysPrestadores;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.security.auth.PrincipalException;
import com.liferay.portal.service.RoleLocalServiceUtil;
import com.liferay.portal.util.PortalUtil;

public class ActualizarSolicitarCotizacionPrestadorAction extends PrestadoresBaseAction {

    @Override
    public void processAction(
            ActionMapping mapping,
            ActionForm form,
            PortletConfig portletConfig,
            ActionRequest actionRequest,
            ActionResponse actionResponse) throws Exception {

        User user = PortalUtil.getUser(actionRequest);

        boolean puedeModificar =
                RoleLocalServiceUtil.hasUserRole(
                        user.getUserId(),
                        user.getCompanyId(),
                        WebKeysPrestadores.ROL_ABM_COTIZACION,
                        true
                );

        if (!puedeModificar) {
            throw new PrincipalException();
        }

        int idPrestador = ParamUtil.getInteger(actionRequest, "idPrestador");

        if (idPrestador <= 0) {
            throw new IllegalArgumentException("idPrestador inválido: " + idPrestador);
        }

        boolean solicitarCotizacion =
                ParamUtil.getBoolean(actionRequest, "solicitarCotizacion");

        int actualizados =
                PrestadorServiceUtil.actualizarSolicitarCotizacionPrestador(
                        idPrestador,
                        solicitarCotizacion,
                        user
                );

        if (actualizados != 1) {
            throw new SystemException(
                    "No se actualizó solicitar_cotizacion para idPrestador=" + idPrestador +
                            ". Filas actualizadas=" + actualizados
            );
        }
    }
}