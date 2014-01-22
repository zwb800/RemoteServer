package com.web;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

public class FirstApplication extends Application {

	@Override
	public Set<Class<?>> getClasses() {
		// TODO Auto-generated method stub
		Set<Class<?>> sets = new HashSet<Class<?>>();
		sets.add(Serial.class);
		return sets;
	}
	
}
