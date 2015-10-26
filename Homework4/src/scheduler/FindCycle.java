package scheduler;

import java.util.ArrayList;

public class FindCycle 
{
	public static boolean isCyclic(ArrayList<Task> tasks)
	{
		int numTasks = tasks.size();
		ArrayList<Task> visited = new ArrayList<Task>();
		ArrayList<Task> recStack = new ArrayList<Task>();
		for (int i = 0; i < numTasks; i++)
		{
			if (isCycleUtil(i, tasks, visited, recStack))
				return true;
		}
		return false;
	}
	
	private static boolean isCycleUtil(int i, ArrayList<Task> tasks, ArrayList<Task> visited, ArrayList<Task> recStack)
	{
		Task currentTask = tasks.get(i);
		if (!visited.contains(currentTask))
		{
			visited.add(currentTask);
			recStack.add(currentTask);
			int numDependencies = currentTask.Dependencies.size();
			for (int j = 0; j < numDependencies; j++) //loop through dependencies
			{
				//check if dependencies have already been visited
				//if so, it's a cycle
				String currentDependency = currentTask.Dependencies.get(j);
				if (inArray(visited, currentDependency) && isCycleUtil(j, tasks, visited, recStack))
					return true;
				else if (inArray(recStack, currentDependency))
					return true;
			}
		}
		recStack.remove(currentTask);
		return false;
	}
	
	private static boolean inArray(ArrayList<Task> toSearch, String currentDependency)
	{
		for (int i = 0; i < toSearch.size(); i++)
		{
			if (toSearch.get(i).Name.equals(currentDependency)) return true;
		}
		return false;
	}
	
}
