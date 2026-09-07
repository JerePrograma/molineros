package ar.com.ospim.compras.requerimientos.reportes;

import ar.com.ospim.compras.requerimientos.beans.PrestadorCotizacion;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompra;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraReclamoPrestacional;
import ar.com.ospim.compras.requerimientos.helper.ExportarRequerimientosCompraHelper;
import ar.com.ospim.compras.requerimientos.helper.ExportarRequerimientosCompraHelper.Criterios;
import ar.com.ospim.compras.requerimientos.helper.RequerimientoCompraReclamoPrestacionalHelper;
import ar.com.ospim.compras.requerimientos.service.BusquedaRequerimientoCompraServiceUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.util.PortalUtil;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/** Descarga POI siguiendo XLSServlet y los reportes de Liquidaciones. */
public final class ReporteRequerimientosCompraExcel {

    private static final Log LOG = LogFactoryUtil.getLog(ReporteRequerimientosCompraExcel.class);

    public static void descargar(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        byte[] archivo;
        try {
            User user = PortalUtil.getUser(request);
            ExportarRequerimientosCompraHelper.validarPermiso(user);
            Criterios criterios = ExportarRequerimientosCompraHelper.obtener(
                    request.getSession(false), user.getUserId(),
                    request.getParameter(ExportarRequerimientosCompraHelper.TOKEN));
            List<RequerimientoCompra> requerimientos =
                    BusquedaRequerimientoCompraServiceUtil.buscarRequerimientosListado(
                            criterios.getFiltro(), criterios.incluirRp);
            List<Integer> ids = ExportarRequerimientosCompraHelper.obtenerIds(requerimientos);
            Map<Integer, RequerimientoCompraReclamoPrestacional> relaciones =
                    new RequerimientoCompraReclamoPrestacionalHelper()
                            .obtenerVinculadasPorRequerimientos(ids);
            Map<Integer, List<PrestadorCotizacion>> prestadores =
                    BusquedaRequerimientoCompraServiceUtil.listarPrestadoresAdjudicadosBatch(ids);
            archivo = generar(requerimientos, relaciones, prestadores,
                    criterios.mostrarRp, criterios.locale);
        } catch (SecurityException e) {
            error(response, HttpServletResponse.SC_FORBIDDEN, e.getMessage());
            return;
        } catch (IllegalArgumentException e) {
            error(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
            return;
        } catch (Exception e) {
            LOG.error("No se pudo exportar el listado de requerimientos de compras.", e);
            error(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "No se pudo generar la exportacion. Vuelva a intentar o consulte al administrador.");
            return;
        }

        // No enviar cabeceras Excel hasta completar consultas, validaciones y escritura POI.
        response.setHeader("Cache-Control", "no-store");
        response.setContentType(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=\"requerimientos.xlsx\"");
        response.setContentLength(archivo.length);
        OutputStream salida = response.getOutputStream();
        salida.write(archivo);
        salida.flush();
    }

    public static byte[] generar(List<RequerimientoCompra> requerimientos,
            Map<Integer, RequerimientoCompraReclamoPrestacional> relaciones,
            Map<Integer, List<PrestadorCotizacion>> prestadores,
            boolean mostrarRp, Locale locale) throws IOException {
        if (requerimientos.size() >= SpreadsheetVersion.EXCEL2007.getMaxRows()) {
            throw new IllegalArgumentException(
                    "El resultado supera el limite de filas de Excel. Acote la busqueda.");
        }
        XSSFWorkbook wb = new XSSFWorkbook();
        ByteArrayOutputStream salida = new ByteArrayOutputStream();
        try {
            Sheet hoja = wb.createSheet("Requerimientos");
            CellStyle encabezado = wb.createCellStyle();
            Font negrita = wb.createFont();
            negrita.setBold(true);
            encabezado.setFont(negrita);
            encabezado.setWrapText(true);
            String[] titulos = ExportarRequerimientosCompraHelper.titulosListado(mostrarRp);
            Row cabecera = hoja.createRow(0);
            for (int c = 0; c <= titulos.length; c++) {
                String titulo = c == titulos.length ? "Prestador adjudicado"
                        : LanguageUtil.get(locale, titulos[c]);
                texto(cabecera.createCell(c), titulo);
                cabecera.getCell(c).setCellStyle(encabezado);
                hoja.setColumnWidth(c, (c == 3 || c == titulos.length ? 42 : 20) * 256);
            }
            hoja.createFreezePane(0, 1);
            for (int i = 0; i < requerimientos.size(); i++) {
                RequerimientoCompra req = requerimientos.get(i);
                Integer id = Integer.valueOf(req.getIdRequerimientoCompra());
                String[] valores = ExportarRequerimientosCompraHelper.valoresListado(
                        req, relaciones.get(id), mostrarRp);
                Row fila = hoja.createRow(i + 1);
                for (int c = 0; c < valores.length; c++) {
                    texto(fila.createCell(c), valores[c]);
                }
                texto(fila.createCell(valores.length),
                        ExportarRequerimientosCompraHelper.obtenerPrestador(prestadores.get(id)));
            }
            wb.write(salida);
            return salida.toByteArray();
        } finally {
            wb.close();
            salida.close();
        }
    }

    private static void texto(Cell celda, String valor) {
        String texto = valor == null ? "" : valor;
        if (texto.length() > SpreadsheetVersion.EXCEL2007.getMaxTextLength()) {
            throw new IllegalArgumentException(
                    "Un valor supera el limite de texto de Excel. No se genero un archivo truncado.");
        }
        // setCellValue(String) conserva DNI, porcentajes y textos = + - @ sin formulas.
        celda.setCellValue(texto);
    }

    private static void error(HttpServletResponse response, int estado, String mensaje)
            throws IOException {
        response.setStatus(estado);
        response.setHeader("Cache-Control", "no-store");
        response.setContentType("text/plain; charset=ISO-8859-1");
        response.getWriter().write(mensaje);
    }

    private ReporteRequerimientosCompraExcel() {
    }
}
