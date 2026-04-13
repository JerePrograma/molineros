package ar.com.ospim.webservice;

import java.rmi.RemoteException;
import java.util.Iterator;
import java.util.List;

import org.apache.axis.message.MessageElement;
import org.w3c.dom.Node;

import junit.framework.TestCase;
import ar.com.ospim.afiliados.beans.Afiliado;
import ar.com.ospim.webservice.omint.BajaBeneficiarioResponseBajaBeneficiarioResult;
import ar.com.ospim.webservice.omint.SociosSoapProxy;
import ar.com.ospim.webservice.service.AfiliadoServiceUtil;

public class OmintWSClientTest extends TestCase {
	
	private final Integer company = 2;
	private final String country = "AR";
	private final String language = "ES";

	
	public void testGetSession(){
		
		OmintWSClient wsCliente = new OmintWSClient();
		
		String result = wsCliente.getSession();
		
		System.out.println("Session: " + result);
		
		assertNotNull(result);

	}
	
	public void testGetAfiliadosxAltaGrupoFliar(){
		
		AfiliadoServiceUtil service = new AfiliadoServiceUtil();
		
		List<Afiliado> altaGrupoFliar = service.getAfiliadosxAltaGrupoFliar();
		
		assertTrue(altaGrupoFliar.size()>0);
		
	}
	
	public void testGetAfiliadosxAltaBeneficiario(){
		
		AfiliadoServiceUtil service = new AfiliadoServiceUtil();
		
		List<Afiliado> beneficiarios = service.getAfiliadosxAltaBeneficiario();
		
		assertTrue(beneficiarios.size()>0);
		
	}
	
	public void testGetAfiliadosxModificaBeneficiario(){
	
		AfiliadoServiceUtil service = new AfiliadoServiceUtil();
	
		List<Afiliado> beneficiarios = service.getAfiliadosxModificaBeneficiario();
	
		assertTrue(beneficiarios.size()>0);
	
	}
	
	public void testGetAfiliadosxBajaBeneficiario(){
	
		AfiliadoServiceUtil service = new AfiliadoServiceUtil();
	
		List<Afiliado> beneficiarios = service.getAfiliadosxBajaGrupoFliar();
	
		assertTrue(beneficiarios.size()>0);
	
	}
	
	public void testGetAfiliadosxBajaGrupoFliar(){
		
		AfiliadoServiceUtil service = new AfiliadoServiceUtil();
	
		List<Afiliado> beneficiarios = service.getAfiliadosxBajaGrupoFliar();
	
		assertTrue(beneficiarios.size()>0);
	
	}
	
	public void testCambioPlanGrupoFliar(){
		
		AfiliadoServiceUtil service = new AfiliadoServiceUtil();
	
		List<Afiliado> beneficiarios = service.getAfiliadosxCambioPlanGrupoFliar();
	
		assertTrue(beneficiarios.size()>0);
	
	}
	
	public void testEnviarAltaBeneficiario(){
		
		OmintWSClient wsCliente = new OmintWSClient();
		
		AfiliadoServiceUtil service = new AfiliadoServiceUtil();
		List<Afiliado> beneficiarios = service.getAfiliadosxAltaBeneficiario();
		
		System.out.println("Novedad Altas: " + beneficiarios.size());
		
		for (Iterator<Afiliado> iterator = beneficiarios.iterator(); iterator.hasNext();) {
			
			Afiliado afiliado = iterator.next();
			
			String[] result = wsCliente.enviarAltaBeneficiario(afiliado);
			
			assertNotNull(result);
			assertTrue(Integer.parseInt(result[0]) > 0 );
		}
	}

	public void testEnviarAltaGrupoFamiliar(){
		
		OmintWSClient wsCliente = new OmintWSClient();
		
		AfiliadoServiceUtil service = new AfiliadoServiceUtil();
		List<Afiliado> gruposFliares = service.getAfiliadosxAltaGrupoFliar();
		
		System.out.println("Novedad Altas: " + gruposFliares.size());
		
		String[] result = wsCliente.enviarAltaGrupoFamiliar(gruposFliares);
			
		assertNotNull(result);
		assertTrue(Integer.parseInt(result[0]) > 0 );
	}
	
	public void testEnviarModificaBeneficiario(){
	
		OmintWSClient wsCliente = new OmintWSClient();
		
		AfiliadoServiceUtil service = new AfiliadoServiceUtil();
		List<Afiliado> beneficiarios = service.getAfiliadosxModificaBeneficiario();
		
		System.out.println("Novedad Modif: " + beneficiarios.size());
		
		for (Iterator<Afiliado> iterator = beneficiarios.iterator(); iterator.hasNext();) {
			
			Afiliado afiliado = iterator.next();
			
			String[] result = wsCliente.enviarModificacionBeneficiario(afiliado);
			
			assertNotNull(result);
			assertTrue(Integer.parseInt(result[0]) > 0 );
		}
    }
	
	public void testBajaBeneficiario(){
		
		OmintWSClient wsCliente = new OmintWSClient();
		
		AfiliadoServiceUtil service = new AfiliadoServiceUtil();
		List<Afiliado> beneficiarios = service.getAfiliadosxBajaBeneficiario();
		
		System.out.println("Novedad Baja Beneficiario: " + beneficiarios.size());
		
		for (Iterator<Afiliado> iterator = beneficiarios.iterator(); iterator.hasNext();) {
			
			Afiliado afiliado = iterator.next();
			
			String[] result = wsCliente.enviarBajaBeneficiario(afiliado);
			
			assertNotNull(result);
			assertTrue(Integer.parseInt(result[0]) > 0 );
		}
		
	}
	
	public void testEnviarBajaGrupoFamiliar(){
		
		OmintWSClient wsCliente = new OmintWSClient();
		
		AfiliadoServiceUtil service = new AfiliadoServiceUtil();
		List<Afiliado> gruposFliares = service.getAfiliadosxBajaGrupoFliar();
		
		System.out.println("Novedad Altas: " + gruposFliares.size());
		
		String[] result = wsCliente.enviarBajaGrupoFamiliar(gruposFliares);
			
		assertNotNull(result);
		assertTrue(Integer.parseInt(result[0]) > 0 );
	}
	
	public void testEnviarCambioGrupoFamiliar(){
	
		OmintWSClient wsCliente = new OmintWSClient();
		
		AfiliadoServiceUtil service = new AfiliadoServiceUtil();
		List<Afiliado> gruposFliares = service.getAfiliadosxCambioPlanGrupoFliar();
		
		System.out.println("Novedad Altas: " + gruposFliares.size());
		
//		for (Iterator<Afiliado> iterator = gruposFliares.iterator(); iterator.hasNext();) {
//			Afiliado afi = iterator.next();
			
			String[] result = wsCliente.enviarCambioPlanGrupoFliar(gruposFliares);
		
			assertNotNull(result);
			assertTrue(Integer.parseInt(result[0]) > 0 );
			
//		}

	}
} 

