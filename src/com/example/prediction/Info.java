package com.example.prediction;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Vector;

import org.jfree.chart.JFreeChart;

import com.example.prediction.graphic.BarGraphics;
import com.example.prediction.graphic.LineGraphics;
import com.example.prediction.logica.database.AbsDatabase;
import com.example.prediction.logica.libraries.AbsLibrary;
import com.example.prediction.logica.libraries.LibrariesCollection;
import com.example.prediction.logica.metrics.collection.MetricsCollection;
import com.example.prediction.logica.metrics.collection.SimpleMetricsCollection;
import com.example.prediction.logica.models.AbsModeler;
import com.example.prediction.logica.Config;

public class Info {
	static int IMG_LC_serialId = 0;
	static int IMG_EP_serialId = 0;
	static int IMG_SC_serialId = 0;

	static Vector<BufferedImage> images_learningCurve = new Vector<BufferedImage>();
	static Vector<BufferedImage> images_errorPrediction = new Vector<BufferedImage>();
	static Vector<BufferedImage> images_schemesComparator = new Vector<BufferedImage>();

	static CharSequence[] atts;
	static CharSequence[] libs;
	static CharSequence[] filesDataset;
	static CharSequence[] schemes;
	static String[] configureItems = Config.AppSettings.TITLE_CONFIGURES_ITEMS;

	static int attributeSelected;
	static AbsLibrary librarySelected;
	static File fileDatasetSelected;
	static AbsDatabase trainingSet;
	static Vector<AbsModeler> schemesSelected = new Vector<AbsModeler>();
	static LibrariesCollection classEstructure;

	static int typePrediction;
	static AbsModeler bestScheme;
	static Vector<AbsModeler> filteredBestSchemes = new Vector<AbsModeler>();

	static Vector<AbsModeler> not_handlesSchemes = new Vector<AbsModeler>();

	private static Info info;

	public static Info getInstance() {
		if (info == null)
			info = new Info();
		return info;
	}

	public Info() {
		setLibraries();
		setTypePrediction(Config.InitialSettings.CODE_REGRESSION);
	}

	/* SET OPTIONS TO SELECT */

	public void setLibraries() {
		classEstructure = new LibrariesCollection();
		libs = classEstructure.getListLibraries();
	}

	public void setListFilesDataset() {
		File dir = new File(Config.InitialSettings.getDirWorking());
		File[] files = dir.listFiles();
		Vector<CharSequence> aux = new Vector<CharSequence>();
		for (int f = 0; f < files.length; f++) {
			if (files[f].isFile()
					&& files[f].getAbsolutePath().toString().endsWith(Config.InitialSettings.getFormatDataset()))
				aux.add(files[f].getName());

		}
		filesDataset = new CharSequence[aux.size()];
		int index = 0;
		for (CharSequence cs : aux) {
			filesDataset[index] = cs;
			index++;

		}
	}

	public void setListAttributes(AbsDatabase trainingSet) {
		Vector<String> aux = trainingSet.getNamesAttributes();
		atts = new CharSequence[aux.size()];
		for (int index = 0; index < aux.size(); index++)
			atts[index] = aux.elementAt(index);
	}

