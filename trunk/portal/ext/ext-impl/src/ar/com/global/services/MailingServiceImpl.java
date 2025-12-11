package ar.com.global.services;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.hsqldb.Types;

import ar.com.global.beans.Boletin;
import ar.com.global.beans.Contenido;
import ar.com.global.beans.Destinatario;
import ar.com.global.beans.ListaDestinatarios;
import ar.com.global.exceptions.EmailYaRegistradoException;
import ar.com.ospim.util.ConnectionHelper;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

public class MailingServiceImpl {

	private static Log _log = LogFactoryUtil.getLog(MailingServiceImpl.class);

	public int saveListaEnBase(ListaDestinatarios listaDestinatarios,
			String screenname) throws Exception {
		Connection con = null;

		try {
			_log.debug("comienzo a grabar mails");
			con = ConnectionHelper.getConnectionMailing();

			List<Destinatario> lista = listaDestinatarios
					.getListaDestinatarios();
			int id_lista = grabarListasMailing(listaDestinatarios, screenname,
					con);
			if (lista != null && lista.size() > 0) {
				if (id_lista > 0) {
					listaDestinatarios.setIdListaDestinatarios(id_lista);
					grabarSubscribers(listaDestinatarios, screenname, con);
					insertarSubscribersMailing(listaDestinatarios, screenname,
							con);
				}
			}
			_log.debug("detalles listos");

		} catch (SQLException e) {
			_log.debug("Error al cargar subscribers");
			try {
				con.rollback();
			} catch (SQLException e1) {
				_log.fatal("ERROR AL HACER ROLLBACK!", e);
			}
			throw e;
		} finally {
			ConnectionHelper.cerrar(con);

		}
		return 0;

	}

	public int saveBoletin(Boletin boletin, String screenname) throws Exception {
		Connection con = null;

		try {
			_log.debug("comienzo a grabar mails");
			con = ConnectionHelper.getConnectionMailing();

			int id_boletin = nuevoBoletin(boletin, screenname, con);

			if (id_boletin > 0) {
				boletin.setIdBoletin(id_boletin);
				List<Contenido> contenidos = boletin.getListaContenidos();
				if (null != contenidos && contenidos.size() > 0) {
					grabarContenidos(contenidos, id_boletin, screenname, con);
					insertarListasBoletin(boletin.getListas(), id_boletin,
							screenname, con);
				}

			}

		} catch (SQLException e) {
			_log.debug("Error al cargar subscribers");
			try {
				con.rollback();
			} catch (SQLException e1) {
				_log.fatal("ERROR AL HACER ROLLBACK!", e);
			}
			throw e;
		} finally {
			ConnectionHelper.cerrar(con);

		}
		return 0;

	}
	
	

	public int nuevoBoletin(Boletin boletin, String screenname, Connection con)
			throws Exception {
		CallableStatement stmtMailingList = null;
		boolean vieneCon = false;
		int id_mail = 0;
		try {
			_log.debug("comienzo a grabar mails");
			if (null == con) {
				con = ConnectionHelper.getConnectionMailing();
			} else {
				vieneCon = true;
			}
			con = ConnectionHelper.getConnectionMailing();
			String sqlMailing = "{? = call inserta_boletin(?,?,?,?,?)}";
			stmtMailingList = con.prepareCall(sqlMailing);
			stmtMailingList.registerOutParameter(1, Types.INTEGER);
			stmtMailingList.setString(2, boletin.getNombre());
			stmtMailingList.setString(3, boletin.getAsunto());
			stmtMailingList.setString(4, boletin.getObservaciones());
			stmtMailingList.setBoolean(5, boletin.isSoloTexto());
			stmtMailingList.setString(6, screenname);
			stmtMailingList.execute();
			id_mail = stmtMailingList.getInt(1);
		} catch (SQLException e) {
			_log.error("Error al insertar boletin", e);
			throw new SystemException(e);

		} catch (Exception e) {
			_log.error("Error al insertar boletin", e);
			throw new SystemException(e);
		} finally {
			if (!vieneCon) {
				ConnectionHelper.cerrar(con);
			}
			ConnectionHelper.cerrar(stmtMailingList);
		}
		return id_mail;
	}
	
