package org.project.controlleradvice;


import org.project.exception.ResourceNotFoundException;
import org.project.exception.page.InvalidPageException;
import org.project.exception.page.PageNotFoundException;
import org.project.exception.sql.EntityNotFoundException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ControllerAdvisor {

    @ExceptionHandler(exception = {
            InvalidPageException.class,
            PageNotFoundException.class,
            EntityNotFoundException.class,
            ResourceNotFoundException.class
    })
    public String handleNotFoundExceptions(Exception e, Model model) {
        model.addAttribute("error", e.getMessage());
        return "frontend/404";
    }
}
