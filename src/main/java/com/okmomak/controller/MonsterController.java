package com.okmomak.controller;

import com.okmomak.entity.Monster;
import com.okmomak.service.MonsterService;
import com.okmomak.spring.annotation.Autowired;
import com.okmomak.spring.annotation.Controller;
import com.okmomak.spring.annotation.RequestMapping;
import com.okmomak.spring.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@Controller
public class MonsterController {
    @Autowired
    private MonsterService monsterService;

    @RequestMapping("list")
    public void listMonster(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("text/html; charset=utf-8");
        PrintWriter writer = null;
        try {
            writer = response.getWriter();
        } catch (IOException e) {
            e.printStackTrace();
        }
        writer.println("<h2>monster info list</h2>");
        System.out.println("listMonster(); = ");
    }

    @RequestMapping("order/aa")
    public void orderMonster(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("text/html; charset=utf-8");
        PrintWriter writer = null;
        try {
            writer = response.getWriter();
        } catch (IOException e) {
            e.printStackTrace();
        }
        writer.println("<h2>monster order</h2>");
        System.out.println("orderMonster(); = ");
    }

    @RequestMapping("monster/list")
    public void list(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("text/html; charset=utf-8");
        StringBuilder content = new StringBuilder("<h1> monster info </h1>");
        List<Monster> monsters = monsterService.listMonster();
        content.append("<table>");

        for (Monster monster : monsters) {
            content.append("<tr>"
                                + "<td>" + monster.getId() + "</td>"
                                + "<td>" + monster.getName() + "</td>"
                                + "<td>" + monster.getAge() + "</td>"
                                + "<td>" + monster.getSkill() + "</td>"
                        + "</tr>");
        }
        content.append("</table>");

        try {
            response.getWriter().write(content.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("ok!!!!!!!!!!!!!");
    }

    @RequestMapping("monster/query")
    public void query(HttpServletRequest request, HttpServletResponse response, @RequestParam("name") String username) {
        response.setContentType("text/html; charset=utf-8");
        StringBuilder content = new StringBuilder("<h1> monster of username = " + username + " info </h1>");
        List<Monster> monsters = monsterService.findMonsterByName(username);
        content.append("<table>");

        for (Monster monster : monsters) {
            content.append("<tr>"
                    + "<td>" + monster.getId() + "</td>"
                    + "<td>" + monster.getName() + "</td>"
                    + "<td>" + monster.getAge() + "</td>"
                    + "<td>" + monster.getSkill() + "</td>"
                    + "</tr>");
        }
        content.append("</table>");

        try {
            response.getWriter().write(content.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("ok!!!!!!!!!!!!!");
    }

    @RequestMapping("monster/query2")
    public void query2(HttpServletRequest request, HttpServletResponse response, @RequestParam("name") String username, String skill) {
        response.setContentType("text/html; charset=utf-8");
        StringBuilder content = new StringBuilder("<h1> monster of username = " + username + " info </h1>");
        List<Monster> monsters = monsterService.findMonsterByName(username);
        content.append("<table>");

        for (Monster monster : monsters) {
            content.append("<tr>"
                    + "<td>" + monster.getId() + "</td>"
                    + "<td>" + monster.getName() + "</td>"
                    + "<td>" + monster.getAge() + "</td>"
                    + "<td>" + monster.getSkill() + "</td>"
                    + "</tr>");
        }
        content.append("</table>");

        content.append("<h1> monster of skill = " + skill + " info </h1>");
        List<Monster> monsters1 = monsterService.findMonsterBySkill(skill);
        content.append("<table>");

        for (Monster monster : monsters1) {
            content.append("<tr>"
                    + "<td>" + monster.getId() + "</td>"
                    + "<td>" + monster.getName() + "</td>"
                    + "<td>" + monster.getAge() + "</td>"
                    + "<td>" + monster.getSkill() + "</td>"
                    + "</tr>");
        }
        content.append("</table>");

        try {
            response.getWriter().write(content.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("ok!!!!!!!!!!!!!");
    }

    @RequestMapping("login")
    public String login(HttpServletRequest request, String name) {
        System.out.println("name = " + name);
        request.setAttribute("name", name);
        if (name.equals("john")) {
            return "redirect:login_ok.jsp";
        }
        return "redirect:/login_error.jsp";
    }
}
