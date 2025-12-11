/**
 * AltaGrupoFamiliarResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package ar.com.ospim.webservice.omint;

public class AltaGrupoFamiliarResponse  implements java.io.Serializable {
    private AltaGrupoFamiliarResponseAltaGrupoFamiliarResult altaGrupoFamiliarResult;

    public AltaGrupoFamiliarResponse() {
    }

    public AltaGrupoFamiliarResponse(
           AltaGrupoFamiliarResponseAltaGrupoFamiliarResult altaGrupoFamiliarResult) {
           this.altaGrupoFamiliarResult = altaGrupoFamiliarResult;
    }


    /**
     * Gets the altaGrupoFamiliarResult value for this AltaGrupoFamiliarResponse.
     * 
     * @return altaGrupoFamiliarResult
     */
    public AltaGrupoFamiliarResponseAltaGrupoFamiliarResult getAltaGrupoFamiliarResult() {
        return altaGrupoFamiliarResult;
    }


    /**
     * Sets the altaGrupoFamiliarResult value for this AltaGrupoFamiliarResponse.
     * 
     * @param altaGrupoFamiliarResult
     */
    public void setAltaGrupoFamiliarResult(AltaGrupoFamiliarResponseAltaGrupoFamiliarResult altaGrupoFamiliarResult) {
        this.altaGrupoFamiliarResult = altaGrupoFamiliarResult;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof AltaGrupoFamiliarResponse)) return false;
        AltaGrupoFamiliarResponse other = (AltaGrupoFamiliarResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.altaGrupoFamiliarResult==null && other.getAltaGrupoFamiliarResult()==null) || 
             (this.altaGrupoFamiliarResult!=null &&
              this.altaGrupoFamiliarResult.equals(other.getAltaGrupoFamiliarResult())));
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
        if (getAltaGrupoFamiliarResult() != null) {
            _hashCode += getAltaGrupoFamiliarResult().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(AltaGrupoFamiliarResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://tempuri.org/", ">AltaGrupoFamiliarResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("altaGrupoFamiliarResult");
        elemField.setXmlName(new javax.xml.namespace.QName("http://tempuri.org/", "AltaGrupoFamiliarResult"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://tempuri.org/", ">>AltaGrupoFamiliarResponse>AltaGrupoFamiliarResult"));
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
