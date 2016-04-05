package plugins.ITAV.StructureTensor;

import java.awt.Rectangle;
import java.util.LinkedList;
import java.util.List;

import debrito.listener.OpenAbout;
import debrito.ressources.Double2DReal;
import debrito.ressources.OperationMath;
import debrito.structureTensor.StructureTensorFunction;
import debrito.thread.ThreadEllipso2DOverlay;
import debrito.thread.ThreadSegment2DOverlay;
import icy.gui.dialog.MessageDialog;
import icy.image.IcyBufferedImage;
import icy.image.IcyBufferedImageUtil;
import icy.roi.BooleanMask2D;
import icy.roi.ROI;
import icy.sequence.Sequence;
import icy.type.DataType;
import icy.type.collection.array.Array1DUtil;
import plugins.adufour.blocks.lang.Block;
import plugins.adufour.blocks.util.VarList;
import plugins.adufour.ezplug.EzButton;
import plugins.adufour.ezplug.EzGroup;
import plugins.adufour.ezplug.EzLabel;
import plugins.adufour.ezplug.EzPlug;
import plugins.adufour.ezplug.EzVarBoolean;
import plugins.adufour.ezplug.EzVarInteger;
import plugins.adufour.ezplug.EzVarSequence;
import plugins.adufour.vars.lang.VarROIArray;



/**
 * <p>Apply the structure tensor algorithm in 2D of the PRIMO team of the ITAV (Toulouse, FRANCE)
 * This is a plugin for the software Icy 
 * To use it, you have to get a 8-bits image in 2D
 * You have to fix the parameters sigma and delta 
 * Choose the representation of the direction of the spheroid
 * Do not hesitate to do some different test with different parameters 
 * </p>
 * <p>
 * In case you use this algorithm, please cite : 
 * WEISS Pierre, FEHRENBACH Jerôme, DE BRITO Guillaume - ITAV (Toulouse, France)</p>
 * @see <a href="http://www.math.univ-toulouse.fr/~weiss/Publis/Journals/2014/Structure_Tensor_Cell_Organization_Zhang_Weiss_2014.pdf">http://www.math.univ-toulouse.fr/~weiss/Publis/Journals/2014/Structure_Tensor_Cell_Organization_Zhang_Weiss_2014.pdf</a>
 * @author DE BRITO Guillaume with the help of WEISS Pierre and FEHRENBACH Jerôme
 *
 */
public class StructureTensor extends EzPlug implements Block {

	
	EzVarSequence 		varCurrentSeq                     = new EzVarSequence("Input Sequence") ; 
	EzVarSequence 		varSeEllipse                      = new EzVarSequence("Ellipse Sequence") ; 
	EzVarSequence 		varSeSegment                      = new EzVarSequence("Segment Sequence") ; 
	EzVarSequence 		varSeAnisotropy                   = new EzVarSequence("Anisotropy Sequence") ; 
	EzVarSequence 		varSeBoth                         = new EzVarSequence("Ellipse & Segment Sequence") ; 
	EzVarBoolean 		varChoiceRoi 					  = new EzVarBoolean("ROI ?",false) ;
	VarROIArray         varROIs							  = new VarROIArray("ROIs") ; 
	EzLabel 			labelRoi 						  = new EzLabel("DON'T FORGET TO DRAW A ROI !") ; 
	EzVarBoolean 		varEllipse					      = new EzVarBoolean("Ellipse : ",true) 
						,varSegment			              = new EzVarBoolean("Segment : ",false) 
						,varBoth 						  = new EzVarBoolean("Ellipse and Segment : ",false) ; 
	EzGroup 			varGroup 						  = new EzGroup("Type : ",varEllipse,varSegment,varBoth); 
	EzVarBoolean 		varChoiceAnisotropy 			  = new EzVarBoolean("Display anisotropy image",true); 
	EzVarInteger		varSigma 						  = new EzVarInteger("Sigma :"); 
	EzVarInteger 		varDelta                          = new EzVarInteger("Delta : ");
	EzButton			buttonAbout						  ; 
	Sequence ss ; 
	
	
	@Override
	protected void initialize() {

		varCurrentSeq.setToolTipText("Select the sequence to use");
		super.addEzComponent(varCurrentSeq);
		
		varChoiceRoi.setToolTipText("Process with a ROI or not");
		super.addEzComponent(varChoiceRoi);

		super.addEzComponent(labelRoi);
		labelRoi.setVisible(false);
		varChoiceRoi.addVisibilityTriggerTo(labelRoi,true);
		
		//CHOICE SHAPES
		varEllipse.setToolTipText("Display ellipses");
		varSegment.setToolTipText("Display segments"); 
		varBoth.setToolTipText("Display ellipses and segments on the same image");
		super.addEzComponent(varGroup);
		
		
		varChoiceAnisotropy.setToolTipText("Display the image of the anisotropy");
		super.addEzComponent(varChoiceAnisotropy);
		
		
		varSigma.setToolTipText("Set the value of sigma, characteristic size of the ellipses to analyze");
		varSigma.setValue(15);
		super.addEzComponent(varSigma);
		
		varDelta.setToolTipText("Set the value of delta, step size between segments/ellipses");
		varDelta.setValue(10);
		super.addEzComponent(varDelta);
		
		
		buttonAbout = new EzButton("About", new OpenAbout()) ; 
		super.addEzComponent(buttonAbout);
		
		
		
	}

