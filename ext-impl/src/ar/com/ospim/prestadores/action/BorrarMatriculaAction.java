package ar.com.ospim.prestadores.action;

import java.util.ArrayList;
import java.util.List;

import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.liquidaciones.WebKeysLiquidaciones;
import ar.com.ospim.liquidaciones.beans.MatriculaPrestador;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

/**
 * @author SVA
 */

public class BorrarMatriculaAction extends PortletAction {
	private static Log _log = LogFactoryUtil.getLog(BorrarMatriculaAction.class);

	public ActionForward render(
			ActionMapping mapping, ActionForm form, PortletConfig portletConfig,
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws Exception {
		
		HttpSession session = (HttpSession) PortalUtil.getHttpServletRequest(renderRequest).getSession();
		
		int idMatricula = ParamUtil.getInteger(renderRequest, "idMatricula");
		MatriculaPrestador mat = new MatriculaPrestador();
		mat.setIdMatricula(idMatricula);
		
		_log.debug("Borrando matricula id: " + idMatricula);
		
		List<MatriculaPrestador> listaMatriculasPrestador = (ArrayList<MatriculaPrestador>) session.getAttribute(WebKeysLiquidaciones.MATRICULAS_PRESTADOR_EN_SESSION);
		
		int pos = listaMatriculasPrestador.indexOf(mat);
//		reemplazo por el objeto de la lista
		mat = listaMatriculasPrestador.get(pos);
		if(mat.getEstado()==null){ // esta matricula esta en BD
			mat.setEstado(MatriculaPrestador.ESTADOS.BAJA);
		}else{
			listaMatriculasPrestador.remove(pos);
		}	
		session.removeAttribute(WebKeysLiquidaciones.MATRICULAS_PRESTADOR_EN_SESSION);
		session.setAttribute(WebKeysLiquidaciones.MATRICULAS_PRESTADOR_EN_SESSION, listaMatriculasPrestador);
		
		return mapping.findForward("portlet.liquidaciones.matricula.prestador");
	}
		
}