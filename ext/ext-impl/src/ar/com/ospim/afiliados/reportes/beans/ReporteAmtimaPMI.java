package ar.com.ospim.afiliados.reportes.beans;

import java.text.SimpleDateFormat;
import java.util.Date;


public class ReporteAmtimaPMI {
	
	private Date fechaVto;
	private int id_amtima;
	private int inte;
	private String ape_nom;
	private String seccional;
	private String titular;
	private String empresa;
	
	
	public ReporteAmtimaPMI(Date fechaVto, int id_amtima, int inte, String apeNom, String seccional, String titular, String empresa){
		this.fechaVto=fechaVto;
		this.id_amtima=id_amtima;
		this.inte=inte;
		this.ape_nom=apeNom;
		this.seccional=seccional;
		this.titular=titular;
		this.empresa=empresa;
	}


	public Date getFechaVto() {
		return fechaVto;
	}
	
	public String getFechaVtoAsString() {
		SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");		
		return sdf.format(fechaVto);
	}


	public void setFechaVto(Date fechaVto) {
		this.fechaVto = fechaVto;
	}


	public int getId_amtima() {
		return id_amtima;
	}


	public void setId_amtima(int id_amtima) {
		this.id_amtima = id_amtima;
	}


	public int getInte() {
		return inte;
	}


	public void setInte(int inte) {
		this.inte = inte;
	}


	public String getApe_nom() {
		return ape_nom;
	}


	public void setApe_nom(String ape_nom) {
		this.ape_nom = ape_nom;
	}


	public String getSeccional() {
		return seccional;
	}


	public void setSeccional(String seccional) {
		this.seccional = seccional;
	}


	public String getEmpresa() {
		return empresa;
	}


	public void setEmpresa(String empresa) {
		this.empresa = empresa;
	}


	public String getTitular() {
		return titular;
	}


	public void setTitular(String titular) {
		this.titular = titular;
	}
	
	
	
	
}
