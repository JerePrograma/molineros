package ar.com.ospim.compras.requerimientos.action;

import ar.com.ospim.afiliados.beans.Afiliado;
import ar.com.ospim.afiliados.services.EditarAfiliadoServiceUtil;
import ar.com.ospim.compras.WebKeysCompras;
import ar.com.ospim.crm.action.ActualizaDomicilioAfiliadoAction;
import ar.com.ospim.util.PermissionUtil;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.util.PortalUtil;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

public class ActualizarContactoAfiliadoCompraAction
        extends ActualizaDomicilioAfiliadoAction {

    private static final Log _log =
            LogFactoryUtil.getLog(
                    ActualizarContactoAfiliadoCompraAction.class
            );

    public ActionForward render(
            ActionMapping mapping,
            ActionForm form,
            PortletConfig portletConfig,
            RenderRequest renderRequest,
            RenderResponse renderResponse) throws Exception {

        String cuilTitular =
                ParamUtil.getString(renderRequest, "cuil_titular");
        int integrante = ParamUtil.getInteger(renderRequest, "inte");
        String cmd = ParamUtil.getString(renderRequest, "cmd");

        try {
            User user = PortalUtil.getUser(renderRequest);

            if (user == null
                    || !PermissionUtil.userContainsRole(
                    user,
                    WebKeysCompras.ROL_ABM_COMPRAS
            )) {

                throw new Exception(
                        "No posee permisos para actualizar datos de contacto."
                );
            }

            Afiliado afiliado =
                    EditarAfiliadoServiceUtil.getAfiliadoEntry(
                            cuilTitular,
                            integrante
                    );

            if (afiliado == null) {
                throw new Exception(
                        "El afiliado informado no existe."
                );
            }

            if ("bind".equals(cmd)) {
                ActualizarContactoAfiliadoCompraToken.vincular(
                        renderRequest,
                        cuilTitular,
                        integrante
                );
                renderResponse.setContentType("application/json");
                renderResponse.getWriter().write("{\"status\":\"ok\"}");
                return null;
            }

            ActualizarContactoAfiliadoCompraToken.validar(
                    renderRequest,
                    cuilTitular,
                    integrante
            );

            return super.render(
                    mapping,
                    form,
                    portletConfig,
                    renderRequest,
                    renderResponse
            );

        } catch (Exception e) {
            _log.error(
                    "Se rechazo una actualizacion de contacto desde Compras. "
                            + "cuil=" + cuilTitular
                            + ", integrante=" + integrante,
                    e
            );

            throw new Exception(
                    "No se pudo autorizar la actualizacion de contacto."
            );
        }
    }
}
