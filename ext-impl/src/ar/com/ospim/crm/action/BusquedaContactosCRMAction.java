package ar.com.ospim.crm.action;

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

import ar.com.ospim.crm.WebKeysCrm;
import ar.com.ospim.crm.beans.BusquedaContactoFiltro;
import ar.com.ospim.crm.beans.ContactoCRMTotal;
import ar.com.ospim.crm.services.CrmServiceUtil;
import ar.com.ospim.util.StringUtils;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

/**
 * <a href="BuscarCorrespondenciaInboxAction.java.html"><b><i>View Source</i></b></a>
 * <p>
 * Realiza la búsqueda de contactos con filtro
 * 
 * @author SVA
 * 
 */
public class BusquedaContactosCRMAction extends PortletAction {
	
	private static Log _log = LogFactoryUtil.getLog(BusquedaContactosCRMAction.class);

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		
		User user = PortalUtil.getUser(renderRequest);
		HttpSession session = (HttpSession) PortalUtil.getHttpServletRequest(renderRequest).getSession();
		
		try {
			int motivo = ParamUtil.getInteger(renderRequest, "motivo",0);
			int categoria = ParamUtil.getInteger(renderRequest, "categoria",0);
			int tipo = ParamUtil.getInteger(renderRequest, "tipo",0);
			String estado = ParamUtil.getString(renderRequest, "estado",null);
			String cuilTitular = ParamUtil.getString(renderRequest, "cuil_titular",null);
			String inte = ParamUtil.getString(renderRequest, "inte",null);
			int incluirA = ParamUtil.getInteger(renderRequest, "incluirA", 0);
			int importancia = ParamUtil.getInteger(renderRequest, "importancia",99);
			int incumContrato = ParamUtil.getInteger(renderRequest, "incumplimientoContrato",99);
			int eficaciaConformidad = ParamUtil.getInteger(renderRequest, "eficaciaConform",99);
			String fechaDesdeFinal = ParamUtil.getString(renderRequest,"fechaDesdeFinal", null);
			String fechaHastaFinal = ParamUtil.getString(renderRequest,"fechaHastaFinal", null);
			int nroContacto = ParamUtil.getInteger(renderRequest, "nro_contacto",0);
			String sectorSel = ParamUtil.getString(renderRequest, "sector",null);
			String usuarioSel = ParamUtil.getString(renderRequest, "usuario",null);
			String noAfiliadoDocNumero = ParamUtil.getString(renderRequest, "noAfiliadoDocNumero",null);
			int situacionMedica = ParamUtil.getInteger(renderRequest, "situacion_medica",0);

			
			if(StringUtils.checkEmpty(sectorSel)){
				sectorSel = null;
			}
			if(StringUtils.checkEmpty(usuarioSel)){
				usuarioSel = null;
			}
			int idPlan = ParamUtil.getInteger(renderRequest, "plan",0);
			int idPlanOmint = ParamUtil.getInteger(renderRequest, "planOmint",0);
			
			BusquedaContactoFiltro filtro = (BusquedaContactoFiltro) 
											session.getAttribute(WebKeysCrm.FILTRO_BUSQUEDA_CONTACTOS);
			if(filtro == null){
				filtro = new BusquedaContactoFiltro();
			}
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			Date fechaDesde = null;
			try {
				fechaDesde = sdf.parse(fechaDesdeFinal);
			} catch (Exception e) {
				fechaDesde = null;
			}		
			Date fechaHasta = null;
			try {
				fechaHasta = sdf.parse(fechaHastaFinal);
			} catch (Exception e) {
				fechaHasta = null;
			}			

			int registrosTotalBusqueda = 0;
//			String llamada = ParamUtil.getString(renderRequest, "viene_de", "");
			int pagina_sel = ParamUtil.getInteger(renderRequest, "pagina", 1);
			pagina_sel--;
		
			int seccional = ParamUtil.getInteger(renderRequest, "seccional",0);
			Integer prestador = ParamUtil.getInteger(renderRequest, "idPrestador",0);
			String cuit = ParamUtil.getString(renderRequest, "cuit",null);
			String sucu = ParamUtil.getString(renderRequest, "sucursal",null);
			
			filtro.setEstado(estado);
			filtro.setFechaDesde(fechaDesde);
			filtro.setFechaHasta(fechaHasta);
			filtro.setPagina(pagina_sel);
			filtro.setCategoria(categoria);
			filtro.setCuil_titular(cuilTitular);
			filtro.setInte(inte);
			filtro.setIncluirA(incluirA);
			filtro.setMotivo(motivo);
			filtro.setTipo(tipo);
			filtro.setNro_contacto(nroContacto);
			filtro.setSector(sectorSel);
			filtro.setUsuario(usuarioSel);
			filtro.setIdPlan(idPlan);
			filtro.setIdPlanOmint(idPlanOmint);
			filtro.setImportancia(importancia);
			filtro.setIncumplimientoContacto(incumContrato);
			filtro.setEficaciaConformidad(eficaciaConformidad);
			filtro.setSeccional(seccional);
			filtro.setNoAfiliadoDocNumero(noAfiliadoDocNumero);
			filtro.setSituacionMedica(situacionMedica);
			
			filtro.setPrestador(prestador);
			if(cuit!=null) filtro.setCuit(cuit);
			if(sucu!=null) filtro.setSucursal(sucu);
		
			
			List<ContactoCRMTotal> busqueda = CrmServiceUtil.busquedaContactosCRM(filtro, pagina_sel, user);
			
			session.removeAttribute(WebKeysCrm.BUSQUEDA_CONTACTOS_RESULT);
			session.removeAttribute(WebKeysCrm.FILTRO_BUSQUEDA_CONTACTOS);
			session.removeAttribute(WebKeysCrm.FILTRO_BUSQUEDA_CONTACTOS_TOTAL_REGISTROS);
			session.removeAttribute(WebKeysCrm.FILTRO_BUSQUEDA_CONTACTOS_OFFSET_REG);
			
			if(busqueda != null && busqueda.size() > 0){ 
				registrosTotalBusqueda = busqueda.get(0).getTotalRegistros();
				session.setAttribute(WebKeysCrm.FILTRO_BUSQUEDA_CONTACTOS_TOTAL_REGISTROS, registrosTotalBusqueda);
				session.setAttribute(WebKeysCrm.FILTRO_BUSQUEDA_CONTACTOS_OFFSET_REG , pagina_sel);
			}else{
				session.setAttribute(WebKeysCrm.FILTRO_BUSQUEDA_CONTACTOS_TOTAL_REGISTROS, 0);
				session.setAttribute(WebKeysCrm.FILTRO_BUSQUEDA_CONTACTOS_OFFSET_REG , 0);
			}
			session.setAttribute(WebKeysCrm.FILTRO_BUSQUEDA_CONTACTOS, filtro);
			session.setAttribute(WebKeysCrm.BUSQUEDA_CONTACTOS_RESULT, busqueda);
			
		} catch (Exception e) {
			_log.error(e);
		}

		return mapping.findForward("portlet.crm.contactos.result.search");

	}

}