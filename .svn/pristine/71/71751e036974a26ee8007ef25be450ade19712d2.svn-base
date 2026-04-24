/**
 */

package ar.com.ospim.afiliados.empleadores.action;

import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.afiliados.WebKeysAfiliados;
import ar.com.ospim.global.beans.Empresa;
import ar.com.ospim.global.services.EmpresaServiceUtil;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;


/**
 * <a href="BuscarEmpleadoresAction.java.html"><b><i>View Source</i></b></a>
 * <p>
 * Realiza la búsqueda de empleadores según parámetros de entrada
 * 
 * @author Martin Moreyra
 * 
 */
public class BuscarEmpleadoresAction extends EmpleadoresBaseAction {
	private static Log _log = LogFactoryUtil
			.getLog(BuscarEmpleadoresAction.class);

	public void processAction(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse) throws Exception {
		setForward(actionRequest, "portlet.afiliados.empleadores.result.search");

	}

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		try {
			String cuit = null;
			String sucu = null;
			String descripcion = null;
			String estado = null;
			int idSeccional=0;

			if (null != renderRequest.getParameter("cuit")) {
				cuit = renderRequest.getParameter("cuit").trim().length() > 0 ? renderRequest
						.getParameter("cuit")
						: null;
			}
			if (null != renderRequest.getParameter("sucu")) {
				sucu = renderRequest.getParameter("sucu").trim().length() > 0 ? renderRequest
						.getParameter("sucu")
						: null;
			}			
			if (null != renderRequest.getParameter("descripcion")) {
				descripcion = renderRequest.getParameter("descripcion").trim().length() > 0 ? renderRequest
						.getParameter("descripcion")
						: null;
			}
			if (null != renderRequest.getParameter("estado")) {
				estado = renderRequest.getParameter("estado").trim().length() > 0 ? renderRequest
						.getParameter("estado")
						: null;
			}
			idSeccional=ParamUtil.getInteger(renderRequest, "id_seccional");
			_log.debug("cuit: " + cuit);
			_log.debug("sucu: " + sucu);
			_log.debug("descripcion: " + descripcion);
			_log.debug("estado: " + estado);
			
			List<Empresa> busqueda = EmpresaServiceUtil
					.getEmpleadores(cuit,descripcion, sucu, idSeccional);
			renderRequest.removeAttribute(WebKeysAfiliados.BUSQUEDA_EMPLEADORES);
			renderRequest.setAttribute(WebKeysAfiliados.BUSQUEDA_EMPLEADORES,
					busqueda);			
		} catch (Exception e) {
			_log.error(e);
			e.printStackTrace();
		}
		
		return mapping.findForward("portlet.afiliados.empleadores.result.search");
	}
	
}