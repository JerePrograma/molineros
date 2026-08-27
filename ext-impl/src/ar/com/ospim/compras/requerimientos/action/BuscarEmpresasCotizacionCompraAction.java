package ar.com.ospim.compras.requerimientos.action;

import ar.com.ospim.afiliados.WebKeysAfiliados;
import ar.com.ospim.compras.WebKeysCompras;
import ar.com.ospim.global.beans.Empresa;
import ar.com.ospim.global.services.EmpresaServiceUtil;
import ar.com.ospim.util.PermissionUtil;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

import java.util.ArrayList;
import java.util.List;

import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * Adaptador de búsqueda del padrón legacy de Empresas para Compras.
 */
public class BuscarEmpresasCotizacionCompraAction extends PortletAction {

    private static final Log _log =
            LogFactoryUtil.getLog(
                    BuscarEmpresasCotizacionCompraAction.class
            );

    private static final int MAX_RESULTADOS = 100;

    public ActionForward render(
            ActionMapping mapping,
            ActionForm form,
            PortletConfig portletConfig,
            RenderRequest renderRequest,
            RenderResponse renderResponse) throws Exception {

        validarPermiso(
                PortalUtil.getUser(renderRequest)
        );

        boolean buscar =
                ParamUtil.getBoolean(
                        renderRequest,
                        "buscar",
                        false
                );

        String cuit =
                WebKeysCompras.trimToNull(
                        ParamUtil.getString(
                                renderRequest,
                                "cuit",
                                null
                        )
                );

        String sucursal =
                WebKeysCompras.trimToNull(
                        ParamUtil.getString(
                                renderRequest,
                                "sucu",
                                null
                        )
                );

        String descripcion =
                WebKeysCompras.trimToNull(
                        ParamUtil.getString(
                                renderRequest,
                                "descripcion",
                                null
                        )
                );

        List<Empresa> resultados =
                new ArrayList<Empresa>();

        String error = null;
        boolean limitada = false;

        if (buscar) {
            if (cuit == null
                    && sucursal == null
                    && descripcion == null) {

                error =
                        "Debe informar CUIT, sucursal o razón social para buscar.";

            } else if (cuit != null && cuit.length() > 11) {
                error = "El CUIT informado supera la longitud permitida.";

            } else if (sucursal != null && sucursal.length() > 6) {
                error = "La sucursal informada supera la longitud permitida.";

            } else if (descripcion != null
                    && descripcion.length() > 200) {

                error =
                        "La razón social informada supera la longitud permitida.";

            } else {
                try {
                    List<Empresa> empresas =
                            EmpresaServiceUtil.getEmpleadores(
                                    cuit,
                                    descripcion,
                                    sucursal,
                                    0
                            );

                    if (empresas == null) {
                        error =
                                "No se pudo consultar el padrón de empleadores.";

                    } else {
                        for (int i = 0; i < empresas.size(); i++) {
                            Empresa empresa = empresas.get(i);

                            if (empresa == null
                                    || empresa.getBaja_fecha() != null) {

                                continue;
                            }

                            if (resultados.size() >= MAX_RESULTADOS) {
                                limitada = true;
                                break;
                            }

                            resultados.add(empresa);
                        }
                    }

                } catch (Exception e) {
                    _log.error(
                            "No se pudo buscar Empresas para una cotización "
                                    + "de Compras.",
                            e
                    );

                    error =
                            "No se pudo consultar el padrón de empleadores.";
                }
            }
        }

        renderRequest.setAttribute(
                WebKeysAfiliados.BUSQUEDA_EMPLEADORES,
                resultados
        );

        renderRequest.setAttribute(
                "compras.empresas.busqueda.realizada",
                Boolean.valueOf(buscar)
        );

        renderRequest.setAttribute(
                "compras.empresas.busqueda.limitada",
                Boolean.valueOf(limitada)
        );

        renderRequest.setAttribute(
                "compras.empresas.busqueda.error",
                error
        );

        return mapping.findForward(
                "portlet.compras.empresas.result.search"
        );
    }

    private void validarPermiso(User user) throws Exception {
        if (user == null
                || !PermissionUtil.userContainsRole(
                        user,
                        WebKeysCompras.ROL_COTIZAR_COMPRAS
                )) {

            throw new Exception(
                    "No posee permisos para buscar Empresas "
                            + "para cotizaciones de Compras."
            );
        }
    }
}
