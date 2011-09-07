/**
 * <p>
 * File: SMOTE_RSB.java
 * </p>
 *
 * The SMOTE_RSB algorithm is an oversampling method used to deal with the imbalanced
 * problem.
 *
 * @author Written by Enislay Ramentol (University of Camagüey) 07/09/2011 
 * @version 0.1
 * @since JDK1.5
 *
 */

package keel.Algorithms.ImbalancedClassification.Resampling.SMOTE_RSB;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.core.Fichero;
import org.core.Randomize;

import keel.Algorithms.Preprocess.Basic.KNN;
import keel.Algorithms.Preprocess.Basic.Metodo;
import keel.Algorithms.Preprocess.Basic.OutputIS;
import keel.Dataset.Attribute;
import keel.Dataset.Attributes;
import keel.Dataset.InstanceSet;
import keel.Algorithms.ImbalancedClassification.Resampling.SMOTE_RSB.Rough_Sets.FastVector;
import keel.Algorithms.ImbalancedClassification.Resampling.SMOTE_RSB.Rough_Sets.Instance;
import keel.Algorithms.ImbalancedClassification.Resampling.SMOTE_RSB.Rough_Sets.Instances;
import keel.Algorithms.ImbalancedClassification.Resampling.SMOTE_RSB.Rough_Sets.RoughSetsCuttoff;
import keel.Algorithms.ImbalancedClassification.Resampling.SMOTE_RSB.Rough_Sets.RoughSetsOriginal;

public class SMOTE_RSB extends Metodo {
	private long semilla;
	private int kSMOTE;
	private int ASMO;
	private boolean balance;
	private double smoting;
	private int extention;
	private int tipoComparacion;
	private double cutOffInitial;
	private double cutOffFinal;
	private boolean debeContinuar;
	private String ficheroSaTra;
	private String ficheroSaTest;
	private String cadena = "";
	
	
	
	
	
	
	public SMOTE_RSB(String config) {
		super(config);
		
		cadena += "fichero primario de lectura: " + ficheroTraining + "\n";

		ficheroSaTra = getFicheroSalida()[0];
		ficheroSaTest = getFicheroSalida()[1];
		

	}

	public void ejecutar() throws Exception {	
		double tempCutOff = cutOffInitial;
		while (tempCutOff <= cutOffFinal && debeContinuar) {

			boolean termino = false;
			boolean isProgress = true;
			ficheroSaTra = getFicheroSalida()[0];

			ejecutarSMOTE();
			while (!termino && (tempCutOff <= cutOffFinal)) {

				BufferedReader r = new BufferedReader(new FileReader(ficheroSaTra));
				Instances newInst = new Instances(r);
				FastVector finalInstances = new FastVector();
				for (int i = 0; i < originalElementsIndex; i++) {
					finalInstances.addElement(newInst.instance(i));
				}

				 RoughSetsOriginal rs = new RoughSetsOriginal(newInst, tipoComparacion, tempCutOff);
		           rs.lower_aproximation(); 
		           int[][] mylower = rs.get_lower_aproximation();
		           if (!(mylower[0].length ==0 && mylower[1].length==0)){ 
		        	   for(int i=0; i<newInst.numClasses();i++){
		        		   for (int j=0;j< mylower[i].length; j++){
		        			   if ((mylower[i][j]> originalElementsIndex)){
		        				   finalInstances.addElement(newInst.instance(j));
		        				   
		        			   }
		        		   }
		        	   }
		           }
		           else{
		        	   tempCutOff += 0.05;
		        	   continue;
		           }
				

				writeResult(ficheroSaTra, ficheroSaTest, finalInstances, newInst);

				//isProgress = finalInstances.size() > originalElementsIndex;
				termino = finalInstances.size() == newInst.numInstances();
				debeContinuar = !termino;
				
				if (!termino) {
					reconfigure(ficheroSaTra, ficheroSaTest);
					ejecutarSMOTE();
					//termino = !isProgress;
					tempCutOff += 0.05;
				}
				System.out.println(termino + ", is progres: " + isProgress);
				//tempCutOff += 0.05;
			}
			
		
			
		}}

	
	private void writeResult(String ficheroSaTra, String ficheroSaTest,
			FastVector finalInstances, Instances newInst) {
		double[][] data = new double[finalInstances.size()][newInst
				.numAttributes() - 1];
		int[][] dataN = new int[finalInstances.size()][newInst.numAttributes() - 1];
		boolean[][] dataM = new boolean[finalInstances.size()][newInst
				.numAttributes() - 1];
		int[] clases = new int[finalInstances.size()];
		int[] byclass = { 0, 0 };
		for (int i = 0; i < finalInstances.size(); i++) {
			Instance instance = (Instance) finalInstances.elementAt(i);
			for (int j = 0; j < data[i].length; j++) {
				keel.Algorithms.ImbalancedClassification.Resampling.SMOTE_RSB.Rough_Sets.Attribute att = instance.attribute(j);
				data[i][j] = instance.value(j);
				dataN[i][j] = att.type();
				dataM[i][j] = false;
			}
			clases[i] = (int) instance.classValue();
			byclass[clases[i]]++;
		}

		OutputIS.escribeSalida(ficheroSaTra, data, dataN, dataM, clases,
				entradas, salida, nEntradas, relation);
		OutputIS.escribeSalida(ficheroSaTest, test, entradas, salida,
				nEntradas, relation);
		cadena += "-------------------------nueva escritura----------------------------------\n";
		cadena += "fichero de escritura Tra: " + ficheroSaTra + "\n";
		cadena += "fichero de escritura Test: " + ficheroSaTest + "\n";
		cadena += "cantidad de elementos hasta ahora: " + finalInstances.size()
				+ "\n";
		cadena += "cantidad de elementos por clase; clase 0: " + byclass[0]
				+ ", clase 1: " + byclass[1] + "\n";

	}

	
	// //////////////////////////////////////////SMOTE//////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////////////////
	
