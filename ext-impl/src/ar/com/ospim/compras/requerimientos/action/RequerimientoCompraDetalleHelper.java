package ar.com.ospim.compras.requerimientos.action;

import ar.com.ospim.compras.WebKeysCompras;
import ar.com.ospim.compras.beans.CompraArticulo;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompra;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraDetalle;
import ar.com.ospim.compras.requerimientos.service.BusquedaRequerimientoCompraServiceUtil;
import ar.com.ospim.compras.requerimientos.service.EditarRequerimientoCompraServiceUtil;
import ar.com.ospim.util.PermissionUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import java.math.BigDecimal;
import java.text.Normalizer;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public class RequerimientoCompraDetalleHelper {

    private static final boolean EXIGIR_DETALLES_EN_SAVE_ALL = true;

    private static final Pattern DIACRITICOS =
            Pattern.compile("\\p{InCombiningDiacriticalMarks}+");

    private static final String STRUTS_ACTION_EDITAR_REQUERIMIENTO =
            "/compras/editar_requerimiento";

    public static class ValidacionCompraException extends Exception {

        private final String campo;

        public ValidacionCompraException(String campo, String message) {
            super(message);
            this.campo = campo;
        }

        public String getCampo() {
            return campo;
        }
    }

    public int guardarDetallesDesdeRequest(ActionRequest request,
                                           int idRequerimientoCompra,
                                           String usuario) throws Exception {

        if (idRequerimientoCompra <= 0) {
            errorCampo(
                    "id_requerimiento_compra",
                    "Debe guardar primero la cabecera del requerimiento."
            );
        }

        RequerimientoCompra requerimiento =
                validarRequerimientoEditable(idRequerimientoCompra);

        String deletedIds = getParametroTrim(request, "detalle_deleted_ids");

        Set<Integer> borrados = new HashSet<Integer>();

        if (!WebKeysCompras.isEmpty(deletedIds)) {
            String[] ids = deletedIds.split(",");

            for (int i = 0; i < ids.length; i++) {
                String rawId = ids[i] != null ? ids[i].trim() : "";

                if (WebKeysCompras.isEmpty(rawId)) {
                    continue;
                }

                if (!rawId.matches("^[0-9]+$")) {
                    errorCampo(
                            "detalle_deleted_ids",
                            "Detalle a borrar: ID invalido recibido: '" + rawId + "'."
                    );
                }

                int idDetalleBorrado = Integer.parseInt(rawId);

                if (idDetalleBorrado > 0
                        && !borrados.contains(Integer.valueOf(idDetalleBorrado))) {

                    validarDetallePerteneceARequerimiento(
                            requerimiento,
                            idDetalleBorrado,
                            "detalle_deleted_ids"
                    );

                    EditarRequerimientoCompraServiceUtil.borrarDetalle(
                            idDetalleBorrado,
                            usuario
                    );

                    borrados.add(Integer.valueOf(idDetalleBorrado));
                }
            }
        }

        int count = parseEnteroConDefault(
                request,
                "detalle_count",
                "Cantidad de detalles",
                0
        );

        if (EXIGIR_DETALLES_EN_SAVE_ALL && count <= 0) {
            errorCampo(
                    "detalle_count",
                    "Detalles: no llego ningun detalle al Action. "
                            + "detalle_count vino vacio o en cero. "
                            + "Esto normalmente indica que serializarDetallesCompras() no genero los hidden inputs, "
                            + "que los inputs quedaron fuera del form principal, "
                            + "o que el formulario se rompio por incluir busqueda_afiliado.jsp dentro del form."
            );
        }

        int guardados = 0;
        int omitidos = 0;

        for (int i = 0; i < count; i++) {
            String prefix = "detalle_" + i + "_";
            String contexto = "Detalle #" + (i + 1);

            if (filaDetalleVacia(request, prefix)) {
                omitidos++;
                continue;
            }

            int idDetalle = parseEnteroConDefault(
                    request,
                    prefix + "id",
                    contexto + " - ID",
                    0
            );

            if (idDetalle > 0 && borrados.contains(Integer.valueOf(idDetalle))) {
                omitidos++;
                continue;
            }

            if (idDetalle > 0) {
                validarDetallePerteneceARequerimiento(
                        requerimiento,
                        idDetalle,
                        prefix + "id"
                );
            }

            int idArticulo = parseEnteroConDefault(
                    request,
                    prefix + "id_articulo",
                    contexto + " - Articulo",
                    0
            );

            RequerimientoCompraDetalle detalle = new RequerimientoCompraDetalle();

            detalle.setId(idDetalle > 0 ? Integer.valueOf(idDetalle) : null);
            detalle.setIdRequerimientoCompra(idRequerimientoCompra);
            detalle.setIdArticulo(idArticulo > 0 ? Integer.valueOf(idArticulo) : null);

            detalle.setCantidad(
                    parseCantidadDesdeRequest(
                            request,
                            prefix + "cantidad",
                            contexto + " - Cantidad"
                    )
            );

            detalle.setPrecioUnitarioEstimado(
                    parseBigDecimalNullable(
                            getParametroTrim(
                                    request,
                                    prefix + "precio_unitario_estimado"
                            ),
                            contexto + " - Precio unitario estimado"
                    )
            );

            detalle.setPrecioTotalEstimado(
                    parseBigDecimalNullable(
                            getParametroTrim(
                                    request,
                                    prefix + "precio_total_estimado"
                            ),
                            contexto + " - Precio total estimado"
                    )
            );

            detalle.setObservaciones(
                    getParametroRaw(request, prefix + "observaciones", null)
            );

            normalizarDetalleNuevo(detalle);

            validarDetalle(detalle, contexto);

            EditarRequerimientoCompraServiceUtil.guardarDetalle(detalle, usuario);

            guardados++;
        }

        if (EXIGIR_DETALLES_EN_SAVE_ALL && guardados == 0 && borrados.size() == 0) {
            errorCampo(
                    "detalles",
                    "Detalles: el Action recibio detalle_count=" + count
                            + ", pero no guardo ningun detalle. "
                            + "Revisar los nombres de parametros generados por serializarDetallesCompras()."
            );
        }

        return guardados;
    }

    public void guardarDetalleDesdeRequest(ActionRequest request,
                                           ActionResponse response,
                                           String usuario) throws Exception {

        RequerimientoCompraDetalle detalle = getDetalleFromRequest(request);

        normalizarDetalleNuevo(detalle);

        RequerimientoCompra requerimiento =
                validarRequerimientoEditable(detalle.getIdRequerimientoCompra());

        if (detalle.getIdInt() > 0) {
            validarDetallePerteneceARequerimiento(
                    requerimiento,
                    detalle.getIdInt(),
                    "id_detalle"
            );
        }

        validarDetalle(detalle);

        EditarRequerimientoCompraServiceUtil.guardarDetalle(detalle, usuario);

        setIdRequerimientoEnRequest(
                request,
                response,
                detalle.getIdRequerimientoCompra()
        );
    }

    public void borrarDetalleDesdeRequest(ActionRequest request,
                                          ActionResponse response,
                                          String usuario) throws Exception {

        int idDetalle = getIdDetalleFromRequest(request);

        if (idDetalle <= 0) {
            errorCampo("id_detalle", "Debe informar el detalle a borrar.");
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

        RequerimientoCompra requerimiento =
                validarRequerimientoEditable(idRequerimientoCompra);

        validarDetallePerteneceARequerimiento(
                requerimiento,
                idDetalle,
                "id_detalle"
        );

        EditarRequerimientoCompraServiceUtil.borrarDetalle(idDetalle, usuario);

        setIdRequerimientoEnRequest(
                request,
                response,
                idRequerimientoCompra
        );
    }

    public RequerimientoCompraDetalle getDetalleFromRequest(ActionRequest request)
            throws Exception {

        RequerimientoCompraDetalle detalle = new RequerimientoCompraDetalle();

        int idDetalle = getIdDetalleFromRequest(request);

        detalle.setId(idDetalle > 0 ? Integer.valueOf(idDetalle) : null);

        detalle.setIdRequerimientoCompra(
                parseEnteroConDefault(
                        request,
                        "id_requerimiento_compra",
                        "ID del requerimiento",
                        0
                )
        );

        int idArticulo = parseEnteroConDefault(
                request,
                "id_articulo",
                "Articulo",
                0
        );

        detalle.setIdArticulo(idArticulo > 0 ? Integer.valueOf(idArticulo) : null);

        detalle.setCantidad(
                parseCantidadDesdeRequest(request, "cantidad", "Cantidad")
        );

        detalle.setPrecioUnitarioEstimado(
                parseBigDecimalNullable(
                        ParamUtil.getString(request, "precio_unitario_estimado", null),
                        "Precio unitario estimado"
                )
        );

        detalle.setPrecioTotalEstimado(
                parseBigDecimalNullable(
                        ParamUtil.getString(request, "precio_total_estimado", null),
                        "Precio total estimado"
                )
        );

        detalle.setObservaciones(
                ParamUtil.getString(request, "observaciones_detalle", null)
        );

        return detalle;
    }

    public void validarPermisoABM(User user) throws Exception {
        if (user == null) {
            errorCampo("usuario", "No se pudo determinar el usuario actual.");
        }

        if (!PermissionUtil.userContainsRole(user, WebKeysCompras.ROL_ABM_COMPRAS)) {
            errorCampo(
                    "permisos",
                    "No posee permisos para administrar requerimientos de compras."
            );
        }
    }

    public String getUsuario(User user) {
        return user != null ? user.getScreenName() : "sistema";
    }

    public int getIdRequerimientoCompraFromRequest(ActionRequest request)
            throws Exception {

        return parseEnteroConDefault(
                request,
                "id_requerimiento_compra",
                "ID del requerimiento",
                0
        );
    }

    public void setRenderEdicion(ActionResponse response,
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

    private void validarDetalle(RequerimientoCompraDetalle detalle) throws Exception {
        validarDetalle(detalle, "Detalle");
    }

    private void validarDetalle(RequerimientoCompraDetalle detalle, String contexto)
            throws Exception {

        if (detalle == null) {
            errorCampo(contexto, contexto + ": debe informar el detalle del requerimiento.");
        }

        if (detalle.getIdRequerimientoCompra() <= 0) {
            errorCampo(
                    contexto,
                    contexto + ": debe guardar primero la cabecera del requerimiento."
            );
        }

        if (detalle.getIdArticulo() == null
                || detalle.getIdArticulo().intValue() <= 0) {

            errorCampo(
                    contexto + " - id_articulo",
                    contexto + ": debe seleccionar un articulo."
            );
        }

        if (detalle.getCantidad() == null
                || detalle.getCantidad().intValue() <= 0) {

            errorCampo(
                    contexto + " - cantidad",
                    contexto + ": la Cantidad debe ser mayor a cero."
            );
        }

        if (detalle.getPrecioUnitarioEstimado() != null
                && detalle.getPrecioUnitarioEstimado().compareTo(BigDecimal.ZERO) < 0) {

            errorCampo(
                    contexto + " - precio_unitario_estimado",
                    contexto + ": el Precio unitario estimado no puede ser negativo."
            );
        }

        if (detalle.getPrecioTotalEstimado() != null
                && detalle.getPrecioTotalEstimado().compareTo(BigDecimal.ZERO) < 0) {

            errorCampo(
                    contexto + " - precio_total_estimado",
                    contexto + ": el Precio total estimado no puede ser negativo."
            );
        }
    }

    private RequerimientoCompra validarRequerimientoEditable(int idRequerimientoCompra)
            throws Exception {

        if (idRequerimientoCompra <= 0) {
            return null;
        }

        RequerimientoCompra requerimiento =
                BusquedaRequerimientoCompraServiceUtil.getRequerimientoCompra(
                        idRequerimientoCompra
                );

        if (requerimiento == null) {
            errorCampo(
                    "id_requerimiento_compra",
                    "No se encontro el requerimiento de compra informado. ID recibido: "
                            + idRequerimientoCompra + "."
            );
        }

        if (!requerimiento.isEditable()) {
            errorCampo(
                    "estado",
                    "Solo se pueden editar requerimientos en estado Borrador. Estado actual: "
                            + requerimiento.getEstadoDescripcionVisible() + "."
            );
        }

        return requerimiento;
    }

    private void validarDetallePerteneceARequerimiento(
            RequerimientoCompra requerimiento,
            int idDetalle,
            String campo) throws Exception {

        if (requerimiento == null || idDetalle <= 0) {
            errorCampo(
                    campo,
                    "No se pudo validar el detalle informado."
            );
        }

        List<RequerimientoCompraDetalle> detalles =
                requerimiento.getDetalles();

        if (detalles != null) {
            for (int i = 0; i < detalles.size(); i++) {
                RequerimientoCompraDetalle detalle = detalles.get(i);

                if (detalle != null
                        && detalle.getIdInt() == idDetalle
                        && detalle.getIdRequerimientoCompra()
                        == requerimiento.getIdRequerimientoCompra()) {

                    return;
                }
            }
        }

        errorCampo(
                campo,
                "El detalle informado no pertenece al requerimiento "
                        + requerimiento.getIdRequerimientoCompra()
                        + " o ya no existe. ID de detalle recibido: "
                        + idDetalle
                        + "."
        );
    }

    private int getIdDetalleFromRequest(ActionRequest request) throws Exception {
        return parseEnteroConDefault(request, "id_detalle", "ID del detalle", 0);
    }

    private boolean filaDetalleVacia(ActionRequest request, String prefix) {
        return WebKeysCompras.isEmpty(getParametroTrim(request, prefix + "id"))
                && WebKeysCompras.isEmpty(getParametroTrim(request, prefix + "id_articulo"))
                && WebKeysCompras.isEmpty(getParametroTrim(request, prefix + "cantidad"))
                && WebKeysCompras.isEmpty(
                getParametroTrim(request, prefix + "precio_unitario_estimado")
        )
                && WebKeysCompras.isEmpty(
                getParametroTrim(request, prefix + "precio_total_estimado")
        )
                && WebKeysCompras.isEmpty(getParametroTrim(request, prefix + "observaciones"));
    }

    private boolean esDetalleNuevo(RequerimientoCompraDetalle detalle) {
        return detalle == null
                || detalle.getId() == null
                || detalle.getId().intValue() <= 0;
    }

    private void normalizarDetalleNuevo(RequerimientoCompraDetalle detalle) {
        if (!esDetalleNuevo(detalle)) {
            return;
        }

        if (detalle == null) {
            return;
        }

        detalle.setObservaciones(
                normalizarTextoCarga(detalle.getObservaciones())
        );
    }

    public String normalizarTextoCarga(String value) {
        if (value == null) {
            return null;
        }

        value = value.trim();

        if (value.length() == 0) {
            return null;
        }

        String normalizado = Normalizer.normalize(value, Normalizer.Form.NFD);
        normalizado = DIACRITICOS.matcher(normalizado).replaceAll("");

        return normalizado.toUpperCase(Locale.ROOT).trim();
    }

    private void setIdRequerimientoEnRequest(ActionRequest request,
                                             ActionResponse response,
                                             int idRequerimientoCompra) {

        if (request != null) {
            request.setAttribute(
                    WebKeysCompras.ID_REQUERIMIENTO_COMPRA_EN_EDICION,
                    Integer.valueOf(idRequerimientoCompra)
            );
        }

        if (response != null) {
            response.setRenderParameter(
                    "id_requerimiento_compra",
                    String.valueOf(idRequerimientoCompra)
            );

            response.setRenderParameter(
                    "struts_action",
                    STRUTS_ACTION_EDITAR_REQUERIMIENTO
            );
        }
    }

    private void errorCampo(String campo, String mensaje)
            throws ValidacionCompraException {

        throw new ValidacionCompraException(campo, mensaje);
    }

    public String getParametroTrim(ActionRequest request, String nombre) {
        String value = getParametroRaw(request, nombre, null);

        if (value == null) {
            return "";
        }

        return value.trim();
    }

    public String getParametroRaw(ActionRequest request,
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
        } catch (Exception e) {
            // Sigue fallback manual.
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

        if (raw == null) {
            return defaultValue;
        }

        if (raw instanceof String[]) {
            String[] values = (String[]) raw;

            if (values.length == 0) {
                return defaultValue;
            }

            return values[0];
        }

        return String.valueOf(raw);
    }

    private Integer parseEnteroOpcional(ActionRequest request,
                                        String nombre,
                                        String label)
            throws ValidacionCompraException {

        String value = getParametroTrim(request, nombre);

        if (WebKeysCompras.isEmpty(value)) {
            return null;
        }

        if (!value.matches("^[0-9]+$")) {
            errorCampo(
                    nombre,
                    label + ": el valor ingresado '" + value
                            + "' no es un numero entero valido."
            );
        }

        try {
            return Integer.valueOf(Integer.parseInt(value));
        } catch (Exception e) {
            errorCampo(
                    nombre,
                    label + ": el valor ingresado '" + value
                            + "' esta fuera del rango permitido."
            );
        }

        return null;
    }

    public int parseEnteroConDefault(ActionRequest request,
                                     String nombre,
                                     String label,
                                     int defaultValue)
            throws ValidacionCompraException {

        Integer parsed = parseEnteroOpcional(request, nombre, label);

        if (parsed == null) {
            return defaultValue;
        }

        return parsed.intValue();
    }

    private Integer parseCantidadDesdeRequest(ActionRequest request,
                                              String nombre,
                                              String label)
            throws ValidacionCompraException {

        String value = getParametroTrim(request, nombre);

        if (WebKeysCompras.isEmpty(value)) {
            errorCampo(nombre, label + ": debe informar una cantidad.");
        }

        if (!value.matches("^[0-9]+$")) {
            errorCampo(
                    nombre,
                    label + ": debe ser un numero entero mayor a cero. Valor recibido: '"
                            + value + "'."
            );
        }

        int parsed;

        try {
            parsed = Integer.parseInt(value);
        } catch (Exception e) {
            errorCampo(
                    nombre,
                    label + ": el valor ingresado '" + value
                            + "' esta fuera del rango permitido."
            );
            return null;
        }

        if (parsed <= 0) {
            errorCampo(
                    nombre,
                    label + ": debe ser mayor a cero. Valor recibido: " + parsed + "."
            );
        }

        return Integer.valueOf(parsed);
    }

    private BigDecimal parseBigDecimalNullable(String value, String label)
            throws ValidacionCompraException {

        if (WebKeysCompras.isEmpty(value)) {
            return null;
        }

        String original = value.trim();
        String clean = original.replace(" ", "");

        if (clean.indexOf(',') >= 0) {
            clean = clean.replace(".", "").replace(",", ".");
        }

        if (!clean.matches("^-?[0-9]+(\\.[0-9]+)?$")) {
            errorCampo(
                    label,
                    label + ": importe invalido. Valor recibido: '" + original
                            + "'. Use formatos como 1234.56 o 1.234,56."
            );
        }

        try {
            return new BigDecimal(clean);
        } catch (Exception e) {
            errorCampo(label, label + ": no se pudo interpretar el importe '" + original + "'.");
        }

        return null;
    }

    public CompraArticulo guardarArticuloDesdeRequest(ActionRequest request,
                                                      String usuario) throws Exception {

        int idArticulo = parseEnteroConDefault(
                request,
                "id_articulo",
                "Articulo",
                0
        );

        int idSector = parseEnteroConDefault(
                request,
                "id_sector",
                "Sector del articulo",
                0
        );

        if (idSector <= 0) {
            idSector = parseEnteroConDefault(
                    request,
                    "sector_id",
                    "Sector del articulo",
                    0
            );
        }

        String descripcion = getParametroRaw(
                request,
                "articulo_descripcion",
                null
        );

        if (WebKeysCompras.isEmpty(descripcion)) {
            descripcion = getParametroRaw(
                    request,
                    "articulo",
                    null
            );
        }

        descripcion = normalizarTextoCarga(descripcion);

        validarArticuloParaGuardar(
                idSector,
                descripcion
        );

        int idGuardado =
                EditarRequerimientoCompraServiceUtil.guardarArticulo(
                        idArticulo > 0 ? Integer.valueOf(idArticulo) : null,
                        Integer.valueOf(idSector),
                        descripcion
                );

        CompraArticulo articulo =
                EditarRequerimientoCompraServiceUtil.getArticulo(idGuardado);

        if (articulo != null) {
            return articulo;
        }

        articulo = new CompraArticulo();
        articulo.setId(Integer.valueOf(idGuardado));
        articulo.setIdSector(Integer.valueOf(idSector));
        articulo.setDescripcion(descripcion);

        return articulo;
    }

    public void borrarArticuloDesdeRequest(ActionRequest request) throws Exception {
        int idArticulo = parseEnteroConDefault(
                request,
                "id_articulo",
                "Articulo",
                0
        );

        if (idArticulo <= 0) {
            errorCampo(
                    "id_articulo",
                    "Debe informar el articulo a borrar."
            );
        }

        EditarRequerimientoCompraServiceUtil.borrarArticulo(idArticulo);
    }

    private void validarArticuloParaGuardar(int idSector,
                                            String descripcion) throws Exception {

        if (idSector <= 0) {
            errorCampo(
                    "id_sector",
                    "Debe informar el sector del articulo."
            );
        }

        if (WebKeysCompras.isEmpty(descripcion)) {
            errorCampo(
                    "articulo_descripcion",
                    "Debe informar la descripcion del articulo."
            );
        }
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
