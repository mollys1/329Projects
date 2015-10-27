package scheduler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;

public class Main {
	
	static ArrayList<Task> tasks;
	
	public static void main(String[] args) {
		String dataFileName;
		if (args.length == 1) //if file provided at the command line
		{
			dataFileName = args[0];
			tasks = new ArrayList<Task>();
//			parseDataFile("C:\\Users\\Sarah\\Documents\\Year 4\\SE 329\\test_data.txt");
			//parseDataFile("C:\\Users\\Molly\\Documents\\GitHub\\329Projects\\Homework4\\test_circularDependency.csv");
			parseDataFile(dataFileName);
			PDMScheduler scheduler = new PDMScheduler();
			scheduler.calculatePDMSchedule(tasks);
			scheduler.findCriticalPath(tasks);
		}
		else
		{
			System.out.println("Please specify an input file");
		}
	}
	
	private static void parseDataFile(String dataFileName) {
		File inputFile = new File(dataFileName);
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(inputFile));
			String line = reader.readLine();
			while (line != null) {
				String[] tokens = line.split("[ ]+");
				//Create Task
				Task newTask = new Task();
				newTask.Name = tokens[0];
				// Check that duration is a number
				try {
					newTask.Duration = Integer.parseInt(tokens[1]);
				} catch (NumberFormatException e) {
					System.out.println("Error with the duration of Task " + newTask.Name + "\nEnding Program");
					System.exit(0);
				} 
				// Handle dependencies
				if (tokens.length > 2) {
					String[] dependencyTokens = tokens[2].split("[,]+");
					for (int i=0; i<dependencyTokens.length; i++) {
						Task dependency = findTask(dependencyTokens[i], tasks);
						if (dependency == null) {
							System.out.println("Dependencies must be predecessors\nError with dependencies of " + newTask.Name + "\nEnding Program");
							System.exit(0);
						}
						else newTask.Dependencies.add(dependencyTokens[i]);
					}
				}
				tasks.add(newTask);
				
				line = reader.readLine();				
			}
			
			
			//Keeps track of what tasks are a dependency for
			for (int i=0; i<tasks.size(); i++) {
				Task t = tasks.get(i);
				for (int j=0; j<t.Dependencies.size(); j++) {
					Task dependencyTask = findTask(t.Dependencies.get(j), tasks);
					if (dependencyTask != null) {
						dependencyTask.DependencyFor.add(t.Name);
					}
					else {
						System.out.println("There is an error with dependencies of " + t.Name + "\nEndingProgram");
						System.exit(0);
					}
				}
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	private static Task findTask(String taskName, ArrayList<Task> tasks) {
		for (int i=0; i<tasks.size(); i++) {
			if (tasks.get(i).Name.equals(taskName)) return tasks.get(i);
		}
		return null;
	}
	
}

