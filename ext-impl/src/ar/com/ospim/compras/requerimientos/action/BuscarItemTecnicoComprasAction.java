package ar.com.ospim.compras.requerimientos.action;

import ar.com.ospim.autorizaciones.beans.Nomenclador;
import ar.com.ospim.compras.WebKeysCompras;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompra;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraSector;
import ar.com.ospim.compras.requerimientos.beans.TipoPrestacionCompra;
import ar.com.ospim.compras.requerimientos.helper.NomencladorCompraBusquedaHelper;
import ar.com.ospim.compras.requerimientos.service.BusquedaRequerimientoCompraServiceUtil;

import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import java.util.ArrayList;
import java.util.List;

import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

public class BuscarItemTecnicoComprasAction extends PortletAction {

    private final RequerimientoCompraDetalleHelper detalleHelper =
            new RequerimientoCompraDetalleHelper();

    private final NomencladorCompraBusquedaHelper nomencladorHelper =
            new NomencladorCompraBusquedaHelper();

    public ActionForward render(
            ActionMapping mapping,
            ActionForm form,
            PortletConfig portletConfig,
            RenderRequest request,
            RenderResponse response) throws Exception {

        User user =
                PortalUtil.getUser(
                        request
                );

        detalleHelper.validarPermisoABM(
                user
        );

        request.setAttribute(
                "COMPRAS_CALLBACK_BUSQUEDA",
                response.getNamespace()
                        + "seleccionarNomencladorDetalle"
        );

        buscarNomenclador(
                request
        );

        return mapping.findForward(
                "portlet.compras.buscar_item_tecnico"
        );
    }

    private void buscarNomenclador(
            RenderRequest request) throws Exception {

        String codigo =
                ParamUtil.getString(
                        request,
                        "codigo",
                        ""
                ).trim();

        String descripcion =
                ParamUtil.getString(
                        request,
                        "descripcion",
                        ""
                ).trim();

        RequerimientoCompraSector sector =
                resolverSector(
                        request
                );

        if (codigo.length() == 0
                && descripcion.length() == 0) {

            publicarError(
                    request,
                    "Ingrese código o descripción."
            );

            return;
        }

        int idTipoPrestacion =
                resolverTipoPrestacion(
                        request,
                        sector
                );

        Integer filtroTipoNomenclador =
                WebKeysCompras
                        .getFiltroTipoNomencladorCompras(
                                sector
                        );

        if (filtroTipoNomenclador == null) {
            publicarError(
                    request,
                    "El sector seleccionado no tiene configurado "
                            + "un nomenclador para Compras."
            );

            return;
        }

        int marcaReinLiq = 0;

        request.setAttribute(
                "COMPRAS_SECTOR_NOMENCLADOR",
                sector.getDescripcion()
        );

        request.setAttribute(
                "COMPRAS_MARCA_REIN_LIQ",
                String.valueOf(
                        marcaReinLiq
                )
        );

        request.setAttribute(
                "COMPRAS_ES_PREST_MED",
                sector.isBusquedaNomencladorMedica()
                        ? "1"
                        : "0"
        );

        request.setAttribute(
                "COMPRAS_CODIGO_NOMENCLADOR",
                codigo
        );

        request.setAttribute(
                "COMPRAS_DESCRIPCION_NOMENCLADOR",
                descripcion
        );

        request.setAttribute(
                "COMPRAS_ID_TIPO_NOMENCLADOR",
                String.valueOf(
                        filtroTipoNomenclador.intValue()
                )
        );

        try {
            List<Nomenclador> resultados =
                    nomencladorHelper.buscar(
                            sector,
                            idTipoPrestacion,
                            filtroTipoNomenclador.intValue(),
                            marcaReinLiq,
                            codigo,
                            descripcion
                    );

            request.setAttribute(
                    "COMPRAS_RESULTADOS_NOMENCLADOR",
                    resultados != null
                            ? resultados
                            : new ArrayList<Nomenclador>()
            );

        } catch (Exception e) {
            publicarError(
                    request,
                    e.getMessage() != null
                            ? e.getMessage()
                            : "No se pudieron buscar nomencladores."
            );
        }
    }

    private int resolverTipoPrestacion(
            RenderRequest request,
            RequerimientoCompraSector sector) throws Exception {

        int idTipoPrestacion =
                ParamUtil.getInteger(
                        request,
                        "id_tipo_prestacion",
                        0
                );

        if (idTipoPrestacion <= 0) {
            throw new Exception(
                    "Debe seleccionar el tipo de prestación."
            );
        }

        List<TipoPrestacionCompra> tipos =
                BusquedaRequerimientoCompraServiceUtil
                        .listarTiposPrestacion();

        for (int i = 0; tipos != null && i < tipos.size(); i++) {
            TipoPrestacionCompra tipo = tipos.get(i);

            if (tipo != null
                    && tipo.getIdInt() == idTipoPrestacion
                    && tipo.getIdSectorInt() == sector.getIdSector()) {
                return idTipoPrestacion;
            }
        }

        throw new Exception(
                "El tipo de prestación no corresponde al sector "
                        + "del requerimiento."
        );
    }

    private RequerimientoCompraSector resolverSector(
            RenderRequest request) throws Exception {

        int idRequerimiento =
                ParamUtil.getInteger(
                        request,
                        "id_requerimiento_compra",
                        0
                );

        if (idRequerimiento > 0) {
            RequerimientoCompra requerimiento =
                    BusquedaRequerimientoCompraServiceUtil
                            .getRequerimientoCompra(
                            idRequerimiento
                    );

            if (requerimiento == null) {
                throw new Exception(
                        "No se encontró el requerimiento informado."
                );
            }

            if (!requerimiento.puedeEditarEstructura()) {
                throw new Exception(
                        "Sólo se pueden buscar prestaciones para "
                                + "requerimientos PENDIENTES."
                );
            }

            return requerimiento.getSectorConfiguracion();
        }

        int idSector =
                ParamUtil.getInteger(
                        request,
                        "sector_id",
                        0
                );

        RequerimientoCompraSector sector =
                BusquedaRequerimientoCompraServiceUtil.getSector(
                        idSector
                );

        if (sector == null
                || sector.getIdSector() <= 0) {

            throw new Exception(
                    "El sector informado no es válido."
            );
        }

        return sector;
    }

    private void publicarError(
            RenderRequest request,
            String mensaje) {

        request.setAttribute(
                "COMPRAS_ERROR_BUSQUEDA",
                mensaje
        );

        request.setAttribute(
                "COMPRAS_RESULTADOS_NOMENCLADOR",
                new ArrayList<Nomenclador>()
        );
    }
}
