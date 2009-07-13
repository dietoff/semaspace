package semaGL;

import java.util.HashSet;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

public class Messages {
	private static final String BUNDLE_NAME = "messages"; //$NON-NLS-1$

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);
	
	private Messages() {
	}

	public static String getString(String key) {
		
		try {
			return RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}
	
	public static HashSet<String> getArray(String key) {
		boolean next = true;
		HashSet<String> result = new HashSet<String>();
		try {
			int c = 1;
			
			while (next) {
				result.add(RESOURCE_BUNDLE.getString(key+c));
				c++;
			}
			return result;
		} catch (MissingResourceException e) {
			next = false;
			return result;
		}
	}
}
