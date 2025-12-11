package ar.com.ospim.liquidaciones.beans;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ar.com.ospim.afiliados.beans.Afiliado;
import ar.com.ospim.global.beans.Plan;
import ar.com.ospim.global.beans.Seccional;
import ar.com.ospim.util.DateUtils;

/**
 * @author sistema-09
 * @version 1.0
 * @created 25-Ago-2010 02:25:46 p.m.
 */
public class Reintegro {

	protected int id_reintegro;
	protected Afiliado afiliado;
	protected Seccional seccional;
	protected Date fecha;
	protected Date periodo;
	protected int id_seccional;
	protected Date liquidado;
	protected Date auditado;
	protected Date alta_fecha;
	protected String alta_usr;
	protected Date modi_fecha;
	protected String modi_usr;
	protected Date baja_fecha;
	protected String baja_usr;
	protected int estado; //0 alta, 1 auditado, 2 liquidado
	protected String entidad;
	protected String tipo_reintegro;
	protected List<ReintegroPrestacion> reintegroPrestacion;
	protected BigDecimal importeTotal;
	protected String observaciones;
	protected int idOP;
	protected BigInteger chequeOP;
	protected Date fechaOP;
	protected int id_reintegro_user;
	private List<DetalleCuota> detalleCuota;
	protected int id_lista_reintegro; 
	
	protected boolean transferenciaBancaria;
	protected String cbu;
	protected String cuilCuenta;
	protected String emailCuenta;
	protected String apellidoCuenta;
	protected String nombreCuenta;
	
	
	


	

	public Reintegro(){

	}

	/**
	 * @return the id_reintegro
	 */	
	public Reintegro(Date fecha, Date periodo, int id_seccional, String cuil_titular, int inte, String descripcion,
			int id_reintegro, String tipo_reintegro, Date fecha_baja, String usr_baja, String entidad, int id_plan, 
			String nombre_plan, Date fecha_baja_afil, int id_orden_pago, BigInteger chequeOp, Date fechaOp) {
		this.fecha = fecha;
		this.periodo = periodo;		
		this.id_reintegro = id_reintegro;
		this.tipo_reintegro = tipo_reintegro;		
		this.afiliado = new Afiliado();
		this.afiliado.setCuil_titular(cuil_titular);
		this.afiliado.setInte(inte);
		this.afiliado.setUltimo_plan(new Plan(id_plan, nombre_plan));
		this.afiliado.setBaja_fecha(fecha_baja_afil);
		Seccional seccional = new Seccional();
		seccional.setId_seccional(id_seccional);		
		seccional.setDescripcion(descripcion);
		this.setId_seccional(id_seccional);
		this.afiliado.setSeccional(seccional);
		this.baja_fecha = fecha_baja;
		this.baja_usr = usr_baja;
		this.entidad = entidad;
		this.estado = id_orden_pago != 0 ? 2 : 0; //estado liquidado 2, cargado 0		
		this.idOP = id_orden_pago;
		this.chequeOP = chequeOp;
		this.fechaOP = fechaOp;
	}
	
	public Reintegro(Date fecha, Date periodo, int id_seccional, String cuil_titular, int inte, String descripcion,
			int id_reintegro, String tipo_reintegro, Date fecha_baja, String usr_baja, String entidad, int id_plan, 
			String nombre_plan, Date fecha_baja_afil, int id_orden_pago, BigInteger chequeOp, Date fechaOp, int estado) {
		this.fecha = fecha;
		this.periodo = periodo;		
		this.id_reintegro = id_reintegro;
		this.tipo_reintegro = tipo_reintegro;		
		this.afiliado = new Afiliado();
		this.afiliado.setCuil_titular(cuil_titular);
		this.afiliado.setInte(inte);
		this.afiliado.setUltimo_plan(new Plan(id_plan, nombre_plan));
		this.afiliado.setBaja_fecha(fecha_baja_afil);
		Seccional seccional = new Seccional();
		seccional.setId_seccional(id_seccional);		
		seccional.setDescripcion(descripcion);
		this.setId_seccional(id_seccional);
		this.afiliado.setSeccional(seccional);
		this.baja_fecha = fecha_baja;
		this.baja_usr = usr_baja;
		this.entidad = entidad;
		this.estado = id_orden_pago != 0 ? 2 : 0; //estado liquidado 2, cargado 0		
		this.idOP = id_orden_pago;
		this.chequeOP = chequeOp;
		this.fechaOP = fechaOp;
		this.estado = estado;
	}

	
	public Reintegro(int id_reintegro, BigDecimal importe) {
		this.id_reintegro = id_reintegro;
		this.importeTotal = importe;
	}

