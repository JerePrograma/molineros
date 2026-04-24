package ar.com.ospim.comprobantesPortalProveedores.services;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.model.User;
import com.liferay.portlet.documentlibrary.model.impl.DLFileEntryImpl;

import ar.com.ospim.autorizaciones.beans.AutorizacionPrestacional;
import ar.com.ospim.autorizaciones.beans.Nomenclador;
import ar.com.ospim.comprobantesPortalProveedores.beans.ComprobanteAcompanante;
import ar.com.ospim.comprobantesPortalProveedores.beans.ComprobanteFiltro;
import ar.com.ospim.comprobantesPortalProveedores.beans.ComprobanteHospital;
import ar.com.ospim.comprobantesPortalProveedores.beans.ComprobanteIntegracion;
import ar.com.ospim.comprobantesPortalProveedores.beans.Sector;
import ar.com.ospim.global.beans.ClaseBase;
import ar.com.ospim.global.beans.Comprobante;
import ar.com.ospim.util.ConnectionHelper;

public class ComprobanteServiceUtil {
	private static Log _log = LogFactoryUtil
			.getLog(ComprobanteServiceUtil.class);

	private static ComprobanteServiceImpl instance = null;
	
	public static ComprobanteServiceImpl getInstance() {
		if (null == instance) {
			instance = new ComprobanteServiceImpl();
		}
		return instance;
	}

	
	public static void savecomprobanteProveedor(Comprobante comprobante, String user)
			throws Exception {
		getInstance().saveComprobanteProveedor(comprobante, user, null);
	}
	
	public static List<ClaseBase> getSectoresByUser(
			String user) throws Exception {
		List<ClaseBase> sectores =getInstance().getSectoresByUser(user);

		return sectores;
	}
	
	public static List<Comprobante> getLista(
			ComprobanteFiltro filtro,Integer pagina) throws Exception {
		String idFacturaImg="";
		List<Comprobante> comprobantes = (List<Comprobante>) getInstance().getLista(filtro,pagina);
		for(Comprobante comprobante:comprobantes) {
			idFacturaImg = comprobante.getAcreedorEmpresa().getCuit()+"-"+comprobante.getTipoComprobante()+"-"+
					comprobante.getLetraComprobante()+String.format("%05d",comprobante.getPtoVenta())+comprobante.getNroComprobante();

			List<DLFileEntryImpl>list = ComprobanteServiceUtil.getImagenesComprobantes(idFacturaImg,"TODOS");
			
			//comprobante.setImagenes(list); 
			
			comprobante.setImagenes(new ArrayList<DLFileEntryImpl>());
			for(DLFileEntryImpl d :list){
				if(!d.getTitle().contains("-Recibo")) {
					comprobante.getImagenes().add(d);
				}else {
					comprobante.setImagenRecibo(d);
				}
			}
			
		}
		
		return comprobantes;
	}
	
