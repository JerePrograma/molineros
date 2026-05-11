package ar.com.ospim.liquidaciones.beans;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.liferay.portlet.documentlibrary.model.impl.DLFileEntryImpl;

import ar.com.ospim.global.beans.Comprobante;
import ar.com.ospim.global.beans.ComprobanteItem;
import ar.com.ospim.util.DateUtils;

/**
 * @author sistema-09
 * @version 1.0
 * @created 13-Sep-2010 04:30:50 p.m.
 */
public class Liquidacion {

	private int id_liquidacion;
	private int id_prestador;
	private int id_domicilio;
	private Date fecha;
	private Date periodo;
	private int estado; 
	private String entidad;
	private String compro_a_debitar_tipo;
	private String compro_a_debitar_letra;
	private int sucu;
	private String compro_a_debitar_numero;
	private Date fecha_emitido;
	private Date fecha_recibido;
	private Date fecha_vencimiento;
	private PrestadorLugarAtencion prestador_lugar_atencion;
	private List<LiquidacionPrestacion> liquidacionPrestacion;
	private Date baja_fecha;
	private String baja_usr;
	private Date alta_fecha;
	private String alta_usr;
	private Date modi_fecha;
	private String modi_usr;
	private String tipo_liquidacion;
	private BigDecimal importe_total;		
	private int idOP;
	private BigInteger chequeOP;
	private Date fechaOP;
	private BigDecimal importe;
	private BigDecimal debitado;
	private String observaciones;
	private String tercerizado;
	private List <ComprobanteItem> debitos;
	private Comprobante comprobante;
	private boolean op_baja_existente = false;
	private int idOC;
	private BigDecimal cargoOspim;
	private BigDecimal cargoPS;
	private BigDecimal cargoOmint;
	private BigDecimal cargoEnSalud;
	private BigDecimal cargoCemic;
	private BigDecimal cargoImesa;
	private BigDecimal cargoCES;
	private List<DLFileEntryImpl> imagenes;
	private Integer diasTranscurridos;
	
	
	public Liquidacion(){

	}

	/**
	 * @return the id_reintegro
	 */
	
	public Liquidacion (Date fecha, Date periodo, int id_liquidacion, Date fecha_baja, String usr_baja, String compro_a_debitar_tipo, 
			String compro_a_debitar_letra, int sucu, String compro_a_debitar_numero) {
		this.fecha = fecha;
		this.periodo = periodo;		
		this.id_liquidacion = id_liquidacion;
		this.baja_fecha = fecha_baja;
		this.baja_usr = usr_baja;		
		this.compro_a_debitar_tipo = compro_a_debitar_tipo;		
		this.compro_a_debitar_letra = compro_a_debitar_letra;
		this.sucu = sucu;
		this.compro_a_debitar_numero = compro_a_debitar_numero;		
	}
	/**
	 * @return the id_reintegro
	 */
	
	public Liquidacion (Date fecha, Date periodo, int id_liquidacion, Date fecha_baja, String usr_baja, String compro_a_debitar_tipo, 
			String compro_a_debitar_letra, int sucu, String compro_a_debitar_numero, int id_orden_pago_ospim, BigInteger nro_cheque, Date fecha_op, 
			BigDecimal importe, BigDecimal debitado, String observaciones, int estado, int id_orden_compra) {
		this.fecha = fecha;
		this.periodo = periodo;		
		this.id_liquidacion = id_liquidacion;
		this.baja_fecha = fecha_baja;
		this.baja_usr = usr_baja;		
		this.compro_a_debitar_tipo = compro_a_debitar_tipo;		
		this.compro_a_debitar_letra = compro_a_debitar_letra;
		this.sucu = sucu;
		this.compro_a_debitar_numero = compro_a_debitar_numero;
		this.idOP = id_orden_pago_ospim;
		this.chequeOP = nro_cheque;
		this.fechaOP = fecha_op;
		this.importe = importe;
		this.debitado = debitado;
		this.observaciones = observaciones;
		this.estado = estado;
		this.idOC = id_orden_compra;
	}
	
