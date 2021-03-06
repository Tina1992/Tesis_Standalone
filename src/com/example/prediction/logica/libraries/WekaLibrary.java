package com.example.prediction.logica.libraries;

import java.util.Vector;

import com.example.prediction.logica.Config;
import com.example.prediction.logica.database.WekaDatabase;
import com.example.prediction.logica.evaluation.EvaluationWeka;
import com.example.prediction.logica.metrics.collection.SimpleMetricsCollection;
import com.example.prediction.logica.metrics.collection.WekaMetricsCollection;
import com.example.prediction.logica.models.AbsModeler;
import com.example.prediction.logica.models.LinearRegClassifier;
import com.example.prediction.logica.models.MultilayerPerceptronClassifier;
import com.example.prediction.logica.models.SGDClassifier;
import com.example.prediction.logica.models.SMOregClassifier;
import com.example.prediction.logica.models.SimpleKClusterer;
import com.example.prediction.logica.models.SimpleLinearRegClassifier;
import com.example.prediction.logica.parameters.AbsParameter;
import com.example.prediction.logica.parameters.AbsWekaParameter;
import com.example.prediction.logica.parameters.WekaKernelParameter;
import com.example.prediction.logica.parameters.WekaSimpleParameter;

import weka.core.Attribute;
import weka.core.OptionHandler;
import weka.core.Utils;

public class WekaLibrary extends AbsLibrary{
	
	public static void addParameter(AbsParameter modelParameter, AbsModeler model) {
		model.addParameter(modelParameter);
		modelParameter.modifyModel(model);
	}

	public static int getSimpleParamsCount(AbsModeler model){
		Vector<AbsParameter> modelParameters_=model.getParameters();
		int i=0;
		for (AbsParameter p: modelParameters_){
			i+=((AbsWekaParameter)p).getSimpleParametersCount();
		}
		return i;
	}
	
	public static void parseOptions(String[] options, AbsModeler model) {
		int index = 0;
		for (index = 0; index < options.length; index = index + 2) {
			try {
				if (!options[index].isEmpty() && (options[index].length() == 2)) {
					AbsParameter p = model.getParameter(options[index].charAt(1));
					((WekaSimpleParameter) p).setValue(Double.valueOf(options[index + 1]));
				}
			} catch (Exception e) {
				System.out.println("Parametro no parseado");
			}
		}
		for (AbsParameter p : model.getParameters()) {
			p.modifyModel(model);
		}
	}
	
	public static void modifyObject(AbsWekaParameter parameter, OptionHandler oh) throws Exception {
		String[] currOptions = oh.getOptions();
		String[] newOptions = Utils.splitOptions(parameter.getParameterString());
		for (int i=0;i<currOptions.length;i++){
			if (newOptions[0].equals(currOptions[i])){
				currOptions[i+1]=newOptions[1];
			}
		}
		oh.setOptions(currOptions);
	}

	public static void parseKernelOptions(String[] options, WekaKernelParameter kernel) {
		// TODO Auto-generated method stub
		int index=0;
		for (index=0;index<options.length;index=index+2){
			try{
			if (!options[index].isEmpty()&&(options[index].length()==2)){
				WekaSimpleParameter p=new WekaSimpleParameter(options[index].charAt(1), Double.valueOf(options[index+1]), String.valueOf(options[index].charAt(1)) );
				kernel.addKernelOption(p);
			}}
			catch  (Exception e){
				System.err.println(options[index]);
				System.err.println("Error de parametro");
			}
		}
	}
	
	public WekaLibrary(String ID) {
		super(ID);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void createDatabase() {
		// TODO Auto-generated method stub
		this.trainingSet = new WekaDatabase();
	}

	@Override
	public void createMetricsEvaluation() {
		// TODO Auto-generated method stub
		this.metricsEvaluation = new WekaMetricsCollection();
	}

	@Override
	public void setAcceptedModelers() {						
		// TODO Auto-generated method stub
		acceptedModelers.add(Config.Modeler.LINEAR_REGRESSION);
		acceptedModelers.add(Config.Modeler.NEURAL_NETWORK_REGRESSION);
		acceptedModelers.add(Config.Modeler.SIMPLE_K_CLUSTERER);
		acceptedModelers.add(Config.Modeler.SIMPLE_LINEAR_REGRESSION);
		acceptedModelers.add(Config.Modeler.STOCHASTIC_GRADIENT_DESCENT_REGRESSION);
		acceptedModelers.add(Config.Modeler.SUPPORT_VECTOR_MACHINE_REGRESSION);
	}

	@Override
	public Vector<AbsModeler> createModelers(Vector<Integer> selectedModels, int index) {
		// TODO Auto-generated method stub
		Vector<AbsModeler> ret=new Vector<AbsModeler>();
		for (Integer sel:selectedModels){ 
			if (sel.equals(Config.Modeler.LINEAR_REGRESSION)){
				ret.add(new LinearRegClassifier(index));
			}
			if (sel.equals(Config.Modeler.NEURAL_NETWORK_REGRESSION)){
				ret.add(new MultilayerPerceptronClassifier(index));
			}
			if (sel.equals(Config.Modeler.SIMPLE_K_CLUSTERER)){
				ret.add(new SimpleKClusterer(index));
			}
			if (sel.equals(Config.Modeler.SIMPLE_LINEAR_REGRESSION)){
				ret.add(new SimpleLinearRegClassifier(index));
			}
			if (sel.equals(Config.Modeler.STOCHASTIC_GRADIENT_DESCENT_REGRESSION)){
				ret.add(new SGDClassifier(index));
			}
			if (sel.equals(Config.Modeler.SUPPORT_VECTOR_MACHINE_REGRESSION)){
				ret.add(new SMOregClassifier(index));
			}
		}
		return ret;
	}
	
	@Override
		public void setNumericalTypes() {
			// TODO Auto-generated method stub
			numericalTypes = new Vector<String>(); 		
			numericalTypes.add(Attribute.typeToString(Attribute.NUMERIC));
			numericalTypes.add(Attribute.typeToString(Attribute.DATE));
		}

		@Override
		public void setCategoricalTypes() {
			// TODO Auto-generated method stub
			categoricalTypes = new Vector<String>(); 
			categoricalTypes.add(Attribute.typeToString(Attribute.NOMINAL));
			categoricalTypes.add(Attribute.typeToString(Attribute.RELATIONAL));
			categoricalTypes.add(Attribute.typeToString(Attribute.STRING));
		}
	
}
