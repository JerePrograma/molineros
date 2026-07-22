package ar.com.ospim.farmaciaOspim.services;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.model.User;
import ar.com.ospim.farmaciaOspim.beans.ArchivoMedEspecial;
import ar.com.ospim.procesaArchivos.beans.ArchivoVademecum;
import ar.com.ospim.farmaciaOspim.beans.ItemFarmaciaTotal;
import ar.com.ospim.farmaciaOspim.beans.TiposDeVentas;
import ar.com.ospim.farmaciaOspim.exceptions.ImposibleBorrarFarmaciaOspimException;
import ar.com.ospim.farmaciaOspim.exceptions.ImposibleCerrarVademecumFarmaciaOspimException;
import ar.com.ospim.global.beans.Farmacia;
import ar.com.ospim.prestadores.exception.DuplicatePrestadorIdException;
import ar.com.ospim.procesaArchivos.beans.farmaciaospim.ArchivoAdmifarm;
import ar.com.ospim.procesaArchivos.beans.farmaciaospim.ArchivoDesglose;
import ar.com.ospim.procesaArchivos.beans.farmaciaospim.DetalleAdmifarm;
import ar.com.ospim.procesaArchivos.beans.farmaciaospim.DetalleDesglose;
import ar.com.ospim.util.ConnectionHelper;


	public class FarmaciaServiceImpl {

	private static Log logger = LogFactoryUtil.getLog(FarmaciaServiceImpl.class);
	
	private static FarmaciaServiceImpl instance = null;

	public static FarmaciaServiceImpl getInstance() {
		if (null == instance) {
			instance = new FarmaciaServiceImpl();
		}
		return instance;
	}
	
	public List<Farmacia> getFarmaciasOspim(String cuitFarmacia  , String  descripcionFarmacia ,int  provincia , int localidad , String codMandataria )
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		List<Farmacia> farmacias = null;
		try {
			con = ConnectionHelper.getConnection();
			String sql = "{call  trae_farmacias(?,?,?,?,? )}";
			stmt = con.prepareCall(sql.toString());
			
			if (cuitFarmacia  =="") {
				stmt.setNull(1, Types.VARCHAR );
			}else{
				stmt.setString(1, cuitFarmacia);   
			}	
			if (descripcionFarmacia=="") {
				stmt.setNull(2, Types.VARCHAR );
			}else{
				stmt.setString(2, descripcionFarmacia);   
			}	
			
			if (provincia <1 ) {
				stmt.setNull(3, Types.INTEGER );
			}else{
				stmt.setInt(3, provincia);   
			}
			
			if (localidad<1 ) {
				stmt.setNull(4, Types.INTEGER );
			}else{
				stmt.setInt(4, localidad);   
			}
			
			if (codMandataria =="") {
				stmt.setNull(5, Types.VARCHAR );
			}else{
				stmt.setString(5, codMandataria );   
			}
			
		
			ResultSet rs = stmt.executeQuery();
			farmacias = new ArrayList<Farmacia>();
			
			while (rs.next()) {
				Farmacia archivo = Farmacia.getMappingColegioSeccional(rs,"");
				farmacias.add(archivo);
			}
		} catch (Exception e) {
			logger.error("Error al buscar registros de farmacia Ospim", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}

		return farmacias;		
	}
	
	public List<ItemFarmaciaTotal> getFarmaciasOspimTotal(String cuitFarmacia  , String  descripcionFarmacia ,int  provincia , int localidad , String codMandataria , int pagina  )
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		List<ItemFarmaciaTotal> farmacias = null;
		try {
			con = ConnectionHelper.getConnection();
			String sql = "{call trae_farmacias(?,?,?,?,?,? )}";
			stmt = con.prepareCall(sql.toString());
			
			if (cuitFarmacia  =="") {
				stmt.setNull(1, Types.VARCHAR );
			}else{
				stmt.setString(1, cuitFarmacia);   
			}	
			if (descripcionFarmacia=="") {
				stmt.setNull(2, Types.VARCHAR );
			}else{
				stmt.setString(2, descripcionFarmacia);   
			}	
			
			if (provincia <1 ) {
				stmt.setNull(3, Types.INTEGER );
			}else{
				stmt.setInt(3, provincia);   
			}
			
			if (localidad<1 ) {
				stmt.setNull(4, Types.INTEGER );
			}else{
				stmt.setInt(4, localidad);   
			}
			
			if (codMandataria =="") {
				stmt.setNull(5, Types.VARCHAR );
			}else{
				stmt.setString(5, codMandataria );   
			}
			
			stmt.setInt(6, pagina );
		
			ResultSet rs = stmt.executeQuery();
			farmacias = new ArrayList<ItemFarmaciaTotal >();
			
			while (rs.next()) {
				ItemFarmaciaTotal archivo = Farmacia.getMappingFarmaciaTotal(rs,"");
				farmacias.add(archivo);
			}
		} catch (Exception e) {
			logger.error("Error al buscar registros de farmacia Ospim", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}

		return farmacias;		
	}
	
	public List<ItemFarmaciaTotal> getFarmaciasOspimTotal2(String cuitFarmacia  , String  descripcionFarmacia ,int  provincia , int localidad , String codFarmacia , int pagina  )
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		List<ItemFarmaciaTotal> farmacias = null;
		try {
			con = ConnectionHelper.getConnection();
			String sql = "{call public.trae_farmacias_2025(?,?,?,?,?,? )}";
			stmt = con.prepareCall(sql.toString());
			
			if (cuitFarmacia  =="") {
				stmt.setNull(1, Types.VARCHAR );
			}else{
				stmt.setString(1, cuitFarmacia);   
			}	
			if (descripcionFarmacia=="") {
				stmt.setNull(2, Types.VARCHAR );
			}else{
				stmt.setString(2, descripcionFarmacia);   
			}	
			
			if (provincia <1 ) {
				stmt.setNull(3, Types.INTEGER );
			}else{
				stmt.setInt(3, provincia);   
			}
			
			if (localidad<1 ) {
				stmt.setNull(4, Types.INTEGER );
			}else{
				stmt.setInt(4, localidad);   
			}
			
			if (codFarmacia =="") {
				stmt.setNull(5, Types.VARCHAR );
			}else{
				stmt.setString(5, codFarmacia );   
			}
			
			stmt.setInt(6, pagina );
		
			ResultSet rs = stmt.executeQuery();
			farmacias = new ArrayList<ItemFarmaciaTotal >();
			
			while (rs.next()) {
				ItemFarmaciaTotal archivo = Farmacia.getMappingFarmaciaTotal(rs,"");
				farmacias.add(archivo);
			}
		} catch (Exception e) {
			logger.error("Error al buscar registros de farmacia Ospim", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}

		return farmacias;		
	}
	
	public List<ArchivoDesglose> getArchivosSubidosDesgloseFarmacia ()
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		List<ArchivoDesglose> list = null;
		try {
			con = ConnectionHelper.getConnection();
			String sql = "{call  trae_ultimos_archivos_prevencion_farmacia()}";
			stmt = con.prepareCall(sql.toString());
			
			ResultSet rs = stmt.executeQuery();
			list = new ArrayList<ArchivoDesglose>();
			while (rs.next()) {
				ArchivoDesglose archivo = ArchivoDesglose.getMapping(rs);
				list.add(archivo);
			}
		} catch (Exception e) {
			logger.error("Error al buscar ultimas importaciones de Desglose Farmacia", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}

		return list;
	}
	
	public List<ArchivoMedEspecial> getArchivosSubidosMedEspecial ()
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		List<ArchivoMedEspecial> list = null;
		try {
			con = ConnectionHelper.getConnection();
			String sql = "{call  trae_ultimos_archivos_importados_medesp()}";
			stmt = con.prepareCall(sql.toString());
			
			ResultSet rs = stmt.executeQuery();
			list = new ArrayList<ArchivoMedEspecial>();
			while (rs.next()) {
				ArchivoMedEspecial archivo = ArchivoMedEspecial.getMapping(rs);
				list.add(archivo);
			}
		} catch (Exception e) {
			logger.error("Error al buscar ultimas importaciones de Med Especial", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}

		return list;
	}

	
	public List<ArchivoVademecum> getArchivosSubidosVademecum ()
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		List<ArchivoVademecum> list = null;
		try {
			con = ConnectionHelper.getConnection();
			String sql = "{call  trae_ultimos_archivos_importados_vademecum()}";
			stmt = con.prepareCall(sql.toString());
			
			ResultSet rs = stmt.executeQuery();
			list = new ArrayList<ArchivoVademecum>();
			while (rs.next()) {
				ArchivoVademecum archivo = ArchivoVademecum.getMapping(rs);
				list.add(archivo);
			}
		} catch (Exception e) {
			logger.error("Error al buscar ultimas importaciones de Vademedecum", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}

		return list;
	}

	
	public void   cerrarVademecum(int id, User user ) throws SQLException, ImposibleCerrarVademecumFarmaciaOspimException{
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call cerrar_vademecum(?,?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, id);
			stmt.setString(2,user.getScreenName() );
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				if (rs.getInt(1) == 0) {
					throw new ImposibleCerrarVademecumFarmaciaOspimException  ();
				}
			}
		} catch (ImposibleCerrarVademecumFarmaciaOspimException e) {
			logger.error("Error al borrar la farmacia", e);
			throw e;
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
	}

	
		public List<TiposDeVentas> getTipoDeVentas() {
		Connection con = null;
		List<TiposDeVentas> listaDeTiposDeVentas= null;
		CallableStatement stmt = null;
		try {
			String sql = "{call farmacia.buscar_tipos_de_venta()}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			ResultSet rs = stmt.executeQuery();
			listaDeTiposDeVentas= new ArrayList<TiposDeVentas>();
			while (rs.next()) {
				TiposDeVentas situacionMedica = new TiposDeVentas( rs.getInt("codigo") , rs.getString("detalle") );
				listaDeTiposDeVentas.add(situacionMedica );
			}
		} catch (Exception e) {
			logger.error("Error tipo de venta", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return listaDeTiposDeVentas;
	}



	public void borrar(int id, String screenName) throws SQLException, ImposibleBorrarFarmaciaOspimException{
		Connection con = null;
		CallableStatement stmt = null;
		try {
			String sql = "{call borrar_farmacia(?,?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, id);
			stmt.setString(2, screenName);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				if (rs.getInt(1) == 0) {
					throw new ImposibleBorrarFarmaciaOspimException ();
				}
			}
		} catch (ImposibleBorrarFarmaciaOspimException e) {
			logger.error("Error al borrar la farmacia", e);
			throw e;
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
	}
	
	public void borrar2(long id, String screenName) throws SQLException, ImposibleBorrarFarmaciaOspimException {
	    Connection con = null;
	    CallableStatement stmt = null;
	    try {
	        String sql = "{call borrar_farmacia2(?,?)}";
	        con = ConnectionHelper.getConnection();
	        stmt = con.prepareCall(sql);
	        stmt.setLong(1, id);          // <-- setLong
	        stmt.setString(2, screenName);
	        ResultSet rs = stmt.executeQuery();
	        while (rs.next()) {
	            if (rs.getInt(1) == 0) throw new ImposibleBorrarFarmaciaOspimException();
	        }
	    } finally {
	        ConnectionHelper.cerrar(stmt, con);
	    }
	}


	public Farmacia getFarmacia (int id ) throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		Farmacia farmacia = null;
		
		try {
			String sql = "{call trae_farmacia_por_id(?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setInt(1, id);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				farmacia = Farmacia.getMappingColegioSeccional(rs, "");
							  }
		} catch (Exception e) {
			logger.error("Error al buscar farmacia", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		farmacia.setId_farmacia(id);		
		return farmacia ;
	}

	public Farmacia getFarmacia2(long id) throws SystemException {
	    Connection con = null;
	    CallableStatement stmt = null;
	    Farmacia farmacia = null;

	    try {
	        String sql = "{call trae_farmacia_por_id2(?)}";
	        con = ConnectionHelper.getConnection();
	        stmt = con.prepareCall(sql);
	        stmt.setLong(1, id);   // <-- setLong
	        ResultSet rs = stmt.executeQuery();
	        while (rs.next()) {
	            farmacia = Farmacia.getMappingColegioSeccional(rs, "");
	        }
	    } catch (Exception e) {
	        logger.error("Error al buscar farmacia", e);
	    } finally {
	        ConnectionHelper.cerrar(stmt, con);
	    }

	    if (farmacia != null) {
	        // si el bean tiene id int, acá vas a necesitar casteo o cambiar el tipo en el bean
	        // farmacia.setId_farmacia((int) id);
	        // ideal: farmacia.setId_farmacia(id);
	    }

	    return farmacia;
	}


	
	
	
public int insertar(Farmacia farmacia , User user ) throws SystemException, DuplicatePrestadorIdException {
		
		Connection con = null;
		CallableStatement stmt = null ;
		String screenName = user.getScreenName();
		int idFarmacia =0;
		String sql  = "{call insertar_farmacia(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
		try {			
			
			con = ConnectionHelper.getConnectionForTransaction();		
			stmt = con.prepareCall(sql.toString());
			stmt.registerOutParameter(1, Types.INTEGER);
						
			stmt.setString (1, farmacia.getFarmacia()   );
			stmt.setString (2, farmacia.getEmpresa().getCuit()    );
			stmt.setString (3, farmacia.getCodigo() );
			stmt.setString (4, farmacia.getCalle()   );
			stmt.setString (5, farmacia.getTelefono()  );
			stmt.setString (6, farmacia.getCodigoFarmacia()  );
			stmt.setString (7, farmacia.getEmpresa().getSucursal() );
			stmt.setString (8, farmacia.getBaseDto()  );
			stmt.setInt(9, farmacia.getSeccional().getId() );
			stmt.setString (10, farmacia.getColegio().getCodigo() );
			stmt.setString(11, screenName);
			stmt.setString (12, farmacia.getCamara()    );
			stmt.setBigDecimal(13,farmacia.getPorcDescuento() );
			stmt.setString (14, farmacia.getCodigoFarmaciaMandataria()  );
			stmt.setInt(15, farmacia.getDomicilioDefault().getProvinciaId());
			stmt.setInt(16, farmacia.getDomicilioDefault().getLocalidadId());
			
			idFarmacia = stmt.executeUpdate();
			if(stmt.getInt(1) > 0){				
				idFarmacia =stmt.getInt(1);
			}
			con.commit();
		} catch (SQLException e) {
			logger.error("Error al insertar farmacia" , e);
			ConnectionHelper.rollback(con);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return idFarmacia ;
	}


public long insertar2(Farmacia farmacia, User user) throws SystemException {
    Connection con = null;
    CallableStatement stmt = null;
    long idFarmacia = 0;

    try {
        String sql = "{? = call public.insertar_farmacia2(?,?,?,?,?,?,?,?,?,?,?)}";
        con = ConnectionHelper.getConnectionForTransaction();
        stmt = con.prepareCall(sql);

        stmt.registerOutParameter(1, Types.BIGINT);

        stmt.setString(2, farmacia.getFarmacia());
        stmt.setString(3, farmacia.getEmpresa().getCuit());
        stmt.setString(4, farmacia.getCodigoFarmacia());  // cod_farm_p
        stmt.setString(5, farmacia.getCalle());
        stmt.setString(6, farmacia.getTelefono());
        stmt.setString(7, farmacia.getBaseDto());         // base_descuento_p
        stmt.setString(8, user.getScreenName());          // alta_usr_p
        stmt.setString(9, farmacia.getCamara());
        stmt.setBigDecimal(10, farmacia.getPorcDescuento());
        stmt.setInt(11, farmacia.getDomicilioDefault().getProvinciaId());
        stmt.setInt(12, farmacia.getDomicilioDefault().getLocalidadId());

        stmt.execute();
        idFarmacia = stmt.getLong(1);

        con.commit();
        return idFarmacia;

    } catch (SQLException e) {
        ConnectionHelper.rollback(con);
        throw new SystemException(e);
    } finally {
        ConnectionHelper.cerrar(stmt, con);
    }
}



public boolean actualizar(Farmacia farmacia , User user ) throws SystemException {
	Connection con = null;
	CallableStatement stmt = null ;
	boolean resp=false ;
	
	try {
		String screenName = user.getScreenName();
		String sql1  = "{call actualiza_farmacia (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
		con = ConnectionHelper.getConnectionForTransaction();
		stmt = con.prepareCall(sql1.toString());		
		stmt.setString (1, farmacia.getFarmacia()   );
		stmt.setString(2,farmacia.getEmpresa().getCuit()  );
		stmt.setString (3, farmacia.getCodigo()      );
		stmt.setString (4, farmacia.getCalle()   );
		stmt.setString (5, farmacia.getTelefono()  );
		stmt.setString (6, farmacia.getCodigoFarmacia()  );
		stmt.setString (7, farmacia.getEmpresa().getSucursal() );
		stmt.setString (8, farmacia.getBaseDto()   );
		stmt.setInt(9, farmacia.getSeccional().getId_seccional()  );
		stmt.setString(10,farmacia.getColegio().getCodigo() );
		stmt.setString (11, screenName);
		stmt.setString (12, farmacia.getCamara()  );
		stmt.setBigDecimal(13, farmacia.getPorcDescuento()  );
		stmt.setInt(14, farmacia.getId_farmacia()  );
		stmt.setString (15, farmacia.getCodigoFarmaciaMandataria()       );
		stmt.setInt(16, farmacia.getDomicilioDefault().getProvinciaId() );
		stmt.setInt(17, farmacia.getDomicilioDefault().getLocalidadId() );
		
		stmt.executeUpdate();
		con.commit();	 
		resp=true;
	} catch (SQLException e) {
		logger.error("Error al actualizar farmacia.", e);
		ConnectionHelper.rollback(con);		
		throw new SystemException(e);
	}  finally {
		ConnectionHelper.cerrar(stmt, con);
	}
	return resp ;
}

public boolean actualizar2(Farmacia farmacia, User user) throws SystemException {
    Connection con = null;
    CallableStatement stmt = null;

    try {
        String screenName = user.getScreenName();

        String sql = "{ call public.actualiza_farmacia2(?,?,?,?,?,?,?,?,?,?,?,?) }";
        con = ConnectionHelper.getConnectionForTransaction();
        stmt = con.prepareCall(sql);

        stmt.setString(1, farmacia.getFarmacia());
        stmt.setString(2, farmacia.getEmpresa().getCuit());
        stmt.setString(3, farmacia.getCodigoFarmacia()); // cod_farm_p
        stmt.setString(4, farmacia.getCalle());
        stmt.setString(5, farmacia.getTelefono());
        stmt.setString(6, farmacia.getBaseDto());
        stmt.setString(7, screenName);
        stmt.setString(8, farmacia.getCamara());
        stmt.setBigDecimal(9, farmacia.getPorcDescuento());
        stmt.setLong(10, farmacia.getId_farmacia()); // bigint
        stmt.setInt(11, farmacia.getDomicilioDefault().getProvinciaId());
        stmt.setInt(12, farmacia.getDomicilioDefault().getLocalidadId());

        stmt.execute();
        con.commit();
        return true;

    } catch (SQLException e) {
        logger.error("Error al actualizar farmacia 2025.", e);
        ConnectionHelper.rollback(con);
        throw new SystemException(e);
    } finally {
        ConnectionHelper.cerrar(stmt, con);
    }
}


public List<DetalleDesglose> getListaDesgloseArchivoFarmacia   (String nombreArchvioDesglose)

		throws SystemException {
	Connection con = null;
	CallableStatement stmt = null;
	List<DetalleDesglose> list = null;
	try {
		String sql ="";			
		sql = "{call reporte_desglose_archivo_farmacia_prevencion (?)}";			
		con = ConnectionHelper.getConnection();
		stmt = con.prepareCall(sql.toString());			
		if (nombreArchvioDesglose == null ) {
			stmt.setString(1,"" );
		} else {
			stmt.setString(1,nombreArchvioDesglose  );
		}								
		
		ResultSet rs = stmt.executeQuery();
		list = new ArrayList<DetalleDesglose>();
		while (rs.next()) {
			DetalleDesglose archivo = DetalleDesglose.getMapping(rs);
			list.add(archivo);
		}
	} catch (Exception e) {
		logger.error("Error en la busqueda de registros de archivo prevencion farmacia", e);
		throw new SystemException(e);
	} finally {
		ConnectionHelper.cerrar(stmt, con);
	}
	return list;
}

	public List<ArchivoAdmifarm> getArchivosSubidosAdmifarm()
			throws SystemException {
	
	Connection con = null;
	CallableStatement stmt = null;
	List<ArchivoAdmifarm> list = null;
	
	try {
		con = ConnectionHelper.getConnection();
		String sql = "{call farmacia.trae_ultimos_archivos_admifarm()}";
		stmt = con.prepareCall(sql);
	
		ResultSet rs = stmt.executeQuery();
		list = new ArrayList<ArchivoAdmifarm>();
	
		while (rs.next()) {
			ArchivoAdmifarm archivo = ArchivoAdmifarm.getMapping(rs);
			list.add(archivo);
		}
	
	} catch (Exception e) {
		logger.error("Error al buscar ultimas importaciones Admifarm", e);
		throw new SystemException(e);
	} finally {
		ConnectionHelper.cerrar(stmt, con);
	}
	
	return list;
	}

	public List<ArchivoAdmifarm> getArchivosSubidosAdmifarmOspimGeneral()
			throws SystemException {
	
	Connection con = null;
	CallableStatement stmt = null;
	List<ArchivoAdmifarm> list = null;
	
	try {
		con = ConnectionHelper.getConnection();
		String sql = "{call farmacia.trae_ultimos_archivos_admifarm_ospim_general()}";
		stmt = con.prepareCall(sql);
	
		ResultSet rs = stmt.executeQuery();
		list = new ArrayList<ArchivoAdmifarm>();
	
		while (rs.next()) {
			ArchivoAdmifarm archivo = ArchivoAdmifarm.getMapping(rs);
			list.add(archivo);
		}
	
	} catch (Exception e) {
		logger.error("Error al buscar ultimas importaciones Admifarm Ospim General", e);
		throw new SystemException(e);
	} finally {
		ConnectionHelper.cerrar(stmt, con);
	}
	
	return list;
	}
	
	public List<DetalleAdmifarm> getListaDetalleAdmifarm(String nombreTabla)
	        throws SystemException {
	    Connection con = null;
	    CallableStatement stmt = null;
	    List<DetalleAdmifarm> list = new ArrayList<DetalleAdmifarm>();

	    try {
	        String sql = "{call reporte_admifarm_monotributo(?)}";

	        con = ConnectionHelper.getConnection();
	        stmt = con.prepareCall(sql);
	        stmt.setString(1, nombreTabla);

	        ResultSet rs = stmt.executeQuery();

	        while (rs.next()) {
	            list.add(DetalleAdmifarm.getMapping(rs));
	        }

	    } catch (Exception e) {
	        logger.error("Error cargando detalle ADMIFARM", e);
	        throw new SystemException(e);
	    } finally {
	        ConnectionHelper.cerrar(stmt, con);
	    }

	    return list;
	}
	
	public List<DetalleAdmifarm> getListaDetalleAdmifarmOspimGeneral(String nombreTabla)
	        throws SystemException {
	    Connection con = null;
	    CallableStatement stmt = null;
	    List<DetalleAdmifarm> list = new ArrayList<DetalleAdmifarm>();

	    try {
	        String sql = "{call reporte_admifarm_ospim_general(?)}";

	        con = ConnectionHelper.getConnection();
	        stmt = con.prepareCall(sql);
	        stmt.setString(1, nombreTabla);

	        ResultSet rs = stmt.executeQuery();

	        while (rs.next()) {
	            list.add(DetalleAdmifarm.getMapping(rs));
	        }

	    } catch (Exception e) {
	        logger.error("Error cargando detalle ADMIFARM OSPIM GENERAL", e);
	        throw new SystemException(e);
	    } finally {
	        ConnectionHelper.cerrar(stmt, con);
	    }

	    return list;
	}
	
	
}
