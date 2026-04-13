package ar.com.ospim.autorizaciones.beans;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.liferay.portal.PortalException;
import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.dao.orm.Criterion;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQueryFactoryUtil;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.portlet.PortletClassLoaderUtil;
import com.liferay.portlet.documentlibrary.model.DLFileEntry;
import com.liferay.portlet.documentlibrary.model.DLFolder;
import com.liferay.portlet.documentlibrary.service.DLFolderLocalServiceUtil;

import ar.com.ospim.afiliados.NoSuchAfiliadoEntryException;
import ar.com.ospim.afiliados.beans.Afiliado;
import ar.com.ospim.afiliados.services.EditarAfiliadoServiceUtil;
import ar.com.ospim.crm.beans.ContactoCRM;
import ar.com.ospim.global.beans.Prestacion;
import ar.com.ospim.global.beans.Seccional;
import ar.com.ospim.liquidaciones.beans.Prestador;
import ar.com.ospim.util.DateUtils;
import ar.com.ospim.util.StringUtils;

	
public class ReclamoPrestacional  implements Serializable {
	
	private static final long serialVersionUID = -6102078435078459505L;
	private int id;
	private Afiliado afiliado;
	private Afiliado afiliadoTitular;
	private Prestacion prestacion;
	
	private Date altaFecha;
	
	private Date fechaCierre;
	private String reclamoObservacionCierre;
	private int tipoGestionCierreReclamo;
	private boolean reclamoPsFacturaOspim;
	private boolean reclamoAnegociar;
	private boolean recuperableSur;
	
	private boolean reclamoConvenioGerenciadora;
	
	private boolean dosPorCiento;
	
	private boolean debitoPrestadora ;

	// op del reclamo 
	private int  idOP;
    private Date fechaOP;
    private int id_lista_reintegro ;    
    private BigInteger  chequeOP;
    private String datoOrdenPago; 
    private String ctaDescrpcion;
    private int ctaNro;    
    private int ctaSucursal;
    
    
	private Date ospimFecha;
	private Date seccionalFecha;	
	private String altaUsr;
	private Date modiFecha;
	private Integer idSeccional;
	private String modiUsr;
	private Date bajaFecha;
	private String bajaUsr;
	private Prestador prestador;
	private int estado;
	private String estadoObservacion;
	private boolean recuperaApe;
	private String observaciones;
	private Seccional seccional;
    private String sector; 
    private String  tipoPedido ;
    private Integer nroReclamo;
    private String motivoExcepcion;
    private String motivoExcepcionDescripcion;
    private String motivoEstado;
    
    private String justificaconMedica ;
    private String dictamenComision;
    
    private String diagnosticoAfiliado ;
    private String codigoCie10;
        	
	private boolean discapacitado;
    private List<PrestacionesReclamo> prestaciones;
    private List<PrestacionesReclamo> prestacionesasociadas;
    private List<RevisionesReclamo> revisiones ;

    private List<ContactoCRM> contactos ;
    private String estadoReclamoPrestacion;
    private  int estadoreclamo;   
    
    private boolean  amparo;
    private boolean  recuperable;       
	private boolean  superintendencia;
    private boolean  entramite;
    private int casoVinculado;
    private ESTADOSEVALUACIONRECLAMO  estadoAutorizacionResolucionReclamo=ESTADOSEVALUACIONRECLAMO.SINVALOR;        

    private Integer nroLote;
    
    
    private ESTADOSRECLAMO estadoReclamoTipo;
    
    private boolean marcaReabrirReclamo;
    
    private Date  fechaMailSeccional;
    private int idObservacionMedica;
    private String descObservacionMedica;
    private ReclamoPrestacionalCuenta cuenta;
    
    private int codigoIntegracion;
    private String marcaSeccional;   
    
    private int idReintegroApp;
    private String urlComprobante;
    private String userCbu;
    private String userCbuConstUrl;
    private String urlDocExtra;
    private String reintegroCuil;
    private int titular;
    private String cbuAutorizante;
    
    private String nroComprobante;
	private String tipoComprobante;
	private String letraComprobante;
	private int sucuComprobante;
	private BigDecimal importeComprobante;
	private String cuitPrestador;
    private String cuilTitular;
    private String cuilTitularCuenta;
    
    public enum ESTADOSEVALUACIONRECLAMO {
		SINVALOR,SINEVALUACION, AUTORIZADA, RECHAZADA
	};
	
