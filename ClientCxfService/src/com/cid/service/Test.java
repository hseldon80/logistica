package com.cid.service;

public class Test {

	
	public Test() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String args[]) throws Exception {
		Client_Service service = new Client_Service();
		Model m = service.getCxfServicePort().getLista("");       		
        for (String string : m.getLista()) {
			System.out.println(string);
		}
        
        System.out.println(service.getCxfServicePort().getNello(""));
        

    }
	
}
