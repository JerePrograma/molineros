/**
 * SubscriberData.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package ar.com.global.webservices.agnitas_webservice;

public class SubscriberData  implements java.io.Serializable {
    private java.lang.String[] paramNames;

    private java.lang.String[] paramValues;

    private int customerID;

    public SubscriberData() {
    }

    public SubscriberData(
           java.lang.String[] paramNames,
           java.lang.String[] paramValues,
           int customerID) {
           this.paramNames = paramNames;
           this.paramValues = paramValues;
           this.customerID = customerID;
    }


    /**
     * Gets the paramNames value for this SubscriberData.
     * 
     * @return paramNames
     */
    public java.lang.String[] getParamNames() {
        return paramNames;
    }


    /**
     * Sets the paramNames value for this SubscriberData.
     * 
     * @param paramNames
     */
    public void setParamNames(java.lang.String[] paramNames) {
        this.paramNames = paramNames;
    }


    /**
     * Gets the paramValues value for this SubscriberData.
     * 
     * @return paramValues
     */
    public java.lang.String[] getParamValues() {
        return paramValues;
    }


    /**
     * Sets the paramValues value for this SubscriberData.
     * 
     * @param paramValues
     */
    public void setParamValues(java.lang.String[] paramValues) {
        this.paramValues = paramValues;
    }


    /**
     * Gets the customerID value for this SubscriberData.
     * 
     * @return customerID
     */
    public int getCustomerID() {
        return customerID;
    }


    /**
     * Sets the customerID value for this SubscriberData.
     * 
     * @param customerID
     */
    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof SubscriberData)) return false;
        SubscriberData other = (SubscriberData) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.paramNames==null && other.getParamNames()==null) || 
             (this.paramNames!=null &&
              java.util.Arrays.equals(this.paramNames, other.getParamNames()))) &&
            ((this.paramValues==null && other.getParamValues()==null) || 
             (this.paramValues!=null &&
              java.util.Arrays.equals(this.paramValues, other.getParamValues()))) &&
            this.customerID == other.getCustomerID();
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
        if (getParamNames() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getParamNames());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getParamNames(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getParamValues() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getParamValues());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getParamValues(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        _hashCode += getCustomerID();
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(SubscriberData.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:agnitas-webservice", "SubscriberData"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("paramNames");
        elemField.setXmlName(new javax.xml.namespace.QName("", "paramNames"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("", "x"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("paramValues");
        elemField.setXmlName(new javax.xml.namespace.QName("", "paramValues"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("", "x"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("customerID");
        elemField.setXmlName(new javax.xml.namespace.QName("", "customerID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
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
