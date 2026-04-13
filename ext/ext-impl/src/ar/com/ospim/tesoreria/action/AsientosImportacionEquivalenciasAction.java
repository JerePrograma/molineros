package ar.com.ospim.tesoreria.action;

import java.io.File;
import java.math.BigDecimal;
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

import ar.com.ospim.global.FechaMenorACierreContableException;
import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.beans.ConceptoSueldos;
import ar.com.ospim.global.beans.PlanCuentas;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.procesaArchivos.ProcesaArchivos;
import ar.com.ospim.tesoreria.WebKeysTesoreria;
import ar.com.ospim.tesoreria.beans.contabilidad.Asiento;
import ar.com.ospim.tesoreria.beans.contabilidad.Asiento.Detalle;
import ar.com.ospim.tesoreria.service.AsientoServiceUtil;
import ar.com.ospim.tesoreria.service.ContabilidadServiceUtil;
import ar.com.ospim.util.DateUtils;

import com.liferay.ibm.icu.util.Calendar;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.upload.UploadPortletRequest;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

public class AsientosImportacionEquivalenciasAction extends PortletAction {
	public void processAction(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse) throws Exception {
		
		
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
		String cmd = renderRequest.getParameter("cmd");
		if(cmd==null) cmd = ParamUtil.get(renderRequest, "cmd","");
		if(cmd!=null) {
			if("update_equivalencias".equals(cmd)) {
				ConceptoSueldos conc = new ConceptoSueldos();
				Integer id = ParamUtil.getInteger(renderRequest,"id");
				Integer codigo= ParamUtil.getInteger(renderRequest,"codigo");
				Integer sector= ParamUtil.getInteger(renderRequest,"sector");
				String  enti= ParamUtil.getString(renderRequest,"entidad");
				String  descripcion= ParamUtil.getString(renderRequest,"descripcion");
				String debeHaber=ParamUtil.getString(renderRequest, "debeHaber");
				Integer cuentaId=ParamUtil.getInteger(renderRequest, "cuentaId");
				
				PlanCuentas pc = TraeListasServiceUtil.getCuentaById(cuentaId,new Date(), entidad);
				
				conc.setId(id);
				conc.setCodigo(codigo);
				conc.setSectorLiquidado(sector);
				conc.setEntidad(enti);
				conc.setDescripcion(descripcion);
				conc.setDebeHaber(debeHaber);
				conc.setCuentaContable(pc);
				
				id = ContabilidadServiceUtil.updateEquivalenciasSueldos(conc);
				
				List<ConceptoSueldos> lista = (List<ConceptoSueldos>) session.getAttribute(WebKeysTesoreria.LISTA_DETALLE_ASIENTO_SUELDOS);
				
				for (int i = 0; i < lista.size(); i++) {
					ConceptoSueldos c=lista.get(i);
					if(c.equals(conc)) {
						c.setId(id);
						c.setError("OK");
						c.setConProblema(false);
						c.setDebeHaber(conc.getDebeHaber());
						c.setCuentaContable(conc.getCuentaContable());
						c.setSectorLiquidado(conc.getSectorLiquidado());
						lista.set(i, c);
					}
				}
				
				session.setAttribute(WebKeysTesoreria.LISTA_DETALLE_ASIENTO_SUELDOS,lista); 
				session.setAttribute(WebKeysTesoreria.EQUIVALENCIAS_SUELDOS_EN_EDICION,conc);
				return mapping.findForward(getForward(renderRequest,
							"portlet.tesoreria.contabilidad.editar_asientos_sueldos_search_result"));
			}else if("review".equals(cmd)) {
				 return mapping.findForward(getForward(renderRequest,
							"portlet.tesoreria.contabilidad.editar_asientos_sueldos_search_result"));
			}else if("delete_equivalencias".equals(cmd)) {
				Integer id = ParamUtil.getInteger(renderRequest,"id");
				
				ContabilidadServiceUtil.deleteEquivalenciasSueldos(id);
				
				List<ConceptoSueldos> lista = (List<ConceptoSueldos>) session.getAttribute(WebKeysTesoreria.LISTA_DETALLE_ASIENTO_SUELDOS);
				
				for (int i = 0; i < lista.size(); i++) {
					ConceptoSueldos c=lista.get(i);
					if(c.getId()==id) {
						c.setId(id);
						c.setError("SE");
						c.setConProblema(true);
						c.setDebeHaber(null);
						c.setCuentaContable(null);
						lista.set(i, c);
						break;
					}
				}
				
				session.setAttribute(WebKeysTesoreria.LISTA_DETALLE_ASIENTO_SUELDOS,lista); 
				return mapping.findForward(getForward(renderRequest,
							"portlet.tesoreria.contabilidad.editar_asientos_sueldos_search_result"));
			}else if("asiento_view".equals(cmd)) {
				List<ConceptoSueldos> lista = (List<ConceptoSueldos>) session.getAttribute(WebKeysTesoreria.LISTA_DETALLE_ASIENTO_SUELDOS);
				Integer idNeteo = ParamUtil.getInteger(renderRequest,"neteo");
				Asiento asiento = (Asiento) session.getAttribute(WebKeysTesoreria.ASIENTO_SUELDO_EN_SESSION);
				if(asiento==null) asiento=new Asiento();
				asiento=ContabilidadServiceUtil.buildAsientoSueldos(entiSdo, asiento, lista, idNeteo);
				
				session.setAttribute(WebKeysTesoreria.ASIENTO_SUELDO_EN_SESSION,asiento); 
				
				return mapping.findForward(getForward(renderRequest,
							"portlet.tesoreria.contabilidad.visualizar_asiento_sueldo"));
			}
			
		}
		
		
		return mapping.findForward(getForward(renderRequest,
				"portlet.tesoreria.contabilidad.editar_asientos_sueldos"));
	}
}
