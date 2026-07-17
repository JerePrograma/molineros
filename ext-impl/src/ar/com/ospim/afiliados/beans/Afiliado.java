package ar.com.ospim.afiliados.beans;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ar.com.ospim.afiliados.WebKeysAfiliados;
import ar.com.ospim.global.beans.Domicilio;
import ar.com.ospim.global.beans.Empresa;
import ar.com.ospim.global.beans.Localidad;
import ar.com.ospim.global.beans.Plan;
import ar.com.ospim.global.beans.Provincia;
import ar.com.ospim.global.beans.RamoEmpresa;
import ar.com.ospim.global.beans.Seccional;
import ar.com.ospim.procesaArchivos.beans.opcionesss.DetalleOpcionesSS;
import ar.com.ospim.util.DateUtils;
import ar.com.ospim.util.StringUtils;
import ar.com.ospim.webservice.beans.AfiliacionPrevencion;
import ar.com.uoma.beans.Incidente;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.Validator;

/**
 * @author carlos rivas
 * @version 1.0
 * @created 14-Jul-2010 12:25:06 p.m.
 * @edited 16-May 2013 SVA
 */

public class Afiliado implements Comparable<Afiliado> , Serializable {
	
	private static final long serialVersionUID = -383346659942380672L;

	private static Log _log = LogFactoryUtil.getLog(Afiliado.class);

	private String cuil_titular;
	private int inte;
	private int id_ospim;
	private Date id_ospim_baja_fecha;
	private Date id_uoma_baja_fecha;
	private Date id_amtima_baja_fecha;
	private int id_uoma;
	private int id_amtima;
	private String apellido;
	private String nombre;
	private String documento_tipo;
	private String docu_numero;
	private String sexo;
	private String cuil;
	private Date naci_fecha;
	private String civil_esta;
	private int id_civil_esta;
	private int nacionalidad;
	private String nacionalidad_string;
	private String parentesco;
	private int id_parentesco;
	private Date ingre_fecha;
	private Seccional seccional;
	private int anterior_os;
	private Date vigen_fecha;
	private String observaciones;
	private List<AfiObservacion> observacionesInternas = new ArrayList<AfiObservacion>();
	private Date alta_fecha;
	private String alta_usr;
	private Date modi_fecha;
	private String modi_usr;
	private Date baja_fecha;
	private String baja_usr;
	private String discapacitado;
	private boolean  conReclamoPrestacional;
	private int aportante_titular;
	private Domicilio[] domicilios;
	private Plan ultimo_plan;
	private int id_motivo_baja;
	private int tiene_imagen;
	private int folderid;
	private String title;
	private List<SituacionLaboral> lista_situ_laboral;
	private AfiAporteList afi_aporte_list;
	private String cuit;
	private String razonSoc;	
	private String id_tercerizadora;
	private String tipoOperacion;
	private String desc_tercerizadora;
	private BigDecimal valorCapita;
	private List<AfiDocumentacion> documentacion;
	private int id_categoria;
	private Date FPP;
	private int censo2013;
	private String email;
	private AfiPlan afiPlan;
	private int idCorrespondencia;
	private int tieneAntecedentesJudiciales;
	private int clientePreferencial;
	private long nroCredencial;
	private AfiliacionPrevencion prevencion;
	private String proyecto;
	
	private DetalleOpcionesSS detalleOpcionSss= null;
	private DetalleFechasSuper detalleFechasSss = null;
	
	private List<Incidente> incidentes;
	
	private List<AfiSuspencionCobertura> suspencionCobertura;
	private Integer edad;
	
	public Afiliado() {
	}
			
	public Afiliado(String cuil_titular) {
		this.cuil_titular = cuil_titular;
	}

	public Afiliado(String cuil_titular, int inte) {
		this.cuil_titular = cuil_titular;
		this.inte = inte;
	}

	public Afiliado(String cuil_titular, int inte, String nombre,
			String apellido) {
		this.cuil_titular = cuil_titular;
		this.inte = inte;
		this.nombre = nombre;
		this.apellido = apellido;
	}

	public Afiliado(String cuil_titular, int inte, String cuil, String nombre,
			String apellido) {
		this.cuil_titular = cuil_titular;
		this.inte = inte;
		this.cuil = cuil;
		this.nombre = nombre;
		this.apellido = apellido;
	}

	public Afiliado(String cuil_titular, int inte, String cuil) {
		this.cuil_titular = cuil_titular;
		this.inte = inte;
		this.cuil = cuil;
	}
	
	public Afiliado(String seccional , String cuil_titular,  int inte , String nombre , String apellido ) {
		
		try {
			this.cuil_titular = cuil_titular;
			this.inte = inte;
			this.nombre = nombre;
			this.apellido = apellido;
			Seccional secc = new Seccional();
			secc.setDescripcion(seccional);
			this.setSeccional(secc);
		} catch (Exception e) {
			_log.error(e);
		}
	}
	

	public Afiliado(String cuil_titular, int inte, String nombre,
			String apellido, String tdoc, String documento, String seccional,
			Date ingreso, Date baja) {
		try {
			this.cuil_titular = cuil_titular;
			this.inte = inte;
			this.nombre = nombre;
			this.apellido = apellido;
			this.documento_tipo = tdoc;
			this.docu_numero = documento;
			Seccional secc = new Seccional();
			secc.setDescripcion(seccional);
			this.setSeccional(secc);
			this.ingre_fecha = ingreso;
			this.baja_fecha = baja;
		} catch (Exception e) {
			_log.error(e);
		}
	}

//	public Afiliado(String cuil_titular, int inte, String parentesco,
//			String nombre, String apellido, String tdoc, String documento,
//			String seccional, Date ingreso, Date baja) {
	public Afiliado(String cuil_titular, int inte, int id_parentesco_sss, String parentescoDetalle,
			String nombre, String apellido, String tdoc, String documento,
			String seccional, Date ingreso, Date baja) {	
		try {
			this.cuil_titular = cuil_titular;
			this.inte = inte;
			this.id_parentesco = id_parentesco_sss;
			this.parentesco = parentescoDetalle;
			this.nombre = nombre;
			this.apellido = apellido;
			this.documento_tipo = tdoc;
			this.docu_numero = documento;
			Seccional secc = new Seccional();
			secc.setDescripcion(seccional);
			this.setSeccional(secc);
			this.ingre_fecha = ingreso;
			this.baja_fecha = baja;
		} catch (Exception e) {
			_log.error(e);
		}
	}

