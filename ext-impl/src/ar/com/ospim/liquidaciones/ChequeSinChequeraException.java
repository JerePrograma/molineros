package ar.com.ospim.liquidaciones;

import ar.com.ospim.global.beans.Cheque;

public class ChequeSinChequeraException extends Exception {

	private Cheque cheque;
	private static final long serialVersionUID = 1L;

	public ChequeSinChequeraException() {
		super();
	}

	public ChequeSinChequeraException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public ChequeSinChequeraException(String arg0) {
		super(arg0);
	}

	public ChequeSinChequeraException(Throwable arg0) {
		super(arg0);
	}

	public ChequeSinChequeraException(Cheque cheque) {
		this.cheque = cheque;
	}

	public void setCheque(Cheque cheque) {
		this.cheque = cheque;
	}

	public Cheque getCheque() {
		return cheque;
	}

}
