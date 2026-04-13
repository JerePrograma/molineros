package ar.com.ospim.global.beans;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.Date;

import com.liferay.portal.SystemException;

import ar.com.ospim.global.services.OrdenPagoServiceUtil;
import ar.com.ospim.global.services.ReciboGlobalServiceImpl;
import ar.com.ospim.liquidaciones.DuplicateNumeroChequeException;
import ar.com.ospim.tesoreria.beans.CuentaBancaria;
import ar.com.ospim.tesoreria.beans.Recibo;

public class Retencion  extends Ingreso {	
	
	private BigDecimal importe;
	private Integer tipo;
	private Date fecha;
	
	public static final Integer GRAL=94;
	public static final Integer IIBB = 90;
	public static final Integer IVA = 91; 
	public static final Integer SUSS = 92; 
	

	public void setImporte(BigDecimal importe) {
		this.importe = importe;
	}

	public BigDecimal getImporte() {
		return importe;
	}

	@Override
	public Date getFecha() {
		return fecha;
	}

	@Override
	public String getFechaAsString() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getNumeroStr() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Banco getBanco() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CuentaBancaria getCuentaBancaria() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isNew() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean equals(Object o) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setCuit(String cuit) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Integer getEmisor() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setEmisor(Integer emisor) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getEmisorDescripcion() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setEmisorDescripcion(String emisorDescripcion) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getCuotas() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setCuotas(int cuotas) {
		// TODO Auto-generated method stub
		
	}
	
	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	@Override
	public int saveIngreso(ReciboGlobalServiceImpl instance, Recibo recibo, String user, Connection con, int amtima)
			throws SystemException, DuplicateNumeroChequeException {
		// TODO Auto-generated method stub
		return 0;
	}

	public String getTipo() {
		if (tipo == GRAL){
			return "Retención No Identificada";
		}
		if (tipo == IVA){
			return "Retención IVA";
		}
		if (tipo == IIBB){
			return "Retención Ingresos Brutos";
		}
		if (tipo == SUSS){
			return "Retención Seguridad Social";
		}
		return "";
	}
	
	public void setTipo(int tipo) {
		this.tipo=tipo;
	}

	
}
