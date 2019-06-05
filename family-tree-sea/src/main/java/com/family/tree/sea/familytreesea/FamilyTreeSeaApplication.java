package com.family.tree.sea.familytreesea;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.io.IOException;

@SpringBootApplication
public class FamilyTreeSeaApplication {

	private static final Logger LOG = LoggerFactory.getLogger(FamilyTreeSeaApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(FamilyTreeSeaApplication.class, args);
		openHomePage();
	}

	private static void openHomePage() {
		String url = "http://localhost:8865";
		Runtime rt = Runtime.getRuntime();
		String os = System.getProperty("os.name").toLowerCase();


		if(os.indexOf("win") >= 0){
			// is windows
			try {
				rt.exec("rundll32 url.dll,FileProtocolHandler " + url);
			} catch (IOException e) {
				LOG.error("Failed to launch browser on windows:"+e.getMessage());
			}
		}else if(os.indexOf("mac") >= 0){
			// is mac
			try {
				rt.exec("open " + url);
			} catch (IOException e) {
				LOG.error("Failed to launch browser on mac"+e.getMessage());
			}
		}else if(os.indexOf("nix") >=0 || os.indexOf("nux") >=0){
			// is linux
			String[] browsers = { "epiphany", "firefox", "mozilla", "konqueror",
					"netscape", "opera", "links", "lynx" };

			StringBuffer cmd = new StringBuffer();
			for (int i = 0; i < browsers.length; i++){
				if(i == 0){
					cmd.append(String.format(    "%s \"%s\"", browsers[i], url));
				}
				else{
					cmd.append(String.format(" || %s \"%s\"", browsers[i], url));
				}
			}
			try {
				rt.exec(new String[] { "sh", "-c", cmd.toString() });
			} catch (IOException e) {
				LOG.error("Failed to launch browser on linux"+e.getMessage());
			}
		}else{
			LOG.error("Unknown system platform");
		}
	}
}
