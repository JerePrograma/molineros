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


	
	
public EquipoInterdisciplinario  getEquipoInterdisciplinarioFromRequest(RenderRequest  req,  EquipoInterdisciplinario equipoInterdisciplinario , User user ) {		
	
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
	String tipoTelefono = ParamUtil.getString(req,"tipotelefono");
	
	int provincia= ParamUtil.getInteger(req,"provincia");
	int localidad= ParamUtil.getInteger(req,"localidad");
	String calle= ParamUtil.getString(req,"calle");
	String numero= ParamUtil.getString(req,"numero");
	String dpto= ParamUtil.getString(req,"dpto");
	String barrio= ParamUtil.getString(req,"barrio");
	String piso = ParamUtil.getString(req,"piso");
	String tipoDomicilio = ParamUtil.getString(req,"tipoDomicilio ");
	String codigoPostal= ParamUtil.getString(req,"cod_postal");
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




	
}

