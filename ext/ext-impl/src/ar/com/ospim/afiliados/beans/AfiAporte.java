package ar.com.ospim.afiliados.beans;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import ar.com.ospim.util.DateUtils;

/**
 * @author Martin Moreyra
 * @version 1.0
 * @created 29-Jul-2010 11:34:23 a.m.
 */

@Deprecated
public class AfiAporte implements Serializable {
	private static final long serialVersionUID = 1L;
	private TipoAporte tipoAporte;
	private Date fecha_ingre;
	private Date fecha_egre;
	private MotivoBaja motivo_baja;
	private Afiliado afiliado;
	private int id_afiliado;
	private Date alta_fecha;
	private String alta_usr;
	private Date modi_fecha;
	private String modi_usr;
	private Date baja_fecha;
	private String baja_usr;
	private boolean es_os;
	private int id_socio;
	private String tipo_aporte;

	public AfiAporte() {
	}

	public AfiAporte(String cuil_titular, int inte, String cuil, int id_aporte,
			String descripAporte, Date fecha_ingreso, Date fecha_egreso,
			String descrip_motivo_baja, int id_afiliado) {
		this.afiliado = new Afiliado(cuil_titular, inte, cuil);
		this.tipoAporte = new TipoAporte(id_aporte, descripAporte);
		this.fecha_ingre = fecha_ingreso;
		this.fecha_egre = fecha_egreso;
		this.motivo_baja = new MotivoBaja(descrip_motivo_baja);
		this.id_afiliado = id_afiliado;
	}
	public AfiAporte(String cuil_titular, int inte, String cuil, int id_aporte,
			String descripAporte, Date fecha_ingreso, Date fecha_egreso,
			String descrip_motivo_baja, int id_afiliado, boolean es_os) {
		this.afiliado = new Afiliado(cuil_titular, inte, cuil);
		this.tipoAporte = new TipoAporte(id_aporte, descripAporte);
		this.fecha_ingre = fecha_ingreso;
		this.fecha_egre = fecha_egreso;
		this.motivo_baja = new MotivoBaja(descrip_motivo_baja);
		this.id_afiliado = id_afiliado;
		this.es_os=es_os;
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

	public TipoAporte getTipoAporte() {
		return tipoAporte;
	}

	public void setTipoAporte(TipoAporte tipoAporte) {
		this.tipoAporte = tipoAporte;
	}

	public Date getFecha_ingre() {
		return fecha_ingre;
	}

	public int getId_afiliado() {
		return id_afiliado;
	}

	public void setId_afiliado(int idAfiliado) {
		id_afiliado = idAfiliado;
	}

	public String getFecha_ingreAsString() {
		return fecha_ingre != null ? DateUtils
				.format(fecha_ingre, "dd/MM/yyyy") : "";
	}

	public void setFecha_ingre(Date fechaIngre) {
		fecha_ingre = fechaIngre;
	}

	public Date getFecha_egre() {
		return fecha_egre;
	}

	public String getFecha_egreAsString() {
		return fecha_egre != null ? DateUtils.format(fecha_egre, "dd/MM/yyyy")
				: "";
	}

	public void setFecha_egre(Date fechaBaja) {
		fecha_egre = fechaBaja;
	}

	public MotivoBaja getMotivo_baja() {
		return motivo_baja;
	}

	public void setMotivo_baja(MotivoBaja motivoBaja) {
		motivo_baja = motivoBaja;
	}

	public Afiliado getAfiliado() {
		return afiliado;
	}

	public void setAfiliado(Afiliado afiliado) {
		this.afiliado = afiliado;
	}
	
	

	public boolean isEs_os() {
		return es_os;
	}

	public void setEs_os(boolean es_os) {
		this.es_os = es_os;
	}

	public int getId_socio() {
		return id_socio;
	}

	public void setId_socio(int id_socio) {
		this.id_socio = id_socio;
	}

	public String getTipo_aporte() {
		return tipo_aporte;
	}

	public void setTipo_aporte(String tipo_aporte) {
		this.tipo_aporte = tipo_aporte;
	}

	public static AfiAporte getMapping(ResultSet rs, String prefix)
			throws SQLException {
		AfiAporte ap = new AfiAporte();
		ap.setAfiliado(new Afiliado(rs.getString(prefix + "cuil_titular"), rs
				.getInt(prefix + "inte")));
		ap.setFecha_egre(rs.getDate(prefix + "fecha_egre"));
		ap.setFecha_ingre(rs.getDate(prefix + "fecha_ingre"));
		ap.setId_socio(rs.getInt(prefix + "id_socio"));
		ap.setTipo_aporte(rs.getString(prefix + "tipo_aporte"));
		
		ap.setMotivo_baja(new MotivoBaja(rs
				.getInt(prefix + "id_motivo_baja"), rs.getString("desc_motivo_baja")));
		
		ap.setTipoAporte(new TipoAporte(rs.getInt(prefix + "id_aporte"), ""));
		ap.setAlta_fecha(rs.getDate(prefix + "alta_fecha"));
		ap.setAlta_usr(rs.getString(prefix + "alta_usr"));
		ap.setModi_fecha(rs.getDate(prefix + "modi_fecha"));
		ap.setModi_usr(rs.getString(prefix + "modi_usr"));
		ap.setBaja_fecha(rs.getDate(prefix + "baja_fecha"));
		ap.setBaja_usr(rs.getString(prefix + "baja_usr"));
		return ap;
	}
	
}