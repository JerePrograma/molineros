package ar.com.ospim.prestadores.action;

import java.io.File;
import java.util.Collections;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import ar.com.ospim.prestadores.services.ImportarCartillaSOPServiceUtil;
import ar.com.ospim.util.PermissionUtil;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.upload.UploadPortletRequest;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.security.auth.PrincipalException;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

public class ImportarCartillaSOPAction
    extends PortletAction {

    private static final String FORWARD = "portlet.prestadores.cartilla_sop";

    @Override
    public ActionForward render(
            ActionMapping mapping,
            ActionForm form,
            PortletConfig portletConfig,
            RenderRequest renderRequest,
            RenderResponse renderResponse)
        throws Exception {

        validarPermiso(PortalUtil.getUser(renderRequest));

        List<Object[]> importaciones = Collections.emptyList();

        try {
            importaciones = ImportarCartillaSOPServiceUtil.getImportacionesCartillaSOP();

        } catch (Exception e) {

            _log.error("Error consultando importaciones de Cartilla SOP",e);

            SessionErrors.add(renderRequest,"error-cartilla-sop-consulta");
        }

        renderRequest.setAttribute("importacionesCartillaSOP",importaciones
        );

        return mapping.findForward(FORWARD);
    }

    @Override
    public void processAction(
            ActionMapping mapping,
            ActionForm form,
            PortletConfig portletConfig,
            ActionRequest actionRequest,
            ActionResponse actionResponse)
        throws Exception {

        validarPermiso(PortalUtil.getUser(actionRequest));

        UploadPortletRequest uploadRequest =PortalUtil.getUploadPortletRequest(actionRequest);

        String redirect =ParamUtil.getString(uploadRequest,"redirect");

        File archivo =uploadRequest.getFile("archivo");

        String nombreArchivo =uploadRequest.getFileName("archivo");

        if (archivo == null || !archivo.exists() || archivo.length() == 0) {
            SessionErrors.add(actionRequest,"error-cartilla-sop-archivo");

            redirigirCartillaSOP(redirect,actionResponse);

            return;
        }

        if (nombreArchivo == null) {

            SessionErrors.add(actionRequest,"error-cartilla-sop-formato");

            redirigirCartillaSOP(redirect,actionResponse);

            return;
        }

        /*
         * Evita problemas si el navegador envía una
         * ruta completa.
         */
        String nombreNormalizado =nombreArchivo.replace("\\","/");

        int ultimaBarra =nombreNormalizado.lastIndexOf("/");

        if (ultimaBarra >= 0) {
            nombreNormalizado =nombreNormalizado.substring(ultimaBarra + 1);
        }

        if (!"cartilla_sop.xlsx".equalsIgnoreCase(nombreNormalizado.trim())) {
            SessionErrors.add(actionRequest,"error-cartilla-sop-nombre");

            redirigirCartillaSOP(redirect,actionResponse);

            return;
        }

        try {
            ImportarCartillaSOPServiceUtil.importarCartillaSOP(archivo);
            SessionMessages.add(actionRequest,"cartilla-sop-importada");

        } catch (IllegalArgumentException e) {

            _log.warn("Archivo de Cartilla SOP rechazado: " +e.getMessage());

            SessionErrors.add(actionRequest,"error-cartilla-sop-formato");

        } catch (Exception e) {

            _log.error("Error procesando Cartilla SOP",e);

            SessionErrors.add(actionRequest,"error-cartilla-sop-procesamiento");
        }

        redirigirCartillaSOP(redirect,actionResponse);
    }

    private void redirigirCartillaSOP(
            String redirect,
            ActionResponse actionResponse)
        throws Exception {

        if (redirect != null &&redirect.trim().length() > 0) {
            actionResponse.sendRedirect(redirect);

            return;
        }

        actionResponse.setRenderParameter("struts_action","/prestadores/view");
        actionResponse.setRenderParameter("tabs1","cartilla-sop");
    }

    private void validarPermiso(User user)
        throws PrincipalException {

        if (user == null ||
            (!PermissionUtil.userContainsRole(user,"ABM_PRESTADOR") &&
             !PermissionUtil.userContainsRole(user,"VIEW_PRESTADOR"))) {

            throw new PrincipalException();
        }
    }

    private static final Log _log = LogFactoryUtil.getLog(ImportarCartillaSOPAction.class);
}
