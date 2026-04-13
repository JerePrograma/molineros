/**
 * GetCambioPlanResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package ar.com.ospim.webservice.omint;

public class GetCambioPlanResponse  implements java.io.Serializable {
    private GetCambioPlanResponseGetCambioPlanResult getCambioPlanResult;

    public GetCambioPlanResponse() {
    }

    public GetCambioPlanResponse(
           GetCambioPlanResponseGetCambioPlanResult getCambioPlanResult) {
           this.getCambioPlanResult = getCambioPlanResult;
    }


    /**
     * Gets the getCambioPlanResult value for this GetCambioPlanResponse.
     * 
     * @return getCambioPlanResult
     */
    public GetCambioPlanResponseGetCambioPlanResult getGetCambioPlanResult() {
        return getCambioPlanResult;
    }


    /**
     * Sets the getCambioPlanResult value for this GetCambioPlanResponse.
     * 
     * @param getCambioPlanResult
     */
    public void setGetCambioPlanResult(GetCambioPlanResponseGetCambioPlanResult getCambioPlanResult) {
        this.getCambioPlanResult = getCambioPlanResult;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof GetCambioPlanResponse)) return false;
        GetCambioPlanResponse other = (GetCambioPlanResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.getCambioPlanResult==null && other.getGetCambioPlanResult()==null) || 
             (this.getCambioPlanResult!=null &&
              this.getCambioPlanResult.equals(other.getGetCambioPlanResult())));
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
        if (getGetCambioPlanResult() != null) {
            _hashCode += getGetCambioPlanResult().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(GetCambioPlanResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://tempuri.org/", ">GetCambioPlanResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("getCambioPlanResult");
        elemField.setXmlName(new javax.xml.namespace.QName("http://tempuri.org/", "GetCambioPlanResult"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://tempuri.org/", ">>GetCambioPlanResponse>GetCambioPlanResult"));
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
