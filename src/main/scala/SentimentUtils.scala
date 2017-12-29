import java.util.Properties

import edu.stanford.nlp.ling.CoreAnnotations
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations
import edu.stanford.nlp.pipeline.StanfordCoreNLP
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations

import scala.collection.JavaConversions._
import scala.collection.mutable.ListBuffer

object SentimentUtils {
  val nlpPropts = {
    val propts = new Properties()
    /* Annotators - Meaning http://corenlp.run/
       tokenize   - Tokenize the sentence.
       ssplit     - Split the text into sentence. Identify fullstop, exclamation etc and split sentences
       pos        - Reads text in some language and assigns parts of speech to each word (and other token), such as noun, verb, adjective, etc.
       lemma      - Group together the different inflected forms of a word so they can be analysed as a single item.
       parse      - Provide syntactic analysis http://nlp.stanford.edu:8080/parser/index.jsp
       sentiment  - Provide model for sentiment analysis
       * */
    propts.setProperty("annotators", "tokenize, ssplit, pos, lemma, parse, sentiment")
    propts
  }

  def detectSentiment(message: String): String = {

    // Create a pipeline with NLP properties
    val pipeline = new StanfordCoreNLP(nlpPropts)

    // Run message through the Pipeline
    val annotation = pipeline.process(message)
    var sentiments: ListBuffer[Double] = ListBuffer()
    var sizes: ListBuffer[Int] = ListBuffer()

    var longest = 0
    var mainSentiment = 0

    // An Annotation is a Map and you can get and use the various analyses individually.
    // For instance, this gets the parse tree of the first sentence in the text.
    // Iterate through text
    for (textMsg <- annotation.get(classOf[CoreAnnotations.SentencesAnnotation])) {
      // Create a RNN parse tree
      val parseTree = textMsg.get(classOf[SentimentCoreAnnotations.SentimentAnnotatedTree])
      // Detect Sentiment
      val textSentiment = RNNCoreAnnotations.getPredictedClass(parseTree)
      val partText = textMsg.toString

      if (partText.length() > longest) {
        mainSentiment = textSentiment
        longest = partText.length()
      }

      sentiments += textSentiment.toDouble
      sizes += partText.length
    }

    val weightedSentiments = (sentiments, sizes).zipped.map((sentiment, size) => sentiment * size)
    var weightedSentiment = weightedSentiments.sum / (sizes.fold(0)(_ + _))

    if (weightedSentiment <= 0.0)
      "NOT_UNDERSTOOD"
    else if (weightedSentiment < 1.6)
      "NEGATIVE"
    else if (weightedSentiment <= 2.0)
      "NEUTRAL"
    else if (weightedSentiment < 5.0)
      "POSITIVE"
    else "NOT_UNDERSTOOD"

  }
}
