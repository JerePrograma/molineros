package ar.com.ospim.automatico;

import java.util.Date;
import java.util.Enumeration;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import ar.com.ospim.afiliados.empleadores.index.EmpresasIndex;
import ar.com.ospim.util.DateUtils;

import com.liferay.portal.kernel.job.Scheduler;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

public class InicializadorIndiceEmpresas implements Scheduler,
		ServletContextListener {
	private static Log logger = LogFactoryUtil
			.getLog(InicializadorIndiceEmpresas.class);

	private static boolean indexar = false;

	@SuppressWarnings("unchecked")
	public void contextInitialized(ServletContextEvent event) {
		ServletContext context = event.getServletContext();
		Enumeration<String> params = context.getInitParameterNames();

		while (params.hasMoreElements()) {
			String param = (String) params.nextElement();
			String value = context.getInitParameter(param);
			logger.debug("InicializadorIndiceEmpresas ContextParametros : "+param + " valor:_"+value);
			if (param.startsWith("indexado.empresas")) {
				if (value.equalsIgnoreCase("true")) {
					indexar = true;
				}
			}
		}
	}

	public void contextDestroyed(ServletContextEvent event) {
	}

	public void schedule() {
		logger.info("InicializadorIndiceEmpresas, atributo indexar: " + indexar);
		if (indexar) {
			logger.debug("Indexando empresas "
					+ DateUtils.format(new Date(), DateUtils.LONG_SEC));
			EmpresasIndex.initialize();
			logger.debug("Finalizando Indexado empresas "
					+ DateUtils.format(new Date(), DateUtils.LONG_SEC));
		}
	}

	public void unschedule() {
	}

}
