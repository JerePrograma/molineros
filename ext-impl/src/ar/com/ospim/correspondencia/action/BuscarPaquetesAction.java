package ar.com.ospim.correspondencia.action;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.correspondencia.WebKeysCorrespondencia;
import ar.com.ospim.correspondencia.beans.ItemCorrespondenciaTotal;
import ar.com.ospim.correspondencia.services.CorrespondenciaServiceImpl;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

/**
 * <a href="BuscarPaquetesAction.java.html"><b><i>View Source</i></b></a>
 * <p>
 * Realiza la búsqueda de paquetes según parámetros de entrada
 * 
 * @author Carlos Rivas
 * @modif SVA
 */
public class BuscarPaquetesAction extends PortletAction {
	
	private static Log _log = LogFactoryUtil.getLog(BuscarPaquetesAction.class);

	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
		
		try {
			String edificio = ParamUtil.getString(renderRequest, "edificio",null);
			String fechaDesdeFinal = ParamUtil.getString(renderRequest,"fechaDesdeFinal", null);
			String fechaDesempDesdeFinal = ParamUtil.getString(renderRequest,"fechaDesempDesdeFinal", null);
			String fechaDesempHastaFinal = ParamUtil.getString(renderRequest,"fechaDesempHastaFinal", null);
			long numero_correspondencia = ParamUtil.getLong(renderRequest,"numero_correspondencia", 0);
			String tipo_registro = ParamUtil.getString(renderRequest,"tipo_registro", null);
			long paquete = ParamUtil.getInteger(renderRequest, "paquete", 0);
			String tipo_envio = ParamUtil.getString(renderRequest,"tipo_envio", null);
			String tipo_remitente = ParamUtil.getString(renderRequest,"tipo_remitente", null);
			String cuil = ParamUtil.getString(renderRequest, "cuil", null);
			int inte = ParamUtil.getInteger(renderRequest, "inte", 0);
			String id_farmacia = ParamUtil.getString(renderRequest,"id_farmacia", null);
			String otros = ParamUtil.getString(renderRequest, "otros", null);
			int id_prestador = ParamUtil.getInteger(renderRequest,"id_prestador", 0);
			String cuit_entidad = ParamUtil.getString(renderRequest,"cuit_entidad", null);
			String sucursal_entidad = ParamUtil.getString(renderRequest,"sucursal_entidad", null);
			int id_seccional = ParamUtil.getInteger(renderRequest,"id_seccional", 0);
			String tipo_compro = ParamUtil.getString(renderRequest,"comprobante_tipo", null);
			String letra_compro = ParamUtil.getString(renderRequest,"comprobante_letra", null);
			int sucu = ParamUtil.getInteger(renderRequest, "sucu", 0);
			String nro_compro = ParamUtil.getString(renderRequest,"comprobante_nro", null);
			String importe_total = ParamUtil.getString(renderRequest,"importe_total", null);
			String edificio_destino = ParamUtil.getString(renderRequest,"edificio_destino", null);
			String usuario_destino = ParamUtil.getString(renderRequest,"usuario_destino", null);
			String sector_destino = ParamUtil.getString(renderRequest,"sector_destino", null);
			String contenido = ParamUtil.getString(renderRequest, "contenido",null);

			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			Date fecha = null, fechaDesempDesde = null, fechaDesempHasta = null;
			try {
				fecha = sdf.parse(fechaDesdeFinal);
			} catch (Exception e) {
				fecha = null;
			}
			try {
				fechaDesempDesde = sdf.parse(fechaDesempDesdeFinal);
			} catch (Exception e) {
				fechaDesempDesde = null;
			}
			try {
				fechaDesempHasta = sdf.parse(fechaDesempHastaFinal);
			} catch (Exception e) {
				fechaDesempHasta = null;
			}
			
			String llamada = ParamUtil.getString(renderRequest, "viene_de", "");
			int pagina_sel = ParamUtil.getInteger(renderRequest, "pagina", 1);
			pagina_sel--;
			
			List<ItemCorrespondenciaTotal> busqueda = CorrespondenciaServiceImpl
					.buscarPaquetesPagina(edificio, fecha, fechaDesempDesde, fechaDesempHasta, 
							numero_correspondencia, tipo_registro, paquete,
							tipo_envio, tipo_remitente, cuil, inte,
							id_farmacia, otros, id_prestador, cuit_entidad,
							sucursal_entidad, id_seccional, tipo_compro,
							letra_compro, sucu, nro_compro, importe_total,
							edificio_destino, usuario_destino, sector_destino,
							contenido, pagina_sel);

			// la lista en el request
			HttpSession session = (HttpSession) PortalUtil.getHttpServletRequest(renderRequest).getSession();

			session.removeAttribute(WebKeysCorrespondencia.BUSQUEDA_CORRESPONDENCIA_RESULT);
			session.setAttribute(WebKeysCorrespondencia.BUSQUEDA_CORRESPONDENCIA_RESULT, busqueda);

			if(busqueda != null && busqueda.size() > 0){
				session.setAttribute("total_registros", busqueda.get(0).getTotal_registros());
				session.setAttribute("offset_reg", pagina_sel);
				session.setAttribute("llamada", llamada);
			}else{
				session.setAttribute("total_registros",0 );
				session.setAttribute("offset_reg", 0);
				session.setAttribute("llamada", null);
			}
			
		} catch (Exception e) {
			_log.error(e);
			e.printStackTrace();
		}

		return mapping.findForward("portlet.ospim.correspondencia.paquetes.result.search");

	}

}