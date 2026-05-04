package ar.com.ospim.ws.rest;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.util.PortalUtil;

import ar.com.ospim.afiliados.services.AfiliacionesServiceUtil;

@Controller
public class AfiliacionesFormController {

  private static final Log _log = LogFactoryUtil.getLog(AfiliacionesFormController.class);
  private static final String API_KEY = "TEST-OSPIM-2025";

  @RequestMapping(value = "/AFILIACIONES_FORM_GUARDAR", method = RequestMethod.POST)
  public ModelAndView guardar(
      @RequestParam("nombre") String nombre,
      @RequestParam(value="apellido", required=false) String apellido,
      @RequestParam("edad") Integer edad,
      @RequestParam(value="fecha_nacimiento", required=false) String fechaNacimiento,
      @RequestParam("dni") String dni,
      @RequestParam(value = "codigo_area", required = false) String codigoArea,
      @RequestParam("telefono") String telefono,
      @RequestParam("provincia") String provincia,
      @RequestParam("plan") String plan,
      @RequestParam("email") String email,

      @RequestParam(value="relacion_dependencia", required=false) String relacionDependencia,
      @RequestParam(value="tiene_pareja", required=false) String tienePareja,
      @RequestParam(value="edad_pareja", required=false) Integer edadPareja,
      @RequestParam(value="tiene_hijos", required=false) String tieneHijos,
      @RequestParam(value="cantidad_hijos21", required=false) Integer cantidadHijos21,
      @RequestParam(value="cantidad_hijos25", required=false) Integer cantidadHijos25,
      @RequestParam(value="sueldo_bruto", required=false) String sueldoBruto,
      @RequestParam(value="monto_estimado", required=false) String montoEstimado,
      @RequestParam(value="es_molinero", required=false) String esMolinero,

      @RequestParam(value="generar_ddjj", required=false) String generarDdjj,
      @RequestParam(value="apiKey", required=false) String apiKey,

      @RequestParam(value="idSolicitud", required=false) Long idSolicitud,
      @RequestParam(value="idInteresado", required=false) Long idInteresado,
      @RequestParam(value="modi_usr", required=false) String modiUsr,
      HttpServletRequest request
  ) {
    Map<String, Object> model = new HashMap<String, Object>();

    if (apiKey == null || !API_KEY.equals(apiKey)) {
      model.put("estado", "ERROR");
      model.put("mensaje", "No autorizado");
      return new ModelAndView("jsonView", model);
    }

    try {
      nombre = safe(nombre).trim();
      apellido = safe(apellido).trim();
      fechaNacimiento = safe(fechaNacimiento).trim();
      dni = onlyDigits(safe(dni));
      codigoArea = onlyDigits(safe(codigoArea));
      telefono = onlyDigits(safe(telefono));
      provincia = safe(provincia).trim();
      plan = safe(plan).trim();
      email = safe(email).trim().toLowerCase();

      Boolean relacionDependenciaBool = parseBoolean(relacionDependencia, Boolean.TRUE);
      Boolean tieneParejaBool = parseBoolean(tienePareja, Boolean.FALSE);
      Boolean tieneHijosBool = parseBoolean(tieneHijos, Boolean.FALSE);
      Boolean esMolineroBool = parseBoolean(esMolinero, null);
      boolean generarDdjjBool = Boolean.TRUE.equals(parseBoolean(generarDdjj, Boolean.FALSE));

      BigDecimal sueldoBD = parseMoneyNullable(sueldoBruto);
      montoEstimado = safe(montoEstimado).trim();

      if (nombre.isEmpty() ||dni.isEmpty() || codigoArea.isEmpty() ||
    		    telefono.isEmpty() || provincia.isEmpty() || plan.isEmpty() || email.isEmpty()) {
    		  model.put("estado", "ERROR");
    		  model.put("mensaje", "Por favor complete todos los campos obligatorios");
    		  return new ModelAndView("jsonView", model);
    		}

    		if ((edad == null || edad.intValue() < 18 || edad.intValue() > 120) &&
    		    fechaNacimiento.isEmpty()) {
    		  model.put("estado", "ERROR");
    		  model.put("mensaje", "Debe informar edad o fecha de nacimiento válida");
    		  return new ModelAndView("jsonView", model);
    		}

    		if (!fechaNacimiento.isEmpty() && !fechaNacimiento.matches("^\\d{4}-\\d{2}-\\d{2}$")) {
    		  model.put("estado", "ERROR");
    		  model.put("mensaje", "Fecha de nacimiento inválida");
    		  return new ModelAndView("jsonView", model);
    		}

    		if (edad != null && (edad.intValue() < 18 || edad.intValue() > 120)) {
    		  model.put("estado", "ERROR");
    		  model.put("mensaje", "Edad inválida");
    		  return new ModelAndView("jsonView", model);
    		}

    		dni = onlyDigits(safe(dni));

    		if (dni.length() == 7) {
    		  dni = "0" + dni;
    		}

    		if (!dni.matches("^\\d{8}$")) {
    		  model.put("estado", "ERROR");
    		  model.put("mensaje", "DNI inválido (8 dígitos)");
    		  return new ModelAndView("jsonView", model);
    		}

      if (codigoArea.length() < 2 || codigoArea.length() > 4) {
    	    model.put("estado", "ERROR");
    	    model.put("mensaje", "Código de área inválido");
    	    return new ModelAndView("jsonView", model);
    	}

    	if (telefono.length() < 6 || telefono.length() > 8) {
    	    model.put("estado", "ERROR");
    	    model.put("mensaje", "Teléfono inválido");
    	    return new ModelAndView("jsonView", model);
    	}

    	if ((codigoArea + telefono).length() != 10) {
    	    model.put("estado", "ERROR");
    	    model.put("mensaje", "Código de área + teléfono deben sumar 10 dígitos");
    	    return new ModelAndView("jsonView", model);
    	}

      if (!email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
        model.put("estado", "ERROR");
        model.put("mensaje", "Email inválido");
        return new ModelAndView("jsonView", model);
      }

      if (Boolean.TRUE.equals(tieneParejaBool) && edadPareja == null) {
        model.put("estado", "ERROR");
        model.put("mensaje", "Falta edad de la pareja");
        return new ModelAndView("jsonView", model);
      }

      if (!Boolean.TRUE.equals(tieneParejaBool)) {
        edadPareja = null;
      }

      if (!Boolean.TRUE.equals(tieneHijosBool)) {
        cantidadHijos21 = null;
        cantidadHijos25 = null;
      } else {
        int h21 = (cantidadHijos21 == null) ? 0 : cantidadHijos21.intValue();
        int h25 = (cantidadHijos25 == null) ? 0 : cantidadHijos25.intValue();

        if (h21 < 0 || h25 < 0 || (h21 + h25) < 1) {
          model.put("estado", "ERROR");
          model.put("mensaje", "Cantidad de hijos inválida");
          return new ModelAndView("jsonView", model);
        }
      }

      modiUsr = safe(modiUsr).trim();

      User u = PortalUtil.getUser(request);
      String usuarioPortal = (u != null ? u.getScreenName() : "");

      String usuario = !usuarioPortal.isEmpty()
          ? usuarioPortal
          : (!modiUsr.isEmpty() ? modiUsr : "");
      
      Map<String, Object> out = AfiliacionesServiceUtil.guardarSolicitud(
    	  idInteresado,
    	  idSolicitud,
          nombre,
          apellido,
          edad,
          fechaNacimiento,
          dni,
          codigoArea,
          telefono,
          provincia,
          plan,
          email,
          relacionDependenciaBool,
          tieneParejaBool,
          edadPareja,
          tieneHijosBool,
          cantidadHijos21,
          cantidadHijos25,
          sueldoBD,
          montoEstimado,
          esMolineroBool,
          generarDdjjBool,
          usuario
      );

      model.put("estado", "OK");
      model.putAll(out);
      return new ModelAndView("jsonView", model);

    } catch (Exception e) {
      _log.error("Error guardando solicitud comercial", e);
      model.put("estado", "ERROR");
      model.put("mensaje", "Error interno");
      return new ModelAndView("jsonView", model);
    }
  }

