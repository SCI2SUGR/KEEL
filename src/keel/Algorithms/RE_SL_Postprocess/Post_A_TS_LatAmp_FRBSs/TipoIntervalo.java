package keel.Algorithms.RE_SL_Postprocess.Post_A_TS_LatAmp_FRBSs;
/**
 * Class that defines the TipoIntervalo
 * @author Diana Arquillos
 *
 */
public class TipoIntervalo {
	private double min;
	private double max;
	public void set_max(double value){
		max=value;	
	}
	public void set_min(double value){
		min=value;	
	}
	public double min(){
		return min;
	}
	public double max(){
		return max;
	}

}
