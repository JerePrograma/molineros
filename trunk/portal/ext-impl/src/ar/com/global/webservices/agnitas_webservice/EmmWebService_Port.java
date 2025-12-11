/**
 * EmmWebService_Port.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package ar.com.global.webservices.agnitas_webservice;

public interface EmmWebService_Port extends java.rmi.Remote {
    //public int newEmailMailing(java.lang.String in0, java.lang.String in1, java.lang.String in2, java.lang.String in3, int in4, java.lang.String[] in5, int in6, int in7, java.lang.String in8, java.lang.String in9, java.lang.String in10, int in11, int in12) throws java.rmi.RemoteException;
	public int newEmailMailing(java.lang.String in0, java.lang.String in1, java.lang.String in2, java.lang.String in3, int in4, StringArrayType in5, int in6, int in7, java.lang.String in8, java.lang.String in9, java.lang.String in10, int in11, int in12) throws java.rmi.RemoteException;
    public int newEmailMailingWithReply(java.lang.String in0, java.lang.String in1, java.lang.String in2, java.lang.String in3, int in4, java.lang.String[] in5, int in6, int in7, java.lang.String in8, java.lang.String in9, java.lang.String in10, java.lang.String in11, int in12, int in13) throws java.rmi.RemoteException;
    public boolean updateEmailMailing(java.lang.String in0, java.lang.String in1, int in2, java.lang.String in3, java.lang.String in4, int in5, StringArrayType in6, int in7, java.lang.String in8, java.lang.String in9, java.lang.String in10, java.lang.String in11, int in12, int in13) throws java.rmi.RemoteException;
    public int insertContent(java.lang.String in0, java.lang.String in1, int in2, java.lang.String in3, java.lang.String in4, int in5, int in6) throws java.rmi.RemoteException;
    public int deleteContent(java.lang.String in0, java.lang.String in1, int in2) throws java.rmi.RemoteException;
    public int sendMailing(java.lang.String in0, java.lang.String in1, int in2, java.lang.String in3, int in4, int in5, int in6) throws java.rmi.RemoteException;
    public int addMailinglist(java.lang.String in0, java.lang.String in1, java.lang.String in2, java.lang.String in3) throws java.rmi.RemoteException;
    public int deleteMailinglist(java.lang.String in0, java.lang.String in1, int in2) throws java.rmi.RemoteException;
    public int addSubscriber(java.lang.String in0, java.lang.String in1, boolean in2, java.lang.String in3, boolean in4, StringArrayType in5, StringArrayType in6) throws java.rmi.RemoteException;
    public SubscriberData getSubscriber(java.lang.String in0, java.lang.String in1, int in2) throws java.rmi.RemoteException;
    public int findSubscriber(java.lang.String in0, java.lang.String in1, java.lang.String in2, java.lang.String in3) throws java.rmi.RemoteException;
    public int setSubscriberBinding(java.lang.String in0, java.lang.String in1, int in2, int in3, int in4, int in5, java.lang.String in6, java.lang.String in7, int in8) throws java.rmi.RemoteException;
    public int deleteSubscriber(java.lang.String in0, java.lang.String in1, int in2) throws java.rmi.RemoteException;
    public java.lang.String getSubscriberBinding(java.lang.String in0, java.lang.String in1, int in2, int in3, int in4) throws java.rmi.RemoteException;
    public boolean updateSubscriber(java.lang.String in0, java.lang.String in1, int in2, java.lang.String[] in3, java.lang.String[] in4) throws java.rmi.RemoteException;
}
