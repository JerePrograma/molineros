package ar.com.ospim.liquidaciones.action;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.liquidaciones.WebKeysLiquidaciones;
import ar.com.ospim.liquidaciones.beans.BusquedaCartillaConvenioFiltro;
import ar.com.ospim.liquidaciones.beans.CartillaConvenioRow;
import ar.com.ospim.liquidaciones.services.CartillaConvenioServiceUtil;

import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.ActionConstants;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portlet.ActionResponseImpl;

public class CartillaConvenioPorPlanAction extends PortletAction {

    private final Logger _log = Logger.getLogger(this.getClass());

    private static final String CMD_SEARCH = "search";
    private static final String CMD_EXPORT_XLS = "exportCartillaXls";

    @Override
    public void processAction(ActionMapping mapping, ActionForm form,
                              PortletConfig portletConfig, ActionRequest actionRequest,
                              ActionResponse actionResponse) throws Exception {

        String cmd = ParamUtil.getString(actionRequest, Constants.CMD, null);

        if (CMD_EXPORT_XLS.equals(cmd)) {
            exportarXls(actionRequest, actionResponse);
        }
    }

    @Override
    public ActionForward render(ActionMapping mapping, ActionForm form,
                                PortletConfig portletConfig, RenderRequest renderRequest,
                                RenderResponse renderResponse) throws Exception {

        HttpSession session = PortalUtil.getHttpServletRequest(renderRequest).getSession();

        cargarListas(session);

        String cmd = ParamUtil.getString(renderRequest, Constants.CMD, null);

        if (CMD_SEARCH.equalsIgnoreCase(cmd)) {
            BusquedaCartillaConvenioFiltro filtro = getFiltroFromRequest(renderRequest);
            List<CartillaConvenioRow> resultados = new ArrayList<CartillaConvenioRow>();

            if (filtro.getIdPlan() != null && filtro.getIdPlan().intValue() > 0) {
                resultados = CartillaConvenioServiceUtil.buscarCartillaConvenioPorPlan(filtro);
            }

            session.setAttribute(WebKeysLiquidaciones.BUSQUEDA_CARTILLA_CONVENIO_FILTRO, filtro);
            session.setAttribute(WebKeysLiquidaciones.BUSQUEDA_CARTILLA_CONVENIO_RESULTS, resultados);

            renderRequest.setAttribute(WebKeysLiquidaciones.BUSQUEDA_CARTILLA_CONVENIO_FILTRO, filtro);
            renderRequest.setAttribute(WebKeysLiquidaciones.BUSQUEDA_CARTILLA_CONVENIO_RESULTS, resultados);

            _log.debug("[CARTILLA-CONV][RENDER][SEARCH] resultados=" + resultados.size());
        } else {
            Object filtroSession = session.getAttribute(WebKeysLiquidaciones.BUSQUEDA_CARTILLA_CONVENIO_FILTRO);
            Object resultadosSession = session.getAttribute(WebKeysLiquidaciones.BUSQUEDA_CARTILLA_CONVENIO_RESULTS);

            if (filtroSession != null) {
                renderRequest.setAttribute(WebKeysLiquidaciones.BUSQUEDA_CARTILLA_CONVENIO_FILTRO, filtroSession);
            } else {
                renderRequest.setAttribute(WebKeysLiquidaciones.BUSQUEDA_CARTILLA_CONVENIO_FILTRO,
                        new BusquedaCartillaConvenioFiltro());
            }

            if (resultadosSession != null) {
                renderRequest.setAttribute(WebKeysLiquidaciones.BUSQUEDA_CARTILLA_CONVENIO_RESULTS, resultadosSession);
            } else {
                renderRequest.setAttribute(WebKeysLiquidaciones.BUSQUEDA_CARTILLA_CONVENIO_RESULTS,
                        new ArrayList<CartillaConvenioRow>());
            }
        }

        return mapping.findForward(getForward(renderRequest, "portlet.liquidaciones.cartilla_convenio_por_plan"));
    }

    private BusquedaCartillaConvenioFiltro getFiltroFromRequest(RenderRequest request) {
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

        filtro.setPrestadorDescripcion(ParamUtil.getString(request, "prestadorDescripcion", null));
        filtro.setInstitucion(ParamUtil.getString(request, "institucion", null));
        filtro.setIncluyeBajas(ParamUtil.getBoolean(request, "incluyeBajas", false));
        filtro.setPagina(ParamUtil.getInteger(request, "pagina", 1));

        return filtro;
    }

