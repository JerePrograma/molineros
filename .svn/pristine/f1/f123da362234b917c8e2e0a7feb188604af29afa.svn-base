package ar.com.ospim.procesaArchivos.beans.nacion;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RendicionNacion {

	private BigInteger ente;
	private int suc_origen;
	private int suc_bcra;
	private Date fecha_recauda;
	private Date fecha_rendicion;
	private String cod_movimiento;
	private int nro_movimiento;
	private BigDecimal importe;
	private String cod_barras;
	private int bco_cheque;
	private int suc_cheque;
	private int nro_cheque;
	private String estado_cheque;
	private String cuit;
	private Date periodo_cod_barras;
	private int nro_dec_portal_emple;
	private int nro_boleta_portal_emple;
	private int tipo_boleta;
	
	public RendicionNacion() {}

	public RendicionNacion(String line) {
		this.ente = new BigInteger(line.substring(0, 10)); // 11
		this.suc_origen = Integer.parseInt(line.substring(10,14));
		this.suc_bcra = Integer.parseInt(line.substring(14,18));
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		try{
			this.fecha_recauda= sdf.parse(line.substring(18,26));
			this.fecha_rendicion= sdf.parse(line.substring(26,34));
			sdf=new SimpleDateFormat("MMyyyy");
			this.periodo_cod_barras=sdf.parse(line.substring(73,79));
		}catch(ParseException pe){			
		}
		this.cod_movimiento=line.substring(34,36);
		this.nro_movimiento=Integer.parseInt(line.substring(36,42));
		this.importe= new BigDecimal(line.substring(42,55)+"."+line.substring(55,57));
		this.cod_barras= line.substring(58,138);
		this.cuit=line.substring(62,73);
		this.nro_dec_portal_emple=Integer.parseInt(line.substring(79, 81));
		this.nro_boleta_portal_emple=Integer.parseInt(line.substring(81, 85));
		this.tipo_boleta=Integer.parseInt(line.substring(87, 88));
		this.bco_cheque= Integer.parseInt(line.substring(138,142));
		this.suc_cheque= Integer.parseInt(line.substring(142,146));
		this.nro_cheque= Integer.parseInt(line.substring(146,154));
		try {
		    this.estado_cheque=line.substring(154,155);
		}catch(Exception e) {
			this.estado_cheque="";
		}
	}


	public BigInteger getEnte() {
		return ente;
	}


	public void setEnte(BigInteger ente) {
		this.ente = ente;
	}


	public int getSuc_origen() {
		return suc_origen;
	}


	public void setSuc_origen(int suc_origen) {
		this.suc_origen = suc_origen;
	}


	public int getSuc_bcra() {
		return suc_bcra;
	}


	public void setSuc_bcra(int suc_bcra) {
		this.suc_bcra = suc_bcra;
	}


	public Date getFecha_recauda() {
		return fecha_recauda;
	}


	public void setFecha_recauda(Date fecha_recauda) {
		this.fecha_recauda = fecha_recauda;
	}


	public Date getFecha_rendicion() {
		return fecha_rendicion;
	}


	public void setFecha_rendicion(Date fecha_rendicion) {
		this.fecha_rendicion = fecha_rendicion;
	}


	public String getCod_movimiento() {
		return cod_movimiento;
	}


	public void setCod_movimiento(String cod_movimiento) {
		this.cod_movimiento = cod_movimiento;
	}


	public int getNro_movimiento() {
		return nro_movimiento;
	}


	public void setNro_movimiento(int nro_movimiento) {
		this.nro_movimiento = nro_movimiento;
	}


	public BigDecimal getImporte() {
		return importe;
	}


	public void setImporte(BigDecimal importe) {
		this.importe = importe;
	}


	public String getCod_barras() {
		return cod_barras;
	}


	public void setCod_barras(String cod_barras) {
		this.cod_barras = cod_barras;
	}


	public int getBco_cheque() {
		return bco_cheque;
	}


	public void setBco_cheque(int bco_cheque) {
		this.bco_cheque = bco_cheque;
	}


	public int getSuc_cheque() {
		return suc_cheque;
	}


	public void setSuc_cheque(int suc_cheque) {
		this.suc_cheque = suc_cheque;
	}


	public int getNro_cheque() {
		return nro_cheque;
	}


	public void setNro_cheque(int nro_cheque) {
		this.nro_cheque = nro_cheque;
	}


	public String getEstado_cheque() {
		return estado_cheque;
	}


	public void setEstado_cheque(String estado_cheque) {
		this.estado_cheque = estado_cheque;
	}


	public String getCuit() {
		return cuit;
	}


	public void setCuit(String cuit) {
		this.cuit = cuit;
	}


	public Date getPeriodo_cod_barras() {
		return periodo_cod_barras;
	}


	public void setPeriodo_cod_barras(Date periodo_cod_barras) {
		this.periodo_cod_barras = periodo_cod_barras;
	}


	public int getNro_dec_portal_emple() {
		return nro_dec_portal_emple;
	}


	public void setNro_dec_portal_emple(int nro_dec_portal_emple) {
		this.nro_dec_portal_emple = nro_dec_portal_emple;
	}


	public int getNro_boleta_portal_emple() {
		return nro_boleta_portal_emple;
	}


	public void setNro_boleta_portal_emple(int nro_boleta_portal_emple) {
		this.nro_boleta_portal_emple = nro_boleta_portal_emple;
	}


	public int getTipo_boleta() {
		return tipo_boleta;
	}


	public void setTipo_boleta(int tipo_boleta) {
		this.tipo_boleta = tipo_boleta;
	}
	
	
	
	


}
