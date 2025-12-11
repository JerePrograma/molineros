package ar.com.cgt.ddhh.services;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.hsqldb.Types;

import ar.com.cgt.ddhh.beans.Area;
import ar.com.cgt.ddhh.beans.Comentario;
import ar.com.cgt.ddhh.beans.Contacto;
import ar.com.cgt.ddhh.beans.LineaTrabajo;
import ar.com.cgt.ddhh.beans.Organismo;
import ar.com.ospim.global.beans.Domicilio;
import ar.com.ospim.util.ConnectionHelper;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

public class OrganismoServiceImpl {
	
	private static final int AREA=2;
	private static final int ORGANISMO=1;
	
	private static Log _log = LogFactoryUtil.getLog(OrganismoServiceImpl.class);

	private static OrganismoServiceImpl instance = null;

	public static OrganismoServiceImpl getInstance() {
		if (null == instance) {
			instance = new OrganismoServiceImpl();
		}
		return instance;
	}
	
	public void borrarOrganismo(int id_organismo) throws Exception {
		Connection con = null;
		CallableStatement stmt = null;
		
		try {
			con = ConnectionHelper.getConnectionCGT();
			String sql = "{call borrar_organismo(?)}";
			stmt = con.prepareCall(sql.toString());

			stmt.setInt(1, id_organismo);

			ResultSet rs = stmt.executeQuery();
			
		} catch (SQLException e) {
			_log.error("Error al borrar area", e);
			throw new SystemException(e);

		} catch (Exception e) {
			_log.error("Error al borrar area", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}		

	}
	
	public void borrarArea(int id_area) throws Exception {
		Connection con = null;
		CallableStatement stmt = null;
		
		try {
			con = ConnectionHelper.getConnectionCGT();
			String sql = "{call borrar_area(?)}";
			stmt = con.prepareCall(sql.toString());

			stmt.setInt(1, id_area);

			ResultSet rs = stmt.executeQuery();
			
		} catch (SQLException e) {
			_log.error("Error al borrar area", e);
			throw new SystemException(e);

		} catch (Exception e) {
			_log.error("Error al borrar area", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt,con);
		}		

	}

	public List<Organismo> getOrganismos(String nombre, String ambito,
			String linea, String sigla, String orbita) throws Exception {
		Connection con = null;
		CallableStatement stmt = null;
		List<Organismo> organismos = new ArrayList<Organismo>();
		try {
			con = ConnectionHelper.getConnectionCGT();
			String sql = "{call buscar_organismos(?,?,?,?,?)}";
			stmt = con.prepareCall(sql.toString());
			if (null != nombre && !nombre.trim().equals("")) {
				stmt.setString(1, nombre);
			} else {
				stmt.setNull(1, Types.VARCHAR);
			}
			if (null != ambito && !ambito.trim().equals("")) {
				stmt.setString(2, ambito);
			} else {
				stmt.setNull(2, Types.VARCHAR);
			}

			if (null != linea && !linea.trim().equals("")) {
				stmt.setString(3, linea);
			} else {
				stmt.setNull(3, Types.VARCHAR);
			}
			
			if (null != sigla && !sigla.trim().equals("")) {
				stmt.setString(4, sigla);
			} else {
				stmt.setNull(4, Types.VARCHAR);
			}
			
			if (null != orbita && !orbita.trim().equals("")) {
				stmt.setString(5, orbita);
			} else {
				stmt.setNull(5, Types.VARCHAR);
			}

			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				Organismo org = new Organismo();
				org.setNombre(rs.getString("denominacion"));
				org.setSigla(rs.getString("sigla"));
				org.setId_organismo(rs.getInt("id_organismo"));
				org.setAmbito(rs.getString("ambito"));
				org.setTelefono(rs.getString("telefono"));
				org.setWeb(rs.getString("web"));
				org.setLineasString(rs.getString("lineas"));
				org.setOrbita(rs.getString("orbita"));
				org.setEmail(rs.getString("email"));
				org.setAreas(getAreas(org.getId_organismo(),linea, con));
				organismos.add(org);
			}
		} catch (SQLException e) {
			_log.error("Error al buscar organismos", e);
			throw new SystemException(e);

		} catch (Exception e) {
			_log.error("Error al buscar organismos", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt,con);
		}
		return organismos;

	}

