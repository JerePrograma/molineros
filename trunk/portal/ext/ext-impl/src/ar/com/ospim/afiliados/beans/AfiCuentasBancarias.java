package ar.com.ospim.afiliados.beans;

import java.io.Serializable;
import java.sql.ResultSet;
import java.util.Date;

public class AfiCuentasBancarias implements Serializable {

    private static final long serialVersionUID = 1L;
    private int id;
    private String cuilTitular;
    private int inte;
    private String apellidoAfiliado;
    private String nombreAfiliado;
    private String email;
    private boolean titular;
    private String cbu;
    private String cuilCbu;
    private String apellidoApoderado;
    private String nombreApoderado;
    private String fileCbu;
    private String fileNotaAutorizada;
    private Date altaFecha;
    private String altaUsr;
    private Date modiFecha;
    private String modiUsr;
    private Date bajaFecha;
    private String bajaUsr;

    public int getId() {
		return id;
	}
    
	public void setId(int id) {
		this.id = id;
	}

	public String getCuilTitular() {
		return cuilTitular;
	}
	
	public void setCuilTitular(String cuilTitular) {
		this.cuilTitular = cuilTitular;
	}
	
	public int getInte() {
		return inte;
	}

	public void setInte(int inte) {
		this.inte = inte;
	}

	public String getApellidoAfiliado() {
		return apellidoAfiliado;
	}

	public void setApellidoAfiliado(String apellidoAfiliado) {
		this.apellidoAfiliado = apellidoAfiliado;
	}

	public String getNombreAfiliado() {
		return nombreAfiliado;
	}

	public void setNombreAfiliado(String nombreAfiliado) {
		this.nombreAfiliado = nombreAfiliado;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	public boolean isTitular() {
		return titular;
	}

	public void setTitular(boolean titular) {
		this.titular = titular;
	}

	public String getCbu() {
		return cbu;
	}

	public void setCbu(String cbu) {
		this.cbu = cbu;
	}

	public String getCuilCbu() {
		return cuilCbu;
	}

	public void setCuilCbu(String cuilCbu) {
		this.cuilCbu = cuilCbu;
	}
	
	public String getApellidoApoderado() {
		return apellidoApoderado;
	}

	public void setApellidoApoderado(String apellidoApoderado) {
		this.apellidoApoderado = apellidoApoderado;
	}

	public String getNombreApoderado() {
		return nombreApoderado;
	}

	public void setNombreApoderado(String nombreApoderado) {
		this.nombreApoderado = nombreApoderado;
	}

	public String getFileCbu() {
		return fileCbu;
	}

	public void setFileCbu(String fileCbu) {
		this.fileCbu = fileCbu;
	}

	public String getFileNotaAutorizada() {
		return fileNotaAutorizada;
	}

	public void setFileNotaAutorizada(String fileNotaAutorizada) {
		this.fileNotaAutorizada = fileNotaAutorizada;
	}

	public Date getAltaFecha() {
		return altaFecha;
	}

	public void setAltaFecha(Date altaFecha) {
		this.altaFecha = altaFecha;
	}

	public String getAltaUsr() {
		return altaUsr;
	}

	public void setAltaUsr(String altaUsr) {
		this.altaUsr = altaUsr;
	}

	public Date getModiFecha() {
		return modiFecha;
	}

	public void setModiFecha(Date modiFecha) {
		this.modiFecha = modiFecha;
	}

	public String getModiUsr() {
		return modiUsr;
	}

	public void setModiUsr(String modiUsr) {
		this.modiUsr = modiUsr;
	}

	public Date getBajaFecha() {
		return bajaFecha;
	}

	public void setBajaFecha(Date bajaFecha) {
		this.bajaFecha = bajaFecha;
	}

	public String getBajaUsr() {
		return bajaUsr;
	}

	public void setBajaUsr(String bajaUsr) {
		this.bajaUsr = bajaUsr;
	}
	
	public static AfiCuentasBancarias getMapping(ResultSet rs, String prefix) throws Exception {
        AfiCuentasBancarias cuenta = new AfiCuentasBancarias();
        cuenta.setId(rs.getInt(prefix + "id"));
        cuenta.setCuilTitular(rs.getString(prefix + "cuil_titular"));
        cuenta.setInte(rs.getInt(prefix + "inte"));
        cuenta.setApellidoAfiliado(rs.getString(prefix + "apellido_afiliado"));
        cuenta.setNombreAfiliado(rs.getString(prefix + "nombre_afiliado"));
        cuenta.setEmail(rs.getString(prefix + "email"));
        cuenta.setTitular(rs.getBoolean(prefix + "titular"));
        cuenta.setCbu(rs.getString(prefix + "cbu"));
        cuenta.setCuilCbu(rs.getString("cuil_cbu") == null ? "" : rs.getString("cuil_cbu"));
        cuenta.setNombreApoderado(rs.getString("nombre_apoderado") == null ? "" : rs.getString("nombre_apoderado"));
        cuenta.setApellidoApoderado(rs.getString("apellido_apoderado") == null ? "" : rs.getString("apellido_apoderado"));
        cuenta.setFileCbu(rs.getString(prefix + "file_cbu"));
        cuenta.setFileNotaAutorizada(rs.getString(prefix + "file_nota_autorizada"));
        cuenta.setAltaFecha(rs.getTimestamp(prefix + "alta_fecha"));
        cuenta.setAltaUsr(rs.getString(prefix + "alta_usr"));
        cuenta.setModiFecha(rs.getTimestamp(prefix + "modi_fecha"));
        cuenta.setModiUsr(rs.getString(prefix + "modi_usr"));
        cuenta.setBajaFecha(rs.getTimestamp(prefix + "baja_fecha"));
        cuenta.setBajaUsr(rs.getString(prefix + "baja_usr"));
        return cuenta;
    }
}