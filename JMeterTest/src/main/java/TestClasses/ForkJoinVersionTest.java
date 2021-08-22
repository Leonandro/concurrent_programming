package TestClasses;

import java.io.Serializable;
import java.util.concurrent.ExecutionException;

import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;


import ufrn.ForkJoinKnnClassifier;

public class ForkJoinVersionTest extends AbstractJavaSamplerClient implements Serializable {
	@Override 
	public SampleResult runTest(JavaSamplerContext javaSamplerContext) {
		String k = javaSamplerContext.getParameter("k");
		String train_instances = javaSamplerContext.getParameter("train_instances");
		String test_instances = javaSamplerContext.getParameter("train_instances");
		String test_limit = javaSamplerContext.getParameter("test_limit");
		String n_threads = javaSamplerContext.getParameter("n_threads");
		
        ForkJoinKnnClassifier knn = new ForkJoinKnnClassifier(Integer.valueOf(k), Integer.valueOf(train_instances), Integer.valueOf(test_instances), Integer.valueOf(test_limit), Integer.valueOf(n_threads));
		
        SampleResult result = new SampleResult();
		result.sampleStart();
		result.setSampleLabel("Test Sample");
		
		int[] KNN_ANSWERS =  new int [Integer.valueOf(test_instances)];
		try {
			KNN_ANSWERS = knn.predict();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		int [] EXPECTED_ANSWERS = knn.getExpectedAnswers();
		
		boolean resultIdDiffToExpected = false;

		int i = 0;
		for(int item : EXPECTED_ANSWERS) {
			if(item != KNN_ANSWERS[i]) {
				resultIdDiffToExpected = true;
				break;
			}
			i++;
		}
        
		if(resultIdDiffToExpected) {
			result.sampleEnd();
			result.setResponseCode("200");
			result.setResponseMessage("All predicts are ok");
			result.setSuccessful(true);
		} 
		else {
			result.sampleEnd();
			result.setResponseCode("500");
			result.setResponseMessage("You predict array is wrong");
			result.setSuccessful(false);
		}
		return result; 
	}
	
	@Override 
	public Arguments getDefaultParameters() {
		Arguments defaultParameters = new Arguments();
		defaultParameters.addArgument("k","5");
		defaultParameters.addArgument("train_instances","7526883");
		defaultParameters.addArgument("test_instances","1742866");
		defaultParameters.addArgument("test_limit","500");
		defaultParameters.addArgument("n_threads","2");
		return defaultParameters; 
	} 
}
