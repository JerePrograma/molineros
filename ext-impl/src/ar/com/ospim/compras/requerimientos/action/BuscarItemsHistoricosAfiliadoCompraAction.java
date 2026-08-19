package ar.com.ospim.compras.requerimientos.action;

import ar.com.ospim.compras.WebKeysCompras;
import ar.com.ospim.compras.requerimientos.beans
        .RequerimientoCompraDetalle;
import ar.com.ospim.compras.requerimientos.helper
        .BusquedaRequerimientoCompraHelper;
import ar.com.ospim.util.PermissionUtil;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.ActionConstants;
import com.liferay.portal.struts.JSONAction;
import com.liferay.portal.util.PortalUtil;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class BuscarItemsHistoricosAfiliadoCompraAction
        extends JSONAction {

    private static final Log _log =
            LogFactoryUtil.getLog(
                    BuscarItemsHistoricosAfiliadoCompraAction.class
            );

    private final BusquedaRequerimientoCompraHelper busquedaHelper =
            new BusquedaRequerimientoCompraHelper();

@Override
    public ActionForward execute(
            ActionMapping mapping,
            ActionForm form,
            HttpServletRequest request,
            HttpServletResponse response)
            throws Exception {

        super.execute(
                mapping,
                form,
                request,
                response
        );

        /*
         * JSONAction escribe directamente la respuesta.
         * El forward COMMON_NULL evita que Liferay intente
         * resolver un forward inexistente.
         */
        return new ActionForward(
                ActionConstants.COMMON_NULL
        );
    }

    @Override
    public String getJSON(
            ActionMapping mapping,
            ActionForm form,
            HttpServletRequest request,
            HttpServletResponse response)
            throws Exception {

        try {
            User user =
                    PortalUtil.getUser(
                            request
                    );

            validarPermisoConsulta(
                    user
            );

            String cuilTitular =
                    normalizar(
                            request.getParameter(
                                    "cuil_titular"
                            )
                    );

            String inteRaw =
                    normalizar(
                            request.getParameter(
                                    "inte"
                            )
                    );

            String idSectorRaw =
                    normalizar(
                            request.getParameter(
                                    "id_sector"
                            )
                    );

            String idRequerimientoExcluirRaw =
                    normalizar(
                            request.getParameter(
                                    "id_requerimiento_excluir"
                            )
                    );

            if (cuilTitular == null) {
                return construirRespuesta(null);
            }

            Integer inte =
                    parseEntero(
                            inteRaw
                    );

            Integer idSector =
                    parseEntero(
                            idSectorRaw
                    );

            if (inte == null
                    || inte.intValue() < 0
                    || idSector == null
                    || idSector.intValue() <= 0) {

                return construirRespuesta(
                        null
                );
            }

            int idRequerimientoExcluir =
                    0;

            if (idRequerimientoExcluirRaw != null) {
                Integer idRequerimientoParseado =
                        parseEntero(
                                idRequerimientoExcluirRaw
                        );

                if (idRequerimientoParseado == null
                        || idRequerimientoParseado.intValue() < 0) {

                    return construirRespuesta(
                            null
                    );
                }

                idRequerimientoExcluir =
                        idRequerimientoParseado.intValue();
            }

            List<RequerimientoCompraDetalle> items =
                    busquedaHelper
                            .buscarItemsHistoricosAfiliado(
                                    cuilTitular,
                                    inte.intValue(),
                                    idSector.intValue(),
                                    idRequerimientoExcluir
                            );

            return construirRespuesta(
                    items
            );

        } catch (Exception e) {
            /*
             * Fallar cerrado.
             *
             * No exponer errores técnicos ni confirmar
             * la existencia de información histórica.
             */
            _log.warn(
                    "No se pudieron consultar los ítems históricos "
                            + "del afiliado para Compras.",
                    e
            );

            return construirRespuesta(
                    null
            );
        }
    }

    private void validarPermisoConsulta(
            User user) throws Exception {

        if (user == null) {
            throw new Exception(
                    "No se pudo determinar el usuario."
            );
        }

        boolean puedeAdministrar =
                PermissionUtil.userContainsRole(
                        user,
                        WebKeysCompras.ROL_ABM_COMPRAS
                );

        if (!puedeAdministrar) {
            throw new Exception(
                    "El usuario no posee permisos de ABM Compras."
            );
        }
    }

    private String construirRespuesta(
            List<RequerimientoCompraDetalle> items) {

        StringBuilder json =
                new StringBuilder();

        json.append(
                "{\"items\":["
        );

        boolean primero =
                true;

        if (items != null) {
            for (int i = 0; i < items.size(); i++) {
                RequerimientoCompraDetalle detalle =
                        items.get(
                                i
                        );

                if (detalle == null) {
                    continue;
                }

                int idPrestacion =
                        detalle.getIdPrestacionInt();

                int idTipoNomenclador =
                        detalle.getIdTipoNomencladorInt();

                String codigo =
                        normalizar(
                                detalle
                                        .getCodigoNomencladorVisible()
                        );

                String descripcion =
                        normalizar(
                                detalle
                                        .getDescripcionNomencladorVisible()
                        );

                if (idPrestacion <= 0
                        || idTipoNomenclador <= 0
                        || codigo == null
                        || descripcion == null) {

                    continue;
                }

                if (!primero) {
                    json.append(
                            ','
                    );
                }

                json.append(
                        "{\"idPrestacion\":"
                );

                json.append(
                        idPrestacion
                );

                json.append(
                        ",\"idTipoNomenclador\":"
                );

                json.append(
                        idTipoNomenclador
                );

                json.append(
                        ",\"codigo\":"
                );

                json.append(
                        jsonString(
                                codigo
                        )
                );

                json.append(
                        ",\"descripcion\":"
                );

                json.append(
                        jsonString(
                                descripcion
                        )
                );

                json.append(
                        '}'
                );

                primero =
                        false;
            }
        }

        json.append(
                "]}"
        );

        return json.toString();
    }

    private String jsonString(
            String value) {

        if (value == null) {
            value =
                    "";
        }

        StringBuilder resultado =
                new StringBuilder(
                        value.length()
                                + 16
                );

        resultado.append(
                '"'
        );

        for (int i = 0; i < value.length(); i++) {
            char caracter =
                    value.charAt(
                            i
                    );

            switch (caracter) {
                case '"':
                    resultado.append("\\\"");
                    break;

                case '\\':
                    resultado.append("\\\\");
                    break;

                case '\b':
                    resultado.append("\\b");
                    break;

                case '\f':
                    resultado.append("\\f");
                    break;

                case '\n':
                    resultado.append("\\n");
                    break;

                case '\r':
                    resultado.append("\\r");
                    break;

                case '\t':
                    resultado.append("\\t");
                    break;

                default:
                    if (caracter < 32) {
                        String hexadecimal =
                                Integer.toHexString(
                                        caracter
                                );

                        resultado.append(
                                "\\u"
                        );

                        for (int j = hexadecimal.length();
                             j < 4;
                             j++) {

                            resultado.append(
                                    '0'
                            );
                        }

                        resultado.append(
                                hexadecimal
                        );

                    } else {
                        resultado.append(
                                caracter
                        );
                    }

                    break;
            }
        }

        resultado.append(
                '"'
        );

        return resultado.toString();
    }

    private Integer parseEntero(
            String value) {

        if (value == null
                || !value.matches(
                "^[0-9]+$"
        )) {

            return null;
        }

        try {
            return Integer.valueOf(
                    value
            );

        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String normalizar(
            String value) {

        if (value == null) {
            return null;
        }

        String normalizado =
                value.trim();

        return normalizado.length() > 0
                ? normalizado
                : null;
    }
}