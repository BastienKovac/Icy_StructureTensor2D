package debrito.structureTensor;

import icy.roi.BooleanMask2D;

import java.util.LinkedList;
import java.util.List;

import debrito.ressources.CartesianGrid;
import debrito.ressources.Double2DReal;
import debrito.ressources.OperationMath;
import debrito.thread.ThreadSTEigen;

public class StructureTensorFunction {

	public static final float APPR = 0.8f;

	/**
	 * Use the algorithm StructureTensor on a image with a h
	 * 
	 * @param u
	 * @param h
	 * @return List<?>[]
	 */
	public static List<?>[] structureTensor(Double2DReal u, Double2DReal h, BooleanMask2D bm) {
		int n1, n2;
		Double2DReal dxu, dyu, u00h, u01h, u11h;
		double[][] lambda1, lambda2;
		double[][][] V1, V2;

		// get rows and columns
		n1 = u.getRows();
		n2 = u.getColumns();
		// calculate dxu and dyu for gradient
		dxu = u.calculGradv2(Double2DReal.createYFilter3x3());
		dyu = u.calculGradv2(Double2DReal.createXFilter3x3());

		// ROI management
		for (int i = 0; i < dxu.getRows(); i++) {
			for (int j = 0; j < dxu.getColumns(); j++) {
				if (!bm.contains(j, i)) {
					dxu.setValue(0, i, j);
					dyu.setValue(0, i, j);
				}
			}
		}

		// calculate the convolution with h //NO THREAD
		u00h = Double2DReal.convolution(dxu.multiplyHadamard(dxu), h);
		u01h = Double2DReal.convolution(dxu.multiplyHadamard(dyu), h);
		u11h = Double2DReal.convolution(dyu.multiplyHadamard(dyu), h);

		// THREAD (less efficiency)
		// Double2DReal [] tabU = new Double2DReal[3] ;
		// ThreadConvolution tc1 = new ThreadConvolution(tabU,dxu,dxu,h,0) ;
		// ThreadConvolution tc2 = new ThreadConvolution(tabU,dxu,dyu,h,1) ;
		// ThreadConvolution tc3 = new ThreadConvolution(tabU,dyu,dyu,h,2) ;
		// tc1.setPriority(Thread.MAX_PRIORITY);
		// tc2.setPriority(Thread.MAX_PRIORITY);
		// tc3.setPriority(Thread.MAX_PRIORITY);
		// tc1.start();
		// tc2.start();
		// tc3.start();
		//
		// try {
		// tc1.join();
		// tc2.join();
		// tc3.join();
		// } catch (InterruptedException e) {
		// e.printStackTrace();
		// }
		// u00h = tabU[0] ;
		// u01h = tabU[1] ;
		// u11h = tabU[2] ;

		// create the matrix V1s and V2s
		V1 = new double[2][n1][n2];
		V2 = new double[2][n1][n2];
		// create the matrix lambda1,lambda2
		lambda1 = new double[n1][n2];
		lambda2 = new double[n1][n2];

		double nbProc = Runtime.getRuntime().availableProcessors();
		double r = n2 % nbProc;
		double columnsPerThread1 = Math.floor(n2 / nbProc);
		double columnsPerThread2 = Math.ceil(n2 / nbProc);

		int start, stop;
		start = 0;
		int i = 0;
		ThreadSTEigen[] tabThread = new ThreadSTEigen[(int) nbProc];
		while (i < r) {
			stop = start + (int) (columnsPerThread2 - 1);
			ThreadSTEigen t = new ThreadSTEigen(u00h, u01h, u11h, V1, V2, lambda1, lambda2, n1, start, stop);
			tabThread[i] = t;
			t.start();
			start = stop + 1;
			i++;
		}
		while (i < nbProc) {
			stop = start + (int) (columnsPerThread1 - 1);
			ThreadSTEigen t = new ThreadSTEigen(u00h, u01h, u11h, V1, V2, lambda1, lambda2, n1, start, stop);
			tabThread[i] = t;
			t.start();
			start = stop + 1;
			i++;
		}
		for (int j = 0; j < nbProc; j++) {
			try {
				tabThread[j].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		// list 3D
		LinkedList<double[][][]> l1 = new LinkedList<double[][][]>();
		l1.add(V1);
		l1.add(V2);
		// list2D
		LinkedList<double[][]> l2 = new LinkedList<double[][]>();
		l2.add(lambda1);
		l2.add(lambda2);
		// array of list
		List<?>[] l = new List[2];
		l[0] = l1;
		l[1] = l2;

		return l;

	}

	/**
	 * Calculate the features of the ellipse with the result of the method
	 * structureTensor (sqrtLam,v1x,v1y) and a step specified (delta)
	 * 
	 * @param sqrtLam
	 * @param delta
	 * @param v1x
	 * @param v1y
	 * @return
	 */
	public static List<double[]> calculParamEllipse(double[][] sqrtLam, int delta, double[][] v1x, double[][] v1y,
			double[][] lambda2, double max, double min, BooleanMask2D bm) {

		List<double[]> l = new LinkedList<double[]>();

		float appr = StructureTensorFunction.APPR;
		CartesianGrid cg = new CartesianGrid(delta, sqrtLam.length, sqrtLam[0].length, bm);
		int[][][] coordP = cg.getVal();

		double gamma, b, a, theta;
		for (int i = 0; i < coordP[0].length; i++) {
			for (int j = 0; j < coordP[0][0].length; j++) {

				int x0 = coordP[0][i][j];
				int y0 = coordP[1][i][j];
				gamma = sqrtLam[x0][y0];
				if (x0 != 0 && y0 != 0) {
					b = (delta / 2) * appr; // tall
					a = b * gamma; // small

					double[][] v = new double[1][2];
					v[0][0] = v1x[x0][y0];
					v[0][1] = v1y[x0][y0];

					double tmpColorR = (int) (((lambda2[x0][y0] - min) / (max - min)) * 255);
					double tmpColorB = (int) (((max - lambda2[x0][y0]) / (max - min)) * 255);

					double[][] vTilde = OperationMath.matDivScal(v, OperationMath.normVector(v[0][0], v[0][1]));

					theta = Math.atan2(vTilde[0][1], vTilde[0][0]);
					l.add(new double[] { y0, x0, a, b, theta, tmpColorB, tmpColorR });
				}
			}
		}

		return l;

	}

	public static Double2DReal createH(int n1, int n2, int sigma) {
		// Using Math.floor to handle cases with odd dimensions
		List<double[][]> l1 = OperationMath.meshgrid(Math.floor((-n2 / 2)), 1, Math.floor(n2 / 2) - ((n2 - 1) % 2),
				Math.floor((-n1 / 2)), 1, Math.floor(n1 / 2) - ((n1 - 1) % 2));
		double[][] X = l1.get(0);
		double[][] Y = l1.get(1);
		l1 = null;
		double[][] h = OperationMath
				.matExp(OperationMath.matDivScal(
						OperationMath.matScalMult(
								OperationMath.matAdd(OperationMath.matSquare(X), OperationMath.matSquare(Y)), -1),
						2 * sigma * sigma));
		h = OperationMath.matDivScal(h, OperationMath.matSum(h));
		h = OperationMath.fftShiftMat(h);
		X = null;
		Y = null;

		// return new h
		return new Double2DReal(h);
	}

}
