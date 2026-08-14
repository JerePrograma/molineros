package ar.com.ospim.compras.requerimientos.action;

import ar.com.ospim.autorizaciones.beans
        .BusquedaSituacionMedicaFiltro;
import ar.com.ospim.autorizaciones.beans
        .ItemSituacionMedicaTotal;
import ar.com.ospim.autorizaciones.services
        .SituacionesMedicasServiceUtil;
import ar.com.ospim.compras.WebKeysCompras;
import ar.com.ospim.util.PermissionUtil;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class VerSituacionMedicaVigenteCompraAction
        extends PortletAction {

    private static final Log _log =
            LogFactoryUtil.getLog(
                    VerSituacionMedicaVigenteCompraAction.class
            );

    private static final Pattern CUIL_PATTERN =
            Pattern.compile(
                    "^[0-9]{11}$"
            );

    private static final Pattern INTE_PATTERN =
            Pattern.compile(
                    "^[0-9]+$"
            );

    @Override
    public ActionForward render(
            ActionMapping mapping,
            ActionForm form,
            PortletConfig portletConfig,
            RenderRequest renderRequest,
            RenderResponse renderResponse)
            throws Exception {

        List<ItemSituacionMedicaTotal> situaciones =
                new ArrayList<ItemSituacionMedicaTotal>();

        try {
            HttpServletRequest httpRequest =
                    PortalUtil.getHttpServletRequest(
                            renderRequest
                    );

            User user =
                    PortalUtil.getUser(
                            httpRequest
                    );

            validarPermisoConsulta(
                    user
            );

            String cuilTitular =
                    normalizar(
                            renderRequest.getParameter(
                                    "cuil_titular"
                            )
                    );

            String inteRaw =
                    normalizar(
                            renderRequest.getParameter(
                                    "inte"
                            )
                    );

            if (cuilTitular == null
                    || !CUIL_PATTERN
                    .matcher(cuilTitular)
                    .matches()) {

                throw new Exception(
                        "CUIL inválido para consultar Situación Médica."
                );
            }

            if (inteRaw == null
                    || !INTE_PATTERN
                    .matcher(inteRaw)
                    .matches()) {

                throw new Exception(
                        "Integrante inválido para consultar Situación Médica."
                );
            }

            int inte =
                    Integer.parseInt(
                            inteRaw
                    );

            if (inte < 0) {
                throw new Exception(
                        "Integrante inválido para consultar Situación Médica."
                );
            }

            Date fechaReferencia =
                    new Date();

            BusquedaSituacionMedicaFiltro filtro =
                    new BusquedaSituacionMedicaFiltro(
                            fechaReferencia,
                            null,
                            inte,
                            cuilTitular,
                            0,
                            0
                    );

            List<ItemSituacionMedicaTotal> resultado =
                    SituacionesMedicasServiceUtil
                            .buscarSituacionesMedicasVigente(
                                    filtro
                            );

            /*
             * Filtro defensivo adicional.
             *
             * Conserva la misma semántica utilizada por Compras
             * para decidir si debe mostrar el botón:
             *
             *   baja_fecha IS NULL
             *
             * y
             *
             *   vigen_hasta IS NULL
             *   OR vigen_hasta > CURRENT_DATE
             */
            if (resultado != null) {

                for (int i = 0;
                     i < resultado.size();
                     i++) {

                    ItemSituacionMedicaTotal situacion =
                            resultado.get(i);

                    if (situacion == null) {
                        continue;
                    }

                    if (situacion.getBaja_fecha() != null) {
                        continue;
                    }

                    Date fechaHasta =
                            situacion.getFechaVigen_Hasta();

                    if (fechaHasta != null
                            && !fechaHasta.after(
                            fechaReferencia
                    )) {

                        continue;
                    }

                    situaciones.add(
                            situacion
                    );
                }
            }

        } catch (Exception e) {

            /*
             * No registrar CUIL ni información clínica.
             */
            _log.warn(
                    "No se pudo recuperar la Situación Médica "
                            + "vigente solicitada desde Compras.",
                    e
            );

            situaciones.clear();
        }

        renderRequest.setAttribute(
                WebKeysCompras
                        .SITUACIONES_MEDICAS_VIGENTES_COMPRA,
                situaciones
        );

        return mapping.findForward(
                WebKeysCompras
                        .FORWARD_COMPRAS_SITUACION_MEDICA_VIGENTE
        );
    }

    private void validarPermisoConsulta(
            User user)
            throws Exception {

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