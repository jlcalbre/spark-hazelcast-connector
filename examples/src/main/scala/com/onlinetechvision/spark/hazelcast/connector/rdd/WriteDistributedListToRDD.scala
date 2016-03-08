package com.onlinetechvision.spark.hazelcast.connector.rdd

import java.util.Properties

import com.hazelcast.config.ClasspathXmlConfig
import com.hazelcast.core._
import com.onlinetechvision.spark.hazelcast.connector.{User, DistributedObjectType}
import com.onlinetechvision.spark.hazelcast.connector.config.SparkHazelcastConfig._
import org.apache.spark.{SparkConf, SparkContext}

/**
  * Created by eren.avsarogullari on 3/8/16.
  */
object WriteDistributedListToRDD {

  def main(args: Array[String]) {

    val HazelcastXMLFileName = "hazelcast_config.xml"
    val HazelcastDistributedListName = "test_distributed_list"

    // Spark Context is created...
    val sc = new SparkContext(new SparkConf().setAppName("spark-hazelcast").setMaster("local"))

    // Distributed List is created with the content...
    val hzInstance = Hazelcast.getOrCreateHazelcastInstance(new ClasspathXmlConfig(HazelcastXMLFileName))
    val distributedList: IList[User] = hzInstance.getList(HazelcastDistributedListName)
    (1 to 100).foreach(index => distributedList.add(new User(index, s"name$index", s"surname$index")))

    // Spark Hazelcast properties are created...
    val sparkHazelcastProperties = new Properties()
    sparkHazelcastProperties.put(HazelcastXMLConfigFileName, HazelcastXMLFileName)
    sparkHazelcastProperties.put(HazelcastDistributedObjectName, HazelcastDistributedListName)
    sparkHazelcastProperties.put(HazelcastDistributedObjectType, DistributedObjectType.IList)

    // Distributed List is written to Spark as a RDD...
    val userRDD = new HazelcastItemRDD[User](sc, sparkHazelcastProperties)
    println(s"userRDD has got ${userRDD.count} elements.")

    // Prints elements of 'userRDD'
    userRDD.foreach(println)

  }

}