	@Override
	protected void execute() {
		boolean choiceRoi = varChoiceRoi.getValue() ; 
		boolean choiceAnisoDisplay = varChoiceAnisotropy.getValue() ; 
		int sigma = varSigma.getValue() ; 
		int delta = varDelta.getValue() ; 
		Sequence se = varCurrentSeq.getValue() ; 
		if (se==null) {
			MessageDialog.showDialog("No sequence selected, please select one");
		}
		else {
			ss = se ; 
			IcyBufferedImage img = se.getFirstImage() ; 
			
			//CHOICE SHAPES
			boolean choiceEllipse = varEllipse.getValue() ; 
			boolean choiceSegment = varSegment.getValue() ; 
			boolean choiceBoth = varBoth.getValue() ;  
			
			
			if (choiceRoi==true && (se.getROIs().isEmpty() && varROIs.size()==0)) {
				MessageDialog.showDialog("There is no ROI");
			}
			else {
				if (delta==0) {
					MessageDialog.showDialog("Delta can't be 0");
				}
				else {
					if (sigma==0) {
						MessageDialog.showDialog("Sigma can't be 0");
					}
					else {
						if (varROIs.size()!=0) {
							for (ROI roi : varROIs.getValue(true))
								se.addROI(roi);
						}
						List<double[]> lShapParam = paramProcessing(se, img, sigma, delta,choiceRoi,choiceAnisoDisplay) ; 
						if (choiceEllipse) {
							Sequence se2 = new Sequence() ; 
							se2.addImage(0, img);
							this.addSequence(se2);
							varSeEllipse.setValue(se2);
							ellipseProcessing(se2,lShapParam);
						}
						if (choiceSegment) {
							Sequence se3 = new Sequence() ; 
							se3.addImage(0, img);
							this.addSequence(se3);
							varSeSegment.setValue(se3);
							segmentProcessing(se3,lShapParam);
						}
						if (choiceBoth) {
							Sequence se4 = new Sequence() ; 
							se4.addImage(0, img);
							this.addSequence(se4);
							varSeBoth.setValue(se4);
							ellipseProcessing(se4,lShapParam);
							segmentProcessing(se4,lShapParam);
						}
					}
					
				}
				
				
			}
		}
		
	}

	
	
