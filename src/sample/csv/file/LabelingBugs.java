package sample.csv.file;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/*
 * @deprecated
 * 
 */
public class LabelingBugs {	
	
	protected ArrayList<String> regular_topics = null;
	protected ArrayList<String> labelled_topics = null;
	protected ArrayList<String> union_topics = null;
	private double[] cases = null; // find the better threshold for finding the main distributions
	
	protected String homeFolder = "";	
	protected static String R_TOPIC_FILE_NAME = "regular-topics.csv";
	protected static String L_TOPIC_FILE_NAME = "label-topics.csv";
	protected static String R_DISTRIBUTE_TOPIC_FILE_NAME = "regular-topic-distribute.csv";
	protected static String L_BUG_TOPIC_ENTIRE_NAME = "label-bug-topic-entire.csv";
	protected static String R_BUG_TOPIC_FILE_NAME = "i-regular-bug-topic.csv";
	protected static String L_BUG_TOPIC_FILE_NAME = "i-label-bug-topic.csv";
	protected static String O_BUG_TOPIC_FILE_NAME = "o-merged-bug-topic.csv";
	protected static String O_FALSE_POSITIVE_FILE_NAME = "o-false-positive.csv";
	
	
	public LabelingBugs(String homeFolder){
		this.homeFolder = homeFolder;
		this.regular_topics = this.getRegularTopics(this.homeFolder + "/"+ R_TOPIC_FILE_NAME);
		this.labelled_topics = this.getRegularTopics(this.homeFolder + "/"+ L_TOPIC_FILE_NAME);
		this.union_topics = (ArrayList<String>)this.regular_topics.clone();
		this.union_topics.removeAll(this.labelled_topics);
		System.out.println("unique in regular topic: "+this.union_topics.toString());	
		this.union_topics.addAll(this.labelled_topics);
		HashSet<String> hs = new HashSet<String>();
		hs.addAll(this.union_topics);
		this.union_topics.clear();
		this.union_topics.addAll(hs);
		System.out.println("regular_topic=>"+this.regular_topics.size()+";label_topic=>"+this.labelled_topics.size()+";union=>"+this.union_topics.size());
	}
	/*
	 * get the topics from files 
	 */
	public ArrayList<String> getRegularTopics(String inFile){
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
	 * @deprecated
	 * add label for all bugs in the input file
	 * entire: true, the output file includes all items from  original file; false, the output file only contains the bug and topics
	 */
	public void AddLabels4Regular(int num_of_topics,double[] threshold,boolean entire){
		BufferedReader br = null;
		BufferedWriter bw = null;
		String inFile = this.homeFolder + "/"+ R_DISTRIBUTE_TOPIC_FILE_NAME;
		String outFile = this.homeFolder + "/" + R_BUG_TOPIC_FILE_NAME;	
		this.cases = threshold;
		
		try{
			if(!new File(inFile).exists()){
				throw new Exception("There is no topic file");
			}			
			
			br = new BufferedReader(new FileReader(inFile));
			bw = new BufferedWriter(new FileWriter(outFile));
			String line = br.readLine();
			while(line != null){
				String[] metrics = line.split(",");
				String bugId = metrics[0];
				bw.write(bugId+",");
				ArrayList<String> labels_list = this.caculateLabel4Regular(metrics,num_of_topics,threshold);				
				for(int l=0;l<labels_list.size();l++){
					bw.write(labels_list.get(l)+",");	
				}				
				if(entire){
					bw.write(line.substring(line.indexOf(','),line.length()));	
				}
				bw.write("\n");				
				bw.flush();
				line = br.readLine();
			}
						
		System.out.println("please check output file: "+outFile);			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				if(br!=null)
					br.close();
				if(bw != null)
					bw.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}		
	}
	
	//get the labels if meet the requirement
	private ArrayList<String> caculateLabel4Regular(String[] columns,int num_of_topics,double[] threshold){
		ArrayList<String> results = null;
		try{
			results = new ArrayList<String>();
			for(int n=0;n<threshold.length;n++){
				String labels = "";
				for(int i=1;i<=num_of_topics;i++){
					double  factor = Double.valueOf(columns[i]);
					
					if(factor >= threshold[n]){						
						labels += "#"+this.regular_topics.get(i-1);

					}
				
				}
				results.add(labels);
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}

				
		return results;		
		
	}
	
	/*
	 * @deprecated
	 * get neat bug topic for labelled LDA
	 * left file is Label-LDA file, right file is regular LDA file
	 */
	public void preprocess4LabelledLDA(boolean entire,char delimiter){
		BufferedReader br = null;
		BufferedWriter bw = null;
		String inFile = this.homeFolder + "/"+ L_BUG_TOPIC_ENTIRE_NAME;
		String outFile = this.homeFolder + "/" + L_BUG_TOPIC_FILE_NAME;		
		try{
			if(!new File(inFile).exists()){
				throw new Exception("There is no topic file");
			}			
			
			br = new BufferedReader(new FileReader(inFile));
			bw = new BufferedWriter(new FileWriter(outFile));
			String line = br.readLine();
			while(line != null){
				String[] metrics = line.split(",");
				String bugId = metrics[0];
				String labels = metrics[1].replace(delimiter,'#');				
				
				bw.write(bugId+","+labels);
				if(entire){
					bw.write(line.substring(line.indexOf(','),line.length()));	
				}
				bw.write("\n");				
				bw.flush();
				line = br.readLine();
			}
						
					
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				if(br!=null)
					br.close();
				if(bw != null)
					bw.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}				
	}
	
	private ArrayList<String> toArrayList(String[] str){
		ArrayList<String> output = new ArrayList<String>();
		try{
			if(str!=null){				
				for(int i=0;i<str.length;i++){					
					String tmp = str[i].trim();
					if(tmp.length()==0)
						continue;
					output.add(str[i]);
				}
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return output;
	}
	
	
	/*
	 * @deprecated
	 * get the jaccard value by each bug
	 * A is the set of topics from regular LDA for this bug
	 * B is the set of topics from labelled LDA for this bug
	 */
	public void getJaccard(int num_of_case,char delimiter){
		String[] inFiles = new String[]{			
			this.homeFolder + "/"+ L_BUG_TOPIC_FILE_NAME,
			this.homeFolder + "/"+ R_BUG_TOPIC_FILE_NAME
		};		
		String outFile = this.homeFolder + "/" + O_BUG_TOPIC_FILE_NAME;	
		BufferedReader br1 = null;
		BufferedReader br2 = null;
		BufferedWriter bw = null;
		// initialize the jaccard array
		double sum_jaccard[] = new double[num_of_case];
		for(int j=0;j<sum_jaccard.length;j++){
			sum_jaccard[j] = 0;
		}
		int bug_num = 0;
		try{
			for(int i=0;i<inFiles.length;i++){
				if(!new File(inFiles[i]).exists()){
					throw new Exception("There is no topic file");
				}					
			}			
			br1 = new BufferedReader(new FileReader(inFiles[0]));
			br2 = new BufferedReader(new FileReader(inFiles[1]));
			bw = new BufferedWriter(new FileWriter(outFile));
			String left = br1.readLine();
			String right = br2.readLine();
			
			while(left != null){
				String[] left_metrics = left.split(",");
				String left_bugId = left_metrics[0];
				String left_labels = left_metrics[1].replace(delimiter, '#');
				// write the labelled lda into the file
				bw.write(left_bugId+","+left_labels);
				// statistics of bug numbers
				bug_num++;
				
				String[] right_metrics = right.split(",");
				String right_bugId = right_metrics[0];
				
				for(int i=1;i<num_of_case;i++){	
					String right_labels = "";
					if(right_metrics.length>i){						
						right_labels = right_metrics[i];
					} 			
					
					double one_jaccard = this.jaccard(left_labels, right_labels);
					String left_jacca = new DecimalFormat("##.##").format(one_jaccard);
					// write the regular lda and its jaccard into file
					bw.write(","+right_labels+","+left_jacca);	
					
					sum_jaccard[i-1] += one_jaccard;
					
				}
				
				if(left_bugId.equals(right_bugId)){					
					bw.write("\n");				
					bw.flush();					
				}else{
					System.err.println("left bug is not equal to right bug: "+left_bugId+";"+right_bugId);
				}
			
				left = br1.readLine();
				right = br2.readLine();
			}
			if(right!=null){
				System.err.println(inFiles[1]+" has more rows than "+inFiles[0]);
				while(right != null){
					right = br2.readLine();
					System.out.println(right);
				}
			}	
			System.out.println("Please check your file "+outFile);
			System.out.println("Summary of jaccard for each case are :");	
			
			for(int j=0;j<sum_jaccard.length;j++){
				System.out.println("bug_num=>"+bug_num+";case=>"+this.cases[j]+";sum_jaccard=>"+sum_jaccard[j]);
			}			
					
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				if(br1!=null)
					br1.close();
				if(br2!=null)
					br2.close();
				if(bw != null)
					bw.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}					
	}
	
	
	/*
	 * @deprecated
	 * caculate JACCA the intersection of A and B divide the union of A and B
	 * left_labels: the string with the demiliter "#"
	 */
	private double jaccard(String left_labels,String right_labels){		
		try{
			if(left_labels == null && right_labels == null)
				throw new Exception("The input label has problems!");
			ArrayList<String> left = this.toArrayList(left_labels.split("#"));
			ArrayList<String> right = this.toArrayList(right_labels.split("#"));			
			//System.out.println("DEBUG: raw data: "+left+"==> "+right);
			int intersection = left.size();
			left.removeAll(right);
			intersection -= left.size();
			//System.out.println("DEBUG: "+left);
			left.addAll(right);
			int union = left.size();
			//System.out.println("DEBUG: "+left);			
			
			return ((intersection*1.0) / (union*1.0));		
			
		}catch(Exception e){
			e.printStackTrace();
		}	
		return -1;
		
	}

	
	/*
	 * @deprecated
	 * categorize bugs by topics
	 * the input file is o-merged-bug-topics.csv
	 * out put file is the false positive and true positive
	 */
	public void categorizeBugsByTopics(int num_of_case){
		
		BufferedReader br = null;
		BufferedWriter bw = null;
		String inFile = this.homeFolder + "/" + O_BUG_TOPIC_FILE_NAME;
		String outFile = this.homeFolder + "/" + O_FALSE_POSITIVE_FILE_NAME;
 		//key: topic, value: [label_bugIds, case1_regualar_bugIds, case2_regular_bugIds]
		HashMap<String,String[]> topic_bugs = new HashMap<String,String[]>();
		//initialize the hashmap
		for(int i=0;i<this.union_topics.size();i++){
			String[] values = new String[num_of_case+1];
			for(int j=0;j<num_of_case+1;j++){
				values[j] = "";
			}
			topic_bugs.put(this.union_topics.get(i), values);
		}
		
		try{
			if(!new File(inFile).exists()){
				throw new Exception(inFile+ " does not exist");
			}
			br = new BufferedReader(new FileReader(inFile));
			String line = br.readLine();
			while(line != null){
				line = line.trim();
				String metrics[] = line.split(",");
				String bugId = metrics[0];
				String[] labelled_topic = metrics[1].split("#"); // multiple labelled in labelled LDA
				//System.out.println(metrics[1]);
				for(int l=0;l<labelled_topic.length;l++){
					String item = labelled_topic[l].trim();
					if(item != null && !item.isEmpty()){
						if(topic_bugs.containsKey(item)){
							topic_bugs.get(item)[0]+=bugId+"#";
						}else{
							System.err.println("there is a new topics in labelled lda: "+item);
						}						
					}
					
				}	
				// to deal with the multiple cases 2,4,6,8
				for(int n=1;n<num_of_case;n++){
					String case_topic = metrics[n*2];
					String[] sub_topics = case_topic.split("#");
					for(int j=0;j<sub_topics.length;j++){
						String sub_t = sub_topics[j];
						if(sub_t != null && !sub_t.isEmpty()){
							if(topic_bugs.containsKey(sub_t)){
								topic_bugs.get(sub_t)[n] += bugId+"#";
							}else{
								System.err.println("there is a new topics in regular: "+ sub_t);
							}							
						}
					}					
				}				
				line = br.readLine();
			}			
			bw = new BufferedWriter(new FileWriter(outFile));
			bw.write("topic,label," +
					"0.01-reg,0.01-inter,0.01-fp,0.01-tp," +
					"0.05-reg,0.05-inter,0.05-fp,0.05-tp," +
					"0.1-reg,0.1-inter,0.1-fp,0.1-tp," +
					"0.2-reg,0.2-inter,0.2-fp,0.2-tp," +
					"0.3-reg,0.3-inter,0.3-fp,0.3-tp," +
					"0.4-reg,0.4-inter,0.4-fp,0.4-tp," +
					"0.5-reg,0.5-inter,0.5-fp,0.5-tp\n");			
			bw.flush();
			for(String key:topic_bugs.keySet()){
				String[] values = topic_bugs.get(key);
				
				String labelled_bugs = values[0].trim();
				String[] label_lda = labelled_bugs.split("#");
				if(labelled_bugs.isEmpty()){
					bw.write(key+","+0+",");		
				}else{										
					bw.write(key+","+label_lda.length+",");
				}
					
				for(int c=1;c<values.length;c++){	
					String regular_bugs = values[c].trim();
					if(regular_bugs.isEmpty()){
						bw.write(0+","+0+","+0+","+0+",");
					}else{
						String[] regular_lda = values[c].trim().split("#");					
						double[] metrics = this.falsePositive(regular_lda,label_lda);
						bw.write(regular_lda.length+","+
								metrics[0]+","+
								new DecimalFormat("##.###").format(metrics[1])+","+
								new DecimalFormat("##.###").format(metrics[2])+",");
					}
					
				}	
				bw.write("\n");
				bw.flush();
			}
			bw.close();
			System.out.println("please check the output file : "+outFile);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				if(br!=null)
					br.close();

				if(bw != null)
					bw.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	
	
	/*
	 *  @deprecated
	 * get the false positive for regular LDA
	 * false positive: by topics from label LDA, and cacluate all the bugs including this topics in regular LDA,
	 * formula: not bugs in this topics/ all bugs predicted 
	 */
	private double[] falsePositive(String[] regular,String[] label){
		ArrayList<String> regular_bugs = null;
		ArrayList<String> labeled_bugs = null;		
		try{
			if(regular ==null || label == null)
				throw new Exception("regular bugs string or label bugs string is null");
			regular_bugs = this.toArrayList(regular);
			labeled_bugs = this.toArrayList(label);
			// total bugs which is recognized by regular lda
			int total = regular_bugs.size();
			regular_bugs.removeAll(labeled_bugs);	
			int intersection = total - regular_bugs.size();			
			return new double[] {intersection,(((regular_bugs.size())*1.0) / (total*1.0)),((intersection*1.0)/(total*1.0))};	
			
		}catch(Exception e){
			e.printStackTrace();
		}  
		
		return new double[]{-1,-1};
	}
	
}
