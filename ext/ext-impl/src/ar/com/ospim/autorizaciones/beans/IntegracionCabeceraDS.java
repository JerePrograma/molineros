package ar.com.ospim.autorizaciones.beans;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.liferay.portal.SystemException;
import com.liferay.portlet.documentlibrary.model.impl.DLFileEntryImpl;

import ar.com.ospim.afiliados.beans.AfiPlan;
import ar.com.ospim.afiliados.beans.Afiliado;
import ar.com.ospim.autorizaciones.services.NomencladorServiceUtil;
import ar.com.ospim.autorizaciones.services.PreAutorizacionServiceUtil;
import ar.com.ospim.autorizaciones.services.SeguimientoSurServiceUtil;
import ar.com.ospim.global.beans.Plan;
import ar.com.ospim.global.beans.Seccional;

public class IntegracionCabeceraDS implements Serializable{

	private static final long serialVersionUID = 5608656044727028659L;
	private Integer id;
	private Integer periodo;
	private Integer detalleProcesadosTOTAL;
	private Integer detalleProcesadosOK;
	private Integer detalleProcesadosError;
	private Integer detalleProcesadosDuplicados;
	private Date fecha;
	private Date enviadoSSS;
	private Date respuestaSSS;
	private Integer detalleProcesadosOKSSS;
	private Integer detalleProcesadosErrorSSS;
		
	private String entidad;
	private List<IntegracionDetalleDS> items;
	
	private Integer liquidacionID;
	private Integer OrdenPagoID;
	private Integer loteSSS;
	private Integer liquidados;
	
	private Date fechaCierre;
	
	private Double total;
	private Double totalComprobantes;
	
	public IntegracionCabeceraDS() {
       items = new ArrayList<IntegracionDetalleDS>();		
	}

	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getDetalleProcesadosOK() {
		return detalleProcesadosOK;
	}

	public void setDetalleProcesadosOK(Integer detalleProcesadosOK) {
		this.detalleProcesadosOK = detalleProcesadosOK;
	}

	public Integer getDetalleProcesadosError() {
		return detalleProcesadosError;
	}

