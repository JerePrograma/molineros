package ar.com.ospim.autorizaciones.beans;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

public class ReglaValidacion implements Serializable{

	public ReglaValidacion() {
		super();
		parametros = new ArrayList<ReglaValidacionParametros>();
	}

	private static final long serialVersionUID = -5547452058270036873L;

	protected String id;
	protected String formula;
	protected String codigoError;
	protected List<ReglaValidacionParametros> parametros;
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getFormula() {
		return formula;
	}
	public void setFormula(String formula) {
		this.formula = formula;
	}
	public String getCodigoError() {
		return codigoError;
	}
	public void setCodigoError(String codigoError) {
		this.codigoError = codigoError;
	}
	
	public List<ReglaValidacionParametros> getParametros() {
		return parametros;
	}
	public void setParametros(List<ReglaValidacionParametros> parametros) {
		this.parametros = parametros;
	}
	
	public static ReglaValidacion getMapping(ResultSet rs) throws SQLException {
		ReglaValidacion a = new ReglaValidacion();
		a.setId(rs.getString("id"));
		a.setFormula(rs.getString("formula"));
		a.setCodigoError(rs.getString("codigo_error"));
		
		return a;
	}
	
	private Object evaluar(Object entidad) {
		ScriptEngineManager manager = new ScriptEngineManager(); 
		ScriptEngine interprete = manager.getEngineByName("js"); 
		Object ret =null;
		try { 
			 
		      for(ReglaValidacionParametros p:parametros) {
		    	  interprete.put(p.getNombre(),p.getValor());
		      }
		      
		      interprete.put("__entidad",entidad); 
		      ret =  interprete.eval(getFormula());
	   }catch( Exception e ) {}
       return ret;
	}
	
	private Object evaluar(ArrayList<Object> entidades) {
		ScriptEngineManager manager = new ScriptEngineManager(); 
		ScriptEngine interprete = manager.getEngineByName("js"); 
		Object ret =null;
		try { 
			 
		      for(ReglaValidacionParametros p:parametros) {
		    	  interprete.put(p.getNombre(),p.getValor());
		      }
		      
		      if(entidades!=null && entidades.size()>0) {
		    	 for(int xi=0;xi<entidades.size();xi++) {
		    	   if( xi==0) {	 
		              interprete.put("__entidad",entidades.get(xi));
		    	   }else {
		    		   interprete.put("__entidad_"+ xi ,entidades.get(xi)); 
		    	   }
		    	 }  
		      }
		      ret =  interprete.eval(getFormula());
	   }catch( Exception e ) {}
       return ret;
	}
	
	
	public String evaluarPorError(Object entidad) {
		String ret="OK";
		try { 
		      Object b =  evaluar(entidad);
		      if("true".equalsIgnoreCase(b.toString())) {
		    	ret= getCodigoError();  
		      }

	   }catch( Exception e ) {}
       return ret;
	}
	
	public String evaluarPorOk(Object entidad) {
		String ret="OK";
		try { 
		      Object b =  evaluar(entidad);
		      if("false".equalsIgnoreCase(b.toString())) {
		    	ret=getCodigoError();  
		      }

	   }catch( Exception e ) {}
       return ret;
	}

	public String evaluarPorError(ArrayList<Object> entidad) {
		String ret="OK";
		try { 
		      Object b =  evaluar(entidad);
		      if("true".equalsIgnoreCase(b.toString())) {
		    	ret= getCodigoError();  
		      }

	   }catch( Exception e ) {}
       return ret;
	}
	
	public String evaluarPorOk(ArrayList<Object> entidad) {
		String ret="OK";
		try { 
		      Object b =  evaluar(entidad);
		      if("false".equalsIgnoreCase(b.toString())) {
		    	ret=getCodigoError();  
		      }

	   }catch( Exception e ) {}
       return ret;
	}

	
}

