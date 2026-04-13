/**
 */

package ar.com.ospim.afiliados.empleadores.action;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import ar.com.ospim.global.beans.ContactoElectronico;
import ar.com.ospim.global.beans.Domicilio;
import ar.com.ospim.global.beans.Empresa;
import ar.com.ospim.global.beans.EntidadCamaraEmpresa;
import ar.com.ospim.global.beans.PosicionIva;
import ar.com.ospim.global.beans.Telefono;
import ar.com.ospim.global.services.EmpresaServiceUtil;

import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;

/**
 * <a href="EmpleadoresBaseAction.java.html"><b><i>View Source</i></b></a>
 * 
 * @author Martin Moreyra
 * 
 */
public class EmpleadoresBaseAction extends PortletAction {

	protected Empresa getEmpleadorEntry(HttpServletRequest request)
			throws Exception {

		String cuit = ParamUtil.getString(request, "cuit", null);
		String sucu = ParamUtil.getString(request, "sucu", null);
		if (sucu == null) {
			sucu = ParamUtil.getString(request, "sucursal", null);
		}

		Empresa empresa = null;

		if (cuit != null && cuit.length() > 0) {
			empresa = EmpresaServiceUtil.getEmpleadorCompleto(cuit, sucu);
		}

		return empresa;
	}
	
	public Empresa getDatosFiscalesFromRequest(HttpServletRequest req, Empresa empresa) {
		String calle = ParamUtil.getString(req, "callefisc");
		String numero = ParamUtil.getString(req, "numerofisc");
		String piso = ParamUtil.getString(req, "pisofisc");
		String dpto = ParamUtil.getString(req, "departamentofisc");
		int provincia = ParamUtil.getInteger(req, "provinciafisc");
		int localidad = ParamUtil.getInteger(req, "localidadfisc");
		String cod_postal = ParamUtil.getString(req, "cod_postalfisc");
		int iva = ParamUtil.getInteger(req, "iva");
		int entidad = ParamUtil.getInteger(req, "entidad");
		int idDomicilio = ParamUtil.getInteger(req, "id_domiciliofisc");
		
		Domicilio domifisc = new Domicilio();
		domifisc.setCalle(calle);
		domifisc.setDepto(dpto);
		domifisc.setId_domicilio(idDomicilio);
		domifisc.setLocalidadId(localidad);
		domifisc.setProvinciaId(provincia);
		domifisc.setNumero(numero);
		domifisc.setPostal_codi(cod_postal);
		domifisc.setPiso(piso);
		empresa.setDomicilioFiscal(domifisc);
		
		empresa.setEntidadCamaraEmpresa(new EntidadCamaraEmpresa(entidad,""));
		return empresa;
	}
	
