package ar.com.ospim.liquidaciones.beans;

import java.sql.ResultSet;
import java.sql.SQLException;

import ar.com.ospim.global.beans.OrdenPagoOspim;

/**
 * @author crivas
 * @version 1.0
 * @created 13-Sep-2011 04:29:37 p.m.
 */
public class LiquidacionDebitoTerceroDetalleReint {

	protected int id_detalle;
	protected OrdenPagoOspim op;
	private ReintegroPrestacionNormal reintegroPrestacion;	

	public LiquidacionDebitoTerceroDetalleReint() {
	}

	public static LiquidacionDebitoTerceroDetalleReint getMapping(ResultSet rs,
			String prefix) throws SQLException {

		LiquidacionDebitoTerceroDetalleReint liquidacionDebitoTerceroDetalleReint = new LiquidacionDebitoTerceroDetalleReint();
		liquidacionDebitoTerceroDetalleReint.setId_detalle(rs.getInt(prefix
				+ "id_detalle"));
		liquidacionDebitoTerceroDetalleReint
				.setReintegroPrestacion(ReintegroPrestacionNormal.getMapping(rs,
						prefix));
		return liquidacionDebitoTerceroDetalleReint;
	}

	/**
	 * @return the id_detalle
	 */
	public int getId_detalle() {
		return id_detalle;
	}

	/**
	 * @param idDetalle
	 *            the id_detalle to set
	 */
	public void setId_detalle(int idDetalle) {
		id_detalle = idDetalle;
	}

	/**
	 * @return the reintegroPrestacion
	 */
	public ReintegroPrestacionNormal getReintegroPrestacion() {
		return reintegroPrestacion;
	}

	/**
	 * @param reintegroPrestacion the reintegroPrestacion to set
	 */
	public void setReintegroPrestacion(ReintegroPrestacionNormal reintegroPrestacion) {
		this.reintegroPrestacion = reintegroPrestacion;
	}

	/**
	 * @return the op
	 */
	public OrdenPagoOspim getOp() {
		return op;
	}

	/**
	 * @param op the op to set
	 */
	public void setOp(OrdenPagoOspim op) {
		this.op = op;
	}

}