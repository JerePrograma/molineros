package ar.com.ospim.afiliados.services;

import java.util.Date;
import java.util.List;

import ar.com.ospim.afiliados.beans.EnvioBonos;
import ar.com.ospim.afiliados.exceptions.DuplicateEnvioBonosException;
import ar.com.ospim.afiliados.exceptions.EnvioBonosNoExisteEnSeccionalException;

import com.liferay.portal.model.User;

/**
 * <a href="EnviaBonosServiceUtil .java.html"><b><i>View Source</i></b></a>
 * 
 * <p>
 * This class provides static methods for the
 * <code>ar.com.ospim.afiliados.services.EnviaBonosServiceUtil </code>
 * bean. The static methods of this class calls the same methods of the bean
 * instance. It's convenient to be able to just write one line to call a method
 * on a bean instead of writing a lookup call and a method call.
 * </p>
 *
 * @author Federico Brachi
 *
 * @see ar.com.ospim.afiliados.services.EnviaBonosServiceImpl
 *
 */
public class EnviaBonosServiceUtil {	

	private static EnviaBonosServiceImpl instance=null;
	
	public static EnviaBonosServiceImpl getInstance(){
		if(null==instance){
			instance=new EnviaBonosServiceImpl();			
		}
		return instance;
	}
	
	public static List<EnvioBonos> grabaEnvioBonosRetornaLista(int tipoBono, int seccional, Date fechaEnvio, int bono_desde,int bono_hasta, User user) throws DuplicateEnvioBonosException,Exception {
		return getInstance().grabaEnvioBonosRetornaLista(tipoBono, seccional, fechaEnvio, bono_desde, bono_hasta, user);
	}
	public static int grabaBonosRetornaLista(int tipoBono, Date fechaEnvio, int bono_desde,int bono_hasta, User user) throws DuplicateEnvioBonosException,Exception {
		return getInstance().grabaBonosRetornaLista(tipoBono, fechaEnvio, bono_desde, bono_hasta, user);
	}
	public static int rindeEnvioBonosRetornaLista(int tipoBono, int seccional, Date fechaEnvio, int bono_desde,int bono_hasta, User user) throws EnvioBonosNoExisteEnSeccionalException,Exception {
		return getInstance().rindeEnvioBonosRetornaLista(tipoBono, seccional, fechaEnvio, bono_desde, bono_hasta, user);
	}
		
	public static int anulaEnvioBonosRetornaLista(int tipoBono, int seccional, Date fechaAnula, int bono_desde,int bono_hasta, User user) throws EnvioBonosNoExisteEnSeccionalException,Exception {
		return getInstance().anulaEnvioBonosRetornaLista(tipoBono, seccional, fechaAnula, bono_desde, bono_hasta, user);
	}
	
	public static List<EnvioBonos> buscaBonosRetornaLista(int tipoBono, int seccional, Date fechaDesde, Date fechaHasta, int bono_desde,int bono_hasta, boolean rendidos, boolean sin_rendir, boolean sin_enviar, boolean anulados) throws Exception {
		return getInstance().buscaBonosRetornaLista(tipoBono, seccional, fechaDesde, fechaHasta, bono_desde, bono_hasta, rendidos, sin_rendir, sin_enviar,anulados);
	}
	public static int liberaEnvioBonosRetornaLista(int id_envio, User user) throws Exception {
		return getInstance().liberaEnvioBonosRetornaLista(id_envio, user);
	}	
	
}
