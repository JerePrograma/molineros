package ar.com.ospim.prestadores.beans;

import java.util.Date;

public class HistoricoPrestadorCotizacion {

    private int idHistorico;
    private int idPrestador;
    private String usuario;
    private Date fecha;
    private boolean estadoACotizar;

    public int getIdHistorico() {
        return idHistorico;
    }

    public void setIdHistorico(int idHistorico) {
        this.idHistorico = idHistorico;
    }

    public int getIdPrestador() {
        return idPrestador;
    }

    public void setIdPrestador(int idPrestador) {
        this.idPrestador = idPrestador;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public boolean isEstadoACotizar() {
        return estadoACotizar;
    }

    public void setEstadoACotizar(boolean estadoACotizar) {
        this.estadoACotizar = estadoACotizar;
    }
}