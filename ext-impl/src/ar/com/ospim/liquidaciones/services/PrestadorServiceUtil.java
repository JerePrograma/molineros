package ar.com.ospim.liquidaciones.services;

import java.sql.SQLException;
import java.util.List;


import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.beans.Empresa;
import ar.com.ospim.global.beans.Regimen;
import ar.com.ospim.global.services.EmpresaServiceUtil;
import ar.com.ospim.liquidaciones.ImposibleBorrarPrestadorException;
import ar.com.ospim.liquidaciones.beans.MatriculaPrestador;
import ar.com.ospim.liquidaciones.beans.Prestador;
import ar.com.ospim.liquidaciones.beans.PrestadorPlan;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.model.User;

public class PrestadorServiceUtil {

	private static Log _log = LogFactoryUtil.getLog(PrestadorServiceUtil.class);
	
	private static PrestadorServiceImpl instance = null;

	public static PrestadorServiceImpl getInstance() {
		if (null == instance) {
			instance = new PrestadorServiceImpl();
		}
		return instance;
	}

//	public static List<Prestador> getPrestadores(int id, String cuit, String descripcion)
//			throws Exception {
//		return getInstance().getPrestadores(id, cuit, descripcion);
//	}
	
	public static List<Prestador> getPrestadores(int id, String cuit, String descripcion, boolean soloVigentes)
			throws Exception {
		
		return getInstance().getPrestadores(id, cuit, descripcion, soloVigentes);
	}
	
	public static List<Prestador> getPrestadores(int id, String cuit, String descripcion, boolean soloVigentes,boolean soloHospitales)
			throws Exception {
		
		return getInstance().getPrestadores(id, cuit, descripcion, soloVigentes,soloHospitales);
	}

	public static List<Prestador> getPrestadores(int id, String cuit, String descripcion, int provincia, int localidad, boolean soloVigentes,
			int profesion, int especialidad, int subEspecialidad, int tipoPrestador)
		throws Exception {
		
		return getInstance().getPrestadores(id, cuit, descripcion, provincia, localidad, soloVigentes,
				profesion, especialidad, subEspecialidad, tipoPrestador );
	}

	
	public static Prestador getPrestador(int id) throws Exception {
		return getInstance().getPrestador(id);
	}

	public static List<String> getRubrosPrestador(int idPrestador) throws Exception {
		return getInstance().getRubrosPrestador(idPrestador);
	}
	
	public static void update(Prestador prestador, User user)
			throws Exception {
			
			//		No hacemos mas EmpresaServiceUtil.update desde prestador
		
		getInstance().actualizar(prestador, user.getScreenName());
	}

	public static int actualizarSolicitarCotizacionPrestador(int idPrestador, boolean solicitarCotizacion, User user)
			throws Exception {

	    return getInstance().actualizarSolicitarCotizacionPrestador(
	            idPrestador,
	            solicitarCotizacion,
	            user.getScreenName()
	    );
	}
	
	public static void actualizarRubrosPrestador(int idPrestador, List<String> rubros, User user)
			throws Exception {

	    getInstance().actualizarRubrosPrestador(
	            idPrestador,
	            rubros,
	            user.getScreenName()
	    );
	}

	public static void borrar(int id, User user)
			throws ImposibleBorrarPrestadorException, SQLException {
		getInstance().borrar(id, user.getScreenName());

	}
	
	// traigo las matriculas por id del prestador
	public static List<MatriculaPrestador> getMatriculas(int id)
		throws Exception {
		return getInstance().getMatriculas(id);
	}

	public static int insertar(Prestador prestador, User user) throws Exception{
		int idPrestador = 0;
		Empresa empresa = null;
		
		try{
			empresa = EmpresaServiceUtil.getEmpleadorCompleto(prestador.getCuit(), "000");
			if (empresa == null) {
					Empresa emp= new Empresa(prestador.getCuit(), "000", prestador.getDescripcion());
					emp.setId_ramo_empresa(WebKeysGlobal.ID_RAMO_EMPRESA);
					if(prestador.getCbu()!= null) {
						emp.setCBU(prestador.getCbu());
					}
					
                    Regimen reg = new Regimen(94);	
					emp.setRegimen(reg);
					
					EmpresaServiceUtil.save(emp,user.getScreenName());				
			}else {
				if(prestador.getCbu()!= null && !"".equalsIgnoreCase(prestador.getCbu())) {
					empresa.setCBU(prestador.getCbu());
					EmpresaServiceUtil.updateCBU(empresa,user.getScreenName());
				}
			}
		}catch (Exception e) {
			_log.error("Se grabó un prestador sin existir en informacion_afip.Empresa");
			_log.error(e);
		}	
		
		idPrestador = getInstance().insertar(prestador, user.getScreenName());
		
		if ( prestador.getTipo().getId()==5) { // Hospitales que son sucursales de una municipalidad
			Empresa emp= new Empresa(prestador.getCuit(), String.valueOf(idPrestador), prestador.getDescripcion());
			emp.setId_ramo_empresa(WebKeysGlobal.ID_RAMO_EMPRESA);
			if(prestador.getCbu()!= null) {
				emp.setCBU(prestador.getCbu());
			}
			Regimen reg = new Regimen(94);	
			emp.setRegimen(reg);
			EmpresaServiceUtil.save(emp,user.getScreenName());
		}
		return idPrestador;
	}
	
	public static List<PrestadorPlan> getPlanesDelPrestador(int idPrestador) {
		return getInstance().getPlanesDelPrestador(idPrestador);
	}
	
	public static List<Prestador> getPrestadores(int id, String cuit, String descripcion, int provincia, int localidad, boolean soloVigentes,
			int profesion, int especialidad, int subEspecialidad, int tipoPrestador,String hospital)
		throws Exception {
		
		return getInstance().getPrestadores(id, cuit, descripcion, provincia, localidad, soloVigentes,
				profesion, especialidad, subEspecialidad, tipoPrestador,hospital );
	}

	public static List<Prestador> getPrestadores(int id, String cuit, String descripcion,
			int provincia, int localidad, boolean soloVigentes, int profesion,
			int especialidad, int subEspecialidad, int tipoPrestador, String hospital,
			boolean soloHabilitadosCotizar) throws Exception {
		return getInstance().getPrestadores(id, cuit, descripcion, provincia, localidad,
				soloVigentes, profesion, especialidad, subEspecialidad, tipoPrestador,
				hospital, soloHabilitadosCotizar);
	}
}
