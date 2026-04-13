/**
 * FacturaWS.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package ar.com.ospim.webservice.hoteles;

public class FacturaWS  implements java.io.Serializable {
    private java.lang.String cae;

    private ClienteHotelWS cliente;

    private FacturaDetalleWS[] detalles;

    private java.lang.String error;

    private java.util.Calendar fecha;

    private java.util.Calendar fechaCae;

    private java.util.Calendar fechaProceso;

    private java.lang.Long id;

    private java.lang.Long idCentral;

    private java.math.BigDecimal importeNeto;

    private java.math.BigDecimal importeTotal;

    private FacturaIngresoWS[] ingresos;

    private java.math.BigDecimal iva;

    private java.lang.String letra;

    private java.lang.String numero;

    private java.lang.String observaciones;

    private java.lang.Boolean presentaForm8001;

    private ReciboHotelWS[] recibosAdelantos;

    private java.lang.String sucursal;

    private java.lang.String tipo;

    private java.math.BigDecimal totalExento;

    public FacturaWS() {
    }

    public FacturaWS(
           java.lang.String cae,
           ClienteHotelWS cliente,
           FacturaDetalleWS[] detalles,
           java.lang.String error,
           java.util.Calendar fecha,
           java.util.Calendar fechaCae,
           java.util.Calendar fechaProceso,
           java.lang.Long id,
           java.lang.Long idCentral,
           java.math.BigDecimal importeNeto,
           java.math.BigDecimal importeTotal,
           FacturaIngresoWS[] ingresos,
           java.math.BigDecimal iva,
           java.lang.String letra,
           java.lang.String numero,
           java.lang.String observaciones,
           java.lang.Boolean presentaForm8001,
           ReciboHotelWS[] recibosAdelantos,
           java.lang.String sucursal,
           java.lang.String tipo,
           java.math.BigDecimal totalExento) {
           this.cae = cae;
           this.cliente = cliente;
           this.detalles = detalles;
           this.error = error;
           this.fecha = fecha;
           this.fechaCae = fechaCae;
           this.fechaProceso = fechaProceso;
           this.id = id;
           this.idCentral = idCentral;
           this.importeNeto = importeNeto;
           this.importeTotal = importeTotal;
           this.ingresos = ingresos;
           this.iva = iva;
           this.letra = letra;
           this.numero = numero;
           this.observaciones = observaciones;
           this.presentaForm8001 = presentaForm8001;
           this.recibosAdelantos = recibosAdelantos;
           this.sucursal = sucursal;
           this.tipo = tipo;
           this.totalExento = totalExento;
    }


    /**
     * Gets the cae value for this FacturaWS.
     * 
     * @return cae
     */
    public java.lang.String getCae() {
        return cae;
    }


    /**
     * Sets the cae value for this FacturaWS.
     * 
     * @param cae
     */
    public void setCae(java.lang.String cae) {
        this.cae = cae;
    }


    /**
     * Gets the cliente value for this FacturaWS.
     * 
     * @return cliente
     */
    public ClienteHotelWS getCliente() {
        return cliente;
    }


    /**
     * Sets the cliente value for this FacturaWS.
     * 
     * @param cliente
     */
    public void setCliente(ClienteHotelWS cliente) {
        this.cliente = cliente;
    }


    /**
     * Gets the detalles value for this FacturaWS.
     * 
     * @return detalles
     */
    public FacturaDetalleWS[] getDetalles() {
        return detalles;
    }


    /**
     * Sets the detalles value for this FacturaWS.
     * 
     * @param detalles
     */
    public void setDetalles(FacturaDetalleWS[] detalles) {
        this.detalles = detalles;
    }


    /**
     * Gets the error value for this FacturaWS.
     * 
     * @return error
     */
    public java.lang.String getError() {
        return error;
    }


    /**
     * Sets the error value for this FacturaWS.
     * 
     * @param error
     */
    public void setError(java.lang.String error) {
        this.error = error;
    }


    /**
     * Gets the fecha value for this FacturaWS.
     * 
     * @return fecha
     */
    public java.util.Calendar getFecha() {
        return fecha;
    }


    /**
     * Sets the fecha value for this FacturaWS.
     * 
     * @param fecha
     */
    public void setFecha(java.util.Calendar fecha) {
        this.fecha = fecha;
    }


    /**
     * Gets the fechaCae value for this FacturaWS.
     * 
     * @return fechaCae
     */
    public java.util.Calendar getFechaCae() {
        return fechaCae;
    }


    /**
     * Sets the fechaCae value for this FacturaWS.
     * 
     * @param fechaCae
     */
    public void setFechaCae(java.util.Calendar fechaCae) {
        this.fechaCae = fechaCae;
    }


    /**
     * Gets the fechaProceso value for this FacturaWS.
     * 
     * @return fechaProceso
     */
    public java.util.Calendar getFechaProceso() {
        return fechaProceso;
    }


    /**
     * Sets the fechaProceso value for this FacturaWS.
     * 
     * @param fechaProceso
     */
    public void setFechaProceso(java.util.Calendar fechaProceso) {
        this.fechaProceso = fechaProceso;
    }


    /**
     * Gets the id value for this FacturaWS.
     * 
     * @return id
     */
    public java.lang.Long getId() {
        return id;
    }


    /**
     * Sets the id value for this FacturaWS.
     * 
     * @param id
     */
    public void setId(java.lang.Long id) {
        this.id = id;
    }


    /**
     * Gets the idCentral value for this FacturaWS.
     * 
     * @return idCentral
     */
    public java.lang.Long getIdCentral() {
        return idCentral;
    }


    /**
     * Sets the idCentral value for this FacturaWS.
     * 
     * @param idCentral
     */
    public void setIdCentral(java.lang.Long idCentral) {
        this.idCentral = idCentral;
    }


    /**
     * Gets the importeNeto value for this FacturaWS.
     * 
     * @return importeNeto
     */
    public java.math.BigDecimal getImporteNeto() {
        return importeNeto;
    }


    /**
     * Sets the importeNeto value for this FacturaWS.
     * 
     * @param importeNeto
     */
    public void setImporteNeto(java.math.BigDecimal importeNeto) {
        this.importeNeto = importeNeto;
    }


    /**
     * Gets the importeTotal value for this FacturaWS.
     * 
     * @return importeTotal
     */
    public java.math.BigDecimal getImporteTotal() {
        return importeTotal;
    }


    /**
     * Sets the importeTotal value for this FacturaWS.
     * 
     * @param importeTotal
     */
    public void setImporteTotal(java.math.BigDecimal importeTotal) {
        this.importeTotal = importeTotal;
    }


    /**
     * Gets the ingresos value for this FacturaWS.
     * 
     * @return ingresos
     */
    public FacturaIngresoWS[] getIngresos() {
        return ingresos;
    }


    /**
     * Sets the ingresos value for this FacturaWS.
     * 
     * @param ingresos
     */
    public void setIngresos(FacturaIngresoWS[] ingresos) {
        this.ingresos = ingresos;
    }


    /**
     * Gets the iva value for this FacturaWS.
     * 
     * @return iva
     */
    public java.math.BigDecimal getIva() {
        return iva;
    }


    /**
     * Sets the iva value for this FacturaWS.
     * 
     * @param iva
     */
    public void setIva(java.math.BigDecimal iva) {
        this.iva = iva;
    }


    /**
     * Gets the letra value for this FacturaWS.
     * 
     * @return letra
     */
    public java.lang.String getLetra() {
        return letra;
    }


    /**
     * Sets the letra value for this FacturaWS.
     * 
     * @param letra
     */
    public void setLetra(java.lang.String letra) {
        this.letra = letra;
    }


    /**
     * Gets the numero value for this FacturaWS.
     * 
     * @return numero
     */
    public java.lang.String getNumero() {
        return numero;
    }


    /**
     * Sets the numero value for this FacturaWS.
     * 
     * @param numero
     */
    public void setNumero(java.lang.String numero) {
        this.numero = numero;
    }


    /**
     * Gets the observaciones value for this FacturaWS.
     * 
     * @return observaciones
     */
    public java.lang.String getObservaciones() {
        return observaciones;
    }


    /**
     * Sets the observaciones value for this FacturaWS.
     * 
     * @param observaciones
     */
    public void setObservaciones(java.lang.String observaciones) {
        this.observaciones = observaciones;
    }


    /**
     * Gets the presentaForm8001 value for this FacturaWS.
     * 
     * @return presentaForm8001
     */
    public java.lang.Boolean getPresentaForm8001() {
        return presentaForm8001;
    }


    /**
     * Sets the presentaForm8001 value for this FacturaWS.
     * 
     * @param presentaForm8001
     */
    public void setPresentaForm8001(java.lang.Boolean presentaForm8001) {
        this.presentaForm8001 = presentaForm8001;
    }


    /**
     * Gets the recibosAdelantos value for this FacturaWS.
     * 
     * @return recibosAdelantos
     */
    public ReciboHotelWS[] getRecibosAdelantos() {
        return recibosAdelantos;
    }


    /**
     * Sets the recibosAdelantos value for this FacturaWS.
     * 
     * @param recibosAdelantos
     */
    public void setRecibosAdelantos(ReciboHotelWS[] recibosAdelantos) {
        this.recibosAdelantos = recibosAdelantos;
    }


    /**
     * Gets the sucursal value for this FacturaWS.
     * 
     * @return sucursal
     */
    public java.lang.String getSucursal() {
        return sucursal;
    }


    /**
     * Sets the sucursal value for this FacturaWS.
     * 
     * @param sucursal
     */
    public void setSucursal(java.lang.String sucursal) {
        this.sucursal = sucursal;
    }


    /**
     * Gets the tipo value for this FacturaWS.
     * 
     * @return tipo
     */
    public java.lang.String getTipo() {
        return tipo;
    }


    /**
     * Sets the tipo value for this FacturaWS.
     * 
     * @param tipo
     */
    public void setTipo(java.lang.String tipo) {
        this.tipo = tipo;
    }


    /**
     * Gets the totalExento value for this FacturaWS.
     * 
     * @return totalExento
     */
    public java.math.BigDecimal getTotalExento() {
        return totalExento;
    }


    /**
     * Sets the totalExento value for this FacturaWS.
     * 
     * @param totalExento
     */
    public void setTotalExento(java.math.BigDecimal totalExento) {
        this.totalExento = totalExento;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof FacturaWS)) return false;
        FacturaWS other = (FacturaWS) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.cae==null && other.getCae()==null) || 
             (this.cae!=null &&
              this.cae.equals(other.getCae()))) &&
            ((this.cliente==null && other.getCliente()==null) || 
             (this.cliente!=null &&
              this.cliente.equals(other.getCliente()))) &&
            ((this.detalles==null && other.getDetalles()==null) || 
             (this.detalles!=null &&
              java.util.Arrays.equals(this.detalles, other.getDetalles()))) &&
            ((this.error==null && other.getError()==null) || 
             (this.error!=null &&
              this.error.equals(other.getError()))) &&
            ((this.fecha==null && other.getFecha()==null) || 
             (this.fecha!=null &&
              this.fecha.equals(other.getFecha()))) &&
            ((this.fechaCae==null && other.getFechaCae()==null) || 
             (this.fechaCae!=null &&
              this.fechaCae.equals(other.getFechaCae()))) &&
            ((this.fechaProceso==null && other.getFechaProceso()==null) || 
             (this.fechaProceso!=null &&
              this.fechaProceso.equals(other.getFechaProceso()))) &&
            ((this.id==null && other.getId()==null) || 
             (this.id!=null &&
              this.id.equals(other.getId()))) &&
            ((this.idCentral==null && other.getIdCentral()==null) || 
             (this.idCentral!=null &&
              this.idCentral.equals(other.getIdCentral()))) &&
            ((this.importeNeto==null && other.getImporteNeto()==null) || 
             (this.importeNeto!=null &&
              this.importeNeto.equals(other.getImporteNeto()))) &&
            ((this.importeTotal==null && other.getImporteTotal()==null) || 
             (this.importeTotal!=null &&
              this.importeTotal.equals(other.getImporteTotal()))) &&
            ((this.ingresos==null && other.getIngresos()==null) || 
             (this.ingresos!=null &&
              java.util.Arrays.equals(this.ingresos, other.getIngresos()))) &&
            ((this.iva==null && other.getIva()==null) || 
             (this.iva!=null &&
              this.iva.equals(other.getIva()))) &&
            ((this.letra==null && other.getLetra()==null) || 
             (this.letra!=null &&
              this.letra.equals(other.getLetra()))) &&
            ((this.numero==null && other.getNumero()==null) || 
             (this.numero!=null &&
              this.numero.equals(other.getNumero()))) &&
            ((this.observaciones==null && other.getObservaciones()==null) || 
             (this.observaciones!=null &&
              this.observaciones.equals(other.getObservaciones()))) &&
            ((this.presentaForm8001==null && other.getPresentaForm8001()==null) || 
             (this.presentaForm8001!=null &&
              this.presentaForm8001.equals(other.getPresentaForm8001()))) &&
            ((this.recibosAdelantos==null && other.getRecibosAdelantos()==null) || 
             (this.recibosAdelantos!=null &&
              java.util.Arrays.equals(this.recibosAdelantos, other.getRecibosAdelantos()))) &&
            ((this.sucursal==null && other.getSucursal()==null) || 
             (this.sucursal!=null &&
              this.sucursal.equals(other.getSucursal()))) &&
            ((this.tipo==null && other.getTipo()==null) || 
             (this.tipo!=null &&
              this.tipo.equals(other.getTipo()))) &&
            ((this.totalExento==null && other.getTotalExento()==null) || 
             (this.totalExento!=null &&
              this.totalExento.equals(other.getTotalExento())));
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
        if (getCae() != null) {
            _hashCode += getCae().hashCode();
        }
        if (getCliente() != null) {
            _hashCode += getCliente().hashCode();
        }
        if (getDetalles() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getDetalles());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getDetalles(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getError() != null) {
            _hashCode += getError().hashCode();
        }
        if (getFecha() != null) {
            _hashCode += getFecha().hashCode();
        }
        if (getFechaCae() != null) {
            _hashCode += getFechaCae().hashCode();
        }
        if (getFechaProceso() != null) {
            _hashCode += getFechaProceso().hashCode();
        }
        if (getId() != null) {
            _hashCode += getId().hashCode();
        }
        if (getIdCentral() != null) {
            _hashCode += getIdCentral().hashCode();
        }
        if (getImporteNeto() != null) {
            _hashCode += getImporteNeto().hashCode();
        }
        if (getImporteTotal() != null) {
            _hashCode += getImporteTotal().hashCode();
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
        if (getIva() != null) {
            _hashCode += getIva().hashCode();
        }
        if (getLetra() != null) {
            _hashCode += getLetra().hashCode();
        }
        if (getNumero() != null) {
            _hashCode += getNumero().hashCode();
        }
        if (getObservaciones() != null) {
            _hashCode += getObservaciones().hashCode();
        }
        if (getPresentaForm8001() != null) {
            _hashCode += getPresentaForm8001().hashCode();
        }
        if (getRecibosAdelantos() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getRecibosAdelantos());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getRecibosAdelantos(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getSucursal() != null) {
            _hashCode += getSucursal().hashCode();
        }
        if (getTipo() != null) {
            _hashCode += getTipo().hashCode();
        }
        if (getTotalExento() != null) {
            _hashCode += getTotalExento().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(FacturaWS.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://webservice.transferenciahoteles", "FacturaWS"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cae");
        elemField.setXmlName(new javax.xml.namespace.QName("http://webservice.transferenciahoteles", "cae"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cliente");
        elemField.setXmlName(new javax.xml.namespace.QName("http://webservice.transferenciahoteles", "cliente"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://webservice.transferenciahoteles", "ClienteHotelWS"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("detalles");
        elemField.setXmlName(new javax.xml.namespace.QName("http://webservice.transferenciahoteles", "detalles"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://webservice.transferenciahoteles", "FacturaDetalleWS"));
        elemField.setNillable(true);
        elemField.setItemQName(new javax.xml.namespace.QName("http://webservice.transferenciahoteles", "item"));
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
        elemField.setFieldName("fechaCae");
        elemField.setXmlName(new javax.xml.namespace.QName("http://webservice.transferenciahoteles", "fechaCae"));
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
        elemField.setFieldName("id");
        elemField.setXmlName(new javax.xml.namespace.QName("http://webservice.transferenciahoteles", "id"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("idCentral");
        elemField.setXmlName(new javax.xml.namespace.QName("http://webservice.transferenciahoteles", "idCentral"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("importeNeto");
        elemField.setXmlName(new javax.xml.namespace.QName("http://webservice.transferenciahoteles", "importeNeto"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "decimal"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("importeTotal");
        elemField.setXmlName(new javax.xml.namespace.QName("http://webservice.transferenciahoteles", "importeTotal"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "decimal"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ingresos");
        elemField.setXmlName(new javax.xml.namespace.QName("http://webservice.transferenciahoteles", "ingresos"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://webservice.transferenciahoteles", "FacturaIngresoWS"));
        elemField.setNillable(true);
        elemField.setItemQName(new javax.xml.namespace.QName("http://webservice.transferenciahoteles", "item"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("iva");
        elemField.setXmlName(new javax.xml.namespace.QName("http://webservice.transferenciahoteles", "iva"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "decimal"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("letra");
        elemField.setXmlName(new javax.xml.namespace.QName("http://webservice.transferenciahoteles", "letra"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("numero");
        elemField.setXmlName(new javax.xml.namespace.QName("http://webservice.transferenciahoteles", "numero"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("observaciones");
        elemField.setXmlName(new javax.xml.namespace.QName("http://webservice.transferenciahoteles", "observaciones"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("presentaForm8001");
        elemField.setXmlName(new javax.xml.namespace.QName("http://webservice.transferenciahoteles", "presentaForm8001"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("recibosAdelantos");
        elemField.setXmlName(new javax.xml.namespace.QName("http://webservice.transferenciahoteles", "recibosAdelantos"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://webservice.transferenciahoteles", "ReciboHotelWS"));
        elemField.setNillable(true);
        elemField.setItemQName(new javax.xml.namespace.QName("http://webservice.transferenciahoteles", "item"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("sucursal");
        elemField.setXmlName(new javax.xml.namespace.QName("http://webservice.transferenciahoteles", "sucursal"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("tipo");
        elemField.setXmlName(new javax.xml.namespace.QName("http://webservice.transferenciahoteles", "tipo"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("totalExento");
        elemField.setXmlName(new javax.xml.namespace.QName("http://webservice.transferenciahoteles", "totalExento"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "decimal"));
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
