package ar.com.ospim.autorizaciones.services;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portlet.documentlibrary.model.impl.DLFileEntryImpl;

import ar.com.ospim.automatico.Thread.EnviaEmailsThread;
import ar.com.ospim.autorizaciones.action.UploadArchivoSeguimientoSurAction;
import ar.com.ospim.autorizaciones.beans.BusquedaSeguimientoSurFiltro;
import ar.com.ospim.autorizaciones.beans.ComprobanteTratamientoDiscapacidad;
import ar.com.ospim.autorizaciones.beans.DrogaPatologia;
import ar.com.ospim.autorizaciones.beans.Nomenclador;
import ar.com.ospim.autorizaciones.beans.SeguimientoSur;
import ar.com.ospim.autorizaciones.beans.SeguimientoSurComprobante;
import ar.com.ospim.autorizaciones.beans.SeguimientoSurDetalle;
import ar.com.ospim.autorizaciones.beans.SeguimientoSurEstado;
import ar.com.ospim.autorizaciones.beans.SeguimientoSurLoteProcesado;
import ar.com.ospim.autorizaciones.beans.SeguimientoSurPrestador;
import ar.com.ospim.crm.beans.DerivacionNotificacion;
import ar.com.ospim.crm.services.CrmServiceUtil;
import ar.com.ospim.farmacia.beans.Medicamento;
import ar.com.ospim.global.beans.Comprobante;
import ar.com.ospim.tesoreria.beans.MovimientoBancario;
import ar.com.ospim.util.ConnectionHelper;

public class SeguimientoSurServiceUtil {
	@SuppressWarnings("unused")
	private static Log _log = LogFactoryUtil
			.getLog(SeguimientoSurServiceUtil.class);

	private static SeguimientoSurServiceImpl instance = null;

	public static SeguimientoSurServiceImpl getInstance() {
		if (null == instance) {
			instance = new SeguimientoSurServiceImpl();
		}
		return instance;
	}

	//Lista Seguimientos SUR
	public static List<SeguimientoSur> getListaSeguimientoSur(BusquedaSeguimientoSurFiltro filtro)
			throws SystemException {
		return getInstance().getListaSeguimientoSur(filtro);
		
	}
	
	@Deprecated
	//Lista Seguimientos SUR
	public static List<SeguimientoSur> getListaSeguimientoSur(int anio, int bimestre,int tipoExpediente,int autorizaOmint,String nroSolicitud,
			String codigoPresentado,String descripcionPresentado,String nroExpediente,String cuil,String inte,Date fechaDde, Date fechaHta,
			Boolean incluyeBajas,String estadoExpediente,String clase,String usuarioAlta,String estadoSSS , int claseNro,Date fechaCorresDde, 
			Date fechaCorresHta , int tipoTercerizadora , String nroCorrespondencia  , String  convenioTercerizadora, Date fechaEstadoDde,Date fechaEstadoHta , String estadoHisSSS,
			Date fechaDdeSur , Date fechaHtaSur)
			throws SystemException {
		return getInstance().getListaSeguimientoSur(anio,bimestre,tipoExpediente,autorizaOmint,nroSolicitud,codigoPresentado,descripcionPresentado,
				nroExpediente,cuil,inte,fechaDde,fechaHta,incluyeBajas,estadoExpediente,clase,usuarioAlta,estadoSSS,claseNro,fechaCorresDde,
				fechaCorresHta , tipoTercerizadora , nroCorrespondencia  , convenioTercerizadora,fechaEstadoDde,fechaEstadoHta, estadoHisSSS , fechaDdeSur , fechaHtaSur );
		
	}
		
	public static List<SeguimientoSur> getListaSeguimientoSurXls(BusquedaSeguimientoSurFiltro filtro)
			throws SystemException {
		return getInstance().getListaSeguimientoSurXls(filtro);
		
	}
	
