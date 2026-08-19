package ar.com.ospim.compras.requerimientos.action;

import ar.com.ospim.compras.WebKeysCompras;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraDetalle;
import ar.com.ospim.compras.requerimientos.helper.EditarRequerimientoCompraHelper;
import ar.com.ospim.util.PermissionUtil;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;

/**
 * Adaptador HTTP de los detalles del requerimiento.
 *
 * Esta clase interpreta parametros, conserva compatibilidad con los nombres
 * legacy y coordina el retorno del Action. Las reglas funcionales del detalle
 * pertenecen exclusivamente a EditarRequerimientoCompraHelper.
 */
public class RequerimientoCompraDetalleHelper {

    private static final Log _log =
            LogFactoryUtil.getLog(
                    RequerimientoCompraDetalleHelper.class
            );

    private static final boolean EXIGIR_DETALLES_EN_SAVE_ALL = true;

    private static final String STRUTS_ACTION_EDITAR_REQUERIMIENTO =
            "/compras/editar_requerimiento";

    private final EditarRequerimientoCompraHelper requerimientoHelper =
            new EditarRequerimientoCompraHelper();

    /**
     * Alias legacy conservado para callers que referencien el tipo anidado.
     * La implementacion canonica vive en action.ValidacionCompraException.
     */
    @Deprecated
    public static class ValidacionCompraException
            extends ar.com.ospim.compras.requerimientos.action.ValidacionCompraException {

        public ValidacionCompraException(
                String campo,
                String mensaje) {

            super(campo, mensaje);
        }

        public String getCampo() {
            return super.getCampo();
        }
    }

    public int guardarDetallesDesdeRequest(
            ActionRequest request,
            int idRequerimientoCompra,
            String usuario) throws Exception {

        if (idRequerimientoCompra <= 0) {
            errorCampo(
                    "id_requerimiento_compra",
                    "Debe guardar primero la cabecera del requerimiento."
            );
        }

        String deletedIds =
                getParametroTrim(
                        request,
                        "detalle_deleted_ids"
                );

        Set<Integer> borrados =
                new HashSet<Integer>();

        if (!WebKeysCompras.isEmpty(deletedIds)) {
            String[] ids = deletedIds.split(",");

            for (int i = 0; i < ids.length; i++) {
                String rawId =
                        ids[i] != null
                                ? ids[i].trim()
                                : "";

                if (WebKeysCompras.isEmpty(rawId)) {
                    continue;
                }

                if (!rawId.matches("^[0-9]+$")) {
                    errorCampo(
                            "detalle_deleted_ids",
                            "Detalle a borrar: ID invalido recibido: '"
                                    + rawId
                                    + "'."
                    );
                }

                int idDetalleBorrado;

                try {
                    idDetalleBorrado = Integer.parseInt(rawId);
                } catch (NumberFormatException e) {
                    errorCampo(
                            "detalle_deleted_ids",
                            "Detalle a borrar: ID fuera del rango permitido."
                    );
                    return 0;
                }

                if (idDetalleBorrado <= 0
                        || borrados.contains(
                        Integer.valueOf(idDetalleBorrado)
                )) {

                    continue;
                }

                requerimientoHelper.borrarDetalle(
                        idRequerimientoCompra,
                        idDetalleBorrado,
                        usuario
                );

                borrados.add(
                        Integer.valueOf(idDetalleBorrado)
                );
            }
        }

        int count =
                parseEnteroConDefault(
                        request,
                        "detalle_count",
                        "Cantidad de detalles",
                        0
                );

        if (EXIGIR_DETALLES_EN_SAVE_ALL
                && count <= 0) {

            errorCampo(
                    "detalle_count",
                    "Detalles: no llego ningun detalle al Action. "
                            + "detalle_count vino vacio o en cero."
            );
        }

        int guardados = 0;
        int omitidos = 0;

        for (int i = 0; i < count; i++) {
            String prefix =
                    "detalle_" + i + "_";

            String contexto =
                    "Detalle #" + (i + 1);

            if (filaDetalleTecnicaVacia(
                    request,
                    prefix
            )) {
                omitidos++;
                continue;
            }

            int idDetalle =
                    parseEnteroConDefault(
                            request,
                            prefix + "id",
                            contexto + " - ID",
                            0
                    );

            if (idDetalle > 0
                    && borrados.contains(
                    Integer.valueOf(idDetalle)
            )) {
                omitidos++;
                continue;
            }

            RequerimientoCompraDetalle detalle =
                    getDetalleFromRequest(
                            request,
                            prefix,
                            contexto,
                            idRequerimientoCompra
                    );

            detalle.setId(
                    idDetalle > 0
                            ? Integer.valueOf(idDetalle)
                            : null
            );

            int idDetalleGuardado =
                    requerimientoHelper.guardarDetalle(
                            detalle,
                            usuario
                    );

            if (idDetalleGuardado <= 0) {
                throw new Exception(
                        contexto
                                + ": no se obtuvo un identificador valido."
                );
            }

            guardados++;
        }

        if (EXIGIR_DETALLES_EN_SAVE_ALL
                && guardados == 0
                && borrados.size() == 0) {

            errorCampo(
                    "detalles",
                    "Detalles: el Action recibio detalle_count="
                            + count
                            + ", pero no proceso ningun detalle. "
                            + "Filas omitidas="
                            + omitidos
                            + "."
            );
        }

        if (_log.isDebugEnabled()) {
            _log.debug(
                    "Guardado conjunto de detalles finalizado. "
                            + "idRequerimiento="
                            + idRequerimientoCompra
                            + ", recibidos="
                            + count
                            + ", guardados="
                            + guardados
                            + ", borrados="
                            + borrados.size()
                            + ", omitidos="
                            + omitidos
            );
        }

        return guardados;
    }

