package ar.com.ospim.farmacia.beans;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import ar.com.ospim.util.DateUtils;

/**
 * @author Federico Brachi
 * @version 1.0
 * @created 14-Jul-2010 12:25:06 p.m.
 */
public class ReintegroMedicamentoItem {

	private int id;
	private int id_reintegro;
	private Medicamento medicamento;	
	private double cantidad;
	//private BigDecimal importeTotal;
	
	private Date fechaPrestacion;
	private int numeroReceta;
	private Date fechaReceta;

	private BigDecimal porc_cobertura_sss;
	private BigDecimal porc_cobertura_amtima;
	private BigDecimal porc_cobertura_ospim;
	
	private BigDecimal importeCoberturaOspim;
	private BigDecimal importeCoberturaAmtima;
	private BigDecimal importeCoberturaPrestadora;
	private BigDecimal importeCoberturaImesa;

	
	
	private BigDecimal precio_ospim;
	private BigDecimal precio_al_publico;
	
	private BigDecimal totalMedicamento;
	private BigDecimal totalCobertura;
	private BigDecimal total;
	
	private int id_medicamento;
		
	
	private int idReclamoPrestacional;
	private int idPrestacionReclamo;
	
	protected Date alta_fecha;
	protected String alta_usr;
	protected Date modi_fecha;
	protected String modi_usr;
	protected Date baja_fecha;
	protected String baja_usr;
	
	private boolean isDelete = false;
	private boolean isEdit = false;	
	
	private String comproaDebitarTipo;
	private String comproaDebitarLetra;
	private String comproaDebitarSucursal;
	private String comproaDebitarNumero;
	private String cuitEntidad;
	private String sucursalEntidad;
	private Date fechaComprobante;
	private BigDecimal importeComprobante;
	
	
	
	
	public ReintegroMedicamentoItem() {
	}

	public Medicamento getMedicamento() {
		return medicamento;
	}

	public void setMedicamento(Medicamento medicamento) {
		this.medicamento = medicamento;
	}

	public double getCantidad() {
		return cantidad;
	}

	public void setCantidad(double cantidad) {
		this.cantidad = cantidad;
	}
	
//	public BigDecimal getImporteTotal() {
//		return importeTotal;
//	}
//
//	public void setImporteTotal(BigDecimal importeTotal) {
//		this.importeTotal = importeTotal;
//	}

	public BigDecimal getImporteCoberturaOspim() {
		return importeCoberturaOspim;
	}

	public void setImporteCoberturaOspim(BigDecimal importeCobertura) {
		this.importeCoberturaOspim = importeCobertura;
	}

	public BigDecimal getImporteCoberturaAmtima() {
		return importeCoberturaAmtima;
	}

	public void setImporteCoberturaAmtima(BigDecimal importeCobertura) {
		this.importeCoberturaAmtima = importeCobertura;
	}

	
	/**
	  datos del reclamo prestaiconal del item del reintegro 
	  **/
	
	public int getIdReclamoPrestacional() {
		return idReclamoPrestacional;
	}

	public void setIdReclamoPrestacional(int idReclamoPrestacional) {
		this.idReclamoPrestacional = idReclamoPrestacional;
	}

	public int getIdPrestacionReclamo() {
		return idPrestacionReclamo;
	}

	public void setIdPrestacionReclamo(int idPrestacionReclamo) {
		this.idPrestacionReclamo = idPrestacionReclamo;
	}

	/**
	 datos del reclamo prestaiconal del item del reintegro 
	  **/

	

	
	/**
	 * @return the precio_ospim
	 */
	public BigDecimal getPrecio_ospim() {
		return precio_ospim;
	}

	/**
	 * @param precioOspim the precio_ospim to set
	 */
	public void setPrecio_ospim(BigDecimal precioOspim) {
		precio_ospim = precioOspim;
	}

	/**
	 * @return the precio_al_publico
	 */
	public BigDecimal getPrecio_al_publico() {
		return precio_al_publico;
	}

