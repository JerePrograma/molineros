package ar.com.ospim.global.beans;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.Date;

import ar.com.ospim.global.services.OrdenPagoServiceUtil;
import ar.com.ospim.tesoreria.beans.CuentaBancaria;

public class Caja implements Pago {
	
//	public static final int ID_PAGO_CAJA = 1;
	public static final int ID_PAGO_CAJA = 5; 
	// a Partir del 15/09/2016 xq en Amtima salia mal porque en la tabla de tipos pagos, caja es tipo 5
	public static final int ID_PAGO_CAJA_LOS_DIQUES = 7; 
	private int tipo_pago;
	private BigDecimal importe;
	private PlanCuentas cuentaAsociada;
	
	
	

	public BigDecimal getImporte() {
		return importe;
	}
	

	public void setImporte(BigDecimal importe) {
		this.importe = importe;
	}
	
	
	public String getDescripcion() {
		return "";
	}

	public String getANombreDe() {
		return "";
	}


	public Date getBaja_fecha() {
		return null;
	}

	
	public void savePago(OrdenPago op, String screenName, Connection con,
			int entidad) throws Exception {
		OrdenPagoServiceUtil.savePago(this, op, screenName, con, entidad);
	}


	@Override
	public CuentaBancaria getCuentaBancaria() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public String getNumeroStr() {
		// TODO Auto-generated method stub
		return "";
	}


	@Override
	public String getTipo() {
		return this.getClass().getSimpleName();
	}
	
	public void setTipo_pago(int tipo_pago) {
		this.tipo_pago = tipo_pago;
	}

	public int getTipo_pago() {
		return tipo_pago;
	}
	
	
	public int get_Tipo_pago() {
		return ID_PAGO_CAJA;
	}


	@Override
	public String getIdTipo() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void setCuentaAsociada(PlanCuentas cuentaAsociada) {
		this.cuentaAsociada = cuentaAsociada;
	}

	public PlanCuentas getCuentaAsociada() {
		return cuentaAsociada;
	}


	@Override
	public PagoBancario getPagoBancario() {
		// TODO Auto-generated method stub
		return null;
	}
}
