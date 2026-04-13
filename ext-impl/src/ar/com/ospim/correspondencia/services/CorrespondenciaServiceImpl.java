package ar.com.ospim.correspondencia.services;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import ar.com.ospim.correspondencia.ImposibleBorrarItemCorrespondenciaException;
import ar.com.ospim.correspondencia.beans.BusquedaBandejaCorreoFiltro;
import ar.com.ospim.correspondencia.beans.CabeceraCorrespondencia;
import ar.com.ospim.correspondencia.beans.ItemCorrespondencia;
import ar.com.ospim.correspondencia.beans.ItemCorrespondenciaTotal;
import ar.com.ospim.correspondencia.beans.Paquete;
import ar.com.ospim.correspondencia.beans.TipoRemitente;
import ar.com.ospim.util.ConnectionHelper;
import ar.com.ospim.util.DateUtils;
import ar.com.ospim.util.StringUtils;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.model.User;

public class CorrespondenciaServiceImpl {
	
	private static Log logger = LogFactoryUtil.getLog(CorrespondenciaServiceImpl.class);

	public static List<ItemCorrespondencia> buscarCorrespondencia(
			String edificio, Date fechaDesdeFinal,Date fechaHastaFinal, long numeroCorrespondencia,
			String tipoRegistro, long paquete, long seguim_paquete, String tipoEnvio,
			String tipoRemitente, String cuil, int inte, String idFarmacia,
			String otros, int idPrestador, String cuitEntidad,
			String sucursalEntidad, int idSeccional, String tipoCompro,
			String letraCompro, int sucu, String nroCompro,
			String importeTotal, String edificio_destino,
			String usuario_destino, String sector_destino, 
			String contenido, String oblea, String estado_item)
			throws SystemException {

		Connection con = null;
		CallableStatement stmt = null;
		List<ItemCorrespondencia> listaCorr = new ArrayList<ItemCorrespondencia>();
		
		try {
			String sql = "{call correo.buscar_correspondencia(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}"; //- 1 ?
			
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());

			stmt.setString(1, edificio);
			if (fechaDesdeFinal == null)
				stmt.setNull(2, Types.DATE);
			else
				stmt.setTimestamp(2, fechaDesdeFinal == null ? null
						: new java.sql.Timestamp(DateUtils.getMismoDia_00_00hs(
								fechaDesdeFinal).getTime()));
			stmt.setInt(3, (int) numeroCorrespondencia);
			stmt.setString(4, tipoRegistro);
			stmt.setInt(5, (int) paquete);
			stmt.setString(6, tipoEnvio);
			stmt.setString(7, tipoRemitente);
			stmt.setString(8, cuil);
			stmt.setInt(9, inte);
			stmt.setInt(10, idFarmacia==""?0:Integer.parseInt(idFarmacia) );
			stmt.setString(11, otros);
			stmt.setInt(12, (int) idPrestador);
			stmt.setString(13, cuitEntidad);
			stmt.setString(14, sucursalEntidad);
			stmt.setInt(15, idSeccional);
			stmt.setString(16, tipoCompro);
			stmt.setString(17, letraCompro);
			stmt.setInt(18, sucu);
			stmt.setString(19, nroCompro);
			stmt.setBigDecimal(20, importeTotal == "" ? null
					: new BigDecimal(importeTotal));

			if (fechaHastaFinal == null)
				stmt.setNull(21, Types.DATE);
			else
				stmt.setTimestamp(21, new java.sql.Timestamp(DateUtils
						.getMismoDia_23_59hs(fechaHastaFinal).getTime()));

			stmt.setString(22, edificio_destino);
			stmt.setString(23, usuario_destino.equals("null")?"":usuario_destino);
			stmt.setString(24, sector_destino.equals("null")?"":sector_destino);
			stmt.setString(25, contenido);
			stmt.setString(26, oblea);
			stmt.setString(27, estado_item);
			stmt.setInt(28, (int)seguim_paquete);
//			stmt.setInt(29, 0); // sin paginado 
			
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				ItemCorrespondencia corr = ItemCorrespondencia
						.getMappingItemCorrespondencia(rs, "ic_");

				listaCorr.add(corr);
			}

		} catch (Exception e) {
			logger.error("error al buscar corr", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}

		return listaCorr;
	}
	
	public static List<ItemCorrespondenciaTotal> buscarCorrespondencia(
			String edificio, Date fechaDesdeFinal,Date fechaHastaFinal, long numeroCorrespondencia,
			String tipoRegistro, long paquete, long seguim_paquete, String tipoEnvio,
			String tipoRemitente, String cuil, int inte, String idFarmacia,
			String otros, int idPrestador, String cuitEntidad,
			String sucursalEntidad, int idSeccional, String tipoCompro,
			String letraCompro, int sucu, String nroCompro,
			String importeTotal, String edificio_destino,
			String usuario_destino, String sector_destino, 
			String contenido, String oblea, String estado_item, int offset)
			throws SystemException {

		Connection con = null;
		CallableStatement stmt = null;
		List<ItemCorrespondenciaTotal> listaCorr = new ArrayList<ItemCorrespondenciaTotal>();
		
		try {
			String sql = "{call correo.buscar_correspondencia(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
			
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());

			stmt.setString(1, edificio);
			if (fechaDesdeFinal == null)
				stmt.setNull(2, Types.DATE);
			else
				stmt.setTimestamp(2, fechaDesdeFinal == null ? null
						: new java.sql.Timestamp(DateUtils.getMismoDia_00_00hs(
								fechaDesdeFinal).getTime()));
			stmt.setInt(3, (int) numeroCorrespondencia);
			stmt.setString(4, tipoRegistro);
			stmt.setInt(5, (int) paquete);
			stmt.setString(6, tipoEnvio);
			stmt.setString(7, tipoRemitente);
			stmt.setString(8, cuil);
			stmt.setInt(9, inte);
			stmt.setInt(10, idFarmacia==""?0:Integer.parseInt(idFarmacia) );
			stmt.setString(11, otros);
			stmt.setInt(12, (int) idPrestador);
			stmt.setString(13, cuitEntidad);
			stmt.setString(14, sucursalEntidad);
			stmt.setInt(15, idSeccional);
			stmt.setString(16, tipoCompro);
			stmt.setString(17, letraCompro);
			stmt.setInt(18, sucu);
			stmt.setString(19, nroCompro);
			stmt.setBigDecimal(20, importeTotal == "" ? null
					: new BigDecimal(importeTotal));

			if (fechaHastaFinal == null)
				stmt.setNull(21, Types.DATE);
			else
				stmt.setTimestamp(21, new java.sql.Timestamp(DateUtils
						.getMismoDia_23_59hs(fechaHastaFinal).getTime()));

			stmt.setString(22, edificio_destino);
			stmt.setString(23, usuario_destino.equals("null")?"":usuario_destino);
			stmt.setString(24, sector_destino.equals("null")?"":sector_destino);
			stmt.setString(25, contenido);
			stmt.setString(26, oblea);
			stmt.setString(27, estado_item);
			stmt.setInt(28, (int)seguim_paquete);
			stmt.setInt(29, offset);
			
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
//				ItemCorrespondencia corr = ItemCorrespondencia
//						.getMappingItemCorrespondencia(rs, "ic_");
				ItemCorrespondenciaTotal corr = ItemCorrespondenciaTotal
						.getMappingItemCorrespondencia(rs, "ic_");
				
				
				listaCorr.add(corr);
			}

		} catch (Exception e) {
			logger.error("error al buscar corr", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}

		return listaCorr;
	}

	public static List<ItemCorrespondencia> buscarPaquetes(String edificio,
			Date fechaDesdeFinal, long numeroCorrespondencia,
			String tipoRegistro, long paquete, String tipoEnvio,
			String tipoRemitente, String cuil, int inte, String idFarmacia,
			String otros, int idPrestador, String cuitEntidad,
			String sucursalEntidad, int idSeccional, String tipoCompro,
			String letraCompro, int sucu, String nroCompro,
			String importeTotal, String edificio_destino,
			String usuario_destino, String sector_destino, String contenido)
			throws SystemException {

		Connection con = null;
		CallableStatement stmt = null;
		List<ItemCorrespondencia> listaCorr = new ArrayList<ItemCorrespondencia>();
		try {
			String sql = "{call correo.buscar_paquetes(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			
			stmt.setString(1, edificio);
			if (fechaDesdeFinal == null)
				stmt.setNull(2, Types.DATE);
			else
				stmt.setTimestamp(2, fechaDesdeFinal == null ? null
						: new java.sql.Timestamp(DateUtils.getMismoDia_00_00hs(
								fechaDesdeFinal).getTime()));
			stmt.setInt(3, (int) numeroCorrespondencia);
			stmt.setString(4, tipoRegistro);
			stmt.setInt(5, (int) paquete);
			stmt.setString(6, tipoEnvio);
			stmt.setString(7, tipoRemitente);
			stmt.setString(8, cuil);
			stmt.setInt(9, inte);
			stmt.setInt(10, idFarmacia==""?0:Integer.parseInt(idFarmacia) );
			stmt.setString(11, otros);
			stmt.setInt(12, (int) idPrestador);
			stmt.setString(13, cuitEntidad);
			stmt.setString(14, sucursalEntidad);
			stmt.setInt(15, idSeccional);
			stmt.setString(16, tipoCompro);
			stmt.setString(17, letraCompro);
			stmt.setInt(18, sucu);
			stmt.setString(19, nroCompro);
			stmt.setBigDecimal(20, importeTotal == null ? null
					: new BigDecimal(importeTotal));

			if (fechaDesdeFinal == null)
				stmt.setNull(21, Types.DATE);
			else
				stmt.setTimestamp(21, new java.sql.Timestamp(DateUtils
						.getMismoDia_23_59hs(fechaDesdeFinal).getTime()));

			stmt.setString(22, edificio_destino);
			stmt.setString(23, usuario_destino);
			stmt.setString(24, sector_destino);
			stmt.setString(25, contenido);

			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				ItemCorrespondencia corr = ItemCorrespondencia
						.getMappingItemCorrespondencia(rs, "ic_");
				Paquete paq = new Paquete();
				paq.setId(Long.valueOf(String.valueOf(rs.getInt("lp_id_paquete"))));
				paq.setEstado(rs.getString("paq_estado"));
				paq.setDescripcion(rs.getString("paq_descripcion"));
				corr.setPaquete(paq);

				listaCorr.add(corr);
			}

		} catch (Exception e) {
			logger.error("error al buscar corr", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}

		return listaCorr;
	}
	
	public static List<ItemCorrespondenciaTotal> buscarPaquetesPagina(String edificio,
			Date fechaDesdeFinal, Date fechaDesempDesdeFinal, Date fechaDesempHastaFinal, long numeroCorrespondencia,
			String tipoRegistro, long paquete, String tipoEnvio,
			String tipoRemitente, String cuil, int inte, String idFarmacia,
			String otros, int idPrestador, String cuitEntidad,
			String sucursalEntidad, int idSeccional, String tipoCompro,
			String letraCompro, int sucu, String nroCompro,
			String importeTotal, String edificio_destino,
			String usuario_destino, String sector_destino, String contenido, int offset)
			throws SystemException {

		Connection con = null;
		CallableStatement stmt = null;
		List<ItemCorrespondenciaTotal> listaCorr = new ArrayList<ItemCorrespondenciaTotal>();
		try {
			String sql = "{call correo.buscar_paquetes(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			
			stmt.setString(1, edificio);
			if (fechaDesdeFinal == null){
				stmt.setNull(2, Types.DATE);
			}else{
				stmt.setTimestamp(2, fechaDesdeFinal == null ? null
						: new java.sql.Timestamp(DateUtils.getMismoDia_00_00hs(
								fechaDesdeFinal).getTime()));
			}
			if (fechaDesempDesdeFinal == null){
				stmt.setNull(3, Types.DATE);
			}else{
				stmt.setDate(3, new java.sql.Date(fechaDesempDesdeFinal.getTime()));
			}
			if (fechaDesempHastaFinal == null){
				stmt.setNull(4, Types.DATE);
			}else{
				stmt.setDate(4, new java.sql.Date(fechaDesempHastaFinal.getTime()));
			}
			stmt.setInt(5, (int) numeroCorrespondencia);
			stmt.setString(6, tipoRegistro);
			stmt.setInt(7, (int) paquete);
			stmt.setString(8, tipoEnvio);
			stmt.setString(9, tipoRemitente);
			stmt.setString(10, cuil);
			stmt.setInt(11, inte);
			stmt.setInt(12, idFarmacia==""?0:Integer.parseInt(idFarmacia) );
			stmt.setString(13, otros);
			stmt.setInt(14, (int) idPrestador);
			stmt.setString(15, cuitEntidad);
			stmt.setString(16, sucursalEntidad);
			stmt.setInt(17, idSeccional);
			stmt.setString(18, tipoCompro);
			stmt.setString(19, letraCompro);
			stmt.setInt(20, sucu);
			stmt.setString(21, nroCompro);
			stmt.setBigDecimal(22, importeTotal == null ? null
					: new BigDecimal(importeTotal));

			if (fechaDesdeFinal == null)
				stmt.setNull(23, Types.DATE);
			else
				stmt.setTimestamp(23, new java.sql.Timestamp(DateUtils
						.getMismoDia_23_59hs(fechaDesdeFinal).getTime()));

			stmt.setString(24, edificio_destino);
			stmt.setString(25, usuario_destino);
			stmt.setString(26, sector_destino);
			stmt.setString(27, contenido);
			stmt.setInt(28, offset);
			
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				ItemCorrespondenciaTotal corr = ItemCorrespondenciaTotal
						.getMappingItemCorrespondencia(rs, "ic_");
				Paquete paq = new Paquete();
				paq.setId(Long.valueOf(String.valueOf(rs.getInt("lp_id_paquete"))));
				paq.setEstado(rs.getString("lp_estado"));
				paq.setDescripcion(rs.getString("lp_descripcion"));
				corr.setPaquete(paq);
				listaCorr.add(corr);
			}

		} catch (Exception e) {
			logger.error("error al buscar paquetes corr", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}

		return listaCorr;
	}

	public void borrarItemCorrespondencia(int id, String screenName)
			throws SQLException, ImposibleBorrarItemCorrespondenciaException {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call correo.borra_item_correspondencia(?, ?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, id);
			stmt.setString(2, screenName);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				if (rs.getInt(1) == 0) {
					throw new ImposibleBorrarItemCorrespondenciaException();
				}
			}
		} catch (ImposibleBorrarItemCorrespondenciaException e) {
			logger.error("Error al borrar item correspondencia", e);
			throw e;
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
	}

	public int savePaquete(ArrayList<ItemCorrespondencia> list, String descripcion, String screenName) throws SystemException {

		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call correo.insertar_paquete(?,?)}";
			
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, descripcion);
			stmt.setString(2, screenName);
			
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				return rs.getInt(1);
			}
		} catch (SQLException e) {
			logger.error("Error al insertar paquete", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return -1;
	}

	public void saveListaItemsParaPaquete(ArrayList<ItemCorrespondencia> list,
			int id, String screenName) throws SystemException {

		String ids_concatenados="";
		
		for (ItemCorrespondencia ic : list) {
			ids_concatenados = ids_concatenados + (String.valueOf(ic.getId())+","); // "," es el separador
		}
		
		Connection con = null;
		CallableStatement stmt = null;
		CallableStatement stmt2 = null;
		try {
			String sql = "{call correo.insertar_lista_items_paquete(?, ?, ?)}";
			String sql2 = "{call correo.update_estado_items(?, ?, ?, ?)}";
			logger.debug("obteniendo conexion");
			con = ConnectionHelper.getConnection();
			con.setAutoCommit(false);
			for (ItemCorrespondencia r : list) {
				stmt = con.prepareCall(sql.toString());
				stmt.setInt(1, id);
				stmt.setInt(2, (int) r.getId());
				stmt.setString(3, screenName);
				stmt.executeUpdate();
			}
			stmt2 = con.prepareCall(sql2.toString());
			stmt2.setString(1, ids_concatenados );
			stmt2.setString(2, ",");
			stmt2.setString(3, "ENVIADO");
			stmt2.setString(4, screenName);
			stmt2.executeUpdate();
			
			con.commit();
		} catch (SQLException e) {
			logger.error("Error al insertar item de lista para paquete", e);
			try {
				con.rollback();
			} catch (SQLException e1) {
				throw new SystemException(e1);
			}
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt);
			//ConnectionHelper.cerrar(stmt, con);
			ConnectionHelper.cerrar(stmt2, con);
		}

	}

	public static CabeceraCorrespondencia buscarCabeceraCorrespondenciaPorId(int idCorrespondencia)
			throws SystemException {

		Connection con = null;
		CallableStatement stmt = null;
		CabeceraCorrespondencia cab = new CabeceraCorrespondencia();
		try {
			String sql = "{call correo.buscar_cabecera_corr_por_id(?)}";

			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());

			stmt.setInt(1, idCorrespondencia);

			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				cab = CabeceraCorrespondencia.getMapping(rs, "cab_");
			} else {
				return null;
			}
		} catch (Exception e) {
			logger.error("error al buscar cabecera corr", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}

		return cab;
	}


	public static List<ItemCorrespondencia> buscarItemsPorIdCorrespondencia(
			int id_correspondencia) throws SystemException {

		Connection con = null;
		CallableStatement stmt = null;
		List<ItemCorrespondencia> listaCorr = new ArrayList<ItemCorrespondencia>();
		try {
			String sql = "{ call correo.buscar_items_correspondencia_por_id(?) }";
							
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());

			stmt.setInt(1, id_correspondencia);

			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				ItemCorrespondencia corr = ItemCorrespondencia
						.getMappingItemCorrespondencia(rs, "ic_");
				listaCorr.add(corr);
			}

		} catch (Exception e) {
			logger.error("error al buscar corr", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}

		return listaCorr;
	}

	public static ItemCorrespondencia buscarItemCorrespondenciaPorId(
			int id) throws SystemException {

		Connection con = null;
		CallableStatement stmt = null;
		ItemCorrespondencia itemCorr = null;
		try {
			String sql = "{ call correo.buscar_item_correspondencia_por_id(?) }";
							
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());

			stmt.setInt(1, id);

			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				itemCorr = ItemCorrespondencia
						.getMappingItemCorrespondencia(rs, "ic_");
			}

		} catch (Exception e) {
			logger.error("error al buscar itemCorr", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}

		return itemCorr;
	}

	
	public long insertaCabeceraCorrespondencia(String edificio, Date fecha,
			long numeroCorrespondencia, String tipoRegistro, String tipoEnvio,
			String oblea, String screenName) throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		int id_contrato = 0;
		try {
			String sql = "{call correo.inserta_cabecera(?,?,?,?,?,?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());

			stmt.setString(1, edificio);
			stmt.setDate(2, new java.sql.Date(fecha.getTime()));
			stmt.setString(3, tipoRegistro);
			stmt.setString(4, tipoEnvio);
			stmt.setString(5, oblea);
			stmt.setString(6, screenName);
			
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				id_contrato = rs.getInt(1);
			}
		} catch (SQLException e) {
			logger.error("Error al insertar cabecera", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return id_contrato;
	}

	public void actualizaCabeceraCorrespondencia(String edificio, Date fecha,
			long numeroCorrespondencia, String tipoRegistro, String tipoEnvio,
			String oblea, String screenName) throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call correo.actualiza_cabecera(?,?,?,?,?,?,?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());

			stmt.setString(1, edificio);
			stmt.setDate(2, new java.sql.Date(fecha.getTime()));
			stmt.setString(3, tipoRegistro);
			stmt.setString(4, tipoEnvio);
			stmt.setString(5, screenName);
			stmt.setInt(6, (int) numeroCorrespondencia);
			stmt.setString(7, oblea);
			
			stmt.executeQuery();

		} catch (SQLException e) {
			logger.error("Error al actualizar cabecera corr", e);
			throw new SystemException(e);
		} catch (Exception e) {
			logger.error("Error al actualizar cabecera corr", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
	}
	public long insertaItemCorrespondencia(ItemCorrespondencia item, User user) throws Exception {

		Connection con = null;
		CallableStatement stmt = null;
		String sql=null;
		long idItemCorr = 0;
		
		try {
			con = ConnectionHelper.getConnection();
			
			sql = "{call correo.inserta_item_correspondencia(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
			
			stmt = con.prepareCall(sql.toString());

			stmt.setInt(1, (int) item.getId_correspondencia()); // idCorrespondencia
			stmt.setString(2, item.getEntradaSalida());         // entradaSalida 
			stmt.setString(3, item.getTipoRemitenteDestinatario()); // tipoRemitenteDestinatario

			stmt.setString(4, item.getEmpresa_remite());        //  empresaRemite
			stmt.setString(5, item.getSector_remite());         //  sectorRemite
			stmt.setString(6, item.getUsuario_remite());        //  usuarioRemite
			stmt.setString(7, item.getContenido());             //  contenido

			stmt.setString(8, item.getEstado());                // estado
			stmt.setString(9, item.getAfiliado().getCuil_titular()); //cuilTitular
			stmt.setInt(10, item.getAfiliado().getInte());      // inte   

			stmt.setInt(11, item.getFarmacia().getId_farmacia());    // codigo
			stmt.setString(12, item.getOtro());                 // otro
			
			stmt.setInt(13, item.getPrestador().getId_prestador()); // id
			stmt.setString(14, item.getProveedor().getCuit());      // cuit
			stmt.setString(15, item.getProveedor().getSucursal());  // sucursal
			stmt.setInt(16, item.getCompro_sucu());  // item.getId_punto_venta() id_pto_venta

			stmt.setString(17, item.getCompro_tipo());              // comproTipo
			stmt.setString(18, item.getCompro_nro());               // comproNro
			stmt.setString(19, item.getCuit());                     //cuit
			stmt.setString(20, item.getCompro_letra());             // comproLetra
			stmt.setInt(21, item.getCompro_sucu());					// comproSucu

			stmt.setBigDecimal(22, item.getImporte());				// importe
			
			if(item.getCompro_periodo()!=null){
				stmt.setDate(23, new java.sql.Date(item.getCompro_periodo().getTime())); // periodo comprob audit farmacia
			}else{
				stmt.setNull(23, Types.DATE);
			}
			stmt.setInt(24, item.getSeccional().getIdSeccional());  // idSeccional
			stmt.setString(25, user.getScreenName());
			stmt.setDate(26, new java.sql.Date(item.getFecha_emision().getTime())); //  fechaEmision
			stmt.setDate(27, new java.sql.Date(item.getFecha_vencimiento().getTime())); // fechaVencimiento
			stmt.setString(28, item.getEdificio()); 				// edificio
			stmt.setString(29, item.getSector());  					// sector
			stmt.setString(30, item.getUsuario());  				// usuario
			
			String grupoRecepcionista="";
			try{
				grupoRecepcionista = String.valueOf(user.getUserGroups().get(0).getUserGroupId());
			}catch(Exception e){
				grupoRecepcionista="";
			}
			stmt.setString(31, grupoRecepcionista);
			if(!StringUtils.checkEmpty(item.getSeguimientoPaquete())){
				stmt.setInt(32, Integer.parseInt(item.getSeguimientoPaquete()));
			}else{
				stmt.setNull(32, Types.INTEGER);
			}
			if(item.getIdCRMContacto()!=null){
				stmt.setInt(33, item.getIdCRMContacto());
			}else{
				stmt.setNull(33, Types.INTEGER);
			}
		
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				idItemCorr = rs.getLong(1);
			}
			
		} catch (Exception e) {
			logger.error("Error al cargar item de correspondencia", e);
			throw e;
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		
		return idItemCorr;
	}
	
	@Deprecated
	public void insertaItemsCorrespondencia(ArrayList<ItemCorrespondencia> items, User user) throws Exception {

		Connection con = null;
		CallableStatement stmt = null;
		String sql=null;

		try {
			con = ConnectionHelper.getConnection();
			con.setAutoCommit(false);
			
			for (Iterator<ItemCorrespondencia> iterator = items.iterator(); iterator.hasNext();) {
				ItemCorrespondencia item = iterator.next();

				sql = "{call correo.inserta_item_correspondencia(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
				
				stmt = con.prepareCall(sql.toString());
	
				stmt.setInt(1, (int) item.getId_correspondencia()); // idCorrespondencia
				stmt.setString(2, item.getEntradaSalida());         // entradaSalida 
				stmt.setString(3, item.getTipoRemitenteDestinatario()); // tipoRemitenteDestinatario
	
				stmt.setString(4, item.getEmpresa_remite());        //  empresaRemite
				stmt.setString(5, item.getSector_remite());         //  sectorRemite
				stmt.setString(6, item.getUsuario_remite());        //  usuarioRemite
				stmt.setString(7, item.getContenido());             //  contenido
	
				stmt.setString(8, item.getEstado());                // estado
				stmt.setString(9, item.getAfiliado().getCuil_titular()); //cuilTitular
				stmt.setInt(10, item.getAfiliado().getInte());      // inte   
	
				stmt.setInt(11, item.getFarmacia().getId_farmacia());    // codigo
				stmt.setString(12, item.getOtro());                 // otro
				
				stmt.setInt(13, item.getPrestador().getId_prestador()); // id
				stmt.setString(14, item.getProveedor().getCuit());      // cuit
				stmt.setString(15, item.getProveedor().getSucursal());  // sucursal
				stmt.setInt(16, item.getCompro_sucu());  // item.getId_punto_venta() id_pto_venta
	
				stmt.setString(17, item.getCompro_tipo());              // comproTipo
				stmt.setString(18, item.getCompro_nro());               // comproNro
				stmt.setString(19, item.getCuit());                     //cuit
				stmt.setString(20, item.getCompro_letra());             // comproLetra
				stmt.setInt(21, item.getCompro_sucu());					// comproSucu
	
				stmt.setBigDecimal(22, item.getImporte());				// importe 
				stmt.setInt(23, item.getSeccional().getIdSeccional());  // idSeccional
				stmt.setString(24, user.getScreenName());
				stmt.setDate(25, new java.sql.Date(item.getFecha_emision().getTime())); //  fechaEmision
				stmt.setDate(26, new java.sql.Date(item.getFecha_vencimiento().getTime())); // fechaVencimiento
				stmt.setString(27, item.getEdificio()); 				// edificio
				stmt.setString(28, item.getSector());  					// sector
				stmt.setString(29, item.getUsuario());  				// usuario
				
				String grupoRecepcionista="";
				try{
					grupoRecepcionista = String.valueOf(user.getUserGroups().get(0).getUserGroupId());
				}catch(Exception e){
					grupoRecepcionista="";
				}
				stmt.setString(30, grupoRecepcionista);
			
				stmt.executeUpdate();
		
			}	
			con.commit();
			
		} catch (Exception e) {
			ConnectionHelper.rollback(con);
			logger.error("Error al cargar items de correspondencia", e);
			throw e;
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		
	}

	public void borraCorrespondenciaDetalleEntry(int id, String screenName)
			throws SystemException {

		Connection con = null;
		CallableStatement stmt = null;
		try {
			con = ConnectionHelper.getConnection();
			String sql = "{call correo.borra_item_correspondencia(?,?)}";
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, id);
			stmt.setString(2, screenName);

			stmt.executeUpdate();
		} catch (SQLException e) {
			logger.error("Error al borrar item correspondencia", e);
			throw new SystemException(e);
		} catch (Exception e) {
			logger.error("Error al borrar item correspondencia", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
	}
	
	public List<ItemCorrespondenciaTotal> bandejaEntradaPagina(User usuarioCorrespondencia, boolean esRecepcionista, 
				BusquedaBandejaCorreoFiltro filtro, boolean perteneceLiquidaciones)
			throws SystemException {
		
		Connection con = null;
		CallableStatement stmt = null;
		List<ItemCorrespondenciaTotal> listaCorr = new ArrayList<ItemCorrespondenciaTotal>();
		String usuario="";
		String organizacion="", sector="";
		
		try{
			usuario = usuarioCorrespondencia.getScreenName();
			organizacion = String.valueOf(usuarioCorrespondencia.getOrganizations().get(0).getOrganizationId()) ;
			sector = String.valueOf(usuarioCorrespondencia.getUserGroups().get(0).getUserGroupId());
			
		}catch (Exception e) {
			logger.error("Revisar el usuario liferay " + usuario );
		}
		
		logger.debug("Usuario buscando su bandeja de entrada ");
		logger.debug("Usuario: " + usuario );
		logger.debug("Organizacion: " + organizacion );
		
		try {
			String sql = "{call correo.buscar_bandeja_entrada_por_user(?, ?, ?, ?, ?, ?, ?, ?, ?, ? )}" ;
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());

			stmt.setString(1, usuarioCorrespondencia == null ? "" : usuario);
			stmt.setString(2, usuarioCorrespondencia == null ? "" : organizacion);
			stmt.setString(3, usuarioCorrespondencia == null ? "" : sector);
			stmt.setBoolean(4, esRecepcionista);
			stmt.setDate(5, new java.sql.Date(filtro.getFechaDesde().getTime()));
			stmt.setDate(6, new java.sql.Date(filtro.getFechaHasta().getTime()));
			stmt.setString(7, filtro.getEstado());
			if(StringUtils.checkEmpty(filtro.getCuit())){
				stmt.setString(8, null);
			}else{
				stmt.setString(8, filtro.getCuit());
			}
			stmt.setBoolean(9, perteneceLiquidaciones);
			stmt.setInt(10, filtro.getPagina());
			
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				ItemCorrespondenciaTotal corr = ItemCorrespondenciaTotal
						.getMappingItemCorrespondencia(rs, "ic_");
				listaCorr.add(corr);
			}

		} catch (Exception e) {
			logger.error("error al buscar inbox corr", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}

		return listaCorr;
	}

	public void actualizarEstadoItems(Connection connectioParameter, ArrayList<ItemCorrespondencia> list,
			String estado, String screenName) throws SystemException {

		String ids_concatenados="";
		
		for (ItemCorrespondencia ic : list) {
			ids_concatenados = ids_concatenados + (String.valueOf(ic.getId())+","); // "," es el separador
		}
		
		Connection con = null;
		CallableStatement stmt = null;

		try {
			String sql = "{call correo.update_estado_items(?, ?, ?, ?)}";
			logger.debug("obteniendo conexion");
			
			if(connectioParameter==null){
				con = ConnectionHelper.getConnection();
			}else{
				con = connectioParameter;
			}
			con.setAutoCommit(false);

			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, ids_concatenados );
			stmt.setString(2, ",");
			stmt.setString(3, estado);
			stmt.setString(4, screenName);
			stmt.executeUpdate();
			
			con.commit();
		} catch (SQLException e) {
			logger.error("Error al actualizar items de correspondencia", e);
			try {
				con.rollback();
			} catch (SQLException e1) {
				throw new SystemException(e1);
			}
			throw new SystemException(e);
		} finally {
			if(connectioParameter == null){
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}

	}
	
	public void actualiza_estado_paquete(Connection connectioParameter, int id, String estado, String screenName) throws SystemException {
		
		Connection con = null;
		CallableStatement stmt = null;
		try {
			if(connectioParameter==null){
				con = ConnectionHelper.getConnection();
			}else{
				con = connectioParameter;
			}

			String sql = "{call correo.actualiza_estado_paquete(?, ? ,?)}";
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, id);
			stmt.setString(2, estado);
			stmt.setString(3, screenName);

			stmt.executeUpdate();
		} catch (SQLException e) {
			logger.error("Error al actualizar estado paquete", e);
			throw new SystemException(e);
		} catch (Exception e) {
			logger.error("Error al actualizar estado paquete", e);
			throw new SystemException(e);
		} finally {
			if(connectioParameter==null){
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
	}
	
	public void borrarItemDelPaquete(Connection connectioParameter, int id, String screenName) throws SystemException {

		Connection con = null;
		CallableStatement stmt = null;
		try {
			if(connectioParameter==null){
				con = ConnectionHelper.getConnection();
			}else{
				con = connectioParameter;
			}
			String sql = "{call correo.borrar_item_del_paquete(?,?)}";
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, id);
			stmt.setString(2, screenName);

			stmt.executeUpdate();
		} catch (SQLException e) {
			logger.error("Error al borrar item del paquete", e);
			throw new SystemException(e);
		} catch (Exception e) {
			logger.error("Error al borrar item del paquete", e);
			throw new SystemException(e);
		} finally {
			if(connectioParameter==null){
				ConnectionHelper.cerrar(stmt, con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}
	}
	
	public void marcarRecibido(int id, String marca, Integer idContacto, String comentariosCierreCRM, boolean esCierreContacto,
			String screenName, String usuarioSector) throws SystemException {

		Connection con = null;
		CallableStatement stmt = null;
		try {
			con = ConnectionHelper.getConnection();
			String sql = "{call correo.marcar_recibido(?,?,?,?,?,?,?)}";
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, id);
			stmt.setString(2, marca); // para saber si actualizo un solo item o un paq (Tittarelli)
			stmt.setString(3, screenName);
			stmt.setString(4, usuarioSector); //para las actualizaciones de id_contacto derivados
			if(idContacto==null || idContacto == 0){
				stmt.setNull(5, Types.INTEGER);
			}else{
				stmt.setInt(5, idContacto);
			}
			stmt.setString(6, comentariosCierreCRM);
			stmt.setBoolean(7, esCierreContacto);
			stmt.executeUpdate();
		} catch (SQLException e) {
			logger.error("Error al marcar recibido item correspondencia", e);
			throw new SystemException(e);
		} catch (Exception e) {
			logger.error("Error al marcar recibido item correspondencia", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
	}
	
	public void update_item(int id, String tipo_remitente_destinatario_p, String edificio_p, String sector_p, String usuario_p,
			  String empresa_remite, String sector_remite, String usuario_remite,
			  String contenido_p, String cuil_titular_p, int inte_p, int codigo_farmacia_p,
			  String descripcion_otro_p, int id_prestador_p, String cuit_proveedor_p, String sucu_proveedor_p,
			  int id_punto_venta_p, String compro_tipo_p, String compro_nro_p, String cuit_p, String compro_letra_p,
			  int compro_sucu_p, java.util.Date compro_periodo_p, BigDecimal importe_p, Date fecha_emision_p, 
			  Date fecha_vencimiento_p, int id_seccional_p, String seguim_paquete_p, String screenName) throws SystemException {

		Connection con = null;
		CallableStatement stmt = null;
		
		try {		
			con = ConnectionHelper.getConnection();
			String sql = "{call correo.update_item(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
			
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, id);
			stmt.setString(2, tipo_remitente_destinatario_p);
			stmt.setString(3, edificio_p);
			stmt.setString(4, sector_p);
			stmt.setString(5, usuario_p);
			stmt.setString(6, contenido_p);
			stmt.setString(7, cuil_titular_p);
			stmt.setInt(8, inte_p);
			stmt.setInt(9, codigo_farmacia_p);
			stmt.setString(10, descripcion_otro_p);
			stmt.setInt(11, id_prestador_p);
			stmt.setString(12, cuit_proveedor_p);
			stmt.setString(13, sucu_proveedor_p);
			stmt.setShort(14, Short.valueOf(String.valueOf(compro_sucu_p)));
			stmt.setString(15, compro_tipo_p);
			stmt.setString(16, compro_nro_p);
			stmt.setString(17, cuit_p);
			stmt.setString(18, compro_letra_p);
			stmt.setInt(19, compro_sucu_p);
			if(compro_periodo_p != null){
				stmt.setDate(20, new java.sql.Date(compro_periodo_p.getTime()) );
			}else{
				stmt.setNull(20, Types.DATE);
			}
			stmt.setBigDecimal(21, importe_p);
			if (fecha_emision_p == null)
				stmt.setNull(22, Types.DATE);
			else
				stmt.setTimestamp(22, new java.sql.Timestamp(DateUtils
						.getMismoDia_00_00hs(fecha_emision_p).getTime()));
			if (fecha_vencimiento_p == null)
				stmt.setNull(23, Types.DATE);
			else
				stmt.setTimestamp(23, new java.sql.Timestamp(DateUtils
						.getMismoDia_00_00hs(fecha_vencimiento_p).getTime()));
			stmt.setInt(24, id_seccional_p);
			stmt.setString(25, screenName);
			stmt.setString(26, empresa_remite);
			stmt.setString(27, sector_remite);
			stmt.setString(28, usuario_remite);
			if(!StringUtils.checkEmpty(seguim_paquete_p)){
				int seg_paq = Integer.parseInt(seguim_paquete_p);
				stmt.setInt(29, seg_paq);
			}else{
				stmt.setNull(29, Types.INTEGER);
			}
			
			stmt.executeUpdate();
		} catch (SQLException e) {
			logger.error("Error al actualizar item correspondencia", e);
			throw new SystemException(e);
		} catch (Exception e) {
			logger.error("Error al actualizar item correspondencia", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
	}
	
	public void actualiza_historico(int id) throws SystemException {
		
		Connection con = null;
		CallableStatement stmt = null;
		try {
			con = ConnectionHelper.getConnection();
			String sql = "{call correo.actualiza_cabecera_item_correspondencia_historico(?) }";
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, id);
			
			stmt.executeUpdate();
		} catch (SQLException e) {
			logger.error("Error al actualizar historico cab-item correspondencia", e);
			throw new SystemException(e);
		} catch (Exception e) {
			logger.error("Error al actualizar historico cab-item correspondencia", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
	}
	
	public static boolean buscarFCPrestadorDuplicado(String cuitPrestador, int idPtoVenta, 
			String tipoComprobante, String letraComprobante, String nroComprobante, int sucuComprobante)
			throws SystemException {

		Connection con = null;
		CallableStatement stmt = null;
		int resp = 0;
		
		try {
			String sql = "{call correo.buscar_fc_prestador_duplicado(?, ?, ?, ?, ?, ?)}";

			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());

			stmt.setString(1, cuitPrestador);
			stmt.setInt(2, idPtoVenta);
			stmt.setString(3, tipoComprobante);
			stmt.setString(4, letraComprobante);
			stmt.setString(5, nroComprobante);
			stmt.setInt(6, sucuComprobante);

			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				resp = rs.getInt(1);
			} 
		} catch (Exception e) {
			logger.error("error al validar FC prestador duplicada", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}

		return resp == 1;
	}
	
	public static List<TipoRemitente> getTiposRemitentes() throws SystemException {
		
		Connection con = null;
		CallableStatement stmt = null;
		List<TipoRemitente> tiposRem = new ArrayList<TipoRemitente>();
		TipoRemitente tr = null;
		
		try {
			String sql = "{call correo.buscar_tipos_remitente() }";

			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());

			ResultSet rs = stmt.executeQuery();
			
			while(rs.next()){
				tr = TipoRemitente.getMapping("tr_", rs);
				tiposRem.add(tr);
			} 
		} catch (Exception e) {
			logger.error("error al buscar Tipos Remitente de Correspondencia", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}

		return tiposRem;
	}
}
