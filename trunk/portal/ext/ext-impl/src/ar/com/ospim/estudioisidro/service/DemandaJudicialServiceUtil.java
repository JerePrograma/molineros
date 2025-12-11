package ar.com.ospim.estudioisidro.service;

import java.io.Serializable;
import java.sql.Connection;
import java.util.Date;
import java.util.List;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portlet.documentlibrary.model.impl.DLFileEntryImpl;

import ar.com.ospim.autorizaciones.beans.Estado;
import ar.com.ospim.comprobantesPortalProveedores.beans.ComprobanteFiltro;
import ar.com.ospim.comprobantesPortalProveedores.services.ComprobanteServiceUtil;
import ar.com.ospim.estudioisidro.beans.DemandaJudicial;
import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.beans.Cheque;
import ar.com.ospim.global.beans.Comprobante;
import ar.com.ospim.global.beans.PlanCuentas;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.liquidaciones.services.CuentaServiceUtil;
import ar.com.ospim.tesoreria.beans.Acta;
import ar.com.ospim.tesoreria.beans.contabilidad.Asiento;
import ar.com.ospim.tesoreria.beans.contabilidad.Asiento.Detalle;
import ar.com.ospim.tesoreria.beans.convenio.Convenio;
import ar.com.ospim.util.ConnectionHelper;

public class DemandaJudicialServiceUtil implements Serializable {

	private static final long serialVersionUID = -4723578996785941546L;

	private static Log _log = LogFactoryUtil
			.getLog(DemandaJudicialServiceUtil.class);

	private static DemandaJudicialServiceImpl instance = null;

	public static DemandaJudicialServiceImpl getInstance() {
		if (null == instance) {
			instance = new DemandaJudicialServiceImpl();
		}
		return instance;
	}
	
	
	public static Integer insertaDemanda(DemandaJudicial demanda, String screenName) throws Exception {
		Integer id = 0; 
		Connection connection = null;
		
		try {			
			connection = ConnectionHelper.getConnection();
			connection.setAutoCommit(false);
			id= getInstance().insertaDemanda(demanda, screenName,connection);
			
			Estado estado=new Estado();
			estado.setId("IN");
			estado.setFecha(demanda.getFecha());
			
			demanda.getEstados().add(estado);
			
			getInstance().insertaDemandaEstado(id,estado, screenName, connection);
			
			for(Acta a : demanda.getActas()) {
				getInstance().insertaDemandaActa(id, a, screenName, connection);	
			}
			
			for(Convenio a : demanda.getConvenios()) {
				getInstance().insertaDemandaConvenio(id, a, screenName, connection);	
			}
			
			for(Cheque a : demanda.getCheques()) {
				getInstance().insertaDemandaCheque(id, a, screenName, connection);	
			}
			connection.commit();
	  } catch (Exception e) {
		  _log.error(e);
		  ConnectionHelper.rollback(connection); 
	  } finally {
		 ConnectionHelper.cerrar(connection);
	  }    
	  return id;
	}
	
	
	public static Integer updateDemanda(DemandaJudicial demanda, String screenName) throws Exception {
		Integer id = 0; 
		Connection connection = null;
		
		try {			
			connection = ConnectionHelper.getConnection();
			connection.setAutoCommit(false);
			id= getInstance().updateDemanda(demanda, screenName,connection);
			
			getInstance().deleteActasByIdDemanda(demanda.getId(), connection);
			getInstance().deleteConveniosByIdDemanda(demanda.getId(), connection);
			getInstance().deleteChequesByIdDemanda(demanda.getId(), connection);
			getInstance().deleteEstadosByIdDemanda(demanda.getId(), connection);
			
			for(Estado e: demanda.getEstados()) {
			   getInstance().insertaDemandaEstado(id,e, screenName, connection);
			}   
			
			for(Acta a : demanda.getActas()) {
				getInstance().insertaDemandaActa(id, a, screenName, connection);	
			}
			
			for(Convenio a : demanda.getConvenios()) {
				getInstance().insertaDemandaConvenio(id, a, screenName, connection);	
			}
			
			for(Cheque a : demanda.getCheques()) {
				getInstance().insertaDemandaCheque(id, a, screenName, connection);	
			}
			connection.commit();
	  } catch (Exception e) {
		  _log.error(e);
		  ConnectionHelper.rollback(connection); 
	  } finally {
		 ConnectionHelper.cerrar(connection);
	  }    
	  return id;
	}
	
