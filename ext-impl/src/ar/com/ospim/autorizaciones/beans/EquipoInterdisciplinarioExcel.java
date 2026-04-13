package ar.com.ospim.autorizaciones.beans;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import ar.com.ospim.afiliados.beans.Afiliado;
import ar.com.ospim.autorizaciones.reportes.action.ReporteEquiposInterDisciplinarios;
import ar.com.ospim.util.DateUtils;

public class EquipoInterdisciplinarioExcel extends EquipoInterdisciplinario{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// campos excel equipo interdisciplinario
	
	private int dictamen;
	private Date 	fechaDictamen;
	private String	cuilTitular;
	private int 	inte;
	private String 	nombre;
	private String  apellido;
	private String 	cuil;
	private String 	 docuNumero;
	private String 	 seccional; 
	private Date fechaNaci;
	private String codigoArea;
	private String 	telefono;
	private String 	diagnostico;
	private String	cieDiez;
	private Date fechaVto;
	private String observaciones;
	private String participantes;
	private String diagnosticoCieDiez;
	private String planMolineros;
	private String planPrevencion;	
	private int edad;
	private String prestacion;
	private String  antecedentes;
	private String  dictamenMedicoAuditor;
	private String  dictamenAsistenteSocial;
	private String dictamenLicinciadoKinesiologia;
	private String dictamenLegales;
	private String dictamenEquipoInterdisciplinario;
     
	private static Log _log = LogFactoryUtil
			.getLog(ReporteEquiposInterDisciplinarios.class);
	
	
	public EquipoInterdisciplinarioExcel () {
		super();
	}

	public static EquipoInterdisciplinarioExcel  getMapping(ResultSet rs) throws SQLException {
		EquipoInterdisciplinarioExcel  archivo = new EquipoInterdisciplinarioExcel  ();
		Afiliado afiliado = new Afiliado();
		try {
			afiliado.setCuil(rs.getString("rpt_cuil"));
			afiliado.setInte(rs.getInt("rpt_inte"));
			afiliado.setApellido(rs.getString("rpt_apellido"));
			afiliado.setNombre(rs.getString("rpt_nombre"));
			afiliado.setPlanAfiliado(rs.getString("rpt_plan_molineros"));
			archivo.setAfiliado(afiliado);
			archivo.setDictamen(rs.getInt("rpt_dictamen"));
			archivo.setFechaDictamen(rs.getDate("rpt_fechaDictamen") );
			archivo.setCuilTitular(rs.getString("rpt_cuil_titular") );
			archivo.setCuil(rs.getString("rpt_cuil") );
			archivo.setDocuNumero(rs.getString("rpt_docu_numero") );
			archivo.setSeccional(rs.getString("rpt_seccional") );
			archivo.setFechaNaci(rs.getDate("rpt_fecha_naci") );			 
			archivo.setCodigoArea(rs.getString("rpt_codigo_area") );
			archivo.setTelefono(rs.getString("rpt_telefono") );
			archivo.setDiagnostico(rs.getString("rpt_diagnostico") );
			archivo.setCieDiez(rs.getString("rpt_cie_diez") );   
			archivo.setFechaVto(rs.getDate("rpt_fecha_vto"));
			archivo.setObservaciones(rs.getString("rpt_observaciones") );
			archivo.setParticipantes(rs.getString("rpt_participantes") );
			archivo.setDiagnosticoCieDiez(rs.getString("rpt_diagnosticoCie10") );
			archivo.setPlanMolineros(rs.getString("rpt_plan_molineros") );
			archivo.setPlanPrevencion(rs.getString("rpt_plan_prevencion") );
			archivo.setEdad(rs.getInt("rpt_edad") );
			archivo.setPrestacion(rs.getString("rpt_prestacion") );
			archivo.setAntecedentes(rs.getString("rpt_antecedentes") );
			archivo.setDictamenMedicoAuditor(rs.getString("rpt_medico_auditor") );
			archivo.setDictamenAsistenteSocial(rs.getString("rpt_asistente_social") );
			archivo.setDictamenLicinciadoKinesiologia(rs.getString("rpt_licenciado_kinesiologia") );
			archivo.setDictamenLegales(rs.getString("rpt_legales") );
			archivo.setDictamenEquipoInterdisciplinario(rs.getString("rpt_equipo_interdisciplinario") );
			archivo.setEstadoRegEquipoInter(rs.getString("rpt_estado") );
			archivo.setMotivoCierreEquipoInter(rs.getString("rpt_motivo_cierre") );
			
		} catch (Exception e) {
			_log.error(
					"Error al generar reporte de equipos Interdisciplinarios",e);
			return null;
		}
		
		return archivo;
	}

