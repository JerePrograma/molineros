package ar.com.uoma.correspondencia.action;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.global.beans.Domicilio;
import ar.com.ospim.global.beans.Localidad;
import ar.com.ospim.global.beans.Provincia;
import ar.com.ospim.global.beans.Seccional;
import ar.com.ospim.liquidaciones.DuplicateLiquidacionIdException;
import ar.com.ospim.liquidaciones.NoSuchLiquidacionEntryException;
import ar.com.ospim.liquidaciones.NoSuchLiquidacionPrestacionEntryException;
import ar.com.uoma.beans.Correspondencia;
import ar.com.uoma.beans.TipoCorrespondencia;
import ar.com.uoma.correspondencia.WebKeysCorrespondencia;
import ar.com.uoma.correspondencia.services.CorrespondenciaServiceImpl;

import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

/**
 * <a href="EditarCorrespondenciaAction.java.html"><b><i>View
 * Source</i></b></a>
 * 
 * @author Federico Brachi
 * 
 */
public class EditarCorrespondenciaAction extends PortletAction {

	public void processAction(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse) throws Exception {

		String cmd = ParamUtil.getString(actionRequest, Constants.CMD, null);
		User user = PortalUtil.getUser(actionRequest);
		boolean errors = false;
		try {
			if (cmd != null) {
				if (cmd.equals(Constants.ADD)) {
					Correspondencia correspondencia=armarCorrespondencia(actionRequest);		
					CorrespondenciaServiceImpl service= new CorrespondenciaServiceImpl();
					actionRequest.setAttribute(WebKeysCorrespondencia.CORRESPONDENCIA_EN_EDICION, service.grabarCorrespondencia(correspondencia, user));
				} else if  (cmd.equals(Constants.UPDATE)) {
					Correspondencia correspondencia=armarCorrespondencia(actionRequest);
					CorrespondenciaServiceImpl service= new CorrespondenciaServiceImpl();
					service.actualizarCorrespondencia(correspondencia,user);
					//UnidadOperativaServiceUtil.editarIncidente(incidente, user);
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
		
		int id_correspondencia=ParamUtil.getInteger(renderRequest, "id_correspondencia");
		String nueva=ParamUtil.getString(renderRequest,"nueva");
		
		if(id_correspondencia!=0 && !nueva.trim().equals("true")){			
			Correspondencia correspondencia=CorrespondenciaServiceImpl.buscarCorrespondenciaPorId(id_correspondencia);
			renderRequest.setAttribute(WebKeysCorrespondencia.CORRESPONDENCIA_EN_EDICION, correspondencia);
		}
		
		return mapping.findForward(getForward(renderRequest,
				"portlet.uoma.editar_correspondencia"));
	}
	
	public Correspondencia armarCorrespondencia(ActionRequest req){
		Correspondencia correspondencia=new Correspondencia();
		
		String destino=ParamUtil.getString(req,"destino");
		correspondencia.setDestino(destino);
		
		SimpleDateFormat formatoDeFecha = new SimpleDateFormat("dd/MM/yyyy");
		String fechaDia = ParamUtil.getString(req, "fechaDiaRecepcion");
		String fechaMes = ParamUtil.getString(req, "fechaMesRecepcion");
		String fechaAnio = ParamUtil.getString(req, "fechaAnioRecepcion");		
		Date fecha = null;
		try {
			fecha = formatoDeFecha.parse(fechaDia + "/"
					+ (Integer.parseInt(fechaMes) + 1) + "/" + fechaAnio);
		} catch (Exception e) {
			fecha = null;
		
		}	
		correspondencia.setGastoSeccional(ParamUtil.getBoolean(req, "gastos_seccional"));
		correspondencia.setReintegro(ParamUtil.getBoolean(req, "reintegros"));
		correspondencia.setPadrones(ParamUtil.getBoolean(req, "padrones"));
		correspondencia.setDiscapacidad(ParamUtil.getBoolean(req, "discapacidad"));
		correspondencia.setOtros(ParamUtil.getBoolean(req, "otros"));
		correspondencia.setDocumentacion(ParamUtil.getBoolean(req, "documentacion"));
		correspondencia.setFacturacion(ParamUtil.getBoolean(req, "facturacion"));
		correspondencia.setMedicamentos(ParamUtil.getBoolean(req, "medicamentos"));
		correspondencia.setTesoreria(ParamUtil.getBoolean(req, "tesoreria"));
		correspondencia.setIdCorrespondencia(ParamUtil.getInteger(req, "id_correspondencia"));
		
		correspondencia.setFechaEnvioRecepcion(fecha);
		
		if(destino.trim().equals("ENTRANTE")){
			correspondencia.setLugarRecepcion(ParamUtil.getString(req, "edificioRecepcion"));
		}
		
		correspondencia.setTipo(new TipoCorrespondencia(ParamUtil.getInteger(req, "tipoCorresp")));
		
		correspondencia.setApellidoDestinatario(ParamUtil.getString(req, "apellidoDestinatario"));
		correspondencia.setNombreDestinatario(ParamUtil.getString(req, "nombreDestinatario"));
		
		correspondencia.setApellidoRemitente(ParamUtil.getString(req, "apellidoRemitente"));
		correspondencia.setNombreRemitente(ParamUtil.getString(req, "nombreRemitente"));
		
		correspondencia.setEdificioRemitente(ParamUtil.getString(req,"lugarRemitente"));
		correspondencia.setEdificioDestinatario(ParamUtil.getString(req,"lugarDestinatario"));
		
		correspondencia.setObservaciones(ParamUtil.getString(req, "observaciones"));
		correspondencia.setRazonPrestadorRemitente(ParamUtil.getString(req, "razonPrestadorRemitente"));
		
		Domicilio domicilioRemi=new Domicilio();
		Provincia provinciaRemi=new Provincia(ParamUtil.getInteger(req, "provinciaremi"));		
		domicilioRemi.setProvincia(provinciaRemi);
		Localidad localidadRemi=new Localidad(ParamUtil.getInteger(req, "localidadremi"));
		domicilioRemi.setId_domicilio(ParamUtil.getInteger(req,"id_domicilioremi"));
		domicilioRemi.setLocalidad(localidadRemi);		
		domicilioRemi.setCalle(ParamUtil.getString(req, "calleremi"));
		domicilioRemi.setNumero(ParamUtil.getString(req, "numeroremi"));
		domicilioRemi.setPiso(ParamUtil.getString(req, "pisoremi"));
		domicilioRemi.setDepto(ParamUtil.getString(req, "dptoremi"));
		domicilioRemi.setPostal_codi(ParamUtil.getString(req, "cod_postalremi"));
		domicilioRemi.setObservaciones(ParamUtil.getString(req, "obserDomicilioremi"));		
		domicilioRemi.setId_domicilio(ParamUtil.getInteger(req,"id_domicilio_remitente"));
		
		correspondencia.setDomicilioRemitente(domicilioRemi);
		
		Domicilio domicilioDesti=new Domicilio();
		Provincia provinciaDesti=new Provincia(ParamUtil.getInteger(req, "provinciadesti"));		
		domicilioDesti.setProvincia(provinciaDesti);
		Localidad localidadDesti=new Localidad(ParamUtil.getInteger(req, "localidaddesti"));
		domicilioDesti.setId_domicilio(ParamUtil.getInteger(req,"id_domiciliodesti"));
		domicilioDesti.setLocalidad(localidadDesti);		
		domicilioDesti.setCalle(ParamUtil.getString(req, "calledesti"));
		domicilioDesti.setNumero(ParamUtil.getString(req, "numerodesti"));
		domicilioDesti.setPiso(ParamUtil.getString(req, "pisodesti"));
		domicilioDesti.setDepto(ParamUtil.getString(req, "dptodesti"));
		domicilioDesti.setPostal_codi(ParamUtil.getString(req, "cod_postaldesti"));
		domicilioDesti.setObservaciones(ParamUtil.getString(req, "obserDomiciliodesti"));	
		domicilioDesti.setId_domicilio(ParamUtil.getInteger(req,"id_domicilio_destinatario"));
		
		correspondencia.setDomicilioDestinatario(domicilioDesti);
		
		int id_seccional_remi=ParamUtil.getInteger(req, "id_seccional_rremi");
		if(id_seccional_remi>0){
			correspondencia.setSeccionalRemitente(new Seccional(id_seccional_remi));
		}
		int id_seccional_desti=ParamUtil.getInteger(req, "id_seccional_rdesti");
		if(id_seccional_desti>0){
			correspondencia.setSeccionalDestinatario(new Seccional(id_seccional_desti));
		}
		correspondencia.setRazonPrestadorDestinatario(ParamUtil.getString(req, "razonPrestadorDestinatario"));
		correspondencia.setDatosFactura(ParamUtil.getString(req, "datos_factura"));
		correspondencia.setTipoEnvio(ParamUtil.getString(req, "tipoEnvio"));
		correspondencia.setOblea(ParamUtil.getString(req, "codigoOblea"));
		correspondencia.setCodFarmacia(ParamUtil.getString(req, "id_farmacia"));
		correspondencia.setFarmacia(ParamUtil.getString(req, "farmacia"));
		
		return correspondencia;
	}

}

