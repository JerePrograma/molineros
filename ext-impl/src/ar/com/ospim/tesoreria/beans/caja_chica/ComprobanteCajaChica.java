package ar.com.ospim.tesoreria.beans.caja_chica;


import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import javax.portlet.PortletRequest;

import org.compass.core.util.backport.java.util.Collections;

import ar.com.ospim.afiliados.beans.Afiliado;
import ar.com.ospim.afip.service.AfipServiceUtil;
import ar.com.ospim.afip.service.FeriadosServiceUtil;
import ar.com.ospim.autorizaciones.beans.SeguimientoSur;
import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.beans.Cheque;
import ar.com.ospim.global.beans.Comprobante;
import ar.com.ospim.global.beans.Concepto;
import ar.com.ospim.global.beans.DepositoBancario;
import ar.com.ospim.global.beans.Efectivo;
import ar.com.ospim.global.beans.Empresa;
import ar.com.ospim.global.beans.Pagare;
import ar.com.ospim.global.beans.Seccional;
import ar.com.ospim.global.services.EmpresaServiceUtil;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.tesoreria.WebKeysCajaChica;
import ar.com.ospim.tesoreria.service.CajaChicaServiceImpl;
import ar.com.ospim.tesoreria.service.CajaChicaServiceUtil;
import ar.com.ospim.util.DateUtils;
import ar.com.uoma.beans.CentroCosto;

import com.liferay.portal.PortalException;
import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.dao.orm.Criterion;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQueryFactoryUtil;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.PortletClassLoaderUtil;
import com.liferay.portal.model.User;
import com.liferay.portlet.documentlibrary.model.DLFileEntry;
import com.liferay.portlet.documentlibrary.model.DLFolder;
import com.liferay.portlet.documentlibrary.service.DLFileEntryLocalServiceUtil;
import com.liferay.portlet.documentlibrary.service.DLFolderLocalServiceUtil;

import edu.emory.mathcs.backport.java.util.Arrays;

public class ComprobanteCajaChica   extends Comprobante implements Serializable{
	private static final long serialVersionUID = 723699513781186142L;
	private static Log _log = LogFactoryUtil.getLog(ComprobanteCajaChica.class);
	
	private Integer id;
	private Date fechaRendicion;
	private Boolean solicitaReposicion;
	private Boolean rechazado;
	private Boolean reposicionAprobadaSinOP;
	private CentroCosto centroCosto;
	private Integer totalRegistros;
	private Integer ordenPago;
	
	public ComprobanteCajaChica() {
		super();
	}
	public ComprobanteCajaChica(Comprobante comp) {
		super(comp);
	}
	
	public Date getFechaRendicion() {
		return fechaRendicion;
	}
	
	public void setFechaRendicion(Date fechaRendicion) {
		this.fechaRendicion = fechaRendicion;
	}
	
	public Boolean getRechazado() {
		return rechazado;
	}
	
