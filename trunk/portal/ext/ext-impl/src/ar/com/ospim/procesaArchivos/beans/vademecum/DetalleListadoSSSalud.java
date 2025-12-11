package ar.com.ospim.procesaArchivos.beans.vademecum;

import java.math.BigDecimal;
import java.text.ParseException;

import ar.com.ospim.afiliados.services.AporteServiceImpl;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

public class DetalleListadoSSSalud {
	private static Log _log = LogFactoryUtil.getLog(DetalleListadoSSSalud.class);
	
	private int id;
	private int registro;
	private String atc;
	private String generico;
	private String nombre;
	private String presentacion;
	private BigDecimal pvp;
	private BigDecimal acargoos;
	private BigDecimal acargoafil;
	private String laboratorio;
	private BigDecimal cober;
	private int grupoter;
	private String observaciones;

	public DetalleListadoSSSalud() {
		super();
		
	}
	
	public DetalleListadoSSSalud(String line){
		super();
		
		try{
			String splitarray[] = line.split("\\t");
			this.id = Integer.parseInt(splitarray[0]);
			this.atc = splitarray[1].trim();
			this.generico = splitarray[2].trim();
			this.nombre = splitarray[3].trim();
			this.presentacion = splitarray[4].trim();
			this.pvp = new BigDecimal(splitarray[5].substring(1).replace(".", "").replace(",", ".").trim());
			this.acargoos = new BigDecimal(splitarray[6].substring(1).replace(".", "").replace(",", ".").trim());
			this.acargoafil = new BigDecimal(splitarray[7].substring(1).replace(".", "").replace(",", ".").trim());
			this.laboratorio = splitarray[8].trim();
			this.registro = Integer.parseInt(splitarray[9].trim());
			this.cober = new BigDecimal(splitarray[10].substring(2).replace(".", "").replace(",", ".").trim());
			this.grupoter = Integer.parseInt(splitarray[11]);
			try {
				this.observaciones = splitarray[12];
			} catch (ArrayIndexOutOfBoundsException e) {
	
			}
			
		}catch (NumberFormatException e) {
			_log.error("ERROR EN REGISTRO: "+this.registro,e);	
		}catch (Exception e){
			_log.error("ERROR EN REGISTRO: "+this.registro,e);
		}

	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getRegistro() {
		return registro;
	}

	public void setRegistro(int registro) {
		this.registro = registro;
	}

	public String getAtc() {
		return atc;
	}

	public void setAtc(String atc) {
		this.atc = atc;
	}

	public String getGenerico() {
		return generico;
	}

	public void setGenerico(String generico) {
		this.generico = generico;
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

	public BigDecimal getPvp() {
		return pvp;
	}

	public void setPvp(BigDecimal pvp) {
		this.pvp = pvp;
	}

	public BigDecimal getAcargoos() {
		return acargoos;
	}

	public void setAcargoos(BigDecimal acargoos) {
		this.acargoos = acargoos;
	}

	public BigDecimal getAcargoafil() {
		return acargoafil;
	}

	public void setAcargoafil(BigDecimal acargoafil) {
		this.acargoafil = acargoafil;
	}

	public String getLaboratorio() {
		return laboratorio;
	}

	public void setLaboratorio(String laboratorio) {
		this.laboratorio = laboratorio;
	}

	public BigDecimal getCober() {
		return cober;
	}

	public void setCober(BigDecimal cober) {
		this.cober = cober;
	}

	public int getGrupoter() {
		return grupoter;
	}

	public void setGrupoter(int grupoter) {
		this.grupoter = grupoter;
	}

	public String getObservaciones() {
		return observaciones;
	}

	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}

}