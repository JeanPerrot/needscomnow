package org.rhok.pdx

import graffiti.Get
import graffiti.Graffiti

class SignalGrafiti {


    def serve() {
        Graffiti.config['port'] = 8111
        Graffiti.root "./src/main/resources/"
        Graffiti.serve '*.html'
        Graffiti.serve this
        Graffiti.start()
    }


    @Get('/test')
    def runTest() {
        "hello, world"
    }

    public static void main(String[] args) {
        new SignalGrafiti().serve()
    }
}