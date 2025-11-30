package me.sathish.aws_refresh.controller;

import jakarta.validation.Valid;
import me.sathish.aws_refresh.model.JobRunEmailDTO;
import me.sathish.aws_refresh.service.BatchJobRunService;
import me.sathish.aws_refresh.service.EmailTemplateService;
import me.sathish.aws_refresh.service.JobRunEmailService;
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
@RequestMapping("/jobRunEmails")
public class JobRunEmailController {

    private final JobRunEmailService jobRunEmailService;
    private final BatchJobRunService batchJobRunService;
    private final EmailTemplateService emailTemplateService;

    public JobRunEmailController(final JobRunEmailService jobRunEmailService,
            final BatchJobRunService batchJobRunService,
            final EmailTemplateService emailTemplateService) {
        this.jobRunEmailService = jobRunEmailService;
        this.batchJobRunService = batchJobRunService;
        this.emailTemplateService = emailTemplateService;
    }

    @ModelAttribute
    public void prepareContext(final Model model) {
        model.addAttribute("jobRunValues", batchJobRunService.getBatchJobRunValues());
        model.addAttribute("templateValues", emailTemplateService.getEmailTemplateValues());
    }

    @GetMapping
    public String list(final Model model) {
        model.addAttribute("jobRunEmails", jobRunEmailService.findAll());
        return "jobRunEmail/list";
    }

    @GetMapping("/add")
    public String add(@ModelAttribute("jobRunEmail") final JobRunEmailDTO jobRunEmailDTO) {
        return "jobRunEmail/add";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute("jobRunEmail") @Valid final JobRunEmailDTO jobRunEmailDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "jobRunEmail/add";
        }
        jobRunEmailService.create(jobRunEmailDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("jobRunEmail.create.success"));
        return "redirect:/jobRunEmails";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable(name = "id") final Long id, final Model model) {
        model.addAttribute("jobRunEmail", jobRunEmailService.get(id));
        return "jobRunEmail/edit";
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable(name = "id") final Long id,
            @ModelAttribute("jobRunEmail") @Valid final JobRunEmailDTO jobRunEmailDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "jobRunEmail/edit";
        }
        jobRunEmailService.update(id, jobRunEmailDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("jobRunEmail.update.success"));
        return "redirect:/jobRunEmails";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable(name = "id") final Long id,
            final RedirectAttributes redirectAttributes) {
        jobRunEmailService.delete(id);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("jobRunEmail.delete.success"));
        return "redirect:/jobRunEmails";
    }

}
