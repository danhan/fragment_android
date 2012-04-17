package evaluatoin.lda;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class Evaluation {
	
	public ArrayList<String> regular_lda_topics = null;
	public ArrayList<String> label_lda_topics = null;
	private String home = "./data/";
	private int num_of_bugs = 0;
	
	SimpleDateFormat df = new SimpleDateFormat("EEE dd MMM yyyy HH:mm:ss ZZZZ"); 	
	SimpleDateFormat rMonth = new SimpleDateFormat("yyyy-MM");
	
	
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
		String vendor = "htc";
		String home = "./data/"+vendor+"/";
		Evaluation evalue = new Evaluation(vendor);
		
		int function = 1;
		if(function == 0){
			// get relevance for regular
			XMatrix regular = evalue.getDistribution4R();		
			XMatrix relevance4r = evalue.calculateRelevance(regular);
			System.out.println(evalue.regular_lda_topics);
			relevance4r.setHeader(evalue.regular_lda_topics);
			evalue.serializeMatrix(relevance4r,home+"matrix-relevance-r.csv");
			
			XMatrix label = evalue.getDistribution4L();	
			XMatrix relevance4l = evalue.calculateRelevance(label);
			relevance4l.setHeader(evalue.label_lda_topics);
			evalue.serializeMatrix(relevance4l,home+"matrix-relevance-l.csv");
					
		}else if(function ==1){
			// get number of bugs for each topics for lda and labeled lda
			XMatrix regular = evalue.getDistribution4R();		
			XMatrix label = evalue.getDistribution4L();						
			
			//regular bugs
			XMatrix bugStat4R = evalue.BugNumbersMatrix(regular,label,0.2,1);
			bugStat4R.setHeader(evalue.label_lda_topics);
			bugStat4R.setRow(evalue.regular_lda_topics);
			evalue.serializeMatrix(bugStat4R,home+"topics-bugs-r.csv");
			
			
			XMatrix bugStat4l = evalue.BugNumbersMatrix(regular,label,0.2,2);
			bugStat4l.setHeader(evalue.label_lda_topics);
			bugStat4l.setRow(evalue.regular_lda_topics);
			evalue.serializeMatrix(bugStat4l,home+"topics-bugs-l.csv");
			
			// for the intersection of bugs of lda and labled , 
			// row is topcis from regular lda, column is topics from labeled lda
			XMatrix bugStat4Inter = evalue.BugNumbersMatrix(regular,label,0.2,3);
			bugStat4Inter.setHeader(evalue.label_lda_topics);
			bugStat4Inter.setRow(evalue.regular_lda_topics);
			evalue.serializeMatrix(bugStat4Inter,home+"topics-bugs-inter.csv");			
		}else{
			// create similarity
			XMatrix regular = evalue.getDistribution4R();
			//for test
			//evalue.serializeMatrix(regular,home+"matrix-regular.csv");
			
			XMatrix label = evalue.getDistribution4L();				
			//for test
			//evalue.serializeMatrix(label,home+"matrix-label.csv");	
			
			XMatrix similarity = evalue.JaccardSimilarity(regular,label,0.2);
			similarity.setHeader(evalue.label_lda_topics);
			similarity.setRow(evalue.regular_lda_topics);
			evalue.serializeMatrix(similarity,home+"matrix-similarity.csv");			
		}

		
	}
	
	/*
	 * get the number of bugs by topic for regular LDA and labeled LDA
	 * the input is the distribution of topic and bugs
	 * the output is the bugs 
	 * type: 1: bug numbers of regular LDA, 2: bug numbers of Labeled LDA, 3: intersection bugs of two LDAs
	 */
	public XMatrix BugNumbersMatrix (XMatrix r_topic_distr,XMatrix l_topic_distr,double threshold,int type){
		XMatrix similarity = new XMatrix(r_topic_distr.getM(),l_topic_distr.getM());
		try{
			for(int r=0;r<r_topic_distr.getM();r++){
				for(int l=0;l<l_topic_distr.getM();l++){
					similarity.setData(r, l, makeJaccard(r_topic_distr.getRow(r),l_topic_distr.getRow(l),threshold)[type]);		
				}
			}							
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return similarity;		
	}	

	
	
	/*
	 * Calculate Jaccard similarity of topics from regular LDA and Labelled LDA with a threshold
	 * input: regular distribution matrix(topics*bugs), label distribution matrix(topics*bugs)
	 * output: matrix (regular_topics, label_topics)  
	 */
	public XMatrix JaccardSimilarity (XMatrix r_topic_distr,XMatrix l_topic_distr,double threshold){
		XMatrix similarity = new XMatrix(r_topic_distr.getM(),l_topic_distr.getM());
		try{
			for(int r=0;r<r_topic_distr.getM();r++){
				for(int l=0;l<l_topic_distr.getM();l++){
					similarity.setData(r, l, makeJaccard(r_topic_distr.getRow(r),l_topic_distr.getRow(l),threshold)[0]);		
				}
			}							
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return similarity;		
	}	
	
	/*
	 * calculate jaccard between two vectors, but first we need to optimize the distribution  
	 * the vectors are the distribution from regular LDA and labelled LDA
	 * 
	 */
	private double[] makeJaccard(double[] r_distribution, double[] l_distribution,double threshold){
		try{
			ArrayList<Integer> regular = new ArrayList<Integer>();
			ArrayList<Integer> labeled = new ArrayList<Integer>();			
			for(int i=0;i<r_distribution.length;i++){
				if(r_distribution[i]>=threshold)
					regular.add(i);
			}
						
			for(int j=0;j<l_distribution.length;j++){
				if(l_distribution[j]>=threshold)
					labeled.add(j);
			}
			//System.out.println(regular);
			double r = regular.size();
			double l = labeled.size();
			int intersection = regular.size();
			regular.removeAll(labeled);
			intersection -= regular.size();
			regular.addAll(labeled);
			int union = regular.size();
			
			//System.out.println(intersection+"/"+union);
			
			return new double[]{(intersection * 1.0)/ (union * 1.0),r,l,intersection};
			
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return new double[]{-1,-1,-1};
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
		System.out.println("label matrix : "+matrix.getM()+";"+matrix.getN());
		BufferedReader br = null;
		try{
			br = new BufferedReader(new FileReader(this.home+Constant.LABEL_DISTRIBUTION_FILE));
			String line = br.readLine();
			int bugs = 0;
			while(line != null){
				line = line.trim();
				String[] metrics = line.split(",");				
				//1,3,5,7
				for(int i=1;i<metrics.length;i+=2){
					int topic_index = Integer.parseInt(metrics[i]);
					double value = Double.parseDouble(metrics[i+1]);
					//System.out.println(topic_index+";"+bugs+"=>"+value);
					matrix.setData(topic_index,bugs,value);
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
	
	/*
	 * calculate the relevance for each topic 
	 * input: the bug time mapping
	 *        the matrix of bug-topic distribution
	 *        type: 0 means regular, 1 means label
	 *  output: file: time(month),topic1,topic2.....      
	 */
	public XMatrix calculateRelevance(XMatrix distribution){
		
		// get the bug and time mapping 
		HashMap<String,ArrayList<Integer>> time_bugs = this.formatTimeToBugs();
		
		// row is the time, column is the topics
		XMatrix relevanceMatrix = new XMatrix(time_bugs.size(),distribution.getM());
		try{
			
			int period = 0;
			for(String time:time_bugs.keySet()){
				relevanceMatrix.setRowLabel(time);
				ArrayList<Integer> bug_indexes = time_bugs.get(time);
				System.out.println(time+"=>"+bug_indexes.toString());
				// a certain time for each topics
				for(int t=0;t<distribution.getM();t++){
					double t_relevance = 0;
					for(int i=0;i<bug_indexes.size();i++){
						int bug_index = bug_indexes.get(i);
						t_relevance += distribution.getCell(t,bug_index);
					}
					relevanceMatrix.setData(period, t, (t_relevance / bug_indexes.size()));
				}
				period++;				
			}
					
			

		}catch(Exception e){
			e.printStackTrace();
		}			
			
		return relevanceMatrix;
	}
	
	
	/*
	 * change the time from the readable to the centain format
	 * 
	 * input : the file with the bugs and time
	 * 			type: 0 is regular, 1 is label
	 * output: the file with the bugs and formated time
	 * 
	 */
	private HashMap<String,ArrayList<Integer>> formatTimeToBugs(){		
		
		BufferedReader br = null;
		BufferedWriter bw = null;
		HashMap<String,ArrayList<Integer>> time_bugs = new HashMap<String,ArrayList<Integer>>();
		try{
			br = new BufferedReader(new FileReader(this.home+Constant.BUT_TIME_FILE));
			bw = new BufferedWriter(new FileWriter(this.home+Constant.FORMATED_BUG_TIME_FILE));	
			
			String line = br.readLine();
			int bug_index = 0;
			while(line != null){
				line = line.trim();
				String[] metrics = line.split(",");				
				//System.out.println(metrics[2]);
				Date date_formated = df.parse(metrics[1].trim());					
				String to_date = rMonth.format(date_formated);
				if(time_bugs.containsKey(to_date)){
					time_bugs.get(to_date).add(bug_index);
				}else{
					ArrayList<Integer> bug_list = new ArrayList<Integer>();
					bug_list.add(bug_index);
					time_bugs.put(to_date, bug_list);
				}
				
				bw.write(metrics[0]+","+to_date+","+metrics[1]+"\n");
			
				line = br.readLine();	
				bug_index++;
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				if(br != null)
					br.close();
				if(bw != null)
					bw.close();
			}catch(Exception e){
				e.printStackTrace();
			}			
		}
		return time_bugs;
	}
	
	
	/*
	 * change the matrix into string
	 */
	private void serializeMatrix(XMatrix matrix,String outFile){
		BufferedWriter bw = null;
		try{
			bw = new BufferedWriter(new FileWriter(outFile));
			if(matrix.getColumn_labels().size()>0){
				bw.write(matrix.toHeader());	
			}	
			System.out.println("========"+matrix.toHeader());
			
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
	/*
	 * @deprecated
	 * to get the actual relevance
	 */
	public XMatrix getActualDistribution(XMatrix matrix){		
		for(int i=0;i<matrix.getM();i++){
			for(int j=0;j<matrix.getN();j++){
				if(matrix.getCell(i, j)>0){
					matrix.setData(i, j, 1);
				}
			}
		}
		return matrix;
	}
	
}