	public Afiliado(String cuil_titular, int inte, String parentesco,
			String nombre, String apellido, String tdoc, String documento,
			int id_seccional, String seccional, Date ingreso, Date baja) {
		try {
			this.cuil_titular = cuil_titular;
			this.inte = inte;
			this.parentesco = parentesco;
			this.nombre = nombre;
			this.apellido = apellido;
			this.documento_tipo = tdoc;
			this.docu_numero = documento;
			Seccional secc = new Seccional(id_seccional, seccional);
			this.setSeccional(secc);
			this.ingre_fecha = ingreso;
			this.baja_fecha = baja;
		} catch (Exception e) {
			_log.error(e);
		}
	}

	public Afiliado(String cuil_titular, int inte, String parentesco,
			String nombre, String apellido, String tdoc, String documento,
			int id_seccional, String seccional, Date ingreso, Date baja,
			int id_ospim, int id_amtima, int id_uoma) {
		try {
			this.cuil_titular = cuil_titular;
			this.inte = inte;
			this.parentesco = parentesco;
			this.nombre = nombre;
			this.apellido = apellido;
			this.documento_tipo = tdoc;
			this.docu_numero = documento;
			Seccional secc = new Seccional(id_seccional, seccional);
			this.setSeccional(secc);
			this.ingre_fecha = ingreso;
			this.baja_fecha = baja;
			this.id_uoma = id_uoma;
			this.id_amtima = id_amtima;
			this.id_ospim = id_ospim;

		} catch (Exception e) {
			_log.error(e);
		}
	}
	
	public Afiliado(String cuil_titular, int inte, String parentesco,
			String nombre, String apellido, String tdoc, String documento,
			int id_seccional, String seccional, Date ingreso, Date baja,
			int id_ospim, int id_amtima, int id_uoma, String plan) {
		try {
			this.cuil_titular = cuil_titular;
			this.inte = inte;
			this.parentesco = parentesco;
			this.nombre = nombre;
			this.apellido = apellido;
			this.documento_tipo = tdoc;
			this.docu_numero = documento;
			Seccional secc = new Seccional(id_seccional, seccional);
			this.setSeccional(secc);
			this.ingre_fecha = ingreso;
			this.baja_fecha = baja;
			this.id_uoma = id_uoma;
			this.id_amtima = id_amtima;
			this.id_ospim = id_ospim;
			this.ultimo_plan= new Plan(plan);
		} catch (Exception e) {
			_log.error(e);
		}
	}

	public Afiliado(String cuil_titular, int inte, int id_parentesco_sss, String parentesco,
			String nombre, String apellido, String tdoc, String documento,
			int id_seccional, String seccional, Date ingreso, Date baja,
			int id_ospim, int id_amtima, int id_uoma, int id_plan,
			String nombre_plan, Date alta_fecha, String discapacitado, 
			String idTercerizadora, String tercerizadora) {
		try {
			this.cuil_titular = cuil_titular;
			this.inte = inte;
			this.id_parentesco = id_parentesco_sss;
			this.parentesco = parentesco;
			this.nombre = nombre;
			this.apellido = apellido;
			this.documento_tipo = tdoc;
			this.docu_numero = documento;
			Seccional secc = new Seccional(id_seccional, seccional);
			this.setSeccional(secc);
			this.ingre_fecha = ingreso;
			this.baja_fecha = baja;
			this.id_uoma = id_uoma;
			this.id_amtima = id_amtima;
			this.id_ospim = id_ospim;
			Plan plan = new Plan(id_plan, nombre_plan);
			this.setUltimo_plan(plan);
			this.alta_fecha = alta_fecha;
			this.discapacitado = discapacitado;
			this.id_tercerizadora = idTercerizadora;
			this.desc_tercerizadora = tercerizadora;
		} catch (Exception e) {
			_log.error(e);
		}
	}

	public Afiliado(String cuil_titular, int inte, int id_parentesco_sss, String parentesco,
			String nombre, String apellido, String tdoc, String documento,
			int id_seccional, String seccional, Date ingreso, Date baja,
			int id_ospim, int id_amtima, int id_uoma, int id_plan,
			String nombre_plan, Date alta_fecha, String discapacitado, 
			String id_tercerizadora, String tercerizadora, int nroSocioPrev , BigDecimal nroCredenPrev, Incidente incidente) {
		try {
			this.cuil_titular = cuil_titular;
			this.inte = inte;
			this.id_parentesco = id_parentesco_sss;
			this.parentesco = parentesco;
			this.nombre = nombre;
			this.apellido = apellido;
			this.documento_tipo = tdoc;
			this.docu_numero = documento;
			Seccional secc = new Seccional(id_seccional, seccional);
			this.setSeccional(secc);
			this.ingre_fecha = ingreso;
			this.baja_fecha = baja;
			this.id_uoma = id_uoma;
			this.id_amtima = id_amtima;
			this.id_ospim = id_ospim;
			Plan plan = new Plan(id_plan, nombre_plan);
			this.setUltimo_plan(plan);
			this.alta_fecha = alta_fecha;
			this.discapacitado = discapacitado;
			this.id_tercerizadora = id_tercerizadora;
			this.desc_tercerizadora = tercerizadora;
			AfiliacionPrevencion afi = new AfiliacionPrevencion();
			afi.setNroSocio(nroSocioPrev);
			afi.setNroCredencial(nroCredenPrev);
			this.setPrevencion(afi);
			if (incidente != null) {
				this.addIncidente(incidente);
			}
			
			
		} catch (Exception e) {
			_log.error(e);
		}
	}
	
	
//	public Afiliado(String cuil_titular, int inte, int id_parentesco_sss, String parentesco,
//			String nombre, String apellido, String tdoc, String documento,
//			int id_seccional, String seccional, Date ingreso, Date baja,
//			int id_ospim, int id_amtima, int id_uoma, int id_plan,
//			String nombre_plan, Date alta_fecha, String discapacitado) {
//		try {
//			this.cuil_titular = cuil_titular;
//			this.inte = inte;
//			this.id_parentesco = id_parentesco_sss;
//			this.parentesco = parentesco;
//			this.nombre = nombre;
//			this.apellido = apellido;
//			this.documento_tipo = tdoc;
//			this.docu_numero = documento;
//			Seccional secc = new Seccional(id_seccional, seccional);
//			this.setSeccional(secc);
//			this.ingre_fecha = ingreso;
//			this.baja_fecha = baja;
//			this.id_uoma = id_uoma;
//			this.id_amtima = id_amtima;
//			this.id_ospim = id_ospim;
//			Plan plan = new Plan(id_plan, nombre_plan);
//			this.setUltimo_plan(plan);
//			this.alta_fecha = alta_fecha;
//			this.discapacitado = discapacitado;			
//		} catch (Exception e) {
//			_log.error(e);
//		}
//	}

