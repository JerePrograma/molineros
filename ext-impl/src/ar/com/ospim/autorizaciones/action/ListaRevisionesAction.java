package ar.com.ospim.autorizaciones.action;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.autorizaciones.beans.RevisionesReclamo;
import ar.com.ospim.autorizaciones.services.WebKeysAutorizaciones;
import ar.com.ospim.global.beans.Provincia;
import ar.com.ospim.liquidaciones.WebKeysLiquidaciones;
import ar.com.ospim.prestadores.action.ListaMatriculasAction;
import ar.com.ospim.prestadores.exception.MatriculaNacionalPrestadorException;
import ar.com.ospim.prestadores.exception.MatriculaProvincialPrestadorException;
import ar.com.ospim.liquidaciones.beans.MatriculaPrestador;
import ar.com.ospim.util.PermissionUtil;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;


public class ListaRevisionesAction extends PortletAction {	
	
	private static Log _log = LogFactoryUtil.getLog(ListaRevisionesAction.class);

    public void processAction(
            ActionMapping mapping,
            ActionForm form,
            PortletConfig portletConfig,
            ActionRequest actionRequest,
            ActionResponse actionResponse) throws Exception {

        HttpSession session =
                (HttpSession) PortalUtil
                        .getHttpServletRequest(
                                actionRequest
                        )
                        .getSession();

        User user =
                PortalUtil.getUser(
                        actionRequest
                );

        if (user == null) {
            throw new Exception(
                    "Debe iniciar sesión para agregar una revisión."
            );
        }

        if (!PermissionUtil.userContainsRole(
                user,
                WebKeysAutorizaciones
                        .ROL_ABM_RECLAMOS_PRESTACIONALES
        )) {
            throw new Exception(
                    "No posee permiso para agregar revisiones."
            );
        }

        validarContextoAltaCompra(
                session,
                actionRequest,
                user
        );

        RevisionesReclamo revision =
                construirRevision(
                        actionRequest
                );

        synchronized (session) {
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

            if (tieneRevisionActiva(revisiones)) {
                throw new Exception(
                        "El reclamo ya posee una revisión activa."
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

}