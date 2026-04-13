/**
 * FacturaIngresoWS.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package ar.com.ospim.webservice.hoteles;

public class FacturaIngresoWS  extends IngresoWS  implements java.io.Serializable {
    public FacturaIngresoWS() {
    }

    public FacturaIngresoWS(
           java.lang.Integer bancoId,
           java.lang.Integer chequeEstado,
           java.lang.Integer cuentaBancariaId,
           java.util.Calendar fecha,
           java.lang.Double importe,
           java.lang.String operacionNro,
           java.lang.String sucursal,
           java.lang.Integer tarjetaCuotas,
           java.lang.Integer tarjetaEmisor,
           java.lang.String tipoIngreso,
           java.lang.Integer transferenciaTipo,
           java.lang.String usuario) {
        super(
            bancoId,
            chequeEstado,
            cuentaBancariaId,
            fecha,
            importe,
            operacionNro,
            sucursal,
            tarjetaCuotas,
            tarjetaEmisor,
            tipoIngreso,
            transferenciaTipo,
            usuario);
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof FacturaIngresoWS)) return false;
        FacturaIngresoWS other = (FacturaIngresoWS) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj);
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = super.hashCode();
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(FacturaIngresoWS.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://webservice.transferenciahoteles", "FacturaIngresoWS"));
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
