package scheduler;

import java.util.ArrayList;
import java.util.Queue;
import java.util.Stack;

public class PDMScheduler {
	
	private boolean scheduleHasBeenCalculated = false;
	private ArrayList<Task> tasks = new ArrayList<Task>();
	//private ArrayList<ArrayList<Task>> criticalPaths = new ArrayList<ArrayList<Task>>();
	
	public PDMScheduler(ArrayList<Task> tasks)
	{
		this.tasks = tasks;
	}
	
	
	public void calculatePDMSchedule() {
		scheduleHasBeenCalculated = true;
		
		//Calculate Early Starts and Finishes
		for(int i=0; i<tasks.size(); i++) {
			Task t = tasks.get(i);
			
			if(t.Dependencies.size() == 0) {
				t.EarlyStart = 0;
				t.EarlyFinish = t.Duration;
			}
			else {
				//Find dependency with highest early finish
				int dependencyMaxEarlyFinish = 0;
				for (int j=0; j<t.Dependencies.size(); j++) {
					Task dependencyT = findTask(t.Dependencies.get(j));
					if (dependencyT != null) {
						if (dependencyT.EarlyFinish > dependencyMaxEarlyFinish)
								dependencyMaxEarlyFinish = dependencyT.EarlyFinish;
					}
					else {
						System.out.println("There is an error with dependencies of " + t.Name);
					}
				}
				t.EarlyStart = dependencyMaxEarlyFinish;
				t.EarlyFinish = dependencyMaxEarlyFinish + t.Duration;
			}
		}
		
		//Find ending tasks max late finish
		int maxLateFinish = 0;
		for (int i=0; i<tasks.size(); i++) {
			Task t = tasks.get(i);
			if (t.DependencyFor.size() == 0) {
				if (t.EarlyFinish > maxLateFinish) {
					maxLateFinish = t.EarlyFinish;
				}
			}
		}
	
		//Calculate Late Starts and Finishes
		for (int i=(tasks.size()-1); i>=0; i--) {
			Task t = tasks.get(i);
			
			if (t.DependencyFor.size() == 0) {
				t.LateFinish = maxLateFinish;
				t.LateStart = t.LateFinish - t.Duration;
			}
			else {
				//Find dependency with lowest late start
				int dependencyForMinLateStart = Integer.MAX_VALUE;
				for (int j=0; j<t.DependencyFor.size(); j++) {
					Task dependencyForTask = findTask(t.DependencyFor.get(j));
					if (dependencyForTask.LateStart < dependencyForMinLateStart)
							dependencyForMinLateStart = dependencyForTask.LateStart;
				}
				t.LateFinish = dependencyForMinLateStart;
				t.LateStart = t.LateFinish - t.Duration;
			}
			System.out.println(t.Name + " ES: " + t.EarlyStart + " EF: " + t.EarlyFinish + " LS: " + t.LateStart + " LF: " + t.LateFinish);
		}
		
		
	}
	
	public ArrayList<String> findCriticalPath(){
		if (!scheduleHasBeenCalculated) calculatePDMSchedule();
		//create list of tasks that could be possible beginnings of critical paths
		ArrayList<Task> startingTasks = new ArrayList<Task>();
		for (int i = 0; i < tasks.size(); i++)
		{
			Task current = tasks.get(i);
			if (current.EarlyStart == 0 && current.floatIsZero())
			{
				startingTasks.add(current);
			}
		}
		//loop through starting tasks
		for (int j = 0; j < startingTasks.size(); j++)
		{
			Task currentStarting = startingTasks.get(j);
			path = new ArrayList<Task>();
			recursiveFindPath(currentStarting);
		}
		return null;
	}
	
	/**
	 * Helper method searching the list of tasks for a task whose name matches the 
	 * given name
	 * 
	 * @param taskName
	 * @param tasks
	 * @return task we were looking for or null if not found
	 */
	private Task findTask(String taskName) {
		for (int i=0; i<tasks.size(); i++) {
			if (tasks.get(i).Name.equals(taskName)) return tasks.get(i);
		}
		return null;
	}
	
	ArrayList<Task> path;
	
	private void recursiveFindPath(Task toCheck)
	{
		//base case
		if (toCheck.floatIsZero() && toCheck.DependencyFor.size() == 0)
		{
			for (int i = 0; i < path.size(); i++)
			{
				System.out.print(path.get(i).Name);
			}
			System.out.println(toCheck.Name);
			path = new ArrayList<Task>();
		}
		else if (toCheck.floatIsZero())
		{
			for (int i = 0; i < toCheck.DependencyFor.size(); i++)
			{
				//System.out.print(toCheck.Name);
				path.add(toCheck);
				//System.out.println(path.toString());
				Task nextToCheck = findTask(toCheck.DependencyFor.get(i));
				recursiveFindPath(nextToCheck);
			}
		}
		else //not a valid path
		{
			path = new ArrayList<Task>();
			return;
		}
	}
	
}
