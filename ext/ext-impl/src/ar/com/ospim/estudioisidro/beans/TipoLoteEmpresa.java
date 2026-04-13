package ar.com.ospim.estudioisidro.beans;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TipoLoteEmpresa implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4308628131445749534L;
	private Integer lote;
	private String tipoLote;
	private String descripcionLote;
	private int diasVencimiento;
	
	
	public TipoLoteEmpresa() {
		super();
	}
	
	public TipoLoteEmpresa(Integer lote, String tipoLote, String descripcionLote, int diasVencimiento) {
		super();
		this.lote = lote;
		this.tipoLote = tipoLote;
		this.descripcionLote = descripcionLote;
		this.diasVencimiento = diasVencimiento;
	}
	
	public Integer getLote() {
		return lote;
	}
	public void setLote(Integer lote) {
		this.lote = lote;
	}
	public String getTipoLote() {
		return tipoLote;
	}
	public void setTipoLote(String tipoLote) {
		this.tipoLote = tipoLote;
	}
	public String getDescripcionLote() {
		return descripcionLote;
	}
	public void setDescripcionLote(String descripcionLote) {
		this.descripcionLote = descripcionLote;
	}
	public int getDiasVencimiento() {
		return diasVencimiento;
	}
	public void setFechaVencimiento(int diasVencimiento) {
		this.diasVencimiento = diasVencimiento;
	}
	
	
	public static TipoLoteEmpresa getMapping(String prefix, ResultSet rs) throws SQLException {
		
		TipoLoteEmpresa le = new TipoLoteEmpresa(
				rs.getInt(prefix + "nro_lote"), 
				rs.getString(prefix + "tipo"),
				rs.getString(prefix + "descripcion"),
				rs.getInt(prefix + "dias_vencimiento")
				);

		return le;
	}
	
	
}
