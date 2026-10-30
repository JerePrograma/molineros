import ar.com.ospim.autorizaciones.beans.Nomenclador;
import ar.com.ospim.compras.requerimientos.beans.*;
import ar.com.ospim.compras.requerimientos.helper.EditarRequerimientoCompraHelper;
import ar.com.ospim.compras.requerimientos.service.*;
import java.lang.reflect.*;
import java.math.BigDecimal;
import java.sql.*;
import java.util.*;

/** Ejecuta el Helper real y su JDBC real con conexion simulada; no usa DB ni UI. */
public class ComprasCorreccionJavaTransaccionTest {
    private static int pruebas;
    public static void main(String[] args) throws Exception {
        Field buscar = BusquedaRequerimientoCompraServiceUtil.class.getDeclaredField("instance");
        Field editar = EditarRequerimientoCompraServiceUtil.class.getDeclaredField("instance");
        buscar.setAccessible(true); editar.setAccessible(true);
        Object anteriorBusqueda = buscar.get(null); Object anteriorEdicion = editar.get(null);
        try {
            Estado e = nuevo(445,10415,3); instalar(e,buscar,editar);
            int id = new EditarRequerimientoCompraHelper().corregirItem(copiar(e.origen),"prueba");
            comprobar(id==445 && e.detallesEscritos==0 && e.comparativasEscritas==0,
                    "445 historico tipo3 es no-op sin UPDATE aun con politica nueva15");
            comprobar(e.matricesLeidas==0 && "HISTORICO".equals(e.origen.getDescripcionNomenclador()),
                    "No-op conserva snapshots y no revalida matriz nueva");
            comprobar(e.origen.getPrecioTotalEstimadoInformado().compareTo(new BigDecimal("77.00"))==0,
                    "No-op conserva total informado explicitamente");
            comprobar(e.commits==1 && e.rollbacks==0 && e.cerrada && e.llamadasReglaSQL==0,
                    "No-op cierra su TX sin CALL de correccion legacy");

            e=nuevo(445,11503,3); instalar(e,buscar,editar);
            id=new EditarRequerimientoCompraHelper().corregirItem(copiar(e.origen),"prueba");
            comprobar(id==445&&e.origen.getIdPrestacionInt()==11503&&e.detallesEscritos==0
                    &&e.comparativasEscritas==0&&e.matricesLeidas==0&&e.commits==1
                    &&"HISTORICO".equals(e.origen.getDescripcionNomenclador())
                    &&new BigDecimal("77.00").equals(e.origen.getPrecioTotalEstimadoInformado()),
                    "Actual QA445 canonico11503 tipo3 es no-op y conserva snapshots e importes sin reclasificar historicos10415");

            e=nuevo(445,11503,3);e.origen.setIdTipoPrestacion(null);
            e.origen.setPrecioUnitarioEstimado(null);e.origen.setPrecioTotalEstimado(null);
            e.configuracionAutomatico.put("COMPRAS_CONCEPTOS_AUTOMATICOS","1=10609;2=11503;3=0;4=0;5=10609;6=10609");
            e.configuracionAutomatico.put("COMPRAS_RUBROS_CONCEPTO_AUTOMATICO","1=0;2=0;3=0;4=0;5=0;6=0");
            e.configuracionAutomatico.put("COMPRAS_CANTIDAD_CONCEPTO_AUTOMATICO","1");instalar(e,buscar,editar);
            EditarRequerimientoCompraServiceUtil.Transaccion automatica=EditarRequerimientoCompraServiceUtil.abrirTransaccion();
            try{
                Method asegurar=EditarRequerimientoCompraHelper.class.getDeclaredMethod("asegurarConceptoAutomatico",
                        RequerimientoCompra.class,Integer.TYPE,EditarRequerimientoCompraServiceUtil.Transaccion.class,String.class);
                asegurar.setAccessible(true);asegurar.invoke(new EditarRequerimientoCompraHelper(),e.requerimiento,Integer.valueOf(1388),automatica,"prueba");
                comprobar(e.lecturasDetallesAutomaticos==1&&e.intentosInsertarAutomatico==0&&e.detallesEscritos==0
                        &&e.requerimiento.getDetalles().size()==1&&e.origen.getIdPrestacionInt()==11503
                        &&e.origen.getIdTipoNomencladorInt()==3&&e.origen.getIdTipoPrestacion()==null
                        &&e.commits==0&&!e.cerrada&&e.matricesLeidas==0,
                        "Automatico11503 configurado acepta fila compatible ya creada por trigger sin duplicar ni cambiar TX caller");
            }finally{automatica.cerrar();}

            e=nuevo(446,11452,10); instalar(e,buscar,editar);
            id=new EditarRequerimientoCompraHelper().corregirItem(copiar(e.origen),"prueba");
            comprobar(id==446 && e.detallesEscritos==0 && e.matricesLeidas==0,
                    "446 historico tipo10 conserva identidad sin politica retroactiva");

            e=nuevo(446,2573,3); instalar(e,buscar,editar);
            id=new EditarRequerimientoCompraHelper().corregirItem(copiar(e.origen),"prueba");
            comprobar(id==446 && e.origen.getIdPrestacionInt()==2573 && e.detallesEscritos==0,
                    "Correccion externa autorizada2573 permanece intacta");

            e=nuevo(445,10415,3); instalar(e,buscar,editar);
            RequerimientoCompraDetalle entrada=copiar(e.origen);entrada.setCantidad(Integer.valueOf(2));
            id=new EditarRequerimientoCompraHelper().corregirItem(entrada,"prueba");
            comprobar(id==445 && e.detallesEscritos==1 && e.commits==1,
                    "Cambio de cantidad usa UPDATE crudo confirmado");
            comprobar(new BigDecimal("20.00").compareTo((BigDecimal)e.parametrosDetalle.get(Integer.valueOf(5)))==0,
                    "Total se calcula en Java con cantidad nueva y precio persistido");
            comprobar("HISTORICO".equals(e.parametrosDetalle.get(Integer.valueOf(8)))
                    && !e.sqlDetalle.contains("precio_unitario_estimado =")
                    && !e.sqlDetalle.contains("id_prestador =") && !e.sqlDetalle.contains("observaciones ="),
                    "Correccion conserva snapshot, unitario, adjudicacion y observaciones");
            comprobar(e.eventos.indexOf("LOCK_RP") < e.eventos.indexOf("LOCK_CABECERA")
                    && e.eventos.indexOf("LOCK_CABECERA") < e.eventos.indexOf("LOCK_DETALLES")
                    && e.eventos.indexOf("LOCK_DETALLES") < e.eventos.indexOf("LOCK_COMPARATIVA")
                    && e.eventos.indexOf("LOCK_COMPARATIVA") < e.eventos.indexOf("LOCK_COMPARATIVA_DETALLE"),
                    "Conserva orden de locks RP cabecera detalle y comparativas");

            e=nuevo(445,10415,3); e.comparativa.add(Integer.valueOf(10415)); instalar(e,buscar,editar);
            entrada=seleccionNueva(e); id=new EditarRequerimientoCompraHelper().corregirItem(entrada,"prueba");
            comprobar(id==445 && e.comparativasEscritas==1 && e.detallesEscritos==1
                    && e.llamadasReglaSQL==0 && e.commits==1,
                    "Seleccion nueva tipo15 autorizada coordina comparativa y detalle sin funcion de negocio");

            e=nuevo(445,10415,3); e.existeRP=true; instalar(e,buscar,editar);
            rechazar(e,copiar(e.origen));
            comprobar(e.detallesEscritos==0 && e.comparativasEscritas==0 && e.rollbacks==1,
                    "Reserva RP revalidada bajo locks impide correccion sin escrituras");

            e=nuevo(445,10415,3); e.comparativa.add(Integer.valueOf(300)); instalar(e,buscar,editar);
            rechazar(e,seleccionNueva(e));
            comprobar(e.detallesEscritos==0 && e.comparativasEscritas==0 && e.commits==0,
                    "Destino ya presente en comparativa rechaza sin mutacion");

            e=nuevo(445,10415,3);e.comparativa.add(Integer.valueOf(10415));
            RequerimientoCompraDetalle compartido=copiar(e.origen);compartido.setId(Integer.valueOf(999));
            e.requerimiento.getDetalles().add(compartido);instalar(e,buscar,editar);
            rechazar(e,seleccionNueva(e));
            comprobar(e.detallesEscritos==0 && e.comparativasEscritas==0,
                    "Origen compartido impide reasignar importes de comparativa");

            e=nuevo(445,10415,3);e.comparativa.add(Integer.valueOf(10415));e.falloDetalle=true;
            instalar(e,buscar,editar);rechazar(e,seleccionNueva(e));
            comprobar(e.comparativasEscritas==1 && e.detallesEscritos==0 && e.rollbacks==1 && e.commits==0,
                    "Fallo tras escritura de comparativa solicita rollback total");

            e=nuevo(445,10415,3);e.origen.setIdPrestador(Integer.valueOf(8338));
            e.rubrosPrestador.add("OTRO");instalar(e,buscar,editar);
            entrada=copiar(e.origen);entrada.setCantidad(Integer.valueOf(2));rechazar(e,entrada);
            comprobar(e.detallesEscritos==1 && e.rollbacks==1 && e.commits==0,
                    "Prestador incompatible rechaza despues de escritura parcial y revierte TX");

            e=nuevo(445,10415,3);e.origen.setIdPrestador(Integer.valueOf(8338));
            e.rubrosPrestador.add("INSUMOS_DIABETES");instalar(e,buscar,editar);
            entrada=copiar(e.origen);entrada.setCantidad(Integer.valueOf(2));
            new EditarRequerimientoCompraHelper().corregirItem(entrada,"prueba");
            comprobar(e.detallesEscritos==1 && e.commits==1 && e.llamadasReglaSQL==0,
                    "Compatibilidad de rubro se evalua en Java con normalizacion legacy");
            e=nuevo(445,10415,3);
            RequerimientoCompraDetalle otro=copiar(e.origen);otro.setId(Integer.valueOf(999));
            otro.setIdPrestacion(Integer.valueOf(400));otro.setIdTipoNomenclador(Integer.valueOf(15));
            otro.setCodigoNomenclador("SNAPSHOT ANTERIOR");e.requerimiento.getDetalles().add(otro);
            e.canonicos.put(Integer.valueOf(400),nom(400,15,"NUEVO","OTRO"));instalar(e,buscar,editar);
            rechazar(e,seleccionNueva(e));
            comprobar(e.detallesEscritos==0 && e.comparativasEscritas==0,
                    "Duplicado por codigo canonico normalizado rechaza aun con ID y snapshot diferentes");

            e=nuevo(445,10415,3);e.origen.setIdPrestador(Integer.valueOf(8338));
            e.excepcion="1388=20111111112";e.proveedorSolicita=false;instalar(e,buscar,editar);
            entrada=copiar(e.origen);entrada.setCantidad(Integer.valueOf(2));
            new EditarRequerimientoCompraHelper().corregirItem(entrada,"prueba");
            comprobar(e.commits==1 && e.detallesEscritos==1,
                    "Excepcion funcional proviene de configuracion, sin ID ni CUIT fijo en Java");

            e=nuevo(445,10415,3);e.origen.setIdPrestador(Integer.valueOf(8338));
            e.excepcion="1388=20111111112";e.proveedorBaja=true;instalar(e,buscar,editar);
            entrada=copiar(e.origen);entrada.setCantidad(Integer.valueOf(2));rechazar(e,entrada);
            comprobar(e.commits==0 && e.rollbacks==1,
                    "Excepcion configurada no permite prestador inactivo");

            e=nuevo(445,10415,3);e.origen.setIdPrestador(Integer.valueOf(8338));
            e.excepcion="1388=malformado";instalar(e,buscar,editar);
            entrada=copiar(e.origen);entrada.setCantidad(Integer.valueOf(2));
            boolean errorConfiguracion=false;
            try { new EditarRequerimientoCompraHelper().corregirItem(entrada,"prueba"); }
            catch(Exception esperada) {
                errorConfiguracion=esperada.getMessage()!=null
                        && esperada.getMessage().toLowerCase(java.util.Locale.ROOT).contains("configuracion")
                        && !esperada.getMessage().contains("SQLException");
            }
            comprobar(errorConfiguracion,"Error de configuracion informa el problema con mensaje seguro al usuario");
            comprobar(e.commits==0 && e.rollbacks==1,
                    "Configuracion de excepcion malformada no permite todo y revierte operacion");
            e=nuevo(445,10415,3);e.requerimiento.setEstado(1);instalar(e,buscar,editar);
            RequerimientoCompra cabecera=RequerimientoCompra.paraMapeo();cabecera.setIdRequerimientoCompra(1388);
            cabecera.setCargoOspim(Integer.valueOf(100));cabecera.setCargoTercerizadora(Integer.valueOf(0));
            id=new EditarRequerimientoCompraHelper().guardarRequerimientoCompra(cabecera,"prueba");
            comprobar(id==1388&&e.cabecerasEscritas==1&&e.commits==1&&!e.sqlCabecera.contains("afiliado_")&&e.llamadasReglaSQL==0,
                    "Cabecera se valida y actualiza por JDBC bajo misma TX sin modificar snapshot");

            e=nuevo(445,10415,3);e.requerimiento.setEstado(1);instalar(e,buscar,editar);
            entrada=copiar(e.origen);entrada.setCantidad(Integer.valueOf(2));
            new EditarRequerimientoCompraHelper().guardarDetalle(entrada,"prueba");
            comprobar(e.detallesEscritos==1&&e.commits==1&&e.sqlDetalle.contains("precio_unitario_estimado=NULL")
                    &&"HISTORICO".equals(e.parametrosDetalle.get(Integer.valueOf(7)))&&e.matricesLeidas==0,
                    "Edicion estructural conserva historico y limpia cotizacion por JDBC sin matriz retroactiva");

            e=nuevo(445,10415,3);e.requerimiento.setEstado(3);instalar(e,buscar,editar);
            int estado=new EditarRequerimientoCompraHelper().guardarCotizacionPersistida(1388,null,null,null,null,null,"prueba");
            comprobar(estado==3&&e.importesEscritos==0&&e.estadosEscritos==0&&e.commits==1,
                    "POST cotizacion ya cerrada retorna estado sin mutar ni exigir arreglos");

            e=nuevo(445,10415,3);e.requerimiento.setEstado(2);instalar(e,buscar,editar);
            estado=new EditarRequerimientoCompraHelper().guardarCotizacionPersistida(1388,"{445}","{NULL}","{}",null,Boolean.TRUE,"prueba");
            comprobar(estado==2&&e.importesEscritos==1&&e.surgeEscritos==1&&e.estadosEscritos==0&&e.commits==1,
                    "Cotizacion parcial actualiza importes y surge misma TX y conserva A COTIZAR");

            e=nuevo(445,10415,3);e.requerimiento.setEstado(2);e.rubrosPrestador.add("INSUMOS_DIABETES");instalar(e,buscar,editar);
            estado=new EditarRequerimientoCompraHelper().guardarCotizacionPersistida(1388,"{445}","{25.55}","{}",Integer.valueOf(8338),Boolean.FALSE,"prueba");
            comprobar(estado==3&&e.importesEscritos==1&&e.estadosEscritos==1&&e.commits==1&&e.llamadasReglaSQL==0,
                    "Cotizacion completa valida presupuesto prestador rubros y cierra con CAS JDBC");
            comprobar(new BigDecimal("25.55").equals(e.parametrosDetalle.get(Integer.valueOf(2))),
                    "Cotizacion calcula total Java con cantidad autoritativa de DB");

            e=nuevo(445,10415,3);e.requerimiento.setEstado(2);e.envio="ENVIADO";e.rubrosPrestador.add("INSUMOS_DIABETES");instalar(e,buscar,editar);
            boolean rechazo=false;try{new EditarRequerimientoCompraHelper().guardarCotizacionPersistida(1388,"{445}","{25}","{}",Integer.valueOf(8338),null,"prueba");}catch(Exception esperado){rechazo=true;}
            comprobar(rechazo&&e.importesEscritos==1&&e.rollbacks==1&&e.commits==0,"Cotizacion sin COTIZADO revierte importes y cabecera juntos");

            e=nuevo(445,10415,3);e.requerimiento.setEstado(4);e.existeRP=true;instalar(e,buscar,editar);
            new EditarRequerimientoCompraHelper().cambiarEstado(1388,6,"prueba");
            comprobar(e.estadosEscritos==1&&e.commits==1&&e.llamadasReglaSQL==0,"Autorizar exige RP vigente bajo locks y persiste CAS en misma TX");

            e=nuevo(445,10415,3);e.requerimiento.setEstado(4);e.existeRP=true;e.rpBaja=true;instalar(e,buscar,editar);
            rechazo=false;try{new EditarRequerimientoCompraHelper().cambiarEstado(1388,6,"prueba");}catch(Exception esperado){rechazo=true;}
            comprobar(rechazo&&e.estadosEscritos==0&&e.rollbacks==1,"RP dado de baja impide autorizacion sin cambiar estado");

            e=nuevo(445,10415,3);instalar(e,buscar,editar);
            rechazo=false;try{new EditarRequerimientoCompraHelper().cambiarEstado(1388,4,"prueba");}catch(Exception esperado){rechazo=true;}
            comprobar(rechazo&&e.estadosEscritos==0&&e.rollbacks==1,"Matriz configurable no permite saltar operacion especifica de vinculacion RP");

            e=nuevo(445,10415,3);instalar(e,buscar,editar);
            new EditarRequerimientoCompraHelper().anularRequerimiento(1388,"motivo","prueba");
            comprobar(e.estadosEscritos==1&&e.commits==1&&"motivo".equals(e.parametrosCabecera.get(Integer.valueOf(3))),
                    "Anulacion sin RP confirma baja motivo y auditoria en una TX Java");

            e=nuevo(445,10415,3);e.existeRP=true;instalar(e,buscar,editar);
            rechazo=false;try{new EditarRequerimientoCompraHelper().anularRequerimiento(1388,"motivo","prueba");}catch(Exception esperado){rechazo=true;}
            comprobar(rechazo&&e.estadosEscritos==0&&e.rollbacks==1,"Anulacion respeta vinculo RP activo incluso en estado previo");

            e=nuevo(445,10415,3);e.requerimiento.setEstado(1);instalar(e,buscar,editar);
            new EditarRequerimientoCompraHelper().borrarDetalle(1388,445,"prueba");
            comprobar(e.bajasEscritas==1&&e.commits==1,"Baja detalle PENDIENTE aplica auditoria raw tras revalidar pertenencia bajoTX");
            e=nuevo(445,10415,3);e.requerimiento.setEstado(2);instalar(e,buscar,editar);
            rechazo=false;try{new EditarRequerimientoCompraHelper().borrarDetalle(1388,445,"prueba");}catch(Exception esperado){rechazo=true;}
            comprobar(rechazo&&e.bajasEscritas==0&&e.rollbacks==1,"Baja ultimo detalle A COTIZAR se rechaza sin escritura");
            e=nuevo(445,10415,3);e.requerimiento.setEstado(99);e.requerimiento.setBajaFecha(new java.util.Date(1));instalar(e,buscar,editar);
            id=new EditarRequerimientoCompraHelper().guardarObservacionInterna(1388,"nota interna","prueba");
            comprobar(id==1388&&e.observacionesEscritas==1&&e.commits==1,"Observacion interna ANULADO conserva contrato y usa TX unica");
            e=nuevo(445,10415,3);e.falloEstado=true;instalar(e,buscar,editar);
            rechazo=false;try{new EditarRequerimientoCompraHelper().anularRequerimiento(1388,"motivo","prueba");}catch(Exception esperado){rechazo=true;}
            comprobar(rechazo&&e.rollbacks==1&&e.commits==0,"CAS fallido al anular nunca informa exito falso");

            e=nuevo(445,10415,3);e.requerimiento.setEstado(1);instalar(e,buscar,editar);
            cabecera=RequerimientoCompra.paraMapeo();cabecera.setIdRequerimientoCompra(1388);cabecera.setIdSector(Integer.valueOf(99));
            rechazo=false;try{new EditarRequerimientoCompraHelper().guardarRequerimientoCompra(cabecera,"prueba");}catch(Exception esperado){rechazo=true;}
            comprobar(rechazo&&e.cabecerasEscritas==0&&e.rollbacks==1,"Cabecera rechaza cambio de sector con identidad autoritativa bajo lock");

            e=nuevo(445,10415,3);e.requerimiento.setEstado(1);instalar(e,buscar,editar);
            cabecera=RequerimientoCompra.paraMapeo();cabecera.setIdRequerimientoCompra(1388);cabecera.setAfiliadoCuilTitular("20111111112");
            rechazo=false;try{new EditarRequerimientoCompraHelper().guardarRequerimientoCompra(cabecera,"prueba");}catch(Exception esperado){rechazo=true;}
            comprobar(rechazo&&e.cabecerasEscritas==0&&e.rollbacks==1,"Cabecera rechaza cambio de afiliado sin abrir otra conexion");

            e=nuevo(445,10415,3);e.requerimiento.setEstado(1);instalar(e,buscar,editar);
            rechazo=false;try{new EditarRequerimientoCompraHelper().cambiarEstado(1388,2,"prueba");}catch(Exception esperado){rechazo=true;}
            comprobar(rechazo&&e.estadosEscritos==0&&e.rollbacks==1,"Matriz configurable no permite omitir operacion especifica de notificacion");

            e=nuevo(445,10415,3);e.requerimiento.setEstado(2);e.origen.setCantidad(Integer.valueOf(3));instalar(e,buscar,editar);
            entrada=copiar(e.origen);entrada.setCantidad(Integer.valueOf(999));entrada.setPrecioUnitarioEstimado(new BigDecimal("5"));
            List<RequerimientoCompraDetalle> cotizar=new ArrayList<RequerimientoCompraDetalle>();cotizar.add(entrada);
            new EditarRequerimientoCompraHelper().guardarAvanceCotizacion(1388,cotizar,new ArrayList<Integer>(),true,"prueba");
            comprobar(new BigDecimal("15.00").equals(e.parametrosDetalle.get(Integer.valueOf(2)))&&e.origen.getCantidad().intValue()==3,
                    "Cotizacion ignora cantidad HTTP y calcula total solo con cantidad bloqueada de DB");

            e=nuevo(445,10415,3);e.requerimiento.setEstado(1);e.origen.setTipoItem("MEDICAMENTO");e.origen.setIdPrestacion(null);
            e.origen.setIdTipoNomenclador(null);e.origen.setCodigoNomenclador(null);e.origen.setDescripcionNomenclador(null);
            e.origen.setIdMedicamento(Integer.valueOf(123));e.origen.setTroquel(Integer.valueOf(222));e.origen.setNombreMedicamento("HISTORIA MED");instalar(e,buscar,editar);
            entrada=copiar(e.origen);entrada.setIdMedicamento(Integer.valueOf(999));entrada.setNombreMedicamento("HTTP");entrada.setCantidad(Integer.valueOf(2));
            new EditarRequerimientoCompraHelper().guardarDetalle(entrada,"prueba");
            comprobar(Integer.valueOf(123).equals(e.parametrosDetalle.get(Integer.valueOf(8)))&&Integer.valueOf(222).equals(e.parametrosDetalle.get(Integer.valueOf(9)))
                    &&"HISTORIA MED".equals(e.parametrosDetalle.get(Integer.valueOf(10)))&&e.commits==1,
                    "Medicamento historico conserva identidad troquel y nombre al editar cantidad por JDBC");

            e=nuevo(445,10415,3);RequerimientoCompraDetalle duplicadoHistorico=copiar(e.origen);duplicadoHistorico.setId(Integer.valueOf(777));
            e.requerimiento.getDetalles().add(duplicadoHistorico);instalar(e,buscar,editar);
            id=new EditarRequerimientoCompraHelper().corregirItem(copiar(e.origen),"prueba");
            comprobar(id==445&&e.detallesEscritos==0&&e.commits==1,
                    "No-op historico compartido conserva datos sin activar validacion retroactiva de nuevas selecciones");
            String[] usuarios={null,"","   ","  persona  ","\t"," \t "};
            String[] auditoria={"db_actual","db_actual","db_actual","persona","\t","\t"};
            for(int i=0;i<usuarios.length;i++){
                e=nuevo(445,10415,3);instalar(e,buscar,editar);
                new EditarRequerimientoCompraHelper().anularRequerimiento(1388,"motivo",usuarios[i]);
                comprobar(auditoria[i].equals(e.parametrosCabecera.get(Integer.valueOf(2)))&&e.commits==1
                        &&e.lecturasUsuario==(i<3?1:0),"Auditoria caso"+i+" usa btrim ASCII/current_user misma TX, preserva tabs y no sistema fijo");
            }
            e=nuevo(445,10415,3);instalar(e,buscar,editar);e.usuarioActual=null;
            rechazo=false;try{new EditarRequerimientoCompraHelper().anularRequerimiento(1388,"motivo",null);}catch(Exception esperado){rechazo=true;}
            comprobar(rechazo&&e.estadosEscritos==0&&e.rollbacks==1,"Sin identidad DB verificable no hay escritura ni exito falso");
            e=nuevo(445,10415,3);
            new EditarRequerimientoCompraServiceImpl().persistirEstado(e.con,1388,3,99,"   ","motivo",true);
            comprobar("db_actual".equals(e.parametrosCabecera.get(Integer.valueOf(2)))&&e.lecturasUsuario==1&&!e.cerrada,
                    "Puente JDBC raw publico resuelve usuario DB en la conexion caller sin cerrar ni confirmar");
            // Los defaults de Estado/Lecturas coinciden con la semilla real. No ampliar la matriz para estas excepciones.
            String[] sectoresEmpresa={"RRHH","SISTEMAS"};
            for(int i=0;i<sectoresEmpresa.length;i++){
                e=nuevo(445,10415,3);e.requerimiento.setEstado(1);e.sectorDescripcion=sectoresEmpresa[i];e.empresaAdjudicada=true;instalar(e,buscar,editar);
                rechazo=false;try{new EditarRequerimientoCompraHelper().cambiarEstado(1388,6,"prueba");}catch(Exception esperado){rechazo=true;}
                comprobar(!rechazo&&e.estadosEscritos==1&&e.commits==1&&e.rollbacks==0,
                        "Empresa "+sectoresEmpresa[i]+" PENDIENTE pasa a PARA AUTORIZAR con semilla exacta y adjudicacion validada");
            }
            e=nuevo(445,10415,3);e.requerimiento.setEstado(1);e.sectorDescripcion="RRHH";instalar(e,buscar,editar);
            rechazo=false;try{new EditarRequerimientoCompraHelper().cambiarEstado(1388,6,"prueba");}catch(Exception esperado){rechazo=true;}
            comprobar(rechazo&&e.estadosEscritos==0&&e.rollbacks==1,"Empresa sin cotizacion adjudicada no obtiene excepcion de matriz");
            e=nuevo(445,10415,3);e.requerimiento.setEstado(1);e.empresaAdjudicada=true;instalar(e,buscar,editar);
            rechazo=false;try{new EditarRequerimientoCompraHelper().cambiarEstado(1388,6,"prueba");}catch(Exception esperado){rechazo=true;}
            comprobar(rechazo&&e.estadosEscritos==0&&e.rollbacks==1,"Proveedor PENDIENTE no puede usar excepcion de empresa");
            e=nuevo(445,10415,3);e.requerimiento.setEstado(4);e.existeRP=true;e.estadoRp="BAJA";e.rpBaja=true;e.vinculoBaja=true;instalar(e,buscar,editar);
            rechazo=false;try{new EditarRequerimientoCompraHelper().anularRequerimiento(1388,"motivo","prueba");}catch(Exception esperado){rechazo=true;}
            comprobar(!rechazo&&e.estadosEscritos==1&&e.commits==1&&e.rollbacks==0,
                    "RECLAMO RP con RP y vinculo BAJA coherentes se anula con ANULAR y TRANSICIONES exactos");
            for(int caso=0;caso<3;caso++){
                e=nuevo(445,10415,3);e.requerimiento.setEstado(4);e.existeRP=caso!=0;e.estadoRp="BAJA";e.rpBaja=caso!=1;e.vinculoBaja=caso!=2;instalar(e,buscar,editar);
                rechazo=false;try{new EditarRequerimientoCompraHelper().anularRequerimiento(1388,"motivo","prueba");}catch(Exception esperado){rechazo=true;}
                comprobar(rechazo&&e.estadosEscritos==0&&e.rollbacks==1,"RP no coherente caso"+caso+" no obtiene excepcion de anulacion");
            }
            e=nuevo(445,10415,3);e.requerimiento.setEstado(6);e.existeRP=true;e.estadoRp="BAJA";e.rpBaja=true;e.vinculoBaja=true;instalar(e,buscar,editar);
            rechazo=false;try{new EditarRequerimientoCompraHelper().anularRequerimiento(1388,"motivo","prueba");}catch(Exception esperado){rechazo=true;}
            comprobar(rechazo&&e.estadosEscritos==0&&e.rollbacks==1,"PARA AUTORIZAR con RP BAJA conserva prohibicion de anulacion");
            System.out.println("APROBADAS_CORRECCION="+pruebas+"; alcance=Helper/JDBC reales con conexion simulada, sin DB ni UI");
        } finally {buscar.set(null,anteriorBusqueda);editar.set(null,anteriorEdicion);}
    }
    private static void rechazar(Estado e,RequerimientoCompraDetalle entrada)throws Exception{
        boolean fallo=false;try{new EditarRequerimientoCompraHelper().corregirItem(entrada,"prueba");}
        catch(Exception esperada){fallo=true;}comprobar(fallo,"Operacion invalida rechazada por Helper real");
    }
    private static void comprobar(boolean valor,String nombre)throws Exception{
        if(!valor)throw new Exception("FALLO: "+nombre);pruebas++;System.out.println("OK "+nombre);
    }
    private static void instalar(Estado e,Field buscar,Field editar)throws Exception{
        buscar.set(null,new Lecturas(e));editar.set(null,new Escrituras(e));
    }
    private static Estado nuevo(int detalle,int id,int tipo)throws Exception{
        Estado e=new Estado();e.origen=new RequerimientoCompraDetalle();
        e.origen.setId(Integer.valueOf(detalle));e.origen.setIdRequerimientoCompra(1388);
        e.origen.setTipoItem("NOMENCLADOR");e.origen.setIdPrestacion(Integer.valueOf(id));
        e.origen.setIdTipoNomenclador(Integer.valueOf(tipo));e.origen.setIdTipoPrestacion(Integer.valueOf(8));
        e.origen.setCodigoNomenclador("ORIGEN");e.origen.setDescripcionNomenclador("HISTORICO");
        e.origen.setCantidad(Integer.valueOf(1));e.origen.setPrecioUnitarioEstimado(new BigDecimal("10.00"));
        e.origen.setPrecioTotalEstimado(new BigDecimal("77.00"));e.origen.setObservaciones("NO CAMBIAR");
        e.requerimiento=RequerimientoCompra.paraMapeo();e.requerimiento.setIdRequerimientoCompra(1388);
        e.requerimiento.setIdSector(Integer.valueOf(2));e.requerimiento.setEstado(3);
        e.requerimiento.setDetalles(new ArrayList<RequerimientoCompraDetalle>());e.requerimiento.getDetalles().add(e.origen);
        e.canonicos.put(Integer.valueOf(id),nom(id,tipo,"ORIGEN","CANONICO ACTUAL DISTINTO"));
        e.canonicos.put(Integer.valueOf(300),nom(300,15,"NUEVO","NUEVA"));
        e.con=(Connection)Proxy.newProxyInstance(ComprasCorreccionJavaTransaccionTest.class.getClassLoader(),
                new Class[]{Connection.class},e);return e;
    }
    private static Nomenclador nom(int id,int tipo,String codigo,String descripcion){
        Nomenclador n=new Nomenclador();n.setId_prestacion(id);n.setId_tipo_nomenclador(tipo);
        n.setCodigo(codigo);n.setDescripcion(descripcion);return n;
    }
    private static RequerimientoCompraDetalle copiar(RequerimientoCompraDetalle d){
        RequerimientoCompraDetalle c=new RequerimientoCompraDetalle();c.setId(d.getId());
        c.setIdRequerimientoCompra(d.getIdRequerimientoCompra());c.setTipoItem(d.getTipoItem());
        c.setIdPrestacion(d.getIdPrestacion());c.setIdTipoNomenclador(d.getIdTipoNomenclador());
        c.setIdTipoPrestacion(d.getIdTipoPrestacion());c.setCodigoNomenclador(d.getCodigoNomenclador());
        c.setDescripcionNomenclador(d.getDescripcionNomenclador());c.setCantidad(d.getCantidad());
        c.setPrecioUnitarioEstimado(d.getPrecioUnitarioEstimado());c.setPrecioTotalEstimado(d.getPrecioTotalEstimadoInformado());
        c.setIdPrestador(d.getIdPrestador());c.setObservaciones(d.getObservaciones());return c;
    }
    private static RequerimientoCompraDetalle seleccionNueva(Estado e){
        RequerimientoCompraDetalle c=copiar(e.origen);c.setIdPrestacion(Integer.valueOf(300));
        c.setIdTipoNomenclador(Integer.valueOf(15));c.setCodigoNomenclador("NUEVO");c.setDescripcionNomenclador("NUEVA");return c;
    }
    private static class Escrituras extends EditarRequerimientoCompraServiceImpl{
        private final Estado e;Escrituras(Estado estado){e=estado;}
        public Transaccion abrirTransaccion()throws Exception{
            Constructor<Transaccion> constructor=Transaccion.class.getDeclaredConstructor(
                    EditarRequerimientoCompraServiceImpl.class,Connection.class);
            constructor.setAccessible(true);return constructor.newInstance(this,e.con);
        }
    }
    private static class Lecturas extends BusquedaRequerimientoCompraServiceImpl{
        private final Estado e;Lecturas(Estado estado){e=estado;}
        private void con(Connection con)throws Exception{if(con!=e.con)throw new Exception("Otra conexion");}
        public RequerimientoCompra getRequerimientoCompra(Connection con,int id)throws Exception{con(con);return e.requerimiento;}
        public Nomenclador obtenerNomencladorCanonico(Connection con,int id)throws Exception{con(con);return e.canonicos.get(Integer.valueOf(id));}
        public List<RequerimientoCompraDetalle> getDetalles(Connection con,int id)throws Exception{
            con(con);if(id!=e.requerimiento.getIdRequerimientoCompra())throw new Exception("Otro requerimiento");
            e.lecturasDetallesAutomaticos++;return e.requerimiento.getDetalles();
        }

