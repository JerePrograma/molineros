package ar.com.ospim.global.actions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.global.beans.Concepto;
import ar.com.ospim.liquidaciones.services.ConceptoServiceUtil;
import ar.com.ospim.util.StringUtils;

import com.liferay.portal.kernel.util.ParamUtil;


public class TraeConceptosCuitComproTipoAction extends
		TraeConceptosJSONAction {

	@Override
	public String getJSON(ActionMapping arg0, ActionForm arg1,
			HttpServletRequest req, HttpServletResponse arg3) throws Exception {

		try {
			int entidad=ParamUtil.getInteger(req, "entidad");
			String comproTipo=ParamUtil.getString(req, "compro_tipo");
			String cuit=ParamUtil.getString(req, "cuit");			
			
			Concepto concepto = null;
			
			if(StringUtils.checkNotEmpty(cuit) && StringUtils.checkNotEmpty(comproTipo)) {
				concepto= ConceptoServiceUtil.getConceptoCuitComproTipo(cuit, comproTipo, entidad);
			}
			return getConceptoJSON(concepto, false); // fuerzo la excepcion porque es null el concepto.
		} catch (Exception e) {
			return "{\"status\":\"falla_inesperada\"}";
		}
	}
}
