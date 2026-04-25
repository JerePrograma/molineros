package ar.com.ospim.liquidaciones.action;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.prestadores.beans.ConvenioPrestacionalDetalle;
import ar.com.ospim.prestadores.services.ConvenioPrestacionalServiceUtil;

import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.JSONAction;

public class BuscaContratoPrestadorAction extends JSONAction {

	@Override
	public String getJSON(ActionMapping arg0, ActionForm arg1,
			HttpServletRequest req, HttpServletResponse res) throws Exception {
		
		int id_prestador=ParamUtil.getInteger(req, "idprestador");
		int fechaDesdeDia=ParamUtil.getInteger(req, "desdedia");
		int fechaDesdeMes=ParamUtil.getInteger(req, "desdemes");
		int fechaDesdeAnio=ParamUtil.getInteger(req, "desdeanio");
		
		int fechaHastaDia=ParamUtil.getInteger(req, "hastadia");
		int fechaHastaMes=ParamUtil.getInteger(req, "hastames");
		int fechaHastaAnio=ParamUtil.getInteger(req, "hastaanio");
		
		SimpleDateFormat formatoDeFecha = new SimpleDateFormat("dd/MM/yyyy");
		Date fechaDesde = null;
		Date fechaHasta = null;
		try {
			fechaDesde = formatoDeFecha.parse(fechaDesdeDia + "/"
					+ (fechaDesdeMes + 1) + "/"
					+ fechaDesdeAnio);
		} catch (Exception e) {
			fechaDesde = null;
		}
		try {
			fechaHasta = formatoDeFecha.parse(fechaHastaDia + "/"
					+ (fechaHastaMes + 1) + "/"
					+ fechaHastaAnio);
		} catch (Exception e) {
			fechaHasta = null;
		}
		
		
		String codigo = ParamUtil.getString(req, "codigo");
		
		
		
		Double resultado = 0D;
		Boolean sugiere =false;
		String msg="";
		Double importe=0D;
		
		List <ConvenioPrestacionalDetalle> l = ConvenioPrestacionalServiceUtil.detalleValorizarTratamientoV01(id_prestador, fechaDesde, fechaHasta, codigo,0);
		
		if(l.size()==0 && !"".equals(codigo)){
			msg="No se encuentra Convenio para la prestaci�n solicitada";
		}else{
			resultado=l.get(0).getImporte().doubleValue();
		}
		
		return "{ \"resultado\" : \"" + resultado + 
				"\",\"mensaje\" : \"" + msg + "\"}";
				
	}

}