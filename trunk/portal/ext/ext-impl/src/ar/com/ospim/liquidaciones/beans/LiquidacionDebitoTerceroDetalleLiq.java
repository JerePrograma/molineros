package ar.com.ospim.liquidaciones.beans;

import java.sql.ResultSet;
import java.sql.SQLException;
import ar.com.ospim.global.beans.Comprobante;
import ar.com.ospim.global.beans.OrdenPagoOspim;
import ar.com.ospim.global.beans.Comprobante.ComprobanteConcepto;

/**
 * @author crivas
 * @version 1.0
 * @created 13-Sep-2011 04:29:37 p.m.
 */
public class LiquidacionDebitoTerceroDetalleLiq {

	protected int id_detalle;
	protected Liquidacion liquidacion;
	protected OrdenPagoOspim op;
	private ComprobanteConcepto comprobanteConcepto;
	private Comprobante comprobante;

	public LiquidacionDebitoTerceroDetalleLiq() {

	}

	public static LiquidacionDebitoTerceroDetalleLiq getMapping(ResultSet rs,
			String prefix) throws SQLException {

		LiquidacionDebitoTerceroDetalleLiq liquidacionDebitoTerceroDetalleLiq = new LiquidacionDebitoTerceroDetalleLiq();
		liquidacionDebitoTerceroDetalleLiq.setId_detalle(rs.getInt(prefix
				+ "id_detalle"));
		liquidacionDebitoTerceroDetalleLiq
				.setComprobanteConcepto(ComprobanteConcepto.getMapping(rs,
						prefix));
		liquidacionDebitoTerceroDetalleLiq.setComprobante(Comprobante
				.getMapping(rs, prefix));
		liquidacionDebitoTerceroDetalleLiq.setLiquidacion(Liquidacion
				.getMapping(rs, prefix));
		liquidacionDebitoTerceroDetalleLiq.setOp(OrdenPagoOspim.getMapping(rs,
				prefix));
		return liquidacionDebitoTerceroDetalleLiq;
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
	 * @return the comprobanteConcepto
	 */
	public ComprobanteConcepto getComprobanteConcepto() {
		return comprobanteConcepto;
	}

	/**
	 * @param comprobanteConcepto
	 *            the comprobanteConcepto to set
	 */
	public void setComprobanteConcepto(ComprobanteConcepto comprobanteConcepto) {
		this.comprobanteConcepto = comprobanteConcepto;
	}

	/**
	 * @return the comprobante
	 */
	public Comprobante getComprobante() {
		return comprobante;
	}

	/**
	 * @param comprobante
	 *            the comprobante to set
	 */
	public void setComprobante(Comprobante comprobante) {
		this.comprobante = comprobante;
	}

	/**
	 * @return the liquidacion
	 */
	public Liquidacion getLiquidacion() {
		return liquidacion;
	}

	/**
	 * @param liquidacion
	 *            the liquidacion to set
	 */
	public void setLiquidacion(Liquidacion liquidacion) {
		this.liquidacion = liquidacion;
	}

	/**
	 * @return the op
	 */
	public OrdenPagoOspim getOp() {
		return op;
	}

	/**
	 * @param op
	 *            the op to set
	 */
	public void setOp(OrdenPagoOspim op) {
		this.op = op;
	}

}