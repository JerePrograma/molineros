package ar.com.ospim.rrhh.action;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import ar.com.ospim.rrhh.beans.RegistroAcceso;
import ar.com.ospim.rrhh.services.ProcesaArchivoHorariosServiceImpl;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.model.User;

/**
 * Read and write a file using an explicit encoding. Removing the encoding from
 * this code will simply cause the system's default encoding to be used instead.
 */
public final class ProcesaArchivoHorarios {
	private static Log _log = LogFactoryUtil.getLog(ProcesaArchivoHorarios.class);
    private ProcesaArchivoHorariosServiceImpl servicio = new ProcesaArchivoHorariosServiceImpl();
    
	public void procesarArchivoImportaHorarios(Scanner scanner, int origenEdificio, User user)
			throws IOException, ParseException, SQLException {		
		List<RegistroAcceso> detalleList = new ArrayList<RegistroAcceso>();
		try {
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				if (null != line && !line.trim().equals("")) {
					if(line.contains("LECTOR 1") || line.contains("LECTOR 2")) {
					  RegistroAcceso deta = new RegistroAcceso(line,origenEdificio);
					  detalleList.add(deta);
					}else {
						
					}
				}
			}
		} finally {
			scanner.close();
		}
		_log.debug("Se estan por registrar las entradas de fichadas cantidad: " + detalleList.size());
		
		
		servicio.grabaArchivo(detalleList,origenEdificio, user);
	}
	
	public void procesarArchivoImportaHorariosConVerificacion(Scanner scanner, int origenEdificio, User user)
			throws IOException, ParseException, SQLException {		
		List<RegistroAcceso> detalleList = new ArrayList<RegistroAcceso>();
		try {
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				if (null != line && !line.trim().equals("")) {
					RegistroAcceso deta = new RegistroAcceso(line,origenEdificio);
					detalleList.add(deta);
				}
			}
		} finally {
			scanner.close();
		}
		_log.debug("Se estan por registrar las entradas de fichadas cantidad: " + detalleList.size());
		
		servicio.grabaArchivo(detalleList,origenEdificio, user);
		
	}
	
	public List<RegistroAcceso> verificaLosDiques(){
		return servicio.verificaLosDiques();
	}
	
	public List<RegistroAcceso> buscarUltimasLecturasAccesoLosDiques(){
		return servicio.buscarUltimasLecturasAccesoLosDiques();
	}
	
	public void corrigeFichadaLosDiques(Integer id, String tipoRegistro, User user) {
		servicio.corrigeFichadaLosDiques(id, tipoRegistro, user);
	}

}
