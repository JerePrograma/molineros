package ar.com.ospim.afiliados.services;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import ar.com.ospim.afiliados.beans.AfiPlan;
import ar.com.ospim.afiliados.beans.AfiSuspencionCobertura;
import ar.com.ospim.afiliados.beans.AfiTercerizadoraServicio;
import ar.com.ospim.afiliados.beans.Afiliado;
import ar.com.ospim.afiliados.beans.DetalleFechasSuper;
import ar.com.ospim.autorizaciones.beans.BusquedaConsultasIGSFiltro;
import ar.com.ospim.autorizaciones.beans.ConsultaIGSTotal;
import ar.com.ospim.global.beans.Domicilio;
import ar.com.ospim.global.beans.Localidad;
import ar.com.ospim.global.beans.Plan;
import ar.com.ospim.global.beans.Provincia;
import ar.com.ospim.procesaArchivos.beans.opcionesss.DetalleOpcionesSS;
import ar.com.ospim.util.ConnectionHelper;
import ar.com.ospim.webservice.beans.AfiliacionPrevencion;
import ar.com.uoma.beans.Incidente;

/**
 * <a href="BusquedaAfiliadoServiceImpl.java.html"><b><i>View Source</i></b></a>
 * 
 * @author Federico Brachi
 * 
 */
public class BusquedaAfiliadoServiceImpl {

	private static Log _log = LogFactoryUtil.getLog(BusquedaAfiliadoServiceImpl.class);

