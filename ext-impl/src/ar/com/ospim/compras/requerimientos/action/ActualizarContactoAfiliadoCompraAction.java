package ar.com.ospim.compras.requerimientos.action;

import ar.com.ospim.compras.requerimientos.helper.ConfiguracionComprasHelper;
import ar.com.ospim.compras.requerimientos.helper.RequerimientoCompraOrdenHelper;

import ar.com.ospim.afiliados.beans.Afiliado;
import ar.com.ospim.afiliados.WebKeysAfiliados;
import ar.com.ospim.afiliados.services.EditarAfiliadoServiceUtil;
import ar.com.ospim.compras.WebKeysCompras;
import ar.com.ospim.crm.action.ActualizaDomicilioAfiliadoAction;
import ar.com.ospim.util.PermissionUtil;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.ActionConstants;
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

            validarPermiso(renderRequest, user, cuilTitular, integrante);

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
                return new ActionForward(
                        ActionConstants.COMMON_NULL
                );
            }

            ActualizarContactoAfiliadoCompraToken.validar(
                    renderRequest,
                    cuilTitular,
                    integrante
            );

            renderRequest.setAttribute("COMPRAS_CONTACTO_AFILIADO_VALIDADO", afiliado);
            ActionForward resultado = super.render(
                    mapping,
                    form,
                    portletConfig,
                    renderRequest,
                    renderResponse
            );
            return responderActualizacion(renderRequest, renderResponse, resultado);

        } catch (Exception e) {
            _log.error(
                    "Se rechazó una actualización de contacto desde Compras. "
                            + "cuil=" + cuilTitular
                            + ", integrante=" + integrante,
                    e
            );

            throw new Exception(
                    "No se pudo autorizar la actualización de contacto."
            );
        }
    }

    protected void validarPermiso(RenderRequest request, User user,
            String cuilTitular, int integrante) throws Exception {
        if (user == null) {
            throw new Exception("No posee permisos para actualizar datos de contacto.");
        }
        if (PermissionUtil.userContainsRole(user,
                ConfiguracionComprasHelper.getRolAbmCompras())) {
            return;
        }
        if (!PermissionUtil.userContainsRole(user,
                ConfiguracionComprasHelper.getRolGestionCompras())
                || "bind".equals(ParamUtil.getString(request, "cmd"))) {
            throw new Exception("No posee permisos para actualizar datos de contacto.");
        }
        int idRequerimiento = ActualizarContactoAfiliadoCompraToken
                .validarSuministro(request, cuilTitular, integrante);
        obtenerHelperOrden().validarAfiliadoContactoOrden(
                idRequerimiento, cuilTitular, integrante);
    }

    /** El popup legacy muestra contacto familiar para estos parentescos. */
    protected int obtenerIntegranteContacto(RenderRequest request) {
        Object valor = request.getAttribute("COMPRAS_CONTACTO_AFILIADO_VALIDADO");
        if (!(valor instanceof Afiliado)) {
            throw new IllegalStateException("No se pudo validar el afiliado del contacto.");
        }
        int parentesco = ((Afiliado) valor).getId_parentesco();
        if (parentesco == WebKeysAfiliados.PARENTESCO_DEFAULT
                || parentesco == WebKeysAfiliados.CONYUGE_DEFAULT
                || parentesco == WebKeysAfiliados.CONCUBINO_DEFAULT) {
            return ParamUtil.getInteger(request, "inte");
        }
        return 0;
    }

    protected ActionForward responderActualizacion(RenderRequest request,
            RenderResponse response, ActionForward resultado) throws Exception {
        if (!"save".equals(ParamUtil.getString(request, "cmd"))
                || !ParamUtil.getBoolean(request, "respuesta_contacto_json", false)) {
            return resultado;
        }
        if ("portlet.afiliados.error".equals(
                getForward(request, "portlet.crm.actualiza.domicilio.popup"))) {
            throw new Exception("No se pudieron guardar los datos de contacto.");
        }
        response.setContentType("application/json");
        response.getWriter().write("{\"status\":\"ok\"}");
        return new ActionForward(ActionConstants.COMMON_NULL);
    }

    protected RequerimientoCompraOrdenHelper obtenerHelperOrden() {
        return new RequerimientoCompraOrdenHelper();
    }
}
