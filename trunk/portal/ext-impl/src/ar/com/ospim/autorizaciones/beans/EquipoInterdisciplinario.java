package ar.com.ospim.autorizaciones.beans;

import java.io.Serializable;
import java.sql.ResultSet;
import java.util.Date;
import java.util.List;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.Validator;

import ar.com.global.services.MailingServiceImpl;
import ar.com.ospim.afiliados.beans.Afiliado;
import ar.com.ospim.global.beans.Domicilio;
import ar.com.ospim.global.beans.Prestacion;
import ar.com.ospim.global.beans.Telefono;
import ar.com.ospim.util.DateUtils;

public class EquipoInterdisciplinario implements Serializable {
	

	
	private static final long serialVersionUID = -6102078435999959505L;
	
	private static Log _log = LogFactoryUtil.getLog(MailingServiceImpl.class);
	
	private int id;
	private Afiliado afiliado;
	private Date fechaRegistro;

    private String codigoCie10; 	
    private String diagnosticoAfiliado;
    
    private Boolean cambioDiagnostico =false;
    private Boolean cambioEmailAfiliado =false;
    private int idEmail ;
    
	private Telefono telefonoDeContacto ;
    private Boolean cambioTelefono =false;
    private Boolean cambioDomicilio = false;
    private Boolean cambioMail =false;
    
	private int idAfiliado;
	private String observacionRegistro;
	private String participantesRegistro;
	private String tipoDomicilio ;
	private int nroRegistro; 
	private List<PrestacionesEquipoInterdisciplinario> prestaciones;
	
	private Prestacion prestacion;
	private Date altaFecha;
	private String altaUsr;
	private Date modiFecha;
	private String modiUsr;
	private Date bajaFecha;
	private String bajaUsr;
	
	private String estadoRegistroeq;
	private String motivoCierre;
	
	private String dictamenes[] = new String[6]; // soporta 6 tipos de dictamenes 
	private String dictamenesOriginales[] = new String[6]; // soporta 6 tipos de dictamenes
	
	private List<FirmaAutorizante> firmaAutorizante;  
	
	public enum DICTAMENES{
		ANTECEDENTES, MEDICOAUDITOR , ASISTENTESOCIAL, LICENCIADOKINESIOTERAPIAFISICA,LEGALES,EQUIPOINTERDISCIPLINARIO
	};
    
	
	public EquipoInterdisciplinario () {
	}

	public EquipoInterdisciplinario (String cuilTitular, int inte,	Date fecha , String participantes , String observacion  ,String estado ,String diagnostico,String cie10 , String  codAreaTelefono ,String telefono, String  tipoTelefono 
			, int provincia , int localidad  ,String calle , String  numero ,String departamento   ,String barrio ,String piso ,String tipoDomicilio ,String codigoPostal, String [] dictamenes , String emailAfiliado , String motivoCierre,
			List<FirmaAutorizante> firmaAutorizante) 
	
		throws Exception {
	
try {
	
	this.altaFecha = fecha ;
	this.fechaRegistro= fecha ;
	this.estadoRegistroeq= estado;
	this.observacionRegistro = observacion;
	this.participantesRegistro = participantes;		
    this.codigoCie10 = cie10;
    this.diagnosticoAfiliado = diagnostico;
    this.telefonoDeContacto = new Telefono();
    this.telefonoDeContacto.setTipo(tipoTelefono );
    this.telefonoDeContacto.setCodigoArea(codAreaTelefono );
    this.telefonoDeContacto.setNumero(telefono);
    this.setTipoDomicilio(tipoDomicilio);    
    this.setMotivoCierreEquipoInter(motivoCierre);
    Domicilio domicilio=new Domicilio();	
	domicilio.setProvinciaId(provincia);
	domicilio.setLocalidadId(localidad);	
	domicilio.setNumero(numero);
	domicilio.setPiso(piso);
	domicilio.setDepto(departamento);
	domicilio.setCalle(calle);
	domicilio.setBarrio(barrio);
	domicilio.setPostal_codi(codigoPostal);
// Asigna Datos Afiliado 	
	this.afiliado = new Afiliado(cuilTitular, inte);	
	this.afiliado.setEmail(emailAfiliado );
	this.getAfiliado().setDomicilioDefault(domicilio);	
// carga dictamenes
	this.dictamenes[DICTAMENES.ANTECEDENTES.ordinal()]=dictamenes[DICTAMENES.ANTECEDENTES.ordinal()];
	this.dictamenes[DICTAMENES.MEDICOAUDITOR.ordinal()]=dictamenes[DICTAMENES.MEDICOAUDITOR.ordinal()];
	this.dictamenes[DICTAMENES.ASISTENTESOCIAL.ordinal()]=dictamenes[DICTAMENES.ASISTENTESOCIAL.ordinal()];
	this.dictamenes[DICTAMENES.LICENCIADOKINESIOTERAPIAFISICA.ordinal()]=dictamenes[DICTAMENES.LICENCIADOKINESIOTERAPIAFISICA.ordinal()];
	this.dictamenes[DICTAMENES.EQUIPOINTERDISCIPLINARIO.ordinal()]=dictamenes[DICTAMENES.EQUIPOINTERDISCIPLINARIO.ordinal()];
	this.dictamenes[DICTAMENES.LEGALES.ordinal()]=dictamenes[DICTAMENES.LEGALES.ordinal()];
	this.firmaAutorizante = firmaAutorizante;
}
		catch (Exception e ){
			throw e;
		}
	    
	}

