package ar.com.ospim.webservice;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import java.lang.String;

import org.apache.axis2.AxisFault;
import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import ar.com.ospim.afiliados.reportes.NovedadesProcesadasPrevencionWSExcel;
import ar.com.ospim.automatico.AgendadoJava;
import ar.com.ospim.automatico.beans.ReporteAutomatico;
import ar.com.ospim.automatico.service.AgendaReporteUtil;
import ar.com.ospim.global.beans.Domicilio;
import ar.com.ospim.util.DateUtils;
import ar.com.ospim.util.StringUtils;
import ar.com.ospim.webservice.prevencion.IMembershipService_AddMember_ValidationFault_FaultMessage;
import ar.com.ospim.webservice.prevencion.IMembershipService_ChangeHealthPlan_ValidationFault_FaultMessage;
import ar.com.ospim.webservice.prevencion.IMembershipService_CreateFamilyGroup_ValidationFault_FaultMessage;
import ar.com.ospim.webservice.prevencion.IMembershipService_DeleteFamilyGroup_ValidationFault_FaultMessage;
import ar.com.ospim.webservice.prevencion.IMembershipService_DeleteMember_ValidationFault_FaultMessage;
import ar.com.ospim.webservice.prevencion.MembershipServiceStub;
import ar.com.ospim.webservice.prevencion.MembershipServiceStub.*;
import ar.com.ospim.webservice.service.AfiliadoOpe;
import ar.com.ospim.webservice.service.AfiliadoServiceUtil;

import com.liferay.portal.SystemException;

public class PrevencionWSClient extends AgendadoJava {
	
	public final int ALTA_TOTAL=0;
	public final int ALTA_BENEFICIARIO=1;
	public final int MODIF_BENEFICIARIO=2;
	public final int BAJA_TOTAL=3;
	public final int BAJA_BENEFICIARIO=4;
	public final int MODIF_PLAN=5;
	
	private final int OOSS = 112608;
	public final static int ACCOUNT_ID = 392;
	public final static int ACCOUNT_ID_ROYALCANIN = 692;
	private final int SUBACCOUNT_ID = 1;
	private final int SUBACCOUNT_ID_VOLVER = 3;
	private MembershipServiceStub._char ORIGEN_CODE = new MembershipServiceStub._char() ;
	
	MembershipServiceStub stub = null;
	
	private static Logger logger = Logger.getLogger(PrevencionWSClient.class);

	private AfiliadoServiceUtil service = new AfiliadoServiceUtil();
	
