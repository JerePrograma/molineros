package ar.com.ospim.liquidaciones;

import java.math.BigDecimal;

public class OrdenPagoOspimCreacionNuevoAnticipoException extends Exception {

	private static final long serialVersionUID = 4298281587968377487L;
	private BigDecimal importeNuevo;
	private BigDecimal importeOriginal;

	public OrdenPagoOspimCreacionNuevoAnticipoException() {
		super();
	}

	public OrdenPagoOspimCreacionNuevoAnticipoException(String arg0,
			Throwable arg1) {
		super(arg0, arg1);
	}

	public OrdenPagoOspimCreacionNuevoAnticipoException(String arg0) {
		super(arg0);
	}

	public OrdenPagoOspimCreacionNuevoAnticipoException(Throwable arg0) {
		super(arg0);
	}

	public OrdenPagoOspimCreacionNuevoAnticipoException(
			BigDecimal importeOriginal, BigDecimal importeNuevo) {
		this.setImporteOriginal(importeOriginal);
		this.setImporteNuevo(importeNuevo);
	}

	public void setImporteNuevo(BigDecimal importeNuevo) {
		this.importeNuevo = importeNuevo;
	}

	public BigDecimal getImporteNuevo() {
		return importeNuevo;
	}

	public void setImporteOriginal(BigDecimal importeOriginal) {
		this.importeOriginal = importeOriginal;
	}

	public BigDecimal getImporteOriginal() {
		return importeOriginal;
	}

}
