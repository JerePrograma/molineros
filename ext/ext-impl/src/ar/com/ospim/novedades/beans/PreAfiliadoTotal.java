package ar.com.ospim.novedades.beans;

import java.sql.ResultSet;
import java.sql.SQLException;


public class PreAfiliadoTotal extends PreAfiliado {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4068859297549385965L;
	
	private int total_registros;
	private String empresaDesc;
	private String razonSocial;
	private String plan;
	private String motivo_baja;
	private String tercerizadora;
	
	public int getTotal_registros() {
		return total_registros;
	}
	public void setTotal_registros(int total_registros) {
		this.total_registros = total_registros;
	}
	public String getEmpresaDesc() {
		return empresaDesc;
	}
	public void setEmpresaDesc(String empresaDesc) {
		this.empresaDesc = empresaDesc;
	}
	public String getRazonSocial() {
		return razonSocial;
	}
	public void setRazonSocial(String razonSocial) {
		this.razonSocial = razonSocial;
	}
	public String getPlan() {
		return plan;
	}
	public void setPlan(String plan) {
		this.plan = plan;
	}
	public String getMotivo_baja() {
		return motivo_baja;
	}
	public void setMotivo_baja(String motivo_baja) {
		this.motivo_baja = motivo_baja;
	}
	public String getTercerizadora() {
		return tercerizadora;
	}
	public void setTercerizadora(String tercerizadora) {
		this.tercerizadora = tercerizadora;
	}
	
	public static PreAfiliadoTotal getMapping(ResultSet rs) throws SQLException{
		
		PreAfiliadoTotal pat = new PreAfiliadoTotal();
		
		pat.setId(rs.getInt("id"));
		pat.setCuil_titular(rs.getString("cuil_titular"));
		pat.setInte(rs.getInt("inte"));
		pat.setCuil(rs.getString("cuil"));
		pat.setApellido(rs.getString("apellido"));
		pat.setNombre(rs.getString("nombre"));
		pat.setId_parentesco_sss(rs.getInt("id_parentesco_sss"));
		pat.setId_estado_civil_sss(rs.getInt("id_estado_civil_sss"));
		pat.setDocumento_tipo(rs.getString("documento_tipo"));
		pat.setDocumento_numero(rs.getString("documento_numero"));
		pat.setSexo(rs.getString("sexo"));
		pat.setDiscapacitado(rs.getString("discapacitado"));
		pat.setNacionalidad(rs.getInt("nacionalidad"));
		pat.setNaci_fecha(rs.getDate("naci_fecha"));
		pat.setId_seccional(rs.getInt("id_seccional"));
		pat.setVigen_fecha(rs.getDate("vigen_fecha"));
		pat.setObservaciones(rs.getString("observaciones"));
		pat.setEmail(rs.getString("email"));
		pat.setDomi_tipo(rs.getString("domi_tipo"));
		pat.setCalle(rs.getString("calle"));
		pat.setNumero(rs.getString("numero"));
		pat.setPiso(rs.getString("piso"));
		pat.setDepto(rs.getString("depto"));
		pat.setPostal_codi(rs.getString("postal_codi"));
		pat.setBarrio(rs.getString("barrio"));
		pat.setId_provincia(rs.getInt("id_provincia"));
		pat.setId_localidad(rs.getInt("id_localidad"));
		pat.setCod_area_telefono(rs.getString("cod_area_telefono"));
		pat.setTelefono(rs.getString("telefono"));
		pat.setCod_area_celular(rs.getString("cod_area_celular"));
		pat.setCelular(rs.getString("celular"));
		pat.setCod_area_tel_laboral(rs.getString("cod_area_tel_laboral"));
		pat.setTel_laboral(rs.getString("tel_laboral"));
		//Situ laboral
		pat.setCuit(rs.getString("cuit"));
		pat.setRazonSocial(rs.getString("razon_social"));
	    pat.setSucursal(rs.getString("sucursal"));
	    pat.setFecha_ingre(rs.getDate("fecha_ingre"));
	    pat.setId_revista(rs.getInt("id_revista"));
	    pat.setId_categoria(rs.getInt("id_categoria"));
	    pat.setEscala_salarial(rs.getString("escala_salarial"));
	    //Plan
	    pat.setId_plan(rs.getInt("id_plan"));
	    pat.setPlan(rs.getString("plan"));
	    pat.setVigenDesde(rs.getDate("plan_vigen_desde"));
	    pat.setVigenHasta(rs.getDate("plan_vigen_hasta"));
	    pat.setId_motivo_baja(rs.getInt("plan_id_motivo_baja"));
	    //Tercerizadora
	    pat.setId_tercerizadora(rs.getString("id_tercerizadora"));
	    pat.setTercerizadora(rs.getString("tercerizadora"));
	    pat.setFecha_inicio_prestacion(rs.getDate("fecha_inicio_prestacion"));
	    pat.setFecha_fin_prestacion(rs.getDate("fecha_fin_prestacion"));
	    
	    pat.setDe_alta_portal(rs.getBoolean("de_alta_portal"));
	    pat.setTipo_novedad(rs.getString("tipo_novedad"));
	    pat.setAlta_fecha(rs.getDate("alta_fecha"));
	    pat.setAlta_usr(rs.getString("alta_usr"));
	    pat.setAlta_empresa_usr(rs.getInt("alta_empresa_usr"));
	    pat.setModi_fecha(rs.getDate("modi_fecha"));
	    pat.setModi_usr(rs.getString("modi_usr"));
	    pat.setBaja_fecha(rs.getDate("baja_fecha"));
	    pat.setBaja_usr(rs.getString("baja_usr"));
		pat.setTieneAntecedentesJudiciales(
				rs.getInt("tiene_antecedentes_judiciales")
		);

	    pat.setTotal_registros(rs.getInt("total_registros_v"));

		return pat;
	}
}
