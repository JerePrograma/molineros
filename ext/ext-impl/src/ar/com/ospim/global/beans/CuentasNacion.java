package ar.com.ospim.global.beans;

import java.io.Serializable;

public class CuentasNacion implements Serializable {

	private static final long serialVersionUID = 1L;
	public String descripcion;
	public int id;
	public int cuenta_suc;
	public int tipo_boleta;
	public boolean ospim;
	public boolean amtima;
	public boolean uoma;
	
	public CuentasNacion(String descripcion,int id, int cuenta_suc, int tipo_boleta,
			boolean ospim, boolean uoma, boolean amtima){
		
		this.descripcion=descripcion;
		this.id=id;
		this.cuenta_suc=cuenta_suc;
		this.tipo_boleta=tipo_boleta;
		this.ospim=ospim;
		this.amtima=amtima;
		this.uoma=uoma;
	}

	public CuentasNacion(int cuenta_suc) {
		this.cuenta_suc=cuenta_suc;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public boolean getOspim() {
		return ospim;
	}

	public boolean getAmtima() {
		return amtima;
	}

	public boolean getUoma() {
		return uoma;
	}

	public void setOspim(boolean ospim) {
		this.ospim = ospim;
	}

	public void setAmtima(boolean amtima) {
		this.amtima = amtima;
	}

	public void setUoma(boolean uoma) {
		this.uoma = uoma;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public String getCuenta_suc() {
		String cuenta_sucP = Integer.toString(cuenta_suc);
		String cuenta_sucS = cuenta_sucP.substring( 0, 5 ) + 
				"/" + cuenta_sucP.substring( 5, 7 );
		return cuenta_sucS;
	}
	
	public void setCuenta_suc(int cuenta_suc) { 
		this.cuenta_suc = cuenta_suc;
	}
	
	public int getTipo_boleta() {
		return tipo_boleta;
	}

	public void setTipo_boleta(int tipo_boleta) {
		this.tipo_boleta = tipo_boleta;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + cuenta_suc;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CuentasNacion other = (CuentasNacion) obj;
		if (cuenta_suc != other.cuenta_suc)
			return false;
		return true;
	}
	
	
}
