import ar.com.ospim.compras.WebKeysCompras;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompra;
import java.util.Date;

/* Prueba las reglas productivas sin base de datos ni sesion del portal. */
public class ComprasAutorizadorPermisosTest {
    private static int checks;

    public static void main(String[] args) {
        int[] estados = new int[] {
                WebKeysCompras.ESTADO_PENDIENTE,
                WebKeysCompras.ESTADO_A_COTIZAR,
                WebKeysCompras.ESTADO_COTIZADO,
                WebKeysCompras.ESTADO_RECLAMO_RP,
                WebKeysCompras.ESTADO_ORDEN_COMPRA,
                WebKeysCompras.ESTADO_PARA_AUTORIZAR,
                WebKeysCompras.ESTADO_AUTORIZADO,
                WebKeysCompras.ESTADO_ANULADO
        };
        for (int i = 0; i < estados.length; i++) {
            int estado = estados[i];
            RequerimientoCompra actual = nuevo(estado);
            if (estado == WebKeysCompras.ESTADO_ANULADO) {
                actual.setBajaFecha(new Date());
            }
            verificar("observacion en estado " + estado,
                    actual.puedeEditarObservacionInterna());
            verificar("solo autorizar desde 6, estado " + estado,
                    WebKeysCompras.validarTransicionEstado(
                            estado, WebKeysCompras.ESTADO_AUTORIZADO)
                            == (estado == WebKeysCompras.ESTADO_PARA_AUTORIZAR));
            if (estado != WebKeysCompras.ESTADO_ANULADO) {
                actual.setBajaFecha(new Date());
                verificar("baja fuera de 99, estado " + estado,
                        !actual.puedeEditarObservacionInterna());
            }
        }
        RequerimientoCompra sinId = new RequerimientoCompra();
        sinId.setEstado(WebKeysCompras.ESTADO_ANULADO);
        sinId.setBajaFecha(new Date());
        verificar("observacion exige id persistido",
                !sinId.puedeEditarObservacionInterna());
        verificar("estado invalido 0", !nuevo(0).puedeEditarObservacionInterna());
        verificar("estado invalido 98", !nuevo(98).puedeEditarObservacionInterna());
        System.out.println("COMPRAS_AUTORIZADOR_REGLAS_OFFLINE_OK checks=" + checks);
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
