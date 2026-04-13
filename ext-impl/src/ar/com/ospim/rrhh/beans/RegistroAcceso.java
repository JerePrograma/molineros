package ar.com.ospim.rrhh.beans;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import ar.com.ospim.util.DateUtils;

/**
 * @author Carlos Rivas
 * @edit SVA
 * @version 1.0
 * @created 21-Feb-2013 03:11 a.m.
 * @modified 06/04/2016 
 */
public class RegistroAcceso implements Serializable {
	
	private static final long serialVersionUID = -5815302311623904263L;
	private static Log logger = LogFactoryUtil.getLog(RegistroAcceso.class);
	
	private long id;
	private int id_tarjeta_acceso;
	private Date fecha_registro;
	private String tipo_registro;
	private int punto_acceso;
	private Date alta_fecha;
	private String alta_usr;
	private Date modi_fecha;
	private String modi_usr;
	private Date baja_fecha;
	private String baja_usr;
	private TarjetaAcceso tarjetaAcceso;
	
	public RegistroAcceso() {
		super();
	}

	public RegistroAcceso(long id, int idTarjetaAcceso, Date fechaRegistro,
			String tipoRegistro, int puntoAcceso, Date altaFecha,
			String altaUsr, Date modiFecha, String modiUsr, Date bajaFecha,
			String bajaUsr) {		
		this.id = id;
		id_tarjeta_acceso = idTarjetaAcceso;
		fecha_registro = fechaRegistro;
		tipo_registro = tipoRegistro;
		punto_acceso = puntoAcceso;
		alta_fecha = altaFecha;
		alta_usr = altaUsr;
		modi_fecha = modiFecha;
		modi_usr = modiUsr;
		baja_fecha = bajaFecha;
		baja_usr = bajaUsr;
	}

	public RegistroAcceso(String line, int puntoAcceso) throws ParseException  {
		SimpleDateFormat sdf= new SimpleDateFormat(DateUtils.LONG_SEC); //21/11/2016 09:44:08
		SimpleDateFormat sdf3= new SimpleDateFormat("yyyy/MM/dd HH:mm"); //2016/08/01  07:50
		
		int idTarjetaAcceso = 0;
		String fecha = null;
		String tipoRegistro = null;
		
		if(puntoAcceso==1 || puntoAcceso==2){
			idTarjetaAcceso=Integer.valueOf(line.substring(0,10).trim());
			logger.debug("tarjeta acceso: " + idTarjetaAcceso);		
			this.id_tarjeta_acceso = idTarjetaAcceso;
			
			fecha=line.substring(11,30);		
			logger.debug("fecha y hora registro: " + fecha);		
			this.fecha_registro = sdf.parse(fecha); 
					
			tipoRegistro=line.substring(31,32);			
			logger.debug("tipo registro: "+ tipoRegistro);
			this.tipo_registro = tipoRegistro;
			
	//		como no se va a configurar en el aparato de fichadas, quien sabe que mambo hay? lo ajustamos con el parametro de origenEdificio		
	//		String puntoAcceso =line.substring(35,38);
	//		logger.debug("punto acceso: "+ puntoAcceso);
	//		this.punto_acceso = Integer.parseInt(puntoAcceso);
		}else if(puntoAcceso==3){
			idTarjetaAcceso=Integer.valueOf(line.substring(8,20).trim());
			logger.debug("tarjeta acceso: " + idTarjetaAcceso);		
			this.id_tarjeta_acceso = idTarjetaAcceso;
			
			fecha=line.substring(32,49);		
			logger.debug("fecha y hora registro: " + fecha);		
			this.fecha_registro = sdf3.parse(fecha); 
					
			tipoRegistro="E";  //line.substring(31,32);			
			logger.debug("tipo registro: "+ tipoRegistro);
			this.tipo_registro = tipoRegistro;
			
	//		como no se va a configurar en el aparato de fichadas, quien sabe que mambo hay? lo ajustamos con el parametro de origenEdificio		
	//		String puntoAcceso =line.substring(35,38);
	//		logger.debug("punto acceso: "+ puntoAcceso);
	//		this.punto_acceso = Integer.parseInt(puntoAcceso);
		}	
	}
		
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getId_tarjeta_acceso() {
		return id_tarjeta_acceso;
	}

	public void setId_tarjeta_acceso(int idTarjetaAcceso) {
		id_tarjeta_acceso = idTarjetaAcceso;
	}

	public Date getFecha_registro() {
		return fecha_registro;
	}

	public String getFecha_registroSinHora() {
		return null!=fecha_registro?DateUtils.format(fecha_registro,DateUtils.SHORT):"";
	}

	public String getFecha_registroSoloHora() {
		return null!=fecha_registro?DateUtils.getHora(fecha_registro)+":"+DateUtils.getMinuto(fecha_registro):"";
	}
	
	public void setFecha_registro(Date fechaRegistro) {
		fecha_registro = fechaRegistro;
	}

	public String getTipo_registro() {
		return tipo_registro;
	}

	public void setTipo_registro(String tipoRegistro) {
		tipo_registro = tipoRegistro;
	}

