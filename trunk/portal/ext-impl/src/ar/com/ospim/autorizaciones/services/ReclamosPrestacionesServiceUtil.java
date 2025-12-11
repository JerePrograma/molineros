package ar.com.ospim.autorizaciones.services;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.service.UserServiceUtil;
import com.liferay.portal.service.persistence.UserUtil;

import ar.com.ospim.afiliados.beans.Afiliado;
import ar.com.ospim.afiliados.services.SeccionalServiceUtil;
import ar.com.ospim.automatico.Thread.EnviaEmailsThread;
import ar.com.ospim.autorizaciones.action.BusquedaReclamosPrestacionalesFiltro;
import ar.com.ospim.autorizaciones.beans.AfiCuentaBancaria;
import ar.com.ospim.autorizaciones.beans.EstadisticaGastoMedico;
import ar.com.ospim.autorizaciones.beans.EstadisticaGastoMedicoDetalle;
import ar.com.ospim.autorizaciones.beans.ItemReclamoPrestacionalesTotal;
import ar.com.ospim.autorizaciones.beans.MovimientoReclamoHistorico;
import ar.com.ospim.autorizaciones.beans.PrestacionesReclamo;
import ar.com.ospim.autorizaciones.beans.ReclamoPrestacional;
import ar.com.ospim.autorizaciones.beans.ReclamoPrestacional.ESTADOSEVALUACIONRECLAMO;
import ar.com.ospim.autorizaciones.beans.ReclamoPrestacionalCuenta;
import ar.com.ospim.autorizaciones.beans.ReporteIntegracionReclamo;
import ar.com.ospim.autorizaciones.beans.ReportePreCargaReclamo;
import ar.com.ospim.autorizaciones.exceptions.ImposibleBorrarReclamoPrestacionalException;
import ar.com.ospim.crm.beans.CategoriaContacto;
import ar.com.ospim.crm.beans.ContactoCRM;
import ar.com.ospim.crm.beans.MotivoContacto;
import ar.com.ospim.crm.beans.TipoContacto;
import ar.com.ospim.crm.services.CrmServiceUtil;
import ar.com.ospim.global.beans.ContactoElectronico;
import ar.com.ospim.global.services.ProcesosCorreoServiceUtil;


public class ReclamosPrestacionesServiceUtil {		

	private static Log _log = LogFactoryUtil
			.getLog(ReclamoPrestacionServiceImpl.class);

	private static ReclamoPrestacionServiceImpl  instance = null;

	public static ReclamoPrestacionServiceImpl  getInstance() {
		if (null == instance) {
			instance = new ReclamoPrestacionServiceImpl ();
		}
		return instance;
	}
	
	public static List<ItemReclamoPrestacionalesTotal> buscarReclamosPrestacionalTotales(BusquedaReclamosPrestacionalesFiltro filtro ) throws Exception {
		return getInstance().buscarReclamosPrestacionalesTotales(filtro);
	}
	
	public static int insertar(ReclamoPrestacional   reclamo , User user) throws Exception{
		int idReclamo = 0;			
		idReclamo = getInstance().insertar(reclamo , user);
		if (idReclamo>0   && reclamo.getEstado()==3 ){			
			Afiliado afil = reclamo.getAfiliado();			
			reclamo= getReclamoPrestacional(idReclamo );
			grabarContactoCRM(reclamo,user );
			reclamo.setAfiliado(afil);
			enviarMailsCierreReclamoPrestacional(reclamo,user );
		}
		return idReclamo ;
	}
	
	
	public static void update(ReclamoPrestacional   reclamo , User user) throws Exception{
		//getInstance().actualizar(reclamo, user );
			if ( getInstance().actualizar(reclamo, user )  && reclamo.getEstado()==3) {
				grabarContactoCRM(reclamo,user );
				Afiliado afil = reclamo.getAfiliado();			
				reclamo= getReclamoPrestacional(reclamo.getId_reclamo() );			
				reclamo.setAfiliado(afil);
				enviarMailsCierreReclamoPrestacional(reclamo,user );
			}				
		 
		}
	
	
	public static void setDatosOpReclamoPrestacional (ReclamoPrestacional   reclamo ) throws Exception {
		 getInstance().setDatosOpReclamoPrestacional (reclamo );
	}
	
