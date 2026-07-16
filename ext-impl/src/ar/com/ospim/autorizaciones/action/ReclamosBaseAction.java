package ar.com.ospim.autorizaciones.action;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.portlet.RenderRequest;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;

import ar.com.ospim.afiliados.NoSuchAfiliadoEntryException;
import ar.com.ospim.afiliados.beans.Afiliado;
import ar.com.ospim.afiliados.services.EditarAfiliadoServiceUtil;
import ar.com.ospim.afiliados.services.SeccionalServiceUtil;
import ar.com.ospim.autorizaciones.beans.ReclamoPrestacional;
import ar.com.ospim.autorizaciones.beans.ReclamoPrestacionalCuenta;
import ar.com.ospim.autorizaciones.services.ReclamosPrestacionesServiceUtil;
import ar.com.ospim.autorizaciones.services.WebKeysAutorizaciones;
import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.beans.ContactoElectronico;
import ar.com.ospim.global.beans.Seccional;
import ar.com.ospim.liquidaciones.beans.Prestador;
import ar.com.ospim.liquidaciones.beans.Prestador.TipoPrestador;
import ar.com.ospim.liquidaciones.services.PrestadorServiceUtil;
import ar.com.ospim.util.StringUtils;


public class ReclamosBaseAction  extends PortletAction {

	private Logger _log = Logger.getLogger(this.getClass());
	
	protected Prestador getPrestadorEntry(HttpServletRequest request)
			throws Exception {

		Prestador prestador = null;
		String idString = request.getParameter("prestador_id");
		if (idString == null || idString.trim().equals("")){
			idString = (String)request.getAttribute("prestador_id");
		}
		if (idString != null && !idString.trim().equals("")) {
			int id = Integer.parseInt(idString);
			if (id > 0) {
				prestador = PrestadorServiceUtil.getPrestador(id);
			}
		}
		return prestador;
	}

	public Prestador getOtrosDatosFromRequest(HttpServletRequest req,
			Prestador prestador) {

		return prestador;
	}

