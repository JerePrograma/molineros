package ar.com.ospim.global.beans;

import java.io.Serializable;

public class ConvenioNacion implements Serializable{

	private static final long serialVersionUID = 1L;
	public String descripcion;
	public int id;
	public int cuenta_suc;
	public int tipo_boleta;
	public boolean ospim;
	public boolean amtima;
	public boolean uoma;
	
	
	public ConvenioNacion() {
		super();
	}

	public ConvenioNacion(String descripcion,int id, int cuenta_suc, int tipo_boleta,
			boolean ospim, boolean uoma, boolean amtima){
		this.descripcion=descripcion;
		this.id=id;
		this.cuenta_suc=cuenta_suc;
		this.tipo_boleta=tipo_boleta;
		this.ospim=ospim;
		this.amtima=amtima;
		this.uoma=uoma;
	}

	public ConvenioNacion(int cuenta_suc) {
		this.cuenta_suc=cuenta_suc;
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

	public String getDescripcion() {
		return descripcion;
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
	
	public int getCuenta_suc() {
		return cuenta_suc;
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
	
}






