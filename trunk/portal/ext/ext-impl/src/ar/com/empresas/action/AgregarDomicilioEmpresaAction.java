package ar.com.empresas.action;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.portlet.PortletConfig;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.empresas.WebKeysEmpresas;
import ar.com.ospim.estudioisidro.WebKeysEstudioIsidro;
import ar.com.ospim.estudioisidro.beans.LlamadosEstudio;
import ar.com.ospim.global.beans.Domicilio;
import ar.com.ospim.global.beans.Empresa;
import ar.com.ospim.global.beans.Localidad;
import ar.com.ospim.global.beans.Provincia;
import ar.com.ospim.global.services.TraeListasServiceUtil;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;

public class AgregarDomicilioEmpresaAction extends PortletAction {

	private static Log logger = LogFactoryUtil
			.getLog(AgregarDomicilioEmpresaAction.class);

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		logger.debug("Agregando domicilio empresa");
			
		boolean esSeguientoEmpresa=false;
		
		Empresa empresa =(Empresa) renderRequest.getPortletSession().getAttribute(WebKeysEmpresas.EMPRESA_EN_EDICION, PortletSession.APPLICATION_SCOPE);
		if(empresa==null){
			LlamadosEstudio llest=(LlamadosEstudio)renderRequest.getPortletSession().getAttribute(WebKeysEstudioIsidro.EMPRESA_SEGUIMIENTO,PortletSession.APPLICATION_SCOPE);
			empresa=llest!=null?llest.getEmpresa():null;
			if(empresa!=null){
				esSeguientoEmpresa=true;
			}
		}

		Domicilio domi = new Domicilio();
		String accion = ParamUtil.getString(renderRequest, "accion");
		List<Domicilio> domiciliosEmpresa = null;
		int pos = -1;
		
			if (null == empresa) {
				empresa = new Empresa();
			}
			if (null != empresa.getDomicilios()
					&& empresa.getDomicilios().size() > 0) {
				domiciliosEmpresa = empresa.getDomicilios();
			} else {
				domiciliosEmpresa = new ArrayList<Domicilio>();
			}
			
		if (accion.equals("ADD")) {
			domi = getDomicilio(renderRequest);
			
			pos = domiciliosEmpresa.indexOf(domi);
			
			if(domi.getId_domicilio() > 0){
				domi.setEstado(Domicilio.ESTADOS.MODIF);
			}else{
				domi.setEstado(Domicilio.ESTADOS.NUEVO);
//				solo si edito un domicilio, tendre el domicilio con id, sino le genero uno aleatorio para el borrado de domicilio
//				me aseguro sea un numero negativo para no confundir con IDs de BD
				Random r = new Random(System.currentTimeMillis());
				int idAux = r.nextInt(); // sale neg o pos, si es pos, lo pasamos con (-1)
				if(idAux > 0){
					idAux = (-1)*idAux;
				}
				domi.setId_domicilio(idAux);
			}
			if(pos >= 0){ // lo encontro, para update remuevo el viejo, para insert el pos da -1
				domiciliosEmpresa.remove(pos);
			}
			domiciliosEmpresa.add(domi);			
		}else if (accion.equals("DELETE")) {			
//			removeDomicilioFromList(list,getDomicilio(renderRequest));
			domi = getDomicilio(renderRequest);
			pos = domiciliosEmpresa.indexOf(domi);
			
			if(domi.getId_domicilio() >= 0){ // lo encontro, para update remuevo el viejo, para insert el pos da -1
				domi.setEstado(Domicilio.ESTADOS.BAJA);
				domi.setBaja_fecha(new Date());
				domiciliosEmpresa.remove(pos);
				domiciliosEmpresa.add(domi);
				
			}else{ // se quita un nuevo domicilio que no fue insertado a la BD
				domiciliosEmpresa.remove(pos);
			}
			
		}
		
		empresa.setDomicilios(domiciliosEmpresa);
		
		if(!esSeguientoEmpresa){
			renderRequest.getPortletSession().setAttribute(WebKeysEmpresas.EMPRESA_EN_EDICION, empresa, PortletSession.APPLICATION_SCOPE);
		}else{
			renderRequest.getPortletSession().setAttribute(WebKeysEstudioIsidro.EMPRESA_SEGUIMIENTO, empresa, PortletSession.APPLICATION_SCOPE);
		}
		return mapping.findForward("portlet.empresas.agregar_domicilio");

	}

	private Domicilio getDomicilio(RenderRequest renderRequest)
			throws ParseException, SystemException {
		Domicilio domicilio = new Domicilio();
		
		String tipo= ParamUtil.getString(renderRequest, "tipo_domi_empre");
		domicilio.setDomi_tipo(tipo);
		int idDomicilio = ParamUtil.getInteger(renderRequest, "id_domicilio");
		int idProvincia = ParamUtil.getInteger(renderRequest, "provincia");
		int idLocalidad = ParamUtil.getInteger(renderRequest, "localidad");
		String codPostal = ParamUtil.getString(renderRequest, "cod_postal");
		String calle = ParamUtil.getString(renderRequest, "calle");
		String numero = ParamUtil.getString(renderRequest, "numero");
		String departamento = ParamUtil.getString(renderRequest, "departamento");
		String piso = ParamUtil.getString(renderRequest, "piso");
		String observaciones = ParamUtil.getString(renderRequest, "observaciones");
		
		Provincia provincia=TraeListasServiceUtil.getProvincia(idProvincia, renderRequest);
		
		domicilio.setProvincia(provincia);
		
		Localidad localidad=TraeListasServiceUtil.getLocalidad(idLocalidad, renderRequest);
		domicilio.setLocalidad(localidad);
		
		domicilio.setPostal_codi(codPostal);
		domicilio.setCalle(calle);
		domicilio.setNumero(numero);
		domicilio.setDepto(departamento);
		domicilio.setPiso(piso);
		
		domicilio.setId_domicilio(idDomicilio);
		domicilio.setObservaciones(observaciones);

		return domicilio;
	}
	
//	private void removeDomicilioFromList(List<Domicilio> list, Domicilio ap) {
//		Iterator<Domicilio> it = list.iterator();
//		while (it.hasNext()) {
//			Domicilio aDomcilioEnLista = it.next();
//			if (aDomcilioEnLista.equals(ap)) {				
//					if(aDomcilioEnLista.getId_domicilio()!=0){
//						aDomcilioEnLista.setBaja_fecha(new Date());
//					}else{
//						it.remove();
//					}
//			}
//		}
//	}

}
