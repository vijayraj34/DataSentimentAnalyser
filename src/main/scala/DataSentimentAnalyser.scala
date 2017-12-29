import org.apache.spark.SparkConf
import org.apache.spark.streaming.kafka.KafkaUtils

import org.apache.spark.streaming.{ Seconds, StreamingContext }
import SentimentUtils._

object DataSentimentAnalyser {
  def main(args :Array[String]): Unit ={
    val conf = new SparkConf().setAppName("Data Sentiment Analyser").setMaster("local[*]")
    val ssc = new StreamingContext(conf,org.apache.spark.streaming.Seconds(10))
    val lines = KafkaUtils.createStream(ssc,"nn01.itversity.com:2181,nn02.itversity.com:2181,rm01.itversity.com:2181","spark-group",Map("kafkaStream"->5))  //5 is no. of parallel threads running
    //lines.print()
    lines.map(rec => (rec._2,detectSentiment(rec._2))).print()

    ssc.start()

    ssc.awaitTermination()
  }
}
