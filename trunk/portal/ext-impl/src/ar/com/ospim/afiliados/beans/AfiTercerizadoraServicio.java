package ar.com.ospim.afiliados.beans;

import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import ar.com.ospim.afiliados.beans.AfiPlan.ESTADOS;
import ar.com.ospim.util.DateUtils;

/**
 * @author Administrador
 * @version 1.0
 * @created 29-Jul-2010 11:34:23 a.m.
 */
public class AfiTercerizadoraServicio {
	
	private TercerizadoraServicio tercerizadora;
	private Date fechaInicioPres;
	private Date fechaFinPres;
	private Afiliado afiliado;
	private Date altaFecha;
	private String altaUsr;
	private Date modiFecha;
	private String modiUsr;
	private Date bajaFecha;
	private String bajaUsr;
	private BigInteger id;
	
	private ESTADOS estado; 
	
	public enum ESTADOS {
		ALTA, MODIFICADO, BAJA
	};
//	transient private boolean borradoLogico = false;
//	transient private boolean nuevo = false;
	

	// la fecha de ingreso es pk, entonces si la cambian necesito guardar el
	// original para poder hacer el update
	transient private Date fechaInicioPresEditada;
		
	
	public AfiTercerizadoraServicio() {
	}

	public AfiTercerizadoraServicio(String id_tercerizadora) {
		this.tercerizadora = new TercerizadoraServicio(id_tercerizadora, null);
	}

	public AfiTercerizadoraServicio(String id_tercerizadora,
			String descripTerc, Date fechaInicioPres, Date fechaFinPres) {
		this.tercerizadora = new TercerizadoraServicio(id_tercerizadora,
				descripTerc);
		this.fechaInicioPres = fechaInicioPres;
		this.fechaFinPres = fechaFinPres;
	}

	public Date getFechaInicioPres() {
		return fechaInicioPres;
	}

	public void setFechaInicioPres(Date fechaInicioPres) {
		this.fechaInicioPres = fechaInicioPres;
	}

	public Date getFechaFinPres() {
		return fechaFinPres;
	}

	public void setFechaFinPres(Date fechaFinPres) {
		this.fechaFinPres = fechaFinPres;
	}

	public TercerizadoraServicio getTercerizadora() {
		return tercerizadora;
	}

	public void setTercerizadora(TercerizadoraServicio tercerizadora) {
		this.tercerizadora = tercerizadora;
	}

	public Afiliado getAfiliado() {
		return afiliado;
	}

	public void setAfiliado(Afiliado afiliado) {
		this.afiliado = afiliado;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((fechaFinPres == null) ? 0 : fechaFinPres.hashCode());
		result = prime * result
				+ ((fechaInicioPres == null) ? 0 : fechaInicioPres.hashCode());
		result = prime * result
				+ ((tercerizadora == null) ? 0 : tercerizadora.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AfiTercerizadoraServicio other = (AfiTercerizadoraServicio) obj;
		if (fechaFinPres == null) {
			if (other.fechaFinPres != null)
				return false;
		} else if (!fechaFinPres.equals(other.fechaFinPres))
			return false;
		if (fechaInicioPres == null) {
			if (other.fechaInicioPres != null)
				return false;
		} else if (!fechaInicioPres.equals(other.fechaInicioPres))
			return false;
		if (tercerizadora == null) {
			if (other.tercerizadora != null)
				return false;
		} else if (!tercerizadora.equals(other.tercerizadora))
			return false;
		return true;
	}

//	public boolean isBorradoLogico() {
//		return borradoLogico;
//	}
//
//	public void setBorradoLogico(boolean borradoLogico) {
//		this.borradoLogico = borradoLogico;
//	}

//	public boolean isNuevo() {
//		return nuevo;
//	}
//
//	public void setNuevo(boolean nuevo) {
//		this.nuevo = nuevo;
//	}

	public Date getFechaInicioPresEditada() {
		return fechaInicioPresEditada;
	}

	public void setFechaInicioPreseditada(Date fecha_inicio_editada) {
		this.fechaInicioPresEditada = fecha_inicio_editada;
	}

	public String getFechaInicioPresOriginalAsString() {
		Date ingre = fechaInicioPres;
		if (fechaInicioPresEditada != null) {
			ingre = fechaInicioPresEditada;
		}
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		return format.format(ingre);
	}

	public Date getFechaInicioPresOriginal() {
		Date ingre = fechaInicioPres;
		if (fechaInicioPresEditada != null) {
			ingre = fechaInicioPresEditada;
		}
		return ingre;
	}
	
	public Date getAltaFecha() {
		return altaFecha;
	}

	public void setAltaFecha(Date altaFecha) {
		this.altaFecha = altaFecha;
	}

	public String getAltaUsr() {
		return altaUsr;
	}

	public void setAltaUsr(String altaUsr) {
		this.altaUsr = altaUsr;
	}

	public Date getModiFecha() {
		return modiFecha;
	}

	public void setModiFecha(Date modiFecha) {
		this.modiFecha = modiFecha;
	}

	public String getModiUsr() {
		return modiUsr;
	}

	public void setModiUsr(String modiUsr) {
		this.modiUsr = modiUsr;
	}

	public Date getBajaFecha() {
		return bajaFecha;
	}

	public void setBajaFecha(Date bajaFecha) {
		this.bajaFecha = bajaFecha;
	}

	public String getBajaUsr() {
		return bajaUsr;
	}

	public void setBajaUsr(String bajaUsr) {
		this.bajaUsr = bajaUsr;
	}

	public static AfiTercerizadoraServicio getMapping(String prefix, ResultSet rs) throws SQLException {
		
		AfiTercerizadoraServicio ats = new AfiTercerizadoraServicio();
		
		ats.setAltaFecha(rs.getTimestamp(prefix + "alta_fecha"));
		ats.setAltaUsr(rs.getString(prefix + "alta_usr"));
		ats.setBajaFecha(rs.getTimestamp(prefix + "baja_fecha"));
		ats.setBajaUsr(rs.getString(prefix + "baja_usr"));
		ats.setModiFecha(rs.getTimestamp(prefix + "modi_fecha"));
		ats.setModiUsr(rs.getString(prefix + "modi_usr"));
		ats.setFechaInicioPres(rs.getDate(prefix + "fecha_inicio_pres"));
		ats.setFechaFinPres(rs.getDate(prefix + "fecha_fin_pres"));
		ats.setTercerizadora(new TercerizadoraServicio(rs.getString(prefix + "id_tercerizadora"), rs.getString(prefix + "descripcion")));
		try{
			ats.setId(BigInteger.valueOf(rs.getLong(prefix+"id")));
		}catch (Exception e) {
//			nada
		}
		
		return ats;
	}

	public BigInteger getId() {
		return id;
	}

	public void setId(BigInteger id) {
		this.id = id;
	}

	@Override
	public String toString() {
		String result = "";
		
		if(tercerizadora!=null){
			result += tercerizadora.getDescripcion()!=null?tercerizadora.getDescripcion():tercerizadora.getId_tercerizadora();
		}
		result += " Fecha Inicio Prestación = " + DateUtils.format(fechaInicioPres, DateUtils.SHORT);
		if(fechaFinPres != null){
			result += " Fecha Fin Prestación = " + DateUtils.format(fechaFinPres, DateUtils.SHORT);
		}else{
			result += " Fecha Fin Prestación no definida";
		}
		return result;
	}

	public ESTADOS getEstado() {
		return estado;
	}

	public void setEstado(ESTADOS estado) {
		this.estado = estado;
	}
	
	
}