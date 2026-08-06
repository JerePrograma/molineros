package ar.com.ospim.autorizaciones.action;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpSession;

import ar.com.ospim.autorizaciones.exceptions.RevisionesReclamosException;
import ar.com.ospim.compras.WebKeysCompras;
import ar.com.ospim.compras.requerimientos.beans.ReclamoPrestacionalCompraContexto;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.model.User;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.autorizaciones.beans.RevisionesReclamo;
import ar.com.ospim.autorizaciones.services.WebKeysAutorizaciones;

import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

public class ListaRevisionesAction extends PortletAction {

    public void processAction(
            ActionMapping mapping,
            ActionForm form,
            PortletConfig portletConfig,
            ActionRequest actionRequest,
            ActionResponse actionResponse) throws Exception {

        HttpSession session =
                PortalUtil
                        .getHttpServletRequest(
                                actionRequest
                        )
                        .getSession();

        try {
            RevisionesReclamo revision =
                    construirRevision(
                            actionRequest
                    );

            synchronized (session) {
                validarContextoComprasSiCorresponde(
                        session,
                        actionRequest
                );

                @SuppressWarnings("unchecked")
                List<RevisionesReclamo> revisiones =
                        (List<RevisionesReclamo>)
                                session.getAttribute(
                                        WebKeysAutorizaciones
                                                .LISTADO_REVISIONES_RECLAMOS_EN_SESION
                                );

                if (revisiones == null) {
                    revisiones =
                            new ArrayList<RevisionesReclamo>();
                }

                if (tieneRevisionActiva(
                        revisiones
                )) {
                    throw new RevisionesReclamosException(
                            "El reclamo ya posee "
                                    + "una revision activa."
                    );
                }

                revision.setId(
                        obtenerIdTemporal(
                                revisiones
                        )
                );

                revision.setEstado(
                        RevisionesReclamo.ESTADOS.NUEVO
                );

                revisiones.add(
                        revision
                );

                session.setAttribute(
                        WebKeysAutorizaciones
                                .LISTADO_REVISIONES_RECLAMOS_EN_SESION,
                        revisiones
                );
            }

            actionResponse.setRenderParameter(
                    "revisionOperacionOk",
                    "1"
            );

        } catch (RevisionesReclamosException e) {
            SessionErrors.add(
                    actionRequest,
                    e.getClass().getName()
            );

            actionResponse.setRenderParameter(
                    "revisionOperacionOk",
                    "0"
            );
        }
    }

    public ActionForward render(
            ActionMapping mapping,
            ActionForm form,
            PortletConfig portletConfig,
            RenderRequest renderRequest,
            RenderResponse renderResponse) throws Exception {

        return mapping.findForward(
                getForward(
                        renderRequest,
                        "portlet.autorizaciones."
                                + "reclamosprestacionales."
                                + "revision.reclamo"
                )
        );
    }

    private RevisionesReclamo construirRevision(
            ActionRequest actionRequest) throws Exception {

        RevisionesReclamo revision =
                new RevisionesReclamo();

        String dia =
                ParamUtil.getString(
                        actionRequest,
                        "fechaRevisionDay"
                );

        String mes =
                ParamUtil.getString(
                        actionRequest,
                        "fechaRevisionMonth"
                );

        String anio =
                ParamUtil.getString(
                        actionRequest,
                        "fechaRevisionYear"
                );

        if (!esVacio(dia)
                && !esVacio(mes)
                && !esVacio(anio)) {

            revision.setFecha_revision(
                    parsearFecha(
                            dia,
                            mes,
                            anio
                    )
            );
        }

        revision.setUsr_presente(
                normalizar(
                        ParamUtil.getString(
                                actionRequest,
                                "usr_presente"
                        )
                )
        );

        revision.setUsr_resolucion(
                normalizar(
                        ParamUtil.getString(
                                actionRequest,
                                "usr_resolucion"
                        )
                )
        );

        revision.setUsr_responsable_resolucion(
                normalizar(
                        ParamUtil.getString(
                                actionRequest,
                                "usr_responsable_resolucion"
                        )
                )
        );

        revision.setObservacion(
                normalizar(
                        ParamUtil.getString(
                                actionRequest,
                                "observacion"
                        )
                )
        );

        validarRevision(
                revision
        );

        return revision;
    }

