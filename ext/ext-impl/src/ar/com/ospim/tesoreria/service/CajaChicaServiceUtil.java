package ar.com.ospim.tesoreria.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ar.com.ospim.global.beans.Comprobante;
import ar.com.ospim.global.beans.Concepto;
import ar.com.ospim.global.beans.OrdenPago;
import ar.com.ospim.global.beans.OrdenPagoOspim;
import ar.com.ospim.global.beans.OrdenPagoUoma;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.tesoreria.beans.caja_chica.CajaChica;
import ar.com.ospim.tesoreria.beans.caja_chica.ComprobanteCajaChica;
import ar.com.ospim.tesoreria.beans.caja_chica.WorkflowDefinition;
import ar.com.ospim.util.ConnectionHelper;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.model.User;

public class CajaChicaServiceUtil {
	
	private static Log _log = LogFactoryUtil
			.getLog(CajaChicaServiceUtil.class);

	private static CajaChicaServiceImpl instance = null;

	public static CajaChicaServiceImpl getInstance() {
		if (null == instance) {
			instance = new CajaChicaServiceImpl();
		}
		return instance;
	}
	
	
	public static CajaChica get(int id,int entidad)
			throws SystemException, SQLException {
		
		Connection connection = null;
		CajaChica cajaChica= new CajaChica();
		try {
		  connection = ConnectionHelper.getConnection();
		  List<CajaChica> list = getInstance().list(null,0,0,entidad,id,connection);
		  cajaChica =list.get(0);
		  List<User>usuarios = getInstance().usuariosHabilidados(id, entidad,connection);
		  cajaChica.setUsuariosHabilitados(usuarios);
		
			connection.close();
		} catch (SQLException e) {
			
		}finally{
			if (connection != null) {
				 connection.close();
			 }
		}
		return cajaChica;
    }
	
	public static List<CajaChica> list(String descripcion,int concepto, int estado,int entidad)
				throws Exception{
		
		Connection connection = null;
		connection = ConnectionHelper.getConnection();
		List<CajaChica>cajas=new ArrayList<CajaChica>();
		try{
		  cajas =getInstance().list(descripcion,concepto,estado,entidad,connection);
		  for(CajaChica caja:cajas){
		   List<User>usuarios = getInstance().usuariosHabilidados(caja.getId(), entidad,connection);
		   caja.setUsuariosHabilitados(usuarios);
		  }
		}catch(Exception e){
			throw e;
		}finally{
			if (connection != null) {
				 connection.close();
			 }
		}
		return cajas;
	}
	
	public static long add(CajaChica cajaChica, String screenName,int entidad) throws Exception {

		long idCajaChica = 0; 
		Connection connection = null;
		try {			
			connection = ConnectionHelper.getConnection();
			connection.setAutoCommit(false);
		    idCajaChica=getInstance().add(cajaChica, screenName,entidad,connection);
			for(User d:cajaChica.getUsuariosHabilitados()){
				getInstance().addUsuarioHabilitado((int)idCajaChica,entidad,d, screenName, connection);
			}
		    
            connection.commit();
	  } catch (Exception e) {
		  if(null!=connection){
			  connection.rollback();
			  throw e;
		  }			
	  } finally {
		 if (connection != null) {
			 connection.close();
		 }
	  }    
	  return idCajaChica;
	}
	
	
	public static long update(CajaChica cajaChica, String screenName,int entidad) throws Exception {
		long idCajaChica = 0; 
		Connection connection = null;
		//Boolean esBaja=false;
		try {			
			connection = ConnectionHelper.getConnection();
			connection.setAutoCommit(false);
		    idCajaChica=getInstance().update(cajaChica, screenName,entidad,connection);
		    CajaChica cajaChicaDB = get(cajaChica.getId(),entidad);
		    
		  //Inserta usuarios
			if(cajaChica.getUsuariosHabilitados() !=null){
				//Analiza Altas - apareo con registros existentes en BD			
				for(User td:cajaChica.getUsuariosHabilitados()){
					Boolean existe=false;
					for(User tdDB:cajaChicaDB.getUsuariosHabilitados()){
						if(td.getUserId()==tdDB.getUserId()){
							existe=true;
							break;
						}
					}
					if(!existe){
			    	   getInstance().addUsuarioHabilitado((int)idCajaChica, entidad, td, screenName, connection);
			    	}
				}
				
				//Analiza Bajas - apareo con registros existentes en BD
				for(User tdDB:cajaChicaDB.getUsuariosHabilitados() ){
					Boolean existe=false;
					for(User td:cajaChica.getUsuariosHabilitados()){
						if(td.getUserId()==tdDB.getUserId()){
							existe=true;
							break;
						}
					}
					if(!existe){
					   getInstance().deleteUsuarioHabilitado((int)idCajaChica, entidad, tdDB, screenName, connection);	
		    	    }
				}
			}else{
				if( cajaChicaDB.getUsuariosHabilitados() != null &&  cajaChicaDB.getUsuariosHabilitados().size()>0){
					for(User tdDB:cajaChicaDB.getUsuariosHabilitados() ){
						//Marcar con fecha de baja todos los usuarios
						getInstance().deleteUsuarioHabilitado((int)idCajaChica, entidad, tdDB, screenName, connection);	
					}
				}
			}
			
		    
		    
		    
		    
            connection.commit();
	  } catch (Exception e) {
		  if(null!=connection){
			  connection.rollback();
			  throw e;
		  }			
	  } finally {
		 if (connection != null) {
			 connection.close();
		 }
	  }    
	  return idCajaChica;
	}
	
