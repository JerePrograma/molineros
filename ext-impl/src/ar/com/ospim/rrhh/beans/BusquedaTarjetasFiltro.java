package ar.com.ospim.rrhh.beans;
import java.io.Serializable;


public class BusquedaTarjetasFiltro implements Serializable {

	
	private static final long serialVersionUID = -1317878718464195850L;
	
	private String estado;
	private int nroTarjeta ;
	private final int registrosPorPagina = 50;
	private String nombre ;
	private String apellido ;
	private String entidad;
	private String sector;
	private int  pagina;
	private int totalRegistros;
	private int legajo ;
	
	public BusquedaTarjetasFiltro( int nroTarjeta , String nombre , String  apellido, String  entidad , String  sector , int pagina , int legajo)	{
        this.setApellido(apellido);
        this.setEntidad(entidad);
        this.setEstado(sector);
        this.setNombre(nombre);
        this.setNroTarjeta(nroTarjeta);
        this.setSector(sector);
        this.setPagina(pagina);
        this.setLegajo(legajo);
        
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public int getNroTarjeta() {
		return nroTarjeta;
	}

	public void setNroTarjeta(int nroTarjeta) {
		this.nroTarjeta = nroTarjeta;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getApellido() {
		return apellido;
	}

	public void setApellido(String apellido) {
		this.apellido = apellido;
	}

	public String getEntidad() {
		return entidad;
	}

	public void setEntidad(String entidad) {
		this.entidad = entidad;
	}

	public String getSector() {
		return sector;
	}

	public void setSector(String sector) {
		this.sector = sector;
	}

	public int getPagina() {
		return pagina;
	}

	public void setPagina(int pagina) {
		this.pagina = pagina;
	}
	
	public int getTotalRegistros() {
		return totalRegistros;
	}

	public void setTotalRegistros(int totalRegistros) {
		this.totalRegistros = totalRegistros;
	}

	public int getLegajo() {
		return legajo;
	}

	public void setLegajo(int legajo) {
		this.legajo = legajo;
	}	
	
	
}
