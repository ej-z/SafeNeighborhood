package com.semanticweb.group2;

public class TypeData {

	public String Type;
	public int IsZipcode;
	public String[] Categories;
	
	public TypeData(String type, int isZipcode, String[] categories) {
		Type = type;
		Categories = categories.clone();
		IsZipcode = isZipcode;
	}
	
}