    public void guardarDetalleDesdeRequest(
            ActionRequest request,
            ActionResponse response,
            String usuario) throws Exception {

        RequerimientoCompraDetalle detalle =
                getDetalleFromRequest(
                        request
                );

        int idDetalleGuardado =
                requerimientoHelper.guardarDetalle(
                        detalle,
                        usuario
                );

        if (idDetalleGuardado <= 0) {
            throw new Exception(
                    "No se obtuvo un identificador valido del detalle."
            );
        }

        setIdRequerimientoEnRequest(
                request,
                response,
                detalle.getIdRequerimientoCompra()
        );
    }

    public void borrarDetalleDesdeRequest(
            ActionRequest request,
            ActionResponse response,
            String usuario) throws Exception {

        int idDetalle =
                getIdDetalleFromRequest(
                        request
                );

        if (idDetalle <= 0) {
            errorCampo(
                    "id_detalle",
                    "Debe informar el detalle a borrar."
            );
        }

        int idRequerimientoCompra =
                parseEnteroConDefault(
                        request,
                        "id_requerimiento_compra",
                        "ID del requerimiento",
                        0
                );

        if (idRequerimientoCompra <= 0) {
            errorCampo(
                    "id_requerimiento_compra",
                    "Debe informar el requerimiento de compra."
            );
        }

        requerimientoHelper.borrarDetalle(
                idRequerimientoCompra,
                idDetalle,
                usuario
        );

        setIdRequerimientoEnRequest(
                request,
                response,
                idRequerimientoCompra
        );
    }

    public RequerimientoCompraDetalle getDetalleFromRequest(
            ActionRequest request) throws Exception {

        return getDetalleFromRequest(
                request,
                "",
                "Detalle",
                parseEnteroConDefault(
                        request,
                        "id_requerimiento_compra",
                        "ID del requerimiento",
                        0
                )
        );
    }

    private RequerimientoCompraDetalle getDetalleFromRequest(
            ActionRequest request,
            String prefix,
            String contexto,
            int idRequerimientoCompra) throws Exception {

        RequerimientoCompraDetalle detalle =
                new RequerimientoCompraDetalle();

        int idDetalle;

        if (WebKeysCompras.isEmpty(prefix)) {
            idDetalle = getIdDetalleFromRequest(request);
        } else {
            idDetalle =
                    parseEnteroConDefault(
                            request,
                            prefix + "id",
                            contexto + " - ID",
                            0
                    );
        }

        detalle.setId(
                idDetalle > 0
                        ? Integer.valueOf(idDetalle)
                        : null
        );

        detalle.setIdRequerimientoCompra(
                idRequerimientoCompra
        );

        cargarDetalleTecnicoDesdeRequest(
                request,
                prefix,
                contexto,
                detalle
        );

        detalle.setCantidad(
                parseCantidadDesdeRequest(
                        request,
                        prefix + "cantidad",
                        contexto + " - Cantidad"
                )
        );

        /*
         * Precio y adjudicacion pertenecen al flujo de cotizacion, no al
         * guardado estructural del detalle.
         */
        detalle.setPrecioUnitarioEstimado(null);
        detalle.setPrecioTotalEstimado(null);
        detalle.setIdPrestador(null);

        String observaciones =
                getParametroRaw(
                        request,
                        prefix + "observaciones",
                        null
                );

        if (WebKeysCompras.isEmpty(observaciones)
                && WebKeysCompras.isEmpty(prefix)) {

            observaciones =
                    ParamUtil.getString(
                            request,
                            "observaciones_detalle",
                            null
                    );
        }

        detalle.setObservaciones(
                observaciones
        );

        return detalle;
    }

