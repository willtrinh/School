/** FCFSSchedulingAlgorithm.java
 * 
 * A first-come first-served scheduling algorithm.
 *
 * @author: Will Trinh
 * Spring 2018
 * CS 143A Programming Assignment
 *
 */
package com.jimweller.cpuscheduler;

import java.util.*;

public class FCFSSchedulingAlgorithm extends BaseSchedulingAlgorithm {
	private Vector<Process> jobs;
	
    FCFSSchedulingAlgorithm(){
        // Fill in this method
        /*------------------------------------------------------------*/
    	jobs = new Vector<Process>();


        /*------------------------------------------------------------*/
    }

    /** Add the new job to the correct queue.*/
    public void addJob(Process p){
        // Fill in this method
        /*------------------------------------------------------------*/
    	jobs.add(p);


        /*------------------------------------------------------------*/
    }
    
    /** Returns true if the job was present and was removed. */
    public boolean removeJob(Process p){
        // Fill in this method
        /*------------------------------------------------------------*/
    	return jobs.remove(p);


        /*------------------------------------------------------------*/
    }

    /** Transfer all the jobs in the queue of a SchedulingAlgorithm to another, such as
    when switching to another algorithm in the GUI */
    public void transferJobsTo(SchedulingAlgorithm otherAlg) {
    	for (int i = jobs.size()-1; i >= 0; i--) 
    	{
    	    Process temp = jobs.peek();
    	    this.removeJob(temp);
    	    otherAlg.addJob(temp);
    	}
    }
    
    public boolean shouldPreempt(long currentTime){
    	return false;
    }

    /** Returns the next process that should be run by the CPU, null if none available.*/
    public Process getNextJob(long currentTime){
    	return processQueue.peek();
    }

    public String getName(){
        return "First-Come First-Served";
    }
    
}