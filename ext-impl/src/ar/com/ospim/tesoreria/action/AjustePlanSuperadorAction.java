package ar.com.ospim.tesoreria.action;


import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

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

import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

import ar.com.ospim.afiliados.beans.Afiliado;
import ar.com.ospim.afiliados.services.BusquedaAfiliadoServiceUtil;
import ar.com.ospim.global.WebKeysGlobal;
import ar.com.ospim.global.beans.Parentesco;
import ar.com.ospim.global.beans.Plan;
import ar.com.ospim.global.beans.Provincia;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.tesoreria.WebKeysTesoreria;
import ar.com.ospim.tesoreria.beans.AjustePlanSuperador;
import ar.com.ospim.tesoreria.beans.PrecioPlanSuperador;
import ar.com.ospim.tesoreria.service.LiquidacionPlanesSuperadoresServiceUtil;
import ar.com.ospim.util.StringUtils;
import com.liferay.portal.kernel.servlet.SessionMessages;

public class AjustePlanSuperadorAction extends PortletAction {
	
	private Logger _log = Logger.getLogger(this.getClass());
	SimpleDateFormat formatoDeFechas = new SimpleDateFormat("dd/MM/yyyy");
	public void processAction(ActionMapping mapping, ActionForm form,
							  PortletConfig portletConfig, ActionRequest actionRequest,
							  ActionResponse actionResponse) throws Exception {
	}
	
