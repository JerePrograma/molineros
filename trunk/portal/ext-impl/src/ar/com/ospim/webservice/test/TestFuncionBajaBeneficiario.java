package ar.com.ospim.webservice.test;

import java.rmi.RemoteException;
import java.util.Calendar;

import org.apache.axis2.AxisFault;

import ar.com.ospim.webservice.prevencion.IMembershipService_DeleteMember_ValidationFault_FaultMessage;
import ar.com.ospim.webservice.prevencion.MembershipServiceStub;
import ar.com.ospim.webservice.prevencion.MembershipServiceStub.ArrayOfValidationDetail;
import ar.com.ospim.webservice.prevencion.MembershipServiceStub.DeleteMember;
import ar.com.ospim.webservice.prevencion.MembershipServiceStub.DeleteMemberRequest;
import ar.com.ospim.webservice.prevencion.MembershipServiceStub.DeleteMemberResponse8;
import ar.com.ospim.webservice.prevencion.MembershipServiceStub.ValidationDetail;
import ar.com.ospim.webservice.prevencion.MembershipServiceStub.ValidationFault;
import ar.com.ospim.webservice.prevencion.MembershipServiceStub.ValidationFault11;
import ar.com.ospim.webservice.service.AfiliadoOpe;


public class TestFuncionBajaBeneficiario extends TestFuncionWSPrevencion {

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

			MembershipServiceStub stub = new MembershipServiceStub("https://integration-pre-ws.gruposancorseguros.com/PrevencionSaludValidatorPublicCli/Membership/MembershipService.svc");
			
			DeleteMemberResponse8 response = null;
			
			AfiliadoOpe afiliado = new AfiliadoOpe();
			afiliado.setCuil_titular("20280248259");
			afiliado.setCuil("27-21011804-2");
			afiliado.setPlanPrevencion("A3");
			afiliado.setIdTransaccion(1000);
			afiliado.setRazonSoc("ROYAL CANIN ARGENTINA SA");
//			afiliado.setRazonSoc("LA EMPRESITA");
			afiliado.setVigen_fecha(vigencia.getTime());
			afiliado.setId_motivo_baja(14);
			afiliado.setBaja_fecha(bajaFecha.getTime());
			
			String cuilAux[] = afiliado.getCuil().split("-");
			String cuilSinGuiones = cuilAux[0]+cuilAux[1]+cuilAux[2];
			
			DeleteMember deleteMember0 = new DeleteMember();
			DeleteMemberRequest cabecera = new DeleteMemberRequest();
			deleteMember0.setRequest(cabecera);
			
			cabecera.setAccountId(getAccountID(afiliado));
			cabecera.setDeleteReasonId(afiliado.getId_motivo_baja());     
			cabecera.setHolderCuil(afiliado.getCuil_titular());
			cabecera.setNullDate(getVigenciaBajaCorrespondiente(afiliado.getBaja_fecha()));
			cabecera.setMemberCuil(Long.parseLong(cuilSinGuiones));
			cabecera.setTransactionId(afiliado.getIdTransaccion());
			
			deleteMember0.setRequest(cabecera);
			
			response = stub.deleteMember(deleteMember0); 
			
			
			System.out.println("Respuesta: " + response.getDeleteMemberResult().getTransactionId());
			
		} catch (AxisFault e) {
			System.err.println("1 " + e);
		} catch (RemoteException e) {
			System.err.println("2 " + e);
		} catch (IMembershipService_DeleteMember_ValidationFault_FaultMessage e) {
			
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
