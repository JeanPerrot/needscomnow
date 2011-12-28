package org.rhok.pdx.web;

import org.restlet.Application;
import org.restlet.Component;
import org.restlet.Restlet;
import org.restlet.data.MediaType;
import org.restlet.data.Protocol;
import org.restlet.routing.Router;
import org.springframework.context.support.FileSystemXmlApplicationContext;

public class SignalStrengthApplication extends Application {

    private Router router;
    private static Component component;

    @Override
    public synchronized Restlet createInboundRoot() {
        return router;
    }

    public void setRouter(Router router) {
        this.router = router;
    }

    public static void main(String[] args) throws Exception {
        FileSystemXmlApplicationContext applicationContext = new FileSystemXmlApplicationContext("classpath*:applicationContext.xml");


        SignalStrengthApplication application = (SignalStrengthApplication) applicationContext.getBean("signalStrengthApplication");
        application.getMetadataService().addExtension("kml", MediaType.APPLICATION_KML);
        component = new Component();
        component.getServers().add(Protocol.HTTP, PortUtil.getPort());
        component.getDefaultHost().attachDefault(application);
        component.start();
    }

    public static void shutdown() throws Exception {
        component.stop();
    }



}
