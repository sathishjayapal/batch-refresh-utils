package me.sathish.aws_refresh.controller;

import jakarta.validation.Valid;
import me.sathish.aws_refresh.model.EmailTemplateDTO;
import me.sathish.aws_refresh.service.EmailTemplateService;
import me.sathish.aws_refresh.util.ReferencedException;
import me.sathish.aws_refresh.util.WebUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
@RequestMapping("/emailTemplates")
public class EmailTemplateController {

    private final EmailTemplateService emailTemplateService;

    public EmailTemplateController(final EmailTemplateService emailTemplateService) {
        this.emailTemplateService = emailTemplateService;
    }

    @GetMapping
    public String list(final Model model) {
        model.addAttribute("emailTemplates", emailTemplateService.findAll());
        return "emailTemplate/list";
    }

    @GetMapping("/add")
    public String add(@ModelAttribute("emailTemplate") final EmailTemplateDTO emailTemplateDTO) {
        return "emailTemplate/add";
    }

    @PostMapping("/add")
    public String add(
            @ModelAttribute("emailTemplate") @Valid final EmailTemplateDTO emailTemplateDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "emailTemplate/add";
        }
        emailTemplateService.create(emailTemplateDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("emailTemplate.create.success"));
        return "redirect:/emailTemplates";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable(name = "id") final Long id, final Model model) {
        model.addAttribute("emailTemplate", emailTemplateService.get(id));
        return "emailTemplate/edit";
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable(name = "id") final Long id,
            @ModelAttribute("emailTemplate") @Valid final EmailTemplateDTO emailTemplateDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "emailTemplate/edit";
        }
        emailTemplateService.update(id, emailTemplateDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("emailTemplate.update.success"));
        return "redirect:/emailTemplates";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable(name = "id") final Long id,
            final RedirectAttributes redirectAttributes) {
        try {
            emailTemplateService.delete(id);
            redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("emailTemplate.delete.success"));
        } catch (final ReferencedException referencedException) {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, WebUtils.getMessage(
                    referencedException.getKey(), referencedException.getParams().toArray()));
        }
        return "redirect:/emailTemplates";
    }

}
