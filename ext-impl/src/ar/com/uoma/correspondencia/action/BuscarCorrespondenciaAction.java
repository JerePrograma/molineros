package ar.com.uoma.correspondencia.action;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.global.beans.Localidad;
import ar.com.ospim.global.beans.Provincia;
import ar.com.uoma.beans.Correspondencia;
import ar.com.uoma.correspondencia.WebKeysCorrespondencia;
import ar.com.uoma.correspondencia.services.CorrespondenciaServiceImpl;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

/**
 * <a href="BuscarCorrespondenciaAction.java.html"><b><i>View Source</i></b></a>
 * <p>
 * Realiza la búsqueda de correspondencia según parámetros de entrada
 * 
 * @author Federico Brachi
 * 
 */
public class BuscarCorrespondenciaAction extends PortletAction {
	private static Log _log = LogFactoryUtil
			.getLog(BuscarCorrespondenciaAction.class);

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		
		try {
			String destino=ParamUtil.getString(renderRequest,"destino");
			String lugarRecepcion=ParamUtil.getString(renderRequest,"edificio");
			String desdeFinal=ParamUtil.getString(renderRequest,"desde_final");
			String hastaFinal=ParamUtil.getString(renderRequest,"hasta_final");
			int idCorrDesde=ParamUtil.getInteger(renderRequest,"id_corr_desde");
			int idCorrHasta=ParamUtil.getInteger(renderRequest,"id_corr_hasta");
			int tipoCorr=ParamUtil.getInteger(renderRequest, "tipoCorr");
			String remitente=ParamUtil.getString(renderRequest,"remitente");
			String destinatario=ParamUtil.getString(renderRequest,"destinatario");
			String receptor=ParamUtil.getString(renderRequest,"receptor");
			
			String razon_prestador=ParamUtil.getString(renderRequest,"razon_prestador");
			
			int provincia= ParamUtil.getInteger(renderRequest, "provinciaremi");
			int localidad= ParamUtil.getInteger(renderRequest, "localidadremi");
			
			int id_seccional_remi=ParamUtil.getInteger(renderRequest, "id_seccional_r");
			
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			Date envioRecepDesde= sdf.parse(desdeFinal);
			Date envioRecepHasta= sdf.parse(hastaFinal);
			
			List<Correspondencia> busqueda = CorrespondenciaServiceImpl.buscarCorrespondencia(destino, lugarRecepcion, envioRecepDesde, envioRecepHasta,
																							 idCorrDesde, idCorrHasta, tipoCorr, remitente, destinatario, 
																							 receptor, razon_prestador, provincia, localidad, id_seccional_remi);
												
			
			//la lista en el request			
			HttpSession session = (HttpSession) PortalUtil.getHttpServletRequest(
					renderRequest).getSession();
			session.removeAttribute(WebKeysCorrespondencia.BUSQUEDA_CORRESPONDENCIA);
			session.setAttribute(WebKeysCorrespondencia.BUSQUEDA_CORRESPONDENCIA,
					busqueda);
			
		} catch (Exception e) {
			_log.error(e);
			e.printStackTrace();
		}
		
		return mapping.findForward("portlet.uoma.correspondencia.result.search");
		

	}

}