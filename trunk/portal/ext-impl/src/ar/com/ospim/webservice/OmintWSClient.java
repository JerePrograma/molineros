package ar.com.ospim.webservice;

import java.rmi.RemoteException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.apache.axis.message.MessageElement;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import ar.com.ospim.afiliados.beans.Afiliado;
import ar.com.ospim.afiliados.reportes.NovedadesProcesadasOmintWSExcel;
import ar.com.ospim.automatico.AgendadoJava;
import ar.com.ospim.automatico.beans.MensajeEnvioyRespuestaWSOmint;
import ar.com.ospim.automatico.beans.ReporteAutomatico;
import ar.com.ospim.automatico.service.AgendaReporteUtil;
import ar.com.ospim.global.beans.Domicilio;
import ar.com.ospim.webservice.omint.AltaBeneficiarioResponseAltaBeneficiarioResult;
import ar.com.ospim.webservice.omint.AltaGrupoFamiliarResponseAltaGrupoFamiliarResult;
import ar.com.ospim.webservice.omint.AltaGrupoFamiliarTransactionData;
import ar.com.ospim.webservice.omint.BajaBeneficiarioResponseBajaBeneficiarioResult;
import ar.com.ospim.webservice.omint.BajaGrupoFamiliarResponseBajaGrupoFamiliarResult;
import ar.com.ospim.webservice.omint.Beneficiario;
import ar.com.ospim.webservice.omint.CambioPlanGrupoFamiliarResponseCambioPlanGrupoFamiliarResult;
import ar.com.ospim.webservice.omint.GetSessionResponseGetSessionResult;
import ar.com.ospim.webservice.omint.ModificacionBeneficiarioResponseModificacionBeneficiarioResult;
import ar.com.ospim.webservice.omint.SociosSoapProxy;
import ar.com.ospim.webservice.service.AfiliadoOpe;
import ar.com.ospim.webservice.service.AfiliadoServiceUtil;
import ar.com.ospim.webservice.xmlparser.OmintResponseXMLParser;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

public class OmintWSClient extends AgendadoJava {
	
	private final Integer company = 2;
	private final String country = "AR";
	private final String language = "ES";
	private Calendar calendarVigencia = Calendar.getInstance();
	private SociosSoapProxy ssp ;//= new SociosSoapProxy();
//	private java.lang.String SociosSoap_address = "http://200.47.31.26/wstransfer/socios.asmx"; // Testing
//	private java.lang.String SociosSoap_address = "http://ws.omint.com.ar/wstransfer/socios.asmx"; // Produccion
	private java.lang.String SociosSoap_address = "http://200.47.31.28/wstransfer/socios.asmx"; // Produccion
	private String namespace_uri = "http://tempuri.org/";  // Testing
//	private String namespace_uri = "http://tempuri.org/"; // Produccion
	private String namespace_schema = "http://www.w3.org/2001/XMLSchema";
	
	private static Log logger = LogFactoryUtil.getLog(OmintWSClient.class);
	private OmintResponseXMLParser parser;
	private Map<String,String> provinciaLetra = null;
	private String session = null;
	private Date sessionExpire = null;
	private int sessionExpireMin = 29; // a los 30 lo controlan del lado de Omint...

	private AfiliadoServiceUtil service = new AfiliadoServiceUtil();
	
	public OmintWSClient(){
		super();
		
		parser = new OmintResponseXMLParser();
		
		provinciaLetra = this.setearConversionProvincia();
		
//		SociosLocator locator = new SociosLocator();
//		locator.setSociosSoapEndpointAddress(SociosSoap_address);
		this.ssp = new SociosSoapProxy(SociosSoap_address);
//		this.ssp = new SociosSoapProxy(SociosSoap_address, namespace_uri);
		
		logger.info("Instanciando OmintWSClient");
	}
	
