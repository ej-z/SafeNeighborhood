package com.semanticweb.group2;

public class TypeData {

	public String Type;
	public String[] Categories;
	
	public TypeData(String type, String[] categories) {
		Type = type;
		Categories = categories.clone();
	}
	
}