	public PrevencionWSClient(){
		super();
		
		ORIGEN_CODE.set_char( (int) 'R');  //R=Obligatorio D=Voluntario
		
		logger.info("Instanciando PrevencionWSClient");
		
		/*Seteamos si corre o no con un archivo de propiedades */
		File configDir = new File(System.getProperty("catalina.base"), "conf");
		File configFile = new File(configDir, "liferay_schedulers.properties");
		
		try {
			
			InputStream stream = new FileInputStream(configFile);
			
			Properties props = new Properties();
			props.load(stream);
			String urlServicio = props.getProperty("prevencion_url_service");
				
			logger.info("Prevención WS url: " + urlServicio);
		
			stub = new MembershipServiceStub(urlServicio);
	
			logger.info("Seteamos TimeOut a 2 Minutos...");
//			long timeout = 2 * 60 * 1000; // Two minutes
//			stub._getServiceClient().getOptions().setTimeOutInMilliSeconds(timeout);
			int timeout = 2 * 60 * 1000; // Two minutes;
			stub._getServiceClient().getOptions().setProperty(
	                 HTTPConstants.SO_TIMEOUT, new Integer(timeout));
			stub._getServiceClient().getOptions().setProperty(
	                 HTTPConstants.CONNECTION_TIMEOUT, new Integer(timeout));
			
		} catch (AxisFault e) {
			logger.error(e);
		} catch (FileNotFoundException e) {
			logger.error(e);
		} catch (IOException e) {
			logger.error(e);
		}
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
		AfiliadoOpe afiliado = null;
		List<AfiliadoOpe> altaGrupoFliar = new ArrayList<AfiliadoOpe>();
		List<AfiliadoOpe> altaBeneficiario = new ArrayList<AfiliadoOpe>();
		List<AfiliadoOpe> bajaGrupoFliar = new ArrayList<AfiliadoOpe>();
		List<AfiliadoOpe> bajaBeneficiario = new ArrayList<AfiliadoOpe>();
//		List<AfiliadoOpe> modificaBeneficiario = new ArrayList<AfiliadoOpe>();
		List<AfiliadoOpe> cambioPlanGrupoFliar = new ArrayList<AfiliadoOpe>();
		
		logger.info("Procesando novedades para mandar a Prevencion via WS");
	
		afiliados = service.getTodasNovedadesPadron();
//		afiliados = new ArrayList<AfiliadoOpe>();
		
		logger.info("Cantidad de novedades: " + afiliados.size());
		
		for (Iterator<AfiliadoOpe> iterator = afiliados.iterator(); iterator.hasNext();) {
			AfiliadoOpe afiOpe = (AfiliadoOpe) iterator.next();

			if(afiOpe.getOperacion() == ALTA_TOTAL){
//			if(afiOpe.getOperacion() == ALTA_TOTAL && afiOpe.getCuil_titular().equals("20-10413590-1")){ //Alta GrupoFamiliares,
//			if(afiOpe.getOperacion() == ALTA_TOTAL && afiOpe.getCuil_titular().equals("20-07373123-3")){ //Alta GrupoFamiliares,
//			if(afiOpe.getOperacion() == ALTA_TOTAL && afiOpe.getCuil_titular().equals("20-12903329-1")){ //Alta GrupoFamiliares,
				afiliado = afiOpe ;
				altaGrupoFliar.add(afiliado);
			}
			if(afiOpe.getOperacion() == ALTA_BENEFICIARIO){
//			if(afiOpe.getOperacion() == ALTA_BENEFICIARIO && altaBeneficiario.size() < 2){ // Alta Beneficiarios,  
//			if(afiOpe.getOperacion() == ALTA_BENEFICIARIO && afiOpe.getCuil().equals("27-14174636-2")){ // Alta Beneficiarios,  
				
				afiliado = afiOpe ;
				altaBeneficiario.add(afiliado);
			}
////			if(afiOpe.getOperacion() == MODIF_BENEFICIARIO ){ // Modificar Beneficiarios,
////				afiliado = afiOpe ;
////				modificaBeneficiario.add(afiliado);
////			}
			if(afiOpe.getOperacion() == BAJA_TOTAL && afiOpe.getInte() == 0 
//			if(afiOpe.getOperacion() == BAJA_TOTAL && afiOpe.getInte() == 0 && bajaGrupoFliar.size() < 1
					/*&& ( afiOpe.getCuil_titular().equals("20-05097948-3") ||
							 afiOpe.getCuil_titular().equals("20-07637374-5") ||
							 	afiOpe.getCuil_titular().equals("20-08297370-3") )*/
					){ // Baja GrupoFamiliares, 
				afiliado = afiOpe ;
				bajaGrupoFliar.add(afiliado);
			}
			if(afiOpe.getOperacion() == BAJA_BENEFICIARIO
//			if(afiOpe.getOperacion() == BAJA_BENEFICIARIO && bajaBeneficiario.size() < 1
					/*&& (afiOpe.getCuil_titular().equals("20-17720381-6") ||
							 afiOpe.getCuil_titular().equals("20-17846329-3") ||
							 	afiOpe.getCuil_titular().equals("20-17865001-8") )*/

				){ // Baja Beneficiarios,
				afiliado = afiOpe ;
				bajaBeneficiario.add(afiliado);
			}
			if(afiOpe.getOperacion() == MODIF_PLAN && afiOpe.getInte() == 0){ // Modificar plan para grupo familiar
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
//			for (Iterator<Afiliado> iterator = bajaBeneficiario.iterator(); iterator.hasNext();) {
//				Afiliado afiliado1 = iterator.next();
//				this.enviarBajaBeneficiario(afiliado1);
//			}
			this.enviarBajaBeneficiario(bajaBeneficiario);
		}
		
//		#3
		if(altaGrupoFliar.size() > 0){
			this.enviarAltaGrupoFamiliar(altaGrupoFliar);
		}
		
//		#4
		if(altaBeneficiario.size() > 0){
//			for (Iterator<AfiliadoOpe> iterator = altaBeneficiario.iterator(); iterator.hasNext();) {
//				Afiliado afiliado1 = iterator.next();
//				this.enviarAltaBeneficiario(afiliado1);
//			}
			this.enviarAltaBeneficiario(altaBeneficiario);
		}

//		#5
		if(cambioPlanGrupoFliar.size() > 0){
			this.enviarCambioPlanGrupoFliar(cambioPlanGrupoFliar);
		}
//		
////		#6
//		if(modificaBeneficiario.size() > 0){
//			for (Iterator<Afiliado> iterator = modificaBeneficiario.iterator(); iterator.hasNext();) {
//				Afiliado afiliado1 = iterator.next();
//				this.enviarModificacionBeneficiario(afiliado1);
//			}
//		}

		logger.info("Fin procesando novedades para mandar a Prevención via WS");
		
	}
	
//	public void enviarAltaBeneficiario(AfiliadoOpe afiliado){
	public void enviarAltaBeneficiario(List<AfiliadoOpe> afiliados){

		AddMemberResponse9 response = null;
		
		for (Iterator<AfiliadoOpe> iterator = afiliados.iterator(); iterator.hasNext();) {
			AfiliadoOpe afiliado = iterator.next();
		
		logger.info("Enviando x WS al Familiar: " + afiliado.getCuil_titular() + "/" + afiliado.getInte() 
				+ " Transaccion N° " + afiliado.getIdTransaccion());
		
			try {		
				
				AddMember addMember0 = new AddMember();
				AddMemberRequest cabecera = new AddMemberRequest();
				cabecera.setHolderCuil(afiliado.getCuil_titular());
				cabecera.setAccountId(getAccountID(afiliado));
				cabecera.setServiceStartDate(getVigenciaCorrespondiente(afiliado.getVigen_fecha()));
				cabecera.setTransactionId(afiliado.getIdTransaccion());
	
				FamilyGroupMember fgm = getFamilyMember(afiliado);
				cabecera.setMember(fgm);
				
				addMember0.setRequest(cabecera);
				
				response = stub.addMember(addMember0);
				
//				if(response.getAddMemberResult().getTransactionId() == afiliado.getIdTransaccion() ){
//					service.updateNovedadesResponse(ALTA_BENEFICIARIO, afiliado.getCuil_titular(), afiliado.getInte(), 
//							afiliado.getIdTransaccion(), null);
//				}
				logger.info("RESPUESTA OK para transacción: " + response.getAddMemberResult().getTransactionId() );
				
				afiliado.setMensajeDesc(null); // salio todo bien
				
			} catch (RemoteException e){
				afiliado.setMensajeDesc(e.getMessage()!=null?e.getMessage():"RemoteException");
				logger.error("Error al procesar enviarAltaBeneficiarios");
				logger.error(e);
				
			} catch (IMembershipService_AddMember_ValidationFault_FaultMessage e) {
				logger.error("Respuesta con validacion en enviarAltaIntegrante");
	
				ValidationFault11 v =  e.getFaultMessage();
				ValidationFault vv = v.getValidationFault();
				ArrayOfValidationDetail detallesValidacion = vv.getDetails();
		
				ValidationDetail[] detalles = detallesValidacion.getValidationDetail();
				
				for (int i = 0; i < detalles.length; i++) {
					
					MembershipServiceStub.ValidationDetail d =  detalles[i];
					
					logger.info("pos " + i + 
							  " key: " + d.getKey() +
							  " msg: " + d.getMessage() +
							  " param: " + d.getParameterName() ); 
				}
		        
	//				result = e.getMessage();
				logger.error(e.getFaultMessage());
				logger.error(e);
				
				afiliado.setMensajeDesc(e.getMessage()!=null?e.getMessage():"IMembershipService_AddMember_ValidationFault_FaultMessage");
				
			} catch (UnsupportedOperationException e) {
				afiliado.setMensajeDesc(e.getMessage()!=null?e.getMessage():"UnsupportedOperationException");
				logger.fatal(e);	
			} catch (Exception e) {
				String mensajeError = "DESCONOCIDO ";
				
				if(e.getMessage()!=null){
					mensajeError += " 1: " + e.getMessage();
				}
				if(e.getCause()!=null){
					logger.fatal(e.getCause());
					if(e.getCause().getMessage() !=null){
						mensajeError += " 2: " + e.getCause().getMessage() ;
					}
				}
				if(e.getLocalizedMessage()!=null){
					mensajeError += " 3: " + e.getLocalizedMessage();
				}	
				afiliado.setMensajeDesc(mensajeError);
				logger.error(e);
			} finally {
					service.updateNovedadesResponse(ALTA_BENEFICIARIO, afiliado.getCuil_titular(), afiliado.getInte(), 
							afiliado.getIdTransaccion(), afiliado.getMensajeDesc());
			}
			
		} // fin for	
	}
	
//	public String[] enviarAltaGrupoFamiliar(List<Afiliado> afiliados){
	public void enviarAltaGrupoFamiliar(List<AfiliadoOpe> afiliados){

		CreateFamilyGroup createFamilyGroup0 = null;
		CreateFamilyGroupRequest request = null;
		CreateFamilyGroupResponse7 response = null;
		
		logger.info("Enviando alta de grupo familiar");
		logger.info("Novedades: " + afiliados.size());
//		
//		for (Iterator<Afiliado> iterator = altas.iterator(); iterator.hasNext();) {
//			Afiliado afiliado = iterator.next();
		int i = 0;
		AfiliadoOpe afiliado =null;
		AfiliadoOpe titularGrupoFliar =null;
		String cuil_titu_ant = "0";
		if(afiliados != null && afiliados.size() > 0){
			cuil_titu_ant = afiliados.get(0).getCuil_titular();
//		}else{
//			return null;
		}
		
		afiliado = afiliados.get(i);
		
//		el stored procedure debe venir ordenado por cuil_titular e inte
		while(i < afiliados.size()){	
			
			cuil_titu_ant = afiliado.getCuil_titular();
			
			createFamilyGroup0 = new CreateFamilyGroup();
			request = new CreateFamilyGroupRequest();
			
			ArrayList<FamilyGroupMember> listaMiembrosAux = new ArrayList<MembershipServiceStub.FamilyGroupMember>();
			
			ArrayOfFamilyGroupMember listaMiembrosDelGrupoFamiliar = new ArrayOfFamilyGroupMember();
			FamilyGroupMember fgm = null;
			
			while(i < afiliados.size() && cuil_titu_ant.equals(afiliado.getCuil_titular())){	
				
				if(afiliado.getInte() == 0 ){ // Si es el titular tomo su plan medico y su fecha de vigencia para todo el grupo fliar.
					titularGrupoFliar = afiliado;
					request = crearRequestAltaGrupoFamiliar(afiliado);
					
				}	
				
				fgm = getFamilyMember(afiliado);
				
				listaMiembrosAux.add(fgm);

				i++;
				if(i < afiliados.size()){
					afiliado = afiliados.get(i);
				}
			}	
			try {
				logger.info("Enviando x WS al Grupo Familiar: " + request.getHolderCuil() + " Transaccion N° " + request.getTransactionId());
				
				FamilyGroupMember[] familyGroupMembers = new FamilyGroupMember[listaMiembrosAux.size()];  // ajustar a la cantidad de integrantes
				int posicion = 0;
//				familyGroupMembers = (FamilyGroupMember[]) listaMiembrosAux.toArray();
				for (Iterator<FamilyGroupMember> iterator = listaMiembrosAux.iterator(); iterator.hasNext();) {
					FamilyGroupMember fgmAux = iterator.next();
					familyGroupMembers[posicion] = fgmAux;
					posicion++;
				}
				
				listaMiembrosDelGrupoFamiliar.setFamilyGroupMember(familyGroupMembers);
				
				request.setFamilyGroupMembers(listaMiembrosDelGrupoFamiliar);
				
				createFamilyGroup0.setRequest(request); 
				
//				logger.info("Default TimeOut WS Client: " + stub._getServiceClient().getOptions().getTimeOutInMilliSeconds());
//				logger.info("Seteamos TimeOut a 2 Minutos...");
//				
//				long timeout = 2 * 60 * 1000; // Two minutes
//				stub._getServiceClient().getOptions().setTimeOutInMilliSeconds(timeout);		
//				stub._getServiceClient().getOptions().setProperty(
//		                 HTTPConstants.SO_TIMEOUT, new Integer(timeout));
//				stub._getServiceClient().getOptions().setProperty(
//		                 HTTPConstants.CONNECTION_TIMEOUT, new Integer(timeout));
//				Options options=stub._getServiceClient().getOptions();
//				options.setProperty(org.apache.axis2.transport.http.HTTPConstants.HTTP_PROTOCOL_VERSION,org.apache.axis2.transport.http.HTTPConstants.HEADER_PROTOCOL_11);
//				options.setTimeOutInMilliSeconds(timeout);

				response = stub.createFamilyGroup(createFamilyGroup0);
				
				logger.info("RESPUESTA OK para transacción: " + response.getCreateFamilyGroupResult().getTransactionId() );

				titularGrupoFliar.setMensajeDesc(null); // salio todo bien
				
			} catch (RemoteException e) {
				titularGrupoFliar.setMensajeDesc(e.getMessage()!=null?e.getMessage():"RemoteException");
				logger.error("Error al procesar enviarAltaGrupoFamiliar");
				logger.error(e);
			} catch (IMembershipService_CreateFamilyGroup_ValidationFault_FaultMessage e) {
				titularGrupoFliar.setMensajeDesc(e.getMessage()!=null?e.getMessage():"IMembershipService_CreateFamilyGroup_ValidationFault_FaultMessage");
				logger.error("Respuesta con validacion en enviarAltaGrupoFamiliar");
				ValidationFault11 v =  e.getFaultMessage();
				ValidationFault vv = v.getValidationFault();
				ArrayOfValidationDetail detallesValidacion = vv.getDetails();
		
				ValidationDetail[] detalles = detallesValidacion.getValidationDetail();
				
				for (int j = 0; j < detalles.length; j++) {
					
					MembershipServiceStub.ValidationDetail d =  detalles[j];
					
					logger.info("pos " + j + 
							  " key: " + d.getKey() +
							  " msg: " + d.getMessage() +
							  " param: " + d.getParameterName() ); 
				}
			} catch (UnsupportedOperationException e) {
				titularGrupoFliar.setMensajeDesc(e.getMessage()!=null?e.getMessage():"UnsupportedOperationException");
				logger.fatal(e);
			} catch (Exception e) {
				String mensajeError = "DESCONOCIDO ";
				
				if(e.getMessage()!=null){
					mensajeError += " 1: " + e.getMessage();
				}
				if(e.getCause()!=null){
					logger.fatal(e.getCause());
					if(e.getCause().getMessage() !=null){
						mensajeError += " 2: " + e.getCause().getMessage() ;
					}
				}
				if(e.getLocalizedMessage()!=null){
					mensajeError += " 3: " + e.getLocalizedMessage();
				}	
				titularGrupoFliar.setMensajeDesc(mensajeError);
//				titularGrupoFliar.setMensajeDesc(e.getMessage()!=null?e.getMessage():"Exception");
				logger.error(e);
			}finally {
				service.updateNovedadesResponse(ALTA_TOTAL, titularGrupoFliar.getCuil_titular(), titularGrupoFliar.getInte(), 
						titularGrupoFliar.getIdTransaccion(), titularGrupoFliar.getMensajeDesc());
			}
			
			
		}
//		return respuesta;
	}

	private CreateFamilyGroupRequest crearRequestAltaGrupoFamiliar(AfiliadoOpe a) {

		CreateFamilyGroupRequest reqAux = new CreateFamilyGroupRequest();
		ArrayOfFamilyGroupMetadata metadata = new ArrayOfFamilyGroupMetadata();
		
		reqAux.setAccountId(getAccountID(a));
		reqAux.setDelegation(a.getSeccional().getId_seccional());
//		reqAux.setHealthPlan(a.getPlanPrevencion()); //select * from plan_omint
		reqAux.setHealthPlan(getHealthPlan(a.getPlanPrevencion()));
		reqAux.setHolderCuil(a.getCuil_titular());
		reqAux.setOriginId(ORIGEN_CODE);
//		reqAux.setServiceStartDate(getVigenciaCorrespondiente(a.getVigen_fecha()));
		reqAux.setServiceStartDate(getVigenciaCorrespondiente(a.getFechaOspim()));
		reqAux.setTransactionId(a.getIdTransaccion());
		
	//				 	CLIENTE PREFERENCIAL
		FamilyGroupMetadata prefClient;
		prefClient = new FamilyGroupMetadata();
		prefClient.setKey(FamilyGroupMetadataIds.PreferentialClient);
		prefClient.setValue(a.getClientePreferencial()==1); // obtenemos un T o F
		metadata.addFamilyGroupMetadata(prefClient);
		
	//					UOMA
		FamilyGroupMetadata uoma;
		uoma = new FamilyGroupMetadata();
		uoma.setKey(FamilyGroupMetadataIds.UOMA);
		uoma.setValue(a.isFarmaciaUoma());
		metadata.addFamilyGroupMetadata(uoma);
		
	//					AMTIMA
		FamilyGroupMetadata amtima;
		amtima = new FamilyGroupMetadata();
		amtima.setKey(FamilyGroupMetadataIds.AMTIMA);
		amtima.setValue(a.isFarmaciaAmtima());
		metadata.addFamilyGroupMetadata(amtima);
		
	//					PLAN FARMACIA
		FamilyGroupMetadata farmacia;
		farmacia = new FamilyGroupMetadata();
		farmacia.setKey(FamilyGroupMetadataIds.DrugstorePlan);
		farmacia.setValue(a.getPlanFarmacia());
		metadata.addFamilyGroupMetadata(farmacia);
		
//						SUBCUENTA
		FamilyGroupMetadata subCuenta;
		subCuenta = new FamilyGroupMetadata();
		subCuenta.setKey(FamilyGroupMetadataIds.SubAccountId);
//		subCuenta.setValue(SUBACCOUNT_ID);
		subCuenta.setValue(getSubAccountID(a));	
		metadata.addFamilyGroupMetadata(subCuenta);
		
		reqAux.setMetadata(metadata);
		
		return reqAux;
	}
	
	private ChangeHealthPlanRequest crearRequestCambioPlan(AfiliadoOpe a) {

		ChangeHealthPlanRequest reqAux = new ChangeHealthPlanRequest(); 
		FamilyGroupMetadata metadata = new FamilyGroupMetadata();
		
		metadata.setKey(FamilyGroupMetadataIds.DrugstorePlan);
		metadata.setValue(a.getPlanFarmacia());
		
		reqAux.setAccountId(getAccountID(a));
		reqAux.setHolderCuil(a.getCuil_titular());
		reqAux.setMetadata(metadata);
		reqAux.setNewHealthPlan(getHealthPlan(a.getPlanPrevencion()));
		reqAux.setStartDate(getVigenciaCorrespondienteCambioPlan(a.getAfiPlan().getVigenDesde()));
		reqAux.setTransactionId(a.getIdTransaccion());
		
		return reqAux;
	}
	
	public void enviarCambioPlanGrupoFliar(List<AfiliadoOpe> afiliados){
		
		ChangeHealthPlan changeHealthPlan10 = null;
		ChangeHealthPlanRequest request = null;
		ChangeHealthPlanResponse6 response = null;
		
		logger.info("Enviando cambio de plan de grupo familiar");
		logger.info("Novedades: " + afiliados.size());

		int i = 0;
		AfiliadoOpe afiliado =null;
		AfiliadoOpe titularGrupoFliar =null;
		String cuil_titu_ant = "0";
		
		if(afiliados != null && afiliados.size() > 0){
			cuil_titu_ant = afiliados.get(0).getCuil_titular();
		}
		
		afiliado = afiliados.get(i);
		
//		el stored procedure debe venir ordenado por cuil_titular e inte
		while(i < afiliados.size()){	
			
			cuil_titu_ant = afiliado.getCuil_titular();
			
			changeHealthPlan10 = new ChangeHealthPlan();
			request = new ChangeHealthPlanRequest();
			
			while(i < afiliados.size() && cuil_titu_ant.equals(afiliado.getCuil_titular())){	
				
				if(afiliado.getInte() == 0 ){ // Si es el titular tomo su plan medico y su fecha de vigencia para todo el grupo fliar.
					titularGrupoFliar = afiliado;
					request = crearRequestCambioPlan(afiliado);
					
				}	

				i++;
				if(i < afiliados.size()){
					afiliado = afiliados.get(i);
				}
			}	
			try {
				logger.info("Enviando x WS al Cambio Plan del Grupo Familiar: " + request.getHolderCuil() + " Transaccion N° " + request.getTransactionId());

				changeHealthPlan10.setRequest(request);
				response = stub.changeHealthPlan(changeHealthPlan10);
				
				logger.info("RESPUESTA OK para transacción: " + response.getChangeHealthPlanResult().getTransactionId() );

				titularGrupoFliar.setMensajeDesc(null); // salio todo bien
				
			} catch (RemoteException e) {
				titularGrupoFliar.setMensajeDesc(e.getMessage()!=null?e.getMessage():"RemoteException");
				logger.error("Error al procesar enviarCambioPlanGrupoFliar");
				logger.error(e);
			} catch (IMembershipService_ChangeHealthPlan_ValidationFault_FaultMessage e) {
				titularGrupoFliar.setMensajeDesc(e.getMessage()!=null?e.getMessage():"IMembershipService_ChangeHealthPlan_ValidationFault_FaultMessage");
				logger.error("Respuesta con validacion en enviarCambioPlanGrupoFliar");
				ValidationFault11 v =  e.getFaultMessage();
				ValidationFault vv = v.getValidationFault();
				ArrayOfValidationDetail detallesValidacion = vv.getDetails();
		
				ValidationDetail[] detalles = detallesValidacion.getValidationDetail();
				
				for (int j = 0; j < detalles.length; j++) {
					
					MembershipServiceStub.ValidationDetail d =  detalles[j];
					
					logger.info("pos " + j + 
							  " key: " + d.getKey() +
							  " msg: " + d.getMessage() +
							  " param: " + d.getParameterName() ); 
				}
			} catch (UnsupportedOperationException e) {
				titularGrupoFliar.setMensajeDesc(e.getMessage()!=null?e.getMessage():"UnsupportedOperationException");
				logger.fatal(e);
			} catch (Exception e) {
				String mensajeError = "DESCONOCIDO ";
				
				if(e.getMessage()!=null){
					mensajeError += " 1: " + e.getMessage();
				}
				if(e.getCause()!=null){
					logger.fatal(e.getCause());
					if(e.getCause().getMessage() !=null){
						mensajeError += " 2: " + e.getCause().getMessage() ;
					}
				}
				if(e.getLocalizedMessage()!=null){
					mensajeError += " 3: " + e.getLocalizedMessage();
				}	
				titularGrupoFliar.setMensajeDesc(mensajeError);
//				titularGrupoFliar.setMensajeDesc(e.getMessage()!=null?e.getMessage():"Exception");
				logger.error(e);
			}finally {
				service.updateNovedadesResponse(MODIF_PLAN, titularGrupoFliar.getCuil_titular(), titularGrupoFliar.getInte(), 
						titularGrupoFliar.getIdTransaccion(), titularGrupoFliar.getMensajeDesc());
			}
			
			
		}
//		return respuesta;
	}
	
//	public void enviarModificacionBeneficiario(Afiliado afiliado){
//
//		this.getSession();
//
//		ModificacionBeneficiarioResponseModificacionBeneficiarioResult responseResult;
//		MessageElement[] msg; 
//		Beneficiario ben ;
//		String[] respuesta = null;
//		
//		logger.info("Enviando modificacion de beneficiario");
////		logger.info("Novedades: " + altas.size());
////		
////		for (Iterator<Afiliado> iterator = altas.iterator(); iterator.hasNext();) {
////			Afiliado afiliado = iterator.next();
//			
//			ben = this.completarBeneficiario(afiliado, calendarVigencia);
//			
//			try {
//				logger.debug("Invocando WS");
//				logger.debug("Afiliado: " + ben.getCUIL() + " / " +ben.getNroIntegrante());
//				
//				responseResult = ssp.modificacionBeneficiario(session, company, ben.getCUILTitular(), ben.getFecVig(), ben.getApellido(), ben.getNombre(), ben.getParentesco(),
//					ben.getSexo(), ben.getFecNac(), ben.getCalle(), ben.getNroCalle(), ben.getResto(), ben.getLocalidad(), ben.getCP(), ben.getProvincia(),
//					ben.getTelefono(), ben.getTipoDoc(), ben.getNroDoc(), ben.getSeccional() , ben.getCategoria(), ben.getCUIL(), ben.getFPP(), ben.getNacionalidad(),
//					ben.getEstadoCivil(), ben.getDiscapacidad());
//				
//				if(responseResult != null){
//					msg = responseResult.get_any();
//					if(msg[0] !=null && msg[0].getAsString() != null){
//						logger.debug("Recibiendo respuesta del WS");
//						
//						respuesta = this.parser.parsearResponseXML( msg[0].getAsString() );
//						
//					}
//				}
//				
//				if(respuesta != null){
//					service.updateNovedadesResponse(2, ben.getCUILTitular(), ben.getInte(), Integer.parseInt(respuesta[0]), respuesta[1], respuesta[2]);
//
//				}else{
//					logger.error("La respuesta parseada no se pudo leer correctamente");
//				}
//				
//		
//			} catch (RemoteException e) {
//				logger.error("Error al procesar enviarModificacionBeneficiario");
//				logger.error(e);
//			} catch (Exception e){
//				logger.error("Error al procesar respuesta enviarModificacionBeneficiario");
//				logger.error(e);
//			}
//
////		}
//	}
//	
//	public void enviarBajaBeneficiario(Afiliado afiliado){
	public void enviarBajaBeneficiario(List<AfiliadoOpe> afiliados){
				
		DeleteMemberResponse8 response = null;

		logger.info("Enviando bajas de beneficiarios");
		logger.info("Novedades: " + afiliados.size());
		
		for (Iterator<AfiliadoOpe> iterator = afiliados.iterator(); iterator.hasNext();) {
			AfiliadoOpe afiliado = iterator.next();
			String cuilAux[] = afiliado.getCuil().split("-");
//			String cuilSinGuiones = "27319933021"; 
			String cuilSinGuiones = cuilAux[0]+cuilAux[1]+cuilAux[2];
			try {
				logger.info("Enviando x WS baja del Familiar: " + afiliado.getCuil() + " Transaccion N° " + afiliado.getIdTransaccion());
				
				DeleteMember deleteMember0 = new DeleteMember();
				DeleteMemberRequest cabecera = new DeleteMemberRequest();
				deleteMember0.setRequest(cabecera);
				
				cabecera.setAccountId(getAccountID(afiliado));
				cabecera.setDeleteReasonId(afiliado.getId_motivo_baja());     
//				cabecera.setHolderCuil("20327810279");
				cabecera.setHolderCuil(afiliado.getCuil_titular());
				cabecera.setNullDate(getVigenciaBajaCorrespondiente(afiliado.getBaja_fecha()));
				cabecera.setMemberCuil(Long.parseLong(cuilSinGuiones));
				cabecera.setTransactionId(afiliado.getIdTransaccion());
				
				deleteMember0.setRequest(cabecera);
				
				response = stub.deleteMember(deleteMember0); 
				
				logger.info("RESPUESTA OK para transacción: " + response.getDeleteMemberResult().getTransactionId() );
				afiliado.setMensajeDesc(null); // salio todo bien
				
			} catch (RemoteException e) {
				afiliado.setMensajeDesc(e.getMessage()!=null?e.getMessage():"RemoteException");
				logger.error("Error al procesar enviarBajaBeneficiarios");
				logger.error(e);
			} catch (IMembershipService_DeleteMember_ValidationFault_FaultMessage e) {
				afiliado.setMensajeDesc(e.getMessage()!=null?e.getMessage():"IMembershipService_DeleteMember_ValidationFault_FaultMessage");
				logger.error("Respuesta con validacion en enviarBajaFamiliar");
				ValidationFault11 v =  e.getFaultMessage();
				ValidationFault vv = v.getValidationFault();
				ArrayOfValidationDetail detallesValidacion = vv.getDetails();
			
				ValidationDetail[] detalles = detallesValidacion.getValidationDetail();
				
				for (int i = 0; i < detalles.length; i++) {
					
					MembershipServiceStub.ValidationDetail d =  detalles[i];
					
					logger.info("pos " + i + 
							  " key: " + d.getKey() +
							  " msg: " + d.getMessage() +
							  " param: " + d.getParameterName() ); 
				}
				   
//				result = e.getMessage();
				logger.error(e.getFaultMessage());
				logger.error(e);
				
			} catch (UnsupportedOperationException e) {
				afiliado.setMensajeDesc(e.getMessage()!=null?e.getMessage():"UnsupportedOperationException");
				logger.fatal(e);
				
			} catch (Exception e) {
				String mensajeError = "DESCONOCIDO ";
				
				if(e.getMessage()!=null){
					mensajeError += " 1: " + e.getMessage();
				}
				if(e.getCause()!=null){
					logger.fatal(e.getCause());
					if(e.getCause().getMessage() !=null){
						mensajeError += " 2: " + e.getCause().getMessage() ;
					}
				}
				if(e.getLocalizedMessage()!=null){
					mensajeError += " 3: " + e.getLocalizedMessage();
				}	
				afiliado.setMensajeDesc(mensajeError);
				logger.error(e);
			} finally {
				service.updateNovedadesResponse(BAJA_BENEFICIARIO, afiliado.getCuil_titular(), afiliado.getInte(), 
						afiliado.getIdTransaccion(), afiliado.getMensajeDesc());
			}	
		}
	}
//	
	public void enviarBajaGrupoFamiliar(List<AfiliadoOpe> afiliados){
		
		DeleteFamilyGroupResponse10 response = null;
		
		logger.info("Enviando baja de grupo familiar");
		logger.info("Novedades: " + afiliados.size());
//		
		for (Iterator<AfiliadoOpe> iterator = afiliados.iterator(); iterator.hasNext();) {
			AfiliadoOpe afiliado = iterator.next();
			
			try {
				logger.info("Enviando x WS baja del Grupo Familiar: " + afiliado.getCuil() + " Transaccion N° " + afiliado.getIdTransaccion());
				
				DeleteFamilyGroup deleteFamilyGroup0 = new DeleteFamilyGroup();
				
				DeleteFamilyGroupRequest cabecera = new DeleteFamilyGroupRequest();
				cabecera.setAccountId(getAccountID(afiliado));
				cabecera.setDeleteReasonId(afiliado.getId_motivo_baja());  
//				cabecera.setHolderCuil("20-94677054-0");
				cabecera.setHolderCuil(afiliado.getCuil_titular());
	 			cabecera.setNullDate(getVigenciaBajaCorrespondiente(afiliado.getBaja_fecha()));
				cabecera.setTransactionId(afiliado.getIdTransaccion());
				
				deleteFamilyGroup0.setRequest(cabecera);
				
				response = stub.deleteFamilyGroup(deleteFamilyGroup0);
				
				logger.info("RESPUESTA OK para transacción: " +response.getDeleteFamilyGroupResult().getTransactionId() );
				
				afiliado.setMensajeDesc(null); // salio todo bien
				
			} catch (RemoteException e) {
				afiliado.setMensajeDesc(e.getMessage()!=null?e.getMessage():"RemoteException");
				logger.error("Error al procesar enviarBajaGrupoFamiliar");
				logger.error(e);
			} catch (IMembershipService_DeleteFamilyGroup_ValidationFault_FaultMessage e) {
				afiliado.setMensajeDesc(e.getMessage()!=null?e.getMessage():"IMembershipService_DeleteFamilyGroup_ValidationFault_FaultMessage");
				logger.error("Respuesta con validacion en enviarBajaGrupoFamiliar");
				
				ValidationFault11 v =  e.getFaultMessage();
				ValidationFault vv = v.getValidationFault();
				ArrayOfValidationDetail detallesValidacion = vv.getDetails();
			
				ValidationDetail[] detalles = detallesValidacion.getValidationDetail();
				
				for (int i = 0; i < detalles.length; i++) {
					
					MembershipServiceStub.ValidationDetail d =  detalles[i];
					
					logger.info("pos " + i + 
							  " key: " + d.getKey() +
							  " msg: " + d.getMessage() +
							  " param: " + d.getParameterName() ); 
				}
				   
//				result = e.getMessage();
				logger.error(e.getFaultMessage());
				logger.error(e);
				
			} catch (UnsupportedOperationException e) {
				afiliado.setMensajeDesc(e.getMessage()!=null?e.getMessage():"UnsupportedOperationException");
				logger.fatal(e);
			} catch (Exception e) {
				String mensajeError = "DESCONOCIDO ";
				
				if(e.getMessage()!=null){
					mensajeError += " 1: " + e.getMessage();
				}
				if(e.getCause()!=null){
					logger.fatal(e.getCause());
					if(e.getCause().getMessage() !=null){
						mensajeError += " 2: " + e.getCause().getMessage() ;
					}
				}
				if(e.getLocalizedMessage()!=null){
					mensajeError += " 3: " + e.getLocalizedMessage();
				}	
				afiliado.setMensajeDesc(mensajeError);
				logger.error(e);
			} finally {
				service.updateNovedadesResponse(BAJA_TOTAL, afiliado.getCuil_titular(), afiliado.getInte(), 
						afiliado.getIdTransaccion(), afiliado.getMensajeDesc());
			}
		}
	}

	@Override
	public void correrAgendado(ReporteAutomatico ra) {
		this.procesarNovedades();
//	logger.info("Corrimos el Prevencion WS Client!!! ");
		
	}

	@Override
	public HSSFWorkbook getResultados() {
		
		HSSFWorkbook wb = null;
		
		AgendaReporteUtil arUtil = new AgendaReporteUtil();
		
		List<AfiliadoOpe> resultados = null;
		
		try {
			resultados = arUtil.getNovedadesProcesadasPrevencion(new Date());
		
			wb = NovedadesProcesadasPrevencionWSExcel.generaPlanillaNovedadesProcesadas(resultados);
			
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
	
	private FamilyGroupMember getFamilyMember(AfiliadoOpe afi) {
		
		FamilyGroupMember fgm = new FamilyGroupMember();

		try{
		
			Domicilio dom = afi.getDomicilioDefault();
			
			ArrayOfstring listaEmails = null;
			ArrayOfstring listaTelefonos = new ArrayOfstring();
			
//			TimeZone tz = TimeZone.getTimeZone("America/Buenos_Aires");
			Calendar naciFech = Calendar.getInstance(); // fecha de hoy
//			naciFech.setTimeZone(tz);
//			Calendar naciFech = Calendar.getInstance();
			Calendar fpp = null;
			
			naciFech.setTime(afi.getNaci_fecha());
			
			if(afi.getFPP()!=null){
				fpp = Calendar.getInstance();
				fpp.setTime(afi.getFPP());
			}
			
			if(afi.getEmail()!=null && !afi.getEmail().isEmpty()){
				listaEmails = new ArrayOfstring();
				listaEmails.addString(afi.getEmail());
			}	
			
			String telefonoCompleto = null;
			try {
				String codAreaTelefono = dom.getCod_area_telefono();
				String telefono = dom.getTelefono();
				telefonoCompleto = (codAreaTelefono!=null&&!codAreaTelefono.isEmpty()?(String.format("%05d",Integer.parseInt(codAreaTelefono))+"-"):"").concat(telefono!=null&&!telefono.isEmpty()?telefono.replaceAll("-", ""):null);
			} catch (Exception e) {
				logger.error("parseando telefono");
				logger.error(e);
			}
					
			String telefonoLaboralCompleto = null;
			try {
				String codAreaTelLaboral = dom.getCod_area_tel_laboral();
				String telLaboral = dom.getTel_laboral();
				telefonoLaboralCompleto = (codAreaTelLaboral!=null&&!codAreaTelLaboral.isEmpty()?(String.format("%05d",Integer.parseInt(codAreaTelLaboral))+"-"):"").concat(telLaboral!=null&&!telLaboral.isEmpty()?telLaboral.replaceAll("-", ""):null);
			} catch (Exception e) {
				logger.error("parseando telefono laboral");
				logger.error(e);
			}
			
			String celularCompleto = null;
			try {
				String codAreaCelular = dom.getCod_area_celular();
				String celular = dom.getCelular();
				celularCompleto = (codAreaCelular!=null&&!codAreaCelular.isEmpty()?(String.format("%05d",Integer.parseInt(codAreaCelular))+"-"):"").concat(celular!=null&&!celular.isEmpty()?celular.replaceAll("-", ""):null);
			} catch (Exception e) {
				logger.error("parseando celular");
				logger.error(e);
			}
					
			if(telefonoCompleto!=null&&!telefonoCompleto.isEmpty()){
				listaTelefonos.addString(telefonoCompleto);
			}
			if(telefonoLaboralCompleto!=null&&!telefonoLaboralCompleto.isEmpty()){
				listaTelefonos.addString(telefonoLaboralCompleto);
			}
			if(celularCompleto!=null&&!celularCompleto.isEmpty()){
				listaTelefonos.addString(celularCompleto);
			}
//			if(listaTelefonos.getString().length == 0){
//				listaTelefonos = null;
//			}
			
			fgm.setBirthDate(naciFech);
			fgm.setCityCode(dom.getLocalidad().getId_localidadesss());      //(dom.getLocalidadId());
			fgm.setContributeSalarySubject(afi.getInte()==0?1:java.lang.Integer.MIN_VALUE); // deberia ser null para integrantes y > 0 para titular
			fgm.setCuil(afi.getCuil());
			fgm.setDepartment(StringUtils.checkNotEmpty(dom.getDepto())?dom.getDepto():null);
			fgm.setDocumentNumber(Integer.parseInt(afi.getDocu_numero()));
			fgm.setDocumentTypeId(afi.getDocumento_tipo());
			fgm.setEmails(listaEmails);
			fgm.setExternalIdentification(String.valueOf(afi.getId_ospim())+"/"+afi.getInte());
			fgm.setElectedSocialInsurance(afi.getInte()==0?OOSS:Integer.MIN_VALUE);  //deberia ser null para los que no tienen empleador
			fgm.setFirstName(afi.getNombre());
			fgm.setFixedPhones(listaTelefonos) ; 		 
			fgm.setFloor(StringUtils.checkNotEmpty(dom.getPiso())?dom.getPiso():null);
			MembershipServiceStub._char sexoId = new MembershipServiceStub._char();
			sexoId.set_char( (int) afi.getSexo().trim().toUpperCase().charAt(0));
			fgm.setGenderCode(sexoId);
			MembershipServiceStub._char discapaci = new MembershipServiceStub._char();
			int nroDiscap = Character.getNumericValue(afi.getDiscapacitado().equalsIgnoreCase("1")?'S':'N');
			discapaci.set_char(nroDiscap);
			fgm.setIncapable(discapaci);
	//			param2.setIncapable(afi.getDiscapacitado());
			fgm.setLastName(afi.getApellido());
			fgm.setMaritalStatusId(afi.getId_civil_esta());
			fgm.setNationalityId(afi.getIdNacionalidadSSS());  //(afi.getNacionalidad()) de la sss;
			fgm.setNeighborhood(Integer.MIN_VALUE);
			fgm.setPMIExpirationDate(fpp);
			fgm.setRelationshipCode(afi.getId_parentesco());
			fgm.setStreet(dom.getCalle());
			fgm.setStreetNumber(StringUtils.checkNotEmpty(dom.getNumero())?Integer.parseInt(dom.getNumero()):0 );
			fgm.setSubNeighborhood(StringUtils.checkNotEmpty(dom.getBarrio())?dom.getBarrio():null);
			fgm.setWorkingRelationshipCuit(afi.getInte()==0?afi.getCuit():null ); // la ultima situ laboral 
			fgm.setWorkingRelationshipSocialReason(afi.getInte()==0?afi.getRazonSoc().trim():null);
			fgm.setZipCode(StringUtils.checkNotEmpty(dom.getPostal_codi())?Short.parseShort(dom.getPostal_codi()):(short)0);
		
		}catch (Exception e) {
			logger.error(e);
		}
		
		return fgm;
	}
	
	private String getHealthPlan(String planDesc) {
//		if(planDesc.equalsIgnoreCase("AG")){
//			return "A GENERAL"; 
//		}else{
//			return planDesc;
//		}
		if(planDesc.equalsIgnoreCase("AG")){
			return "AG MOLIN"; 
		}else if (planDesc.equalsIgnoreCase("A1")){
			return "A Molinero";
		}else{
			return planDesc;
		}
	}
	
	private Calendar getVigenciaCorrespondiente(Date vigenciaReal){
		
		Calendar vigencia = Calendar.getInstance();
		
		Calendar primeroDelMes = Calendar.getInstance();
		
		primeroDelMes.set(Calendar.DATE,1);
//		Calendar hoy = Calendar.getInstance();
		
//		intentamos no mandar vigencias anteriores al dia en curso para no pagar capitas retroactivas
		if(!vigenciaReal.before(primeroDelMes.getTime())){
			vigencia.setTime(vigenciaReal);
		}else{
			vigencia.setTime(primeroDelMes.getTime());
		}
		
		return vigencia;
		
	}
	
	public static Calendar getVigenciaBajaCorrespondiente(Date vigenciaReal){

		
		final long MILLSECS_PER_DAY = 24 * 60 * 60 * 1000;
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		
		Calendar vigencia = Calendar.getInstance();
		
		Calendar hoy = Calendar.getInstance();
//		hoy.add(Calendar.DATE, -1);
		
		logger.debug("fecha vigenciaReal: " + sdf.format(vigenciaReal));
		logger.debug("fecha hoy: " + sdf.format(hoy.getTime()));
		
//		intentamos no mandar vigencias anteriores al dia en curso para no pagar capitas retroactivas
//		if(!vigenciaReal.before(hoy.getTime())){
//			vigencia.setTime(vigenciaReal);
//		}else{
//			vigencia.setTime(hoy.getTime());
//		}
		long diferencia = ( DateUtils.getMismoDia_00_00hs(vigenciaReal).getTime() 
							- DateUtils.getMismoDia_00_00hs(hoy.getTime()).getTime() )/MILLSECS_PER_DAY; 
		
		logger.debug("Diferencia en dias = " + diferencia); 

	/**
		Si es el ultimo viernes del mes, mandamos bajas futuras (las que caen el finde) (dif 1 a 2)
		Si es el primer lunes del mes y se detectan bajas del mes anterior (supongamos no corrio el viernes)
		mandamos bajas al corte del mes anterior (dif -2 a-1)
	*/
		
//		if((diferencia >= -1 && diferencia <= 2) // esto aparece si se filtran bajas x ultimo viernes del mes (corta el mes en el finde)
//				|| ((hoy.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY) && (diferencia >= -2 && diferencia < 0) ) 
//		){
//			vigencia.setTime(vigenciaReal);
//		}else{
//			vigencia.setTime(hoy.getTime()); // mismo dia o baja retroctiva
//		}
		if((diferencia >= -1 && diferencia <= 2) // esto aparece si se filtran bajas x ultimo viernes del mes (corta el mes en el finde)
				|| ((hoy.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY) && (diferencia >= -2 && diferencia < 0) ) 
		){
			vigencia.setTime(vigenciaReal);
		}else if(diferencia > 2){ // futura
			vigencia.setTime(vigenciaReal);
		}else{	
			vigencia.setTime(hoy.getTime()); // mismo dia o baja retroctiva
		}

		return vigencia;
		
	}
	
	private Calendar getVigenciaCorrespondienteCambioPlan(Date vigenciaReal){
	/**
		4. Cambio de plan (para proceso de Facturación): se define que los cambios que se
		produzcan hasta día 10 tengan vigencia para el mes en curso; y que los cambios que 
		se produzcan a partir del día 11, tengan vigencia para el mes próximo. Si el cambio es 
		retroactivo, se sigue la misma regla. SV modificará el web service para cumplir con 
		esta definición.*/
		Calendar hoy = Calendar.getInstance();
		int diaActual = hoy.get(Calendar.DAY_OF_MONTH);
		
		Calendar vigenciaRealAux = Calendar.getInstance();
		vigenciaRealAux.setTime(vigenciaReal);
		
		Calendar vigencia = Calendar.getInstance();
		
		Calendar primeroDelMes = Calendar.getInstance();
//		Calendar onceDelMes = Calendar.getInstance();
		Calendar primeroDelProximoMes = Calendar.getInstance();
		
		primeroDelMes.set(Calendar.DATE,1);
		
//		onceDelMes.set(Calendar.DATE,11);
		
		primeroDelProximoMes.set(Calendar.DATE,1);
		primeroDelProximoMes.add(Calendar.MONTH,1);

//		if(vigenciaRealAux.compareTo(onceDelMes) < 0){
//			vigencia.setTime(primeroDelMes.getTime());
//		}else if(vigenciaRealAux.compareTo(primeroDelProximoMes) > 0){
//			vigencia.setTime(vigenciaReal);
//		}else if(vigenciaRealAux.compareTo(onceDelMes) >= 0){
//			vigencia.setTime(primeroDelProximoMes.getTime());
//		}
//		evaluamos novedades antes y despues del dia 10. 
//		si las novedades son futuras, tener en cuenta fecha del mes que corresponde
		if(diaActual > 10){
			if(vigenciaRealAux.compareTo(primeroDelProximoMes) > 0){
				vigencia.setTime(vigenciaReal);
			}else{	
				vigencia.setTime(primeroDelProximoMes.getTime());
			}
		}else{
			if(vigenciaRealAux.compareTo(primeroDelProximoMes) > 0){
				vigencia.setTime(vigenciaReal);
			}else{	
				vigencia.setTime(primeroDelMes.getTime());
			}
		}
		
		return vigencia;
		
	}
	
	private int getAccountID(AfiliadoOpe afi){
		
		int idCta = ACCOUNT_ID;
		
		if(/*afi.getPlanPrevencion().equalsIgnoreCase("A3") &&*/ 
//				afi.getRazonSoc().trim().equalsIgnoreCase("ROYAL CANIN ARGENTINA SA")){
			afi.getCuit().equalsIgnoreCase("30604871286")){
			idCta = ACCOUNT_ID_ROYALCANIN;
		}
		
		return idCta;
		
	}
	
	private int getSubAccountID(AfiliadoOpe afi){
	
		int idSubCta = SUBACCOUNT_ID;
		
		if(afi.getProyecto() != null && afi.getProyecto().equalsIgnoreCase("VOLVER2016")
//				&& !afi.getRazonSoc().trim().equalsIgnoreCase("ROYAL CANIN ARGENTINA SA")){
				&& !afi.getCuit().equalsIgnoreCase("30604871286")) {
			idSubCta = SUBACCOUNT_ID_VOLVER;
		}
		
		return idSubCta;
	}
	
} 