	public void procesarNovedades(){
		
//		Parametros: 
//			 * 				0 el SP busca Afiliados p Alta GrupoFamiliares,
//			 * 				1 el SP busca Afiliados p Alta Beneficiarios, 
//			 * 				2 el SP busca Afiliados p Modificar Beneficiarios,
//			 * 				5 el SP busca Afiliados p Modificar plan para grupo familiar
//			 * 	  			3 el SP busca Afiliados p Baja GrupoFamiliares,
//			 * 				4 el SP busca Afiliados p Baja Beneficiarios,
		
		List<AfiliadoOpe> afiliados = null;
		Afiliado afiliado = null;
		List<Afiliado> altaGrupoFliar = new ArrayList<Afiliado>();
		List<Afiliado> altaBeneficiario = new ArrayList<Afiliado>();
		List<Afiliado> bajaGrupoFliar = new ArrayList<Afiliado>();
		List<Afiliado> bajaBeneficiario = new ArrayList<Afiliado>();
		List<Afiliado> modificaBeneficiario = new ArrayList<Afiliado>();
		List<Afiliado> cambioPlanGrupoFliar = new ArrayList<Afiliado>();
		
		logger.info("Procesando novedades para mandar a Omint via WS");
	
		afiliados = service.getTodasNovedadesPadron();
		
		logger.debug("Cantidad de novedades: " + afiliados.size());
		
		for (Iterator<AfiliadoOpe> iterator = afiliados.iterator(); iterator.hasNext();) {
			AfiliadoOpe afiOpe = (AfiliadoOpe) iterator.next();
			
			if(afiOpe.getOperacion() == 0 ){ //Alta GrupoFamiliares,
				afiliado = afiOpe ;
				altaGrupoFliar.add(afiliado);
			}
			if(afiOpe.getOperacion() == 1 ){ // Alta Beneficiarios, 
				afiliado = afiOpe ;
				altaBeneficiario.add(afiliado);
			}
			if(afiOpe.getOperacion() == 2 ){ // Modificar Beneficiarios,
				afiliado = afiOpe ;
				modificaBeneficiario.add(afiliado);
			}
			if(afiOpe.getOperacion() == 3 ){ // Baja GrupoFamiliares,
				afiliado = afiOpe ;
				bajaGrupoFliar.add(afiliado);
			}
			if(afiOpe.getOperacion() == 4 ){ // Baja Beneficiarios,
				afiliado = afiOpe ;
				bajaBeneficiario.add(afiliado);
			}
			if(afiOpe.getOperacion() == 5 ){ // Modificar plan para grupo familiar
				afiliado = afiOpe ;
				cambioPlanGrupoFliar.add(afiliado);
			}
			
		}
//		El orden del procesamiento se definio c Sandra Querin, Marcelo Cerfoglio, Federico Brachi y yo, segun especificacion recibida de Omint 
		
//		#1
		if(bajaGrupoFliar.size() > 0){
			this.enviarBajaGrupoFamiliar(bajaGrupoFliar);
		}
		
		
//		#2
		if(bajaBeneficiario.size() > 0){
			for (Iterator<Afiliado> iterator = bajaBeneficiario.iterator(); iterator.hasNext();) {
				Afiliado afiliado1 = iterator.next();
				this.enviarBajaBeneficiario(afiliado1);
			}
		}
		
//		#3
		if(altaGrupoFliar.size() > 0){
			this.enviarAltaGrupoFamiliar(altaGrupoFliar);
		}
		
//		#4
		if(altaBeneficiario.size() > 0){
			for (Iterator<Afiliado> iterator = altaBeneficiario.iterator(); iterator.hasNext();) {
				Afiliado afiliado1 = iterator.next();
				this.enviarAltaBeneficiario(afiliado1);
			}
		}

//		#5
		if(cambioPlanGrupoFliar.size() > 0){
			this.enviarCambioPlanGrupoFliar(cambioPlanGrupoFliar);
		}
		
//		#6
		if(modificaBeneficiario.size() > 0){
			for (Iterator<Afiliado> iterator = modificaBeneficiario.iterator(); iterator.hasNext();) {
				Afiliado afiliado1 = iterator.next();
				this.enviarModificacionBeneficiario(afiliado1);
			}
		}

		logger.info("Fin procesando novedades para mandar a Omint via WS");
		
	}

