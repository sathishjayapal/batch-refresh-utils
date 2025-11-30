package me.sathish.aws_refresh.controller;

import jakarta.validation.Valid;
import me.sathish.aws_refresh.model.BatchJobRunDTO;
import me.sathish.aws_refresh.service.BatchJobRunService;
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
@RequestMapping("/batchJobRuns")
public class BatchJobRunController {

    private final BatchJobRunService batchJobRunService;
    private final BatchJobService batchJobService;

    public BatchJobRunController(final BatchJobRunService batchJobRunService,
            final BatchJobService batchJobService) {
        this.batchJobRunService = batchJobRunService;
        this.batchJobService = batchJobService;
    }

    @ModelAttribute
    public void prepareContext(final Model model) {
        model.addAttribute("jobValues", batchJobService.getBatchJobValues());
    }

    @GetMapping
    public String list(final Model model) {
        model.addAttribute("batchJobRuns", batchJobRunService.findAll());
        return "batchJobRun/list";
    }

    @GetMapping("/add")
    public String add(@ModelAttribute("batchJobRun") final BatchJobRunDTO batchJobRunDTO) {
        return "batchJobRun/add";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute("batchJobRun") @Valid final BatchJobRunDTO batchJobRunDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "batchJobRun/add";
        }
        batchJobRunService.create(batchJobRunDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("batchJobRun.create.success"));
        return "redirect:/batchJobRuns";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable(name = "id") final Long id, final Model model) {
        model.addAttribute("batchJobRun", batchJobRunService.get(id));
        return "batchJobRun/edit";
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable(name = "id") final Long id,
            @ModelAttribute("batchJobRun") @Valid final BatchJobRunDTO batchJobRunDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "batchJobRun/edit";
        }
        batchJobRunService.update(id, batchJobRunDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("batchJobRun.update.success"));
        return "redirect:/batchJobRuns";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable(name = "id") final Long id,
            final RedirectAttributes redirectAttributes) {
        try {
            batchJobRunService.delete(id);
            redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("batchJobRun.delete.success"));
        } catch (final ReferencedException referencedException) {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, WebUtils.getMessage(
                    referencedException.getKey(), referencedException.getParams().toArray()));
        }
        return "redirect:/batchJobRuns";
    }

}
