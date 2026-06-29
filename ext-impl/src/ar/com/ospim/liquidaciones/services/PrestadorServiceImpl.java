package ar.com.ospim.liquidaciones.services;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import ar.com.ospim.global.beans.ContactoElectronico;
import ar.com.ospim.global.beans.Telefono;
import ar.com.ospim.liquidaciones.ImposibleBorrarPrestadorException;
import ar.com.ospim.prestadores.exception.DuplicatePrestadorIdException;
import ar.com.ospim.liquidaciones.beans.EspecialidadPrestador;
import ar.com.ospim.liquidaciones.beans.MatriculaPrestador;
import ar.com.ospim.liquidaciones.beans.Prestador;
import ar.com.ospim.liquidaciones.beans.Prestador.TipoPrestador;
import ar.com.ospim.liquidaciones.beans.ContactoElectronicoPrestador;
import ar.com.ospim.liquidaciones.beans.PrestadorLugarAtencion;
import ar.com.ospim.liquidaciones.beans.PrestadorPlan;
import ar.com.ospim.liquidaciones.beans.ProfesionPrestador;
import ar.com.ospim.liquidaciones.beans.SubEspecialidadPrestador;
import ar.com.ospim.liquidaciones.beans.TelefonoPrestador;
import ar.com.ospim.util.ConnectionHelper;
import ar.com.ospim.util.StringUtils;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

public class PrestadorServiceImpl {
	private static Log _log = LogFactoryUtil.getLog(PrestadorServiceImpl.class);

	private static PrestadorServiceImpl instance = null;

	public static PrestadorServiceImpl getInstance() {
		if (null == instance) {
			instance = new PrestadorServiceImpl();
		}
		return instance;
	}

//	public List<Prestador> getPrestadores(int id, String cuit,
//			String descripcion) {
//		Connection con = null;
//		CallableStatement stmt = null;
//		List<Prestador> listaPrestadores = null;
//		try {
//			String sql = "{call buscar_prestadores(?,?,?)}";
//			con = ConnectionHelper.getConnection();
//			stmt = con.prepareCall(sql.toString());
//			if (cuit != null && cuit.trim().equals("")) {
//				cuit = null;
//			}
//			stmt.setString(1, cuit);
//			stmt.setString(2, descripcion);
//			stmt.setInt(3, id);
//
//			ResultSet rs = stmt.executeQuery();
//			listaPrestadores = new ArrayList<Prestador>();
//			while (rs.next()) {
//				Prestador emp = Prestador.getMapping(rs,"");
//				listaPrestadores.add(emp);
//			}
//		} catch (Exception e) {
//			_log.error("Error al buscar prestadores", e);
//		} finally {
//			ConnectionHelper.cerrar(stmt, con);
//		}
//		return listaPrestadores;
//	}

