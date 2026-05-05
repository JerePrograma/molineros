package ar.com.ospim.autorizaciones.beans;

import java.io.Serializable;
import java.util.Date;

public class BusquedaPreautorizacionesFiltro implements Serializable {


	/**
	 * 
	 */
	private static final long serialVersionUID = 7267471908351937910L;
	private Integer id;
	private String cuil;
	private Integer inte;
	private Date fechaD;
	private Date fechaH;
	private String estado;
	private Date fechaEmail;
	private Date fechaEmailH;
	private Integer seccional;
	private boolean alertaRoja;
	private boolean discapacidad;
	private boolean supra;
	private boolean cirugia;
	private boolean medicamento;
	private boolean sinReintento;
	private boolean alojamiento;
	private Integer idAutorizacion;
	private boolean protesisOrt;
	private boolean ART;
	private boolean diabetes;
	
	private int pagina;
	private int registrosTotal;
	private final int registrosPorPagina = 50;
	
	
	

	public BusquedaPreautorizacionesFiltro(Integer id, String cuil, Integer inte, Date fechaD, Date fechaH,
			String estado, Date fechaEmail, Date fechaEmailH, Integer seccional, boolean alertaRoja,
			boolean discapacidad, boolean supra, boolean cirugia, boolean medicamento, boolean sinReintento, boolean alojamiento,
			Integer idAutorizacion, boolean protesisOrtesis, int pagina) {
		
		super();
		this.id = id;
		this.cuil = cuil;
		this.inte = inte;
		this.fechaD = fechaD;
		this.fechaH = fechaH;
		this.estado = estado;
		this.fechaEmail = fechaEmail;
		this.fechaEmailH = fechaEmailH;
		this.seccional = seccional;
		this.alertaRoja = alertaRoja;
		this.discapacidad = discapacidad;
		this.supra = supra;
		this.cirugia = cirugia;
		this.medicamento = medicamento;
		this.idAutorizacion = idAutorizacion;
		this.sinReintento = sinReintento;
		this.alojamiento=alojamiento;
		this.protesisOrt=protesisOrtesis;
		this.pagina = pagina;
	}
	
	public BusquedaPreautorizacionesFiltro(Integer id, String cuil, Integer inte, Date fechaD, Date fechaH,
			String estado, Date fechaEmail, Date fechaEmailH, Integer seccional, boolean alertaRoja,
			boolean discapacidad, boolean supra, boolean cirugia, boolean medicamento, boolean sinReintento,
			boolean alojamiento, Integer idAutorizacion, boolean protesisOrt, boolean ART, int pagina) {
		super();
		this.id = id;
		this.cuil = cuil;
		this.inte = inte;
		this.fechaD = fechaD;
		this.fechaH = fechaH;
		this.estado = estado;
		this.fechaEmail = fechaEmail;
		this.fechaEmailH = fechaEmailH;
		this.seccional = seccional;
		this.alertaRoja = alertaRoja;
		this.discapacidad = discapacidad;
		this.supra = supra;
		this.cirugia = cirugia;
		this.medicamento = medicamento;
		this.sinReintento = sinReintento;
		this.alojamiento = alojamiento;
		this.idAutorizacion = idAutorizacion;
		this.protesisOrt = protesisOrt;
		this.ART = ART;
		this.pagina = pagina;
		this.diabetes=diabetes;
	}
	
