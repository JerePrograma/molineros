/**
 * ReciboHotelWS.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package ar.com.ospim.webservice.hoteles;

public class ReciboHotelWS  implements java.io.Serializable {
    private java.lang.String clienteDocumento;

    private java.lang.Integer clienteId;

    private java.lang.String clienteNombre;

    private java.lang.Integer comprobanteAnio;

    private java.lang.String comprobanteLetra;

    private java.lang.String comprobanteNumero;

    private java.lang.String comprobanteSucursal;

    private java.lang.String comprobanteTipo;

    private java.lang.String descripcion;

    private java.lang.String error;

    private java.util.Calendar fecha;

    private java.util.Calendar fechaProceso;

    private java.lang.Double importe;

    private ReciboIngresoWS[] ingresos;

    private java.lang.String numero;

    private java.lang.String sucursal;

    private java.lang.Double total;

    public ReciboHotelWS() {
    }

    public ReciboHotelWS(
           java.lang.String clienteDocumento,
           java.lang.Integer clienteId,
           java.lang.String clienteNombre,
           java.lang.Integer comprobanteAnio,
           java.lang.String comprobanteLetra,
           java.lang.String comprobanteNumero,
           java.lang.String comprobanteSucursal,
           java.lang.String comprobanteTipo,
           java.lang.String descripcion,
           java.lang.String error,
           java.util.Calendar fecha,
           java.util.Calendar fechaProceso,
           java.lang.Double importe,
           ReciboIngresoWS[] ingresos,
           java.lang.String numero,
           java.lang.String sucursal,
           java.lang.Double total) {
           this.clienteDocumento = clienteDocumento;
           this.clienteId = clienteId;
           this.clienteNombre = clienteNombre;
           this.comprobanteAnio = comprobanteAnio;
           this.comprobanteLetra = comprobanteLetra;
           this.comprobanteNumero = comprobanteNumero;
           this.comprobanteSucursal = comprobanteSucursal;
           this.comprobanteTipo = comprobanteTipo;
           this.descripcion = descripcion;
           this.error = error;
           this.fecha = fecha;
           this.fechaProceso = fechaProceso;
           this.importe = importe;
           this.ingresos = ingresos;
           this.numero = numero;
           this.sucursal = sucursal;
           this.total = total;
    }


    /**
     * Gets the clienteDocumento value for this ReciboHotelWS.
     * 
     * @return clienteDocumento
     */
    public java.lang.String getClienteDocumento() {
        return clienteDocumento;
    }


    /**
     * Sets the clienteDocumento value for this ReciboHotelWS.
     * 
     * @param clienteDocumento
     */
    public void setClienteDocumento(java.lang.String clienteDocumento) {
        this.clienteDocumento = clienteDocumento;
    }


    /**
     * Gets the clienteId value for this ReciboHotelWS.
     * 
     * @return clienteId
     */
    public java.lang.Integer getClienteId() {
        return clienteId;
    }


    /**
     * Sets the clienteId value for this ReciboHotelWS.
     * 
     * @param clienteId
     */
    public void setClienteId(java.lang.Integer clienteId) {
        this.clienteId = clienteId;
    }


    /**
     * Gets the clienteNombre value for this ReciboHotelWS.
     * 
     * @return clienteNombre
     */
    public java.lang.String getClienteNombre() {
        return clienteNombre;
    }


    /**
     * Sets the clienteNombre value for this ReciboHotelWS.
     * 
     * @param clienteNombre
     */
    public void setClienteNombre(java.lang.String clienteNombre) {
        this.clienteNombre = clienteNombre;
    }


    /**
     * Gets the comprobanteAnio value for this ReciboHotelWS.
     * 
     * @return comprobanteAnio
     */
    public java.lang.Integer getComprobanteAnio() {
        return comprobanteAnio;
    }


    /**
     * Sets the comprobanteAnio value for this ReciboHotelWS.
     * 
     * @param comprobanteAnio
     */
    public void setComprobanteAnio(java.lang.Integer comprobanteAnio) {
        this.comprobanteAnio = comprobanteAnio;
    }


    /**
     * Gets the comprobanteLetra value for this ReciboHotelWS.
     * 
     * @return comprobanteLetra
     */
    public java.lang.String getComprobanteLetra() {
        return comprobanteLetra;
    }


    /**
     * Sets the comprobanteLetra value for this ReciboHotelWS.
     * 
     * @param comprobanteLetra
     */
    public void setComprobanteLetra(java.lang.String comprobanteLetra) {
        this.comprobanteLetra = comprobanteLetra;
    }


    /**
     * Gets the comprobanteNumero value for this ReciboHotelWS.
     * 
     * @return comprobanteNumero
     */
    public java.lang.String getComprobanteNumero() {
        return comprobanteNumero;
    }


    /**
     * Sets the comprobanteNumero value for this ReciboHotelWS.
     * 
     * @param comprobanteNumero
     */
    public void setComprobanteNumero(java.lang.String comprobanteNumero) {
        this.comprobanteNumero = comprobanteNumero;
    }


    /**
     * Gets the comprobanteSucursal value for this ReciboHotelWS.
     * 
     * @return comprobanteSucursal
     */
    public java.lang.String getComprobanteSucursal() {
        return comprobanteSucursal;
    }


    /**
     * Sets the comprobanteSucursal value for this ReciboHotelWS.
     * 
     * @param comprobanteSucursal
     */
    public void setComprobanteSucursal(java.lang.String comprobanteSucursal) {
        this.comprobanteSucursal = comprobanteSucursal;
    }


    /**
     * Gets the comprobanteTipo value for this ReciboHotelWS.
     * 
     * @return comprobanteTipo
     */
    public java.lang.String getComprobanteTipo() {
        return comprobanteTipo;
    }


    /**
     * Sets the comprobanteTipo value for this ReciboHotelWS.
     * 
     * @param comprobanteTipo
     */
    public void setComprobanteTipo(java.lang.String comprobanteTipo) {
        this.comprobanteTipo = comprobanteTipo;
    }


    /**
     * Gets the descripcion value for this ReciboHotelWS.
     * 
     * @return descripcion
     */
    public java.lang.String getDescripcion() {
        return descripcion;
    }


    /**
     * Sets the descripcion value for this ReciboHotelWS.
     * 
     * @param descripcion
     */
    public void setDescripcion(java.lang.String descripcion) {
        this.descripcion = descripcion;
    }


    /**
     * Gets the error value for this ReciboHotelWS.
     * 
     * @return error
     */
    public java.lang.String getError() {
        return error;
    }


    /**
     * Sets the error value for this ReciboHotelWS.
     * 
     * @param error
     */
    public void setError(java.lang.String error) {
        this.error = error;
    }


    /**
     * Gets the fecha value for this ReciboHotelWS.
     * 
     * @return fecha
     */
    public java.util.Calendar getFecha() {
        return fecha;
    }


    /**
     * Sets the fecha value for this ReciboHotelWS.
     * 
     * @param fecha
     */
    public void setFecha(java.util.Calendar fecha) {
        this.fecha = fecha;
    }


    /**
     * Gets the fechaProceso value for this ReciboHotelWS.
     * 
     * @return fechaProceso
     */
    public java.util.Calendar getFechaProceso() {
        return fechaProceso;
    }


    /**
     * Sets the fechaProceso value for this ReciboHotelWS.
     * 
     * @param fechaProceso
     */
    public void setFechaProceso(java.util.Calendar fechaProceso) {
        this.fechaProceso = fechaProceso;
    }


    /**
     * Gets the importe value for this ReciboHotelWS.
     * 
     * @return importe
     */
    public java.lang.Double getImporte() {
        return importe;
    }


    /**
     * Sets the importe value for this ReciboHotelWS.
     * 
     * @param importe
     */
    public void setImporte(java.lang.Double importe) {
        this.importe = importe;
    }


    /**
     * Gets the ingresos value for this ReciboHotelWS.
     * 
     * @return ingresos
     */
    public ReciboIngresoWS[] getIngresos() {
        return ingresos;
    }


    /**
     * Sets the ingresos value for this ReciboHotelWS.
     * 
     * @param ingresos
     */
    public void setIngresos(ReciboIngresoWS[] ingresos) {
        this.ingresos = ingresos;
    }


    /**
     * Gets the numero value for this ReciboHotelWS.
     * 
     * @return numero
     */
    public java.lang.String getNumero() {
        return numero;
    }


    /**
     * Sets the numero value for this ReciboHotelWS.
     * 
     * @param numero
     */
    public void setNumero(java.lang.String numero) {
        this.numero = numero;
    }


    /**
     * Gets the sucursal value for this ReciboHotelWS.
     * 
     * @return sucursal
     */
    public java.lang.String getSucursal() {
        return sucursal;
    }


    /**
     * Sets the sucursal value for this ReciboHotelWS.
     * 
     * @param sucursal
     */
    public void setSucursal(java.lang.String sucursal) {
        this.sucursal = sucursal;
    }


    /**
     * Gets the total value for this ReciboHotelWS.
     * 
     * @return total
     */
    public java.lang.Double getTotal() {
        return total;
    }


    /**
     * Sets the total value for this ReciboHotelWS.
     * 
     * @param total
     */
    public void setTotal(java.lang.Double total) {
        this.total = total;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ReciboHotelWS)) return false;
        ReciboHotelWS other = (ReciboHotelWS) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.clienteDocumento==null && other.getClienteDocumento()==null) || 
             (this.clienteDocumento!=null &&
              this.clienteDocumento.equals(other.getClienteDocumento()))) &&
            ((this.clienteId==null && other.getClienteId()==null) || 
             (this.clienteId!=null &&
              this.clienteId.equals(other.getClienteId()))) &&
            ((this.clienteNombre==null && other.getClienteNombre()==null) || 
             (this.clienteNombre!=null &&
              this.clienteNombre.equals(other.getClienteNombre()))) &&
            ((this.comprobanteAnio==null && other.getComprobanteAnio()==null) || 
             (this.comprobanteAnio!=null &&
              this.comprobanteAnio.equals(other.getComprobanteAnio()))) &&
            ((this.comprobanteLetra==null && other.getComprobanteLetra()==null) || 
             (this.comprobanteLetra!=null &&
              this.comprobanteLetra.equals(other.getComprobanteLetra()))) &&
            ((this.comprobanteNumero==null && other.getComprobanteNumero()==null) || 
             (this.comprobanteNumero!=null &&
              this.comprobanteNumero.equals(other.getComprobanteNumero()))) &&
            ((this.comprobanteSucursal==null && other.getComprobanteSucursal()==null) || 
             (this.comprobanteSucursal!=null &&
              this.comprobanteSucursal.equals(other.getComprobanteSucursal()))) &&
            ((this.comprobanteTipo==null && other.getComprobanteTipo()==null) || 
             (this.comprobanteTipo!=null &&
              this.comprobanteTipo.equals(other.getComprobanteTipo()))) &&
            ((this.descripcion==null && other.getDescripcion()==null) || 
             (this.descripcion!=null &&
              this.descripcion.equals(other.getDescripcion()))) &&
            ((this.error==null && other.getError()==null) || 
             (this.error!=null &&
              this.error.equals(other.getError()))) &&
            ((this.fecha==null && other.getFecha()==null) || 
             (this.fecha!=null &&
              this.fecha.equals(other.getFecha()))) &&
            ((this.fechaProceso==null && other.getFechaProceso()==null) || 
             (this.fechaProceso!=null &&
              this.fechaProceso.equals(other.getFechaProceso()))) &&
            ((this.importe==null && other.getImporte()==null) || 
             (this.importe!=null &&
              this.importe.equals(other.getImporte()))) &&
            ((this.ingresos==null && other.getIngresos()==null) || 
             (this.ingresos!=null &&
              java.util.Arrays.equals(this.ingresos, other.getIngresos()))) &&
            ((this.numero==null && other.getNumero()==null) || 
             (this.numero!=null &&
              this.numero.equals(other.getNumero()))) &&
            ((this.sucursal==null && other.getSucursal()==null) || 
             (this.sucursal!=null &&
              this.sucursal.equals(other.getSucursal()))) &&
            ((this.total==null && other.getTotal()==null) || 
             (this.total!=null &&
              this.total.equals(other.getTotal())));
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
        if (getClienteDocumento() != null) {
            _hashCode += getClienteDocumento().hashCode();
        }
        if (getClienteId() != null) {
            _hashCode += getClienteId().hashCode();
        }
        if (getClienteNombre() != null) {
            _hashCode += getClienteNombre().hashCode();
        }
        if (getComprobanteAnio() != null) {
            _hashCode += getComprobanteAnio().hashCode();
        }
        if (getComprobanteLetra() != null) {
            _hashCode += getComprobanteLetra().hashCode();
        }
        if (getComprobanteNumero() != null) {
            _hashCode += getComprobanteNumero().hashCode();
        }
        if (getComprobanteSucursal() != null) {
            _hashCode += getComprobanteSucursal().hashCode();
        }
        if (getComprobanteTipo() != null) {
            _hashCode += getComprobanteTipo().hashCode();
        }
        if (getDescripcion() != null) {
            _hashCode += getDescripcion().hashCode();
        }
        if (getError() != null) {
            _hashCode += getError().hashCode();
        }
        if (getFecha() != null) {
            _hashCode += getFecha().hashCode();
        }
        if (getFechaProceso() != null) {
            _hashCode += getFechaProceso().hashCode();
        }
        if (getImporte() != null) {
            _hashCode += getImporte().hashCode();
        }
        if (getIngresos() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getIngresos());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getIngresos(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getNumero() != null) {
            _hashCode += getNumero().hashCode();
        }
        if (getSucursal() != null) {
            _hashCode += getSucursal().hashCode();
        }
        if (getTotal() != null) {
            _hashCode += getTotal().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ReciboHotelWS.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://webservice.transferenciahoteles", "ReciboHotelWS"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("clienteDocumento");
        elemField.setXmlName(new javax.xml.namespace.QName("http://webservice.transferenciahoteles", "clienteDocumento"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("clienteId");
        elemField.setXmlName(new javax.xml.namespace.QName("http://webservice.transferenciahoteles", "clienteId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("clienteNombre");
        elemField.setXmlName(new javax.xml.namespace.QName("http://webservice.transferenciahoteles", "clienteNombre"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("comprobanteAnio");
        elemField.setXmlName(new javax.xml.namespace.QName("http://webservice.transferenciahoteles", "comprobanteAnio"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("comprobanteLetra");
        elemField.setXmlName(new javax.xml.namespace.QName("http://webservice.transferenciahoteles", "comprobanteLetra"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("comprobanteNumero");
        elemField.setXmlName(new javax.xml.namespace.QName("http://webservice.transferenciahoteles", "comprobanteNumero"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("comprobanteSucursal");
        elemField.setXmlName(new javax.xml.namespace.QName("http://webservice.transferenciahoteles", "comprobanteSucursal"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("comprobanteTipo");
        elemField.setXmlName(new javax.xml.namespace.QName("http://webservice.transferenciahoteles", "comprobanteTipo"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("descripcion");
        elemField.setXmlName(new javax.xml.namespace.QName("http://webservice.transferenciahoteles", "descripcion"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("error");
        elemField.setXmlName(new javax.xml.namespace.QName("http://webservice.transferenciahoteles", "error"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("fecha");
        elemField.setXmlName(new javax.xml.namespace.QName("http://webservice.transferenciahoteles", "fecha"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("fechaProceso");
        elemField.setXmlName(new javax.xml.namespace.QName("http://webservice.transferenciahoteles", "fechaProceso"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("importe");
        elemField.setXmlName(new javax.xml.namespace.QName("http://webservice.transferenciahoteles", "importe"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "double"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ingresos");
        elemField.setXmlName(new javax.xml.namespace.QName("http://webservice.transferenciahoteles", "ingresos"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://webservice.transferenciahoteles", "ReciboIngresoWS"));
        elemField.setNillable(true);
        elemField.setItemQName(new javax.xml.namespace.QName("http://webservice.transferenciahoteles", "item"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("numero");
        elemField.setXmlName(new javax.xml.namespace.QName("http://webservice.transferenciahoteles", "numero"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("sucursal");
        elemField.setXmlName(new javax.xml.namespace.QName("http://webservice.transferenciahoteles", "sucursal"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("total");
        elemField.setXmlName(new javax.xml.namespace.QName("http://webservice.transferenciahoteles", "total"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "double"));
        elemField.setNillable(true);
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
