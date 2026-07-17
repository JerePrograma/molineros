package ar.com.ospim.afiliados.action;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

import ar.com.ospim.afiliados.WebKeysAfiliados;
import ar.com.ospim.afiliados.beans.Afiliado;
import ar.com.ospim.afiliados.services.SolicitudAfiliacionServiceUtil;
import ar.com.ospim.tesoreria.WebKeysTesoreria;
import ar.com.ospim.tesoreria.beans.AjustePlanSuperador;
import ar.com.ospim.tesoreria.beans.PrecioPlanSuperador;
import ar.com.ospim.util.PermissionUtil;

public class SolicitudAfiliacionAction extends PortletAction {

	private Logger _log = Logger.getLogger(this.getClass());
	SimpleDateFormat formatoDeFechas = new SimpleDateFormat("dd/MM/yyyy");
	
	public ActionForward render(
			ActionMapping mapping, 
			ActionForm form,
			PortletConfig portletConfig, 
			RenderRequest renderRequest,
			RenderResponse renderResponse
		) throws Exception {
		
		String cmd = ParamUtil.getString(renderRequest, "cmd", null);

	    String tabs1 = ParamUtil.getString(renderRequest, "tabs1", "");
	    
	    if ("seguimiento-formulario".equals(tabs1)) {
	
		     if ("buscarSeguimiento".equals(cmd)) {
		         return buscarSeguimiento(mapping, renderRequest);
		     }
	
		     if ("verFormulario".equals(cmd)) {
		         return verFormulario(mapping, renderRequest);
		     }
		     if ("cotizarFormulario".equals(cmd)) {
		         return cotizarFormulario(mapping, renderRequest);
		     }
		     
		     if ("seleccionarAjuste".equals(cmd)) {
		    	 return seleccionarAjuste(mapping, renderRequest);
		     }
		     
		     if ("agregarAjuste".equals(cmd)) {
		    	 return agregarAjuste(mapping, renderRequest);
		     }
		     
		     if ("sacarAjuste".equals(cmd)) {
		    	 return sacarAjuste(mapping, renderRequest);
		     }
		     
		     if ("grabarCotizar".equals(cmd)) {
		         return grabarCotizacion(mapping, renderRequest);
		     }
		     return mapping.findForward("portlet.afiliados.seguimiento.form");
		 }	    
		
	    return mapping.findForward("portlet.afiliados.seguimiento.form");
	}	

