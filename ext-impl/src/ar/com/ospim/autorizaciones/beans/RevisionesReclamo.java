package ar.com.ospim.autorizaciones.beans;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class RevisionesReclamo implements Serializable {

	/**
	 * 
	 * 
	 */
	private static final long serialVersionUID = -886930017592263415L;
        
    private ReclamoPrestacional reclamoprestacion;
    private int id; 
    private int idReclamoPrestacional;
    private Date fechaRevision;
    private String usrPresente;
    private String usrResolucion;
    private String usrResponsableResolucion;
    private String descObservacionMedica;
    
    private Date altaFecha;
    private String altaUsr;
    private String observacion ;           
    private Date modiFecha ;
    private String modiUsr;
    private Date bajaFecha ;
    private String bajaUsr;       

    
	

private ESTADOS estado;
	
	public enum ESTADOS {
		NUEVO, MODIF, BAJA
	};	
	    
	
	public ReclamoPrestacional  getReclamoPrestacion() {
		return reclamoprestacion;
	}

	public void setReclamoPrestacion(ReclamoPrestacional reclamoprestacion) {
		this.reclamoprestacion = reclamoprestacion;
	}

    public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getId_reclamo_prestacional() {
		return idReclamoPrestacional;
	}

	public void setId_reclamo_prestacional(int id_reclamo_prestacional) {
		this.idReclamoPrestacional = id_reclamo_prestacional;
	}

	public Date getFecha_revision() {
		return fechaRevision;
	}

	public void setFecha_revision(Date fecha_revision) {
		this.fechaRevision = fecha_revision;
	}

	public String getUsr_presente() {
		return usrPresente;
	}

	public void setUsr_presente(String usr_presente) {
		this.usrPresente = usr_presente;
	}

	public String getUsr_resolucion() {
		return usrResolucion;
	}

	public void setUsr_resolucion(String usr_resolucion) {
		this.usrResolucion = usr_resolucion;
	}

	public String getUsr_responsable_resolucion() {
		return usrResponsableResolucion;
	}

	public void setUsr_responsable_resolucion(String usr_responsable_resolucion) {
		this.usrResponsableResolucion = usr_responsable_resolucion;
	}

	public Date getAlta_fecha() {
		return altaFecha;
	}

	public void setAlta_fecha(Date alta_fecha) {
		this.altaFecha = alta_fecha;
	}

	public String getAlta_usr() {
		return altaUsr;
	}

	public void setAlta_usr(String alta_usr) {
		this.altaUsr = alta_usr;
	}

	public Date getModi_fecha() {
		return modiFecha;
	}

	public void setModi_fecha(Date modi_fecha) {
		this.modiFecha = modi_fecha;
	}

	public String getModi_usr() {
		return modiUsr;
	}

	public void setModi_usr(String modi_usr) {
		this.modiUsr = modi_usr;
	}

	public Date getBaja_fecha() {
		return bajaFecha;
	}

	public void setBaja_fecha(Date baja_fecha) {
		this.bajaFecha = baja_fecha;
	}

	public String getBaja_usr() {
		return bajaUsr;
	}

	public void setBaja_usr(String baja_usr) {
		this.bajaUsr = baja_usr;
	}

    public RevisionesReclamo(){
    	super();
    }
    
    public RevisionesReclamo(Date fecha_revision, String usr_presente, String usr_resolucion, String usr_responsable_resolucion, 
    		String observacion ) {
    	
		super();							
		this.fechaRevision = fecha_revision;
		this.usrPresente = usr_presente; 
 		this.usrResolucion = usr_resolucion ; 
		this.usrResponsableResolucion =usr_responsable_resolucion;
		this.setObservacion(observacion);
        this.setObservacion(observacion);
        
	}

    
	public String getObservacion() {
		return observacion;
	}

	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}

	public static RevisionesReclamo getMapping(String prefix, ResultSet rs)
			throws SQLException {
    	
		RevisionesReclamo revreclamo= new RevisionesReclamo();
    	
    	
		revreclamo.getAlta_fecha();
		revreclamo.setId(rs.getInt(prefix+"id"));
		revreclamo.setId_reclamo_prestacional(rs.getInt(prefix+"id_reclamo_prestacional") );
		revreclamo.setFecha_revision(rs.getDate(prefix+"fecha_revision") );
		revreclamo.setUsr_presente(rs.getString(prefix+"usr_presente"));
		revreclamo.setUsr_resolucion(rs.getString(prefix+"usr_resolucion"));
		revreclamo.setUsr_responsable_resolucion(rs.getString(prefix+"usr_responsable_resolucion"));
		revreclamo.setAlta_fecha(rs.getDate(prefix+"alta_fecha"));
		revreclamo.setAlta_usr(rs.getString(prefix+"alta_usr"));
		revreclamo.setModi_fecha(rs.getDate(prefix+"modi_fecha"));
		revreclamo.setModi_usr(rs.getString(prefix+"modi_usr"));
		revreclamo.setBaja_fecha(rs.getDate(prefix+"baja_fecha"));
		revreclamo.setBaja_usr(rs.getString(prefix+"baja_usr"));
		
		revreclamo.setObservacion(rs.getString(prefix+"observacion"));
		if (revreclamo.getBaja_fecha()!=null ){
			revreclamo.setEstado(RevisionesReclamo.ESTADOS.BAJA);		
		}	

		
		return revreclamo;
	}

	public String getidToString() {
		return Integer.toString(id);
	}


	public String getFecha_revisionTostring() {
		SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");
		return fechaRevision != null ? sdf.format(fechaRevision): "";
	}

	public ESTADOS getEstado() {
		return estado;
	}

	public void setEstado(ESTADOS estado) {
		this.estado = estado;
	}

	@Override
	public String toString() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		String fechaRevisionStr = "";		
		
		if(fechaRevision !=null){
			fechaRevisionStr =sdf.format(fechaRevision);
		}
		
		return "RevisionesReclamo[fecharevision"+ fechaRevisionStr + ", idRevision ="+ id +", presentes ="+ usrPresente + ",resolucion ="+usrResolucion +", resp. resolucion ="+ usrResponsableResolucion+ "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
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
		RevisionesReclamo other = (RevisionesReclamo) obj;
		if (id != other.getId())
			return false;
		return true;
	}



	public String getDescObservacionMedica() {
		return descObservacionMedica;
	}

	public void setDescObservacionMedica(String descObservacionMedica) {
		this.descObservacionMedica = descObservacionMedica;
	}
	
	

	

}