	public int getPunto_acceso() {
		return punto_acceso;
	}

	public void setPunto_acceso(int puntoAcceso) {
		punto_acceso = puntoAcceso;
	}

	public Date getAlta_fecha() {
		return alta_fecha;
	}

	public void setAlta_fecha(Date altaFecha) {
		alta_fecha = altaFecha;
	}

	public String getAlta_usr() {
		return alta_usr;
	}

	public void setAlta_usr(String altaUsr) {
		alta_usr = altaUsr;
	}

	public Date getModi_fecha() {
		return modi_fecha;
	}

	public void setModi_fecha(Date modiFecha) {
		modi_fecha = modiFecha;
	}

	public String getModi_usr() {
		return modi_usr;
	}

	public void setModi_usr(String modiUsr) {
		modi_usr = modiUsr;
	}

	public Date getBaja_fecha() {
		return baja_fecha;
	}

	public void setBaja_fecha(Date bajaFecha) {
		baja_fecha = bajaFecha;
	}

	public String getBaja_usr() {
		return baja_usr;
	}

	public void setBaja_usr(String bajaUsr) {
		baja_usr = bajaUsr;
	}
		
	public TarjetaAcceso getTarjetaAcceso() {
		return tarjetaAcceso;
	}

	public void setTarjetaAcceso(TarjetaAcceso tarjetaAcceso) {
		this.tarjetaAcceso = tarjetaAcceso;
	}

	public static RegistroAcceso getMapping(ResultSet rs, String prefix)
			throws SQLException {

		RegistroAcceso ra = new RegistroAcceso();
		ra.setId(rs.getInt(prefix + "id"));
		ra.setId_tarjeta_acceso(rs.getInt(prefix + "id_tarjeta_acceso"));
		ra.setFecha_registro(rs.getTimestamp(prefix + "fecha_registro"));
		ra.setTipo_registro(rs.getString(prefix + "tipo_registro"));
		ra.setPunto_acceso(rs.getInt(prefix + "punto_acceso"));
		ra.setAlta_fecha(rs.getDate(prefix + "alta_fecha"));
		ra.setAlta_usr(rs.getString(prefix + "alta_usr"));
		ra.setModi_fecha(rs.getDate(prefix + "modi_fecha"));
		ra.setModi_usr(rs.getString(prefix + "modi_usr"));
//		ra.setBaja_fecha(rs.getDate(prefix + "baja_fecha"));
//		ra.setBaja_usr(rs.getString(prefix + "baja_usr"));
		return ra;
	}
		
	long milisegundosPermanenciaLectura;	
	
	long milisegundosPermanenciaDia;
	long milisegundosLaboralesDia;
	long diferenciaMilisegundosDia;
	
	long milisegundosPermanenciaPeriodo;
	long milisegundosLaboralesPeriodo;
	long diferenciaMilisegundosPeriodo;
	
	public long getMilisegundosPermanenciaDia() {
		return milisegundosPermanenciaDia;
	}

	public void setMilisegundosPermanenciaDia(long milisegundosPermanenciaDia) {
		this.milisegundosPermanenciaDia = milisegundosPermanenciaDia;
	}

	public long getMilisegundosLaboralesDia() {
		return milisegundosLaboralesDia;
	}

	public void setMilisegundosLaboralesDia(long milisegundosLaboralesDia) {
		this.milisegundosLaboralesDia = milisegundosLaboralesDia;
	}

	public long getDiferenciaMilisegundosDia() {
		return diferenciaMilisegundosDia;
	}

	public void setDiferenciaMilisegundosDia(long diferenciaMilisegundosDia) {
		this.diferenciaMilisegundosDia = diferenciaMilisegundosDia;
	}

	public long getMilisegundosPermanenciaPeriodo() {
		return milisegundosPermanenciaPeriodo;
	}

	public void setMilisegundosPermanenciaPeriodo(
			long milisegundosPermanenciaPeriodo) {
		this.milisegundosPermanenciaPeriodo = milisegundosPermanenciaPeriodo;
	}

	public long getMilisegundosLaboralesPeriodo() {
		return milisegundosLaboralesPeriodo;
	}

	public void setMilisegundosLaboralesPeriodo(long milisegundosLaboralesPeriodo) {
		this.milisegundosLaboralesPeriodo = milisegundosLaboralesPeriodo;
	}

	public long getDiferenciaMilisegundosPeriodo() {
		return diferenciaMilisegundosPeriodo;
	}

	public void setDiferenciaMilisegundosPeriodo(long diferenciaMilisegundosPeriodo) {
		this.diferenciaMilisegundosPeriodo = diferenciaMilisegundosPeriodo;
	}

	public long getMilisegundosPermanenciaLectura() {
		return milisegundosPermanenciaLectura;
	}

	public void setMilisegundosPermanenciaLectura(
			long milisegundosPermanenciaLectura) {
		this.milisegundosPermanenciaLectura = milisegundosPermanenciaLectura;
	}
	
	private boolean ocultar = false;

	public boolean isOcultar() {
		return ocultar;
	}

	public void setOcultar(boolean ocultar) {
		this.ocultar = ocultar;
	}

}