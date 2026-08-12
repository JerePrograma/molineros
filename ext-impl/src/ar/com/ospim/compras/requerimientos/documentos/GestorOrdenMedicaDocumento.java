package ar.com.ospim.compras.requerimientos.documentos;

public interface GestorOrdenMedicaDocumento {

    DocumentoComprasCreado crearOrdenMedica(
            int idRequerimientoCompra,
            OrdenMedicaValidada ordenMedica) throws Exception;

    void eliminarDocumento(
            DocumentoComprasCreado documento) throws Exception;
}
