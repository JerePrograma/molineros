package ar.com.ospim.liquidaciones.beans;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CartillaConvenioRow implements Serializable {

    private static final long serialVersionUID = 1L;

    private String planDescripcion;
    private String prestadorDescripcion;
    private String cuitPrestador;
    private String zona;
    private String especialidad;
    private String domicilio;
    private String telefono;
    private String localidad;
    private String provincia;

    public CartillaConvenioRow() {
        super();
    }

    public CartillaConvenioRow(String planDescripcion,
                               String prestadorDescripcion,
                               String cuitPrestador,
                               String zona,
                               String especialidad,
                               String domicilio,
                               String telefono,
                               String localidad,
                               String provincia) {
        this.planDescripcion = planDescripcion;
        this.prestadorDescripcion = prestadorDescripcion;
        this.cuitPrestador = cuitPrestador;
        this.zona = zona;
        this.especialidad = especialidad;
        this.domicilio = domicilio;
        this.telefono = telefono;
        this.localidad = localidad;
        this.provincia = provincia;
    }

    public static CartillaConvenioRow getMapping(ResultSet rs, String prefix) throws SQLException {
        CartillaConvenioRow row = new CartillaConvenioRow();
        row.setPlanDescripcion(rs.getString(prefix + "plan_descripcion"));
        row.setPrestadorDescripcion(rs.getString(prefix + "prestador_descripcion"));
        row.setCuitPrestador(rs.getString(prefix + "cuit_prestador"));
        row.setZona(rs.getString(prefix + "zona"));
        row.setEspecialidad(rs.getString(prefix + "especialidad"));
        row.setDomicilio(rs.getString(prefix + "domicilio"));
        row.setTelefono(rs.getString(prefix + "telefono"));
        row.setLocalidad(rs.getString(prefix + "localidad"));
        row.setProvincia(rs.getString(prefix + "provincia"));
        return row;
    }

    public String getPlanDescripcion() {
        return planDescripcion;
    }

    public void setPlanDescripcion(String planDescripcion) {
        this.planDescripcion = planDescripcion;
    }

    public String getPrestadorDescripcion() {
        return prestadorDescripcion;
    }

    public void setPrestadorDescripcion(String prestadorDescripcion) {
        this.prestadorDescripcion = prestadorDescripcion;
    }

    public String getCuitPrestador() {
        return cuitPrestador;
    }

    public void setCuitPrestador(String cuitPrestador) {
        this.cuitPrestador = cuitPrestador;
    }

    public String getZona() {
        return zona;
    }

    public void setZona(String zona) {
        this.zona = zona;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    public String getDomicilio() {
        return domicilio;
    }

    public void setDomicilio(String domicilio) {
        this.domicilio = domicilio;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getLocalidad() {
        return localidad;
    }

    public void setLocalidad(String localidad) {
        this.localidad = localidad;
    }

    public String getProvincia() {
        return provincia;
    }

    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }

    @Override
    public String toString() {
        return "CartillaConvenioRow{" +
                "planDescripcion='" + planDescripcion + '\'' +
                ", prestadorDescripcion='" + prestadorDescripcion + '\'' +
                ", cuitPrestador='" + cuitPrestador + '\'' +
                ", zona='" + zona + '\'' +
                ", especialidad='" + especialidad + '\'' +
                ", domicilio='" + domicilio + '\'' +
                ", telefono='" + telefono + '\'' +
                ", localidad='" + localidad + '\'' +
                ", provincia='" + provincia + '\'' +
                '}';
    }
}