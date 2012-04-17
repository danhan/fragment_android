// Stanford TMT Example 2 - Learning an LDA model
// http://nlp.stanford.edu/software/tmt/0.4/

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
  TermMinimumDocumentCountFilter(2)~>    // filter terms in <2 docs
  //DocumentMinimumLengthFilter(5) ~>       // take only docs with >=5 terms
  TermStopListFilter(List("android","htc","phone","phones","problem","wonq'et","2.29.405.5","q\"einviteeq\"e","iq'ed","way","does","0000000000000000","00000000","00000007","4096","think","like","147","comes","just","q\"e","tried","donq'et","iq'em","iq'eve","a2dp","able","=q>e","itq'es","went","said","based","doesnq'et","?q\"e","canq'et","doesnt","wont","easy","dont","come","example","try","use","using","q\"ethere","124","132","happen","happens","d/dalvikvm","know","[2]","q\"elow","get","got","a2dp","q&e","0000","went","way","htc","results","result","use","need",":00","pid","e/androidruntime","i/activitymanager","d/dalvikvm","iq'eve","donq'et","tried","i/debug","works","system/lib/libwebcore.so","0000000000000000","00000000","itq'es","eas","donq'et","sure","q\"e",">eq>eq>e","like","youq'ere","iq'eve","@and18-2","00000007","afraid","think","4096","147","comes","a2dp","=q>e","said","maybe","q\"eadb","want",
  "i/windowmanager","act","applications","death","current","company","policies","apps","application","public","import","void","private","int","data","class","[heap]","goes","steps","really","thing","hello","files","ogg","://groups.google.com/group/android-discuss","running","visit","order","available","currently","level","correct","include","bugs","mention","features","features","new","api","apis","showing","code","developer","sample","different","problems","work","help",
"null","appropriate","new","err","raw","intent","total","starting","cmp","freed","method","items","start","com.android.settings","exiting","did","today","exactly","didnq'et","doing","took","look","test","following","baseband",
"including","init","observed","pair","possible","thanks","best","good","callback","final","fine","incorrect","happened",
"interface","correctly","bug","unable","issue","previous","primary","used","wrong","error","unknown","errors","fix",
"failure","release-keys","info","instead","missing","unexpectedly",":false","provide","shows","fail","says","trying","makes","unable",
"wrong","bugreportq\"e","annoying","stays","q\"eworkq\"e","bytes","objects","failed","let","impossible","=false}","desire","great"))
  //TermDynamicStopListFilter(30) ~>       // filter out 30 most common terms
}

// turn the text into a dataset ready to be used with LDA
val dataset = LDADataset(text);

// define the model parameters
val params = LDAModelParams(numTopics = 35, dataset = dataset,
  topicSmoothing = 0.01, termSmoothing = 0.01);

// Name of the output model folder to generate
val modelPath = file("lda-"+dataset.signature+"-"+params.signature);

// Trains the model: the model (and intermediate models) are written to the
// output folder.  If a partially trained model with the same dataset and
// parameters exists in that folder, training will be resumed.
TrainCVB0LDA(params, dataset, output=modelPath, maxIterations=2500);

// To use the Gibbs sampler for inference, instead use
// TrainGibbsLDA(params, dataset, output=modelPath, maxIterations=1500);

