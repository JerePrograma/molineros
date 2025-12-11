package ar.com.ospim.estudioisidro.action;

import java.io.IOException;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import ar.com.global.services.GoogleCalendarUtil;
import ar.com.ospim.estudioisidro.beans.Llamado;

public class AgendarGoogleCalendar extends Thread{ 
	private static Log _log = LogFactoryUtil.getLog(AgendarGoogleCalendar.class);

	private int id;
	private String[] email={"info@ospim.org.ar"};
	private Llamado llamado;
	
	
	
	
	public AgendarGoogleCalendar(int id,  Llamado llamado) {
		super();
		this.id = id;
		this.llamado = llamado;
	}




		public void run()
	    {
	    	String agendaEvent = null;
	    	com.google.api.services.calendar.Calendar calendar;
			try {
				calendar = GoogleCalendarUtil.getCalendarService(email[0]);
				_log.debug("getApplicationName: " + calendar.getApplicationName());
				
				if (id != 0) {
					agendaEvent = GoogleCalendarUtil
							.updateOrCreateEvent(calendar,
									llamado.getGoogleEvent(),
									llamado.getFechaAgenda(),
									llamado.getObservaciones(),
									"OSPIM",
									llamado.getObservaciones(), email);
				} else {
					agendaEvent = GoogleCalendarUtil.createEvent(
							calendar, llamado.getFechaAgenda(),
							llamado.getObservaciones(), "OSPIM",
							llamado.getObservaciones(), email);
				}
				llamado.setGoogleEvent(agendaEvent);

			} catch (IOException e) {
				_log.debug("Error AgendarGoogleCalendar: " + e);
			} catch (Exception e) {
				_log.debug("Error AgendarGoogleCalendar: " + e);
			}

	    }
	     
	    

}
