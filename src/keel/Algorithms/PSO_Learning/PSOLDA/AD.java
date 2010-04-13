package keel.Algorithms.PSO_Learning.PSOLDA;

/**
 * <p>Title: AD</p>
 *
 * <p>Company: KEEL</p>
 *
 * @author Jose A. Saez Munoz
 * @version 1.0
 */


import keel.Algorithms.Statistical_Classifiers.Shared.MatrixCalcs.*;


public class AD{
	
    double COVAR[][][];
    double MEDIA[][][];
    double ejemplos[][];
    double deseado[][];
    int nentradas;
    int nsalidas;
    int nelem;
    int nejemplos[];
    
    
    public AD(double [][]vejemplos, double [][]vdeseado) {
        ejemplos=vejemplos; deseado=vdeseado;
        nentradas=ejemplos[0].length; nsalidas=deseado[0].length;
        nelem=ejemplos.length;
        COVAR=new double[nsalidas][nentradas][nentradas];
        MEDIA=new double[nsalidas][nentradas][1];
        nejemplos=new int[nsalidas];
        for (int i=0;i<nelem;i++) {
            for (int s=0;s<nsalidas;s++)
                if (deseado[i][s]!=0) { nejemplos[s]++; }
        }
    }
        
    public void CalculaParametros() 
    
    	throws ErrorDimension, ErrorSingular {
             
        // If 'lineal' is true, Every covariance matrix are equal                
        for (int i=0;i<nelem;i++) {
        	for (int s=0;s<nsalidas;s++)
        		if (deseado[i][s]!=0){
        			MEDIA[s]=MatrixCalcs.matsum(MEDIA[s],
					MatrixCalcs.columna(ejemplos[i]));
                }
        }
        
        for (int s=0;s<nsalidas;s++)
        	MEDIA[s]=MatrixCalcs.matmul(MEDIA[s],1.0f/nejemplos[s]);
                    
                    
        double tmp[][];
                    
        // Every calculus are made over COVAR[0]     
        for(int i=0;i<nelem;i++) {         
        	for (int s=0;s<nsalidas;s++)
        		if (deseado[i][s]!=0) {
        			tmp=MatrixCalcs.matsum(MatrixCalcs.columna(ejemplos[i]),MatrixCalcs.matmul(MEDIA[s],-1.0f));
        			tmp=MatrixCalcs.matmul(tmp,MatrixCalcs.tr(tmp));
        			COVAR[0]=MatrixCalcs.matsum(COVAR[0],tmp);
        		}
        }
                            
        // Covariance matrix is inverted
        COVAR[0]=MatrixCalcs.matmul(COVAR[0],1.0f/nelem);
        COVAR[0]=MatrixCalcs.inv(COVAR[0]);
                            
        // Results are copied to every  matrix
        for (int s=1;s<nsalidas;s++){
           	for (int i=0;i<COVAR[s].length;i++)
           		for (int j=0;j<COVAR[s][i].length;j++)
           			COVAR[s][i][j]=COVAR[0][i][j];
        }               
    }
                                     
    
    public  double [] distancias(double []x) throws ErrorDimension,ErrorSingular {
    	
    	// Distance from each example to each prototype is calculated
        double d[]=new double[nsalidas];
        double g[][];
                           
        double [][]cx=MatrixCalcs.columna(x);
                            
        for (int s=0;s<nsalidas;s++) {
	                                
	        //Linear term
	        double [][]w=MatrixCalcs.tr(MatrixCalcs.matmul(COVAR[s],MEDIA[s]));
	        g=MatrixCalcs.matmul(w,cx);
	                                
	        // Constant term
	        double[][] C1=MatrixCalcs.matmul(MatrixCalcs.tr(MEDIA[s]), MatrixCalcs.matmul(COVAR[s],MEDIA[s]));
	        C1=MatrixCalcs.matmul(C1,-0.5f);
	                                
	        double C2=0.5f*(double)Math.log(MatrixCalcs.determinante(COVAR[s]));
	                                
	        double C3=(double)Math.log(nejemplos[s]/(double)nelem);
	        	        	                                
	        d[s]=g[0][0]+C1[0][0]+C2+C3;
        }
                            
        return d;
    }
    
    
    public String[] Coeficientes() throws ErrorDimension,ErrorSingular {
    	
    	String res="";    	
        String discriminantes[]=new String[nsalidas];
                           
                            
        for (int s=0 ; s<nsalidas ; s++){
        	
        	discriminantes[s]="";
        	res="";
	                                
	        //Linear term
	        double [][]w=MatrixCalcs.tr(MatrixCalcs.matmul(COVAR[s],MEDIA[s]));
	        
	        res+="\n\tDiscriminant coefficients:\n";
	        
	        for(int ii=0 ; ii<w.length ; ++ii)
		        for(int jj=0 ; jj<w[0].length ; ++jj)
		        	res+="\t\tw"+jj+" = "+w[ii][jj]+"\n";
		        	
	        
	        // Constant term
	        double[][] C1=MatrixCalcs.matmul(MatrixCalcs.tr(MEDIA[s]), MatrixCalcs.matmul(COVAR[s],MEDIA[s]));
	        C1=MatrixCalcs.matmul(C1,-0.5f);
	                                
	        double C2=0.5f*(double)Math.log(MatrixCalcs.determinante(COVAR[s]));
	                                
	        double C3=(double)Math.log(nejemplos[s]/(double)nelem);
	        
	        res+="\n\tConstant term:\n\t\tb"+s+" = "+(C1[0][0]+C2+C3)+"\n\n";
	        
	        discriminantes[s]+=res;
        }
                            
        return discriminantes;
    }
                       
                        
    public int argmax(double []x){
    	double max=x[0]; int imax=0;
    	for (int i=1;i<x.length;i++)
    		if (x[i]>max) { max=x[i]; imax=i; }
    			return imax;
    }
                                            
}
