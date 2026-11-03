package ar.com.ospim.compras.requerimientos.beans;

import java.util.Date;

/** Datos de seguimiento propios de una OC emitida. */
public class RequerimientoCompraOrdenSuministro {

    private int idOrdenCompra;
    private Date fechaContacto;
    private String personaContactada;
    private String medioContacto;
    private String detalle;
    private String respuesta1;
    private String respuesta2;
    private String respuesta3;
    private String respuesta4;
    private String respuesta5;
    private int versionEdicion;
    private Date altaFecha;
    private String altaUsr;
    private Date modiFecha;
    private String modiUsr;

    public int getIdOrdenCompra() { return idOrdenCompra; }
    public void setIdOrdenCompra(int idOrdenCompra) { this.idOrdenCompra = idOrdenCompra; }
    public Date getFechaContacto() { return fechaContacto; }
    public void setFechaContacto(Date fechaContacto) { this.fechaContacto = fechaContacto; }
    public String getPersonaContactada() { return personaContactada; }
    public void setPersonaContactada(String personaContactada) { this.personaContactada = personaContactada; }
    public String getMedioContacto() { return medioContacto; }
    public void setMedioContacto(String medioContacto) { this.medioContacto = medioContacto; }
    public String getDetalle() { return detalle; }
    public void setDetalle(String detalle) { this.detalle = detalle; }
    public String getRespuesta1() { return respuesta1; }
    public void setRespuesta1(String respuesta1) { this.respuesta1 = respuesta1; }
    public String getRespuesta2() { return respuesta2; }
    public void setRespuesta2(String respuesta2) { this.respuesta2 = respuesta2; }
    public String getRespuesta3() { return respuesta3; }
    public void setRespuesta3(String respuesta3) { this.respuesta3 = respuesta3; }
    public String getRespuesta4() { return respuesta4; }
    public void setRespuesta4(String respuesta4) { this.respuesta4 = respuesta4; }
    public String getRespuesta5() { return respuesta5; }
    public void setRespuesta5(String respuesta5) { this.respuesta5 = respuesta5; }
    public int getVersionEdicion() { return versionEdicion; }
    public void setVersionEdicion(int versionEdicion) { this.versionEdicion = versionEdicion; }
    public Date getAltaFecha() { return altaFecha; }
    public void setAltaFecha(Date altaFecha) { this.altaFecha = altaFecha; }
    public String getAltaUsr() { return altaUsr; }
    public void setAltaUsr(String altaUsr) { this.altaUsr = altaUsr; }
    public Date getModiFecha() { return modiFecha; }
    public void setModiFecha(Date modiFecha) { this.modiFecha = modiFecha; }
    public String getModiUsr() { return modiUsr; }
    public void setModiUsr(String modiUsr) { this.modiUsr = modiUsr; }
}
