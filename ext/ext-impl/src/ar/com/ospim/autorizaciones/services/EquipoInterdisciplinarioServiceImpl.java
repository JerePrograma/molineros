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

import ar.com.ospim.autorizaciones.beans.EquipoInterdisciplinario;
import ar.com.ospim.autorizaciones.beans.FirmaAutorizante;
import ar.com.ospim.autorizaciones.beans.PrestacionesEquipoInterdisciplinario;
import ar.com.ospim.autorizaciones.exceptions.ImposibleBorrarEquipoInterdisiciplinarioException;
import ar.com.ospim.util.ConnectionHelper;


	
public class EquipoInterdisciplinarioServiceImpl {

	private static Log _log = LogFactoryUtil
			.getLog(EquipoInterdisciplinarioServiceImpl.class);

	
	public EquipoInterdisciplinario buscarDatosAfiliadoEquipoInterInter(
			 int inte, String cuilTitular ) throws SystemException,
			NumberFormatException, ParseException {
		
		EquipoInterdisciplinario equipo = null;

				
		Connection con = null;
		CallableStatement stmt = null;
		
		try {
			String sql="" ;
			sql = "{call autorizaciones.equipo_interdisciplinario_domicilio_afiliado_default (?,?)}";
			
			_log.debug("obteniendo conexion");
			con = ConnectionHelper.getConnection();
			
			stmt = con.prepareCall(sql.toString());
			stmt.setInt (1, inte);
			stmt.setString(2, cuilTitular );
			
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) { // Trae a lo sumo dos registros pero prioriza el tipo I antes del tipo P
				if (rs.getString("ei_calle")!="" && rs.getString("ei_provincia")!=null ){ 
					equipo = EquipoInterdisciplinario.getMappingDefaultDatos(rs, "ei_" , cuilTitular , inte);
				}				
			}
		} catch (Exception e) {
			_log.error(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}		
		return equipo ;
	}
	

