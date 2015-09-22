/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package schedulingalgos;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author nithishkp
 */

interface SchedulingMethod
{
    int FIFO = 0;
    int SJF = 1;
    int ROUND_ROBBIN = 2;
}
class SortProcess implements Comparator
{
    public int compare(Object m1,Object m2)
    {
        MiniProcess mp1 = (MiniProcess)m1;
        MiniProcess mp2 = (MiniProcess)m2;
                
        return mp1.getExecutionTime()-mp2.getExecutionTime();
    }
}
class Scheduler implements Runnable,SchedulingMethod{
    
    ArrayList <MiniProcess>processList;
    int SCHEDULING_METHOD;
    int slotTime;
    Scheduler(ArrayList processList,int SCHEDULING_METHOD,int slotTime)
    {
        this.processList = processList;
        this.SCHEDULING_METHOD = SCHEDULING_METHOD;
        this.slotTime = slotTime;
    }
    
    public void SJFScheduling()
    {
        processList.sort(new SortProcess());
        
     for(int i = 0; i<processList.size(); i++)
     {
         System.out.println("Starting Thread:"+processList.get(i).toString());
         
         new Thread(processList.get(i)).start();
         int temp = processList.get(i).getExecutionTime();
            
         do{
            temp--;
             try {
                 Thread.sleep(1000);
             } catch (InterruptedException ex) {
                 Logger.getLogger(Scheduler.class.getName()).log(Level.SEVERE, null, ex);
             }
         }while(temp>0);
     }
      
    }
    public void FIFOScheduling()
    {        
        
     for(int i = 0; i<processList.size(); i++)
     {
         System.out.println("Starting Thread:"+processList.get(i).toString());
         
         new Thread(processList.get(i)).start();
         int temp = processList.get(i).getExecutionTime();
            
         do{
            temp--;
             try {
                 Thread.sleep(200);
             } catch (InterruptedException ex) {
                 Logger.getLogger(Scheduler.class.getName()).log(Level.SEVERE, null, ex);
             }
         }while(temp>0);
     }
        
    }
        
    public void RoudRobinScheduling()
    {
     int slotTime = this.slotTime;
     int processFinishedCount = 0;
     int i = 0;
     
     for(; processFinishedCount<processList.size();i = (i+1)%processList.size())
     {
         int processTimeLimit = 0;
         System.out.println("Starting Thread:"+processList.get(i).toString());
         
         if(processList.get(i).getCurrentTime() < processList.get(i).getExecutionTime())
         {
             processList.get(i).setRunningState(true);         
             new Thread(processList.get(i)).start();
             processList.get(i).getExecutionTime();          
         }
         else
         {
             processFinishedCount++;
             System.out.println("Ending Thread:"+processList.get(i).toString());
             continue;
         }
         
         processTimeLimit = processList.get(i).getCurrentTime()+slotTime;
         
         do{
           
             try {
                 Thread.sleep(200);
             } catch (InterruptedException ex) {
                 Logger.getLogger(Scheduler.class.getName()).log(Level.SEVERE, null, ex);
             }
         }while(processList.get(i).getCurrentTime()<processTimeLimit && processList.get(i).getCurrentTime() < processList.get(i).getExecutionTime());
         
         synchronized(ProcessController.getProcessController())
         {
             processList.get(i).setRunningState(false);
         }         
     }
    
        
    }
    public void run()
    {     
        switch(SCHEDULING_METHOD)
        {
            case FIFO : FIFOScheduling();
                        break;
                
            case SJF : SJFScheduling();
                        break;
              
            case ROUND_ROBBIN : RoudRobinScheduling();
                         break;
           
            default : break;
        }
       
    }
    
    
}
class ProcessController
{
    private static ProcessController singleInstance;
    public static ProcessController getProcessController()
    {
        if(singleInstance == null)
        {
            singleInstance = new ProcessController();
        }
        
        return singleInstance;
    }
    
    private ProcessController()
    {
        singleInstance = null;
    }
    
}
class MiniProcess implements Runnable{
  private boolean runningState;
  private int executionTime;
  private int currentTime;
  private int arrivalTime;
  private String threadName;
  
  MiniProcess(String threadName,int arrivalTime,int executionTime)
  {
      Thread.currentThread().setName(threadName);
      this.threadName = threadName;
      this.executionTime = executionTime;
      this.arrivalTime=arrivalTime;
      currentTime = 0;
      runningState = true;
  }
  public String toString()
  {
      return threadName;
  }
  public int getExecutionTime()
  {
      return executionTime;
  }
  public int getCurrentTime()
  {
      return currentTime;
  }
  public boolean getRunningState()
  {
      return runningState;
  }
  
  public void setRunningState(boolean state)
  {
      runningState = state; 
  }
  
  public void run()
  { 
      
      while(getRunningState() && currentTime<executionTime)
      {
          synchronized(ProcessController.getProcessController())
          {
            if(!getRunningState())
            {
              break;
            }
          }
          System.out.println(threadName+" "+currentTime);
          currentTime++;
          try {
              Thread.sleep(1000);
          } catch (InterruptedException ex) {
              Logger.getLogger(MiniProcess.class.getName()).log(Level.SEVERE, null, ex);
          }
      }
      
  }
}

public class SchedulingAlgos {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
 
        ArrayList<MiniProcess> processList = new ArrayList<MiniProcess>();
     
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter the number of process :"); 
        int processCount = sc.nextInt();
        
        for(int i =0;i<processCount;i++)
        {
            System.out.println("Enter the arrivalTime for process :"+(i+1));
            int arrivalTime = sc.nextInt();
            System.out.println("Enter the executionTime for process :"+(i+1));
            int executionTime = sc.nextInt();
            
            processList.add(new MiniProcess("Thread"+(i+1),arrivalTime,executionTime));
        }
        System.out.println("Enter Scheduling Scheme : 0:FIFO,1:SJF,2:RoundRobin");
        int schedulingScheme = sc.nextInt();
        int slotTime =0;
        if(schedulingScheme == 2)
        {
          System.out.println("Enter Slot time : ");          
          slotTime = sc.nextInt();
        }
        Scheduler sc1 = new Scheduler(processList,schedulingScheme,slotTime);
        new Thread(sc1).start();
        
                
    }
    
}
