package debrito.ressources;



public class Double2DReal {
	
	private double [][] array ; 
	private int rows, columns;
	private boolean complexExist ; 
	private Double2DComplex complex ; 
	
	//----------Constructors----------//
	//								   //
	//								   //
	//---------------------------------//
	/**
	 * Construct matrix with number of rows and columns
	 * @param r
	 * @param c
	 */
	public Double2DReal (int r, int c)
	{
		this.rows = r;
		this.columns = c;
		array = new double[r][c];
		complexExist = false ;
	}
	
	/**
	 * Construct matrix with an array
	 * @param tab
	 */
	public Double2DReal(double [][] tab) {
		this.rows = tab.length;
		this.columns = tab[0].length;
		array = new double[rows][columns];
		for(int i= 0; i < rows; i++)
		{
			for(int j= 0; j< columns; j++)
			{ 
				array[i][j] = tab[i][j];
			}
		}
		complexExist = false ;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	//----------Arrays----------//
	//			  and values    //
	//						    //
	//--------------------------//
	/**
	 * Put a value in the array
	 * @param value
	 * @param row
	 * @param column
	 * @param complex
	 * @throws IllegalArgumentException
	 */
	public void setValue(double value, int row, int column) throws IllegalArgumentException
	{
		if(row < this.rows && column < this.columns)
		{
				this.array[row][column] = value;
		} else {
			throw new IllegalArgumentException("rows and columns wrong") ; 
		}
	}
	
	
	/**
	 * Get a value from the array
	 * @param row
	 * @param column
	 * @param complex
	 * @return
	 */
	public double getValue(int row, int column) throws IllegalArgumentException 
	{
		if(row < this.rows && column < this.columns)
		{
				return this.array[row][column];
		} else {
			throw new IllegalArgumentException("Row and colomuns wrong") ; 
		}
	}
	
	/**
	 * Get the array
	 * @return array
	 */
	public double[][] getArray()
	{
		return this.array;
	}
	/**
	 * Get the number of rows
	 * @return rows
	 */
	public int getRows() {
		return rows;
	}

	/** 
	 * Get the number of columns
	 * @return columns
	 */
	public int getColumns() {
		return columns;
	}
	
	
	
	
	
	
	
	
	
	//----------Print Array--------//
	//							   //
	//							   //
	//-----------------------------//
	/**
	 * Print array
	 * @return s
	 */
	public String toStringArray() {
		String s = ""; 
		for (int i = 0 ; i<rows ; i++) {
			for (int j = 0 ; j<this.columns ; j++) {
				s+= "[" + this.array[i][j] + "] " ; 
				
			}
			s+= "\n" ; 
		}
		return s ; 
	}
	
	
	
	
	
	
	
	
	
	
	//----------Gradient----------//
	//							  //
	//							  //
	//----------------------------//
	
	/**
	 * Calculate value of dxu
	 */
	public Double2DReal calculDxu() {
		double[][]dxuM ;
		dxuM = new double[rows][columns] ;
		for (int i = 0 ; i<array.length-1 ; i++) {
			for (int j = 0 ; j<array[0].length ; j++) {
				dxuM[i][j]=array[i+1][j]-array[i][j] ; 
			}
		}
		Double2DReal dxu = new Double2DReal(dxuM) ; 
		return dxu ; 
	}
	
	
	/**
	 * Calculate value of dyu
	 */
	public Double2DReal calculDyu() {
		double[][]dyuM ; 
		dyuM = new double[rows][columns] ;
		for (int i = 0 ; i<array.length ; i++) {
			for (int j = 0 ; j<array[0].length-1 ; j++) {
				dyuM[i][j]=array[i][j+1]-array[i][j] ;
			}
		}
		Double2DReal dyu = new Double2DReal(dyuM) ; 
		return dyu ; 
	}
	
	
	public Double2DReal calculGradv2(double[][] filter) {
		
		double[][] dxuM = new double[rows][columns] ; 
		
		
		//calcul coefficient filter
		double coeff=0  ; 
		for (int i=0 ; i<filter.length ; i++) {
			for (int j=0 ; j<filter[0].length ; j++) {
				coeff+=Math.abs(filter[i][j]) ; 
			}
		}
		coeff=1/coeff ; 
		
		
		//initialization
		int sum, iEff, jEff, a , it, jt, w , h ; 
		sum = 0 ; 
		it = 0 ; 
		jt = 0 ; 
		h = rows ; 
		w = columns  ; 
		a = (filter.length-1)/2 ; 
		
		for (int i=0 ; i<rows ; i++) {
			for (int j=0 ; j<columns ; j++) {
				for (int k=-a ; k<=a ; k++) {
					for (int l=-a ; l<=a ; l++) {
						
						iEff  = i+k ; 
						jEff  = j+l ;
						
						if (jEff<0) {
							jEff = Math.abs(l) ; 
						}
						else if(jEff>=w) {
							jEff = (w - ((j+l+1)-w))-1 ; 
						}
						
						
						
						if (iEff<0) {
							iEff = Math.abs(k) ; 
						}
						else if (iEff>=h) {
							iEff = (h - ((i+k+1)-h))-1 ;
						}
						sum+=filter[it][jt]*array[iEff][jEff] ; 
						jt++ ; 
					}
					jt=0 ; 
					it++ ; 
				}
				it = 0 ; 
				jt = 0 ; 
				dxuM[i][j]=coeff*sum ; 
				sum = 0 ; 
			}
		}
		
		Double2DReal dxu = new Double2DReal(dxuM) ; 
		
		return dxu  ; 
	}
	
	public static double[][] createYFilter3x3 () {
		double[][] filter = new double[3][3] ; 
		filter[0][0] = -3 ; 
		filter[0][1] =  -10;
		filter[0][2] =  -3 ; 
		
		filter[2][0] =   3 ; 
		filter[2][1] =	10; 
		filter[2][2] =  3 ;
		
		return filter ; 
	}
	
	public static double[][] createXFilter3x3 () {
		double[][] filter = new double[3][3] ; 
		filter[0][0] = -3 ; 
		filter[0][2] =  3 ; 
		filter[1][0] = -10; 
		filter[1][2] =  10; 
		filter[2][0] = -3 ; 
		filter[2][2] =  3 ;
		
		return filter ; 
	}
	
	
	
	
	
	
	
	
	
	
	//----------Fourier----------//
	//							 //
	//							 //
	//---------------------------//
	
	/**
	 * Initialization of the complex matrix
	 */
	private void initFourier () {
		double[][] tmp = transformInComplex() ;
		complex = new Double2DComplex(tmp) ;
		complexExist = true ; 
	}
	
	/**
	 * Transform a real matrix in complex matrix
	 * @return tmp
	 */
	private double[][] transformInComplex () {
		double [][] tmp = new double[rows][columns*2] ;
		for (int i = 0 ; i<array.length ; i++) {
			for (int j=0 ; j<array[0].length ; j++) {
				tmp[i][j*2]=array[i][j] ; 
			}
		}
		return tmp ;
	}
	
	/**
	 * Calculate the FFT 
	 * @return fftn
	 */
	public Double2DComplex getFFTn () {
		if (!complexExist) 
			initFourier() ; 
		return   complex.getFFTn() ; 
	}
	
	/**
	 * Calculate the inverse of the fft
	 * @return ifftn
	 */
	public Double2DComplex getIFFTn() {
		if (!complexExist) 
			initFourier() ; 
		return   complex.getIFFTn(true) ; 
	}
	
	
	
	
	
	
	
	
	//----------Convolution----------//
	//							 	 //
	//								 //
	//-------------------------------//
	/**
	 * Convolution beetween an image and a filter
	 * @param u
	 * @param h
	 * @return convolution
	 */
	public static Double2DReal convolution(Double2DReal u , Double2DReal h) {
		//ifft2(fft2(u).*fft2(h));
		//THREAD (less efficiency)
//		Double2DComplex [] tmp = new Double2DComplex[2] ;
//		ThreadConvoFFT tU = new ThreadConvoFFT(tmp,u,0) ; 
//		ThreadConvoFFT tH = new ThreadConvoFFT(tmp,h,1) ; 
//		tU.start();
//		tH.start();
//		try {
//			tU.join();
//			tH.join();
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//		
//		return new Double2DReal(((tmp[0].multiply(tmp[1])).getIFFTn(true)).transformInReal()) ; 
		
		
		return new Double2DReal(((u.getFFTn().multiply(h.getFFTn())).getIFFTn(true)).transformInReal()) ; 
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	//----------Eigen-Values-Vectors----------//
	//										 //
	//										 //
	//										 //
	//---------------------------------------//
	/**
	 * Check values of rows and columns
	 * Return values of matrix
	 * @return tmp
	 * @throws IllegalArgumentException
	 */
	public double[] initEigen () throws IllegalArgumentException{
		if ((rows!=columns) || ((rows==columns) && (rows!=2))) {
			throw new IllegalArgumentException("Need a 2x2 symetric matrix") ; 
		}
		double a,c,c2,b; 
		a=this.array[0][0] ; 
		c=this.array[0][1] ; 
		c2=this.array[1][0] ;
		if (c!=c2) {
			throw new IllegalArgumentException("Need a 2x2 symetric matrix") ; 
		}
		b=this.array[1][1] ; 
		double tmp[] = {a,c,b} ; 
		return  tmp  ; 
	}
	
	
	/**
	 * Calculate the eigenvalues of the matrix
	 * 
	 * @return tmp
	 */
	public double[] eigenValues() {
		double a,c,b,
				lamP,lamM ;
		double [] tmpValues ; 
		
		//initialize eigen
		tmpValues = initEigen() ; 
		
		//get the values of the matrix
		a=tmpValues[0] ; 
		c=tmpValues[1] ; 
		b=tmpValues[2] ; 
		

		//calculate eigenvalues
		lamP=0.5d*((a+b)+Math.sqrt(((a-b)*(a-b))+4*(c*c))) ; 
		lamM=0.5d*((a+b)-Math.sqrt(((a-b)*(a-b))+4*(c*c))) ; 
		
		//Store eigenvalues into an array 
		double [] tmp = new double[2] ; 
		tmp[0]=lamM ; 
		tmp[1]=lamP ; 
		
		return tmp ; 
		
	}
	
	
	/**
	 * Calculate the eigenvectors of the matrix
	 * @return
	 */
	public double[] eigenVectors() {
		double a,c,b,
				nP,nM,
				v11,v12,
				v21,v22;
		double [] tmpValeurs ; 
		double [] tmp ; 
		
		//initialize eigen
		tmpValeurs = initEigen() ;  
		
		//get the values of the matrix
		a=tmpValeurs[0] ; 
		c=tmpValeurs[1] ; 
		b=tmpValeurs[2] ; 
		 
		
		
		//calculate nPlus and nMoins
		nP=Math.sqrt(((2*c)*(2*c))+( (b-a) + (Math.sqrt(((a-b)*(a-b))+4*(c*c))))*((b-a)+Math.sqrt(((a-b)*(a-b))+4*(c*c)))) ; 
		nM=Math.sqrt(((2*c)*(2*c))+( (b-a) - (Math.sqrt(((a-b)*(a-b))+4*(c*c))))*((b-a)-Math.sqrt(((a-b)*(a-b))+4*(c*c)))); 
		
		//calculate the vector v1
		v11=(2*c)/nP ; 
		v12=((b-a)+(Math.sqrt(( (a-b)*(a-b) )+ (4 *(c*c)))))/nP ; 
		
		//calculate the vector v2
		v21=(2*c)/nM ; 
		v22=((b-a)-(Math.sqrt(( (a-b)*(a-b) )+4*(c*c)) ))/nM ; 
		
		//Store the eigenvectors into an array
		tmp = new double[4] ;
		tmp[0]= v11 ; 
		tmp[1]= v21 ; 
		tmp[2]= v12 ; 
		tmp[3]= v22 ;
			
		
		return tmp ; 
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	//----------Operations----------//
	//								//
	//								//
	//------------------------------//
	
	/**
	 * Calculate the Hadamard multiplication beetween 2 matrix
	 * @param d
	 * @return res
	 */
	public Double2DReal multiplyHadamard (Double2DReal d) throws IllegalArgumentException{
		if (rows==d.getRows() && columns==d.getColumns()) {
			double [][] tmp = new double[rows][columns] ;
			for (int i=0 ; i<rows ; i++) {
				for (int j=0 ; j<columns ; j++) {
					tmp[i][j]=((this.getValue(i, j))*(d.getValue(i, j))) ; 
				}
			}
			Double2DReal res = new Double2DReal(tmp); 
			return res ; 
		}
		else
			throw new IllegalArgumentException("Matrix must have the same dimension ! ") ;
		
	}
	
	
	
}
