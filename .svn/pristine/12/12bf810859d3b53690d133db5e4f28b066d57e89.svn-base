package ar.com.ospim.farmacia.services;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ar.com.ospim.procesaArchivos.beans.padron.ArchivoPadronContribuyentes;
import ar.com.ospim.procesaArchivos.beans.padron.DetallePadronContribuyentes;
import ar.com.ospim.procesaArchivos.beans.vademecum.ArchivoManualDat;
import ar.com.ospim.procesaArchivos.beans.vademecum.DetalleManualDat;
import ar.com.ospim.procesaArchivos.services.ProcesaArchivosFarmaciaServiceImpl;
import ar.com.ospim.procesaArchivos.services.ProcesaArchivosServiceImpl;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

/**
 * Read and write a file using an explicit encoding. Removing the encoding from
 * this code will simply cause the system's default encoding to be used instead.
 */
public final class ProcesaArchivosFarmacia {
	private static Log _log = LogFactoryUtil.getLog(ProcesaArchivosFarmacia.class);

	public static void main(String... aArgs) throws IOException {
		System.out.println(new Date());
		File folderAProcesar = new File("C:\\AFIP\\aProcesar");
		File[] filesList = folderAProcesar.listFiles();
		ProcesaArchivosFarmacia proc = new ProcesaArchivosFarmacia();

		for (int i = 0; i < filesList.length; i++) {
			if (filesList[i].isFile()) {
				String name = filesList[i].getName().toUpperCase();
				BufferedReader reader = new BufferedReader(new FileReader(
						filesList[i].getAbsolutePath()));

				System.out.println("File " + name + " Absolute PATH: "
						+ filesList[i].getAbsolutePath());
				try {					
						proc.procesarListadoSSSuper(reader);
					
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println(filesList[i]);
					System.out.println(e.getMessage());
					// proc.moveFile(filesList[i], folderError);
				} finally {
					reader.close();
				}
			} else if (filesList[i].isDirectory()) {
			}
		}
		System.out.println(new Date());
	}

	/**
	 * Procesa archivos listado medicamentos Super
	 * padron
	 * 
	 * @param scanner
	 * @throws IOException
	 * @throws ParseException
	 * @throws SQLException
	 */
	public void procesarListadoSSSuper(BufferedReader scanner) throws IOException,
			ParseException,
			SQLException {
		
		ArchivoPadronContribuyentes nuevoArchivo = new ArchivoPadronContribuyentes();
		List<DetallePadronContribuyentes> detalleList = new ArrayList<DetallePadronContribuyentes>();		
		String line = null;
		
		while ((line = scanner.readLine()) != null) {
			if (null != line && !line.trim().equals("")){
				detalleList.add(new DetallePadronContribuyentes(line));
			}
		}
	
		nuevoArchivo.setDetalle(detalleList);
		BigInteger cantRegs = new BigInteger(nuevoArchivo.getFooter()
				.getCantRegistros());		
		_log.debug(nuevoArchivo.getFooter());

		ProcesaArchivosServiceImpl servicio = new ProcesaArchivosServiceImpl();
		servicio.grabaArchivo(nuevoArchivo);
	}

	public void procesarArchivoManualDat(BufferedReader scanner)
			throws IOException, ParseException, SQLException {
		ArchivoManualDat nuevoArchivo = new ArchivoManualDat();
		List<DetalleManualDat> detalleList = new ArrayList<DetalleManualDat>();
		String line = null;
		while ((line = scanner.readLine()) != null) {
			if (null != line && !line.trim().equals("")) {
				DetalleManualDat deta = new DetalleManualDat(line);
				deta.toString();
				detalleList.add(deta);
			}
		}
		nuevoArchivo.setDetalle(detalleList);

		ProcesaArchivosFarmaciaServiceImpl servicio = new ProcesaArchivosFarmaciaServiceImpl();
		servicio.grabaArchivo(nuevoArchivo);

	}

}
