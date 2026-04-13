package ar.com.ospim.afiliados.beans;

import java.sql.Timestamp;

import ar.com.ospim.util.DateUtils;
import ar.com.ospim.util.StringUtils;

public class HistoricoMovimientoAfiliado {
	private String cuil_titular;
	private int inte;
	private String parentesco;
	private String nro_documento;
	private String apellido;
	private String nombre;
	private String modificacion;
	private String valor_anterior;
	private String valor_actual;
	private String usuario;
	private Timestamp fecha_modificacion;
	private String discapacitado;

	/**
	 * @return the cuil_titular
	 */
	public String getCuil_titular() {
		return cuil_titular;
	}

	public String getCuil_titularMasked() {
		return StringUtils.getCuilMask(cuil_titular);
	}

	/**
	 * @param cuilTitular
	 *            the cuil_titular to set
	 */
	public void setCuil_titular(String cuilTitular) {
		cuil_titular = cuilTitular;
	}

	/**
	 * @return the inte
	 */
	public int getInte() {
		return inte;
	}

	public String getInteAsString() {
		return String.valueOf(inte);
	}

	/**
	 * @param inte
	 *            the inte to set
	 */
	public void setInte(int inte) {
		this.inte = inte;
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
	 * @return the nro_documento
	 */
	public String getNro_documento() {
		return nro_documento;
	}

	/**
	 * @param nroDocumento
	 *            the nro_documento to set
	 */
	public void setNro_documento(String nroDocumento) {
		nro_documento = nroDocumento;
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
	 * @return the modificacion
	 */
	public String getModificacion() {
		return modificacion;
	}

	/**
	 * @param modificacion
	 *            the modificacion to set
	 */
	public void setModificacion(String modificacion) {
		this.modificacion = modificacion;
	}

	/**
	 * @return the valor_anterior
	 */
	public String getValor_anterior() {
		return valor_anterior;
	}

	/**
	 * @param valorAnterior
	 *            the valor_anterior to set
	 */
	public void setValor_anterior(String valorAnterior) {
		valor_anterior = valorAnterior;
	}

	/**
	 * @return the valor_actual
	 */
	public String getValor_actual() {
		return valor_actual;
	}

	/**
	 * @param valorActual
	 *            the valor_actual to set
	 */
	public void setValor_actual(String valorActual) {
		valor_actual = valorActual;
	}

	/**
	 * @return the usuario
	 */
	public String getUsuario() {
		return usuario;
	}

	/**
	 * @param usuario
	 *            the usuario to set
	 */
	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	/**
	 * @return the fecha_modificacion
	 */
	public Timestamp getFecha_modificacion() {
		return fecha_modificacion;
	}

	public String getFecha_modificacionAsString() {
		return null != fecha_modificacion ? DateUtils.format(
				fecha_modificacion, DateUtils.SHORT) : "";
	}

	/**
	 * @param fechaModificacion
	 *            the fecha_modificacion to set
	 */
	public void setFecha_modificacion(Timestamp fechaModificacion) {
		fecha_modificacion = fechaModificacion;
	}

	public String getDiscapacitado() {
		return discapacitado;
	}

	public void setDiscapacitado(String discapacitado) {
		this.discapacitado = discapacitado;
	}
	
	
}