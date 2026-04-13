package ar.com.ospim.comprobantesPortalProveedores.action;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.Company;
import com.liferay.portal.model.User;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portlet.documentlibrary.model.DLFileEntry;
import com.liferay.portlet.documentlibrary.model.DLFolder;
import com.liferay.portlet.documentlibrary.service.DLFileEntryLocalServiceUtil;
import com.liferay.portlet.documentlibrary.service.DLFolderServiceUtil;

import ar.com.ospim.comprobantesPortalProveedores.beans.Sector;
import ar.com.ospim.comprobantesPortalProveedores.services.ComprobanteServiceUtil;
import ar.com.ospim.comprobantesPortalProveedores.services.WebKeysComprobantes;
import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.beans.Comprobante;
import ar.com.ospim.util.StringUtils;
import ar.com.ospim.webservice.proveedoresLPA.ClienteProveedoresLPA;

public class ComprobantesAdministracionAction extends PortletAction {
	
	private Logger _log = Logger.getLogger(this.getClass());
	SimpleDateFormat formatoDeFechas = new SimpleDateFormat("dd/MM/yyyy");
	public void processAction(ActionMapping mapping, ActionForm form,
							  PortletConfig portletConfig, ActionRequest actionRequest,
							  ActionResponse actionResponse) throws Exception {
	}
	
	@SuppressWarnings("deprecation")
	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
			