	public static List<Concepto> getConceptos(Date fecha,int id)
			throws SystemException {
		return getInstance().getConceptos(fecha, id);
    }
	
	public static Double getSaldo(Integer idCaja,Integer entidad) throws Exception {
		Double saldo = 0D; 
		Connection connection = null;
		try {			
			connection = ConnectionHelper.getConnection();
		    saldo=getInstance().getSaldo(idCaja,entidad,connection);
		    
	  } catch (Exception e) {
		  if(null!=connection){
			  throw e;
		  }			
	  } finally {
		 if (connection != null) {
			 connection.close();
		 }
	  }    
	  return saldo;
	}
	
	public static long updateEstado(CajaChica cajaChica, String screenName,int entidad) throws Exception {
		long idCajaChica = 0; 
		Connection connection = null;
		try {			
			connection = ConnectionHelper.getConnection();
			connection.setAutoCommit(false);
		    idCajaChica=getInstance().updateEstado(cajaChica, screenName,entidad,connection);
		    
            connection.commit();
	  } catch (Exception e) {
		  if(null!=connection){
			  connection.rollback();
			  throw e;
		  }			
	  } finally {
		 if (connection != null) {
			 connection.close();
		 }
	  }    
	  return idCajaChica;
	}
	
	public static WorkflowDefinition getEstadoActual(Integer idCaja) throws Exception {
		WorkflowDefinition estado = new WorkflowDefinition();			
		estado=getInstance().getEstadoActual(idCaja);		    
	    return estado;
	}
	
	
	public static long addComprobante(long idCajaChica,ComprobanteCajaChica comprobante, String screenName,int entidad) throws Exception {
		long idComprobanteCajaChica = 0; 
		Connection connection = null;
		try {			
			connection = ConnectionHelper.getConnection();
			connection.setAutoCommit(false);
		    idComprobanteCajaChica=getInstance().addComprobante(idCajaChica,comprobante, screenName,entidad,connection);
		    
            connection.commit();
	  } catch (Exception e) {
		  if(null!=connection){
			  connection.rollback();
			  throw e;
		  }			
	  } finally {
		 if (connection != null) {
			 connection.close();
		 }
	  }    
	  return idComprobanteCajaChica;
	}
	
	public static List<ComprobanteCajaChica> comprobantesPendientesRendicion(int entidad,int id) throws SystemException{
		List<ComprobanteCajaChica> comprobantes;
		comprobantes = getInstance().comprobantesPendientesRendicion(entidad, id);
		return comprobantes;
	}
	
	public static ComprobanteCajaChica comprobantePorId(int entidad,int id) throws SystemException{
		ComprobanteCajaChica comprobante= new ComprobanteCajaChica();
		comprobante = getInstance().comprobantePorId(entidad, id);
		return comprobante;
	}
	
