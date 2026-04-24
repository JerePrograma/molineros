package ar.com.ospim.estudioisidro.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import com.liferay.portal.SystemException;

import ar.com.ospim.estudioisidro.beans.ActaAcuerdoSeguimientoResumen;
import ar.com.ospim.estudioisidro.beans.ArchivoSubidoEstudio;
import ar.com.ospim.estudioisidro.beans.Llamado;
import ar.com.ospim.estudioisidro.beans.LlamadosEstudio;
import ar.com.ospim.estudioisidro.beans.ReporteEstadisticoSeguimientoEmpresa;
import ar.com.ospim.estudioisidro.beans.ReporteSeguimientoEmpresa;
import ar.com.ospim.estudioisidro.beans.TipoLoteEmpresa;
import ar.com.ospim.global.beans.Empresa;
import ar.com.ospim.global.services.EmpresaServiceUtil;
import ar.com.ospim.util.ConnectionHelper;

public class LlamadoServiceUtil {
	
	public static int getTotalLlamados(String cuit) throws Exception{
		return LlamadoServiceImpl.getInstance().getTotalLlamados(cuit);
	}


	public static LlamadosEstudio getLlamados(String cuit, int cursor) throws Exception {
		return LlamadoServiceImpl.getInstance().getLlamados(cuit, cursor);
	}
	public static List<Llamado> getLlamadosList(String cuit, int cursor, Connection con) throws Exception {
		return LlamadoServiceImpl.getInstance().getLlamadosList(cuit, cursor, con);
	}
	
	public static int grabaLlamado(Llamado llamado, boolean molinera) throws Exception{
		return LlamadoServiceImpl.getInstance().grabaLlamado(llamado, molinera);		
	}
	
	//public static LlamadosEstudio grabaLlamado(String cuit, Date fechaLlamado, String observaciones, Integer idEstado, String cartaDoc, String ubicacionCarpeta, boolean molinera, String tipoContacto, String username) throws Exception{
	//	LlamadoServiceImpl.getInstance().grabaLlamado(cuit, fechaLlamado, observaciones, idEstado, cartaDoc, ubicacionCarpeta, molinera, tipoContacto, username);
	//	return LlamadoServiceImpl.getInstance().getLlamados(cuit, 0);
	//}
	
	public static void actualizaLlamado(Llamado llamado) throws Exception{
		LlamadoServiceImpl.getInstance().actualizaLlamado(llamado);		
	}
	
	public static void bajaLlamado(int id, String username) throws Exception{
		LlamadoServiceImpl.getInstance().bajaLlamado(id, username);		
	}
	
	public static List<ReporteSeguimientoEmpresa> getReporteSeguimientoEmpresa(Date fechaIni, Date fechaFin, Empresa empresa,Integer nroLote,String tipoLote){
		return LlamadoServiceImpl.getInstance().getReporteSeguimientoEmpresa(fechaIni, fechaFin, empresa,nroLote,tipoLote);
	}
	
	public static void traeTotalesSeguimiento(LlamadosEstudio llest, Connection con) throws SQLException {
		LlamadoServiceImpl.getInstance().traeTotalesSeguimiento(llest, con);
	}
	
	public static Llamado getProponeNroLote(String cuit, Connection connection) {
		return LlamadoServiceImpl.getInstance().getProponeNroLote(cuit, connection);
	}
	
	public static int grabaLlamadoLote(Llamado llamado, boolean molinera,Connection connection) throws Exception{
		return LlamadoServiceImpl.getInstance().grabaLlamadoLote(llamado, molinera,connection);		
	}
	
