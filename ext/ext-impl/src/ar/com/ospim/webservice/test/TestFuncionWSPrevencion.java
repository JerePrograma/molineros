package ar.com.ospim.webservice.test;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import ar.com.ospim.global.beans.Domicilio;
import ar.com.ospim.util.DateUtils;
import ar.com.ospim.util.StringUtils;
import ar.com.ospim.webservice.prevencion.MembershipServiceStub;
import ar.com.ospim.webservice.prevencion.MembershipServiceStub.ArrayOfFamilyGroupMetadata;
import ar.com.ospim.webservice.prevencion.MembershipServiceStub.ArrayOfstring;
import ar.com.ospim.webservice.prevencion.MembershipServiceStub.CreateFamilyGroupRequest;
import ar.com.ospim.webservice.prevencion.MembershipServiceStub.FamilyGroupMember;
import ar.com.ospim.webservice.prevencion.MembershipServiceStub.FamilyGroupMetadata;
import ar.com.ospim.webservice.prevencion.MembershipServiceStub.FamilyGroupMetadataIds;
import ar.com.ospim.webservice.service.AfiliadoOpe;


public abstract class TestFuncionWSPrevencion {

	/**
	 * @param args
	 */
	protected final static int OOSS = 112608;
	protected final static int ACCOUNT_ID = 392;
	protected final static int ACCOUNT_ID_ROYALCANIN = 692;
	protected final static int SUBACCOUNT_ID = 1;
	protected final static int SUBACCOUNT_ID_VOLVER = 3;
	
	protected static MembershipServiceStub._char ORIGEN_CODE = new MembershipServiceStub._char() ;


	protected static String getHealthPlan(String planDesc) {
		if(planDesc.equalsIgnoreCase("AG")){
			return "A GENERAL"; 
		}else{
			return planDesc;
		}
		
	}
	
