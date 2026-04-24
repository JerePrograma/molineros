/**
 */

package ar.com.ospim.farmacia.action;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.RenderRequest;
import javax.servlet.http.HttpServletRequest;

import ar.com.ospim.farmacia.WebKeysFarmacia;
import ar.com.ospim.farmacia.beans.ReintegroMedicamento;
import ar.com.ospim.farmacia.beans.ReintegroMedicamentoItem;
import ar.com.ospim.farmacia.beans.ReporteOrdenPagoReintegrosFarmacia;
import ar.com.ospim.farmacia.services.ReintegroFarmaciaServiceUtil;
import ar.com.ospim.liquidaciones.NoSuchReintegroEntryException;
import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.util.PortalUtil;

/**
 * <a href="ReintegroActionUtil"><b><i>View Source</i></b></a>
 * 
 * @author Carlos Rivas
 * 
 */
public class ReintegroActionUtil {

	public static void getReintegroEntry(HttpServletRequest req)
			throws Exception {

		ReintegroMedicamento reintegro = (ReintegroMedicamento) req
				.getAttribute(WebKeysFarmacia.REINTEGRO_EN_EDICION);
		
		if (reintegro == null) {
			req.getSession().removeAttribute(WebKeysFarmacia.REINTEGRO_PRESTACIONES_EN_EDICION);
			
			int idReintegroAtt = req
					.getAttribute(WebKeysFarmacia.ID_REINTEGRO_EN_EDICION) == null ? 0
					: (Integer) req
							.getAttribute(WebKeysFarmacia.ID_REINTEGRO_EN_EDICION);
			
			int idReintegro = ParamUtil.getInteger(req, "id_reintegro", 0) != 0 ? ParamUtil
					.getInteger(req, "id_reintegro", 0)
					: idReintegroAtt;
			
			List<ReintegroMedicamentoItem> reintPrest = new ArrayList<ReintegroMedicamentoItem>();									
			if (idReintegro != 0) {
				reintegro = ReintegroFarmaciaServiceUtil.getReintegroEntry(idReintegro);
				reintPrest = reintegro.getMedicamentos();
			}
			req.setAttribute(WebKeysFarmacia.REINTEGRO_EN_EDICION,
					reintegro);
			req.getSession().removeAttribute(WebKeysFarmacia.REINTEGRO_PRESTACIONES_EN_EDICION);
			req.getSession().setAttribute(WebKeysFarmacia.REINTEGRO_PRESTACIONES_EN_EDICION,
					reintPrest);
			
			req.getSession().removeAttribute("total_precio_pub");
			req.getSession().removeAttribute("total_cobertura");
						
			req.getSession().setAttribute("total_precio_pub",
					getPrecioPublicoTotal(reintPrest).toString());
			req.getSession().setAttribute("total_cobertura",
					getImporteTotal(reintPrest).toString());

		}
	}

	public static void getReintegroEntry(ActionRequest actionRequest)
			throws Exception {

		HttpServletRequest request = PortalUtil
				.getHttpServletRequest(actionRequest);

		getReintegroEntry(request);
	}

	public static void getReintegroEntry(RenderRequest renderRequest)
			throws Exception {

		HttpServletRequest request = PortalUtil
				.getHttpServletRequest(renderRequest);

		getReintegroEntry(request);
	}

	public static BigDecimal getImporteTotal(List<ReintegroMedicamentoItem> medicamentos) {		
		BigDecimal total = new BigDecimal(0);
		if (medicamentos != null) {
			for (ReintegroMedicamentoItem rPrest : medicamentos) {
				if (rPrest.isDelete() || rPrest.getBaja_fecha() != null) {
					continue;
				}
				total = total
						.add(rPrest != null
								 && rPrest.getImporteCoberturaAmtima() != null ? (((rPrest
								.getImporteCoberturaAmtima().add(rPrest
								.getImporteCoberturaOspim())
								)).add(rPrest.getImporteCoberturaPrestadora()!=null?rPrest.getImporteCoberturaPrestadora():BigDecimal.ZERO))
								/**.multiply(new BigDecimal(rPrest
										.getCantidad()))*/
								: new BigDecimal(0));
				
				total = total.add(rPrest.getImporteCoberturaImesa()!=null?rPrest.getImporteCoberturaImesa():BigDecimal.ZERO);
			}
		}
		return total;
	}

	public static BigDecimal getPrecioPublicoTotal(List<ReintegroMedicamentoItem> medicamentos) {		
		BigDecimal total = new BigDecimal(0);
		if (medicamentos != null) {
			for (ReintegroMedicamentoItem rPrest : medicamentos) {
				if (rPrest.isDelete() || rPrest.getBaja_fecha() != null) {
					continue;
				}
				total = total.add(rPrest != null && rPrest.isDelete() != true
						&& rPrest.getPrecio_al_publico() != null ? (rPrest
						.getPrecio_al_publico()).multiply(new BigDecimal(
						rPrest.getCantidad())) : new BigDecimal(0));
			}
		}
		return total;
	}	
	
	public static List<ReporteOrdenPagoReintegrosFarmacia> getReintegrosFromReporteId(
			int id) throws SystemException, NoSuchReintegroEntryException {
		return ReintegroFarmaciaServiceUtil.getReintegros(id);
	}
}