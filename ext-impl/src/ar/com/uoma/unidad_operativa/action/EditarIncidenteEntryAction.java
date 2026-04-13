package ar.com.uoma.unidad_operativa.action;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;
import ar.com.ospim.afiliados.beans.Afiliado;
import ar.com.ospim.afiliados.services.BusquedaAfiliadoServiceUtil;
import ar.com.ospim.automatico.ReportesScheduler.ReportesAutomaticosConfiguracion;
import ar.com.ospim.automatico.Thread.EnviaEmailsThread;
import ar.com.ospim.automatico.service.ReportesServiceUtil;
import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.beans.Domicilio;
import ar.com.ospim.global.beans.Localidad;
import ar.com.ospim.global.beans.Provincia;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.liquidaciones.DuplicateLiquidacionIdException;
import ar.com.ospim.liquidaciones.NoSuchLiquidacionEntryException;
import ar.com.ospim.liquidaciones.NoSuchLiquidacionPrestacionEntryException;
import ar.com.ospim.mail.MailUtils;
import ar.com.ospim.util.DateUtils;
import ar.com.uoma.beans.Incidente;
import ar.com.uoma.unidad_operativa.WebKeysUnidadOperativa;
import ar.com.uoma.unidad_operativa.services.UnidadOperativaServiceUtil;

/**
 * <a href="EditarIncidenteEntryAction.java.html"><b><i>View
 * Source</i></b></a>
 * 
 * @author Federico Brachi
 * 
 */
public class EditarIncidenteEntryAction extends PortletAction {
	private static Log _log = LogFactoryUtil.getLog(EditarIncidenteEntryAction.class);


	public void processAction(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse) throws Exception {

		String cmd = ParamUtil.getString(actionRequest, Constants.CMD, null);
		User user = PortalUtil.getUser(actionRequest);
		boolean errors = false;
		try {
			if (cmd != null) {
				if (cmd.equals(Constants.ADD)) {
					Incidente incidente=armarIncidente(actionRequest);					
					actionRequest.setAttribute(WebKeysUnidadOperativa.INCIDENTE_EN_EDICION, UnidadOperativaServiceUtil.grabarIncidente(incidente, user));
					this.enviarCorreoUnidadOpertiva(incidente);	
				} else if  (cmd.equals(Constants.UPDATE)) {
					Incidente incidente=armarIncidente(actionRequest);
					UnidadOperativaServiceUtil.editarIncidente(incidente, user);
				} else if (cmd.equals(Constants.DELETE)){
				
				}
			} 
		} catch (Exception e) {
			if (e instanceof NoSuchLiquidacionEntryException
					|| e instanceof DuplicateLiquidacionIdException
					|| e instanceof NoSuchLiquidacionPrestacionEntryException) {
				SessionErrors.add(actionRequest, e.getClass().getName());
				setForward(actionRequest, "portlet.uoma.error");
			} else {
				throw e;
			}
		}
		if (SessionErrors.isEmpty(actionRequest) && !errors) {
			String successMessage = ParamUtil.getString(actionRequest,
					"successMessage");
			SessionMessages.add(actionRequest, "request_processed",
					successMessage);
		}
	}

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		
		int id_incidente=ParamUtil.getInteger(renderRequest, "id_incidente");
		
		
		if(id_incidente!=0){
			Incidente incidente=UnidadOperativaServiceUtil.buscarIncidente(id_incidente);
			renderRequest.setAttribute(WebKeysUnidadOperativa.INCIDENTE_EN_EDICION, incidente);
		}
		