	@Override
	public void processAction(
			ActionMapping mapping, 
			ActionForm form, 
			PortletConfig portletConfig,
	        ActionRequest actionRequest, 
	        ActionResponse actionResponse
	) throws Exception {

	    String cmd = ParamUtil.getString(actionRequest, "cmd", "");
	    String volverDetalle = ParamUtil.getString(actionRequest, "volverDetalle", "");

	    if ("guardarNotaSeguimiento".equals(cmd)) {
	        try {
	        	guardarNotaSeguimientoAction(actionRequest);
	            SessionMessages.add(actionRequest, "seguimiento_guardado_ok");
	        } catch (Exception e) {
	            _log.error("Error guardando seguimiento", e);
	            SessionErrors.add(actionRequest, "seguimiento_guardado_error");
	        }

	        String redirect = ParamUtil.getString(actionRequest, "redirect");
	        if (Validator.isNotNull(redirect)) {
	            actionResponse.sendRedirect(redirect);
	            return;
	        }
	        
	        actionResponse.setRenderParameter("tabs1", "seguimiento-formulario");
	        
	        if ("1".equals(volverDetalle)) {
	            actionResponse.setRenderParameter("cmd", "verFormulario");
	            actionResponse.setRenderParameter("id", String.valueOf(ParamUtil.getLong(actionRequest, "id")));
	            actionResponse.setRenderParameter("modo", ParamUtil.getString(actionRequest, "modo", "editar"));
	            
	            actionResponse.setRenderParameter("segDesdeDia",  ParamUtil.getString(actionRequest, "segDesdeDia"));
	            actionResponse.setRenderParameter("segDesdeMes",  ParamUtil.getString(actionRequest, "segDesdeMes"));
	            actionResponse.setRenderParameter("segDesdeAnio", ParamUtil.getString(actionRequest, "segDesdeAnio"));

	            actionResponse.setRenderParameter("segHastaDia",  ParamUtil.getString(actionRequest, "segHastaDia"));
	            actionResponse.setRenderParameter("segHastaMes",  ParamUtil.getString(actionRequest, "segHastaMes"));
	            actionResponse.setRenderParameter("segHastaAnio", ParamUtil.getString(actionRequest, "segHastaAnio"));

	            actionResponse.setRenderParameter("nombre",    ParamUtil.getString(actionRequest, "nombre"));
	            actionResponse.setRenderParameter("dni",       ParamUtil.getString(actionRequest, "dni"));
	            actionResponse.setRenderParameter("estado",    ParamUtil.getString(actionRequest, "estado"));
	            actionResponse.setRenderParameter("provincia", ParamUtil.getString(actionRequest, "provincia"));
	            
	            actionResponse.setRenderParameter("molinero", ParamUtil.getString(actionRequest, "molinero"));
	            
	            actionResponse.setRenderParameter("ddjj", ParamUtil.getString(actionRequest, "ddjj"));
	            actionResponse.setRenderParameter("vendedor", ParamUtil.getString(actionRequest, "vendedor"));
	            return;
	        }
	        
	        actionResponse.setRenderParameter("cmd", "buscarSeguimiento");
	        
	        actionResponse.setRenderParameter("segDesdeDia",  ParamUtil.getString(actionRequest, "segDesdeDia"));
	        actionResponse.setRenderParameter("segDesdeMes",  ParamUtil.getString(actionRequest, "segDesdeMes"));
	        actionResponse.setRenderParameter("segDesdeAnio", ParamUtil.getString(actionRequest, "segDesdeAnio"));

	        actionResponse.setRenderParameter("segHastaDia",  ParamUtil.getString(actionRequest, "segHastaDia"));
	        actionResponse.setRenderParameter("segHastaMes",  ParamUtil.getString(actionRequest, "segHastaMes"));
	        actionResponse.setRenderParameter("segHastaAnio", ParamUtil.getString(actionRequest, "segHastaAnio"));

	        actionResponse.setRenderParameter("nombre",    ParamUtil.getString(actionRequest, "nombre"));
	        actionResponse.setRenderParameter("dni",       ParamUtil.getString(actionRequest, "dni"));
	        actionResponse.setRenderParameter("estado",    ParamUtil.getString(actionRequest, "estado"));
	        actionResponse.setRenderParameter("provincia", ParamUtil.getString(actionRequest, "provincia"));

	        actionResponse.setRenderParameter("molinero", ParamUtil.getString(actionRequest, "molinero"));
	        
	        actionResponse.setRenderParameter("ddjj", ParamUtil.getString(actionRequest, "ddjj"));
	        actionResponse.setRenderParameter("vendedor", ParamUtil.getString(actionRequest, "vendedor"));
	        
	        return;
	    }

	    if ("derivarSolicitud".equals(cmd)) {
	        try {
	            derivarSolicitudAction(actionRequest);
	            SessionMessages.add(actionRequest, "solicitud_derivada_ok");
	        } catch (Exception e) {
	            _log.error("Error derivando solicitud", e);
	            SessionErrors.add(actionRequest, "solicitud_derivada_error");
	        }

	        String redirect = ParamUtil.getString(actionRequest, "redirect");
	        if (Validator.isNotNull(redirect)) {
	            actionResponse.sendRedirect(redirect);
	            return;
	        }

	        actionResponse.setRenderParameter("tabs1", "seguimiento-formulario");
	        actionResponse.setRenderParameter("cmd", "buscarSeguimiento");
	        return;
	    }

	    if ("desasignarSolicitud".equals(cmd)) {
	    	
	        try {
	            desasignarSolicitudAction(actionRequest);
	            SessionMessages.add(actionRequest, "solicitud_desasignada_ok");
	        } catch (Exception e) {
	            _log.error("Error desasignando solicitud", e);
	            SessionErrors.add(actionRequest, "solicitud_desasignada_error");
	        }

	        String redirect = ParamUtil.getString(actionRequest, "redirect");
	        if (Validator.isNotNull(redirect)) {
	            actionResponse.sendRedirect(redirect);
	            return;
	        }

	        actionResponse.setRenderParameter("tabs1", "seguimiento-formulario");
	        actionResponse.setRenderParameter("cmd", "buscarSeguimiento");
	        return;
	    }	   	    
	    
	    if ("guardarFormularioAfiliado".equals(cmd)) {
	        try {
	            guardarFormularioAfiliadoAction(actionRequest);
	            SessionMessages.add(actionRequest, "seguimiento_guardado_ok");
	        } catch (Exception e) {
	            _log.error("Error guardando formulario afiliado", e);
	            SessionErrors.add(actionRequest, "seguimiento_guardado_error");
	        }

	        String redirect = ParamUtil.getString(actionRequest, "redirect");
	        if (Validator.isNotNull(redirect)) {
	            actionResponse.sendRedirect(redirect);
	            return;
	        }

	        actionResponse.setRenderParameter("tabs1", "seguimiento-formulario");
	        actionResponse.setRenderParameter("cmd", "verFormulario");
	        actionResponse.setRenderParameter("id", String.valueOf(ParamUtil.getLong(actionRequest, "id")));
	        actionResponse.setRenderParameter("modo", ParamUtil.getString(actionRequest, "modo", "editar"));

	        actionResponse.setRenderParameter("segDesdeDia", ParamUtil.getString(actionRequest, "segDesdeDia"));
	        actionResponse.setRenderParameter("segDesdeMes", ParamUtil.getString(actionRequest, "segDesdeMes"));
	        actionResponse.setRenderParameter("segDesdeAnio", ParamUtil.getString(actionRequest, "segDesdeAnio"));
	        actionResponse.setRenderParameter("segHastaDia", ParamUtil.getString(actionRequest, "segHastaDia"));
	        actionResponse.setRenderParameter("segHastaMes", ParamUtil.getString(actionRequest, "segHastaMes"));
	        actionResponse.setRenderParameter("segHastaAnio", ParamUtil.getString(actionRequest, "segHastaAnio"));

	        actionResponse.setRenderParameter("nombre", ParamUtil.getString(actionRequest, "filtroNombre"));
	        actionResponse.setRenderParameter("dni", ParamUtil.getString(actionRequest, "filtroDni"));
	        actionResponse.setRenderParameter("estado", ParamUtil.getString(actionRequest, "filtroEstado"));
	        actionResponse.setRenderParameter("provincia", ParamUtil.getString(actionRequest, "filtroProvincia"));
	        actionResponse.setRenderParameter("molinero", ParamUtil.getString(actionRequest, "filtroMolinero"));
	        return;
	    }
	    
	    if ("generarLinkDdjjSolicitud".equals(cmd)) {
	        try {
	            generarLinkDdjjSolicitudAction(actionRequest);
	            SessionMessages.add(actionRequest, "seguimiento_guardado_ok");
	        } catch (Exception e) {
	            _log.error("Error generando link DDJJ", e);
	            SessionErrors.add(actionRequest, "seguimiento_guardado_error");
	            SessionErrors.add(actionRequest, "seguimiento_guardado_error_detalle", e.getMessage());
	        }

	        String redirect = ParamUtil.getString(actionRequest, "redirect");
	        if (Validator.isNotNull(redirect)) {
	            actionResponse.sendRedirect(redirect);
	            return;
	        }

	        actionResponse.setRenderParameter("tabs1", "seguimiento-formulario");
	        actionResponse.setRenderParameter("cmd", "verFormulario");
	        actionResponse.setRenderParameter("id", String.valueOf(ParamUtil.getLong(actionRequest, "id")));
	        actionResponse.setRenderParameter("modo", ParamUtil.getString(actionRequest, "modo", "editar"));

	        actionResponse.setRenderParameter("segDesdeDia", ParamUtil.getString(actionRequest, "segDesdeDia"));
	        actionResponse.setRenderParameter("segDesdeMes", ParamUtil.getString(actionRequest, "segDesdeMes"));
	        actionResponse.setRenderParameter("segDesdeAnio", ParamUtil.getString(actionRequest, "segDesdeAnio"));
	        actionResponse.setRenderParameter("segHastaDia", ParamUtil.getString(actionRequest, "segHastaDia"));
	        actionResponse.setRenderParameter("segHastaMes", ParamUtil.getString(actionRequest, "segHastaMes"));
	        actionResponse.setRenderParameter("segHastaAnio", ParamUtil.getString(actionRequest, "segHastaAnio"));

	        actionResponse.setRenderParameter("nombre", ParamUtil.getString(actionRequest, "filtroNombre"));
	        actionResponse.setRenderParameter("dni", ParamUtil.getString(actionRequest, "filtroDni"));
	        actionResponse.setRenderParameter("estado", ParamUtil.getString(actionRequest, "filtroEstado"));
	        actionResponse.setRenderParameter("provincia", ParamUtil.getString(actionRequest, "filtroProvincia"));
	        actionResponse.setRenderParameter("molinero", ParamUtil.getString(actionRequest, "filtroMolinero"));
	        return;
	    }
	    
        if ("cotizarFormulario".equals(cmd)  ||
        		"seleccionarAjuste".equals(cmd) || "agregarAjuste".equals(cmd) || "sacarAjuste".equals(cmd)) {
        	actionResponse.setRenderParameter("tabs1", "seguimiento-formulario");
	        actionResponse.setRenderParameter("cmd", "verFormulario");
	        actionResponse.setRenderParameter("id", String.valueOf(ParamUtil.getLong(actionRequest, "id")));
	        actionResponse.setRenderParameter("modo", ParamUtil.getString(actionRequest, "modo", "editar"));

	        actionResponse.setRenderParameter("segDesdeDia", ParamUtil.getString(actionRequest, "segDesdeDia"));
	        actionResponse.setRenderParameter("segDesdeMes", ParamUtil.getString(actionRequest, "segDesdeMes"));
	        actionResponse.setRenderParameter("segDesdeAnio", ParamUtil.getString(actionRequest, "segDesdeAnio"));
	        actionResponse.setRenderParameter("segHastaDia", ParamUtil.getString(actionRequest, "segHastaDia"));
	        actionResponse.setRenderParameter("segHastaMes", ParamUtil.getString(actionRequest, "segHastaMes"));
	        actionResponse.setRenderParameter("segHastaAnio", ParamUtil.getString(actionRequest, "segHastaAnio"));

	        actionResponse.setRenderParameter("nombre", ParamUtil.getString(actionRequest, "filtroNombre"));
	        actionResponse.setRenderParameter("dni", ParamUtil.getString(actionRequest, "filtroDni"));
	        actionResponse.setRenderParameter("estado", ParamUtil.getString(actionRequest, "filtroEstado"));
	        actionResponse.setRenderParameter("provincia", ParamUtil.getString(actionRequest, "filtroProvincia"));
	        actionResponse.setRenderParameter("molinero", ParamUtil.getString(actionRequest, "filtroMolinero"));
	        return;
	    }
	    	    
	    super.processAction(mapping, form, portletConfig, actionRequest, actionResponse);
	}

