package ar.com.ospim.autorizaciones.services;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;
import javax.mail.util.ByteArrayDataSource;

import org.apache.commons.io.IOUtils;

//import com.google.api.services.gmail.Gmail;
//import com.google.api.services.gmail.model.Message;
import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.MimeTypesUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portlet.documentlibrary.model.DLFileEntry;
import com.liferay.portlet.documentlibrary.model.DLFolder;
import com.liferay.portlet.documentlibrary.model.impl.DLFileEntryImpl;
import com.liferay.portlet.documentlibrary.service.DLFileEntryLocalServiceUtil;
import com.liferay.portlet.documentlibrary.service.DLFolderLocalServiceUtil;

import ar.com.ospim.afiliados.NoSuchAfiliadoEntryException;
import ar.com.ospim.afiliados.beans.Afiliado;
import ar.com.ospim.afiliados.services.EditarAfiliadoServiceUtil;
//import ar.com.global.services.GmailAPIUtil;
import ar.com.ospim.afiliados.services.SeccionalServiceUtil;
import ar.com.ospim.automatico.ReportesScheduler.ReportesAutomaticosConfiguracion;
import ar.com.ospim.automatico.Thread.EnviaEmailsThread;
import ar.com.ospim.automatico.service.ReportesServiceUtil;
import javax.script.ScriptEngine; 
import javax.script.ScriptEngineManager; 
import javax.script.ScriptException; 
import ar.com.ospim.autorizaciones.action.UploadArchivoPreautorizacionesAction.ArchivoPrevencion;
import ar.com.ospim.autorizaciones.beans.CuentasInterbaking;
import ar.com.ospim.autorizaciones.beans.Estado;
import ar.com.ospim.autorizaciones.beans.IntegracionCabeceraDR;
import ar.com.ospim.autorizaciones.beans.IntegracionCabeceraDS;
import ar.com.ospim.autorizaciones.beans.IntegracionDetalleDR;
import ar.com.ospim.autorizaciones.beans.IntegracionDetalleDS;
import ar.com.ospim.autorizaciones.beans.IntegracionReglasValidacion;
import ar.com.ospim.autorizaciones.beans.Nomenclador;
import ar.com.ospim.autorizaciones.beans.PagosInterbanking;
import ar.com.ospim.autorizaciones.beans.PreAutorizacion;
import ar.com.ospim.autorizaciones.beans.PreAutorizacionLoteProcesado;
import ar.com.ospim.autorizaciones.beans.PreAutorizacionPrestacion;
import ar.com.ospim.autorizaciones.beans.PrestacionesReclamo;
import ar.com.ospim.autorizaciones.beans.ReclamoPrestacional;
import ar.com.ospim.autorizaciones.beans.ReclamoPrestacional.ESTADOSEVALUACIONRECLAMO;
import ar.com.ospim.autorizaciones.beans.ReglaValidacion;
import ar.com.ospim.autorizaciones.beans.RevisionesReclamo;
import ar.com.ospim.autorizaciones.beans.RespuestaPreAutorizPSDTO;
import ar.com.ospim.crm.beans.DerivacionNotificacion;
import ar.com.ospim.crm.services.CrmServiceUtil;
import ar.com.ospim.global.beans.Empresa;
import ar.com.ospim.global.beans.OrdenPagoOspim;
import ar.com.ospim.global.beans.Seccional;
import ar.com.ospim.global.services.EmpresaServiceUtil;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.liquidaciones.beans.Prestador;
import ar.com.ospim.liquidaciones.services.PrestadorServiceUtil;
import ar.com.ospim.util.ConnectionHelper;
import ar.com.ospim.util.StringUtils;

public class IntegracionServiceUtil {
	@SuppressWarnings("unused")
	private static Log _log = LogFactoryUtil
			.getLog(IntegracionServiceUtil.class);

	private static IntegracionServiceImpl instance = null;
	

	public static IntegracionServiceImpl getInstance() {
		if (null == instance) {
			instance = new IntegracionServiceImpl();
		}
		return instance;
	}

