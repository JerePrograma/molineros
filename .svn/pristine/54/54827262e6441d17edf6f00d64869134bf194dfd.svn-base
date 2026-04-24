package ar.com.ospim.crm.beans;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class CRMEstadisticaCierre implements Serializable {


	/**
	 * 
	 */
	private static final long serialVersionUID = -6109069341160524578L;
	
	private Date altaFecha; // fechaCarga
	private Date modiFecha; // fechaCierre
	
	public CRMEstadisticaCierre(){
		super();
	}
	
	public CRMEstadisticaCierre(Date alta, Date cierre) {
		
		super();
		this.altaFecha = alta;
		this.modiFecha = cierre;
	}
	
	public Date getAltaFecha() {
		return altaFecha;
	}

	public void setAltaFecha(Date altaFecha) {
		this.altaFecha = altaFecha;
	}

	public Date getModiFecha() {
		return modiFecha;
	}

	public void setModiFecha(Date modiFecha) {
		this.modiFecha = modiFecha;
	}

	public static CRMEstadisticaCierre getMapping(String prefix, ResultSet rs) throws SQLException{
		
		CRMEstadisticaCierre crmEst = new CRMEstadisticaCierre();
		
		crmEst.setAltaFecha(rs.getTimestamp("alta_fecha"));
		crmEst.setModiFecha(rs.getTimestamp("modi_fecha"));
		
		return crmEst;
		
	}
	
}
