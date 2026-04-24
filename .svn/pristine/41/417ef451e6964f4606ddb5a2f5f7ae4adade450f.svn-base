package ar.com.uoma.beans;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import ar.com.empresas.beans.Actividad;
import ar.com.ospim.global.beans.Banco;
import ar.com.ospim.global.beans.ContactoElectronico;
import ar.com.ospim.global.beans.Domicilio;
import ar.com.ospim.global.beans.Empresa;
import ar.com.ospim.global.beans.Localidad;
import ar.com.ospim.global.beans.Provincia;
import ar.com.ospim.global.beans.Regimen;
import ar.com.ospim.tesoreria.beans.CuentaBancaria;

public class Proveedor extends Empresa implements Serializable {
	private static Log _log = LogFactoryUtil.getLog(Proveedor.class);
	private Integer id;
	private boolean agenteRetencion;
	private String formaPago;
	private CuentaBancaria cuentaBcria;
	private Domicilio domicilio;
	private Double porcentajeExencion;
	
	public Proveedor() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Proveedor(String cuit, String sucur, String razon, int id_ramo, String posicionGanancias, Date bajaFecha) {
		super(cuit, sucur, razon, id_ramo, posicionGanancias, bajaFecha);
		// TODO Auto-generated constructor stub
	}

	public Proveedor(String cuit, String sucur, String razon, int id_ramo) {
		super(cuit, sucur, razon, id_ramo);
		// TODO Auto-generated constructor stub
	}

	public Proveedor(String cuit2, String sucur, String razon, Integer seccional) {
		super(cuit2, sucur, razon, seccional);
		// TODO Auto-generated constructor stub
	}

	public Proveedor(String cuit, String sucur, String razon) {
		super(cuit, sucur, razon);
		// TODO Auto-generated constructor stub
	}

	public Proveedor(String cuit, String sucur) {
		super(cuit, sucur);
		// TODO Auto-generated constructor stub
	}

	public Proveedor(String cuit) {
		super(cuit);
		// TODO Auto-generated constructor stub
	}
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public boolean isAgenteRetencion() {
		return agenteRetencion;
	}

	public void setAgenteRetencion(boolean agenteRetencion) {
		this.agenteRetencion = agenteRetencion;
	}

	public String getFormaPago() {
		return formaPago;
	}

	public void setFormaPago(String formaPago) {
		this.formaPago = formaPago;
	}

	public CuentaBancaria getCuentaBcria() {
		if( getCuentasBcrias()!=null &&  getCuentasBcrias().size()>0) {
			cuentaBcria=getCuentasBcrias().get(0);
		}
		return cuentaBcria;
	}

	public void setCuentaBcria(CuentaBancaria cuentaBcria) {
		this.cuentaBcria = cuentaBcria;
	}

	public Domicilio getDomicilio() {
		if(getDomicilios()!=null && getDomicilios().size()>0) {
			domicilio=getDomicilios().get(0);
		}
		return domicilio;
	}

	public void setDomicilio(Domicilio domicilio) {
		this.domicilio = domicilio;
	}

	public Double getPorcentajeExencion() {
		return porcentajeExencion;
	}

	public void setPorcentajeExencion(Double porcentajeExencion) {
		this.porcentajeExencion = porcentajeExencion;
	}
	
	public static Proveedor getMapping(ResultSet rs, String prefix)
			throws SQLException {
		Proveedor emp = new Proveedor(rs.getString(prefix + "cuit"),
				rs.getString(prefix + "sucursal"), rs.getString(prefix
						+ "razon_social"));
		emp.setActividadPrincipal(new Actividad(rs.getInt("actividad_principal"), rs.getString("actividad_principal_descripcion")));
		emp.setActividadSecundaria(new Actividad(rs.getInt("actividad_secundaria"), rs.getString("actividad_secundaria_descripcion")));	
		emp.setImpIva(rs.getString(prefix + "condicion_iva"));
		emp.setMonotributo(rs.getString(prefix + "categoria_monotributo"));
		
		emp.setPorcentajeExencion(rs.getDouble(prefix +"retencion_exencion"));
		emp.setFormaPago(rs.getString(prefix+"forma_pago"));
		emp.setRegimen(new Regimen(rs.getInt(prefix + "regimen_ganancias")));
		emp.setAgenteRetencion(rs.getBoolean(prefix+"agente_retencion"));
		
		emp.setId(rs.getInt(prefix + "id"));
		
		Banco banco = new Banco();
		if(rs.getInt(prefix + "banco_id")>0) {
			banco.setId_banco(rs.getInt(prefix + "banco_id"));
		}
		
		CuentaBancaria ctaB = new CuentaBancaria();
		ctaB.setId_cuenta_bcria(rs.getInt(prefix+"ctabcria_id"));
		ctaB.setBanco(banco);
		ctaB.setCBU(rs.getString(prefix+"cbu"));
		ctaB.setDescripcion(rs.getString(prefix +"nro_cuenta_bcria"));
		ctaB.setBajaFecha(rs.getDate(prefix+"baja_fecha"));
		
		emp.setCuentaBcria(ctaB);
		if(rs.getString(prefix+"email")!=null) {
		   ContactoElectronico ce =new ContactoElectronico();
		   ce.setTipo(ContactoElectronico.Tipo.EMAIL);
		   ce.setContacto(rs.getString(prefix+"email"));
		   
		   List<ContactoElectronico>list=new ArrayList<ContactoElectronico>();
		   list.add(ce);
		   emp.setContactosElectronicos(list);
		}
		
		Provincia provincia = new Provincia();
		if(rs.getInt(prefix+"provincia")>0) {
			provincia.setId(rs.getInt(prefix+"provincia"));
		}
		
		Localidad localidad = new Localidad();
		if(rs.getInt(prefix+"localidad")>0) {
			localidad.setId(rs.getInt(prefix+"localidad"));
		}
		
		Domicilio domicilio =new Domicilio();
		if(rs.getInt(prefix +"domicilio_id")>0) {
			domicilio.setId_domicilio(rs.getInt(prefix +"domicilio_id"));
			domicilio.setCalle(rs.getString(prefix+"calle"));
			domicilio.setNumero(rs.getString(prefix+"numero"));
			domicilio.setPiso(rs.getString(prefix+ "piso"));
			domicilio.setDepto(rs.getString(prefix+"depto"));
			domicilio.setPostal_codi(rs.getString( prefix+"postal_codi"));
		}
		
		domicilio.setProvincia(provincia);
		domicilio.setLocalidad(localidad);
		emp.setDomicilio(domicilio);
		return emp;
	}
	
}
