package ar.com.ospim.liquidaciones.beans;

/**
 * @author sistema-09
 * @version 1.0
 * @created 13-Sep-2010 04:29:47 p.m.
 */
public class PrestacionPrestadorPlan {

	private int id_prestacion;
	private int id_prestador;
	private int id_domicilio;
	private int id_plan;
	private int importe;
	private PrestacionPrestador prestacionPrestador;

	public PrestacionPrestadorPlan(){

	}

	/**
	 * @return the id_prestacion
	 */
	public int getId_prestacion() {
		return id_prestacion;
	}

	/**
	 * @param idPrestacion the id_prestacion to set
	 */
	public void setId_prestacion(int idPrestacion) {
		id_prestacion = idPrestacion;
	}

	/**
	 * @return the id_prestador
	 */
	public int getId_prestador() {
		return id_prestador;
	}

	/**
	 * @param idPrestador the id_prestador to set
	 */
	public void setId_prestador(int idPrestador) {
		id_prestador = idPrestador;
	}

	/**
	 * @return the id_domicilio
	 */
	public int getId_domicilio() {
		return id_domicilio;
	}

	/**
	 * @param idDomicilio the id_domicilio to set
	 */
	public void setId_domicilio(int idDomicilio) {
		id_domicilio = idDomicilio;
	}

	/**
	 * @return the id_plan
	 */
	public int getId_plan() {
		return id_plan;
	}

	/**
	 * @param idPlan the id_plan to set
	 */
	public void setId_plan(int idPlan) {
		id_plan = idPlan;
	}

	/**
	 * @return the importe
	 */
	public int getImporte() {
		return importe;
	}

	/**
	 * @param importe the importe to set
	 */
	public void setImporte(int importe) {
		this.importe = importe;
	}

	/**
	 * @return the prestacionPrestador
	 */
	public PrestacionPrestador getPrestacionPrestador() {
		return prestacionPrestador;
	}

	/**
	 * @param prestacionPrestador the prestacionPrestador to set
	 */
	public void setPrestacionPrestador(PrestacionPrestador prestacionPrestador) {
		this.prestacionPrestador = prestacionPrestador;
	}
}