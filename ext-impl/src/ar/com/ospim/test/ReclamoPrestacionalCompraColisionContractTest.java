package ar.com.ospim.test;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;

/**
 * Contrato ejecutable de la recuperacion de colisiones del editor RP.
 */
public final class ReclamoPrestacionalCompraColisionContractTest {

    private static final Charset LATIN1 =
            Charset.forName("ISO-8859-1");

    public static void main(String[] args) throws Exception {
        String helper = leer(
                "ext-impl/src/ar/com/ospim/compras/requerimientos/helper/"
                        + "ReclamoPrestacionalCompraPrecargaHelper.java"
        );
        String iniciar = leer(
                "ext-impl/src/ar/com/ospim/compras/requerimientos/action/"
                        + "IniciarReclamoPrestacionalCompraAction.java"
        );
        String editar = leer(
                "ext-impl/src/ar/com/ospim/autorizaciones/action/"
                        + "EditarReclamosEntryAction.java"
        );
        String wrapper = leer(
                "ext-web/docroot/html/portlet/autorizaciones/"
                        + "reclamos_prestacionales/"
                        + "editar_reclamosprestacionales_entry.jsp"
        );
        String view = leer(
                "ext-web/docroot/html/portlet/autorizaciones/"
                        + "reclamos_prestacionales/view_reclamo.jsp"
        );

        String registrar = extraerMetodo(
                helper,
                "public static RegistroContextoBorrador "
                        + "registrarContextoBorrador("
        );
        String descartar = extraerMetodo(
                helper,
                "public static int descartarEdicionActual("
        );
        String validarRecuperacion = extraerMetodo(
                helper,
                "private void validar("
        );
        String limpiar = extraerMetodo(
                helper,
                "private static void limpiarEstadoEditorSincronizado("
        );
        String urlRecuperacion = extraerMetodo(
                iniciar,
                "private String construirURLRecuperacionEdicion("
        );

        // Caso 1: una sesion limpia registra y conserva el flujo feliz.
        contiene(
                registrar,
                "sesion limpia registra el contexto nuevo",
                "return RegistroContextoBorrador.registrado();"
        );
        antes(
                iniciar,
                "registrar antes de precargar",
                ".registrarContextoBorrador(",
                ".precargar("
        );

        // Caso 2: un ADD activo no se reemplaza ni se limpia.
        contiene(
                registrar,
                "la colision conserva el objeto exacto",
                "new RecuperacionEdicion("
        );
        noContiene(
                registrar,
                "registrar no limpia un editor ocupado",
                "limpiarEstadoEditorSincronizado("
        );

        // Caso 3: un RP persistido se renderiza desde sesion, no desde BD.
        contiene(
                editar,
                "recuperacion fuerza busqueda cero",
                "int idReclamoDeBuscador = recuperacionEdicion != null"
        );
        contiene(
                editar,
                "recuperacion persistida conserva EDIT",
                "recuperacionEdicion.getIdReclamoActual() > 0"
        );
        String seleccionReclamo = extraerEntre(
                editar,
                "if ( idReclamoDeBuscador==0 || cmd.equals(Constants.UPDATE) )",
                "if (cmd.equals(WebKeysAutorizaciones.CUENTA))"
        );
        contiene(
                seleccionReclamo,
                "id cero selecciona el objeto de sesion",
                "session.getAttribute(WebKeysAutorizaciones."
                        + "RECLAMO_PRESTACION_EN_EDICION)"
        );

        // Casos 4 y 5: descartar solo limpia memoria, sin borrar en BD.
        contiene(
                descartar,
                "descarte usa limpieza del editor",
                "limpiarEstadoEditorSincronizado("
        );
        noContiene(
                descartar,
                "descarte no invoca persistencia",
                "ServiceUtil"
        );
        contiene(
                limpiar,
                "limpia cabecera de sesion",
                ".RECLAMO_PRESTACION_EN_EDICION"
        );
        contiene(
                limpiar,
                "limpia prestaciones asociadas",
                ".LISTADO_PRESTACIONES_ASOCIADAS_RECLAMOS_EN_SESION"
        );
        contiene(
                limpiar,
                "limpia cuenta temporal",
                ".RECLAMO_PRESTACION_CUENTA_SELECT"
        );

        // Caso 6: Compras B no pisa el borrador activo de Compras A.
        antes(
                registrar,
                "solo reutiliza el mismo requerimiento vigente",
                "contextoAnterior.getIdRequerimientoCompra()",
                "new RecuperacionEdicion("
        );
        contiene(
                registrar,
                "otro requerimiento crea recuperacion",
                ".RECUPERACION_RECLAMO_PRESTACIONAL_COMPRA"
        );

        // Caso 7: descartar, limpiar y luego revalidar desde origen.
        antes(
                iniciar,
                "descarte antes de recargar requerimiento",
                ".descartarEdicionActual(",
                "obtenerRequerimientoActivo("
        );
        antes(
                iniciar,
                "requerimiento antes de validar alta",
                "obtenerRequerimientoActivo(",
                "validarRequerimientoParaCrearReclamo("
        );

        // Caso 8: doble click del mismo requerimiento es idempotente.
        contiene(
                registrar,
                "mismo requerimiento reutiliza nonce activo",
                "RegistroContextoBorrador.reutilizado("
        );
        contiene(
                iniciar,
                "accion navega al contexto reutilizado",
                "registroContexto.getNonceContextoActual()"
        );

        // Caso 9: un contexto vencido no autoriza ADD ni se borra al verlo.
        contiene(
                urlRecuperacion,
                "contexto vencido abre consulta",
                "recuperacion.tieneContextoCompraNoVigente("
        );
        contiene(
                urlRecuperacion,
                "contexto vencido usa VIEW",
                "Constants.VIEW"
        );
        contiene(
                view,
                "vista protege la recuperacion de limpieza implicita",
                "StringUtils.checkEmpty(nonceRecuperacionReclamo)"
        );
        String contextoInvalidoVista = extraerEntre(
                view,
                "} else if (!reclamoPersistido",
                "Calendar prestacionFecha"
        );
        noContiene(
                contextoInvalidoVista,
                "contexto vencido no limpia el editor",
                "removeAttribute("
        );

        // Caso 10: IDs manipulados no determinan lo que se descarta.
        contiene(
                descartar,
                "id de request coincide con recuperacion",
                "recuperacion.getIdRequerimientoCompra()"
        );
        contiene(
                validarRecuperacion,
                "valida identidad de cabecera",
                "!= reclamoEsperado"
        );
        contiene(
                validarRecuperacion,
                "valida identidad de contexto",
                "!= contextoCompraEsperado"
        );

        // Caso 11: el token se consume y un segundo POST falla cerrado.
        contiene(
                limpiar,
                "consume recuperacion al descartar",
                ".RECUPERACION_RECLAMO_PRESTACIONAL_COMPRA"
        );
        contiene(
                helper,
                "sin recuperacion no hay fallback",
                "La recuperacion de la edicion ya no esta disponible."
        );

        // Caso 12: el guardado final conserva reserva y vinculo atomico.
        antes(
                editar,
                "reserva antes de crear y vincular",
                ".reservarCreacion(",
                ".crearYVincular("
        );

        // Caso 13: colision funcional sin ERROR; fallos tecnicos con ERROR.
        String ramaColision = extraerEntre(
                iniciar,
                "if (registroContexto.isColision())",
                "if (!registroContexto.isRegistrado())"
        );
        contiene(
                ramaColision,
                "colision se registra como WARN",
                "_log.warn("
        );
        noContiene(
                ramaColision,
                "colision no genera ERROR",
                "_log.error("
        );
        contiene(
                iniciar,
                "fallo tecnico conserva ERROR",
                "_log.error("
        );

        // UX legacy: continuar, descarte explicito y volver.
        contiene(wrapper, "boton continuar", "value=\"Continuar ");
        contiene(wrapper, "boton descartar", "value=\"Descartar ");
        contiene(wrapper, "boton volver", "Volver al requerimiento");
        contiene(wrapper, "confirmacion explicita", "return confirm(");

        System.out.println(
                "CONTRATO_COLISION_RECLAMO_PRESTACIONAL_COMPRA_OK"
        );
    }

