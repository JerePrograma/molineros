package ar.com.ospim.compras.requerimientos.documentos;

import java.io.File;
import java.sql.Date;

public final class OrdenMedicaValidada {

    private final File archivo;
    private final String nombreOriginal;
    private final String extension;
    private final String contentType;
    private final Date fechaDocumento;
    private final String numeroReceta;

    public OrdenMedicaValidada(
            File archivo,
            String nombreOriginal,
            String extension,
            String contentType,
            Date fechaDocumento) {

        this(
                archivo,
                nombreOriginal,
                extension,
                contentType,
                fechaDocumento,
                null
        );
    }

    public OrdenMedicaValidada(
            File archivo,
            String nombreOriginal,
            String extension,
            String contentType,
            Date fechaDocumento,
            String numeroReceta) {

        this.archivo = archivo;
        this.nombreOriginal = nombreOriginal;
        this.extension = extension;
        this.contentType = contentType;
        this.fechaDocumento = fechaDocumento;
        this.numeroReceta = numeroReceta;
    }

    public File getArchivo() {
        return archivo;
    }

    public String getNombreOriginal() {
        return nombreOriginal;
    }

    public String getExtension() {
        return extension;
    }

    public String getContentType() {
        return contentType;
    }

    public Date getFechaDocumento() {
        return fechaDocumento;
    }

    public String getNumeroReceta() {
        return numeroReceta;
    }
}
