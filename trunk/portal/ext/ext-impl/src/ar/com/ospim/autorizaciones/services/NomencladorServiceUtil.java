package ar.com.ospim.autorizaciones.services;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import ar.com.ospim.autorizaciones.beans.AutorizacionesPmi;
import ar.com.ospim.autorizaciones.beans.ModalidadAtencion;
import ar.com.ospim.autorizaciones.beans.Nomenclador;
import ar.com.ospim.autorizaciones.beans.NomencladorPlan;
import ar.com.ospim.autorizaciones.exceptions.AfiliadoNoEsBebeException;
import ar.com.ospim.autorizaciones.exceptions.ExcedeCantAutoException;
import ar.com.ospim.autorizaciones.exceptions.NoEsPlanMolineroException;
import ar.com.ospim.autorizaciones.exceptions.PeriodoNoConsecutivoException;
import ar.com.ospim.util.ConnectionHelper;
import ar.com.ospim.util.DateUtils;
import ar.com.ospim.webservice.service.AfiliadoServiceImpl;
import ar.com.ospim.autorizaciones.services.AutorizacionesServiceImpl;
import ar.com.ospim.autorizaciones.services.NomencladorServiceImpl;
import ar.com.ospim.correspondencia.beans.ItemCorrespondencia;
import ar.com.ospim.farmacia.beans.Medicamento;
import ar.com.ospim.global.beans.Concepto;
import ar.com.ospim.global.beans.Prestacion;
import ar.com.ospim.global.beans.PrestacionConcepto;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.util.PortalUtil;

public class NomencladorServiceUtil {
	private static final int TIPO_HONORARIOS_AMBULATORIO = 1;
	private static final int TIPO_HONORARIOS_INTERNACION = 2;
	private static final int TIPO_GASTOS_AMBULATORIO = 3;
	private static final int TIPO_GASTOS_INTERNACION = 4;
	@SuppressWarnings("unused")
	private static Log _log = LogFactoryUtil
			.getLog(NomencladorServiceUtil.class);

	private static NomencladorServiceImpl instance = null;

	public static NomencladorServiceImpl getInstance() {
		if (null == instance) {
			instance = new NomencladorServiceImpl();
		}
		return instance;
	}

	//Lista Nomenclador	
	public static List<Nomenclador> getListaNomenclador(
			int tipoNomenclador,String descripcionNomenclador,int especialidad,String codigoNomenclador,Boolean recuperaSUR,String resolucionNomenclador)
			throws SystemException {
		return getInstance().getListaNomenclador(tipoNomenclador,descripcionNomenclador,especialidad,codigoNomenclador,recuperaSUR,resolucionNomenclador);
	}
	public static List<Nomenclador> getListaNomencladorMarcaReinLiq(
			int tipoNomenclador,String descripcionNomenclador,int especialidad,String codigoNomenclador,Boolean recuperaSUR,String resolucionNomenclador, int  marca)
			throws SystemException {
		return getInstance().getListaNomencladorMarcaReinLiq(tipoNomenclador,descripcionNomenclador,especialidad,codigoNomenclador,recuperaSUR,resolucionNomenclador, marca);
	}
	public static List<Nomenclador> getListaNomencladorPrestacionesMedicas(
			int tipoNomenclador,String descripcionNomenclador,int especialidad,String codigoNomenclador,Boolean recuperaSUR,String resolucionNomenclador)
			throws SystemException {
		return getInstance().getListaNomencladorPrestacionesMedicas(tipoNomenclador,descripcionNomenclador,especialidad,codigoNomenclador,recuperaSUR,resolucionNomenclador);
	}
	
	public static List<Nomenclador> getListaNomencladorPreautorizaciones(
			int tipoNomenclador,String descripcionNomenclador,int especialidad,String codigoNomenclador,Boolean recuperaSUR,String resolucionNomenclador)
			throws SystemException {
		return getInstance().getListaNomencladorPreautorizaciones(tipoNomenclador,descripcionNomenclador,especialidad,codigoNomenclador,recuperaSUR,resolucionNomenclador);
	}
	
