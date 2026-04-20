package ar.com.ospim.prestadores.action;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletRequest;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import ar.com.ospim.prestadores.WebKeysPrestadores;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.prestadores.beans.BusquedaCartillaConvenioFiltro;
import ar.com.ospim.prestadores.beans.CartillaConvenioRow;
import ar.com.ospim.prestadores.services.CartillaConvenioServiceUtil;

import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.ActionConstants;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portlet.ActionResponseImpl;

public class CartillaConvenioPorPlanAction extends PortletAction {

    private static final Logger log = Logger.getLogger(CartillaConvenioPorPlanAction.class);

    private static final String CMD_SEARCH = "search";
    private static final String CMD_EXPORT_XLS = "exportCartillaXls";

    private static final String FORWARD_CARTILLA = "portlet.prestadores.cartilla_convenio_por_plan";
    private static final String FORWARD_CARTILLA_RESULTADOS = "portlet.prestadores.cartilla_convenio_por_plan_resultados";

    private static final String XLS_FILE_NAME = "cartilla_convenio_prestadores.xls";

    @Override
    public void processAction(ActionMapping mapping, ActionForm form,
                              PortletConfig portletConfig, ActionRequest actionRequest,
                              ActionResponse actionResponse) throws Exception {

        String cmd = ParamUtil.getString(actionRequest, Constants.CMD, "");

        if (CMD_EXPORT_XLS.equalsIgnoreCase(cmd)) {
            exportarXls(actionRequest, actionResponse);
        }
    }

    @Override
    public ActionForward render(ActionMapping mapping, ActionForm form,
                                PortletConfig portletConfig, RenderRequest renderRequest,
                                RenderResponse renderResponse) throws Exception {

        String cmd = ParamUtil.getString(renderRequest, Constants.CMD, "");
        HttpSession session = PortalUtil.getHttpServletRequest(renderRequest).getSession();

        if (CMD_SEARCH.equalsIgnoreCase(cmd)) {
            BusquedaCartillaConvenioFiltro filtro = getFiltroFromRequest(renderRequest);
            List<CartillaConvenioRow> resultados = buscarResultados(filtro);

            guardarBusquedaEnContexto(session, renderRequest, filtro, resultados);

            log.debug("[CARTILLA-CONV][RENDER][SEARCH] resultados=" + resultados.size());

            return mapping.findForward(getForward(renderRequest, FORWARD_CARTILLA_RESULTADOS));
        }

        cargarListas(session);
        restaurarBusquedaDesdeSession(session, renderRequest);

        return mapping.findForward(getForward(renderRequest, FORWARD_CARTILLA));
    }

    private BusquedaCartillaConvenioFiltro getFiltroFromRequest(PortletRequest request) {
        BusquedaCartillaConvenioFiltro filtro = new BusquedaCartillaConvenioFiltro();

        int idPlan = ParamUtil.getInteger(request, "idPlan", 0);
        int idPrestador = ParamUtil.getInteger(request, "idPrestador", 0);
        int idProvincia = ParamUtil.getInteger(request, "idProvincia", 0);
        int idLocalidad = ParamUtil.getInteger(request, "idLocalidad", 0);
        int idEspecialidad = ParamUtil.getInteger(request, "idEspecialidad", 0);

        filtro.setIdPlan(idPlan > 0 ? Integer.valueOf(idPlan) : null);
        filtro.setIdPrestador(idPrestador > 0 ? Integer.valueOf(idPrestador) : null);
        filtro.setIdProvincia(idProvincia > 0 ? Integer.valueOf(idProvincia) : null);
        filtro.setIdLocalidad(idLocalidad > 0 ? Integer.valueOf(idLocalidad) : null);
        filtro.setIdEspecialidad(idEspecialidad > 0 ? Integer.valueOf(idEspecialidad) : null);

        filtro.setCuitPrestador(normalizarTexto(
                ParamUtil.getString(request, "cuitPrestador", null)
        ));
        filtro.setPrestadorDescripcion(normalizarTexto(
                ParamUtil.getString(request, "prestadorDescripcion", null)
        ));

        filtro.setIncluyeBajas(ParamUtil.getBoolean(request, "incluyeBajas", false));
        filtro.setPagina(ParamUtil.getInteger(request, "pagina", 1));

        return filtro;
    }

