/**
 * 
 */
package scheduler;

import java.util.ArrayList;

/**
 * @author Molly
 *
 */
public class Task 
{
	String Name;
	int Duration;
<<<<<<< HEAD
	ArrayList<String> Dependencies;
	
	
	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof Task)
		{
			Task task = (Task) obj;
			if (task.Name == this.Name && task.Duration == this.Duration && task.Dependencies.equals(this.Dependencies))
			{
				return true;
			}
			else return false;
		}
		else return false;
	}
=======
	ArrayList<String> Dependencies = new ArrayList<String>();
	int EarlyStart;
	int EarlyFinish;
	int LateStart;
	int LateFinish;
	ArrayList<String> DependencyFor = new ArrayList<String>();
>>>>>>> origin/master
}
