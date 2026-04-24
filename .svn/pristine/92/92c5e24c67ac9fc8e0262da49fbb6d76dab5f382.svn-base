package ar.com.ospim.novedades.beans;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TipoNovedad implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 51241308854554525L;
	
	private String codigo;
	private String grupo;
	private String descripcion;
	
	public String getCodigo() {
		return codigo;
	}
	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}
	public String getGrupo() {
		return grupo;
	}
	public void setGrupo(String grupo) {
		this.grupo = grupo;
	}
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	
	public TipoNovedad(){
		super();
	}
	
	public TipoNovedad(String cod, String grup, String desc){
		super();
		this.codigo = cod;
		this.grupo = grup;
		this.descripcion = desc;
	}
	
	public static TipoNovedad getMapping(String prefix, ResultSet rs) throws SQLException{
		
		TipoNovedad tn = new TipoNovedad(rs.getString(prefix + "codigo"),
											rs.getString(prefix + "grupo"),
											rs.getString(prefix + "descripcion"));
		return tn;
		
	}
}