	public static long updateComprobante(long idComprobanteCajaChica,ComprobanteCajaChica comprobante, String screenName,int entidad) throws Exception {
		Connection connection = null;
		try {			
			connection = ConnectionHelper.getConnection();
			connection.setAutoCommit(false);
		    idComprobanteCajaChica=getInstance().updateComprobante(idComprobanteCajaChica,comprobante, screenName,entidad,connection);
		    
            connection.commit();
	  } catch (Exception e) {
		  if(null!=connection){
			  connection.rollback();
			  throw e;
		  }			
	  } finally {
		 if (connection != null) {
			 connection.close();
		 }
	  }    
	  return idComprobanteCajaChica;
	}
	
	public static long deleteComprobante(long idComprobanteCajaChica,int entidad) throws Exception {
		Connection connection = null;
		try {			
			connection = ConnectionHelper.getConnection();
			connection.setAutoCommit(false);
		    idComprobanteCajaChica=getInstance().deleteComprobante(idComprobanteCajaChica,entidad,connection);
		    
            connection.commit();
	  } catch (Exception e) {
		  if(null!=connection){
			  connection.rollback();
			  throw e;
		  }			
	  } finally {
		 if (connection != null) {
			 connection.close();
		 }
	  }    
	  return idComprobanteCajaChica;
	}
	
	public static long solicitaReposicion(CajaChica cajaChica, List<ComprobanteCajaChica>comprobantes, String screenName,int entidad) throws Exception {
		long idCajaChica=0;
		Connection connection = null;
		try {			
			connection = ConnectionHelper.getConnection();
			connection.setAutoCommit(false);
			
			for(ComprobanteCajaChica comp:comprobantes){
               getInstance().solicitaReposicionComprobante(comp.getId(), entidad, connection);				
			}			
			String estadoId = TraeListasServiceUtil.getSystemConfig("ESTADO_CAJA_CHICA_SOLICITA_REPOSICION");
			cajaChica.getEstado().setId(Integer.parseInt(estadoId));
			getInstance().updateEstado(cajaChica, screenName, entidad, connection);
			idCajaChica=cajaChica.getId();
            connection.commit();
	  } catch (Exception e) {
		  if(null!=connection){
			  connection.rollback();
			  throw e;
		  }			
	  } finally {
		 if (connection != null) {
			 connection.close();
		 }
	  }    
	  return idCajaChica;
	}
	
	public static List<ComprobanteCajaChica> comprobantesEnviadosARendicion(int entidad,int id) throws SystemException{
		List<ComprobanteCajaChica> comprobantes;
		comprobantes = getInstance().comprobantesEnviadosARendicion(entidad, id);
		return comprobantes;
	}
	
	public static List<ComprobanteCajaChica> comprobantesEnviadosARendicionResumido(int entidad,int id) throws SystemException{
		List<ComprobanteCajaChica> comprobantes;
		comprobantes = getInstance().comprobantesEnviadosARendicionResumido(entidad, id);
		return comprobantes;
	}
	
	public static long procesaComprobantesRechazados(List<Integer>rechazados,List<Integer>aprobados,CajaChica cajaChica, String screenName,int entidad) throws Exception {
		long idCajaChica=0;
		Connection connection = null;
		try {			
			connection = ConnectionHelper.getConnection();
			connection.setAutoCommit(false);
			
			for(Integer nro:rechazados){
				getInstance().procesaRechazoComprobante(nro,true,entidad,connection);
			}
			
			for(Integer nro:aprobados){
				getInstance().procesaRechazoComprobante(nro,false,entidad,connection);
			}
			
			String estadoId = TraeListasServiceUtil.getSystemConfig("ESTADO_CAJA_CHICA_RECHAZA_REPOSICION");
			cajaChica.getEstado().setId(Integer.parseInt(estadoId));
			getInstance().updateEstado(cajaChica, screenName, entidad, connection);
			idCajaChica=cajaChica.getId();
            connection.commit();
	  } catch (Exception e) {
		  if(null!=connection){
			  connection.rollback();
			  throw e;
		  }			
	  } finally {
		 if (connection != null) {
			 connection.close();
		 }
	  }    
	  return idCajaChica;
	}
	