	public static long insertaNomenclador(Nomenclador nomenclador, String screenName,List<NomencladorPlan>listModalidad,PrestacionConcepto prestacionConcepto,
			List<NomencladorPlan>listTopes) throws Exception {
		long idNomenclador = 0; 
		Connection connection = null;
		
		
		try {			
			connection = ConnectionHelper.getConnection();
			connection.setAutoCommit(false);
		    // Alta del Nomenclador
			idNomenclador=getInstance().insertaNomenclador(nomenclador, screenName,connection);
		    
            // Insertar Lista Modalidad
			if(listModalidad !=null){
		      for(NomencladorPlan n:listModalidad){
		    	getInstance().insertaModalidadAtencion((int)idNomenclador, n, screenName, connection);
		      }
			}  
		    
			// Insertar Nomenclador Concepto
			if(prestacionConcepto !=null){
				Prestacion prestacion = new Prestacion();
				prestacion.setId_prestacion((int)idNomenclador);
				prestacion.setCodigo(nomenclador.getCodigo());
				prestacion.setDescripcion(nomenclador.getDescripcion());
				prestacion.setMarca_rein_liq(nomenclador.getMarcaReintegroLiquidacion());
				
				prestacionConcepto.setPrestacion(prestacion);
				prestacionConcepto.setIdTipoNomenclador(nomenclador.getId_tipo_nomenclador());
				prestacionConcepto.setCoeficienteGastos(BigDecimal.valueOf(nomenclador.getCoeficienteGastos()));
				prestacionConcepto.setCoeficienteHonorarios(BigDecimal.valueOf(nomenclador.getCoeficienteHonorarios()));
				
				getInstance().insertaNomencladorConceptos(prestacionConcepto, screenName,connection);
			}
			
			// Insertar Lista Topes Reintegros
			if(listTopes !=null){
			    for(NomencladorPlan n:listTopes){
					getInstance().insertaTopesReintegros((int)idNomenclador, n, screenName, connection);
			    }
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
	  return idNomenclador;
	}
	
    
	public static Nomenclador buscarNomencladorPorId(int id) throws SystemException{
		
		return getInstance().buscarNomencladorPorId(id);
		
	}
	
    public static ModalidadAtencion buscarModalidadAtencionPorId(int id) throws SystemException{
		
		return getInstance().buscarModalidadAtencionPorId(id);
		
	}
    
    public static List<NomencladorPlan> buscarNomencladorPlanPorId(int id) throws SystemException{
		
		return getInstance().buscarNomencladorPlanPorId(id);
		
	}
    
    
    public static long updateNomenclador(Nomenclador nomenclador, String screenName,List<NomencladorPlan>listModalidad,
    		PrestacionConcepto prestacionConcepto,Date ejercicioOriginal,List<NomencladorPlan>listTopes) throws Exception {
		long idNomenclador = 0; 
		Connection connection = null;
		Boolean modificoPlan=false;
		
		try {			
			connection = ConnectionHelper.getConnection();
			connection.setAutoCommit(false);
		    
            // Insertar Lista Modalidad
			List<NomencladorPlan> lnp = getInstance().buscarNomencladorPlanPorId(nomenclador.getId_prestacion());
			if(listModalidad !=null){
              //Analiza Altas - apareo con registros existentes en BD				
		      for(NomencladorPlan n:listModalidad){
		    	  Boolean existe=false;
		    	  for(NomencladorPlan np:lnp){
		    		  if(n.getPlan().getId()==np.getPlan().getId() &&
		    		     n.getAutorizacion().getId()==np.getAutorizacion().getId()){
		    			 existe=true; 
		    			 break;
		    		  }
		    	  }
		    	  if(!existe){
		    		  modificoPlan=true;
		    		  getInstance().insertaModalidadAtencion(nomenclador.getId_prestacion(), n, screenName, connection);
		    	  }
		      }
		      
		      //Analiza Bajas - apareo con registros existentes en BD
		      for(NomencladorPlan n:lnp){
		    	  Boolean existe=false;
		    	  for(NomencladorPlan np:listModalidad){
		    		  if(n.getPlan().getId()==np.getPlan().getId() &&
		    		     n.getAutorizacion().getId()==np.getAutorizacion().getId()){
		    			 existe=true; 
		    			 break;
		    		  }
		    	  }
		    	  if(!existe){
                      //Eliminar Modalidad Atencion
		    		  modificoPlan=true;
		    		  getInstance().eliminaModalidadAtencion(n.getId_prestacion(),n, screenName, connection);
		    		  
		    	  }
		      }

			}else{
				if(lnp != null && lnp.size()>0){
					modificoPlan=true;
					for(NomencladorPlan np:lnp){
						//Marcar con fecha de baja todos lo Nomenclador Plan
						getInstance().eliminaModalidadAtencion(np.getId_prestacion(),np, screenName, connection);
					}
				}
			}
			
            // Insertar Lista Topes
			List<NomencladorPlan> lnt = getInstance().buscarNomencladorPlanTopesReintegrosPorId(nomenclador.getId_prestacion());
			if(listTopes !=null){
              //Analiza Altas - apareo con registros existentes en BD				
		      for(NomencladorPlan n:listTopes){
		    	  Boolean existe=false;
		    	  for(NomencladorPlan np:lnt){
		    		  if(n.getId()==np.getId() ){
		    			 existe=true; 
		    			 break;
		    		  }
		    	  }
		    	  if(!existe){
		    		  modificoPlan=true;
		    		  getInstance().insertaTopesReintegros(nomenclador.getId_prestacion(), n, screenName, connection);
		    	  }else {
		    		  getInstance().updateTopesReintegros(nomenclador.getId_prestacion(), n, screenName, connection);
		    	  }
		      }
		      
		      //Analiza Bajas - apareo con registros existentes en BD
		      for(NomencladorPlan n:lnt){
		    	  Boolean existe=false;
		    	  for(NomencladorPlan np:listTopes){
		    		  if(n.getId()==np.getId() ){
		    			 existe=true; 
		    			 break;
		    		  }
		    	  }
		    	  if(!existe){
                      //Eliminar Tope
		    		  modificoPlan=true;
		    		  getInstance().eliminaTopesReintegros(n.getId_prestacion(),n, screenName, connection);
		    		  
		    	  }
		      }

			}else{
				if(lnt != null && lnt.size()>0){
					modificoPlan=true;
					for(NomencladorPlan np:lnt){
						//Marcar con fecha de baja todos lo Nomenclador Plan
						getInstance().eliminaTopesReintegros(np.getId_prestacion(),np, screenName, connection);
					}
				}
			}
			
			
			
			
			// Alta del Nomenclador
			idNomenclador=getInstance().updateNomenclador(nomenclador,modificoPlan?"P":"M", screenName,connection);
			
////////////////////////////////////////			
			//Actualiza Nomenclador Conceptos 
			
			PrestacionConcepto pConceptosEnBase = getInstance()
					.getPrestacionesConceptosActualPorIdPrestacion(
							prestacionConcepto.getPrestacion().getId(), ejercicioOriginal,
							prestacionConcepto.getValidoHastaGastosAmbulatorio());

			actualizarNomencladorConcepto(prestacionConcepto, connection,
					prestacionConcepto.getValidoDesdeHonorariosAmbulatorio(),
					pConceptosEnBase.getValidoDesdeHonorariosAmbulatorio(),
					prestacionConcepto.getValidoHastaHonorariosAmbulatorio(),
					pConceptosEnBase.getValidoHastaHonorariosAmbulatorio(),
					TIPO_HONORARIOS_AMBULATORIO,
					prestacionConcepto.getIdHonorariosAmbulatorio(),
					prestacionConcepto.getHonorariosAmbulatorio(), screenName);

			actualizarNomencladorConcepto(prestacionConcepto, connection,
					prestacionConcepto.getValidoDesdeHonorariosInternacion(),
					pConceptosEnBase.getValidoDesdeHonorariosInternacion(),
					prestacionConcepto.getValidoHastaHonorariosInternacion(),
					pConceptosEnBase.getValidoHastaHonorariosInternacion(),
					TIPO_HONORARIOS_INTERNACION,
					prestacionConcepto.getIdHonorariosInternacion(),
					prestacionConcepto.getHonorariosInternacion(), screenName);

			actualizarNomencladorConcepto(prestacionConcepto, connection,
					prestacionConcepto.getValidoDesdeGastosAmbulatorio(),
					pConceptosEnBase.getValidoDesdeGastosAmbulatorio(),
					prestacionConcepto.getValidoHastaGastosAmbulatorio(),
					pConceptosEnBase.getValidoHastaGastosAmbulatorio(),
					TIPO_GASTOS_AMBULATORIO,
					prestacionConcepto.getIdGastosAmbulatorio(),
					prestacionConcepto.getGastosAmbulatorio(), screenName);

			actualizarNomencladorConcepto(prestacionConcepto, connection,
					prestacionConcepto.getValidoDesdeGastosInternacion(),
					pConceptosEnBase.getValidoDesdeGastosInternacion(),
					prestacionConcepto.getValidoHastaGastosInternacion(),
					pConceptosEnBase.getValidoHastaGastosInternacion(),
					TIPO_GASTOS_INTERNACION,
					prestacionConcepto.getIdGastosInternacion(),
					prestacionConcepto.getGastosInternacion(), screenName);
///////////////////////////////////////
		    
			connection.commit();
	  } catch (Exception e) {
		  if(null!=connection){
			  connection.rollback();
			  throw e;
		  }			
	  } finally {
		 ConnectionHelper.cerrar(connection);
	  }    
	  return idNomenclador;
	}
    
    
    public static long eliminaNomenclador(int idNomenclador, String screenName) throws Exception {
    	long ret=0;
		Connection connection = null;
		
		try {			
			connection = ConnectionHelper.getConnection();
			connection.setAutoCommit(false);
		    // Baja del Nomenclador
			ret=getInstance().eliminaNomenclador(idNomenclador, screenName,connection);
			connection.commit();
	  } catch (Exception e) {
		  if(null!=connection){
			  connection.rollback();
			  throw e;
		  }			
	  } finally {
		  ConnectionHelper.cerrar(connection);
	  }    
	  return idNomenclador;
	}
    
    public static long recuperaNomenclador(int idNomenclador, String screenName) throws Exception {
    	long ret=0;
		Connection connection = null;
		
		try {			
			connection = ConnectionHelper.getConnection();
			connection.setAutoCommit(false);
		    // Recupera Nomenclador dado de Baja
			ret=getInstance().recuperaNomenclador(idNomenclador, screenName,connection);
			connection.commit();
	  } catch (Exception e) {
		  if(null!=connection){
			  connection.rollback();
			  throw e;
		  }			
	  } finally {
		 ConnectionHelper.cerrar(connection);
	  }    
	  return idNomenclador;
	}
    
    public static boolean existeNomencladorPorTipoCodigo(int idTipo, String codigo) throws Exception {
    	boolean ret=false;
		try {			
			ret=getInstance().existeNomencladorPorTipoCodigo(idTipo, codigo);
	    } catch (Exception e) {
			  throw e;
	    }   
	  return ret;
	}
    
    private static void actualizarNomencladorConcepto(
			PrestacionConcepto prestacionConcepto, Connection con,
			Date desdeNuevo, Date desdeOriginal, Date hastaNuevo,
			Date hastaOriginal, int tipo, int idNomencladorConcepto,
			Concepto conceptoNuevo, String user) throws Exception {

		if (desdeOriginal != null
				&& DateUtils.compararFechasTruncarEnDia(desdeNuevo,
						desdeOriginal) == 0
				&& DateUtils.compararFechasTruncarEnDia(hastaNuevo,
						hastaOriginal) == 0) {
			getInstance().updateNomencladorConcepto(con,
					prestacionConcepto.getPrestacion().getId_prestacion(),
					idNomencladorConcepto, conceptoNuevo.getId(), desdeNuevo,
					hastaNuevo, tipo, user);
		} else {
			getInstance().reemplazarNomencladorConcepto(
					con,
					prestacionConcepto.getPrestacion().getId_prestacion(),
					desdeNuevo,
					desdeOriginal != null ? desdeOriginal : DateUtils
							.getDesdeInfinito().getTime(),
					hastaNuevo,
					hastaOriginal != null ? hastaOriginal : DateUtils
							.getHastaInfinito().getTime(),
					idNomencladorConcepto, conceptoNuevo.getId(),
					DateUtils.getDesdeEjercicioActual(),
					DateUtils.getInfinito(), user, tipo);
		}
	}

    
    public static PrestacionConcepto getPrestacionesConceptos(int id,
			Date desdeEjercicio, Date hastaEjercicio) {
		return getInstance().getPrestacionesConceptosActualPorIdPrestacion(id,
				desdeEjercicio, hastaEjercicio);
	}
    
    public static List<Medicamento> getBusquedaMedicamentos(int troquel,
			String nombre) {
		List<Medicamento> medicamentos = getInstance().getBusquedaMedicamentos(
				troquel, 0, nombre, "", "", "");
		return medicamentos;
	}

    
    public static Nomenclador getEstudiosRequeridosPorId(int id) throws SystemException{
		
		return getInstance().getEstudiosRequeridosPorId(id);
		
	}
    
    
    public static List<NomencladorPlan> buscarNomencladorPlanTopesReintegrosPorId(Integer id) throws SystemException{
		
		return getInstance().buscarNomencladorPlanTopesReintegrosPorId(id);
		
	}
    
    public static NomencladorPlan buscarNomencladorPlanTopesReintegros(Integer id,Integer idPlan, Date fecha) throws SystemException{
		
		return getInstance().buscarNomencladorPlanTopesReintegros(id,idPlan,fecha);
		
	}
}
