/**
 * BajaGrupoFamiliar.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package ar.com.ospim.webservice.omint;

public class BajaGrupoFamiliar  implements java.io.Serializable {
    private java.lang.String sessionID;

    private int compania;

    private java.lang.String CUILTitular;

    private java.util.Calendar fecVig;

    public BajaGrupoFamiliar() {
    }

    public BajaGrupoFamiliar(
           java.lang.String sessionID,
           int compania,
           java.lang.String CUILTitular,
           java.util.Calendar fecVig) {
           this.sessionID = sessionID;
           this.compania = compania;
           this.CUILTitular = CUILTitular;
           this.fecVig = fecVig;
    }


    /**
     * Gets the sessionID value for this BajaGrupoFamiliar.
     * 
     * @return sessionID
     */
    public java.lang.String getSessionID() {
        return sessionID;
    }


    /**
     * Sets the sessionID value for this BajaGrupoFamiliar.
     * 
     * @param sessionID
     */
    public void setSessionID(java.lang.String sessionID) {
        this.sessionID = sessionID;
    }


    /**
     * Gets the compania value for this BajaGrupoFamiliar.
     * 
     * @return compania
     */
    public int getCompania() {
        return compania;
    }


    /**
     * Sets the compania value for this BajaGrupoFamiliar.
     * 
     * @param compania
     */
    public void setCompania(int compania) {
        this.compania = compania;
    }


    /**
     * Gets the CUILTitular value for this BajaGrupoFamiliar.
     * 
     * @return CUILTitular
     */
    public java.lang.String getCUILTitular() {
        return CUILTitular;
    }


    /**
     * Sets the CUILTitular value for this BajaGrupoFamiliar.
     * 
     * @param CUILTitular
     */
    public void setCUILTitular(java.lang.String CUILTitular) {
        this.CUILTitular = CUILTitular;
    }


    /**
     * Gets the fecVig value for this BajaGrupoFamiliar.
     * 
     * @return fecVig
     */
    public java.util.Calendar getFecVig() {
        return fecVig;
    }


    /**
     * Sets the fecVig value for this BajaGrupoFamiliar.
     * 
     * @param fecVig
     */
    public void setFecVig(java.util.Calendar fecVig) {
        this.fecVig = fecVig;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof BajaGrupoFamiliar)) return false;
        BajaGrupoFamiliar other = (BajaGrupoFamiliar) obj;
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
              this.fecVig.equals(other.getFecVig())));
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
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(BajaGrupoFamiliar.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://tempuri.org/", ">BajaGrupoFamiliar"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("sessionID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://tempuri.org/", "SessionID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("compania");
        elemField.setXmlName(new javax.xml.namespace.QName("http://tempuri.org/", "Compania"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("CUILTitular");
        elemField.setXmlName(new javax.xml.namespace.QName("http://tempuri.org/", "CUILTitular"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("fecVig");
        elemField.setXmlName(new javax.xml.namespace.QName("http://tempuri.org/", "FecVig"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
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

}