	public static String validaDetalle(IntegracionDetalleDS s,boolean validaDuplicado,IntegracionReglasValidacion reglas) throws Exception {
		String ret="OK";
		
		try {
			Afiliado a =EditarAfiliadoServiceUtil.getAfiliadoInclusoDadoBajaPorCuil(s.getCuil(), null);
		
		   if(a==null) { 
			  ret="AI"; //Afiliado Inexistente
		   }else if(a.getBaja_fecha()!=null && a.getBaja_fecha().before((new Date()))) {
			  ret ="AB"; //Afiliado Dado de Baja
		   }else if(a.getDiscapacitado()==null || "".equalsIgnoreCase(a.getDiscapacitado()) || "0".equalsIgnoreCase(a.getDiscapacitado()) ){
			  ret ="AD"; // Afiliado no Discapacitado
		   }else if(a.getFechaVtoDocDiscap()!=null  && a.getFechaVtoDocDiscap().before(s.getComprobanteFechaEmision())){
			  ret ="CV"; //Certificado Vencido
		   }
		
//		   Empresa lp =  EmpresaServiceUtil.getEmpleadorCompleto(s.getCuitPrestador(), "000");
		   List<Prestador> lp = PrestadorServiceUtil.getPrestadores(0, s.getCuitPrestador(), null, false);
	       if(lp==null || lp.size()==0){
	         ret="PI"; //Prestador Inexistente
	       }else if(lp.size()>0){
	    	   Empresa e =  EmpresaServiceUtil.getEmpleadorCompleto(s.getCuitPrestador(), "000");
	    	   if(e==null || e.getCBU()==null || "".equalsIgnoreCase(e.getCBU()) ) {
	    		   ret="CB";
	    	   }
	       }
		}catch(NoSuchAfiliadoEntryException e) {
		   ret="AI";	//Afiliado Inexistente
		}
	    
		Nomenclador n= IntegracionServiceUtil.buscaNomencladorSSSById(Integer.parseInt( s.getPrestacionCodigo()));
		if(n==null || n.getId_prestacion()==0) {
			ret="NI";
		}
	    ArrayList<Object>entidades=new ArrayList<Object>();
	    entidades.add(s);
	    entidades.add(getInstance());
		for(ReglaValidacion r:reglas.getReglas()) {
//			if("OK".equalsIgnoreCase(ret)) {
				String res = r.evaluarPorError(entidades);
				if(!"OK".equalsIgnoreCase(res)) {
					ret=res;
					break;
				}
//			}
		}
				
		if(validaDuplicado) {
		   boolean duplicado = "DS".equalsIgnoreCase(s.getTipoArchivo())?getValidaDuplicado(s):false;
	       if(duplicado) ret="DU";
		}
		
		if(s.getComprobanteImporte()<s.getImporteSolicitado()) {
			   ret="II"; // Importe Comprobante menor al Solicitado
		}
	    
		return ret;
	}
	
