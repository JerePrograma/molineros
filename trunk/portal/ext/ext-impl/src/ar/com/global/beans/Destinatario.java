package ar.com.global.beans;

import java.sql.ResultSet;
import java.sql.SQLException;



/**
 * @author sistema-09
 * @version 1.0
 * @created 30-Jul-2010 05:27:49 p.m.
 */
public class Destinatario{
	private int idDestinatario;
	private String email;
	private String mailtype;
	private String gender;			
	private String firstname;
	private String lastname;
	private String title;
	private String accion;
	private String[] listas;
	private boolean casillaPrueba;
	
	public Destinatario(){}
	
	public static Destinatario getMapping(ResultSet rs) throws SQLException{
		Destinatario dest=new Destinatario();
		dest.setIdDestinatario(rs.getInt("idDestinatario"));				
		dest.setEmail(rs.getString("email"));
		dest.setMailtype(rs.getString("mailtype"));
		dest.setGender(rs.getString("gender"));
		dest.setFirstname(rs.getString("firstname"));
		dest.setLastname(rs.getString("lastname"));
		dest.setTitle(rs.getString("title"));
		return dest;
	}

	public int getIdDestinatario() {
		return idDestinatario;
	}

	public void setIdDestinatario(int idDestinatario) {
		this.idDestinatario = idDestinatario;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getMailtype() {
		return mailtype;
	}

	public void setMailtype(String mailtype) {
		this.mailtype = mailtype;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAccion() {
		return accion;
	}

	public void setAccion(String accion) {
		this.accion = accion;
	}

	public String[] getListas() {
		return listas;
	}

	public void setListas(String[] listas) {
		this.listas = listas;
	}

	public boolean isCasillaPrueba() {
		return casillaPrueba;
	}

	public void setCasillaPrueba(boolean casillaPrueba) {
		this.casillaPrueba = casillaPrueba;
	}
	
	
	
	
}
