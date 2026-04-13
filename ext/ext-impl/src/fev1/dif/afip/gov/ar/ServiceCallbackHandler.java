
/**
 * ServiceCallbackHandler.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.7.4  Built on : Oct 21, 2016 (10:47:34 BST)
 */

    package fev1.dif.afip.gov.ar;

    /**
     *  ServiceCallbackHandler Callback class, Users can extend this class and implement
     *  their own receiveResult and receiveError methods.
     */
    public abstract class ServiceCallbackHandler{



    protected Object clientData;

    /**
    * User can pass in any object that needs to be accessed once the NonBlocking
    * Web service call is finished and appropriate method of this CallBack is called.
    * @param clientData Object mechanism by which the user can pass in user data
    * that will be avilable at the time this callback is called.
    */
    public ServiceCallbackHandler(Object clientData){
        this.clientData = clientData;
    }

    /**
    * Please use this constructor if you don't want to set any clientData
    */
    public ServiceCallbackHandler(){
        this.clientData = null;
    }

    /**
     * Get the client data
     */

     public Object getClientData() {
        return clientData;
     }

        
           /**
            * auto generated Axis2 call back method for fECAEASinMovimientoConsultar method
            * override this method for handling normal response from fECAEASinMovimientoConsultar operation
            */
           public void receiveResultfECAEASinMovimientoConsultar(
                    fev1.dif.afip.gov.ar.ServiceStub.FECAEASinMovimientoConsultarResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from fECAEASinMovimientoConsultar operation
           */
            public void receiveErrorfECAEASinMovimientoConsultar(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for fEParamGetTiposIva method
            * override this method for handling normal response from fEParamGetTiposIva operation
            */
           public void receiveResultfEParamGetTiposIva(
                    fev1.dif.afip.gov.ar.ServiceStub.FEParamGetTiposIvaResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from fEParamGetTiposIva operation
           */
            public void receiveErrorfEParamGetTiposIva(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for fECAEASinMovimientoInformar method
            * override this method for handling normal response from fECAEASinMovimientoInformar operation
            */
           public void receiveResultfECAEASinMovimientoInformar(
                    fev1.dif.afip.gov.ar.ServiceStub.FECAEASinMovimientoInformarResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from fECAEASinMovimientoInformar operation
           */
            public void receiveErrorfECAEASinMovimientoInformar(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for fECompUltimoAutorizado method
            * override this method for handling normal response from fECompUltimoAutorizado operation
            */
           public void receiveResultfECompUltimoAutorizado(
                    fev1.dif.afip.gov.ar.ServiceStub.FECompUltimoAutorizadoResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from fECompUltimoAutorizado operation
           */
            public void receiveErrorfECompUltimoAutorizado(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for fEParamGetPtosVenta method
            * override this method for handling normal response from fEParamGetPtosVenta operation
            */
           public void receiveResultfEParamGetPtosVenta(
                    fev1.dif.afip.gov.ar.ServiceStub.FEParamGetPtosVentaResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from fEParamGetPtosVenta operation
           */
            public void receiveErrorfEParamGetPtosVenta(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for fECAESolicitar method
            * override this method for handling normal response from fECAESolicitar operation
            */
           public void receiveResultfECAESolicitar(
                    fev1.dif.afip.gov.ar.ServiceStub.FECAESolicitarResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from fECAESolicitar operation
           */
            public void receiveErrorfECAESolicitar(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for fECAEAConsultar method
            * override this method for handling normal response from fECAEAConsultar operation
            */
           public void receiveResultfECAEAConsultar(
                    fev1.dif.afip.gov.ar.ServiceStub.FECAEAConsultarResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from fECAEAConsultar operation
           */
            public void receiveErrorfECAEAConsultar(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for fEDummy method
            * override this method for handling normal response from fEDummy operation
            */
           public void receiveResultfEDummy(
                    fev1.dif.afip.gov.ar.ServiceStub.FEDummyResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from fEDummy operation
           */
            public void receiveErrorfEDummy(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for fEParamGetTiposConcepto method
            * override this method for handling normal response from fEParamGetTiposConcepto operation
            */
           public void receiveResultfEParamGetTiposConcepto(
                    fev1.dif.afip.gov.ar.ServiceStub.FEParamGetTiposConceptoResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from fEParamGetTiposConcepto operation
           */
            public void receiveErrorfEParamGetTiposConcepto(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for fEParamGetTiposMonedas method
            * override this method for handling normal response from fEParamGetTiposMonedas operation
            */
           public void receiveResultfEParamGetTiposMonedas(
                    fev1.dif.afip.gov.ar.ServiceStub.FEParamGetTiposMonedasResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from fEParamGetTiposMonedas operation
           */
            public void receiveErrorfEParamGetTiposMonedas(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for fECompTotXRequest method
            * override this method for handling normal response from fECompTotXRequest operation
            */
           public void receiveResultfECompTotXRequest(
                    fev1.dif.afip.gov.ar.ServiceStub.FECompTotXRequestResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from fECompTotXRequest operation
           */
            public void receiveErrorfECompTotXRequest(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for fECAEARegInformativo method
            * override this method for handling normal response from fECAEARegInformativo operation
            */
           public void receiveResultfECAEARegInformativo(
                    fev1.dif.afip.gov.ar.ServiceStub.FECAEARegInformativoResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from fECAEARegInformativo operation
           */
            public void receiveErrorfECAEARegInformativo(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for fEParamGetTiposOpcional method
            * override this method for handling normal response from fEParamGetTiposOpcional operation
            */
           public void receiveResultfEParamGetTiposOpcional(
                    fev1.dif.afip.gov.ar.ServiceStub.FEParamGetTiposOpcionalResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from fEParamGetTiposOpcional operation
           */
            public void receiveErrorfEParamGetTiposOpcional(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for fEParamGetCotizacion method
            * override this method for handling normal response from fEParamGetCotizacion operation
            */
           public void receiveResultfEParamGetCotizacion(
                    fev1.dif.afip.gov.ar.ServiceStub.FEParamGetCotizacionResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from fEParamGetCotizacion operation
           */
            public void receiveErrorfEParamGetCotizacion(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for fEParamGetTiposPaises method
            * override this method for handling normal response from fEParamGetTiposPaises operation
            */
           public void receiveResultfEParamGetTiposPaises(
                    fev1.dif.afip.gov.ar.ServiceStub.FEParamGetTiposPaisesResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from fEParamGetTiposPaises operation
           */
            public void receiveErrorfEParamGetTiposPaises(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for fECAEASolicitar method
            * override this method for handling normal response from fECAEASolicitar operation
            */
           public void receiveResultfECAEASolicitar(
                    fev1.dif.afip.gov.ar.ServiceStub.FECAEASolicitarResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from fECAEASolicitar operation
           */
            public void receiveErrorfECAEASolicitar(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for fEParamGetTiposTributos method
            * override this method for handling normal response from fEParamGetTiposTributos operation
            */
           public void receiveResultfEParamGetTiposTributos(
                    fev1.dif.afip.gov.ar.ServiceStub.FEParamGetTiposTributosResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from fEParamGetTiposTributos operation
           */
            public void receiveErrorfEParamGetTiposTributos(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for fECompConsultar method
            * override this method for handling normal response from fECompConsultar operation
            */
           public void receiveResultfECompConsultar(
                    fev1.dif.afip.gov.ar.ServiceStub.FECompConsultarResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from fECompConsultar operation
           */
            public void receiveErrorfECompConsultar(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for fEParamGetTiposCbte method
            * override this method for handling normal response from fEParamGetTiposCbte operation
            */
           public void receiveResultfEParamGetTiposCbte(
                    fev1.dif.afip.gov.ar.ServiceStub.FEParamGetTiposCbteResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from fEParamGetTiposCbte operation
           */
            public void receiveErrorfEParamGetTiposCbte(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for fEParamGetTiposDoc method
            * override this method for handling normal response from fEParamGetTiposDoc operation
            */
           public void receiveResultfEParamGetTiposDoc(
                    fev1.dif.afip.gov.ar.ServiceStub.FEParamGetTiposDocResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from fEParamGetTiposDoc operation
           */
            public void receiveErrorfEParamGetTiposDoc(java.lang.Exception e) {
            }
                


    }
    