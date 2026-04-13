package ar.com.ospim.liquidaciones.action;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.liquidaciones.services.NomencladorServiceUtil;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;

/**
 * <a href="IncrementarNomenclador.java.html"><b><i>View Source</i></b></a>
 * <p>
 * IncrementarNomenclador
 * 
 * @author Gustavo Fernandez
 * 
 */
public class IncrementarNomencladorAction extends PortletAction {
	private static Log _log = LogFactoryUtil
			.getLog(IncrementarNomencladorAction.class);

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {

		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());

		String vigAumentoDia = ParamUtil.getString(renderRequest,
				"vigAumentoDia");
		SimpleDateFormat formatoDePeriodos = new SimpleDateFormat("MM/yyyy");
		String[] vigAumentoMesAnio = ParamUtil.getString(renderRequest,
				"vigAumentoMesAnio").split(",");

		Date vigAumento = null;
		try {
			vigAumento = formatoDePeriodos.parse(Integer
					.parseInt(vigAumentoMesAnio[0])
					+ vigAumentoDia
					+ "/"
					+ vigAumentoMesAnio[1]);
		} catch (Exception e) {
			vigAumento = null;
		}
		if (vigAumento == null) {
			vigAumento = formatoDePeriodos.parse(Integer.parseInt("01") + "/"
					+ (calendar.get(Calendar.YEAR) - 1));
		}

		BigDecimal porc_aumento = BigDecimal.valueOf(ParamUtil.getDouble(renderRequest, "porc_aumento",0));
		String resolucion = ParamUtil.getString(renderRequest, "resolucion");
		boolean ttos = ParamUtil.getBoolean(renderRequest, "ttos", false);
		int nomenclador = ParamUtil.getInteger(renderRequest, "nomenclador", 0);
		String usuario_modi = ParamUtil.getString(renderRequest, "usuario_modi");
		int cod_desde = ParamUtil.getInteger(renderRequest, "cod_desde", 0);
		int cod_hasta = ParamUtil.getInteger(renderRequest, "cod_hasta", 0);

		try {

			NomencladorServiceUtil.getIncrementarNomenclador(vigAumento,
					porc_aumento, resolucion, ttos, nomenclador, usuario_modi,
					cod_desde, cod_hasta);

		} catch (Exception e) {
			_log.error(e);
			SessionErrors.add(renderRequest, e.getClass().getName());			
		}
		if (SessionErrors.isEmpty(renderRequest)) {
			SessionMessages.add(renderRequest, "request_processed", "");
		}

		return mapping
				.findForward("portlet.liquidaciones.aumento_prestaciones");
	}

}