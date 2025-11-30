package me.sathish.aws_refresh.controller;

import jakarta.validation.Valid;
import me.sathish.aws_refresh.model.BatchJobDTO;
import me.sathish.aws_refresh.service.BatchJobService;
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
@RequestMapping("/batchJobs")
public class BatchJobController {

    private final BatchJobService batchJobService;

    public BatchJobController(final BatchJobService batchJobService) {
        this.batchJobService = batchJobService;
    }

    @GetMapping
    public String list(final Model model) {
        model.addAttribute("batchJobs", batchJobService.findAll());
        return "batchJob/list";
    }

    @GetMapping("/add")
    public String add(@ModelAttribute("batchJob") final BatchJobDTO batchJobDTO) {
        return "batchJob/add";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute("batchJob") @Valid final BatchJobDTO batchJobDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "batchJob/add";
        }
        batchJobService.create(batchJobDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("batchJob.create.success"));
        return "redirect:/batchJobs";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable(name = "id") final Long id, final Model model) {
        model.addAttribute("batchJob", batchJobService.get(id));
        return "batchJob/edit";
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable(name = "id") final Long id,
            @ModelAttribute("batchJob") @Valid final BatchJobDTO batchJobDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "batchJob/edit";
        }
        batchJobService.update(id, batchJobDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("batchJob.update.success"));
        return "redirect:/batchJobs";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable(name = "id") final Long id,
            final RedirectAttributes redirectAttributes) {
        try {
            batchJobService.delete(id);
            redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("batchJob.delete.success"));
        } catch (final ReferencedException referencedException) {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, WebUtils.getMessage(
                    referencedException.getKey(), referencedException.getParams().toArray()));
        }
        return "redirect:/batchJobs";
    }

}