	public static List<DemandaJudicial> getLista(
			DemandaJudicial filtro,Integer pagina) throws Exception {
		String idFacturaImg="";
		List<DemandaJudicial> demandas = (List<DemandaJudicial>) getInstance().getLista(filtro,pagina,null);
		
		/*
		for(Comprobante comprobante:comprobantes) {
			idFacturaImg = comprobante.getAcreedorEmpresa().getCuit()+"-"+comprobante.getTipoComprobante()+"-"+
					comprobante.getLetraComprobante()+String.format("%05d",comprobante.getPtoVenta())+comprobante.getNroComprobante();

			List<DLFileEntryImpl>list = ComprobanteServiceUtil.getImagenesComprobantes(idFacturaImg,"TODOS");
			comprobante.setImagenes(list);
		}
		*/
		return demandas;
	}

	
	public static DemandaJudicial getDemandaById( Integer id) throws Exception {
		String idFacturaImg="";
		DemandaJudicial demanda=null;
		DemandaJudicial filtro= new DemandaJudicial(id);
		
		Connection con =ConnectionHelper.getConnection();
		List<DemandaJudicial> demandas = (List<DemandaJudicial>) getInstance().getLista(filtro,0,con);
		
		if(!demandas.isEmpty()) {
			demanda=demandas.get(0);
		}
		
		List<Acta>actas =(List<Acta>)getInstance().getActasByIdDemanda(id, con);
		demanda.setActas(actas);
		
		List<Convenio>convenios =(List<Convenio>)getInstance().getConveniosByIdDemanda(id, con);
		demanda.setConvenios(convenios);
		
		List<Cheque>cheques =(List<Cheque>)getInstance().getChequesByIdDemanda(id, con);
		demanda.setCheques(cheques);
		
		List<Estado>estados =(List<Estado>)getInstance().getEstadosByIdDemanda(id, con);
		demanda.setEstados(estados);
		
		List<Asiento>asientos=(List<Asiento>)getInstance().getAsientosByIdDemanda(id, con);
		demanda.setAsientos(asientos);
		
	    ConnectionHelper.cerrar(con);
	    
		return demanda;
	}
	
	public static Integer deleteDemanda(Integer demanda, String screenName) throws Exception {
	  Integer id = 0; 
	  Connection connection = null;
	  try {			
			connection = ConnectionHelper.getConnection();
			connection.setAutoCommit(false);
			id= getInstance().deleteDemanda(demanda, screenName,connection);
			connection.commit();
	  } catch (Exception e) {
		  _log.error(e);
		  ConnectionHelper.rollback(connection); 
	  } finally {
		 ConnectionHelper.cerrar(connection);
	  }    
	  return id;
	}
	
	
	
	public static Integer insertaAsiento(Integer demandaId,Asiento asiento, String screenName) throws Exception {
		Integer id = 0; 
		Connection connection = null;
		
		try {			
			connection = ConnectionHelper.getConnection();
			connection.setAutoCommit(false);
			id= getInstance().insertaAsiento(demandaId,asiento, screenName,connection);
			
			for(Detalle a : asiento.getDetalle()) {
				getInstance().insertaAsientoDetalle(demandaId,id, a, screenName, connection);	
			}
			
			connection.commit();
	  } catch (Exception e) {
		  _log.error(e);
		  ConnectionHelper.rollback(connection); 
	  } finally {
		 ConnectionHelper.cerrar(connection);
	  }    
	  return id;
	}
	
	
	public static Integer updateAsiento(Integer demandaId,Asiento asiento, String screenName) throws Exception {
		Integer id = 0; 
		Connection connection = null;
		
		try {			
			connection = ConnectionHelper.getConnection();
			connection.setAutoCommit(false);
			id= getInstance().updateAsiento(demandaId,asiento, screenName,connection);
			
			getInstance().deleteAsientoDetalle(demandaId,asiento.getId(),connection);
			
			for(Detalle a : asiento.getDetalle()) {
				getInstance().insertaAsientoDetalle(demandaId,id, a, screenName, connection);	
			}
			
			connection.commit();
	  } catch (Exception e) {
		  _log.error(e);
		  ConnectionHelper.rollback(connection); 
	  } finally {
		 ConnectionHelper.cerrar(connection);
	  }    
	  return id;
	}
	
	public static Integer deleteAsiento(Integer demandaId,Integer asiento) throws Exception {
		Integer id = 0; 
		Connection connection = null;
		
		try {	
			connection = ConnectionHelper.getConnection();
			connection.setAutoCommit(false);
			
			getInstance().deleteAsiento(demandaId,asiento,connection);
			
			connection.commit();
	  } catch (Exception e) {
		  _log.error(e);
		  ConnectionHelper.rollback(connection); 
	  } finally {
		 ConnectionHelper.cerrar(connection);
	  }    
	  return id;
	}
	
	
	public static Asiento getAsiento(
			Integer demandaId ,Integer asientoId,String entidadStr) throws Exception {
		int entidad=0;
		Asiento asiento = (Asiento) getInstance().getAsientosByIdDemanda_IdAsiento(demandaId, asientoId, null);
		if("U".equals(entidadStr)) {
			entidad =WebKeysGlobal.UOMA;
		}else if("O".equals(entidadStr)) {
			entidad=WebKeysGlobal.OSPIM;
		}else if("A".equals(entidadStr)) {
			entidad=WebKeysGlobal.AMTIMA;
		}
		
		
		for(Detalle d:asiento.getDetalle()) {
			PlanCuentas pc =TraeListasServiceUtil.getCuentaById(d.getCuenta().getId(), asiento.getFecha(), entidad);
			if(pc!=null) {
			  pc.setNumero(pc.getNumero()+ " " + pc.getCuenta()); 	
			  d.setCuenta(pc);
			}  
		}
		
		return asiento;
	}

	public static List<Asiento> getAsientosByDemandaId( Integer id) throws Exception {
		List<Asiento>asientos=getInstance().getAsientosByIdDemanda(id, null);
		return asientos;
	}
	
	public static List<Asiento> getAsientosByFechas( Date dde,Date hta, String entidad) throws Exception {
		List<Asiento>asientos=getInstance().getAsientosByFechas(entidad,dde,hta,null);
		return asientos;
	}
	
}

