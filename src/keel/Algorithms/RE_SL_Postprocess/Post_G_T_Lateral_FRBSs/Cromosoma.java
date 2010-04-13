package keel.Algorithms.RE_SL_Postprocess.Post_G_T_Lateral_FRBSs;
/**
 * the class which contains the characteristics of the chromosome
 * @author Diana Arquillos
 */
public class Cromosoma {
	private double [] Gene;	
	private double Perf;
	private int HaEntrado;
	
	public double gene(int pos){
		return Gene[pos];
	}
	
	public double perf(){
		
		return Perf;
	}
	public double [] Gene(){
		
		return Gene;
	}
	public void set_perf(double value){
		Perf=value;
		
	}
	public int entrado(){
		
		return HaEntrado;
	}
	public void set_entrado(int value){
		HaEntrado=value;
		
	}
	public Cromosoma(int Genes) {
        Gene = new double [Genes];
    }
	public void set_gene(int pos , double value){
		Gene[pos]=value;
	}
	
}
