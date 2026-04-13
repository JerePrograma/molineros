package ar.com.ospim.afiliados.reportes.beans;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import com.liferay.ibm.icu.text.SimpleDateFormat;

public class UltimosProcesosSisOld {

	private Date fecha_proceso;
	
	public static UltimosProcesosSisOld getMapping(ResultSet rs) throws SQLException {
		UltimosProcesosSisOld archivo = new UltimosProcesosSisOld();
		archivo.setFecha_proceso(rs.getDate("fecha_proceso"));
		return archivo;
	}

	public final Date getFecha_proceso() {
		return fecha_proceso;
	}

	public String getFecha_procesoString() {
		SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");
		return sdf.format(fecha_proceso);
	}

	public final void setFecha_proceso(Date fecha_proceso) {
		this.fecha_proceso = fecha_proceso;
	}
	
}
