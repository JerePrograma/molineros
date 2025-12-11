package ar.com.ospim.procesaArchivos.beans;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import ar.com.ospim.farmacia.beans.Vademecum;

public class ArchivoVademecum {

	private static Log logger = LogFactoryUtil.getLog(ArchivoVademecum.class);
	
	private int id ;
	private String usuario;
	private Date fecha_importacion;	
	private Date periodo ;		
	private Integer cantRegManualDat;	
	private Integer cantRegSSS;
	private Integer cantRegAltas;
	private Integer cantRegBajas;
	private int estadoCierre;
	private boolean exportable;			
	  
private List<Vademecum> detalle;


public List<Vademecum> getDetalle() { 
	return detalle;
}

public void setDetalle(List<Vademecum> detalleList) { 
	this.detalle = detalleList;
}


public static ArchivoVademecum   getMapping(ResultSet rs) throws SQLException {
	ArchivoVademecum      archivo = new ArchivoVademecum    ();
	try {
		archivo.setId(rs.getInt("arch_id"));
		archivo.setUsuario (rs.getString("arch_usuario"));
		archivo.setFecha_importacion(rs.getDate("arch_fecha_importacion"));
		archivo.setPeriodo(rs.getDate("arch_periodo")); 
		archivo.setCantRegSSS(rs.getInt("arch_cantreg_sss"));
		archivo.setCantRegManualDat(rs.getInt("arch_cantreg_mandat"));
		archivo.setCantRegAltas(rs.getInt("arch_cant_altas"));
		archivo.setCantRegBajas(rs.getInt("arch_cant_bajas"));
		archivo.setEstadoCierre(rs.getInt("arch_estado"));
	    archivo.setExportable(rs.getBoolean("arch_exportable"));
	}
	catch (Exception e) {
		 logger.error("Error en el Mapping de Archivos Vademecum", e);
	}	
	return archivo;
}

public int getId() {
	return id;
}

public void setId(int id) {
	this.id = id;
}

public String getUsuario() {
	return usuario;
}

public void setUsuario(String usuario) {
	this.usuario = usuario;
}

public Date getFecha_importacion() {
	return fecha_importacion;
}

public void setFecha_importacion(Date fecha_importacion) {
	this.fecha_importacion = fecha_importacion;
}

public Date getPeriodo() {
	return periodo;
}

public void setPeriodo(Date periodo) {
	this.periodo = periodo;
}

public Integer getCantRegManualDat() {
	return cantRegManualDat;
}

public void setCantRegManualDat(Integer cantRegManualDat) {
	this.cantRegManualDat = cantRegManualDat;
}

public Integer getCantRegSSS() {
	return cantRegSSS;
}

public void setCantRegSSS(Integer cantRegSSS) {
	this.cantRegSSS = cantRegSSS;
}

public Integer getCantRegAltas() {
	return cantRegAltas;
}

public void setCantRegAltas(Integer cantRegAltas) {
	this.cantRegAltas = cantRegAltas;
}

public Integer getCantRegBajas() {
	return cantRegBajas;
}

public void setCantRegBajas(Integer cantRegBajas) {
	this.cantRegBajas = cantRegBajas;
}

public int getEstadoCierre() {
	return estadoCierre;
}

public void setEstadoCierre(int estadoCierre) {
	this.estadoCierre = estadoCierre;
}

public boolean isExportable() {
	return exportable;
}

public void setExportable(boolean exportable) {
	this.exportable = exportable;
}



}
