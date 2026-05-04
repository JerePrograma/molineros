package ar.com.ospim.afiliados.action;

import java.util.List;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.theme.ThemeDisplay;

import ar.com.ospim.afiliados.services.VendedorServiceUtil;

public class VendedorAction extends PortletAction {

    @Override
    public ActionForward render(
        ActionMapping mapping,
        ActionForm form,
        PortletConfig config,
        RenderRequest renderRequest,
        RenderResponse renderResponse
    ) throws Exception {

        String cmd = ParamUtil.getString(renderRequest, "cmd");

        if ("buscar".equals(cmd)) {
            return buscar(mapping, renderRequest);
        }

        if ("editar".equals(cmd)) {
            return editar(mapping, renderRequest);
        }

        if ("nuevo".equals(cmd)) {
            return mapping.findForward("portlet.afiliados.vendedor.detalle");
        }

        return mapping.findForward("portlet.afiliados.vendedor.list");
    }

    @Override
    public void processAction(
        ActionMapping mapping,
        ActionForm form,
        PortletConfig config,
        ActionRequest actionRequest,
        ActionResponse actionResponse
    ) throws Exception {

        String cmd = ParamUtil.getString(actionRequest, "cmd");

        if ("guardar".equals(cmd)) {
            guardar(actionRequest, actionResponse);
            return;
        }

        if ("eliminar".equals(cmd)) {
            eliminar(actionRequest, actionResponse);
            return;
        }

        if ("guardarHistorico".equals(cmd)) {
            guardarHistorico(actionRequest, actionResponse);
            return;
        }
        
        if ("eliminarHistorico".equals(cmd)) {
            eliminarHistorico(actionRequest, actionResponse);
            return;
        }
    }

    private ActionForward buscar(ActionMapping mapping, RenderRequest request) throws Exception {
        String nombre = ParamUtil.getString(request, "nombre");
        String apellido = ParamUtil.getString(request, "apellido");
        String dni = ParamUtil.getString(request, "dni");

        List<Map<String, Object>> resultados = VendedorServiceUtil.buscarVendedores(nombre, apellido, dni);

        request.setAttribute("resultados", resultados);
        request.getPortletSession().setAttribute("ven_nombre", nombre);
        request.getPortletSession().setAttribute("ven_apellido", apellido);
        request.getPortletSession().setAttribute("ven_dni", dni);

        if (LiferayWindowState.EXCLUSIVE.equals(request.getWindowState())) {
            return mapping.findForward("portlet.afiliados.vendedores.result");
        }

        return mapping.findForward("portlet.afiliados.vendedor.list");
    }

    private ActionForward editar(ActionMapping mapping, RenderRequest request) throws Exception {
        long idVal = ParamUtil.getLong(request, "id");

        if (idVal > 0) {
            Long id = Long.valueOf(idVal);
            request.setAttribute("vendedor", VendedorServiceUtil.getVendedor(id));
            request.setAttribute("historico", VendedorServiceUtil.getHistorico(id));
        }

        return mapping.findForward("portlet.afiliados.vendedor.detalle");
    }

    private void volverAPantallaEdicion(ActionRequest request, ActionResponse response, Long id) {
        response.setRenderParameter("struts_action", "/afiliados/vendedor");
        response.setRenderParameter("tabs1", "vendedores");

        if (id != null) {
            response.setRenderParameter("cmd", "editar");
            response.setRenderParameter("id", String.valueOf(id));
        } else {
            response.setRenderParameter("cmd", "nuevo");
        }
    }
    