	public static void altaModiCuenta (ReclamoPrestacional   reclamo, User user ) throws Exception {
		 getInstance().altaModiCuenta (reclamo ,  user);
	}
	
	public static ReclamoPrestacionalCuenta getReclamoPrestacionalCuenta (int idReclamo) throws Exception {
		return getInstance().getReclamoPrestacionalCuenta (idReclamo);
	}
	
	public static ReclamoPrestacional getReclamoPrestacional (int id) throws Exception {
		
		return getInstance().getReclamoPrestacional(id) ;
	}
	
	public static List<PrestacionesReclamo >  getPrestacionesAsociadas(int casoasociado ) throws Exception {
		return getInstance().retornaPrestacionesAsociadasxCasoVinculado(casoasociado );
	}
	 
	
	public static void borrar(int id, User user)
			throws ImposibleBorrarReclamoPrestacionalException, SystemException{
		    getInstance().borrar(id, user.getScreenName());
	}
	
	/*
	 Registra el contacto en CRM : tipo = 6  "CORREO SALIENTE" , descripcion =  "RECLAMO PRESTACIONAL NRO " NNNN
	 campo estado CERRADO   categoria = 2  "RECLAMO"  id motivo = 5 PRESTACIONES MÉDICAS
	 */
	public static void grabarContactoCRM(ReclamoPrestacional reclamo , User user ) throws SystemException {
		ContactoCRM contactoCrm = new ContactoCRM ();
		try {
			contactoCrm.setAfiliado(new Afiliado (reclamo.getCuit_titular(),reclamo.getInte()));		
			contactoCrm.setAltaSector(UserUtil.getUserGroups(user.getUserId()).get(0).getGroup().getDescription() );		
			contactoCrm.setTipo(new TipoContacto(6, "CORREO SALIENTE"));
			contactoCrm.setCategoria(new CategoriaContacto(2,"RECLAMO") );
			contactoCrm.setMotivo(new MotivoContacto (5,"PRESTACIONES MÉDICAS"));
			contactoCrm.setDescripcion("RECLAMO PRESTACIONAL NRO::" + String.valueOf(reclamo.getId_reclamo())   );
			contactoCrm.setIdCrmRelacionado(0);
			contactoCrm.setComentarioCierre("Cierre automático por carga de reclamo prestacional");
			contactoCrm.setEstado(ContactoCRM.ESTADOS.CERRADO);
			CrmServiceUtil.insertaContacto(contactoCrm, user.getScreenName(),String.valueOf(UserUtil.getUserGroups(user.getUserId()).get(0).getUserGroupId()),null );	
		}catch (Exception e) {
			_log.error("Usuario: "+user.getScreenName());
			_log.error(e);	
		}		
	}

