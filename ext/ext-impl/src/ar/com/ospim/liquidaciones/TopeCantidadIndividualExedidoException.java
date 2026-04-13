package ar.com.ospim.liquidaciones;

public class TopeCantidadIndividualExedidoException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private double topeIndivCant = 0;
	private double cantidad = 0;

	public TopeCantidadIndividualExedidoException() {
		super();
	}

	public TopeCantidadIndividualExedidoException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public TopeCantidadIndividualExedidoException(String arg0) {
		super(arg0);
	}

	public TopeCantidadIndividualExedidoException(Throwable arg0) {
		super(arg0);
	}

	public TopeCantidadIndividualExedidoException(double cantidad,
			double topeIndivCant) {
		this.cantidad = cantidad;
		this.topeIndivCant = topeIndivCant;
	}

	public double getCantidad() {
		return cantidad;
	}

	public double getTopeIndivCant() {
		return topeIndivCant;
	}

}
