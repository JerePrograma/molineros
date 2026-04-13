package ar.com.ospim.liquidaciones.beans;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import ar.com.ospim.util.DateUtils;

public class DetalleCuota{

	private int id_reintegro_user; //id del reintegro al usuario, se actualiza cuando se autoriza o audita la cuota, 
    							   //es ahí cuando la cuota se convierte en reintegro.
    private int id_reintegro; //id del tratamiento (reintegro) al que pertenece la cuota.
    private int nro_cuota; //1, 2, ó 3.
    private Date fecha;
    private Date periodo;
    private int porcentaje;
    private BigDecimal importe;
    private String diagnostico;
    private String plan_tratamiento;
    private String tiempo_estimado;
    private String pronostico;
    private String informe;
	private String compro_a_debitar_tipo;
	private String comproaDebitarLetra;
	private String comproaDebitarSucursal;
	private String compro_a_debitar_numero;
    private int estado;
    private int id_op;
    private int id_reclamo;
    private int id_reclamo_prestaciones;
    
    public DetalleCuota(){    	
    }
    
    
    public DetalleCuota(int idReintegroUser, int idReintegro, int nroCuota,
			Date fecha, Date periodo, int porcentaje, BigDecimal importe,
			String diagnostico, String planTratamiento, String tiempoEstimado,
			String pronostico, String informe, String comproADebitarTipo,
			String comproADebitarLetra, String comproADebitarSucursal,
			String comproADebitarNumero, int estado, 
			int idReclamo, int idReclamoPrestaciones) {
		super();
		this.id_reintegro_user = idReintegroUser;
		this.id_reintegro = idReintegro;
		this.nro_cuota = nroCuota;
		this.fecha = fecha;
		this.periodo = periodo;
		this.porcentaje = porcentaje;
		this.importe = importe;
		this.diagnostico = diagnostico;
		this.plan_tratamiento = planTratamiento;
		this.tiempo_estimado = tiempoEstimado;
		this.pronostico = pronostico;
		this.informe = informe;
		this.compro_a_debitar_tipo = comproADebitarTipo;
		this.setComproaDebitarLetra(comproADebitarLetra);
		this.setComproaDebitarSucursal(comproADebitarSucursal);
		this.compro_a_debitar_numero = comproADebitarNumero;
		this.estado = estado;
		this.id_reclamo = idReclamo;
		this.id_reclamo_prestaciones = idReclamoPrestaciones;
		
	}
	/**
	 * @return the id_reintegro_user
	 */
	public int getId_reintegro_user() {
		return id_reintegro_user;
	}
	/**
	 * @param idReintegroUser the id_reintegro_user to set
	 */
	public void setId_reintegro_user(int idReintegroUser) {
		id_reintegro_user = idReintegroUser;
	}
	/**
	 * @return the id_reintegro
	 */
	public int getId_reintegro() {
		return id_reintegro;
	}
	/**
	 * @param idReintegro the id_reintegro to set
	 */
	public void setId_reintegro(int idReintegro) {
		id_reintegro = idReintegro;
	}
	/**
	 * @return the nro_cuota
	 */
	public int getNro_cuota() {
		return nro_cuota;
	}
	/**
	 * @param nroCuota the nro_cuota to set
	 */
	public void setNro_cuota(int nroCuota) {
		nro_cuota = nroCuota;
	}
	/**
	 * @return the fecha
	 */
	public Date getFecha() {
		return fecha;
	}
	/**
	 * @param fecha the fecha to set
	 */
	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}
	/**
	 * @return the periodo
	 */
	public Date getPeriodo() {
		return periodo;
	}
	/**
	 * @param periodo the periodo to set
	 */
	public void setPeriodo(Date periodo) {
		this.periodo = periodo;
	}
	/**
	 * @return the porcentaje
	 */
	public int getPorcentaje() {
		return porcentaje;
	}
	/**
	 * @param porcentaje the porcentaje to set
	 */
	public void setPorcentaje(int porcentaje) {
		this.porcentaje = porcentaje;
	}
	/**
	 * @return the importe
	 */
	public BigDecimal getImporte() {
		return importe;
	}
	/**
	 * @param importe the importe to set
	 */
	public void setImporte(BigDecimal importe) {
		this.importe = importe;
	}
	/**
	 * @return the diagnostico
	 */
	public String getDiagnostico() {
		return diagnostico;
	}
	/**
	 * @param diagnostico the diagnostico to set
	 */
	public void setDiagnostico(String diagnostico) {
		this.diagnostico = diagnostico;
	}
	/**
	 * @return the plan_tratamiento
	 */
	public String getPlan_tratamiento() {
		return plan_tratamiento;
	}
	/**
	 * @param planTratamiento the plan_tratamiento to set
	 */
	public void setPlan_tratamiento(String planTratamiento) {
		plan_tratamiento = planTratamiento;
	}
	/**
	 * @return the tiempo_estimado
	 */
	public String getTiempo_estimado() {
		return tiempo_estimado;
	}
	/**
	 * @param tiempoEstimado the tiempo_estimado to set
	 */
	public void setTiempo_estimado(String tiempoEstimado) {
		tiempo_estimado = tiempoEstimado;
	}
	/**
	 * @return the pronostico
	 */
	public String getPronostico() {
		return pronostico;
	}
	/**
	 * @param pronostico the pronostico to set
	 */
	public void setPronostico(String pronostico) {
		this.pronostico = pronostico;
	}
	/**
	 * @return the informe
	 */
	public String getInforme() {
		return informe;
	}
	/**
	 * @param informe the informe to set
	 */
	public void setInforme(String informe) {
		this.informe = informe;
	}
	/**
	 * @return the compro_a_debitar_tipo
	 */
	public String getCompro_a_debitar_tipo() {
		return compro_a_debitar_tipo;
	}
	/**
	 * @param comproADebitarTipo the compro_a_debitar_tipo to set
	 */
	public void setCompro_a_debitar_tipo(String comproADebitarTipo) {
		compro_a_debitar_tipo = comproADebitarTipo;
	}
	/**
	 * @return the compro_a_debitar_numero
	 */
	public String getCompro_a_debitar_numero() {
		return compro_a_debitar_numero == null ? "" : compro_a_debitar_numero;
	}
	/**
	 * @param comproADebitarNumero the compro_a_debitar_numero to set
	 */
	public void setCompro_a_debitar_numero(String comproADebitarNumero) {
		compro_a_debitar_numero = comproADebitarNumero;
	}
	/**
	 * @return the estado
	 */
	public int getEstado() {
		return estado;
	}
	/**
	 * @param estado the estado to set
	 */
	public void setEstado(int estado) {
		this.estado = estado;
	}

	
	public int getId_Reclamo() {
		return id_reclamo;
	}

	public void setId_Reclamo(int idReclamo) {
		this.id_reclamo = idReclamo;
	}
	
	public int getId_ReclamoPrestaciones() {
		return id_reclamo_prestaciones;
	}

	public void setId_ReclamoPrestaciones(int idReclamoPrestaciones) {
		this.id_reclamo_prestaciones = idReclamoPrestaciones;
	}
	
	public String getId_reintegro_userString() {
		return String.valueOf(id_reintegro_user);
	}
	
	public static DetalleCuota getMapping(ResultSet rs,
			String prefix) throws SQLException {
		
		DetalleCuota detalleCuota = new DetalleCuota();
		detalleCuota.setId_reintegro(rs.getInt(prefix + "id_reintegro"));
		detalleCuota.setNro_cuota(rs.getInt(prefix + "nro_cuota"));
		detalleCuota.setFecha(rs.getDate(prefix + "fecha"));
		detalleCuota.setPeriodo(rs.getDate(prefix + "periodo"));
		detalleCuota.setPorcentaje(rs.getInt(prefix + "porcentaje"));
		detalleCuota.setImporte(rs.getBigDecimal(prefix + "importe"));
		detalleCuota.setDiagnostico(rs.getString(prefix + "diagnostico"));
		detalleCuota.setPlan_tratamiento(rs.getString(prefix + "plan_tratamiento"));
		detalleCuota.setTiempo_estimado(rs.getString(prefix + "tiempo_estimado"));
		detalleCuota.setPronostico(rs.getString(prefix + "pronostico"));
		detalleCuota.setInforme(rs.getString(prefix + "informe"));
		detalleCuota.setCompro_a_debitar_tipo(rs.getString(prefix + "compro_a_debitar_tipo"));
		detalleCuota.setComproaDebitarLetra(rs.getString(prefix + "compro_a_debitar_letra"));
		detalleCuota.setComproaDebitarSucursal(rs.getString(prefix + "compro_a_debitar_sucu"));
		detalleCuota.setCompro_a_debitar_numero(rs.getString(prefix + "compro_a_debitar_numero"));
		detalleCuota.setEstado(rs.getInt(prefix + "estado"));
		
		try {
			detalleCuota.setId_Reclamo(rs.getInt(prefix + "id_reclamo"));
			detalleCuota.setId_ReclamoPrestaciones(rs.getInt(prefix + "id_reclamo_prestaciones"));
		} catch (Exception e) {}
		
		try {
			detalleCuota.setId_reintegro_user(rs.getInt(prefix + "id_cuota"));
		} catch (Exception e) {}
		try {
			detalleCuota.setId_reintegro_user(rs.getInt(prefix + "id_reintegro_user"));
		} catch (Exception e) {}
		try {
			detalleCuota.setId_op(rs.getInt("op_id"));
		} catch (Exception e) {}
				
		return detalleCuota;
	}

	/**
	 * @return the id_op
	 */
	public int getId_op() {
		return id_op;
	}

	/**
	 * @param idOp the id_op to set
	 */
	public void setId_op(int idOp) {
		id_op = idOp;
	}
	
	public boolean isPaga(){
		return id_op != 0;
	}
	
	public String getFechaAsString() {
		return null!=fecha?DateUtils.format(fecha,DateUtils.SHORT):"";
	}


	public String getComproaDebitarLetra() {
		return comproaDebitarLetra == null ? "" : comproaDebitarLetra;
	}


	public void setComproaDebitarLetra(String comproaDebitarLetra) {
		this.comproaDebitarLetra = comproaDebitarLetra;
	}


	public String getComproaDebitarSucursal() {
		return comproaDebitarSucursal == null ? "" : comproaDebitarSucursal;
	}


	public void setComproaDebitarSucursal(String comproaDebitarSucursal) {
		this.comproaDebitarSucursal = comproaDebitarSucursal;
	}

}