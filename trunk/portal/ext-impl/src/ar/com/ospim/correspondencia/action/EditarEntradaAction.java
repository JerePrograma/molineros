package ar.com.ospim.correspondencia.action;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.afiliados.beans.Afiliado;
import ar.com.ospim.correspondencia.WebKeysCorrespondencia;
import ar.com.ospim.correspondencia.beans.CabeceraCorrespondencia;
import ar.com.ospim.correspondencia.beans.ItemCorrespondencia;
import ar.com.ospim.correspondencia.beans.ItemCorrespondencia.RemitenteDestinatario;
import ar.com.ospim.correspondencia.services.CorrespondenciaServiceImpl;
import ar.com.ospim.correspondencia.services.CorrespondenciaServiceUtil;
import ar.com.ospim.global.beans.Empresa;
import ar.com.ospim.global.beans.Farmacia;
import ar.com.ospim.global.beans.Seccional;
import ar.com.ospim.liquidaciones.beans.Prestador;
import ar.com.ospim.util.StringUtils;

import com.liferay.portal.PortalException;
import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

/**
 * <a href="EditarEntradaAction.java.html"><b><i>View Source</i></b></a>
 * 
 * @author SVA
 * 
 */
public class EditarEntradaAction extends PortletAction {
	
	private Logger _log = Logger.getLogger(this.getClass());
	
// redirige al render
	public void processAction(ActionMapping mapping, ActionForm form,
							  PortletConfig portletConfig, ActionRequest actionRequest,
							  ActionResponse actionResponse) throws Exception {

	// preferi no hacer nada x el processAction...
//			System.out.println("pasando x el processAction");
		
	}
	
	@SuppressWarnings("deprecation")
	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
//			
		HttpServletRequest httpServletRequest = PortalUtil.getHttpServletRequest(renderRequest);
		HttpSession session = (HttpSession) httpServletRequest.getSession();
		User user = PortalUtil.getUser(renderRequest);
		String cmd = ParamUtil.getString(renderRequest, Constants.CMD, null);
		
		long idCorrespondencia = 0;
		long idItemCorrespondencia = ParamUtil.getLong(renderRequest,"id_item_correspondencia",0);
		
		String msg = "";
		
		CabeceraCorrespondencia cab, entrada = null;

		ArrayList<ItemCorrespondencia> items = null;
		ItemCorrespondencia item = null;

