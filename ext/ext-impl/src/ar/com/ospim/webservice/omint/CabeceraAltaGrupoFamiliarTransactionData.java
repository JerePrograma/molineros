package ar.com.ospim.webservice.omint;

import java.io.Serializable;
import java.util.Calendar;

public class CabeceraAltaGrupoFamiliarTransactionData implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1425619919272419704L;
	
	private int compania;
    private String planMed;
    private java.util.Date fecVig;
//    private Calendar fecVig;
	
    public int getCompania() {
		return compania;
	}
	public void setCompania(int compania) {
		this.compania = compania;
	}
	public String getPlanMed() {
		return planMed;
	}
	public void setPlanMed(String planMed) {
		this.planMed = planMed;
	}
//	public Calendar getFecVig() {
//		return fecVig;
//	}
//	public void setFecVig(Calendar fecVig) {
//		this.fecVig = fecVig;
//	}
	public java.util.Date getFecVig() {
		return fecVig;
	}
	public void setFecVig(java.util.Date fecVig) {
		this.fecVig = fecVig;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + compania;
		result = prime * result + ((fecVig == null) ? 0 : fecVig.hashCode());
		result = prime * result + ((planMed == null) ? 0 : planMed.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CabeceraAltaGrupoFamiliarTransactionData other = (CabeceraAltaGrupoFamiliarTransactionData) obj;
		if (compania != other.compania)
			return false;
		if (fecVig == null) {
			if (other.fecVig != null)
				return false;
		} else if (!fecVig.equals(other.fecVig))
			return false;
		if (planMed == null) {
			if (other.planMed != null)
				return false;
		} else if (!planMed.equals(other.planMed))
			return false;
		return true;
	}
	
	// Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(CabeceraAltaGrupoFamiliarTransactionData.class, true);

    static {
//        typeDesc.setXmlType(new javax.xml.namespace.QName("http://tempuri.org/", ">GetBeneficiario"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("Compania");
        elemField.setXmlName(new javax.xml.namespace.QName("http://tempuri.org/", "Compania"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(1);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("PlanMed");
        elemField.setXmlName(new javax.xml.namespace.QName("http://tempuri.org/", "PlanMed"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("FecVig");
        elemField.setXmlName(new javax.xml.namespace.QName("http://tempuri.org/", "FecVig"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
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
