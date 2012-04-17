// Stanford TMT Example 6 - Training a LabeledLDA model
// http://nlp.stanford.edu/software/tmt/0.3/

// tells Scala where to find the TMT classes
import scalanlp.io._;
import scalanlp.stage._;
import scalanlp.stage.text._;
import scalanlp.text.tokenize._;
import scalanlp.pipes.Pipes.global._;

import edu.stanford.nlp.tmt.stage._;
import edu.stanford.nlp.tmt.model.lda._;
import edu.stanford.nlp.tmt.model.llda._;

val source = CSVFile("HTC.csv") ~> IDColumn(1);

val tokenizer = {
  SimpleEnglishTokenizer() ~>            // tokenize on space and punctuation
  CaseFolder() ~>                        // lowercase everything
  WordsAndNumbersOnlyFilter() ~>         // ignore non-words and non-numbers
  MinimumLengthFilter(3) ~>               // take terms with >=3 characters
  StopWordFilter("en")                    // remove standard english stop words
}


val text = {
  source ~>                              
  Columns(4,5) ~>                           
  Join(" ") ~>
  TokenizeWith(tokenizer) ~>             // tokenize with tokenizer above
  TermCounter() ~>                       // collect counts (needed below)
  TermMinimumDocumentCountFilter(2) ~>   // filter terms in <2 docs
  //DocumentMinimumLengthFilter(5) ~>       // take only docs with >=5 terms
  TermStopListFilter(List("android","htc","phone","phones","problem","way","does","0000000000000000","00000000","00000007","4096","think","like","147","comes","just","q\"e","tried","donq'et","iq'em","iq'eve","a2dp","able","=q>e","itq'es","went","said","based","doesnq'et","?q\"e","canq'et","doesnt","wont","easy","dont","come","2003","2010","example","try","use","using","q\"ethere","124","132","happen","happens","d/dalvikvm","know","[2]","q\"elow","get","got","a2dp","q&e"))
  //TermDynamicStopListFilter(30) ~>       // filter out 30 most common terms
}

// define fields from the dataset we are going to slice against
val labels = {
  source ~>                              // read from the source file
  Column(2) ~>                           // take column two, the year
  TokenizeWith(WhitespaceTokenizer()) ~> // turns label field into an array
  TermCounter() ~>                       // collect label counts
  TermMinimumDocumentCountFilter(1)     // filter labels in < 10 docs
}

val dataset = LabeledLDADataset(text, labels);

// define the model parameters
val modelParams = LabeledLDAModelParams(dataset=dataset);

// Name of the output model folder to generate
val modelPath = file("llda-cvb0-"+dataset.signature+"-"+modelParams.signature);
//val modelPath = file("llda-gibbs-"+dataset.signature+"-"+modelParams.signature);


// Trains the model, writing to the given output path
TrainCVB0LabeledLDA(modelParams, dataset, output = modelPath, maxIterations = 2500);