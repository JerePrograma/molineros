package ar.com.ospim.farmaciaOspim.action;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpServletResponse;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import ar.com.ospim.farmaciaOspim.beans.ImportacionVademecumAdmifarm;
import ar.com.ospim.farmaciaOspim.beans.ImportacionVademecumAdmifarm.Comparacion;
import ar.com.ospim.farmaciaOspim.beans.ImportacionVademecumAdmifarm.Registro;
import ar.com.ospim.farmaciaOspim.helper.VademecumAdmifarmHelper;
import ar.com.ospim.farmaciaOspim.reportes.GeneraVademecumAdmifarmXLS;
import ar.com.ospim.farmaciaOspim.services.VademecumAdmifarmServiceUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.upload.UploadPortletRequest;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.ActionConstants;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;
import com.liferay.util.servlet.ServletResponseUtil;

public class ImportarVademecumAdmifarmAction extends PortletAction {
    private static final Log log = LogFactoryUtil.getLog(ImportarVademecumAdmifarmAction.class);

    public ActionForward render(ActionMapping mapping, ActionForm form, PortletConfig config,
            RenderRequest request, RenderResponse response) throws Exception {
        request.setAttribute("tabs1", "vademecum");
        return mapping.findForward(getForward(request, "portlet.farmaciaospim.view"));
    }

    public void processAction(ActionMapping mapping, ActionForm form, PortletConfig config,
            ActionRequest request, ActionResponse response) throws Exception {
        try {
            User usuario = PortalUtil.getUser(request);
            VademecumAdmifarmHelper.validarPermiso(usuario);
            if ("descargar".equals(ParamUtil.getString(request, "operacion"))) {
                descargar(request, response);
                return;
            }
            if (!"POST".equalsIgnoreCase(PortalUtil.getHttpServletRequest(request).getMethod())) {
                throw new IllegalArgumentException("La importacion debe enviarse desde el formulario.");
            }
            UploadPortletRequest upload = PortalUtil.getUploadPortletRequest(request);
            String tipo = ParamUtil.getString(upload, "tipo");
            VademecumAdmifarmHelper.consumirToken(request.getPortletSession(), usuario.getUserId(),
                tipo, ParamUtil.getString(upload, "tokenVademecum"));
            List<Registro> registros = VademecumAdmifarmHelper.leerArchivo(upload.getFile("archivo"),
                upload.getFileName("archivo"), tipo);
            ImportacionVademecumAdmifarm importacion = VademecumAdmifarmServiceUtil.importar(tipo, registros);
            Comparacion cambios = VademecumAdmifarmHelper.comparar(importacion.getAnteriores(), registros);
            SessionMessages.add(request, "vademecum-importado", "Vademecum " + tipo + " importado: "
                + registros.size() + " registros. Altas: " + cambios.getAltas().size()
                + ". Bajas: " + cambios.getBajas().size() + ". Modificaciones: "
                + cambios.getModificadosDespues().size() + ". Sin cambios: " + cambios.getSinCambios() + ".");
        } catch (SecurityException e) {
            SessionErrors.add(request, "vademecum-error", e.getMessage());
        } catch (IllegalArgumentException e) {
            SessionErrors.add(request, "vademecum-error", e.getMessage());
        } catch (Exception e) {
            log.error("Error procesando Vademecum Admifarm", e);
            SessionErrors.add(request, "vademecum-error",
                "No se pudo completar la operacion de Vademecum. Verifique el estado antes de reintentar.");
        }
        response.setRenderParameter("struts_action", "/farmaciaospim/view");
        response.setRenderParameter("tabs1", "vademecum");
    }

    // Adaptado de DescargarDocumentoCompraReclamoAction: permiso en servidor y respuesta binaria.
    private void descargar(ActionRequest request, ActionResponse response) throws Exception {
        String tipo = ParamUtil.getString(request, "tipo");
        Timestamp fecha;
        try { fecha = Timestamp.valueOf(ParamUtil.getString(request, "fecha")); }
        catch (IllegalArgumentException e) { throw new IllegalArgumentException("La fecha de importacion no es valida."); }
        ImportacionVademecumAdmifarm importacion = VademecumAdmifarmServiceUtil.getImportacion(tipo, fecha);
        ByteArrayOutputStream salida = new ByteArrayOutputStream();
        HSSFWorkbook libro = GeneraVademecumAdmifarmXLS.generar(tipo, importacion);
        try { libro.write(salida); }
        finally { libro.close(); }
        byte[] contenido = salida.toByteArray();
        HttpServletResponse httpResponse = PortalUtil.getHttpServletResponse(response);
        if (httpResponse == null) { throw new IllegalStateException("No se pudo preparar la descarga."); }
        httpResponse.setHeader("Cache-Control", "no-store");
        String nombre = "vademecum_" + tipo + "_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(fecha) + ".xls";
        ServletResponseUtil.sendFile(httpResponse, nombre, new ByteArrayInputStream(contenido),
            contenido.length, "application/vnd.ms-excel");
        setForward(request, ActionConstants.COMMON_NULL);
    }

    protected boolean isCheckMethodOnProcessAction() {
        // La descarga usa el enlace de accion legacy; la importacion exige POST arriba.
        return false;
    }
}
