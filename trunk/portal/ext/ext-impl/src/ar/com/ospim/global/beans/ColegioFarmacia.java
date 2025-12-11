package ar.com.ospim.global.beans;

public class ColegioFarmacia {

	private String codigo;
	private String descripcion;

	public ColegioFarmacia(String codigo, String descrip){
		this.codigo=codigo;
		this.descripcion=descrip;
	}
	public String getCodigo() {
		return codigo;
	}
	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	
}