	public static long addOrdenDePagoOspim(CajaChica cajaChica,OrdenPagoOspim op, String screenName,int entidad) throws Exception {
		long idCajaChica = 0; 
		Connection connection = null;
		try {			
			connection = ConnectionHelper.getConnection();
			connection.setAutoCommit(false);
			
			getInstance().addOrdenDePagoOspim(cajaChica,op,screenName,connection);
			
			idCajaChica=getInstance().updateEstado(cajaChica, screenName,entidad,connection);
			
            connection.commit();
	  } catch (Exception e) {
		  if(null!=connection){
			  connection.rollback();
			  throw e;
		  }			
	  } finally {
		 if (connection != null) {
			 connection.close();
		 }
	  }    
	  return idCajaChica;
	}
	
	public static long ingresaReposicion(CajaChica cajaChica, Date fecha, int entidad,String screenName) throws Exception {
		long idCajaChica=0;
		Connection connection = null;
		try {			
			connection = ConnectionHelper.getConnection();
			connection.setAutoCommit(false);
			getInstance().ingresaReposicion(cajaChica, fecha, entidad, screenName, connection);
			idCajaChica=cajaChica.getId();
            connection.commit();
	  } catch (Exception e) {
		  if(null!=connection){
			  connection.rollback();
			  throw e;
		  }			
	  } finally {
		 if (connection != null) {
			 connection.close();
		 }
	  }    
	  return idCajaChica;
	}
	
	public static WorkflowDefinition getUltimoEstadoPorId(Integer idCajaChica,Integer idEstado) throws Exception {
		WorkflowDefinition estado = new WorkflowDefinition(); 
		try {			
			estado=getInstance().getUltimoEstadoPorId(idCajaChica,idEstado);
		    
	  } catch (Exception e) {
		    throw e;
		  			
	  } finally {}    
	  return estado;
	}
	
	public static List<ComprobanteCajaChica> reporteCajaChica(int entidad,int id,Date fechaHasta) throws SystemException{
		List<ComprobanteCajaChica> comprobantes;
		comprobantes = getInstance().reporteCajaChica(entidad, id, fechaHasta);
		
		return comprobantes;
	}
	
	public static long procesaComprobantesAprobadosSinOP(List<Integer>aprobados,CajaChica cajaChica, String screenName,int entidad) throws Exception {
		long idCajaChica=0;
		Connection connection = null;
		try {			
			connection = ConnectionHelper.getConnection();
			connection.setAutoCommit(false);
			
			
			for(Integer nro:aprobados){
				getInstance().procesaApruebaSinOPComprobante(nro, true, entidad, connection);  
			}
						
			String estadoId = TraeListasServiceUtil.getSystemConfig("ESTADO_CAJA_CHICA_APRUEBA_SIN_OP_REPOSICION");
			cajaChica.getEstado().setId(Integer.parseInt(estadoId));
			getInstance().updateEstado(cajaChica, screenName, entidad, connection);
			idCajaChica=cajaChica.getId();
            connection.commit();
	  } catch (Exception e) {
		  if(null!=connection){
			  connection.rollback();
			  throw e;
		  }			
	  } finally {
		 if (connection != null) {
			 connection.close();
		 }
	  }    
	  return idCajaChica;
	}
	
	/*
	public static Integer getUltimoNroComprobante(String cuit,String sucursal,String tipo,String letra,Integer entidad,Integer ptoVta) throws Exception {
		Integer nro = 0; 
		Connection connection = null;
		try {			
			connection = ConnectionHelper.getConnection();
		    nro=getInstance().getUltimoNroComprobante(tipo, letra, cuit, sucursal, entidad,ptoVta, connection);
		    
	  } catch (Exception e) {
		  if(null!=connection){
			  throw e;
		  }			
	  } finally {
		 if (connection != null) {
			 connection.close();
		 }
	  }    
	  return nro;
	}
	*/
	
	public static Double getUltimoNroComprobante(String cuit,String sucursal,String tipo,String letra,Integer entidad,Integer ptoVta) throws Exception {
		Double nro = 0D; 
		try {			
		    nro=getInstance().getUltimoNroComprobante(tipo, letra, cuit, sucursal, entidad,ptoVta, null);
		    
	    } catch (Exception e) {
	    	_log.error("Error al obtener ultimo nro del Comprobante Caja Chica");
		 	_log.error(e);
	  }  
	  return nro;
	}
	