	public List<Prestador> getPrestadores(int id, String cuit,
			String descripcion, boolean soloVigentes) {
		Connection con = null;
		CallableStatement stmt = null;
		List<Prestador> listaPrestadores = null;
		try {
			String sql = "{call buscar_prestadores(?,?,?,?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			if (cuit != null && cuit.trim().equals("")) {
				cuit = null;
			}
			stmt.setString(1, cuit);
			stmt.setString(2, descripcion);
			stmt.setInt(3, id);
			stmt.setBoolean(4, soloVigentes);

			ResultSet rs = stmt.executeQuery();
			listaPrestadores = new ArrayList<Prestador>();
			while (rs.next()) {
				Prestador emp = Prestador.getMappingSimple(rs,"");
				emp.setConvenioDirecto(rs.getBoolean("convenio_directo"));
				listaPrestadores.add(emp);
			}
		} catch (Exception e) {
			_log.error("Error al buscar prestadores vigentes", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return listaPrestadores;
	}
	
	public List<Prestador> getPrestadores(int id, String cuit,
			String descripcion, boolean soloVigentes,boolean soloHospitales) {
		Connection con = null;
		CallableStatement stmt = null;
		List<Prestador> listaPrestadores = null;
		try {
			String sql = "{call buscar_prestadores(?,?,?,?,?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			if (cuit != null && cuit.trim().equals("")) {
				cuit = null;
			}
			stmt.setString(1, cuit);
			stmt.setString(2, descripcion);
			stmt.setInt(3, id);
			stmt.setBoolean(4, soloVigentes);
			stmt.setBoolean(5, soloHospitales);

			ResultSet rs = stmt.executeQuery();
			listaPrestadores = new ArrayList<Prestador>();
			while (rs.next()) {
				Prestador emp = Prestador.getMappingSimple(rs,"");
				listaPrestadores.add(emp);
			}
		} catch (Exception e) {
			_log.error("Error al buscar prestadores vigentes", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return listaPrestadores;
	}


	public List<Prestador> getPrestadores(int id, String cuit,
			String descripcion, int provincia, int localidad,boolean soloVigentes,
			int profesion, int especialidad, int subEspecialidad, int tipoPrestador) {
		Connection con = null;
		CallableStatement stmt = null;
		List<Prestador> listaPrestadores = null;
		Prestador pr = null;
		
		try {
			String sql = "{call buscar_prestadores(?,?,?,?,?,?,?,?,?,?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			if (cuit != null && cuit.trim().equals("")) {
				cuit = null;
			}
			stmt.setString(1, cuit);
			stmt.setString(2, descripcion);
			stmt.setInt(3, id);
			stmt.setInt(4, provincia);
			stmt.setInt(5, localidad);
			stmt.setBoolean(6, soloVigentes);
			stmt.setInt(7, profesion);
			stmt.setInt(8, especialidad);
			stmt.setInt(9, subEspecialidad);
			stmt.setInt(10, tipoPrestador);

			ResultSet rs = stmt.executeQuery();
			listaPrestadores = new ArrayList<Prestador>();
			while (rs.next()) {
				pr = Prestador.getMapping(rs, "prs__");
				pr.setCbu(rs.getString("prs__cbu"));
				listaPrestadores.add(pr);
			}
		} catch (Exception e) {
			_log.error("Error en busqueda prestadores", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return listaPrestadores;
	}

	public List<Prestador> getPrestadores(
			int id,
			String cuit,
			String descripcion,
			int provincia,
			int localidad,
			boolean soloVigentes,
			int profesion,
			int especialidad,
			int subEspecialidad,
			int tipoPrestador,
			String hospital,
			boolean soloHabilitadosCotizar) {

		Connection con = null;
		CallableStatement stmt = null;
		List<Prestador> listaPrestadores = null;

		try {
			String sql =
					"{call buscar_prestadores(?,?,?,?,?,?,?,?,?,?,?,?)}";

			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql);

			if (cuit != null && cuit.trim().equals("")) {
				cuit = null;
			}

			stmt.setString(1, cuit);
			stmt.setString(2, descripcion);
			stmt.setInt(3, id);
			stmt.setInt(4, provincia);
			stmt.setInt(5, localidad);
			stmt.setBoolean(6, soloVigentes);
			stmt.setInt(7, profesion);
			stmt.setInt(8, especialidad);
			stmt.setInt(9, subEspecialidad);
			stmt.setInt(10, tipoPrestador);
			stmt.setString(11, hospital);
			stmt.setBoolean(12, soloHabilitadosCotizar);

			ResultSet rs = stmt.executeQuery();

			listaPrestadores = new ArrayList<Prestador>();

			while (rs.next()) {
				Prestador prestador =
						Prestador.getMapping(rs, "prs__");

				prestador.setCbu(
						rs.getString("prs__cbu")
				);

				listaPrestadores.add(prestador);
			}

		} catch (Exception e) {
			_log.error(
					"Error en busqueda prestadores",
					e
			);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}

		return listaPrestadores;
	}

	public Prestador getPrestador(int id) {
		Connection con = null;
		CallableStatement stmt = null;
		Prestador pr = null;
		int idDomi = 0;
		int idPrest = 0;
		
		try {
			String sql = "{call buscar_prestador_by_id(?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, id);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				pr = Prestador.getMapping(rs, "prs__");
				pr.setCbu(rs.getString("prs__cbu"));
			}
		} catch (Exception e) {
			_log.error("Error al buscar prestador", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		List<PrestadorLugarAtencion> lugaresAt = new ArrayList<PrestadorLugarAtencion>();
		
		lugaresAt = this.getLugaresAtencionDelPrestador(id);
		
		for (Iterator<PrestadorLugarAtencion> iterator = lugaresAt.iterator(); iterator.hasNext();) {
			PrestadorLugarAtencion pla = iterator.next();
			idDomi = pla.getId_domicilio();
			idPrest = pla.getId_prestador();
			pla.setTelefonos(this.getTelefonos(idPrest, idDomi));
			pla.setContactosElectronicos(this.getContactosElectronicos(idPrest, idDomi));
		}
		pr.setLugaresAtencion(lugaresAt);
		pr.setMatriculas(this.getMatriculas(id));
		pr.setProfesiones(this.getProfesionesEspecialidadesySubEspecialidades(id));
	
		List<PrestadorPlan> planesAt = new ArrayList<PrestadorPlan>();

		planesAt = this.getPlanesDelPrestador(id);
		pr.setPlanes(planesAt);
		
		return pr;
	}

	/**
	 * Devuelve los telefonos de un prestador x domicilio del lugar de atencion
	 * 
	 * @param id
	 * @return List<TelefonoPrestador>
	 */
	public List<TelefonoPrestador> getTelefonos(int id_prestador, int id_domicilio) {
		List<TelefonoPrestador> tels = new ArrayList<TelefonoPrestador>();
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call buscar_telefonos_prestador(?, ?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, id_prestador);
			stmt.setInt(2, id_domicilio);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				TelefonoPrestador tel = TelefonoPrestador.getMapping(rs);
				tels.add(tel);
			}
		} catch (Exception e) {
			_log.error("Error al buscar telefonos", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return tels;
	}

	/**
	 * Devuelve los medios de contacto electronicos del lugar de atencion del prestador
	 * 
	 * @param id
	 * @return List<ContactoElectronico>
	 */
	public List<ContactoElectronicoPrestador> getContactosElectronicos(int id_prestador, int id_domicilio) {
		List<ContactoElectronicoPrestador> contactos = new ArrayList<ContactoElectronicoPrestador>();
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call buscar_contactos_prestador(?, ?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, id_prestador);
			stmt.setInt(2, id_domicilio);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				ContactoElectronicoPrestador contacto = ContactoElectronicoPrestador.getMapping(rs);
				contactos.add(contacto);
			}
		} catch (Exception e) {
			_log.error("Error al buscar contactos", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return contactos;
	}

	public void borrar(int id, String screenName) throws SQLException,
			ImposibleBorrarPrestadorException {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call borra_prestador(?, ?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, id);
			stmt.setString(2, screenName);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				if (rs.getInt(1) == 0) {
					throw new ImposibleBorrarPrestadorException();
				}
			}
		} catch (ImposibleBorrarPrestadorException e) {
			_log.error("Error al buscar contactos", e);
			throw e;
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
	}

	public List<TipoPrestador> getTipos() {
		Connection con = null;
		CallableStatement stmt = null;
		List<TipoPrestador> listaTipos = null;
		try {
			String sql = "{call trae_tipos_prestadores()}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());

			ResultSet rs = stmt.executeQuery();
			listaTipos = new ArrayList<TipoPrestador>();
			while (rs.next()) {
				TipoPrestador emp = TipoPrestador.getMapping(rs);
				listaTipos.add(emp);
			}
		} catch (Exception e) {
			_log.error("Error al buscar tipos prestadores", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return listaTipos;
	}
	
	// / traigo matriculas del prestador //
	public List<MatriculaPrestador> getMatriculas(int id) {
		Connection con = null;
		CallableStatement stmt = null;
		List<MatriculaPrestador> matriculas = null;
		try {
			String sql = "{call autorizaciones.trae_matriculas_prestador(?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, id);

			ResultSet rs = stmt.executeQuery();
			matriculas = new ArrayList<MatriculaPrestador>();
			while (rs.next()) {
				MatriculaPrestador pos = MatriculaPrestador.getMapping("pmat_",rs);
				matriculas.add(pos);
			}
		} catch (Exception e) {
			_log.error("Error al buscar matriculas del prestador", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return matriculas;
	}

	public int insertar(Prestador prestador, String screenName) throws SystemException, DuplicatePrestadorIdException {
		
		Connection con = null;
		CallableStatement stmt = null, stmt2 = null, stmt3 = null, stmt4 = null, stmt5 = null, stmt6 = null, stmt7 = null;
		
		int idPrestador=0, idDomicilio=0;
		
		try {
			
			String sql  = "{call autorizaciones.insertar_prestador(?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
			String sql2 = "{call autorizaciones.insertar_matricula_prestador(?,?,?,?,?,?,?)}";
			String sql3 = "{call autorizaciones.insertar_prof_espec_subesp_prestador(?,?,?,?,?,?,?,?)}";
			String sql4 = "{call autorizaciones.insertar_lugar_atencion_prestador(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";
			String sql5 = "{call autorizaciones.insertar_telefonos_prestador(?, ?, ?, ?, ?, ?, ?, ?, ?)}";
			String sql6 = "{call autorizaciones.insertar_contactose_prestador(?, ? ,? ,? ,? ,?)}";
			String sql7 = "{call autorizaciones.insertar_plan_prestador(?, ? ,? ,? ,?)}";
					
			con = ConnectionHelper.getConnection();
			con.setAutoCommit(false);

//			*** PRESTADOR ***
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, prestador.getCuit());
			stmt.setInt(2, prestador.getTipo().getId()); // tipoPrestador
			stmt.setString(3, prestador.getContacto());
			stmt.setString(4, prestador.getObservaciones());
			stmt.setString(5, prestador.getDescripcion());
			stmt.setString(6, prestador.getCodigoHospital());
			stmt.setBoolean(7, prestador.getCertificacionProfesional());
			if (null != prestador.getFechaVtoCertificacion()) {
				stmt.setDate(8, new java.sql.Date(prestador.getFechaVtoCertificacion().getTime()));
			} else {
				stmt.setNull(8, Types.DATE);
			}	
			stmt.setBoolean(9, prestador.getSeguroCobertura());
			stmt.setString(10, prestador.getCiaSeguro());
			if (null != prestador.getFechaVtoSeguro()) {
				stmt.setDate(11, new java.sql.Date(prestador.getFechaVtoSeguro().getTime()));
			} else {
				stmt.setNull(11, Types.DATE);
			}
			stmt.setString(12, prestador.getOtorgaCertificacion());
			stmt.setString(13, screenName);
			if (StringUtils.checkNotEmpty(prestador.getCbu())) {
				stmt.setString(14,prestador.getCbu());
			} else {
				stmt.setNull(14, Types.VARCHAR);
			}
			

			//me devuelve el id del prestador.
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				idPrestador=rs.getInt(1);
			}	
//			*** MATRICULAS ***	
			if(prestador.getMatriculas()!=null && prestador.getMatriculas().size() > 0){
				for (MatriculaPrestador mat : prestador.getMatriculas()) {
					stmt2 = con.prepareCall(sql2.toString());
					stmt2.setInt(1, idPrestador);
					stmt2.setInt(2, mat.getNumero());
					if(mat.getProvincia()!=null){
						stmt2.setInt(3, mat.getProvincia().getId());
					}else{
						stmt2.setNull(3, Types.INTEGER);
					}
					stmt2.setString(4, mat.getTipo());
					stmt2.setString(5, screenName);		
					stmt2.setBoolean(6, mat.isPresentaCopia());		
					if (mat.getFechaVto() != null) {
						stmt2.setDate(7, new java.sql.Date(mat.getFechaVto().getTime()));
					} else {
						stmt2.setNull(7, Types.DATE);
					}
					stmt2.executeUpdate();
				}
			}	
//			*** PROFESION, ESPECIALIDAD y SUBESPECIALIDAD ***
			if(prestador.getProfesiones()!=null && prestador.getProfesiones().size() > 0){
				for (Iterator<ProfesionPrestador> iterator = prestador.getProfesiones().iterator(); iterator.hasNext();) {
					ProfesionPrestador p =  iterator.next();
					
					for (Iterator<EspecialidadPrestador> iterator2 = p.getEspecialidades().iterator(); iterator2.hasNext();) {
						EspecialidadPrestador e = iterator2.next();
	
						if(e.getSubEspecialidades() != null && e.getSubEspecialidades().size()>0){
							for (Iterator<SubEspecialidadPrestador> iterator3 = e.getSubEspecialidades().iterator(); iterator3.hasNext();) {
								SubEspecialidadPrestador s =  iterator3.next();
								
								stmt3 = con.prepareCall(sql3.toString());
								stmt3.setInt(1, idPrestador);
								stmt3.setInt(2, p.getIdProfesion());
								stmt3.setInt(3, e.getIdEspecialidad());
								stmt3.setInt(4, s.getId()); //con id de subespecialidad
								stmt3.setString(5, screenName);	
								stmt3.setString(6, p.getCategoriaProfOspim());	
								stmt3.setBoolean(7, p.isTituloProfesional());
								stmt3.setBoolean(8, e.isTituloEspecialidad());
								
								stmt3.executeUpdate();
							}
						}else{
							stmt3 = con.prepareCall(sql3.toString());
							stmt3.setInt(1, idPrestador);
							stmt3.setInt(2, p.getIdProfesion());
							stmt3.setInt(3, e.getIdEspecialidad());
							stmt3.setNull(4, Types.INTEGER); // sin subEspecialidad
							stmt3.setString(5, screenName);	
							stmt3.setString(6, p.getCategoriaProfOspim());	
							stmt3.setBoolean(7, p.isTituloProfesional());
							stmt3.setBoolean(8, e.isTituloEspecialidad());
							
							stmt3.executeUpdate();
						}
					}
				}
			}
//			*** Lugar Atencion, La Direccion, Los telefonos y Contactos electrónicos del mismo
			
			for (Iterator<PrestadorLugarAtencion> iterator = prestador.getLugaresAtencion().iterator(); iterator.hasNext();) {
				PrestadorLugarAtencion pla = iterator.next();
						
				stmt4 = con.prepareCall(sql4.toString());
				stmt4.setInt(1, idPrestador);
				if(pla.getId_domicilio() > 0){ // indirecto
					stmt4.setInt(2, pla.getId_domicilio());
				}else{
					stmt4.setNull(2, Types.INTEGER);
				}
				stmt4.setString(3, pla.getFactura());
				stmt4.setString(4, pla.getNombre());
				stmt4.setInt(5, pla.getIdPrestadorAtencion());
				stmt4.setInt(6, pla.getNumeroHabilitacion());
				stmt4.setString(7, pla.getAutoridadHabilitacion());
				if(pla.getVigenciaDesdeHabilitacion()!=null){
					stmt4.setDate(8, new java.sql.Date(pla.getVigenciaDesdeHabilitacion().getTime()));
				}else{
					stmt4.setNull(8, Types.DATE);
				}
				if(pla.getVigenciaHastaHabilitacion()!=null){
					stmt4.setDate(9, new java.sql.Date(pla.getVigenciaHastaHabilitacion().getTime()));
				}else{
					stmt4.setNull(9, Types.DATE);
				}
				stmt4.setBoolean(10, pla.isPresentaCopiaHabilitacion());
				stmt4.setString(11, pla.getCategoriaProfesional());
				stmt4.setString(12, pla.getRegistroHistoriaClinica());
				stmt4.setInt(13, pla.getDomicilio().getProvinciaId());
				stmt4.setInt(14, pla.getDomicilio().getLocalidadId());
				stmt4.setString(15, pla.getDomicilio().getCalle());
				stmt4.setString(16, pla.getDomicilio().getNumero());
				stmt4.setString(17, pla.getDomicilio().getPiso());
				stmt4.setString(18, pla.getDomicilio().getDepto());
				stmt4.setString(19, pla.getDomicilio().getPostal_codi());
				stmt4.setString(20, pla.getDomicilio().getBarrio());
				stmt4.setString(21, screenName);	

				ResultSet rs2 = stmt4.executeQuery();
				while (rs2.next()) {
					idDomicilio=rs2.getInt(1); 
				}
				
//				*** TELEFONOS DEL LUGAR DE ATENCION DEL PRESTADOR
				if(pla.getTelefonos()!=null && pla.getTelefonos().size()>0){
					for (Iterator<TelefonoPrestador> iterator2 = pla.getTelefonos().iterator(); iterator2.hasNext();) {
						TelefonoPrestador tel = iterator2.next();
					
						stmt5 = con.prepareCall(sql5.toString());
						stmt5.setInt(1, idPrestador);
						if(pla.getId_domicilio() > 0){ // indirecto
							stmt5.setInt(2, idDomicilio);
						}else{
							stmt5.setNull(2, Types.INTEGER); // son Telefonos del prestador pero no del Lugar de Atencion Indirecto
						}
						stmt5.setString(3, tel.getTipo());
						stmt5.setString(4, tel.getCodigoPais());
						stmt5.setString(5, tel.getCodigoArea());
						stmt5.setString(6, tel.getNumero());
						stmt5.setString(7, tel.getExtension());
						stmt5.setString(8, tel.getObservaciones());
						stmt5.setString(9, screenName);	
	
						stmt5.executeUpdate();
					}
				}
//				*** CONTACTOS ELECT. DEL LUGAR DE ATENCION DEL PRESTADOR
				if(pla.getContactosElectronicos()!=null && pla.getContactosElectronicos().size()>0){
					for (Iterator<ContactoElectronicoPrestador> iterator3 = pla.getContactosElectronicos().iterator(); iterator3.hasNext();) {
						ContactoElectronico ce = iterator3.next();
	
						stmt6 = con.prepareCall(sql6.toString());
						stmt6.setInt(1, idPrestador);
						if(pla.getId_domicilio() > 0){ // indirecto
							stmt6.setInt(2, idDomicilio);
						}else{
							stmt6.setNull(2, Types.INTEGER); // son Conactos Elect. del prestador pero no del Lugar de Atencion Indirecto
						}
						stmt6.setString(3, ce.getTipo().getId());
						stmt6.setString(4, ce.getContacto());
						stmt6.setString(5, ce.getObservaciones());
						stmt6.setString(6, screenName);	
	
						stmt6.executeUpdate();
					}
				}	
			}
//			*** PLANES DEL PRESTADOR
			if(prestador.getPlanes()!=null && prestador.getPlanes().size() > 0){
				for (PrestadorPlan pp : prestador.getPlanes()) {
					stmt7 = con.prepareCall(sql7.toString());
					stmt7.setInt(1, idPrestador);
					stmt7.setInt(2, pp.getId_plan());
					if (pp.getVigencia_desde() != null) {
						stmt7.setDate(3, new java.sql.Date(pp.getVigencia_desde().getTime()));
					} else {
						stmt7.setNull(3, Types.DATE);
					}
					if (pp.getVigencia_hasta() != null) {
						stmt7.setDate(4, new java.sql.Date(pp.getVigencia_hasta().getTime()));
					} else {
						stmt7.setNull(4, Types.DATE);
					}
					stmt7.setString(5, screenName);		
					
					stmt7.executeUpdate();
				}
			}	
			
			con.commit();
		} catch (SQLException e) {
			_log.error("Error al insertar prestador y sus componentes", e);
			try {
				con.rollback();
			} catch (SQLException e1) {
				throw new SystemException(e1);
			}
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt);
			ConnectionHelper.cerrar(stmt2);
			ConnectionHelper.cerrar(stmt3);
			ConnectionHelper.cerrar(stmt4);
			ConnectionHelper.cerrar(stmt5);
			ConnectionHelper.cerrar(stmt6, con);
		}
		return idPrestador;
	}
	
	public List<PrestadorLugarAtencion> getLugaresAtencionDelPrestador(int idPrestador) {
		
		List<PrestadorLugarAtencion> lugaresAt = new ArrayList<PrestadorLugarAtencion>();
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call buscar_lugares_atencion_del_prestador_by_id(?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, idPrestador);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				PrestadorLugarAtencion pla = PrestadorLugarAtencion.getMappingConDomicilio("pla_", rs) ;
				lugaresAt.add(pla);
			}
		} catch (Exception e) {
			_log.error("Error al buscar lugares de atención del prestador", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return lugaresAt;
	}
	
	public static List<PrestadorPlan> getPlanesDelPrestador(int idPrestador) {
		
		List<PrestadorPlan> planesPrest = new ArrayList<PrestadorPlan>();
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call buscar_planes_at_del_prestador_by_id(?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, idPrestador);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				PrestadorPlan pla = PrestadorPlan.getMapping("pplan_", rs) ;
				planesPrest.add(pla);
			}
		} catch (Exception e) {
			_log.error("Error al buscar lugares de atención del prestador", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return planesPrest;
	}

	public List<ProfesionPrestador> getProfesionesEspecialidadesySubEspecialidades(int idPrestador) {
		
		Connection con = null;
		CallableStatement stmt = null;

		List<ProfesionPrestador> profesiones = new ArrayList<ProfesionPrestador>();
		List<EspecialidadPrestador> especialidades = new ArrayList<EspecialidadPrestador>();
		List<SubEspecialidadPrestador> subespecialidades = new ArrayList<SubEspecialidadPrestador>();
		
		ProfesionPrestador prof = null;
		EspecialidadPrestador esp = null;
		SubEspecialidadPrestador subEsp = null;
		
		try {
			String sql = "{call autorizaciones.buscar_prof_esp_subesp_prestador(?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, idPrestador);

			ResultSet rs = stmt.executeQuery();
			
			while (rs.next()) {
				
				prof = ProfesionPrestador.getMappingProfesionDelPrestador("prof_", rs);
				esp = EspecialidadPrestador.getMappingEspecialidadDelPrestador("esp_", rs);
				subEsp = SubEspecialidadPrestador.getMappingSubEspecialidadDelPrestador("subesp_", rs);
				
				especialidades = new ArrayList<EspecialidadPrestador>();
				subespecialidades = new ArrayList<SubEspecialidadPrestador>();
				
				if(subEsp.getId() > 0){
					subespecialidades.add(subEsp);
				}
				esp.setSubEspecialidades(subespecialidades);
				
				especialidades.add(esp);
				prof.setEspecialidades(especialidades);
				
				profesiones.add(prof);
			}
		} catch (Exception e) {
			_log.error("Error al buscar lista de profesiones-especialidades-subespecialidades del prestador", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return profesiones;
	}
	
	public int actualizar(Prestador prestador, String screenName) throws SystemException, DuplicatePrestadorIdException {
		
		Connection con = null;
		CallableStatement stmt = null, stmt2 = null, stmt3 = null, stmt4 = null, stmt5 = null, stmt6 = null, stmt7 = null;
		
		int idPrestador=0, idDomicilio=0;
		
		idPrestador = prestador.getId_prestador();
		
		try {
			
			String sql  = "{call autorizaciones.actualizar_prestador(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
			String sql2 = "{call autorizaciones.insertar_matricula_prestador(?,?,?,?,?,?,?)}";
			String sql21 = "{call autorizaciones.borrar_matricula_prestador(?,?)}";
			String sql3 = "{call autorizaciones.insertar_prof_espec_subesp_prestador(?,?,?,?,?,?,?,?)}";
			String sql31 = "{call autorizaciones.borrar_prof_espec_subesp_prestador(?,?)}";
			String sql4 = "{call autorizaciones.insertar_lugar_atencion_prestador(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";
			String sql41 = "{call autorizaciones.borrar_lugar_atencion_prestador(?,?,?)}";
			String sql42 = "{call autorizaciones.actualizar_lugar_atencion_prestador(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";
			String sql5 = "{call autorizaciones.insertar_telefonos_prestador(?, ?, ?, ?, ?, ?, ?, ?, ?)}";
			String sql51 = "{call autorizaciones.borrar_telefono_prestador(?,?,?)}";
			String sql6 = "{call autorizaciones.insertar_contactose_prestador(?, ? ,? ,? ,? ,?)}";
			String sql61 = "{call autorizaciones.borrar_contacto_prestador(?,?,?)}";
			String sql7 = "{call autorizaciones.insertar_plan_prestador(?, ? ,? ,? ,?)}";
			String sql71 = "{call autorizaciones.actualizar_plan_prestador(?,?,?,?,?,?,?,?)}";
			
			con = ConnectionHelper.getConnection();
			con.setAutoCommit(false);

//			*** PRESTADOR ***
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, prestador.getCuit());
			stmt.setInt(2, prestador.getTipo().getId()); // tipoPrestador
			stmt.setString(3, prestador.getContacto());
			stmt.setString(4, prestador.getObservaciones());
			stmt.setString(5, prestador.getDescripcion());
			stmt.setString(6, prestador.getCodigoHospital());
			stmt.setBoolean(7, prestador.getCertificacionProfesional());
			if (null != prestador.getFechaVtoCertificacion()) {
				stmt.setDate(8, new java.sql.Date(prestador.getFechaVtoCertificacion().getTime()));
			} else {
				stmt.setNull(8, Types.DATE);
			}	
			stmt.setBoolean(9, prestador.getSeguroCobertura());
			stmt.setString(10, prestador.getCiaSeguro());
			if (null != prestador.getFechaVtoSeguro()) {
				stmt.setDate(11, new java.sql.Date(prestador.getFechaVtoSeguro().getTime()));
			} else {
				stmt.setNull(11, Types.DATE);
			}
			stmt.setString(12, prestador.getOtorgaCertificacion());
			stmt.setInt(13, prestador.getId_prestador());
			stmt.setString(14, screenName);
			if (StringUtils.checkNotEmpty(prestador.getCbu())) {
			   stmt.setString(15, prestador.getCbu());
			}else {
				stmt.setNull(15, Types.VARCHAR);
			}

			stmt.executeUpdate();
	
//			*** MATRICULAS ***
			if(prestador.getMatriculas()!=null && prestador.getMatriculas().size() > 0){
				for (MatriculaPrestador mat : prestador.getMatriculas()) {
					if(mat.getEstado() != null){ // si el estado es null, es un registro que no se modifico (ni alta ni baja)
						if(mat.getEstado().equals(MatriculaPrestador.ESTADOS.BAJA)){ 
							stmt2 = con.prepareCall(sql21.toString());
							stmt2.setInt(1, mat.getIdMatricula());
							stmt2.setString(2, screenName);													
						}else if(mat.getEstado().equals(MatriculaPrestador.ESTADOS.NUEVO)){
							stmt2 = con.prepareCall(sql2.toString());
							stmt2.setInt(1, idPrestador);
							stmt2.setInt(2, mat.getNumero());
							if(mat.getProvincia()!=null){
								stmt2.setInt(3, mat.getProvincia().getId());
							}else{
								stmt2.setNull(3, Types.INTEGER);
							}
							stmt2.setString(4, mat.getTipo());
							stmt2.setString(5, screenName);		
							stmt2.setBoolean(6, mat.isPresentaCopia());		
							if (mat.getFechaVto() != null) {
								stmt2.setDate(7, new java.sql.Date(mat.getFechaVto().getTime()));
							} else {
								stmt2.setNull(7, Types.DATE);
							}
						}
						stmt2.executeUpdate();
					}
				}
			}	
//			*** PROFESION, ESPECIALIDAD y SUBESPECIALIDAD ***
			if(prestador.getProfesiones()!=null && prestador.getProfesiones().size() > 0){
				for (Iterator<ProfesionPrestador> iterator = prestador.getProfesiones().iterator(); iterator.hasNext();) {
					ProfesionPrestador p =  iterator.next();
					
					for (Iterator<EspecialidadPrestador> iterator2 = p.getEspecialidades().iterator(); iterator2.hasNext();) {
						EspecialidadPrestador e = iterator2.next();
	
						if(e.getSubEspecialidades() != null && e.getSubEspecialidades().size()>0){
							for (Iterator<SubEspecialidadPrestador> iterator3 = e.getSubEspecialidades().iterator(); iterator3.hasNext();) {
								SubEspecialidadPrestador s =  iterator3.next();
								
								if(p.getEstado()!= null && p.getEstado().equals(ProfesionPrestador.ESTADOS.BAJA)){ 
									stmt3 = con.prepareCall(sql31.toString());
									stmt3.setInt(1, p.getIdPrestProf());
									stmt3.setString(2, screenName);	
									
									stmt3.executeUpdate();
									
								}else if(p.getEstado()!= null && p.getEstado().equals(ProfesionPrestador.ESTADOS.NUEVO)){
									stmt3 = con.prepareCall(sql3.toString());
									stmt3.setInt(1, idPrestador);
									stmt3.setInt(2, p.getIdProfesion());
									stmt3.setInt(3, e.getIdEspecialidad());
									stmt3.setInt(4, s.getId()); //con id de subespecialidad
									stmt3.setString(5, screenName);	
									stmt3.setString(6, p.getCategoriaProfOspim());	
									stmt3.setBoolean(7, p.isTituloProfesional());
									stmt3.setBoolean(8, e.isTituloEspecialidad());
									
									stmt3.executeUpdate();
								}
							}
						}else{
							if(p.getEstado() != null && p.getEstado().equals(ProfesionPrestador.ESTADOS.BAJA)){ 
								stmt3 = con.prepareCall(sql31.toString());
								stmt3.setInt(1, p.getIdPrestProf());
								stmt3.setString(2, screenName);	
								
								stmt3.executeUpdate();
								
							}else if(p.getEstado() != null && p.getEstado().equals(ProfesionPrestador.ESTADOS.NUEVO)){
								stmt3 = con.prepareCall(sql3.toString());
								stmt3.setInt(1, idPrestador);
								stmt3.setInt(2, p.getIdProfesion());
								stmt3.setInt(3, e.getIdEspecialidad());
								stmt3.setNull(4, Types.INTEGER); // sin subEspecialidad
								stmt3.setString(5, screenName);	
								stmt3.setString(6, p.getCategoriaProfOspim());	
								stmt3.setBoolean(7, p.isTituloProfesional());
								stmt3.setBoolean(8, e.isTituloEspecialidad());
								
								stmt3.executeUpdate();
							}	
						}
					}
				}
			}
//			*** Lugar Atencion, La Direccion, Los telefonos y Contactos electrónicos del mismo
			
//			(En la actualizacion, nos pueden dar de baja todo el lugar de atencion, 
//			o editar el lugar de atencion(lugar de at y domicilio), altas o bajas de telefonos y contactos de ese lugar de atencion
//			o pueden agregar un nuevo lugar de atencion completo(lugar at, domicilio, telefonos y contactos))
			for (Iterator<PrestadorLugarAtencion> iterator = prestador.getLugaresAtencion().iterator(); iterator.hasNext();) {
				PrestadorLugarAtencion pla = iterator.next();
				
				idDomicilio = pla.getId_domicilio();
				
				if(pla.getEstado() != null && pla.getEstado().equals(PrestadorLugarAtencion.ESTADOS.BAJA)){
					stmt4 = con.prepareCall(sql41.toString());
					stmt4.setInt(1, pla.getId_prestador());
					stmt4.setInt(2, idDomicilio);
					stmt4.setString(3, screenName);	
					
					stmt4.executeUpdate();
					
				}else if(pla.getEstado() != null && pla.getEstado().equals(PrestadorLugarAtencion.ESTADOS.NUEVO)){
					stmt4 = con.prepareCall(sql4.toString());
					stmt4.setInt(1, idPrestador);
					if(pla.getId_domicilio() > 0){ // indirecto
						stmt4.setInt(2, pla.getId_domicilio());
					}else{
						stmt4.setNull(2, Types.INTEGER);
					}
					stmt4.setString(3, pla.getFactura());
					stmt4.setString(4, pla.getNombre());
					stmt4.setInt(5, pla.getIdPrestadorAtencion());
					stmt4.setInt(6, pla.getNumeroHabilitacion());
					stmt4.setString(7, pla.getAutoridadHabilitacion());
					if(pla.getVigenciaDesdeHabilitacion()!=null){
						stmt4.setDate(8, new java.sql.Date(pla.getVigenciaDesdeHabilitacion().getTime()));
					}else{
						stmt4.setNull(8, Types.DATE);
					}
					if(pla.getVigenciaHastaHabilitacion()!=null){
						stmt4.setDate(9, new java.sql.Date(pla.getVigenciaHastaHabilitacion().getTime()));
					}else{
						stmt4.setNull(9, Types.DATE);	
					}	
					stmt4.setBoolean(10, pla.isPresentaCopiaHabilitacion());
					stmt4.setString(11, pla.getCategoriaProfesional());
					stmt4.setString(12, pla.getRegistroHistoriaClinica());
					stmt4.setInt(13, pla.getDomicilio().getProvinciaId());
					stmt4.setInt(14, pla.getDomicilio().getLocalidadId());
					stmt4.setString(15, pla.getDomicilio().getCalle());
					stmt4.setString(16, pla.getDomicilio().getNumero());
					stmt4.setString(17, pla.getDomicilio().getPiso());
					stmt4.setString(18, pla.getDomicilio().getDepto());
					stmt4.setString(19, pla.getDomicilio().getPostal_codi());
					stmt4.setString(20, pla.getDomicilio().getBarrio());
					stmt4.setString(21, screenName);	
					
					ResultSet rs2 = stmt4.executeQuery();
					while (rs2.next()) {
						idDomicilio=rs2.getInt(1); 
					}
					
				}else if(pla.getEstado() != null && 
						pla.getEstado().equals(PrestadorLugarAtencion.ESTADOS.MODIF) &&
						pla.getFactura().equalsIgnoreCase("DIRECTO")){  // los lugares indirectos no se modifica domicilio ni otros campos de LA.
					stmt4 = con.prepareCall(sql42.toString());
					stmt4.setInt(1, idPrestador);
					stmt4.setInt(2, idDomicilio);
					stmt4.setString(3, pla.getFactura());
					stmt4.setString(4, pla.getNombre());
					stmt4.setInt(5, pla.getIdPrestadorAtencion());
					stmt4.setInt(6, pla.getNumeroHabilitacion());
					stmt4.setString(7, pla.getAutoridadHabilitacion());
					if(pla.getVigenciaDesdeHabilitacion()!=null){
						stmt4.setDate(8, new java.sql.Date(pla.getVigenciaDesdeHabilitacion().getTime()));
					}else{
						stmt4.setNull(8, Types.DATE);
					}
					if(pla.getVigenciaHastaHabilitacion()!=null){
						stmt4.setDate(9, new java.sql.Date(pla.getVigenciaHastaHabilitacion().getTime()));
					}else{
						stmt4.setNull(9, Types.DATE);
					}	
					stmt4.setBoolean(10, pla.isPresentaCopiaHabilitacion());
					stmt4.setString(11, pla.getCategoriaProfesional());
					stmt4.setString(12, pla.getRegistroHistoriaClinica());
					stmt4.setInt(13, pla.getDomicilio().getProvinciaId());
					stmt4.setInt(14, pla.getDomicilio().getLocalidadId());
					stmt4.setString(15, pla.getDomicilio().getCalle());
					stmt4.setString(16, pla.getDomicilio().getNumero());
					stmt4.setString(17, pla.getDomicilio().getPiso());
					stmt4.setString(18, pla.getDomicilio().getDepto());
					stmt4.setString(19, pla.getDomicilio().getPostal_codi());
					stmt4.setString(20, pla.getDomicilio().getBarrio());
					stmt4.setString(21, screenName);	
						
					stmt4.executeUpdate();
				}

//				*** TELEFONOS DEL LUGAR DE ATENCION DEL PRESTADOR
				if( (pla.getEstado() != null && !pla.getEstado().equals(PrestadorLugarAtencion.ESTADOS.BAJA))){

					if(pla.getTelefonos()!=null && pla.getTelefonos().size()>0){
						for (Iterator<TelefonoPrestador> iterator2 = pla.getTelefonos().iterator(); iterator2.hasNext();) {
							TelefonoPrestador tel = iterator2.next();
							
							if(tel.getEstado() != null && tel.getEstado().equals(Telefono.ESTADOS.BAJA)){
								stmt5 = con.prepareCall(sql51.toString());
								stmt5.setInt(1, idDomicilio);
								stmt5.setInt(2, tel.getId());
								stmt5.setString(3, screenName);	
								
								stmt5.executeUpdate();
								
							}else if(tel.getEstado() != null && tel.getEstado().equals(Telefono.ESTADOS.NUEVO)){
								stmt5 = con.prepareCall(sql5.toString());
								stmt5.setInt(1, idPrestador);
								if(pla.getFactura().equalsIgnoreCase("DIRECTO")){
									stmt5.setInt(2, idDomicilio);
								}else{
									stmt5.setNull(2, Types.INTEGER);
								}
								stmt5.setString(3, tel.getTipo());
								stmt5.setString(4, tel.getCodigoPais());
								stmt5.setString(5, tel.getCodigoArea());
								stmt5.setString(6, tel.getNumero());
								stmt5.setString(7, tel.getExtension());
								stmt5.setString(8, tel.getObservaciones());
								stmt5.setString(9, screenName);	
							
								stmt5.executeUpdate();
							}	
						}
					}	
	//				*** CONTACTOS ELECT. DEL LUGAR DE ATENCION DEL PRESTADOR
					if(pla.getContactosElectronicos()!=null && pla.getContactosElectronicos().size()>0){
						for (Iterator<ContactoElectronicoPrestador> iterator3 = pla.getContactosElectronicos().iterator(); iterator3.hasNext();) {
							ContactoElectronico ce = iterator3.next();
		
							if(ce.getEstado() != null && ce.getEstado().equals(ContactoElectronico.ESTADOS.BAJA)){
								stmt6 = con.prepareCall(sql61.toString());
								stmt6.setInt(1, idDomicilio);
								stmt6.setInt(2, ce.getId());
								stmt6.setString(3, screenName);		
								
								stmt6.executeUpdate();
								
							}else if(ce.getEstado() != null && ce.getEstado().equals(ContactoElectronico.ESTADOS.NUEVO)){
								stmt6 = con.prepareCall(sql6.toString());
								stmt6.setInt(1, idPrestador);
								if(pla.getFactura().equalsIgnoreCase("DIRECTO")){
									stmt6.setInt(2, idDomicilio);
								}else{
									stmt6.setNull(2, Types.INTEGER);
								}
								stmt6.setString(3, ce.getTipo().getId());
								stmt6.setString(4, ce.getContacto());
								stmt6.setString(5, ce.getObservaciones());
								stmt6.setString(6, screenName);	
							
								stmt6.executeUpdate();
							}	
						}
					}	
				}	
			}
			
//			*** PLANES DEL PRESTADOR

			if(prestador.getPlanes()!=null && prestador.getPlanes().size()>0){
				for (PrestadorPlan pp : prestador.getPlanes()) {
					
					if(pp.getEstado() != null && pp.getEstado().equals(PrestadorPlan.ESTADOS.NUEVO)){
						stmt7 = con.prepareCall(sql7.toString());
						stmt7.setInt(1, idPrestador);
						stmt7.setInt(2, pp.getId_plan());
						if (pp.getVigencia_desde() != null) {
							stmt7.setDate(3, new java.sql.Date(pp.getVigencia_desde().getTime()));
						} else {
							stmt7.setNull(3, Types.DATE);
						}
						if (pp.getVigencia_hasta() != null) {
							stmt7.setDate(4, new java.sql.Date(pp.getVigencia_hasta().getTime()));
						} else {
							stmt7.setNull(4, Types.DATE);
						}
						stmt7.setString(5, screenName);		
						
						stmt7.executeUpdate();
					}else if(pp.getEstado() != null && pp.getEstado().equals(PrestadorPlan.ESTADOS.BAJA)){
							
						stmt7 = con.prepareCall(sql71.toString());
						stmt7.setInt(1, pp.getId());
						stmt7.setInt(2, pp.getId_prestador());
						stmt7.setInt(3, pp.getId_plan());
						if (pp.getVigencia_desde() != null) {
							stmt7.setDate(4, new java.sql.Date(pp.getVigencia_desde().getTime()));
						} else {
							stmt7.setNull(4, Types.DATE);
						}
						if (pp.getVigencia_hasta() != null) {
							stmt7.setDate(5, new java.sql.Date(pp.getVigencia_hasta().getTime()));
						} else {
							stmt7.setNull(5, Types.DATE);
						}
						stmt7.setString(6, screenName);
						if(pp.getEstado() != null && pp.getEstado().equals(PrestadorPlan.ESTADOS.BAJA)){
							stmt7.setString(7, screenName);
							stmt7.setTimestamp(8, new java.sql.Timestamp(Calendar.getInstance().getTimeInMillis()));
						}else{
							stmt7.setNull(7, Types.VARCHAR);
							stmt7.setNull(8, Types.TIMESTAMP);
						}
						stmt7.executeUpdate();	
					}	
						
				}
			}	
			con.commit();
		} catch (SQLException e) {
			_log.error("Error al actualizar prestador y sus componentes", e);
			try {
				con.rollback();
			} catch (SQLException e1) {
				throw new SystemException(e1);
			}
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt);
			ConnectionHelper.cerrar(stmt2);
			ConnectionHelper.cerrar(stmt3);
			ConnectionHelper.cerrar(stmt4);
			ConnectionHelper.cerrar(stmt5);
			ConnectionHelper.cerrar(stmt6, con);
		}
		return idPrestador;
	}
	
	
	public List<Prestador> getPrestadores(int id, String cuit,
			String descripcion, int provincia, int localidad,boolean soloVigentes,
			int profesion, int especialidad, int subEspecialidad, int tipoPrestador, String hospital) {
		Connection con = null;
		CallableStatement stmt = null;
		List<Prestador> listaPrestadores = null;
		Prestador pr = null;
		
		try {
			String sql = "{call buscar_prestadores(?,?,?,?,?,?,?,?,?,?,?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			if (cuit != null && cuit.trim().equals("")) {
				cuit = null;
			}
			stmt.setString(1, cuit);
			stmt.setString(2, descripcion);
			stmt.setInt(3, id);
			stmt.setInt(4, provincia);
			stmt.setInt(5, localidad);
			stmt.setBoolean(6, soloVigentes);
			stmt.setInt(7, profesion);
			stmt.setInt(8, especialidad);
			stmt.setInt(9, subEspecialidad);
			stmt.setInt(10, tipoPrestador);
			stmt.setString(11, hospital);

			ResultSet rs = stmt.executeQuery();
			listaPrestadores = new ArrayList<Prestador>();
			while (rs.next()) {
				pr = Prestador.getMapping(rs, "prs__");
				pr.setCbu(rs.getString("prs__cbu"));
				listaPrestadores.add(pr);
			}
		} catch (Exception e) {
			_log.error("Error en busqueda prestadores", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return listaPrestadores;
	}

	public int actualizarSolicitarCotizacionPrestador(
			int idPrestador,
			boolean solicitarCotizacion,
			String screenName) throws SystemException {

		Connection con = null;
		CallableStatement stmt = null;
		int resultado = 0;

		try {
			String sql = "{call autorizaciones.actualizar_solicitar_cotizacion_prestador(?,?,?)}";

			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql);

			stmt.setInt(1, idPrestador);
			stmt.setBoolean(2, solicitarCotizacion);
			stmt.setString(3, screenName);

			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				resultado = rs.getInt(1);
			}

		} catch (SQLException e) {
			_log.error("Error al actualizar solicitar cotizacion del prestador", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}

		return resultado;
	}
}
