/**
 * SincronizarHotelesServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package ar.com.ospim.webservice.hoteles;

public class SincronizarHotelesServiceLocator extends org.apache.axis.client.Service implements SincronizarHotelesService {

    public SincronizarHotelesServiceLocator() {
    }


    public SincronizarHotelesServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public SincronizarHotelesServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for SincronizarHoteles
    //private java.lang.String SincronizarHoteles_address = "http://localhost:8080/transferenciahoteles.webservice/services/SincronizarHoteles";
    private java.lang.String SincronizarHoteles_address = "http://200.125.119.204/transferenciahoteles.webservice/services/SincronizarHoteles";

    public java.lang.String getSincronizarHotelesAddress() {
        return SincronizarHoteles_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String SincronizarHotelesWSDDServiceName = "SincronizarHoteles";

    public java.lang.String getSincronizarHotelesWSDDServiceName() {
        return SincronizarHotelesWSDDServiceName;
    }

    public void setSincronizarHotelesWSDDServiceName(java.lang.String name) {
        SincronizarHotelesWSDDServiceName = name;
    }

    public SincronizarHoteles getSincronizarHoteles() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(SincronizarHoteles_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getSincronizarHoteles(endpoint);
    }

    public SincronizarHoteles getSincronizarHoteles(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            SincronizarHotelesSoapBindingStub _stub = new SincronizarHotelesSoapBindingStub(portAddress, this);
            _stub.setPortName(getSincronizarHotelesWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setSincronizarHotelesEndpointAddress(java.lang.String address) {
        SincronizarHoteles_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (SincronizarHoteles.class.isAssignableFrom(serviceEndpointInterface)) {
                SincronizarHotelesSoapBindingStub _stub = new SincronizarHotelesSoapBindingStub(new java.net.URL(SincronizarHoteles_address), this);
                _stub.setPortName(getSincronizarHotelesWSDDServiceName());
                return _stub;
            }
        }
        catch (java.lang.Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        java.lang.String inputPortName = portName.getLocalPart();
        if ("SincronizarHoteles".equals(inputPortName)) {
            return getSincronizarHoteles();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://webservice.transferenciahoteles", "SincronizarHotelesService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://webservice.transferenciahoteles", "SincronizarHoteles"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("SincronizarHoteles".equals(portName)) {
            setSincronizarHotelesEndpointAddress(address);
        }
        else 
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
