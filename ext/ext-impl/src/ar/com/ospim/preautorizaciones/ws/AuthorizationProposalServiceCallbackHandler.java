
/**
 * AuthorizationProposalServiceCallbackHandler.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.6.4  Built on : Dec 28, 2015 (10:03:39 GMT)
 */

    package ar.com.ospim.preautorizaciones.ws;

    /**
     *  AuthorizationProposalServiceCallbackHandler Callback class, Users can extend this class and implement
     *  their own receiveResult and receiveError methods.
     */
    public abstract class AuthorizationProposalServiceCallbackHandler{



    protected Object clientData;

    /**
    * User can pass in any object that needs to be accessed once the NonBlocking
    * Web service call is finished and appropriate method of this CallBack is called.
    * @param clientData Object mechanism by which the user can pass in user data
    * that will be avilable at the time this callback is called.
    */
    public AuthorizationProposalServiceCallbackHandler(Object clientData){
        this.clientData = clientData;
    }

    /**
    * Please use this constructor if you don't want to set any clientData
    */
    public AuthorizationProposalServiceCallbackHandler(){
        this.clientData = null;
    }

    /**
     * Get the client data
     */

     public Object getClientData() {
        return clientData;
     }

        
           /**
            * auto generated Axis2 call back method for createAuthorizationProposal method
            * override this method for handling normal response from createAuthorizationProposal operation
            */
           public void receiveResultcreateAuthorizationProposal(
                    ar.com.ospim.preautorizaciones.ws.AuthorizationProposalServiceStub.CreateAuthorizationProposalResponse4 result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from createAuthorizationProposal operation
           */
            public void receiveErrorcreateAuthorizationProposal(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for sendAdditionalInformation method
            * override this method for handling normal response from sendAdditionalInformation operation
            */
           public void receiveResultsendAdditionalInformation(
                    ar.com.ospim.preautorizaciones.ws.AuthorizationProposalServiceStub.SendAdditionalInformationResponse3 result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from sendAdditionalInformation operation
           */
            public void receiveErrorsendAdditionalInformation(java.lang.Exception e) {
            }
                


    }
    