	public List<LineaTrabajo> getLineasTrabajo(int id_organismo, Connection con, int origen)
			throws Exception {

		CallableStatement stmt = null;
		List<LineaTrabajo> lineas = null;
		try {			
			String sql = "{call buscar_lineas_trabajo(?,?)}";
			stmt = con.prepareCall(sql.toString());

			stmt.setInt(1, id_organismo);
			stmt.setInt(2, origen);

			
			lineas = new ArrayList<LineaTrabajo>();
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				LineaTrabajo linea = new LineaTrabajo();
				linea.setTipoLinea(rs.getString("tipo_linea"));
				linea.setDescripcion(rs.getString("lineas"));
				lineas.add(linea);
			}
		} catch (SQLException e) {
			_log.error("Error al buscar organismos", e);
			throw new SystemException(e);

		} catch (Exception e) {
			_log.error("Error al buscar organismos", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt);
		}
		return lineas;
	}

	public List<Contacto> getContactos(int id_organismo, Connection con, int origen)
			throws Exception {
		CallableStatement stmt = null;
		List<Contacto> contactos = null;
		try {
			
			String sql = "{call buscar_contactos(?,?)}";
			stmt = con.prepareCall(sql.toString());

			stmt.setInt(1, id_organismo);
			stmt.setInt(2, origen);

			contactos = new ArrayList<Contacto>();			
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				Contacto contacto = new Contacto();
				contacto.setCargo(rs.getString("cargo"));
				contacto.setApellido(rs.getString("apellido"));
				contacto.setNombre(rs.getString("nombre"));
				contacto.setEmail(rs.getString("email"));
				contacto.setTelefono(rs.getString("telefono"));
				contacto.setTratamiento(rs.getString("tratamiento"));
				contactos.add(contacto);
			}
		} catch (SQLException e) {
			_log.error("Error al buscar organismos", e);
			throw new SystemException(e);

		} catch (Exception e) {
			_log.error("Error al buscar organismos", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt);
		}
		return contactos;
	}

	public List<Comentario> getComentarios(int id_organismo, Connection con, int origen) throws Exception {
		
		CallableStatement stmt = null;
		List<Comentario> comentarios = null;
		try {
			
			String sql = "{call buscar_comentarios(?,?)}";
			stmt = con.prepareCall(sql.toString());

			stmt.setInt(1, id_organismo);
			stmt.setInt(2, origen);

			comentarios = new ArrayList<Comentario>();
			
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				Comentario comentario = new Comentario();
				comentario.setFecha(rs.getDate("fecha"));
				comentario.setDescripcion(rs.getString("comentario"));

				comentarios.add(comentario);
			}
		} catch (SQLException e) {
			_log.error("Error al buscar organismos", e);
			throw new SystemException(e);

		} catch (Exception e) {
			_log.error("Error al buscar organismos", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt);
		}
		
		return comentarios;
	}
	
	public List<Area> getAreas(int id_organismo, String linea, Connection con)
			throws Exception {
		CallableStatement stmt = null;
		List<Area> areas = null;
		try {
			
			String sql = "{call buscar_areas(?,?)}";
			stmt = con.prepareCall(sql.toString());

			stmt.setInt(1, id_organismo);			
			stmt.setString(2, linea);			
			
			areas = new ArrayList<Area>();			
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				Area area = new Area();
				area.setId_area(rs.getInt("id_area"));
				area.setId_organismo(rs.getInt("id_organismo"));
				area.setNombre(rs.getString("denominacion"));
				area.setTelefono(rs.getString("telefono"));
				area.setWeb(rs.getString("web"));
				area.setLineasString(rs.getString("lineas"));				
				areas.add(area);
			}
		} catch (SQLException e) {
			_log.error("Error al buscar organismos", e);
			throw new SystemException(e);

		} catch (Exception e) {
			_log.error("Error al buscar organismos", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt);
		} 
		return areas;
	}
	
	public List<Area> getAreas(int id_organismo, Connection con)
			throws Exception {
		CallableStatement stmt = null;
		List<Area> areas = null;
		try {
			
			String sql = "{call buscar_areas(?)}";
			stmt = con.prepareCall(sql.toString());

			stmt.setInt(1, id_organismo);
			
			areas = new ArrayList<Area>();			
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				Area area = new Area();
				area.setId_area(rs.getInt("id_area"));
				area.setId_organismo(rs.getInt("id_organismo"));
				area.setNombre(rs.getString("denominacion"));
				area.setTelefono(rs.getString("telefono"));
				area.setWeb(rs.getString("web"));
				area.setLineasString(rs.getString("lineas"));				
				areas.add(area);
			}
		} catch (SQLException e) {
			_log.error("Error al buscar organismos", e);
			throw new SystemException(e);

		} catch (Exception e) {
			_log.error("Error al buscar organismos", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt);
		}
		return areas;
	}

	public Organismo getOrganismo(int id_organismo) throws Exception {
		Connection con = null;
		CallableStatement stmt = null;
		Organismo organismo = null;

		try {
			con = ConnectionHelper.getConnectionCGT();
			String sql = "{call buscar_organismo(?)}";
			stmt = con.prepareCall(sql.toString());

			stmt.setInt(1, id_organismo);

			organismo = new Organismo();
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				organismo.setNombre(rs.getString("denominacion"));
				organismo.setSigla(rs.getString("sigla"));
				organismo.setId_organismo(rs.getInt("id_organismo"));
				organismo.setAmbito(rs.getString("ambito"));
				organismo.setTelefono(rs.getString("telefono"));
				organismo.setWeb(rs.getString("web"));
				organismo.setObservaciones(rs.getString("observaciones"));
				organismo.setOrbita(rs.getString("orbita"));
				organismo.setEmail(rs.getString("email"));				
				
				organismo.setDomicilio(getDomicilio(rs));
				
				organismo.setLineasTrabajo(getLineasTrabajo(organismo
						.getId_organismo(),con, ORGANISMO));
				organismo
						.setContactos(getContactos(organismo.getId_organismo(),con,ORGANISMO));
				organismo.setComentario(getComentarios(organismo
						.getId_organismo(),con, ORGANISMO));
				organismo.setAreas(getAreas(organismo.getId_organismo(),con));
			}
		} catch (SQLException e) {
			_log.error("Error al buscar organismos", e);
			throw new SystemException(e);

		} catch (Exception e) {
			_log.error("Error al buscar organismos", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt,con);
		}
		return organismo;

	}
	
	public Domicilio getDomicilio(ResultSet rs) throws SQLException{
		Domicilio domicilio=new Domicilio();
		domicilio.setCalle(rs.getString("calle"));
		domicilio.setNumero(rs.getString("numero"));
		domicilio.setPiso(rs.getString("piso"));
		domicilio.setDepto(rs.getString("departamento"));
		domicilio.setLocalidadId(rs.getInt("id_localidad"));
		domicilio.setProvinciaId(rs.getInt("id_provincia"));
		domicilio.setPostal_codi(rs.getString("cod_postal"));
		domicilio.setPaisId(rs.getInt("id_pais"));
		return domicilio;		
	}
	
	public Area getArea(int id_organismo) throws Exception {
		Connection con = null;
		CallableStatement stmt = null;
		Area area = null;

		try {
			con = ConnectionHelper.getConnectionCGT();
			String sql = "{call buscar_area(?)}";
			stmt = con.prepareCall(sql.toString());

			stmt.setInt(1, id_organismo);

			area = new Area();
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				area.setNombre(rs.getString("denominacion"));
				area.setId_area(rs.getInt("id_area"));
				area.setId_organismo(rs.getInt("id_organismo"));				
				area.setTelefono(rs.getString("telefono"));
				area.setWeb(rs.getString("web"));
				area.setObservaciones(rs.getString("observaciones"));
				area.setEmail(rs.getString("email"));
				
				area.setDomicilio(getDomicilio(rs));
				
				area.setLineasTrabajo(getLineasTrabajo(area
						.getId_area(),con, AREA));
				area.setContactos(getContactos(area.getId_area(),con, AREA));
				area.setComentario(getComentarios(area
						.getId_area(),con, AREA));
			}
		} catch (SQLException e) {
			_log.error("Error al buscar area", e);
			throw new SystemException(e);

		} catch (Exception e) {
			_log.error("Error al buscar area", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return area;

	}
	
	public int saveArea(Area area, String screenName,
			Connection connectionParameter) throws SystemException,
			SQLException {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnectionCGT();
				con.setAutoCommit(false);
			} else {
				con = connectionParameter;
			}
			String sql = "{call inserta_area(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, area.getId_organismo());
			stmt.setString(2, area.getNombre());			
			stmt.setString(3, area.getTelefono());
			stmt.setString(4, area.getWeb());
			stmt.setString(5, area.getObservaciones());			
			
			stmt.setString(6, area.getEmail());
			
			Domicilio domicilio=area.getDomicilio();
			
			stmt.setString(7,domicilio.getCalle());
			stmt.setString(8,domicilio.getNumero());
			stmt.setString(9,domicilio.getPiso());
			stmt.setString(10,domicilio.getDepto());
			stmt.setString(11,domicilio.getPostal_codi());
			stmt.setInt(12,domicilio.getLocalidadId());
			stmt.setInt(13,domicilio.getProvinciaId());
			stmt.setInt(14,domicilio.getPaisId());
			
			
			stmt.setString(15, screenName);
			
			ResultSet rs = stmt.executeQuery();
			if (connectionParameter == null) {
				con.commit();
			}
			while (rs.next()) {
				return rs.getInt(1);
			}
		} catch (SQLException e) {
			_log.error("Error al insertar area", e);
			throw new SystemException(e);

		} catch (Exception e) {
			_log.error("Error al insertar area", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return 0;
	}

	public int save(Organismo organismo, String screenName,
			Connection connectionParameter) throws SystemException,
			SQLException {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnectionCGT();
				con.setAutoCommit(false);
			} else {
				con = connectionParameter;
			}
			String sql = "{call inserta_organismo(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, organismo.getNombre());
			stmt.setString(2, organismo.getAmbito());
			stmt.setString(3, organismo.getTelefono());
			stmt.setString(4, organismo.getWeb());
			stmt.setString(5, organismo.getObservaciones());
			stmt.setString(6, organismo.getSigla());
			stmt.setString(7, organismo.getOrbita());
			stmt.setString(8, organismo.getEmail());
			
			Domicilio domicilio=organismo.getDomicilio();
			
			stmt.setString(9,domicilio.getCalle());
			stmt.setString(10,domicilio.getNumero());
			stmt.setString(11,domicilio.getPiso());
			stmt.setString(12,domicilio.getDepto());
			stmt.setString(13,domicilio.getPostal_codi());
			stmt.setInt(14,domicilio.getLocalidadId());
			stmt.setInt(15,domicilio.getProvinciaId());
			stmt.setInt(16,domicilio.getPaisId());
			
			
			stmt.setString(17, screenName);
			ResultSet rs = stmt.executeQuery();
			if (connectionParameter == null) {
				con.commit();
			}
			while (rs.next()) {
				return rs.getInt(1);
			}
		} catch (SQLException e) {
			_log.error("Error al insertar organismo", e);
			throw new SystemException(e);

		} catch (Exception e) {
			_log.error("Error al insertar organismo", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt,con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return 0;
	}

	public int saveContacto(Contacto contacto, String screenName,
			int id_organismo, Connection connectionParameter, int origen)
			throws SystemException, SQLException {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnectionCGT();
				con.setAutoCommit(false);
			} else {
				con = connectionParameter;
			}
			String sql = "{call inserta_contacto(?,?,?,?,?,?,?,?,?)}";
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, contacto.getNombre());
			stmt.setString(2, contacto.getApellido());
			stmt.setString(3, contacto.getCargo());
			stmt.setString(4, contacto.getTelefono());
			stmt.setString(5, contacto.getEmail());
			stmt.setString(6, contacto.getTratamiento());
			stmt.setInt(7, id_organismo);
			stmt.setString(8, screenName);
			stmt.setInt(9, origen);
			ResultSet rs = stmt.executeQuery();
			if (connectionParameter == null) {
				con.commit();
			}
			while (rs.next()) {
				return rs.getInt(1);
			}
		} catch (SQLException e) {
			_log.error("Error al insertar contacto", e);
			throw new SystemException(e);

		} catch (Exception e) {
			_log.error("Error al insertar contacto", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt,con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return 0;
	}

	public int saveLineas(LineaTrabajo linea, String screenName,
			int id_organismo, Connection connectionParameter, int origen)
			throws SystemException, SQLException {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnectionCGT();
				con.setAutoCommit(false);
			} else {
				con = connectionParameter;
			}
			String sql = "{call inserta_linea(?,?,?,?,?)}";
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, linea.getTipoLinea());
			stmt.setString(2, linea.getDescripcion());
			stmt.setInt(3, id_organismo);
			stmt.setString(4, screenName);
			stmt.setInt(5, origen);
			ResultSet rs = stmt.executeQuery();
			if (connectionParameter == null) {
				con.commit();
			}
			while (rs.next()) {
				return rs.getInt(1);
			}
		} catch (SQLException e) {
			_log.error("Error al insertar linea", e);
			throw new SystemException(e);

		} catch (Exception e) {
			_log.error("Error al insertar linea", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}	
		}
		return 0;
	}

	public int saveComentarios(Comentario comentario, String screenName,
			int id_organismo, Connection connectionParameter, int origen)
			throws SystemException, SQLException {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnectionCGT();
				con.setAutoCommit(false);
			} else {
				con = connectionParameter;
			}
			String sql = "{call inserta_comentario(?,?,?,?,?)}";
			stmt = con.prepareCall(sql.toString());
			stmt.setDate(1, new java.sql.Date(comentario.getFecha().getTime()));
			stmt.setString(2, comentario.getDescripcion());
			stmt.setInt(3, id_organismo);
			stmt.setString(4, screenName);
			stmt.setInt(5, origen);
			ResultSet rs = stmt.executeQuery();
			if (connectionParameter == null) {
				con.commit();
			}
			while (rs.next()) {
				return rs.getInt(1);
			}
		} catch (SQLException e) {
			_log.error("Error al insertar linea", e);
			throw new SystemException(e);

		} catch (Exception e) {
			_log.error("Error al insertar linea", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt,con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}	
		}
		return 0;
	}
	
	public int update(Organismo organismo, String screenName,
			Connection connectionParameter) throws SystemException,
			SQLException {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnectionCGT();
				con.setAutoCommit(false);
			} else {
				con = connectionParameter;
			}
			String sql = "{call actualiza_organismo(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1,organismo.getId_organismo());
			stmt.setString(2, organismo.getNombre());
			stmt.setString(3, organismo.getAmbito());
			stmt.setString(4, organismo.getTelefono());
			stmt.setString(5, organismo.getWeb());
			stmt.setString(6, organismo.getObservaciones());
			stmt.setString(7, organismo.getSigla());
			stmt.setString(8, organismo.getOrbita());
			
			stmt.setString(9, organismo.getEmail());
			
			Domicilio domicilio=organismo.getDomicilio();
			
			stmt.setString(10,domicilio.getCalle());
			stmt.setString(11,domicilio.getNumero());
			stmt.setString(12,domicilio.getPiso());
			stmt.setString(13,domicilio.getDepto());
			stmt.setString(14,domicilio.getPostal_codi());
			stmt.setInt(15,domicilio.getLocalidadId());
			stmt.setInt(16,domicilio.getProvinciaId());
			
			stmt.setString(17, screenName);
			ResultSet rs = stmt.executeQuery();
			if (connectionParameter == null) {
				con.commit();
			}
			while (rs.next()) {
				return rs.getInt(1);
			}
		} catch (SQLException e) {
			_log.error("Error al actualizar organismo", e);
			throw new SystemException(e);

		} catch (Exception e) {
			_log.error("Error al actualizar organismo", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}	
		}
		return organismo.getId_organismo();
	}
	
	public int updateArea(Area area, String screenName,
			Connection connectionParameter) throws SystemException,
			SQLException {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnectionCGT();
				con.setAutoCommit(false);
			} else {
				con = connectionParameter;
			}
			String sql = "{call actualiza_area(?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1,area.getId_area());
			stmt.setString(2, area.getNombre());			
			stmt.setString(3, area.getTelefono());
			stmt.setString(4, area.getWeb());
			stmt.setString(5, area.getObservaciones());
			
			stmt.setString(6, area.getEmail());
			
			Domicilio domicilio=area.getDomicilio();
			
			stmt.setString(7,domicilio.getCalle());
			stmt.setString(8,domicilio.getNumero());
			stmt.setString(9,domicilio.getPiso());
			stmt.setString(10,domicilio.getDepto());
			stmt.setString(11,domicilio.getPostal_codi());
			stmt.setInt(12,domicilio.getLocalidadId());
			stmt.setInt(13,domicilio.getProvinciaId());
			
			
			stmt.setString(14, screenName);
			ResultSet rs = stmt.executeQuery();
			if (connectionParameter == null) {
				con.commit();
			}
			while (rs.next()) {
				return rs.getInt(1);
			}
		} catch (SQLException e) {
			_log.error("Error al actualizar area", e);
			throw new SystemException(e);

		} catch (Exception e) {
			_log.error("Error al actualizar area", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}	
		}
		return area.getId_area();
	}
	
	public int deleteContactos(int id_organismo, String screenName, Connection connectionParameter, int origen)
			throws SystemException, SQLException {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnectionCGT();
				con.setAutoCommit(false);
			} else {
				con = connectionParameter;
			}
			String sql = "{call borra_contactos(?,?,?)}";
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, id_organismo);			
			stmt.setString(2, screenName);
			stmt.setInt(3, origen);
			ResultSet rs = stmt.executeQuery();
			if (connectionParameter == null) {
				con.commit();
			}			
		} catch (SQLException e) {
			_log.error("Error al borrar contacto", e);
			throw new SystemException(e);

		} catch (Exception e) {
			_log.error("Error al borrar contacto", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt,con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return 0;
	}
	
	public int deleteComentarios(int id_organismo, String screenName, Connection connectionParameter, int origen)
			throws SystemException, SQLException {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnectionCGT();
				con.setAutoCommit(false);
			} else {
				con = connectionParameter;
			}
			String sql = "{call borra_comentarios(?,?,?)}";
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, id_organismo);			
			stmt.setString(2, screenName);
			stmt.setInt(3, origen);
			ResultSet rs = stmt.executeQuery();
			if (connectionParameter == null) {
				con.commit();
			}			
		} catch (SQLException e) {
			_log.error("Error al borrar comentario", e);
			throw new SystemException(e);

		} catch (Exception e) {
			_log.error("Error al borrar comentario", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt,con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}	
		}
		return 0;
	}
	
	public int deleteLineasTrabajo(int id_organismo, String screenName, Connection connectionParameter, int origen)
			throws SystemException, SQLException {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			if (connectionParameter == null) {
				con = ConnectionHelper.getConnectionCGT();
				con.setAutoCommit(false);
			} else {
				con = connectionParameter;
			}
			String sql = "{call borra_lineas(?,?,?)}";
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, id_organismo);			
			stmt.setString(2, screenName);
			stmt.setInt(3, origen);
			ResultSet rs = stmt.executeQuery();
			if (connectionParameter == null) {
				con.commit();
			}			
		} catch (SQLException e) {
			_log.error("Error al borrar lineas", e);
			throw new SystemException(e);

		} catch (Exception e) {
			_log.error("Error al borrar lineas", e);
			throw new SystemException(e);
		} finally {
			if (connectionParameter == null) {
				ConnectionHelper.cerrar(stmt,con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
		return 0;
	}

}
