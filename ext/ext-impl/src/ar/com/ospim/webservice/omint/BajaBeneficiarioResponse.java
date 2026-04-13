/**
 * BajaBeneficiarioResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package ar.com.ospim.webservice.omint;

public class BajaBeneficiarioResponse  implements java.io.Serializable {
    private BajaBeneficiarioResponseBajaBeneficiarioResult bajaBeneficiarioResult;

    public BajaBeneficiarioResponse() {
    }

    public BajaBeneficiarioResponse(
           BajaBeneficiarioResponseBajaBeneficiarioResult bajaBeneficiarioResult) {
           this.bajaBeneficiarioResult = bajaBeneficiarioResult;
    }


    /**
     * Gets the bajaBeneficiarioResult value for this BajaBeneficiarioResponse.
     * 
     * @return bajaBeneficiarioResult
     */
    public BajaBeneficiarioResponseBajaBeneficiarioResult getBajaBeneficiarioResult() {
        return bajaBeneficiarioResult;
    }


    /**
     * Sets the bajaBeneficiarioResult value for this BajaBeneficiarioResponse.
     * 
     * @param bajaBeneficiarioResult
     */
    public void setBajaBeneficiarioResult(BajaBeneficiarioResponseBajaBeneficiarioResult bajaBeneficiarioResult) {
        this.bajaBeneficiarioResult = bajaBeneficiarioResult;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof BajaBeneficiarioResponse)) return false;
        BajaBeneficiarioResponse other = (BajaBeneficiarioResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.bajaBeneficiarioResult==null && other.getBajaBeneficiarioResult()==null) || 
             (this.bajaBeneficiarioResult!=null &&
              this.bajaBeneficiarioResult.equals(other.getBajaBeneficiarioResult())));
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
        if (getBajaBeneficiarioResult() != null) {
            _hashCode += getBajaBeneficiarioResult().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(BajaBeneficiarioResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://tempuri.org/", ">BajaBeneficiarioResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("bajaBeneficiarioResult");
        elemField.setXmlName(new javax.xml.namespace.QName("http://tempuri.org/", "BajaBeneficiarioResult"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://tempuri.org/", ">>BajaBeneficiarioResponse>BajaBeneficiarioResult"));
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
