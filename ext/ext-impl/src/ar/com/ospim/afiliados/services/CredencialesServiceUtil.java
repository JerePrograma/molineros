package ar.com.ospim.afiliados.services;

import java.util.List;
import java.util.Map;

import ar.com.ospim.afiliados.NoSuchAfiliadoEntryException;
import ar.com.ospim.afiliados.beans.Afiliado;
import ar.com.ospim.webservice.dto.AfiliacionPrevencionDTO;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.model.User;

/**
 * <a href="CredencialesServiceUtil .java.html"><b><i>View Source</i></b></a>
 * 
 * <p>
 * This class provides static methods for the
 * <code>ar.com.ospim.afiliados.services.CredencialesServiceUtil </code>
 * bean. The static methods of this class calls the same methods of the bean
 * instance. It's convenient to be able to just write one line to call a method
 * on a bean instead of writing a lookup call and a method call.
 * </p>
 *
 * @author Federico Brachi
 *
 * @see ar.com.ospim.afiliados.services.CredencialesServiceImpl
 *
 */
public class CredencialesServiceUtil {
	
	private static Log _log = LogFactoryUtil.getLog(CredencialesServiceUtil.class);
	private static CredencialesServiceImpl instance=null;
	
	public static CredencialesServiceImpl getInstance(){
		if(null==instance){
			instance=new CredencialesServiceImpl();			
		}
		return instance;
	}
	
	public static int generaLoteAImprimir(Map<String,Afiliado>credenciales, User user) throws Exception{
		_log.debug("generando lote");
		return getInstance().generaLoteAImprimir(credenciales, user);
	}
	
	public static int generaLoteAImprimir(List<Afiliado>credenciales, User user) throws Exception{
		return getInstance().generaLoteAImprimir(credenciales, user);
	}
	
	
	public static boolean validarAfiliadoCredencialPrevencion(AfiliacionPrevencionDTO credencial) throws NoSuchAfiliadoEntryException{
		boolean result = false;
		
		try {
			result = getInstance().validarAfiliadoCredencialPrevencion(credencial);
		}catch(NoSuchAfiliadoEntryException e){
			 throw new NoSuchAfiliadoEntryException(e);
		}
		
		return result;
	}
	
	public static int actualizarCredencialPrevencion(AfiliacionPrevencionDTO credencial, String user) throws Exception{
		
		return getInstance().actualizarCredencialPrevencion(credencial, user);
		
	}
	
	public static int insertarCredencial(String cuilTitular , int inte, String user){
		
		try {
			return getInstance().insertarCredencial(cuilTitular, inte, user);
		} catch (Exception e) {
			_log.debug("error al crear credenciales del afiliado exento de copago", e);
		}
		return 0;
		
	}
	
	
	public static int validarExisteExentoCopago(String cuil_titular, int inte) {
		return  getInstance().validarExisteExentoCopago(cuil_titular,inte);
	}
	
	
}
