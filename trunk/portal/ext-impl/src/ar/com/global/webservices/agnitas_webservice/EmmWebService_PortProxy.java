package ar.com.global.webservices.agnitas_webservice;

public class EmmWebService_PortProxy implements EmmWebService_Port {
  private String _endpoint = null;
  private EmmWebService_Port emmWebService_Port = null;
  
  public EmmWebService_PortProxy() {
    _initEmmWebService_PortProxy();
  }
  
  public EmmWebService_PortProxy(String endpoint) {
    _endpoint = endpoint;
    _initEmmWebService_PortProxy();
  }
  
  private void _initEmmWebService_PortProxy() {
    try {
      emmWebService_Port = (new EmmWebService_PortServiceLocator()).getemm_webservice();
      if (emmWebService_Port != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)emmWebService_Port)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)emmWebService_Port)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (emmWebService_Port != null)
      ((javax.xml.rpc.Stub)emmWebService_Port)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public EmmWebService_Port getEmmWebService_Port() {
    if (emmWebService_Port == null)
      _initEmmWebService_PortProxy();
    return emmWebService_Port;
  }
  
  //public int newEmailMailing(java.lang.String in0, java.lang.String in1, java.lang.String in2, java.lang.String in3, int in4, java.lang.String[] in5, int in6, int in7, java.lang.String in8, java.lang.String in9, java.lang.String in10, int in11, int in12) throws java.rmi.RemoteException{
  public int newEmailMailing(java.lang.String in0, java.lang.String in1, java.lang.String in2, java.lang.String in3, int in4, StringArrayType in5, int in6, int in7, java.lang.String in8, java.lang.String in9, java.lang.String in10, int in11, int in12) throws java.rmi.RemoteException{
    if (emmWebService_Port == null)
      _initEmmWebService_PortProxy();
    return emmWebService_Port.newEmailMailing(in0, in1, in2, in3, in4, in5, in6, in7, in8, in9, in10, in11, in12);
  }
  
  public int newEmailMailingWithReply(java.lang.String in0, java.lang.String in1, java.lang.String in2, java.lang.String in3, int in4, java.lang.String[] in5, int in6, int in7, java.lang.String in8, java.lang.String in9, java.lang.String in10, java.lang.String in11, int in12, int in13) throws java.rmi.RemoteException{
    if (emmWebService_Port == null)
      _initEmmWebService_PortProxy();
    return emmWebService_Port.newEmailMailingWithReply(in0, in1, in2, in3, in4, in5, in6, in7, in8, in9, in10, in11, in12, in13);
  }
  
  public boolean updateEmailMailing(java.lang.String in0, java.lang.String in1, int in2, java.lang.String in3, java.lang.String in4, int in5, StringArrayType in6, int in7, java.lang.String in8, java.lang.String in9, java.lang.String in10, java.lang.String in11, int in12, int in13) throws java.rmi.RemoteException{
    if (emmWebService_Port == null)
      _initEmmWebService_PortProxy();
    return emmWebService_Port.updateEmailMailing(in0, in1, in2, in3, in4, in5, in6, in7, in8, in9, in10, in11, in12, in13);
  }
  
  public int insertContent(java.lang.String in0, java.lang.String in1, int in2, java.lang.String in3, java.lang.String in4, int in5, int in6) throws java.rmi.RemoteException{
    if (emmWebService_Port == null)
      _initEmmWebService_PortProxy();
    return emmWebService_Port.insertContent(in0, in1, in2, in3, in4, in5, in6);
  }
  
  public int deleteContent(java.lang.String in0, java.lang.String in1, int in2) throws java.rmi.RemoteException{
    if (emmWebService_Port == null)
      _initEmmWebService_PortProxy();
    return emmWebService_Port.deleteContent(in0, in1, in2);
  }
  
  public int sendMailing(java.lang.String in0, java.lang.String in1, int in2, java.lang.String in3, int in4, int in5, int in6) throws java.rmi.RemoteException{
    if (emmWebService_Port == null)
      _initEmmWebService_PortProxy();
    return emmWebService_Port.sendMailing(in0, in1, in2, in3, in4, in5, in6);
  }
  
  public int addMailinglist(java.lang.String in0, java.lang.String in1, java.lang.String in2, java.lang.String in3) throws java.rmi.RemoteException{
    if (emmWebService_Port == null)
      _initEmmWebService_PortProxy();
    return emmWebService_Port.addMailinglist(in0, in1, in2, in3);
  }
  
  public int deleteMailinglist(java.lang.String in0, java.lang.String in1, int in2) throws java.rmi.RemoteException{
    if (emmWebService_Port == null)
      _initEmmWebService_PortProxy();
    return emmWebService_Port.deleteMailinglist(in0, in1, in2);
  }
  
  public int addSubscriber(java.lang.String in0, java.lang.String in1, boolean in2, java.lang.String in3, boolean in4, StringArrayType in5, StringArrayType in6) throws java.rmi.RemoteException{
    if (emmWebService_Port == null)
      _initEmmWebService_PortProxy();
    return emmWebService_Port.addSubscriber(in0, in1, in2, in3, in4, in5, in6);
  }
  
  public SubscriberData getSubscriber(java.lang.String in0, java.lang.String in1, int in2) throws java.rmi.RemoteException{
    if (emmWebService_Port == null)
      _initEmmWebService_PortProxy();
    return emmWebService_Port.getSubscriber(in0, in1, in2);
  }
  
  public int findSubscriber(java.lang.String in0, java.lang.String in1, java.lang.String in2, java.lang.String in3) throws java.rmi.RemoteException{
    if (emmWebService_Port == null)
      _initEmmWebService_PortProxy();
    return emmWebService_Port.findSubscriber(in0, in1, in2, in3);
  }
  
  public int setSubscriberBinding(java.lang.String in0, java.lang.String in1, int in2, int in3, int in4, int in5, java.lang.String in6, java.lang.String in7, int in8) throws java.rmi.RemoteException{
    if (emmWebService_Port == null)
      _initEmmWebService_PortProxy();
    return emmWebService_Port.setSubscriberBinding(in0, in1, in2, in3, in4, in5, in6, in7, in8);
  }
  
  public int deleteSubscriber(java.lang.String in0, java.lang.String in1, int in2) throws java.rmi.RemoteException{
    if (emmWebService_Port == null)
      _initEmmWebService_PortProxy();
    return emmWebService_Port.deleteSubscriber(in0, in1, in2);
  }
  
  public java.lang.String getSubscriberBinding(java.lang.String in0, java.lang.String in1, int in2, int in3, int in4) throws java.rmi.RemoteException{
    if (emmWebService_Port == null)
      _initEmmWebService_PortProxy();
    return emmWebService_Port.getSubscriberBinding(in0, in1, in2, in3, in4);
  }
  
  public boolean updateSubscriber(java.lang.String in0, java.lang.String in1, int in2, java.lang.String[] in3, java.lang.String[] in4) throws java.rmi.RemoteException{
    if (emmWebService_Port == null)
      _initEmmWebService_PortProxy();
    return emmWebService_Port.updateSubscriber(in0, in1, in2, in3, in4);
  }
  
  
}