    private void guardar(ActionRequest request, ActionResponse response) throws Exception {
        try {
            long idVal = ParamUtil.getLong(request, "id");
            Long id = (idVal > 0) ? Long.valueOf(idVal) : null;

            String nombre = ParamUtil.getString(request, "nombre");
            String apellido = ParamUtil.getString(request, "apellido");
            String dni = ParamUtil.getString(request, "dni");
            String email = ParamUtil.getString(request, "email");       
            String horaDesde = ParamUtil.getString(request, "horaDesde").trim();
            String horaHasta = ParamUtil.getString(request, "horaHasta").trim();
            
            nombre = nombre != null ? nombre.trim() : "";
            apellido = apellido != null ? apellido.trim() : "";
            dni = dni != null ? dni.trim() : "";
            email = email != null ? email.trim().toLowerCase() : "";
            
            if (nombre.isEmpty() || apellido.isEmpty() || dni.isEmpty() || email.isEmpty()) {
                SessionErrors.add(request, "vendedor_datos_obligatorios");
                volverAPantallaEdicion(request, response, id);
                return;
            }

            if (!dni.matches("\\d{5,8}")) {
                SessionErrors.add(request, "vendedor_dni_invalido");
                volverAPantallaEdicion(request, response, id);
                return;
            }

            if (!nombre.matches("[A-Za-zÁÉÍÓÚáéíóúÑñÜü' -]+")) {
                SessionErrors.add(request, "vendedor_nombre_invalido");
                volverAPantallaEdicion(request, response, id);
                return;
            }

            if (!apellido.matches("[A-Za-zÁÉÍÓÚáéíóúÑñÜü' -]+")) {
                SessionErrors.add(request, "vendedor_apellido_invalido");
                volverAPantallaEdicion(request, response, id);
                return;
            }

            if (email.isEmpty()) {
                SessionErrors.add(request, "vendedor_email_obligatorio");
                volverAPantallaEdicion(request, response, id);
                return;
            }

            if (!email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
                SessionErrors.add(request, "vendedor_email_invalido");
                volverAPantallaEdicion(request, response, id);
                return;
            }
            
            if (horaDesde.isEmpty() || horaHasta.isEmpty()) {
                SessionErrors.add(request, "vendedor_horario_obligatorio");
                volverAPantallaEdicion(request, response, id);
                return;
            }

            if (!horaDesde.matches("^\\d{2}:\\d{2}$") || !horaHasta.matches("^\\d{2}:\\d{2}$")) {
                SessionErrors.add(request, "vendedor_horario_invalido");
                volverAPantallaEdicion(request, response, id);
                return;
            }

            if (horaDesde.compareTo(horaHasta) >= 0) {
                SessionErrors.add(request, "vendedor_horario_rango_invalido");
                volverAPantallaEdicion(request, response, id);
                return;
            }
            
            String usuario = getUsuarioPortal(request);

            Long idGuardado = VendedorServiceUtil.guardarVendedor(
            	    id, nombre, apellido, dni, email, horaDesde, horaHasta, usuario
            		);

            SessionMessages.add(request, "vendedor_guardado_ok");

            String redirect = ParamUtil.getString(request, "redirect");
            if (redirect != null && redirect.trim().length() > 0) {
                response.sendRedirect(redirect);
                return;
            }

            response.setRenderParameter("struts_action", "/afiliados/vendedor");
            response.setRenderParameter("tabs1", "vendedores");
            response.setRenderParameter("cmd", "editar");
            response.setRenderParameter("id", String.valueOf(idGuardado));

        } catch (Exception e) {
            String msg = e.getMessage() != null ? e.getMessage() : "";

            if (msg.contains("vendedor_dni_key")) {
                SessionErrors.add(request, "vendedor_dni_duplicado");
            } else {
                SessionErrors.add(request, "vendedor_guardado_error");
            }

            response.setRenderParameter("struts_action", "/afiliados/vendedor");
            response.setRenderParameter("tabs1", "vendedores");

            long idVal = ParamUtil.getLong(request, "id");
            if (idVal > 0) {
                response.setRenderParameter("cmd", "editar");
                response.setRenderParameter("id", String.valueOf(idVal));
            } else {
                response.setRenderParameter("cmd", "nuevo");
            }
        }
    }

    private void eliminar(ActionRequest request, ActionResponse response) throws Exception {
        try {
            Long id = Long.valueOf(ParamUtil.getLong(request, "id"));
            String usuario = getUsuarioPortal(request);

            VendedorServiceUtil.darBaja(id, usuario);

            SessionMessages.add(request, "vendedor_baja_ok");

            String redirect = ParamUtil.getString(request, "redirect");
            if (Validator.isNotNull(redirect)) {
                response.sendRedirect(redirect);
                return;
            }

            response.setRenderParameter("struts_action", "/afiliados/vendedor");
            response.setRenderParameter("tabs1", "vendedores");
            response.setRenderParameter("cmd", "buscar");

            String nombre = (String) request.getPortletSession().getAttribute("ven_nombre");
            String apellido = (String) request.getPortletSession().getAttribute("ven_apellido");
            String dni = (String) request.getPortletSession().getAttribute("ven_dni");

            if (nombre != null) response.setRenderParameter("nombre", nombre);
            if (apellido != null) response.setRenderParameter("apellido", apellido);
            if (dni != null) response.setRenderParameter("dni", dni);

        } catch (Exception e) {
            SessionErrors.add(request, "vendedor_baja_error");
            throw e;
        }
    }

