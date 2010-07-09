package keel.Algorithms.RE_SL_Postprocess.Post_G_TS_Lateral_FRBSs;
/**
 * the class which contains the characteristics of the chromosome
 * @author Diana Arquillos
 */
public class Cromosoma {
	private double [] Gene;	
	private char [] GeneR;
	private double Perf;
	private int HaEntrado;
	
	
	public double gene(int pos){
		return Gene[pos];
	}
	
	public char geneR(int pos){
		return GeneR[pos];
	}
	
	public double perf(){
		
		return Perf;
	}
	public double [] Gene(){
		
		return Gene;
	}
	public char [] GeneR(){
		
		return GeneR;
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
	public Cromosoma(int Genes, int GenesR) {
        Gene = new double [Genes];
        GeneR = new char [GenesR];
        
    }
	public void set_gene(int pos , double value){
		Gene[pos]=value;
	}
	public void set_geneR(int pos , char value){
		GeneR[pos]=value;
	}
}
