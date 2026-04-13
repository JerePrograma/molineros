package ar.com.ospim.tesoreria.beans;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import ar.com.ospim.global.beans.Concepto;

public class ConceptoAfip {
	private int id;
	private String codigoConcepto;
	private String descripcion;
	private String codigoContraConcepto;
	private String debitoCredito;
	private boolean liquidable;
	private Concepto concepto;
	private Date validoDesde;
	private Date validoHasta;

	public ConceptoAfip() {
	}

	public ConceptoAfip(int id) {
		this.id = id;
	}

	public String getCodigoConcepto() {
		return codigoConcepto;
	}

	public void setCodigoConcepto(String codigoConcepto) {
		this.codigoConcepto = codigoConcepto;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public String getCodigoContraConcepto() {
		return codigoContraConcepto;
	}

	public void setCodigoContraConcepto(String codigoContraConcepto) {
		this.codigoContraConcepto = codigoContraConcepto;
	}

	public String getDebitoCredito() {
		return debitoCredito;
	}

	public void setDebitoCredito(String debitoCredito) {
		this.debitoCredito = debitoCredito;
	}

	public boolean isLiquidable() {
		return liquidable;
	}

	public void setLiquidable(boolean liquidable) {
		this.liquidable = liquidable;
	}

	public Concepto getConcepto() {
		return concepto;
	}

	public void setConcepto(Concepto concepto) {
		this.concepto = concepto;
	}

	public String getValidoDesdeString() {
		if (validoDesde == null) {
			return "";
		}
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		return format.format(validoDesde);
	}

	public String getValidoHastaString() {
		if (validoHasta == null) {
			return "";
		}
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		return format.format(validoHasta);
	}

	public static ConceptoAfip getMapping(ResultSet rs) throws SQLException {
		ConceptoAfip ret = new ConceptoAfip();
		ret.setCodigoConcepto(rs.getString("cod_conc"));
		ret.setDescripcion(rs.getString("descripcion"));
		ret.setCodigoContraConcepto(rs.getString("cod_contra_conc"));
		ret.setDebitoCredito(rs.getString("deb_cred"));
		ret.setLiquidable(rs.getBoolean("liquidable"));
		ret.setValidoDesde(rs.getDate("valido_desde"));
		ret.setValidoHasta(rs.getDate("valido_hasta"));
		ret.setId(rs.getInt("id"));
		ret.setConcepto(Concepto.getMapping(rs, "c__"));
		return ret;
	}

	public Date getValidoDesde() {
		return validoDesde;
	}

	public void setValidoDesde(Date validoDesde) {
		this.validoDesde = validoDesde;
	}

	public Date getValidoHasta() {
		return validoHasta;
	}

	public void setValidoHasta(Date validoHasta) {
		this.validoHasta = validoHasta;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
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
		ConceptoAfip other = (ConceptoAfip) obj;
		if (id != other.id)
			return false;
		return true;
	}

}