    private void cargarListas(HttpSession session) throws Exception {

        if (session.getAttribute(WebKeysLiquidaciones.PLANES_EN_SESSION) == null) {
            session.setAttribute(WebKeysLiquidaciones.PLANES_EN_SESSION,
                    TraeListasServiceUtil.getPlanesOspim());
        }

        if (session.getAttribute(WebKeysLiquidaciones.PRESTADORES_EN_SESSION) == null) {
            session.setAttribute(WebKeysLiquidaciones.PRESTADORES_EN_SESSION,
                    TraeListasServiceUtil.getPrestadores());
        }

        if (session.getAttribute(WebKeysLiquidaciones.PROVINCIAS_EN_SESSION) == null) {
            session.setAttribute(WebKeysLiquidaciones.PROVINCIAS_EN_SESSION,
                    TraeListasServiceUtil.getProvincias());
        }

        if (session.getAttribute(WebKeysLiquidaciones.LOCALIDADES_EN_SESSION) == null) {
            session.setAttribute(WebKeysLiquidaciones.LOCALIDADES_EN_SESSION,
                    TraeListasServiceUtil.getLocalidades());
        }

        if (session.getAttribute(WebKeysLiquidaciones.LISTAS_DE_ESPECIALIDAD_PRESTADOR_EN_SESSION) == null) {
            session.setAttribute(WebKeysLiquidaciones.LISTAS_DE_ESPECIALIDAD_PRESTADOR_EN_SESSION,
                    TraeListasServiceUtil.getEspecialidadesPrestador());
        }
    }

    @SuppressWarnings("unchecked")
    private void exportarXls(ActionRequest actionRequest, ActionResponse actionResponse) throws Exception {
        HttpSession session = PortalUtil.getHttpServletRequest(actionRequest).getSession();

        List<CartillaConvenioRow> resultados =
                (List<CartillaConvenioRow>) session.getAttribute(WebKeysLiquidaciones.BUSQUEDA_CARTILLA_CONVENIO_RESULTS);

        if (resultados == null) {
            resultados = new ArrayList<CartillaConvenioRow>();
        }

        HSSFWorkbook workbook = null;
        OutputStream outputStream = null;

        try {
            workbook = crearWorkbook(resultados);

            javax.servlet.http.HttpServletResponse httpRes =
                    ((ActionResponseImpl) actionResponse).getHttpServletResponse();

            httpRes.reset();
            httpRes.setContentType("application/vnd.ms-excel");
            httpRes.setHeader("Content-Disposition", "attachment; filename=\"cartilla_convenio_por_plan.xls\"");
            httpRes.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
            httpRes.setHeader("Pragma", "public");

            outputStream = httpRes.getOutputStream();
            workbook.write(outputStream);
            outputStream.flush();

            setForward(actionRequest, ActionConstants.COMMON_NULL);
        } finally {
            if (outputStream != null) {
                try { outputStream.close(); } catch (Exception e) { _log.warn(e); }
            }
            if (workbook != null) {
                try { workbook.close(); } catch (Exception e) { _log.warn(e); }
            }
        }
    }

    private HSSFWorkbook crearWorkbook(List<CartillaConvenioRow> resultados) {
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("cartilla_convenio");

        int rowNum = 0;
        Row header = sheet.createRow(rowNum++);

        header.createCell(0).setCellValue("plan");
        header.createCell(1).setCellValue("prestador");
        header.createCell(2).setCellValue("cuit");
        header.createCell(3).setCellValue("zona");
        header.createCell(4).setCellValue("especialidad");
        header.createCell(5).setCellValue("institucion");
        header.createCell(6).setCellValue("domicilio");
        header.createCell(7).setCellValue("telefono");
        header.createCell(8).setCellValue("localidad");
        header.createCell(9).setCellValue("provincia");

        for (CartillaConvenioRow rowData : resultados) {
            Row row = sheet.createRow(rowNum++);

            row.createCell(0).setCellValue(safe(rowData.getPlanDescripcion()));
            row.createCell(1).setCellValue(safe(rowData.getPrestadorDescripcion()));
            row.createCell(2).setCellValue(safe(rowData.getCuitPrestador()));
            row.createCell(3).setCellValue(safe(rowData.getZona()));
            row.createCell(4).setCellValue(safe(rowData.getEspecialidad()));
            row.createCell(5).setCellValue(safe(rowData.getInstitucion()));
            row.createCell(6).setCellValue(safe(rowData.getDomicilio()));
            row.createCell(7).setCellValue(safe(rowData.getTelefono()));
            row.createCell(8).setCellValue(safe(rowData.getLocalidad()));
            row.createCell(9).setCellValue(safe(rowData.getProvincia()));
        }

        for (int i = 0; i <= 9; i++) {
            sheet.autoSizeColumn(i);
        }

        return workbook;
    }

    private String safe(String value) {
        return value != null ? value : "";
    }
}