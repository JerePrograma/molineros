/**
 * AltaBeneficiario.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package ar.com.ospim.webservice.omint;

public class Beneficiario  implements java.io.Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static String namespace_uri = "http://tempuri.org/";
	private static String namespace_schema = "http://www.w3.org/2001/XMLSchema";
	
	private java.lang.String sessionID;

    private java.lang.String CUILTitular;

    private java.util.Calendar fecVig;

    private java.lang.String apellido;

    private java.lang.String nombre;

    private java.lang.String parentesco;

    private java.lang.String sexo;

    private java.util.Calendar fecNac;

    private java.lang.String calle;

    private java.lang.String nroCalle;

    private java.lang.String resto;

    private java.lang.String localidad;

    private java.lang.String CP;

    private java.lang.String provincia;

    private java.lang.String telefono;

    private java.lang.String tipoDoc;

    private java.lang.String nroDoc;

    private java.lang.String seccional;

    private int categoria;

    private java.lang.String CUIL;
    
    private int nacionalidad;

    private int estadoCivil;
    
    private java.util.Calendar FPP;

    private int nroIntegrante;

    private java.lang.String discapacidad;
    
    private java.util.Calendar fecBaja;
    
    private Integer inte;
    
    public Beneficiario() {
    }
    
    public Beneficiario(String uri, String schema){
    	super();
    	namespace_uri = uri;
    	namespace_schema = schema;
    }

    public Beneficiario(
           java.lang.String sessionID,
           java.lang.String CUILTitular,
           java.util.Calendar fecVig,
           java.lang.String apellido,
           java.lang.String nombre,
           java.lang.String parentesco,
           java.lang.String sexo,
           java.util.Calendar fecNac,
           java.lang.String calle,
           java.lang.String nroCalle,
           java.lang.String resto,
           java.lang.String localidad,
           java.lang.String CP,
           java.lang.String provincia,
           java.lang.String telefono,
           java.lang.String tipoDoc,
           java.lang.String nroDoc,
           java.lang.String seccional,
           int categoria,
           java.lang.String CUIL,
           int nacionalidad,
           int estadoCivil,
           java.util.Calendar FPP,
           int nroIntegrante,
           String discapacidad) {
           this.sessionID = sessionID;
           this.CUILTitular = CUILTitular;
           this.fecVig = fecVig;
           this.apellido = apellido;
           this.nombre = nombre;
           this.parentesco = parentesco;
           this.sexo = sexo;
           this.fecNac = fecNac;
           this.calle = calle;
           this.nroCalle = nroCalle;
           this.resto = resto;
           this.localidad = localidad;
           this.CP = CP;
           this.provincia = provincia;
           this.telefono = telefono;
           this.tipoDoc = tipoDoc;
           this.nroDoc = nroDoc;
           this.seccional = seccional;
           this.categoria = categoria;
           this.CUIL = CUIL;
           this.nacionalidad = nacionalidad;
           this.estadoCivil = estadoCivil;
           this.FPP = FPP;
           this.nroIntegrante = nroIntegrante;
           this.discapacidad = discapacidad;
    }


    /**
     * Gets the sessionID value for this AltaBeneficiario.
     * 
     * @return sessionID
     */
    public java.lang.String getSessionID() {
        return sessionID;
    }


    /**
     * Sets the sessionID value for this AltaBeneficiario.
     * 
     * @param sessionID
     */
    public void setSessionID(java.lang.String sessionID) {
        this.sessionID = sessionID;
    }


    /**
     * Gets the CUILTitular value for this AltaBeneficiario.
     * 
     * @return CUILTitular
     */
    public java.lang.String getCUILTitular() {
        return CUILTitular;
    }


    /**
     * Sets the CUILTitular value for this AltaBeneficiario.
     * 
     * @param CUILTitular
     */
    public void setCUILTitular(java.lang.String CUILTitular) {
        this.CUILTitular = CUILTitular;
    }


    /**
     * Gets the fecVig value for this AltaBeneficiario.
     * 
     * @return fecVig
     */
    public java.util.Calendar getFecVig() {
        return fecVig;
    }


    /**
     * Sets the fecVig value for this AltaBeneficiario.
     * 
     * @param fecVig
     */
    public void setFecVig(java.util.Calendar fecVig) {
        this.fecVig = fecVig;
    }


    /**
     * Gets the apellido value for this AltaBeneficiario.
     * 
     * @return apellido
     */
    public java.lang.String getApellido() {
        return apellido;
    }


    /**
     * Sets the apellido value for this AltaBeneficiario.
     * 
     * @param apellido
     */
    public void setApellido(java.lang.String apellido) {
        this.apellido = apellido;
    }


    /**
     * Gets the nombre value for this AltaBeneficiario.
     * 
     * @return nombre
     */
    public java.lang.String getNombre() {
        return nombre;
    }


    /**
     * Sets the nombre value for this AltaBeneficiario.
     * 
     * @param nombre
     */
    public void setNombre(java.lang.String nombre) {
        this.nombre = nombre;
    }


    /**
     * Gets the parentesco value for this AltaBeneficiario.
     * 
     * @return parentesco
     */
    public java.lang.String getParentesco() {
        return parentesco;
    }


    /**
     * Sets the parentesco value for this AltaBeneficiario.
     * 
     * @param parentesco
     */
    public void setParentesco(java.lang.String parentesco) {
        this.parentesco = parentesco;
    }


    /**
     * Gets the sexo value for this AltaBeneficiario.
     * 
     * @return sexo
     */
    public java.lang.String getSexo() {
        return sexo;
    }


    /**
     * Sets the sexo value for this AltaBeneficiario.
     * 
     * @param sexo
     */
    public void setSexo(java.lang.String sexo) {
        this.sexo = sexo;
    }


    /**
     * Gets the fecNac value for this AltaBeneficiario.
     * 
     * @return fecNac
     */
    public java.util.Calendar getFecNac() {
        return fecNac;
    }


    /**
     * Sets the fecNac value for this AltaBeneficiario.
     * 
     * @param fecNac
     */
    public void setFecNac(java.util.Calendar fecNac) {
        this.fecNac = fecNac;
    }


    /**
     * Gets the calle value for this AltaBeneficiario.
     * 
     * @return calle
     */
    public java.lang.String getCalle() {
        return calle;
    }


    /**
     * Sets the calle value for this AltaBeneficiario.
     * 
     * @param calle
     */
    public void setCalle(java.lang.String calle) {
        this.calle = calle;
    }


    /**
     * Gets the nroCalle value for this AltaBeneficiario.
     * 
     * @return nroCalle
     */
    public java.lang.String getNroCalle() {
        return nroCalle;
    }


    /**
     * Sets the nroCalle value for this AltaBeneficiario.
     * 
     * @param nroCalle
     */
    public void setNroCalle(java.lang.String nroCalle) {
        this.nroCalle = nroCalle;
    }


    /**
     * Gets the resto value for this AltaBeneficiario.
     * 
     * @return resto
     */
    public java.lang.String getResto() {
        return resto;
    }


    /**
     * Sets the resto value for this AltaBeneficiario.
     * 
     * @param resto
     */
    public void setResto(java.lang.String resto) {
        this.resto = resto;
    }


    /**
     * Gets the localidad value for this AltaBeneficiario.
     * 
     * @return localidad
     */
    public java.lang.String getLocalidad() {
        return localidad;
    }


    /**
     * Sets the localidad value for this AltaBeneficiario.
     * 
     * @param localidad
     */
    public void setLocalidad(java.lang.String localidad) {
        this.localidad = localidad;
    }


    /**
     * Gets the CP value for this AltaBeneficiario.
     * 
     * @return CP
     */
    public java.lang.String getCP() {
        return CP;
    }


    /**
     * Sets the CP value for this AltaBeneficiario.
     * 
     * @param CP
     */
    public void setCP(java.lang.String CP) {
        this.CP = CP;
    }


    /**
     * Gets the provincia value for this AltaBeneficiario.
     * 
     * @return provincia
     */
    public java.lang.String getProvincia() {
        return provincia;
    }


    /**
     * Sets the provincia value for this AltaBeneficiario.
     * 
     * @param provincia
     */
    public void setProvincia(java.lang.String provincia) {
        this.provincia = provincia;
    }


    /**
     * Gets the telefono value for this AltaBeneficiario.
     * 
     * @return telefono
     */
    public java.lang.String getTelefono() {
        return telefono;
    }


    /**
     * Sets the telefono value for this AltaBeneficiario.
     * 
     * @param telefono
     */
    public void setTelefono(java.lang.String telefono) {
        this.telefono = telefono;
    }


    /**
     * Gets the tipoDoc value for this AltaBeneficiario.
     * 
     * @return tipoDoc
     */
    public java.lang.String getTipoDoc() {
        return tipoDoc;
    }


    /**
     * Sets the tipoDoc value for this AltaBeneficiario.
     * 
     * @param tipoDoc
     */
    public void setTipoDoc(java.lang.String tipoDoc) {
        this.tipoDoc = tipoDoc;
    }


    /**
     * Gets the nroDoc value for this AltaBeneficiario.
     * 
     * @return nroDoc
     */
    public java.lang.String getNroDoc() {
        return nroDoc;
    }


    /**
     * Sets the nroDoc value for this AltaBeneficiario.
     * 
     * @param nroDoc
     */
    public void setNroDoc(java.lang.String nroDoc) {
        this.nroDoc = nroDoc;
    }


    /**
     * Gets the seccional value for this AltaBeneficiario.
     * 
     * @return seccional
     */
    public java.lang.String getSeccional() {
        return seccional;
    }


    /**
     * Sets the seccional value for this AltaBeneficiario.
     * 
     * @param seccional
     */
    public void setSeccional(java.lang.String seccional) {
        this.seccional = seccional;
    }


    /**
     * Gets the categoria value for this AltaBeneficiario.
     * 
     * @return categoria
     */
    public int getCategoria() {
        return categoria;
    }


    /**
     * Sets the categoria value for this AltaBeneficiario.
     * 
     * @param categoria
     */
    public void setCategoria(int categoria) {
        this.categoria = categoria;
    }


    /**
     * Gets the CUIL value for this AltaBeneficiario.
     * 
     * @return CUIL
     */
    public java.lang.String getCUIL() {
        return CUIL;
    }


    /**
     * Sets the CUIL value for this AltaBeneficiario.
     * 
     * @param CUIL
     */
    public void setCUIL(java.lang.String CUIL) {
        this.CUIL = CUIL;
    }

    public int getNacionalidad() {
		return nacionalidad;
	}

	public void setNacionalidad(int nacionalidad) {
		this.nacionalidad = nacionalidad;
	}
	public int getEstadoCivil() {
		return estadoCivil;
	}
	
	public void setEstadoCivil(int estadoCivil) {
		this.estadoCivil = estadoCivil;
	}
		
    /**
     * Gets the FPP value for this AltaBeneficiario.
     * 
     * @return FPP
     */
    public java.util.Calendar getFPP() {
        return FPP;
    }


    /**
     * Sets the FPP value for this AltaBeneficiario.
     * 
     * @param FPP
     */
    
    public void setFPP(java.util.Calendar FPP) {
        this.FPP = FPP;
    }

    /**
     * Gets the nroIntegrante value for this AltaBeneficiario.
     * 
     * @return nroIntegrante
     */
    public int getNroIntegrante() {
        return nroIntegrante;
    }


    /**
     * Sets the nroIntegrante value for this AltaBeneficiario.
     * 
     * @param nroIntegrante
     */
    public void setNroIntegrante(int nroIntegrante) {
        this.nroIntegrante = nroIntegrante;
    }
    
	public java.lang.String getDiscapacidad() {
		return discapacidad;
	}

	public void setDiscapacidad(java.lang.String discapacidad) {
		this.discapacidad = discapacidad;
	}




	// Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(Beneficiario.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName(namespace_uri, ">AltaBeneficiario"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("sessionID");
        elemField.setXmlName(new javax.xml.namespace.QName(namespace_uri, "SessionID"));
        elemField.setXmlType(new javax.xml.namespace.QName(namespace_schema, "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("CUILTitular");
        elemField.setXmlName(new javax.xml.namespace.QName(namespace_uri, "CUILTitular"));
        elemField.setXmlType(new javax.xml.namespace.QName(namespace_schema, "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("fecVig");
        elemField.setXmlName(new javax.xml.namespace.QName(namespace_uri, "FecVig"));
        elemField.setXmlType(new javax.xml.namespace.QName(namespace_schema, "dateTime"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("apellido");
        elemField.setXmlName(new javax.xml.namespace.QName(namespace_uri, "Apellido"));
        elemField.setXmlType(new javax.xml.namespace.QName(namespace_schema, "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("nombre");
        elemField.setXmlName(new javax.xml.namespace.QName(namespace_uri, "Nombre"));
        elemField.setXmlType(new javax.xml.namespace.QName(namespace_schema, "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("parentesco");
        elemField.setXmlName(new javax.xml.namespace.QName(namespace_uri, "Parentesco"));
        elemField.setXmlType(new javax.xml.namespace.QName(namespace_schema, "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("sexo");
        elemField.setXmlName(new javax.xml.namespace.QName(namespace_uri, "Sexo"));
        elemField.setXmlType(new javax.xml.namespace.QName(namespace_schema, "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("fecNac");
        elemField.setXmlName(new javax.xml.namespace.QName(namespace_uri, "FecNac"));
        elemField.setXmlType(new javax.xml.namespace.QName(namespace_schema, "dateTime"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("calle");
        elemField.setXmlName(new javax.xml.namespace.QName(namespace_uri, "Calle"));
        elemField.setXmlType(new javax.xml.namespace.QName(namespace_schema, "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("nroCalle");
        elemField.setXmlName(new javax.xml.namespace.QName(namespace_uri, "NroCalle"));
        elemField.setXmlType(new javax.xml.namespace.QName(namespace_schema, "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("resto");
        elemField.setXmlName(new javax.xml.namespace.QName(namespace_uri, "Resto"));
        elemField.setXmlType(new javax.xml.namespace.QName(namespace_schema, "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("localidad");
        elemField.setXmlName(new javax.xml.namespace.QName(namespace_uri, "Localidad"));
        elemField.setXmlType(new javax.xml.namespace.QName(namespace_schema, "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("CP");
        elemField.setXmlName(new javax.xml.namespace.QName(namespace_uri, "CP"));
        elemField.setXmlType(new javax.xml.namespace.QName(namespace_schema, "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("provincia");
        elemField.setXmlName(new javax.xml.namespace.QName(namespace_uri, "Provincia"));
        elemField.setXmlType(new javax.xml.namespace.QName(namespace_schema, "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("telefono");
        elemField.setXmlName(new javax.xml.namespace.QName(namespace_uri, "Telefono"));
        elemField.setXmlType(new javax.xml.namespace.QName(namespace_schema, "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("tipoDoc");
        elemField.setXmlName(new javax.xml.namespace.QName(namespace_uri, "TipoDoc"));
        elemField.setXmlType(new javax.xml.namespace.QName(namespace_schema, "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("nroDoc");
        elemField.setXmlName(new javax.xml.namespace.QName(namespace_uri, "NroDoc"));
        elemField.setXmlType(new javax.xml.namespace.QName(namespace_schema, "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("seccional");
        elemField.setXmlName(new javax.xml.namespace.QName(namespace_uri, "Seccional"));
        elemField.setXmlType(new javax.xml.namespace.QName(namespace_schema, "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("categoria");
        elemField.setXmlName(new javax.xml.namespace.QName(namespace_uri, "Categoria"));
        elemField.setXmlType(new javax.xml.namespace.QName(namespace_schema, "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("CUIL");
        elemField.setXmlName(new javax.xml.namespace.QName(namespace_uri, "CUIL"));
        elemField.setXmlType(new javax.xml.namespace.QName(namespace_schema, "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
//        elemField = new org.apache.axis.description.ElementDesc();
//        elemField.setFieldName("Nacionalidad");
//        elemField.setXmlName(new javax.xml.namespace.QName(namespace_uri, "Nacionalidad"));
//        elemField.setXmlType(new javax.xml.namespace.QName(namespace_schema, "int"));
//        elemField.setNillable(false);
//        typeDesc.addFieldDesc(elemField);
//      elemField = new org.apache.axis.description.ElementDesc();
//      elemField.setFieldName("EstadoCivil");
//      elemField.setXmlName(new javax.xml.namespace.QName(namespace_uri, "EstadoCivil"));
//      elemField.setXmlType(new javax.xml.namespace.QName(namespace_schema, "int"));
//      elemField.setNillable(false);
//      typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("FPP");
        elemField.setXmlName(new javax.xml.namespace.QName(namespace_uri, "FPP"));
        elemField.setXmlType(new javax.xml.namespace.QName(namespace_schema, "dateTime"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("nroIntegrante");
        elemField.setXmlName(new javax.xml.namespace.QName(namespace_uri, "NroIntegrante"));
        elemField.setXmlType(new javax.xml.namespace.QName(namespace_schema, "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
//      elemField = new org.apache.axis.description.ElementDesc();
//      elemField.setFieldName("Discapacidad");
//      elemField.setXmlName(new javax.xml.namespace.QName(namespace_uri, "Discapacidad"));
//      elemField.setXmlType(new javax.xml.namespace.QName(namespace_schema, "string"));
//      elemField.setNillable(false);
//      typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((CP == null) ? 0 : CP.hashCode());
		result = prime * result + ((CUIL == null) ? 0 : CUIL.hashCode());
		result = prime * result
				+ ((CUILTitular == null) ? 0 : CUILTitular.hashCode());
		result = prime * result + ((FPP == null) ? 0 : FPP.hashCode());
		result = prime * result
				+ ((apellido == null) ? 0 : apellido.hashCode());
		result = prime * result + ((calle == null) ? 0 : calle.hashCode());
		result = prime * result + categoria;
		result = prime * result
				+ ((discapacidad == null) ? 0 : discapacidad.hashCode());
		result = prime * result + estadoCivil;
		result = prime * result + ((fecNac == null) ? 0 : fecNac.hashCode());
		result = prime * result + ((fecVig == null) ? 0 : fecVig.hashCode());
		result = prime * result
				+ ((localidad == null) ? 0 : localidad.hashCode());
		result = prime * result + nacionalidad;
		result = prime * result + ((nombre == null) ? 0 : nombre.hashCode());
		result = prime * result
				+ ((nroCalle == null) ? 0 : nroCalle.hashCode());
		result = prime * result + ((nroDoc == null) ? 0 : nroDoc.hashCode());
		result = prime * result + nroIntegrante;
		result = prime * result
				+ ((parentesco == null) ? 0 : parentesco.hashCode());
		result = prime * result
				+ ((provincia == null) ? 0 : provincia.hashCode());
		result = prime * result + ((resto == null) ? 0 : resto.hashCode());
		result = prime * result
				+ ((seccional == null) ? 0 : seccional.hashCode());
		result = prime * result + ((sexo == null) ? 0 : sexo.hashCode());
		result = prime * result
				+ ((telefono == null) ? 0 : telefono.hashCode());
		result = prime * result + ((tipoDoc == null) ? 0 : tipoDoc.hashCode());
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
		Beneficiario other = (Beneficiario) obj;
		if (CP == null) {
			if (other.CP != null)
				return false;
		} else if (!CP.equals(other.CP))
			return false;
		if (CUIL == null) {
			if (other.CUIL != null)
				return false;
		} else if (!CUIL.equals(other.CUIL))
			return false;
		if (CUILTitular == null) {
			if (other.CUILTitular != null)
				return false;
		} else if (!CUILTitular.equals(other.CUILTitular))
			return false;
		if (FPP == null) {
			if (other.FPP != null)
				return false;
		} else if (!FPP.equals(other.FPP))
			return false;
		if (apellido == null) {
			if (other.apellido != null)
				return false;
		} else if (!apellido.equals(other.apellido))
			return false;
		if (calle == null) {
			if (other.calle != null)
				return false;
		} else if (!calle.equals(other.calle))
			return false;
		if (categoria != other.categoria)
			return false;
		if (discapacidad == null) {
			if (other.discapacidad != null)
				return false;
		} else if (!discapacidad.equals(other.discapacidad))
			return false;
		if (estadoCivil != other.estadoCivil)
			return false;
		if (fecNac == null) {
			if (other.fecNac != null)
				return false;
		} else if (!fecNac.equals(other.fecNac))
			return false;
		if (fecVig == null) {
			if (other.fecVig != null)
				return false;
		} else if (!fecVig.equals(other.fecVig))
			return false;
		if (localidad == null) {
			if (other.localidad != null)
				return false;
		} else if (!localidad.equals(other.localidad))
			return false;
		if (nacionalidad != other.nacionalidad)
			return false;
		if (nombre == null) {
			if (other.nombre != null)
				return false;
		} else if (!nombre.equals(other.nombre))
			return false;
		if (nroCalle == null) {
			if (other.nroCalle != null)
				return false;
		} else if (!nroCalle.equals(other.nroCalle))
			return false;
		if (nroDoc == null) {
			if (other.nroDoc != null)
				return false;
		} else if (!nroDoc.equals(other.nroDoc))
			return false;
		if (nroIntegrante != other.nroIntegrante)
			return false;
		if (parentesco == null) {
			if (other.parentesco != null)
				return false;
		} else if (!parentesco.equals(other.parentesco))
			return false;
		if (provincia == null) {
			if (other.provincia != null)
				return false;
		} else if (!provincia.equals(other.provincia))
			return false;
		if (resto == null) {
			if (other.resto != null)
				return false;
		} else if (!resto.equals(other.resto))
			return false;
		if (seccional == null) {
			if (other.seccional != null)
				return false;
		} else if (!seccional.equals(other.seccional))
			return false;
		if (sexo == null) {
			if (other.sexo != null)
				return false;
		} else if (!sexo.equals(other.sexo))
			return false;
		if (telefono == null) {
			if (other.telefono != null)
				return false;
		} else if (!telefono.equals(other.telefono))
			return false;
		if (tipoDoc == null) {
			if (other.tipoDoc != null)
				return false;
		} else if (!tipoDoc.equals(other.tipoDoc))
			return false;
		return true;
	}

	public java.util.Calendar getFecBaja() {
		return fecBaja;
	}

	public void setFecBaja(java.util.Calendar fecBaja) {
		this.fecBaja = fecBaja;
	}

	public Integer getInte() {
		return inte;
	}

	public void setInte(Integer inte) {
		this.inte = inte;
	}
	

}
