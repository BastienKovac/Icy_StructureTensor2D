package debrito.line;

import icy.canvas.IcyCanvas;
import icy.canvas.IcyCanvas2D;
import icy.painter.Overlay;
import icy.sequence.Sequence;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;

import debrito.structureTensor.StructureTensorFunction;

public class Segment2DTensor {

	
	int x1 , y1 ; 
	int x2 , y2 ; 
	int x0,y0 ; 
	double alpha ; 
	Color c ; 
	
	public Segment2DTensor (int x0,int y0,int  x1, int y1,int  x2, int y2 ,double alpha, Color c) {
		this.x0=x0 ; 
		this.y0=y0 ; 
		this.x1=x1 ; 
		this.x2=x2 ; 
		this.y1=y1 ;
		this.y2=y2 ;
		this.alpha=alpha ; 
		this.c=c ;
	}
	
	public void setColor(Color c) {
		this.c=c ; 
	}
	
	
	public static double[] calculParamLine(double a  , double x0 , double y0,int delta) {
		int x1,y1 ; 
		int x2,y2 ; 
		x1 = (int)(x0+(StructureTensorFunction.APPR*delta)/2) ;
		x2 = (int)(x0-(StructureTensorFunction.APPR*delta)/2) ; 
		
		y1 = (int)y0 ; 
		y2 = (int)y0 ; 
		
		return new double[]{x1,y1,x2,y2} ; 
		
	}
	
	public Overlay toOverlay () {
		return new LineOverlay2D("Segment") ; 
	}
	
	
	protected class LineOverlay2D extends Overlay {
		
		protected LineOverlay2D(String name) {
			super(name) ; 
		}
		
		public void paint (Graphics2D g2,Sequence sequence, IcyCanvas canvas) {
			
			if (canvas instanceof IcyCanvas2D) {
				//get zoom and modify the size of the font according to it
				// (the size of the stroke in fact)
				float fontSize = (float) canvas.canvasToImageLogDeltaX(30);
	            g2.setFont(new Font("Trebuchet MS", Font.BOLD, 10).deriveFont(fontSize));
	            double stroke = Math.max(canvas.canvasToImageLogDeltaX(3), canvas.canvasToImageLogDeltaY(3));
				
	            g2.setStroke(new BasicStroke((float) stroke, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
	            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
				
				g2.setColor(Segment2DTensor.this.c);
		        
				//create the segment with the params
				Shape baseShape = new Line2D.Double(Segment2DTensor.this.x1, Segment2DTensor.this.y1, Segment2DTensor.this.x2, Segment2DTensor.this.y2) ; 
				
				//allow to rotate the segment according to the angle specidied in Segment2DTensor
				Shape rotatedShape = AffineTransform.getRotateInstance(
				          -Segment2DTensor.this.alpha,Segment2DTensor.this.x0, Segment2DTensor.this.y0)
				          .createTransformedShape(baseShape);
		        //draw segment
		        g2.draw(rotatedShape);
	        
			}
		}
	}
	
}
