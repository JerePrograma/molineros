package ar.com.ospim.farmacia.services;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import ar.com.ospim.farmacia.beans.Medicamento;
import ar.com.ospim.farmacia.beans.Vademecum;
import ar.com.ospim.farmaciaOspim.beans.BusquedaVademecumFiltro;
import ar.com.ospim.farmaciaOspim.beans.ItemVademecumTotal;
import ar.com.ospim.farmaciaOspim.exceptions.ImposibleBorrarMedicamentoOspimException;
import ar.com.ospim.prestadores.exception.DuplicatePrestadorIdException;
import ar.com.ospim.util.ConnectionHelper;
import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.model.User;



public class BusquedaVademecumServiceImpl {

	private static Log _log = LogFactoryUtil
			.getLog(BusquedaVademecumServiceImpl.class);

	
	public List<ItemVademecumTotal> getBusquedaVademecumTotal(BusquedaVademecumFiltro  filtro) {
		Connection con = null;
		CallableStatement stmt=null;
		String sql; 
		List<ItemVademecumTotal> listaMedicamentos= null;
		try {
			if (filtro.isBuscaEnHistoricoDeVademecum()){
				sql = "{call buscar_vademecum_historico(?,?,?,?,?,?,?,?,?,?,?,?,?)}";
			}else{
				sql = "{call buscar_vademecum (?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";	
			}
			
			_log.debug("obteniendo conexion");
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			if(filtro.getRegistro() !=0){
				stmt.setInt(1, filtro.getRegistro());	
			}else{
				stmt.setNull(1, Types.INTEGER);	
			}
			if(filtro.getTroquel() !=0){
				stmt.setInt(2, filtro.getTroquel() );
			}else{
				stmt.setNull(2, Types.INTEGER);
			}
			stmt.setString(3, (null!=filtro.getNombre()  &&filtro.getNombre().trim().equals(""))?null:filtro.getNombre());
			stmt.setString(4, (null!=filtro.getPresentacion() &&filtro.getPresentacion().trim().equals(""))?null:filtro.getPresentacion());
			stmt.setString(5, (null!=filtro.getLaboratorio()&&filtro.getLaboratorio().trim().equals(""))?null:filtro.getLaboratorio() );
			stmt.setString(6, (null!=filtro.getDroga()&&filtro.getDroga().trim().equals(""))?null:filtro.getDroga());
			stmt.setBoolean(7, filtro.isPmiMadre());
			stmt.setBoolean(8, filtro.isPmiHijo());				
			stmt.setBoolean(9, filtro.isAco());
			stmt.setBoolean(10, filtro.isVadeGral());
			stmt.setBoolean(11, filtro.isSoloInformadosxSss());	
			stmt.setBoolean(12, filtro.isPadronMolineros());

			if (filtro.isBuscaEnHistoricoDeVademecum()){
				stmt.setInt(13, filtro.getPagina() );
			}else{
				stmt.setBoolean(13, filtro.isBuscasoloNuevasAltas()  );
				stmt.setInt(14, filtro.getPagina() );
			}			
			
			ResultSet rs = stmt.executeQuery();
			listaMedicamentos = new ArrayList<ItemVademecumTotal>();
			while (rs.next()) {
				ItemVademecumTotal bp = Vademecum.getMappingTotal(rs, "vade_");
				listaMedicamentos.add(bp);
			}			
		} catch (Exception e) {
			_log.error(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return listaMedicamentos;
	}	


	

	public int insertar(Medicamento medicacion , User user ) throws SystemException, DuplicatePrestadorIdException {
		
		Connection con = null;
		CallableStatement stmt = null ;
		String screenName = user.getScreenName();
		int idMedicacion =0;
		String sql  = "{call farmacia.insertar_medicacion(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
		try {			
			
			con = ConnectionHelper.getConnectionForTransaction();		
			stmt = con.prepareCall(sql.toString());
			stmt.registerOutParameter(1, Types.INTEGER);
			if (null != medicacion.getPeriodo() ) {
				stmt.setDate(1, new java.sql.Date(medicacion.getPeriodo().getTime()));						
			} else {					
				stmt.setNull(1, Types.DATE);
			}
			if (null != medicacion.getFecha()) {
				stmt.setDate(2, new java.sql.Date(medicacion.getFecha().getTime()));						
			} else {					
				stmt.setNull(2, Types.DATE);
			}			
			stmt.setInt (3, medicacion.getRegistro()  );
			stmt.setString(4, screenName);
			stmt.setString (5, medicacion.getBaja() );
			stmt.setInt (6, medicacion.getTroquel()   );
			stmt.setString (7, medicacion.getNombre() );
			stmt.setString (8, medicacion.getPresentacion()  );
			stmt.setString (9, medicacion.getLaboratorio()   );
			stmt.setBigDecimal(10, medicacion.getPrecio()  );
			stmt.setString (11, medicacion.getCod_barra()   );
			stmt.setString (12, medicacion.getAccion()    );
			stmt.setString (13, medicacion.getDroga()     );
			stmt.setString (14, medicacion.getTipoVenta()      );
			stmt.setString (15, medicacion.getIva()       );
			stmt.setBoolean(16 , medicacion.getManualDat() );
			idMedicacion = stmt.executeUpdate();
			if(stmt.getInt(1) > 0){				
				idMedicacion =stmt.getInt(1);
			}
			con.commit();
		} catch (SQLException e) {
			_log.error("Error al insertar medicacion", e);
			ConnectionHelper.rollback(con);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return idMedicacion ;
	}

public boolean actualizar(Vademecum  medicacion, User user ) throws SystemException {
	Connection con = null;
	CallableStatement stmt = null ;
	boolean resp=true;
	try {
		String sql  = "{call update_vademecum(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
		con = ConnectionHelper.getConnection();
		stmt = con.prepareCall(sql.toString());		
		stmt.setString (1, medicacion.getDroga());
		stmt.setString (2, medicacion.getNombre());
		stmt.setString (3, medicacion.getPresentacion());
		stmt.setString (4, medicacion.getLaboratorio());
		stmt.setString (5, medicacion.getAccion());
		stmt.setInt (6, medicacion.getTroquel());
		stmt.setInt (7, medicacion.getRegistro());
		stmt.setDouble(8, medicacion.getPorc_sssalud());
		stmt.setDouble(9,medicacion.getPmoe_n());
		stmt.setBoolean(10, medicacion.isPmiMadre());
		stmt.setBoolean(11, medicacion.isPmiHijo());
		stmt.setBoolean(12, medicacion.isAnticoncepcion());
		stmt.setBoolean(13, medicacion.isVademecumGral());
		if (null != medicacion.getPeriodoAltasBajas() ) {
			stmt.setDate(14, new java.sql.Date(medicacion.getPeriodoAltasBajas().getTime()));						
		} else {					
			stmt.setNull(14, Types.DATE);
		}
		stmt.setString(15, user.getScreenName() );
		stmt.executeUpdate();
	} catch (Exception e) {
		_log.error("Error al actualizar vademecum.", e);
		ConnectionHelper.rollback(con);		
		throw new SystemException(e);
	}  finally {
		ConnectionHelper.cerrar(stmt, con);
	}
	return resp ;
}

public void borrar(int idegistro , int idTroquel , String screenName) throws SQLException, ImposibleBorrarMedicamentoOspimException {
	Connection con = null;
	CallableStatement stmt = null;
	try {
		String sql = "{call borra_vademecum (?,?,?)}";
		con = ConnectionHelper.getConnection();
		stmt = con.prepareCall(sql.toString());
		stmt.setInt(1, idegistro );
		stmt.setInt(2, idTroquel );	
		stmt.setString(3, screenName);
		ResultSet rs = stmt.executeQuery();
		while (rs.next()) {
			if (rs.getInt(1) == 0) {
				throw new ImposibleBorrarMedicamentoOspimException ();
			}
		}
	} catch (ImposibleBorrarMedicamentoOspimException  e) {
		_log.error("Error al borrar el vademecum", e);
		throw e;
	} finally {
		ConnectionHelper.cerrar(stmt, con);
	}
}

public int insertar(Vademecum medicacion , User user ) throws SystemException {
	
	Connection con = null;
	CallableStatement stmt = null ;
	int idMedicacion =0;
	String sql  = "{call insertar_vademecum(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
	try {			
		
		con = ConnectionHelper.getConnection(); 		
		stmt = con.prepareCall(sql.toString());
		stmt.registerOutParameter(1, Types.INTEGER);
		stmt.setString(1, medicacion.getDroga() );
		stmt.setString(2, medicacion.getNombre() );
		stmt.setString(3, medicacion.getPresentacion()  );
		stmt.setString(4, medicacion.getLaboratorio()   );
		stmt.setString(5, medicacion.getAccion()  );  
		stmt.setInt (6, medicacion.getTroquel()   );
		stmt.setInt (7, medicacion.getRegistro()  );
		stmt.setDouble(8, medicacion.getPorc_sssalud()  );
		stmt.setDouble(9, medicacion.getPmoe_n()  );		
		stmt.setString(10, user.getScreenName());
		stmt.setBoolean(11, medicacion.isPmiMadre() );
		stmt.setBoolean(12, medicacion.isPmiHijo());
		stmt.setBoolean(13, medicacion.isAnticoncepcion() );
		stmt.setBoolean(14, medicacion.isVademecumGral()  );		
		if (null != medicacion.getPeriodoAltasBajas()) {
			stmt.setDate(15, new java.sql.Date(medicacion.getPeriodoAltasBajas().getTime()));						
		} else {					
			stmt.setNull(15, Types.DATE);
		}
		
		idMedicacion = stmt.executeUpdate();
		if(stmt.getInt(1) > 0){				
			idMedicacion =stmt.getInt(1);
		}
		
	} catch (SQLException e) {
		_log.error("Error al insertar vademecum", e);
		ConnectionHelper.rollback(con);
	} finally {
		ConnectionHelper.cerrar(stmt, con);
	}
	return idMedicacion ;
}


public List<Vademecum> getHistoricoDePrecios(int idRegistro  ) {
	Connection con = null;
	CallableStatement stmt=null;
	List<Vademecum> listaVademecums= null;
	try {
		String sql = "{call buscar_vademecum_precios_medicamentos(?)}";
		con = ConnectionHelper.getConnection();
		stmt = con.prepareCall(sql.toString());
		stmt.setInt(1, idRegistro );
		ResultSet rs = stmt.executeQuery();
		listaVademecums= new ArrayList<Vademecum>();
		while (rs.next()) {
			Vademecum  bp = Vademecum.getMappingPreciosHistoricos(rs, "med_");
			listaVademecums.add(bp);
		}			
	} catch (Exception e) {
		_log.error(e);
	} finally {
		ConnectionHelper.cerrar(stmt, con);
	}
	return listaVademecums;
}	


public Vademecum getVademecum (int idRegistro , boolean buscaHistorico , Date periodoHistorico   ) throws SystemException {
	Connection con = null;
	CallableStatement stmt = null;
	Vademecum  vademecum = null;
	String sql; 
	try {
		if (buscaHistorico ){
			sql = "{call buscar_vademecum_historico_by_registro(?,?)}";	
		}else{
			sql = "{call buscar_vademecum_by_registro(?)}";
		}
		
		con = ConnectionHelper.getConnection();
		stmt = con.prepareCall(sql.toString());
		stmt.setInt(1, idRegistro );
		if (buscaHistorico ) {
			stmt.setDate(2, new java.sql.Date(periodoHistorico.getTime()));
		}
		ResultSet rs = stmt.executeQuery();
		while (rs.next()) {
			vademecum = Vademecum.getMappingxRegistroTroquel(rs, "vade_") ;
						  }
	} catch (Exception e) {
		_log.error("Error al buscar vademecum", e);
	} finally {
		ConnectionHelper.cerrar(stmt, con);
	}
			
	return vademecum ;
}


}
