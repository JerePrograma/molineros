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

import ar.com.ospim.afiliados.services.DDJJServiceUtil;

@Controller
public class DDJJFormController {

  private static final Log _log = LogFactoryUtil.getLog(DDJJFormController.class);
  private static final String API_KEY = "TEST-OSPIM-2025";

  @RequestMapping(value = "/DDJJ_SET_ESTADO", method = RequestMethod.POST)
  public ModelAndView setEstado(
      @RequestParam("token") String token,
      @RequestParam("estado") String estado,
      @RequestParam(value="detalle", required=false) String detalle,
      @RequestParam(value="actor", required=false) String actor,
      @RequestParam(value="apiKey", required=false) String apiKey,
      HttpServletRequest request
  ) {
    Map<String, Object> model = new HashMap<String, Object>();

    if (apiKey == null || !API_KEY.equals(apiKey)) {
      model.put("ok", false);
      model.put("error", "No autorizado");
      return new ModelAndView("jsonView", model);
    }

    try {
      token = safe(token).trim();
      estado = safe(estado).trim();

      if (token.isEmpty() || estado.isEmpty()) {
        model.put("ok", false);
        model.put("error", "token/estado requeridos");
        return new ModelAndView("jsonView", model);
      }

      DDJJServiceUtil.cambiarEstado(token, estado);

      model.put("ok", true);
      model.put("token", token);
      model.put("estado", estado);
      return new ModelAndView("jsonView", model);

    } catch (Exception e) {
      _log.error("Error cambiando estado DDJJ", e);
      model.put("ok", false);
      model.put("error", e.getMessage());
      return new ModelAndView("jsonView", model);
    }
  }

  @RequestMapping(value = "/DDJJ_FORM_GET", method = RequestMethod.POST)
  public ModelAndView get(
      @RequestParam("token") String token,
      @RequestParam(value="apiKey", required=false) String apiKey
  ) {
    Map<String, Object> model = new HashMap<String, Object>();

    if (apiKey == null || !API_KEY.equals(apiKey)) {
      model.put("ok", false);
      model.put("error", "No autorizado");
      return new ModelAndView("jsonView", model);
    }

    try {
      token = safe(token).trim();
      if (token.isEmpty()) {
        model.put("ok", false);
        model.put("error", "Token requerido");
        return new ModelAndView("jsonView", model);
      }

      Map<String, Object> datos = DDJJServiceUtil.getByToken(token);
      if (datos == null) {
        model.put("ok", false);
        model.put("error", "DDJJ no encontrada");
        return new ModelAndView("jsonView", model);
      }

      model.put("ok", true);
      model.put("datos", datos);
      return new ModelAndView("jsonView", model);

    } catch (Exception e) {
      _log.error("Error DDJJ_FORM_GET", e);
      model.put("ok", false);
      model.put("error", "Error interno");
      return new ModelAndView("jsonView", model);
    }
  }