		if (!StringUtils.checkEmpty(cmd)) {
			
			if(cmd.equals(Constants.WRITE) ){ // lo voy a usar como -NEW, para crear nuevas entradas en blanco
				entrada = new CabeceraCorrespondencia();
				entrada.setTipoEnvio(WebKeysCorrespondencia.TIPOS_ENVIOS[0][0]);
				entrada.setTipoRegistro("ENTRADA");
				_log.debug("Usuario: " + user.getScreenName()); 
				entrada.setLugarRecepEmision(String.valueOf(user.getOrganizations().get(0).getOrganizationId()));
				entrada.setItemsCorrespondencia(new ArrayList<ItemCorrespondencia>());
				
				session.removeAttribute(WebKeysCorrespondencia.ENTRADA_DETALLE_EN_EDICION);
				
				session.setAttribute(WebKeysCorrespondencia.ENTRADA_EN_EDICION, entrada);
				
				_log.debug("Usuario: " + user.getScreenName() 
						+ " cmd: " + cmd 
//							+ " id corr: " + idCorrespondencia
//							+ " id item: " + idItemCorrespondencia
						);
				
				return mapping.findForward(getForward(renderRequest,
						"portlet.correspondencia.editar_entrada_entry"));
			}
			
			if(cmd.equals(Constants.EDIT) ){ // prepara itemCorrespondencia a editar
				
				idCorrespondencia = ParamUtil.getInteger(renderRequest,"id_correspondencia", 0);
				int id_item_correspondencia = ParamUtil.getInteger(renderRequest,"id_item_correspondencia");
				
				ItemCorrespondencia itemAEditar =  CorrespondenciaServiceUtil.buscarItemCorrespondenciaPorId(id_item_correspondencia);
				
				session.setAttribute(WebKeysCorrespondencia.ENTRADA_DETALLE_EN_EDICION, itemAEditar);
								
				_log.debug("Usuario: " + user.getScreenName() 
						+ " cmd: " + cmd 
						+ " id corr: " + idCorrespondencia
						+ " id item: " + idItemCorrespondencia
						);
				
				return mapping.findForward(getForward(renderRequest,
						"portlet.ospim.correspondencia.detalle.item"));		
			}
			
			if(cmd.equals(Constants.CANCEL)){ // cancela edicion item
				session.removeAttribute(WebKeysCorrespondencia.ENTRADA_DETALLE_EN_EDICION);
				
				_log.debug("Usuario: " + user.getScreenName() 
						+ " cmd: " + cmd 
						+ " id corr: " + idCorrespondencia
						+ " id item: " + idItemCorrespondencia
						);
				
				return mapping.findForward(getForward(renderRequest,
						"portlet.ospim.correspondencia.detalle.item"));		
			}
			
			entrada = (CabeceraCorrespondencia) session.getAttribute(WebKeysCorrespondencia.ENTRADA_EN_EDICION);
			cab = entrada;
			session.removeAttribute(WebKeysCorrespondencia.ENTRADA_EN_EDICION);
			
			if(entrada !=null){
//					idCorrespondencia = entrada.getId_correspondencia();
				items = entrada.getItemsCorrespondencia();		
			}else{
				items = new ArrayList<ItemCorrespondencia>();
			}
//				Puede ser que me editen la cabecera por cada alta item o modificacion gral de la corresp. )
			entrada = getCorrespFromRequest(renderRequest);
			idCorrespondencia = entrada.getId_correspondencia();
			
//				graba toda la carga inicial
			if(cmd.equals(Constants.ADD) ){

				item = getItemCorrespFromRequest(renderRequest);
				item.setEstado("INGRESADO");	   
				
				
				if(idCorrespondencia == 0){// primera vez, antes del insert...
					idCorrespondencia = insertCabeceraCorrespondencia(entrada, user.getScreenName());
					entrada.setId_correspondencia(idCorrespondencia);
					
					msg = LanguageUtil.get(defaultLocale, "insert-correspondencia");
					msg = msg + idCorrespondencia;
					SessionMessages.add(renderRequest, "insertCabOk");
					renderRequest.setAttribute("msgCabOk", msg);
					_log.debug("Usuario: " + user.getScreenName() 
							+ " cmd: " + cmd 
							+ " id corr: " + idCorrespondencia
//								+ " id item: " + idItemCorrespondencia
							);
				}
				item.setId_correspondencia(idCorrespondencia);
			
				idItemCorrespondencia = CorrespondenciaServiceUtil.cargaCorrespondenciaDetalleEntry(item,user);
				
				item.setId(idItemCorrespondencia);
				
				if(item.getRemiDest()!=null) {
					renderRequest.setAttribute(WebKeysCorrespondencia.REMITENTE, item.getRemiDest());
				}
				
				msg = LanguageUtil.get(defaultLocale, "insert-item-correspondencia");
				msg = msg + idItemCorrespondencia + " a la correspondencia: " + idCorrespondencia;
				SessionMessages.add(renderRequest, "insertItemOk");
				renderRequest.setAttribute("msgItemOk", msg);
				_log.debug("Usuario: " + user.getScreenName() 
						+ " cmd: " + cmd 
						+ " id corr: " + idCorrespondencia
						+ " id item: " + idItemCorrespondencia
						);
			}
			// la correspondencia y algun item ya existe.
			if(cmd.equals(Constants.UPDATE) ){
				
				item = getItemCorrespFromRequest(renderRequest);
//				item.setEstado("INGRESADO");	   
				
				if(item.getId() > 0){
//					guardamos una historia del item antes de actualizar
					CorrespondenciaServiceUtil.actualizarCorrespondenciaHistorico(Integer.parseInt(String.valueOf(item.getId())));
				
//					actualizamos el item
					CorrespondenciaServiceUtil.updateItemCorrespondencia(Integer.parseInt(String.valueOf(item.getId())), 
							item.getTipoRemitenteDestinatario(), item.getEdificio(),
							item.getSector(), item.getUsuario(), item.getEmpresa_remite(),
							item.getSector_remite(), item.getUsuario_remite(), item.getContenido(), item.getAfiliado().getCuil_titular(),
							item.getAfiliado().getInte(), item.getFarmacia().getId_farmacia(), item.getDescRemitenteDestinatario(), 
							item.getPrestador().getId_prestador(), item.getProveedor().getCuit(), item.getProveedor().getSucursal(), 
							item.getId_punto_venta(), item.getCompro_tipo(), item.getCompro_nro(), item.getCuit(), item.getCompro_letra(), 
							item.getCompro_sucu(), item.getCompro_periodo(), item.getImporte(), item.getFecha_emision(), item.getFecha_vencimiento(), 
							item.getSeccional().getId(), item.getSeguimientoPaquete()!=null?item.getSeguimientoPaquete():null, 
							user.getScreenName());
				
					msg = LanguageUtil.get(defaultLocale, "update-item-correspondencia");
					msg = msg + idItemCorrespondencia + " de la correspondencia: " + idCorrespondencia;
					SessionMessages.add(renderRequest, "updateItemOk");
					renderRequest.setAttribute("msgItemOk", msg); 
					
					session.removeAttribute(WebKeysCorrespondencia.ENTRADA_DETALLE_EN_EDICION);
					
				}
				_log.debug("Usuario: " + user.getScreenName() 
				+ " cmd: " + cmd 
				+ " id corr: " + idCorrespondencia
				+ " id item: " + idItemCorrespondencia
				);
				
				if(!cab.equals(entrada)){ // revisamos si hubo cambios de cabecera, para actualizar si corresponde
					updateCabeceraCorrespondencia(entrada, user.getScreenName());
	
					msg = LanguageUtil.get(defaultLocale, "update-correspondencia");
					msg = msg + idCorrespondencia;
					SessionMessages.add(renderRequest, "updateCabOk");
					renderRequest.setAttribute("msgCabOk", msg);  
					
					_log.debug("Usuario: " + user.getScreenName() 
							+ " cmd: " + cmd 
							+ " id corr: " + idCorrespondencia
							+ " id item: " + idItemCorrespondencia
							);
				}
				
			}
			
			if (cmd.equals(Constants.DELETE)) { // borrado logico
				CorrespondenciaServiceUtil.borraCorrespondenciaDetalleEntry( (int)idItemCorrespondencia,user.getScreenName());

				msg = LanguageUtil.get(defaultLocale, "delete-item-correspondencia");
				msg = msg + idItemCorrespondencia + " de la correspondencia: " + idCorrespondencia;
				SessionMessages.add(renderRequest, "deleteItemOk");
				renderRequest.setAttribute("msgItemOk", msg); 
				
//					refreshResult = true;
				
				_log.debug("Usuario: " + user.getScreenName() 
						+ " cmd: " + cmd 
						+ " id corr: " + idCorrespondencia
						+ " id item: " + idItemCorrespondencia
						);
			}
			
//				los recupero con el id de la BD
			items = (ArrayList<ItemCorrespondencia>) CorrespondenciaServiceImpl.buscarItemsPorIdCorrespondencia(Integer.parseInt( String.valueOf(idCorrespondencia) )  );
			entrada.setItemsCorrespondencia(items);
		}
		
