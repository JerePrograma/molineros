package ar.com.uoma.proveedores.action;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.portlet.PortletSession;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.compass.core.util.backport.java.util.Collections;

import ar.com.ospim.afiliados.WebKeysAfiliados;
import ar.com.ospim.afiliados.services.PlanServiceUtil;
import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.beans.Domicilio;
import ar.com.ospim.global.beans.Empresa;
import ar.com.ospim.global.beans.ItemSubdiarioEgreso;
import ar.com.ospim.global.beans.ItemSubdiarioIngreso;
import ar.com.ospim.global.beans.Localidad;
import ar.com.ospim.global.beans.SubdiarioComprobante;
import ar.com.ospim.global.beans.SubdiarioEgresoColumna;
import ar.com.ospim.global.services.EmpresaServiceUtil;
import ar.com.ospim.global.services.OrdenPagoServiceUtil;
import ar.com.ospim.tesoreria.beans.CuentaBancaria;
import ar.com.ospim.tesoreria.beans.FichaBoletaPortal;
import ar.com.ospim.tesoreria.reportes.ReporteSubdiarioIngresoExcel;
import ar.com.ospim.tesoreria.service.ContabilidadServiceUtil;
import ar.com.ospim.tesoreria.service.PortalEmpleadoresServiceUtil;
import ar.com.ospim.tesoreria.services.MovimientoBancarioServiceUtil;
import ar.com.ospim.util.DateUtils;
import ar.com.ospim.webservice.dto.AfiliacionPrevencionDTO;
import ar.com.uoma.beans.Proveedor;
import ar.com.uoma.proveedores.services.ProveedoresServiceUtil;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.JSONAction;
import com.liferay.portal.util.PortalUtil;

