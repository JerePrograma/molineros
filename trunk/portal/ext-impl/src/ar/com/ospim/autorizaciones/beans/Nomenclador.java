package ar.com.ospim.autorizaciones.beans;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Nomenclador implements Serializable{

	private static final long serialVersionUID = 742694095159319193L;
	private int id_prestacion;
	private int id_tipo_nomenclador;
	private String codigo;
	private String descripcion;
	private Date baja_fecha;
	private String descripcionTipoNomenclador;
	private String especialidadDescripcion;
	private int id_especialidad;
	private Boolean recuperaSUR;
	private String resolucion;
	private Double importe;
	private Double cantidadGaleno;
	private Double cantidadGalenoAyudante;
	private Double cantidadGalenoAnestesista;
	private Double valorGaleno;
	private String codigoHospital;
	private Double cantidadAyudantes;
	private Double cantidadGalenoGastos;
	private Double valorGalenoGastos;
	private int marcaReintegroLiquidacion;
	private Double coeficienteGastos;
	private Double coeficienteHonorarios;
	private int troquelMedicamento;
	private boolean modulo;
	private Double importeHonorarios;
	private Double importeGastos;
	private boolean requiereAutorizacion;
	private String observaciones;
	private Double cantidad;
	private Integer idPrestacionOSPIM;
	
	private boolean requiereHistoriaClinica;
	private boolean requiereEstudiosComplementarios;
	private boolean requiereBiopsia;
	private boolean requiereAnatomiaPatologica;
	private boolean supra;
	private boolean cirugia;
	private boolean planBasico;
	private boolean enviarWSTercerizadora;
	
	private Integer cantidadDesde;
	private Integer cantidadHasta;
	private Integer cantidadCorrecta;
	
	public static Nomenclador getMapping(ResultSet rs) throws SQLException {
		
		Nomenclador nome = new Nomenclador();
		
		nome.setBaja_fecha(rs.getDate("baja_fecha"));
		nome.setCodigo(rs.getString("codigo"));
		nome.setDescripcion(rs.getString("descripcion"));
		nome.setDescripcionTipoNomenclador(rs.getString("descripcionTipoNomenclador"));
		nome.setId_prestacion(rs.getInt("id_prestacion"));
		nome.setId_tipo_nomenclador(rs.getInt("id_tipo_nomenclador"));
		nome.setEspecialidadDescripcion(rs.getString("descripcionEspecialidad")==null || 
				   "null".equalsIgnoreCase(rs.getString("descripcionEspecialidad"))?"":rs.getString("descripcionEspecialidad"));
		nome.setId_especialidad(rs.getInt("id_especialidad"));
		nome.setRecuperaSUR(rs.getBoolean("recuperaSUR"));
		nome.setResolucion(rs.getString("resolucion"));
		nome.setCodigoHospital(rs.getString("codigo_hospital"));
		
		nome.setCantidadGaleno(rs.getDouble("cantidad_galeno"));
		nome.setCantidadGalenoAnestesista(rs.getDouble("cantidad_galeno_anestesista"));
		nome.setCantidadGalenoAyudante(rs.getDouble("cantidad_galeno_ayudante"));
		nome.setValorGaleno(rs.getDouble("valor_galeno"));
		nome.setCantidadAyudantes(rs.getDouble("cantidad_ayudantes"));
		nome.setImporte(rs.getDouble("importe"));
		
		nome.setCantidadGalenoGastos(rs.getDouble("cantidad_galeno_gastos"));
		nome.setValorGalenoGastos(rs.getDouble("valor_galeno_gastos"));
		
		nome.setMarcaReintegroLiquidacion(rs.getInt("marca_rein_liq"));
		nome.setCoeficienteGastos(rs.getDouble("coef_gastos"));
		nome.setCoeficienteHonorarios(rs.getDouble("coef_honorarios"));
		nome.setModulo(rs.getBoolean("modulo"));
		nome.setImporteGastos(rs.getDouble("importe_gastos"));
		nome.setImporteHonorarios(rs.getDouble("importe_honorarios"));
		nome.setRequiereAutorizacion(rs.getBoolean("requiere_autorizacion"));
		
		nome.setObservaciones(rs.getString("observaciones"));
		
		nome.setSupra(rs.getBoolean("supra"));
		
		nome.setCirugia(rs.getBoolean("cirugia"));
		
		nome.setPlanBasico(rs.getBoolean("plan_basico"));
		
		nome.setEnviarWSTercerizadora(rs.getBoolean("enviar_ws_tercerizadora"));
		
		return nome;
	}
	
	public final Date getBaja_fecha() {
		return baja_fecha;
	}
	
	public String getBaja_Fecha_string() {
		SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");
		return baja_fecha != null ? sdf.format(baja_fecha)
				: "";
		}

	public final void setBaja_fecha(Date baja_fecha) {
		this.baja_fecha = baja_fecha;
	}

	public int getId_prestacion() {
		return id_prestacion;
	}

	public void setId_prestacion(int id_prestacion) {
		this.id_prestacion = id_prestacion;
	}
	
	public String getId_prestacion_string() {
		String id_prestacion = Integer.toString(getId_prestacion());
		return id_prestacion;
	}

	public int getId_tipo_nomenclador() {
		return id_tipo_nomenclador;
	}

	public void setId_tipo_nomenclador(int id_tipo_nomenclador) {
		this.id_tipo_nomenclador = id_tipo_nomenclador;
	}
	
	public String getId_tipo_nomenclador_string() {
		String id_tipo_nomenclador = Integer.toString(getId_tipo_nomenclador());
		return id_tipo_nomenclador;
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public String getDescripcionTipoNomenclador() {
		return descripcionTipoNomenclador;
	}

	public void setDescripcionTipoNomenclador(String descripcionTipoNomenclador) {
		this.descripcionTipoNomenclador = descripcionTipoNomenclador;
	}

	public String getEspecialidadDescripcion() {
		return especialidadDescripcion;
	}

	public void setEspecialidadDescripcion(String especialidadDescripcion) {
		this.especialidadDescripcion = especialidadDescripcion;
	}

	public Boolean getRecuperaSUR() {
		return recuperaSUR==null?false:recuperaSUR;
	}

	public void setRecuperaSUR(Boolean recuperaSUR) {
		this.recuperaSUR = recuperaSUR;
	}

	public int getId_especialidad() {
		return id_especialidad;
	}

	public void setId_especialidad(int id_especialidad) {
		this.id_especialidad = id_especialidad;
	}

	public String getResolucion() {
		return resolucion;
	}

	public void setResolucion(String resolucion) {
		this.resolucion = resolucion;
	}

	public Double getImporte() {
		return importe;
	}

	public void setImporte(Double importe) {
		this.importe = importe;
	}

	public Double getCantidadGaleno() {
		return cantidadGaleno;
	}

	public void setCantidadGaleno(Double cantidadGaleno) {
		this.cantidadGaleno = cantidadGaleno;
	}

	public Double getCantidadGalenoAyudante() {
		return cantidadGalenoAyudante;
	}

	public void setCantidadGalenoAyudante(Double cantidadGalenoAyudante) {
		this.cantidadGalenoAyudante = cantidadGalenoAyudante;
	}

	public Double getCantidadGalenoAnestesista() {
		return cantidadGalenoAnestesista;
	}

	public void setCantidadGalenoAnestesista(Double cantidadGalenoAnestesista) {
		this.cantidadGalenoAnestesista = cantidadGalenoAnestesista;
	}

	public Double getValorGaleno() {
		return valorGaleno;
	}

	public void setValorGaleno(Double valorGaleno) {
		this.valorGaleno = valorGaleno;
	}

	public String getCodigoHospital() {
		return codigoHospital;
	}

	public void setCodigoHospital(String codigoHospital) {
		this.codigoHospital = codigoHospital;
	}

	public Double getCantidadAyudantes() {
		return cantidadAyudantes;
	}

	public void setCantidadAyudantes(Double cantidadAyudantes) {
		this.cantidadAyudantes = cantidadAyudantes;
	}

	public Double getCantidadGalenoGastos() {
		return cantidadGalenoGastos;
	}

	public void setCantidadGalenoGastos(Double cantidadGalenoGastos) {
		this.cantidadGalenoGastos = cantidadGalenoGastos;
	}

	public Double getValorGalenoGastos() {
		return valorGalenoGastos;
	}

	public void setValorGalenoGastos(Double valorGalenoGastos) {
		this.valorGalenoGastos = valorGalenoGastos;
	}

	public int getMarcaReintegroLiquidacion() {
		return marcaReintegroLiquidacion;
	}

	public void setMarcaReintegroLiquidacion(int marcaReintegroLiquidacion) {
		this.marcaReintegroLiquidacion = marcaReintegroLiquidacion;
	}

	public Double getCoeficienteGastos() {
		return coeficienteGastos;
	}

	public void setCoeficienteGastos(Double coeficienteGastos) {
		this.coeficienteGastos = coeficienteGastos;
	}

	public Double getCoeficienteHonorarios() {
		return coeficienteHonorarios;
	}

	public void setCoeficienteHonorarios(Double coeficienteHonorarios) {
		this.coeficienteHonorarios = coeficienteHonorarios;
	}

	public int getTroquelMedicamento() {
		return troquelMedicamento;
	}

	public void setTroquelMedicamento(int troquelMedicamento) {
		this.troquelMedicamento = troquelMedicamento;
	}

	public boolean isModulo() {
		return modulo;
	}

	public void setModulo(boolean modulo) {
		this.modulo = modulo;
	}

	public Double getImporteHonorarios() {
		return importeHonorarios;
	}

	public void setImporteHonorarios(Double importeHonorarios) {
		this.importeHonorarios = importeHonorarios;
	}

	public Double getImporteGastos() {
		return importeGastos;
	}

	public void setImporteGastos(Double importeGastos) {
		this.importeGastos = importeGastos;
	}

	public boolean getRequiereAutorizacion() {
		return requiereAutorizacion;
	}

	public void setRequiereAutorizacion(boolean requiereAutorizacion) {
		this.requiereAutorizacion = requiereAutorizacion;
	}

	public String getObservaciones() {
		return observaciones;
	}

	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}

	public Double getCantidad() {
		return cantidad;
	}

	public void setCantidad(Double cantidad) {
		this.cantidad = cantidad;
	}

	public boolean isRequiereHistoriaClinica() {
		return requiereHistoriaClinica;
	}

	public void setRequiereHistoriaClinica(boolean requiereHistoriaClinica) {
		this.requiereHistoriaClinica = requiereHistoriaClinica;
	}

	public boolean isRequiereEstudiosComplementarios() {
		return requiereEstudiosComplementarios;
	}

	public void setRequiereEstudiosComplementarios(
			boolean requiereEstudiosComplementarios) {
		this.requiereEstudiosComplementarios = requiereEstudiosComplementarios;
	}

	public boolean isRequiereBiopsia() {
		return requiereBiopsia;
	}

	public void setRequiereBiopsia(boolean requiereBiopsia) {
		this.requiereBiopsia = requiereBiopsia;
	}

	public boolean isRequiereAnatomiaPatologica() {
		return requiereAnatomiaPatologica;
	}

	public void setRequiereAnatomiaPatologica(boolean requiereAnatomiaPatologica) {
		this.requiereAnatomiaPatologica = requiereAnatomiaPatologica;
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

	public boolean isPlanBasico() {
		return planBasico;
	}

	public void setPlanBasico(boolean planBasico) {
		this.planBasico = planBasico;
	}

	public boolean isEnviarWSTercerizadora() {
		return enviarWSTercerizadora;
	}

	public void setEnviarWSTercerizadora(boolean enviarWSTercerizadora) {
		this.enviarWSTercerizadora = enviarWSTercerizadora;
	}

	public Integer getCantidadDesde() {
		return cantidadDesde;
	}

	public void setCantidadDesde(Integer cantidadDesde) {
		this.cantidadDesde = cantidadDesde;
	}

	public Integer getCantidadHasta() {
		return cantidadHasta;
	}

	public void setCantidadHasta(Integer cantidadHasta) {
		this.cantidadHasta = cantidadHasta;
	}

	public Integer getCantidadCorrecta() {
		return cantidadCorrecta;
	}

	public void setCantidadCorrecta(Integer cantidadCorrecta) {
		this.cantidadCorrecta = cantidadCorrecta;
	}

	public Integer getIdPrestacionOSPIM() {
		return idPrestacionOSPIM;
	}

	public void setIdPrestacionOSPIM(Integer idPrestacionOSPIM) {
		this.idPrestacionOSPIM = idPrestacionOSPIM;
	}
	
	
}
