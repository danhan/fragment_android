package evaluatoin.lda;

public class XMatrix {

    private int m = 0;             // number of rows
    private int n = 0;             // number of columns
    private double[][] data;   // M-by-N array

    public XMatrix(int m,int n){
    	this.m = m;
    	this.n = n;
    	this.data = new double[m][n];
    }
    
    // x, y is the sub-index
    public void setData(int x, int y, double value){
    	this.data[x][y] = value;
    }
    

    public double[] getRow(int x){
    	return this.data[x];
    }
    
    public String toRow(int m){
    	String row = "";
    	for(int i=0;i<n;i++){
    		row +=this.data[m][i]+",";
    	}
    	row+="\n";
    	return row;
    }

	public int getM() {
		return m;
	}

	public int getN() {
		return n;
	}
    
    
}