	@SuppressWarnings("deprecation")
	public ActionForward render(ActionMapping mapping, ActionForm form,
			PortletConfig portletConfig, RenderRequest renderRequest,
			RenderResponse renderResponse) throws Exception {
			
		HttpServletRequest httpServletRequest = PortalUtil.getHttpServletRequest(renderRequest);
		HttpSession session = (HttpSession) httpServletRequest.getSession();
		User user = PortalUtil.getUser(renderRequest);
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
	    DecimalFormat df = new DecimalFormat("0.00");
		String cmd = ParamUtil.getString(renderRequest, Constants.CMD, null);
		
		AjustePlanSuperador precio = null;
		
		Long idPrecio = 0L;
		String msg = "";
		
		
        int entidad=WebKeysGlobal.OSPIM;
		if(renderResponse.getNamespace().equals("_FAR_1_")){
			entidad=WebKeysGlobal.AMTIMA;
		}else if(renderResponse.getNamespace().equals("_UOM_1_")){
			entidad=WebKeysGlobal.UOMA;
		}
		
		if (!StringUtils.checkEmpty(cmd)) {
			idPrecio = ParamUtil.getLong(renderRequest,"id_precio", 0);
			if("NEW".equals(cmd) ){ 
				
				precio = new AjustePlanSuperador();
				
				session.setAttribute(WebKeysTesoreria.AJUSTE_EN_SESSION , precio);
				
				List<Plan> planes=TraeListasServiceUtil.getPlanesFacturables();
				session.setAttribute(WebKeysTesoreria.AJUSTE_EN_SESSION_PLANES,planes);
				
				List<Parentesco> parentescos = TraeListasServiceUtil.getParentescosFacturables();
				session.setAttribute(WebKeysTesoreria.AJUSTE_EN_SESSION_PARENTESCOS,parentescos);
				
				List<Provincia> provinciasPrecio = TraeListasServiceUtil.getProvinciasFacturables();
				session.setAttribute(WebKeysTesoreria.AJUSTE_EN_SESSION_PROVINCIAS,provinciasPrecio);
				
				_log.debug("Usuario: " + user.getScreenName() 
						+ " cmd: " + cmd 
						);
				
				return mapping.findForward(getForward(renderRequest,
						"portlet.tesoreria.facturacion.listaajustes.edit"));
			}
			
			if(cmd.equals(Constants.EDIT) ){
				precio= LiquidacionPlanesSuperadoresServiceUtil.getPlanSuperadorAjuste(idPrecio.intValue());
				session.setAttribute(WebKeysTesoreria.AJUSTE_EN_SESSION, precio);
				
				List<Plan> planes=TraeListasServiceUtil.getPlanesFacturables();
				planes.removeAll(precio.getPlanes());
				session.setAttribute(WebKeysTesoreria.AJUSTE_EN_SESSION_PLANES,planes);
				
				List<Parentesco> parentescos = TraeListasServiceUtil.getParentescosFacturables();
				parentescos.removeAll(precio.getParentescos());
				session.setAttribute(WebKeysTesoreria.AJUSTE_EN_SESSION_PARENTESCOS,parentescos);
				
				List<Provincia> provinciasPrecio = TraeListasServiceUtil.getProvinciasFacturables();
				provinciasPrecio.removeAll(precio.getProvincias());
				session.setAttribute(WebKeysTesoreria.AJUSTE_EN_SESSION_PROVINCIAS,provinciasPrecio);
				return mapping.findForward(getForward(renderRequest,
						"portlet.tesoreria.facturacion.listaajustes.edit"));
			}
			
			
			if(cmd.equals("filter") ){
				String fechaDdeDia = ParamUtil.getString(renderRequest,"fechadesdedia");
				String fechaDdeMes = ParamUtil.getString(renderRequest,"fechadesdemes");
				String fechaDdeAnio = ParamUtil.getString(renderRequest,"fechadesdeanio");
				String descripcion = ParamUtil.getString(renderRequest,"descripcion");
				
				Date fechaDde = null;
				try {
					fechaDde = formatoDeFechas.parse(fechaDdeDia + "/"
							+ (Integer.parseInt(fechaDdeMes) + 1) + "/"
							+ fechaDdeAnio);
				} catch (Exception e) {
					fechaDde = null;
				}
				
				Integer plan = ParamUtil.getInteger(renderRequest,"plan");
				Integer parentesco = ParamUtil.getInteger(renderRequest,"parentesco");
				Integer provincia = ParamUtil.getInteger(renderRequest,"provincia");
				String cuil = ParamUtil.getString(renderRequest,"cuil");
				Afiliado a = new Afiliado(cuil,0);
				AjustePlanSuperador filtro = new AjustePlanSuperador();
				filtro.setId(idPrecio.intValue());
				filtro.setDescripcion(descripcion);
				filtro.setFechaDesde(fechaDde);
				filtro.getAfiliados().add(a);
				Plan p =new Plan();
				p.setId(plan);
				filtro.getPlanes().add(p);
				Parentesco pa = new Parentesco();
				pa.setCodigo(parentesco);
				filtro.getParentescos().add(pa);
				Provincia pr = new Provincia();
				pr.setId(provincia);
				filtro.getProvincias().add(pr);
				
				List<AjustePlanSuperador>precios= LiquidacionPlanesSuperadoresServiceUtil.searchPlanSuperadorAjustes(filtro);
				
				session.setAttribute(WebKeysTesoreria.AJUSTES_RESULT , precios);
				return mapping
        				.findForward("portlet.tesoreria.facturacion.listaajustes.result");
			}
			
			if(cmd.equals("agregarParentesco") ){
				String parentescos = ParamUtil.getString(renderRequest,"parentescosid");
				String[] parentescosArray=parentescos.split(",");
				
				List<Parentesco>listaParentescos = (List<Parentesco>) session.getAttribute(WebKeysTesoreria.AJUSTE_EN_SESSION_PARENTESCOS);
				precio =(AjustePlanSuperador)  session.getAttribute(WebKeysTesoreria.AJUSTE_EN_SESSION);
				Iterator<Parentesco> iterator = listaParentescos.iterator();
		        while (iterator.hasNext()) {
		            Parentesco p = iterator.next();
		            if( Arrays.asList(parentescosArray).contains(String.valueOf(p.getCodigo()))) {
						precio.getParentescos().add(p);
						iterator.remove();
					}
		        }
				
				session.setAttribute(WebKeysTesoreria.AJUSTE_EN_SESSION_PARENTESCOS , listaParentescos);
				session.setAttribute(WebKeysTesoreria.AJUSTE_EN_EDICION , precio);
				return mapping
        				.findForward("portlet.tesoreria.facturacion.listaajustes.parentescos");
			}
			
			if(cmd.equals("sacarParentesco") ){
				String parentescos = ParamUtil.getString(renderRequest,"parentescosid");
				String[] parentescosArray=parentescos.split(",");
				
				List<Parentesco>listaParentescos = (List<Parentesco>) session.getAttribute(WebKeysTesoreria.AJUSTE_EN_SESSION_PARENTESCOS);
				precio =(AjustePlanSuperador)  session.getAttribute(WebKeysTesoreria.AJUSTE_EN_SESSION);
				Iterator<Parentesco> iterator = precio.getParentescos().iterator();
		        while (iterator.hasNext()) {
		            Parentesco p = iterator.next();
		            if( Arrays.asList(parentescosArray).contains(String.valueOf(p.getCodigo()))) {
		            	listaParentescos.add(p);
						iterator.remove();
					}
		        }
				
				session.setAttribute(WebKeysTesoreria.AJUSTE_EN_SESSION_PARENTESCOS , listaParentescos);
				session.setAttribute(WebKeysTesoreria.AJUSTE_EN_EDICION , precio);
				return mapping
        				.findForward("portlet.tesoreria.facturacion.listaajustes.parentescos");
			}
			
			
			if(cmd.equals("agregarPlan") ){
				String planes = ParamUtil.getString(renderRequest,"planesid");
				String[] planesArray=planes.split(",");
				
				List<Plan>listaPlanes = (List<Plan>) session.getAttribute(WebKeysTesoreria.AJUSTE_EN_SESSION_PLANES);
				precio =(AjustePlanSuperador)  session.getAttribute(WebKeysTesoreria.AJUSTE_EN_SESSION);
				Iterator<Plan> iterator = listaPlanes.iterator();
		        while (iterator.hasNext()) {
		            Plan p = iterator.next();
		            if( Arrays.asList(planesArray).contains(String.valueOf(p.getId()))) {
						precio.getPlanes().add(p);
						iterator.remove();
					}
		        }
				
				session.setAttribute(WebKeysTesoreria.AJUSTE_EN_SESSION_PLANES , listaPlanes);
				session.setAttribute(WebKeysTesoreria.AJUSTE_EN_EDICION , precio);
				return mapping
        				.findForward("portlet.tesoreria.facturacion.listaajustes.planes");
			}
			
			if(cmd.equals("sacarPlan") ){
				String planes = ParamUtil.getString(renderRequest,"planesid");
				String[] planesArray=planes.split(",");
				
				List<Plan>listaPlanes = (List<Plan>) session.getAttribute(WebKeysTesoreria.AJUSTE_EN_SESSION_PLANES);
				precio =(AjustePlanSuperador)  session.getAttribute(WebKeysTesoreria.AJUSTE_EN_SESSION);
				Iterator<Plan> iterator = precio.getPlanes().iterator();
		        while (iterator.hasNext()) {
		            Plan p = iterator.next();
		            if( Arrays.asList(planesArray).contains(String.valueOf(p.getId()))) {
		            	listaPlanes.add(p);
						iterator.remove();
					}
		        }
				
				session.setAttribute(WebKeysTesoreria.AJUSTE_EN_SESSION_PLANES , listaPlanes);
				session.setAttribute(WebKeysTesoreria.AJUSTE_EN_EDICION , precio);
				return mapping
        				.findForward("portlet.tesoreria.facturacion.listaajustes.planes");
			}
			
			
			if(cmd.equals("agregarLocalidad") ){
				String provincias = ParamUtil.getString(renderRequest,"provinciasid");
				String[] provinciasArray=provincias.split(",");
				
				List<Provincia>listaLocalidades = (List<Provincia>) session.getAttribute(WebKeysTesoreria.AJUSTE_EN_SESSION_PROVINCIAS);
				precio =(AjustePlanSuperador)  session.getAttribute(WebKeysTesoreria.AJUSTE_EN_SESSION);
				Iterator<Provincia> iterator = listaLocalidades.iterator();
		        while (iterator.hasNext()) {
		            Provincia p = iterator.next();
		            if( Arrays.asList(provinciasArray).contains(String.valueOf(p.getId()))) {
						precio.getProvincias().add(p);
						iterator.remove();
					}
		        }
				
				session.setAttribute(WebKeysTesoreria.AJUSTE_EN_SESSION_PROVINCIAS , listaLocalidades);
				session.setAttribute(WebKeysTesoreria.AJUSTE_EN_EDICION , precio);
				return mapping
        				.findForward("portlet.tesoreria.facturacion.listaajustes.localidades");
			}
			
			if(cmd.equals("sacarLocalidad") ){
				String provincias = ParamUtil.getString(renderRequest,"provinciasid");
				String[] provinciasArray=provincias.split(",");
				
				List<Provincia>listaLocalidades = (List<Provincia>) session.getAttribute(WebKeysTesoreria.AJUSTE_EN_SESSION_PROVINCIAS);
				precio =(AjustePlanSuperador)  session.getAttribute(WebKeysTesoreria.AJUSTE_EN_SESSION);
				Iterator<Provincia> iterator = precio.getProvincias().iterator();
		        while (iterator.hasNext()) {
		            Provincia p = iterator.next();
		            if( Arrays.asList(provinciasArray).contains(String.valueOf(p.getId()))) {
		            	listaLocalidades.add(p);
						iterator.remove();
					}
		        }
				
				session.setAttribute(WebKeysTesoreria.AJUSTE_EN_SESSION_PROVINCIAS , listaLocalidades);
				session.setAttribute(WebKeysTesoreria.AJUSTE_EN_EDICION , precio);
				return mapping
        				.findForward("portlet.tesoreria.facturacion.listaajustes.localidades");
			}
			
			
			if(cmd.equals("agregarValor") ){
				
				String cuil = ParamUtil.getString(renderRequest,"cuil");
				precio =(AjustePlanSuperador)  session.getAttribute(WebKeysTesoreria.AJUSTE_EN_SESSION);
				
				List<Afiliado> list = BusquedaAfiliadoServiceUtil.getBusquedaAfiliadosComponente( cuil,
						"0", null, null, 0,
						null,null, null, 0, 
						0, null) ;
				if(list!=null && list.size()>0)
				   precio.getAfiliados().add(list.get(0));
				
				session.setAttribute(WebKeysTesoreria.AJUSTE_EN_EDICION , precio);
				
				return mapping
        				.findForward("portlet.tesoreria.facturacion.listaajustes.cuiles");
			}
			
			
			if(cmd.equals("sacarValor") ){
				
				String cuil = ParamUtil.getString(renderRequest,"cuil");
				precio =(AjustePlanSuperador)  session.getAttribute(WebKeysTesoreria.AJUSTE_EN_SESSION);
				
				
				Collections.sort(precio.getAfiliados(), new Comparator<Object>() {
					public int compare(Object o1, Object o2) {
						return ((Comparable<String>) ((Afiliado) (o1)).getCuil_titular())
								.compareTo(((Afiliado) (o2)).getCuil_titular());
					}
				});
				
				List<Afiliado>newValores =new ArrayList<Afiliado>();
				for(Afiliado p:precio.getAfiliados()) {
					if(!p.getCuil_titular().equals(cuil)) {
						newValores.add(p);
					}
				}
				
				
				precio.setAfiliados(newValores);
				
				session.setAttribute(WebKeysTesoreria.AJUSTE_EN_EDICION , precio);
				return mapping
        				.findForward("portlet.tesoreria.facturacion.listaajustes.cuiles");
			}
			
			
			if(cmd.equals(Constants.UPDATE) ){
				precio = (AjustePlanSuperador) session.getAttribute(WebKeysTesoreria.AJUSTE_EN_SESSION);
				actualizaAjuste(precio,renderRequest);
				if(idPrecio == 0){
					idPrecio=LiquidacionPlanesSuperadoresServiceUtil.addAjustePlanSuperador(precio, user.getScreenName());
					 SessionMessages.add(renderRequest, "insertCabOk");
					 msg = "Se inserto el precio plan superador nro ";
					  msg = msg +" " +idPrecio;
					 SessionMessages.add(renderRequest, "insertCabOk");
					  renderRequest.setAttribute("msgCabOk", msg);
					  _log.debug("Usuario: " + user.getScreenName() 
							+ " cmd: " + cmd 
							+ " id precio: " + idPrecio
							);
				}else if(idPrecio!=0){
					precio.setId(idPrecio.intValue());
					idPrecio=LiquidacionPlanesSuperadoresServiceUtil.updateAjustePlanSuperador(precio, user.getScreenName());
					msg = "Se modificó el precio plan superador nro ";
					msg = msg +" " +idPrecio;
					
					SessionMessages.add(renderRequest, "updateCabOk");
					renderRequest.setAttribute("msgCabOk", msg);
					 _log.debug("Usuario: " + user.getScreenName() 
						+ " cmd: " + cmd 
						+ " id precio: " + idPrecio
						);
				}
				
				session.setAttribute(WebKeysTesoreria.AJUSTE_EN_SESSION, precio);
				return mapping
		 				.findForward("portlet.tesoreria.facturacion.listaajustes.edit");
		   }
			
           if(cmd.equals(Constants.DELETE) ){ 
            	
            	try {           		
                   LiquidacionPlanesSuperadoresServiceUtil.deletePlanSuperadorAjuste(idPrecio.intValue());
	            } catch (Exception e) {
	                _log.error("Error eliminando precio");
	            }
            	
            	List<AjustePlanSuperador>ln=   (List<AjustePlanSuperador>) session.getAttribute(WebKeysTesoreria.AJUSTES_RESULT);
            	List<AjustePlanSuperador>lista=new ArrayList<AjustePlanSuperador>();
	           	for(AjustePlanSuperador n:ln){
	            	if(!n.getId().equals(idPrecio.intValue() )){
	            	   lista.add(n);
	            	}
	            }
	           	
	            session.setAttribute(WebKeysTesoreria.AJUSTES_RESULT,lista);
	            return mapping
	        				.findForward("portlet.tesoreria.facturacion.listaajustes.result");
			}
           
            if(cmd.equals("atras") ){ 
            	return mapping
        				.findForward("portlet.tesoreria.facturacion.listaajustes");
            }
            
            
            /*
             if(cmd.equals("generarPrecios") ){
				
				String fechaDdeDia = ParamUtil.getString(renderRequest,"fechadesdedia");
				String fechaDdeMes = ParamUtil.getString(renderRequest,"fechadesdemes");
				String fechaDdeAnio = ParamUtil.getString(renderRequest,"fechadesdeanio");
				
				String fechaHtaDia = ParamUtil.getString(renderRequest,"fechahastadia");
				String fechaHtaMes = ParamUtil.getString(renderRequest,"fechahastames");
				String fechaHtaAnio = ParamUtil.getString(renderRequest,"fechahastaanio");
				
				Double porcentaje = ParamUtil.getDouble(renderRequest,"porcentaje");
				
				Date fechaDde = null;
				try {
					fechaDde = formatoDeFechas.parse(fechaDdeDia + "/"
							+ (Integer.parseInt(fechaDdeMes) + 1) + "/"
							+ fechaDdeAnio);
				} catch (Exception e) {
					fechaDde = null;
				}
				
				
				Date fechaHta = null;
				try {
					fechaHta = formatoDeFechas.parse(fechaHtaDia + "/"
							+ (Integer.parseInt(fechaHtaMes) + 1) + "/"
							+ fechaHtaAnio);
				} catch (Exception e) {
					fechaHta = null;
				}
				
				String ids = ParamUtil.getString(renderRequest, "ids");
				Calendar calendar = Calendar.getInstance();
             	calendar.setTime(fechaDde);
				calendar.add(Calendar.DAY_OF_YEAR, -1); 
				List<PrecioPlanSuperador>precios=(List<PrecioPlanSuperador>) session.getAttribute(WebKeysTesoreria.PRECIOS_RESULT );
				List<PrecioPlanSuperador>preciosNews = new ArrayList<PrecioPlanSuperador>();
				Boolean inconsistencia=false;
				List<String> mensaje= new ArrayList<String>();
				for(PrecioPlanSuperador xd: precios) {
					if (ids.contains(xd.getId().toString().trim())) {
						xd.setFechaHasta(calendar.getTime());
						PrecioPlanSuperador nuevo =LiquidacionPlanesSuperadoresServiceUtil.getPlanSuperador(xd.getId());
						nuevo.setFechaDesde(fechaDde);
						nuevo.setFechaHasta(fechaHta);
						String importesStr="";
						for(Producto valor : nuevo.getValores()) {
							BigDecimal newValor = new BigDecimal(valor.getPrecioUnitario().doubleValue() * (1+porcentaje/100))  ;
							valor.setPrecioUnitario(newValor);
							if(importesStr.length()>0) importesStr+=";";
							importesStr+= String.valueOf(valor.getId())+" -- " + df.format(newValor.setScale(2, RoundingMode.HALF_UP));
						}
						nuevo.setValoresString(importesStr);
						mensaje = LiquidacionPlanesSuperadoresServiceUtil.verificarPrecioPlanSuperador(nuevo);
						
						if(!mensaje.isEmpty()) {
						    inconsistencia = true;
							break;
						}
						preciosNews.add(nuevo);
					}
				}
				if(!inconsistencia) {
				   for(PrecioPlanSuperador xd: precios) {
						   idPrecio=LiquidacionPlanesSuperadoresServiceUtil.updateVigenciaPrecioPlanSuperador(xd, user.getScreenName());
				   }	
				   for(PrecioPlanSuperador xd: preciosNews) {
					   idPrecio=LiquidacionPlanesSuperadoresServiceUtil.addPrecioPlanSuperador(xd, user.getScreenName());
					   xd.setId(idPrecio.intValue());
				   }
				   precios.addAll(preciosNews);
				   session.setAttribute(WebKeysTesoreria.PRECIOS_RESULT , precios);
				}else {
				  renderRequest.setAttribute("errores", mensaje);
				}
				return mapping
        				.findForward("portlet.tesoreria.facturacion.listaprecios.result");
			}
			*/
		}
		session.setAttribute(WebKeysTesoreria.AJUSTE_EN_SESSION, precio);
		
		return mapping
 				.findForward("portlet.tesoreria.facturacion.listaajustes");
	}
	
