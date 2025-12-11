package ar.com.ospim.procesaArchivos.beans.padron;

import java.math.BigInteger;

public class DetallePadronContribuyentes {

	private BigInteger cuit;
	private String razonSocial;
	private String calle;
	private String numero;
	private String piso;
	private String dpto;
	private String localidad;
	private String provincia;
	private String codigoPostal;
	private String codigoOOSS;
	private String COD_ACT_PPAL;
	private String COD_ACT_SEC1;
	private String COD_ACT_SEC2;

	public DetallePadronContribuyentes(String line) {
		
		cuit = new BigInteger(line.substring(0, 11)); // 11
		razonSocial = line.substring(11, 61).trim();// 50
		calle = line.substring(61, 81).trim(); // 20
		numero = line.substring(81, 88).trim();// 7
		piso = line.substring(88, 90).trim();// 2
		dpto = line.substring(90, 93).trim();// 3
		localidad = line.substring(93, 113).trim();// 20
		provincia = line.substring(113, 116).trim(); // 3
		codigoPostal = line.substring(116, 124).trim(); // 8
		codigoOOSS = line.substring(124, 130).trim(); // 6
		COD_ACT_PPAL = line.substring(130, 136).trim(); // 6
		COD_ACT_SEC1 = line.substring(136, 142).trim(); // 6
		COD_ACT_SEC2 = line.substring(142, 148).trim(); // 6
	}

	public BigInteger getCuit() {
		return cuit;
	}

	public void setCuit(BigInteger cuit) {
		this.cuit = cuit;
	}

	public String getRazonSocial() {
		return razonSocial;
	}

	public void setRazonSocial(String razonSocial) {
		this.razonSocial = razonSocial;
	}

	public String getCalle() {
		return calle;
	}

	public void setCalle(String calle) {
		this.calle = calle;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public String getPiso() {
		return piso;
	}

	public void setPiso(String piso) {
		this.piso = piso;
	}

	public String getDpto() {
		return dpto;
	}

	public void setDpto(String dpto) {
		this.dpto = dpto;
	}

	public String getLocalidad() {
		return localidad;
	}

	public void setLocalidad(String localidad) {
		this.localidad = localidad;
	}

	public String getProvincia() {
		return provincia;
	}

	public void setProvincia(String provincia) {
		this.provincia = provincia;
	}

	public String getCodigoPostal() {
		return codigoPostal;
	}

	public void setCodigoPostal(String codigoPostal) {
		this.codigoPostal = codigoPostal;
	}

	public String getCodigoOOSS() {
		return codigoOOSS;
	}

	public void setCodigoOOSS(String codigoOOSS) {
		this.codigoOOSS = codigoOOSS;
	}

	public String getCOD_ACT_PPAL() {
		return COD_ACT_PPAL;
	}

	public void setCOD_ACT_PPAL(String cOD_ACT_PPAL) {
		COD_ACT_PPAL = cOD_ACT_PPAL;
	}

	public String getCOD_ACT_SEC1() {
		return COD_ACT_SEC1;
	}

	public void setCOD_ACT_SEC1(String cOD_ACT_SEC1) {
		COD_ACT_SEC1 = cOD_ACT_SEC1;
	}

	public String getCOD_ACT_SEC2() {
		return COD_ACT_SEC2;
	}

	public void setCOD_ACT_SEC2(String cOD_ACT_SEC2) {
		COD_ACT_SEC2 = cOD_ACT_SEC2;
	}

	
	
}
