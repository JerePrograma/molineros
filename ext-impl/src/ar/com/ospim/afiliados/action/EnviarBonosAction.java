/**
 */

package ar.com.ospim.afiliados.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.portlet.PortletConfig;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.afiliados.WebKeysAfiliados;
import ar.com.ospim.afiliados.beans.EnvioBonos;
import ar.com.ospim.afiliados.exceptions.BonoNoCargadoException;
import ar.com.ospim.afiliados.exceptions.DuplicateEnvioBonosException;
import ar.com.ospim.afiliados.exceptions.EnvioBonosNoExisteEnSeccionalException;
import ar.com.ospim.afiliados.services.EnviaBonosServiceUtil;
import ar.com.ospim.util.DateUtils;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

/**
 * <a href="EnviarBonosAction.java.html"><b><i>View Source</i></b></a>
 * <p>
 * Graba los bonos enviados
 * 
 * @author Federico Brachi
 * 
 */
public class EnviarBonosAction extends PortletAction {
	private static Log _log = LogFactoryUtil.getLog(EnviarBonosAction.class);

	@SuppressWarnings("unchecked")
	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {

		User user = PortalUtil.getUser(renderRequest);
		PortletSession portletSession = renderRequest.getPortletSession();
		Date fecha_envio = null;
		Date fecha_rendicion = null;
		User user_anula = PortalUtil.getUser(renderRequest); //usuario que anula
		Date fecha_anula = null; // fecha anula
		
		String fecha_anula_string = ParamUtil.getString(renderRequest,"fecha_anula");						
		
		if (null != fecha_anula_string && !fecha_anula_string.trim().equals("")) {
			fecha_anula = DateUtils.parse(fecha_anula_string,
					"dd/MM/yyyy");
		}
		
		String accion = ParamUtil.getString(renderRequest, "accion");

		String fecha_envio_string = ParamUtil.getString(renderRequest,
				"fecha_envio");
		if (null != fecha_envio_string && !fecha_envio_string.trim().equals("")) {
			fecha_envio = DateUtils.parse(fecha_envio_string, "dd/MM/yyyy");
		}

		String fecha_rendicion_string = ParamUtil.getString(renderRequest,
				"fecha_rendicion");
		if (null != fecha_rendicion_string && !fecha_rendicion_string.trim().equals("")) {
			fecha_rendicion = DateUtils.parse(fecha_rendicion_string,
					"dd/MM/yyyy");
		}
		
		//fecha_anula=fecha_rendicion;
		//fecha_anula_string =fecha_rendicion_string ;
		
		String tipo_bono_string = ParamUtil.getString(renderRequest,
				"tipo_bono");
		String[] tipo_bono_array = tipo_bono_string.split("-");

		int tipo_bono = 0;

		if (null != accion && !accion.trim().equals("liberar")) {
			tipo_bono = Integer.parseInt(tipo_bono_array[0]);
		}
		int seccional = ParamUtil.getInteger(renderRequest, "id_seccional");
		int bono_desde = ParamUtil.getInteger(renderRequest, "bono_desde");
		int bono_hasta = ParamUtil.getInteger(renderRequest, "bono_hasta");
		int id_envio = ParamUtil.getInteger(renderRequest, "id_envio");

		try {
			int result = 0;
			List<EnvioBonos> envioBonosNuevo = null;
			List<EnvioBonos> envioBonos = (ArrayList<EnvioBonos>) portletSession
					.getAttribute(WebKeysAfiliados.ENVIO_BONOS,
							PortletSession.APPLICATION_SCOPE);
			if (0 != seccional || accion.trim().equals("liberar") || accion.trim().equals("cargar")) {
				if (null != accion && accion.trim().equals("liberar")) {
					result = EnviaBonosServiceUtil.liberaEnvioBonosRetornaLista(id_envio, user);
					if (envioBonos != null && result == 0) {
						for (EnvioBonos eb : envioBonos) {
							if (eb.getIdEnvio() == id_envio) {
								envioBonos.remove(eb);
							}
						}
					}
				}

				if (null != accion && accion.trim().equals("rendir")) {
					result = EnviaBonosServiceUtil.rindeEnvioBonosRetornaLista(tipo_bono, seccional, fecha_rendicion, bono_desde,bono_hasta, user);
					envioBonosNuevo = new ArrayList<EnvioBonos>();
					
					envioBonosNuevo.add(new EnvioBonos(tipo_bono_string,0,String.valueOf(seccional),fecha_envio,bono_desde,bono_hasta,0,fecha_rendicion,bono_hasta+1-bono_desde,fecha_anula ));
				
					
					
				} else if (null != accion && accion.trim().equals("cargar")) {
					result = EnviaBonosServiceUtil.grabaBonosRetornaLista(tipo_bono, fecha_envio, bono_desde, bono_hasta, user);
					envioBonosNuevo = new ArrayList<EnvioBonos>();
					envioBonosNuevo.add(new EnvioBonos(tipo_bono_string, 0,"No Enviado", new Date(System.currentTimeMillis()),bono_desde, bono_hasta, 0, fecha_rendicion, bono_hasta+1-bono_desde,fecha_anula));

				} else if (null != accion && accion.trim().equals("anular")) {
					result = EnviaBonosServiceUtil.anulaEnvioBonosRetornaLista(tipo_bono, seccional, fecha_anula , bono_desde,bono_hasta, user);
					envioBonosNuevo = new ArrayList<EnvioBonos>();					
					envioBonosNuevo.add(new EnvioBonos(tipo_bono_string,0,String.valueOf(seccional),fecha_envio,bono_desde,bono_hasta,0,fecha_rendicion,bono_hasta+1-bono_desde,fecha_anula ));
			
				} else {
					envioBonosNuevo = EnviaBonosServiceUtil.grabaEnvioBonosRetornaLista(tipo_bono, seccional,fecha_envio, bono_desde, bono_hasta, user);
				}				
				
				if (envioBonos != null) {
					envioBonos.addAll(envioBonosNuevo);
				} else {
					envioBonos = envioBonosNuevo;
				}
				portletSession.setAttribute(WebKeysAfiliados.ENVIO_BONOS,
						envioBonos, PortletSession.APPLICATION_SCOPE);
			}

		} catch (BonoNoCargadoException e) {
			_log.error(e);
			SessionErrors.add(renderRequest, BonoNoCargadoException.class
					.getName());
		} catch (DuplicateEnvioBonosException e) {
			_log.error(e);
			SessionErrors.add(renderRequest, DuplicateEnvioBonosException.class
					.getName());
		} catch (EnvioBonosNoExisteEnSeccionalException e) {
			_log.error(e);
			SessionErrors.add(renderRequest,
					EnvioBonosNoExisteEnSeccionalException.class.getName());
		} catch (Exception e) {
			_log.error(e);
			SessionErrors.add(renderRequest, Exception.class.getName());
		}
		if (SessionErrors.isEmpty(renderRequest)) {
			SessionMessages.add(renderRequest, "request_processed", "");
		}
		return mapping.findForward("portlet.ver.bonos.enviados");
	}

}