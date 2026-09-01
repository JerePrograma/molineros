package ar.com.ospim.procesaArchivos;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.portlet.ActionRequest;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.util.PortalUtil;

import ar.com.global.services.CalculaCapitalCuotaServiceUtil;
import ar.com.ospim.afiliados.WebKeysAfiliados;
import ar.com.ospim.afiliados.beans.Afiliado;
import ar.com.ospim.afiliados.services.EditarAfiliadoServiceUtil;
import ar.com.ospim.estudioisidro.LoteEmpresaExistenteException;
import ar.com.ospim.estudioisidro.WebKeysEstudioIsidro;
import ar.com.ospim.estudioisidro.beans.EstadoGestion;
import ar.com.ospim.estudioisidro.beans.Llamado;
import ar.com.ospim.estudioisidro.service.LlamadoServiceUtil;
import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.beans.Banco;
import ar.com.ospim.global.beans.Cheque;
import ar.com.ospim.global.beans.Cheque.Estado;
import ar.com.ospim.global.beans.ConceptoSueldos;
import ar.com.ospim.global.beans.CuentaCorriente;
import ar.com.ospim.global.beans.Delegacion;
import ar.com.ospim.global.beans.DepositoBancario;
import ar.com.ospim.global.beans.Efectivo;
import ar.com.ospim.global.beans.Empresa;
import ar.com.ospim.global.beans.FinanciacionTurismo;
import ar.com.ospim.global.beans.PlanCuentas;
import ar.com.ospim.global.beans.Retencion;
import ar.com.ospim.global.beans.TarjetaDebitoCredito;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.hoteles.beans.Recibo;
import ar.com.ospim.hoteles.services.HotelesServiceUtil;
import ar.com.ospim.novedades.beans.Novedad;
import ar.com.ospim.novedades.beans.NovedadPadronConsolidado;
import ar.com.ospim.novedades.exception.PeriodoArchivoDuplicadoException;
import ar.com.ospim.procesaArchivos.beans.ArchivoARBAPadronAlicuota;
import ar.com.ospim.procesaArchivos.beans.ArchivoAfipRG830;
import ar.com.ospim.procesaArchivos.beans.ArchivoOSAportes;
import ar.com.ospim.procesaArchivos.beans.ArchivoSubsidioMitigacionAsimetricas;
import ar.com.ospim.procesaArchivos.beans.ArchivoSubsidioMitigacionAsimetricasSuma;
import ar.com.ospim.procesaArchivos.beans.DetalleOSAportes;
import ar.com.ospim.procesaArchivos.beans.DetalleSuma;
import ar.com.ospim.procesaArchivos.beans.DetalleSumaXxxx;
import ar.com.ospim.procesaArchivos.beans.FooterNomOSAportes;
import ar.com.ospim.procesaArchivos.beans.FooterOSAportes;
import ar.com.ospim.procesaArchivos.beans.FooterSumaXxxx;
import ar.com.ospim.procesaArchivos.beans.HeaderOSAportes;
import ar.com.ospim.procesaArchivos.beans.HeaderSumaXxxx;
import ar.com.ospim.procesaArchivos.beans.JubiladosSitaci;
import ar.com.ospim.procesaArchivos.beans.desempleo.ArchivoDesempleo;
import ar.com.ospim.procesaArchivos.beans.desempleo.DetalleDesempleo;
import ar.com.ospim.procesaArchivos.beans.dj.ArchivoDJ;
import ar.com.ospim.procesaArchivos.beans.dj.DetalleDJ;
import ar.com.ospim.procesaArchivos.beans.dj.FooterDJ;
import ar.com.ospim.procesaArchivos.beans.dj.HeaderDJ;
import ar.com.ospim.procesaArchivos.beans.extraccionbancaria.ArchivoExtraccionBancaria;
import ar.com.ospim.procesaArchivos.beans.extraccionbancaria.DetalleExtraccionBancaria;
import ar.com.ospim.procesaArchivos.beans.extraccionbancaria.HeaderExtraccionBancaria;
import ar.com.ospim.procesaArchivos.beans.nacion.ListadoRendicionNacion;
import ar.com.ospim.procesaArchivos.beans.nacion.RendicionNacion;
import ar.com.ospim.procesaArchivos.beans.opcionesss.BajaOpcionesSS;
import ar.com.ospim.procesaArchivos.beans.opcionesss.DetalleBajasOpcionesSS;
import ar.com.ospim.procesaArchivos.beans.opcionesss.DetalleOpcionesSS;
import ar.com.ospim.procesaArchivos.beans.opcionesss.OpcionesSS;
import ar.com.ospim.procesaArchivos.beans.padron.ArchivoPadronContribuyentes;
import ar.com.ospim.procesaArchivos.beans.padron.DetalleAfipContribuyentes;
import ar.com.ospim.procesaArchivos.beans.padron.DetallePadronContribuyentes;
import ar.com.ospim.procesaArchivos.beans.so.ArchivoSubsidioOS;
import ar.com.ospim.procesaArchivos.beans.so.DetalleSubsidioOS;
import ar.com.ospim.procesaArchivos.beans.so.FooterSubsidioOS;
import ar.com.ospim.procesaArchivos.beans.so.HeaderSubsidioOS;
import ar.com.ospim.procesaArchivos.beans.transferenciaexterna.ArchivoTransferenciaExterna;
import ar.com.ospim.procesaArchivos.beans.transferenciaexterna.DetalleTransferenciaExterna;
import ar.com.ospim.procesaArchivos.beans.transferenciaexterna.FooterTransferenciaExterna;
import ar.com.ospim.procesaArchivos.beans.transferenciaexterna.HeaderTransferenciaExterna;
import ar.com.ospim.procesaArchivos.exception.AfipCantidadRegistrosIncorrectaException;
import ar.com.ospim.procesaArchivos.exception.ArchivoAdmifarmGeneralOspimIncorrectoException;
import ar.com.ospim.procesaArchivos.exception.ArchivoAdmifarmIncorrectoException;
import ar.com.ospim.procesaArchivos.exception.ArchivoMedEsIncorrectoException;
import ar.com.ospim.procesaArchivos.exception.RendicionBancoNacionRegistroDuplicado;
import ar.com.ospim.procesaArchivos.padron.FooterPadronContribuyentes;
import ar.com.ospim.procesaArchivos.padron.HeaderPadronContribuyentes;
import ar.com.ospim.procesaArchivos.services.ProcesaArchivosAdmifarmServiceImpl;
import ar.com.ospim.procesaArchivos.services.ProcesaArchivosServiceImpl;
import ar.com.ospim.tesoreria.beans.CuentaBancaria;
import ar.com.ospim.tesoreria.beans.FichaBoletaPortal;
import ar.com.ospim.tesoreria.beans.MovimientoBancario;
import ar.com.ospim.tesoreria.beans.contabilidad.Asiento.Detalle;
import ar.com.ospim.tesoreria.beans.contabilidad.CoeficienteAjusteInflacion;
import ar.com.ospim.tesoreria.service.ContabilidadServiceUtil;
import ar.com.ospim.tesoreria.service.PortalEmpleadoresServiceImpl;
import ar.com.ospim.tesoreria.services.MovimientoBancarioServiceImpl;
import ar.com.ospim.util.ConnectionHelper;
import ar.com.ospim.util.DateUtils;
import ar.com.ospim.webservice.dto.AfiliacionPrevencionDTO;
import ar.com.uoma.WebKeysUOMA;
import ar.com.uoma.beans.CentroCosto;
import ar.com.uoma.facturacion.Cliente;
import ar.com.uoma.facturacion.Factura;
import ar.com.uoma.facturacion.FacturaDetalle;
import ar.com.uoma.facturacion.FacturaIngreso;
import ar.com.uoma.facturacion.Producto;

/**
 * Read and write a file using an explicit encoding. Removing the encoding from
 * this code will simply cause the system's default encoding to be used instead.
 */
public final class ProcesaArchivos {
	private static Log _log = LogFactoryUtil.getLog(ProcesaArchivos.class);
	private ProcesaArchivosServiceImpl servicio = new ProcesaArchivosServiceImpl();
    private PortalEmpleadoresServiceImpl empleadoresService=new PortalEmpleadoresServiceImpl();
	public static void main(String... aArgs) throws IOException {
		// NO SE USA MAS LO VOY A USAR PARA PROCESAR TEMPORALMENTE UN ARCHIVO DE
		// ESTUDIO
		System.out.println(new Date());
		File folderAProcesar = new File("/home/sistemas-01/DEUDAS_OSPIM.csv");
		System.out.println("FOLDER: " + folderAProcesar.getAbsolutePath());
		BufferedReader reader = new BufferedReader(new FileReader(
				folderAProcesar));
		String linea = new String();
		String cuit = new String();
		BufferedWriter out = new BufferedWriter(new FileWriter(
				"/home/sistemas-01/DEUDAS_OSPIM_nuevo.csv"));
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMdd");
		Date fecha = null;
		Date fechaAnt = null;
		boolean error = false;
		while (null != linea) {
			System.out.println("LINEA: " + linea.length());
			if (linea.length() > 0) {
				if (linea.contains("|")) {
					cuit = linea.substring(0, linea.indexOf("|"));
					linea = linea.substring(linea.indexOf("|") + 1,
							linea.length());
					System.out.println("LINEA A: " + linea);
				}
				try {
					System.out.println("Fecha: "
							+ linea.substring(linea.indexOf("|") + 1,
									linea.indexOf("|") + 11));
					fecha = sdf.parse(linea.substring(linea.indexOf("|") + 1,
							linea.indexOf("|") + 11));
					fechaAnt = fecha;
					error = false;
				} catch (Exception e) {
					error = true;
					System.out.println("ERROR!");
				}
				if (error) {
					linea = cuit + "|" + sdf2.format(fechaAnt) + "|" + linea;
				} else {
					linea = cuit + "|" + sdf2.format(fechaAnt) + "|"
							+ linea.substring(10, linea.length());
				}

				// linea = sdf.format(fechaAnt) + linea;

				out.write(linea + "\n");
			}
			linea = reader.readLine();
		}
		out.close();

	}

	/**
	 * Procesa archivos .dj que pueden ser declaraciones juradas o archivos
	 * padron
	 * 
	 * @param scanner
	 * @throws IOException
	 * @throws ParseException
	 * @throws AfipCantidadRegistrosIncorrectaException
	 * @throws SQLException
	 */
	
	
@SuppressWarnings("deprecation")
	public void procesarArchivoMedEsp(User usuario,BufferedReader scanner,Date fechaArchivo  ) throws IOException,
			ParseException, ArchivoMedEsIncorrectoException,SQLException, Exception {
		int salidas=0; 
		int mes;
		int anio;
		Calendar cal = Calendar.getInstance();
		String text = scanner.readLine(); 
		if (text== null) {
			_log.error("Error en primer linea del archivo");
			
			throw new ParseException(
					"Error en primer linea del archivo", 0);      	    
		}
		scanner.mark(1);
		text = scanner.readLine(); 
		try {
			mes =Integer.parseInt(text.substring(0,2));
			anio=Integer.parseInt(text.substring(3,7));
			cal.setTime(fechaArchivo);
			int mes1 = cal.get(Calendar.MONTH)+1;
			int anio1 = cal.get(Calendar.YEAR );
			
				if (mes1!= mes || anio1  !=anio){
					_log.error("PERIODO NO COINCIDENTE CON ARCHIVO");
					    salidas=1;  // PERIODO NO COINCIDENTE CON ARCHIVO 1 
				}else{
					if (servicio.buscaPeriodoProcesado("medespecial_cabecera", fechaArchivo  ) ) {
					//if (servicio.elPeriodoMedEspYafueImportado( FechaArchivo  )) {	
						_log.error("PERIODO YA IMPORTADO");
						salidas=3;  // PERIODO YA IMPORTADO  3  
					}else{		 
						scanner.reset();
						servicio.procesarArchivoMedEsp(text,scanner,usuario.getScreenName() , mes,anio,fechaArchivo);	
					}     
			    }
			}catch(NumberFormatException nfe){
				_log.error(nfe);
			}catch(ArchivoMedEsIncorrectoException e1){
				_log.error(e1);
			    throw new ArchivoMedEsIncorrectoException("1");//PERIODO NO COINCIDENTE CON ARCHIVO
			}catch(Exception e){
				_log.error(e);
				throw new ArchivoMedEsIncorrectoException("4"); // ERRORES CON LOS DATOS DEL ARCHIVO 
			}
			
			
			switch (salidas) {
				case 3:  
					 throw new ArchivoMedEsIncorrectoException("3");         
				case 1: 
					 throw new ArchivoMedEsIncorrectoException("1");
			}
	}



	public void procesarArchivoDesglose (User usuario,HSSFWorkbook workbook ,Date FechaArchivo  ) throws IOException,
		ParseException, ArchivoMedEsIncorrectoException,SQLException, Exception {
		
		int salidas=0; 
		
		Calendar cal = Calendar.getInstance(); 
		 
		try {
			cal.setTime(FechaArchivo);
			int mes = cal.get(Calendar.MONTH)+1;
			int anio = cal.get(Calendar.YEAR );
			
			//if (servicio.elPeriodoDesgloseYafueImportado ( FechaArchivo  )) {
			if (servicio.buscaPeriodoProcesado ("liquidacion_prevencion_archivos", FechaArchivo  )) {				
				
				_log.debug("PERIODO YA IMPORTADO");
				salidas=3;  			
			}else{		
				servicio.procesarArchivoDesglose(workbook,usuario.getScreenName() , mes,anio,FechaArchivo);	
			}     
		
		}catch(NumberFormatException nfe){
			_log.error(nfe);
		}catch(ArchivoMedEsIncorrectoException e1){
			_log.error(e1);
		}catch(Exception e){
			_log.error(e);
			throw new ArchivoMedEsIncorrectoException("4"); // ERRORES CON LOS DATOS DEL ARCHIVO 
		}
		
		
		switch (salidas) {
			case 3:  
				 throw new ArchivoMedEsIncorrectoException("3");         
//			case 1: 
//				 throw new ArchivoMedEsIncorrectoException("1");
		}
	}


	public void procesarArchivoDJ(BufferedReader scanner) throws IOException,
			ParseException, AfipCantidadRegistrosIncorrectaException,
			SQLException, Exception {
		ArchivoDJ nuevoArchivo = new ArchivoDJ();
		List<DetalleDJ> detalleList = new ArrayList<DetalleDJ>();

		String line = scanner.readLine();

		if (line == null || line.length() < 24) {
			throw new ParseException(
					"Error al parsear archivo, header incorrecto", 0);
		}

		String tipo = line.substring(10, 23).trim();
		if (tipo.equals("DDJJ-PADRON") || tipo.equals("DDJJ_PADRON") ) {
			procesarArchivoPA(line, scanner);
			return;
		} else if (!tipo.equals("DDJJ-NOMINAS")) {
			return;
		}

		nuevoArchivo.setHeader(new HeaderDJ(line));
		while ((line = scanner.readLine()) != null) {
			if (null != line && !line.trim().equals("")
					&& line.substring(0, 2).equals("TF")) {
				nuevoArchivo.setFooter(new FooterDJ(line));
			} else if (line != null && !line.trim().equals("")) {
				detalleList.add(new DetalleDJ(line));
			}
		}
		nuevoArchivo.setDetalle(detalleList);
		BigInteger cantRegs = new BigInteger(nuevoArchivo.getFooter()
				.getCantRegistros());
		if (!cantRegs.equals(new BigInteger(String.valueOf(nuevoArchivo
				.getDetalle().size())))) {
			throw new AfipCantidadRegistrosIncorrectaException();
		}
		_log.debug(nuevoArchivo.getFooter());

		servicio.grabaArchivo(nuevoArchivo);

	}

