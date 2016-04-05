package debrito.ressources;

import icy.roi.BooleanMask2D;

import java.util.LinkedList;
import java.util.List;

import debrito.thread.ThreadMatExp;

public class OperationMath {
	
	
	
	//--------sqrt lambda-------//
	//			divide		    //
	//						    //
	//						    //
	//						    //
	/**
	 * Make the square root of the division between lambda1 and lambda2
	 * @param l1
	 * @param l2
	 * @return
	 */
	public static double[][] sqrtDivideLambda(double[][] l1,double[][]l2) {
		double[][] tmp = new double[l1.length][l1[0].length] ; 
		for (int i = 0 ; i<l1.length ; i++) {
			for (int j = 0 ; j<l1[0].length ; j++) {
				tmp[i][j]=Math.sqrt((l1[i][j])/(l2[i][j])) ; 
			}
		}
		return tmp ; 
	}
	
	
	
	
	
	
	
	
	
	
	
	//----------Arrays---------//
	//				   	       //
	//					       //
	//					       //
	//					       //
	
	//values max and min of 2D array
	/**
	 * Return the values max and min of an array with a mask restriction 
	 * @param d
	 * @return
	 */
	public static double[] maxminTabWithRestriction(double[][] d,BooleanMask2D bm) {
		double max,min ; 
		max = d[0][0] ; 
		min= d[0][0] ;
		
		for (int i=0 ; i<d.length ; i++) {
			for (int j=0 ; j<d[0].length ; j++) {
				if (bm.contains(j,i)) {
					if (d[i][j]<min) {
						min=d[i][j] ; 
					}else if (d[i][j]>max){
						max=d[i][j] ; 
					}
				}
			}
		}
		
		double[] tmp = {max,min} ; 
		return tmp ; 
	}
	
	//convert from a pixel format to an other (0 to 255)
	/**
	 * convert pixel from a format to another one
	 * @param d
	 * @param max
	 * @param min
	 * @return
	 */
	public static double[][] convert(double[][] d , double max , double min) {
		double[][] tmp = new double[d.length][d[0].length] ; 
		
		for (int i =0 ; i<d.length ; i++) {
			for (int j = 0 ; j<d[0].length ; j++) {
				tmp[i][j]=(((max-d[i][j])/(max-min))*255) ; 
			}
		}
		
		return tmp ;
	}
	
	
	
	
	
	
	
	
	
	
	
	//----------Meshgrid---------//
	//				   	         //
	//					         //
	//					         //
	//					         //
	//meshgrid
	/**
	 * Make a meshgrid like Matlab
	 * @param vmin1
	 * @param pas1
	 * @param vmax1
	 * @param vmin2
	 * @param pas2
	 * @param vmax2
	 * @return
	 */
	public static List<double[][]> meshgrid (double vmin1,double pas1,double vmax1,
						  double vmin2,double pas2,double vmax2 ) {
		double [][] tab1,tab2 ; 
		int t1,t2 ; 
		t1 = (int) (((vmax1-vmin1)/pas1)+1) ; 
		t2 = (int) (((vmax2-vmin2)/pas2)+1) ; 
		tab1 = new double[t2][t1] ; 
		tab2 = new double[t2][t1] ;
		
		double vtmp  ; 
		
		for (int i=0 ; i<t2 ; i++) {
			vtmp=vmin2+i*pas2 ; 
			for (int j=0 ; j<t1 ; j++) {
					tab1[i][j]=vmin1+j*pas1 ; 
					tab2[i][j]=vtmp ; 
			}
		}
		
		
		List<double[][]> l = new LinkedList<double[][]>() ; 
		l.add(tab1) ; 
		l.add(tab2) ; 
		
		return l ; 
		
	}
	
	
	
	
	
	
	
	
	
	
	//----------Matrix---------//
	//		   Operations      //
	//					       //
	//					       //
	//					       //
	//matrix square
	/**
	 * Make the square root of a matrix
	 * @param d
	 * @return
	 */
	public static double[][] matSquare (double[][] d ) {
		double[][] tmp = new double[d.length][d[0].length] ;
		for (int i=0 ; i<tmp.length ; i++) {
			for (int j=0 ; j<tmp[0].length ; j++) {
				tmp[i][j]=(d[i][j]*d[i][j]) ; 
			}
		}
		return tmp ;
	}
	
	
	//matrix add
	/**
	 * Make the addition of two matrix
	 * @param d
	 * @param c
	 * @return
	 */
	public static double[][] matAdd (double[][] d,double [][] c) {
		double[][] tmp = new double[d.length][d[0].length] ;
		for (int i=0 ; i<tmp.length ; i++) {
			for (int j=0 ; j<tmp[0].length ; j++) {
				tmp[i][j]=(d[i][j]+c[i][j]) ; 
			}
		}
		return tmp ;
	}
	
	//matrix scal mult
	/**
	 * Multiplication of a matrix by a scalar
	 * @param d
	 * @param s
	 * @return
	 */
	public static double[][] matScalMult (double[][] d , double s){
		double[][] tmp = new double[d.length][d[0].length] ;
		for (int i=0 ; i<tmp.length ; i++) {
			for (int j=0 ; j<tmp[0].length ; j++) {
				tmp[i][j]=(d[i][j]*s) ; 
			}
		}
		return tmp ; 
	}
	
