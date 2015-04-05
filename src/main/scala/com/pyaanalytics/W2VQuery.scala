/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.pyaanalytics

import org.apache.spark.mllib.feature.Word2Vec
import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf
import org.apache.spark.rdd.RDD

import scopt.OptionParser
import java.io.{ObjectOutputStream, FileOutputStream}
import scala.io.Source
import scala.xml._

object W2VQuery {

  case class W2VQueryConfig(abstractsFile: String = "",
                              modelFile: String = "",
                              sparkMaster: String = "local[64]")

  def main(args: Array[String]): Unit = {

    val parser = new OptionParser[W2VQueryConfig]("W2VQuery") {

      arg[String]("abstractsFile") valueName("abstractsFile") action {
        (x, c) => c.copy(abstractsFile = x)
      }


      arg[String]("modelFile") valueName("modelFile") action {
        (x, c) => c.copy(modelFile = x)
      }

      arg[String]("sparkMaster") valueName("sparkMaster") action {
        (x, c) => c.copy(sparkMaster = x)
      }
    }

    parser.parse(args, W2VQueryConfig()) match {
      case Some(config) => {
        run(config)
      } case None => {
        System.exit(1)
      }
    }
  }

  def run(config: W2VQueryConfig): Unit = {
    val sparkConf = new SparkConf()
      .setAppName("W2VQuery")
      .setMaster(config.sparkMaster)
      .set("spark.executor.memory", "10g")

    val sc = new SparkContext(sparkConf)

    val lines = sc.textFile(config.abstractsFile)
      .map(_.split("\\s+").toSeq)

    val w2v = new Word2Vec()
      .setVectorSize(800)

    val w2vModel = w2v.fit(lines)

    val oStream = new ObjectOutputStream(new FileOutputStream(config.modelFile))
    oStream.writeObject(w2vModel)

    val synonyms = w2vModel.findSynonyms("patient", 20)

    synonyms map {x => println(x.toString)}

    sc.stop()
  }
}