        public List<TipoPrestacionCompra> listarTiposPrestacion(Connection con)throws Exception{
            con(con);TipoPrestacionCompra t=new TipoPrestacionCompra();t.setId(Integer.valueOf(8));
            t.setIdSector(Integer.valueOf(2));t.setDescripcion("INSUMOS DIABETES");
            List<TipoPrestacionCompra> l=new ArrayList<TipoPrestacionCompra>();l.add(t);return l;
        }
        public List<RequerimientoCompraSector> listarSectores(Connection con)throws Exception{
            con(con);List<RequerimientoCompraSector> l=new ArrayList<RequerimientoCompraSector>();
            l.add(new RequerimientoCompraSector(Integer.valueOf(2)));return l;
        }
        public List<SectorNomencladorCompra> listarAsociacionesNomenclador(Connection con,int id)throws Exception{
            con(con);e.matricesLeidas++;SectorNomencladorCompra a=new SectorNomencladorCompra();
            String[] campos={"idSector","idTipoPrestacion","idTipoNomenclador","idTipoNomencladorCatalogo"};
            int[] valores={2,8,15,15};for(int i=0;i<campos.length;i++){
                Field f=SectorNomencladorCompra.class.getDeclaredField(campos[i]);f.setAccessible(true);f.set(a,Integer.valueOf(valores[i]));}
            List<SectorNomencladorCompra> l=new ArrayList<SectorNomencladorCompra>();l.add(a);return l;
        }
        public List<RequerimientoCompraPresupuesto> listarPresupuestos(Connection con,int id)throws Exception{
            con(con);List<RequerimientoCompraPresupuesto> l=new ArrayList<RequerimientoCompraPresupuesto>();
            if(e.presupuesto){RequerimientoCompraPresupuesto p=new RequerimientoCompraPresupuesto();
                p.setIdRequerimiento(Integer.valueOf(id));p.setTipoDocumento(Integer.valueOf(1));p.setIdPrestador(Integer.valueOf(8338));p.setDlFileEntryId(Long.valueOf(12));l.add(p);}return l;
        }
        public List<RequerimientoCompraPresupuesto> listarDocumentos(Connection con,int id,int tipo)throws Exception{
            con(con);List<RequerimientoCompraPresupuesto> l=new ArrayList<RequerimientoCompraPresupuesto>();
            if(e.empresaAdjudicada){RequerimientoCompraPresupuesto p=new RequerimientoCompraPresupuesto();
                p.setIdRequerimiento(Integer.valueOf(id));p.setTipoDocumento(Integer.valueOf(3));
                p.setEmpresaAdjudicada(true);p.setDescripcionEmpresa("EMPRESA TEST");p.setDlFileEntryId(Long.valueOf(12));l.add(p);}return l;
        }
        public String getConfiguracionCompra(String clave)throws Exception{
            if(e.configuracionAutomatico.containsKey(clave))return e.configuracionAutomatico.get(clave);
            if("COMPRAS_ESTADOS_IDS".equals(clave))return e.estados;
            if("COMPRAS_TRANSICIONES_ESTADO".equals(clave))return e.transiciones;
            if("COMPRAS_ESTADOS_ANULAR".equals(clave))return "1;2;3";
            if("COMPRAS_ESTADOS_ELIMINAR_DETALLE".equals(clave))return "1;2";
            if("COMPRAS_ESTADOS_EDITAR_OBSERVACION_INTERNA".equals(clave))return "1;2;3;4;5;6;7;99";
            if("COMPRAS_SECTORES_SIN_COTIZACION_PRESTADOR".equals(clave))return "RRHH;SISTEMAS";
            if("COMPRAS_SECTORES_DETALLE_OBSERVACION".equals(clave))return "RRHH;LEGALES;SISTEMAS;OTROS";
            if("COMPRAS_ESTADOS_ENVIO".equals(clave))return "PENDIENTE=PENDIENTE;PROCESANDO=PROCESANDO;ENVIADO=ENVIADO;COTIZADO=COTIZADO;ERROR=ERROR;EMAIL_INVALIDO=EMAIL_INVALIDO";
            if("COMPRAS_ESTADOS_CORREGIR_ITEM".equals(clave))return "1;2;3";
            if("COMPRAS_EXCEPCIONES_PRESTADOR_REQUERIMIENTO".equals(clave))return e.excepcion;
            throw new Exception("Configuracion inesperada: "+clave);
        }
        public String getConfiguracionCompra(String clave,Connection con)throws Exception{con(con);return getConfiguracionCompra(clave);}
    }
    private static class Estado implements InvocationHandler{
        Connection con;RequerimientoCompra requerimiento;RequerimientoCompraDetalle origen;
        Map<Integer,Nomenclador> canonicos=new HashMap<Integer,Nomenclador>();
        Map<String,String> configuracionAutomatico=new HashMap<String,String>();
        int lecturasDetallesAutomaticos,intentosInsertarAutomatico;

