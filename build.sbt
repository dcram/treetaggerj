import Dependencies._
import SparklaneSbt._
import sbt._

import sbtdocker.DockerPlugin.autoImport.ImageName
import SparklaneSbt.dockerImageName

organization := "fr.univ-nantes.julestar"
version:= "1.1"

// runtime
libraryDependencies ++= uimaCore
libraryDependencies ++= uimaFit

// testing
libraryDependencies ++= javaTesting
libraryDependencies ++= uimaTest