	public ReclamoPrestacional  getReclamoPrestacionalFromRequest(HttpServletRequest req, ReclamoPrestacional reclamoprestacional, String cmdAction , String cmd ) {		
		
		String cuilTitular="";
		int inte=0;
		Date fechaOspim;
		Date fechaSeccional;
		Date fechaCierre;
		
		int idAfiliado=0;	
					 
// datos de la clase		
				
		
		SimpleDateFormat formatoDePeriodo = new SimpleDateFormat("dd/MM/yyyy");
		

		String fechaOspimDia = ParamUtil.getString(req,"fechaospimDia");
		String fechaOspimMes = ParamUtil.getString(req,"fechaospimMes");
		String fechaOspimAnio = ParamUtil.getString(req,"fechaospimAnio");
		String fechaSeccionalDia = ParamUtil.getString(req,"fechaseccionalDia");
		String fechaSeccionalMes = ParamUtil.getString(req,"fechaseccionalMes");
		String fechaSeccionalAnio = ParamUtil.getString(req,"fechaseccionalAnio");		
		String fechacierreDia = ParamUtil.getString(req,"fechacierreDia");
		String fechacierreMes = ParamUtil.getString(req,"fechacierreMes") ;
		String fechacierreAnio = ParamUtil.getString(req,"fechacierreAnio");
		
		String justificacionMedica = ParamUtil.getString(req,"justificacionmedcica_reclamo");
		String dictamenComision = ParamUtil.getString(req,"dictamencomision_reclamo");
		
		String sector = ParamUtil.getString(req,"sector");		
		String estado = ParamUtil.getString(req, "estado");	
		
		
		String cuil = ParamUtil.getString(req, "cuil");	
		String intAux = ParamUtil.getString(req, "inte");
		if (!StringUtils.checkEmpty(intAux)){
			inte=   Integer.parseInt(intAux) ;
		}
		String frecuencia= ParamUtil.getString(req, "frecuencia");	 		
		String importe= ParamUtil.getString(req, "importe");		
		String cargoospim = ParamUtil.getString(req, "cargoospim");		
		String cargops= ParamUtil.getString(req, "cargops");
		String cargoimesa= ParamUtil.getString(req, "cargoimesa");
		String responsables = ParamUtil.getString(req, "responsables");	
		String respresolucion= ParamUtil.getString(req, "respresolucion");		
		String presentes= ParamUtil.getString(req, "presentes");		
		String resolucion = ParamUtil.getString(req, "resolucion");
		String observacionReclamo= ParamUtil.getString(req, "observacion_reclamo");		
		String observacionPrestacion= ParamUtil.getString(req, "observacion_prestacion");		
		String observacionRecuperable = ParamUtil.getString(req, "observacion_recuperable");
		
		String caso_vinculado= ParamUtil.getString(req, "caso_vinculado");
		
		
		String reclamo_observacion_cierre = ParamUtil.getString(req, "reclamo_observacion_cierre");
		int tipoGestionCierreReclamo2 =  ParamUtil.getInteger(req, "tipo_gestion_cierre_reclamo");
		int tipoGestionCierreReclamo= ParamUtil.getInteger(req, "tipogestion");
		if (tipoGestionCierreReclamo <= 0 && tipoGestionCierreReclamo2 > 0) {
			tipoGestionCierreReclamo = tipoGestionCierreReclamo2;
		}
		int idObservacionMedica = ParamUtil.getInteger(req, "observacion_medica");	
	    boolean reclamoPsFacturaOspim= ParamUtil.getBoolean(req, "reclamo_ps_factura_ospim");
	    boolean reclamoPorNegociar= ParamUtil.getBoolean(req, "reclamo_a_negociar");
	    boolean debitoPrestador= ParamUtil.getBoolean(req, "debitoprestadora");
	    
	    boolean superIntendencia= ParamUtil.getBoolean(req, "chk_superintendencia");
	    boolean amparo= ParamUtil.getBoolean(req, "chk_amparo");
	    boolean recuperable = ParamUtil.getBoolean(req, "chk_recuperable");
	    boolean enTramite = ParamUtil.getBoolean(req, "chk_entramite"); 
	    Afiliado afi=null;
	    Afiliado afiliadoTitular=null;    
	    boolean incluidoConvenioGerenciadora= ParamUtil.getBoolean(req, "incluido_convenio_gerenciadora");
	    boolean dosporciento= ParamUtil.getBoolean(req, "dosporciento");
	    // datos del diagnostico del afiliado
	    String diagnostico= ParamUtil.getString(req, "diagnostico");
	    String codigoCie10= ParamUtil.getString(req, "codigoCie10");
	    
	    String  evaluacion = ParamUtil.getString(req, "evaluacionreclamo");	    
	    String tipoPedido= ParamUtil.getString(req, "tipopedido");
	    ReclamoPrestacional.ESTADOSEVALUACIONRECLAMO evaluacionReclamo = parseEvaluacionReclamo(evaluacion);
	    String nroDoc= ParamUtil.getString(req, "nroDoc"); 
	    String apellido= ParamUtil.getString(req, "apellido");
	    String nombre= ParamUtil.getString(req, "nombre");
	    Integer nroLote =ParamUtil.getInteger(req, "nroLote");
	    
		fechaOspim= null;
		try {
			fechaOspim= formatoDePeriodo.parse(fechaOspimDia + "/"
					+ (Integer.parseInt(fechaOspimMes) + 1) + "/"
					+ fechaOspimAnio);
		} catch (Exception e) {
			fechaOspim= null;
		}
		
		fechaSeccional= null;
		try {
			fechaSeccional= formatoDePeriodo.parse(fechaSeccionalDia + "/"
					+ (Integer.parseInt(fechaSeccionalMes) + 1) + "/"
					+ fechaSeccionalAnio);
		} catch (Exception e) {
			fechaSeccional= null;
		}
		
		fechaCierre= null;
		try {
			fechaCierre= formatoDePeriodo.parse(fechacierreDia + "/"
					+ (Integer.parseInt(fechacierreMes) + 1) + "/"
					+ fechacierreAnio);
		} catch (Exception e) {
			_log.debug("item: " + e.getMessage() );
		}
		
		try {
			afi= EditarAfiliadoServiceUtil.getAfiliadoEntryInclusoDadoBaja(cuil, inte);
		} catch (NoSuchAfiliadoEntryException e) {
			_log.error(e);
		} catch (SystemException e) {
			_log.error(e);
		}
		
		try {
			afiliadoTitular= EditarAfiliadoServiceUtil.getAfiliadoEntryInclusoDadoBaja(cuil, 0);
		} catch (NoSuchAfiliadoEntryException e) {
			_log.error(e);
		} catch (SystemException e) {
			_log.error(e);
		}
		
	
		if (cmdAction != null && WebKeysAutorizaciones.RECLAMO_PRESTACIONAL_SECCIONAL.equals(cmdAction)){
			fechaOspim = new Date();
		}

		ReclamoPrestacionalCuenta cuenta = new ReclamoPrestacionalCuenta();
		
		String titular = ParamUtil.getString(req, "cmb_titular");
		if("0".equals(titular)){
			cuenta.setIdReclamoPrestacional(ParamUtil.getInteger(req, "id_reclamosel"));
			cuenta.setCbu(ParamUtil.getString(req, "cuenta_cbu")); 
			cuenta.setEmail(ParamUtil.getString(req, "cuenta_email"));
			cuenta.setCuil(ParamUtil.getString(req, "cuil_titular_cuenta"));
			String string = ParamUtil.getString(req, "denominacion");
			String[] parts = string.split(",");
			String part1 = parts[0];  
			String part2 = parts[1]; 
			cuenta.setApellido(part1.trim());
			cuenta.setNombre(part2.trim());
			cuenta.setCmbTitular(titular);
			cuenta.setCuilGrupoFamiliar(ParamUtil.getString(req, "cuil_grupo_familar"));
			String cbu =  ParamUtil.getString(req, "file_cbu");
			if (cbu != null & !cbu.equals("0")){
				cuenta.setImagenCBU(cbu);
			}
			
			
		}else if ("1".equals(titular)){
			cuenta.setIdReclamoPrestacional(ParamUtil.getInteger(req, "id_reclamosel"));
			cuenta.setCbu(ParamUtil.getString(req, "cuenta_cbu_autorizado")); 
			cuenta.setEmail(ParamUtil.getString(req, "cuenta_email_autorizado"));
			cuenta.setCuil(ParamUtil.getString(req, "cuil_autorizado"));
			cuenta.setApellido(ParamUtil.getString(req, "apellido_autorizado"));
			cuenta.setNombre(ParamUtil.getString(req, "nombre_autorizado"));
			cuenta.setCuilGrupoFamiliar(ParamUtil.getString(req, "cuil_grupo_familar"));
			cuenta.setCmbTitular(titular);
			String cbu =  ParamUtil.getString(req, "file_cbu");
			String notaAutorizada = ParamUtil.getString(req, "file_nota_autorizada");
			if (cbu != null & !cbu.equals("0")){
				cuenta.setImagenCBU(cbu);
				cuenta.setImagenNotaAutorizada(notaAutorizada);
			}
		}else if ("2".equals(titular)){
			
			String idSeccional = ParamUtil.getString(req, "id_seccional");
			String cuit = WebKeysGlobal.CUIT_UOMA;
			String email = null;
			
			Seccional seccional = null;
			
			
			List<ContactoElectronico> contactose; 		
			contactose=SeccionalServiceUtil.buscarContactosSeccionalEmail(Integer.parseInt(idSeccional));
		  	
			for (ContactoElectronico contactoElectronico : contactose) {
				email = contactoElectronico.getContacto();
			}
			
			
			try{
				seccional = SeccionalServiceUtil.buscarSeccionalById(Integer.parseInt(idSeccional));
			}catch (Exception e) {
				seccional = null;
				_log.debug("Error al traer Seccioanl");
			}
			
			cuenta.setIdReclamoPrestacional(ParamUtil.getInteger(req, "id_reclamosel"));
			cuenta.setCmbTitular(titular);
			cuenta.setEmail(email);
			cuenta.setCuil(cuit);
			cuenta.setCuilGrupoFamiliar(cuit);
			cuenta.setCbu(seccional.getCBU());
			cuenta.setApellido("Seccional");
			cuenta.setNombre(seccional.getDescripcion());
			
		}

		
		int codIntegracion =  ParamUtil.getInteger(req, "integracion");

		
		try {	
			if(cmd.equals(WebKeysAutorizaciones.CUENTA)){
				reclamoprestacional = new ReclamoPrestacional();
				reclamoprestacional.setCuenta(cuenta);
			}else{				
			reclamoprestacional = new ReclamoPrestacional(cuil, inte, fechaOspim, sector, fechaSeccional, Integer.parseInt(estado), 
					fechaCierre, reclamo_observacion_cierre, tipoGestionCierreReclamo, reclamoPsFacturaOspim, reclamoPorNegociar, 
					superIntendencia, amparo, recuperable, enTramite, incluidoConvenioGerenciadora, Integer.parseInt(caso_vinculado), 
					dosporciento,dictamenComision,  justificacionMedica, diagnostico, codigoCie10, tipoPedido, debitoPrestador, 
					evaluacionReclamo, afi, idObservacionMedica,codIntegracion);

			
			}
			
		} catch (Exception e) {			
			_log.error("No se pudo reconstruir el Reclamo Prestacional desde la solicitud.", e);
			throw new IllegalArgumentException("Los datos del Reclamo Prestacional son inválidos.", e);
		}
		
		if (reclamoprestacional == null) {
			throw new IllegalStateException("La reconstrucción del Reclamo Prestacional no produjo un objeto válido.");
		}
		reclamoprestacional.setNroLote(nroLote);
		int idReclamo = ParamUtil.getInteger(req, "id_reclamosel");
		reclamoprestacional.setId(idReclamo);
		reclamoprestacional.setAfiliadoTitular(afiliadoTitular);
		
		if(!cmd.equals(WebKeysAutorizaciones.CUENTA) && "REINTEGRO".equalsIgnoreCase(reclamoprestacional.getTipoPedido())){			
			try {
				ReclamoPrestacional reclamoPrestacionalAux = ReclamosPrestacionesServiceUtil.getReclamoPrestacional(idReclamo);
				
				if (reclamoPrestacionalAux != null && reclamoPrestacionalAux.getCuenta()!= null){
					reclamoprestacional.setCuenta(reclamoPrestacionalAux.getCuenta());
				}
				
			} catch (Exception e) {
				_log.debug(e.getMessage());
			}	
		}

		
		return reclamoprestacional;
	}
	

public ReclamoPrestacional  getReclamoPrestacionalFromRequest(RenderRequest  req, ReclamoPrestacional reclamoPrestacional ) {		
		
		String cuilTitular="";
		int inte=0;
		Date fechaOspim;
		Date fechaSeccional;
		Date fechaCierre;
		
		int id_afiliado=0;	
					 
// datos de la clase		
				
		SimpleDateFormat formatoDePeriodo = new SimpleDateFormat("dd/MM/yyyy");
		
		String fechaOspimDia = ParamUtil.getString(req,"fechaospimDia");
		String fechaOspimMes = ParamUtil.getString(req,"fechaospimMes");
		String fechaOspimAnio = ParamUtil.getString(req,"fechaospimAnio");
		String fechaSeccionalDia = ParamUtil.getString(req,"fechaseccionalDia");
		String fechaSeccionalMes = ParamUtil.getString(req,"fechaseccionalMes");
		String fechaSeccionalAnio = ParamUtil.getString(req,"fechaseccionalAnio");		
		String fechaCierreDia = ParamUtil.getString(req,"fechacierreDia");
		String fechaCierreMes = ParamUtil.getString(req,"fechacierreMes") ;
		String fechaCierreAnio = ParamUtil.getString(req,"fechacierreAnio");
		
		String justificacionMedica = ParamUtil.getString(req,"justificacionmedcica_reclamo");
		String dictamenComision = ParamUtil.getString(req,"dictamencomision_reclamo");
		
		
		
		String sector = ParamUtil.getString(req,"sector");		
		String estado = ParamUtil.getString(req, "estado");		
		String cuil = ParamUtil.getString(req, "cuil");
		inte=   Integer.parseInt(ParamUtil.getString(req, "inte")) ;
		String frecuencia= ParamUtil.getString(req, "frecuencia");	 		
		String importe= ParamUtil.getString(req, "importe");		
		String cargoospim = ParamUtil.getString(req, "cargoospim");		
		String cargops= ParamUtil.getString(req, "cargops");	
		
		String cargoimesa=""; 
		try {
			cargoimesa=ParamUtil.getString(req, "cargoimesa");
		}catch(Exception e) {
			cargoimesa="";
		}
		String responsables = ParamUtil.getString(req, "responsables");	
		String respresolucion= ParamUtil.getString(req, "respresolucion");		
		String presentes= ParamUtil.getString(req, "presentes");		
		String resolucion = ParamUtil.getString(req, "resolucion");
		String observacionReclamo= ParamUtil.getString(req, "observacion_reclamo");		
		String observacionPrestacion= ParamUtil.getString(req, "observacion_prestacion");		
		String observacionRecuperable = ParamUtil.getString(req, "observacion_recuperable");
		String casoVinculado= ParamUtil.getString(req, "caso_vinculado");
		
		String reclamoObservacionCierre = ParamUtil.getString(req, "reclamo_observacion_cierre");
		int tipoGestionCierreReclamo= ParamUtil.getInteger(req, "tipogestion");
		int tipoGestionVisible = ParamUtil.getInteger(req, "tipo_gestion_cierre_reclamo");
		if (tipoGestionCierreReclamo <= 0 && tipoGestionVisible > 0) {
			tipoGestionCierreReclamo = tipoGestionVisible;
		}
		int idObservacionMedica = ParamUtil.getInteger(req, "observacion_medica");		
	    boolean reclamoPsFacturaOspim= ParamUtil.getBoolean(req, "reclamo_ps_factura_ospim");
	    boolean reclamoPorNegociar= ParamUtil.getBoolean(req, "reclamo_a_negociar");
	    boolean debitoPrestador= ParamUtil.getBoolean(req, "debitoprestadora");
	    
	    boolean recuperable= ParamUtil.getBoolean(req, "chk_recuperable");
	    boolean superIntendencia= ParamUtil.getBoolean(req, "chk_superintendencia");
	    boolean amparo= ParamUtil.getBoolean(req, "chk_amparo");
	    boolean entramite= ParamUtil.getBoolean(req, "chk_entramite");
	    
	    boolean convenioGerenciadora= ParamUtil.getBoolean(req, "incluido_convenio_gerenciadora");
	    boolean dosporciento= ParamUtil.getBoolean(req, "dosporciento");
		
	    // datos del diagnostico del afiliado
	    String diagnostico= ParamUtil.getString(req, "diagnostico");
	    String codigoCie10= ParamUtil.getString(req, "codigoCie10");
	    String tipoPedido= ParamUtil.getString(req, "tipopedido");
	    String  evaluacion = ParamUtil.getString(req, "evaluacionreclamo");
	    Afiliado afi=null;
	   
	    
	    ReclamoPrestacional.ESTADOSEVALUACIONRECLAMO evaluacionReclamo = parseEvaluacionReclamo(evaluacion);
		
	    fechaOspim= null;
		
	    try {
			fechaOspim= formatoDePeriodo.parse(fechaOspimDia + "/"
					+ (Integer.parseInt(fechaOspimMes) + 1) + "/"
					+ fechaOspimAnio);
		} catch (Exception e) {
			fechaOspim= null;
		}
		
		fechaSeccional= null;
		try {
			fechaSeccional= formatoDePeriodo.parse(fechaSeccionalDia + "/"
					+ (Integer.parseInt(fechaSeccionalMes) + 1) + "/"
					+ fechaSeccionalAnio);
		} catch (Exception e) {
			fechaSeccional= null;
		}
		
		fechaCierre= null;
		try {
			fechaCierre= formatoDePeriodo.parse(fechaCierreDia + "/"
					+ (Integer.parseInt(fechaCierreMes) + 1) + "/"
					+ fechaCierreAnio);
		} catch (Exception e) {
			fechaCierre= null;
		}			    
		try {
			afi= EditarAfiliadoServiceUtil.getAfiliadoEntryInclusoDadoBaja(cuil, inte);
		} catch (NoSuchAfiliadoEntryException e) {
			_log.error(e);
		} catch (SystemException e) {
			_log.error(e);
		}	
		
		int codIntegracion =  ParamUtil.getInteger(req, "integracion");

		ReclamoPrestacionalCuenta cuenta = new ReclamoPrestacionalCuenta();
		
		try {
			reclamoPrestacional = new ReclamoPrestacional(cuil,inte, fechaOspim, sector, fechaSeccional, Integer.parseInt(estado), 
					fechaCierre, reclamoObservacionCierre, tipoGestionCierreReclamo, reclamoPsFacturaOspim, reclamoPorNegociar, 
					superIntendencia, amparo ,recuperable, entramite, convenioGerenciadora, Integer.valueOf(casoVinculado), dosporciento, 
					dictamenComision, justificacionMedica, diagnostico, codigoCie10, tipoPedido, debitoPrestador, evaluacionReclamo, afi, 
					idObservacionMedica, codIntegracion);
		} catch (Exception e) {
			_log.error("No se pudo reconstruir el Reclamo Prestacional desde RenderRequest.", e);
			throw new IllegalArgumentException("Los datos del Reclamo Prestacional son inválidos.", e);
		}
		
		if (reclamoPrestacional == null) {
			throw new IllegalStateException("La reconstrucción del Reclamo Prestacional no produjo un objeto válido.");
		}
		return reclamoPrestacional;
	}