  @RequestMapping(value = "/DDJJ_FORM_STEP1_GUARDAR", method = RequestMethod.POST)
  public ModelAndView guardarPaso1(
      @RequestParam("token") String token,
      @RequestParam("plan") String plan,

      @RequestParam("nombre") String nombre,
      @RequestParam("apellido") String apellido,
      @RequestParam("email") String email,
      @RequestParam("dni") String dni,
      @RequestParam("cuil") String cuil,
      @RequestParam("codigo_area") String codArea,
      @RequestParam("telefono") String telefono,
      @RequestParam("fecha_nacimiento") String fechaNacimiento,
      @RequestParam("sexo") String sexo,
      @RequestParam("nacionalidad") String nacionalidad,

      @RequestParam("calle") String calle,
      @RequestParam("numero") String numero,
      @RequestParam(value="barrio", required=false) String barrio,
      @RequestParam("localidad") String localidad,
      @RequestParam("provincia") String provincia,
      @RequestParam(value="monto_estimado", required=false) String montoEstimado,
      @RequestParam("estado_civil") String estadoCivil,
      @RequestParam(value="piso", required=false) String piso,
      @RequestParam(value="dpto", required=false) String dpto,
      @RequestParam("cp") String cp,
      
      @RequestParam("laboral_cuit") String laboralCuit,
      @RequestParam("laboral_razon_social") String laboralRazonSocial,
      @RequestParam("laboral_fecha_ingreso") String laboralFechaIngreso,
      @RequestParam(value="sueldo_bruto", required=false) String sueldoBruto,
      @RequestParam(value="grupo_familiar_json", required=false) String grupoFamiliarJson,

      @RequestParam(value="modi_usr", required=false) String modiUsr,
      
      @RequestParam(value="apiKey", required=false) String apiKey,
      HttpServletRequest request
  ) {
    Map<String, Object> model = new HashMap<String, Object>();

    if (apiKey == null || !API_KEY.equals(apiKey)) {
      model.put("ok", false);
      model.put("error", "No autorizado");
      return new ModelAndView("jsonView", model);
    }

    try {
      token = safe(token).trim();
      plan = safe(plan).trim();

      nombre = safe(nombre).trim();
      apellido = safe(apellido).trim();
      email = safe(email).trim().toLowerCase();
      dni = onlyDigits(safe(dni));    
      
      if (dni.length() == 7) {
    	  dni = "0" + dni;
    	}
      
      cuil = onlyDigits(safe(cuil));
      codArea = onlyDigits(safe(codArea));
      telefono = onlyDigits(safe(telefono));
      fechaNacimiento = safe(fechaNacimiento).trim();
      sexo = safe(sexo).trim();
      nacionalidad = safe(nacionalidad).trim();
      estadoCivil = safe(estadoCivil).trim();
      piso = safe(piso).trim();
      dpto = safe(dpto).trim();
      cp = onlyDigits(safe(cp));
      calle = safe(calle).trim();
      numero = safe(numero).trim();
      barrio = safe(barrio).trim();
      localidad = safe(localidad).trim();
      provincia = safe(provincia).trim();
      montoEstimado = safe(montoEstimado).trim();
      laboralCuit = onlyDigits(safe(laboralCuit));
      laboralRazonSocial = safe(laboralRazonSocial).trim();
      laboralFechaIngreso = safe(laboralFechaIngreso).trim();

      BigDecimal sueldoBrutoBD = parseMoneyNullable(sueldoBruto);
      
      grupoFamiliarJson = safe(grupoFamiliarJson).trim();
      
      if (grupoFamiliarJson.isEmpty()) grupoFamiliarJson = "[]";
      
      if (token.isEmpty() || nombre.isEmpty() || apellido.isEmpty() || email.isEmpty() || dni.isEmpty() ||
    		  cuil.isEmpty() || fechaNacimiento.isEmpty() || codArea.isEmpty() || telefono.isEmpty()) {
        model.put("ok", false);
        model.put("error", "Campos obligatorios incompletos");
        return new ModelAndView("jsonView", model);
      }
      
      if (!email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
    	  model.put("ok", false);
    	  model.put("error", "Email inválido");
    	  return new ModelAndView("jsonView", model);
    	}

    	if (!dni.matches("^\\d{8}$")) {
    	  model.put("ok", false);
    	  model.put("error", "DNI inválido (8 dígitos)");
    	  return new ModelAndView("jsonView", model);
    	}

    	if (!cuil.matches("^\\d{11}$")) {
    	  model.put("ok", false);
    	  model.put("error", "CUIL inválido (11 dígitos)");
    	  return new ModelAndView("jsonView", model);
    	}

    	if (!codArea.matches("^\\d{2,4}$")) {
    	  model.put("ok", false);
    	  model.put("error", "Código de área inválido (2 a 4 dígitos)");
    	  return new ModelAndView("jsonView", model);
    	}

    	if (!telefono.matches("^\\d{6,8}$")) {
    	  model.put("ok", false);
    	  model.put("error", "Teléfono inválido (6 a 8 dígitos)");
    	  return new ModelAndView("jsonView", model);
    	}

    	if ((codArea + telefono).length() != 10) {
    	  model.put("ok", false);
    	  model.put("error", "Código de área + teléfono deben sumar 10 dígitos");
    	  return new ModelAndView("jsonView", model);
    	}

    	if (!fechaNacimiento.matches("^\\d{4}-\\d{2}-\\d{2}$")) {
    	  model.put("ok", false);
    	  model.put("error", "Fecha de nacimiento inválida");
    	  return new ModelAndView("jsonView", model);
    	}
    	
    	if (estadoCivil.isEmpty()) {
    		  model.put("ok", false);
    		  model.put("error", "Estado civil requerido");
    		  return new ModelAndView("jsonView", model);
    		}

    		if (cp.isEmpty()) {
    		  model.put("ok", false);
    		  model.put("error", "Código postal requerido");
    		  return new ModelAndView("jsonView", model);
    		}
    		
    		modiUsr = safe(modiUsr).trim();

	        User u = PortalUtil.getUser(request);
	        String usuarioPortal = (u != null ? u.getScreenName() : "");

	        String usuario = !usuarioPortal.isEmpty()
	            ? usuarioPortal
	            : (!modiUsr.isEmpty() ? modiUsr : "ddjj_form");
    		        
    		
      long idDdjj = DDJJServiceUtil.guardarPaso1(
          token, plan,
          nombre, apellido, email, dni, cuil, codArea, telefono,
          fechaNacimiento, sexo, nacionalidad, estadoCivil, 
          calle, numero, piso, dpto, barrio, localidad, provincia, cp, montoEstimado, sueldoBrutoBD,
          laboralCuit, laboralRazonSocial, laboralFechaIngreso,
          grupoFamiliarJson, usuario
      );

      model.put("ok", true);
      model.put("id", idDdjj);
      model.put("token", token);
      model.put("plan", plan);
      return new ModelAndView("jsonView", model);

    } catch (Exception e) {
      _log.error("Error guardando DDJJ paso 1", e);
      model.put("ok", false);
      model.put("error", e.getMessage());
      return new ModelAndView("jsonView", model);
    }
  }

