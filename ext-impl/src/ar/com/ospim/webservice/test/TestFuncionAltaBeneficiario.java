package ar.com.ospim.webservice.test;

import java.rmi.RemoteException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.axis2.AxisFault;

import com.liferay.portal.SystemException;

import ar.com.ospim.afiliados.NoSuchAfiliadoEntryException;
import ar.com.ospim.afiliados.beans.Afiliado;
import ar.com.ospim.util.ConnectionHelper;
import ar.com.ospim.webservice.prevencion.IMembershipService_AddMember_ValidationFault_FaultMessage;
import ar.com.ospim.webservice.prevencion.MembershipServiceStub;
import ar.com.ospim.webservice.prevencion.MembershipServiceStub.AddMember;
import ar.com.ospim.webservice.prevencion.MembershipServiceStub.AddMemberRequest;
import ar.com.ospim.webservice.prevencion.MembershipServiceStub.AddMemberResponse9;
import ar.com.ospim.webservice.prevencion.MembershipServiceStub.ArrayOfValidationDetail;
import ar.com.ospim.webservice.prevencion.MembershipServiceStub.FamilyGroupMember;
import ar.com.ospim.webservice.prevencion.MembershipServiceStub.ValidationDetail;
import ar.com.ospim.webservice.prevencion.MembershipServiceStub.ValidationFault;
import ar.com.ospim.webservice.prevencion.MembershipServiceStub.ValidationFault11;
import ar.com.ospim.webservice.service.AfiliadoOpe;


public class TestFuncionAltaBeneficiario extends TestFuncionWSPrevencion {

	/**
	 * @param args
	 */
	
	public static void main(String[] args) {
		
		
		try {
			Calendar fechaNacim = Calendar.getInstance();
			fechaNacim.set(Calendar.DATE, 15);
			fechaNacim.set(Calendar.MONTH, 9);
			fechaNacim.set(Calendar.YEAR, 1987);
			
			Calendar vigencia = Calendar.getInstance();
			vigencia.set(Calendar.DATE, 1);
			vigencia.set(Calendar.MONTH, 2);
			vigencia.set(Calendar.YEAR, 2018);

			MembershipServiceStub stub = new MembershipServiceStub("https://integration-pre-ws.gruposancorseguros.com/PrevencionSaludValidatorPublic/Membership/MembershipService.svc");
			
			AfiliadoOpe afiliado = new AfiliadoOpe();
			afiliado.setApellido("prueba apellido");
			afiliado.setNombre("prueba nombre");
			afiliado.setNaci_fecha(fechaNacim.getTime());
			afiliado.setNacionalidad(12);
			afiliado.setCuil_titular("20345187422");
			afiliado.setCuil("20334920160");
			afiliado.setDocu_numero("33492016");
			afiliado.setDocumento_tipo("DU");
			afiliado.setSexo("M");
			afiliado.setInte(2);
			afiliado.setPlanPrevencion("A3");
//			afiliado.setPlanPrevencion("A1");
			afiliado.setIdTransaccion(1000);
//			afiliado.setRazonSoc("PRUEBA SA");
			afiliado.setRazonSoc("ROYAL CANIN ARGENTINA SA");
			afiliado.setVigen_fecha(vigencia.getTime());
			
			AddMemberResponse9 response = null;
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
			
			System.out.println("Enviando x WS al Familiar: " + afiliado.getCuil_titular() + "/" + afiliado.getInte() 
				+ " Transaccion N° " + afiliado.getIdTransaccion());

			
			System.out.println("Respuesta: " + response.getAddMemberResult().getTransactionId());
			
		} catch (AxisFault e) {
			System.err.println("1 " + e);
		} catch (RemoteException e) {
			System.err.println("2 " + e);
		} catch (IMembershipService_AddMember_ValidationFault_FaultMessage e) {
			
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
