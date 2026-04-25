package ar.com.ospim.prestadores.beans;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import ar.com.ospim.global.beans.TipoPago;
import ar.com.ospim.liquidaciones.beans.Prestador;

/**
 * @version 1.0
 * @created 19-Oct-2012 02:25:41 p.m. */
public class ConvenioPrestacional implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1536808048904663585L;
	
	private int id;
	private Prestador prestador;
//	private int estado; //integer NOT NULL,-- 1. cargado, 2. aprobado, 3. rechazado.
	private EstadosConvPrest estado;
	private int diaRecepcion; //integer,
	private String condicionDePago; 
	private TipoPago tipoPago;
	private Date vigencia;
	private Date vencimiento;
	private Date altaFecha;
	private String altaUsr;
	private Date modiFecha;
	private String modiUsr;
	private Date bajaFecha;
	private String bajaUsr;
	
	public enum EstadosConvPrest {
        CARGADO(1), APROBADO(2), RECHAZADO(3);
        private int value;

        private EstadosConvPrest(int value) {
                this.value = value;
        }
        
        public int getIntValue(){
        	return this.value;
        }
	};   

	private List<ConvenioPrestacionalDetalle> convenioPrestDetalle;

	public ConvenioPrestacional(){
		super();
	}
	
	public ConvenioPrestacional(int id_convenio_prest, Prestador prestador,
			EstadosConvPrest estado, int diaRecepcion, String condicionDePago,
			TipoPago tipoPago, Date fechaVigencia, Date fechaVencimiento) {
		super();
		this.id = id_convenio_prest;
		this.prestador = prestador;
		this.estado = estado;
		this.diaRecepcion = diaRecepcion;
		this.condicionDePago = condicionDePago;
		this.tipoPago = tipoPago;
		this.vigencia = fechaVigencia;
		this.vencimiento = fechaVencimiento;
	}

	public int getId() {
		return id;
	}

	public void setId(int idConvenioPrest) {
		this.id = idConvenioPrest;
	}

	public Prestador getPrestador() {
		return prestador;
	}

	public void setPrestador(Prestador prestador) {
		this.prestador = prestador;
	}

	public EstadosConvPrest getEstado() {
		return estado;
	}

	public void setEstado(EstadosConvPrest estado) {
		this.estado = estado;
	}

	public int getDiaRecepcion() {
		return diaRecepcion;
	}

	public void setDiaRecepcion(int diaRecepcion) {
		this.diaRecepcion = diaRecepcion;
	}

	public String getCondicionDePago() {
		return condicionDePago;
	}

	public void setCondicionDePago(String condicionDePago) {
		this.condicionDePago = condicionDePago;
	}

	public TipoPago getTipoPago() {
		return tipoPago;
	}

	public void setTipo_pago(TipoPago tipoPago) {
		this.tipoPago = tipoPago;
	}

	public Date getAltaFecha() {
		return altaFecha;
	}

	public void setAltaFecha(Date altaFecha) {
		this.altaFecha = altaFecha;
	}

	public String getAltaUsr() {
		return altaUsr;
	}

	public void setAltaUsr(String altaUsr) {
		this.altaUsr = altaUsr;
	}

	public Date getModiFecha() {
		return modiFecha;
	}

	public void setModiFecha(Date modiFecha) {
		this.modiFecha = modiFecha;
	}

	public String getModiUsr() {
		return modiUsr;
	}

	public void setModiUsr(String modiUsr) {
		this.modiUsr = modiUsr;
	}

	public Date getBajaFecha() {
		return bajaFecha;
	}

	public void setBajaFecha(Date bajaFecha) {
		this.bajaFecha = bajaFecha;
	}

	public String getBajaUsr() {
		return bajaUsr;
	}

	public void setBajaUsr(String bajaUsr) {
		this.bajaUsr = bajaUsr;
	}

	public List<ConvenioPrestacionalDetalle> getConvenioPrestDetalle() {
		return convenioPrestDetalle;
	}

	public void setConvenioPrestDetalle(
			List<ConvenioPrestacionalDetalle> convenioPrestDetalle) {
		this.convenioPrestDetalle = convenioPrestDetalle;
	}

	public Date getVigencia() {
		return vigencia;
	}

	public void setVigencia(Date vigencia) {
		this.vigencia = vigencia;
	}

	public Date getVencimiento() {
		return vencimiento;
	}

	public void setVencimiento(Date vencimiento) {
		this.vencimiento = vencimiento;
	}

	public void setTipoPago(TipoPago tipoPago) {
		this.tipoPago = tipoPago;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
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
		ConvenioPrestacional other = (ConvenioPrestacional) obj;
		if (id != other.id)
			return false;
		return true;
	}
		
	public static ConvenioPrestacional getMapping(ResultSet rs, String prefix) throws SQLException {
		
		ConvenioPrestacional convenio = new ConvenioPrestacional();	
		Prestador prest = Prestador.getMappingTipo(rs, "prest_");
		TipoPago tpago = TipoPago.getMapping("tpago_",rs);
		
		convenio.setId(rs.getInt(prefix+"id_convenio_prest"));		 
//		convenio.setEstado(EstadosConvPrest.valueOf(rs.getString(prefix+"estado")));
		switch (rs.getInt(prefix+"estado")) {
		case 1:
			convenio.setEstado(EstadosConvPrest.CARGADO);
			break;
		case 2:
			convenio.setEstado(EstadosConvPrest.APROBADO);
			break;
		case 3:
			convenio.setEstado(EstadosConvPrest.RECHAZADO);
			break;	
		}
		convenio.setDiaRecepcion(rs.getInt(prefix+"dia_recepcion"));
		convenio.setCondicionDePago(rs.getString(prefix+"condicion_de_pago"));
		convenio.setVigencia(rs.getDate(prefix+"fecha_vigencia")); 
		convenio.setVencimiento(rs.getDate(prefix+"fecha_vencimiento")); 
		convenio.setAltaFecha(rs.getDate(prefix+"alta_fecha")); 
		convenio.setAltaUsr(rs.getString(prefix+"alta_usr")); 
		convenio.setModiFecha(rs.getDate(prefix+"modi_fecha")); 
		convenio.setModiUsr(rs.getString(prefix+"modi_usr")); 
		convenio.setBajaFecha(rs.getDate(prefix+"baja_fecha")); 
		convenio.setBajaUsr(rs.getString(prefix+"baja_usr")); 
		
		convenio.setPrestador(prest);
		convenio.setTipo_pago(tpago);
		return convenio;
	}
	
	