		HttpServletRequest httpServletRequest = PortalUtil.getHttpServletRequest(renderRequest);
		HttpSession session = (HttpSession) httpServletRequest.getSession();
		User user = PortalUtil.getUser(renderRequest);
		String cmd = ParamUtil.getString(renderRequest, Constants.CMD, null);
		Sector sector=new Sector();
		
		
		if (!StringUtils.checkEmpty(cmd)) {
			
			if(cmd.equals("findSector") ){
				Company company = PortalUtil.getCompany(renderRequest);
				String sectorStr = ParamUtil.getString(renderRequest,"sector");
				
				sector.setId(sectorStr);
				List<User> usuarios = ComprobanteServiceUtil.getUsuariosHabilitadosBySector(company.getCompanyId(),sectorStr,WebKeysGlobal.OSPIM,null);
				
				sector.setUsuariosHabilitados(usuarios);
				session.setAttribute(WebKeysComprobantes.COMPROBANTE_SECTOR_EN_EDICION, sector);
        		
               	return mapping
        				.findForward("portlet.comprobantes.usuarios.result");
			}
			
			if(cmd.equals("adduser") ){
				String usuarioDescripcion = ParamUtil.getString(renderRequest,"usuariodescripcion");
				Integer usuarioId = ParamUtil.getInteger(renderRequest,"usuarioid");
				String sectorStr = ParamUtil.getString(renderRequest,"sector");
				
				sector = (Sector) session.getAttribute(WebKeysComprobantes.COMPROBANTE_SECTOR_EN_EDICION);
				Boolean existeUser=false;
				if(sector.getUsuariosHabilitados()!=null) {
				  for(User ds: sector.getUsuariosHabilitados() ){
					  if(ds.getUserId()  == usuarioId){
						  existeUser=true; 
						  break;
					  }
				  }
				}  
				if(!existeUser){
				  User usuario =UserLocalServiceUtil.getUser(usuarioId) ;
				  ComprobanteServiceUtil.addUsuarioHabilitado(sector.getId(),usuario);	
				  
				  sector.getUsuariosHabilitados().add(usuario);
				}  
                
            	session.setAttribute(WebKeysComprobantes.COMPROBANTE_SECTOR_EN_EDICION, sector);
        		
               	return mapping
        				.findForward("portlet.comprobantes.usuarios.result");
			}
			
			if(cmd.equals("deleteuser") ){
				
				  Integer usuarioId= ParamUtil.getInteger(renderRequest,"usuarioid");
				  String sectorStr = ParamUtil.getString(renderRequest,"sector");
				  
				  List<User> ld = new ArrayList<User>();
				  User usuario =UserLocalServiceUtil.getUser(usuarioId) ;
				  	
				  sector = (Sector) session.getAttribute(WebKeysComprobantes.COMPROBANTE_SECTOR_EN_EDICION);
				  ComprobanteServiceUtil.deleteUsuarioHabilitado(sector.getId(),usuario);
				  
				  if(sector.getUsuariosHabilitados()!=null) {
				    for(User d: sector.getUsuariosHabilitados()){
					  if(d.getUserId()!=usuarioId){
						  ld.add(d);
					  }
				    }
				  }  
				  sector.setUsuariosHabilitados(ld);
				  session.setAttribute(WebKeysComprobantes.COMPROBANTE_SECTOR_EN_EDICION, sector);
	        		
	              return mapping
	        				.findForward("portlet.comprobantes.usuarios.result");
	        				
			}
			
			if(cmd.equals("download") ){
				String fechaEstadoComprobanteMesDde = ParamUtil.getString(renderRequest,
						"fechaEstadoComprobanteMesDde");
				String fechaEstadoComprobanteDiaDde = ParamUtil.getString(renderRequest,
						"fechaEstadoComprobanteDiaDde");
				String fechaEstadoComprobanteAnioDde = ParamUtil.getString(renderRequest,
						"fechaEstadoComprobanteAnioDde");
				SimpleDateFormat formatoDeFecha = new SimpleDateFormat("dd/MM/yyyy");
				Date fechaEstadoCDde = null;
				try {
					fechaEstadoCDde = formatoDeFecha.parse(fechaEstadoComprobanteDiaDde
							+ "/" + (Integer.parseInt(fechaEstadoComprobanteMesDde) + 1)
							+ "/" + fechaEstadoComprobanteAnioDde);
				} catch (Exception e) {
					fechaEstadoCDde = null;
				}

				String fechaEstadoComprobanteMesHta = ParamUtil.getString(renderRequest,
						"fechaEstadoComprobanteMesHta");
				String fechaEstadoComprobanteDiaHta = ParamUtil.getString(renderRequest,
						"fechaEstadoComprobanteDiaHta");
				String fechaEstadoComprobanteAnioHta = ParamUtil.getString(renderRequest,
						"fechaEstadoComprobanteAnioHta");
				Date fechaEstadoCHta = null;
				try {
					fechaEstadoCHta = formatoDeFecha.parse(fechaEstadoComprobanteDiaHta
							+ "/" + (Integer.parseInt(fechaEstadoComprobanteMesHta) + 1)
							+ "/" + fechaEstadoComprobanteAnioHta);
				} catch (Exception e) {
					fechaEstadoCHta = null;
				}
				
				String estado=ParamUtil.getString(renderRequest,"estado",null);
				
				List <Comprobante> cs = ClienteProveedoresLPA.getComprobantesByEstado(estado,fechaEstadoCDde,fechaEstadoCHta);
				List <Comprobante> csProcesados = new ArrayList<Comprobante>();
				List <Comprobante> csErroneos = new ArrayList<Comprobante>();
				if(!cs.isEmpty()) {
					for(Comprobante c:cs) {
						try {
						    ComprobanteServiceUtil.savecomprobanteProveedor(c, user.getScreenName());
						    csProcesados.add(c);
						}catch(Exception ec) {
							csErroneos.add(c);
							
						}
					}
					
				}
				
				if(!csProcesados.isEmpty()) {
					ClienteProveedoresLPA.getComprobantes(csProcesados);
					ClienteProveedoresLPA.getAdjuntos(csProcesados);
				}
				
				session.setAttribute(WebKeysComprobantes.COMPROBANTES_RESULT_PROCESADOS,csProcesados);
				session.setAttribute(WebKeysComprobantes.COMPROBANTES_RESULT_ERRONEOS,csErroneos);
				
				return mapping
        				.findForward("portlet.comprobantes.download.result");
			}
			
			
			
			if(cmd.equals("downloadRecibo") ){
				String fechaEstadoComprobanteMesDde = ParamUtil.getString(renderRequest,
						"fechaEstadoComprobanteMesDde");
				String fechaEstadoComprobanteDiaDde = ParamUtil.getString(renderRequest,
						"fechaEstadoComprobanteDiaDde");
				String fechaEstadoComprobanteAnioDde = ParamUtil.getString(renderRequest,
						"fechaEstadoComprobanteAnioDde");
				SimpleDateFormat formatoDeFecha = new SimpleDateFormat("dd/MM/yyyy");
				Date fechaEstadoCDde = null;
				try {
					fechaEstadoCDde = formatoDeFecha.parse(fechaEstadoComprobanteDiaDde
							+ "/" + (Integer.parseInt(fechaEstadoComprobanteMesDde) + 1)
							+ "/" + fechaEstadoComprobanteAnioDde);
				} catch (Exception e) {
					fechaEstadoCDde = null;
				}

				String fechaEstadoComprobanteMesHta = ParamUtil.getString(renderRequest,
						"fechaEstadoComprobanteMesHta");
				String fechaEstadoComprobanteDiaHta = ParamUtil.getString(renderRequest,
						"fechaEstadoComprobanteDiaHta");
				String fechaEstadoComprobanteAnioHta = ParamUtil.getString(renderRequest,
						"fechaEstadoComprobanteAnioHta");
				Date fechaEstadoCHta = null;
				try {
					fechaEstadoCHta = formatoDeFecha.parse(fechaEstadoComprobanteDiaHta
							+ "/" + (Integer.parseInt(fechaEstadoComprobanteMesHta) + 1)
							+ "/" + fechaEstadoComprobanteAnioHta);
				} catch (Exception e) {
					fechaEstadoCHta = null;
				}
				
				String estado=ParamUtil.getString(renderRequest,"estado",null);
				
				DLFolder f = DLFolderServiceUtil.getFolder(10136, 0L, "Comprobantes");
				long folderId = f.getFolderId();
				String title="";
				String extension="pdf";
				
				List <Comprobante> cs = ClienteProveedoresLPA.getComprobantesByEstado(estado,fechaEstadoCDde,fechaEstadoCHta);
				List <Comprobante> csProcesados = new ArrayList<Comprobante>();
				List <Comprobante> csErroneos = new ArrayList<Comprobante>();
				if(!cs.isEmpty()) {
					for(Comprobante c:cs) {
						try {
							String idFacturaImg = c.getAcreedorEmpresa().getCuit()+"-"+c.getTipoComprobante()+"-"+
									c.getLetraComprobante()+String.format("%05d",c.getPtoVenta())+c.getNroComprobante();
							DLFileEntry dl=null;
					      	title=idFacturaImg +"-Recibo" ;
					      	try{
					      		 dl=	DLFileEntryLocalServiceUtil.getFileEntryByTitle(folderId, title + (extension.length()>0?".":"") + extension);
					      	} catch(Exception e2){}   
					      	if(dl==null) {
						       csProcesados.add(c);
							}else {
							   csErroneos.add(c);
							}
						}catch(Exception ec) {
							csErroneos.add(c);
						}
					}
				}
				
				if(!csProcesados.isEmpty()) {
					ClienteProveedoresLPA.getComprobanteRecibo(csProcesados);
				}
				
				session.setAttribute(WebKeysComprobantes.COMPROBANTES_RESULT_PROCESADOS,csProcesados);
				session.setAttribute(WebKeysComprobantes.COMPROBANTES_RESULT_ERRONEOS,csErroneos);
				
				return mapping
        				.findForward("portlet.comprobantes.download.result");
			}

			
			
            
///--			
			if(cmd.equals("upload_op") ){
				String fechaMesDde = ParamUtil.getString(renderRequest,
						"fechaMesDde");
				String fechaDiaDde = ParamUtil.getString(renderRequest,
						"fechaDiaDde");
				String fechaAnioDde = ParamUtil.getString(renderRequest,
						"fechaAnioDde");
				SimpleDateFormat formatoDeFecha = new SimpleDateFormat("dd/MM/yyyy");
				Date fechaCDde = null;
				try {
					fechaCDde = formatoDeFecha.parse(fechaDiaDde
							+ "/" + (Integer.parseInt(fechaMesDde) + 1)
							+ "/" + fechaAnioDde);
				} catch (Exception e) {
					fechaCDde = null;
				}
				String rta="";
				
				List <Comprobante> cs = ComprobanteServiceUtil.getAvisosPagoByFechaTransferencia(fechaCDde);
				List <Comprobante> csProcesados = new ArrayList<Comprobante>();
				List <Comprobante> csErroneos = new ArrayList<Comprobante>();
				if(!cs.isEmpty()) {
					for(Comprobante c:cs) {
						try {
							Calendar cal = Calendar.getInstance();
							cal.setTime(c.getFechaPrimerPago());
							rta=ClienteProveedoresLPA.setOrdenPagoWithPDF(c.getId(),c.getIdOp(),cal);
							if("OK".equals(rta) ) {
						       csProcesados.add(c);
							}else {
							   csErroneos.add(c);
							}
						}catch(Exception ec) {
							csErroneos.add(c);
							
						}
					}
					
				}
				session.setAttribute(WebKeysComprobantes.COMPROBANTES_RESULT_PROCESADOS,csProcesados);
				session.setAttribute(WebKeysComprobantes.COMPROBANTES_RESULT_ERRONEOS,csErroneos);
				
				return mapping
        				.findForward("portlet.comprobantes.upload_op.result");
			}
///--           
		}
		
		return mapping
 				.findForward("");
	}
	
	
	
	
	
	
}
