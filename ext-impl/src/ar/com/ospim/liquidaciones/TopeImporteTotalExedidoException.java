package ar.com.ospim.liquidaciones;

public class TopeImporteTotalExedidoException extends Exception {

	private double importe;
	private double topeImporte;

	public TopeImporteTotalExedidoException() {
		super();
	}

	public TopeImporteTotalExedidoException(String message, Throwable cause) {
		super(message, cause);
	}

	public TopeImporteTotalExedidoException(String message) {
		super(message);
	}

	public TopeImporteTotalExedidoException(Throwable cause) {
		super(cause);
	}

	public TopeImporteTotalExedidoException(double importe, double topeImporte) {
		this.setImporte(importe);
		this.setTopeImporte(topeImporte);
	}

	public void setImporte(double importe) {
		this.importe = importe;
	}

	public double getImporte() {
		return importe;
	}

	public void setTopeImporte(double topeImporte) {
		this.topeImporte = topeImporte;
	}

	public double getTopeImporte() {
		return topeImporte;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
