/**
 * IngresoWS.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package ar.com.ospim.webservice.hoteles;

public class IngresoWS  implements java.io.Serializable {
    private java.lang.Integer bancoId;

    private java.lang.Integer chequeEstado;

    private java.lang.Integer cuentaBancariaId;

    private java.util.Calendar fecha;

    private java.lang.Double importe;

    private java.lang.String operacionNro;

    private java.lang.String sucursal;

    private java.lang.Integer tarjetaCuotas;

    private java.lang.Integer tarjetaEmisor;

    private java.lang.String tipoIngreso;

    private java.lang.Integer transferenciaTipo;

    private java.lang.String usuario;

    public IngresoWS() {
    }

    public IngresoWS(
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
           this.bancoId = bancoId;
           this.chequeEstado = chequeEstado;
           this.cuentaBancariaId = cuentaBancariaId;
           this.fecha = fecha;
           this.importe = importe;
           this.operacionNro = operacionNro;
           this.sucursal = sucursal;
           this.tarjetaCuotas = tarjetaCuotas;
           this.tarjetaEmisor = tarjetaEmisor;
           this.tipoIngreso = tipoIngreso;
           this.transferenciaTipo = transferenciaTipo;
           this.usuario = usuario;
    }


    /**
     * Gets the bancoId value for this IngresoWS.
     * 
     * @return bancoId
     */
    public java.lang.Integer getBancoId() {
        return bancoId;
    }


    /**
     * Sets the bancoId value for this IngresoWS.
     * 
     * @param bancoId
     */
    public void setBancoId(java.lang.Integer bancoId) {
        this.bancoId = bancoId;
    }


    /**
     * Gets the chequeEstado value for this IngresoWS.
     * 
     * @return chequeEstado
     */
    public java.lang.Integer getChequeEstado() {
        return chequeEstado;
    }


    /**
     * Sets the chequeEstado value for this IngresoWS.
     * 
     * @param chequeEstado
     */
    public void setChequeEstado(java.lang.Integer chequeEstado) {
        this.chequeEstado = chequeEstado;
    }


    /**
     * Gets the cuentaBancariaId value for this IngresoWS.
     * 
     * @return cuentaBancariaId
     */
    public java.lang.Integer getCuentaBancariaId() {
        return cuentaBancariaId;
    }


    /**
     * Sets the cuentaBancariaId value for this IngresoWS.
     * 
     * @param cuentaBancariaId
     */
    public void setCuentaBancariaId(java.lang.Integer cuentaBancariaId) {
        this.cuentaBancariaId = cuentaBancariaId;
    }


    /**
     * Gets the fecha value for this IngresoWS.
     * 
     * @return fecha
     */
    public java.util.Calendar getFecha() {
        return fecha;
    }


    /**
     * Sets the fecha value for this IngresoWS.
     * 
     * @param fecha
     */
    public void setFecha(java.util.Calendar fecha) {
        this.fecha = fecha;
    }


    /**
     * Gets the importe value for this IngresoWS.
     * 
     * @return importe
     */
    public java.lang.Double getImporte() {
        return importe;
    }


    /**
     * Sets the importe value for this IngresoWS.
     * 
     * @param importe
     */
    public void setImporte(java.lang.Double importe) {
        this.importe = importe;
    }


    /**
     * Gets the operacionNro value for this IngresoWS.
     * 
     * @return operacionNro
     */
    public java.lang.String getOperacionNro() {
        return operacionNro;
    }


    /**
     * Sets the operacionNro value for this IngresoWS.
     * 
     * @param operacionNro
     */
    public void setOperacionNro(java.lang.String operacionNro) {
        this.operacionNro = operacionNro;
    }


    /**
     * Gets the sucursal value for this IngresoWS.
     * 
     * @return sucursal
     */
    public java.lang.String getSucursal() {
        return sucursal;
    }


    /**
     * Sets the sucursal value for this IngresoWS.
     * 
     * @param sucursal
     */
    public void setSucursal(java.lang.String sucursal) {
        this.sucursal = sucursal;
    }


    /**
     * Gets the tarjetaCuotas value for this IngresoWS.
     * 
     * @return tarjetaCuotas
     */
    public java.lang.Integer getTarjetaCuotas() {
        return tarjetaCuotas;
    }


    /**
     * Sets the tarjetaCuotas value for this IngresoWS.
     * 
     * @param tarjetaCuotas
     */
    public void setTarjetaCuotas(java.lang.Integer tarjetaCuotas) {
        this.tarjetaCuotas = tarjetaCuotas;
    }


    /**
     * Gets the tarjetaEmisor value for this IngresoWS.
     * 
     * @return tarjetaEmisor
     */
    public java.lang.Integer getTarjetaEmisor() {
        return tarjetaEmisor;
    }


    /**
     * Sets the tarjetaEmisor value for this IngresoWS.
     * 
     * @param tarjetaEmisor
     */
    public void setTarjetaEmisor(java.lang.Integer tarjetaEmisor) {
        this.tarjetaEmisor = tarjetaEmisor;
    }


    /**
     * Gets the tipoIngreso value for this IngresoWS.
     * 
     * @return tipoIngreso
     */
    public java.lang.String getTipoIngreso() {
        return tipoIngreso;
    }


    /**
     * Sets the tipoIngreso value for this IngresoWS.
     * 
     * @param tipoIngreso
     */
    public void setTipoIngreso(java.lang.String tipoIngreso) {
        this.tipoIngreso = tipoIngreso;
    }


    /**
     * Gets the transferenciaTipo value for this IngresoWS.
     * 
     * @return transferenciaTipo
     */
    public java.lang.Integer getTransferenciaTipo() {
        return transferenciaTipo;
    }


    /**
     * Sets the transferenciaTipo value for this IngresoWS.
     * 
     * @param transferenciaTipo
     */
    public void setTransferenciaTipo(java.lang.Integer transferenciaTipo) {
        this.transferenciaTipo = transferenciaTipo;
    }


    /**
     * Gets the usuario value for this IngresoWS.
     * 
     * @return usuario
     */
    public java.lang.String getUsuario() {
        return usuario;
    }


    /**
     * Sets the usuario value for this IngresoWS.
     * 
     * @param usuario
     */
    public void setUsuario(java.lang.String usuario) {
        this.usuario = usuario;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof IngresoWS)) return false;
        IngresoWS other = (IngresoWS) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.bancoId==null && other.getBancoId()==null) || 
             (this.bancoId!=null &&
              this.bancoId.equals(other.getBancoId()))) &&
            ((this.chequeEstado==null && other.getChequeEstado()==null) || 
             (this.chequeEstado!=null &&
              this.chequeEstado.equals(other.getChequeEstado()))) &&
            ((this.cuentaBancariaId==null && other.getCuentaBancariaId()==null) || 
             (this.cuentaBancariaId!=null &&
              this.cuentaBancariaId.equals(other.getCuentaBancariaId()))) &&
            ((this.fecha==null && other.getFecha()==null) || 
             (this.fecha!=null &&
              this.fecha.equals(other.getFecha()))) &&
            ((this.importe==null && other.getImporte()==null) || 
             (this.importe!=null &&
              this.importe.equals(other.getImporte()))) &&
            ((this.operacionNro==null && other.getOperacionNro()==null) || 
             (this.operacionNro!=null &&
              this.operacionNro.equals(other.getOperacionNro()))) &&
            ((this.sucursal==null && other.getSucursal()==null) || 
             (this.sucursal!=null &&
              this.sucursal.equals(other.getSucursal()))) &&
            ((this.tarjetaCuotas==null && other.getTarjetaCuotas()==null) || 
             (this.tarjetaCuotas!=null &&
              this.tarjetaCuotas.equals(other.getTarjetaCuotas()))) &&
            ((this.tarjetaEmisor==null && other.getTarjetaEmisor()==null) || 
             (this.tarjetaEmisor!=null &&
              this.tarjetaEmisor.equals(other.getTarjetaEmisor()))) &&
            ((this.tipoIngreso==null && other.getTipoIngreso()==null) || 
             (this.tipoIngreso!=null &&
              this.tipoIngreso.equals(other.getTipoIngreso()))) &&
            ((this.transferenciaTipo==null && other.getTransferenciaTipo()==null) || 
             (this.transferenciaTipo!=null &&
              this.transferenciaTipo.equals(other.getTransferenciaTipo()))) &&
            ((this.usuario==null && other.getUsuario()==null) || 
             (this.usuario!=null &&
              this.usuario.equals(other.getUsuario())));
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
        if (getBancoId() != null) {
            _hashCode += getBancoId().hashCode();
        }
        if (getChequeEstado() != null) {
            _hashCode += getChequeEstado().hashCode();
        }
        if (getCuentaBancariaId() != null) {
            _hashCode += getCuentaBancariaId().hashCode();
        }
        if (getFecha() != null) {
            _hashCode += getFecha().hashCode();
        }
        if (getImporte() != null) {
            _hashCode += getImporte().hashCode();
        }
        if (getOperacionNro() != null) {
            _hashCode += getOperacionNro().hashCode();
        }
        if (getSucursal() != null) {
            _hashCode += getSucursal().hashCode();
        }
        if (getTarjetaCuotas() != null) {
            _hashCode += getTarjetaCuotas().hashCode();
        }
        if (getTarjetaEmisor() != null) {
            _hashCode += getTarjetaEmisor().hashCode();
        }
        if (getTipoIngreso() != null) {
            _hashCode += getTipoIngreso().hashCode();
        }
        if (getTransferenciaTipo() != null) {
            _hashCode += getTransferenciaTipo().hashCode();
        }
        if (getUsuario() != null) {
            _hashCode += getUsuario().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(IngresoWS.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://webservice.transferenciahoteles", "IngresoWS"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("bancoId");
        elemField.setXmlName(new javax.xml.namespace.QName("http://webservice.transferenciahoteles", "bancoId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("chequeEstado");
        elemField.setXmlName(new javax.xml.namespace.QName("http://webservice.transferenciahoteles", "chequeEstado"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cuentaBancariaId");
        elemField.setXmlName(new javax.xml.namespace.QName("http://webservice.transferenciahoteles", "cuentaBancariaId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("fecha");
        elemField.setXmlName(new javax.xml.namespace.QName("http://webservice.transferenciahoteles", "fecha"));
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
        elemField.setFieldName("operacionNro");
        elemField.setXmlName(new javax.xml.namespace.QName("http://webservice.transferenciahoteles", "operacionNro"));
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
        elemField.setFieldName("tarjetaCuotas");
        elemField.setXmlName(new javax.xml.namespace.QName("http://webservice.transferenciahoteles", "tarjetaCuotas"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("tarjetaEmisor");
        elemField.setXmlName(new javax.xml.namespace.QName("http://webservice.transferenciahoteles", "tarjetaEmisor"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("tipoIngreso");
        elemField.setXmlName(new javax.xml.namespace.QName("http://webservice.transferenciahoteles", "tipoIngreso"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("transferenciaTipo");
        elemField.setXmlName(new javax.xml.namespace.QName("http://webservice.transferenciahoteles", "transferenciaTipo"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("usuario");
        elemField.setXmlName(new javax.xml.namespace.QName("http://webservice.transferenciahoteles", "usuario"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
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
