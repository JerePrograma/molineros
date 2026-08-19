package ar.com.ospim.test;

import ar.com.ospim.compras.WebKeysCompras;
import ar.com.ospim.compras.requerimientos.beans.NotificacionCotizacionResultado;
import ar.com.ospim.compras.requerimientos.beans.PrestadorCotizacion;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompra;
import ar.com.ospim.compras.requerimientos.beans.RequerimientoCompraDetalle;
import ar.com.ospim.compras.requerimientos.service.NotificarCotizacionPrestadorServiceImpl;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class NotificarCotizacionPrestadorServiceImplTest {

    private static final String ENVIADO = "ENVIADO";
    private static final String ERROR = "ERROR";
    private static final String EMAIL_INVALIDO = "EMAIL_INVALIDO";

    public static void main(String[] args) throws Exception {
        assertReservaFalseOmiteSinEnviarNiFinalizar();
        assertEmailReservadoNuloConTemporalNoEnvia();
        assertEmailReservadoInvalidoConTemporalNoEnvia();
        assertErrorEnvioFinalizaError();
        assertEnvioAceptadoYEnviadoPersistidoCuentaEnviado();
        assertEnvioAceptadoYFinalizacionFalseCuentaError();
        assertEnvioAceptadoYFinalizacionLanzaCuentaError();
        assertUsaEmailReservadoDespuesDeReservar();
        assertCuerpoUsaDescripcion();
        assertPdfSeGeneraUnaVezYSeAdjunta();
        assertFalloPdfNoReservaNiEnvia();
        assertHistoricoSinOrdenMedicaConservaPdfSolo();
        assertOrdenMedicaSeRecuperaAntesDeReservarYSeAdjunta();
        assertFalloOrdenMedicaNoReservaNiEnvia();
        assertFirmasOrdenMedica();
        assertNoMantieneOrdenMedicaEnEstadoCompartido();
        assertDiagnosticoSinCompatiblesSector();
        assertDiagnosticoTodosBloqueadosPorEstadoPrevio();
    }

    private static void assertEmailReservadoNuloConTemporalNoEnvia()
            throws Exception {

        ServicioPrueba service =
                servicioConPrestador("listado@ospim.org.ar", null);

        NotificacionCotizacionResultado resultado =
                service.notificarPrestadores(10, "tester", 1L);

        assertInt("enviados", 1, resultado.getEnviados());
        assertInt("errores", 0, resultado.getErrores());
        assertInt("emails invalidos", 0, resultado.getEmailsInvalidos());
        assertInt("advertencias QA", 1, resultado.getEmailsRealesInvalidosAdvertidos());
        assertInt("mails enviados", 1, service.mailsEnviados);
        assertEstadoFinal(service, 0, ENVIADO);
        assertEvento(service, 0, "reservar");
        assertEvento(service, 1, "enviar");
        assertEvento(service, 2, "finalizar:" + ENVIADO);
        assertInt("eventos", 3, service.eventos.size());
    }

    private static void assertReservaFalseOmiteSinEnviarNiFinalizar()
            throws Exception {

        ServicioPrueba service =
                servicioConPrestador("listado@ospim.org.ar",
                        "reservado@ospim.org.ar");

        service.reserva = false;

        NotificacionCotizacionResultado resultado =
                service.notificarPrestadores(10, "tester", 1L);

        assertInt("enviados", 0, resultado.getEnviados());
        assertInt("errores", 0, resultado.getErrores());
        assertInt("omitidos", 1, resultado.getOmitidos());
        assertInt("mails enviados", 0, service.mailsEnviados);
        assertInt("finalizaciones", 0, service.estadosFinalizados.size());
        assertEvento(service, 0, "reservar");
        assertInt("eventos", 1, service.eventos.size());
    }

    private static void assertEmailReservadoInvalidoConTemporalNoEnvia()
            throws Exception {

        ServicioPrueba service =
                servicioConPrestador("listado@ospim.org.ar",
                        "email-invalido");

        NotificacionCotizacionResultado resultado =
                service.notificarPrestadores(10, "tester", 1L);

        assertInt("enviados", 1, resultado.getEnviados());
        assertInt("errores", 0, resultado.getErrores());
        assertInt("emails invalidos", 0, resultado.getEmailsInvalidos());
        assertInt("advertencias QA", 1, resultado.getEmailsRealesInvalidosAdvertidos());
        assertInt("omitidos", 0, resultado.getOmitidos());
        assertInt("mails enviados", 1, service.mailsEnviados);
        assertEstadoFinal(service, 0, ENVIADO);
        assertEvento(service, 0, "reservar");
        assertEvento(service, 1, "enviar");
        assertEvento(service, 2, "finalizar:" + ENVIADO);
    }

    private static void assertErrorEnvioFinalizaError()
            throws Exception {

        ServicioPrueba service =
                servicioConPrestador("listado@ospim.org.ar",
                        "reservado@ospim.org.ar");

        service.errorEnvio =
                new Exception("smtp caido");

        NotificacionCotizacionResultado resultado =
                service.notificarPrestadores(10, "tester", 1L);

        assertInt("enviados", 0, resultado.getEnviados());
        assertInt("errores", 1, resultado.getErrores());
        assertInt("omitidos", 0, resultado.getOmitidos());
        assertEstadoFinal(service, 0, ERROR);
        assertContains("detalle error", service.ultimoError, "smtp caido");
    }

    private static void assertEnvioAceptadoYEnviadoPersistidoCuentaEnviado()
            throws Exception {

        ServicioPrueba service =
                servicioConPrestador("listado@ospim.org.ar",
                        "reservado@ospim.org.ar");

        NotificacionCotizacionResultado resultado =
                service.notificarPrestadores(10, "tester", 1L);

        assertInt("enviados", 1, resultado.getEnviados());
        assertInt("errores", 0, resultado.getErrores());
        assertInt("omitidos", 0, resultado.getOmitidos());
        assertEstadoFinal(service, 0, ENVIADO);
    }

    private static void assertEnvioAceptadoYFinalizacionFalseCuentaError()
            throws Exception {

        ServicioPrueba service =
                servicioConPrestador("listado@ospim.org.ar",
                        "reservado@ospim.org.ar");

        service.finalizarEnviado = false;

        NotificacionCotizacionResultado resultado =
                service.notificarPrestadores(10, "tester", 1L);

        assertInt("enviados", 0, resultado.getEnviados());
        assertInt("errores", 1, resultado.getErrores());
        assertEstadoFinal(service, 0, ENVIADO);
        assertNoEstadoFinal(service, ERROR);
    }

    private static void assertEnvioAceptadoYFinalizacionLanzaCuentaError()
            throws Exception {

        ServicioPrueba service =
                servicioConPrestador("listado@ospim.org.ar",
                        "reservado@ospim.org.ar");

        service.errorFinalizarEnviado =
                new Exception("db caida");

        NotificacionCotizacionResultado resultado =
                service.notificarPrestadores(10, "tester", 1L);

        assertInt("enviados", 0, resultado.getEnviados());
        assertInt("errores", 1, resultado.getErrores());
        assertEstadoFinal(service, 0, ENVIADO);
        assertNoEstadoFinal(service, ERROR);
    }

    private static void assertUsaEmailReservadoDespuesDeReservar()
            throws Exception {

        ServicioPrueba service =
                servicioConPrestador("viejo@ospim.org.ar",
                        "reservado@ospim.org.ar");

        NotificacionCotizacionResultado resultado =
                service.notificarPrestadores(10, "tester", 1L);

        assertInt("enviados", 1, resultado.getEnviados());
        assertString(
                "email temporal",
                "acomas@ospim.org.ar",
                service.emailEnviado
        );
        assertEvento(service, 0, "reservar");
        assertEvento(service, 1, "enviar");
        assertEvento(service, 2, "finalizar:" + ENVIADO);
    }

    private static void assertCuerpoUsaDescripcion()
            throws Exception {

        ServicioPrueba service =
                servicioConPrestador(
                        "listado@ospim.org.ar",
                        "reservado@ospim.org.ar"
                );

        RequerimientoCompraDetalle detalle =
                new RequerimientoCompraDetalle();

        detalle.setTipoItem(
                RequerimientoCompraDetalle.TIPO_ITEM_NOMENCLADOR
        );

        detalle.setIdPrestacion(
                Integer.valueOf(
                        1001
                )
        );

        detalle.setIdTipoNomenclador(
                Integer.valueOf(
                        9
                )
        );

        detalle.setCodigoNomenclador(
                "PROT-001"
        );

        detalle.setDescripcionNomenclador(
                "Prótesis"
        );

        detalle.setCantidad(
                Integer.valueOf(
                        1
                )
        );

        detalle.setObservaciones(
                "Entrega prioritaria"
        );

        List<RequerimientoCompraDetalle> detalles =
                new ArrayList<RequerimientoCompraDetalle>();

        detalles.add(
                detalle
        );

        service.requerimiento.setDetalles(
                detalles
        );

        service.notificarPrestadores(
                10,
                "tester",
                1L
        );

        assertContains(
                "descripcion en cuerpo",
                service.cuerpoEnviado,
                " | Descripcion: Entrega prioritaria"
        );

        assertNotContains(
                "abreviatura anterior eliminada",
                service.cuerpoEnviado,
                " | Obs: "
        );
    }

    private static void assertPdfSeGeneraUnaVezYSeAdjunta()
            throws Exception {

        ServicioPrueba service =
                servicioConPrestador(
                        "listado@ospim.org.ar",
                        "reservado@ospim.org.ar"
                );

        service.notificarPrestadores(
                10,
                "tester",
                1L
        );

        assertInt(
                "PDF generado una sola vez",
                1,
                service.pdfsGenerados
        );

        if (service.pdfEnviado == null
                || service.pdfEnviado.length == 0) {

            throw new AssertionError(
                    "No se recibió el PDF en enviarMail."
            );
        }

        assertString(
                "nombre del PDF",
                "PedidoPresupuesto_10.pdf",
                service.nombrePdfEnviado
        );
    }

    private static void assertFalloPdfNoReservaNiEnvia()
            throws Exception {

        ServicioPrueba service =
                servicioConPrestador(
                        "listado@ospim.org.ar",
                        "reservado@ospim.org.ar"
                );

        service.errorPdf =
                new Exception("jasper caído");

        boolean fallo = false;

        try {
            service.notificarPrestadores(
                    10,
                    "tester",
                    1L
            );
        } catch (Exception e) {
            fallo = true;
            assertContains(
                    "error PDF propagado",
                    e.getMessage(),
                    "jasper caído"
            );
        }

        if (!fallo) {
            throw new AssertionError(
                    "Se esperaba que fallara la generación del PDF."
            );
        }

        assertInt("PDF intentado una vez", 1, service.pdfsGenerados);
        assertInt("sin reservas", 0, service.eventos.size());
        assertInt("sin mails", 0, service.mailsEnviados);
        assertInt(
                "sin finalizaciones",
                0,
                service.estadosFinalizados.size()
        );
    }

    private static void assertHistoricoSinOrdenMedicaConservaPdfSolo()
            throws Exception {

        ServicioPrueba service =
                servicioConPrestador(
                        "listado@ospim.org.ar",
                        "reservado@ospim.org.ar"
                );

        service.notificarPrestadores(
                10,
                "tester",
                1L
        );

        assertInt("mail historico", 1, service.mailsEnviados);

        if (service.envioConOrdenMedica) {
            throw new AssertionError(
                    "Un requerimiento historico sin Orden medica "
                            + "no debe inventar un segundo adjunto."
            );
        }
    }

    private static void
    assertOrdenMedicaSeRecuperaAntesDeReservarYSeAdjunta()
            throws Exception {

        ServicioPrueba service =
                servicioConPrestador(
                        "listado@ospim.org.ar",
                        "reservado@ospim.org.ar"
                );

        byte[] ordenMedica = new byte[] {
                (byte) 0xFF,
                (byte) 0xD8,
                (byte) 0xFF,
                0x01
        };

        service.registrarEventoOrdenMedica = true;
        service.usarOrdenMedica(
                ordenMedica,
                "orden-original.jpeg",
                "image/jpeg"
        );

        NotificacionCotizacionResultado resultado =
                service.notificarPrestadores(
                        10,
                        "tester",
                        1L
                );

        assertInt("enviados con Orden medica", 1, resultado.getEnviados());
        assertEvento(service, 0, "orden_medica");
        assertEvento(service, 1, "reservar");
        assertEvento(service, 2, "enviar");
        assertEvento(service, 3, "finalizar:" + ENVIADO);

        if (!service.envioConOrdenMedica
                || service.ordenMedicaEnviada != ordenMedica) {

            throw new AssertionError(
                    "La Orden medica recuperada no llego al helper de correo."
            );
        }

        assertString(
                "nombre original Orden medica",
                "orden-original.jpeg",
                service.nombreOrdenMedicaEnviado
        );
        assertString(
                "MIME Orden medica",
                "image/jpeg",
                service.contentTypeOrdenMedicaEnviado
        );
    }

    private static void assertFalloOrdenMedicaNoReservaNiEnvia()
            throws Exception {

        ServicioPrueba service =
                servicioConPrestador(
                        "listado@ospim.org.ar",
                        "reservado@ospim.org.ar"
                );

        service.registrarEventoOrdenMedica = true;
        service.errorOrdenMedica =
                new Exception("identidad DL inconsistente");

        boolean fallo = false;

        try {
            service.notificarPrestadores(
                    10,
                    "tester",
                    1L
            );
        } catch (Exception e) {
            fallo = true;
            assertContains(
                    "error Orden medica propagado",
                    e.getMessage(),
                    "identidad DL inconsistente"
            );
        }

        if (!fallo) {
            throw new AssertionError(
                    "Se esperaba el rechazo de la Orden medica inconsistente."
            );
        }

        assertInt("PDF previo a Orden medica", 1, service.pdfsGenerados);
        assertEvento(service, 0, "orden_medica");
        assertInt("sin reserva por Orden medica", 1, service.eventos.size());
        assertInt("sin mail por Orden medica", 0, service.mailsEnviados);
        assertInt(
                "sin finalizacion por Orden medica",
                0,
                service.estadosFinalizados.size()
        );
    }

    private static void assertFirmasOrdenMedica()
            throws Exception {

        ServicioPrueba service = new ServicioPrueba();

        assertString(
                "firma JPEG",
                "image/jpeg",
                service.validarContenido(
                        new byte[] {
                                (byte) 0xFF,
                                (byte) 0xD8,
                                (byte) 0xFF
                        },
                        "orden.jpg"
                )
        );

        assertString(
                "firma PNG",
                "image/png",
                service.validarContenido(
                        new byte[] {
                                (byte) 0x89,
                                0x50,
                                0x4E,
                                0x47,
                                0x0D,
                                0x0A,
                                0x1A,
                                0x0A
                        },
                        "orden.png"
                )
        );

        assertContenidoOrdenMedicaInvalido(
                service,
                new byte[0],
                "orden.jpg",
                "vacía"
        );
        assertContenidoOrdenMedicaInvalido(
                service,
                new byte[] {
                        0x01,
                        0x02,
                        0x03
                },
                "orden.jpeg",
                "firma"
        );
        assertContenidoOrdenMedicaInvalido(
                service,
                new byte[] {
                        (byte) 0xFF,
                        (byte) 0xD8,
                        (byte) 0xFF
                },
                "orden.pdf",
                "JPEG/JPG ni PNG"
        );
    }

    private static void assertContenidoOrdenMedicaInvalido(
            ServicioPrueba service,
            byte[] contenido,
            String nombre,
            String mensaje) throws Exception {

        boolean fallo = false;

        try {
            service.validarContenido(
                    contenido,
                    nombre
            );
        } catch (Exception e) {
            fallo = true;
            assertContains(
                    "validacion Orden medica",
                    e.getMessage(),
                    mensaje
            );
        }

        if (!fallo) {
            throw new AssertionError(
                    "Se esperaba rechazo del contenido de Orden medica."
            );
        }
    }

    private static void assertNoMantieneOrdenMedicaEnEstadoCompartido() {
        Field[] fields = NotificarCotizacionPrestadorServiceImpl.class
                .getDeclaredFields();

        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];

            if (!Modifier.isStatic(field.getModifiers())
                    && (field.getType().equals(byte[].class)
                    || field.getType().getName().indexOf(
                            "OrdenMedicaAdjunta"
                    ) >= 0
                    || field.getName().toLowerCase().indexOf(
                            "ordenmedica"
                    ) >= 0)) {

                throw new AssertionError(
                        "Estado compartido de Orden medica detectado: "
                                + field.getName()
                );
            }
        }
    }

    private static void assertDiagnosticoSinCompatiblesSector()
            throws Exception {

        ServicioPrueba service =
                servicioConPrestador("listado@ospim.org.ar",
                        "reservado@ospim.org.ar");

        service.candidatos.clear();
        service.prestadoresHabilitados = 2;
        service.prestadoresCompatiblesSector = 0;
        service.prestadoresBloqueadosEstadoPrevio = 0;

        NotificacionCotizacionResultado resultado =
                service.notificarPrestadores(10, "tester", 1L);

        assertInt("candidatos", 0, resultado.getTotalCandidatos());
        assertInt(
                "habilitados",
                2,
                resultado.getPrestadoresHabilitados()
        );
        assertInt(
                "compatibles",
                0,
                resultado.getPrestadoresCompatiblesSector()
        );
        assertInt("mails enviados", 0, service.mailsEnviados);
    }

    private static void assertDiagnosticoTodosBloqueadosPorEstadoPrevio()
            throws Exception {

        ServicioPrueba service =
                servicioConPrestador("listado@ospim.org.ar",
                        "reservado@ospim.org.ar");

        service.candidatos.clear();
        service.prestadoresHabilitados = 2;
        service.prestadoresCompatiblesSector = 2;
        service.prestadoresBloqueadosEstadoPrevio = 2;

        NotificacionCotizacionResultado resultado =
                service.notificarPrestadores(10, "tester", 1L);

        assertInt("candidatos", 0, resultado.getTotalCandidatos());
        assertInt(
                "compatibles",
                2,
                resultado.getPrestadoresCompatiblesSector()
        );
        assertInt(
                "bloqueados",
                2,
                resultado.getPrestadoresBloqueadosEstadoPrevio()
        );
        assertInt("mails enviados", 0, service.mailsEnviados);
    }

    private static ServicioPrueba servicioConPrestador(
            String emailListado,
            String emailReservado) {

        RequerimientoCompra requerimiento =
                new RequerimientoCompra();

        requerimiento.setIdRequerimientoCompra(10);
        requerimiento.setIdSector(Integer.valueOf(1));
        requerimiento.setSectorDescripcion("Farmacia");
        requerimiento.setEstado(
                WebKeysCompras.ESTADO_A_COTIZAR
        );

        PrestadorCotizacion prestador =
                new PrestadorCotizacion();

        prestador.setIdPrestador(20);
        prestador.setDescripcion("Prestador Test");
        prestador.setEmail(emailListado);
        prestador.setIdTipoPrestador(30);
        prestador.setTipoPrestador("Tipo Test");

        ServicioPrueba service =
                new ServicioPrueba();

        service.requerimiento =
                requerimiento;

        service.emailReservado =
                emailReservado;

        service.candidatos.add(prestador);

        return service;
    }

    private static void assertEstadoFinal(
            ServicioPrueba service,
            int index,
            String estado) {

        if (service.estadosFinalizados.size() <= index) {
            throw new AssertionError(
                    "Estado final faltante en posicion "
                            + index
                            + ": estados="
                            + service.estadosFinalizados
            );
        }

        assertString(
                "estado final " + index,
                estado,
                service.estadosFinalizados.get(index)
        );
    }

    private static void assertNoEstadoFinal(
            ServicioPrueba service,
            String estado) {

        if (service.estadosFinalizados.contains(estado)) {
            throw new AssertionError(
                    "No se esperaba finalizacion "
                            + estado
                            + ": estados="
                            + service.estadosFinalizados
            );
        }
    }

    private static void assertEvento(
            ServicioPrueba service,
            int index,
            String esperado) {

        if (service.eventos.size() <= index) {
            throw new AssertionError(
                    "Evento faltante en posicion "
                            + index
                            + ": esperado="
                            + esperado
                            + ", eventos="
                            + service.eventos
            );
        }

        assertString(
                "evento " + index,
                esperado,
                service.eventos.get(index)
        );
    }

    private static void assertContains(
            String descripcion,
            String value,
            String esperado) {

        if (value == null || value.indexOf(esperado) < 0) {
            throw new AssertionError(
                    descripcion
                            + ": esperado contener="
                            + esperado
                            + ", actual="
                            + value
            );
        }
    }

    private static void assertNotContains(
            String descripcion,
            String value,
            String inesperado) {

        if (value != null && value.indexOf(inesperado) >= 0) {
            throw new AssertionError(
                    descripcion
                            + ": no se esperaba contener="
                            + inesperado
                            + ", actual="
                            + value
            );
        }
    }

    private static void assertString(
            String descripcion,
            String esperado,
            String actual) {

        if (esperado == null
                ? actual != null
                : !esperado.equals(actual)) {

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

        if (actual != esperado) {
            throw new AssertionError(
                    descripcion
                            + ": esperado="
                            + esperado
                            + ", actual="
                            + actual
            );
        }
    }

    private static class ServicioPrueba
            extends NotificarCotizacionPrestadorServiceImpl {

        private RequerimientoCompra requerimiento;
        private List<PrestadorCotizacion> candidatos =
                new ArrayList<PrestadorCotizacion>();
        private List<String> eventos =
                new ArrayList<String>();
        private List<String> estadosFinalizados =
                new ArrayList<String>();
        private boolean reserva = true;
        private boolean finalizarEnviado = true;
        private Exception errorEnvio;
        private Exception errorFinalizarEnviado;
        private Exception errorPdf;
        private Exception errorOrdenMedica;
        private String errorFinalizarEstado;
        private int mailsEnviados;
        private int pdfsGenerados;
        private byte[] pdfEnviado;
        private String nombrePdfEnviado;
        private boolean registrarEventoOrdenMedica;
        private boolean envioConOrdenMedica;
        private OrdenMedicaAdjunta ordenMedicaAdjunta;
        private byte[] ordenMedicaEnviada;
        private String nombreOrdenMedicaEnviado;
        private String contentTypeOrdenMedicaEnviado;
        private int prestadoresHabilitados = 1;
        private int prestadoresCompatiblesSector = 1;
        private int prestadoresBloqueadosEstadoPrevio;
        private String emailReservado;
        private String emailEnviado;
        private String cuerpoEnviado;
        private String ultimoError;

        protected RequerimientoCompra getRequerimientoCompra(
                int idRequerimientoCompra) {

            return requerimiento;
        }

        protected List<PrestadorCotizacion>
        listarPrestadoresCandidatos(
                int idRequerimientoCompra) {

            return candidatos;
        }

        protected void cargarDiagnosticoCandidatos(
                RequerimientoCompra requerimiento,
                NotificacionCotizacionResultado resultado) {

            resultado.setPrestadoresHabilitados(
                    prestadoresHabilitados
            );

            resultado.setPrestadoresCompatiblesSector(
                    prestadoresCompatiblesSector
            );

            resultado.setPrestadoresBloqueadosEstadoPrevio(
                    prestadoresBloqueadosEstadoPrevio
            );
        }

        protected ReservaCotizacionPrestador reservarCotizacionPrestador(
                int idRequerimientoCompra,
                int idPrestador,
                String usuario) {

            eventos.add("reservar");

            ReservaCotizacionPrestador resultado =
                    new ReservaCotizacionPrestador();

            resultado.setReservado(reserva);
            resultado.setEstadoEnvio(
                    reserva ? WebKeysCompras.ENVIO_PROCESANDO : ENVIADO
            );
            resultado.setEmailDestino(emailReservado);
            resultado.setMotivoCodigo(
                    reserva ? "RESERVA_OTORGADA" : "ESTADO_NO_REINTENTABLE"
            );
            resultado.setMotivoDescripcion("resultado de prueba");

            return resultado;
        }

        protected byte[] generarPedidoPresupuestoPdf(
                int idRequerimientoCompra) throws Exception {

            pdfsGenerados++;

            if (errorPdf != null) {
                throw errorPdf;
            }

            return new byte[] {
                    37, 80, 68, 70
            };
        }

        protected OrdenMedicaAdjunta recuperarOrdenMedicaAdjunta(
                int idRequerimientoCompra,
                long companyId) throws Exception {

            if (registrarEventoOrdenMedica) {
                eventos.add("orden_medica");
            }

            if (errorOrdenMedica != null) {
                throw errorOrdenMedica;
            }

            return ordenMedicaAdjunta;
        }

        private void usarOrdenMedica(
                byte[] contenido,
                String nombreOriginal,
                String contentType) {

            ordenMedicaAdjunta = crearOrdenMedicaAdjunta(
                    contenido,
                    nombreOriginal,
                    contentType
            );
        }

        private String validarContenido(
                byte[] contenido,
                String nombreOriginal) throws Exception {

            return validarContenidoOrdenMedica(
                    contenido,
                    nombreOriginal
            );
        }

        protected void enviarMail(
                long companyId,
                String email,
                String asunto,
                String cuerpo,
                byte[] pedidoPresupuestoPdf,
                String nombrePedidoPresupuestoPdf)
                throws Exception {

            eventos.add("enviar");
            mailsEnviados++;
            emailEnviado = email;
            cuerpoEnviado = cuerpo;
            pdfEnviado = pedidoPresupuestoPdf;
            nombrePdfEnviado = nombrePedidoPresupuestoPdf;

            if (errorEnvio != null) {
                throw errorEnvio;
            }
        }

        protected void enviarMail(
                long companyId,
                String email,
                String asunto,
                String cuerpo,
                byte[] pedidoPresupuestoPdf,
                String nombrePedidoPresupuestoPdf,
                byte[] ordenMedica,
                String nombreOrdenMedica,
                String contentTypeOrdenMedica)
                throws Exception {

            envioConOrdenMedica = true;
            ordenMedicaEnviada = ordenMedica;
            nombreOrdenMedicaEnviado = nombreOrdenMedica;
            contentTypeOrdenMedicaEnviado = contentTypeOrdenMedica;

            enviarMail(
                    companyId,
                    email,
                    asunto,
                    cuerpo,
                    pedidoPresupuestoPdf,
                    nombrePedidoPresupuestoPdf
            );
        }

        protected FinalizacionCotizacionPrestador
        finalizarCotizacionPrestadorConDetalle(
                int idRequerimiento,
                int idPrestador,
                String estado,
                String error,
                String usuario) throws Exception {

            eventos.add("finalizar:" + estado);
            estadosFinalizados.add(estado);
            ultimoError = error;

            if (estado.equals(errorFinalizarEstado)) {
                throw new Exception(
                        "falla persistiendo " + estado
                );
            }

            if (ENVIADO.equals(estado)
                    && errorFinalizarEnviado != null) {

                throw errorFinalizarEnviado;
            }

            FinalizacionCotizacionPrestador resultado =
                    new FinalizacionCotizacionPrestador();

            resultado.setActualizado(
                    !ENVIADO.equals(estado) || finalizarEnviado
            );
            resultado.setEstadoAnterior(WebKeysCompras.ENVIO_PROCESANDO);
            resultado.setEstadoActual(estado);
            resultado.setMotivo("resultado de prueba");

            return resultado;
        }
    }

    private NotificarCotizacionPrestadorServiceImplTest() {
    }
}