	public void setRechazado(Boolean rechazado) {
		this.rechazado = rechazado;
	}
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	public static ComprobanteCajaChica getMapping(ResultSet rs,Integer entidad)
			throws SQLException	 {
		ComprobanteCajaChica comp = new ComprobanteCajaChica();
		comp.setId(rs.getInt("id"));
		comp.setConceptos(new ArrayList<ComprobanteConcepto>());
		
		comp.setFechaEmision(rs.getDate("fecha_emision"));
		comp.setImporteComprobante(rs.getBigDecimal("importe_comprobante"));
		
		comp.setNroComprobante(rs.getString("nro"));
		comp.setTipoComprobante(rs.getString("tipo"));
		comp.setPtoVenta(rs.getInt("id_punto_venta"));
		
		comp.setRechazado(rs.getBoolean("rechazado"));
		
		comp.setLetraComprobante(rs.getString("compro_letra"));
		
		comp.setReposicionAprobadaSinOP(rs.getBoolean("aprobada_sin_op")); 
		
		String cuitAcreedor = rs.getString("cuit_acreedor");
		String sucuAcreedor = rs.getString("sucu_acreedor");
		String razonSocial="";
		Empresa acreedor = new Empresa(cuitAcreedor, sucuAcreedor, "");
		
		
		
        try {
        	razonSocial = rs.getString("razon_social");
			acreedor.setRazon_soc(razonSocial);
		} catch (Exception e1) {
			try {
				acreedor = EmpresaServiceUtil.getEmpleadorCompleto(cuitAcreedor, sucuAcreedor);
			}catch(Exception e2) {}
		} 
        
		if(acreedor==null || acreedor.getRazon_soc()==null || acreedor.getRazon_soc().isEmpty()){
			
			if (cuitAcreedor.equals(WebKeysGlobal.CUIT_AMTIMA)
					|| cuitAcreedor.equals(WebKeysGlobal.CUIT_OSPIM)
					|| cuitAcreedor.equals(WebKeysGlobal.CUIT_UOMA)){
				
			   List<Seccional>seccionales = TraeListasServiceUtil.getSeccionales(Integer.parseInt(sucuAcreedor),null, cuitAcreedor);
			   if(seccionales.size()>0){
				   for(Seccional s:seccionales){
				     if(s.getId()==Integer.parseInt(sucuAcreedor)){  
				        acreedor= new Empresa(cuitAcreedor,sucuAcreedor,s.getDescripcion());
				        break;
				     }   
				   }  
			   }
			
			}else{
			  acreedor = new Empresa(cuitAcreedor, sucuAcreedor, "");
			}
			
		}
		
		
		comp.setAcreedorEmpresa(acreedor);
		try {
			comp.setObservaciones(rs.getString("observaciones"));
		} catch (Exception e) {}

		Seccional seccional = new Seccional();
		try {
			seccional.setId_seccional(rs.getInt("seccional_id"));
		} catch (Exception e) {}
		try {
			seccional.setDescripcion(rs.getString("seccional_descripcion"));
		}catch (Exception e) {}
		
		comp.setSeccional(seccional);
		ComprobanteConcepto concepto = new ComprobanteConcepto(new Concepto(rs.getInt("concepto_id"),rs.getString("concepto_descripcion")));
		comp.getConceptos().add(concepto);
		
		try{
			CentroCosto centro= new CentroCosto();
			centro.setId(rs.getInt("centrocosto_id"));
	  		centro.setDescripcion(rs.getString("centrocosto_descripcion"));
			comp.setCentroCosto(centro);
		}catch(Exception e){}
		
		
		if(entidad==WebKeysGlobal.UOMA) {
			try{
			  comp.setGravadoIVA(rs.getBigDecimal("gravado_iva"));
			  comp.setTasaIva(rs.getDouble("tasa_iva"));
			  comp.setIva(rs.getBigDecimal("iva"));
			  comp.setPercepcionIVA(rs.getBigDecimal("perc_iva"));
			  comp.setPercepcionIIBB(rs.getBigDecimal("perc_iibb"));
			  comp.setJurisdiccionIIBB(rs.getInt("jurisdiccion_iibb"));
			  comp.setOtrosTributos(rs.getBigDecimal("otros_tributos"));
			}catch(Exception e){}
		}
		
		return comp;
	}
	