	private void guardarNotaSeguimientoAction(ActionRequest request) throws Exception {
	    long id = ParamUtil.getLong(request, "id");
	    String estado = ParamUtil.getString(request, "estadoNuevo", "pendiente");
	    String nota = ParamUtil.getString(request, "nota", "");

	    if (id <= 0) {
	        throw new RuntimeException("Solicitud inválida.");
	    }

	    if (Validator.isNull(nota) || nota.trim().isEmpty()) {
	        return;
	    }

	    User u = PortalUtil.getUser(request);
	    String usuario = (u != null ? u.getScreenName() : "");

	    SolicitudAfiliacionServiceUtil.guardarSeguimientoSolicitud(id, estado, nota, usuario);
	}

	private ActionForward buscarSeguimiento(ActionMapping mapping, RenderRequest renderRequest) {
	    try {
	        String segDesdeDia  = ParamUtil.getString(renderRequest, "segDesdeDia", "");
	        String segDesdeMes  = ParamUtil.getString(renderRequest, "segDesdeMes", "");
	        String segDesdeAnio = ParamUtil.getString(renderRequest, "segDesdeAnio", "");

	        String segHastaDia  = ParamUtil.getString(renderRequest, "segHastaDia", "");
	        String segHastaMes  = ParamUtil.getString(renderRequest, "segHastaMes", "");
	        String segHastaAnio = ParamUtil.getString(renderRequest, "segHastaAnio", "");

	        String desde = "";
	        String hasta = "";

	        SimpleDateFormat sdfIn  = new SimpleDateFormat("dd/MM/yyyy");
	        SimpleDateFormat sdfOut = new SimpleDateFormat("yyyy-MM-dd");

	        if (!segDesdeDia.isEmpty() && !segDesdeMes.isEmpty() && !segDesdeAnio.isEmpty()) {
	            Date d = sdfIn.parse(segDesdeDia + "/" + (Integer.parseInt(segDesdeMes) + 1) + "/" + segDesdeAnio);
	            desde = sdfOut.format(d);
	        }

	        if (!segHastaDia.isEmpty() && !segHastaMes.isEmpty() && !segHastaAnio.isEmpty()) {
	            Date h = sdfIn.parse(segHastaDia + "/" + (Integer.parseInt(segHastaMes) + 1) + "/" + segHastaAnio);
	            hasta = sdfOut.format(h);
	        }

	        String nombre = ParamUtil.getString(renderRequest, "nombre", "");
	        String dni = ParamUtil.getString(renderRequest, "dni", "");
	        String estado = ParamUtil.getString(renderRequest, "estado", "");
	        String provincia = ParamUtil.getString(renderRequest, "provincia", "");
	        String molinero = ParamUtil.getString(renderRequest, "molinero", "").trim();
	        String ddjj = ParamUtil.getString(renderRequest, "ddjj", "").trim();
	        String vendedor = ParamUtil.getString(renderRequest, "vendedor", "").trim();

	        String molineroForzado = resolverFiltroMolineroPorRol(renderRequest);

	        if ("__SIN_PERMISO__".equals(molineroForzado)) {
	            renderRequest.setAttribute("resultados", new ArrayList<Map<String,Object>>());
	            renderRequest.setAttribute("vendedores", SolicitudAfiliacionServiceUtil.getTodosLosVendedores());
	            renderRequest.setAttribute("vendedoresActivos", SolicitudAfiliacionServiceUtil.getVendedoresActivos());
	            renderRequest.setAttribute("molineroForzado", molineroForzado);
	            renderRequest.setAttribute("mensaje", "No tiene permisos para visualizar solicitudes.");
	            renderRequest.setAttribute("puedeElegirVendedor", Boolean.FALSE);

	            if (renderRequest.getWindowState().equals(LiferayWindowState.EXCLUSIVE)) {
	                return mapping.findForward("portlet.afiliados.seguimiento.list");
	            }

	            return mapping.findForward("portlet.afiliados.seguimiento.form");
	        }

	        if (Validator.isNotNull(molineroForzado)) {
	            molinero = molineroForzado;
	        }

	        User user = PortalUtil.getUser(renderRequest);
	        
	        boolean veTodos = (user != null) &&
	            PermissionUtil.userContainsRole(user, WebKeysAfiliados.COMERCIAL_ADMINISTRADOR);

	        
	        boolean veMolineros = (user != null) &&
	        	    PermissionUtil.userContainsRole(user, WebKeysAfiliados.COMERCIAL_SEGUIMIENTO_MOLINEROS);

	        boolean veNoMolineros = (user != null) &&
	        	    PermissionUtil.userContainsRole(user, WebKeysAfiliados.COMERCIAL_SEGUIMIENTO_NO_MOLINEROS);

	        boolean veConsulta = (user != null) &&
		    	    PermissionUtil.userContainsRole(user, WebKeysAfiliados.COMERCIAL_CONSULTA);
	        
	        if (!veTodos  && !veConsulta) {
	            if (user == null) {
	                renderRequest.setAttribute("resultados", new ArrayList<Map<String,Object>>());
	                renderRequest.setAttribute("mensaje", "No se pudo identificar el usuario logueado.");
	                renderRequest.setAttribute("puedeElegirVendedor", Boolean.FALSE);

	                if (renderRequest.getWindowState().equals(LiferayWindowState.EXCLUSIVE)) {
	                    return mapping.findForward("portlet.afiliados.seguimiento.list");
	                }

	                return mapping.findForward("portlet.afiliados.seguimiento.form");
	            }

	            Long idVendedorLogueado =
	            	    SolicitudAfiliacionServiceUtil.getIdVendedorByEmail(user.getEmailAddress());

	            if (idVendedorLogueado == null || idVendedorLogueado.longValue() <= 0) {
	                renderRequest.setAttribute("resultados", new ArrayList<Map<String,Object>>());
	                renderRequest.setAttribute("mensaje", "El usuario no tiene vendedor asociado.");
	                renderRequest.setAttribute("puedeElegirVendedor", Boolean.FALSE);

	                if (renderRequest.getWindowState().equals(LiferayWindowState.EXCLUSIVE)) {
	                    return mapping.findForward("portlet.afiliados.seguimiento.list");
	                }

	                return mapping.findForward("portlet.afiliados.seguimiento.form");
	            }

	            vendedor = String.valueOf(idVendedorLogueado);
	        }

	        List<Map<String,Object>> resultados =
	            SolicitudAfiliacionServiceUtil.buscarSolicitudesComercial(
	                desde, hasta, nombre, dni, estado, provincia, molinero, ddjj, vendedor
	            );

	        renderRequest.setAttribute("resultados", resultados);
	        renderRequest.setAttribute("vendedores", SolicitudAfiliacionServiceUtil.getTodosLosVendedores());
	        renderRequest.setAttribute("vendedoresActivos", SolicitudAfiliacionServiceUtil.getVendedoresActivos());
	        renderRequest.setAttribute("molineroForzado", molineroForzado);
	        renderRequest.setAttribute("puedeElegirVendedor", Boolean.valueOf(veTodos));
	        renderRequest.setAttribute("vendedorForzado", vendedor);

	        if (renderRequest.getWindowState().equals(LiferayWindowState.EXCLUSIVE)) {
	            return mapping.findForward("portlet.afiliados.seguimiento.list");
	        }

	        return mapping.findForward("portlet.afiliados.seguimiento.form");

	    } catch (Exception e) {
	        _log.error("Error buscando seguimiento", e);
	        renderRequest.setAttribute("mensaje", "Error buscando datos.");
	        return mapping.findForward("portlet.afiliados.seguimiento.form");
	    }
	}
	
