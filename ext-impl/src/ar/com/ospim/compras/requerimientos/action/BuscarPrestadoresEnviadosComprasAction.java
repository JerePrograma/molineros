package ar.com.ospim.compras.requerimientos.action;

import ar.com.ospim.compras.WebKeysCompras;
import ar.com.ospim.compras.requerimientos.beans.PrestadorCotizacion;
import ar.com.ospim.compras.requerimientos.helper.BusquedaRequerimientoCompraHelper;
import ar.com.ospim.util.PermissionUtil;

import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import java.util.ArrayList;
import java.util.List;

public class BuscarPrestadoresEnviadosComprasAction extends PortletAction {

    private final BusquedaRequerimientoCompraHelper busquedaHelper =
            new BusquedaRequerimientoCompraHelper();

    public ActionForward render(ActionMapping mapping,
                                ActionForm form,
                                PortletConfig portletConfig,
                                RenderRequest renderRequest,
                                RenderResponse renderResponse) throws Exception {

        List<PrestadorCotizacion> prestadores = new ArrayList<PrestadorCotizacion>();

        try {
            validarPermisoCotizar(PortalUtil.getUser(renderRequest));

            int idRequerimiento =
                    ParamUtil.getInteger(renderRequest, "id_requerimiento_compra", 0);
            String texto =
                    ParamUtil.getString(renderRequest, "texto", null);
            int limite =
                    ParamUtil.getInteger(renderRequest, "limite", 20);

            prestadores =
                    busquedaHelper.buscarPrestadoresEnviados(
                            idRequerimiento,
                            texto,
                            limite
                    );
        } catch (Exception e) {
            renderRequest.setAttribute(WebKeysCompras.ERROR_PARA_ALERT, e.getMessage());
        }

        renderRequest.setAttribute(
                WebKeysCompras.PRESTADORES_ENVIADOS_COTIZACION,
                prestadores
        );

        return mapping.findForward(WebKeysCompras.FORWARD_COMPRAS_PRESTADORES_ENVIADOS);
    }

    private void validarPermisoCotizar(User user) throws Exception {
        if (user == null
                || !PermissionUtil.userContainsRole(user, WebKeysCompras.ROL_COTIZAR_COMPRAS)) {
            throw new Exception("No posee permisos para buscar prestadores enviados.");
        }
    }
}
