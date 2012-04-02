package sample.csv.file;

public class MotoLabelling extends LabelingBugs{

	
	public MotoLabelling(String homeFolder){
		super(homeFolder);	
	}

	/**
	 * @param args
	 */
	public static void main(String[] args){
		
		String homeFolder = "./data/moto";	
		HTCLabeling htc = new HTCLabeling(homeFolder);
		double[] cases = new double[]{0.01, 0.05, 0.1, 0.2, 0.3, 0.4, 0.5};
		htc.AddLabels4Regular(30,cases,false);
		htc.preprocess4LabelledLDA(false);
		htc.getJaccard(cases.length);
		htc.categorizeBugsByTopics(cases.length);
	}

}