	public List<EquipoInterdisciplinario> buscarEquipoInterRegistros(
			String estado , Date fecha,  int inte, String cuilTitular, int nroRegistro , String motivo ) throws SystemException,
			NumberFormatException, ParseException {

		Connection con = null;
		CallableStatement stmt = null;
		ArrayList<EquipoInterdisciplinario> listaEquipos = null;
		listaEquipos = new ArrayList<EquipoInterdisciplinario>();
		
		try {
			String sql="" ;
			sql = "{call autorizaciones.equipo_interdisciplinario_registros(?,?,?,?,?,?)}";
			
			_log.debug("obteniendo conexion");
			con = ConnectionHelper.getConnection();
			
			stmt = con.prepareCall(sql.toString());										
			
			if (nroRegistro == 0) {
				stmt.setNull(1, Types.INTEGER);
			} else {
				stmt.setInt(1, nroRegistro); 
			}
			
			stmt.setDate(2, fecha == null ? null : new java.sql.Date(fecha.getTime()));
			
			
			if (cuilTitular!="") {
				stmt.setString(3, cuilTitular);
			} else {				
				stmt.setNull(3, Types.VARCHAR );
			}
			
			if (cuilTitular!="" ) {
				stmt.setInt(4, inte);
			} else {
				stmt.setNull(4, Types.INTEGER);
			}
			
			if (estado ==""  ) {
				stmt.setNull(5, Types.CHAR);
				stmt.setNull(6, Types.VARCHAR );
			} else {
				stmt.setString(5, estado);
				if (estado.equals("CERRADO") && !motivo.equals("TODOS")){					
					stmt.setString(6, motivo );		
				}else{
					stmt.setNull(6, Types.VARCHAR );
				}				
			}			
			
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				EquipoInterdisciplinario equipoInter = EquipoInterdisciplinario.getMappingBuscador(rs, "ei_");
				listaEquipos.add(equipoInter );
			}
		} catch (Exception e) {
			_log.error(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return listaEquipos  ;
	}
	

	
public int insertar(EquipoInterdisciplinario equipoInterDisciplinario  , String screenName) throws Exception {
		
		Connection con = null;
		CallableStatement stmt = null, stmt1 = null, stmt2 = null, stmt3 = null , stmt4 = null , stmt5 = null  , stmt6 = null , stmt7 = null ;
		
		int idRegEquipoInter =0;
		int idRegIdTelefono =0;
		int idRegIdEmailAfiliado =0;
		
		String sql  = "{call autorizaciones.inserta_equipo_interdisciplinario(?,?,?,?,?,?,?,?,?,?)}";
		String sql1  = "{call autorizaciones.inserta_equipo_interdisciplinario_diagnostico  (?,?,?,?,?)}";
		String sql2  = "{call autorizaciones.inserta_equipo_interdisciplinario_telefonos (?,?,?,?)}";
		String sql3  = "{call autorizaciones.inserta_equipo_interdisciplinario_domicilio_afi (?,?,?,?,?,?,?,?,?,?,?)}";
		String sql4  = "{call autorizaciones.inserta_equipo_interdisciplinario_prestaciones (?,?,?,?,?,?,?)}";
		String sql5  = "{call autorizaciones.inserta_update_equipo_dictamen  (?,?,?,?,?)}";
		String sql6  = "{call autorizaciones.inserta_equipo_interdisciplinario_email(?,?)}";
		String sql7  = "{call autorizaciones.inserta_firma_eq_firma_autorizante(?,?,?,?)}";

		
		try {			
						
			con = ConnectionHelper.getConnection();
			con.setAutoCommit(false);
// **************************************************************			
//           inserta telefono  del afiliado
// **************************************************************			
			if (equipoInterDisciplinario.getTelefonoContacto().getNumero()!=""    ) {
				stmt2 = con.prepareCall(sql2.toString());
				stmt2.registerOutParameter(1, Types.INTEGER);
				stmt2.setString(1, screenName );
				stmt2.setString(2, equipoInterDisciplinario.getTelefonoContacto().getCodigoArea() );
				stmt2.setString(3, equipoInterDisciplinario.getTelefonoContacto().getTipo()  );
				stmt2.setString(4, equipoInterDisciplinario.getTelefonoContacto().getNumero() );
				idRegIdTelefono = stmt2.executeUpdate();
				if(stmt2.getInt(1) > 0){				
					idRegIdTelefono =stmt2.getInt(1);
				}
			}			
// **************************************************************			
//           inserta email del afiliado
// **************************************************************			
			if (   !equipoInterDisciplinario.getAfiliado().getEmail().equals("")  ) { // inserta nuevo mail ingresado  
				stmt6 = con.prepareCall(sql6.toString());
				stmt6.registerOutParameter(1, Types.INTEGER);
				stmt6.setString(1, screenName );
				stmt6.setString(2, equipoInterDisciplinario.getAfiliado().getEmail());
				idRegIdEmailAfiliado  = stmt6.executeUpdate();
				if(stmt6.getInt(1) > 0){				
					idRegIdEmailAfiliado  =stmt6.getInt(1);
				}
			}else{
				idRegIdEmailAfiliado=equipoInterDisciplinario.getIdEmail();
			}
// **************************************************************	
//            inserta la cabecera 
// **************************************************************
			stmt = con.prepareCall(sql.toString());
			stmt.registerOutParameter(1, Types.INTEGER);
			if (null != equipoInterDisciplinario.getFechaRegistro()) {
				stmt.setDate(1, new java.sql.Date(equipoInterDisciplinario.getFechaRegistro().getTime()));
			} else {
				stmt.setNull(1, Types.DATE);
			}					
			stmt.setString(2, equipoInterDisciplinario.getCuit_titular()  );
			stmt.setInt(3, equipoInterDisciplinario.getInte() );
			stmt.setString(4, screenName);
			stmt.setString(5, equipoInterDisciplinario.getObservaciones() );
			stmt.setString(6, equipoInterDisciplinario.getParticipantes() );
			stmt.setString(7, equipoInterDisciplinario.getEstadoRegEquipoInter());
			stmt.setInt(8, idRegIdTelefono  );
			stmt.setInt(9,  idRegIdEmailAfiliado     );
			/*
			if (equipoInterDisciplinario.getEstadoRegEquipoInter().equals("CARGADO")){
				stmt.setString(10, "");
			}else{
				stmt.setString(10, equipoInterDisciplinario.getMotivoCierreEquipoInter()  );
			}
			*/
			stmt.setString(10, equipoInterDisciplinario.getMotivoCierreEquipoInter()  );
			
			idRegEquipoInter = stmt.executeUpdate();
			if(stmt.getInt(1) > 0){				 
				idRegEquipoInter =stmt.getInt(1);
			}			

			stmt1 = con.prepareCall(sql1.toString());			
			stmt1.setString(1, equipoInterDisciplinario.getCuit_titular()  );
			stmt1.setInt(2, equipoInterDisciplinario.getInte() );
			stmt1.setString(3, equipoInterDisciplinario.getDiagnosticoAfiliado() );
			stmt1.setString(4, screenName   );
			stmt1.setString(5, equipoInterDisciplinario.getCodigoCie10());
			stmt1.executeUpdate();
// **************************************************************	
//          inserta el domicilio si cambio o es del tipo P 
//**************************************************************
			if (equipoInterDisciplinario.getTipoDomicilio()!="I"    ) {
				    stmt3 = con.prepareCall(sql3.toString());			
					stmt3.setString(1, equipoInterDisciplinario.getCuit_titular()  );
					stmt3.setInt(2, equipoInterDisciplinario.getInte() );
					stmt3.setString(3, equipoInterDisciplinario.getAfiliado().getDomicilioDefault().getCalle()  );
					stmt3.setString(4, equipoInterDisciplinario.getAfiliado().getDomicilioDefault().getNumero()   );
					stmt3.setString(5, equipoInterDisciplinario.getAfiliado().getDomicilioDefault().getDepto()    );
					stmt3.setString(6, equipoInterDisciplinario.getAfiliado().getDomicilioDefault().getBarrio()    );
					stmt3.setInt(7, equipoInterDisciplinario.getAfiliado().getDomicilioDefault().getLocalidad().getId()   );
					stmt3.setInt(8, equipoInterDisciplinario.getAfiliado().getDomicilioDefault().getProvincia().getId()   );
					stmt3.setString(9, equipoInterDisciplinario.getAfiliado().getDomicilioDefault().getPiso()   );					
					stmt3.setString(10, screenName   );					
					stmt3.setString(11, equipoInterDisciplinario.getAfiliado().getDomicilioDefault().getPostal_codi()  );
				    stmt3.executeUpdate();
			}
// **************************************************************			
//			*** INSERTA REGISTROS DE PRESTACIONES ***				
// **************************************************************			
			stmt4 = con.prepareCall(sql4.toString());				
			
			if(equipoInterDisciplinario.getPrestaciones()!=null && equipoInterDisciplinario.getPrestaciones().size() > 0){
				for (PrestacionesEquipoInterdisciplinario prestequipo : equipoInterDisciplinario.getPrestaciones() ) {
					stmt4 = con.prepareCall(sql4.toString());
					stmt4.setInt(1, idRegEquipoInter);
					stmt4.setInt(2, prestequipo.getId_prestacion()    );						
					stmt4.setDouble(3, prestequipo.getImporte() );  
					stmt4.setDouble(4, prestequipo.getTotal()   );  
					stmt4.setInt(5, prestequipo.getCantidad()   );
					stmt4.setString(6, screenName);
					if (prestequipo.gettipoPrestacion()==0 ) {
						stmt4.setNull(7, Types.INTEGER );
					} else {
						stmt4.setInt(7, prestequipo.gettipoPrestacion()); 
					}
					stmt4.executeUpdate();
				}
			}
//**************************************************************	
//	      graba dictamenes modificados
//**************************************************************
			for (EquipoInterdisciplinario.DICTAMENES dictamen: EquipoInterdisciplinario.DICTAMENES.values()) {
				  if (!equipoInterDisciplinario.getDictamen(dictamen).equals("")    ) { 
					    stmt5 = con.prepareCall(sql5.toString());
					    stmt5.setInt(1, idRegEquipoInter );						
					    stmt5.setInt(2, dictamen.ordinal() );
					    stmt5.setString(3, equipoInterDisciplinario.getDictamen(dictamen));
					    stmt5.setString(4, screenName);
					    stmt5.setBoolean(5, false );
					    stmt5.executeUpdate();
				  }
				}
			
//**************************************************************	
//		      graba firmas
//**************************************************************
			if (equipoInterDisciplinario.getFirmaAutorizante() != null && 
					!equipoInterDisciplinario.getFirmaAutorizante().isEmpty()) {
				for (FirmaAutorizante firma : equipoInterDisciplinario.getFirmaAutorizante() ) {
				    stmt7 = con.prepareCall(sql7.toString());
				    stmt7.setInt(1,idRegEquipoInter);
				    stmt7.setInt(2,firma.getTipoDictamen());
				    stmt7.setDouble(3, firma.getIdUsuario());
				    stmt7.setString(4, firma.getAltaUsr());
				    stmt7.executeUpdate();
				}
				
			}
			
			con.commit();	
			
		} catch (SQLException e) {
			_log.error("Error al insertar equipo interdisicplinario y sus componentes", e);
			try {
				con.rollback();
			} catch (SQLException e1) {
				throw new SystemException(e1);
			}
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt);
			ConnectionHelper.cerrar(stmt1);
			ConnectionHelper.cerrar(stmt2);
			ConnectionHelper.cerrar(stmt3);
			ConnectionHelper.cerrar(stmt4);
			ConnectionHelper.cerrar(stmt5);			
			ConnectionHelper.cerrar(stmt6);
			ConnectionHelper.cerrar(stmt7, con);

		}
		return idRegEquipoInter ;
	}

/*
public List<ContactoCRM> getContactosDelReclamo(String cuilTitular, int inte) throws SystemException {	
	return CrmServiceUtil.buscarUltimosContactosCRMconDataReclamo(cuilTitular, inte );	
}

public List<ContactoCRM> getContactosDelReclamoxIdReclamo(String cuilTitular, int inte, int idReclamo) throws SystemException {	
	return CrmServiceUtil.buscarUltimosContactosCRMxidReclamo(cuilTitular, inte ,idReclamo);	
}
*/
	
public EquipoInterdisciplinario    getEquipoInterdisciplinario (int id) throws SystemException {
	Connection con = null;
	CallableStatement stmt = null;
	EquipoInterdisciplinario equipo = null;
	
	
	try {
		String sql = "{call autorizaciones.equipo_interdiscipinario_by_id (?)}";
		con = ConnectionHelper.getConnection();
		stmt = con.prepareCall(sql.toString());
		stmt.setInt(1, id);
		ResultSet rs = stmt.executeQuery();
		while (rs.next()) {
			equipo = EquipoInterdisciplinario.getMapping(rs, "ei_");						  }
	} catch (Exception e) {
		_log.error("Error al buscar reclamo prestacional", e);
	} finally {
		ConnectionHelper.cerrar(stmt, con);
	}
	// asigna los prestaiones del equipo interdisciplinario	
	equipo.setPrestaciones(this.getPrestacionesDelEquipoInterdisciplinario(id));
	// asigna los dictamenes del equipo interdisciplinario 
	equipo.setDictamenes(this.getDictamenesDelEquipoInterdisciplinario(id));
	equipo.setId(id);	
	// asigna las firmas de los autorizantes
	equipo.setFirmaAutorizante(firmasPorDictamen(id));
	
	
	
	return equipo ;	
}


public String [] getDictamenesDelEquipoInterdisciplinario(int idEquipoInterDisciplinario ) {
	
	String dictamenes[] = new String[6]; // soporta 6 tipos de dictamenes
	Connection con = null;
	CallableStatement stmt = null;
	try {
		String sql = "{call autorizaciones.equipo_interdisciplinario_dictamenes_by_id(?)}";
		con = ConnectionHelper.getConnection();
		stmt = con.prepareCall(sql.toString());	
		stmt.setInt(1, idEquipoInterDisciplinario );
		ResultSet rs = stmt.executeQuery();
		for(int i=1;i<6;i++){
			dictamenes[i]="";
		}
		while (rs.next()) {
			dictamenes[rs.getInt("dic_tipo_dictamen")]=rs.getString("dic_dictamen");
		}
	} catch (Exception e) {
		_log.error("Error al buscar los dictamenes del equipo interdisciplinario", e);
	} finally {
		ConnectionHelper.cerrar(stmt, con);
	}
	return dictamenes;
}



public List<PrestacionesEquipoInterdisciplinario> getPrestacionesDelEquipoInterdisciplinario(int idEquipoInterDisciplinario ) {
	
	List<PrestacionesEquipoInterdisciplinario> prestacionesDelEquipoInterDisciplinario = new ArrayList<PrestacionesEquipoInterdisciplinario>();
	Connection con = null;
	CallableStatement stmt = null;
	try {
		String sql = "{call autorizaciones.equipo_interdisciplinario_prestaciones_by_Id (?)}";
		con = ConnectionHelper.getConnection();
		stmt = con.prepareCall(sql.toString());	
		stmt.setInt(1, idEquipoInterDisciplinario );
		ResultSet rs = stmt.executeQuery();
		while (rs.next()) {
			PrestacionesEquipoInterdisciplinario pla = PrestacionesEquipoInterdisciplinario.getMapping("pr_", rs);						
			prestacionesDelEquipoInterDisciplinario.add(pla);
		}
	} catch (Exception e) {
		_log.error("Error al buscar prestaciones del equipo interdisciplinario", e);
	} finally {
		ConnectionHelper.cerrar(stmt, con);
	}
	return prestacionesDelEquipoInterDisciplinario;
}

public int actualizar(EquipoInterdisciplinario equipoInterDisciplinario , String screenName) throws Exception {
	
	Connection con = null;
	CallableStatement stmt = null, stmt1 = null, stmt2 = null , stmt3 = null , stmt4 = null , stmt5 = null , stmt6 = null , stmt7 = null;
		
	con = ConnectionHelper.getConnection();
	con.setAutoCommit(false);
	int idRegEquiInter = equipoInterDisciplinario.getId_registroEquipoInter()  ;
	int idRegIdTelefono =0;	
	try {
		String sql1 = "{call autorizaciones.update_datos_equipointerdisciplinario(?,?,?,?,?,?,?,?)}";		
		String sql11  = "{call autorizaciones.inserta_equipo_interdisciplinario_prestaciones(?,?,?,?,?,?,?)}";		
        String sql111  = "{call autorizaciones.borrar_prestacion_equipointerdisciplinario(?,?)}"; 

		
		String sql2  = "{call autorizaciones.update_datos_equipointerdisciplinario_diagnostico(?,?,?,?,?)}"; 
		String sql3  = "{call autorizaciones.update_datos_equipointerdisciplinario_telefonos (?,?,?,?,?)}"; 
		String sql4  = "{call autorizaciones.inserta_equipo_interdisciplinario_domicilio_afi (?,?,?,?,?,?,?,?,?,?,?)}";
		String sql5  = "{call autorizaciones.inserta_update_equipo_dictamen   (?,?,?,?,?)}";
		String sql6  = "{call autorizaciones.update_datos_equipointerdisciplinario_email(?,?,?)}";
		String sql61  = "{call autorizaciones.inserta_update_equipo_interdisciplinario_email(?,?,?)}";
		String sql7  = "{call autorizaciones.inserta_firma_eq_firma_autorizante(?,?,?,?)}";

		 
		// actualiza los telefonos
		idRegIdTelefono =0;
		if (equipoInterDisciplinario.isCambiocambioTelefono() ) 
		{
			stmt2 = con.prepareCall(sql3.toString());
			stmt2.registerOutParameter(1, Types.INTEGER);
			stmt2.setInt(1 , equipoInterDisciplinario.getTelefonoContacto().getId() );
			stmt2.setString(2 , screenName);
		    stmt2.setString(3 , equipoInterDisciplinario.getTelefonoContacto().getCodigoArea());
		    stmt2.setString(4 , equipoInterDisciplinario.getTelefonoContacto().getTipo()  );
		    stmt2.setString(5 , equipoInterDisciplinario.getTelefonoContacto().getNumero()  );
		    idRegIdTelefono = stmt2.executeUpdate();
			if(stmt2.getInt(1) > 0){				
				idRegIdTelefono =stmt2.getInt(1);
									}
		}
		
	    stmt = con.prepareCall(sql1.toString()); 
	    stmt.setInt(1, idRegEquiInter);
	    stmt.setString(2 , equipoInterDisciplinario.getEstadoRegEquipoInter() );
	    stmt.setString(3 , equipoInterDisciplinario.getParticipantes()  );
	    stmt.setString(4 , equipoInterDisciplinario.getObservaciones()  );
	    stmt.setString(5 , screenName);	    
	    if (null != equipoInterDisciplinario.getFechaRegistro()) {
			stmt.setDate(6, new java.sql.Date(equipoInterDisciplinario.getFechaRegistro().getTime()));						
		} else {					
			stmt.setNull(6, Types.DATE);
		}
	    stmt.setInt(7 , idRegIdTelefono );	    
	    stmt.setString(8 , equipoInterDisciplinario.getMotivoCierreEquipoInter() );
		stmt.executeUpdate();
		
		// actualiza el diagnostico si cambio pisa el existente 
		if (equipoInterDisciplinario.isCambioDiagnosticoCie10() ) {
		stmt1 = con.prepareCall(sql2.toString()); 
	    stmt1.setInt(1, equipoInterDisciplinario.getInte());	    
	    stmt1.setString(2 , equipoInterDisciplinario.getCuit_titular());
	    stmt1.setString(3 , equipoInterDisciplinario.getDiagnosticoAfiliado()  );
	    stmt1.setString(4 , equipoInterDisciplinario.getCodigoCie10()  );
	    stmt1.setString(5 , screenName);
	    stmt1.executeUpdate();
		}
		// actualiza el mail Si cambio
		if (equipoInterDisciplinario.isCambioEmailAfiliado()  ) {
			if ( equipoInterDisciplinario.getIdEmail() == 0) { // no existe registro en tabla contacto_e  
				stmt6 = con.prepareCall(sql61.toString());		
				stmt6.setString(1 , screenName);	    	    
			    stmt6.setString(2 , equipoInterDisciplinario.getAfiliado().getEmail() );
			    stmt6.setInt(3 , equipoInterDisciplinario.getId_registroEquipoInter()  );			    
			}else{
				stmt6 = con.prepareCall(sql6.toString());
				stmt6.setString(1 , screenName);	    	    
			    stmt6.setString(2 , equipoInterDisciplinario.getAfiliado().getEmail() );	    
			    stmt6.setInt(3 , equipoInterDisciplinario.getIdEmail()   );
			}   
			stmt6.executeUpdate();	        
		}		
// **************************************************************	
//      inserta el domicilio si cambio o es del tipo P 
//**************************************************************
		if (equipoInterDisciplinario.isCambioDomicilioAfiliadoRegEquipo()    ) {
			    stmt3 = con.prepareCall(sql4.toString());			
				stmt3.setString(1, equipoInterDisciplinario.getCuit_titular()  );
				stmt3.setInt(2, equipoInterDisciplinario.getInte() );
				stmt3.setString(3, equipoInterDisciplinario.getAfiliado().getDomicilioDefault().getCalle()  );
				stmt3.setString(4, equipoInterDisciplinario.getAfiliado().getDomicilioDefault().getNumero()   );
				stmt3.setString(5, equipoInterDisciplinario.getAfiliado().getDomicilioDefault().getDepto()    );
				stmt3.setString(6, equipoInterDisciplinario.getAfiliado().getDomicilioDefault().getBarrio()    );
				stmt3.setInt(7, equipoInterDisciplinario.getAfiliado().getDomicilioDefault().getLocalidad().getId()   );
				stmt3.setInt(8, equipoInterDisciplinario.getAfiliado().getDomicilioDefault().getProvincia().getId()   );
				stmt3.setString(9, equipoInterDisciplinario.getAfiliado().getDomicilioDefault().getPiso()   );
				stmt3.setString(10, screenName   );
				stmt3.setString(11, equipoInterDisciplinario.getAfiliado().getDomicilioDefault().getPostal_codi()   );
			    stmt3.executeUpdate();
		}
//		*** GRABAR REGISTRO DE PRESTACIONES ***				
		if(equipoInterDisciplinario.getPrestaciones()!=null && equipoInterDisciplinario.getPrestaciones().size() > 0){
			for (PrestacionesEquipoInterdisciplinario  pres : equipoInterDisciplinario.getPrestaciones()) {
				if(pres.getEstado() != null){ 
					if(pres.getEstado().equals(PrestacionesEquipoInterdisciplinario.ESTADOS.BAJA))
					{ 
						stmt4 = con.prepareCall(sql111.toString());
						stmt4.setInt(1, pres.getId_prestacionrecord()   );						
						stmt4.setString(2, screenName);						
					}
					else
					{ 
						if(pres.getEstado().equals(PrestacionesEquipoInterdisciplinario.ESTADOS.NUEVO))
							{
								stmt4 = con.prepareCall(sql11.toString());
								stmt4.setInt(1, idRegEquiInter);
								stmt4.setInt(2, pres.getId_prestacion()    );								
								stmt4.setDouble(3, pres.getImporte() );  
								stmt4.setDouble(4, pres.getTotal()   );
								stmt4.setInt(5, pres.getCantidad() );								
								stmt4.setString(6, screenName);
								if (pres.gettipoPrestacion()==0  ) {
									stmt4.setNull(7, Types.INTEGER);
								} else {
									stmt4.setInt(7, pres.gettipoPrestacion()); 
								}
							}
					}
					stmt4.executeUpdate();
				}
			}
		}
// **************************************************************	
//      graba dictamenes modificados
//**************************************************************
		for (EquipoInterdisciplinario.DICTAMENES dictamen: EquipoInterdisciplinario.DICTAMENES.values()) {
			  if (equipoInterDisciplinario.isCambioElDictamen(dictamen) ) {				  
				    stmt5 = con.prepareCall(sql5.toString());
				    stmt5.setInt(1, equipoInterDisciplinario.getId_registroEquipoInter() );						
				    stmt5.setInt(2, dictamen.ordinal() );
				    stmt5.setString(3, equipoInterDisciplinario.getDictamen(dictamen));
				    stmt5.setString(4, screenName);
				    stmt5.setBoolean(5, false);
				    stmt5.executeUpdate();
			  }
			}
		
//**************************************************************	
//	      graba firmas
//**************************************************************
		if (equipoInterDisciplinario.getFirmaAutorizante() != null && 
				!equipoInterDisciplinario.getFirmaAutorizante().isEmpty()) {
			for (FirmaAutorizante firma : equipoInterDisciplinario.getFirmaAutorizante() ) {
			    stmt7 = con.prepareCall(sql7.toString());
			    stmt7.setInt(1,equipoInterDisciplinario.getId_registroEquipoInter() );
			    stmt7.setInt(2,firma.getTipoDictamen());
			    stmt7.setDouble(3, firma.getIdUsuario());
			    stmt7.setString(4, firma.getAltaUsr());
			    stmt7.executeUpdate();
			}
			
		}
				
		con.commit();
		
	} catch (SQLException e) {
		_log.error("Error al actualizar equipo insterdisciplinario y sus componentes", e);
		try {
			con.rollback();
		} catch (SQLException e1) {
			throw new SystemException(e1);
		}
		throw new SystemException(e);
	} finally {
		ConnectionHelper.cerrar(stmt);
		ConnectionHelper.cerrar(stmt1);
		ConnectionHelper.cerrar(stmt2);
		ConnectionHelper.cerrar(stmt3);
		ConnectionHelper.cerrar(stmt4);
		ConnectionHelper.cerrar(stmt5);
		ConnectionHelper.cerrar(stmt6, con );
	}
	return idRegEquiInter ;
}


