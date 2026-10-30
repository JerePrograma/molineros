package ar.com.ospim.compras.requerimientos.beans;

import java.io.Serializable;
import ar.com.ospim.autorizaciones.beans.ReclamoPrestacional;

/**
 * Contexto temporal utilizado para transferir desde Compras hacia
 * Autorizaciones los datos necesarios para iniciar un borrador de
 * Reclamo Prestacional.
 *
 * No representa una relación persistida y no contiene un identificador
 * de Reclamo Prestacional.
 */
public class ReclamoPrestacionalCompraContexto
        implements Serializable {

    private static final long serialVersionUID = 1L;

    /*
     * Valores utilizados por el selector recuperable_sur del RP.
     */
    @Deprecated
    public static final int RECUPERABLE_SUR = 1;
    @Deprecated
    public static final int NO_RECUPERABLE = 2;

    /*
     * Se conserva por compatibilidad binaria con consumidores anteriores.
     * El handoff desde Compras no retorna este valor.
     */
    @Deprecated
    public static final int RECUPERABLE_INTEGRACION = 3;

    private final int idRequerimientoCompra;
    private final String afiliadoCuilTitular;
    private final Integer afiliadoInt;
    private final String usuarioInicio;
    private final long fechaInicio;
    private final String nonce;
    private ReclamoPrestacional reclamoEnEdicion;
    /* Identidad server-side; no son valores de configuracion ni datos HTTP. */
    private java.util.Map<Integer, Integer> detallesOrigen;

    // En un stream anterior el campo ausente toma false, conservando UID 1L.
    private boolean formatoConIdentidadEditor = true;

    /*
     * En Compras estos valores son porcentajes.
     *
     * En Reclamos Prestacionales se utilizan para calcular importes:
     *
     * cargo_ospim = total * cargoOspimPorcentaje / 100
     * cargo_ps    = total * cargoTercerizadoraPorcentaje / 100
     *
     * cargo_ps en RP equivale a Cargo tercerizadora en Compras.
     */
    private final int cargoOspimPorcentaje;
    private final int cargoTercerizadoraPorcentaje;

    private final boolean recupero;
    private final boolean surge;

    /**
     * Constructor conservado para mantener compatibilidad con posibles
     * invocaciones anteriores.
     */
    public ReclamoPrestacionalCompraContexto(
            int idRequerimientoCompra,
            String afiliadoCuilTitular,
            Integer afiliadoInt,
            String usuarioInicio,
            long fechaInicio,
            String nonce) {

        this(
                idRequerimientoCompra,
                afiliadoCuilTitular,
                afiliadoInt,
                usuarioInicio,
                fechaInicio,
                nonce,
                Integer.valueOf(0),
                Integer.valueOf(0),
                false,
                false
        );
    }

    /**
     * Constructor utilizado por el flujo Compras -> Reclamo Prestacional.
     */
    public ReclamoPrestacionalCompraContexto(
            int idRequerimientoCompra,
            String afiliadoCuilTitular,
            Integer afiliadoInt,
            String usuarioInicio,
            long fechaInicio,
            String nonce,
            Integer cargoOspimPorcentaje,
            Integer cargoTercerizadoraPorcentaje,
            boolean recupero,
            boolean surge) {

        this.idRequerimientoCompra =
                idRequerimientoCompra;

        this.afiliadoCuilTitular =
                afiliadoCuilTitular;

        this.afiliadoInt =
                afiliadoInt;

        this.usuarioInicio =
                usuarioInicio;

        this.fechaInicio =
                fechaInicio;

        this.nonce =
                nonce;

        this.cargoOspimPorcentaje =
                obtenerPorcentaje(
                        cargoOspimPorcentaje
                );

        this.cargoTercerizadoraPorcentaje =
                obtenerPorcentaje(
                        cargoTercerizadoraPorcentaje
                );

        this.recupero =
                recupero;

        this.surge =
                surge;
    }

    /** Conserva la identidad del borrador, como la compensacion de la precarga. */
    public void setReclamoEnEdicion(ReclamoPrestacional reclamo) {
        reclamoEnEdicion = reclamo;
        formatoConIdentidadEditor = true;
    }

    public boolean tieneIdentidadEditorDesconocida() {
        return !formatoConIdentidadEditor && reclamoEnEdicion == null;
    }

    public boolean esBorradorEnEdicion(Object reclamo) {
        return reclamoEnEdicion != null && reclamoEnEdicion == reclamo
                && reclamoEnEdicion.getId_reclamo() <= 0;
    }


    public void definirDetallesOrigen(java.util.Map<Integer, Integer> detalles) {
        if (detalles == null || detalles.isEmpty()) {
            throw new IllegalArgumentException("La precarga debe identificar sus detalles de origen.");
        }
        detallesOrigen = new java.util.HashMap<Integer, Integer>(detalles);
    }

    public Integer getIdDetalleOrigen(int idRegistro) {
        return detallesOrigen != null
                ? detallesOrigen.get(Integer.valueOf(idRegistro)) : null;
    }

    public int getCantidadDetallesOrigen() {
        return detallesOrigen != null ? detallesOrigen.size() : 0;
    }

    public int getIdRequerimientoCompra() {
        return idRequerimientoCompra;
    }

    public String getAfiliadoCuilTitular() {
        return afiliadoCuilTitular;
    }

    public Integer getAfiliadoInt() {
        return afiliadoInt;
    }

    public String getAfiliadoIntString() {
        return afiliadoInt != null
                ? String.valueOf(
                afiliadoInt
        )
                : "";
    }

    public String getUsuarioInicio() {
        return usuarioInicio;
    }

    public long getFechaInicio() {
        return fechaInicio;
    }

    public String getNonce() {
        return nonce;
    }

    public int getCargoOspimPorcentaje() {
        return cargoOspimPorcentaje;
    }

    public int getCargoTercerizadoraPorcentaje() {
        return cargoTercerizadoraPorcentaje;
    }

    /**
     * Alias con la denominación utilizada en Reclamos Prestacionales.
     *
     * Cargo Prestadora del RP es Cargo tercerizadora del requerimiento.
     */
    public int getCargoPrestadoraPorcentaje() {
        return cargoTercerizadoraPorcentaje;
    }

    public boolean isRecupero() {
        return recupero;
    }

    public boolean isSurge() {
        return surge;
    }

    /**
     * Resuelve el valor inicial utilizado por recuperable_sur.
     *
     * Reglas:
     *
     * 1 = SUR
     * 2 = No recuperable
     *
     * Recupero no se transforma en Integración durante el handoff
     * desde Compras.
     */
    public int getRecuperableInicial() {
        if (surge) {
            return ar.com.ospim.compras.requerimientos.helper.ConfiguracionComprasHelper.getRecuperableSur();
        }

        return ar.com.ospim.compras.requerimientos.helper.ConfiguracionComprasHelper.getNoRecuperable();
    }

    public boolean coincideNonce(
            String nonceRequest) {

        return nonce != null
                && nonce.equals(
                nonceRequest
        );
    }

    public boolean perteneceAUsuario(
            String usuario) {

        return usuarioInicio != null
                && usuarioInicio.equals(
                usuario
        );
    }

    public boolean estaVigente(
            long ahoraMillis) {

        return fechaInicio > 0L
                && ahoraMillis >= fechaInicio
                && ahoraMillis - fechaInicio
                <= ar.com.ospim.compras.requerimientos.helper.ConfiguracionComprasHelper.getVigenciaContextoMillis();
    }

    private static int obtenerPorcentaje(
            Integer value) {

        return value != null
                ? value.intValue()
                : 0;
    }
}