	public static enum ESTADOSRECLAMO {
		PENDIENTE, ANULADO, CERRADO, INCOMPLETO, PRECARGA, OBSERVADO 
	};
	 		
	
	public ReclamoPrestacional () {
	}

	public ReclamoPrestacional (String cuilTitular, int inte,	Date altaFecha, String sector,Date fechaSeccional ) {
		
		this.afiliado = new Afiliado(cuilTitular, inte);
		this.altaFecha = altaFecha;
		this.ospimFecha= altaFecha;				
		this.seccionalFecha = fechaSeccional;
		this.sector =  sector;		
	
	}

    public ReclamoPrestacional (String cuilTitular, int inte,	Date altaFecha, String sector,Date fechaSeccional, int estadop
    		, Date fechaCierre , String reclamoObservacionCierre  , int tipoGestionCierreReclamo , boolean reclamoPsFacturaOspim
    		, boolean reclamoAnegociar , boolean superIntendencia ,boolean  amparo ,boolean  recuperable ,boolean  entramite
    		, boolean incluidoConvenioGerenciadora  ,int caso_vinculado ,boolean dosporciento, String dictamenComision, String justificacionMedica  
    		, String diagnostico , String codigoCie10, String  tipopedido,
    		boolean debitoPrestadora , ReclamoPrestacional.ESTADOSEVALUACIONRECLAMO  evaluacionreclamo ,Afiliado afi , int idObservacionMedica , int CodIntegracion) throws NoSuchAfiliadoEntryException, SystemException    
    {			
		
    	if (afi==null){
    		this.afiliado = EditarAfiliadoServiceUtil.getAfiliadoEntryInclusoDadoBaja(cuilTitular, inte);
    	}else{
    		this.setAfiliado(afi);
    	}
  			
		this.altaFecha = altaFecha;
		this.ospimFecha= altaFecha;				
		this.seccionalFecha = fechaSeccional;
		this.sector =  sector;
		this.estado=estadop;		
		this.fechaCierre =fechaCierre ;
		this.reclamoObservacionCierre= reclamoObservacionCierre;  
		this.tipoGestionCierreReclamo=tipoGestionCierreReclamo; 
		this.reclamoPsFacturaOspim=reclamoPsFacturaOspim;
		this.reclamoAnegociar=reclamoAnegociar;
		this.setRecuperable(recuperable );
		this.setSuperintendencia(superIntendencia);
		this.setAmparo(amparo);
		this.setEntramite(entramite);
		this.setReclamo_convenio_gerenciadora(incluidoConvenioGerenciadora);
		this.setDosPorciento(dosporciento);
		this.setCaso_vinculado(caso_vinculado);
		this.setJustificaconMedica(justificacionMedica);
		this.setDictamenComision(dictamenComision);
		this.setDiagnosticoAfiliado(diagnostico);
		this.setCodigoCie10(codigoCie10);
		this.setTipoPedido(tipopedido);
		this.setEstadoResolucionAutorizada(evaluacionreclamo);
		this.setDebitoPrestadora(debitoPrestadora );
		this.setIdObservacionMedica(idObservacionMedica);
		this.setCuenta(cuenta);
		this.setCodigoIntegracion(CodIntegracion);
	}

	public String getReclamo_observacion_cierre() {
		return reclamoObservacionCierre;
	}

	public void setReclamo_observacion_cierre(String reclamoObservacionCierre) {
		this.reclamoObservacionCierre = reclamoObservacionCierre;
	}

	public int getTipo_gestion_cierre_reclamo() {
		return tipoGestionCierreReclamo;
	}

	public void setTipo_gestion_cierre_reclamo(int tipoGestionCierreReclamo) {
		this.tipoGestionCierreReclamo = tipoGestionCierreReclamo;
	}

	
	
	
	public boolean isRecuperableSur() {
		return this.recuperableSur;
	}

	public void setRecuperableSur(boolean recuperableSur) {
		this.recuperableSur= recuperableSur;
	}

	
	public boolean isReclamo_ps_factura_ospim() {
		return reclamoPsFacturaOspim;
	}

	public void setReclamo_ps_factura_ospim(boolean reclamoPsFacturaOspim) {
		this.reclamoPsFacturaOspim = reclamoPsFacturaOspim;
	}

	public boolean isReclamo_a_negociar() {
		return reclamoAnegociar;
	}

	public void setReclamo_a_negociar(boolean reclamoAnegociar) {
		this.reclamoAnegociar = reclamoAnegociar;
	}

	public int getId_reclamo() {
		return id;
	}

	public String getId_String() {
		return String.valueOf(id);
	}

