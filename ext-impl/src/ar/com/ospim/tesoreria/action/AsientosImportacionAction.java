package ar.com.ospim.tesoreria.action;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.liferay.ibm.icu.util.Calendar;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.upload.UploadPortletRequest;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

import ar.com.ospim.global.FechaMenorACierreContableException;
import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.beans.ConceptoSueldos;
import ar.com.ospim.global.beans.PlanCuentas;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.procesaArchivos.ProcesaArchivos;
import ar.com.ospim.tesoreria.WebKeysTesoreria;
import ar.com.ospim.tesoreria.beans.contabilidad.Asiento;
import ar.com.ospim.tesoreria.service.AsientoServiceUtil;
import ar.com.ospim.tesoreria.service.ContabilidadServiceUtil;
import ar.com.ospim.util.DateUtils;

public class AsientosImportacionAction extends PortletAction {
	public void processAction(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse) throws Exception {
		
		String entidad="O";
		Integer entidadNro=WebKeysGlobal.OSPIM;
		List<String>errores = new ArrayList<String>();
		
		if(actionResponse.getNamespace().equals("_FAR_1_")){
			entidad="A";
			entidadNro=WebKeysGlobal.AMTIMA;
		}else if(actionResponse.getNamespace().equals("_UOM_1_")){
			entidad="U";
			entidadNro=WebKeysGlobal.UOMA;
		}

		HttpSession session = PortalUtil.getHttpServletRequest(actionRequest)
				.getSession();

		UploadPortletRequest uploadReq = null;
		try {
		   uploadReq = PortalUtil
					.getUploadPortletRequest(actionRequest);
		}catch(Exception xx) {}   
		
		Asiento asiento =asiento = (Asiento) session.getAttribute(WebKeysTesoreria.ASIENTO_SUELDO_EN_SESSION); 
		if(asiento==null) {		
			asiento= new Asiento();
		}
		
		try {
			if(uploadReq!=null && uploadReq.getFileName("archivo")!=null) {
			   String fileName = uploadReq.getFileName("archivo").toLowerCase();
			   List<ConceptoSueldos> lista = null;
			   Boolean proceso=false;
			   if (fileName != null) {
				File zip = uploadReq.getFile("archivo");
				if ( fileName.endsWith(".xls")) {
					proceso=true;
					Integer ccosto=Integer.parseInt(actionRequest.getParameter("sector"));
					Integer cuentaNeteo=Integer.parseInt(actionRequest.getParameter("neteo"));
					lista = new ProcesaArchivos().procesarSueldosXLS(actionRequest, zip,fileName,entidad,ccosto,cuentaNeteo);
					session.setAttribute(WebKeysTesoreria.ASIENTO_SUELDOS_CUENTA_NETEO,cuentaNeteo);
					session.setAttribute(WebKeysTesoreria.ASIENTO_SUELDOS_SECTOR_LIQUIDADO,ccosto);
					
					if(lista!=null && lista.size()>0) {
						List<ConceptoSueldos> listaSession =(List<ConceptoSueldos>) session.getAttribute(WebKeysTesoreria.LISTA_DETALLE_ASIENTO_SUELDOS);
						if(listaSession==null) listaSession= new ArrayList<ConceptoSueldos>();
						listaSession.addAll(lista);
						session.setAttribute(WebKeysTesoreria.LISTA_DETALLE_ASIENTO_SUELDOS,listaSession);
					}
				}	
				
			   }
			}
			
			
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");			
			if (StringUtils.isNotBlank(actionRequest.getParameter("id"))) {
				asiento.setId(Integer.parseInt(actionRequest.getParameter("id")));
			}
			String cantString = actionRequest.getParameter("cantidad");
			String htaString = actionRequest.getParameter("ejercicio_hasta");
			String ddString = actionRequest.getParameter("ejercicio_desde");
			String ejercicio = actionRequest.getParameter("ejercicio");
			String fechaString = actionRequest.getParameter("fecha");
			String descripcion = actionRequest.getParameter("descripcion");

			if (StringUtils.isNotBlank(ejercicio) && entidadNro!=WebKeysGlobal.AMTIMA) {
				ddString = "01/08/" + Integer.valueOf(ejercicio.split("-")[0]);
				htaString = "31/07/" + Integer.valueOf(ejercicio.split("-")[1]);
			}
			if (StringUtils.isNotBlank(ejercicio) && entidadNro==WebKeysGlobal.AMTIMA) {
				ddString = "01/07/" + Integer.valueOf(ejercicio.split("-")[0]);
				htaString = "30/06/" + Integer.valueOf(ejercicio.split("-")[1]);
			}

			asiento.setDescripcion(descripcion);
			asiento.setEjercicioDesdeString(ddString);
			asiento.setEjercicioHastaString(htaString);
			if(fechaString!=null && !"".equals(fechaString)) {
			  asiento.setFechaString(fechaString);
			}  

			session.setAttribute(WebKeysTesoreria.ASIENTO_SUELDO_EN_SESSION,asiento);
			
			
		} catch (Exception e) {
			actionRequest.setAttribute("asiento", asiento);
			SessionErrors.add(actionRequest, e.getClass().getName());
			if(e.getClass().getName().contains("OldExcelFormatException")) {
			   errores.add("Error en Versión de Excel - Debe ser 97-2003 ");
			}
			if(errores.size()>0) {
			   actionRequest.setAttribute("errores", errores);
			}   
		}

		if (SessionErrors.isEmpty(actionRequest)) {
			String successMessage = ParamUtil.getString(actionRequest,
					"successMessage");
			SessionMessages.add(actionRequest, "request_processed",
					successMessage);
		}

	}

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		
		int entidad=WebKeysGlobal.OSPIM;
		String entiSdo="O";		
		if(renderResponse.getNamespace().equals("_FAR_1_")){
			entidad=WebKeysGlobal.AMTIMA;
			entiSdo="A";
		}else if(renderResponse.getNamespace().equals("_UOM_1_")){
			entidad=WebKeysGlobal.UOMA;
			entiSdo="U";
		}

