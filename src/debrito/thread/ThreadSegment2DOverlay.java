package debrito.thread;

import icy.painter.Overlay;
import icy.sequence.Sequence;

import java.awt.Color;
import java.util.List;

import debrito.line.Segment2DTensor;

public class ThreadSegment2DOverlay extends Thread {
	
	private List<double[]> list ; 
	private int start, stop,varDelta ; 
	private Sequence se ; 
	
	public ThreadSegment2DOverlay (List<double[]> list, int start , int stop ,int varDelta, Sequence se) {
		this.list=list ; 
		this.start=start ; 
		this.stop=stop ; 
		this.varDelta=varDelta ; 
		this.se=se ; 
	}
	
	//draw segments
	public void run() {
		double x0,y0,x1,y1,x2,y2,alpha,a ; 
		
		for (int i=start ; i<=stop ; i++) {
			double[] d = list.get(i) ;
			//get the params of the segments
			x0=d[0] ; 
			y0=d[1] ; 
			a=d[2] ; 
			alpha=d[4] ; 
			
			//determine the coordinates of the segment
			double[] coordTmp = Segment2DTensor.calculParamLine(a, x0, y0,varDelta) ; 
			//get these coordinates
			x1=coordTmp[0]; 
			y1=coordTmp[1] ; 
			x2=coordTmp[2] ;
			y2=coordTmp[3] ;
			//set the color
			Color c = new Color((int)d[6],0,(int)d[5]) ;
			//construct the segment
			Segment2DTensor l = new Segment2DTensor((int)x0,(int) y0,(int)x1,(int)y1,(int)x2,(int)y2,alpha,c) ; 
			Overlay o = l.toOverlay() ;
			se.addOverlay(o) ; //add the segment on the image
		}
	}

}
