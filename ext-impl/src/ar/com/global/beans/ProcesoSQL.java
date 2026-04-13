package ar.com.global.beans;

import java.text.SimpleDateFormat;
import java.util.Date;



/**
 * @author sistema-09
 * @version 1.0
 * @created 30-Jul-2010 05:27:49 p.m.
 */
public class ProcesoSQL{
	private int procid;
	private Date fechaComienzo;
	public int getProcid() {
		return procid;
	}
	public void setProcid(int procid) {
		this.procid = procid;
	}
	public Date getFechaComienzo() {
		return fechaComienzo;
	}
	public void setFechaComienzo(Date fechaComienzo) {
		this.fechaComienzo = fechaComienzo;
	}
	public String getFechaComienzoAsString(){
		SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy hh:mm");
		if(fechaComienzo!=null){
			return sdf.format(fechaComienzo);
		}else{
			return "";
		}
	}
	
	
	
}
