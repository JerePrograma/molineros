package ar.com.ospim.farmaciaOspim.action;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import ar.com.ospim.farmacia.beans.Vademecum;
import ar.com.ospim.farmacia.services.BusquedaVademecumServiceUtil;
import ar.com.ospim.farmaciaOspim.WebKeysFarmaciaOspim;
import ar.com.ospim.util.StringUtils;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

	public class EditarVademecumOspimEntryAction extends PortletAction  {
		
	private Logger _log = Logger.getLogger(this.getClass());
	
	public void processAction(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse) throws Exception {

	}
	
	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		
		HttpSession session = (HttpSession) PortalUtil.getHttpServletRequest(renderRequest).getSession();
		
		String cmd = ParamUtil.getString(renderRequest, Constants.CMD);
		User user = PortalUtil.getUser(renderRequest);
        int idRegMedicamento = ParamUtil.getInteger(renderRequest, "id_registro_vade",0);
        boolean  buscaEnHistorico  = ParamUtil.getBoolean(renderRequest, "buscaEnHistorico",false);
        SimpleDateFormat formatoDeFecha = new SimpleDateFormat("dd/MM/yyyy");
		String periodoDia = ParamUtil.getString(renderRequest, "periodoDia");
		String periodoMes = ParamUtil.getString(renderRequest, "periodoMes");
		String periodoAnio = ParamUtil.getString(renderRequest, "periodoAnio");
		Date periodoHistorico  = null;
		try {
			periodoHistorico  = formatoDeFecha.parse(periodoDia + "/" + (Integer.parseInt(periodoMes) + 1) + "/" + periodoAnio);
		} catch (Exception e) {
			periodoHistorico  = null;
		}
		
        
        Vademecum  vademecum =null;
        List<Vademecum> preciosHistoricosMedicamento ;
		if(!StringUtils.checkEmpty(cmd)){	
			if(cmd.equals(Constants.DELETE)){ 
				  borraMedicamentoEntry(renderRequest);
				  idRegMedicamento =0;
			      return mapping.findForward("portlet.farmaciaospim.view");
		    }

			session.removeAttribute(WebKeysFarmaciaOspim.VADEMECUM_EN_EDICION);

				if(cmd.equals(Constants.SAVE)){
					vademecum=getMedicacionFromRequest(renderRequest, vademecum);
					idRegMedicamento = BusquedaVademecumServiceUtil.insertar(vademecum   , user); 					
					vademecum = BusquedaVademecumServiceUtil.getVademecum(vademecum.getRegistro() , buscaEnHistorico , periodoHistorico);
					//session.setAttribute(WebKeysFarmaciaOspim.VADEMECUM_EN_EDICION, vademecum  );
					renderRequest.setAttribute(Constants.CMD, Constants.EDIT);
				}
				if(cmd.equals(Constants.EDIT ) || cmd.equals(Constants.VIEW )){
					vademecum = BusquedaVademecumServiceUtil.getVademecum(idRegMedicamento , buscaEnHistorico , periodoHistorico);
					preciosHistoricosMedicamento =BusquedaVademecumServiceUtil.getHistoricoDePrecios(idRegMedicamento );
					//session.setAttribute(WebKeysFarmaciaOspim.VADEMECUM_EN_EDICION, vademecum   );
					session.setAttribute(WebKeysFarmaciaOspim.LISTADO_PRECIOS_VADEMECUM, preciosHistoricosMedicamento );
					renderRequest.setAttribute(Constants.CMD, Constants.EDIT);
					if (cmd.equals(Constants.VIEW ) ){
						renderRequest.setAttribute(Constants.CMD,Constants.VIEW);						
					}
				}
				
				if(cmd.equals(Constants.UPDATE )){

					vademecum=getMedicacionFromRequest(renderRequest, vademecum);   
					BusquedaVademecumServiceUtil.actualizar(vademecum, user);
					vademecum= BusquedaVademecumServiceUtil.getVademecum(vademecum.getRegistro()  , buscaEnHistorico , periodoHistorico  );
					//session.setAttribute(WebKeysFarmaciaOspim.VADEMECUM_EN_EDICION , vademecum);					
				}	
				if (SessionErrors.isEmpty(renderRequest)  && (cmd.equals(Constants.UPDATE)  || cmd.equals(Constants.SAVE))  ) 	{
						String successMessage = ParamUtil.getString(renderRequest, "successMessage");
						SessionMessages.add(renderRequest, "request_processed", successMessage);																}
						renderRequest.setAttribute(Constants.CMD, Constants.EDIT);					
					    if (cmd.equals(Constants.VIEW) ){
						   renderRequest.setAttribute(Constants.CMD,Constants.VIEW);	
						}					    
				
				}else{  	  
					//session.removeAttribute(WebKeysFarmaciaOspim.VADEMECUM_EN_EDICION );
					renderRequest.setAttribute(Constants.CMD, Constants.ADD);
				}		
		
		
		        session.setAttribute(WebKeysFarmaciaOspim.VADEMECUM_EN_EDICION , vademecum);
		
		return mapping.findForward(getForward(renderRequest,
						"portlet.farmaciaospim.vademecum.vademecum_edicion_entry"));
	}	
	

	protected void borraMedicamentoEntry(RenderRequest renderRequest)
			throws Exception {
		    int idRegistro = ParamUtil.getInteger(renderRequest,
				"id_registro_vade", 0);
		    int idTroquel = ParamUtil.getInteger(renderRequest,
					"id_troquel_vade", 0);
		    User user = PortalUtil.getUser(renderRequest);
		    BusquedaVademecumServiceUtil.borrar(idRegistro , idTroquel  , user);
	}
		
	public Vademecum getMedicacionFromRequest(RenderRequest req, Vademecum  vademecum ) {
			
			SimpleDateFormat formatoDePeriodo = new SimpleDateFormat("dd/MM/yyyy");
			Date fecha_periodo;
			int registro = ParamUtil.getInteger(req,"registro");			
			int troquel = ParamUtil.getInteger(req,"troquel");
			String nombre = ParamUtil.getString(req,"nombre");
			double pmo= ParamUtil.getDouble(req,"pmo");
			String presentacion= ParamUtil.getString(req,"presentacion");
			String laboratorio= ParamUtil.getString(req,"laboratorio");
			String droga = ParamUtil.getString(req,"droga");
			String accion =ParamUtil.getString(req,"accion");
			boolean pmiMadre=ParamUtil.getBoolean(req,"pmi_madre");
			boolean pmiHijo =ParamUtil.getBoolean(req,"pmi_hijo");
			boolean aco =ParamUtil.getBoolean(req,"anticoncepcion");
			boolean vadeGral =ParamUtil.getBoolean(req,"vade_gral");		
			double 	amtima=ParamUtil.getDouble(req,"amtima");
			double 	sssalud=ParamUtil.getDouble(req,"sssalud");
			double 	ospim=ParamUtil.getDouble(req,"ospim");			
			String fechaPeriodoMes = ParamUtil.getString(req,"periodoMes");
			String fechaPeriodoAnio = ParamUtil.getString(req,"periodoAnio");
			
			fecha_periodo= null;
			
			try {
				fecha_periodo= formatoDePeriodo.parse( "01/"
						+ (Integer.parseInt(fechaPeriodoMes) + 1) + "/"
						+ fechaPeriodoAnio);
			} catch (Exception e) {
				fecha_periodo= null;
			}
				
			vademecum = new Vademecum(registro, troquel, nombre, presentacion ,  laboratorio, droga, fecha_periodo, 
					pmiMadre, pmiHijo, aco, vadeGral, accion, pmo, ospim, sssalud, amtima  );
						
			return vademecum ;
		}	
	}