    private static String leer(String ruta) throws Exception {
        File archivo = new File(ruta);

        if (!archivo.isFile()) {
            throw new AssertionError("No existe el archivo: " + ruta);
        }

        return new String(
                Files.readAllBytes(archivo.toPath()),
                LATIN1
        );
    }

    private static String extraerMetodo(
            String fuente,
            String firma) {

        int inicio = fuente.indexOf(firma);

        if (inicio < 0) {
            throw new AssertionError(
                    "No se encontro la firma: " + firma
            );
        }

        int apertura = fuente.indexOf('{', inicio);
        int nivel = 0;

        for (int i = apertura; i < fuente.length(); i++) {
            char actual = fuente.charAt(i);

            if (actual == '{') {
                nivel++;
            } else if (actual == '}') {
                nivel--;

                if (nivel == 0) {
                    return fuente.substring(inicio, i + 1);
                }
            }
        }

        throw new AssertionError("Metodo sin cierre: " + firma);
    }

    private static String extraerEntre(
            String fuente,
            String inicioTexto,
            String finTexto) {

        int inicio = fuente.indexOf(inicioTexto);
        int fin = fuente.indexOf(finTexto, inicio + inicioTexto.length());

        if (inicio < 0 || fin < 0) {
            throw new AssertionError(
                    "No se pudo extraer el bloque entre "
                            + inicioTexto + " y " + finTexto
            );
        }

        return fuente.substring(inicio, fin);
    }

    private static void contiene(
            String fuente,
            String caso,
            String esperado) {

        if (fuente.indexOf(esperado) < 0) {
            throw new AssertionError(
                    caso + ": no contiene " + esperado
            );
        }
    }

    private static void noContiene(
            String fuente,
            String caso,
            String inesperado) {

        if (fuente.indexOf(inesperado) >= 0) {
            throw new AssertionError(
                    caso + ": contiene " + inesperado
            );
        }
    }

    private static void antes(
            String fuente,
            String caso,
            String primero,
            String segundo) {

        int posicionPrimero = fuente.indexOf(primero);
        int posicionSegundo = fuente.indexOf(segundo);

        if (posicionPrimero < 0
                || posicionSegundo < 0
                || posicionPrimero >= posicionSegundo) {

            throw new AssertionError(
                    caso + ": orden invalido entre "
                            + primero + " y " + segundo
            );
        }
    }
}
