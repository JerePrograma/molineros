import ar.com.ospim.compras.WebKeysCompras;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompra;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraDetalle;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraEstado;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraSector;
import ar.com.ospim.compras.requerimientos.beans.TipoPrestacionCompra;
import ar.com.ospim.compras.requerimientos.helper.EditarRequerimientoCompraHelper;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.Arrays;

/** Regresion focalizada sin DB, correo, RP ni Document Library. */
public class ComprasConfiguracionTest {
    private static int verificaciones;

    private static void verificar(boolean condicion, String caso) {
        if (!condicion) { throw new AssertionError(caso); }
        verificaciones++;
    }

    private static RequerimientoCompra requerimiento(int ospim, int tercero) {
        RequerimientoCompra r = new RequerimientoCompra();
        r.setIdSector(Integer.valueOf(1));
        r.setRequiereAfiliado(true);
        r.setAfiliadoCuilTitular("20000000001");
        r.setAfiliadoInt(Integer.valueOf(0));
        r.setIdTercerizadora("PRUEBA");
        r.setCargoOspim(Integer.valueOf(ospim));
        r.setCargoTercerizadora(Integer.valueOf(tercero));
        return r;
    }

    public static void main(String[] args) throws Exception {
        int[] porcentajes = new int[] {0, 1, 50, 99, 100};
        EditarRequerimientoCompraHelper helper = new EditarRequerimientoCompraHelper();
        Method validar = EditarRequerimientoCompraHelper.class.getDeclaredMethod(
                "validarRequerimientoParaGuardar", new Class[] {RequerimientoCompra.class});
        validar.setAccessible(true);
        for (int i = 0; i < porcentajes.length; i++) {
            int porcentaje = porcentajes[i];
            for (int surge = 0; surge < 2; surge++) {
                RequerimientoCompra r = requerimiento(100 - porcentaje, porcentaje);
                r.setSurge(surge == 1);
                r.setRecupero(porcentaje == 0);
                validar.invoke(helper, new Object[] {r});
                verificar(r.isRecupero() == (porcentaje > 0), "Recupero deriva de cargos");
                verificar(r.isSurge() == (surge == 1), "SURGE independiente");
            }
        }
        int[][] invalidos = new int[][] {{-1,101},{101,-1},{40,40},{0,0}};
        for (int i = 0; i < invalidos.length; i++) {
            boolean rechazo = false;
            try {
                validar.invoke(helper, new Object[] {requerimiento(invalidos[i][0],invalidos[i][1])});
            } catch (InvocationTargetException e) {
                rechazo = e.getCause() != null && e.getCause().getMessage() != null;
            }
            verificar(rechazo, "Servidor rechaza rango/suma incorrectos");
        }
        RequerimientoCompra r = requerimiento(0,100);
        r.setRecupero(Boolean.FALSE);
        verificar(r.getRecupero().booleanValue(), "Caso 1186 no reproduce drift");
        r.setCargoTercerizadora(Integer.valueOf(0));
        verificar(!r.isRecupero(), "Cambio de cargo actualiza recupero sin setter");

        RequerimientoCompraSector sector = new RequerimientoCompraSector(Integer.valueOf(42), "Nombre editable", Boolean.TRUE);
        sector.setTipoItem(RequerimientoCompraDetalle.TIPO_ITEM_NOMENCLADOR);
        sector.setNomencladores(Arrays.asList(new Integer[] {Integer.valueOf(10),Integer.valueOf(14),Integer.valueOf(44)}));
        TipoPrestacionCompra tipo = new TipoPrestacionCompra();
        tipo.setId(Integer.valueOf(73));
        tipo.setIdSector(Integer.valueOf(42));
        tipo.setNomencladores(sector.getNomencladores());
        for (int i = 0; i < sector.getNomencladores().size(); i++) {
            verificar(WebKeysCompras.esNomencladorValidoParaTipoPrestacionCompras(sector,tipo,sector.getNomencladores().get(i).intValue()), "N:M admite cada tipo configurado");
        }
        verificar(!WebKeysCompras.esNomencladorValidoParaTipoPrestacionCompras(sector,tipo,9), "N:M rechaza tipo ausente");
        sector.setDescripcion("Otro nombre");
        verificar(WebKeysCompras.esNomencladorValidoParaTipoPrestacionCompras(sector,tipo,44), "Nombre no decide admision");
        tipo.setIdSector(Integer.valueOf(43));
        verificar(!WebKeysCompras.esNomencladorValidoParaTipoPrestacionCompras(sector,tipo,14), "Tipo de otro sector rechazado");
        verificar(!WebKeysCompras.puedePasarAOrdenCompra(WebKeysCompras.ESTADO_PENDIENTE,sector,true,true), "OC directa requiere capacidad");
        sector.setPermiteOrdenCompraDirecta(true);
        verificar(WebKeysCompras.puedePasarAOrdenCompra(WebKeysCompras.ESTADO_PENDIENTE,sector,true,true), "OC directa configurada");
        verificar(!WebKeysCompras.puedePasarAOrdenCompra(WebKeysCompras.ESTADO_PENDIENTE,sector,true,false), "OC requiere documento");
        verificar(!WebKeysCompras.puedeGenerarReclamoPrestacional(sector), "RP sin contrato rechazado");
        sector.setSectorReclamoPrestacional("FARMACIA");
        verificar(WebKeysCompras.puedeGenerarReclamoPrestacional(sector), "RP usa contrato configurado");
        verificar(!new RequerimientoCompra().puedeEnviarACotizar(), "Sin configuracion no habilitar envio");

        RequerimientoCompraDetalle detalle = new RequerimientoCompraDetalle();
        detalle.setCantidad(Integer.valueOf(3));
        detalle.setPrecioUnitarioEstimado(new BigDecimal("10"));
        verificar(detalle.getPrecioTotalEstimado().compareTo(new BigDecimal("30")) == 0, "Total automatico");
        detalle.setPrecioTotalEstimado(new BigDecimal("25.50"));
        verificar(detalle.getPrecioTotalEstimado().compareTo(new BigDecimal("25.50")) == 0, "Total manual distinto del producto");
        detalle.setCantidad(Integer.valueOf(4));
        verificar(detalle.getPrecioTotalEstimado().compareTo(new BigDecimal("40")) == 0, "Cambio de cantidad recalcula");
        detalle.setPrecioUnitarioEstimado(new BigDecimal("12"));
        verificar(detalle.getPrecioTotalEstimado().compareTo(new BigDecimal("48")) == 0, "Cambio de unitario recalcula");
        RequerimientoCompraEstado estado = new RequerimientoCompraEstado();
        estado.setDescripcion("A COTIZAR");
        estado.setDescripcionVisual("ENVIADO A COTIZAR");
        verificar("ENVIADO A COTIZAR".equals(estado.getDescripcion()), "Presentacion historica estado 2");
        estado.setDescripcionVisual(null);
        verificar("A COTIZAR".equals(estado.getDescripcion()), "Descripcion del catalogo como respaldo");
        System.out.println("OK: " + verificaciones + " verificaciones funcionales sin servicios externos.");
    }
}