	public String getSession(){
		
//		this.session = "";
//		Para no consumir una sesion x cada invocacion de novedades
		if(this.session!=null && !esSessionExpirada() ){
			return this.session;
		}
		try {					
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			sdf.setTimeZone(TimeZone.getTimeZone("GMT-3") );
//			sdf.setTimeZone(TimeZone.getTimeZone("UTC") );
			GetSessionResponseGetSessionResult response = ssp.getSession(String.valueOf(company), country, language);
			
			MessageElement[] msg = response.get_any();
			logger.debug(" getSession()" + msg[0].getAsString());
			
			
			
			this.session = response.getSessionId();
//			String starTime = response.getSessionStartTime();
//			String endTime = response.getSessionEndTime();
			String endTime = response.getSessionEndTime().substring(0, 10) + " " + response.getSessionEndTime().substring(11, 19); 
			logger.debug("Omint SessionEndTime " + endTime);
//			NOTA: asi viene yyyy-MM-dd'T'HH:mm:ssz
//						    20110525T14:00:02-03:00
			this.sessionExpire = sdf.parse(endTime); // queda en GMT
//			this.sessionExpire = new Date(); // yo seteo la hora min que obtuve la session
			
			if(this.session != null && this.session.length() > 1){
				logger.debug("Session: " + this.session);
				logger.debug("Session expire: " + this.sessionExpire);
				return this.session;
			}else{
				return null;
			}
			
		} catch (RemoteException e) {
			logger.error(e);
			return null;
		} catch (ParseException e) {
			logger.error(e);
			return null;
		} catch (Exception e) {
			logger.error(e);
			return null;
		}
	}
	
	private boolean esSessionExpirada(){
		
		boolean expiro = true;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		sdf.setTimeZone(TimeZone.getTimeZone("UTC") );
//		sdf.setTimeZone(TimeZone.getTimeZone("GMT-3") );
		
		Calendar ahora = Calendar.getInstance();
		Calendar expire = Calendar.getInstance();
		expire.setTime(sessionExpire);
		
		logger.debug("Ahora: " + sdf.format(ahora.getTime()));
		logger.debug("expire time: " + sdf.format(expire.getTime()));

		long milis1 = ahora.getTimeInMillis();
        long milis2 = expire.getTimeInMillis();

        // calcular la diferencia en milisengundos
        long diff = milis1 - milis2;
        // calcular la diferencia en minutos
        long diffMinutes = diff / (60 * 1000);
        
//		if(ahora.before(sessionExpire)){
        if(diffMinutes < this.sessionExpireMin){
			expiro = false;
		}
		
		return expiro;
	}
	
