package ar.com.ospim.tesoreria.service;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ar.com.ospim.afip.beans.ReporteDeudaNominaEmpresa;
import ar.com.ospim.tesoreria.beans.FichaBoletaPortal;
import ar.com.ospim.util.ConnectionHelper;


//import com.liferay.ibm.icu.math.BigDecimal;
import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

/**
 * <a href="BusquedaLiquidacionServiceImpl.java.html"><b><i>View
 * Source</i></b></a>
 * 
 * @author Gustavo Fernandez
 * 
 */
public class PortalEmpleadoresServiceImpl {

	private static Log _log = LogFactoryUtil
			.getLog(PortalEmpleadoresServiceImpl.class);
	
	public List<ReporteDeudaNominaEmpresa> getDeudaNominaEmpresa(String cuit,
			Date desde, Date hasta) throws SystemException {
		_log.debug("Obteniendo deuda de empresa " + cuit);
		Connection con = null;
		CallableStatement stmt = null;
		List<ReporteDeudaNominaEmpresa> list = null;
		try {
			con = ConnectionHelper.getConnectionPortalEmpleadoresV01();
			String sql = "{call  reporte_deuda_nomina_empresa (?,?,?)}";
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, cuit);
			stmt.setDate(2, new java.sql.Date(desde.getTime()));
			stmt.setDate(3, new java.sql.Date(hasta.getTime()));
			ResultSet rs = stmt.executeQuery();
			list = new ArrayList<ReporteDeudaNominaEmpresa>();
			while (rs.next()) {
				ReporteDeudaNominaEmpresa deuda = ReporteDeudaNominaEmpresa.getMappingEmpleadores(rs);
				int indexOf = list.indexOf(deuda);
				if (indexOf == -1) {
					list.add(deuda);
				} else {
					list.get(indexOf).getPagos().addAll(deuda.getPagos());
				}
			}
		} catch (Exception e) {
			_log.error("Error al buscar deuda nomina empresa", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		_log.debug("Saliendo de obtencion de deuda nomina empresa " + cuit);
		return list;
	}

	public List<FichaBoletaPortal> getReporteBoletaPortalTodasEmpresas(Date periodoDesde, Date periodoHasta, String cuit_entidad, int idSeccional, boolean consolidado) {

		Connection con = null;
		PreparedStatement stmt = null;
		ArrayList<FichaBoletaPortal> listaFichas= null;
		listaFichas = new ArrayList<FichaBoletaPortal>();
		try {
			
			StringBuffer sql = new StringBuffer("select ");
			if(consolidado){
				 sql.append("empresa_cuit, periodo, razon_soc, camara, sum(remuneracion) as remuneracion, sum(importenoremunerativo) as importenoremunerativo," +
				 		"sum(cast(aportesocialuoma as numeric)) as aportesocialuoma, sum(cast(articulo46 as numeric)) as articulo46," +
				 		"sum(cast(cuotaamtima as numeric)) as cuotaamtima,sum(cast(cuotasocialuoma as numeric)) as cuotasocialuoma, " +
				 		"sum(cast(cuotausufructo as numeric)) as cuotausufructo, sum(cast(adherenteamtima as numeric)) as adherenteamtima");
				 sql.append(", cant_aporte_social_uoma, cant_cuota_soc_uoma , cant_usufructo , cant_art_46, cant_cuota_amtima ,  cant_adh_amtima, cant_total_declarada");
			}else{
				sql.append("*");
			}
			sql.append(" from reporte_boletas_empleadores_todas(?,?,?,?)");
			if(consolidado){
				sql.append(" group by periodo, empresa_cuit, razon_soc, camara");
				sql.append(", cant_aporte_social_uoma, cant_cuota_soc_uoma , cant_usufructo , cant_art_46, cant_cuota_amtima ,  cant_adh_amtima, cant_total_declarada"  );
				sql.append(" order by empresa_cuit, periodo");
			}
			_log.debug("obteniendo conexion");
			con = ConnectionHelper.getConnectionPortalEmpleadoresV01();
			stmt = con.prepareCall(sql.toString());
			
			if(null!=periodoDesde){
				stmt.setDate(1, new java.sql.Date(periodoDesde.getTime()));
			}else{
				stmt.setNull(1, Types.DATE);
			}
			if(null!=periodoHasta){
				stmt.setDate(2, new java.sql.Date(periodoHasta.getTime()));				
			}else{
				stmt.setNull(2, Types.DATE);				
			}
			
			if(null!=cuit_entidad && cuit_entidad.trim().length()>0){
				stmt.setString(3, cuit_entidad);
			}else{
				stmt.setNull(3, Types.VARCHAR);
			}	
			
			if(idSeccional>0){
				stmt.setInt(4, idSeccional);
			}else{
				stmt.setNull(4, Types.INTEGER);
			}	
							
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				FichaBoletaPortal ficha=FichaBoletaPortal.getMapingTodasEmpresas(rs);
				listaFichas.add(ficha);							
			}		
 		} catch (Exception e) {
			_log.error(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}

		return listaFichas;
	}

	public List<FichaBoletaPortal> getReporteBoletaPortal(Date periodoDesde,
			Date periodoHasta, String cuentaSuc, String tipoBoleta,
			String actaConvenio, Date fechaRecDesde, Date fechaRecHasta,
			String nroCheque, int impDesde, int impHasta, String estadoCheque,
			String cuit_entidad, Date fechaRenDesde, Date fechaRenHasta, int idSeccional) {

		Connection con = null;
		CallableStatement stmt = null;
		ArrayList<FichaBoletaPortal> listaFichas = null;
		listaFichas = new ArrayList<FichaBoletaPortal>();
		try {

			String sql = "{call reporte_boletas_empleadores(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
			_log.debug("obteniendo conexion");
			con = ConnectionHelper.getConnectionPortalEmpleadoresV01();
			stmt = con.prepareCall(sql.toString());

			if (null != periodoDesde) {
				stmt.setDate(1, new java.sql.Date(periodoDesde.getTime()));
			} else {
				stmt.setNull(1, Types.DATE);
			}
			if (null != periodoHasta) {
				stmt.setDate(2, new java.sql.Date(periodoHasta.getTime()));
			} else {
				stmt.setNull(2, Types.DATE);
			}

			if (null != cuentaSuc && cuentaSuc.trim().length() > 0) {
				cuentaSuc = cuentaSuc + ",";
				stmt.setString(3, cuentaSuc);
			} else {
				stmt.setNull(3, Types.VARCHAR);
			}

			if (null != tipoBoleta && tipoBoleta.trim().length() > 0) {
				tipoBoleta = tipoBoleta + ",";
				stmt.setString(4, tipoBoleta);
			} else {
				stmt.setNull(4, Types.VARCHAR);
			}

			if (null != actaConvenio && actaConvenio.trim().length() > 0) {
				stmt.setString(5, actaConvenio);
			} else {
				stmt.setNull(5, Types.VARCHAR);
			}

			if (null != fechaRecDesde) {
				stmt.setDate(6, new java.sql.Date(fechaRecDesde.getTime()));
			} else {
				stmt.setNull(6, Types.DATE);
			}
			if (null != fechaRecHasta) {
				stmt.setDate(7, new java.sql.Date(fechaRecHasta.getTime()));
			} else {
				stmt.setNull(7, Types.DATE);
			}

			if (null != nroCheque && nroCheque.trim().length() > 0) {
				stmt.setString(8, nroCheque);
			} else {
				stmt.setNull(8, Types.VARCHAR);
			}

			if (impDesde > 0) {
				stmt.setInt(9, impDesde);
			} else {
				stmt.setNull(9, Types.INTEGER);
			}

			if (impHasta > 0) {
				stmt.setInt(10, impHasta);
			} else {
				stmt.setNull(10, Types.INTEGER);
			}

			if (null != estadoCheque && estadoCheque.trim().length() > 0) {
				stmt.setString(11, estadoCheque);
			} else {
				stmt.setNull(11, Types.VARCHAR);
			}

			if (null != cuit_entidad && cuit_entidad.trim().length() > 0) {
				stmt.setString(12, cuit_entidad);
			} else {
				stmt.setNull(12, Types.VARCHAR);
			}
			
			if (null != fechaRenDesde) {
				stmt.setDate(13, new java.sql.Date(fechaRenDesde.getTime()));
			} else {
				stmt.setNull(13, Types.DATE);
			}
			if (null != fechaRenHasta) {
				stmt.setDate(14, new java.sql.Date(fechaRenHasta.getTime()));
			} else {
				stmt.setNull(14, Types.DATE);
			}
			
			if (idSeccional>0) {
				stmt.setInt(15, idSeccional);
			} else {
				stmt.setNull(15, Types.INTEGER);
			}

			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				FichaBoletaPortal ficha = FichaBoletaPortal
						.getMapingFiltrada(rs);
				listaFichas.add(ficha);
			}
		} catch (Exception e) {
			_log.error(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}

		return listaFichas;
	}
	
	
	
	public List<FichaBoletaPortal> getBoletaCapitalInteresPortal(Date periodoDesde,
			Date periodoHasta) {

		Connection con = null;
		CallableStatement stmt = null;
		ArrayList<FichaBoletaPortal> listaFichas = null;
		listaFichas = new ArrayList<FichaBoletaPortal>();
		try {

			String sql = "{call reporte_boletas_capital_interes(?,?)}";
			_log.debug("obteniendo conexion");
			con = ConnectionHelper.getConnectionPortalEmpleadoresV01();
			stmt = con.prepareCall(sql.toString());

			if (null != periodoDesde) {
				stmt.setDate(1, new java.sql.Date(periodoDesde.getTime()));
			} else {
				stmt.setNull(1, Types.DATE);
			}
			if (null != periodoHasta) {
				stmt.setDate(2, new java.sql.Date(periodoHasta.getTime()));
			} else {
				stmt.setNull(2, Types.DATE);
			}


			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				FichaBoletaPortal ficha = new FichaBoletaPortal();
				ficha.setDescripcion(rs.getString("tipoboleta"));
				ficha.setCantidad(rs.getInt("cantidad"));
				BigDecimal capital=rs.getBigDecimal("capital");
				ficha.setCapital(capital);
				BigDecimal interes=(BigDecimal) (rs.getBigDecimal("interes")!=null?rs.getBigDecimal("interes"):BigDecimal.ZERO);
				ficha.setInteres(interes);
				BigDecimal ajusteCapital=(BigDecimal) (rs.getBigDecimal("ajustecapital")!=null?rs.getBigDecimal("ajustecapital"):BigDecimal.ZERO);
				ficha.setAjusteCapital(ajusteCapital);
				BigDecimal ajusteInteres=(BigDecimal) (rs.getBigDecimal("ajusteinteres")!=null?rs.getBigDecimal("ajusteinteres"):BigDecimal.ZERO);
				ficha.setAjusteInteres(ajusteInteres);
				BigDecimal total=capital.add(interes).add(ajusteCapital).add(ajusteInteres);
				ficha.setImporte(total);
				listaFichas.add(ficha);
			}
		} catch (Exception e) {
			_log.error(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}

		return listaFichas;
	}
	
	public List<FichaBoletaPortal> getBoletaCapitalSinDDJJ(Date periodoDesde,
			Date periodoHasta) {

		Connection con = null;
		CallableStatement stmt = null;
		ArrayList<FichaBoletaPortal> listaFichas = null;
		listaFichas = new ArrayList<FichaBoletaPortal>();
		try {

			String sql = "{call reporte_boletas_sin_ddjj_capital_interes(?,?)}";
			_log.debug("obteniendo conexion");
			
			con = ConnectionHelper.getConnectionPortalEmpleadoresV01();
			stmt = con.prepareCall(sql.toString());

			if (null != periodoDesde) {
				stmt.setDate(1, new java.sql.Date(periodoDesde.getTime()));
			} else {
				stmt.setNull(1, Types.DATE);
			}
			if (null != periodoHasta) {
				stmt.setDate(2, new java.sql.Date(periodoHasta.getTime()));
			} else {
				stmt.setNull(2, Types.DATE);
			}


			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				FichaBoletaPortal ficha = new FichaBoletaPortal();
				ficha.setDescripcion(rs.getString("conceptouoma") !=null ? rs.getString("conceptouoma") :"");
				ficha.setCantidad(rs.getInt("cantidad"));
				BigDecimal capital=rs.getBigDecimal("total");
				ficha.setCapital(capital);
				ficha.setRazon_soc(rs.getString("razon"));
				ficha.setEntidadBoleta(rs.getString("entidadboleta"));
				BigDecimal interes=(BigDecimal) BigDecimal.ZERO;
				ficha.setInteres(interes);
				BigDecimal ajusteCapital=(BigDecimal) BigDecimal.ZERO;
				ficha.setAjusteCapital(ajusteCapital);
				BigDecimal ajusteInteres=(BigDecimal) BigDecimal.ZERO;
				ficha.setAjusteInteres(ajusteInteres);
				BigDecimal total=capital.add(interes).add(ajusteCapital).add(ajusteInteres);
				ficha.setImporte(total);
				listaFichas.add(ficha);
			}
		} catch (Exception e) {
			_log.error(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}

		return listaFichas;
	}
	
	
	public FichaBoletaPortal getReporteCantDDJJFinales(Date periodoDesde,
			Date periodoHasta) {

		Connection con = null;
		CallableStatement stmt = null;
		FichaBoletaPortal ficha = new FichaBoletaPortal();
		try {

			String sql = "{call reporte_cant_ddjj_finales(?,?)}";
			_log.debug("obteniendo conexion");
			
			con = ConnectionHelper.getConnectionPortalEmpleadoresV01();			
			stmt = con.prepareCall(sql.toString());

			if (null != periodoDesde) {
				stmt.setDate(1, new java.sql.Date(periodoDesde.getTime()));
			} else {
				stmt.setNull(1, Types.DATE);
			}
			if (null != periodoHasta) {
				stmt.setDate(2, new java.sql.Date(periodoHasta.getTime()));
			} else {
				stmt.setNull(2, Types.DATE);
			}
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				ficha.setReporteCantDDJJFinales(rs.getInt(1)); 
			}
		} catch (Exception e) {
			_log.error(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}

		return ficha;
	}
	
	public FichaBoletaPortal getReporteCantDDJJ(Date periodoDesde,
			Date periodoHasta) {

		Connection con = null;
		CallableStatement stmt = null;
		FichaBoletaPortal ficha = new FichaBoletaPortal();
		try {

			String sql = "{call reporte_cant_ddjj(?,?)}";
			_log.debug("obteniendo conexion");
			
			con = ConnectionHelper.getConnectionPortalEmpleadoresV01();			
			stmt = con.prepareCall(sql.toString());

			if (null != periodoDesde) {
				stmt.setDate(1, new java.sql.Date(periodoDesde.getTime()));
			} else {
				stmt.setNull(1, Types.DATE);
			}
			if (null != periodoHasta) {
				stmt.setDate(2, new java.sql.Date(periodoHasta.getTime()));
			} else {
				stmt.setNull(2, Types.DATE);
			}
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				ficha.setReporteCantDDJJ(rs.getInt(1)); 
			}
		} catch (Exception e) {
			_log.error(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}

		return ficha;
	}
	
	public FichaBoletaPortal getReporteEmpresasActivas(Date periodoDesde,
			Date periodoHasta) {

		Connection con = null;
		CallableStatement stmt = null;
		FichaBoletaPortal ficha = new FichaBoletaPortal();
		try {

			String sql = "select count(*)  from reporte_empresas_activas(?,?)";
			_log.debug("obteniendo conexion");
			
			con = ConnectionHelper.getConnectionPortalEmpleadoresV01();			
			stmt = con.prepareCall(sql.toString());

			if (null != periodoDesde) {
				stmt.setDate(1, new java.sql.Date(periodoDesde.getTime()));
			} else {
				stmt.setNull(1, Types.DATE);
			}
			if (null != periodoHasta) {
				stmt.setDate(2, new java.sql.Date(periodoHasta.getTime()));
			} else {
				stmt.setNull(2, Types.DATE);
			}
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				ficha.setEmpresasActivas(rs.getInt(1)); 
			}
		} catch (Exception e) {
			_log.error(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}

		return ficha;
	}

	public Integer getBoletaNroSecuencia(String cuit,Integer secuenciaDDJJ, Date periodo, String tipoBoleta) throws SystemException {
		_log.debug("Obteniendo Nro Secuencia Boleta " + cuit + " " + tipoBoleta);
		Connection con = null;
		CallableStatement stmt = null;
		Integer ret=0;
		List<Integer>list=new ArrayList<Integer>();
		try {
			con = ConnectionHelper.getConnectionPortalEmpleadoresV01();
			String sql = "{call  trae_nro_secuencia_boleta (?,?,?,?)}";
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, cuit);
			stmt.setInt(2,secuenciaDDJJ);
			stmt.setDate(3, new java.sql.Date(periodo.getTime()));
			stmt.setString(4, tipoBoleta);
			
			ResultSet rs = stmt.executeQuery();
			
			while (rs.next()) {
			  ret=rs.getInt("numerosecuencia");
			}
		} catch (Exception e) {
			_log.error("Error al buscar Nro Secuencia Boleta", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		_log.debug("Saliendo de Nro Secuencia Boleta " + cuit + " " + tipoBoleta);
		return ret;
	}

	public List<FichaBoletaPortal> getBoletasPorSecuencia(String cuit,String sucursal,
			Integer tipoBoleta, Integer nro) {

		Connection con = null;
		CallableStatement stmt = null;
		ArrayList<FichaBoletaPortal> listaFichas = null;
		listaFichas = new ArrayList<FichaBoletaPortal>();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		try {

			String sql = "{call trae_boleta_por_secuencia(?,?,?,?)}";
			_log.debug("obteniendo conexion Boletas por Secuencia");
			
			con = ConnectionHelper.getConnectionPortalEmpleadoresV01();
			stmt = con.prepareCall(sql.toString());

			stmt.setString(1, cuit);
			stmt.setString(2, sucursal);
			
			if (null != tipoBoleta) {
				stmt.setInt(3, tipoBoleta);
			} else {
				stmt.setNull(3, Types.INTEGER);
			}
			if (null != nro) {
				stmt.setInt(4,nro);
			} else {
				stmt.setNull(4, Types.INTEGER);
			}


			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				FichaBoletaPortal ficha = new FichaBoletaPortal();
				   
			    ficha.setDescripcion(rs.getString("tipoboleta"));
				BigDecimal capital=rs.getBigDecimal("capital");
				ficha.setCapital(capital);
				BigDecimal interes=rs.getBigDecimal("interes");
				ficha.setInteres(interes);
				BigDecimal ajustes=rs.getBigDecimal("ajustes");
				ficha.setAjusteCapital(ajustes);
				BigDecimal total=capital.add(interes).add(ajustes);
				
				ficha.setEmpresa_cuit(rs.getString("empresa_cuit"));
				ficha.setPeriodo_cod_barras(rs.getDate("periodo"));
				
				ficha.setNro_boleta_portal_emple(rs.getInt("numerosecuencia"));
				ficha.setFecha_ing(sdf.format(rs.getDate("fechavencimiento")));
				
				ficha.setNro_secuendia_ddjj_portal_emple(rs.getInt("numerosecuenciadeclaracion"));
				
				
				//Pagos
				Date fechaRecauda=null;
				fechaRecauda=rs.getDate("fecha_recauda");
                ficha.setFecha_recauda(fechaRecauda);
                
                BigDecimal importe=BigDecimal.ZERO;
                importe = rs.getBigDecimal("importe_pago");
                ficha.setImporte(importe);
				
				String cuenta_sucursal="";
				if(rs.getBigDecimal("suc_nacion")!=null) {
			      cuenta_sucursal=rs.getBigDecimal("suc_nacion").toPlainString();
				}
				ficha.setCuenta_sucursal(cuenta_sucursal);
				
				Integer cod_sucursal_nacion=0;
				if(rs.getBigDecimal("suc_cbra")!=null) {
				 cod_sucursal_nacion=rs.getBigDecimal("suc_cbra").intValue();
				} 
				ficha.setCod_sucursal_nacion(cod_sucursal_nacion);
				
				ficha.setRemuneracion(rs.getBigDecimal("remuneracion_total"));
				ficha.setCantidad(rs.getInt("cantidad_empleados"));
				
				listaFichas.add(ficha);
			}
		} catch (Exception e) {
			_log.error(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}

		return listaFichas;
	}


	public List<FichaBoletaPortal> getBoletasImpagas(String cuit,String sucursal,
			Integer tipoBoleta, Integer visualizar) {

		Connection con = null;
		CallableStatement stmt = null;
		ArrayList<FichaBoletaPortal> listaFichas = null;
		listaFichas = new ArrayList<FichaBoletaPortal>();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		try {

			String sql = "{call trae_boletas_impagas(?,?,?,?)}";
			_log.debug("obteniendo conexion Boletas Impagas");
			
			con = ConnectionHelper.getConnectionPortalEmpleadoresV01();
			stmt = con.prepareCall(sql.toString());

			stmt.setString(1, cuit);
			stmt.setString(2, sucursal);
			
			if (null != tipoBoleta) {
				stmt.setInt(3, tipoBoleta);
			} else {
				stmt.setNull(3, Types.INTEGER);
			}
			if (null != visualizar) {
				stmt.setInt(4,visualizar);
			} else {
				stmt.setNull(4, Types.INTEGER);
			}


			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				FichaBoletaPortal ficha = new FichaBoletaPortal();
				   
			    ficha.setDescripcion(rs.getString("tipoboleta"));
				BigDecimal capital=rs.getBigDecimal("capital");
				ficha.setCapital(capital);
				BigDecimal interes=rs.getBigDecimal("interes");
				ficha.setInteres(interes);
				BigDecimal ajustes=rs.getBigDecimal("ajustes");
				ficha.setAjusteCapital(ajustes);
				BigDecimal total=capital.add(interes).add(ajustes);
				
				ficha.setEmpresa_cuit(rs.getString("empresa_cuit"));
				ficha.setPeriodo_cod_barras(rs.getDate("periodo"));
				
				ficha.setNro_boleta_portal_emple(rs.getInt("numerosecuencia"));
				ficha.setFecha_ing(sdf.format(rs.getDate("fechavencimiento")));
				
				listaFichas.add(ficha);
			}
		} catch (Exception e) {
			_log.error(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}

		return listaFichas;
	}

	
	public FichaBoletaPortal getDatosCobranzaPagosMisCuentas(String cuit,Integer nroBoleta) throws SystemException {
		_log.debug("Obteniendo Datos Boleta " + cuit + " " + nroBoleta.toString());
		Connection con = null;
		CallableStatement stmt = null;
		FichaBoletaPortal ret=new FichaBoletaPortal();
		List<Integer>list=new ArrayList<Integer>();
		try {
			con = ConnectionHelper.getConnectionPortalEmpleadoresV01();
			String sql = "{call  trae_datos_boleta_by_cuit_numero (?,?)}";
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, cuit);
			stmt.setInt(2,nroBoleta);
			
			ResultSet rs = stmt.executeQuery();
			
			while (rs.next()) {
			  ret.setNro_secuendia_ddjj_portal_emple(rs.getInt("numerosecuenciadeclaracion"));
			  ret.setPeriodo_cod_barras(rs.getDate("periodo"));
			  ret.setTipoBoleta(rs.getInt("tipoboletanumero"));
			}
		} catch (Exception e) {
			_log.error("Error al buscar Nro Secuencia Boleta", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		_log.debug("Saliendo de Nro Secuencia Boleta " + cuit + " " + nroBoleta.toString());
		return ret;
	}
	
	public FichaBoletaPortal getDatosCobranza(String cuit,Integer nroBoleta) throws SystemException {
		_log.debug("Obteniendo Datos Boleta " + cuit + " " + nroBoleta.toString());
		Connection con = null;
		CallableStatement stmt = null;
		FichaBoletaPortal ret=new FichaBoletaPortal();
		List<Integer>list=new ArrayList<Integer>();
		try {
			con = ConnectionHelper.getConnectionPortalEmpleadoresV01();
			String sql = "{call  trae_datos_boleta_by_cuit_numero (?,?)}";
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, cuit);
			stmt.setInt(2,nroBoleta);
			
			ResultSet rs = stmt.executeQuery();
			
			while (rs.next()) {
				ret.setBoletaId(rs.getLong("id"));
				ret.setIntencionPago(rs.getDate("fechapago"));
				ret.setVencimiento(rs.getDate("fechavencimiento"));
				ret.setNro_boleta_portal_emple(rs.getInt("numerosecuencia"));
				ret.setNro_secuendia_ddjj_portal_emple(rs.getInt("numerosecuenciadeclaracion"));
				ret.setTipoBoletaStr(rs.getString("tipoboleta"));
				ret.setCapital(rs.getBigDecimal("capital"));
				ret.setInteres(rs.getBigDecimal("interes"));
				ret.setAjusteCapital(rs.getBigDecimal("ajustes"));
				ret.setCuit(rs.getString("empresa_cuit"));
				ret.setEmpresa_sucursal(rs.getString("empresa_sucursal"));
				ret.setPeriodo_cod_barras(rs.getDate("periodo"));
				ret.setFecha_recauda(rs.getDate("fecha_recauda"));
				ret.setNroMovimiento(rs.getString("nro_movimiento"));
				ret.setImporte(rs.getBigDecimal("importe_pago"));
				ret.setCod_sucursal_nacion(rs.getInt("suc_nacion"));
				ret.setCantidad(rs.getInt("cantidad_empleados"));
				ret.setRemuneracion(rs.getBigDecimal("remuneracion_total"));
				ret.setTipoBoleta(rs.getInt("tipoboletanumero"));
				ret.setFecha_rendicion(rs.getDate("fecha_rendicion"));
				ret.setCodBarras(rs.getString("cod_barras"));
			}
		} catch (Exception e) {
			_log.error("Error al buscar Cobranza Boleta", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		_log.debug("Saliendo de Cobranza Boleta " + cuit + " " + nroBoleta.toString());
		return ret;
	}
	
	public FichaBoletaPortal getDatosCobranza(String cuit,Integer nroBoleta,String nroMovimiento) throws SystemException {
		_log.debug("Obteniendo Datos Boleta " + cuit + " " + nroBoleta.toString());
		Connection con = null;
		CallableStatement stmt = null;
		FichaBoletaPortal ret=new FichaBoletaPortal();
		List<Integer>list=new ArrayList<Integer>();
		try {
			con = ConnectionHelper.getConnectionPortalEmpleadoresV01();
			String sql = "{call  trae_datos_boleta_by_cuit_numero (?,?)}";
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, cuit);
			stmt.setInt(2,nroBoleta);
			
			ResultSet rs = stmt.executeQuery();
			
			while (rs.next()) {
				if(rs.getString("nro_movimiento").equals(nroMovimiento)) {
				 ret.setBoletaId(rs.getLong("id"));
				 ret.setIntencionPago(rs.getDate("fechapago"));
				 ret.setVencimiento(rs.getDate("fechavencimiento"));
				 ret.setNro_boleta_portal_emple(rs.getInt("numerosecuencia"));
				 ret.setNro_secuendia_ddjj_portal_emple(rs.getInt("numerosecuenciadeclaracion"));
				 ret.setTipoBoletaStr(rs.getString("tipoboleta"));
				 ret.setCapital(rs.getBigDecimal("capital"));
				 ret.setInteres(rs.getBigDecimal("interes"));
				 ret.setAjusteCapital(rs.getBigDecimal("ajustes"));
				 ret.setCuit(rs.getString("empresa_cuit"));
				 ret.setEmpresa_sucursal(rs.getString("empresa_sucursal"));
				 ret.setPeriodo_cod_barras(rs.getDate("periodo"));
				 ret.setFecha_recauda(rs.getDate("fecha_recauda"));
				 ret.setNroMovimiento(rs.getString("nro_movimiento"));
				 ret.setImporte(rs.getBigDecimal("importe_pago"));
				 ret.setCod_sucursal_nacion(rs.getInt("suc_nacion"));
				 ret.setCantidad(rs.getInt("cantidad_empleados"));
				 ret.setRemuneracion(rs.getBigDecimal("remuneracion_total"));
				 ret.setTipoBoleta(rs.getInt("tipoboletanumero"));
				 ret.setFecha_rendicion(rs.getDate("fecha_rendicion"));
				 ret.setCodBarras(rs.getString("cod_barras"));
				}
			}
		} catch (Exception e) {
			_log.error("Error al buscar Cobranza Boleta", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		_log.debug("Saliendo de Cobranza Boleta " + cuit + " " + nroBoleta.toString());
		return ret;
	}
}
