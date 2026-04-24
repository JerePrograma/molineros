package ar.com.ospim.autorizaciones.beans;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ar.com.ospim.farmacia.beans.Medicamento;
import ar.com.ospim.liquidaciones.beans.LiquidacionPrestacion;
import ar.com.ospim.liquidaciones.beans.Prestador;
import ar.com.ospim.liquidaciones.beans.TratamientoDiscapacidad;

public class DrogaPatologia implements Serializable{
	
	private static final long serialVersionUID = 8425734311063680075L;
	
	private Integer id;
	private Integer drogaId;
	private String  drogaDescripcion;
	private String patologia;
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getDrogaId() {
		return drogaId;
	}

	public void setDrogaId(Integer drogaId) {
		this.drogaId = drogaId;
	}

	public String getDrogaDescripcion() {
		return drogaDescripcion;
	}

	public void setDrogaDescripcion(String drogaDescripcion) {
		this.drogaDescripcion = drogaDescripcion;
	}

	
	public String getPatologia() {
		return patologia;
	}

	public void setPatologia(String patologia) {
		this.patologia = patologia;
	}
	
	public String getDescripcion() {
		return patologia;
	}


	public static DrogaPatologia getMapping(ResultSet rs) throws SQLException {
		
		DrogaPatologia archivo = new DrogaPatologia();
		archivo.setId(rs.getInt("id"));
		archivo.setDrogaId(rs.getInt("id_droga"));
		archivo.setDrogaDescripcion(rs.getString("descripcion"));
		archivo.setPatologia(rs.getString("patologia"));
		return archivo;
		
	}
	
}
