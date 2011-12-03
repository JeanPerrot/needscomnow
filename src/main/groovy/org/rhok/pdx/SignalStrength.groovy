package org.rhok.pdx;

import graffiti.*

public class SignalStrength {

    @Get('/helloworld')
    def hello() {
        'Hello World With graffiti and all '
    }

    public static void main(String[] args) throws Exception {
        Graffiti.config['port'] = 8111
        Graffiti.serve this
        Graffiti.start()

    }


}
