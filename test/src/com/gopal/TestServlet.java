package com.gopal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.gopal.dao.PresetVO;
import com.portfolio.transaction.ReportGeneration;

public class TestServlet extends HttpServlet
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -109249589232537594L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException {
		try {
			
			/*String cosa = req.getParameter("cosa");
			System.out.println(cosa);
		    System.out.println("Query string : "+req.getQueryString());
		    String json="[\"choose a cosa\"]";
		    if(cosa!= null)
		    {
		    	if(cosa.equalsIgnoreCase("GC")) {
		    		System.out.println("Setting the GC");
		    	json = "[\"preset1\",\"preset2\"]";
		    	}
		    	else if(cosa.equalsIgnoreCase("ASC")) 
		    	{
		    		System.out.println("Setting the ASC");
		    		json= "[\"preset2\"]";
		    	}
		    }
				String preset = req.getParameter("preset");
				if(preset!= null) {
					PresetVO presetVO = new PresetVO();
					if(preset.equalsIgnoreCase("preset1")) {
						presetVO.setCosa(cosa);
						presetVO.setPreset("preset1");
						presetVO.setFrom("imco_doc");
						presetVO.setWhere("where id=1");
						presetVO.setSelect("r_object_id");
						
					}
					else if(preset.equalsIgnoreCase("preset2")) {
						presetVO.setCosa(cosa);
						presetVO.setPreset("preset2");
						presetVO.setFrom("asc_doc");
						presetVO.setWhere("where id=2");
						presetVO.setSelect("r_object_id,r_creation_date");
					}
					json=new Gson().toJson(presetVO);
				}
		System.out.println(json);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");*/
			String transactionFile = "C:/Documents/MultiTransaction_1.csv";
			String configurationFile = "C:/Documents/config.txt";
			String sumJsonLocation = "C:/Documents/summaryJson.json";
			boolean saveReport=true;
					
			String json = ReportGeneration.generateReport(transactionFile,configurationFile, sumJsonLocation, saveReport);
			response.getWriter().write(json);
			response.setStatus(200);
		
} catch (Exception e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
	response.setStatus(500);
	response.getWriter().write(e.getMessage());
} 
	finally {
		System.out.println("I am frustrated");
	}
	}
		
		
	

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		System.out.println(req.getQueryString());
		
		try {
			String responsetxt = req.getParameter("cosa") + req.getParameter("preset")+req.getParameter("select")+req.getParameter("from")+ req.getParameter("where");
			System.out.println(responsetxt);
			resp.setContentType("application/plain");
			resp.setCharacterEncoding("UTF-8");
			resp.getWriter().write(responsetxt);
		} catch (Exception e) {
			throw e;
		}
		
	}
	
	
	
}