    private void cargarDetalleTecnicoDesdeRequest(
            ActionRequest request,
            String prefix,
            String contexto,
            RequerimientoCompraDetalle detalle) throws Exception {

        detalle.setTipoItem(
                getParametroTrim(
                        request,
                        prefix + "tipo_item"
                )
        );

        int idPrestacion =
                parseEnteroConDefault(
                        request,
                        prefix + "id_prestacion",
                        contexto + " - Prestacion",
                        0
                );

        int idTipoNomenclador =
                parseEnteroConDefault(
                        request,
                        prefix + "id_tipo_nomenclador",
                        contexto + " - Tipo de nomenclador",
                        0
                );

        int idMedicamento =
                parseEnteroConDefault(
                        request,
                        prefix + "id_medicamento",
                        contexto + " - Medicamento",
                        0
                );

        int troquel =
                parseEnteroConDefault(
                        request,
                        prefix + "troquel",
                        contexto + " - Troquel",
                        0
                );

        detalle.setIdPrestacion(
                idPrestacion > 0
                        ? Integer.valueOf(idPrestacion)
                        : null
        );

        detalle.setIdTipoNomenclador(
                idTipoNomenclador > 0
                        ? Integer.valueOf(idTipoNomenclador)
                        : null
        );

        detalle.setCodigoNomenclador(
                getParametroTrim(
                        request,
                        prefix + "codigo_nomenclador"
                )
        );

        detalle.setDescripcionNomenclador(
                getParametroTrim(
                        request,
                        prefix + "descripcion_nomenclador"
                )
        );

        detalle.setIdMedicamento(
                idMedicamento > 0
                        ? Integer.valueOf(idMedicamento)
                        : null
        );

        detalle.setTroquel(
                troquel > 0
                        ? Integer.valueOf(troquel)
                        : null
        );

        detalle.setNombreMedicamento(
                getParametroTrim(
                        request,
                        prefix + "nombre_medicamento"
                )
        );

        detalle.setCodigoItem(
                getParametroTrim(
                        request,
                        prefix + "codigo_item"
                )
        );

        detalle.setDescripcionItem(
                getParametroTrim(
                        request,
                        prefix + "descripcion_item"
                )
        );
    }

    private boolean filaDetalleTecnicaVacia(
            ActionRequest request,
            String prefix) {

        return WebKeysCompras.isEmpty(
                getParametroTrim(request, prefix + "id")
        )
                && WebKeysCompras.isEmpty(
                getParametroTrim(request, prefix + "tipo_item")
        )
                && WebKeysCompras.isEmpty(
                getParametroTrim(request, prefix + "id_prestacion")
        )
                && WebKeysCompras.isEmpty(
                getParametroTrim(request, prefix + "id_tipo_nomenclador")
        )
                && WebKeysCompras.isEmpty(
                getParametroTrim(request, prefix + "codigo_nomenclador")
        )
                && WebKeysCompras.isEmpty(
                getParametroTrim(request, prefix + "descripcion_nomenclador")
        )
                && WebKeysCompras.isEmpty(
                getParametroTrim(request, prefix + "id_medicamento")
        )
                && WebKeysCompras.isEmpty(
                getParametroTrim(request, prefix + "troquel")
        )
                && WebKeysCompras.isEmpty(
                getParametroTrim(request, prefix + "nombre_medicamento")
        );
    }

    public void validarPermisoABM(
            User user) throws Exception {

        if (user == null) {
            errorCampo(
                    "usuario",
                    "No se pudo determinar el usuario actual."
            );
        }

        if (!PermissionUtil.userContainsRole(
                user,
                WebKeysCompras.ROL_ABM_COMPRAS
        )) {
            errorCampo(
                    "permisos",
                    "No posee permisos para administrar requerimientos de compras."
            );
        }
    }

    public String getUsuario(User user) {
        return user != null
                ? user.getScreenName()
                : "sistema";
    }

    public int getIdRequerimientoCompraFromRequest(
            ActionRequest request) throws Exception {

        return parseEnteroConDefault(
                request,
                "id_requerimiento_compra",
                "ID del requerimiento",
                0
        );
    }

    public void setRenderEdicion(
            ActionResponse response,
            int idRequerimientoCompra) {

        if (response == null) {
            return;
        }

        if (idRequerimientoCompra > 0) {
            response.setRenderParameter(
                    "id_requerimiento_compra",
                    String.valueOf(idRequerimientoCompra)
            );
        }

        response.setRenderParameter(
                "struts_action",
                STRUTS_ACTION_EDITAR_REQUERIMIENTO
        );
    }