	public static Integer saveLote(IntegracionCabeceraDS cab, String screenName) throws Exception {
		Integer idLote = 0; 
		Connection connection = null;
		Boolean esBaja=false;
		
		try {			
			connection = ConnectionHelper.getConnection();
			connection.setAutoCommit(false);
		    // Alta de la Cabecera
			idLote = getInstance().insertaIntegracionCabezaDS(cab, screenName,connection);
			
			
		    //Inserta Detalle
			for(IntegracionDetalleDS d:cab.getItems()){
				getInstance().insertaIntegracionDetalleDS(idLote,d, screenName, connection);
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
	  return idLote;
	}
	
	public static List<IntegracionCabeceraDS> lotesProcesados()
			throws SystemException {
		return getInstance().lotesProcesados();
	}
	
	public static boolean getValidaDuplicado(IntegracionDetalleDS d)
			throws SystemException {
		return getInstance().getValidaDuplicado(d);
	}

	public static void eliminaLote(Integer id) throws Exception {
		 getInstance().eliminaLote(id);
	}
	
	public static List<IntegracionDetalleDS> detalleDSByIdLote(Integer idLote) throws SystemException{
		return getInstance().detalleDSByIdLote(idLote);
	}
	
	public static List<IntegracionDetalleDS> detalleLiquidacionByIdLote(Integer idLote) throws SystemException{
		return getInstance().detalleLiquidacionByIdLote(idLote);
	}
	
	public static List<IntegracionDetalleDS> detalleLiquidacionByIdCab(Integer idLote) throws SystemException{
		return getInstance().detalleLiquidacionByIdCab(idLote);
	}
	
	
	public static List<IntegracionDetalleDS> detalleLiquidacionByIdLotePorOp(Integer idLote) throws SystemException{
		return getInstance().detalleLiquidacionByIdLotePorOp(idLote);
	}
	
	
	public static List<IntegracionDetalleDS> detalleDS_Errores_By_IdLote(Integer idLote) throws SystemException{
		List<IntegracionDetalleDS>aux = detalleDSByIdLote(idLote);
		List<IntegracionDetalleDS>conError = new ArrayList<IntegracionDetalleDS>();
		for(IntegracionDetalleDS d:aux) {
			if(!"OK".equalsIgnoreCase(d.getError())) {
				conError.add(d);
			}
		}
		return conError;
	}
	
	public static long updateErrorDetalleDS(IntegracionDetalleDS det, String screenName) throws Exception {
	    	
			try {			
				getInstance().updateErrorDetalleDS(det, screenName,null);
		  } catch (Exception e) {
			 	_log.error("Error al Actualizar Codigo Error Integracion Detalle DS");
			 	_log.error(e);
		  }  
		  return det.getId();
	}
	
	public static List<IntegracionDetalleDS> detalleDSByPeriodo(Integer periodo) throws SystemException{
		return getInstance().detalleDSByPeriodo(periodo);
	}
	
	public static Nomenclador buscaNomencladorSSSById(Integer id) {
		return getInstance().buscaNomencladorSSSById(id) ;
	}
	
	public static Integer updateInformadoFTPDS(Integer periodo,Connection connectionParameter) throws SystemException, SQLException {
		return getInstance().updateInformadoFTPDS(periodo, null);
	}
	
	public static List<IntegracionCabeceraDS> lotesSSS()
			throws SystemException {
		return getInstance().lotesSSS();
	}
	
	
	public static Integer updateFTPDS_OK(List<IntegracionDetalleDS> lista) throws SystemException, SQLException {
		Integer idLote = 0; 
		Connection connection = null;
		Boolean esBaja=false;
		
		try {			
			connection = ConnectionHelper.getConnection();
			connection.setAutoCommit(false);
			
			for(IntegracionDetalleDS d:lista){
				getInstance().updateFTPDS_OK(d,connection);
			}
			
			connection.commit();
	  } catch (Exception e) {
		  if(null!=connection){
			  connection.rollback();
		  }			
	  } finally {
		 if (connection != null) {
			 ConnectionHelper.cerrar(connection);
		 }
	  }    
	  return idLote;
	}
	
	
	public static boolean getValidaFTPProcesado(Integer periodo,String tipoArchivo)
			throws SystemException {
		return getInstance().getValidaFTPProcesado(periodo,tipoArchivo);
	}
	
	public static List<IntegracionDetalleDS> detalleDSByIdLoteSSS(Integer idLote) throws SystemException{
		return getInstance().detalleDSByIdLoteSSS(idLote);
	}
	
	public static boolean liquidarLoteSSS(Integer idLote) throws Exception{
		return getInstance().liquidarLoteSSS(idLote);
	}
	
	public static boolean liquidarLoteSSSCab(Integer idLote) throws Exception{
		return getInstance().liquidarLoteSSSCab(idLote);
	}
	
	public static boolean cerrarLoteSSS(Integer idLote) throws SystemException{
		return getInstance().cerrarLoteSSS(idLote);
	}
	
	public static Integer historicoLoteSSS(Integer idLote,String usr) throws Exception{
		return getInstance().historicoLoteSSS(idLote,usr);
	}
	
	public static boolean existeMovimientoTransferencia(Date fechaMov,Date fechaValor,Double monto,String cuit,String referencia) throws Exception{
		return getInstance().existeMovimientoTransferencia(fechaMov,fechaValor,monto,cuit,referencia);
	}
	
	public static List<OrdenPagoOspim> proponeOrdenPagoTransferenciaBancaria(String cuit,Date fecha,Double importe) throws SystemException{
        return getInstance().proponeOrdenPagoTransferenciaBancaria(cuit, fecha, importe); 		
	}
	
	public static Integer ultimoLoteTransferenciaProcesado() throws SystemException   {
		return getInstance().ultimoLoteTransferenciaProcesado();
	}
	
	public static Integer insertaIntegracionTranferencia(
			  Integer nro_lote_p,
			  Date fecha_valor_p,
			  Date fecha_mov_p,
			  Double monto_p ,
			  String referencia_p,
			  String concepto_p,
			  String cuit_p,
			  Integer ordenpago_id_p,
			  String alta_usr_p,Connection connectionParameter) throws Exception{
		return getInstance().insertaIntegracionTranferencia(nro_lote_p, fecha_valor_p, fecha_mov_p, monto_p, 
				referencia_p, concepto_p, cuit_p, ordenpago_id_p, alta_usr_p, connectionParameter);
	}
	
	public static List<IntegracionCabeceraDS> lotesTransferenciasExtractos()
			throws SystemException {
		return getInstance().lotesTransferenciasExtractos();
	}
	
	public static boolean avisoTransferenciaOP(Integer idOp) throws SystemException {
		return getInstance().avisoTransferenciaOP(idOp) ;
	}
	
	public static boolean marcaAvisoTransferencia(Integer idOp) throws SystemException {
		return getInstance().marcaAvisoTransferencia(idOp) ;
	}

	public static boolean existeAvisoTransferencia(Integer op) throws Exception {
		return getInstance().existeAvisoTransferencia(op);
	}
	
	public static boolean asociarRecibo(Integer idOp,String nroRecibo) throws SystemException {
		return getInstance().asociarRecibo(idOp, nroRecibo);
	}
	
	public static List<IntegracionDetalleDS> inconsistenciasExtractosBancariosByIdLote(Integer idLote) throws SystemException{
		return getInstance().inconsistenciasExtractosBancariosByIdLote(idLote);
	}
	
	public static Integer updateFTPDS_Subsidio(List<IntegracionDetalleDS> lista) throws SystemException, SQLException {
		Integer idLote = 0; 
		Connection connection = null;
		Boolean esBaja=false;
		
		try {			
			connection = ConnectionHelper.getConnection();
			connection.setAutoCommit(false);
			
			for(IntegracionDetalleDS d:lista){
				getInstance().updateFTPDS_Subsidio(d,connection);
			}
			
			connection.commit();
	  } catch (Exception e) {
		  if(null!=connection){
			  connection.rollback();
		  }			
	  } finally {
		 if (connection != null) {
			 ConnectionHelper.cerrar(connection);
		 }
	  }    
	  return idLote;
	}
	

	public static IntegracionReglasValidacion getReglasValidacion() throws SystemException{
		IntegracionReglasValidacion irv = new IntegracionReglasValidacion();
		irv.setReglas(getInstance().getReglasValidacion());
		return irv;
	}

	public static List<IntegracionDetalleDS> detalleDSByCuilPeriodo(String cuil,Integer periodo,String prestacionesIncluye,String prestacionesExcluye)
			throws SystemException {
		return getInstance().detalleDSByCuilPeriodo(cuil, periodo, prestacionesIncluye, prestacionesExcluye);
	}
	
	
	public static List<CuentasInterbaking> exportarCuentasInterbanking(String opDesde, String opHasta, String in) throws SystemException {
		return getInstance().obtenerCuentasExportarInterbanking(opDesde, opHasta, in);
	}
	
	public static List<CuentasInterbaking> getCuentasInterbanking(String in,String entidad) throws SystemException {
		return getInstance().cuentasExportarInterbanking(in,entidad);
	}
		
	public static OrdenesPagoInterbanking  exportacionPagosInterbanking(String opDesde, String opHasta, String in) throws SystemException {
		return getInstance().exportacionPagosInterbanking(opDesde, opHasta, in);
	}
	
	public static OrdenesPagoInterbanking  exportacionPagosInterbankingOPS(String opDesde, String opHasta, String in,Integer ctaBcria) throws SystemException {
		return getInstance().exportacionPagosInterbankingOPS(opDesde, opHasta, in, ctaBcria);
	}
	
	public static Integer  registrarOrdenesInterbankingOPS(Integer op) throws SystemException {
		return getInstance().registrarOrdenesInterbankingOPS(op);
	}
	
	public static Integer  registrarOrdenesInterbanking(Integer op) throws SystemException {
		return getInstance().registrarOrdenesInterbanking(op);
	}
	
	public static OrdenesPagoInterbanking  getPagosInterbankingOPS(String in,Integer ctaBcria,String entidad) throws SystemException {
		return getInstance().getPagosInterbankingOPS( in, ctaBcria,entidad);
	}
	
	public static boolean agregarDebito(Integer idCpte,Double debito,String motivo) throws SystemException {
		return getInstance().agregarDebito(idCpte, debito, motivo);
	}
	
	public static boolean eliminarDebito(Integer idCpte) throws SystemException {
		return getInstance().eliminarDebito(idCpte);
	}
	
	public static List<OrdenPagoOspim> getOrdenesPagoSinAvisoTransferencia() throws SystemException{
        return getInstance().getOrdenesPagoSinAvisoTransferencia(); 		
	}
	
	public static List<OrdenPagoOspim> getOrdenesPagoSinAvisoTransferenciaPagoAfiliado() throws SystemException{
        return getInstance().getOrdenesPagoSinAvisoTransferenciaPagoAfiliado(); 		
	}
	
	public static List<OrdenPagoOspim> getOrdenesPagoGRALSinAvisoTransferencia() throws SystemException{
        return getInstance().getOrdenesPagoGRALSinAvisoTransferencia(); 		
	}
	
	

	
	public static List<IntegracionCabeceraDR> lotesRendicion()
			throws SystemException {
		return getInstance().lotesRendicion();
	}
	
	public static Integer saveDR_Envio(IntegracionCabeceraDR cab, String screenName) throws Exception {
		Integer idLote = 0; 
		Connection connection = null;
		Boolean esBaja=false;
		
		try {			
			connection = ConnectionHelper.getConnection();
			connection.setAutoCommit(false);
		    // Alta de la Cabecera
			idLote = getInstance().inserta_DR_Envio_Cabecera(cab, screenName, connection);
			
			
		    //Inserta Detalle
			for(IntegracionDetalleDR d:cab.getItems()){
				getInstance().inserta_DR_Envio_Detalle(d, screenName, connection);
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
	  return idLote;
	}
	
	public static boolean generarDevolucion(Integer periodo,String screenName) throws Exception{
		return getInstance().generarDevolucion(periodo,screenName);
	}
	
	public static List<IntegracionDetalleDR> traeListaDetalleDR(Integer offset,
			IntegracionDetalleDR filtro) throws SystemException{
		List<IntegracionDetalleDR> list = getInstance().traeListaDetalleDR(offset,filtro);
		return list;
	}
	
	public static long updateDetalleDR(IntegracionDetalleDR det, String screenName,boolean soloError) throws Exception {
    	Integer idDetalle=0;
		try {			
			getInstance().updateDetalleDR(det, screenName,soloError,null);
	  } catch (Exception e) {
		 	_log.error("Error al Actualizar Codigo Error Integracion Detalle DR");
		 	_log.error(e);
	  }  
	  return idDetalle;
   }
	
   public static String getDescripcionError(Integer codError) throws Exception{
		return getInstance().getDescripcionError(codError) ;
   }
   
   public static Integer eliminarRendicionPeriodo(Integer periodo) throws Exception{
		return getInstance().eliminarRendicionPeriodo(periodo);
   }
   
   public static Integer cerrarRendicionPeriodo(Integer periodo) throws Exception{
		return getInstance().cerrarRendicionPeriodo(periodo);
  }
   
   public static List<IntegracionCabeceraDS> lotesSSSCab()
			throws SystemException {
		return getInstance().lotesSSSCabecera();
	}
   
   public static boolean excluirLiquidacion(Integer idCpte) throws SystemException {
		return getInstance().excluirLiquidacion(idCpte);
   }
   
   public static boolean incluirLiquidacion(Integer idCpte) throws SystemException {
		return getInstance().incluirLiquidacion(idCpte);
  }
   
   public static List<CuentasInterbaking> exportarCuentasInterbankingEmail(String opDesde, String opHasta, String in) throws SystemException {
		return getInstance().obtenerCuentasExportarInterbankingEmail(opDesde, opHasta, in);
   }
   
   public static List<CuentasInterbaking> getCuentasInterbankingEmail(String in,String entidad) throws SystemException {
		return getInstance().cuentasExportarInterbankingEmail(in,entidad);
   }
}