	public static long insertaSeguimientoSur(SeguimientoSur seguimiento, String screenName) throws Exception {
		long idSeguimiento = 0; 
		Connection connection = null;
		Boolean esBaja=false;
		String motivoBaja="";
		
		try {			
			connection = ConnectionHelper.getConnection();
			connection.setAutoCommit(false);
		    // Alta del Seguimiento
			if(seguimiento.getTipoNomencladorId() ==null) seguimiento.setTipoNomencladorId(0); 
			idSeguimiento=getInstance().insertaSeguimiento(seguimiento, screenName,connection);
			
		    //Inserta Detalle
			for(SeguimientoSurDetalle d:seguimiento.getDetalles()){
				getInstance().insertaSeguimientoDetalle((int)idSeguimiento,d, screenName, connection);
			}
			
			
			//Inserta Estados
			Boolean enviaMail=false;
			String usrDestino="";
			String asunto="";
			String mensaje="";
			for(SeguimientoSurEstado d:seguimiento.getEstados()){
				Long idEstado = getInstance().insertaSeguimientoEstado((int)idSeguimiento,d, screenName, connection);
				d.setId(idEstado.intValue());
				if(getInstance().realizaBajaSeguimientoSUR(d.getIdEstado(),connection)) {
					esBaja=true;
					motivoBaja=d.getDescripcionMotivo();
				}
				Map<String,Object> datosEstado = getInstance().datosEstadoSeguimientoSUR(d.getIdEstado(),connection);
				enviaMail=Boolean.valueOf(datosEstado.get("enviaemail").toString());
				if(enviaMail){
					usrDestino=datosEstado.get("destinatarioemail").toString();
					asunto=datosEstado.get("asuntoemail").toString();
					mensaje= String.format(datosEstado.get("mensajeemail").toString(),
							seguimiento.getNro_solicitud_sur())  ;
				}
				
			}
			
			
			//Genera Baja Logica
			if(esBaja){
				getInstance().eliminaSeguimiento((int)idSeguimiento, screenName, motivoBaja, connection);
			}
			
			//Inserta Prestadores
			for(SeguimientoSurPrestador d:seguimiento.getPrestadores() ){
				getInstance().insertaSeguimientoPrestador((int)idSeguimiento,d, screenName, connection);
			}
			
			
			//Inserta Códigos Presentados
			for(Nomenclador d:seguimiento.getCodigosPresentados()){
				getInstance().insertaSeguimientoCodigoPresentado((int)idSeguimiento,d, screenName, connection);
			}
			
			
			for(SeguimientoSurComprobante c:seguimiento.getComprobantes()){
				getInstance().insertaSeguimientoComprobante((int)idSeguimiento,c, screenName, connection);
			}
/*	Comentado por cambio de comportamiento DISCAPACIDAD		
			if("DI".equalsIgnoreCase(seguimiento.getClaseExpediente())){
				//Inserta Tratamientos
				for(TratamientoDiscapacidadSeguimiento td:seguimiento.getTratamientos()){
					getInstance().insertaSeguimientoTratamiento((int)idSeguimiento, td, screenName, connection);
				}
				
				//Inserta Comprobantes de Tratamientos
				for(TratamientoDiscapacidadSeguimiento td:seguimiento.getTratamientos()){
					for(ComprobanteTratamientoDiscapacidad c:td.getComprobantes() ){
					   getInstance().insertaSeguimientoTratamientoComprobante((int)idSeguimiento,seguimiento, c, screenName, connection);
					}
				}
			}
			
*///Fin comentario
			
//Cambio Comportamiento reunion 06/2016			
/*			
			if("DI".equalsIgnoreCase(seguimiento.getClaseExpediente()) ||
			   "ME".equalsIgnoreCase(seguimiento.getClaseExpediente()) ||
			   "PR".equalsIgnoreCase(seguimiento.getClaseExpediente()) ||
			   "OT".equalsIgnoreCase(seguimiento.getClaseExpediente()) ||
			   "HI".equalsIgnoreCase(seguimiento.getClaseExpediente()) ||
			   "HE".equalsIgnoreCase(seguimiento.getClaseExpediente()) ||
			   "DR".equalsIgnoreCase(seguimiento.getClaseExpediente()) ||
			   "DB".equalsIgnoreCase(seguimiento.getClaseExpediente())){
               //Inserta Liquidaciones				
				for(ComprobanteTratamientoDiscapacidad td:seguimiento.getLiquidaciones() ){
					getInstance().insertaSeguimientoMedicamentoLiquidacion((int)idSeguimiento, td, screenName, connection);
				}
			}
*/
			
			if(!esBaja && !"".equals(usrDestino)){
				
				enviaEmailSeguimiento(usrDestino,asunto,mensaje);
				
                   
				
			}
			
			connection.commit();
	  } catch (Exception e) {
		  if(null!=connection){
			  connection.rollback();
			  throw e;
		  }			
	  } finally {
		 if (connection != null) {
			 ConnectionHelper.cerrar(connection);
		 }
	  }    
	  return idSeguimiento;
	}
	
    
	public static SeguimientoSur buscarSeguimientoSurPorId(int id) throws SystemException{
		
		return getInstance().buscarSeguimientoSurPorId(id,null);
		
	}
	
    public static SeguimientoSur buscarSeguimientoSurPorId(int id,Connection connectionParameter) throws SystemException{
		
		return getInstance().buscarSeguimientoSurPorId(id,connectionParameter);
		
	}
	