	public void borrar(int id, String screenName) throws SQLException,
			ImposibleBorrarEquipoInterdisiciplinarioException {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call autorizaciones.borra_equipo_interdisciplinario(?, ?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, id);
			stmt.setString(2, screenName);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				if (rs.getInt(1) == 0) {
					throw new ImposibleBorrarEquipoInterdisiciplinarioException();
				}
			}
		} catch (ImposibleBorrarEquipoInterdisiciplinarioException e) {
			_log.error("Error al borrar el equipo interdisciplinario", e);
			throw e;
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
	}

	

	public int  getEsFirmante (User user) throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		
		int tipoDictamen = 0;
		
		try {
			String sql = "{call autorizaciones.eq_traer_firmante(?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, user.getScreenName());
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				tipoDictamen = rs.getInt(1);				  
			}
		} catch (Exception e) {
			_log.error("Error al traer firmante", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		
		return tipoDictamen ;	
	}
	
	
	public  List<FirmaAutorizante> firmasPorDictamen(int idEquipoInterDisciplinario ) {
		
		List<FirmaAutorizante> firmasAutorizante = new ArrayList<FirmaAutorizante>();
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call autorizaciones.equipo_interdisciplinario_firmas_by_id (?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());	
			stmt.setInt(1, idEquipoInterDisciplinario );
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				FirmaAutorizante firmaAut = FirmaAutorizante.getMapping(rs);						
				firmasAutorizante.add(firmaAut);
			}
		} catch (Exception e) {
			_log.error("Error al buscar firmas por dictamen", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return firmasAutorizante;
	}
	
	

}