        List<Integer> comparativa=new ArrayList<Integer>();List<String> rubrosPrestador=new ArrayList<String>();
        List<String> eventos=new ArrayList<String>();Map<Integer,Object> parametrosDetalle;
        String excepcion="1297=33719082799";boolean proveedorSolicita=true,proveedorBaja;
        String sectorDescripcion="PRESTACIONES MEDICAS",estadoRp="VINCULADO";boolean vinculoBaja,empresaAdjudicada;
        boolean existeRP,falloDetalle,cerrada;int commits,rollbacks,detallesEscritos,comparativasEscritas,llamadasReglaSQL,matricesLeidas;
        String usuarioActual="db_actual";int lecturasUsuario;String sqlDetalle,sqlCabecera;Map<Integer,Object> parametrosCabecera;
        String estados="PENDIENTE=1;A_COTIZAR=2;COTIZADO=3;RECLAMO_RP=4;ORDEN_COMPRA=5;PARA_AUTORIZAR=6;AUTORIZADO=7;ANULADO=99";
        String transiciones="1=2,99;2=3,99;3=4,99;4=6;6=7";
        String envio="COTIZADO";boolean presupuesto=true,rpBaja,falloEstado;int estadosEscritos,bajasEscritas,importesEscritos,surgeEscritos,cabecerasEscritas,observacionesEscritas;