	@Override
	public void clean() {
		if (ss!=null)
			ss.removeAllROI();
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public List<double[]> paramProcessing(Sequence se, IcyBufferedImage img,int sigma, int delta,boolean choiceRoi,boolean choiceAnisoDisplay) {
		
		int width = img.getWidth() ;
		int height = img.getHeight() ;
		
		//Manage Mask
		BooleanMask2D bm ; 
		if (choiceRoi==true) {
			List<ROI> listRoi = se.getROIs() ;
			ROI firstRoi = listRoi.get(0) ; 
			bm = firstRoi.getBooleanMask2D(0, 0, 0, true) ;
		}
		else {
			Rectangle r = new Rectangle() ;
			r.setBounds(0, 0, width, height);
			boolean [] mask ; 
			mask = new boolean[width*height] ; 
			for (int i=0 ; i<mask.length ; i++) {
				mask[i]=true ; 
			}
			bm = new BooleanMask2D(r,mask) ; 
		}
		
		
		double[] tmp = Array1DUtil.arrayToDoubleArray(img.getDataXY(0), img.isSignedDataType()) ; 
		double[][] pixels = new double[height][width] ;
		
		for (int i = 0 ; i<height ; i++) {
			for (int j = 0 ; j<width ; j++) {
					pixels[i][j]=tmp[i*width+j] ; 
			}
		}
		
		//create u
		Double2DReal u = new Double2DReal(pixels) ;
		pixels=null ; 
		
		//create h (like MatLab)
		Double2DReal h2 = StructureTensorFunction.createH(height, width,sigma) ; 
		
		
		
		
		
		
		
		//launch of StructureTensor algorithm 
		List<?>[] l = StructureTensorFunction.structureTensor(u,h2,bm) ;
		h2 = null ; u = null ;  
		
		
		
		
		
		//get V1
		@SuppressWarnings("unchecked")
		LinkedList<double[][][]> l0 = (LinkedList<double[][][]>) l[0] ;
		double[][][] V1 = l0.get(0); 
		double[][] v1x = V1[0] ; 
		double[][] v1y = V1[1] ; 
		V1=null ; 
		
		
		
		
		
		//get lambda1 and lambda2
		@SuppressWarnings("unchecked")
		LinkedList<double[][]> l2 = (LinkedList<double[][]>) l[1] ;
		double[][] lambda1, lambda2 ;  
		lambda1=l2.get(0) ; 
		lambda2=l2.get(1) ;
		l=null ; 
		l2=null ;
		
		
		
		// get sqrt(lambda1/lambda2)		
		double[][] matLambda = OperationMath.sqrtDivideLambda(lambda1,lambda2) ;
		

		
		lambda1=null ;
		
		double[] maxMin = OperationMath.maxminTabWithRestriction(lambda2,bm) ; 
		
		
		double max = maxMin[0] ; 
		double min = maxMin[1] ;
		
		
		if (choiceAnisoDisplay==true) {
			
			//convert in ICY format 
			tmp = new double[height*width] ; 
			for (int i=0 ; i<height ; i++){
				for (int j = 0 ; j<width ; j++) {
					if (bm.contains(j,i))
						tmp[j+i*width]=matLambda[i][j] ;
					else
						tmp[j+i*width]=0 ; 
				}
			}
			
			//convert in 32-bit to print values between 0 and 1 
			//create new image
			IcyBufferedImage img2 = IcyBufferedImageUtil.convertToType(img,DataType.FLOAT , false) ; 
			Array1DUtil.doubleArrayToSafeArray(tmp, img2.getDataXY(0),img2.isSignedDataType());
			
			
			
			//create sequence with result image (sqrtLam)
			Sequence se2 = new Sequence() ; 
			se2.addImage(img2);
			this.addSequence(se2);
			varSeAnisotropy.setValue(se2);
			
			//inform the image that data changed 
			img2.dataChanged();
		}
		img.dataChanged();
		

		//get features of ellipse or segment
		return StructureTensorFunction.calculParamEllipse( matLambda, delta, v1x, v1y,lambda2,max,min,bm) ; 
		
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	public void ellipseProcessing (Sequence se, List<double[]> lShapParam ){
		//THREAD
		double nbProc = Runtime.getRuntime().availableProcessors() ;
		double lon = lShapParam.size() ; 
		double r = lon%nbProc; 
		double columnsPerThread1 = Math.floor(lon / nbProc);
		double columnsPerThread2 = Math.ceil(lon / nbProc);
		int start , stop  ;
		start = 0 ;
		int i = 0 ; 
		ThreadEllipso2DOverlay[] tabThread = new ThreadEllipso2DOverlay[(int)nbProc] ;
		
		while(i<r) {
			stop = start + (int) (columnsPerThread2-1) ; 
			ThreadEllipso2DOverlay t = new ThreadEllipso2DOverlay(lShapParam,start,stop,se) ; 
			tabThread[i]=t ; 
			t.start();
			start=stop+1 ; 
			i++ ; 
		}
		while(i<nbProc) {
			stop=start + (int) (columnsPerThread1-1) ; 
			ThreadEllipso2DOverlay t = new ThreadEllipso2DOverlay(lShapParam,start,stop,se) ; 
			tabThread[i]=t ; 
			t.start();
			start=stop+1 ; 
			i++ ; 
		}
		for (int j=0 ; j<nbProc ; j++){
			try {
				tabThread[j].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		
		
		
//		NO THREAD
//	for (int i=0 ; i<lShapParam.size() ; i++) {			
//		double[] d = lShapParam.get(i) ; 			
//		Ellipso2DTensor e = new Ellipso2DTensor(d[0],d[1],d[3],d[2],d[4]);
//		Color c = new Color((int)d[6],0,(int)d[5]) ;
//		e.setColor(c);
//		Overlay o = e.toOverlay() ; 
//		se.addOverlay(o) ; 
//	}
		
	}
	
	
	public void segmentProcessing(Sequence se, List<double[]> lShapParam) {
		
		//THREAD
		double nbProc = Runtime.getRuntime().availableProcessors() ;
		double lon = lShapParam.size() ; 
		double r = lon%nbProc; 
		double columnsPerThread1 = Math.floor(lon / nbProc);
		double columnsPerThread2 = Math.ceil(lon / nbProc);
		int start , stop  ;
		start = 0 ;
		int i = 0 ; 
		ThreadSegment2DOverlay[] tabThread = new ThreadSegment2DOverlay[(int)nbProc] ;
		
		while(i<r) {
			stop = start + (int) (columnsPerThread2-1) ; 
			ThreadSegment2DOverlay t = new ThreadSegment2DOverlay(lShapParam,start,stop,varDelta.getValue(),se) ; 
			tabThread[i]=t ; 
			t.start();
			start=stop+1 ; 
			i++ ; 
		}
		while(i<nbProc) {
			stop=start + (int) (columnsPerThread1-1) ; 
			ThreadSegment2DOverlay t = new ThreadSegment2DOverlay(lShapParam,start,stop,varDelta.getValue(),se) ; 
			tabThread[i]=t ; 
			t.start();
			start=stop+1 ; 
			i++ ; 
		}
		for (int j=0 ; j<nbProc ; j++){
			try {
				tabThread[j].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		
		
		
		//NO THREAD
//		double x0,y0,x1,y1,x2,y2,alpha,a ; 
//		for (int i=0 ; i<lShapParam.size() ; i++) {
//			double[] d = lShapParam.get(i) ;
//			
//			x0=d[0] ; 
//			y0=d[1] ; 
//			a=d[2] ; 
//			alpha=d[4] ; 
//			
//			double[] coordTmp = Segment2DTensor.calculParamLine(a, x0, y0,Integer.parseInt(varDelta.getValue())) ; 
//			
//			x1=coordTmp[0]; 
//			y1=coordTmp[1] ; 
//			x2=coordTmp[2] ;
//			y2=coordTmp[3] ;
//			
//			Color c = new Color((int)d[6],0,(int)d[5]) ;
//			Segment2DTensor l = new Segment2DTensor((int)x0,(int) y0,(int)x1,(int)y1,(int)x2,(int)y2,alpha,c) ; 
//			Overlay o = l.toOverlay() ;
//			se.addOverlay(o) ; 
//			l=null ; 
//		}
	}

	@Override
	public void declareInput(VarList inputMap) {
		varSigma.setValue(15);
		varDelta.setValue(10);
		inputMap.add("Sequence",varCurrentSeq.getVariable());
		inputMap.add("ChoiceRoi",varChoiceRoi.getVariable());
		inputMap.add("ROI", varROIs);
		inputMap.add("Ellipse",varEllipse.getVariable());
		inputMap.add("Segment",varSegment.getVariable());
		inputMap.add("Both",varBoth.getVariable());
		inputMap.add("Anisotropy",varChoiceAnisotropy.getVariable());
		inputMap.add("Sigma",varSigma.getVariable());
		inputMap.add("Delta",varDelta.getVariable());
	}

	@Override
	public void declareOutput(VarList outputMap) {
		outputMap.add("Anisotropy Sequence", varSeAnisotropy.getVariable());
		outputMap.add("Ellipse Sequence", varSeEllipse.getVariable());
		outputMap.add("Segment Sequence", varSeSegment.getVariable());
		outputMap.add("Ellispe & Segment Sequence", varSeBoth.getVariable());
	}
}
