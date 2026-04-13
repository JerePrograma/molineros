package ar.com.ospim.procesaArchivos.services;

import java.io.BufferedReader;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import ar.com.ospim.afiliados.WebKeysAfiliados;
import ar.com.ospim.automatico.beans.ReporteAutomatico;
import ar.com.ospim.automatico.service.ReportesServiceUtil;
import ar.com.ospim.global.beans.Delegacion;
import ar.com.ospim.global.beans.Empresa;
import ar.com.ospim.novedades.beans.Novedad;
import ar.com.ospim.novedades.beans.NovedadPadronConsolidado;
import ar.com.ospim.novedades.exception.PeriodoArchivoDuplicadoException;
import ar.com.ospim.novedades.service.NovedadesServiceUtil;
import ar.com.ospim.procesaArchivos.beans.ArchivoARBAPadronAlicuota;
import ar.com.ospim.procesaArchivos.beans.ArchivoAfipRG830;
import ar.com.ospim.procesaArchivos.beans.ArchivoOSAportes;
import ar.com.ospim.procesaArchivos.beans.ArchivoSubsidioMitigacionAsimetricas;
import ar.com.ospim.procesaArchivos.beans.ArchivoSubsidioMitigacionAsimetricasSuma;
import ar.com.ospim.procesaArchivos.beans.DetalleOSAportes;
import ar.com.ospim.procesaArchivos.beans.DetalleSuma;
import ar.com.ospim.procesaArchivos.beans.DetalleSumaXxxx;
import ar.com.ospim.procesaArchivos.beans.FooterNomOSAportes;
import ar.com.ospim.procesaArchivos.beans.HeaderSumaXxxx;
import ar.com.ospim.procesaArchivos.beans.JubiladosSitaci;
import ar.com.ospim.procesaArchivos.beans.desempleo.ArchivoDesempleo;
import ar.com.ospim.procesaArchivos.beans.desempleo.DetalleDesempleo;
import ar.com.ospim.procesaArchivos.beans.dj.ArchivoDJ;
import ar.com.ospim.procesaArchivos.beans.dj.DetalleDJ;
import ar.com.ospim.procesaArchivos.beans.dj.FooterDJ;
import ar.com.ospim.procesaArchivos.beans.extraccionbancaria.ArchivoExtraccionBancaria;
import ar.com.ospim.procesaArchivos.beans.extraccionbancaria.DetalleExtraccionBancaria;
import ar.com.ospim.procesaArchivos.beans.farmaciaospim.ArchivoDesglose;
import ar.com.ospim.procesaArchivos.beans.farmaciaospim.Archivomedesp;
import ar.com.ospim.procesaArchivos.beans.farmaciaospim.DetalleDesglose;
import ar.com.ospim.procesaArchivos.beans.farmaciaospim.Detallemedesp;
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
import ar.com.ospim.procesaArchivos.beans.transferenciaexterna.ArchivoTransferenciaExterna;
import ar.com.ospim.procesaArchivos.beans.transferenciaexterna.DetalleTransferenciaExterna;
import ar.com.ospim.procesaArchivos.exception.RendicionBancoNacionRegistroDuplicado;
import ar.com.ospim.procesaArchivos.padron.FooterPadronContribuyentes;
import ar.com.ospim.util.ConnectionHelper;
import ar.com.ospim.util.DateUtils;
import ar.com.ospim.util.StringUtils;
import ar.com.ospim.webservice.dto.AfiliacionPrevencionDTO;

public class ProcesaArchivosServiceImpl {
	private static final int BATCH_UPDATES = 250;

	

	private static Log logger = LogFactoryUtil
			.getLog(ProcesaArchivosServiceImpl.class);

	public void editaTotalesHeaderMedEsp (Connection con , double totalConIva , double totalSinIva , int pkId) throws SQLException 
	{							
		CallableStatement  stmt = null;
		try {												
			String sql = "{call edita_totales_header_med_especial(?, ?, ?)}";	
			stmt =con.prepareCall(sql.toString());	
			stmt.setDouble(1,totalSinIva);  
			stmt.setDouble(2,totalConIva);  						
			stmt.setInt(3, pkId);			
			stmt.executeUpdate();			
		}catch (SQLException e) {			
			logger.error("ERROR ACTUALIZACION DE TOTALES HEADER MED ESP!", e);
			throw e;
		}finally {
			ConnectionHelper.cerrar(stmt);
		}					
	}
	

	
	
	public void correProcesosPrevencionArchivos(Connection con , String nombreTabla , String fechaProceso , String fechaProcesomenores) throws SQLException {							
		CallableStatement  stmt = null;
		try {												
			String sql = "{call proceso_archivo_farmacia_prevencion_updates(?,?,?)}";	
			stmt =con.prepareCall(sql.toString());
			stmt.setString (1,nombreTabla);
			stmt.setString (2,fechaProceso);
			stmt.setString (3,fechaProcesomenores);
			stmt.executeUpdate();			
		}catch (SQLException e) {			
			logger.error("ERROR EN EL PROCESO DE LA TABLA DE PREVENCION !", e);
			throw e;
		}finally {
			ConnectionHelper.cerrar(stmt);
		}					
	}
	
	
	public void creaTablaConciliacionPrevencionArchivo (Connection con ,String nombreTabla  ) throws SQLException 
	{							
		CallableStatement  stmt = null;
		try {												
			String sql = "{call crea_tabla_conciliacion_prevencion_archivo(?)}";
			//String nombreTabla ="conciliacion.farmacia_preven_"; // +String.valueOf(mes)+String.valueOf(anio);
			stmt =con.prepareCall(sql.toString());	
			stmt.setString(1,nombreTabla  );
			stmt.executeUpdate();			
		}catch (SQLException e) {			
			logger.error("ERROR EN LA CREACION DE LA TABLA DE CONCILIACION PREVENCION ARCHIVO!", e);
			throw e;
		}finally {
			ConnectionHelper.cerrar(stmt);
		}				
	}

	
	
	
	public int grabaHeaderMedEsp (Connection con , String  user, int mes, int anio,int cantRegistros , 
			double totalConIva , double totalSinIva ,Date FechaArchivo) throws SQLException 
	{	
		
		int pkHeader = 0 ;		
		CallableStatement  stmt = null;
		try {															
			String sql = "{call inserta_header_med_especial(?, ?, ?, ?, ?)}";			
			stmt =con.prepareCall(sql.toString());
			stmt.registerOutParameter(1, Types.INTEGER);
			stmt.setDate(1, new java.sql.Date(FechaArchivo.getTime()));
			stmt.setInt(2, cantRegistros);
			stmt.setDouble(3,totalSinIva);  
			stmt.setDouble(4,totalConIva);  						
			stmt.setString(5, user);			
			pkHeader = stmt.executeUpdate();
			if(stmt.getInt(1) > 0){
				logger.debug("return id cabecera");
				pkHeader =stmt.getInt(1);
			}

		}catch (SQLException e) {				
			ConnectionHelper.rollback(con);
		}finally {
			ConnectionHelper.cerrar(stmt);
		}
		
		return pkHeader ;		
	}

		
	public void procesarArchivoMedEsp(String header, BufferedReader scanner ,String  user, int mes, int anio ,Date fechaArchivo)
			throws Exception{		
		
		Archivomedesp nuevoArchivo = new Archivomedesp();			
		
		List<Detallemedesp> detalleList = new ArrayList<Detallemedesp>();
			
		String renglon = header;
		int contadorLinea = 0 ;
		
			while ((renglon = scanner.readLine()) != null) {
				logger.debug("Linea " + contadorLinea);
				logger.debug(renglon);
				detalleList.add(new Detallemedesp(renglon));
				contadorLinea++;
			}						
		
		nuevoArchivo.setDetalle(detalleList);		
		grabaArchivo(nuevoArchivo,user , mes,anio,detalleList.size() , fechaArchivo ); // graba datos archivo y resumen del archivo		 

	}

	public void procesarArchivoDesglose (HSSFWorkbook workbook ,String  user, int mes, int anio ,Date fechaArchivo)
			throws Exception{		
		
		ArchivoDesglose nuevoArchivo = new ArchivoDesglose();			
		DetalleDesglose  rowDatosExcel = new DetalleDesglose()  ;
		
		List<DetalleDesglose> detalleList = new ArrayList<DetalleDesglose>();
		NumberFormat format0D = new DecimalFormat("#0");
		NumberFormat format2D = new DecimalFormat("#0.00");
		SimpleDateFormat sdf= new SimpleDateFormat("dd/MM/yyyy");
		
		HSSFSheet sheet = workbook.getSheetAt(0);
		Iterator<Row> rowIterator = sheet.iterator();
		
		boolean data1 ;
		boolean data2 ;
		CellType data3;
		
		Row row;
		Integer qRow=0;
		while (rowIterator.hasNext()){
		    row = rowIterator.next();		    
		    if(qRow>0){		       	
		       Iterator<Cell> cellIterator = row.cellIterator();
		       Cell celda;
		       Integer qCel=0;		       
		       while (cellIterator.hasNext()){		    	   
				celda = cellIterator.next();
				try{  
				if (celda!=null ){				
					if(qCel==1){
						rowDatosExcel.setCod_col(format0D.format(celda.getNumericCellValue() ) );
					}else if(qCel==2){
						rowDatosExcel.setColegio(celda.toString() );
					}else if(qCel==3){
						rowDatosExcel.setCod_farmacia(celda.toString().replace(".0", ""));
					}else if(qCel==4){
						rowDatosExcel.setFarmacia(celda.toString());
					}else if(qCel==5){	
						rowDatosExcel.setCuit(format0D.format(celda.getNumericCellValue() ));
					}else if(qCel==6){	
						rowDatosExcel.setLocalidad(celda.toString());	
					}else if(qCel==7){	
						rowDatosExcel.setRegion(celda.toString());
					}else if(qCel==8){
						rowDatosExcel.setOrden(celda.toString().replace(".0", ""));
				   	}else if(qCel==9){	
						rowDatosExcel.setEnv(format0D.format(celda.getNumericCellValue() ));								
					}else if(qCel==10){	
						rowDatosExcel.setPvp(format2D.format(celda.getNumericCellValue()) );						
					}else if(qCel==11){		
						rowDatosExcel.setEntidad(format2D.format(celda.getNumericCellValue()) );						
					}else if(qCel==12){
						rowDatosExcel.setPorcentaje(celda.toString().replace(".0", ""));
					}else if(qCel==13){	
						rowDatosExcel.setTroquel(celda.toString().replace(".0", ""));
					}else if(qCel==14){	
						//rowDatosExcel.setRegistro(celda.toString() );
						rowDatosExcel.setRegistro(format0D.format(celda.getNumericCellValue())  );
					}else if(qCel==15){	
						rowDatosExcel.setNombre_comercial(celda.toString());
					}else if(qCel==16){	
						rowDatosExcel.setPot(celda.toString());
					}else if(qCel==17){	
						rowDatosExcel.setForma_farm(celda.toString());
					}else if(qCel==18){
						rowDatosExcel.setCont(celda.toString().replace(".0", ""));
					}else if(qCel==19){	
						rowDatosExcel.setPrincipio(celda.toString());
					}else if(qCel==20){	
						rowDatosExcel.setAccion(celda.toString());
					}else if(qCel==21){ //  && celda.getDateCellValue()!=null  && !celda.getDateCellValue().equals("")  ){						
						/*if(celda.getCellType() != HSSFCell.CELL_TYPE_NUMERIC && celda.getCellType() != HSSFCell.CELL_TYPE_STRING )   {
							rowDatosExcel.setFecha(sdf.format(celda.getDateCellValue()));
						}else{
							rowDatosExcel.setFecha(celda.getDateCellValue().toString() );
						}*/			
						
						//rowDatosExcel.setFecha(celda.toString() );
						
					}else if(qCel==22 ) { // && celda.getDateCellValue()!=null  && !celda.getDateCellValue().equals("")){	
						//rowDatosExcel.setDispensa(sdf.format(celda.getDateCellValue()));
						//rowDatosExcel.setFecha(celda.toString() );
					}else if(qCel==23){	
						rowDatosExcel.setMatricula(celda.toString().replace(".0", "")); 
					}else if(qCel==24){	
						rowDatosExcel.setProfesional(celda.toString());
					}else if(qCel==25){
						//rowDatosExcel.setGrupo(celda.toString().replace(".0", ""));
						String  valor;
						String  valor1;
						String  valor2;
						//data1 =celda.getCellType() == HSSFCell.CELL_TYPE_NUMERIC ;
						data1 =celda.getCellType() == CellType.NUMERIC ;
						//data2 =celda.getCellType() == HSSFCell.CELL_TYPE_STRING ;
						data2 =celda.getCellType() == CellType.STRING ;
						data3=celda.getCellType();
						valor1=celda.toString();						
						valor=String.valueOf(format0D.format(celda.getNumericCellValue())  )  ;
						
						rowDatosExcel.setGrupo(valor );
					}else if(qCel==26){	
						rowDatosExcel.setNombre_benef(celda.toString());
					}else if(qCel==27){	
						rowDatosExcel.setTp(celda.toString());
					}					
				}
		    
				}catch(Exception e){
					logger.debug(e);
				}
				qCel++;				
			  }	         
		   }
		   detalleList.add(rowDatosExcel ); 
		   rowDatosExcel = new DetalleDesglose()  ;
		   qRow++; 
		} 		
		nuevoArchivo.setDetalle(detalleList);		
		grabaArchivoDesglose(nuevoArchivo,user , mes,anio,detalleList.size() , fechaArchivo ); // graba datos archivo y resumen del archivo		 

	}
	
		
	public int grabaHeaderDesglosePrevencion (Connection con , String  user, int mes, int anio,int cantRegistros , double totalPvp , double totalEntidad
			, double totalOspim , double totalUoma ,double    totalAntima ,Date FechaArchivo) throws SQLException 
	{	
		
		int pkHeader = 0 ;		
		CallableStatement  stmt = null;
		try {															
			String sql = "{call inserta_totales_archivo_prevencion_farmacia(?,?,?,?,?,?,?,?)}";			
			stmt =con.prepareCall(sql.toString());
			stmt.registerOutParameter(1, Types.INTEGER);
			stmt.setDate(1, new java.sql.Date(FechaArchivo.getTime()));
			stmt.setInt(2, cantRegistros);
			stmt.setDouble(3,totalPvp);  
			stmt.setDouble(4,totalEntidad );
			stmt.setDouble(5,totalOspim);  
			stmt.setDouble(6,totalUoma);  						
			stmt.setDouble(7,totalAntima);
			stmt.setString(8, user);	

			stmt.executeUpdate();
			if(stmt.getInt(1) > 0){
				pkHeader =stmt.getInt(1);
				logger.debug("return id cabecera " + pkHeader);
			}

		}catch (SQLException e) {				
			ConnectionHelper.rollback(con);
		}
		
		return pkHeader ;		
	}

	
	public int grabaArchivo(ArchivoOSAportes archivo) throws SQLException {
		int result = 0;
		Connection con = null;
		PreparedStatement stmt = null;
		try {
			logger.debug("Comienzo a grabar archivo OS");
			con = ConnectionHelper.getReportesOspimConnection();

			con.setAutoCommit(false);
			Date fecha_proceso = archivo.getHeader().getFecha_proceso();
			String hora_proceso = archivo.getHeader().getHora_proceso();
			// Verifico si ya procesé el archivo por footer PK...
			String sql = "INSERT INTO informacion_afip.os_aportes_footer(fecha_proceso, hora_proceso, cant_reg, importe_total, deb_cred,"
					+ "cant_reg_tn, cant_trf_nom, importe_trf_nom, deb_cred2)"
					+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
			stmt = con.prepareStatement(sql.toString());
			stmt.setDate(1, new java.sql.Date(fecha_proceso.getTime()));
			stmt.setString(2, hora_proceso);
			stmt.setInt(3, archivo.getFooter().getCantidad_registros());
			stmt.setDouble(4, archivo.getFooter().getImporte_total()
					.doubleValue());
			stmt.setString(5, archivo.getFooter().getDebito_credito1());
			stmt.setInt(6, archivo.getFooter().getCantidad_reg_tn());
			stmt.setInt(7, archivo.getFooter().getCantidad_trf_nominada());
			stmt.setDouble(8, archivo.getFooter().getImporte_trf_nom()
					.doubleValue());
			stmt.setString(9, archivo.getFooter().getDebito_credito2());
			stmt.executeUpdate();
			logger.debug("archivo OS: Footer listo");
			ArrayList<DetalleOSAportes> detalles = new ArrayList<DetalleOSAportes>();
			ArrayList<DetalleOSAportes> detallesRem = new ArrayList<DetalleOSAportes>();

			if (archivo.getDetalle() != null) {
				for (DetalleOSAportes d : archivo.getDetalle()) {
					if (d.getConcepto_transf().equalsIgnoreCase("REM")) {
						detallesRem.add(d);
					} else {
						detalles.add(d);
					}
				}
			}

			grabarDetalles(archivo, con, fecha_proceso, hora_proceso, detalles);
			
			grabarDetallesRem(archivo, con, fecha_proceso, hora_proceso,
					detallesRem);

			logger.debug("archivo OS: detalles listos");
			sql = "INSERT INTO informacion_afip.os_aportes_footer_fn(fecha_proceso, hora_proceso, secuencia_reg, cant_trf_nom, importe_nom, deb_cred, cant_trf_fdo, importe_fdo_res,"
					+ "deb_cred2, cant_trf_ant, importe_ant, deb_cred3, saldo_ant_sin_nominar)"
					+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
			stmt = con.prepareStatement(sql.toString());
			ArrayList<FooterNomOSAportes> footers = new ArrayList<FooterNomOSAportes>(
					archivo.getFooterNom());
			int cont = 0;
			for (FooterNomOSAportes footerFN : footers) {
				cont++;
				stmt.setDate(1, new java.sql.Date(fecha_proceso.getTime()));
				stmt.setString(2, hora_proceso);
				stmt.setString(3, footerFN.getSecuencia_registro());
				stmt.setInt(4, footerFN.getCantidad_trf_nominada());
				stmt.setDouble(5, footerFN.getImporte_trf_nom().doubleValue());
				stmt.setString(6, footerFN.getDebito_credito1());
				stmt.setInt(7, footerFN.getCantidad_trf_fdo_rva());
				stmt.setDouble(8, footerFN.getImporte_trf_fdo_rva()
						.doubleValue());
				stmt.setString(9, footerFN.getDebito_credito2());
				stmt.setInt(10, footerFN.getCantidad_trf_anticipo());
				stmt.setDouble(11, footerFN.getImporte_trf_anticipo()
						.doubleValue());
				stmt.setString(12, footerFN.getDebito_credito3());
				stmt.setDouble(13, footerFN.getSaldo_anterior_sin_nominar()
						.doubleValue());
				stmt.executeUpdate();
				logger.debug("LINEA: " + cont);
			}
			logger.debug("archivo OS: Footer nom listo");
			con.commit();
			logger.debug("archivo OS: commiteado");
		} catch (SQLException e) {
			logger.error("Error al insertar archivo " + archivo.getFooter(), e);
			ConnectionHelper.rollback(con);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}

		return result;
	}

