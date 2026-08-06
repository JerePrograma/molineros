package ar.com.ospim.compras.requerimientos.action;

import ar.com.ospim.compras.WebKeysCompras;

import ar.com.ospim.compras.requerimientos.beans.NotificacionCotizacionResultado;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompra;

import ar.com.ospim.compras.requerimientos.service.BusquedaRequerimientoCompraServiceUtil;
import ar.com.ospim.compras.requerimientos.service.EditarRequerimientoCompraServiceUtil;

import ar.com.ospim.util.PermissionUtil;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;

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

public class CambiarEstadoRequerimientoCompraAction
        extends PortletAction {

    private static final Log _log =
            LogFactoryUtil.getLog(
                    CambiarEstadoRequerimientoCompraAction.class
            );

    private static final String
            STRUTS_ACTION_VER_REQUERIMIENTO =
            "/compras/ver_requerimiento";

    private static final String
            STRUTS_ACTION_EDITAR_REQUERIMIENTO =
            "/compras/editar_requerimiento";

    public void processAction(
            ActionMapping mapping,
            ActionForm form,
            PortletConfig portletConfig,
            ActionRequest actionRequest,
            ActionResponse actionResponse)
            throws Exception {

        int idRequerimientoCompra =
                ParamUtil.getInteger(
                        actionRequest,
                        "id_requerimiento_compra",
                        0
                );

        int estadoNuevo =
                ParamUtil.getInteger(
                        actionRequest,
                        "estado_nuevo",
                        0
                );

        boolean reintentarNotificaciones =
                ParamUtil.getBoolean(
                        actionRequest,
                        "reintentar_notificaciones",
                        false
                );

        String strutsActionDestino =
                STRUTS_ACTION_VER_REQUERIMIENTO;

        String forwardDestino =
                WebKeysCompras
                        .FORWARD_COMPRAS_VER_REQUERIMIENTO;

        try {
            User user =
                    PortalUtil.getUser(
                            actionRequest
                    );

            String usuario =
                    user != null
                            ? user.getScreenName()
                            : "sistema";

            long companyId =
                    PortalUtil.getCompanyId(
                            actionRequest
                    );

            if (reintentarNotificaciones) {
                validarRolCotizar(
                        user
                );

                NotificacionCotizacionResultado resultado =
                        EditarRequerimientoCompraServiceUtil
                                .reintentarNotificacionesCotizacion(
                                        idRequerimientoCompra,
                                        usuario,
                                        companyId
                                );

                int estadoPersistido =
                        getEstadoPersistido(
                                idRequerimientoCompra
                        );

                registrarResultado(
                        actionRequest,
                        resultado,
                        false,
                        estadoPersistido
                );

                if (estadoPersistido
                        == WebKeysCompras.ESTADO_A_COTIZAR) {

                    strutsActionDestino =
                            STRUTS_ACTION_EDITAR_REQUERIMIENTO;

                    forwardDestino =
                            WebKeysCompras
                                    .FORWARD_COMPRAS_EDITAR_REQUERIMIENTO;
                }

            } else if (estadoNuevo
                    == WebKeysCompras.ESTADO_A_COTIZAR) {

                validarRolCotizar(
                        user
                );

                NotificacionCotizacionResultado resultado =
                        EditarRequerimientoCompraServiceUtil
                                .enviarACotizar(
                                        idRequerimientoCompra,
                                        usuario,
                                        companyId
                                );

                int estadoPersistido =
                        getEstadoPersistido(
                                idRequerimientoCompra
                        );

                registrarResultado(
                        actionRequest,
                        resultado,
                        true,
                        estadoPersistido
                );

                if (estadoPersistido
                        == WebKeysCompras.ESTADO_A_COTIZAR) {

                    strutsActionDestino =
                            STRUTS_ACTION_EDITAR_REQUERIMIENTO;

                    forwardDestino =
                            WebKeysCompras
                                    .FORWARD_COMPRAS_EDITAR_REQUERIMIENTO;
                }

            } else if (estadoNuevo
                    == WebKeysCompras.ESTADO_ANULADO) {

                validarRolAnular(
                        user
                );

                EditarRequerimientoCompraServiceUtil
                        .cambiarEstado(
                                idRequerimientoCompra,
                                WebKeysCompras.ESTADO_ANULADO,
                                usuario
                        );

                SessionMessages.add(
                        actionRequest,
                        "requerimiento-compra-anulado"
                );

            } else {
                throw new Exception(
                        "La transición de estado solicitada "
                                + "no es válida."
                );
            }

        } catch (Exception e) {
            _log.error(
                    "Error procesando estado del requerimiento "
                            + "de compra. id="
                            + idRequerimientoCompra
                            + ", estadoNuevo="
                            + estadoNuevo
                            + ", reintentarNotificaciones="
                            + reintentarNotificaciones,
                    e
            );

            SessionErrors.add(
                    actionRequest,
                    "estado-requerimiento-compra-error"
            );

            actionRequest.setAttribute(
                    WebKeysCompras.ERROR_PARA_ALERT,
                    mensajeError(
                            e
                    )
            );
        }

        actionResponse.setRenderParameter(
                "struts_action",
                strutsActionDestino
        );

        if (idRequerimientoCompra > 0) {
            actionResponse.setRenderParameter(
                    "id_requerimiento_compra",
                    String.valueOf(
                            idRequerimientoCompra
                    )
            );
        }

        setForward(
                actionRequest,
                forwardDestino
        );
    }

    public ActionForward render(
            ActionMapping mapping,
            ActionForm form,
            PortletConfig portletConfig,
            RenderRequest renderRequest,
            RenderResponse renderResponse)
            throws Exception {

        return mapping.findForward(
                WebKeysCompras
                        .FORWARD_COMPRAS_VER_REQUERIMIENTO
        );
    }

    private void registrarResultado(
            ActionRequest actionRequest,
            NotificacionCotizacionResultado resultado,
            boolean cambiaAAcotizar,
            int estadoPersistido) {

        if (resultado == null) {
            SessionMessages.add(
                    actionRequest,
                    "cotizacion-prestadores-sin-resultado"
            );

            return;
        }

        /*
         * El objeto completo queda disponible durante el render
         * para mostrar el resumen y el detalle por prestador.
         */
        SessionMessages.add(
                actionRequest,
                WebKeysCompras
                        .RESULTADO_NOTIFICACION_COTIZACION,
                resultado
        );

        boolean hayEnviados =
                resultado.getEnviados() > 0;

        boolean hayEmailsInvalidos =
                resultado.getEmailsInvalidos() > 0;

        boolean hayErroresTecnicos =
                resultado.getErrores() > 0
                        || resultado
                        .getPendientesSinClasificar() > 0;

        boolean hayOmitidos =
                resultado.getOmitidos() > 0;

        if (!hayEnviados) {

            /*
             * Estos dos diagnósticos describen situaciones
             * anteriores al procesamiento individual.
             */
            if (sinCompatiblesSector(
                    resultado
            )) {
                SessionMessages.add(
                        actionRequest,
                        "cotizacion-prestadores-sin-compatibles-sector"
                );

                return;
            }

            if (todosBloqueadosPorEstadoPrevio(
                    resultado
            )) {
                SessionMessages.add(
                        actionRequest,
                        "cotizacion-prestadores-todos-omitidos-previos"
                );

                return;
            }

            /*
             * No usar else-if entre email inválido y error técnico.
             *
             * Una misma ejecución puede contener ambas causas
             * y ambas deben quedar evidenciadas.
             */
            if (hayEmailsInvalidos) {
                SessionMessages.add(
                        actionRequest,
                        "cotizacion-prestadores-emails-invalidos"
                );
            }

            if (hayErroresTecnicos) {
                SessionMessages.add(
                        actionRequest,
                        "cotizacion-prestadores-errores-envio"
                );
            }

            /*
             * Si no hubo error ni email inválido, la ausencia
             * de envíos no debe presentarse como una falla.
             *
             * Puede deberse a omisiones por concurrencia o
             * a que no existían candidatos procesables.
             */
            if (!hayEmailsInvalidos
                    && !hayErroresTecnicos) {

                if (hayOmitidos
                        || estadoPersistido
                        != WebKeysCompras.ESTADO_PENDIENTE) {

                    SessionMessages.add(
                            actionRequest,
                            "cotizacion-prestadores-sin-nuevos-envios"
                    );

                } else {
                    SessionMessages.add(
                            actionRequest,
                            "cotizacion-prestadores-no-enviados"
                    );
                }
            }

            return;
        }

        /*
         * Si hubo al menos un envío, los omitidos no convierten
         * por sí solos el resultado en error.
         *
         * Los omitidos se muestran como información en el JSP.
         */
        if (hayEmailsInvalidos
                || hayErroresTecnicos) {

            boolean cambioAAcotizarConfirmado =
                    cambiaAAcotizar
                            && estadoPersistido
                            == WebKeysCompras.ESTADO_A_COTIZAR;

            SessionMessages.add(
                    actionRequest,
                    cambioAAcotizarConfirmado
                            ? "requerimiento-compra-enviado-a-cotizar-con-errores"
                            : "cotizacion-prestadores-notificados-con-errores"
            );

            return;
        }

        boolean cambioAAcotizarConfirmado =
                cambiaAAcotizar
                        && estadoPersistido
                        == WebKeysCompras.ESTADO_A_COTIZAR;

        if (cambioAAcotizarConfirmado) {
            SessionMessages.add(
                    actionRequest,
                    "requerimiento-compra-enviado-a-cotizar"
            );

        } else {
            SessionMessages.add(
                    actionRequest,
                    "cotizacion-prestadores-notificados"
            );
        }
    }

    private boolean sinCompatiblesSector(
            NotificacionCotizacionResultado resultado) {

        if (resultado == null) {
            return false;
        }

        return resultado.getTotalCandidatos() <= 0
                && resultado.getPrestadoresHabilitados() > 0
                && resultado
                .getPrestadoresCompatiblesSector() <= 0;
    }

    private boolean todosBloqueadosPorEstadoPrevio(
            NotificacionCotizacionResultado resultado) {

        if (resultado == null) {
            return false;
        }

        return resultado.getTotalCandidatos() <= 0
                && resultado
                .getPrestadoresCompatiblesSector() > 0
                && resultado
                .getPrestadoresBloqueadosEstadoPrevio()
                >= resultado
                .getPrestadoresCompatiblesSector();
    }

    private int getEstadoPersistido(
            int idRequerimientoCompra)
            throws Exception {

        RequerimientoCompra requerimiento =
                BusquedaRequerimientoCompraServiceUtil
                        .getRequerimientoCompra(
                                idRequerimientoCompra
                        );

        if (requerimiento == null) {
            throw new Exception(
                    "No se encontró el requerimiento "
                            + "de compra informado."
            );
        }

        return requerimiento.getEstado();
    }

    private void validarRolCotizar(
            User user)
            throws Exception {

        if (user == null
                || !PermissionUtil.userContainsRole(
                user,
                WebKeysCompras.ROL_COTIZAR_COMPRAS
        )) {
            throw new Exception(
                    "No posee permisos para cotizar "
                            + "requerimientos de compras."
            );
        }
    }

    private void validarRolAnular(
            User user)
            throws Exception {

        if (user == null
                || !PermissionUtil.userContainsRole(
                user,
                WebKeysCompras.ROL_ANULAR_COMPRAS
        )) {
            throw new Exception(
                    "No posee permisos para anular "
                            + "requerimientos de compras."
            );
        }
    }

    private String mensajeError(
            Exception e) {

        if (e == null
                || WebKeysCompras.isEmpty(
                e.getMessage()
        )) {
            return "No se pudo procesar "
                    + "el requerimiento de compra.";
        }

        return e.getMessage();
    }
}