  @RequestMapping(value = "/DDJJ_SALUD_GUARDAR", method = RequestMethod.POST)
  public ModelAndView guardarSalud(
      @RequestParam("token") String token,
      @RequestParam("salud_json") String saludJson,
      @RequestParam(value="observaciones_json", required=false) String observacionesJson,
      @RequestParam(value="finalizar", required=false) String finalizar,
      @RequestParam(value="apiKey", required=false) String apiKey,
      HttpServletRequest request
  ) {
    Map<String, Object> model = new HashMap<String, Object>();

    if (apiKey == null || !API_KEY.equals(apiKey)) {
      model.put("ok", false);
      model.put("error", "No autorizado");
      return new ModelAndView("jsonView", model);
    }

    try {
      token = safe(token).trim();
      saludJson = safe(saludJson).trim();
      observacionesJson = safe(observacionesJson).trim();

      boolean finalizarBool = "true".equalsIgnoreCase(safe(finalizar).trim()) || "1".equals(safe(finalizar).trim());

      if (token.isEmpty()) {
        model.put("ok", false);
        model.put("error", "Token requerido");
        return new ModelAndView("jsonView", model);
      }

      if (saludJson.isEmpty()) {
        model.put("ok", false);
        model.put("error", "Datos de salud requeridos");
        return new ModelAndView("jsonView", model);
      }

      long idDdjj = DDJJServiceUtil.guardarSaludV2(
          token,
          saludJson,
          observacionesJson,
          finalizarBool
      );

      model.put("ok", true);
      model.put("id", idDdjj);
      model.put("token", token);
      model.put("finalizar", finalizarBool);
      return new ModelAndView("jsonView", model);

    } catch (Exception e) {
      _log.error("Error guardando salud DDJJ", e);
      model.put("ok", false);
      model.put("error", e.getMessage());
      return new ModelAndView("jsonView", model);
    }
  }

