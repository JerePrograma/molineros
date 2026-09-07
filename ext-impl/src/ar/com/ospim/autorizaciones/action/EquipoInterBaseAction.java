package ar.com.ospim.autorizaciones.action;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.portlet.RenderRequest;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormatter;

import ar.com.ospim.autorizaciones.beans.EquipoInterdisciplinario;
import ar.com.ospim.autorizaciones.beans.FirmaAutorizante;
import ar.com.ospim.autorizaciones.beans.ReclamoPrestacional;

import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.PortletAction;


public class EquipoInterBaseAction  extends PortletAction {

	private Logger _log = Logger.getLogger(this.getClass());
	

public EquipoInterdisciplinario  getEquipoInterdisciplinarioFromRequest(HttpServletRequest req, EquipoInterdisciplinario equipoInterdisciplinario , User user ) {		
		
	
	
		Date fecha;				
		SimpleDateFormat formatoDePeriodo = new SimpleDateFormat("dd/MM/yyyy");		
		String fechaDia = ParamUtil.getString(req,"fechaequipoDia");
		String fechaMes = ParamUtil.getString(req,"fechaequipoMes") ;
		String fechaAnio = ParamUtil.getString(req,"fechaequipoAnio");
		String participantes = ParamUtil.getString(req,"participantes");
		String observacion  = ParamUtil.getString(req,"observacion");
		String estado = ParamUtil.getString(req,"estado");
		String cie10= ParamUtil.getString(req,"codigoCie10");
		String diagnosticoCie10= ParamUtil.getString(req,"diagnostico");
		String cuil_titular = ParamUtil.getString(req,"cuil");
 		int  inte = ParamUtil.getInteger(req,"inte");			
		String codAreaTelefono= ParamUtil.getString(req,"cod_area_telefono");
		String telefono= ParamUtil.getString(req,"telefono");
		String tipoTelefono= ParamUtil.getString(req,"tipo_telefono");
		int provincia= ParamUtil.getInteger(req,"provincia");
		int localidad= ParamUtil.getInteger(req,"localidad");
		String calle= ParamUtil.getString(req,"calle");
		String numero= ParamUtil.getString(req,"numero");
		String dpto= ParamUtil.getString(req,"dpto");
		String barrio= ParamUtil.getString(req,"barrio");
		String piso = ParamUtil.getString(req,"piso");
		String tipoDomicilio= ParamUtil.getString(req,"tipoDomicilio");
		String codigoPostal= ParamUtil.getString(req,"cod_postal");
		// email afiliado 
		String emailAfiliado= ParamUtil.getString(req,"email_afiliado");		
		String motivoCierre = ParamUtil.getString(req,"motivo");
		// carga de dictamenes
		String dictamenes[] = new String[6]; // soporta 6 tipos de dictamenes
		dictamenes[EquipoInterdisciplinario.DICTAMENES.ANTECEDENTES.ordinal() ]=ParamUtil.getString(req,"dictamenAntecedentes");		
		dictamenes[EquipoInterdisciplinario.DICTAMENES.MEDICOAUDITOR.ordinal() ]=ParamUtil.getString(req,"dictamenMedicoAuditor");		
		dictamenes[EquipoInterdisciplinario.DICTAMENES.ASISTENTESOCIAL.ordinal() ]=ParamUtil.getString(req,"dictamenAsistenteSocial");		
		dictamenes[EquipoInterdisciplinario.DICTAMENES.LICENCIADOKINESIOTERAPIAFISICA.ordinal() ]=ParamUtil.getString(req,"dictamenKinesiologia");		
		dictamenes[EquipoInterdisciplinario.DICTAMENES.LEGALES.ordinal() ]=ParamUtil.getString(req,"dictamenLegales");		
		dictamenes[EquipoInterdisciplinario.DICTAMENES.EQUIPOINTERDISCIPLINARIO.ordinal() ]=ParamUtil.getString(req,"dictamenEquipoInter");
		
		String firmaTipoDictamen = ParamUtil.getString(req,"firmaTipoDictamen");
		
		List<FirmaAutorizante> firmaAutorizante = null;
		if (firmaTipoDictamen!= null && !firmaTipoDictamen.isEmpty()) {// ME guardo la firma
			firmaAutorizante = new ArrayList<FirmaAutorizante>(); 
			FirmaAutorizante firma = new FirmaAutorizante(user.getUserId(), Integer.parseInt(firmaTipoDictamen),user.getScreenName());
			firmaAutorizante.add(firma);
		}
		
		
		
		
		fecha= null;
		
		try {
			fecha= formatoDePeriodo.parse(fechaDia + "/"
					+ (Integer.parseInt(fechaMes) + 1) + "/"
					+ fechaAnio);
		} catch (Exception e) {
			_log.debug("item: " + e.getMessage() );
		}
			
		try {
			if (estado.equals("CARGADO") ){
				motivoCierre="";		
			}
			equipoInterdisciplinario = new EquipoInterdisciplinario(cuil_titular,inte, fecha ,participantes,observacion ,estado,diagnosticoCie10, cie10, codAreaTelefono ,telefono,tipoTelefono , provincia,localidad ,calle, numero  ,dpto ,barrio, piso , tipoDomicilio , codigoPostal , dictamenes,emailAfiliado,motivoCierre,firmaAutorizante );	
		} catch (Exception e) {
			
		}
		
		return equipoInterdisciplinario  ;
	}


	
	
public EquipoInterdisciplinario  getEquipoInterdisciplinarioFromRequest(RenderRequest  req,  EquipoInterdisciplinario equipoInterdisciplinario , User user ) throws Exception {		
	
	Date fecha;				
	SimpleDateFormat formatoDePeriodo = new SimpleDateFormat("dd/MM/yyyy");		
	String fechaDia = ParamUtil.getString(req,"fechaequipoDia");
	String fechaMes = ParamUtil.getString(req,"fechaequipoMes") ;
	String fechaAnio = ParamUtil.getString(req,"fechaequipoAnio");
	String participantes = ParamUtil.getString(req,"participantes");
	String observacion  = ParamUtil.getString(req,"observacion");
	String estado = ParamUtil.getString(req,"estado");
	String cie10= ParamUtil.getString(req,"codigoCie10");
	String diagnosticoCie10= ParamUtil.getString(req,"diagnostico");
	String cuil_titular = ParamUtil.getString(req,"cuil");
	int  inte = ParamUtil.getInteger(req,"inte");

	String codAreaTelefono= ParamUtil.getString(req,"cod_area_telefono");
	String telefono= ParamUtil.getString(req,"telefono");
	String tipoTelefono = ParamUtil.getString(req,"tipo_telefono");
	
	int provincia= ParamUtil.getInteger(req,"provincia");
	int localidad= ParamUtil.getInteger(req,"localidad");
	String calle= ParamUtil.getString(req,"calle");
	String numero= ParamUtil.getString(req,"numero");
	String dpto= ParamUtil.getString(req,"dpto");
	String barrio= ParamUtil.getString(req,"barrio");
	String piso = ParamUtil.getString(req,"piso");
	String tipoDomicilio = ParamUtil.getString(req,"tipoDomicilio");
	String codigoPostal= ParamUtil.getString(req,"cod_postal");
	String emailAfiliado= ParamUtil.getString(req,"email_afiliado");
	String motivoCierre = ParamUtil.getString(req,"motivo");
	
	// carga de dictamenes
	String dictamenes[] = new String[6]; // soporta 6 tipos de dictamenes
	/*
	dictamenes[EquipoInterdisciplinario.DICTAMENES.ANTECEDENTES.ordinal() ]=ParamUtil.getString(req,"dictamenAntecedentes");		
	dictamenes[EquipoInterdisciplinario.DICTAMENES.MEDICOAUDITOR.ordinal() ]=ParamUtil.getString(req,"dictamenMedicoAuditor");		
	dictamenes[EquipoInterdisciplinario.DICTAMENES.ASISTENTESOCIAL.ordinal() ]=ParamUtil.getString(req,"dictamenAsistenteSocial");		
	dictamenes[EquipoInterdisciplinario.DICTAMENES.LICENCIADOKINESIOTERAPIAFISICA.ordinal() ]=ParamUtil.getString(req,"dictamenKinesiologia");		
	dictamenes[EquipoInterdisciplinario.DICTAMENES.LEGALES.ordinal() ]=ParamUtil.getString(req,"dictamenLegales");		
	dictamenes[EquipoInterdisciplinario.DICTAMENES.EQUIPOINTERDISCIPLINARIO.ordinal() ]=ParamUtil.getString(req,"dictamenEquipoInter");
	*/
	
	String bdAntecedentes = equipoInterdisciplinario != null
	        && equipoInterdisciplinario.getDictamen(EquipoInterdisciplinario.DICTAMENES.ANTECEDENTES) != null
	        ? equipoInterdisciplinario.getDictamen(EquipoInterdisciplinario.DICTAMENES.ANTECEDENTES)
	        : "";

	String bdMedicoAuditor = equipoInterdisciplinario != null
	        && equipoInterdisciplinario.getDictamen(EquipoInterdisciplinario.DICTAMENES.MEDICOAUDITOR) != null
	        ? equipoInterdisciplinario.getDictamen(EquipoInterdisciplinario.DICTAMENES.MEDICOAUDITOR)
	        : "";

	String bdAsistenteSocial = equipoInterdisciplinario != null
	        && equipoInterdisciplinario.getDictamen(EquipoInterdisciplinario.DICTAMENES.ASISTENTESOCIAL) != null
	        ? equipoInterdisciplinario.getDictamen(EquipoInterdisciplinario.DICTAMENES.ASISTENTESOCIAL)
	        : "";

	String bdKinesiologia = equipoInterdisciplinario != null
	        && equipoInterdisciplinario.getDictamen(EquipoInterdisciplinario.DICTAMENES.LICENCIADOKINESIOTERAPIAFISICA) != null
	        ? equipoInterdisciplinario.getDictamen(EquipoInterdisciplinario.DICTAMENES.LICENCIADOKINESIOTERAPIAFISICA)
	        : "";

	String bdLegales = equipoInterdisciplinario != null
	        && equipoInterdisciplinario.getDictamen(EquipoInterdisciplinario.DICTAMENES.LEGALES) != null
	        ? equipoInterdisciplinario.getDictamen(EquipoInterdisciplinario.DICTAMENES.LEGALES)
	        : "";

	String bdEquipoInter = equipoInterdisciplinario != null
	        && equipoInterdisciplinario.getDictamen(EquipoInterdisciplinario.DICTAMENES.EQUIPOINTERDISCIPLINARIO) != null
	        ? equipoInterdisciplinario.getDictamen(EquipoInterdisciplinario.DICTAMENES.EQUIPOINTERDISCIPLINARIO)
	        : "";
	
	        	dictamenes[EquipoInterdisciplinario.DICTAMENES.ANTECEDENTES.ordinal()] =
	        	    resolverDictamen(
			        req.getParameter("dictamenAntecedentes"),
			        req.getParameter("origDictamenAntecedentes"),
	        	        bdAntecedentes,
	        	        "Psicología",
	        	        EquipoInterdisciplinario.DICTAMENES.ANTECEDENTES.ordinal()
	        	    );

	        	dictamenes[EquipoInterdisciplinario.DICTAMENES.MEDICOAUDITOR.ordinal()] =
	        	    resolverDictamen(
			        req.getParameter("dictamenMedicoAuditor"),
			        req.getParameter("origDictamenMedicoAuditor"),
	        	        bdMedicoAuditor,
	        	        "Médico Auditor",
	        	        EquipoInterdisciplinario.DICTAMENES.MEDICOAUDITOR.ordinal()
	        	    );

	        	dictamenes[EquipoInterdisciplinario.DICTAMENES.ASISTENTESOCIAL.ordinal()] =
	        	    resolverDictamen(
			        req.getParameter("dictamenAsistenteSocial"),
			        req.getParameter("origDictamenAsistenteSocial"),
	        	        bdAsistenteSocial,
	        	        "Trabajadora Social",
	        	        EquipoInterdisciplinario.DICTAMENES.ASISTENTESOCIAL.ordinal()
	        	    );

	        	dictamenes[EquipoInterdisciplinario.DICTAMENES.LICENCIADOKINESIOTERAPIAFISICA.ordinal()] =
	        	    resolverDictamen(
			        req.getParameter("dictamenKinesiologia"),
			        req.getParameter("origDictamenKinesiologia"),
	        	        bdKinesiologia,
	        	        "Kinesiología",
	        	        EquipoInterdisciplinario.DICTAMENES.LICENCIADOKINESIOTERAPIAFISICA.ordinal()
	        	    );

	        	dictamenes[EquipoInterdisciplinario.DICTAMENES.LEGALES.ordinal()] =
	        	    resolverDictamen(
			        req.getParameter("dictamenLegales"),
			        req.getParameter("origDictamenLegales"),
	        	        bdLegales,
	        	        "Legales",
	        	        EquipoInterdisciplinario.DICTAMENES.LEGALES.ordinal()
	        	    );

	        	dictamenes[EquipoInterdisciplinario.DICTAMENES.EQUIPOINTERDISCIPLINARIO.ordinal()] =
	        	    resolverDictamen(
			        req.getParameter("dictamenEquipoInter"),
			        req.getParameter("origDictamenEquipoInter"),
	        	        bdEquipoInter,
	        	        "Equipo Interdisciplinario",
	        	        EquipoInterdisciplinario.DICTAMENES.EQUIPOINTERDISCIPLINARIO.ordinal()
	        	    );
	        	
	fecha= null;
	
	String firmaTipoDictamen = ParamUtil.getString(req,"firmaTipoDictamen");

	
	List<FirmaAutorizante> firmaAutorizante = null;
	if (firmaTipoDictamen!= null && !firmaTipoDictamen.isEmpty()) {// ME guardo la firma
		firmaAutorizante = new ArrayList<FirmaAutorizante>(); 
		FirmaAutorizante firma = new FirmaAutorizante(user.getUserId(), Integer.parseInt(firmaTipoDictamen),user.getScreenName());
		firmaAutorizante.add(firma);
	}
	
	
	try {
		fecha= formatoDePeriodo.parse(fechaDia + "/"
				+ (Integer.parseInt(fechaMes) + 1) + "/"
				+ fechaAnio);
	} catch (Exception e) {
		_log.debug("item: " + e.getMessage() );
	}
		
	try {
		 
		equipoInterdisciplinario = new EquipoInterdisciplinario(cuil_titular,inte, fecha ,participantes,observacion ,estado,diagnosticoCie10, cie10, codAreaTelefono ,telefono,tipoTelefono , provincia,localidad ,calle, numero  ,dpto ,barrio, piso,tipoDomicilio	,codigoPostal,dictamenes,emailAfiliado,motivoCierre ,firmaAutorizante); 
		
	} catch (Exception e) {
		
	}
	
	
	
	return equipoInterdisciplinario  ;
}


