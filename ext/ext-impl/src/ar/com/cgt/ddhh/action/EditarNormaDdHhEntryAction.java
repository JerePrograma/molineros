package ar.com.cgt.ddhh.action;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.cgt.ddhh.WebKeysCGT;
import ar.com.cgt.ddhh.beans.NormaDdHh;
import ar.com.cgt.ddhh.beans.TemasNormasDDHH;
import ar.com.cgt.ddhh.beans.TiposNormasDDHH;
import ar.com.cgt.ddhh.services.NormaDDHHServiceUtil;
import ar.com.cgt.ddhh.services.TraeListasServiceUtil;

//import ar.com.cgt.ddhh.beans.Organismo;
//import ar.com.cgt.ddhh.services.OrganismoServiceUtil;
//import ar.com.ospim.global.beans.Domicilio;
//import ar.com.ospim.global.beans.Pais;
//import ar.com.ospim.global.services.TraeListasServiceUtil;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

/**
 * <a href="EditarActasEntryAction.java.html"><b><i>View Source</i></b></a>
 * 
 * @author Sergio Valentini
 * 
 */
public class EditarNormaDdHhEntryAction extends PortletAction {
	private static Log logger = LogFactoryUtil
			.getLog(EditarNormaDdHhEntryAction.class);	

	public void processAction(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse) throws Exception {

		String cmd = ParamUtil.getString(actionRequest, Constants.CMD);		
		PortletSession portletSession =  actionRequest.getPortletSession();
		
		try {
			if (cmd.equals(Constants.ADD) || cmd.equals(Constants.UPDATE)) {
				
				NormaDdHh normaDH = this.recuperarNormaDH(portletSession, actionRequest, cmd);
				
				User user = PortalUtil.getUser(actionRequest);
				
				if (cmd.equals(Constants.ADD)) {
					NormaDDHHServiceUtil.save(normaDH, user);
					
				} else {
					NormaDDHHServiceUtil.update(normaDH, user);
				}
				String successMessage = ParamUtil.getString(actionRequest,
						"successMessage");
				SessionMessages.add(actionRequest, "request_processed",
						successMessage);
				
//				el ID fue insertado al normaDH en el metodo save
				actionRequest.setAttribute("id_normaddhh",normaDH.getId() );
				
				portletSession.setAttribute(WebKeysCGT.NORMADDHH_EN_EDICION, normaDH);
				portletSession.setAttribute(WebKeysCGT.TIPOS_NORMADDHH, TraeListasServiceUtil.getTiposNormasDDHH(normaDH.getSistema()));
				
				setForward(actionRequest, "portlet.cgt_ddhh.editar_norma_ddhh_entry");
			}
		} catch (Exception e) {
			logger.debug("Error al guardar Norma de DDHH", e);
			SessionErrors.add(actionRequest, e.getClass().getName());
		}
	}

	private NormaDdHh recuperarNormaDH(PortletSession portletSession, ActionRequest actionRequest, String cmd) throws Exception{
		
		NormaDdHh norma;
		TemasNormasDDHH tema;
		TiposNormasDDHH tipo;
		int idTema=0, idTipo=0;
		
		norma = (NormaDdHh) portletSession.getAttribute(WebKeysCGT.NORMADDHH_EN_EDICION);
		
		if(norma==null){ 
			norma = new NormaDdHh();
		}
		
		String fechaMes = ParamUtil.getString(actionRequest, "fechaMes");
		String fechaDia = ParamUtil.getString(actionRequest, "fechaDia");
		String fechaAnio = ParamUtil.getString(actionRequest,"fechaAnio");
		SimpleDateFormat formatoDeFechaV = new SimpleDateFormat("dd/MM/yyyy");
		Date fecha = null;
		try {
			fecha = formatoDeFechaV.parse(fechaDia + "/"
					+ (Integer.parseInt(fechaMes) + 1) + "/"
					+ fechaAnio);
		} catch (Exception e) {
			fecha = null;
		}
		
		norma.setAutor( ParamUtil.getString(actionRequest,"autor") );
		norma.setContenido(ParamUtil.getString(actionRequest,"contenido"));
		norma.setFecha(fecha);
		norma.setFuenteDependencia(ParamUtil.getString(actionRequest,"fuenteDependencia"));
		norma.setLink(ParamUtil.getString(actionRequest,"link"));
		norma.setLugar(ParamUtil.getString(actionRequest,"lugar"));
		norma.setNumero(ParamUtil.getString(actionRequest,"numero"));
		norma.setResumen(ParamUtil.getString(actionRequest,"resumen"));
		norma.setSistema(ParamUtil.getString(actionRequest,"sistemaselect"));
		norma.setSigla(ParamUtil.getString(actionRequest,"sigla"));
		norma.setIncLegisNac(ParamUtil.getString(actionRequest,"incLegNac"));
		
		idTema = ParamUtil.getInteger(actionRequest, "tema_norma");
		idTipo = ParamUtil.getInteger(actionRequest, "tipo_norma");
		
		tema = new TemasNormasDDHH(idTema, ""); // desconsidere la descripcion 
		tipo = new TiposNormasDDHH(idTipo, "");
		
		norma.setTema(tema);
		norma.setTipo(tipo);
		
		
		return norma;
	}
	
	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {

		// recien entro a la edicion/alta
					
		PortletSession portletSession =  renderRequest.getPortletSession();
		String cmd = ParamUtil.getString(renderRequest, Constants.CMD);	
		if(cmd!=null&&!cmd.equals(Constants.UPDATE)&&!cmd.equals(Constants.ADD)){
			portletSession.removeAttribute(WebKeysCGT.NORMADDHH_EN_EDICION);
		}
		
//		TraeListasServiceUtil.getPaises(renderRequest);
//		TraeListasServiceUtil.getProvincias(renderRequest);
//		TraeListasServiceUtil.getLocalidades(renderRequest);
//		String sistema = "NACIONAL";
//		if( ! ParamUtil.getString(renderRequest,"sistema").isEmpty() ) {
//			sistema = ParamUtil.getString(renderRequest,"sistema") ;
//			renderRequest.setAttribute(WebKeysCGT.TIPOS_NORMADDHH, TraeListasServiceUtil.getTiposNormasDDHH(sistema));
//		}else{ // primera pasada sin seleccionar sistema
//			renderRequest.setAttribute(WebKeysCGT.TIPOS_NORMADDHH, TraeListasServiceUtil.getTiposNormasDDHH(sistema));
//		}
		
		if(portletSession.getAttribute(WebKeysCGT.TEMAS_NORMADDHH) == null){
			portletSession.setAttribute(WebKeysCGT.TEMAS_NORMADDHH, TraeListasServiceUtil.getTemasNormasDDHH());
		}
		
		return mapping.findForward(getForward(renderRequest,
				"portlet.cgt_ddhh.editar_norma_ddhh_entry"));

	}
}