	public Prestador getPrestadorFromRequest(HttpServletRequest req, Prestador prestador) {
		
		String cuit = ParamUtil.getString(req, "cuit");
		String desc = ParamUtil.getString(req, "desc");
		String ciaSeguro = ParamUtil.getString(req, "compania_seguro");
		boolean seguroCobertura = ParamUtil.getBoolean(req, "seguro_cobertura");
		boolean certificacionProfesional = ParamUtil.getBoolean(req, "certificacion"); 
		String otorgaCertificacion = ParamUtil.getString(req, "otorga_cert");

		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		SimpleDateFormat formatoDePeriodo = new SimpleDateFormat("dd/MM/yyyy");

		String seguroFechaVtoDia = ParamUtil.getString(req,"seguroFechaVtoDia");
		String seguroFechaVtoMes = ParamUtil.getString(req,"seguroFechaVtoMes");
		String seguroFechaVtoAnio = ParamUtil.getString(req,"seguroFechaVtoAnio");
		Date fechaVtoSeguro = null;
		try {
			fechaVtoSeguro = formatoDePeriodo.parse(seguroFechaVtoDia + "/"
					+ (Integer.parseInt(seguroFechaVtoMes) + 1) + "/"
					+ seguroFechaVtoAnio);
		} catch (Exception e) {
			fechaVtoSeguro = null;
		}
		
		String certificacionFechaVtoDia = ParamUtil.getString(req,"certificacionFechaVtoDia");
		String certificacionFechaVtoMes = ParamUtil.getString(req,"certificacionFechaVtoMes");
		String certificacionFechaVtoAnio = ParamUtil.getString(req,"certificacionFechaVtoAnio");
		Date fechaVtoCertificacion = null;
		try {
			fechaVtoCertificacion = formatoDePeriodo.parse(certificacionFechaVtoDia + "/"
					+ (Integer.parseInt(certificacionFechaVtoMes) + 1) + "/"
					+ certificacionFechaVtoAnio);
		} catch (Exception e) {
			fechaVtoCertificacion = null;
		}
		String contacto = ParamUtil.getString(req, "contacto");
		String obs = ParamUtil.getString(req, "observaciones");
		int idPrestador = ParamUtil.getInteger(req, "id_prestador");
		String codigoHospital = ParamUtil.getString(req, "codigo_hospital");
		int idTipoPrest = ParamUtil.getInteger(req, "tipo_prestador");
		TipoPrestador tipoPrestador = new TipoPrestador(idTipoPrest, "");
		
		prestador = new Prestador(idPrestador, cuit, tipoPrestador, contacto.toUpperCase(), obs, 
				desc.toUpperCase(), codigoHospital, ciaSeguro.toUpperCase(), seguroCobertura, certificacionProfesional, 
				otorgaCertificacion.toUpperCase(), fechaVtoSeguro, fechaVtoCertificacion);

		
		return prestador;
	}

