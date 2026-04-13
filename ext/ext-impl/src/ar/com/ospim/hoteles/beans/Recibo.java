package ar.com.ospim.hoteles.beans;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ar.com.ospim.farmacia.beans.Vademecum;
import ar.com.ospim.farmaciaOspim.beans.ItemVademecumTotal;
import ar.com.uoma.WebKeysUOMA;
import ar.com.uoma.facturacion.Cliente;
import ar.com.uoma.facturacion.Factura;
import ar.com.uoma.facturacion.FacturaDetalle;
import ar.com.uoma.facturacion.FacturaIngreso;
import ar.com.uoma.facturacion.Producto;

public class Recibo  implements Serializable {
	
	private static final long serialVersionUID = 7589753241065771886L;
	private String sucursal;
	private Long numero;
	private String descripcion;
	//private String codHotel;
	private Date fecha;
	private Factura factura;
	private Reserva reserva;
	private Cliente cliente;
	private Double total;
	private Double totalAnterior;
	private Date fechaBaja;
	private List<FacturaIngreso> ingresos = new ArrayList<FacturaIngreso>();
	private Date fechaDdeFiltro;
	private Date fechaHtaFiltro;
	private Date fechaTransferencia;
	private Date fechaProceso;
	private Date aprobadoFecha;
	private String aprobadoUser;
	private Integer estadoFiltro;
	
	
	public Long getNumero() {
		return numero;
	}

	public void setNumero(Long numero) {
		this.numero = numero;
	}


	public String getDescripcion() {
		return descripcion;
	}



	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public String getSucursal() {
		return sucursal;
	}

	public void setSucursal(String sucursal) {
		this.sucursal = sucursal;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

		public Factura getFactura() {
		return factura;
	}

	public void setFactura(Factura factura) {
		this.factura = factura;
	}

	public Reserva getReserva() {
		return reserva;
	}

	public void setReserva(Reserva reserva) {
		this.reserva = reserva;
	}

	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}
	
	
	public List<FacturaIngreso> getIngresos() {
		return ingresos;
	}

	public void setIngresos(List<FacturaIngreso> ingresos) {
		this.ingresos = ingresos;
	}
	
	

	public Double getTotal() {
		return total;
	}

	public void setTotal(Double total) {
		this.total = total;
	}

	
	
	public Double getTotalAnterior() {
		return totalAnterior;
	}

	public void setTotalAnterior(Double totalAnterior) {
		this.totalAnterior = totalAnterior;
	}
	
	public Date getFechaBaja() {
		return fechaBaja;
	}

	public void setFechaBaja(Date fechaBaja) {
		this.fechaBaja = fechaBaja;
	}
	
	public Date getFechaDdeFiltro() {
		return fechaDdeFiltro;
	}

	public void setFechaDdeFiltro(Date fechaDdeFiltro) {
		this.fechaDdeFiltro = fechaDdeFiltro;
	}

	public Date getFechaHtaFiltro() {
		return fechaHtaFiltro;
	}

	public void setFechaHtaFiltro(Date fechaHtaFiltro) {
		this.fechaHtaFiltro = fechaHtaFiltro;
	}

	public static Recibo  getMapping(ResultSet rs, String prefix) throws Exception {
		Recibo   rec  = new Recibo();
		
		rec.setDescripcion(rs.getString(prefix+"descripcion"));
		rec.setNumero(rs.getLong(prefix+"numero"));
		rec.setSucursal(rs.getString(prefix+"sucursal"));
		rec.setFecha(rs.getDate(prefix + "fecha"));
		rec.setFechaBaja(rs.getDate(prefix + "baja_fecha"));
		if(rs.getDate(prefix + "fecha_aprobacion")!=null){
			rec.setAprobadoFecha(rs.getDate(prefix + "fecha_aprobacion"));
			rec.setAprobadoUser(rs.getString(prefix+"user_aprobacion"));
		}
		rec.setFechaProceso(rs.getDate(prefix+"fecha_proceso_en_central"));
		
		Cliente cliente= new Cliente();
		cliente.setRazonSocial(rs.getString(prefix + "cliente_nombre"));
		cliente.setCuit(rs.getString(prefix + "cliente_documento"));
		cliente.setId(rs.getInt(prefix + "cliente_id_en_origen"));
		rec.setCliente(cliente);
		
		if("RES".equalsIgnoreCase(rs.getString(prefix+"comprobante_tipo"))) {
			Reserva reserva = new Reserva();
			reserva.setAnio(rs.getInt(prefix + "comprobante_anio"));
			reserva.setIdReserva(rs.getInt(prefix + "comprobante_nro"));
			rec.setReserva(reserva);
		}else {
			Factura factura=new Factura();
			factura.setTipo(rs.getString(prefix+"comprobante_tipo"));
			factura.setSucursal(rs.getString(prefix+"comprobante_sucursal"));
			factura.setNumero(rs.getString(prefix + "comprobante_nro"));
			rec.setFactura(factura);
		}
		
		rec.setTotal(rs.getDouble(prefix+"total"));
		return rec ;
   }

	public Recibo() {
		super();
		ingresos = new ArrayList<FacturaIngreso>();
	}
	
	
public Double totalIngresos() {
  Double ret=0D;	
	if(ingresos!=null) {
		for(FacturaIngreso i:ingresos) {
			ret+=i.getIngreso().getImporte().doubleValue();
		}
	}
   return ret;	
}


public void recalcularImportes() {

}

public Date getFechaTransferencia() {
	return fechaTransferencia;
}

public void setFechaTransferencia(Date fechaTransferencia) {
	this.fechaTransferencia = fechaTransferencia;
}

public Date getFechaProceso() {
	return fechaProceso;
}

public void setFechaProceso(Date fechaProceso) {
	this.fechaProceso = fechaProceso;
}

public Date getAprobadoFecha() {
	return aprobadoFecha;
}

public void setAprobadoFecha(Date aprobadoFecha) {
	this.aprobadoFecha = aprobadoFecha;
}

public String getAprobadoUser() {
	return aprobadoUser;
}

public void setAprobadoUser(String aprobadoUser) {
	this.aprobadoUser = aprobadoUser;
}

public Integer getEstadoFiltro() {
	return estadoFiltro;
}

public void setEstadoFiltro(Integer estadoFiltro) {
	this.estadoFiltro = estadoFiltro;
}

public Recibo(String sucursal, Long numero) {
	super();
	this.sucursal = sucursal;
	this.numero = numero;
}

public Recibo(String sucursal, Long numero, Date fechaProceso) {
	super();
	this.sucursal = sucursal;
	this.numero = numero;
	this.fechaProceso = fechaProceso;
}



	
}
