package ar.com.ospim.global.beans;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PrestacionConcepto implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1274730989270448938L;
	
	private Prestacion prestacion;
	private BigDecimal coeficienteGastos;
	private BigDecimal coeficienteHonorarios;
	private int idTipoNomenclador;
	private String tipoNomenclador;

	private Concepto honorariosAmbulatorio;
	private Date validoDesdeHonorariosAmbulatorio;
	private Date validoHastaHonorariosAmbulatorio;
	private int idNomencladorConceptosHonorariosAmbulatorio;

	private Concepto honorariosInternacion;
	private Date validoDesdeHonorariosInternacion;
	private Date validoHastaHonorariosInternacion;
	private int idNomencladorConceptosHonorariosInternacion;

	private Concepto gastosAmbulatorio;
	private Date validoDesdeGastosAmbulatorio;
	private Date validoHastaGastosAmbulatorio;
	private int idNomencladorConceptosGastosAmbulatorio;

	private Concepto gastosInternacion;
	private Date validoDesdeGastosInternacion;
	private Date validoHastaGastosInternacion;
	private int idNomencladorConceptosGastosInternacion;

	public PrestacionConcepto() {
	}

	public Concepto getHonorariosAmbulatorio() {
		return honorariosAmbulatorio;
	}

	public void setHonorariosAmbulatorio(Concepto honorariosAmbulatorio) {
		this.honorariosAmbulatorio = honorariosAmbulatorio;
	}

	public Concepto getHonorariosInternacion() {
		return honorariosInternacion;
	}

	public void setHonorariosInternacion(Concepto honorariosInternacion) {
		this.honorariosInternacion = honorariosInternacion;
	}

	public Concepto getGastosAmbulatorio() {
		return gastosAmbulatorio;
	}

	public void setGastosAmbulatorio(Concepto gastosAmbulatorio) {
		this.gastosAmbulatorio = gastosAmbulatorio;
	}

	public Concepto getGastosInternacion() {
		return gastosInternacion;
	}

	public void setGastosInternacion(Concepto gastosInternacion) {
		this.gastosInternacion = gastosInternacion;
	}

	public Prestacion getPrestacion() {
		return prestacion;
	}

	public void setPrestacion(Prestacion prestacion) {
		this.prestacion = prestacion;
	}

	public static PrestacionConcepto getMapping(ResultSet rs)
			throws SQLException {
		PrestacionConcepto pc = new PrestacionConcepto();
		Prestacion prestacion = new Prestacion();
		Concepto honAmb = new Concepto();
		Concepto honInt = new Concepto();
		Concepto gasAmb = new Concepto();
		Concepto gasInt = new Concepto();

		prestacion.setCodigo(rs.getString("codigo"));
		prestacion.setId_prestacion(rs.getInt("id_prestacion"));
		prestacion.setDescripcion(rs.getString("descripcion"));
		prestacion.setMarca_rein_liq(rs.getInt("marca_rein_liq"));
		prestacion.setImporte(rs.getBigDecimal("importe"));
		BigDecimal gastos = rs.getBigDecimal("coef_gastos");
		if (gastos != null) {
			pc.setCoeficienteGastos(gastos.setScale(6));
		}
		BigDecimal honorarios = rs.getBigDecimal("coef_honorarios");
		if (honorarios != null) {
			pc.setCoeficienteHonorarios(honorarios.setScale(6));
		}
		
		pc.setIdTipoNomenclador(rs.getInt("id_tipo_nomenclador"));
		pc.setTipoNomenclador(rs.getString("tipo_nomenclador"));

		honAmb.setId(rs.getInt("id_honorarios_ambulatorio"));
		honAmb.setDescripcion(rs.getString("desc_honorarios_ambulatorio"));

		honInt.setId(rs.getInt("id_honorarios_internacion"));
		honInt.setDescripcion(rs.getString("desc_honorarios_internacion"));

		gasAmb.setId(rs.getInt("id_gastos_ambulatorio"));
		gasAmb.setDescripcion(rs.getString("desc_gastos_ambulatorio"));

		gasInt.setId(rs.getInt("id_gastos_internacion"));
		gasInt.setDescripcion(rs.getString("desc_gastos_internacion"));

		pc.setIdHonorariosAmbulatorio(rs.getInt("nomenclador_conceptos_ha_id"));
		pc.setValidoDesdeHonorariosAmbulatorio(rs.getDate("ha_valido_desde"));
		pc.setValidoHastaHonorariosAmbulatorio(rs.getDate("ha_valido_hasta"));

		pc.setIdHonorariosInternacion(rs.getInt("nomenclador_conceptos_hi_id"));
		pc.setValidoDesdeHonorariosInternacion(rs.getDate("hi_valido_desde"));
		pc.setValidoHastaHonorariosInternacion(rs.getDate("hi_valido_hasta"));

		pc.setIdGastosAmbulatorio(rs.getInt("nomenclador_conceptos_ga_id"));
		pc.setValidoDesdeGastosAmbulatorio(rs.getDate("ga_valido_desde"));
		pc.setValidoHastaGastosAmbulatorio(rs.getDate("ga_valido_hasta"));

		pc.setIdGastosInternacion(rs.getInt("nomenclador_conceptos_gi_id"));
		pc.setValidoDesdeGastosInternacion(rs.getDate("gi_valido_desde"));
		pc.setValidoHastaGastosInternacion(rs.getDate("gi_valido_hasta"));

		pc.setPrestacion(prestacion);
		pc.setHonorariosAmbulatorio(honAmb);
		pc.setHonorariosInternacion(honInt);
		pc.setGastosAmbulatorio(gasAmb);
		pc.setGastosInternacion(gasInt);
		return pc;
	}

	public Date getValidoDesdeHonorariosAmbulatorio() {
		return validoDesdeHonorariosAmbulatorio;
	}

	public String getValidoDesdeHonorariosAmbulatorioString() {
		if (validoDesdeHonorariosAmbulatorio == null) {
			return "";
		}
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		return format.format(validoDesdeHonorariosAmbulatorio);
	}

	public void setValidoDesdeHonorariosAmbulatorio(
			Date validoDesdeHonorariosAmbulatorio) {
		this.validoDesdeHonorariosAmbulatorio = validoDesdeHonorariosAmbulatorio;
	}

	public Date getValidoHastaHonorariosAmbulatorio() {
		return validoHastaHonorariosAmbulatorio;
	}

	public String getValidoHastaHonorariosAmbulatorioString() {
		if (validoHastaHonorariosAmbulatorio == null) {
			return "";
		}
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		return format.format(validoHastaHonorariosAmbulatorio);
	}

	public void setValidoHastaHonorariosAmbulatorio(
			Date validoHastaHonorariosAmbulatorio) {
		this.validoHastaHonorariosAmbulatorio = validoHastaHonorariosAmbulatorio;
	}

	public int getIdHonorariosAmbulatorio() {
		return idNomencladorConceptosHonorariosAmbulatorio;
	}

	public void setIdHonorariosAmbulatorio(int idHonorariosAmbulatorio) {
		this.idNomencladorConceptosHonorariosAmbulatorio = idHonorariosAmbulatorio;
	}

	public Date getValidoDesdeHonorariosInternacion() {
		return validoDesdeHonorariosInternacion;
	}

	public String getValidoDesdeHonorariosInternacionString() {
		if (validoDesdeHonorariosInternacion == null) {
			return "";
		}
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		return format.format(validoDesdeHonorariosInternacion);
	}

	public void setValidoDesdeHonorariosInternacion(
			Date validoDesdeHonorariosInternacion) {
		this.validoDesdeHonorariosInternacion = validoDesdeHonorariosInternacion;
	}

	public Date getValidoHastaHonorariosInternacion() {
		return validoHastaHonorariosInternacion;
	}

	public String getValidoHastaHonorariosInternacionString() {
		if (validoHastaHonorariosInternacion == null) {
			return "";
		}
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		return format.format(validoHastaHonorariosInternacion);
	}

	public void setValidoHastaHonorariosInternacion(
			Date validoHastaHonorariosInternacion) {
		this.validoHastaHonorariosInternacion = validoHastaHonorariosInternacion;
	}

	public int getIdHonorariosInternacion() {
		return idNomencladorConceptosHonorariosInternacion;
	}

	public void setIdHonorariosInternacion(int idHonorariosInternacion) {
		this.idNomencladorConceptosHonorariosInternacion = idHonorariosInternacion;
	}

	public Date getValidoDesdeGastosAmbulatorio() {
		return validoDesdeGastosAmbulatorio;
	}

	public String getValidoDesdeGastosAmbulatorioString() {
		if (validoDesdeGastosAmbulatorio == null) {
			return "";
		}
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		return format.format(validoDesdeGastosAmbulatorio);
	}

	public void setValidoDesdeGastosAmbulatorio(
			Date validoDesdeGastosAmbulatorio) {
		this.validoDesdeGastosAmbulatorio = validoDesdeGastosAmbulatorio;
	}

	public Date getValidoHastaGastosAmbulatorio() {
		return validoHastaGastosAmbulatorio;
	}

	public String getValidoHastaGastosAmbulatorioString() {
		if (validoHastaGastosAmbulatorio == null) {
			return "";
		}
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		return format.format(validoHastaGastosAmbulatorio);
	}

	public void setValidoHastaGastosAmbulatorio(
			Date validoHastaGastosAmbulatorio) {
		this.validoHastaGastosAmbulatorio = validoHastaGastosAmbulatorio;
	}

	public int getIdGastosAmbulatorio() {
		return idNomencladorConceptosGastosAmbulatorio;
	}

	public void setIdGastosAmbulatorio(int idGastosAmbulatorio) {
		this.idNomencladorConceptosGastosAmbulatorio = idGastosAmbulatorio;
	}

	public Date getValidoDesdeGastosInternacion() {
		return validoDesdeGastosInternacion;
	}

	public String getValidoDesdeGastosInternacionString() {
		if (validoDesdeGastosInternacion == null) {
			return "";
		}
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		return format.format(validoDesdeGastosInternacion);
	}

	public void setValidoDesdeGastosInternacion(
			Date validoDesdeGastosInternacion) {
		this.validoDesdeGastosInternacion = validoDesdeGastosInternacion;
	}

	public Date getValidoHastaGastosInternacion() {
		return validoHastaGastosInternacion;
	}

	public String getValidoHastaGastosInternacionString() {
		if (validoHastaGastosInternacion == null) {
			return "";
		}
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		return format.format(validoHastaGastosInternacion);
	}

	public void setValidoHastaGastosInternacion(
			Date validoHastaGastosInternacion) {
		this.validoHastaGastosInternacion = validoHastaGastosInternacion;
	}

	public int getIdGastosInternacion() {
		return idNomencladorConceptosGastosInternacion;
	}

	public void setIdGastosInternacion(int idGastosInternacion) {
		this.idNomencladorConceptosGastosInternacion = idGastosInternacion;
	}

	public BigDecimal getCoeficienteGastos() {
		return coeficienteGastos;
	}

	public void setCoeficienteGastos(BigDecimal coeficienteGastos) {
		this.coeficienteGastos = coeficienteGastos;
	}

	public BigDecimal getCoeficienteHonorarios() {
		return coeficienteHonorarios;
	}

	public void setCoeficienteHonorarios(BigDecimal coeficienteHonorarios) {
		this.coeficienteHonorarios = coeficienteHonorarios;
	}

	public int getIdTipoNomenclador() {
		return idTipoNomenclador;
	}

	public void setIdTipoNomenclador(int idTipoNomenclador) {
		this.idTipoNomenclador = idTipoNomenclador;
	}

	public String getTipoNomenclador() {
		return tipoNomenclador;
	}

	public void setTipoNomenclador(String tipoNomenclador) {
		this.tipoNomenclador = tipoNomenclador;
	}
}