	//////////////////////////////////////////////////////////////////////////////////////////////////
	
	private int minorityID, mayorityID = -1;
	private int originalElementsIndex;
	public void ejecutarSMOTE() {

		int nPos = 0;
		int nNeg = 0;
		int i, j, l, m;
		int tmp;
		int positives[];
		// double conjS[][];
		double conjR[][];
		int conjN[][];
		boolean conjM[][];
		int clasesS[];
		double genS[][];
		double genR[][];
		int genN[][];
		boolean genM[][];
		int clasesGen[];
		int tamS;
		int pos;
		int neighbors[][];
		int nn;

		long tiempo = System.currentTimeMillis();

		/* Count of number of positive and negative examples */
		for (i = 0; i < clasesTrain.length; i++) {
			if (clasesTrain[i] == 0)
				nPos++;
			else
				nNeg++;
		}
		originalElementsIndex = nPos + nNeg;
		
		if (nPos > 0 && nNeg > 0) {

			if (nPos > nNeg) {
				tmp = nPos;
				nPos = nNeg;
				nNeg = tmp;
				minorityID = 1;
				mayorityID = 0;
			} else {
				minorityID = 0;
				mayorityID = 1;
			}

			/* Localize the positive instances */
			positives = new int[nPos];
			for (i = 0, j = 0; i < clasesTrain.length; i++) {
				if (clasesTrain[i] == minorityID) {
					positives[j] = i;
					j++;
				}
			}

			/* Randomize the instance presentation */
			Randomize.setSeed(semilla);
			for (i = 0; i < positives.length; i++) {
				tmp = positives[i];
				pos = Randomize.Randint(0, positives.length - 1);
				positives[i] = positives[pos];
				positives[pos] = tmp;
			}

			/* Obtain k-nearest neighbors of each positive instance */
			neighbors = new int[positives.length][kSMOTE];
			for (i = 0; i < positives.length; i++) {
				switch (ASMO) {
				case 0:
					KNN.evaluacionKNN2(kSMOTE, datosTrain, realTrain,
							nominalTrain, nulosTrain, clasesTrain,
							datosTrain[positives[i]], realTrain[positives[i]],
							nominalTrain[positives[i]],
							nulosTrain[positives[i]], 2, distanceEu,
							neighbors[i]);
					break;
				case 1:
					evaluacionKNNClass(kSMOTE, datosTrain, realTrain,
							nominalTrain, nulosTrain, clasesTrain,
							datosTrain[positives[i]], realTrain[positives[i]],
							nominalTrain[positives[i]],
							nulosTrain[positives[i]], 2, distanceEu,
							neighbors[i], minorityID);
					break;
				case 2:
					evaluacionKNNClass(kSMOTE, datosTrain, realTrain,
							nominalTrain, nulosTrain, clasesTrain,
							datosTrain[positives[i]], realTrain[positives[i]],
							nominalTrain[positives[i]],
							nulosTrain[positives[i]], 2, distanceEu,
							neighbors[i], mayorityID);
					break;
				}
			}

			/* Interpolation of the minority instances */
			if (balance) {
				genS = new double[nNeg - nPos][datosTrain[0].length];
				genR = new double[nNeg - nPos][datosTrain[0].length];
				genN = new int[nNeg - nPos][datosTrain[0].length];
				genM = new boolean[nNeg - nPos][datosTrain[0].length];
				clasesGen = new int[nNeg - nPos];
			} else {
				genS = new double[(int) (nPos * smoting)][datosTrain[0].length];
				genR = new double[(int) (nPos * smoting)][datosTrain[0].length];
				genN = new int[(int) (nPos * smoting)][datosTrain[0].length];
				genM = new boolean[(int) (nPos * smoting)][datosTrain[0].length];
				clasesGen = new int[(int) (nPos * smoting)];
			}
			for (i = 0; i < genS.length; i++) {
				clasesGen[i] = minorityID;
				nn = Randomize.Randint(0, kSMOTE - 1);
				interpola(realTrain[positives[i % positives.length]],
						realTrain[neighbors[i % positives.length][nn]],
						nominalTrain[positives[i % positives.length]],
						nominalTrain[neighbors[i % positives.length][nn]],
						nulosTrain[positives[i % positives.length]],
						nulosTrain[neighbors[i % positives.length][nn]],
						genS[i], genR[i], genN[i], genM[i]);
			}

			if (balance) {
				tamS = 2 * nNeg;
			} else {
				tamS = nNeg + nPos + (int) (nPos * smoting);
			}
			/* Construction of the S set from the previous vector S */
			// conjS = new double[tamS][datosTrain[0].length];
			conjR = new double[tamS][datosTrain[0].length];
			conjN = new int[tamS][datosTrain[0].length];
			conjM = new boolean[tamS][datosTrain[0].length];
			clasesS = new int[tamS];
			for (j = 0; j < datosTrain.length; j++) {
				for (l = 0; l < datosTrain[0].length; l++) {
					// conjS[j][l] = datosTrain[j][l];
					conjR[j][l] = realTrain[j][l];
					conjN[j][l] = nominalTrain[j][l];
					conjM[j][l] = nulosTrain[j][l];
				}
				clasesS[j] = clasesTrain[j];
			}
			for (m = 0; j < tamS; j++, m++) {
				for (l = 0; l < datosTrain[0].length; l++) {
					// conjS[j][l] = genS[m][l];
					conjR[j][l] = genR[m][l];
					conjN[j][l] = genN[m][l];
					conjM[j][l] = genM[m][l];
				}
				clasesS[j] = clasesGen[m];
			}

			System.out.println("SMOTE_RSB " + relation + " "
					+ (double) (System.currentTimeMillis() - tiempo) / 1000.0
					+ "s");

			OutputIS.escribeSalida(ficheroSalida[0], conjR, conjN, conjM,
					clasesS, entradas, salida, nEntradas, relation);
			OutputIS.escribeSalida(ficheroSalida[1], test, entradas, salida,
					nEntradas, relation);

		}
	}

