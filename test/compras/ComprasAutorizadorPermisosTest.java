import ar.com.ospim.compras.requerimientos.helper.ConfiguracionComprasHelper;
import ar.com.ospim.compras.WebKeysCompras;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompra;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraDetalle;
import java.util.Date;

/* Prueba las reglas productivas sin base de datos ni sesion del portal. */
public class ComprasAutorizadorPermisosTest {
    private static int checks;

    public static void main(String[] args) {
        ConfiguracionComprasDatosFixture.instalar();
        int[] estados = new int[] {
                ConfiguracionComprasHelper.getEstadoPendiente(),
                ConfiguracionComprasHelper.getEstadoACotizar(),
                ConfiguracionComprasHelper.getEstadoCotizado(),
                ConfiguracionComprasHelper.getEstadoReclamoRP(),
                ConfiguracionComprasHelper.getEstadoOrdenCompra(),
                ConfiguracionComprasHelper.getEstadoParaAutorizar(),
                ConfiguracionComprasHelper.getEstadoAutorizado(),
                ConfiguracionComprasHelper.getEstadoAnulado()
        };
        for (int i = 0; i < estados.length; i++) {
            int estado = estados[i];
            RequerimientoCompra actual = nuevo(estado);
            if (estado == ConfiguracionComprasHelper.getEstadoAnulado()) {
                actual.setBajaFecha(new Date());
            }
            verificar("observacion en estado " + estado,
                    actual.puedeEditarObservacionInterna());
            verificar("solo autorizar desde 6, estado " + estado,
                    WebKeysCompras.validarTransicionEstado(
                            estado, ConfiguracionComprasHelper.getEstadoAutorizado())
                            == (estado == ConfiguracionComprasHelper.getEstadoParaAutorizar()));
            if (estado != ConfiguracionComprasHelper.getEstadoAnulado()) {
                actual.setBajaFecha(new Date());
                verificar("baja fuera de 99, estado " + estado,
                        !actual.puedeEditarObservacionInterna());
            }
        }
        RequerimientoCompra sinId = new RequerimientoCompra();
        sinId.setEstado(ConfiguracionComprasHelper.getEstadoAnulado());
        sinId.setBajaFecha(new Date());
        verificar("observacion exige id persistido",
                !sinId.puedeEditarObservacionInterna());
        verificar("estado invalido 0", !nuevo(0).puedeEditarObservacionInterna());
        verificar("estado invalido 98", !nuevo(98).puedeEditarObservacionInterna());
        verificarFlujoPorSector(estados);
        System.out.println("COMPRAS_AUTORIZADOR_REGLAS_OFFLINE_OK checks=" + checks);
    }

    private static void verificarFlujoPorSector(int[] estados) {
        String[] sectores = new String[] { "SISTEMAS", "RRHH", "PRESTACIONES MEDICAS" };
        for (int s = 0; s < sectores.length; s++) {
            String sector = sectores[s];
            boolean empresa = s < 2;
            for (int i = 0; i < estados.length; i++) {
                int estado = estados[i];
                String caso = sector + " estado " + estado;
                boolean puedeEnviar = estado == (empresa
                        ? ConfiguracionComprasHelper.getEstadoPendiente()
                        : ConfiguracionComprasHelper.getEstadoReclamoRP());
                verificar(caso + " enviar a autorizar desde estado permitido",
                        WebKeysCompras.validarTransicionEstado(
                                estado, ConfiguracionComprasHelper.getEstadoParaAutorizar(), sector)
                                == puedeEnviar);
                verificar(caso + " autorizar solo desde PARA AUTORIZAR",
                        WebKeysCompras.validarTransicionEstado(
                                estado, ConfiguracionComprasHelper.getEstadoAutorizado(), sector)
                                == (estado == ConfiguracionComprasHelper.getEstadoParaAutorizar()));
                verificar(caso + " OC exige operacion especifica",
                        !WebKeysCompras.validarTransicionEstado(
                                estado, ConfiguracionComprasHelper.getEstadoOrdenCompra(), sector));
                verificar(caso + " cambio repetido rechazado",
                        !WebKeysCompras.validarTransicionEstado(estado, estado, sector));
                for (int datos = 0; datos < 4; datos++) {
                    boolean detalles = (datos & 1) != 0;
                    boolean cotizaciones = (datos & 2) != 0;
                    verificar(caso + " condiciones envio " + datos,
                            WebKeysCompras.puedeEnviarAAutorizar(
                                    estado, sector, detalles, cotizaciones)
                                    == (puedeEnviar && (!empresa || detalles && cotizaciones)));
                }
                RequerimientoCompra actual = nuevo(estado);
                actual.setSectorDescripcion(sector);
                verificar(caso + " cotizacion a prestadores solo circuito medico",
                        actual.puedeEnviarACotizar()
                                == (!empresa && estado == ConfiguracionComprasHelper.getEstadoPendiente()));
                verificar(caso + " OC no disponible sin detalles",
                        !actual.puedePasarAOrdenCompra(true));
                actual.getDetalles().add(new RequerimientoCompraDetalle(Integer.valueOf(10)));
                verificar(caso + " OC corporativa solo desde AUTORIZADO",
                        actual.puedePasarAOrdenCompra(true)
                                == (empresa && estado == ConfiguracionComprasHelper.getEstadoAutorizado()));
                verificar(caso + " OC no disponible sin cotizacion",
                        !actual.puedePasarAOrdenCompra(false));
                actual.setBajaFecha(new Date());
                verificar(caso + " OC no disponible con baja",
                        !actual.puedePasarAOrdenCompra(true));
            }
        }
    }

    private static RequerimientoCompra nuevo(int estado) {
        RequerimientoCompra requerimiento = new RequerimientoCompra(Integer.valueOf(40));
        requerimiento.setEstado(estado);
        return requerimiento;
    }

    private static void verificar(String caso, boolean condicion) {
        checks++;
        if (!condicion) {
            throw new AssertionError(caso);
        }
    }
}
