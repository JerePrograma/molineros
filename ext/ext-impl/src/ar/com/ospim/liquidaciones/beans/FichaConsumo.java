package ar.com.ospim.liquidaciones.beans;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author crivas
 * @version 1.0
 * @created 01-Sep-2012 04:30:50 p.m.
 */
public class FichaConsumo {

	String tipo_consumo;
	int id_liquidacion;
	Date fecha_prestacion;
	String apellido;
	String nombre;
	String docu_numero;
	String secciona;
	String cuit;
	String razon_soc;
	String codigo;
	String descripcion;
	String presentacion;
	String laboratorio;
	String pieza;
	String cara;
	BigDecimal importe_total;
	int nro_cuota;
	int porcentaje_cuota;
	BigDecimal cantidad;
	BigDecimal importe;
	BigDecimal ospim;
	BigDecimal amtima;
	BigDecimal uoma;
	String receta;
	BigDecimal porcentaje;
	String localidad_prestador;
	String prov_prestador;
	BigDecimal debitado_omint;
	int id_orden_pago;
	String discapacitado;
	int cta;
	Date periodo;
	String cuil_titular;
	int inte;
	Date fecha;
	Date fecha_comprobante;
	BigDecimal importe_comprobante;
	String tercerizado;
	Date op_fecha;
	String comprobante;
	String ciex;
	String diagnostico;
	String provinciaSecc;
	String plan;
	
	public FichaConsumo(String tipoConsumo, int idLiquidacion,
			Date fechaPrestacion, String apellido, String nombre,
			String docuNumero, String secciona, String cuit, String razonSoc,
			String codigo, String descripcion, String presentacion,
			String laboratorio, String pieza, String cara,
			BigDecimal importeTotal, int nroCuota, int porcentajeCuota,
			BigDecimal cantidad, BigDecimal importe, BigDecimal ospim, BigDecimal amtima,
			BigDecimal uoma,String receta, BigDecimal porcentaje, String localidadPrestador,
			String provPrestador, BigDecimal debitadoOmint, int idOrdenPago,
			String discapacitado, int cta, Date periodo, String cuil_titular, 
			int inte, Date fecha, Date fecha_comprobante, BigDecimal importe_comprobante, 
			String tercerizado, Date op_fecha, String comprobante, String provinciaSecc) {
		super();
		tipo_consumo = tipoConsumo;
		id_liquidacion = idLiquidacion;
		fecha_prestacion = fechaPrestacion;
		this.apellido = apellido;
		this.nombre = nombre;
		docu_numero = docuNumero;
		this.secciona = secciona;
		this.cuit = cuit;
		razon_soc = razonSoc;
		this.codigo = codigo;
		this.descripcion = descripcion;
		this.presentacion = presentacion;
		this.laboratorio = laboratorio;
		this.pieza = pieza;
		this.cara = cara;
		importe_total = importeTotal;
		nro_cuota = nroCuota;
		porcentaje_cuota = porcentajeCuota;
		this.cantidad = cantidad;
		this.importe = importe;
		this.ospim = ospim;
		this.amtima = amtima;
		this.uoma= uoma;
		this.receta = receta;
		this.porcentaje = porcentaje;
		localidad_prestador = localidadPrestador;
		prov_prestador = provPrestador;
		debitado_omint = debitadoOmint;
		id_orden_pago = idOrdenPago;
		this.discapacitado = discapacitado;
		this.cta = cta;
		this.periodo = periodo;
		this.cuil_titular = cuil_titular;
		this.inte = inte;
		this.fecha = fecha;
		this.fecha_comprobante = fecha_comprobante;
		this.importe_comprobante = importe_comprobante; 
		this.tercerizado = tercerizado; 
		this.op_fecha = op_fecha;
		this.comprobante = comprobante;
		this.provinciaSecc = provinciaSecc;
	}

	public String getTipo_consumo() {
		return tipo_consumo;
	}

	public void setTipo_consumo(String tipoConsumo) {
		tipo_consumo = tipoConsumo;
	}

	public int getId_liquidacion() {
		return id_liquidacion;
	}

	public void setId_liquidacion(int idLiquidacion) {
		id_liquidacion = idLiquidacion;
	}

	public Date getFecha_prestacion() {
		return fecha_prestacion;
	}

	public void setFecha_prestacion(Date fechaPrestacion) {
		fecha_prestacion = fechaPrestacion;
	}

	public String getApellido() {
		return apellido;
	}

