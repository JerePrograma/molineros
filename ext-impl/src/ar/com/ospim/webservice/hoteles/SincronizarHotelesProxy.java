package ar.com.ospim.webservice.hoteles;

public class SincronizarHotelesProxy implements SincronizarHoteles {
  private String _endpoint = null;
  private SincronizarHoteles sincronizarHoteles = null;
  
  public SincronizarHotelesProxy() {
    _initSincronizarHotelesProxy();
  }
  
  public SincronizarHotelesProxy(String endpoint) {
    _endpoint = endpoint;
    _initSincronizarHotelesProxy();
  }
  
  private void _initSincronizarHotelesProxy() {
    try {
      sincronizarHoteles = (new SincronizarHotelesServiceLocator()).getSincronizarHoteles();
      if (sincronizarHoteles != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)sincronizarHoteles)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)sincronizarHoteles)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (sincronizarHoteles != null)
      ((javax.xml.rpc.Stub)sincronizarHoteles)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public SincronizarHoteles getSincronizarHoteles() {
    if (sincronizarHoteles == null)
      _initSincronizarHotelesProxy();
    return sincronizarHoteles;
  }
  
  public java.lang.String saludo(java.lang.String nombre) throws java.rmi.RemoteException{
    if (sincronizarHoteles == null)
      _initSincronizarHotelesProxy();
    return sincronizarHoteles.saludo(nombre);
  }
  
  public ReciboHotelWS[] sincronizarRecibos(ReciboHotelWS[] recibos) throws java.rmi.RemoteException{
    if (sincronizarHoteles == null)
      _initSincronizarHotelesProxy();
    return sincronizarHoteles.sincronizarRecibos(recibos);
  }
  
  public FacturaWS[] sincronizarFacturas(FacturaWS[] facturas) throws java.rmi.RemoteException{
    if (sincronizarHoteles == null)
      _initSincronizarHotelesProxy();
    return sincronizarHoteles.sincronizarFacturas(facturas);
  }
  
  
}