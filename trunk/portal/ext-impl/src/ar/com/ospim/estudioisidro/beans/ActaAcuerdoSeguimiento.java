package ar.com.ospim.estudioisidro.beans;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class ActaAcuerdoSeguimiento implements Serializable {	
		
	private static final long serialVersionUID = 1L;
	private int entidad;
	private String tipo;
	private int id;
	private String numero;
	private Date cierreFecha;
	private BigDecimal total;
	private BigDecimal interes;
	private BigDecimal capital;
	private String periodos;
	private BigDecimal saldo;
	private String convenioPago;
			
	public ActaAcuerdoSeguimiento() {
	}
	
	public static ActaAcuerdoSeguimiento getMapping(ResultSet rs) throws SQLException {
		ActaAcuerdoSeguimiento acAc=new ActaAcuerdoSeguimiento();		
		acAc.setEntidad(rs.getInt("entidad"));
		acAc.setTipo(rs.getString("tipo"));
		acAc.setId(rs.getInt("acta_id"));
		acAc.setNumero(rs.getString("numero"));
		acAc.setCierreFecha(rs.getDate("cierre_fecha"));
		acAc.setTotal(rs.getBigDecimal("total"));
		acAc.setInteres(rs.getBigDecimal("interes"));
		acAc.setCapital(rs.getBigDecimal("capital"));
		acAc.setPeriodos(rs.getString("periodos"));
		acAc.setSaldo(rs.getBigDecimal("saldo"));
		acAc.setConvenioPago(rs.getString("convenio_pago"));
		return acAc;
	}

	public int getEntidad() {
		return entidad;
	}

	public void setEntidad(int entidad) {
		this.entidad = entidad;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public Date getCierreFecha() {
		return cierreFecha;
	}
	
	public String getCierreFechaAsString() {
		if(cierreFecha!=null){
			SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");
			return sdf.format(cierreFecha);
		}else{		
			return "";
		}
	}

	public void setCierreFecha(Date cierreFecha) {
		this.cierreFecha = cierreFecha;
	}

	public BigDecimal getTotal() {
		return total;
	}

	public void setTotal(BigDecimal total) {
		this.total = total;
	}

	public BigDecimal getInteres() {
		return interes;
	}

	public void setInteres(BigDecimal interes) {
		this.interes = interes;
	}

	public BigDecimal getCapital() {
		return capital;
	}

	public void setCapital(BigDecimal capital) {
		this.capital = capital;
	}

	public String getPeriodos() {
		return periodos;
	}

	public void setPeriodos(String periodos) {
		this.periodos = periodos;
	}

	public BigDecimal getSaldo() {
		return saldo;
	}

	public void setSaldo(BigDecimal saldo) {
		this.saldo = saldo;
	}

	public String getConvenioPago() {
		return convenioPago;
	}

	public void setConvenioPago(String convenioPago) {
		this.convenioPago = convenioPago;
	}
	

	public String getEstado(){
		if(null!=this.convenioPago){
			return "Pagado por Acuerdo";
		}
		if(null!=this.getSaldo()&& this.getSaldo().compareTo(BigDecimal.ZERO)<=0){
			if(this.getTipo().equalsIgnoreCase("ACTA")){
				return "Pagado"; // si es acta es Pagado si es Conv es cumplido.
			}else{
				return "Cumplido";
			}
		}else if(this.getSaldo()!=null && this.getSaldo().compareTo(BigDecimal.ZERO)>0){
			return "Pendiente";
		}
		return "no se pudo definir estado";
	}
	
			
}