		return mapping.findForward(getForward(renderRequest,
				"portlet.uoma.editar_incidente_entry"));
	}
	
	public Incidente armarIncidente(ActionRequest req){
		Incidente incidente=new Incidente();
		
		SimpleDateFormat formatoDeFecha = new SimpleDateFormat("dd/MM/yyyy");
		String fechaDia = ParamUtil.getString(req, "fechaDia");
		String fechaMes = ParamUtil.getString(req, "fechaMes");
		String fechaAnio = ParamUtil.getString(req, "fechaAnio");		
		Date fecha = null;
		try {
			fecha = formatoDeFecha.parse(fechaDia + "/"
					+ (Integer.parseInt(fechaMes) + 1) + "/" + fechaAnio);
		} catch (Exception e) {
			fecha = null;
		
		}
		String fechaDiaRecepcion = ParamUtil.getString(req, "fechaDiaRecepcion");
		String fechaMesRecepcion = ParamUtil.getString(req, "fechaMesRecepcion");
		String fechaAnioRecepcion = ParamUtil.getString(req, "fechaAnioRecepcion");		
		Date fechaRecepcion = null;
		try {
			fechaRecepcion = formatoDeFecha.parse(fechaDiaRecepcion + "/"
					+ (Integer.parseInt(fechaMesRecepcion) + 1) + "/" + fechaAnioRecepcion);
		} catch (Exception e) {
			fechaRecepcion = null;
		
		}
		incidente.setIdIncidente(ParamUtil.getInteger(req, "id_incidente"));
		
		incidente.setFecha(fecha);
		incidente.setFechaRecepcion(fechaRecepcion);
		
		incidente.setIdSeccional(ParamUtil.getInteger(req,"id_seccional_r"));
		
		Afiliado afiliado=new Afiliado();
		afiliado.setCuil_titular(ParamUtil.getString(req,"cuil"));
		afiliado.setInte(ParamUtil.getInteger(req,"inte"));
		afiliado.setApellido(ParamUtil.getString(req, "apellido"));
		afiliado.setNombre(ParamUtil.getString(req, "nombre"));
		incidente.setAfiliado(afiliado);
		
		Domicilio domicilio=new Domicilio();
		Provincia provincia=new Provincia(ParamUtil.getInteger(req, "provincia"));		
		domicilio.setProvincia(provincia);
		Localidad localidad=new Localidad(ParamUtil.getInteger(req, "localidad"));
		domicilio.setId_domicilio(ParamUtil.getInteger(req,"id_domicilio"));
		domicilio.setLocalidad(localidad);		
		domicilio.setCalle(ParamUtil.getString(req, "calle"));
		domicilio.setNumero(ParamUtil.getString(req, "numero"));
		domicilio.setPiso(ParamUtil.getString(req, "piso"));
		domicilio.setDepto(ParamUtil.getString(req, "depto"));
		domicilio.setPostal_codi(ParamUtil.getString(req, "cod_postal"));
		domicilio.setObservaciones(ParamUtil.getString(req, "obserDomicilio"));		
		incidente.setLugarIncidente(domicilio);
		
		incidente.setDetalleIncidente(ParamUtil.getString(req, "detalle"));
		incidente.setSeguimientoIncidenteNuevo(ParamUtil.getString(req, "seguimiento"));
		
		incidente.getAfiliado().setId_ospim(ParamUtil.getInteger(req, "numero_afi"));
		
		
		
		return incidente;
	}
	
	private  void enviarCorreoUnidadOpertiva(Incidente incidente) {
		List<Afiliado> afiliados =  null;
		Afiliado afiliado =  null;
		try {
			
			//Solo me interesa si esta  afiliado a OSPIM
			afiliados = BusquedaAfiliadoServiceUtil.getBusquedaAfiliadosComponente(incidente.getAfiliado().getCuil_titular(),
					String.valueOf(incidente.getAfiliado().getInte()), null, null,0,
						null, null, WebKeysGlobal.ID_DEFAULT_ENTIDAD, 0, new Date(), 0, null);
		} catch (Exception e) {
			_log.error(e);
			_log.debug(e.getMessage());
		}
		if (afiliados != null && !afiliados.isEmpty()) {
			afiliado = afiliados.iterator().next();
			if(afiliado.getId_ospim() != 0 && 
								(afiliado.getId_ospim_baja_fecha() == null || DateUtils.esMayor(afiliado.getId_ospim_baja_fecha(), new Date()))) {
				ReportesAutomaticosConfiguracion rac = null;
		      	try {
			         rac = ReportesServiceUtil.getConfiguracion();
				} catch (SystemException e) {
					e.printStackTrace();
				}
				List<String> emails;
				String subject;
				String body;
				String destinos;
				
				emails = new ArrayList<String>();
				destinos=TraeListasServiceUtil.getSystemConfig("EMAIL_DESTINATARIO_UNIDAD_OPERATIVA");
				String[] auxDestinos = destinos.split(";");
				for (String to : auxDestinos) {
					emails.add(to);
				}

				
		 		subject = "Carga Unidad Operativa   ";
		 		body= "Se cargo un nuevo caso de Unidad Operativa   "         +"\n\n\n" +
		 		"CUIL titular: "  +  incidente.getAfiliado().getCuil_titular()  + "    " +
		 		"Integrante:   "  +  incidente.getAfiliado().getInte()          +"\n\n\n" +
		 		"Apellido:     "  +  incidente.getAfiliado().getApellido()      + "    " +
		 		"Nombre:       "  +  incidente.getAfiliado().getNombre()        +"\n\n\n" +
		 		"";

//		 		MailUtils.enviarMailGmailSinAdj(rac.getMailFrom(), rac.getPass(), emails, subject, body, 3);
		 		EnviaEmailsThread.enviarMailDesatendido(subject, body, emails, 3);
			}
	 }					 		
	}

}

