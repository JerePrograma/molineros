package ar.com.ospim.tesoreria.recibos.action;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.portlet.PortletConfig;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

import ar.com.ospim.afiliados.beans.Afiliado;
import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.beans.Concepto;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.hoteles.beans.Prestamo;
import ar.com.ospim.hoteles.services.HotelesServiceUtil;
import ar.com.ospim.hoteles.services.WebKeysHoteles;
import ar.com.ospim.liquidaciones.WebKeysLiquidaciones;
import ar.com.ospim.tesoreria.WebKeysTesoreria;
import ar.com.ospim.tesoreria.beans.FichaBoletaPortal;
import ar.com.ospim.tesoreria.beans.Recibo;
import ar.com.ospim.tesoreria.beans.ReciboOtroConcepto;
import ar.com.ospim.tesoreria.beans.ReciboPrestamo;
import ar.com.ospim.tesoreria.service.PortalEmpleadoresServiceUtil;

public class ABMReciboPrestamosAction extends PortletAction {
	private static Log _log = LogFactoryUtil
			.getLog(ABMReciboPrestamosAction.class);

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		_log.debug("Entrando a render");

		HttpSession session = PortalUtil.getHttpServletRequest(renderRequest)
				.getSession();

// Busqueda de Prestamos	
		String bprestamos = renderRequest.getParameter("prestamos");
		if (bprestamos != null && bprestamos.equals("prestamos")) {
			buscarPrestamos(renderRequest,session);
			session.setAttribute("esEdicion","NO");
			return mapping
					.findForward("portlet.hoteles.prestamos_result");
			
		}

		
		Recibo recibo = (Recibo) session
				.getAttribute(WebKeysTesoreria.RECIBO_EN_EDICION);

		if (recibo == null) {
			recibo = new Recibo();
		}

		if (recibo.getReciboPrestamos() == null) {
			recibo.setReciboPrestamos(new ArrayList<ReciboPrestamo>());
		}

		String borrar = renderRequest.getParameter("borrar");
		if (borrar != null && borrar.equals("borrar")) {
			borrarPrestamo(renderRequest, recibo);
		} else {
			agregarPrestamo(renderRequest, recibo);
		}

		session.setAttribute(WebKeysTesoreria.RECIBO_EN_EDICION, recibo);
		return mapping
				.findForward("portlet.tesoreria.recibos.prestamos.result.search");
	}

	private void agregarPrestamo(RenderRequest renderRequest, Recibo recibo) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		Date fecha = null;
		Long prestamoId = 0L;
		Double prestamoTotal=0D;
		Double prestamoImporte=0D;
		String prestamoNro = "";
		
		prestamoNro = ParamUtil.getString(renderRequest, "prestamo_id");
		try{
			prestamoId = Long.parseLong(prestamoNro);
		}catch(NumberFormatException e){
			
		}
		
		String importe = renderRequest.getParameter("importe");
		String importeTotal = renderRequest.getParameter("importeTotal");
		if(importeTotal==null || "".equalsIgnoreCase(importeTotal)) {
			importeTotal="0";
		}

		String dia = renderRequest.getParameter("dia");
		String mes = renderRequest.getParameter("mes");
		String anio = renderRequest.getParameter("anio");
		
		
		prestamoImporte=Double.parseDouble(importe); 
		prestamoTotal=Double.parseDouble(importeTotal);
		
		try {
			fecha=sdf.parse(dia+"/"+mes+"/"+anio);
		} catch (ParseException e) {
			fecha=null;
		}
		
        Prestamo prestamo = new Prestamo();
        prestamo.setId(prestamoId);
        prestamo.setTotal(prestamoTotal);
        prestamo.setMonto(prestamoImporte);
        prestamo.setAcuerdoFecha(fecha);
        
        Boolean encontro=false;
	    for(ReciboPrestamo r:recibo.getReciboPrestamos()) {
				  if(r.getPrestamo().getId()==prestamo.getId()){
					  encontro=true;
					  r.getPrestamo().setTotal( prestamo.getTotal()) ;
					  r.getPrestamo().setMonto(prestamo.getMonto());
					  r.getPrestamo().setAcuerdoFecha(prestamo.getAcuerdoFecha());
				  }
		}
		   
		if( !encontro) {
			   ReciboPrestamo rp = new ReciboPrestamo();
			   rp.setPrestamo(prestamo);
			   recibo.getReciboPrestamos().add(rp); 
		}
	}


	private void borrarPrestamo(RenderRequest renderRequest, Recibo recibo) {
		String id = renderRequest.getParameter("prestamo_id");
		Boolean procedeEliminar=true;
		
        List<ReciboPrestamo>aux=new ArrayList<ReciboPrestamo>();		
		for(ReciboPrestamo r:recibo.getReciboPrestamos()){
			if(r.getPrestamo().getId()!=Integer.parseInt(id) ) {
				aux.add(r);
			}
		}
		recibo.setReciboPrestamos(aux);
		
	}
	
	
	private void buscarPrestamos(RenderRequest renderRequest,HttpSession session) {

		Date fechaPeriodo = null;
		int conceptoId = 0;
		String comproNro = "";
		String cuil = ParamUtil.getString(renderRequest, "cuil",
				null);
		
		
		Prestamo filtro=new Prestamo();
		Afiliado afiliado = new Afiliado();
		afiliado.setCuil_titular(cuil);
		filtro.setAfiliado(afiliado);
		List<Prestamo> prestamos= new ArrayList<Prestamo>();
		try {
			prestamos = HotelesServiceUtil.getListaPrestamos(filtro);
		} catch (SystemException e) {
			
		}
		
		session.setAttribute(WebKeysHoteles.PRESTAMOS_RESULT, prestamos);
		
		
		
	}


}
