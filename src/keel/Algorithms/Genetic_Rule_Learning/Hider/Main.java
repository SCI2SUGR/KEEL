/*
 * Created on 12-feb-2005
 *
 */
package keel.Algorithms.Genetic_Rule_Learning.Hider;

import java.io.FileNotFoundException;
import java.io.IOException;


/**
 * @author Sebas
 */
public final class Main {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Error. Only a parameter is needed.");
        }
        else {
            try {
                new Hider(args[0]);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
