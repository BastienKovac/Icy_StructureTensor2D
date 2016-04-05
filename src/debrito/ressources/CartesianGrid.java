package debrito.ressources;

import icy.roi.BooleanMask2D;

/**
 * Construct a cartesian grid 
 * @author guillaume.de_brito
 *
 */
public class CartesianGrid {
	
	private int delta ; 
	private int h, w ; 
	private int[][][] val ; 
	private BooleanMask2D bm ; 
	
	
	/**
	 * Construct the Cartesian grid with step delta 
	 * and the width and height 
	 * @param delta
	 * @param h
	 * @param w
	 */
	public CartesianGrid(int delta,int h , int w,BooleanMask2D bm ) {
		this.delta = delta ; 
		this.w=w ; 
		this.h=h ; 
		this.bm=bm ;
		init() ; 
	}
	
	
	/**
	 * Get the index of the point of Cartesian Grid
	 * @return
	 */
	public int[][][] getVal() {
		return val ; 
	}
	
	
	/**
	 * Initialize the Cartesian Grid
	 */
	private void init() {
		int deltaDiv = delta/2 ;
		int nbPL , nbPC ;  
		
		nbPC = Math.round((w-deltaDiv)/delta) ; 
		nbPL = Math.round((h-deltaDiv)/delta) ; 
		
		val = new int[2][nbPL][nbPC] ;
		for (int i = 0 ; i<nbPL ; i++) {
			for (int j=0 ; j<nbPC ; j++) {
				if (bm.contains(deltaDiv+j*delta,deltaDiv+i*delta)){
					val[0][i][j]=deltaDiv+i*delta ;
					val[1][i][j]=deltaDiv+j*delta ;
				}
			}
		}
		
	}
	
	

}
