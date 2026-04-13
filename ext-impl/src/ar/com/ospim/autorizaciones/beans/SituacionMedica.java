package ar.com.ospim.autorizaciones.beans;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import java.math.BigInteger;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;

import ar.com.ospim.afiliados.NoSuchAfiliadoEntryException;
import ar.com.ospim.afiliados.beans.Afiliado;
import ar.com.ospim.afiliados.beans.DetalleDiscapacidad;
import ar.com.ospim.afiliados.services.EditarAfiliadoServiceUtil;
import ar.com.ospim.correspondencia.beans.ItemCorrespondenciaTotal;
import ar.com.ospim.crm.beans.ContactoCRM;
import ar.com.ospim.global.beans.Domicilio;
import ar.com.ospim.global.beans.Prestacion;
import ar.com.ospim.global.beans.Seccional;
import ar.com.ospim.global.beans.Telefono;
import ar.com.ospim.liquidaciones.beans.Prestador;
import ar.com.ospim.util.DateUtils;

public class SituacionMedica  implements Serializable {
	
	
	private static final long serialVersionUID = -6102078435078459505L;
	private int id;
	private Afiliado afiliado;
	private DetalleDiscapacidad detalleDiscapacidad; 
	private Date altaFecha;	
	private Date vigenDesde;
	private Date vigenHasta;
	private Date bajaFecha;
		
    private String tipoSituMedica; 
    private int idTipoSituMedica;

    private String estadoRegistroSitMedica;    	
	private boolean discapacitado;
	
    private List<PatologiasSituacionMedica>  patologiasSituMedicas;
    private String cie10;
    private String diagnostico;
    private String diagnosticoCieX;
    private String tipodiscapacidades;
    private  int idSituacionMedica;
    private String detalleSituMedica;       

	public SituacionMedica (){
		
	}
	public SituacionMedica (String cuilTitular, int inte) {
		this.setAfiliado(new Afiliado(cuilTitular, inte));
	}

	public SituacionMedica (String cuilTitular, int inte, String cie10 , String diagnostico , String tipodiscapacidades ,  boolean dependencia , String telefono , int idSituacionMedica , String detalleSituMedica,boolean esDiscapacitado , Date fechaDesde ,Date fechaHasta) {
		
		this.setAfiliado(new Afiliado(cuilTitular, inte));
		this.setCie10(cie10 );
		this.setdiagnostico(diagnostico);
		this.setTipodiscapacidades(tipodiscapacidades);
		this.setIdTipoSituMedica(idSituacionMedica);
		this.setDiscapacitado(esDiscapacitado);
		this.setFechaVigen_Desde(fechaDesde);
		this.setFechaVigen_Hasta(fechaHasta);
		this.setDetalleSituMedica(detalleSituMedica);
		
		DetalleDiscapacidad detalledisca = new DetalleDiscapacidad();
		detalledisca.setDependencia(dependencia);
		detalledisca.setCie_diez(cie10);
		detalledisca.setDiagnostico(diagnostico);
		detalledisca.setCuil_titular(cuilTitular);
		detalledisca.setInte(inte);
		detalledisca.setTelefono_contacto(telefono);
		detalledisca.armarListaTiposDiscapacidades(tipodiscapacidades);
		this.setDetalleDiscapacidad(detalledisca);
	}
	
	public String getDetalleSituMedica () {
		return this.detalleSituMedica;
	}

	public void setDetalleSituMedica(String detalleSituMedica) {
		this.detalleSituMedica=detalleSituMedica;
	}
	 
	public int getIdSituacionMedica () {
		return this.idSituacionMedica;
	}

	public void setIdSituacionMedica (int idSituacionMedica) {
		this.idSituacionMedica=idSituacionMedica;
	}
	
	public String getTipodiscapacidades () {
		return this.tipodiscapacidades;
	}

	public void setTipodiscapacidades(String tipodiscapacidades) {
		this.tipodiscapacidades=tipodiscapacidades;
	}

	
	public String getdiagnostico () {
		return this.diagnostico;
	}

	public void setdiagnostico(String diagnostico) {
		this.diagnostico=diagnostico;
	}
	public String getdiagnosticoCieX () {
		return this.diagnosticoCieX;
	}

	public void setdiagnosticoCieX(String diagnostico) {
		this.diagnosticoCieX=diagnostico;
	}
	
	public String getCie10 () {
		return this.cie10 ;
	}

	public void setCie10(String cie10dato) {
		this.cie10 =cie10dato;
	}

    	
	public int getId_Situacion () {
		return id;
	}

	public String getId_String() {
		return String.valueOf(id);
	}

	public void setId(int idSituacionMedica ) {
		id = idSituacionMedica ;
	}
	
	public Date getAlta_fecha() {
		return altaFecha;
	}

	public void setAlta_fecha(Date altaFecha) {
	  this.altaFecha = altaFecha;
	}

	public Date getFechaVigen_Desde () {
		return vigenDesde	;
	}

	public void setFechaVigen_Desde (Date fechaDesde ) {
	  this.vigenDesde	 = fechaDesde ;
	}

	public Date getFechaVigen_Hasta () {
		return vigenHasta 	;
	}

	public void setFechaVigen_Hasta (Date fechaHasta ) {
	  this.vigenHasta = fechaHasta ;
	}	 
	
	public Date getBaja_fecha() {
		return bajaFecha;
	}

	public void setBaja_fecha(Date bajaFecha) {
		this.bajaFecha = bajaFecha;
	}
	
	public String getAlta_fechaAsString() {
		return altaFecha != null ? DateUtils.format(altaFecha, "dd/MM/yyyy")
				: "";
	}

