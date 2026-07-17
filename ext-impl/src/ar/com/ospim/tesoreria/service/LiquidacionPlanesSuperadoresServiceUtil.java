package ar.com.ospim.tesoreria.service;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.CalendarFactoryUtil;

import ar.com.ospim.afiliados.beans.Afiliado;
import ar.com.ospim.global.beans.Parentesco;
import ar.com.ospim.global.beans.Plan;
import ar.com.ospim.global.beans.Provincia;
import ar.com.ospim.tesoreria.beans.AjustePlanSuperador;
import ar.com.ospim.tesoreria.beans.PrecioPlanSuperador;
import ar.com.ospim.util.ConnectionHelper;
import ar.com.uoma.facturacion.Producto;

public class LiquidacionPlanesSuperadoresServiceUtil {
	
	private static Log _log = LogFactoryUtil
			.getLog(LiquidacionPlanesSuperadoresServiceUtil.class);

	private static LiquidacionPlanesSuperadoresServiceImpl instance = null;

	public static LiquidacionPlanesSuperadoresServiceImpl getInstance() {
		if (null == instance) {
			instance = new LiquidacionPlanesSuperadoresServiceImpl();
		}
		return instance;
	}
	
	public static List<PrecioPlanSuperador> searchPlanSuperador(PrecioPlanSuperador filtro)
			throws Exception{
	
	Connection connection = null;
	connection = ConnectionHelper.getConnection();
	List<PrecioPlanSuperador>precios=new ArrayList<PrecioPlanSuperador>();
	try{
	  precios =getInstance().searchPlanSuperador(filtro,connection);
	}catch(Exception e){
		throw e;
	}finally{
		if (connection != null) {
			 connection.close();
		 }
	}
	return precios;
}
	
	
	public static long addPrecioPlanSuperador(PrecioPlanSuperador precio, String screenName) throws Exception {

		long idPrecio = 0; 
		Connection connection = null;
		try {			
			connection = ConnectionHelper.getConnection();
			connection.setAutoCommit(false);
		    idPrecio=getInstance().addPrecioPlanSuperador(precio, screenName,connection);
		    
		    for(Plan p :precio.getPlanes()) {
		    	getInstance().addPrecioPlanSuperadorPlanes((int) idPrecio,p, connection);
		    }
		    
		    for(Parentesco pa :precio.getParentescos()) {
		    	getInstance().addPrecioPlanSuperadorParenstescos((int) idPrecio,pa, connection);
		    }
		    
		    for(Provincia pr :precio.getProvincias()) {
		    	getInstance().addPrecioPlanSuperadorProvincias((int) idPrecio,pr, connection);
		    }
		    
		    for(Producto pd :precio.getValores()) {
		    	getInstance().addPrecioPlanSuperadorValores((int) idPrecio,pd, connection);
		    }
		    
		    connection.commit();
	  } catch (Exception e) {
		  if(null!=connection){
			  connection.rollback();
			  throw e;
		  }			
	  } finally {
		 if (connection != null) {
			 connection.close();
		 }
	  }    
	  return idPrecio;
	}
	
