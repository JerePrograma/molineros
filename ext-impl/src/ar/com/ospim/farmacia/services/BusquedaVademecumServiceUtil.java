package ar.com.ospim.farmacia.services;

import java.util.List;
import com.liferay.portal.model.User;
import com.sun.star.sdbc.SQLException;
import ar.com.ospim.farmacia.beans.Vademecum;
import ar.com.ospim.farmaciaOspim.beans.BusquedaVademecumFiltro;
import ar.com.ospim.farmaciaOspim.beans.ItemVademecumTotal;
import ar.com.ospim.farmaciaOspim.exceptions.ImposibleBorrarMedicamentoOspimException;

import java.util.Date;

public class BusquedaVademecumServiceUtil {

	private static BusquedaVademecumServiceImpl instance = null;

	public static BusquedaVademecumServiceImpl getInstance() {
		if (null == instance) {
			instance = new BusquedaVademecumServiceImpl();
		}
		return instance;
	}
  
	public static List<ItemVademecumTotal> getBusquedaVademecumTotal ( BusquedaVademecumFiltro filtro ) {
		List<ItemVademecumTotal> vademecums= getInstance().getBusquedaVademecumTotal(filtro );
		return vademecums;
	}	
	
	public static void borrar(int idRegistro, int idTroquel , User user)
			throws ImposibleBorrarMedicamentoOspimException,
			SQLException, java.sql.SQLException, ImposibleBorrarMedicamentoOspimException{
		    getInstance().borrar(idRegistro, idTroquel, user.getScreenName()); 
	}
	
	public static Vademecum  getVademecum (int idRegistro, boolean buscaHistorico , Date periodoHistorico   ) throws Exception {		
		return getInstance().getVademecum (idRegistro ,buscaHistorico ,periodoHistorico    ) ;
	}
	
	public static List<Vademecum> getHistoricoDePrecios (int idRegistro ) throws Exception {		
		return getInstance().getHistoricoDePrecios (idRegistro ) ;
	}
	
	public static int insertar(Vademecum medicacion   , User user) throws Exception{
		int idMedicacion = 0;			
		idMedicacion = getInstance().insertar(medicacion , user);
		return idMedicacion ;
	}

	public static void actualizar(Vademecum  medicacion , User user) throws Exception{
		getInstance().actualizar(medicacion , user );
		}
	

}
