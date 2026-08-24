package ar.com.ospim.global.services;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import ar.com.empresas.beans.Actividad;
import ar.com.ospim.afiliados.beans.CategoriaLaboral;
import ar.com.ospim.afiliados.beans.CieDiez;
import ar.com.ospim.afiliados.beans.Direccion;
import ar.com.ospim.afiliados.beans.Documento;
import ar.com.ospim.afiliados.beans.MotivoBaja;
import ar.com.ospim.afiliados.beans.SituacionRevista;
import ar.com.ospim.afiliados.beans.TercerizadoraServicio;
import ar.com.ospim.afiliados.beans.TipoAporte;
import ar.com.ospim.afiliados.beans.TipoBono;
import ar.com.ospim.afiliados.reportes.beans.UltimosProcesosSisOld;
import ar.com.ospim.autorizaciones.beans.Cartilla;
import ar.com.ospim.autorizaciones.beans.DrogaPatologia;
import ar.com.ospim.autorizaciones.beans.ModalidadAtencion;
import ar.com.ospim.autorizaciones.beans.MotivoExcepcion;
import ar.com.ospim.autorizaciones.beans.OpcionesPrestacion;
import ar.com.ospim.autorizaciones.beans.ReclamosPrestacionalesIntegracion;
import ar.com.ospim.autorizaciones.beans.ReclamosPrestacionalesRevisionEstado;
import ar.com.ospim.autorizaciones.beans.TiposDeGestionReclamosPrestacionales;
import ar.com.ospim.autorizaciones.beans.TiposDeSituacionesMedicas;
import ar.com.ospim.autorizaciones.beans.EstadosReclamosPrestacionales;
import ar.com.ospim.correspondencia.beans.UsuarioCorrespondencia;
import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.beans.Anticipo;
import ar.com.ospim.global.beans.AportesMonotributo;
import ar.com.ospim.global.beans.AportesMonotributoClase;
import ar.com.ospim.global.beans.Banco;
import ar.com.ospim.global.beans.ClaseBase;
import ar.com.ospim.global.beans.Comprobante;
import ar.com.ospim.global.beans.Concepto;
import ar.com.ospim.global.beans.ConvenioNacion;
import ar.com.ospim.global.beans.CuentasNacion;
import ar.com.ospim.global.beans.Delegacion;
import ar.com.ospim.global.beans.Efectivo;
import ar.com.ospim.global.beans.Efectivo.Estado;
import ar.com.ospim.global.beans.Empresa;
import ar.com.ospim.global.beans.EstadoCivil;
import ar.com.ospim.global.beans.Farmacia;
import ar.com.ospim.global.beans.ListaConcepto;
import ar.com.ospim.global.beans.Localidad;
import ar.com.ospim.global.beans.Motivo;
import ar.com.ospim.global.beans.Nacionalidad;
import ar.com.ospim.global.beans.ObraSocialCampo;
import ar.com.ospim.global.beans.Pais;
import ar.com.ospim.global.beans.Parentesco;
import ar.com.ospim.global.beans.Plan;
import ar.com.ospim.global.beans.PlanCuentas;
import ar.com.ospim.global.beans.PlanCuentasSSS;
import ar.com.ospim.global.beans.Prestacion;
import ar.com.ospim.global.beans.Provincia;
import ar.com.ospim.global.beans.RamoEmpresa;
import ar.com.ospim.global.beans.Seccional;
import ar.com.ospim.global.beans.TipoMovExtractoBancario;
import ar.com.ospim.global.beans.TipoPago;
import ar.com.ospim.liquidaciones.beans.Especialidad;
import ar.com.ospim.liquidaciones.beans.EspecialidadPrestador;
import ar.com.ospim.liquidaciones.beans.Prestador;
import ar.com.ospim.liquidaciones.beans.PrestadorLugarAtencion;
import ar.com.ospim.liquidaciones.beans.ProfesionPrestador;
import ar.com.ospim.liquidaciones.beans.SubEspecialidadPrestador;
import ar.com.ospim.liquidaciones.beans.TipoDiscapacidad;
import ar.com.ospim.liquidaciones.beans.TipoNomenclador;
import ar.com.ospim.novedades.beans.ArchivoNovedad;
import ar.com.ospim.novedades.beans.TipoNovedad;
import ar.com.ospim.rrhh.beans.TarjetaAcceso;
import ar.com.ospim.tesoreria.beans.Chequera;
import ar.com.ospim.tesoreria.beans.CuentaBancaria;
import ar.com.ospim.tesoreria.beans.MovimientoBancario;
import ar.com.ospim.tesoreria.beans.TipoMovBcrio;
import ar.com.ospim.tesoreria.beans.TipoTrxBancaria;
import ar.com.ospim.util.ConnectionHelper;
import ar.com.ospim.util.StringUtils;
import ar.com.uoma.beans.CentroCosto;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

/**
 * <a href="TraeListasServiceImpl.java.html"><b><i>View Source</i></b></a>
 * 
 * @author Carlos Rivas
 * 
 */
@SuppressWarnings("unused")
public class TraeListasServiceImpl {

	private static Log _log = LogFactoryUtil
			.getLog(TraeListasServiceImpl.class);

