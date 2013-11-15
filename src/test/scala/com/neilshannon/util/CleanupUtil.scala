package com.neilshannon.util

import java.io.File

object CleanupUtil {

  def cleanUp(){
    val neo4jdir = new File("/tmp/neo4j-store")
    neo4jdir.listFiles.foreach { f => deleteFile(f) }

    deleteFile(neo4jdir)
  }

  private def deleteFile(dfile : File) : Unit = {
    if(dfile.isDirectory){
      dfile.listFiles.foreach{ f => deleteFile(f) }
    }
    dfile.delete
  }

}