package com.example.prediction.logica.metrics.composite;

import com.example.prediction.logica.Config;
import com.example.prediction.logica.metrics.abstracts.AbsCompMetric;
import com.example.prediction.logica.metrics.abstracts.Info;
import com.example.prediction.logica.metrics.abstracts.Representation;
import com.example.prediction.logica.metrics.abstracts.Required;
import com.example.prediction.logica.metrics.abstracts.Type;
import com.example.prediction.logica.metrics.collection.MetricsCollection;
import com.example.prediction.logica.metrics.evaluation_metric.WekaCCMetric;
import com.example.prediction.logica.metrics.evaluation_metric.WekaRAEMetric;
import com.example.prediction.logica.metrics.evaluation_metric.WekaRRSEMetric;

public class WekaCOMBMetric extends AbsCompMetric{

	public WekaCOMBMetric() {
		super(MetricsCollection.COMB,Required.MAX, Representation.NORMALIZED, Type.REGRESSION, Info.ERROR_PREDICTION, "Comb");
		addMetric(new WekaCCMetric());
		addMetric(new WekaRRSEMetric());
		addMetric(new WekaRAEMetric());
		// TODO Auto-generated constructor stub
	}

	@Override
	public Double calculate(int mode) throws Exception {
		// TODO Auto-generated method stub
		double r=0;
		r+=1-Math.abs(getMetrics().get(0).calculate(mode));
		if (mode==Config.TrainingMode.TRAINING_MODE){
			r+=getMetrics().get(1).calculateNormalized(database, modeler);
			r+=getMetrics().get(2).calculateNormalized(database, modeler);
		}
		else
		{
			r+=getMetrics().get(1).calculateNormalized(database, modeler, 5);
			r+=getMetrics().get(2).calculateNormalized(database, modeler, 5);
		}
		return r;
	}
	

}
