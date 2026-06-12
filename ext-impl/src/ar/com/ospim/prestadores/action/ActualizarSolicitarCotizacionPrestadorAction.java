package ar.com.ospim.prestadores.action;

import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.liquidaciones.services.PrestadorServiceUtil;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.Role;
import com.liferay.portal.model.User;
import com.liferay.portal.security.auth.PrincipalException;
import com.liferay.portal.service.RoleLocalServiceUtil;
import com.liferay.portal.util.PortalUtil;

public class ActualizarSolicitarCotizacionPrestadorAction extends PrestadoresBaseAction {

    private static final String ROL_COTIZACION = "COTIZACION";

    @Override
    public void processAction(
            ActionMapping mapping,
            ActionForm form,
            PortletConfig portletConfig,
            ActionRequest actionRequest,
            ActionResponse actionResponse) throws Exception {

        User user = PortalUtil.getUser(actionRequest);

        boolean puedeModificar =
                puedeModificarSolicitarCotizacion(user);

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

    private boolean puedeModificarSolicitarCotizacion(User user) {
        if (user == null) {
            return false;
        }

        try {
            boolean tieneRol =
                    RoleLocalServiceUtil.hasUserRole(
                            user.getUserId(),
                            user.getCompanyId(),
                            ROL_COTIZACION,
                            true
                    );

            if (tieneRol) {
                return true;
            }
        } catch (Exception e) {
            // seguir con fallback
        }

        try {
            List<Role> rolesUsuario = user.getRoles();

            if (rolesUsuario != null) {
                for (Role role : rolesUsuario) {
                    if (role != null &&
                            role.getName() != null &&
                            ROL_COTIZACION.equals(role.getName().trim())) {

                        return true;
                    }
                }
            }
        } catch (Exception e) {
            // sin permiso
        }

        return false;
    }
}