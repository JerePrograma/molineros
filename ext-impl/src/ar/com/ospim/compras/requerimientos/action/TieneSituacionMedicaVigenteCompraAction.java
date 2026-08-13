package ar.com.ospim.compras.requerimientos.action;

import ar.com.ospim.compras.WebKeysCompras;
import ar.com.ospim.compras.requerimientos.service
        .BusquedaRequerimientoCompraServiceUtil;
import ar.com.ospim.util.PermissionUtil;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.JSONAction;
import com.liferay.portal.util.PortalUtil;

import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

public class TieneSituacionMedicaVigenteCompraAction
        extends JSONAction {

    private static final Log _log =
            LogFactoryUtil.getLog(
                    TieneSituacionMedicaVigenteCompraAction.class
            );

    private static final Pattern CUIL_PATTERN =
            Pattern.compile(
                    "^[0-9]{11}$"
            );

    @Override
    public String getJSON(
            ActionMapping mapping,
            ActionForm form,
            HttpServletRequest request,
            HttpServletResponse response)
            throws Exception {

        /*
         * Fail-closed:
         * solamente se devuelve true cuando la existencia
         * de la situación médica vigente pudo confirmarse.
         */
        boolean tieneSituacionMedica =
                false;

        try {
            User user =
                    PortalUtil.getUser(
                            request
                    );

            validarPermisoConsulta(
                    user
            );

            String cuilTitular =
                    normalizar(
                            request.getParameter(
                                    "cuil_titular"
                            )
                    );

            String inteRaw =
                    normalizar(
                            request.getParameter(
                                    "inte"
                            )
                    );

            if (cuilTitular == null
                    || !CUIL_PATTERN
                    .matcher(
                            cuilTitular
                    )
                    .matches()) {

                return construirRespuesta(
                        false
                );
            }

            if (inteRaw == null
                    || !inteRaw.matches(
                    "^[0-9]+$"
            )) {

                return construirRespuesta(
                        false
                );
            }

            int inte =
                    Integer.parseInt(
                            inteRaw
                    );

            if (inte < 0) {
                return construirRespuesta(
                        false
                );
            }

            tieneSituacionMedica =
                    BusquedaRequerimientoCompraServiceUtil
                            .tieneSituacionMedicaVigente(
                                    cuilTitular,
                                    inte
                            );

        } catch (Exception e) {
            /*
             * No exponer información técnica ni clínica.
             * Si la consulta falla, el botón permanece oculto.
             */
            _log.warn(
                    "No se pudo confirmar la existencia de "
                            + "una situación médica vigente para Compras.",
                    e
            );

            tieneSituacionMedica =
                    false;
        }

        return construirRespuesta(
                tieneSituacionMedica
        );
    }

    private void validarPermisoConsulta(
            User user) throws Exception {

        if (user == null) {
            throw new Exception(
                    "No se pudo determinar el usuario."
            );
        }

        boolean puedeVer =
                PermissionUtil.userContainsRole(
                        user,
                        WebKeysCompras.ROL_VIEW_COMPRAS
                );

        boolean puedeAdministrar =
                PermissionUtil.userContainsRole(
                        user,
                        WebKeysCompras.ROL_ABM_COMPRAS
                );

        boolean puedeCotizar =
                PermissionUtil.userContainsRole(
                        user,
                        WebKeysCompras.ROL_COTIZAR_COMPRAS
                );

        if (!puedeVer
                && !puedeAdministrar
                && !puedeCotizar) {

            throw new Exception(
                    "El usuario no posee permisos de Compras."
            );
        }
    }

    private String construirRespuesta(
            boolean tieneSituacionMedica) {

        return "{ \"tieneSituacionMedica\" : "
                + (
                tieneSituacionMedica
                        ? "true"
                        : "false"
        )
                + " }";
    }

    private String normalizar(
            String value) {

        if (value == null) {
            return null;
        }

        String normalizado =
                value.trim();

        return normalizado.length() > 0
                ? normalizado
                : null;
    }
}