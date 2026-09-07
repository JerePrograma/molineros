package ar.com.ospim.test;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import ar.com.ospim.afiliados.reportes.ReporteListadoPadron;
import ar.com.ospim.afiliados.reportes.ReportePadronResult;
import ar.com.ospim.afiliados.reportes.ReportesAfiliadoServiceImpl;
import ar.com.ospim.afiliados.reportes.beans.BusquedaReportePadronFiltro;
/**
 * Regresion de Admifarm sobre clases reales, JDBC simulado y POI del proyecto.
 * No abre conexiones ni usa datos de afiliados. Argumento opcional: carpeta
 * existente donde guardar una planilla con datos sinteticos.
 */
public class PadronAdmifarmIntegracionTest {
 static int checks;
 static class Bind {String method;Object value;Bind(String m,Object v){method=m;value=v;}}
 static final Map<Integer,Bind> binds=new LinkedHashMap<Integer,Bind>();
 static void check(String message,boolean ok){checks++;if(!ok)throw new RuntimeException(message);}
 static Object primitive(Class type){
  if(type==Boolean.TYPE)return Boolean.FALSE;
  if(type==Integer.TYPE)return Integer.valueOf(0);
  if(type==Long.TYPE)return Long.valueOf(0L);
  if(type==Double.TYPE)return Double.valueOf(0.0);
  return null;
 }
 static ResultSet rows(final String plan,final String pmi,final String aco){
  return (ResultSet)Proxy.newProxyInstance(PadronAdmifarmIntegracionTest.class.getClassLoader(),new Class[]{ResultSet.class},new InvocationHandler(){
   public Object invoke(Object proxy,Method method,Object[] args)throws Throwable{
    String name=method.getName();
    if(name.equals("getString")){
     String column=(String)args[0];
     if(column.equals("plan"))return plan;
     if(column.equals("plan_omint"))return pmi;
     if(column.equals("unsuscribe_email"))return aco;
     if(column.equals("sexo"))return "M";
     if(column.equals("apellido"))return "PRUEBA \u00d1";
     if(column.equals("nombre"))return "SINTETICO";
     return "";
    }
    if(name.equals("getInt")){return Integer.valueOf("id_ospim".equals(args[0])?123:0);}
    if(name.equals("getDate")){return "naci_fecha".equals(args[0])?java.sql.Date.valueOf("2025-01-01"):null;}
    return primitive(method.getReturnType());
   }
  });
 }
 static CallableStatement statement(){
  return (CallableStatement)Proxy.newProxyInstance(PadronAdmifarmIntegracionTest.class.getClassLoader(),new Class[]{CallableStatement.class},new InvocationHandler(){
   public Object invoke(Object proxy,Method method,Object[] args)throws Throwable{
    if(method.getName().startsWith("set") && args.length==2 && args[0] instanceof Integer){
     Integer index=(Integer)args[0];check("bind repetido "+index,!binds.containsKey(index));binds.put(index,new Bind(method.getName(),args[1]));
    }
    return primitive(method.getReturnType());
   }
  });
 }
 static BusquedaReportePadronFiltro filter(){
  BusquedaReportePadronFiltro f=new BusquedaReportePadronFiltro();
  f.setVistaAdmifarm(true);f.setTipoBusqueda(3);f.setFechaDesde(java.sql.Date.valueOf("2026-01-01"));f.setFechaHasta(java.sql.Date.valueOf("2026-09-06"));
  return f;
 }
 static void bind(BusquedaReportePadronFiltro f)throws Exception{
  binds.clear();Method m=ReportesAfiliadoServiceImpl.class.getDeclaredMethod("setearParametrosQueryPadron",BusquedaReportePadronFiltro.class,CallableStatement.class);
  m.setAccessible(true);m.invoke(new ReportesAfiliadoServiceImpl(),f,statement());
 }
 static void assertBind(int index,String method,Object value){
  Bind b=binds.get(Integer.valueOf(index));check("bind "+index+" "+method,b!=null && method.equals(b.method) && (value==null?b.value==null:value.equals(b.value)));
 }
 public static void main(String[] args)throws Exception{
  System.out.println("START actual classes; only ResultSet/CallableStatement proxies, no ConnectionHelper invocation");
  ReportePadronResult r=ReportePadronResult.getMappingAdmifarm(rows("PLAN SERVIDOR \u00d1","S","S"));
  check("plan mapped from plan","PLAN SERVIDOR \u00d1".equals(r.getPlanAfiliado()));
  check("PMI mapped from plan_omint","S".equals(r.getPmi()));
  check("ACO mapped from unsuscribe_email","S".equals(r.getAco()));
  check("ordinary getters retained",r.getPlanAfiliado().equals(r.getPlan()) && r.getPmi().equals(r.getPlanOmint()) && r.getAco().equals(r.getUnsuscribeEmail()));
  ReportePadronResult empty=ReportePadronResult.getMappingAdmifarm(rows(null,null,null));
  check("null contract retained",empty.getPlanAfiliado()==null && empty.getPmi()==null && empty.getAco()==null);
  ReportePadronResult ordinary=ReportePadronResult.getMapping(rows("PLAN LEGACY","OMINT","EMAIL"));
  check("ordinary mapper keeps ordinary fields","PLAN LEGACY".equals(ordinary.getPlan()) && "OMINT".equals(ordinary.getPlanOmint()) && "EMAIL".equals(ordinary.getUnsuscribeEmail()) && ordinary.getPlanAfiliado()==null);
  BusquedaReportePadronFiltro f=filter();f.setIdsTercerizadora("T1,");f.setCodigosSeccional("2,");f.setCodigosProvincia("3,");f.setCodigosLocalidad("4,");f.setTituyfliares(1);f.setParentescoId(Integer.valueOf(5));f.setCuit("CUIT_SINTETICO");f.setFechaNacimIni(java.sql.Date.valueOf("1980-01-01"));f.setFechaNacimFin(java.sql.Date.valueOf("2000-12-31"));f.setCodigosPlan("6,");f.setCodigosAportes("S");f.setCategoriaUoma("CAT");f.setProyecto("PROY");
  bind(f);check("15 binds only",binds.size()==15);
  Object[] expected={"T1,","2,",java.sql.Date.valueOf("2026-01-01"),java.sql.Date.valueOf("2026-09-06"),"3,","4,","1",Integer.valueOf(5),"CUIT_SINTETICO",java.sql.Date.valueOf("1980-01-01"),java.sql.Date.valueOf("2000-12-31"),"6,","S","CAT","PROY"};
  String[] methods={"setString","setString","setDate","setDate","setString","setString","setString","setInt","setString","setDate","setDate","setString","setString","setString","setString"};
  for(int i=0;i<15;i++)assertBind(i+1,methods[i],expected[i]);
  f=filter();bind(f);check("15 nullable binds",binds.size()==15);
  int[] indexes={1,2,5,6,8,9,10,11,12,13,14,15};int[] types={Types.VARCHAR,Types.VARCHAR,Types.VARCHAR,Types.VARCHAR,Types.INTEGER,Types.VARCHAR,Types.DATE,Types.DATE,Types.VARCHAR,Types.CHAR,Types.CHAR,Types.VARCHAR};
  for(int i=0;i<indexes.length;i++)assertBind(indexes[i],"setNull",Integer.valueOf(types[i]));
  f.setCodigosLocalidad("0,");bind(f);assertBind(6,"setNull",Integer.valueOf(Types.VARCHAR));
  f.setTipoBusqueda(4);f.setCodigosSeccional("9,");bind(f);check("seccional one bind",binds.size()==1);assertBind(1,"setInt",Integer.valueOf(9));
  HSSFWorkbook workbook=new HSSFWorkbook();
  try{
   Sheet sheet=workbook.createSheet("Admifarm prueba");Row header=sheet.createRow(0);Row row=sheet.createRow(1);Row nullRow=sheet.createRow(2);CellStyle style=workbook.createCellStyle();
   Method h=ReporteListadoPadron.class.getDeclaredMethod("createHeaderAdmifarm",Row.class);h.setAccessible(true);h.invoke(null,header);
   Method m=ReporteListadoPadron.class.getDeclaredMethod("createReportePadronDetalleAdmifarm",Integer.TYPE,CellStyle.class,ReportePadronResult.class,Row.class);m.setAccessible(true);m.invoke(null,Integer.valueOf(0),style,r,row);m.invoke(null,Integer.valueOf(0),style,empty,nullRow);
   check("45 export columns",header.getLastCellNum()==45 && row.getLastCellNum()==45);
   check("plan header","PLAN AFILIADO".equals(header.getCell(42).getStringCellValue()));
   check("plan export direct getter","PLAN SERVIDOR \u00d1".equals(row.getCell(42).getStringCellValue()));
   check("PMI export direct getter","S".equals(row.getCell(43).getStringCellValue()));
   check("ACO export no age/sex recompute","S".equals(row.getCell(44).getStringCellValue()));
   check("null exported empty","".equals(nullRow.getCell(42).getStringCellValue()) && "".equals(nullRow.getCell(43).getStringCellValue()) && "".equals(nullRow.getCell(44).getStringCellValue()));
   if (args.length > 0) {
    FileOutputStream out = new FileOutputStream(new File(args[0], "admifarm-datos-sinteticos.xls"));
    try { workbook.write(out); } finally { out.close(); }
   }
  }finally{workbook.close();}
  List<ReportePadronResult> list=new ArrayList<ReportePadronResult>();list.add(r);
  Method report=ReporteListadoPadron.class.getDeclaredMethod("getReporte",List.class,BusquedaReportePadronFiltro.class,Boolean.TYPE);report.setAccessible(true);
  for(int mode=0;mode<3;mode++){
   f=filter();f.setVistaAdmifarm(mode==0);f.setVistaPrevencion(mode==2); f.setDescBusqueda("vigentes");f.setTitularesYFliares("todos");f.setCuit("");f.setCategoriaUoma("");f.setParentescoDesc("");f.setDescMotivoBaja("");f.setDescSeccional("");f.setDescProvincia("");f.setDescLocalidad("");f.setDescTercerizadora("");f.setDescPlan("");f.setDescAportes("");
   SXSSFWorkbook wb=(SXSSFWorkbook)report.invoke(null,list,f,Boolean.valueOf(mode==0));
   try{int rownum=mode==2?2:1;check("POI full report mode "+mode,wb.getNumberOfSheets()==1 && wb.getSheetAt(0).getRow(rownum)!=null);System.out.println("PASS full POI report mode="+mode+" dataColumns="+wb.getSheetAt(0).getRow(rownum).getLastCellNum());}finally{wb.close();wb.dispose();}
  }
  System.out.println("PASS Padron actual mapper, binder, POI exports checks="+checks);
 }
}