	@SuppressWarnings("unused")
	private void moveFile(File file, File newFolder) {
		// Move file to new directory
		boolean success = file.renameTo(new File(newFolder, file.getName()));
		if (!success) {
			_log.debug("ERROR AL MOVER ARCHIVO: " + file.getName());
		} else {
			_log.debug("FILE " + file.getName() + " MOVED");
		}

	}

	public ProcesaArchivos() {
	}

	public void procesarArchivoOS(BufferedReader scanner) throws IOException,
			SQLException, AfipCantidadRegistrosIncorrectaException,
			ParseException {
		ArchivoOSAportes nuevoArchivo = new ArchivoOSAportes();
		List<DetalleOSAportes> detalleList = new ArrayList<DetalleOSAportes>();
		List<FooterNomOSAportes> listaFooterFN = new ArrayList<FooterNomOSAportes>();

		// Encabezado...
		String line = scanner.readLine();
		nuevoArchivo.setHeader(new HeaderOSAportes(line));
		boolean tieneTn = false;
		while ((line = scanner.readLine()) != null) {
			if (line.length() > 2) {
				if (null != line && !line.trim().equals("")
						&& line.substring(0, 2).equals("TN")) {
					listaFooterFN.add(new FooterNomOSAportes(line));
					tieneTn = true;
				} else if (null != line && !line.trim().equals("")
						&& line.substring(0, 2).equals("TF")) {
					nuevoArchivo.setFooter(new FooterOSAportes(line, tieneTn));
				} else if (line != null && !line.trim().equals("")) {
					detalleList.add(new DetalleOSAportes(line));
				}
			}
		}
		nuevoArchivo.setDetalle(detalleList);
		nuevoArchivo.setFooterNom(listaFooterFN);
		BigInteger cantRegs = new BigInteger(String.valueOf(nuevoArchivo
				.getFooter().getCantidad_registros()));
		if (!cantRegs.equals(new BigInteger(String.valueOf(nuevoArchivo
				.getDetalle().size())))) {
			throw new AfipCantidadRegistrosIncorrectaException();
		}

		servicio.grabaArchivo(nuevoArchivo);
	}

	public void procesarArchivoPA(String header, BufferedReader scanner)
			throws IOException, ParseException,
			AfipCantidadRegistrosIncorrectaException, SQLException {
		
		ArchivoPadronContribuyentes nuevoArchivo = new ArchivoPadronContribuyentes();
		
		List<DetallePadronContribuyentes> detalleList = new ArrayList<DetallePadronContribuyentes>();

		nuevoArchivo.setHeader(new HeaderPadronContribuyentes(header));
		
		String line = header;
		
		while ((line = scanner.readLine()) != null) {
			if (null != line && !line.trim().equals("")
					&& line.substring(0, 2).equals("TF")) {
				nuevoArchivo.setFooter(new FooterPadronContribuyentes(line));
			} else if (line != null && !line.trim().equals("")) {
				detalleList.add(new DetallePadronContribuyentes(line));
			}
		}
		nuevoArchivo.setDetalle(detalleList);
		BigInteger cantRegs = new BigInteger(nuevoArchivo.getFooter()
				.getCantRegistros());
		if (!cantRegs.equals(new BigInteger(String.valueOf(nuevoArchivo
				.getDetalle().size())))) {
			throw new AfipCantidadRegistrosIncorrectaException();
		}
		_log.debug(nuevoArchivo.getFooter());

		servicio.grabaArchivo(nuevoArchivo);

	}

	public void procesarArchivoDesempleo(BufferedReader scanner)
			throws IOException, ParseException, SQLException, RendicionBancoNacionRegistroDuplicado {
		ArchivoDesempleo nuevoArchivo = new ArchivoDesempleo();
		List<DetalleDesempleo> detalleList = new ArrayList<DetalleDesempleo>();
		String line = null;
		while ((line = scanner.readLine()) != null) {
			if (null != line && !line.trim().equals("")) {
				DetalleDesempleo deta = new DetalleDesempleo(line);
				deta.toString();
				detalleList.add(deta);
			}
		}
		nuevoArchivo.setDetalleDesempleo(detalleList);

		servicio.grabaArchivo(nuevoArchivo);

	}

	public void procesarArchivoSO(BufferedReader scanner) throws IOException,
			ParseException, AfipCantidadRegistrosIncorrectaException,
			SQLException {
		ArchivoSubsidioOS nuevoArchivo = new ArchivoSubsidioOS();
		List<DetalleSubsidioOS> detalleList = new ArrayList<DetalleSubsidioOS>();

		String line = scanner.readLine();
		nuevoArchivo.setHeader(new HeaderSubsidioOS(line));
		while ((line = scanner.readLine()) != null) {
			if (null != line && !line.trim().equals("")
					&& line.substring(0, 2).equals("TR")) {
				nuevoArchivo.setFooter(new FooterSubsidioOS(line));
			} else if (line != null && !line.trim().equals("")) {
				detalleList.add(new DetalleSubsidioOS(line));
			}
		}
		nuevoArchivo.setDetalle(detalleList);
		FooterSubsidioOS footer = nuevoArchivo.getFooter();
		BigInteger cantRegs = footer.getCantidadRegistrosDE()
				.add(footer.getCantidadRegistrosDT())
				.add(footer.getCantidadRegistrosTO());
		if (!cantRegs.equals(new BigInteger(String.valueOf(nuevoArchivo
				.getDetalle().size())))) {
			throw new AfipCantidadRegistrosIncorrectaException();
		}
		_log.debug(footer);

		servicio.grabaArchivo(nuevoArchivo);

	}

	public void procesarArchivoExtraccionBancaria(BufferedReader scanner)
			throws IOException, ParseException, SQLException {
		ArchivoExtraccionBancaria nuevoArchivo = new ArchivoExtraccionBancaria();
		List<DetalleExtraccionBancaria> detalleList = new ArrayList<DetalleExtraccionBancaria>();

		String line = scanner.readLine();
		nuevoArchivo.setHeaderExtraccionBancaria(new HeaderExtraccionBancaria(
				line));
		while ((line = scanner.readLine()) != null) {
			if (line != null && !line.trim().equals("")) {
				detalleList.add(new DetalleExtraccionBancaria(line));
			}
		}
		nuevoArchivo.setDetalleList(detalleList);

		servicio.grabaArchivo(nuevoArchivo);

	}

	public void procesarArchivoTransferenciaExterna(BufferedReader scanner)
			throws IOException, ParseException, SQLException {
		ArchivoTransferenciaExterna nuevoArchivo = new ArchivoTransferenciaExterna();
		List<DetalleTransferenciaExterna> detalleList = new ArrayList<DetalleTransferenciaExterna>();

		String line = null;
		while ((line = scanner.readLine()) != null) {
			if (line != null && line.startsWith("HF")) {
				nuevoArchivo.setHeader(new HeaderTransferenciaExterna(line));
			} else if (line != null && line.startsWith("TF")) {
				nuevoArchivo.setFooter(new FooterTransferenciaExterna(line));
			} else if (line != null && !line.trim().equals("")) {
				DetalleTransferenciaExterna det = new DetalleTransferenciaExterna(
						line);
				if (det.getClasificacionExpediente() == 60
						&& detalleList.contains(det)) {
					detalleList.get(detalleList.indexOf(det)).getFacturas()
							.addAll(det.getFacturas());
				} else {
					detalleList.add(det);
				}
			}
		}
		nuevoArchivo.setDetalleList(detalleList);

		servicio.grabaArchivo(nuevoArchivo);
	}

	public void procesarArchivoBajaOpciones(BufferedReader scanner)
			throws IOException, ParseException, SQLException {
		BajaOpcionesSS nuevoArchivo = new BajaOpcionesSS();
		List<DetalleBajasOpcionesSS> detalleList = new ArrayList<DetalleBajasOpcionesSS>();
		String line = null;
		while ((line = scanner.readLine()) != null && !line.startsWith("CK")) {
			detalleList.add(new DetalleBajasOpcionesSS(line));
		}
		nuevoArchivo.setDetalle(detalleList);

		servicio.grabaArchivoBajaOpciones(nuevoArchivo);
	}

	public void procesarArchivoAltasOpciones(BufferedReader scanner)
			throws IOException, ParseException, SQLException {
		OpcionesSS nuevoArchivo = new OpcionesSS();
		List<DetalleOpcionesSS> detalleList = new ArrayList<DetalleOpcionesSS>();
		String line = null;
		while ((line = scanner.readLine()) != null) {
			detalleList.add(DetalleOpcionesSS.altaVuelta(line));
		}
		nuevoArchivo.setDetalle(detalleList);

		servicio.grabaArchivoOpciones(nuevoArchivo);
	}

	public void procesarArchivoAltasOpcionesMonotrib(BufferedReader scanner)
			throws IOException, ParseException, SQLException {
		OpcionesSS nuevoArchivo = new OpcionesSS();
		List<DetalleOpcionesSS> detalleList = new ArrayList<DetalleOpcionesSS>();
		String line = null;
		while ((line = scanner.readLine()) != null) {
			detalleList.add(DetalleOpcionesSS.altaVueltaMono(line));
		}
		nuevoArchivo.setDetalle(detalleList);

		servicio.grabaArchivoOpciones(nuevoArchivo);
	}

	public void procesarArchivoRendicionNacion(BufferedReader scanner,
			int convenio) throws IOException, ParseException, SQLException,
			RendicionBancoNacionRegistroDuplicado {
		ListadoRendicionNacion nuevoArchivo = new ListadoRendicionNacion();
		List<RendicionNacion> detalleList = new ArrayList<RendicionNacion>();
		String line = null;
		while ((line = scanner.readLine()) != null) {
			detalleList.add(new RendicionNacion(line));
		}
		nuevoArchivo.setDetalle(detalleList);

		Connection con1 = ConnectionHelper.getReportesOspimConnection();
		servicio.grabaArchivoRendicionNacion(nuevoArchivo, convenio, con1, false);
		
		Connection con2 = ConnectionHelper.getConnectionPortalEmpleadoresV01();
		servicio.grabaArchivoRendicionNacion(nuevoArchivo, convenio, con2, true);
		
		ConnectionHelper.cerrar(con1);
		ConnectionHelper.cerrar(con2);
		
	}

	public void procesarArchivoErroresSS(BufferedReader scanner, int tipo)
			throws IOException, ParseException, SQLException,
			RendicionBancoNacionRegistroDuplicado {
		String line = null;
		ArrayList<DetalleOpcionesSS> detalleList = new ArrayList<DetalleOpcionesSS>();
		while ((line = scanner.readLine()) != null) {
			if (tipo == 0) {
				detalleList.add(new DetalleOpcionesSS().cargaError(line));
			} else if (tipo == 1) {
				detalleList.add(new DetalleOpcionesSS()
						.cargaErrorDesdeInforme(line));
			}
		}

		servicio.actualizaSuperErrores(detalleList);
	}

	public void procesarArchivoPadronEmpresa(BufferedReader scanner)
			throws IOException, ParseException, SQLException {
		ArrayList<Empresa> detalleList = new ArrayList<Empresa>();
		String line = null;
		int cont = 0;
		int pagina = 0;
		while ((line = scanner.readLine()) != null) {
			cont++;
			detalleList.add(Empresa.getFromPadronAfip(line));
			if (cont == 40000) {
				pagina++;
				_log.debug("Voy!!" + cont + " Pagina: " + pagina);
				ProcesaArchivosServiceImpl servicio = new ProcesaArchivosServiceImpl();
				servicio.grabaArchivoPadronEmpresa(detalleList);
				detalleList = new ArrayList<Empresa>();
				cont = 0;
			}
		}
		if (cont > 0) {

			servicio.grabaArchivoPadronEmpresa(detalleList);
		}
	}

	/**
	 * Procesa archivos .txt que son de opciones de SSS padron
	 * 
	 * @param scanner
	 * @throws IOException
	 * @throws ParseException
	 * @throws AfipCantidadRegistrosIncorrectaException
	 * @throws SQLException
	 * @throws PeriodoArchivoDuplicadoException
	 */
	public void procesarArchivoOpcionesSSS(BufferedReader scanner,
			Date fechaArchivo, User user) throws IOException, ParseException,
			SQLException, PeriodoArchivoDuplicadoException {
		OpcionesSS nuevoArchivo = new OpcionesSS();
		List<DetalleOpcionesSS> detalleList = new ArrayList<DetalleOpcionesSS>();
		String line = null;
		while ((line = scanner.readLine()) != null && !line.startsWith("CK")) {
			// detalleList.add(new DetalleOpcionesSS(line));
			detalleList.add(DetalleOpcionesSS.altaVuelta(line));
		}
		nuevoArchivo.setDetalle(detalleList);

		// servicio.grabaArchivoOpciones(nuevoArchivo); //version vieja
		servicio.actualizaAfiOpcionesDesdeArchivoSSS(nuevoArchivo,
				fechaArchivo, user.getScreenName(),
				WebKeysAfiliados.TIPOS_ORIGEN_OPCIONES[0]); // version nueva

	}

	public void procesarArchivoOpcionesMonotrib(BufferedReader scanner,
			Date fechaArchivo, User user) throws IOException, ParseException,
			SQLException, PeriodoArchivoDuplicadoException {
		OpcionesSS nuevoArchivo = new OpcionesSS();
		List<DetalleOpcionesSS> detalleList = new ArrayList<DetalleOpcionesSS>();
		String line = null;
		while ((line = scanner.readLine()) != null) {
			detalleList.add(DetalleOpcionesSS.altaVueltaMono(line));
		}
		nuevoArchivo.setDetalle(detalleList);

		// servicio.grabaArchivoOpciones(nuevoArchivo);
		servicio.actualizaAfiOpcionesDesdeArchivoSSS(nuevoArchivo,
				fechaArchivo, user.getScreenName(),
				WebKeysAfiliados.TIPOS_ORIGEN_OPCIONES[1]);
	}

	public void procesarArchivoSUMARTE(BufferedReader scanner)
			throws IOException, SQLException,
			AfipCantidadRegistrosIncorrectaException, ParseException {

		ArchivoSubsidioMitigacionAsimetricas nuevoArchivo = new ArchivoSubsidioMitigacionAsimetricas();
		HeaderSumaXxxx head = null;
		DetalleSumaXxxx det = null;

		// Encabezado...
		String line = scanner.readLine();
		head = new HeaderSumaXxxx(line, HeaderSumaXxxx.SUMARTE);

		while ((line = scanner.readLine()) != null) {
			if (line.length() > 2) {
				// TODO Houston tenemos un problema---
			}

			det = new DetalleSumaXxxx(line);
		}
		nuevoArchivo.setHeader(head);
		nuevoArchivo.setDetalle(det);
		// nuevoArchivo.setFooterNom(listaFooterFN);

		// BigInteger cantRegs = new BigInteger(String.valueOf(nuevoArchivo
		// .getFooter().getCantidad_registros()));
		// if (!cantRegs.equals(new BigInteger(String.valueOf(nuevoArchivo
		// .getDetalle().size())))) {
		// throw new AfipCantidadRegistrosIncorrectaException();
		// }

		servicio.grabaArchivoSubsidios(nuevoArchivo);
	}

