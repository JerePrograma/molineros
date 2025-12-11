package ar.com.ospim.autorizaciones.action;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.JSONAction;

import ar.com.ospim.autorizaciones.beans.Nomenclador;
import ar.com.ospim.autorizaciones.beans.PrestacionesReclamo;
import ar.com.ospim.autorizaciones.services.NomencladorServiceUtil;
import ar.com.ospim.farmacia.beans.Medicamento;
import ar.com.ospim.farmacia.services.BusquedaMedicamentoServiceUtil;
import ar.com.ospim.liquidaciones.beans.Prestador;
import ar.com.ospim.liquidaciones.services.PrestadorServiceUtil;
import ar.com.ospim.util.DateUtils;

public class ValidarReclamo extends JSONAction  {
	private static Log _log = LogFactoryUtil.getLog(ValidarReclamo.class);


	public String getJSON(ActionMapping arg0, ActionForm arg1,
			HttpServletRequest req, HttpServletResponse arg3) throws Exception {
		
	
	    String resultado = "{}";
		String codError = "0";
	
		_log.debug("ValidarReclamo ");
		
		String tipoPedido = ParamUtil.getString(req, "tipopedido");
		int tipoNomnecladorPrestacion= ParamUtil.getInteger(req, "tiponomnecladorprestacion");


		
		PrestacionesReclamo prestacionFromRequest = getPrestacionesReclamosFromRequest(req);
		
		boolean fechaPrestacion =  DateUtils.esMayor(prestacionFromRequest.getFechaPrestacion(),new Date());
		boolean fechaComprobante =  DateUtils.esMayor(prestacionFromRequest.getComprobanteFecha(), new Date());
		boolean fechaBaja =false;
		
		if(prestacionFromRequest.getBajaFecha()!=null) {
		  fechaBaja=DateUtils.esMayor( DateUtils.getMismoDia_00_00hs(prestacionFromRequest.getFechaPrestacion()), DateUtils.getMismoDia_00_00hs(prestacionFromRequest.getBajaFecha())); 
		}
		
		if (fechaPrestacion == true){
			codError = "1";
		}
	
		if (fechaComprobante == true){
			codError = "2";
		}
		
		
		if (!"REINTEGRO".equalsIgnoreCase(tipoPedido)){
			List<Prestador> lp = PrestadorServiceUtil.getPrestadores(0, prestacionFromRequest.getComprobanteCUIT() ,null, false);
			if(lp==null || lp.size()==0){
				codError = "3";//No existe prestador
			}	
		}
		
		if (prestacionFromRequest.getTipoPrestacion()==1){
			List<Nomenclador> nomencladores = NomencladorServiceUtil.getListaNomenclador(tipoNomnecladorPrestacion,"",0, prestacionFromRequest.getCodigoPrestacion(),false,"");
			if(nomencladores==null || nomencladores.size()==0){
				codError = "4";//No existe Prestación
			}	
		}else{
			List<Medicamento> medicamentos  = BusquedaMedicamentoServiceUtil.getBusquedaMedicamentos(Integer.parseInt(prestacionFromRequest.getCodigoPrestacion()), null);
			if(medicamentos==null || medicamentos.size()==0){
				codError = "5";//No existe medicamento
			}	
		}
		
		if (fechaBaja == true){
			codError = "6";
		}
		
		return  resultado = "{ \"codError\" : \"" 
								    + codError 
							        + "\" }";
		  
	}
	
	
	public PrestacionesReclamo getPrestacionesReclamosFromRequest(HttpServletRequest req) {


		int cpbteDia = ParamUtil.getInteger(req, "cpbte_dia");
		int cpbteMes = ParamUtil.getInteger(req, "cpbte_mes");
		int cpbteAnio = ParamUtil.getInteger(req, "cpbte_anio");
	
		int fechaPrestacionDia = ParamUtil.getInteger(req, "fecha_prestacion_dia");
		int fechaPrestacionMes = ParamUtil.getInteger(req, "fecha_prestacion_mes");
		int fechaPrestacionAnio = ParamUtil.getInteger(req, "fecha_prestacion_anio");
	
		String cpbteCuit = ParamUtil.getString(req, "cpbteCuit");
		
		Calendar cpbteFecha = Calendar.getInstance();
		try {
		  cpbteFecha.set(cpbteAnio, cpbteMes, cpbteDia);
		}catch( Exception e) {		
			cpbteFecha=null;
		}
		
		
		Calendar fechaPrestacion = Calendar.getInstance();
		try {
			fechaPrestacion.set(fechaPrestacionAnio, fechaPrestacionMes, fechaPrestacionDia);
		}catch( Exception e) {
			fechaPrestacion=null;	
		}
	
		int  tipoNomenclador= ParamUtil.getInteger(req, "tiponomenclador");
		String prestacion= ParamUtil.getString(req, "prestacion");
		String troquel = ParamUtil.getString(req, "troquel","0");
		
		
		PrestacionesReclamo prestacionreclamo = new PrestacionesReclamo();
		
		
		prestacionreclamo.setFechaPrestacion(fechaPrestacion.getTime());
		prestacionreclamo.setComprobanteFecha(cpbteFecha.getTime());
		prestacionreclamo.setComprobanteCUIT(cpbteCuit);  
		prestacionreclamo.setTipoPrestacion(tipoNomenclador);
		if (tipoNomenclador==1){
			prestacionreclamo.setCodigoPrestacion(prestacion);
		}else{
			prestacionreclamo.setCodigoPrestacion(troquel);
		}
		
		
        String baja = ParamUtil.getString(req, "baja");
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		Date bajaFecha = new Date();
		try {
		  bajaFecha= sdf.parse(baja);
		}catch(Exception e) {
			bajaFecha=null;
		}
		prestacionreclamo.setBajaFecha(bajaFecha);
		
		return prestacionreclamo;
	}

	
	
	
}