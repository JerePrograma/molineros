package ar.com.ospim.compras.requerimientos.beans;

import java.util.Date;

public class RequerimientoCompraPresupuesto {

    public static final int TIPO_DOCUMENTO_PRESUPUESTO = 1;
    public static final int TIPO_DOCUMENTO_ORDEN_MEDICA = 2;

    private Integer idRequerimientoPresupuesto;
    private Integer idRequerimiento;
    private Integer idPrestador;
    private Integer tipoDocumento =
            Integer.valueOf(TIPO_DOCUMENTO_PRESUPUESTO);
    private Date fechaDocumento;
    private String numeroReceta;

    private Long dlGroupId;
    private Long dlFolderId;
    private Long dlFileEntryId;
    private String dlFileUuid;

    private String nombreOriginal;
    private String nombrePersistido;
    private String titulo;
    private String descripcionPrestador;

    private Date altaFecha;
    private String altaUsr;
    private Date bajaFecha;
    private String bajaUsr;

    public RequerimientoCompraPresupuesto() {
    }

    public RequerimientoCompraPresupuesto(Integer idRequerimientoPresupuesto, Integer idRequerimiento, Integer idPrestador, Long dlGroupId, Long dlFolderId, Long dlFileEntryId, String dlFileUuid, String nombreOriginal, String nombrePersistido, String titulo, String descripcionPrestador, Date altaFecha, String altaUsr, Date bajaFecha, String bajaUsr) {
        this.idRequerimientoPresupuesto = idRequerimientoPresupuesto;
        this.idRequerimiento = idRequerimiento;
        this.idPrestador = idPrestador;
        this.dlGroupId = dlGroupId;
        this.dlFolderId = dlFolderId;
        this.dlFileEntryId = dlFileEntryId;
        this.dlFileUuid = dlFileUuid;
        this.nombreOriginal = nombreOriginal;
        this.nombrePersistido = nombrePersistido;
        this.titulo = titulo;
        this.descripcionPrestador = descripcionPrestador;
        this.altaFecha = altaFecha;
        this.altaUsr = altaUsr;
        this.bajaFecha = bajaFecha;
        this.bajaUsr = bajaUsr;
    }

    public RequerimientoCompraPresupuesto(Integer idRequerimientoPresupuesto, Integer idRequerimiento, Integer idPrestador, Integer tipoDocumento, Date fechaDocumento, Long dlGroupId, Long dlFolderId, Long dlFileEntryId, String dlFileUuid, String nombreOriginal, String nombrePersistido, String titulo, String descripcionPrestador, Date altaFecha, String altaUsr, Date bajaFecha, String bajaUsr) {
        this(idRequerimientoPresupuesto, idRequerimiento, idPrestador, dlGroupId, dlFolderId, dlFileEntryId, dlFileUuid, nombreOriginal, nombrePersistido, titulo, descripcionPrestador, altaFecha, altaUsr, bajaFecha, bajaUsr);
        this.tipoDocumento = tipoDocumento;
        this.fechaDocumento = fechaDocumento;
    }

    public Integer getIdRequerimientoPresupuesto() {
        return idRequerimientoPresupuesto;
    }

    public void setIdRequerimientoPresupuesto(Integer idRequerimientoPresupuesto) {
        this.idRequerimientoPresupuesto = idRequerimientoPresupuesto;
    }

    public Integer getIdRequerimiento() {
        return idRequerimiento;
    }

    public void setIdRequerimiento(Integer idRequerimiento) {
        this.idRequerimiento = idRequerimiento;
    }

    public Integer getIdPrestador() {
        return idPrestador;
    }

    public void setIdPrestador(Integer idPrestador) {
        this.idPrestador = idPrestador;
    }

    public Integer getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(Integer tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public Date getFechaDocumento() {
        return fechaDocumento;
    }

    public void setFechaDocumento(Date fechaDocumento) {
        this.fechaDocumento = fechaDocumento;
    }

    public String getNumeroReceta() {
        return numeroReceta;
    }

    public void setNumeroReceta(String numeroReceta) {
        this.numeroReceta = numeroReceta;
    }

    public Long getDlGroupId() {
        return dlGroupId;
    }

    public void setDlGroupId(Long dlGroupId) {
        this.dlGroupId = dlGroupId;
    }

    public Long getDlFolderId() {
        return dlFolderId;
    }

    public void setDlFolderId(Long dlFolderId) {
        this.dlFolderId = dlFolderId;
    }

    public Long getDlFileEntryId() {
        return dlFileEntryId;
    }

    public void setDlFileEntryId(Long dlFileEntryId) {
        this.dlFileEntryId = dlFileEntryId;
    }

    public String getDlFileUuid() {
        return dlFileUuid;
    }

    public void setDlFileUuid(String dlFileUuid) {
        this.dlFileUuid = dlFileUuid;
    }

    public String getNombreOriginal() {
        return nombreOriginal;
    }

    public void setNombreOriginal(String nombreOriginal) {
        this.nombreOriginal = nombreOriginal;
    }

    public String getNombrePersistido() {
        return nombrePersistido;
    }

    public void setNombrePersistido(String nombrePersistido) {
        this.nombrePersistido = nombrePersistido;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcionPrestador() {
        return descripcionPrestador;
    }

    public void setDescripcionPrestador(String descripcionPrestador) {
        this.descripcionPrestador = descripcionPrestador;
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

    public boolean isActivo() {
        return bajaFecha == null;
    }
}
