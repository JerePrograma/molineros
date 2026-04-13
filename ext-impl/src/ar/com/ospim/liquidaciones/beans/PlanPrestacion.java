package ar.com.ospim.liquidaciones.beans;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

import ar.com.ospim.global.beans.Prestacion;

/**
 * @author sistema-09
 * @version 1.0
 * @created 25-Ago-2010 02:25:36 p.m.
 */
public class PlanPrestacion {

	private int tope_cantidad;
	private BigDecimal tope_importe;
	private int tope_individ_cantidad;
	private BigDecimal tope_individ_importe;
	private Prestacion nomenclador;
	private Plan plan;

	public PlanPrestacion() {

	}

	/**
	 * @return the id_prestacion
	 */
	public int getId_prestacion() {
		if (nomenclador == null) {
			return 0;
		}
		return nomenclador.getId();
	}

	/**
	 * @param idPrestacion
	 *            the id_prestacion to set
	 */
	public void setId_prestacion(int idPrestacion) {
		if (nomenclador == null){
			nomenclador = new Prestacion();
		}
		nomenclador.setId_prestacion(idPrestacion);
	}
	
	/**
	 * @param idPrestacion
	 *            the id_prestacion to set
	 */
	public void setId_prestacion(int idPrestacion, String codigo) {
		if (nomenclador == null){
			nomenclador = new Prestacion();
		}
		nomenclador.setId_prestacion(idPrestacion);
		nomenclador.setCodigo(codigo);
	}

	/**
	 * @return the id_plan
	 */
	public int getId_plan() {
		if (plan == null) {
			return 0;
		}
		return plan.getId_plan();
	}

	/**
	 * @param idPlan
	 *            the id_plan to set
	 */
	public void setId_plan(int idPlan) {
		if (plan == null){
			plan = new Plan();
		}
		plan.setId_plan(idPlan);
	}

	/**
	 * @return the tope_cantidad
	 */
	public int getTope_cantidad() {
		return tope_cantidad;
	}

	/**
	 * @param topeCantidad
	 *            the tope_cantidad to set
	 */
	public void setTope_cantidad(int topeCantidad) {
		tope_cantidad = topeCantidad;
	}

	/**
	 * @return the tope_importe
	 */
	public BigDecimal getTope_importe() {
		return tope_importe;
	}

	/**
	 * @param topeImporte
	 *            the tope_importe to set
	 */
	public void setTope_importe(BigDecimal topeImporte) {
		tope_importe = topeImporte;
	}

	/**
	 * @return the tope_individ_cantidad
	 */
	public int getTope_individ_cantidad() {
		return tope_individ_cantidad;
	}

	/**
	 * @param topeIndividCantidad
	 *            the tope_individ_cantidad to set
	 */
	public void setTope_individ_cantidad(int topeIndividCantidad) {
		tope_individ_cantidad = topeIndividCantidad;
	}

	/**
	 * @return the tope_individ_importe
	 */
	public BigDecimal getTope_individ_importe() {
		return tope_individ_importe;
	}

	/**
	 * @param topeIndividImporte
	 *            the tope_individ_importe to set
	 */
	public void setTope_individ_importe(BigDecimal topeIndividImporte) {
		tope_individ_importe = topeIndividImporte;
	}

	/**
	 * @return the nomenclador
	 */
	public Prestacion getNomenclador() {
		return nomenclador;
	}

	/**
	 * @param nomenclador
	 *            the nomenclador to set
	 */
	public void setNomenclador(Prestacion nomenclador) {
		this.nomenclador = nomenclador;
	}

	/**
	 * @return the plan
	 */
	public Plan getPlan() {
		return plan;
	}

	/**
	 * @param plan
	 *            the plan to set
	 */
	public void setPlan(Plan plan) {
		this.plan = plan;
	}

	public static PlanPrestacion getMapping(ResultSet rs) throws SQLException {
		return getMapping(rs, "");
	}

	public static PlanPrestacion getMapping(ResultSet rs, String prefix)
			throws SQLException {
		PlanPrestacion planPrestacion = new PlanPrestacion();
		planPrestacion.setTope_cantidad(rs.getInt(prefix + "tope_cantidad"));
		planPrestacion.setTope_importe(rs
				.getBigDecimal(prefix + "tope_importe"));
		planPrestacion.setTope_individ_cantidad(rs.getInt(prefix
				+ "tope_individ_cantidad"));
		planPrestacion.setTope_individ_importe(rs.getBigDecimal(prefix
				+ "tope_individ_importe"));
		try {
			planPrestacion
					.setId_prestacion(rs.getInt(prefix + "id_prestacion"));
			planPrestacion.setId_plan(rs.getInt(prefix + "id_plan"));
		} catch (Exception e) {
			// DO NOTHING
		}
		return planPrestacion;
	}

}