package ar.com.ospim.autorizaciones.exceptions;

public class DictamenConcurrenteException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private final int tipoDictamen;
	private final String valorActualBD;
	private final String valorIngresado;

	public DictamenConcurrenteException(
	        String nombreDictamen,
	        int tipoDictamen,
	        String valorActualBD,
	        String valorIngresado) {

	    super(nombreDictamen);
	    this.tipoDictamen = tipoDictamen;
	    this.valorActualBD = valorActualBD;
	    this.valorIngresado = valorIngresado;
	}

	public int getTipoDictamen() {
	    return tipoDictamen;
	}

	public String getValorActualBD() {
	    return valorActualBD;
	}

	public String getValorIngresado() {
	    return valorIngresado;
	}
}