	public static int evaluacionKNNClass(int nvec, double conj[][],
			double real[][], int nominal[][], boolean nulos[][], int clases[],
			double ejemplo[], double ejReal[], int ejNominal[],
			boolean ejNulos[], int nClases, boolean distance, int vecinos[],
			int clase) {

		int i, j, l;
		boolean parar = false;
		int vecinosCercanos[];
		double minDistancias[];
		int votos[];
		double dist;
		int votada, votaciones;

		if (nvec > conj.length)
			nvec = conj.length;

		votos = new int[nClases];
		vecinosCercanos = new int[nvec];
		minDistancias = new double[nvec];
		for (i = 0; i < nvec; i++) {
			vecinosCercanos[i] = -1;
			minDistancias[i] = Double.POSITIVE_INFINITY;
		}

		for (i = 0; i < conj.length; i++) {
			dist = KNN.distancia(conj[i], real[i], nominal[i], nulos[i],
					ejemplo, ejReal, ejNominal, ejNulos, distance);
			if (dist > 0 && clases[i] == clase) {
				parar = false;
				for (j = 0; j < nvec && !parar; j++) {
					if (dist < minDistancias[j]) {
						parar = true;
						for (l = nvec - 1; l >= j + 1; l--) {
							minDistancias[l] = minDistancias[l - 1];
							vecinosCercanos[l] = vecinosCercanos[l - 1];
						}
						minDistancias[j] = dist;
						vecinosCercanos[j] = i;
					}
				}
			}
		}

		for (j = 0; j < nClases; j++) {
			votos[j] = 0;
		}

		for (j = 0; j < nvec; j++) {
			if (vecinosCercanos[j] >= 0)
				votos[clases[vecinosCercanos[j]]]++;
		}

		votada = 0;
		votaciones = votos[0];
		for (j = 1; j < nClases; j++) {
			if (votaciones < votos[j]) {
				votaciones = votos[j];
				votada = j;
			}
		}

		for (i = 0; i < vecinosCercanos.length; i++)
			vecinos[i] = vecinosCercanos[i];

		return votada;
	}

