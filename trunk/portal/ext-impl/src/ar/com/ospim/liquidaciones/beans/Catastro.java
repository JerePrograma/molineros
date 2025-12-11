package ar.com.ospim.liquidaciones.beans;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import ar.com.ospim.afiliados.beans.Afiliado;
import ar.com.ospim.global.beans.Prestacion;
import ar.com.ospim.util.DateUtils;

/**
 * @author carlos rivas
 * @version 1.0
 * @created 25-Ago-2010 02:25:56 p.m.
 */
public class Catastro{
	private int id;
	private Afiliado afiliado;
	private String pieza;
	private String cara;
	private Date fecha_prestacion;	
	private PlanPrestacion plan_prestacion;
	private String codigo;
	
	public Catastro() {

	}
	
	public Catastro(int id, Afiliado afiliado, String pieza, String cara,
			Date fechaPrestacion, PlanPrestacion planPrestacion, String codigo) {
		super();
		this.id = id;
		this.afiliado = afiliado;
		this.pieza = pieza;
		this.cara = cara;
		this.fecha_prestacion = fechaPrestacion;
		this.plan_prestacion = planPrestacion;
		this.codigo = codigo;
	}

	public Catastro(Afiliado afiliado, String pieza, String cara,
			Date fechaPrestacion, int id_codigo, String codigo) {
		super();		
		this.afiliado = afiliado;
		this.pieza = pieza;
		this.cara = cara;
		this.fecha_prestacion = fechaPrestacion;
		Prestacion nomencla = new Prestacion(id_codigo, "");
		Plan plan = new Plan(1, ""); 
		this.plan_prestacion = new PlanPrestacion();
		this.plan_prestacion.setNomenclador(nomencla);
		this.plan_prestacion.setPlan(plan);
		this.codigo = codigo;
	}


	/**
	 * @return the afiliado
	 */
	public Afiliado getAfiliado() {
		return afiliado;
	}

	/**
	 * @param afiliado the afiliado to set
	 */
	public void setAfiliado(Afiliado afiliado) {
		this.afiliado = afiliado;
	}

	/**
	 * @return the pieza
	 */
	public String getPieza() {
		return pieza;
	}

	/**
	 * @param pieza
	 *            the pieza to set
	 */
	public void setPieza(String pieza) {
		this.pieza = pieza;
	}

	/**
	 * @return the cara
	 */
	public String getCara() {
		return cara;
	}

	/**
	 * @param cara
	 *            the cara to set
	 */
	public void setCara(String cara) {
		this.cara = cara;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}
	
	/**
	 * @return the fecha_prestacion
	 */
	public Date getFecha_prestacion() {
		return fecha_prestacion;
	}

	/**
	 * @param fechaPrestacion the fecha_prestacion to set
	 */
	public void setFecha_prestacion(Date fechaPrestacion) {
		fecha_prestacion = fechaPrestacion;
	}

	/**
	 * @return the plan_prestacion
	 */
	public PlanPrestacion getPlan_prestacion() {
		return plan_prestacion;
	}

	/**
	 * @param planPrestacion the plan_prestacion to set
	 */
	public void setPlan_prestacion(PlanPrestacion planPrestacion) {
		plan_prestacion = planPrestacion;
	}

	/**
	 * @return the codigo
	 */
	public String getCodigo() {
		return codigo;
	}

	/**
	 * @param codigo the codigo to set
	 */
	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public String getFechaAsString() {
		return null!=fecha_prestacion?DateUtils.format(fecha_prestacion,DateUtils.SHORT):"";
	}
	
	public static Catastro getMapping(ResultSet rs,
			String prefix) throws SQLException {
		Catastro catastro = new Catastro();		
		catastro.setId(rs
				.getInt(prefix + "id"));		
		catastro.setPieza(rs.getString(prefix + "pieza"));
		catastro.setCara(rs.getString(prefix + "cara"));
		catastro.setFecha_prestacion(rs.getDate(prefix
				+ "fecha_prestacion"));		
		catastro.setCodigo(rs.getString(prefix+"codigo"));
		catastro.setAfiliado(new Afiliado(rs.getString(prefix+"cuil_titular"), rs.getInt(prefix+"inte")));
		Prestacion nomencla = new Prestacion(rs.getInt(prefix+"id_prestacion"), "");
		Plan plan = new Plan(1, ""); 
		catastro.setPlan_prestacion(new PlanPrestacion());
		catastro.getPlan_prestacion().setNomenclador(nomencla);
		catastro.getPlan_prestacion().setPlan(plan);
		return catastro;
	}	
}