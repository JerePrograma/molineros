package ar.com.ospim.liquidaciones.beans;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import ar.com.ospim.util.DateUtils;

/**
 * @author Martin Moreyra
 * @version 1.0
 * @created 25-Ago-2010 02:25:51 p.m.
 */
public class ReintegroPrestacionNormal extends ReintegroPrestacion {

	private BigDecimal cantidad;
	private Date periodo;
	public ReintegroPrestacionNormal() {

	}

	public ReintegroPrestacionNormal(Reintegro reintegro, int idPrestacion, String codigo,
			String cuit, String descripcion, BigDecimal importe, BigDecimal cant, Date fecha_prestacion, String comprobante) {
		super(reintegro, idPrestacion, codigo, cuit, descripcion, importe != null ? importe
				.multiply(cant).setScale(2,RoundingMode.HALF_DOWN) : new BigDecimal(0));
		setFecha_prestacion(fecha_prestacion);
		setComprobanteString(comprobante);
		setImporte(importe);
		setCantidad(cant);
	}

	/**
	 * @return the cantidad
	 */
	public BigDecimal getCantidad() {
		return cantidad;
	}

	/**
	 * @param cantidad
	 *            the cantidad to set
	 */
	public void setCantidad(BigDecimal cantidad) {
		this.cantidad = cantidad;
	}

	public Date getPeriodo() {
		return periodo;
	}

	public void setPeriodo(Date periodo) {
		this.periodo = periodo;
	}
	
	public String getPeriodoAsString() {
		return null!=periodo?DateUtils.format(periodo,DateUtils.PERIODO):"";
	}

	public static ReintegroPrestacionNormal getMapping(ResultSet rs)
			throws SQLException {
		return getMapping(rs, "");
	}

	public static ReintegroPrestacionNormal getMapping(ResultSet rs,
			String prefix) throws SQLException {
		
		ReintegroPrestacionNormal reintegroPrestacion = new ReintegroPrestacionNormal();
		reintegroPrestacion.setId_prestacion(rs.getInt(prefix + "id_prestacion"));		
		reintegroPrestacion.setId_plan(rs.getInt(prefix + "id_plan"));
		reintegroPrestacion.setFecha_prestacion(rs.getDate(prefix+ "fecha_prestacion"));
		reintegroPrestacion.setCantidad(rs.getBigDecimal(prefix + "cantidad"));
		reintegroPrestacion.setImporte(rs.getBigDecimal(prefix + "importe"));
		reintegroPrestacion.setCompro_a_debitar_tipo(rs.getString(prefix+ "compro_a_debitar_tipo"));
		reintegroPrestacion.setComproaDebitarLetra(rs.getString(prefix+ "compro_a_debitar_letra"));
		reintegroPrestacion.setCompro_a_debitar_sucursal(rs.getString(prefix+ "compro_a_debitar_sucursal"));
		reintegroPrestacion.setCompro_a_debitar_numero(rs.getString(prefix+ "compro_a_debitar_numero"));
		reintegroPrestacion.setTercerizado(rs.getString(prefix + "tercerizado"));
		reintegroPrestacion.setCuit(rs.getString(prefix + "cuit"));
		reintegroPrestacion.setDescripcion(rs.getString(prefix + "descripcion"));
		reintegroPrestacion.setAlta_fecha(rs.getTimestamp(prefix+"alta_fecha"));
		reintegroPrestacion.setAlta_usr(rs.getString(prefix+"alta_usr"));  
		reintegroPrestacion.setModi_fecha(rs.getDate(prefix+"modi_fecha"));
		reintegroPrestacion.setModi_usr(rs.getString(prefix+"modi_usr"));  
		try{
			reintegroPrestacion.setCodigo(rs.getString(prefix+"codigo"));  
		} catch (Exception e) {
		}
		try{
			reintegroPrestacion.setPeriodo(rs.getDate(prefix+"periodo"));  
		} catch (Exception e) {
		}
		try{
			reintegroPrestacion.setCuit_entidad(rs.getString(prefix+"cuit_entidad"));  
		} catch (Exception e) {
		}
		try{
			reintegroPrestacion.setSucursal_entidad(rs.getString(prefix+"sucursal_entidad"));
			reintegroPrestacion.setRazon_social_entidad(rs.getString(prefix+"razon_social_entidad"));
		} catch (Exception e) {
		}
		try{
			reintegroPrestacion.setFecha_comprobante(rs.getDate(prefix+"fecha_comprobante"));
			reintegroPrestacion.setImporte_comprobante(rs.getBigDecimal(prefix+"importe_comprobante"));
		} catch (Exception e) {}
		if (reintegroPrestacion.getImporte() != null) {
			reintegroPrestacion.setImporteTotal(reintegroPrestacion
					.getImporte().multiply(reintegroPrestacion.getCantidad()).setScale(2, RoundingMode.HALF_DOWN));
		}
		try{
			reintegroPrestacion.setImporteOspim(rs.getBigDecimal(prefix + "cargo_ospim"));
			reintegroPrestacion.setImportePrestadora(rs.getBigDecimal(prefix + "cargo_prestadora"));
			reintegroPrestacion.setImporteImesa(rs.getBigDecimal(prefix + "cargo_imesa"));
		} catch (Exception e) {
		}
		
		try{
			reintegroPrestacion.setId_reclamo_prestacional(rs.getInt(prefix+"id_reclamo_prestacional"));  
		} catch (Exception e) {
		}
		
		return reintegroPrestacion;
	}

}