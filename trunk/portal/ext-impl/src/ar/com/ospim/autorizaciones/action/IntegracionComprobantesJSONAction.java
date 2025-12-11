package ar.com.ospim.autorizaciones.action;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.JSONAction;

import ar.com.ospim.autorizaciones.beans.IntegracionDetalleDS;
import ar.com.ospim.autorizaciones.beans.Nomenclador;
import ar.com.ospim.autorizaciones.services.IntegracionServiceUtil;
import ar.com.ospim.autorizaciones.services.NomencladorServiceUtil;

public class IntegracionComprobantesJSONAction extends JSONAction {

	@Override
	public String getJSON(ActionMapping arg0, ActionForm arg1,
			HttpServletRequest req, HttpServletResponse arg3) throws Exception {
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		DecimalFormat nf = new DecimalFormat("#0.00");
		
		String cuit = ParamUtil.getString(req, "cuit");
		String cuil = ParamUtil.getString(req, "cuil");
		Integer tipo = ParamUtil.getInteger(req, "tipo");
		Integer sucursal = ParamUtil.getInteger(req, "sucursal");
		Integer numero = ParamUtil.getInteger(req, "numero");
		String prestacion = ParamUtil.getString(req, "prestacion");
		Integer lote = ParamUtil.getInteger(req, "lote");
		
		String estado = "";
		
		IntegracionDetalleDS cpbte = new IntegracionDetalleDS();
		try {
			
			List<IntegracionDetalleDS>detalles= IntegracionServiceUtil.detalleDSByIdLoteSSS(lote);
			
			
			if(detalles!=null && detalles.size()>0){
				
				for(IntegracionDetalleDS d:detalles) {
					if(cuit.equals(d.getCuitPrestador()) &&
					   cuil.equals(d.getCuil()) &&
					   tipo.equals(d.getComprobanteTipo()) &&
					   sucursal.equals(d.getComprobantePtoVta()) &&
					   numero.equals(d.getComprobanteNro()) &&
					   prestacion.equals(d.getPrestacionCodigo()) &&
					   "DS".equals(d.getTipoArchivo())	   
					  ) { 
						cpbte=d;
						if(cpbte.getError()!=null) estado=cpbte.getError();
						break;
					}
				}
				
			}
		} catch (Exception e) {
			return "{\"status\":\"falla_inesperada\"}";
		}
		
		String id="";
		String fecha="";
		String importe="";
		String solicitado="";
		
		if(cpbte.getId()!=null) id=cpbte.getId().toString();
		if(cpbte.getComprobanteFechaEmision()!=null) fecha=sdf.format(cpbte.getComprobanteFechaEmision());
		if(cpbte.getComprobanteImporte()!=null) importe=nf.format(cpbte.getComprobanteImporte()/100);
		if(cpbte.getImporteSolicitado()!=null) solicitado=nf.format(cpbte.getImporteSolicitado()/100);
		
		return "{ \"id\" : \""+ cpbte.getId()+
				 "\",\"fecha\" : \"" + fecha  +
				 "\",\"importe\" : \"" + importe +
				 "\",\"solicitado\" : \"" +  solicitado +
				 "\",\"status\" : \"" + estado +
				 "\"}";
		
	}
	
}