	public static ComprobanteCajaChica getMappingResumido(ResultSet rs)
			throws SQLException {
		ComprobanteCajaChica comp = new ComprobanteCajaChica();
		comp.setId(rs.getInt("id"));
		comp.setConceptos(new ArrayList<ComprobanteConcepto>());
		
		comp.setFechaEmision(rs.getDate("fecha_emision"));
		comp.setImporteComprobante(rs.getBigDecimal("importe_comprobante"));
		
		comp.setNroComprobante(rs.getString("nro"));
		comp.setTipoComprobante(rs.getString("tipo"));
		comp.setPtoVenta(rs.getInt("id_punto_venta"));
		
		comp.setRechazado(rs.getBoolean("rechazado"));
		
		comp.setLetraComprobante(rs.getString("compro_letra"));
		
		comp.setReposicionAprobadaSinOP(rs.getBoolean("aprobada_sin_op")); 
		
		String cuitAcreedor = rs.getString("cuit_acreedor");
		String sucuAcreedor = rs.getString("sucu_acreedor");
		Empresa acreedor = new Empresa(cuitAcreedor, sucuAcreedor, "");
        
		if(acreedor==null){
			
			if (cuitAcreedor.equals(WebKeysGlobal.CUIT_AMTIMA)
					|| cuitAcreedor.equals(WebKeysGlobal.CUIT_OSPIM)
					|| cuitAcreedor.equals(WebKeysGlobal.CUIT_UOMA)){
				
			   List<Seccional>seccionales = TraeListasServiceUtil.getSeccionales(Integer.parseInt(sucuAcreedor),null, cuitAcreedor);
			   if(seccionales.size()>0){
				   for(Seccional s:seccionales){
				     if(s.getId()==Integer.parseInt(sucuAcreedor)){  
				        acreedor= new Empresa(cuitAcreedor,sucuAcreedor,s.getDescripcion());
				        break;
				     }   
				   }  
			   }
			
			}else{
			  acreedor = new Empresa(cuitAcreedor, sucuAcreedor, "");
			}
			
		}	
		comp.setAcreedorEmpresa(acreedor);
		try {
			comp.setObservaciones(rs.getString("observaciones"));
		} catch (Exception e) {}

		Seccional seccional = new Seccional();
		try {
			seccional.setId_seccional(rs.getInt("seccional_id"));
		} catch (Exception e) {}
		try {
			seccional.setDescripcion(rs.getString("seccional_descripcion"));
		}catch (Exception e) {}
		
		comp.setSeccional(seccional);
		ComprobanteConcepto concepto = new ComprobanteConcepto(new Concepto(rs.getInt("concepto_id"),rs.getString("concepto_descripcion")));
		comp.getConceptos().add(concepto);
		
		try{
			CentroCosto centro= new CentroCosto();
			centro.setId(rs.getInt("centrocosto_id"));
	  		centro.setDescripcion(rs.getString("centrocosto_descripcion"));
			comp.setCentroCosto(centro);
		}catch(Exception e){}
		   
		
		return comp;
	}

	
	
