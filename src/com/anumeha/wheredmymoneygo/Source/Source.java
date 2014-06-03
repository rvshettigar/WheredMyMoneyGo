package com.anumeha.wheredmymoneygo.Source;

public class Source {
	
	//class variables
	private int _id;
	private String _name;
	
	//empty constructor
	public Source(){
		
	}
	
	public Source(int id, String name)
	{
		this._id = id;
		this._name = name;
	}
	
	public Source(String name)
	{
		setName(name);
	}
	
	public void setName(String name) {
		this._name = name;		
	}
	public String getName() {
		
		return this._name;
	}

}
