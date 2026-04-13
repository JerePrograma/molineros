package ar.com.ospim.autorizaciones.beans;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import ar.com.ospim.afiliados.beans.Afiliado;

import ar.com.ospim.autorizaciones.reportes.action.ReporteSituacionMedica;


public class SituacionMedicaExcel extends SituacionMedica{

	
	private static final long serialVersionUID = 1L;

	// campos excel equipo situacion medica 
	
	private String diagnostico;
	private String cieDiez;
	private String diagnosticoCieDiez;
	private String codigoCieDiez ;
	private String dependenciaDiscapacidad;
	private String detalleTipoSituacionMedica;
	private Date fechaBaja ;
	private String detalleBaja;
	private String telefonoContacto ; 
	private String detalleTextoDiscapacidades; 
     
	private static Log _log = LogFactoryUtil
			.getLog(ReporteSituacionMedica.class);
	
	
	public SituacionMedicaExcel () {
		super();
	}

	public static SituacionMedicaExcel   getMapping(ResultSet rs) throws SQLException {
		SituacionMedicaExcel   archivo = new SituacionMedicaExcel   ();
		Afiliado afiliado = new Afiliado();
		try {
			afiliado.setCuil_titular(rs.getString("rptsm_cuil_titular"));
			afiliado.setInte(rs.getInt("rptsm_inte"));
			afiliado.setApellido(rs.getString("rptsm_apellido"));
			afiliado.setNombre(rs.getString("rptsm_nombre"));			
			archivo.setAfiliado(afiliado );
			archivo.setId(rs.getInt("rptsm_id"));;
			archivo.setDiscapacitado(rs.getBoolean("rptsm_discapacitado"));
			archivo.setTipoSituMedica(rs.getString("rptsm_tipo_situ_medica"));
			archivo.setFechaVigen_Desde(rs.getDate("rptsm_fecha_desde"));     
			archivo.setFechaVigen_Hasta(rs.getDate("rptsm_fecha_hasta"));
			archivo.setDiagnostico(rs.getString("rptsm_diagnostico")); 
			archivo.setCodigoCieDiez(rs.getString("rptsm_codigociediez"));
			archivo.setDiagnosticoCieDiez(rs.getString("rptsm_descripcion_ciediez"));
			archivo.setDependencia(rs.getString("rptsm_dependencia"));
			archivo.setDetalleTipoSituacionMedica(rs.getString("rptsm_detalle_tipo_situ"));
			archivo.setFechaBaja(rs.getDate("rptsm_fecha_baja"));
			archivo.setDetalleBaja(rs.getString("rptsm_baja"));
			archivo.setDetalleTextoDiscapacidades(rs.getString("rptsm_tipodiscapacidadtexto"));
			archivo.setTelefonoContacto(rs.getString("rptsm_telefonocontacto"));
			
		} catch (Exception e) {
			_log.error(
					"Error al generar reporte de situacion medica",e);
			return null;
		}
		
		return archivo;
	}
	
	public String getCuilTitular() {
		return this.getAfiliado().getCuil_titular();
	}

	public void setCuilTitular(String cuilTitular) {
		this.getAfiliado().setCuil_titular(cuilTitular);
	}

	public int getInte() {
		return this.getAfiliado().getInte();
	}

	public void setInte(int inte) {
		this.getAfiliado().setInte(inte);
	}

	public String getNombre() {
		return this.getAfiliado().getNombre() ;
	}

	public void setNombre(String nombre) {
		this.getAfiliado().setNombre(nombre);
	}

	public String getApellido() {
		return this.getAfiliado().getApellido()  ;
	}

	public void setApellido(String apellido) {
		this.getAfiliado().setApellido(apellido);
	}

	public String getCuil() {
		return this.getAfiliado().getCuil_titular();
	}

	public void setCuil(String cuil) {
		this.getAfiliado().setCuil_titular(cuil);
	}
	
	public String getDiagnostico() {
		return diagnostico;
	}

	public void setDiagnostico(String diagnostico) {
		this.diagnostico = diagnostico;
	}

	public String getCieDiez() {
		return cieDiez;
	}

	public void setCieDiez(String cieDiez) {
		this.cieDiez = cieDiez;
	}

	public String getDiagnosticoCieDiez() {
		return diagnosticoCieDiez;
	}

	public void setDiagnosticoCieDiez(String diagnosticoCieDiez) {
		this.diagnosticoCieDiez = diagnosticoCieDiez;
	}

	public String getCodigoCieDiez () {
		return codigoCieDiez;
	}

	public void setCodigoCieDiez(String codigoCieDiez) {
		this.codigoCieDiez= codigoCieDiez;
	}

	public String getDependencia  () {
		return dependenciaDiscapacidad ;
	}

	public void setDependencia (String dependencia) {
		this.dependenciaDiscapacidad = dependencia ;
	}
	public String getDetalleTipoSituacionMedica  () {
		return detalleTipoSituacionMedica;
	}

	public void setDetalleTipoSituacionMedica(String detalleTipoSituMedica) {
		this.detalleTipoSituacionMedica= detalleTipoSituMedica;
	}
    public void setFechaBaja(Date fecha){
    	fechaBaja =fecha;
    }
	public Date getFechaBaja(){
		return fechaBaja ;
	}
	public String getDetalleBaja () {
		return detalleBaja;
	}

	public void setDetalleBaja(String detalleBaja ) {
		this.detalleBaja= detalleBaja ;
	}

	public String getTelefonoContacto() {
		return telefonoContacto;
	}

	public void setTelefonoContacto(String telefonoContacto) {
		this.telefonoContacto = telefonoContacto;
	}

	public String getDetalleTextoDiscapacidades() {
		return detalleTextoDiscapacidades;
	}

	public void setDetalleTextoDiscapacidades(String detalleTextoDiscapacidades) {
		this.detalleTextoDiscapacidades = detalleTextoDiscapacidades;
	}	
	
	
	
}
