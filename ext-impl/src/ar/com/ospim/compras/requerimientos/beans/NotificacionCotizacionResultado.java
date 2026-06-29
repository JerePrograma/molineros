package ar.com.ospim.compras.requerimientos.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NotificacionCotizacionResultado implements Serializable {
    private static final long serialVersionUID = 1L;
    private int totalCandidatos;
    private int enviados;
    private int errores;
    private int emailsInvalidos;
    private int omitidos;
    private int emailsRealesInvalidosAdvertidos;
    private int prestadoresHabilitados;
    private int prestadoresCompatiblesSector;
    private int prestadoresBloqueadosEstadoPrevio;
    private final List<NotificacionCotizacionDetalle> detalles = new ArrayList<NotificacionCotizacionDetalle>();

    public void agregarDetalle(NotificacionCotizacionDetalle detalle) {
        if (detalle == null) {
            return;
        }
        detalles.add(detalle);
        String resultado = detalle.getResultado();
        if (NotificacionCotizacionDetalle.RESULTADO_ENVIADO.equals(resultado)) {
            enviados++;
        } else if (NotificacionCotizacionDetalle.RESULTADO_OMITIDO.equals(resultado)) {
            omitidos++;
        } else if (NotificacionCotizacionDetalle.RESULTADO_EMAIL_INVALIDO.equals(resultado)) {
            emailsInvalidos++;
        } else if (NotificacionCotizacionDetalle.RESULTADO_ERROR.equals(resultado)) {
            errores++;
        }
        if (detalle.isEmailRealInvalidoAdvertido()) {
            emailsRealesInvalidosAdvertidos++;
        }
    }

    public List<NotificacionCotizacionDetalle> getDetalles() {
        return Collections.unmodifiableList(detalles);
    }

    public int getTotalCandidatos() {
        return totalCandidatos;
    }

    public void setTotalCandidatos(int totalCandidatos) {
        this.totalCandidatos = totalCandidatos;
    }

    public int getEnviados() {
        return enviados;
    }

    public void setEnviados(int enviados) {
        this.enviados = enviados;
    }

    public void incrementarEnviados() {
        enviados++;
    }

    public int getErrores() {
        return errores;
    }

    public void setErrores(int errores) {
        this.errores = errores;
    }

    public void incrementarErrores() {
        errores++;
    }

    public int getEmailsInvalidos() {
        return emailsInvalidos;
    }

    public void setEmailsInvalidos(int emailsInvalidos) {
        this.emailsInvalidos = emailsInvalidos;
    }

    public void incrementarEmailsInvalidos() {
        emailsInvalidos++;
    }

    public int getOmitidos() {
        return omitidos;
    }

    public void setOmitidos(int omitidos) {
        this.omitidos = omitidos;
    }

    public void incrementarOmitidos() {
        omitidos++;
    }

    public int getEmailsRealesInvalidosAdvertidos() {
        return emailsRealesInvalidosAdvertidos;
    }

    public void setEmailsRealesInvalidosAdvertidos(int emailsRealesInvalidosAdvertidos) {
        this.emailsRealesInvalidosAdvertidos = emailsRealesInvalidosAdvertidos;
    }

    public void incrementarEmailsRealesInvalidosAdvertidos() {
        emailsRealesInvalidosAdvertidos++;
    }

    public int getPrestadoresHabilitados() {
        return prestadoresHabilitados;
    }

    public void setPrestadoresHabilitados(int prestadoresHabilitados) {
        this.prestadoresHabilitados = prestadoresHabilitados;
    }

    public int getPrestadoresCompatiblesSector() {
        return prestadoresCompatiblesSector;
    }

    public void setPrestadoresCompatiblesSector(int prestadoresCompatiblesSector) {
        this.prestadoresCompatiblesSector = prestadoresCompatiblesSector;
    }

    public int getPrestadoresBloqueadosEstadoPrevio() {
        return prestadoresBloqueadosEstadoPrevio;
    }

    public void setPrestadoresBloqueadosEstadoPrevio(int prestadoresBloqueadosEstadoPrevio) {
        this.prestadoresBloqueadosEstadoPrevio = prestadoresBloqueadosEstadoPrevio;
    }

    public int getTotalProcesados() {
        return enviados + errores + emailsInvalidos + omitidos;
    }

    public int getPendientesSinClasificar() {
        int pendientes = totalCandidatos - getTotalProcesados();
        return pendientes > 0 ? pendientes : 0;
    }

    public boolean tieneErroresTecnicos() {
        return errores > 0;
    }

    public boolean tieneEmailsInvalidos() {
        return emailsInvalidos > 0;
    }

    public boolean tieneOmitidos() {
        return omitidos > 0;
    }

    public boolean tieneAdvertenciasQa() {
        return emailsRealesInvalidosAdvertidos > 0;
    }

    public boolean tieneIncidencias() {
        return tieneErroresTecnicos() || tieneEmailsInvalidos() || tieneOmitidos() || tieneAdvertenciasQa();
    } /* * Compatibilidad con código existente. * * Mantiene la semántica anterior: * error técnico o email inválido. */

    public boolean tieneErrores() {
        return errores > 0 || emailsInvalidos > 0;
    }
}