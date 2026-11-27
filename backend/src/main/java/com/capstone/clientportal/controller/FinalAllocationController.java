package com.capstone.clientportal.controller;
import com.capstone.clientportal.service.FinalAllocationService;
import com.capstone.clientportal.entity.FinalAllocation;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.List;
@RestController
@RequestMapping("/api/matching/final")
public class FinalAllocationController {
 private final FinalAllocationService service;
 public FinalAllocationController(FinalAllocationService service){this.service=service;}
 @GetMapping("/{termId}/options") public List<FinalAllocationService.Option> options(@PathVariable Long termId){return service.options(termId);}
 @GetMapping("/{termId}") public List<FinalAllocation> view(@PathVariable Long termId){return service.view(termId);}
 @PostMapping("/{termId}") public List<FinalAllocation> confirm(@PathVariable Long termId,@RequestBody FinalAllocationService.Confirmation request,Principal principal){return service.confirm(termId,request,principal.getName());}
}