    public EquipoInterdisciplinario (String cuilTitular, int inte,	Date altaFecha
    		, String observacion , String participantes )
    
    {			
		this.afiliado = new Afiliado(cuilTitular, inte);
		this.altaFecha = altaFecha;
	}

	
	public int getId_registroEquipoInter() {
		return id;
	}

	public String getId_registroEquipoInter_String() {
		return String.valueOf(id);
	}

	public void setId(int idRegEquipoInter ) {
		id = idRegEquipoInter ;
	}
	
	public Date getAlta_fecha() {
		return altaFecha;
	}

	public void setAlta_fecha(Date altaFecha) {
	  this.altaFecha = altaFecha;
	}

	public String getAlta_usr() {
		return altaUsr;
	}

	public void setAlta_usr(String altaUsr) {
		this.altaUsr= altaUsr;
	}

	public Date getModi_fecha() {
		return modiFecha;
	}

	public void setModi_fecha(Date modiFecha) {
		this.modiFecha = modiFecha;
	}

	public String getModi_usr() {
		return modiUsr;
	}

	public void setModi_usr(String modiUsr) {
		this.modiUsr = modiUsr;
	}

	
	public Date getBaja_fecha() {
		return bajaFecha;
	}

	public void setBaja_fecha(Date bajaFecha) {
		this.bajaFecha = bajaFecha;
	}

	public String getBaja_usr() {
		return bajaUsr;
	}

	public void setBaja_usr(String bajaUsr) {
		this.bajaUsr = bajaUsr;
	}

	public int getId_afiliado() {
		return idAfiliado;
	}

	public void setId_afiliado(int idAfiliado) {
	   this.idAfiliado= idAfiliado;
	}

	public String getAlta_fechaAsString() {
		return altaFecha != null ? DateUtils.format(altaFecha, "dd/MM/yyyy") : "";
	}

	public Afiliado getAfiliado() {
		return afiliado;
	}

	public void setAfiliado(Afiliado afiliado) {
		this.afiliado = afiliado;
	}

	public Prestacion getPrestacion() {
		return prestacion;
	}

