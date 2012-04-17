package evaluatoin.lda;

import java.util.ArrayList;

public class XMatrix {

    private int m = 0;             // number of rows
    private int n = 0;             // number of columns
    private double[][] data;   // M-by-N array
    private ArrayList<String> row_labels = null;
    private ArrayList<String>  column_labels = null;

    public XMatrix(int m,int n){
    	this.m = m;
    	this.n = n;
    	this.data = new double[m][n];
    	this.row_labels = new ArrayList<String>();
    	this.column_labels = new ArrayList<String>();
    }
    
    // x, y is the sub-index
    public void setData(int x, int y, double value){
    	this.data[x][y] = value;
    }
    
    public void setRowLabel(String label){
    	this.row_labels.add(label);
    }    
    
    public double getCell(int x, int y){
    	return this.data[x][y];
    }
    
    public double[] getRow(int x){
    	return this.data[x];
    }
    
    public ArrayList<String> getColumn_labels() {
    	System.out.println("colume:"+this.column_labels);
		return column_labels;
	}

	public String toRow(int m){
    	String row = "";
    	if(this.row_labels.size()>0){
    		row+=this.row_labels.get(m)+",";
    	}    	
    	for(int i=0;i<n;i++){
    		row +=this.data[m][i]+",";
    	}
    	row+="\n";
    	return row;
    }
    
    public String toHeader(){
    	String header = "";
    	System.out.println("toheader: colum: "+this.column_labels.toString());
    	if(this.column_labels.size()>0){
        	for(int i=0;i<n;i++){
        		header +=","+this.column_labels.get(i);
        	}    		
    	}
    	header += "\n";
    	System.out.println(header);
    	return header;
    }

    
	public int getM() {
		return m;
	}

	public int getN() {
		return n;
	}
    
    public void setHeader(ArrayList<String> topics){
    	for(int t=0;t<topics.size();t++){
    		this.column_labels.add(topics.get(t));
    	}    	
    }
	
	public void setRow(ArrayList<String> topics){
    	for(int t=0;t<topics.size();t++){
    		this.row_labels.add(topics.get(t));
    	}    		
	}
	
}