	public List<Afiliado> getBusquedaAfiliados(String cuil, String inte,
			String tdoc, String nroDoc, int seccional_id, String apellido,
			String nombre) {
		Connection con = null;
		CallableStatement stmt = null;
		List<Afiliado> listaAfiliados = null;
		try {
			String sql = "{call buscar_afiliados(?,?,?,?,?,?,?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, cuil);
			if (null != inte) {
				stmt.setInt(2, Integer.parseInt(inte));
			} else {
				stmt.setNull(2, Types.INTEGER);
			}
			stmt.setString(3, tdoc);
			stmt.setString(4, nroDoc);
			if (seccional_id == 0) {
				stmt.setNull(5, Types.INTEGER);
			} else {
				stmt.setInt(5, seccional_id);
			}
			stmt.setString(6, apellido);
			stmt.setString(7, nombre);
			ResultSet rs = stmt.executeQuery();
			listaAfiliados = new ArrayList<Afiliado>();
			while (rs.next()) {
				Afiliado bp = new Afiliado(rs.getString("cuil"),
						rs.getInt("inte"), rs.getString("parentesco"),
						rs.getString("nombre"), rs.getString("apellido"),
						rs.getString("tdoc"), rs.getString("documento"),
						rs.getInt("id_seccional"), rs.getString("seccional"),
						rs.getDate("ingreso"), rs.getDate("baja_fecha"),
						rs.getInt("id_ospim"), rs.getInt("id_amtima"),
						rs.getInt("id_uoma"), rs.getString("plan"));
				// _log.debug("NOMBRE: " + bp.getNombre());
				listaAfiliados.add(bp);
			}

		} catch (Exception e) {
			_log.error(e);
			_log.debug(e.getMessage());
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return listaAfiliados;
	}

	public List<Afiliado> getBusquedaGrupoFliar(String cuil_titular) {
		Connection con = null;
		CallableStatement stmt = null;
		List<Afiliado> listaAfiliados = null;
		try {
			String sql = "{call buscar_grupo_fliar(?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, cuil_titular);
			ResultSet rs = stmt.executeQuery();
			listaAfiliados = new ArrayList<Afiliado>();
			while (rs.next()) {
				Afiliado bp = new Afiliado(rs.getString("cuil_titular"),
						rs.getInt("inte"), 
						rs.getInt("id_parentesco_sss"), 
						rs.getString("parentesco"),
						rs.getString("nombre"), 
						rs.getString("apellido"),
						rs.getString("tdoc"), 
						rs.getString("documento"),
						rs.getString("seccional"), 
						rs.getDate("ingreso"),
						rs.getDate("baja_fecha"));
				bp.setVigen_fecha(rs.getDate("vigen_fecha"));
				bp.setDiscapacitado(rs.getString("discapacitado"));

				listaAfiliados.add(bp);
			}

		} catch (Exception e) {
			_log.error(e);
			_log.debug(e.getMessage());
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return listaAfiliados;
	}

	public List<Afiliado> getBusquedaAfiliadosComponente(String cuil,
			String inte, String tipoDoc, String nroDoc, int seccional,
			String apellido, String nombre, int entidad, int afiNumero, 
			int nroSocioPrev, BigDecimal nroCredenPrev) {
		
		Connection con = null;
		CallableStatement stmt = null;
		List<Afiliado> listaAfiliados = null;
		try {
			String sql = "{call buscar_afiliados_componente(?,?,?,?,?,?,?,?,?,?,?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, cuil);
			if (null != inte) {
				stmt.setInt(2, Integer.parseInt(inte));
			} else {
				stmt.setNull(2, Types.INTEGER);
			}
			stmt.setString(3, tipoDoc);
			stmt.setString(4, nroDoc);
			if (seccional == 0) {
				stmt.setNull(5, Types.INTEGER);
			} else {
				stmt.setInt(5, seccional);
			}
			stmt.setString(6, apellido);
			stmt.setString(7, nombre);
			stmt.setInt(8, entidad);
			if (afiNumero == 0) {
				stmt.setNull(9, Types.INTEGER);
			} else {
				stmt.setInt(9, afiNumero);
			}
			if (nroSocioPrev == 0) {
				stmt.setNull(10, Types.INTEGER);
			} else {
				stmt.setInt(10, nroSocioPrev);
			}
			if (nroCredenPrev == null || nroCredenPrev.equals(new BigDecimal(0))) {
				stmt.setNull(11, Types.INTEGER);
			} else {
				stmt.setBigDecimal(11, nroCredenPrev);
			}
			
			ResultSet rs = stmt.executeQuery();
			listaAfiliados = new ArrayList<Afiliado>();
			
			String prefixAsc = "afi_cob_med_susp_";
			
			while (rs.next()) {
				Afiliado afi = new Afiliado(rs.getString("cuil"),
						rs.getInt("inte"), rs.getInt("id_parentesco_sss"),
						rs.getString("parentesco"),
						rs.getString("nombre"), rs.getString("apellido"),
						rs.getString("tdoc"), rs.getString("documento"),
						rs.getInt("id_seccional"), rs.getString("seccional"),
						rs.getDate("ingreso"), rs.getDate("baja_fecha"),
						rs.getInt("id_ospim"), rs.getInt("id_amtima"),
						rs.getInt("id_uoma"), rs.getInt("id_plan"),
						rs.getString("nombre_plan"), rs.getDate("alta_fecha"),
						rs.getString("discapacitado"), "", "");  // algun d�a buscar la tercerizadora y descripcion

				afi.setId_ospim_baja_fecha(rs.getDate("id_ospim_baja_fecha"));
				afi.setId_uoma_baja_fecha(rs.getDate("id_uoma_baja_fecha"));
				afi.setId_amtima_baja_fecha(rs.getDate("id_amtima_baja_fecha"));
				afi.setId_motivo_baja(rs.getInt("id_motivo_baja"));
				afi.setVigen_fecha(rs.getDate("vigen_fecha"));
				afi.setNaci_fecha(rs.getDate("fecha_nacimiento"));
				afi.setEmail(rs.getString("email"));
				afi.setIdCorrespondencia(rs.getInt("id_correspondencia"));
				afi.setProyecto(rs.getString("proyecto"));
				afi.setId_tercerizadora("");
				afi.setDesc_tercerizadora("");
				afi.setDetalleFechasSuperintendencia(DetalleFechasSuper.getMapping("", rs));
				afi.setTieneAntecedentesJudiciales(
						buscarTieneAntecedentesGrupoFamiliar(afi.getCuil_titular())
				);

				AfiSuspencionCobertura asc = new AfiSuspencionCobertura(rs.getInt(prefixAsc+"id"),
						rs.getDate(prefixAsc+"vigen_desde"),rs.getDate(prefixAsc+"vigen_hasta"));
				afi.addUltimaSuspCobertura(asc);
				
				try{
				   AfiTercerizadoraServicio ats = TercerizadoraServiceUtil.getInstance().buscarUltimaTercerizadoraDelAfiliado(null, afi.getCuil_titular());
				   afi.setId_tercerizadora(ats.getTercerizadora().getId_tercerizadora());
				   afi.setDesc_tercerizadora(ats.getTercerizadora().getDescripcion());
				}catch(Exception e1){}   
				
				listaAfiliados.add(afi);
			}

		} catch (Exception e) {
			_log.error(e);
			_log.debug(e.getMessage());
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return listaAfiliados;
	}

	
	public List<Afiliado> getBusquedaAfiliadosComponenteCredencialUOMA(String cuil,
			String inte, String tipoDoc, String nroDoc, int seccional,
			String apellido, String nombre, int entidad, int afiNumero, 
			int nroSocioPrev, BigDecimal nroCredenPrev) {
		
		Connection con = null;
		CallableStatement stmt = null;
		List<Afiliado> listaAfiliados = null;
		try {
			String sql = "{call buscar_afiliados_componente_credencial_uoma(?,?,?,?,?,?,?,?,?,?,?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, cuil);
			if (null != inte) {
				stmt.setInt(2, Integer.parseInt(inte));
			} else {
				stmt.setNull(2, Types.INTEGER);
			}
			stmt.setString(3, tipoDoc);
			stmt.setString(4, nroDoc);
			if (seccional == 0) {
				stmt.setNull(5, Types.INTEGER);
			} else {
				stmt.setInt(5, seccional);
			}
			stmt.setString(6, apellido);
			stmt.setString(7, nombre);
			stmt.setInt(8, entidad);
			if (afiNumero == 0) {
				stmt.setNull(9, Types.INTEGER);
			} else {
				stmt.setInt(9, afiNumero);
			}
			if (nroSocioPrev == 0) {
				stmt.setNull(10, Types.INTEGER);
			} else {
				stmt.setInt(10, nroSocioPrev);
			}
			if (nroCredenPrev == null || nroCredenPrev.equals(new BigDecimal(0))) {
				stmt.setNull(11, Types.INTEGER);
			} else {
				stmt.setBigDecimal(11, nroCredenPrev);
			}
			
			ResultSet rs = stmt.executeQuery();
			listaAfiliados = new ArrayList<Afiliado>();
			
			String prefixAsc = "afi_cob_med_susp_";
			
			while (rs.next()) {
				Afiliado afi = new Afiliado(rs.getString("cuil"),
						rs.getInt("inte"), rs.getInt("id_parentesco_sss"),
						rs.getString("parentesco"),
						rs.getString("nombre"), rs.getString("apellido"),
						rs.getString("tdoc"), rs.getString("documento"),
						rs.getInt("id_seccional"), rs.getString("seccional"),
						rs.getDate("ingreso"), rs.getDate("baja_fecha"),
						rs.getInt("id_ospim"), rs.getInt("id_amtima"),
						rs.getInt("id_uoma"), rs.getInt("id_plan"),
						rs.getString("nombre_plan"), rs.getDate("alta_fecha"),
						rs.getString("discapacitado"), "", "");  // algun d�a buscar la tercerizadora y descripcion

				afi.setId_ospim_baja_fecha(rs.getDate("id_ospim_baja_fecha"));
				afi.setId_uoma_baja_fecha(rs.getDate("id_uoma_baja_fecha"));
				afi.setId_amtima_baja_fecha(rs.getDate("id_amtima_baja_fecha"));
				afi.setId_motivo_baja(rs.getInt("id_motivo_baja"));
				afi.setVigen_fecha(rs.getDate("vigen_fecha"));
				afi.setNaci_fecha(rs.getDate("fecha_nacimiento"));
				afi.setEmail(rs.getString("email"));
				afi.setIdCorrespondencia(rs.getInt("id_correspondencia"));
				afi.setProyecto(rs.getString("proyecto"));
				afi.setId_tercerizadora("");
				afi.setDesc_tercerizadora("");
				afi.setDetalleFechasSuperintendencia(DetalleFechasSuper.getMapping("", rs));
				
				AfiSuspencionCobertura asc = new AfiSuspencionCobertura(rs.getInt(prefixAsc+"id"),
						rs.getDate(prefixAsc+"vigen_desde"),rs.getDate(prefixAsc+"vigen_hasta"));
				afi.addUltimaSuspCobertura(asc);
				
				listaAfiliados.add(afi);
			}

		} catch (Exception e) {
			_log.error(e);
			_log.debug(e.getMessage());
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return listaAfiliados;
	}

	
	public List<Afiliado> getBusquedaAfiliadosComponenteReintegro(String cuil,
			String inte, String tipoDoc, String nroDoc, int seccional,
			String apellido, String nombre, int entidad, int afiNumero, 
			int nroSocioPrev, BigDecimal nroCredenPrev) {
		
		Connection con = null;
		CallableStatement stmt = null;
		List<Afiliado> listaAfiliados = null;
		try {
			
			String sql = "{call buscar_afiliados_componente_reintegro(?,?,?,?,?,?,?,?,?,?,?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, cuil);
			if (null != inte) {
				stmt.setInt(2, Integer.parseInt(inte));
			} else {
				stmt.setNull(2, Types.INTEGER);
			}
			stmt.setString(3, tipoDoc);
			stmt.setString(4, nroDoc);
			if (seccional == 0) {
				stmt.setNull(5, Types.INTEGER);
			} else {
				stmt.setInt(5, seccional);
			}
			stmt.setString(6, apellido);
			stmt.setString(7, nombre);
			stmt.setInt(8, entidad);
			if (afiNumero == 0) {
				stmt.setNull(9, Types.INTEGER);
			} else {
				stmt.setInt(9, afiNumero);
			}
			if (nroSocioPrev == 0) {
				stmt.setNull(10, Types.INTEGER);
			} else {
				stmt.setInt(10, nroSocioPrev);
			}
			if (nroCredenPrev == null || nroCredenPrev.equals(new BigDecimal(0))) {
				stmt.setNull(11, Types.INTEGER);
			} else {
				stmt.setBigDecimal(11, nroCredenPrev);
			}
			
			ResultSet rs = stmt.executeQuery();
			listaAfiliados = new ArrayList<Afiliado>();
			while (rs.next()) {
				Afiliado bp = new Afiliado(rs.getString("cuil"),
						rs.getInt("inte"), rs.getInt("id_parentesco_sss"),
						rs.getString("parentesco"),
						rs.getString("nombre"), rs.getString("apellido"),
						rs.getString("tdoc"), rs.getString("documento"),
						rs.getInt("id_seccional"), rs.getString("seccional"),
						rs.getDate("ingreso"), rs.getDate("baja_fecha"),
						rs.getInt("id_ospim"), rs.getInt("id_amtima"),
						rs.getInt("id_uoma"), rs.getInt("id_plan"),
						rs.getString("nombre_plan"), rs.getDate("alta_fecha"),
						rs.getString("discapacitado"),
						rs.getString("id_tercerizadora"),
						rs.getString("desc_tercerizadora"));

				bp.setId_ospim_baja_fecha(rs.getDate("id_ospim_baja_fecha"));
				bp.setId_uoma_baja_fecha(rs.getDate("id_uoma_baja_fecha"));
				bp.setId_amtima_baja_fecha(rs.getDate("id_amtima_baja_fecha"));
				bp.setId_motivo_baja(rs.getInt("id_motivo_baja"));
				bp.setVigen_fecha(rs.getDate("vigen_fecha"));
				bp.setNaci_fecha(rs.getDate("fecha_nacimiento"));
				bp.setEmail(rs.getString("email"));
				bp.setIdCorrespondencia(rs.getInt("id_correspondencia"));
				bp.setProyecto(rs.getString("proyecto"));
				bp.setConReclamoPrestacional(rs.getBoolean("conreclamo_prestacional"));
				listaAfiliados.add(bp);
			}

		} catch (Exception e) {
			_log.error(e);
			_log.debug(e.getMessage());
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return listaAfiliados;
	}

	
	public List<Afiliado> getBusquedaAfiliadosOpciones(String cuil,
			String delegacion, String apellido, String nombre, int libro, 
			int nroFormulario, boolean incluyeBajas) {
		
		Connection con = null;
		CallableStatement stmt = null;
		List<Afiliado> listaAfiliados = null;
		try {
			String sql = "{call buscar_afiliados_opciones(?,?,?,?,?,?,?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, cuil);
			stmt.setString(2, delegacion);
			stmt.setString(3, apellido);
			stmt.setString(4, nombre);
			if (libro == 0) {
				stmt.setNull(5, Types.INTEGER);
			} else {
				stmt.setInt(5, libro);
			}
			if (nroFormulario == 0) {
				stmt.setNull(6, Types.INTEGER);
			} else {
				stmt.setInt(6, nroFormulario);
			}
			stmt.setBoolean(7, incluyeBajas);
			
			ResultSet rs = stmt.executeQuery();
			listaAfiliados = new ArrayList<Afiliado>();
			while (rs.next()) {
				Afiliado bp = new Afiliado(rs.getString("cuil"),
						rs.getInt("inte"), rs.getInt("id_parentesco_sss"), 
						rs.getString("parentesco"),
						rs.getString("nombre"), rs.getString("apellido"),
						rs.getString("tdoc"), rs.getString("documento"),
						rs.getInt("id_seccional"), rs.getString("seccional"),
						rs.getDate("ingreso"), rs.getDate("baja_fecha"),
						rs.getInt("id_ospim"), rs.getInt("id_amtima"),
						rs.getInt("id_uoma"), rs.getInt("id_plan"),
						rs.getString("nombre_plan"), rs.getDate("alta_fecha"),
						rs.getString("discapacitado"),
						"", //id_tercerizadora
						rs.getString("desc_tercerizadora"));

				bp.setId_ospim_baja_fecha(rs.getDate("id_ospim_baja_fecha"));
				bp.setId_uoma_baja_fecha(rs.getDate("id_uoma_baja_fecha"));
				bp.setId_amtima_baja_fecha(rs.getDate("id_amtima_baja_fecha"));
				bp.setId_motivo_baja(rs.getInt("id_motivo_baja"));
				bp.setVigen_fecha(rs.getDate("vigen_fecha"));
				bp.setNaci_fecha(rs.getDate("fecha_nacimiento"));
				bp.setIdCorrespondencia(rs.getInt("id_correspondencia"));
				bp.setInte(rs.getInt("inte")); // en opciones aca viene el nro_formulario, si es una chanchada...
				listaAfiliados.add(bp);
			}

		} catch (Exception e) {
			_log.error(e);
			_log.debug(e.getMessage());
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return listaAfiliados;
	}

	public List<Afiliado> getBusquedaAfiliadosComponenteReintegro(String cuil,
			String inte, String tipoDoc, String nroDoc, int seccional,
			String apellido, String nombre, int entidad, int afiNumero,
			Date fecha_prestacion, BigDecimal nroCredenPrev, int nroSocioPrev) {
		Connection con = null;
		CallableStatement stmt = null;
		List<Afiliado> listaAfiliados = null;
		try {
			String sql = "{call buscar_afiliados_componente_fecharef_reintegros(?,?,?,?,?,?,?,?,?,?,?,?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, cuil);
			if (null != inte) {
				stmt.setInt(2, Integer.parseInt(inte));
			} else {
				stmt.setNull(2, Types.INTEGER);
			}
			stmt.setString(3, tipoDoc);
			stmt.setString(4, nroDoc);
			if (seccional == 0) {
				stmt.setNull(5, Types.INTEGER);
			} else {
				stmt.setInt(5, seccional);
			}
			stmt.setString(6, apellido);
			stmt.setString(7, nombre);
			stmt.setInt(8, entidad);
			if (afiNumero == 0) {
				stmt.setNull(9, Types.INTEGER);
			} else {
				stmt.setInt(9, afiNumero);
			}
			
			
			if (null != fecha_prestacion ) {
				stmt.setDate(10, new java.sql.Date(fecha_prestacion.getTime()));
			} else {
				stmt.setNull(10, Types.DATE );
			}
			if (nroSocioPrev == 0) {
				stmt.setNull(11, Types.INTEGER);
			} else {
				stmt.setInt(11, nroSocioPrev);
			}
			if (nroCredenPrev == null || nroCredenPrev.equals(new BigDecimal(0))) {
				stmt.setNull(12, Types.INTEGER);
			} else {
				stmt.setBigDecimal(12, nroCredenPrev);
			}
			
			
			ResultSet rs = stmt.executeQuery();
			listaAfiliados = new ArrayList<Afiliado>();
			while (rs.next()) {
				Afiliado bp = new Afiliado(rs.getString("cuil"),
						rs.getInt("inte"), rs.getInt("id_parentesco_sss"),
						rs.getString("parentesco"),
						rs.getString("nombre"), rs.getString("apellido"),
						rs.getString("tdoc"), rs.getString("documento"),
						rs.getInt("id_seccional"), rs.getString("seccional"),
						rs.getDate("ingreso"), rs.getDate("baja_fecha"),
						rs.getInt("id_ospim"), rs.getInt("id_amtima"),
						rs.getInt("id_uoma"), rs.getInt("id_plan"),
						rs.getString("nombre_plan"), rs.getDate("alta_fecha"),
						rs.getString("discapacitado"),
						rs.getString("id_tercerizadora"),
						rs.getString("desc_tercerizadora"));

				bp.setId_ospim_baja_fecha(rs.getDate("id_ospim_baja_fecha"));
				bp.setId_uoma_baja_fecha(rs.getDate("id_uoma_baja_fecha"));
				bp.setId_amtima_baja_fecha(rs.getDate("id_amtima_baja_fecha"));
				bp.setNaci_fecha(rs.getDate("fecha_nacimiento"));
				bp.setConReclamoPrestacional(rs.getBoolean("conreclamo_prestacional"));
				bp.setTieneAntecedentesJudiciales(rs.getInt("tiene_antecedentes_judiciales"));
				AfiliacionPrevencion pre = new AfiliacionPrevencion();
				pre.setNroSocio(rs.getInt("nrosocioprev"));
				pre.setNroCredencial(rs.getBigDecimal("nrocredenprev"));
				bp.setPrevencion(pre);
				Incidente incidente = new Incidente();
				incidente = buscarUltimoIncidente(rs.getString("cuil"), rs.getInt("inte"));
				if (incidente != null) {
					bp.addIncidente(incidente);
				}
				
				
				
				
				
				listaAfiliados.add(bp);
			}

		} catch (Exception e) {
			_log.error(e);
			_log.debug(e.getMessage());
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return listaAfiliados;
	}
	

	
	public List<Afiliado> getBusquedaAfiliadosComponente(String cuil,
			String inte, String tipoDoc, String nroDoc, int seccional,
			String apellido, String nombre, int entidad, int afiNumero,
			Date fecha_prestacion, int nroSocioPrev , BigDecimal nroCredenPrev) {
		Connection con = null;
		CallableStatement stmt = null;
		List<Afiliado> listaAfiliados = null;
		try {
			String sql = "{call buscar_afiliados_componente_fecharef(?,?,?,?,?,?,?,?,?,?,?,?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, cuil);
			if (null != inte) {
				stmt.setInt(2, Integer.parseInt(inte));
			} else {
				stmt.setNull(2, Types.INTEGER);
			}
			stmt.setString(3, tipoDoc);
			stmt.setString(4, nroDoc);
			if (seccional == 0) {
				stmt.setNull(5, Types.INTEGER);
			} else {
				stmt.setInt(5, seccional);
			}
			stmt.setString(6, apellido);
			stmt.setString(7, nombre);
			stmt.setInt(8, entidad);
			if (afiNumero == 0) {
				stmt.setNull(9, Types.INTEGER);
			} else {
				stmt.setInt(9, afiNumero);
			}
			stmt.setDate(10, new java.sql.Date(fecha_prestacion.getTime()));
			
			if (nroSocioPrev == 0) {
				stmt.setNull(11, Types.INTEGER);
			} else {
				stmt.setInt(11, nroSocioPrev);
			}
			if (nroCredenPrev == null || nroCredenPrev.equals(new BigDecimal(0))) {
				stmt.setNull(12, Types.INTEGER);
			} else {
				stmt.setBigDecimal(12, nroCredenPrev);
			}
			
			ResultSet rs = stmt.executeQuery();
			listaAfiliados = new ArrayList<Afiliado>();
			while (rs.next()) {
				Afiliado bp = new Afiliado(rs.getString("cuil"),
						rs.getInt("inte"), rs.getInt("id_parentesco_sss"),
						rs.getString("parentesco"),
						rs.getString("nombre"), rs.getString("apellido"),
						rs.getString("tdoc"), rs.getString("documento"),
						rs.getInt("id_seccional"), rs.getString("seccional"),
						rs.getDate("ingreso"), rs.getDate("baja_fecha"),
						rs.getInt("id_ospim"), rs.getInt("id_amtima"),
						rs.getInt("id_uoma"), rs.getInt("id_plan"),
						rs.getString("nombre_plan"), rs.getDate("alta_fecha"),
						rs.getString("discapacitado"),
						rs.getString("id_tercerizadora"),
						rs.getString("desc_tercerizadora"),
						rs.getInt("nrosocioprev"),
						rs.getBigDecimal("nrocredenprev"),
						buscarUltimoIncidente(rs.getString("cuil"), rs.getInt("inte")));
				bp.setId_ospim_baja_fecha(rs.getDate("id_ospim_baja_fecha"));
				bp.setId_uoma_baja_fecha(rs.getDate("id_uoma_baja_fecha"));
				bp.setId_amtima_baja_fecha(rs.getDate("id_amtima_baja_fecha"));
				bp.setNaci_fecha(rs.getDate("fecha_nacimiento"));
				bp.setTieneAntecedentesJudiciales(
						buscarTieneAntecedentesGrupoFamiliar(bp.getCuil_titular())
				);
				listaAfiliados.add(bp);
			}

		} catch (Exception e) {
			_log.error(e);
			_log.debug(e.getMessage());
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return listaAfiliados;
	}
	
	public List<DetalleOpcionesSS> buscarOpcionesSSSpendientesExportar(){
		
		Connection con = null;
		CallableStatement stmt = null;
		List<DetalleOpcionesSS> listaOpciones = null;
		
		DetalleOpcionesSS detOpSS = null;
		
		try {
			String sql = "{call buscar_opciones_sss_para_exportar()}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());

			ResultSet rs = stmt.executeQuery();
			listaOpciones = new ArrayList<DetalleOpcionesSS>();
			while (rs.next()) {
				detOpSS = DetalleOpcionesSS.getMapping("opsss_", rs);
				listaOpciones.add(detOpSS);
			}

		} catch (Exception e) {
			_log.error(e);
			_log.debug(e.getMessage());
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return listaOpciones;
	}
	
	public List<DetalleOpcionesSS> buscarOpcionesSSSpendientesExportarXls(){
		
		Connection con = null;
		CallableStatement stmt = null;
		List<DetalleOpcionesSS> listaOpciones = null;
		
		DetalleOpcionesSS detOpSS = null;
		
		try {
			String sql = "{call buscar_opciones_sss_para_exportar_xls()}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());

			ResultSet rs = stmt.executeQuery();
			listaOpciones = new ArrayList<DetalleOpcionesSS>();
			while (rs.next()) {
				detOpSS = DetalleOpcionesSS.getMapping("opsss_", rs);
				listaOpciones.add(detOpSS);
			}

		} catch (Exception e) {
			_log.error(e);
			_log.debug(e.getMessage());
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return listaOpciones;
	}
	
 public List<Domicilio> buscarDomiciliosAfiliado(String cuil_titular, int inte){
		
		Connection con = null;
		CallableStatement stmt = null;
		List<Domicilio> lista = new ArrayList<Domicilio>();
		Domicilio domi = null;
		
		try {
			String sql = "{call busca_afiliado_domicilio(?,?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, cuil_titular);
			stmt.setInt(2, inte);
			
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				domi = Domicilio.getMapping(rs, "afidom_");
				lista.add(domi);
			}

		} catch (Exception e) {
			_log.error(e);
			_log.debug(e.getMessage());
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return lista;
	}
 
 	public Afiliado registraConsultaAfiliadoIGS(String cuilTitular, String nroCredencial, String inte, String docuTipo, String docuNumero, 
 			String ip,String fecha) throws Exception{
		
		Connection con = null;
		CallableStatement stmt = null;
		Afiliado afiliado= null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");		
		try {
			String sql = "{call informes.registra_consulta_afiliado_igs(?,?,?,?,?,?,?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			if(null!=cuilTitular && !"".equals(cuilTitular.trim())){
				stmt.setString(1, cuilTitular);
			}else{
				stmt.setNull(1, Types.VARCHAR);
			}
			try{
				stmt.setBigDecimal(2, BigDecimal.valueOf(Long.valueOf(nroCredencial)));				
			}catch(Exception e){
				stmt.setNull(2, Types.INTEGER);				
			}			
			try{
				stmt.setInt(3, Integer.valueOf(inte));
			}catch(Exception e){
				stmt.setNull(3, Types.INTEGER);				
			}
			
			if(null!=docuTipo && !"".equals(docuTipo.trim())){
				stmt.setString(4, docuTipo);
			}else{
				stmt.setNull(4, Types.VARCHAR);
			}
			
			if(null!=docuNumero && !"".equals(docuNumero.trim())){
				stmt.setString(5, docuNumero);
			}else{
				stmt.setNull(5, Types.VARCHAR);
			}

			stmt.setString(6, ip);
			
			stmt.setDate(7, new java.sql.Date((sdf.parse(fecha)).getTime()));
			
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				afiliado=new Afiliado();
				afiliado.setCuil_titular(rs.getString("cuil_titular"));
				afiliado.setApellido(rs.getString("apellido"));
				afiliado.setNombre(rs.getString("nombre"));
				afiliado.setDocumento_tipo(rs.getString("docu_tipo"));
				afiliado.setDocu_numero(rs.getString("docu_numero"));
				afiliado.setNroCredencial(rs.getBigDecimal("nro_creden").longValue());
				afiliado.setInte(rs.getInt("inte"));
				Plan plan=new Plan(rs.getString("plan"));
				AfiPlan afiPlan=new AfiPlan();
				afiPlan.setPlan(plan);
				afiliado.setAfiPlan(afiPlan);
				Domicilio domi=new Domicilio();
				domi.setTelefono(rs.getString("telefono"));
				Localidad loca=new Localidad();
				loca.setDescripcion(rs.getString("localidad"));
				domi.setLocalidad(loca);
				Provincia prov=new Provincia();
				prov.setDescripcion(rs.getString("provincia"));
				domi.setProvincia(prov);
				afiliado.setDomicilioDefault(domi);	
				afiliado.setId_tercerizadora(rs.getString("tercerizadora"));
			}

		} catch (Exception e) {
			_log.error(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return afiliado;
	}
 
	public List<ConsultaIGSTotal> buscarConsultasIGS(BusquedaConsultasIGSFiltro filtro) throws Exception{
			
		Connection con = null;
		CallableStatement stmt = null;
		List<ConsultaIGSTotal> consultasIGS= new ArrayList<ConsultaIGSTotal>();
		ConsultaIGSTotal consIGS = null;		
		try {
			String sql = "{call informes.buscar_consultas_IGS(?,?,?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			
			stmt.setDate(1, new java.sql.Date(filtro.getFechaDesde().getTime()));
			stmt.setDate(2, new java.sql.Date(filtro.getFechaHasta().getTime()));
			stmt.setInt(3, filtro.getPagina());
			
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				consIGS = ConsultaIGSTotal.getMapping("igs_", rs);
				consultasIGS.add(consIGS);
			}

		} catch (Exception e) {
			_log.error(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return consultasIGS;
	} 
	
	public List<ConsultaIGSTotal> buscarConsultasIGS_xls(BusquedaConsultasIGSFiltro filtro) throws Exception{
		
		Connection con = null;
		CallableStatement stmt = null;
		List<ConsultaIGSTotal> consultasIGS= new ArrayList<ConsultaIGSTotal>();
		ConsultaIGSTotal consIGS = null;		
		try {
			String sql = "{call informes.buscar_consultas_IGS_xls(?,?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			
			stmt.setDate(1, new java.sql.Date(filtro.getFechaDesde().getTime()));
			stmt.setDate(2, new java.sql.Date(filtro.getFechaHasta().getTime()));
			
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				consIGS = ConsultaIGSTotal.getMapping("igs_", rs);
				consultasIGS.add(consIGS);
			}

		} catch (Exception e) {
			_log.error(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return consultasIGS;
	} 
	
	/**
	 * 
	 * Este metodo trae el ultimo caso de unidad operativa
	 * 
	 *  
	 * @return Incidente
	 * @throws Exception
	 */
	public static Incidente buscarUltimoIncidente (String cuilTitular, int inte) throws Exception{
		
		Connection con = null;
		CallableStatement stmt = null;
		Incidente incidente= null;
				
		try {
			String sql = "{call uoma.buscar_ultimo_incidente(?,?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			
			stmt.setString(1, cuilTitular);
			stmt.setInt(2, inte);
		
				
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				incidente= Incidente.getMappingUltimoIncidente(rs);
			}

		} catch (Exception e) {
			_log.error(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return incidente;
	}

	public int buscarTieneAntecedentesGrupoFamiliar(String cuilTitular) {
		Connection con = null;
		CallableStatement stmt = null;
		ResultSet rs = null;
		try {

			String sql = "select public.buscar_tiene_antecedentes_grupo_familiar(?) as tiene_antecedentes_judiciales";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql);
			stmt.setString(1, cuilTitular);

			rs = stmt.executeQuery();
			if (rs.next()) {
				int tieneAntecedentes = rs.getInt("tiene_antecedentes_judiciales");
				return tieneAntecedentes;
			}


		} catch (Exception e) {
			_log.error("[ERROR] Error buscando antecedentes del grupo familiar para cuilTitular=" + cuilTitular, e);
		} finally {
			try {
				if (rs != null) rs.close();
			} catch (Exception e) {
				_log.error("[FINALLY] Error cerrando ResultSet -buscarTieneAntecedentesGrupoFamiliar", e);
			}
			ConnectionHelper.cerrar(stmt, con);
		}

		return 0;
	}
}