	public static long addOrdenDePagoUoma(CajaChica cajaChica,OrdenPago op, String comprobantes, String screenName,int entidad) throws Exception {
		long idCajaChica = 0; 
		Connection connection = null;
		try {			
			connection = ConnectionHelper.getConnection();
			connection.setAutoCommit(false);
			
			String[]vAprobados = comprobantes.split(";");
			for(int i=0;i<vAprobados.length;i++){
				Integer idComprobante = Integer.parseInt(vAprobados[i]);
				getInstance().updateOrdenDePagoUomaComprobante(cajaChica,op,idComprobante,screenName,connection);
			}
			
			getInstance().addOrdenDePagoUoma(cajaChica,op,screenName,connection);
			
			idCajaChica=getInstance().updateEstado(cajaChica, screenName,entidad,connection);
			
            connection.commit();
	  } catch (Exception e) {
		  if(null!=connection){
			  connection.rollback();
			  throw e;
		  }			
	  } finally {
		 if (connection != null) {
			 connection.close();
		 }
	  }    
	  return idCajaChica;
	}
	
	public static Concepto getConceptoMaestro(int id)
			throws SystemException {
		Concepto c=new Concepto();
		List<Concepto>l =getInstance().getConceptosMaestro( id);
		if(l.size()>0){
			c=l.get(0);
		}
		return c;
    }
	
	public static List<ComprobanteCajaChica> comprobantesPendientesInforme(int entidad,int id) throws SystemException{
		List<ComprobanteCajaChica> comprobantes;
		comprobantes = getInstance().comprobantesPendientesInforme(entidad, id);
		return comprobantes;
	}
	
	public static long updateComprobantesPendientesInforme(int entidad,int id) throws Exception {
		Connection connection = null;
		long idComprobanteCajaChica = 0; 
		try {			
			connection = ConnectionHelper.getConnection();
			connection.setAutoCommit(false);
		    idComprobanteCajaChica=getInstance().updateComprobantesPendientesInforme(entidad, id, connection);
            connection.commit();
	  } catch (Exception e) {
		  if(null!=connection){
			  connection.rollback();
			  throw e;
		  }			
	  } finally {
		 if (connection != null) {
			 connection.close();
		 }
	  }    
	  return idComprobanteCajaChica;
	}
	
	public static long updateComprobantesPendientesRecibo(int entidad,int idCajaChica, int idSeccional) throws Exception {
		Connection connection = null;
		long idComprobanteCajaChica = 0; 
		try {			
			connection = ConnectionHelper.getConnection();
			connection.setAutoCommit(false);
		    idComprobanteCajaChica=getInstance().updateComprobantesPendientesRecibo(entidad, idCajaChica,idSeccional, connection);
            connection.commit();
	  } catch (Exception e) {
		  if(null!=connection){
			  connection.rollback();
			  throw e;
		  }			
	  } finally {
		 if (connection != null) {
			 connection.close();
		 }
	  }    
	  return idComprobanteCajaChica;
	}
	
	public static Integer  verificaImpresionRecibo(int entidad,int idCajaChica,int idSeccional) throws Exception{
		Connection connection = null;
		Integer  cantidad = 0; 
		try {			
			connection = ConnectionHelper.getConnection();
			connection.setAutoCommit(false);
		    cantidad=getInstance().verificaImpresionRecibo(entidad, idCajaChica,idSeccional, connection);
            connection.commit();
	  } catch (Exception e) {
		  if(null!=connection){
			  connection.rollback();
			  throw e;
		  }			
	  } finally {
		 if (connection != null) {
			 connection.close();
		 }
	  }    
	  return cantidad;
	}
	
	public static List<ComprobanteCajaChica> comprobantesPorOP(int entidad,int id) throws SystemException{
		List<ComprobanteCajaChica> comprobantes;
		comprobantes = getInstance().comprobantesPorOP(entidad, id);
		return comprobantes;
	}
	
	public static boolean verificaComprobante(ComprobanteCajaChica comprobante,int entidad) throws SystemException{
		boolean ret=false;
		ret = getInstance().verificaComprobante(comprobante,entidad);
		return ret;
	}
}