	public BusquedaPreautorizacionesFiltro(Integer id, String cuil, Integer inte, Date fechaD, Date fechaH,
			String estado, Date fechaEmail, Date fechaEmailH, Integer seccional, boolean alertaRoja,
			boolean discapacidad, boolean supra, boolean cirugia, boolean medicamento, boolean sinReintento,
			boolean alojamiento, Integer idAutorizacion, boolean protesisOrt, boolean ART,boolean diabetes, int pagina) {
		super();
		this.id = id;
		this.cuil = cuil;
		this.inte = inte;
		this.fechaD = fechaD;
		this.fechaH = fechaH;
		this.estado = estado;
		this.fechaEmail = fechaEmail;
		this.fechaEmailH = fechaEmailH;
		this.seccional = seccional;
		this.alertaRoja = alertaRoja;
		this.discapacidad = discapacidad;
		this.supra = supra;
		this.cirugia = cirugia;
		this.medicamento = medicamento;
		this.sinReintento = sinReintento;
		this.alojamiento = alojamiento;
		this.idAutorizacion = idAutorizacion;
		this.protesisOrt = protesisOrt;
		this.ART = ART;
		this.pagina = pagina;
		this.diabetes=diabetes;
	}



	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getCuil() {
		return cuil;
	}

	public void setCuil(String cuil) {
		this.cuil = cuil;
	}

	public Integer getInte() {
		return inte;
	}

	public void setInte(Integer inte) {
		this.inte = inte;
	}

	public Date getFechaD() {
		return fechaD;
	}

	public void setFechaD(Date fechaD) {
		this.fechaD = fechaD;
	}

	public Date getFechaH() {
		return fechaH;
	}

	public void setFechaH(Date fechaH) {
		this.fechaH = fechaH;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public Date getFechaEmail() {
		return fechaEmail;
	}

	public void setFechaEmail(Date fechaEmail) {
		this.fechaEmail = fechaEmail;
	}

	public Date getFechaEmailH() {
		return fechaEmailH;
	}

	public void setFechaEmailH(Date fechaEmailH) {
		this.fechaEmailH = fechaEmailH;
	}

	public Integer getSeccional() {
		return seccional;
	}

	public void setSeccional(Integer seccional) {
		this.seccional = seccional;
	}

	public boolean isAlertaRoja() {
		return alertaRoja;
	}

	public void setAlertaRoja(boolean alertaRoja) {
		this.alertaRoja = alertaRoja;
	}

	public boolean isDiscapacidad() {
		return discapacidad;
	}

	public void setDiscapacidad(boolean discapacidad) {
		this.discapacidad = discapacidad;
	}

	public boolean isSupra() {
		return supra;
	}

	public void setSupra(boolean supra) {
		this.supra = supra;
	}

	public boolean isCirugia() {
		return cirugia;
	}

	public void setCirugia(boolean cirugia) {
		this.cirugia = cirugia;
	}

	public boolean isMedicamento() {
		return medicamento;
	}

	public void setMedicamento(boolean medicamento) {
		this.medicamento = medicamento;
	}

	public Integer getIdAutorizacion() {
		return idAutorizacion;
	}

	public void setIdAutorizacion(Integer idAutorizacion) {
		this.idAutorizacion = idAutorizacion;
	}

	public int getPagina() {
		return pagina;
	}
	
	public void setPagina(int pagina) {
		this.pagina = pagina;
	}
	
	public int getRegistrosTotal() {
		return registrosTotal;
	}
	
	public void setRegistrosTotal(int registrosTotal) {
		this.registrosTotal = registrosTotal;
	}
	
	public int getRegistrosPorPagina() {
		return registrosPorPagina;
	}

	public boolean isSinReintento() {
		return sinReintento;
	}

	public void setSinReintento(boolean sinReintento) {
		this.sinReintento = sinReintento;
	}

	public boolean isAlojamiento() {
		return alojamiento;
	}

	public void setAlojamiento(boolean alojamiento) {
		this.alojamiento = alojamiento;
	}

	public boolean isProtesisOrt() {
		return protesisOrt;
	}

	public void setProtesisOrt(boolean protesisOrt) {
		this.protesisOrt = protesisOrt;
	}

	public boolean isART() {
		return ART;
	}

	public void setART(boolean aRT) {
		ART = aRT;
	}

	public boolean isDiabetes() {
		return diabetes;
	}

	public void setDiabetes(boolean diabetes) {
		this.diabetes = diabetes;
	}
	
}
