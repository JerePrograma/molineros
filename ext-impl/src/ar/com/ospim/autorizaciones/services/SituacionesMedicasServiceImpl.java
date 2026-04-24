package ar.com.ospim.autorizaciones.services;


import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.model.User;

import ar.com.ospim.autorizaciones.beans.BusquedaSituacionMedicaFiltro;
import ar.com.ospim.autorizaciones.beans.ItemSituacionMedicaTotal;
import ar.com.ospim.autorizaciones.beans.PatologiasSituacionMedica;
import ar.com.ospim.autorizaciones.beans.SituacionMedica;
import ar.com.ospim.autorizaciones.exceptions.ImposibleBorrarSituacionMedicaException;
import ar.com.ospim.prestadores.exception.DuplicatePrestadorIdException;
import ar.com.ospim.util.ConnectionHelper;


	
public class SituacionesMedicasServiceImpl {

	private static Log _log = LogFactoryUtil
			.getLog(ReclamoPrestacionServiceImpl.class);

	
	
	//, 	 Date fechaDesde , Date fechaHasta , 	int inte, String cuilTitular,int tipoSituMedica ,  Integer pagina 
	public List<ItemSituacionMedicaTotal> buscarSituacionesMedicasTotales(BusquedaSituacionMedicaFiltro  filtro ) throws SystemException,
			NumberFormatException, ParseException {

		Connection con = null;
		CallableStatement stmt = null;
		ArrayList<ItemSituacionMedicaTotal> listaSituacionesMedicasTotales = null;
		listaSituacionesMedicasTotales  = new ArrayList<ItemSituacionMedicaTotal>();
		
		try {
			String sql="" ;
			sql = "{call buscar_situaciones_medicas(?,?,?,?,?,?)}";	
				
			
			_log.debug("obteniendo conexion");
			con = ConnectionHelper.getConnection();
			
			stmt = con.prepareCall(sql.toString());										
			
			if (filtro.gettipoSituMedica()== 0) {
				stmt.setNull(1, Types.INTEGER);
			} else {
				stmt.setInt(1, filtro.gettipoSituMedica()); 
				//pagina=0;
			}
			
			stmt.setDate(2, filtro.getFechaDesde()== null ? null : new java.sql.Date(
					filtro.getFechaDesde().getTime()));  
			stmt.setDate(3, filtro.getFechaHasta()  == null ? null : new java.sql.Date(
					filtro.getFechaHasta().getTime()));					
			
			if (filtro.getCuilTitular()  ==null ||  filtro.getCuilTitular().equals("")   ) {
				stmt.setNull(4, Types.VARCHAR );
			} else {
				stmt.setString(4,filtro.getCuilTitular() ); 
			}
			
			
			if ( filtro.getCuilTitular() ==null ||  filtro.getCuilTitular().equals("")   ) {
				stmt.setNull(5, Types.INTEGER);
			} else {
				stmt.setInt(5, filtro.getInte()); 
			}			
			
			stmt.setInt(6, filtro.getPagina()); 
			
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				ItemSituacionMedicaTotal situacionMedica = ItemSituacionMedicaTotal.getMappingBuscadorTotal(rs, "sm_") ;
				listaSituacionesMedicasTotales.add(situacionMedica );
			}
		} catch (Exception e) {
			_log.error(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return listaSituacionesMedicasTotales  ;
	}
	

	
public int insertar(SituacionMedica situacionMedica , User user ) throws SystemException, DuplicatePrestadorIdException {
		
		Connection con = null;
		CallableStatement stmt = null, stmt2 = null, stmt3 = null;
		
		String screenName = user.getScreenName();
		int idSituacionMedica =0;
		
		String sql  = "{call insertar_situacion_medica(?,?,?,?,?,?,?)}";
		String sql2 = "{call insertar_situacion_medica_detalle(?,?,?,?,?,?,?,?,?,?,?)}";	
		try {			
			
			con = ConnectionHelper.getConnectionForTransaction();
		
			stmt = con.prepareCall(sql.toString());
			stmt.registerOutParameter(1, Types.INTEGER);
			stmt.setString(1, situacionMedica.getCuit_titular() );
			stmt.setInt (2, situacionMedica.getInte() );
			stmt.setInt (3, situacionMedica.getIdTipoSituMedica());
			if (null != situacionMedica.getFechaVigen_Desde()) {
				stmt.setDate(4, new java.sql.Date(situacionMedica.getFechaVigen_Desde().getTime()));						
			} else {					
				stmt.setNull(4, Types.DATE);
			}
			if (null != situacionMedica.getFechaVigen_Hasta()) {
				stmt.setDate(5, new java.sql.Date(situacionMedica.getFechaVigen_Hasta().getTime()));						
			} else {					
				stmt.setNull(5, Types.DATE);
			}			
			stmt.setString(6, screenName);
			stmt.setString(7, situacionMedica.getDetalleSituMedica() );
			idSituacionMedica = stmt.executeUpdate();
			if(stmt.getInt(1) > 0){				
				idSituacionMedica =stmt.getInt(1);
			}
//			*** GRABAR DETALLE DEL AFILIADO DISCAPACITADO O NO 	
			        stmt2 = con.prepareCall(sql2.toString());	
					stmt2.setString(1, situacionMedica.getCuit_titular() );
					stmt2.setInt (2, situacionMedica.getInte() );
					stmt2.setString(3, situacionMedica.getdiagnostico()  );					
					stmt2.setString(4, screenName);
					stmt2.setString(5, situacionMedica.getCie10());
					stmt2.setInt (6, idSituacionMedica  );
					stmt2.setBoolean(7, situacionMedica.isDiscapacitado() );
					stmt2.setInt (8, situacionMedica.getInte() );
					stmt2.setBoolean(9, situacionMedica.getDetalleDiscapacidad().isDependencia() );
					stmt2.setString(10, situacionMedica.getDetalleDiscapacidad().getTelefono_contacto() );
					stmt2.setString(11, situacionMedica.getDetalleDiscapacidad().getTiposDiscapacidadDelAfiliado() );
					
					stmt2.executeUpdate();
			con.commit();
	
		} catch (SQLException e) {
			_log.error("Error al insertar situacion medica y sus componentes", e);
			ConnectionHelper.rollback(con);
		} finally {
			ConnectionHelper.cerrar(stmt);
			ConnectionHelper.cerrar(stmt2);
			ConnectionHelper.cerrar(stmt3, con);
		}
		return idSituacionMedica ;
	}


public SituacionMedica    getSituacionMedica(int id , String   cuil , int inte ) throws SystemException {
	Connection con = null;
	CallableStatement stmt = null;
	SituacionMedica sitMedica = null;
	
	try {
		String sql = "{call buscar_situaciones_medicas_by_id_o_cuil_inte(?,?,?)}";
		con = ConnectionHelper.getConnection();
		stmt = con.prepareCall(sql.toString());
		if (id!=0) {
			stmt.setInt(1, id);						
		} else {					
			stmt.setNull(1, Types.INTEGER );
		}
		if (null != cuil ) {
			stmt.setString(2, cuil );						
		} else {					
			stmt.setNull(2, Types.VARCHAR );
		}
		if (inte==0){
			stmt.setNull(3, Types.INTEGER );			
		}else{
			stmt.setInt(3, inte);	
		}
		
		ResultSet rs = stmt.executeQuery();
		while (rs.next()) {
			sitMedica  = SituacionMedica.getMapping(rs, "sm_");
						  }
	} catch (Exception e) {
		_log.error("Error al buscar situacion medica", e);
	} finally {
		ConnectionHelper.cerrar(stmt, con);
	}
	// recupera las patologias del afiliado 
	sitMedica.setPatologias(this.getPatologiasDelAfiliadoDeLaSituacionMedica(sitMedica.getAfiliado().getInte() , sitMedica.getAfiliado().getCuil_titular() ));		
	sitMedica.setId(id);		
	return sitMedica ;
}
 
public List<PatologiasSituacionMedica> getPatologiasDelAfiliadoDeLaSituacionMedica(int inte , String cuilTitular ) {
	
	List<PatologiasSituacionMedica> patologiasDelAfiliadoSituacionMedica  = new ArrayList<PatologiasSituacionMedica>();
	Connection con = null;
	CallableStatement stmt = null;
	try {
		String sql = "{call buscar_patologias_de_situaciones_medicas_by_afiliado(?,?)}";
		con = ConnectionHelper.getConnection();
		stmt = con.prepareCall(sql.toString());	
		stmt.setString(1, cuilTitular );
		stmt.setInt(2, inte );
		ResultSet rs = stmt.executeQuery();
		while (rs.next()) {
			PatologiasSituacionMedica pla = PatologiasSituacionMedica.getMapping("sm_", rs);						
			patologiasDelAfiliadoSituacionMedica.add(pla);
		}
	} catch (Exception e) {
		_log.error("Error al buscar las patologias del afiliado de la situacion medica", e);
	} finally {
		ConnectionHelper.cerrar(stmt, con);
	}
	return patologiasDelAfiliadoSituacionMedica;
}




public boolean actualizar(SituacionMedica situacionMedica, User user ) throws SystemException {
	
	Connection con = null;
	CallableStatement stmt = null, stmt2 = null ;
	boolean resp=false ;
		
	try {
		
		String screenName = user.getScreenName();
		String sql1  = "{call update_situacion_medica(?,?,?,?,?)}";
		String sql2  = "{call update_detalle_patologia (?,?,?,?,?,?)}";
		String sql3  = "{call update_detalle_discapacidad (?,?,?,?,?,?,?,?,?)}";
		con = ConnectionHelper.getConnectionForTransaction();
		
// actualiza datos de la fecha seccional en la base se analiza si la fecha es distinta a null 			   
		stmt = con.prepareCall(sql1.toString());
		
		stmt.setInt(1 , situacionMedica.getIdSituacionMedica() );
		if (null != situacionMedica.getFechaVigen_Desde()) {
			stmt.setDate(2, new java.sql.Date(situacionMedica.getFechaVigen_Desde().getTime()));						
		} else {					
			stmt.setNull(2, Types.DATE);
		}
		
		if (null != situacionMedica.getFechaVigen_Hasta() ) {
			stmt.setDate(3, new java.sql.Date(situacionMedica.getFechaVigen_Hasta().getTime()));						
		} else {					
			stmt.setNull(3, Types.DATE);
		}
	    stmt.setString(4 , screenName  );
	    stmt.setString(5 , situacionMedica.getDetalleSituMedica()  );
		
		stmt.executeUpdate();
		
		stmt2 = con.prepareCall(sql1.toString());	
        if (situacionMedica.isDiscapacitado()){ // discapacitado 
    		stmt2 = con.prepareCall(sql3.toString());
    		stmt2.setInt(1 , situacionMedica.getIdSituacionMedica() );
    		stmt2.setString (2 , situacionMedica.getDetalleDiscapacidad().getDiagnostico()  );
    		stmt2.setBoolean(3 , situacionMedica.getDetalleDiscapacidad().isDependencia() );
    		stmt2.setString(4 , situacionMedica.getDetalleDiscapacidad().getTelefono_contacto() );
    		stmt2.setString(5 , screenName);
    		stmt2.setString(6 , situacionMedica.getDetalleDiscapacidad().getCie_diez() );
    		stmt2.setString(7 , situacionMedica.getDetalleDiscapacidad().getTiposDiscapacidadDelAfiliado() );
    		stmt2.setString(8 , situacionMedica.getAfiliado().getCuil_titular()   );
    		stmt2.setInt(9 , situacionMedica.getAfiliado().getInte() );
    		
        }else{  // no discapacitado 
    		stmt2 = con.prepareCall(sql2.toString());
    		stmt2.setInt(1 , situacionMedica.getIdSituacionMedica()  );
    		stmt2.setString(2 , situacionMedica.getdiagnostico()  );
    		stmt2.setString(3 , screenName   );
    		stmt2.setString(4 , situacionMedica.getCie10()  );
    		stmt2.setString(5 , situacionMedica.getAfiliado().getCuil_titular()   );
    		stmt2.setInt(6 , situacionMedica.getAfiliado().getInte() );
        }
        	
        stmt2.executeUpdate();
				
		con.commit();	 
		resp=true;
	} catch (SQLException e) {
		_log.error("Error al actualizar prestador y sus componentes", e);
		ConnectionHelper.rollback(con);		
		throw new SystemException(e);
	}  finally {
		ConnectionHelper.cerrar(stmt);
		ConnectionHelper.cerrar(stmt2, con);
	}
	return resp ;
}

public void borrar(int id, String screenName) throws SQLException,
ImposibleBorrarSituacionMedicaException {
Connection con = null;
CallableStatement stmt = null;
try {
String sql = "{call borra_situacion_medica(?, ?)}";
con = ConnectionHelper.getConnection();
stmt = con.prepareCall(sql.toString());
stmt.setInt(1, id);
stmt.setString(2, screenName);
ResultSet rs = stmt.executeQuery();
while (rs.next()) {
	if (rs.getInt(1) == 0) {
		throw new ImposibleBorrarSituacionMedicaException ();
	}
}
} catch (ImposibleBorrarSituacionMedicaException  e) {
_log.error("Error al borrar la situacion medica", e);
throw e;
} finally {
ConnectionHelper.cerrar(stmt, con);
}
}


}



