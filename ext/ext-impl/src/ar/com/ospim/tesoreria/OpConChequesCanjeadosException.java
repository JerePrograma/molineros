package ar.com.ospim.tesoreria;

import java.util.List;

import ar.com.ospim.global.beans.Cheque;

public class OpConChequesCanjeadosException extends Exception {

	private static final long serialVersionUID = 1132145574611643464L;
	private List<Cheque> chequesCanjeados;

	public OpConChequesCanjeadosException() {
		super();
	}

	public OpConChequesCanjeadosException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public OpConChequesCanjeadosException(String arg0) {
		super(arg0);
	}

	public OpConChequesCanjeadosException(Throwable arg0) {
		super(arg0);
	}

	public OpConChequesCanjeadosException(List<Cheque> chequesCanjeados) {
		this.setChequesCanjeados(chequesCanjeados);
	}

	public List<Cheque> getChequesCanjeados() {
		return chequesCanjeados;
	}

	public void setChequesCanjeados(List<Cheque> chequesCanjeados) {
		this.chequesCanjeados = chequesCanjeados;
	}

}
