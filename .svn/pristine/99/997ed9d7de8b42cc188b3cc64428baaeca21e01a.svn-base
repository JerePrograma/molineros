package ar.com.ospim.liquidaciones.beans;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import ar.com.ospim.util.DateUtils;

/**
 * @author crivas
 * @version 1.0
 * @created 01-Sep-2011 04:30:50 p.m.
 */
public class LiquidacionDebitoTercero {

	private int id_liquidacion;
	private Date periodoHasta;
	private String observaciones;
	private Date alta_fecha;
	private String alta_usr;
	private Date modi_fecha;
	private String modi_usr;
	private Date baja_fecha;
	private String baja_usr;
	private BigDecimal importe_total;	
	private List <LiquidacionDebitoTerceroDetalleLiq> detalleLiquidacionDebitosTercerosLiq;
	private List <LiquidacionDebitoTerceroDetalleReint> detalleLiquidacionDebitosTercerosReint;//prestacionale
	private List <LiquidacionDebitoTerceroDetalleReintOrtod> detalleLiquidacionDebitosTercerosReintOrtod;//ortop
	int numeroDebito;

	public LiquidacionDebitoTercero(){

	}
	
	/**
	 * @return the id_liquidacion
	 */
	public int getId_liquidacion() {
		return id_liquidacion;
	}

	public String getId_liquidacionString() {
		return String.valueOf(getId_liquidacion());
	}
	
	/**
	 * @param idLiquidacion the id_liquidacioan to set
	 */
	public void setId_liquidacion(int idLiquidacion) {
		id_liquidacion = idLiquidacion;
	}

	/**
	 * @return the periodoHasta
	 */
	public Date getPeriodoHasta() {
		return periodoHasta;
	}

	/**
	 * @return the periodo
	 */
	public String getPeriodoString() {
		return null!=periodoHasta?DateUtils.format(periodoHasta,DateUtils.PERIODO):"";
	}

	/**
	 * @param periodo the periodo to set
	 */
	public void setPeriodoHasta(Date periodo) {
		this.periodoHasta = periodo;
	}
	
	/**
	 * @return the baja_fecha
	 */
	public Date getBaja_fecha() {
		return baja_fecha;
	}

	/**
	 * @param bajaFecha the baja_fecha to set
	 */
	public void setBaja_fecha(Date bajaFecha) {
		baja_fecha = bajaFecha;
	}

	/**
	 * @return the baja_usr
	 */
	public String getBaja_usr() {
		return baja_usr;
	}

	/**
	 * @param bajaUsr the baja_usr to set
	 */
	public void setBaja_usr(String bajaUsr) {
		baja_usr = bajaUsr;
	}

	/**
	 * @return the alta_fecha
	 */
	public Date getAlta_fecha() {
		return alta_fecha;
	}

	/**
	 * @param altaFecha the alta_fecha to set
	 */
	public void setAlta_fecha(Date altaFecha) {
		alta_fecha = altaFecha;
	}

	/**
	 * @return the alta_usr
	 */
	public String getAlta_usr() {
		return alta_usr;
	}

	/**
	 * @param altaUsr the alta_usr to set
	 */
	public void setAlta_usr(String altaUsr) {
		alta_usr = altaUsr;
	}

	/**
	 * @return the modi_fecha
	 */
	public Date getModi_fecha() {
		return modi_fecha;
	}

	/**
	 * @param modiFecha the modi_fecha to set
	 */
	public void setModi_fecha(Date modiFecha) {
		modi_fecha = modiFecha;
	}

	/**
	 * @return the modi_usr
	 */
	public String getModi_usr() {
		return modi_usr;
	}

	/**
	 * @param modiUsr the modi_usr to set
	 */
	public void setModi_usr(String modiUsr) {
		modi_usr = modiUsr;
	}
	
	public static LiquidacionDebitoTercero getMapping(ResultSet rs, String prefix) throws SQLException {
		LiquidacionDebitoTercero liquidacion = new LiquidacionDebitoTercero();					
		liquidacion.setId_liquidacion(rs.getInt(prefix+"id_liquidacion"));
		liquidacion.setPeriodoHasta(rs.getDate(prefix+"periodo_hasta"));
		liquidacion.setAlta_fecha(rs.getDate(prefix+"alta_fecha")); 
		liquidacion.setAlta_usr(rs.getString(prefix+"alta_usr")); 
		liquidacion.setModi_fecha(rs.getDate(prefix+"modi_fecha")); 
		liquidacion.setModi_usr(rs.getString(prefix+"modi_usr")); 
		liquidacion.setBaja_fecha(rs.getDate(prefix+"baja_fecha")); 
		liquidacion.setBaja_usr(rs.getString(prefix+"baja_usr")); 									
		liquidacion.setObservaciones(rs.getString(prefix+"observaciones"));
		liquidacion.setNumeroDebito(rs.getInt(prefix+"numero_ndb"));		
		return liquidacion;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LiquidacionDebitoTercero other = (LiquidacionDebitoTercero) obj;
		if (id_liquidacion != other.id_liquidacion)
			return false;
		return true;
	}

