package ar.com.ospim.autorizaciones.action;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import ar.com.ospim.automatico.AgendadoJava;
import ar.com.ospim.automatico.beans.ReporteAutomatico;
import ar.com.ospim.util.ConnectionHelper;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

public class FinalizarAutorizacionesVencidasAutomaticas extends AgendadoJava {

    private static Log _log = LogFactoryUtil.getLog(FinalizarAutorizacionesVencidasAutomaticas.class);

    @Override
    public void correrAgendado(ReporteAutomatico ra) {
        Connection con = null;
        CallableStatement stmt = null;

        try {
            _log.info("Arranca job FinalizarAutorizacionesVencidas. Reporte=" + ra.getId() + " - " + ra.getTitulo());

            con = ConnectionHelper.getConnection();

            String sql = "SELECT autorizaciones.finalizar_autorizaciones_vencidas()";
            stmt = con.prepareCall(sql);

            ResultSet rs = stmt.executeQuery();
            int cant = 0;
            if (rs.next()) {
                cant = rs.getInt(1);
            }

            _log.info("Fin job FinalizarAutorizacionesVencidas. Finalizadas=" + cant);

        } catch (Exception e) {
            _log.error("Error ejecutando finalizar_autorizaciones_vencidas", e);
            throw new RuntimeException(e);
        } finally {
            ConnectionHelper.cerrar(stmt, con);
        }
    }


    @Override
    public HSSFWorkbook getResultados() {
        return null;
    }
}