//	public ConvenioPrestacional(int idContratoDetalle, int idContrato, int idPrestador, int estado,
//			int diaRecepcion, String condicionDePago, int idTipoPago,
//			Date altaFecha, String altaUsr, Date modiFecha, String modiUsr,
//			Date bajaFecha, String bajaUsr) {
//		super();
//		id_contrato = idContrato;
//		id_prestador = idPrestador;
//		this.estado = estado;
//		dia_recepcion = diaRecepcion;
//		condicion_de_pago = condicionDePago;
//		id_tipo_pago = idTipoPago;
//		alta_fecha = altaFecha;
//		alta_usr = altaUsr;
//		modi_fecha = modiFecha;
//		modi_usr = modiUsr;
//		baja_fecha = bajaFecha;
//		baja_usr = bajaUsr;
//	}
//	public ConvenioPrestacional() {
//		// TODO Auto-generated constructor stub
//	}
//				
//	public int getId_contrato_detalle() {
//		return id_contrato_detalle;
//	}
//	public void setId_contrato_detalle(int idContratoDetalle) {
//		id_contrato_detalle = idContratoDetalle;
//	}
//	public int getId_contrato() {
//		return id_contrato;
//	}
//	public void setId_contrato(int idContrato) {
//		id_contrato = idContrato;
//	}
//	public int getId_prestador() {
//		return id_prestador;
//	}	
//	public String getId_contratoAsString() {
//		return String.valueOf(id_contrato);
//	}	
//	public void setId_prestador(int idPrestador) {
//		id_prestador = idPrestador;
//	}
//	public String getCuit() {
//		return cuit;
//	}
//	public void setCuit(String cuit) {
//		this.cuit = cuit;
//	}
//	public String getDescripcion() {
//		return descripcion;
//	}
//	public void setDescripcion(String descripcion) {
//		this.descripcion = descripcion;
//	}
//	public int getEstado() {
//		return estado;
//	}
//	
//	public String getEstadoString() {
//		return estado == 1 ? "Cargado" : estado == 2 ? "Aprobado" : estado == 3 ? "Rechazado" : "";
//	}
//
//	public void setEstado(int estado) {
//		this.estado = estado;
//	}
//	public int getDia_recepcion() {
//		return dia_recepcion;
//	}
//	public void setDia_recepcion(int diaRecepcion) {
//		dia_recepcion = diaRecepcion;
//	}
//	public String getCondicion_de_pago() {
//		return condicion_de_pago;
//	}
//	public void setCondicion_de_pago(String condicionDePago) {
//		condicion_de_pago = condicionDePago;
//	}
//	public int getId_tipo_pago() {
//		return id_tipo_pago;
//	}
//	public void setId_tipo_pago(int idTipoPago) {
//		id_tipo_pago = idTipoPago;
//	}
//	public Date getAlta_fecha() {
//		return alta_fecha;
//	}
//	public void setAlta_fecha(Date altaFecha) {
//		alta_fecha = altaFecha;
//	}
//	public String getAlta_usr() {
//		return alta_usr;
//	}
//	public void setAlta_usr(String altaUsr) {
//		alta_usr = altaUsr;
//	}
//	public Date getModi_fecha() {
//		return modi_fecha;
//	}
//	public void setModi_fecha(Date modiFecha) {
//		modi_fecha = modiFecha;
//	}
//	public String getModi_usr() {
//		return modi_usr;
//	}
//	public void setModi_usr(String modiUsr) {
//		modi_usr = modiUsr;
//	}
//	public Date getBaja_fecha() {
//		return baja_fecha;
//	}
//	public void setBaja_fecha(Date bajaFecha) {
//		baja_fecha = bajaFecha;
//	}
//	public String getBaja_usr() {
//		return baja_usr;
//	}
//	public void setBaja_usr(String bajaUsr) {
//		baja_usr = bajaUsr;
//	}
//	
//	/**
//	 * @return the fecha_prestacion
//	 */
//	public String getAltaFechaAsString() {
//		return null!=alta_fecha?DateUtils.format(alta_fecha,DateUtils.SHORT):"";
//	}
//	
//	public List<ContratoDetalle> getContratoDetalle() {
//		return contratoDetalle;
//	}
//				
//	public static ConvenioPrestacional getMapping(ResultSet rs) throws SQLException {
//		return getMapping(rs, "");		
//	}
//	
//	
//	@Override
//	public int hashCode() {
//		final int prime = 31;
//		int result = 1;
//		result = prime * result + id_contrato;
//		return result;
//	}
//
//	@Override
//	public boolean equals(Object obj) {
//		if (this == obj)
//			return true;
//		if (obj == null)
//			return false;
//		if (getClass() != obj.getClass())
//			return false;
//		ConvenioPrestacional other = (ConvenioPrestacional) obj;
//		if (id_contrato != other.id_contrato)
//			return false;
//		return true;
//	}
//
//	public void setContratoDetalle(List<ContratoDetalle> contratoDetalleEntry) {
//		contratoDetalle = contratoDetalleEntry;	
//	}
	
}
