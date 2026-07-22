package ar.com.ospim.procesaArchivos.exception;

public class ArchivoAdmifarmGeneralOspimIncorrectoException extends Exception {

    private static final long serialVersionUID = 1L;
    
    private int code;

    public ArchivoAdmifarmGeneralOspimIncorrectoException(int code) {
        super();
        this.code = code;
    }

    public ArchivoAdmifarmGeneralOspimIncorrectoException(int code, String message) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
