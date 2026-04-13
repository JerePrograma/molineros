package ar.com.ospim.global.beans;

import java.util.List;

import ar.com.empresas.beans.Contacto;
import ar.com.ospim.tesoreria.beans.CuentaBancaria;

public interface EntidadPadronUnificado {
	
	String getCuit();

	String getSucursal();

	String getDescripcion();

	int getIdSeccional();
	
	String getDestinoCorrespondencia();
	String getCBU();
	String getPortaCheque();
	
	List<Domicilio> getDomicilios();
	List<Contacto> getContactos();
	List<Contacto> getContactosPorNombreApe();
	List<Contacto> getContactosPorNombreApePersonas();
	
	List<CuentaBancaria> getCuentasBcrias();
	
	void setContactos(List<Contacto> contactos);
}
