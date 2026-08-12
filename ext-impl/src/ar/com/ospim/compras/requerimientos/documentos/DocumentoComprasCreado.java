package ar.com.ospim.compras.requerimientos.documentos;

public final class DocumentoComprasCreado {

    private final long groupId;
    private final long folderId;
    private final long fileEntryId;
    private final String uuid;
    private final String nombrePersistido;
    private final String titulo;

    public DocumentoComprasCreado(
            long groupId,
            long folderId,
            long fileEntryId,
            String uuid,
            String nombrePersistido,
            String titulo) {

        this.groupId = groupId;
        this.folderId = folderId;
        this.fileEntryId = fileEntryId;
        this.uuid = uuid;
        this.nombrePersistido = nombrePersistido;
        this.titulo = titulo;
    }

    public long getGroupId() {
        return groupId;
    }

    public long getFolderId() {
        return folderId;
    }

    public long getFileEntryId() {
        return fileEntryId;
    }

    public String getUuid() {
        return uuid;
    }

    public String getNombrePersistido() {
        return nombrePersistido;
    }

    public String getTitulo() {
        return titulo;
    }
}
