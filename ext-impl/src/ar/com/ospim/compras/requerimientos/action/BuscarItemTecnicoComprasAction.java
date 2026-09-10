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
                WebKeysCompras.ATTR_CALLBACK_BUSQUEDA_NOMENCLADOR,
                response.getNamespace()
                        + "seleccionarNomencladorDetalle"
        );

        buscarNomenclador(
                request
        );

        return mapping.findForward(
                WebKeysCompras.FORWARD_COMPRAS_BUSCAR_ITEM_TECNICO
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

        if (WebKeysCompras.SECTOR_PRESTACIONES_MEDICAS.equals(
                sector
        )) {
            /*
             * El tipo de prestacion fue validado contra el catalogo del
             * sector. INSUMOS consulta exclusivamente el tipo 10; los
             * demas tipos medicos consultan la clasificacion general,
             * cuya busqueda excluye el tipo 10.
             */
            filtroTipoNomenclador =
                    WebKeysCompras.esTipoPrestacionInsumos(
                            idTipoPrestacion
                    )
                            ? Integer.valueOf(
                                    WebKeysCompras
                                            .TIPO_NOMENCLADOR_PROTESIS_INSUMOS
                            )
                            : Integer.valueOf(0);
        }

        int marcaReinLiq =
                WebKeysCompras.SECTOR_DISCAPACIDAD.equals(sector)
                        ? WebKeysCompras
                        .MARCA_REIN_LIQ_DISCAPACIDAD
                        : 0;

        request.setAttribute(
                WebKeysCompras.ATTR_SECTOR_NOMENCLADOR,
                sector
        );

        request.setAttribute(
                WebKeysCompras.ATTR_MARCA_REIN_LIQ,
                String.valueOf(
                        marcaReinLiq
                )
        );

        request.setAttribute(
                WebKeysCompras.ATTR_ES_PRESTACIONES_MEDICAS,
                WebKeysCompras.SECTOR_PRESTACIONES_MEDICAS.equals(sector)
                        ? "1"
                        : "0"
        );

        request.setAttribute(
                WebKeysCompras.ATTR_CODIGO_NOMENCLADOR,
                codigo
        );

        request.setAttribute(
                WebKeysCompras.ATTR_DESCRIPCION_NOMENCLADOR,
                descripcion
        );

        request.setAttribute(
                WebKeysCompras.ATTR_ID_TIPO_NOMENCLADOR,
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
                    WebKeysCompras.ATTR_RESULTADOS_NOMENCLADOR,
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
            String sector) throws Exception {

        int idTipoPrestacion =
                ParamUtil.getInteger(
                        request,
                        "id_tipo_prestacion",
                        0
                );

        if (!WebKeysCompras.SECTOR_PRESTACIONES_MEDICAS.equals(sector)) {
            return idTipoPrestacion;
        }

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
                    && sector.equals(
                            WebKeysCompras.normalizarSectorCompra(
                                    tipo.getSectorDescripcion()
                            )
                    )) {
                return idTipoPrestacion;
            }
        }

        throw new Exception(
                "El tipo de prestación no corresponde al sector "
                        + "del requerimiento."
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
                BusquedaRequerimientoCompraServiceUtil.getSector(
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
                WebKeysCompras.ATTR_ERROR_BUSQUEDA_NOMENCLADOR,
                mensaje
        );

        request.setAttribute(
                WebKeysCompras.ATTR_RESULTADOS_NOMENCLADOR,
                new ArrayList<Nomenclador>()
        );
    }
}
