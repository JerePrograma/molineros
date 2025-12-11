package ar.com.ospim.autorizaciones.action;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Formatter;
import java.util.List;
import java.util.Random;

import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

import ar.com.ospim.autorizaciones.beans.Nomenclador;
import ar.com.ospim.autorizaciones.beans.PrestacionesReclamo;
import ar.com.ospim.autorizaciones.exceptions.PrestacionesReclamosException;
import ar.com.ospim.autorizaciones.services.NomencladorServiceUtil;
import ar.com.ospim.autorizaciones.services.WebKeysAutorizaciones;


public class ListaPrestacionesreclamosAction extends PortletAction {
	private static Log _log = LogFactoryUtil.getLog(ListaPrestacionesreclamosAction.class);

	public ActionForward render(ActionMapping mapping, ActionForm form, PortletConfig portletConfig,
			RenderRequest renderRequest, RenderResponse renderResponse) throws Exception {
		
		HttpSession session = (HttpSession) PortalUtil.getHttpServletRequest(renderRequest).getSession();								
		
		String cmdAction = ParamUtil.getString(renderRequest, Constants.ACTION);
		
		Formatter fmt = new Formatter();
		
		String frecuencia = ParamUtil.getString(renderRequest, "frecuencia");
		double importe  = ParamUtil.getDouble(renderRequest, "importe");
		double cargoOspim = ParamUtil.getDouble(renderRequest, "cargoospim");
		double cargoPs = ParamUtil.getDouble(renderRequest, "cargops");
		double cargoImesa = ParamUtil.getDouble(renderRequest, "cargoimesa");
		double reconocidoSSS = ParamUtil.getDouble(renderRequest, "reconocidoSSS");
		String troquel = ParamUtil.getString(renderRequest, "troquel");
		String prestacion= ParamUtil.getString(renderRequest, "prestacion");
		int  tipoNomenclador= ParamUtil.getInteger(renderRequest, "tiponomenclador");
		int tipoNomnecladorPrestacion= ParamUtil.getInteger(renderRequest, "tiponomnecladorprestacion");
		double cantidad= ParamUtil.getDouble(renderRequest, "cantidad");
		String observaciones= ParamUtil.getString(renderRequest, "observaciones");
		String nombreMedicamento = ParamUtil.getString(renderRequest, "nombre_medicamento");
		String nombrePrestacion= ParamUtil.getString(renderRequest, "nombre_prestacion");	
		boolean recuperableSur = false;//ParamUtil.getBoolean(renderRequest, "recuperableSur");
		
		Integer recuperable = ParamUtil.getInteger(renderRequest, "recuperableSur");
		
		String cpbteTipo= ParamUtil.getString(renderRequest, "cpbte_tipo");
		int nro = ParamUtil.getInteger(renderRequest, "cpbte_nro",0);
		String cpbteNro = fmt.format("%08d",nro).toString() ;

				
		int cpbteDia=ParamUtil.getInteger(renderRequest,"cpbte_dia");
		int cpbteMes=ParamUtil.getInteger(renderRequest,"cpbte_mes");
		int cpbteAnio=ParamUtil.getInteger(renderRequest,"cpbte_anio");
		Double cpbteCantidad  = ParamUtil.getDouble(renderRequest, "cpbte_cantidad");
		Double cpbteImporte= ParamUtil.getDouble(renderRequest, "cpbte_importe");
		Double cpbteTotal  = ParamUtil.getDouble(renderRequest, "importeFC");
		String cpbteCUIT= ParamUtil.getString(renderRequest, "cpbte_cuit");
		int cpbteSucursalAux= ParamUtil.getInteger(renderRequest, "cpbte_sucursal");
		String cpbteSucursal =  null;
		String cpbteCuitSucursal =  null;
		String comprobanteLetra =  null;
		cpbteCuitSucursal= ParamUtil.getString(renderRequest, "cpbte_cuit_sucursal");
		if (!"OTR".equals(cpbteTipo)){			
			comprobanteLetra= ParamUtil.getString(renderRequest, "cpbte_letra");
			fmt = new Formatter();
			cpbteSucursal = fmt.format("%05d",cpbteSucursalAux).toString() ;
		}
		
		
		int fechaPrestacionDia=ParamUtil.getInteger(renderRequest,"fecha_prestacion_dia");
		int fechaPrestacionMes=ParamUtil.getInteger(renderRequest,"fecha_prestacion_mes");
		int fechaPrestacionAnio=ParamUtil.getInteger(renderRequest,"fecha_prestacion_anio");
		
		Calendar cpbteFecha = Calendar.getInstance();
		try {
		  cpbteFecha.set(cpbteAnio, cpbteMes, cpbteDia);
		
		}catch( Exception e) {		cpbteFecha=null;	}
		
		
		Calendar fechaPrestacion = Calendar.getInstance();
		try {
			fechaPrestacion.set(fechaPrestacionAnio, fechaPrestacionMes, fechaPrestacionDia);
		}catch( Exception e) {
			fechaPrestacion=null;	
		}
		
		int Idprestacion;
		int Idfarmacia;
	
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
				
//		me aseguro sea un numero negativo para no confundir con IDs de BD
		Random r = new Random(System.currentTimeMillis());
		int idAux = r.nextInt(); // sale neg o pos, si es pos, lo pasamos con (-1)
		if(idAux > 0){
			idAux = (-1)*idAux;
		}	
		
		
		if (tipoNomenclador==1){
			// buscar id de la prestacion
			Idprestacion=0;
			List<Nomenclador> nomencladores = NomencladorServiceUtil.getListaNomenclador(tipoNomnecladorPrestacion,"",0, prestacion,false,"");
			   for(Nomenclador nom:nomencladores){			   				   
				   if(prestacion.equals(nom.getCodigo())  ){
				       Idprestacion= nom.getId_prestacion();
				    
				   }   
			   }
            if ( Idprestacion==0){
            	_log.debug("Error en la busqueda de id prestacion : ");
            }            	

			Idfarmacia =0;
		
		}else{ // farmacia 
			Idfarmacia =Integer.parseInt(troquel);
			Idprestacion=0;			
		}			
		
		PrestacionesReclamo prestacionreclamo = new PrestacionesReclamo(observaciones,frecuencia,cargoPs,importe,cargoOspim,Idprestacion,
				Idfarmacia ,tipoNomenclador,nombreMedicamento,nombrePrestacion,recuperableSur,cantidad,
				cpbteTipo, cpbteNro,cpbteFecha.getTime(), cpbteCantidad, cpbteImporte,cpbteTotal,cpbteCUIT,cpbteSucursal,
				cpbteCuitSucursal,comprobanteLetra, fechaPrestacion.getTime(), 0,cargoImesa);
		prestacionreclamo.setEstado(PrestacionesReclamo.ESTADOS.NUEVO );
		prestacionreclamo.setRecuperable(recuperable);
		prestacionreclamo.setReconocidoSSS(reconocidoSSS);
	
		if (Idprestacion != 0){
			prestacionreclamo.setCodigoPrestacion(prestacion);
		}else{
			prestacionreclamo.setCodigoPrestacion(troquel);

		}
		
		prestacionreclamo.setIdRegistro(idAux); 
		_log.debug("Agrega prestacion: " + prestacionreclamo.toString());
		
		@SuppressWarnings("unchecked")
		List<PrestacionesReclamo> prestacionesreclamo= (ArrayList<PrestacionesReclamo >) session.getAttribute(WebKeysAutorizaciones.LISTADO_PRESTACIONES_RECLAMOS_EN_SESION);

		session.removeAttribute(WebKeysAutorizaciones.LISTADO_PRESTACIONES_RECLAMOS_EN_SESION);
		
		if(prestacionesreclamo == null){
			prestacionesreclamo = new ArrayList<PrestacionesReclamo >();
		}
		
		
	
		
		prestacionesreclamo.add(prestacionreclamo);

		
		//boolean validaprestacion =true;
		
		//try{
			//validaprestacion = validaPrestacionReclamo (prestacionreclamo, prestacionesreclamo ); // valida que no se repita la prestacion
			
		//	if(validaprestacion ){
		//	}
			
		//}catch (PrestacionesReclamosException e) {
		//	SessionErrors.add(renderRequest, e.getClass().getName());
		//}
		
		
		//pongo la lista en session
		
		session.setAttribute(WebKeysAutorizaciones.LISTADO_PRESTACIONES_RECLAMOS_EN_SESION, prestacionesreclamo);	
				 
		if (WebKeysAutorizaciones.RECLAMO_PRESTACIONAL_SECCIONAL.equals(cmdAction)){
			return mapping.findForward(getForward(renderRequest,
					"portlet.autorizaciones.reclamosprestacionales.prestacion_reclamo_seccional"));
		}else{			
			return mapping.findForward(getForward(renderRequest,
					"portlet.autorizaciones.reclamosprestacionales.prestacion_reclamo"));
			
		}
		
	}
	
	


	private boolean validaPrestacionReclamo (PrestacionesReclamo  nuevaPrestacion , List<PrestacionesReclamo > lista)
			throws SystemException, PrestacionesReclamosException {

		boolean resultado = true;		
		boolean cond1 ;
			 
			for (int i = 0; i < lista.size(); i++) {	    
		 		
				PrestacionesReclamo mat = (PrestacionesReclamo) lista.get(i);
		 	if ( nuevaPrestacion.getId_medicamento()==0 ){		 		
		 		cond1 = mat.getEstado() != PrestacionesReclamo.ESTADOS.BAJA && mat.getId_prestacion() == nuevaPrestacion.getId_prestacion();
		 	}	
		 	else{
		 		cond1 = mat.getEstado() != PrestacionesReclamo.ESTADOS.BAJA && mat.getId_medicamento() == nuevaPrestacion.getId_medicamento();		 		
		 	}		 					
			
				if (cond1) {					
					resultado = false;					
		  			throw new PrestacionesReclamosException();
		 		}				
		 		
			} 
		
		return resultado;
	}
		
}