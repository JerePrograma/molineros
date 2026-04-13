package ar.com.ospim.tesoreria.beans;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.compass.core.util.backport.java.util.Collections;

import ar.com.global.services.CalculaCapitalCuotaServiceUtil;
import ar.com.ospim.afip.beans.ReporteDeudaNominaEmpresa;
import ar.com.ospim.afip.beans.ReporteDeudaNominaEmpresa.PagosEmpresa;
import ar.com.ospim.afip.service.AfipServiceUtil;
import ar.com.ospim.tesoreria.actas.action.BuscarActasPeriodosAction;
import ar.com.ospim.util.DateUtils;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

public class ActaPeriodoDeudaEmpresa {
	private static Log _log = LogFactoryUtil
			.getLog(ActaPeriodoDeudaEmpresa.class);
	
	private boolean borradoLogico = false;
	private String cuil;
	private Date periodo;
	private BigDecimal remuneracionDeclarada;
	private BigDecimal calculado;
	private BigDecimal decreto;
	private Date alta_fecha;
	private String alta_usr;
	private String alta_ip;
	private Date modi_fecha;
	private String modi_usr;
	private String modi_ip;
	private Date baja_fecha;
	private String baja_usr;
	private String baja_ip;
	private String apellido;
	private String nombre;
	private Date fechaIngreso;
	private List<Detalle> detalle;	
	private String categoria;
	private BigDecimal remuneracionTotal;
	private int cantTotalAfi;
	
	private int tipoAporte;
	private String camara;

	public ActaPeriodoDeudaEmpresa(Date time, String cuil) {
		this.periodo = time;
		this.cuil = cuil;
	}

	public ActaPeriodoDeudaEmpresa() {
	}

	public ActaPeriodoDeudaEmpresa(ReporteDeudaNominaEmpresa deuda) {
		this.cuil = deuda.getCuilAportante();
		this.periodo = deuda.getPeriodo();
		this.remuneracionDeclarada = deuda.getRemuneracionDeclarada();
		this.calculado = deuda.getCalculado();		
		this.decreto = null;
		this.apellido = deuda.getApellido();
		this.nombre = deuda.getNombre();
		this.detalle = new ArrayList<Detalle>();
		this.camara=deuda.getCamara();
		this.categoria=deuda.getCategoria();
		this.fechaIngreso=deuda.getFechaIngreso();
		this.tipoAporte=deuda.getTipoAporte();
		this.remuneracionTotal=deuda.getRemuneracionDeclaradaTotal();
		this.cantTotalAfi=deuda.getCantAfiliadosTotal();
		// System.out.println(cuil+";"+calculado);
		for (PagosEmpresa pe : deuda.getPagos()) {
			this.detalle.add(new Detalle(pe));			
		}
	}
	
	

	public BigDecimal getRemuneracionTotal() {
		return remuneracionTotal;
	}

	public void setRemuneracionTotal(BigDecimal remuneracionTotal) {
		this.remuneracionTotal = remuneracionTotal;
	}

	public int getCantTotalAfi() {
		return cantTotalAfi;
	}

	public void setCantTotalAfi(int cantTotalAfi) {
		this.cantTotalAfi = cantTotalAfi;
	}

	public Date getAlta_fecha() {
		return alta_fecha;
	}

	public void setAlta_fecha(Date altaFecha) {
		alta_fecha = altaFecha;
	}

	public String getAlta_usr() {
		return alta_usr;
	}

	public void setAlta_usr(String altaUsr) {
		alta_usr = altaUsr;
	}

	public String getAlta_ip() {
		return alta_ip;
	}

	public void setAlta_ip(String altaIp) {
		alta_ip = altaIp;
	}

	public Date getModi_fecha() {
		return modi_fecha;
	}

	public void setModi_fecha(Date modiFecha) {
		modi_fecha = modiFecha;
	}

	public String getModi_usr() {
		return modi_usr;
	}

	public void setModi_usr(String modiUsr) {
		modi_usr = modiUsr;
	}

	public String getModi_ip() {
		return modi_ip;
	}

