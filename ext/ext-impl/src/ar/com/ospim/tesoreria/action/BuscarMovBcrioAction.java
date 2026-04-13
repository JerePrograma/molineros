package ar.com.ospim.tesoreria.action;

import java.util.GregorianCalendar;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.tesoreria.WebKeysTesoreria;
import ar.com.ospim.tesoreria.beans.MovimientoBancario;
import ar.com.ospim.tesoreria.services.MovimientoBancarioServiceUtil;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;

/**
 * <a href="BuscarMovBcrioAction.java.html"><b><i>View Source</i></b></a>
 * <p>
 * Realiza la búsqueda de movs bcrios según parámetros de entrada
 * 
 * @author Federico Brachi
 * 
 */
public class BuscarMovBcrioAction extends PortletAction {
	private static Log _log = LogFactoryUtil.getLog(BuscarMovBcrioAction.class);

	public void processAction(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse) throws Exception {
		setForward(actionRequest, "portlet.afiliados.result.search");

	}

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {

		try {
			int entidad=WebKeysGlobal.OSPIM;
			
			if(renderResponse.getNamespace().equals("_FAR_1_")){
				entidad=WebKeysGlobal.AMTIMA;
			}else if(renderResponse.getNamespace().equals("_UOM_1_")){
				entidad=WebKeysGlobal.UOMA;
			}	
			
			int tipo_mov = ParamUtil.getInteger(renderRequest, "tipo_mov");

			int desde_dia = ParamUtil.getInteger(renderRequest, "desde_dia");
			int desde_mes = ParamUtil.getInteger(renderRequest, "desde_mes");
			int desde_anio = ParamUtil.getInteger(renderRequest, "desde_anio");

			GregorianCalendar fecha_desde = null;
			if (desde_dia != 0 && desde_anio != 0) {
				fecha_desde = new GregorianCalendar(desde_anio, desde_mes,
						desde_dia);
			}

			int hasta_dia = ParamUtil.getInteger(renderRequest, "hasta_dia");
			int hasta_mes = ParamUtil.getInteger(renderRequest, "hasta_mes");
			int hasta_anio = ParamUtil.getInteger(renderRequest, "hasta_anio");

			GregorianCalendar fecha_hasta = null;
			if (hasta_dia != 0 && hasta_anio != 0) {
				fecha_hasta = new GregorianCalendar(hasta_anio, hasta_mes,
						hasta_dia);
			}

			int cta_bcria = ParamUtil.getInteger(renderRequest, "cta_bcria");
			String descripcion = ParamUtil.getString(renderRequest,
					"descripcion");

			List<MovimientoBancario> busqueda = MovimientoBancarioServiceUtil
					.buscaMovimientoBcrio(fecha_desde.getTime(),
							fecha_hasta.getTime(), cta_bcria, descripcion,
							tipo_mov, entidad);
			renderRequest.setAttribute(WebKeysTesoreria.MOVS_BCRIOS, busqueda);

		} catch (Exception e) {
			_log.error(e);
			throw e;
		}

		return mapping.findForward("portlet.tesoreria.buscar.movs.result");

	}

}
