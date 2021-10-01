package ua.com.pedpresa;

import ua.com.pedpresa.service.ServiceSite;
import ua.com.pedpresa.service.ServiceText;
import ua.com.pedpresa.src.Props;

import java.io.IOException;

public class PedpresaApp {


    public static void main(String[] args) throws IOException {

        Props.getProperties();
//        ServiceSite.siteReadService();
        ServiceText.textExecute();
    }
}
