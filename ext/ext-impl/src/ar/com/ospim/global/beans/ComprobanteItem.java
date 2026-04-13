package ar.com.ospim.global.beans;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class ComprobanteItem {
  	
    private int ptoVenta;
    private String nroComprobante;
	private String tipoComprobante;
	private String letraComprobante;
	private int sucuComprobante;
	private String cuit;
	private int item;
	
	private BigDecimal porcentaje;
	private BigDecimal valor;
	private BigDecimal ivains;
	private BigDecimal ivanins;
	private BigDecimal ivaexen;
	private BigDecimal saldo;
	
	private String observaciones;

	private Date alta_fecha;
	private String alta_usr;
	private Date modi_fecha;
	private String modi_usr;
	private Date baja_fecha;
	private String baja_usr;

	private int motivo;
	private String descripcion_motivo;
	
	public Date getAlta_fecha() {
		return alta_fecha;
	}

	public void setAlta_fecha(Date altaFecha) {
		alta_fecha = altaFecha;
	}

	public String getAlta_usr() {
		return alta_usr;
	}

	public void setAlta_usr(String altaUsr) {
		alta_usr = altaUsr;
	}

	public Date getModi_fecha() {
		return modi_fecha;
	}

	public void setModi_fecha(Date modiFecha) {
		modi_fecha = modiFecha;
	}

	public String getModi_usr() {
		return modi_usr;
	}

	public void setModi_usr(String modiUsr) {
		modi_usr = modiUsr;
	}

	public Date getBaja_fecha() {
		return baja_fecha;
	}

	public void setBaja_fecha(Date bajaFecha) {
		baja_fecha = bajaFecha;
	}

	public String getBaja_usr() {
		return baja_usr;
	}

	public void setBaja_usr(String bajaUsr) {
		baja_usr = bajaUsr;
	}

	public ComprobanteItem() {

	}
	
	public ComprobanteItem(int ptoVenta, String tipoComprobante,
			String nroComprobante, String cuit,
			String letraComprobante, int sucuComprobante, int item, BigDecimal valor, 
			BigDecimal porcentaje, BigDecimal ivains, BigDecimal ivanins, BigDecimal ivaexen, BigDecimal saldo, String observaciones, int motivo) {
		this.ptoVenta = ptoVenta;
		this.tipoComprobante = tipoComprobante;
		this.nroComprobante = nroComprobante;
		this.cuit = cuit;
		this.letraComprobante = letraComprobante;
		this.sucuComprobante = sucuComprobante;
		this.item = item;		
		this.valor = valor;
		this.porcentaje = porcentaje;
		this.ivains = ivains;
		this.ivanins = ivanins;
		this.ivaexen = ivaexen;
		this.saldo = saldo;
		this.observaciones = observaciones;
		this.motivo = motivo;
	}

	public ComprobanteItem(int ptoVenta, String tipoComprobante,
			String nroComprobante, String letraComprobante,
			int sucuComprobante, String cuit, int item) {
		this.ptoVenta = ptoVenta;
		this.tipoComprobante = tipoComprobante;
		this.nroComprobante = nroComprobante;
		this.cuit = cuit;
		this.letraComprobante = letraComprobante;
		this.sucuComprobante = sucuComprobante;
		this.item = item;
	}

	public String getNroComprobante() {
		return nroComprobante;
	}

	public void setNroComprobante(String nroComprobante) {
		this.nroComprobante = nroComprobante;
	}

	public String getTipoComprobante() {
		return tipoComprobante;
	}

	public void setTipoComprobante(String tipoComprobante) {
		this.tipoComprobante = tipoComprobante;
	}

	public static ComprobanteItem getMapping(ResultSet rs) throws SQLException {
		return getMapping(rs, "");
	}

	public static ComprobanteItem getMapping(ResultSet rs, String prefix)
			throws SQLException {
		ComprobanteItem comp = new ComprobanteItem();
		comp.setNroComprobante(rs.getString(prefix + "nro"));
		comp.setTipoComprobante(rs.getString(prefix + "tipo"));
		comp.setPtoVenta(rs.getInt(prefix + "id_punto_venta"));
		comp.setLetraComprobante(rs.getString(prefix + "compro_letra"));
		comp.setSucuComprobante(rs.getInt(prefix + "compro_sucu"));
		comp.setCuit(rs.getString(prefix + "cuit"));		
		comp.setItem(rs.getInt(prefix + "item"));
		comp.setObservaciones(rs.getString(prefix + "observaciones"));
		comp.setSaldo(rs.getBigDecimal(prefix + "saldo"));		
		comp.setAlta_fecha(rs.getDate(prefix + "alta_fecha"));
		comp.setAlta_usr(rs.getString(prefix + "alta_usr"));
		comp.setModi_fecha(rs.getDate(prefix + "modi_fecha"));
		comp.setModi_usr(rs.getString(prefix + "modi_usr"));
		comp.setBaja_fecha(rs.getDate(prefix + "baja_fecha"));
		comp.setBaja_usr(rs.getString(prefix + "baja_usr"));
		comp.setMotivo(rs.getInt(prefix + "motivo"));
		comp.setDescripcion_motivo(rs.getString(prefix + "descripcion_motivo"));
		return comp;
	}

	public void setPtoVenta(int ptoVenta) {
		this.ptoVenta = ptoVenta;
	}

	public int getPtoVenta() {
		return ptoVenta;
	}

	public void setCuit(String cuit) {
		this.cuit = cuit;
	}

	public String getCuit() {
		return cuit;
	}

	/**
	 * @return the letraComprobante
	 */
	public String getLetraComprobante() {
		return letraComprobante;
	}

	/**
	 * @param letraComprobante
	 *            the letraComprobante to set
	 */
	public void setLetraComprobante(String letraComprobante) {
		this.letraComprobante = letraComprobante;
	}

	/**
	 * @return the sucuComprobante
	 */
	public int getSucuComprobante() {
		return sucuComprobante;
	}

	/**
	 * @param sucuComprobante
	 *            the sucuComprobante to set
	 */
	public void setSucuComprobante(int sucuComprobante) {
		this.sucuComprobante = sucuComprobante;
	}

	/**
	 * @return the porcentaje
	 */
	public BigDecimal getPorcentaje() {
		return porcentaje;
	}

	/**
	 * @param porcentaje the porcentaje to set
	 */
	public void setPorcentaje(BigDecimal porcentaje) {
		this.porcentaje = porcentaje;
	}

	/**
	 * @return the valor
	 */
	public BigDecimal getValor() {
		return valor;
	}

	/**
	 * @param valor the valor to set
	 */
	public void setValor(BigDecimal valor) {
		this.valor = valor;
	}

	/**
	 * @return the ivains
	 */
	public BigDecimal getIvains() {
		return ivains;
	}

	/**
	 * @param ivains the ivains to set
	 */
	public void setIvains(BigDecimal ivains) {
		this.ivains = ivains;
	}

	/**
	 * @return the ivanins
	 */
	public BigDecimal getIvanins() {
		return ivanins;
	}

	/**
	 * @param ivanins the ivanins to set
	 */
	public void setIvanins(BigDecimal ivanins) {
		this.ivanins = ivanins;
	}

	/**
	 * @return the ivaexen
	 */
	public BigDecimal getIvaexen() {
		return ivaexen;
	}

	/**
	 * @param ivaexen the ivaexen to set
	 */
	public void setIvaexen(BigDecimal ivaexen) {
		this.ivaexen = ivaexen;
	}

	/**
	 * @return the saldo
	 */
	public BigDecimal getSaldo() {
		return saldo;
	}

	/**
	 * @param saldo the saldo to set
	 */
	public void setSaldo(BigDecimal saldo) {
		this.saldo = saldo;
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
	 * @return the item
	 */
	public int getItem() {
		return item;
	}

	/**
	 * @param item the item to set
	 */
	public void setItem(int item) {
		this.item = item;
	}

	/**
	 * @return the motivo
	 */
	public int getMotivo() {
		return motivo;
	}

	/**
	 * @param motivo the motivo to set
	 */
	public void setMotivo(int motivo) {
		this.motivo = motivo;
	}

	/**
	 * @return the descripcion_motivo
	 */
	public String getDescripcion_motivo() {
		return descripcion_motivo;
	}

	/**
	 * @param descripcionMotivo the descripcion_motivo to set
	 */
	public void setDescripcion_motivo(String descripcionMotivo) {
		descripcion_motivo = descripcionMotivo;
	}
}