	private void grabarDetalles(ArchivoOSAportes archivo, Connection con,
			Date fechaProceso, String horaProceso,
			ArrayList<DetalleOSAportes> detalles) throws SQLException {
		String sql = "INSERT INTO informacion_afip.os_aportes_detalle("
				+ "fecha_proceso, hora_proceso, concepto_transf, importe, deb_cred,"
				+ "fecha_transf, fecha_recauda, cuit_contribuyente, periodo, num_oblig,"
				+ "sec_oblic, cuil_aportante, banco, sucur, zona, porc_reducc, porc_reducc2,"
				+ "porc_reducc3, grupo_fliar, tipo_pago, marca_apro)"
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		grabarDetalles(con, fechaProceso, horaProceso, detalles, sql);
	}
	
	
	private void grabarDetallesRem(ArchivoOSAportes archivo, Connection con,
			Date fechaProceso, String horaProceso,
			ArrayList<DetalleOSAportes> detallesRem) throws SQLException {
		String sql = "INSERT INTO informacion_afip.os_aportes_rem("
				+ "fecha_proceso, hora_proceso, concepto_transf, importe, deb_cred,"
				+ "fecha_transf, fecha_recauda, cuit_contribuyente, periodo, num_oblig,"
				+ "sec_oblic, cuil_aportante, banco, sucur, zona, porc_reducc, porc_reducc2,"
				+ "porc_reducc3, grupo_fliar, tipo_pago, marca_apro)"
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		grabarDetalles(con, fechaProceso, horaProceso, detallesRem, sql);
	}

	
	
	private void grabarDetalles(Connection con, Date fecha_proceso,
			String hora_proceso, ArrayList<DetalleOSAportes> detalles,
			String sql) throws SQLException {
		PreparedStatement stmt = null;
		if (detalles != null) {
			int cantGrabada = 0;
			try {
				while (cantGrabada < detalles.size()) {
					int falta = detalles.size() - cantGrabada;
					int hta = 0;
					if (falta > BATCH_UPDATES) {
						hta = cantGrabada + BATCH_UPDATES;
					} else {
						hta = detalles.size();
					}
					stmt = con.prepareStatement(sql.toString());
					for (int i = cantGrabada; i < hta; i++) {
						logger.debug("posicion " + i);
						
						DetalleOSAportes detalle = detalles.get(i);
						
						logger.debug("detalle " + detalle.toString());
						
						stmt.setDate(1, new java.sql.Date(fecha_proceso.getTime()));
						stmt.setString(2, hora_proceso);
						stmt.setString(3, detalle.getConcepto_transf());
						stmt.setDouble(4, detalle.getImporte().doubleValue());
						stmt.setString(5, detalle.getDebito_credito());
						stmt.setDate(6, new java.sql.Date(detalle.getFecha_transf()
								.getTime()));
						stmt.setDate(7, new java.sql.Date(detalle
								.getFecha_recauda().getTime()));
						stmt.setString(8, detalle.getCuit_contrib());
						stmt.setDate(9, new java.sql.Date(detalle.getPeriodo()
								.getTime()));
						stmt.setString(10, detalle.getNum_obligacion());
						stmt.setString(11, detalle.getSec_obligacion());
						stmt.setString(12, detalle.getCuit_aportante());
						stmt.setString(13, detalle.getBanco());
						stmt.setString(14, detalle.getCod_sucur());
						stmt.setString(15, detalle.getZona());
						stmt.setInt(16, detalle.getPorc_reduccion());
						stmt.setInt(17, detalle.getPorc_reduccion2());
						stmt.setInt(18, detalle.getPorc_reduccion3());
						stmt.setString(19, detalle.getGrupo_fliar());
						stmt.setString(20, detalle.getTipo_pago());
						stmt.setString(21, detalle.getMarca_aprop());
						stmt.addBatch();
						cantGrabada++;
					}
					stmt.executeBatch();
				}
			}catch (Exception e) {
				logger.error(e);
				throw new SQLException();
			}finally {
				ConnectionHelper.cerrar(stmt);
			}	
		}
	}

	public int grabaArchivo(ArchivoDJ archivo) throws SQLException, Exception {
		int result = 0;
		Connection con = null;
		PreparedStatement stmt = null;
		PreparedStatement stmt1 = null;
		try {
			logger.debug("archivo DJ: comienzo a grabar");
			con = ConnectionHelper.getReportesOspimConnection();
			con.setAutoCommit(false);
			FooterDJ footer = archivo.getFooter();
			if (footer != null) {
				String sql = "INSERT INTO informacion_afip.footer_declaracion_jurada("
						+ "tiporegistro, codigoregistro, indicadordeproceso, fechaproceso,"
						+ "cantempleadosporig, cantempleadosprect, cantregistros)"
						+ "VALUES (?, ?, ?, ?, ?, ?, ?)";
				stmt = con.prepareStatement(sql.toString());				
				stmt.setString(1, footer.getTipoRegistro());
				stmt.setString(2, footer.getCodigoRegistro());
				stmt.setString(3, footer.getIndicadorDeProceso());
				stmt.setDate(4, new java.sql.Date(footer.getFechaProceso()
						.getTime()));
				stmt.setBigDecimal(5, new BigDecimal(footer
						.getCantEmpleadosPOrig().toString()));
				stmt.setBigDecimal(6, new BigDecimal(footer
						.getCantEmpleadosPRect().toString()));
				stmt.setBigDecimal(7, new BigDecimal(footer.getCantRegistros()
						.toString()));
				stmt.executeUpdate();
			}
			logger.debug("archivo DJ: footer listo");
			stmt1 = guardarDetalle(archivo, footer, con,"informacion_afip.detalle_declaracion_jurada");			
			logger.debug("archivo DJ: detalles listos");
			con.commit();
			logger.debug("archivo DJ: commiteado");
		} catch (SQLException e) {
			logger.debug("Error al insertar archivo DJ " + archivo.getFooter(),e);
			ConnectionHelper.rollback(con);
			throw e;
		} catch (Exception e) {
			logger.debug("Error al insertar archivo DJ " + archivo.getFooter(),e);
			ConnectionHelper.rollback(con);
			throw e;
		} finally {
			ConnectionHelper.cerrar(stmt);
			ConnectionHelper.cerrar(stmt1,con);			
		}
		return 0;
	}

