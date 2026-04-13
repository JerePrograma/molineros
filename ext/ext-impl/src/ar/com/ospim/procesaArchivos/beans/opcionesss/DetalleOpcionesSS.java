package ar.com.ospim.procesaArchivos.beans.opcionesss;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import ar.com.ospim.util.StringUtils;

public class DetalleOpcionesSS implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Long id;
	private String tipoExportacion = "Sc";  //"Sede Central"
	private int delegacionId;
	private String delegacion;
	private int libro;
	private int tomo;
	private int nroFormulario;
	private int osElegida = 112608; // Ospim.
	private String regimen;
	private String cuil;
	private String apeNom;
	private String apellido;
	private String nombre;
	private String sexo;
	private String calle;
	private String numero;
	private int piso;
	private String departamento;
	private String localidad;
	private String codAreaTelParticular;
	private String telParticular;
	private String codAreaTelLaboral;
	private String telLaboral;
	private String codAreaCelular;
	private String telCelular;
	private String email;
	private int osAnterior;
	private String cuit; //Cuit Empleador
	private String unificaApo;
	private Date fechaElecc;
	private Date fechaCerti;
	private String cuilConyuge;
	private String apeNomConyuge;
	private Date fechaBaja;
	private Date fechaEntrega;
	private int numeroLote;
	private String versionSistema = "V60";
	private String cod_postal;
	private String provincia;
	private String razon_soc;
	private String docu_numero;
	private Date fecha_vigencia;
	private Date fechaExportacion;
	private boolean okDesdeSSS;
	private String proyecto;
	
	public DetalleOpcionesSS(){} 

	public DetalleOpcionesSS(String tipoExportacion, int delegacionId, String delegacionDesc, int libro, int tomo, int nroFormulario, String regimen, String cuil, 
							 String apellidoyNombre, String sexo, String calle, String numero, int piso, String dpto, String localidad,
							 String codAreaTelefonoPart, String telefonoPart, String codAreaTelLaboral, String telefonoLab, String codAreaCelular, String celular, 
							 String email, int osAnterior, String prestadorCUIT, Date fechaElecc, Date fechaCerti, Date fechaBaja, 
							 Date fechaEntrega, String codPostal,  String provincia, Date fechaExportacion, String unificaAportes, 
							 String cuilConyuge, String apeyNomConyuge){
		
		this.tipoExportacion=tipoExportacion;
		this.delegacionId = delegacionId;
		this.delegacion=delegacionDesc;
		this.libro=libro;
		this.tomo=tomo;
		this.nroFormulario=nroFormulario;
		this.regimen=regimen;
		this.cuil=cuil;
		this.apeNom=apellidoyNombre;
		this.sexo=sexo;
		this.calle=calle;
		this.numero=numero;
		this.piso=piso;
		this.departamento=dpto;
		this.localidad=localidad;
		this.setCodAreaTelParticular(codAreaTelefonoPart);
		this.telParticular=telefonoPart;
		this.setCodAreaTelLaboral(codAreaTelLaboral);
		this.telLaboral=telefonoLab;
		this.setCodAreaCelular(codAreaCelular);
		this.telCelular=celular;
		this.email=email;
		this.osAnterior=osAnterior;
		this.cuit=prestadorCUIT; //Cuit Empleador
		this.fechaElecc=fechaElecc;
		this.fechaCerti=fechaCerti;
		this.fechaEntrega=fechaEntrega;
		this.cod_postal=codPostal;
		this.provincia=provincia;
		this.fechaExportacion=fechaExportacion;
		this.apeNomConyuge = apeyNomConyuge;
		this.cuilConyuge = cuilConyuge;
		this.unificaApo = unificaAportes;
	}
	
	public DetalleOpcionesSS(String tipoExportacion, int delegacionId, String delegacionDesc, int libro, int tomo, int nroFormulario, String regimen, String cuil, 
			 String apellido, String nombre, String sexo, String calle, String numero, int piso, String dpto, String localidad,
			 String codAreaTelefonoPart, String telefonoPart, String codAreaTelLaboral, String telefonoLab, String codAreaCelular, String celular,
			 String email, int osAnterior, String prestadorCUIT, Date fechaElecc, Date fechaCerti, Date fechaBaja, Date fechaEntrega, 
			 String codPostal,  String provincia, Date fechaExportacion, String unificaAportes, String cuilConyuge, String apeyNomConyuge,
			 String proyecto){

		this.tipoExportacion=tipoExportacion;
		this.delegacionId = delegacionId;
		this.delegacion=delegacionDesc;
		this.libro=libro;
		this.tomo=tomo;
		this.nroFormulario=nroFormulario;
		this.regimen=regimen;
		this.cuil=cuil;
		this.apellido = apellido;
		this.nombre = nombre;
		this.sexo=sexo;
		this.calle=calle;
		this.numero=numero;
		this.piso=piso;
		this.departamento=dpto;
		this.localidad=localidad;
		this.setCodAreaTelParticular(codAreaTelefonoPart);
		this.telParticular=telefonoPart;
		this.setCodAreaTelLaboral(codAreaTelLaboral);
		this.telLaboral=telefonoLab;
		this.setCodAreaCelular(codAreaCelular);
		this.telCelular=celular;
		this.email=email;
		this.osAnterior=osAnterior;
		this.cuit=prestadorCUIT; //Cuit Empleador
		this.fechaElecc=fechaElecc;
		this.fechaCerti=fechaCerti;
		this.fechaEntrega=fechaEntrega;
		this.cod_postal=codPostal;
		this.provincia=provincia;
		this.fechaExportacion=fechaExportacion;
		this.apeNomConyuge = apeyNomConyuge;
		this.cuilConyuge = cuilConyuge;
		this.unificaApo = unificaAportes;
		this.proyecto = proyecto;
	}
	public DetalleOpcionesSS cargaError(String line) throws ParseException {
		DetalleOpcionesSS detalle=new DetalleOpcionesSS();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		String[] linea = line.split("\\|");
		detalle.setDocu_numero(linea[6].trim());		
		return detalle;
	}
	
	public DetalleOpcionesSS cargaErrorDesdeInforme(String line) throws ParseException {
		DetalleOpcionesSS detalle=new DetalleOpcionesSS();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		String[] linea = line.split("\\|");
		detalle.setDocu_numero(linea[6].trim());		
		return detalle;
	}
	
	
	public DetalleOpcionesSS(String line) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		String[] linea = line.split("\\|");

		this.tipoExportacion = linea[0].trim();
		this.delegacion = linea[1].trim();
		this.libro = linea[2] != null && linea[2].trim().length() > 0 ? Integer.parseInt(linea[2].trim()) : 0;
		this.tomo = linea[3] != null && linea[3].trim().length() > 0 ? Integer.parseInt(linea[3].trim()) : 0;
		this.nroFormulario = linea[4] != null && linea[4].trim().length() > 0 ? Integer.parseInt(linea[4].trim()) : 0;
		this.osElegida = linea[5] != null && linea[5].trim().length() > 0 ? Integer.parseInt(linea[5].trim()) : 0;
		this.regimen = linea[6].trim();
		this.cuil = linea[7].trim();
		this.apeNom = linea[8].trim();
		this.sexo = linea[9].trim();
		this.calle = linea[10].trim();
		this.numero = linea[11].trim();
		try{
		this.piso = linea[12] != null && linea[12].trim().length() > 0 ? Integer
				.parseInt(linea[12].trim()) : 0;
		}catch(NumberFormatException e){
			this.piso=0;
		}
		this.departamento = linea[13].trim();
		this.localidad = linea[14].trim();
		this.telParticular = linea[15].trim();
		this.telLaboral = linea[16].trim();
		this.telCelular = linea[17].trim();
		this.email = linea[18].trim();
		this.osAnterior = linea[19] != null && linea[19].trim().length() > 0 ? Integer.parseInt(linea[19].trim()) : 0;
		this.cuit = linea[20].trim();
		this.unificaApo = linea[21].trim();
		this.fechaElecc = linea[22] != null && linea[22].trim().length() > 0 ? sdf.parse(linea[22].trim()) : null;
		this.fechaCerti = linea[23] != null && linea[23].trim().length() > 0 ? sdf.parse(linea[23].trim()) : null;
		this.cuilConyuge = linea[24].trim();
		this.apeNomConyuge = linea[25].trim();
		this.fechaBaja = linea[26] != null && linea[26].trim().length() > 0 ? sdf.parse(linea[26].trim()) : null;
		this.fechaEntrega = linea[27] != null && linea[27].trim().length() > 0 ? sdf.parse(linea[27].trim()) : null;
		this.numeroLote = linea[28] != null && linea[28].trim().length() > 0 ? Integer.parseInt(linea[28].trim()) : 0;
		this.versionSistema = linea[29].trim();
