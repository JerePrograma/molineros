package ar.com.ospim.autorizaciones.action;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.autorizaciones.beans.RevisionesReclamo;
import ar.com.ospim.autorizaciones.exceptions.RevisionesReclamosException;
import ar.com.ospim.autorizaciones.services.WebKeysAutorizaciones;
import ar.com.ospim.global.beans.Provincia;
import ar.com.ospim.liquidaciones.WebKeysLiquidaciones;
import ar.com.ospim.prestadores.action.ListaMatriculasAction;
import ar.com.ospim.prestadores.exception.MatriculaNacionalPrestadorException;
import ar.com.ospim.prestadores.exception.MatriculaProvincialPrestadorException;
import ar.com.ospim.liquidaciones.beans.MatriculaPrestador;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;


public class ListaRevisionesAction extends PortletAction {	
	
	private static Log _log = LogFactoryUtil.getLog(ListaRevisionesAction.class);

	public ActionForward render(ActionMapping mapping, ActionForm form, PortletConfig portletConfig,
			RenderRequest renderRequest, RenderResponse renderResponse) throws Exception {
		
		HttpSession session = (HttpSession) PortalUtil.getHttpServletRequest(renderRequest).getSession();
		
		String resolucion = ParamUtil.getString(renderRequest, "resolucion");		
		String presentes = ParamUtil.getString(renderRequest, "presentes");
		String respresolucion = ParamUtil.getString(renderRequest, "respresolucion");		
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		SimpleDateFormat formatoDePeriodo = new SimpleDateFormat("dd/MM/yyyy");
		formatoDePeriodo.setLenient(false);

		String revisionFechaVtoDia = ParamUtil.getString(renderRequest,"revisionFechaVtoDia");
		String revisionFechaVtoMes = ParamUtil.getString(renderRequest,"revisionFechaVtoMes");
		String revisionFechaVtoAnio = ParamUtil.getString(renderRequest,"revisionFechaVtoAnio");
		int idObservacionMedica = ParamUtil.getInteger(renderRequest,"observacionMedica",0);

		
		Date fechaRevision = null;		
		
		/*
		  boolean superintendencia= ParamUtil.getBoolean(renderRequest,"reclamosuperintendencia");
		 
		boolean  recuperable= ParamUtil.getBoolean(renderRequest,"reclamorecuperable");
		boolean  amparo= ParamUtil.getBoolean(renderRequest,"reclamoamparo");			   		   
		 */  
		String observacion=ParamUtil.getString(renderRequest,"reclamoobservacion");
		
		
			try {
				fechaRevision = formatoDePeriodo.parse(revisionFechaVtoDia + "/"
						+ (Integer.parseInt(revisionFechaVtoMes) + 1) + "/"
						+ revisionFechaVtoAnio);
			} catch (Exception e) {
				fechaRevision = null;
			}

		if (fechaRevision == null || resolucion.trim().length() == 0) {
			agregarErrorRevision(
					renderRequest,
					"La revision no contiene una fecha y resolucion validas."
			);
			return mapping.findForward(getForward(renderRequest,
					"portlet.autorizaciones.reclamosprestacionales.revision.reclamo"));
		}

		synchronized (session) {
			@SuppressWarnings("unchecked")
			List<RevisionesReclamo> revisionesActuales =
					(List<RevisionesReclamo>) session.getAttribute(
							WebKeysAutorizaciones.LISTADO_REVISIONES_RECLAMOS_EN_SESION
					);

			if (tieneRevisionActiva(revisionesActuales)) {
				agregarErrorRevision(
						renderRequest,
						"El reclamo ya contiene una revision activa."
				);
				return mapping.findForward(getForward(renderRequest,
						"portlet.autorizaciones.reclamosprestacionales.revision.reclamo"));
			}

//			me aseguro sea un numero negativo para no confundir con IDs de BD
			Random r = new Random(System.currentTimeMillis());
			int idAux = r.nextInt();
			if(idAux >= 0){
				idAux = idAux == 0 ? -1 : (-1)*idAux;
			}

			RevisionesReclamo revreclamo = new RevisionesReclamo(
					fechaRevision, presentes, resolucion, respresolucion, observacion
			);
			revreclamo.setEstado(RevisionesReclamo.ESTADOS.NUEVO);
			revreclamo.setId(idAux);

			List<RevisionesReclamo> revisionesreclamo =
					revisionesActuales == null
							? new ArrayList<RevisionesReclamo>()
							: new ArrayList<RevisionesReclamo>(revisionesActuales);

			revisionesreclamo.add(revreclamo);
			session.setAttribute(
					WebKeysAutorizaciones.LISTADO_REVISIONES_RECLAMOS_EN_SESION,
					revisionesreclamo
			);

			_log.debug("Agrega revision : " + revreclamo.toString());
		}
		
//		return mapping.findForward("portlet.liquidaciones.matricula.prestador");
		return mapping.findForward(getForward(renderRequest,
				"portlet.autorizaciones.reclamosprestacionales.revision.reclamo"));
	}
	
			
	private boolean tieneRevisionActiva(List<RevisionesReclamo> revisiones) {
		if (revisiones == null) {
			return false;
		}

		for (RevisionesReclamo revision : revisiones) {
			if (revision != null
					&& (revision.getEstado() == null
					|| !RevisionesReclamo.ESTADOS.BAJA.equals(revision.getEstado()))) {
				return true;
			}
		}

		return false;
	}

	private void agregarErrorRevision(RenderRequest renderRequest, String mensaje) {
		SessionErrors.add(
				renderRequest,
				RevisionesReclamosException.class.getName(),
				new RevisionesReclamosException(mensaje)
		);
	}

}