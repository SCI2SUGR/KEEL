package keel.Algorithms.Statistical_Classifiers.Shared.MatrixCalcs;

public class ErrorDimension extends Exception {
    ErrorDimension(String reason) {
		super(reason);
    }
    public String toString() {
		return "Dimensiones incorrectas: " +getMessage();
    }
}
