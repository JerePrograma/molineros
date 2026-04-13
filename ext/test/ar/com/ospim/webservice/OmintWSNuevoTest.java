package ar.com.ospim.webservice;

import static org.junit.Assert.*;

import org.junit.Test;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import ar.com.ospim.automatico.ReportesScheduler;
import ar.com.ospim.mail.MailUtils;

public class OmintWSNuevoTest {

	private static Log logger = LogFactoryUtil.getLog(OmintWSNuevoTest.class);
	
	@Test
	public void test() {
		
		logger.info("entramos al WSClient Job");
		OmintWSClient wsClient = new OmintWSClient();
		wsClient.procesarNovedades();
		logger.info("salimos del WSClient Job");
		
//		MailUtils.enviarMailGmailSinAdjunto("info@ospim.org.ar", "ospim123", ra.getEmailsAsList(), ra.getTitulo(),
//				"Se ha corrido el envio de novedades de padron de afiliados a Omint");
		
	}

}