	private ActionForward verFormulario(ActionMapping mapping, RenderRequest renderRequest) {
	    try {
	        long id = ParamUtil.getLong(renderRequest, "id");
	        String modo = ParamUtil.getString(renderRequest, "modo", "ver");

	        Map<String,Object> formulario = SolicitudAfiliacionServiceUtil.getSolicitudById(id);
	        List<Map<String,Object>> historial = SolicitudAfiliacionServiceUtil.getHistorialBySolicitudId(id);

	        if (formulario == null) {
	            renderRequest.setAttribute("mensaje", "No se encontró la solicitud.");
	            return mapping.findForward("portlet.afiliados.seguimiento.form");
	        }

	        User user = PortalUtil.getUser(renderRequest);
	        boolean veTodos = (user != null) &&
	            PermissionUtil.userContainsRole(user, WebKeysAfiliados.COMERCIAL_ADMINISTRADOR);

	        boolean veConsulta = (user != null) &&
	        	    PermissionUtil.userContainsRole(user, WebKeysAfiliados.COMERCIAL_CONSULTA);
	        
	        if (!veTodos  && !veConsulta) {
	            if (user == null) {
	                renderRequest.setAttribute("mensaje", "No se pudo identificar el usuario logueado.");
	                return mapping.findForward("portlet.afiliados.seguimiento.form");
	            }

	            Long idVendedorLogueado =
	                SolicitudAfiliacionServiceUtil.getIdVendedorByEmail(user.getEmailAddress());

	            if (idVendedorLogueado == null || idVendedorLogueado.longValue() <= 0) {
	                renderRequest.setAttribute("mensaje", "El usuario no tiene vendedor asociado.");
	                return mapping.findForward("portlet.afiliados.seguimiento.form");
	            }

	            Object idVendedorSolicitudObj = formulario.get("id_vendedor");
	            Long idVendedorSolicitud = null;

	            if (idVendedorSolicitudObj != null) {
	                idVendedorSolicitud = Long.valueOf(String.valueOf(idVendedorSolicitudObj));
	            }

	            if (idVendedorSolicitud == null || !idVendedorLogueado.equals(idVendedorSolicitud)) {
	                renderRequest.setAttribute("mensaje", "No tiene permisos para visualizar esta solicitud.");
	                return mapping.findForward("portlet.afiliados.seguimiento.form");
	            }
	        }

	        renderRequest.setAttribute("formulario", formulario);
	        renderRequest.setAttribute("historial", historial);
	        renderRequest.setAttribute("modo", modo);

	        return mapping.findForward("portlet.afiliados.formulario.detalle");

	    } catch (Exception e) {
	        _log.error("Error verFormulario", e);
	        renderRequest.setAttribute("mensaje", "Error cargando formulario.");
	        return mapping.findForward("portlet.afiliados.seguimiento.form");
	    }
	}
	
