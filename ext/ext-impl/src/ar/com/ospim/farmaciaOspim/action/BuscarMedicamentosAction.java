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

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

import ar.com.ospim.farmacia.services.BusquedaMedicamentoServiceUtil;
import ar.com.ospim.farmaciaOspim.WebKeysFarmaciaOspim;
import ar.com.ospim.farmaciaOspim.beans.ItemMedicacionTotal;

public class BuscarMedicamentosAction extends PortletAction  {
	private static Log _log = LogFactoryUtil
			.getLog(BuscarMedicamentosAction.class);

	public void processAction(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse) throws Exception {
		setForward(actionRequest,
				"portlet.farmaciaospim.medicamento.result.search");
		
	}

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		try {
			
			SimpleDateFormat formatoDeFechas = new SimpleDateFormat(
					"dd/MM/yyyy");
			
			HttpSession session = (HttpSession) PortalUtil.getHttpServletRequest(renderRequest).getSession();
			
			String fechaDia ="01";
			String fechaMes = ParamUtil.getString(renderRequest,"mediPeriodoMes");
			String fechaAnio = ParamUtil.getString(renderRequest,"mediPeriodoYear");
			Date fechaPeriodo= null;
			
			try {
				fechaPeriodo= formatoDeFechas.parse(fechaDia + "/"
						+ (Integer.parseInt(fechaMes) + 1) + "/"
						+ fechaAnio);
			} catch (Exception e) {
				fechaPeriodo= null;
			}
			PortletSession portletSession = renderRequest.getPortletSession();
			String nombre = ParamUtil.getString(renderRequest, "mediNombre", null);
			String presentacion  = ParamUtil.getString(renderRequest, "mediPresentacion", null);			
			String droga = ParamUtil.getString(renderRequest, "mediDroga", null);
			String laboratorio = ParamUtil.getString(renderRequest, "mediLaboratorio",null);
			Integer troquel  = ParamUtil.getInteger(renderRequest, "mediTroquel", 0);
			Integer registro  = ParamUtil.getInteger(renderRequest, "mediRegistro", 0);
			String mediCodBarra = ParamUtil.getString(renderRequest, "mediCodBarra", null);
			boolean manualDat  = ParamUtil.getBoolean(renderRequest, "manualDat", false);
			boolean incluyeBajas = ParamUtil.getBoolean(renderRequest, "incluyeBajas", false);
			int  pagina =ParamUtil.getInteger(renderRequest,"pagina_sel");
			int totalrecords=0;		    
			List<ItemMedicacionTotal> busqueda =BusquedaMedicamentoServiceUtil.getBusquedaMedicamentosOspimTotal(troquel, registro, nombre, presentacion, laboratorio, mediCodBarra,fechaPeriodo,droga ,manualDat,pagina,incluyeBajas );
			if (busqueda.size()>0){
				totalrecords = busqueda.get(0).getTotal_registros();
			}else{
				totalrecords =0;
			}
				
			portletSession.removeAttribute(WebKeysFarmaciaOspim.BUSQUEDA_REGISTROS_MEDICAMENTOS_OSPIM );
			portletSession.setAttribute(WebKeysFarmaciaOspim.BUSQUEDA_REGISTROS_MEDICAMENTOS_OSPIM   ,	busqueda);
			session.removeAttribute(WebKeysFarmaciaOspim.TOTAL_DE_REGISTROS_BUSQUEDA);
			session.removeAttribute(WebKeysFarmaciaOspim.FILTRO_BUSQUEDA_MEDICACION_OFFSET_REG);
			session.setAttribute(WebKeysFarmaciaOspim.TOTAL_DE_REGISTROS_BUSQUEDA, totalrecords );
			session.setAttribute(WebKeysFarmaciaOspim.FILTRO_BUSQUEDA_MEDICACION_OFFSET_REG, pagina);
			
		} catch (Exception e) {
			_log.error(e);
		}
		return mapping
				.findForward("portlet.farmaciaospim.medicamento.result.search");
	}
}
