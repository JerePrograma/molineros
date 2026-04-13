package ar.com.ospim.crm.beans;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CRMEstadisticaRendimiento implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6418564648504738264L;
	
	private String sector;
    private int totalEstadoPendiente;
    private int totalEstadoDerivado;
    private int totalEstadoCerrado;
    private int totalDiasResolucionSector; 
    private int totalContactosSector;
    private int promedioResolucion;
    private int total;
    
	
	public CRMEstadisticaRendimiento(String sector, int total_estado_pendiente,
			int total_estado_derivado, int total_estado_cerrado,
			int total_dias_resolucion_sector, int total_contactos_sector,
			int promedio_resolucion, int total) {
		super();
		this.sector = sector;
		this.totalEstadoPendiente = total_estado_pendiente;
		this.totalEstadoDerivado = total_estado_derivado;
		this.totalEstadoCerrado = total_estado_cerrado;
		this.totalDiasResolucionSector = total_dias_resolucion_sector;
		this.totalContactosSector = total_contactos_sector;
		this.promedioResolucion = promedio_resolucion;
		this.total = total;
	}
	
	@Override
	public String toString() {
		return "CRMEstadisticaRendimiento [sector=" + sector
				+ ", total_estado_pendiente=" + totalEstadoPendiente
				+ ", total_estado_derivado=" + totalEstadoDerivado
				+ ", total_estado_cerrado=" + totalEstadoCerrado
				+ ", total_dias_resolucion_sector="
				+ totalDiasResolucionSector + ", total_contactos_sector="
				+ totalContactosSector + ", promedio_resolucion="
				+ promedioResolucion + "]";
	}

	public String getSector() {
		return sector;
	}

	public void setSector(String sector) {
		this.sector = sector;
	}

	public int getTotalEstadoPendiente() {
		return totalEstadoPendiente;
	}

	public void setTotalEstadoPendiente(int totalEstadoPendiente) {
		this.totalEstadoPendiente = totalEstadoPendiente;
	}

	public int getTotalEstadoDerivado() {
		return totalEstadoDerivado;
	}

	public void setTotalEstadoDerivado(int totalEstadoDerivado) {
		this.totalEstadoDerivado = totalEstadoDerivado;
	}

	public int getTotalEstadoCerrado() {
		return totalEstadoCerrado;
	}

	public void setTotalEstadoCerrado(int totalEstadoCerrado) {
		this.totalEstadoCerrado = totalEstadoCerrado;
	}

	public int getTotalDiasResolucionSector() {
		return totalDiasResolucionSector;
	}

	public void setTotalDiasResolucionSector(int totalDiasResolucionSector) {
		this.totalDiasResolucionSector = totalDiasResolucionSector;
	}

	public int getTotalContactosSector() {
		return totalContactosSector;
	}

	public void setTotalContactosSector(int totalContactosSector) {
		this.totalContactosSector = totalContactosSector;
	}

	public int getPromedioResolucion() {
		return promedioResolucion;
	}

	public void setPromedioResolucion(int promedioResolucion) {
		this.promedioResolucion = promedioResolucion;
	}
    
	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}
	
	public static CRMEstadisticaRendimiento getMapping(String prefix, ResultSet rs) throws SQLException{
		
		CRMEstadisticaRendimiento estadRendim = new CRMEstadisticaRendimiento(
				rs.getString(prefix + "sector"), 
				rs.getInt(prefix + "total_estado_pendiente"), 
				rs.getInt(prefix + "total_estado_derivado"), 
				rs.getInt(prefix + "total_estado_cerrado"),
				rs.getInt(prefix + "total_dias_resolucion_sector"),
				rs.getInt(prefix + "total_contactos_sector"),
				rs.getInt(prefix + "promedio_resolucion"),
				rs.getInt(prefix + "total_contactos"));
		
		return estadRendim;
	}
    
}