	public void enviarAltaBeneficiario(Afiliado afiliado){

		this.getSession();

		AltaBeneficiarioResponseAltaBeneficiarioResult responseResult;
		MessageElement[] msg; 
		Beneficiario ben ;
		String[] respuesta = null;
		
		logger.info("Enviando alta de beneficiario");
//		logger.info("Novedades: " + altas.size());
//		
//		for (Iterator<Afiliado> iterator = altas.iterator(); iterator.hasNext();) {
//			Afiliado afiliado = iterator.next();
			
			ben = this.completarBeneficiario(afiliado, calendarVigencia);
			
			try {
				logger.debug("Invocando WS");
				logger.debug("Afiliado: " + ben.getCUIL() + " / " +ben.getNroIntegrante());
				responseResult = ssp.altaBeneficiario(session, ben.getCUILTitular(), ben.getFecVig(), ben.getApellido(), ben.getNombre(), ben.getParentesco(),
						ben.getSexo(), ben.getFecNac(), ben.getCalle(), ben.getNroCalle(), ben.getResto(), ben.getLocalidad(), ben.getCP(), ben.getProvincia(),
						ben.getTelefono(), ben.getTipoDoc(), ben.getNroDoc(), ben.getSeccional() , ben.getCategoria(), ben.getCUIL(), ben.getFPP(), ben.getNroIntegrante(), 
						ben.getNacionalidad(), ben.getEstadoCivil(), ben.getDiscapacidad());

				if(responseResult != null){
					msg = responseResult.get_any();
					if(msg[0] !=null && msg[0].getAsString() != null){
						logger.debug("Recibiendo respuesta del WS");
						
						respuesta = this.parser.parsearResponseXML( msg[0].getAsString() );

					}
				}
				
				if(respuesta != null){
					service.updateNovedadesResponse(1, ben.getCUILTitular(), ben.getInte(), Integer.parseInt(respuesta[0]), respuesta[1], respuesta[2]);
				}else{
					logger.error("La respuesta parseada no se pudo leer correctamente");
				}
				
				
			} catch (RemoteException e) {
				logger.error("Error al procesar enviarAltaBeneficiarios");
				logger.error(e);
			} catch (Exception e){
				logger.error("Error al procesar respuesta enviarAltaBeneficiarios");
				logger.error(e);
			}

//		}
	}
	
//	public String[] enviarAltaGrupoFamiliar(List<Afiliado> afiliados){
	public void enviarAltaGrupoFamiliar(List<Afiliado> afiliados){

		this.getSession();

		AltaGrupoFamiliarTransactionData transactionData;
		AltaGrupoFamiliarResponseAltaGrupoFamiliarResult responseResult;
		MessageElement[] msg; 
		List<Beneficiario> grupoFliar ;
		Beneficiario ben = null ;
		String planMedico = null;
		Date fechaVig = null;
		String[] respuesta = null;
		
		logger.info("Enviando alta de grupo familiar");
		logger.info("Novedades: " + afiliados.size());
//		
//		for (Iterator<Afiliado> iterator = altas.iterator(); iterator.hasNext();) {
//			Afiliado afiliado = iterator.next();
		int i = 0;
		Afiliado a =null;
		String cuil_titu_ant = "0";
		if(afiliados != null && afiliados.size() > 0){
			cuil_titu_ant = afiliados.get(0).getCuil_titular();
//		}else{
//			return null;
		}
		
		a = afiliados.get(i);
		
//		el stored procedure debe venir ordenado por cuil_titular e inte
		while(i < afiliados.size()){	
			
			cuil_titu_ant = a.getCuil_titular();
			
			grupoFliar = new ArrayList<Beneficiario>();

			while(i < afiliados.size() && cuil_titu_ant.equals(a.getCuil_titular())){	
				
				if(a.getInte() == 0 ){ // Si es el titular tomo su plan medico y su fecha de vigencia para todo el grupo fliar.
					planMedico = a.getNombrePlan();
					fechaVig = a.getIngre_fecha() ;
				}	
					
				ben = this.completarBeneficiario(a, calendarVigencia);
				grupoFliar.add(ben);
				
				i++;
				if(i < afiliados.size()){
					a = afiliados.get(i);
				}
			}	
			try {
				logger.debug("Invocando WS");
				logger.debug("Afiliado: " + ben.getCUIL() + " / " +ben.getNroIntegrante());
				
				transactionData = new AltaGrupoFamiliarTransactionData(); 
				
				transactionData.set_any( this.parser.generarXMLGrupoFamiliar(grupoFliar, String.valueOf(company), fechaVig, planMedico)  );
				
				responseResult = ssp.altaGrupoFamiliar(session, transactionData);

				if(responseResult != null){
					msg = responseResult.get_any();
					if(msg[0] !=null && msg[0].getAsString() != null){
						logger.debug("Recibiendo respuesta del WS");
						
						respuesta = this.parser.parsearResponseXML( msg[0].getAsString() );
					}
				}
				
				if(respuesta != null){
					for (Beneficiario b : grupoFliar) {
						
						service.updateNovedadesResponse(0, b.getCUILTitular(), b.getInte(), Integer.parseInt(respuesta[0]), respuesta[1], respuesta[2]);
						
					}
				}else{
					logger.error("La respuesta parseada no se pudo leer correctamente");
				}
				
				
			} catch (RemoteException e) {
				logger.error("Error al procesar enviarAltaGrupoFamiliar");
				logger.error(e);
			} catch (Exception e){
				logger.error("Error al procesar respuesta enviarAltaGrupoFamiliar");
				logger.error(e);
			}
			
			
		}
//		return respuesta;
	}
	
