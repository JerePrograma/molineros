import java.lang.reflect.Field;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import ar.com.ospim.compras.requerimientos.service.BusquedaRequerimientoCompraServiceImpl;
import ar.com.ospim.compras.requerimientos.service.BusquedaRequerimientoCompraServiceUtil;

/** Solo datos de configuracion simulados. Hereda las consultas productivas. */
public class ConfiguracionComprasDatosFixture extends BusquedaRequerimientoCompraServiceImpl {
    private final Map<String, String> datos = new HashMap<String, String>();

    public ConfiguracionComprasDatosFixture() {
        datos.put("COMPRAS_CANTIDAD_CONCEPTO_AUTOMATICO", "1");
        datos.put("COMPRAS_CANTIDAD_DETALLE_INICIAL", "1");
        datos.put("COMPRAS_COMPARATIVA_DEFAULTS", "PAGO=45;PLAZO=48/72;VALIDEZ=48;ENVIO=Beneficiario");
        datos.put("COMPRAS_COMPARATIVA_ENVIO", "Obra Social;Beneficiario;Delegaci\u00f3n");
        datos.put("COMPRAS_COMPARATIVA_PAGO", "30;45;60");
        datos.put("COMPRAS_COMPARATIVA_PLAZO", "24/48;48/72");
        datos.put("COMPRAS_COMPARATIVA_VALIDEZ", "24;48");
        datos.put("COMPRAS_CONCEPTOS_AUTOMATICOS", "1=10609;2=11503;3=0;4=0;5=10609;6=10609");
        datos.put("COMPRAS_CONTACTO_PRIORIDADES_EMAIL", "E=1");
        datos.put("COMPRAS_CONTACTO_TIPOS_EMAIL", "E");
        datos.put("COMPRAS_DL_GRUPO_LEGACY", "10136");
        datos.put("COMPRAS_DOCUMENTOS_USUARIO_REACTIVACION", "sistema");
        datos.put("COMPRAS_ENVIO_ESTADOS_FINALES", "ENVIADO;ERROR;EMAIL_INVALIDO");
        datos.put("COMPRAS_ENVIO_ESTADOS_REINTENTO", "PENDIENTE;ERROR;EMAIL_INVALIDO");
        datos.put("COMPRAS_ESTADOS_ADMINISTRAR_PRESUPUESTOS", "2");
        datos.put("COMPRAS_ESTADOS_ANULAR", "1;2;3");
        datos.put("COMPRAS_ESTADOS_CONSULTAR_RP", "3;4;6;7;5");
        datos.put("COMPRAS_ESTADOS_CORREGIR_ITEM", "1;2;3");
        datos.put("COMPRAS_ESTADOS_DESCRIPCIONES", "1=PENDIENTE;2=ENVIADO A COTIZAR;3=COTIZADO;4=RECLAMO (RP);6=PARA AUTORIZAR;7=AUTORIZADO;5=ORDEN DE COMPRA;99=ANULADO");
        datos.put("COMPRAS_ESTADOS_EDITAR_COTIZACION", "2");
        datos.put("COMPRAS_ESTADOS_EDITAR_ESTRUCTURA", "1");
        datos.put("COMPRAS_ESTADOS_EDITAR_OBSERVACION_INTERNA", "1;2;3;4;5;6;7;99");
        datos.put("COMPRAS_ESTADOS_EDITAR_SURGE", "1;2");
        datos.put("COMPRAS_ESTADOS_ELIMINAR_DETALLE", "1;2");
        datos.put("COMPRAS_ESTADOS_ENVIAR_AUTORIZAR", "4");
        datos.put("COMPRAS_ESTADOS_ENVIAR_COTIZAR", "1");
        datos.put("COMPRAS_ESTADOS_ENVIO", "PENDIENTE=PENDIENTE;PROCESANDO=PROCESANDO;ENVIADO=ENVIADO;COTIZADO=COTIZADO;ERROR=ERROR;EMAIL_INVALIDO=EMAIL_INVALIDO");
        datos.put("COMPRAS_ESTADOS_IDS", "PENDIENTE=1;A_COTIZAR=2;COTIZADO=3;RECLAMO_RP=4;ORDEN_COMPRA=5;PARA_AUTORIZAR=6;AUTORIZADO=7;ANULADO=99");
        datos.put("COMPRAS_ESTADOS_NOTIFICAR_PRESTADORES", "1;2");
        datos.put("COMPRAS_ESTADOS_NOTIFICAR_TODOS", "1");
        datos.put("COMPRAS_ESTADOS_PRESTADORES_ENVIADOS", "2;3;4;5;6;7;99");
        datos.put("COMPRAS_ESTADOS_REINTENTAR_NOTIFICACIONES", "2");
        datos.put("COMPRAS_ESTADOS_SOLO_LECTURA", "3;4;6;7;5;99");
        datos.put("COMPRAS_ESTADOS_VER_COTIZACION", "2;3;4;6;7;5;99");
        datos.put("COMPRAS_ESTADOS_VER_PRESUPUESTOS", "2;3;4;6;7;5;99");
        datos.put("COMPRAS_EXCEPCIONES_PRESTADOR_REQUERIMIENTO", "1297=00000000000");
        datos.put("COMPRAS_FILTROS_NOMENCLADOR", "FARMACIA=9;DISCAPACIDAD=8;ODONTOLOGIA=1;PRESTACIONES MEDICAS=0");
        datos.put("COMPRAS_LIMITES", "PRESUPUESTOS_POR_CARGA=10;PRESTADORES_ENVIADOS=500;DL_EXTENSION=20;DL_TITULO=240;ITEMS_HISTORICOS_AFILIADO=20;ORDENES_MEDICAS_POR_CARGA=20;REQUERIMIENTOS_POR_PAGINA=50;RECETA_ORDEN_MEDICA=100");
        datos.put("COMPRAS_MAX_ERROR_NOTIFICACION", "4000");
        datos.put("COMPRAS_OC_MAX_IMPORTE", "99999999999999.99");
        datos.put("COMPRAS_ROLES", "VIEW=VIEW_COMPRAS;ABM=ABM_COMPRAS;ANULAR=ANULAR_COMPRAS;COTIZAR=COTIZAR_COMPRAS;AUTORIZAR=AUTORIZAR_COMPRAS");
        datos.put("COMPRAS_RP_COMPROBANTE_INICIAL", "OTR");
        datos.put("COMPRAS_RP_DESCRIPCION_NO_RECUPERABLE", "NO ES RECUPERABLE");
        datos.put("COMPRAS_RP_ERROR_DEFECTO", "Error de vinculacion no especificado.");
        datos.put("COMPRAS_RP_ESTADO_PRESTACION_INICIAL", "0");
        datos.put("COMPRAS_RP_FRECUENCIA_INICIAL", "UNICA");
        datos.put("COMPRAS_RP_MAX_ERROR", "2000");
        datos.put("COMPRAS_RP_MAX_OBSERVACION", "200");
        datos.put("COMPRAS_RP_NO_RECUPERABLE", "2");
        datos.put("COMPRAS_RP_RECUPERABLE_INTEGRACION", "3");
        datos.put("COMPRAS_RP_RECUPERABLE_SUR", "1");
        datos.put("COMPRAS_RP_SUCURSAL_INICIAL", "000");
        datos.put("COMPRAS_RP_TIPO_PRESTACION_INICIAL", "1");
        datos.put("COMPRAS_RP_VIGENCIA_CONTEXTO_MILLIS", "7200000");
        datos.put("COMPRAS_RUBROS_CONCEPTO_AUTOMATICO", "1=0;2=0;3=0;4=0;5=0;6=0");
        datos.put("COMPRAS_RUBROS_INSUMOS", "6");
        datos.put("COMPRAS_RUBROS_PROTESIS", "3;4;5");
        datos.put("COMPRAS_SECTORES_BUSQUEDA_SIN_ACENTOS", "PRESTACIONES MEDICAS");
        datos.put("COMPRAS_SECTORES_DESCRIPCION_VISIBLE", "PRESTACIONES MEDICAS=PRESTACIONES M\u00c9DICAS;ODONTOLOGIA=ODONTOLOG\u00cdA");
        datos.put("COMPRAS_SECTORES_DETALLE_OBSERVACION", "RRHH;LEGALES;SISTEMAS;OTROS");
        datos.put("COMPRAS_SECTORES_NO_SELECCIONABLES", "LEGALES;OTROS");
        datos.put("COMPRAS_SECTORES_RP", "PRESTACIONES MEDICAS=PRESTACIONES MEDICAS;DISCAPACIDAD=DISCAPACIDAD;FARMACIA=FARMACIA;ODONTOLOGIA=ODONTOLOGIA;LEGALES=LEGALES");
        datos.put("COMPRAS_SECTORES_SIN_COTIZACION_PRESTADOR", "RRHH;SISTEMAS");
        datos.put("COMPRAS_SECTOR_MEDICO", "PRESTACIONES MEDICAS");
        datos.put("COMPRAS_TIPO_NOMENCLADOR_FARMACIA", "9");
        datos.put("COMPRAS_TRANSICIONES_ESTADO", "1=2,99;2=3,99;3=4,99;4=6;6=7");
        datos.put("COMPRAS_USUARIO_DEFECTO_NOTIFICACION", "sistema");
    }

    public String getConfiguracionCompra(String clave) {
        return datos.get(clave);
    }

    public String getConfiguracionCompra(String clave, Connection con) {
        return datos.get(clave);
    }

    public static void configurar(String clave, String valor) {
        try {
            Field campo = BusquedaRequerimientoCompraServiceUtil.class.getDeclaredField("instance");
            campo.setAccessible(true);
            ((ConfiguracionComprasDatosFixture) campo.get(null)).datos.put(clave, valor);
        } catch (Exception error) {
            throw new IllegalStateException("No se pudo cambiar la frontera de configuracion de prueba.", error);
        }
    }

    public static void instalar() {
        try {
            Field campo = BusquedaRequerimientoCompraServiceUtil.class.getDeclaredField("instance");
            campo.setAccessible(true);
            campo.set(null, new ConfiguracionComprasDatosFixture());
        } catch (Exception error) {
            throw new IllegalStateException("No se pudo preparar la frontera de datos de configuracion.", error);
        }
    }
}
