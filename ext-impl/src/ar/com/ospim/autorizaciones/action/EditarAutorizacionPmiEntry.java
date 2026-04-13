package ar.com.ospim.autorizaciones.action;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;

/**
 * <a href="EditarAutorizacionPmiEntry.java.html"><b><i>View Source</i></b></a>
 * <p>
 * Editar Autorizaciones Recetas PMI
 * 
 * @author Gustavo Fernandez
 * 
 */

public class EditarAutorizacionPmiEntry extends PortletAction {
	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {

		String fechaRecetaStr = ParamUtil.getString(renderRequest,"fechaReceta");
		String numReceta = ParamUtil.getString(renderRequest, "numReceta");
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		Date fechaReceta=new Date();
		Calendar recetaCalendar=new GregorianCalendar();
		if(null!=fechaRecetaStr){
			fechaReceta=sdf.parse(fechaRecetaStr);
			recetaCalendar.setTime(fechaReceta);
		}
		
		String observaciones = ParamUtil.getString(renderRequest,"observaciones");
		
		renderRequest.removeAttribute("numReceta");
		renderRequest.removeAttribute("fechaReceta");
		renderRequest.removeAttribute("observaciones");
		
		renderRequest.setAttribute("numReceta", numReceta);
		renderRequest.setAttribute("fechaReceta", recetaCalendar);
		renderRequest.setAttribute("observaciones", observaciones);

		return mapping.findForward("portlet.autorizaciones.editar_autorizacion_pmi");
	}

}