	private static void  enviarMailsCierreReclamoPrestacional (ReclamoPrestacional reclamo ,User user ){
		
		String subjectautorizados=" Reclamo Prestacional Nro:" + String.valueOf(reclamo.getId_reclamo()) + ", Afiliado:" +  reclamo.getAfiliado().getApeNombre() + "(" + reclamo.getEstadoResolucionAutorizadaString()  + ")." ;
		String subjectSeccionalCAB =" Reclamo Prestacional Nro:" + String.valueOf(reclamo.getId_reclamo()) + ", Afiliado:" +  reclamo.getAfiliado().getApeNombre()  ;
		String bodyAutorizados = "Se ha cerrado el reclamo Nro " + String.valueOf(reclamo.getId_reclamo()) + " con resolucion " +  reclamo.getEstadoResolucionAutorizadaString()    +   "." +"\n\n\n";		
		String bodySeccionalCAB = "Prestación evaluada por Auditoría Médica. Contáctese al 0810-345-0208.";
		
		List<String>emails = new ArrayList<String>();
		List<String>emailsSeccionalCAB= new ArrayList<String>();
		List<ContactoElectronico> contactose; 
		
		try{
			int id = reclamo.getSeccional().getId();		
			contactose=SeccionalServiceUtil.buscarContactosSeccionalEmail(id );
			// carga destinatarios del mails de la seccional dominio UOMA 
			for (ContactoElectronico contac  : contactose) {
				if (contac.getContacto().indexOf("uoma")>1 ){ //uoma
					emails.add(contac.getContacto() );	
					emailsSeccionalCAB.add(contac.getContacto() );					
				} 
			}				
			// añade mails si es RECHAZADO
			if (reclamo.getEstadoResolucionAutorizada().equals(ESTADOSEVALUACIONRECLAMO.RECHAZADA ) ){		  
				List<String> destinatarios = ProcesosCorreoServiceUtil.getListaCorreoDestinatariosInformadosPorProceso(ProcesosCorreoServiceUtil.CIERRE_RECLAMO_PRESTACIONAL_RECHAZO );
				for (String  desti : destinatarios ) {			
					emails.add(desti );			
					emailsSeccionalCAB.add(desti );
				}
			}
			// destinatarios extras al envio cuando se cierra rechazada o no 
			List<String> destinatariosExtra= ProcesosCorreoServiceUtil.getListaCorreoDestinatariosInformadosPorProceso(ProcesosCorreoServiceUtil.CIERRE_RECLAMO_PRESTACIONAL );
			for (String  desti : destinatariosExtra) {			
				emails.add(desti );
				emailsSeccionalCAB.add(desti );
			}
			
		}catch(NumberFormatException e){		
			_log.error(e);
		}catch (Exception e) {
			_log.error(e);
		}
		bodyAutorizados ="";
		
		if ( reclamo.getEstado()==3 &&  reclamo.getEstadoResolucionAutorizada().equals(ESTADOSEVALUACIONRECLAMO.AUTORIZADA) ) {
					bodyAutorizados = bodyAutorizados +"Afiliado  " +  reclamo.getAfiliado().getApellidoNombre()  +"\n" +  
			        "DNI:  " +	reclamo.getAfiliado().getDocu_numero() +"\n" +
					"CUIL TITULAR: " +reclamo.getAfiliado().getCuil_titular()   + "\n" +
					"CUIL: " +reclamo.getAfiliado().getCuil() + "\n" +					
					"ID Reclamo: " +reclamo.getId_reclamo()  + "\n"
					+ "\n"	+ "\n Prestación/es"+ "\n";
					for (PrestacionesReclamo  pres : reclamo.getPrestaciones()) {
						if ( pres.getEstadoRechazoAprobado()==2) { //autorizado
							bodyAutorizados = bodyAutorizados + pres.getDescripcion() + "\n";	
						} 
					}
					bodyAutorizados = bodyAutorizados + "\n\n";	
					bodyAutorizados = bodyAutorizados + "Resolución:"+  reclamo.getEstadoResolucionAutorizadaString() ;		
		}
		List<String> lm =new ArrayList<String>();
		if(!user.getScreenName().equalsIgnoreCase("liquidaciones")){
			if (reclamo.getEstadoResolucionAutorizada().equals(ESTADOSEVALUACIONRECLAMO.AUTORIZADA)) {		
				for(String s:emails) {
				  lm.clear();
				  lm.add(s);
				  EnviaEmailsThread.enviarMailDesatendido(subjectautorizados, bodyAutorizados ,  lm, 0); // con detalles
				}  
			}else{	
				for(String s:emailsSeccionalCAB) {
					lm.clear();
					lm.add(s);
				    EnviaEmailsThread.enviarMailDesatendido(subjectSeccionalCAB , bodySeccionalCAB ,  lm, 0);	// sin detalles
				}    
			}
		}		
	}


	public static int insertarDesdePreautorizacion(ReclamoPrestacional   reclamo , String screenName) throws Exception{
		int idReclamo = 0;	
		
		User user = UserServiceUtil.getUserByScreenName(10112, screenName);
		
		idReclamo = getInstance().insertar(reclamo , user);
		if (idReclamo>0   && reclamo.getEstado()==3 ){			
			Afiliado afil = reclamo.getAfiliado();			
			reclamo= getReclamoPrestacional(idReclamo );
//			grabarContactoCRM(reclamo,user );
			reclamo.setAfiliado(afil);
//			enviarMailsCierreReclamoPrestacional(reclamo,user );
		}
		return idReclamo ;
	}
	
