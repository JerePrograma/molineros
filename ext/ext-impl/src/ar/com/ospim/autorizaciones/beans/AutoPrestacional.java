package ar.com.ospim.autorizaciones.beans;

import java.util.Date;

import com.liferay.portal.model.User;

public class AutoPrestacional {
	
	private int idTratamiento;
	private int idPrestacion;
	private String cuil; 
	private int inte;
	private String cantidad; 
	private String importeTotal;
	private String periodicidad;
	private Date periodoDesde;
	private Date periodoHasta;
	private User user;
	private String cuit;
	private String prestador; 
	private String idSeccional;
	private String observaciones;
	private boolean recuperaApe; 
	private int estado;
	private String documentacion;
	private String cantidadViajesMes;
	private String cantidadKilometrosDia;
	private String cantidadKilometrosMes;
	private String importeKilometroUnit;
	private String hsEsperaDia;
	private String hsEsperaMes;
	private String importeHsEsperaUnit;
	private String importeTercerizado;
	private String idTercerizadora;
	private int idPrestador;
	private String esExcepcion;
	private boolean esDiscapacitado;
	private String motivoExcepcion;
	private boolean esLecheMaternizada;
	private boolean esConDependencia;
	private String observacionesInternas;
	private Integer copago;
	
	
	
	public AutoPrestacional(int idTratamiento, int idPrestacion, String cuil, int inte, String cantidad, String importeTotal,
			String periodicidad, Date periodoDesde, Date periodoHasta, User user, String cuit, String prestador,
			String idSeccional, String observaciones, boolean recuperaApe, int estado, String documentacion,
			String cantidadViajesMes, String cantidadKilometrosDia, String cantidadKilometrosMes,
			String importeKilometroUnit, String hsEsperaDia, String hsEsperaMes, String importeHsEsperaUnit,
			String importeTercerizado, String idTercerizadora, int idPrestador, String esExcepcion,
			boolean esDiscapacitado, String motivoExcepcion) {
		super();
		this.idTratamiento = idTratamiento;
		this.idPrestacion = idPrestacion;
		this.cuil = cuil;
		this.inte = inte;
		this.cantidad = cantidad;
		this.importeTotal = importeTotal;
		this.periodicidad = periodicidad;
		this.periodoDesde = periodoDesde;
		this.periodoHasta = periodoHasta;
		this.user = user;
		this.cuit = cuit;
		this.prestador = prestador;
		this.idSeccional = idSeccional;
		this.observaciones = observaciones;
		this.recuperaApe = recuperaApe;
		this.estado = estado;
		this.documentacion = documentacion;
		this.cantidadViajesMes = cantidadViajesMes;
		this.cantidadKilometrosDia = cantidadKilometrosDia;
		this.cantidadKilometrosMes = cantidadKilometrosMes;
		this.importeKilometroUnit = importeKilometroUnit;
		this.hsEsperaDia = hsEsperaDia;
		this.hsEsperaMes = hsEsperaMes;
		this.importeHsEsperaUnit = importeHsEsperaUnit;
		this.importeTercerizado = importeTercerizado;
		this.idTercerizadora = idTercerizadora;
		this.idPrestador = idPrestador;
		this.esExcepcion = esExcepcion;
		this.esDiscapacitado = esDiscapacitado;
		this.motivoExcepcion = motivoExcepcion;
	}
	
	
	public AutoPrestacional(int idTratamiento, int idPrestacion, String cuil, int inte, String cantidad, String importeTotal,
			String periodicidad, Date periodoDesde, Date periodoHasta, User user, String cuit, String prestador,
			String idSeccional, String observaciones, boolean recuperaApe, int estado, String documentacion,
			String cantidadViajesMes, String cantidadKilometrosDia, String cantidadKilometrosMes,
			String importeKilometroUnit, String hsEsperaDia, String hsEsperaMes, String importeHsEsperaUnit,
			String importeTercerizado, String idTercerizadora, int idPrestador, String esExcepcion,
			boolean esDiscapacitado, String motivoExcepcion,boolean esLecheMaternizada) {
		super();
		this.idTratamiento = idTratamiento;
		this.idPrestacion = idPrestacion;
		this.cuil = cuil;
		this.inte = inte;
		this.cantidad = cantidad;
		this.importeTotal = importeTotal;
		this.periodicidad = periodicidad;
		this.periodoDesde = periodoDesde;
		this.periodoHasta = periodoHasta;
		this.user = user;
		this.cuit = cuit;
		this.prestador = prestador;
		this.idSeccional = idSeccional;
		this.observaciones = observaciones;
		this.recuperaApe = recuperaApe;
		this.estado = estado;
		this.documentacion = documentacion;
		this.cantidadViajesMes = cantidadViajesMes;
		this.cantidadKilometrosDia = cantidadKilometrosDia;
		this.cantidadKilometrosMes = cantidadKilometrosMes;
		this.importeKilometroUnit = importeKilometroUnit;
		this.hsEsperaDia = hsEsperaDia;
		this.hsEsperaMes = hsEsperaMes;
		this.importeHsEsperaUnit = importeHsEsperaUnit;
		this.importeTercerizado = importeTercerizado;
		this.idTercerizadora = idTercerizadora;
		this.idPrestador = idPrestador;
		this.esExcepcion = esExcepcion;
		this.esDiscapacitado = esDiscapacitado;
		this.motivoExcepcion = motivoExcepcion;
		this.esLecheMaternizada=esLecheMaternizada;
	}
	


