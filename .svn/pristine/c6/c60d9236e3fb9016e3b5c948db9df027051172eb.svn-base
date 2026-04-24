package ar.com.ospim.estudioisidro.beans;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class ActaAcuerdoSeguimientoResumen implements Serializable {	
		
	private static final long serialVersionUID = -5630028924204125947L;
	private Double actasPagosAtrasadosOspim;
	private Double conveniosPagosAtrasadosOspim;
	
	private Double actasPagosAtrasadosUoma;
	private Double conveniosPagosAtrasadosUoma;
			
	private Double actasPagosAtrasadosAmtima;
	private Double conveniosPagosAtrasadosAmtima;
	
	private Double actasPagosACobrarOspim;
	private Double conveniosPagosACobrarOspim;
	
	private Double actasPagosACobrarUoma;
	private Double conveniosPagosACobrarUoma;
			
	private Double actasPagosACobrarAmtima;
	private Double conveniosPagosACobrarAmtima;
	
	public ActaAcuerdoSeguimientoResumen() {
	}
	
	public static ActaAcuerdoSeguimientoResumen getMapping(ResultSet rs) throws SQLException {
		ActaAcuerdoSeguimientoResumen ac=new ActaAcuerdoSeguimientoResumen();	
		
		ac.setActasPagosAtrasadosAmtima(rs.getDouble("acta_amtima"));
		ac.setActasPagosAtrasadosOspim(rs.getDouble("acta_ospim"));
		ac.setActasPagosAtrasadosUoma(rs.getDouble("acta_uoma"));
		
		ac.setConveniosPagosAtrasadosAmtima(rs.getDouble("convenio_amtima"));
		ac.setConveniosPagosAtrasadosOspim(rs.getDouble("convenio_ospim"));
		ac.setConveniosPagosAtrasadosUoma(rs.getDouble("convenio_uoma"));
		
		ac.setActasPagosACobrarAmtima(rs.getDouble("acta_amtima_a_cobrar"));
		ac.setActasPagosACobrarOspim(rs.getDouble("acta_ospim_a_cobrar"));
		ac.setActasPagosACobrarUoma(rs.getDouble("acta_uoma_a_cobrar"));
		
		ac.setConveniosPagosACobrarAmtima(rs.getDouble("convenio_amtima_a_cobrar"));
		ac.setConveniosPagosACobrarOspim(rs.getDouble("convenio_ospim_a_cobrar"));
		ac.setConveniosPagosACobrarUoma(rs.getDouble("convenio_uoma_a_cobrar"));
		
		return ac;
	}

	public Double getConveniosPagosACobrarOspim() {
		return conveniosPagosACobrarOspim;
	}

	public void setConveniosPagosACobrarOspim(Double conveniosPagosACobrarOspim) {
		this.conveniosPagosACobrarOspim = conveniosPagosACobrarOspim;
	}

	public Double getConveniosPagosACobrarUoma() {
		return conveniosPagosACobrarUoma;
	}

	public void setConveniosPagosACobrarUoma(Double conveniosPagosCobrarUoma) {
		this.conveniosPagosACobrarUoma = conveniosPagosCobrarUoma;
	}

	public Double getActasPagosAtrasadosOspim() {
		return actasPagosAtrasadosOspim;
	}

	public void setActasPagosAtrasadosOspim(Double actasPagosAtrasadosOspim) {
		this.actasPagosAtrasadosOspim = actasPagosAtrasadosOspim;
	}

	public Double getActasPagosAtrasadosUoma() {
		return actasPagosAtrasadosUoma;
	}

	public void setActasPagosAtrasadosUoma(Double actasPagosAtrasadosUoma) {
		this.actasPagosAtrasadosUoma = actasPagosAtrasadosUoma;
	}

	public Double getActasPagosAtrasadosAmtima() {
		return actasPagosAtrasadosAmtima;
	}

	public void setActasPagosAtrasadosAmtima(Double actasPagosAtrasadosAmtima) {
		this.actasPagosAtrasadosAmtima = actasPagosAtrasadosAmtima;
	}

	public Double getConveniosPagosAtrasadosAmtima() {
		return conveniosPagosAtrasadosAmtima;
	}

	public void setConveniosPagosAtrasadosAmtima(Double conveniosPagosAtrasadosAmtima) {
		this.conveniosPagosAtrasadosAmtima = conveniosPagosAtrasadosAmtima;
	}

	public Double getConveniosPagosAtrasadosOspim() {
		return conveniosPagosAtrasadosOspim;
	}

	public void setConveniosPagosAtrasadosOspim(Double conveniosPagosAtrasadosOspim) {
		this.conveniosPagosAtrasadosOspim = conveniosPagosAtrasadosOspim;
	}

	public Double getConveniosPagosAtrasadosUoma() {
		return conveniosPagosAtrasadosUoma;
	}

	public Double getActasPagosACobrarOspim() {
		return actasPagosACobrarOspim;
	}

	public void setActasPagosACobrarOspim(Double actasPagosACobrarOspim) {
		this.actasPagosACobrarOspim = actasPagosACobrarOspim;
	}

	public Double getActasPagosACobrarUoma() {
		return actasPagosACobrarUoma;
	}

	public void setActasPagosACobrarUoma(Double actasPagosACobrarUoma) {
		this.actasPagosACobrarUoma = actasPagosACobrarUoma;
	}

	public Double getActasPagosACobrarAmtima() {
		return actasPagosACobrarAmtima;
	}

	public void setActasPagosACobrarAmtima(Double actasPagosACobrarAmtima) {
		this.actasPagosACobrarAmtima = actasPagosACobrarAmtima;
	}

	public Double getConveniosPagosACobrarAmtima() {
		return conveniosPagosACobrarAmtima;
	}

	public void setConveniosPagosACobrarAmtima(Double conveniosPagosACobrarAmtima) {
		this.conveniosPagosACobrarAmtima = conveniosPagosACobrarAmtima;
	}

	public void setConveniosPagosAtrasadosUoma(Double conveniosPagosAtrasadosUoma) {
		this.conveniosPagosAtrasadosUoma = conveniosPagosAtrasadosUoma;
	}
	
	
}
