package ar.com.ospim.procesaArchivos.beans;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FooterNomOSAportes {
	private String tipo_registro; // 2
	private String descripcion_registro;// 4
	private String secuencia_registro; // 2
	private Date fecha_proceso; // 8
	private int cantidad_trf_nominada; // 7
	private BigDecimal importe_trf_nom; // 15
	private String debito_credito1; // 1
	private int cantidad_trf_fdo_rva; // 7
	private BigDecimal importe_trf_fdo_rva;// 15
	private String debito_credito2; // 1
	private int cantidad_trf_anticipo; // 7
	private BigDecimal importe_trf_anticipo; // 15
	private String debito_credito3; // 1
	private BigDecimal saldo_anterior_sin_nominar;// 15

	public FooterNomOSAportes(String line) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		this.tipo_registro = line.substring(0, 2);
		this.descripcion_registro = line.substring(2, 6);
		this.secuencia_registro = line.substring(6, 8);
		this.fecha_proceso = sdf.parse(line.substring(8, 16));
		this.cantidad_trf_nominada = Integer.parseInt(line.substring(16, 23));
		this.importe_trf_nom = new BigDecimal(line.substring(23, 36) + "."
				+ line.substring(36, 38));
		this.debito_credito1 = line.substring(38, 39);
		this.cantidad_trf_fdo_rva = Integer.parseInt(line.substring(39, 46));
		this.importe_trf_fdo_rva = new BigDecimal(line.substring(46, 59) + "."
				+ line.substring(59, 61));
		this.debito_credito2 = line.substring(61, 62);
		this.cantidad_trf_anticipo = Integer.parseInt(line.substring(62, 69));
		this.importe_trf_anticipo = new BigDecimal(line.substring(69, 82) + "."
				+ line.substring(82, 84));
		this.debito_credito3 = line.substring(84, 85);
		this.saldo_anterior_sin_nominar = new BigDecimal(line.substring(85, 98)
				+ "." + line.substring(98, 100));

		if (debito_credito1 != null
				&& debito_credito1.trim().toUpperCase().equals("D")) {
			importe_trf_nom = importe_trf_nom.negate();
		}
		
		if (debito_credito2 != null
				&& debito_credito2.trim().toUpperCase().equals("D")) {
			importe_trf_fdo_rva = importe_trf_fdo_rva.negate();
		}
		if (debito_credito3 != null
				&& debito_credito3.trim().toUpperCase().equals("D")) {
			importe_trf_anticipo = importe_trf_anticipo.negate();
		}
	}

	public String getTipo_registro() {
		return tipo_registro;
	}

	public void setTipo_registro(String tipoRegistro) {
		tipo_registro = tipoRegistro;
	}

	public String getDescripcion_registro() {
		return descripcion_registro;
	}

	public void setDescripcion_registro(String descripcionRegistro) {
		descripcion_registro = descripcionRegistro;
	}

	public String getSecuencia_registro() {
		return secuencia_registro;
	}

	public void setSecuencia_registro(String secuenciaRegistro) {
		secuencia_registro = secuenciaRegistro;
	}

	public Date getFecha_proceso() {
		return fecha_proceso;
	}

	public void setFecha_proceso(Date fechaProceso) {
		fecha_proceso = fechaProceso;
	}

	public int getCantidad_trf_nominada() {
		return cantidad_trf_nominada;
	}

	public void setCantidad_trf_nominada(int cantidadTrfNominada) {
		cantidad_trf_nominada = cantidadTrfNominada;
	}

	public BigDecimal getImporte_trf_nom() {
		return importe_trf_nom;
	}

	public void setImporte_trf_nom(BigDecimal importeTrfNom) {
		importe_trf_nom = importeTrfNom;
	}

	public String getDebito_credito1() {
		return debito_credito1;
	}

	public void setDebito_credito1(String debitoCredito1) {
		debito_credito1 = debitoCredito1;
	}

	public int getCantidad_trf_fdo_rva() {
		return cantidad_trf_fdo_rva;
	}

	public void setCantidad_trf_fdo_rva(int cantidadTrfFdoRva) {
		cantidad_trf_fdo_rva = cantidadTrfFdoRva;
	}

	public BigDecimal getImporte_trf_fdo_rva() {
		return importe_trf_fdo_rva;
	}

	public void setImporte_trf_fdo_rva(BigDecimal importeTrfFdoRva) {
		importe_trf_fdo_rva = importeTrfFdoRva;
	}

	public String getDebito_credito2() {
		return debito_credito2;
	}

	public void setDebito_credito2(String debitoCredito2) {
		debito_credito2 = debitoCredito2;
	}

	public int getCantidad_trf_anticipo() {
		return cantidad_trf_anticipo;
	}

	public void setCantidad_trf_anticipo(int cantidadTrfAnticipo) {
		cantidad_trf_anticipo = cantidadTrfAnticipo;
	}

	public BigDecimal getImporte_trf_anticipo() {
		return importe_trf_anticipo;
	}

	public void setImporte_trf_anticipo(BigDecimal importeTrfAnticipo) {
		importe_trf_anticipo = importeTrfAnticipo;
	}

	public String getDebito_credito3() {
		return debito_credito3;
	}

	public void setDebito_credito3(String debitoCredito3) {
		debito_credito3 = debitoCredito3;
	}

	public BigDecimal getSaldo_anterior_sin_nominar() {
		return saldo_anterior_sin_nominar;
	}

	public void setSaldo_anterior_sin_nominar(BigDecimal saldoAnteriorSinNominar) {
		saldo_anterior_sin_nominar = saldoAnteriorSinNominar;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("\nTIPO REG.: " + this.tipo_registro);
		sb.append("\nDesc.Reg.: " + this.descripcion_registro);
		sb.append("\nSec. Reg.: " + this.secuencia_registro);
		sb.append("\nFecha Proc: " + this.fecha_proceso);
		sb.append("\nCant TRF Nom: " + this.cantidad_trf_nominada);
		sb.append("\nImporte Nom: " + this.importe_trf_nom);
		sb.append("\nDeb/Cred.: " + this.debito_credito1);
		sb.append("\nCant.Fdo.Rva: " + this.cantidad_trf_fdo_rva);
		sb.append("\nImporte Fdo.Rva.: " + this.importe_trf_fdo_rva);
		sb.append("\nDeb/Cred2: " + this.debito_credito2);
		sb.append("\nCant. TRF Ant: " + this.cantidad_trf_anticipo);
		sb.append("\nImporte. TRF Ant: " + this.importe_trf_anticipo);
		sb.append("\nDB/CR 3: " + this.debito_credito3);
		sb.append("Sdo.Ant S/Nom: " + this.saldo_anterior_sin_nominar);
		return sb.toString();
	}

}