	public static long updatePrecioPlanSuperador(PrecioPlanSuperador precio, String screenName) throws Exception {

		long idPrecio = 0; 
		Connection connection = null;
		try {			
			connection = ConnectionHelper.getConnection();
			connection.setAutoCommit(false);
		    idPrecio=getInstance().updatePrecioPlanSuperador(precio, screenName,connection);
		    
		    getInstance().deleteChildsPrecioPlanSuperador(precio.getId(),connection);
		    
		    
		    for(Plan p :precio.getPlanes()) {
		    	getInstance().addPrecioPlanSuperadorPlanes((int) idPrecio,p, connection);
		    }
		    
		    for(Parentesco pa :precio.getParentescos()) {
		    	getInstance().addPrecioPlanSuperadorParenstescos((int) idPrecio,pa, connection);
		    }
		    
		    for(Provincia pr :precio.getProvincias()) {
		    	getInstance().addPrecioPlanSuperadorProvincias((int) idPrecio,pr, connection);
		    }
		    
		    for(Producto pd :precio.getValores()) {
		    	getInstance().addPrecioPlanSuperadorValores((int) idPrecio,pd, connection);
		    }
		    
		    connection.commit();
	  } catch (Exception e) {
		  if(null!=connection){
			  connection.rollback();
			  throw e;
		  }			
	  } finally {
		 if (connection != null) {
			 connection.close();
		 }
	  }    
	  return idPrecio;
	}
	
	
public static long updateVigenciaPrecioPlanSuperador(PrecioPlanSuperador precio, String screenName) throws Exception {

		long idPrecio = 0; 
		Connection connection = null;
		try {			
			connection = ConnectionHelper.getConnection();
			connection.setAutoCommit(false);
		    idPrecio=getInstance().updatePrecioPlanSuperador(precio, screenName,connection);
		    connection.commit();
	  } catch (Exception e) {
		  if(null!=connection){
			  connection.rollback();
			  throw e;
		  }			
	  } finally {
		 if (connection != null) {
			 connection.close();
		 }
	  }    
	  return idPrecio;
}
	
	
public static PrecioPlanSuperador getPlanSuperador(Integer id)
			throws Exception{
	
	Connection connection = null;
	connection = ConnectionHelper.getConnection();
	PrecioPlanSuperador precio= new PrecioPlanSuperador();
	precio.setId(id);
	try{
	  List<PrecioPlanSuperador>precios =getInstance().searchPlanSuperador(precio,connection);
	  precio=precios.get(0);
	  List<Plan>planes=getInstance().searchPlanSuperadorPlanes(id,connection);
	  precio.setPlanes(planes);
	  
	  List<Parentesco>parentescos=getInstance().searchPlanSuperadorParentescos(id,connection);
	  precio.setParentescos(parentescos);
	  
	  List<Provincia>provs=getInstance().searchPlanSuperadorProvincias(id,connection);
	  precio.setProvincias(provs);
	  
	  List<Producto>valores=getInstance().searchPlanSuperadorValores(id,connection);
	  precio.setValores(valores);
	  	  
	}catch(Exception e){
		throw e;
	}finally{
		if (connection != null) {
			 connection.close();
		 }
	}
	return precio;
}
	
public static void deletePlanSuperadorPrecio(Integer id)
		throws Exception{

Connection connection = null;
connection = ConnectionHelper.getConnection();
  try{
      getInstance().deleteChildsPrecioPlanSuperador(id,connection);
      getInstance().deletePrecioPlanSuperador(id,connection);
  	  
    }catch(Exception e){
	  throw e;
    }finally{
	   if (connection != null) {
		 connection.close();
	   }
    }

}


public static List<String> verificarPrecioPlanSuperador(PrecioPlanSuperador m) throws Exception{
  Boolean conProvincia=false;
  Boolean conParentesco=false;
  Boolean conPlanes=false;
  List<String> mensaje = new ArrayList<String>();
  PrecioPlanSuperador filtro= new PrecioPlanSuperador();
  filtro.setFechaDesde(m.getFechaDesde());
  filtro.setFechaHasta(m.getFechaHasta());
  Boolean inconsistencia=false;
  for (Plan pl : m.getPlanes()) {
	  conPlanes=true;
      filtro.setPlanes(new ArrayList<Plan>());
	  filtro.getPlanes().add(pl);
	  for(Parentesco pa:m.getParentescos()) {
		 conParentesco=true; 
		filtro.setParentescos(new ArrayList<Parentesco>());  
		filtro.getParentescos().add(pa);  
		for(Provincia pr:m.getProvincias()) {
			conProvincia=true;
			filtro.getProvincias().add(pr);
			mensaje = LiquidacionPlanesSuperadoresServiceUtil.verificarPrecioPlanSuperador(m, filtro);
			if(!mensaje.isEmpty()) {
				inconsistencia=true;
				break;
			}
		}
		if(inconsistencia || conProvincia) break;
		mensaje = LiquidacionPlanesSuperadoresServiceUtil.verificarPrecioPlanSuperador(m, filtro);
		if(!mensaje.isEmpty()) {
			inconsistencia=true;
			break;
		}
	  }
	  if(inconsistencia || conParentesco) break;
	  mensaje = LiquidacionPlanesSuperadoresServiceUtil.verificarPrecioPlanSuperador(m, filtro);
	  if(!mensaje.isEmpty()) {
		 inconsistencia=true;
		 break;
	  }
  }
  if(!conPlanes) {
	 mensaje = LiquidacionPlanesSuperadoresServiceUtil.verificarPrecioPlanSuperador(m, filtro);
  }
  return mensaje;
}

public static List<String> verificarPrecioPlanSuperador(PrecioPlanSuperador precio, PrecioPlanSuperador filtro) throws Exception {

	List<String> ret = new ArrayList<String>();
	Calendar fechaHta = CalendarFactoryUtil.getCalendar();
    fechaHta.set(2999, 0, 1);
    
	Boolean errorEdad=false;
	Boolean errorFecha=false;
    Integer edadHta=9999;
    
	List<PrecioPlanSuperador>list = searchPlanSuperador(filtro);
	if(filtro.getFechaHasta()==null) {
		filtro.setFechaHasta(fechaHta.getTime());
	}
	filtro.setFechaDesde(filtro.getFechaHasta());
	list.addAll(searchPlanSuperador(filtro));	
	
	Date fechaPrecioHasta=precio.getFechaHasta();
	if(fechaPrecioHasta==null) fechaPrecioHasta=fechaHta.getTime();
	
	for(PrecioPlanSuperador l:list) {
		if(precio.getId()!=null && precio.getId().equals(l.getId())) continue;
		
		if(l.getFechaHasta()==null) l.setFechaHasta(fechaHta.getTime());
		if(l.getEdadHasta()==null || l.getEdadHasta()==0) l.setEdadHasta(edadHta);
		
		errorEdad=false;
		errorFecha=false;
		
		if(!errorFecha && l.getFechaDesde()!=null && (((l.getFechaDesde().compareTo(precio.getFechaDesde()) <=0) && 
				l.getFechaHasta().compareTo(precio.getFechaDesde())>0) ||
				(precio.getFechaDesde().compareTo(l.getFechaDesde())<=0 && 
				  fechaPrecioHasta.compareTo(l.getFechaDesde())>=0)) ) {
			errorFecha=true;
		}
		/*
		else if(!errorFecha && l.getFechaDesde()!= null && l.getFechaHasta()!=null &&  precio.getFechaDesde().compareTo(l.getFechaDesde())<=0 &&  precio.getFechaHasta().compareTo(l.getFechaDesde())>=1) {
			errorFecha=true;
		}
		*/
		
		if(!errorEdad && l.getEdadDesde()!= null &&
				((l.getEdadDesde()<= precio.getEdadDesde() && l.getEdadHasta()>=precio.getEdadDesde()) || 
					(precio.getEdadDesde()<=l.getEdadDesde() && precio.getEdadHasta()>=l.getEdadDesde()))) {
				errorEdad=true;
		}
		/*
		  else if(!errorEdad && l.getEdadDesde()!= null && l.getEdadHasta()!=null &&  precio.getEdadDesde()<=l.getEdadDesde() && precio.getEdadHasta()>=l.getEdadDesde()) {
		 
				errorEdad=true;
		}
		*/
		if(errorEdad && errorFecha) {
			ret.add("Verifique Id: "  +l.getId().toString() + ". Puede haber una superposición de Fechas/Edades") ;
			break;
		}
	}
	
	
	
	return ret;
}

public static long addAjustePlanSuperador(AjustePlanSuperador precio, String screenName) throws Exception {

	long idPrecio = 0; 
	Connection connection = null;
	try {			
		connection = ConnectionHelper.getConnection();
		connection.setAutoCommit(false);
	    idPrecio=getInstance().addAjustePlanSuperador(precio, screenName,connection);
	    
	    for(Plan p :precio.getPlanes()) {
	    	getInstance().addAjustePlanSuperadorPlanes((int) idPrecio,p, connection);
	    }
	    
	    for(Parentesco pa :precio.getParentescos()) {
	    	getInstance().addAjustePlanSuperadorParenstescos((int) idPrecio,pa, connection);
	    }
	    
	    for(Provincia pr :precio.getProvincias()) {
	    	getInstance().addAjustePlanSuperadorProvincias((int) idPrecio,pr, connection);
	    }
	    
	    for(Afiliado pd :precio.getAfiliados()) {
	    	getInstance().addAjustePlanSuperadorCuiles((int) idPrecio,pd, connection);
	    }
	    
	    connection.commit();
  } catch (Exception e) {
	  if(null!=connection){
		  connection.rollback();
		  throw e;
	  }			
  } finally {
	 if (connection != null) {
		 connection.close();
	 }
  }    
  return idPrecio;
}

public static long updateAjustePlanSuperador(AjustePlanSuperador precio, String screenName) throws Exception {

	long idPrecio = 0; 
	Connection connection = null;
	try {			
		connection = ConnectionHelper.getConnection();
		connection.setAutoCommit(false);
	    idPrecio=getInstance().updateAjustePlanSuperador(precio, screenName,connection);
	    
	    getInstance().deleteChildsAjustePlanSuperador(precio.getId(),connection);
	    
	    
	    for(Plan p :precio.getPlanes()) {
	    	getInstance().addAjustePlanSuperadorPlanes((int) idPrecio,p, connection);
	    }
	    
	    for(Parentesco pa :precio.getParentescos()) {
	    	getInstance().addAjustePlanSuperadorParenstescos((int) idPrecio,pa, connection);
	    }
	    
	    for(Provincia pr :precio.getProvincias()) {
	    	getInstance().addAjustePlanSuperadorProvincias((int) idPrecio,pr, connection);
	    }
	    
	    for(Afiliado pd :precio.getAfiliados()) {
	    	getInstance().addAjustePlanSuperadorCuiles((int) idPrecio,pd, connection);
	    }
	    
	    connection.commit();
  } catch (Exception e) {
	  if(null!=connection){
		  connection.rollback();
		  throw e;
	  }			
  } finally {
	 if (connection != null) {
		 connection.close();
	 }
  }    
  return idPrecio;
}

public static List<AjustePlanSuperador> searchPlanSuperadorAjustes(AjustePlanSuperador filtro)
		throws Exception{

Connection connection = null;
connection = ConnectionHelper.getConnection();
List<AjustePlanSuperador>precios=new ArrayList<AjustePlanSuperador>();
try{
  precios =getInstance().searchPlanSuperadorAjuste(filtro,connection);
}catch(Exception e){
	throw e;
}finally{
	if (connection != null) {
		 connection.close();
	 }
}
return precios;
}

public static void deletePlanSuperadorAjuste(Integer id)
		throws Exception{

Connection connection = null;
connection = ConnectionHelper.getConnection();
  try{
      getInstance().deleteChildsAjustePlanSuperador(id,connection);
      getInstance().deleteAjustePlanSuperador(id,connection);
  	  
    }catch(Exception e){
	  throw e;
    }finally{
	   if (connection != null) {
		 connection.close();
	   }
    }

}

public static AjustePlanSuperador getPlanSuperadorAjuste(Integer id)
		throws Exception{

Connection connection = null;
connection = ConnectionHelper.getConnection();
AjustePlanSuperador precio= new AjustePlanSuperador();
precio.setId(id);
try{
  List<AjustePlanSuperador>precios =getInstance().searchPlanSuperadorAjuste(precio,connection);
  precio=precios.get(0);
  List<Plan>planes=getInstance().searchAjustePlanes(id,connection);
  precio.setPlanes(planes);
  
  List<Parentesco>parentescos=getInstance().searchAjusteParentescos(id,connection);
  precio.setParentescos(parentescos);
  
  List<Provincia>provs=getInstance().searchAjusteProvincias(id,connection);
  precio.setProvincias(provs);
  
  List<Afiliado>valores=getInstance().searchAjusteAfiliados(id,connection);
  precio.setAfiliados(valores);
  	  
}catch(Exception e){
	throw e;
}finally{
	if (connection != null) {
		 connection.close();
	 }
}
return precio;
}


public static List<Afiliado> getBusquedaGrupoFliar(String cuil_titular)
		throws Exception {
	return getInstance().getBusquedaGrupoFliar(cuil_titular);
}

public static List<PrecioPlanSuperador> cotizar(Integer plan_id, Integer provincia_id, Date fecha ,String[]grupoFliar)
		throws Exception{

Connection connection = null;
connection = ConnectionHelper.getConnection();
List<PrecioPlanSuperador>precios=new ArrayList<PrecioPlanSuperador>();
try{
  precios =getInstance().cotizar(plan_id,provincia_id,fecha ,grupoFliar,connection);
}catch(Exception e){
	throw e;
}finally{
	if (connection != null) {
		 connection.close();
	 }
}
return precios;
}

public static Map<String,Integer> getPlanesEquivalentes()
		throws Exception {
	return getInstance().getPlanesEquivalencias();
}


public static List<AjustePlanSuperador> getAjustesPersonalizables(Integer plan_id, Integer provincia_id, Date fecha ,String[]grupoFliar)
		throws Exception{

Connection connection = null;
connection = ConnectionHelper.getConnection();
List<AjustePlanSuperador>ajustes=new ArrayList<AjustePlanSuperador>();
try{
  ajustes =getInstance().getAjustesPersonalizables(plan_id,provincia_id,fecha ,grupoFliar,connection);
}catch(Exception e){
	throw e;
}finally{
	if (connection != null) {
		 connection.close();
	 }
}
return ajustes;
}
}
