package ar.com.ospim.compras.requerimientos.action;

/**
 * Error de validación asociado a un campo del formulario de Compras.
 *
 * Esta excepción pertenece a la capa HTTP: permite conservar el nombre
 * del campo que debe destacarse sin trasladar esa responsabilidad a los
 * Helpers de negocio o a los ServiceImpl.
 */
public class ValidacionCompraException extends Exception {

    private final String campo;

    public ValidacionCompraException(
            String campo,
            String mensaje) {

        super(mensaje);
        this.campo = campo;
    }

    public String getCampo() {
        return campo;
    }
}