	public void procesarArchivoSUMA70(BufferedReader scanner)
			throws IOException, SQLException,
			AfipCantidadRegistrosIncorrectaException, ParseException {

		ArchivoSubsidioMitigacionAsimetricas nuevoArchivo = new ArchivoSubsidioMitigacionAsimetricas();
		HeaderSumaXxxx head = null;
		DetalleSumaXxxx det = null;

		// Encabezado...
		String line = scanner.readLine();
		head = new HeaderSumaXxxx(line, HeaderSumaXxxx.SUMA70);

		while ((line = scanner.readLine()) != null) {
			if (line.length() > 2) {
				// TODO Houston tenemos un problema---
			}

			det = new DetalleSumaXxxx(line);
		}
		nuevoArchivo.setHeader(head);
		nuevoArchivo.setDetalle(det);
		// nuevoArchivo.setFooterNom(listaFooterFN);

		// BigInteger cantRegs = new BigInteger(String.valueOf(nuevoArchivo
		// .getFooter().getCantidad_registros()));
		// if (!cantRegs.equals(new BigInteger(String.valueOf(nuevoArchivo
		// .getDetalle().size())))) {
		// throw new AfipCantidadRegistrosIncorrectaException();
		// }

		servicio.grabaArchivoSubsidios(nuevoArchivo);
	}

	public void procesarArchivoSUBASI(BufferedReader scanner)
			throws IOException, SQLException,
			AfipCantidadRegistrosIncorrectaException, ParseException {

		ArchivoSubsidioMitigacionAsimetricas nuevoArchivo = new ArchivoSubsidioMitigacionAsimetricas();
		HeaderSumaXxxx head = null;
		DetalleSumaXxxx det = null;

		// Encabezado...
		String line = scanner.readLine();
		head = new HeaderSumaXxxx(line, HeaderSumaXxxx.SUBASI); // SUBASI

		while ((line = scanner.readLine()) != null) {
			if (line.length() > 2) {
				// TODO Houston tenemos un problema---
			}

			det = new DetalleSumaXxxx(line);
		}
		nuevoArchivo.setHeader(head);
		nuevoArchivo.setDetalle(det);
		// nuevoArchivo.setFooterNom(listaFooterFN);

		// BigInteger cantRegs = new BigInteger(String.valueOf(nuevoArchivo
		// .getFooter().getCantidad_registros()));
		// if (!cantRegs.equals(new BigInteger(String.valueOf(nuevoArchivo
		// .getDetalle().size())))) {
		// throw new AfipCantidadRegistrosIncorrectaException();
		// }

		servicio.grabaArchivoSubsidios(nuevoArchivo);
	}

	public void procesarArchivoSUMA(BufferedReader scanner) throws IOException,
			SQLException, AfipCantidadRegistrosIncorrectaException,
			ParseException {

		ArchivoSubsidioMitigacionAsimetricasSuma nuevoArchivo = new ArchivoSubsidioMitigacionAsimetricasSuma();
		HeaderSumaXxxx head = null;
		DetalleSuma det = null;

		// Encabezado...
		String line = scanner.readLine();
		head = new HeaderSumaXxxx(line, HeaderSumaXxxx.SUMA); // SUBASI

		while ((line = scanner.readLine()) != null) {
			if (line.length() > 2) {
				// TODO Houston tenemos un problema---
			}

			det = new DetalleSuma(line);
		}
		nuevoArchivo.setHeader(head);
		nuevoArchivo.setDetalle(det);
		// nuevoArchivo.setFooterNom(listaFooterFN);

		// BigInteger cantRegs = new BigInteger(String.valueOf(nuevoArchivo
		// .getFooter().getCantidad_registros()));
		// if (!cantRegs.equals(new BigInteger(String.valueOf(nuevoArchivo
		// .getDetalle().size())))) {
		// throw new AfipCantidadRegistrosIncorrectaException();
		// }

		servicio.grabaArchivoSubsidiosSuma(nuevoArchivo);
	}

	public void procesarArchivoNovedades(BufferedReader scanner,
			Date fechaArchivo, User user) throws IOException, ParseException,
			SQLException, PeriodoArchivoDuplicadoException {

		List<Novedad> detalleList = new ArrayList<Novedad>();
		Novedad n = null;
		String line = null;

		while ((line = scanner.readLine()) != null) {
			n = new Novedad(line);
			detalleList.add(n);
		}

		servicio.grabaArchivoNovedades(detalleList, fechaArchivo,
				user.getScreenName());

	}
	
	
	public void procesarArchivoNovedadesPadronConsolidado(BufferedReader scanner,
			Date fechaArchivo, User user) throws IOException, ParseException,
			SQLException, PeriodoArchivoDuplicadoException {

		List<NovedadPadronConsolidado> detalleList = new ArrayList<NovedadPadronConsolidado>();
		NovedadPadronConsolidado n = null;
		String line = null;

		while ((line = scanner.readLine()) != null) {
			n = new NovedadPadronConsolidado(line);
			detalleList.add(n);
		}

		servicio.grabaArchivoNovedadesPadronConsolidado(detalleList, fechaArchivo,
				user.getScreenName());

	}
	

	/**
	 * Procesa archivos .txt que son de opciones de SSS padron
	 * 
	 * @param scanner
	 * @throws IOException
	 * @throws ParseException
	 * @throws AfipCantidadRegistrosIncorrectaException
	 * @throws SQLException
	 * @throws PeriodoArchivoDuplicadoException
	 */
	public void procesarArchivoDelegaciones112608SSS(BufferedReader scanner,
			Date fechaArchivo, User user) throws IOException, ParseException,
			SQLException, PeriodoArchivoDuplicadoException {

		Delegacion del = null;
		List<Delegacion> detalleList = new ArrayList<Delegacion>();
		String line = null;
		while ((line = scanner.readLine()) != null) {
			del = Delegacion.parseLine(line);
			if (del != null) {
				detalleList.add(del);
				del = null;
			}
		}
		servicio.actualizarDelegaciones112608(detalleList, fechaArchivo,
				user.getScreenName(), WebKeysAfiliados.TIPOS_ORIGEN_OPCIONES[2]);
	}

	public void procesarArchivoMovBcrio(BufferedReader scanner, User user)
			throws IOException, ParseException, SQLException,
			PeriodoArchivoDuplicadoException {

		List<MovimientoBancario> detalleList = new ArrayList<MovimientoBancario>();
		MovimientoBancario n = null;
		String line = null;
		//LA primera tiene el título
		scanner.readLine();
		while ((line = scanner.readLine()) != null) {
			n = new MovimientoBancario(line);
			detalleList.add(n);
		}
		MovimientoBancarioServiceImpl serv=new MovimientoBancarioServiceImpl();
		serv.grabaArchivoMovBcrios(detalleList, user.getScreenName(), WebKeysGlobal.OSPIM);

	}
	
	public void procesarArchivoAfiliacionPrevencion(BufferedReader scanner,
			Date fechaArchivo, User user) throws IOException, ParseException,
			SQLException, PeriodoArchivoDuplicadoException {

		List<AfiliacionPrevencionDTO> detalleList = new ArrayList<AfiliacionPrevencionDTO>();
		AfiliacionPrevencionDTO dto = null;
		String line = null;
		String cuilTitularAux = null;
		int renglon = 0;
		
		while ((line = scanner.readLine()) != null) {
			if(renglon > 0){ // saltea primer renglon de titulos columnas...
				dto = new AfiliacionPrevencionDTO(line, cuilTitularAux);
			
				cuilTitularAux = dto.getCuilTitular();
			
				detalleList.add(dto);
			}
			_log.debug("renglon " + renglon);
			renglon++;
		}

		servicio.grabaArchivoAfiliacionPrevencion(detalleList, fechaArchivo,
				user.getScreenName());

	}
	public void procesarArchivoMovBcrioConformados(BufferedReader scanner, User user)
			throws IOException, ParseException, SQLException,
			PeriodoArchivoDuplicadoException {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		List<MovimientoBancario> detalleList = new ArrayList<MovimientoBancario>();

		List<CuentaBancaria> ctas=null;
		ctas=TraeListasServiceUtil.getCtasBcrias();
		CuentaBancaria ctaBcria= null;
		String conceptosValidos ="";
		
		String titulo="";
		String fechaConsulta="";
		String horaConsulta="";
		String usuarioConsulta="";
		String parametros="";
		
		Integer cantidadRegistros=0;
				
		MovimientoBancario n = null;
		String line = null;
		Double saldo =0D;
		
		//LA primera tiene el título
		String[] vTitulo=scanner.readLine().split(";");
		titulo=vTitulo[0];
		while ((line = scanner.readLine()) != null) {
			String[] vLine = line.split(";");
			n = new MovimientoBancario();
			
			try{
				cantidadRegistros++;    
	            Date dm = sdf.parse(vLine[0]);
	            n.setFecha_movimiento(dm);
	            
	            String[] vDes = vLine[4].split("   ");
	            String[] vDes1 =vDes[0].split("-SUC");
	            n.setDescripcion(vDes1[0]);
	            if(vLine.length>5){
	                   saldo = Double.parseDouble(vLine[5].replace(".","").replace(",", ".") );
	            }
//	            if(conceptosValidos.length()>0 && conceptosValidos.toUpperCase().indexOf(n.getDescripcion().toUpperCase()) !=-1 ){
		        if(conceptosValidos.length()>0){	
	            	
	               String vConceptos[] = conceptosValidos.split(";");
	               Boolean agregar=false;
	               for(int i=0;i<vConceptos.length;i++){
	            	 if(n.getDescripcion().toUpperCase().startsWith(vConceptos[i].toUpperCase())){
	            		 agregar=true;
	            		 break;
	            	 }
	               }
	               if(agregar){
	                 Date dv = sdf.parse(vLine[1]);
	                 n.setFecha_comprobante(dv);
	            
	                 Double importe = Double.parseDouble(vLine[2].replace(".","").replace(",", ".") );
	                 n.setImporte(BigDecimal.valueOf(importe));
	            
	                 n.setNro_comprobante(vLine[3]);
	                 detalleList.add(n);
	               }
	            }
				
			}catch(Exception e){
				if(ctaBcria==null){
					for(CuentaBancaria c:ctas){
						String nroCta = String.valueOf(c.getNro_cuenta()) + String.valueOf(c.getSucursal());
					   if(vLine.length>0 && vLine[1].indexOf(nroCta) != -1) {
						   ctaBcria=c;
						   conceptosValidos =TraeListasServiceUtil.getSystemConfig("archivos_movimientos_conformados_conceptos_"+ String.valueOf(c.getNro_cuenta()) + String.valueOf(c.getSucursal()));
						   parametros=vLine[1];
						   break;
					   }
					}
				}
				
				if(vLine.length>0){
				  if(vLine[0].toUpperCase().startsWith("FECHA CONSULTA") ) fechaConsulta=vLine[1];
				  if(vLine[0].toUpperCase().startsWith("HORA CONSULTA") ) horaConsulta=vLine[1];
				  if(vLine[0].toUpperCase().startsWith("USUARIO") ) usuarioConsulta=vLine[1];
				}
			}
			
		}
		MovimientoBancarioServiceImpl serv=new MovimientoBancarioServiceImpl();
		
		if(detalleList.size()>0) {
		   serv.grabaArchivoMovBcriosConformados(detalleList,ctaBcria,titulo,fechaConsulta,horaConsulta,usuarioConsulta,
				parametros,cantidadRegistros,saldo, user.getScreenName(), WebKeysGlobal.OSPIM);

		}
	}
	
	public void procesarArchivoLoteEmpresa(BufferedReader scanner, User user)
			throws Exception,LoteEmpresaExistenteException {

		List<Llamado> detalleList = new ArrayList<Llamado>();
		Llamado llamado = null;
		String line = null;
		Calendar fechaCalendar = DateUtils.getCalendarGMTMenos3();
		while ((line = scanner.readLine()) != null && !"".equals(line)) {
			llamado = new Llamado(line);
			llamado.setEstado("ABIERTO");
			llamado.setEstadoGestion(new EstadoGestion(WebKeysEstudioIsidro.ESTADO_INICIAL_LOTE_EMPRESA, null));
			llamado.setFecha(fechaCalendar.getTime());
			llamado.setUser(user.getScreenName());
			llamado.setObservaciones("");
			llamado.setCartaDocumento("");
			detalleList.add(llamado);
		}
		
		//Valida Lote
		for(Llamado l:detalleList){
			if(LlamadoServiceUtil.existeLote(l.getCuit(),l.getLote(), l.getTipoLote())){
				throw new LoteEmpresaExistenteException("Existe el lote "+  l.getTipoLote() + " " + l.getLote() + " para el CUIT " + l.getCuit());
			}
			Llamado llamadoAux = LlamadoServiceUtil.getProponeNroLote(l.getCuit(), null);
			if(llamadoAux!=null && llamadoAux.getLote()!=null && llamadoAux.getLote()!=0 && llamadoAux.getLote()!=l.getLote()){
				throw new LoteEmpresaExistenteException("Existe el lote "+ llamadoAux.getLote() + " ABIERTO para el CUIT " + l.getCuit());	
			}
		}
		
		LlamadoServiceUtil.grabaLlamadoLote(detalleList);
		
	}

	
	public void procesarArchivoDesglose (User usuario,BufferedReader scanner,Date FechaArchivo  ) throws IOException,
		ParseException, ArchivoMedEsIncorrectoException,SQLException, Exception {
		int salidas=0; 
	
		Calendar cal = Calendar.getInstance();
		String text = scanner.readLine(); 
		if (text== null) {
			_log.error("Error en primer linea del archivo");
	
			throw new ParseException("Error en primer linea del archivo", 0);      	    
		}
		//scanner.mark(1);
		//text = scanner.readLine(); 
		try {
			cal.setTime(FechaArchivo);
			int mes = cal.get(Calendar.MONTH)+1;
			int anio = cal.get(Calendar.YEAR );
	
			if (servicio.buscaPeriodoProcesado ("liquidacion_prevencion_archivos", FechaArchivo  )) {
				_log.error("PERIODO YA IMPORTADO");
				salidas=3;  // PERIODO YA IMPORTADO  3  
			}else{		 
				//scanner.reset();
				servicio.procesarArchivoDesglose(text,scanner,usuario.getScreenName() , mes,anio ,FechaArchivo);
				
			}     
	
		}catch(NumberFormatException nfe){
			_log.error(nfe);
		}catch(ArchivoMedEsIncorrectoException e1){
			_log.error(e1);
		    throw new ArchivoMedEsIncorrectoException("1");//PERIODO NO COINCIDENTE CON ARCHIVO
		}catch(Exception e){
			_log.error(e);
			throw new ArchivoMedEsIncorrectoException("4"); // ERRORES CON LOS DATOS DEL ARCHIVO 
		}
	
		switch (salidas) {
			case 3:  
				 throw new ArchivoMedEsIncorrectoException("3");         
			case 1: 
				 throw new ArchivoMedEsIncorrectoException("1");
		}
	}