	/**
	 * @param precioAlPublico the precio_al_publico to set
	 */
	public void setPrecio_al_publico(BigDecimal precioAlPublico) {
		precio_al_publico = precioAlPublico;
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
	 * @return the fechaPrestacion
	 */
	public Date getFechaPrestacion() {
		return fechaPrestacion;
	}

	/**
	 * @param fechaPrestacion the fechaPrestacion to set
	 */
	public void setFechaPrestacion(Date fechaPrestacion) {
		this.fechaPrestacion = fechaPrestacion;
	}

	
	public String getFechaPrestacionTexto(){
		
		return fechaPrestacion != null ? DateUtils.format(fechaPrestacion, "dd/MM/yyyy") : "";	
		
	}
	
	/**
	 * @return the numeroReceta
	 */
	public int getNumeroReceta() {
		return numeroReceta;
	}

	/**
	 * @param numeroReceta the numeroReceta to set
	 */
	public void setNumeroReceta(int numeroReceta) {
		this.numeroReceta = numeroReceta;
	}

	public Date getFechaReceta() {
		return fechaReceta;
	}

	public void setFechaReceta(Date fechaReceta) {
		this.fechaReceta = fechaReceta;
	}	
	
	public static ReintegroMedicamentoItem getMapping(ResultSet rs)
			throws SQLException {
		return getMapping(rs, "");
	}
			
	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @return the id
	 */
	public String getIdAsString() {
		return String.valueOf(id);
	}
	
	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the porc_cobertura_sss
	 */
	public BigDecimal getPorc_cobertura_sss() {
		return porc_cobertura_sss;
	}

	/**
	 * @param porcCoberturaSss the porc_cobertura_sss to set
	 */
	public void setPorc_cobertura_sss(BigDecimal porcCoberturaSss) {
		porc_cobertura_sss = porcCoberturaSss;
	}

	/**
	 * @return the porc_cobertura_amtima
	 */
	public BigDecimal getPorc_cobertura_amtima() {
		return porc_cobertura_amtima;
	}

	/**
	 * @param porcCoberturaAmtima the porc_cobertura_amtima to set
	 */
	public void setPorc_cobertura_amtima(BigDecimal porcCoberturaAmtima) {
		porc_cobertura_amtima = porcCoberturaAmtima;
	}

	/**
	 * @return the porc_cobertura_ospim
	 */
	public BigDecimal getPorc_cobertura_ospim() {
		return porc_cobertura_ospim;
	}

	/**
	 * @param porcCoberturaOspim the porc_cobertura_ospim to set
	 */
	public void setPorc_cobertura_ospim(BigDecimal porcCoberturaOspim) {
		porc_cobertura_ospim = porcCoberturaOspim;
	}

	/**
	 * @return the totalMedicamento
	 */
	public BigDecimal getTotalMedicamento() {
		return totalMedicamento;
	}

	/**
	 * @param totalMedicamento the totalMedicamento to set
	 */
	public void setTotalMedicamento(BigDecimal totalMedicamento) {
		this.totalMedicamento = totalMedicamento;
	}

	/**
	 * @return the totalCobertura
	 */
	public BigDecimal getTotalCobertura() {
		return totalCobertura;
	}

	/**
	 * @param totalCobertura the totalCobertura to set
	 */
	public void setTotalCobertura(BigDecimal totalCobertura) {
		this.totalCobertura = totalCobertura;
	}

	/**
	 * @return the total
	 */
	public BigDecimal getTotal() {
		return total;
	}

	/**
	 * @param total the total to set
	 */
	public void setTotal(BigDecimal total) {
		this.total = total;
	}

	/**
	 * @return the id_medicamento
	 */
	public int getId_medicamento() {
		return id_medicamento;
	}

	/**
	 * @param idMedicamento the id_medicamento to set
	 */
	public void setId_medicamento(int idMedicamento) {
		id_medicamento = idMedicamento;
	}
		
	/**
	 * @return the isDelete
	 */
	public boolean isDelete() {
		return isDelete;
	}

	/**
	 * @param isDelete the isDelete to set
	 */
	public void setDelete(boolean isDelete) {
		this.isDelete = isDelete;
	}

	/**
	 * @return the isEdit
	 */
	public boolean isEdit() {
		return isEdit;
	}

	/**
	 * @param isEdit the isEdit to set
	 */
	public void setEdit(boolean isEdit) {
		this.isEdit = isEdit;
	}	
	
	/**
	 * @return the id_reintegro
	 */
	public int getId_reintegro() {
		return id_reintegro;
	}

	/**
	 * @param idReintegro the id_reintegro to set
	 */
	public void setId_reintegro(int idReintegro) {
		id_reintegro = idReintegro;
	}

	public BigDecimal getImporteCoberturaPrestadora() {
		return importeCoberturaPrestadora;
	}

	public void setImporteCoberturaPrestadora(BigDecimal importeCoberturaPrestadora) {
		this.importeCoberturaPrestadora = importeCoberturaPrestadora;
	}

	public String getComproaDebitarTipo() {
		return comproaDebitarTipo;
	}

	public void setComproaDebitarTipo(String comproaDebitarTipo) {
		this.comproaDebitarTipo = comproaDebitarTipo;
	}

	public String getComproaDebitarLetra() {
		return comproaDebitarLetra;
	}

	public void setComproaDebitarLetra(String comproaDebitarLetra) {
		this.comproaDebitarLetra = comproaDebitarLetra;
	}

	public String getComproaDebitarSucursal() {
		return comproaDebitarSucursal;
	}

	public void setComproaDebitarSucursal(String comproaDebitarSucursal) {
		this.comproaDebitarSucursal = comproaDebitarSucursal;
	}

	public String getComproaDebitarNumero() {
		return comproaDebitarNumero;
	}

	public void setComproaDebitarNumero(String comproaDebitarNumero) {
		this.comproaDebitarNumero = comproaDebitarNumero;
	}

	public String getCuitEntidad() {
		return cuitEntidad;
	}

	public void setCuitEntidad(String cuitEntidad) {
		this.cuitEntidad = cuitEntidad;
	}

	public String getSucursalEntidad() {
		return sucursalEntidad;
	}

	public void setSucursalEntidad(String sucursalEntidad) {
		this.sucursalEntidad = sucursalEntidad;
	}

	public Date getFechaComprobante() {
		return fechaComprobante;
	}

	public void setFechaComprobante(Date fechaComprobante) {
		this.fechaComprobante = fechaComprobante;
	}

	public BigDecimal getImporteComprobante() {
		return importeComprobante;
	}

	public void setImporteComprobante(BigDecimal importeComprobante) {
		this.importeComprobante = importeComprobante;
	}

	
	public String getComprobanteTexto(){
		
		return fechaComprobante != null ? DateUtils.format(fechaComprobante, "dd/MM/yyyy") : "";	
		
	}
	
	public BigDecimal getImporteCoberturaImesa() {
		return importeCoberturaImesa;
	}

	public void setImporteCoberturaImesa(BigDecimal importeCoberturaImesa) {
		this.importeCoberturaImesa = importeCoberturaImesa;
	}

	//llenar el medicamento, (troquel, id_medicamento) por afuera
	public static ReintegroMedicamentoItem getMapping(ResultSet rs,
			String prefix) throws SQLException {
		ReintegroMedicamentoItem reintegroPrestacion = new ReintegroMedicamentoItem();
		reintegroPrestacion.setId(rs.getInt(prefix + "id"));		
		reintegroPrestacion.setFechaPrestacion(rs.getDate(prefix + "fecha"));
		reintegroPrestacion.setNumeroReceta(rs.getInt(prefix + "nro_receta"));
		reintegroPrestacion.setFechaReceta(rs.getDate(prefix + "fecha_receta"));
		reintegroPrestacion.setId_reintegro(rs.getInt(prefix + "id_reintegro"));
		reintegroPrestacion.setCantidad(rs.getDouble(prefix + "cantidad"));
		reintegroPrestacion.setPorc_cobertura_sss(rs.getBigDecimal(prefix + "cober_sss"));
		reintegroPrestacion.setPorc_cobertura_amtima(rs.getBigDecimal(prefix + "cober_amtima"));
		reintegroPrestacion.setPorc_cobertura_ospim(rs.getBigDecimal(prefix + "cober_ospim"));			
		reintegroPrestacion.setImporteCoberturaOspim(rs.getBigDecimal(prefix + "monto_ospim"));
		reintegroPrestacion.setImporteCoberturaAmtima(rs.getBigDecimal(prefix + "monto_amtima"));		
		reintegroPrestacion.setImporteCoberturaPrestadora(rs.getBigDecimal(prefix + "monto_prestadora"));
		
		reintegroPrestacion.setPrecio_al_publico(rs.getBigDecimal(prefix + "precio_al_publico"));		
		reintegroPrestacion.setPrecio_ospim(rs.getBigDecimal(prefix + "precio_ospim"));
		reintegroPrestacion.setTotalMedicamento(rs.getBigDecimal(prefix + "total_med"));
		reintegroPrestacion.setTotalCobertura(rs.getBigDecimal(prefix + "total_cobertura"));
		reintegroPrestacion.setTotal(rs.getBigDecimal(prefix + "total"));
		reintegroPrestacion.setId_medicamento(rs.getInt(prefix + "id_medicamento"));
		
		reintegroPrestacion.setIdReclamoPrestacional(rs.getInt(prefix + "id_reclamo_prestacional")); 
		reintegroPrestacion.setIdPrestacionReclamo(rs.getInt(prefix + "id_prestacion_reclamo"));
		
		
		reintegroPrestacion.setAlta_fecha(rs
				.getTimestamp(prefix + "alta_fecha"));
		reintegroPrestacion.setAlta_usr(rs.getString(prefix + "alta_usr"));
		reintegroPrestacion.setModi_fecha(rs.getDate(prefix + "modi_fecha"));
		reintegroPrestacion.setModi_usr(rs.getString(prefix + "modi_usr"));
		reintegroPrestacion.setBaja_fecha(rs.getDate(prefix + "baja_fecha"));
		reintegroPrestacion.setBaja_usr(rs.getString(prefix + "baja_usr"));	
		
	
		try {
			reintegroPrestacion.setFechaComprobante(rs.getDate(prefix + "fecha_comprobante"));
			reintegroPrestacion.setCuitEntidad(rs.getString(prefix + "cuit_entidad"));
			reintegroPrestacion.setSucursalEntidad(rs.getString(prefix + "sucursal_entidad"));
			reintegroPrestacion.setFechaPrestacion(rs.getDate(prefix + "fecha_prestacion"));
			reintegroPrestacion.setImporteComprobante(rs.getBigDecimal(prefix + "importe_comprobante"));


		} catch (Exception e) {
			
		}
		
		try {
			reintegroPrestacion.setComproaDebitarTipo(rs.getString(prefix + "compro_a_debitar_tipo"));
			reintegroPrestacion.setComproaDebitarLetra(rs.getString(prefix + "compro_a_debitar_letra"));
			reintegroPrestacion.setComproaDebitarSucursal(rs.getString(prefix + "compro_a_debitar_sucursal"));
			reintegroPrestacion.setComproaDebitarNumero(rs.getString(prefix + "compro_a_debitar_numero"));
			
		} catch (Exception e) {
			
		}
		
		try {
			reintegroPrestacion.setImporteCoberturaImesa(rs.getBigDecimal(prefix + "monto_imesa"));
		}catch (Exception e) {
			
		}
		
		return reintegroPrestacion;
	}
	
}