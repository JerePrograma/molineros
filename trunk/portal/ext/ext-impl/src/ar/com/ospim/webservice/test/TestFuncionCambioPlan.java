package ar.com.ospim.webservice.test;

import java.rmi.RemoteException;
import java.util.Calendar;

import org.apache.axis2.AxisFault;

import ar.com.ospim.webservice.prevencion.IMembershipService_ChangeHealthPlan_ValidationFault_FaultMessage;
import ar.com.ospim.webservice.prevencion.MembershipServiceStub;
import ar.com.ospim.webservice.prevencion.MembershipServiceStub.ArrayOfValidationDetail;
import ar.com.ospim.webservice.prevencion.MembershipServiceStub.ChangeHealthPlan;
import ar.com.ospim.webservice.prevencion.MembershipServiceStub.ChangeHealthPlanRequest;
import ar.com.ospim.webservice.prevencion.MembershipServiceStub.ChangeHealthPlanResponse6;
import ar.com.ospim.webservice.prevencion.MembershipServiceStub.FamilyGroupMetadata;
import ar.com.ospim.webservice.prevencion.MembershipServiceStub.FamilyGroupMetadataIds;
import ar.com.ospim.webservice.prevencion.MembershipServiceStub.ValidationDetail;
import ar.com.ospim.webservice.prevencion.MembershipServiceStub.ValidationFault;
import ar.com.ospim.webservice.prevencion.MembershipServiceStub.ValidationFault11;
import ar.com.ospim.webservice.service.AfiliadoOpe;


public class TestFuncionCambioPlan extends TestFuncionWSPrevencion {

	/**
	 * @param args
	 */
	
	public static void main(String[] args) {
		
		
		try {
			Calendar vigencia = Calendar.getInstance();
			vigencia.set(Calendar.DATE, 1);
			vigencia.set(Calendar.MONTH, 4);
			
			MembershipServiceStub stub = new MembershipServiceStub("https://integration-pre-ws.gruposancorseguros.com/PrevencionSaludValidatorPublic/Membership/MembershipService.svc");
		
		
			ChangeHealthPlan changeHealthPlan10 = new ChangeHealthPlan();
			ChangeHealthPlanResponse6 response = null;
		
			ChangeHealthPlanRequest reqAux = new ChangeHealthPlanRequest(); 
			FamilyGroupMetadata metadata = new FamilyGroupMetadata();
			
			AfiliadoOpe afi = new AfiliadoOpe();
			afi.setCuil_titular("20289563653");
//			afi.setPlanPrevencion("A3");
			afi.setPlanPrevencion("A1");
			afi.setIdTransaccion(1000);
//			afi.setRazonSoc("PRUEBA SA");
			afi.setRazonSoc("ROYAL CANIN ARGENTINA SA");
			afi.setVigen_fecha(vigencia.getTime());
			
			metadata.setKey(FamilyGroupMetadataIds.DrugstorePlan);
			metadata.setValue("B");
			
			reqAux.setAccountId(getAccountID(afi));
			reqAux.setHolderCuil(afi.getCuil_titular());
			reqAux.setMetadata(metadata);
			reqAux.setNewHealthPlan(getHealthPlan(afi.getPlanPrevencion()));
			reqAux.setStartDate(vigencia);
			reqAux.setTransactionId(1100);
		
			changeHealthPlan10.setRequest(reqAux);
			
//			FIXME con paciencia buscar como se invocaba el mensaje
//			System.out.println("Mensaje: " + stub._getServiceClient().getServiceContext().getAxisService()
//					);
			
			stub.changeHealthPlan(changeHealthPlan10);
			
			
			response = stub.changeHealthPlan(changeHealthPlan10);
			
			System.out.println("Respuesta: " + response.getChangeHealthPlanResult().getTransactionId());
			
		} catch (AxisFault e) {
			System.err.println("1 " + e);
		} catch (RemoteException e) {
			System.err.println("2 " + e);
		} catch (IMembershipService_ChangeHealthPlan_ValidationFault_FaultMessage e) {
			
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

		
	}

}
