/**
 */

package ar.com.ospim.afiliados.action;

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
import ar.com.ospim.afiliados.exceptions.DuplicateEnvioBonosException;
import ar.com.ospim.afiliados.services.EnviaBonosServiceUtil;
import ar.com.ospim.util.DateUtils;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;

/**
 * <a href="BuscarBonosAction.java.html"><b><i>View Source</i></b></a>
 * <p>
 * Busca los bonos enviados
 * 
 * @author Federico Brachi
 * 
 */
public class BuscarBonosAction extends PortletAction {
	private static Log _log = LogFactoryUtil.getLog(BuscarBonosAction.class);

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {

		PortletSession portletSession = renderRequest.getPortletSession();
		Date fecha_desde = null;
		String fecha_desde_string = ParamUtil.getString(renderRequest,
				"fecha_desde");
		if (null != fecha_desde_string) {
			fecha_desde = DateUtils.parse(fecha_desde_string, "dd/MM/yyyy");
		}
		Date fecha_hasta = null;
		String fecha_hasta_string = ParamUtil.getString(renderRequest,
				"fecha_hasta");
		if (null != fecha_hasta_string) {
			fecha_hasta = DateUtils.parse(fecha_hasta_string, "dd/MM/yyyy");
		}
		String tipo_bono_string = ParamUtil.getString(renderRequest,
				"tipo_bono");
		String[] tipo_bono_array = tipo_bono_string.split("-");
		int tipo_bono = 0;
		if (!tipo_bono_array[0].equals("TODOS")) {
			tipo_bono = Integer.parseInt(tipo_bono_array[0]);
		}
		int seccional = ParamUtil.getInteger(renderRequest, "id_seccional");
		int bono_desde = ParamUtil.getInteger(renderRequest, "bono_desde");
		int bono_hasta = ParamUtil.getInteger(renderRequest, "bono_hasta");

		boolean sin_rendir = ParamUtil.getBoolean(renderRequest, "sin_rendir");
		boolean sin_enviar = ParamUtil.getBoolean(renderRequest, "sin_enviar");
		boolean rendidos = ParamUtil.getBoolean(renderRequest, "rendidos");
		boolean anulados = ParamUtil.getBoolean(renderRequest, "anulados");
		try {
			List<EnvioBonos> envioBonos = null;
			
			envioBonos = EnviaBonosServiceUtil.buscaBonosRetornaLista(
					tipo_bono, seccional, fecha_desde, fecha_hasta, bono_desde,
					bono_hasta, rendidos, sin_rendir, sin_enviar,anulados );

			portletSession.setAttribute(WebKeysAfiliados.ENVIO_BONOS,
					envioBonos, PortletSession.APPLICATION_SCOPE);

		} catch (DuplicateEnvioBonosException e) {
			_log.error(e);
			SessionErrors.add(renderRequest, DuplicateEnvioBonosException.class
					.getName());
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