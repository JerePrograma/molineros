package ar.com.ospim.procesaArchivos.beans.vademecum;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;



public class DetalleManualDat {
	private long troquel;
	private String nombre;
	private String presentacion;
	private BigDecimal monto_ioma;
	private String norma_ioma;
	private String cober_ioma;
	private String laboratorio;
	private BigDecimal precio;
	private Date fecha_vig;
	private String controlado;
	private String importado;
	private String tipo_venta;
	private String iva;
	private String cod_dto_pami;
	private int cod_lab;
	private int nro_registro;
	private String baja;
	private String cod_barra;
	private int unidades;
	private String tamanio;
	private String heladera;
	private String sifar;
	private String baja_especial;
	private String blanco;
	
	public DetalleManualDat(String line) throws ParseException{
		super();
		
		SimpleDateFormat sdf= new SimpleDateFormat("yyyyMMdd");		
		this.troquel=Long.valueOf(line.substring(0,7).trim());
		this.nombre=line.substring(7,48);
		this.presentacion=line.substring(51,75);
//		this.presentacion=line.substring(51,77);
		this.monto_ioma=new BigDecimal(Double.valueOf(line.substring(75,81)+"."+line.substring(81,83)));
//		this.monto_ioma=new BigDecimal(Double.valueOf(line.substring(77,81)+"."+line.substring(81,83)));
		this.norma_ioma=line.substring(83,84);
		this.cober_ioma=line.substring(84,85);
		this.laboratorio=line.substring(85,101);
//		this.laboratorio=line.substring(85,102);
		this.precio=new BigDecimal(Double.valueOf(line.substring(101,108)+"."+line.substring(108,110)));
//		this.precio=new BigDecimal(Double.valueOf(line.substring(102,108)+"."+line.substring(108,110)));
//		this.precio=new BigDecimal(Double.valueOf(line.substring(103,108)+"."+line.substring(108,110)));
		this.fecha_vig=sdf.parse(line.substring(110,118));
		this.controlado=line.substring(118,119);
		this.importado=line.substring(119,120);
		this.tipo_venta=line.substring(120,121);
		this.iva=line.substring(121,122);
		this.cod_dto_pami=line.substring(122,123);
		this.cod_lab=Integer.parseInt(line.substring(123,126));
		this.nro_registro=Integer.parseInt(line.substring(126,131));
		this.baja=line.substring(131,132);
		this.cod_barra=line.substring(132,145);
		this.unidades=Integer.parseInt(line.substring(145,149));
//		this.unidades=Integer.parseInt(StringUtils.checkEmpty(line.substring(145,149).trim())?"0":line.substring(145,149).trim());
		this.tamanio=line.substring(149,150);
		this.heladera=line.substring(150,151);
		this.sifar=line.substring(151,152);
		this.baja_especial=line.substring(152,153);
	}
	
	
	public long getTroquel() {
		return troquel;
	}
	public void setTroquel(long troquel) {
		this.troquel = troquel;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getPresentacion() {
		return presentacion;
	}
	public void setPresentacion(String presentacion) {
		this.presentacion = presentacion;
	}
	public BigDecimal getMonto_ioma() {
		return monto_ioma;
	}
	public void setMonto_ioma(BigDecimal montoIoma) {
		monto_ioma = montoIoma;
	}
	public String getNorma_ioma() {
		return norma_ioma;
	}
	public void setNorma_ioma(String normaIoma) {
		norma_ioma = normaIoma;
	}
	public String getCober_ioma() {
		return cober_ioma;
	}
	public void setCober_ioma(String coberIoma) {
		cober_ioma = coberIoma;
	}
	public String getLaboratorio() {
		return laboratorio;
	}
	public void setLaboratorio(String laboratorio) {
		this.laboratorio = laboratorio;
	}
	public BigDecimal getPrecio() {
		return precio;
	}
	public void setPrecio(BigDecimal precio) {
		this.precio = precio;
	}
	public Date getFecha_vig() {
		return fecha_vig;
	}
	public void setFecha_vig(Date fechaVig) {
		fecha_vig = fechaVig;
	}
	public String getControlado() {
		return controlado;
	}
	public void setControlado(String controlado) {
		this.controlado = controlado;
	}
	public String getImportado() {
		return importado;
	}
	public void setImportado(String importado) {
		this.importado = importado;
	}
	public String getTipo_venta() {
		return tipo_venta;
	}
	public void setTipo_venta(String tipoVenta) {
		tipo_venta = tipoVenta;
	}
	public String getIva() {
		return iva;
	}
	public void setIva(String iva) {
		this.iva = iva;
	}
	public String getCod_dto_pami() {
		return cod_dto_pami;
	}
	public void setCod_dto_pami(String codDtoPami) {
		cod_dto_pami = codDtoPami;
	}
	public int getCod_lab() {
		return cod_lab;
	}
	public void setCod_lab(int codLab) {
		cod_lab = codLab;
	}
	public int getNro_registro() {
		return nro_registro;
	}
	public void setNro_registro(int nroRegistro) {
		nro_registro = nroRegistro;
	}
	public String getBaja() {
		return baja;
	}
	public void setBaja(String baja) {
		this.baja = baja;
	}
	public String getCod_barra() {
		return cod_barra;
	}
	public void setCod_barra(String codBarra) {
		cod_barra = codBarra;
	}
	public int getUnidades() {
		return unidades;
	}
	public void setUnidades(int unidades) {
		this.unidades = unidades;
	}
	public String getTamanio() {
		return tamanio;
	}
	public void setTamanio(String tamanio) {
		this.tamanio = tamanio;
	}
	public String getHeladera() {
		return heladera;
	}
	public void setHeladera(String heladera) {
		this.heladera = heladera;
	}
	public String getSifar() {
		return sifar;
	}
	public void setSifar(String sifar) {
		this.sifar = sifar;
	}
	public String getBaja_especial() {
		return baja_especial;
	}
	public void setBaja_especial(String bajaEspecial) {
		baja_especial = bajaEspecial;
	}
	public String getBlanco() {
		return blanco;
	}
	public void setBlanco(String blanco) {
		this.blanco = blanco;
	}

	
	
}