package ar.com.ospim.farmaciaOspim.action;

import java.math.BigDecimal;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletRequest;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mortbay.jetty.Request;


import ar.com.ospim.autorizaciones.beans.SituacionMedica;

import ar.com.ospim.autorizaciones.services.SituacionesMedicasServiceUtil;
import ar.com.ospim.autorizaciones.services.WebKeysAutorizaciones;
import ar.com.ospim.farmacia.beans.Medicamento;
import ar.com.ospim.farmacia.services.BusquedaMedicamentoServiceUtil;
import ar.com.ospim.farmaciaOspim.WebKeysFarmaciaOspim;
import ar.com.ospim.util.StringUtils;

import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

	public class EditarMedicacionOspimEntryAction extends PortletAction  {
		
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
        int idRegMedicamento = ParamUtil.getInteger(renderRequest, "id_registro_med",0);
        
        if(StringUtils.checkEmpty(cmd))		{ 			
												this.cargarListas(renderRequest);
		}	
        Medicamento medicacion =null;
		if(!StringUtils.checkEmpty(cmd)){
			if(cmd.equals(Constants.DELETE)){ 
				  borraMedicamentoEntry(renderRequest);
				  idRegMedicamento =0;
			      return mapping.findForward("portlet.farmaciaospim.view");
		    }

			session.removeAttribute(WebKeysFarmaciaOspim.MEDICACION_EN_EDICION);

				if(cmd.equals(Constants.SAVE)){
					medicacion=getMedicacionFromRequest(renderRequest, medicacion);
					idRegMedicamento = BusquedaMedicamentoServiceUtil.insertar(medicacion   , user);					
					medicacion = BusquedaMedicamentoServiceUtil.getMedicamento(idRegMedicamento);
					session.setAttribute(WebKeysFarmaciaOspim.MEDICACION_EN_EDICION, medicacion  );
					renderRequest.setAttribute(Constants.CMD, Constants.EDIT);
				}
				if(cmd.equals(Constants.EDIT ) || cmd.equals(Constants.VIEW )){					
					medicacion = BusquedaMedicamentoServiceUtil.getMedicamento(idRegMedicamento );
					session.setAttribute(WebKeysFarmaciaOspim.MEDICACION_EN_EDICION, medicacion   );
					renderRequest.setAttribute(Constants.CMD, Constants.EDIT);
					if (cmd.equals(Constants.VIEW ) ){
						renderRequest.setAttribute(Constants.CMD,Constants.VIEW);						
					}
				}
				
				if(cmd.equals(Constants.UPDATE )){

					medicacion=getMedicacionFromRequest(renderRequest, medicacion);   
					BusquedaMedicamentoServiceUtil.actualizar(medicacion, user);
					medicacion= BusquedaMedicamentoServiceUtil.getMedicamento(medicacion.getId_medicamento() );
					session.setAttribute(WebKeysFarmaciaOspim.MEDICACION_EN_EDICION, medicacion);					
				}	
				if (SessionErrors.isEmpty(renderRequest)  && (cmd.equals(Constants.UPDATE)  || cmd.equals(Constants.SAVE))  ) 	{
						String successMessage = ParamUtil.getString(renderRequest, "successMessage");
						SessionMessages.add(renderRequest, "request_processed", successMessage);																}
						renderRequest.setAttribute(Constants.CMD, Constants.EDIT);					
					    if (cmd.equals(Constants.VIEW) ){
						   renderRequest.setAttribute(Constants.CMD,Constants.VIEW);	
						}					    
				
				}else{  	  
					session.removeAttribute(WebKeysFarmaciaOspim.MEDICACION_EN_EDICION);
					renderRequest.setAttribute(Constants.CMD, Constants.ADD);
				}		
		return mapping.findForward(getForward(renderRequest,
						"portlet.farmaciaospim.medicamento.medicamento_edicion_entry"));
	}	
	
	private void cargarListas(RenderRequest renderRequest) throws Exception{
		// por las dudas
	}

	protected void borraMedicamentoEntry(RenderRequest renderRequest)
			throws Exception {
		    int idMedicacion = ParamUtil.getInteger(renderRequest,
				"id_registro_med", 0);
		    User user = PortalUtil.getUser(renderRequest);
		    BusquedaMedicamentoServiceUtil.borrar(idMedicacion , user);
	}
	
		
public Medicamento getMedicacionFromRequest(RenderRequest req, Medicamento medicacion ) {		

		try {
			
			Date fecha;
			Date fechaPeriodo;			
			SimpleDateFormat formatoDePeriodo = new SimpleDateFormat("dd/MM/yyyy");
			String fechaPeriodoMes = ParamUtil.getString(req,"periodoMes") ;
			String fechaPeriodoAnio  = ParamUtil.getString(req,"periodoAnio");
			
			if (! ( fechaPeriodoMes==""  && fechaPeriodoAnio =="")){
				fechaPeriodo= formatoDePeriodo.parse( "01/"
						+ (Integer.parseInt(fechaPeriodoMes) + 1) + "/"
						+ fechaPeriodoAnio );
			}else{
				fechaPeriodo=null;
			}			
			
			
			String fechaDia = ParamUtil.getString(req,"fechaDia") ;
			String fechaMes = ParamUtil.getString(req,"fechaMes") ;
			String fechaAnio  = ParamUtil.getString(req,"fechaAnio");			
			if (! ( fechaMes==""  && fechaAnio  =="")){
				fecha= formatoDePeriodo.parse( fechaDia + "/"
						+ (Integer.parseInt(fechaMes) + 1) + "/"
						+ fechaAnio );
			}else{
				fecha=null;
			}
			int registro = ParamUtil.getInteger(req,"registro");
			String presentacionactiva= ParamUtil.getString(req,"presentacionactiva");
			int troquel = ParamUtil.getInteger(req,"troquel");
			String  nombre = ParamUtil.getString(req,"nombre");
			String  presentacion= ParamUtil.getString(req,"presentacion");
			String  laboratorio= ParamUtil.getString(req,"laboratorio");
			BigDecimal precio = new BigDecimal(Double.toString(ParamUtil.getDouble(req,"precio")));
			String   codebar = ParamUtil.getString(req,"codebar");
			String  accion = ParamUtil.getString(req,"accion");
			String  droga = ParamUtil.getString(req,"droga");			
			String tipoventa = ParamUtil.getString(req,"tipoventa"); 
		    String  iva = ParamUtil.getString(req,"iva");
		    boolean manualDat = ParamUtil.getBoolean(req, "manualdat");
		    int idMedicamento = ParamUtil.getInteger(req,"idMedicamento"); 
			medicacion = new Medicamento(idMedicamento,troquel,registro,nombre,presentacion,laboratorio,accion , droga,precio,fecha ,fechaPeriodo,codebar,presentacionactiva,tipoventa, iva, manualDat  ); 
			
			
		} catch (Exception e) {
			_log.debug("item:errorr " );
		}		
		return medicacion ;
	}	
}
