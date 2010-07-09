package keel.Algorithms.Statistical_Classifiers.Shared.MatrixCalcs;

public class ErrorSingular extends Exception {
    ErrorSingular(String reason) {
		super(reason);
    }
    public String toString() {
		return "Matriz singular: " +getMessage();
    }
}

