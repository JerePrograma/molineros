package ar.com.ospim.liquidaciones.beans;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import ar.com.ospim.afiliados.beans.Afiliado;
import ar.com.ospim.global.beans.Prestacion;
import ar.com.ospim.util.DateUtils;

/**
 * @author sistema-09
 * @version 1.0
 * @created 13-Sep-2010 04:29:37 p.m.
 */
public class LiquidacionPrestacion {

	protected int id_liquidacion;
	protected int orden;
	protected String cuil_titular;
	protected int inte;
	protected int id_prestacion;
	protected Date fecha_prestacion;
	protected BigDecimal cantidad;
	protected BigDecimal importe;
	protected BigDecimal importeTotal;
	protected String servicio;
	protected BigDecimal solicitado;
	protected BigDecimal debitado;
	protected BigDecimal resultado;
	protected String tercerizado;
	protected Liquidacion liquidacion;
	protected Afiliado afiliado;
	protected Prestacion prestacion;
	protected Date periodo;
	protected int motivoAltaDiscapacidad;
	protected BigDecimal cargoOspim;
	protected BigDecimal cargoPrestadora;
	protected BigDecimal cargoImesa;
	
	
	public BigDecimal getCargoOspim() {
		return cargoOspim;
	}

	public BigDecimal getCargoPrestadora() {
		return cargoPrestadora;
	}

	public void setCargoOspim(BigDecimal cargoOspim) {
		this.cargoOspim = cargoOspim;
	}

	public void setCargoPrestadora(BigDecimal cargoPrestadora) {
		this.cargoPrestadora = cargoPrestadora;
	}


	public LiquidacionPrestacion(){

	}

