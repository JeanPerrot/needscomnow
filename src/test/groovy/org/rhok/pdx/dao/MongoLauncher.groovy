package org.rhok.pdx.dao


class MongoLauncher {
    def procId

    def launchMongo() {
        "/opt/local/bin/mongod".execute()

        //get the process id
        def text = 'ps -ef'.execute().text
        new StringReader(text).eachLine {line->
            if (!line.contains("/opt/local/bin/mongod")) return
            procId = line.split()[1]
        }
        println "started mongodb with pid $procId"
    }

    def stopMongo() {
        println "stopping $procId"
        "kill -2 $procId".execute()
    }

    public static void main(String[] args) {
        def launcher = new MongoLauncher()
        launcher.launchMongo()
        Thread.sleep(5000)
        launcher.stopMongo()
    }

}
