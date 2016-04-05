package debrito.ressources;

import debrito.convolution.res.DoubleFFT_2D;

public class Double2DComplex {
	
	
	private double[][] array ; 
	private int rows,columns ; 
	private DoubleFFT_2D fft ; 
	
	//----------Constructors-----------//
	//								   //
	//								   //
	//---------------------------------//
	/**
	 * Construct matrix 
	 * @param r
	 * @param c
	 */
	public Double2DComplex (int r, int c)
	{
		this.rows = r;
		this.columns = c;
		array = new double[r][columns*2];
	}
	
	/**
	 * Construct matrix with an array
	 * @param tab
	 */
	public Double2DComplex(double [][] tab) {
		this.rows = tab.length;
		this.columns = tab[0].length/2;
		array = new double[rows][columns*2];
		for(int i= 0; i < rows; i++)
		{
			for(int j= 0; j< columns*2; j++)
			{ 
				array[i][j] = tab[i][j];
			}
		}
		fft = new DoubleFFT_2D(tab.length, tab[0].length/2);
	}
	
	
	
	
	public double[][] getArray(){
		return array ; 
	}
	
	public String toStringArray() {
		String s ="" ; 
		for (int i=0 ; i<array.length ; i++) {
			for (int j =0 ; j<array[0].length ; j++) {
				s+="["+array[i][j]+"]  " ; 
			}
			s+="\n" ; 
		}
		return s ; 
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	//----------Fourier----------//
	//							 //
	//							 //
	//---------------------------//
	
	
	/**
	 * Initialize the FFT matrix
	 */
	public void initFFT()
	{
		if(this.fft == null){
			this.fft = new DoubleFFT_2D(rows,columns); //call the extern library
		}
	}
	
	
	
	/**
	 * Transform complex in real
	 * @return tmp
	 */
	public double [][] transformInReal () {
		double [][] tmp = new double[rows][columns] ; 
		for (int i=0 ; i<array.length ; i++) {
			for (int j=0 ; j<(array[0].length)/2 ; j++) {
				tmp[i][j] = array[i][j*2] ; 
			}
		}
		return tmp ; 
	}
	
	
	/** 
	 * Calculate FFT 
	 * @return res
	 */
	public Double2DComplex getFFTn()
	{
		
		this.initFFT();
		
		double[][] backup = new double[rows][columns*2];

		for(int i= 0; i < rows; i++)
		{
			for(int j= 0; j< columns*2; j++)
			{ 
				backup[i][j]= array[i][j];
			}
		}

		fft.complexForward(this.array);	// call the extern library

		Double2DComplex res = new Double2DComplex(this.array);
		
		array=backup ; 
		
		
		return res;
		
	}
	
	
	/**
	 * Calculate the inverse of FFT 
	 * @param scaling
	 * @return res
	 */
	public Double2DComplex getIFFTn(boolean scaling) {
		this.initFFT();
		// lets backup array
		double[][] backup = new double[rows][columns*2];

		for(int i= 0; i < rows; i++)
		{
			for(int j= 0; j< columns*2; j++)
			{ 
				backup[i][j]= this.array[i][j];
			}
		}

		fft.complexInverse(this.array, scaling);  // call the extern library

		Double2DComplex res = new Double2DComplex(this.array);

		array=backup ; 
		
		return res;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	//----------Arrays----------//
	//			  and values    //
	//						    //
	//--------------------------//
	
	
	/**
	 * Put a value 
	 * @param value
	 * @param row
	 * @param column
	 * @param complex
	 * @throws IllegalArgumentException
	 */
	public void setValue(double value, int row, int column, boolean complex) throws IllegalArgumentException
	{
		if(row < this.rows && column < this.columns)
		{
			if(complex)
				this.array[row][2*column+1] = value;
			else
				this.array[row][2*column] = value;
		} else {
			throw new IllegalArgumentException("rows and columns wrong") ; 
		}
	}
	
	
	/**
	 * Get a value
	 * @param row
	 * @param column
	 * @param complex
	 * @return
	 */
	public double getValue(int row, int column, boolean complex) throws IllegalArgumentException 
	{
		if(row < this.rows && column < this.columns)
		{
			if(complex)
				return this.array[row][2*column+1];
			else 
				return this.array[row][2*column];
		} else {
			throw new IllegalArgumentException("Row and colomuns wrong") ; 
		}
	}
	
	
	
	
	
	
	public int getRows() {
		return this.rows;
	}
	
	public int getColumns() {
		return this.columns;
	}
	
	private String name;
	
	public void setName(String s) {
		name = s;
	}
	
	public String getName() {
		return name;
	}
	
	
	//----------Operations----------//
	//								//
	//								//
	//------------------------------//
	
	/**
	 * Multiplication of two complex matrix
	 * @param d
	 * @return
	 */
	public Double2DComplex multiply(Double2DComplex d)
	{
		Double2DComplex res = new Double2DComplex(this.rows, this.columns);
		double[] val = new double[2];
		this.setName("Locale");
		d.setName("d");
		for(int j= 0; j< rows; j++)
		{ 
			for(int k= 0; k < columns; k++)
			{
				val = multiplyComplex(this.getValue(j, k, false), this.getValue( j, k, true), d.getValue( j, k, false), d.getValue( j, k, true));
				res.setValue(val[0], j, k, false);
				res.setValue(val[1], j, k, true);
			}
		}
		return res;
	}
	
	

	/**
	 * Multiplication of two complex
	 * @param real1
	 * @param im1
	 * @param real2
	 * @param im2
	 * @return
	 */
	public static double[] multiplyComplex(double real1, double im1, double real2, double im2) {
		double r = real1 * real2 - im1 * im2;
		double imag = real1 * im2 + im1 * real2;
		double[] res =  {r, imag};
		return res;
	}
	
	
		
		
	

}
