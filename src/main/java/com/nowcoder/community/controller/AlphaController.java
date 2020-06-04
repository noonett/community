package com.nowcoder.community.controller;

import com.nowcoder.community.service.AlphaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

@Controller  //有这样的注解的bean才会被扫描或者@service, @Repository,他们都是由@Component实现的
@RequestMapping("/alpha")
public class AlphaController {

    @Autowired              //controller依赖service执行服务
    private AlphaService alphaService;

    @RequestMapping("/data")
    @ResponseBody           //返回给浏览器的response体
    public  String getDate(){
        return alphaService.find();
    }


    @RequestMapping("/hello")
    @ResponseBody
    public String sayHello(){
        return "Hello Spring Boot.";
    }
    /***
        常规的servlet编程
    ***/

    @RequestMapping("/http")
    public void http(HttpServletRequest request, HttpServletResponse response) {
        //获取请求数据
        System.out.println(request.getMethod());
        System.out.println(request.getServletPath());
        Enumeration<String> enumeration = request.getHeaderNames();
        while(enumeration.hasMoreElements()){
            String name = enumeration.nextElement();
            String value = request.getHeader(name);
            System.out.println(name + ":" + value);
        }
        System.out.println(request.getParameter("code"));

        //返回响应数据
        response.setContentType("text/html;charset=utf-8");
        try{
            PrintWriter writer = response.getWriter();
            writer.write("<h1>牛客网</h1>");
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    /*
        GET请求
     */

    // /students?current=1&limit=20  (分页条件, 设置参数)
    @RequestMapping(path = "students", method = RequestMethod.GET)
    @ResponseBody
    public String getStudents(   //传入的参数,RequestParam还可以设置参数
            @RequestParam(name = "current", required = false, defaultValue = "1") int current,
                                 @RequestParam(name = "limit", required = false, defaultValue = "10") int limit){
        System.out.println(current);
        System.out.println(limit);
        return "some students";
    }

    // /student/123 直接将参数编排到路径中
    @RequestMapping(path = "/student/{id}", method = RequestMethod.GET)
    @ResponseBody
    public String getStudent(   //传入的参数,RequestParam还可以设置参数
                                @PathVariable("id") int id){
        System.out.println(id);
        return "a student";
    }

    /*
        POST请求
     */
    @RequestMapping(path="/student", method = RequestMethod.POST)
    @ResponseBody
    public String saveStudent(String name, int age){
        System.out.println(name);
        System.out.println(age);
        return "success";
    }

    /*
        总结：
            浏览器向服务器传参有两种方式：
            1. 通过get请求，在路径后加问号携带参数，如/xxx?id=1。
            2. 通过post请求，在request请求体中携带表单中的参数，这种参数在路径上是看不到的。
            这两种方式所传的参数，在服务端都可以通过request.getParameter(参数名)这样的方式来获取。
                而@RequestParam注解，就相当于是request.getParameter()，是从request对象中获取参数的。
            有时，我们也愿意利用请求路径本身来传参，即将参数拼到路径里，如/xxx/1，这里的1就是参数，那么在解析路径的时候，
            也是能获取到这个参数的。而@PathVarible就是解析路径，从中获得对应级次的参数。
     */

    /*
        响应HTML的两种方法
     */
    //返回响应HTML数据
    @RequestMapping(path = "/teacher", method = RequestMethod.GET)  //不加responsebody，默认返回html
    public ModelAndView getTeacher(){   //给DispatcherServlet返回的model和view
        ModelAndView mav = new ModelAndView();
        mav.addObject("name","张三");
        mav.addObject("age","30");
        mav.setViewName("/demo/view");
        return mav;
    }

    //上面是把view和对象装进model返回model给DispatcherServlet
    //这里是传入前端控制器创建的model实例，然后往里装数据
    @RequestMapping(path = "/school", method = RequestMethod.GET)
    public String getSchool(Model model){   //返回html的路径
        model.addAttribute("name", "悉尼大学");
        model.addAttribute("age", 150);
        return "/demo/view";
    }


    /*
        响应JSON请求（接收异步请求用，当前html并未刷新，返回结果）
        JAVA对象 -> JSON字符串 -> JS对象 ：通过JSON实现Java对象与JS对象的交互，本质是JSON字符串
     */
    @RequestMapping(path = "/emp", method = RequestMethod.GET)
    @ResponseBody       //为了返回字符串
    public Map<String, Object> getEmp(){    //DispatcherServlet看到key-value结构和@Responsebody会自动转为Json字符串传给浏览器
        Map<String, Object> emp = new HashMap<>();
        emp.put("name", "张三");
        emp.put("age", 23);
        emp.put("salary", 8000.00);
        return emp;
    }

    //返回多个对象，集合形式的JSON字符串
    @RequestMapping(path = "/emps", method = RequestMethod.GET)
    @ResponseBody       //为了返回字符串
    public List<Map<String, Object>> getEmps(){    //DispatcherServlet看到key-value结构和@Responsebody会自动转为Json字符串传给浏览器
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> emp = new HashMap<>();
        emp.put("name", "张三");
        emp.put("age", 23);
        emp.put("salary", 8000.00);
        list.add(new HashMap<>(emp));

        emp.put("name", "李四");
        emp.put("age", 24);
        emp.put("salary", 9000.00);
        list.add(new HashMap<>(emp));

        emp.put("name", "王五");
        emp.put("age", 25);
        emp.put("salary", 10000.00);
        list.add(new HashMap<>(emp));
        return list;
    }
}