	public void setPrestacion(Prestacion prestacion) {
		this.prestacion = prestacion;
	}

	
	
	        
	 public static EquipoInterdisciplinario  getMappingDefaultDatos(ResultSet rs, String prefix, String cuil_titular , int inte )
				throws Exception {
			
			EquipoInterdisciplinario equi = new EquipoInterdisciplinario();
			
			try{
				equi.setDiagnosticoAfiliado(rs.getString(prefix + "diagnostico"));
				equi.setCodigoCie10(rs.getString(prefix + "cie_10"));
				equi.setTelefonoContacto( new Telefono());
				if (rs.getString(prefix + "numero_telefonos") == null  ){ // de afiliado 
					equi.getTelefonoContacto().setTipo(Validator.isNotNull(rs.getString(prefix + "tipo_tele"))? rs.getString(prefix + "tipo_tele")    : "" ); 
					equi.getTelefonoContacto().setCodigoArea(Validator.isNotNull(rs.getString(prefix + "codigo_area"))? rs.getString(prefix + "codigo_area")    : "" ); 
					equi.getTelefonoContacto().setNumero(Validator.isNotNull(rs.getString(prefix + "numero"))? rs.getString(prefix + "numero")    : "" );
				}else{ // de la tabla telefono 
					equi.getTelefonoContacto().setTipo(Validator.isNotNull(rs.getString(prefix + "tipotele_telefonos"))? rs.getString(prefix + "tipotele_telefonos")    : "" ); 
					equi.getTelefonoContacto().setCodigoArea(Validator.isNotNull(rs.getString(prefix + "codigo_area_telefonos"))? rs.getString(prefix + "codigo_area_telefonos")    : "" ); 
					equi.getTelefonoContacto().setNumero(Validator.isNotNull(rs.getString(prefix + "numero_telefonos"))? rs.getString(prefix + "numero_telefonos")    : "" );
				}	
				Domicilio domicilio=new Domicilio();
				domicilio.setNumero(Validator.isNotNull(rs.getString(prefix + "numero_calle"))? rs.getString(prefix + "numero_calle")    : "" );
				domicilio.setBarrio(Validator.isNotNull(rs.getString(prefix + "barrio"))? rs.getString(prefix + "barrio")    : "" );				
				domicilio.setCalle(Validator.isNotNull(rs.getString(prefix + "calle"))? rs.getString(prefix + "calle")    : "" );
				domicilio.setPiso(Validator.isNotNull(rs.getString(prefix + "piso"))? rs.getString(prefix + "piso")    : "" );
				domicilio.setDepto(Validator.isNotNull(rs.getString(prefix + "calle"))? rs.getString(prefix + "depto")    : "" );
				domicilio.setProvinciaId(Validator.isNotNull(rs.getInt(prefix + "provincia"))? rs.getInt(prefix + "provincia")    : 0 );
				domicilio.setLocalidadId(Validator.isNotNull(rs.getInt(prefix + "localidad"))? rs.getInt(prefix + "localidad")    : 0 );
				equi.setTipoDomicilio(rs.getString(prefix + "tipo_domicilio"));
				domicilio.setPostal_codi(Validator.isNotNull(rs.getString(prefix + "postal_codi"))? rs.getString(prefix + "postal_codi")    : "" );
				equi.setAfiliado(new Afiliado(cuil_titular ,inte));
				equi.getAfiliado().setEmail(Validator.isNotNull(rs.getString(prefix + "email"))? rs.getString(prefix + "email")    : "" );
				equi.getAfiliado().setDomicilioDefault(domicilio);
				
			}
			catch (Exception e ){
			    throw e;
			}
					
			return equi;
		}		

