package ar.com.ospim.tesoreria.beans;

import java.util.Date;

public class Chequera{
	private int id_chequera;
	private int id_cuenta;
	private String descripcion;
	private int nroDesde;
	private int nroHasta;
	private int idCtaBcria;
	private String usuario;
	private Date fechaAlta;
	
	
		
	public Chequera(){}
	
	public Chequera(int cta_bcria, int desde, int hasta){
		this.idCtaBcria=cta_bcria;
		this.nroDesde=desde;
		this.nroHasta=hasta;		
	}
	
	public Chequera(int id_chequera, String desc, int desde, int hasta){
		this.id_chequera=id_chequera;
		this.descripcion=desc;
		this.nroDesde=desde;
		this.nroHasta=hasta;		
	}
	
	public Chequera(int id_chequera, int id_cuenta, String desc){
		this.id_chequera=id_chequera;
		this.id_cuenta=id_cuenta;
		this.descripcion=desc;		
	}
	public Chequera(int id_chequera){
		this.id_chequera=id_chequera;				
	}
	
	public Chequera(int id_chequera, String desc, int desde, int hasta,String usuario, Date fechaAlta){
		this.id_chequera=id_chequera;
		this.descripcion=desc;
		this.nroDesde=desde;
		this.nroHasta=hasta;
		this.usuario = usuario;
		this.fechaAlta = fechaAlta;
	}
	
	public Chequera(int id_chequera, String desc, int desde, int hasta,String usuario, Date fechaAlta,int id_cuenta){
		this.id_chequera=id_chequera;
		this.descripcion=desc;
		this.nroDesde=desde;
		this.nroHasta=hasta;
		this.usuario = usuario;
		this.fechaAlta = fechaAlta;
		this.id_cuenta=id_cuenta;
	}
	
	public int getId_chequera() {
		return id_chequera;
	}

	public void setId_chequera(int idChequera) {
		id_chequera = idChequera;
	}

	public int getId_cuenta() {
		return id_cuenta;
	}

	public void setId_cuenta(int idCuenta) {
		id_cuenta = idCuenta;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public int getNroDesde() {
		return nroDesde;
	}

	public void setNroDesde(int nroDesde) {
		this.nroDesde = nroDesde;
	}

	public int getNroHasta() {
		return nroHasta;
	}

	public void setNroHasta(int nroHasta) {
		this.nroHasta = nroHasta;
	}

	public int getIdCtaBcria() {
		return idCtaBcria;
	}

	public void setIdCtaBcria(int idCtaBcria) {
		this.idCtaBcria = idCtaBcria;
	}

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public Date getFechaAlta() {
		return fechaAlta;
	}

	public void setFechaAlta(Date fechaAlta) {
		this.fechaAlta = fechaAlta;
	}
	
	
	
}