	public void procesarArchivoLinkPagos(BufferedReader scanner,String entidad) throws IOException,
	         ParseException, AfipCantidadRegistrosIncorrectaException,
	         SQLException, Exception {
		
        String line=null;
        String tipo="";
        String idDeuda="";
        String idConcepto="";
        String idUsuario="";
        String importeStr="";
        String fechaStr="";
        String boletaEmpleadores="";
        Integer nroSecuenciaDDJJ=0;
        Integer tipoBoleta=0;
        Integer nroSecuenciaBoleta=0;
        
        SimpleDateFormat sdf =new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat sdf1 =new SimpleDateFormat("yyMMdd");
        sdf1.setLenient(false);
        ListadoRendicionNacion nuevoArchivo = new ListadoRendicionNacion();
		List<RendicionNacion> detalleList = new ArrayList<RendicionNacion>();
		int codEntidad=0;
		String fechaRendStr="";
		Date fechaRendicion=null;
		
		
		if("AMTIMA".equalsIgnoreCase(entidad)) {
			codEntidad=5652;
			tipoBoleta=CalculaCapitalCuotaServiceUtil.AMTIMA;
			boletaEmpleadores="AMTIMACS";
		}
		while ((line = scanner.readLine()) != null) {
        	
        	tipo = line.substring(0, 1).trim();
        	idDeuda="";
        	idConcepto="";
            idUsuario="";
            importeStr="";
            fechaStr="";
            if("0".equalsIgnoreCase(tipo)) {
              fechaRendStr=	 line.substring(4,12).trim();
              fechaRendicion = sdf.parse(fechaRendStr);
            	
            }else if("1".equalsIgnoreCase(tipo)) {
              idDeuda=  line.substring(1,6).trim();
              idConcepto=  line.substring(6,9).trim();
              idUsuario=line.substring(9,28).trim();
              importeStr=line.substring(29,40).trim();
//              fechaStr=line.substring(40,48).trim();
              fechaStr=line.substring(1,5).trim()+"01";
              if("UOMA".equalsIgnoreCase(entidad)) {
      			if("001".equalsIgnoreCase(idConcepto)) {
      			   tipoBoleta=CalculaCapitalCuotaServiceUtil.SOCIAL;
      			   codEntidad=5783;
      			 boletaEmpleadores="UOMACS";
      			}else if("002".equalsIgnoreCase(idConcepto)) {
       			   tipoBoleta=CalculaCapitalCuotaServiceUtil.SOLIDARIO;
       			   codEntidad=5784;
       			   boletaEmpleadores="UOMAAS";
      			}else if("003".equalsIgnoreCase(idConcepto)) {
       			   tipoBoleta=CalculaCapitalCuotaServiceUtil.USUFRUCTO;
       			   codEntidad=5783;
       			   boletaEmpleadores="UOMACU";
      				
      			}else if("004".equalsIgnoreCase(idConcepto)) {
       			   tipoBoleta=CalculaCapitalCuotaServiceUtil.ART_46;
       			   codEntidad=5785;
       			   boletaEmpleadores="ART46";
      			}   
      	      }
              
              Date fecha=null; 
              FichaBoletaPortal fb=null;
              nroSecuenciaBoleta=0;
              try {
                fecha = sdf1.parse(fechaStr);
                nroSecuenciaDDJJ=Integer.parseInt(line.substring(5,6).trim());
                
                if(fecha.after(sdf.parse("20240101"))) { //Fecha Cambio portal Empleadores a Systemcorp
                  try {
                    nroSecuenciaBoleta=empleadoresService.getBoletaNroSecuencia(idUsuario.trim(), nroSecuenciaDDJJ, fecha, boletaEmpleadores);
                  }catch(Exception e) {
            	    nroSecuenciaBoleta=0; 
                  }
                }
                
                if(nroSecuenciaBoleta==0) {
                     nroSecuenciaBoleta=Integer.parseInt(idDeuda);
               	     fb=empleadoresService.getDatosCobranzaPagosMisCuentas(idUsuario.trim(),Integer.parseInt(idDeuda));	
                }
                
              }catch(Exception e1) {
            	  nroSecuenciaBoleta=Integer.parseInt(idDeuda);
            	  fb=empleadoresService.getDatosCobranzaPagosMisCuentas(idUsuario.trim(),Integer.parseInt(idDeuda));
              }
              
              Double importe = Double.valueOf(importeStr)/100;
              RendicionNacion rendi = new RendicionNacion();
              rendi.setFecha_rendicion(fechaRendicion);
              rendi.setEnte(BigInteger.valueOf(codEntidad));
              rendi.setFecha_recauda(fechaRendicion); 
              rendi.setCod_movimiento(idConcepto);
              BigInteger nmov = new BigInteger(idUsuario+idDeuda+tipoBoleta);
              rendi.setNro_movimiento(nmov.intValue());
              rendi.setCuit(idUsuario);
              rendi.setImporte(BigDecimal.valueOf(importe));
              
              if(fecha!=null && fb==null) {
                rendi.setPeriodo_cod_barras(fecha);
              }else if(fb!=null && fb.getPeriodo_cod_barras()!=null) {
            	  rendi.setPeriodo_cod_barras(fb.getPeriodo_cod_barras());  
              }
              
              rendi.setNro_boleta_portal_emple(nroSecuenciaBoleta);
              rendi.setNro_dec_portal_emple(nroSecuenciaDDJJ);
              
              rendi.setTipo_boleta(tipoBoleta);
              rendi.setEstado_cheque("");
              rendi.setCod_barras("RedLink");
              
              detalleList.add(rendi);
//              _log.debug(idDeuda + " " + idConcepto + " " + idUsuario + " " + importeStr + " " + fechaStr);
        	}
        	
        }
		
		nuevoArchivo.setDetalle(detalleList);
		
		Connection con1 = ConnectionHelper.getReportesOspimConnection();
		servicio.grabaArchivoRendicionNacion(nuevoArchivo, codEntidad, con1, false);
		
		Connection con2 = ConnectionHelper.getConnectionPortalEmpleadoresV01();
		servicio.grabaArchivoRendicionNacion(nuevoArchivo, codEntidad, con2, true);
		
		ConnectionHelper.cerrar(con1);
		ConnectionHelper.cerrar(con2);
   }
	/**
	 * http://www.afip.gob.ar/genericos/rg830/
	 * 
	 * @param scanner
	 * @throws IOException
	 * @throws SQLException
	 * @throws ParseException
	 */
	public void procesarArchivoAFIPrg830(BufferedReader scanner, User user)
			throws IOException, SQLException, ParseException {

		String line;
		
		ArchivoAfipRG830 afipRG830 = null;
		
		List<ArchivoAfipRG830> detalles = new ArrayList<ArchivoAfipRG830>();
		
		while ((line = scanner.readLine()) != null) {
			String[] vLine = line.split(";");
		
			afipRG830 = new ArchivoAfipRG830(vLine);
			
			detalles.add(afipRG830);
		}
		
		servicio.grabaArchivoAFIPrg830(detalles, user.getScreenName());
	}
	
	public void procesarXLSBajasPorOpcion(ActionRequest actionRequest, Date fechaArchivo, File archivo,String fileName, User user, List<String> errores) throws Exception{
				
	    ArrayList<String> listaCuil = new ArrayList<String>();
		FileInputStream file = new FileInputStream(archivo);
		HSSFWorkbook workbook = new HSSFWorkbook(file);
		
		
		HSSFSheet sheet = workbook.getSheetAt(0);
		Iterator<Row> rowIterator = sheet.iterator();
		 
		
		Row row;
		Integer qRow=0;
		while (rowIterator.hasNext()){
			
		    row = rowIterator.next();
		    if(qRow>=0){
		       //Iterator<HSSFCell> cellIterator = row.cellIterator();
		    	Iterator<Cell> cellIterator = row.cellIterator();
		       Cell celda;
		       Integer qCel=0;
		       
		       while (cellIterator.hasNext()){
					celda = cellIterator.next();
					try{
					  Double xval =null;
					  String valor = null;
					  if(qCel==0){//no proceso el header
						//xval= Double.valueOf( celda.getRichStringCellValue().toString());
						//listaCuil.add(String.valueOf(xval));
						xval= celda.getNumericCellValue();
						valor = String.valueOf(xval.longValue());
						if (qRow > 1 && valor.length() != 11) {
							//errores.add(archivo.getName()+" " + "cuil invalido" + " " +  valor);
							throw new Exception("Error: " + "cuil invalido  " +  valor);
						}
						listaCuil.add(valor);
					  }
					}catch(Exception e){
						if (qRow == 0) {
//							_log.debug(e);
						}else {
							
							throw new Exception(e);
						}
					}
					qCel++;
			  }
		   }
		   qRow++; 
		} 
	
	  try {
		new ProcesaArchivosServiceImpl().procesarBajasPorOpcion(listaCuil, fechaArchivo, user.getScreenName());
	} catch (SQLException e) {
		throw new SQLException();
	}
	}
	
	
	public void procesarArchivoPagoMisCuentas(BufferedReader scanner,String entidad) throws IOException,
    ParseException, AfipCantidadRegistrosIncorrectaException, SQLException, Exception {

       String line=null;
       String tipo="";
       String idDeuda="";
       String idConcepto="";
       String idUsuario="";
       String importeStr="";
       String fechaRecaudaStr="";
       String boletaEmpleadores="";
       Integer nroSecuenciaDDJJ=0;
       Integer tipoBoleta=0;
       Integer nroSecuenciaBoleta=0;

       SimpleDateFormat sdf =new SimpleDateFormat("yyyyMMdd");
       SimpleDateFormat sdf1 =new SimpleDateFormat("yyMMdd");
       ListadoRendicionNacion nuevoArchivo = new ListadoRendicionNacion();
       List<RendicionNacion> detalleList = new ArrayList<RendicionNacion>();
       int codEntidad=0;
       String fechaRendStr="";
       Date fechaRendicion=null;
       Date fechaRecaudacion=null;


       if("AMTIMA".equalsIgnoreCase(entidad)) {
	      codEntidad=5652;
	      tipoBoleta=CalculaCapitalCuotaServiceUtil.AMTIMA;
	      boletaEmpleadores="CUOTA_AMTIMA";
       }
       while ((line = scanner.readLine()) != null) {
	
	      tipo = line.substring(0, 1).trim();
	      
	      idDeuda="";
	      idConcepto="";
          idUsuario="";
          importeStr="";
          fechaRecaudaStr="";
          if("0".equalsIgnoreCase(tipo)) {
        	  
        	  
   	
          }else if("5".equalsIgnoreCase(tipo)) {
        	idUsuario=line.substring(1,20).trim();  
        	  
            idDeuda=  line.substring(20,40).trim();
            
            FichaBoletaPortal fb=empleadoresService.getDatosCobranzaPagosMisCuentas(idUsuario.trim(),Integer.parseInt(idDeuda));
            
            
            
            fechaRecaudaStr=line.substring(49,57).trim();
            fechaRecaudacion=sdf.parse(fechaRecaudaStr);
            
            importeStr=line.substring(57,68).trim();
            
            fechaRendStr=	 line.substring(69,77).trim();
            fechaRendicion = sdf.parse(fechaRendStr);
            
            
            idConcepto=  line.substring(68,69).trim();
//          
            
            if("UOMA".equalsIgnoreCase(entidad)) {
               tipoBoleta=fb.getTipoBoleta();	
			   if(tipoBoleta==CalculaCapitalCuotaServiceUtil.SOCIAL) {
			       codEntidad=5783;
			   }else if(tipoBoleta==CalculaCapitalCuotaServiceUtil.SOLIDARIO) {
			       codEntidad=5784;
			   }else if(tipoBoleta==CalculaCapitalCuotaServiceUtil.USUFRUCTO) {
			       codEntidad=5783;
			   }else if( tipoBoleta==CalculaCapitalCuotaServiceUtil.ART_46) {
			       codEntidad=5785;
			   }   
	        }
     
            Double importe = Double.valueOf(importeStr)/100;
            RendicionNacion rendi = new RendicionNacion();
            rendi.setFecha_rendicion(fechaRendicion);
            rendi.setEnte(BigInteger.valueOf(codEntidad));
            rendi.setFecha_recauda(fechaRecaudacion); 
            rendi.setCod_movimiento(idConcepto);
            
            
            Integer nmov = Integer.parseInt( line.substring(79,83).trim()); 
            rendi.setNro_movimiento(nmov.intValue());
            rendi.setCuit(idUsuario);
            rendi.setImporte(BigDecimal.valueOf(importe));
            rendi.setPeriodo_cod_barras(fb.getPeriodo_cod_barras());  
     
            rendi.setNro_boleta_portal_emple(Integer.parseInt(idDeuda));
            rendi.setNro_dec_portal_emple(fb.getNro_secuendia_ddjj_portal_emple());
     
            rendi.setTipo_boleta(tipoBoleta);
            rendi.setEstado_cheque("");
            rendi.setCod_barras("PagoMisCuentas");
     
            detalleList.add(rendi);
//     _log.debug(idDeuda + " " + idConcepto + " " + idUsuario + " " + importeStr + " " + fechaStr);
	  }
	
   }

   nuevoArchivo.setDetalle(detalleList);

   Connection con1 = ConnectionHelper.getReportesOspimConnection();
   servicio.grabaArchivoRendicionNacion(nuevoArchivo, codEntidad, con1, false);


   Connection con2 = ConnectionHelper.getConnectionPortalEmpleadoresV01();
   servicio.grabaArchivoRendicionNacion(nuevoArchivo, codEntidad, con2, true);

   ConnectionHelper.cerrar(con1);
   ConnectionHelper.cerrar(con2);
  }

  
  public void procesarCabeceraFacturaHotel(BufferedReader scanner,Map<String,Factura>facturas) throws IOException,
	SQLException, AfipCantidadRegistrosIncorrectaException,
	ParseException {
	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");  
	String tipo ="";
	String letra="";
	String ptoVta="";
	String nro="";
	String[] razonSocial;
	String clienteDoc="";
	String clienteProvincia="";
	String clienteSituacion="";
	String clienteCuit="";
	String fecha="";
	String cae="";
	String caeVto="";
	
	Map<String,Cliente>clientes=new HashMap<String,Cliente>();
	Factura f=null;
	String line = "";
  
    while ((line = scanner.readLine()) != null) {
    	tipo=line.substring(0,3).trim();
    	if("FC".equals(tipo)) {
    		tipo="FCP";
    	}else if("NC".equals(tipo)) {
    		tipo="NCR";
    	}else if("ND".equals(tipo)) {
    		tipo="NDB";
    	}else if("FCE".equals(tipo)) {
    		tipo="FCE";
    	}else if("NCE".equals(tipo)) {
    		tipo="NCE";
    	}
    	
    	letra=line.substring(3,4).trim();
    	ptoVta=String.format("%05d",  Integer.parseInt(line.substring(4,8).trim()));
    	nro=line.substring(8,16).trim();
    	fecha=line.substring(24,32).trim();
    	razonSocial=line.substring(38,78).trim().split(",");
    	clienteDoc=line.substring(78,80).trim();
    	clienteProvincia=line.substring(80,83).trim();
    	clienteSituacion=line.substring(83,84).trim();
    	clienteCuit=line.substring(84,95).trim();
    	cae=line.substring(402,417).trim();
    	caeVto=line.substring(417,425).trim();
    	
    	f=facturas.get(tipo+letra+ptoVta+nro);
    	if(f==null) {
    		f= new Factura();
    		f.setTipo(tipo);
    		f.setLetra(letra);
    		f.setSucursal(ptoVta);
    		f.setNumero(nro);
    		f.setFecha(sdf.parse(fecha));
    		if(cae!=null && !"".equals(cae) && caeVto!=null && !"".equals(caeVto)) {
    		  f.setCae(cae);
    		  f.setFechaCae(sdf.parse(caeVto));
    		}
    	}
    	
    	if(f.getCliente()==null) {
    		Cliente c =null;
    		c=clientes.get(clienteCuit);
    		if(c==null) {
    		   c=new Cliente();
    		   if( !"1".equals(clienteDoc)
 //   				   "3".equals(clienteDoc) || "4".equals(clienteDoc) || "5".equals(clienteDoc) || "6".equals(clienteDoc)
    				   ) {
    		      if(razonSocial.length==2) {
    				   c.setApellido(razonSocial[0]);
    				   c.setNombre(razonSocial[1]);
   			      }else if(razonSocial.length==1) {
   				       c.setApellido(razonSocial[0]);	
   			      }		
    		      String td="";
    		      if("3".equals(clienteDoc)) {
    			    td="LE";  
    		      }else if("4".equals(clienteDoc)) {
    			    td="LC";  
    		      }else if("5".equals(clienteDoc)) {
    			    td="DU";  
    		      }else {
    		    	td="DU";  
    		      }
    		      
    		      List<Afiliado> la = EditarAfiliadoServiceUtil.getAfiliadosPorDocumentoInclusoDadoDeBaja(clienteCuit, td);
    		      if(la!=null && !la.isEmpty()){
    			      c.setTipo(c.getTipo().AFILIADO);
    			      c.setCuilTitular(la.get(0).getCuil_titular());
    			      c.setInte(0);
    		      }else {
    			      c.setTipo(c.getTipo().VISITA);
    		      }
    		      c.setDocumentoTipo(td);
    		      c.setDocumentoNro(clienteCuit);
    	       }else if("1".equals(clienteDoc)) {
 			      c.setTipo(c.getTipo().EMPRESA);
 			      c.setCuit(clienteCuit);
 			      c.setSucursal("000");
 			      if(razonSocial.length==2) {
 				     c.setRazonSocial(razonSocial[0] + " " + razonSocial[1]); 
 			      }else if(razonSocial.length==1) {
  				     c.setRazonSocial(razonSocial[0] ); 
  			      }
 		       }
    		   if ("1".equals(clienteSituacion)) { 
    			   c.setCategoriaIVA("RI");
    		   }else if ("2".equals(clienteSituacion)) { 
    			   c.setCategoriaIVA("NI");
    		   }if ("3".equals(clienteSituacion)) { 
    			   c.setCategoriaIVA("CS");
    		   }if ("5".equals(clienteSituacion)) { 
    			   c.setCategoriaIVA("EX");
    		   }
    		   clientes.put(clienteCuit, c);
    		}
    		f.setCliente(c);
    	}
    	
    	facturas.put(tipo+letra+ptoVta+nro, f);
  
    }
  }
  
