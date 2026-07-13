package ar.com.ospim.compras.requerimientos.action;

import ar.com.ospim.compras.WebKeysCompras;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompra;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraSector;
import ar.com.ospim.compras.requerimientos.service.BusquedaRequerimientoCompraServiceUtil;
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
import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Pattern;

public class BuscarItemTecnicoComprasAction extends PortletAction {

    private static final Pattern DIACRITICOS =
            Pattern.compile("[\\p{InCombiningDiacriticalMarks}]");

    private final RequerimientoCompraDetalleHelper detalleHelper =
            new RequerimientoCompraDetalleHelper();

    public ActionForward render(ActionMapping mapping,
                                ActionForm form,
                                PortletConfig portletConfig,
                                RenderRequest request,
                                RenderResponse response) throws Exception {

        User user = PortalUtil.getUser(request);
        detalleHelper.validarPermisoABM(user);

        request.setAttribute(
                "COMPRAS_CALLBACK_BUSQUEDA",
                response.getNamespace() + "seleccionarNomencladorDetalle"
        );

        buscarNomenclador(request);

        return mapping.findForward("portlet.compras.buscar_item_tecnico");
    }

    private void buscarNomenclador(RenderRequest request) throws Exception {
        String codigo = ParamUtil.getString(request, "codigo", "").trim();
        String descripcion = ParamUtil.getString(request, "descripcion", "").trim();
        String sector = resolverSector(request);

        if (codigo.length() == 0 && descripcion.length() == 0) {
            request.setAttribute(
                    "COMPRAS_ERROR_BUSQUEDA",
                    "Ingrese codigo o descripcion."
            );
            return;
        }

        if ("PRESTACIONES MEDICAS".equals(sector)) {
            request.setAttribute("COMPRAS_ES_PREST_MED", "1");
        } else if ("LEGALES".equals(sector)) {
            request.setAttribute("COMPRAS_ES_PREST_MED", "0");
        } else {
            request.setAttribute(
                    "COMPRAS_ERROR_BUSQUEDA",
                    "El sector no admite prestaciones de nomenclador."
            );
            return;
        }

        request.setAttribute("COMPRAS_CODIGO_NOMENCLADOR", codigo);
        request.setAttribute("COMPRAS_DESCRIPCION_NOMENCLADOR", descripcion);
        request.setAttribute("COMPRAS_ID_TIPO_NOMENCLADOR", "0");
    }

    private String resolverSector(RenderRequest request) throws Exception {
        int idRequerimiento =
                ParamUtil.getInteger(request, "id_requerimiento_compra", 0);

        if (idRequerimiento > 0) {
            RequerimientoCompra requerimiento =
                    BusquedaRequerimientoCompraServiceUtil
                            .getRequerimientoCompra(idRequerimiento);

            if (requerimiento == null) {
                throw new Exception("No se encontro el requerimiento informado.");
            }

            if (!requerimiento.puedeEditarEstructura()) {
                throw new Exception(
                        "Solo se pueden buscar prestaciones para requerimientos PENDIENTES."
                );
            }

            return normalizar(requerimiento.getSectorDescripcion());
        }

        int idSector = ParamUtil.getInteger(request, "sector_id", 0);
        RequerimientoCompraSector sector =
                BusquedaRequerimientoCompraServiceUtil.getSector(idSector);

        if (sector == null || sector.getIdSector() <= 0) {
            throw new Exception("El sector informado no es valido.");
        }

        return normalizar(sector.getDescripcion());
    }

    private String normalizar(String value) {
        value = value == null ? "" : value.trim();
        String normalized = Normalizer.normalize(value, Normalizer.Form.NFD);
        normalized = DIACRITICOS.matcher(normalized).replaceAll("");
        return normalized.toUpperCase(Locale.ROOT);
    }
}