	private String resolverFiltroMolineroPorRol(RenderRequest request) throws Exception {
	    User user = PortalUtil.getUser(request);
	    if (user == null) return "__SIN_PERMISO__";

	    boolean veTodos = PermissionUtil.userContainsRole(
	        user, WebKeysAfiliados.COMERCIAL_ADMINISTRADOR);

	    boolean veMolineros = PermissionUtil.userContainsRole(
	        user, WebKeysAfiliados.COMERCIAL_SEGUIMIENTO_MOLINEROS);

	    boolean veNoMolineros = PermissionUtil.userContainsRole(
	        user, WebKeysAfiliados.COMERCIAL_SEGUIMIENTO_NO_MOLINEROS);

	    boolean veConsulta = (user != null) &&
	    	    PermissionUtil.userContainsRole(user, WebKeysAfiliados.COMERCIAL_CONSULTA);
	    
	    if (veTodos) return "";
	    if (veConsulta) return "";
	    if (veMolineros && veNoMolineros) return "";
	    if (veMolineros) return "si";
	    if (veNoMolineros) return "no";

	    return "__SIN_PERMISO__";
	    //return "";
	}
	
	private void derivarSolicitudAction(ActionRequest request) throws Exception {
	    long idSolicitud = ParamUtil.getLong(request, "idSolicitud");
	    long idVendedorDestino = ParamUtil.getLong(request, "idVendedorDestino");
	    String nota = ParamUtil.getString(request, "notaDerivacion", "");

	    User u = PortalUtil.getUser(request);
	    String usr = (u != null ? u.getScreenName() : null);

	    if (idSolicitud <= 0) {
	        throw new RuntimeException("Solicitud inválida.");
	    }
	    if (idVendedorDestino <= 0) {
	        throw new RuntimeException("Vendedor destino inválido.");
	    }
	    if (Validator.isNull(usr)) {
	        throw new RuntimeException("No se pudo identificar el usuario.");
	    }

	    SolicitudAfiliacionServiceUtil.derivarSolicitud(idSolicitud, idVendedorDestino, usr, nota);
	}

	private void desasignarSolicitudAction(ActionRequest request) throws Exception {
	    long idSolicitud = ParamUtil.getLong(request, "idSolicitud");
	    String nota = ParamUtil.getString(request, "notaDesasignacion", "");

	    User u = PortalUtil.getUser(request);
	    String usr = (u != null ? u.getScreenName() : null);

	    if (idSolicitud <= 0) {
	        throw new RuntimeException("Solicitud inválida.");
	    }
	    if (Validator.isNull(usr)) {
	        throw new RuntimeException("No se pudo identificar el usuario.");
	    }

	    SolicitudAfiliacionServiceUtil.desasignarSolicitud(idSolicitud, usr, nota);
	}
	
	private void guardarFormularioAfiliadoAction(ActionRequest request) throws Exception {
	    long idSolicitud = ParamUtil.getLong(request, "id");

	    User u = PortalUtil.getUser(request);
	    String usuario = (u != null ? u.getScreenName() : "");
	    
	    String nombre = ParamUtil.getString(request, "nombre", "").trim();
	    String apellido = ParamUtil.getString(request, "apellido", "").trim();
	    String dni = ParamUtil.getString(request, "dni_form", "").trim();
	    String email = ParamUtil.getString(request, "email", "").trim();
	    String codigoArea = ParamUtil.getString(request, "codigo_area", "").trim();
	    String telefono = ParamUtil.getString(request, "telefono", "").trim();
	    String provincia = ParamUtil.getString(request, "provincia", "").trim();
	    String plan = ParamUtil.getString(request, "plan", "").trim();

	    BigDecimal sueldoBruto = null;
	    String sueldoTxt = ParamUtil.getString(request, "sueldoBruto", "").trim();
	    if (Validator.isNotNull(sueldoTxt)) {
	        sueldoBruto = new BigDecimal(sueldoTxt.replace(",", "."));
	    }

	    Boolean relacionDependencia = Boolean.valueOf(
	        ParamUtil.getString(request, "relacionDependencia", "false")
	    );
	    Boolean tienePareja = Boolean.valueOf(
	        ParamUtil.getString(request, "tienePareja", "false")
	    );
	    Boolean tieneHijos = Boolean.valueOf(
	        ParamUtil.getString(request, "tieneHijos", "false")
	    );
	    Boolean esMolinero = Boolean.valueOf(
	        ParamUtil.getString(request, "esMolinero", "false")
	    );

	    String edadParejaTxt = ParamUtil.getString(request, "edad_pareja", "").trim();
	    Integer edadPareja = Validator.isNotNull(edadParejaTxt) ? Integer.valueOf(edadParejaTxt) : null;

	    String cantidadHijos21Txt = ParamUtil.getString(request, "cantidad_hijos21", "").trim();
	    String cantidadHijos25Txt = ParamUtil.getString(request, "cantidad_hijos25", "").trim();

	    Integer cantidadHijos21 = Validator.isNotNull(cantidadHijos21Txt)
	        ? Integer.valueOf(cantidadHijos21Txt)
	        : null;

	    Integer cantidadHijos25 = Validator.isNotNull(cantidadHijos25Txt)
	        ? Integer.valueOf(cantidadHijos25Txt)
	        : null;

	    // VALIDACIONES
	    if (Validator.isNull(nombre) || !nombre.matches("^[A-Za-zÁÉÍÓÚáéíóúÑñ ]+$")) {
	        throw new Exception("El nombre es obligatorio y solo puede contener letras.");
	    }

	    if (Validator.isNull(apellido) || !apellido.matches("^[A-Za-zÁÉÍÓÚáéíóúÑñ ]+$")) {
	        throw new Exception("El apellido es obligatorio y solo puede contener letras.");
	    }

	    if (!dni.matches("^\\d{7,8}$")) {
	        throw new Exception("El DNI debe tener 7 u 8 dígitos.");
	    }

	    if (Validator.isNull(email) || !email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
	        throw new Exception("El email no es válido.");
	    }

	    if (Validator.isNull(codigoArea) || Validator.isNull(telefono) || !(codigoArea + telefono).matches("^\\d{10}$")) {
	        throw new Exception("Código de área + teléfono deben sumar 10 dígitos.");
	    }

	    if (!"LUMA 200".equals(plan) && !"LUMA 400".equals(plan)) {
	        throw new Exception("El plan debe ser LUMA 200 o LUMA 400.");
	    }

	    if (Boolean.TRUE.equals(tienePareja)) {
	        if (edadPareja == null || edadPareja.intValue() <= 0) {
	            throw new Exception("Si indicó que tiene pareja, debe informar la edad de la pareja.");
	        }
	    } else {
	        edadPareja = null;
	    }

	    if (Boolean.TRUE.equals(tieneHijos)) {
	        boolean tieneAlguno =
	            (cantidadHijos21 != null && cantidadHijos21.intValue() > 0) ||
	            (cantidadHijos25 != null && cantidadHijos25.intValue() > 0);

	        if (!tieneAlguno) {
	            throw new Exception("Si indicó que tiene hijos, debe informar al menos una cantidad.");
	        }
	    } else {
	        cantidadHijos21 = null;
	        cantidadHijos25 = null;
	    }

	    SolicitudAfiliacionServiceUtil.actualizarFormularioAfiliado(
	        idSolicitud,
	        nombre,
	        apellido,
	        dni,
	        email,
	        codigoArea,
	        telefono,
	        provincia,
	        plan,
	        sueldoBruto,
	        relacionDependencia,
	        tienePareja,
	        edadPareja,
	        tieneHijos,
	        cantidadHijos21,
	        cantidadHijos25,
	        esMolinero,
	        usuario
	    );
	}
	
