package ar.com.ospim.ws.rest;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import ar.com.ospim.afiliados.beans.Afiliado;
import ar.com.ospim.afiliados.services.BusquedaAfiliadoServiceUtil;

@Controller
public class WSIGSController {

	private static Log _log = LogFactoryUtil.getLog(WSIGSController.class);
	
	@RequestMapping(value = "/IGS", method = RequestMethod.GET)
	protected ModelAndView validaAfiliado(@RequestParam("nro_credencial") String nroCredencial,
			@RequestParam("inte") String inte,
			@RequestParam("tipoDoc") String tipoDoc,
			@RequestParam("nroDoc") String nroDoc,
			@RequestParam("fecha") String fecha,
			HttpServletRequest request)
			throws Exception {
		
		Map<String, String> model = new HashMap<String, String>();
		Afiliado afiliado = null;

		String ip = request.getRemoteAddr();
		_log.info("IGS CONSULTA IP: " + ip);
		File configDir = new File(System.getProperty("catalina.base"), "conf");
		File configFile = new File(configDir, "ingreso_portal.properties");
		InputStream stream = new FileInputStream(configFile);
		Properties props = new Properties();
		props.load(stream);
		StringTokenizer ips_seguras = new StringTokenizer(
				props.getProperty("ip_seguras_ws"), ",");
		boolean ipSegura=false;
		while (ips_seguras.hasMoreElements()) {
			String ip_segura = ips_seguras.nextToken();
			if (ip.contains(ip_segura)) {
				ipSegura= true;
			}
		}
		if(!ipSegura){
			model.put("estado", "IP DEL CLIENTE NO SEGURA");
			return new ModelAndView("jsonView", model);
		}

		model.put("nro_credencial", String.valueOf(nroCredencial));
		model.put("inte", String.valueOf(inte));
		model.put("apellido", "");
		model.put("nombre", "");
		model.put("tipo_doc", tipoDoc);
		model.put("nro_doc", nroDoc);
		model.put("plan", "");
		model.put("telefono", "");
		model.put("localidad", "");
		model.put("provincia", "");
		model.put("tercerizadora","");
		_log.debug("nro credencial: " + nroCredencial + "inte: " + inte + "Documento:  " + tipoDoc + " " +nroDoc);
		
		if (!validarIngreso(nroCredencial, inte, tipoDoc, nroDoc)) {
			model.put("estado", "DATOS INGRESADOS NO VALIDOS");
		} else {
			try {
				afiliado = BusquedaAfiliadoServiceUtil
						.registraConsultaAfiliadoIGS(null, nroCredencial, inte,
								!"null".equals(tipoDoc) ? tipoDoc : null,
								nroDoc, ip,fecha);

				if (null != afiliado) {
					if (null != afiliado.getAfiPlan().getPlan()
							.getDescripcion()) {
						model.put("nro_credencial",
								String.valueOf(afiliado.getNroCredencial()));
						model.put("inte", String.valueOf(afiliado.getInte()));
						model.put("apellido", afiliado.getApellido());
						model.put("nombre", afiliado.getNombre());
						model.put("tipo_doc", afiliado.getDocumento_tipo());
						model.put("nro_doc", afiliado.getDocu_numero());
						model.put("plan", afiliado.getAfiPlan().getPlan()
								.getDescripcion());
						model.put("telefono", afiliado.getDomicilioDefault()
								.getTelefono());
						model.put("localidad", afiliado.getDomicilioDefault()
								.getLocalidad().getDescripcion());
						model.put("provincia", afiliado.getDomicilioDefault()
								.getProvincia().getDescripcion());
						model.put("estado", "AFILIADO VIGENTE");
						model.put("tercerizadora", afiliado.getId_tercerizadora());
					} else {
						model.put("estado", "AFILIADO NO VIGENTE");
					}
				} else {
					model.put("estado", "AFILIADO INEXISTENTE");
				}

			} catch (Exception e) {
				_log.error("IGS CONSULTA ERROR");
				_log.error(e);
				model.put("estado", "ERROR AL CONSULTAR AFILIADO");
			}
			
		}

		return new ModelAndView("jsonView", model);
	}

	public boolean validarIngreso(String nroCredencial, String inte, String tipoDoc,
			String nroDoc) {
		if ((ar.com.ospim.util.StringUtils.checkEmpty(nroCredencial)
				|| "null".equals(nroCredencial.trim()) || null == ar.com.ospim.util.StringUtils
				.getLongOrNull(nroCredencial))
				&& (ar.com.ospim.util.StringUtils.checkEmpty(nroDoc)
						|| "null".equals(nroDoc.trim()) || null == ar.com.ospim.util.StringUtils
						.getIntegerOrNull(nroDoc))) {
			return false;
		}
		return true;
	}

}
