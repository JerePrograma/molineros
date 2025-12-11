package ar.com.ospim.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;



public class ListUtils {
	
@SuppressWarnings("unchecked")
public static List traeCoincidenciasDeLista(List lista, String descripcion, String id){
		
		List result=null;
		try{
		if(null!=id&&id.length()>0){
			result=new ArrayList();			
				
				for(int i=0;i<lista.size();i++){
					Object o= lista.get(i);
					Class cls=o.getClass();
					Method meth=cls.getMethod("getId");					
					if(String.valueOf(meth.invoke(o,new Object[]{})).equals(id.trim()) && result.size()<20){
						result.add(o);
					}
				}
			
			
		}else if(null!=descripcion&&descripcion.length()>0){
			result=new ArrayList();
			for(int j=0;j<lista.size();j++){
				Object o= lista.get(j);
				Class cls=o.getClass();
				Method meth=cls.getMethod("getDescripcion");
				if(((String)meth.invoke(o,new Object[]{})).trim().toUpperCase().contains(descripcion.trim().toUpperCase()) && result.size()<20){
					result.add(o);
				}
			}
		}
		}catch(NumberFormatException e){
			//DO NOTHING?
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return result!=null?result:lista;		
	}



public static List traeCoincidenciasDeListaMayusculas(List lista, String descripcion, String id){
	
	List result=null;
	try{
	if(null!=id&&id.length()>0){
		result=new ArrayList();			
			
			for(int i=0;i<lista.size();i++){
				Object o= lista.get(i);
				Class cls=o.getClass();
				Method meth=cls.getMethod("getId");					
				if(String.valueOf(meth.invoke(o,new Object[]{})).equals(id.trim().toUpperCase()) && result.size()<20){
					result.add(o);
				}
			}
		
		
	}else if(null!=descripcion&&descripcion.length()>0){
		result=new ArrayList();
		for(int j=0;j<lista.size();j++){
			Object o= lista.get(j);
			Class cls=o.getClass();
			Method meth=cls.getMethod("getDescripcion");
			if(((String)meth.invoke(o,new Object[]{})).trim().toUpperCase().contains(descripcion.trim().toUpperCase()) && result.size()<20){
				result.add(o);
			}
		}
	}
	}catch(NumberFormatException e){
		//DO NOTHING?
	} catch (SecurityException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (NoSuchMethodException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (IllegalArgumentException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (IllegalAccessException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (InvocationTargetException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
	return result!=null?result:lista;		
  }



}