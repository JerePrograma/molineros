package ar.com.ospim.procesaArchivos.beans.farmaciaospim;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ArchivoAdmifarm {

    private String usuario;
    private Date fecha_importacion;
    private Date periodo;
    private int totalrecords;
    private double totalpvp;
    private double totalentidad;
    private double totalospim;
    private double totaluoma;
    private double totalamtima;

    public static ArchivoAdmifarm getMapping(ResultSet rs) throws SQLException {
        ArchivoAdmifarm a = new ArchivoAdmifarm();
        a.setUsuario(rs.getString("arch_usuario"));
        a.setFecha_importacion(rs.getDate("arch_fecha_importacion"));
        a.setPeriodo(rs.getDate("arch_periodo"));
        a.setTotalrecords(rs.getInt("arch_registros"));
        a.setTotalpvp(rs.getDouble("arch_totalpvp"));
        a.setTotalentidad(rs.getDouble("arch_totalentidad"));
        a.setTotalospim(rs.getDouble("arch_totalospim"));
        a.setTotaluoma(rs.getDouble("arch_totaluoma"));
        a.setTotalamtima(rs.getDouble("arch_totalamtima"));
        return a;
    }

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public Date getFecha_importacion() {
		return fecha_importacion;
	}

	public void setFecha_importacion(Date fecha_importacion) {
		this.fecha_importacion = fecha_importacion;
	}

	public Date getPeriodo() {
		return periodo;
	}

	public void setPeriodo(Date periodo) {
		this.periodo = periodo;
	}

	public int getTotalrecords() {
		return totalrecords;
	}

	public void setTotalrecords(int totalrecords) {
		this.totalrecords = totalrecords;
	}

	public double getTotalpvp() {
		return totalpvp;
	}

	public void setTotalpvp(double totalpvp) {
		this.totalpvp = totalpvp;
	}

	public double getTotalentidad() {
		return totalentidad;
	}

	public void setTotalentidad(double totalentidad) {
		this.totalentidad = totalentidad;
	}

	public double getTotalospim() {
		return totalospim;
	}

	public void setTotalospim(double totalospim) {
		this.totalospim = totalospim;
	}

	public double getTotaluoma() {
		return totaluoma;
	}

	public void setTotaluoma(double totaluoma) {
		this.totaluoma = totaluoma;
	}

	public double getTotalamtima() {
		return totalamtima;
	}

	public void setTotalamtima(double totalamtima) {
		this.totalamtima = totalamtima;
	}
    
    
}
