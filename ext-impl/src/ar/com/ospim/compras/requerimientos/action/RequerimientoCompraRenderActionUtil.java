package ar.com.ospim.compras.requerimientos.action;

import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompra;
import ar.com.ospim.compras.requerimientos.helper.RequerimientoCompraPresentacionHelper;

import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.WebKeys;

import javax.portlet.RenderRequest;

/**
 * Adaptador de presentación para los Actions de Compras.
 *
 * Sigue el patrón de ActionUtil del módulo Liquidaciones: la capa Action
 * conserva el conocimiento de RenderRequest y publica atributos ya preparados
 * para que los JSP se limiten a renderizar.
 */
public final class RequerimientoCompraRenderActionUtil {

    public static final String ATTR_PRESTADORES_ENVIADOS =
            "compras.requerimiento.prestadoresEnviados";

    public static final String ATTR_ERROR_PRESTADORES_ENVIADOS =
            "compras.requerimiento.errorPrestadoresEnviados";

    public static final String ATTR_PRESTADORES_DISPONIBLES_PRESUPUESTO =
            "compras.requerimiento.prestadoresDisponiblesPresupuesto";

    public static final String ATTR_PRESUPUESTOS =
            "compras.requerimiento.presupuestos";

    public static final String ATTR_IDS_PRESTADORES_CON_PRESUPUESTO =
            "compras.requerimiento.idsPrestadoresConPresupuesto";

    public static final String ATTR_ERROR_PRESUPUESTOS =
            "compras.requerimiento.errorPresupuestos";

    public static final String ATTR_PRESUPUESTO_DOCUMENTO_VALIDO =
            "compras.requerimiento.presupuestoDocumentoValido";

    public static final String ATTR_PRESUPUESTO_DOWNLOAD_URL =
            "compras.requerimiento.presupuestoDownloadURL";

    public static final String ATTR_ORDENES_MEDICAS =
            "compras.requerimiento.ordenesMedicas";

    public static final String ATTR_ERROR_ORDENES_MEDICAS =
            "compras.requerimiento.errorOrdenesMedicas";

    private RequerimientoCompraRenderActionUtil() {
    }

    public static void publicarContexto(
            RenderRequest renderRequest,
            RequerimientoCompra requerimiento) {

        if (renderRequest == null) {
            return;
        }

        ThemeDisplay themeDisplay =
                (ThemeDisplay) renderRequest.getAttribute(
                        WebKeys.THEME_DISPLAY
                );

        long scopeGroupId =
                themeDisplay != null
                        ? themeDisplay.getScopeGroupId()
                        : 0L;

        String pathMain =
                themeDisplay != null
                        ? themeDisplay.getPathMain()
                        : "";

        RequerimientoCompraPresentacionHelper.ContextoPresentacion contexto =
                new RequerimientoCompraPresentacionHelper()
                        .preparar(
                                requerimiento,
                                scopeGroupId,
                                pathMain
                        );

        renderRequest.setAttribute(
                ATTR_PRESTADORES_ENVIADOS,
                contexto.getPrestadoresEnviados()
        );

        renderRequest.setAttribute(
                ATTR_ERROR_PRESTADORES_ENVIADOS,
                contexto.getErrorPrestadoresEnviados()
        );

        renderRequest.setAttribute(
                ATTR_PRESTADORES_DISPONIBLES_PRESUPUESTO,
                contexto.getPrestadoresDisponiblesPresupuesto()
        );

        renderRequest.setAttribute(
                ATTR_PRESUPUESTOS,
                contexto.getPresupuestos()
        );

        renderRequest.setAttribute(
                ATTR_IDS_PRESTADORES_CON_PRESUPUESTO,
                contexto.getIdsPrestadoresConPresupuesto()
        );

        renderRequest.setAttribute(
                ATTR_ERROR_PRESUPUESTOS,
                contexto.getErrorPresupuestos()
        );

        renderRequest.setAttribute(
                ATTR_PRESUPUESTO_DOCUMENTO_VALIDO,
                contexto.getPresupuestoDocumentoValido()
        );

        renderRequest.setAttribute(
                ATTR_PRESUPUESTO_DOWNLOAD_URL,
                contexto.getPresupuestoDownloadURL()
        );

        renderRequest.setAttribute(
                ATTR_ORDENES_MEDICAS,
                contexto.getOrdenesMedicas()
        );

        renderRequest.setAttribute(
                ATTR_ERROR_ORDENES_MEDICAS,
                contexto.getErrorOrdenesMedicas()
        );
    }
}
