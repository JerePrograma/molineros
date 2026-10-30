package ar.com.ospim.compras.requerimientos.action;

import ar.com.ospim.autorizaciones.beans.Nomenclador;
import ar.com.ospim.compras.WebKeysCompras;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompra;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraSector;
import ar.com.ospim.compras.requerimientos.beans.SectorNomencladorCompra;
import ar.com.ospim.compras.requerimientos.helper.NomencladorCompraBusquedaHelper;
import ar.com.ospim.compras.requerimientos.service.BusquedaRequerimientoCompraServiceUtil;

import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.ActionConstants;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.json.JSONObject;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

public class BuscarItemTecnicoComprasAction extends PortletAction {

    private static final Log _log = LogFactoryUtil.getLog(
            BuscarItemTecnicoComprasAction.class);

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

        if (ParamUtil.getBoolean(request, "modo_correccion", false)
                && ParamUtil.getInteger(request, "id_requerimiento_compra", 0) > 0) {
            detalleHelper.validarPermisoCorregirItem(user);
        } else {
            detalleHelper.validarPermisoABM(user);
        }

        if ("CONSULTAR_CONFIGURACION".equals(
                ParamUtil.getString(request, "cmd", ""))) {
            publicarConfiguracion(request, response);
            return new ActionForward(ActionConstants.COMMON_NULL);
        }

        if ("CONSULTAR_DROGA".equals(
                ParamUtil.getString(
                        request,
                        "cmd",
                        ""
                )
        )) {
            String sector =
                    WebKeysCompras.normalizarSectorCompra(
                            resolverSector(request).getDescripcion());

            String droga =
                    nomencladorHelper.obtenerDrogaMedicamento(
                            sector,
                            ParamUtil.getString(
                                    request,
                                    "codigo",
                                    ""
                            )
                    );

            JSONObject resultado = new JSONObject();
            resultado.put("droga", droga);

            response.setContentType("application/json");
            response.getWriter().write(resultado.toString());

            return new ActionForward(
                    ActionConstants.COMMON_NULL
            );
        }

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

    private void publicarConfiguracion(
            RenderRequest request, RenderResponse response) throws Exception {
        JSONObject resultado = new JSONObject();
        JSONArray asociaciones = new JSONArray();
        try {
            RequerimientoCompraSector sector = resolverSector(request, false);
            Integer idTipoPrestacion = resolverTipoPrestacion(request);
            List<SectorNomencladorCompra> configuracion =
                    BusquedaRequerimientoCompraServiceUtil
                            .getConfiguracionNomenclador(
                                    sector.getIdSector(), idTipoPrestacion);
            for (int i = 0; i < configuracion.size(); i++) {
                SectorNomencladorCompra asociacion = configuracion.get(i);
                JSONObject fila = new JSONObject();
                fila.put("idTipoNomenclador", asociacion.getIdTipoNomenclador()
                        != null ? asociacion.getIdTipoNomenclador() : JSONObject.NULL);
                fila.put("marcaReinLiq", asociacion.getMarcaReinLiq()
                        != null ? asociacion.getMarcaReinLiq() : JSONObject.NULL);
                fila.put("codigoNomenclador", asociacion.getCodigoNomenclador()
                        != null ? asociacion.getCodigoNomenclador() : JSONObject.NULL);
                asociaciones.put(fila);
            }
            resultado.put("habilitado", !configuracion.isEmpty());
            if (configuracion.isEmpty()) {
                resultado.put("error", "El contexto no tiene nomencladores configurados.");
            }
        } catch (Exception e) {
            _log.error("No se pudo resolver el contexto de nomencladores de Compras.", e);
            resultado.put("habilitado", false);
            resultado.put("error", "No se pudo resolver la configuración de nomencladores. "
                    + "Revise el sector y el tipo de cotización.");
        }
        resultado.put("asociaciones", asociaciones);
        response.setContentType("application/json");
        response.getWriter().write(resultado.toString());
    }

