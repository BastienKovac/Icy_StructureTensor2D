package debrito.thread;

import debrito.ressources.Double2DReal;

public class ThreadSTEigen extends Thread{
	
	private Double2DReal u00h , u01h , u11h ;
	private double [][][] V1,V2 ; 
	private double[][] lambda1,lambda2 ; 
	private int n1 , start , stop ; 
	
	
	public ThreadSTEigen(Double2DReal u00h,Double2DReal u01h, Double2DReal u11h,
			double[][][] V1,double[][][] V2,
			double[][]lambda1 , double[][]lambda2,
			int n1, int start , int stop) {
		this.u00h=u00h ; 
		this.u01h=u01h ; 
		this.u11h=u11h ; 
		this.V1=V1 ; 
		this.V2=V2 ; 
		this.lambda1=lambda1 ; 
		this.lambda2=lambda2 ; 
		this.n1=n1 ; 
		this.start=start ; 
		this.stop=stop ; 
	}
	
	public void run() {
		
		double[][] Mm ; 
		Double2DReal M ; 
	    double [] vectPropre,valPropre ; 
	    
		for (int i = 0 ; i<n1 ; i++) {
			for (int j = start ; j<=stop ; j++) {
				
				//get the values of convolution 
				Mm = new double[2][2] ;
				Mm[0][0] = u00h.getValue(i, j)  ; 
				Mm[0][1] = u01h.getValue(i, j); 
				Mm[1][0] = u01h.getValue(i, j); 
				Mm[1][1] = u11h.getValue(i, j); 
				M = new Double2DReal(Mm) ;
				//get eigenvectors
				vectPropre = M.eigenVectors() ;
				//get eigenvalues
				valPropre = M.eigenValues() ;
				
				//put vectors in V1,V2
				//V1
				V1[0][i][j] = vectPropre[0] ;
				V1[1][i][j] = vectPropre[2] ;
				//V2
				V2[0][i][j] = vectPropre[1] ;
				V2[1][i][j] = vectPropre[3] ; 
				
				//put values in lambda1,lambda2
				lambda1[i][j] = Math.max(0, valPropre[0])  ; //petit
				lambda2[i][j] =  Math.max(0, valPropre[1]) ; //grand
			}
		}
	}

}
