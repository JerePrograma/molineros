package ar.com.ospim.global.beans;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;



public class AportesMonotributoClase implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8434264413079182458L;
	private Integer padreId;
	private Integer id;
	private Integer categoriaId;
	private String clase;
	private Date desde;
	private Date hasta;
	private Double aporte;
	
	
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
	
	public String getClase() {
		return clase;
	}

	public void setClase(String clase) {
		this.clase = clase;
	}
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	public Integer getPadreId() {
		return padreId;
	}

	public void setPadreId(Integer padreId) {
		this.padreId = padreId;
	}
	
	public Integer getCategoriaId() {
		return categoriaId;
	}

	public void setCategoriaId(Integer categoriaId) {
		this.categoriaId = categoriaId;
	}

	public static AportesMonotributoClase getMapping( ResultSet rs) throws SQLException{
		AportesMonotributoClase a = new AportesMonotributoClase();
		a.setId(rs.getInt("id"));
		a.setPadreId(rs.getInt("padre_id"));
		a.setCategoriaId(rs.getInt("categoria_id"));
		a.setAporte(rs.getDouble("aporte"));
		a.setClase(rs.getString("clase_id"));
		a.setDesde(rs.getDate("desde"));
		a.setHasta(rs.getDate("hasta"));
		return a;
	}
	
}