    private void guardarHistorico(ActionRequest request, ActionResponse response) throws Exception {
        try {
            Long idVendedor = Long.valueOf(ParamUtil.getLong(request, "idVendedor"));

            long idHistoricoVal = ParamUtil.getLong(request, "idHistorico");
            Long idHistorico = (idHistoricoVal > 0) ? Long.valueOf(idHistoricoVal) : null;

            int desdeDia = ParamUtil.getInteger(request, "histDesdeDia");
            int desdeMes = ParamUtil.getInteger(request, "histDesdeMes");
            int desdeAnio = ParamUtil.getInteger(request, "histDesdeAnio");

            int hastaDia = ParamUtil.getInteger(request, "histHastaDia");
            int hastaMes = ParamUtil.getInteger(request, "histHastaMes");
            int hastaAnio = ParamUtil.getInteger(request, "histHastaAnio");

            if (desdeDia <= 0 || desdeAnio <= 0 || hastaDia <= 0 || hastaAnio <= 0) {
                SessionErrors.add(request, "historico_fechas_obligatorias");
                response.setRenderParameter("struts_action", "/afiliados/vendedor");
                response.setRenderParameter("tabs1", "vendedores");
                response.setRenderParameter("cmd", "editar");
                response.setRenderParameter("id", String.valueOf(idVendedor));
                return;
            }

            String fechaDesde = String.format("%04d-%02d-%02d", desdeAnio, desdeMes + 1, desdeDia);
            String fechaHasta = String.format("%04d-%02d-%02d", hastaAnio, hastaMes + 1, hastaDia);

            if (fechaDesde.compareTo(fechaHasta) > 0) {
                SessionErrors.add(request, "historico_rango_fechas_invalido");
                response.setRenderParameter("struts_action", "/afiliados/vendedor");
                response.setRenderParameter("tabs1", "vendedores");
                response.setRenderParameter("cmd", "editar");
                response.setRenderParameter("id", String.valueOf(idVendedor));
                return;
            }
            
            String motivo = ParamUtil.getString(request, "motivo");
            String observacion = ParamUtil.getString(request, "observacion");
            String usuario = getUsuarioPortal(request);

            VendedorServiceUtil.guardarHistorico(idHistorico, idVendedor, fechaDesde, fechaHasta, motivo, observacion, usuario);

            SessionMessages.add(request, "historico_guardado_ok");

            String redirect = ParamUtil.getString(request, "redirect");
            if (redirect != null && redirect.trim().length() > 0) {
                response.sendRedirect(redirect);
                return;
            }

            response.setRenderParameter("struts_action", "/afiliados/vendedor");
            response.setRenderParameter("tabs1", "vendedores");
            response.setRenderParameter("cmd", "editar");
            response.setRenderParameter("id", String.valueOf(idVendedor));

        } catch (Exception e) {
            SessionErrors.add(request, "historico_guardado_error");
            throw e;
        }
    }
    
    private void eliminarHistorico(ActionRequest request, ActionResponse response) throws Exception {
        try {
            Long idVendedor = Long.valueOf(ParamUtil.getLong(request, "idVendedor"));
            Long idHistorico = Long.valueOf(ParamUtil.getLong(request, "idHistorico"));
            String usuario = getUsuarioPortal(request);

            VendedorServiceUtil.eliminarHistorico(idHistorico, usuario);

            SessionMessages.add(request, "historico_guardado_ok");

            String redirect = ParamUtil.getString(request, "redirect");
            if (redirect != null && redirect.trim().length() > 0) {
                response.sendRedirect(redirect);
                return;
            }
            
            response.setRenderParameter("struts_action", "/afiliados/vendedor");
            response.setRenderParameter("tabs1", "vendedores");
            response.setRenderParameter("cmd", "editar");
            response.setRenderParameter("id", String.valueOf(idVendedor));

        } catch (Exception e) {
            SessionErrors.add(request, "historico_guardado_error");
            throw e;
        }
    }
    
    private String getUsuarioPortal(ActionRequest request) {
        ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);

        if (themeDisplay != null && themeDisplay.getUser() != null) {
            return themeDisplay.getUser().getScreenName();
        }

        return request.getRemoteUser();
    }
}