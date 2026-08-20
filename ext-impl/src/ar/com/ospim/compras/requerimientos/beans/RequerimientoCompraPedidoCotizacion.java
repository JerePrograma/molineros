package ar.com.ospim.compras.requerimientos.beans;

import java.util.Date;

public class RequerimientoCompraPedidoCotizacion {

    private Integer idRequerimiento;
    private Integer idPrestador;
    private Integer intento;

    private Long dlGroupId;
    private Long dlFolderId;
    private Long dlFileEntryId;
    private String dlFileUuid;

    private String nombreOriginal;
    private String nombrePersistido;
    private String titulo;

    private Date altaFecha;
    private String altaUsr;

    public Integer getIdRequerimiento() {
        return idRequerimiento;
    }

    public void setIdRequerimiento(
            Integer idRequerimiento) {

        this.idRequerimiento =
                idRequerimiento;
    }

    public Integer getIdPrestador() {
        return idPrestador;
    }

    public void setIdPrestador(
            Integer idPrestador) {

        this.idPrestador =
                idPrestador;
    }

    public Integer getIntento() {
        return intento;
    }

    public void setIntento(
            Integer intento) {

        this.intento =
                intento;
    }

    public Long getDlGroupId() {
        return dlGroupId;
    }

    public void setDlGroupId(
            Long dlGroupId) {

        this.dlGroupId =
                dlGroupId;
    }

    public Long getDlFolderId() {
        return dlFolderId;
    }

    public void setDlFolderId(
            Long dlFolderId) {

        this.dlFolderId =
                dlFolderId;
    }

    public Long getDlFileEntryId() {
        return dlFileEntryId;
    }

    public void setDlFileEntryId(
            Long dlFileEntryId) {

        this.dlFileEntryId =
                dlFileEntryId;
    }

    public String getDlFileUuid() {
        return dlFileUuid;
    }

    public void setDlFileUuid(
            String dlFileUuid) {

        this.dlFileUuid =
                dlFileUuid;
    }

    public String getNombreOriginal() {
        return nombreOriginal;
    }

    public void setNombreOriginal(
            String nombreOriginal) {

        this.nombreOriginal =
                nombreOriginal;
    }

    public String getNombrePersistido() {
        return nombrePersistido;
    }

    public void setNombrePersistido(
            String nombrePersistido) {

        this.nombrePersistido =
                nombrePersistido;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(
            String titulo) {

        this.titulo =
                titulo;
    }

    public Date getAltaFecha() {
        return altaFecha;
    }

    public void setAltaFecha(
            Date altaFecha) {

        this.altaFecha =
                altaFecha;
    }

    public String getAltaUsr() {
        return altaUsr;
    }

    public void setAltaUsr(
            String altaUsr) {

        this.altaUsr =
                altaUsr;
    }
}