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

public class IntegracionCabeceraDR implements Serializable{

	private static final long serialVersionUID = 7290834974427887649L;
	private Integer periodo;
	private Integer detalleProcesadosTOTAL;
	private Integer detalleProcesadosOK;
	private Integer detalleProcesadosError;
	
	private Date enviadoSSS;
	private Date respuestaSSS;
	private Integer detalleProcesadosOKSSS;
	private Integer detalleProcesadosErrorSSS;
	
	private boolean tieneDetalleDevolucion;
		
	private List<IntegracionDetalleDR> items;
	
	
	private Date fechaCierre;
	
	private Double importeLiquidado;
	private Double importeSolicitado;
	private Double total;
	private Double totalComprobantes;
	
	public IntegracionCabeceraDR() {
       items = new ArrayList<IntegracionDetalleDR>();		
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

	
	public Date getEnviadoSSS() {
		return enviadoSSS;
	}

	public void setEnviadoSSS(Date enviadoSSS) {
		this.enviadoSSS = enviadoSSS;
	}

	public List<IntegracionDetalleDR> getItems() {
		return items;
	}

	public void setItems(List<IntegracionDetalleDR> items) {
		this.items = items;
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

    public Double getImporteLiquidado() {
		return importeLiquidado;
	}

	public void setImporteLiquidado(Double importeLiquidado) {
		this.importeLiquidado = importeLiquidado;
	}

	public Double getImporteSolicitado() {
		return importeSolicitado;
	}

	public void setImporteSolicitado(Double importeSolicitado) {
		this.importeSolicitado = importeSolicitado;
	}

	public boolean isTieneDetalleDevolucion() {
		return tieneDetalleDevolucion;
	}

	public void setTieneDetalleDevolucion(boolean tieneDetalleDevolucion) {
		this.tieneDetalleDevolucion = tieneDetalleDevolucion;
	}

	public static IntegracionCabeceraDR getMappingSSS(ResultSet rs) throws SQLException {
		
		IntegracionCabeceraDR a = new IntegracionCabeceraDR();
		a.setDetalleProcesadosTOTAL(rs.getInt("cantidad_registros"));
		a.setDetalleProcesadosOK(rs.getInt("cantidad_OK"));
		a.setDetalleProcesadosError(rs.getInt("cantidad_error"));
		a.setPeriodo(rs.getInt("periodo"));
	    a.setEnviadoSSS(rs.getDate("fecha_envio_sss"));
	    a.setFechaCierre(rs.getDate("fecha_cierre"));
	    a.setImporteLiquidado(rs.getDouble("importe_liquidado"));
	    a.setImporteSolicitado(rs.getDouble("importe_solicitado"));
	    a.setTieneDetalleDevolucion(rs.getBoolean("tiene_detalle_devolucion"));
		return a;
	}
		
		
	
}

