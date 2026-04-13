
/**
 * IMembershipService_DeleteMember_ValidationFault_FaultMessage.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.6.3  Built on : Jun 27, 2015 (11:17:49 BST)
 */

package ar.com.ospim.webservice.prevencion;

public class IMembershipService_DeleteMember_ValidationFault_FaultMessage extends java.lang.Exception{

    private static final long serialVersionUID = 1466793092700L;
    
    private ar.com.ospim.webservice.prevencion.MembershipServiceStub.ValidationFault11 faultMessage;

    
        public IMembershipService_DeleteMember_ValidationFault_FaultMessage() {
            super("IMembershipService_DeleteMember_ValidationFault_FaultMessage");
        }

        public IMembershipService_DeleteMember_ValidationFault_FaultMessage(java.lang.String s) {
           super(s);
        }

        public IMembershipService_DeleteMember_ValidationFault_FaultMessage(java.lang.String s, java.lang.Throwable ex) {
          super(s, ex);
        }

        public IMembershipService_DeleteMember_ValidationFault_FaultMessage(java.lang.Throwable cause) {
            super(cause);
        }
    

    public void setFaultMessage(ar.com.ospim.webservice.prevencion.MembershipServiceStub.ValidationFault11 msg){
       faultMessage = msg;
    }
    
    public ar.com.ospim.webservice.prevencion.MembershipServiceStub.ValidationFault11 getFaultMessage(){
       return faultMessage;
    }
}
    