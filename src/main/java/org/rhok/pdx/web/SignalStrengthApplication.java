package org.rhok.pdx.web;

import org.apache.log4j.Logger;
import org.restlet.Application;
import org.restlet.Component;
import org.restlet.Restlet;
import org.restlet.data.MediaType;
import org.restlet.data.Protocol;
import org.restlet.data.Reference;
import org.restlet.resource.Directory;
import org.restlet.routing.Router;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import java.io.File;

public class SignalStrengthApplication extends Application {
    private static Logger logger = Logger.getLogger(SignalStrengthApplication.class);

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

    private void attachHtml() {
        File file = new File("");
        String webDir = "file://" + file.getAbsolutePath() + "/src/main/web/";
        logger.info("serving html files under" + file.getAbsolutePath());
        Directory directory = new Directory(getContext(), webDir);
        directory.setListingAllowed(true);
        router.attach("/web/", directory);
    }

    public static void shutdown() throws Exception {
        component.stop();
    }


}
