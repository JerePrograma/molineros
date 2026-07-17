package ar.com.ospim.afiliados.services;

import java.math.BigDecimal;
import java.util.Map;

public class AfiliacionesServiceUtil {

  private static AfiliacionesServiceImpl instance = null;

  public static AfiliacionesServiceImpl getInstance() {
    if (instance == null) instance = new AfiliacionesServiceImpl();
    return instance;
  }

  public static Map<String, Object> guardarSolicitud(
		    Long idInteresado,
    	    Long idSolicitud,
		    String nombre,
		    String apellido,
		    Integer edad,
		    String fechaNacimiento,
		    String dni,
		    String codigoArea,
		    String telefono,
		    String provincia,
		    String plan,
		    String email,
		    Boolean relacionDependencia,
		    Boolean tienePareja,
		    Integer edadPareja,
		    Boolean tieneHijos,
		    Integer cantidadHijos21,
		    Integer cantidadHijos25,
		    BigDecimal sueldoBruto,
		    String montoEstimado,
		    Boolean esMolinero,
		    boolean generarDdjj,
		    String usuario
		) throws Exception {

		  return getInstance().guardarSolicitud(
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
		      relacionDependencia,
		      tienePareja,
		      edadPareja,
		      tieneHijos,
		      cantidadHijos21,
		      cantidadHijos25,
		      sueldoBruto,
		      montoEstimado,
		      esMolinero,
		      generarDdjj,
		      usuario
		  );
  }
  
  public static void guardarPdfSolicitud(
		    Long idSolicitud,
		    String pdfSolicitud,
		    String urlSolicitud,
		    String modiUsr
		) throws Exception {
		    getInstance().guardarPdfSolicitud(idSolicitud, pdfSolicitud, urlSolicitud, modiUsr);
		}
  
  public static void guardarPdfDdjj(
		    String token,
		    String pdfUrl
		) throws Exception {
		    getInstance().guardarPdfDdjj(token, pdfUrl);
		}
  
  public static void crearDdjjPorSolicitud(
		    Long idSolicitud,
		    String token,
		    String ddjjUrl
		) throws Exception {
		    getInstance().crearDdjjPorSolicitud(idSolicitud, token, ddjjUrl);
		}
  
  public static Map<String, Object> cotizarPlanesLuma(
		    Integer edad,
		    String provincia,
		    Boolean tienePareja,
		    Integer edadPareja,
		    Boolean tieneHijos,
		    Integer cantidadHijos21,
		    Integer cantidadHijos25
		) throws Exception {
		    return getInstance().cotizarPlanesLuma(
		        edad,
		        provincia,
		        tienePareja,
		        edadPareja,
		        tieneHijos,
		        cantidadHijos21,
		        cantidadHijos25
		    );
		}
}