	/**
	 * @return the importe_total
	 */
	public BigDecimal getImporte_total() {
		return importe_total;
	}

	/**
	 * @param importeTotal the importe_total to set
	 */
	public void setImporte_total(BigDecimal importeTotal) {
		importe_total = importeTotal;
	}
	
	/**
	 * @return the observaciones
	 */
	public String getObservaciones() {
		return observaciones;
	}

	/**
	 * @param observaciones the observaciones to set
	 */
	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}



	/**
	 * @return the detalleLiquidacionDebitosTercerosLiq
	 */
	public List<LiquidacionDebitoTerceroDetalleLiq> getDetalleLiquidacionDebitosTercerosLiq() {
		return detalleLiquidacionDebitosTercerosLiq;
	}



	/**
	 * @param detalleLiquidacionDebitosTercerosLiq the detalleLiquidacionDebitosTercerosLiq to set
	 */
	public void setDetalleLiquidacionDebitosTercerosLiq(
			List<LiquidacionDebitoTerceroDetalleLiq> detalleLiquidacionDebitosTercerosLiq) {
		this.detalleLiquidacionDebitosTercerosLiq = detalleLiquidacionDebitosTercerosLiq;
	}



	/**
	 * @return the detalleLiquidacionDebitosTercerosReint
	 */
	public List<LiquidacionDebitoTerceroDetalleReint> getDetalleLiquidacionDebitosTercerosReint() {
		return detalleLiquidacionDebitosTercerosReint;
	}



	/**
	 * @param detalleLiquidacionDebitosTercerosReint the detalleLiquidacionDebitosTercerosReint to set
	 */
	public void setDetalleLiquidacionDebitosTercerosReint(
			List<LiquidacionDebitoTerceroDetalleReint> detalleLiquidacionDebitosTercerosReint) {
		this.detalleLiquidacionDebitosTercerosReint = detalleLiquidacionDebitosTercerosReint;
	}



	/**
	 * @return the detalleLiquidacionDebitosTercerosReintOrtod
	 */
	public List<LiquidacionDebitoTerceroDetalleReintOrtod> getDetalleLiquidacionDebitosTercerosReintOrtod() {
		return detalleLiquidacionDebitosTercerosReintOrtod;
	}



	/**
	 * @param detalleLiquidacionDebitosTercerosReintOrtod the detalleLiquidacionDebitosTercerosReintOrtod to set
	 */
	public void setDetalleLiquidacionDebitosTercerosReintOrtod(
			List<LiquidacionDebitoTerceroDetalleReintOrtod> detalleLiquidacionDebitosTercerosReintOrtod) {
		this.detalleLiquidacionDebitosTercerosReintOrtod = detalleLiquidacionDebitosTercerosReintOrtod;
	}

	/**
	 * @return the numeroDebito
	 */
	public int getNumeroDebito() {
		return numeroDebito;
	}

	public String getNumeroDebitoMasked() {
		String numero = String.valueOf(numeroDebito);										
		while (numero.length() < 8) {
			numero = "0" + numero;
		}		
		numero = "0002-" + numero;  
		return numero;
	}
	/**
	 * @param numeroDebito the numeroDebito to set
	 */
	public void setNumeroDebito(int numeroDebito) {
		this.numeroDebito = numeroDebito;
	}
	
	public void generarImporteTotal() {
		BigDecimal importeTotal = BigDecimal.ZERO;
		for (LiquidacionDebitoTerceroDetalleLiq l : getDetalleLiquidacionDebitosTercerosLiq()) {
			importeTotal = importeTotal.add(l.getComprobanteConcepto().getImporte());
		}
		for (LiquidacionDebitoTerceroDetalleReint l : getDetalleLiquidacionDebitosTercerosReint()) {
			importeTotal = importeTotal.add(l.getReintegroPrestacion().getImporte()
			.multiply(
					new BigDecimal(l.getReintegroPrestacion()
							.getCantidad().doubleValue())));
		}
		for (LiquidacionDebitoTerceroDetalleReintOrtod l : getDetalleLiquidacionDebitosTercerosReintOrtod()) {
			importeTotal = importeTotal.add(l.getReintegroPrestacion()
					.getReintegro().getDetalleCuota().get(0).getImporte());
		}
		setImporte_total(importeTotal);
	}	
}