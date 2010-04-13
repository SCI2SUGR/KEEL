package keel.Algorithms.RE_SL_Postprocess.Post_A_TS_LatAmp_FRBSs;
/**
 * the class which contains the characteristics of the chromosome
 * @author Diana Arquillos
 */
public class Cromosoma {
	private double [] Gene;	
	private double Perf;
	private int HaEntrado;
	private double [] GeneA;
	private char [] GeneR;
	
	
	public double gene(int pos){
		return Gene[pos];
	}
	public double geneA(int pos){
		return GeneA[pos];
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
	public double [] GeneA(){
		
		return GeneA;
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
	public Cromosoma(int Genes,int GenesA, int GenesR) {
        Gene = new double [Genes];
        GeneA = new double [GenesA];
        GeneR = new char [GenesR];
    }
	public void set_gene(int pos , double value){
		Gene[pos]=value;
	}
	
	public void set_geneA(int pos , double value){
		GeneA[pos]=value;
	}
	public void set_geneR(int pos , char value){
		GeneR[pos]=value;
	}
}
