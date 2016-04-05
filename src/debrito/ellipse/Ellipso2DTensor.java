package debrito.ellipse;

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
import java.awt.geom.Ellipse2D;

/**
 * Specification of the ellipse
 * @author guillaume.de_brito
 *
 */
public class Ellipso2DTensor {

	public double a = 0 ; 
	public double b = 0 ; 
	public double x0 = 0 ;  
	public double y0 = 0  ; 
	public double alpha = 0 ; 
	public Color c ; 
	
	
	public Ellipso2DTensor(double x0,double y0 ,double a, double b, double alpha) {
		this.a=a ; 
		this.b=b ; 
		this.x0=x0 ; 
		this.y0=y0 ; 
		this.alpha=alpha ; 
	}
	
	
	
	public void setColor(Color c) {
		this.c=c ; 
	}
	
	public Overlay toOverlay() {
		return new EllipseOverlay2D("Ellipse") ;
	}

	
	
	protected class EllipseOverlay2D extends Overlay{
		
		protected EllipseOverlay2D(String name) {
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
				
				
	            //change color of the drawing
	            Color c = Ellipso2DTensor.this.c ; 
		        g2.setColor(c);
		        
		        
		        
		        
		        //create the ellipse without angle with the params of the ellipses
		        Shape baseShape = new Ellipse2D.Double(
		        		Ellipso2DTensor.this.x0 - Ellipso2DTensor.this.a, Ellipso2DTensor.this.y0 - Ellipso2DTensor.this.b, 
		          2.0D * Ellipso2DTensor.this.a, 2.0D * Ellipso2DTensor.this.b);
		        
		        //allow to rotate the ellipse according to the angle specified in Ellipso2DTensor
		        Shape rotatedShape = AffineTransform.getRotateInstance(
		          -Ellipso2DTensor.this.alpha, Ellipso2DTensor.this.x0, Ellipso2DTensor.this.y0)
		          .createTransformedShape(baseShape);
		        
		        //draw ellipse
		        g2.draw(rotatedShape);
			}
			
			
		}
		
		
	}

}
