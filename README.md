### spark-tutorial_2 collects small projects that I worked on using spark scala 2 Dataset/ DataFrame.  Those were first inpired by open source articles from DataBrick community, LinkedIn 'Apache Spark' User Group.  I instilled my idea while rewriting. 
#### The topics include:
    1. A complete movie recommendation System using Spark-ML ALS (Alternating Least Square) algorithm 
       a) Use original format of MovieLens 1M Dataset (https://grouplens.org/datasets/movielens/1m/)
       b) Write python scripts to generate my PersonalRating data
       c) Parse data into Rating and Movie case classes
       d) Adopt the strategy learned from edx.org BerkeleyX course "CS120x: Distrubuted Machine Learning with Apache 
          Spark": split data into training, validation and test dataset.  Use the best model evaluated based upon 
          validation dataset and apply it to test dataset
       e) Use ParamGridBuilder to faciliate GridSearch to find best model
       f) Compare RMSE from the prediction of best model with the baseline's one which use average rating as the 
          prediction
       g) Augment the model with all dataset and perform transform on unrated movies
       h) Unrated dataset was first generated by excluding movieId of PersonRating from all MovieRatings then join with 
          Movie. Improve performance by using Movies dataset directly to avoid expensive join plus distinct function.
          The first approach is reflected in recommend.log and the second approach is reflect in the recommend2.log.
          The second approach definitely perform better.
          
       i) Avoid NaN pitfall by excluding NaN prediction data from the result from transformation.  
          See MovieLensALS.scala for the details of NaN pitfall. 
       j) The remaining issue: accented characters in title of movies like è in La Vita è bella is lost in Spark 
          DataFrame operation. Research on org.apache.spark.sql.Encoder might be needed.
 
    2. Similar movie recommendation system but the source is data in MongoDB
       a) Write simple convert.py to covert the delimiter from '::' to ',' so that I can use mongoimport to import 
          Ratings and Movies data
       b) Use MongoSpark and ReadConfig of mongo-spark-connector 2.0 to load Mongodb data
       c) Perform similar steps as the above ML-1M recommendation system.
       d) Save recommendations to mongo

    3. Analyze Apache access log
       a) Write AccessLogParser using RegEx pattern to parse data into AccessLogRecord 
       b) Write parseDate UDF to parse access log date format to be compatible with timestamp type 
       c) Load diamond.csv using new SparkSession.read.csv with correct options 
       d) DataFrame Join of access log data with diamond.csv
    
    4. DanubeStatesAnalysis,DanubeStatesAnalysis2
       A task for Rovi to ensure the java-transform system is compatible with Pri-java-transform system by analyzing 
       transformer.log entries, including parsing, filter data by pubId and grouping.
       a) DanubeStatesAnalysis has separate non java-transform Dataset and java-transform Dataset and group them and 
          generate reports separately.  However, it's difficult to compare separate reports grouped by resources since
          there are too many resources.
       b) DanubeStatesAnalysis2 improves on top of DanubeStatesAnalysis. It parses non java-transform and java-transform 
          into a common obeject: DanubeStates which has additional numeric fields jtNo and jtYes of value 0 or 1.   
          In this way, I can generate report of sum(jtNo) and sum(jtYes) side by side grouped by publish state, 
          resource or both.
          
    5. DanubeResolverAnalysis
       A task for Rovi to ensure the java-transform system is compatible with Pri-java-transform system by analyzing 
       resolver.log entries.
       a) It adds "difference" display field by using format_string 
       b) It adds "diff. flag" field by using nested when and otheriwise sql functions on numeric conditions
    6. SparkSessionZipsExample to be familiar with Spark 2 Dataset/ DataFrame operation.