	public Afiliado(String cuilTitular, int inte, int idOspim,
			Date idOspimBajaFecha, Date idUomaBajaFecha,
			Date idAmtimaBajaFecha, int idUoma, int idAmtima, String apellido,
			String nombre, String documentoTipo, String docuNumero,
			String sexo, String cuil, Date naciFecha, int civilEsta,
			int nacionalidad, int parentesco, Seccional seccional,
			int anteriorOs, Date vigenFecha, String observaciones,
			String discapacitado, String censo2013, Domicilio[] domicilios, 
			String email, int numeroCorrespondencia, String tieneAntecJudiciales, 
			String clientePreferencial, String proyecto) {
		
		super();
		cuil_titular = cuilTitular;
		this.inte = inte;
		id_ospim = idOspim;
		id_ospim_baja_fecha = idOspimBajaFecha;
		id_uoma_baja_fecha = idUomaBajaFecha;
		id_amtima_baja_fecha = idAmtimaBajaFecha;
		id_uoma = idUoma;
		id_amtima = idAmtima;
		this.apellido = apellido;
		this.nombre = nombre;
		documento_tipo = documentoTipo;
		docu_numero = docuNumero;
		this.sexo = sexo;
		this.cuil = cuil;
		naci_fecha = naciFecha;
//		civil_esta = civilEsta;
		id_civil_esta = civilEsta;
		this.nacionalidad = nacionalidad;
//		this.parentesco = parentesco;
		id_parentesco = parentesco;
		this.seccional = seccional;
		anterior_os = anteriorOs;
		vigen_fecha = vigenFecha;
		this.observaciones = observaciones;
		this.discapacitado = discapacitado;
		this.censo2013=Integer.parseInt(censo2013);
		this.domicilios = domicilios;
		this.email = email;
		this.idCorrespondencia = numeroCorrespondencia;
		this.tieneAntecedentesJudiciales =Integer.parseInt(tieneAntecJudiciales);
		this.clientePreferencial=Integer.parseInt(clientePreferencial);
		this.proyecto = proyecto;
	}
	
	public Afiliado(String cuilTitular, int inte, int idOspim,
				Date idOspimBajaFecha, Date idUomaBajaFecha,
				Date idAmtimaBajaFecha, int idUoma, int idAmtima, String apellido,
				String nombre, String documentoTipo, String docuNumero,
				String sexo, String cuil, Date naciFecha, int civilEsta,
				int nacionalidad, int parentesco, Seccional seccional,
				int anteriorOs, Date vigenFecha, String observaciones,
				String discapacitado, Domicilio[] domicilios, String cuit, 
				String razonSoc, String email, int numeroCorrespondencia,
				String proyecto) {	
		super();
		cuil_titular = cuilTitular;
		this.inte = inte;
		id_ospim = idOspim;
		id_ospim_baja_fecha = idOspimBajaFecha;
		id_uoma_baja_fecha = idUomaBajaFecha;
		id_amtima_baja_fecha = idAmtimaBajaFecha;
		id_uoma = idUoma;
		id_amtima = idAmtima;
		this.apellido = apellido;
		this.nombre = nombre;
		documento_tipo = documentoTipo;
		docu_numero = docuNumero;
		this.sexo = sexo;
		this.cuil = cuil;
		naci_fecha = naciFecha;
//		civil_esta = civilEsta;
		id_civil_esta = civilEsta;
		this.nacionalidad = nacionalidad;
//		this.parentesco = parentesco;
		id_parentesco = parentesco;
		this.seccional = seccional;
		anterior_os = anteriorOs;
		vigen_fecha = vigenFecha;
		this.observaciones = observaciones;
		this.discapacitado = discapacitado;
		this.domicilios = domicilios;
		this.cuit=cuit;
		this.razonSoc=razonSoc;
		this.email = email;
		this.idCorrespondencia = numeroCorrespondencia;
		this.proyecto=proyecto;
	}
	
	public static Afiliado getMappingVigentesTercerizadora(ResultSet rs) throws SQLException{
		Afiliado afiliado = new Afiliado();
		afiliado.setId_ospim(rs.getInt("id_ospim"));
		Seccional secc=new Seccional(1, rs.getString("seccional"));
		afiliado.setSeccional(secc);
		afiliado.setId_tercerizadora(rs.getString("id_tercerizadora"));
		afiliado.setCuil_titular(rs.getString("cuil_titular"));
		afiliado.setCuil(rs.getString("cuil"));
		afiliado.setInte(rs.getInt("inte"));
		afiliado.setId_parentesco(rs.getInt("id_parentesco_sss"));
		afiliado.setParentesco(rs.getString("parentesco"));
		afiliado.setApellido(rs.getString("apellido"));
		afiliado.setNombre(rs.getString("nombre"));
		afiliado.setDocumento_tipo(rs.getString("documento_tipo"));
		afiliado.setDocu_numero(rs.getString("docu_numero"));
		afiliado.setNaci_fecha(rs.getDate("naci_fecha"));
		afiliado.setSexo(rs.getString("sexo"));
		afiliado.setId_civil_esta(rs.getInt("id_estado_civil_sss"));
		afiliado.setCivil_esta(rs.getString("civil_esta"));
		afiliado.setNacionalidad_string(rs.getString("nacionalidad"));
		Provincia prov= new Provincia(1,rs.getString("provincia"));
		Localidad loca= new Localidad(1,rs.getString("localidad"));
		Domicilio domicilio=new Domicilio();
		domicilio.setProvincia(prov);
		domicilio.setLocalidad(loca);
		domicilio.setPostal_codi(rs.getString("postal_codi"));
		domicilio.setCalle(rs.getString("calle"));
		domicilio.setNumero(rs.getString("numero"));
		domicilio.setPiso(rs.getString("piso"));
		domicilio.setDepto(rs.getString("depto"));
		domicilio.setTelefono(rs.getString("telefono") != null ? rs.getString("telefono") : "");
		domicilio.setCod_area_celular("");// datos pendientes de añadir a la funcion 		
		domicilio.setCod_area_tel_laboral("");
		domicilio.setTel_laboral("");
		domicilio.setCod_area_telefono("");		
		afiliado.setDomicilioDefault(domicilio);
		SituacionLaboral situLaboral=new SituacionLaboral();
		situLaboral.setCategoria(rs.getString("categoria"));
		List<SituacionLaboral> listaSL=new ArrayList<SituacionLaboral>();
		Empresa empresa=new Empresa(rs.getString("cuit"),"000",rs.getString("razon_soc"));
		RamoEmpresa ramoEmpresa=new RamoEmpresa(rs.getInt("ramo"));		
		empresa.setRamoEmpresa(ramoEmpresa);
		situLaboral.setEmpresa(empresa);
		listaSL.add(situLaboral);
		afiliado.setLista_situ_laboral(listaSL);
		Plan plan= new Plan(rs.getInt("id_plan"),rs.getString("plan"));
		plan.setDescripcionPrevencion(rs.getString("plan_prevencion"));
		plan.setFarmaciaPrevencion(rs.getString("plan_farmacia"));
		plan.setAmtima(rs.getBoolean("farmacia_amtima"));
		plan.setUoma(rs.getBoolean("farmacia_uoma"));
		afiliado.setUltimo_plan(plan);
		afiliado.setIngre_fecha(rs.getDate("ingre_fecha"));
		afiliado.setBaja_fecha(rs.getDate("baja_fecha"));
		afiliado.setId_uoma(rs.getInt("id_uoma"));
		afiliado.setVigen_fecha(rs.getDate("fecha_ospim"));
		afiliado.setAnterior_os(rs.getInt("os_anterior"));
		afiliado.setDiscapacitado(rs.getString("discapacidad"));
		afiliado.setTipoOperacion(rs.getString("tipo_operacion"));
		afiliado.setValorCapita(rs.getBigDecimal("valor_capita"));
//		MotivoBaja mb = new MotivoBaja(rs.getInt("id_motivo_baja"), rs.getString("motivo_baja"));
		afiliado.setId_motivo_baja(rs.getInt("id_motivo_baja"));
		afiliado.setTitle(rs.getString("motivo_baja"));   //chanchada pero si pongo setMotivoBaja(mb) habria que cambiar otros lugares tamb para que quede lindo-
		afiliado.setClientePreferencial(rs.getInt("cliente_preferencial"));
		return afiliado;
		
	}

