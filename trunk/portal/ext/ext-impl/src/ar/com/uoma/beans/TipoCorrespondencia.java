package ar.com.uoma.beans;

public class TipoCorrespondencia {
	private int idTipo;
	private String descripcion;
	
	public TipoCorrespondencia(){}
	
	public TipoCorrespondencia(int id, String descripcion){
		this.idTipo=id;
		this.descripcion=descripcion;		
	}
	
	public TipoCorrespondencia(int id){
		this.idTipo=id;				
	}
	
	public TipoCorrespondencia(String id){
		this.descripcion=id;				
	}
	
	public int getIdTipo() {
		return idTipo;
	}
	public void setIdTipo(int idTipo) {
		this.idTipo = idTipo;
	}
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	
	
		
}
