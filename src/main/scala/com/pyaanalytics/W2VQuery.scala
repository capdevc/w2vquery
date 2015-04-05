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

import org.apache.spark.mllib.feature._

import scopt.OptionParser
import java.io.{ObjectInputStream, FileInputStream}

object W2VQuery {

  case class W2VQueryConfig(queryString: String = "",
                            modelFile: String = "")

  def main(args: Array[String]): Unit = {

    val parser = new OptionParser[W2VQueryConfig]("W2VQuery") {

      arg[String]("queryString") valueName("queryString") action {
        (x, c) => c.copy(queryString = x)
      }


      arg[String]("modelFile") valueName("modelFile") action {
        (x, c) => c.copy(modelFile = x)
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

    val iStream = new ObjectInputStream(new FileInputStream(config.modelFile))
    val w2vModel = iStream.readObject().asInstanceOf[Word2VecModel]

    val synonyms = w2vModel.findSynonyms(config.queryString, 20)

    synonyms map {x => println(x.toString)}
  }
}
