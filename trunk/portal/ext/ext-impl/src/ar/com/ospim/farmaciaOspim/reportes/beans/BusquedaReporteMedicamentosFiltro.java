package ar.com.ospim.farmaciaOspim.reportes.beans;

import java.io.Serializable;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;


public class BusquedaReporteMedicamentosFiltro implements Serializable {
	
	private static final long serialVersionUID = 7660154674673345362L;
	
	private int troquel;
	private int registro;
	private Date periodo ;
	private Date fecha;	
	private String codBarra;
	private String droga;
	private String nombre;
	private String presentacion;
	private String laboratorio;
	private boolean manualDat;
	private boolean incluyeBajas;
	
	
	public BusquedaReporteMedicamentosFiltro() {
		
	}
	
	public boolean isManualDat() {
		return manualDat;
	}

	public void setManualDat(boolean manualDat) {
		this.manualDat = manualDat;
	}

	public boolean isIncluyeBajas() {
		return incluyeBajas;
	}

	public void setIncluyeBajas(boolean incluyeBajas) {
		this.incluyeBajas = incluyeBajas;
	}
	
	public int getTroquel() {
		return troquel;
	}

	public void setTroquel(int troquel) {
		this.troquel = troquel;
	}

	public int getRegistro() {
		return registro;
	}

	public void setRegistro(int registro) {
		this.registro = registro;
	}
	public Date getPeriodo() {
		return periodo;
	}
	public void setPeriodo(Date periodo) {
		this.periodo = periodo;
	}
	public Date getFecha() {
		return fecha;
	}
	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}
	public String getCod_barra() {
		return codBarra;
	}
	public void setCod_barra(String cod_barra) {
		this.codBarra= cod_barra;
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


	public String getDescripcionFiltros(){
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		
		String descripcion = "";
		
		if (null != periodo ) {
			descripcion += "Criterios de la Búsqueda:  Periodo : " + sdf.format(periodo);
		} else {					
			descripcion += "Criterios de la Búsqueda:" ;	
		}
		
		descripcion +=  (registro==0?"":", Registro: " +registro);
		
		descripcion +=  (troquel==0?" ": ", Nro Troquel: " + troquel);
							
		descripcion +=  (nombre==null||nombre.equals("") ?" ":", Nombre: "  + nombre);
		
		descripcion +=  (codBarra==null||codBarra.equals("") ?" ":", Codigo Barra: " +codBarra);

		descripcion +=  (presentacion==null||presentacion.equals("") ?" ":", Presentación: " +presentacion);

		descripcion +=  (laboratorio==null||laboratorio.equals("")?" ":", Laboratorio: " +laboratorio);

		descripcion +=  (droga==null||droga.equals("")?"":", Droga: " +droga);

		descripcion += ", Manual Dat: " + (manualDat?"SI":"NO");

		
		return descripcion;
		
	}
}
