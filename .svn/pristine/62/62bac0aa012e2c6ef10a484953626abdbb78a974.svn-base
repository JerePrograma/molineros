package ar.com.ospim.liquidaciones.services;

import java.util.Date;
import java.util.List;

import ar.com.ospim.afiliados.beans.Afiliado;
import ar.com.ospim.liquidaciones.WebKeysLiquidaciones;
import ar.com.ospim.liquidaciones.beans.Catastro;

import com.liferay.portal.SystemException;
import com.liferay.portal.model.User;

/**
 * <a href="DebitoServiceUtil .java.html"><b><i>View Source</i></b></a>
 * 
 * <p>
 * This class provides static methods for the
 * <code>ar.com.ospim.afiliados.services.CatastroServiceUtil </code>
 * bean. The static methods of this class calls the same methods of the bean
 * instance. It's convenient to be able to just write one line to call a method
 * on a bean instead of writing a lookup call and a method call.
 * </p>
 *
 * @author Carlos Rivas
 *
 * @see ar.com.ospim.afiliados.services.CatastroServiceImpl
 *
 */
public class CatastroServiceUtil {
	
//	private static Log _log = LogFactoryUtil.getLog(CatastroServiceUtil.class);
	private static CatastroServiceImpl instance=null;
	
	public static CatastroServiceImpl getInstance(){
		if(null==instance){
			instance=new CatastroServiceImpl();			
		}
		return instance;
	}

	public static List<Catastro> grabaCatastroRetornaLista(Date prestacionFecha, int id_codigo, String codigo, String pieza1, String pieza2, String pieza3, String pieza4, String pieza5, String pieza6, String pieza7, String pieza8, String pieza9, String pieza10, 
			String cara1, String cara2, String cara3, String cara4, String cara5, String cara6, String cara7, String cara8, String cara9, String cara10, String cuil_titular, int inte, User user) throws Exception{						
		if (pieza1.length() > 0) {
			Catastro catastro = new Catastro(new Afiliado(cuil_titular, inte), pieza1, cara1, prestacionFecha, id_codigo, codigo);
			CatastroServiceUtil.save(catastro, user);
		}
		if (pieza2.length() > 0) {
			Catastro catastro = new Catastro(new Afiliado(cuil_titular, inte), pieza2, cara2, prestacionFecha, id_codigo, codigo);
			CatastroServiceUtil.save(catastro, user);
		}
		if (pieza3.length() > 0) {
			Catastro catastro = new Catastro(new Afiliado(cuil_titular, inte), pieza3, cara3, prestacionFecha, id_codigo, codigo);
			CatastroServiceUtil.save(catastro, user);
		}
		if (pieza4.length() > 0) {
			Catastro catastro = new Catastro(new Afiliado(cuil_titular, inte), pieza4, cara4, prestacionFecha, id_codigo, codigo);
			CatastroServiceUtil.save(catastro, user);
		}
		if (pieza5.length() > 0) {
			Catastro catastro = new Catastro(new Afiliado(cuil_titular, inte), pieza5, cara5, prestacionFecha, id_codigo, codigo);
			CatastroServiceUtil.save(catastro, user);
		}
		if (pieza6.length() > 0) {
			Catastro catastro = new Catastro(new Afiliado(cuil_titular, inte), pieza6, cara6, prestacionFecha, id_codigo, codigo);
			CatastroServiceUtil.save(catastro, user);
		}
		if (pieza7.length() > 0) {
			Catastro catastro = new Catastro(new Afiliado(cuil_titular, inte), pieza7, cara7, prestacionFecha, id_codigo, codigo);
			CatastroServiceUtil.save(catastro, user);
		}
		if (pieza8.length() > 0) {
			Catastro catastro = new Catastro(new Afiliado(cuil_titular, inte), pieza8, cara8, prestacionFecha, id_codigo, codigo);
			CatastroServiceUtil.save(catastro, user);
		}
		if (pieza9.length() > 0) {
			Catastro catastro = new Catastro(new Afiliado(cuil_titular, inte), pieza9, cara9, prestacionFecha, id_codigo, codigo);
			CatastroServiceUtil.save(catastro, user);
		}
		if (pieza10.length() > 0) {
			Catastro catastro = new Catastro(new Afiliado(cuil_titular, inte), pieza10, cara10, prestacionFecha, id_codigo, codigo);
			CatastroServiceUtil.save(catastro, user);
		}
		return getInstance().buscaCatastro(cuil_titular, inte);
	}
	
	public static List<Catastro> grabaCatastroCompletoRetornaLista(Date prestacionFecha, String cuil_titular, int inte, int id_codigo, String codigo, User user) throws Exception{
		for (int pieza = 11 ; pieza <= 18 ; pieza++) {
			try{
				Catastro catastro = new Catastro(new Afiliado(cuil_titular, inte), String.valueOf(pieza), "", prestacionFecha, id_codigo, codigo);
				CatastroServiceUtil.save(catastro, user);
			}
			catch (Exception e) {}
		}
		for (int pieza = 21 ; pieza <= 28 ; pieza++) {
			try{
				Catastro catastro = new Catastro(new Afiliado(cuil_titular, inte), String.valueOf(pieza), "", prestacionFecha, id_codigo, codigo);
				CatastroServiceUtil.save(catastro, user);
			}
			catch (Exception e) {}
		}
		for (int pieza = 31 ; pieza <= 38 ; pieza++) {
			try{
				Catastro catastro = new Catastro(new Afiliado(cuil_titular, inte), String.valueOf(pieza), "", prestacionFecha, id_codigo, codigo);
				CatastroServiceUtil.save(catastro, user);
			}
			catch (Exception e) {}
		}
		for (int pieza = 41 ; pieza <= 48 ; pieza++) {
			try{
				Catastro catastro = new Catastro(new Afiliado(cuil_titular, inte), String.valueOf(pieza), "", prestacionFecha, id_codigo, codigo);
				CatastroServiceUtil.save(catastro, user);
			}
			catch (Exception e) {}
		}
		return getInstance().buscaCatastro(cuil_titular, inte);
	}
	
	public static List<Catastro> buscaCatastro(String cuil_titular, int inte) throws Exception{
		return getInstance().buscaCatastro(cuil_titular, inte);
	}	
	
	public static String imagenDadaPieza(List<Catastro> lista, int pieza) {
		String imagen = WebKeysLiquidaciones.IMAGEN_NORMAL;
		for (Catastro catastro : lista) {
			if (catastro.getPieza().equals(String.valueOf(pieza))) {
				return WebKeysLiquidaciones.IMAGEN_EXTRACCION;
			}
		}				
		return imagen;
	}
	
	public static List<Catastro> borraCatastroRetornaLista(int id, String cuil_titular, int inte, User user)throws Exception {				
		CatastroServiceUtil.delete(id, user);
		List<Catastro> catastro = getInstance().buscaCatastro(cuil_titular, inte);
		return catastro;
	}
	
	public static void save(Catastro catastro, User user)
	throws SystemException {
			getInstance().save(catastro,
					user.getScreenName());
	}
	
	public static void delete(int id, User user)
	throws SystemException {
		getInstance().delete(id,
				user.getScreenName());
	}
}