	public Liquidacion (int idLiquidacion){
		this.id_liquidacion=idLiquidacion;
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
	 * @return the id_prestador
	 */
	public int getId_prestador() {
		return id_prestador;
	}

	/**
	 * @param idPrestador the id_prestador to set
	 */
	public void setId_prestador(int idPrestador) {
		id_prestador = idPrestador;
	}

	/**
	 * @return the id_domicilio
	 */
	public int getId_domicilio() {
		return id_domicilio;
	}

	/**
	 * @param idDomicilio the id_domicilio to set
	 */
	public void setId_domicilio(int idDomicilio) {
		id_domicilio = idDomicilio;
	}

	/**
	 * @return the fecha
	 */
	public Date getFecha() {
		return fecha;
	}

	
	public String getFechaAsString() {
		return null!=fecha?DateUtils.format(fecha,DateUtils.SHORT):"";
	}

	/**
	 * @param fecha the fecha to set
	 */
	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	/**
	 * @return the periodo
	 */
	public Date getPeriodo() {
		return periodo;
	}

	/**
	 * @return the periodo
	 */
	public String getPeriodoString() {
		return null!=periodo?DateUtils.format(periodo,DateUtils.PERIODO):"";
	}

	/**
	 * @param periodo the periodo to set
	 */
	public void setPeriodo(Date periodo) {
		this.periodo = periodo;
	}

	/**
	 * @return the estado
	 */
	public int getEstado() {
		return estado;
	}

	/**
	 * @param estado the estado to set
	 */
	public void setEstado(int estado) {
		this.estado = estado;
	}

	/**
	 * @return the entidad
	 */
	public String getEntidad() {
		return entidad;
	}

	/**
	 * @param entidad the entidad to set
	 */
	public void setEntidad(String entidad) {
		this.entidad = entidad;
	}

	/**
	 * @return the compro_a_debitar_tipo
	 */
	public String getCompro_a_debitar_tipo() {
		return compro_a_debitar_tipo;
	}

	/**
	 * @param comproADebitarTipo the compro_a_debitar_tipo to set
	 */
	public void setCompro_a_debitar_tipo(String comproADebitarTipo) {
		compro_a_debitar_tipo = comproADebitarTipo;
	}

	/**
	 * @return the compro_a_debitar_letra
	 */
	public String getCompro_a_debitar_letra() {
		return compro_a_debitar_letra;
	}

	/**
	 * @param comproADebitarLetra the compro_a_debitar_letra to set
	 */
	public void setCompro_a_debitar_letra(String comproADebitarLetra) {
		compro_a_debitar_letra = comproADebitarLetra;
	}

	/**
	 * @return the sucu
	 */
	public int getSucu() {
		return sucu;
	}

	/**
	 * @param sucu the sucu to set
	 */
	public void setSucu(int sucu) {
		this.sucu = sucu;
	}

	/**
	 * @return the compro_a_debitar_numero
	 */
	public String getCompro_a_debitar_numero() {
		return compro_a_debitar_numero;
	}

	/**
	 * @param comproADebitarNumero the compro_a_debitar_numero to set
	 */
	public void setCompro_a_debitar_numero(String comproADebitarNumero) {
		compro_a_debitar_numero = comproADebitarNumero;
	}

	/**
	 * @return the fecha_emitido
	 */
	public Date getFecha_emitido() {
		return fecha_emitido;
	}

	/**
	 * @param fechaEmitido the fecha_emitido to set
	 */
	public void setFecha_emitido(Date fechaEmitido) {
		fecha_emitido = fechaEmitido;
	}

	/**
	 * @return the fecha_recibido
	 */
	public Date getFecha_recibido() {
		return fecha_recibido;
	}

	/**
	 * @param fechaRecibido the fecha_recibido to set
	 */
	public void setFecha_recibido(Date fechaRecibido) {
		fecha_recibido = fechaRecibido;
	}

	/**
	 * @return the fecha_vencimiento
	 */
	public Date getFecha_vencimiento() {
		return fecha_vencimiento;
	}

	/**
	 * @param fechaVencimiento the fecha_vencimiento to set
	 */
	public void setFecha_vencimiento(Date fechaVencimiento) {
		fecha_vencimiento = fechaVencimiento;
	}

	/**
	 * @return the prestador_lugar_atencion
	 */
	public PrestadorLugarAtencion getPrestador_lugar_atencion() {
		return prestador_lugar_atencion;
	}

	/**
	 * @param prestadorLugarAtencion the prestador_lugar_atencion to set
	 */
	public void setPrestador_lugar_atencion(
			PrestadorLugarAtencion prestadorLugarAtencion) {
		prestador_lugar_atencion = prestadorLugarAtencion;
	}

	/**
	 * @return the liquidacionPrestacion
	 */
	public List<LiquidacionPrestacion> getLiquidacionPrestacion() {
		return liquidacionPrestacion;
	}

	/**
	 * @param liquidacionPrestacion the liquidacionPrestacion to set
	 */
	public void setLiquidacionPrestacion(List< ? extends LiquidacionPrestacion> liquidacionPrestacionList) {
		liquidacionPrestacion  = new ArrayList<LiquidacionPrestacion>();
		if (liquidacionPrestacionList != null){
			this.liquidacionPrestacion.addAll(liquidacionPrestacionList);
		}
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

	/**
	 * @return the tipo_liquidacion
	 */
	public String getTipo_liquidacion() {
		return tipo_liquidacion;
	}

	/**
	 * @param tipoLiquidacion the tipo_liquidacion to set
	 */
	public void setTipo_liquidacion(String tipoLiquidacion) {
		tipo_liquidacion = tipoLiquidacion;
	}
	
	public BigDecimal getImporteTotal(){
		BigDecimal total = new BigDecimal(0);
		if (liquidacionPrestacion != null){
			for (LiquidacionPrestacion lPrest : liquidacionPrestacion){
				total =  total.add(lPrest.getImporteTotal());
			}
		}
		setImporte_total(total);
		return total;
	}	

	public BigDecimal getImporteTotalNoEstadistico(){
		BigDecimal totalNE = new BigDecimal(0);
		if (liquidacionPrestacion != null){
			for (LiquidacionPrestacion lPrest : liquidacionPrestacion){
				if (lPrest.getTercerizado().equals("0")) { //1 estadístico
					totalNE =  totalNE.add(lPrest.getImporteTotal());
				}
			}
		}
		return totalNE;
	}

	public BigDecimal getCargoImesa() {
		return cargoImesa;
	}

	public void setCargoImesa(BigDecimal cargoImesa) {
		this.cargoImesa = cargoImesa;
	}

	public static Liquidacion getMapping(ResultSet rs, String prefix) throws SQLException {
		Liquidacion liquidacion = new Liquidacion();
		liquidacion.setId_liquidacion(rs.getInt(prefix+"id_liquidacion"));
		liquidacion.setId_prestador(rs.getInt(prefix+"id_prestador"));
		liquidacion.setFecha(rs.getDate(prefix+"fecha")); 
		liquidacion.setPeriodo(rs.getDate(prefix+"periodo"));
		liquidacion.setEstado(rs.getInt(prefix+"estado"));
		liquidacion.setEntidad(rs.getString(prefix+"entidad"));
		liquidacion.setCompro_a_debitar_tipo(rs.getString(prefix+"compro_a_debitar_tipo"));
		liquidacion.setCompro_a_debitar_letra(rs.getString(prefix+"compro_a_debitar_letra"));
		liquidacion.setSucu(rs.getInt(prefix+"sucu"));
		liquidacion.setCompro_a_debitar_numero(rs.getString(prefix+"compro_a_debitar_numero"));
		liquidacion.setFecha_emitido(rs.getDate(prefix+"fecha_emitido"));
		liquidacion.setFecha_recibido(rs.getDate(prefix+"fecha_recibido"));
		liquidacion.setFecha_vencimiento(rs.getDate(prefix+"fecha_vencimiento"));
		liquidacion.setAlta_fecha(rs.getDate(prefix+"alta_fecha")); 
		liquidacion.setAlta_usr(rs.getString(prefix+"alta_usr")); 
		liquidacion.setModi_fecha(rs.getDate(prefix+"modi_fecha")); 
		liquidacion.setModi_usr(rs.getString(prefix+"modi_usr")); 
		liquidacion.setBaja_fecha(rs.getDate(prefix+"baja_fecha")); 
		liquidacion.setBaja_usr(rs.getString(prefix+"baja_usr")); 
		liquidacion.setTipo_liquidacion(rs.getString(prefix+"tipo_liquidacion"));				
		liquidacion.setDebitado(rs.getBigDecimal(prefix+"debitado"));
		liquidacion.setImporte(rs.getBigDecimal(prefix+"importe"));
		liquidacion.setIdOC(rs.getInt(prefix+"id_orden_compra"));
		liquidacion.setObservaciones(rs.getString(prefix+"observaciones"));
		liquidacion.setTercerizado(rs.getString(prefix+"tercerizado"));
		liquidacion.setCargoOspim(rs.getBigDecimal(prefix+"cargo_ospim"));
		liquidacion.setCargoPS(rs.getBigDecimal(prefix+"cargo_ps"));
		liquidacion.setCargoOmint(rs.getBigDecimal(prefix+"cargo_omint"));
		liquidacion.setCargoEnSalud(rs.getBigDecimal(prefix+"cargo_ensalud"));
		liquidacion.setCargoCemic(rs.getBigDecimal(prefix+"cargo_cemic"));
		liquidacion.setCargoImesa(rs.getBigDecimal(prefix+"cargo_imesa"));
		liquidacion.setCargoCES(rs.getBigDecimal(prefix+"cargo_ces"));
		return liquidacion;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id_liquidacion;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Liquidacion other = (Liquidacion) obj;
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
	 * @return the idOP
	 */
	public int getIdOP() {
		return idOP;
	}

	/**
	 * @param idOP the idOP to set
	 */
	public void setIdOP(int idOP) {
		this.idOP = idOP;
	}

	/**
	 * @return the chequeOP
	 */
	public BigInteger getChequeOP() {
		return chequeOP;
	}

	/**
	 * @param chequeOP the chequeOP to set
	 */
	public void setChequeOP(BigInteger chequeOP) {
		this.chequeOP = chequeOP;
	}

	/**
	 * @return the fechaOP
	 */
	public Date getFechaOP() {
		return fechaOP;
	}

	/**
	 * @param fechaOP the fechaOP to set
	 */
	public void setFechaOP(Date fechaOP) {
		this.fechaOP = fechaOP;
	}
	
	public String getOPLiquidacion () {
		StringBuffer sb = new StringBuffer("");
		sb.append(this.idOP != 0 ? this.idOP + " / " : "" );
		sb.append(this.chequeOP != null && this.chequeOP.intValue() != 0 ? this.chequeOP.toString() + " / " : "");
		sb.append(this.fechaOP != null ? this.fechaOP.toString() : "");
		return sb.toString();
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
	 * @return the debitado
	 */
	public BigDecimal getDebitado() {
		return debitado;
	}

	/**
	 * @param debitado the debitado to set
	 */
	public void setDebitado(BigDecimal debitado) {
		this.debitado = debitado;
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
	 * @return the tercerizado
	 */
	public String getTercerizado() {
		return tercerizado;
	}

	/**
	 * @return true if tercerizado
	 */
	public boolean esTercerizado() {
		return tercerizado != null && tercerizado.equals("1") ? true : false;
	}
	
	/**
	 * @param tercerizado the tercerizado to set
	 */
	public void setTercerizado(String tercerizado) {
		this.tercerizado = tercerizado;
	}

	/**
	 * @return the debitos
	 */
	public List<ComprobanteItem> getDebitos() {
		return debitos;
	}

	/**
	 * @param debitos the debitos to set
	 */
	public void setDebitos(List<ComprobanteItem> debitos) {
		this.debitos = debitos;
	}

	/**
	 * @return the comprobante
	 */
	public Comprobante getComprobante() {
		return comprobante;
	}

	/**
	 * @param comprobante the comprobante to set
	 */
	public void setComprobante(Comprobante comprobante) {
		this.comprobante = comprobante;
	}

	/**
	 * @return the op_baja_existente
	 */
	public boolean isOp_baja_existente() {
		return op_baja_existente;
	}

	/**
	 * @param opBajaExistente the op_baja_existente to set
	 */
	public void setOp_baja_existente(boolean opBajaExistente) {
		op_baja_existente = opBajaExistente;
	}

	public int getIdOC() {
		return idOC;
	}

	public void setIdOC(int idOC) {
		this.idOC = idOC;
	}
	
	public BigDecimal getCargoOspim() {
		return cargoOspim;
	}

	

	public void setCargoOspim(BigDecimal cargoOspim) {
		this.cargoOspim = cargoOspim;
	}

	public BigDecimal getCargoPS() {
		return cargoPS;
	}

	public void setCargoPS(BigDecimal cargoPS) {
		this.cargoPS = cargoPS;
	}

	public BigDecimal getCargoOmint() {
		return cargoOmint;
	}

	public void setCargoOmint(BigDecimal cargoOmint) {
		this.cargoOmint = cargoOmint;
	}

	public BigDecimal getCargoEnSalud() {
		return cargoEnSalud;
	}

	public void setCargoEnSalud(BigDecimal cargoEnSalud) {
		this.cargoEnSalud = cargoEnSalud;
	}

	public BigDecimal getCargoCemic() {
		return cargoCemic;
	}

	public void setCargoCemic(BigDecimal cargoCemic) {
		this.cargoCemic = cargoCemic;
	}

	public List<DLFileEntryImpl> getImagenes() {
		return imagenes;
	}

	public void setImagenes(List<DLFileEntryImpl> imagenes) {
		this.imagenes = imagenes;
	}

	public Integer getDiasTranscurridos() {
		return diasTranscurridos;
	}

	public void setDiasTranscurridos(Integer diasTranscurridos) {
		this.diasTranscurridos = diasTranscurridos;
	}

	public BigDecimal getCargoCES() {
		return cargoCES;
	}

	public void setCargoCES(BigDecimal cargoCES) {
		this.cargoCES = cargoCES;
	}
	
	
}