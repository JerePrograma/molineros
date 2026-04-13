package ar.com.ospim.liquidaciones.beans;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

public class CartillaConvenioRow implements Serializable {

    private static final long serialVersionUID = 5487963214789654123L;

    private Integer idConvenioPrest;
    private Integer idPrestador;
    private Integer idPlan;
    private Integer idLugarAtencion;
    private Integer idEspecialidad;

    private String cuitPrestador;
    private String prestadorDescripcion;
    private String planDescripcion;

    private String zona;
    private String especialidad;
    private String institucion;
    private String domicilio;
    private String telefono;
    private String localidad;
    private String provincia;

    private Date vigenciaConvenioDesde;
    private Date vigenciaConvenioHasta;
    private Date vigenciaPlanDesde;
    private Date vigenciaPlanHasta;
    private Date vigenciaLugarDesde;
    private Date vigenciaLugarHasta;

    public CartillaConvenioRow() {
        super();
    }

    public static CartillaConvenioRow getMapping(ResultSet rs, String prefix) throws SQLException {
        CartillaConvenioRow row = new CartillaConvenioRow();

        row.setIdConvenioPrest(getNullableInteger(rs, prefix + "id_convenio_prest"));
        row.setIdPrestador(getNullableInteger(rs, prefix + "id_prestador"));
        row.setIdPlan(getNullableInteger(rs, prefix + "id_plan"));
        row.setIdLugarAtencion(getNullableInteger(rs, prefix + "id_lugar_atencion"));
        row.setIdEspecialidad(getNullableInteger(rs, prefix + "id_especialidad"));

        row.setCuitPrestador(rs.getString(prefix + "cuit_prestador"));
        row.setPrestadorDescripcion(rs.getString(prefix + "prestador_descripcion"));
        row.setPlanDescripcion(rs.getString(prefix + "plan_descripcion"));

        row.setZona(rs.getString(prefix + "zona"));
        row.setEspecialidad(rs.getString(prefix + "especialidad"));
        row.setInstitucion(rs.getString(prefix + "institucion"));
        row.setDomicilio(rs.getString(prefix + "domicilio"));
        row.setTelefono(rs.getString(prefix + "telefono"));
        row.setLocalidad(rs.getString(prefix + "localidad"));
        row.setProvincia(rs.getString(prefix + "provincia"));

        row.setVigenciaConvenioDesde(getNullableTimestamp(rs, prefix + "vigencia_convenio_desde"));
        row.setVigenciaConvenioHasta(getNullableTimestamp(rs, prefix + "vigencia_convenio_hasta"));
        row.setVigenciaPlanDesde(getNullableTimestamp(rs, prefix + "vigencia_plan_desde"));
        row.setVigenciaPlanHasta(getNullableTimestamp(rs, prefix + "vigencia_plan_hasta"));
        row.setVigenciaLugarDesde(getNullableTimestamp(rs, prefix + "vigencia_lugar_desde"));
        row.setVigenciaLugarHasta(getNullableTimestamp(rs, prefix + "vigencia_lugar_hasta"));

        return row;
    }

    private static Integer getNullableInteger(ResultSet rs, String column) throws SQLException {
        int value = rs.getInt(column);
        return rs.wasNull() ? null : Integer.valueOf(value);
    }

    private static Date getNullableTimestamp(ResultSet rs, String column) throws SQLException {
        Timestamp ts = rs.getTimestamp(column);
        return ts != null ? new Date(ts.getTime()) : null;
    }

    public Integer getIdConvenioPrest() {
        return idConvenioPrest;
    }

    public void setIdConvenioPrest(Integer idConvenioPrest) {
        this.idConvenioPrest = idConvenioPrest;
    }

    public Integer getIdPrestador() {
        return idPrestador;
    }

    public void setIdPrestador(Integer idPrestador) {
        this.idPrestador = idPrestador;
    }

    public Integer getIdPlan() {
        return idPlan;
    }

    public void setIdPlan(Integer idPlan) {
        this.idPlan = idPlan;
    }

    public Integer getIdLugarAtencion() {
        return idLugarAtencion;
    }

    public void setIdLugarAtencion(Integer idLugarAtencion) {
        this.idLugarAtencion = idLugarAtencion;
    }

    public Integer getIdEspecialidad() {
        return idEspecialidad;
    }

    public void setIdEspecialidad(Integer idEspecialidad) {
        this.idEspecialidad = idEspecialidad;
    }

    public String getCuitPrestador() {
        return cuitPrestador;
    }

    public void setCuitPrestador(String cuitPrestador) {
        this.cuitPrestador = cuitPrestador;
    }

    public String getPrestadorDescripcion() {
        return prestadorDescripcion;
    }

    public void setPrestadorDescripcion(String prestadorDescripcion) {
        this.prestadorDescripcion = prestadorDescripcion;
    }

    public String getPlanDescripcion() {
        return planDescripcion;
    }

    public void setPlanDescripcion(String planDescripcion) {
        this.planDescripcion = planDescripcion;
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

    public String getInstitucion() {
        return institucion;
    }

    public void setInstitucion(String institucion) {
        this.institucion = institucion;
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

    public Date getVigenciaConvenioDesde() {
        return vigenciaConvenioDesde;
    }

    public void setVigenciaConvenioDesde(Date vigenciaConvenioDesde) {
        this.vigenciaConvenioDesde = vigenciaConvenioDesde;
    }

    public Date getVigenciaConvenioHasta() {
        return vigenciaConvenioHasta;
    }

    public void setVigenciaConvenioHasta(Date vigenciaConvenioHasta) {
        this.vigenciaConvenioHasta = vigenciaConvenioHasta;
    }

    public Date getVigenciaPlanDesde() {
        return vigenciaPlanDesde;
    }

    public void setVigenciaPlanDesde(Date vigenciaPlanDesde) {
        this.vigenciaPlanDesde = vigenciaPlanDesde;
    }

    public Date getVigenciaPlanHasta() {
        return vigenciaPlanHasta;
    }

    public void setVigenciaPlanHasta(Date vigenciaPlanHasta) {
        this.vigenciaPlanHasta = vigenciaPlanHasta;
    }

    public Date getVigenciaLugarDesde() {
        return vigenciaLugarDesde;
    }

    public void setVigenciaLugarDesde(Date vigenciaLugarDesde) {
        this.vigenciaLugarDesde = vigenciaLugarDesde;
    }

    public Date getVigenciaLugarHasta() {
        return vigenciaLugarHasta;
    }

    public void setVigenciaLugarHasta(Date vigenciaLugarHasta) {
        this.vigenciaLugarHasta = vigenciaLugarHasta;
    }

    @Override
    public String toString() {
        return "CartillaConvenioRow [idConvenioPrest=" + idConvenioPrest
                + ", idPrestador=" + idPrestador
                + ", idPlan=" + idPlan
                + ", idLugarAtencion=" + idLugarAtencion
                + ", idEspecialidad=" + idEspecialidad
                + ", cuitPrestador=" + cuitPrestador
                + ", prestadorDescripcion=" + prestadorDescripcion
                + ", planDescripcion=" + planDescripcion
                + ", zona=" + zona
                + ", especialidad=" + especialidad
                + ", institucion=" + institucion
                + ", domicilio=" + domicilio
                + ", telefono=" + telefono
                + ", localidad=" + localidad
                + ", provincia=" + provincia + "]";
    }
}