		session.setAttribute(WebKeysCorrespondencia.ENTRADA_EN_EDICION, entrada);
		
		return mapping.findForward(getForward(renderRequest,
				"portlet.correspondencia.editar_entrada_entry"));
	}

	private CabeceraCorrespondencia getCorrespFromRequest(RenderRequest renderRequest){
		
		CabeceraCorrespondencia ent = new CabeceraCorrespondencia();
		
		String edificio = ParamUtil.getString(renderRequest, "edificio", null);
		long numero_correspondencia = ParamUtil.getLong(renderRequest,"id_correspondencia", 0);
		String tipo_registro = ParamUtil.getString(renderRequest,"tipo_registro", null);
		String tipo_envio = ParamUtil.getString(renderRequest, "tipo_envio",null);
		String oblea = ParamUtil.getString(renderRequest, "oblea", null);
		
		SimpleDateFormat formatoDeFecha = new SimpleDateFormat("dd/MM/yyyy");
		String fechaRecepDia = ParamUtil.getString(renderRequest, "fechaDesdeDia");
		String fechaRecepMes = ParamUtil.getString(renderRequest, "fechaDesdeMes");
		String fechaRecepAnio = ParamUtil.getString(renderRequest, "fechaDesdeAnio");
		Date fechaRecep = null;
		try {
			fechaRecep = formatoDeFecha.parse(fechaRecepDia + "/"
					+ (Integer.parseInt(fechaRecepMes) + 1) + "/" + fechaRecepAnio);
		} catch (Exception e) {
			fechaRecep = null;
		}
		
		ent.setFecha(fechaRecep);
		ent.setLugarRecepEmision(edificio);
		ent.setTipoEnvio(tipo_envio);
		ent.setTipoRegistro(tipo_registro);
		ent.setId_correspondencia(numero_correspondencia);
		ent.setOblea(oblea);
		
		return ent;
	}
	
	private ItemCorrespondencia getItemCorrespFromRequest(RenderRequest renderRequest ){
	
		User user = null;
		try {
			user = PortalUtil.getUser(renderRequest);
		} catch (PortalException e1) {
			_log.error(e1);
		} catch (SystemException e2) {
			_log.error(e2);
		}
		
		SimpleDateFormat formatoDeFecha = new SimpleDateFormat("dd/MM/yyyy");
		ItemCorrespondencia item = null;
		long paquete = ParamUtil.getInteger(renderRequest, "paquete", 0);		
		long numeroCorrespondencia = 0;
		
		String tipo_envio = ParamUtil.getString(renderRequest, "tipo_envio",null);
		String tipo_registro = ParamUtil.getString(renderRequest,"tipo_registro", null);
		
		long idItem = 0;
		String idItemStr = ParamUtil.getString(renderRequest,"id_item_correspondencia");
		if(idItemStr != null && !idItemStr.isEmpty()){
			idItem = Long.parseLong(idItemStr);
		}
		
		String tipo_remitente = ParamUtil.getString(renderRequest,"tipo_remitente", null);
		String cuil = ParamUtil.getString(renderRequest, "cuil", null);
		int inte = ParamUtil.getInteger(renderRequest, "inte", 0);
		String id_farmacia = ParamUtil.getString(renderRequest, "id_farmacia",null);
		String id_farmacia_serial = ParamUtil.getString(renderRequest, "id_farmacia_serial",null);
		String otros = ParamUtil.getString(renderRequest, "otros", null);
		int id_prestador = ParamUtil.getInteger(renderRequest, "id_prestador",0);
		String cuit_entidad = ParamUtil.getString(renderRequest,"cuit_entidad_cor", null);
		String sucursal_entidad = ParamUtil.getString(renderRequest,"sucursal_entidad_cor", null);
		int id_seccional = ParamUtil.getInteger(renderRequest, "id_seccional_r",0);
		String tipo_compro =  null;
		String letra_compro = null;
		int sucu_compro = 0;
		String nro_compro = null;
		String importe_total = null;
//			Solo cargamos el comprobante si se marco el check de CargaFC
		if(ParamUtil.getString(renderRequest,"cargaFC")!=null && ParamUtil.getString(renderRequest,"cargaFC").equalsIgnoreCase("on")){
		 tipo_compro = ParamUtil.getString(renderRequest,"comprobante_tipo", null);
		 letra_compro = ParamUtil.getString(renderRequest,"comprobante_letra", null);
		 sucu_compro = ParamUtil.getInteger(renderRequest, "comprobante_sucu", 0);
		 nro_compro = ParamUtil.getString(renderRequest, "comprobante_nro", null);
		 importe_total = ParamUtil.getString(renderRequest,"comprobante_importe_total", null);
		}
		String fechaEDia = ParamUtil.getString(renderRequest, "fechaEDia");
		String fechaEMes = ParamUtil.getString(renderRequest, "fechaEMes");
		String fechaEAnio = ParamUtil.getString(renderRequest, "fechaEAnio");

		String fechaVDia = ParamUtil.getString(renderRequest, "fechaVDia");
		String fechaVMes = ParamUtil.getString(renderRequest, "fechaVMes");
		String fechaVAnio = ParamUtil.getString(renderRequest, "fechaVAnio");

		String periodoComprobDia = "1";//ParamUtil.getString(renderRequest, "periodoComprobanteDia");
		String periodoComprobMes = ParamUtil.getString(renderRequest, "periodoComprobanteMes");
		String periodoComprobAnio = ParamUtil.getString(renderRequest, "periodoComprobanteAnio");
		
		Date fechaEmision = null;
		try {
			fechaEmision = formatoDeFecha.parse(fechaEDia + "/"
					+ (Integer.parseInt(fechaEMes) + 1) + "/" + fechaEAnio);
		} catch (Exception e) {
			fechaEmision = null;
		}

		Date fechaVencimiento = null;
		try {
			fechaVencimiento = formatoDeFecha.parse(fechaVDia + "/"
					+ (Integer.parseInt(fechaVMes) + 1) + "/" + fechaVAnio);
		} catch (Exception e) {
			fechaVencimiento = null;
		}
		Date periodoComprobante = null;
		try {
			periodoComprobante = formatoDeFecha.parse(periodoComprobDia + "/"
					+ (Integer.parseInt(periodoComprobMes) + 1) + "/" + periodoComprobAnio);
		} catch (Exception e) {
			periodoComprobante = null;
		}
		String edificio_destino = ParamUtil.getString(renderRequest,"edificio_destino", null);
		String usuario_destino = ParamUtil.getString(renderRequest,"usuario_destino", null);
		String sector_destino = ParamUtil.getString(renderRequest,"sector_destino", null);
//		String empresa_remite = String.valueOf(user.getOrganizations().get(0).getOrganizationId()) ;
//		String sector_remite = String.valueOf(user.getUserGroups().get(0).getUserGroupId()) ;
//		String usuario_remite = String.valueOf(user.getScreenName()) ;
		
		String empresa_remite   = ParamUtil.getString(renderRequest,"combo_0", String.valueOf(user.getOrganizations().get(0).getOrganizationId()) );
		String sector_remite    = ParamUtil.getString(renderRequest,"combo_1", String.valueOf(user.getUserGroups().get(0).getUserGroupId()) );
		String usuario_remite   = ParamUtil.getString(renderRequest,"combo_2", String.valueOf(user.getScreenName()) );
		
		
		String contenido = ParamUtil.getString(renderRequest, "contenido", null);
		String nombre_afiliado = ParamUtil.getString(renderRequest, "nombre","");
		String apellido_afiliado = ParamUtil.getString(renderRequest,"apellido", "");
		String desc_farmacia = ParamUtil.getString(renderRequest, "farmacia","");
		String desc_prestador = ParamUtil.getString(renderRequest,"nombre_prestador", "");
		String desc_empresa = ParamUtil.getString(renderRequest,"entidad_ent_cor", "");
		String desc_seccional = ParamUtil.getString(renderRequest,"seccional_r", "");
		
		String seguim_paquete = ParamUtil.getString(renderRequest,"seguimiento_paquete", null);
		
		item = new ItemCorrespondencia(numeroCorrespondencia, tipo_registro, paquete, tipo_envio, tipo_remitente, cuil, inte, 
									nombre_afiliado, apellido_afiliado, id_farmacia==""?"0":id_farmacia, id_farmacia_serial==""?"0":id_farmacia_serial, desc_farmacia, otros, id_prestador, desc_prestador, cuit_entidad, 
									sucursal_entidad, desc_empresa, id_seccional, desc_seccional, tipo_compro, letra_compro, sucu_compro, nro_compro, 
									importe_total, periodoComprobante, edificio_destino, usuario_destino, sector_destino, 
									empresa_remite, sector_remite, usuario_remite, contenido, fechaEmision, fechaVencimiento, seguim_paquete, 0);
//		recuperamos el remitente para facilitar proximas cargas del mismo
		if(ParamUtil.getString(renderRequest,"mantieneRemitente")!=null 
				&& ParamUtil.getString(renderRequest,"mantieneRemitente").equalsIgnoreCase("on")){
			
		
			item.setRemiDest(new RemitenteDestinatario(tipo_remitente, 
					new Afiliado(cuil, inte,nombre_afiliado, apellido_afiliado), 
					new Farmacia(id_farmacia_serial==""?new Integer(0):Integer.parseInt(id_farmacia_serial), id_farmacia==""?"0":id_farmacia, desc_farmacia), 
					otros, 
					new Prestador("",id_prestador, desc_prestador), 
					new Empresa(cuit_entidad, sucursal_entidad, desc_empresa), 
					new Seccional(id_seccional, desc_seccional),
					empresa_remite, "", sector_remite, "", usuario_remite));
		}
		
		item.setId(idItem);
		
		return item;
	}
	
	private long insertCabeceraCorrespondencia(CabeceraCorrespondencia entrada, String user) throws SystemException{
		long id = 0;
		
		id = CorrespondenciaServiceUtil.insertaCabeceraCorrespondencia(entrada.getLugarRecepEmision(), entrada.getFecha(), entrada.getId_correspondencia(),
				entrada.getTipoRegistro(), entrada.getTipoEnvio(), entrada.getOblea(), user);
				
		return id;
	}
	
	private void updateCabeceraCorrespondencia(CabeceraCorrespondencia entrada, String user) throws SystemException{
		
		CorrespondenciaServiceUtil.actualizaCabeceraCorrespondencia(entrada.getLugarRecepEmision(), entrada.getFecha(), entrada.getId_correspondencia(),
								entrada.getTipoRegistro(), entrada.getTipoEnvio(), entrada.getOblea(), user);
	}
	
}
