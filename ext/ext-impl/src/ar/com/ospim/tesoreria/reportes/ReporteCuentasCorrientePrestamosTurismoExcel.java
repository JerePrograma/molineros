package ar.com.ospim.tesoreria.reportes;
import ar.com.ospim.hoteles.services.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import ar.com.ospim.tesoreria.beans.CuentaCorriente;
import ar.com.ospim.tesoreria.beans.EstadoInicialCuentaCorriente;

import ar.com.ospim.afiliados.beans.Afiliado;
import ar.com.ospim.global.beans.Seccional;
import ar.com.ospim.hoteles.beans.Prestamo;
//import ar.com.ospim.hoteles.services.HotelesServiceUtil;
import ar.com.ospim.hoteles.services.WebKeysHoteles;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;

public class ReporteCuentasCorrientePrestamosTurismoExcel extends
		ReporteCuentaCorrientePrestamosTurismo {
	private static Log _log = LogFactoryUtil
			.getLog(ReporteCuentasCorrientePrestamosTurismoExcel.class);

	public static HSSFWorkbook generaReportePrestamosTurismo(HttpServletRequest req,
			HttpServletResponse res, boolean soloReporteConsolidado) {

		HttpSession session = (HttpSession) req.getSession();
		
		String cuil=ParamUtil.getString(req,"cuil",null);
		String inteParam =  ParamUtil.getString(req, "inte",null);
		String seccionalP =  ParamUtil.getString(req, "seccional",null);
		
		Integer inte = null;
		try {
			inte = Integer.parseInt(inteParam);
		} catch (Exception e) {}
		
		Integer seccional=null;
		try {
			seccional = Integer.parseInt(seccionalP);
		} catch (Exception e) {}

		SimpleDateFormat formatoDeFechas = new SimpleDateFormat("dd/MM/yyyy");
				
		String fechaDesdeDia = ParamUtil.getString(req,"fechadesdedia");
		String fechaDesdeMes = ParamUtil.getString(req,"fechadesdemes");
		String fechaDesdeAnio = ParamUtil.getString(req,"fechadesdeanio");		
		String fechaHastaDia = ParamUtil.getString(req,"fechahastadia");
		String fechaHastaMes = ParamUtil.getString(req,"fechahastames");
		String fechaHastaAnio = ParamUtil.getString(req,"fechahastaanio");

		boolean soloConSaldo = ParamUtil.getBoolean(req, "soloConSaldo");

		int entidad = ParamUtil.getInteger(req, "entidad");

		String tipoReporte = ParamUtil.getString(req, "tipoReporte");

		Long id = ParamUtil.getLong(req, "id",0);
		String hotel = ParamUtil.getString(req,"hotel");
		
		Date fechaD = null;
		try {
			fechaD = formatoDeFechas.parse(fechaDesdeDia + "/"
					+ (Integer.parseInt(fechaDesdeMes) + 1) + "/"
					+ fechaDesdeAnio);
		} catch (Exception e) {
			fechaD = null;
		}
		
		Date fechaH = null;
		try {
			fechaH = formatoDeFechas.parse(fechaHastaDia + "/"
					+ (Integer.parseInt(fechaHastaMes) + 1) + "/"
					+ fechaHastaAnio);
		} catch (Exception e) {
			fechaH = null;
		}
		
		String fechaCuotaDia = ParamUtil.getString(req,"fechadesdecuotadia");
		String fechaCuotaMes = ParamUtil.getString(req,"fechadesdecuotames");
		String fechaCuotaAnio = ParamUtil.getString(req,"fechadesdecuotaanio");
		
		String fechaCuotaDiaH = ParamUtil.getString(req,"fechahastacuotadia");
		String fechaCuotaMesH = ParamUtil.getString(req,"fechahastacuotames");
		String fechaCuotaAnioH = ParamUtil.getString(req,"fechahastacuotaanio");
		
		Date fechaCuotaD = null;
		try {
			fechaCuotaD= formatoDeFechas.parse(fechaCuotaDia + "/"
					+ (Integer.parseInt(fechaCuotaMes) + 1) + "/"
					+ fechaCuotaAnio);
		} catch (Exception e) {
			fechaCuotaD = null;
		}
		
		Date fechaCuotaH = null;
		try {
			fechaCuotaH = formatoDeFechas.parse(fechaCuotaDiaH + "/"
					+ (Integer.parseInt(fechaCuotaMesH) + 1) + "/"
					+ fechaCuotaAnioH);
		} catch (Exception e) {
			fechaCuotaH = null;
		}

		session.removeAttribute(WebKeysHoteles.PRESTAMO_FILTRO);
		session.removeAttribute(WebKeysHoteles.PRESTAMOS_RESULT);
			
        Prestamo filtro = new Prestamo();
        filtro.setId(id);
        
        if(hotel!=null) {
          filtro.setHotel(hotel);	
        }
        Afiliado afiliado = new Afiliado();
        if(cuil!=null) {
        	afiliado.setCuil_titular(cuil);
        	if(inte!=null) {
        		afiliado.setInte(inte);
        	}
        	
        	filtro.setAfiliado(afiliado);
        }
        
        if(seccional!=null) {
           Seccional secc =new Seccional();
           secc.setId_seccional(seccional);
           afiliado.setSeccional(secc);
        }
        
        if(fechaD!=null) {
        	filtro.setFechaConvenioDesde(fechaD);
        }
        
        if(fechaH!=null) {
        	filtro.setFechaConvenioHasta(fechaH);
        }
        
        if(fechaCuotaD!=null) {
        	filtro.setFechaCuotaDesde(fechaCuotaD);
        }
        
        if(fechaCuotaH!=null) {
        	filtro.setFechaCuotaHasta(fechaCuotaH);
        }
        
        
        
        String fechaCCDia = ParamUtil.getString(req,"fechaccdia");
		String fechaCCMes = ParamUtil.getString(req,"fechaccmes");
		String fechaCCAnio = ParamUtil.getString(req,"fechaccanio");
		
		Date fechaCCH = null;
		try {
			fechaCCH = formatoDeFechas.parse(fechaCCDia + "/"
					+ (Integer.parseInt(fechaCCMes) + 1) + "/"
					+ fechaCCAnio);
		} catch (Exception e) {
			fechaCCH = null;
		}
        
		filtro.setCorteCuentaCorriente(fechaCCH);
        
		try {
			
	        List<Prestamo> lista = HotelesServiceUtil.getListaPrestamos(filtro);
	        
			session.setAttribute(WebKeysHoteles.PRESTAMO_FILTRO,filtro);
			session.setAttribute(WebKeysHoteles.PRESTAMOS_RESULT,lista);

			return generarReportePreTur(fechaD, fechaH, lista, 
					soloConSaldo, entidad, soloReporteConsolidado,fechaCCH);
		} catch (Exception e) {
			_log.error("Error al generar libro banco", e);
			return null;
		}
		
	}

}
