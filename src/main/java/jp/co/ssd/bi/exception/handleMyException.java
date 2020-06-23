package jp.co.ssd.bi.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import jp.co.ssd.bi.model.MyException;

@ControllerAdvice
class handleMyException {
	public static final String DEFAULT_ERROR_VIEW = "error";
	@ExceptionHandler(value = MyException.class)
	Object handleMyException(MyException e) {
	    ModelAndView mv = new ModelAndView();
	    mv.setViewName("error.html");
	    mv.addObject("msg", e.getMessage());
	    return mv;
	}

}