	public int editarBoletin(Boletin boletin, String screenname, Connection con)
			throws Exception {
		CallableStatement stmtMailingList = null;
		boolean vieneCon = false;
		int id_mail = 0;
		try {
			_log.debug("comienzo a grabar mails");
			if (null == con) {
				con = ConnectionHelper.getConnectionMailing();
			} else {
				vieneCon = true;
			}
			con = ConnectionHelper.getConnectionMailing();
			String sqlMailing = "{call edita_boletin(?,?,?,?,?,?)}";
			stmtMailingList = con.prepareCall(sqlMailing);		
			stmtMailingList.setInt(1, boletin.getIdBoletin());
			stmtMailingList.setString(2, boletin.getNombre());
			stmtMailingList.setString(3, boletin.getAsunto());
			stmtMailingList.setString(4, boletin.getObservaciones());
			stmtMailingList.setBoolean(5, boletin.isSoloTexto());
			stmtMailingList.setString(6, screenname);
			stmtMailingList.execute();

		} catch (SQLException e) {
			_log.error("Error al editar boletin", e);
			throw new SystemException(e);

		} catch (Exception e) {
			_log.error("Error al editar boletin", e);
			throw new SystemException(e);
		} finally {
			if (!vieneCon) {
				ConnectionHelper.cerrar(con);
			}
			ConnectionHelper.cerrar(stmtMailingList);
		}
		return id_mail;
	}

	public int insertarListasBoletin(String[] listas, int id_boletin,
			String screenname, Connection con) throws Exception {

		PreparedStatement stmtMailSubscriber = null;
		boolean vieneCon = false;
		try {
			_log.debug("comienzo a grabar mails");
			if (null == con) {
				con = ConnectionHelper.getConnectionMailing();
			} else {
				vieneCon = true;
			}
			con.setAutoCommit(false);
			String sqlMailingSubs = "INSERT INTO boletin_listas(id_boletin, id_mailing_list, alta_user, alta_fecha) VALUES(?,?,?,?);";
			stmtMailSubscriber = con.prepareStatement(sqlMailingSubs);

			if (listas != null && !listas[0].trim().isEmpty()) {
				for (String mailing : listas) {
					stmtMailSubscriber.setInt(1, id_boletin);
					stmtMailSubscriber.setInt(2, Integer.parseInt(mailing));
					stmtMailSubscriber.setString(3, screenname);
					stmtMailSubscriber.setDate(4,
							new java.sql.Date(System.currentTimeMillis()));
					stmtMailSubscriber.addBatch();
				}
				stmtMailSubscriber.executeBatch();
			}
			con.commit();
		} catch (SQLException e) {
			_log.debug("Error al cargar boletin-mailing");
			try {
				if (!vieneCon) {
					con.rollback();
				}
			} catch (SQLException e1) {
				_log.fatal("ERROR AL HACER ROLLBACK!", e);
			}
			throw e;
		} finally {
			if (!vieneCon) {
				ConnectionHelper.cerrar(stmtMailSubscriber, con);
			}
		}

		return 0;

	}

	public List<Contenido> grabarContenidos(List<Contenido> contenidos,
			int id_boletin, String screenname, Connection con) throws Exception {

		CallableStatement stmt = null;
		boolean vieneCon = false;

		List<Contenido> listaNueva = new ArrayList<Contenido>();

		try {

			_log.debug("comienzo a grabar mails");
			if (null == con) {
				con = ConnectionHelper.getConnectionMailing();
			} else {
				vieneCon = true;
			}
			con = ConnectionHelper.getConnectionMailing();
			String sqlMailing = "{? = call inserta_contenido(?,?,?,?,?)}";

			stmt = con.prepareCall(sqlMailing);

			for (Contenido dest : contenidos) {
				stmt.registerOutParameter(1, Types.INTEGER);
				stmt.setString(2, dest.getSeccion());
				stmt.setString(3, dest.getTitulo());
				stmt.setString(4, dest.getContenido());
				stmt.setInt(5, id_boletin);
				stmt.setString(6, screenname);
				stmt.executeUpdate();
				dest.setIdContenido(stmt.getInt(1));
				listaNueva.add(dest);
			}

		} catch (SQLException e) {
			_log.error("Error al insertar contenido", e);
			throw new SystemException(e);
		} catch (Exception e) {
			_log.error("Error al insertar contenido", e);
			throw new SystemException(e);
		} finally {
			if (!vieneCon) {
				ConnectionHelper.cerrar(con);
			}
			ConnectionHelper.cerrar(stmt);
		}
		return listaNueva;
	}