	public void enviarModificacionBeneficiario(Afiliado afiliado){

		this.getSession();

		ModificacionBeneficiarioResponseModificacionBeneficiarioResult responseResult;
		MessageElement[] msg; 
		Beneficiario ben ;
		String[] respuesta = null;
		
		logger.info("Enviando modificacion de beneficiario");
//		logger.info("Novedades: " + altas.size());
//		
//		for (Iterator<Afiliado> iterator = altas.iterator(); iterator.hasNext();) {
//			Afiliado afiliado = iterator.next();
			
			ben = this.completarBeneficiario(afiliado, calendarVigencia);
			
			try {
				logger.debug("Invocando WS");
				logger.debug("Afiliado: " + ben.getCUIL() + " / " +ben.getNroIntegrante());
				
				responseResult = ssp.modificacionBeneficiario(session, company, ben.getCUILTitular(), ben.getFecVig(), ben.getApellido(), ben.getNombre(), ben.getParentesco(),
					ben.getSexo(), ben.getFecNac(), ben.getCalle(), ben.getNroCalle(), ben.getResto(), ben.getLocalidad(), ben.getCP(), ben.getProvincia(),
					ben.getTelefono(), ben.getTipoDoc(), ben.getNroDoc(), ben.getSeccional() , ben.getCategoria(), ben.getCUIL(), ben.getFPP(), ben.getNacionalidad(),
					ben.getEstadoCivil(), ben.getDiscapacidad());
				
				if(responseResult != null){
					msg = responseResult.get_any();
					if(msg[0] !=null && msg[0].getAsString() != null){
						logger.debug("Recibiendo respuesta del WS");
						
						respuesta = this.parser.parsearResponseXML( msg[0].getAsString() );
						
					}
				}
				
				if(respuesta != null){
					service.updateNovedadesResponse(2, ben.getCUILTitular(), ben.getInte(), Integer.parseInt(respuesta[0]), respuesta[1], respuesta[2]);

				}else{
					logger.error("La respuesta parseada no se pudo leer correctamente");
				}
				
		
			} catch (RemoteException e) {
				logger.error("Error al procesar enviarModificacionBeneficiario");
				logger.error(e);
			} catch (Exception e){
				logger.error("Error al procesar respuesta enviarModificacionBeneficiario");
				logger.error(e);
			}

//		}
	}
	
	public void enviarBajaBeneficiario(Afiliado afiliado){

		this.getSession();

		BajaBeneficiarioResponseBajaBeneficiarioResult responseResult;
		MessageElement[] msg; 
		Beneficiario ben ;
		String[] respuesta = null;
		
		logger.info("Enviando baja de beneficiario");
//		logger.info("Novedades: " + altas.size());
//		
//		for (Iterator<Afiliado> iterator = altas.iterator(); iterator.hasNext();) {
//			Afiliado afiliado = iterator.next();
			
			ben = this.completarBeneficiario(afiliado, calendarVigencia);
			
			try {
				logger.debug("Invocando WS");
				logger.debug("Afiliado: " + ben.getCUIL() + " / " +ben.getNroIntegrante());
				if(ben.getFecBaja() != null){
					responseResult = ssp.bajaBeneficiario(session, company, ben.getCUILTitular(), ben.getFecBaja() , ben.getCUIL());
				}else{
//					TODO Ver que venga la fecha de baja del afiliado desde el SP
					responseResult = ssp.bajaBeneficiario(session, company, ben.getCUILTitular(), Calendar.getInstance(), ben.getCUIL());
				}

				if(responseResult != null){
					msg = responseResult.get_any();
					if(msg[0] !=null && msg[0].getAsString() != null){
						logger.debug("Recibiendo respuesta del WS");
						
						respuesta = this.parser.parsearResponseXML( msg[0].getAsString() );
						
					}
				}
				
				if(respuesta != null){
					service.updateNovedadesResponse(4, ben.getCUILTitular(), ben.getInte(), Integer.parseInt(respuesta[0]), respuesta[1], respuesta[2]);

				}else{
					logger.error("La respuesta parseada no se pudo leer correctamente");
				}
				
				
			} catch (RemoteException e) {
				logger.error("Error al procesar enviarBajaBeneficiarios");
				logger.error(e);
			} catch (Exception e){
				logger.error("Error al procesar respuesta enviarBajaBeneficiarios");
				logger.error(e);
			}

//		}
	}
	