	/**
	 * @return the cuil_titular
	 */
	public String getCuil_titular() {
		return cuil_titular;
	}

	public String getCuil_titularMasked() {
		return StringUtils.getCuilMask(cuil_titular);
	}

	/**
	 * @param cuilTitular
	 *            the cuil_titular to set
	 */
	public void setCuil_titular(String cuilTitular) {
		cuil_titular = cuilTitular;
	}

	/**
	 * @return the inte
	 */
	public int getInte() {
		return inte;
	}

	public String getInteAsString() {
		return String.valueOf(inte);
	}

	/**
	 * @param inte
	 *            the inte to set
	 */
	public void setInte(int inte) {
		this.inte = inte;
	}

	/**
	 * @return the id_ospim
	 */
	public int getId_ospim() {
		return id_ospim;
	}

	/**
	 * @param idOspim
	 *            the id_ospim to set
	 */
	public void setId_ospim(int idOspim) {
		id_ospim = idOspim;
	}

	/**
	 * @return the apellido
	 */
	public String getApellido() {
		return apellido;
	}

	/**
	 * @param apellido
	 *            the apellido to set
	 */
	public void setApellido(String apellido) {
		this.apellido = apellido;
	}

	/**
	 * @return the nombre
	 */
	public String getNombre() {
		return nombre;
	}

	/**
	 * @param nombre
	 *            the nombre to set
	 */
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	/**
	 * @return the documento_tipo
	 */
	public String getDocumento_tipo() {
		return documento_tipo;
	}

	/**
	 * @param documentoTipo
	 *            the documento_tipo to set
	 */
	public void setDocumento_tipo(String documentoTipo) {
		documento_tipo = documentoTipo;
	}

	/**
	 * @return the docu_numero
	 */
	public String getDocu_numero() {
		return docu_numero;
	}

	/**
	 * @param docuNumero
	 *            the docu_numero to set
	 */
	public void setDocu_numero(String docuNumero) {
		docu_numero = docuNumero;
	}

	/**
	 * @return the sexo
	 */
	public String getSexo() {
		return sexo;
	}

	/**
	 * @param sexo
	 *            the sexo to set
	 */
	public void setSexo(String sexo) {
		this.sexo = sexo;
	}

	/**
	 * @return the cuil
	 */
	public String getCuil() {
		return cuil;
	}

	/**
	 * @param cuil
	 *            the cuil to set
	 */
	public void setCuil(String cuil) {
		this.cuil = cuil;
	}

	/**
	 * @return the naci_fecha
	 */
	public Date getNaci_fecha() {
		return naci_fecha;
	}

	public String getNaci_fechaAsString() {
		return null != naci_fecha ? DateUtils.format(naci_fecha,
				DateUtils.SHORT) : "";
	}

	/**
	 * @param naciFecha
	 *            the naci_fecha to set
	 */
	public void setNaci_fecha(Date naciFecha) {
		naci_fecha = naciFecha;
	}

	/**
	 * @return the civil_esta
	 */
	public String getCivil_esta() {
		return civil_esta;
	}

	/**
	 * @param civilEsta
	 *            the civil_esta to set
	 */
	public void setCivil_esta(String civilEsta) {
		civil_esta = civilEsta;
	}

	/**
	 * @return the nacionalidad
	 */
	public int getNacionalidad() {
		return nacionalidad;
	}

	/**
	 * @param nacionalidad
	 *            the nacionalidad to set
	 */
	public void setNacionalidad(int nacionalidad) {
		this.nacionalidad = nacionalidad;
	}

	/**
	 * @return the parentesco
	 */
	public String getParentesco() {
		return parentesco;
	}

	/**
	 * @param parentesco
	 *            the parentesco to set
	 */
	public void setParentesco(String parentesco) {
		this.parentesco = parentesco;
	}

	/**
	 * @return the ingre_fecha
	 */
	public Date getIngre_fecha() {
		return ingre_fecha;
	}

	public String getIngre_fechaAsString() {
		return null != ingre_fecha ? DateUtils.format(ingre_fecha,
				DateUtils.SHORT) : "";
	}

	/**
	 * @param ingreFecha
	 *            the ingre_fecha to set
	 */
	public void setIngre_fecha(Date ingreFecha) {
		ingre_fecha = ingreFecha;
	}

	/**
	 * @return the seccional
	 */
	public Seccional getSeccional() {
		return seccional = seccional == null ? new Seccional() : seccional;
	}

	/**
	 * @param seccional
	 *            the seccional to set
	 */
	public void setSeccional(Seccional seccional) {
		this.seccional = seccional;
	}

	/**
	 * @return the anterior_os
	 */
	public int getAnterior_os() {
		return anterior_os;
	}

	/**
	 * @param anteriorOs
	 *            the anterior_os to set
	 */
	public void setAnterior_os(int anteriorOs) {
		anterior_os = anteriorOs;
	}

