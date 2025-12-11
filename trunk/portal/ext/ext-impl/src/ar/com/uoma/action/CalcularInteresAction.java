package ar.com.uoma.action;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import ar.com.global.beans.DetalleEscalaSalarial;
import ar.com.global.beans.TablaEscalaSalarial;
import ar.com.global.beans.TablaEscalaSalarial.Camara;
import ar.com.global.services.CalculaCapitalCuotaServiceUtil;
import ar.com.global.services.EscalaSalarialServiceImpl;
import ar.com.ospim.afip.beans.ReporteDeudaNominaEmpresa.PagosEmpresa;
import ar.com.ospim.afip.service.AfipServiceUtil;
import ar.com.ospim.afip.service.FeriadosServiceUtil;
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.tesoreria.beans.ActaPeriodoDeudaEmpresa;
import ar.com.ospim.tesoreria.beans.ActaPeriodoDeudaEmpresa.Detalle;
import ar.com.ospim.tesoreria.beans.InteresAfip;

import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.JSONAction;

public class CalcularInteresAction extends JSONAction {

	@Override
	public String getJSON(ActionMapping arg0, ActionForm arg1,
			HttpServletRequest req, HttpServletResponse arg3) throws Exception {

		double pagado = ParamUtil.getDouble(req, "pagado");
		ActaPeriodoDeudaEmpresa auxActa = null;
		double remuneracion = ParamUtil.getDouble(req, "remuneracion");
		String periodoString = ParamUtil.getString(req, "periodo");
		String cuit = ParamUtil.getString(req, "cuit");
		String camara = ParamUtil.getString(req, "camara");
		Date periodo = getPeriodoAsDate(periodoString);

		int obligDia = ParamUtil.getInteger(req, "obligDia");
		int obligMes = ParamUtil.getInteger(req, "obligMes");
		int obligAnio = ParamUtil.getInteger(req, "obligAnio");

		int pagoDia = ParamUtil.getInteger(req, "pagoDia");
		int pagoMes = ParamUtil.getInteger(req, "pagoMes");
		int pagoAnio = ParamUtil.getInteger(req, "pagoAnio");

		int tipoBoleta = ParamUtil.getInteger(req, "tipo_boleta");
		
		int cantAfiliados= ParamUtil.getInteger(req, "cant_afiliados");

		// Cálculo de Interés

		GregorianCalendar calendarObligacion = null;
		Date fechaObligacion = null;
		GregorianCalendar calendarPago = null;
		Date fechaPago = null;
		List<InteresAfip> listaIntereses = TraeListasServiceUtil
				.getInteresesAfip(null);

		if (0 != obligDia && 0 != obligAnio) {
			calendarObligacion = new GregorianCalendar(obligAnio, obligMes,
					obligDia);
			fechaObligacion = calendarObligacion.getTime();
		}

		if (0 != pagoDia && 0 != pagoAnio) {
			calendarPago = new GregorianCalendar(pagoAnio, pagoMes, pagoDia);
			fechaPago = calendarPago.getTime();
		}
		Date vencimientoOriginal = null;
		// LA FECHA DE VTO ES EL DIA HABIL SIGUIENTE AL 15 DE CADA MES.
		if (tipoBoleta == CalculaCapitalCuotaServiceUtil.AMTIMA || tipoBoleta ==CalculaCapitalCuotaServiceUtil.SOLIDARIO) {
			FeriadosServiceUtil feri = new FeriadosServiceUtil();
			Calendar periodoCalendar = Calendar.getInstance();			
			periodoCalendar.setTime(periodo);
			periodoCalendar.add(Calendar.MONTH, 1);
			periodoCalendar.set(Calendar.DAY_OF_MONTH, 15);
			vencimientoOriginal = feri
					.obtenerSiguienteDiaHabil(periodoCalendar).getTime();
		} else {
			vencimientoOriginal = AfipServiceUtil.getVencimientoOriginalAFIP(
					cuit, periodo, req);
			Calendar periodoCalendar = Calendar.getInstance();
			periodoCalendar.setTime(fechaObligacion);
			periodoCalendar.set(Calendar.DAY_OF_MONTH, 1);

		}

		try {
			// DEBO CALCULAR NUEVAMENTE TODO EL CAPITAL Y EL INTERES A LA FECHA
			// DE PAGO

			auxActa = new ActaPeriodoDeudaEmpresa();
			auxActa.setPeriodo(periodo);
			auxActa.setRemuneracionDeclarada(new BigDecimal(remuneracion));
			if (tipoBoleta == CalculaCapitalCuotaServiceUtil.AMTIMA) {
				Map<Camara, List<DetalleEscalaSalarial>> tablaEscalaSalarialSueldos = EscalaSalarialServiceImpl
						.getEscalasSalariales(periodo);
				auxActa.setCalculado(CalculaCapitalCuotaServiceUtil
						.calcularCapitalCuotaAMTIMA(false,
								TablaEscalaSalarial.Camara.valueOf(camara),
								periodo, 0, tablaEscalaSalarialSueldos));
			} else if (tipoBoleta == CalculaCapitalCuotaServiceUtil.SOCIAL) {
				auxActa.setCalculado(CalculaCapitalCuotaServiceUtil
						.calcularCapitalCuotaSocialUOMA(new BigDecimal(
								remuneracion)));
			} else if (tipoBoleta == CalculaCapitalCuotaServiceUtil.USUFRUCTO) {
				auxActa.setCalculado(CalculaCapitalCuotaServiceUtil
						.calcularCapitalCuotaUsufructo(new BigDecimal(
								remuneracion)));
			} else if (tipoBoleta == CalculaCapitalCuotaServiceUtil.ART_46) {
				Map<Camara, List<DetalleEscalaSalarial>> tablaEscalaSalarialSueldos = EscalaSalarialServiceImpl
						.getEscalasSalariales(periodo);
				auxActa.setCalculado(CalculaCapitalCuotaServiceUtil
						.calcularCapitalArticulo46(
								TablaEscalaSalarial.Camara.valueOf(camara),
								periodo, 0, tablaEscalaSalarialSueldos).multiply(cantAfiliados>0?new BigDecimal(cantAfiliados):BigDecimal.ONE));
			} else if (tipoBoleta == CalculaCapitalCuotaServiceUtil.SOLIDARIO) {
				auxActa.setCalculado(CalculaCapitalCuotaServiceUtil
						.calcularCapitalAporteSocialUOMA(new BigDecimal(
								remuneracion)));
			}
			PagosEmpresa pago = new PagosEmpresa(fechaPago, new BigDecimal(
					pagado));
			List<Detalle> listaPagos = new ArrayList<Detalle>();
			Detalle detalle = new Detalle(pago);
			listaPagos.add(detalle);
			auxActa.setDetalle(listaPagos);
			auxActa.calcularSaldoConInteres(vencimientoOriginal,
					listaIntereses, fechaObligacion);
		} catch (Exception e) {
			return "{\"status\":\"falla_inesperada\"}";
		}

		return "{ \"interes\" : \""
				+ auxActa.getInteres().toString()
				+ "\",\"capital\" : \""
				+ auxActa.getDetalle().get(0).getCapital()
						.add(new BigDecimal(pagado)).toString()
				+ "\",\"interesApago\" : \""
				+ auxActa.getDetalle().get(0).getInteresAFechaPagada().toString()+ "\"}";
	}

	private Date getPeriodoAsDate(String periodo) {
		Date periodoDate = new Date();
		SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
		periodo = periodo.replaceAll("_", "-");
		periodo = "01-" + periodo;
		try {
			periodoDate = format.parse(periodo);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return periodoDate;
	}

}