	public int getIdPrestacion() {
		return idPrestacion;
	}
	public void setIdPrestacion(int idPrestacion) {
		this.idPrestacion = idPrestacion;
	}
	public String getCuil() {
		return cuil;
	}
	public void setCuil(String cuil) {
		this.cuil = cuil;
	}
	public int getInte() {
		return inte;
	}
	public void setInte(int inte) {
		this.inte = inte;
	}
	public String getCantidad() {
		return cantidad;
	}
	public void setCantidad(String cantidad) {
		this.cantidad = cantidad;
	}
	public String getImporteTotal() {
		return importeTotal;
	}
	public void setImporteTotal(String importeTotal) {
		this.importeTotal = importeTotal;
	}
	public String getPeriodicidad() {
		return periodicidad;
	}
	public void setPeriodicidad(String periodicidad) {
		this.periodicidad = periodicidad;
	}
	public Date getPeriodoDesde() {
		return periodoDesde;
	}
	public void setPeriodoDesde(Date periodoDesde) {
		this.periodoDesde = periodoDesde;
	}
	public Date getPeriodoHasta() {
		return periodoHasta;
	}
	public void setPeriodoHasta(Date periodoHasta) {
		this.periodoHasta = periodoHasta;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public String getCuit() {
		return cuit;
	}
	public void setCuit(String cuit) {
		this.cuit = cuit;
	}
	public String getPrestador() {
		return prestador;
	}
	public void setPrestador(String prestador) {
		this.prestador = prestador;
	}
	public String getIdSeccional() {
		return idSeccional;
	}
	public void setIdSeccional(String idSeccional) {
		this.idSeccional = idSeccional;
	}
	public String getObservaciones() {
		return observaciones;
	}
	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}
	public boolean isRecuperaApe() {
		return recuperaApe;
	}
	public void setRecuperaApe(boolean recuperaApe) {
		this.recuperaApe = recuperaApe;
	}
	public int getEstado() {
		return estado;
	}
	public void setEstado(int estado) {
		this.estado = estado;
	}
	public String getDocumentacion() {
		return documentacion;
	}
	public void setDocumentacion(String documentacion) {
		this.documentacion = documentacion;
	}
	public String getCantidadViajesMes() {
		return cantidadViajesMes;
	}
	public void setCantidadViajesMes(String cantidadViajesMes) {
		this.cantidadViajesMes = cantidadViajesMes;
	}
	public String getCantidadKilometrosDia() {
		return cantidadKilometrosDia;
	}
	public void setCantidadKilometrosDia(String cantidadKilometrosDia) {
		this.cantidadKilometrosDia = cantidadKilometrosDia;
	}
	public String getCantidadKilometrosMes() {
		return cantidadKilometrosMes;
	}
	public void setCantidadKilometrosMes(String cantidadKilometrosMes) {
		this.cantidadKilometrosMes = cantidadKilometrosMes;
	}
	public String getImporteKilometroUnit() {
		return importeKilometroUnit;
	}
	public void setImporteKilometroUnit(String importeKilometroUnit) {
		this.importeKilometroUnit = importeKilometroUnit;
	}
	public String getHsEsperaDia() {
		return hsEsperaDia;
	}
	public void setHsEsperaDia(String hsEsperaDia) {
		this.hsEsperaDia = hsEsperaDia;
	}
	public String getHsEsperaMes() {
		return hsEsperaMes;
	}
	public void setHsEsperaMes(String hsEsperaMes) {
		this.hsEsperaMes = hsEsperaMes;
	}
	public String getImporteHsEsperaUnit() {
		return importeHsEsperaUnit;
	}
	public void setImporteHsEsperaUnit(String importeHsEsperaUnit) {
		this.importeHsEsperaUnit = importeHsEsperaUnit;
	}
	public String getImporteTercerizado() {
		return importeTercerizado;
	}
	public void setImporteTercerizado(String importeTercerizado) {
		this.importeTercerizado = importeTercerizado;
	}
	public String getIdTercerizadora() {
		return idTercerizadora;
	}
	public void setIdTercerizadora(String idTercerizadora) {
		this.idTercerizadora = idTercerizadora;
	}
	public int getIdPrestador() {
		return idPrestador;
	}
	public void setIdPrestador(int idPrestador) {
		this.idPrestador = idPrestador;
	}
	public String getEsExcepcion() {
		return esExcepcion;
	}
	public void setEsExcepcion(String esExcepcion) {
		this.esExcepcion = esExcepcion;
	}
	public boolean isEsDiscapacitado() {
		return esDiscapacitado;
	}
	public void setEsDiscapacitado(boolean esDiscapacitado) {
		this.esDiscapacitado = esDiscapacitado;
	}
	public String getMotivoExcepcion() {
		return motivoExcepcion;
	}
	public void setMotivoExcepcion(String motivoExcepcion) {
		this.motivoExcepcion = motivoExcepcion;
	}