	private String resolverDictamen(String valorIngresado, String valorOriginalPantalla,
	        String valorActualBD, String nombreDictamen, int tipoDictamen) {
	
	    valorActualBD = valorActualBD != null ? valorActualBD : "";
	    // Un campo disabled o ausente no solicita borrar su contenido.
	    if (valorIngresado == null) {
	        return valorActualBD;
	    }
	    String ingresadoComparable = valorIngresado.replace("\r\n", "\n").replace("\r", "\n");
	    String originalComparable = valorOriginalPantalla != null
	        ? valorOriginalPantalla.replace("\r\n", "\n").replace("\r", "\n") : null;
	    String actualComparable = valorActualBD.replace("\r\n", "\n").replace("\r", "\n");
	
	    // No modifico el campo
	    if (ingresadoComparable.equals(originalComparable)) {
	        return valorActualBD;
	    }

	    // Lo modifico y nadie mas lo habia modificado
	    if (actualComparable.equals(originalComparable)) {
	        return valorIngresado;
	    }
	
	    // Ambos dejaron el mismo valor
	    if (ingresadoComparable.equals(actualComparable)) {
	        return valorActualBD;
	    }
	
	    // Ambos modificaron el mismo dictamen
	    throw new DictamenConcurrenteException(
	    	    nombreDictamen,
	    	    tipoDictamen,
	    	    valorActualBD,
	    	    valorIngresado
	    	);
	}

	public static class DictamenConcurrenteException
	        extends ar.com.ospim.autorizaciones.exceptions.DictamenConcurrenteException {

	    private static final long serialVersionUID = 1L;

	    public DictamenConcurrenteException(String nombreDictamen, int tipoDictamen,
	            String valorActualBD, String valorIngresado) {
	        super(nombreDictamen, tipoDictamen, valorActualBD, valorIngresado);
	    }
	}
	
}