	public void setDetalleProcesadosError(Integer detalleProcesadosError) {
		this.detalleProcesadosError = detalleProcesadosError;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public Date getEnviadoSSS() {
		return enviadoSSS;
	}

	public void setEnviadoSSS(Date enviadoSSS) {
		this.enviadoSSS = enviadoSSS;
	}

	public List<IntegracionDetalleDS> getItems() {
		return items;
	}

	public void setItems(List<IntegracionDetalleDS> items) {
		this.items = items;
	}
	
	
	

	public String getEntidad() {
		return entidad;
	}


	public void setEntidad(String entidad) {
		this.entidad = entidad;
	}

	

	public Integer getDetalleProcesadosDuplicados() {
		return detalleProcesadosDuplicados;
	}


	public void setDetalleProcesadosDuplicados(Integer detalleProcesadosDuplicados) {
		this.detalleProcesadosDuplicados = detalleProcesadosDuplicados;
	}

	
	public Date getRespuestaSSS() {
		return respuestaSSS;
	}


	public void setRespuestaSSS(Date respuestaSSS) {
		this.respuestaSSS = respuestaSSS;
	}


	public Integer getDetalleProcesadosOKSSS() {
		return detalleProcesadosOKSSS;
	}


	public void setDetalleProcesadosOKSSS(Integer detalleProcesadosOKSSS) {
		this.detalleProcesadosOKSSS = detalleProcesadosOKSSS;
	}


	public Integer getDetalleProcesadosErrorSSS() {
		return detalleProcesadosErrorSSS;
	}


	public void setDetalleProcesadosErrorSSS(Integer detalleProcesadosErrorSSS) {
		this.detalleProcesadosErrorSSS = detalleProcesadosErrorSSS;
	}

    
	
	public Integer getLiquidacionID() {
		return liquidacionID;
	}


	public void setLiquidacionID(Integer liquidacionID) {
		this.liquidacionID = liquidacionID;
	}


	public Integer getOrdenPagoID() {
		return OrdenPagoID;
	}


	public void setOrdenPagoID(Integer ordenPagoID) {
		OrdenPagoID = ordenPagoID;
	}

	
	

	public Integer getPeriodo() {
		return periodo;
	}


	public void setPeriodo(Integer periodo) {
		this.periodo = periodo;
	}

    public Integer getDetalleProcesadosTOTAL() {
		return detalleProcesadosTOTAL;
	}


	public void setDetalleProcesadosTOTAL(Integer detalleProcesadosTOTAL) {
		this.detalleProcesadosTOTAL = detalleProcesadosTOTAL;
	}

	
	
	public Integer getLoteSSS() {
		return loteSSS;
	}


	public void setLoteSSS(Integer loteSSS) {
		this.loteSSS = loteSSS;
	}


	public Integer getLiquidados() {
		return liquidados;
	}


	public void setLiquidados(Integer liquidados) {
		this.liquidados = liquidados;
	}

 
	
	public Date getFechaCierre() {
		return fechaCierre;
	}


	public void setFechaCierre(Date fechaCierre) {
		this.fechaCierre = fechaCierre;
	}

	public Double getTotal() {
		return total;
	}


	public void setTotal(Double total) {
		this.total = total;
	}

	
	
	public Double getTotalComprobantes() {
		return totalComprobantes;
	}


	public void setTotalComprobantes(Double totalComprobantes) {
		this.totalComprobantes = totalComprobantes;
	}


	public static IntegracionCabeceraDS getMapping(ResultSet rs) throws SQLException {
		
		IntegracionCabeceraDS a = new IntegracionCabeceraDS();
		a.setId(rs.getInt("id"));
		a.setFecha(rs.getDate("fecha_proceso"));
		a.setEntidad(rs.getString("entidad"));
		a.setDetalleProcesadosTOTAL(rs.getInt("cantidad_registros"));
		a.setDetalleProcesadosOK(rs.getInt("cantidad_OK"));
		a.setDetalleProcesadosError(rs.getInt("cantidad_error"));
		a.setPeriodo(rs.getInt("periodo"));
		a.setLoteSSS(rs.getInt("lote_sss"));
	    a.setEnviadoSSS(rs.getDate("enviado_sss"));
		return a;
	}
	
    public static IntegracionCabeceraDS getMappingSSS(ResultSet rs) throws SQLException {
		
		IntegracionCabeceraDS a = new IntegracionCabeceraDS();
		a.setDetalleProcesadosTOTAL(rs.getInt("cantidad_registros"));
		a.setDetalleProcesadosOK(rs.getInt("cantidad_OK"));
		a.setDetalleProcesadosError(rs.getInt("cantidad_error"));
		a.setPeriodo(rs.getInt("periodo"));
		a.setLoteSSS(rs.getInt("lote_sss"));
	    a.setEnviadoSSS(rs.getDate("enviado_sss"));
	    a.setLiquidados(rs.getInt("liquidados"));
	    a.setFechaCierre(rs.getDate("fecha_cierre"));
		return a;
	}
		
		
   public static IntegracionCabeceraDS getMappingSSSCab(ResultSet rs) throws SQLException {
		
		IntegracionCabeceraDS a = new IntegracionCabeceraDS();
		a.setDetalleProcesadosTOTAL(rs.getInt("cantidad_registros"));
		a.setDetalleProcesadosOK(rs.getInt("cantidad_OK"));
		a.setDetalleProcesadosError(rs.getInt("cantidad_error"));
		a.setPeriodo(rs.getInt("periodo"));
		a.setLoteSSS(rs.getInt("lote_sss"));
	    a.setEnviadoSSS(rs.getDate("enviado_sss"));
	    a.setLiquidados(rs.getInt("liquidados"));
	    a.setFechaCierre(rs.getDate("fecha_cierre"));
	    a.setId(rs.getInt("id"));
		a.setEntidad(rs.getString("entidad"));
		return a;
	}
	
}

