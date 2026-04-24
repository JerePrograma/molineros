package ar.com.ospim.novedades.beans;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Novedad implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = -6963448643559872116L;
	
	private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	
	private Integer id;
	private Integer idProceso;
	private Integer codigo_ooss;
	private String cuit_empleador;
	private String cuil_titular;
	private Integer codigo_parentesco;
	private String cuil;
	private String documento_tipo;
	private Integer documento_numero;
	private String apellido_nombre;
	private String sexo;
	private Integer estado_civil;
	private Integer fecha_nacimiento;
	private Integer nacionalidad;
	private String calle;
	private String numero_puerta;
	private String piso;
	private String departamento;
	private String localidad;
	private String codigo_postal;
	private Integer provincia;
	private Integer tipo_domicilio;
	private String telefono;
	private Integer situacion_revista;
	private Integer incapacidad;
	private Integer tipo_beneficiario_titular;
	private Integer fecha_alta_en_ooss;
	private Integer fecha_cierre_presentacion;
	private String codigo_movimiento;
	private String detalle_novedad;
	private Date alta_fecha;
	private String alta_usr;
	private Date modi_fecha;
	private String modi_usr;
	private Date baja_fecha;
	private String baja_usr;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getCodigo_ooss() {
		return codigo_ooss;
	}
	public void setCodigo_ooss(Integer codigo_ooss) {
		this.codigo_ooss = codigo_ooss;
	}
	public String getCuit_empleador() {
		return cuit_empleador;
	}
	public void setCuit_empleador(String cuit_empleador) {
		this.cuit_empleador = cuit_empleador;
	}
	public String getCuil_titular() {
		return cuil_titular;
	}
	public void setCuil_titular(String cuil_titular) {
		this.cuil_titular = cuil_titular;
	}
	public Integer getCodigo_parentesco() {
		return codigo_parentesco;
	}
	public void setCodigo_parentesco(Integer codigo_parentesco) {
		this.codigo_parentesco = codigo_parentesco;
	}
	public String getCuil() {
		return cuil;
	}
	public void setCuil(String cuil) {
		this.cuil = cuil;
	}
	public String getDocumento_tipo() {
		return documento_tipo;
	}
	public void setDocumento_tipo(String documento_tipo) {
		this.documento_tipo = documento_tipo;
	}
	public Integer getDocumento_numero() {
		return documento_numero;
	}
	public void setDocumento_numero(Integer documento_numero) {
		this.documento_numero = documento_numero;
	}
	public String getApellido_nombre() {
		return apellido_nombre;
	}
	public void setApellido_nombre(String apellido_nombre) {
		this.apellido_nombre = apellido_nombre;
	}
	public String getSexo() {
		return sexo;
	}
	public void setSexo(String sexo) {
		this.sexo = sexo;
	}
	public Integer getEstado_civil() {
		return estado_civil;
	}
	public void setEstado_civil(Integer estado_civil) {
		this.estado_civil = estado_civil;
	}
	public Integer getFecha_nacimiento() {
		return fecha_nacimiento;
	}
	public String getFecha_nacimiento_Str() {
//		viene en formato numero ordenado como ddMMyyyy
		
		String fechaParseada = "";
		try{
			fechaParseada = String.valueOf(this.fecha_nacimiento);  // sdf.format(this.fecha_nacimiento);
			if(fechaParseada.length() == 7){ // falta el 0 delante de los dias que son menores a 10
				fechaParseada = "0" +fechaParseada; 
			}
			fechaParseada = fechaParseada.substring(0, 1)+"/"+fechaParseada.substring(2, 3)+"/"+fechaParseada.substring(4, 8);
		}catch (Exception e) {
			return "01/01/1800";
		}	
		return fechaParseada;
	}
	public void setFecha_nacimiento(Integer fecha_nacimiento) {
		this.fecha_nacimiento = fecha_nacimiento;
	}
	public Integer getNacionalidad() {
		return nacionalidad;
	}
	public void setNacionalidad(Integer nacionalidad) {
		this.nacionalidad = nacionalidad;
	}
	public String getCalle() {
		return calle;
	}
	public void setCalle(String calle) {
		this.calle = calle;
	}
	public String getNumero_puerta() {
		return numero_puerta;
	}
	public void setNumero_puerta(String numero_puerta) {
		this.numero_puerta = numero_puerta;
	}
	public String getPiso() {
		return piso;
	}
	public void setPiso(String piso) {
		this.piso = piso;
	}
	public String getDepartamento() {
		return departamento;
	}
	public void setDepartamento(String departamento) {
		this.departamento = departamento;
	}
	public String getLocalidad() {
		return localidad;
	}
	public void setLocalidad(String localidad) {
		this.localidad = localidad;
	}
	public String getCodigo_postal() {
		return codigo_postal;
	}
	public void setCodigo_postal(String codigo_postal) {
		this.codigo_postal = codigo_postal;
	}
	public Integer getProvincia() {
		return provincia;
	}
	public void setProvincia(Integer provincia) {
		this.provincia = provincia;
	}
	public Integer getTipo_domicilio() {
		return tipo_domicilio;
	}
	public void setTipo_domicilio(Integer tipo_domicilio) {
		this.tipo_domicilio = tipo_domicilio;
	}
	public String getTelefono() {
		return telefono;
	}
	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}
	public Integer getSituacion_revista() {
		return situacion_revista;
	}
	public void setSituacion_revista(Integer situacion_revista) {
		this.situacion_revista = situacion_revista;
	}
	public Integer getIncapacidad() {
		return incapacidad;
	}
	public void setIncapacidad(Integer incapacidad) {
		this.incapacidad = incapacidad;
	}
	public Integer getTipo_beneficiario_titular() {
		return tipo_beneficiario_titular;
	}
	public void setTipo_beneficiario_titular(Integer tipo_beneficiario_titular) {
		this.tipo_beneficiario_titular = tipo_beneficiario_titular;
	}
	public String getFecha_alta_en_ooss_Str() {
//		viene en formato numero ordenado como ddMMyyyy
		
		String fechaParseada = "";
		try{
			fechaParseada = String.valueOf(this.fecha_alta_en_ooss); 
			fechaParseada = fechaParseada.substring(0, 1)+"/"+fechaParseada.substring(2, 3)+"/"+fechaParseada.substring(4, 8);
		}catch (Exception e) {
			return "01/01/1800";
		}	
		return fechaParseada;
	}
	public Integer getFecha_alta_en_ooss() {
		return fecha_alta_en_ooss;
	}
	public void setFecha_alta_en_ooss(Integer fecha_alta_en_ooss) {
		this.fecha_alta_en_ooss = fecha_alta_en_ooss;
	}
	public String getFecha_cierre_presentacion_Str() {
//		viene en formato numero ordenado como ddMMyyyy
		
		String fechaParseada = "";
		try{
			fechaParseada = String.valueOf(this.fecha_cierre_presentacion); 
			fechaParseada = fechaParseada.substring(0, 1)+"/"+fechaParseada.substring(2, 3)+"/"+fechaParseada.substring(4, 8);
		}catch (Exception e) {
			return "01/01/1800";
		}	
		return fechaParseada;
	}
	
	public Integer getFecha_cierre_presentacion() {
		return fecha_cierre_presentacion;
	}
	public void setFecha_cierre_presentacion(Integer fecha_cierre_presentacion) {
		this.fecha_cierre_presentacion = fecha_cierre_presentacion;
	}
	public String getCodigo_movimiento() {
		return codigo_movimiento;
	}
	public void setCodigo_movimiento(String codigo_movimiento) {
		this.codigo_movimiento = codigo_movimiento;
	}
	public Date getAlta_fecha() {
		return alta_fecha;
	}
	public void setAlta_fecha(Date alta_fecha) {
		this.alta_fecha = alta_fecha;
	}
	public String getAlta_usr() {
		return alta_usr;
	}
	public void setAlta_usr(String alta_usr) {
		this.alta_usr = alta_usr;
	}
	public Date getModi_fecha() {
		return modi_fecha;
	}
	public void setModi_fecha(Date modi_fecha) {
		this.modi_fecha = modi_fecha;
	}
	public String getModi_usr() {
		return modi_usr;
	}
	public void setModi_usr(String modi_usr) {
		this.modi_usr = modi_usr;
	}
	public Date getBaja_fecha() {
		return baja_fecha;
	}
	public void setBaja_fecha(Date baja_fecha) {
		this.baja_fecha = baja_fecha;
	}
	public String getBaja_usr() {
		return baja_usr;
	}
	public void setBaja_usr(String baja_usr) {
		this.baja_usr = baja_usr;
	}

	public Novedad() {
		super();
	}	
	
	public Novedad(String line) {
		super();
		String[] linea = line.split("\\|");

		this.codigo_ooss = linea[0] != null && linea[0].trim().length() > 0 ? Integer.parseInt(linea[0].trim()) : 0;
		this.cuit_empleador = linea[1].trim();
		this.cuil_titular = linea[2].trim();
		this.codigo_parentesco = linea[3] != null && linea[3].trim().length() > 0 ? Integer.parseInt(linea[3].trim()) : 0;
		this.cuil = linea[4].trim();
		this.documento_tipo = linea[5].trim();
		this.documento_numero = linea[6] != null && linea[6].trim().length() > 0 ? Integer.parseInt(linea[6].trim()) : 0;
		this.apellido_nombre = linea[7].trim();
		this.sexo = linea[8].trim();
		this.estado_civil = linea[9] != null && linea[9].trim().length() > 0 ? Integer.parseInt(linea[9].trim()) : 0;
		this.fecha_nacimiento = linea[10] != null && linea[10].trim().length() > 0 ? Integer.parseInt(linea[10].trim()) : 0;
		this.nacionalidad = linea[11] != null && linea[11].trim().length() > 0 ? Integer.parseInt(linea[11].trim()) : 0;
		this.calle = linea[12].trim();
		this.numero_puerta = linea[13].trim();
		this.piso = linea[14].trim();
		this.departamento = linea[15].trim();
		this.localidad = linea[16].trim();
		this.codigo_postal = linea[17].trim();
		this.provincia = linea[18] != null && linea[18].trim().length() > 0 ? Integer.parseInt(linea[18].trim()) : 0;
		this.tipo_domicilio = linea[19] != null && linea[19].trim().length() > 0 ? Integer.parseInt(linea[19].trim()) : 0;
		this.telefono = linea[20].trim();
		this.situacion_revista = linea[21] != null && linea[21].trim().length() > 0 ? Integer.parseInt(linea[21].trim()) : 0;
		this.incapacidad = linea[22] != null && linea[22].trim().length() > 0 ? Integer.parseInt(linea[22].trim()) : 0;
		this.tipo_beneficiario_titular = linea[23] != null && linea[23].trim().length() > 0 ? Integer.parseInt(linea[23].trim()) : 0;
		this.fecha_alta_en_ooss = linea[24] != null && linea[24].trim().length() > 0 ? Integer.parseInt(linea[24].trim()) : 0;
		this.fecha_cierre_presentacion = linea[25] != null && linea[25].trim().length() > 0 ? Integer.parseInt(linea[25].trim()) : 0;
		this.codigo_movimiento = linea[26].trim();
		this.detalle_novedad = linea[27].trim();
		
	}
	
	public static Novedad getMapping(String prefix, ResultSet rs) throws SQLException{
		Novedad nov = new Novedad();
		
		nov.setId(rs.getInt(prefix + "id"));
		nov.setIdProceso(rs.getInt(prefix + "id_proceso"));
		nov.setCodigo_ooss(rs.getInt(prefix + "codigo_ooss"));
		nov.setCuit_empleador(rs.getString(prefix + "cuit_empleador"));
		nov.setCuil_titular(rs.getString(prefix + "cuil_titular"));
		nov.setCodigo_parentesco(rs.getInt(prefix + "codigo_parentesco"));
		nov.setCuil(rs.getString(prefix + "cuil"));
		nov.setDocumento_tipo(rs.getString(prefix + "documento_tipo"));
		nov.setDocumento_numero(rs.getInt(prefix + "documento_numero"));
		nov.setApellido_nombre(rs.getString(prefix + "apellido_nombre"));
		nov.setSexo(rs.getString(prefix + "sexo"));
		nov.setEstado_civil(rs.getInt(prefix + "estado_civil"));
		nov.setFecha_nacimiento(rs.getInt(prefix + "fecha_nacimiento"));
		nov.setNacionalidad(rs.getInt(prefix + "nacionalidad"));
		nov.setCalle(rs.getString(prefix + "calle"));
		nov.setNumero_puerta(rs.getString(prefix + "numero_puerta"));
		nov.setPiso(rs.getString(prefix + "piso"));
		nov.setDepartamento(rs.getString(prefix + "departamento"));
		nov.setLocalidad(rs.getString(prefix + "localidad"));
		nov.setCodigo_postal(rs.getString(prefix + "codigo_postal"));
		nov.setProvincia(rs.getInt(prefix + "provincia"));
		nov.setTipo_domicilio(rs.getInt(prefix + "tipo_domicilio"));
		nov.setTelefono(rs.getString(prefix + "telefono"));
		nov.setSituacion_revista(rs.getInt(prefix + "situacion_revista"));
		nov.setIncapacidad(rs.getInt(prefix + "incapacidad"));
		nov.setTipo_beneficiario_titular(rs.getInt(prefix + "tipo_beneficiario_titular"));
		nov.setFecha_alta_en_ooss(rs.getInt(prefix + "fecha_alta_en_ooss"));
		nov.setFecha_cierre_presentacion(rs.getInt(prefix + "fecha_cierre_presentacion"));
		nov.setCodigo_movimiento(rs.getString(prefix + "codigo_movimiento"));
		nov.setDetalle_novedad(rs.getString(prefix + "detalle_novedad"));
		nov.setAlta_fecha(rs.getDate(prefix + "alta_fecha"));
		nov.setAlta_usr(rs.getString(prefix + "alta_usr"));
		nov.setModi_fecha(rs.getDate(prefix + "modi_fecha"));
		nov.setModi_usr(rs.getString(prefix + "modi_usr"));
		nov.setBaja_fecha(rs.getDate(prefix + "baja_fecha"));
		nov.setBaja_usr(rs.getString(prefix + "baja_usr"));

		return nov;
	}
	public Integer getIdProceso() {
		return idProceso;
	}
	public void setIdProceso(Integer idProceso) {
		this.idProceso = idProceso;
	}
	public String getDetalle_novedad() {
		return detalle_novedad;
	}
	public void setDetalle_novedad(String detalle_novedad) {
		this.detalle_novedad = detalle_novedad;
	}
}

