package ar.com.ospim.liquidaciones;

public class TopeCantidadTotalExedidoException extends Exception {

	private double cantidad;
	private double topeCant;

	public TopeCantidadTotalExedidoException() {
		super();
	}

	public TopeCantidadTotalExedidoException(String message, Throwable cause) {
		super(message, cause);
	}

	public TopeCantidadTotalExedidoException(String message) {
		super(message);
	}

	public TopeCantidadTotalExedidoException(Throwable cause) {
		super(cause);
	}

	public TopeCantidadTotalExedidoException(double cantidad, double topeCant) {
		this.setCantidad(cantidad);
		this.setTopeCant(topeCant);
	}

	public void setCantidad(double cantidad) {
		this.cantidad = cantidad;
	}

	public double getCantidad() {
		return cantidad;
	}

	public void setTopeCant(double topeCant) {
		this.topeCant = topeCant;
	}

	public double getTopeCant() {
		return topeCant;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
