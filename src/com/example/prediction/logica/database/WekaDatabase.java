package com.example.prediction.logica.database;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;

import com.example.prediction.logica.individual.Individual;
import com.example.prediction.logica.individual.WekaIndividual;

import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.NominalAttributeInfo;
import weka.core.ProtectedProperties;
import weka.core.converters.CSVLoader;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.StringToNominal;
import weka.filters.unsupervised.instance.RemoveRange;

public class WekaDatabase extends AbsDatabase {

	private Instances trainingSet;

	public WekaDatabase() {
		// TODO Auto-generated constructor stub
		super();
	}

	public WekaDatabase(WekaDatabase wd) {
		this.trainingSet = new Instances((Instances) wd.getDatabaseImplementation());
		this.database = new Vector<Individual>();
		this.database.addAll(wd.getIndividuals());
	}

	public WekaDatabase(Instances instances) {
		trainingSet = instances;
		Enumeration<Instance> enDatabase = trainingSet.enumerateInstances();
		while (enDatabase.hasMoreElements()) {
			Instance i = enDatabase.nextElement();
			WekaIndividual wi = new WekaIndividual(i);
			database.addElement(wi);
		}
	}

	@Override
	public void parseFile(File file) {
		// TODO Auto-generated method stub
		CSVLoader cvsloader = new CSVLoader();
		cvsloader.setNoHeaderRowPresent(false);
		cvsloader.setFieldSeparator(",");
		try {
			cvsloader.setSource(file);
			trainingSet = cvsloader.getDataSet();
			Enumeration<Instance> enDatabase = trainingSet.enumerateInstances();
			while (enDatabase.hasMoreElements()) {
				Instance i = enDatabase.nextElement();
				WekaIndividual wi = new WekaIndividual(i);
				database.addElement(wi);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public Object getDatabaseImplementation() {
		// TODO Auto-generated method stub
		return trainingSet;
	}

	@Override
	public void addData(File file) {
		// TODO Auto-generated method stub
		CSVLoader cvsloader = new CSVLoader();
		try {
			cvsloader.setSource(file);
			trainingSet.addAll(cvsloader.getDataSet());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		super.addData(file);
	}

	@Override
	public void addAttribute(String name) {
		// TODO Auto-generated method stub
		Attribute at = new Attribute(name);
		trainingSet.insertAttributeAt(at, trainingSet.numAttributes());
		super.addAttribute(name);
	}

	/* Sil */

	public WekaDatabase(File file) throws Exception {
		super(file);
	}

	@Override
	public int numAttributes() {
		// TODO Auto-generated method stub
		return trainingSet.numAttributes();
	}

	@Override
	public int numInstances() {
		return trainingSet.numInstances();
	}

	@Override
	public void removeInstances(int first, int last) throws Exception {
		String[] options = new String[2];
		options[0] = "-R";
		options[1] = first + "-" + last;
		RemoveRange remove = new RemoveRange();
		remove.setInputFormat(trainingSet);
		remove.setOptions(options);
		remove.getOptions();
		trainingSet.numInstances();
		trainingSet = Filter.useFilter(trainingSet, remove);
	}

	@Override
	public Double getInstanceValue(int instance, Object att) {
		// TODO Auto-generated method stub
		return trainingSet.instance(instance).value((Integer) att);
	}

	@Override
	public String getNameAttribute(int attribute) {
		// TODO Auto-generated method stub
		return trainingSet.attribute(attribute).name();
	}

	@Override
	public AbsDatabase newInstance(AbsDatabase database) {
		// TODO Auto-generated method stub
		return new WekaDatabase((WekaDatabase) database);
	}

	public AbsDatabase getNewDatasetByRemove(int first, int last) throws Exception {
		AbsDatabase d = super.getNewDatasetByRemove(first, last);
		WekaDatabase result = (WekaDatabase) newInstance(this);
		result.database = d.database;
		result.trainingSet = this.trainingSet.stringFreeStructure();
		result.trainingSet.addAll(this.trainingSet);
		result.removeInstances(first, last);
		return result;
	}

	public AbsDatabase subDatabase(int i, int j) {
		// TODO Auto-generated method stub
		AbsDatabase r = super.subDatabase(i, j);
		WekaDatabase result = (WekaDatabase) newInstance(r);
		result.database = r.database;
		result.trainingSet = new Instances(trainingSet, i, j - i);
		return result;
	}

	public AbsDatabase removeSubAbsDatabase(int i, int j) {
		// TODO Auto-generated method stub
		AbsDatabase r = super.removeSubAbsDatabase(i, j);
		WekaDatabase result = (WekaDatabase) newInstance(r);
		result.database = r.database;
		Instances copy = new Instances(trainingSet);
		for (int k = j - 1; k >= i; k--) {
			copy.remove(k);
			System.out.print(k);
		}
		result.trainingSet = new Instances(copy);
		return result;
	}

	@SuppressWarnings("static-access")
	@Override
	public String getTypeAttribute(int attribute) {
		// TODO Auto-generated method stub
		return ((Instances) trainingSet).attribute(attribute).typeToString(attribute);
	}

	@Override
	public void convertFromStringToNominal() {
		// TODO Auto-generated method stub
		Instances filteredTrainingSet = trainingSet;
		StringToNominal stringToNominal;
		try {
			for (int attIndex = 0; attIndex < trainingSet.numAttributes() - 1; attIndex++) {
				if (trainingSet.attribute(attIndex).isString()) {
					stringToNominal = new StringToNominal();
					stringToNominal.setInputFormat(filteredTrainingSet);
					filteredTrainingSet = Filter.useFilter(filteredTrainingSet, stringToNominal);
				}
			}
			trainingSet=filteredTrainingSet;
			WekaDatabase wd=new WekaDatabase(trainingSet);
			this.database=wd.database;
		} catch (Exception ex) {
			throw new RuntimeException("String to nominal conversion failed", ex);
		}
	}
}
