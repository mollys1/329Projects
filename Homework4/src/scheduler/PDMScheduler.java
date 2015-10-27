package scheduler;

import java.util.ArrayList;

public class PDMScheduler {
	
	private boolean scheduleHasBeenCalculated = false;
	
	public void calculatePDMSchedule(ArrayList<Task> tasks) {
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
					Task dependencyT = findTask(t.Dependencies.get(j), tasks);
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
	
		//Calculate Late Starts and Finishes
		for (int i=(tasks.size()-1); i>=0; i--) {
			Task t = tasks.get(i);
			
			if (t.DependencyFor.size() == 0) {
				t.LateFinish = t.EarlyFinish;
				t.LateStart = t.EarlyFinish - t.Duration;
			}
			else {
				//Find dependency with lowest late start
				int dependencyForMinLateStart = Integer.MAX_VALUE;
				for (int j=0; j<t.DependencyFor.size(); j++) {
					Task dependencyForTask = findTask(t.DependencyFor.get(j), tasks);
					if (dependencyForTask.LateStart < dependencyForMinLateStart)
							dependencyForMinLateStart = dependencyForTask.LateStart;
				}
				t.LateFinish = dependencyForMinLateStart;
				t.LateStart = t.LateFinish - t.Duration;
			}
			System.out.println(t.Name + " ES: " + t.EarlyStart + " EF: " + t.EarlyFinish + " LS: " + t.LateStart + " LF: " + t.LateFinish);
		}
		
		
	}
	
	public ArrayList<String> findCriticalPath(ArrayList<Task> tasks){
		if (!scheduleHasBeenCalculated) calculatePDMSchedule(tasks);
		
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
	private Task findTask(String taskName, ArrayList<Task> tasks) {
		for (int i=0; i<tasks.size(); i++) {
			if (tasks.get(i).Name.equals(taskName)) return tasks.get(i);
		}
		return null;
	}

}
