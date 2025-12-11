/**
 * AltaGrupoFamiliar.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package ar.com.ospim.webservice.omint;

public class AltaGrupoFamiliar  implements java.io.Serializable {
    
	private java.lang.String sessionID;

    private AltaGrupoFamiliarTransactionData transactionData;

    public AltaGrupoFamiliar() {
    }

    public AltaGrupoFamiliar(
           java.lang.String sessionID,
           AltaGrupoFamiliarTransactionData transactionData) {
           this.sessionID = sessionID;
           this.transactionData = transactionData;
    }


    /**
     * Gets the sessionID value for this AltaGrupoFamiliar.
     * 
     * @return sessionID
     */
    public java.lang.String getSessionID() {
        return sessionID;
    }


    /**
     * Sets the sessionID value for this AltaGrupoFamiliar.
     * 
     * @param sessionID
     */
    public void setSessionID(java.lang.String sessionID) {
        this.sessionID = sessionID;
    }


    /**
     * Gets the transactionData value for this AltaGrupoFamiliar.
     * 
     * @return transactionData
     */
    public AltaGrupoFamiliarTransactionData getTransactionData() {
        return transactionData;
    }


    /**
     * Sets the transactionData value for this AltaGrupoFamiliar.
     * 
     * @param transactionData
     */
    public void setTransactionData(AltaGrupoFamiliarTransactionData transactionData) {
        this.transactionData = transactionData;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof AltaGrupoFamiliar)) return false;
        AltaGrupoFamiliar other = (AltaGrupoFamiliar) obj;
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
            ((this.transactionData==null && other.getTransactionData()==null) || 
             (this.transactionData!=null &&
              this.transactionData.equals(other.getTransactionData())));
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
        if (getTransactionData() != null) {
            _hashCode += getTransactionData().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(AltaGrupoFamiliar.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://tempuri.org/", ">AltaGrupoFamiliar"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("sessionID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://tempuri.org/", "SessionID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("transactionData");
        elemField.setXmlName(new javax.xml.namespace.QName("http://tempuri.org/", "TransactionData"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://tempuri.org/", ">>AltaGrupoFamiliar>TransactionData"));
        elemField.setMinOccurs(0);
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