	public void setId(int idReclamo ) {
		id = idReclamo ;
	}
			
	public String  getFecha_cierre_Texto(){
		return fechaCierre != null ? DateUtils.format(fechaCierre, "dd/MM/yyyy") : "";	
	}
		
	
	public Date getFecha_cierre() {
		return fechaCierre;
	}

	public void setFecha_cierre(Date fechaCierreData) {
	  this.fechaCierre = fechaCierreData;
	}
	
	public ESTADOSEVALUACIONRECLAMO getEstadoResolucionAutorizada(){
		return estadoAutorizacionResolucionReclamo;
	}
	
	public String getEstadoResolucionAutorizadaString(){		
		String estadostextos [] = {"Sin Valor","Sin Evaluacion","Autorizado","Rechazado"}; 
		return estadostextos[estadoAutorizacionResolucionReclamo.ordinal() ]  ;
	}
	
	public void  setEstadoResolucionAutorizada(ESTADOSEVALUACIONRECLAMO valor ){
		estadoAutorizacionResolucionReclamo = valor ;
	}
	
	public void  setEstadoReclamo(ESTADOSRECLAMO  valor ){
		estadoReclamoTipo= valor ;
	}
	
	public ESTADOSRECLAMO   getEstadoReclamo (){
		return estadoReclamoTipo;
	}
	
	public static int getEstadoObservado () {
	    /* Review */
		return 5;
	}
	