	/**
	 * @return the vigen_fecha
	 */
	public Date getVigen_fecha() {
		return vigen_fecha;
	}

	public String getVigen_fechaAsString() {
		return null != vigen_fecha ? DateUtils.format(vigen_fecha,
				DateUtils.SHORT) : "";
	}

	/**
	 * @param vigenFecha
	 *            the vigen_fecha to set
	 */
	public void setVigen_fecha(Date vigenFecha) {
		vigen_fecha = vigenFecha;
	}

	/**
	 * @return the observaciones
	 */
	public String getObservaciones() {
		return observaciones;
	}

	/**
	 * @param observaciones
	 *            the observaciones to set
	 */
	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}
	
	public List<AfiObservacion> getObservacionesInternas() {
		return observacionesInternas;
	}

	public void setObservacionesInternas(List<AfiObservacion> observacionesInternas) {
		this.observacionesInternas = observacionesInternas;
	}

	/**
	 * @return the alta_fecha
	 */
	public Date getAlta_fecha() {
		return alta_fecha;
	}

	/**
	 * @return the alta_fecha
	 */
	public String getAlta_fechaAsString() {
		return null != alta_fecha ? DateUtils.format(alta_fecha,
				DateUtils.SHORT) : "";
	}

	/**
	 * @param altaFecha
	 *            the alta_fecha to set
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
	 * @param altaUsr
	 *            the alta_usr to set
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
	 * @param modiFecha
	 *            the modi_fecha to set
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
	 * @param modiUsr
	 *            the modi_usr to set
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

	public String getBaja_fechaAsString() {
		return null != baja_fecha ? DateUtils.format(baja_fecha,
				DateUtils.SHORT) : "";
	}

	/**
	 * @param bajaFecha
	 *            the baja_fecha to set
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
	 * @param bajaUsr
	 *            the baja_usr to set
	 */
	public void setBaja_usr(String bajaUsr) {
		baja_usr = bajaUsr;
	}

	/**
	 * @return the discapacitado
	 */
	public String getDiscapacitado() {
		return discapacitado;
	}

	/**
	 * @param discapacitado
	 *            the discapacitado to set
	 */
	public void setDiscapacitado(String discapacitado) {
		this.discapacitado = discapacitado;
	}

	
	/**
	 * 
	 */
	public  boolean getConReclamoPrestacional() {
		return  conReclamoPrestacional;
	}

	/**
	 * 
	 *  
	 */
	public void setConReclamoPrestacional(boolean conReclamoPrestacional) {
		this.conReclamoPrestacional = conReclamoPrestacional;
	}
	
	
	/**
	 * @return the domicilios
	 */
	public Domicilio[] getDomicilios() {
		return domicilios;
	}

	/**
	 * @param domicilios
	 *            the domicilios to set
	 */
	public void setDomicilios(Domicilio[] domicilios) {
		this.domicilios = domicilios;
	}

	/**
	 * retorna el primer domicilio del afiliado, es el dom vigente
	 */
	public Domicilio getDomicilioDefault() {
		return domicilios[0] = domicilios[0] == null ? new Domicilio()
				: domicilios[0];
	}

	/**
	 * setea el primer domicilio del afiliado domicilio[0]
	 */
	public Domicilio setDomicilioDefault(Domicilio domicilio) {
		domicilios = new Domicilio[1];
		return domicilios[0] = domicilio;
	}

	/**
	 * @return the id_uoma
	 */
	public int getId_uoma() {
		return id_uoma;
	}

	/**
	 * @param idUoma
	 *            the id_uoma to set
	 */
	public void setId_uoma(int idUoma) {
		id_uoma = idUoma;
	}

	/**
	 * @return the id_amtima
	 */
	public int getId_amtima() {
		return id_amtima;
	}

	/**
	 * @param idamtima
	 *            the id_amtima to set
	 */
	public void setId_amtima(int idAmtima) {
		id_amtima = idAmtima;
	}

	/**
	 * @return the aportante_titular
	 */
	public int getAportante_titular() {
		return aportante_titular;
	}

	/**
	 * @param aportanteTitular
	 *            the aportante_titular to set
	 */
	public void setAportante_titular(int aportanteTitular) {
		aportante_titular = aportanteTitular;
	}

	public boolean esTitular() {
		return inte == 0;
	}

	public boolean esHijoMenor21() {
		return (getId_parentesco() == WebKeysAfiliados.HIJO_MENOR 
				|| getId_parentesco() == WebKeysAfiliados.HIJO_MENOR_CONYUGE
				|| getId_parentesco() == WebKeysAfiliados.MENOR_BAJO_GUARDA);
	}

	public String getApeNombre() {
		return getApellido() != null ? getApellido() + ", " + getNombre() : "";
	}
	
	public String getApellidoNombre() {
		return getApellido() != null ? getApellido() + " " + getNombre() : "";
	}

	public Date getId_ospim_baja_fecha() {
		return id_ospim_baja_fecha;
	}
	public String getIdOspimBajaFechaAsString() {		
		return null != id_ospim_baja_fecha ? DateUtils.format(id_ospim_baja_fecha,
				DateUtils.SHORT) : "";
	}

	public void setId_ospim_baja_fecha(Date idOspimBajaFecha) {
		id_ospim_baja_fecha = idOspimBajaFecha;
	}

	public Date getId_uoma_baja_fecha() {
		return id_uoma_baja_fecha;
	}

	public void setId_uoma_baja_fecha(Date idUomaBajaFecha) {
		id_uoma_baja_fecha = idUomaBajaFecha;
	}
	
	public String getIdUomaBajaFechaAsString() {		
		return null != id_uoma_baja_fecha ? DateUtils.format(id_uoma_baja_fecha ,
				DateUtils.SHORT) : "";
	}

	public Date getId_amtima_baja_fecha() {
		return id_amtima_baja_fecha;
	}

	public void setId_amtima_baja_fecha(Date idAmtimaBajaFecha) {
		id_amtima_baja_fecha = idAmtimaBajaFecha;
	}
	
	public String getIdAmtimaBajaFechaAsString() {		
		return null != id_amtima_baja_fecha ? DateUtils.format(id_amtima_baja_fecha ,
				DateUtils.SHORT) : "";
	}

	public boolean esBaja() {
		return Validator.isNotNull(getBaja_fecha())
				&& getBaja_fecha().getTime() < System.currentTimeMillis();
	}

	public static Afiliado getMapping(ResultSet rs) throws SQLException {
		return getMapping(rs, "");
	}

	public static Afiliado getMapping(ResultSet rs, String prefix)
			throws SQLException {
		Afiliado afiliado = new Afiliado();
		afiliado.setCuil_titular(rs.getString(prefix + "cuil_titular"));
		afiliado.setInte(rs.getInt(prefix + "inte"));
		afiliado.setId_ospim(rs.getInt(prefix + "id_ospim"));
		afiliado.setId_uoma(rs.getInt(prefix + "id_uoma"));
		afiliado.setId_amtima(rs.getInt(prefix + "id_amtima"));
		afiliado.setApellido(rs.getString(prefix + "apellido"));
		afiliado.setNombre(rs.getString(prefix + "nombre"));
		afiliado.setDocumento_tipo(rs.getString(prefix + "documento_tipo"));
		afiliado.setSexo(rs.getString(prefix + "sexo"));
		afiliado.setCuil(rs.getString(prefix + "cuil"));
		afiliado.setNaci_fecha(rs.getDate(prefix + "naci_fecha"));
		afiliado.setId_civil_esta(rs.getInt(prefix + "id_estado_civil_sss"));
		afiliado.setCivil_esta(rs.getString(prefix + "civil_esta"));
		afiliado.setId_parentesco(rs.getInt(prefix + "id_parentesco_sss"));
		afiliado.setParentesco(rs.getString(prefix + "parentesco"));
		afiliado.setIngre_fecha(rs.getDate(prefix + "ingre_fecha"));
		afiliado.setAnterior_os(rs.getInt(prefix + "anterior_os"));
		afiliado.setVigen_fecha(rs.getDate(prefix + "vigen_fecha"));
		afiliado.setObservaciones(rs.getString(prefix + "observaciones"));
		afiliado.setAlta_usr(rs.getString(prefix + "alta_usr"));
		afiliado.setAlta_fecha(rs.getDate(prefix + "alta_fecha"));
		afiliado.setModi_usr(rs.getString(prefix + "modi_usr"));
		afiliado.setModi_fecha(rs.getDate(prefix + "modi_fecha"));
		afiliado.setBaja_fecha(rs.getDate(prefix + "baja_fecha"));
		afiliado.setBaja_usr(rs.getString(prefix + "baja_usr"));
		afiliado.setDiscapacitado(rs.getString(prefix + "discapacitado"));
		afiliado.setDocu_numero(rs.getString(prefix + "docu_numero") != null ? rs
						.getString(prefix + "docu_numero"): "");
		afiliado.setNacionalidad(rs.getInt(prefix + "nacionalidad"));
		afiliado.setAportante_titular(rs.getInt(prefix + "aportante_titular"));
		afiliado.setId_ospim_baja_fecha(rs.getDate(prefix + "id_ospim_baja_fecha"));
		afiliado.setId_uoma_baja_fecha(rs.getDate(prefix + "id_uoma_baja_fecha"));
		afiliado.setId_amtima_baja_fecha(rs.getDate(prefix + "id_amtima_baja_fecha"));
		try {
			afiliado.setPlanAfiliado(rs.getString(prefix + "plan"));
				
		} catch (Exception e) {
//			_log.error(e);
			}
			return afiliado;
		}
	
	public static Afiliado getMappingDatosBasicos(ResultSet rs, String prefix)
			throws SQLException {	
		Afiliado afiliado = new Afiliado();
		afiliado.setCuil_titular(rs.getString(prefix + "cuil_titular"));
		afiliado.setInte(rs.getInt(prefix + "inte"));
//		afiliado.setId_ospim(rs.getInt(prefix + "id_ospim"));
//		afiliado.setId_uoma(rs.getInt(prefix + "id_uoma"));
		afiliado.setId_amtima(rs.getInt(prefix + "id_amtima"));
		afiliado.setApellido(rs.getString(prefix + "apellido"));
		afiliado.setNombre(rs.getString(prefix + "nombre"));
		afiliado.setDocumento_tipo(rs.getString(prefix + "documento_tipo"));
		afiliado.setDocu_numero(rs.getString(prefix + "docu_numero") != null ? rs
						.getString(prefix + "docu_numero"): "");
		
		
		
		
		
		return afiliado;
	}

	/**
	 * @param ultimo_plan
	 *            the ultimo_plan to set
	 */
	public void setUltimo_plan(Plan ultimo_plan) {
		this.ultimo_plan = ultimo_plan;
	}

	/**
	 * @return the ultimo_plan
	 */
	public Plan getUltimo_plan() {
		return ultimo_plan;
	}

	public String getNombrePlan() {
		return this.ultimo_plan != null ? this.ultimo_plan.getDescripcion()
				: "";
	}

	@Override
	public int hashCode() { 
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((cuil_titular == null) ? 0 : cuil_titular.hashCode());
		result = prime * result + inte;
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
		Afiliado other = (Afiliado) obj;
		if (cuil_titular == null) {
			if (other.cuil_titular != null)
				return false;
		} else if (!cuil_titular.equals(other.cuil_titular))
			return false;
		if (inte != other.inte)
			return false;
		return true;
	}

	public void setId_motivo_baja(int id_motivo_baja) {
		this.id_motivo_baja = id_motivo_baja;
	}

	public int getId_motivo_baja() {
		return id_motivo_baja;
	}

	public int getTiene_imagen() {
		return tiene_imagen;
	}

	public void setTiene_imagen(int tieneImagen) {
		tiene_imagen = tieneImagen;
	}

	public int getFolderid() {
		return folderid;
	}

	public void setFolderid(int folderid) {
		this.folderid = folderid;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int compareTo(Afiliado o) {
		if (this.cuil_titular.equals(o.getCuil_titular()))
			return 0;
		else if ((this.cuil_titular).compareTo(o.getCuil_titular()) > 1)
			return 1;
		else
			return -1;
	}

	public List<SituacionLaboral> getLista_situ_laboral() {
		return lista_situ_laboral;
	}

	public void setLista_situ_laboral(List<SituacionLaboral> listaSituLaboral) {
		lista_situ_laboral = listaSituLaboral;
	}

	public AfiAporteList getAfi_aporte_list() {
		return afi_aporte_list;
	}

	public void setAfi_aporte_list(AfiAporteList afiAporteList) {
		afi_aporte_list = afiAporteList;
	}
	public String getCuitSituLaboral(int pos){
		return lista_situ_laboral!=null&&lista_situ_laboral.get(pos)!=null?lista_situ_laboral.get(pos).getEmpresa().getCuit():"";
	}
	public String getRazonSocSituLaboral(int pos){
		return lista_situ_laboral!=null&&lista_situ_laboral.get(pos)!=null?lista_situ_laboral.get(pos).getEmpresa().getRazon_soc():"";
	}
	
	public String getSucursalSituLaboral(int pos){
		return lista_situ_laboral!=null&&lista_situ_laboral.get(pos)!=null?lista_situ_laboral.get(pos).getEmpresa().getSucursal():"";
	}
	
	public int getIdCategoriaSituLaboral(int pos){
		return lista_situ_laboral!=null&&lista_situ_laboral.get(pos)!=null?lista_situ_laboral.get(pos).getId_categoria():0;
	}
	
	public String getCategoriaSituLaboral(int pos){
		return lista_situ_laboral!=null&&lista_situ_laboral.get(pos)!=null?lista_situ_laboral.get(pos).getCategoria():"";
	}

	public int getIdRevistaSituLaboral(int pos){
		return lista_situ_laboral!=null&&lista_situ_laboral.get(pos)!=null?lista_situ_laboral.get(pos).getId_revista():0;
	}
	
	public String getSituRevistaSituLaboral(int pos){
		return lista_situ_laboral!=null&&lista_situ_laboral.get(pos)!=null?lista_situ_laboral.get(pos).getRevista():"";
	}
	
	public String getCuit() {
		return cuit;
	}

	public void setCuit(String cuit) {
		this.cuit = cuit;
	}

	public String getRazonSoc() {
		return razonSoc;
	}

	public void setRazonSoc(String razonSoc) {
		this.razonSoc = razonSoc;
	}

	public String getId_tercerizadora() {
		return id_tercerizadora;
	}

	public void setId_tercerizadora(String id_tercerizadora) {
		this.id_tercerizadora = id_tercerizadora;
	}

	public String getNacionalidad_string() {
		return nacionalidad_string;
	}

	public void setNacionalidad_string(String nacionalidad_string) {
		this.nacionalidad_string = nacionalidad_string;
	}

	public String getTipoOperacion() {
		return tipoOperacion;
	}

	public void setTipoOperacion(String tipo_operacion) {
		this.tipoOperacion = tipo_operacion;
	}

	public String getDesc_tercerizadora() {
		return desc_tercerizadora;
	}

	public void setDesc_tercerizadora(String descTercerizadora) {
		desc_tercerizadora = descTercerizadora;
	}

	public BigDecimal getValorCapita() {
		return valorCapita;
	}

	public void setValorCapita(BigDecimal valorCapita) {
		this.valorCapita = valorCapita;
	}
	
	/**
	 * getFechaVtoDocDiscap: devuelve la fecha de vencimiento si existiera el documento de discapacidad
	 * Se utiliza el atributo fecha_baja como el campo fecha_vto del afi_documento
	 * 
	 */
	public Date getFechaVtoDocDiscap(){
		
		Date fechaVto=null; // No tiene documento
		if(this.getDocumentacion() !=null && this.getDocumentacion().size()>0){
//			Se supone el query solo trae el afi_documento de discapacidad con la fecha maxima unicamente
			AfiDocumentacion doc = this.getDocumentacion().get(0);
//			fechaVto = DateUtils.format( doc.getFecha_baja() , "dd/MM/yyyy" );
			fechaVto = doc.getFecha_baja();
		}
		
		return fechaVto;
	}

	public List<AfiDocumentacion> getDocumentacion() {
		return documentacion;
	}

	public void setDocumentacion(List<AfiDocumentacion> documentacion) {
		this.documentacion = documentacion;
	}

	public int getId_civil_esta() {
		return id_civil_esta;
	}

	public void setId_civil_esta(int id_civil_esta) {
		this.id_civil_esta = id_civil_esta;
	}

	public int getId_categoria() {
		return id_categoria;
	}

	public void setId_categoria(int id_categoria) {
		this.id_categoria = id_categoria;
	}

	public Date getFPP() {
		return FPP;
	}

	public void setFPP(Date fPP) {
		FPP = fPP;
	}

	public int getCenso2013() {
		return censo2013;
	}

	public void setCenso2013(int censo2013) {
		this.censo2013 = censo2013;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public AfiPlan getAfiPlan() {
		return afiPlan;
	}

	public void setAfiPlan(AfiPlan afiPlan) {
		this.afiPlan = afiPlan;
	}

	public int getId_parentesco() {
		return id_parentesco;
	}

	public void setId_parentesco(int id_parentesco) {
		this.id_parentesco = id_parentesco;
	}


	public int getIdCorrespondencia() {
		return idCorrespondencia;
	}

	public void setIdCorrespondencia(int idCorrespondencia) {
		this.idCorrespondencia = idCorrespondencia;
	}

	public int getTieneAntecedentesJudiciales() {
		return tieneAntecedentesJudiciales;
	}

	public void setTieneAntecedentesJudiciales(int tieneAntecedentesJudiciales) {
		this.tieneAntecedentesJudiciales = tieneAntecedentesJudiciales;
	}

	public int getClientePreferencial() {
		return clientePreferencial;
	}

	public void setClientePreferencial(int clientePreferencial) {
		this.clientePreferencial = clientePreferencial;
	}

	public AfiliacionPrevencion getPrevencion() {
		return prevencion;
	}

	public void setPrevencion(AfiliacionPrevencion prevencion) {
		this.prevencion = prevencion;
	}

	public long getNroCredencial() {
		return nroCredencial;
	}

	public void setNroCredencial(long nroCredencial) {
		this.nroCredencial = nroCredencial;
	}

	public String getProyecto() {
		return proyecto;
	}

	public void setProyecto(String proyecto) {
		this.proyecto = proyecto;
	}
	
	public void setPlanAfiliado(String plan) {
			this.ultimo_plan= new Plan(plan);
	}
	public void setDetalleOpcionSs(DetalleOpcionesSS detalleOpcionsss){
		detalleOpcionSss=detalleOpcionsss;
	}
	
	public DetalleOpcionesSS getDetalleOpcionSs(){
		return  detalleOpcionSss ; 
	}
	
	public void setDetalleFechasSuperintendencia(DetalleFechasSuper detalleFechasSss){
		this.detalleFechasSss=detalleFechasSss;
	}
	
	public DetalleFechasSuper getDetalleFechasSuperintendencia(){
		return  detalleFechasSss; 
	}
	 
	public static Afiliado getMappingAfiliadoConDomicilioyDocDiscapacidad(ResultSet rs) throws SQLException{
		Domicilio afiDomicilio = null;
		AfiDocumentacion documDiscapacidad = null;
		Afiliado afiliado = new Afiliado();
		afiDomicilio = new Domicilio();
		afiliado.setCuil_titular(rs.getString("cuil_titular"));
		afiliado.setInte(rs.getInt("inte"));
		afiliado.setId_ospim(rs.getInt("id_ospim"));
		afiliado.setId_uoma(rs.getInt("id_uoma"));
		afiliado.setId_amtima(rs.getInt("id_amtima"));
		afiliado.setApellido(rs.getString("apellido"));
		afiliado.setNombre(rs.getString("nombre"));
		afiliado.setDocumento_tipo(rs.getString("documento_tipo"));
		afiliado.setSexo(rs.getString("sexo"));
		afiliado.setCuil(rs.getString("cuil") != null ? rs.getString("cuil") : "");
		afiliado.setNaci_fecha(rs.getDate("naci_fecha"));
		afiliado.setId_civil_esta(rs.getInt("id_estado_civil_sss"));
		afiliado.setCivil_esta(rs.getString("civil_esta"));
		afiliado.setNacionalidad(rs.getInt("nacionalidad"));
		afiliado.setId_parentesco(rs.getInt("id_parentesco_sss"));
		afiliado.setParentesco(rs.getString("parentesco"));
		afiliado.setSeccional(new Seccional(rs.getInt("id_seccional"), rs.getString("descripcion")));
		afiliado.setAnterior_os(rs.getInt("anterior_os"));
		afiliado.setVigen_fecha(rs.getDate("vigen_fecha"));
		afiliado.setObservaciones(rs.getString("observaciones"));
		afiliado.setAlta_usr(rs.getString("alta_usr"));
		afiliado.setModi_usr(rs.getString("modi_usr"));
		afiliado.setDiscapacitado(rs.getString("discapacitado"));
		afiliado.setCenso2013(rs.getInt("censo2013"));
		afiliado.setEmail(rs.getString("email"));
		afiliado.setDocu_numero(rs.getString("docu_numero") != null ? rs.getString("docu_numero") : "");
		afiliado.setIdCorrespondencia(rs.getInt("id_correspondencia"));
		afiDomicilio.setDomi_tipo(rs.getString("domi_tipo"));
		afiDomicilio.setCalle(rs.getString("calle"));
		afiDomicilio.setPiso(rs.getString("piso") != null ? rs.getString("piso") : "");
		afiDomicilio.setDepto(rs.getString("depto") != null ? rs.getString("depto") : "");
		afiDomicilio.setOficina(rs.getString("oficina") != null ? rs.getString("oficina") : "");
		afiDomicilio.setPostal_codi(rs.getString("postal_codi"));
		afiDomicilio.setBarrio(rs.getString("barrio") != null ? rs.getString("barrio") : "");
		afiDomicilio.setCod_area_telefono(rs.getString("cod_area_telefono") != null ? rs.getString("cod_area_telefono") : "");
		afiDomicilio.setTelefono(rs.getString("telefono") != null ? rs.getString("telefono") : "");
		afiDomicilio.setCod_area_tel_laboral(rs.getString("cod_area_tel_laboral") != null ? rs.getString("cod_area_tel_laboral") : "");
		afiDomicilio.setTel_laboral(rs.getString("tel_laboral") != null ? rs.getString("tel_laboral") : "");
		afiDomicilio.setCod_area_celular(rs.getString("cod_area_celular") != null ? rs.getString("cod_area_celular") : "");
		afiDomicilio.setCelular(rs.getString("celular") != null ? rs.getString("celular") : "");
		afiDomicilio.setObservaciones(rs.getString("observaciones_dom"));
		afiDomicilio.setDomi_val(rs.getString("domi_val"));
		afiDomicilio.setAlta_usr(rs.getString("alta_usr_d"));
		afiDomicilio.setModi_usr(rs.getString("modi_usr_d"));
		afiDomicilio.setProvinciaId(rs.getInt("provincia"));
		afiDomicilio.setLocalidadId(rs.getInt("localidad"));
		afiDomicilio.setNumero(rs.getString("numero"));
		afiliado.setAportante_titular(rs.getInt("aportante_titular"));
		afiliado.setBaja_fecha(rs.getDate("baja_f"));
		afiliado.setBaja_usr(rs.getString("baja_u"));
		afiliado.setIngre_fecha(rs.getDate("ingre_f"));
		afiliado.setDomicilioDefault(afiDomicilio);
		afiliado.setId_motivo_baja(rs.getInt("id_motivo_baja"));
		afiliado.setId_amtima_baja_fecha(rs.getDate("id_amtima_baja_fecha"));
		afiliado.setId_ospim_baja_fecha(rs.getDate("id_ospim_baja_fecha"));
		afiliado.setId_uoma_baja_fecha(rs.getDate("id_uoma_baja_fecha"));
		afiliado.setTieneAntecedentesJudiciales(rs.getInt("tiene_antecedentes_judiciales"));
		afiliado.setClientePreferencial(rs.getInt("cliente_preferencial"));
		afiliado.setProyecto(rs.getString("proyecto"));
		afiliado.setDetalleFechasSuperintendencia(DetalleFechasSuper.getMapping("", rs));
		
		// Ver si trajo algun documento del tipo discapacidad
		Integer idDocum = rs.getInt("id_afi_docum");
		List<AfiDocumentacion> documentos = new ArrayList<AfiDocumentacion>();
		if(idDocum != null){
			documDiscapacidad = new AfiDocumentacion();
			documDiscapacidad.setId(idDocum);
			documDiscapacidad.setDocumento(new Documento(rs.getInt("id_documento"), ""));
			documDiscapacidad.setFecha_baja(rs.getDate("fecha_vto"));
			documentos.add(documDiscapacidad);
			afiliado.setDocumentacion(documentos);
		}
		
		return afiliado;
	}

	public List<Incidente> getIncidentes() {
		return incidentes;
	}

	public void setIncidentes(List<Incidente> incidentes) {
		this.incidentes = incidentes;
	}
 
	public void addIncidente(Incidente incidente) {
		if (incidentes == null) {
			incidentes =  new ArrayList<Incidente>();
		}
		this.incidentes.add(incidente);
	}

	public List<AfiSuspencionCobertura> getSuspencionCobertura() {
		return suspencionCobertura;
	}

	public void setSuspencionCobertura(List<AfiSuspencionCobertura> suspencionCobertura) {
		this.suspencionCobertura = suspencionCobertura;
	}
	
	public void addUltimaSuspCobertura(AfiSuspencionCobertura asc) {
		if(asc.getId() > 0) {
			if(suspencionCobertura==null) {
				suspencionCobertura=new ArrayList<AfiSuspencionCobertura>();
			}
			suspencionCobertura.add(asc);
		}
		
	}

	public Integer getEdad() {
		return edad;
	}

	public void setEdad(Integer edad) {
		this.edad = edad;
	}
	
	
	
}