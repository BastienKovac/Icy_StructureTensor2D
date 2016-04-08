package debrito.thread;

import icy.painter.Overlay;
import icy.sequence.Sequence;

import java.awt.Color;
import java.util.List;

import debrito.ellipse.Ellipso2DTensor;

public class ThreadEllipso2DOverlay extends Thread{

	private List<double[]> list  ; 
	private int start , stop ; 
	private Sequence se ;
	
	
	public ThreadEllipso2DOverlay(List<double[]> list , int start , int stop , Sequence se) {
		this.list=list ; 
		this.start=start ; 
		this.stop=stop ; 
		this.se=se ; 
	}
	
	// draw ellises 
	public void run() {
		for (int i = start ; i<=stop ; i++) {
			double[] d = list.get(i) ; 	//get the params of the ellipse		
			Ellipso2DTensor e = new Ellipso2DTensor(d[0],d[1],d[3],d[2],d[4]); //construct the ellipse
			Color c = new Color((int)d[6],0,(int)d[5]) ; //set the color
			e.setColor(c);
			Overlay o = e.toOverlay() ;
			se.addOverlay(o) ; //add the ellipse on the image
		}
	}
	
	
}
