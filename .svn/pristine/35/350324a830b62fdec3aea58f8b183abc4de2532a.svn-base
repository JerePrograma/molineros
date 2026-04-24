package ar.com.ospim.autorizaciones.beans;
import java.io.Serializable;
import java.util.Date;

public class BusquedaSituacionMedicaFiltro implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1317878718464195850L;

	private Date fechaDesde;
	private Date fechaHasta;
	private String estado;
	private int registrosTotal;
	private final int registrosPorPagina = 50;
	private int inte; 
	private String cuilTitular;
	private int tipoSituMedica;
	private int  pagina; 
	
	public BusquedaSituacionMedicaFiltro(Date fechaDesde, Date fechaHasta, int inte, String cuilTitular, int tipoSituMedica, int  pagina)	{
		this.setFechaDesde(fechaDesde); 
		this.setFechaHasta(fechaHasta);
		this.settipoSituMedica(tipoSituMedica);
		this.setInte(inte);
		this.setCuilTitular(cuilTitular);
		this.setPagina(pagina);
	}
	
	public Date getFechaDesde() {
		return fechaDesde;
	}
	public void setFechaDesde(Date fechaDesde) {
		this.fechaDesde = fechaDesde;
	}
	public Date getFechaHasta() {
		return fechaHasta;
	}
	public void setFechaHasta(Date fechaHasta) {
		this.fechaHasta = fechaHasta;
	}
	public String getEstado() {
		return estado;
	}
	public void setEstado(String estado) {
		this.estado = estado;
	}
	public int getPagina() {
		return pagina;
	}
	public void setPagina(int pagina) {
		this.pagina = pagina;
	}
	public String getCuilTitular() {
		return cuilTitular;
	}
	public void setCuilTitular(String cuit) {
		this.cuilTitular= cuit;
	}
	public int getRegistrosTotal() {
		return registrosTotal;
	}
	public void setRegistrosTotal(int registrosTotal) {
		this.registrosTotal = registrosTotal;
	}
	public int getRegistrosPorPagina() {
		return registrosPorPagina;
	}

	public int getInte () {
		return inte;
	}
	public void setInte (int inteAfiliado) {
		this.inte = inteAfiliado;
	}
	
	public int gettipoSituMedica() {
		return tipoSituMedica;
	}
	public void settipoSituMedica(int tipoSituMedicaAfiliado ) {
		this.tipoSituMedica= tipoSituMedicaAfiliado;
	}

	
	
}
