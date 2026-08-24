package ar.com.ospim.compras.requerimientos.action;

import ar.com.ospim.compras.WebKeysCompras;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraPresupuesto;
import ar.com.ospim.compras.requerimientos.documentos.DocumentoLibraryComprasHelper;
import ar.com.ospim.compras.requerimientos.service.BusquedaRequerimientoCompraServiceUtil;
import ar.com.ospim.util.PermissionUtil;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.security.permission.ActionKeys;
import com.liferay.portal.struts.ActionConstants;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portal.util.WebKeys;
import com.liferay.portlet.documentlibrary.model.DLFileEntry;
import com.liferay.portlet.documentlibrary.service.permission.DLFileEntryPermission;
import com.liferay.util.servlet.ServletResponseUtil;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.servlet.http.HttpServletResponse;

public class DescargarOrdenMedicaCompraAction
        extends PortletAction {

    private static final Log _log =
            LogFactoryUtil.getLog(
                    DescargarOrdenMedicaCompraAction.class
            );

    public void processAction(
            ActionMapping mapping,
            ActionForm form,
            PortletConfig portletConfig,
            ActionRequest actionRequest,
            ActionResponse actionResponse)
            throws Exception {

        InputStream input =
                null;

        try {
            User user =
                    PortalUtil.getUser(
                            actionRequest
                    );

            validarPermisoConsulta(
                    user
            );

            int idRequerimientoCompra =
                    ParamUtil.getInteger(
                            actionRequest,
                            "id_requerimiento_compra",
                            0
                    );

            if (idRequerimientoCompra <= 0) {
                throw new Exception(
                        "Debe informar el requerimiento de compra."
                );
            }

            long dlFileEntryIdSolicitado =
                    ParamUtil.getLong(
                            actionRequest,
                            "dl_file_entry_id",
                            0L
                    );

            boolean visualizar =
                    ParamUtil.getBoolean(
                            actionRequest,
                            "visualizar",
                            false
                    );

            RequerimientoCompraPresupuesto ordenMedica =
                    resolverOrdenMedica(
                            idRequerimientoCompra,
                            dlFileEntryIdSolicitado
                    );

            ThemeDisplay themeDisplay =
                    (ThemeDisplay)
                            actionRequest.getAttribute(
                                    WebKeys.THEME_DISPLAY
                            );

            if (themeDisplay == null) {
                throw new Exception(
                        "No se pudo determinar el contexto "
                                + "del portal para descargar "
                                + "la Orden médica."
                );
            }

            /*
             * Primero se valida completamente la relación persistida
             * SQL -> Document Library.
             *
             * Esta operación obtiene solamente los metadatos de la entrada.
             * Todavia no se lee el contenido binario.
             */
            DLFileEntry entry =
                    DocumentoLibraryComprasHelper
                            .obtenerEntradaOrdenMedicaValidada(
                                    ordenMedica,
                                    idRequerimientoCompra,
                                    themeDisplay.getCompanyId()
                            );

            /*
             * El permiso sobre Document Library se comprueba antes de
             * acceder al contenido físico.
             *
             * La lectura posterior utiliza la capa baja de DL para evitar
             * los efectos secundarios de DLFileRank del servicio legacy.
             * Por eso esta comprobación de VIEW no debe quitarse.
             */
            DLFileEntryPermission.check(
                    themeDisplay.getPermissionChecker(),
                    entry.getFolderId(),
                    entry.getName(),
                    ActionKeys.VIEW
            );

            /*
             * La entrada ya fue:
             *
             * - asociada al requerimiento;
             * - validada contra Document Library;
             * - validada contra la identidad DL persistida;
             * - autorizada mediante VIEW.
             *
             * Recién ahora se recupera y valida el contenido binario.
             */
            DocumentoLibraryComprasHelper.OrdenMedicaContenido documento =
                    DocumentoLibraryComprasHelper
                            .leerOrdenMedicaValidada(
                                    entry,
                                    ordenMedica.getNombreOriginal()
                            );

            byte[] contenido =
                    documento.getContenido();

            if (contenido == null
                    || contenido.length == 0) {

                throw new Exception(
                        "La Orden médica recuperada está vacía."
                );
            }

            input =
                    new ByteArrayInputStream(
                            contenido
                    );

            HttpServletResponse response =
                    PortalUtil.getHttpServletResponse(
                            actionResponse
                    );

            if (response == null) {
                throw new Exception(
                        "No se pudo preparar la respuesta "
                                + "de descarga de la Orden médica."
                );
            }

            /*
             * Se utiliza el tamaño del byte[] efectivamente validado,
             * no un tamaño externo o inferido.
             */
            if (visualizar) {

                /*
                 * sendFile fuerza attachment salvo que la extensión figure
                 * en una propiedad global del portal. La lupa de Compras
                 * solicita una visualización y debe ser determinística,
                 * independientemente de esa configuración externa.
                 */
                response.setContentType(
                        documento.getContentType()
                );
                response.setHeader(
                        "Content-Disposition",
                        "inline"
                );
                response.setHeader(
                        "X-Content-Type-Options",
                        "nosniff"
                );

                ServletResponseUtil.write(
                        response,
                        input,
                        contenido.length
                );

            } else {

                ServletResponseUtil.sendFile(
                        response,
                        documento.getNombreOriginal(),
                        input,
                        contenido.length,
                        documento.getContentType()
                );
            }

            /*
             * No poner input = null.
             *
             * El finally conserva la responsabilidad de cerrar el stream
             * también en el camino exitoso.
             */
            setForward(
                    actionRequest,
                    ActionConstants.COMMON_NULL
            );

        } catch (Exception e) {
            _log.error(
                    "No se pudo descargar de forma segura "
                            + "la Orden médica de Compras.",
                    e
            );

            PortalUtil.sendError(
                    e,
                    actionRequest,
                    actionResponse
            );

        } finally {
            ServletResponseUtil.cleanUp(
                    input
            );
        }
    }

    /*
     * Si la URL identifica un fileEntry concreto, se busca exactamente
     * esa Orden médica dentro del requerimiento.
     *
     * Si no viene ese parámetro se conserva el comportamiento histórico
     * mediante getOrdenMedica(idRequerimientoCompra).
     */
    private RequerimientoCompraPresupuesto resolverOrdenMedica(
            int idRequerimientoCompra,
            long dlFileEntryIdSolicitado)
            throws Exception {

        if (dlFileEntryIdSolicitado <= 0L) {
            return BusquedaRequerimientoCompraServiceUtil
                    .getOrdenMedica(
                            idRequerimientoCompra
                    );
        }

        List<RequerimientoCompraPresupuesto> ordenesMedicas =
                BusquedaRequerimientoCompraServiceUtil
                        .listarOrdenesMedicas(
                                idRequerimientoCompra
                        );

        if (ordenesMedicas != null) {
            for (int i = 0;
                 i < ordenesMedicas.size();
                 i++) {

                RequerimientoCompraPresupuesto ordenMedica =
                        ordenesMedicas.get(i);

                if (ordenMedica == null
                        || ordenMedica.getDlFileEntryId() == null) {

                    continue;
                }

                if (ordenMedica
                        .getDlFileEntryId()
                        .longValue()
                        == dlFileEntryIdSolicitado) {

                    return ordenMedica;
                }
            }
        }

        throw new Exception(
                "La Orden médica solicitada "
                        + "no pertenece al requerimiento informado."
        );
    }

    private void validarPermisoConsulta(
            User user) throws Exception {

        if (user == null) {
            throw new Exception(
                    "No se pudo determinar el usuario actual."
            );
        }

        boolean permitido =
                PermissionUtil.userContainsRole(
                        user,
                        WebKeysCompras.ROL_VIEW_COMPRAS
                )
                        || PermissionUtil.userContainsRole(
                        user,
                        WebKeysCompras.ROL_ABM_COMPRAS
                )
                        || PermissionUtil.userContainsRole(
                        user,
                        WebKeysCompras.ROL_COTIZAR_COMPRAS
                )
                        || PermissionUtil.userContainsRole(
                        user,
                        WebKeysCompras.ROL_ANULAR_COMPRAS
                );

        if (!permitido) {
            throw new Exception(
                    "No posee permisos para consultar "
                            + "documentos de Compras."
            );
        }
    }

    protected boolean isCheckMethodOnProcessAction() {
        return false;
    }
}
