package keel.Algorithms.UnsupervisedLearning.AssociationRules.FuzzyRuleLearning.Fingrams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;

public class ThreadedStreamHandler extends Thread
{
	  InputStream inputStream;
	  String adminPassword;
	  OutputStream outputStream;
	  PrintWriter printWriter;
	  StringBuilder outputBuffer = new StringBuilder();
	  private boolean sudoIsRequested = false;
	  
	  ThreadedStreamHandler(InputStream inputStream)
	  {
	    this.inputStream = inputStream;
	  }

	  ThreadedStreamHandler(InputStream inputStream, OutputStream outputStream, String adminPassword)
	  {
	    this.inputStream = inputStream;
	    this.outputStream = outputStream;
	    this.printWriter = new PrintWriter(outputStream);
	    this.adminPassword = adminPassword;
	    this.sudoIsRequested = true;
	  }
	  
	  public void run()
	  {
		  if (sudoIsRequested)
		    {
			  printWriter.println(adminPassword);
		      printWriter.flush();
		    }
		  
		  BufferedReader bufferedReader = null;
		    try
		    {
		      bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
		      String line = null;
		      while ((line = bufferedReader.readLine()) != null)
		      {
		        outputBuffer.append(line + "\n");
		      }
		    }
		    catch (IOException ioe)
		    {
		    	  ioe.printStackTrace();
		    }
		    catch (Throwable t)
		    {
		    	  t.printStackTrace();
		    }
		    finally
		    {
		      try
		      {
		        bufferedReader.close();
		      }
		      catch (IOException e)
		      {
		      
		      }
		    }
	  }
	  private void doSleep(long millis)
	  {
	    try
	    {
	      Thread.sleep(millis);
	    }
	    catch (InterruptedException e)
	    {
	      // ignore
	    }
	  }
	  public StringBuilder getOutputBuffer()
	  {
	    return outputBuffer;
	  }
}
