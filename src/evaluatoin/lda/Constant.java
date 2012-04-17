package evaluatoin.lda;

public class Constant {

	// one column in file showing topics from regular and label
	public static String REGULAR_TOPICS_FILE = "regular-topics.csv";
	public static String LABEL_TOPICS_FILE = "label-topics.csv";
	
	// distribution row: bugs, column: topics for regular and label
	public static String REGULAR_DISTRIBUTION_FILE = "regular-topic-distribute.csv";
	public static String LABEL_DISTRIBUTION_FILE = "label-topic-distribute.csv";
	
	// labeled bug files, row: bugs, column: time, bug number, bug description
	public static String LABEL_ENTIRE_FILE = "label-bug-topic-entire.csv";
	
	// mapping the bug and the bug opened time
	public static String BUT_TIME_FILE = "bug-time.csv";


	// output: mapping the bug and bug opened time which is formated to facilitate sorting
	public static String FORMATED_BUG_TIME_FILE = "formated-bug-time.csv";
		
	//output: relevance for labeled and regular lda
	public static String REGULAR_RELEVANCE_FILE = "regular-relevance.csv";
	public static String LABEL_RELEVANCE_FILE = "label-relevance.csv";
	
	
	
	public static int NUM_OF_BUG_HTC = 1304;
	public static int NUM_OF_BUG_MOTO = 985;
	
	
}
