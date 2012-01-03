package org.rhok.pdx.web;

import org.restlet.Application;
import org.restlet.Component;
import org.restlet.Restlet;
import org.restlet.data.MediaType;
import org.restlet.data.Protocol;
import org.restlet.data.Reference;
import org.restlet.resource.Directory;
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
        component.getClients().add(Protocol.FILE);
        component.getDefaultHost().attachDefault(application);
        application.attachHtml();
        component.start();
    }

    private void attachHtml(){
        Directory directory = new Directory(getContext(), "file:///Users/jperrot/github/fun/rhok/needcomsnow/src/main/web/");
        directory.setListingAllowed(true);
        router.attach("/web2/", directory);
    }

    public static void shutdown() throws Exception {
        component.stop();
    }



}
