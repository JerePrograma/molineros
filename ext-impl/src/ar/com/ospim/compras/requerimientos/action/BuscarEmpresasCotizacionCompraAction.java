package ar.com.ospim.compras.requerimientos.action;

import ar.com.ospim.compras.WebKeysCompras;
import ar.com.ospim.compras.requerimientos.service.BusquedaRequerimientoCompraServiceUtil;
import ar.com.ospim.global.beans.Empresa;
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
 * Búsqueda acotada de Empresas para cotizaciones internas de Compras.
 */
public class BuscarEmpresasCotizacionCompraAction extends PortletAction {

    private static final Log _log =
            LogFactoryUtil.getLog(
                    BuscarEmpresasCotizacionCompraAction.class
            );

    private static final int MAX_RESULTADOS = 100;
    private static final int LIMITE_CON_MARCA = MAX_RESULTADOS + 1;
    private static final int MIN_CARACTERES_RAZON_SOCIAL = 3;

    public ActionForward render(
            ActionMapping mapping,
            ActionForm form,
            PortletConfig portletConfig,
            RenderRequest renderRequest,
            RenderResponse renderResponse) throws Exception {

        validarPermiso(
                PortalUtil.getUser(renderRequest)
        );

        int idRequerimientoCompra =
                ParamUtil.getInteger(
                        renderRequest,
                        WebKeysCompras.PARAM_ID_REQUERIMIENTO_COMPRA,
                        0
                );

        validarRequerimientoInterno(
                idRequerimientoCompra
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
            if (cuit == null && descripcion == null) {

                error =
                        "Debe informar un CUIT completo o al menos tres "
                                + "caracteres de raz\u00f3n social.";

            } else if (cuit != null && !cuit.matches("^[0-9]{11}$")) {
                error = "El CUIT debe contener exactamente 11 d\u00edgitos.";

            } else if (sucursal != null && sucursal.length() > 6) {
                error = "La sucursal informada supera la longitud permitida.";

            } else if (cuit == null
                    && descripcion != null
                    && descripcion.length()
                    < MIN_CARACTERES_RAZON_SOCIAL) {

                error =
                        "La raz\u00f3n social debe contener al menos tres "
                                + "caracteres.";

            } else if (descripcion != null
                    && descripcion.length() > 200) {

                error =
                        "La razón social informada supera la longitud permitida.";

            } else {
                try {
                    List<Empresa> empresas =
                            BusquedaRequerimientoCompraServiceUtil
                                    .buscarEmpresasCotizacionRapida(
                                    cuit,
                                    descripcion,
                                    sucursal,
                                    LIMITE_CON_MARCA
                            );

                    if (empresas == null) {
                        error =
                                "No se pudo consultar el padrón de empleadores.";

                    } else {
                        int cantidadVisible = Math.min(
                                empresas.size(),
                                MAX_RESULTADOS
                        );

                        for (int i = 0; i < cantidadVisible; i++) {
                            resultados.add(empresas.get(i));
                        }

                        limitada = empresas.size() > MAX_RESULTADOS;
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
                WebKeysCompras.BUSQUEDA_EMPRESAS_COTIZACION,
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

    private void validarRequerimientoInterno(
            int idRequerimientoCompra) throws Exception {

        if (idRequerimientoCompra <= 0) {
            throw new Exception(
                    "Debe informar el requerimiento de compra."
            );
        }

        if (!BusquedaRequerimientoCompraServiceUtil
                .esRequerimientoHabilitadoBusquedaEmpresaCotizacion(
                        idRequerimientoCompra
                )) {

            throw new Exception(
                    "El requerimiento informado no admite "
                            + "cotizaciones de Empresas."
            );
        }
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