	private void generarLinkDdjjSolicitudAction(ActionRequest request) throws Exception {
	    long idSolicitud = ParamUtil.getLong(request, "id");
	    if (idSolicitud <= 0) {
	        throw new Exception("Solicitud inválida.");
	    }

	    //guardar primero los datos del afiliado
	    guardarFormularioAfiliadoAction(request);

	    //llamar al PHP que arma el borrador + crea la DDJJ
	    //String phpUrl = "http://localhost:8001/ospim/alta-online/api/ddjj_generar_desde_seguimiento.php";
	    //String phpUrl = "http://localhost:5173/luma-salud/backend/api/ddjj_generar_desde_seguimiento.php";
	    String phpUrl = "https://lumasalud.ar/backend/api/ddjj_generar_desde_seguimiento.php";
	    
	    
	    Map<String, String> params = new HashMap<String, String>();
	    params.put("idSolicitud", String.valueOf(idSolicitud));
	    params.put("nombre", ParamUtil.getString(request, "nombre", ""));
	    params.put("apellido", ParamUtil.getString(request, "apellido", ""));
	    params.put("edad", ParamUtil.getString(request, "edad", ""));
	    params.put("dni", ParamUtil.getString(request, "dni_form", ""));
	    params.put("email", ParamUtil.getString(request, "email", ""));
	    params.put("codigo_area", ParamUtil.getString(request, "codigo_area", ""));
	    params.put("telefono", ParamUtil.getString(request, "telefono", ""));
	    params.put("provincia", ParamUtil.getString(request, "provincia", ""));
	    params.put("plan", ParamUtil.getString(request, "plan", ""));
	    params.put("relacion_dependencia", ParamUtil.getString(request, "relacionDependencia", "false"));
	    params.put("tiene_pareja", ParamUtil.getString(request, "tienePareja", "false"));
	    params.put("edad_pareja", ParamUtil.getString(request, "edad_pareja", ""));
	    params.put("tiene_hijos", ParamUtil.getString(request, "tieneHijos", "false"));
	    params.put("cantidad_hijos21", ParamUtil.getString(request, "cantidad_hijos21", ""));
	    params.put("cantidad_hijos25", ParamUtil.getString(request, "cantidad_hijos25", ""));
	    params.put("sueldo_bruto", ParamUtil.getString(request, "sueldoBruto", ""));
	    params.put("es_molinero", ParamUtil.getString(request, "esMolinero", "false"));

	    String response = postForm(phpUrl, params);

	    if (response == null || response.trim().length() == 0) {
	        throw new Exception("Respuesta vacía al generar el link DDJJ.");
	    }
	    
	    _log.error("Respuesta ddjj_generar_desde_seguimiento: " + response);

	}
	
	private String postForm(String targetUrl, Map<String, String> params) throws Exception {
	    HttpURLConnection conn = null;
	    OutputStream os = null;
	    BufferedReader br = null;

	    try {
	        StringBuilder postData = new StringBuilder();
	        for (Map.Entry<String, String> entry : params.entrySet()) {
	            if (postData.length() > 0) postData.append("&");
	            postData.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
	            postData.append("=");
	            postData.append(URLEncoder.encode(entry.getValue() != null ? entry.getValue() : "", "UTF-8"));
	        }

	        byte[] postBytes = postData.toString().getBytes("UTF-8");

	        URL url = new URL(targetUrl);
	        conn = (HttpURLConnection) url.openConnection();
	        conn.setRequestMethod("POST");
	        conn.setConnectTimeout(15000);
	        conn.setReadTimeout(30000);
	        conn.setDoOutput(true);
	        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
	        conn.setRequestProperty("Content-Length", String.valueOf(postBytes.length));

	        os = conn.getOutputStream();
	        os.write(postBytes);
	        os.flush();

	        int status = conn.getResponseCode();

	        InputStream is = (status >= 200 && status < 300)
	            ? conn.getInputStream()
	            : conn.getErrorStream();

	        br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
	        StringBuilder resp = new StringBuilder();
	        String line;
	        while ((line = br.readLine()) != null) {
	            resp.append(line);
	        }

	        if (status < 200 || status >= 300) {
	            throw new Exception("HTTP " + status + " calling " + targetUrl + ": " + resp.toString());
	        }

	        return resp.toString();

	    } finally {
	        try { if (br != null) br.close(); } catch (Exception ignored) {}
	        try { if (os != null) os.close(); } catch (Exception ignored) {}
	        if (conn != null) conn.disconnect();
	    }
	}
	
