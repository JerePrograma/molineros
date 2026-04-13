package ar.com.ospim.autorizaciones.beans;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.liferay.portal.SystemException;
import com.liferay.portlet.documentlibrary.model.impl.DLFileEntryImpl;

import ar.com.ospim.afiliados.beans.AfiPlan;
import ar.com.ospim.afiliados.beans.Afiliado;
import ar.com.ospim.autorizaciones.services.NomencladorServiceUtil;
import ar.com.ospim.autorizaciones.services.PreAutorizacionServiceUtil;
import ar.com.ospim.global.beans.ClaseBase;
import ar.com.ospim.global.beans.Plan;
import ar.com.ospim.global.beans.Seccional;

public class EstadisticaGastoMedico implements Serializable{

		
	private static final long serialVersionUID = 3624663072458885308L;
	private String periodo;
	private String tipo;
	private String descripcion;
	private Double total;
	private Double porcentaje;
	
	public String getPeriodo() {
		return periodo;
	}

	public void setPeriodo(String periodo) {
		this.periodo = periodo;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public Double getTotal() {
		return total;
	}

	public void setTotal(Double total) {
		this.total = total;
	}

	public Double getPorcentaje() {
		return porcentaje;
	}

	public void setPorcentaje(Double porcentaje) {
		this.porcentaje = porcentaje;
	}

	public static EstadisticaGastoMedico getMapping(ResultSet rs) throws SQLException {
		
		EstadisticaGastoMedico gasto = new EstadisticaGastoMedico();
		
		gasto.setDescripcion(rs.getString("descripcion"));
		gasto.setPeriodo(rs.getString("periodo"));
		gasto.setPorcentaje(rs.getDouble("porcentaje"));
		gasto.setTipo(rs.getString("tipo"));
		gasto.setTotal(rs.getDouble("total"));
		
				
		return gasto;
	}
	
		
}