	public int getIdTratamiento() {
		return idTratamiento;
	}


	public void setIdTratamiento(int idTratamiento) {
		this.idTratamiento = idTratamiento;
	}


	public boolean isEsLecheMaternizada() {
		return esLecheMaternizada;
	}


	public void setEsLecheMaternizada(boolean esLecheMaternizada) {
		this.esLecheMaternizada = esLecheMaternizada;
	}

	
	public boolean isEsConDependencia() {
		return esConDependencia;
	}


	public void setEsConDependencia(boolean esConDependencia) {
		this.esConDependencia = esConDependencia;
	}

	public String getObservacionesInternas() {
		return observacionesInternas;
	}


	public void setObservacionesInternas(String observacionesInternas) {
		this.observacionesInternas = observacionesInternas;
	}

	public Integer getCopago() {
		return copago;
	}


	public void setCopago(Integer copago) {
		this.copago = copago;
	}


	public AutoPrestacional(int idTratamiento, int idPrestacion, String cuil, int inte, String cantidad, String importeTotal,
			String periodicidad, Date periodoDesde, Date periodoHasta, User user, String cuit, String prestador,
			String idSeccional, String observaciones, boolean recuperaApe, int estado, String documentacion,
			String cantidadViajesMes, String cantidadKilometrosDia, String cantidadKilometrosMes,
			String importeKilometroUnit, String hsEsperaDia, String hsEsperaMes, String importeHsEsperaUnit,
			String importeTercerizado, String idTercerizadora, int idPrestador, String esExcepcion,
			boolean esDiscapacitado, String motivoExcepcion,boolean esLecheMaternizada,boolean esDependencia) {
		super();
		this.idTratamiento = idTratamiento;
		this.idPrestacion = idPrestacion;
		this.cuil = cuil;
		this.inte = inte;
		this.cantidad = cantidad;
		this.importeTotal = importeTotal;
		this.periodicidad = periodicidad;
		this.periodoDesde = periodoDesde;
		this.periodoHasta = periodoHasta;
		this.user = user;
		this.cuit = cuit;
		this.prestador = prestador;
		this.idSeccional = idSeccional;
		this.observaciones = observaciones;
		this.recuperaApe = recuperaApe;
		this.estado = estado;
		this.documentacion = documentacion;
		this.cantidadViajesMes = cantidadViajesMes;
		this.cantidadKilometrosDia = cantidadKilometrosDia;
		this.cantidadKilometrosMes = cantidadKilometrosMes;
		this.importeKilometroUnit = importeKilometroUnit;
		this.hsEsperaDia = hsEsperaDia;
		this.hsEsperaMes = hsEsperaMes;
		this.importeHsEsperaUnit = importeHsEsperaUnit;
		this.importeTercerizado = importeTercerizado;
		this.idTercerizadora = idTercerizadora;
		this.idPrestador = idPrestador;
		this.esExcepcion = esExcepcion;
		this.esDiscapacitado = esDiscapacitado;
		this.motivoExcepcion = motivoExcepcion;
		this.esLecheMaternizada=esLecheMaternizada;
		this.esConDependencia=esDependencia;
	}

