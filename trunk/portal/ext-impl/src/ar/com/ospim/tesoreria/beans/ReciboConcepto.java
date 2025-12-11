package ar.com.ospim.tesoreria.beans;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import ar.com.ospim.global.beans.Cheque;
import ar.com.ospim.global.beans.Concepto;
import ar.com.ospim.hoteles.beans.Prestamo;
import ar.com.ospim.tesoreria.beans.convenio.Convenio;
import ar.com.ospim.util.DateUtils;

public abstract class ReciboConcepto {
	protected int id;
	// los pagos del recibo que corresponden a este concepto
	private List<ConceptoPago> pagos;
	private Date alta_fecha;
	private String alta_usr;
	private Date modi_fecha;
	private String modi_usr;
	private Date baja_fecha;
	private String baja_usr;
	private String entidad;
	private String comproNroAntic;

	public abstract BigDecimal getTotalAPagar();
	public abstract BigDecimal getTotalAPagarNoOS();

	public abstract Date getFechaAPagar();

	public BigDecimal getTotalPagado() {
		BigDecimal totalPagado = BigDecimal.ZERO;
		if (pagos != null) {
			for (ConceptoPago cp : pagos) {
				totalPagado = totalPagado.add(cp.getImporte());
			}
		}
		return totalPagado;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public Date getAlta_fecha() {
		return alta_fecha;
	}

	public void setAlta_fecha(Date altaFecha) {
		alta_fecha = altaFecha;
	}

	public String getAlta_usr() {
		return alta_usr;
	}

	public void setAlta_usr(String altaUsr) {
		alta_usr = altaUsr;
	}

	public Date getModi_fecha() {
		return modi_fecha;
	}

	public void setModi_fecha(Date modiFecha) {
		modi_fecha = modiFecha;
	}

	public String getModi_usr() {
		return modi_usr;
	}

	public void setModi_usr(String modiUsr) {
		modi_usr = modiUsr;
	}

	public Date getBaja_fecha() {
		return baja_fecha;
	}

	public void setBaja_fecha(Date bajaFecha) {
		baja_fecha = bajaFecha;
	}

	public String getBaja_usr() {
		return baja_usr;
	}

	public String getBaja_fechaAsString() {
		return null != baja_fecha ? DateUtils.format(baja_fecha,
				DateUtils.SHORT) : "";
	}

	public void setBaja_usr(String bajaUsr) {
		baja_usr = bajaUsr;
	}

	public void setPagos(List<ConceptoPago> pagos) {
		this.pagos = pagos;
	}

	/**
	 * Pagos del recibo que corresponden a este concepto
	 * 
	 * @return pagos
	 */
	public List<ConceptoPago> getPagos() {
		return pagos;
	}

	public abstract String getDescripcion();

	public abstract BigDecimal getImporte();

	public static ReciboConcepto getMapping(ResultSet rs) throws SQLException {
		return getMapping(rs, "");
	}

	public static ReciboConcepto getMapping(ResultSet rs, String prefix)
			throws SQLException {
		ReciboConcepto rc = null;

		int conceptoId = rs.getInt(prefix + "caja_concepto_id");
		int actaId = rs.getInt(prefix + "acta_id");
		int convenioId = rs.getInt(prefix + "convenio_id");
		
		
		Long prestamoId =0L;
		Date prestamoFecha=null;
		Double prestamoImporte=0D;
		Double prestamoTotal=0D;
		
		try {
			prestamoId=rs.getLong(prefix+"prestamo_id");
			//prestamoFecha=rs.getDate(prefix+"prestamo_fecha");
			//prestamoImporte=rs.getDouble(prefix+"prestamo_importe");
		    //prestamoTotal=rs.getDouble(prefix+"prestamo_total");
		}catch(Exception e) {
			
		}
		
		
		
		BigDecimal nroChequeNoDepositado = rs.getBigDecimal(prefix
				+ "nro_cheque_no_depositado");
		BigDecimal nroChequeRechazado = rs.getBigDecimal(prefix
				+ "nro_cheque_rechazado");
		if (actaId != 0) {
			rc = new ReciboActa();
			ReciboActa reciboActa = (ReciboActa) rc;
			reciboActa.setImportePorCheques(rs.getBigDecimal(prefix
					+ "concepto_importe_por_cheques"));
			reciboActa.setImporteAdicional(rs.getBigDecimal(prefix
					+ "concepto_importe_adicional"));
			reciboActa.setActa(new Acta(actaId));
		} else if (convenioId != 0) {
			rc = new ReciboConvenio();
			ReciboConvenio reciboConvenio = (ReciboConvenio) rc;
			reciboConvenio.setImportePorCheques(rs.getBigDecimal(prefix
					+ "concepto_importe_por_cheques"));
			reciboConvenio.setImporteAdicional(rs.getBigDecimal(prefix
					+ "concepto_importe_adicional"));
			reciboConvenio.setConvenio(new Convenio(convenioId));
		} else if (nroChequeNoDepositado != null) {
			rc = new ReciboCheque();
			Cheque cheque = new Cheque(nroChequeNoDepositado, rs.getInt(prefix
					+ "id_banco_no_depositado"));
			((ReciboCheque) rc).setChequeASustituir(cheque);
			((ReciboCheque) rc).setTipo(ReciboCheque.Tipo.NO_DEPOSITADO);
		} else if (nroChequeRechazado != null) {
			rc = new ReciboCheque();
			((ReciboCheque) rc).setTipo(ReciboCheque.Tipo.RECHAZADO);
			Cheque cheque = new Cheque(nroChequeRechazado, rs.getInt(prefix
					+ "id_banco_rechazado"));
			((ReciboCheque) rc).setChequeASustituir(cheque);
		} else if (conceptoId != 0 && prestamoId==0L) {
			rc = new ReciboOtroConcepto();
			((ReciboOtroConcepto) rc).setConcepto(new Concepto(conceptoId, rs
					.getString("descripcion_otro_concepto")));
			((ReciboOtroConcepto) rc).setImporte(rs.getBigDecimal(prefix
					+ "concepto_importe_adicional"));
		}else if(prestamoId!=0) {
			rc = new ReciboPrestamo();
			try {
			   rc=ReciboPrestamo.getMapping(rs);
			}catch(Exception e) {}   
		}
		
		try{
			rc.setEntidad(rs.getString("entidad"));			
		}catch(Exception e){
			//Do Nothing
		}
		try{
			rc.setComproNroAntic(rs.getString("compro_nro_antic"));			
		}catch(Exception e){
		}

		rc.setId(rs.getInt(prefix + "id"));
		rc.setAlta_usr(rs.getString(prefix + "alta_usr"));
		rc.setAlta_fecha(rs.getDate(prefix + "alta_fecha"));
		rc.setModi_usr(rs.getString(prefix + "modi_usr"));
		rc.setModi_fecha(rs.getDate(prefix + "modi_fecha"));
		rc.setBaja_fecha(rs.getDate(prefix + "baja_fecha"));
		rc.setBaja_usr(rs.getString(prefix + "baja_usr"));
		return rc;
	}

	public static class ConceptoPago {
		protected ReciboIngreso ingreso;
		private int recibo_concepto_id;
		protected BigDecimal importe;
		protected Date alta_fecha;
		protected String alta_usr;
		protected Date modi_fecha;
		protected String modi_usr;
		protected Date baja_fecha;
		protected String baja_usr;

		public ConceptoPago(int recibo_concepto_id, int reciboIngresoId,
				BigDecimal importe) {
			this.ingreso = new ReciboIngreso(null, reciboIngresoId);
			this.recibo_concepto_id = recibo_concepto_id;
			this.importe = importe;
		}

		public ConceptoPago() {
		}

		public Date getAlta_fecha() {
			return alta_fecha;
		}

		public void setAlta_fecha(Date altaFecha) {
			alta_fecha = altaFecha;
		}

		public String getAlta_usr() {
			return alta_usr;
		}

		public void setAlta_usr(String altaUsr) {
			alta_usr = altaUsr;
		}

		public Date getModi_fecha() {
			return modi_fecha;
		}

		public void setModi_fecha(Date modiFecha) {
			modi_fecha = modiFecha;
		}

		public String getModi_usr() {
			return modi_usr;
		}

		public void setModi_usr(String modiUsr) {
			modi_usr = modiUsr;
		}

		public Date getBaja_fecha() {
			return baja_fecha;
		}

		public void setBaja_fecha(Date bajaFecha) {
			baja_fecha = bajaFecha;
		}

		public String getBaja_usr() {
			return baja_usr;
		}

		public void setBaja_usr(String bajaUsr) {
			baja_usr = bajaUsr;
		}

		public BigDecimal getImporte() {
			return importe;
		}

		public void setImporte(BigDecimal importe) {
			this.importe = importe;
		}

		public void setIngreso(ReciboIngreso ingreso) {
			this.ingreso = ingreso;
		}

		public ReciboIngreso getIngreso() {
			return ingreso;
		}

		public static ConceptoPago getMapping(ResultSet rs) throws SQLException {
			return getMapping(rs, "");
		}

		public static ConceptoPago getMapping(ResultSet rs, String prefix)
				throws SQLException {
			ConceptoPago cp = new ConceptoPago();
			cp.setImporte(rs.getBigDecimal(prefix + "importe"));
			cp.setIngreso(new ReciboIngreso(null, rs.getInt(prefix
					+ "recibo_ingreso_id")));
			cp.setRecibo_concepto_id(rs.getInt(prefix + "recibo_concepto_id"));
			cp.setAlta_usr(rs.getString(prefix + "alta_usr"));
			cp.setAlta_fecha(rs.getDate(prefix + "alta_fecha"));
			cp.setModi_usr(rs.getString(prefix + "modi_usr"));
			cp.setModi_fecha(rs.getDate(prefix + "modi_fecha"));
			cp.setBaja_fecha(rs.getDate(prefix + "baja_fecha"));
			cp.setBaja_usr(rs.getString(prefix + "baja_usr"));
			return cp;
		}

		public void setRecibo_concepto_id(int recibo_concepto_id) {
			this.recibo_concepto_id = recibo_concepto_id;
		}

		public int getRecibo_concepto_id() {
			return recibo_concepto_id;
		}

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
		ReciboConcepto other = (ReciboConcepto) obj;
		if (id != other.id)
			return false;
		return true;
	}

	public String getEntidad() {
		return entidad;
	}

	public void setEntidad(String entidad) {
		this.entidad = entidad;
	}
	public String getComproNroAntic() {
		return comproNroAntic;
	}
	public void setComproNroAntic(String comproNroAntic) {
		this.comproNroAntic = comproNroAntic;
	}
	
}