	public void enviarBajaGrupoFamiliar(List<Afiliado> afiliados){
		
		this.getSession();

		BajaGrupoFamiliarResponseBajaGrupoFamiliarResult responseResult;
		MessageElement[] msg; 
//		Beneficiario ben = null ;
		String[] respuesta = null;
		
		logger.info("Enviando baja de grupo familiar");
		logger.info("Novedades: " + afiliados.size());
//		
		for (Iterator<Afiliado> iterator = afiliados.iterator(); iterator.hasNext();) {
			Afiliado afiliado = iterator.next();
			
			try {
				logger.debug("Invocando WS");
				logger.debug("Afiliado: " + afiliado.getCuil() + " / " +afiliado.getInte());
				
				Calendar fecha_baja = Calendar.getInstance();
				fecha_baja.set(Calendar.HOUR_OF_DAY, 0);
				fecha_baja.set(Calendar.MINUTE, 0);
				fecha_baja.set(Calendar.SECOND, 0);
				fecha_baja.set(Calendar.MILLISECOND, 0);
				
				if(afiliado.getBaja_fecha() != null){
					fecha_baja.setTime(afiliado.getBaja_fecha());
				}
				responseResult = ssp.bajaGrupoFamiliar(session, company, afiliado.getCuil_titular(), fecha_baja);


				if(responseResult != null){
					msg = responseResult.get_any();
					if(msg[0] !=null && msg[0].getAsString() != null){
						logger.debug("Recibiendo respuesta del WS");
						
						respuesta = this.parser.parsearResponseXML( msg[0].getAsString() );
						
					}
				}
				
				if(respuesta != null){
						service.updateNovedadesResponse(3, afiliado.getCuil_titular(), afiliado.getInte(), Integer.parseInt(respuesta[0]), respuesta[1], respuesta[2]);
					
				}else{
					logger.error("La respuesta parseada no se pudo leer correctamente");
				}
				
				
			} catch (RemoteException e) {
				logger.error("Error al procesar enviarBajaGrupoFamiliar");
				logger.error(e);
			} catch (Exception e){
				logger.error("Error al procesar respuesta enviarBajaGrupoFamiliar");
				logger.error(e);
			}
		}
		
	}
	