	public Afiliado getAfiliado() {
		return afiliado;
	}

	public void setAfiliado(Afiliado afiliado) {
		this.afiliado = afiliado;
	}

	public DetalleDiscapacidad getDetalleDiscapacidad() {
		return detalleDiscapacidad;
	}

	public void setDetalleDiscapacidad (DetalleDiscapacidad  detalleDiscapacidad) {
		this.detalleDiscapacidad= detalleDiscapacidad;
	}
	
	
	public static ItemSituacionMedicaTotal getMappingBuscadorTotal(ResultSet rs, String prefix) throws Exception {
	
		ItemSituacionMedicaTotal situacionMedica = new ItemSituacionMedicaTotal();
		
		try{		
			
			situacionMedica.setId(rs.getInt(prefix + "id"));
			situacionMedica.setAfiliado(new Afiliado(rs.getString(prefix + "cuil_titular"), rs.getInt(prefix + "inte") ,rs.getString(prefix + "nombre"),rs.getString(prefix + "apellido"))  );
			situacionMedica.setFechaVigen_Desde(rs.getDate(prefix + "fecha_desde"));
			situacionMedica.setFechaVigen_Hasta (rs.getDate(prefix + "fecha_hasta"));			
			situacionMedica.setTotal_registros(rs.getInt(prefix + "total"));
			situacionMedica.setDiscapacitado(rs.getBoolean(prefix + "discapacitado"));
			situacionMedica.setTipoSituMedica(rs.getString(prefix + "tipo_situ_medica"));
			situacionMedica.setBaja_fecha(rs.getDate(prefix + "fecha_baja"));
			
		}
		catch (Exception e ){
		    throw e;
		}
				
		return situacionMedica ;

	}

	public boolean isDiscapacitado() {
		return discapacitado;
	}

	public void setDiscapacitado(boolean discapacitado) {
		this.discapacitado = discapacitado;
	}
	public String getCuit_titular()
	{
		return afiliado.getCuil_titular();
	}

	public int getInte()
	{
		return afiliado.getInte() ;
	}
	
	public String  getInteAsString()
	{
		return String.valueOf(this.getInte());
	}	
	
	public int getIdTipoSituMedica() {
		return idTipoSituMedica;
	}

	public void setIdTipoSituMedica(int idTipoSituMedica) {
		this.idTipoSituMedica= idTipoSituMedica;
	}
	
	public String getTipoSituMedica() {
		return tipoSituMedica;
	}

	public void setTipoSituMedica(String tipoSituMedica) {
		this.tipoSituMedica= tipoSituMedica;
	}
	
	public String getEstadoRegSitMedica  () {
		return estadoRegistroSitMedica ;
	}

	public void setEstadoRegSitMedica (String estRegistroSitMedica ) {
		this.estadoRegistroSitMedica= estRegistroSitMedica;
	}


	public static SituacionMedica  getMapping(ResultSet rs, String prefix)
			throws Exception {
		
		
		SituacionMedica situMedica = new SituacionMedica();
		
		try{			
			
		situMedica.setId(rs.getInt(prefix + "id"));
		//situMedica.setAfiliado(new Afiliado(rs.getString(prefix + "cuil_titular"), rs.getInt(prefix + "inte"),rs.getString(prefix + "nombre") , rs.getString(prefix + "inte")   ));
		situMedica.setAfiliado(EditarAfiliadoServiceUtil.getAfiliadoEntryInclusoDadoBaja(rs.getString(prefix + "cuil_titular"), rs.getInt(prefix + "inte")));
		situMedica.setDiscapacitado(rs.getBoolean(prefix + "discapacitado") );		
		situMedica.setBaja_fecha(rs.getDate(prefix + "baja_fecha"));
		situMedica.setTipoSituMedica(rs.getString(prefix + "tipo_situ_medica"));
		situMedica.setIdTipoSituMedica(rs.getInt(prefix + "id_tipo_situ_medica"));		
		situMedica.setFechaVigen_Desde(rs.getDate(prefix + "fecha_desde"));
		situMedica.setFechaVigen_Hasta(rs.getDate(prefix + "fecha_hasta"));
		situMedica.setdiagnostico(rs.getString(prefix + "diagnostico"));
		situMedica.setDetalleSituMedica(rs.getString(prefix + "detalle_tipo_situ"));
		situMedica.setdiagnosticoCieX(rs.getString(prefix + "descripcion_ciediez"));
		
		DetalleDiscapacidad detalledisca = new DetalleDiscapacidad();
		detalledisca.setDependencia(rs.getBoolean(prefix + "dependencia"));
		detalledisca.setCie_diez(rs.getString(prefix + "cie_diez"));
		detalledisca.setDiagnostico(rs.getString(prefix + "diagnostico"));
		detalledisca.setCuil_titular(rs.getString(prefix + "cuil_titular"));
		detalledisca.setInte(rs.getInt(prefix + "inte"));
		detalledisca.setTelefono_contacto(rs.getString(prefix + "telefono_contacto"));
		detalledisca.armarListaTiposDiscapacidades(rs.getString(prefix + "ids_tipo_discapacidad"));
		detalledisca.setCie_diezDescripcion(rs.getString(prefix + "descripcion_ciediez"));
		
		situMedica.setDetalleDiscapacidad(detalledisca );
		
	    
		}
		catch (Exception e ){
		    throw e;
		}
				
		return situMedica;
	}

	public List<PatologiasSituacionMedica> getPatologias () {
		return patologiasSituMedicas;
	}
	
	public void setPatologias (List<PatologiasSituacionMedica>  patologias) {
		this.patologiasSituMedicas= patologias;
	}	

}