  @RequestMapping(value = "/DDJJ_GET_ENFERMEDADES", method = {RequestMethod.GET, RequestMethod.POST})
  public ModelAndView getEnfermedades(
      @RequestParam(value="apiKey", required=false) String apiKey
  ) {
    Map<String, Object> model = new HashMap<String, Object>();

    if (apiKey == null || !API_KEY.equals(apiKey)) {
      model.put("ok", false);
      model.put("error", "No autorizado");
      return new ModelAndView("jsonView", model);
    }

    try {
      model.put("ok", true);
      model.put("enfermedades", DDJJServiceUtil.getEnfermedadesActivas());
      return new ModelAndView("jsonView", model);

    } catch (Exception e) {
      _log.error("Error DDJJ_GET_ENFERMEDADES", e);
      model.put("ok", false);
      model.put("error", "Error interno");
      return new ModelAndView("jsonView", model);
    }
  }

  @RequestMapping(value = "/DDJJ_SET_ENVELOPE", method = RequestMethod.POST)
  public ModelAndView setEnvelope(
      @RequestParam("token") String token,
      @RequestParam("envelopeId") String envelopeId,
      @RequestParam(value="apiKey", required=false) String apiKey
  ) {
    Map<String, Object> model = new HashMap<String, Object>();

    if (apiKey == null || !API_KEY.equals(apiKey)) {
      model.put("ok", false);
      model.put("error", "No autorizado");
      return new ModelAndView("jsonView", model);
    }

    try {
      token = safe(token).trim();
      envelopeId = safe(envelopeId).trim();

      if (token.isEmpty() || envelopeId.isEmpty()) {
        model.put("ok", false);
        model.put("error", "token/envelopeId requeridos");
        return new ModelAndView("jsonView", model);
      }

      DDJJServiceUtil.setEnvelopeId(token, envelopeId);

      model.put("ok", true);
      model.put("token", token);
      model.put("envelopeId", envelopeId);
      return new ModelAndView("jsonView", model);

    } catch (Exception e) {
      _log.error("Error guardando envelopeId DDJJ", e);
      model.put("ok", false);
      model.put("error", e.getMessage());
      return new ModelAndView("jsonView", model);
    }
  }

  @RequestMapping(value = "/DDJJ_GET_BY_ENVELOPE", method = RequestMethod.POST)
  public ModelAndView getByEnvelope(
      @RequestParam("envelopeId") String envelopeId,
      @RequestParam(value="apiKey", required=false) String apiKey
  ) {
    Map<String, Object> model = new HashMap<String, Object>();

    if (apiKey == null || !API_KEY.equals(apiKey)) {
      model.put("ok", false);
      model.put("error", "No autorizado");
      return new ModelAndView("jsonView", model);
    }

    try {
      envelopeId = safe(envelopeId).trim();
      if (envelopeId.isEmpty()) {
        model.put("ok", false);
        model.put("error", "envelopeId requerido");
        return new ModelAndView("jsonView", model);
      }

      Map<String, Object> datos = DDJJServiceUtil.getByEnvelopeId(envelopeId);
      if (datos == null) {
        model.put("ok", false);
        model.put("error", "DDJJ no encontrada");
        return new ModelAndView("jsonView", model);
      }

      model.put("ok", true);
      model.put("datos", datos);
      return new ModelAndView("jsonView", model);

    } catch (Exception e) {
      _log.error("Error DDJJ_GET_BY_ENVELOPE", e);
      model.put("ok", false);
      model.put("error", "Error interno");
      return new ModelAndView("jsonView", model);
    }
  }

