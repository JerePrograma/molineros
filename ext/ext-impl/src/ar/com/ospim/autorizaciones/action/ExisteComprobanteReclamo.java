package ar.com.ospim.autorizaciones.action;

import java.util.Calendar;
import java.util.Comparator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import com.liferay.ibm.icu.text.SimpleDateFormat;
import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.JSONAction;

import ar.com.ospim.autorizaciones.beans.Nomenclador;
import ar.com.ospim.autorizaciones.beans.PrestacionesReclamo;
import ar.com.ospim.autorizaciones.services.NomencladorServiceUtil;
import ar.com.ospim.autorizaciones.services.ReclamosPrestacionesServiceUtil;
import ar.com.ospim.autorizaciones.services.WebKeysAutorizaciones;
import ar.com.ospim.util.DateUtils;
import ar.com.ospim.util.StringUtils;

public class ExisteComprobanteReclamo extends JSONAction implements Comparator<PrestacionesReclamo> {
	private static Log _log = LogFactoryUtil.getLog(ExisteComprobanteReclamo.class);


	public String getJSON(ActionMapping arg0, ActionForm arg1,
			HttpServletRequest req, HttpServletResponse arg3) throws Exception {
		
		

    String resultado = "{}";
	Boolean existe=false;
	String outMensaje = "";
	String mensaje = "";
	Boolean error=false;

	
	PrestacionesReclamo prestacionreclamoFromRequest = getPrestacionesReclamosFromRequest(req);
	
	
	@SuppressWarnings("unchecked")
	List<PrestacionesReclamo> prestaciones=  (List<PrestacionesReclamo>) req.getSession()
			.getAttribute(WebKeysAutorizaciones.LISTADO_PRESTACIONES_RECLAMOS_EN_SESION );
	
	
	
	mensaje = ReclamosPrestacionesServiceUtil.validarExisteComprobante(prestacionreclamoFromRequest);
	if (!StringUtils.checkEmpty(mensaje)){
		outMensaje = mensaje;	
		error = true;
	}else	
		if (prestaciones != null && !prestaciones.isEmpty()){
			for( PrestacionesReclamo r : prestaciones) {
				if(r.getBajaFecha() == null && !error){					
					mensaje = ReclamosPrestacionesServiceUtil.validarExisteComprobante(r);
				}
				if (!StringUtils.checkEmpty(mensaje)){
					outMensaje = mensaje;
					break;
			 }	
		}
	}	
			

	if (prestaciones != null &&  !prestaciones.isEmpty()){
		for (PrestacionesReclamo prestacionesReclamo : prestaciones) {
			if (prestacionesReclamo.getBajaFecha() == null &&  !error
					&& compare(prestacionesReclamo, prestacionreclamoFromRequest)==1){
				existe = true;
			}
			_log.debug("log  " + prestacionesReclamo.getIdregistroString());
		}
	}

	
		 
	return  resultado = "{ \"existe\" : \"" 
				    + existe 
				    + "\",\"mensajeError\" : \""
			        + outMensaje
			        + "\" }";
		  
	}
	
	
	public PrestacionesReclamo getPrestacionesReclamosFromRequest(HttpServletRequest req) {

		String frecuencia = ParamUtil.getString(req, "frecuencia");
		double importe = ParamUtil.getDouble(req, "importe");
		int troquel = ParamUtil.getInteger(req, "troquel",0) != 0 ? ParamUtil.getInteger(req, "troquel",0) :  ParamUtil.getInteger(req, "id_medicamento_edit",0) ;
		String prestacion = ParamUtil.getString(req, "prestacion");
		if (prestacion != null && prestacion.equalsIgnoreCase("Graba Edicion")){
			prestacion = ParamUtil.getString(req, "codigoSeguimiento_filtro_edit");
		}

		int tiponomenclador = ParamUtil.getInteger(req, "tiponomenclador") != 0  ? ParamUtil.getInteger(req, "tiponomenclador") : ParamUtil.getInteger(req, "nom_seleccionado_edit",0); ; 
		int cantidad = ParamUtil.getInteger(req, "cantidad");
		String nombreMedicamento = ParamUtil.getString(req, "nombre_medicamento");
		String nombrePrestacion = ParamUtil.getString(req, "nombre_prestacion");
		int tipoNomnecladorPrestacion= ParamUtil.getInteger(req, "tiponomnecladorprestacion");
				
		String cpbteTipo = ParamUtil.getString(req, "cpbte_tipo");
		String cpbteNro = ParamUtil.getString(req, "cpbte_nro");
		int cpbteDia = ParamUtil.getInteger(req, "cpbte_dia");
		int cpbteMes = ParamUtil.getInteger(req, "cpbte_mes");
		int cpbteAnio = ParamUtil.getInteger(req, "cpbte_anio");
		Double cpbteCantidad = ParamUtil.getDouble(req, "cpbte_cantidad");
		Double cpbteImporte = ParamUtil.getDouble(req, "cpbte_importe");
		Double cpbteTotal = ParamUtil.getDouble(req, "importeFC");
		String cpbteCUIT = ParamUtil.getString(req, "cpbte_cuit");
		String cpbteSucursal = ParamUtil.getString(req, "cpbte_sucursal");
		String cpbteCuitSucursal = ParamUtil.getString(req, "cpbte_cuit_sucursal");

		String comprobanteLetra = ParamUtil.getString(req, "cpbte_letra");

		int fechaPrestacionDia = ParamUtil.getInteger(req, "fecha_prestacion_dia");
		int fechaPrestacionMes = ParamUtil.getInteger(req, "fecha_prestacion_mes");
		int fechaPrestacionAnio = ParamUtil.getInteger(req, "fecha_prestacion_anio");
		
		int idRegistro = ParamUtil.getInteger(req, "idRegistro");

		String cuil = ParamUtil.getString(req, "cuil");
		int inte = ParamUtil.getInteger(req, "inte");
		
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
		int Idprestacion =0;
			try {
			if (tiponomenclador==1){
				// buscar id de la prestacion
				List<Nomenclador> nomencladores;
		
					nomencladores = NomencladorServiceUtil.getListaNomenclador(tipoNomnecladorPrestacion,"",0, prestacion,false,"");
			
				   for(Nomenclador nom:nomencladores){			   				   
					   if(prestacion.equals(nom.getCodigo())  ){
					       Idprestacion= nom.getId_prestacion();
					   }   
				   }
	            if ( Idprestacion==0){
	            	_log.debug("Error en la busqueda de id prestacion : ");
	            }            	
	
			}
		} catch (SystemException e) {
			_log.debug("Error en la busqueda de id prestacion : " + e.getMessage());
		}
		
		PrestacionesReclamo prestacionreclamo = new PrestacionesReclamo(null,frecuencia,0,importe,0,Idprestacion ,
				troquel ,tiponomenclador,nombreMedicamento,nombrePrestacion,false,cantidad,
				cpbteTipo, cpbteNro,cpbteFecha.getTime(), cpbteCantidad, cpbteImporte,cpbteTotal,cpbteCUIT,cpbteSucursal,
				cpbteCuitSucursal,comprobanteLetra, fechaPrestacion.getTime(), 0);
		prestacionreclamo.setEstado(PrestacionesReclamo.ESTADOS.NUEVO );
		prestacionreclamo.setIdRegistro(idRegistro);
		prestacionreclamo.setCuilTitular(cuil);
		prestacionreclamo.setInte(inte);
		return prestacionreclamo;
	}


