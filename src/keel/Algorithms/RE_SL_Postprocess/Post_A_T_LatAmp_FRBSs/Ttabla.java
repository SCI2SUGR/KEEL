package keel.Algorithms.RE_SL_Postprocess.Post_A_T_LatAmp_FRBSs;
/**
 * Class that defines the Ttabla
 * @author Diana Arquillos
 *
 */
public class Ttabla {
	public double [] ejemplo; 
	public double nivel_cubrimiento, maximo_cubrimiento;
    int cubierto;
 
/**
 * Constructor with one parameter
 * @param valor it defines the size of vector ejemplo 
 */    public Ttabla(int valor){
    	ejemplo=new double [valor];
    	this.nivel_cubrimiento = 0.0;
		this.maximo_cubrimiento = 0.0;
		this.cubierto = 0;
    }

/**
 * Constructor that defines all elements of the class
 * @param ejemplo it defines the vector
 * @param nivel_cubrimiento it defines the nivel_cubrimiento
 * @param maximo_cubrimiento it defines the maximo_cubrimiento
 * @param cubierto it defines the cubierto
 */    public Ttabla(double [] ejemplo, double nivel_cubrimiento,
			double maximo_cubrimiento, int cubierto) {
		super();
		this.ejemplo = ejemplo;
		this.nivel_cubrimiento = nivel_cubrimiento;
		this.maximo_cubrimiento = maximo_cubrimiento;
		this.cubierto = cubierto;
	}
    
}