		HttpSession session = PortalUtil.getHttpServletRequest(renderRequest)
				.getSession();
		
		PortletSession portletSession = renderRequest.getPortletSession();
		
		Asiento asiento = null;
		Date fecha = null;//new Date();
		
		if(fecha==null){
			String ejercicio_seleccionado=(String)portletSession.getAttribute("ejercicio_seleccionado", PortletSession.PORTLET_SCOPE);
			if(ejercicio_seleccionado!=null && !ejercicio_seleccionado.trim().equals("")){
				Calendar fechaCal=Calendar.getInstance();
				fechaCal.set(Calendar.YEAR,Integer.parseInt(ejercicio_seleccionado.substring(0, 4)));
				fechaCal.set(Calendar.MONTH,7);
				fechaCal.set(Calendar.DAY_OF_MONTH,1);
				fecha=fechaCal.getTime();
			}
		}
		if(fecha==null){
			fecha=new Date();
		}
		
		renderRequest.setAttribute("planCuentas",
				TraeListasServiceUtil.getPlanCuentasImputables(fecha, entidad));
		
		String cmd = renderRequest.getParameter("cmd");
		if(cmd==null) cmd = ParamUtil.get(renderRequest, "cmd","");
		if(cmd!=null) {
			if("clean".equals(cmd)) {
				 session.setAttribute(WebKeysTesoreria.LISTA_DETALLE_ASIENTO_SUELDOS,new ArrayList<ConceptoSueldos>());
				 return mapping.findForward(getForward(renderRequest,
							"portlet.tesoreria.contabilidad.editar_asientos_sueldos_search_result"));
			} else if("equivalencias".equals(cmd)) {
				ConceptoSueldos conc = new ConceptoSueldos();
				Integer id = ParamUtil.getInteger(renderRequest,"id");
				Integer codigo= ParamUtil.getInteger(renderRequest,"codigo");
				Integer sector= ParamUtil.getInteger(renderRequest,"sector");
				String  enti= ParamUtil.getString(renderRequest,"entidad");
				String  descripcion= ParamUtil.getString(renderRequest,"descripcion");
				Integer cuentaid = ParamUtil.getInteger(renderRequest,"cuentaid");
				String  debehaber= ParamUtil.getString(renderRequest,"debehaber");
				conc.setId(id);
				conc.setCodigo(codigo);
				conc.setSectorLiquidado(sector);
				conc.setEntidad(enti);
				conc.setDescripcion(descripcion);
				conc.setCuentaContable(new PlanCuentas(cuentaid));
				conc.setDebeHaber(debehaber);
				
				 
				session.setAttribute(WebKeysTesoreria.EQUIVALENCIAS_SUELDOS_EN_EDICION,conc);
				return mapping.findForward(getForward(renderRequest,
							"portlet.tesoreria.contabilidad.equivalencias_asientos_sueldos"));
			}else if("save".equals(cmd)) {
				SessionMessages.clear(renderRequest);
				SessionErrors.clear(renderRequest);
				asiento = (Asiento) session.getAttribute(WebKeysTesoreria.ASIENTO_SUELDO_EN_SESSION);
				
				if (DateUtils.compararFechasTruncarEnDia(
						ContabilidadServiceUtil.getFechaCierreAsientos(entidad),
						asiento.getFecha()) > 0) {
					throw new FechaMenorACierreContableException();
				}

				User user = PortalUtil.getUser(renderRequest);
				if (asiento.getId() == 0) {
					
					List<ConceptoSueldos> lista = (List<ConceptoSueldos>) session.getAttribute(WebKeysTesoreria.LISTA_DETALLE_ASIENTO_SUELDOS);
					
					if(lista!=null && lista.size()>0) {
					
						Integer idNeteo = ParamUtil.getInteger(renderRequest,"neteo");
						asiento=ContabilidadServiceUtil.buildAsientoSueldos(entiSdo, asiento, lista, idNeteo);
						AsientoServiceUtil.save(asiento, user, entidad);
						String successMessage = ParamUtil.getString(renderRequest,
								"successMessage");
						SessionMessages.add(renderRequest, "request_processed",	successMessage);
						session.setAttribute(WebKeysTesoreria.ASIENTO_SUELDO_EN_SESSION,new Asiento());
					}else {
						SessionErrors.add(renderRequest, "No tiene registros preparados para  generar el asiento");
					}
					
				} 
				
			    session.setAttribute(WebKeysTesoreria.LISTA_DETALLE_ASIENTO_SUELDOS,new ArrayList<ConceptoSueldos>());
				return mapping.findForward(getForward(renderRequest,
							"portlet.tesoreria.contabilidad.editar_asientos_sueldos"));
			} 
			
		}
		
		return mapping.findForward(getForward(renderRequest,
				"portlet.tesoreria.contabilidad.editar_asientos_sueldos"));
	}
}