	public static List<DLFileEntryImpl> getImagenesComprobantes(String titulo,String tipo)
			throws SystemException {
		Connection con = null;
		CallableStatement stmt = null;
		List<DLFileEntryImpl> list = null;
		try {
			String sql = "{call comprobantes.busca_imagenes_comprobantes(?,?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			
			stmt.setString(1, titulo);
			stmt.setString(2, tipo);
			
			ResultSet rs = stmt.executeQuery();
			list = new ArrayList<DLFileEntryImpl>();
			while (rs.next()) {
				DLFileEntryImpl a = new DLFileEntryImpl();
				a.setFolderId(rs.getLong("folderId"));
				a.setName(rs.getString("fileName"));
				a.setDescription(rs.getString("fileDescription"));
				a.setTitle(rs.getString("fileTitle"));
				a.setSize(rs.getInt("size"));
//				a.setCreateDate(arg0);
				
				list.add(a);
			}
		} catch (Exception e) {
			_log.error("Error al buscar Imagenes", e);
			throw new SystemException(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return list;
	}
	
	
	public static long updateComprobante(Comprobante comprobante, String screenName) throws Exception {
		Integer idC = 0; 
		Connection connection = null;		
		
		try {			
			connection = ConnectionHelper.getConnection();
			connection.setAutoCommit(false);
			idC=getInstance().updateComprobante(comprobante,screenName,connection);

			connection.commit();
	  } catch (Exception e) {
		  _log.error(e);
		  ConnectionHelper.rollback(connection);		
	  } finally {
		  ConnectionHelper.cerrar(connection);
	  }    
	  return idC;
	}

	
	public static List<Sector> getSectores() throws Exception {
		List<Sector> sectores =getInstance().getSectores();
		return sectores;
	}
	
	public static List<User> getUsuariosHabilitadosBySector(Long companyId,
			String idSector,int entidad,Connection connectionParameter) throws Exception {
		List<User> usuarios =getInstance().getUsuariosHabilitadosBySector(companyId,idSector,entidad,connectionParameter );

		return usuarios;
	}
	
	public static long addUsuarioHabilitado(String sector, User user) throws Exception {
		Integer idC = 0; 
		Connection connection = null;		
		
		try {			
			connection = ConnectionHelper.getConnection();
			connection.setAutoCommit(false);
			idC=getInstance().addUsuarioHabilitado(sector,user,connection);

			connection.commit();
	  } catch (Exception e) {
		  _log.error(e);
		  ConnectionHelper.rollback(connection);		
	  } finally {
		  ConnectionHelper.cerrar(connection);
	  }    
	  return idC;
	}
	
	public static long deleteUsuarioHabilitado(String sector, User user) throws Exception {
		Integer idC = 0; 
		Connection connection = null;		
		try {			
			connection = ConnectionHelper.getConnection();
			connection.setAutoCommit(false);
			idC=getInstance().deleteUsuarioHabilitado(sector,user,connection);

			connection.commit();
	  } catch (Exception e) {
		  _log.error(e);
		  ConnectionHelper.rollback(connection);		
	  } finally {
		  ConnectionHelper.cerrar(connection);
	  }    
	  return idC;
	}
	
	public static List<ComprobanteIntegracion> getListaIntegracion(
			ComprobanteFiltro filtro,Integer pagina) throws Exception {
		String idFacturaImg="";
		List<ComprobanteIntegracion> comprobantes = (List<ComprobanteIntegracion>) getInstance().getListaIntegracion(filtro,pagina);
		for(ComprobanteIntegracion comprobante:comprobantes) {
			idFacturaImg = comprobante.getAcreedorEmpresa().getCuit()+"-"+comprobante.getTipoComprobante()+"-"+
					comprobante.getLetraComprobante()+String.format("%05d",comprobante.getPtoVenta())+comprobante.getNroComprobante();

			List<DLFileEntryImpl>list = ComprobanteServiceUtil.getImagenesComprobantes(idFacturaImg,"TODOS");
			comprobante.setImagenes(list);
		}
		
		return comprobantes;
	}
	
	public static long updateCarpetaIntegracion(String ids,Boolean operacion,Date carpeta) throws Exception {
		Integer idC = 0; 
		Connection connection = null;		
		try {			
			connection = ConnectionHelper.getConnection();
			connection.setAutoCommit(false);
			idC=getInstance().updateCarpetaIntegracion(ids,operacion,carpeta,connection);

			connection.commit();
	  } catch (Exception e) {
		  _log.error(e);
		  ConnectionHelper.rollback(connection);		
	  } finally {
		  ConnectionHelper.cerrar(connection);
	  }    
	  return idC;
	}
	
	public static long updateComprobanteIntegracion(ComprobanteIntegracion comprobante, String screenName) throws Exception {
		Integer idC = 0; 
		Connection connection = null;		
		
		try {			
			connection = ConnectionHelper.getConnection();
			connection.setAutoCommit(false);
			idC=getInstance().updateComprobante(comprobante,screenName,connection);
			getInstance().updateComprobanteIntegracion(comprobante,screenName,connection);

			connection.commit();
	  } catch (Exception e) {
		  _log.error(e);
		  ConnectionHelper.rollback(connection);		
	  } finally {
		  ConnectionHelper.cerrar(connection);
	  }    
	  return idC;
	}
	
	public static Nomenclador buscaNomencladorSSSByCodigo(String id) {
		Nomenclador nomenclador = (Nomenclador) getInstance().buscaNomencladorSSSByCodigo(id); 
		return nomenclador;
		
	}
	
	public static List<ComprobanteIntegracion> validaExistenciaComprobante(
			ComprobanteFiltro filtro) throws Exception {
		List<ComprobanteIntegracion> comprobantes = (List<ComprobanteIntegracion>) getInstance().validaExistenciaComprobante(filtro);
		return comprobantes;
	}
	
	public static Integer eliminarIntegracionPeriodo(Integer periodo,String entidad)throws Exception{
		return getInstance().eliminarIntegracionPeriodo(periodo,entidad);
   }
	
	
   public static List<ComprobanteAcompanante> getListaAcompanantes(
			ComprobanteFiltro filtro,Integer pagina) throws Exception {
		String idFacturaImg="";
		List<ComprobanteAcompanante> comprobantes = (List<ComprobanteAcompanante>) getInstance().getListaAcompanantes(filtro,pagina);
		for(ComprobanteAcompanante comprobante:comprobantes) {
			idFacturaImg = comprobante.getAcreedorEmpresa().getCuit()+"-"+comprobante.getTipoComprobante()+"-"+
					comprobante.getLetraComprobante()+String.format("%05d",comprobante.getPtoVenta())+comprobante.getNroComprobante();

			List<DLFileEntryImpl>list = ComprobanteServiceUtil.getImagenesComprobantes(idFacturaImg,"TODOS");
			comprobante.setImagenes(list);
		}
		
		return comprobantes;
  }

   public static long updateComprobanteAcompanante(ComprobanteAcompanante comprobante, String screenName) throws Exception {
		Integer idC = 0; 
		Connection connection = null;		
		
		try {			
			connection = ConnectionHelper.getConnection();
			connection.setAutoCommit(false);
			idC=getInstance().updateComprobante(comprobante,screenName,connection);
			getInstance().updateComprobanteReclamo(comprobante,screenName,connection);
			connection.commit();
	  } catch (Exception e) {
		  _log.error(e);
		  ConnectionHelper.rollback(connection);		
	  } finally {
		  ConnectionHelper.cerrar(connection);
	  }    
	  return idC;
  } 
   
   
  public static List<AutorizacionPrestacional> getListaAutorizacionesPrestacionales(
			ComprobanteFiltro filtro) throws Exception {
		List<AutorizacionPrestacional> autorizaciones = 
				(List<AutorizacionPrestacional>) getInstance().getListaAutorizacionesPrestacionales(filtro);
				return autorizaciones;
  } 
  
  public static List<Comprobante> getAvisosPagoByFechaTransferencia(
			Date fecha) throws Exception {
		List<Comprobante> comprobantes = (List<Comprobante>) getInstance().getAvisosPagoByFechaTransferencia(fecha);
		return comprobantes;
  }
  
  
public static List<ComprobanteHospital> getListaHospitales(
			ComprobanteFiltro filtro,Integer pagina) throws Exception {
		String idFacturaImg="";
		List<ComprobanteHospital> comprobantes = (List<ComprobanteHospital>) getInstance().getListaHospitales(filtro,pagina);
		for(ComprobanteHospital comprobante:comprobantes) {
			idFacturaImg = comprobante.getAcreedorEmpresa().getCuit()+"-"+comprobante.getTipoComprobante()+"-"+
					comprobante.getLetraComprobante()+String.format("%05d",comprobante.getPtoVenta())+comprobante.getNroComprobante();

			List<DLFileEntryImpl>list = ComprobanteServiceUtil.getImagenesComprobantes(idFacturaImg,"TODOS");
			comprobante.setImagenes(list);
		}
		
		return comprobantes;
}

 public static long updateComprobanteHospital(ComprobanteHospital comprobante, String screenName) throws Exception {
		Integer idC = 0; 
		Connection connection = null;		
		
		try {			
			connection = ConnectionHelper.getConnection();
			connection.setAutoCommit(false);
			idC=getInstance().updateComprobante(comprobante,screenName,connection);
			getInstance().updateComprobanteLiquidacion(comprobante,screenName,connection);
			connection.commit();
	  } catch (Exception e) {
		  _log.error(e);
		  ConnectionHelper.rollback(connection);		
	  } finally {
		  ConnectionHelper.cerrar(connection);
	  }    
	  return idC;
} 
 
 
 public static List<ComprobanteHospital> getListaFarmacia(
			ComprobanteFiltro filtro,Integer pagina) throws Exception {
		String idFacturaImg="";
		List<ComprobanteHospital> comprobantes = (List<ComprobanteHospital>) getInstance().getListaFarmacia(filtro,pagina);
		for(ComprobanteHospital comprobante:comprobantes) {
			idFacturaImg = comprobante.getAcreedorEmpresa().getCuit()+"-"+comprobante.getTipoComprobante()+"-"+
					comprobante.getLetraComprobante()+String.format("%05d",comprobante.getPtoVenta())+comprobante.getNroComprobante();

			List<DLFileEntryImpl>list = ComprobanteServiceUtil.getImagenesComprobantes(idFacturaImg,"TODOS");
			comprobante.setImagenes(list);
		}
		
		return comprobantes;
} 
 
 
 public static List<ComprobanteHospital> getListaProveedores(
			ComprobanteFiltro filtro,Integer pagina) throws Exception {
		String idFacturaImg="";
		List<ComprobanteHospital> comprobantes = (List<ComprobanteHospital>) getInstance().getListaProveedores(filtro,pagina);
		for(ComprobanteHospital comprobante:comprobantes) {
			idFacturaImg = comprobante.getAcreedorEmpresa().getCuit()+"-"+comprobante.getTipoComprobante()+"-"+
					comprobante.getLetraComprobante()+String.format("%05d",comprobante.getPtoVenta())+comprobante.getNroComprobante();

			List<DLFileEntryImpl>list = ComprobanteServiceUtil.getImagenesComprobantes(idFacturaImg,"TODOS");
			comprobante.setImagenes(list);
		}
		
		return comprobantes;
}
 
 
 public static List<ComprobanteHospital> getListaGerenciadoras(
			ComprobanteFiltro filtro,Integer pagina) throws Exception {
		String idFacturaImg="";
		List<ComprobanteHospital> comprobantes = (List<ComprobanteHospital>) getInstance().getListaGerenciadoras(filtro, pagina);
		for(ComprobanteHospital comprobante:comprobantes) {
			idFacturaImg = comprobante.getAcreedorEmpresa().getCuit()+"-"+comprobante.getTipoComprobante()+"-"+
					comprobante.getLetraComprobante()+String.format("%05d",comprobante.getPtoVenta())+comprobante.getNroComprobante();

			List<DLFileEntryImpl>list = ComprobanteServiceUtil.getImagenesComprobantes(idFacturaImg,"TODOS");
			comprobante.setImagenes(list);
		}
		
		return comprobantes;
}  
   
}