  @RequestMapping(value = "/DDJJ_SET_DOCUMENTO_FIRMADO", method = RequestMethod.POST)
  public ModelAndView setDocumentoFirmado(
      @RequestParam("token") String token,
      @RequestParam(value="pdf_ddjj", required=false) String pdfDdjj,
      @RequestParam(value="url_ddjj", required=false) String urlDdjj,
      @RequestParam(value="pdf_solicitud", required=false) String pdfSolicitud,
      @RequestParam(value="pdf_contrato", required=false) String pdfContrato,
      @RequestParam(value="apiKey", required=false) String apiKey
  ) {
    Map<String, Object> model = new HashMap<String, Object>();

    if (apiKey == null || !API_KEY.equals(apiKey)) {
      model.put("ok", false);
      model.put("error", "No autorizado");
      return new ModelAndView("jsonView", model);
    }

    try {
      token = safe(token).trim();
      pdfDdjj = safe(pdfDdjj).trim();
      urlDdjj = safe(urlDdjj).trim();
      pdfSolicitud = safe(pdfSolicitud).trim();
      pdfContrato = safe(pdfContrato).trim();

      if (token.isEmpty()) {
        model.put("ok", false);
        model.put("error", "token requerido");
        return new ModelAndView("jsonView", model);
      }

      DDJJServiceUtil.setDocumentoFirmado(
        token,
        pdfDdjj,
        urlDdjj,
        pdfSolicitud,
        pdfContrato
      );

      model.put("ok", true);
      model.put("token", token);
      model.put("estado", "firmada");
      model.put("pdf_ddjj", pdfDdjj);
      model.put("pdf_solicitud", pdfSolicitud);
      model.put("pdf_contrato", pdfContrato);

      return new ModelAndView("jsonView", model);

    } catch (Exception e) {
      _log.error("Error guardando documento firmado DDJJ", e);
      model.put("ok", false);
      model.put("error", e.getMessage());
      return new ModelAndView("jsonView", model);
    }
  }

  @RequestMapping(value = "/DDJJ_RESOLUCION_GUARDAR", method = RequestMethod.POST)
  public ModelAndView guardarMontoFinal(
      @RequestParam("token") String token,
      @RequestParam("monto_final") String montoFinal,
      @RequestParam(value="actor", required=false) String actor,
      @RequestParam(value="apiKey", required=false) String apiKey
  ) {
      Map<String, Object> model = new HashMap<String, Object>();

      if (apiKey == null || !API_KEY.equals(apiKey)) {
          model.put("ok", false);
          model.put("error", "No autorizado");
          return new ModelAndView("jsonView", model);
      }

      try {
          token = safe(token).trim();
          montoFinal = safe(montoFinal).trim();
          actor = safe(actor).trim();
          if (actor.isEmpty()) actor = "asesor";

          if (token.isEmpty()) {
              model.put("ok", false);
              model.put("error", "Token requerido");
              return new ModelAndView("jsonView", model);
          }

          if (montoFinal.isEmpty()) {
              model.put("ok", false);
              model.put("error", "Monto final requerido");
              return new ModelAndView("jsonView", model);
          }

          DDJJServiceUtil.guardarMontoFinal(token, montoFinal, actor);

          model.put("ok", true);
          model.put("token", token);
          model.put("monto_final", montoFinal);
          return new ModelAndView("jsonView", model);

      } catch (Exception e) {
          _log.error("Error guardando monto final DDJJ", e);
          model.put("ok", false);
          model.put("error", e.getMessage());
          return new ModelAndView("jsonView", model);
      }
  }
  