  public void procesarItemsFacturaHotel(BufferedReader scanner,Map<String,Factura>facturas) throws IOException,
	SQLException, AfipCantidadRegistrosIncorrectaException,
	ParseException {
	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");  
	String[] equivalencias=TraeListasServiceUtil.getSystemConfig("HOTELES_EQUIVALENCIAS_PRODUCTOS").split("-");
	
	String tipo ="";
	String letra="";
	String ptoVta="";
	String nro="";
	String fecha="";
	String codigo="";
	String tasaIva="";
	String iva="";
	String neto="";
	
	
	Factura f=null;
	String line;

   while ((line = scanner.readLine()) != null) {
	tasaIva="";
	iva="";
	neto="";
  	tipo=line.substring(0,3).trim();
  	if("FC".equals(tipo)) {
  		tipo="FCP";
  	}else if("NC".equals(tipo)) {
  		tipo="NCR";
  	}else if("ND".equals(tipo)) {
  		tipo="NDB";
  	}
  	
  	letra=line.substring(3,4).trim();
  	ptoVta=String.format("%05d",  Integer.parseInt(line.substring(4,8).trim()));
  	nro=line.substring(8,16).trim();
  	fecha=line.substring(24,32).trim();
  	codigo=line.substring(39,63).trim();
  	tasaIva=line.substring(160,169).trim();
  	iva=line.substring(176,193).trim();
  	neto=line.substring(208,225).trim();
  	f=facturas.get(tipo+letra+ptoVta+nro);
  	if(f==null) {
  		f= new Factura();
  		f.setTipo(tipo);
  		f.setLetra(letra);
  		f.setSucursal(ptoVta);
  		f.setNumero(nro);
  		f.setFecha(sdf.parse(fecha));
  		f.setIvaReintegro(BigDecimal.ZERO);
  	}
  	
  	if(f.getDetalles()==null) {
  	   f.setDetalle(new ArrayList<FacturaDetalle>());
  	}
  	
  	Integer prd=null;
  	for(String s:Arrays.asList(equivalencias)) {
  		String[] aux = s.split("=");
  		String[] productos=aux[1].split(";");
  		for(String p : productos) {
  		  if(p.equals(codigo)) {
  			prd=Integer.parseInt( aux[0]);
  			break;
  		  }
  		}
  		if(prd!=null) break;
  	}
  	
  	Producto producto=new Producto();
	FacturaDetalle detalle=new FacturaDetalle(); 
	   
  	
  	if(prd!=null) {
  	   producto.setId(prd);
  	}else {
  	   producto.setId(3);
   	}
    detalle.setDetalle(producto);
	detalle.setPrecio(new BigDecimal(iva).add(new BigDecimal(neto)));
	f.getDetalles().add(detalle) ;
	
  	
  	if(tasaIva !=null && Double.parseDouble(tasaIva)!=0D) {
	   f.setIva(f.getIva().add(new BigDecimal(iva)));
	   if("T".equalsIgnoreCase(f.getLetra()) && producto.getId()==1 ) {
		   f.setIvaReintegro( f.getIvaReintegro().add(new BigDecimal(iva)) );
	   }
	   f.setTotalNeto(f.getTotalNeto().add(new BigDecimal(neto)  ));
	   f.setTotal(f.getImporteTotal().add(new BigDecimal(neto)).add(new BigDecimal(iva)));
	   if("T".equalsIgnoreCase(f.getLetra()) && producto.getId()==1  ) {
		   f.setTotal(f.getImporteTotal().subtract(new BigDecimal(iva)));
	   }
	}else {
	   f.setTotalExento(f.getTotalExento().add(new BigDecimal(neto))); 
	   f.setTotal(f.getImporteTotal().add(new BigDecimal(neto)));
	}
  	
  	facturas.put(tipo+letra+ptoVta+nro, f);
  }
}	

  //VER CUENTA BANCARIA EN DONDE IMPUTAR las transferencias
  public void procesarPagosFacturaHotel(BufferedReader scanner,Map<String,Factura>facturas) throws IOException,
	SQLException, AfipCantidadRegistrosIncorrectaException,
	ParseException {
	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");  
	
	String tipo ="";
	String letra="";
	String ptoVta="";
	String nro="";
	String fecha="";
	String fechaVto="";
	
	String codigo="";
	String importe="";
	String nroMovimiento="";
	

	Factura f=null;
	String line = "";

   while ((line = scanner.readLine()) != null) {
	importe="";
	tipo=line.substring(0,3).trim();
	if("FC".equals(tipo)) {
		tipo="FCP";
	}else if("NC".equals(tipo)) {
		tipo="NCR";
	}else if("ND".equals(tipo)) {
		tipo="NDB";
	}
	
	letra=line.substring(3,4).trim();
	ptoVta=String.format("%05d",  Integer.parseInt(line.substring(4,8).trim()));
	nro=line.substring(8,16).trim();
	fecha=line.substring(24,32).trim();
		
	codigo=line.substring(60,63).trim();
	fechaVto=line.substring(63,72).trim();
	importe=line.substring(71,87).trim();
	nroMovimiento=line.substring(121,146).trim();
	
	f=facturas.get(tipo+letra+ptoVta+nro);
	if(f==null) {
		f= new Factura();
		f.setTipo(tipo);
		f.setLetra(letra);
		f.setSucursal(ptoVta);
		f.setNumero(nro);
		f.setFecha(sdf.parse(fecha));
	}
	
	if(f.getIngresos()==null) {
	   f.setIngresos(new ArrayList<FacturaIngreso>());
	}
	
	FacturaIngreso fi= new FacturaIngreso();
	if("MA".equals(codigo) || "VD".equals(codigo) || "PIN".equals(codigo) || "MD".equals(codigo)){
		TarjetaDebitoCredito t = new TarjetaDebitoCredito();
		t.setTipo(TarjetaDebitoCredito.ID_TIPO_DEBITO);
		t.setImporte(new BigDecimal(importe));
		t.setFecha(sdf.parse(fechaVto));
		t.setNumero(nroMovimiento);
		if("MA".equals(codigo) ) {
		  t.setEmisor(WebKeysUOMA.TARJETA_MAESTRO);	
		}else if("VD".equals(codigo)) {
		  t.setEmisor(WebKeysUOMA.TARJETA_VISA);
		}else if("MD".equals(codigo)) {
			  t.setEmisor(WebKeysUOMA.TARJETA_MASTERCARD);
		}else {
		  t.setEmisor(WebKeysUOMA.TARJETA_OTRAS);	
		}
		Banco b = new Banco();
		b.setId_banco(WebKeysUOMA.TARJETA_BANCO_DEFECTO);
		t.setBanco(b);
		fi.setIngreso(t);
	}else if("EFE".equals(codigo) ){
		Efectivo e = new Efectivo();
		e.setImporte(new BigDecimal(importe));
		e.setFecha(sdf.parse(fechaVto));
		fi.setIngreso(e);
	}else if("MC".equals(codigo) ||"CAB".equals(codigo) || "VC".equals(codigo) || "AX".equals(codigo) ||"NAR".equals(codigo)){
		TarjetaDebitoCredito t = new TarjetaDebitoCredito();
		t.setTipo(TarjetaDebitoCredito.ID_TIPO_CREDITO);
		t.setImporte(new BigDecimal(importe));
		t.setFecha(sdf.parse(fechaVto));
		if("MC".equals(codigo) ) {
			t.setEmisor(WebKeysUOMA.TARJETA_MASTERCARD);	
		}else if("VC".equals(codigo)) {
			t.setEmisor(WebKeysUOMA.TARJETA_VISA);
		}else if("CAB".equals(codigo)) {
			t.setEmisor(WebKeysUOMA.TARJETA_CABAL);
		}else if("AX".equals(codigo)) {
			t.setEmisor(WebKeysUOMA.TARJETA_AMEX);
		}else if("NAR".equals(codigo)) {
			t.setEmisor(WebKeysUOMA.TARJETA_OTRAS);
		}
		Banco b = new Banco();
		b.setId_banco(WebKeysUOMA.TARJETA_BANCO_DEFECTO);
		t.setBanco(b);
		fi.setIngreso(t);
	}else if("BFR".equals(codigo)) {
//Falta agregar cta bcria
		String ctaStr=TraeListasServiceUtil.getSystemConfig("HOTELES_CTA_BCRIA_TRANSFERENCIA_"+codigo);
		CuentaBancaria cb= new CuentaBancaria();
		cb.setId_cuenta_bcria(Integer.parseInt(ctaStr));
		DepositoBancario d = new DepositoBancario();
		d.setTipoDeposito(DepositoBancario.ID_TIPO_TRANSFERENCIA);
		d.setImporte(new BigDecimal(importe));
		d.setFecha(sdf.parse(fechaVto));
		d.setCuentaBancaria(cb);
		fi.setIngreso(d);
	}else if("BANK".equals(codigo)|| "BAN".equals(codigo) ) {
		//Falta agregar cta bcria
		        if("BAN".equals(codigo) && !"00010".equalsIgnoreCase(f.getSucursal())) {
		        	codigo="BANK";
		        }if("BAN".equals(codigo) && "00010".equalsIgnoreCase(f.getSucursal())) {
		        	codigo="NECO";
		        }
				String ctaStr=TraeListasServiceUtil.getSystemConfig("HOTELES_CTA_BCRIA_TRANSFERENCIA_"+codigo);
				CuentaBancaria cb= new CuentaBancaria();
				cb.setId_cuenta_bcria(Integer.parseInt(ctaStr));
				DepositoBancario d = new DepositoBancario();
				d.setTipoDeposito(DepositoBancario.ID_TIPO_TRANSFERENCIA);
				d.setImporte(new BigDecimal(importe));
				d.setFecha(sdf.parse(fechaVto));
				d.setCuentaBancaria(cb);
				fi.setIngreso(d);
	}else if("FTA".equals(codigo)) {
//Como imputar
		FinanciacionTurismo d = new FinanciacionTurismo();
		d.setImporte(new BigDecimal(importe));
		d.setFecha(sdf.parse(fechaVto));
		fi.setIngreso(d);
	}else if("CxC".equals(codigo)  || "DSP".equals(codigo)) {
		CuentaCorriente d = new CuentaCorriente();
		d.setImporte(new BigDecimal(importe));
		d.setFecha(sdf.parse(fechaVto));
		fi.setIngreso(d);
		
	}else if("MP".equals(codigo) && "00030".equalsIgnoreCase(f.getSucursal())) {
		//Falta agregar cta bcria
				String ctaStr=TraeListasServiceUtil.getSystemConfig("HOTELES_CTA_BCRIA_TRANSFERENCIA_BFR");
				CuentaBancaria cb= new CuentaBancaria();
				cb.setId_cuenta_bcria(Integer.parseInt(ctaStr));
				DepositoBancario d = new DepositoBancario();
				d.setTipoDeposito(DepositoBancario.ID_TIPO_TRANSFERENCIA);
				d.setImporte(new BigDecimal(importe));
				d.setFecha(sdf.parse(fechaVto));
				d.setCuentaBancaria(cb);
				fi.setIngreso(d);
    }else if("MP".equals(codigo) && "00020".equalsIgnoreCase(f.getSucursal())) {
		//Falta agregar cta bcria
		String ctaStr=TraeListasServiceUtil.getSystemConfig("HOTELES_CTA_BCRIA_TRANSFERENCIA_BANK");
		CuentaBancaria cb= new CuentaBancaria();
		cb.setId_cuenta_bcria(Integer.parseInt(ctaStr));
		DepositoBancario d = new DepositoBancario();
		d.setTipoDeposito(DepositoBancario.ID_TIPO_TRANSFERENCIA);
		d.setImporte(new BigDecimal(importe));
		d.setFecha(sdf.parse(fechaVto));
		d.setCuentaBancaria(cb);
		fi.setIngreso(d);
   }else if("MP".equals(codigo) && "00010".equalsIgnoreCase(f.getSucursal())) {
		//Falta agregar cta bcria
		String ctaStr=TraeListasServiceUtil.getSystemConfig("HOTELES_CTA_BCRIA_TRANSFERENCIA_NECO");
		CuentaBancaria cb= new CuentaBancaria();
		cb.setId_cuenta_bcria(Integer.parseInt(ctaStr));
		DepositoBancario d = new DepositoBancario();
		d.setTipoDeposito(DepositoBancario.ID_TIPO_TRANSFERENCIA);
		d.setImporte(new BigDecimal(importe));
		d.setFecha(sdf.parse(fechaVto));
		d.setCuentaBancaria(cb);
		fi.setIngreso(d);
   }else if("RET".equals(codigo) ) {		
		Retencion ret = new Retencion();	
	      ret.setTipo(Retencion.GRAL);
	      ret.setImporte(new BigDecimal(importe));
	      ret.setFecha(sdf.parse(fechaVto));
	      fi.setIngreso(ret);
		
  }else if("CHQ".equals(codigo)) {
	    CuentaBancaria cb = null;
		String nroCheque=line.substring(87,96).trim();
		String idBanco=TraeListasServiceUtil.getSystemConfig("HOTELES_BCO_CHEQUE_CTA_CTE");
		String idCtaBcria=TraeListasServiceUtil.getSystemConfig("HOTELES_BCO_CTA_BCRIA_CTA_CTE");
		String idCuit=TraeListasServiceUtil.getSystemConfig("HOTELES_CUIT_CHEQUES_CTA_CTE");

		Cheque cheque = new Cheque(new BigDecimal(nro+nroCheque), Integer.parseInt(idBanco));
		cheque.setImporte(new BigDecimal(importe));
		Estado estado= new Estado(Cheque.Estado.RECIBIDO);
		cheque.setEstado(estado);
		cheque.setFecha(sdf.parse(fechaVto));
		cheque.setDebitoCredito(Cheque.Tipo.CREDITO);
		cheque.setCuit(idCuit);

		Banco b = new Banco(Integer.parseInt(idBanco));
		cb = new CuentaBancaria(Integer.parseInt(idCtaBcria));
		cb.setBanco(b);
		cheque.setCuentaBancaria(cb);
		fi.setIngreso(cheque);
  }else {
	  Efectivo e = new Efectivo();
	  e.setImporte(new BigDecimal(importe));
	  e.setFecha(sdf.parse(fechaVto));
	  fi.setIngreso(e);
  }
		
	
  f.getIngresos().add(fi);
	
  facturas.put(tipo+letra+ptoVta+nro, f);
 }
 }
  
