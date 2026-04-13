/**
 * SociosSoap.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package ar.com.ospim.webservice.omint;

public interface SociosSoap extends java.rmi.Remote {
    public GetLoginTokenResponseGetLoginTokenResult getLoginToken(java.lang.String companyCode, java.lang.String country) throws java.rmi.RemoteException;
    public GetSessionResponseGetSessionResult getSession(java.lang.String company, java.lang.String country,java.lang.String language) throws java.rmi.RemoteException;
    public AltaGrupoFamiliarResponseAltaGrupoFamiliarResult altaGrupoFamiliar(java.lang.String sessionID, AltaGrupoFamiliarTransactionData transactionData) throws java.rmi.RemoteException;
    public AltaBeneficiarioResponseAltaBeneficiarioResult altaBeneficiario(java.lang.String sessionID, java.lang.String CUILTitular, java.util.Calendar fecVig, java.lang.String apellido, java.lang.String nombre, java.lang.String parentesco, java.lang.String sexo, java.util.Calendar fecNac, java.lang.String calle, java.lang.String nroCalle, java.lang.String resto, java.lang.String localidad, java.lang.String CP, java.lang.String provincia, java.lang.String telefono, java.lang.String tipoDoc, java.lang.String nroDoc, java.lang.String seccional, int categoria, java.lang.String CUIL, java.util.Calendar FPP, int nroIntegrante, int nacionalidad, int estadoCivil, java.lang.String discapacidad) throws java.rmi.RemoteException;
    public BajaGrupoFamiliarResponseBajaGrupoFamiliarResult bajaGrupoFamiliar(java.lang.String sessionID, int compania, java.lang.String CUILTitular, java.util.Calendar fecVig) throws java.rmi.RemoteException;
    public BajaBeneficiarioResponseBajaBeneficiarioResult bajaBeneficiario(java.lang.String sessionID, int compania, java.lang.String CUILTitular, java.util.Calendar fecVig, java.lang.String CUILBeneficiario) throws java.rmi.RemoteException;
    public ModificacionBeneficiarioResponseModificacionBeneficiarioResult modificacionBeneficiario(java.lang.String sessionID, int compania, java.lang.String CUILTitular, java.util.Calendar fecVig, java.lang.String apellido, java.lang.String nombre, java.lang.String parentesco, java.lang.String sexo, java.util.Calendar fecNac, java.lang.String calle, java.lang.String nroCalle, java.lang.String resto, java.lang.String localidad, java.lang.String CP, java.lang.String provincia, java.lang.String telefono, java.lang.String tipoDoc, java.lang.String nroDoc, java.lang.String seccional, int categoria, java.lang.String CUIL, java.util.Calendar FPP, int nacionalidad, int estadoCivil, java.lang.String discapacidad) throws java.rmi.RemoteException;
    public CambioPlanGrupoFamiliarResponseCambioPlanGrupoFamiliarResult cambioPlanGrupoFamiliar(java.lang.String sessionID, int compania, java.lang.String CUILTitular, java.lang.String planMedico, java.util.Calendar fecVig) throws java.rmi.RemoteException;
    public GetCambioPlanResponseGetCambioPlanResult getCambioPlan(java.lang.String sessionID, int compania, java.util.Calendar fecVig) throws java.rmi.RemoteException;
    public GetBeneficiarioResponseGetBeneficiarioResult getBeneficiario(java.lang.String sessionID, int compania, java.util.Calendar fecVig, java.lang.String CUILTitular, int nroIntegrante) throws java.rmi.RemoteException;
    public void testAltaGrupoFamiliar() throws java.rmi.RemoteException;
    public void testAltaBeneficiario() throws java.rmi.RemoteException;
    public void testBajaGrupoFamiliar() throws java.rmi.RemoteException;
    public void testBajaBeneficiario() throws java.rmi.RemoteException;
    public void testModificacionBeneficiario() throws java.rmi.RemoteException;
    public void testCambioPlanGrupoFamiliar() throws java.rmi.RemoteException;
    public void testGetCambioPlan() throws java.rmi.RemoteException;
    public void testGetBeneficiario() throws java.rmi.RemoteException;
}
