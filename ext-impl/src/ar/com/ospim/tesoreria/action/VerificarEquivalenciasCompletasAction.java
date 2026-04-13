package ar.com.ospim.tesoreria.action;

import java.util.Calendar;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.stringtree.util.StringUtils;

import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.liquidaciones.services.ConceptoServiceUtil;
import ar.com.ospim.util.DateUtils;

import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.JSONAction;

public class VerificarEquivalenciasCompletasAction extends JSONAction {

	@Override
	public String getJSON(ActionMapping arg0, ActionForm arg1,
			HttpServletRequest req, HttpServletResponse arg3) throws Exception {

		try {
			int entidad=ParamUtil.getInteger(req, "entidad");
			if(entidad==0){
				entidad=WebKeysGlobal.OSPIM;
			}
			
			Date fechaDesde = null;
			Date fechaFin = null;
			if (StringUtils.isBlank(req.getParameter("periodo_desde"))
					&& StringUtils.isBlank(req.getParameter("periodo"))) {
				fechaDesde = DateUtils.getFechaDesde(req);
				fechaFin = DateUtils.getFechaHasta(req);
			} else if (!StringUtils.isBlank(req.getParameter("periodo_desde"))) {
				fechaDesde = DateUtils.getDesdePeriodo(req, entidad).getTime();
				fechaFin = DateUtils.getHastaPeriodo(req, entidad).getTime();
			} else {
				Calendar desdeC = DateUtils.getDesdeEjercicio(req, entidad);				
				Calendar hastaC = DateUtils.getHastaEjercicio(req, entidad);
								
				fechaDesde = desdeC.getTime();
				fechaFin = hastaC.getTime();
			}

			boolean equivalenciasConceptosCompleto = ConceptoServiceUtil
					.verificarEquivalenciasConceptosCompleto(fechaDesde,
							fechaFin, entidad);
			if (!equivalenciasConceptosCompleto) {
				return "{\"status\":\"equivalencias_conceptos_incompleto\"}";
			}
			
			boolean equivalenciasPrestacionesCompleto = true;
			
			if(entidad==WebKeysGlobal.OSPIM){
				equivalenciasPrestacionesCompleto = ConceptoServiceUtil
						.verificarEquivalenciasPrestacionesCompleto(fechaDesde,
								fechaFin, entidad);
			}
			if (!equivalenciasPrestacionesCompleto) {
				return "{\"status\":\"equivalencias_prestaciones_incompleto\"}";
			}
			
		} catch (Exception e) {
			return "{\"status\":\"falla_inesperada\"}";
		}
		return "{\"status\":\"ok\"}";
	}
}
