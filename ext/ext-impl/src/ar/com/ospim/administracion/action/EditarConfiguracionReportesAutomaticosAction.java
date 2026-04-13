package ar.com.ospim.administracion.action;

import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.administracion.WebKeysAdministracion;
import ar.com.ospim.administracion.exception.EmailInvalidoException;
import ar.com.ospim.administracion.exception.ParametroInvalidoException;
import ar.com.ospim.automatico.ReportesScheduler.ReportesAutomaticosConfiguracion;
import ar.com.ospim.automatico.service.ReportesServiceUtil;
import ar.com.ospim.util.StringUtils;

import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;

public class EditarConfiguracionReportesAutomaticosAction extends PortletAction {
	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest req,
			RenderResponse renderResponse) throws Exception {

		ReportesAutomaticosConfiguracion configuracion = ReportesServiceUtil
				.getConfiguracion();

		String mailsFrom = ParamUtil.getString(req, "mail_from");
		String pass = ParamUtil.getString(req, "pass");
		String mailsError = ParamUtil.getString(req, "mails_error");

		if (StringUtils.checkNotEmpty(mailsFrom)) {
			configuracion.setMailFrom(mailsFrom);
			configuracion.setPass(pass);
			configuracion.setMailsDeError(mailsError);

			try {
				validate(configuracion);
				ReportesServiceUtil.update(configuracion);
			} catch (Exception e) {
				SessionErrors.add(req, e.getClass().getName());
			}
			if (SessionErrors.isEmpty(req)) {
				req.setAttribute(WebKeysAdministracion.SUCCESS,
						WebKeysAdministracion.SUCCESS);
			}
		}
		req.setAttribute(
				WebKeysAdministracion.REPORTES_AUTOMATICOS_CONFIGURACION,
				configuracion);

		return mapping.findForward(getForward(req,
				"portlet.administracion.editar_configuracion_reportes"));
	}

	private void validate(ReportesAutomaticosConfiguracion configuracion)
			throws ParametroInvalidoException, EmailInvalidoException {
		if (StringUtils.checkEmpty(configuracion.getMailFrom())) {
			throw new ParametroInvalidoException();
		}
		if (StringUtils.checkEmpty(configuracion.getMailFrom())) {
			throw new ParametroInvalidoException();
		}
		if (StringUtils.checkEmpty(configuracion.getMailFrom())) {
			throw new ParametroInvalidoException();
		}
		for (String mail : configuracion.getMailFrom().split(",")) {
			if (!mail.contains("@")) {
				throw new EmailInvalidoException();
			}
		}

		for (String mail : configuracion.getMailsDeError().split(",")) {
			if (!mail.contains("@")) {
				throw new EmailInvalidoException();
			}
		}
	}
}
