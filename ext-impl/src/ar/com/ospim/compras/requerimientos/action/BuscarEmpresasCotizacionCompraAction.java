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
                    < WebKeysCompras.MIN_CARACTERES_RAZON_SOCIAL_EMPRESA) {

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
                                    WebKeysCompras.LIMITE_EMPRESAS_COTIZACION_CON_MARCA
                            );

                    if (empresas == null) {
                        error =
                                "No se pudo consultar el padrón de empleadores.";

                    } else {
                        int cantidadVisible = Math.min(
                                empresas.size(),
                                WebKeysCompras.MAX_RESULTADOS_EMPRESAS_COTIZACION
                        );

                        for (int i = 0; i < cantidadVisible; i++) {
                            resultados.add(empresas.get(i));
                        }

                        limitada = empresas.size() > WebKeysCompras.MAX_RESULTADOS_EMPRESAS_COTIZACION;
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
                WebKeysCompras.ATTR_EMPRESAS_BUSQUEDA_REALIZADA,
                Boolean.valueOf(buscar)
        );

        renderRequest.setAttribute(
                WebKeysCompras.ATTR_EMPRESAS_BUSQUEDA_LIMITADA,
                Boolean.valueOf(limitada)
        );

        renderRequest.setAttribute(
                WebKeysCompras.ATTR_EMPRESAS_BUSQUEDA_ERROR,
                error
        );

        return mapping.findForward(
                WebKeysCompras.FORWARD_COMPRAS_EMPRESAS_RESULT_SEARCH
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
