package ar.com.ospim.autorizaciones.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.liferay.portal.SystemException;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.model.User;



import com.sun.star.sdbc.SQLException;



import ar.com.ospim.autorizaciones.beans.SituacionMedica;
import ar.com.ospim.autorizaciones.beans.BusquedaSituacionMedicaFiltro;
import ar.com.ospim.autorizaciones.beans.ItemSituacionMedicaTotal;

import ar.com.ospim.autorizaciones.exceptions.ImposibleBorrarSituacionMedicaException;



public class SituacionesMedicasServiceUtil {		

	
	private static Log _log = LogFactoryUtil
			.getLog(SituacionesMedicasServiceImpl.class);

	private static SituacionesMedicasServiceImpl  instance = null;

	public static SituacionesMedicasServiceImpl  getInstance() {
		if (null == instance) {
			instance = new SituacionesMedicasServiceImpl();
		}
		return instance;
	}

//Date fechaDesde, Date fechaHasta,int inte, String cuilTitular, int tipoSituMedica,Integer pagina 
	public static List<ItemSituacionMedicaTotal> buscarSituacionesMedicasTotales(BusquedaSituacionMedicaFiltro filtro  
			) throws Exception {
		//, fechaDesde, fechaHasta, inte, cuilTitular, tipoSituMedica, pagina
		return getInstance().buscarSituacionesMedicasTotales( filtro ); 
	}
	
	public static int insertar(SituacionMedica      situMedica  , User user) throws Exception{
		int idSituMedica = 0;			
		idSituMedica= getInstance().insertar(situMedica  , user);
		return idSituMedica;
	}
	
	public static void update(SituacionMedica   situMedica , User user) throws Exception{
		getInstance().actualizar(situMedica, user );
		}
	
	public static SituacionMedica getSituacionMedica (int id , String  cuil , int inte) throws Exception {		
		return getInstance().getSituacionMedica(id , cuil , inte   ) ;
	}
	
	public static void borrar(int id, User user)
			throws ImposibleBorrarSituacionMedicaException,
			SQLException, java.sql.SQLException {
		    getInstance().borrar(id, user.getScreenName());
	}
	

	public static List<ItemSituacionMedicaTotal> buscarSituacionesMedicasVigente(BusquedaSituacionMedicaFiltro filtro  
			) throws Exception {
		//, fechaDesde, fechaHasta, inte, cuilTitular, tipoSituMedica, pagina
		return getInstance().buscarSituacionesMedicasVigente( filtro ); 
	}
	
	
}