	public void setListSchemes() {
		Vector<Integer> Listschemes = librarySelected.getAcceptedModelers();
		schemes = new CharSequence[Listschemes.size()];
		Field[] fields = Config.Modeler.class.getFields();
		// int j = 0;
		for (Field f : fields) {
			try {
				if (Listschemes.contains(f.getInt(null))) {
					schemes[f.getInt(null)] = f.getName();
				}
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/* GET OPTIONS TO SELECT */

	public String[] getConfigureItems() {
		return Config.AppSettings.TITLE_CONFIGURES_ITEMS;
	}

	public CharSequence[] getListLibraries() {
		return libs;
	}

	public CharSequence[] getListFilesDataset() {
		return filesDataset;
	}

	public CharSequence[] getListAttributes() {
		return atts;
	}

	public CharSequence[] getListSchemes() {
		return schemes;
	}

	/* GRAPHIC IMAGES */

	/* GENERATE IMAGE */

	public BufferedImage generateImageLearningCurve(AbsDatabase database) throws Exception { // TODO
																								// Auto-generated
																								// method
																								// stub

		Vector<AbsModeler> schemes = new Vector<AbsModeler>();
		schemes.add(this.getBestScheme());

		LineGraphics linechart = new LineGraphics();
		JFreeChart chart = linechart.graphedLearningCurve(database, schemes);
		/*
		 * ChartView chartView = new ChartView(Config.Graphic.GRAPHIC_TYPE_LINE,
		 * chart); chartView.drawChart(chart);
		 */
		return chart.createBufferedImage(WindowPreferences.DEFAULT_WIDTH_P, WindowPreferences.HEIGHT_I);
	}

	public BufferedImage generateImageErrorPrediction(AbsDatabase database) throws Exception { // TODO
		// Auto-generated
		// method
		// stub

		Vector<AbsModeler> schemes = new Vector<AbsModeler>();
		schemes.add(this.getBestScheme());

		LineGraphics linechart = new LineGraphics();
		JFreeChart chart = linechart.graphedErrorPrediction(database, schemes, attributeSelected);
		/*
		 * ChartView chartView = new ChartView(Config.Graphic.GRAPHIC_TYPE_LINE,
		 * chart); chartView.drawChart(chart);
		 */
		return chart.createBufferedImage(WindowPreferences.DEFAULT_WIDTH_P, WindowPreferences.HEIGHT_I);
	}

	public Vector<BufferedImage> generateImagesSchemesComparator(AbsDatabase database) throws Exception {
		// TODO Auto-generated method stub

		MetricsCollection mc = getLibrarySelected().getMetricsEvaluationObject();
		boolean acept = true;
		for (AbsModeler model : this.getListSchemesSelected()) {
			acept = acept && mc.aceptModel(model);
		}
		if (acept != true) {
			mc = new SimpleMetricsCollection();
		}

		Vector<AbsModeler> listschemes = getBestSchemes();

		BarGraphics barchart = new BarGraphics();
		barchart.setSeries(listschemes);
		JFreeChart chart1 = barchart.graphedErrorPredictionNormalized(database, mc);

		BufferedImage img = chart1.createBufferedImage(WindowPreferences.DEFAULT_WIDTH_P, WindowPreferences.HEIGHT_I);
		images_schemesComparator.add(img);

		/*
		 * chartView.measure(chartView.getFitWidth(), chartView.getFitHeight());
		 * chartView.setDrawingCacheEnabled(true);
		 * chartView.buildDrawingCache();
		 * images_schemesComparator.add(((BitmapDrawable)
		 * chartView.getDrawable()).getBitmap());
		 */

		JFreeChart chart2 = barchart.graphedErrorPredictionScale(database, mc);
		images_schemesComparator
				.add(chart2.createBufferedImage(WindowPreferences.DEFAULT_WIDTH_P, WindowPreferences.HEIGHT_I));
		/*
		 * chartView = new ChartView(context, Config.Graphic.GRAPHIC_TYPE_BAR,
		 * chart2); chartView.drawChart(chart2); chartView.buildDrawingCache();
		 * images_schemesComparator.add(((BitmapDrawable)
		 * chartView.getDrawable()).getBitmap());
		 */

		JFreeChart chart3 = barchart.graphedRelationData(database, mc);
		images_schemesComparator
				.add(chart3.createBufferedImage(WindowPreferences.DEFAULT_WIDTH_P, WindowPreferences.HEIGHT_I));
		/*
		 * chartView = new ChartView(context, Config.Graphic.GRAPHIC_TYPE_BAR,
		 * chart3); chartView.drawChart(chart3); chartView.buildDrawingCache();
		 * images_schemesComparator.add(((BitmapDrawable)
		 * chartView.getDrawable()).getBitmap());
		 */

		return images_schemesComparator;
	}

	protected BufferedImage draw(JFreeChart chart, int width, int height) {
		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2 = img.createGraphics();
		chart.draw(g2, new Rectangle.Double(0, 0, width, height));
		g2.dispose();
		return img;
	}

	/* SAVE IMAGES */

	/*
	 * private boolean persist(Bitmap bitmap, String path, String name){
	 * 
	 * File appDirectory = new File(path ); if(!appDirectory.exists()){
	 * appDirectory.mkdir(); } File archivo = new File(path + "/" + name);
	 * boolean result = false;
	 * 
	 * 
	 * 
	 * FileOutputStream out = null; try { out = new FileOutputStream(archivo);
	 * bitmap.compress(Bitmap.CompressFormat.PNG, 100, out); result = true; }
	 * catch (Exception e) { } finally { try { if (out != null) out.close(); }
	 * catch (IOException e) { }
	 * 
	 * } return result; }
	 * 
	 * public void saveLearningCurveImage(Bitmap img){ String name =
	 * "LearningCurve_" + getFileDatasetSelected().getName() + "_" +
	 * this.getBestScheme().getName() + "_" + IMG_LC_serialId + ".png";
	 * 
	 * String path = Config.InitialSettings.getDirWorking() +
	 * Config.InitialSettings.SUBDIR_APP +
	 * Config.InitialSettings.SUBDIR_OPT_PARAMS;
	 * 
	 * if(persist(img, path, name)) IMG_LC_serialId++; }
	 * 
	 * public void saveErrorPredictionImage(Bitmap img){ String path =
	 * Config.InitialSettings.getDirWorking() +
	 * Config.InitialSettings.SUBDIR_APP +
	 * Config.InitialSettings.SUBDIR_OPT_PARAMS;
	 * 
	 * String name = "ErrorPrediction_" + getFileDatasetSelected().getName() +
	 * "_" + this.getBestScheme().getName() + "_" + IMG_EP_serialId + ".png";
	 * if(persist(context,img, path, name)) IMG_EP_serialId++; }
	 * 
	 * public boolean saveSchemesComparatorImage(Bitmap img){ String name =
	 * "SchemesComparator_" + getFileDatasetSelected().getName() + "_" +
	 * IMG_SC_serialId + ".png";
	 * 
	 * String path = Config.InitialSettings.getDirWorking() +
	 * Config.InitialSettings.SUBDIR_APP +
	 * Config.InitialSettings.SUBDIR_OPT_SCHEMES;
	 * 
	 * if(persist(img, path, name)) { IMG_SC_serialId++; return true; } return
	 * false; }
	 * 
	 * /* GET IMAGES
	 */

	public Vector<BufferedImage> getSchemesComparatorImages() {
		return images_schemesComparator;
	}

	public Vector<BufferedImage> getLearningCurveImages() {
		return images_learningCurve;
	}

	public Vector<BufferedImage> getErrorPredictionImages() {
		return images_errorPrediction;
	}

	/*
	 * OPTIONS SELECTED
	 */
	/* SET OPTIONS SELECTED */

	public void setLibrarySelected(String ID) {
		librarySelected = classEstructure.getLibrary(ID);
	}

	public void setFileDatasetSelected(String name) throws Exception {
		fileDatasetSelected = new File(Config.InitialSettings.getDirWorking() + name);
		trainingSet = getLibrarySelected().getDatasetObject();
		trainingSet.parseFile(fileDatasetSelected);
		setListAttributes(trainingSet);
	}

	public void setAttributeSelected(int att) {
		attributeSelected = att;
	}

	public void setSchemesSelected(Vector<Integer> schemes) {
		schemesSelected.removeAllElements();
		schemesSelected = librarySelected.createModelers(schemes, attributeSelected);

		/*
		 * Vector<AbsModeler> handles = new Vector<AbsModeler>();
		 * not_handlesSchemes = new Vector<AbsModeler>(); for(AbsModeler
		 * model:schemesSelected){ if(model.handles(getDatasetSelected()))
		 * handles.add(model); else not_handlesSchemes.add(model); }
		 * schemesSelected.removeAllElements(); schemesSelected = handles;
		 */
	}

	/* GET OPTIONS SELECTED */

	public AbsLibrary getLibrarySelected() {
		return librarySelected;
	}

	public File getFileDatasetSelected() {
		return fileDatasetSelected;
	}

	public AbsDatabase getDatasetSelected() {
		return trainingSet;
	}

	public int getAttributeSelected() {
		return attributeSelected;
	}

	public Vector<AbsModeler> getListSchemesSelected() {
		return schemesSelected;
	}

	/* PROCESSING */

	public void setFilteredBestSchemes() { // MODIFICAR!!!!!!!!!
		filteredBestSchemes = this.getListSchemesSelected();
	}

	public Vector<AbsModeler> getBestSchemes() {
		return filteredBestSchemes;
	}

	public void setBestScheme(AbsModeler scheme) {
		bestScheme = scheme;
	}

	public AbsModeler getBestScheme() { // AL CUAL SE LE APLICA LA OPTIMIZACION
										// DE LOS PARAMETROS
		// TODO Auto-generated method stub
		return bestScheme;
	}

	public static int getTypePrediction() {
		// TODO Auto-generated method stub
		return typePrediction;
	}

	public void setTypePrediction(int type) {
		typePrediction = type;
	}

	/*
	 * public Vector<Integer> getListSchemesHandlerData(AbsDatabase dataset){
	 * return librarySelected.getSchemesHandleData(dataset); }
	 */

	public Vector<Integer> getListAttHandlerPrediction(AbsDatabase dataset) {
		Vector<String> types = null;
		switch (typePrediction) {
		case Config.InitialSettings.CODE_REGRESSION:
			types = librarySelected.getNumericalTypes();
			break;
		case Config.InitialSettings.CODE_CLASSIFICATION:
			types = librarySelected.getCategoricalTypes();
			break;
		case Config.InitialSettings.CODE_CLUSTERING:

		}

		Vector<Integer> result = new Vector<Integer>();
		for (int index = 0; index < dataset.numAttributes(); index++) {
			if (types.contains(dataset.getTypeAttribute(index)))
				result.add(index);
		}
		return result;
	}

	public Vector<AbsModeler> getNotHandlesSchemes() {
		return not_handlesSchemes;
	}

	public void addNotComputedSchemes(AbsModeler m) {
		// TODO Auto-generated method stub
		not_handlesSchemes.add(m);
	}

	public void reset() {
		images_learningCurve.removeAllElements();
		images_errorPrediction.removeAllElements();
		images_schemesComparator.removeAllElements();
	}

	/*
	 * DEBERIAN ESTAR LAS SUGERENCIAS, AYUDAS ... ESTO ES RESPECTO A LOS
	 * SCHEMES, LOS PARAMETROS (LEARNING CURVE) --> FALTA LA ACTIVIDAD DE LA
	 * AYUDA SOBRE LEARNING CURVE
	 * 
	 */

}