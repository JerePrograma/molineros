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

public class BuscarItemTecnicoComprasAction extends PortletAction {

    private final RequerimientoCompraDetalleHelper detalleHelper =
            new RequerimientoCompraDetalleHelper();

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

            request.setAttribute(
                    "COMPRAS_ERROR_BUSQUEDA",
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
            request.setAttribute(
                    "COMPRAS_ERROR_BUSQUEDA",
                    "El sector seleccionado no tiene configurado "
                            + "un nomenclador para Compras."
            );

            return;
        }

        /*
         * PRESTACIONES MEDICAS no tiene un único tipo fijo por sector.
         *
         * El tipo concreto debe ser el seleccionado por el usuario
         * en el editor del detalle.
         *
         * Se valida nuevamente en servidor para no confiar en el
         * parámetro recibido desde JavaScript.
         */
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

                request.setAttribute(
                        "COMPRAS_ERROR_BUSQUEDA",
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
                BusquedaRequerimientoCompraServiceUtil
                        .getSector(
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
}