package keel.Algorithms.UnsupervisedLearning.AssociationRules.FuzzyRuleLearning.Fingrams;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public class SystemCommandExecutor {

	private List<String> commandInformation;
	private String adminPassword;
	private ThreadedStreamHandler inputStreamHandler;
	private ThreadedStreamHandler errorStreamHandler;

	public SystemCommandExecutor(final List<String> commandInformation) {
		if (commandInformation == null)
			throw new NullPointerException(
					"The commandInformation is required.");
		this.commandInformation = commandInformation;
		this.adminPassword = null;
	}

	public int executeCommand() throws IOException, InterruptedException {
		int exitValue = -99;

		try {
			ProcessBuilder pb = new ProcessBuilder(commandInformation);
			Process process = pb.start();

			OutputStream stdOutput = process.getOutputStream();

			InputStream inputStream = process.getInputStream();
			InputStream errorStream = process.getErrorStream();

			inputStreamHandler = new ThreadedStreamHandler(inputStream,
					stdOutput, adminPassword);
			errorStreamHandler = new ThreadedStreamHandler(errorStream);

			inputStreamHandler.start();
			errorStreamHandler.start();

			exitValue = process.waitFor();

			//System.out.println("Finalizing executeCommand");
			
			inputStreamHandler.interrupt();
			errorStreamHandler.interrupt();
			inputStreamHandler.join();
			errorStreamHandler.join();
		} catch (IOException e) {
			throw e;
		} catch (InterruptedException e) {
			throw e;
		} finally {
			return exitValue;
		}
	}

	public StringBuilder getStandardOutputFromCommand() {
		return inputStreamHandler.getOutputBuffer();
	}

	public StringBuilder getStandardErrorFromCommand() {
		return errorStreamHandler.getOutputBuffer();
	}

}