	public LiquidacionPrestacion(Liquidacion liquidacion, BigDecimal cantidad, BigDecimal importe){		
		this.liquidacion = liquidacion;
		this.cantidad = cantidad;
		this.importe = importe;
		this.importeTotal = importe != null ? importe
				.multiply(cantidad != null ? cantidad : new BigDecimal(0)) : new BigDecimal(0);
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
	 * @param idLiquidacion the id_liquidacion to set
	 */
	public void setId_liquidacion(int idLiquidacion) {
		id_liquidacion = idLiquidacion;
	}

	/**
	 * @return the orden
	 */
	public int getOrden() {
		return orden;
	}

	/**
	 * @return the orden
	 */
	public String getOrdenAsString() {
		return String.valueOf(orden);
	}

	/**
	 * @param orden the orden to set
	 */
	public void setOrden(int orden) {
		this.orden = orden;
	}

	/**
	 * @return the cuil_titular
	 */
	public String getCuil_titular() {
		return cuil_titular;
	}

	/**
	 * @param cuilTitular the cuil_titular to set
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

	/**
	 * @param inte the inte to set
	 */
	public void setInte(int inte) {
		this.inte = inte;
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
	 * @return the fecha_prestacion
	 */
	public Date getFecha_prestacion() {
		return fecha_prestacion;
	}
	
	public String getFecha_prestacionAsString() {
		SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");
		return fecha_prestacion!=null?sdf.format(fecha_prestacion):"";
	}

	/**
	 * @param fechaPrestacion the fecha_prestacion to set
	 */
	public void setFecha_prestacion(Date fechaPrestacion) {
		fecha_prestacion = fechaPrestacion;
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
	 * @return the importe
	 */
	public BigDecimal getImporte() {
		return importe;
	}

	/**
	 * @param importe the importe to set
	 */
	public void setImporte(BigDecimal importe) {
		this.importe = importe;
	}

	/**
	 * @return the servicio
	 */
	public String getServicio() {
		return servicio;
	}

	/**
	 * @param servicio the servicio to set
	 */
	public void setServicio(String servicio) {
		this.servicio = servicio;
	}

	/**
	 * @return the solicitado
	 */
	public BigDecimal getSolicitado() {
		return solicitado == null ? new BigDecimal (0) : solicitado;
	}

	/**
	 * @param solicitado the solicitado to set
	 */
	public void setSolicitado(BigDecimal solicitado) {
		this.solicitado = solicitado;
	}

	/**
	 * @return the debitado
	 */
	public BigDecimal getDebitado() {
		return debitado == null ? new BigDecimal (0) : debitado;
	}

	/**
	 * @param debitado the debitado to set
	 */
	public void setDebitado(BigDecimal debitado) {
		this.debitado = debitado;
	}

	/**
	 * @return the resultado
	 */
	public BigDecimal getResultado() {
		return resultado == null ? new BigDecimal (0) : resultado;
	}

	public BigDecimal getCalculado() {
		//TODO no es claro cual es el cálculo, averiguar esto
		return new BigDecimal(0);
	}
	
	/**
	 * @param resultado the resultado to set
	 */
	public void setResultado(BigDecimal resultado) {
		this.resultado = resultado;
	}

	/**
	 * @return the tercerizado
	 */
	public String getTercerizado() {
		return tercerizado;
	}

	/**
	 * @param tercerizado the tercerizado to set
	 */
	public void setTercerizado(String tercerizado) {
		this.tercerizado = tercerizado;
	}

	/**
	 * @return the liquidacion
	 */
	public Liquidacion getLiquidacion() {
		return liquidacion;
	}

	/**
	 * @param liquidacion the liquidacion to set
	 */
	public void setLiquidacion(Liquidacion liquidacion) {
		this.liquidacion = liquidacion;
	}
	
	/**
	 * @return the importeTotal
	 */
	public BigDecimal getImporteTotal() {
		return importeTotal;
	}

	/**
	 * @param importeTotal
	 *            the importeTotal to set
	 */
	public void setImporteTotal(BigDecimal importeTotal) {
		this.importeTotal = importeTotal;
	}

	public BigDecimal getMultiplicarImporteCant() {
		BigDecimal cant = this.cantidad != null ? this.cantidad : new BigDecimal(0);
		if (importe == null){ 
			return new BigDecimal(0);
		}
		else {
			return this.importe.multiply(cant); // importe * cant
		}
	}

	/**
	 * @return the afiliado
	 */
	public Afiliado getAfiliado() {
		return afiliado;
	}

	/**
	 * @param afiliado the afiliado to set
	 */
	public void setAfiliado(Afiliado afiliado) {
		this.afiliado = afiliado;
	}

	/**
	 * @return the prestacion
	 */
	public Prestacion getPrestacion() {
		return prestacion;
	}

	/**
	 * @param prestacion the prestacion to set
	 */
	public void setPrestacion(Prestacion prestacion) {
		this.prestacion = prestacion;
	}
	
	public void generateImporteTotal() {
		BigDecimal cant = this.cantidad != null ? this.cantidad : new BigDecimal(0);
		this.importeTotal = 
			importe != null ? importe.multiply(cant) : new BigDecimal(0);
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

	public int getMotivoAltaDiscapacidad() {
		return motivoAltaDiscapacidad;
	}

	public void setMotivoAltaDiscapacidad(int motivoAltaDiscapacidad) {
		this.motivoAltaDiscapacidad = motivoAltaDiscapacidad;
	}
	
	public BigDecimal getCargoImesa() {
		return cargoImesa;
	}

	public void setCargoImesa(BigDecimal cargoImesa) {
		this.cargoImesa = cargoImesa;
	}

	public static LiquidacionPrestacion getMapping(ResultSet rs, String prefix) throws SQLException {
		LiquidacionPrestacion liquidacionPrestacion = new LiquidacionPrestacion();
		liquidacionPrestacion.setId_liquidacion(rs.getInt(prefix+"id_liquidacion"));
		liquidacionPrestacion.setOrden(rs.getInt(prefix+"orden"));
		liquidacionPrestacion.setCuil_titular(rs.getString(prefix+"cuil_titular"));		
		liquidacionPrestacion.setInte(rs.getInt(prefix+"inte"));
		liquidacionPrestacion.setId_prestacion(rs.getInt(prefix+"id_prestacion"));		
		liquidacionPrestacion.setFecha_prestacion(rs.getDate(prefix+"fecha_prestacion"));
		liquidacionPrestacion.setCantidad(rs.getBigDecimal(prefix+"cantidad"));
		liquidacionPrestacion.setImporte(rs.getBigDecimal(prefix+"importe"));		
		liquidacionPrestacion.generateImporteTotal();
		liquidacionPrestacion.setServicio(rs.getString(prefix+"servicio"));
		liquidacionPrestacion.setSolicitado(rs.getBigDecimal(prefix+"solicitado"));
		liquidacionPrestacion.setDebitado(rs.getBigDecimal(prefix+"debitado"));
		liquidacionPrestacion.setResultado(rs.getBigDecimal(prefix+"resultado"));
		liquidacionPrestacion.setTercerizado(rs.getString(prefix+"tercerizado"));
		liquidacionPrestacion.setPeriodo(rs.getDate(prefix+"periodo"));
		liquidacionPrestacion.setCargoOspim(rs.getBigDecimal(prefix+"cargo_ospim"));		
		liquidacionPrestacion.setCargoPrestadora(rs.getBigDecimal(prefix+"cargo_prestadora"));
		liquidacionPrestacion.setCargoImesa(rs.getBigDecimal(prefix+"cargo_imesa"));
		return liquidacionPrestacion;		
	}
}