	private PreparedStatement guardarDetalle(ArchivoDJ archivo,FooterDJ footer, Connection con, String tabla) throws SQLException, Exception {
		PreparedStatement stmt=null;
		if (archivo.getDetalle() != null) {
			int cantGrabada = 0;
			
			try {
				while (cantGrabada < archivo.getDetalle().size()) {
					int falta = archivo.getDetalle().size() - cantGrabada;
					int hta = 0;
					if (falta > BATCH_UPDATES) {
						hta = cantGrabada + BATCH_UPDATES;
					} else {
						hta = archivo.getDetalle().size();
					}
					String sql = "INSERT INTO "+tabla +"("
							+ "codigoobrasocial, periodo, cuit, cuil, remuneracionafectos,"
							+ "importeadicionalos, zona, cantgrupofamiliar, cantadherentesgrupofamiliar,"
							+ "secobligacion, condicioncuil, situacioncuil, actividad, modalidad, "
							+ "codigosiniestro, aporteadicionalos, versionaplicativo, remuneraciondecreto1273_02,"
							+ "esposa, excedenteaporteos, declaroretenciones, declaro, fechapresentacion,"
							+ "fechaproceso, original, importebasecontribucionos, fecha_proceso,aporte_obra_social,"
							+ "contribucion_obra_social,remuneracion_total,obra_social_informada) "
							+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?, ?, ?, ?,?,?,?);";
					stmt = con.prepareStatement(sql.toString());
					for (int i = cantGrabada; i < hta; i++) {
						DetalleDJ det = archivo.getDetalle().get(i);
						stmt.setString(1, det.getCodigoObraSocial());
						stmt.setDate(2, new java.sql.Date(det.getPeriodo()
								.getTime()));
						stmt.setString(3, det.getCuit());
						stmt.setString(4, det.getCuil());
						stmt.setBigDecimal(5, det.getRemuneracionAfectOS());
						stmt.setBigDecimal(6, det.getImporteAdicionalOS());
						stmt.setString(7, det.getZona());
						stmt.setString(8, det.getCantGrupoFamiliar());
						stmt.setString(9, det.getCantAdherentesGrupoFamiliar());
						stmt.setInt(10, det.getSecObligacion());
						stmt.setString(11, det.getCondicionCuil());
						stmt.setString(12, det.getSituacionCuil());
						stmt.setString(13, det.getActividad());
						stmt.setInt(14, det.getModalidad());
						stmt.setString(15, det.getCodigoSiniestro());
						stmt.setBigDecimal(16, det.getAporteAdicionalOS());
						stmt.setString(17, det.getVersionAplicativo());
						stmt.setBigDecimal(18,
								det.getRemuneracionDecreto1273_02());
						stmt.setString(19, det.getEsposa());
						stmt.setBigDecimal(20, det.getExcedenteAporteOS());
						stmt.setBoolean(21, det.isDeclaroRetenciones());
						stmt.setBoolean(22, det.isDeclaro());
						stmt.setDate(23, new java.sql.Date(det
								.getFechaPresentacion().getTime()));
						stmt.setDate(24, new java.sql.Date(det
								.getFechaProceso().getTime()));
						stmt.setString(25, String.valueOf(det.getOriginal()));
						stmt.setBigDecimal(26,
								det.getImporteBaseContribucionOS());
						stmt.setDate(27, new java.sql.Date(footer
								.getFechaProceso().getTime()));
						
						stmt.setBigDecimal(28,det.getAporteBasicoOS());
						stmt.setBigDecimal(29,det.getContribucionOS());
						stmt.setBigDecimal(30,det.getRemuneracionTotal());
						stmt.setString(31, String.valueOf(det.getObraSocialInformada()));
						
						
						stmt.addBatch();
						cantGrabada++;
					}
					stmt.executeBatch();
				}
			}catch (Exception e) {
				logger.error(e);
				throw new SQLException();
			}finally {
				ConnectionHelper.cerrar(stmt);
			}	
		}
		return stmt;
	}

	
		
	
	
	public void grabaArchivo(ArchivoPadronContribuyentes archivo)
			throws SQLException {
		Connection con = null;
		PreparedStatement stmt = null;
		try {
			logger.debug("archivo PA: comienzo a grabar");
			con = ConnectionHelper.getReportesOspimConnection();
			con.setAutoCommit(false);
			FooterPadronContribuyentes footer = archivo.getFooter();
			if (footer != null) {
				String sql = "INSERT INTO informacion_afip.footer_padron_contribuyentes("
						+ "tiporegistro, codigoregistro, indicadordeproceso, fechaproceso, cantregistros)"
						+ "VALUES (?, ?, ?, ?, ?)";
				stmt = con.prepareStatement(sql.toString());
				stmt.setString(1, footer.getTipoRegistro());
				stmt.setString(2, footer.getCodigoRegistro());
				stmt.setString(3, footer.getIndicadorDeProceso());
				stmt.setDate(4, new java.sql.Date(footer.getFechaProceso()
						.getTime()));
				stmt.setBigDecimal(5, new BigDecimal(footer.getCantRegistros()
						.toString()));
				stmt.executeUpdate();
			}
			logger.debug("archivo PA: footer listo");
			if (archivo.getDetalle() != null) {
				int cantGrabada = 0;
				while (cantGrabada < archivo.getDetalle().size()) {
					int falta = archivo.getDetalle().size() - cantGrabada;
					int hta = 0;
					if (falta > BATCH_UPDATES) {
						hta = cantGrabada + BATCH_UPDATES;
					} else {
						hta = archivo.getDetalle().size();
					}
					String sql = "INSERT INTO informacion_afip.detalle_padron_contribuyentes(cuit, razonsocial, "
							+ "calle, numero, piso, dpto, localidad, provincia, codigopostal, fecha_proceso, codigo_ooss, "
							+ "cod_act_ppal, cod_act_sec1, cod_act_sec2)"
							+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
					stmt = con.prepareStatement(sql.toString());
					for (int i = cantGrabada; i < hta; i++) {
						DetallePadronContribuyentes det = archivo.getDetalle().get(i);
						stmt.setBigDecimal(1, new BigDecimal(det.getCuit().toString()));
						stmt.setString(2, det.getRazonSocial());
						stmt.setString(3, det.getCalle());
						stmt.setString(4, det.getNumero());
						stmt.setString(5, det.getPiso());
						stmt.setString(6, det.getDpto());
						stmt.setString(7, det.getLocalidad());
//						stmt.setString(8, det.getProvincia());
						stmt.setInt(8, Integer.parseInt(det.getProvincia()));
						stmt.setString(9, det.getCodigoPostal());
						stmt.setDate(10, new java.sql.Date(footer.getFechaProceso().getTime()));
						stmt.setString(11, det.getCodigoOOSS());
						stmt.setString(12, det.getCOD_ACT_PPAL());
						stmt.setString(13, det.getCOD_ACT_SEC1());
						stmt.setString(14, det.getCOD_ACT_SEC2());
						stmt.addBatch();
						cantGrabada++;
					}
					stmt.executeBatch();
				}
			}
			logger.debug("archivo PA: detalles listos");
			con.commit();
			logger.debug("archivo PA: commiteado");
		} catch (SQLException e) {
			logger.debug("Error al insertar archivo PA " + archivo.getFooter(),e);
			ConnectionHelper.rollback(con);
			throw e;
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
	}
	
	
	public void grabaArchivo(Archivomedesp archivo,String  user, int mes, int anio,int cantrecords,Date FechaArchivo)
			throws SQLException {
		Connection con = null;
		PreparedStatement stmt = null;		
		double TotalConIvaArchivo = 0;
		double TotalSinIvaArchivo = 0;
		int pkheader =0;
			
		try {
			logger.debug("archivo MedEsp: comienzo a grabar");
			con = ConnectionHelper.getReportesOspimConnection();
			con.setAutoCommit(false);			
			if (archivo.getDetalle() != null) {
				    int cantGrabada = 0;									
					int hta = 0;
					hta = archivo.getDetalle().size();					
					
					logger.debug("Graba datos de la cabecera del archivo totales etc");			
					Integer pkid = grabaHeaderMedEsp(con,user , mes,anio,cantrecords,TotalConIvaArchivo,TotalSinIvaArchivo,FechaArchivo);
										
/*
					String sql = "INSERT INTO medespecial_detalle (fecha_compra ,cuit_proveedor ,proveedor, cuil , "
							+ "nombre,cod_medicamento,medicamento,cantidad,preciosiniva,precioconiva,iva,"
							+ "totalconiva,totalsiniva,plan,afiliado,docu_numero,troquel,id_medespecial,inte) "
							+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?,?);";
*/							
					
					String sql = "{call inserta_medespecial_detalle(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?,?) }";
//					stmt = con.prepareStatement(sql.toString());
					stmt = con.prepareCall(sql.toString());
					
					for (int i = cantGrabada; i < hta; i++) {
						Detallemedesp det = archivo.getDetalle()
								.get(i);						
						stmt.setDate(1, new java.sql.Date(det.getFecha_compra().getTime()));					
						stmt.setString(2, det.getCuit_proveedor() );
						stmt.setString(3, det.getProveedor() );
						stmt.setString(4, det.getCuil() );						
						stmt.setString(5, det.getNombre() );
						stmt.setString(6, det.getCod_medicamento()  );
						stmt.setString(7, det.getMedicamento()  );
						stmt.setInt(8, det.getCantidad() );	
						stmt.setDouble(9, det.getPreciosiniva()  );
						stmt.setDouble(10, det.getPrecioconiva()   );
						stmt.setDouble(11, det.getIva()  );
						stmt.setDouble(12, det.getTotalconiva()  );
						stmt.setDouble(13, det.getTotalsiniva()  );																						
						stmt.setString(14, det.getPlan()   );
						stmt.setString(15, det.getAfiliado()  );
						stmt.setString(16, det.getDocu_numero()   );
						stmt.setInt(17, det.getTroquel()  );						
						stmt.setInt(18, pkid   );
						stmt.setInt(19, det.getInte()  );
						
						TotalConIvaArchivo= TotalConIvaArchivo + det.getTotalconiva();
						TotalSinIvaArchivo  = TotalSinIvaArchivo   + det.getTotalsiniva();
						stmt.addBatch();
						cantGrabada++; 
					}
					stmt.executeBatch();
					pkheader =pkid   ;
			}			
			
			editaTotalesHeaderMedEsp(con,TotalConIvaArchivo,TotalSinIvaArchivo,pkheader );
			con.commit();
			logger.debug("archivo MedEsp : commiteado");
		} catch (SQLException e) {
			logger.debug("Error al insertar en medespecialregistros",e);
			ConnectionHelper.rollback(con);
			throw e;
		}catch (Exception e1) {
			logger.debug("Error al insertar en medespecialregistros",e1);
			ConnectionHelper.rollback(con);
		}finally {
			ConnectionHelper.cerrar(stmt, con);
		}				
	}


	public void grabaArchivoDesglose(ArchivoDesglose archivo,String  user, int mes, int anio,int cantrecords,Date FechaArchivo)
			throws SQLException, Exception {
			Connection con = null;
			PreparedStatement stmt = null;
			
			int pkheader =0;
			String nombreTabla ="conciliacion.farmacia_preven_"+String.format ("%02d", mes);
			nombreTabla =nombreTabla +String.valueOf(anio); 
			String fechaPeriodoArchivo=String.valueOf(anio) + String.format ("%02d", mes)+"01";
			String fechaMenor=String.valueOf(anio-1) + String.format ("%02d", mes)+"01";
			try {
				logger.debug("archivo Desglose: comienzo a grabar");
				con = ConnectionHelper.getReportesOspimConnection();
				
				con.setAutoCommit(false);
				
				creaTablaConciliacionPrevencionArchivo(con, nombreTabla  );	
		
			    int cantGrabada = 0;									
				int hta = 0;
				hta = archivo.getDetalle().size();					
				
				logger.debug("Graba datos de la cabecera del archivo totales etc");			
				Integer pkid = grabaHeaderDesglosePrevencion (con,user , mes,anio,cantrecords,0,0,0,0,0,FechaArchivo);
				String sql = "INSERT INTO " + nombreTabla + " (cod_col,colegio,cod_farmacia, farmacia, "
						+ "cuit,direccion, localidad,region,codReg,codReceta,orden,env,pvp,entidad,porcentaje,troquel,registro,nombre_comercial,pot,forma_farm,cont,principio,accion,fecha,dispensa,matricula,profesional,grupo,nombre_benef,tp,id_cabecera)"
						+ "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);";
				stmt = con.prepareStatement(sql.toString());										
				
				for (int i = cantGrabada; i < hta; i++) {
					
					DetalleDesglose det = archivo.getDetalle().get(i);						
					if (det.getCod_farmacia()!=null){
						stmt.setString(1, det.getCod_col());											
						stmt.setString(2, det.getColegio() );
						stmt.setString(3, det.getCod_farmacia() );
						stmt.setString(4, det.getFarmacia()  );						
						stmt.setString(5, det.getCuit()  );
						stmt.setString(6, det.getDireccion()  );
						stmt.setString(7, det.getLocalidad()   );
						stmt.setString(8, det.getRegion()   );
						stmt.setString(9, det.getCodReg()   );
						stmt.setString(10, det.getCodReceta()   );						
						stmt.setString(11, det.getOrden()   );
						stmt.setString(12, det.getEnv()   );
						stmt.setString(13, det.getPvp()   );
						stmt.setString(14, det.getEntidad()    );
						stmt.setString(15, det.getPorcentaje()    );
						stmt.setString(16, det.getTroquel()    );
						stmt.setString(17, det.getRegistro()    );
						stmt.setString(18, det.getNombre_comercial()   );
						stmt.setString(19, det.getPot()   );
						stmt.setString(20, det.getForma_farm()  );
						stmt.setString(21, det.getCont()   );
						stmt.setString(22, det.getPrincipio()   );
						stmt.setString(23, det.getAccion()   );
						stmt.setString(24, det.getFecha()   );
						stmt.setString(25, det.getDispensa()   );						
						stmt.setString(26, det.getMatricula()   );
						stmt.setString(27, det.getProfesional()  );
						stmt.setString(28, det.getGrupo()  );
						stmt.setString(29, det.getNombre_benef()   );
						stmt.setString(30, det.getTp()    );						
						stmt.setInt(31, pkid );
						stmt.addBatch();
						cantGrabada++;
				}
			}
			stmt.executeBatch();
			pkheader =pkid   ;		
				
			correProcesosPrevencionArchivos(con,nombreTabla,fechaPeriodoArchivo,fechaMenor);
	
			con.commit();
			
			logger.debug("archivo Desglose Farmacia Prevencion : commiteado");
		} catch (SQLException e) {
			logger.debug("Error al procesar archivo farmacia desglose",e);
			ConnectionHelper.rollback(con);
			throw e;
		}catch (Exception e1) {
			logger.debug("Error el script de update masivos ",e1);
			ConnectionHelper.rollback(con);
			throw e1;
		}finally {
			ConnectionHelper.cerrar(stmt, con);
		}				
	}

	
	
	public void grabaArchivo(ArchivoSubsidioOS archivo) throws SQLException {
		Connection con = null;
		PreparedStatement stmt = null;
		try {
			logger.debug("archivo SO: comienzo a grabar");
			con = ConnectionHelper.getReportesOspimConnection();
			con.setAutoCommit(false);
			FooterSubsidioOS footer = archivo.getFooter();
			if (footer != null) {
				String sql = "INSERT INTO footer_subsidio_os(fecha_proceso, tiporegistro, identificador, codigoos, cantidadregistrosde,"
						+ "cantidadregistrosto, cantidadregistrosdt, cantidadregistros, importesubsidio, debitocredito, importesubsidioreal)"
						+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
				stmt = con.prepareStatement(sql.toString());
				stmt.setDate(1, new java.sql.Date(archivo.getHeader()
						.getFechaProceso().getTime()));
				stmt.setString(2, footer.getTipoRegistro());
				stmt.setString(3, footer.getIdentificador());
				stmt.setString(4, footer.getCodigoOS());
				stmt.setBigDecimal(5, new BigDecimal(footer
						.getCantidadRegistrosDE().toString()));
				stmt.setBigDecimal(6, new BigDecimal(footer
						.getCantidadRegistrosTO().toString()));
				stmt.setBigDecimal(7, new BigDecimal(footer
						.getCantidadRegistrosDT().toString()));
				stmt.setBigDecimal(8, new BigDecimal(footer
						.getCantidadRegistros().toString()));
				stmt.setBigDecimal(9, footer.getImporteSubsidio());
				stmt.setString(10, footer.getDebitoCredito());
				stmt.setBigDecimal(11, footer.getImporteSubsidioReal());
				stmt.executeUpdate();
			}
			logger.debug("archivo SO: footer listo");
			if (archivo.getDetalle() != null) {
				int cantGrabada = 0;
				while (cantGrabada < archivo.getDetalle().size()) {
					int falta = archivo.getDetalle().size() - cantGrabada;
					int hta = 0;
					if (falta > BATCH_UPDATES) {
						hta = cantGrabada + BATCH_UPDATES;
					} else {
						hta = archivo.getDetalle().size();
					}
					String sql = "INSERT INTO detalle_subsidio_os(fecha_proceso, tiporegistro, cuit, cuil, codigoos, periodo, "
							+ "remuneracionafectos,aportesos, contirbucionos, subsidio, obrasocialrel, indpartot,debitocredito, motivoexcepcion, "
							+ "capita, hombre0a14, hombre15a19,hombre50a64, hombre65a99, mujer0a14, mujer15a49, mujer50a64,mujer65a99)"
							+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
					stmt = con.prepareStatement(sql.toString());
					for (int i = cantGrabada; i < hta; i++) {
						DetalleSubsidioOS det = archivo.getDetalle().get(i);
						stmt.setDate(1, new java.sql.Date(archivo.getHeader()
								.getFechaProceso().getTime()));
						stmt.setString(2, det.getTipoRegistro());
						stmt.setString(3, det.getCuit());
						stmt.setString(4, det.getCuil());
						stmt.setString(5, det.getCodigoOS());
						stmt.setDate(6, new java.sql.Date(det.getPeriodo()
								.getTime()));
						stmt.setBigDecimal(7, det.getRemuneracionAfectOS());
						stmt.setBigDecimal(8, det.getAportesOS());
						stmt.setBigDecimal(9, det.getContirbucionOS());
						stmt.setBigDecimal(10, det.getSubsidio());
						stmt.setString(11, det.getObraSocialRel());
						stmt.setString(12, det.getIndParTot());
						stmt.setString(13, det.getDebitoCredito());
						stmt.setString(14, det.getMotivoExcepcion());
						stmt.setBigDecimal(15, det.getCapita());
						stmt.setString(16, det.getHombre0a14());
						stmt.setString(17, det.getHombre15a49());
						stmt.setString(18, det.getHombre50a64());
						stmt.setString(19, det.getHombre65a99());
						stmt.setString(20, det.getMujer0a14());
						stmt.setString(21, det.getMujer15a49());
						stmt.setString(22, det.getMujer50a64());
						stmt.setString(23, det.getMujer65a99());
						stmt.addBatch();
						cantGrabada++;
					}
					stmt.executeBatch();
				}
			}
			logger.debug("archivo SO: detalles listos");
			con.commit();
			logger.debug("archivo SO: commiteado");
		} catch (SQLException e) {
			logger.debug("Error al insertar archivo SO " + archivo.getFooter(),e);
			ConnectionHelper.rollback(con);
			throw e;
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
	}

	public int grabaArchivo(ArchivoDesempleo archivo) throws SQLException, RendicionBancoNacionRegistroDuplicado {
		int result = 0;
		Connection con = null;
		PreparedStatement stmt = null;
		try {
			logger.debug("archivo Desempleo: comienzo a grabar");
			con = ConnectionHelper.getReportesOspimConnection();
			con.setAutoCommit(false);

			if (archivo.getDetalleDesempleo() != null) {
				int cantGrabada = 0;
				while (cantGrabada < archivo.getDetalleDesempleo().size()) {
					int falta = archivo.getDetalleDesempleo().size()
							- cantGrabada;
					int hta = 0;
					if (falta > BATCH_UPDATES) {
						hta = cantGrabada + BATCH_UPDATES;
					} else {
						hta = archivo.getDetalleDesempleo().size();
					}
					String sql = "INSERT INTO detalle_desempleo_anses(clave, finpago, cod_paren, tipo_doc,"
							+ " nro_doc, prov_emi, cuil,fecha_nac, ape_nombre, fecha_vig, sexo, fecha_ini_rel,"
							+ " fecha_cese_rel,cod_os, "
							+ "fecha_proceso, cuil_titular)"
							+ "VALUES (?, ?, ?, ?, ?, ?, ?,?, ?, ?, ?, ?, ?,?, ?, ?)";
					stmt = con.prepareStatement(sql.toString());
					for (int i = cantGrabada; i < hta; i++) {
						DetalleDesempleo det = archivo.getDetalleDesempleo()
								.get(i);
						stmt.setString(1, det.getClaveDesempleo());
						stmt.setString(2, det.getFinPago());
						stmt.setInt(3, det.getCodParen());
						stmt.setInt(4, det.getTipoDoc());
						stmt.setInt(5, det.getNroDoc());
						stmt.setString(6, det.getProvEmi());
						stmt.setString(7, det.getCuil());
						stmt.setDate(8, new java.sql.Date(det.getFechaNac()
								.getTime()));
						stmt.setString(9, det.getApeNombre());
						stmt.setDate(10, new java.sql.Date(det.getFechaVig()
								.getTime()));
						stmt.setString(11, det.getSexo());
						if (null != det.getFechaIniRel()) {
							stmt.setDate(12, new java.sql.Date(det
									.getFechaIniRel().getTime()));
						} else {
							stmt.setNull(12, Types.DATE);
						}
						stmt.setDate(13, new java.sql.Date(det.getFechaCese()
								.getTime()));
						stmt.setInt(14, det.getCodOS());
						stmt.setDate(15, new java.sql.Date(det
								.getFechaProceso().getTime()));
						stmt.setString(16, det.getCuilTitular());
						stmt.addBatch();
						cantGrabada++;
					}
					stmt.executeBatch();
				}
			}
			logger.debug("archivo DESEMPLEO: detalles listos");
			con.commit();
			logger.debug("archivo DESEMPLEO: commiteado");
		} catch (SQLException e) {
			logger.debug("Error al insertar archivo DESEMPLEO ");
			ConnectionHelper.rollback(con);
			if (e.getNextException().getSQLState().equals("23505")) {
				throw new RendicionBancoNacionRegistroDuplicado();
//			}
//			if (e.getNextException().getClass().equals(PSQLException.class)){
//				throw new PeriodoArchivoDuplicadoException();
			}else{
				throw e;
			}
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}

		return result;
	}

	public int grabaArchivo(ArchivoExtraccionBancaria archivo)
			throws SQLException {
		int result = 0;
		Connection con = null;
		PreparedStatement stmt = null;
		try {
			logger.debug("archivo extr bcria: comienzo a grabar");
			con = ConnectionHelper.getReportesOspimConnection();
			con.setAutoCommit(false);

			if (archivo.getDetalleList() != null
					&& archivo.getDetalleList().size() > 0) {
				String sql1 = "INSERT INTO extraccion_bancaria(fecha, tipo, codigo_os)"
						+ "VALUES (?, ?, ?)";
				stmt = con.prepareStatement(sql1.toString());

				stmt.setDate(1, new java.sql.Date(archivo
						.getHeaderExtraccionBancaria().getFecha().getTime()));
				stmt.setString(2, archivo.getHeaderExtraccionBancaria()
						.getTipo());
				stmt.setString(3, archivo.getHeaderExtraccionBancaria()
						.getCodigoOS());
				stmt.execute();

				int cantGrabada = 0;
				while (cantGrabada < archivo.getDetalleList().size()) {
					int falta = archivo.getDetalleList().size() - cantGrabada;
					int hta = 0;
					if (falta > BATCH_UPDATES) {
						hta = cantGrabada + BATCH_UPDATES;
					} else {
						hta = archivo.getDetalleList().size();
					}
					String sql = "INSERT INTO detalle_extraccion_bancaria(fecha, tipo, codigo_os_header, codigo_os, debito_credito, codigo_movimiento, importe, importe_rechazado)"
							+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
					stmt = con.prepareStatement(sql.toString());
					for (int i = cantGrabada; i < hta; i++) {
						DetalleExtraccionBancaria det = archivo
								.getDetalleList().get(i);
						stmt.setDate(1, new java.sql.Date(archivo
								.getHeaderExtraccionBancaria().getFecha()
								.getTime()));
						stmt.setString(2, archivo.getHeaderExtraccionBancaria()
								.getTipo());
						stmt.setString(3, archivo.getHeaderExtraccionBancaria()
								.getCodigoOS());
						stmt.setString(4, det.getCodigoOS());
						stmt.setString(5, det.getDebitoCredito());
						stmt.setString(6, det.getCodigoMovimiento());
						stmt.setBigDecimal(7, det.getImporte());
						stmt.setBigDecimal(8, det.getImporteRechazado());
						stmt.addBatch();
						cantGrabada++;
					}
					stmt.executeBatch();
				}
			}
			logger.debug("archivo extraccion bancaria: detalles listos");
			con.commit();
			logger.debug("archivo extraccion bancaria: commiteado");
		} catch (SQLException e) {
			logger.debug("Error al insertar archivo ext bcria ", e);
			ConnectionHelper.rollback(con);
			throw e;
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}

		return result;
	}

	public int grabaArchivo(ArchivoTransferenciaExterna archivo)
			throws SQLException {
		int result = 0;
		Connection con = null;
		PreparedStatement stmt = null;
		PreparedStatement stmtFacturas = null;
		try {
			logger.debug("archivo transferencia ext: comienzo a grabar");
			con = ConnectionHelper.getReportesOspimConnection();
			con.setAutoCommit(false);

			if (archivo.getDetalleList() != null
					&& archivo.getDetalleList().size() > 0) {
				String sql1 = "INSERT INTO transferencia_externa(fecha_proceso, codigo_organismo, debito_credito)"
						+ "VALUES (?, ?, ?)";
				stmt = con.prepareStatement(sql1.toString());

				stmt.setDate(1, new java.sql.Date(archivo.getFooter()
						.getFechaProceso().getTime()));
				stmt.setString(2, archivo.getFooter().getCodigoOrganismo());
				stmt.setString(3, archivo.getFooter().getDebitoCredito());
				stmt.execute();

				int cantGrabada = 0;
				while (cantGrabada < archivo.getDetalleList().size()) {
					int falta = archivo.getDetalleList().size() - cantGrabada;
					int hta = 0;
					if (falta > BATCH_UPDATES) {
						hta = cantGrabada + BATCH_UPDATES;
					} else {
						hta = archivo.getDetalleList().size();
					}
					String sql = "INSERT INTO detalle_transferencia_externa(fecha_proceso_transf, codigo_organismo_transf, debito_credito_transf,"
							+ "codigo_organismo, numero_expediente, fecha_proceso, fecha_transferencia, clasif_expediente,  importe_total, nro_cuota,"
							+ "importe_transferencia,debito_credito, nro_expediente_original, codigo_htal, nro_expediente_anssal, observacion, detalle_juzgado,"
							+ "detalle_secretaria, autos )"
							+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
					stmt = con.prepareStatement(sql.toString());
					String sqlFacturas = "INSERT INTO detalle_transferencia_externa_factura(fecha_proceso_transf , codigo_organismo_transf ,"
							+ " debito_credito_transf , numero_expediente, debito_credito, numero_factura )"
							+ "VALUES (?, ?, ?, ?, ?, ?)";
					stmtFacturas = con.prepareStatement(sqlFacturas.toString());
					boolean executeFacturasBatch = false;
					for (int i = cantGrabada; i < hta; i++) {
						DetalleTransferenciaExterna det = archivo
								.getDetalleList().get(i);
						stmt.setDate(1, new java.sql.Date(archivo.getFooter()
								.getFechaProceso().getTime()));
						stmt.setString(2, archivo.getFooter()
								.getCodigoOrganismo());
						stmt.setString(3, archivo.getFooter()
								.getDebitoCredito());
						stmt.setString(4, det.getCodigoOrganismo());
						stmt.setString(5, det.getNroExpediente());
						stmt.setDate(6, new java.sql.Date(det.getFechaProceso()
								.getTime()));
						stmt.setDate(7, new java.sql.Date(det
								.getFechaTransferencia().getTime()));
						stmt.setInt(8, det.getClasificacionExpediente());
						stmt.setBigDecimal(9, det.getImporteTotal());
						stmt.setInt(10, det.getNroCuota());
						stmt.setBigDecimal(11, det.getImporteTransferencia());
						stmt.setString(12, det.getDebitoCredito());
						stmt.setString(13, det.getNroExpedienteOriginal());
						stmt.setString(14, det.getCodigoHospital());
						stmt.setString(15, det.getNroExpedienteAnssal());
						stmt.setString(16, det.getObservacion());
						stmt.setString(17, det.getDetalleJuzgado());
						stmt.setString(18, det.getDetalleSecretaria());
						stmt.setString(19, det.getAutos());
						stmt.addBatch();

						if (det.getFacturas() != null
								&& det.getFacturas().size() > 0) {
							for (String nro : det.getFacturas()) {
								stmtFacturas.setDate(1, new java.sql.Date(
										archivo.getFooter().getFechaProceso()
												.getTime()));
								stmtFacturas.setString(2, archivo.getFooter()
										.getCodigoOrganismo());
								stmtFacturas.setString(3, archivo.getFooter()
										.getDebitoCredito());
								stmtFacturas.setString(4,
										det.getNroExpediente());
								stmtFacturas.setString(5,
										det.getDebitoCredito());
								stmtFacturas.setString(6, nro);
								stmtFacturas.addBatch();
								executeFacturasBatch = true;
							}
						}
						cantGrabada++;
					}
					stmt.executeBatch();
					if (executeFacturasBatch) {
						stmtFacturas.executeBatch();
					}
				}
			}
			logger.debug("archivo transferencia externa: detalles listos");
			con.commit();
			logger.debug("archivo transferencia externa: commiteado");
		} catch (SQLException e) {
			logger.debug("Error al insertar archivo transferencia externa ", e);
			ConnectionHelper.rollback(con);
			throw e;
		} finally {
			ConnectionHelper.cerrar(stmtFacturas);
			ConnectionHelper.cerrar(stmt, con);
		}

		return result;

	}

	private Connection getConnectionFromJavaApplication() {
		try {
			Class.forName("org.postgresql.Driver");
			return DriverManager.getConnection(
					"jdbc:postgresql://10.1.1.28:5432/devmolineros",
					"postgres", "barracud4");
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) { // TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static boolean buscaPeriodoProcesado ( String nombreTabla, Date fechaArchivo  )
	{
		
		Connection con = null;
		CallableStatement stmt = null;
		boolean resp= false;
		try {
			String sql = "{call buscar_periodo_procesado_archivos (?,?)}";
			con = ConnectionHelper.getConnection();
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, nombreTabla);
			stmt.setDate(2, new java.sql.Date(fechaArchivo.getTime()) );  
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				logger.debug("hay datos");
				resp=true;
			}
		} catch (Exception e) {
			logger.error("Error al buscar prestaciones asociadas del reclamo", e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
				
		return resp; 
	}
	
		
	public Map<String, String> obtieneAfiliadosSinCuil() {
		System.out.print("obtieneAfiliados");
		Connection con = null;
		PreparedStatement stmt = null;
		con = getConnectionFromJavaApplication();
		System.out.print("con cnx!");
		String sql = "select docu_numero,sexo from afiliado a where cuil is null and (a.baja_fecha is null or a.baja_fecha>'20101201')";
		Map<String, String> cuils = new HashMap<String, String>();
		try {
			stmt = con.prepareStatement(sql.toString());
			ResultSet result = stmt.executeQuery();
			while (result.next()) {
				cuils.put(result.getString("docu_numero"),
						result.getString("sexo"));
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return cuils;
	}

	public void updateAfiliadosSinCuil(Map<String, String> sinCuil) {
		Connection con = null;
		PreparedStatement stmt = null;
		con = getConnectionFromJavaApplication();

		ArrayList<String> documentos = new ArrayList<String>();
		documentos.addAll(sinCuil.keySet());

		for (String doc : documentos) {

			String sql = "update afiliado a set cuil='"
					+ sinCuil.get(doc)
					+ "' where cuil is null and (a.baja_fecha is null or a.baja_fecha>'20101201') and "
					+ "docu_numero='" + doc + "'";
			System.out.println(sql);

			ConnectionHelper.cerrar(stmt, con);
		}

	}

	@Deprecated
	public int grabaArchivoOpciones(OpcionesSS archivo) throws SQLException {

		int result = 0;
		Connection con = null;
		CallableStatement stmt = null;
		
		try {
			logger.debug("archivo transferencia ext: comienzo a grabar");
			con = ConnectionHelper.getReportesOspimConnection();
			con.setAutoCommit(false);

			if (archivo.getDetalle() != null && archivo.getDetalle().size() > 0) {

				int cantGrabada = 0;
				while (cantGrabada < archivo.getDetalle().size()) {
					int falta = archivo.getDetalle().size() - cantGrabada;
					int hta = 0;
					if (falta > BATCH_UPDATES) {
						hta = cantGrabada + BATCH_UPDATES;
					} else {
						hta = archivo.getDetalle().size();
					}

					String sql = "{call insertar_opcionsss_viejo(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) }";

					stmt = con.prepareCall(sql.toString());
					
					for (int i = cantGrabada; i < hta; i++) {
						DetalleOpcionesSS det = archivo.getDetalle().get(i);
						stmt.setString(1, det.getTipoExportacion());
						stmt.setString(2, det.getDelegacion());
						stmt.setInt(3, det.getLibro());
						stmt.setInt(4, det.getTomo());
						stmt.setInt(5, det.getNroFormulario());
						stmt.setInt(6, det.getOsElegida());
						stmt.setString(7, det.getRegimen());
						stmt.setString(8, det.getCuil());
						stmt.setString(9, det.getApeNom());
						stmt.setString(10, det.getSexo());
						stmt.setString(11, det.getCalle());
						stmt.setString(12, det.getNumero());
						stmt.setInt(13, det.getPiso());
						if(!StringUtils.checkEmpty(det.getDepartamento())){
							stmt.setString(14, det.getDepartamento() );
						}else{
							stmt.setNull(14, Types.VARCHAR);
						}
						stmt.setString(15, det.getLocalidad());
						stmt.setString(16, det.getTelParticular());
						stmt.setString(17, det.getTelLaboral());
						stmt.setString(18, det.getTelCelular());
						stmt.setString(19, det.getEmail());
						stmt.setInt(20, det.getOsAnterior());
						stmt.setString(21, det.getCuit());
						stmt.setString(22, det.getUnificaApo());
						if (det.getFechaElecc() != null) {
							stmt.setDate(23, new java.sql.Date(det
									.getFechaElecc().getTime()));
						} else {
							stmt.setNull(23, Types.DATE);
						}
						if (det.getFechaCerti() != null) {
							stmt.setDate(24, new java.sql.Date(det
									.getFechaCerti().getTime()));
						} else {
							stmt.setNull(24, Types.DATE);
						}
						stmt.setString(25, det.getCuilConyuge());
						stmt.setString(26, det.getApeNomConyuge());
						if (det.getFechaBaja() != null) {
							stmt.setDate(27, new java.sql.Date(det
									.getFechaBaja().getTime()));
						} else {
							stmt.setNull(27, Types.DATE);
						}
						if (det.getFechaEntrega() != null) {
							stmt.setDate(28, new java.sql.Date(det
									.getFechaEntrega().getTime()));
						} else {
							stmt.setNull(28, Types.DATE);
						}
						stmt.setInt(29, det.getNumeroLote());
						stmt.setString(30, det.getVersionSistema());
						stmt.setString(31, det.getCod_postal());
						stmt.setBoolean(32, true);
						stmt.addBatch();
						cantGrabada++;
					}
					stmt.executeBatch();

				}
			}
			logger.debug("archivo transferencia externa: detalles listos");
			con.commit();
			logger.debug("archivo transferencia externa: commiteado");
		} catch (SQLException e) {
			logger.debug("Error al insertar archivo transferencia externa ", e);
			ConnectionHelper.rollback(con);
			throw e;
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}

		return result;
	}

	public int actualizaAfiOpcionesDesdeArchivoSSS(OpcionesSS archivo, Date fechaArchivo, String user, String tipoOrigen) throws SQLException, PeriodoArchivoDuplicadoException {

		int result = 0;
		Connection con = null;
		PreparedStatement stmt = null, stmt1 = null;

		try {
			logger.debug("archivo transferencia ext: comienzo a actualizar Afi_opciones_sss");
			con = ConnectionHelper.getReportesOspimConnection();
			con.setAutoCommit(false);

			if (archivo.getDetalle() != null && archivo.getDetalle().size() > 0) {

				int cantGrabada = 0;
				while (cantGrabada < archivo.getDetalle().size()) {
					int falta = archivo.getDetalle().size() - cantGrabada;
					int hta = 0;
					if (falta > BATCH_UPDATES) {
						hta = cantGrabada + BATCH_UPDATES;
					} else {
						hta = archivo.getDetalle().size();
					}

					String sql = "{call novedades_sss.actualiza_respuesta_opciones_sss(?,?,?) }" ;
					
					stmt = con.prepareCall(sql.toString());

					for (int i = cantGrabada; i < hta; i++) {
						DetalleOpcionesSS det = archivo.getDetalle().get(i);
						stmt.setString(1, det.getCuil());
						stmt.setInt(2, det.getNroFormulario());
						stmt.setString(3, user);
						
						stmt.addBatch();

						cantGrabada++;
					}
					stmt.executeBatch();

				}
				
				String queryArch = "{call novedades_sss.inserta_archivos_novedades(?, ?, ?, ?) }";
	    		
				stmt1 = con.prepareCall(queryArch.toString());
				
				stmt1.setDate(1, new java.sql.Date(fechaArchivo.getTime()) );
				stmt1.setString(2, tipoOrigen);
				stmt1.setInt(3, cantGrabada);
				stmt1.setString(4, user);
				stmt1.executeQuery();
				
			}
			logger.debug("archivo transferencia externa: detalles listos");
			con.commit();
			logger.debug("archivo transferencia externa: commiteado");
		} catch (SQLException e) {
			logger.debug("Error al actualizando archivo transferencia externa ", e);
			ConnectionHelper.rollback(con);
			if(e.getMessage().contains("duplicate key value violates unique constraint") ){
//				org.postgresql.util.PSQLException: ERROR: duplicate key value violates unique constraint "FECHA_DESC_PK"
//				  Detail: Key (fecha_archivo, descripcion)=(2014-05-01, NOVEDADES) already exists.
				
				throw new PeriodoArchivoDuplicadoException();
			}else{
				throw e;
			}
		} finally {
			ConnectionHelper.cerrar(stmt);
			ConnectionHelper.cerrar(stmt1, con);
		}

		return result;
	}
	
	public static void insertarOpcionSS(DetalleOpcionesSS det) throws SQLException{
		
		Connection con = null;
		CallableStatement stmt = null;

		try {
			con = ConnectionHelper.getConnectionFromJavaApplication() ;
			String sql = "INSERT INTO afi_opciones_sss(tipo_exportacion, delegacion, libro, tomo, nro_formulario, os_elegida,"
					+ "regimen, cuil, ape_nom, sexo, calle, numero, piso, departamento, localidad, telefono_particular, telefono_laboral,"
					+ "telefono_celular,email, os_anterior, cuit, unifica_apo, fecha_elecc, fecha_certi, cuil_conyuge, ape_nom_conyuge,"
					+ "fecha_baja, fecha_entrega, numero_lote, version_sistema, postal_codi) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,"
					+ "?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?, ?, ?, ?, ?, ?, ?)";
			
			stmt = con.prepareCall(sql.toString());
			
			stmt.setString(1, det.getTipoExportacion());
			stmt.setString(2, det.getDelegacion());
			stmt.setInt(3, det.getLibro());
			stmt.setInt(4, det.getTomo());
			stmt.setInt(5, det.getNroFormulario());
			stmt.setInt(6, det.getOsElegida());
			stmt.setString(7, det.getRegimen());
			stmt.setString(8, det.getCuil());
			stmt.setString(9, det.getApeNom());
			stmt.setString(10, det.getSexo());
			stmt.setString(11, det.getCalle());
			stmt.setString(12, det.getNumero());
			stmt.setInt(13, det.getPiso());
			stmt.setString(14, det.getDepartamento());
			stmt.setString(15, det.getLocalidad());
			stmt.setString(16, det.getTelParticular());
			stmt.setString(17, det.getTelLaboral());
			stmt.setString(18, det.getTelCelular());
			stmt.setString(19, det.getEmail());
			stmt.setInt(20, det.getOsAnterior());
			stmt.setString(21, det.getCuit());
			stmt.setString(22, det.getUnificaApo());
			if (det.getFechaElecc() != null) {
				stmt.setDate(23, new java.sql.Date(det.getFechaElecc().getTime()));
			} else {
				stmt.setNull(23, Types.DATE);
			}
			if (det.getFechaCerti() != null) {
				stmt.setDate(24, new java.sql.Date(det.getFechaCerti().getTime()));
			} else {
				stmt.setNull(24, Types.DATE);
			}
			stmt.setString(25, det.getCuilConyuge());
			stmt.setString(26, det.getApeNomConyuge());
			if (det.getFechaBaja() != null) {
				stmt.setDate(27, new java.sql.Date(det.getFechaBaja().getTime()));
			} else {
				stmt.setNull(27, Types.DATE);
			}
			if (det.getFechaEntrega() != null) {
				stmt.setDate(28, new java.sql.Date(det.getFechaEntrega().getTime()));
			} else {
				stmt.setNull(28, Types.DATE);
			}
			stmt.setInt(29, det.getNumeroLote());
			stmt.setString(30, det.getVersionSistema());
			stmt.setString(31, det.getCod_postal());
			
			stmt.executeUpdate();

		} catch (SQLException e) {
			logger.error(e);
		} catch (Exception e) {
			logger.error(e);
		} finally {
			ConnectionHelper.cerrar(stmt,con);
		}
		
	}
	
	public int grabaArchivoBajaOpciones(BajaOpcionesSS archivo)
			throws SQLException {

		int result = 0;
		Connection con = null;
		PreparedStatement stmt = null;

		try {
			logger.debug("archivo transferencia ext: comienzo a grabar");
			con = ConnectionHelper.getReportesOspimConnection();
			con.setAutoCommit(false);

			if (archivo.getDetalle() != null && archivo.getDetalle().size() > 0) {

				int cantGrabada = 0;
				while (cantGrabada < archivo.getDetalle().size()) {
					int falta = archivo.getDetalle().size() - cantGrabada;
					int hta = 0;
					if (falta > BATCH_UPDATES) {
						hta = cantGrabada + BATCH_UPDATES;
					} else {
						hta = archivo.getDetalle().size();
					}
					String sql = "INSERT INTO afi_baja_opcionesss(tipo_exportacion, nroformulario, cuil, apenom, calle, numero,"
							+ "piso, departamento, telparticular, localidad, codpostal, provincia,cuit, razonsoc, nose, fechaelecc, osselecc)"
							+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?, ?, ?, ?, ?);";
					stmt = con.prepareStatement(sql.toString());

					for (int i = cantGrabada; i < hta; i++) {
						DetalleBajasOpcionesSS det = archivo.getDetalle()
								.get(i);
						stmt.setString(1, det.getTipoExportacion());
						stmt.setInt(2, det.getNroFormulario());
						stmt.setString(3, det.getCuil());
						stmt.setString(4, det.getApeNom());
						stmt.setString(5, det.getCalle());
						stmt.setString(6, det.getNumero());
						stmt.setString(7, det.getPiso());
						stmt.setString(8, det.getDepartamento());
						stmt.setString(9, det.getTelParticular());
						stmt.setString(10, det.getLocalidad());
						stmt.setString(11, det.getCodPostal());
						stmt.setString(12, det.getProvincia());
						stmt.setString(13, det.getCuit());
						stmt.setString(14, det.getRazonSoc());
						stmt.setString(15, det.getNoSe());
						if (det.getFechaElecc() != null) {
							stmt.setDate(16, new java.sql.Date(det
									.getFechaElecc().getTime()));
						} else {
							stmt.setNull(16, Types.DATE);
						}
						stmt.setInt(17, det.getOsSelecci());

						stmt.addBatch();

						cantGrabada++;
					}
					stmt.executeBatch();

				}
			}
			logger.debug("archivo transferencia externa: detalles listos");
			con.commit();
			logger.debug("archivo transferencia externa: commiteado");
		} catch (SQLException e) {
			logger.debug("Error al insertar archivo transferencia externa ", e);
			ConnectionHelper.rollback(con);
			throw e;
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}

		return result;
	}

	public int grabaArchivoPadronEmpresa(ArrayList<Empresa> archivo)
			throws SQLException {

		int result = 0;
		Connection con = null;
		PreparedStatement stmt = null;

		try {
			logger.debug("archivo transferencia ext: comienzo a grabar");
			con = ConnectionHelper.getReportesOspimConnection();
			con.setAutoCommit(false);

			if (archivo.size() > 0) {
				String sql = "INSERT INTO empresa_aux(cuit, sucursal, razon_soc, nombre_fantasia, vigen_fecha, alta_fecha, alta_usr, modi_fecha, modi_usr,"
						+ "afip, imp_ganancias, imp_iva, monotributo, integrante_soc, empleador)"
						+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
				stmt = con.prepareStatement(sql.toString());
				for (Empresa e : archivo) {
					stmt.setString(1, e.getCuit());
					stmt.setString(2, "000");
					stmt.setString(3, e.getRazon_soc());
					stmt.setString(4, e.getRazon_soc());
					stmt.setDate(5,
							new java.sql.Date(System.currentTimeMillis()));
					stmt.setDate(6,
							new java.sql.Date(System.currentTimeMillis()));
					stmt.setString(7, "admin");
					stmt.setDate(8,
							new java.sql.Date(System.currentTimeMillis()));
					stmt.setString(9, "admin");
					stmt.setBoolean(10, true);
					stmt.setString(11, e.getImpGanancias());
					stmt.setString(12, e.getImpIva());
					stmt.setString(13, e.getMonotributo());
					stmt.setString(14, e.getIntegranteSoc());
					stmt.setString(15, e.getEmpleador());
					stmt.addBatch();
				}
				stmt.executeBatch();

			}
			logger.debug("archivo empresa: detalles listos");
			con.commit();
			logger.debug("archivo empresa: commiteado");
		} catch (SQLException e) {
			logger.debug("Error al insertar archivo empresa", e);
			ConnectionHelper.rollback(con);
			throw e;
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}

		return result;
	}

	public int grabaArchivoRendicionNacion(ListadoRendicionNacion archivo,
			int tipo, Connection connectionParameter, boolean empleadores) throws SQLException,
			RendicionBancoNacionRegistroDuplicado {

		int result = 0;		
		PreparedStatement stmt = null;
		Connection con = null;
		try {
			logger.debug("archivo rendicion: comienzo a grabar");
			
			if(connectionParameter == null){
				con = ConnectionHelper.getConnectionForTransaction();
			}else{
				con = connectionParameter;
				if(con.getAutoCommit()) {
					con.setAutoCommit(false);
				}
				logger.debug(con.getMetaData().getURL());
			}
			
			if (archivo.getDetalle() != null && archivo.getDetalle().size() > 0) {

				int cantGrabada = 0;
				while (cantGrabada < archivo.getDetalle().size()) {
					int falta = archivo.getDetalle().size() - cantGrabada;
					int hta = 0;
					if (falta > BATCH_UPDATES) {
						hta = cantGrabada + BATCH_UPDATES;
					} else {
						hta = archivo.getDetalle().size();
					}
					String sql = null;
					if (tipo == 5652) {
						sql = "INSERT INTO amtima_aportes(ente, suc_nacion, suc_bcra, fecha_recauda, fecha_rendicion, cod_movimiento, nro_movimiento, "
								+ "importe, cod_barras, banco_cheque, sucursal_cheque, nro_cheque, estado_cheque, cuit, periodo_cod_barras, nro_dec_portal_emple,"
								+ "nro_boleta_portal_emple, tipo_boleta, fecha_proceso) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
					} else {
						if(empleadores){
							sql = "INSERT INTO uoma_aportes(ente, suc_nacion, suc_bcra, fecha_recauda, fecha_rendicion, cod_movimiento, nro_movimiento, "
									+ "importe, cod_barras, banco_cheque, sucursal_cheque, nro_cheque, estado_cheque, cuit, periodo_cod_barras, nro_dec_portal_emple,"
									+ "nro_boleta_portal_emple, tipo_boleta, fecha_proceso) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
							
						}else{
							sql = "INSERT INTO uoma.uoma_aportes(ente, suc_nacion, suc_bcra, fecha_recauda, fecha_rendicion, cod_movimiento, nro_movimiento, "
									+ "importe, cod_barras, banco_cheque, sucursal_cheque, nro_cheque, estado_cheque, cuit, periodo_cod_barras, nro_dec_portal_emple,"
									+ "nro_boleta_portal_emple, tipo_boleta, fecha_proceso) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";							
						}
						
					}
					stmt = con.prepareStatement(sql.toString());

					for (int i = cantGrabada; i < hta; i++) {
						RendicionNacion det = archivo.getDetalle().get(i);
						stmt.setInt(1, det.getEnte().intValue());
						stmt.setInt(2, det.getSuc_origen());
						stmt.setInt(3, det.getSuc_bcra());
						stmt.setDate(4, new java.sql.Date(det
								.getFecha_recauda().getTime()));
						stmt.setDate(5, new java.sql.Date(det
								.getFecha_rendicion().getTime()));
						stmt.setString(6, det.getCod_movimiento());
						stmt.setInt(7, det.getNro_movimiento());
						stmt.setBigDecimal(8, det.getImporte());
						stmt.setString(9, det.getCod_barras());
						stmt.setInt(10, det.getBco_cheque());
						stmt.setInt(11, det.getSuc_cheque());
						stmt.setInt(12, det.getNro_cheque());
						stmt.setString(13, det.getEstado_cheque());
						stmt.setString(14, det.getCuit());
						stmt.setDate(15, new java.sql.Date(det
								.getPeriodo_cod_barras().getTime()));
						stmt.setInt(16, det.getNro_dec_portal_emple());
						stmt.setInt(17, det.getNro_boleta_portal_emple());
						stmt.setInt(18, det.getTipo_boleta());
						stmt.setDate(19,
								new java.sql.Date(System.currentTimeMillis()));
						stmt.addBatch();

						cantGrabada++;
					}
					stmt.executeBatch();

				}
			}
			logger.debug("archivo rendicion: detalles listos");
			con.commit();
			logger.debug("archivo rendicion: commiteado");
		} catch (SQLException e) {
			if (e.getNextException().getSQLState().equals("23505")) {
				throw new RendicionBancoNacionRegistroDuplicado();
			}
			logger.debug("Error al insertar archivo rendicion ", e);
			ConnectionHelper.rollback(con);
			
		} finally {
			if(connectionParameter==null){
				ConnectionHelper.cerrar(stmt,con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}

		return result;
	}

	public int actualizaSuperErrores(List<DetalleOpcionesSS> archivo)
			throws SQLException, RendicionBancoNacionRegistroDuplicado {

		int result = 0;
		Connection con = null;
		CallableStatement stmt = null;

		try {
			logger.debug("archivo rendicion: comienzo a grabar");
			con = ConnectionHelper.getReportesOspimConnection();
			con.setAutoCommit(false);

			if (archivo != null && archivo.size() > 0) {

				int cantGrabada = 0;
				while (cantGrabada < archivo.size()) {
					int falta = archivo.size() - cantGrabada;
					int hta = 0;
					if (falta > BATCH_UPDATES) {
						hta = cantGrabada + BATCH_UPDATES;
					} else {
						hta = archivo.size();
					}
					String sql = null;

					sql = "{call informes.actualiza_error_super(?)}";
					
					stmt = con.prepareCall(sql.toString());

					for (int i = cantGrabada; i < hta; i++) {
						DetalleOpcionesSS det = archivo.get(i);
						try{
							cantGrabada++;
							int docu = Integer.parseInt(det.getDocu_numero());

							stmt.setString(1, String.valueOf(docu));
							stmt.addBatch();
							
						} catch (NumberFormatException pe) {

						}
					}

					stmt.executeBatch();

				}
			}
			logger.debug("archivo error ss: detalles listos");
			con.commit();
			logger.debug("archivo error ss: commiteado");
		} catch (SQLException e) {
			logger.debug("Error al insertar archivo error ss", e);
			ConnectionHelper.rollback(con);
			throw e;
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}

		return result;
	}

	public int grabaArchivoSubsidios(ArchivoSubsidioMitigacionAsimetricas archivo) throws SQLException {
     	
		int result = 0;
		Connection con = null;
		PreparedStatement stmt = null;
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdfPeriodo = new SimpleDateFormat("yyyyMM");
		
		HeaderSumaXxxx header =	archivo.getHeader();
		DetalleSumaXxxx detalle = archivo.getDetalle();
//		FooterSumaXxxx footer = archivo.getFooter();
		
		String[] horaProcAfip = header.getHoraEnvioAFIP().substring(0, 5).split(":");
		cal.setTime(header.getFechaEnvioAFIP());
		cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(horaProcAfip[0]));
		cal.set(Calendar.MINUTE, Integer.parseInt(horaProcAfip[1]));		
		
		
		try {
				
			Date fechaPeriodo = sdfPeriodo.parse(detalle.getPeriodo());
			
			logger.debug("Comienzo a grabar archivo Subsidios");
			con = ConnectionHelper.getReportesOspimConnection();

			con.setAutoCommit(false);

			String queryDetalle = "INSERT INTO detalle_subsidio_os( "+
		            "fecha_proceso, tiporegistro, codigoos, periodo, subsidio, debitocredito) "+
				    "VALUES (?, ?, ?, ?, ?, ?);";
				            		
			stmt = con.prepareStatement(queryDetalle.toString());

			stmt.setDate(1, new java.sql.Date(cal.getTimeInMillis()));
			stmt.setString(2, header.getTipoRegistro());
			stmt.setString(3, detalle.getCodigoOOSS());
			stmt.setDate(4, new java.sql.Date(fechaPeriodo.getTime())) ;
			stmt.setBigDecimal(5, detalle.getTotalSubsidio());
			stmt.setString(6, "C");
			
			result = stmt.executeUpdate();
			
			String queryFooter = "INSERT INTO footer_subsidio_os( "+
		           "fecha_proceso, tiporegistro, identificador, codigoos, cantidadregistros, "+ 
		           "importesubsidio, debitocredito, importesubsidioreal) "+
 				   "VALUES (?, ?, ?, ?, ?, ?, ?, ?);";
				            		
			stmt = con.prepareStatement(queryFooter.toString());

			stmt.setDate(1, new java.sql.Date(cal.getTimeInMillis()));
			stmt.setString(2, header.getTipoRegistro());
			stmt.setString(3, header.getTipoProceso());
			stmt.setString(4, detalle.getCodigoOOSS());
			stmt.setInt(5, detalle.getCantidadBeneficiarios());
			stmt.setBigDecimal(6, detalle.getTotalSubsidio());
			stmt.setString(7, "C");
			stmt.setBigDecimal(8, detalle.getTotalSubsidio());
			
			result = result * stmt.executeUpdate();
			
//			logger.debug("archivo Subsidio: Footer listo");
			con.commit();
			logger.debug("archivo Subsidio: commiteado");
		} catch (SQLException e) {
			logger.error("Error al insertar archivo de subsidios " + header.getTipoProceso(), e);
			ConnectionHelper.rollback(con);
			throw e;
		} catch (ParseException e) {
			logger.error(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return result;
	}

	public int grabaArchivoSubsidiosSuma(ArchivoSubsidioMitigacionAsimetricasSuma archivo) throws SQLException {
     	
		int result = 0;
		Connection con = null;
		PreparedStatement stmt = null;
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdfPeriodo = new SimpleDateFormat("yyyyMM");
		
		HeaderSumaXxxx header =	archivo.getHeader();
		DetalleSuma detalle = archivo.getDetalle();
//		FooterSumaXxxx footer = archivo.getFooter();
		
		String[] horaProcAfip = header.getHoraEnvioAFIP().substring(0, 5).split(":");
		cal.setTime(header.getFechaEnvioAFIP());
		cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(horaProcAfip[0]));
		cal.set(Calendar.MINUTE, Integer.parseInt(horaProcAfip[1]));		
		
		
		try {
				
			Date fechaPeriodo = sdfPeriodo.parse(detalle.getPeriodo());
			
			logger.debug("Comienzo a grabar archivo Subsidios Suma");
			con = ConnectionHelper.getReportesOspimConnection();

			con.setAutoCommit(false);

//			String queryDetalle = "INSERT INTO detalle_subsidio_os( "+
//		            "fecha_proceso, tiporegistro, codigoos, periodo, subsidio, debitocredito) "+
//				    "VALUES (?, ?, ?, ?, ?, ?);";
				            		
			String queryDetalle = "INSERT INTO detalle_subsidio_os( "+
		            "fecha_proceso, tiporegistro, codigoos, periodo, subsidio, "+ 
		            "debitocredito, capita, art3, art2inc_a, art2inc_b, art2inc_c) "+
		            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
			
			stmt = con.prepareStatement(queryDetalle.toString());

			stmt.setDate(1, new java.sql.Date(cal.getTimeInMillis()));
			stmt.setString(2, header.getTipoRegistro());
			stmt.setString(3, detalle.getCodigoOOSS());
			stmt.setDate(4, new java.sql.Date(fechaPeriodo.getTime())) ;
			stmt.setBigDecimal(5, detalle.getTotalSubsidio());
			stmt.setString(6, "C");
			stmt.setBigDecimal(7, detalle.getCapita());
			stmt.setBigDecimal(8, detalle.getArt3());
			stmt.setBigDecimal(9, detalle.getArt2incA());
			stmt.setBigDecimal(10, detalle.getArt2incB());
			stmt.setBigDecimal(11, detalle.getArt2incC());
			
			
			result = stmt.executeUpdate();
			
			String queryFooter = "INSERT INTO footer_subsidio_os( "+
		           "fecha_proceso, tiporegistro, identificador, codigoos, cantidadregistros, "+ 
		           "importesubsidio, debitocredito, importesubsidioreal) "+
 				   "VALUES (?, ?, ?, ?, ?, ?, ?, ?);";
				            		
			stmt = con.prepareStatement(queryFooter.toString());

			stmt.setDate(1, new java.sql.Date(cal.getTimeInMillis()));
			stmt.setString(2, header.getTipoRegistro());
			stmt.setString(3, header.getTipoProceso());
			stmt.setString(4, detalle.getCodigoOOSS());
			stmt.setInt(5, detalle.getCantidadBeneficiarios());
			stmt.setBigDecimal(6, detalle.getTotalSubsidio());
			stmt.setString(7, "C");
			stmt.setBigDecimal(8, detalle.getTotalSubsidio());
			
			result = result * stmt.executeUpdate();
			
//			logger.debug("archivo Subsidio: Footer listo");
			con.commit();
			logger.debug("archivo Subsidio: commiteado");
		} catch (SQLException e) {
			logger.error("Error al insertar archivo de subsidios " + header.getTipoProceso(), e);
			ConnectionHelper.rollback(con);
			throw e;
		} catch (ParseException e) {
			logger.error(e);
		} finally {
			ConnectionHelper.cerrar(stmt, con);
		}
		return result;
	}

	public int grabaArchivoNovedades(List<Novedad> novs, Date fechaArchivo, String user) throws SQLException, PeriodoArchivoDuplicadoException {
     	
		int result = 0, idProceso = 0;
		Connection con = null;
		CallableStatement stmt = null, stmt1 = null;
		String queryNov, queryArch;
		try {
			
			logger.debug("Comienzo a grabar archivo Novedades");
			con = ConnectionHelper.getReportesOspimConnection();

			con.setAutoCommit(false);

			queryArch = "{call novedades_sss.inserta_archivos_novedades(?, ?, ?, ?) }";
    		
			stmt1 = con.prepareCall(queryArch.toString());
			
			stmt1.setDate(1, new java.sql.Date(fechaArchivo.getTime()) );
			stmt1.setString(2, WebKeysAfiliados.TIPOS_ORIGEN[3]);
			stmt1.setInt(3, novs.size());
			stmt1.setString(4, user);
			
			ResultSet rs = stmt1.executeQuery();
			while (rs.next()) {
				idProceso =  rs.getInt(1);
			}
			
			for (Iterator<Novedad> iterator = novs.iterator(); iterator.hasNext();) {
				Novedad nov = iterator.next();
				
			
				queryNov = "{call novedades_sss.inserta_novedad(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?," +
															"?,?,?,?,?,?,?,?,?,?,?,?,?,?) }";
					            		
				stmt = con.prepareCall(queryNov.toString());

				stmt.setInt(1, idProceso);
				stmt.setInt(2, nov.getCodigo_ooss());
				stmt.setString(3, nov.getCuit_empleador());
				stmt.setString(4, nov.getCuil_titular());
				stmt.setInt(5, nov.getCodigo_parentesco());
				stmt.setString(6, nov.getCuil());
				stmt.setString(7, nov.getDocumento_tipo());
				stmt.setInt(8, nov.getDocumento_numero());
				stmt.setString(9, nov.getApellido_nombre());
				stmt.setString(10, nov.getSexo());
				stmt.setInt(11, nov.getEstado_civil());
				stmt.setInt(12, nov.getFecha_nacimiento());
				stmt.setInt(13, nov.getNacionalidad());
				stmt.setString(14, nov.getCalle());
				stmt.setString(15, nov.getNumero_puerta());
				stmt.setString(16, nov.getPiso());
				stmt.setString(17, nov.getDepartamento());
				stmt.setString(18, nov.getLocalidad());
				stmt.setString(19, nov.getCodigo_postal());
				stmt.setInt(20, nov.getProvincia());
				if(nov.getTipo_domicilio()==null){
					stmt.setNull(21, Types.INTEGER );
				}else{
					stmt.setInt(21, nov.getTipo_domicilio());
				}
				if(nov.getTelefono() == null){
					stmt.setNull(22, Types.VARCHAR);
				}else{
					stmt.setString(22, nov.getTelefono());					
				}
				stmt.setInt(23, nov.getSituacion_revista());
				stmt.setInt(24, nov.getIncapacidad());
				stmt.setInt(25, nov.getTipo_beneficiario_titular());
				stmt.setInt(26, nov.getFecha_alta_en_ooss());
				stmt.setInt(27, nov.getFecha_cierre_presentacion());
				stmt.setString(28, nov.getCodigo_movimiento());
				stmt.setString(29, nov.getDetalle_novedad());
				stmt.setString(30, user);

				result = stmt.executeUpdate();
			
			}
		
			con.commit();
			logger.debug("archivo Novedades: commiteado");			
		} catch (SQLException e) {
			
			if(e.getMessage().contains("duplicate key value violates unique constraint") ){
//				org.postgresql.util.PSQLException: ERROR: duplicate key value violates unique constraint "FECHA_DESC_PK"
//				  Detail: Key (fecha_archivo, descripcion)=(2014-05-01, NOVEDADES) already exists.
				
				throw new PeriodoArchivoDuplicadoException();
			}
			
			logger.error("Error al insertar archivo de novedades ", e);
			ConnectionHelper.rollback(con);
			throw e;
		} finally {
			ConnectionHelper.cerrar(stmt);
			ConnectionHelper.cerrar(stmt1, con);
		}
		return result;
	}
	
	

	public int grabaArchivoNovedadesPadronConsolidado(List<NovedadPadronConsolidado> novs, Date fechaArchivo, String user) throws SQLException, PeriodoArchivoDuplicadoException {
     	
		int result = 0, idProceso = 0;
		Connection con = null;
		CallableStatement stmt = null, stmt1 = null;
		String queryNov, queryArch;
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

		try {
			
			logger.debug("Comienzo a grabar archivo Novedades Padron Consolidado ");
			con = ConnectionHelper.getReportesOspimConnection();

			con.setAutoCommit(false);

			queryArch = "{call novedades_sss.inserta_archivos_novedades(?, ?, ?, ?) }";
    		
			stmt1 = con.prepareCall(queryArch.toString());
			
			stmt1.setDate(1, new java.sql.Date(fechaArchivo.getTime()) );
			stmt1.setString(2, WebKeysAfiliados.TIPOS_ORIGEN[6]);
			stmt1.setInt(3, novs.size());
			stmt1.setString(4, user);
			
			ResultSet rs = stmt1.executeQuery();
			while (rs.next()) {
				idProceso =  rs.getInt(1);
			}
			
			for (Iterator<NovedadPadronConsolidado> iterator = novs.iterator(); iterator.hasNext();) {
				NovedadPadronConsolidado nov = iterator.next();
				
			
				queryNov = "{call novedades_sss.inserta_novedad_padron_consolidado(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?," +
															"?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) }";
					            		
				stmt = con.prepareCall(queryNov.toString());

				stmt.setInt(1, idProceso);
				stmt.setInt(2, nov.getCodOOSS());
				stmt.setString(3, nov.getCuitEmpleador());
				stmt.setString(4, nov.getCuilTitular());
				stmt.setInt(5, nov.getCodigoParentesco());
				stmt.setString(6, nov.getCuil());
				stmt.setString(7, nov.getDocumentoTipo());
				stmt.setInt(8, nov.getDocumentoNumero());
				stmt.setString(9, nov.getApellidoNombre());
				stmt.setString(10, nov.getSexo());
				stmt.setInt(11, nov.getEstadoCivil());
				stmt.setDate(12, new java.sql.Date(nov.getFechaNacimiento().getTime()));
				stmt.setInt(13, nov.getNacionalidad());
				stmt.setString(14, nov.getCalle());
				stmt.setString(15, nov.getNumeroPuerta());
				stmt.setString(16, nov.getPiso());
				stmt.setString(17, nov.getDepartamento());
				stmt.setString(18, nov.getLocalidad());
				stmt.setString(19, nov.getCodigoPostal());
				stmt.setInt(20, nov.getProvincia());
				if(nov.getTipoDomicilio()==null){
					stmt.setNull(21, Types.INTEGER );
				}else{
					stmt.setInt(21, nov.getTipoDomicilio());
				}
				if(nov.getTelefono() == null){
					stmt.setNull(22, Types.VARCHAR);
				}else{
					stmt.setString(22, nov.getTelefono());					
				}
				stmt.setInt(23, nov.getSituacionRevista());
				stmt.setInt(24, nov.getIncapacidad());
				stmt.setInt(25, nov.getTipoBeneficiarioTitular());
				stmt.setDate(26, new java.sql.Date(nov.getFechaAltaOOSS().getTime()));
				stmt.setDate(27, new java.sql.Date(nov.getFechaCierrePresentacion().getTime()));
				//Otros campos
				stmt.setString(28, nov.getVerificacionCUIL());
				if(nov.getCuilInformadoPorOtraObraSocial() == null){
					stmt.setNull(29, Types.VARCHAR);
				}else{
					stmt.setString(29, nov.getCuilInformadoPorOtraObraSocial());					
				}
				if(nov.getTipoBeneficiarioSegunSIJP() == null){
					stmt.setNull(30, Types.VARCHAR);
				}else{
					stmt.setString(30, nov.getTipoBeneficiarioSegunSIJP());					
				}
				if(nov.getCUITSegunSIJP() == null){
					stmt.setNull(31, Types.VARCHAR);
				}else{
					stmt.setString(31, nov.getCUITSegunSIJP());					
				}
				if(nov.getOSSegunSIJP() == null){
					stmt.setNull(32, Types.INTEGER);
				}else{
					stmt.setInt(32, nov.getOSSegunSIJP());					
				}
				if(nov.getUltimoPeriodoInfomadoSIJP() == null){
					stmt.setNull(33, Types.VARCHAR);
				}else{
					stmt.setString(33, nov.getUltimoPeriodoInfomadoSIJP());					
				}
				if(nov.getObrasSocialOpcionVigente() == null){
					stmt.setNull(34, Types.VARCHAR);
				}else{
					stmt.setString(34, nov.getObrasSocialOpcionVigente());					
				}
			
				stmt.setString(35, nov.getPeriodoOpcionAnterior());				
				
				stmt.setString(36, user);

				result = stmt.executeUpdate();
			
			}
			
			
			
			int horaCorridaDiferida = 4; 
			
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.HOUR_OF_DAY, horaCorridaDiferida);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			cal.add(Calendar.DATE, 1);

			ReporteAutomatico raAux = new ReporteAutomatico();
			raAux.setFechaUnicaVez(new java.sql.Date(cal.getTimeInMillis())); 
			raAux.setHora(horaCorridaDiferida);
			raAux.setTitulo("PROCESAMIENTO AUTOMATICO BAJAS Y ALTAS  de novedades SSS, id Proceso : " +  idProceso);
			raAux.setJava(null);
			raAux.setEmails("acomas@ospim.org.ar,htivoli@ospim.org.ar,afiliaciones@ospim.org.ar");
			raAux.setBase(1);
			raAux.setCsvParameteres(null);
			raAux.setDiaDeLaSemana(0);
			raAux.setDiaDelMes(0);
			raAux.setDiario(false); // true
			raAux.setDifusion(0);
			raAux.setIncluirFinDeSemana(false);
			raAux.setStoredProcedure("novedades_sss.regla_automatica_ejecuta");
			raAux.setUltimaEjecucion(null);
			
			try {
				ReportesServiceUtil.save(raAux);
			} catch (SystemException e) {
				logger.debug("Error agendar ");			;
			}
			
			//genera   novedades_sss.novedades_pendientes 
			NovedadesServiceUtil.getInstance().getNovedadesXls(null, null, null, null, null, null,  null, "NOVEDADES", fechaArchivo);

			
			
			con.commit();
			logger.debug("archivo Novedades: commiteado Padron Consolidado");			
		} catch (SQLException e) {
			
			if(e.getMessage().contains("duplicate key value violates unique constraint") ){
//				org.postgresql.util.PSQLException: ERROR: duplicate key value violates unique constraint "FECHA_DESC_PK"
//				  Detail: Key (fecha_archivo, descripcion)=(2014-05-01, NOVEDADES) already exists.
				
				throw new PeriodoArchivoDuplicadoException();
			}
			
			logger.error("Error al insertar archivo de novedades ", e);
			ConnectionHelper.rollback(con);
			throw e;
		} catch (SystemException e) {
			logger.debug(e);		
		} finally {
			ConnectionHelper.cerrar(stmt);
			ConnectionHelper.cerrar(stmt1, con);
		}
		return result;
	}
	
	
	
	public void actualizarDelegaciones112608(List<Delegacion> delegaciones, Date fechaArchivo, String user, String tipoOrigen) 
			throws SQLException {

		int cantGrabada = 0;
		Connection con = null;
		PreparedStatement stmt = null, stmt1 = null, stmt2 = null;;
		String sql = "";
	
		try {
			logger.debug("archivo transferencia ext: comienzo a actualizar delegaciones");
			con = ConnectionHelper.getReportesOspimConnection();
			con.setAutoCommit(false);

			if (delegaciones != null && delegaciones.size() > 0) {
				
//				Se borra toda la tabla siempre, antes de insertar todas las delegaciones
				String slqDel = "delete from delegacion";
				
				stmt = con.prepareStatement(slqDel.toString());
				stmt.executeUpdate();

						  
				for (Iterator<Delegacion> iterator = delegaciones.iterator(); iterator.hasNext();) {
					Delegacion d = iterator.next();
					
					sql = "{call novedades_sss.inserta_delegacion(?,?,?,?,?,?,?) }";
		
					stmt2 = con.prepareCall(sql.toString());
					stmt2.setInt(1, d.getId_delegacion());
					stmt2.setString(2, d.getDescripcion().toUpperCase());
					stmt2.setInt(3, d.getRubrica());
					stmt2.setInt(4, d.getLibro());
					stmt2.setBoolean(5, d.isEsCentral());
					stmt2.setInt(6, d.getTomo());
					if(d.getAltaFecha()!= null){
						stmt2.setDate(7, new java.sql.Date(d.getAltaFecha().getTime()));
					}else{
						stmt2.setNull(7, Types.DATE);
					}
					stmt2.executeUpdate();
					cantGrabada++;
				}

				
				String queryArch = "{call novedades_sss.inserta_archivos_novedades(?, ?, ?, ?) }";
	    		
				stmt1 = con.prepareCall(queryArch.toString());
				
				stmt1.setDate(1, new java.sql.Date(fechaArchivo.getTime()) );
				stmt1.setString(2, tipoOrigen);
				stmt1.setInt(3, cantGrabada);
				stmt1.setString(4, user);
				stmt1.executeQuery();
				
			}
			logger.debug("archivo transferencia externa: delegaciones listas");
			con.commit();
			logger.debug("archivo transferencia externa: commiteado");
		} catch (SQLException e) {
			logger.debug("Error al actualizando archivo transferencia externa ", e);
			ConnectionHelper.rollback(con);

		} finally {
			ConnectionHelper.cerrar(stmt);
			ConnectionHelper.cerrar(stmt1);
			ConnectionHelper.cerrar(stmt2, con);
		}
	}
	
	public int grabaArchivoAfiliacionPrevencion(List<AfiliacionPrevencionDTO> novs, Date fechaArchivo, String user) throws SQLException, PeriodoArchivoDuplicadoException {
     	
		int result = 0;
		Connection con = null;
		CallableStatement stmt = null, stmt1 = null;
		String queryNov, queryArch;
		AfiliacionPrevencionDTO nov = null;
		try {
			
			logger.debug("Comienzo a actualizar Afiliación Prevención desde archivo");
			con = ConnectionHelper.getReportesOspimConnection();

			con.setAutoCommit(false);
			
			for (Iterator<AfiliacionPrevencionDTO> iterator = novs.iterator(); iterator.hasNext();) {
				nov = iterator.next();
				
				
				queryNov = "{ ? = call actualizar_credencial_prevencion_batch(?, ?, ?, ?, ?, ?, ?) }";
				logger.debug(nov.toString());
				
				stmt = con.prepareCall(queryNov.toString());
				stmt.registerOutParameter(1, Types.INTEGER);
				stmt.setInt(2, nov.getNroSocio());
				stmt.setString(3, nov.getNroDocumento());
				stmt.setString(4, nov.getCuil());
				stmt.setBigDecimal(5, nov.getNroCredencial());
				stmt.setString(6, nov.getCuilTitular());
				stmt.setInt(7, nov.getIntePrevencion());
				stmt.setString(8, user);

				stmt.executeUpdate();
			
				if(stmt.getInt(1) > 0){
					logger.debug("actualizado");
					result++;
				}
			}
		
			queryArch = "{call novedades_sss.inserta_archivos_novedades(?, ?, ?, ?) }";
    		
			stmt1 = con.prepareCall(queryArch.toString());
			
			stmt1.setDate(1, new java.sql.Date(DateUtils.getCalendarGMTMenos3().getTime().getTime()) );
			stmt1.setString(2, WebKeysAfiliados.TIPOS_ORIGEN[4]);
			stmt1.setInt(3, result);
			stmt1.setString(4, user);
			
			stmt1.executeQuery();

			con.commit();
			logger.debug("archivo Novedades Prevención: commiteado");			
		} catch (SQLException e) {
			logger.error("******** ERROR ***********: " + nov.toString());
			if(e.getMessage().contains("duplicate key value violates unique constraint") ){
//				org.postgresql.util.PSQLException: ERROR: duplicate key value violates unique constraint "FECHA_DESC_PK"
//				  Detail: Key (fecha_archivo, descripcion)=(2014-05-01, NOVEDADES) already exists.
				
				throw new PeriodoArchivoDuplicadoException();
			}
			
			logger.error("Error al insertar archivo de novedades prevención ", e);
			ConnectionHelper.rollback(con);
			throw e;
		} finally {
			ConnectionHelper.cerrar(stmt);
			ConnectionHelper.cerrar(stmt1, con);
		}
		return result;
	}

	public void procesarArchivoDesglose (String header, BufferedReader scanner ,String  user, int mes, int anio ,Date FechaArchivo)
		throws Exception{		
		
		ArchivoDesglose nuevoArchivo = new ArchivoDesglose();			
		
		List<DetalleDesglose> detalleList = new ArrayList<DetalleDesglose>();
		
		String line = header;
		
			while ((line = scanner.readLine()) != null) {	
		          detalleList.add(new DetalleDesglose (line));			  
			}						
		
			nuevoArchivo.setDetalle(detalleList);		
			grabaArchivoDesglose(nuevoArchivo,user , mes,anio,detalleList.size() , FechaArchivo ); // graba datos archivo y resumen del archivo		 
		
	}
	
	public int grabaArchivoAFIPrg830(List<ArchivoAfipRG830> detalles, String user) throws SQLException {
     	
		int result = 0;
		Connection con = null;
		CallableStatement stmt = null;
		String query;
		
		try {
			
			logger.debug("Comienzo a grabar archivo Afip RG 830");
			con = ConnectionHelper.getReportesOspimConnection();

			con.setAutoCommit(false);

			query = "{call uoma.inserta_registro_afipRG830(?,?,?,?,?,?,?,?,?,?,?) }";

			for (Iterator<ArchivoAfipRG830> iterator = detalles.iterator(); iterator.hasNext();) {
				
				ArchivoAfipRG830 det = iterator.next();
   		
				stmt = con.prepareCall(query.toString());

				stmt.setString(1,det.getCredencialNumero());
				stmt.setString(2,det.getCredencialDescripcion());
				stmt.setString(3,det.getCredencialAlcance());
				stmt.setShort(4, (short)det.getPorcentajeExcep());
				stmt.setString(5,det.getCuit());
				stmt.setShort(6, (short)det.getAnio());
				stmt.setShort(7, (short)det.getNro());
				stmt.setDate(8, new java.sql.Date(det.getFechaPresentacion().getTime()));
				stmt.setDate(9, new java.sql.Date(det.getVigenciaDesde().getTime()));
				stmt.setDate(10, new java.sql.Date(det.getVigenciaHasta().getTime()));
				stmt.setString(11, user);

				result = stmt.executeUpdate();
			
			}
		
			con.commit();
			logger.debug("archivo detalles AFIP RG 830: commiteado");			
		} catch (SQLException e) {
			logger.error("Error al insertar archivo AFIP RG 830 ", e);
			ConnectionHelper.rollback(con);
			throw e;
		} finally {
			ConnectionHelper.cerrar(stmt,con);
		}
		return result;
	}
	
	public int procesarBajasPorOpcion(ArrayList<String> listaCuil , Date fechaArchivo, String user) throws SQLException {
	
		int result = 0;
		Connection con = null;
		PreparedStatement stmt = null, stmt1 = null, stmt2 = null;
	
		try {
			
			con = ConnectionHelper.getConnection();
			con.setAutoCommit(false);
	
			StringBuilder sbString = new StringBuilder("");
			for(String cuil : listaCuil){
	            sbString.append(cuil).append(",");
	        }
			String cuils =   sbString.toString();
			cuils = cuils.substring(0, cuils.length() - 1);
			
		   
			
			int bajasPorOpcion = 0;
			bajasPorOpcion =  listaCuil.size();
			String sql = "{call conciliacion.insertar_bajas_por_opcion_sss_masivas(?,?,?) }" ;
				
	
			stmt = con.prepareCall(sql.toString());
			stmt.setString(1, cuils);
			stmt.setDate(2, new java.sql.Date(fechaArchivo.getTime()));
			stmt.setString(3, user);
			
				
			stmt.executeQuery();
	 
			con.commit();
			
			
			String sqlBajas = "{call public.bajas_por_opcion_sss_masivas(?)}";
			stmt1 = con.prepareCall(sqlBajas.toString());
			stmt1.setDate(1, new java.sql.Date(fechaArchivo.getTime()));
			stmt1.executeQuery();
					
			String queryArch = "{call novedades_sss.inserta_archivos_novedades(?, ?, ?, ?) }";
	    		
			stmt2 = con.prepareCall(queryArch.toString());
				
			stmt2.setDate(1, new java.sql.Date(fechaArchivo.getTime()) );
			stmt2.setString(2, WebKeysAfiliados.TIPOS_ORIGEN[5]);
			stmt2.setInt(3, bajasPorOpcion);
			stmt2.setString(4, user);
			stmt2.executeQuery();
				
			
			logger.debug("bajas por Opcion");
			con.commit();
		} catch (SQLException e) {
			logger.error(e);
			ConnectionHelper.rollback(con);
			throw e;	
		} finally {
			ConnectionHelper.cerrar(stmt);
			ConnectionHelper.cerrar(stmt1);
			ConnectionHelper.cerrar(stmt2, con);
		}
		return result;
	}
	
	public int deleteArchivoPadronAlicuotasARBA(Connection connectionParameter) throws Exception{
		
		int result = 0;		
		PreparedStatement stmt1 = null;
		Connection con = null;
		try {
			logger.debug("delete archivo Alicuotas ARBA: comienzo a grabar");
			
			if(connectionParameter == null){
				con = ConnectionHelper.getConnectionForTransaction();
			}else{
				con = connectionParameter;
				if(con.getAutoCommit()) {
					con.setAutoCommit(false);
				}
				logger.debug(con.getMetaData().getURL());
			}
			String sql1 = null;
			sql1 ="delete from uoma.arba_padron_alicuotas";
			stmt1 = con.prepareStatement(sql1.toString());
			result = stmt1.executeUpdate();
			
			logger.debug("borrado archivo Alicuotas ARBA: detalles listos");
			con.commit();
			logger.debug("borrado archivo Alicuotas ARBA: commiteado");
		} catch (SQLException e) {
						logger.debug("Error al insertar archivo Alicuotas ARBA ", e);
			ConnectionHelper.rollback(con);
			
		} finally {
			if(connectionParameter==null){
				ConnectionHelper.cerrar(stmt1,con);
			}else{
				ConnectionHelper.cerrar(stmt1);
			}
		}

		return result;
	}


	
	
	public int grabaArchivoPadronAlicuotasARBA(List<ArchivoARBAPadronAlicuota> archivo, Connection connectionParameter) throws Exception{

		int result = 0;		
		PreparedStatement stmt = null;
		Connection con = null;
		try {
			logger.debug("archivo Alicuotas ARBA: comienzo a grabar");
			
			if(connectionParameter == null){
				con = ConnectionHelper.getConnectionForTransaction();
			}else{
				con = connectionParameter;
				if(con.getAutoCommit()) {
					con.setAutoCommit(false);
				}
				logger.debug(con.getMetaData().getURL());
			}
			
			if (archivo != null && archivo.size() > 0) {
				
				int cantGrabada = 0;
				while (cantGrabada < archivo.size()) {
					int falta = archivo.size() - cantGrabada;
					int hta = 0;
					if (falta > BATCH_UPDATES) {
						hta = cantGrabada + BATCH_UPDATES;
					} else {
						hta = archivo.size();
					}
					
					String sql = null;
					
					sql = "INSERT INTO  uoma.arba_padron_alicuotas(regimen,cuit,vigente_desde, vigente_hasta, alicuota) VALUES (?, ?, ?, ?, ?)";
					stmt = con.prepareStatement(sql.toString());

					for (int i = cantGrabada; i < hta; i++) {
						ArchivoARBAPadronAlicuota det = archivo.get(i);
						stmt.setString(1,det.getRegimen());
						stmt.setString(2,det.getCuit());
						stmt.setDate(3, new java.sql.Date(det
								.getVigenciaDesde().getTime()));
						stmt.setDate(4, new java.sql.Date(det
								.getVigenciaHasta().getTime()));
						stmt.setDouble(5, det.getAlicuota());
						
						stmt.addBatch();

						cantGrabada++;
					}
					stmt.executeBatch();

				}
			}
			logger.debug("archivo Alicuotas ARBA: detalles listos");
			con.commit();
			logger.debug("archivo Alicuotas ARBA: commiteado");
		} catch (SQLException e) {
						logger.debug("Error al insertar archivo Alicuotas ARBA ", e);
			ConnectionHelper.rollback(con);
			
		} finally {
			if(connectionParameter==null){
				ConnectionHelper.cerrar(stmt,con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}

		return result;
	}
	
	
    public int grabaJubiladosSitaci(List<JubiladosSitaci> detalles, String user) throws SQLException {
     	
		int result = 0;
		Connection con = null;
		CallableStatement stmt = null;
		String query;
		
		try {
			
			logger.debug("Comienzo a grabar Jubilados SITACI");
			con = ConnectionHelper.getReportesOspimConnection();

			con.setAutoCommit(false);

			query = "{call inserta_registro_jubilados_sitaci(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) }";

			for (Iterator<JubiladosSitaci> iterator = detalles.iterator(); iterator.hasNext();) {
				
				JubiladosSitaci det = iterator.next();
   		
				stmt = con.prepareCall(query.toString());
				
				stmt.setString(1,det.getBeneficio());
				stmt.setString(2,det.getAfiliado());
				stmt.setString(3,det.getTipo1());
				stmt.setString(4,det.getTipo2());
				stmt.setString(5,det.getDni());
				stmt.setString(6,det.getConcepto());
				stmt.setDouble(7,det.getSumatoria());
				stmt.setDouble(8,det.getConceptoImporte());
				stmt.setString(9,det.getPeriodo());
				stmt.setString(10,det.getCuil());
				stmt.setDate(11, new java.sql.Date(det.getNacimiento().getTime()));
				stmt.setString(12,det.getSexo());
				stmt.setString(13,det.getFiller01());
				stmt.setString(14,det.getRegistro());
				stmt.setString(15, user);
                stmt.setInt(16,det.getPeriodoLiquidacion()); 
				result = stmt.executeUpdate();
			
			}
		
			con.commit();
			logger.debug("archivo detalles Jubilados SITACI: commiteado");			
		} catch (SQLException e) {
			if (e.getSQLState().equals("23505")) {
				logger.error("Error al insertar archivo Jubilados SITACI- Registro Existente en tabla Jubilados SITACI");
			}else {
				logger.error("Error al insertar archivo Jubilados SITACI ", e);
			}
			ConnectionHelper.rollback(con);
			throw e;
		} finally {
			ConnectionHelper.cerrar(stmt,con);
		}
		return result;
	}
    
    
    public int truncateArchivoAfip() throws SQLException, Exception {
		int result = 0;
		Connection con = null;
		PreparedStatement stmt = null;
		try {
			logger.debug("Truncate Base AFIP: comienzo a grabar");
			con = ConnectionHelper.getReportesOspimConnection();
			con.setAutoCommit(false);
			String s="Truncate table base_afip"; 
			stmt = con.prepareStatement(s.toString());
			stmt.executeUpdate();
			
						
			logger.debug("Truncate Base AFIP listo");
			con.commit();
		} catch (Exception e) {
			logger.debug("Error Truncate Base AFIP ",e);
			ConnectionHelper.rollback(con);
			throw e;
		} finally {
			ConnectionHelper.cerrar(stmt,con);
		}
		return 0;
	}

    
    
    public int grabaArchivoPadronAFIP(List<String> archivo, Connection connectionParameter) throws Exception{
    	int BATCH_UPDATES_AFIP = 1;
		int result = 0;		
		PreparedStatement stmt = null;
		Connection con = null;
		try {
			logger.debug("archivo Base AFIP: comienzo a grabar");
			
			if(connectionParameter == null){
				con = ConnectionHelper.getConnectionForTransaction();
			}else{
				con = connectionParameter;
				if(con.getAutoCommit()) {
					con.setAutoCommit(false);
				}
				logger.debug(con.getMetaData().getURL());
			}
			/*
			String s="Truncate table base_afip"; 
			stmt = con.prepareStatement(s.toString());
			stmt.executeUpdate();
			*/
			logger.debug("archivo Base AFIP: Fin truncate - Inicio grabacion");
			if (archivo != null && archivo.size() > 0) {
				
				int cantGrabada = 0;
				while (cantGrabada < archivo.size()) {
					int falta = archivo.size() - cantGrabada;
					int hta = 0;
					if (falta > BATCH_UPDATES_AFIP) {
						hta = cantGrabada + BATCH_UPDATES_AFIP;
					} else {
						hta = archivo.size();
					}
					
					String sql = null;
					
					for (int i = cantGrabada; i < hta; i++) {
						stmt = con.prepareStatement(archivo.get(i));
						stmt.addBatch();
						cantGrabada++;
					}
					stmt.executeBatch();

				}
			}
			logger.debug("archivo Base AFIP listo");
			con.commit();
			logger.debug("archivo Base AFIP: commiteado");
		} catch (SQLException e) {
						logger.debug("Error al insertar archivo BASE AFIP ", e);
			ConnectionHelper.rollback(con);
			
		} finally {
			if(connectionParameter==null){
				ConnectionHelper.cerrar(stmt,con);
			}else{
				ConnectionHelper.cerrar(stmt);
			}
		}

		return result;
	}
    
}
