
/**
 * MembershipServiceCallbackHandler.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.6.3  Built on : Jun 27, 2015 (11:17:49 BST)
 */

    package ar.com.ospim.webservice.prevencion;

    /**
     *  MembershipServiceCallbackHandler Callback class, Users can extend this class and implement
     *  their own receiveResult and receiveError methods.
     */
    public abstract class MembershipServiceCallbackHandler{



    protected Object clientData;

    /**
    * User can pass in any object that needs to be accessed once the NonBlocking
    * Web service call is finished and appropriate method of this CallBack is called.
    * @param clientData Object mechanism by which the user can pass in user data
    * that will be avilable at the time this callback is called.
    */
    public MembershipServiceCallbackHandler(Object clientData){
        this.clientData = clientData;
    }

    /**
    * Please use this constructor if you don't want to set any clientData
    */
    public MembershipServiceCallbackHandler(){
        this.clientData = null;
    }

    /**
     * Get the client data
     */

     public Object getClientData() {
        return clientData;
     }

        
           /**
            * auto generated Axis2 call back method for deleteMember method
            * override this method for handling normal response from deleteMember operation
            */
           public void receiveResultdeleteMember(
                    ar.com.ospim.webservice.prevencion.MembershipServiceStub.DeleteMemberResponse8 result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from deleteMember operation
           */
            public void receiveErrordeleteMember(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for addMember method
            * override this method for handling normal response from addMember operation
            */
           public void receiveResultaddMember(
                    ar.com.ospim.webservice.prevencion.MembershipServiceStub.AddMemberResponse9 result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from addMember operation
           */
            public void receiveErroraddMember(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for createFamilyGroup method
            * override this method for handling normal response from createFamilyGroup operation
            */
           public void receiveResultcreateFamilyGroup(
                    ar.com.ospim.webservice.prevencion.MembershipServiceStub.CreateFamilyGroupResponse7 result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from createFamilyGroup operation
           */
            public void receiveErrorcreateFamilyGroup(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for deleteFamilyGroup method
            * override this method for handling normal response from deleteFamilyGroup operation
            */
           public void receiveResultdeleteFamilyGroup(
                    ar.com.ospim.webservice.prevencion.MembershipServiceStub.DeleteFamilyGroupResponse10 result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from deleteFamilyGroup operation
           */
            public void receiveErrordeleteFamilyGroup(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for changeHealthPlan method
            * override this method for handling normal response from changeHealthPlan operation
            */
           public void receiveResultchangeHealthPlan(
                    ar.com.ospim.webservice.prevencion.MembershipServiceStub.ChangeHealthPlanResponse6 result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from changeHealthPlan operation
           */
            public void receiveErrorchangeHealthPlan(java.lang.Exception e) {
            }
                


    }
    