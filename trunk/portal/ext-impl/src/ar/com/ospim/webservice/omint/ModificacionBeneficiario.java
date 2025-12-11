/**
 * ModificacionBeneficiario.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package ar.com.ospim.webservice.omint;

import org.dom4j.Element;

public class ModificacionBeneficiario  implements java.io.Serializable {
    private java.lang.String sessionID;

    private int compania;

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

    private java.util.Calendar FPP;

    private int nacionalidad;

    private int estadoCivil;

    private java.lang.String discapacidad;

    public ModificacionBeneficiario() {
    }

    public ModificacionBeneficiario(
           java.lang.String sessionID,
           int compania,
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
           java.util.Calendar FPP,
           int nacionalidad,
           int estadoCivil,
           java.lang.String discapacidad) {
           this.sessionID = sessionID;
           this.compania = compania;
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
           this.FPP = FPP;
           this.nacionalidad = nacionalidad;
           this.estadoCivil = estadoCivil;
           this.discapacidad = discapacidad;
    }


    /**
     * Gets the sessionID value for this ModificacionBeneficiario.
     * 
     * @return sessionID
     */
    public java.lang.String getSessionID() {
        return sessionID;
    }


    /**
     * Sets the sessionID value for this ModificacionBeneficiario.
     * 
     * @param sessionID
     */
    public void setSessionID(java.lang.String sessionID) {
        this.sessionID = sessionID;
    }


    /**
     * Gets the compania value for this ModificacionBeneficiario.
     * 
     * @return compania
     */
    public int getCompania() {
        return compania;
    }


    /**
     * Sets the compania value for this ModificacionBeneficiario.
     * 
     * @param compania
     */
    public void setCompania(int compania) {
        this.compania = compania;
    }


    /**
     * Gets the CUILTitular value for this ModificacionBeneficiario.
     * 
     * @return CUILTitular
     */
    public java.lang.String getCUILTitular() {
        return CUILTitular;
    }


    /**
     * Sets the CUILTitular value for this ModificacionBeneficiario.
     * 
     * @param CUILTitular
     */
    public void setCUILTitular(java.lang.String CUILTitular) {
        this.CUILTitular = CUILTitular;
    }


    /**
     * Gets the fecVig value for this ModificacionBeneficiario.
     * 
     * @return fecVig
     */
    public java.util.Calendar getFecVig() {
        return fecVig;
    }


    /**
     * Sets the fecVig value for this ModificacionBeneficiario.
     * 
     * @param fecVig
     */
    public void setFecVig(java.util.Calendar fecVig) {
        this.fecVig = fecVig;
    }


    /**
     * Gets the apellido value for this ModificacionBeneficiario.
     * 
     * @return apellido
     */
    public java.lang.String getApellido() {
        return apellido;
    }


    /**
     * Sets the apellido value for this ModificacionBeneficiario.
     * 
     * @param apellido
     */
    public void setApellido(java.lang.String apellido) {
        this.apellido = apellido;
    }


    /**
     * Gets the nombre value for this ModificacionBeneficiario.
     * 
     * @return nombre
     */
    public java.lang.String getNombre() {
        return nombre;
    }


    /**
     * Sets the nombre value for this ModificacionBeneficiario.
     * 
     * @param nombre
     */
    public void setNombre(java.lang.String nombre) {
        this.nombre = nombre;
    }


    /**
     * Gets the parentesco value for this ModificacionBeneficiario.
     * 
     * @return parentesco
     */
    public java.lang.String getParentesco() {
        return parentesco;
    }


    /**
     * Sets the parentesco value for this ModificacionBeneficiario.
     * 
     * @param parentesco
     */
    public void setParentesco(java.lang.String parentesco) {
        this.parentesco = parentesco;
    }


    /**
     * Gets the sexo value for this ModificacionBeneficiario.
     * 
     * @return sexo
     */
    public java.lang.String getSexo() {
        return sexo;
    }


    /**
     * Sets the sexo value for this ModificacionBeneficiario.
     * 
     * @param sexo
     */
    public void setSexo(java.lang.String sexo) {
        this.sexo = sexo;
    }


    /**
     * Gets the fecNac value for this ModificacionBeneficiario.
     * 
     * @return fecNac
     */
    public java.util.Calendar getFecNac() {
        return fecNac;
    }


    /**
     * Sets the fecNac value for this ModificacionBeneficiario.
     * 
     * @param fecNac
     */
    public void setFecNac(java.util.Calendar fecNac) {
        this.fecNac = fecNac;
    }


    /**
     * Gets the calle value for this ModificacionBeneficiario.
     * 
     * @return calle
     */
    public java.lang.String getCalle() {
        return calle;
    }


    /**
     * Sets the calle value for this ModificacionBeneficiario.
     * 
     * @param calle
     */
    public void setCalle(java.lang.String calle) {
        this.calle = calle;
    }


    /**
     * Gets the nroCalle value for this ModificacionBeneficiario.
     * 
     * @return nroCalle
     */
    public java.lang.String getNroCalle() {
        return nroCalle;
    }


    /**
     * Sets the nroCalle value for this ModificacionBeneficiario.
     * 
     * @param nroCalle
     */
    public void setNroCalle(java.lang.String nroCalle) {
        this.nroCalle = nroCalle;
    }


    /**
     * Gets the resto value for this ModificacionBeneficiario.
     * 
     * @return resto
     */
    public java.lang.String getResto() {
        return resto;
    }


    /**
     * Sets the resto value for this ModificacionBeneficiario.
     * 
     * @param resto
     */
    public void setResto(java.lang.String resto) {
        this.resto = resto;
    }


    /**
     * Gets the localidad value for this ModificacionBeneficiario.
     * 
     * @return localidad
     */
    public java.lang.String getLocalidad() {
        return localidad;
    }


    /**
     * Sets the localidad value for this ModificacionBeneficiario.
     * 
     * @param localidad
     */
    public void setLocalidad(java.lang.String localidad) {
        this.localidad = localidad;
    }


    /**
     * Gets the CP value for this ModificacionBeneficiario.
     * 
     * @return CP
     */
    public java.lang.String getCP() {
        return CP;
    }


    /**
     * Sets the CP value for this ModificacionBeneficiario.
     * 
     * @param CP
     */
    public void setCP(java.lang.String CP) {
        this.CP = CP;
    }


    /**
     * Gets the provincia value for this ModificacionBeneficiario.
     * 
     * @return provincia
     */
    public java.lang.String getProvincia() {
        return provincia;
    }


    /**
     * Sets the provincia value for this ModificacionBeneficiario.
     * 
     * @param provincia
     */
    public void setProvincia(java.lang.String provincia) {
        this.provincia = provincia;
    }


    /**
     * Gets the telefono value for this ModificacionBeneficiario.
     * 
     * @return telefono
     */
    public java.lang.String getTelefono() {
        return telefono;
    }


    /**
     * Sets the telefono value for this ModificacionBeneficiario.
     * 
     * @param telefono
     */
    public void setTelefono(java.lang.String telefono) {
        this.telefono = telefono;
    }


    /**
     * Gets the tipoDoc value for this ModificacionBeneficiario.
     * 
     * @return tipoDoc
     */
    public java.lang.String getTipoDoc() {
        return tipoDoc;
    }


    /**
     * Sets the tipoDoc value for this ModificacionBeneficiario.
     * 
     * @param tipoDoc
     */
    public void setTipoDoc(java.lang.String tipoDoc) {
        this.tipoDoc = tipoDoc;
    }


    /**
     * Gets the nroDoc value for this ModificacionBeneficiario.
     * 
     * @return nroDoc
     */
    public java.lang.String getNroDoc() {
        return nroDoc;
    }


    /**
     * Sets the nroDoc value for this ModificacionBeneficiario.
     * 
     * @param nroDoc
     */
    public void setNroDoc(java.lang.String nroDoc) {
        this.nroDoc = nroDoc;
    }


    /**
     * Gets the seccional value for this ModificacionBeneficiario.
     * 
     * @return seccional
     */
    public java.lang.String getSeccional() {
        return seccional;
    }


    /**
     * Sets the seccional value for this ModificacionBeneficiario.
     * 
     * @param seccional
     */
    public void setSeccional(java.lang.String seccional) {
        this.seccional = seccional;
    }


    /**
     * Gets the categoria value for this ModificacionBeneficiario.
     * 
     * @return categoria
     */
    public int getCategoria() {
        return categoria;
    }


    /**
     * Sets the categoria value for this ModificacionBeneficiario.
     * 
     * @param categoria
     */
    public void setCategoria(int categoria) {
        this.categoria = categoria;
    }


    /**
     * Gets the CUIL value for this ModificacionBeneficiario.
     * 
     * @return CUIL
     */
    public java.lang.String getCUIL() {
        return CUIL;
    }


    /**
     * Sets the CUIL value for this ModificacionBeneficiario.
     * 
     * @param CUIL
     */
    public void setCUIL(java.lang.String CUIL) {
        this.CUIL = CUIL;
    }


    /**
     * Gets the FPP value for this ModificacionBeneficiario.
     * 
     * @return FPP
     */
    public java.util.Calendar getFPP() {
        return FPP;
    }


    /**
     * Sets the FPP value for this ModificacionBeneficiario.
     * 
     * @param FPP
     */
    public void setFPP(java.util.Calendar FPP) {
        this.FPP = FPP;
    }


    /**
     * Gets the nacionalidad value for this ModificacionBeneficiario.
     * 
     * @return nacionalidad
     */
    public int getNacionalidad() {
        return nacionalidad;
    }


    /**
     * Sets the nacionalidad value for this ModificacionBeneficiario.
     * 
     * @param nacionalidad
     */
    public void setNacionalidad(int nacionalidad) {
        this.nacionalidad = nacionalidad;
    }


    /**
     * Gets the estadoCivil value for this ModificacionBeneficiario.
     * 
     * @return estadoCivil
     */
    public int getEstadoCivil() {
        return estadoCivil;
    }


    /**
     * Sets the estadoCivil value for this ModificacionBeneficiario.
     * 
     * @param estadoCivil
     */
    public void setEstadoCivil(int estadoCivil) {
        this.estadoCivil = estadoCivil;
    }


    /**
     * Gets the discapacidad value for this ModificacionBeneficiario.
     * 
     * @return discapacidad
     */
    public java.lang.String getDiscapacidad() {
        return discapacidad;
    }


    /**
     * Sets the discapacidad value for this ModificacionBeneficiario.
     * 
     * @param discapacidad
     */
    public void setDiscapacidad(java.lang.String discapacidad) {
        this.discapacidad = discapacidad;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ModificacionBeneficiario)) return false;
        ModificacionBeneficiario other = (ModificacionBeneficiario) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.sessionID==null && other.getSessionID()==null) || 
             (this.sessionID!=null &&
              this.sessionID.equals(other.getSessionID()))) &&
            this.compania == other.getCompania() &&
            ((this.CUILTitular==null && other.getCUILTitular()==null) || 
             (this.CUILTitular!=null &&
              this.CUILTitular.equals(other.getCUILTitular()))) &&
            ((this.fecVig==null && other.getFecVig()==null) || 
             (this.fecVig!=null &&
              this.fecVig.equals(other.getFecVig()))) &&
            ((this.apellido==null && other.getApellido()==null) || 
             (this.apellido!=null &&
              this.apellido.equals(other.getApellido()))) &&
            ((this.nombre==null && other.getNombre()==null) || 
             (this.nombre!=null &&
              this.nombre.equals(other.getNombre()))) &&
            ((this.parentesco==null && other.getParentesco()==null) || 
             (this.parentesco!=null &&
              this.parentesco.equals(other.getParentesco()))) &&
            ((this.sexo==null && other.getSexo()==null) || 
             (this.sexo!=null &&
              this.sexo.equals(other.getSexo()))) &&
            ((this.fecNac==null && other.getFecNac()==null) || 
             (this.fecNac!=null &&
              this.fecNac.equals(other.getFecNac()))) &&
            ((this.calle==null && other.getCalle()==null) || 
             (this.calle!=null &&
              this.calle.equals(other.getCalle()))) &&
            ((this.nroCalle==null && other.getNroCalle()==null) || 
             (this.nroCalle!=null &&
              this.nroCalle.equals(other.getNroCalle()))) &&
            ((this.resto==null && other.getResto()==null) || 
             (this.resto!=null &&
              this.resto.equals(other.getResto()))) &&
            ((this.localidad==null && other.getLocalidad()==null) || 
             (this.localidad!=null &&
              this.localidad.equals(other.getLocalidad()))) &&
            ((this.CP==null && other.getCP()==null) || 
             (this.CP!=null &&
              this.CP.equals(other.getCP()))) &&
            ((this.provincia==null && other.getProvincia()==null) || 
             (this.provincia!=null &&
              this.provincia.equals(other.getProvincia()))) &&
            ((this.telefono==null && other.getTelefono()==null) || 
             (this.telefono!=null &&
              this.telefono.equals(other.getTelefono()))) &&
            ((this.tipoDoc==null && other.getTipoDoc()==null) || 
             (this.tipoDoc!=null &&
              this.tipoDoc.equals(other.getTipoDoc()))) &&
            ((this.nroDoc==null && other.getNroDoc()==null) || 
             (this.nroDoc!=null &&
              this.nroDoc.equals(other.getNroDoc()))) &&
            ((this.seccional==null && other.getSeccional()==null) || 
             (this.seccional!=null &&
              this.seccional.equals(other.getSeccional()))) &&
            this.categoria == other.getCategoria() &&
            ((this.CUIL==null && other.getCUIL()==null) || 
             (this.CUIL!=null &&
              this.CUIL.equals(other.getCUIL()))) &&
            ((this.FPP==null && other.getFPP()==null) || 
             (this.FPP!=null &&
              this.FPP.equals(other.getFPP()))) &&
            this.nacionalidad == other.getNacionalidad() &&
            this.estadoCivil == other.getEstadoCivil() &&
            ((this.discapacidad==null && other.getDiscapacidad()==null) || 
             (this.discapacidad!=null &&
              this.discapacidad.equals(other.getDiscapacidad())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getSessionID() != null) {
            _hashCode += getSessionID().hashCode();
        }
        _hashCode += getCompania();
        if (getCUILTitular() != null) {
            _hashCode += getCUILTitular().hashCode();
        }
        if (getFecVig() != null) {
            _hashCode += getFecVig().hashCode();
        }
        if (getApellido() != null) {
            _hashCode += getApellido().hashCode();
        }
        if (getNombre() != null) {
            _hashCode += getNombre().hashCode();
        }
        if (getParentesco() != null) {
            _hashCode += getParentesco().hashCode();
        }
        if (getSexo() != null) {
            _hashCode += getSexo().hashCode();
        }
        if (getFecNac() != null) {
            _hashCode += getFecNac().hashCode();
        }
        if (getCalle() != null) {
            _hashCode += getCalle().hashCode();
        }
        if (getNroCalle() != null) {
            _hashCode += getNroCalle().hashCode();
        }
        if (getResto() != null) {
            _hashCode += getResto().hashCode();
        }
        if (getLocalidad() != null) {
            _hashCode += getLocalidad().hashCode();
        }
        if (getCP() != null) {
            _hashCode += getCP().hashCode();
        }
        if (getProvincia() != null) {
            _hashCode += getProvincia().hashCode();
        }
        if (getTelefono() != null) {
            _hashCode += getTelefono().hashCode();
        }
        if (getTipoDoc() != null) {
            _hashCode += getTipoDoc().hashCode();
        }
        if (getNroDoc() != null) {
            _hashCode += getNroDoc().hashCode();
        }
        if (getSeccional() != null) {
            _hashCode += getSeccional().hashCode();
        }
        _hashCode += getCategoria();
        if (getCUIL() != null) {
            _hashCode += getCUIL().hashCode();
        }
        if (getFPP() != null) {
            _hashCode += getFPP().hashCode();
        }
        _hashCode += getNacionalidad();
        _hashCode += getEstadoCivil();
        if (getDiscapacidad() != null) {
            _hashCode += getDiscapacidad().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

//    // Type metadata
//    private static org.apache.axis.description.TypeDesc typeDesc =
//        new org.apache.axis.description.TypeDesc(ModificacionBeneficiario.class, true);
//
//    static {
//        typeDesc.setXmlType(new javax.xml.namespace.QName("http://tempuri.org/", ">ModificacionBeneficiario"));
//        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
//        elemField.setFieldName("sessionID");
//        elemField.setXmlName(new javax.xml.namespace.QName("http://tempuri.org/", "SessionID"));
//        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
//        elemField.setMinOccurs(0);
//        elemField.setNillable(false);
//        typeDesc.addFieldDesc(elemField);
//        elemField = new org.apache.axis.description.ElementDesc();
//        elemField.setFieldName("compania");
//        elemField.setXmlName(new javax.xml.namespace.QName("http://tempuri.org/", "Compania"));
//        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
//        elemField.setNillable(false);
//        typeDesc.addFieldDesc(elemField);
//        elemField = new org.apache.axis.description.ElementDesc();
//        elemField.setFieldName("CUILTitular");
//        elemField.setXmlName(new javax.xml.namespace.QName("http://tempuri.org/", "CUILTitular"));
//        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
//        elemField.setMinOccurs(0);
//        elemField.setNillable(false);
//        typeDesc.addFieldDesc(elemField);
//        elemField = new org.apache.axis.description.ElementDesc();
//        elemField.setFieldName("fecVig");
//        elemField.setXmlName(new javax.xml.namespace.QName("http://tempuri.org/", "FecVig"));
//        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
//        elemField.setNillable(false);
//        typeDesc.addFieldDesc(elemField);
//        elemField = new org.apache.axis.description.ElementDesc();
//        elemField.setFieldName("apellido");
//        elemField.setXmlName(new javax.xml.namespace.QName("http://tempuri.org/", "Apellido"));
//        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
//        elemField.setMinOccurs(0);
//        elemField.setNillable(false);
//        typeDesc.addFieldDesc(elemField);
//        elemField = new org.apache.axis.description.ElementDesc();
//        elemField.setFieldName("nombre");
//        elemField.setXmlName(new javax.xml.namespace.QName("http://tempuri.org/", "Nombre"));
//        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
//        elemField.setMinOccurs(0);
//        elemField.setNillable(false);
//        typeDesc.addFieldDesc(elemField);
//        elemField = new org.apache.axis.description.ElementDesc();
//        elemField.setFieldName("parentesco");
//        elemField.setXmlName(new javax.xml.namespace.QName("http://tempuri.org/", "Parentesco"));
//        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
//        elemField.setMinOccurs(0);
//        elemField.setNillable(false);
//        typeDesc.addFieldDesc(elemField);
//        elemField = new org.apache.axis.description.ElementDesc();
//        elemField.setFieldName("sexo");
//        elemField.setXmlName(new javax.xml.namespace.QName("http://tempuri.org/", "Sexo"));
//        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
//        elemField.setMinOccurs(0);
//        elemField.setNillable(false);
//        typeDesc.addFieldDesc(elemField);
//        elemField = new org.apache.axis.description.ElementDesc();
//        elemField.setFieldName("fecNac");
//        elemField.setXmlName(new javax.xml.namespace.QName("http://tempuri.org/", "FecNac"));
//        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
//        elemField.setNillable(false);
//        typeDesc.addFieldDesc(elemField);
//        elemField = new org.apache.axis.description.ElementDesc();
//        elemField.setFieldName("calle");
//        elemField.setXmlName(new javax.xml.namespace.QName("http://tempuri.org/", "Calle"));
//        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
//        elemField.setMinOccurs(0);
//        elemField.setNillable(false);
//        typeDesc.addFieldDesc(elemField);
//        elemField = new org.apache.axis.description.ElementDesc();
//        elemField.setFieldName("nroCalle");
//        elemField.setXmlName(new javax.xml.namespace.QName("http://tempuri.org/", "NroCalle"));
//        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
//        elemField.setMinOccurs(0);
//        elemField.setNillable(false);
//        typeDesc.addFieldDesc(elemField);
//        elemField = new org.apache.axis.description.ElementDesc();
//        elemField.setFieldName("resto");
//        elemField.setXmlName(new javax.xml.namespace.QName("http://tempuri.org/", "Resto"));
//        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
//        elemField.setMinOccurs(0);
//        elemField.setNillable(false);
//        typeDesc.addFieldDesc(elemField);
//        elemField = new org.apache.axis.description.ElementDesc();
//        elemField.setFieldName("localidad");
//        elemField.setXmlName(new javax.xml.namespace.QName("http://tempuri.org/", "Localidad"));
//        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
//        elemField.setMinOccurs(0);
//        elemField.setNillable(false);
//        typeDesc.addFieldDesc(elemField);
//        elemField = new org.apache.axis.description.ElementDesc();
//        elemField.setFieldName("CP");
//        elemField.setXmlName(new javax.xml.namespace.QName("http://tempuri.org/", "CP"));
//        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
//        elemField.setMinOccurs(0);
//        elemField.setNillable(false);
//        typeDesc.addFieldDesc(elemField);
//        elemField = new org.apache.axis.description.ElementDesc();
//        elemField.setFieldName("provincia");
//        elemField.setXmlName(new javax.xml.namespace.QName("http://tempuri.org/", "Provincia"));
//        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
//        elemField.setMinOccurs(0);
//        elemField.setNillable(false);
//        typeDesc.addFieldDesc(elemField);
//        elemField = new org.apache.axis.description.ElementDesc();
//        elemField.setFieldName("telefono");
//        elemField.setXmlName(new javax.xml.namespace.QName("http://tempuri.org/", "Telefono"));
//        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
//        elemField.setMinOccurs(0);
//        elemField.setNillable(false);
//        typeDesc.addFieldDesc(elemField);
//        elemField = new org.apache.axis.description.ElementDesc();
//        elemField.setFieldName("tipoDoc");
//        elemField.setXmlName(new javax.xml.namespace.QName("http://tempuri.org/", "TipoDoc"));
//        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
//        elemField.setMinOccurs(0);
//        elemField.setNillable(false);
//        typeDesc.addFieldDesc(elemField);
//        elemField = new org.apache.axis.description.ElementDesc();
//        elemField.setFieldName("nroDoc");
//        elemField.setXmlName(new javax.xml.namespace.QName("http://tempuri.org/", "NroDoc"));
//        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
//        elemField.setMinOccurs(0);
//        elemField.setNillable(false);
//        typeDesc.addFieldDesc(elemField);
//        elemField = new org.apache.axis.description.ElementDesc();
//        elemField.setFieldName("seccional");
//        elemField.setXmlName(new javax.xml.namespace.QName("http://tempuri.org/", "Seccional"));
//        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
//        elemField.setMinOccurs(0);
//        elemField.setNillable(false);
//        typeDesc.addFieldDesc(elemField);
//        elemField = new org.apache.axis.description.ElementDesc();
//        elemField.setFieldName("categoria");
//        elemField.setXmlName(new javax.xml.namespace.QName("http://tempuri.org/", "Categoria"));
//        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
//        elemField.setNillable(false);
//        typeDesc.addFieldDesc(elemField);
//        elemField = new org.apache.axis.description.ElementDesc();
//        elemField.setFieldName("CUIL");
//        elemField.setXmlName(new javax.xml.namespace.QName("http://tempuri.org/", "CUIL"));
//        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
//        elemField.setMinOccurs(0);
//        elemField.setNillable(false);
//        typeDesc.addFieldDesc(elemField);
//        elemField = new org.apache.axis.description.ElementDesc();
//        elemField.setFieldName("FPP");
//        elemField.setXmlName(new javax.xml.namespace.QName("http://tempuri.org/", "FPP"));
//        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
////        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
//        elemField.setNillable(true);
//        typeDesc.addFieldDesc(elemField);
//        elemField = new org.apache.axis.description.ElementDesc();
//        elemField.setFieldName("nacionalidad");
//        elemField.setXmlName(new javax.xml.namespace.QName("http://tempuri.org/", "Nacionalidad"));
//        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
//        elemField.setNillable(false);
//        typeDesc.addFieldDesc(elemField);
//        elemField = new org.apache.axis.description.ElementDesc();
//        elemField.setFieldName("estadoCivil");
//        elemField.setXmlName(new javax.xml.namespace.QName("http://tempuri.org/", "EstadoCivil"));
//        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
//        elemField.setNillable(false);
//        typeDesc.addFieldDesc(elemField);
//        elemField = new org.apache.axis.description.ElementDesc();
//        elemField.setFieldName("discapacidad");
//        elemField.setXmlName(new javax.xml.namespace.QName("http://tempuri.org/", "Discapacidad"));
//        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
//        elemField.setMinOccurs(0);
//        elemField.setNillable(false);
//        typeDesc.addFieldDesc(elemField);
//    }
//
//    /**
//     * Return type metadata object
//     */
//    public static org.apache.axis.description.TypeDesc getTypeDesc() {
//        return typeDesc;
//    }
//
//    /**
//     * Get Custom Serializer
//     */
//    public static org.apache.axis.encoding.Serializer getSerializer(
//           java.lang.String mechType, 
//           java.lang.Class _javaType,  
//           javax.xml.namespace.QName _xmlType) {
//        return 
//          new  org.apache.axis.encoding.ser.BeanSerializer(
//            _javaType, _xmlType, typeDesc);
//    }
//
//    /**
//     * Get Custom Deserializer
//     */
//    public static org.apache.axis.encoding.Deserializer getDeserializer(
//           java.lang.String mechType, 
//           java.lang.Class _javaType,  
//           javax.xml.namespace.QName _xmlType) {
//        return 
//          new  org.apache.axis.encoding.ser.BeanDeserializer(
//            _javaType, _xmlType, typeDesc);
//    }

}
