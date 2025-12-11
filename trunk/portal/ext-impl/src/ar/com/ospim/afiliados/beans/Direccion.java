package ar.com.ospim.afiliados.beans;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Direccion {
	private String calle;
	private int cp;
	private int altura_inicio;
	private int altura_fin;

	/**
	 * @return the calle
	 */
	public String getCalle() {
		return calle;
	}

	/**
	 * @param calle
	 *            the calle to set
	 */
	public void setCalle(String calle) {
		this.calle = calle;
	}

	/**
	 * @return the cp
	 */
	public int getCp() {
		return cp;
	}

	/**
	 * @param cp
	 *            the cp to set
	 */
	public void setCp(int cp) {
		this.cp = cp;
	}

	/**
	 * @return the altura_inicio
	 */
	public int getAltura_inicio() {
		return altura_inicio;
	}

	/**
	 * @param alturaInicio
	 *            the altura_inicio to set
	 */
	public void setAltura_inicio(int alturaInicio) {
		this.altura_inicio = alturaInicio;
	}

	/**
	 * @return the altura_fin
	 */
	public int getAltura_fin() {
		return altura_fin;
	}

	/**
	 * @param alturaFin
	 *            the altura_fin to set
	 */
	public void setAltura_fin(int alturaFin) {
		this.altura_fin = alturaFin;
	}

	public static Direccion getMapping(ResultSet rs) throws SQLException {
		Direccion direccion = new Direccion();
		direccion.setCalle(rs.getString(1));
		return direccion;
	}	
	
	public static Direccion getMappingComplete(ResultSet rs) {
		Direccion direccion = new Direccion();
		try {
			direccion.setCalle(rs.getString("calle"));
			direccion.setCp(Integer.parseInt(rs.getString("cp")));
			direccion.setAltura_fin(Integer.parseInt(rs.getString("altura_fin")));
			direccion.setAltura_inicio(Integer.parseInt(rs.getString("altura_inicio")));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return direccion;
	}
}