	public Empresa getEmpleadorFromRequest(HttpServletRequest req, Empresa empresa) {
		String cuit = ParamUtil.getString(req, "cuit");
		String sucu = ParamUtil.getString(req, "sucursal");
		String desc = ParamUtil.getString(req, "desc");
		int ramo = ParamUtil.getInteger(req, "ramo");
		int seccional = ParamUtil.getInteger(req, "id_seccional");
		int provincia = ParamUtil.getInteger(req, "provincia");
		int localidad = ParamUtil.getInteger(req, "localidad");
		String cod_postal = ParamUtil.getString(req, "cod_postal");
		String calle = ParamUtil.getString(req, "calle");
		String numero = ParamUtil.getString(req, "numero");
		String piso = ParamUtil.getString(req, "piso");
		String dpto = ParamUtil.getString(req, "departamento");

		String telefono0Pais = ParamUtil.getString(req, "telefono0_pais");
		String telefono0Area = ParamUtil.getString(req, "telefono0_area");
		String telefono0Numero = ParamUtil.getString(req, "telefono0_numero");
		String telefono0Ext = ParamUtil.getString(req, "telefono0_ext");

		String telefono1Pais = ParamUtil.getString(req, "telefono1_pais");
		String telefono1Area = ParamUtil.getString(req, "telefono1_area");
		String telefono1Numero = ParamUtil.getString(req, "telefono1_numero");
		String telefono1Ext = ParamUtil.getString(req, "telefono1_ext");

		String telefono2Pais = ParamUtil.getString(req, "telefono2_pais");
		String telefono2Area = ParamUtil.getString(req, "telefono2_area");
		String telefono2Numero = ParamUtil.getString(req, "telefono2_numero");
		String telefono2Ext = ParamUtil.getString(req, "telefono2_ext");

		int idTelefono0 = ParamUtil.getInteger(req, "telefono0_id");
		int idTelefono1 = ParamUtil.getInteger(req, "telefono1_id");
		int idTelefono2 = ParamUtil.getInteger(req, "telefono2_id");
		String fax = ParamUtil.getString(req, "fax");
		String email = ParamUtil.getString(req, "email");
		String sitio = ParamUtil.getString(req, "sitioweb");
		int idFax = ParamUtil.getInteger(req, "fax_id");
		int idEmail = ParamUtil.getInteger(req, "email_id");
		int idSitio = ParamUtil.getInteger(req, "sitioweb_id");
		String contacto = ParamUtil.getString(req, "contacto");
		int idDomicilio = ParamUtil.getInteger(req, "id_domicilio");

		String obs = ParamUtil.getString(req, "observaciones");
		empresa.setObservaciones(obs);
		
		empresa.setCuit(cuit);
		empresa.setSucursal(sucu);
		empresa.setRazon_soc(desc);
		empresa.setNombre_fantasia(desc);
		empresa.setId_seccional(seccional);
		empresa.setContacto(contacto);
		empresa.setId_ramo_empresa(ramo);
		Domicilio domi = new Domicilio();
		domi.setCalle(calle);
		domi.setDepto(dpto);
		domi.setId_domicilio(idDomicilio);
		domi.setLocalidadId(localidad);
		domi.setProvinciaId(provincia);
		domi.setNumero(numero);
		domi.setPostal_codi(cod_postal);
		domi.setPiso(piso);
		empresa.setDomicilio(domi);

		if (telefono0Numero != null || telefono1Numero != null
				|| telefono2Numero != null) {
			List<Telefono> telefonos = new ArrayList<Telefono>();

			if (telefono0Numero != null) {
				Telefono tel = new Telefono();
				tel.setCodigoPais(telefono0Pais);
				tel.setCodigoArea(telefono0Area);
				tel.setNumero(telefono0Numero);
				tel.setExtension(telefono0Ext);
				tel.setId(idTelefono0);
				telefonos.add(tel);
			}

			if (telefono1Numero != null) {
				Telefono tel = new Telefono();
				tel.setCodigoPais(telefono1Pais);
				tel.setCodigoArea(telefono1Area);
				tel.setNumero(telefono1Numero);
				tel.setExtension(telefono1Ext);
				tel.setId(idTelefono1);
				telefonos.add(tel);
			}

			if (telefono2Numero != null) {
				Telefono tel = new Telefono();
				tel.setCodigoPais(telefono2Pais);
				tel.setCodigoArea(telefono2Area);
				tel.setNumero(telefono2Numero);
				tel.setExtension(telefono2Ext);
				tel.setId(idTelefono2);
				telefonos.add(tel);
			}

			//empresa.setTelefonos(telefonos);
		}

		if (fax != null || email != null || sitio != null) {
			List<ContactoElectronico> contactosElectronicos = new ArrayList<ContactoElectronico>();

			if (fax != null) {
				ContactoElectronico contactoE = new ContactoElectronico();
				contactoE.setContacto(fax);
				contactoE.setId(idFax);
				contactoE.setTipo(ContactoElectronico.Tipo.FAX);
				contactosElectronicos.add(contactoE);
			}

			if (email != null) {
				ContactoElectronico contactoE = new ContactoElectronico();
				contactoE.setContacto(email);
				contactoE.setId(idEmail);
				contactoE.setTipo(ContactoElectronico.Tipo.EMAIL);
				contactosElectronicos.add(contactoE);
			}

			if (sitio != null) {
				ContactoElectronico contactoE = new ContactoElectronico();
				contactoE.setContacto(sitio);
				contactoE.setId(idSitio);
				contactoE.setTipo(ContactoElectronico.Tipo.SITIOWEB);
				contactosElectronicos.add(contactoE);
			}
			//empresa.setContactosElectronicos(contactosElectronicos);
		}
		return empresa;
	}

}