	public static int grabaLlamadoLote(List<Llamado> llamados) throws Exception{
		int cantidad = 0; 
		Connection connection = null;
		try {			
			connection = ConnectionHelper.getReportesOspimConnection();
			connection.setAutoCommit(false);
			
			for(Llamado l:llamados){
				Boolean molinera=false;
//				List<Empresa>eps = EmpresaServiceUtil.getEmpleadores(l.getCuit(), null, "000", 0);
				List<Empresa>eps = EmpresaServiceUtil.getEmpleadores(l.getCuit(), null, null, 0,connection);
				Empresa empresa=new Empresa();
				if(eps.size()>0){
				   empresa = eps.get(0);
				} else{
				   empresa = EmpresaServiceUtil.getEmpleadorCompleto(l.getCuit(),"000" );	
				}
				//Empresa empresa =EmpresaServiceUtil.getEmpleadorCompleto(l.getCuit(),"000" );
				if(empresa.getRamoEmpresa()!=null && empresa.getRamoEmpresa().getId_ramo_empresa()<11) molinera=true;
				LlamadoServiceUtil.grabaLlamadoLote(l, molinera,connection);
			}
			
			ArchivoSubidoEstudio a = new ArchivoSubidoEstudio("LOTE EMPRESAS",llamados.get(0).getLote(),
					llamados.get(0).getTipoLote(), new Date(), llamados.size(),llamados.get(0).getUser());
			
			
			LlamadoServiceImpl.getInstance().grabaArchivoSubido(a,connection);
			
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
	  return cantidad;		
	}
	
	public static boolean existeLote(String cuit, Integer lote, String tipoLote) throws Exception{
		return LlamadoServiceImpl.getInstance().existeLote(cuit, lote, tipoLote);		
	}
	
	public static List<ArchivoSubidoEstudio> getArchivosSubidosEstudio() throws SystemException {
		return LlamadoServiceImpl.getInstance().getArchivosSubidosEstudio();
	}
	
	public static List<ReporteEstadisticoSeguimientoEmpresa> getReporteEstadisticoSeguimientoEmpresaLotes(Date fechaIni, Date fechaFin, Empresa empresa,Integer nroLote,String tipoLote){
		return LlamadoServiceImpl.getInstance().getReporteEstadisticoSeguimientoEmpresaLotes(fechaIni, fechaFin, empresa,nroLote,tipoLote);
	}
	
	public static List<ReporteEstadisticoSeguimientoEmpresa> getReporteEstadisticoSeguimientoEmpresa(Empresa empresa,Integer nroLote,String tipoLote){
		return LlamadoServiceImpl.getInstance().getReporteEstadisticoSeguimientoEmpresa(empresa,nroLote,tipoLote);
	}
	
	public static ReporteEstadisticoSeguimientoEmpresa getReporteEstadisticoSeguimientoEmpresaAsignado(Empresa empresa,Integer nroLote,String tipoLote,Integer estado){
		return LlamadoServiceImpl.getInstance().getReporteEstadisticoSeguimientoEmpresaAsignado(empresa,nroLote,tipoLote,estado);
	}
	
	public static ReporteEstadisticoSeguimientoEmpresa getReporteEstadisticoSeguimientoEmpresaRecuperado(Empresa empresa,Integer nroLote,String tipoLote,Integer estado){
		return LlamadoServiceImpl.getInstance().getReporteEstadisticoSeguimientoEmpresaRecuperado(empresa,nroLote,tipoLote,estado);
	}
	public static ReporteEstadisticoSeguimientoEmpresa getReporteEstadisticoSeguimientoEmpresaCobrado(Empresa empresa,Integer nroLote,String tipoLote,Integer estado){
		return LlamadoServiceImpl.getInstance().getReporteEstadisticoSeguimientoEmpresaCobrado(empresa,nroLote,tipoLote,estado);
	}
	
	public static boolean cierreAutomaticoLotes() throws Exception{
		return LlamadoServiceImpl.getInstance().cierreAutomaticoLotes();		
	}
	
	public static ActaAcuerdoSeguimientoResumen getDesglosePagosActasConvenios(String cuit) {
		return LlamadoServiceImpl.getInstance().getDesglosePagosActasConvenios(cuit);	
	}
	
	public static List<TipoLoteEmpresa> avisoVencimientoLotesSeguimientoEmpresas(Integer diasAlVencimiento, Integer diasAntesAviso) {
		return LlamadoServiceImpl.getInstance().avisoVencimientoLotesSeguimientoEmpresas(diasAlVencimiento,diasAntesAviso,null);
	}
	
}
