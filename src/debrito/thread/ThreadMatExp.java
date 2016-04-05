package debrito.thread;

public class ThreadMatExp extends Thread{
	
	private double [][] d,array ; 
	private int start,stop ; 
	
	public ThreadMatExp (double [][] d , double [][] array, int start, int stop) {
		this.d=d ; 
		this.array=array ; 
		this.start=start ; 
		this.stop=stop ; 
	}
	
	public void run() {
		for (int i = 0 ; i<array.length ; i++) {
			for (int j = start ; j<=stop ; j++) {
				array[i][j]=Math.exp(d[i][j]) ;
			}
		}
	}
}
