/**
 * AltaBeneficiarioResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package ar.com.ospim.webservice.omint;

public class AltaBeneficiarioResponse  implements java.io.Serializable {
    private AltaBeneficiarioResponseAltaBeneficiarioResult altaBeneficiarioResult;

    public AltaBeneficiarioResponse() {
    }

    public AltaBeneficiarioResponse(
           AltaBeneficiarioResponseAltaBeneficiarioResult altaBeneficiarioResult) {
           this.altaBeneficiarioResult = altaBeneficiarioResult;
    }


    /**
     * Gets the altaBeneficiarioResult value for this AltaBeneficiarioResponse.
     * 
     * @return altaBeneficiarioResult
     */
    public AltaBeneficiarioResponseAltaBeneficiarioResult getAltaBeneficiarioResult() {
        return altaBeneficiarioResult;
    }


    /**
     * Sets the altaBeneficiarioResult value for this AltaBeneficiarioResponse.
     * 
     * @param altaBeneficiarioResult
     */
    public void setAltaBeneficiarioResult(AltaBeneficiarioResponseAltaBeneficiarioResult altaBeneficiarioResult) {
        this.altaBeneficiarioResult = altaBeneficiarioResult;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof AltaBeneficiarioResponse)) return false;
        AltaBeneficiarioResponse other = (AltaBeneficiarioResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.altaBeneficiarioResult==null && other.getAltaBeneficiarioResult()==null) || 
             (this.altaBeneficiarioResult!=null &&
              this.altaBeneficiarioResult.equals(other.getAltaBeneficiarioResult())));
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
        if (getAltaBeneficiarioResult() != null) {
            _hashCode += getAltaBeneficiarioResult().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(AltaBeneficiarioResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://tempuri.org/", ">AltaBeneficiarioResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("altaBeneficiarioResult");
        elemField.setXmlName(new javax.xml.namespace.QName("http://tempuri.org/", "AltaBeneficiarioResult"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://tempuri.org/", ">>AltaBeneficiarioResponse>AltaBeneficiarioResult"));
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