	public static ComprobanteCajaChica getMappingInforme(ResultSet rs)
			throws SQLException {
		ComprobanteCajaChica comp = new ComprobanteCajaChica();
		comp.setId(rs.getInt("id"));
		comp.setConceptos(new ArrayList<ComprobanteConcepto>());
		
		comp.setFechaEmision(rs.getDate("fecha_emision"));
		comp.setImporteComprobante(rs.getBigDecimal("importe_comprobante"));
		
		comp.setNroComprobante(rs.getString("nro"));
		comp.setTipoComprobante(rs.getString("tipo"));
		comp.setPtoVenta(rs.getInt("id_punto_venta"));
		
		
		comp.setLetraComprobante(rs.getString("compro_letra"));
		
		comp.setAlta_fecha(rs.getTimestamp("alta_fecha"));
		comp.setBaja_fecha(rs.getTimestamp("baja_fecha"));
		comp.setModi_fecha(rs.getTimestamp("modi_fecha"));
		
		String cuitAcreedor = rs.getString("cuit_acreedor");
		String sucuAcreedor = rs.getString("sucu_acreedor");
		Empresa acreedor = new Empresa(cuitAcreedor, sucuAcreedor, "");
        try {
			acreedor = EmpresaServiceUtil.getEmpleadorCompleto(cuitAcreedor, sucuAcreedor);
		} catch (Exception e1) {} 
        
		if(acreedor==null){
			
			if (cuitAcreedor.equals(WebKeysGlobal.CUIT_AMTIMA)
					|| cuitAcreedor.equals(WebKeysGlobal.CUIT_OSPIM)
					|| cuitAcreedor.equals(WebKeysGlobal.CUIT_UOMA)){
				
			   List<Seccional>seccionales = TraeListasServiceUtil.getSeccionales(Integer.parseInt(sucuAcreedor),null, cuitAcreedor);
			   if(seccionales.size()>0){
				   for(Seccional s:seccionales){
				     if(s.getId()==Integer.parseInt(sucuAcreedor)){  
				        acreedor= new Empresa(cuitAcreedor,sucuAcreedor,s.getDescripcion());
				        break;
				     }   
				   }  
			   }
			
			}else{
			  acreedor = new Empresa(cuitAcreedor, sucuAcreedor, "");
			}
			
		}	
		comp.setAcreedorEmpresa(acreedor);
		try {
			comp.setObservaciones(rs.getString("observaciones"));
		} catch (Exception e) {}

		ComprobanteConcepto concepto = new ComprobanteConcepto(new Concepto(rs.getInt("concepto_id"),rs.getString("concepto_descripcion")));
		comp.getConceptos().add(concepto);
		
		return comp;
	}

	
	public Boolean getSolicitaReposicion() {
		return solicitaReposicion;
	}
	public void setSolicitaReposicion(Boolean solicitaReposicion) {
		this.solicitaReposicion = solicitaReposicion;
	}
	public Boolean getReposicionAprobadaSinOP() {
		return reposicionAprobadaSinOP;
	}
	public void setReposicionAprobadaSinOP(Boolean reposicionAprobadaSinOP) {
		this.reposicionAprobadaSinOP = reposicionAprobadaSinOP;
	}
	public CentroCosto getCentroCosto() {
		return centroCosto;
	}
	public void setCentroCosto(CentroCosto centroCosto) {
		this.centroCosto = centroCosto;
	}
	public Integer getTotalRegistros() {
		return totalRegistros;
	}
	public void setTotalRegistros(Integer totalRegistros) {
		this.totalRegistros = totalRegistros;
	}
	public Integer getOrdenPago() {
		return ordenPago;
	}
	public void setOrdenPago(Integer ordenPago) {
		this.ordenPago = ordenPago;
	}
    
	
	public String getImagenNombre() {
		String ret ="";
		try {
			ret=getAcreedorEmpresa().getCuit() + "-" + getTipoComprobante() + "-" + getLetraComprobante() + "-" +  String.format("%05d",getPtoVenta())+"-" +
					getNroComprobante();
		}catch(Exception e) {}
		
		return ret; 
	}
	
	public String getImagenNombreFileEntry() throws PortalException, SystemException {
		String ret ="";
		
		DLFolder f = DLFolderLocalServiceUtil.getFolder(10136, 0L, "CajaChica");
		long folderIdNew=f.getFolderId();
		String keywords = getImagenNombre();
		try{
      		if(!"".equals(keywords))  { 
      		   DynamicQuery dlf =DynamicQueryFactoryUtil.forClass(
      				DLFileEntry.class, PortletClassLoaderUtil.getClassLoader());
      		   Criterion criterion1 = null;
      		   criterion1 = RestrictionsFactoryUtil.eq("folderId",folderIdNew);
      		   criterion1=RestrictionsFactoryUtil.and(criterion1,
      		   RestrictionsFactoryUtil.ilike("title", keywords+"%" ));
      		   dlf.add(criterion1);
      		   List<Object> results=DLFolderLocalServiceUtil.dynamicQuery(dlf);
      		   for (Object f1 :results){
      			  DLFileEntry fileEntry = (DLFileEntry) f1;
      			  if(keywords.equalsIgnoreCase(fileEntry.getTitle())) {
      				ret =fileEntry.getName();
      				break;
      			  }
      		   }
      		}   
      	}catch (Exception e) {
      		ret=e.getMessage();
      	}
		return ret; 
	}
	
}
