package ar.com.ospim.compras.requerimientos.helper;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class ImportesCompraHelper {
    public static final int ESCALA_IMPORTE = 2;
    public static final RoundingMode REDONDEO_IMPORTE = RoundingMode.HALF_UP;

    private ImportesCompraHelper() {
    }

    public static BigDecimal normalizarImporte(
            BigDecimal value) {

        if (value == null) {
            return null;
        }

        return value.setScale(
                ESCALA_IMPORTE,
                REDONDEO_IMPORTE
        );
    }

    public static BigDecimal calcularPrecioTotal(
            Integer cantidad,
            BigDecimal precioUnitario) {

        if (cantidad == null
                || precioUnitario == null) {

            return null;
        }

        return normalizarImporte(
                precioUnitario.multiply(
                        new BigDecimal(cantidad.intValue())
                )
        );
    }

    public static String formatearImporte(
            BigDecimal value) {

        BigDecimal normalizado =
                normalizarImporte(value);

        return normalizado != null
                ? normalizado.toPlainString()
                : "";
    }
}
