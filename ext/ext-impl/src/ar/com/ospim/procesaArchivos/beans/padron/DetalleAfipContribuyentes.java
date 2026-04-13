package ar.com.ospim.procesaArchivos.beans.padron;

import java.math.BigInteger;

public class DetalleAfipContribuyentes {

	private String cuit;
	private String razonSocial;
	private String ganancias;
	private String iva;
	private String monotributo;
	private String integranteSoc;
	private String empleador;
	private String actividadMonotributo;
	
	public DetalleAfipContribuyentes(String line) {
		cuit = line.substring(0, 11); 
		razonSocial = line.substring(11, 41).trim();
		ganancias = line.substring(41, 43).trim();
		iva = line.substring(43, 45).trim();
		monotributo = line.substring(45, 47).trim();
		integranteSoc = line.substring(47,48).trim();
		empleador = line.substring(48,49).trim();
		actividadMonotributo = line.substring(49,51).trim();
	}

	public String getCuit() {
		return cuit;
	}

	public void setCuit(String cuit) {
		this.cuit = cuit;
	}

	public String getRazonSocial() {
		return razonSocial;
	}

	public void setRazonSocial(String razonSocial) {
		this.razonSocial = razonSocial;
	}

	public String getGanancias() {
		return ganancias;
	}

	public void setGanancias(String ganancias) {
		this.ganancias = ganancias;
	}

	public String getIva() {
		return iva;
	}

	public void setIva(String iva) {
		this.iva = iva;
	}

	public String getMonotributo() {
		return monotributo;
	}

	public void setMonotributo(String monotributo) {
		this.monotributo = monotributo;
	}

	public String getIntegranteSoc() {
		return integranteSoc;
	}

	public void setIntegranteSoc(String integranteSoc) {
		this.integranteSoc = integranteSoc;
	}

	public String getEmpleador() {
		return empleador;
	}

	public void setEmpleador(String empleador) {
		this.empleador = empleador;
	}

	public String getActividadMonotributo() {
		return actividadMonotributo;
	}

	public void setActividadMonotributo(String actividadMonotributo) {
		this.actividadMonotributo = actividadMonotributo;
	}

		
	
}
