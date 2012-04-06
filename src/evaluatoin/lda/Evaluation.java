package evaluatoin.lda;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;

public class Evaluation {
	
	private ArrayList<String> regular_lda_topics = null;
	private ArrayList<String> label_lda_topics = null;
	private String home = "./data/";
	private int num_of_bugs = 0;
	
	
	public Evaluation(String vendor){		
		if(vendor.equals("htc")){
			this.home += "htc/";
			this.num_of_bugs = Constant.NUM_OF_BUG_HTC;
		}else if(vendor.equals("moto")){
			this.home += "moto/";
			this.num_of_bugs = Constant.NUM_OF_BUG_MOTO;
		}
		
		this.regular_lda_topics = this.getRegularTopics(this.home+Constant.REGULAR_TOPICS_FILE);
		this.label_lda_topics = this.getRegularTopics(this.home+Constant.LABEL_TOPICS_FILE);	
		
	}
	
	
	public static void main(String args[]){
		Evaluation evalue = new Evaluation("htc");
		XMatrix regular = evalue.getDistribution4R();
		evalue.serializeMatrix(regular,"./data/output.csv");
	}
	
	/*
	 * Calculate Jaccard simularity of topics from regular LDA and Labelled LDA with a threshold
	 * input: regular distribution matrix(topics*bugs), label distribution matrix(topics*bugs)
	 * output: matrix (regular_topics, label_topics)  
	 */
	public XMatrix JaccardSimularity (XMatrix r_topic_distr,XMatrix l_topic_distr,double threshold){
		XMatrix simularity = new XMatrix(r_topic_distr.getM(),l_topic_distr.getM());
		try{
			for(int r=0;r<r_topic_distr.getM();r++){
				for(int l=0;l<l_topic_distr.getM();l++){
					simularity.setData(r, l, makeJaccard(r_topic_distr.getRow(r),l_topic_distr.getRow(l),threshold));		
				}
			}							
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return simularity;		
	}
	
	/*
	 * calculate jaccard between two vectors, but first we need to optimize the distribution  
	 * the vectors are the distribution from regular LDA and labelled LDA
	 * 
	 */
	private double makeJaccard(double[] r_distribution, double[] l_distribution,double threshold){
		try{
			ArrayList<Integer> regular = new ArrayList<Integer>();
			ArrayList<Integer> labeled = new ArrayList<Integer>();			
			for(int i=0;i<r_distribution.length;i++){
				if(r_distribution[i]>=threshold)
					regular.add(i);
			}
						
			for(int j=0;j<l_distribution.length;j++){
				if(r_distribution[j]>=threshold)
					labeled.add(j);
			}
			int intersection = regular.size();
			regular.removeAll(labeled);
			intersection -= regular.size();
			regular.addAll(labeled);
			int union = regular.size();
			
			return (intersection * 1.0)/ (union * 1.0);
			
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return -1;
	}
	
	/*
	 * get the topics from files 
	 */

	private ArrayList<String> getRegularTopics(String inFile){
		ArrayList<String> topics = new ArrayList<String>();
		BufferedReader br = null;	
		try{
			if(!new File(inFile).exists()){
				throw new Exception("There is no topic file");
			}
			
			br = new BufferedReader(new FileReader(inFile));
			String line = br.readLine();
			while(line != null){
				topics.add(line.trim());
				line = br.readLine();
			}			
		}catch(Exception e){
			e.printStackTrace();
		}
		return topics;
	}

	/*
	 * parse distribution for regular lda
	 * input: the distribution file
	 * output: a matrix (topics * bugs), rows is topics and columns are bugs,
	 * value is the relevance of this topic for each bug 
	 */
	private XMatrix getDistribution4R(){
		XMatrix matrix = new XMatrix(this.regular_lda_topics.size(),this.num_of_bugs);
		System.out.println("regular distribution matrix : "+ matrix.getM()+":"+matrix.getN());
		BufferedReader br = null;
		try{
			br = new BufferedReader(new FileReader(this.home+Constant.REGULAR_DISTRIBUTION_FILE));
			String line = br.readLine();
			int bugs = 0;
			while(line != null){
				line = line.trim();
				String[] metrics = line.split(",");
				for(int i=0;i<matrix.getM();i++){
					double value = Double.parseDouble(metrics[i+1]);
					matrix.setData(i,bugs,value);					
				}
				
				line = br.readLine();
				bugs++;
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				if(br != null)
					br.close();	
			}catch(Exception e){
				e.printStackTrace();
			}			
		}	
		return matrix;
	}
	
	/*
	 * parse distribution for lablled lda
	 * This is different from regular LDA, because the distribution file format is different
	 * input: the distribution file
	 * output: a matrix (topics * bugs), rows is topics and columns are bugs,
	 * value is the relevance of this topic for each bug 
	 * 
	 */
	private XMatrix getDistribution4L(){
		
		XMatrix matrix = new XMatrix(this.label_lda_topics.size(),this.num_of_bugs);
		BufferedReader br = null;
		try{
			br = new BufferedReader(new FileReader(this.home+Constant.LABEL_DISTRIBUTION_FILE));
			String line = br.readLine();
			int bugs = 0;
			while(line != null){
				line = line.trim();
				String[] metrics = line.split(",");
				//1,3,5,7
				for(int i=1;i<=(metrics.length-1)/2;i+=2){
					int topic_index = Integer.parseInt(metrics[i]);
					double value = Double.parseDouble(metrics[i+1]);
					matrix.setData(bugs,topic_index,value);
					}					
				line = br.readLine();
				bugs++;
			}
			br.close();
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				if(br != null)
					br.close();	
			}catch(Exception e){
				e.printStackTrace();
			}			
		}	
		return matrix;		
	}
	
	
	private void serializeMatrix(XMatrix matrix,String outFile){
		BufferedWriter bw = null;
		try{
			bw = new BufferedWriter(new FileWriter(outFile));
			for(int i=0;i<matrix.getM();i++){
				bw.write(matrix.toRow(i));	
			}
			bw.close();
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				if(bw != null)
					bw.close();	
			}catch(Exception e){
				e.printStackTrace();
			}			
		}	
	}
	
	
	
}
