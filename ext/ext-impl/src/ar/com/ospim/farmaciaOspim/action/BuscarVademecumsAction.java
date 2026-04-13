package ar.com.ospim.farmaciaOspim.action;
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
import ar.com.ospim.farmacia.services.BusquedaVademecumServiceUtil;
import ar.com.ospim.farmaciaOspim.WebKeysFarmaciaOspim;
import ar.com.ospim.farmaciaOspim.beans.BusquedaVademecumFiltro;
import ar.com.ospim.farmaciaOspim.beans.ItemVademecumTotal;

public class BuscarVademecumsAction extends PortletAction  {
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
			
			HttpSession session = (HttpSession) PortalUtil.getHttpServletRequest(renderRequest).getSession();

			PortletSession portletSession = renderRequest.getPortletSession();
			String nombre = ParamUtil.getString(renderRequest, "mediNombre", null);
			String presentacion  = ParamUtil.getString(renderRequest, "mediPresentacion", null);			
			String droga = ParamUtil.getString(renderRequest, "mediDroga", null);
			String laboratorio = ParamUtil.getString(renderRequest, "mediLaboratorio",null);
			Integer troquel  = ParamUtil.getInteger(renderRequest, "mediTroquel", 0);
			Integer registro  = ParamUtil.getInteger(renderRequest, "mediRegistro", 0);
			Boolean todosLosTipos = ParamUtil.getBoolean(renderRequest, "todosLosTipos", false);
			Boolean soloInformadosxSuper= ParamUtil.getBoolean(renderRequest, "soloInformadosxSuper", false);
			Boolean pmiHijo= ParamUtil.getBoolean(renderRequest, "pmiHijo", false);
			Boolean pmiMadre= ParamUtil.getBoolean(renderRequest, "pmiMadre", false);
			Boolean anticonceptivo = ParamUtil.getBoolean(renderRequest, "aco", false);
			Boolean gral= ParamUtil.getBoolean(renderRequest, "gral", false);		
			Boolean buscaEnHistorico= ParamUtil.getBoolean(renderRequest, "buscaEnHistorico", false);
			Boolean soloNuevasAltas= ParamUtil.getBoolean(renderRequest, "soloNuevasAltas", false);
			Boolean padronMolineros = ParamUtil.getBoolean(renderRequest, "molineros", false);
			
			int  pagina =ParamUtil.getInteger(renderRequest,"pagina_sel");
			int totalrecords=0;		
			BusquedaVademecumFiltro filtro = new BusquedaVademecumFiltro (nombre  ,presentacion , droga , laboratorio ,troquel , 
																		  registro , pmiHijo,
																		  pmiMadre,anticonceptivo,gral,pagina,todosLosTipos,
																		  anticonceptivo,gral ,soloInformadosxSuper ,buscaEnHistorico
																		  ,soloNuevasAltas , padronMolineros);
			
			List<ItemVademecumTotal> busqueda =BusquedaVademecumServiceUtil.getBusquedaVademecumTotal(filtro);			
			if (busqueda.size()>0){
				totalrecords = busqueda.get(0).getTotal_registros();
			}else{
				totalrecords =0;
			}				
			portletSession.removeAttribute(WebKeysFarmaciaOspim.BUSQUEDA_REGISTROS_VADEMECUM);
			portletSession.setAttribute(WebKeysFarmaciaOspim.BUSQUEDA_REGISTROS_VADEMECUM,	busqueda);
			
			session.removeAttribute(WebKeysFarmaciaOspim.TOTAL_DE_REGISTROS_BUSQUEDA_VADEMECUM );
			session.removeAttribute(WebKeysFarmaciaOspim.FILTRO_BUSQUEDA_VADEMECUM_OFFSET_REG);
			session.removeAttribute(WebKeysFarmaciaOspim.FILTRO_BUSQUEDA_VADEMECUM);
			
			session.setAttribute(WebKeysFarmaciaOspim.TOTAL_DE_REGISTROS_BUSQUEDA_VADEMECUM , totalrecords );
			session.setAttribute(WebKeysFarmaciaOspim.FILTRO_BUSQUEDA_VADEMECUM_OFFSET_REG, pagina);
			session.setAttribute(WebKeysFarmaciaOspim.FILTRO_BUSQUEDA_VADEMECUM, filtro);
			
			
		} catch (Exception e) {
			_log.error(e);
		}
		return mapping
				.findForward("portlet.farmaciaospim.vademecum.result.search");
	}
}