	public int getDictamen() {
		return dictamen;
	}

	public void setDictamen(int dictamen) {
		this.dictamen = dictamen;
	}

	public Date getFechaDictamen() {
		return fechaDictamen;
	}

	public void setFechaDictamen(Date fechaDictamen) {
		this.fechaDictamen = fechaDictamen;
	}

	public String getCuilTitular() {
		return cuilTitular;
	}

	public void setCuilTitular(String cuilTitular) {
		this.cuilTitular = cuilTitular;
	}

	public int getInte() {
		return inte;
	}

	public void setInte(int inte) {
		this.inte = inte;
	}

	public String getNombre() {
		return this.getAfiliado().getNombre() ;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getApellido() {
		return this.getAfiliado().getApellido()  ;
	}

	public void setApellido(String apellido) {
		this.apellido = apellido;
	}

	public String getCuil() {
		return cuil;
	}

	public void setCuil(String cuil) {
		this.cuil = cuil;
	}

	public String getDocuNumero() {
		return docuNumero;
	}

	public void setDocuNumero(String docuNumero) {
		this.docuNumero = docuNumero;
	}

	public String getseccional(){
		return this.seccional;
	}

	public void setSeccional(String seccional) {
		this.seccional = seccional;
	}

	public Date getFechaNaci() {
		return fechaNaci;
	}

	public void setFechaNaci(Date fechaNaci) {
		this.fechaNaci = fechaNaci;
	}

	public String getCodigoArea() {
		return codigoArea;
	}

	public void setCodigoArea(String codigoArea) {
		this.codigoArea = codigoArea;
	}

	public String getTelefono() {
		return telefono;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
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

	public Date getFechaVto() {
		return fechaVto;
	}

	public void setFechaVto(Date fechaVto) {
		this.fechaVto = fechaVto;
	}

	public String getObservaciones() {
		return observaciones;
	}

	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}

	public String getParticipantes() {
		return participantes;
	}

	public void setParticipantes(String participantes) {
		this.participantes = participantes;
	}

	public String getDiagnosticoCieDiez() {
		return diagnosticoCieDiez;
	}

	public void setDiagnosticoCieDiez(String diagnosticoCieDiez) {
		this.diagnosticoCieDiez = diagnosticoCieDiez;
	}

	public String getPlanMolineros() {
		return planMolineros;
	}

	public void setPlanMolineros(String planMolineros) {
		this.planMolineros = planMolineros;
	}

	public String getPlanPrevencion() {
		return planPrevencion;
	}

	public void setPlanPrevencion(String planPrevencion) {
		this.planPrevencion = planPrevencion;
	}
	
	public int getEdad() {
		return edad;
	}

	public void setEdad(int edad) {
		this.edad = edad;
	}

	public String getprestacion(){
		return this.prestacion;
	}
	
	public void setPrestacion(String prestacion) {
		this.prestacion = prestacion;
	}

	public String getAntecedentes() {
		return antecedentes;
	}

	public void setAntecedentes(String antecedentes) {
		this.antecedentes = antecedentes;
	}

	public String getDictamenMedicoAuditor() {
		return dictamenMedicoAuditor;
	}

	public void setDictamenMedicoAuditor(String dictamenMedicoAuditor) {
		this.dictamenMedicoAuditor = dictamenMedicoAuditor;
	}

	public String getDictamenAsistenteSocial() {
		return dictamenAsistenteSocial;
	}

	public void setDictamenAsistenteSocial(String dictamenAsistenteSocial) {
		this.dictamenAsistenteSocial = dictamenAsistenteSocial;
	}

	public String getDictamenLicinciadoKinesiologia() {
		return dictamenLicinciadoKinesiologia;
	}

	public void setDictamenLicinciadoKinesiologia(String dictamenLicinciadoKinesiologia) {
		this.dictamenLicinciadoKinesiologia = dictamenLicinciadoKinesiologia;
	}

	public String getDictamenLegales() {
		return dictamenLegales;
	}

	public void setDictamenLegales(String dictamenLegales) {
		this.dictamenLegales = dictamenLegales;
	}

	public String getDictamenEquipoInterdisciplinario() {
		return dictamenEquipoInterdisciplinario;
	}

	public void setDictamenEquipoInterdisciplinario(String dictamenEquipoInterdisciplinario) {
		this.dictamenEquipoInterdisciplinario = dictamenEquipoInterdisciplinario;
	}

	
}
