import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import tr.edu.iyte.esg.conversion.dot.ESGToDOTFileConverter;
import tr.edu.iyte.esg.model.ESG;
import tr.edu.iyte.esg.testgeneration.TestSuite;
import tr.edu.iyte.esg.testgeneration.TestSuiteGenerator;

public class Main {
	
	public static void main(String[] args)
	{		
		
		FileParser test = new FileParser("D:\\College\\Graduate Project\\EFGs\\Recipes\\recipe.json");
		HashMap<String,Set<String>> graph = test.CreateGraph();
		ESG graph1 = test.createESG("Spanish Recipies");
		
		ESGToDOTFileConverter.buildDOTFileFromESG(graph1,"D:\\College\\Graduate Project\\ESGs\\recipe.dot");
		
		TestSuiteGenerator gen = new TestSuiteGenerator();
		TestSuite tests = gen.generateTestSuite(graph1);
		System.out.println(tests.toString());

	}
}