	public int getId_reintegro() {
		return id_reintegro;
	}
	
	public String getId_reintegroString() {
		return String.valueOf(id_reintegro);
	}

	/**
	 * @param idReintegro the id_reintegro to set
	 */
	public void setId_reintegro(int idReintegro) {
		id_reintegro = idReintegro;
	}

	
	/**
	 * @return the fecha
	 */
	public Date getFecha() {
		return fecha;
	}
	
	public String getFechaAsString() {
		return null!=fecha?DateUtils.format(fecha,DateUtils.SHORT):"";
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
	 * @return the periodo
	 */
	public String getPeriodoString() {
		return null!=periodo?DateUtils.format(periodo,DateUtils.PERIODO):"";
	}

	/**
	 * @param periodo the periodo to set
	 */
	public void setPeriodo(Date periodo) {
		this.periodo = periodo;
	}

	/**
	 * @return the id_seccional
	 */
	public int getId_seccional() {
		return id_seccional;
	}

	/**
	 * @param idSeccional the id_seccional to set
	 */
	public void setId_seccional(int idSeccional) {
		id_seccional = idSeccional;
	}

	/**
	 * @return the liquidado
	 */
	public Date getLiquidado() {
		return liquidado;
	}

	/**
	 * @param liquidado the liquidado to set
	 */
	public void setLiquidado(Date liquidado) {
		this.liquidado = liquidado;
	}

	/**
	 * @return the auditado
	 */
	public Date getAuditado() {
		return auditado;
	}

	/**
	 * @param auditado the auditado to set
	 */
	public void setAuditado(Date auditado) {
		this.auditado = auditado;
	}

	/**
	 * @return the alta_fecha
	 */
	public Date getAlta_fecha() {
		return alta_fecha;
	}

	/**
	 * @param altaFecha the alta_fecha to set
	 */
	public void setAlta_fecha(Date altaFecha) {
		alta_fecha = altaFecha;
	}

	/**
	 * @return the alta_usr
	 */
	public String getAlta_usr() {
		return alta_usr;
	}

	/**
	 * @param altaUsr the alta_usr to set
	 */
	public void setAlta_usr(String altaUsr) {
		alta_usr = altaUsr;
	}

	/**
	 * @return the modi_fecha
	 */
	public Date getModi_fecha() {
		return modi_fecha;
	}

	/**
	 * @param modiFecha the modi_fecha to set
	 */
	public void setModi_fecha(Date modiFecha) {
		modi_fecha = modiFecha;
	}

	/**
	 * @return the modi_usr
	 */
	public String getModi_usr() {
		return modi_usr;
	}

	/**
	 * @param modiUsr the modi_usr to set
	 */
	public void setModi_usr(String modiUsr) {
		modi_usr = modiUsr;
	}

	/**
	 * @return the baja_fecha
	 */
	public Date getBaja_fecha() {
		return baja_fecha;
	}

	/**
	 * @param bajaFecha the baja_fecha to set
	 */
	public void setBaja_fecha(Date bajaFecha) {
		baja_fecha = bajaFecha;
	}

	/**
	 * @return the baja_usr
	 */
	public String getBaja_usr() {
		return baja_usr;
	}

	/**
	 * @param bajaUsr the baja_usr to set
	 */
	public void setBaja_usr(String bajaUsr) {
		baja_usr = bajaUsr;
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

	/**
	 * @return the entidad
	 */
	public String getEntidad() {
		return entidad;
	}

	/**
	 * @param entidad the entidad to set
	 */
	public void setEntidad(String entidad) {
		this.entidad = entidad;
	}

	/**
	 * @return the tipo_reintegro
	 */
	public String getTipo_reintegro() {
		return tipo_reintegro;
	}

	/**
	 * @param tipoReintegro the tipo_reintegro to set
	 */
	public void setTipo_reintegro(String tipoReintegro) {
		tipo_reintegro = tipoReintegro;
	}

	/**
	 * @return the afiliado
	 */
	public Afiliado getAfiliado() {
		return afiliado;
	}

	/**
	 * @param afiliado the afiliado to set
	 */
	public void setAfiliado(Afiliado afiliado) {
		this.afiliado = afiliado;
	}

	/**
	 * @return the reintegroPrestacion
	 */
	public List<ReintegroPrestacion> getReintegroPrestacion() {
		return reintegroPrestacion;
	}

	/**
	 * @param reintegroPrestacionList the reintegroPrestacion to set
	 */
	public void setReintegroPrestacion(List< ? extends ReintegroPrestacion> reintegroPrestacionList) {
		reintegroPrestacion  = new ArrayList<ReintegroPrestacion>();
		if (reintegroPrestacionList != null){
			this.reintegroPrestacion.addAll(reintegroPrestacionList);
		}
	}
	
	public BigDecimal getImporteTotal(){
		BigDecimal total = new BigDecimal(0);
		if (reintegroPrestacion != null){
			for (ReintegroPrestacion rPrest : reintegroPrestacion){
				total =  total.add(rPrest != null && rPrest.getImporteTotal() != null ? rPrest.getImporteTotal() : new BigDecimal(0));
			}
		}
		return total.setScale(2, RoundingMode.HALF_DOWN);
	}
	
	public BigDecimal getImportePrestacion(){
		BigDecimal total = new BigDecimal(0);
		if (reintegroPrestacion != null){
			for (ReintegroPrestacion rPrest : reintegroPrestacion){
				total =  total.add(rPrest != null && rPrest.getImporte() != null ? rPrest.getImporte() : new BigDecimal(0));
			}
		}
		return total.setScale(2, RoundingMode.HALF_DOWN);
	}
	
	public void setImporteTotal () {
		this.importeTotal = getImporteTotal();
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id_reintegro;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Reintegro other = (Reintegro) obj;
		if (id_reintegro != other.id_reintegro)
			return false;
		return true;
	}

	
	public static Reintegro getMapping(ResultSet rs) throws SQLException {
		return getMapping(rs, "");		
	}
	
	public static Reintegro getMapping(ResultSet rs, String prefix) throws SQLException {
		Reintegro reintegro = new Reintegro();
		reintegro.setId_reintegro(rs.getInt(prefix+"id_reintegro"));		 
		reintegro.setFecha(rs.getDate(prefix+"fecha")); 
		reintegro.setPeriodo(rs.getDate(prefix+"periodo")); 
		reintegro.setLiquidado(rs.getDate(prefix+"liquidado"));		
		reintegro.setAuditado(rs.getDate(prefix+"auditado")); 
		reintegro.setAlta_fecha(rs.getDate(prefix+"alta_fecha")); 
		reintegro.setAlta_usr(rs.getString(prefix+"alta_usr")); 
		reintegro.setModi_fecha(rs.getDate(prefix+"modi_fecha")); 
		reintegro.setModi_usr(rs.getString(prefix+"modi_usr")); 
		reintegro.setBaja_fecha(rs.getDate(prefix+"baja_fecha")); 
		reintegro.setBaja_usr(rs.getString(prefix+"baja_usr")); 
		reintegro.setEstado(rs.getInt(prefix+"estado")); 
		reintegro.setEntidad(rs.getString(prefix+"entidad")); 
		reintegro.setTipo_reintegro(rs.getString(prefix+"tipo_reintegro"));
		try{
			reintegro.setObservaciones(rs.getString(prefix+"observaciones"));
		} catch (Exception e) {}
		try{
			reintegro.setId_reintegro_user(rs.getInt(prefix+"id_reintegro_user"));
		} catch (Exception e) {}
		return reintegro;
	}

	/**
	 * @return the seccional
	 */
	public Seccional getSeccional() {
		return seccional;
	}

	/**
	 * @param seccional the seccional to set
	 */
	public void setSeccional(Seccional seccional) {
		this.seccional = seccional;
	}
	
	public BigDecimal importeTotal () {
		return this.importeTotal;
	}
	
	public boolean estaLiquidado () {
		return estado == 2 ? true : false;
	}

	/**
	 * @return the observaciones
	 */
	public String getObservaciones() {
		return observaciones;
	}

	/**
	 * @param observaciones the observaciones to set
	 */
	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}

	/**
	 * @return the idOP
	 */
	public int getIdOP() {
		return idOP;
	}

	/**
	 * @param idOP the idOP to set
	 */
	public void setIdOP(int idOP) {
		this.idOP = idOP;
	}

	/**
	 * @return the chequeOP
	 */
	public BigInteger getChequeOP() {
		return chequeOP;
	}

	/**
	 * @param chequeOP the chequeOP to set
	 */
	public void setChequeOP(BigInteger chequeOP) {
		this.chequeOP = chequeOP;
	}

	/**
	 * @return the fechaOP
	 */
	public Date getFechaOP() {
		return fechaOP;
	}

	/**
	 * @param fechaOP the fechaOP to set
	 */
	public void setFechaOP(Date fechaOP) {
		this.fechaOP = fechaOP;
	}
	
	public String getOPReintegro () {
		StringBuffer sb = new StringBuffer("");
		sb.append(this.id_lista_reintegro > 0 ? this.id_lista_reintegro + " / " : "" );
		sb.append(this.idOP > 0 ? this.idOP + " / " : "" );
		sb.append(this.chequeOP != null ? this.chequeOP.toString() + " / " : "");
		sb.append(this.fechaOP != null ? this.fechaOP.toString() : "");
		return sb.toString();
	}

	/**
	 * @return the id_reintegro_user
	 */
	public int getId_reintegro_user() {
		return id_reintegro_user;
	}

	/**
	 * @param idReintegroProtesis the id_reintegro_user to set
	 */
	public void setId_reintegro_user(int idReintegroProtesis) {
		id_reintegro_user = idReintegroProtesis;
	}
	
	public String getId_reintegro_userString() {
		return String.valueOf(id_reintegro_user);
	}
	
	
	/**
	 * @return the detalleCuota
	 */	
	public List<DetalleCuota> getDetalleCuota() {
		return detalleCuota;
	}

	/**
	 * @param detalleCuota the detalleCuota to set
	 */
	public void setDetalleCuota(List<DetalleCuota> detalleCuota) {
		this.detalleCuota = detalleCuota;
	}

	public int getId_lista_reintegro() {
		return id_lista_reintegro;
	}

	public void setId_lista_reintegro(int idListaReintegro) {
		id_lista_reintegro = idListaReintegro;
	}

	

	public String getCbu() {
		return cbu;
	}

	public void setCbu(String cbu) {
		this.cbu = cbu;
	}

	public String getCuilCuenta() {
		return cuilCuenta;
	}

	public void setCuilCuenta(String cuilCuenta) {
		this.cuilCuenta = cuilCuenta;
	}

	public String getEmailCuenta() {
		return emailCuenta;
	}

	public void setEmailCuenta(String emailCuenta) {
		this.emailCuenta = emailCuenta;
	}

	public String getApellidoCuenta() {
		return apellidoCuenta;
	}

	public void setApellidoCuenta(String apellidoCuenta) {
		this.apellidoCuenta = apellidoCuenta;
	}

	public String getNombreCuenta() {
		return nombreCuenta;
	}

	public void setNombreCuenta(String nombreCuenta) {
		this.nombreCuenta = nombreCuenta;
	}
	
	public boolean isTransferenciaBancaria() {
		return transferenciaBancaria;
	}

	public void setTransferenciaBancaria(boolean transferenciaBancaria) {
		this.transferenciaBancaria = transferenciaBancaria;
	}
	
}