	//matrix division
	/**
	 * Division of a matrix by a scalar
	 * @param d
	 * @param s
	 * @return
	 */
	public static double[][] matDivScal (double[][] d , double s){
		
		double[][] tmp = new double[d.length][d[0].length] ;
		for (int i=0 ; i<tmp.length ; i++) {
			for (int j=0 ; j<tmp[0].length ; j++) {
				tmp[i][j]=(d[i][j]/s) ; 
			}
		}
		return tmp ; 
	}
	
	
	//matrix exp
	/**
	 * Make the exponential of a matrix
	 * @param d
	 * @return
	 */
	public static double[][] matExp (double[][] d) {
		double[][] tmp = new double[d.length][d[0].length] ;
		
		//THREAD
		int w = d[0].length ; //get the width of the initial array
		double nbProc = Runtime.getRuntime().availableProcessors() ; //get the number of available processors
		
		//determine the number of column (of array) by thread
		double r = w%nbProc; 						
		double columnsPerThread1 = Math.floor(w / nbProc); //number of columns by thread (more)
		double columnsPerThread2 = Math.ceil(w / nbProc); //number of columns by thread (less)
		
		int start , stop  ;
		start = 0 ; 
		int i = 0;
		ThreadMatExp[] tabThread = new ThreadMatExp[(int)nbProc] ; //create an array of threads
		while(i<r) {	//run all of the thread with columnsPerThread2
			stop = start + (int) (columnsPerThread2-1) ; 
			ThreadMatExp t = new ThreadMatExp(d,tmp,start,stop) ; 
			tabThread[i]=t ; 
			t.start();
			start=stop+1 ; 
			i++ ; 
		}
		while(i<nbProc) { //run all of thread with columnsPerThread1
			stop=start + (int) (columnsPerThread1-1) ; 
			ThreadMatExp t = new ThreadMatExp(d,tmp,start,stop) ; 
			tabThread[i]=t ; 
			t.start();
			start=stop+1 ; 
			i++ ; 
		}
		for (int j=0 ; j<nbProc ; j++){ //join the thread here
			try {
				tabThread[j].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		
		
		return tmp ; 
		
	}
	
	
	//matrix sum 
	/**
	 * Sum of all of the term of the matrix
	 * @param d
	 * @return
	 */
	public static double matSum(double[][] d){
		double tmp = 0 ; 
		for (int i=0 ; i<d.length ; i++) {
			for (int j=0 ; j<d[0].length ; j++) {
				tmp+=d[i][j] ;  
			}
		}
		return tmp ; 
	}
	
	
	//matrix product
	public static double[][] matProduct (double[][] d1,double[][]d2) {
		double[][] dFinal = new double[d1.length][d2[0].length] ;
		double sum = 0 ;
		int i , j , k;
		for (i =0 ; i<d1.length ; i++) {
			for (k=0 ; k<d2[0].length ; k++) {
				for (j=0 ; j<d1[0].length ; j++) {
					sum+=d1[i][j]*d2[j][k] ; 
				}
				dFinal[i][k]=sum ; 
				sum=0 ; 
			}
		}
		
		
		return dFinal ; 
	}
	
	
	
	//matrix shift
	/**
	 * Make the function fftshift of matlab
	 * @param d
	 * @return
	 */
	public static double[][] fftShiftMat(double[][] d) {
		double[][] tmp = new double[d.length][d[0].length] ;
		int n1=d.length ; 
		int n2 = d[0].length ; 
		for (int i=0 ; i<tmp.length ; i++) {
			for (int j=0 ; j<tmp[0].length ; j++) {
				tmp[(i+(n1/2)) % n1][(j+(n2/2)) % n2]=d[i][j] ;  
			}
		}
		return tmp ;
	}
	
	// create matrix
	/**
	 * Create an incremental matrix 
	 * @param h
	 * @param w
	 * @param max
	 * @return
	 */
	public static double[][] createMatrix(int h , int w ,int max) {
		double[][] tmp = new double[h][w] ; 
		int k=1 ; 
		for (int i = 0 ; i<h ; i++) {
			for (int j = 0 ; j<w ; j++) {
				tmp[i][j]=k ;
				k++ ; 
			}
		}
		return tmp ; 
	}
	
	//create matrix rand
	/**
	 * Create a random matrix
	 * @param h
	 * @param w
	 * @return
	 */
	public static double[][] createMatrixRand(int h, int w ) {
		double[][] tmp = new double[h][w] ; 
		for (int i = 0 ; i<h ; i++) {
			for (int j = 0 ; j<w ; j++) {
				tmp[i][j] =  (int)(Math.random()*100) ;
			}
		}
		
		return tmp ; 
			
		}
	//create matrix rand (with debug)
	/**
	 * Create a random matrix with debug
	 * @param h
	 * @param w
	 * @return
	 */
	public static double[][] createMatrixRandDebug(int h, int w ) {
		double[][] tmp = new double[h][w] ; 
		String s = "" ; 
		for (int i = 0 ; i<h ; i++) {
			for (int j = 0 ; j<w ; j++) {
				tmp[i][j] =  (int)(Math.random()*100) ;
				s+="["+tmp[i][j]+"]" ; 
			}
			s+="\n" ;
		}
		System.out.println(s);
		
		return tmp ; 
		
	}
	
	//Norm vector
	/**
	 * Calculate the norm of a vector 
	 * @param args
	 * @return
	 */
	public static double normVector(double ... args) {
		double sum = 0 ; 
		for (int i=0 ; i<args.length ; i++) {
			sum+=(args[i]*args[i]) ; 
		}
		
		return Math.sqrt(sum) ; 
	}
	
	
	
	
	
	
	
	
	
	
	
	

}
