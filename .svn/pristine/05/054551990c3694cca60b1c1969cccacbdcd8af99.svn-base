package ar.com.ospim.farmacia.beans;


import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import ar.com.ospim.farmaciaOspim.beans.ItemVademecumTotal;


/**
 * @author Federico Brachi
 * @version 1.0
 * @created 14-Jul-2010 12:25:06 p.m.
 */
public class Vademecum implements Serializable {
	
	private static final long serialVersionUID = -1926053363012546744L;
	
	  private String droga; 
	  private String nombre;
	  private String presentacion;
	  private String laboratorio;
	  private String accion;
	  private int troquel;
	  private int registro;
	  private double porc_ospim;
	  private double porc_amtima;
	  private double porc_sssalud;
	  private double pmoe_n;
	  private Date alta_fecha;
	  private String alta_usr;
	  private Date modi_fecha;
	  private Date baja_fecha;	  
	  private String baja_usr;
	  private String modi_usr;
	  private Date fecha_vig;
	  private boolean pmiMadre;
	  private boolean pmiHijo;
	  private boolean anticoncepcion;
	  private boolean vademecumGral;
	  private boolean altaSSS;
	  private BigDecimal precioAlPublicoSss;
	  private BigDecimal precioAcargoOsocialSss ;
	  private BigDecimal precioManualDat ;
	  private BigDecimal precioAlPublico;	  
	  private Date periodo ;
	  private boolean bajaSSS;
	  private Date periodoAltasBajas;
	  private int unidades;
	  private String origenDeLosDatos;
	  private boolean  nuevaAltaSss;

		private static Log _log = LogFactoryUtil
				.getLog(Vademecum.class);
	public Vademecum() {
		
	}  
	
	public Vademecum(int registro , int troquel, String nombre,
			String presentacion, String laboratorio, String droga , Date periodo , boolean pmiMadre , boolean pmiHijo , boolean aco,
			boolean vadeGral, String accion ,double pmo, double porcOspim , double porcSssalud ,double porcAmtima ) {
		this.troquel = troquel;
		this.registro = registro;
		this.nombre = nombre;
		this.presentacion = presentacion;
		this.laboratorio = laboratorio;
		this.droga = droga;
		this.pmiMadre=pmiMadre;
		this.pmoe_n=pmo;
		this.accion=accion;
		this.pmiHijo=pmiHijo;
		this.vademecumGral=vadeGral ;
		this.anticoncepcion=aco;
		this.periodoAltasBajas= periodo;
		this.setPorc_amtima(porcAmtima);
		this.setPorc_ospim(porcOspim);
		this.setPorc_sssalud(porcSssalud);
	}
	
	public Vademecum(ResultSet rs) throws SQLException  {
		this.droga=rs.getString("droga");
		this.nombre=rs.getString("nombre");
		this.presentacion=rs.getString("presentacion");
		this.laboratorio=rs.getString("laboratorio");
		this.accion=rs.getString("accion");
		this.troquel=rs.getInt("troquel");
		this.registro=rs.getInt("registro");
		this.porc_sssalud=rs.getDouble("porc_sssalud");
		this.pmoe_n=rs.getDouble("pmoe_n");
		this.alta_fecha=rs.getDate("alta_fecha");
		this.modi_fecha=rs.getDate("modi_fecha");
		this.baja_fecha=rs.getDate("baja_fecha");
		this.fecha_vig=rs.getDate("fecha_vig");
		this.alta_usr=rs.getString("alta_usr");
		this.modi_usr=rs.getString("modi_usr");
		this.baja_usr=rs.getString("baja_usr");
		this.anticoncepcion = rs.getBoolean("anticoncepcion");
		this.pmiMadre= rs.getBoolean("pmi_madre");
		this.pmiHijo= rs.getBoolean("pmi_hijo");
	    this.vademecumGral= rs.getBoolean("vademedecum_gral");
	    this.setUnidades(rs.getInt("unidades"));
	    
	}
	
	public static Vademecumreporte  getMapping(ResultSet rs, String prefix)
			throws Exception {
		Vademecumreporte   vademecum = new Vademecumreporte  ();
		try{			
			vademecum.setRegistro(rs.getInt(prefix + "registro"));
			vademecum.setDroga(rs.getString(prefix + "droga"));
		    vademecum.setNombre(rs.getString(prefix + "nombre"));
		    vademecum.setLaboratorio(rs.getString(prefix + "laboratorio"));
		    vademecum.setPresentacion(rs.getString(prefix + "presentacion"));
		    vademecum.setBajaSSS(rs.getBoolean(prefix + "baja"));
		    vademecum.setAltaSSS(rs.getBoolean(prefix + "alta"));
		    vademecum.setPeriodoAltasBajas(rs.getDate(prefix + "periodo"));		    
		    vademecum.setCantidadGenericos(rs.getInt(prefix + "cantidad"));
		    vademecum.setTipoDatoVademecum(rs.getInt(prefix + "tipo_dato"));
		}
		catch (Exception e ){
		    throw e;
		}
				
		return vademecum;
	}


