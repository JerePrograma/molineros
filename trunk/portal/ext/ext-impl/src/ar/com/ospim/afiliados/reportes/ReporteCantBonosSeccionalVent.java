package ar.com.ospim.afiliados.reportes;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class ReporteCantBonosSeccionalVent {
	private String rendido;
	private String anulado;
	private Date alta_fecha;
	private int nro_bono;
	private String descripcion;
	private int id_seccional;
	private int tipo_bono;	
	
	public ReporteCantBonosSeccionalVent() {
		
	}
	
	public ReporteCantBonosSeccionalVent(String rendidov,int nro_bonov,int id_seccionalv,Date alta_fechav,int tipo_bonov,String descripcionv ){	

		this.rendido=rendidov;
		this.alta_fecha = alta_fechav;
		this.nro_bono=nro_bonov;
		this.descripcion=descripcionv;
		this.id_seccional=id_seccionalv;
		this.tipo_bono=tipo_bonov;		
	}	

	public String getRendido() {
		return rendido;
	}
	
	public void setRendido(String rendido) {
		this.rendido = rendido;
	}

	public String getAnulado() {
		return anulado;
	}
	
	public void setAnulado(String anulado) {
		this.anulado = anulado;
	}

	public Date getAlta_fecha() {
		return alta_fecha;
	}
	
	public void setAlta_fecha(Date alta_fecha) {
		this.alta_fecha = alta_fecha;
	}

	public int getNro_bono() {
		return nro_bono;
	}
	
	public void setNro_bono(int nro_bono) {
		this.nro_bono = nro_bono;
	}

	public String getDescripcion() {
		return descripcion;
	}
	
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public int getId_seccional() {
		return id_seccional;
	}
	
	public void setId_seccional(int id_seccional) {
		this.id_seccional = id_seccional;
	}

	public int getTipo_bono() {
		return tipo_bono;
	}
	
	public void setTipo_bono(int tipo_bono) {
		this.tipo_bono = tipo_bono;
	}
	
	public static ReporteCantBonosSeccionalVent getMapping(String prefix, ResultSet rs) throws SQLException {
		
		ReporteCantBonosSeccionalVent rbv = new ReporteCantBonosSeccionalVent();
		
		rbv.setAlta_fecha(rs.getDate(prefix+"alta_fecha"));
//		rbv.setAnulado(anulado);
		rbv.setDescripcion(rs.getString(prefix+"descripcion"));
		rbv.setId_seccional(rs.getInt(prefix+"id_Seccional"));
		rbv.setNro_bono(rs.getInt(prefix + "nro_bono"));
		rbv.setRendido(rs.getString(prefix + "rendido"));
		rbv.setTipo_bono(rs.getInt(prefix +"tipo_bono"));
		
		
		return rbv;
		
	}
	
	
}