  /*
  @RequestMapping(value = "/DDJJ_RESOLUCION_RESPONDER", method = RequestMethod.POST)
  public ModelAndView responderResolucion(
      @RequestParam("token_respuesta") String tokenRespuesta,
      @RequestParam("respuesta") String respuesta,
      @RequestParam(value="apiKey", required=false) String apiKey
  ) {
    Map<String, Object> model = new HashMap<String, Object>();

    if (apiKey == null || !API_KEY.equals(apiKey)) {
      model.put("ok", false);
      model.put("error", "No autorizado");
      return new ModelAndView("jsonView", model);
    }

    try {
      tokenRespuesta = safe(tokenRespuesta).trim();
      respuesta = safe(respuesta).trim().toLowerCase();

      if (tokenRespuesta.isEmpty()) {
        model.put("ok", false);
        model.put("error", "token_respuesta requerido");
        return new ModelAndView("jsonView", model);
      }

      if (!"aceptada".equals(respuesta) && !"rechazada".equals(respuesta)) {
        model.put("ok", false);
        model.put("error", "Respuesta inválida");
        return new ModelAndView("jsonView", model);
      }

      Map<String, Object> out = DDJJServiceUtil.responderResolucion(tokenRespuesta, respuesta);

      model.put("ok", true);
      model.put("token", out.get("token"));
      model.put("respuesta", respuesta);
      return new ModelAndView("jsonView", model);

    } catch (Exception e) {
      _log.error("Error respondiendo resolución DDJJ", e);
      model.put("ok", false);
      model.put("error", e.getMessage());
      return new ModelAndView("jsonView", model);
    }
  }
  
  
  @RequestMapping(value = "/DDJJ_RESOLUCION_CONSULTAR", method = RequestMethod.POST)
  public ModelAndView consultarResolucion(
      @RequestParam("token_respuesta") String tokenRespuesta,
      @RequestParam(value="apiKey", required=false) String apiKey
  ) {
    Map<String, Object> model = new HashMap<String, Object>();

    if (apiKey == null || !API_KEY.equals(apiKey)) {
      model.put("ok", false);
      model.put("error", "No autorizado");
      return new ModelAndView("jsonView", model);
    }

    try {
      tokenRespuesta = safe(tokenRespuesta).trim();
      if (tokenRespuesta.isEmpty()) {
        model.put("ok", false);
        model.put("error", "token_respuesta requerido");
        return new ModelAndView("jsonView", model);
      }

      Map<String, Object> datos = DDJJServiceUtil.consultarResolucionPorTokenRespuesta(tokenRespuesta);
      if (datos == null) {
        model.put("ok", false);
        model.put("error", "Resolución no encontrada");
        return new ModelAndView("jsonView", model);
      }

      model.put("ok", true);
      model.put("datos", datos);
      return new ModelAndView("jsonView", model);

    } catch (Exception e) {
      _log.error("Error DDJJ_RESOLUCION_CONSULTAR", e);
      model.put("ok", false);
      model.put("error", e.getMessage());
      return new ModelAndView("jsonView", model);
    }
  }
  */
  @RequestMapping(value = "/DDJJ_SET_CONTRATO", method = RequestMethod.POST)
  public ModelAndView guardarContrato(
      @RequestParam("token") String token,
      @RequestParam("estado") String estado,
      @RequestParam(value="envelope_id", required=false) String envelopeId,
      @RequestParam(value="pdf_contrato", required=false) String pdfContrato,
      @RequestParam(value="url_contrato", required=false) String urlContrato,
      @RequestParam(value="apiKey", required=false) String apiKey
  ) {
      Map<String, Object> model = new HashMap<String, Object>();

      if (apiKey == null || !API_KEY.equals(apiKey)) {
          model.put("ok", false);
          model.put("error", "No autorizado");
          return new ModelAndView("jsonView", model);
      }

      try {
          token = safe(token).trim();
          estado = safe(estado).trim();
          envelopeId = safe(envelopeId).trim();
          pdfContrato = safe(pdfContrato).trim();
          urlContrato = safe(urlContrato).trim();

          if (token.isEmpty()) {
              model.put("ok", false);
              model.put("error", "Token requerido");
              return new ModelAndView("jsonView", model);
          }

          if (estado.isEmpty()) {
              model.put("ok", false);
              model.put("error", "Estado requerido");
              return new ModelAndView("jsonView", model);
          }

          DDJJServiceUtil.guardarContrato(token, estado, envelopeId, pdfContrato, urlContrato);

          model.put("ok", true);
          model.put("token", token);
          model.put("estado", estado);
          return new ModelAndView("jsonView", model);

      } catch (Exception e) {
          _log.error("Error guardando contrato DDJJ", e);
          model.put("ok", false);
          model.put("error", e.getMessage());
          return new ModelAndView("jsonView", model);
      }
  }
  
  private static String safe(String s) { return s == null ? "" : s; }
  private static String onlyDigits(String s) { return s == null ? "" : s.replaceAll("\\D+", ""); }
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