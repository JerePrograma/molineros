package ar.com.ospim.global.beans;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;



public class AportesMonotributo implements Serializable{
	
	private static final long serialVersionUID = -9122795577273532086L;
	private Integer categoria;
	private String descripcion;
	private Integer id;
	private Date desde;
	private Date hasta;
	private Double aporte;
	private String errorMsg;
	
	private List<AportesMonotributoClase>clases;
	private List<AportesMonotributoClase>clasesOriginal;
	
	public Integer getCategoria() {
		return categoria;
	}

	public void setCategoria(Integer categoria) {
		this.categoria = categoria;
	}
	
	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Date getDesde() {
		return desde;
	}

	public void setDesde(Date desde) {
		this.desde = desde;
	}

	public Date getHasta() {
		return hasta;
	}

	public void setHasta(Date hasta) {
		this.hasta = hasta;
	}

	public Double getAporte() {
		return aporte;
	}

	public void setAporte(Double aporte) {
		this.aporte = aporte;
	}

	public List<AportesMonotributoClase> getClases() {
		return clases;
	}

	public void setClases(List<AportesMonotributoClase> clases) {
		this.clases = clases;
	}
	
	public List<AportesMonotributoClase> getClasesOriginal() {
		return clasesOriginal;
	}

	public void setClasesOriginal(List<AportesMonotributoClase> clasesOriginal) {
		this.clasesOriginal = clasesOriginal;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public static AportesMonotributo getMapping( ResultSet rs) throws SQLException{
		AportesMonotributo a = new AportesMonotributo();
		a.setAporte(rs.getDouble("aporte"));
		a.setCategoria(rs.getInt("categoria"));
		a.setDescripcion(rs.getString("descripcion"));
		a.setDesde(rs.getDate("desde"));
		a.setHasta(rs.getDate("hasta"));
		a.setId(rs.getInt("id"));
		return a;
	}
	
}