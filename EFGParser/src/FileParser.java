import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONObject;

import tr.edu.iyte.esg.model.ESG;
import tr.edu.iyte.esg.model.EdgeSimple;
import tr.edu.iyte.esg.model.EventSimple;
import tr.edu.iyte.esg.model.Vertex;
import tr.edu.iyte.esg.model.VertexSimple;

public class FileParser 
{
	HashMap<String,Set<String>> events = new HashMap<>();
	
	HashMap<String,String> stateToStructure= new HashMap<>();

	HashMap<String,Set<String>> eventGraph = new HashMap<>();
	
	public FileParser(String filepath)
	{
		try 
		{
			ParseWholeGraph(filepath);
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void ParseWholeGraph(String filepath) throws IOException
	{
		
		String jsonString = Files.readString(Paths.get(filepath));
		
		JSONObject ob = new JSONObject(jsonString);
		
		JSONArray nodes = ob.getJSONArray("nodes");
		
		JSONArray edges = ob.getJSONArray("edges");
		
		String firstNode = null;
		String lastNode = null;
		
		for(int i = 0; i < nodes.length(); i++)
		{
			JSONObject j = nodes.getJSONObject(i);
			
			String state = j.getString("state_str");
			String structure = j.getString("structure_str");
			
			stateToStructure.put(state, structure);
			eventGraph.put(structure, new HashSet<>());
			
			if(j.getString("label").endsWith("<FIRST>"))
			{
				firstNode = structure;
			}
			else if(j.getString("label").endsWith("<LAST>"))
			{
				lastNode = structure;
			}
		}
		
		eventGraph.put("[",new HashSet<>());
		eventGraph.get("[").add(firstNode);
		eventGraph.put("]", new HashSet<>());
		eventGraph.get(lastNode).add("]");
		
		for(int i = 0; i < edges.length(); i++)
		{
			JSONObject j = edges.getJSONObject(i);
			
			String start = j.getString("from");
			String stop = j.getString("to");
						
			if(events.containsKey(start))
			{
				events.get(start).add(stop);
			}
			else
			{
				events.put(start, new HashSet<>());
				events.get(start).add(stop);
			}

		}	
	}	
	
	public HashMap<String,Set<String>> CreateGraph()
	{
		for(String startState : events.keySet())
		{
			for(String stopState : events.get(startState)) 
			{
				String startStruct = stateToStructure.get(startState);
				String stopStruct = stateToStructure.get(stopState);
				
				if(!startStruct.equals(stopStruct))
				{
					eventGraph.get(startStruct).add(stopStruct);
				}
			}
		}
		
		//PrintSets(eventGraph);
		
		return eventGraph;
	}
	
	public void PrintSets(HashMap<String,Set<String>> setToPrint)
	{
		for (HashMap.Entry<String, Set<String>> entry : setToPrint.entrySet()) 
		{
			System.out.println(entry.getKey() + ": " + entry.getValue());
		}
		System.out.println();
	}
	
	public void PrintMap()
	{
		for (Entry<String, String> entry : stateToStructure.entrySet()) 
		{
			System.out.println(entry.getKey() + ": " + entry.getValue());
		}
		System.out.println();
	}
	
	public ESG createESG(String name)
	{
		ESG graph = new ESG(1,name);
		
		int i = 1;
		
		for(String key : eventGraph.keySet())
		{
			EventSimple node = new EventSimple(i,key);
			VertexSimple v = new VertexSimple(i,node);
			graph.addEvent(node);
			graph.addVertex(v);

			i++;
		}
		
		i = 1;
		for(Vertex start : graph.getVertexList())
		{
			for(String stop : eventGraph.get(start.getEvent().getName()))
			{
				Vertex s = graph.getVertexByEventName(stop);
				EdgeSimple ed = new EdgeSimple(i,start,s);
				graph.addEdge(ed);
				i++;
			}
		}
		
		return graph;
	}
}