	public static Vademecum  getMappingxRegistroTroquel(ResultSet rs, String prefix)
			throws Exception {
		Vademecum  vademecum = new Vademecum();
		try{			
			vademecum.setRegistro(rs.getInt(prefix + "registro"));
			vademecum.setDroga(rs.getString(prefix + "droga"));
		    vademecum.setNombre(rs.getString(prefix + "nombre"));
		    vademecum.setAccion(rs.getString(prefix + "accion"));
		    vademecum.setTroquel(rs.getInt(prefix + "troquel"));
		    vademecum.setLaboratorio(rs.getString(prefix + "laboratorio"));
		    vademecum.setPresentacion(rs.getString(prefix + "presentacion"));
		    vademecum.setPorc_sssalud(rs.getDouble(prefix + "porc_sssalud"));
		    vademecum.setPmoe_n(rs.getDouble(prefix + "pmoe_n"));		    
		    vademecum.setAlta_fecha(rs.getDate(prefix + "alta_fecha"));
		    vademecum.setBaja_fecha(rs.getDate(prefix + "baja_fecha"));
		    vademecum.setPmiMadre(rs.getBoolean(prefix + "pmi_madre"));
		    vademecum.setPmiHijo(rs.getBoolean(prefix + "pmi_hijo"));
		    vademecum.setAnticoncepcion(rs.getBoolean(prefix + "anticoncepcion"));
		    vademecum.setVademecumGral(rs.getBoolean(prefix + "vademedecum_gral"));
		    vademecum.setPeriodoAltasBajas(rs.getDate(prefix + "periodo"));
		    vademecum.setOrigenDeLosDatos(rs.getString(prefix + "origendatos"));
		}
		catch (Exception e ){
		    throw e;
		}
				
		return vademecum;
	}

	
	public String getDroga() {
		return droga;
	}
	public void setDroga(String droga) {
		this.droga = droga;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getPresentacion() {
		return presentacion;
	}
	public void setPresentacion(String presentacion) {
		this.presentacion = presentacion;
	}
	public String getLaboratorio() {
		return laboratorio;
	}
	public void setLaboratorio(String laboratorio) {
		this.laboratorio = laboratorio;
	}
	public String getAccion() {
		return accion;
	}
	public void setAccion(String accion) {
		this.accion = accion;
	}
	public int getTroquel() {
		return troquel;
	}
	public void setTroquel(int numeric) {
		this.troquel = numeric;
	}
	public int getRegistro() {
		return registro;
	}
	public void setRegistro(int registro) {
		this.registro = registro;
	}
	public double getPorc_ospim() {
		return porc_ospim;
	}
	public void setPorc_ospim(double porcOspim) {
		porc_ospim = porcOspim;
	}
	public double getPorc_amtima() {
		return porc_amtima;
	}
	public void setPorc_amtima(double porcAmtima) {
		porc_amtima = porcAmtima;
	}
	public double getPorc_sssalud() {
		return porc_sssalud;
	}
	public void setPorc_sssalud(double porcSssalud) {
		porc_sssalud = porcSssalud;
	}
	public double getPmoe_n() {
		return pmoe_n;
	}
	public void setPmoe_n(double pmoeN) {
		pmoe_n = pmoeN;
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
	public String getModi_usr() {
		return modi_usr;
	}
	public void setModi_usr(String modiUsr) {
		modi_usr = modiUsr;
	}
	public Date getFecha_vig() {
		return fecha_vig;
	}
	public void setFecha_vig(Date fechaVig) {
		fecha_vig = fechaVig;
	}

	public boolean isPmiMadre() {
		return pmiMadre;
	}

	public void setPmiMadre(boolean pmiMadre) {
		this.pmiMadre = pmiMadre;
	}

	public boolean isPmiHijo() {
		return pmiHijo;
	}

	public void setPmiHijo(boolean pmiHijo) {
		this.pmiHijo = pmiHijo;
	}

	public boolean isAnticoncepcion() {
		return anticoncepcion;
	}

	public void setAnticoncepcion(boolean anticoncepcion) {
		this.anticoncepcion = anticoncepcion;
	}

	public boolean isVademecumGral() {
		return vademecumGral;
	}

	public void setVademecumGral(boolean vademecumGral) {
		this.vademecumGral= vademecumGral;
	}
		  
	public boolean isAltaSSS() {
		return altaSSS;
	}
	public void setAltaSSS(boolean altaSSS) {
		this.altaSSS = altaSSS;
	}
	public boolean isBajaSSS() {
		return bajaSSS;
	}
	public void setBajaSSS(boolean bajaSSS) {
		this.bajaSSS = bajaSSS;
	}
	public Date getPeriodoAltasBajas() {
		return periodoAltasBajas;
	}
	public void setPeriodoAltasBajas(Date periodoAltasBajas) {
		this.periodoAltasBajas = periodoAltasBajas;
	}

	public String getOrigenDeLosDatos() {
		return origenDeLosDatos;
	}

	public void setOrigenDeLosDatos(String origenDeLosDatos) {
		this.origenDeLosDatos = origenDeLosDatos;
	}
 
	public BigDecimal getPrecioAlPublicoSss() {
		return precioAlPublicoSss;
	}

	public void setPrecioAlPublicoSss(BigDecimal precioAlPublicoSss) {
		this.precioAlPublicoSss = precioAlPublicoSss;
	}

	public BigDecimal getPrecioAcargoOsocialSss() {
		return precioAcargoOsocialSss;
	}

	public void setPrecioAcargoOsocialSss(BigDecimal precioAcargoOsocialSss) {
		this.precioAcargoOsocialSss = precioAcargoOsocialSss;
	}

	public BigDecimal getPrecioManualDat() {
		return precioManualDat;
	}

	public void setPrecioManualDat(BigDecimal precioManualDat) {
		this.precioManualDat = precioManualDat;
	}

	public BigDecimal getPrecioAlPublico() {
		return precioAlPublico;
	}

	public void setPrecioAlPublico(BigDecimal precioAlPublico) {
		this.precioAlPublico = precioAlPublico;
	}

	public int getUnidades() {
		return unidades;
	}

	public void setUnidades(int unidades) {
		this.unidades = unidades;
	}

	public Date getPeriodo () {
		return periodo ;
	}
	
	public void setPeriodo (Date fecha) {
		this.periodo = fecha;
	}
	
	public boolean isNuevaAltaDeLaSss() {
		return nuevaAltaSss;
	}

	public void setNuevaAltaDeLaSss(boolean nuevaAltaSss) {
		this.nuevaAltaSss = nuevaAltaSss;
	}

	/*public int getCantidadGenericos() {
		return cantidadGenericos;
	}

	public void setCantidadGenericos(int cantidadGenericos) {
		this.cantidadGenericos = cantidadGenericos;
	}

	
	public int getTipoDatoVademecum() {
		return tipoDatoVademecum;
	}

	public void setTipoDatoVademecum(int tipoDatoVademecum) {
		this.tipoDatoVademecum = tipoDatoVademecum;
	}
*/
public static Vademecum  getMappingPreciosHistoricos(ResultSet rs, String prefix) throws SQLException {
		
		Vademecum vademecum= new Vademecum();
		
		vademecum.setPeriodo(rs.getDate(prefix+"periodo"));
		vademecum.setRegistro(rs.getInt(prefix+"nro_registro"));
		vademecum.setTroquel(rs.getInt(prefix+"troquel"));
		vademecum.setUnidades(rs.getInt(prefix+"unidades"));		
		vademecum.setPrecioAcargoOsocialSss(rs.getBigDecimal(prefix+"precio_acargos_sss"));
		vademecum.setPrecioAlPublicoSss(rs.getBigDecimal(prefix+"precio_pvp_sss"));
		vademecum.setPrecioManualDat(rs.getBigDecimal(prefix+"precio_manual_dat"));
		
		return vademecum;
		
	}

	
public static ItemVademecumTotal getMappingTotal(ResultSet rs, String prefix) throws SQLException {
	ItemVademecumTotal   vademecum  = new ItemVademecumTotal();
		try {
			vademecum.setTroquel(rs.getInt(prefix+"troquel"));
			vademecum.setRegistro(rs.getInt(prefix+"nro_registro"));		 	  
			vademecum.setPresentacion(rs.getString(prefix+"presentacion"));
			vademecum.setLaboratorio(rs.getString(prefix+"laboratorio"));
			vademecum.setDroga(rs.getString(prefix+"droga"));
			vademecum.setNombre(rs.getString(prefix+"nombre"));
			vademecum.setBaja_fecha(rs.getDate(prefix+"fecha_baja"));		
			vademecum.setPeriodoAltasBajas(rs.getDate(prefix+"periodo"));
			vademecum.setAccion(rs.getString(prefix+"accion"));
			vademecum.setTotal_registros(rs.getInt(prefix+"canttotal"));
			vademecum.setOrigenDeLosDatos(rs.getString(prefix+"origendatos"));
			vademecum.setNuevaAltaDeLaSss(rs.getBoolean(prefix+"nuevoRegSss"));
		}
 catch (Exception e) {
	_log.error(e);
}
		return vademecum ;
}	
}