	public void setModi_ip(String modiIp) {
		modi_ip = modiIp;
	}

	public Date getBaja_fecha() {
		return baja_fecha;
	}

	public void setBaja_fecha(Date bajaFecha) {
		baja_fecha = bajaFecha;
	}

	public String getBaja_usr() {
		return baja_usr;
	}

	public void setBaja_usr(String bajaUsr) {
		baja_usr = bajaUsr;
	}

	public String getBaja_ip() {
		return baja_ip;
	}

	public void setBaja_ip(String bajaIp) {
		baja_ip = bajaIp;
	}

	public Date getPeriodo() {
		return periodo;
	}

	public void setPeriodo(Date periodo) {
		this.periodo = periodo;
	}

	public String getCuil() {
		return cuil;
	}

	public void setCuil(String cuil) {
		this.cuil = cuil;
	}

	public BigDecimal getRemuneracionDeclarada() {
		return remuneracionDeclarada;
	}
	
	public String getRemuneracionDeclaradaAsString() {
		return remuneracionDeclarada != null ? remuneracionDeclarada.toString() : "";
	}
	

	public void setRemuneracionDeclarada(BigDecimal remuneracionDeclarada) {
		this.remuneracionDeclarada = remuneracionDeclarada;
	}

	public BigDecimal getCalculado() {
		return calculado;
	}

	public void setCalculado(BigDecimal calculado) {
		this.calculado = calculado;
	}

	public BigDecimal getDecreto() {
		return decreto;
	}

	public void setDecreto(BigDecimal decreto) {
		this.decreto = decreto;
	}

	public void setBorradoLogico(boolean borradoLogico) {
		this.borradoLogico = borradoLogico;

		if (detalle != null) {
			Iterator<Detalle> iterator2 = detalle.iterator();
			while (iterator2.hasNext()) {
				Detalle det = iterator2.next();
				if (det.getId() <= 0) {
					iterator2.remove();
				} else {
					det.setBorradoLogico(true);
				}
			}
		}
	}
	
	

	public Date getFechaIngreso() {
		return fechaIngreso;
	}
	
	public String getFechaIngresoAsString() {
		return null != fechaIngreso ? DateUtils.format(fechaIngreso,
				DateUtils.SHORT) : "";
	}

	public void setFechaIngreso(Date fechaIngreso) {
		this.fechaIngreso = fechaIngreso;
	}

	public boolean isBorradoLogico() {
		return borradoLogico;
	}

	public BigDecimal getSubtotal() {
		BigDecimal subtotal = BigDecimal.ZERO;
		if (detalle != null) {
			for (Detalle det : detalle) {
				if (!det.isBorradoLogico()) {
					subtotal = subtotal.add(det.getCapital()!=null?det.getCapital():BigDecimal.ZERO);
				}
			}
		}
		return subtotal;
	}
	
	public BigDecimal getSubtotalNoOS() {
		BigDecimal subtotal = BigDecimal.ZERO;
		if (detalle != null) {
			for (Detalle det : detalle) {
				if (!det.isBorradoLogico()) {					
					//subtotal = subtotal.add(det.getCapitalOriginal()!=null?det.getCapitalOriginal():det.getCapital()).add(det.getInteres()).subtract(det.getMontoPagado());
					if(det.getCapital().compareTo(det.getCapitalOriginal())==0){
						//subtotal = subtotal.add(det.getCapital()!=null?det.getCapital():BigDecimal.ZERO.add(det.getInteres()!=null?det.getInteres():BigDecimal.ZERO).subtract(det.getMontoPagado()!=null?det.getMontoPagado():BigDecimal.ZERO));
						subtotal = subtotal.add(det.getCapital()!=null?det.getCapital():BigDecimal.ZERO);
						subtotal = subtotal.add(det.getInteres()!=null?det.getInteres():BigDecimal.ZERO);
						subtotal = subtotal.subtract(det.getMontoPagado()!=null?det.getMontoPagado():BigDecimal.ZERO);						
					}else{
						//subtotal = subtotal.add(det.getCapitalOriginal()!=null?det.getCapitalOriginal():det.getCapital()).add(det.getInteres()).subtract(det.getMontoPagado());
						//subtotal = subtotal.add(det.getCapital()!=null?det.getCapital():BigDecimal.ZERO.add(det.getInteres()!=null?det.getInteres():BigDecimal.ZERO)); --02-06-2022
						
						subtotal = subtotal.add(det.getCapital()!=null?det.getCapital():BigDecimal.ZERO);
						subtotal = subtotal.add(det.getInteres()!=null?det.getInteres():BigDecimal.ZERO);
					}
				}
			}
		}
		return subtotal;
	}