	private void actualizaAjuste(AjustePlanSuperador precio,RenderRequest renderRequest){
		String descripcion = ParamUtil.getString(renderRequest, "descripcion", null);
		String fechaDdeDia = ParamUtil.getString(renderRequest,"fechaDesdeDia");
		String fechaDdeMes = ParamUtil.getString(renderRequest,"fechaDesdeMes");
		String fechaDdeAnio = ParamUtil.getString(renderRequest,"fechaDesdeAnio");
		
		String fechaHtaDia = ParamUtil.getString(renderRequest,"fechaHastaDia");
		String fechaHtaMes = ParamUtil.getString(renderRequest,"fechaHastaMes");
		String fechaHtaAnio = ParamUtil.getString(renderRequest,"fechaHastaAnio");
		
		
		Integer edadDde = ParamUtil.getInteger(renderRequest,"edadDde", 0);
		Integer edadHta = ParamUtil.getInteger(renderRequest,"edadHta", 0);
		
		
		Date fechaDde = null;
		try {
			fechaDde = formatoDeFechas.parse(fechaDdeDia + "/"
					+ (Integer.parseInt(fechaDdeMes) + 1) + "/"
					+ fechaDdeAnio);
		} catch (Exception e) {
			fechaDde = null;
		}
		
		Date fechaHta = null;
		try {
			fechaHta = formatoDeFechas.parse(fechaHtaDia + "/"
					+ (Integer.parseInt(fechaHtaMes) + 1) + "/"
					+ fechaHtaAnio);
		} catch (Exception e) {
			fechaHta = null;
		}
		
		Boolean usoPersonalizado = ParamUtil.getBoolean(renderRequest,"usoPersonalizado");
		
		Double porcentaje = ParamUtil.getDouble(renderRequest,"porcentaje", 0);
		Double importe = ParamUtil.getDouble(renderRequest,"importe", 0);
		
		precio.setDescripcion(descripcion);
		precio.setFechaDesde(fechaDde);
		precio.setFechaHasta(fechaHta);
		precio.setEdadDesde(edadDde);
		precio.setEdadHasta(edadHta);
		precio.setPorcentaje(porcentaje);
		precio.setImporte(new BigDecimal(importe));
		precio.setSoloUsoPersonalizado(usoPersonalizado);
	}
	
	
}
