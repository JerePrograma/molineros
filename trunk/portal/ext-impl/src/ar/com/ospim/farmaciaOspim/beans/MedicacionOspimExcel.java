package ar.com.ospim.farmaciaOspim.beans;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import ar.com.ospim.afiliados.beans.Afiliado;

import ar.com.ospim.autorizaciones.reportes.action.ReporteSituacionMedica;
import ar.com.ospim.farmacia.beans.Medicamento;


public class MedicacionOspimExcel extends Medicamento implements Comparable<Medicamento> {

	
	private static final long serialVersionUID = 1L;

	// campos excel equipo situacion medica 
	


	private boolean presentacionActivaMedicamento;
	
	private String tipoventaMedicamento;	
			 
     
	private static Log _log = LogFactoryUtil
			.getLog(ReporteSituacionMedica.class);
	
	
	public MedicacionOspimExcel () {
		super();
	}

	public static MedicacionOspimExcel   getMappingReporte(ResultSet rs) throws SQLException {
		MedicacionOspimExcel   medicamento = new MedicacionOspimExcel   ();
		
		try {
			medicamento.setAccion(rs.getString("rpt_med_accion"));  
			medicamento.setFecha(rs.getDate("rpt_med_fecha"));
			medicamento.setPeriodo(rs.getDate("rpt_med_periodo"));
			medicamento.setRegistro(rs.getInt("rpt_med_registro"));
			medicamento.setTroquel(rs.getInt("rpt_med_troquel"));
			medicamento.setNombre(rs.getString("rpt_med_nombre"));
			medicamento.setPresentacion(rs.getString("rpt_med_pesentacion"));
			medicamento.setLaboratorio(rs.getString("rpt_med_laboratorio"));
			medicamento.setPrecio(rs.getBigDecimal("rpt_med_precio"));
			medicamento.setPresentacionActivaMedicamento(rs.getBoolean ("rpt_med_activa"));
			medicamento.setCod_barra(rs.getString("rpt_med_codebar"));
			medicamento.setAccion(rs.getString("rpt_med_accion"));
			medicamento.setDroga(rs.getString("rpt_med_droga"));
			medicamento.setTipoventaMedicamento(rs.getString("rpt_med_tipoventamedicamento"));
			medicamento.setId_medicamento(rs.getInt("rpt_med_id_medicamento"));
			medicamento.setFecha_baja(rs.getDate("rpt_med_fecha_baja"));
			medicamento.setManualDat(rs.getBoolean("rpt_med_manual_dat")); 
			
		} catch (Exception e) {
			_log.error(
					"Error al generar reporte de situacion medica",e);
			return null;
		}
		
		return medicamento;
	}

	public boolean isPresentacionActivaMedicamento() {
		return presentacionActivaMedicamento;
	}

	public void setPresentacionActivaMedicamento(boolean presentacionActivaMedicamento) {
		this.presentacionActivaMedicamento = presentacionActivaMedicamento;
	}

	public String getTipoventaMedicamento() {
		return tipoventaMedicamento;
	}

	public void setTipoventaMedicamento(String tipoventaMedicamento) {
		this.tipoventaMedicamento = tipoventaMedicamento;
	}
	

	
		
	
}
