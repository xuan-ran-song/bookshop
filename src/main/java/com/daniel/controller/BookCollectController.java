package com.daniel.controller;

import com.daniel.common.Result;
import com.daniel.common.ResultGenerator;
import com.daniel.pojo.Book;
import com.daniel.pojo.BookCollect;
import com.daniel.pojo.User;
import com.daniel.service.BookCollectService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequestMapping("/collect")
public class BookCollectController {

//    private static final Logger LOGGER = Logger.getLogger(BookCollectController.class);

    @Autowired
    private BookCollectService bookCollectService;


    @RequestMapping(value = "/add.do",method = RequestMethod.POST)
    @ResponseBody
    public Result add(HttpServletRequest request, int bookId, double bookPrice) {

        // 获取当前用户的信息
        User user = (User) request.getSession().getAttribute("user");

        BookCollect bookCollect = new BookCollect();
        bookCollect.setBookId(bookId);
        bookCollect.setStudentId(user.getStudentid());
        bookCollect.setCollectPrice(bookPrice);
        bookCollectService.add(bookCollect);
        return ResultGenerator.genSuccessResult(null);
    }

    @RequestMapping(value = "/delete.do",method = RequestMethod.POST)
    @ResponseBody
    public Result delete(int deleteBookId){
        if(deleteBookId > 0){
            if(bookCollectService.delete(deleteBookId) > 0){
                return ResultGenerator.genSuccessResult(null);
            }else {
                return ResultGenerator.genFailResult("未找到该书本,移除失败");
            }
        }else {
            return ResultGenerator.genFailResult("图书id不正确,移除失败");
        }

    }

    /***
     * 跳转到个人主页
     * @return
     */
    @RequestMapping("/myhome/{id}")
    public ModelAndView myhome(@PathVariable("id") String id , HttpServletRequest request) {
        ModelAndView mav =new ModelAndView("myhome");
        // 获取当前用户的信息
        User user = (User) request.getSession().getAttribute("user");
        int pageNumber = Integer.parseInt(id);
        List<Book> collectBooks = bookCollectService.getBookAndCollectBook(pageNumber,user.getStudentid());
        int pageCount = bookCollectService.getPageCount(user.getStudentid());
        mav.addObject("pageCount",pageCount);
        mav.addObject("collectBooks",collectBooks);
        mav.addObject("pageNumber",pageNumber);
        return mav;
    }

}
