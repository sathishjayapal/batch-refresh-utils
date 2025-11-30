package me.sathish.aws_refresh.controller;

import jakarta.validation.Valid;
import me.sathish.aws_refresh.model.JobNotificationSubscriptionDTO;
import me.sathish.aws_refresh.service.BatchJobService;
import me.sathish.aws_refresh.service.EmailTemplateService;
import me.sathish.aws_refresh.service.JobNotificationSubscriptionService;
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
@RequestMapping("/jobNotificationSubscriptions")
public class JobNotificationSubscriptionController {

    private final JobNotificationSubscriptionService jobNotificationSubscriptionService;
    private final BatchJobService batchJobService;
    private final EmailTemplateService emailTemplateService;

    public JobNotificationSubscriptionController(
            final JobNotificationSubscriptionService jobNotificationSubscriptionService,
            final BatchJobService batchJobService,
            final EmailTemplateService emailTemplateService) {
        this.jobNotificationSubscriptionService = jobNotificationSubscriptionService;
        this.batchJobService = batchJobService;
        this.emailTemplateService = emailTemplateService;
    }

    @ModelAttribute
    public void prepareContext(final Model model) {
        model.addAttribute("jobValues", batchJobService.getBatchJobValues());
        model.addAttribute("templateValues", emailTemplateService.getEmailTemplateValues());
    }

    @GetMapping
    public String list(final Model model) {
        model.addAttribute("jobNotificationSubscriptions", jobNotificationSubscriptionService.findAll());
        return "jobNotificationSubscription/list";
    }

    @GetMapping("/add")
    public String add(
            @ModelAttribute("jobNotificationSubscription") final JobNotificationSubscriptionDTO jobNotificationSubscriptionDTO) {
        return "jobNotificationSubscription/add";
    }

    @PostMapping("/add")
    public String add(
            @ModelAttribute("jobNotificationSubscription") @Valid final JobNotificationSubscriptionDTO jobNotificationSubscriptionDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "jobNotificationSubscription/add";
        }
        jobNotificationSubscriptionService.create(jobNotificationSubscriptionDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("jobNotificationSubscription.create.success"));
        return "redirect:/jobNotificationSubscriptions";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable(name = "id") final Long id, final Model model) {
        model.addAttribute("jobNotificationSubscription", jobNotificationSubscriptionService.get(id));
        return "jobNotificationSubscription/edit";
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable(name = "id") final Long id,
            @ModelAttribute("jobNotificationSubscription") @Valid final JobNotificationSubscriptionDTO jobNotificationSubscriptionDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "jobNotificationSubscription/edit";
        }
        jobNotificationSubscriptionService.update(id, jobNotificationSubscriptionDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("jobNotificationSubscription.update.success"));
        return "redirect:/jobNotificationSubscriptions";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable(name = "id") final Long id,
            final RedirectAttributes redirectAttributes) {
        jobNotificationSubscriptionService.delete(id);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("jobNotificationSubscription.delete.success"));
        return "redirect:/jobNotificationSubscriptions";
    }

}