	public int insertarSubscribersMailing(
			ListaDestinatarios listaDestinatarios, String screenname,
			Connection con) throws Exception {

		PreparedStatement stmtMailSubscriber = null;
		boolean vieneCon = false;
		try {
			_log.debug("comienzo a grabar mails");
			if (null == con) {
				con = ConnectionHelper.getConnectionMailing();
			} else {
				vieneCon = true;
			}
			con.setAutoCommit(false);
			String sqlMailingSubs = "INSERT INTO subscriber_mailing(id_subscriber, id_mailing_list, alta_user, alta_fecha) VALUES(?,?,?,?);";
			stmtMailSubscriber = con.prepareStatement(sqlMailingSubs);

			List<Destinatario> lista = listaDestinatarios
					.getListaDestinatarios();

			if (lista != null && lista.size() > 0) {
				for (Destinatario dest : lista) {

					stmtMailSubscriber.setInt(1, dest.getIdDestinatario());
					stmtMailSubscriber.setInt(2,
							listaDestinatarios.getIdListaDestinatarios());
					stmtMailSubscriber.setString(3, screenname);
					stmtMailSubscriber.setDate(4,
							new java.sql.Date(System.currentTimeMillis()));
					stmtMailSubscriber.addBatch();
				}
				stmtMailSubscriber.executeBatch();
			}
			con.commit();
		} catch (SQLException e) {
			_log.debug("Error al cargar mailing-subscribers");
			try {
				if (!vieneCon) {
					con.rollback();
				}
			} catch (SQLException e1) {
				_log.fatal("ERROR AL HACER ROLLBACK!", e);
			}
			throw e;
		} finally {
			if (!vieneCon) {
				ConnectionHelper.cerrar(stmtMailSubscriber, con);
			}
		}

		return 0;

	}

	public int insertarSubscriberMailing(int id_destinatario,
			int id_mailing_list, String screenname, Connection con)
			throws Exception {

		PreparedStatement stmtMailSubscriber = null;
		boolean vieneCon = false;
		try {
			_log.debug("comienzo a grabar mails");
			if (null == con) {
				con = ConnectionHelper.getConnectionMailing();
			} else {
				vieneCon = true;
			}

			String sqlMailingSubs = "INSERT INTO subscriber_mailing(id_subscriber, id_mailing_list, alta_user, alta_fecha) VALUES(?,?,?,?);";
			stmtMailSubscriber = con.prepareStatement(sqlMailingSubs);

			stmtMailSubscriber.setInt(1, id_destinatario);
			stmtMailSubscriber.setInt(2, id_mailing_list);
			stmtMailSubscriber.setString(3, screenname);
			stmtMailSubscriber.setDate(4,
					new java.sql.Date(System.currentTimeMillis()));
			stmtMailSubscriber.execute();

		} catch (SQLException e) {
			_log.debug("Error al cargar mailing-subscribers");
			throw e;
		} finally {
			if (!vieneCon) {
				ConnectionHelper.cerrar(stmtMailSubscriber, con);
			}
		}

		return 0;

	}

	public ListaDestinatarios grabarSubscribers(
			ListaDestinatarios listaDestinatarios, String screenname,
			Connection con) throws Exception {

		CallableStatement stmt = null;
		boolean vieneCon = false;
		List<Destinatario> lista = null;
		List<Destinatario> listaNueva = new ArrayList<Destinatario>();

		try {
			lista = listaDestinatarios.getListaDestinatarios();
			_log.debug("comienzo a grabar mails");
			if (null == con) {
				con = ConnectionHelper.getConnectionMailing();
			} else {
				vieneCon = true;
			}
			con = ConnectionHelper.getConnectionMailing();
			String sqlMailing = "{? = call inserta_subscriber(?,?,?,?,?,?)}";

			stmt = con.prepareCall(sqlMailing);

			for (Destinatario dest : lista) {
				stmt.registerOutParameter(1, Types.INTEGER);
				stmt.setInt(2, dest.getIdDestinatario());
				stmt.setString(3, dest.getFirstname());
				stmt.setString(4, dest.getLastname());
				stmt.setString(5, dest.getTitle());
				stmt.setString(6, dest.getEmail());
				stmt.setString(7, screenname);
				stmt.executeUpdate();
				dest.setIdDestinatario(stmt.getInt(1));
				listaNueva.add(dest);
			}
			listaDestinatarios.setListaDestinatarios(listaNueva);

		} catch (SQLException e) {
			_log.error("Error al insertar subscribers", e);
			if (e.getSQLState().equals("23505")) {
				throw new EmailYaRegistradoException();
			} else {
				throw new SystemException(e);
			}

		} catch (Exception e) {
			_log.error("Error al insertar subscribers", e);
			throw new SystemException(e);
		} finally {
			if (!vieneCon) {
				ConnectionHelper.cerrar(con);
			}
			ConnectionHelper.cerrar(stmt);
		}
		return listaDestinatarios;
	}