	public void enviarCambioPlanGrupoFliar(List<Afiliado> afiliados){	
		
		this.getSession();

		CambioPlanGrupoFamiliarResponseCambioPlanGrupoFamiliarResult responseResult;
		MessageElement[] msg; 
		List<Beneficiario> grupoFliar ;
		Beneficiario ben = null ;
		String planMedico = null;
		String cuil_titular = null;
		Date fechaVig = null;
		String[] respuesta = null;
		
		logger.info("Enviando CambioPlan de grupo familiar");
		logger.info("Novedades: " + afiliados.size());

		int i = 0;
		Afiliado a =null;
		String cuil_titu_ant = "0";
		if(afiliados != null && afiliados.size() > 0){
			cuil_titu_ant = afiliados.get(0).getCuil_titular();
		}
		
		a = afiliados.get(i);
		
//		el stored procedure debe venir ordenado por cuil_titular e inte
		while(i < afiliados.size()){	
			
			cuil_titu_ant = a.getCuil_titular();
			
			grupoFliar = new ArrayList<Beneficiario>();

			while(i < afiliados.size() && cuil_titu_ant.equals(a.getCuil_titular())){	
				
				if(a.getInte() == 0 ){ // Si es el titular tomo su plan medico y su fecha de vigencia para todo el grupo fliar.
					cuil_titular = a.getCuil_titular();
					planMedico = a.getNombrePlan();
					if(a.getIngre_fecha()!=null)fechaVig = a.getIngre_fecha() ;
					else fechaVig = new Date();
					calendarVigencia.setTime(fechaVig);
				}	
					
				ben = this.completarBeneficiario(a, calendarVigencia);
				grupoFliar.add(ben);
				
				i++;
				if(i < afiliados.size()){
					a = afiliados.get(i);
				}
			}	
			try {
				logger.debug("Invocando WS");
				logger.debug("Afiliado: " + a.getCuil() + " / " +a.getInte());
				
				responseResult = ssp.cambioPlanGrupoFamiliar(session, company, cuil_titular, planMedico, calendarVigencia) ;

				if(responseResult != null){
					msg = responseResult.get_any();
					if(msg[0] !=null && msg[0].getAsString() != null){
						logger.debug("Recibiendo respuesta del WS");
						
						respuesta = this.parser.parsearResponseXML( msg[0].getAsString() );
						
					}
				}
				
				if(respuesta != null){
					
					for (Beneficiario b : grupoFliar) {
						
						service.updateNovedadesResponse(5, b.getCUILTitular(), b.getInte(), Integer.parseInt(respuesta[0]), respuesta[1], respuesta[2]);
						
					}
					
				}else{
					logger.error("La respuesta parseada no se pudo leer correctamente");
				}
				
				
			} catch (RemoteException e) {
				logger.error("Error al procesar enviarCambioPlanGrupoFamiliar");
				logger.error(e);
			} catch (Exception e){
				logger.error("Error al procesar respuesta enviarCambioPlanGrupoFamiliar");
				logger.error(e);
			}
		}
	}
	
	private Beneficiario completarBeneficiario(Afiliado afi, Calendar fechaVig){
		
		Domicilio domicilio;
		Beneficiario ben = new Beneficiario();
		Calendar cal_fechaNac = Calendar.getInstance();
		Calendar cal_fechaFPP = Calendar.getInstance();
		Calendar cal_fechaBaja = Calendar.getInstance();
		
		ben.setCUILTitular(afi.getCuil_titular());
		ben.setFecVig(fechaVig);
		ben.setApellido(afi.getApellido());
		ben.setNombre(afi.getNombre());
		ben.setParentesco(afi.getParentesco());
		ben.setSexo(afi.getSexo());
		cal_fechaNac.setTime( afi.getNaci_fecha() );
		ben.setFecNac(cal_fechaNac );
//		Domicilio por defecto el 0
		domicilio = afi.getDomicilios()[0];
		ben.setCalle(domicilio.getCalle() );
		ben.setNroCalle(domicilio.getNumero());
		if(domicilio.getPiso() != null && domicilio.getPiso().length() >0 ){
			ben.setResto(domicilio.getPiso());
		}
		if(domicilio.getDepto() !=null && domicilio.getDepto().length() >0){
			ben.setResto(ben.getResto() + " " + domicilio.getDepto());
		}
//		ben.setResto(domicilio.getPiso() + "" + domicilio.getDepto());
		ben.setLocalidad(domicilio.getLocalidad().getDescripcion());
		ben.setCP(String.valueOf(domicilio.getPostal_codi() ));
		ben.setProvincia(this.getConversionProvincia(domicilio.getProvincia().getDescripcion()) );
		ben.setTelefono(domicilio.getTelefono());
		ben.setNacionalidad(afi.getNacionalidad());
		ben.setTipoDoc(afi.getDocumento_tipo());  
		ben.setNroDoc(afi.getDocu_numero());
		ben.setSeccional(String.valueOf(afi.getSeccional().getIdSeccional()));
		ben.setCUIL(afi.getCuil());
		if(afi.getFPP() != null ){
			cal_fechaFPP.setTime(afi.getFPP());	
			ben.setFPP(cal_fechaFPP);
//			SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
//			ben.setFPP(sdf.format(cal_fechaFPP.getTime()));
		}else{
//			ben.setFPP(null); }
			cal_fechaFPP.set(1900, 0, 1);
			ben.setFPP(cal_fechaFPP); }
		ben.setNroIntegrante(afi.getInte());
		ben.setCategoria(afi.getId_categoria());
		ben.setEstadoCivil(afi.getId_civil_esta());
		ben.setDiscapacidad( (afi.getDiscapacitado().equals("0"))?"N":"S" );
		if(afi.getBaja_fecha() != null){
			cal_fechaBaja.setTime(afi.getBaja_fecha());
//			cal_fechaBaja.set(Calendar.DAY_OF_MONTH,1); el doc dice al primero del mes...
			ben.setFecBaja(cal_fechaBaja);
		}
		ben.setInte(afi.getInte());
		
		return ben;
	}
	
