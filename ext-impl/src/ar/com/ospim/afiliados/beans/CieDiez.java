package ar.com.ospim.afiliados.beans;


/**
 * @author crivas
 * @version 1.0
 * @created 30-Jul-2010 05:27:49 p.m.
 */
public class CieDiez {

	private String codigo;
	private String descripcion;

	public CieDiez(String codigo, String descrip){
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