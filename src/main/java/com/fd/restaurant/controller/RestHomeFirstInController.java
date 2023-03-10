package com.fd.restaurant.controller;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.fd.admin.model.vo.Question;
import com.fd.book.model.vo.Book;
import com.fd.book.model.vo.NotAble;
import com.fd.restaurant.model.service.RestaurantService;
import com.fd.restaurant.model.vo.Restaurant;
import com.fd.review.model.vo.Review;

/**
 * Servlet implementation class RestFirstInController
 */
@WebServlet("/home.re")
public class RestHomeFirstInController extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public RestHomeFirstInController() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();	
		if(session.getAttribute("loginRest")==null) {
			session.setAttribute("alertMsg", "로그인 후 이용가능한 서비스입니다.");
			response.sendRedirect(request.getContextPath()+"/rest.admin");
		}else {
			Restaurant rest = (Restaurant)session.getAttribute("loginRest");
			if(rest.getStatus().equals("C")) { //관리자 승인 후, 첫로그인을 한 상태(첫 비번설정을 안한상태)
				request.getRequestDispatcher("views/restaurant/restFirstIn_1.jsp").forward(request, response);
			}else if(rest.getStatus().equals("Y")) { //정상적으로 이용중인 업체
				//홈화면을 꾸며줄 데이터들 조회해오기
				int resNo = ((Restaurant)session.getAttribute("loginRest")).getResNo();
				
				//새로운 예약건 조회해오기
				ArrayList<Book> bList = new RestaurantService().selectNewBook(resNo);
				request.setAttribute("bList", bList);
				
				//달력 구성에 필요한 정보 담기
				String year = (String)session.getAttribute("sessionYear");
				String month = (String)session.getAttribute("sessionMonth");
				request.setAttribute("year", Integer.parseInt(year));
				request.setAttribute("month", Integer.parseInt(month));
				
				//불가날짜 담기
				NotAble na = new NotAble();
				na.setResNo(resNo);
				na.setYear(String.valueOf(year));
				na.setMonth(String.valueOf(month));
				request.setAttribute("naList", new RestaurantService().selectNotAble(na));
				
				//일별예약정보담기(달력용)
				if(month.length()==1) {month = "0"+month;} //달력용 숫자는 2자리로 만들기
				ArrayList<Book> calBookList = new RestaurantService().selectBookForCalendar(resNo, year, month);
				request.setAttribute("calBookList", calBookList);
				
				//새로운 문의 조회해서 담기
				request.setAttribute("qList", new RestaurantService().selectNewQuestion(resNo));
				
				//새로운 리뷰 조회해서 담기
				request.setAttribute("rList", new RestaurantService().selectNewReview(resNo));
				
				//포워딩하기
				request.getRequestDispatcher("views/restaurant/restHome.jsp").forward(request, response);
			}
		}
	}
	

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