    public static long updateSeguimientoSur(SeguimientoSur seguimiento, String screenName) throws Exception {
		long idSeguimiento = seguimiento.getId(); 
		Connection connection = null;		
		Boolean esBaja=false;
		String motivoBaja="";
		
		try {			
			connection = ConnectionHelper.getConnection();
			connection.setAutoCommit(false);
			SeguimientoSur seguimientoDB = getInstance().buscarSeguimientoSurPorId(seguimiento.getId(),connection);
			if(seguimiento.getTipoNomencladorId() ==null) seguimiento.setTipoNomencladorId(0);
			//Inserta tratamientos
/* Comentado por cambio de comportamiento de Discapacidad
 			
			if("DI".equalsIgnoreCase(seguimiento.getClaseExpediente())){
				if(seguimiento.getTratamientos()!=null){
					//Analiza Altas - apareo con registros existentes en BD			
					for(TratamientoDiscapacidadSeguimiento td:seguimiento.getTratamientos()){
						Boolean existe=false;
						for(TratamientoDiscapacidadSeguimiento tdDB:seguimientoDB.getTratamientos()){
							if(td.getId_tratamiento()==tdDB.getId_tratamiento()){
								existe=true;
								Boolean existeComprobante=false;
								//Inserta comprobante no existente en tratamiento
								for(ComprobanteTratamientoDiscapacidad c:td.getComprobantes() ){
									existeComprobante=false;
									for(ComprobanteTratamientoDiscapacidad cDB:tdDB.getComprobantes()  ){
										if( c.getTratamientoId().equals(cDB.getTratamientoId()) &&
											c.getLiquidacionPrestacion().getId_liquidacion()==cDB.getLiquidacionPrestacion().getId_liquidacion() &&
											c.getLiquidacionPrestacion().getOrden()==cDB.getLiquidacionPrestacion().getOrden()){
											existeComprobante=true;
											break;
										}
									}
									
									if(!existeComprobante){
	                                    getInstance().insertaSeguimientoTratamientoComprobante((int)idSeguimiento,seguimiento, c, screenName, connection);
	                                }
								}
								
								//Elimina comprobante existente en DB y no en tratamiento
								for(ComprobanteTratamientoDiscapacidad cDB:tdDB.getComprobantes() ){
									existeComprobante=false;
									for(ComprobanteTratamientoDiscapacidad c:td.getComprobantes()  ){
										if( c.getTratamientoId().equals(cDB.getTratamientoId()) &&
											c.getLiquidacionPrestacion().getId_liquidacion()==cDB.getLiquidacionPrestacion().getId_liquidacion() &&
											c.getLiquidacionPrestacion().getOrden()==cDB.getLiquidacionPrestacion().getOrden()){
											existeComprobante=true;
											break;
										}
									}
									
									if(!existeComprobante){
										getInstance().eliminaSeguimientoTratamientoComprobante((int)idSeguimiento,seguimiento, cDB, screenName, connection);
	                                }
									
								}
								
								break;
							}
						}
						if(!existe){
				    	   getInstance().insertaSeguimientoTratamiento(seguimiento.getId(), td, screenName, connection);
                           //Actualiza comprobantes si el tratamiento es nuevo				    	   
							for(ComprobanteTratamientoDiscapacidad c:td.getComprobantes() ){
								   getInstance().insertaSeguimientoTratamientoComprobante((int)idSeguimiento,seguimiento, c, screenName, connection);
							}

				    	}
					}
					
					//Analiza Bajas - apareo con registros existentes en BD
					for(TratamientoDiscapacidadSeguimiento tdDB:seguimientoDB.getTratamientos()){
						Boolean existe=false;
						for(TratamientoDiscapacidad td:seguimiento.getTratamientos()){
							if(td.getId_tratamiento()== tdDB.getId_tratamiento()){
								existe=true;
								break;
							}
						}
						if(!existe){
							
						  for(ComprobanteTratamientoDiscapacidad cDB:tdDB.getComprobantes() ){
							 getInstance().eliminaSeguimientoTratamientoComprobante((int)idSeguimiento,seguimiento, cDB, screenName, connection);
						   }
							
				    	   getInstance().eliminaSeguimientoTratamiento(seguimiento.getId(), tdDB, screenName, connection) ;
			    	    }
					}
				}else{
					if(seguimientoDB.getTratamientos() != null && seguimientoDB.getTratamientos().size()>0){
						for(TratamientoDiscapacidadSeguimiento tdDB:seguimientoDB.getTratamientos()){
							for(ComprobanteTratamientoDiscapacidad cDB:tdDB.getComprobantes() ){
								 getInstance().eliminaSeguimientoTratamientoComprobante((int)idSeguimiento,seguimiento, cDB, screenName, connection);
							}
							
							//Marcar con fecha de baja todos los Tratamientos
							 getInstance().eliminaSeguimientoTratamiento(seguimiento.getId(), tdDB, screenName, connection) ;
						}
					}
				}
			}
*/
			
			if("DI".equalsIgnoreCase(seguimiento.getClaseExpediente()) ||
					   "ME".equalsIgnoreCase(seguimiento.getClaseExpediente()) ||
					   "PR".equalsIgnoreCase(seguimiento.getClaseExpediente()) ||
					   "OT".equalsIgnoreCase(seguimiento.getClaseExpediente()) ||
					   "HI".equalsIgnoreCase(seguimiento.getClaseExpediente()) ||
					   "HE".equalsIgnoreCase(seguimiento.getClaseExpediente()) ||
					   "DR".equalsIgnoreCase(seguimiento.getClaseExpediente()) ||
					   "FE".equalsIgnoreCase(seguimiento.getClaseExpediente()) ||
					   "DB".equalsIgnoreCase(seguimiento.getClaseExpediente()) ||
					   "CO".equalsIgnoreCase(seguimiento.getClaseExpediente()) ){
				if(seguimiento.getComprobantes()!=null){
						//Analiza Altas - apareo con registros existentes en BD			
						for(SeguimientoSurComprobante td:seguimiento.getComprobantes() ){
							Boolean existe=false;
							for(SeguimientoSurComprobante tdDB:seguimientoDB.getComprobantes()){
								if( td.getCuit().equalsIgnoreCase(tdDB.getCuit()) &&
									td.getTipoComprobante().equalsIgnoreCase(tdDB.getTipoComprobante()) &&
									td.getLetraComprobante().equalsIgnoreCase(tdDB.getLetraComprobante()) &&
									td.getPtoVenta()== tdDB.getPtoVenta() &&
									td.getSucuComprobante()== tdDB.getSucuComprobante() &&
									td.getNroComprobante().equalsIgnoreCase(tdDB.getNroComprobante()) &&
									td.getAcreedorEmpresa().getSucursal().equalsIgnoreCase(tdDB.getAcreedorEmpresa().getSucursal())
									){
									existe=true;
									break;
								}
							}
							if(!existe){
							   getInstance().insertaSeguimientoComprobante(seguimiento.getId(), td, screenName, connection);
					    	}
						}
						
						//Analiza Bajas - apareo con registros existentes en BD
						for(SeguimientoSurComprobante tdDB:seguimientoDB.getComprobantes() ){
							Boolean existe=false;
							for(SeguimientoSurComprobante td:seguimiento.getComprobantes()){
								if(td.getCuit().equalsIgnoreCase(tdDB.getCuit()) &&
								   td.getTipoComprobante().equalsIgnoreCase(tdDB.getTipoComprobante()) &&
								   td.getLetraComprobante().equalsIgnoreCase(tdDB.getLetraComprobante()) &&
								   td.getPtoVenta()== tdDB.getPtoVenta() &&
								   td.getSucuComprobante()== tdDB.getSucuComprobante() &&
								   td.getNroComprobante().equalsIgnoreCase(tdDB.getNroComprobante()) &&
								   td.getAcreedorEmpresa().getSucursal().equalsIgnoreCase(tdDB.getAcreedorEmpresa().getSucursal())){
									existe=true;
									break;
								}
							}
							if(!existe){
					    	   getInstance().eliminaSeguimientoComprobante(seguimiento.getId(),tdDB, screenName, connection) ;
				    	    }
						}
				}else{
					if(seguimientoDB.getComprobantes() != null && seguimientoDB.getComprobantes().size()>0){
						for(SeguimientoSurComprobante tdDB:seguimientoDB.getComprobantes()){
							getInstance().eliminaSeguimientoComprobante(seguimiento.getId(),tdDB, screenName, connection) ;
						}
					}
				}
			}
			
			//Inserta medicamentos
/* Cambio de comportamiento Reunion 06/2016			
			
			if(//"DI".equalsIgnoreCase(seguimiento.getClaseExpediente()) ||
			   "ME".equalsIgnoreCase(seguimiento.getClaseExpediente()) ||
			   "PR".equalsIgnoreCase(seguimiento.getClaseExpediente()) ||
			   "OT".equalsIgnoreCase(seguimiento.getClaseExpediente()) ||
			   "HI".equalsIgnoreCase(seguimiento.getClaseExpediente()) ||
			   "HE".equalsIgnoreCase(seguimiento.getClaseExpediente()) ||
			   "DR".equalsIgnoreCase(seguimiento.getClaseExpediente()) ||
			   "DB".equalsIgnoreCase(seguimiento.getClaseExpediente())){	
				if(seguimiento.getLiquidaciones() !=null){
					//Analiza Altas - apareo con registros existentes en BD			
					for(ComprobanteTratamientoDiscapacidad td:seguimiento.getLiquidaciones() ){
						Boolean existe=false;
						for(ComprobanteTratamientoDiscapacidad tdDB:seguimientoDB.getLiquidaciones() ){
							if(td.getLiquidacionPrestacion().getId_liquidacion() ==tdDB.getLiquidacionPrestacion().getId_liquidacion() &&
							   td.getLiquidacionPrestacion().getId_prestacion() == tdDB.getLiquidacionPrestacion().getId_prestacion() &&
							   td.getLiquidacionPrestacion().getOrden()== tdDB.getLiquidacionPrestacion().getOrden()){
								existe=true;
								break;
							}
						}
						if(!existe){
						   getInstance().insertaSeguimientoMedicamentoLiquidacion(seguimiento.getId(), td, screenName, connection);	
				    	}
					}
					
					//Analiza Bajas - apareo con registros existentes en BD
					for(ComprobanteTratamientoDiscapacidad tdDB:seguimientoDB.getLiquidaciones() ){
						Boolean existe=false;
						for(ComprobanteTratamientoDiscapacidad td:seguimiento.getLiquidaciones() ){
							if(td.getLiquidacionPrestacion().getId_liquidacion() ==tdDB.getLiquidacionPrestacion().getId_liquidacion() &&
							   td.getLiquidacionPrestacion().getId_prestacion() == tdDB.getLiquidacionPrestacion().getId_prestacion() &&
							   td.getLiquidacionPrestacion().getOrden()== tdDB.getLiquidacionPrestacion().getOrden()){
								existe=true;
								break;
							}
						}
						if(!existe){
				    	   getInstance().eliminaSeguimientoMedicamentoLiquidacion(seguimiento.getId(), tdDB, screenName, connection) ;
			    	    }
					}
				}else{
					if(seguimientoDB.getLiquidaciones()  != null && seguimientoDB.getLiquidaciones().size()>0){
						for(ComprobanteTratamientoDiscapacidad tdDB:seguimientoDB.getLiquidaciones()){
							//Marcar con fecha de baja todos las Liquidaciones
							 getInstance().eliminaSeguimientoMedicamentoLiquidacion(seguimiento.getId(), tdDB, screenName, connection) ;
						}
					}
				}
			}
*/
			
			//Inserta detalles
			if(seguimiento.getDetalles()!=null){
				//Analiza Altas - apareo con registros existentes en BD			
				for(SeguimientoSurDetalle td:seguimiento.getDetalles()){
					Boolean existe=false;
					for(SeguimientoSurDetalle tdDB:seguimientoDB.getDetalles()){
						if(td.getId().equals(tdDB.getId())){
							existe=true;
							getInstance().updateSeguimientoDetalle(td.getId(), td, screenName, connection);
							break;
						}
					}
					if(!existe){
			    	   getInstance().insertaSeguimientoDetalle(seguimiento.getId(), td, screenName, connection);
			    	}
				}
				
				//Analiza Bajas - apareo con registros existentes en BD
				for(SeguimientoSurDetalle tdDB:seguimientoDB.getDetalles()){
					Boolean existe=false;
					for(SeguimientoSurDetalle td:seguimiento.getDetalles()){
						if(td.getId().equals(tdDB.getId())){
							existe=true;
							break;
						}
					}
					if(!existe){
			    	   getInstance().eliminaSeguimientoDetalle(tdDB.getId(), screenName, connection) ;
		    	    }
				}
			}else{
				if(seguimientoDB.getDetalles() != null && seguimientoDB.getDetalles().size()>0){
					for(SeguimientoSurDetalle tdDB:seguimientoDB.getDetalles()){
						//Marcar con fecha de baja todos los Tratamientos
						 getInstance().eliminaSeguimientoDetalle(tdDB.getId(), screenName, connection) ;
					}
				}
			}
			
			
			//Inserta estados
			Boolean enviaMail=false;
			String usrDestino="";
			String asunto="";
			String mensaje="";
			if(seguimiento.getEstados()!=null){
				//Analiza Altas - apareo con registros existentes en BD			
				for(SeguimientoSurEstado td:seguimiento.getEstados()){
					Boolean existe=false;
					for(SeguimientoSurEstado tdDB:seguimientoDB.getEstados()){
						if(td.getId().equals(tdDB.getId())){
							existe=true;
							getInstance().updateSeguimientoEstado(td.getId(), td, screenName, connection);
							break;
						}
					}
					if(!existe){
			    	   Long nIdEstado=getInstance().insertaSeguimientoEstado(seguimiento.getId(), td, screenName, connection);
			    	   td.setId(nIdEstado.intValue());
			    	   if(getInstance().realizaBajaSeguimientoSUR(td.getIdEstado(),connection)) {
			    		   esBaja=true;
			    		   motivoBaja=td.getDescripcionMotivo();
			    	   }
			    	   
//DS Agrego envio de mail
			    	   Map<String,Object> datosEstado = getInstance().datosEstadoSeguimientoSUR(td.getIdEstado(),connection);
					   enviaMail=Boolean.valueOf(datosEstado.get("enviaemail").toString());
					   if(enviaMail){
							usrDestino=datosEstado.get("destinatarioemail").toString();
							asunto=datosEstado.get("asuntoemail").toString();
							mensaje= String.format(datosEstado.get("mensajeemail").toString(),
									seguimiento.getNro_solicitud_sur())  ;
					   }
			    	}
				}
				
				//Analiza Bajas - apareo con registros existentes en BD
				for(SeguimientoSurEstado tdDB:seguimientoDB.getEstados()){
					Boolean existe=false;
					for(SeguimientoSurEstado td:seguimiento.getEstados()){
						if(td.getId().equals(tdDB.getId())){
							existe=true;
							break;
						}
					}
					if(!existe){
			    	   getInstance().eliminaSeguimientoEstado(tdDB.getId(), screenName, connection) ;
		    	    }
				}
			}else{
				if(seguimientoDB.getEstados() != null && seguimientoDB.getEstados().size()>0){
					for(SeguimientoSurEstado tdDB:seguimientoDB.getEstados()){
						//Marcar con fecha de baja todos los Tratamientos
						 getInstance().eliminaSeguimientoEstado(tdDB.getId(), screenName, connection) ;
					}
				}
			}
			
			//Genera Baja Logica
			if(esBaja){
				getInstance().eliminaSeguimiento(seguimiento.getId(), screenName, motivoBaja, connection);
			}else{
				getInstance().recuperaSeguimiento(seguimiento.getId(), screenName, connection);
			}
			
			//Inserta Prestadores
			if(seguimiento.getPrestadores()!=null){
				//Analiza Altas - apareo con registros existentes en BD			
				for(SeguimientoSurPrestador td:seguimiento.getPrestadores()){
					Boolean existe=false;
					for(SeguimientoSurPrestador tdDB:seguimientoDB.getPrestadores()){
						if(td.getId().equals(tdDB.getId())){
							existe=true;
							getInstance().updateSeguimientoPrestador(td.getId(), td, screenName, connection);
							break;
						}
					}
					if(!existe){
			    	   getInstance().insertaSeguimientoPrestador(seguimiento.getId(), td, screenName, connection);
			    	}
				}
				
				//Analiza Bajas - apareo con registros existentes en BD
				for(SeguimientoSurPrestador tdDB:seguimientoDB.getPrestadores()){
					Boolean existe=false;
					for(SeguimientoSurPrestador td:seguimiento.getPrestadores()){
						if(td.getId().equals(tdDB.getId())){
							existe=true;
							break;
						}
					}
					if(!existe){
			    	   getInstance().eliminaSeguimientoPrestador(tdDB.getId(), screenName, connection) ;
		    	    }
				}
			}else{
				if(seguimientoDB.getPrestadores() != null && seguimientoDB.getPrestadores().size()>0){
					for(SeguimientoSurPrestador tdDB:seguimientoDB.getPrestadores()){
						 getInstance().eliminaSeguimientoPrestador(tdDB.getId(), screenName, connection) ;
					}
				}
			}
			
			
			
			///------
			//-------
			//Inserta Prestaciones Nomenclador
			if(seguimiento.getCodigosPresentados()!=null){
				//Analiza Altas - apareo con registros existentes en BD			
				for(Nomenclador td:seguimiento.getCodigosPresentados() ){
					Boolean existe=false;
					for(Nomenclador tdDB:seguimientoDB.getCodigosPresentados()){
						if(td.getId_prestacion() == tdDB.getId_prestacion()){
							existe=true;
							break;
						}
					}
					if(!existe){
			    	   getInstance().insertaSeguimientoCodigoPresentado(seguimiento.getId(), td, screenName, connection);
			    	}
				}
				
				//Analiza Bajas - apareo con registros existentes en BD
				for(Nomenclador tdDB:seguimientoDB.getCodigosPresentados() ){
					Boolean existe=false;
					for(Nomenclador td:seguimiento.getCodigosPresentados()){
						if(td.getId_prestacion()==tdDB.getId_prestacion() ){
							existe=true;
							break;
						}
					}
					if(!existe){
			    	   getInstance().eliminaSeguimientoCodigoPresentado(seguimiento.getId(),tdDB, screenName, connection) ;
		    	    }
				}
			}else{
				if(seguimientoDB.getCodigosPresentados() != null && seguimientoDB.getCodigosPresentados().size()>0){
					for(Nomenclador tdDB:seguimientoDB.getCodigosPresentados()){
						getInstance().eliminaSeguimientoCodigoPresentado(seguimiento.getId(),tdDB, screenName, connection) ;
					}
				}
			}
			
			// Modifica Seguimiento
			idSeguimiento=getInstance().updateSeguimiento(seguimiento,screenName,connection);
			
			if(!esBaja && !"".equals(usrDestino)){
				enviaEmailSeguimiento(usrDestino,asunto,mensaje);
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
	  return idSeguimiento;
	}
    
    
    public static Boolean existeSeguimientoSurPorBimestre(String cuil,String inte, String bimestre) throws SystemException{
		return getInstance().existeSeguimientoSURBimestre(cuil, inte, bimestre) ;
	}
    
    public static Boolean existeMovimientoBancoSeguimientoSur(String nroExpediente) throws SystemException{
		return getInstance().existeMovimientoBancoSeguimientoSUR(nroExpediente) ;
	}
    
    public static long eliminaSeguimiento(int idSeguimiento, String screenName,String motivo) throws Exception {
    	
		try {			
		    // Baja del Seguimiento
			getInstance().eliminaSeguimiento(idSeguimiento, screenName,motivo,null);
	  } catch (Exception e) {
		 	_log.error("Error al Eliminar Seguimiento");
		 	_log.error(e);
	  }  
	  return idSeguimiento;
	}
    
    public static long recuperaSeguimiento(int idSeguimiento, String screenName) throws Exception {    	
		
		try {			
		    // Recupera Seguimiento dado de Baja
			getInstance().recuperaSeguimiento(idSeguimiento, screenName,null);
	  } catch (Exception e) {
			_log.error("Error al Recuperar Seguimiento");
		 	_log.error(e);
	  }    
	  return idSeguimiento;
	}
    
    public static List<ComprobanteTratamientoDiscapacidad> recuperaComprobantesTratamientos(String cuil,int inte,int prestacion ,Date periodo_dde,Date periodo_hta,String cuitPrestador) throws Exception {
    	List<ComprobanteTratamientoDiscapacidad> ret=new ArrayList<ComprobanteTratamientoDiscapacidad>();

		try {			
		    ret=getInstance().recuperaComprobantesTratamientos(cuil, inte, prestacion,periodo_dde, periodo_hta,cuitPrestador,null,null);
	  } catch (Exception e) {
			_log.error("Error al Recuperar Comprob. Tratamientos");
		 	_log.error(e);
	  }    
	  return ret;
	}
    
    public static List<ComprobanteTratamientoDiscapacidad> recuperaComprobantesTratamientos(String cuil,int inte,int prestacion ,Date periodo_dde,Date periodo_hta,
    		      String cuitPrestador,String descPrestador,Integer idDroga) throws Exception {
    	List<ComprobanteTratamientoDiscapacidad> ret=new ArrayList<ComprobanteTratamientoDiscapacidad>();

		try {			
		    ret=getInstance().recuperaComprobantesTratamientos(cuil, inte, prestacion,periodo_dde, periodo_hta,cuitPrestador,descPrestador,idDroga);
	  } catch (Exception e) {
			_log.error("Error al Recuperar Comprobantes Tratamientos");
		 	_log.error(e);
	  }    
	  return ret;
	}
    
    public static long cierraSeguimiento(int idSeguimiento, Date fechaCierre,String motivoCierre) throws Exception {    	

		try {			
		    // Baja del Seguimiento
			getInstance().cierraSeguimiento(idSeguimiento, fechaCierre,motivoCierre,null);
	  } catch (Exception e) {
			_log.error("Error al Cierra Seguimiento");
		 	_log.error(e);
	  }    
	  return idSeguimiento;
	}
    
    public static MovimientoBancario traeMovimientoBancoSeguimientoSur(String nroExpediente) throws SystemException{
		return getInstance().traeMovimientoBancoSeguimientoSUR(nroExpediente) ;
	}
    
    public static Date[] traeFechasBimestreSeguimientoSur(Integer idBimestre) throws SystemException{
		return getInstance().traeFechasBimestreSeguimientoSur(idBimestre);
	}
    
    public static ComprobanteTratamientoDiscapacidad recuperaLiquidacionPrestacion(int idLiquidacion,int prestacion ,int orden) throws Exception {
      ComprobanteTratamientoDiscapacidad ret=new ComprobanteTratamientoDiscapacidad();
	
	  try {			
	    ret=getInstance().recuperaLiquidacionPrestacion(idLiquidacion, prestacion, orden);
      } catch (Exception e) {
    	_log.error("Error al Recuperar Liquidacion Prestacion");
		_log.error(e);
      }    
      return ret;
   }
    
    public static List<Medicamento> getBusquedaProtesisYOtros(int troquel,
			String nombre) {
		List<Medicamento> medicamentos = getInstance().getBusquedaProtesisYOtros(
				troquel, nombre);
		return medicamentos;
	}
    
    public static List<DrogaPatologia> traeDrogasPatologia(int id) {
		List<DrogaPatologia> drogas = getInstance().traeDrogasPatologia(id);
		return drogas;
	}
    
    public static List<DrogaPatologia> traePatologias(int id) {
		List<DrogaPatologia> drogas = getInstance().traePatologias(id);
		return drogas;
	}
    
    public static List<DrogaPatologia> traeNormasSeguimientoSur(int id) {
		List<DrogaPatologia> normas = getInstance().traeNormasSeguimientoSur(id);
		return normas;
	}
    
    public static List<Medicamento> getBusquedaMedicamentos(int troquel,
			String nombre,int patologia) {
    	
 //   	DrogaPatologia droga=new DrogaPatologia();
    	List<Medicamento> result = new ArrayList<Medicamento>();
    	if(patologia!=0){
    	   List<DrogaPatologia> drogas = SeguimientoSurServiceUtil.traeDrogasPatologia(patologia);
    	   if(drogas!=null){
    		   
			   List<Medicamento> medicamentos = NomencladorServiceUtil.getBusquedaMedicamentos(troquel, nombre); 
			   for(Medicamento m:medicamentos){
				   for(DrogaPatologia droga:drogas){
				      if(droga.getDrogaDescripcion()!=null && m.getDroga().trim().equalsIgnoreCase(droga.getDrogaDescripcion().trim())){
					     result.add(m);
					     break;
				      }
				   }   
			   }

    		   
/*    		   
	    	   if(drogas!=null && drogas.size()>0){
	    		  droga=drogas.get(0);
	    	   }
			   List<Medicamento> medicamentos = NomencladorServiceUtil.getBusquedaMedicamentos(troquel, nombre); 
			   for(Medicamento m:medicamentos){
				  if(m.getDroga().trim().equalsIgnoreCase(droga.getDrogaDescripcion().trim())){
					  result.add(m);
				  }
			   }
*/			   
    	   }
    	}else{
    	  result = NomencladorServiceUtil.getBusquedaMedicamentos(troquel, nombre);	
    	}
    	return result;
	}
    
    public static Boolean existeSeguimientoSurNroExpediente(String nroExpediente,Integer idSeguimiento) throws SystemException{
		return getInstance().existeSeguimientoSURNroExpediente(nroExpediente, idSeguimiento);
	}
    
    public static long updateComprobanteSeguimientoSur(SeguimientoSur seguimiento, String screenName) throws Exception {
		long idSeguimiento = 0; 
	
		try {			
			// Modifica Seguimiento
			idSeguimiento=getInstance().updateComprobanteSeguimiento(seguimiento,screenName,null);
	  } catch (Exception e) {
			_log.error("Error al Actualizar Seguimiento");
		 	_log.error(e);
	  }    
	  return idSeguimiento;
	}
    
    public static List<Medicamento> getBusquedaDrogadependencia(int troquel,
			String nombre) {
		List<Medicamento> medicamentos = getInstance().getBusquedaDrogadependencia(
				troquel, nombre);
		return medicamentos;
	}

    private static void enviaEmailSeguimiento(String usrDestino,String asunto,String mensaje) throws SystemException{
    	List<String> direc = new ArrayList<String>();
    	String[] usuarios =usrDestino.split(";");
    	if(usuarios.length>0){
    	   for(int i=0;i<usuarios.length;i++){	
    	      DerivacionNotificacion dv = CrmServiceUtil.getNotificacionDerivacion(usuarios[i]);
    	      String eMail="";
    	      if(dv!=null){
    		    eMail=dv.getDerivacionEmail();
    	      }
    	      direc.add(eMail);
    	   }   
    	}else{
    		DerivacionNotificacion dv = CrmServiceUtil.getNotificacionDerivacion(usrDestino);
    		String eMail="";
    		if(dv!=null){
    		  eMail=dv.getDerivacionEmail();
    		}
    		direc.add(eMail);
    	}
    	
    	if(direc.size()>0){
    	   EnviaEmailsThread.enviarMailDesatendido(asunto, mensaje, direc,1);
    	}
    	
    }
    
    public static SeguimientoSurEstado ultimoEstadoSeguimientoSUR(int idSeguimiento) throws Exception{
    	  SeguimientoSurEstado ret=new SeguimientoSurEstado();

		  try {			
		    ret=getInstance().ultimoEstadoSeguimientoSUR(idSeguimiento, null);
	      } catch (Exception e) {
	    		_log.error("Error al obtener ultimo estado del Seguimiento");
			 	_log.error(e);
	      }    
	      return ret;
    }
    
    
    public static List<SeguimientoSurComprobante> recuperaComprobantesLiquidados(
    		Integer idPrestador,
			String cuit,
			String razonSocial,
			String tipo,
			String letra,
			Integer ptoVta,
			String nro,
			Date fechaEmision,
			Date fechaRecibido,
            Date fechaVencimiento) throws Exception {
    	
    	List<SeguimientoSurComprobante> ret=new ArrayList<SeguimientoSurComprobante>();

		try {			
		    ret=getInstance().recuperaComprobantesLiquidados(idPrestador, cuit, razonSocial, tipo, letra, ptoVta, nro, fechaEmision, fechaRecibido, fechaVencimiento);
		    
	  } catch (Exception e) {
			_log.error("Error al Recuperar Comprob. Liquidados");
		 	_log.error(e);
	  }    
	  return ret;
	}
    
    public static Comprobante getComprobante(Comprobante comp, int entidad)
			throws Exception {

		Comprobante comprobante = getInstance().getComprobante(comp, entidad);
		return comprobante;
	}

    public static Boolean existePatologiaSur(String descripcion) throws SystemException{
		return getInstance().existePatologiaSur(descripcion);
	}
    
    public static Long insertaPatologia(String descripcion, String screenName) throws SystemException, SQLException{
		return getInstance().insertaPatologia(descripcion,screenName,null);
	}
    
    public static List<DLFileEntryImpl> getImagenesSeguimientoSur(String titulo) throws SystemException{
		return getInstance().getImagenesSeguimientoSur(titulo);
	}
    
    public static List<SeguimientoSurComprobante> buscarComprobantesLiquidadosSeguimientoSurPorId(
			int id,Connection connectionParameter) throws SystemException {
    	return getInstance().buscarComprobantesLiquidadosSeguimientoSurPorId(id,connectionParameter);
    }
    
    public static long buscarIdSeguimientoByNroExpediente(String nroExpdte,Connection connectionParameter) throws SystemException, SQLException {
    	return getInstance().buscarIdSeguimientoByNroExpediente(nroExpdte,connectionParameter);
    }
    
    public static Integer proximoNroLotePago(Connection connectionParameter) throws SystemException, SQLException {
    	return getInstance().proximoNroLotePago(connectionParameter);
    }
    
    public static long imputaPagoSeguimiento(List<SeguimientoSur> novedades,String tipo,Integer nroLotep,String screenName,
    		Connection connectionParameter) throws Exception {    	

    	long idSeguimiento = 0L; 
		Connection connection = null;		
		
		try {			
			
			if (connectionParameter == null) {
				connection = ConnectionHelper.getConnection();
			} else {
				connection = connectionParameter;
			}
			connection.setAutoCommit(false);
			Integer nroLote=nroLotep;
			if(nroLotep == null){
			   nroLote=getInstance().proximoNroLotePago(connection);
			}   
			for(SeguimientoSur seguimiento:novedades){
			   if("IMP".equalsIgnoreCase(tipo)){	
			     idSeguimiento=getInstance().imputaPagoSeguimiento(seguimiento, screenName, connection);
			   }  
			   getInstance().guardarLoteNovedades(nroLote, "PAGOS", seguimiento, screenName, connection); 
			}   
			
			connection.commit();
	  } catch (Exception e) {
		  ConnectionHelper.rollback(connection);		
	  } finally {
		  ConnectionHelper.cerrar(connection);
	  }    
	  return idSeguimiento;
	}
    
    public static  List<SeguimientoSur> traePorUltimoEstado(String estadoSSS,Date fechaDesde,Date fechaHasta) throws SystemException{
    	return getInstance().traePorUltimoEstado(estadoSSS, fechaDesde, fechaHasta);
    }
    
    public static List<SeguimientoSurLoteProcesado> lotesProcesadosAdelantos() throws SystemException{
    	return getInstance().lotesProcesadosAdelantos();
    }
    
    public static List<SeguimientoSur> lotesProcesadosAdelantosDetalle(Integer nroLote,String tipo) throws SystemException{
    	return getInstance().lotesProcesadosAdelantosDetalle(nroLote, tipo);
    }
    
    public static void actualizarPagoProporcional(List<SeguimientoSur> novedades, Integer nroLotep,
    		String screenName, int idEstado, Connection connectionParameter) throws Exception {    	

		Connection connection = null;		
		
		try {			
			
			if (connectionParameter == null) {
				connection = ConnectionHelper.getConnection();
			} else {
				connection = connectionParameter;
			}
			connection.setAutoCommit(false);
			Integer nroLote=nroLotep;
			if(nroLotep == null){
			   nroLote=getInstance().proximoNroLotePago(connection);
			}   
			for(SeguimientoSur seguimiento:novedades){
			   if("NUE".equalsIgnoreCase(seguimiento.getTipoRegistro())){	
				   getInstance().actualizaPagoProporcional(seguimiento, screenName, idEstado, connection);
			   }
			   if(idEstado == UploadArchivoSeguimientoSurAction.EN_ANALISIS_SUR) {
				   getInstance().guardarLoteNovedades(nroLote, "ENANALISISSUR", seguimiento, screenName, connection); 
			   }else if(idEstado == UploadArchivoSeguimientoSurAction.PENDIENTE_DE_PAGO) {
				   getInstance().guardarLoteNovedades(nroLote, "ANALISIS", seguimiento, screenName, connection);
			   }	   
			}   
			
			connection.commit();
	  } catch (Exception e) {
		  ConnectionHelper.rollback(connection);	
		  throw e;	
	  } finally {
		  ConnectionHelper.cerrar(connection);
	  }    
	}
    
    public static void updateEstadosDesdeListaSeguimientoSur (List<SeguimientoSur> seguimientos, 	String screenName, Connection connectionParameter) throws Exception {    	

		Connection connection = null;		
		
		try {			
		
			connection = connectionParameter;
		
			for(SeguimientoSur seguimiento:seguimientos){
			     if (seguimiento.getTipoRegistro().equals("EXI")  && seguimiento.getId()>0 && seguimiento.getEstados().get(0)!=null && seguimiento.getEstados().get(0).getIdEstado()>0 ){
			    	 Long idEstado = getInstance().insertaSeguimientoEstado(seguimiento.getId() ,seguimiento.getEstados().get(0) , screenName, connection);	 
			     }
			}   
	
	  } catch (Exception e) {
		  _log.error(e);
		  ConnectionHelper.rollback(connection);	
		  throw e;	
	  } 
		   
	}
    

    
    
    public static void actualizarPagoImputado(List<SeguimientoSur> novedades, Integer nroLotep,
    		String screenName, int idEstado, Connection connectionParameter) throws Exception {    	

		Connection connection = null;		
		
		try {			
			
			if (connectionParameter == null) {
				connection = ConnectionHelper.getConnection();
			} else {
				connection = connectionParameter;
			}
			connection.setAutoCommit(false);
			Integer nroLote=nroLotep;
			if(nroLotep == null){
			   nroLote=getInstance().proximoNroLotePago(connection);
			}   
			for(SeguimientoSur seguimiento:novedades){
			   if("NUE".equalsIgnoreCase(seguimiento.getTipoRegistro())){	
				   getInstance().actualizaPagoImputado(seguimiento, screenName, idEstado, connection);
			   }
			   if(idEstado == UploadArchivoSeguimientoSurAction.PAGO_IMPUTADO){
				   getInstance().guardarLoteNovedades(nroLote, "PAGOS", seguimiento, screenName, connection);
			   }else if(idEstado == UploadArchivoSeguimientoSurAction.PAGADO_POR_MOV_BANCARIO){
				   getInstance().guardarLoteNovedades(nroLote, "MOV_BCRIOS", seguimiento, screenName, connection);
			   }else if(idEstado == UploadArchivoSeguimientoSurAction.CAMBIO_MASIVO_DE_ESTADO ){				   
				   getInstance().guardarLoteNovedades(nroLote, "CAMBIO_MASIVO_ESTADOS", seguimiento, screenName, connection);
			   }
			}   
			if (connectionParameter == null) {
				connection.commit();	
			}
	  } catch (Exception e) {
		  _log.error(e);
		  ConnectionHelper.rollback(connection);	
		  throw e;	
	  } finally {
		  //ConnectionHelper.cerrar(connection);
		  if (connectionParameter == null) {
				ConnectionHelper.cerrar(connection);			
		  }
	  }    
	}
    
    public static List<SeguimientoSur> getListaSeguimientoSurImputados(int anio, int bimestre,int tipoExpediente,int autorizaOmint,String nroSolicitud,
			String codigoPresentado,String descripcionPresentado,String nroExpediente,String cuil,String inte,Date fechaDde, Date fechaHta,
			Boolean incluyeBajas,String estadoExpediente,String clase,String usuarioAlta,String estadoSSS,Date fechaDdePago, Date fechaHtaPago)
			throws SystemException {
		return getInstance().getListaSeguimientoSurImputados(anio,bimestre,tipoExpediente,autorizaOmint,nroSolicitud,codigoPresentado,descripcionPresentado,
				nroExpediente,cuil,inte,fechaDde,fechaHta,incluyeBajas,estadoExpediente,clase,usuarioAlta,estadoSSS,fechaDdePago,fechaHtaPago);
		
	}
    
    
    public static void cambiaEstadoListaSeguimientoSur(List<SeguimientoSur> novedades,String screenName, Connection connectionParameter) throws Exception {    	

		Connection connection = null;		
		
		try {			
			
			if (connectionParameter == null) {
				connection = ConnectionHelper.getConnection();
			} else {
				connection = connectionParameter;
			}
			connection.setAutoCommit(false);

			   
			for(SeguimientoSur seguimiento:novedades){
			   if("NUE".equalsIgnoreCase(seguimiento.getTipoRegistro())){	
				   getInstance().actualizaPagoProporcional(seguimiento, screenName, 1, connection);
			   }			   
			   getInstance().guardarLoteNovedades(1, "ANALISIS", seguimiento, screenName, connection); 
			}   
			
			connection.commit();
	  } catch (Exception e) {
		  _log.error(e);
		  ConnectionHelper.rollback(connection);	
		  throw e;	
	  } finally {
		  if (connectionParameter == null) {
			  ConnectionHelper.cerrar(connection);
		  }    
	  }
		
    }
    
}