	void interpola(double ra[], double rb[], int na[], int nb[], boolean ma[],
			boolean mb[], double resS[], double resR[], int resN[],
			boolean resM[]) {

		int i;
		double diff;
		double gap;
		int suerte;

		for (i = 0; i < ra.length; i++) {
			if (ma[i] == true && mb[i] == true) {
				resM[i] = true;
				resS[i] = 0;
			} else if (ma[i] == true) {
				if (entradas[i].getType() == Attribute.REAL) {
					resR[i] = rb[i];
					resS[i] = (resR[i] + entradas[i].getMinAttribute())
							/ (entradas[i].getMaxAttribute() - entradas[i]
									.getMinAttribute());
				} else if (entradas[i].getType() == Attribute.INTEGER) {
					resR[i] = rb[i];
					resS[i] = (resR[i] + entradas[i].getMinAttribute())
							/ (entradas[i].getMaxAttribute() - entradas[i]
									.getMinAttribute());
				} else {
					resN[i] = nb[i];
					resS[i] = (double) resN[i]
							/ (double) (entradas[i].getNominalValuesList()
									.size() - 1);
				}
			} else if (mb[i] == true) {
				if (entradas[i].getType() == Attribute.REAL) {
					resR[i] = ra[i];
					resS[i] = (resR[i] + entradas[i].getMinAttribute())
							/ (entradas[i].getMaxAttribute() - entradas[i]
									.getMinAttribute());
				} else if (entradas[i].getType() == Attribute.INTEGER) {
					resR[i] = ra[i];
					resS[i] = (resR[i] + entradas[i].getMinAttribute())
							/ (entradas[i].getMaxAttribute() - entradas[i]
									.getMinAttribute());
				} else {
					resN[i] = na[i];
					resS[i] = (double) resN[i]
							/ (double) (entradas[i].getNominalValuesList()
									.size() - 1);
				}
			} else {
				resM[i] = false;
				if (entradas[i].getType() == Attribute.REAL) {
					diff = rb[i] - ra[i];
					gap = Randomize.Rand();
					resR[i] = ra[i] + gap * diff;
					resS[i] = (resR[i] + entradas[i].getMinAttribute())
							/ (entradas[i].getMaxAttribute() - entradas[i]
									.getMinAttribute());
				} else if (entradas[i].getType() == Attribute.INTEGER) {
					diff = rb[i] - ra[i];
					gap = Randomize.Rand();
					resR[i] = Math.round(ra[i] + gap * diff);
					resS[i] = (resR[i] + entradas[i].getMinAttribute())
							/ (entradas[i].getMaxAttribute() - entradas[i]
									.getMinAttribute());
				} else {
					suerte = Randomize.Randint(0, 2);
					if (suerte == 0) {
						resN[i] = na[i];
					} else {
						resN[i] = nb[i];
					}
					resS[i] = (double) resN[i]
							/ (double) (entradas[i].getNominalValuesList()
									.size() - 1);
				}
			}
		}
	}

