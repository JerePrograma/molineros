package ar.com.ospim.tesoreria.reportes;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import ar.com.ospim.util.ConnectionHelper;

public class ReportePadronesPPP {

    private static Log _log = LogFactoryUtil.getLog(ReportePadronesPPP.class);

    public static void generarCSV(HttpServletRequest req, HttpServletResponse res) throws Exception {

        Connection con = null;
        CallableStatement stmt = null;
        ResultSet rs = null;

        try {

            Calendar fechaHasta = Calendar.getInstance();
            Calendar fechaDesde = (Calendar) fechaHasta.clone();

            fechaDesde.add(Calendar.DAY_OF_MONTH, -30);

            SimpleDateFormat formato =
                    new SimpleDateFormat("yyyyMMdd");

            String hasta = formato.format(fechaHasta.getTime());
            String desde = formato.format(fechaDesde.getTime());

            _log.info("Generando Padrones PPP desde " + desde + " hasta " + hasta);

            con = ConnectionHelper.getConnection();
            String sql = "{call reporte_padrones_ppp(?, ?)}";
            stmt = con.prepareCall(sql);
            stmt.setDate(1, new java.sql.Date(fechaDesde.getTimeInMillis()));
            stmt.setDate(2, new java.sql.Date(fechaHasta.getTimeInMillis()));

            rs = stmt.executeQuery();
            
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            PrintWriter writer = new PrintWriter(new OutputStreamWriter(baos, "UTF-8"));

            while (rs.next()) {

                String documento = rs.getString("docu_numero");
                String afiliado = rs.getString("afiliado");
                String bajaFecha = rs.getString("baja_fecha");

                writer.print(documento != null ? documento : "");
                writer.print(",");
                writer.print("\"");
                writer.print(afiliado != null ? afiliado.replace("\"", "\"\"") : "");
                writer.print("\"");
                writer.print(",");
                writer.print("\"");
                writer.print(bajaFecha != null ? bajaFecha.replace("\"", "\"\"") : "");
                writer.println("\"");
            }

            writer.flush();

            String descargaId = req.getParameter("descargaId");

            if (descargaId != null && descargaId.matches("[0-9]{1,20}")) {

                Cookie cookie = new Cookie("descargaPPP", descargaId);
                cookie.setPath("/");
                cookie.setMaxAge(60);

                res.addCookie(cookie);
            }
            
            res.setContentType("text/csv");
            res.setCharacterEncoding("UTF-8");
            res.setHeader("Content-Disposition", "attachment; filename=\"Padron_ppp_" + hasta + ".csv\"");
            res.setContentLength(baos.size());
            res.getOutputStream().write(baos.toByteArray());
            res.getOutputStream().flush();

        } catch (Exception e) {
            _log.error("Error generando CSV Padrones PPP", e);
            throw e;

        } finally {

            if (rs != null) {
                try {
                    rs.close();
                } catch (Exception e) {
                    _log.error("Error cerrando ResultSet PPP", e);
                }
            }

            ConnectionHelper.cerrar(stmt, con);
        }
    }
    
}