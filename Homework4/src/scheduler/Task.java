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
	ArrayList<String> Dependencies = new ArrayList<String>();
	int EarlyStart;
	int EarlyFinish;
	int LateStart;
	int LateFinish;
}
