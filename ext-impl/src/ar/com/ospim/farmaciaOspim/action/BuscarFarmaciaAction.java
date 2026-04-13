package ar.com.ospim.farmaciaOspim.action;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import ar.com.ospim.farmacia.beans.Medicamento;
import ar.com.ospim.farmacia.services.BusquedaMedicamentoServiceUtil;
import ar.com.ospim.farmaciaOspim.WebKeysFarmaciaOspim;
import ar.com.ospim.farmaciaOspim.beans.ItemFarmaciaTotal;
import ar.com.ospim.farmaciaOspim.beans.ItemMedicacionTotal;
import ar.com.ospim.farmaciaOspim.services.FarmaciaServiceUtil;
import ar.com.ospim.global.beans.Farmacia;
import ar.com.ospim.autorizaciones.beans.ItemReclamoPrestacionalesTotal;
import ar.com.ospim.autorizaciones.services.ReclamosPrestacionesServiceUtil;
import ar.com.ospim.autorizaciones.services.WebKeysAutorizaciones;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

public class BuscarFarmaciaAction  extends PortletAction  {
	private static Log _log = LogFactoryUtil
			.getLog(BuscarMedicamentosAction.class);

	public void processAction(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse) throws Exception {
		setForward(actionRequest,
				"portlet.farmaciaospim.farmacia.result.search");
		
	}

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		try {
			
			HttpSession session = (HttpSession) PortalUtil.getHttpServletRequest(renderRequest).getSession();			
			PortletSession portletSession = renderRequest.getPortletSession();
			String cuitFarmacia= ParamUtil.getString(renderRequest, "farmaciaCuit", null);
			String descripcionFarmacia= ParamUtil.getString(renderRequest, "farmaciaDescripcion", null);
			String codMandataria = ParamUtil.getString(renderRequest, "farmaciaCodigoMandataria", null);
			int localidad = ParamUtil.getInteger(renderRequest, "farmaciaLocalidad", 0);
			int provincia = ParamUtil.getInteger(renderRequest, "farmaciaProvincia", 0);
			int pagina =ParamUtil.getInteger(renderRequest,"pagina_sel");
			int totalrecords=0;
			List<ItemFarmaciaTotal> busqueda =FarmaciaServiceUtil.getFarmaciasOspimTotal (cuitFarmacia,descripcionFarmacia,provincia,localidad,codMandataria,pagina );
			
			if (busqueda.size()>0){
				totalrecords = busqueda.get(0).getTotal_registros();
			}else{
				totalrecords =0;
			}
			portletSession.removeAttribute(WebKeysFarmaciaOspim.BUSQUEDA_REGISTROS_FARMACIA_OSPIM);
			portletSession.setAttribute(WebKeysFarmaciaOspim.BUSQUEDA_REGISTROS_FARMACIA_OSPIM,	busqueda);
			session.removeAttribute(WebKeysFarmaciaOspim.TOTAL_DE_REGISTROS_BUSQUEDA_FARMACIA);
			session.setAttribute(WebKeysFarmaciaOspim.TOTAL_DE_REGISTROS_BUSQUEDA_FARMACIA, totalrecords );
			session.setAttribute(WebKeysFarmaciaOspim.FILTRO_BUSQUEDA_FARMACIA_OSPIM_OFFSET_REG, pagina);

			
		} catch (Exception e) {
			_log.error(e);
		}
		return mapping
				.findForward("portlet.farmaciaospim.farmacia.result.search");
	}
}
