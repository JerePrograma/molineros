package ar.com.ospim.autorizaciones.action;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.afiliados.WebKeysAfiliados;
import ar.com.ospim.afiliados.beans.Afiliado;
import ar.com.ospim.afiliados.services.BusquedaAfiliadoServiceUtil;
import ar.com.ospim.afiliados.services.EditarAfiliadoServiceUtil;
import ar.com.ospim.afiliados.services.SeccionalServiceUtil;
import ar.com.ospim.afiliados.services.TelefonoServiceUtil;
import ar.com.ospim.autorizaciones.beans.Nomenclador;
import ar.com.ospim.autorizaciones.services.NomencladorServiceUtil;
import ar.com.ospim.crm.WebKeysCrm;
import ar.com.ospim.crm.action.ActualizaDomicilioAfiliadoAction;
import ar.com.ospim.global.beans.Domicilio;
import ar.com.ospim.global.beans.Telefono;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.tesoreria.service.ReciboServiceUtil;
import ar.com.ospim.util.StringUtils;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.JSONAction;

public class AfiliadoDatosJSONAction extends JSONAction {

	private static Log log = LogFactoryUtil
			.getLog(AfiliadoDatosJSONAction.class);
	@Override
	public String getJSON(ActionMapping arg0, ActionForm arg1,
			HttpServletRequest req, HttpServletResponse arg3) throws Exception {
		final long MILLSECS_PER_DAY = 24 * 60 * 60 * 1000; 
		int inte = ParamUtil.getInteger(req, "inte");
		String cuil_titular = ParamUtil.getString(req, "cuil_titular");
		String email="";
		String actualizaDomicilio = "";
		String actualizaTelefono = "";
		String qDiasAValidar="";
		
		try {
	            Afiliado afiliado = EditarAfiliadoServiceUtil.getAfiliadoEntry(cuil_titular, inte);
	            if (afiliado != null) {
	                email = afiliado.getEmail();
	            } else {
	                log.warn("Afiliado nulo para cuil=" + cuil_titular + " inte=" + inte);
	            }
	            
	            if (StringUtils.checkEmpty(email) || email.equalsIgnoreCase("null")) {
	                Afiliado titular = EditarAfiliadoServiceUtil.getAfiliadoEntry(cuil_titular, 0);
	                if (titular != null) {
	                    email = titular.getEmail();
	                }
	            }

	            Calendar c = Calendar.getInstance();
	            c.setTime(new Date());

	            //Domicilio (siempre inte=0)
	            List<Domicilio> domicilios = BusquedaAfiliadoServiceUtil.buscarDomiciliosAfiliado(cuil_titular, 0);
	            boolean domicilioVigente = false;
	            if (domicilios != null && !domicilios.isEmpty()) {
	                Domicilio domicilio = domicilios.get(0);
	                if (domicilio.getModi_fecha() != null) {
	                    long diferencia = (c.getTimeInMillis() - domicilio.getModi_fecha().getTime()) / MILLSECS_PER_DAY;
	                    int diasDomi = Integer.parseInt(TraeListasServiceUtil.getSystemConfig("DIAS_PARA_VALIDAR_DOMICILIO"));
	                    domicilioVigente = Math.abs(diferencia) <= diasDomi;
	                }
	            }
	            
	            //usa telefono del titular
	            int idPar = (afiliado != null) ? afiliado.getId_parentesco() : -1;
	            boolean esTitularOConyugeOConcubino =
	                    idPar == WebKeysAfiliados.PARENTESCO_DEFAULT ||
	                    idPar == WebKeysAfiliados.CONYUGE_DEFAULT ||
	                    idPar == WebKeysAfiliados.CONCUBINO_DEFAULT;

	            int inteTelefono = esTitularOConyugeOConcubino ? inte : 0;
	            
	            List<Telefono> telefonos = TelefonoServiceUtil.getTelefonos(cuil_titular, inteTelefono);

	            if (telefonos == null || telefonos.isEmpty()) {
	                actualizaTelefono = "true"; // no tiene ningún teléfono
	            } else {
	                boolean telefonoVigente = false;
	                for (Telefono tel : telefonos) {
	                    if (tel.getModiFecha() != null) {
	                        long difTel = (c.getTimeInMillis() - tel.getModiFecha().getTime()) / MILLSECS_PER_DAY;
	                        int diasTel = Integer.parseInt(TraeListasServiceUtil.getSystemConfig("DIAS_PARA_VALIDAR_TELEFONO"));
	                        if (Math.abs(difTel) <= diasTel) {
	                            telefonoVigente = true;
	                            break;
	                        }
	                    }
	                }
	                
	                if (domicilioVigente || telefonoVigente) {
	                    actualizaDomicilio = "false";
	                    actualizaTelefono = "false";
	                } else {
	                    actualizaDomicilio = "true";
	                    actualizaTelefono = "true";
	                }
	            }
			
		} catch (Exception e) {
			return "{\"status\":\"falla_inesperada\"}";
		}

		//valores por defecto en caso de que no se haya seteado nada
        if (actualizaDomicilio.isEmpty()) actualizaDomicilio = "false";
        if (actualizaTelefono.isEmpty()) actualizaTelefono = "false";
        
		return "{"
		    + "\"email\":\"" + (email != null ? email : "") + "\","
		    + "\"actualizadomicilio\":" + actualizaDomicilio + ","
		    + "\"actualizatelefono\":" + actualizaTelefono
		    + "}";
	}	
}
