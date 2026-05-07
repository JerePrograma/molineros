package ar.com.ospim.novedades.beans;

import java.sql.ResultSet;
import java.sql.SQLException;

public class NovedadTotal extends Novedad {

	/**
	 * 
	 */
	private static final long serialVersionUID = 199987230769991655L;
	
	private int total_registros;
	private String parentescoDesc;
	private String estadoCivilDesc;
	private String nacionalidadDesc;
	private String situacionRevistaDesc;
	private String tipoNovedad;
	private int inconsistencia;
	private String razonSocial;
	private String ramoEmpresa;
	private String seccional;
	
	public int getTotal_registros() {
		return total_registros;
	}

	public void setTotal_registros(int total_registros) {
		this.total_registros = total_registros;
	}

	public static NovedadTotal getMappingTotal(
			ResultSet rs, String prefix) throws Exception {
		
		NovedadTotal nov = NovedadTotal.getMapping(prefix, rs);

		return nov;
	}
	
	public static NovedadTotal getMapping(String prefix, ResultSet rs) throws SQLException{
		
		NovedadTotal nov = new NovedadTotal();
		
		nov.setId(rs.getInt(prefix + "id"));
		nov.setIdProceso(rs.getInt(prefix + "id_proceso"));
		nov.setCodigo_ooss(rs.getInt(prefix + "codigo_ooss"));
		nov.setCuit_empleador(rs.getString(prefix + "cuit_empleador"));
		nov.setCuil_titular(rs.getString(prefix + "cuil_titular"));
		nov.setCodigo_parentesco(rs.getInt(prefix + "codigo_parentesco"));
		nov.setParentescoDesc(rs.getString(prefix + "parentesco"));
		nov.setCuil(rs.getString(prefix + "cuil"));
		nov.setDocumento_tipo(rs.getString(prefix + "documento_tipo"));
		nov.setDocumento_numero(rs.getInt(prefix + "documento_numero"));
		nov.setApellido_nombre(rs.getString(prefix + "apellido_nombre"));
		nov.setSexo(rs.getString(prefix + "sexo"));
		nov.setEstado_civil(rs.getInt(prefix + "id_estado_civil_sss"));
		nov.setEstadoCivilDesc(rs.getString(prefix + "estado_civil"));
		nov.setFecha_nacimiento(rs.getInt(prefix + "fecha_nacimiento"));
		nov.setNacionalidad(rs.getInt(prefix + "nacionalidad"));
		nov.setNacionalidadDesc(rs.getString(prefix + "nacionalidad_detalle"));
		nov.setCalle(rs.getString(prefix + "calle"));
		nov.setNumero_puerta(rs.getString(prefix + "numero_puerta"));
		nov.setPiso(rs.getString(prefix + "piso"));
		nov.setDepartamento(rs.getString(prefix + "departamento"));
		nov.setLocalidad(rs.getString(prefix + "localidad"));
		nov.setCodigo_postal(rs.getString(prefix + "codigo_postal"));
		nov.setProvincia(rs.getInt(prefix + "provincia"));
		nov.setTipo_domicilio(rs.getInt(prefix + "tipo_domicilio"));
		nov.setTelefono(rs.getString(prefix + "telefono"));
		nov.setSituacion_revista(rs.getInt(prefix + "id_situacion_revista_sss"));
		nov.setSituacionRevistaDesc(rs.getString(prefix + "situacion_revista"));
		nov.setIncapacidad(rs.getInt(prefix + "incapacidad"));
		nov.setTipo_beneficiario_titular(rs.getInt(prefix + "tipo_beneficiario_titular"));
		nov.setFecha_alta_en_ooss(rs.getInt(prefix + "fecha_alta_en_ooss"));
		nov.setFecha_cierre_presentacion(rs.getInt(prefix + "fecha_cierre_presentacion"));
		nov.setCodigo_movimiento(rs.getString(prefix + "codigo_movimiento"));
		nov.setDetalle_novedad(rs.getString(prefix + "detalle_novedad"));
		nov.setAlta_fecha(rs.getDate(prefix + "alta_fecha"));
		nov.setAlta_usr(rs.getString(prefix + "alta_usr"));
		nov.setModi_fecha(rs.getDate(prefix + "modi_fecha"));
		nov.setModi_usr(rs.getString(prefix + "modi_usr"));
		nov.setBaja_fecha(rs.getDate(prefix + "baja_fecha"));
		nov.setBaja_usr(rs.getString(prefix + "baja_usr"));
		nov.setTotal_registros(rs.getInt("total_registros_v"));
		try {
			nov.setTipoNovedad(rs.getString("tipo_novedad"));
			nov.setInconsistencia(rs.getInt("inconsistencia"));		
		}catch (Exception e) {
		}
		
		try {
			nov.setRazonSocial(rs.getString("razon_social"));
			nov.setRamoEmpresa(rs.getString("actividad"));
			nov.setSeccional(rs.getString("seccional"));
					
		}catch (Exception e) {
		}

		return nov;
	}

	public String toString(){
		String detalle = "";
		detalle = "Novedad id: " + getId() + " CuilTitular / Cuil: " + getCuil_titular() + " / " + getCuil() + " cod_oper: " + getCodigo_movimiento(); 
		return detalle;
	}
	public String getParentescoDesc() {
		return parentescoDesc;
	}

	public void setParentescoDesc(String parentescoDesc) {
		this.parentescoDesc = parentescoDesc;
	}

	public String getEstadoCivilDesc() {
		return estadoCivilDesc;
	}

	public void setEstadoCivilDesc(String estadoCivilDesc) {
		this.estadoCivilDesc = estadoCivilDesc;
	}

	public String getNacionalidadDesc() {
		return nacionalidadDesc;
	}

	public void setNacionalidadDesc(String nacionalidadDesc) {
		this.nacionalidadDesc = nacionalidadDesc;
	}

	public String getSituacionRevistaDesc() {
		return situacionRevistaDesc;
	}

	public void setSituacionRevistaDesc(String situacionRevDesc) {
		this.situacionRevistaDesc = situacionRevDesc;
	}

	public String getTipoNovedad() {
		return tipoNovedad;
	}

	public void setTipoNovedad(String tipoNovedad) {
		this.tipoNovedad = tipoNovedad;
	}

	public int getInconsistencia() {
		return inconsistencia;
	}

	public void setInconsistencia(int inconsistencia) {
		this.inconsistencia = inconsistencia;
	}

	public String getRazonSocial() {
		return razonSocial;
	}

	public void setRazonSocial(String razonSocial) {
		this.razonSocial = razonSocial;
	}

	public String getRamoEmpresa() {
		return ramoEmpresa;
	}

	public void setRamoEmpresa(String ramoEmpresa) {
		this.ramoEmpresa = ramoEmpresa;
	}

	public String getSeccional() {
		return seccional;
	}

	public void setSeccional(String seccional) {
		this.seccional = seccional;
	}
	
	
}