 public void procesarArchivoARBARetenciones(BufferedReader scanner,String user) throws IOException,
  ParseException, AfipCantidadRegistrosIncorrectaException,
  SQLException, Exception {
  try {
   List<ArchivoARBAPadronAlicuota> lista = new ArrayList<ArchivoARBAPadronAlicuota>();
   SimpleDateFormat sdf =new SimpleDateFormat("ddMMyyyy");
   String line = "";
   Double l=0D;
   servicio.deleteArchivoPadronAlicuotasARBA( null);
   
   while ((line = scanner.readLine()) != null) {
	  String[] vLine = line.split(";");
	  ArchivoARBAPadronAlicuota detalle =new ArchivoARBAPadronAlicuota(vLine);
	  if(!"B".equals(detalle.getOperacion())){
		lista.add(detalle);
	  }
	  if((l++ % 1000D)==0D) _log.debug("Linea "+ l.toString());
	  if(lista.size()>1000) {
		 servicio.grabaArchivoPadronAlicuotasARBA(lista, null);
		 lista.clear(); 
	  }
   }

   _log.debug("Linea Final "+ l.toString()); 
    servicio.grabaArchivoPadronAlicuotasARBA(lista, null);
  
  }catch(Exception e) {
	   _log.debug("Error "+ e.getMessage()); 
  }
  
  
 }
 
 public void procesarPercepcionesFacturaHotel(BufferedReader scanner,Map<String,Factura>facturas) throws IOException,
	SQLException, AfipCantidadRegistrosIncorrectaException,
	ParseException {
	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");  
	
	String tipo ="";
	String letra="";
	String ptoVta="";
	String nro="";
	String fecha="";
	String fechaVto="";
	
	String codigo="";
	String importe="";
	String nroMovimiento="";
	
	Factura f=null;
	String line = "";

   while ((line = scanner.readLine()) != null) {
	importe="";
	tipo=line.substring(0,3).trim();
	if("FC".equals(tipo)) {
		tipo="FCP";
	}else if("NC".equals(tipo)) {
		tipo="NCR";
	}else if("ND".equals(tipo)) {
		tipo="NDB";
	}
	
	letra=line.substring(3,4).trim();
	ptoVta=String.format("%05d",  Integer.parseInt(line.substring(4,8).trim()));
	nro=line.substring(8,16).trim();
	fecha=line.substring(24,32).trim();
		
	codigo=line.substring(37,42).trim();
	importe=line.substring(45,62).trim();
	
	BigDecimal imp = (new BigDecimal(importe)).abs();
	
	f=facturas.get(tipo+letra+ptoVta+nro);
	if(f==null) {
		f= new Factura();
		f.setTipo(tipo);
		f.setLetra(letra);
		f.setSucursal(ptoVta);
		f.setNumero(nro);
		f.setFecha(sdf.parse(fecha));
	}
	
	
	if(f.getDetalles()==null) {
	    f.setDetalle(new ArrayList<FacturaDetalle>());
	}
	
	Producto producto=new Producto();
	FacturaDetalle detalle=new FacturaDetalle(); 
	producto.setId(7); //Percepcion IIBB ARBA
	detalle.setDetalle(producto);
	detalle.setPrecio(imp);
	f.getDetalles().add(detalle) ;
	f.setTotal(f.getImporteTotal().abs().add(imp ));
	f.setPercepcion(f.getPercepcion().add(imp));
	facturas.put(tipo+letra+ptoVta+nro, f);
	
   }
}
 
	
 public void procesarArchivoJubiladosSitaciPIMME(BufferedReader scanner,User user,String periodoLiquidacionStr) throws IOException,
 ParseException, AfipCantidadRegistrosIncorrectaException,
 SQLException, Exception {
	 
  String beneficio="";
  String afiliado="";
  String tipo1="";
  String tipo2="";
  String dni="";
  String concepto="";
  Double sumatoria=0D;
  Double conceptoImporte=0D;
  String periodo="";
  String cuil="";
  Date nacimiento=null;
  String sexo="";
  String filler01="";
  String registro="";
	 
  String line=null;
  Integer periodoLiquidacion =Integer.valueOf(periodoLiquidacionStr);

  SimpleDateFormat sdf =new SimpleDateFormat("dd.MM.yyyy");
  
  List<JubiladosSitaci>list=new ArrayList<JubiladosSitaci>();

  while ((line = scanner.readLine()) != null) {

   beneficio = line.substring(0, 11).trim();
   afiliado = line.substring(11, 33).trim();
   tipo1= line.substring(33, 35).trim();
   tipo2=line.substring(35, 36).trim();
   dni=line.substring(36, 44).trim();
   concepto=line.substring(44,50);
   sumatoria=Double.valueOf(line.substring(50,61))/100;
   conceptoImporte=Double.valueOf(line.substring(61,72))/100;
   periodo=line.substring(72, 76);
   cuil=line.substring(76, 87);
   nacimiento=sdf.parse(line.substring(87, 97));
   sexo=line.substring(97, 98);
   filler01=line.substring(98, 100);
   registro=line.substring(100, 103);
   
   JubiladosSitaci j =new JubiladosSitaci(beneficio , afiliado , tipo1 ,tipo2 ,dni ,concepto,sumatoria,
           conceptoImporte,periodo,cuil,nacimiento,sexo,filler01,registro,periodoLiquidacion) ;
   list.add(j);

   _log.debug(beneficio + " " + afiliado +" "+ tipo1 +" " +tipo2+" "+dni +" "+concepto+" "+sumatoria.toString()+" "+
              conceptoImporte.toString()+ " "+periodo+" "+cuil+" "+nacimiento.toString()+ " "+ sexo +" "+filler01+" "+registro);
 }

Connection con1 = ConnectionHelper.getReportesOspimConnection();
servicio.grabaJubiladosSitaci(list, user.getScreenName());


ConnectionHelper.cerrar(con1);

 }

 
 public void procesarArchivoJubiladosSitaciPIMIM(BufferedReader scanner,User user,String periodoLiquidacionStr) throws IOException,
 ParseException, AfipCantidadRegistrosIncorrectaException,
 SQLException, Exception {
	 
  String beneficio="";
  String afiliado="";
  String tipo1="";
  String tipo2="";
  String dni="";
  String concepto="";
  Double sumatoria=0D;
  Double conceptoImporte=0D;
  String periodo="";
  String cuil="";
  Date nacimiento=null;
  String sexo="";
  String filler01="";
  String registro="";
	 
  String line=null;

  Integer periodoLiquidacion =Integer.valueOf(periodoLiquidacionStr);
  
  SimpleDateFormat sdf =new SimpleDateFormat("dd.MM.yyyy");
  
  List<JubiladosSitaci>list=new ArrayList<JubiladosSitaci>();

  while ((line = scanner.readLine()) != null) {

   beneficio = line.substring(0, 11).trim();
   afiliado = line.substring(11, 33).trim();
   tipo1= line.substring(33, 35).trim();
   tipo2=line.substring(35, 36).trim();
   dni=line.substring(36, 44).trim();
   concepto=line.substring(44,50);
   sumatoria=(Double.valueOf(line.substring(50,61)))*-1/100;
   conceptoImporte=(Double.valueOf(line.substring(61,72)))*-1/100;
   periodo=line.substring(72, 76);
   cuil=line.substring(76, 87);
   nacimiento=sdf.parse(line.substring(87, 97));
   sexo=line.substring(97, 98);
   filler01=line.substring(98, 100);
   registro=line.substring(100, 103);
   
   JubiladosSitaci j =new JubiladosSitaci(beneficio , afiliado , tipo1 ,tipo2 ,dni ,concepto,sumatoria,
           conceptoImporte,periodo,cuil,nacimiento,sexo,filler01,registro,periodoLiquidacion	) ;
   list.add(j);

   _log.debug(beneficio + " " + afiliado +" "+ tipo1 +" " +tipo2+" "+dni +" "+concepto+" "+sumatoria.toString()+" "+
              conceptoImporte.toString()+ " "+periodo+" "+cuil+" "+nacimiento.toString()+ " "+ sexo +" "+filler01+" "+registro);
 }

Connection con1 = ConnectionHelper.getReportesOspimConnection();
servicio.grabaJubiladosSitaci(list, user.getScreenName());


ConnectionHelper.cerrar(con1);

 }

 
 
