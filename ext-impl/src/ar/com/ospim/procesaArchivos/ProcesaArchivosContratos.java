package ar.com.ospim.procesaArchivos;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import ar.com.ospim.prestadores.beans.ConvenioPrestacional;
import ar.com.ospim.prestadores.beans.ConvenioPrestacionalDetalle;
import ar.com.ospim.procesaArchivos.beans.vademecum.ArchivoListadoSSSalud;
import ar.com.ospim.procesaArchivos.beans.vademecum.DetalleListadoSSSalud;
import ar.com.ospim.procesaArchivos.services.ProcesaArchivosFarmaciaServiceImpl;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

/**
 * Read and write a file using an explicit encoding. Removing the encoding from
 * this code will simply cause the system's default encoding to be used instead.
 */
public final class ProcesaArchivosContratos {
	private static Log _log = LogFactoryUtil
			.getLog(ProcesaArchivosContratos.class);

	public void procesarArchivoImportaContrato(BufferedReader scanner)
			throws IOException, ParseException, SQLException {
		ConvenioPrestacional contrato = new ConvenioPrestacional();
		List<ConvenioPrestacionalDetalle> detalleList = new ArrayList<ConvenioPrestacionalDetalle>();
		String line = null;
		while ((line = scanner.readLine()) != null) {
			if (null != line && !line.trim().equals("")) {
				ConvenioPrestacionalDetalle deta = new ConvenioPrestacionalDetalle(
						line);
				detalleList.add(deta);
			}
		}
		contrato.setConvenioPrestDetalle(detalleList);
		// ProcesaArchivosFarmaciaServiceImpl servicio = new
		// ProcesaArchivosFarmaciaServiceImpl();
		// servicio.grabaArchivo(nuevoArchivo);
	}

	public void procesarArchivoListadoActualizaValores(BufferedReader scanner)
			throws IOException, ParseException, SQLException {
		ArchivoListadoSSSalud nuevoArchivo = new ArchivoListadoSSSalud();
		List<DetalleListadoSSSalud> detalleList = new ArrayList<DetalleListadoSSSalud>();
		String line = null;
		for (int i = 0; i < 5; i++) {
			scanner.readLine();
		}
		while ((line = scanner.readLine()) != null) {
			System.out.println("LINE: " + line);
			if (null != line) {
				DetalleListadoSSSalud deta = new DetalleListadoSSSalud(line);
				detalleList.add(deta);
			}
		}
		nuevoArchivo.setDetalle(detalleList);

		ProcesaArchivosFarmaciaServiceImpl servicio = new ProcesaArchivosFarmaciaServiceImpl();
		servicio.grabaArchivo(nuevoArchivo);

	}

	/*
	 * public void actualizarVademecum()throws SQLException {
	 * ProcesaArchivosFarmaciaServiceImpl servicio = new
	 * ProcesaArchivosFarmaciaServiceImpl(); servicio.actualizaVademecum();
	 * 
	 * }
	 */

}
