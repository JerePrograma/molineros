package ar.com.ospim.liquidaciones;

import ar.com.ospim.global.beans.Cheque;

public class DuplicateNumeroChequeException extends Exception {

	private Cheque cheque;
	private static final long serialVersionUID = 1L;

	public DuplicateNumeroChequeException() {
		super();
	}

	public DuplicateNumeroChequeException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public DuplicateNumeroChequeException(String arg0) {
		super(arg0);
	}

	public DuplicateNumeroChequeException(Throwable arg0) {
		super(arg0);
	}

	public DuplicateNumeroChequeException(Cheque cheque) {
		this.cheque = cheque;
	}

	public void setCheque(Cheque cheque) {
		this.cheque = cheque;
	}

	public Cheque getCheque() {
		return cheque;
	}

}