    private void buscarNomenclador(
            RenderRequest request) throws Exception {
        String codigo = ParamUtil.getString(request, "codigo", "").trim();
        String descripcion = ParamUtil.getString(request, "descripcion", "").trim();
        String droga = ParamUtil.getString(request, "droga", "").trim();
        try {
            RequerimientoCompraSector sector = resolverSector(request);
            String descripcionSector = WebKeysCompras.normalizarSectorCompra(
                    sector.getDescripcion());
            Integer idTipoPrestacion = resolverTipoPrestacion(request);
            boolean farmacia = NomencladorCompraBusquedaHelper.esSectorFarmacia(descripcionSector);
            boolean sinAcentos = ar.com.ospim.compras.requerimientos.helper.ConfiguracionComprasHelper
                    .compararBusquedaSinAcentos(descripcionSector);
            if (!farmacia) {
                droga = "";
            }
            if (codigo.length() == 0 && descripcion.length() == 0
                    && droga.length() == 0) {
                publicarError(request, farmacia
                        ? "Ingrese código, descripción o droga."
                        : "Ingrese código o descripción.");
                return;
            }
            request.setAttribute("COMPRAS_SECTOR_NOMENCLADOR", descripcionSector);
            request.setAttribute("COMPRAS_MARCA_REIN_LIQ", "0");
            request.setAttribute("COMPRAS_ES_PREST_MED",
                    sinAcentos ? "1" : "0");
            request.setAttribute("COMPRAS_CODIGO_NOMENCLADOR", codigo);
            request.setAttribute("COMPRAS_DESCRIPCION_NOMENCLADOR", descripcion);
            // 0 es contexto de búsqueda múltiple; nunca se persiste como tipo real.
            request.setAttribute("COMPRAS_ID_TIPO_NOMENCLADOR", "0");
            List<Nomenclador> resultados = nomencladorHelper.buscar(
                    sector.getIdSector(), descripcionSector, idTipoPrestacion,
                    codigo, descripcion, droga);
            if (farmacia) {
                request.setAttribute("COMPRAS_DROGAS_NOMENCLADOR",
                        BusquedaRequerimientoCompraServiceUtil
                                .obtenerDrogasNomencladorFarmacia(resultados));
            }
            request.setAttribute("COMPRAS_RESULTADOS_NOMENCLADOR", resultados);
        } catch (Exception e) {
            _log.error("No se pudo buscar el nomenclador configurado de Compras.", e);
            publicarError(request,
                    "El contexto no tiene nomencladores configurados o no pudo "
                            + "resolverse su configuración. Revise el sector y el tipo de cotización.");
        }
    }

    private Integer resolverTipoPrestacion(
            RenderRequest request) throws Exception {
        String valor = ParamUtil.getString(request, "id_tipo_prestacion", "").trim();
        if (valor.length() == 0 || "0".equals(valor)) {
            return null;
        }
        if (!valor.matches("^[0-9]+$")) {
            throw new Exception("El tipo de cotización informado no es válido.");
        }
        try {
            int id = Integer.parseInt(valor);
            if (id <= 0) {
                throw new Exception("El tipo de cotización informado no es válido.");
            }
            return Integer.valueOf(id);
        } catch (NumberFormatException e) {
            throw new Exception("El tipo de cotización informado no es válido.");
        }
    }

    private RequerimientoCompraSector resolverSector(
            RenderRequest request) throws Exception {
        return resolverSector(request, true);
    }

    private RequerimientoCompraSector resolverSector(
            RenderRequest request, boolean requiereEdicion) throws Exception {

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

            boolean puedeBuscar = requerimiento.puedeEditarEstructura();
            if (ParamUtil.getBoolean(request, "modo_correccion", false)) {
                puedeBuscar = requerimiento.isActivo()
                        && WebKeysCompras.puedeCorregirItem(requerimiento.getEstado());
            }
            if (requiereEdicion && !puedeBuscar) {
                throw new Exception(
                        "El estado actual del requerimiento no permite "
                                + "buscar prestaciones para su modificación."
                );
            }

            RequerimientoCompraSector sectorReal =
                    BusquedaRequerimientoCompraServiceUtil.getSector(
                            requerimiento.getIdSector());
            if (sectorReal == null) {
                throw new Exception("El sector del requerimiento no está disponible.");
            }
            return sectorReal;
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
