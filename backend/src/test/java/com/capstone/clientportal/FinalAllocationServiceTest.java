package com.capstone.clientportal;
import com.capstone.clientportal.entity.*;
import com.capstone.clientportal.repository.*;
import com.capstone.clientportal.service.*;
import org.junit.jupiter.api.*;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
class FinalAllocationServiceTest {
 final MatchingPreviewService preview=mock(MatchingPreviewService.class);
 final ProjectRepository projects=mock(ProjectRepository.class);
 final TermRepository terms=mock(TermRepository.class);
 final FinalAllocationRepository allocations=mock(FinalAllocationRepository.class);
 final FinalAllocationService service=new FinalAllocationService(preview,projects,terms,allocations);
 final MatchingPreviewService.MatchingPreview baseline=new MatchingPreviewService.MatchingPreview(1L,"Fall",List.of(new MatchingPreviewService.Recommendation(1L,"Group",10L,"A",5,"RECOMMENDED","Review")));
 @BeforeEach void setup(){when(preview.generate(1L)).thenReturn(baseline);when(terms.findById(1L)).thenReturn(Optional.of(new Term("Fall",true)));var p=new Project("B","Demo","APPROVED","Fall");ReflectionTestUtils.setField(p,"id",20L);when(projects.findByStatusAndTerm("APPROVED","Fall")).thenReturn(List.of(p));}
 @Test void overrideRequiresReason(){assertThrows(ResponseStatusException.class,()->service.confirm(1L,new FinalAllocationService.Confirmation(baseline,List.of(new FinalAllocationService.Choice(1L,20L,""))),"faculty"));verify(allocations,never()).saveAllAndFlush(anyList());}
 @Test void validOverrideRecordsPreviousProject(){when(allocations.saveAllAndFlush(anyList())).thenAnswer(i->i.getArgument(0));var rows=service.confirm(1L,new FinalAllocationService.Confirmation(baseline,List.of(new FinalAllocationService.Choice(1L,20L,"Client request"))),"faculty");assertEquals(10L,rows.get(0).getPreviousProjectId());assertEquals(20L,rows.get(0).getProjectId());assertEquals("faculty",rows.get(0).getConfirmedBy());}
 @Test void stalePreviewRejected(){var stale=new MatchingPreviewService.MatchingPreview(1L,"Fall",List.of());assertThrows(ResponseStatusException.class,()->service.confirm(1L,new FinalAllocationService.Confirmation(stale,List.of()),"faculty"));verify(allocations,never()).saveAllAndFlush(anyList());}
}
