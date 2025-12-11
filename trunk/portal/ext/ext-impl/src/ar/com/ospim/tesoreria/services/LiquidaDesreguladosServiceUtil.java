package ar.com.ospim.tesoreria.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ar.com.global.beans.ProcesoSQL;
import ar.com.ospim.mail.MailUtils;
import ar.com.ospim.procesaArchivos.beans.JubiladosSitaci;
import ar.com.ospim.tesoreria.beans.ConsolidadoLiquidaciones;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

public class LiquidaDesreguladosServiceUtil {
	
	private static Log logger = LogFactoryUtil.getLog(LiquidaDesreguladosServiceUtil.class);

	private static LiquidaDesreguladosServiceImpl instance = null;

	public static LiquidaDesreguladosServiceImpl getInstance() {
		if (null == instance) {
			instance = new LiquidaDesreguladosServiceImpl();
		}
		return instance;
	}

	public static List<ConsolidadoLiquidaciones> getConsolidadoLiquidaciones(String id_terc, Date fechaDesde, Date fechaHasta)
			throws SystemException {		
		return getInstance().getConsolidadoLiquidaciones(id_terc, fechaDesde, fechaHasta);
	}
	
	public static List<String> getDerivaDesregulaString(String id_terc, Date fecha_liq)
			throws Exception {		
		return getInstance().getDerivaDesregulaString(id_terc, fecha_liq);
	}
	
	public static List<String> getAfiliadosSinAporteString(String id_terc, Date fecha_liq)
			throws Exception {	
		return getInstance().getAfiliadosSinAporteString(id_terc, fecha_liq);
	}
	
	public static ProcesoSQL isRunningProcess()
			throws Exception {	
		return getInstance().isRunningProcess();
	}
	public static boolean cancelaProceso(int procid)
			throws Exception {	
		return getInstance().cancelaProceso(procid);
	}
		
	public static List<String> getComisionesTercerizadoraString(String id_terc,
			Date fecha_liq) throws Exception {
		return getInstance().getComisionesTercerizadoraString(id_terc, fecha_liq);
	}
	
	
	public static List<String> getPedidoInformeJubilados(Date fechaDesde, Date fechaHasta)
			throws SystemException {		
		return getInstance().getPedidoInformeJubilados(fechaDesde, fechaHasta);
	}
	
	
	public static List<JubiladosSitaci> getPeriodosProcesadosJubilados() throws SystemException{
		return getInstance().getPeriodosProcesadosJubilados();
	}
	
	public static Integer liquidarPeriodoJubilados(String periodo) throws Exception{
		return getInstance().liquidarPeriodoJubilados(periodo);
	}
	
	public static List<JubiladosSitaci> getJubilados(String periodo,String cuil,String dni,String tercerizadora) throws SystemException{
		return getInstance().getJubilados(periodo,cuil,dni,tercerizadora);
	}
	
	public static Integer eliminarPeriodoJubilados(String periodo) throws Exception{
		return getInstance().eliminarPeriodoJubilados(periodo);
	}
}
