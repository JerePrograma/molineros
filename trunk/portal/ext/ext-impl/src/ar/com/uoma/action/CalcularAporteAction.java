package ar.com.uoma.action;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
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
import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.tesoreria.beans.InteresAfip;

import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.JSONAction;

public class CalcularAporteAction extends JSONAction {

	@Override
	public String getJSON(ActionMapping arg0, ActionForm arg1,
			HttpServletRequest req, HttpServletResponse arg3) throws Exception {

		//int entidad=ParamUtil.getInteger(req, "entidad");
		double remuneracion= ParamUtil.getDouble(req, "remuneracion");
		int tipoBoleta= ParamUtil.getInteger(req, "tipo_boleta");
		String camara= ParamUtil.getString(req, "camara");
		String periodoString = ParamUtil.getString(req, "periodo");
		int cantAfi=ParamUtil.getInteger(req, "cant_afi")>0?ParamUtil.getInteger(req, "cant_afi"):1;
		
		int obligDia=ParamUtil.getInteger(req, "obligDia");
		int obligMes=ParamUtil.getInteger(req, "obligMes");
		int obligAnio=ParamUtil.getInteger(req, "obligAnio");
		
		GregorianCalendar calendarObligacion = null;
		Date fechaObligacion = null;
		
		if (0 != obligDia && 0 != obligAnio) {
			calendarObligacion = new GregorianCalendar(obligAnio, obligMes, obligDia);
			fechaObligacion = calendarObligacion.getTime();
		} 
		
		
		
		Date periodo=getPeriodoAsDate(periodoString);
		BigDecimal calculado=BigDecimal.ZERO;
		
		//Cálculo de capital
		Map<Camara, List<DetalleEscalaSalarial>> tablaEscalaSalarialSueldos=EscalaSalarialServiceImpl.getEscalasSalariales(periodo); 
		
		try{
			if(tipoBoleta==CalculaCapitalCuotaServiceUtil.AMTIMA){//AMTIMA
				calculado=CalculaCapitalCuotaServiceUtil.calcularCapitalCuotaAMTIMA(false, TablaEscalaSalarial.Camara.valueOf(camara), periodo, 0, tablaEscalaSalarialSueldos);
			}
			
			if(tipoBoleta==CalculaCapitalCuotaServiceUtil.SOCIAL){//Cuota Social
				calculado=CalculaCapitalCuotaServiceUtil.calcularCapitalCuotaSocialUOMA(new BigDecimal(remuneracion));
			}
			
			if(tipoBoleta==CalculaCapitalCuotaServiceUtil.USUFRUCTO){//Cuota Usufructo
				calculado=CalculaCapitalCuotaServiceUtil.calcularCapitalCuotaUsufructo(new BigDecimal(remuneracion));
			}
			if(tipoBoleta==CalculaCapitalCuotaServiceUtil.ART_46){//Art 46.
				calculado=CalculaCapitalCuotaServiceUtil.calcularCapitalArticulo46(TablaEscalaSalarial.Camara.valueOf(camara), periodo, 0, tablaEscalaSalarialSueldos);
				calculado=calculado.multiply(new BigDecimal(cantAfi));
			}
			if(tipoBoleta==CalculaCapitalCuotaServiceUtil.SOLIDARIO){//Cuota Solidario
				calculado=CalculaCapitalCuotaServiceUtil.calcularCapitalAporteSocialUOMA(new BigDecimal(remuneracion));
			}
					
		} catch (Exception e) {
			return "{\"status\":\"falla_inesperada\"}";
		}
		
		return "{ \"calculado\" : \"" + calculado.toString() + "\"}";
		
	}
	
	private Date getPeriodoAsDate(String periodo){
		Date periodoDate=new Date();
		SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
		periodo = periodo.replaceAll("_", "-");
		periodo = "01-" + periodo;
		try{
			periodoDate = format.parse(periodo);
		}catch(Exception e){
			e.printStackTrace();			
		}
		return periodoDate;
	}
	
}
