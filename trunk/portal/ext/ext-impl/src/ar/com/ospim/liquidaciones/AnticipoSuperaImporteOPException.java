package ar.com.ospim.liquidaciones;

public class AnticipoSuperaImporteOPException extends Exception {

	private String razonSoc;
	private static final long serialVersionUID = 1L;

	public AnticipoSuperaImporteOPException() {
		super();
	}

	public AnticipoSuperaImporteOPException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public AnticipoSuperaImporteOPException(Throwable arg0) {
		super(arg0);
	}

	public AnticipoSuperaImporteOPException(String razon) {
		this.razonSoc = razon;
	}

	public String getRazonSoc() {
		return razonSoc;
	}

	public void setRazonSoc(String razonSoc) {
		this.razonSoc = razonSoc;
	}
	
	

}
