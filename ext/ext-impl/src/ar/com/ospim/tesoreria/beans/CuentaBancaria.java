package ar.com.ospim.tesoreria.beans;

import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;

import ar.com.ospim.global.beans.Banco;
import ar.com.ospim.global.beans.PlanCuentas;

public class CuentaBancaria {
	private int id_cuenta_bcria;
	private int nro_cuenta;
	private int sucursal;
	private String sucursalString;
	private String descripcion;
	private Banco banco;
	private PlanCuentas cuentaAsociada;
	private String entidad;
	private String CBU;
	private String modiUsr;
	private Date modiFecha;
	private Date bajaFecha;	
	
	
	public CuentaBancaria() {
	}

	public CuentaBancaria(String desc) {
		this.descripcion = desc;
	}

	public CuentaBancaria(int id) {
		this.id_cuenta_bcria = id;
	}
	public CuentaBancaria(int id_cta_bcria, String desc) {
		this.id_cuenta_bcria=id_cta_bcria;
		this.descripcion=desc;
	}

	public CuentaBancaria(int id_cta_bcria, int nro_cuenta, int sucursal,
			String desc, int id_banco, String desc_bco, String entidad) {
		this.id_cuenta_bcria = id_cta_bcria;
		this.nro_cuenta = nro_cuenta;
		this.sucursal = sucursal;
		this.descripcion = desc;
		this.banco = new Banco(id_banco, desc_bco);
		this.entidad=entidad;
	}
	
	public static CuentaBancaria getMapping(ResultSet rs) throws Exception{
		CuentaBancaria cuenta=new CuentaBancaria();		
		cuenta.setId_cuenta_bcria(rs.getInt("id_cta_bcria"));
		cuenta.setDescripcion(rs.getString("cta_bcria"));
		cuenta.setCBU(rs.getString("cbu"));
		cuenta.setBanco(new Banco(rs.getInt("id_banco"), rs.getString("banco")));
		cuenta.setSucursalString(rs.getString("sucur_cta"));
		cuenta.setModiUsr(rs.getString("modi_usr"));
		cuenta.setModiFecha(rs.getDate("modi_fecha"));
		return cuenta;		
	}

	public String getCtaBcriaAsString() {
		return nro_cuenta + "/" + sucursal + " " + descripcion + " - "
				+ banco.getDescripcion_banco();
	}

	public int getId_cuenta_bcria() {
		return id_cuenta_bcria;
	}

	public void setId_cuenta_bcria(int idCuentaBcria) {
		id_cuenta_bcria = idCuentaBcria;
	}

	public int getNro_cuenta() {
		return nro_cuenta;
	}
	public String getNro_cuentaAsString() {
		if(nro_cuenta>0){
			return String.valueOf(nro_cuenta);
		}else{
			return "0";
		}
	}

	public void setNro_cuenta(int nroCuenta) {
		nro_cuenta = nroCuenta;
	}

	public int getSucursal() {
		return sucursal;
	}

	public void setSucursal(int sucursal) {
		this.sucursal = sucursal;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public Banco getBanco() {
		return banco;
	}

	public void setBanco(Banco banco) {
		this.banco = banco;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CuentaBancaria other = (CuentaBancaria) obj;
		if (id_cuenta_bcria != other.id_cuenta_bcria)
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id_cuenta_bcria;
		return result;
	}

	public void setCuentaAsociada(PlanCuentas cuentaAsociada) {
		this.cuentaAsociada = cuentaAsociada;
	}

	public PlanCuentas getCuentaAsociada() {
		return cuentaAsociada;
	}

	public String getEntidad() {
		return entidad;
	}

	public void setEntidad(String entidad) {
		this.entidad = entidad;
	}

	public String getCBU() {
		return CBU;
	}

	public void setCBU(String cBU) {
		CBU = cBU;
	}

	public String getModiUsr() {
		return null!=modiUsr?modiUsr:"";		
	}

	public void setModiUsr(String modiUsr) {
		this.modiUsr = modiUsr;
	}

	public Date getModiFecha() {
		return modiFecha;
	}
	
	public String getModiFechaAsString() {
		if(null!=modiFecha){
			SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");
			return sdf.format(modiFecha);
		}else{	
			return "";
		}
	}


	public void setModiFecha(Date modiFecha) {
		this.modiFecha = modiFecha;
	}

	public String getSucursalString() {
		return sucursalString;
	}

	public void setSucursalString(String sucursalString) {
		this.sucursalString = sucursalString;
	}

	public Date getBajaFecha() {
		return bajaFecha;
	}

	public void setBajaFecha(Date bajaFecha) {
		this.bajaFecha = bajaFecha;
	}
	
			

}
