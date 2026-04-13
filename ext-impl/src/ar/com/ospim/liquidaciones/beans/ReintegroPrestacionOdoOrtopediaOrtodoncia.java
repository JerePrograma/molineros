package ar.com.ospim.liquidaciones.beans;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author sistema-09
 * @version 1.0 
 */
public class ReintegroPrestacionOdoOrtopediaOrtodoncia extends ReintegroPrestacion{
	private int id;	
	private BigDecimal gastos;
	private BigDecimal cantidad;
	private int id_prestador_externo;
	private int nro_cuotas;
    
	public ReintegroPrestacionOdoOrtopediaOrtodoncia() {

	}

	public ReintegroPrestacionOdoOrtopediaOrtodoncia(Reintegro reintegro, int idPrestacion,
			String cuit, String descripcion, BigDecimal importeTotal) {
		super(reintegro, idPrestacion, cuit, descripcion, importeTotal);
		setImporte(importeTotal);
	}

	public ReintegroPrestacionOdoOrtopediaOrtodoncia(Reintegro reintegro, int idPrestacion, String codigo,
			String cuit, String descripcion, BigDecimal importeTotal) {
		super(reintegro, idPrestacion, cuit, descripcion, importeTotal);
		setCodigo(codigo);
		setImporte(importeTotal);		
	}

	public ReintegroPrestacionOdoOrtopediaOrtodoncia(Reintegro reintegro, int idPrestacion, String codigo,
			String cuit, String descripcion, BigDecimal importeTotal, BigDecimal cantidad ) {
		super(reintegro, idPrestacion, cuit, descripcion, importeTotal);
		setImporte(importeTotal);		
	}
	

	/**
	 * @return the gastos
	 */
	public BigDecimal getGastos() {
		return gastos;
	}

	/**
	 * @param gastos
	 *            the gastos to set
	 */
	public void setGastos(BigDecimal gastos) {
		this.gastos = gastos;
	}

	/**
	 * @return the cantidad
	 */
	public BigDecimal getCantidad() {
		return cantidad;
	}

	/**
	 * @param cantidad the cantidad to set
	 */
	public void setCantidad(BigDecimal cantidad) {
		this.cantidad = cantidad;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}
	
	public static ReintegroPrestacionOdoOrtopediaOrtodoncia getMapping(ResultSet rs,
			String prefix) throws SQLException {
		ReintegroPrestacionOdoOrtopediaOrtodoncia reintegroPrestacion = new ReintegroPrestacionOdoOrtopediaOrtodoncia();		
		reintegroPrestacion.setId_prestacion(rs
				.getInt(prefix + "id_prestacion"));		
		reintegroPrestacion.setId_plan(rs.getInt(prefix + "id_plan"));
		reintegroPrestacion.setFecha_prestacion(rs.getDate(prefix
				+ "fecha_prestacion"));
		reintegroPrestacion.setCantidad(rs.getBigDecimal(prefix + "cantidad"));
		reintegroPrestacion.setImporte(rs.getBigDecimal(prefix + "importe"));
		reintegroPrestacion.setCompro_a_debitar_tipo(rs.getString(prefix
				+ "compro_a_debitar_tipo"));
		reintegroPrestacion.setCompro_a_debitar_numero(rs.getString(prefix
				+ "compro_a_debitar_numero"));
		reintegroPrestacion
				.setTercerizado(rs.getString(prefix + "tercerizado"));
		reintegroPrestacion.setCuit(rs.getString(prefix + "cuit"));
		reintegroPrestacion.setDescripcion(rs.getString(prefix + "descripcion"));
		reintegroPrestacion.setAlta_fecha(rs.getTimestamp(prefix+"alta_fecha"));
		reintegroPrestacion.setAlta_usr(rs.getString(prefix+"alta_usr"));  
		reintegroPrestacion.setModi_fecha(rs.getDate(prefix+"modi_fecha"));
		reintegroPrestacion.setModi_usr(rs.getString(prefix+"modi_usr"));
		reintegroPrestacion.setId_prestador_externo(rs.getInt(prefix+"id_prestador_externo"));
		try{
			reintegroPrestacion.setNro_cuotas(rs.getInt(prefix+"nro_cuotas"));
		} catch (Exception e) {}
		try{
			reintegroPrestacion.setId(rs.getInt(prefix+"id"));
		} catch (Exception e) {}
		try{
			reintegroPrestacion.setGastos(rs.getBigDecimal(prefix+"gastos"));
		} catch (Exception e) {}
		try{
			reintegroPrestacion.setHonorarios(rs.getBigDecimal(prefix+"honorarios"));		
		} catch (Exception e) {}
		try{
			reintegroPrestacion.setCodigo(rs.getString(prefix+"codigo"));
		} catch (Exception e) {}
		if (reintegroPrestacion.getImporte() != null) {
			reintegroPrestacion.setImporteTotal(reintegroPrestacion
					.getImporte().multiply(reintegroPrestacion.getCantidad()).setScale(2,RoundingMode.HALF_DOWN));
		}
		return reintegroPrestacion;
	}

	/**
	 * @return the id_prestador_externo
	 */
	public int getId_prestador_externo() {
		return id_prestador_externo;
	}

	/**
	 * @param idPrestadorExterno the id_prestador_externo to set
	 */
	public void setId_prestador_externo(int idPrestadorExterno) {
		id_prestador_externo = idPrestadorExterno;
	}

	/**
	 * @return the nro_cuotas
	 */
	public int getNro_cuotas() {
		return nro_cuotas;
	}

	/**
	 * @param nroCuotas the nro_cuotas to set
	 */
	public void setNro_cuotas(int nroCuotas) {
		nro_cuotas = nroCuotas;
	}
}