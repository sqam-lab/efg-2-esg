import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import tr.edu.iyte.esg.model.ESG;

public class ParseEFG 
{
	HashMap<String,List<String>> structures = new HashMap<>();

	HashMap<String,Set<String>> events = new HashMap<>();
	
	Set<String> states  = new HashSet<>();
	
	HashMap<String,String> stateToStructure= new HashMap<>();

	HashMap<String,Set<String>> eventGraph = new HashMap<>();
	
	public ParseEFG() {
        Path directoryPath = Paths.get("D:\\College\\Graduate Project\\test\\events"); 
        Path directoryPath2 = Paths.get("D:\\College\\Graduate Project\\test\\states"); 

		ParseStructureString(directoryPath2);
		ParseEvents(directoryPath);
		InvertStructMap();
		PrintMap();
	}
	
	public void ParseStructureString(Path directoryPath)
	{		
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(directoryPath)) 
		{ 		    
			for (Path file : stream) 
			{ 
				if(file.getFileName().toString().contains(".json"))
				{
					String jsonString = Files.readString(file);

					JSONObject ob = new JSONObject(jsonString);

					String state = ob.getString("state_str");
					String structure = ob.getString("state_str_content_free");
					
					if(!state.isEmpty() && !structure.isEmpty())
					{
						if(structures.containsKey(structure))
						{
							List<String> temp = structures.get(structure);
							temp.add(state);
							structures.put(structure, temp);
						}
						else
						{
							List<String> temp = new ArrayList<>();
							temp.add(state);
							structures.put(structure, temp);
						}
					}
					else
					{
						System.out.println("Hello");
					}
				}
			}
			int count = 0;
			for (String key : structures.keySet()) 
			{
				System.out.println(key + ": " + structures.get(key));
				states.addAll(structures.get(key));
				count += structures.get(key).size();
			} 

			//System.out.println(structures.keySet().size());
			//System.out.println(count);
			//System.out.println(states.size());
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}     
	}

	public void ParseEvents(Path directoryPath)
	{	
		for(String s : states)
		{
			events.put(s,new HashSet<>());
		}
		
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(directoryPath)) 
		{ 		    
			for (Path file : stream) 
			{ 
				if(file.getFileName().toString().contains(".json"))
				{
					String jsonString = Files.readString(file);

					JSONObject ob = new JSONObject(jsonString);

					String start = ob.getString("start_state");
					String stop = ob.getString("stop_state"); 
					
					if(!start.isEmpty() && !stop.isEmpty() && states.contains(start) && states.contains(stop))
					{
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
			}
			
			System.out.println();
			int count = 0;
			for (HashMap.Entry<String, Set<String>> entry : events.entrySet()) 
			{
				System.out.println(entry.getKey() + ": " + entry.getValue());
				count += entry.getValue().size(); 
			}

			//System.out.println(events.entrySet().size());
			//System.out.println(count); 
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}     
	}
	
	public void InvertStructMap()
	{
		for(String s : structures.keySet()) 
		{
			for(String state : structures.get(s))
			{
				stateToStructure.put(state, s);
				eventGraph.put(s, new HashSet<>());
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
		
		return eventGraph;
	}
	
	public void PrintMap()
	{
		System.out.println();
		for (Entry<String, String> entry : stateToStructure.entrySet()) 
		{
			System.out.println(entry.getKey() + ": " + entry.getValue());
		}

	}
}

