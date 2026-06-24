package ar.com.ospim.test;

import ar.com.ospim.compras.WebKeysCompras;
import ar.com.ospim.compras.requerimientos.action.UploadPresupuestosComprasAction;
import ar.com.ospim.compras.requerimientos.beans.PrestadorCotizacion;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompra;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraPresupuesto;

import com.liferay.portal.service.ServiceContext;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class UploadPresupuestosComprasActionTest {

    private static final String[] EXTENSIONES =
            new String[]{
                    ".pdf",
                    ".doc"
            };

    public static void main(
            String[] args)
            throws Exception {

        List<File> archivos =
                new ArrayList<File>();

        try {
            File archivoUno =
                    crearArchivo(
                            "presupuesto-uno",
                            ".pdf"
                    );

            File archivoDos =
                    crearArchivo(
                            "presupuesto-dos",
                            ".doc"
                    );

            File archivoTres =
                    crearArchivo(
                            "presupuesto-tres",
                            ".pdf"
                    );

            archivos.add(
                    archivoUno
            );

            archivos.add(
                    archivoDos
            );

            archivos.add(
                    archivoTres
            );

            assertAcceso();

            assertColeccionValida(
                    archivoUno,
                    archivoDos,
                    archivoTres
            );

            assertPrestadorInvalido(
                    archivoUno
            );

            assertFilaSinArchivo(
                    archivoUno
            );

            assertFilaSinPrestador(
                    archivoUno
            );

            assertCantidadEIndiceManipulados(
                    archivoUno
            );

            assertPathTraversal(
                    archivoUno
            );

            assertLimpiezaParcial(
                    archivoUno,
                    archivoDos,
                    archivoTres
            );

            assertLimpiezaSiFallaRegistro(
                    archivoUno
            );

            assertReactivacionSiFallaEliminar(
                    archivoUno,
                    archivoDos
            );
        } finally {
            for (int i = 0;
                    i < archivos.size();
                    i++) {

                archivos.get(
                        i
                ).delete();
            }
        }
    }

    private static void assertAcceso()
            throws Exception {

        final AccionPrueba accion =
                new AccionPrueba();

        final RequerimientoCompra requerimiento =
                requerimiento(
                        WebKeysCompras
                                .ESTADO_A_COTIZAR
                );

        accion.validarAcceso(
                true,
                requerimiento,
                false
        );

        assertException(
                "usuario sin rol",
                new Ejecucion() {
                    public void ejecutar()
                            throws Exception {

                        accion.validarAcceso(
                                false,
                                requerimiento,
                                false
                        );
                    }
                }
        );

        assertAccesoRechazado(
                accion,
                "PENDIENTE",
                requerimiento(
                        WebKeysCompras
                                .ESTADO_PENDIENTE
                )
        );

        assertAccesoRechazado(
                accion,
                "COTIZADO",
                requerimiento(
                        WebKeysCompras
                                .ESTADO_COTIZADO
                )
        );

        assertAccesoRechazado(
                accion,
                "RECLAMO (RP)",
                requerimiento(
                        WebKeysCompras
                                .ESTADO_RECLAMO_RP
                )
        );

        assertAccesoRechazado(
                accion,
                "ORDEN DE COMPRA",
                requerimiento(
                        WebKeysCompras
                                .ESTADO_ORDEN_COMPRA
                )
        );

        assertAccesoRechazado(
                accion,
                "ANULADO",
                requerimiento(
                        WebKeysCompras
                                .ESTADO_ANULADO
                )
        );

        assertException(
                "modo solo lectura",
                new Ejecucion() {
                    public void ejecutar()
                            throws Exception {

                        accion.validarAcceso(
                                true,
                                requerimiento,
                                true
                        );
                    }
                }
        );
    }

    private static void assertAccesoRechazado(
            final AccionPrueba accion,
            String estado,
            final RequerimientoCompra requerimiento)
            throws Exception {

        assertException(
                "estado fuera de A COTIZAR: "
                        + estado,
                new Ejecucion() {
                    public void ejecutar()
                            throws Exception {

                        accion.validarAcceso(
                                true,
                                requerimiento,
                                false
                        );
                    }
                }
        );
    }

    private static void assertColeccionValida(
            File archivoUno,
            File archivoDos,
            File archivoTres)
            throws Exception {

        final AccionPrueba accion =
                new AccionPrueba();

        List<EntradaPrueba> entradas =
                new ArrayList<EntradaPrueba>();

        entradas.add(
                new EntradaPrueba(
                        0,
                        archivoUno,
                        "uno.pdf",
                        20,
                        "LABEL MANIPULADO"
                )
        );

        entradas.add(
                new EntradaPrueba(
                        1,
                        archivoDos,
                        "dos.doc",
                        21,
                        "OTRO LABEL"
                )
        );

        entradas.add(
                new EntradaPrueba(
                        2,
                        archivoTres,
                        "tres.pdf",
                        20,
                        "TERCER LABEL"
                )
        );

        List<ResultadoPrueba> resultados =
                accion.validar(
                        25,
                        3,
                        entradas,
                        prestadoresEnviados()
                );

        assertInt(
                "colección de tres archivos",
                3,
                resultados.size()
        );

        assertInt(
                "primer prestador",
                20,
                resultados.get(
                        0
                ).idPrestador
        );

        assertInt(
                "segundo prestador",
                21,
                resultados.get(
                        1
                ).idPrestador
        );

        assertInt(
                "prestador repetido permitido",
                20,
                resultados.get(
                        2
                ).idPrestador
        );

        assertString(
                "metadata canónica",
                "PRESTADOR CANONICO - 20-12345678-9",
                resultados.get(
                        0
                ).descripcion
        );

        assertFalse(
                "label del navegador ignorado",
                resultados.get(
                        0
                ).descripcion.contains(
                        "MANIPULADO"
                )
        );

        assertMatches(
                "nombre persistido seguro",
                "PRESUPUESTO-COMPRA-25-PRESTADOR-20-[0-9a-f]{32}\\.pdf",
                resultados.get(
                        0
                ).nombrePersistido
        );

        assertFalse(
                "nombre sin tercerizadora",
                resultados.get(
                        0
                ).nombrePersistido.contains(
                        "OMI"
                )
        );

        assertTrue(
                "título conserva nombre original",
                resultados.get(
                        0
                ).titulo.startsWith(
                        "PRESUPUESTO-COMPRA-25-uno.pdf_"
                )
        );
    }

    private static void assertPrestadorInvalido(
            final File archivo)
            throws Exception {

        final AccionPrueba accion =
                new AccionPrueba();

        assertException(
                "prestador de otro requerimiento",
                new Ejecucion() {
                    public void ejecutar()
                            throws Exception {

                        accion.validar(
                                25,
                                1,
                                entradas(
                                        archivo,
                                        30
                                ),
                                prestadoresEnviados()
                        );
                    }
                }
        );

        assertException(
                "prestador con envío fallido",
                new Ejecucion() {
                    public void ejecutar()
                            throws Exception {

                        accion.validar(
                                25,
                                1,
                                entradas(
                                        archivo,
                                        40
                                ),
                                prestadoresConEnvioFallido()
                        );
                    }
                }
        );
    }

    private static void assertFilaSinArchivo(
            File archivo)
            throws Exception {

        final AccionPrueba accion =
                new AccionPrueba();

        final List<EntradaPrueba> entradas =
                entradas(
                        archivo,
                        20
                );

        entradas.set(
                0,
                new EntradaPrueba(
                        0,
                        null,
                        "uno.pdf",
                        20,
                        ""
                )
        );

        assertException(
                "fila sin archivo",
                new Ejecucion() {
                    public void ejecutar()
                            throws Exception {

                        accion.validar(
                                25,
                                1,
                                entradas,
                                prestadoresEnviados()
                        );
                    }
                }
        );
    }

    private static void assertFilaSinPrestador(
            final File archivo)
            throws Exception {

        final AccionPrueba accion =
                new AccionPrueba();

        assertException(
                "fila sin prestador",
                new Ejecucion() {
                    public void ejecutar()
                            throws Exception {

                        accion.validar(
                                25,
                                1,
                                entradas(
                                        archivo,
                                        0
                                ),
                                prestadoresEnviados()
                        );
                    }
                }
        );
    }

    private static void assertCantidadEIndiceManipulados(
            final File archivo)
            throws Exception {

        final AccionPrueba accion =
                new AccionPrueba();

        assertException(
                "cantidad manipulada",
                new Ejecucion() {
                    public void ejecutar()
                            throws Exception {

                        accion.validarCantidad(
                                WebKeysCompras
                                        .MAX_PRESUPUESTOS_POR_CARGA
                                        + 1
                        );
                    }
                }
        );

        final List<EntradaPrueba> entradas =
                entradas(
                        archivo,
                        20
                );

        entradas.set(
                0,
                new EntradaPrueba(
                        2,
                        archivo,
                        "uno.pdf",
                        20,
                        ""
                )
        );

        assertException(
                "índice manipulado",
                new Ejecucion() {
                    public void ejecutar()
                            throws Exception {

                        accion.validar(
                                25,
                                1,
                                entradas,
                                prestadoresEnviados()
                        );
                    }
                }
        );
    }

    private static void assertPathTraversal(
            final File archivo)
            throws Exception {

        final AccionPrueba accion =
                new AccionPrueba();

        final List<EntradaPrueba> entradas =
                entradas(
                        archivo,
                        20
                );

        entradas.set(
                0,
                new EntradaPrueba(
                        0,
                        archivo,
                        "..\\temporal\\presupuesto.pdf",
                        20,
                        ""
                )
        );

        assertException(
                "path traversal",
                new Ejecucion() {
                    public void ejecutar()
                            throws Exception {

                        accion.validar(
                                25,
                                1,
                                entradas,
                                prestadoresEnviados()
                        );
                    }
                }
        );
    }

    private static void assertLimpiezaParcial(
            File archivoUno,
            File archivoDos,
            File archivoTres)
            throws Exception {

        final AccionPrueba accion =
                new AccionPrueba();

        accion.validar(
                25,
                3,
                tresEntradas(
                        archivoUno,
                        archivoDos,
                        archivoTres
                ),
                prestadoresEnviados()
        );

        accion.fallarEnCreacion =
                2;

        assertException(
                "falla parcial de Document Library",
                new Ejecucion() {
                    public void ejecutar()
                            throws Exception {

                        accion.guardarValidados();
                    }
                }
        );

        assertInt(
                "solo primer archivo creado",
                1,
                accion.creados.size()
        );

        assertInt(
                "primera asociación registrada",
                1,
                accion.asociacionesRegistradas.size()
        );

        assertInt(
                "asociación compensada",
                1,
                accion.asociacionesBaja.size()
        );

        assertInt(
                "primer archivo limpiado",
                1,
                accion.eliminados.size()
        );

        assertString(
                "limpieza exacta",
                accion.creados.get(
                        0
                ),
                accion.eliminados.get(
                        0
                )
        );

        assertInt(
                "sin reactivación cuando la limpieza física funciona",
                0,
                accion.asociacionesReactivadas.size()
        );
    }

    private static void assertLimpiezaSiFallaRegistro(
            File archivo)
            throws Exception {

        final AccionPrueba accion =
                new AccionPrueba();

        accion.validar(
                25,
                1,
                entradas(
                        archivo,
                        20
                ),
                prestadoresEnviados()
        );

        accion.fallarEnRegistro =
                1;

        assertException(
                "falla al registrar asociación",
                new Ejecucion() {
                    public void ejecutar()
                            throws Exception {

                        accion.guardarValidados();
                    }
                }
        );

        assertInt(
                "documento creado antes de fallar SQL",
                1,
                accion.creados.size()
        );

        assertInt(
                "sin asociación completada",
                0,
                accion.asociacionesRegistradas.size()
        );

        assertInt(
                "documento huérfano eliminado",
                1,
                accion.eliminados.size()
        );

        assertString(
                "se elimina exactamente el documento creado",
                accion.creados.get(
                        0
                ),
                accion.eliminados.get(
                        0
                )
        );
    }

    private static void assertReactivacionSiFallaEliminar(
            File archivoUno,
            File archivoDos)
            throws Exception {

        final AccionPrueba accion =
                new AccionPrueba();

        List<EntradaPrueba> entradas =
                new ArrayList<EntradaPrueba>();

        entradas.add(
                new EntradaPrueba(
                        0,
                        archivoUno,
                        "uno.pdf",
                        20,
                        ""
                )
        );

        entradas.add(
                new EntradaPrueba(
                        1,
                        archivoDos,
                        "dos.doc",
                        21,
                        ""
                )
        );

        accion.validar(
                25,
                2,
                entradas,
                prestadoresEnviados()
        );

        accion.fallarEnCreacion =
                2;

        accion.fallarEliminacion =
                true;

        assertException(
                "falla de eliminación durante compensación",
                new Ejecucion() {
                    public void ejecutar()
                            throws Exception {

                        accion.guardarValidados();
                    }
                }
        );

        assertInt(
                "una asociación dada de baja",
                1,
                accion.asociacionesBaja.size()
        );

        assertInt(
                "asociación reactivada al conservarse el documento",
                1,
                accion.asociacionesReactivadas.size()
        );

        assertInt(
                "el documento no figura como eliminado",
                0,
                accion.eliminados.size()
        );

        assertInt(
                "la misma asociación fue reactivada",
                accion.asociacionesBaja.get(
                        0
                ).intValue(),
                accion.asociacionesReactivadas.get(
                        0
                ).intValue()
        );
    }

    private static RequerimientoCompra requerimiento(
            int estado) {

        RequerimientoCompra requerimiento =
                new RequerimientoCompra();

        requerimiento.setIdRequerimientoCompra(
                25
        );

        requerimiento.setEstado(
                estado
        );

        return requerimiento;
    }

    private static List<PrestadorCotizacion>
    prestadoresEnviados() {

        List<PrestadorCotizacion> prestadores =
                new ArrayList<PrestadorCotizacion>();

        prestadores.add(
                prestador(
                        20,
                        "Prestador Canonico",
                        "20-12345678-9"
                )
        );

        prestadores.add(
                prestador(
                        21,
                        "Segundo Prestador",
                        "30-87654321-0"
                )
        );

        return prestadores;
    }

    private static List<PrestadorCotizacion>
    prestadoresConEnvioFallido() {

        List<PrestadorCotizacion> prestadores =
                prestadoresEnviados();

        PrestadorCotizacion fallido =
                prestador(
                        40,
                        "Prestador Fallido",
                        "30-11111111-1"
                );

        fallido.setEstadoEnvio(
                WebKeysCompras
                        .ENVIO_ERROR
        );

        prestadores.add(
                fallido
        );

        return prestadores;
    }

    private static PrestadorCotizacion prestador(
            int id,
            String descripcion,
            String cuit) {

        PrestadorCotizacion prestador =
                new PrestadorCotizacion();

        prestador.setIdPrestador(
                id
        );

        prestador.setDescripcion(
                descripcion
        );

        prestador.setCuit(
                cuit
        );

        prestador.setEstadoEnvio(
                WebKeysCompras
                        .ENVIO_ENVIADO
        );

        return prestador;
    }

    private static List<EntradaPrueba> entradas(
            File archivo,
            int idPrestador) {

        List<EntradaPrueba> entradas =
                new ArrayList<EntradaPrueba>();

        entradas.add(
                new EntradaPrueba(
                        0,
                        archivo,
                        "uno.pdf",
                        idPrestador,
                        ""
                )
        );

        return entradas;
    }

    private static List<EntradaPrueba> tresEntradas(
            File archivoUno,
            File archivoDos,
            File archivoTres) {

        List<EntradaPrueba> entradas =
                new ArrayList<EntradaPrueba>();

        entradas.add(
                new EntradaPrueba(
                        0,
                        archivoUno,
                        "uno.pdf",
                        20,
                        ""
                )
        );

        entradas.add(
                new EntradaPrueba(
                        1,
                        archivoDos,
                        "dos.doc",
                        21,
                        ""
                )
        );

        entradas.add(
                new EntradaPrueba(
                        2,
                        archivoTres,
                        "tres.pdf",
                        20,
                        ""
                )
        );

        return entradas;
    }

    private static File crearArchivo(
            String prefijo,
            String extension)
            throws Exception {

        File archivo =
                File.createTempFile(
                        prefijo,
                        extension
                );

        FileOutputStream output =
                new FileOutputStream(
                        archivo
                );

        try {
            output.write(
                    new byte[]{
                            1,
                            2,
                            3
                    }
            );
        } finally {
            output.close();
        }

        return archivo;
    }

    private static void assertException(
            String descripcion,
            Ejecucion ejecucion)
            throws Exception {

        try {
            ejecucion.ejecutar();
        } catch (Exception e) {
            return;
        }

        throw new AssertionError(
                descripcion
                        + ": se esperaba rechazo."
        );
    }

    private static void assertMatches(
            String descripcion,
            String expresion,
            String actual) {

        if (actual == null
                || !actual.matches(
                        expresion
                )) {

            throw new AssertionError(
                    descripcion
                            + ": expresión="
                            + expresion
                            + ", actual="
                            + actual
            );
        }
    }

    private static void assertString(
            String descripcion,
            String esperado,
            String actual) {

        if (esperado == null
                ? actual != null
                : !esperado.equals(
                        actual
                )) {

            throw new AssertionError(
                    descripcion
                            + ": esperado="
                            + esperado
                            + ", actual="
                            + actual
            );
        }
    }

    private static void assertInt(
            String descripcion,
            int esperado,
            int actual) {

        if (esperado != actual) {
            throw new AssertionError(
                    descripcion
                            + ": esperado="
                            + esperado
                            + ", actual="
                            + actual
            );
        }
    }

    private static void assertTrue(
            String descripcion,
            boolean actual) {

        if (!actual) {
            throw new AssertionError(
                    descripcion
            );
        }
    }

    private static void assertFalse(
            String descripcion,
            boolean actual) {

        assertTrue(
                descripcion,
                !actual
        );
    }

    private interface Ejecucion {

        void ejecutar()
                throws Exception;
    }

    private static class EntradaPrueba {

        private final int indice;
        private final File archivo;
        private final String nombreOriginal;
        private final int idPrestador;
        private final String labelNavegador;

        private EntradaPrueba(
                int indice,
                File archivo,
                String nombreOriginal,
                int idPrestador,
                String labelNavegador) {

            this.indice =
                    indice;

            this.archivo =
                    archivo;

            this.nombreOriginal =
                    nombreOriginal;

            this.idPrestador =
                    idPrestador;

            this.labelNavegador =
                    labelNavegador;
        }
    }

    private static class ResultadoPrueba {

        private final int indice;
        private final int idPrestador;
        private final String nombrePersistido;
        private final String titulo;
        private final String descripcion;

        private ResultadoPrueba(
                int indice,
                int idPrestador,
                String nombrePersistido,
                String titulo,
                String descripcion) {

            this.indice =
                    indice;

            this.idPrestador =
                    idPrestador;

            this.nombrePersistido =
                    nombrePersistido;

            this.titulo =
                    titulo;

            this.descripcion =
                    descripcion;
        }
    }

    private static class AccionPrueba
            extends UploadPresupuestosComprasAction {

        private List<PresupuestoValidado> validados;

        private final List<String> creados =
                new ArrayList<String>();

        private final List<String> eliminados =
                new ArrayList<String>();

        private final List<Integer> asociacionesRegistradas =
                new ArrayList<Integer>();

        private final List<Integer> asociacionesBaja =
                new ArrayList<Integer>();

        private final List<Integer> asociacionesReactivadas =
                new ArrayList<Integer>();

        private int fallarEnCreacion;
        private int fallarEnRegistro;

        private int creacionesIntentadas;
        private int registrosIntentados;
        private int siguienteIdAsociacion =
                1;

        private boolean fallarEliminacion;

        public void validarAcceso(
                boolean tieneRol,
                RequerimientoCompra requerimiento,
                boolean soloLectura)
                throws Exception {

            validarAccesoCarga(
                    tieneRol,
                    requerimiento,
                    soloLectura
            );
        }

        public void validarCantidad(
                int cantidad)
                throws Exception {

            validarCantidadPresupuestos(
                    cantidad
            );
        }

        public List<ResultadoPrueba> validar(
                int idRequerimiento,
                int cantidad,
                List<EntradaPrueba> entradas,
                List<PrestadorCotizacion> prestadores)
                throws Exception {

            List<PresupuestoEntrada> entradasAction =
                    new ArrayList<PresupuestoEntrada>();

            for (int i = 0;
                    i < entradas.size();
                    i++) {

                EntradaPrueba entrada =
                        entradas.get(
                                i
                        );

                entradasAction.add(
                        crearEntradaPresupuesto(
                                entrada.indice,
                                entrada.archivo,
                                entrada.nombreOriginal,
                                entrada.idPrestador
                        )
                );

                if (entrada.labelNavegador == null) {
                    throw new AssertionError(
                            "El label de prueba no puede ser null."
                    );
                }
            }

            validados =
                    validarPresupuestos(
                            idRequerimiento,
                            cantidad,
                            entradasAction,
                            prestadores,
                            5120000L,
                            EXTENSIONES
                    );

            List<ResultadoPrueba> resultados =
                    new ArrayList<ResultadoPrueba>();

            for (int i = 0;
                    i < validados.size();
                    i++) {

                PresupuestoValidado validado =
                        validados.get(
                                i
                        );

                resultados.add(
                        new ResultadoPrueba(
                                validado.getIndice(),
                                validado.getIdPrestador(),
                                validado.getNombrePersistido(),
                                validado.getTitulo(),
                                validado
                                        .getDescripcionPrestador()
                        )
                );
            }

            return resultados;
        }

        public void guardarValidados()
                throws Exception {

            guardarPresupuestosValidados(
                    25,
                    validados,
                    1L,
                    2L,
                    "test",
                    null
            );
        }

        protected DocumentoPresupuestoCreado crearArchivoPresupuesto(
                long userId,
                long folderId,
                PresupuestoValidado presupuesto,
                ServiceContext serviceContext)
                throws Exception {

            creacionesIntentadas++;

            if (fallarEnCreacion > 0
                    && creacionesIntentadas
                            == fallarEnCreacion) {

                throw new Exception(
                        "Falla simulada de Document Library."
                );
            }

            String nombre =
                    presupuesto
                            .getNombrePersistido();

            creados.add(
                    nombre
            );

            return new DocumentoPresupuestoCreadoPrueba(
                    1L,
                    folderId,
                    1000L
                            + creacionesIntentadas,
                    "uuid-prueba-"
                            + creacionesIntentadas,
                    nombre,
                    presupuesto.getTitulo()
            );
        }

        protected RequerimientoCompraPresupuesto
        registrarAsociacionPresupuesto(
                int idRequerimientoCompra,
                PresupuestoValidado presupuesto,
                DocumentoPresupuestoCreado documento,
                String usuario)
                throws Exception {

            registrosIntentados++;

            if (fallarEnRegistro > 0
                    && registrosIntentados
                            == fallarEnRegistro) {

                throw new Exception(
                        "Falla simulada al registrar la asociación SQL."
                );
            }

            int idAsociacion =
                    siguienteIdAsociacion++;

            RequerimientoCompraPresupuesto asociacion =
                    new RequerimientoCompraPresupuesto();

            asociacion.setIdRequerimientoPresupuesto(
                    Integer.valueOf(
                            idAsociacion
                    )
            );

            asociacion.setIdRequerimiento(
                    Integer.valueOf(
                            idRequerimientoCompra
                    )
            );

            asociacion.setIdPrestador(
                    Integer.valueOf(
                            presupuesto.getIdPrestador()
                    )
            );

            asociacion.setDlGroupId(
                    Long.valueOf(
                            documento.getGroupId()
                    )
            );

            asociacion.setDlFolderId(
                    Long.valueOf(
                            documento.getFolderId()
                    )
            );

            asociacion.setDlFileEntryId(
                    Long.valueOf(
                            documento.getFileEntryId()
                    )
            );

            asociacion.setDlFileUuid(
                    documento.getUuid()
            );

            asociacion.setNombreOriginal(
                    presupuesto.getNombreOriginal()
            );

            asociacion.setNombrePersistido(
                    documento.getNombre()
            );

            asociacion.setTitulo(
                    documento.getTitulo()
            );

            asociacion.setDescripcionPrestador(
                    presupuesto.getDescripcionPrestador()
            );

            asociacionesRegistradas.add(
                    Integer.valueOf(
                            idAsociacion
                    )
            );

            return asociacion;
        }

        protected boolean darDeBajaAsociacionPresupuesto(
                int idRequerimientoPresupuesto,
                int idRequerimientoCompra,
                String usuario)
                throws Exception {

            asociacionesBaja.add(
                    Integer.valueOf(
                            idRequerimientoPresupuesto
                    )
            );

            return true;
        }

        protected boolean reactivarAsociacionPresupuesto(
                int idRequerimientoPresupuesto,
                int idRequerimientoCompra)
                throws Exception {

            asociacionesReactivadas.add(
                    Integer.valueOf(
                            idRequerimientoPresupuesto
                    )
            );

            return true;
        }

        protected void eliminarArchivoPresupuesto(
                long folderId,
                String nombre)
                throws Exception {

            if (fallarEliminacion) {
                throw new Exception(
                        "Falla simulada al eliminar el documento."
                );
            }

            eliminados.add(
                    nombre
            );
        }
    }

    private static class DocumentoPresupuestoCreadoPrueba
            extends UploadPresupuestosComprasAction.DocumentoPresupuestoCreado {

        private DocumentoPresupuestoCreadoPrueba(
                long groupId,
                long folderId,
                long fileEntryId,
                String uuid,
                String nombre,
                String titulo) {

            super(
                    groupId,
                    folderId,
                    fileEntryId,
                    uuid,
                    nombre,
                    titulo
            );
        }
    }

    private UploadPresupuestosComprasActionTest() {
    }
}
