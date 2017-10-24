package SparkExample

object Entirety extends App {

import org.apache.spark.SparkContext
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql._
import org.apache.spark._
import org.apache.spark.sql.functions._

//enable various commented out config options for hadoop clusters with hive 

val spark = SparkSession.builder()
//  .master("yarn-client")
  .master("local")
  .appName("my-spark-app")
 // .config("spark.warehouse.dir", "hdfs//your_node:your_hdfs_port/apps/hive/warehouse")
//  .config("spark.sql.warehouse.dir", "hdfs//your_node:your_hdfs_port/apps/hive/warehouse")
//  .config("spark.sql.hive.metastore.version", "1.2.1")
//  .config("spark.sql.hive.metastore.jars", "/builtin")
//  .config("spark.sql.hive.metastore.jars", "/path/to/your/hive/lib") 
//  .config("spark.yarn.archive", "hdfs//your_node:your_hdfs_port/apps/spark2/spark2-hdp-yarn-archive.tar.gz")
//  .config("spark.yarn.stagingDir", "hdfs//your_node:your_hdfs_port/tmp/")
//  .config("spark.driver.extraLibraryPath", "/yourhadoop/lib/paths")
  .config("spark.dynamicAllocation.enabled", "true")
  .config("spark.dynamicAllocation.initialExecutors", "0")
  .config("spark.dynamicAllocation.maxExecutors", "20")
  .config("spark.dynamicAllocation.minExecutors", "0")
//  .config("spark.executor.extraLibraryPath", "/yourhadoop/lib/paths")
//  .config("spark.executor.id", "driver")
// add for hdp hortonworks //.config("spark.driver.extraJavaOptions", "-Dhdp.version=2.6.0.3-8") 
// add for hdp hortonworks //.config("spark.yarn.am.extraJavaOptions", "-Dhdp.version=2.6.0.3-8")
//  .config("spark.home", "/path/to/your/spark/home/spark2")
//  .config("spark.jars", "/usr/hdp/current/spark2-client/jars")
//  .config("spark.submit.deployMode", "client")
//  .config("spark.shuffle.service.enabled", "true")
//  .config("spark.yarn.queue", "default")
//  .config("spark.hadoop.yarn.resourcemanager.address", "local:8050")
//  .config("hive.metastore.warehouse.dir", "hdfs//your_node:your_hdfs_port/apps/hive/warehouse")
//.enableHiveSupport()
  .getOrCreate()



//test for hive table support spark.sql("show tables").show


     //create a Dataset using spark.range starting from 5 to 100, with increments of 5
val numDs = spark.range(5, 100, 5)
     // reverse the order and display first 5 items
numDs.orderBy(desc("id")).show(5)
    //compute descriptive stats and display them
numDs.describe().show()
    // create a DataFrame using spark.createDataFrame from a List or Seq
val langPercentDF = spark.createDataFrame(List(("Scala", 35), ("Python", 30), ("R", 15), ("Java", 20)))
    //rename the columns
val lpDF = langPercentDF.withColumnRenamed("_1", "language").withColumnRenamed("_2", "percent")
   //order the DataFrame in descending order of percentage
lpDF.orderBy(desc("percent")).show(false)



}