	public void setEstadoReclamoxValor( int valor ){
		if (valor==1){		
			estadoReclamoTipo=this.estadoReclamoTipo.PENDIENTE;
		}
		if (valor==2){		
			estadoReclamoTipo=this.estadoReclamoTipo.ANULADO;
		}
		if (valor==3){		
			estadoReclamoTipo=this.estadoReclamoTipo.CERRADO;
		}
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

	public String getSector() {
		return this.sector;
	}

	public void setSector(String sector) {
		this.sector = sector;
	}

	public String  getTipoPedido () {
		return this.tipoPedido ;
	}

	public void setTipoPedido (String tipoPedidoValor ) {
		this.tipoPedido = tipoPedidoValor ;
	}
	 
	public String getBaja_fechaAsString() {
		return bajaFecha != null ? DateUtils.format(bajaFecha, "dd/MM/yyyy")
				: "";
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

	public Prestacion getPrestacion() {
		return prestacion;
	}

	public void setPrestacion(Prestacion prestacion) {
		this.prestacion = prestacion;
	}


	public static ReclamoPrestacional getMapping(ResultSet rs, String prefix)
			throws Exception {
		
		
		ReclamoPrestacional rp = new ReclamoPrestacional();

		rp.setId(rs.getInt(prefix + "id_reclamo"));
		rp.setNroReclamo(rs.getInt(prefix + "id_reclamo"));
		rp.setOspim_fecha(rs.getDate(prefix + "fecha_ospim"));
		rp.setSeccional_fecha(rs.getDate(prefix + "fecha_seccional"));
		rp.setSector(rs.getString(prefix + "sector"));	
		Afiliado afiliado;
	
    	afiliado = EditarAfiliadoServiceUtil.getAfiliadoEntryInclusoDadoBaja(rs.getString(prefix + "cuil_titular"), rs.getInt(prefix + "inte"));
    	rp.setAfiliado(afiliado);
    	//rp.setAfiliado(new Afiliado(rs.getString(prefix + "cuil_titular"), rs.getInt(prefix + "inte")));

    	rp.setAlta_fecha(rs.getDate(prefix + "fecha_ospim"));
		rp.setEstado(rs.getInt(prefix + "id_estado"));
		
		// Agregado de Observacion de Estado (actualmente, solo para estado 5 -> Observado)
		rp.setEstadoObservacion(rs.getString(prefix + "observacion"));
		
		rp.setEstadoReclamoxValor(rs.getInt(prefix + "id_estado"));
		rp.setTipo_gestion_cierre_reclamo(rs.getInt(prefix + "tipo_gestion"));
		rp.setFecha_cierre(rs.getDate(prefix + "fecha_cierre"));
		rp.setReclamo_observacion_cierre(rs.getString(prefix + "observacion_cierre"));
		rp.setReclamo_ps_factura_ospim(rs.getBoolean(prefix + "factura_ospim")); 
		rp.setReclamo_a_negociar(rs.getBoolean(prefix + "a_negociar"));
		rp.setSuperintendencia(rs.getBoolean(prefix + "superintendencia"));
		rp.setAmparo(rs.getBoolean(prefix + "amparo"));
		rp.setRecuperable(rs.getBoolean(prefix + "recuperable"));		
		rp.setDebitoPrestadora(rs.getBoolean(prefix + "debito_prestadora")); 
		rp.setEntramite(rs.getBoolean(prefix + "entramite"));
		rp.setReclamo_convenio_gerenciadora(rs.getBoolean(prefix + "incluidoconveniogerenciadora"));
		rp.setDosPorciento(rs.getBoolean(prefix + "dosporciento"));
		rp.setCaso_vinculado(rs.getInt(prefix + "caso_vinculado"));
		rp.setJustificaconMedica(rs.getString(prefix + "justificacion_medica")) ;
		rp.setDictamenComision(rs.getString(prefix + "dictamen_comision")) ;
		rp.setDiagnosticoAfiliado(rs.getString(prefix + "diagnostico")) ;
		rp.setCodigoCie10(rs.getString(prefix + "cie10")) ;
		rp.setTipoPedido(rs.getString(prefix + "tipopedido"));		
		rp.setEstadoResolucionAutorizada(rs.getString(prefix + "evaluacion") == null ? ESTADOSEVALUACIONRECLAMO.SINEVALUACION  : ESTADOSEVALUACIONRECLAMO.SINVALOR );		
		if (rp.getEstadoResolucionAutorizada()== ESTADOSEVALUACIONRECLAMO.SINVALOR){ // evaluar respuesta
			rp.setEstadoResolucionAutorizada(ESTADOSEVALUACIONRECLAMO.RECHAZADA );
			if (rs.getString(prefix + "evaluacion").equals("AUTORIZADO") ){
				rp.setEstadoResolucionAutorizada(ESTADOSEVALUACIONRECLAMO.AUTORIZADA);
			} 
		}
		
		int seccional = rs.getInt(prefix + "id_seccional");		
		
		rp.setSeccional(new Seccional(seccional, null));
		rp.setAlta_fecha(rs.getTimestamp(prefix + "alta_fecha"));
		rp.setModi_fecha(rs.getTimestamp(prefix + "modi_fecha"));
		rp.setAlta_usr(rs.getString(prefix + "alta_usr"));
		rp.setModi_usr(rs.getString(prefix + "modi_usr"));
		
		rp.setNroLote(rs.getInt(prefix + "nro_lote"));
		
		rp.setFechaMailSeccional(rs.getTimestamp(prefix + "fecha_mail_seccional"));
		rp.setIdSeccional(rs.getInt(prefix + "seccional_carga"));

		rp.setIdObservacionMedica(rs.getInt(prefix + "id_observacion_medica"));
		rp.setDescObservacionMedica(rs.getString(prefix + "observaciones_area_medica"));
		
		try{			
			rp.setCodigoIntegracion(rs.getInt(prefix + "codigo_integracion"));
		}catch (Exception e) {
			rp.setCodigoIntegracion(0);
		}
		
		try {
		    int v = rs.getInt(prefix + "id_reintegro_app");
		    if (!rs.wasNull()) {
		        rp.setIdReintegroApp(v);
		    } else {
		        rp.setIdReintegroApp(0);
		    }
		} catch (Exception e) {
		    rp.setIdReintegroApp(0);
		}

		return rp;
	}
	
	

	public static ReclamoPrestacional getMappingPorOP(ResultSet rs, String prefix)
			throws Exception {
		
		
		ReclamoPrestacional rp = new ReclamoPrestacional();

		rp.setId(rs.getInt(prefix + "id_reclamo"));
		
		
		//TODO implementar mas campos

		return rp;
	}

	public static ItemReclamoPrestacionalesTotal getMappingBuscadorTotal(ResultSet rs, String prefix) throws Exception {
		

		ItemReclamoPrestacionalesTotal reclamo = new ItemReclamoPrestacionalesTotal();
		
		try{		
			
			
		reclamo.setId(rs.getInt(prefix + "id_reclamo"));
		reclamo.setNroReclamo(rs.getInt(prefix + "id_reclamo"));
		reclamo.setOspim_fecha(rs.getDate(prefix + "fecha_ospim"));
		reclamo.setSeccional_fecha(rs.getDate(prefix + "fecha_seccional"));
		reclamo.setSector(rs.getString(prefix + "sector"));		
		reclamo.setAfiliado(new Afiliado(rs.getString(prefix + "cuil_titular"), rs.getInt(prefix + "inte")));
		reclamo.setAlta_fecha(rs.getDate(prefix + "fecha_ospim"));
		reclamo.setEstado(rs.getInt(prefix + "id_estado"));
		reclamo.setBaja_fecha(rs.getDate(prefix + "baja_fecha"));
		reclamo.setModi_fecha(rs.getDate(prefix + "modi_fecha"));
		reclamo.setEstadoReclamoPrestacion(rs.getString(prefix + "estadoreclamo") );
		reclamo.setEstadoreclamo(rs.getInt(prefix + "id_estadoreclamo") );
		reclamo.setTipoPedido(rs.getString(prefix + "tipopedido") );
		int seccional = rs.getInt(prefix + "id_seccional");
	    reclamo.setPagoDatoCompleto(rs.getString(prefix + "ordendepago") );
		 
	    reclamo.setSeccional(new Seccional(seccional, null));
	    reclamo.setFechaMailSeccional(rs.getDate(prefix + "fecha_mail_seccional") );
	    reclamo.setPrestacionesConcat(rs.getString(prefix + "prestaciones"));
	    
	    reclamo.setTotal_registros(rs.getInt("total"));
	    
		}
		catch (Exception e ){
		    throw e;
		}
				
		return reclamo;

	}

	
	public static ReclamoPrestacional getMappingBuscador(ResultSet rs, String prefix)
			throws Exception {
		
		
		ReclamoPrestacional rp = new ReclamoPrestacional();
		
		rp.setId(rs.getInt(prefix + "id_reclamo"));
		rp.setNroReclamo(rs.getInt(prefix + "id_reclamo"));
		rp.setOspim_fecha(rs.getDate(prefix + "fecha_ospim"));
		rp.setSeccional_fecha(rs.getDate(prefix + "fecha_seccional"));
		rp.setSector(rs.getString(prefix + "sector"));		
		rp.setAfiliado(new Afiliado(rs.getString(prefix + "cuil_titular"), rs.getInt(prefix + "inte")));
		rp.setAlta_fecha(rs.getDate(prefix + "fecha_ospim"));
		rp.setEstado(rs.getInt(prefix + "id_estado"));
		rp.setBaja_fecha(rs.getDate(prefix + "baja_fecha"));
		rp.setModi_fecha(rs.getDate(prefix + "modi_fecha"));
		rp.setEstadoReclamoPrestacion(rs.getString(prefix + "estadoreclamo") );
		rp.setEstadoreclamo(rs.getInt(prefix + "id_estadoreclamo") );
		rp.setTipoPedido(rs.getString(prefix + "tipopedido") );
		int seccional = rs.getInt(prefix + "id_seccional");
	    rp.setPagoDatoCompleto(rs.getString(prefix + "ordendepago") );
		 
		rp.setSeccional(new Seccional(seccional, null));
				
		return rp;
	}
	
	public Prestador getPrestador() {
		return prestador;
	}

	public void setPrestador(Prestador prestador) {
		this.prestador = prestador;
	}

	public int getEstado() {
		return estado;
	}

	public void setEstado(int estado) {
		this.estado = estado;
	}

	public void setEstadoObservacion(String estadoObservacion) {
		this.estadoObservacion = estadoObservacion;
	}

	public String getEstadoObservacion() {
		return this.estadoObservacion;
	}

	public boolean isRecupera_ape() {
		return recuperaApe;
	}

	public void setRecupera_ape(boolean recuperaApe) {
		this.recuperaApe = recuperaApe;
	}

	public String getObservaciones() {
		return observaciones;
	}

	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}


	public void setSeccional(Seccional seccional) {
		this.seccional = seccional;
	}

	public Seccional getSeccional() {
		return seccional;
	}

	public Integer getNroReclamo () {
		return nroReclamo;
	}

	public void setNroReclamo (Integer nroReclamo ) {
		this.nroReclamo= nroReclamo ;
	}

	public String getMotivoExcepcion() {
		return motivoExcepcion;
	}

	public void setMotivoExcepcion(String motivoExcepcion) {
		this.motivoExcepcion = motivoExcepcion;
	}

	public String getMotivoExcepcionDescripcion() {
		return motivoExcepcionDescripcion;
	}

	public void setMotivoExcepcionDescripcion(String motivoExcepcionDescripcion) {
		this.motivoExcepcionDescripcion = motivoExcepcionDescripcion;
	}

	public String getMotivoEstado() {
		return motivoEstado;
	}

	public void setMotivoEstado(String motivoEstado) {
		this.motivoEstado = motivoEstado;
	}

	public boolean isDiscapacitado() {
		return discapacitado;
	}

	public void setDiscapacitado(boolean discapacitado) {
		this.discapacitado = discapacitado;
	}

	public Date getOspim_fecha() {
		return ospimFecha;
	}

	public void setOspim_fecha(Date ospim_fecha) {
		this.ospimFecha = ospim_fecha;
	}

	public String getOspim_fechaAsString() {
		return ospimFecha != null ? DateUtils.format(ospimFecha , "dd/MM/yyyy") : "";
	}
	
	
	public String getSeccional_fechaAsString() {
		return seccionalFecha != null ? DateUtils.format(seccionalFecha, "dd/MM/yyyy") : "";
	}
	
	public Date getSeccional_fecha() {
		return seccionalFecha;
	}

	public void setSeccional_fecha(Date seccional_fecha) {
		this.seccionalFecha = seccional_fecha;
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
	
	public List<PrestacionesReclamo> getPrestaciones () {
		return prestaciones;
	}
	
	public void setPrestaciones (List<PrestacionesReclamo> prestaciones ) {
		this.prestaciones= prestaciones;
	}
	
	public List<PrestacionesReclamo> getPrestacionesAsociadas () {
		return prestacionesasociadas;
	}
	
	public void setPrestacionesAsociadas (List<PrestacionesReclamo> prestacionesAsociadas  ) {
		this.prestacionesasociadas= prestacionesAsociadas ;
	}
	
	
	
	public List<RevisionesReclamo> getRevisiones () {
		return revisiones ;
	}
	
	public void setRevisiones (List<RevisionesReclamo> revisiones  ) {
		this.revisiones = revisiones  ;
	}
	public void setContactosCRM (List<ContactoCRM> contactosCRM) {
		this.contactos = contactosCRM;
	}
	
	public List<ContactoCRM> getContactosCRM  () {
		return this.contactos ;
	}
	
	
	public String getEstadoReclamoPrestacion() {
		return estadoReclamoPrestacion;
	}

	public void setEstadoReclamoPrestacion(String estadoReclamoPrestacion) {
		this.estadoReclamoPrestacion = estadoReclamoPrestacion;
	}

	public int getEstadoreclamo() {
		return estadoreclamo;
	}

	public void setEstadoreclamo(int estadoreclamo) {
		this.estadoreclamo = estadoreclamo;
	}
	
	public boolean isAmparo() {
		return amparo;
	}

	public void setAmparo(boolean amparo) {
		this.amparo = amparo;
	}

	public boolean isRecuperable() {
		return recuperable;
	}

	public void setRecuperable(boolean recuperable) {
		this.recuperable = recuperable;
	}

	public boolean isSuperintendencia() {
		return superintendencia;
	}

	public void setSuperintendencia(boolean superintendencia) {
		this.superintendencia = superintendencia;
	}

	public boolean isEntramite() {
		return entramite;
	}

	public void setEntramite(boolean entramite) {
		this.entramite = entramite;
	}
	

	public boolean isReclamo_convenio_gerenciadora() {
		return reclamoConvenioGerenciadora;
	}

	public void setReclamo_convenio_gerenciadora(boolean reclamoConvenioGerenciadora) {
		this.reclamoConvenioGerenciadora = reclamoConvenioGerenciadora;
	}
	
	public boolean isDosPorciento () {
		return dosPorCiento;
	}

	public void setDosPorciento (boolean valDosPorCiento) {
		this.dosPorCiento =  valDosPorCiento;
	}
	
	public boolean isDebitoPrestadora () {
		return debitoPrestadora ;
	}

	public void setDebitoPrestadora (boolean valDebitoPrestadora) {
		this.debitoPrestadora =  valDebitoPrestadora;
	}

	public int getCaso_vinculado() {
		return casoVinculado;
	}

	public void setCaso_vinculado(int caso_vinculado) {
		this.casoVinculado = caso_vinculado;
	}

	public String getJustificaconMedica() {
		return justificaconMedica;
	}

	public void setJustificaconMedica(String justificaconMedica) {
		this.justificaconMedica = justificaconMedica;
	}

	public String getDictamenComision() {
		return dictamenComision;
	}

	public void setDictamenComision(String dictamenComision) {
		this.dictamenComision = dictamenComision;
	}
	
		
	public String getOrdenPagoDatoCompleto () {
		return datoOrdenPago;
	}

	public void setPagoDatoCompleto (String datoOrden ) {
		this.datoOrdenPago  = datoOrden ;
	}
	public int getIdOP() {
		return idOP;
	}

	public void setIdOP(int idOP) {
		this.idOP = idOP;
	}

	public BigInteger getChequeOP() {
		return chequeOP;
	}

	public void setChequeOP(BigInteger chequeOP) {
		this.chequeOP = chequeOP;
	}

	public int getId_lista_reintegro() {
		return id_lista_reintegro;
	}

	public void setId_lista_reintegro(int idListaReintegro) {
		id_lista_reintegro = idListaReintegro;
	}
	
	
	public String getfechaOPAsString() {
		return fechaOP != null ? DateUtils.format(fechaOP, "dd/MM/yyyy")
				: "";
	}
	

	public Date getFechaOP() {
		return fechaOP;
	}

	public void setFechaOP(Date fechaOP) {
		this.fechaOP = fechaOP;
	}

	public String getDiagnosticoAfiliado() {
		return diagnosticoAfiliado;
	}

	public void setDiagnosticoAfiliado(String diagnosticoAfiliado) {
		this.diagnosticoAfiliado = diagnosticoAfiliado;
	}

	public String getCodigoCie10() {
		return codigoCie10;
	}

	public void setCodigoCie10(String codigoCie10) {
		this.codigoCie10 = codigoCie10;
	}

	public Integer getNroLote() {
		return nroLote;
	}

	public void setNroLote(Integer nroLote) {
		this.nroLote = nroLote;
	}

	public boolean isMarcaReabrirReclamo() {
		return marcaReabrirReclamo;
	}

	public void setMarcaReabrirReclamo(boolean marcaReabrirReclamo) {
		this.marcaReabrirReclamo = marcaReabrirReclamo;
	}

	public Integer getIdSeccional() {
		return idSeccional;
	}

	public void setIdSeccional(Integer idSeccional) {
		this.idSeccional = idSeccional;
	}

	

	public Date getFechaMailSeccional() {
		return fechaMailSeccional;
	}

	public void setFechaMailSeccional(Date fechaMailSeccional) {
		this.fechaMailSeccional = fechaMailSeccional;
	}
	
	public int getCantidadImagenes(int idReclamo) {

		int cant = 0;

		List<String> listStrings = new ArrayList<String>();
		listStrings.add("CBU");
		listStrings.add("NOTA AUTORIZACION PAGO");
		
		DynamicQuery dlf = DynamicQueryFactoryUtil.forClass(DLFileEntry.class, PortletClassLoaderUtil.getClassLoader());

		DLFolder f;
		try {
			f = DLFolderLocalServiceUtil.getFolder(10136, 0L, "ReclamosPrestacionales");

			long folderId = f.getFolderId();

			Criterion criterion1 = null;
			Criterion criterion2 = null;
			criterion1 = RestrictionsFactoryUtil.eq("folderId", folderId);
			criterion2 = RestrictionsFactoryUtil.eq("folderId", folderId);
			criterion1 = RestrictionsFactoryUtil.and(criterion1,
					RestrictionsFactoryUtil.ilike("title", String.valueOf(idReclamo) + "%"));
			
			criterion2=  RestrictionsFactoryUtil.not(RestrictionsFactoryUtil.and(criterion2,
						RestrictionsFactoryUtil.in("description",listStrings)));

			dlf.add(criterion1);
			dlf.add(criterion2);
			List<Object> results = DLFolderLocalServiceUtil.dynamicQuery(dlf);
			
			cant = results.size();

		} catch (PortalException e) {

		} catch (SystemException e) {

		}

		return cant;

	}

	public int getIdObservacionMedica() {
		return idObservacionMedica;
	}

	public void setIdObservacionMedica(int idObservacionMedica) {
		this.idObservacionMedica = idObservacionMedica;
	}

	public String getDescObservacionMedica() {
		return descObservacionMedica;
	}

	public void setDescObservacionMedica(String descObservacionMedica) {
		this.descObservacionMedica = descObservacionMedica;
	}

	public String getCtaDescrpcion() {
		return ctaDescrpcion;
	}

	public void setCtaDescrpcion(String ctaDescrpcion) {
		this.ctaDescrpcion = ctaDescrpcion;
	}

	public int getCtaNro() {
		return ctaNro;
	}

	public void setCtaNro(int ctaNro) {
		this.ctaNro = ctaNro;
	}

	public int getCtaSucursal() {
		return ctaSucursal;
	}

	public void setCtaSucursal(int ctaSucursal) {
		this.ctaSucursal = ctaSucursal;
	}

	public Afiliado getAfiliadoTitular() {
		return afiliadoTitular;
	}

	public void setAfiliadoTitular(Afiliado afiliadoTitular) {
		this.afiliadoTitular = afiliadoTitular;
	}

	public ReclamoPrestacionalCuenta getCuenta() {
		return cuenta;
	}

	public void setCuenta(ReclamoPrestacionalCuenta cuenta) {
		this.cuenta = cuenta;
	}

	
	public boolean existeCuentaAfi(){
		boolean cuenta = false;
		//titular 
		if (this.getCuenta() !=  null ){
			if("0".equals(this.getCuenta().getCmbTitular()) 
					&& StringUtils.checkNotEmpty(this.getCuenta().getImagenCBU())){
				cuenta = true;
			}
		}
		//apoderado
		if (this.getCuenta() !=  null ){
			if("1".equals(this.getCuenta().getCmbTitular()) 
					&& StringUtils.checkNotEmpty(this.getCuenta().getImagenCBU())
					&& StringUtils.checkNotEmpty(this.getCuenta().getImagenNotaAutorizada())){
				cuenta = true;
			}
		}
		
		if (this.getCuenta() !=  null ){
			if("2".equals(this.getCuenta().getCmbTitular())){
				cuenta = true;
			}
		
		}
		
		
		
		return cuenta;
		
	}

	public int getCodigoIntegracion() {
		return codigoIntegracion;
	}

	public void setCodigoIntegracion(int codigoIntegracion) {
		this.codigoIntegracion = codigoIntegracion;
	}

	public String getMarcaSeccional() {
		return marcaSeccional;
	}

	public void setMarcaSeccional(String marcaSeccional) {
		this.marcaSeccional = marcaSeccional;
	}
	
	public int getIdReintegroApp() {
		return idReintegroApp;
	}

	public void setIdReintegroApp(int idReintegroApp) {
		this.idReintegroApp = idReintegroApp;
	}
	
	public String getUrlComprobante() {
	    return urlComprobante;
	}

	public void setUrlComprobante(String urlComprobante) {
	    this.urlComprobante = urlComprobante;
	}

	public String getUserCbu() {
		return userCbu;
	}

	public void setUserCbu(String userCbu) {
		this.userCbu = userCbu;
	}

	public String getUserCbuConstUrl() {
		return userCbuConstUrl;
	}

	public void setUserCbuConstUrl(String userCbuConstUrl) {
		this.userCbuConstUrl = userCbuConstUrl;
	}

	public String getUrlDocExtra() {
		return urlDocExtra;
	}

	public void setUrlDocExtra(String urlDocExtra) {
		this.urlDocExtra = urlDocExtra;
	}

	public String getReintegroCuil() {
		return reintegroCuil;
	}

	public void setReintegroCuil(String reintegroCuil) {
		this.reintegroCuil = reintegroCuil;
	}

	public int getTitular() {
		return titular;
	}

	public void setTitular(int titular) {
		this.titular = titular;
	}

	public String getNroComprobante() {
		return nroComprobante;
	}

	public void setNroComprobante(String nroComprobante) {
		this.nroComprobante = nroComprobante;
	}

	public String getTipoComprobante() {
		return tipoComprobante;
	}

	public void setTipoComprobante(String tipoComprobante) {
		this.tipoComprobante = tipoComprobante;
	}

	public String getLetraComprobante() {
		return letraComprobante;
	}

	public void setLetraComprobante(String letraComprobante) {
		this.letraComprobante = letraComprobante;
	}

	public int getSucuComprobante() {
		return sucuComprobante;
	}

	public void setSucuComprobante(int sucuComprobante) {
		this.sucuComprobante = sucuComprobante;
	}

	public BigDecimal getImporteComprobante() {
		return importeComprobante;
	}

	public void setImporteComprobante(BigDecimal importeComprobante) {
		this.importeComprobante = importeComprobante;
	}

	public String getCuitPrestador() {
		return cuitPrestador;
	}

	public void setCuitPrestador(String cuitPrestador) {
		this.cuitPrestador = cuitPrestador;
	}

	public String getCuilTitular() {
		return cuilTitular;
	}

	public void setCuilTitular(String cuilTitular) {
		this.cuilTitular = cuilTitular;
	}

	public String getCbuAutorizante() {
		return cbuAutorizante;
	}

	public void setCbuAutorizante(String cbuAutorizante) {
		this.cbuAutorizante = cbuAutorizante;
	}

	public String getCuilTitularCuenta() {
		return cuilTitularCuenta;
	}

	public void setCuilTitularCuenta(String cuilTitularCuenta) {
		this.cuilTitularCuenta = cuilTitularCuenta;
	}

}