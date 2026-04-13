package ar.com.ospim.autorizaciones.beans;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import ar.com.ospim.automatico.AgendadoJava;
import ar.com.ospim.automatico.beans.ReporteAutomatico;
import ar.com.ospim.automatico.service.ReportesServiceUtil;
import ar.com.ospim.autorizaciones.reportes.action.ReporteEstadisticaGastoMedicoExcel;
import ar.com.ospim.autorizaciones.reportes.action.ReporteReclamosPrestacionales;
import ar.com.ospim.autorizaciones.services.AutorizacionesServiceUtil;
import ar.com.ospim.autorizaciones.services.ReclamosPrestacionesServiceUtil;
import ar.com.ospim.mail.MailUtils;
import ar.com.ospim.util.DateUtils;

public class ReporteEstadisticasGastosMedicos extends AgendadoJava implements Serializable {

	private static final long serialVersionUID = 7974168700348801594L;
	private static Log logger = LogFactoryUtil.getLog(ReporteEstadisticasGastosMedicos.class);

	@Override
	public void correrAgendado(ReporteAutomatico ra) {

		ar.com.ospim.automatico.ReportesScheduler.ReportesAutomaticosConfiguracion rac = null;
		HSSFWorkbook wb = new HSSFWorkbook();		
		try{
			rac = ReportesServiceUtil.getConfiguracion();
			
			Date fechaDde=null;
			Date fechaHta=null;
			Calendar calendar = Calendar.getInstance();
			List<String> emails = new ArrayList<String>();
			SimpleDateFormat sdf =new SimpleDateFormat("yyyy-MM");
//Comentar			
//calendar.set(2022, Calendar. FEBRUARY, 01);			

			Date fecha = DateUtils.getPreceedingMonth(calendar.getTime());
			fechaDde=DateUtils.getFirstDateOfMonth(fecha, true);
			fechaHta=DateUtils.getLastDateOfMonth(fecha, true);
			   
			List<EstadisticaGastoMedico> list = new ArrayList<EstadisticaGastoMedico>();
			List<EstadisticaGastoMedicoDetalle> listP = new ArrayList<EstadisticaGastoMedicoDetalle>();
			List<ReclamoPrestacionalExcel> reclamosPrestacionales= new ArrayList<ReclamoPrestacionalExcel>();
			List<ReclamoPrestacionalExcel> reclamosPrestacionalesAg= new ArrayList<ReclamoPrestacionalExcel>();

			
			try {
				list = ReclamosPrestacionesServiceUtil.getEstadisticaGastoMedico(fechaDde, fechaHta);
				wb=ReporteEstadisticaGastoMedicoExcel.generaReporteAgrupado(wb,list,fechaDde,fechaHta);
			} catch (SystemException e) {
				   logger.debug("Error al generar Estadistico de Gastos Médicos (Agendado)");
			}
			
			try {
				listP = ReclamosPrestacionesServiceUtil.getEstadisticaGastoMedicoDetalle(fechaDde, fechaHta);
				wb=ReporteEstadisticaGastoMedicoExcel.generaReporteDetallado(wb,listP,fechaDde,fechaHta);
			} catch (SystemException e) {
				   logger.debug("Error al generar Estadistico de Gastos Médicos Detallado (Agendado)");
			}

			
			BusquedaReporteReclamoFiltro filtro = new BusquedaReporteReclamoFiltro(null, null,0,"",0,
					null,0 ,-1,fechaDde,fechaHta,
					"0",  null, null ,null,null , 	 
					"Seleccione", "Seleccione", null, null,
					null,null, 0,0,0);

            try {
                 reclamosPrestacionales= AutorizacionesServiceUtil.getListaReclamosPrestacionales (filtro);
                 wb=ReporteReclamosPrestacionales.generaReporteReclamosPrestacionales(reclamosPrestacionales, filtro, wb);
            } catch (Exception e) {
                logger.debug("Error al generar Estadistico de Gastos Médicos Cierre Reclamos (Agendado)");
            }
            
            try {
                reclamosPrestacionalesAg= AutorizacionesServiceUtil.getListaReclamosPrestacionalesAgrupado (filtro);
                wb=ReporteReclamosPrestacionales.generaReporteReclamosPrestacionalesAgrupado(reclamosPrestacionalesAg, filtro, wb);
            } catch (Exception e) {
               logger.debug("Error al generar Estadistico de Gastos Médicos Cierre Reclamos (Agendado)");
            }
			  
			   emails=ra.getEmailsAsList();	
			
//			   emails.add("dsulfaro@uoma.org.ar");
			   
			   String nbeArchivo="EstadisticasGastosMedicos_"+ sdf.format(fechaDde)+".xls";
			   List<String>lem = new ArrayList<String>();
			   for(String em:emails) {
			      lem.clear();
			      lem.add(em);
			      MailUtils.enviarMailGmailconXls(rac.getMailFrom(),rac.getPass(),lem, "Informes Estadisticas de Gastos Médicos ",
						  "Informe de Estadisticas de Gastos Médicos", wb, nbeArchivo);
			   }   
			
			ra.setUltimaEjecucion(new Date());
			ReportesServiceUtil.reporteEjecutado(ra);
			
			logger.debug("Fin de Envío de Estadisticas de Gastos Médicos");
		} catch (NumberFormatException e) {
			logger.error(e);
		} catch (SystemException e) {
			logger.error(e);
		} catch (Exception e) {
			logger.error(e);
		}
		
		
		
	}

	@Override
	public HSSFWorkbook getResultados() {
		// TODO Auto-generated method stub
		return null;
	}

    
}
