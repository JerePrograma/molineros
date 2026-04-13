/**
 */
package ar.com.ospim.farmacia.action;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.farmacia.WebKeysFarmacia;
import ar.com.ospim.farmacia.beans.ReintegroMedicamento;
import ar.com.ospim.farmacia.services.ReintegroFarmaciaServiceUtil;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;

/**
 * <a href="BuscarReintegrosAction.java.html"><b><i>View Source</i></b></a>
 * <p>
 * Realiza la búsqueda de reintegros de farmacia según parámetros de entrada
 * 
 * @author Carlos Rivas
 * 
 */
public class BuscarReintegrosAction extends PortletAction {
	private static Log _log = LogFactoryUtil
			.getLog(BuscarReintegrosAction.class);

	public void processAction(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse) throws Exception {
		setForward(actionRequest,
				"portlet.farmacia.reintegros.result.search");
	}

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		try {
			
			String entidad = ParamUtil
					.getString(renderRequest, "entidad", null);
			SimpleDateFormat formatoDeFechas = new SimpleDateFormat(
					"dd/MM/yyyy");
			String fechaDesdeDia = ParamUtil.getString(renderRequest,
					"fechaDesdeDia");
			String fechaDesdeMes = ParamUtil.getString(renderRequest,
					"fechaDesdeMes");
			String fechaDesdeAnio = ParamUtil.getString(renderRequest,
					"fechaDesdeAnio");
			Date fechaDesde = null;
			try {
				fechaDesde = formatoDeFechas.parse(fechaDesdeDia + "/"
						+ (Integer.parseInt(fechaDesdeMes) + 1) + "/"
						+ fechaDesdeAnio);
			} catch (Exception e) {
				fechaDesde = null;
			}
			String fechaHastaDia = ParamUtil.getString(renderRequest,
					"fechaHastaDia");
			String fechaHastaMes = ParamUtil.getString(renderRequest,
					"fechaHastaMes");
			String fechaHastaAnio = ParamUtil.getString(renderRequest,
					"fechaHastaAnio");
			Date fechaHasta = null;
			try {
				fechaHasta = formatoDeFechas.parse(fechaHastaDia + "/"
						+ (Integer.parseInt(fechaHastaMes) + 1) + "/"
						+ fechaHastaAnio);
			} catch (Exception e) {
				fechaHasta = null;
			}
			SimpleDateFormat formatoDePeriodos = new SimpleDateFormat("MM/yyyy");
			String periodoDesdeMesAnio = ParamUtil.getString(renderRequest,
					"periodoDesdeMesAnio", null);
			
			Date periodoDesde = null;
			try {
				String[] periodoDesdeSplit = null;
				if (periodoDesdeMesAnio.length() > 0) {
					 periodoDesdeSplit = periodoDesdeMesAnio.split("_");
				}					
				periodoDesde = formatoDePeriodos.parse(Integer
						.parseInt(periodoDesdeSplit[0])
						+ 1 + "/" + periodoDesdeSplit[1]);
			} catch (Exception e) {
				periodoDesde = null;
			}
			String periodoHastaMesAnio = ParamUtil.getString(renderRequest,
					"periodoHastaMesAnio");
			Date periodoHasta = null;
			try {
				String[] periodoHastaSplit = null;
				if (periodoHastaMesAnio.length() > 0) {
					 periodoHastaSplit = periodoHastaMesAnio.split("_");
				}					
				periodoHasta = formatoDePeriodos.parse(Integer
						.parseInt(periodoHastaSplit[0])
						+ 1 + "/" + periodoHastaSplit[1]);			
				} catch (Exception e) {
				periodoHasta = null;
			}

			int seccional = ParamUtil.getInteger(renderRequest, "id_seccional_r", 0);
			int numero = ParamUtil.getInteger(renderRequest, "numero", 0);
			
			int inte = ParamUtil.getInteger(renderRequest, "inte", 0);
			int nroAfi = ParamUtil.getInteger(renderRequest, "numero_afi", 0);
			String cuil_titular = ParamUtil.getString(renderRequest,
					"cuil_titular");
			
			int estado = ParamUtil.getInteger(renderRequest, "estado", 0);
			
			String pagos = ParamUtil.getString(renderRequest, "pagos", "0");
			
			String alta_usr = ParamUtil.getString(renderRequest, "alta_usr", "");			
			String codPrest = ParamUtil.getString(renderRequest, "codPrest",
					null);

			int id_medicamento = ParamUtil.getInteger(renderRequest, "id_medicamento", 0);			
			int receta = ParamUtil.getInteger(renderRequest, "receta", 0);
			
			PortletSession portletSession = renderRequest.getPortletSession();

			List<ReintegroMedicamento> busqueda = ReintegroFarmaciaServiceUtil
					.buscarReintegros(entidad, fechaDesde, fechaHasta,
							periodoDesde, periodoHasta, codPrest, nroAfi,
							inte, cuil_titular, 
							seccional, numero, estado, alta_usr, id_medicamento, receta);
			renderRequest.removeAttribute(WebKeysFarmacia.BUSQUEDA_REINTEGRO);
			
			renderRequest.setAttribute(WebKeysFarmacia.BUSQUEDA_REINTEGRO,  busqueda);
			
			if (seccional == 0 && numero != 0 && busqueda.size() > 0) {
				
				seccional = busqueda.get(0).getId_seccional();
			}
			renderRequest.setAttribute(
					WebKeysFarmacia.REINTEGRO_DE_SECCIONAL, seccional);
			
			portletSession.removeAttribute(WebKeysFarmacia.BUSQUEDA_REINTEGRO, PortletSession.PORTLET_SCOPE);
			portletSession.setAttribute(WebKeysFarmacia.BUSQUEDA_REINTEGRO, busqueda, PortletSession.PORTLET_SCOPE);
				
		} catch (Exception e) {
			_log.error(e);
		}
		return mapping
				.findForward("portlet.farmacia.reintegros.result.search");
	}
}