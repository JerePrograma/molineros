package ar.com.ospim.administracion.action;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.administracion.WebKeysAdministracion;
import ar.com.ospim.administracion.exception.EmailInvalidoException;
import ar.com.ospim.automatico.ReportesScheduler.ReportesAutomaticosConfiguracion;
import ar.com.ospim.automatico.beans.ReporteAutomatico;
import ar.com.ospim.automatico.service.ReportesServiceUtil;
import ar.com.ospim.util.StringUtils;

import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;

public class EditarReporteAction extends PortletAction {

	private void validar(ReporteAutomatico ra) throws EmailInvalidoException {
		for (String mail : ra.getEmails().split(",")) {
			if (!mail.contains("@")) {
				throw new EmailInvalidoException();
			}
		}
	}

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest req,
			RenderResponse renderResponse) throws Exception {

		String sp = ParamUtil.getString(req, "stored_procedure");
		String java = ParamUtil.getString(req, "java");
		int id = ParamUtil.getInteger(req, "id");
		
		if (StringUtils.checkNotEmpty(sp) || StringUtils.checkNotEmpty(java)) {
			String titulo = ParamUtil.getString(req, "titulo");
			int hora = ParamUtil.getInteger(req, "hora");
			String csvparam = ParamUtil.getString(req, "csv_parameteres");
			String emails = ParamUtil.getString(req, "emails");

			String periodicidad = ParamUtil.getString(req, "periodicidad");

			// DIARIO
			boolean diario = periodicidad.equals("diario");
			boolean incluirFinde = ParamUtil.getBoolean(req,
					"incluir_fin_de_semana");

			// SEMANAL
			int diaSemana = ParamUtil.getInteger(req, "dia_de_la_semana");

			// MENSUAL
			int diaDelMes = 0;
			if (periodicidad.equals("mensual")) {
				String diaDelMesString = ParamUtil.getString(req, "dia_del_mes");
				if (StringUtils.checkNotEmpty(diaDelMesString)) {
					try {
						diaDelMes = Integer.parseInt(diaDelMesString);
					} catch (Exception e) {
					}
				}
			}

			// UNICA VEZ
			Date fechaUnicaVez = null;
			if (periodicidad.equals("unicavez")) {
				SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
				String unicaVezDia = ParamUtil.getString(req, "fechaDiaUnicaVez");
				String unicaVezMes = ParamUtil.getString(req, "fechaMesUnicaVez");
				unicaVezMes = String.valueOf(Integer.valueOf(unicaVezMes) + 1);
				String unicaVezAnio = ParamUtil.getString(req, "fechaAnioUnicaVez");
				try {
					fechaUnicaVez = format.parse(unicaVezDia + "-"
							+ unicaVezMes + "-" + unicaVezAnio);
				} catch (Exception e) {
				}
			}
			Integer difusion = ParamUtil.getInteger(req,"difusion");
			Integer base = ParamUtil.getInteger(req,"base");
			
			ReporteAutomatico ra = new ReporteAutomatico();
			ra.setId(id);
			ra.setTitulo(titulo);
			ra.setDiario(diario);
			ra.setIncluirFinDeSemana(incluirFinde);
			ra.setDiaDeLaSemana(diaSemana);
			ra.setDiaDelMes(diaDelMes);
			ra.setFechaUnicaVez(fechaUnicaVez);
			ra.setHora(hora);
			ra.setStoredProcedure(sp);
			ra.setCsvParameteres(csvparam);
			ra.setEmails(emails);
			ra.setDifusion(difusion);
			ra.setBase(base);
			ra.setJava(java);
			
			req.setAttribute(WebKeysAdministracion.REPORTE_EN_EDICION, ra);

			try {
				validar(ra);
				if (ra.getId() == 0) {
					ReportesServiceUtil.save(ra);
				} else {
					ReportesServiceUtil.update(ra);
				}
			} catch (Exception e) {
				SessionErrors.add(req, e.getClass().getName());
			}

			if (SessionErrors.isEmpty(req)) {
				req.setAttribute(WebKeysAdministracion.SUCCESS, WebKeysAdministracion.SUCCESS);
			}
		} else if (id != 0) {
			ReporteAutomatico ra = ReportesServiceUtil.get(id);
			req.setAttribute(WebKeysAdministracion.REPORTE_EN_EDICION, ra);
		}

		ReportesAutomaticosConfiguracion configuracion = ReportesServiceUtil.getConfiguracion();
		
		req.setAttribute(WebKeysAdministracion.REPORTES_AUTOMATICOS_CONFIGURACION,configuracion);
		
		return mapping.findForward(getForward(req, "portlet.administracion.editar_reporte"));
	}
}