	public BigDecimal getInteres() {
		BigDecimal interes = BigDecimal.ZERO;
		if (detalle != null) {
			for (Detalle det : detalle) {
				if (!det.isBorradoLogico()) {
					interes = interes.add(det.getInteres());
				}
			}
		}
		return interes;
	}

	
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((cuil == null) ? 0 : cuil.hashCode());
		result = prime * result + ((periodo == null) ? 0 : periodo.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj){
			return true;
		}
		if (obj == null){
			return false;
		}
		if (getClass() != obj.getClass()){
			return false;
		}
		ActaPeriodoDeudaEmpresa other = (ActaPeriodoDeudaEmpresa) obj;
		if (cuil == null) {
			if (other.cuil != null){
				return false;
			}
		} else if (!cuil.equals(other.cuil)){
			return false;
		}
		if (periodo == null) {
			if (other.periodo != null){
				return false;
			}
		} else if (!periodo.equals(other.periodo)){
			return false;
		}
		if(tipoAporte!=other.tipoAporte){
			return false;
		}
	
		return true;
	}

	public static ActaPeriodoDeudaEmpresa getMapping(ResultSet rs)
			throws SQLException {
		return getMapping(rs, "");
	}

	public static ActaPeriodoDeudaEmpresa getMapping(ResultSet rs, String prefix)
			throws SQLException {
		ActaPeriodoDeudaEmpresa deudaEmpresa = new ActaPeriodoDeudaEmpresa();
		deudaEmpresa.setPeriodo(rs.getDate(prefix + "periodo"));
		deudaEmpresa.setCuil(rs.getString(prefix + "cuil"));
		deudaEmpresa.setRemuneracionDeclarada(rs.getBigDecimal(prefix
				+ "remuneracion_declarada"));
		deudaEmpresa.setCalculado(rs.getBigDecimal(prefix + "calculado"));
		deudaEmpresa.setDecreto(rs.getBigDecimal(prefix + "decreto"));
		deudaEmpresa.setAlta_fecha(rs.getDate(prefix + "alta_fecha"));
		deudaEmpresa.setAlta_usr(rs.getString(prefix + "alta_usr"));
		deudaEmpresa.setAlta_ip(rs.getString(prefix + "alta_ip"));
		deudaEmpresa.setModi_fecha(rs.getDate(prefix + "modi_fecha"));
		deudaEmpresa.setModi_usr(rs.getString(prefix + "modi_usr"));
		deudaEmpresa.setModi_ip(rs.getString(prefix + "modi_ip"));
		deudaEmpresa.setBaja_fecha(rs.getDate(prefix + "baja_fecha"));
		deudaEmpresa.setBaja_usr(rs.getString(prefix + "baja_usr"));
		deudaEmpresa.setBaja_ip(rs.getString(prefix + "baja_ip"));
		deudaEmpresa.setApellido(rs.getString("apellido"));
		deudaEmpresa.setNombre(rs.getString("nombre"));

		List<Detalle> detalle = new ArrayList<Detalle>();
		Detalle det = new Detalle(rs.getDate(prefix + "pagado_fecha"),
				rs.getBigDecimal(prefix + "pagado"), rs.getBigDecimal(prefix
						+ "subtotal"), rs.getBigDecimal(prefix + "interes"),
				rs.getInt("id"));
		try {
			det.setAgregadoManual(rs.getBoolean(prefix + "agregado_manual"));
		} catch (Exception e) {
			// por si no existe
		}
		detalle.add(det);

		deudaEmpresa.setDetalle(detalle);
		return deudaEmpresa;
	}
	
		
	public static ActaPeriodoDeudaEmpresa getMappingEmpleadores(ResultSet rs)
			throws SQLException {
		ActaPeriodoDeudaEmpresa deudaEmpresa = new ActaPeriodoDeudaEmpresa();
		deudaEmpresa.setPeriodo(rs.getDate( "periodo"));
		deudaEmpresa.setCuil(rs.getString( "cuil"));
		deudaEmpresa.setRemuneracionDeclarada(rs.getBigDecimal("remuneracion_declarada"));
		deudaEmpresa.setTipoAporte(rs.getInt("tipo_aporte"));
		deudaEmpresa.setCalculado(rs.getBigDecimal( "calculado"));
		deudaEmpresa.setDecreto(rs.getBigDecimal( "decreto"));
		deudaEmpresa.setAlta_fecha(rs.getDate( "alta_fecha"));
		deudaEmpresa.setAlta_usr(rs.getString( "alta_usr"));
		deudaEmpresa.setAlta_ip(rs.getString( "alta_ip"));
		deudaEmpresa.setModi_fecha(rs.getDate( "modi_fecha"));
		deudaEmpresa.setModi_usr(rs.getString( "modi_usr"));
		deudaEmpresa.setModi_ip(rs.getString( "modi_ip"));
		deudaEmpresa.setBaja_fecha(rs.getDate( "baja_fecha"));
		deudaEmpresa.setBaja_usr(rs.getString( "baja_usr"));
		deudaEmpresa.setBaja_ip(rs.getString( "baja_ip"));
		deudaEmpresa.setApellido(rs.getString("apellido"));
		deudaEmpresa.setNombre(rs.getString("nombre"));
		deudaEmpresa.setCantTotalAfi(rs.getInt("cant_afiliados_total"));
		deudaEmpresa.setRemuneracionTotal(rs.getBigDecimal("remuneracion_total"));
		deudaEmpresa.setCamara(rs.getString("camara"));
		deudaEmpresa.setFechaIngreso(rs.getDate("fecha_ingreso"));
		List<Detalle> detalle = new ArrayList<Detalle>();
		Detalle det = new Detalle();
		BigDecimal calculadoB=rs.getBigDecimal("calculado");
		det.setCapitalOriginal(calculadoB);
		det.setInteresAFechaPagada(rs.getBigDecimal("interes_a_pago"));
		det.setCapital(rs.getBigDecimal("subtotal"));
		det.setInteres(rs.getBigDecimal("interes"));			
		det.setId(rs.getInt("id"));
		det.setMontoPagado(rs.getBigDecimal( "pagado"));
		det.setFechaPagado(rs.getDate("pagado_fecha"));		
		det.setTipoAporte(deudaEmpresa.getTipoAporte());
		
		
				
		try {
			det.setAgregadoManual(rs.getBoolean( "agregado_manual"));
		} catch (Exception e) {
			// por si no existe
		}
		detalle.add(det);

		deudaEmpresa.setDetalle(detalle);
		return deudaEmpresa;
	}

	public void setApellido(String apellido) {
		this.apellido = apellido;
	}

	public String getApellido() {
		return apellido;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getNombre() {
		return nombre;
	}

	public String toString() {
		return periodo + " " + apellido;
	}

	public void setDetalle(List<Detalle> pagos) {
		this.detalle = pagos;
	}

	public List<Detalle> getDetalle() {
		return detalle;
	}
	
	public BigDecimal getCapitalArt46(){
		BigDecimal art46=BigDecimal.ZERO;
		if(null!=detalle && detalle.size()>0){
			for(Detalle det:detalle){
				if(det.tipoAporte==CalculaCapitalCuotaServiceUtil.ART_46){
					if(!det.isBorradoLogico()){
						art46=art46.add(det.getCapital());
					}
				}
			}
			
		}
		return art46;
	}
	
	public BigDecimal getInteresArt46(){
		BigDecimal art46=BigDecimal.ZERO;
		if(null!=detalle && detalle.size()>0){
			for(Detalle det:detalle){
				if(det.tipoAporte==CalculaCapitalCuotaServiceUtil.ART_46){
					if(!det.isBorradoLogico()){
						art46=art46.add(det.getInteres());
					}
				}
			}
			
		}
		return art46;
	}
	
	public BigDecimal getTotalArt46(){
		BigDecimal art46=BigDecimal.ZERO;
		if(null!=detalle && detalle.size()>0){
			for(Detalle det:detalle){
				if(det.tipoAporte==CalculaCapitalCuotaServiceUtil.ART_46){
					if(!det.isBorradoLogico()){
						art46=art46.add(det.getCapital()).add(det.getInteres());
					}
				}
			}
			
		}
		return art46;
	}
	
	public BigDecimal getCapitalUsufructo(){
		BigDecimal usu=BigDecimal.ZERO;
		if(null!=detalle && detalle.size()>0){
			for(Detalle det:detalle){
				if(det.tipoAporte==CalculaCapitalCuotaServiceUtil.USUFRUCTO){
					if(!det.isBorradoLogico()){
						usu=usu.add(det.getCapital());
					}
				}
			}
			
		}
		return usu;
	}
	
	public BigDecimal getInteresUsufructo(){
		BigDecimal usu=BigDecimal.ZERO;
		if(null!=detalle && detalle.size()>0){
			for(Detalle det:detalle){
				if(det.tipoAporte==CalculaCapitalCuotaServiceUtil.USUFRUCTO){
					if(!det.isBorradoLogico()){
						usu=usu.add(det.getInteres());
					}
				}
			}
			
		}
		return usu;
	}
	
	public BigDecimal getTotalUsufructo(){
		BigDecimal usu=BigDecimal.ZERO;
		if(null!=detalle && detalle.size()>0){
			for(Detalle det:detalle){
				if(det.tipoAporte==CalculaCapitalCuotaServiceUtil.USUFRUCTO){
					if(!det.isBorradoLogico()){
						usu=usu.add(det.getCapital()).add(det.getInteres());
					}
				}
			}
			
		}
		return usu;
	}
	
	public BigDecimal getCapitalSocialUOMA(){
		BigDecimal usu=BigDecimal.ZERO;
		if(null!=detalle && detalle.size()>0){
			for(Detalle det:detalle){
				if(det.tipoAporte==CalculaCapitalCuotaServiceUtil.SOCIAL){
					if(!det.isBorradoLogico()){
						usu=usu.add(det.getCapital());
					}
				}
			}
			
		}
		return usu;
	}
	
	public BigDecimal getInteresSocialUOMA(){
		BigDecimal usu=BigDecimal.ZERO;
		if(null!=detalle && detalle.size()>0){
			for(Detalle det:detalle){
				if(det.tipoAporte==CalculaCapitalCuotaServiceUtil.SOCIAL){
					if(!det.isBorradoLogico()){
						usu=usu.add(det.getInteres());
					}
				}
			}
			
		}
		return usu;
	}
	
	public BigDecimal getTotalSocialUOMA(){
		BigDecimal usu=BigDecimal.ZERO;
		if(null!=detalle && detalle.size()>0){
			for(Detalle det:detalle){
				if(det.tipoAporte==CalculaCapitalCuotaServiceUtil.SOCIAL){
					if(!det.isBorradoLogico()){
						usu=usu.add(det.getCapital()).add(det.getInteres());
					}
				}
			}
			
		}
		return usu;
	}
	
	public BigDecimal getCapitalSolidario(){
		BigDecimal usu=BigDecimal.ZERO;
		if(null!=detalle && detalle.size()>0){
			for(Detalle det:detalle){
				if(det.tipoAporte==CalculaCapitalCuotaServiceUtil.SOLIDARIO){
					if(!det.isBorradoLogico()){
						usu=usu.add(det.getCapital());
					}
				}
			}
			
		}
		return usu;
	}
	
	public BigDecimal getInteresSolidario(){
		BigDecimal usu=BigDecimal.ZERO;
		if(null!=detalle && detalle.size()>0){
			for(Detalle det:detalle){
				if(det.tipoAporte==CalculaCapitalCuotaServiceUtil.SOLIDARIO){
					if(!det.isBorradoLogico()){
						usu=usu.add(det.getInteres());
					}
				}
			}
			
		}
		return usu;
	}
	
	public BigDecimal getTotalSolidario(){
		BigDecimal usu=BigDecimal.ZERO;
		if(null!=detalle && detalle.size()>0){
			for(Detalle det:detalle){
				if(det.tipoAporte==CalculaCapitalCuotaServiceUtil.SOLIDARIO){
					if(!det.isBorradoLogico()){
						usu=usu.add(det.getCapital()).add(det.getInteres());
					}
				}
			}
			
		}
		return usu;
	}
	
	public BigDecimal getCapitalAMTIMA(){
		BigDecimal usu=BigDecimal.ZERO;
		if(null!=detalle && detalle.size()>0){
			for(Detalle det:detalle){
				if(det.tipoAporte==CalculaCapitalCuotaServiceUtil.AMTIMA){
					if(!det.isBorradoLogico()){
						usu=usu.add(det.getCapital());
					}
				}
			}
			
		}
		return usu;
	}
	
	public BigDecimal getInteresAMTIMA(){
		BigDecimal usu=BigDecimal.ZERO;
		if(null!=detalle && detalle.size()>0){
			for(Detalle det:detalle){
				if(det.tipoAporte==CalculaCapitalCuotaServiceUtil.AMTIMA){
					if(!det.isBorradoLogico()){
						usu=usu.add(det.getInteres());
					}
				}
			}
			
		}
		return usu;
	}
	
	public BigDecimal getTotalAMTIMA(){
		BigDecimal usu=BigDecimal.ZERO;
		if(null!=detalle && detalle.size()>0){
			for(Detalle det:detalle){
				if(det.tipoAporte==CalculaCapitalCuotaServiceUtil.AMTIMA){
					if(!det.isBorradoLogico()){
						usu=usu.add(det.getCapital()).add(det.getInteres());
					}
				}
			}
			
		}
		return usu;
	}
	
	public BigDecimal getCapitalTotal(){
		BigDecimal usu=BigDecimal.ZERO;
		if(null!=detalle && detalle.size()>0){
			for(Detalle det:detalle){
				if(!det.isBorradoLogico()){
					usu=usu.add(det.getCapital());
				}
			}
			
		}
		return usu;
	}
	
	public BigDecimal getInteresTotal(){
		BigDecimal usu=BigDecimal.ZERO;
		if(null!=detalle && detalle.size()>0){
			for(Detalle det:detalle){				
				if(!det.isBorradoLogico()){
					usu=usu.add(det.getInteres());
				}
			}
			
		}
		return usu;
	}
	
	public BigDecimal getTotal(){
		BigDecimal usu=BigDecimal.ZERO;
		if(null!=detalle && detalle.size()>0){
			for(Detalle det:detalle){				
					if(!det.isBorradoLogico()){
						usu=usu.add(det.getInteres()!=null?det.getInteres():BigDecimal.ZERO).add(det.getCapital()!=null?det.getCapital():BigDecimal.ZERO);
					}
			}
			
		}
		return usu;
	}

	static public class Detalle {
		transient private boolean borradoLogico = false;
		private int id;
		private Date fechaPagado;
		private BigDecimal montoPagado;
		private BigDecimal capital;
		private BigDecimal capitalOriginal;
		private BigDecimal interes;
		private BigDecimal interesAFechaPagada;
		private int cantidadAfiliados;
		private boolean agregadoManual = false;
		private int tipoAporte;
		
		
		public Detalle() {			
			super();
		}


		public Detalle(Date fechaPagado, BigDecimal montoPagado,
				BigDecimal capital, BigDecimal interes, int id) {			
			this.fechaPagado = fechaPagado;
			this.montoPagado = montoPagado;
			this.capital = capital;
			this.capitalOriginal = capital;
			this.interes = interes;
			this.id = id;
		}
	
		public Detalle(PagosEmpresa pago) {			
			this.fechaPagado = pago.getFecha();
			this.montoPagado = pago.getMonto();
			this.tipoAporte=pago.getTipoBoleta();
		}

		public Date getFechaPagado() {
			return fechaPagado;
		}

		public void setFechaPagado(Date fechaPagado) {
			this.fechaPagado = fechaPagado;
		}

		public String getFechaPagadoAsString() {
			return null != fechaPagado ? DateUtils.format(fechaPagado,
					DateUtils.SHORT) : "";
		}

		public BigDecimal getMontoPagado() {
			return montoPagado;
		}

		public String getMontoPagadoAsString() {
			return montoPagado != null ? montoPagado.toString() : "";
		}

		public void setMontoPagado(BigDecimal montoPagado) {
			this.montoPagado = montoPagado;
		}

		public BigDecimal getCapital() {
			return capital;
		}

		public void setCapital(BigDecimal capital) {
			this.capital = capital;
		}

		public BigDecimal getInteres() {
			return interes;
		}

		public void setInteres(BigDecimal interes) {
			this.interes = interes;
		}

		public void setId(int id) {
			this.id = id;
		}

		public int getId() {
			return id;
		}

		public boolean isBorradoLogico() {
			return borradoLogico;
		}

		public void setBorradoLogico(boolean borradoLogico) {
			this.borradoLogico = borradoLogico;
		}

		public boolean isAgregadoManual() {
			return agregadoManual;
		}

		public void setAgregadoManual(boolean agregadoManual) {
			this.agregadoManual = agregadoManual;
		}

		public int getCantidadAfiliados() {
			return cantidadAfiliados>0?cantidadAfiliados:1;
		}

		public void setCantidadAfiliados(int cantidadAfiliados) {
			this.cantidadAfiliados = cantidadAfiliados;
		}

		public int getTipoAporte() {
			return tipoAporte;
		}

		public void setTipoAporte(int tipoAporte) {
			this.tipoAporte = tipoAporte;
		}

		public BigDecimal getCapitalOriginal() {
			return capitalOriginal;
		}

		public void setCapitalOriginal(BigDecimal calculado) {
			this.capitalOriginal = calculado;
		}

		public BigDecimal getInteresAFechaPagada() {
			return interesAFechaPagada;
		}

		public void setInteresAFechaPagada(BigDecimal interesAFechaPagada) {
			this.interesAFechaPagada = interesAFechaPagada;
		}
						
		
	}

	public void calcularSaldoConInteres(Date vincimientoOriginal,
			List<InteresAfip> intereses, Date fechaPago) throws SystemException {
		Collections.sort(detalle, new Comparator<Detalle>() {
			public int compare(Detalle di, Detalle dd) {
				if (di.getFechaPagado() != null && dd.getFechaPagado() != null) {
					return di.getFechaPagado().compareTo(dd.getFechaPagado());
				} else if (di.getFechaPagado() == null
						&& dd.getFechaPagado() != null) {
					return -1;
				} else if (di.getFechaPagado() != null
						&& dd.getFechaPagado() == null) {
					return 1;
				}
				return 0;
			}
		});
		
//		_log.debug("Fecha Vencimiento Afip: "+ vincimientoOriginal);
		
		if (detalle.size() == 1 && detalle.get(0).getFechaPagado() == null) {
//			_log.debug("111");
			if (!detalle.get(0).isAgregadoManual() && !detalle.get(0).isBorradoLogico()) {
				BigDecimal interes = AfipServiceUtil.calculoInteres(calculado,
						vincimientoOriginal, fechaPago, intereses);
				detalle.get(0).setCapitalOriginal(calculado);
				detalle.get(0).setCapital(calculado);
				detalle.get(0).setInteres(interes);
				detalle.get(0).setInteresAFechaPagada(BigDecimal.ZERO);
			}
		} else {
//			_log.debug("222");
			BigDecimal capitalActualizado = null;
			for (int i = 0; i < detalle.size(); i++) {
				Detalle det = detalle.get(i);
				if (!det.isAgregadoManual() && !det.isBorradoLogico()) {
					Date fechaPagoAux = null;
					Date fechaVenc = null;
					det.setCapitalOriginal(calculado);
					if (i == 0) {
						capitalActualizado = calculado;
						if (det.getFechaPagado() != null) {
							fechaPagoAux = det.getFechaPagado();
						} else {
							fechaPagoAux = fechaPago;
						}
						fechaVenc = vincimientoOriginal;
					} else {
						Calendar aux = Calendar.getInstance();
//						aux.setTime(detalle.get(i - 1).getFechaPagado()  ); DS -2022-09-22 Comentado por error al tener fecha pagado null
						aux.setTime(detalle.get(i - 1).getFechaPagado()!=null? detalle.get(i - 1).getFechaPagado() :fechaPago);
						aux.add(Calendar.DATE, 1);
						fechaVenc = aux.getTime();
						if (fechaVenc.before(vincimientoOriginal)) {
							fechaVenc = vincimientoOriginal;
						}

//						fechaPagoAux = det.getFechaPagado();  DS -2022-09-22 Comentado por error al tener fecha pagado null
						fechaPagoAux = det.getFechaPagado()!=null? det.getFechaPagado():fechaPago;
					}
//					_log.debug("Fecha Vencimiento: "+ fechaVenc);
//					_log.debug("Fecha Pago Aux: "+ fechaPagoAux);
					
					BigDecimal interesAFechaPagada = AfipServiceUtil
							.calculoInteres(capitalActualizado, fechaVenc,
									fechaPagoAux, intereses);
					capitalActualizado = capitalActualizado.add(
							interesAFechaPagada).subtract(
							detalle.get(i).getMontoPagado());					
					det.setCapital(BigDecimal.ZERO);
					det.setInteres(BigDecimal.ZERO);
					det.setInteresAFechaPagada(interesAFechaPagada);
					
//					_log.debug("interesAFechaPagada: "+ interesAFechaPagada);
					if (i == (detalle.size() - 1)) {
						Calendar aux = Calendar.getInstance();
						aux.setTime(fechaPagoAux);
						aux.add(Calendar.DATE, 1);

						if (aux.getTime().before(vincimientoOriginal)) {
							aux.setTime(vincimientoOriginal);
						}

						BigDecimal interesAFechaPagoActa = AfipServiceUtil
								.calculoInteres(capitalActualizado,
										aux.getTime(), fechaPago, intereses);
						det.setCapital(capitalActualizado);
						det.setInteres(interesAFechaPagoActa);
						
//						_log.debug("interesAFechaObligacion: "+ interesAFechaPagoActa);
					}
				}
			}
		}
	}

	public BigDecimal getMontoPagadoTotal() {
		BigDecimal total = new BigDecimal("0");
		for (Detalle det : detalle) {
			if(!det.isBorradoLogico()){
				total = total.add(det.getMontoPagado() != null ? det
						.getMontoPagado() : BigDecimal.ZERO);
			}
		}
		return total;
	}

	public int getTipoAporte() {
		return tipoAporte;
	}
	
	public String getTipoAporteAsString(){
		if(tipoAporte==1){//AMTIMA
			return "AMTIMA";
		}
		
		if(tipoAporte==2){//Cuota Social
			return "Cta.Soc.UOMA";
		}
		
		if(tipoAporte==3){//Cuota Usufructo
			return "Usufructo";
		}
		if(tipoAporte==4){//Art 46.
			return "Art.46";
		}
		if(tipoAporte==5){//Cuota Solidario
			return "Aporte Solidario";
		}
		return "";
	}

	public void setTipoAporte(int tipoBoleta) {
		this.tipoAporte = tipoBoleta;
	}

	public String getCamara() {
		return camara;
	}

	public void setCamara(String camara) {
		this.camara = camara;
	}

	public String getCategoria() {
		return categoria;
	}

	public void setCategoria(String categoria) {
		this.categoria = categoria;
	}
	
	
	
	
}