  @RequestMapping(value = "/SOLICITUD_PDF_GUARDAR", method = RequestMethod.POST)
  public ModelAndView guardarPdfSolicitud(
      @RequestParam("idSolicitud") Long idSolicitud,
      @RequestParam("pdfSolicitud") String pdfSolicitud,
      @RequestParam("urlSolicitud") String urlSolicitud,
      @RequestParam(value="modiUsr", required=false) String modiUsr,
      @RequestParam(value="apiKey", required=false) String apiKey
  ) {
      Map<String, Object> model = new HashMap<String, Object>();

      if (apiKey == null || !API_KEY.equals(apiKey)) {
          model.put("ok", false);
          model.put("error", "No autorizado");
          return new ModelAndView("jsonView", model);
      }

      try {
          AfiliacionesServiceUtil.guardarPdfSolicitud(idSolicitud, pdfSolicitud, urlSolicitud, modiUsr);
          model.put("ok", true);
          return new ModelAndView("jsonView", model);
      } catch (Exception e) {
          _log.error("Error SOLICITUD_PDF_GUARDAR", e);
          model.put("ok", false);
          model.put("error", "Error interno");
          return new ModelAndView("jsonView", model);
      }
  }
  
  @RequestMapping(value = "/DDJJ_PDF_GUARDAR", method = RequestMethod.POST)
  public ModelAndView guardarPdfDdjj(
      @RequestParam("token") String token,
      @RequestParam("pdfUrl") String pdfUrl,
      @RequestParam(value="apiKey", required=false) String apiKey
  ) {
      Map<String, Object> model = new HashMap<String, Object>();

      if (apiKey == null || !API_KEY.equals(apiKey)) {
          model.put("ok", false);
          model.put("error", "No autorizado");
          return new ModelAndView("jsonView", model);
      }

      try {
          AfiliacionesServiceUtil.guardarPdfDdjj(token, pdfUrl);
          model.put("ok", true);
          return new ModelAndView("jsonView", model);
      } catch (Exception e) {
          _log.error("Error DDJJ_PDF_GUARDAR", e);
          model.put("ok", false);
          model.put("error", "Error interno");
          return new ModelAndView("jsonView", model);
      }
  }
  
