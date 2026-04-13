package ar.com.ospim.webservice.test;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;

import org.apache.axis2.AxisFault;

import ar.com.ospim.webservice.prevencion.IMembershipService_CreateFamilyGroup_ValidationFault_FaultMessage;
import ar.com.ospim.webservice.prevencion.MembershipServiceStub;
import ar.com.ospim.webservice.prevencion.MembershipServiceStub.ArrayOfFamilyGroupMember;
import ar.com.ospim.webservice.prevencion.MembershipServiceStub.ArrayOfFamilyGroupMetadata;
import ar.com.ospim.webservice.prevencion.MembershipServiceStub.ArrayOfValidationDetail;
import ar.com.ospim.webservice.prevencion.MembershipServiceStub.CreateFamilyGroup;
import ar.com.ospim.webservice.prevencion.MembershipServiceStub.CreateFamilyGroupRequest;
import ar.com.ospim.webservice.prevencion.MembershipServiceStub.CreateFamilyGroupResponse7;
import ar.com.ospim.webservice.prevencion.MembershipServiceStub.FamilyGroupMember;
import ar.com.ospim.webservice.prevencion.MembershipServiceStub.FamilyGroupMetadata;
import ar.com.ospim.webservice.prevencion.MembershipServiceStub.FamilyGroupMetadataIds;
import ar.com.ospim.webservice.prevencion.MembershipServiceStub.ValidationDetail;
import ar.com.ospim.webservice.prevencion.MembershipServiceStub.ValidationFault;
import ar.com.ospim.webservice.prevencion.MembershipServiceStub.ValidationFault11;
import ar.com.ospim.webservice.service.AfiliadoOpe;


public class TestFuncionAltaGrupoFliar extends TestFuncionWSPrevencion {

	/**
	 * @param args
	 */
	
	public static void main(String[] args) {
		
		
		try {
			Calendar vigencia = Calendar.getInstance();
			vigencia.add(Calendar.DATE, 1);

			MembershipServiceStub stub = new MembershipServiceStub("https://integration-pre-ws.gruposancorseguros.com/PrevencionSaludValidatorPublicCli/Membership/MembershipService.svc");
			
			CreateFamilyGroup createFamilyGroup0 = null;
			CreateFamilyGroupRequest request = null;
			CreateFamilyGroupResponse7 response = null;
			
			AfiliadoOpe afiliado = new AfiliadoOpe();
			afiliado.setCuil_titular("20342754296");
			afiliado.setCuil("20342754296");
			afiliado.setInte(0);
			afiliado.setDocu_numero("34275429");
			afiliado.setDocumento_tipo("DU");
			afiliado.setPlanPrevencion("A3");
			afiliado.setApellido("Ape prueba");
			afiliado.setNombre("Nom prueba");
			afiliado.setSexo("M");
			afiliado.setIdTransaccion(1000);
			afiliado.setRazonSoc("ROYAL CANIN ARGENTINA SA");
			afiliado.setVigen_fecha(vigencia.getTime());

			
			createFamilyGroup0 = new CreateFamilyGroup();
			request = new CreateFamilyGroupRequest();
			
			ArrayList<FamilyGroupMember> listaMiembrosAux = new ArrayList<MembershipServiceStub.FamilyGroupMember>();
			
			ArrayOfFamilyGroupMember listaMiembrosDelGrupoFamiliar = new ArrayOfFamilyGroupMember();
			FamilyGroupMember fgm = null;
			
			if(afiliado.getInte() == 0 ){ // Si es el titular tomo su plan medico y su fecha de vigencia para todo el grupo fliar.
				
				request = crearRequestAltaGrupoFamiliar(afiliado);
				
			}	
			
			fgm = getFamilyMember(afiliado);
			
			listaMiembrosAux.add(fgm);
			
			System.out.println("Enviando x WS al Grupo Familiar: " + request.getHolderCuil() + " Transaccion N° " + request.getTransactionId());
			
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
			
			response = stub.createFamilyGroup(createFamilyGroup0);
			
			System.out.println("Respuesta: " + response.getCreateFamilyGroupResult().getTransactionId());
			
		} catch (AxisFault e) {
			System.err.println("1 " + e);
		} catch (RemoteException e) {
			System.err.println("2 " + e);
		} catch (IMembershipService_CreateFamilyGroup_ValidationFault_FaultMessage e) {
			
			System.err.println("3 " +e);
			
			ValidationFault11 v =  e.getFaultMessage();
			ValidationFault vv = v.getValidationFault();
			ArrayOfValidationDetail detallesValidacion = vv.getDetails();
	
			ValidationDetail[] detalles = detallesValidacion.getValidationDetail();
			
			for (int j = 0; j < detalles.length; j++) {
				
				MembershipServiceStub.ValidationDetail d =  detalles[j];
				
				System.err.println("pos " + j + 
						  " key: " + d.getKey() +
						  " msg: " + d.getMessage() +
						  " param: " + d.getParameterName() ); 
			}
		}	
//		}finally {
//			service.updateNovedadesResponse(ALTA_TOTAL, titularGrupoFliar.getCuil_titular(), titularGrupoFliar.getInte(), 
//					titularGrupoFliar.getIdTransaccion(), titularGrupoFliar.getMensajeDesc());
//		}

		
	}
	
	protected static CreateFamilyGroupRequest crearRequestAltaGrupoFamiliar(AfiliadoOpe a) {

		CreateFamilyGroupRequest reqAux = new CreateFamilyGroupRequest();
		ArrayOfFamilyGroupMetadata metadata = new ArrayOfFamilyGroupMetadata();
		
		reqAux.setAccountId(getAccountID(a));
		reqAux.setDelegation(201);
//		reqAux.setHealthPlan(a.getPlanPrevencion()); //select * from plan_omint
		reqAux.setHealthPlan(getHealthPlan(a.getPlanPrevencion()));
		reqAux.setHolderCuil(a.getCuil_titular());
		reqAux.setOriginId(ORIGEN_CODE);
		reqAux.setServiceStartDate(getVigenciaCorrespondiente(a.getVigen_fecha()));
//		reqAux.setServiceStartDate(getVigenciaCorrespondiente(a.getFechaOspim()));
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

}
