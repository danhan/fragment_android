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

val source = CSVFile("moto.csv") ~> IDColumn(1);

val tokenizer = {
  SimpleEnglishTokenizer() ~>            // tokenize on space and punctuation
  CaseFolder() ~>                        // lowercase everything
  WordsAndNumbersOnlyFilter() ~>         // ignore non-words and non-numbers
  MinimumLengthFilter(3) ~>                 // take terms with >=3 characters
  StopWordFilter("en")                    // remove standard english stop words
}

val text = {
  source ~>                              // read from the source file
  Columns(4,5) ~>                         // select column containing text
  Join(" ") ~>
  TokenizeWith(tokenizer) ~>             // tokenize with tokenizer above
  TermCounter() ~>                       // collect counts (needed below)
  TermMinimumDocumentCountFilter(4) ~>   // filter terms in <4 docs
  TermStopListFilter(List("err","failed","1015","new",">eq>eq>e",
  "rfcomm","attempts","case","getting","load","blur","1px","turn",
  "error","having","q'eunable","able","whilst","mins","itq'es","q\"e",
  "?q\"e","donq'et","know","just","phone","phones","like","happens",
  "bought","[heap]","00000000","system/lib/libwebcore.so",
  "0000000000000000","00000001","pid","page","sigsegv",
  "3ff0000000000000","died","youq'ere","need","needs","add",
  "select","did","q\"ecall","attempted","e/androidruntime",
  "d/dalvikvm","i/activitymanager","_explicit","cmp","came",
  "randomly","used","live","q&e","usually","takes","exactly","thanks",
  "itq'es","want","selection","selecting","expected","q\"eusb","jack",
  "tab","@apa26","esd56","ese81","511","appear","doesnq'et",
  "q\"esound","make","didnq'et","tried","api","deaf","alt","q\"eall",
  "dayq\"e","fine","clear","manager","usefull","person","verizon",
  "enhancement","main","click","iq'em","iq'eve","old","really",
  "looking","09-09","system/lib/libc.so","system/lib/libdvm.so",
  "android.os.handler.dispatchmessage(handler.java","239",
  "java.lang.reflect.method.invoke(method.java",
  "com.google.android.talk","exception","uncaught",":99)",
  "anr","java.lang.reflect.method.invokenative(native",
  "donq'et","q\"eadb","visit","bugreportq\"e","include",
  "form","forget","black","donq'et","iq'em","doesnq'et",
  "didnq'et","second","first","canq'et","wonq'et","turns",
  "enter","software","previous","option","donq'et","unable","says",
  "zimbra","java.lang.thread.run(thread.java","different",
  "additional","end","speakerphone","december","11-26","entered",
  "androidruntime","adding","contactsq\"e","samsung","particular",
  "instead","corporate","users","itq'es","iq'em","m4a","callq'e",
  "astro","able","iq'eve","got","started","related","try",
  "doesnq'et","allow","actual","phone","motorola","problem",
  "using","user","users","does","use","277","a2dp","files","think",
  "sure","donq'et","does","tested","works","running","issue","having",
  "able","trying","changing","results","successfully","method",
  "android.os.looper.loop(looper.java",
  "android.app.activitythread.main(activitythread.java",
  "long","bug","fix","work","way","know","include","think",
  "output","mention","correct","00000001","win","steps",
  "reproduce","bytes","test","caused","a2dp","public","private",
  "void","import","freed","objects","ignoring","reason","://groups.google.com/group/android-discuss", "android","happen","later","gone","null","=false}","established","enable","debug","death","false","intent","project","example","actually","say","appears","original","seeing","com.android.vending","monkey","forever","ago","occurs","properly",
"available","given","intent","previously","code","final","run","happened","development","environment","final",
"iq'ed","ago","cause","5195","twice","log","lot","100","incorrect","correctly")) ~>
  //TermDynamicStopListFilter(30) ~>       // filter out 30 most common terms
  //TermStopListFilter(List("when","not","data","using","the","get","only","after","desire",               "with","from","one","problem","stopped","issues","and","unable","user","over","for","latest","different","does","work","added","are","but","new",                "when","all","error","have","fails","cannot","default","while","wrong","off","causes","than","that","missing","just","canq'et","doesnq'et")) ~>
  DocumentMinimumLengthFilter(5)         // take only docs with >=5 terms
}

// turn the text into a dataset ready to be used with LDA
val dataset = LDADataset(text);

// define the model parameters
val params = LDAModelParams(numTopics = 30, dataset = dataset,
  topicSmoothing = 0.01, termSmoothing = 0.01);

// Name of the output model folder to generate
val modelPath = file("lda-"+dataset.signature+"-"+params.signature);

// Trains the model: the model (and intermediate models) are written to the
// output folder.  If a partially trained model with the same dataset and
// parameters exists in that folder, training will be resumed.
TrainCVB0LDA(params, dataset, output=modelPath, maxIterations=2500);

// To use the Gibbs sampler for inference, instead use
// TrainGibbsLDA(params, dataset, output=modelPath, maxIterations=3000);

