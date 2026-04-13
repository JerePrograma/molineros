package ar.com.ospim.afiliados.beans;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;


public class DetalleFechasSuper   implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Date fechaPresSuper ;
	private Date fechaBajaSuper ;
	private Date fechaModiSuper ;
	private Date fechaPresentacionSuper ;
	
	public DetalleFechasSuper(){} 

	public DetalleFechasSuper(Date fechaBajaSss, Date fechaModiSss, Date fechaPresSss, Date fechaPresentacionSss ){
		this.fechaBajaSuper =fechaBajaSss;
		this.fechaModiSuper =fechaModiSss ;
		this.fechaPresentacionSuper=fechaPresentacionSss;
		this.fechaPresSuper = fechaPresSss;
	}

	public Date getFechaPresSuper() {
		return fechaPresSuper;
	}

	public void setFechaPresSuper(Date fechaPresSuper) {
		this.fechaPresSuper = fechaPresSuper;
	}

	public Date getFechaBajaSuper() {
		return fechaBajaSuper;
	}

	public void setFechaBajaSuper(Date fechaBajaSuper) {
		this.fechaBajaSuper = fechaBajaSuper;
	}

	public Date getFechaModiSuper() {
		return fechaModiSuper;
	}

	public void setFechaModiSuper(Date fechaModiSuper) {
		this.fechaModiSuper = fechaModiSuper;
	}

	public Date getFechaPresentacionSuper() {
		return fechaPresentacionSuper;
	}

	public void setFechaPresentacionSuper(Date fechaPresentacionSuper) {
		this.fechaPresentacionSuper = fechaPresentacionSuper;
	}
		

	public static DetalleFechasSuper getMapping(String prefijo, ResultSet rs) throws SQLException{
     	DetalleFechasSuper detFechasSss = new DetalleFechasSuper();
     	 
     	detFechasSss.setFechaBajaSuper(rs.getDate(prefijo+"fecha_baja_super"));
     	detFechasSss.setFechaModiSuper(rs.getDate(prefijo+"fecha_mod_super"));
     	detFechasSss.setFechaPresentacionSuper(rs.getDate(prefijo+"fecha_pres_super"));
     	detFechasSss.setFechaPresSuper(rs.getDate(prefijo+"pres_ssalud_fecha"));
     	
		return detFechasSss;
	}

}
