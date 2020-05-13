package com.noob.servlet;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;

@WebServlet(urlPatterns = "/async", asyncSupported = true)
@Slf4j
public class AsyncServlet extends HttpServlet {

	ExecutorService executorService = Executors.newSingleThreadExecutor();

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		log.info("step into AsyncServlet");
		// 获取异步上下文
		final AsyncContext ctx = req.startAsync();
		ctx.getResponse().getWriter().print("async servlet");
		executorService.submit(()->{
			try {
				Thread.sleep(1000L);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
	
	}
}