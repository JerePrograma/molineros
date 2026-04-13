package ar.com.ospim.webservice.test;

import java.rmi.RemoteException;
import java.util.Calendar;

import org.apache.axis2.AxisFault;

import ar.com.ospim.webservice.prevencion.IMembershipService_DeleteFamilyGroup_ValidationFault_FaultMessage;
import ar.com.ospim.webservice.prevencion.MembershipServiceStub;
import ar.com.ospim.webservice.prevencion.MembershipServiceStub.ArrayOfValidationDetail;
import ar.com.ospim.webservice.prevencion.MembershipServiceStub.DeleteFamilyGroup;
import ar.com.ospim.webservice.prevencion.MembershipServiceStub.DeleteFamilyGroupRequest;
import ar.com.ospim.webservice.prevencion.MembershipServiceStub.DeleteFamilyGroupResponse10;
import ar.com.ospim.webservice.prevencion.MembershipServiceStub.ValidationDetail;
import ar.com.ospim.webservice.prevencion.MembershipServiceStub.ValidationFault;
import ar.com.ospim.webservice.prevencion.MembershipServiceStub.ValidationFault11;
import ar.com.ospim.webservice.service.AfiliadoOpe;


public class TestFuncionBajaGrupoFliar extends TestFuncionWSPrevencion {

	/**
	 * @param args
	 */
	
	public static void main(String[] args) {
		
		
		try {
			Calendar vigencia = Calendar.getInstance();
			vigencia.add(Calendar.DATE, 1);
			
			Calendar bajaFecha = Calendar.getInstance();
			bajaFecha.add(Calendar.MONTH, 1);
			bajaFecha.set(Calendar.DAY_OF_MONTH, Calendar.getInstance().getMaximum(Calendar.DAY_OF_MONTH));

			MembershipServiceStub stub = new MembershipServiceStub("https://integration-pre-ws.gruposancorseguros.com/PrevencionSaludValidatorPublic/Membership/MembershipService.svc");
			
			DeleteFamilyGroup deleteFamilyGroup0 = new DeleteFamilyGroup();
			DeleteFamilyGroupResponse10 response = null;
			
			AfiliadoOpe afiliado = new AfiliadoOpe();
			afiliado.setCuil_titular("27242923400");
//			afiliado.setPlanPrevencion("A3");
			afiliado.setPlanPrevencion("A1");
			afiliado.setIdTransaccion(1000);
//			afiliado.setRazonSoc("ROYAL CANIN ARGENTINA SA");
			afiliado.setRazonSoc("PRUEBA SA");
			afiliado.setVigen_fecha(vigencia.getTime());
			afiliado.setId_motivo_baja(14);
			afiliado.setBaja_fecha(bajaFecha.getTime());
			
			DeleteFamilyGroupRequest cabecera = new DeleteFamilyGroupRequest();
			cabecera.setAccountId(getAccountID(afiliado));
			cabecera.setDeleteReasonId(afiliado.getId_motivo_baja());  
			cabecera.setHolderCuil(afiliado.getCuil_titular());
 			cabecera.setNullDate(getVigenciaBajaCorrespondiente(afiliado.getBaja_fecha()));
			cabecera.setTransactionId(afiliado.getIdTransaccion());
			
			deleteFamilyGroup0.setRequest(cabecera);
			
			response = stub.deleteFamilyGroup(deleteFamilyGroup0);
			
			
			System.out.println("Respuesta: " + response.getDeleteFamilyGroupResult().getTransactionId());
			
		} catch (AxisFault e) {
			System.err.println("1 " + e);
		} catch (RemoteException e) {
			System.err.println("2 " + e);
		} catch (IMembershipService_DeleteFamilyGroup_ValidationFault_FaultMessage e) {
			
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

}
