package ar.com.ospim.liquidaciones.action;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.global.beans.Prestacion;
import ar.com.ospim.liquidaciones.WebKeysLiquidaciones;
import ar.com.ospim.liquidaciones.administracion.prestadores.exception.ProfesionEspecialidadSubEspecPrestadorException;
import ar.com.ospim.liquidaciones.beans.ConvenioPrestacional;
import ar.com.ospim.liquidaciones.beans.ConvenioPrestacional.EstadosConvPrest;
import ar.com.ospim.liquidaciones.beans.ConvenioPrestacionalDetalle;
import ar.com.ospim.liquidaciones.beans.ConvenioPrestacionalDetalle.ESTADOS;
import ar.com.ospim.liquidaciones.beans.TipoNomenclador;
import ar.com.ospim.liquidaciones.services.ConvenioPrestacionalServiceUtil;
import ar.com.ospim.util.StringUtils;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

/**
 * @author SVA
 */

public class ListaPrestacionesConvenioPrestAction extends PortletAction {
	
	private static Log _log = LogFactoryUtil.getLog(ListaPrestacionesConvenioPrestAction.class);

	public ActionForward render(
			ActionMapping mapping, ActionForm form, PortletConfig portletConfig,
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws Exception {
		
		HttpSession session = (HttpSession) PortalUtil.getHttpServletRequest(renderRequest).getSession();
		Prestacion prestDesde=null, prestHasta=null;		
		
		boolean validaConvPrestDetalle = true;
		String cmd = ParamUtil.getString(renderRequest, Constants.CMD, null);
		ArrayList<ConvenioPrestacionalDetalle> detalles = null;
		
//		agrega una prestacion al convenio
		if(cmd!=null && cmd.equalsIgnoreCase(Constants.ADD)){
			int idTipoNomenclador = ParamUtil.getInteger(renderRequest, "tipoNomenclador");
			String tipoNomencladorDesc = ParamUtil.getString(renderRequest, "tipoNomencladorDesc");
			TipoNomenclador tn = new TipoNomenclador();
			tn.setId_tipo_nomenclador(idTipoNomenclador);
			tn.setDescripcion(tipoNomencladorDesc);
			Integer idPrestDesde = ParamUtil.getInteger(renderRequest, "idPrestDesde");
			prestDesde = new Prestacion(idPrestDesde, "");
			Integer idPrestHasta = ParamUtil.getInteger(renderRequest, "idPrestHasta");
			prestHasta = new Prestacion(idPrestHasta, "");
			String codigoPrestDesde = ParamUtil.getString(renderRequest, "codigoDesde");
			String codigoPrestHasta = ParamUtil.getString(renderRequest, "codigoHasta");
			String fechaDesdeFinal = ParamUtil.getString(renderRequest, "fechaDesde");
			String fechaHastaFinal = ParamUtil.getString(renderRequest, "fechaHasta");
			String servicio = ParamUtil.getString(renderRequest, "servicio");
			int idPlan = ParamUtil.getInteger(renderRequest, "planId");
			String planDescripcion = ParamUtil.getString(renderRequest, "planDesc");
			String tipoValorizacion = ParamUtil.getString(renderRequest, "tipoValorizacion");
			String coseguroAux = ParamUtil.getString(renderRequest, "coseguro");
			BigDecimal coseguro = new BigDecimal(StringUtils.checkNotEmpty(coseguroAux)?coseguroAux:"0");
	//		double honorarios = ParamUtil.getDouble(renderRequest, "honorarios");
	//		double gastos = ParamUtil.getDouble(renderRequest, "gastos");
			String importeAux = ParamUtil.getString(renderRequest, "importe");
			String porcentajeAux = ParamUtil.getString(renderRequest, "porcentaje");
			BigDecimal importe = new BigDecimal(StringUtils.checkNotEmpty(importeAux)?importeAux:"0");
			BigDecimal porcentaje = new BigDecimal(StringUtils.checkNotEmpty(porcentajeAux)?porcentajeAux:"0");
			
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			Date fechaDesde = null;
			try {
				fechaDesde = sdf.parse(fechaDesdeFinal);
			} catch (Exception e) {
				fechaDesde = null;
			}		
			Date fechaHasta = null;
			try {
				fechaHasta = sdf.parse(fechaHastaFinal);
			} catch (Exception e) {
				fechaHasta = null;
			}
	
			
	
	//		me aseguro sea un numero negativo para no confundir con IDs de BD
			Random r = new Random(System.currentTimeMillis());
			int idAux = r.nextInt(); // sale neg o pos, si es pos, lo pasamos con (-1)
			if(idAux > 0){
				idAux = (-1)*idAux;
			}
			
			ConvenioPrestacionalDetalle convPrestDetalle = new ConvenioPrestacionalDetalle(idAux, 0, fechaDesde, fechaHasta, 
					tn, prestDesde, codigoPrestDesde, prestHasta, codigoPrestHasta, idPlan, planDescripcion, coseguro, 
					tipoValorizacion, importe, porcentaje, servicio);
			
			convPrestDetalle.setEstado(ConvenioPrestacionalDetalle.ESTADOS.NUEVO);
	
			_log.debug("Agregar ConvenioPrestacionalDetalle: " + convPrestDetalle.toString());	
			
			detalles = (ArrayList<ConvenioPrestacionalDetalle>) session.getAttribute(WebKeysLiquidaciones.CONVENIO_PREST_DETALLES_EN_SESSION);
	
			if(detalles == null){
				detalles = new ArrayList<ConvenioPrestacionalDetalle>();
			}
			
			try{
				validaConvPrestDetalle = validaDetalleConvPrest(detalles, convPrestDetalle);
				
				if(validaConvPrestDetalle){
					detalles.add(convPrestDetalle);
				}
				
			}catch (ProfesionEspecialidadSubEspecPrestadorException e) {
				SessionErrors.add(renderRequest, e.getClass().getName());
			}		
			
			//pongo la lista en session
			session.removeAttribute(WebKeysLiquidaciones.CONVENIO_PREST_DETALLES_EN_SESSION);
			session.setAttribute(WebKeysLiquidaciones.CONVENIO_PREST_DETALLES_EN_SESSION, detalles);
		}
//		Copia toda las prestaciones del convenio de otro prestador en este.
		if(cmd!=null && cmd.equalsIgnoreCase(Constants.COPY)){
			
			int id_prestador = ParamUtil.getInteger(renderRequest,"id_prestador");

			ConvenioPrestacional convenioPrest = ConvenioPrestacionalServiceUtil
					.getConvenioPrestacionalPorPrestador(id_prestador);
			
			ArrayList<ConvenioPrestacionalDetalle> aux = (ArrayList<ConvenioPrestacionalDetalle>) convenioPrest.getConvenioPrestDetalle();
			for (Iterator<ConvenioPrestacionalDetalle> iterator = aux.iterator(); iterator.hasNext();) {
//				me aseguro sea un numero negativo para no confundir con IDs de BD
				Random r = new Random(System.currentTimeMillis());
				int idAux = r.nextInt(); // sale neg o pos, si es pos, lo pasamos con (-1)
				if(idAux > 0){
					idAux = (-1)*idAux;
				}
				ConvenioPrestacionalDetalle detAux = iterator.next();
				detAux.setEstado(ESTADOS.NUEVO);
				detAux.setId(idAux);
			}
			detalles = (ArrayList<ConvenioPrestacionalDetalle>) session.getAttribute(WebKeysLiquidaciones.CONVENIO_PREST_DETALLES_EN_SESSION);

			if (detalles == null) {
				detalles = new ArrayList<ConvenioPrestacionalDetalle>();
				
				detalles.addAll(convenioPrest.getConvenioPrestDetalle());
				
				session.setAttribute(WebKeysLiquidaciones.CONVENIO_PREST_DETALLES_EN_SESSION, detalles);
			}else{
//				Analizamos items duplicados, los que estan bien pasan, los que da error duplicado informa con mensaje...
				for (Iterator<ConvenioPrestacionalDetalle> iterator = aux.iterator(); iterator.hasNext();) {
					ConvenioPrestacionalDetalle detAux = iterator.next();
					try{
						validaConvPrestDetalle = validaDetalleConvPrest(detalles, detAux);
						
						if(validaConvPrestDetalle){
							detalles.add(detAux);
						}
						
					}catch (ProfesionEspecialidadSubEspecPrestadorException e) {
						SessionErrors.add(renderRequest, e.getClass().getName());
					}	
				}
				
			}
			
		}
//		elimina una prestacion del convenio, si es nueva solo la quita de la lista, sino, marca para borrar en la base...
		if(cmd!=null && cmd.equalsIgnoreCase(Constants.DELETE)){
			int idConvPrestDet = ParamUtil.getInteger(renderRequest,"id_convprest_det");
			ConvenioPrestacionalDetalle auxDet = new ConvenioPrestacionalDetalle();
			auxDet.setId(idConvPrestDet);
			_log.debug("Borrando conv prest id: " + idConvPrestDet);
			detalles = (ArrayList<ConvenioPrestacionalDetalle>) session.getAttribute(WebKeysLiquidaciones.CONVENIO_PREST_DETALLES_EN_SESSION);
			int pos = detalles.indexOf(auxDet);
			auxDet = detalles.get(pos);
			if(auxDet.getEstado()==null){
				auxDet.setEstado(ESTADOS.BAJA);
			}else{
				detalles.remove(pos);
			}
			session.removeAttribute(WebKeysLiquidaciones.CONVENIO_PREST_DETALLES_EN_SESSION);
			session.setAttribute(WebKeysLiquidaciones.CONVENIO_PREST_DETALLES_EN_SESSION, detalles);
		}
		
		return mapping.findForward(getForward(renderRequest,
				"portlet.liquidaciones.lista_convenio_prest_detalle"));
	}
	
	private boolean validaDetalleConvPrest(List<ConvenioPrestacionalDetalle> detalles, ConvenioPrestacionalDetalle convPrestDet) throws ProfesionEspecialidadSubEspecPrestadorException{
		
		boolean result = true;
//		for (Iterator<ProfesionPrestador> iterator = listaProf.iterator(); iterator.hasNext();) {
//			ProfesionPrestador _profPrest =  iterator.next();
//			EspecialidadPrestador _espePrest = _profPrest.getEspecialidades().get(0);
//			SubEspecialidadPrestador _subEspePrestador = _espePrest.getSubEspecialidades().get(0);
//			
//			if(_profPrest.getIdProfesion() == prof.getIdProfesion() 
//				&& _espePrest.getIdEspecialidad() == esp.getIdEspecialidad()
//				&& _subEspePrestador.getId() == subEsp.getId()){
//				
//				result = false;
//				throw new ProfesionEspecialidadSubEspecPrestadorException();
//			}
//		}

		return result;
	}
		
}