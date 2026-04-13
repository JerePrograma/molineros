package ar.com.ospim.liquidaciones;

public class TopeImporteIndividualExedidoException extends Exception {

	private double importe;
	private double topeIndivImporte;

	public TopeImporteIndividualExedidoException() {
		super();
	}

	public TopeImporteIndividualExedidoException(String message, Throwable cause) {
		super(message, cause);
	}

	public TopeImporteIndividualExedidoException(String message) {
		super(message);
	}

	public TopeImporteIndividualExedidoException(Throwable cause) {
		super(cause);
	}

	public TopeImporteIndividualExedidoException(double importe,
			double topeIndivImporte) {
		this.setImporte(importe);
		this.setTopeIndivImporte(topeIndivImporte);
	}

	public void setImporte(double importe) {
		this.importe = importe;
	}

	public double getImporte() {
		return importe;
	}

	public void setTopeIndivImporte(double topeIndivImporte) {
		this.topeIndivImporte = topeIndivImporte;
	}

	public double getTopeIndivImporte() {
		return topeIndivImporte;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