        public Object invoke(Object proxy,Method method,Object[] args)throws Throwable{
            String nombre=method.getName();
            if("commit".equals(nombre)){commits++;return null;}if("rollback".equals(nombre)){rollbacks++;return null;}
            if("close".equals(nombre)){cerrada=true;return null;}if("isClosed".equals(nombre))return Boolean.valueOf(cerrada);
            if("setAutoCommit".equals(nombre))return null;if("getAutoCommit".equals(nombre))return Boolean.FALSE;
            if("prepareCall".equals(nombre)||"prepareStatement".equals(nombre))return sentencia((String)args[0]);
            throw new UnsupportedOperationException(nombre);
        }
        private Object sentencia(final String sql)throws Exception{
            final Estado estado=this;
            if(sql.contains("corregir_item_requerimiento")){llamadasReglaSQL++;throw new Exception("CALL legacy prohibido en prueba");}
            if(sql.startsWith("INSERT INTO compras.requerimiento_detalle")){
                intentosInsertarAutomatico++;throw new Exception("No debe insertar un automatico compatible ya existente");
            }

            Class<?> api=sql.startsWith("{call")?CallableStatement.class:PreparedStatement.class;
            return Proxy.newProxyInstance(ComprasCorreccionJavaTransaccionTest.class.getClassLoader(),new Class[]{api},
                new InvocationHandler(){Map<Integer,Object> parametros=new HashMap<Integer,Object>();
                public Object invoke(Object proxy,Method method,Object[] args)throws Throwable{
                    String n=method.getName();if(n.startsWith("set")){parametros.put((Integer)args[0],"setNull".equals(n)?null:args[1]);return null;}
                    if("close".equals(n))return null;
                    if("execute".equals(n)){eventos.add("LOCK_RP");return Boolean.FALSE;}
                    if("executeUpdate".equals(n)){
                        if(sql.startsWith("UPDATE compras.requerimiento_comparativa_detalle")){
                            if(!parametros.get(Integer.valueOf(1)).equals(parametros.get(Integer.valueOf(4))))comparativasEscritas++;
                            return Integer.valueOf(comparativasEscritas>0?1:0);
                        }
                        if(sql.startsWith("UPDATE compras.requerimiento SET observacion_interna=")){observacionesEscritas++;return Integer.valueOf(1);}
                        if(sql.startsWith("UPDATE compras.requerimiento SET estado=")){estadosEscritos++;parametrosCabecera=new HashMap<Integer,Object>(parametros);return Integer.valueOf(falloEstado?0:1);}
                        if(sql.startsWith("UPDATE compras.requerimiento SET surge=")){surgeEscritos++;return Integer.valueOf(1);}
                        if(sql.startsWith("UPDATE compras.requerimiento_detalle SET baja_fecha=")){bajasEscritas++;return Integer.valueOf(1);}
                        if(sql.startsWith("UPDATE compras.requerimiento_detalle SET precio_unitario_estimado")){importesEscritos++;parametrosDetalle=new HashMap<Integer,Object>(parametros);return Integer.valueOf(1);}
                        throw new Exception("UPDATE inesperado: "+sql);
                    }
                    if("executeQuery".equals(n)){
                        List<Map<String,Object>> filas=new ArrayList<Map<String,Object>>();
                        if(sql.startsWith("SELECT current_user")){lecturasUsuario++;if(usuarioActual!=null)filas.add(fila("1",usuarioActual));}
                        else if(sql.startsWith("SELECT rp.id")){eventos.add("LOCK_RP_FILA");}
                        else if(sql.startsWith("SELECT id_requerimiento")){eventos.add("LOCK_CABECERA");filas.add(fila("1",Integer.valueOf(1388)));}
                        else if(sql.startsWith("SELECT id_detalle")){eventos.add("LOCK_DETALLES");filas.add(fila("1",origen.getId()));}
                        else if(sql.startsWith("SELECT 1 FROM compras.requerimiento_reclamo")){if(existeRP)filas.add(fila("1",Integer.valueOf(1)));}
                        else if(sql.startsWith("SELECT id_comparativa")){eventos.add("LOCK_COMPARATIVA");filas.add(fila("1",Integer.valueOf(1)));}
                        else if(sql.startsWith("SELECT cd.id_prestacion")){eventos.add("LOCK_COMPARATIVA_DETALLE");for(Integer id:comparativa)filas.add(fila("1",id));}
                        else if(sql.startsWith("SELECT descripcion,requiere_afiliado")){
                            Map<String,Object> f=fila("descripcion",sectorDescripcion);f.put("requiere_afiliado",Boolean.FALSE);f.put("activo",Boolean.TRUE);f.put("baja_fecha",null);filas.add(f);
                        }else if(sql.startsWith("SELECT rr.estado")){
                            if(existeRP){Map<String,Object> f=fila("estado",estadoRp);f.put("id_reclamo_prestacional",Integer.valueOf(88));f.put("rp_id",Integer.valueOf(88));
                                f.put("rp_baja",rpBaja?new Timestamp(1):null);f.put("vinculo_baja",vinculoBaja?new Timestamp(1):null);filas.add(f);}
                        }else if(sql.startsWith("SELECT estado_envio")){if(envio!=null){Map<String,Object> f=fila("estado_envio",envio);f.put("1",envio);filas.add(f);}}
                        else if(sql.startsWith("UPDATE compras.requerimiento SET cargo_ospim=")){
                            sqlCabecera=sql;parametrosCabecera=new HashMap<Integer,Object>(parametros);cabecerasEscritas++;filas.add(fila("1",Integer.valueOf(1388)));
                        }else if(sql.startsWith("UPDATE compras.requerimiento_detalle")){
                            if(falloDetalle)throw new SQLException("Fallo parcial simulado");
                            sqlDetalle=sql;parametrosDetalle=new HashMap<Integer,Object>(parametros);detallesEscritos++;
                            filas.add(fila("1",origen.getId()));
                        }else if(sql.startsWith("SELECT id_prestador")){
                            Map<String,Object> f=fila("id_prestador",Integer.valueOf(8338));f.put("cuit","20111111112");
                            f.put("baja_fecha",proveedorBaja?new Timestamp(1):null);
                            f.put("solicitar_cotizacion",Boolean.valueOf(proveedorSolicita));filas.add(f);
                        }else if(sql.startsWith("SELECT rubro")){for(String r:rubrosPrestador)filas.add(fila("rubro",r));}
                        else throw new Exception("SELECT inesperado: "+sql);
                        return resultado(filas);
                    }
                    throw new UnsupportedOperationException(n);
                }});
        }
    }
    private static Map<String,Object> fila(String campo,Object valor){Map<String,Object> f=new HashMap<String,Object>();f.put(campo,valor);return f;}
    private static ResultSet resultado(final List<Map<String,Object>> filas){
        return (ResultSet)Proxy.newProxyInstance(ComprasCorreccionJavaTransaccionTest.class.getClassLoader(),new Class[]{ResultSet.class},
            new InvocationHandler(){int pos=-1;boolean nulo;
            public Object invoke(Object proxy,Method method,Object[] args)throws Throwable{
                String n=method.getName();if("next".equals(n))return Boolean.valueOf(++pos<filas.size());
                if("close".equals(n))return null;if("wasNull".equals(n))return Boolean.valueOf(nulo);
                Object valor=filas.get(pos).get(String.valueOf(args[0]));nulo=valor==null;
                if("getInt".equals(n))return valor!=null?valor:Integer.valueOf(0);
                if("getString".equals(n)||"getTimestamp".equals(n))return valor;
                if("getBoolean".equals(n))return valor!=null?valor:Boolean.FALSE;
                throw new UnsupportedOperationException(n);
            }});
    }
}