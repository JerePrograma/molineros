package ar.com.ospim.comprobantesPortalProveedores.action;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import ar.com.ospim.automatico.AgendadoJava;
import ar.com.ospim.automatico.beans.ReporteAutomatico;
import ar.com.ospim.automatico.service.ReportesServiceUtil;
import ar.com.ospim.comprobantesPortalProveedores.services.ComprobanteServiceUtil;
import ar.com.ospim.global.beans.Comprobante;
import ar.com.ospim.webservice.proveedoresLPA.ClienteProveedoresLPA;

public class ReporteRegistroOPSScheduler extends AgendadoJava implements Serializable {

    private static final long serialVersionUID = 6554741339837030239L;

	private static Log logger = LogFactoryUtil.getLog(ReporteRegistroOPSScheduler.class);

	@Override
	public void correrAgendado(ReporteAutomatico ra) {

		logger.debug("ya esta corriendo ReporteRegistroOPS");
		ar.com.ospim.automatico.ReportesScheduler.ReportesAutomaticosConfiguracion rac = null;
		ScheduledExecutorService scheduler  = Executors.newSingleThreadScheduledExecutor();
		Calendar cal1 = Calendar.getInstance();
		cal1.add(Calendar.DATE, -1);
		
		try {
			rac = ReportesServiceUtil.getConfiguracion();
			List <Comprobante> cs = ComprobanteServiceUtil.getAvisosPagoByFechaTransferencia(cal1.getTime());
			if(!cs.isEmpty()) {
				for(Comprobante c:cs) {
					try {
						Calendar cal = Calendar.getInstance();
						cal.setTime(c.getFechaPrimerPago());
						ClienteProveedoresLPA.setOrdenPagoWithPDF(c.getId(),c.getIdOp(),cal);
					}catch(Exception ec) {}
				}
			}
			ra.setUltimaEjecucion(new Date());
			ReportesServiceUtil.reporteEjecutado(ra);
			
			logger.debug("Fin de ReporteRegistroOPS");
		} catch (NumberFormatException e) {
			logger.error(e);
		} catch (SystemException e) {
			logger.error(e);
		} catch (Exception e) {
			logger.error(e);
		}
		scheduler.shutdown();
	}

	@Override
	public HSSFWorkbook getResultados() {
		// TODO Auto-generated method stub
		return null;
	}
    
}
