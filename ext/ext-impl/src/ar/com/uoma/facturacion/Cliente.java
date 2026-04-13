package ar.com.uoma.facturacion;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import ar.com.ospim.global.beans.ContactoElectronico;
import ar.com.ospim.global.beans.Domicilio;

public class Cliente implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1128014231451368626L;
	
	public enum ESTADOS {
		SELECCIONADO, ALTA, MODIFICACION, BAJA
	};
	
	public enum TIPOS_CLIENTE {
		AFILIADO, VISITA, EMPRESA
	};
	
	private int id;
	private TIPOS_CLIENTE tipo;
	private String apellido;
	private String nombre;
	private String documentoTipo;
	private String documentoNro;
	private String cuil;
	private String cuilTitular;
	private Integer inte;
	private String cuit;
	private String sucursal;
	private String razonSocial;
	private String categoriaIVA;
	private Domicilio domicilio;
	private List<ContactoElectronico> contactos;
	private String observaciones;
	private ESTADOS estado;
	
	
	
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public TIPOS_CLIENTE getTipo() {
		return tipo;
	}

	public void setTipo(TIPOS_CLIENTE tipo) {
		this.tipo = tipo;
	}

	public String getApellido() {
		return apellido;
	}

	public void setApellido(String apellido) {
		this.apellido = apellido;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getDocumentoTipo() {
		return documentoTipo;
	}

	public void setDocumentoTipo(String documentoTipo) {
		this.documentoTipo = documentoTipo;
	}

	public String getDocumentoNro() {
		return documentoNro;
	}

	public void setDocumentoNro(String documentoNro) {
		this.documentoNro = documentoNro;
	}

	public String getCuil() {
		return cuil;
	}

	public void setCuil(String cuil) {
		this.cuil = cuil;
	}

	public String getCuilTitular() {
		return cuilTitular;
	}

	public void setCuilTitular(String cuilTitular) {
		this.cuilTitular = cuilTitular;
	}

	public Integer getInte() {
		return inte;
	}

	public void setInte(Integer inte) {
		this.inte = inte;
	}

	public String getCuit() {
		return cuit;
	}

	public void setCuit(String cuit) {
		this.cuit = cuit;
	}

	public String getSucursal() {
		return sucursal;
	}

	public void setSucursal(String sucursal) {
		this.sucursal = sucursal;
	}

	public String getRazonSocial() {
		return razonSocial;
	}

	public void setRazonSocial(String razonSocial) {
		this.razonSocial = razonSocial;
	}

	public String getCategoriaIVA() {
		return categoriaIVA;
	}

	public void setCategoriaIVA(String categoriaIVA) {
		this.categoriaIVA = categoriaIVA;
	}

	public Domicilio getDomicilio() {
		return domicilio;
	}

	public void setDomicilio(Domicilio domicilio) {
		this.domicilio = domicilio;
	}

	public List<ContactoElectronico> getContactos() {
		return contactos;
	}

	public void setContactos(List<ContactoElectronico> contactos) {
		this.contactos = contactos;
	}

	public String getObservaciones() {
		return observaciones;
	}

	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}

	public ESTADOS getEstado() {
		return estado;
	}

	public void setEstado(ESTADOS estado) {
		this.estado = estado;
	}


	public static Cliente getMapping(String prefix, ResultSet rs) throws SQLException {
		
		Cliente c = new Cliente();
		
		c.setId(rs.getInt(prefix + "id"));
		c.setTipo(Cliente.TIPOS_CLIENTE.valueOf(Cliente.TIPOS_CLIENTE.class, rs.getString(prefix + "tipo")));
		c.setApellido(rs.getString(prefix + "apellido"));
		c.setNombre(rs.getString(prefix + "nombre"));
		c.setDocumentoTipo(rs.getString(prefix + "documento_tipo"));
		c.setDocumentoNro(rs.getString(prefix + "documento_numero"));
		c.setCuil(rs.getString(prefix + "cuil"));
		c.setCuilTitular(rs.getString(prefix + "cuil_titular"));
		c.setCategoriaIVA(rs.getString(prefix + "condicion_iva"));
		c.setEstado(Cliente.ESTADOS.valueOf(Cliente.ESTADOS.class, rs.getString(prefix + "estado")));
		c.setInte(rs.getInt(prefix + "inte"));
		c.setCuit(rs.getString(prefix + "cuit"));
		c.setSucursal(rs.getString(prefix + "sucursal"));
		c.setRazonSocial(rs.getString(prefix + "razon_social"));
		
		return c;
	}
	

	public static Cliente getMapping2(String prefix, ResultSet rs) throws SQLException {
		
		Cliente c = new Cliente();
		
		c.setId(rs.getInt(prefix + "id"));
		c.setApellido(rs.getString(prefix + "apellido"));
		c.setNombre(rs.getString(prefix + "nombre"));
		c.setDocumentoTipo(rs.getString(prefix + "documento_tipo"));
		c.setDocumentoNro(rs.getString(prefix + "documento_numero"));
		c.setCuil(rs.getString(prefix + "cuil"));
		c.setCategoriaIVA(rs.getString(prefix + "condicion_iva"));

		
		return c;
	}
	
	
	public String getDescripcionCliente() {
		String ret="";
		if(apellido!=null) {
			ret+=apellido;
			
			if(nombre!=null) {
				ret += " " +nombre;
			}
			
			if(documentoNro != null) {
				ret += " (" +documentoNro +")";
			}
			
		}else {
			if(razonSocial!=null) {
			  ret = razonSocial;
			}  
			if(cuit!=null) {
			  ret += " ("+cuit+")";	
			}
		}
		return ret;
	}
	
	public String getClienteNombre() {
		String ret="";
		if(apellido!=null) {
			ret+=apellido;
			
			if(nombre!=null) {
				ret += " " +nombre;
			}
			
		}else {
			if(razonSocial!=null) {
			  ret = razonSocial;
			}  
		}
		return ret;
	}
	
	public String getClienteDocumento() {
		String ret="";
		if(apellido!=null) {
			if(documentoNro != null) {
				ret += documentoNro;
			}
			
		}else {
			if(cuit!=null) {
			  ret += cuit;	
			}
		}
		return ret;
	}
}