	private ActionForward cotizarFormulario(ActionMapping mapping, RenderRequest renderRequest) {
	    try {
	    	
	    	HttpServletRequest httpServletRequest = PortalUtil.getHttpServletRequest(renderRequest);
			HttpSession session = (HttpSession) httpServletRequest.getSession();
	        long id = ParamUtil.getLong(renderRequest, "id");
	        String modo = ParamUtil.getString(renderRequest, "modo", "ver");
	        String origen = ParamUtil.getString(renderRequest, "origen", "recot");
	        Map<String,Object> formulario = SolicitudAfiliacionServiceUtil.getSolicitudById(id);
	        List<Afiliado >grupo = SolicitudAfiliacionServiceUtil.getGrupoFamiliarDDJJ(id);
	      
	        String cotizaDia  = ParamUtil.getString(renderRequest, "cotizaDia", "");
	        String cotizaMes  = ParamUtil.getString(renderRequest, "cotizaMes", "");
	        String cotizaAnio = ParamUtil.getString(renderRequest, "cotizaAnio", "");
	        
	        if(cotizaDia.isEmpty()) {
	        	cotizaDia=  formulario.get("fecha_ingreso").toString().substring(8,10);
	        }
	        if(cotizaMes.isEmpty()) {
	        	cotizaMes=  formulario.get("fecha_ingreso").toString().substring(5,7);
	        	cotizaMes=  String.format("%02d",Integer.parseInt(cotizaMes)-1);
	        }
	        
	        if(cotizaAnio.isEmpty()) {
	        	cotizaAnio=  formulario.get("fecha_ingreso").toString().substring(0,4);
	        }
	        
	        if (formulario == null) {
	            renderRequest.setAttribute("mensaje", "No se encontró la solicitud.");
	            return mapping.findForward("portlet.afiliados.seguimiento.form");
	        }

	        User user = PortalUtil.getUser(renderRequest);
	        boolean veTodos = (user != null) &&
	            PermissionUtil.userContainsRole(user, WebKeysAfiliados.COMERCIAL_ADMINISTRADOR);

	        boolean veConsulta = (user != null) &&
	        	    PermissionUtil.userContainsRole(user, WebKeysAfiliados.COMERCIAL_CONSULTA);
	        
	        /*
	        if (!veTodos  && !veConsulta) {
	            if (user == null) {
	                renderRequest.setAttribute("mensaje", "No se pudo identificar el usuario logueado.");
	                return mapping.findForward("portlet.afiliados.seguimiento.form");
	            }

	            Long idVendedorLogueado =
	                SolicitudAfiliacionServiceUtil.getIdVendedorByEmail(user.getEmailAddress());

	            if (idVendedorLogueado == null || idVendedorLogueado.longValue() <= 0) {
	                renderRequest.setAttribute("mensaje", "El usuario no tiene vendedor asociado.");
	                return mapping.findForward("portlet.afiliados.seguimiento.form");
	            }

	           
	        }
	        */
	        renderRequest.setAttribute("formulario", formulario);
	        renderRequest.setAttribute("grupoFamiliar", grupo);
	        renderRequest.setAttribute("modo", modo);
	        renderRequest.setAttribute("cotizaDia", cotizaDia);
	        renderRequest.setAttribute("cotizaMes", cotizaMes);
	        renderRequest.setAttribute("cotizaAnio", cotizaAnio);
	        
	        
	        List<PrecioPlanSuperador>precios = new ArrayList<PrecioPlanSuperador>();;
	        List<AjustePlanSuperador>ajustes = new ArrayList<AjustePlanSuperador>();
	        
	        
	        if("cot".equalsIgnoreCase(origen)) {
	            ajustes=SolicitudAfiliacionServiceUtil.getCotizacionAjustesById(id);
	            precios = SolicitudAfiliacionServiceUtil.getCotizacionPreciosById(id);
	        }    
	        session.setAttribute(WebKeysTesoreria.PRECIOS_COTIZACION_RESULT, precios);
	        
	        session.setAttribute(WebKeysTesoreria.AJUSTES_COTIZACION_ASIGNADOS, ajustes);
	        session.setAttribute(WebKeysTesoreria.AJUSTES_COTIZACION_SELECCIONADO, new AjustePlanSuperador());
	        session.setAttribute(WebKeysTesoreria.AJUSTE_COTIZACION_FECHA,cotizaDia + "/" +
	        		String.format("%02d",Integer.parseInt(cotizaMes) + 1) + "/" + cotizaAnio);

	        return mapping.findForward("portlet.afiliados.formulario.cotizar");

	    } catch (Exception e) {
	        _log.error("Error cotizar Grupo Familiar", e);
	        renderRequest.setAttribute("mensaje", "Error cargando grupo familiar a cotizar.");
	        return mapping.findForward("portlet.afiliados.seguimiento.form");
	    }
	}
	
	
	private ActionForward seleccionarAjuste(ActionMapping mapping, RenderRequest renderRequest) {
	    try {
	    	HttpServletRequest httpServletRequest = PortalUtil.getHttpServletRequest(renderRequest);
			HttpSession session = (HttpSession) httpServletRequest.getSession();
			List<AjustePlanSuperador> disponibles = (List<AjustePlanSuperador>)session.getAttribute(WebKeysTesoreria.AJUSTES_COTIZACION_RESULT);
			
			
			
	    	String ajustes = ParamUtil.getString(renderRequest,"ajustesid");
			String[] ajustesArray=ajustes.split(",");
			Integer ajusteId=0;
			if(ajustesArray.length>0) {
			   ajusteId = Integer.parseInt(ajustesArray[0]);
			}
			for(AjustePlanSuperador aj:disponibles) {
				if(aj.getId().equals(ajusteId)) {
					session.setAttribute(WebKeysTesoreria.AJUSTES_COTIZACION_SELECCIONADO,aj);
					break;
				}
			}
			return mapping
    				.findForward("portlet.afiliados.formulario.cotizar.ajustes");
	    } catch (Exception e) {
	        _log.error("Error cotizar Grupo Familiar - Ajustes", e);
	        renderRequest.setAttribute("mensaje", "Error cargando ajustes a cotizar.");
	        return mapping.findForward("portlet.afiliados.formulario.cotizar.ajustes");
	    }
	}
	
