package ar.com.ospim.tesoreria.action;


import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import ar.com.ospim.global.services.TraeListasServiceUtil;
import ar.com.ospim.tesoreria.beans.MovimientoBancario;
import ar.com.ospim.tesoreria.beans.caja_chica.CajaChica;
import ar.com.ospim.tesoreria.service.CajaChicaServiceUtil;
import ar.com.ospim.util.StringUtils;

import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.struts.JSONAction;

public class TraeSaldoCuentaBancariaAction extends JSONAction {

	@Override
	public String getJSON(ActionMapping arg0, ActionForm arg1,
			HttpServletRequest req, HttpServletResponse res) throws Exception {
		
		int idCta=ParamUtil.getInteger(req, "cuenta");
		
		Date fecha=null;
		BigDecimal saldo=BigDecimal.ZERO;
		String sFecha="";
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		DecimalFormat df = new DecimalFormat("###,###,###,###.00");
		List<MovimientoBancario>l =TraeListasServiceUtil.getSaldoCuentasBancariasConformado(idCta);
		
		if(l!=null && l.size()>0){
		  fecha = l.get(0).getFecha_movimiento();
		  saldo= l.get(0).getImporte().setScale(2,BigDecimal.ROUND_HALF_UP) ;
		}
		
		if(fecha !=null) sFecha = sdf.format(fecha);
		
		return "{ \"fecha\" : \"" + sFecha + 
				"\",\"saldo\" : \"" + df.format(saldo) +"\"}";
	}

}