package ar.com.ospim.compras.requerimientos.action;

import ar.com.ospim.compras.WebKeysCompras;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraSector;
import ar.com.ospim.compras.requerimientos.beans.TipoPrestadorSector;
import ar.com.ospim.compras.requerimientos.helper.ConfiguracionCotizacionPrestadorHelper;
import ar.com.ospim.compras.requerimientos.service.BusquedaRequerimientoCompraServiceUtil;
import ar.com.ospim.compras.requerimientos.service.ConfiguracionCotizacionPrestadorServiceUtil;
import ar.com.ospim.util.PermissionUtil;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletRequest;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ConfigurarTiposPrestadorSectorAction
        extends PortletAction {

    private static final Log _log =
            LogFactoryUtil.getLog(
                    ConfigurarTiposPrestadorSectorAction.class
            );

    private static final String STRUTS_ACTION_CONFIGURACION =
            "/compras/configurar_tipos_prestador_sector";

    private final ConfiguracionCotizacionPrestadorHelper helper =
            new ConfiguracionCotizacionPrestadorHelper();

    public void processAction(ActionMapping mapping,
                              ActionForm form,
                              PortletConfig portletConfig,
                              ActionRequest actionRequest,
                              ActionResponse actionResponse)
            throws Exception {

        int idSector =
                getIntegerParam(
                        actionRequest,
                        "id_sector",
                        0
                );

        try {
            User user = PortalUtil.getUser(actionRequest);

            validarPermisoConfiguracion(user);
            validarSector(idSector);

            List<TipoPrestadorSector> tiposSeleccionados =
                    getTiposSeleccionados(
                            actionRequest,
                            "tipo_prestador_cotizacion"
                    );

            helper
                    .guardarConfiguracion(
                            idSector,
                            tiposSeleccionados,
                            user.getScreenName()
                    );

            SessionMessages.add(
                    actionRequest,
                    "configuracion-prestadores-sector-actualizada"
            );

        } catch (Exception e) {
            _log.error(
                    "Error guardando configuración sector/tipo prestador. "
                            + "idSector="
                            + idSector,
                    e
            );

            /*
             * Se guarda el mensaje como valor del SessionError para que
             * permanezca disponible durante el render posterior.
             */
            SessionErrors.add(
                    actionRequest,
                    "configuracion-prestadores-sector-error",
                    getMensajeError(e)
            );
        }

        prepararRender(
                actionResponse,
                idSector
        );

        setForward(
                actionRequest,
                WebKeysCompras
                        .FORWARD_COMPRAS_CONFIGURAR_TIPOS_PRESTADOR
        );
    }

    public ActionForward render(ActionMapping mapping,
                                ActionForm form,
                                PortletConfig portletConfig,
                                RenderRequest renderRequest,
                                RenderResponse renderResponse)
            throws Exception {

        try {
            User user = PortalUtil.getUser(renderRequest);

            validarPermisoConfiguracion(user);

            List<RequerimientoCompraSector> sectores =
                    BusquedaRequerimientoCompraServiceUtil
                            .listarSectores();

            if (sectores == null) {
                sectores =
                        new ArrayList<RequerimientoCompraSector>();
            }

            int idSector =
                    getIntegerParam(
                            renderRequest,
                            "id_sector",
                            0
                    );

            if (idSector <= 0) {
                idSector =
                        obtenerPrimerSector(sectores);
            }

            renderRequest.setAttribute(
                    WebKeysCompras.SECTORES_REQUERIMIENTO,
                    sectores
            );

            renderRequest.setAttribute(
                    WebKeysCompras
                            .ID_SECTOR_CONFIGURACION_COTIZACION,
                    Integer.valueOf(idSector)
            );

            List<TipoPrestadorSector> tipos =
                    new ArrayList<TipoPrestadorSector>();

            if (idSector > 0) {
                tipos =
                        ConfiguracionCotizacionPrestadorServiceUtil
                                .listarTiposPrestadorSector(
                                        idSector
                                );
            }

            if (tipos == null) {
                tipos =
                        new ArrayList<TipoPrestadorSector>();
            }

            renderRequest.setAttribute(
                    WebKeysCompras.TIPOS_PRESTADOR_SECTOR,
                    tipos
            );

            return mapping.findForward(
                    WebKeysCompras
                            .FORWARD_COMPRAS_CONFIGURAR_TIPOS_PRESTADOR
            );

        } catch (Exception e) {
            _log.error(
                    "Error cargando configuración "
                            + "sector/tipo prestador.",
                    e
            );

            renderRequest.setAttribute(
                    WebKeysCompras.ERROR_PARA_ALERT,
                    getMensajeError(e)
            );

            return mapping.findForward(
                    WebKeysCompras.FORWARD_COMPRAS_ERROR
            );
        }
    }

    private void prepararRender(ActionResponse actionResponse,
                                int idSector) {

        actionResponse.setRenderParameter(
                "struts_action",
                STRUTS_ACTION_CONFIGURACION
        );

        if (idSector > 0) {
            actionResponse.setRenderParameter(
                    "id_sector",
                    String.valueOf(idSector)
            );
        }
    }

    private void validarPermisoConfiguracion(User user)
            throws Exception {

        if (user == null) {
            throw new Exception(
                    "No se pudo determinar el usuario actual."
            );
        }

        if (!PermissionUtil.userContainsRole(
                user,
                WebKeysCompras.ROL_ABM_COMPRAS
        )) {
            throw new Exception(
                    "No posee permisos para configurar "
                            + "los tipos de prestador por sector."
            );
        }
    }

    private void validarSector(int idSector)
            throws Exception {

        if (idSector <= 0) {
            throw new Exception(
                    "Debe seleccionar un sector."
            );
        }

        RequerimientoCompraSector sector =
                BusquedaRequerimientoCompraServiceUtil
                        .getSector(idSector);

        if (sector == null
                || sector.getIdSector() <= 0) {

            throw new Exception(
                    "El sector seleccionado no existe."
            );
        }
    }

    private int obtenerPrimerSector(
            List<RequerimientoCompraSector> sectores) {

        if (sectores == null
                || sectores.isEmpty()) {

            return 0;
        }

        for (int i = 0; i < sectores.size(); i++) {
            RequerimientoCompraSector sector =
                    sectores.get(i);

            if (sector != null
                    && sector.getIdSector() > 0) {

                return sector.getIdSector();
            }
        }

        return 0;
    }

    private int getIntegerParam(PortletRequest request,
                                String paramName,
                                int defaultValue) {

        int value =
                ParamUtil.getInteger(
                        request,
                        paramName,
                        defaultValue
                );

        if (value != defaultValue) {
            return value;
        }

        String[] values =
                getParameterValues(
                        request,
                        paramName
                );

        if (values == null
                || values.length == 0
                || values[0] == null) {

            return defaultValue;
        }

        try {
            return Integer.parseInt(
                    values[0].trim()
            );

        } catch (NumberFormatException ignored) {
            return defaultValue;
        }
    }

    private List<TipoPrestadorSector> getTiposSeleccionados(
            PortletRequest request,
            String paramName) throws Exception {

        String[] values =
                getParameterValues(
                        request,
                        paramName
                );

        if (values == null
                || values.length == 0) {

            return new ArrayList<TipoPrestadorSector>();
        }

        List<TipoPrestadorSector> resultado =
                new ArrayList<TipoPrestadorSector>();

        for (int i = 0; i < values.length; i++) {
            String value = values[i];

            if (value == null
                    || value.trim().length() == 0) {

                continue;
            }

            String[] partes = value.trim().split(":");

            if (partes.length != 2) {
                throw new Exception(
                        "La configuración recibida posee un formato inválido."
                );
            }

            try {
                TipoPrestadorSector tipo = new TipoPrestadorSector();
                tipo.setIdTipoPrestacion(
                        Integer.parseInt(partes[0])
                );
                tipo.setIdTipoPrestador(
                        Integer.parseInt(partes[1])
                );
                resultado.add(tipo);
            } catch (NumberFormatException e) {
                throw new Exception(
                        "La configuración recibida contiene IDs inválidos."
                );
            }
        }

        return resultado;
    }

    private String[] getParameterValues(
            PortletRequest request,
            String paramName) {

        String[] directValues =
                request.getParameterValues(
                        paramName
                );

        if (directValues != null) {
            return directValues;
        }

        Map parameterMap =
                request.getParameterMap();

        if (parameterMap == null
                || parameterMap.isEmpty()) {

            return null;
        }

        for (Object entryObject :
                parameterMap.entrySet()) {

            Map.Entry entry =
                    (Map.Entry) entryObject;

            Object keyObject =
                    entry.getKey();

            if (keyObject == null) {
                continue;
            }

            String key =
                    String.valueOf(keyObject);

            if (!key.endsWith(paramName)) {
                continue;
            }

            Object valueObject =
                    entry.getValue();

            if (valueObject instanceof String[]) {
                return (String[]) valueObject;
            }
        }

        return null;
    }

    private String getMensajeError(Exception e) {
        if (e == null
                || e.getMessage() == null
                || e.getMessage().trim().length() == 0) {

            return "Ocurrió un error procesando "
                    + "la configuración de prestadores por sector.";
        }

        return e.getMessage().trim();
    }
}
