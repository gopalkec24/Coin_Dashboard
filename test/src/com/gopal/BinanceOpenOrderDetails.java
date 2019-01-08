package com.gopal;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.trade.BinanceTrade;

public class BinanceOpenOrderDetails extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		BinanceTrade trade = new BinanceTrade();	
		String json = trade.getOrderDetails(null);
		resp.getWriter().write(json);
		resp.setStatus(200);
	}

}
