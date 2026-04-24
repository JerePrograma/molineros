package ar.com.ospim.hoteles.beans;

import java.io.Serializable;
import java.sql.ResultSet;
import java.util.Date;

import ar.com.ospim.afiliados.beans.Afiliado;
import ar.com.ospim.farmacia.beans.Vademecum;
import ar.com.ospim.farmaciaOspim.beans.ItemVademecumTotal;
import ar.com.uoma.facturacion.Factura;

public class PrestamoCuota  implements Serializable {
	
	private static final long serialVersionUID = 9061541587582038179L;
	private Integer numero;
	private Date vencimiento;
	private Double importe;
	private Double pagado;
	private Boolean modificada;
	
	public Integer getNumero() {
		return numero;
	}
	public void setNumero(Integer numero) {
		this.numero = numero;
	}
	public Date getVencimiento() {
		return vencimiento;
	}
	public void setVencimiento(Date vencimiento) {
		this.vencimiento = vencimiento;
	}
	public Double getImporte() {
		return importe;
	}
	public void setImporte(Double importe) {
		this.importe = importe;
	}
	public Double getPagado() {
		return pagado;
	}
	public void setPagado(Double pagado) {
		this.pagado = pagado;
	}
	
	
	public Boolean getModificada() {
		return modificada;
	}
	public void setModificada(Boolean modificada) {
		this.modificada = modificada;
	}
	
	
	public static PrestamoCuota  getMapping(ResultSet rs, String prefix) throws Exception {
		PrestamoCuota  pre  = new PrestamoCuota();
		
		pre.setVencimiento(rs.getDate("vencimiento"));
	    pre.setNumero(rs.getInt("numero"));
	    pre.setImporte(rs.getDouble("importe"));
	    pre.setPagado(rs.getDouble("pagado"));
	    
		return pre ;
   }

	
	
}
