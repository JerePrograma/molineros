package ar.com.ospim.global.services;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import ar.com.ospim.global.beans.Cheque;
import ar.com.ospim.global.beans.Concepto;
import ar.com.ospim.global.beans.DepositoBancario;
import ar.com.ospim.global.beans.Efectivo;
import ar.com.ospim.global.beans.Efectivo.Estado;
import ar.com.ospim.global.beans.Empresa;
import ar.com.ospim.global.beans.Pagare;
import ar.com.ospim.global.beans.ReciboAnticipo;
import ar.com.ospim.global.beans.TarjetaDebitoCredito;
import ar.com.ospim.liquidaciones.DuplicateNumeroChequeException;
import ar.com.ospim.tesoreria.beans.Acta;
import ar.com.ospim.tesoreria.beans.Recibo;
import ar.com.ospim.tesoreria.beans.ReciboActa;
import ar.com.ospim.tesoreria.beans.ReciboCheque;
import ar.com.ospim.tesoreria.beans.ReciboConcepto;
import ar.com.ospim.tesoreria.beans.ReciboConcepto.ConceptoPago;
import ar.com.ospim.tesoreria.beans.ReciboConvenio;
import ar.com.ospim.tesoreria.beans.ReciboIngreso;
import ar.com.ospim.tesoreria.beans.ReciboOtroConcepto;
import ar.com.ospim.tesoreria.beans.convenio.Convenio;
import ar.com.ospim.tesoreria.reportes.ReporteAnticiposExcel.ReporteAnticipos;

import com.liferay.portal.SystemException;

public interface ReciboGlobalServiceImpl {
	
//	public void saveConcepto(Recibo recibo, Acta acta, Convenio convenio,
//			Cheque chequeNoDepositado, Cheque chequeRechazado,
//			Integer otroConceptoId, BigDecimal importePorCheques,
//			BigDecimal importeAdicional, List<ConceptoPago> cpagos,
//			String userName, Connection connectionParameter, boolean amtima)
//			throws SystemException ;
	public void saveConcepto(Recibo recibo, Acta acta, Convenio convenio,
			Cheque chequeNoDepositado, Cheque chequeRechazado,
			Concepto otroConceptoId, BigDecimal importePorCheques,
			BigDecimal importeAdicional, List<ConceptoPago> cpagos,
			BigDecimal importeRemunTotal, Date periodo, Integer cantidadEmpleados,
			Integer nroBoletaEmpleadores,Integer nroSecuenciaDDJJEmpleadores,BigDecimal totalBoleta,
			String userName, Connection connectionParameter, int entidad)
			throws SystemException ;

	public int save(Recibo recibo, boolean debaja, String user,
			Connection connectionParameter, int entidad) throws SystemException ;
	
	/*
	public int saveIngreso(Recibo recibo, Cheque cheque, DepositoBancario depo,
			Efectivo ef, ReciboAnticipo rAnticipo, String user,
			Connection connectionParameter, int entidad) throws SystemException ;
	*/
	
	public int saveIngreso(Recibo recibo, Cheque cheque, DepositoBancario depo,
			Efectivo ef, ReciboAnticipo rAnticipo,TarjetaDebitoCredito tarjeta ,String user,
			Connection connectionParameter, int entidad) throws SystemException ;
	
	public int save(DepositoBancario depo, Recibo recibo, String user,
			Connection connectionParameter, int entidad) throws SystemException;

	public int save(Efectivo ef, Recibo recibo, String user,
			Connection connectionParameter, int entidad) throws SystemException;
	
	public int save(Cheque cheque, Recibo recibo, String user,
			Connection connectionParameter, int entidad) throws SystemException,
			DuplicateNumeroChequeException;
	
	public int save(Pagare pagare, Recibo recibo, String user,
			Connection connectionParameter, int entidad) throws SystemException;
	
	public int save(TarjetaDebitoCredito depo, Recibo recibo, String user,
			Connection connectionParameter, int entidad) throws SystemException;

	public void save(Recibo recibo, ReciboActa rActa, String userName,
			Connection connectionParameter, int entidad) throws SystemException;

	public void save(Recibo recibo, ReciboConvenio rConv, String userName,
			Connection connectionParameter, int entidad) throws SystemException;

	public void saveChequeRechazadoASustituir(ReciboCheque rCh, Recibo recibo,
			String userName, Connection connectionParameter, int entidad)
			throws SystemException;

	public void saveChequeASustituir(ReciboCheque rCh, Recibo recibo,
			String userName, Connection connectionParameter, int entidad)
			throws SystemException;

	public void save(ReciboOtroConcepto oc, Recibo recibo, String userName,
			Connection connectionParameter, int entidad) throws SystemException;


	public List<Recibo> get(String reciboNroStr, String cuit, String empresa,
			Connection connectionParameter, String cuil_titular, Integer inte, int entidad) throws SystemException ;
	
	public Recibo get(int id, int entidad) throws SystemException;

	public List<ReciboConcepto> getConceptos(int id, int entidad) throws SystemException ;
	public List<ReciboIngreso> getIngresos(int reciboId, int entidad) throws SystemException;
	public void anular(int reciboId, String user, Date fechaBaja, int entidad) throws SystemException;	
	public List<ReciboIngreso> getEfectivosRecibidos(int estadoEfectivo, int entidad)
			throws SystemException;

	public void cambiarEstadoReciboEfectivo(ReciboIngreso ri, Estado estado,
			String screenName, Connection connectionParameter, int entidad)
			throws SystemException ;	
	public List<Recibo> get(Date fechaIni, Date fechaFin, Empresa empresa, int entidad)
			throws SystemException;
	public Map<Recibo, List<ReciboConcepto>> getConceptos(Date fechaIni,
			Date fechaFin, int entidad) throws SystemException ;

	public List<Recibo> getIngresos(Date fechaIni, Date fechaFin, int entidad)
			throws SystemException;

	public List<ReciboIngreso> getAnticiposParaAplicar(Empresa empresa, int entidad)
			throws SystemException;

	public int save(ReciboAnticipo reciboAnticipo, Recibo recibo, String user,
			Connection connectionParameter, int entidad) throws SystemException ;

	public List<ReporteAnticipos> getReporteAnticipos(Date fechaIni,
			Date fechaFin, Empresa empresa) throws SystemException ;

	public boolean verificarReciboDerivado(int reciboId, int entidad) throws SystemException;

}