    private List<CartillaConvenioRow> buscarResultados(BusquedaCartillaConvenioFiltro filtro) throws Exception {
        if (filtro == null) {
            filtro = new BusquedaCartillaConvenioFiltro();
        }

        List<CartillaConvenioRow> resultados = CartillaConvenioServiceUtil.buscarCartillaConvenioPorPlan(filtro);
        return resultados != null ? resultados : new ArrayList<CartillaConvenioRow>();
    }

    private void guardarBusquedaEnContexto(HttpSession session,
                                           RenderRequest renderRequest,
                                           BusquedaCartillaConvenioFiltro filtro,
                                           List<CartillaConvenioRow> resultados) {

        session.setAttribute(WebKeysPrestadores.BUSQUEDA_CARTILLA_CONVENIO_FILTRO, filtro);
        session.setAttribute(WebKeysPrestadores.BUSQUEDA_CARTILLA_CONVENIO_RESULTS, resultados);

        renderRequest.setAttribute(WebKeysPrestadores.BUSQUEDA_CARTILLA_CONVENIO_FILTRO, filtro);
        renderRequest.setAttribute(WebKeysPrestadores.BUSQUEDA_CARTILLA_CONVENIO_RESULTS, resultados);
    }

    private void restaurarBusquedaDesdeSession(HttpSession session, RenderRequest renderRequest) {
        Object filtroSession = session.getAttribute(WebKeysPrestadores.BUSQUEDA_CARTILLA_CONVENIO_FILTRO);
        Object resultadosSession = session.getAttribute(WebKeysPrestadores.BUSQUEDA_CARTILLA_CONVENIO_RESULTS);

        if (filtroSession != null) {
            renderRequest.setAttribute(WebKeysPrestadores.BUSQUEDA_CARTILLA_CONVENIO_FILTRO, filtroSession);
        } else {
            renderRequest.setAttribute(
                    WebKeysPrestadores.BUSQUEDA_CARTILLA_CONVENIO_FILTRO,
                    new BusquedaCartillaConvenioFiltro()
            );
        }

        if (resultadosSession != null) {
            renderRequest.setAttribute(WebKeysPrestadores.BUSQUEDA_CARTILLA_CONVENIO_RESULTS, resultadosSession);
        } else {
            renderRequest.setAttribute(
                    WebKeysPrestadores.BUSQUEDA_CARTILLA_CONVENIO_RESULTS,
                    new ArrayList<CartillaConvenioRow>()
            );
        }
    }

    private void cargarListas(HttpSession session) throws Exception {

        if (session.getAttribute(WebKeysPrestadores.PLANES_EN_SESSION) == null) {
            session.setAttribute(
                    WebKeysPrestadores.PLANES_EN_SESSION,
                    TraeListasServiceUtil.getPlanesOspim()
            );
        }

        if (session.getAttribute(WebKeysPrestadores.PROVINCIAS_EN_SESSION) == null) {
            session.setAttribute(
                    WebKeysPrestadores.PROVINCIAS_EN_SESSION,
                    TraeListasServiceUtil.getProvincias()
            );
        }

        if (session.getAttribute(WebKeysPrestadores.LOCALIDADES_EN_SESSION) == null) {
            session.setAttribute(
                    WebKeysPrestadores.LOCALIDADES_EN_SESSION,
                    TraeListasServiceUtil.getLocalidades()
            );
        }

        if (session.getAttribute(WebKeysPrestadores.LISTAS_DE_ESPECIALIDAD_PRESTADOR_EN_SESSION) == null) {
            session.setAttribute(
                    WebKeysPrestadores.LISTAS_DE_ESPECIALIDAD_PRESTADOR_EN_SESSION,
                    TraeListasServiceUtil.getEspecialidadesPrestador()
            );
        }
    }