    /**
     * Contrato conservado para callers existentes. La normalizacion real se
     * encuentra en el Helper de negocio.
     */
    public String normalizarTextoCarga(String value) {
        return requerimientoHelper.normalizarTextoCarga(value);
    }

    private void setIdRequerimientoEnRequest(
            ActionRequest request,
            ActionResponse response,
            int idRequerimientoCompra) {

        if (request != null) {
            request.setAttribute(
                    WebKeysCompras.ID_REQUERIMIENTO_COMPRA_EN_EDICION,
                    Integer.valueOf(idRequerimientoCompra)
            );
        }

        setRenderEdicion(
                response,
                idRequerimientoCompra
        );
    }

    private void errorCampo(
            String campo,
            String mensaje) throws ValidacionCompraException {

        throw new ValidacionCompraException(
                campo,
                mensaje
        );
    }

    public String getParametroTrim(
            ActionRequest request,
            String nombre) {

        String value =
                getParametroRaw(
                        request,
                        nombre,
                        null
                );

        return value != null
                ? value.trim()
                : "";
    }

    public String getParametroRaw(
            ActionRequest request,
            String nombre,
            String defaultValue) {

        if (request == null || nombre == null) {
            return defaultValue;
        }

        String value = request.getParameter(nombre);

        if (value != null) {
            return value;
        }

        try {
            value = ParamUtil.getString(request, nombre, null);

            if (value != null) {
                return value;
            }
        } catch (Exception ignored) {
            /* Sigue fallback manual para portlets legacy. */
        }

        Map parameterMap = request.getParameterMap();

        if (parameterMap == null || parameterMap.isEmpty()) {
            return defaultValue;
        }

        String bestKey = null;
        Iterator it = parameterMap.keySet().iterator();

        while (it.hasNext()) {
            Object keyObj = it.next();

            if (keyObj == null) {
                continue;
            }

            String key = String.valueOf(keyObj);

            if (key.equals(nombre)
                    || key.endsWith("_" + nombre)
                    || key.endsWith(nombre)) {

                if (bestKey == null || key.length() < bestKey.length()) {
                    bestKey = key;
                }
            }
        }

        if (bestKey == null) {
            return defaultValue;
        }

        Object raw = parameterMap.get(bestKey);

        if (raw instanceof String[]) {
            String[] values = (String[]) raw;
            return values.length > 0
                    ? values[0]
                    : defaultValue;
        }

        return raw != null
                ? String.valueOf(raw)
                : defaultValue;
    }

    private Integer parseEnteroOpcional(
            ActionRequest request,
            String nombre,
            String label) throws ValidacionCompraException {

        String value = getParametroTrim(request, nombre);

        if (WebKeysCompras.isEmpty(value)) {
            return null;
        }

        if (!value.matches("^[0-9]+$")) {
            errorCampo(
                    nombre,
                    label + ": el valor ingresado '"
                            + value
                            + "' no es un numero entero valido."
            );
        }

        try {
            return Integer.valueOf(
                    Integer.parseInt(value)
            );
        } catch (Exception e) {
            errorCampo(
                    nombre,
                    label + ": el valor ingresado '"
                            + value
                            + "' esta fuera del rango permitido."
            );
        }

        return null;
    }

    public int parseEnteroConDefault(
            ActionRequest request,
            String nombre,
            String label,
            int defaultValue) throws ValidacionCompraException {

        Integer parsed =
                parseEnteroOpcional(
                        request,
                        nombre,
                        label
                );

        return parsed != null
                ? parsed.intValue()
                : defaultValue;
    }

    private Integer parseCantidadDesdeRequest(
            ActionRequest request,
            String nombre,
            String label) throws ValidacionCompraException {

        String value = getParametroTrim(request, nombre);

        if (WebKeysCompras.isEmpty(value)) {
            errorCampo(
                    nombre,
                    label + ": debe informar una cantidad."
            );
        }

        if (!value.matches("^[0-9]+$")) {
            errorCampo(
                    nombre,
                    label + ": debe ser un numero entero mayor a cero."
            );
        }

        int parsed;

        try {
            parsed = Integer.parseInt(value);
        } catch (Exception e) {
            errorCampo(
                    nombre,
                    label + ": el valor esta fuera del rango permitido."
            );
            return null;
        }

        if (parsed <= 0) {
            errorCampo(
                    nombre,
                    label + ": debe ser mayor a cero."
            );
        }

        return Integer.valueOf(parsed);
    }

    public String sanitizarCallback(String callback) {
        if (callback == null) {
            return "";
        }

        callback = callback.trim();

        if (!callback.matches("[A-Za-z0-9_]+")) {
            return "";
        }

        return callback;
    }
}
