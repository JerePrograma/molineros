package ar.com.ospim.compras.requerimientos.action;

import ar.com.ospim.autorizaciones.beans.Nomenclador;
import ar.com.ospim.compras.WebKeysCompras;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompra;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraSector;
import ar.com.ospim.compras.requerimientos.helper.BusquedaRequerimientoCompraHelper;
import ar.com.ospim.compras.requerimientos.helper.NomencladorCompraBusquedaHelper;

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

    private final BusquedaRequerimientoCompraHelper busquedaHelper =
            new BusquedaRequerimientoCompraHelper();

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

        String sector =
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

        if ("PRESTACIONES MEDICAS".equals(
                sector
        )) {

            int idTipoNomencladorSolicitado =
                    ParamUtil.getInteger(
                            request,
                            "id_tipo_nomenclador",
                            0
                    );

            if (!WebKeysCompras
                    .esTipoNomencladorPrestacionesMedicas(
                            idTipoNomencladorSolicitado
                    )) {

                publicarError(
                        request,
                        "Debe seleccionar un Tipo Nomenclador válido "
                                + "para PRESTACIONES MEDICAS."
                );

                return;
            }

            filtroTipoNomenclador =
                    Integer.valueOf(
                            idTipoNomencladorSolicitado
                    );
        }

        int marcaReinLiq =
                "DISCAPACIDAD".equals(sector)
                        ? WebKeysCompras
                        .MARCA_REIN_LIQ_DISCAPACIDAD
                        : 0;

        request.setAttribute(
                "COMPRAS_SECTOR_NOMENCLADOR",
                sector
        );

        request.setAttribute(
                "COMPRAS_MARCA_REIN_LIQ",
                String.valueOf(
                        marcaReinLiq
                )
        );

        request.setAttribute(
                "COMPRAS_ES_PREST_MED",
                "PRESTACIONES MEDICAS".equals(sector)
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

    private String resolverSector(
            RenderRequest request) throws Exception {

        int idRequerimiento =
                ParamUtil.getInteger(
                        request,
                        "id_requerimiento_compra",
                        0
                );

        if (idRequerimiento > 0) {
            RequerimientoCompra requerimiento =
                    busquedaHelper.getRequerimientoCompra(
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

            return WebKeysCompras
                    .normalizarSectorCompra(
                            requerimiento
                                    .getSectorDescripcion()
                    );
        }

        int idSector =
                ParamUtil.getInteger(
                        request,
                        "sector_id",
                        0
                );

        RequerimientoCompraSector sector =
                busquedaHelper.getSector(
                        idSector
                );

        if (sector == null
                || sector.getIdSector() <= 0) {

            throw new Exception(
                    "El sector informado no es válido."
            );
        }

        return WebKeysCompras
                .normalizarSectorCompra(
                        sector.getDescripcion()
                );
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
