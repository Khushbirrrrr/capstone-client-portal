package com.capstone.clientportal.service;
import com.capstone.clientportal.entity.FinalAllocation;
import com.capstone.clientportal.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import java.util.*;
@Service
public class FinalAllocationService {
 private final MatchingPreviewService preview;
 private final ProjectRepository projects;
 private final TermRepository terms;
 private final FinalAllocationRepository allocations;
 public FinalAllocationService(MatchingPreviewService preview, ProjectRepository projects, TermRepository terms, FinalAllocationRepository allocations){this.preview=preview;this.projects=projects;this.terms=terms;this.allocations=allocations;}
 public record Option(Long id,String title){}
 public record Choice(Long groupId,Long projectId,String reason){}
 public record Confirmation(MatchingPreviewService.MatchingPreview baseline,List<Choice> choices){}
 @Transactional(readOnly=true) public List<Option> options(Long termId){var term=terms.findById(termId).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Term not found"));return projects.findByStatusAndTerm("APPROVED",term.getName()).stream().map(p->new Option(p.getId(),p.getTitle())).toList();}
 @Transactional(readOnly=true) public List<FinalAllocation> view(Long termId){return allocations.findByTermIdOrderByGroupId(termId);}
 @Transactional(isolation=Isolation.SERIALIZABLE) public List<FinalAllocation> confirm(Long termId,Confirmation request,String faculty){
  if(request==null || request.baseline()==null || request.choices()==null) throw bad("Missing reviewed draft");
  if(!allocations.findByTermIdOrderByGroupId(termId).isEmpty()) throw new ResponseStatusException(HttpStatus.CONFLICT,"Term already finalized");
  var current=preview.generate(termId);
  if(!current.equals(request.baseline())) throw new ResponseStatusException(HttpStatus.CONFLICT,"Preview changed; regenerate before confirming");
  var available=new HashMap<Long,String>(); for(var p:options(termId)) available.put(p.id(),p.title());
  var choices=new HashMap<Long,Choice>(); for(var c:request.choices()){if(c==null || c.groupId()==null || choices.putIfAbsent(c.groupId(),c)!=null) throw bad("Duplicate or missing group");}
  if(current.recommendations().isEmpty() || choices.size()!=current.recommendations().size()) throw bad("Every group must be assigned");
  var used=new HashSet<Long>(); var rows=new ArrayList<FinalAllocation>();
  for(var rec:current.recommendations()){
   var c=choices.get(rec.groupId());
   if(c==null || c.projectId()==null || !available.containsKey(c.projectId()) || !used.add(c.projectId())) throw bad("Select one available approved project per group; no duplicates");
   boolean changed=!Objects.equals(rec.projectId(),c.projectId()); String reason=c.reason()==null?"":c.reason().trim();
   if(reason.length()>500 || (changed && reason.isBlank())) throw bad("Overrides require a reason of 1–500 characters");
   rows.add(new FinalAllocation(termId,rec.groupId(),c.projectId(),rec.projectId(),rec.groupName(),available.get(c.projectId()),changed?reason:null,faculty));
  }
  return allocations.saveAllAndFlush(rows);
 }
 private ResponseStatusException bad(String message){return new ResponseStatusException(HttpStatus.BAD_REQUEST,message);}
}
