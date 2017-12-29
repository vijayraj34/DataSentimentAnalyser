name := "DataSentimentAnalyser"

version := "0.1"

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  "org.apache.spark" % "spark-streaming-kafka-0-8_2.11" % "2.1.1",
  "org.apache.spark" % "spark-core_2.11" % "2.1.0",
  "org.apache.spark" % "spark-streaming_2.11" % "2.1.0",
  "edu.stanford.nlp" % "stanford-corenlp" % "3.8.0" artifacts (Artifact("stanford-corenlp", "models"), Artifact("stanford-corenlp")),
  "com.googlecode.efficient-java-matrix-library" % "ejml" % "0.23"
)