	@Override
	public int compare(PrestacionesReclamo o1, PrestacionesReclamo o2) {
		int comprobanteSucO1 = Integer.parseInt(o1.getComprobanteSucursal());
		int comprobanteSucO2 = Integer.parseInt(o2.getComprobanteSucursal());
		int numeroComprobanteNroO1 =  Integer.parseInt(o1.getComprobanteNro()) ;
		int numeroComprobanteNroO2 =  Integer.parseInt(o2.getComprobanteNro()) ;
		
		//int comprobateFecha =  DateUtils.diferenciaDias(DateUtils.toCalendar(o1.getComprobanteFecha()), DateUtils.toCalendar(o2.getComprobanteFecha()));
		//int prestacionFecha =  DateUtils.diferenciaDias(DateUtils.toCalendar(o1.getFechaPrestacion()), DateUtils.toCalendar(o2.getFechaPrestacion()));
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		
		if (o1.getIdRegistro() != o2.getIdRegistro()
				&& o1.getComprobanteLetra().equals(o2.getComprobanteLetra())
				&& o1.getComprobanteTipo().equals(o2.getComprobanteTipo())
			//	&& comprobateFecha == 0
			//	&& prestacionFecha == 0
				&& sdf.format(o1.getFechaPrestacion()).equals(sdf.format(o2.getFechaPrestacion()))
				&& o1.getComprobanteCUIT().equals(o2.getComprobanteCUIT())
				&& comprobanteSucO1 == comprobanteSucO2
				&& numeroComprobanteNroO1 ==  numeroComprobanteNroO2
		    ){
				if(o1.getId_medicamento() != 0){
					if(o1.getId_medicamento() == o2.getId_medicamento()){
						return 1;
					}
				}else{
					if (o1.getId_prestacion() == o2.getId_prestacion()){
						return 1;
					}
				}
		}
		return 0;
	}
	
	
	
	
}