	public static EquipoInterdisciplinario  getMapping(ResultSet rs, String prefix)
			throws Exception {
		
		
		EquipoInterdisciplinario equi = new EquipoInterdisciplinario();
		
		try{			
			
		equi.setId(rs.getInt(prefix + "id"));
		equi.setNroRegistroequipoInter(rs.getInt(prefix + "id"));
		equi.setFechaRegistro(rs.getDate(prefix + "fecha"));		
		equi.setAfiliado(new Afiliado(rs.getString(prefix + "cuil_titular"), rs.getInt(prefix + "inte")));
		// graba el email que va a contactos_e 
		equi.getAfiliado().setEmail(Validator.isNotNull(rs.getString(prefix + "email"))? rs.getString(prefix + "email")    : "" );		
		equi.setIdEmail(Validator.isNotNull(rs.getInt(prefix + "idcontacto_e"))? rs.getInt(prefix + "idcontacto_e")    : 0 );		 
		
		Domicilio domicilio=new Domicilio();
		domicilio.setProvinciaId(Validator.isNotNull(rs.getInt(prefix + "provincia"))? rs.getInt(prefix + "provincia")    : 0 );
		domicilio.setLocalidadId(Validator.isNotNull(rs.getInt(prefix + "localidad"))? rs.getInt(prefix + "localidad")    : 0 );		
		domicilio.setCalle(Validator.isNotNull(rs.getString(prefix + "calle"))? rs.getString(prefix + "calle")    : "" );
		domicilio.setNumero(Validator.isNotNull(rs.getString(prefix + "numero_calle"))? rs.getString(prefix + "numero_calle")    : "" );
		domicilio.setDepto(Validator.isNotNull(rs.getString(prefix + "calle"))? rs.getString(prefix + "depto")    : "" );
		domicilio.setPiso(Validator.isNotNull(rs.getString(prefix + "piso"))? rs.getString(prefix + "piso")    : "" );
		domicilio.setBarrio(Validator.isNotNull(rs.getString(prefix + "barrio"))? rs.getString(prefix + "barrio")    : "" );
		domicilio.setPostal_codi(Validator.isNotNull(rs.getString(prefix + "postal_codi"))? rs.getString(prefix + "postal_codi")    : "" );
		equi.getAfiliado().setDomicilioDefault(domicilio);
		
		equi.setEstadoRegEquipoInter( rs.getString(prefix + "estado"));
	    equi.setParticipantes(rs.getString(prefix + "participantes"));
	    equi.setObservaciones(rs.getString(prefix + "observacion"));
	    equi.setBaja_fecha(rs.getDate(prefix + "baja_fecha"));
	    equi.setDiagnosticoAfiliado(rs.getString(prefix + "diagnostico"));
	    equi.setCodigoCie10(rs.getString(prefix + "cie_10"));
		equi.setTelefonoContacto( new Telefono());
		equi.getTelefonoContacto().setCodigoArea(rs.getString(prefix + "codigo_area"));
		equi.getTelefonoContacto().setNumero(rs.getString(prefix + "numero"));		
		equi.getTelefonoContacto().setTipo(rs.getString(prefix + "tipo_tele"));
	    equi.getTelefonoContacto().setId(rs.getInt(prefix + "id_telefono"));
	    equi.setTipoDomicilio(rs.getString(prefix + "tipo_domicilio"));	    
	    equi.setMotivoCierreEquipoInter(rs.getString(prefix + "motivo_cierre"));
	    
		}
		catch (Exception e ){
		    throw e;
		}
				
		return equi;
	}

	public static EquipoInterdisciplinario  getMappingBuscador(ResultSet rs, String prefix)
			throws Exception {
		
		EquipoInterdisciplinario ei = new EquipoInterdisciplinario();
		
		try{			
			
		ei.setId(rs.getInt(prefix + "id"));
		ei.setNroRegistroequipoInter(rs.getInt(prefix + "id"));
		ei.setAfiliado(new Afiliado(rs.getString(prefix + "seccional"),  rs.getString(prefix + "cuil_titular"), rs.getInt(prefix + "inte") , rs.getString(prefix + "nombre"), rs.getString(prefix + "apellido")) );
		ei.setAlta_fecha(rs.getDate(prefix + "fecha"));		
		ei.setEstadoRegEquipoInter(rs.getString(prefix + "estado") );
		ei.setObservaciones(rs.getString(prefix + "observacion"));
		ei.setParticipantes(rs.getString(prefix + "participantes"));
		ei.setBaja_fecha(rs.getDate(prefix + "baja_fecha"));
		
		}
		catch (Exception e ){
		    throw e;
		}
				
		return ei;
	}

	
	
	
	
	

	public String getParticipantes () {
		return participantesRegistro;
	}

	public void setParticipantes (String participantes ) {
		this.participantesRegistro= participantes ;
	}

	
	public String getObservaciones() {
		return observacionRegistro;
	}

	public void setObservaciones(String observaciones) {
		this.observacionRegistro = observaciones;
	}

	

	public Integer getNroRegistroequipoInter () {
		return nroRegistro;
	}

	public void setNroRegistroequipoInter  (Integer nroEquipo ) {
		this.nroRegistro= nroEquipo ;
	}

	public String get_fechaAsString() {
		return fechaRegistro != null ? DateUtils.format(fechaRegistro, "dd/MM/yyyy") : "";
	}

	public Date getFechaRegistro() {
		return fechaRegistro;
	}

