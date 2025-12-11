package ar.com.ospim.afiliados.empleadores.index;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import ar.com.ospim.global.beans.Empresa;
import ar.com.ospim.util.ConnectionHelper;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

public class EmpresasIndex {

	private static Log _log = LogFactoryUtil.getLog(EmpresasIndex.class);
	private static Indexador indexador = null;
	private static boolean inicializado;

	final static File INDEX_DIR = new File("index");

	public static void initialize() {
		indexador = new Indexador();
		indexador.start();
		inicializado = true;
	}

	public static Result buscar(String razon, int cantidad, int nroPag)
			throws CorruptIndexException, IOException, ParseException {
		if (!inicializado) {
			return null;
		}
		StandardAnalyzer analyzer = new StandardAnalyzer();
		Searcher searcher = new IndexSearcher(IndexReader.open("index"));
		Query query2 = new QueryParser("razon_soc", analyzer).parse(razon
				.replaceAll(" ", "+"));
		Hits hits = searcher.search(query2);

		List<Empresa> empresas = new ArrayList<Empresa>();
		for (int i = cantidad * nroPag; i < (nroPag + 1) * cantidad; i++) {
			if (hits.length() > i) {
				Document doc = hits.doc(i);
				String cuitsucu = doc.get("cuitsucu");
				Empresa empresa = new Empresa(cuitsucu.split("-")[0],
						cuitsucu.split("-")[1], doc.get("razon_soc"));
				String seccional = doc.get("id_seccional");
				String id_ramo_empresa = doc.get("id_ramo_empresa");
				if (!seccional.equals("0")) {
					empresa.setId_seccional(Integer.valueOf(seccional));
				}
				if (!id_ramo_empresa.equals("0")) {
					empresa.setId_ramo_empresa(Integer.valueOf(id_ramo_empresa));
				}
				empresas.add(empresa);
			}
		}
		Result res = new Result();
		res.setEmpresas(empresas);
		res.setTotal(hits.length());
		return res;
	}

	public static boolean isListoParaUsar() {
		if (indexador != null) {
			return indexador.isListoParaUsar();
		}
		return false;
	}

	public static synchronized void reindexar(String cuit, String sucu,
			Integer seccional, Integer ramo, String desc) {
		if (!inicializado) {
			return;
		}
		try {
			int cont=0;
			while (indexador == null || !indexador.isListoParaUsar() || cont<5) {
				Thread.sleep(1000);
				cont++;
			}
			Directory directory = FSDirectory.getDirectory(INDEX_DIR);
			IndexReader indexReader = IndexReader.open(directory);
			Term key = new Term("cuitsucu", cuit + "-" + sucu);
			indexReader.deleteDocuments(key);
			indexReader.close();

			StandardAnalyzer analyzer = new StandardAnalyzer();
			IndexWriter writer = new IndexWriter(INDEX_DIR, analyzer, false);
			Document d = new Document();
			d.add(new Field("cuitsucu", cuit + "-" + sucu, Field.Store.YES,
					Field.Index.UN_TOKENIZED));
			d.add(new Field("id_seccional", String.valueOf(seccional),
					Field.Store.YES, Field.Index.NO));
			d.add(new Field("razon_soc", desc.trim(), Field.Store.YES,
					Field.Index.TOKENIZED));
			d.add(new Field("id_ramo_empresa", String.valueOf(ramo), Field.Store.YES,
					Field.Index.NO));
			writer.addDocument(d);
			writer.close();
		} catch (Exception e) {
			_log.error("Error al agregar al indice lucene cuit:" + cuit, e);
		}
	}

	public static boolean isInicializado() {
		return inicializado;
	}

	public static void setInicializado(boolean inicializado) {
		EmpresasIndex.inicializado = inicializado;
	}

	private static class Indexador extends Thread {

		private boolean listoParaUsar = false;

		public void run() {
			try {
				Connection con = ConnectionHelper.getReportesOspimConnection();
				StandardAnalyzer analyzer = new StandardAnalyzer();
				IndexWriter writer = new IndexWriter(INDEX_DIR, analyzer, true);
				System.out.println("Indexando a '" + INDEX_DIR + "'... "
						+ new Date());
				indexEmpresas(writer, con);
				System.out.println("Optimizando a '" + INDEX_DIR + "'... ");
				writer.optimize();
				writer.close();
				System.out.println("Indice finalizado.. " + new Date());
				listoParaUsar = true;
			} catch (Exception e) {
				_log.error("Error al crear indice lucene", e);
			}

		}

		private void indexEmpresas(IndexWriter writer, Connection conn)
				throws Exception {
//			AL 25/08/2016 necesito 13:30 para correr este query...
			String sql = "select cuit, sucursal, razon_soc, id_seccional, id_ramo_empresa from informacion_afip.empresa order by cuit";
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				Document d = new Document();
				d.add(new Field("cuitsucu", rs.getString("cuit") + "-"
						+ rs.getString("sucursal"), Field.Store.YES,
						Field.Index.UN_TOKENIZED));
				d.add(new Field("id_seccional", String.valueOf(rs
						.getInt("id_seccional")), Field.Store.YES,
						Field.Index.NO));
				d.add(new Field("id_ramo_empresa", String.valueOf(rs
						.getInt("id_ramo_empresa")), Field.Store.YES,
						Field.Index.NO));
				d.add(new Field("razon_soc", rs.getString("razon_soc").trim(),
						Field.Store.YES, Field.Index.TOKENIZED));
				writer.addDocument(d);
			}
		}

		public boolean isListoParaUsar() {
			return listoParaUsar;
		}

	}

	public static class Result {
		private List<Empresa> empresas;
		private int total;

		public void setEmpresas(List<Empresa> empresas) {
			this.empresas = empresas;
		}

		public List<Empresa> getEmpresas() {
			return empresas;
		}

		public void setTotal(int total) {
			this.total = total;
		}

		public int getTotal() {
			return total;
		}
	}
}