	public void leerConfiguracion(String ficheroScript) {

		String fichero, linea, token;
		StringTokenizer lineasFichero, tokens;
		byte line[];
		int i, j;

		ficheroSalida = new String[2];

		fichero = Fichero.leeFichero(ficheroScript);
		lineasFichero = new StringTokenizer(fichero, "\n\r");

		lineasFichero.nextToken();
		linea = lineasFichero.nextToken();

		tokens = new StringTokenizer(linea, "=");
		tokens.nextToken();
		token = tokens.nextToken();

		/* Getting the names of the training and test files */
		line = token.getBytes();
		for (i = 0; line[i] != '\"'; i++)
			;
		i++;
		for (j = i; line[j] != '\"'; j++)
			;
		ficheroTraining = new String(line, i, j - i);
		for (i = j + 1; line[i] != '\"'; i++)
			;
		i++;
		for (j = i; line[j] != '\"'; j++)
			;
		ficheroTest = new String(line, i, j - i);

		/* Getting the path and base name of the results files */
		linea = lineasFichero.nextToken();
		tokens = new StringTokenizer(linea, "=");
		tokens.nextToken();
		token = tokens.nextToken();

		/* Getting the names of output files */
		line = token.getBytes();
		for (i = 0; line[i] != '\"'; i++)
			;
		i++;
		for (j = i; line[j] != '\"'; j++)
			;
		ficheroSalida[0] = new String(line, i, j - i);
		for (i = j + 1; line[i] != '\"'; i++)
			;
		i++;
		for (j = i; line[j] != '\"'; j++)
			;
		ficheroSalida[1] = new String(line, i, j - i);

		/* Getting the seed */
		linea = lineasFichero.nextToken();
		tokens = new StringTokenizer(linea, "=");
		tokens.nextToken();
		semilla = Long.parseLong(tokens.nextToken().substring(1));

		/* Getting the number of neighbors */
		linea = lineasFichero.nextToken();
		tokens = new StringTokenizer(linea, "=");
		tokens.nextToken();
		kSMOTE = Integer.parseInt(tokens.nextToken().substring(1));

		/* Getting the type of SMOTE algorithm */
		linea = lineasFichero.nextToken();
		tokens = new StringTokenizer(linea, "=");
		tokens.nextToken();
		token = tokens.nextToken();
		token = token.substring(1);
		if (token.equalsIgnoreCase("both"))
			ASMO = 0;
		else if (token.equalsIgnoreCase("minority"))
			ASMO = 1;
		else
			ASMO = 2;

		/* Getting the type of balancing in SMOTE */
		linea = lineasFichero.nextToken();
		tokens = new StringTokenizer(linea, "=");
		tokens.nextToken();
		token = tokens.nextToken();
		token = token.substring(1);
		if (token.equalsIgnoreCase("YES"))
			balance = true;
		else
			balance = false;

		/* Getting the quantity of smoting */
		linea = lineasFichero.nextToken();
		tokens = new StringTokenizer(linea, "=");
		tokens.nextToken();
		smoting = Double.parseDouble(tokens.nextToken().substring(1));

		/* Getting the type of distance function 
		linea = lineasFichero.nextToken();
		tokens = new StringTokenizer(linea, "=");
		tokens.nextToken();
		distanceEu = tokens.nextToken().substring(1).equalsIgnoreCase(
				"Euclidean") ? true : false;


		/* Getting the type of extention */
		linea = lineasFichero.nextToken();
		tokens = new StringTokenizer(linea, "=");
		tokens.nextToken();
		extention = Integer.parseInt(tokens.nextToken().substring(1));
		
		
		/* Getting the type of comparation */
		linea = lineasFichero.nextToken();
		tokens = new StringTokenizer(linea, "=");
		tokens.nextToken();
		tipoComparacion = Integer.parseInt(tokens.nextToken().substring(1));


		/* Getting the initial cutoff's value */
		linea = lineasFichero.nextToken();
		tokens = new StringTokenizer(linea, "=");
		tokens.nextToken();
		cutOffInitial = Double.parseDouble(tokens.nextToken().substring(1));

		/* Getting the final cutoff's value */
		linea = lineasFichero.nextToken();
		tokens = new StringTokenizer(linea, "=");
		tokens.nextToken();
		cutOffFinal = Double.parseDouble(tokens.nextToken().substring(1));
	
		
		linea = lineasFichero.nextToken();
		tokens = new StringTokenizer(linea, "=");
		tokens.nextToken();
		token = tokens.nextToken();
		token = token.substring(1);
		if (token.equalsIgnoreCase("YES"))
			debeContinuar = true;
		else
			debeContinuar = false;

	}