	public AutoPrestacional(int idTratamiento, int idPrestacion, String cuil, int inte, String cantidad, String importeTotal,
			String periodicidad, Date periodoDesde, Date periodoHasta, User user, String cuit, String prestador,
			String idSeccional, String observaciones, boolean recuperaApe, int estado, String documentacion,
			String cantidadViajesMes, String cantidadKilometrosDia, String cantidadKilometrosMes,
			String importeKilometroUnit, String hsEsperaDia, String hsEsperaMes, String importeHsEsperaUnit,
			String importeTercerizado, String idTercerizadora, int idPrestador, String esExcepcion,
			boolean esDiscapacitado, String motivoExcepcion,boolean esLecheMaternizada,boolean esDependencia,String observaciones_int) {
		super();
		this.idTratamiento = idTratamiento;
		this.idPrestacion = idPrestacion;
		this.cuil = cuil;
		this.inte = inte;
		this.cantidad = cantidad;
		this.importeTotal = importeTotal;
		this.periodicidad = periodicidad;
		this.periodoDesde = periodoDesde;
		this.periodoHasta = periodoHasta;
		this.user = user;
		this.cuit = cuit;
		this.prestador = prestador;
		this.idSeccional = idSeccional;
		this.observaciones = observaciones;
		this.recuperaApe = recuperaApe;
		this.estado = estado;
		this.documentacion = documentacion;
		this.cantidadViajesMes = cantidadViajesMes;
		this.cantidadKilometrosDia = cantidadKilometrosDia;
		this.cantidadKilometrosMes = cantidadKilometrosMes;
		this.importeKilometroUnit = importeKilometroUnit;
		this.hsEsperaDia = hsEsperaDia;
		this.hsEsperaMes = hsEsperaMes;
		this.importeHsEsperaUnit = importeHsEsperaUnit;
		this.importeTercerizado = importeTercerizado;
		this.idTercerizadora = idTercerizadora;
		this.idPrestador = idPrestador;
		this.esExcepcion = esExcepcion;
		this.esDiscapacitado = esDiscapacitado;
		this.motivoExcepcion = motivoExcepcion;
		this.esLecheMaternizada=esLecheMaternizada;
		this.esConDependencia=esDependencia;
		this.setObservacionesInternas(observaciones_int);
	}

	
}

