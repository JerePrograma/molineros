/**
 * BajaGrupoFamiliarResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package ar.com.ospim.webservice.omint;

public class BajaGrupoFamiliarResponse  implements java.io.Serializable {
    private BajaGrupoFamiliarResponseBajaGrupoFamiliarResult bajaGrupoFamiliarResult;

    public BajaGrupoFamiliarResponse() {
    }

    public BajaGrupoFamiliarResponse(
           BajaGrupoFamiliarResponseBajaGrupoFamiliarResult bajaGrupoFamiliarResult) {
           this.bajaGrupoFamiliarResult = bajaGrupoFamiliarResult;
    }


    /**
     * Gets the bajaGrupoFamiliarResult value for this BajaGrupoFamiliarResponse.
     * 
     * @return bajaGrupoFamiliarResult
     */
    public BajaGrupoFamiliarResponseBajaGrupoFamiliarResult getBajaGrupoFamiliarResult() {
        return bajaGrupoFamiliarResult;
    }


    /**
     * Sets the bajaGrupoFamiliarResult value for this BajaGrupoFamiliarResponse.
     * 
     * @param bajaGrupoFamiliarResult
     */
    public void setBajaGrupoFamiliarResult(BajaGrupoFamiliarResponseBajaGrupoFamiliarResult bajaGrupoFamiliarResult) {
        this.bajaGrupoFamiliarResult = bajaGrupoFamiliarResult;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof BajaGrupoFamiliarResponse)) return false;
        BajaGrupoFamiliarResponse other = (BajaGrupoFamiliarResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.bajaGrupoFamiliarResult==null && other.getBajaGrupoFamiliarResult()==null) || 
             (this.bajaGrupoFamiliarResult!=null &&
              this.bajaGrupoFamiliarResult.equals(other.getBajaGrupoFamiliarResult())));
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
        if (getBajaGrupoFamiliarResult() != null) {
            _hashCode += getBajaGrupoFamiliarResult().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(BajaGrupoFamiliarResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://tempuri.org/", ">BajaGrupoFamiliarResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("bajaGrupoFamiliarResult");
        elemField.setXmlName(new javax.xml.namespace.QName("http://tempuri.org/", "BajaGrupoFamiliarResult"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://tempuri.org/", ">>BajaGrupoFamiliarResponse>BajaGrupoFamiliarResult"));
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
