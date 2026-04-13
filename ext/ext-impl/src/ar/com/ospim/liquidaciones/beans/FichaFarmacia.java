package ar.com.ospim.liquidaciones.beans;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class FichaFarmacia {

	private Date fechaReceta;
	private Date periodo;
	private String nroPrestador; 
	private String nombrePrestador; 
	private int codFarmacia; 
	private String farmacia;
	private String receta;
	private String nro_troquel; 
	private String medicamento; 
	private int cantidad; 
	private BigDecimal pvp; 
	private BigDecimal totalOspim; 
	private BigDecimal totalAmtima;
	private String debito;
	private BigDecimal dif_ospim;
	private BigDecimal dif_amtima;
	private float porcentaje_ospim;//	  double precision
	private float porcentaje_amtima;//	  double precision
	private String pmi;
	private int id_ospim;
	private int id_amtima;
	private String cuil_titular;
	private int inte;
	private String nombre_apellido;
	private Date fecha_proceso;
	private int id_orden_pago;
	private Date fecha_op;
	private BigDecimal importe_op;
	private BigDecimal descuento;
	private BigDecimal descuento_por_drogueria;
	private BigDecimal nro_cheque;
	private BigDecimal importe_cheque;
	private String liquida;
	private String anticipos;
	private String plan;
	private String discapacitado;
	private String seccional;
	
	public static FichaFarmacia getMapping(ResultSet rs) throws SQLException{
		FichaFarmacia rdsab= new FichaFarmacia();
		rdsab.setFechaReceta(rs.getDate("fecha_receta"));
		rdsab.setPeriodo(rs.getDate("periodo"));
		rdsab.setNroPrestador(rs.getString("nro_prestador"));
		rdsab.setNombrePrestador(rs.getString("prestador"));
		rdsab.setCodFarmacia(rs.getInt("cod_farmacia"));
		rdsab.setFarmacia(rs.getString("farmacia"));
		rdsab.setReceta(rs.getString("receta"));
		rdsab.setNro_troquel(rs.getString("nro_troquel"));
		rdsab.setMedicamento(rs.getString("medicamento"));
		rdsab.setCantidad(rs.getInt("cantidad"));
		rdsab.setPvp(rs.getBigDecimal("pvp"));
		rdsab.setTotalOspim(rs.getBigDecimal("total_ospim"));
		rdsab.setTotalAmtima(rs.getBigDecimal("total_amtima"));
		rdsab.setDebito(rs.getString("debito"));
		rdsab.setDif_ospim(rs.getBigDecimal("dif_ospim"));
		rdsab.setDif_amtima(rs.getBigDecimal("dif_amtima"));
		rdsab.setPorcentaje_ospim(rs.getFloat("porcentaje_ospim"));
		rdsab.setPorcentaje_amtima(rs.getFloat("porcentaje_amtima"));
		rdsab.setPmi(rs.getString("pmi"));
		rdsab.setId_ospim(rs.getInt("id_ospim"));
		rdsab.setId_amtima(rs.getInt("id_amtima"));
		rdsab.setCuil_titular(rs.getString("cuil_titular"));
		rdsab.setInte(rs.getInt("inte"));
		rdsab.setNombre_apellido(rs.getString("nombre_apellido"));
		rdsab.setFecha_proceso(rs.getDate("fecha_proceso"));
		rdsab.setId_orden_pago(rs.getInt("id_orden_pago"));
		rdsab.setFecha_op(rs.getDate("fecha_op"));
		rdsab.setImporte_op(rs.getBigDecimal("importe_op"));
		rdsab.setDescuento(rs.getBigDecimal("descuento"));
		rdsab.setDescuento_por_drogueria(rs.getBigDecimal("descuento_por_drogueria"));
		rdsab.setNro_cheque(rs.getBigDecimal("nro_cheque"));
		rdsab.setImporte_cheque(rs.getBigDecimal("importe_cheque"));
		rdsab.setLiquida(rs.getString("liquida"));
		rdsab.setAnticipos(rs.getString("anticipos"));
		rdsab.setPlan(rs.getString("plan"));
		rdsab.setDiscapacitado(rs.getString("discapacitado"));		
		rdsab.setSeccional(rs.getString("seccional"));
		return rdsab;
	}	
	
	public Date getFechaReceta() {
		return fechaReceta;
	}
	public void setFechaReceta(Date fechaReceta) {
		this.fechaReceta = fechaReceta;
	}
	public Date getPeriodo() {
		return periodo;
	}
	public void setPeriodo(Date periodo) {
		this.periodo = periodo;
	}
	public String getNroPrestador() {
		return nroPrestador;
	}
	public void setNroPrestador(String nroPrestador) {
		this.nroPrestador = nroPrestador;
	}
	public String getNombrePrestador() {
		return nombrePrestador;
	}
	public void setNombrePrestador(String nombrePrestador) {
		this.nombrePrestador = nombrePrestador;
	}
	public int getCodFarmacia() {
		return codFarmacia;
	}
	public void setCodFarmacia(int codFarmacia) {
		this.codFarmacia = codFarmacia;
	}
	public String getFarmacia() {
		return farmacia;
	}
	public void setFarmacia(String farmacia) {
		this.farmacia = farmacia;
	}
	public String getReceta() {
		return receta;
	}
	public void setReceta(String receta) {
		this.receta = receta;
	}
	public String getNro_troquel() {
		return nro_troquel;
	}
	public void setNro_troquel(String nro_troquel) {
		this.nro_troquel = nro_troquel;
	}
	public String getMedicamento() {
		return medicamento;
	}
	public void setMedicamento(String medicamento) {
		this.medicamento = medicamento;
	}
	public int getCantidad() {
		return cantidad;
	}
	public void setCantidad(int cantidad) {
		this.cantidad = cantidad;
	}
	public BigDecimal getPvp() {
		return pvp;
	}
	public void setPvp(BigDecimal pvp) {
		this.pvp = pvp;
	}
	public BigDecimal getTotalOspim() {
		return totalOspim;
	}
	public void setTotalOspim(BigDecimal totalOspim) {
		this.totalOspim = totalOspim;
	}
	public BigDecimal getTotalAmtima() {
		return totalAmtima;
	}
	public void setTotalAmtima(BigDecimal totalAmtima) {
		this.totalAmtima = totalAmtima;
	}

	public String getDebito() {
		return debito;
	}

	public BigDecimal getDif_ospim() {
		return dif_ospim;
	}

	public BigDecimal getDif_amtima() {
		return dif_amtima;
	}

	public float getPorcentaje_ospim() {
		return porcentaje_ospim;
	}

	public float getPorcentaje_amtima() {
		return porcentaje_amtima;
	}

	public String getPmi() {
		return pmi;
	}

	public int getId_ospim() {
		return id_ospim;
	}

	public int getId_amtima() {
		return id_amtima;
	}

	public String getCuil_titular() {
		return cuil_titular;
	}

	public int getInte() {
		return inte;
	}

	public String getNombre_apellido() {
		return nombre_apellido;
	}

	public Date getFecha_proceso() {
		return fecha_proceso;
	}

	public int getId_orden_pago() {
		return id_orden_pago;
	}

	public Date getFecha_op() {
		return fecha_op;
	}

	public BigDecimal getImporte_op() {
		return importe_op;
	}

	public BigDecimal getDescuento() {
		return descuento;
	}

	public BigDecimal getDescuento_por_drogueria() {
		return descuento_por_drogueria;
	}

	public BigDecimal getNro_cheque() {
		return nro_cheque;
	}

	public BigDecimal getImporte_cheque() {
		return importe_cheque;
	}

	public String getLiquida() {
		return liquida;
	}

	public void setDebito(String debito) {
		this.debito = debito;
	}

	public void setDif_ospim(BigDecimal dif_ospim) {
		this.dif_ospim = dif_ospim;
	}

	public void setDif_amtima(BigDecimal dif_amtima) {
		this.dif_amtima = dif_amtima;
	}

	public void setPorcentaje_ospim(float porcentaje_ospim) {
		this.porcentaje_ospim = porcentaje_ospim;
	}

	public void setPorcentaje_amtima(float porcentaje_amtima) {
		this.porcentaje_amtima = porcentaje_amtima;
	}

	public void setPmi(String pmi) {
		this.pmi = pmi;
	}

	public void setId_ospim(int id_ospim) {
		this.id_ospim = id_ospim;
	}

	public void setId_amtima(int id_amtima) {
		this.id_amtima = id_amtima;
	}

	public void setCuil_titular(String cuil_titular) {
		this.cuil_titular = cuil_titular;
	}

	public void setInte(int inte) {
		this.inte = inte;
	}

	public void setNombre_apellido(String nombre_apellido) {
		this.nombre_apellido = nombre_apellido;
	}

	public void setFecha_proceso(Date fecha_proceso) {
		this.fecha_proceso = fecha_proceso;
	}

	public void setId_orden_pago(int id_orden_pago) {
		this.id_orden_pago = id_orden_pago;
	}

	public void setFecha_op(Date fecha_op) {
		this.fecha_op = fecha_op;
	}

	public void setImporte_op(BigDecimal importe_op) {
		this.importe_op = importe_op;
	}

	public void setDescuento(BigDecimal descuento) {
		this.descuento = descuento;
	}

	public void setDescuento_por_drogueria(BigDecimal descuento_por_drogueria) {
		this.descuento_por_drogueria = descuento_por_drogueria;
	}

	public void setNro_cheque(BigDecimal nro_cheque) {
		this.nro_cheque = nro_cheque;
	}

	public void setImporte_cheque(BigDecimal importe_cheque) {
		this.importe_cheque = importe_cheque;
	}

	public void setLiquida(String liquida) {
		this.liquida = liquida;
	}

	public String getAnticipos() {
		return anticipos;
	}

	public void setAnticipos(String anticipos) {
		this.anticipos = anticipos;
	}

	public String getPlan() {
		return plan;
	}

	public void setPlan(String plan) {
		this.plan = plan;
	}

	public String getDiscapacitado() {
		return discapacitado;
	}

	public void setDiscapacitado(String discapacitado) {
		this.discapacitado = discapacitado;
	}

	public String getSeccional() {
		return seccional;
	}

	public void setSeccional(String seccional) {
		this.seccional = seccional;
	}
	
	
}