	public int grabarListasMailing(ListaDestinatarios listaDestinatarios,
			String screenname, Connection con) throws Exception {

		CallableStatement stmtMailingList = null;
		boolean vieneCon = false;
		int id_mail = 0;
		try {
			_log.debug("comienzo a grabar mails");
			if (null == con) {
				con = ConnectionHelper.getConnectionMailing();
			} else {
				vieneCon = true;
			}
			con = ConnectionHelper.getConnectionMailing();
			String sqlMailing = "{? = call inserta_mailing_list(?,?,?,?)}";
			stmtMailingList = con.prepareCall(sqlMailing);
			stmtMailingList.registerOutParameter(1, Types.INTEGER);
			stmtMailingList.setInt(2,
					listaDestinatarios.getIdListaDestinatarios());
			stmtMailingList.setString(3, listaDestinatarios.getNombre());
			stmtMailingList.setString(4, listaDestinatarios.getObservaciones());
			stmtMailingList.setString(5, screenname);
			stmtMailingList.execute();
			id_mail = stmtMailingList.getInt(1);
		} catch (SQLException e) {
			_log.error("Error al buscar organismos", e);
			throw new SystemException(e);

		} catch (Exception e) {
			_log.error("Error al buscar organismos", e);
			throw new SystemException(e);
		} finally {
			if (!vieneCon) {
				ConnectionHelper.cerrar(con);
			}
			ConnectionHelper.cerrar(stmtMailingList);
		}
		return id_mail;
	}

	public List<ListaDestinatarios> getListasMailing(String nombre)
			throws Exception {

		CallableStatement stmt = null;
		Connection con = null;

		List<ListaDestinatarios> listas = null;
		try {
			con = ConnectionHelper.getConnectionMailing();
			String sql = "{call buscar_listas_mailing(?)}";
			stmt = con.prepareCall(sql.toString());

			if (null == nombre || nombre.trim().equals("")) {
				stmt.setNull(1, Types.VARCHAR);
			} else {
				stmt.setString(1, nombre);
			}

			listas = new ArrayList<ListaDestinatarios>();
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				ListaDestinatarios lista = new ListaDestinatarios();
				lista.setIdListaDestinatarios(rs.getInt("id_lista"));
				lista.setNombre(rs.getString("nombre"));
				lista.setObservaciones(rs.getString("observaciones"));
				lista.setAlta_user(rs.getString("alta_user"));
				listas.add(lista);
			}
		} catch (SQLException e) {
			_log.error("Error al buscar organismos", e);
			throw new SystemException(e);

		} catch (Exception e) {
			_log.error("Error al buscar organismos", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(con);
			ConnectionHelper.cerrar(stmt);
		}
		return listas;
	}