	public void setFechaRegistro(Date fechaRegistro) {
		this.fechaRegistro = fechaRegistro;
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
	
	public List<PrestacionesEquipoInterdisciplinario> getPrestaciones () {
		return prestaciones;
	}
	
	public void setPrestaciones (List<PrestacionesEquipoInterdisciplinario> prestaciones ) {
		this.prestaciones= prestaciones;
	}
	
	public void setDictamenes( String [] dictamenes) { 
		this.dictamenes = dictamenes;
		this.dictamenesOriginales= dictamenes;
	}
	
	public String getMotivoCierreEquipoInter () {
		return motivoCierre;
	}

	public void setMotivoCierreEquipoInter (String motivoCierre ) {
		this.motivoCierre = motivoCierre;
	}
	
		
	
	
	public String getEstadoRegEquipoInter () {
		return estadoRegistroeq;
	}

	public void setEstadoRegEquipoInter (String estadoEquipoInter ) {
		this.estadoRegistroeq= estadoEquipoInter ;
	}


	public int getIdEmail(){
		return idEmail; 
	}
	public void setIdEmail ( int idEmailcontacto ) { 
		idEmail = idEmailcontacto; 
	}
	
	public String getDiagnosticoAfiliado() {
		return diagnosticoAfiliado;
	}

	public void setDiagnosticoAfiliado(String diagnosticoAfiliado) {
		if (diagnosticoAfiliado == null ) {
			this.diagnosticoAfiliado = "";
		}else{
			this.diagnosticoAfiliado = diagnosticoAfiliado;
		}
				
	}

	
	public String getTipoDomicilio () {		
		return tipoDomicilio ;
	}

	public void setTipoDomicilio (String tipoDomicilio ) {
		if (tipoDomicilio == null ) {
			this.tipoDomicilio = "";
		}else{
			this.tipoDomicilio = tipoDomicilio ;
		}
	}

	public String getCodigoCie10 () {		
		return this.codigoCie10  ;
	}
	
	public void setCodigoCie10(String codigoCie10) {
		if (codigoCie10== null ) {
			this.codigoCie10 = "";
		}else{
			this.codigoCie10 = codigoCie10;
		}
		
	}
	
	public void setCambioDiagnosticoCie10 (boolean valor) {
		this.cambioDiagnostico = valor;
	}	
	public boolean isCambioDiagnosticoCie10()  {
		return this.cambioDiagnostico ;
	}

	public void setCambioCambioTelefono (boolean valor) {
		this.cambioTelefono = valor;
	}	
	public boolean isCambiocambioTelefono()  {
		return this.cambioTelefono ;
	}
	
	
    public Telefono getTelefonoContacto(){
	  return this.telefonoDeContacto ;
    }
	
    public void setTelefonoContacto(Telefono telefono) {
    	this.telefonoDeContacto = telefono;
	} 	
    
    public Boolean isCambioDomicilioAfiliadoRegEquipo() {
		return cambioDomicilio;
	}

	public void setCambioDomicilio(Boolean cambioDomicilio) {
		this.cambioDomicilio = cambioDomicilio;
	}
	
	public void setDictamenesOrigianles(String[] dictamenes ){
		dictamenesOriginales = dictamenes; 
	}
	public String[] getDictamenOriginales() throws Exception {
		return dictamenesOriginales;
		
	}
	public String getDictamen(DICTAMENES tipoDictamen ) throws Exception {
		String valor="" ;
		try{
			if ( dictamenes[tipoDictamen.ordinal() ] ==  null  || dictamenes[tipoDictamen.ordinal() ].isEmpty()    ) {
				return  valor   ;
			}else {
				valor = dictamenes[tipoDictamen.ordinal() ] ; 
				return  valor  ;	
			}	
		}
		 catch (Exception e ){
			_log.debug("errores con los dictamenes de la base de datos para el equipo", e);
		} 
	return valor;
	}
	public void setDictamen (DICTAMENES tipoDictamen  , String dictamen ) {
		dictamenes[tipoDictamen.ordinal()] =dictamen ;
	}
	public boolean isCambioElDictamen (DICTAMENES tipoDictamen   ) {
		if ( dictamenes[tipoDictamen.ordinal() ].equals("")  &&   dictamenesOriginales[tipoDictamen.ordinal() ] == null )
		{
			return false;
		}else{
		    return  ! dictamenes[tipoDictamen.ordinal()].equals(dictamenesOriginales[tipoDictamen.ordinal() ])   ; 
		}
	}
	
	public boolean isCambioEmailAfiliado ()  {
		return this.cambioEmailAfiliado ;
	}
	public void setCambioEmailAfiliado (boolean valor) {
		this.cambioEmailAfiliado = valor;
	}

	public List<FirmaAutorizante> getFirmaAutorizante() {
		return firmaAutorizante;
	}

	public void setFirmaAutorizante(List<FirmaAutorizante> firmaAutorizante) {
		this.firmaAutorizante = firmaAutorizante;
	}
}