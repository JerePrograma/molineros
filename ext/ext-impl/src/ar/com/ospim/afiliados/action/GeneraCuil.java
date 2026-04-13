package ar.com.ospim.afiliados.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ar.com.ospim.procesaArchivos.services.ProcesaArchivosServiceImpl;

/** 
* Esta clase implementa metodos para generar y validar un número de 
* CUIT/CUIL (Codigo Unico de Identificación Tributaria/Código Unico de 
* Identificación Laboral). 
* En la práctica no se recomienda la utilización del metodo generar() 
* ya q si bien se generará un CUIT/CUIL logicamente valido, esto no 
* implica q el mismo corresponda a la persona en cuestion. El CUIT/CUIL 
* lo otorgan la AFIP o el ANSES (www.anses.gov.ar/cuil.htm [1]). 
* 
* @author Luciano Astor - luciano@beatbang.com.ar [2] 
* @version 1.0 - Mayo 2006 
**/ 

public final class GeneraCuil 
{ 
private static int dniStc; 
private static int xyStc; 
private static int digitoStc; 

public static void main(String args[]){	
	//System.out.println("CUIL: "+generar(27712034,'M'));
	ProcesaArchivosServiceImpl base=new ProcesaArchivosServiceImpl();
	Map<String, String> documentos=null;
	Map<String, String> documentosConCuil=new HashMap<String,String>();
	documentos=base.obtieneAfiliadosSinCuil();
	ArrayList<String> documento= new ArrayList<String>();
	documento.addAll(documentos.keySet());	
	for(String doc:documento){
		String sexo=documentos.get(doc);
		String cuil=generar(Integer.parseInt(doc), sexo.toCharArray()[0]);
		System.out.println("CUIL: "+cuil);
		documentosConCuil.put(doc,cuil);		
	}
	base.updateAfiliadosSinCuil(documentosConCuil);
	
}
/** 
* Metodo estatico para generar un CUIT/CUIL.
* 
* @param dniInt DNI como int 
* @param xyChar Sexo de la persona como char. 
* Masculino: m - Femenino: f - Para Personas Juridicas: cualquier otro caracter 
* 
* @return El CUIT/CUIL como String 
**/ 

public static String generar(int dniInt, char xyChar) 
{ 
if (xyChar == 'F' || xyChar == 'f') 
xyStc = 27; 
else 
if (xyChar == 'M' || xyChar == 'm') 
xyStc = 20; 
else 
xyStc = 30; 

dniStc = dniInt; 

calcular(); 
return String.valueOf(xyStc) + completar(String.valueOf(dniStc)) + String.valueOf(digitoStc);
//return formatear(); 
}

/** 
* Metodo estatico para generar un CUIT/CUIL. 
* 
* @param dniInt DNI como int 
* @param xyInt El prefijo del CUIT/CUIL como int 
* 
* @return El CUIT/CUIL como String 
**/ 

public static String generar(int dniInt, int xyInt) 
{ 
xyStc = xyInt; 
dniStc = dniInt; 
calcular();
return String.valueOf(xyStc) + completar(String.valueOf(dniStc)) + String.valueOf(digitoStc); 
//return formatear(); 
} 

/** 
* Metodo estatico para validar un numero de CUIT/CUIL. 

* 
* @param cuit N° de CUIT/CUIL como String 
* 
* @return Boolean: true si el CUIT/CUIL es correcto, false en caso contrario 
**/ 

public static boolean validar(String cuit) 
{ 
String xyStr, dniStr, digitoStr; 
int digitoTmp; 
int n = cuit.lastIndexOf("-"); 
xyStr = cuit.substring(0, 2); 
dniStr = cuit.substring(cuit.indexOf("-") + 1, n);
digitoStr = cuit.substring(n + 1, n + 2); 
if (xyStr.length() != 2 || dniStr.length() > 8 || digitoStr.length() != 1) 
return false; 
try 
{ 
xyStc = Integer.parseInt(xyStr); 
dniStc = Integer.parseInt(dniStr); 
digitoTmp = Integer.parseInt(digitoStr); 
}
catch (NumberFormatException e) 
{
return false; 
} 

if (xyStc != 20 && xyStc != 23 && xyStc != 24 && xyStc != 27 && xyStc != 30 && xyStc != 33 && xyStc != 34) 
return false; 

calcular(); 

if (digitoStc == digitoTmp && xyStc == Integer.parseInt(xyStr)) 
return true; 

return false; 

} 

/** 
* Metodo estatico que retorna el digito verificador de un CUIT/CUIL. 
* 
* @param xyInt El prefijo como int 
* @param dniInt El DNI como int 
* 
* @return El digito como int. Si se modifico el prefijo (por 23 o 33) 
* retorna 23x o 33x donde x es el digito 
**/ 

public static int digito(int xyInt, int dniInt) 
{ 
xyStc = xyInt; 
dniStc = dniInt; 
calcular(); 
if (xyInt == xyStc) 
return digitoStc; 
else 
return (xyStc * 10 + digitoStc); 
} 

/** 
* Metodo privado q da formato al CUIT como String 
**/ 

public static String formatear() 
{ 
return String.valueOf(xyStc) + "-" + completar(String.valueOf(dniStc)) + "-" + String.valueOf(digitoStc); 

} 

/** 
* Metodo privado q completa con ceros el DNI para q quede con 8 digitos 
**/ 

private static String completar(String dniStr) 
{ 
int n = dniStr.length(); 

while (n < 8) 
{ 
dniStr = "0" + dniStr; 
n = dniStr.length(); 
} 

return dniStr; 
} 

/** 
* Metodo privado q calcula el CUIT 
**/ 




	private static void calcular() 
{ 
long tmp1, tmp2; 
long acum = 0; 
int n = 2; 
tmp1 = xyStc * 100000000L + dniStc; 

for (int i = 0; i < 10; i++) 
{ 
tmp2 = tmp1 / 10; 
acum += (tmp1 - tmp2 * 10L) * n; 
tmp1 = tmp2; 
if (n < 7) 
n++; 
else 
n = 2; 
} 

n = (int)(11 - acum % 11); 

if (n == 10)
{ 
if (xyStc == 20 || xyStc == 27 || xyStc == 24) 
xyStc = 23; 
else 
xyStc = 33; 

/*No es necesario hacer la llamada recursiva a calcular(), 
*se puede poner el digito en 9 si el prefijo original era 
*23 o 33 o poner el dijito en 4 si el prefijo era 27*/ 

calcular(); 
} 
else 
{ 
if (n == 11) 
digitoStc = 0; 
else 
digitoStc = n; 
} 
} 

/** 
* Metodo toString(). 
* 
* @return Info acerca de la clase 
**/ 

public String toString() 
{ 
return "El principal objetivo de esta clase es implementar metodos" + "para generar y validar un numero de CUIT/CUIL.\n "; 
} 

}
