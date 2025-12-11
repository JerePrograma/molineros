package ar.com.ospim.global.beans;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import ar.com.ospim.procesaArchivos.beans.opcionesss.DetalleOpcionesSS;

/**
 * @author SVA
 * @version 1.0
 * @created 19-Nov-2013 03:30:13 p.m.
 */
public class Delegacion implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3610156319142422331L;
	private int id_delegacion;
	private String descripcion;
	private int libro;
	private int rubrica; // deberia ser id_localidad y en la tabla tambien habria q cambiarlo.
	private int tomo;
	private boolean esCentral;
	private Date altaFecha;

	public Delegacion() {

	}
	
	public Delegacion(int id) {
		this.id_delegacion= id;		
	}

	public Delegacion(int id, String descripcion) {
		this.id_delegacion = id;
		this.descripcion = descripcion;
	}

	public Delegacion(int id, String descripcion, int libro, int rubrica) {
		this.id_delegacion = id;
		this.descripcion = descripcion;
		this.libro = libro;
		this.rubrica = rubrica;
	}
	
	public Delegacion(int id_delegacion, String descripcion, int libro,
			int rubrica, int tomo, boolean esCentral, Date altaFecha) {
		super();
		this.id_delegacion = id_delegacion;
		this.descripcion = descripcion;
		this.libro = libro;
		this.rubrica = rubrica;
		this.tomo = tomo;
		this.esCentral = esCentral;
		this.altaFecha = altaFecha;
	}

	/**
	 * @return the id_seccional
	 */
	public int getId() {
		return id_delegacion;
	}

	/**
	 * @param idSeccional
	 *            the id_seccional to set
	 */
	public void setId_delegacion(int idDelegacion) {
		id_delegacion = idDelegacion;
	}

	/**
	 * @return the descripcion
	 */
	public String getDescripcion() {
		return descripcion;
	}

	/**
	 * @param descripcion
	 *            the descripcion to set
	 */
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public int getLibro() {
		return libro;
	}

	public void setLibro(int libro) {
		this.libro = libro;
	}

	public int getRubrica() {
		return rubrica;
	}

	public void setRubrica(int rubrica) {
		this.rubrica = rubrica;
	}

	public int getTomo() {
		return tomo;
	}

	public void setTomo(int tomo) {
		this.tomo = tomo;
	}
	
	public static Delegacion getMapping(ResultSet rs, String prefix)
			throws SQLException {
		
		Delegacion delegacion = new Delegacion();
		delegacion.setId_delegacion(rs.getInt(prefix + "id_delegacion"));
		delegacion.setDescripcion(rs.getString(prefix + "descripcion"));
		delegacion.setTomo(rs.getInt(prefix + "tomo"));
		delegacion.setLibro(rs.getInt(prefix + "libro"));
		delegacion.setRubrica(rs.getInt(prefix + "rubrica"));
		delegacion.setEsCentral(rs.getBoolean(prefix + "sedecentral"));
		delegacion.setAltaFecha(rs.getDate(prefix + "alta_fecha")) ;
		return delegacion;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id_delegacion;
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
		Delegacion other = (Delegacion) obj;
		if (id_delegacion != other.id_delegacion)
			return false;
		return true;
	}

	public boolean isEsCentral() {
		return esCentral;
	}

	public void setEsCentral(boolean esCentral) {
		this.esCentral = esCentral;
	}

	public Date getAltaFecha() {
		return altaFecha;
	}

	public void setAltaFecha(Date altaFecha) {
		this.altaFecha = altaFecha;
	}

	public int getId_delegacion() {
		return id_delegacion;
	}

	public static Delegacion parseLine(String line) throws ParseException {
		Delegacion deleg=null;
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		String[] linea = line.split("\\|");

//		select * from delegacion where id = 10
//		10|112608|CARCARANA|12901|1|No|1|23-09-2013|
//		id, ooss, descripcion, rubrica, libro, sedecentral, tomo, alta
//		integer, integer, character varying(50), integer, integer, boolean, integer, date
		
		if(linea[1].equals("112608")) {   // filtramos solo Ospim
			deleg = new Delegacion();
			deleg.id_delegacion = Integer.parseInt(linea[0]);
	//		deleg.ooss = linea[1] != null && linea[1].trim().length() > 0 ? Integer.parseInt(linea[1].trim()) : 0;
			deleg.descripcion = linea[2].trim();
			deleg.rubrica = Integer.parseInt(linea[3]);
			deleg.libro = Integer.parseInt(linea[4]);
			deleg.esCentral = Boolean.valueOf(linea[5].equalsIgnoreCase("Si")?"true":"false");
			deleg.tomo = Integer.parseInt(linea[6]);
			deleg.altaFecha = linea[7] != null && linea[7].trim().length() > 0 ? sdf.parse(linea[7].trim()) : null;
		}		
		return deleg;

	}
}