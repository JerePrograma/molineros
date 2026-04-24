/**
 * SociosLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package ar.com.ospim.webservice.omint;

public class SociosLocator extends org.apache.axis.client.Service implements Socios {

    public SociosLocator() {
    }


    public SociosLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public SociosLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for SociosSoap
    //private java.lang.String SociosSoap_address = "http://localhost/wstransfer/socios.asmx";
    private java.lang.String SociosSoap_address = "http://200.47.31.26/wstransfer/socios.asmx";

    public java.lang.String getSociosSoapAddress() {
        return SociosSoap_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String SociosSoapWSDDServiceName = "SociosSoap";

    public java.lang.String getSociosSoapWSDDServiceName() {
        return SociosSoapWSDDServiceName;
    }

    public void setSociosSoapWSDDServiceName(java.lang.String name) {
        SociosSoapWSDDServiceName = name;
    }

    public SociosSoap getSociosSoap() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(SociosSoap_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getSociosSoap(endpoint);
    }

    public SociosSoap getSociosSoap(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            SociosSoapStub _stub = new SociosSoapStub(portAddress, this);
            _stub.setPortName(getSociosSoapWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setSociosSoapEndpointAddress(java.lang.String address) {
        SociosSoap_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (SociosSoap.class.isAssignableFrom(serviceEndpointInterface)) {
                SociosSoapStub _stub = new SociosSoapStub(new java.net.URL(SociosSoap_address), this);
                _stub.setPortName(getSociosSoapWSDDServiceName());
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
        if ("SociosSoap".equals(inputPortName)) {
            return getSociosSoap();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://tempuri.org/", "Socios");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://tempuri.org/", "SociosSoap"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("SociosSoap".equals(portName)) {
            setSociosSoapEndpointAddress(address);
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
