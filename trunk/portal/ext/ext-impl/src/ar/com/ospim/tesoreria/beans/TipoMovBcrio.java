package ar.com.ospim.tesoreria.beans;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import ar.com.ospim.global.beans.Concepto;
import ar.com.ospim.global.beans.PlanCuentas;

public class TipoMovBcrio {

	public static final int RECHAZAR_CHEQUES = 42;
	public static final int DEPOSITAR_CHEQUES = 27;
	public static final int DEPOSITAR_EFECTIVO = 36;
	public static final int CANJE_CHEQUE = 26;
	public static final int SUBSIDIOS_APE = 39;

	private int id;
	private int id_tipo_mov;
	private String descripcion;
	private PlanCuentas cuentaAsociada;

	private Concepto concepto;
	private Date validoDesde;
	private Date validoHasta;

	public TipoMovBcrio() {
	}

	public TipoMovBcrio(int id) {
		this.id_tipo_mov = id;
	}

	public TipoMovBcrio(String desc) {
		this.descripcion = desc;
	}

	public TipoMovBcrio(int id_tipo_mov, String desc) {
		this.id_tipo_mov = id_tipo_mov;
		this.descripcion = desc;
	}

	public int getId_tipo_mov() {
		return id_tipo_mov;
	}
	
	public int getId(){
		return id;
	}
	
	public void setId(int i){
		this.id=i;
	}

	public void setId_tipo_mov(int idTipoMov) {
		id_tipo_mov = idTipoMov;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id_tipo_mov;
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
		TipoMovBcrio other = (TipoMovBcrio) obj;
		if (id_tipo_mov != other.id_tipo_mov)
			return false;
		return true;
	}

	public void setCuentaAsociada(PlanCuentas cuentaAsociada) {
		this.cuentaAsociada = cuentaAsociada;
	}

	public PlanCuentas getCuentaAsociada() {
		return cuentaAsociada;
	}

	public Concepto getConcepto() {
		return concepto;
	}

	public void setConcepto(Concepto concepto) {
		this.concepto = concepto;
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

	public static TipoMovBcrio getMapping(ResultSet rs) throws SQLException {
		TipoMovBcrio tipoMovBcrio = new TipoMovBcrio();
		tipoMovBcrio.setConcepto(Concepto.getMapping(rs, "c__"));
		tipoMovBcrio.setDescripcion(rs.getString("descripcion"));
		tipoMovBcrio.setId_tipo_mov(rs.getInt("id_tipo_mov"));
		tipoMovBcrio.setValidoDesde(rs.getDate("valido_desde"));
		tipoMovBcrio.setValidoHasta(rs.getDate("valido_hasta"));
		tipoMovBcrio.setId(rs.getInt("c__id"));
		
		return tipoMovBcrio;
	}

}
