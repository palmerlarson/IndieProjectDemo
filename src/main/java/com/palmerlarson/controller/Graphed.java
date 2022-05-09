package com.palmerlarson.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Class used as practice - will be used as main graph creation class
 */
@WebServlet(
        urlPatterns = {"/graphs"}
)
public class Graphed extends HttpServlet {
    private final Logger logger = LogManager.getLogger(this.getClass());

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        InfoPage.pullName(req, resp, logger);


        RequestDispatcher dispatcher = req.getRequestDispatcher("/graphs.jsp");
        dispatcher.forward(req, resp);
    }



}
