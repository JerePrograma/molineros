package ar.com.ospim.webservice.omint;

import java.rmi.RemoteException;
import java.util.Calendar;

public class SociosSoapProxy implements SociosSoap {
  private String _endpoint = null;
  private SociosSoap sociosSoap = null;
  
  public SociosSoapProxy() {
    _initSociosSoapProxy();
  }
  
  public SociosSoapProxy(String endpoint) {
    _endpoint = endpoint;
    _initSociosSoapProxy();
  }
  
  private void _initSociosSoapProxy() {
    try {
      sociosSoap = (new SociosLocator()).getSociosSoap();
      if (sociosSoap != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)sociosSoap)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)sociosSoap)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (sociosSoap != null)
      ((javax.xml.rpc.Stub)sociosSoap)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public SociosSoap getSociosSoap() {
    if (sociosSoap == null)
      _initSociosSoapProxy();
    return sociosSoap;
  }
  
  public GetLoginTokenResponseGetLoginTokenResult getLoginToken(java.lang.String companyCode, java.lang.String country) throws java.rmi.RemoteException{
    if (sociosSoap == null)
      _initSociosSoapProxy();
    return sociosSoap.getLoginToken(companyCode, country);
  }
  
  public GetSessionResponseGetSessionResult getSession(java.lang.String company, java.lang.String country , java.lang.String language) throws java.rmi.RemoteException{
    if (sociosSoap == null)
      _initSociosSoapProxy();
    return sociosSoap.getSession(company, country, language);
  }
  
  public AltaGrupoFamiliarResponseAltaGrupoFamiliarResult altaGrupoFamiliar(java.lang.String sessionID, AltaGrupoFamiliarTransactionData transactionData) throws java.rmi.RemoteException{
    if (sociosSoap == null)
      _initSociosSoapProxy();
    return sociosSoap.altaGrupoFamiliar(sessionID, transactionData);
  }
  
  public AltaBeneficiarioResponseAltaBeneficiarioResult altaBeneficiario(java.lang.String sessionID, java.lang.String CUILTitular, java.util.Calendar fecVig, java.lang.String apellido, java.lang.String nombre, java.lang.String parentesco, java.lang.String sexo, java.util.Calendar fecNac, java.lang.String calle, java.lang.String nroCalle, java.lang.String resto, java.lang.String localidad, java.lang.String CP, java.lang.String provincia, java.lang.String telefono, java.lang.String tipoDoc, java.lang.String nroDoc, java.lang.String seccional, int categoria, java.lang.String CUIL, java.util.Calendar FPP, int nroIntegrante, int nacionalidad, int estadoCivil, java.lang.String discapacidad) throws java.rmi.RemoteException{
    if (sociosSoap == null)
      _initSociosSoapProxy();
    return sociosSoap.altaBeneficiario(sessionID, CUILTitular, fecVig, apellido, nombre, parentesco, sexo, fecNac, calle, nroCalle, resto, localidad, CP, provincia, telefono, tipoDoc, nroDoc, seccional, categoria, CUIL, FPP, nroIntegrante, nacionalidad, estadoCivil, discapacidad);
  }
  
  public BajaGrupoFamiliarResponseBajaGrupoFamiliarResult bajaGrupoFamiliar(java.lang.String sessionID, int compania, java.lang.String CUILTitular, java.util.Calendar fecVig) throws java.rmi.RemoteException{
    if (sociosSoap == null)
      _initSociosSoapProxy();
    return sociosSoap.bajaGrupoFamiliar(sessionID, compania, CUILTitular, fecVig);
  }
  
  public BajaBeneficiarioResponseBajaBeneficiarioResult bajaBeneficiario(java.lang.String sessionID, int compania, java.lang.String CUILTitular, java.util.Calendar fecVig, java.lang.String CUILBeneficiario) throws java.rmi.RemoteException{
    if (sociosSoap == null)
      _initSociosSoapProxy();
    return sociosSoap.bajaBeneficiario(sessionID, compania, CUILTitular, fecVig, CUILBeneficiario);
  }
  
  public ModificacionBeneficiarioResponseModificacionBeneficiarioResult modificacionBeneficiario(java.lang.String sessionID, int compania, java.lang.String CUILTitular, java.util.Calendar fecVig, java.lang.String apellido, java.lang.String nombre, java.lang.String parentesco, java.lang.String sexo, java.util.Calendar fecNac, java.lang.String calle, java.lang.String nroCalle, java.lang.String resto, java.lang.String localidad, java.lang.String CP, java.lang.String provincia, java.lang.String telefono, java.lang.String tipoDoc, java.lang.String nroDoc, java.lang.String seccional, int categoria, java.lang.String CUIL, java.util.Calendar FPP, int nacionalidad, int estadoCivil, java.lang.String discapacidad) throws java.rmi.RemoteException{
    if (sociosSoap == null)
      _initSociosSoapProxy();
    	
    return sociosSoap.modificacionBeneficiario(sessionID, compania, CUILTitular, fecVig, apellido, nombre, parentesco, sexo, fecNac, calle, nroCalle, resto, localidad, CP, provincia, telefono, tipoDoc, nroDoc, seccional, categoria, CUIL, FPP, nacionalidad, estadoCivil, discapacidad);
    	
  }
  
  public CambioPlanGrupoFamiliarResponseCambioPlanGrupoFamiliarResult cambioPlanGrupoFamiliar(java.lang.String sessionID, int compania, java.lang.String CUILTitular, java.lang.String planMedico, java.util.Calendar fecVig) throws java.rmi.RemoteException{
    if (sociosSoap == null)
      _initSociosSoapProxy();
    return sociosSoap.cambioPlanGrupoFamiliar(sessionID, compania, CUILTitular, planMedico, fecVig);
  }
  
  public GetCambioPlanResponseGetCambioPlanResult getCambioPlan(java.lang.String sessionID, int compania, java.util.Calendar fecVig) throws java.rmi.RemoteException{
    if (sociosSoap == null)
      _initSociosSoapProxy();
    return sociosSoap.getCambioPlan(sessionID, compania, fecVig);
  }
  
  public GetBeneficiarioResponseGetBeneficiarioResult getBeneficiario(java.lang.String sessionID, int compania, java.util.Calendar fecVig, java.lang.String CUILTitular, int nroIntegrante) throws java.rmi.RemoteException{
    if (sociosSoap == null)
      _initSociosSoapProxy();
    return sociosSoap.getBeneficiario(sessionID, compania, fecVig, CUILTitular, nroIntegrante);
  }
  
  public void testAltaGrupoFamiliar() throws java.rmi.RemoteException{
    if (sociosSoap == null)
      _initSociosSoapProxy();
    sociosSoap.testAltaGrupoFamiliar();
  }
  
  public void testAltaBeneficiario() throws java.rmi.RemoteException{
    if (sociosSoap == null)
      _initSociosSoapProxy();
    sociosSoap.testAltaBeneficiario();
  }
  
  public void testBajaGrupoFamiliar() throws java.rmi.RemoteException{
    if (sociosSoap == null)
      _initSociosSoapProxy();
    sociosSoap.testBajaGrupoFamiliar();
  }
  
  public void testBajaBeneficiario() throws java.rmi.RemoteException{
    if (sociosSoap == null)
      _initSociosSoapProxy();
    sociosSoap.testBajaBeneficiario();
  }
  
  public void testModificacionBeneficiario() throws java.rmi.RemoteException{
    if (sociosSoap == null)
      _initSociosSoapProxy();
    sociosSoap.testModificacionBeneficiario();
  }
  
  public void testCambioPlanGrupoFamiliar() throws java.rmi.RemoteException{
    if (sociosSoap == null)
      _initSociosSoapProxy();
    sociosSoap.testCambioPlanGrupoFamiliar();
  }
  
  public void testGetCambioPlan() throws java.rmi.RemoteException{
    if (sociosSoap == null)
      _initSociosSoapProxy();
    sociosSoap.testGetCambioPlan();
  }
  
  public void testGetBeneficiario() throws java.rmi.RemoteException{
    if (sociosSoap == null)
      _initSociosSoapProxy();
    sociosSoap.testGetBeneficiario();
  }


  
  
}