public class ExisteCuitEmpresaProveedor extends JSONAction {
	private static Log _log = LogFactoryUtil
			.getLog(ExisteCuitEmpresaProveedor.class);
	@Override
	public String getJSON(ActionMapping arg0, ActionForm arg1,
			HttpServletRequest req, HttpServletResponse arg3) throws Exception {
		
		
	    String resultado = "{}";
		 
		String cuit= ParamUtil.getString(req, "cuit");
		String sucursal= ParamUtil.getString(req, "sucursal");
		Boolean existe=false;
		String mensaje ="";
		String razonsocial="";
		String actividadprincipal="";
		String actividadprincipaldescripcion="";
		String actividadsecundaria="";
		String actividadsecundariadescripcion="";
		String posicioniva="";
		String monotributo="";
		String cbu="";
		String iddomicilio="";
		String calle="";
		String nro="";
		String piso="";
		String depto="";
		String codpostal="";
		String localidad="";
		String provincia="";
		String email="";
		String idctabcria="";
		String nroctabcria="";
		String idbanco="";
		String regimenGanancias="";
				
		if(cuit!=null && cuit!=""){
			
		  List <Proveedor> proveedores = ProveedoresServiceUtil.getProveedores(cuit, sucursal, null, null);	
		  if(proveedores !=null && proveedores.size()>0) existe=true;
		  
		  
		  if(proveedores==null ||proveedores.size() ==0) {
			  Empresa empresa=EmpresaServiceUtil.getEmpleadorCompleto(cuit, sucursal);
		      if(empresa!=null) {
		    	 
		         mensaje="Datos encontrado en Empresas";
		         razonsocial=empresa.getRazon_soc();
		         if(empresa.getActividadPrincipal()!=null &&  empresa.getActividadPrincipal().getCodigo() !=0) {
		             actividadprincipal= String.valueOf(empresa.getActividadPrincipal().getId());
		             actividadprincipaldescripcion=empresa.getActividadPrincipal().getDescripcion();
		         }
		         if(empresa.getActividadSecundaria()!=null &&  empresa.getActividadSecundaria().getCodigo() !=0) {
		            actividadsecundaria= String.valueOf(empresa.getActividadSecundaria().getId());
		            actividadsecundariadescripcion=empresa.getActividadSecundaria().getDescripcion();
		         }
		         if(empresa.getImpIva()!=null) {
		        	 posicioniva=empresa.getImpIva();
		         }
		         if(empresa.getMonotributo()!=null) {
		        	 monotributo=empresa.getMonotributo();
		         }
		         
		         /*
		         if(empresa.getCBU()!=null) {
		        	 cbu=empresa.getCBU();
		         }
		         */
		         // Busco Domicilios FISCAL si lo hubiere, caso contrario el primero de la lista
		         
		         if(empresa.getDomicilios()!=null && empresa.getDomicilios().size()>0) {
		        	 boolean encontro=false;
		        	 for(Domicilio d:empresa.getDomicilios()) {
		        		 //if(d.getDomi_tipo().indexOf("FISCAL") != -1) {
		        		 if("F".equalsIgnoreCase(d.getDomi_tipo())) {	 
		        		   encontro=true; 	 
		        	       iddomicilio=String.valueOf(d.getId_domicilio());
		        	       if(d.getCalle()!=null) {
		        	          calle=d.getCalle();
		        	       }
		        	       if(d.getNumero()!=null) {
		        		      nro= d.getNumero();
		        	       }
		        	       if(d.getPiso()!=null) {
		        	    	   piso=d.getPiso();
		        	       }
		        	       if(d.getDepto()!=null) {
		        	    	   depto=d.getDepto();
		        	       }
		        	       if(d.getLocalidad()!=null) {
		        	    	   localidad =String.valueOf( d.getLocalidad().getId());
		        	    	   provincia =String.valueOf(d.getProvincia().getId());
		        	    	   codpostal=String.valueOf(d.getLocalidad().getCod_postal());
		        	       }
		        	       
		        		 }
		        		 
		        	 }
		        	 
		        	 if(!encontro) {
		        		 Domicilio d = empresa.getDomicilios().get(0);
		        		 iddomicilio=String.valueOf(d.getId_domicilio());
		        	       if(d.getCalle()!=null) {
		        	          calle=d.getCalle();
		        	       }
		        	       if(d.getNumero()!=null) {
		        		      nro= d.getNumero();
		        	       }
		        	       if(d.getPiso()!=null) {
		        	    	   piso=d.getPiso();
		        	       }
		        	       if(d.getDepto()!=null) {
		        	    	   depto=d.getDepto();
		        	       }
		        	       if(d.getLocalidad()!=null) {
		        	    	   localidad =String.valueOf( d.getLocalidad().getId());
		        	    	   provincia =String.valueOf(d.getProvincia().getId() );
		        	    	   codpostal=String.valueOf(d.getLocalidad().getCod_postal());
		        	       }
		        		 
		        	 }
		         }
		        	
		         if(empresa.getEmail()!=null) {
		        	 email= empresa.getEmail().getContacto();
		         }
		         
		         if(empresa.getCuentasBcrias()!=null && !empresa.getCuentasBcrias().isEmpty()) {
		        	 for(CuentaBancaria cb:empresa.getCuentasBcrias()) {
		        		if(cb.getBajaFecha()==null) {
		        			idctabcria=String.valueOf(cb.getId_cuenta_bcria());
		        			nroctabcria=cb.getNro_cuentaAsString();
		        			idbanco=String.valueOf( cb.getBanco().getId_banco());
		        			cbu=cb.getCBU()==null?"":cb.getCBU();
		        		}
		        	 }
		         }
		         
		         if(empresa.getRegimen()!=null && empresa.getRegimen().getCodigoRegimen()!=null && empresa.getRegimen().getCodigoRegimen()!=0) {
		        	 regimenGanancias=empresa.getRegimen().getCodigoRegimen().toString();
		         }
		         
		      }
		  }    
		  
		}			
		resultado = "{ \"existe\" : \"" 
				    + existe 
				    +"\",\"mensaje\" : \""
				    + mensaje
				    +"\",\"razonsocial\" : \""
				    + razonsocial
				    +"\",\"actividadprincipal\" : \""
				    + actividadprincipal
				    +"\",\"actividadprincipaldescripcion\" : \""
				    + actividadprincipaldescripcion
				    +"\",\"actividadsecundaria\" : \""
				    + actividadsecundaria
				    +"\",\"actividadsecundariadescripcion\" : \""
				    + actividadsecundariadescripcion
				    +"\",\"posicioniva\" : \""
				    + posicioniva
				    +"\",\"monotributo\" : \""
				    + monotributo
				    +"\",\"cbu\" : \""
				    + cbu
				    +"\",\"iddomicilio\" : \""
				    + iddomicilio
				    +"\",\"calle\" : \""
				    + calle
				    +"\",\"nro\" : \""
				    + nro
				    +"\",\"piso\" : \""
				    + piso
				    +"\",\"depto\" : \""
				    + depto
				    +"\",\"codpostal\" : \""
				    + codpostal
				    +"\",\"localidad\" : \""
				    + localidad
				    +"\",\"provincia\" : \""
				    + provincia
				    +"\",\"email\" : \""
				    + email
				    +"\",\"idctabcria\" : \""
				    + idctabcria
				    +"\",\"nroctabcria\" : \""
				    + nroctabcria
				    +"\",\"regimenganancias\" : \""
				    + regimenGanancias
				    +"\",\"idbanco\" : \""
				    + idbanco
			        + "\" "
			        + "}";
		
		return resultado;
	}
	
		
}