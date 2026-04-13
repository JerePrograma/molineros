package ar.com.ospim.afiliados.reportes;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class ReporteAfiliadosAnses {
	
		private String tipo ;
		private Date periodo;
		private String cuil_titular ;
		private String cuil ;
		private int inte ;
		private String apellido;
		private String nombre;		
		private Date naci_fecha;
		private int edadAnios ;
		private String edad;	
		
		public ReporteAfiliadosAnses() {
			
		}
		
		public ReporteAfiliadosAnses(String tipov, Date periodov, String cuil_titularv, 
				String cuilv , int intev, String apellidov, String nombrev, 
				Date naci_fechav, int edadAniosv, String edadv)
		{	
			this.tipo =tipov;
			this.periodo=periodov;
			this.cuil_titular=cuil_titularv;
			this.cuil = cuilv;
			this.inte = intev;
			this.apellido= apellidov;
			this.nombre= nombrev;
			this.naci_fecha= naci_fechav;
			this.edadAnios = edadAniosv;
			this.edad = edadv;					
		}

		public String getTipo() {
			return tipo;
		}

		public void setTipo(String tipo) {
			this.tipo = tipo;
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

		public void setCuil_titular(String cuil_titular) {
			this.cuil_titular = cuil_titular;
		}

		public String getCuil() {
			return cuil;
		}

		public void setCuil(String cuil) {
			this.cuil = cuil;
		}

		public int getInte() {
			return inte;
		}

		public void setInte(int inte) {
			this.inte = inte;
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

		public Date getNaci_fecha() {
			return naci_fecha;
		}

		public void setNaci_fecha(Date naci_fecha) {
			this.naci_fecha = naci_fecha;
		}

		public int getEdadAnios() {
			return edadAnios;
		}

		public void setEdadAnios(int edadAnios) {
			this.edadAnios = edadAnios;
		}

		public String getEdad() {
			return edad;
		}

		public void setEdad(String edad) {
			this.edad = edad;
		}	

		public static ReporteAfiliadosAnses getMapping(String prefix, ResultSet rs) throws SQLException{	
			
			ReporteAfiliadosAnses raa = new ReporteAfiliadosAnses();

			raa.setApellido(rs.getString(prefix+"apellidov") );
			raa.setCuil(rs.getString(prefix+"cuilv"));
			raa.setCuil_titular(rs.getString(prefix+"cuil_titularv"));
			raa.setEdad(rs.getString(prefix+"edadv"));
			raa.setEdadAnios(rs.getInt(prefix+"edadyearsv"));
			raa.setInte(rs.getInt(prefix+"intev"));
			raa.setNaci_fecha(rs.getDate(prefix+"naci_fechav"));
			raa.setNombre(rs.getString(prefix+"nombrev"));
			raa.setPeriodo(rs.getDate(prefix+"periodov"));
			raa.setTipo(rs.getString(prefix+"tipov"));
			
			return raa;
		}
		
		
	}
