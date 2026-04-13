package ar.com.ospim.liquidaciones.beans;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class TratamiendoDiscapacidad {
	
private int idOspim;
private String cuil;
private String beneficiario;
private int edad;
private String provincia;
private String cieDiez; 
private String tiposDiscapacidades; //ids de lso tipos de discapacidades separados por ,
private String cuit;
private String prestador;
private Date periodoDesde; 
private Date periodoHasta;
private String sur;
private String codigo;
private String prestacion;
private BigDecimal importeTotal;
private int id;
private int cantidad;
private String grupo;
private String diagnostico;
	
	public static TratamiendoDiscapacidad getMapping(ResultSet rs) throws SQLException{
		TratamiendoDiscapacidad rdsab= new TratamiendoDiscapacidad();
		rdsab.setBeneficiario(rs.getString("beneficiario"));
		rdsab.setIdOspim(rs.getInt("id_ospim"));
		rdsab.setCuil(rs.getString("cuil"));
		rdsab.setEdad(rs.getInt("edad"));
		rdsab.setProvincia(rs.getString("provincia"));
		rdsab.setCieDiez(rs.getString("cie_diez"));
		rdsab.setTiposDiscapacidades(rs.getString("ids_tipo_discapacidad"));
		rdsab.setCuit(rs.getString("cuit"));
		rdsab.setPrestador(rs.getString("prestador"));
		rdsab.setPeriodoDesde(rs.getDate("periodo_desde"));
		rdsab.setPeriodoHasta(rs.getDate("periodo_hasta"));
		rdsab.setSur(rs.getString("sur"));
		rdsab.setCodigo(rs.getString("codigo"));
		rdsab.setPrestacion(rs.getString("prestacion"));
		rdsab.setImporteTotal(rs.getBigDecimal("importe_total"));
		rdsab.setDiagnostico(rs.getString("diagnostico"));
	
		return rdsab;
	}
	
	public static TratamiendoDiscapacidad getMappingPorEdad(ResultSet rs) throws SQLException{
		TratamiendoDiscapacidad rdsab= new TratamiendoDiscapacidad();
		
		rdsab.setId(rs.getInt("id"));
		rdsab.setCantidad(rs.getInt("cantidad"));
		rdsab.setGrupo(rs.getString("grupo"));

		return rdsab;
	}

	public int getIdOspim() {
		return idOspim;
	}

	public String getCuil() {
		return cuil;
	}

	public int getEdad() {
		return edad;
	}

	public String getProvincia() {
		return provincia;
	}

	public String getCieDiez() {
		return cieDiez;
	}

	public String getCuit() {
		return cuit;
	}

	public String getPrestador() {
		return prestador;
	}

	public Date getPeriodoDesde() {
		return periodoDesde;
	}

	public Date getPeriodoHasta() {
		return periodoHasta;
	}

	public String getSur() {
		return sur;
	}

	public String getCodigo() {
		return codigo;
	}

	public String getPrestacion() {
		return prestacion;
	}

	public BigDecimal getImporteTotal() {
		return importeTotal;
	}

	public void setIdOspim(int idOspim) {
		this.idOspim = idOspim;
	}

	public void setCuil(String cuil) {
		this.cuil = cuil;
	}

	public void setEdad(int edad) {
		this.edad = edad;
	}

	public void setProvincia(String provincia) {
		this.provincia = provincia;
	}

	public void setCieDiez(String cieDiez) {
		this.cieDiez = cieDiez;
	}

	public void setCuit(String cuit) {
		this.cuit = cuit;
	}

	public void setPrestador(String prestador) {
		this.prestador = prestador;
	}

	public void setPeriodoDesde(Date periodoDesde) {
		this.periodoDesde = periodoDesde;
	}

	public void setPeriodoHasta(Date periodoHasta) {
		this.periodoHasta = periodoHasta;
	}

	public void setSur(String sur) {
		this.sur = sur;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public void setPrestacion(String prestacion) {
		this.prestacion = prestacion;
	}

	public void setImporteTotal(BigDecimal importeTotal) {
		this.importeTotal = importeTotal;
	}

	public int getId() {
		return id;
	}

	public int getCantidad() {
		return cantidad;
	}

	public String getGrupo() {
		return grupo;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setCantidad(int cantidad) {
		this.cantidad = cantidad;
	}

	public void setGrupo(String grupo) {
		this.grupo = grupo;
	}

	public String getTiposDiscapacidades() {
		return tiposDiscapacidades;
	}

	public void setTiposDiscapacidades(String tiposDiscapacidades) {
		this.tiposDiscapacidades = tiposDiscapacidades;
	}

	public String getBeneficiario() {
		return beneficiario;
	}

	public void setBeneficiario(String beneficiario) {
		this.beneficiario = beneficiario;
	}

	public String getDiagnostico() {
		return diagnostico;
	}

	public void setDiagnostico(String diagnostico) {
		this.diagnostico = diagnostico;
	}	
	
	
	
}