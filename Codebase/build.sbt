import Gulp._
import play.PlayImport.PlayKeys.playRunHooks

name := "GA2SA"

version := "0.0.1"

resolvers ++= Seq(
    Resolver.url("Edulify Repository", url("http://edulify.github.io/modules/releases/"))(Resolver.ivyStylePatterns)
)

libraryDependencies ++= Seq(
  javaCore, 
  javaJdbc, 
  javaJpa.exclude("org.hibernate.javax.persistence", "hibernate-jpa-2.0-api"), 
  cache,
  // APPLICATION DEPENDENCIES
  "org.postgresql" 			% "postgresql"						 	% "9.3-1102-jdbc41",
  "com.google.api-client" 	% "google-api-client" 					% "1.19.1",
  "com.google.apis" 		% "google-api-services-analytics" 		% "v3-rev111-1.19.1",
  "com.google.apis" 		% "google-api-services-oauth2" 			% "v2-rev86-1.19.1",
  "org.hibernate" 			% "hibernate-entitymanager" 			% "4.3.8.Final",
  "org.hibernate" 			% "hibernate-validator" 				% "5.1.3.Final",
  "com.edulify" 			%% "play-hikaricp" 						% "2.0.4",
  "javax.el" 				% "javax.el-api" 						% "3.0.0",
  "org.glassfish.web" 		% "javax.el" 							% "2.2.6",
  "org.apache.openjpa" 		% "openjpa-persistence" 				% "2.3.0",
  "org.mindrot"				% "jbcrypt" 							% "0.3m"
)

lazy val root = (project in file(".")).enablePlugins(PlayJava, SbtWeb)

pipelineStages := Seq(gzip)

includeFilter in (Assets, LessKeys.less) := "index.less"

playRunHooks <+= baseDirectory.map(base => Gulp(base))

herokuAppName in Compile := "ga2sa"