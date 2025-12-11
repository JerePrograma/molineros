package ar.com.ospim.login.coordenadas.services;

import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import ar.com.ospim.login.coordenadas.beans.TarjetaCoordenadas;
import ar.com.ospim.webservice.service.AfiliadoServiceImpl;

public class CoordenadasServiceUtil {
	private static final String COORDENADA1_X = "c1x";
	private static final String COORDENADA1_Y = "c1y";
	private static final String COORDENADA2_X = "c2x";
	private static final String COORDENADA2_Y = "c2y";
	private static final String VALOR_COORDENADA1 = "valor1";
	private static final String VALOR_COORDENADA2 = "valor2";
	
	
	private static CoordenadasServiceImpl instance = null;

	public static CoordenadasServiceImpl getInstance() {
		if (null == instance) {
			instance = new CoordenadasServiceImpl();
		}
		return instance;
	}

	public static int getCoord2Y(HttpSession session) {
		if (session.getAttribute(COORDENADA2_Y) == null) {
			return 0;
		}
		return ((Long) session.getAttribute(COORDENADA2_Y)).intValue();
	}

	public static int getCoord2X(HttpSession session) {
		if (session.getAttribute(COORDENADA2_X) == null) {
			return 0;
		}
		return ((Long) session.getAttribute(COORDENADA2_X)).intValue();
	}

	public static int getCoord1Y(HttpSession session) {
		if (session.getAttribute(COORDENADA1_Y) == null) {
			return 0;
		}
		return ((Long) session.getAttribute(COORDENADA1_Y)).intValue();
	}

	public static int getCoord1X(HttpSession session) {
		if (session.getAttribute(COORDENADA1_X) == null) {
			return 0;
		}
		return ((Long) session.getAttribute(COORDENADA1_X)).intValue();
	}

	public static void setearCoordenadas(RenderRequest req) {
		long coordenada1x = getPosicionCoordenada();
		long coordenada1y = getPosicionCoordenada();
		long coordenada2x = getPosicionCoordenada();
		long coordenada2y = getPosicionCoordenada();
		PortletSession session = req.getPortletSession();
		session.removeAttribute(COORDENADA1_X);
		session.removeAttribute(COORDENADA1_Y);
		session.removeAttribute(COORDENADA2_X);
		session.removeAttribute(COORDENADA2_Y);		
		session.setAttribute(COORDENADA1_X, coordenada1x);
		session.setAttribute(COORDENADA1_Y, coordenada1y+1);
		session.setAttribute(COORDENADA2_X, coordenada2x);
		session.setAttribute(COORDENADA2_Y, coordenada2y+1);
	}

	private static long getPosicionCoordenada() {
		long coord = Math.round(Math.random() * 10);
		while (coord > TarjetaCoordenadas.CANT_COORDENADAS - 1) {
			coord = Math.round(Math.random() * 10);
		}
		return coord;
	}

	public static String getValor1(HttpServletRequest req) {
		return req.getParameter(VALOR_COORDENADA1);
	}

	public static String getValor2(HttpServletRequest req) {
		return req.getParameter(VALOR_COORDENADA2);
	}

	public static String generarCoordenadas() {
		StringBuilder coordenadas = new StringBuilder();
		for (int y = 0; y < TarjetaCoordenadas.CANT_COORDENADAS; y++) {
			for (int x = 0; x < TarjetaCoordenadas.CANT_COORDENADAS; x++) {
				coordenadas.append(generarCoordenada());
				coordenadas.append(TarjetaCoordenadas.SEPARADOR_COLUMNAS);
			}
			coordenadas.replace(coordenadas.length() - 1, coordenadas.length(),
					TarjetaCoordenadas.SEPARADOR_FILAS);
		}
		String coordenadasFinal = coordenadas.substring(0,
				coordenadas.length() - 1);
		return coordenadasFinal;
	}

	private static String generarCoordenada() {
		long coord = Math.round(Math.random() * 100);
		while (coord > 99) {
			coord = Math.round(Math.random() * 100);
		}
		String coordenada = String.valueOf(coord < 10 ? "0" + coord : coord);
		return coordenada;
	}

	@SuppressWarnings("unused")
	private static String getCoordenadaBoba() {
		return "11";
	}
	
	public static TarjetaCoordenadas getTarjetaCoordenadasUsuario(long userid) throws Exception {
		return getInstance().getTarjetaCoordenadasUsuario(userid);
	}
}
