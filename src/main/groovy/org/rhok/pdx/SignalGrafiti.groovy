package org.rhok.pdx

import graffiti.Get
import graffiti.Graffiti

class SignalGrafiti {


    def serve() {
        Graffiti.config['port'] = getPort()
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

    private Integer getPort() {
        Integer port = null;
        try {
            port = Integer.valueOf(System.getenv("PORT"));
        } catch (Exception e) {
            port = 8881;
        }
        return port;
    }
}