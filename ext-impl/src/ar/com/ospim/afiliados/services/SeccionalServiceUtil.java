package ar.com.ospim.afiliados.services;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.liferay.portal.SystemException;

import ar.com.empresas.beans.Contacto;
import ar.com.ospim.afiliados.beans.SeccionalExcel;
import ar.com.ospim.global.beans.ClaseBase;
import ar.com.ospim.global.beans.ContactoElectronico;
import ar.com.ospim.global.beans.Delegacion;
import ar.com.ospim.global.beans.Empresa;
import ar.com.ospim.global.beans.Seccional;
import ar.com.ospim.global.beans.Telefono;
import ar.com.ospim.seccional.beans.GestionSeccional;
import ar.com.ospim.util.ConnectionHelper;
import ar.com.uoma.beans.CentroCosto;

public class SeccionalServiceUtil {

	private static SeccionalServiceImpl instance = null;

	public static SeccionalServiceImpl getInstance() {
		if (null == instance) {
			instance = new SeccionalServiceImpl();
		}
		return instance;
	}

	public static List<Seccional> buscarSeccionales(Integer codigo, String descripcion,Integer provincia) throws Exception {
		return getInstance().buscarSeccionales(codigo,descripcion, provincia);
	}
	
	
	public static Seccional buscarSeccionalById(Integer codigo) throws Exception {
		
		Seccional seccional = new Seccional();
		
		List<Seccional> l = getInstance().buscarSeccionales(codigo,null, null);
		if(l!=null && codigo!=null && codigo>0){
			seccional = l.get(0);
			List<Contacto> lcontac = new ArrayList<Contacto>();
			seccional.setContactos(lcontac);
			List<Delegacion>delegaciones = getInstance().buscarDelegacionesSeccional(seccional.getId());
			seccional.setDelegaciones(delegaciones);
			List<ContactoElectronico>contactosElectronicos = getInstance().buscarContactosSeccional(seccional.getId());
			for(ContactoElectronico e: contactosElectronicos){
				Contacto contacto = new Contacto();
				contacto.setContacto(e);
				seccional.getContactos().add(contacto);
			}
			
			List<Telefono>telefonos = getInstance().buscarTelefonosSeccional(seccional.getId());
			for(Telefono e: telefonos){
				Contacto contacto = new Contacto();
				contacto.setTelefono(e);
				seccional.getContactos().add(contacto);
			}
			
			List<Contacto>contactos = getInstance().buscarContactosPersonalesSeccional(seccional.getId());
			seccional.setPlantel(contactos);
			
		}
		return seccional;
		
	}
	
	

