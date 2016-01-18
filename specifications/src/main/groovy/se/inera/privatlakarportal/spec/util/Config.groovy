package se.inera.privatlakarportal.spec.util

import fitnesse.slim.converters.ConverterRegistry
import fitnesse.slim.converters.StringConverter

class Config {

	String property
	String value
	
    Config() {
        ConverterRegistry.addConverter(String.class, new StringConverter());
    }

	void execute() {
		if (value && !value.contains("undefined variable:")) System.setProperty(property, value)
	}
}