    private void validarRevision(
            RevisionesReclamo revision)
            throws Exception {

        if (revision == null
                || revision.getFecha_revision() == null
                || esVacio(
                revision.getUsr_presente()
        )
                || esVacio(
                revision.getUsr_resolucion()
        )
                || esVacio(
                revision
                        .getUsr_responsable_resolucion()
        )) {

            throw new RevisionesReclamosException(
                    "Debe informar fecha, presentes, resolucion "
                            + "y responsable de resolucion."
            );
        }
    }

    private boolean tieneRevisionActiva(
            List<RevisionesReclamo> revisiones) {

        if (revisiones == null) {
            return false;
        }

        for (RevisionesReclamo revision : revisiones) {
            if (revision == null) {
                continue;
            }

            if (!RevisionesReclamo.ESTADOS.BAJA.equals(
                    revision.getEstado()
            )) {
                return true;
            }
        }

        return false;
    }

    private int obtenerIdTemporal(
            List<RevisionesReclamo> revisiones) {

        int idTemporal =
                -1;

        if (revisiones == null) {
            return idTemporal;
        }

        for (RevisionesReclamo revision : revisiones) {
            if (revision != null
                    && revision.getId() <= idTemporal) {

                idTemporal =
                        revision.getId() - 1;
            }
        }

        return idTemporal;
    }

    private Date parsearFecha(
            String dia,
            String mes,
            String anio) throws Exception {

        SimpleDateFormat formato =
                new SimpleDateFormat(
                        "dd/MM/yyyy"
                );

        formato.setLenient(
                false
        );

        return formato.parse(
                dia
                        + "/"
                        + (Integer.parseInt(mes) + 1)
                        + "/"
                        + anio
        );
    }

    private String normalizar(
            String valor) {

        if (valor == null) {
            return null;
        }

        valor =
                valor.trim();

        return valor.length() == 0
                ? null
                : valor;
    }

    private boolean esVacio(
            String valor) {

        return valor == null
                || valor.trim().length() == 0;
    }

    private void validarContextoComprasSiCorresponde(
            HttpSession session,
            ActionRequest actionRequest)
            throws RevisionesReclamosException {

        String nonceRequest =
                ParamUtil.getString(
                        actionRequest,
                        WebKeysCompras
                                .PARAM_RECLAMO_PRESTACIONAL_NONCE,
                        ""
                );

        Object contextoObj =
                session.getAttribute(
                        WebKeysCompras
                                .CONTEXTO_RECLAMO_PRESTACIONAL_COMPRA
                );

        /*
         * Sin contexto y sin nonce se conserva el flujo manual.
         * Si existe un contexto de Compras, el nonce es obligatorio.
         */
        if (WebKeysCompras.isEmpty(
                nonceRequest
        )) {
            if (contextoObj != null) {
                throw new RevisionesReclamosException(
                        "El contexto de Compras requiere "
                                + "un nonce valido."
                );
            }

            return;
        }

        if (!(contextoObj
                instanceof ReclamoPrestacionalCompraContexto)) {

            throw new RevisionesReclamosException(
                    "El contexto de Compras expiro "
                            + "o ya no esta disponible."
            );
        }

        ReclamoPrestacionalCompraContexto contexto =
                (ReclamoPrestacionalCompraContexto)
                        contextoObj;

        User user;

        try {
            user =
                    PortalUtil.getUser(
                            actionRequest
                    );
        } catch (Exception e) {
            throw new RevisionesReclamosException(
                    "No se pudo determinar "
                            + "el usuario actual.",
                    e
            );
        }

        String usuario =
                user != null
                        ? user.getScreenName()
                        : "";

        if (!contexto.coincideNonce(
                nonceRequest
        )
                || !contexto.perteneceAUsuario(
                usuario
        )
                || !contexto.estaVigente(
                System.currentTimeMillis()
        )) {

            throw new RevisionesReclamosException(
                    "El contexto de Compras no es valido "
                            + "o vencio."
            );
        }
    }
}