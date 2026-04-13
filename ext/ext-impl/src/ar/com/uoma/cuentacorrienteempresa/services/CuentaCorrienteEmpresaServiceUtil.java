package ar.com.uoma.cuentacorrienteempresa.services;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import ar.com.empresas.beans.Contacto;
import ar.com.ospim.afiliados.empleadores.DuplicateEmpresaIdException;
import ar.com.ospim.afiliados.empleadores.index.EmpresasIndex;
import ar.com.ospim.autorizaciones.beans.PreAutorizacion;
import ar.com.ospim.autorizaciones.beans.PreAutorizacionMedicamento;
import ar.com.ospim.autorizaciones.beans.PreAutorizacionPrestacion;
import ar.com.ospim.global.beans.Domicilio;
import ar.com.ospim.global.beans.Empresa;
import ar.com.ospim.tesoreria.beans.CuentaBancaria;
import ar.com.ospim.util.ConnectionHelper;
import ar.com.ospim.util.DateUtils;
import ar.com.uoma.beans.CuentaCorrienteEmpresa;

public class CuentaCorrienteEmpresaServiceUtil {

	private static Log _log = LogFactoryUtil.getLog(CuentaCorrienteEmpresaServiceUtil.class);

	public static List<CuentaCorrienteEmpresa> getCuentaCorriente(
			String cuit, String  sucursal, 
			Date fechaDesde, Date fechaHasta, 
			Boolean procesarConsulta, int modo, 
			int tipoBoleta, int qrySoloUoma, int qrySoloAmtima,
			int nro_vista, String periodo, int pagina) {
		return CuentaCorrienteEmpresaServiceImpl.getInstance().getCuentaCorriente(cuit, sucursal, 
				fechaDesde, fechaHasta, 
				procesarConsulta, modo, 
				tipoBoleta, qrySoloUoma, qrySoloAmtima,
				nro_vista, periodo, pagina) ;
	}
	
}
