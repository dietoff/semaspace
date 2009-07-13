package data;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;

public class SemaPropertyFiles {

	public void load() throws IOException {
		Properties props = new Properties();
		URL url = ClassLoader.getSystemResource("myprops.props");
		props.load(url.openStream());
	}
	
}


/*
public class LoadProps {

public static void main(String args[]) throws Exception{
   h.doit();
   h.doitagain();
   }
public void doit() throws Exception{
   // properties in the classpath
   java.util.Properties props = new java.util.Properties();
   java.net.URL url = ClassLoader.getSystemResource("myprops.props");
   props.load(url.openStream());
   System.out.println(props);
  }
public void doitagain() throws Exception{
   // properties in the startup directory
   java.util.Properties props = new java.util.Properties();
   String path = getClass().getProtectionDomain().getCodeSource().
                 getLocation().toString().substring(6);
   java.io.FileInputStream fis = new java.io.FileInputStream
      (new java.io.File( path + "\\myprops.props"));
   props.load(fis);
   System.out.println(props);
  }
}
*/