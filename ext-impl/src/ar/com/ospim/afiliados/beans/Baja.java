package ar.com.ospim.afiliados.beans;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import ar.com.ospim.global.beans.Plan;

public class Baja {
	private String cuil;
	private String dni;
	private String parentesco;
	private String apellido;
	private String nombre;
	private Date alta_fecha;
	private Date baja_fecha;
	private Plan ultimo_plan;
	private String tipo_de_baja;

	public Baja(String cuil, String dni, String parentesco, String apellido,
			String nombre, Date altaFecha, Date bajaFecha, String tipoDeBaja) {
		super();
		this.cuil = cuil;
		this.dni = dni;
		this.parentesco = parentesco;
		this.apellido = apellido;
		this.nombre = nombre;
		this.alta_fecha = altaFecha;
		this.baja_fecha = bajaFecha;
		this.tipo_de_baja = tipoDeBaja;
	}

	/**
	 * @return the cuil
	 */
	public String getCuil() {
		return cuil;
	}

	/**
	 * @param cuil
	 *            the cuil to set
	 */
	public void setCuil(String cuil) {
		this.cuil = cuil;
	}

	/**
	 * @return the dni
	 */
	public String getDni() {
		return dni;
	}

	/**
	 * @param dni
	 *            the dni to set
	 */
	public void setDni(String dni) {
		this.dni = dni;
	}

	/**
	 * @return the parentesco
	 */
	public String getParentesco() {
		return parentesco;
	}

	/**
	 * @param parentesco
	 *            the parentesco to set
	 */
	public void setParentesco(String parentesco) {
		this.parentesco = parentesco;
	}

	/**
	 * @return the apellido
	 */
	public String getApellido() {
		return apellido;
	}

	/**
	 * @param apellido
	 *            the apellido to set
	 */
	public void setApellido(String apellido) {
		this.apellido = apellido;
	}

	/**
	 * @return the nombre
	 */
	public String getNombre() {
		return nombre;
	}

	/**
	 * @param nombre
	 *            the nombre to set
	 */
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	/**
	 * @return the alta_fecha
	 */
	public Date getAlta_fecha() {
		return alta_fecha;
	}

	/**
	 * @param altaFecha
	 *            the alta_fecha to set
	 */
	public void setAlta_fecha(Date altaFecha) {
		alta_fecha = altaFecha;
	}

	/**
	 * @return the baja_fecha
	 */
	public Date getBaja_fecha() {
		return baja_fecha;
	}

	/**
	 * @param bajaFecha
	 *            the baja_fecha to set
	 */
	public void setBaja_fecha(Date bajaFecha) {
		baja_fecha = bajaFecha;
	}

	/**
	 * @return the ultimo_plan
	 */
	public Plan getUltimo_plan() {
		return ultimo_plan;
	}

	/**
	 * @param ultimoPlan
	 *            the ultimo_plan to set
	 */
	public void setUltimo_plan(Plan ultimoPlan) {
		ultimo_plan = ultimoPlan;
	}

	/**
	 * @return the tipo_de_baja
	 */
	public String getTipo_de_baja() {
		return tipo_de_baja;
	}

	/**
	 * @param tipoDeBaja
	 *            the tipo_de_baja to set
	 */
	public void setTipo_de_baja(String tipoDeBaja) {
		tipo_de_baja = tipoDeBaja;
	}

	public static Baja getMapping(ResultSet rs) {
		Baja baja = null;
		try {
			baja = new Baja(rs.getString("cuil"), rs.getString("dni"), rs
					.getString("parentesco"), rs.getString("apellido"), rs
					.getString("nombre"), rs.getDate("alta_fecha"), rs
					.getDate("baja_fecha"), rs.getString("tipo_baja"));
			Plan ultimoPlan = new Plan();
			ultimoPlan.setId(rs.getInt("id_plan"));
			ultimoPlan.setDescripcion(rs.getString("plan"));
			baja.setUltimo_plan(ultimoPlan);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return baja;
	}
}