 public void procesarArchivoBASEAFIP(BufferedReader scanner) throws IOException,
	ParseException, AfipCantidadRegistrosIncorrectaException,
	SQLException, Exception {
    int registros_a_insertar=1000; //250
    StringBuffer sb = new StringBuffer();
	List<String> lInserts = new ArrayList<String>();
	
    String line = "";
    Long totalReg=0L;
    _log.debug("Comienza Lectura registros BASES AFIP ");
    
    String sql ="INSERT INTO base_afip(cuit,denominacion,ganancia,iva, monotrib,soc,empleador,actividad) VALUES ";
    
    sb.append(sql);
    Long row=0L;
    
    while ((line = scanner.readLine()) != null) {
	  if (line != null && !line.trim().equals("")) {
		  
		  DetalleAfipContribuyentes d = new DetalleAfipContribuyentes(line);
		  sb.append("('");
		  sb.append(d.getCuit());
		  sb.append("', '");
		  sb.append(d.getRazonSocial().replace("'", ""));
		  sb.append("', '");
		  sb.append(d.getGanancias());
		  sb.append("', '");
		  sb.append(d.getIva());
		  sb.append("', '");
		  sb.append(d.getMonotributo());
		  sb.append("', '");
		  sb.append(d.getIntegranteSoc());
		  sb.append("', '");
		  sb.append(d.getEmpleador());
		  sb.append("', '");
		  sb.append(d.getActividadMonotributo());
		  sb.append("'),");
		 
		  totalReg++;
		  row++;
		  if(row%100000==0){
		     _log.debug("    registro : " + row);
		  }   

		  if(totalReg == registros_a_insertar) {
			  totalReg=0L;
		      sb.deleteCharAt(sb.length()-1); //Quitamos la ultima ","
		      sb.append(";");
		      lInserts.add(sb.toString());
		      sb.setLength(0);
		      sb.append(sql);
	      }
	  }
	  
    }
    
    
    if(totalReg >0) {
		  sb.deleteCharAt(sb.length()-1); //Quitamos la ultima ","
		  sb.append(";");
	      lInserts.add(sb.toString());
    }
    
    //Hecho de esta manera para subsanar errores de memoria que impedian terminar la grabación
     _log.debug("Base AFIP Registros levantados txt: " + row);
     row=Long.valueOf(lInserts.size());
     servicio.truncateArchivoAfip();
     for(String s:lInserts){
    	 _log.debug("Base AFIP Lote a grabar: " + row--); 
    	List<String> l = new ArrayList<String>();
    	l.add(s);
        servicio.grabaArchivoPadronAFIP(l,null);
     }   

//     

}

 
 public  List<String> procesarXLSRecibosHoteles(ActionRequest actionRequest,  File archivo,String fileName) throws Exception{
	    User user = PortalUtil.getUser(actionRequest);
	    List<String>errores = new ArrayList<String>();
	    Map<String,Recibo> mRecibos = new HashMap<String,Recibo>();
	    Map<String,Double> mRecibosNeto = new HashMap<String,Double>();
	    
	    SimpleDateFormat sdf = new SimpleDateFormat();
		
		FileInputStream file = new FileInputStream(archivo);
		HSSFWorkbook workbook = new HSSFWorkbook(file);
		
		HSSFSheet sheet = workbook.getSheetAt(0);
		Iterator<Row> rowIterator = sheet.iterator();
		
		Row row;
		Integer qRow=0;
		Recibo recibo;	
	    Cliente cliente = new Cliente();
	    String hotel;
		Double importe=null;
		Date fecha=null;
		Double nroRecibo=null;
		String fPago=null;
		String facturas=null;
		String razon=null;
		String sucursal=null;
		String claveRecibo="";
		String movimientoBco="";
	    Double importeNeto=0D;
	    Integer qCheque=0;
	    
		while (rowIterator.hasNext()){
		    row = rowIterator.next();
		    if(qRow>=2){
		      
		       Iterator<Cell> cellIterator = row.cellIterator();
		       Cell celda;
		       Integer qCel=0;
		       int lastColumn = row.getLastCellNum();
		       cliente = new Cliente();
		       
		       for ( qCel = 0; qCel < lastColumn; qCel++) {
		       //while (cellIterator.hasNext()){
		    	   
				
				try{
				  celda = row.getCell(qCel, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
				  //celda = cellIterator.next();
				  if(qCel==0){//Hotel
					hotel=celda.getStringCellValue();
					sucursal= TraeListasServiceUtil.getSystemConfig("HOTEL_SUCURSAL_"+hotel);
					
				  }else if(qCel==1){//Fecha
					try {  
					   fecha=celda.getDateCellValue();	
					}catch(Exception e) {
					//   archivo.setNroRecibo(celda.toString());	
					}
				  }else if(qCel==2) { //Razón Social
					 razon =celda.getStringCellValue();
					 cliente.setRazonSocial(razon);
					
				  }else if(qCel==3) { //Importe
				      importe=celda.getNumericCellValue();
				  }else if(qCel==4) { //Forma de pago
					  fPago=celda.getStringCellValue();
				  }else if(qCel==6) { //Nro Recibo
					  nroRecibo=celda.getNumericCellValue();
				  }else if(qCel==8) { //Comentario 
					  movimientoBco=celda.getStringCellValue();
				  }else if(qCel==9) {// Nros facturas que aplica
					  facturas =celda.getStringCellValue();
				  }
				}catch(Exception e){
					_log.debug(e);
				}
				//qCel++;
			  }
		      claveRecibo=sucursal + String.format("%08d",nroRecibo.intValue()); 
		      recibo = mRecibos.get(claveRecibo);
		      importeNeto =mRecibosNeto.get(claveRecibo);
		      if(importeNeto==null) {
		    	  importeNeto=importe;
		      }else {
		    	  importeNeto+=importe;
		      }
		      mRecibosNeto.put(claveRecibo,importeNeto);
		      
		      if (recibo==null){
		    	 recibo = new Recibo();
		    	 recibo.setSucursal(sucursal);
		    	 recibo.setFecha(fecha);
		    	 recibo.setCliente(cliente);
		    	 recibo.setNumero(nroRecibo.longValue());
		    	 recibo.setDescripcion(facturas);
		      }
		      if(importe.doubleValue()>0D) {
		    	  recibo.setTotal(importe.doubleValue());
		      }
		      
		      /////// FORMA DE PAGO
		    FacturaIngreso fi= new FacturaIngreso();
		    if(importe.doubleValue()>0D) {
		  	  if("MA".equals(fPago) || "VD".equals(fPago) || "PIN".equals(fPago) || "MD".equals(fPago)){
		  		TarjetaDebitoCredito t = new TarjetaDebitoCredito();
		  		t.setTipo(TarjetaDebitoCredito.ID_TIPO_DEBITO);
		  		t.setImporte(new BigDecimal(importe));
		  		t.setFecha(fecha);
		  		t.setNumero(movimientoBco);
		  		if("MA".equals(fPago) ) {
		  		  t.setEmisor(WebKeysUOMA.TARJETA_MAESTRO);	
		  		}else if("VD".equals(fPago)) {
		  		  t.setEmisor(WebKeysUOMA.TARJETA_VISA);
		  		}else if("MD".equals(fPago)) {
			  		  t.setEmisor(WebKeysUOMA.TARJETA_MASTERCARD);
			  	}else {
		  		  t.setEmisor(WebKeysUOMA.TARJETA_OTRAS);	
		  		}
		  		Banco b = new Banco();
		  		b.setId_banco(WebKeysUOMA.TARJETA_BANCO_DEFECTO);
		  		t.setBanco(b);
		  		fi.setIngreso(t);
		  	  }else if("EFE".equals(fPago) ){
		  		Efectivo e = new Efectivo();
		  		e.setImporte(new BigDecimal(importe));
		  		e.setFecha(fecha);
		  		fi.setIngreso(e);
		  	  }else if("MC".equals(fPago) ||"CAB".equals(fPago) || "VC".equals(fPago) || "AX".equals(fPago) ||"NAR".equals(fPago)){
		  		TarjetaDebitoCredito t = new TarjetaDebitoCredito();
		  		t.setTipo(TarjetaDebitoCredito.ID_TIPO_CREDITO);
		  		t.setImporte(new BigDecimal(importe));
		  		t.setFecha(fecha);
		  		if("MC".equals(fPago) ) {
		  			t.setEmisor(WebKeysUOMA.TARJETA_MASTERCARD);	
		  		}else if("VC".equals(fPago)) {
		  			t.setEmisor(WebKeysUOMA.TARJETA_VISA);
		  		}else if("CAB".equals(fPago)) {
		  			t.setEmisor(WebKeysUOMA.TARJETA_CABAL);
		  		}else if("AX".equals(fPago)) {
		  			t.setEmisor(WebKeysUOMA.TARJETA_AMEX);
		  		}else if("NAR".equals(fPago)) {
		  			t.setEmisor(WebKeysUOMA.TARJETA_OTRAS);
		  		}
		  		Banco b = new Banco();
		  		b.setId_banco(WebKeysUOMA.TARJETA_BANCO_DEFECTO);
		  		t.setBanco(b);
		  		fi.setIngreso(t);
		  	  }else if("BFR".equals(fPago)) {
		  //Falta agregar cta bcria
		  		String ctaStr=TraeListasServiceUtil.getSystemConfig("HOTELES_CTA_BCRIA_TRANSFERENCIA_"+fPago);
		  		CuentaBancaria cb= new CuentaBancaria();
		  		cb.setId_cuenta_bcria(Integer.parseInt(ctaStr));
		  		DepositoBancario d = new DepositoBancario();
		  		d.setTipoDeposito(DepositoBancario.ID_TIPO_TRANSFERENCIA);
		  		d.setImporte(new BigDecimal(importe));
		  		d.setFecha(fecha);
		  		d.setCuentaBancaria(cb);
		  		fi.setIngreso(d);
		  	  }else if("BANK".equals(fPago)|| "BAN".equals(fPago) ) {
		  		//Falta agregar cta bcriafPago
		  		        if("BAN".equals(fPago) && !"00010".equalsIgnoreCase(sucursal)) {
		  		        	fPago="BANK";
		  		        }if("BAN".equals(fPago) && "00010".equalsIgnoreCase(sucursal)) {
		  		        	fPago="NECO";
		  		        }
		  				String ctaStr=TraeListasServiceUtil.getSystemConfig("HOTELES_CTA_BCRIA_TRANSFERENCIA_"+fPago);
		  				CuentaBancaria cb= new CuentaBancaria();
		  				cb.setId_cuenta_bcria(Integer.parseInt(ctaStr));
		  				DepositoBancario d = new DepositoBancario();
		  				d.setTipoDeposito(DepositoBancario.ID_TIPO_TRANSFERENCIA);
		  				d.setImporte(new BigDecimal(importe));
		  				d.setFecha(fecha);
		  				d.setCuentaBancaria(cb);
		  				fi.setIngreso(d);
		  	  }else if("FTA".equals(fPago)) {
		  //Como imputar
		  		FinanciacionTurismo d = new FinanciacionTurismo();
		  		d.setImporte(new BigDecimal(importe));
		  		d.setFecha(fecha);
		  		fi.setIngreso(d);
		  	  }else if("CxC".equals(fPago)  || "DSP".equals(fPago)) {
		  		CuentaCorriente d = new CuentaCorriente();
		  		d.setImporte(new BigDecimal(importe));
		  		d.setFecha(fecha);
		  		fi.setIngreso(d);
		  		
		  	  }else if("MP".equals(fPago) && "00030".equalsIgnoreCase(sucursal)) {
		  		//Falta agregar cta bcria
		  				String ctaStr=TraeListasServiceUtil.getSystemConfig("HOTELES_CTA_BCRIA_TRANSFERENCIA_BFR");
		  				CuentaBancaria cb= new CuentaBancaria();
		  				cb.setId_cuenta_bcria(Integer.parseInt(ctaStr));
		  				DepositoBancario d = new DepositoBancario();
		  				d.setTipoDeposito(DepositoBancario.ID_TIPO_TRANSFERENCIA);
		  				d.setImporte(new BigDecimal(importe));
		  				d.setFecha(fecha);
		  				d.setCuentaBancaria(cb);
		  				fi.setIngreso(d);
		      }else if("MP".equals(fPago) && "00020".equalsIgnoreCase(sucursal)) {
		  		//Falta agregar cta bcria
		  		String ctaStr=TraeListasServiceUtil.getSystemConfig("HOTELES_CTA_BCRIA_TRANSFERENCIA_BANK");
		  		CuentaBancaria cb= new CuentaBancaria();
		  		cb.setId_cuenta_bcria(Integer.parseInt(ctaStr));
		  		DepositoBancario d = new DepositoBancario();
		  		d.setTipoDeposito(DepositoBancario.ID_TIPO_TRANSFERENCIA);
		  		d.setImporte(new BigDecimal(importe));
		  		d.setFecha(fecha);
		  		d.setCuentaBancaria(cb);
		  		fi.setIngreso(d);
		      }else if("MP".equals(fPago) && "00010".equalsIgnoreCase(sucursal)) {
		  		//Falta agregar cta bcria
		  		String ctaStr=TraeListasServiceUtil.getSystemConfig("HOTELES_CTA_BCRIA_TRANSFERENCIA_NECO");
		  		CuentaBancaria cb= new CuentaBancaria();
		  		cb.setId_cuenta_bcria(Integer.parseInt(ctaStr));
		  		DepositoBancario d = new DepositoBancario();
		  		d.setTipoDeposito(DepositoBancario.ID_TIPO_TRANSFERENCIA);
		  		d.setImporte(new BigDecimal(importe));
		  		d.setFecha(fecha);
		  		d.setCuentaBancaria(cb);
		  		fi.setIngreso(d);
		      }else if("CHQ".equals(fPago)) {
		    	    CuentaBancaria cb = null;
					String nroCheque= String.valueOf((recibo.getNumero()*1000))
							 + String.format("%03d", ++qCheque);
					
					String idBanco=TraeListasServiceUtil.getSystemConfig("HOTELES_BCO_CHEQUE_CTA_CTE");
					String idCtaBcria=TraeListasServiceUtil.getSystemConfig("HOTELES_BCO_CTA_BCRIA_CTA_CTE");
					String idCuit=TraeListasServiceUtil.getSystemConfig("HOTELES_CUIT_CHEQUES_CTA_CTE");
					
					Cheque cheque = new Cheque(new BigDecimal(nroCheque), Integer.parseInt(idBanco));
					cheque.setImporte(new BigDecimal(importe));
					Estado estado= new Estado(Cheque.Estado.RECIBIDO);
					cheque.setEstado(estado);
					cheque.setFecha(fecha);
					cheque.setDebitoCredito(Cheque.Tipo.CREDITO);
					cheque.setCuit(idCuit);
					
					Banco b = new Banco(Integer.parseInt(idBanco));
					cb = new CuentaBancaria(Integer.parseInt(idCtaBcria));
					cb.setBanco(b);
					cheque.setCuentaBancaria(cb);
			  		fi.setIngreso(cheque);	
		     }else {
		  	  Efectivo e = new Efectivo();
		  	  e.setImporte(new BigDecimal(importe));
		  	  e.setFecha(fecha);
		  	  fi.setIngreso(e);
		     }
		    }else {
		      Retencion ret = new Retencion();	
		      ret.setTipo(Retencion.GRAL);
		      ret.setImporte(new BigDecimal(importe));
		      ret.setFecha(fecha);
		      fi.setIngreso(ret);
		    	
		    } 
		    recibo.getIngresos().add(fi);  
		      
		      //////
		      mRecibos.put(claveRecibo,recibo);  
		      
		   }
		   qRow++; 
		} 

		
		if(mRecibos.size()>0){
			try {
			
		      for(Map.Entry<String, Recibo> entry : mRecibos.entrySet()) {
		        String k = entry.getKey();
		        Recibo rbo = entry.getValue();
		        for(FacturaIngreso i:rbo.getIngresos()) {
					 if(i.getIngreso().getImporte().compareTo(BigDecimal.ZERO)>0 ) {
						if( i.getIngreso()  instanceof  TarjetaDebitoCredito) {
							((TarjetaDebitoCredito) i.getIngreso()).setImporte(new BigDecimal(mRecibosNeto.get(k)));
						} else if( i.getIngreso()  instanceof  DepositoBancario) {
							((DepositoBancario) i.getIngreso()).setImporte(new BigDecimal(mRecibosNeto.get(k)));
						} else if( i.getIngreso()  instanceof  FinanciacionTurismo) {
							((FinanciacionTurismo) i.getIngreso()).setImporte(new BigDecimal(mRecibosNeto.get(k)));
						}else if( i.getIngreso()  instanceof  Efectivo) {
							((Efectivo) i.getIngreso()).setImporte(new BigDecimal(mRecibosNeto.get(k)));
						}else if( i.getIngreso()  instanceof  Cheque) {
							((Cheque) i.getIngreso()).setImporte(new BigDecimal(mRecibosNeto.get(k)));
						}
					 }else {
						 ((Retencion) i.getIngreso()).setImporte(  ((Retencion) i.getIngreso()).getImporte().negate());
					 }
		        }	 
		     }
			
			
		     for(Map.Entry<String, Recibo> entry : mRecibos.entrySet()) {
		        Recibo rbo = entry.getValue();
		        HotelesServiceUtil.updateRecibo(rbo, user.getScreenName()) ;
		     }     
		   }catch (Exception e) {
			   errores.add(e.getMessage());
		   }
		}
		
	   return errores;
} 
 

 
 public  List<String> procesarXLSRecibosRetencionesHoteles(ActionRequest actionRequest,  File archivo,String fileName) throws Exception{
	    User user = PortalUtil.getUser(actionRequest);
	    List<String>errores = new ArrayList<String>();
	    Map<String,Recibo> mRecibos = new HashMap<String,Recibo>();
	    Map<String,Double> mRecibosNeto = new HashMap<String,Double>();
	    
	    SimpleDateFormat sdf = new SimpleDateFormat();
		
		FileInputStream file = new FileInputStream(archivo);
		HSSFWorkbook workbook = new HSSFWorkbook(file);
		
		HSSFSheet sheet = workbook.getSheetAt(0);
		Iterator<Row> rowIterator = sheet.iterator();
		
		Row row;
		Integer qRow=0;
		Recibo recibo;	
	    Cliente cliente = new Cliente();
	    String hotel;
		Double importe=null;
		Date fecha=null;
		Double nroRecibo=null;
		String fPago=null;
		String facturas=null;
		String razon=null;
		String sucursal=null;
		String claveRecibo="";
		String movimientoBco="";
	    Double importeNeto=0D;
		while (rowIterator.hasNext()){
		    row = rowIterator.next();
		    if(qRow>=2){
		      
		       Iterator<Cell> cellIterator = row.cellIterator();
		       Cell celda;
		       Integer qCel=0;
		       
		       while (cellIterator.hasNext()){
		    	   
				celda = cellIterator.next();
				try{
				 
				  
				  if(qCel==0){//Hotel
					hotel=celda.getStringCellValue();
					sucursal= TraeListasServiceUtil.getSystemConfig("HOTEL_SUCURSAL_"+hotel);
					
				  }else if(qCel==1){//Fecha
					try {  
					   fecha=celda.getDateCellValue();	
					}catch(Exception e) {
					//   archivo.setNroRecibo(celda.toString());	
					}
				  }else if(qCel==2) { //Nro Recibo
					  nroRecibo=celda.getNumericCellValue();
				  }else if(qCel==3) { //Razón Social
					 razon =celda.getStringCellValue();
					 cliente.setRazonSocial(razon);
					
				  }else if(qCel==4) { //Forma de pago
					  fPago=celda.getStringCellValue();
				  }else if(qCel==5) { //Importe
				      importe=celda.getNumericCellValue();
				  }else if(qCel==6) { //Comentario 
					  movimientoBco=celda.getStringCellValue();
				  }
				}catch(Exception e){
					_log.debug(e);
				}
				qCel++;
			  }
		      claveRecibo=sucursal + String.format("%08d",nroRecibo.intValue()); 
		      recibo = mRecibos.get(claveRecibo);
		      importeNeto =mRecibosNeto.get(claveRecibo);
		      if(importeNeto==null) {
		    	  importeNeto=importe;
		      }else {
		    	  importeNeto+=importe;
		      }
		      mRecibosNeto.put(claveRecibo,importeNeto);
		      
		      if (recibo==null){
		    	 recibo = new Recibo();
		    	 recibo.setSucursal(sucursal);
		    	 recibo.setFecha(fecha);
		    	 recibo.setCliente(cliente);
		    	 recibo.setNumero(nroRecibo.longValue());
		    	 recibo.setDescripcion(facturas);
		      }
		      if(importe.doubleValue()>0D) {
		    	  recibo.setTotal(importe.doubleValue());
		      }
		      
		      /////// FORMA DE PAGO
		    FacturaIngreso fi= new FacturaIngreso();
 	  	    if(fPago.contains("IIBB")){
		  		  Retencion ret = new Retencion();	
			      ret.setTipo(Retencion.IIBB);
			      ret.setImporte(new BigDecimal(importe));
			      ret.setFecha(fecha);
			      fi.setIngreso(ret);
		  	}else if(fPago.contains("IVA")){
		  		  Retencion ret = new Retencion();	
			      ret.setTipo(Retencion.IVA);
			      ret.setImporte(new BigDecimal(importe));
			      ret.setFecha(fecha);
			      fi.setIngreso(ret);
		  	}else if(fPago.contains("SUSS")){
		  		  Retencion ret = new Retencion();	
			      ret.setTipo(Retencion.SUSS);
			      ret.setImporte(new BigDecimal(importe));
			      ret.setFecha(fecha);
			      fi.setIngreso(ret);
		  	}    
		  	recibo.getIngresos().add(fi);  
		    mRecibos.put(claveRecibo,recibo);  
		   }
		   qRow++; 
		} 

		
		if(mRecibos.size()>0){
			try {
			 for(Map.Entry<String, Recibo> entry : mRecibos.entrySet()) {
		        Recibo rbo = entry.getValue();
		        HotelesServiceUtil.updateReciboRetencion(rbo, user.getScreenName()) ;
		     }     
		   }catch (Exception e) {
			   errores.add(e.getMessage());
		   }
		}
		
	   return errores;
} 
 
 
public  List<ConceptoSueldos> procesarSueldosXLS(ActionRequest actionRequest,  File archivo,String fileName,String entidad,Integer sectorLiq,Integer cuentaNeteo) throws Exception{
	    User user = PortalUtil.getUser(actionRequest);
	    List<String>errores = new ArrayList<String>();
	    
	    List<ConceptoSueldos> lista = new ArrayList<ConceptoSueldos>();
	    
	    List<ConceptoSueldos> equivalencias = ContabilidadServiceUtil.equivalenciasSueldos(entidad, sectorLiq, null);
	    Map<Integer,ConceptoSueldos> mEquivalencias = new HashMap<Integer,ConceptoSueldos>();
	    
	    for(ConceptoSueldos c:equivalencias) {
	    	mEquivalencias.put(c.getCodigo(),c);
	    }
	    
	    SimpleDateFormat sdf = new SimpleDateFormat();
		
		FileInputStream file = new FileInputStream(archivo);
		HSSFWorkbook workbook = new HSSFWorkbook(file);
		
		HSSFSheet sheet = workbook.getSheetAt(0);
		Iterator<Row> rowIterator = sheet.iterator();
		
		Row row;
		Integer qRow=0;
		
		Double codigo=null;
		String descripcion=null;
		Double remunerativo=null;
		Double noRemunerativo=null;
		Double retencion=null;
		Double contribucion=null;
		
		while (rowIterator.hasNext()){
		    row = rowIterator.next();
		    if(qRow>-1){
		       Integer qCel=0;
		       
		       codigo=null;
			   descripcion=null;
			   remunerativo=0D;
			   noRemunerativo=0D;
			   retencion=0D;
			   contribucion=0D;
			   try{
			      Cell celdaCodigo = row.getCell(0,Row.MissingCellPolicy.CREATE_NULL_AS_BLANK); 
			      Cell celdaDescripcion = row.getCell(1,Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
			      Cell celdaRemunerativo = row.getCell(3,Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
			      Cell celdaNoRemunerativo = row.getCell(4,Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
			      Cell celdaRetencion = row.getCell(5,Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
			      Cell celdaContribucion = row.getCell(6,Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
			      
			      if(celdaCodigo.getCellType()==CellType.NUMERIC ) codigo = celdaCodigo.getNumericCellValue();
			      if(celdaDescripcion.getCellType()==CellType.STRING ) descripcion = celdaDescripcion.getStringCellValue();
			      if(celdaRemunerativo.getCellType()==CellType.NUMERIC ) remunerativo = celdaRemunerativo.getNumericCellValue();
			      if(celdaNoRemunerativo.getCellType()==CellType.NUMERIC ) noRemunerativo = celdaNoRemunerativo.getNumericCellValue();
			      if(celdaRetencion.getCellType()==CellType.NUMERIC ) retencion = celdaRetencion.getNumericCellValue();
			      if(celdaContribucion.getCellType()==CellType.NUMERIC ) contribucion = celdaContribucion.getNumericCellValue();
			    
			      if(codigo!=null) {
			    	  
			    	  
				   ConceptoSueldos concepto = new ConceptoSueldos();
		           concepto.setCodigo(codigo.intValue());
		           concepto.setDescripcion(descripcion);
		           concepto.setRemunerativo(remunerativo!=null?remunerativo:0D);
		           concepto.setNoRemunerativo(noRemunerativo!=null?noRemunerativo:0D);
		           concepto.setRetencion(retencion!=null?retencion:0D);
		           concepto.setContribucion(contribucion!=null?contribucion:0D);
		           concepto.setSectorLiquidado(sectorLiq);
		           concepto.setEntidad(entidad);
		           
		           ConceptoSueldos eq=mEquivalencias.get(codigo.intValue());
		           if(eq==null) {
		        	   concepto.setId(0);
		        	   concepto.setConProblema(true);
		        	   concepto.setError("SE");
		           }else {
		        	   concepto.setId(eq.getId());
		        	   concepto.setCuentaContable(eq.getCuentaContable());
		        	   concepto.setDebeHaber(eq.getDebeHaber());
		        	   concepto.setError("OK");
		        	   concepto.setConProblema(false);
		           }
		           lista.add(concepto);
			      }  
			    
			   }catch(Exception e1) {_log.debug(e1);}
			  
		    }
		   qRow++; 
		} 
	   return lista;
}


public List<Detalle> procesarAsientoXLS(ActionRequest actionRequest,  File archivo,String fileName,String entidad,List<PlanCuentas>cuentas,
		List<CentroCosto>centros) throws Exception{
    User user = PortalUtil.getUser(actionRequest);
    List<String>errores = new ArrayList<String>();
    
    List<Detalle> lista = new ArrayList<Detalle>();
    
    SimpleDateFormat sdf = new SimpleDateFormat();
	
	FileInputStream file = new FileInputStream(archivo);
	HSSFWorkbook workbook = new HSSFWorkbook(file);
	
	HSSFSheet sheet = workbook.getSheetAt(0);
	Iterator<Row> rowIterator = sheet.iterator();
	
	Row row;
	Integer qRow=0;
	
	String codigo=null;
	Double debe=null;
	Double haber=null;
	Integer centroId=null;
	
	while (rowIterator.hasNext()){
	    row = rowIterator.next();
	    if(qRow>-1){
	       Integer qCel=0;
	       
	       codigo=null;
		   debe=0D;
		   haber=0D;
		   centroId=0;
		   try{
		      Cell celdaCodigo = row.getCell(0,Row.MissingCellPolicy.CREATE_NULL_AS_BLANK); 
		      Cell celdaDebe = row.getCell(1,Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
		      Cell celdaHaber = row.getCell(2,Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
		      Cell celdaCCosto = row.getCell(3,Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
		      
		      
		      if(celdaCodigo.getCellType()==CellType.STRING ) codigo = celdaCodigo.getStringCellValue();
		      if(celdaDebe.getCellType()==CellType.NUMERIC ) debe = celdaDebe.getNumericCellValue();
		      if(celdaHaber.getCellType()==CellType.NUMERIC ) haber = celdaHaber.getNumericCellValue();
		      if(celdaCCosto.getCellType()==CellType.NUMERIC ) centroId = (int) celdaCCosto.getNumericCellValue();
		      
		      if(codigo!=null) {
		    	  Detalle detalle= new Detalle();
		    	  PlanCuentas c = new PlanCuentas();
		    	  CentroCosto centro = new CentroCosto(0,"");
		    	  for(PlanCuentas p:cuentas) {
		    		  if(p.getNumero().equals(codigo)) {
		    			  detalle.setCuenta(p);
		    			  break;
		    		  }
		    	  }
		    	  
		    	  for(CentroCosto p:centros) {
		    		  if(p.getId().equals(centroId)) {
		    			  detalle.setCentroCosto(p);
		    			  break;
		    		  }
		    	  }
		    	  
		    	  if(detalle.getCuenta()==null || detalle.getCuenta().getId()==0) {
		    		  c.setNumero(codigo);
		    		  c.setCuenta("ERROR - NO EXISTE EN EL PLAN DE CUENTAS");
		    		  c.setTipo("ERR");
		    		  detalle.setCuenta(c);
		    	  }
		    	  
		    	  /*
		    	  if(detalle.getCentroCosto()==null && centroId>0) {
		    		  centro = new CentroCosto(centroId,"ERROR -CENTRO DE COSTO NO HABILITADO");
		    		  c.setTipo("ERR");
		    		  detalle.setCentroCosto(centro);
		    	  }
		    	  */
		    	  
		    	  detalle.setDebe(new BigDecimal(debe));
		    	  detalle.setHaber(new BigDecimal(haber));
		    	  detalle.setId(qRow);
		    	  lista.add(detalle);
			   }  
		   }catch(Exception e1) {_log.debug(e1);}
	    }
	   qRow++; 
	} 
   return lista;
}


public List<CoeficienteAjusteInflacion> procesarCoeficientesAjusteInflacionXLS(ActionRequest actionRequest,  File archivo,String fileName,Integer entidad) throws Exception{
    User user = PortalUtil.getUser(actionRequest);
    List<String>errores = new ArrayList<String>();
    
    List<CoeficienteAjusteInflacion> lista = new ArrayList<CoeficienteAjusteInflacion>();
    
    SimpleDateFormat sdf = new SimpleDateFormat();
	
	FileInputStream file = new FileInputStream(archivo);
	HSSFWorkbook workbook = new HSSFWorkbook(file);
	
	HSSFSheet sheet = workbook.getSheetAt(0);
	Iterator<Row> rowIterator = sheet.iterator();
	
	Row row;
	Integer qRow=0;
	
	Double periodo=null;
	Double coeficiente=null;
	
	while (rowIterator.hasNext()){
	    row = rowIterator.next();
	    if(qRow>-1){
	      periodo=0D;
		   coeficiente=0D;
		   try{
		      Cell celdaPeriodo = row.getCell(0,Row.MissingCellPolicy.CREATE_NULL_AS_BLANK); 
		      Cell celdaCoeficiente = row.getCell(1,Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
		    
		      
		      
		      if(celdaPeriodo.getCellType()==CellType.NUMERIC ) periodo =celdaPeriodo.getNumericCellValue();
		      if(celdaCoeficiente.getCellType()==CellType.NUMERIC ) coeficiente = celdaCoeficiente.getNumericCellValue();
		    
		      if(celdaPeriodo!=null) {
		    	  CoeficienteAjusteInflacion detalle= new CoeficienteAjusteInflacion();
		    	  detalle.setEntidad(entidad);
		    	  detalle.setPeriodo(periodo.intValue());
		    	  detalle.setCoeficiente(new BigDecimal(coeficiente).setScale(6, RoundingMode.HALF_UP));
		    	  lista.add(detalle);
			   }  
		   }catch(Exception e1) {_log.debug(e1);}
	    }
	   qRow++; 
	} 
   return lista;
}

@SuppressWarnings("deprecation")
public void procesarArchivoAdmifarm(User usuario, BufferedReader scanner, Date fechaArchivo) 
        throws Exception {

    //determina período
    Calendar cal = Calendar.getInstance();
    cal.setTime(fechaArchivo);
    int mes = cal.get(Calendar.MONTH) + 1;
    int anio = cal.get(Calendar.YEAR);

    String nombreTabla = "admifarm_monotributo_" + String.format("%02d", mes) + anio;

    //verifica si el período ya fue procesado antes de leer el archivo
    if (servicio.buscaPeriodoProcesadoAdmifarm(nombreTabla)) {
        throw new ArchivoAdmifarmIncorrectoException(3); // período ya procesado
    }

    try {

        //procesa archivo completo
        new ProcesaArchivosAdmifarm().procesarArchivoAdmifarm(
                usuario,
                scanner,
                fechaArchivo
        );

    } catch (ArchivoAdmifarmIncorrectoException e) {
        throw e;

    } catch (Exception e) {
        _log.error("Error procesando archivo Admifarm", e);
        throw new ArchivoAdmifarmIncorrectoException(
                4, 
                "Error procesando archivo Admifarm: " + e.getMessage()
        );
    }
}

@SuppressWarnings("deprecation")
public void procesarArchivoAdmifarmOspimGeneral(User usuario, BufferedReader scanner, Date fechaArchivo) 
        throws Exception {

    //determina período
    Calendar cal = Calendar.getInstance();
    cal.setTime(fechaArchivo);
    int mes = cal.get(Calendar.MONTH) + 1;
    int anio = cal.get(Calendar.YEAR);

    String nombreTabla = "admifarm_ospim_general_" + String.format("%02d", mes) + anio;

    //verifica si el período ya fue procesado antes de leer el archivo
    if (servicio.buscaPeriodoProcesadoAdmifarmOspimGeneral(nombreTabla)) {
        throw new ArchivoAdmifarmGeneralOspimIncorrectoException(3); // período ya procesado
    }

    try {

        //procesa archivo completo
        new ProcesaArchivosAdmifarm().procesarArchivoAdmifarmOspimGeneral(
                usuario,
                scanner,
                fechaArchivo
        );

    } catch (ArchivoAdmifarmGeneralOspimIncorrectoException e) {
        throw e;

    } catch (Exception e) {
        _log.error("Error procesando archivo Admifarm", e);
        throw new ArchivoAdmifarmGeneralOspimIncorrectoException(
                4, 
                "Error procesando archivo Admifarm: " + e.getMessage()
        );
    }
}

public void procesarArchivoSUMARGF(BufferedReader scanner)
		throws IOException, SQLException,
		AfipCantidadRegistrosIncorrectaException, ParseException {
	SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd");	
	ArchivoSubsidioMitigacionAsimetricas nuevoArchivo = new ArchivoSubsidioMitigacionAsimetricas();
	HeaderSumaXxxx head = new HeaderSumaXxxx();
	DetalleSumaXxxx det = null;
	List<DetalleSumaXxxx> detalleList = new ArrayList<DetalleSumaXxxx>();
	
	String line;
	while ((line = scanner.readLine()) != null) {
		if (line != null && line.startsWith("HF")) {
			head.setTipoRegistro(line.substring(0,2));
			head.setTipoProceso(line.substring(2,12));
			head.setFechaEnvioAFIP(sdf.parse(line.substring(12,22)));
			head.setHoraEnvioAFIP(line.substring(22,30));
			head.setClvId(line.trim());
			nuevoArchivo.setHeader(head);
		} else if (line != null && line.startsWith("TF")) {
			FooterSumaXxxx ft =new FooterSumaXxxx(line);
			ft.setCodigo_archivo(head.getTipoProceso());
			nuevoArchivo.setFooter(ft);
		} else if (line != null && !line.trim().equals("")) {
			det = new DetalleSumaXxxx();
			det.setPeriodo(line.substring(0,6));
			det.setCodigoOOSS(line.substring(6,12));
			det.setCuil(line.substring(12, 23));
			det.setCantidadBeneficiarios(Integer.parseInt(line.substring(23,26)));
			Double subsidio = Double.parseDouble(line.substring(26,41))/100D;
			
			BigDecimal bd = new BigDecimal(subsidio);
			det.setTotalSubsidio(bd.setScale(2,RoundingMode.HALF_UP	));
			detalleList.add(det);
		}
	}
	
	nuevoArchivo.setDetalles(detalleList);

	servicio.grabaArchivoSubsidiosGF(nuevoArchivo);
}


}
