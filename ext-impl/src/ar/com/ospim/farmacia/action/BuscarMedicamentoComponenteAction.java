package ar.com.ospim.farmacia.action;

import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.afiliados.NoSuchAfiliadoEntryException;
import ar.com.ospim.afiliados.action.BuscarPreCargaAfiliadosAction;
import ar.com.ospim.afiliados.beans.Afiliado;
import ar.com.ospim.afiliados.services.EditarAfiliadoServiceUtil;
import ar.com.ospim.farmacia.WebKeysFarmacia;
import ar.com.ospim.farmacia.beans.Medicamento;
import ar.com.ospim.farmacia.services.BusquedaMedicamentoServiceUtil;
import ar.com.ospim.global.beans.Plan;
import ar.com.ospim.global.services.TraeListasServiceUtil;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;

/**
 * <a href="BuscarMedicamentoComponenteAction.java.html"><b><i>View Source</i></b></a>
 * <p>
 * Realiza la búsqueda de medicamentos según parámetros de entrada
 * 
 * @author Federico Brachi
 * 
 */
public class BuscarMedicamentoComponenteAction extends PortletAction {
	private static Log _log = LogFactoryUtil
			.getLog(BuscarPreCargaAfiliadosAction.class);

	public void processAction(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse) throws Exception {
		setForward(actionRequest, "portlet.farmacia.medicamento.result.search");
	}

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {

		String popup = null;
		Afiliado afiliado=null;
		
		try {
			int troquel = 0;
			int registro = 0;
			String nombre = null;
			String cuil = null;
			int inte= 0;
			String presentacion = null;
			String laboratorio = null;
			String cod_barras = null;
			
			cuil=ParamUtil.getString(renderRequest, "cuil");
			inte=ParamUtil.getInteger(renderRequest, "inte");
			afiliado=EditarAfiliadoServiceUtil.getAfiliadoDadoBaja(cuil, inte);
			
			troquel=  ParamUtil.getInteger(renderRequest, "troquel", 0);
			registro= ParamUtil.getInteger(renderRequest, "registro", 0);
			nombre= ParamUtil.getString(renderRequest, "nombre_medicamento", null);
			if(null==nombre||nombre.trim().equals("")){
				nombre= ParamUtil.getString(renderRequest, "nombre", null);
			}
			presentacion= ParamUtil.getString(renderRequest, "presentacion", null);
			laboratorio= ParamUtil.getString(renderRequest, "laboratorio", null);
			cod_barras = ParamUtil.getString(renderRequest, "cod_barras", null);
			
			List<Plan> planes= TraeListasServiceUtil.getPlanes(renderRequest);
			
			List<Medicamento> busqueda = BusquedaMedicamentoServiceUtil.getBusquedaMedicamentos(troquel, registro, nombre, presentacion, laboratorio, afiliado!=null?afiliado.getUltimo_plan().getId():0, cod_barras, planes,null);
			
			renderRequest.removeAttribute(WebKeysFarmacia.BUSQUEDA_MEDICAMENTO);
			renderRequest.setAttribute(WebKeysFarmacia.BUSQUEDA_MEDICAMENTO,busqueda);
			
		} catch (NoSuchAfiliadoEntryException e) {
			_log.error(e);
			e.printStackTrace();
		}

		popup = ParamUtil.getString(renderRequest, "popup");

		if (null != popup && !popup.trim().equals("") && afiliado!=null) {
			return mapping.findForward("portlet.farmacia.medicamento.result.search.popup");
		} else if (null != popup && !popup.trim().equals("")){
			return mapping.findForward("portlet.utils.medicamento.view");
		}else{
			return mapping.findForward("portlet.farmacia.medicamento.result.search");
		}
	}
}