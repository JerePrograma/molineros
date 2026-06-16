package ar.com.ospim.compras.requerimientos.action;

import ar.com.ospim.compras.WebKeysCompras;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraSector;
import ar.com.ospim.compras.requerimientos.beans.TipoPrestadorSector;
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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ConfigurarTiposPrestadorSectorAction
        extends PortletAction {

    private static final Log _log =
            LogFactoryUtil.getLog(
                    ConfigurarTiposPrestadorSectorAction.class
            );

    private static final String STRUTS_ACTION_CONFIGURACION =
            "/compras/configurar_tipos_prestador_sector";

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

            int[] idsTiposSeleccionados =
                    getIntegerArrayParam(
                            actionRequest,
                            "id_tipo_prestador"
                    );

            ConfiguracionCotizacionPrestadorServiceUtil
                    .guardarConfiguracion(
                            idSector,
                            idsTiposSeleccionados,
                            user.getScreenName()
                    );

            SessionMessages.add(
                    actionRequest,
                    "configuracion-cotizaciones-actualizada"
            );

            if (_log.isInfoEnabled()) {
                _log.info(
                        "Configuracion de cotizaciones actualizada. "
                                + "idSector="
                                + idSector
                                + ", cantidadTipos="
                                + idsTiposSeleccionados.length
                                + ", usuario="
                                + user.getScreenName()
                );
            }

        } catch (Exception e) {
            _log.error(
                    "Error guardando configuracion sector/tipo prestador. "
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
                    "configuracion-cotizaciones-error",
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
                    "Error cargando configuracion "
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

    private int[] getIntegerArrayParam(
            PortletRequest request,
            String paramName) {

        String[] values =
                getParameterValues(
                        request,
                        paramName
                );

        if (values == null
                || values.length == 0) {

            return new int[0];
        }

        Set<Integer> ids =
                new LinkedHashSet<Integer>();

        for (int i = 0; i < values.length; i++) {
            String value = values[i];

            if (value == null
                    || value.trim().length() == 0) {

                continue;
            }

            try {
                int id =
                        Integer.parseInt(
                                value.trim()
                        );

                if (id > 0) {
                    ids.add(
                            Integer.valueOf(id)
                    );
                }

            } catch (NumberFormatException ignored) {
            }
        }

        int[] resultado =
                new int[ids.size()];

        int index = 0;

        for (Integer id : ids) {
            resultado[index++] =
                    id.intValue();
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

            return "Ocurrio un error procesando "
                    + "la configuracion de cotizaciones.";
        }

        return e.getMessage().trim();
    }
}