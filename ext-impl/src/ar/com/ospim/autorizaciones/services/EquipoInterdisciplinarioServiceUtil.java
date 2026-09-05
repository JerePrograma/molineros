package ar.com.ospim.autorizaciones.services;

import java.util.Date;
import java.util.List;

import com.liferay.portal.model.User;
import com.sun.star.sdbc.SQLException;

import ar.com.ospim.autorizaciones.beans.EquipoInterdisciplinario;
import ar.com.ospim.autorizaciones.exceptions.ImposibleBorrarEquipoInterdisiciplinarioException;


public class EquipoInterdisciplinarioServiceUtil {		

	private static EquipoInterdisciplinarioServiceImpl  instance = null;

	public static EquipoInterdisciplinarioServiceImpl  getInstance() {
		if (null == instance) {
			instance = new EquipoInterdisciplinarioServiceImpl ();
		}
		return instance;
	}

	
	
	public static List<EquipoInterdisciplinario> buscarEquipoInterRegistros(
			String estado , Date fecha,  int inte, String cuilTitular, int nroRegistro, String motivo ) throws Exception {
		return getInstance().buscarEquipoInterRegistros(estado , fecha,  inte, cuilTitular, nroRegistro,motivo );
	}
	

	
	public static int insertar(EquipoInterdisciplinario equipoInterDisciplinario , User user) throws Exception{
		int idRegEquipoInter = 0;	
		
		idRegEquipoInter = getInstance().insertar(equipoInterDisciplinario  , user.getScreenName());
		
		return idRegEquipoInter ;
	}
	
	
	public static void update(EquipoInterdisciplinario equipo , User user) throws Exception{			
		
		getInstance().actualizar(equipo , user.getScreenName());	
	}
	
	
	public static EquipoInterdisciplinario getEquipoInterdisciplinario (int id) throws Exception {
		
		return getInstance().getEquipoInterdisciplinario (id) ;
	}
	
	public String [] getDictamenesDelEquipoInterdisciplinario(int id) throws Exception {
		return getInstance().getDictamenesDelEquipoInterdisciplinario(id) ;
	}
	/*
	public static List<PrestacionesReclamo >  getPrestacionesAsociadas(int casoasociado ) throws Exception {
		return getInstance().retornaPrestacionesAsociadasxCasoVinculado(casoasociado );
	}
	 */
	
	public static void borrar(int id, User user)
			throws ImposibleBorrarEquipoInterdisiciplinarioException,
			SQLException, java.sql.SQLException {
		    getInstance().borrar(id, user.getScreenName());
		     
		     
	}

	
	public static int getEsFirmante(User user) throws Exception {
		return getInstance().getEsFirmante(user);
		
	}
	
	public static String getUsuarioUltimaModificacionDictamen(int idEquipoInterdisciplinario, int tipoDictamen) 
			throws Exception {

	    return getInstance().getUsuarioUltimaModificacionDictamen(
	        idEquipoInterdisciplinario,
	        tipoDictamen
	    );
	}
}