	 public static long update(Seccional seccional, String screenName) throws Exception {
			long idSeccional = seccional.getId(); 
			Integer idDomicilio=0;
			Connection connection = null;		
			
			try {			
				connection = ConnectionHelper.getConnection();
				connection.setAutoCommit(false);
				Seccional seccionalDB =buscarSeccionalById(seccional.getId());
				
//-----------------
//Contactos				
				if(seccional.getContactos() !=null){
					//Analiza Altas - apareo con registros existentes en BD			
					for(Contacto td:seccional.getContactos() ){
						Boolean existe=false;
						for(Contacto tdDB:seccionalDB.getContactos()){
							if( td.getIdContacto()==tdDB.getIdContacto()){
								existe=true;
								break;
							}
						}
						if(!existe){
						   getInstance().addContacto(seccional.getId(), td, screenName, connection);
				    	}else{
				    		if(td.getBajaFecha()==null) {
				    	     getInstance().updateContacto(seccional.getId(), td, screenName, connection);
				    		}else {
				    		 getInstance().deleteContacto(seccional.getId(),td, screenName, connection) ;	
				    		}
				    	}
					}
					
					//Analiza Bajas - apareo con registros existentes en BD
					for(Contacto tdDB:seccionalDB.getContactos() ){
						Boolean existe=false;
						for(Contacto td:seccional.getContactos()){
							if(td.getIdContacto()==tdDB.getIdContacto()){
								existe=true;
								break;
							}
						}
						if(!existe){
				    	   getInstance().deleteContacto(seccional.getId(),tdDB, screenName, connection) ;
			    	    }
					}
			    }else{
				   if(seccionalDB.getContactos() != null && seccionalDB.getContactos().size()>0){
					   for(Contacto tdDB:seccionalDB.getContactos()){
						   getInstance().deleteContacto(seccional.getId(),tdDB, screenName, connection) ;
					   }
				   }
			   }				
//-----------------				
				

//-----------------
//Delegaciones				
			 if(seccional.getDelegaciones() !=null){
				 //Analiza Altas - apareo con registros existentes en BD			
				 for(Delegacion td:seccional.getDelegaciones() ){
					Boolean existe=false;
				    for(Delegacion tdDB:seccionalDB.getDelegaciones()){
						if( td.getId()==tdDB.getId()){
							existe=true;
							break;
						}
					}
					if(!existe){
					   getInstance().addDelegacion(seccional.getId(), td, screenName, connection);
					   seccional.setId_delegacion_sss(td.getId());
					}else{
//					   getInstance().updateDelegacion(seccional.getId(), td, screenName, connection);	
					}
				 }
									
				 //Analiza Bajas - apareo con registros existentes en BD
				 for(Delegacion tdDB:seccionalDB.getDelegaciones() ){
					Boolean existe=false;
					for(Delegacion td:seccional.getDelegaciones()){
						if(td.getId()==tdDB.getId()){
							existe=true;
							break;
						}
					}
					if(!existe){
					   getInstance().deleteDelegacion(seccional.getId(),tdDB, screenName, connection) ;
					}
				}
			 }else{
				if(seccionalDB.getDelegaciones() != null && seccionalDB.getDelegaciones().size()>0){
					for(Delegacion tdDB:seccionalDB.getDelegaciones()){
						getInstance().deleteDelegacion(seccional.getId(),tdDB, screenName, connection) ;
					}
				}
			 }				
//-----------------	
			 
			 
//-----------------
//Contactos Personal				
			if(seccional.getPlantel() !=null){
				//Analiza Altas - apareo con registros existentes en BD			
				for(Contacto td:seccional.getPlantel() ){
					Boolean existe=false;
					for(Contacto tdDB:seccionalDB.getPlantel()){
						if( td.getIdContacto()==tdDB.getIdContacto()){
							existe=true;
							break;
						}
					}
						
					if(!existe){
					   getInstance().addContactoPersonal(seccional.getId(), td, screenName, connection);
				    }else{
				    	if(!Contacto.ESTADOS.BAJA.equals(td.getEstado())){
				    	  if("SI".equalsIgnoreCase(td.getProfesion())){	 
				           getInstance().updateContactoPersonal(seccional.getId(), td, screenName, connection);
				           td.setProfesion("");
				    	  } 
				    	}else{
				    	   getInstance().deleteContactoPersonal(seccional.getId(),td, screenName, connection) ;
				    	}
				    }
				}
								
				//Analiza Bajas - apareo con registros existentes en BD
				for(Contacto tdDB:seccionalDB.getPlantel() ){
					Boolean existe=false;
					for(Contacto td:seccional.getPlantel()){
						if(td.getIdContacto()==tdDB.getIdContacto()){
							existe=true;
							break;
						}
					}
					if(!existe){
				   	   getInstance().deleteContactoPersonal(seccional.getId(),tdDB, screenName, connection) ;
				    }
				}
			}else{
			   if(seccionalDB.getPlantel() != null && seccionalDB.getPlantel().size()>0){
				   for(Contacto tdDB:seccionalDB.getPlantel()){
					   getInstance().deleteContactoPersonal(seccional.getId(),tdDB, screenName, connection) ;
				   }
			   }
 			}				
//-----------------				

	
			 idSeccional=getInstance().update(seccional,screenName,connection);
				
			 if(seccional.getDomicilio()!=null && seccional.getDomicilio().getId_domicilio()>0 ){
				idDomicilio=getInstance().updateDomicilio(seccional,screenName,connection);
			 }else if(seccional.getDomicilio()!=null && seccional.getDomicilio().getId_domicilio()<0 ){
				idDomicilio=getInstance().addDomicilio(seccional,screenName,connection);
			 }
				
			 connection.commit();
		  } catch (Exception e) {
			  if(null!=connection){
				  connection.rollback();
				  throw e;
			  }			
		  } finally {
			  ConnectionHelper.cerrar(connection);
		  }    
		  return idSeccional;
		}