	private String getConversionProvincia(String prov){

		return provinciaLetra.get(prov);

	}
	
	private Map<String,String> setearConversionProvincia(){
		
//		Las provincias estan obtenidas de la BD tabla provincia, 
//		las letras corresponden a la conversion segun doc de Omint
		Map<String, String> provinciasOspim = new HashMap<String,String>();
		provinciasOspim.put("BUENOS AIRES", "B");
		provinciasOspim.put("GRAN BUENOS AIRES", "B");
		provinciasOspim.put("CAPITAL FEDERAL", "C");
		provinciasOspim.put("CATAMARCA", "K");
		provinciasOspim.put("CHACO", "H");
		provinciasOspim.put("CHUBUT", "U");
		provinciasOspim.put("CORDOBA", "X");
		provinciasOspim.put("CORRIENTES", "W");
		provinciasOspim.put("ENTRE RIOS", "E");
		provinciasOspim.put("FORMOSA", "P");
		provinciasOspim.put("JUJUY", "Y");
		provinciasOspim.put("LA PAMPA", "L");
		provinciasOspim.put("LA RIOJA", "F");
		provinciasOspim.put("MENDOZA", "M");
		provinciasOspim.put("MISIONES", "N");
		provinciasOspim.put("NEUQUEN", "Q");
		provinciasOspim.put("RIO NEGRO", "R");
		provinciasOspim.put("SALTA", "A");
		provinciasOspim.put("SAN JUAN", "J");
		provinciasOspim.put("SAN LUIS", "D");
		provinciasOspim.put("SANTA CRUZ", "Z");
		provinciasOspim.put("SANTA FE", "S");
		provinciasOspim.put("SANTIAGO DEL ESTERO", "G");
		provinciasOspim.put("TIERRA DEL FUEGO", "V");
		provinciasOspim.put("TUCUMAN", "T");
		provinciasOspim.put("DESCONOCIDA","*");
		
		return provinciasOspim;
	}

	@Override
	public void correrAgendado(ReporteAutomatico ra) {
		this.procesarNovedades();
//	logger.info("Corrimos el Omint WS Client!!! ");
		
	}

	@Override
	public HSSFWorkbook getResultados() {
		
		HSSFWorkbook wb = null;
		
		AgendaReporteUtil arUtil = new AgendaReporteUtil();
		
		List<MensajeEnvioyRespuestaWSOmint> resultados = null;
		
		try {
			resultados = arUtil.getNovedadesProcesadas(new Date());
		
			wb = NovedadesProcesadasOmintWSExcel.generaPlanillaNovedadesProcesadas(resultados);
			
		} catch (SystemException e) {
			logger.error(e);
		}
		
		return wb;
	}

//	@Override
//	public List<MensajeEnvioyRespuestaWSOmint> correrAgendado() {
//		
//		AgendaReporteUtil arUtil = new AgendaReporteUtil();
//		
//		List<MensajeEnvioyRespuestaWSOmint> resultados = null;
//		
//		this.procesarNovedades();
////		logger.info("Corrimos el Omint WS Client!!! ");
//		
//		try {
//			resultados = arUtil.getNovedadesProcesadas(new Date());
//		} catch (SystemException e) {
//			logger.error(e);
//		}
//		
//		return  resultados;
//	}
	
} 