  @RequestMapping(value = "/DDJJ_CREAR_POR_SOLICITUD", method = RequestMethod.POST)
  public ModelAndView crearDdjjPorSolicitud(
      @RequestParam("idSolicitud") Long idSolicitud,
      @RequestParam("token") String token,
      @RequestParam("ddjjUrl") String ddjjUrl,
      @RequestParam(value="apiKey", required=false) String apiKey
  ) {
      Map<String, Object> model = new HashMap<String, Object>();

      if (apiKey == null || !API_KEY.equals(apiKey)) {
          model.put("ok", false);
          model.put("error", "No autorizado");
          return new ModelAndView("jsonView", model);
      }

      try {
          AfiliacionesServiceUtil.crearDdjjPorSolicitud(idSolicitud, token, ddjjUrl);
          model.put("ok", true);
          return new ModelAndView("jsonView", model);
      } catch (Exception e) {
          _log.error("Error DDJJ_CREAR_POR_SOLICITUD", e);
          model.put("ok", false);
          model.put("error", "Error interno");
          return new ModelAndView("jsonView", model);
      }
  }
  
  private static String safe(String s) {
    return s == null ? "" : s;
  }

  private static String onlyDigits(String s) {
    return s == null ? "" : s.replaceAll("\\D+", "");
  }

  private static Boolean parseBoolean(String s, Boolean defaultValue) {
    s = safe(s).trim().toLowerCase();
    if (s.isEmpty()) return defaultValue;
    if ("true".equals(s) || "1".equals(s) || "si".equals(s) || "sí".equals(s)) return Boolean.TRUE;
    if ("false".equals(s) || "0".equals(s) || "no".equals(s)) return Boolean.FALSE;
    return defaultValue;
  }

  private static BigDecimal parseMoneyNullable(String s) {
    if (s == null) return null;
    s = s.trim();
    if (s.isEmpty()) return null;

    s = s.replaceAll("[^0-9,\\.\\-]", "");
    if (s.isEmpty()) return null;

    if (s.contains(",") && s.contains(".")) {
      s = s.replace(".", "");
      s = s.replace(",", ".");
    } else if (s.contains(",")) {
      s = s.replace(",", ".");
    }

    try {
      return new BigDecimal(s);
    } catch (Exception e) {
      return null;
    }
  }
}