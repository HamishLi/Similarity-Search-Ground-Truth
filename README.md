#Image Similarity Ground Truth

##Configuration
###Database Configuration
There are two files that need to be configured for the database:
(1) Database configuration for Spring Boot.  
File path: MetricSpaceContext/src/main/resources/application.yml  
```
datasource:
    username: root
    password: root123
    url: jdbc:mysql://localhost:3306/similarity_search
    driver-class-name: com.mysql.jdbc.Driver
    type: com.alibaba.druid.pool.DruidDataSource
```
This should configure the Mysql username, password and URL.  

(2) Database configuration for MyBatis-generator.  
>*Please note that this configuration is only used to automatically generate Mapper based on the database using MyBatis-generator.  

File path: MetricSpaceContext/src/main/resources/generatorConfig.xml  

###MySQL database description
This project uses a database called similarity_search, and the table name is feedback. the description of this table is as follows:  
+-----------+-------------+------+-----+---------+----------------+  
| Field     | Type        | Null | Key | Default | Extra          |  
+-----------+-------------+------+-----+---------+----------------+  
| record_no | int         | NO   | PRI | NULL    | auto_increment |  
| image_id  | int         | YES  |     | NULL    |                |  
| result    | varchar(20) | YES  |     | NULL    |                |  
+-----------+-------------+------+-----+---------+----------------+  
The code for cearting this table is:  
```
create table feedback( 
record_no int primary key auto_increment, 
image_id int, 
result varchar(20)
);
```


##How to run this application
###When no changes are made to the code
(a) Run the main function in MetricSpaceContext/src/main/java/SpringBoot/MySpringBootApplication.java
```
 public static void main(String[] args) {
        SpringApplication application = new SpringApplication(MySpringBootApplication.class);
        application.run(args);
    }
```
Or, (b) in the MetricSpaceContext directory, use:
```cmd
java -jar target/MetricSpaceContext-0.0.1-SNAPSHOT.jar
```
###When something changed, especially Vue.js code
(a) in the MetricSpaceContext/src/frontend directory, use:
```cmd
npm run build
```
>*Note the first build may fail due to vue.js syntax warning.  

Then copy all the files in MetricSpaceContext/src/frontend/dist into
MetricSpaceContext/src/main/resources/static, and run the main function in MetricSpaceContext/src/main/java/SpringBoot/MySpringBootApplication.java.  

Or, (b) in the MetricSpaceContext directory, use:
```cmd
./mvnw clean package
java -jar target/MetricSpaceContext-0.0.1-SNAPSHOT.jar
```
>*Note the first build may fail due to vue.js syntax warning.  


##Introduction of project structure
####MetricSpaceContext/src/frontend
Store the Vue code:  
 + node_modules: project dependency modules loaded by npm  
 + public/index.html: home page entry file  
 + src: the development directory, which contains several directories and files:
>* assets: place some pictures, such as logos, etc
>* components: store small components
>* router/index.js: define the route of each page
>* views: store page-level components.
>* App.vue: project entry file
>* main.js: the core file of the project
 + package.json project configuration file   
 + README.md project description document, markdown format

####MetricSpaceContext/src/main/java/eu/similarity/msc
convex_transforms/GroundTruthWithTransform.java: generate the ground truth.  

####MetricSpaceContext/src/main/java/SpringBoot
Store Spring Boot code.

####MetricSpaceContext/src/main/resources
Store SpringBoot resources：  
 + mybatis:   
 >* mapper: store all the SQL code for DAO(Mapper)  
 >* mybatis-config.xml: MyBatis configuration file  
 + static: Store static resources of the website for Spring Boot  
 + application.yml: Spring Boot configuration file  
 + generatorConfig.xml: MyBatis-generator configuration file  

####MetricSpaceContext/src/test
Store all tests, especially JUint tests.
  
##Ground truth file
The transformed ground truth file is in 
