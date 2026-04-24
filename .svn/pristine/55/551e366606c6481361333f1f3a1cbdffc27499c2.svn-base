package ar.com.ospim.automatico.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import ar.com.ospim.automatico.beans.MensajeEnvioyRespuestaWSOmint;
import ar.com.ospim.webservice.service.AfiliadoOpe;

import com.liferay.portal.SystemException;

public class AgendaReporteUtil{

	private AgendaReporteImpl dao = new AgendaReporteImpl();
	
	public static int horaCorridaDiferida = 19;
	
	public int agendarRepoDeudaEmpPeriodo(String reporteDescripcion, String screenName, Date fechaDesde, Date fechaHasta,
		   Integer ramoDesde, Integer ramoHasta, boolean agrupaxRemun, boolean empresaSinDeuda) throws SystemException{
		
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
//		SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy");
		
//		Stored_Procedured = "reporte_deuda_empresas_periodo_batch(periodo_desde date, periodo_hasta date, sin_deuda boolean, 
//		ramo_desde_p integer, ramo_hasta_p integer, usuario_p varchar, fecha_solicitado_p timestamp without time zone)"
		
		String csvParameteres = "";
//		ej: 1=Integer,null=String,Ejemplo=String,true=Boolean,01/01/2011 12:00:00=Date
		csvParameteres = sdf1.format(fechaDesde)+"=Date,"+sdf1.format(fechaHasta)+"=Date,"+empresaSinDeuda+"=Boolean,"+ramoDesde+"=Integer,"+ramoHasta+"=Integer,"
						+screenName+"=String,"+sdf1.format(new Date())+"=Date";
		
		int result = 0;
		
		try{
			result = dao.agendarReporte(reporteDescripcion, csvParameteres);
		}catch (Exception e) {
			return 0;
		}
		return result;
	}
	
	public int agendarRepoProcesoNovedadesSSS(String reporteDescripcion, String screenName, Date fechaProceso, Date fechaArchivo, 
			Date fechaPadronInicio, Date fechaPadronFinal, boolean informar) throws SystemException{
			
			SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			
//			Stored_Procedured = "reporte_deuda_empresas_periodo_batch(periodo_desde date, periodo_hasta date, sin_deuda boolean, 
//			ramo_desde_p integer, ramo_hasta_p integer, usuario_p varchar, fecha_solicitado_p timestamp without time zone)"
			  
			String csvParameteres = "";
//			ej: 1=Integer,null=String,Ejemplo=String,true=Boolean,01/01/2011 12:00:00=Date
			csvParameteres = sdf1.format(fechaProceso)+"=Date,"+sdf1.format(fechaArchivo)+"=Date,"
							+sdf1.format(fechaPadronInicio)+"=Date,"+sdf1.format(fechaPadronFinal)+"=Date,"
							+informar+"=Boolean,"+screenName+"=String";
			
			int result = 0;
			
			try{
				result = dao.agendarReporte(reporteDescripcion, csvParameteres);
			}catch (Exception e) {
				return 0;
			}
			return result;
		}
	
	public List<MensajeEnvioyRespuestaWSOmint> getNovedadesProcesadas(Date fechaProceso) throws SystemException{
		
		return dao.getNovedadesProcesadas(fechaProceso);
		
	}
	
	public List<AfiliadoOpe> getNovedadesProcesadasPrevencion(Date fechaProceso) throws SystemException{
		
		return dao.getNovedadesProcesadasPrevencion(fechaProceso);
		
	}
	
}