	public List<Seccional> getSeccionales() {
		Connection con = null;
		List<Seccional> listaSeccionales = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call trae_seccionales_destino()}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			ResultSet rs = stmt.executeQuery();
			listaSeccionales = new ArrayList<Seccional>();
			while (rs.next()) {
				Seccional seccional = Seccional.getMapping(rs);
				listaSeccionales.add(seccional);
			}
		} catch (Exception e) {
			_log.debug("error al traer seccinoales", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return listaSeccionales;
	}
	
	public List<Actividad> getActividades() {
		Connection con = null;
		List<Actividad> listActividades= null;
		CallableStatement stmt = null;
		try {
			String sql = "{call informacion_afip.trae_actividades()}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			ResultSet rs = stmt.executeQuery();
			listActividades = new ArrayList<Actividad>();
			while (rs.next()) {
				Actividad actividad = Actividad.getMapping(rs);
				listActividades.add(actividad);
			}
		} catch (Exception e) {
			_log.debug("error al traer listActividades", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return listActividades;
	}

	public List<Seccional> getSeccionalesFarmacia() {
		Connection con = null;
		List<Seccional> listaSeccionales = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call trae_seccionales_farmacia()}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			ResultSet rs = stmt.executeQuery();
			listaSeccionales = new ArrayList<Seccional>();
			while (rs.next()) {
				Seccional seccional = Seccional.getMapping(rs);
				listaSeccionales.add(seccional);
			}
		} catch (Exception e) {
			_log.debug("error al traer seccionales", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return listaSeccionales;
	}

	public List<Seccional> getSeccionales(Integer id, String nombre, String cuit) {
		Connection con = null;
		List<Seccional> listaSeccionales = null;
		CallableStatement stmt = null;
		_log.debug("CUIT: " + cuit + " id: "+ id + " nombre: " + nombre);
		try {
			String sql = "{call trae_seccionales_filtrada_destino(?,?,?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			if (id == null) {
				stmt.setNull(1, Types.INTEGER);
			} else {
				stmt.setInt(1, id);
			}
			stmt.setString(2, nombre);
			
			stmt.setString(3, cuit);
			
			ResultSet rs = stmt.executeQuery();
			listaSeccionales = new ArrayList<Seccional>();
			while (rs.next()) {
				Seccional seccional = Seccional.getMapping(rs);
				seccional.setCuitEntidad(cuit);
				listaSeccionales.add(seccional);				
			}
		} catch (Exception e) {
			_log.debug("error al traer seccionales", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return listaSeccionales;
	}

	public List<Delegacion> getDelegaciones() {
		Connection con = null;
		List<Delegacion> listaDelegaciones = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call trae_delegaciones()}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			ResultSet rs = stmt.executeQuery();
			listaDelegaciones = new ArrayList<Delegacion>();
			while (rs.next()) {
				Delegacion delegacion = Delegacion.getMapping(rs,"");
				listaDelegaciones.add(delegacion);
			}
		} catch (Exception e) {
			_log.debug("error al traer delegaciones", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return listaDelegaciones;
	}

	
	public List<Prestacion> getPrestacionesReintegro() {
		Connection con = null;
		List<Prestacion> listaPrestaciones = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call trae_prestaciones_reintegro()}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			ResultSet rs = stmt.executeQuery();
			listaPrestaciones = new ArrayList<Prestacion>();
			while (rs.next()) {
				Prestacion prestacion = new Prestacion(rs
						.getInt("id_prestacion"), rs.getString("descripcion"));
				listaPrestaciones.add(prestacion);
			}
		} catch (Exception e) {
			e.printStackTrace();
			_log.debug(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return listaPrestaciones;
	}

	public List<Prestador> getPrestadores() {
		Connection con = null;
		List<Prestador> listaPrestadores = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call trae_prestadores()}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			ResultSet rs = stmt.executeQuery();
			listaPrestadores = new ArrayList<Prestador>();
			while (rs.next()) {
				Prestador prestador = new Prestador(rs.getString("cuit"), rs
						.getInt("id_prestador"), rs.getString("descripcion"));
				listaPrestadores.add(prestador);
			}
		} catch (Exception e) {
			_log.debug(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return listaPrestadores;
	}
	
	public List<PrestadorLugarAtencion> getPrestadoresLugarAtencion() {
		Connection con = null;
		List<PrestadorLugarAtencion> listaPrestadores = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call trae_prestadores_lugar_atencion()}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			ResultSet rs = stmt.executeQuery();
			listaPrestadores = new ArrayList<PrestadorLugarAtencion>();
			while (rs.next()) {
				Prestador prestador = new Prestador(rs.getString("cuit"), rs
						.getInt("id_prestador"), rs.getString("descripcion"));
				PrestadorLugarAtencion prestadorLA = new PrestadorLugarAtencion();
				prestadorLA.setPrestador(prestador);
				prestadorLA.setId_prestador(prestador.getId_prestador());
				prestadorLA.setId_domicilio(rs.getInt("id_domicilio"));
				listaPrestadores.add(prestadorLA);
			}
		} catch (Exception e) {
			_log.debug(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return listaPrestadores;
	}

	public List<Empresa> getEmpleadores(String cuit, String razon) {
		Connection con = null;
		List<Empresa> listaEmpleadores = null;
		CallableStatement stmt = null;
		_log.debug("CUIT: " + cuit + "Razon Soc.: "+ razon);
		try {
			String sql = "{call trae_empleadores(?,?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, cuit == null || cuit.trim().equals("") ? null
					: cuit);
			stmt.setString(2, razon == null || razon.trim().equals("") ? null
					: razon);
			ResultSet rs = stmt.executeQuery();
			listaEmpleadores = new ArrayList<Empresa>();
			while (rs.next()) {
				Empresa empleador = new Empresa(rs.getString("cuit"), rs
						.getString("sucursal"), rs.getString("descripcion"));
				listaEmpleadores.add(empleador);
			}
		} catch (Exception e) {
			_log.debug(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return listaEmpleadores;
	}

	public List<Empresa> getEmpleadores(String cuit, String razon, String sucu) {
		Connection con = null;
		List<Empresa> listaEmpleadores = null;
		CallableStatement stmt = null;
		_log.debug("CUIT: " + cuit + "Razon Soc.: "+ razon);
		try {
			String sql = "{call trae_empleadores_filtro(?,?,?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			
			if (StringUtils.checkEmpty(cuit)) {
				stmt.setNull(1, Types.VARCHAR);
			} else {
				stmt.setString(1, cuit);
			}
			if (StringUtils.checkEmpty(razon)) {
				stmt.setNull(2, Types.VARCHAR);
			} else {
				stmt.setString(2, razon);
			}
			if (StringUtils.checkEmpty(sucu)) {
				stmt.setNull(3, Types.VARCHAR);
			} else {
				stmt.setString(3, sucu);
			}

			ResultSet rs = stmt.executeQuery();
			listaEmpleadores = new ArrayList<Empresa>();
			while (rs.next()) {
				Empresa empleador = new Empresa(rs.getString("cuit"), rs
						.getString("sucursal"), rs.getString("descripcion"), rs.getInt("id_ramo_empresa"), rs.getString("imp_ganancias"), rs.getDate("baja_fecha"));
				listaEmpleadores.add(empleador);
			}
		} catch (Exception e) {
			_log.debug(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return listaEmpleadores;
	}

	public List<Empresa> getEmpleadoresAFIP(String cuit, String razon, String sucu) {
		Connection con = null;
		List<Empresa> listaEmpleadores = null;
		CallableStatement stmt = null;
		_log.debug("CUIT: " + cuit + "Razon Soc.: "+ razon);
		try {
			String sql = "{call trae_empleadores_afip_filtro(?,?,?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			
			if (StringUtils.checkEmpty(cuit)) {
				stmt.setNull(1, Types.VARCHAR);
			} else {
				stmt.setString(1, cuit);
			}
			if (StringUtils.checkEmpty(razon)) {
				stmt.setNull(2, Types.VARCHAR);
			} else {
				stmt.setString(2, razon);
			}
			if (StringUtils.checkEmpty(sucu)) {
				stmt.setNull(3, Types.VARCHAR);
			} else {
				stmt.setString(3, sucu);
			}

			ResultSet rs = stmt.executeQuery();
			listaEmpleadores = new ArrayList<Empresa>();
			while (rs.next()) {
				Empresa empleador = Empresa.getMappingAfip(rs, "");
				listaEmpleadores.add(empleador);
			}
		} catch (Exception e) {
			_log.debug(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return listaEmpleadores;
	}
	
	public List<Empresa> getEmpleadoresDeOP(String cuit, String razon,
			String sucu, int id_prestador) {
		Connection con = null;
		List<Empresa> listaEmpleadores = null;
		CallableStatement stmt = null;
		_log.debug("CUIT: " + cuit + "Razon Soc.: "+ razon + "Id Prestador: " + id_prestador);
		try {
			String sql = "{call trae_empleadores_OP_filtro(?,?,?,?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			if (StringUtils.checkEmpty(cuit)) {
				stmt.setNull(1, Types.VARCHAR);
			} else {
				stmt.setString(1, cuit);
			}
			if (StringUtils.checkEmpty(razon)) {
				stmt.setNull(2, Types.VARCHAR);
			} else {
				stmt.setString(2, razon);
			}
			if (StringUtils.checkEmpty(sucu)) {
				stmt.setNull(3, Types.VARCHAR);
			} else {
				stmt.setString(3, sucu);
			}
			if (id_prestador == 0) {
				stmt.setNull(4, Types.INTEGER);
			} else {
				stmt.setInt(4, id_prestador);
			}
			ResultSet rs = stmt.executeQuery();
			listaEmpleadores = new ArrayList<Empresa>();
			while (rs.next()) {
				Empresa empleador = new Empresa(rs.getString("cuit"), rs
						.getString("sucursal"), rs.getString("descripcion"));
				listaEmpleadores.add(empleador);
			}
		} catch (Exception e) {
			_log.debug(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return listaEmpleadores;
	}

	
	
	public List<Farmacia> getEmpleadoresFarmacia(String cuit) {
		Connection con = null;
		CallableStatement stmt = null;
		List<Farmacia> listaFarmacias = null;
		Farmacia farmacia=null;
		_log.debug("CUIT: " + cuit );
		try {
			String sql = "{call trae_empleadores_farmacia_afip(?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());

			stmt.setString(1, cuit != null ? (cuit.trim().equals("") ? null
					: cuit.toUpperCase()) : cuit);
			ResultSet rs = stmt.executeQuery();
			listaFarmacias = new ArrayList<Farmacia>();
			while (rs.next()) {
				Empresa empleador = new Empresa(rs.getString("cuit_farmacia"), rs
						.getString("sucursal"), rs.getString("descripcion"));
				        if (rs.getString("id_farmacia")==null){
				        	farmacia = (new Farmacia(0,"Existe Cuit pero no Farmacia"));
				        }else{
				        	farmacia = (new Farmacia(rs.getInt("id_farmacia"),rs.getString("descripcion")));	
				        }				        
				        farmacia.setEmpresa(empleador); 
				        listaFarmacias.add(farmacia);
			}
		} catch (Exception e) {
			_log.debug(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return listaFarmacias ;
	}
	
	public List<Empresa> getEmpleadores(String cuit, String razon,
			int pageStart, int pageEnd) {
		Connection con = null;
		CallableStatement stmt = null;
		List<Empresa> listaEmpleadores = null;
		try {
			_log.debug("CUIT: " + cuit + "Razon Soc.: "+ razon);
			
			String sql = "{call trae_empleadores(?,?,?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());

			if (StringUtils.checkEmpty(cuit)) {
				stmt.setNull(1, Types.VARCHAR);
			} else {
				stmt.setString(1, cuit);
			}
			if (StringUtils.checkEmpty(razon)) {
				stmt.setNull(2, Types.VARCHAR);
			} else {
				stmt.setString(2, razon.toUpperCase());
			}
			// stmt.setInt(3,pageEnd);
			stmt.setInt(3, 0);
			ResultSet rs = stmt.executeQuery();
			listaEmpleadores = new ArrayList<Empresa>();
			while (rs.next()) {
				Empresa empleador = new Empresa(rs.getString("cuit"), rs
						.getString("sucursal"), rs.getString("descripcion"));
				listaEmpleadores.add(empleador);
			}
		} catch (Exception e) {
			_log.debug(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return listaEmpleadores;
	}

	public List<ObraSocialCampo> getObrasSocialesAnteriores() {
		Connection con = null;
		CallableStatement stmt = null;
		List<ObraSocialCampo> listaObraSocialCampo = null;
		try {
			listaObraSocialCampo = new ArrayList<ObraSocialCampo>();
			String sql = "{call trae_obras_sociales()}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			ResultSet rs = stmt.executeQuery();
			listaObraSocialCampo = new ArrayList<ObraSocialCampo>();
			while (rs.next()) {
				ObraSocialCampo obraSocial = new ObraSocialCampo(rs
						.getInt("codigo"), rs.getString("razon"));
				listaObraSocialCampo.add(obraSocial);
			}
		} catch (Exception e) {
			_log.debug(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return listaObraSocialCampo;
	}

	public List<Nacionalidad> getNacionalidades() {
		Connection con = null;
		List<Nacionalidad> listaNaciones = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call trae_nacionalidades()}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			ResultSet rs = stmt.executeQuery();
			listaNaciones = new ArrayList<Nacionalidad>();
			while (rs.next()) {
				Nacionalidad nacion = new Nacionalidad(rs.getInt("id"), rs
						.getString("detalle"),rs.getInt("id_sssuper"));
				listaNaciones.add(nacion);
			}
		} catch (Exception e) {
			_log.debug(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return listaNaciones;
	}

	public List<Provincia> getProvincias() {
		Connection con = null;
		List<Provincia> listaProvincias = null;
		CallableStatement stmt = null;
		Provincia provincia = null;
		try {
			String sql = "{call trae_provincias()}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			ResultSet rs = stmt.executeQuery();
			listaProvincias = new ArrayList<Provincia>();
			while (rs.next()) {
				provincia = Provincia.getMapping(rs);
				listaProvincias.add(provincia);
			}
		} catch (Exception e) {
			_log.debug(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return listaProvincias;
	}
	
	public List<Pais> getPaises() {
		Connection con = null;
		List<Pais> listaPaises= null;
		CallableStatement stmt = null;
		try {
			String sql = "{call trae_paises()}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			ResultSet rs = stmt.executeQuery();
			listaPaises = new ArrayList<Pais>();
			while (rs.next()) {
				Pais pais = new Pais(rs.getInt("id_pais"),
						rs.getString("detalle"));
				listaPaises.add(pais);
			}
		} catch (Exception e) {
			_log.debug(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return listaPaises;
	}

	
	
	public List<OpcionesPrestacion> getOpcionesPrestacion(String codigoPrestacion ) {
		Connection con = null;
		List<OpcionesPrestacion> listaOpcionesPrestacion = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call autorizaciones.trae_opciones_prestacion(?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			
			if (codigoPrestacion ==null ) {
				stmt.setNull(1, Types.VARCHAR);
			} else {
				stmt.setString(1, codigoPrestacion);
			}
			ResultSet rs = stmt.executeQuery();
			listaOpcionesPrestacion= new ArrayList<OpcionesPrestacion >();
			while (rs.next()) {
				OpcionesPrestacion opcionPrestacion = new OpcionesPrestacion  ( rs.getInt("opc_nom_id"), rs.getString("opc_nom_descripcion_opcion"), rs.getString("opc_nom_codigo_prestacion") );
				listaOpcionesPrestacion.add(opcionPrestacion );
			}
		} catch (Exception e) {
			_log.debug(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return listaOpcionesPrestacion;
	}

	public List<EstadosReclamosPrestacionales> getEstadosReclamosPrestacionales() {
		Connection con = null;
		List<EstadosReclamosPrestacionales> listaEstadosReclamos= null;
		CallableStatement stmt = null;
		try {
			String sql = "{call autorizaciones.trae_estadosreclamosprestaciones()}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			ResultSet rs = stmt.executeQuery();
			listaEstadosReclamos= new ArrayList<EstadosReclamosPrestacionales >();
			while (rs.next()) {
				EstadosReclamosPrestacionales estadoreclamo = new EstadosReclamosPrestacionales ( rs.getInt("id"), rs.getString("descripcion"), rs.getString("codigo") );
				listaEstadosReclamos.add(estadoreclamo);
			}
		} catch (Exception e) {
			_log.debug(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return listaEstadosReclamos;
	}
	
	public List<ReclamosPrestacionalesRevisionEstado> getReclamosPrestacionalesRevisionEstado() {
		Connection con = null;
		List<ReclamosPrestacionalesRevisionEstado> listaEstadosRevision= null;
		CallableStatement stmt = null;
		try {
			String sql = "{call autorizaciones.traer_reclamos_prestacionales_revision_estado()}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			ResultSet rs = stmt.executeQuery();
			listaEstadosRevision= new ArrayList<ReclamosPrestacionalesRevisionEstado>();
			while (rs.next()) {
				ReclamosPrestacionalesRevisionEstado estadoreclamo = new ReclamosPrestacionalesRevisionEstado ( rs.getInt("id"), rs.getString("descripcion") );
				listaEstadosRevision.add(estadoreclamo);
			}
		} catch (Exception e) {
			_log.debug(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return listaEstadosRevision;
	}
	
	
	public List<ReclamosPrestacionalesIntegracion> getReclamosPrestacionalesIntegracion() {
		Connection con = null;
		List<ReclamosPrestacionalesIntegracion> listaIntegracion= null;
		CallableStatement stmt = null;
		try {
			String sql = "{call autorizaciones.traer_reclamos_prestacionales_integracion()}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			ResultSet rs = stmt.executeQuery();
			listaIntegracion= new ArrayList<ReclamosPrestacionalesIntegracion>();
			while (rs.next()) {
				ReclamosPrestacionalesIntegracion integracion = new ReclamosPrestacionalesIntegracion ( rs.getInt("id"), rs.getString("descripcion"), rs.getString("descripcion_larga")  );
				listaIntegracion.add(integracion);
			}
		} catch (Exception e) {
			_log.debug(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return listaIntegracion;
	}

	public List<TiposDeGestionReclamosPrestacionales> getTipoGestionclamosPrestacionales() {
		Connection con = null;
		List<TiposDeGestionReclamosPrestacionales> listaTipoGestionReclamosPrestacionales= null;
		CallableStatement stmt = null;
		try {
			String sql = "{call autorizaciones.trae_tipos_de_gestionreclamosprestaciones()}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			ResultSet rs = stmt.executeQuery();
			listaTipoGestionReclamosPrestacionales= new ArrayList<TiposDeGestionReclamosPrestacionales>();
			while (rs.next()) {
				TiposDeGestionReclamosPrestacionales estadoreclamo = new TiposDeGestionReclamosPrestacionales ( rs.getInt("id"), rs.getString("descripcion") );
				listaTipoGestionReclamosPrestacionales.add(estadoreclamo);
			}
		} catch (Exception e) {
			_log.debug(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return listaTipoGestionReclamosPrestacionales;
	}
	
	public List<TiposDeSituacionesMedicas> getTipoSituacionesMedicas() {
		Connection con = null;
		List<TiposDeSituacionesMedicas> listaDeTiposDeSituacionesMedicas = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call trae_tipos_de_situacionesmedicas()}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			ResultSet rs = stmt.executeQuery();
			listaDeTiposDeSituacionesMedicas= new ArrayList<TiposDeSituacionesMedicas>();
			while (rs.next()) {
				TiposDeSituacionesMedicas situacionMedica = new TiposDeSituacionesMedicas( rs.getInt("id"),rs.getString("codigo") , rs.getString("descripcion") );
				listaDeTiposDeSituacionesMedicas.add(situacionMedica );
			}
		} catch (Exception e) {
			_log.debug(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return listaDeTiposDeSituacionesMedicas;
	}
	
	
	
	public List<Localidad> getLocalidades() {
		Connection con = null;
		List<Localidad> listaLocalidades = null;
		CallableStatement stmt = null;
		Localidad localidad;
		try {
			String sql = "{call trae_localidades()}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			ResultSet rs = stmt.executeQuery();
			listaLocalidades = new ArrayList<Localidad>();
			while (rs.next()) {
				localidad = Localidad.getMappingSSS(rs);
				listaLocalidades.add(localidad);
			}
		} catch (Exception e) {
			_log.debug(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return listaLocalidades;
	}

	public List<Localidad> getLocalidadesPorProvincia(Integer idProvincia, Integer idProvinciaSSS) {
		Connection con = null;
		List<Localidad> listaLocalidades = null;
		CallableStatement stmt = null;
		Localidad localidad;
		try {
			String sql = "{call trae_localidades_por_provincia(?,?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			if(idProvincia != null){
				stmt.setInt(1, idProvincia);
				stmt.setNull(2, Types.INTEGER);
			}else{
				stmt.setNull(1, Types.INTEGER);
				stmt.setInt(2, idProvinciaSSS);
			}
			ResultSet rs = stmt.executeQuery();
			listaLocalidades = new ArrayList<Localidad>();
			while (rs.next()) {
				localidad = Localidad.getMappingSSS(rs);
				listaLocalidades.add(localidad);
			}
		} catch (Exception e) {
			_log.debug(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return listaLocalidades;
	}
	
	public List<Localidad> getLocalidadesPorCodPostal(int cp) {
		Connection con = null;
		List<Localidad> listaLocalidades = null;
		CallableStatement stmt = null;
		Localidad localidad;
		try {
			String sql = "{call trae_localidades_por_cp(?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, cp);
			ResultSet rs = stmt.executeQuery();
			listaLocalidades = new ArrayList<Localidad>();
			while (rs.next()) {
				localidad = Localidad.getMappingSSS(rs);
				listaLocalidades.add(localidad);
			}
		} catch (Exception e) {
			_log.debug(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return listaLocalidades;
	}

	public List<TipoAporte> getTiposAporte() {
		Connection con = null;
		List<TipoAporte> tiposAporte = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call trae_tipos_aporte()}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			ResultSet rs = stmt.executeQuery();
			tiposAporte = new ArrayList<TipoAporte>();
			while (rs.next()) {
				TipoAporte tipoAporte = TipoAporte.getMapping(rs);
				tiposAporte.add(tipoAporte);
			}
		} catch (Exception e) {
			_log.debug(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return tiposAporte;
	}

	public List<TipoPago> getTiposPagoContratos() {
		Connection con = null;
		List<TipoPago> tiposPago = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call trae_tipos_pago_contratos()}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			ResultSet rs = stmt.executeQuery();
			tiposPago = new ArrayList<TipoPago>();
			while (rs.next()) {
//				TipoPago tipoPago = 					
//						new TipoPago(rs.getInt("id"), rs
//						.getString("detalle"));				
				TipoPago tipoPago = TipoPago.getMapping("", rs);
				
				tiposPago.add(tipoPago);
			}
		} catch (Exception e) {
			_log.debug(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return tiposPago;
	}

	public List<TercerizadoraServicio> getTercerizadoraServicios() {
		Connection con = null;
		List<TercerizadoraServicio> tercerizadoras = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call trae_tercerizadoras()}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			ResultSet rs = stmt.executeQuery();
			tercerizadoras = new ArrayList<TercerizadoraServicio>();
			while (rs.next()) {
				TercerizadoraServicio tercerizadora = new TercerizadoraServicio(
						rs.getString("id_tercerizadora"), rs
								.getString("descripcion"));
				tercerizadoras.add(tercerizadora);
			}
		} catch (Exception e) {
			_log.debug(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return tercerizadoras;
	}

	public List<TercerizadoraServicio> getTercerizadoraServiciosPorConvenios() {
		Connection con = null;
		List<TercerizadoraServicio> tercerizadoras = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call trae_tercerizadoras_convenios()}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			ResultSet rs = stmt.executeQuery();
			tercerizadoras = new ArrayList<TercerizadoraServicio>();
			while (rs.next()) {
				TercerizadoraServicio tercerizadora = TercerizadoraServicio
						.getMapping(rs, "");
				tercerizadoras.add(tercerizadora);
			}
		} catch (Exception e) {
			_log.debug(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return tercerizadoras;
	}

	public List<Documento> getDocumentos() {
		Connection con = null;
		List<Documento> documentos = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call trae_documentos()}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			ResultSet rs = stmt.executeQuery();
			documentos = new ArrayList<Documento>();
			while (rs.next()) {
				Documento documento = new Documento(rs.getInt("id_documento"),
						rs.getString("descripcion"), rs.getInt("id_motivo_baja"));
				documentos.add(documento);
			}
		} catch (Exception e) {
			_log.debug(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return documentos;
	}

	public List<Documento> getDocumentosActualizanAfiliado() {
		Connection con = null;
		List<Documento> documentos = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call trae_documentos_actualizan_afiliado()}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			ResultSet rs = stmt.executeQuery();
			documentos = new ArrayList<Documento>();
			while (rs.next()) {
				Documento documento = new Documento(rs.getInt("id_documento"),
						rs.getString("descripcion"));
				documentos.add(documento);
			}
		} catch (Exception e) {
			_log.debug(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return documentos;
	}

	public List<Documento> getDocumentosDiscapacidad() {
		Connection con = null;
		List<Documento> documentos = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call trae_documentos_discapacidad()}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			ResultSet rs = stmt.executeQuery();
			documentos = new ArrayList<Documento>();
			while (rs.next()) {
				Documento documento = new Documento(rs.getInt("id_documento"),
						rs.getString("descripcion"));
				documentos.add(documento);
			}
		} catch (Exception e) {
			_log.debug(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return documentos;
	}

	public List<CieDiez> getTraeListadoCieDiez() {
		Connection con = null;
		List<CieDiez> cie = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call trae_listado_cie_diez()}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			ResultSet rs = stmt.executeQuery();
			cie = new ArrayList<CieDiez>();
			while (rs.next()) {
				CieDiez cieDiez = new CieDiez(rs.getString("codigo"), rs
						.getString("descripcion"));
				cie.add(cieDiez);
			}
		} catch (Exception e) {
			_log.debug(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return cie;
	}

	public List<MotivoBaja> getMotivosBaja() {
		Connection con = null;
		List<MotivoBaja> motivosBaja = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call trae_motivos_baja()}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			ResultSet rs = stmt.executeQuery();
			motivosBaja = new ArrayList<MotivoBaja>();
			while (rs.next()) {
				MotivoBaja motivoBaja = new MotivoBaja(rs
						.getInt("id_motivo_baja"), rs.getString("descripcion"),
						rs.getInt("meses_a_baja"));
				motivosBaja.add(motivoBaja);
			}
		} catch (Exception e) {
			_log.debug(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return motivosBaja;
	}

	public List<Motivo> getMotivosDebito() {
		Connection con = null;
		List<Motivo> motivosDebito = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call trae_motivos_debito()}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			ResultSet rs = stmt.executeQuery();
			motivosDebito = new ArrayList<Motivo>();
			while (rs.next()) {
				Motivo motivoDebito = new Motivo(rs.getInt("id_motivo_debito"),
						rs.getString("descripcion"));
				motivosDebito.add(motivoDebito);
			}
		} catch (Exception e) {
			_log.debug(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return motivosDebito;
	}

	public List<CategoriaLaboral> getCategoriasLaborales() {
		Connection con = null;
		List<CategoriaLaboral> categorias = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call trae_categorias_laborales()}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			ResultSet rs = stmt.executeQuery();
			categorias = new ArrayList<CategoriaLaboral>();
			while (rs.next()) {
				CategoriaLaboral categoria = new CategoriaLaboral(rs
						.getInt("id"), rs.getString("detalle"));
				categorias.add(categoria);
			}
		} catch (Exception e) {
			_log.debug(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return categorias;
	}

	public List<SituacionRevista> getSituacionRevista() {
		Connection con = null;
		List<SituacionRevista> situaciones = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call trae_situaciones_revista()}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			ResultSet rs = stmt.executeQuery();
			situaciones = new ArrayList<SituacionRevista>();
			while (rs.next()) {
				SituacionRevista situacion = new SituacionRevista(rs
						.getInt("id"), rs.getString("detalle"));
				situaciones.add(situacion);
			}
		} catch (Exception e) {
			_log.debug(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return situaciones;
	}

	public List<Plan> getPlanes() {
		Connection con = null;
		List<Plan> planes = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call trae_planes()}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			ResultSet rs = stmt.executeQuery();
			planes = new ArrayList<Plan>();
			while (rs.next()) {
				Plan plan = new Plan(rs.getInt("id_plan"), rs
						.getString("descripcion"),rs.getBoolean("uoma"),rs.getBoolean("ospim"),rs.getBoolean("amtima"),rs.getBoolean("molinero"));
				planes.add(plan);
			}
		} catch (Exception e) {
			_log.debug(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return planes;
	}

	public List<RamoEmpresa> getRamosEmpresa() {
		Connection con = null;
		List<RamoEmpresa> ramos = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call trae_ramos_empresa()}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			ResultSet rs = stmt.executeQuery();
			ramos = new ArrayList<RamoEmpresa>();
			while (rs.next()) {
				RamoEmpresa ramo = RamoEmpresa.getMapping(rs);
				ramos.add(ramo);
			}
		} catch (Exception e) {
			_log.debug(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return ramos;
	}

	public List<TipoMovBcrio> getTipoMovBcrio(Date fecha, int entidad) {
		Connection con = null;
		List<TipoMovBcrio> tipos = null;
		CallableStatement stmt = null;
		String sql =null;
		try {
			if(entidad==WebKeysGlobal.AMTIMA){
				sql = "{call trae_tipos_mov_bcrios_amtima(?)}";
			}else if(entidad==WebKeysGlobal.OSPIM){
				sql = "{call trae_tipos_mov_bcrios(?)}";
			}else if(entidad==WebKeysGlobal.UOMA){
				sql = "{call uoma.trae_tipos_mov_bcrios_uoma(?)}";
			}
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setDate(1, new java.sql.Date(fecha.getTime()));
			ResultSet rs = stmt.executeQuery();
			tipos = new ArrayList<TipoMovBcrio>();
			while (rs.next()) {
				TipoMovBcrio tipo = new TipoMovBcrio(rs.getInt("id_tipo_mov"),
						rs.getString("descripcion"));
				tipos.add(tipo);
			}
		} catch (Exception e) {
			_log.debug(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return tipos;
	}

	public List<TipoMovBcrio> getTipoMovBcrio(Date fechaDesde, Date fechaHasta, int entidad) {
		Connection con = null;
		List<TipoMovBcrio> tipos = null;
		CallableStatement stmt = null;
		try {
			String sql = null;
			if(entidad==WebKeysGlobal.AMTIMA){
				sql = "{call trae_tipos_mov_bcrios_por_fechas_amtima(?, ?)}";
			}else if(entidad==WebKeysGlobal.OSPIM){
				sql = "{call trae_tipos_mov_bcrios_por_fechas(?, ?)}";	
			}else if(entidad==WebKeysGlobal.UOMA){
				sql = "{call uoma.trae_tipos_mov_bcrios_por_fechas_uoma(?, ?)}";
			}
				
			
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setDate(1, new java.sql.Date(fechaDesde.getTime()));
			stmt.setDate(2, new java.sql.Date(fechaHasta.getTime()));
			ResultSet rs = stmt.executeQuery();
			tipos = new ArrayList<TipoMovBcrio>();
			while (rs.next()) {
				TipoMovBcrio tipo = TipoMovBcrio.getMapping(rs);
				tipos.add(tipo);
			}
		} catch (Exception e) {
			_log.debug(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return tipos;
	}

	public List<CuentaBancaria> getCtasBcrias() {
		Connection con = null;
		List<CuentaBancaria> cuentas = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call trae_ctas_bcrias()}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			ResultSet rs = stmt.executeQuery();
			cuentas = new ArrayList<CuentaBancaria>();
			while (rs.next()) {
				CuentaBancaria cta = new CuentaBancaria(rs
						.getInt("id_cuenta_bcria"), rs.getInt("nro_cuenta"), rs
						.getInt("sucursal"), rs.getString("descripcion"), rs
						.getInt("id_banco"), rs.getString("descripcion_banco"), rs.getString("entidad"));
				cuentas.add(cta);
			}
		} catch (Exception e) {
			_log.debug(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return cuentas;
	}
	
	public CuentaBancaria getCtasBcriasById(Integer id) {
		Connection con = null;
		List<CuentaBancaria> cuentas = null;
		CuentaBancaria cta =null;
		CallableStatement stmt = null;
		try {
			String sql = "{call trae_ctas_bcrias_by_id(?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, id);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
			   cta = new CuentaBancaria(rs
						.getInt("id_cuenta_bcria"), rs.getInt("nro_cuenta"), rs
						.getInt("sucursal"), rs.getString("descripcion"), rs
						.getInt("id_banco"), rs.getString("descripcion_banco"), rs.getString("entidad"));
				PlanCuentas pc = new PlanCuentas(rs.getInt("id_plan_cuenta"));
			    cta.setCuentaAsociada(pc);
			}
		} catch (Exception e) {
			_log.debug(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return cta;
	}

	public List<Chequera> getChequeras() {
		Connection con = null;
		List<Chequera> chequeras = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call trae_chequeras()}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			ResultSet rs = stmt.executeQuery();
			chequeras = new ArrayList<Chequera>();
			while (rs.next()) {
				Chequera cta = new Chequera(rs.getInt("id_chequera"), rs
						.getInt("id_cuenta"), rs.getString("descripcion"));
				chequeras.add(cta);
			}
		} catch (Exception e) {
			_log.debug(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return chequeras;
	}

	public List<TipoTrxBancaria> getTiposTrxBancarias(int entidad) {
		Connection con = null;
		List<TipoTrxBancaria> trxs = null;
		CallableStatement stmt = null;
		String sql =null;
		try {
			//VER POR QUE TRX HAY QUE CAMBIARLA...
			if(entidad==WebKeysGlobal.AMTIMA){
				sql = "{call trae_trx_bcria()}";
			}else{
				sql = "{call trae_trx_bcria()}";
			}
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			ResultSet rs = stmt.executeQuery();
			trxs = new ArrayList<TipoTrxBancaria>();
			while (rs.next()) {
				TipoTrxBancaria trx = new TipoTrxBancaria(rs
						.getInt("id_tipo_transaccion"), rs
						.getString("descripcion"));
				trxs.add(trx);
			}
		} catch (Exception e) {
			_log.debug(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return trxs;
	}

	public List<String> getUsuariosAltaReintegros() {
		Connection con = null;
		ArrayList<String> listaUsuarios = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call trae_usuarios_alta_reintegros()}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			ResultSet rs = stmt.executeQuery();
			listaUsuarios = new ArrayList<String>();
			while (rs.next()) {
				String usuario = rs.getString("username");
				listaUsuarios.add(usuario);
			}
		} catch (Exception e) {
			_log.debug(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return listaUsuarios;
	}

	public List<String> getUsuariosAltaReintegrosFarmacia() {
		Connection con = null;
		ArrayList<String> listaUsuarios = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call trae_usuarios_alta_reintegros_farmacia()}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			ResultSet rs = stmt.executeQuery();
			listaUsuarios = new ArrayList<String>();
			while (rs.next()) {
				String usuario = rs.getString("username");
				listaUsuarios.add(usuario);
			}
		} catch (Exception e) {
			_log.debug(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return listaUsuarios;
	}

	public List<Banco> getBancos() {
		Connection con = null;
		ArrayList<Banco> lista = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call trae_bancos()}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			ResultSet rs = stmt.executeQuery();
			lista = new ArrayList<Banco>();
			while (rs.next()) {
				lista.add(Banco.getMapping(rs));
			}
		} catch (Exception e) {
			_log.debug(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return lista;
	}

	public List<Estado> getEstadosEfectivo() {
		Connection con = null;
		CallableStatement stmt = null;
		List<Efectivo.Estado> list = new ArrayList<Efectivo.Estado>();
		try {
			String sql = "{call trae_efectivo_estados()}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				list.add(Efectivo.Estado.getMapping(rs));
			}
		} catch (SQLException e) {
			_log.error("Error al traer estados efectivo", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return list;
	}

	public List<TipoMovExtractoBancario> getTiposMovExtractoBancario() {
		Connection con = null;
		CallableStatement stmt = null;
		List<TipoMovExtractoBancario> list = new ArrayList<TipoMovExtractoBancario>();
		try {
			String sql = "{call trae_tipos_movimiento_extracto_bancario()}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				list.add(TipoMovExtractoBancario.getMapping(rs));
			}
		} catch (SQLException e) {
			_log.error("Error al traer TipoMovExtractoBancario", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return list;
	}

	public List<Concepto> getConceptoEgreso(Date fecha, int entidad) {
		Connection con = null;
		CallableStatement stmt = null;
		List<Concepto> list = new ArrayList<Concepto>();
		String sql ="{call trae_concepto_egreso(?)}";
		try {
			if(entidad==WebKeysGlobal.AMTIMA){
				 sql =	"{call trae_concepto_egreso_amtima(?)}";
			}else if(entidad==WebKeysGlobal.UOMA){
				sql =	"{call uoma.trae_concepto_egreso_uoma(?)}";
			}
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setDate(1, new java.sql.Date(fecha.getTime()));
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				Concepto conc=Concepto.getMapping(rs);
				if(entidad==WebKeysGlobal.UOMA){
					conc.setIdSeccional(rs.getInt("id_seccional"));
				}
				list.add(conc);
			}
		} catch (SQLException e) {
			_log.error("Error al traer trae_concepto_comprobante", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return list;
	}

	public List<Concepto> getConceptoLiquidacion(Date fecha) {
		Connection con = null;
		CallableStatement stmt = null;
		List<Concepto> list = new ArrayList<Concepto>();
		try {
			String sql = "{call trae_concepto_liquidacion(?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setDate(1, new java.sql.Date(fecha.getTime()));
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				list.add(Concepto.getMapping(rs));
			}
		} catch (SQLException e) {
			_log.error("Error al traer trae_concepto_liquidacion", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return list;
	}

	public List<Concepto> getConceptoIngreso(Date fecha, int entidad) {
		Connection con = null;
		CallableStatement stmt = null;
		List<Concepto> list = new ArrayList<Concepto>();
		try {
			String sql ="{call trae_concepto_ingreso(?)}";;
			if(entidad==WebKeysGlobal.AMTIMA){
				sql = "{call trae_concepto_ingreso_amtima(?)}";
			}else if(entidad==WebKeysGlobal.UOMA){
				sql = "{call uoma.trae_concepto_ingreso_uoma(?)}";
			}
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setDate(1, new java.sql.Date(fecha.getTime()));
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				list.add(Concepto.getMapping(rs));
			}
		} catch (SQLException e) {
			_log.error("Error al traer trae_concepto_ingreso", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return list;
	}
	
	public List<Concepto> getConceptoIngreso(Date fecha, String cuit, String sucu, int idSeccional, int entidad) {
		Connection con = null;
		CallableStatement stmt = null;
		List<Concepto> list = new ArrayList<Concepto>();
		try {
			String sql ="{call trae_concepto_ingreso(?,?,?,?)}";;
			if(entidad==WebKeysGlobal.AMTIMA){
				sql = "{call trae_concepto_ingreso_amtima(?,?,?,?)}";
			}else if(entidad==WebKeysGlobal.UOMA){
				sql = "{call uoma.trae_concepto_ingreso_uoma(?,?,?,?)}";
			}
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setDate(1, new java.sql.Date(fecha.getTime()));
			stmt.setString(2, cuit);
			stmt.setString(3, cuit!=null && cuit.equals("30531143856")?"000":sucu);
			if(idSeccional>0){
				stmt.setInt(4, idSeccional);
			}else{
				stmt.setNull(4, Types.INTEGER);
			}
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				Concepto conc=Concepto.getMapping(rs);
				String tipo=rs.getString("compro_tipo_antic");
				if(null!=tipo){
					Anticipo ant=new Anticipo();
					Comprobante comp=new Comprobante();
					comp.setTipoComprobante(tipo);
					comp.setNroComprobante(rs.getString("compro_nro_antic"));
					comp.setImporteComprobante(rs.getBigDecimal("saldo_antic_numeric"));
					comp.setCuit(cuit);
					comp.setSeccional(new Seccional(idSeccional));
					ant.setAnticipo(comp);
					ant.setNroCuota(rs.getInt("nro_anticipo"));
					conc.setAnticipo(ant);
				}
				list.add(conc);
			}
		} catch (SQLException e) {
			_log.error("Error al traer trae_concepto_ingreso", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return list;
	}

	public List<Concepto> getConceptos(Date fecha, int entidad) {
		Connection con = null;
		CallableStatement stmt = null;
		List<Concepto> list = new ArrayList<Concepto>();
		try {
			String sql = null;
			if(entidad==WebKeysGlobal.AMTIMA){
				sql = "{call trae_conceptos_amtima(?)}";
			}else if(entidad==WebKeysGlobal.UOMA){
				sql = "{call uoma.trae_conceptos_uoma(?)}";
			}else{
				sql = "{call trae_conceptos(?)}";	
			}
			
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setDate(1, new java.sql.Date(fecha.getTime()));
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				list.add(Concepto.getFullMapping(rs, ""));
			}
		} catch (SQLException e) {
			_log.error("Error al traer trae_conceptos", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return list;
	}

	public List<Empresa> getEmpresasIngreso(String cuit, String entidad,
			String sucursal) {
		Connection con = null;
		List<Empresa> listaEmpleadores = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call trae_empresas_ingreso(?,?,?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, StringUtils.checkEmpty(cuit) ? null : cuit);
			stmt.setString(2, StringUtils.checkEmpty(sucursal) ? null
					: sucursal);
			stmt.setString(3, StringUtils.checkEmpty(entidad) ? null : entidad);
			ResultSet rs = stmt.executeQuery();
			listaEmpleadores = new ArrayList<Empresa>();
			while (rs.next()) {
				Empresa empleador = new Empresa(rs.getString("cuit"), rs
						.getString("sucursal"), rs.getString("descripcion"));
				listaEmpleadores.add(empleador);
			}
		} catch (Exception e) {
			_log.debug(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return listaEmpleadores;
	}

	public List<PlanCuentas> getPlanCuentas(Date validoEnFecha, int entidad) {
		Connection con = null;
		List<PlanCuentas> lista = null;
		CallableStatement stmt = null;
		try {
			String sql="{call trae_plan_cuentas(?)}";
			if(entidad==WebKeysGlobal.AMTIMA){
				sql = "{call trae_plan_cuentas_amtima(?)}";
			}else if(entidad==WebKeysGlobal.UOMA){
				sql = "{call uoma.trae_plan_cuentas_uoma(?)}";
			}
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setDate(1, new java.sql.Date(validoEnFecha.getTime()));
			ResultSet rs = stmt.executeQuery();
			lista = new ArrayList<PlanCuentas>();
			while (rs.next()) {
				PlanCuentas empleador = PlanCuentas.getMapping(rs);
				lista.add(empleador);
			}
		} catch (Exception e) {
			_log.debug(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return lista;
	}

	public List<Direccion> getDirecciones(String direccion) {
		Connection con = null;
		List<Direccion> listaDirecciones = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call trae_direcciones(?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, direccion);
			ResultSet rs = stmt.executeQuery();
			listaDirecciones = new ArrayList<Direccion>();
			while (rs.next()) {
				Direccion dire = Direccion.getMapping(rs);
				listaDirecciones.add(dire);
			}
		} catch (Exception e) {
			_log.debug("error al traer direcciones", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return listaDirecciones;
	}

	public List<Direccion> getCodPostales(String calle) {
		Connection con = null;
		List<Direccion> listaDirecciones = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call trae_codpostal(?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, calle);
			ResultSet rs = stmt.executeQuery();
			listaDirecciones = new ArrayList<Direccion>();
			while (rs.next()) {
				Direccion dire = Direccion.getMappingComplete(rs);
				listaDirecciones.add(dire);
			}
		} catch (Exception e) {
			_log.debug("error al traer códigos postales de la CABA", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return listaDirecciones;
	}

	public List<Concepto> getConceptoEgresoAmtima() {
		Connection con = null;
		CallableStatement stmt = null;
		List<Concepto> list = new ArrayList<Concepto>();
		try {
			String sql = "{call trae_concepto_egreso_amtima()}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				list.add(Concepto.getMapping(rs));
			}
		} catch (SQLException e) {
			_log.error("Error al traer trae_concepto_comprobante", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return list;
	}

	public List<Farmacia> getFarmacias() {
		Connection con = null;
		CallableStatement stmt = null;
		List<Farmacia> list = new ArrayList<Farmacia>();
		try {
			String sql = "{call trae_farmacias_completo()}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				list.add(Farmacia.getMapping(rs));
			}
		} catch (SQLException e) {
			_log.error("Error al traer trae_farmacias", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return list;
	}

	public List<Plan> getPlanesOmint() {
		Connection con = null;
		List<Plan> planes = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call trae_planes_omint()}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			ResultSet rs = stmt.executeQuery();
			planes = new ArrayList<Plan>();
			while (rs.next()) {
				Plan plan = new Plan(rs.getInt("id_plan"), rs.getInt("id_plan_omint"), 
						rs.getString("descripcion"), rs.getString("descripcion_prevencion"), 
						rs.getString("farmacia_prevencion"));
				planes.add(plan);
			}
		} catch (Exception e) {
			_log.debug(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return planes;
	}

	public PlanCuentas getCuentaById(int idInt, Date fecha, int entidad) {
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = null;
			if(entidad==WebKeysGlobal.AMTIMA){
				sql = "{call trae_plan_cuentas_por_id_amtima(?, ?)}";
			}else if(entidad==WebKeysGlobal.UOMA){
				sql = "{call uoma.trae_plan_cuentas_por_id_uoma(?, ?)}";
			}else{
				sql = "{call trae_plan_cuentas_por_id(?, ?)}";	
			}			
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, idInt);
			stmt.setDate(2, new java.sql.Date(fecha.getTime()));
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				return PlanCuentas.getMapping(rs);
			}
		} catch (SQLException e) {
			_log.error("Error al traer trae_conceptos", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return null;
	}

	public List<Concepto> getConceptosEgresoValidosDentroDe(Date fechaDesde,
			Date fechaFin, int entidad) {
		Connection con = null;
		CallableStatement stmt = null;
		List<Concepto> list = new ArrayList<Concepto>();
		try {
			String sql = null;
			if(entidad==WebKeysGlobal.AMTIMA){
				sql = "{call trae_concepto_egreso_valido_dentro_de_amtima(?, ?)}";
			}else if(entidad==WebKeysGlobal.UOMA){
				sql = "{call uoma.trae_concepto_egreso_valido_dentro_de_uoma(?, ?)}";
			}else{
				sql = "{call trae_concepto_egreso_valido_dentro_de(?, ?)}";
			}
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setDate(1, new java.sql.Date(fechaDesde.getTime()));
			stmt.setDate(2, new java.sql.Date(fechaFin.getTime()));
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				Concepto concepto=Concepto.getFullMapping(rs, "");
				if(entidad==WebKeysGlobal.UOMA){
					concepto.setIdSeccional(rs.getInt("id_seccional"));
				}
				list.add(concepto);				
			}
		} catch (SQLException e) {
			_log.error("Error al traer trae_concepto_comprobante", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return list;
	}

	public ListaConcepto getConceptosValidosDentroDe(Date fechaDesde,
			Date fechaFin, int entidad, Integer pagina) {
		Connection con = null;
		CallableStatement stmt = null;
		List<Concepto> list = new ArrayList<Concepto>();
		ListaConcepto listaConceptos=new ListaConcepto();
		try {
			String sql =null;
			if(entidad==WebKeysGlobal.AMTIMA){
				sql = "{call trae_concepto_valido_dentro_de_amtima(?, ?)}";
			}else if(entidad==WebKeysGlobal.UOMA){
				sql = "{call uoma.trae_concepto_valido_dentro_de_uoma(?, ?, ?)}";
			}else {
				sql = "{call trae_concepto_valido_dentro_de(?, ?)}";	
			}
			
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setDate(1, new java.sql.Date(fechaDesde.getTime()));
			stmt.setDate(2, new java.sql.Date(fechaFin.getTime()));
			if(entidad==WebKeysGlobal.UOMA){
				if(null==pagina){
					stmt.setNull(3,Types.INTEGER);
				}else{
					stmt.setInt(3, pagina.intValue());
				}
			}
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				Concepto conc=Concepto.getFullMapping(rs, "");
				if(entidad==WebKeysGlobal.UOMA){
					conc.setIdSeccional(rs.getInt("id_seccional"));
					conc.setSeccional(rs.getString("seccional"));
					conc.setIdSecuencial(rs.getInt("id_secuencial"));
					listaConceptos.setTotalConceptos(rs.getInt("cant_registros"));
				}
				list.add(conc);
			}
		} catch (SQLException e) {
			_log.error("Error al traer trae_concepto_valido_dentro_de", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		listaConceptos.setConceptos(list);
		return listaConceptos;
	}

	public List<PlanCuentas> getPlanCuentasImputables(Date validoEnFecha, int entidad) {
		List<PlanCuentas> cuentas = getPlanCuentas(validoEnFecha, entidad);
		Iterator<PlanCuentas> iterator = cuentas.iterator();
		while (iterator.hasNext()) {
			PlanCuentas cuenta = iterator.next();
			if (!cuenta.isImputable()) {
				iterator.remove();
			}
		}
		return cuentas;
	}

	
	public List<TarjetaAcceso> getTarjetasAccesoVigentes() {
		Connection con = null;
		CallableStatement stmt = null;
		List<TarjetaAcceso> list = new ArrayList<TarjetaAcceso>();
		try {
			String sql = "{call trae_tarjetas_acceso_vigentes()}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				list.add(TarjetaAcceso.getMapping(rs, "tarje_"));
			}
		} catch (SQLException e) {
			_log.error("Error al traer tarjetas de acceso vigentes", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return list;
	}

	public List<UsuarioCorrespondencia> getUsuariosCorrespondenciaVigentes() {
		Connection con = null;
		CallableStatement stmt = null;
		List<UsuarioCorrespondencia> list = new ArrayList<UsuarioCorrespondencia>();
		try {
			String sql = "{call correo.trae_usuarios_correspondencias_vigentes()}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				list.add(UsuarioCorrespondencia.getMapping(rs, "ta_"));
			}
		} catch (SQLException e) {
			_log.error("Error al traer usuarios de correspondencia vigentes", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return list;
	}
	
	public List<ConvenioNacion> getConvenioNac() {
		Connection con = null;
		List<ConvenioNacion> listaConvenioNacion = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call trae_convenio_nacion()}";
			con = ConnectionHelper.getConnectionPortalEmpleadoresV01();
			stmt = con.prepareCall(sql.toString());
			ResultSet rs = stmt.executeQuery();
			listaConvenioNacion = new ArrayList<ConvenioNacion>();
			while (rs.next()) {
				ConvenioNacion convenioNacion = new ConvenioNacion(rs.getString("descripcion"),
						rs.getInt("id"),rs.getInt("cuenta_suc"),rs.getInt("tipo_boleta"),
						rs.getBoolean("ospim"),rs.getBoolean("uoma"), rs.getBoolean("amtima"));
				listaConvenioNacion.add(convenioNacion);
			}
			
		} catch (Exception e) {
			_log.debug("Error al traer cuentas de Convenio Banco Nacion", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return listaConvenioNacion;
	}
	
	public Set<CuentasNacion> getCuentasNac() {
		Connection con = null;
		HashSet<CuentasNacion> listaCuentasNacion = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call trae_convenio_nacion()}";
			con = ConnectionHelper.getConnectionPortalEmpleadoresV01();
			stmt = con.prepareCall(sql.toString());
			ResultSet rs = stmt.executeQuery();
			listaCuentasNacion = new HashSet<CuentasNacion>();
			
			while (rs.next()) {
				CuentasNacion cuentasNacion = new CuentasNacion(rs.getString("descripcion"),
						rs.getInt("id"),rs.getInt("cuenta_suc"),rs.getInt("tipo_boleta"),
						rs.getBoolean("ospim"),rs.getBoolean("uoma"), rs.getBoolean("amtima"));
				listaCuentasNacion.add(cuentasNacion);
			}
			
		} catch (Exception e) {
			_log.debug("Error al traer cuentas de Convenio Banco Nacion", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return listaCuentasNacion;
	}

	public List<UltimosProcesosSisOld> getUltimosPrcesosSisOld(Date fechaArchivo) throws SystemException{
		Connection con = null;
		CallableStatement stmt = null;
		List<UltimosProcesosSisOld> list = null;
		try {
			con = ConnectionHelper.getConnectionPortalEmpleadoresV01();
//			String sql = "{call  trae_ultimos_procesos_sistema_viejo(?)}";
			String sql = "select distinct fecha_proceso from uoma_aportes order by fecha_proceso desc limit 5;";
			stmt = con.prepareCall(sql.toString());		
//			if(null!=fechaArchivo){
//				stmt.setDate(1, new java.sql.Date(fechaArchivo.getTime()));
//			}else{
//				stmt.setNull(1, Types.DATE);
//			}
			ResultSet rs = stmt.executeQuery();
			list = new ArrayList<UltimosProcesosSisOld>();
			while (rs.next()) {
				UltimosProcesosSisOld archivo=UltimosProcesosSisOld.getMapping(rs);
				list.add(archivo);
			}
		} catch (Exception e) {
			_log.error("Error al buscar ultimos procesos sistema viejo", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		
		return list;
	}

	public List<TipoBono> getTiposDeBonos() {
		
		TipoBono tipoBono = null;
		List<TipoBono> tiposBonos = new ArrayList<TipoBono>();
		
		Connection con = null;
		PreparedStatement stmt = null;

		try {
			con = ConnectionHelper.getConnection();

			String sql = "select * from tipos_bono order by tipo_bono";
					
			stmt = con.prepareStatement(sql.toString());

			ResultSet rs = stmt.executeQuery();
				
			while (rs.next()) { 
				
				tipoBono = TipoBono.getMapping(rs);
				
				tiposBonos.add(tipoBono);
			}
			
		} catch (SQLException e) {
			_log.error(e);
		} catch (Exception e) {
			_log.error(e);
		} finally {
			ConnectionHelper.cerrar(con);
			ConnectionHelper.cerrar(stmt);
		}
		
		return tiposBonos;
	}
	
	public List<Parentesco> getParentescos() {
		
		Connection con = null;
		List<Parentesco> listaParent = new ArrayList<Parentesco>();
		CallableStatement stmt = null;
		
		try {
			String sql = "{call trae_parentescos()}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			ResultSet rs = stmt.executeQuery();
			
			while (rs.next()) {
				Parentesco par = Parentesco.getMapping(rs,"par_");
				listaParent.add(par);
			}
		} catch (Exception e) {
			_log.debug("error al traer parentescos", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return listaParent;
	}
	
	public List<EstadoCivil> getEstadosCivil() {
		
		Connection con = null;
		List<EstadoCivil> listaEstadosCiv = new ArrayList<EstadoCivil>();
		CallableStatement stmt = null;
		
		try {
			String sql = "{call trae_estados_civil()}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			ResultSet rs = stmt.executeQuery();
			
			while (rs.next()) {
				EstadoCivil estCiv = EstadoCivil.getMapping(rs,"eciv_");
				listaEstadosCiv.add(estCiv);
			}
		} catch (Exception e) {
			_log.debug("error al traer estados civil", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return listaEstadosCiv;
	}
	
	// / traigo profesion //
	public List<ProfesionPrestador> getProfesion() throws Exception {
		Connection con = null;
		CallableStatement stmt = null;
		List<ProfesionPrestador> profesion = null;
		try {
			String sql = "{call autorizaciones.trae_profesiones()}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			ResultSet rs = stmt.executeQuery();
			profesion = new ArrayList<ProfesionPrestador>();
			while (rs.next()) {
				ProfesionPrestador pos = ProfesionPrestador.getMapping("",rs);
				profesion.add(pos);
			}
		} catch (Exception e) {
			_log.error("Error traer lista profesiones prestadores ", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return profesion;
	}

	// / traigo especialidades //
	public List<EspecialidadPrestador> getEspecialidades() throws Exception {
		Connection con = null;
		CallableStatement stmt = null;
		List<EspecialidadPrestador> listaEspecialidades = null;
		try {
			String sql = "{call autorizaciones.trae_especialidades_prestadores()}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			ResultSet rs = stmt.executeQuery();
			listaEspecialidades = new ArrayList<EspecialidadPrestador>();
			while (rs.next()) {
				EspecialidadPrestador pos = EspecialidadPrestador.getMapping("",rs);
				listaEspecialidades.add(pos);
			}
		} catch (Exception e) {
			_log.error("Error traer lista especialidades para prestadores", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return listaEspecialidades;
	}

	// / traigo sub-especialidades //
	public List<SubEspecialidadPrestador> getSubEspecialidades()
			throws Exception {
		Connection con = null;
		CallableStatement stmt = null;
		List<SubEspecialidadPrestador> listaSubEspecialidades = null;
		try {
			String sql = "{call autorizaciones.trae_sub_especialidades_prestadores()}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			ResultSet rs = stmt.executeQuery();
			listaSubEspecialidades = new ArrayList<SubEspecialidadPrestador>();
			while (rs.next()) {
				SubEspecialidadPrestador subEsp = SubEspecialidadPrestador.getMapping("",rs);
				listaSubEspecialidades.add(subEsp);
			}
		} catch (Exception e) {
			_log.error("Error traer lista sub especialidades para prestadores",e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return listaSubEspecialidades;
	}
			
	public List<TipoNovedad> getTiposNovedadSss() {
		
		Connection con = null;
		List<TipoNovedad> listaTipoNov = new ArrayList<TipoNovedad>();
		CallableStatement stmt = null;
		
		try {
			String sql = "{call novedades_sss.trae_tipos_novedad()}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			ResultSet rs = stmt.executeQuery();
			
			while (rs.next()) {
				TipoNovedad tn = TipoNovedad.getMapping("",rs);
				listaTipoNov.add(tn);
			}
		} catch (Exception e) {
			_log.debug("error al traer tipos novedad sss", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return listaTipoNov;
	}
	
	public List<ArchivoNovedad> getFechasArchivosNovedades(String tipoOrigen) {
		
		Connection con = null;
		List<ArchivoNovedad> lista = new ArrayList<ArchivoNovedad>();
		CallableStatement stmt = null;
		
		try {
			String sql = "{call novedades_sss.trae_archivos_proc_origen(?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, tipoOrigen);
			
			ResultSet rs = stmt.executeQuery();
			
			while (rs.next()) {
				ArchivoNovedad an = ArchivoNovedad.getMapping(rs);
				lista.add(an);
			}
		} catch (Exception e) {
			_log.debug("error al traer archivos procesados por origen", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return lista;
	}
	
    public List<Date> getFechasLiquidacionHistoricaTercerizadoras() {
		
		Connection con = null;
		List<Date> lista = new ArrayList<Date>();
		CallableStatement stmt = null;
		
		try {
			String sql = "{call trae_fechas_liquidacion_historica_tercerizadoras()}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			
			ResultSet rs = stmt.executeQuery();
			
			while (rs.next()) {
				Date fl = rs.getDate("fecha_liq");
				lista.add(fl);
			}
		} catch (Exception e) {
			_log.debug("error al traer archivos procesados por origen", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return lista;
	}
    
    public List<TipoNomenclador> getTiposNomenclador() {
		Connection con = null;
		List<TipoNomenclador> listaTipos = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call trae_tipos_nomenclador()}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			ResultSet rs = stmt.executeQuery();
			listaTipos = new ArrayList<TipoNomenclador>();
			while (rs.next()) {
				TipoNomenclador tipo = TipoNomenclador.getMapping("",rs);
				listaTipos.add(tipo);
			}
		} catch (Exception e) {
			_log.debug("error al traer tipos nomenclador", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return listaTipos;
	}
    
/*    
    public List<Especialidad> getEspecialidades() {
		Connection con = null;
		List<Especialidad> lista = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call trae_especialidades()}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			ResultSet rs = stmt.executeQuery();
			lista = new ArrayList<Especialidad>();
			while (rs.next()) {
				Especialidad tipo = Especialidad.getMapping(rs);
				lista.add(tipo);
			}
		} catch (Exception e) {
			_log.debug("error al traer especialidades", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return lista;
	}
*/   
    public List<ModalidadAtencion> getModalidadAtencion() {
		Connection con = null;
		List<ModalidadAtencion> listaTipos = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call autorizaciones.trae_modalidad_atencion()}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			ResultSet rs = stmt.executeQuery();
			listaTipos = new ArrayList<ModalidadAtencion>();
			while (rs.next()) {
				ModalidadAtencion tipo = ModalidadAtencion.getMapping(rs);
				listaTipos.add(tipo);
			}
		} catch (Exception e) {
			_log.debug("error al traer modalidad atencion", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return listaTipos;
	}
    
    public List<Plan> getPlanesMolineros() {
		Connection con = null;
		List<Plan> planes = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call trae_planes_molineros()}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			ResultSet rs = stmt.executeQuery();
			planes = new ArrayList<Plan>();
			while (rs.next()) {
				Plan plan = new Plan(rs.getInt("id_plan"), rs
						.getString("descripcion"),rs.getBoolean("uoma"),rs.getBoolean("ospim"),rs.getBoolean("amtima"),rs.getBoolean("molinero"));
				planes.add(plan);
			}
		} catch (Exception e) {
			_log.debug(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return planes;
	}
    
    public List<Plan> getPlanesOspim() {
		Connection con = null;
		List<Plan> planes = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call trae_planes_ospim()}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			ResultSet rs = stmt.executeQuery();
			planes = new ArrayList<Plan>();
			while (rs.next()) {
				Plan plan = new Plan(rs.getInt("id_plan"), rs
						.getString("descripcion"),rs.getBoolean("uoma"),rs.getBoolean("ospim"),rs.getBoolean("amtima"),rs.getBoolean("molinero"));
				planes.add(plan);
			}
		} catch (Exception e) {
			_log.debug(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return planes;
	}
    
    public List<Plan> getPlanesSoloOspim() {
		Connection con = null;
		List<Plan> planes = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call trae_planes_solo_ospim()}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			ResultSet rs = stmt.executeQuery();
			planes = new ArrayList<Plan>();
			while (rs.next()) {
				Plan plan = new Plan(rs.getInt("id_plan"), rs
						.getString("descripcion"),rs.getBoolean("uoma"),rs.getBoolean("ospim"),rs.getBoolean("amtima"),rs.getBoolean("molinero"));
				planes.add(plan);
			}
		} catch (Exception e) {
			_log.debug(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return planes;
	}
    
    public List<Especialidad> getEspecialidadesNomenclador() {
		Connection con = null;
		List<Especialidad> lista = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call autorizaciones.trae_especialidades_nomenclador()}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			ResultSet rs = stmt.executeQuery();
			lista = new ArrayList<Especialidad>();
			while (rs.next()) {
				Especialidad tipo = Especialidad.getMapping(rs);
				String tEsp= rs.getString("tipo");
				tipo.setTipoEspecialidad(tEsp);
				lista.add(tipo);
			}
		} catch (Exception e) {
			_log.debug("error al traer especialidades", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return lista;
	}
    
    public List<TipoDiscapacidad> getTraeTiposDiscapacidad() {
		Connection con = null;
		List<TipoDiscapacidad> tiposDisc = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call trae_tipos_discapacidad() }";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			ResultSet rs = stmt.executeQuery();
			tiposDisc = new ArrayList<TipoDiscapacidad>();
			
			while (rs.next()) {
				tiposDisc.add(TipoDiscapacidad.getMapping("", rs));
			}
		} catch (Exception e) {
			_log.debug(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return tiposDisc;
	}
    
    public List<String> getBimestresPorAnio(Date fechaDesde, Date fechaHasta,String clase) {
		Connection con = null;
		List<String> bimestres = null;
		CallableStatement stmt = null;
		try {
			String sql = null;
			sql = "{call autorizaciones.trae_bimestres_por_anio(?, ?,?)}";
			
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setDate(1, new java.sql.Date(fechaDesde.getTime()));
			stmt.setDate(2, new java.sql.Date(fechaHasta.getTime()));
			stmt.setString(3,clase);
			ResultSet rs = stmt.executeQuery();
			bimestres = new ArrayList<String>();
			while (rs.next()) {
				String bimestre = rs.getString("id")+"|"+rs.getString("descripcion");
				bimestres.add(bimestre);
			}
		} catch (Exception e) {
			_log.debug(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return bimestres;
	}
    
    public String getBimestresPorId(Integer id) {
		Connection con = null;
		String bimestre = null;
		CallableStatement stmt = null;
		try {
			String sql = null;
			sql = "{call autorizaciones.trae_bimestres_por_id(?)}";
			
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, id);
			ResultSet rs = stmt.executeQuery();
			bimestre = "";
			while (rs.next()) {
				bimestre = rs.getString("id")+"|"+rs.getString("descripcion")+"|"+rs.getString("fechainicio")+"|"+rs.getString("fechafin");
			}
		} catch (Exception e) {
			_log.debug(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return bimestre;
	}
    
    public List<DrogaPatologia> getDrogasPorPatologia(Integer patologia) {
		Connection con = null;
		List<DrogaPatologia> drogas = null;
		CallableStatement stmt = null;
		try {
			String sql = null;
			sql = "{call autorizaciones.trae_drogas_por_patologia(?)}";
			
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			
			stmt.setInt(1, patologia);
			
			ResultSet rs = stmt.executeQuery();
			drogas = new ArrayList<DrogaPatologia>();
			while (rs.next()) {
				DrogaPatologia dp=new DrogaPatologia();
				dp.setDrogaId(rs.getInt("id_droga"));
				dp.setDrogaDescripcion(rs.getString("descripcion"));
				drogas.add(dp);
			}
		} catch (Exception e) {
			_log.debug(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return drogas;
	}
    
    public List<ModalidadAtencion> getMotivosEstadoSur(Integer idEstado) {
		Connection con = null;
		List<ModalidadAtencion> motivos = null;
		CallableStatement stmt = null;
		try {
			String sql = null;
			sql = "{call autorizaciones.trae_motivos_por_estado_sur(?)}";
			
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			
			stmt.setInt(1, idEstado);
			
			ResultSet rs = stmt.executeQuery();
			motivos = new ArrayList<ModalidadAtencion>();
			while (rs.next()) {
				ModalidadAtencion dp=new ModalidadAtencion();
				dp.setId(rs.getInt("id"));
				dp.setDescripcion(rs.getString("descripcion"));
				motivos.add(dp);
			}
		} catch (Exception e) {
			_log.debug(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return motivos;
	}
    
    public List<String> getUsuariosAltaSeguimientoSur() {
		Connection con = null;
		ArrayList<String> listaUsuarios = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call autorizaciones.trae_usuarios_alta_seguimiento_sur()}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			ResultSet rs = stmt.executeQuery();
			listaUsuarios = new ArrayList<String>();
			while (rs.next()) {
				String usuario = rs.getString("alta_usr");
				listaUsuarios.add(usuario);
			}
		} catch (Exception e) {
			_log.debug(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return listaUsuarios;
	}

    
    public List<ModalidadAtencion> getEstadosSeguimientoSur() {
  		Connection con = null;
  		List<ModalidadAtencion> lista = null;
  		CallableStatement stmt = null;
  		try {
  			String sql = "{call autorizaciones.trae_estados_seguimiento_sur()}";
  			con = ConnectionHelper.getConnection();
  			stmt = con.prepareCall(sql.toString());
  			ResultSet rs = stmt.executeQuery();
  			lista = new ArrayList<ModalidadAtencion>();
  			while (rs.next()) {
  				ModalidadAtencion tipo = ModalidadAtencion.getMapping(rs);
  				lista.add(tipo);
  			}
  		} catch (Exception e) {
  			_log.debug("error al traer estados de seguimiento sur", e);
  		} finally {
  			ConnectionHelper.cerrar(stmt, con);
  		}
  		return lista;
  	}
    
    public List<ModalidadAtencion> getEstadosSeguimientoSurPorEstados(String estados) {
  		Connection con = null;
  		List<ModalidadAtencion> lista = null;
  		CallableStatement stmt = null;
  		try {
  			String sql = "{call autorizaciones.trae_estados_seguimiento_sur_por_estado(?)}";
  			con = ConnectionHelper.getConnection();
  			stmt = con.prepareCall(sql.toString());
  			stmt.setString(1,estados);
  			ResultSet rs = stmt.executeQuery();
  			lista = new ArrayList<ModalidadAtencion>();
  			while (rs.next()) {
  				ModalidadAtencion tipo = ModalidadAtencion.getMapping(rs);
  				lista.add(tipo);
  			}
  		} catch (Exception e) {
  			_log.debug("error al traer estados de seguimiento sur por estado ", e);
  		} finally {
  			ConnectionHelper.cerrar(stmt, con);
  		}
  		return lista;
  	}
    
    public List<ModalidadAtencion> getEstadosOspimSeguimientoSur() {
		Connection con = null;
		List<ModalidadAtencion> lista = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call autorizaciones.trae_estados_ospim_seguimiento_sur()}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			ResultSet rs = stmt.executeQuery();
			lista = new ArrayList<ModalidadAtencion>();
			while (rs.next()) {
				ModalidadAtencion tipo = ModalidadAtencion.getMapping(rs);
				lista.add(tipo);
			}
		} catch (Exception e) {
			_log.debug("error al traer estados de seguimiento sur", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return lista;
	}
    
    public String getSystemConfig(String id) {
		Connection con = null;
		ArrayList<String> lista = null;
		CallableStatement stmt = null;
		String ret="";
		try {
			String sql = "{call trae_system_config(?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, id);
			ResultSet rs = stmt.executeQuery();
			lista = new ArrayList<String>();
			while (rs.next()) {
				String valor = rs.getString("valor");
				lista.add(valor);
			}
			if(lista.size()>0){
				ret=lista.get(0);
			}
		} catch (Exception e) {
			_log.debug(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return ret;
	}
    
    
    public List<Concepto> getConceptosConSeccional(Date fecha, int entidad) {
		Connection con = null;
		CallableStatement stmt = null;
		List<Concepto> list = new ArrayList<Concepto>();
		try {
			String sql = null;
			if(entidad==WebKeysGlobal.AMTIMA){
				sql = "{call trae_conceptos_amtima(?)}";
			}else if(entidad==WebKeysGlobal.UOMA){
				sql = "{call uoma.trae_conceptos_uoma_con_seccional(?)}";
			}else{
			
				sql = "{call trae_conceptos(?)}";	
			}
			
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setDate(1, new java.sql.Date(fecha.getTime()));
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				Concepto concepto = Concepto.getFullMapping(rs, "");
				try{
					Integer idSeccional = rs.getInt("id_seccional");
					concepto.setIdSeccional(idSeccional);
				}catch(Exception e){}
				list.add(concepto);
			}
		} catch (SQLException e) {
			_log.error("Error al traer trae_conceptos", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return list;
	}

    public List<String> getCartillaTipos() {
		Connection con = null;
		List<String> lista = new ArrayList<String>();
		CallableStatement stmt = null;
		try {
			String sql = "{call autorizaciones.cartilla_prevencion_trae_tipo()}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			ResultSet rs = stmt.executeQuery();
			lista = new ArrayList<String>();
			while (rs.next()) {
				String tipo = rs.getString("tipo");
				lista.add(tipo);
			}
		} catch (Exception e) {
			_log.debug("error al traer cartillas tipo", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return lista;
	}
    
    public List<String> getCartillaPlan() {
		Connection con = null;
		List<String> lista = new ArrayList<String>();
		CallableStatement stmt = null;
		try {
			String sql = "{call autorizaciones.cartilla_prevencion_trae_plan()}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			ResultSet rs = stmt.executeQuery();
			lista = new ArrayList<String>();
			while (rs.next()) {
				String tipo = rs.getString("plan");
				lista.add(tipo);
			}
		} catch (Exception e) {
			_log.debug("error al traer cartillas plan", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return lista;
	}
    
    public List<String> getCartillaLocalidad() {
		Connection con = null;
		List<String> lista = new ArrayList<String>();
		CallableStatement stmt = null;
		try {
			String sql = "{call autorizaciones.cartilla_prevencion_trae_localidad()}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			ResultSet rs = stmt.executeQuery();
			lista = new ArrayList<String>();
			while (rs.next()) {
				String tipo = rs.getString("localidad");
				lista.add(tipo);
			}
		} catch (Exception e) {
			_log.debug("error al traer cartillas localidad", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return lista;
	}
    
    public List<String> getCartillaProvincia() {
		Connection con = null;
		List<String> lista = new ArrayList<String>();
		CallableStatement stmt = null;
		try {
			String sql = "{call autorizaciones.cartilla_prevencion_trae_provincia()}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			ResultSet rs = stmt.executeQuery();
			lista = new ArrayList<String>();
			while (rs.next()) {
				String tipo = rs.getString("provincia");
				lista.add(tipo);
			}
		} catch (Exception e) {
			_log.debug("error al traer cartillas provincia", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return lista;
	}
    
    public List<String> getCartillaEspecialidad() {
		Connection con = null;
		List<String> lista = new ArrayList<String>();
		CallableStatement stmt = null;
		try {
			String sql = "{call autorizaciones.cartilla_prevencion_trae_especialidad()}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			ResultSet rs = stmt.executeQuery();
			lista = new ArrayList<String>();
			while (rs.next()) {
				String tipo = rs.getString("especialidad");
				lista.add(tipo);
			}
		} catch (Exception e) {
			_log.debug("error al traer cartillas especialidad", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return lista;
	}
    
    public List<MotivoExcepcion> getMotivosExcepcion() {
		Connection con = null;
		List<MotivoExcepcion> listaTipos = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call autorizaciones.trae_motivos_excepcion()}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			ResultSet rs = stmt.executeQuery();
			listaTipos = new ArrayList<MotivoExcepcion>();
			while (rs.next()) {
				MotivoExcepcion tipo = MotivoExcepcion.getMapping(rs);
				listaTipos.add(tipo);
			}
		} catch (Exception e) {
			_log.debug("error al traer modalidad atencion", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return listaTipos;
	}
    
    public static List<MovimientoBancario> getSaldoCuentasBancariasConformado(Integer idCta) {
    	Connection con = null;
		List<MovimientoBancario> lista = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call trae_saldo_cuenta_bancaria_conformada(?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, idCta);
			ResultSet rs = stmt.executeQuery();
			lista = new ArrayList<MovimientoBancario>();
			while (rs.next()) {
				MovimientoBancario m = new MovimientoBancario();
				m.setFecha_movimiento(rs.getDate("fecha"));
				m.setImporte(rs.getBigDecimal("saldo"));
				lista.add(m);
			}
		} catch (Exception e) {
			_log.debug("error al traer saldo cuenta bancaria", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return lista;
    }
    
    public List<PlanCuentasSSS> getPlanCuentasSSS(int entidad, Integer id, String numero,String descripcion,String tipo) {
		Connection con = null;
		List<PlanCuentasSSS> lista = null;
		CallableStatement stmt = null;
		try {
			String sql="{call trae_plan_cuentas_sss(?,?,?,?)}";
			if(entidad==WebKeysGlobal.AMTIMA){
				sql = "{call trae_plan_cuentas_amtima_sss(?)}";
			}if(entidad==WebKeysGlobal.UOMA){
				sql = "{call uoma.trae_plan_cuentas_uoma_sss(?)}";
			}
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());

			if(id!=null){
					stmt.setInt(1, id);
			}else{
				stmt.setNull(1,Types.INTEGER);
			}
			
			if (null != numero && !numero.trim().equals("")) {
				stmt.setString(2, numero);
			} else {
				stmt.setNull(2, Types.VARCHAR);
			}
			
			if (null != descripcion && !descripcion.trim().equals("")) {
				stmt.setString(3, descripcion);
			} else {
				stmt.setNull(3, Types.VARCHAR);
			}
			
			if (null != tipo && !tipo.trim().equals("")) {
				stmt.setString(4, tipo);
			} else {
				stmt.setNull(4, Types.VARCHAR);
			}
			
			ResultSet rs = stmt.executeQuery();
			lista = new ArrayList<PlanCuentasSSS>();
			while (rs.next()) {
				PlanCuentasSSS pc = PlanCuentasSSS.getMapping(rs);
				lista.add(pc);
			}
		} catch (Exception e) {
			_log.debug(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return lista;
	}
    
    
    public List<Delegacion> getDelegacionesSinSeccional() {
		Connection con = null;
		List<Delegacion> listaDelegaciones = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call trae_delegaciones_sin_seccional()}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			ResultSet rs = stmt.executeQuery();
			listaDelegaciones = new ArrayList<Delegacion>();
			while (rs.next()) {
				Delegacion delegacion = Delegacion.getMapping(rs,"");
				listaDelegaciones.add(delegacion);
			}
		} catch (Exception e) {
			_log.debug("error al traer delegaciones", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return listaDelegaciones;
	}
    
	public Date getMaximoPeriodoActasPorCuit(String cuit,String entidad)
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		int id_liquidacion = 0;
		Date fecha= new Date();
		try {
			String sql ="";
			if("OSPIM".equalsIgnoreCase(entidad)){
			  sql = "{call trae_maxima_fecha_actas_por_cuit(?)}";
			}else{
			  sql = "{call trae_maxima_fecha_actas_no_os_por_cuit(?,?)}";	
			}
			_log.debug("obteniendo conexion");
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, cuit);
			if(!"OSPIM".equalsIgnoreCase(entidad)){
			 stmt.setString(2, entidad);
			} 
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				return fecha = rs.getDate(1);
			}
		} catch (Exception e) {
			_log.error("Error al buscar maximo periodo actas", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return fecha;
	}
	
	public List<CentroCosto> getCentrosDeCostosVigentes(int entidad) throws Exception {
		Connection con = null;
		CallableStatement stmt = null;
		List<CentroCosto> list = new ArrayList<CentroCosto>();
		String sql ="";
		try {
			if(entidad==WebKeysGlobal.AMTIMA){
				sql =   "{call trae_centros_costos_vigentes_amtima()}";
			}else if(entidad==WebKeysGlobal.UOMA){
				sql =	"{call uoma.trae_centros_costos_vigentes()}";
			}else if(entidad==WebKeysGlobal.OSPIM){
				sql =	"{call trae_centros_costos_vigentes()}";
			}
			if(sql.length()>0){
			   con = ConnectionHelper.getConnection();
			   stmt = con.prepareCall(sql.toString());
			   ResultSet rs = stmt.executeQuery();
			   while (rs.next()) {
				   CentroCosto conc=CentroCosto.getMapping(rs);
				   list.add(conc);
			   }
			}
		} catch (SQLException e) {
			_log.error("Error al traer centros costos vigentes", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return list;
	}

	public List<ClaseBase> getTraeDiagnosticos() {
		Connection con = null;
		List<ClaseBase> cie = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call autorizaciones.trae_diagnosticos()}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			ResultSet rs = stmt.executeQuery();
			cie = new ArrayList<ClaseBase>();
			while (rs.next()) {
				ClaseBase cieDiez = new ClaseBase(rs.getString("id"), rs
						.getString("descripcion"));
				cie.add(cieDiez);
			}
		} catch (Exception e) {
			_log.debug(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return cie;
	}
	
	public List<Prestador> getPrestodoresInexistentesMedicacionEspecial(){
		Connection con = null;
		List<Prestador> pre = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call trae_prestadores_inexistentes_med_especial()}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			ResultSet rs = stmt.executeQuery();
			pre = new ArrayList<Prestador>();
			while (rs.next()) {
				Prestador p= new Prestador(rs.getString("cuit"), 0,rs.getString("descripcion"));
				pre.add(p);
			}
		} catch (Exception e) {
			_log.debug(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return pre;
		
	}
	
	public List<ClaseBase> getTarjetasDebitoCreditoEmisores() {
		Connection con = null;
		List<ClaseBase> cie = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call uoma.trae_tarjeta_debito_credito_emisor()}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			ResultSet rs = stmt.executeQuery();
			cie = new ArrayList<ClaseBase>();
			while (rs.next()) {
				ClaseBase cieDiez = new ClaseBase(rs.getString("id"), rs
						.getString("descripcion"));
				cie.add(cieDiez);
			}
		} catch (Exception e) {
			_log.debug(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return cie;
	}
	
	public List<ClaseBase> getSectoresLiquidaciones() {
		Connection con = null;
		List<ClaseBase> lista = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call sectores_liquidaciones()}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			ResultSet rs = stmt.executeQuery();
			lista = new ArrayList<ClaseBase>();
			while (rs.next()) {
				ClaseBase c =new ClaseBase();
				c.setId(rs.getString("sector"));
				c.setDescripcion(rs.getString("descripcion"));
				lista.add(c);
			}
		} catch (Exception e) {
			_log.debug("error al traer sectores liquidaciones", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return lista;
	}
	
	
	public List<AportesMonotributo> getAportesMonotributo(Integer id) {
		Connection con = null;
		List<AportesMonotributo> lista = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call trae_aportes_monotributo(?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			
			if(id!=null){
				stmt.setInt(1, id);
		    }else{
			    stmt.setNull(1,Types.INTEGER);
		    }
			ResultSet rs = stmt.executeQuery();
			lista = new ArrayList<AportesMonotributo>();
			while (rs.next()) {
				AportesMonotributo ap = AportesMonotributo.getMapping(rs);
				lista.add(ap);
			}
		} catch (Exception e) {
			_log.debug("error al traer aportes", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return lista;
	}
	
	
	public List<AportesMonotributo> getCategoriasMonotributo() {
		Connection con = null;
		List<AportesMonotributo> lista = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call trae_categorias_monotributo()}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			
			ResultSet rs = stmt.executeQuery();
			lista = new ArrayList<AportesMonotributo>();
			while (rs.next()) {
				AportesMonotributo ap = new AportesMonotributo();
				ap.setCategoria(rs.getInt("id_categoria"));
				ap.setDescripcion(rs.getString("descripcion"));
				lista.add(ap);
			}
		} catch (Exception e) {
			_log.debug("error al traer categorias monotributo", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return lista;
	}
	
	public List<AportesMonotributoClase> getAportesMonotributoClases(Integer id) {
		Connection con = null;
		List<AportesMonotributoClase> lista = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call trae_aportes_monotributo_clases_by_padre(?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			
			if(id!=null){
				stmt.setInt(1, id);
		    }else{
			    stmt.setNull(1,Types.INTEGER);
		    }
			ResultSet rs = stmt.executeQuery();
			lista = new ArrayList<AportesMonotributoClase>();
			while (rs.next()) {
				AportesMonotributoClase ap = AportesMonotributoClase.getMapping(rs);
				lista.add(ap);
			}
		} catch (Exception e) {
			_log.debug("error al traer aportes", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return lista;
	}
	
	public List<CentroCosto> getSectoresLiquidacionSueldos(String entidad) {
		Connection con = null;
		List<CentroCosto> lista = new ArrayList<CentroCosto>();
		CallableStatement stmt = null;
		try {
			String sql = "{call contabilidad.sectores_liquidacion_sueldos(?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			
			stmt.setString(1, entidad);
			
			ResultSet rs = stmt.executeQuery();
			
			while (rs.next()) {
				CentroCosto cc =new CentroCosto();
				cc.setId(rs.getInt("id"));
				cc.setDescripcion(rs.getString("descripcion"));
				lista.add(cc);				
			}
		} catch (Exception e) {
			_log.debug("error al traer sectores liquidacion sueldos", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return lista;
	}
	
	
	 public List<Plan> getPlanesFacturables() {
			Connection con = null;
			List<Plan> planes = null;
			CallableStatement stmt = null;
			try {
				String sql = "{call trae_planes_facturables()}";
				con = ConnectionHelper.getConnection();
				stmt = con.prepareCall(sql.toString());
				ResultSet rs = stmt.executeQuery();
				planes = new ArrayList<Plan>();
				while (rs.next()) {
					Plan plan = new Plan(rs.getInt("id_plan"), rs
							.getString("descripcion"),rs.getBoolean("uoma"),rs.getBoolean("ospim"),rs.getBoolean("amtima"),rs.getBoolean("molinero"));
					planes.add(plan);
				}
			} catch (Exception e) {
				_log.debug(e);
			} finally {
				ConnectionHelper.cerrar(stmt, con);
			}
			return planes;
	}
	 
	 public List<Parentesco> getParentescosFacturables() {
			
			Connection con = null;
			List<Parentesco> listaParent = new ArrayList<Parentesco>();
			CallableStatement stmt = null;
			
			try {
				String sql = "{call trae_parentescos_facturables()}";
				con = ConnectionHelper.getConnection();
				stmt = con.prepareCall(sql.toString());
				ResultSet rs = stmt.executeQuery();
				
				while (rs.next()) {
					Parentesco par = Parentesco.getMapping(rs,"par_");
					listaParent.add(par);
				}
			} catch (Exception e) {
				_log.debug("error al traer parentescos facturables", e);
			} finally {
				ConnectionHelper.cerrar(stmt, con);
			}
			return listaParent;
	}
	 
	 public List<Provincia> getProvinciasFacturables() {
			Connection con = null;
			List<Provincia> listaProvincias = null;
			CallableStatement stmt = null;
			Provincia provincia = null;
			try {
				String sql = "{call trae_provincias_facturables()}";
				con = ConnectionHelper.getConnection();
				stmt = con.prepareCall(sql.toString());
				ResultSet rs = stmt.executeQuery();
				listaProvincias = new ArrayList<Provincia>();
				while (rs.next()) {
					provincia = Provincia.getMapping(rs);
					listaProvincias.add(provincia);
				}
			} catch (Exception e) {
				_log.debug(e);
			} finally {
				ConnectionHelper.cerrar(stmt, con);
			}
			return listaProvincias;
	} 
	 
	 public List<ReclamosPrestacionalesRevisionEstado> getReclamosPrestacionalesRevisionEstadoAutorizado() {
	
	 Connection con = null;
	 CallableStatement stmt = null;
	
	 List<ReclamosPrestacionalesRevisionEstado> listaEstadosRevision = new ArrayList<ReclamosPrestacionalesRevisionEstado>();
	
	 try {
	
	     String sql =
	         "{call autorizaciones.traer_reclamos_prestacionales_revision_estado_autorizado()}";
	
	     con = ConnectionHelper.getConnection();
	     stmt = con.prepareCall(sql);
	
	     ResultSet rs = stmt.executeQuery();
	
	     while (rs.next()) {
	
	         ReclamosPrestacionalesRevisionEstado estadoReclamo =
	             new ReclamosPrestacionalesRevisionEstado(
	                 rs.getInt("id"),
	                 rs.getString("descripcion")
	             );
	
	         listaEstadosRevision.add(estadoReclamo);
	     }
	
	 } catch (Exception e) {
	
	     _log.error(
	         "Error obteniendo las observaciones especiales de reclamos prestacionales",
	         e
	     );
	
	 } finally {
	
	     ConnectionHelper.cerrar(stmt, con);
	 }
	
	 return listaEstadosRevision;
	}
}