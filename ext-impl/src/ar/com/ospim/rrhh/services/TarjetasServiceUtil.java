package ar.com.ospim.rrhh.services ;


import java.sql.Connection;
import java.util.List;

import com.liferay.portal.SystemException;
import com.liferay.portal.model.User;

import ar.com.ospim.rrhh.beans.BusquedaTarjetasFiltro;
import ar.com.ospim.rrhh.beans.ItemTarjetasTotal;
import ar.com.ospim.rrhh.beans.TarjetaAcceso;
import ar.com.ospim.rrhh.exceptions.ImposibleBorrarTarjetaAcessoException;
import ar.com.ospim.util.ConnectionHelper;


public class TarjetasServiceUtil {		

	private static TarjetasServiceImpl  instance = null;

	public static TarjetasServiceImpl  getInstance() {
		if (null == instance) {
			instance = new TarjetasServiceImpl ();
		}
		return instance;
	}
	
	
	public static List<ItemTarjetasTotal > buscarTarjetasTotales(BusquedaTarjetasFiltro filtro  
			) throws Exception {
		return getInstance().buscarTarjetasTotales( filtro ); 
	}
	
	public static List<TarjetaAcceso > getHistoricoTarjetaEmpleado(int legajoEmpleado 
			) throws Exception {
		return getInstance().getHistoricoTarjetaEmpleado(legajoEmpleado ) ; 
	}
	
	public static int insertar(TarjetaAcceso tarjetaAcceso  , User user) throws Exception{
		return   getInstance().insertar(tarjetaAcceso  , user.getScreenName(),null);
	}
	
	public static int updatePorCambioDeTarjeta  (TarjetaAcceso tarjetaAcceso  , User user) throws Exception{
		Connection connection = null;
		int idRegistroNuevo=0;
		try {		
			connection = ConnectionHelper.getConnectionForTransaction();
			
		    getInstance().borrar(tarjetaAcceso.getId(), user.getScreenName(), connection);
		    idRegistroNuevo=getInstance().insertar(tarjetaAcceso, user.getScreenName() , connection);
		    connection.commit();
		
		} catch (Exception e) {
			if(null!=connection){
				connection.rollback();
				throw e;
			}			
		} finally {
				connection.close();
		}
		
		return idRegistroNuevo ;

	}
	
	public static void update(TarjetaAcceso tarjetaAcceso  , User user) throws Exception{
		getInstance().actualizar(tarjetaAcceso  , user.getScreenName());	
	}
	
	public static TarjetaAcceso getTarjetaAcceso (int idTarjetaAcceso) throws Exception {
		return getInstance().getTarjetaAcceso(idTarjetaAcceso);
	}
	
	public static TarjetaAcceso getTarjetaxNroTarjetaoLegajo (int idTarjetaAcceso, int legajo, int idTarjeta) throws Exception {
		return getInstance().getTarjetaxNroTarjetaoLegajo(idTarjetaAcceso, legajo, idTarjeta );
	}
	
	public static void borrar(int idTarjeta, User user)
			throws ImposibleBorrarTarjetaAcessoException,SystemException {
		
		    getInstance().borrar(idTarjeta, user.getScreenName(),null);
		     
	}

	
	
}