	private ActionForward agregarAjuste(ActionMapping mapping, RenderRequest renderRequest) {
	    try {
	    	HttpServletRequest httpServletRequest = PortalUtil.getHttpServletRequest(renderRequest);
			HttpSession session = (HttpSession) httpServletRequest.getSession();
			List<AjustePlanSuperador> asignados = (List<AjustePlanSuperador>)session.getAttribute(WebKeysTesoreria.AJUSTES_COTIZACION_ASIGNADOS);
			if(asignados==null) asignados = new ArrayList<AjustePlanSuperador>();
			
	    	Integer id = ParamUtil.getInteger(renderRequest,"ajusteid");
	    	String  descripcion =ParamUtil.getString(renderRequest, "ajustede");
	    	Double porcentaje = ParamUtil.getDouble(renderRequest, "ajusteporcentaje");
	    	Double importe = ParamUtil.getDouble(renderRequest, "ajusteimporte");
	    	
	    	String fechaDia = ParamUtil.getString(renderRequest,"fechadesdedia");
			String fechaMes = ParamUtil.getString(renderRequest,"fechadesdemes");
			String fechaAnio = ParamUtil.getString(renderRequest,"fechadesdeanio");
			
			String fechaDiaH = ParamUtil.getString(renderRequest,"fechahastadia");
			String fechaMesH = ParamUtil.getString(renderRequest,"fechahastames");
			String fechaAnioH = ParamUtil.getString(renderRequest,"fechahastaanio");
			
			Date fechaD = null;
			try {
				fechaD = formatoDeFechas.parse(fechaDia + "/"
						+ (Integer.parseInt(fechaMes) + 1) + "/"
						+ fechaAnio);
			} catch (Exception e) {
				fechaD = null;
			}
			
			Date fechaH = null;
			try {
				fechaH = formatoDeFechas.parse(fechaDiaH + "/"
						+ (Integer.parseInt(fechaMesH) + 1) + "/"
						+ fechaAnioH);
			} catch (Exception e) {
				fechaH = null;
			}
	    	
	    	
	    	AjustePlanSuperador ajuste = new AjustePlanSuperador();
	    	ajuste.setId(id);
	    	ajuste.setDescripcion(descripcion);
	    	ajuste.setPorcentaje(porcentaje);
	    	ajuste.setImporte(new BigDecimal(importe));
	    	ajuste.setFechaDesde(fechaD);
	    	ajuste.setFechaHasta(fechaH);
	    	asignados.add(ajuste);
			session.setAttribute(WebKeysTesoreria.AJUSTES_COTIZACION_ASIGNADOS,asignados);
			
			return mapping
    				.findForward("portlet.afiliados.formulario.cotizar.ajustes.asignados");
	    } catch (Exception e) {
	        _log.error("Error cotizar Grupo Familiar - Ajustes asignados agregar", e);
	        renderRequest.setAttribute("mensaje", "Error cargando ajustes a cotizar.");
	        return mapping.findForward("portlet.afiliados.formulario.cotizar.ajustes.asignados");
	    }
	}
	
	private ActionForward sacarAjuste(ActionMapping mapping, RenderRequest renderRequest) {
	    try {
	    	HttpServletRequest httpServletRequest = PortalUtil.getHttpServletRequest(renderRequest);
			HttpSession session = (HttpSession) httpServletRequest.getSession();
			List<AjustePlanSuperador> asignados = (List<AjustePlanSuperador>)session.getAttribute(WebKeysTesoreria.AJUSTES_COTIZACION_ASIGNADOS);
			if(asignados==null) asignados = new ArrayList<AjustePlanSuperador>();
			
	    	int orden = ParamUtil.getInteger(renderRequest,"orden");
	    	
	    	asignados.remove(orden);
			session.setAttribute(WebKeysTesoreria.AJUSTES_COTIZACION_ASIGNADOS,asignados);
			
			return mapping
    				.findForward("portlet.afiliados.formulario.cotizar.ajustes.asignados");
	    } catch (Exception e) {
	        _log.error("Error cotizar Grupo Familiar - Ajustes asignados eliminar", e);
	        renderRequest.setAttribute("mensaje", "Error cargando ajustes a cotizar.");
	        return mapping.findForward("portlet.afiliados.formulario.cotizar.ajustes.asignados");
	    }
	}
	
	
	private ActionForward grabarCotizacion(ActionMapping mapping, RenderRequest renderRequest) {
	    try {
	    	
	    	HttpServletRequest httpServletRequest = PortalUtil.getHttpServletRequest(renderRequest);
			HttpSession session = (HttpSession) httpServletRequest.getSession();
	        long id = ParamUtil.getLong(renderRequest, "id");
	        String modo = ParamUtil.getString(renderRequest, "modo", "ver");
	        Map<String,Object> formulario = SolicitudAfiliacionServiceUtil.getSolicitudById(id);
	        List<Afiliado >grupo = SolicitudAfiliacionServiceUtil.getGrupoFamiliarDDJJ(id);
	        if (formulario == null) {
	            renderRequest.setAttribute("mensaje", "No se encontró la solicitud.");
	            return mapping.findForward("portlet.afiliados.seguimiento.form");
	        }

	        renderRequest.setAttribute("formulario", formulario);
	        renderRequest.setAttribute("modo", modo);
	        
	        String cotizaDia  = ParamUtil.getString(renderRequest, "cotizaDia", "");
	        String cotizaMes  = ParamUtil.getString(renderRequest, "cotizaMes", "");
	        String cotizaAnio = ParamUtil.getString(renderRequest, "cotizaAnio", "");
	        
	        if(cotizaDia.isEmpty()) {
	        	cotizaDia=  formulario.get("fecha_ingreso").toString().substring(8,10);
	        }
	        if(cotizaMes.isEmpty()) {
	        	cotizaMes=  formulario.get("fecha_ingreso").toString().substring(5,7);
	        	cotizaMes=  String.format("%02d",Integer.parseInt(cotizaMes)-1);
	        }
	        
	        if(cotizaAnio.isEmpty()) {
	        	cotizaAnio=  formulario.get("fecha_ingreso").toString().substring(0,4);
	        }
	        
	        session.setAttribute(WebKeysTesoreria.AJUSTE_COTIZACION_FECHA,cotizaDia + "/" +
	        		String.format("%02d",Integer.parseInt(cotizaMes) + 1) + "/" + cotizaAnio);
	        User user = PortalUtil.getUser(renderRequest);
	        
	        List<AjustePlanSuperador> ajustes= (List<AjustePlanSuperador>) session.getAttribute(WebKeysTesoreria.AJUSTES_COTIZACION_ASIGNADOS);
	        List<PrecioPlanSuperador> precios=(List<PrecioPlanSuperador>) session.getAttribute(WebKeysTesoreria.PRECIOS_COTIZACION_RESULT);
	        
	        SolicitudAfiliacionServiceUtil.saveCotizacion(id, precios, ajustes, user.getScreenName());
	        
	        renderRequest.setAttribute("formulario", formulario);
	        renderRequest.setAttribute("grupoFamiliar", grupo);
	        renderRequest.setAttribute("modo", modo);
	        renderRequest.setAttribute("cotizaDia", cotizaDia);
	        renderRequest.setAttribute("cotizaMes", cotizaMes);
	        renderRequest.setAttribute("cotizaAnio", cotizaAnio);
	        
	        return mapping.findForward("portlet.afiliados.formulario.cotizar");

	    } catch (Exception e) {
	        _log.error("Error Grabar Cotizacion", e);
	        renderRequest.setAttribute("mensaje", "Error guardar cotizacion.");
	        return mapping.findForward("portlet.afiliados.seguimiento.form");
	    }
	}

}
