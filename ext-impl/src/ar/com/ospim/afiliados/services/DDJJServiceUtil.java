package ar.com.ospim.afiliados.services;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class DDJJServiceUtil {

  private static DDJJServiceImpl instance = null;

  private static DDJJServiceImpl getInstance() {
    if (instance == null) instance = new DDJJServiceImpl();
    return instance;
  }

  public static void cambiarEstado(String token, String estado) throws Exception {
    getInstance().cambiarEstado(token, estado);
  }

  public static Map<String, Object> getByToken(String token) throws Exception {
    return getInstance().getByToken(token);
  }

  public static long guardarPaso1(
      String token, String plan,
      String nombre, String apellido, String email, String dni, String cuil, String codArea, String telefono,
      String fechaNacimiento, String sexo, String nacionalidad, String estadoCivil,
      String calle, String numero, String piso, String dpto, String barrio, String localidad, String provincia, String cp, String montoEstimado, BigDecimal sueldoBruto,
      String laboralCuit, String laboralRazonSocial, String laboralFechaIngreso,
      String grupoFamiliarJson,
      String usuario
  ) throws Exception {
    return getInstance().guardarPaso1(
        token, plan,
        nombre, apellido, email, dni, cuil, codArea, telefono,
        fechaNacimiento, sexo, nacionalidad, estadoCivil,
        calle, numero, piso, dpto, barrio, localidad, provincia, cp, montoEstimado, sueldoBruto,
        laboralCuit, laboralRazonSocial, laboralFechaIngreso,
        grupoFamiliarJson, usuario
    );
  }

  public static long guardarSalud(
      String token,
      String saludJson,
      String obsSintomas,
      String obsFechaAprox,
      String obsDetalleTratamiento,
      String obsOtras,
      String obsInstituciones,
      boolean finalizar
  ) throws Exception {
    return getInstance().guardarSalud(
        token,
        saludJson,
        obsSintomas,
        obsFechaAprox,
        obsDetalleTratamiento,
        obsOtras,
        obsInstituciones,
        finalizar
    );
  }

  public static long guardarSaludV2(
      String token,
      String saludJson,
      String observacionesJson,
      boolean finalizar
  ) throws Exception {
    return getInstance().guardarSaludV2(
        token,
        saludJson,
        observacionesJson,
        finalizar
    );
  }

  public static List<Map<String, Object>> getEnfermedadesActivas() throws Exception {
    return getInstance().getEnfermedadesActivas();
  }

  public static void setEnvelopeId(String token, String envelopeId) throws Exception {
    getInstance().setEnvelopeId(token, envelopeId);
  }

  public static Map<String, Object> getByEnvelopeId(String envelopeId) throws Exception {
    return getInstance().getByEnvelopeId(envelopeId);
  }

  public static void setDocumentoFirmado(String token, String pdfDdjj, String urlDdjj, String pdfSolicitud, String pdfContrato) throws Exception {
    getInstance().setDocumentoFirmado(token, pdfDdjj, urlDdjj, pdfSolicitud, pdfContrato);
  }

  public static void guardarMontoFinal(String token, String montoFinal, String actor) throws Exception {
    getInstance().guardarMontoFinal(token, montoFinal, actor);
  }

  /*
  public static Map<String, Object> responderResolucion(String tokenRespuesta, String respuesta) throws Exception {
    return getInstance().responderResolucion(tokenRespuesta, respuesta);
  }

  public static Map<String, Object> consultarResolucionPorTokenRespuesta(String tokenRespuesta) throws Exception {
    return getInstance().consultarResolucionPorTokenRespuesta(tokenRespuesta);
  }
*/
  public static void guardarContrato(
      String token,
      String estado,
      String envelopeId,
      String pdfContrato,
      String urlContrato
  ) throws Exception {
    getInstance().guardarContrato(token, estado, envelopeId, pdfContrato, urlContrato);
  }

  public static void marcarContratoFirmado(
      String envelopeId,
      String pdfContrato,
      String urlContrato
  ) throws Exception {
    getInstance().marcarContratoFirmado(envelopeId, pdfContrato, urlContrato);
  }
}