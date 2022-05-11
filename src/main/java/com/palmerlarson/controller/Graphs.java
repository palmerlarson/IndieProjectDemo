package com.palmerlarson.controller;

import com.palmerlarson.entity.Tool;
import com.palmerlarson.entity.User;
import com.palmerlarson.persistence.ToolDao;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Class used as practice - will be used as main graph creation class
 */
@WebServlet(
        urlPatterns = {"/graphs"}
)
public class Graphs extends HttpServlet {
    private final Logger logger = LogManager.getLogger(this.getClass());

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session=req.getSession(false);
        ToolDao tDao = new ToolDao();
        List<Tool> toolList = tDao.getAll();

        resp.setContentType("text/html");

        ArrayList<String> strArr = new ArrayList<String>();
        for (Tool tLister: toolList) {
            User tUserId = tLister.getUser_id();
            User uObj = (User)session.getAttribute("currentUser");
            int tInt = tUserId.getId();
            int uInt = uObj.getId();
            if (tInt == uInt) {
                int tId = tLister.getTool_id();
                int wealth = tLister.getPositive_asset();
                int debt = tLister.getNegative_asset();
                strArr.add("<li id='" + tId
                        + "'><button id=\"btnStatus\" class=\"btn\" onclick=\"removeTool('" + tId + "')\">"
                        + "<i class=\"fa-solid fa-trash text-lg\"></i></button> Wealth: "
                        + wealth + "- Debt: " + debt
                        + "<button id=\"btnStatus\" class=\"btn\" onclick='generateChart(" + wealth + ","
                        + debt + ")'>Generate</button></li>");
            }
        }
        req.setAttribute("liArr", strArr);

        RequestDispatcher dispatcher = req.getRequestDispatcher("/graphs.jsp");
        dispatcher.forward(req, resp);
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(req.getInputStream()));
        JSONArray arr = new JSONArray(br.readLine());
        HttpSession session=req.getSession(false);
        User uObj = (User)session.getAttribute("currentUser");
        ToolDao tDao = new ToolDao();
        int totalDebt = 0;
        int totalWealth = 0;

        try {
            if (uObj.getGross_income() != 0) {
                int income = uObj.getGross_income();
                double mIncome = (double) income / 12;
                totalWealth += mIncome;
            }

            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                String type = obj.getString("type");
                String strAmount = obj.getString("amount");
                int amount = Integer.parseInt(strAmount);

                if (Objects.equals(type, "wealth")) {
                    totalWealth += amount;
                } else if (Objects.equals(type, "debt")) {
                    totalDebt += amount;
                }
            }

            Tool tool = new Tool(totalWealth, totalDebt, uObj);
            tDao.insert(tool);

        } catch (Exception e) {
            System.err.println(e.toString()); /* Throw exceptions to log files */
        }

        RequestDispatcher dispatcher = req.getRequestDispatcher("/graphs.jsp");
        dispatcher.forward(req, resp);
    }



}