	public void setApellido(String apellido) {
		this.apellido = apellido;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getDocu_numero() {
		return docu_numero;
	}

	public void setDocu_numero(String docuNumero) {
		docu_numero = docuNumero;
	}

	public String getSecciona() {
		return secciona;
	}

	public void setSecciona(String secciona) {
		this.secciona = secciona;
	}

	public String getCuit() {
		return cuit;
	}

	public void setCuit(String cuit) {
		this.cuit = cuit;
	}

	public String getRazon_soc() {
		return razon_soc;
	}

	public void setRazon_soc(String razonSoc) {
		razon_soc = razonSoc;
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

	public String getPresentacion() {
		return presentacion;
	}

	public void setPresentacion(String presentacion) {
		this.presentacion = presentacion;
	}

	public String getLaboratorio() {
		return laboratorio;
	}

	public void setLaboratorio(String laboratorio) {
		this.laboratorio = laboratorio;
	}

	public String getPieza() {
		return pieza;
	}

	public void setPieza(String pieza) {
		this.pieza = pieza;
	}

	public String getCara() {
		return cara;
	}

	public void setCara(String cara) {
		this.cara = cara;
	}

	public BigDecimal getImporte_total() {
		return importe_total;
	}

	public void setImporte_total(BigDecimal importeTotal) {
		importe_total = importeTotal;
	}

	public int getNro_cuota() {
		return nro_cuota;
	}

	public void setNro_cuota(int nroCuota) {
		nro_cuota = nroCuota;
	}

	public int getPorcentaje_cuota() {
		return porcentaje_cuota;
	}

	public void setPorcentaje_cuota(int porcentajeCuota) {
		porcentaje_cuota = porcentajeCuota;
	}

	public BigDecimal getCantidad() {
		return cantidad;
	}

	public void setCantidad(BigDecimal cantidad) {
		this.cantidad = cantidad;
	}

	public BigDecimal getImporte() {
		return importe;
	}

	public void setImporte(BigDecimal importe) {
		this.importe = importe;
	}

	public BigDecimal getOspim() {
		return ospim;
	}

	public void setOspim(BigDecimal ospim) {
		this.ospim = ospim;
	}

	public BigDecimal getAmtima() {
		return amtima;
	}

	public void setAmtima(BigDecimal amtima) {
		this.amtima = amtima;
	}

	public String getReceta() {
		return receta;
	}

	public void setReceta(String receta) {
		this.receta = receta;
	}

	public BigDecimal getPorcentaje() {
		return porcentaje;
	}

	public void setPorcentaje(BigDecimal porcentaje) {
		this.porcentaje = porcentaje;
	}

	public String getLocalidad_prestador() {
		return localidad_prestador;
	}

	public void setLocalidad_prestador(String localidadPrestador) {
		localidad_prestador = localidadPrestador;
	}

	public String getProv_prestador() {
		return prov_prestador;
	}

	public void setProv_prestador(String provPrestador) {
		prov_prestador = provPrestador;
	}

	public BigDecimal getDebitado_omint() {
		return debitado_omint;
	}

	public void setDebitado_omint(BigDecimal debitadoOmint) {
		debitado_omint = debitadoOmint;
	}

	public int getId_orden_pago() {
		return id_orden_pago;
	}

	public void setId_orden_pago(int idOrdenPago) {
		id_orden_pago = idOrdenPago;
	}

	public String getDiscapacitado() {
		return discapacitado;
	}

	public void setDiscapacitado(String discapacitado) {
		this.discapacitado = discapacitado;
	}

	public int getCta() {
		return cta;
	}

	public void setCta(int cta) {
		this.cta = cta;
	}

	public Date getPeriodo() {
		return periodo;
	}

	public void setPeriodo(Date periodo) {
		this.periodo = periodo;
	}

	public String getCuil_titular() {
		return cuil_titular;
	}

	public void setCuil_titular(String cuilTitular) {
		cuil_titular = cuilTitular;
	}

	public int getInte() {
		return inte;
	}

	public void setInte(int inte) {
		this.inte = inte;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public Date getFecha_comprobante() {
		return fecha_comprobante;
	}

	public void setFecha_comprobante(Date fechaComprobante) {
		fecha_comprobante = fechaComprobante;
	}

	public BigDecimal getImporte_comprobante() {
		return importe_comprobante;
	}

	public void setImporte_comprobante(BigDecimal importeComprobante) {
		importe_comprobante = importeComprobante;
	}

	public String getTercerizado() {
		return tercerizado;
	}

	public void setTercerizado(String tercerizado) {
		this.tercerizado = tercerizado;
	}

	public Date getOp_fecha() {
		return op_fecha;
	}

	public void setOp_fecha(Date opFecha) {
		op_fecha = opFecha;
	}

	public String getComprobante() {
		return comprobante;
	}

	public void setComprobante(String comprobante) {
		this.comprobante = comprobante;
	}

	public String getCiex() {
		return ciex;
	}

	public void setCiex(String ciex) {
		this.ciex = ciex;
	}

	public String getDiagnostico() {
		return diagnostico;
	}

	public void setDiagnostico(String diagnostico) {
		this.diagnostico = diagnostico;
	}

	public String getProvinciaSecc() {
		return provinciaSecc;
	}

	public void setProvinciaSecc(String provinciaSecc) {
		this.provinciaSecc = provinciaSecc;
	}

	public BigDecimal getUoma() {
		return uoma;
	}

	public void setUoma(BigDecimal uoma) {
		this.uoma = uoma;
	}

	public String getPlan() {
		return plan;
	}

	public void setPlan(String plan) {
		this.plan = plan;
	}
	
	

}