	public static void updateDesdePreautorizacion(ReclamoPrestacional   reclamo , String screenName) throws Exception{
		    User user = UserServiceUtil.getUserByScreenName(10112, screenName);

			if ( getInstance().actualizar(reclamo, user )  && reclamo.getEstado()==3) {
/*				
				grabarContactoCRM(reclamo,user );
				Afiliado afil = reclamo.getAfiliado();			
				reclamo= getReclamoPrestacional(reclamo.getId_reclamo() );			
				reclamo.setAfiliado(afil);
				enviarMailsCierreReclamoPrestacional(reclamo,user );
*/				
			}				
		 
	}
	
	
	public static Integer getLoteVigenteReclamoPrestacional() throws Exception{
		Integer idReclamo = 0;	
		idReclamo=getInstance().getLoteVigenteReclamoPrestacional();
		return idReclamo;
	}
	
	public static void cerrarLote( String screenName) throws Exception{
//	    User user = UserServiceUtil.getUserByScreenName(10112, screenName);

		getInstance().cerrarLote(screenName);
	 
    }
	
	
	public static void cambiarEstado(int nroReclamo, int nuevoEstado, String observacion, String screenName) throws Exception{


		getInstance().cambiarEstado(nroReclamo, nuevoEstado, observacion, screenName);
	 
    }
	
	public static void reabrirReclamo(int nroReclamo,  String screenName) throws Exception{


		getInstance().reabrirReclamo(nroReclamo, screenName);
	 
    }

	public static String validarExisteComprobante(PrestacionesReclamo reclamo) {
		return getInstance().validarExisteComprobante(reclamo); 
	 
    }
	
	
	
	public static void  grabarFechaEnvioSeccional(ReclamoPrestacional reclamoPrestacional) {
		 getInstance().grabarFechaEnvioSeccional(reclamoPrestacional); 
	 
    }
	
	public static List<ReportePreCargaReclamo> reclamosPrestacionalPreCarga() throws Exception {
		return getInstance().reclamosPrestacionalPreCarga();
	}
	
	public static List<ReporteIntegracionReclamo> reclamosPrestaEstadisticaIntegracion() throws Exception {
		return getInstance().reclamosPrestaEstadisticaIntegracion();
	}
	
	
	public static List<EstadisticaGastoMedico> getEstadisticaGastoMedico(Date fini,Date ffin ) throws Exception {
		return getInstance().getEstadisticaGastoMedico(fini, ffin) ;
	}
	
	public static List<EstadisticaGastoMedicoDetalle> getEstadisticaGastoMedicoDetalle(Date fini,Date ffin ) throws Exception {
		return getInstance().getEstadisticaGastoMedicoDetalle(fini, ffin) ;
	}

	
	public static List<AfiCuentaBancaria> traerCuentaBancariaAsociadas(String cuilTitular)
			throws SystemException, NumberFormatException, ParseException {
		return getInstance().traerCuentaBancariaAsociadas(cuilTitular);
	}
	
	
	public static AfiCuentaBancaria traerCuentaBancariaAsociadasPorId(int idCuenta)
			throws SystemException, NumberFormatException, ParseException {
		return getInstance().traerCuentaBancariaAsociadasPorId(idCuenta);
	}
	
	
	
	public static void  deleteImagenCuenta(String  name, String screenName) throws SystemException {
		 getInstance().deleteImagenCuenta(name, screenName); 
    }
	
	public static void  updateNombreImagen(ReclamoPrestacional reclamoPrestacional, String  name,String description , String screenName) throws SystemException {
		 getInstance().updateNombreImagen(reclamoPrestacional,   name, description ,  screenName); 
    }
	

	public static String traerNumerosReclamos(int op, int idFormaPago) throws SystemException {
		return getInstance().traerNumerosReclamos(op, idFormaPago); 
	 
    }
   
	public static List<ReclamoPrestacionalCuenta> getCuentasPorCuil(String cuil) throws Exception {
	    return getInstance().getCuentasPorCuil(cuil);
	}
	
	public static List<MovimientoReclamoHistorico> buscarHistoricoReclamo(
	        int idReclamo) throws Exception {
	    return getInstance().buscaHistoricoReclamo(idReclamo);
	}

}