//		TODO fecha de exportacion?

	}
	
	public static DetalleOpcionesSS altaVuelta(String line) throws ParseException {
		DetalleOpcionesSS detalle=new DetalleOpcionesSS();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		String[] linea = line.split("\\|");

		detalle.tipoExportacion = linea[0].trim();
		detalle.nroFormulario = linea[1] != null && linea[1].trim().length() > 0 ? Integer.parseInt(linea[1].trim()) : 0;
		detalle.cuil = linea[2].trim();
		detalle.apeNom = linea[3].trim();
		detalle.calle = linea[4].trim();
		detalle.numero = linea[5].trim();
		try{
			detalle.piso = linea[6] != null && linea[6].trim().length() > 0 ? Integer.parseInt(linea[6].trim()) : 0;
		}catch(NumberFormatException e){
			detalle.piso=0;
		}
		detalle.departamento = linea[7].trim();
		detalle.telParticular = linea[8].trim();		
		detalle.localidad = linea[9].trim();
		detalle.cod_postal= linea[10].trim();
		detalle.provincia=linea[11].trim();
		detalle.cuit = linea[12].trim();
		detalle.razon_soc = linea[13].trim();
		detalle.unificaApo = linea[14].trim();
		detalle.fecha_vigencia = linea[15] != null && linea[15].trim().length() > 0 ? sdf.parse(linea[15].trim()) : null;
		detalle.delegacion = linea[16].trim();
		detalle.libro=linea[17] != null && linea[17].trim().length() > 0 ? Integer.parseInt(linea[17].trim()) : 0;
		detalle.fechaCerti = linea[18] != null && linea[18].trim().length() > 0 ? sdf.parse(linea[18].trim()) : null;
		detalle.osAnterior = linea[19] != null && linea[19].trim().length() > 0 ? Integer.parseInt(linea[19].trim()) : 0;	
//		TODO fecha exportacion?						
		return detalle;

	}
	
	public static DetalleOpcionesSS altaVueltaMono(String line) throws ParseException {
		DetalleOpcionesSS detalle=new DetalleOpcionesSS();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy"); //version hasta abril2016
//		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy"); //cambio a partir de Mayo.
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMM");
		String[] linea = line.split("\\|");
		
		detalle.nroFormulario = linea[0] != null && linea[0].trim().length() > 0 ? Integer.parseInt(linea[0].trim()) : 0;
		detalle.cuil = linea[1].trim().substring(0,11);
		detalle.apeNom = linea[2].trim();
		detalle.calle = linea[3].trim();
		detalle.numero = linea[4].trim();
		try{
			detalle.piso = linea[5] != null && linea[5].trim().length() > 0 ? Integer.parseInt(linea[5].trim()) : 0;
		}catch(NumberFormatException e){
			detalle.piso=0;
		}
		detalle.departamento = linea[6].trim();
		detalle.cod_postal= linea[7].trim();		
		detalle.localidad = linea[8].trim();
		detalle.provincia=linea[9].trim();		
		detalle.cuit = detalle.cuil;
		detalle.razon_soc = detalle.apeNom;
		detalle.fechaCerti = linea[10] != null && linea[10].trim().length() > 0 ? sdf.parse(linea[10].trim()) : null;
		detalle.fecha_vigencia = linea[11] != null && linea[11].trim().length() > 0 ? sdf2.parse(linea[11].trim()) : null;	
		
		return detalle;

	}
	
	public static DetalleOpcionesSS getMapping(String prefijo, ResultSet rs) throws SQLException{
		
		DetalleOpcionesSS detOpc = new DetalleOpcionesSS();
		
		detOpc.setTipoExportacion(rs.getString(prefijo+"tipo_exportacion"));
		detOpc.setDelegacionId(rs.getInt(prefijo+"id_delegacion"));
		detOpc.setDelegacion(rs.getString(prefijo+"delegacion"));
		detOpc.setLibro(rs.getInt(prefijo+"libro"));
		detOpc.setTomo(rs.getInt(prefijo+"tomo"));
		detOpc.setNroFormulario(rs.getInt(prefijo+"nro_formulario"));
		detOpc.setOsElegida(rs.getInt(prefijo+"os_elegida"));
		detOpc.setRegimen(rs.getString(prefijo+"regimen"));
		detOpc.setCuil(rs.getString(prefijo+"cuil"));
		detOpc.setApellido(rs.getString(prefijo+"apellido"));
		detOpc.setNombre(rs.getString(prefijo+"nombre"));
		detOpc.setSexo(rs.getString(prefijo+"sexo"));
		detOpc.setCalle(rs.getString(prefijo+"calle"));
		detOpc.setNumero(rs.getString(prefijo+"numero"));
		detOpc.setPiso(rs.getInt(prefijo+"piso"));
		detOpc.setDepartamento(rs.getString(prefijo+"departamento"));
		detOpc.setLocalidad(rs.getString(prefijo+"localidad"));
		detOpc.setCodAreaTelParticular(rs.getString(prefijo + "cod_area_telefono"));
		detOpc.setTelParticular(rs.getString(prefijo+"telefono_particular"));
		detOpc.setCodAreaTelLaboral(rs.getString(prefijo + "cod_area_tel_laboral"));
		detOpc.setTelLaboral(rs.getString(prefijo+"telefono_laboral"));
		detOpc.setCodAreaCelular(rs.getString(prefijo + "cod_area_celular"));
		detOpc.setTelCelular(rs.getString(prefijo+"telefono_celular"));
		detOpc.setEmail(rs.getString(prefijo+"email"));
		detOpc.setOsAnterior(rs.getInt(prefijo+"os_anterior"));
		detOpc.setCuit(rs.getString(prefijo+"cuit"));
		detOpc.setUnificaApo(rs.getString(prefijo+"unifica_apo"));
		detOpc.setFechaElecc(rs.getDate(prefijo+"fecha_elecc"));
		detOpc.setFechaCerti(rs.getDate(prefijo+"fecha_certi"));
		detOpc.setCuilConyuge(rs.getString(prefijo+"cuil_conyuge"));
		detOpc.setApeNomConyuge(rs.getString(prefijo+"ape_nom_conyuge"));
		detOpc.setFechaEntrega(rs.getDate(prefijo+"fecha_entrega"));
		detOpc.setFechaExportacion(rs.getDate(prefijo+"fecha_exportacion"));
		detOpc.setNumeroLote(rs.getInt(prefijo+"numero_lote"));
		detOpc.setVersionSistema(rs.getString(prefijo+"version_sistema"));
		detOpc.setCod_postal(rs.getString(prefijo+"postal_codi"));
		detOpc.setId(rs.getLong(prefijo+"id"));
		detOpc.setProvincia(rs.getString(prefijo+"provincia"));
		detOpc.setProyecto(rs.getString(prefijo+"proyecto"));
		
		return detOpc;
	}

	public StringBuffer getRenglonExportacionSSS(){
		
		int longApeyNom = this.apellido.trim().length() + this.nombre.trim().length() ;
		longApeyNom++; // por el espacio que separa Apellido del Nombre al concatenar.
		
		if(longApeyNom > 30){
			longApeyNom = 30; // formzamos al tamaño maximo que recibe la SSS para Apellido + " " + Nombre.
		}
		
		StringBuffer linea = new StringBuffer();

//		Si se desea que un String tenga un tamaño determinado, rellenando con espacios al final hasta un número de caracteres determinado se puede utilizar el método format de la clase String con la siguiente cadena de formato:
//
//			String.format("%1$-10s",cadena);
//
//			(Se debe sustituir el 10 por el tamaño deseado, y cadena debe ser la variable de tipo String o la cadena literal entre comillas sobre el que se desea aplicar el relleno).
		
		linea.append(String.format("%1$-2s",this.tipoExportacion) );
		linea.append("|");
		linea.append(String.format("%1$-50s",this.delegacion));
		linea.append("|");
		linea.append(String.format("%1$-5s",this.libro));
		linea.append("|");
		linea.append(String.format("%1$-5s",this.tomo));
		linea.append("|");
		linea.append(String.format("%1$-9s",this.nroFormulario));
		linea.append("|");
		linea.append(String.format("%1$-6s",this.osElegida));
		linea.append("|");
		linea.append(String.format("%1$-3s",this.regimen));
		linea.append("|");
		linea.append(String.format("%1$-11s",this.cuil));
		linea.append("|");
//		linea.append(String.format("%1$-30s",this.apeNom));
		linea.append(String.format("%1$-30s", (this.apellido.trim()+" "+this.nombre.trim()).substring(0, longApeyNom-1) ) );
		linea.append("|");
		linea.append(String.format("%1$-1s",this.sexo));
		linea.append("|");
		linea.append(String.format("%1$-20s",this.calle));
		linea.append("|");
		linea.append(String.format("%1$-5s",this.numero));
		linea.append("|");
		linea.append(String.format("%1$-4s",this.piso));
		linea.append("|");
		linea.append(String.format("%1$-4s",this.departamento));
		linea.append("|");
		linea.append(String.format("%1$-5s",this.localidad));
		linea.append("|");
		linea.append(String.format("%1$-20s",StringUtils.checkEmpty(this.telParticular)?0:
			StringUtils.checkEmpty(this.codAreaTelParticular)?this.telParticular:this.codAreaTelParticular+this.telParticular ) );
		linea.append("|");
		linea.append(String.format("%1$-20s",StringUtils.checkEmpty(this.telLaboral)?0:
			StringUtils.checkEmpty(this.codAreaTelLaboral)?this.telLaboral:this.codAreaTelLaboral+this.telLaboral ) );
		linea.append("|");
		linea.append(String.format("%1$-20s",StringUtils.checkEmpty(this.telCelular)?0:
			StringUtils.checkEmpty(this.codAreaCelular)?this.telCelular:this.codAreaCelular+this.telCelular ) );
		linea.append("|");
		linea.append(String.format("%1$-50s",this.email));
		linea.append("|");
		linea.append(String.format("%1$-6s",this.osAnterior));
		linea.append("|");
		linea.append(String.format("%1$-11s",this.cuit));
		linea.append("|");
		linea.append(String.format("%1$-2s",this.unificaApo));
		linea.append("|");
		linea.append(String.format("%1$-10s",this.fechaElecc));
		linea.append("|");
		linea.append(String.format("%1$-10s",this.fechaCerti));
		linea.append("|");
		linea.append(String.format("%1$-11s",this.cuilConyuge));
		linea.append("|");
		linea.append(String.format("%1$-30s",this.apeNomConyuge));
		linea.append("|");
		linea.append(String.format("%1$-10s",this.fechaExportacion));
		linea.append("|");
		linea.append(String.format("%1$-10s",this.fechaEntrega));
		linea.append("|");
		linea.append(String.format("%1$-5s",this.numeroLote));
		linea.append("|");
		linea.append(String.format("%1$-4s",this.versionSistema));
		linea.append("|");
		
		return linea;
	}
	public String getTipoExportacion() {
		return tipoExportacion;
	}

	public void setTipoExportacion(String tipoExportacion) {
		this.tipoExportacion = tipoExportacion;
	}

	public String getDelegacion() {
		return delegacion;
	}

	public void setDelegacion(String delegacion) {
		this.delegacion = delegacion;
	}

	public int getLibro() {
		return libro;
	}

	public void setLibro(int libro) {
		this.libro = libro;
	}

	public int getTomo() {
		return tomo;
	}

	public void setTomo(int tomo) {
		this.tomo = tomo;
	}

	public int getNroFormulario() {
		return nroFormulario;
	}

	public void setNroFormulario(int nroFormulario) {
		this.nroFormulario = nroFormulario;
	}

	public int getOsElegida() {
		return osElegida;
	}

	public void setOsElegida(int osElegida) {
		this.osElegida = osElegida;
	}

	public String getRegimen() {
		return regimen;
	}

	public void setRegimen(String regimen) {
		this.regimen = regimen;
	}

	public String getCuil() {
		return cuil;
	}

	public void setCuil(String cuil) {
		this.cuil = cuil;
	}

	public String getApeNom() {
		return apeNom;
	}

	public void setApeNom(String apeNom) {
		this.apeNom = apeNom;
	}

	public String getSexo() {
		return sexo;
	}

	public void setSexo(String sexo) {
		this.sexo = sexo;
	}

	public String getCalle() {
		return calle;
	}

	public void setCalle(String calle) {
		this.calle = calle;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public int getPiso() {
		return piso;
	}

	public void setPiso(int piso) {
		this.piso = piso;
	}

	public String getDepartamento() {
		return departamento;
	}

	public void setDepartamento(String departamento) {
		this.departamento = departamento;
	}

	public String getLocalidad() {
		return localidad;
	}

	public void setLocalidad(String localidad) {
		this.localidad = localidad;
	}

	public String getTelParticular() {
		return telParticular;
	}

	public void setTelParticular(String telParticular) {
		this.telParticular = telParticular;
	}

	public String getTelLaboral() {
		return telLaboral;
	}

	public void setTelLaboral(String telLaboral) {
		this.telLaboral = telLaboral;
	}

	public String getTelCelular() {
		return telCelular;
	}

	public void setTelCelular(String telCelular) {
		this.telCelular = telCelular;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public int getOsAnterior() {
		return osAnterior;
	}

	public void setOsAnterior(int osAnterior) {
		this.osAnterior = osAnterior;
	}

	public String getCuit() {
		return cuit;
	}

	public void setCuit(String cuit) {
		this.cuit = cuit;
	}

	public String getUnificaApo() {
		return unificaApo;
	}

	public void setUnificaApo(String unificaApo) {
		this.unificaApo = unificaApo;
	}

	public Date getFechaElecc() {
		return fechaElecc;
	}

	public void setFechaElecc(Date fechaElecc) {
		this.fechaElecc = fechaElecc;
	}

	public Date getFechaCerti() {
		return fechaCerti;
	}

	public void setFechaCerti(Date fechaCerti) {
		this.fechaCerti = fechaCerti;
	}

	public String getCuilConyuge() {
		return cuilConyuge;
	}

	public void setCuilConyuge(String cuilConyuge) {
		this.cuilConyuge = cuilConyuge;
	}

	public String getApeNomConyuge() {
		return apeNomConyuge;
	}

	public void setApeNomConyuge(String apeNomConyuge) {
		this.apeNomConyuge = apeNomConyuge;
	}

	public Date getFechaBaja() {
		return fechaBaja;
	}

	public void setFechaBaja(Date fechaBaja) {
		this.fechaBaja = fechaBaja;
	}

	public Date getFechaEntrega() {
		return fechaEntrega;
	}

	public void setFechaEntrega(Date fechaEntrega) {
		this.fechaEntrega = fechaEntrega;
	}

	public int getNumeroLote() {
		return numeroLote;
	}

	public void setNumeroLote(int numeroLote) {
		this.numeroLote = numeroLote;
	}

	public String getVersionSistema() {
		return versionSistema;
	}

	public void setVersionSistema(String versionSistema) {
		this.versionSistema = versionSistema;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}

	public String getCod_postal() {
		return cod_postal;
	}

	public void setCod_postal(String cod_postal) {
		this.cod_postal = cod_postal;
	}

	public String getProvincia() {
		return provincia;
	}

	public void setProvincia(String provincia) {
		this.provincia = provincia;
	}

	public String getRazon_soc() {
		return razon_soc;
	}

	public void setRazon_soc(String razon_soc) {
		this.razon_soc = razon_soc;
	}

	public Date getFecha_vigencia() {
		return fecha_vigencia;
	}

	public void setFecha_vigencia(Date fecha_vigencia) {
		this.fecha_vigencia = fecha_vigencia;
	}

	public String getDocu_numero() {
		return docu_numero;
	}

	public void setDocu_numero(String docu_numero) {
		this.docu_numero = docu_numero;
	}

	public Date getFechaExportacion() {
		return fechaExportacion;
	}

	public void setFechaExportacion(Date fechaExportacion) {
		this.fechaExportacion = fechaExportacion;
	}

	public int getDelegacionId() {
		return delegacionId;
	}

	public void setDelegacionId(int delegacionId) {
		this.delegacionId = delegacionId;
	}

	public boolean isOkDesdeSSS() {
		return okDesdeSSS;
	}

	public void setOkDesdeSSS(boolean okDesdeSSS) {
		this.okDesdeSSS = okDesdeSSS;
	}

	public String getApellido() {
		return apellido;
	}

	public void setApellido(String apellido) {
		this.apellido = apellido;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getCodAreaTelParticular() {
		return codAreaTelParticular;
	}

	public void setCodAreaTelParticular(String codAreaTelParticular) {
		this.codAreaTelParticular = codAreaTelParticular;
	}

	public String getCodAreaTelLaboral() {
		return codAreaTelLaboral;
	}

	public void setCodAreaTelLaboral(String codAreaTelLaboral) {
		this.codAreaTelLaboral = codAreaTelLaboral;
	}

	public String getCodAreaCelular() {
		return codAreaCelular;
	}

	public void setCodAreaCelular(String codAreaCelular) {
		this.codAreaCelular = codAreaCelular;
	}

	public String getProyecto() {
		return proyecto;
	}

	public void setProyecto(String proyecto) {
		this.proyecto = proyecto;
	}

}
