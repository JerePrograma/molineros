package ar.com.ospim.afiliados.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

import ar.com.ospim.afiliados.WebKeysAfiliados;
import ar.com.ospim.afiliados.beans.Afiliado;
import ar.com.ospim.afiliados.services.CredencialesServiceUtil;

/**
 * <a href="GrabarDocumentacionAction.java.html"><b><i>View Source</i></b></a>
 * <p>
 * Graba las documentaciones
 * 
 * @author Federico Brachi
 * 
 */
public class GenerarCredencialAction extends PortletAction {
	private static Log _log = LogFactoryUtil
			.getLog(GenerarCredencialAction.class);

	public void processAction(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse) throws Exception {
		
	
	}

	public Map<String, Afiliado> listaCredencialesAImprimir(RenderRequest req,
			List<Afiliado> afiliadosEnSession,
			Map<String, Afiliado> mapCredenciales, String cred_selecc) {
		Map<String, Afiliado> credencialesAImprimir = new HashMap<String, Afiliado>();
		if (null != mapCredenciales && mapCredenciales.size() > 0) {
			credencialesAImprimir.putAll(mapCredenciales);
		}

		StringTokenizer cred = new StringTokenizer(cred_selecc, "-");
		while (cred.hasMoreElements()) {
			StringTokenizer cuilInte = new StringTokenizer(cred.nextToken(),
					"|");
			String cuil = cuilInte.nextToken();
			String inte = cuilInte.nextToken();
			for (Afiliado afiliado : afiliadosEnSession) {
				if (afiliado.getCuil_titular().equals(cuil)
						&& afiliado.getInte() == Integer.parseInt(inte)) {
					credencialesAImprimir.put(cuil + '|' + inte, afiliado);
				}

			}
		}

		return sortByValue(credencialesAImprimir);
	}

	@SuppressWarnings("unchecked")
	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {

		PortletSession portletSession = renderRequest.getPortletSession();
		User user = PortalUtil.getUser(renderRequest);
		List<Afiliado> afiliadosList = (List<Afiliado>) portletSession
				.getAttribute(WebKeysAfiliados.BUSQUEDA_AFILIADO_CRED,
						PortletSession.APPLICATION_SCOPE);
		Map<String, Afiliado> mapCredenciales = (Map<String, Afiliado>) portletSession
				.getAttribute(WebKeysAfiliados.CREDENCIALES_A_IMPRIMIR,
						PortletSession.APPLICATION_SCOPE);
		String cred_selecc = ParamUtil.getString(renderRequest, "credenciales");

		renderRequest.removeAttribute(WebKeysAfiliados.BUSQUEDA_AFILIADO);
		
		renderRequest.removeAttribute(WebKeysAfiliados.LISTA_AFILIADOS_EN_SESSION);
		
		try {
			String borrar = ParamUtil.getString(renderRequest, "borrar");
			String borrarLista = ParamUtil.getString(renderRequest,
					"borrarLista");
			String imprimir = ParamUtil.getString(renderRequest, "imprimir");
			if (null != borrar && borrar.equals("true")) {
				String creden = ParamUtil.getString(renderRequest, "creden");
				mapCredenciales.remove(creden);
				portletSession.setAttribute(
						WebKeysAfiliados.CREDENCIALES_A_IMPRIMIR,
						mapCredenciales, PortletSession.APPLICATION_SCOPE);
				return mapping.findForward("portlet.afiliados.credenciales_busqueda_afiliado_cred.result");

			} else if (null != afiliadosList && afiliadosList.size() > 0
					&& null != cred_selecc && cred_selecc.trim().length() > 0) {
				portletSession.setAttribute(
						WebKeysAfiliados.CREDENCIALES_A_IMPRIMIR,
						listaCredencialesAImprimir(renderRequest,
								afiliadosList, mapCredenciales, cred_selecc),
						PortletSession.APPLICATION_SCOPE);
				return mapping.findForward("portlet.afiliados.credenciales_busqueda_afiliado_cred.result");
			} else if (null != borrarLista && borrarLista.trim().equals("true")) {
				mapCredenciales.clear();
				portletSession.setAttribute(
						WebKeysAfiliados.CREDENCIALES_A_IMPRIMIR,
						mapCredenciales, PortletSession.APPLICATION_SCOPE);
			} else if (null != imprimir && imprimir.equals("true")) {
				// grabo las credenciales a imprimir
				int id_lote = CredencialesServiceUtil.generaLoteAImprimir(
						mapCredenciales, user);
				renderRequest.setAttribute("id_lote", id_lote);
				// Quito de session las listas
				return mapping.findForward("portlet.afiliados.credenciales.result");
			
			}else if (null != imprimir && imprimir.equals("EXENTO_DE_COPAGO")) {

				if (mapCredenciales == null){
					return mapping.findForward("portlet.afiliados.credenciales.search");


				}
				ArrayList<Afiliado> afiliados=new ArrayList<Afiliado>(mapCredenciales.values());
				for(Afiliado afi : afiliados) {
					renderRequest.setAttribute("cuil_aux", afi.getCuil_titular());
					renderRequest.setAttribute("inte_aux", afi.getInte());
				
					if(afiliados.size()  == 0 || afiliados.size() > 1 ||
							(CredencialesServiceUtil.validarExisteExentoCopago(afi.getCuil_titular(),afi.getInte())==0)){
							SessionErrors.add(renderRequest, "error-no-existe-exento");
							
							return mapping.findForward("portlet.afiliados.credenciales.search");

 
					}else{
						// grabo las credenciales a imprimir
						CredencialesServiceUtil.generaLoteAImprimir(mapCredenciales, user);
								
						return mapping.findForward("portlet.afiliados.credenciales_exepcion_copago.result");
					}
				}
			}else if (null != imprimir && imprimir.equals("CES")) {
				// grabo las credenciales a imprimir
				int id_lote = CredencialesServiceUtil.generaLoteAImprimir(
						mapCredenciales, user);
				renderRequest.setAttribute("id_lote", id_lote);
				// Quito de session las listas
				return mapping.findForward("portlet.afiliados.credenciales_ces.result");
			}  
		} catch (Exception e) {
			_log.error(e);
			e.printStackTrace();
			SessionErrors.add(renderRequest, Exception.class.getName());

		}
		
		return mapping.findForward("portlet.afiliados.credenciales.search");

	}

	@SuppressWarnings("unchecked")
	static Map<String, Afiliado> sortByValue(Map map) {
		List list = new LinkedList(map.entrySet());
		Collections.sort(list, new Comparator() {
			public int compare(Object o1, Object o2) {
				return ((Comparable) ((Map.Entry) (o1)).getValue())
						.compareTo(((Map.Entry) (o2)).getValue());
			}
		});
		Map result = new LinkedHashMap();
		for (Iterator it = list.iterator(); it.hasNext();) {
			Map.Entry entry = (Map.Entry) it.next();
			result.put(entry.getKey(), entry.getValue());
		}
		return result;
	}
}