	    public static Integer proximoNumeroSeccional(Integer provincia, String tipo) throws Exception {
			return getInstance().proximoNroSeccional(provincia, tipo, null) ;
		}
	    
	    
	    public static long add(Seccional seccional, String screenName) throws Exception {
			long idSeccional = seccional.getId(); 
			Integer idDomicilio=0;
			Connection connection = null;		
			
			try {			
				connection = ConnectionHelper.getConnection();
				connection.setAutoCommit(false);
				
				 idDomicilio=getInstance().addDomicilio(seccional, screenName, connection);
				 
				 seccional.getDomicilio().setId_domicilio(idDomicilio);

//				 idSeccional=getInstance().add(seccional,screenName,connection);
				 
				 
//-----------------
//Contactos				
				if(seccional.getContactos() !=null){
					for(Contacto td:seccional.getContactos() ){
					   getInstance().addContacto(seccional.getId(), td, screenName, connection);
					}
					
			    }				
//-----------------	

				
//-----------------
//Contactos Personal				
				if(seccional.getPlantel() !=null){
					for(Contacto td:seccional.getPlantel() ){
 					   getInstance().addContactoPersonal(seccional.getId(), td, screenName, connection);
					}
									
			    }				
//-----------------				
				

//-----------------
//Delegaciones				
			 if(seccional.getDelegaciones() !=null){
				 //Analiza Altas - apareo con registros existentes en BD			
				 for(Delegacion td:seccional.getDelegaciones() ){
				   getInstance().addDelegacion(seccional.getId(), td, screenName, connection);
				 }
			 }			
//-----------------
			 
			 idSeccional=getInstance().add(seccional,screenName,connection);
	
			 connection.commit();
		  } catch (Exception e) {
			  
			  
			  if(null!=connection){
				  connection.rollback();
				  throw e;
			  }			
		  } finally {
			  ConnectionHelper.cerrar(connection);
		  }    
		  return idSeccional;
		}


		//Lista Seccionales Contactos  
		public static List<SeccionalExcel> getListaSeccionalesContactos(int provinciaSeleccionada ) throws SystemException
				{
			return getInstance().getListaSeccionalesContactos (provinciaSeleccionada );
		}

//		Lista Seccionales   
		public static List<SeccionalExcel> getListaSeccionales(int provinciaSeleccionada )
				throws SystemException {
			return getInstance().getListaSeccionales(provinciaSeleccionada );
		}

		
	    
	    public static boolean existeNumeroSeccional(Integer numero) throws Exception {
	    	return getInstance().existeNumeroSeccional(numero);			
		}

	    public static List<ClaseBase> traeCargosSeccional() throws Exception {
			return getInstance().traeCargosSeccional() ;
		}
	    
	    public static List<Contacto> buscarContactosPersonalesSeccional(Integer codigo, String descripcion) {
			try {
				return getInstance().buscarContactosPersonalesSeccional(codigo,descripcion);
			} catch (SystemException e) {
				return null;
			}
		}
	    
	    public static Contacto buscarContactoPersonalSeccionalByID(Integer codigo) {
	    	
			try {
				return getInstance().buscarContactoPersonalSeccionalByID(codigo);
			} catch (SystemException e) {
				return null;
			}
		}
	    
	    public static List<ContactoElectronico> buscarContactosSeccionalEmail(Integer codigo){
	    	
	    	try {
				return getInstance().buscarContactosSeccionalEmail(codigo);
			} catch (SystemException e) {
				return null;
			}
	    }
	    
	    public static Map<String,Integer> desgloseSeccional(int idSeccional){
	    	
	    	try {
				return getInstance().desgloseSeccional(idSeccional);
			} catch (SystemException e) {
				return null;
			}
	    }
	    
        public static List<Contacto> buscarAutoridadesSeccionalByID(Integer codigo) {
	    	
			try {
				return getInstance().buscarAutorizadesSeccionalByID(codigo);
			} catch (SystemException e) {
				return null;
			}
		}
        
        public static int insertarGestionSeccional(GestionSeccional gs, String screenName) throws SystemException {
			
        	try {
				return getInstance().insertarGestionSeccional(gs, screenName);
			} catch (SystemException e) {
				return 0;
			}

        }
        
    	public static List<GestionSeccional> buscarGestionesxSeccional(Integer idSeccional) throws Exception {
    		
    		try {
				return getInstance().buscarGestionesxSeccional(idSeccional);
			} catch (SystemException e) {
				return null;
			}
    		
    	}
    	
    	public static List<Empresa> buscarEmpresasSeccionalByID(Integer codigo) {
	    	
			try {
				return getInstance().buscarEmpresasSeccionalByID(codigo);
			} catch (SystemException e) {
				return null;
			}
		}
    	
        public static List<CentroCosto> buscarCentroCostoSeccionalByID(Integer codigo) {
	    	
			try {
				return getInstance().buscarCentroCostoSeccionalByID(codigo);
			} catch (SystemException e) {
				return null;
			}
		}
    	
    	public static String[] buscarCuentaContablexSeccional(Integer idSeccional) {
    		try {
				return getInstance().buscarCuentaContablexSeccional(idSeccional);
			} catch (SystemException e) {
				return null;
			}
    	}
    	
    	public static String buscarTarjetaRecargable(Integer idSeccional) {
    		try {
				return getInstance().buscarTarjetaRecargable(idSeccional);
			} catch (SystemException e) {
				return null;
			}
    	}
        
}