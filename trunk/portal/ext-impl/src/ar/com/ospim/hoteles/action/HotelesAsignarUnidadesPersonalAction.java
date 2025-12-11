package ar.com.ospim.hoteles.action;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.JSONAction;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.PortalUtil;

import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.hoteles.beans.Habitacion;
import ar.com.ospim.hoteles.beans.Mesa;
import ar.com.ospim.hoteles.beans.ProductoCategoria;
import ar.com.ospim.hoteles.services.HotelesServiceUtil;
import ar.com.ospim.hoteles.services.WebKeysHoteles;
import ar.com.ospim.util.StringUtils;



public class HotelesAsignarUnidadesPersonalAction extends JSONAction {

	@Override
	public String getJSON(ActionMapping arg0, ActionForm arg1,
			HttpServletRequest req, HttpServletResponse arg3) throws Exception {
		
		List<Habitacion> grupos = new ArrayList<Habitacion>();
		List<Mesa>mesas=new ArrayList<Mesa>();
		String tipo = req.getParameter("tipo");
		String ptovta = req.getParameter("ptovta");
		String idPersonal= req.getParameter("idpersonal");
		String seleccion=req.getParameter("seleccion");
		User user = PortalUtil.getUser(req);
		String resultado = "{}";
		Boolean respuesta=true;
 		
 		String valor="";
 		String unidades="";
 		
 		if("MESAS".equalsIgnoreCase(tipo)) {
 			try {
 			  HotelesServiceUtil.deleteMesasAsignadasPersonal(ptovta,Integer.valueOf(idPersonal));
 			
 			  String[] vAsignadas= seleccion.split(",");
 			  for(int i=0;i<vAsignadas.length ;i++) {
 				String[] vMesa= vAsignadas[i].split("_");
 				HotelesServiceUtil.insertMesasAsignadasPersonal(ptovta, Integer.valueOf(idPersonal), Integer.valueOf(vMesa[1]), user.getScreenName());
 			  }
 			}catch(Exception e) {
 				respuesta=false;
 			}
 			
 		}
 		resultado = "{\"rta\" : \""
				    +respuesta
			        + "\" }";
		
		return resultado;
	}
}