	public void reconfigure(String ficheroTra, String ficheroTest) {
		int nClases, i, j, l, m, n;
		double VDM;
		int Naxc, Nax, Nayc, Nay;
		double media, SD;

		Attributes.clearAll();
		/* Read of data files */
		try {
			training = new InstanceSet();
			training.readSet(ficheroTra, true);

			/* Normalize and check the data */
			normalizar();
		} catch (Exception e) {
			System.err.println(e);
			System.exit(1);
		}

		try {
			test = new InstanceSet();
			test.readSet(ficheroTest, false);
		} catch (Exception e) {
			System.err.println(e);
			System.exit(1);
		}

		/* Previous computation for HVDM distance */
		if (distanceEu == false) {
			stdDev = new double[Attributes.getInputNumAttributes()];
			nominalDistance = new double[Attributes.getInputNumAttributes()][][];
			nClases = Attributes.getOutputAttribute(0).getNumNominalValues();
			for (i = 0; i < nominalDistance.length; i++) {
				if (Attributes.getInputAttribute(i).getType() == Attribute.NOMINAL) {
					nominalDistance[i] = new double[Attributes
							.getInputAttribute(i).getNumNominalValues()][Attributes
							.getInputAttribute(i).getNumNominalValues()];
					for (j = 0; j < Attributes.getInputAttribute(i)
							.getNumNominalValues(); j++) {
						nominalDistance[i][j][j] = 0.0;
					}
					for (j = 0; j < Attributes.getInputAttribute(i)
							.getNumNominalValues(); j++) {
						for (l = j + 1; l < Attributes.getInputAttribute(i)
								.getNumNominalValues(); l++) {
							VDM = 0.0;
							Nax = Nay = 0;
							for (m = 0; m < training.getNumInstances(); m++) {
								if (nominalTrain[m][i] == j) {
									Nax++;
								}
								if (nominalTrain[m][i] == l) {
									Nay++;
								}
							}
							for (m = 0; m < nClases; m++) {
								Naxc = Nayc = 0;
								for (n = 0; n < training.getNumInstances(); n++) {
									if (nominalTrain[n][i] == j
											&& clasesTrain[n] == m) {
										Naxc++;
									}
									if (nominalTrain[n][i] == l
											&& clasesTrain[n] == m) {
										Nayc++;
									}
								}
								VDM += (((double) Naxc / (double) Nax) - ((double) Nayc / (double) Nay))
										* (((double) Naxc / (double) Nax) - ((double) Nayc / (double) Nay));
							}
							nominalDistance[i][j][l] = Math.sqrt(VDM);
							nominalDistance[i][l][j] = Math.sqrt(VDM);
						}
					}
				} else {
					media = 0;
					SD = 0;
					for (j = 0; j < training.getNumInstances(); j++) {
						media += realTrain[j][i];
						SD += realTrain[j][i] * realTrain[j][i];
					}
					media /= (double) realTrain.length;
					stdDev[i] = Math.sqrt((SD / ((double) realTrain.length))
							- (media * media));
				}
			}
		}
	}

	public String[] getFicheroSalida() {
		return ficheroSalida;
	}

	public String getFicheroTraining() {
		return ficheroTraining;
	}

	public void setFicheroTraining(String ficheroTraining) {
		this.ficheroTraining = ficheroTraining;
	}

	public String getFicheroTest() {
		return ficheroTest;
	}

	public InstanceSet getTest() {
		return test;
	}

	public void setFicheroTest(String ficheroTest) {
		this.ficheroTest = ficheroTest;
	}

	public Attribute[] getEntradas() {
		return entradas;
	}

	public Attribute getSalida() {
		return salida;
	}

	public int getNEntradas() {
		return nEntradas;
	}

	public String getRelation() {
		return relation;
	}

	public int getOriginalElementsIndex() {
		return originalElementsIndex;
	}

}
