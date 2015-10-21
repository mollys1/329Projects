package scheduler;

import java.util.ArrayList;

public class PDMScheduler {
	
	
	public void calculatePDMSchedule(ArrayList<Task> tasks) {
		
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
					Task dependencyT = findDependencyTask(t.Dependencies.get(j), tasks);
					if (dependencyT.EarlyFinish > dependencyMaxEarlyFinish)
						dependencyMaxEarlyFinish = dependencyT.EarlyFinish;
				}
				t.EarlyStart = dependencyMaxEarlyFinish;
				t.EarlyFinish = dependencyMaxEarlyFinish + t.Duration;
			}
			System.out.println(t.Name + " ES: " + t.EarlyStart + " EF: " + t.EarlyFinish);
			
		}
		
	}
	
	public ArrayList<String> findCriticalPath(){
		
		return null;
	}
	
	private Task findDependencyTask(String dependencyTaskName, ArrayList<Task> tasks) {
		for (int i=0; i<tasks.size(); i++) {
			if (tasks.get(i).Name.equals(dependencyTaskName)) return tasks.get(i);
		}
		return null;
	}

}
