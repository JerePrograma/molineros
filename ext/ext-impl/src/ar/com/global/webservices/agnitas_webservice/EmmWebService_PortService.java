/**
 * EmmWebService_PortService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package ar.com.global.webservices.agnitas_webservice;

public interface EmmWebService_PortService extends javax.xml.rpc.Service {
    public java.lang.String getemm_webserviceAddress();

    public EmmWebService_Port getemm_webservice() throws javax.xml.rpc.ServiceException;

    public EmmWebService_Port getemm_webservice(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}