	public ListaDestinatarios getListaMailing(int id_lista_mailing)
			throws Exception {

		CallableStatement stmt = null;
		Connection con = null;
		ListaDestinatarios lista = new ListaDestinatarios();

		try {
			con = ConnectionHelper.getConnectionMailing();
			String sql = "{call buscar_lista_mailing(?)}";
			stmt = con.prepareCall(sql.toString());

			stmt.setInt(1, id_lista_mailing);

			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {

				lista.setIdListaDestinatarios(rs.getInt("id_lista"));
				lista.setNombre(rs.getString("nombre"));
				lista.setObservaciones(rs.getString("observaciones"));
				lista.setAlta_user(rs.getString("alta_user"));
				lista.setListaDestinatarios(getListaDestinatariosMailing(id_lista_mailing));

			}
		} catch (SQLException e) {
			_log.error("Error al buscar organismos", e);
			throw new SystemException(e);

		} catch (Exception e) {
			_log.error("Error al buscar organismos", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(con);
			ConnectionHelper.cerrar(stmt);
		}
		return lista;
	}

	public List<Destinatario> getListaDestinatariosMailing(int id_lista_mailing)
			throws Exception {

		CallableStatement stmt = null;
		Connection con = null;
		List<Destinatario> lista = new ArrayList<Destinatario>();

		try {
			con = ConnectionHelper.getConnectionMailing();
			String sql = "{call buscar_lista_subscriber(?)}";
			stmt = con.prepareCall(sql.toString());

			stmt.setInt(1, id_lista_mailing);

			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				Destinatario d = new Destinatario();
				d.setIdDestinatario(rs.getInt("id_destinatario"));
				d.setFirstname(rs.getString("nombre"));
				d.setLastname(rs.getString("apellido"));
				d.setTitle(rs.getString("tratamiento"));
				d.setEmail(rs.getString("email"));
				lista.add(d);
			}
		} catch (SQLException e) {
			_log.error("Error al buscar organismos", e);
			throw new SystemException(e);

		} catch (Exception e) {
			_log.error("Error al buscar organismos", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(con);
			ConnectionHelper.cerrar(stmt);
		}
		return lista;
	}

	public int borrarListasSubscriberEnBase(int id_subscriber, String username)
			throws Exception {

		CallableStatement stmt = null;
		Connection con = null;
		try {
			con = ConnectionHelper.getConnectionMailing();
			String sql = "{call borrar_listas_subscriber(?,?)}";
			stmt = con.prepareCall(sql.toString());

			stmt.setInt(1, id_subscriber);
			stmt.setString(2, username);
			stmt.executeQuery();

		} catch (SQLException e) {
			_log.error("Error al borrar listas", e);
			throw new SystemException(e);

		} catch (Exception e) {
			_log.error("Error al borrar listas", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(con);
			ConnectionHelper.cerrar(stmt);
		}
		return 0;
	}

	public int borrarListaEnBase(int id_mailing, String username)
			throws Exception {

		CallableStatement stmt = null;
		Connection con = null;
		try {
			con = ConnectionHelper.getConnectionMailing();
			String sql = "{call borrar_lista_destinatarios(?,?)}";
			stmt = con.prepareCall(sql.toString());

			stmt.setInt(1, id_mailing);
			stmt.setString(2, username);
			stmt.executeQuery();

		} catch (SQLException e) {
			_log.error("Error al borrar lista", e);
			throw new SystemException(e);

		} catch (Exception e) {
			_log.error("Error al borrar lista", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(con);
			ConnectionHelper.cerrar(stmt);
		}
		return 0;
	}
	
	public int borrarContenidosBoletin(int id_boletin, String username)
			throws Exception {

		CallableStatement stmt = null;
		Connection con = null;
		try {
			con = ConnectionHelper.getConnectionMailing();
			String sql = "{call borrar_contenidos_boletin(?,?)}";
			stmt = con.prepareCall(sql.toString());

			stmt.setInt(1, id_boletin);
			stmt.setString(2, username);
			stmt.executeQuery();

		} catch (SQLException e) {
			_log.error("Error al borrar contenidos", e);
			throw new SystemException(e);

		} catch (Exception e) {
			_log.error("Error al borrar contenidos", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(con);
			ConnectionHelper.cerrar(stmt);
		}
		return 0;
	}
	
	public int borrarListasBoletin(int id_boletin, String username)
			throws Exception {

		CallableStatement stmt = null;
		Connection con = null;
		try {
			con = ConnectionHelper.getConnectionMailing();
			String sql = "{call borrar_listas_boletin(?,?)}";
			stmt = con.prepareCall(sql.toString());

			stmt.setInt(1, id_boletin);
			stmt.setString(2, username);
			stmt.executeQuery();

		} catch (SQLException e) {
			_log.error("Error al borrar contenidos", e);
			throw new SystemException(e);

		} catch (Exception e) {
			_log.error("Error al borrar contenidos", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(con);
			ConnectionHelper.cerrar(stmt);
		}
		return 0;
	}

	public int actualizarListaEnBase(ListaDestinatarios lista, String username)
			throws Exception {

		CallableStatement stmt = null;
		Connection con = null;
		try {
			con = ConnectionHelper.getConnectionMailing();
			String sql = "{call actualizar_lista_destinatarios(?,?,?,?)}";
			stmt = con.prepareCall(sql.toString());

			stmt.setInt(1, lista.getIdListaDestinatarios());
			stmt.setString(2, lista.getNombre());
			stmt.setString(3, lista.getObservaciones());
			stmt.setString(4, username);
			stmt.executeQuery();

		} catch (SQLException e) {
			_log.error("Error al actualizar lista", e);
			throw new SystemException(e);

		} catch (Exception e) {
			_log.error("Error al actualizar lista", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(con);
			ConnectionHelper.cerrar(stmt);
		}
		return 0;
	}

	public List<Destinatario> getSubscribers(Destinatario destinatario)
			throws Exception {

		CallableStatement stmt = null;
		Connection con = null;
		List<Destinatario> lista = new ArrayList<Destinatario>();

		try {
			con = ConnectionHelper.getConnectionMailing();
			String sql = "{call buscar_subscribers(?,?,?,?)}";
			stmt = con.prepareCall(sql.toString());

			if (null != destinatario.getTitle()
					&& !"".equals(destinatario.getTitle().trim())) {
				stmt.setString(1, destinatario.getTitle());
			} else {
				stmt.setNull(1, Types.VARCHAR);
			}
			if (null != destinatario.getFirstname()
					&& !"".equals(destinatario.getFirstname().trim())) {
				stmt.setString(2, destinatario.getFirstname());
			} else {
				stmt.setNull(2, Types.VARCHAR);
			}

			if (null != destinatario.getLastname()
					&& !"".equals(destinatario.getLastname().trim())) {
				stmt.setString(3, destinatario.getLastname());
			} else {
				stmt.setNull(3, Types.VARCHAR);
			}
			if (null != destinatario.getEmail()
					&& !"".equals(destinatario.getEmail().trim())) {
				stmt.setString(4, destinatario.getEmail());
			} else {
				stmt.setNull(4, Types.VARCHAR);
			}

			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				Destinatario d = new Destinatario();
				d.setIdDestinatario(rs.getInt("id_destinatario"));
				d.setFirstname(rs.getString("nombre"));
				d.setLastname(rs.getString("apellido"));
				d.setTitle(rs.getString("tratamiento"));
				d.setEmail(rs.getString("email"));
				lista.add(d);
			}
		} catch (SQLException e) {
			_log.error("Error al buscar organismos", e);
			throw new SystemException(e);

		} catch (Exception e) {
			_log.error("Error al buscar organismos", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(con);
			ConnectionHelper.cerrar(stmt);
		}
		return lista;
	}

	public List<Boletin> getBoletines(Boletin boletin) throws Exception {

		CallableStatement stmt = null;
		Connection con = null;
		List<Boletin> boletines = new ArrayList<Boletin>();

		try {
			con = ConnectionHelper.getConnectionMailing();
			String sql = "{call buscar_boletines(?,?,?)}";
			stmt = con.prepareCall(sql.toString());

			if (null != boletin && boletin.getIdBoletin() > 0) {
				stmt.setInt(1, boletin.getIdBoletin());
			} else {
				stmt.setNull(1, Types.INTEGER);
			}
			if (null != boletin.getNombre()
					&& !"".equals(boletin.getNombre().trim())) {
				stmt.setString(2, boletin.getNombre());
			} else {
				stmt.setNull(2, Types.VARCHAR);
			}

			if (null != boletin.getAsunto()
					&& !"".equals(boletin.getAsunto().trim())) {
				stmt.setString(3, boletin.getAsunto());
			} else {
				stmt.setNull(3, Types.VARCHAR);
			}

			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				Boletin d = new Boletin();
				d.setIdBoletin(rs.getInt("id_boletin"));
				d.setNombre(rs.getString("nombre"));
				d.setAsunto(rs.getString("asunto"));
				d.setObservaciones(rs.getString("observaciones"));
				d.setSoloTexto(rs.getBoolean("solo_texto"));

				boletines.add(d);
			}
		} catch (SQLException e) {
			_log.error("Error al buscar boletines", e);
			throw new SystemException(e);

		} catch (Exception e) {
			_log.error("Error al buscar boletines", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(con);
			ConnectionHelper.cerrar(stmt);
		}
		return boletines;
	}

	public Boletin getBoletin(int id_boletin) throws Exception {

		CallableStatement stmt = null;
		Connection con = null;
		Boletin d = null;

		try {
			con = ConnectionHelper.getConnectionMailing();
			String sql = "{call buscar_boletin(?)}";
			stmt = con.prepareCall(sql.toString());

			stmt.setInt(1, id_boletin);

			ResultSet rs = stmt.executeQuery();
			d = new Boletin();
			while (rs.next()) {
				d.setIdBoletin(rs.getInt("id_boletin"));
				d.setNombre(rs.getString("nombre"));
				d.setAsunto(rs.getString("asunto"));
				d.setObservaciones(rs.getString("observaciones"));
				d.setSoloTexto(rs.getBoolean("solo_texto"));
				String listasPlain = rs.getString("listas");
				if (null != listasPlain) {
					d.setListas(listasPlain.split(","));
				}
				if (d.getIdBoletin() > 0) {
					d.setListaContenidos(getContenidosBoletin(d.getIdBoletin()));
				}
			}
		} catch (SQLException e) {
			_log.error("Error al buscar boletin", e);
			throw new SystemException(e);

		} catch (Exception e) {
			_log.error("Error al buscar boletin", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(con);
			ConnectionHelper.cerrar(stmt);
		}
		return d;
	}

	public Destinatario grabarSubscriber(Destinatario dest, String screenname,
			Connection con) throws Exception {

		CallableStatement stmt = null;
		boolean vieneCon = false;

		try {
			_log.debug("comienzo a grabar mails");
			if (null == con) {
				con = ConnectionHelper.getConnectionMailing();
			} else {
				vieneCon = true;
			}
			con = ConnectionHelper.getConnectionMailing();
			String sqlMailing = "{? = call inserta_subscriber(?,?,?,?,?,?,?)}";

			stmt = con.prepareCall(sqlMailing);

			stmt.registerOutParameter(1, Types.INTEGER);
			stmt.setInt(2, dest.getIdDestinatario());
			stmt.setString(3, dest.getFirstname());
			stmt.setString(4, dest.getLastname());
			stmt.setString(5, dest.getTitle());
			stmt.setString(6, dest.getEmail());
			stmt.setBoolean(7, dest.isCasillaPrueba());
			stmt.setString(8, screenname);
			stmt.executeUpdate();
			dest.setIdDestinatario(stmt.getInt(1));

			if (null != dest.getListas()
					&& !dest.getListas()[0].trim().equals("")) {
				for (String id_lista : dest.getListas()) {
					insertarSubscriberMailing(dest.getIdDestinatario(),
							Integer.parseInt(id_lista), screenname, con);
				}
			}

		} catch (SQLException e) {
			_log.error("Error al grabar destinatario", e);
			if (e.getSQLState().equals("23505")) {
				throw new EmailYaRegistradoException();
			} else {
				throw new SystemException(e);
			}

		} catch (Exception e) {
			_log.error("Error al grabar destinatario", e);
			throw new SystemException(e);
		} finally {
			if (!vieneCon) {
				ConnectionHelper.cerrar(con);
			}
			ConnectionHelper.cerrar(stmt);
		}
		return dest;
	}

	public int actualizarSubscriber(Destinatario dest, String username)
			throws Exception {

		CallableStatement stmt = null;
		Connection con = null;
		try {
			con = ConnectionHelper.getConnectionMailing();
			String sql = "{call actualizar_destinatario(?,?,?,?,?,?,?)}";
			stmt = con.prepareCall(sql.toString());

			stmt.setInt(1, dest.getIdDestinatario());
			stmt.setString(2, dest.getFirstname());
			stmt.setString(3, dest.getLastname());
			stmt.setString(4, dest.getTitle());
			stmt.setString(5, dest.getEmail());
			stmt.setBoolean(6, dest.isCasillaPrueba());
			stmt.setString(7, username);
			stmt.executeQuery();

			if (null != dest.getListas()
					&& !dest.getListas()[0].trim().equals("")) {
				borrarListasSubscriberEnBase(dest.getIdDestinatario(),
						username);
				for (String id_lista : dest.getListas()) {
					insertarSubscriberMailing(dest.getIdDestinatario(),
							Integer.parseInt(id_lista), username, con);
				}
			}

		} catch (SQLException e) {
			_log.error("Error al actualizar dest", e);
			throw new SystemException(e);

		} catch (Exception e) {
			_log.error("Error al actualizar dest", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(con);
			ConnectionHelper.cerrar(stmt);
		}
		return 0;
	}

	public Destinatario getSubscriber(int id_destinatario) throws Exception {

		CallableStatement stmt = null;
		Connection con = null;
		Destinatario d = new Destinatario();

		try {
			con = ConnectionHelper.getConnectionMailing();
			String sql = "{call buscar_subscriber(?)}";
			stmt = con.prepareCall(sql.toString());

			stmt.setInt(1, id_destinatario);

			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				d.setIdDestinatario(rs.getInt("id_destinatario"));
				d.setFirstname(rs.getString("nombre"));
				d.setLastname(rs.getString("apellido"));
				d.setTitle(rs.getString("tratamiento"));
				d.setEmail(rs.getString("email"));
				d.setCasillaPrueba(rs.getBoolean("casilla_prueba"));
				String listasPlain = rs.getString("listas");
				if (null != listasPlain) {
					d.setListas(listasPlain.split(","));
				}
			}
		} catch (SQLException e) {
			_log.error("Error al buscar organismos", e);
			throw new SystemException(e);

		} catch (Exception e) {
			_log.error("Error al buscar organismos", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(con);
			ConnectionHelper.cerrar(stmt);
		}
		return d;
	}

	public void borrarDestinatario(int id_destinatario, String username)
			throws Exception {

		CallableStatement stmt = null;
		Connection con = null;
		try {
			con = ConnectionHelper.getConnectionMailing();
			String sql = "{call borrar_destinatario(?,?)}";
			stmt = con.prepareCall(sql.toString());

			stmt.setInt(1, id_destinatario);
			stmt.setString(2, username);
			stmt.executeQuery();

		} catch (SQLException e) {
			_log.error("Error al borrar dest", e);
			throw new SystemException(e);

		} catch (Exception e) {
			_log.error("Error al borrar dest", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(con);
			ConnectionHelper.cerrar(stmt);
		}
	}

	public List<Contenido> getContenidosBoletin(int id_boletin)
			throws Exception {

		CallableStatement stmt = null;
		Connection con = null;
		List<Contenido> lista = new ArrayList<Contenido>();

		try {
			con = ConnectionHelper.getConnectionMailing();
			String sql = "{call buscar_contenido(?)}";
			stmt = con.prepareCall(sql.toString());

			stmt.setInt(1, id_boletin);

			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				Contenido contenido = new Contenido();
				contenido.setIdContenido(rs.getInt("id_contenido"));
				contenido.setSeccion(rs.getString("seccion"));
				contenido.setTitulo(rs.getString("titulo"));
				contenido.setContenido(rs.getString("contenido"));
				lista.add(contenido);
			}
		} catch (SQLException e) {
			_log.error("Error al buscar contenido", e);
			throw new SystemException(e);

		} catch (Exception e) {
			_log.error("Error al buscar contenido", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(con);
			ConnectionHelper.cerrar(stmt);
		}
		return lista;
	}

	public List<Destinatario> getDestinatariosFromListas(String[] listas)
			throws Exception {

		CallableStatement stmt = null;
		Connection con = null;
		List<Destinatario> lista = new  ArrayList<Destinatario>();

		try {
			con = ConnectionHelper.getConnectionMailing();
			String sql = "{call buscar_destinatarios_lista(?)}";
			stmt = con.prepareCall(sql.toString());
			if(null!=listas){
				for(String id: listas){
					if(null!=id && !id.trim().isEmpty()){
						stmt.setInt(1, Integer.parseInt(id));
						ResultSet rs = stmt.executeQuery();
						while (rs.next()) {
							Destinatario destinatario=new Destinatario();
							destinatario.setIdDestinatario(rs.getInt("id_destinatario"));
							destinatario.setFirstname(rs.getString("nombre"));
							destinatario.setLastname(rs.getString("apellido"));
							destinatario.setEmail(rs.getString("email"));
							destinatario.setTitle(rs.getString("tratamiento"));
							destinatario.setCasillaPrueba(rs.getBoolean("casilla_prueba"));
							lista.add(destinatario);
						}
					}
					
				}
			}
			
		} catch (SQLException e) {
			_log.error("Error al buscar dest", e);
			throw new SystemException(e);

		} catch (Exception e) {
			_log.error("Error al buscar dest", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(con);
			ConnectionHelper.cerrar(stmt);
		}
		return lista;
	}

}
