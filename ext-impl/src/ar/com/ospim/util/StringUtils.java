package ar.com.ospim.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/*
 */

public class StringUtils {

	public static final boolean DETRAS = true;
	public static final boolean DELANTE = false;

	public static final char SPACE = ' ';
	public static final char ZERO = '0';

	static final int ARRAYS_LENGTH = 200;

	/** 200 espacios en blanco. */
	static final char[] SPACES_ARRAY = new char[ARRAYS_LENGTH];

	/** 200 ceros. */
	static final char[] ZEROS_ARRAY = new char[ARRAYS_LENGTH];

	/**
	 * Comprueba si una cadena no esta vacia o compuesta de espacios en blanco.
	 * 
	 * @param valor
	 *            cadena que comprobar
	 * 
	 * @return si la cadena no esta vacia o compuesta de espacios en blanco
	 */
	public static boolean checkNotEmpty(String valor) {
		return valor != null && valor.trim().length() > 0;
	}

	/**
	 * indica si un objeto es distinto de null
	 * 
	 * @param valor
	 * @return
	 */
	public static boolean checkNotEmpty(Object valor) {
		return valor != null;
	}

	/**
	 * Comprueba si una cadenaesta vacia a o compuesta de espacios en blanco.
	 * 
	 * @param valor
	 *            cadena que comprobar
	 * 
	 * @return si la cadena esta vacia compuesta de espacios en blanco
	 */
	public static boolean checkEmpty(String valor) {
		return !checkNotEmpty(valor);
	}

	/**
	 * Comprueba si un objeto esta vacio
	 * 
	 * @param valor
	 * @return
	 */
	public static boolean checkEmpty(Object valor) {
		return !checkNotEmpty(valor);
	}

	/**
	 * 
	 * @param t
	 *            Excepcion
	 * @return String con la traza de la except
	 */
	public static String getStackTrace(Exception t) {
		java.io.StringWriter s = new java.io.StringWriter();
		t.printStackTrace(new java.io.PrintWriter(s));
		return s.toString();
	}

	/**
	 * Crea una copia de cadena y con el tamano especificado; si la cadena es
	 * mayor que el tamano especificado, devuelve los <code>size</code> primeros
	 * caracteres; en caso contrario, completa con el caracter especificado al
	 * final de la cadena hasta llegar al tamano especificado.
	 * 
	 * @param cadena
	 *            la cadena original
	 * @param size
	 *            el tamano en caracteres de la copia
	 * @param relleno
	 *            el caracter de relleno para completar el tamano
	 * @param detras
	 *            si hay que completar con " " detras (true) o delante (false)
	 *            de la cadena original
	 * @return la copia con el nuevo tamano
	 */
	public static final String changeSize(String cadena, int size,
			char relleno, boolean detras) {
		// crea los arrays
		for (int i = 0; i < ARRAYS_LENGTH; i++) {
			SPACES_ARRAY[i] = SPACE;
			ZEROS_ARRAY[i] = ZERO;
		}

		String res = null;
		if (cadena == null)
			return null;

		int tam = cadena.length();

		if (size == tam) {
			return cadena;
		} else if (size > tam) {
			StringBuffer buffer = new StringBuffer(size);
			int dif = size - tam;

			char[] blancos;
			boolean isLessThan = dif < ARRAYS_LENGTH;

			if (isLessThan && relleno == SPACE) {
				blancos = SPACES_ARRAY;
			} else if (isLessThan && relleno == ZERO) {
				blancos = ZEROS_ARRAY;
			} else {
				blancos = new char[dif];
				for (int i = 0; i < dif; i++) {
					blancos[i] = relleno;
				}
			}

			if (detras)
				buffer.append(cadena).append(blancos, 0, dif);
			else
				buffer.append(blancos, 0, dif).append(cadena);
			return buffer.toString();
		} else {
			if (detras)
				res = cadena.substring(0, size);
			else
				res = cadena.substring(tam - size);
		}

		return res;
	}

	public static String getValueOrNull(String o) {
		if (checkEmpty(o)) {
			return null;
		} else {
			return o;
		}
	}

	public static String getValueOrEmpty(Object o) {
		if (o != null) {
			return o.toString();
		} else {
			return "";
		}
	}

	public static Integer getIntegerOrNull(String o) {
		if (checkEmpty(o)) {
			return null;
		}
		try {
			return Integer.valueOf(o);
		} catch (NumberFormatException e) {
			return null;
		}
	}

	public static Long getLongOrNull(String o) {
		if (checkEmpty(o)) {
			return null;
		}
		return Long.valueOf(o);
	}

	public static Double getDoubleOrNull(String o) {
		if (checkEmpty(o)) {
			return null;
		}
		return Double.valueOf(o);
	}

	public static String getCuilMask(String cuil) {
		StringBuffer cuilMasked = new StringBuffer("");
		if (cuil.trim().length() == 11) {
			cuilMasked.append(cuil.substring(0, 2));
			cuilMasked.append("-");
			cuilMasked.append(cuil.substring(2, 10));
			cuilMasked.append("-");
			cuilMasked.append(cuil.substring(10, 11));
		}
		return cuilMasked.toString();
	}

	public static String replaceAcutesAndEnies(String cadena) {
		String aux = cadena.replace("Ñ", "N").replace("ñ", "n")
				.replace("á", "a").replace("é", "e").replace("í", "i")
				.replace("ó", "o").replace("ú", "u").replace("Á", "A")
				.replace("É", "E").replace("Í", "I").replace("Ó", "O")
				.replace("Ú", "U");
		return aux;
	}
	public static String leftPad(String originalString, int length,
			char padCharacter) {
		String paddedString = originalString;
		while (paddedString.length() < length) {
			paddedString = padCharacter + paddedString;
		}
		return paddedString;
	}

	public static String encodeURIComponent(String originalStr){
	    String encodeString= null;
	    try{
	      encodeString = URLEncoder.encode(originalStr, "UTF-8") 
	                         .replaceAll("\\+", "%20")
	                         .replaceAll("\\%21", "!")
	                         .replaceAll("\\%27", "'")
	                         .replaceAll("\\%28", "(")
	                         .replaceAll("\\%29", ")")
	                         .replaceAll("\\%7E", "~");

	    }catch (UnsupportedEncodingException e)    {
	    //  System.out.println("Exception while encoding");
	    }

	    return encodeString;
   }
}