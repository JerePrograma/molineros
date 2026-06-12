package ar.com.ospim.prestadores.action;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;

import com.liferay.portal.security.auth.PrincipalException;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.liquidaciones.services.PrestadorServiceUtil;
import ar.com.ospim.prestadores.WebKeysPrestadores;
import ar.com.ospim.util.PermissionUtil;

import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.util.PortalUtil;

public class ActualizarSolicitarCotizacionPrestadorAction extends PrestadoresBaseAction {

    public void processAction(
            ActionMapping mapping,
            ActionForm form,
            PortletConfig portletConfig,
            ActionRequest actionRequest,
            ActionResponse actionResponse) throws Exception {

        User user = PortalUtil.getUser(actionRequest);

        boolean puedeModificar =
                PermissionUtil.userContainsRole(
                        user,
                        WebKeysPrestadores.ROL_ABM_COTIZACION
                );

        if (!puedeModificar) {
            throw new PrincipalException();
        }

        int idPrestador = ParamUtil.getInteger(actionRequest, "idPrestador");
        boolean solicitarCotizacion =
                ParamUtil.getBoolean(actionRequest, "solicitarCotizacion");

        PrestadorServiceUtil.actualizarSolicitarCotizacionPrestador(
                idPrestador,
                solicitarCotizacion,
                user
        );
    }
}