	protected static Calendar getVigenciaCorrespondiente(Date vigenciaReal){
		
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
	
	protected static int getAccountID(AfiliadoOpe afi){
		
		int idCta = ACCOUNT_ID;
		
		if(/*afi.getPlanPrevencion().equalsIgnoreCase("A3") 
				&& */afi.getRazonSoc().equalsIgnoreCase("ROYAL CANIN ARGENTINA SA")){
			idCta = ACCOUNT_ID_ROYALCANIN;
		}
		System.out.println("ACCOUNT ID " + idCta);
		return idCta;
		
	}
	
	protected static CreateFamilyGroupRequest crearRequestAltaGrupoFamiliar(AfiliadoOpe a) {

		CreateFamilyGroupRequest reqAux = new CreateFamilyGroupRequest();
		ArrayOfFamilyGroupMetadata metadata = new ArrayOfFamilyGroupMetadata();
		
		reqAux.setAccountId(getAccountID(a));
		reqAux.setDelegation(a.getSeccional().getId_seccional());
//		reqAux.setHealthPlan(a.getPlanPrevencion()); //select * from plan_omint
		reqAux.setHealthPlan(getHealthPlan(a.getPlanPrevencion()));
		reqAux.setHolderCuil(a.getCuil_titular());
		reqAux.setOriginId(ORIGEN_CODE);
//		reqAux.setServiceStartDate(getVigenciaCorrespondiente(a.getVigen_fecha()));
		reqAux.setServiceStartDate(getVigenciaCorrespondiente(new Date()));
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
		if(a.getProyecto() != null && a.getProyecto().equalsIgnoreCase("VOLVER2016")){
			subCuenta.setValue(SUBACCOUNT_ID_VOLVER);
		}else{
			subCuenta.setValue(SUBACCOUNT_ID);
		}	
		metadata.addFamilyGroupMetadata(subCuenta);
		
		reqAux.setMetadata(metadata);
		
		return reqAux;
	}
	
	protected static FamilyGroupMember getFamilyMember(AfiliadoOpe afi) {
		
		FamilyGroupMember fgm = new FamilyGroupMember();

		try{
			Calendar fechaNacim = Calendar.getInstance();
			fechaNacim.set(Calendar.DATE, 18);
			fechaNacim.set(Calendar.MONTH, 4);
			fechaNacim.set(Calendar.YEAR, 1986);
			
//			Domicilio dom = afi.getDomicilioDefault();
			
			ArrayOfstring listaEmails = null;
			ArrayOfstring listaTelefonos = new ArrayOfstring();

			Calendar fpp = null;
					
			if(afi.getFPP()!=null){
				fpp = Calendar.getInstance();
				fpp.setTime(afi.getFPP());
			}
			
			if(afi.getEmail()!=null && !afi.getEmail().isEmpty()){
				listaEmails = new ArrayOfstring();
				listaEmails.addString(afi.getEmail());
			}	
			
			String telefonoCompleto = null;
//			try {
//				String codAreaTelefono = dom.getCod_area_telefono();
//				String telefono = dom.getTelefono();
//				telefonoCompleto = (codAreaTelefono!=null&&!codAreaTelefono.isEmpty()?(String.format("%05d",Integer.parseInt(codAreaTelefono))+"-"):"").concat(telefono!=null&&!telefono.isEmpty()?telefono.replaceAll("-", ""):null);
//			} catch (Exception e) {
//				System.err.println("parseando telefono");
//				System.err.println(e);
//			}
					
			String telefonoLaboralCompleto = null;
//			try {
//				String codAreaTelLaboral = dom.getCod_area_tel_laboral();
//				String telLaboral = dom.getTel_laboral();
//				telefonoLaboralCompleto = (codAreaTelLaboral!=null&&!codAreaTelLaboral.isEmpty()?(String.format("%05d",Integer.parseInt(codAreaTelLaboral))+"-"):"").concat(telLaboral!=null&&!telLaboral.isEmpty()?telLaboral.replaceAll("-", ""):null);
//			} catch (Exception e) {
//				System.err.println("parseando telefono laboral");
//				System.err.println(e);
//			}
			
			String celularCompleto = null;
//			try {
//				String codAreaCelular = dom.getCod_area_celular();
//				String celular = dom.getCelular();
//				celularCompleto = (codAreaCelular!=null&&!codAreaCelular.isEmpty()?(String.format("%05d",Integer.parseInt(codAreaCelular))+"-"):"").concat(celular!=null&&!celular.isEmpty()?celular.replaceAll("-", ""):null);
//			} catch (Exception e) {
//				System.err.println("parseando celular");
//				System.err.println(e);
//			}
					
//			if(telefonoCompleto!=null&&!telefonoCompleto.isEmpty()){
//				listaTelefonos.addString(telefonoCompleto);
//			}
//			if(telefonoLaboralCompleto!=null&&!telefonoLaboralCompleto.isEmpty()){
//				listaTelefonos.addString(telefonoLaboralCompleto);
//			}
//			if(celularCompleto!=null&&!celularCompleto.isEmpty()){
//				listaTelefonos.addString(celularCompleto);
//			}
//			if(listaTelefonos.getString().length == 0){
//				listaTelefonos = null;
//			}
			
			fgm.setBirthDate(fechaNacim);
			fgm.setCityCode(1000);      //(dom.getLocalidadId());
			fgm.setContributeSalarySubject(afi.getInte()==0?1:java.lang.Integer.MIN_VALUE); // deberia ser null para integrantes y > 0 para titular
			fgm.setCuil(afi.getCuil());
			fgm.setDepartment(null);
			fgm.setDocumentNumber(Integer.parseInt(afi.getDocu_numero()));
			fgm.setDocumentTypeId(afi.getDocumento_tipo());
			fgm.setEmails(listaEmails);
			fgm.setExternalIdentification(String.valueOf(85602)+"/"+afi.getInte());
			fgm.setElectedSocialInsurance(afi.getInte()==0?OOSS:Integer.MIN_VALUE);  //deberia ser null para los que no tienen empleador
			fgm.setFirstName(afi.getNombre());
			fgm.setFixedPhones(listaTelefonos) ; 		 
			fgm.setFloor(null);
			MembershipServiceStub._char sexoId = new MembershipServiceStub._char();
			sexoId.set_char( (int) afi.getSexo().trim().toUpperCase().charAt(0));
			fgm.setGenderCode(sexoId);
			MembershipServiceStub._char discapaci = new MembershipServiceStub._char();
			int nroDiscap = Character.getNumericValue('N');
			discapaci.set_char(nroDiscap);
			fgm.setIncapable(discapaci);
	//			param2.setIncapable(afi.getDiscapacitado());
			fgm.setLastName(afi.getApellido());
			fgm.setMaritalStatusId(2);
			fgm.setNationalityId(12);  //(afi.getNacionalidad()) de la sss;
			fgm.setNeighborhood(Integer.MIN_VALUE);
			fgm.setPMIExpirationDate(fpp);
			fgm.setRelationshipCode(8);
			fgm.setStreet("la calle");
			fgm.setStreetNumber(256);
			fgm.setSubNeighborhood(null);
			fgm.setWorkingRelationshipCuit(null); // la ultima situ laboral 
			fgm.setWorkingRelationshipSocialReason(null);
			fgm.setZipCode((short)0);
		
		}catch (Exception e) {
			System.err.println(e);
		}
		
		return fgm;
	}
	
	protected static Calendar getVigenciaBajaCorrespondiente(Date vigenciaReal){

		
		final long MILLSECS_PER_DAY = 24 * 60 * 60 * 1000;
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		
		Calendar vigencia = Calendar.getInstance();
		
		Calendar hoy = Calendar.getInstance();
//		hoy.add(Calendar.DATE, -1);
		
		System.out.println("fecha vigenciaReal: " + sdf.format(vigenciaReal));
		System.out.println("fecha hoy: " + sdf.format(hoy.getTime()));
		
//		intentamos no mandar vigencias anteriores al dia en curso para no pagar capitas retroactivas
//		if(!vigenciaReal.before(hoy.getTime())){
//			vigencia.setTime(vigenciaReal);
//		}else{
//			vigencia.setTime(hoy.getTime());
//		}
		long diferencia = ( DateUtils.getMismoDia_00_00hs(vigenciaReal).getTime() 
							- DateUtils.getMismoDia_00_00hs(hoy.getTime()).getTime() )/MILLSECS_PER_DAY; 
		
		System.out.println("Diferencia en dias = " + diferencia); 

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
}