    private String normalizarTexto(String value) {
        if (value == null) {
            return null;
        }

        String trimmed = value.trim();
        return trimmed.length() > 0 ? trimmed : null;
    }

    private void exportarXls(ActionRequest actionRequest, ActionResponse actionResponse) throws Exception {
        BusquedaCartillaConvenioFiltro filtro = getFiltroFromRequest(actionRequest);
        List<CartillaConvenioRow> resultados = buscarResultados(filtro);

        HSSFWorkbook workbook = null;
        OutputStream outputStream = null;

        try {
            workbook = crearWorkbook(resultados);

            HttpServletResponse httpRes = getHttpServletResponse((ActionResponseImpl) actionResponse);

            outputStream = httpRes.getOutputStream();
            workbook.write(outputStream);
            outputStream.flush();

            setForward(actionRequest, ActionConstants.COMMON_NULL);

            log.info("[CARTILLA-CONV][EXPORT][OK] resultados=" + resultados.size());

        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (Exception e) {
                    log.warn("[CARTILLA-CONV][EXPORT][WARN] Error cerrando OutputStream", e);
                }
            }

            if (workbook != null) {
                try {
                    workbook.close();
                } catch (Exception e) {
                    log.warn("[CARTILLA-CONV][EXPORT][WARN] Error cerrando Workbook", e);
                }
            }
        }
    }

    private static HttpServletResponse getHttpServletResponse(ActionResponseImpl actionResponse) {
        HttpServletResponse httpRes = actionResponse.getHttpServletResponse();

        httpRes.reset();
        httpRes.setContentType("application/vnd.ms-excel");
        httpRes.setHeader("Content-Disposition", "attachment; filename=\"" + XLS_FILE_NAME + "\"");
        httpRes.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        httpRes.setHeader("Pragma", "no-cache");
        httpRes.setDateHeader("Expires", 0);

        return httpRes;
    }

    private HSSFWorkbook crearWorkbook(List<CartillaConvenioRow> resultados) {
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("cartilla_convenio_prestadores");

        int rowNum = 0;
        Row header = sheet.createRow(rowNum++);

        header.createCell(0).setCellValue("plan");
        header.createCell(1).setCellValue("prestador");
        header.createCell(2).setCellValue("cuit");
        header.createCell(3).setCellValue("zona");
        header.createCell(4).setCellValue("especialidad");
        header.createCell(5).setCellValue("domicilio");
        header.createCell(6).setCellValue("telefono");
        header.createCell(7).setCellValue("localidad");
        header.createCell(8).setCellValue("provincia");

        if (resultados != null) {
            for (CartillaConvenioRow rowData : resultados) {
                Row row = sheet.createRow(rowNum++);

                row.createCell(0).setCellValue(safe(rowData.getPlanDescripcion()));
                row.createCell(1).setCellValue(safe(rowData.getPrestadorDescripcion()));
                row.createCell(2).setCellValue(safe(rowData.getCuitPrestador()));
                row.createCell(3).setCellValue(safe(rowData.getZona()));
                row.createCell(4).setCellValue(safe(rowData.getEspecialidad()));
                row.createCell(5).setCellValue(safe(rowData.getDomicilio()));
                row.createCell(6).setCellValue(safe(rowData.getTelefono()));
                row.createCell(7).setCellValue(safe(rowData.getLocalidad()));
                row.createCell(8).setCellValue(safe(rowData.getProvincia()));
            }
        }

        for (int i = 0; i <= 8; i++) {
            sheet.autoSizeColumn(i);
        }

        return workbook;
    }

    private String safe(String value) {
        return value != null ? value : "";
    }
}