	private ReclamoPrestacional.ESTADOSEVALUACIONRECLAMO parseEvaluacionReclamo(String evaluacion) {
		if (StringUtils.checkEmpty(evaluacion)) {
			return ReclamoPrestacional.ESTADOSEVALUACIONRECLAMO.SINEVALUACION;
		}

		String normalizada = evaluacion.trim().toUpperCase();
		if ("AUTORIZADO".equals(normalizada) || "AUTORIZADA".equals(normalizada)) {
			return ReclamoPrestacional.ESTADOSEVALUACIONRECLAMO.AUTORIZADA;
		}
		if ("RECHAZADO".equals(normalizada) || "RECHAZADA".equals(normalizada)) {
			return ReclamoPrestacional.ESTADOSEVALUACIONRECLAMO.RECHAZADA;
		}
		if ("SINVALOR".equals(normalizada)) {
			return ReclamoPrestacional.ESTADOSEVALUACIONRECLAMO.SINVALOR;
		}
		if ("SINEVALUACION".equals(normalizada) || "SIN_EVALUACION".equals(normalizada)) {
			return ReclamoPrestacional.ESTADOSEVALUACIONRECLAMO.SINEVALUACION;
		}

		throw new IllegalArgumentException("Valor de evaluación de reclamo inválido: " + evaluacion);
	}

}
