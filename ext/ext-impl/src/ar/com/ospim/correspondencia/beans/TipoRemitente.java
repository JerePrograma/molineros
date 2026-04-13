package ar.com.ospim.correspondencia.beans;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TipoRemitente implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6211652151152569769L;
	private String idTipoRemitente;
	private String descripcion;
	private ENTRADA_SALIDA es;
	private String div;
	private String divFactura;
	private String valorPorDefecto;
	
	
	public TipoRemitente(){
		super();
	}
	
	public TipoRemitente(String idTipoRemitente, String descripcion,
			ENTRADA_SALIDA es, String div, String divFactura, String valorPorDefecto) {
		
		super();
		this.idTipoRemitente = idTipoRemitente;
		this.descripcion = descripcion;
		this.es = es;
		this.div = div;
		this.divFactura = divFactura;
		this.valorPorDefecto = valorPorDefecto;
	}

	public enum ENTRADA_SALIDA {
		SOLO_ENTRADA, SOLO_SALIDA, AMBOS
	};
	
	public String getIdTipoRemitente() {
		return idTipoRemitente;
	}
	public void setIdTipoRemitente(String idTipoRemitente) {
		this.idTipoRemitente = idTipoRemitente;
	}
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	public String getDiv() {
		return div;
	}
	public void setDiv(String div) {
		this.div = div;
	}
	public String getValorPorDefecto() {
		return valorPorDefecto;
	}
	public void setValorPorDefecto(String valorPorDefecto) {
		this.valorPorDefecto = valorPorDefecto;
	}
	public ENTRADA_SALIDA getEs() {
		return es;
	}
	public void setEs(ENTRADA_SALIDA es) {
		this.es = es;
	}
	public String getDivFactura() {
		return divFactura;
	}

	public void setDivFactura(String divFactura) {
		this.divFactura = divFactura;
	}
	
	public static TipoRemitente getMapping(String prefix ,ResultSet rs) throws SQLException{
		
		TipoRemitente tr = new TipoRemitente(rs.getString(prefix + "codigo"), 
											 rs.getString(prefix + "descripcion"), 
											 TipoRemitente.ENTRADA_SALIDA.valueOf(rs.getString(prefix + "entrada_salida")), 
											 rs.getString(prefix + "div"),
											 rs.getString(prefix + "div_factura"),
											 rs.getString(prefix + "valor_por_defecto"));
		
		return tr;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((idTipoRemitente == null) ? 0 : idTipoRemitente.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TipoRemitente other = (TipoRemitente) obj;
		if (idTipoRemitente == null) {
			if (other.idTipoRemitente != null)
				return false;
		} else if (!idTipoRemitente.equals(other.idTipoRemitente))
			return false;
		return true;
	}
	
	
	
}
