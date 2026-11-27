import { Component, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { AdminService } from '../../services/admin';
import { MatchingService } from '../../services/matching';
import { CapstoneTerm } from '../../models/admin.model';
import { MatchingPreview, AllocationOption, AllocationChoice, FinalAllocation } from '../../models/matching.model';

@Component({ selector: 'app-matching-preview', standalone: true, imports: [FormsModule, RouterLink],
  templateUrl: './matching-preview.html', styleUrl: './matching-preview.scss' })
export class MatchingPreviewPage implements OnInit {
  private admin = inject(AdminService);
  private matching = inject(MatchingService);
  terms: CapstoneTerm[] = [];
  selectedTermId: number | null = null;
  result: MatchingPreview | null = null;
  options: AllocationOption[] = [];
  choices: AllocationChoice[] = [];
  finalRows: FinalAllocation[] = [];
  saving = false;
  loading = true;
  generating = false;
  errorMessage = '';

  ngOnInit(): void {
    this.admin.getTerms().subscribe({
      next: terms => {
        this.terms = terms;
        this.selectedTermId = terms.find(t => t.active)?.id ?? terms[0]?.id ?? null;
        this.loading = false;
      }, error: () => { this.loading = false; this.errorMessage = 'Unable to load terms.'; }
    });
  }
  clearPreview(): void { this.result = null; this.finalRows = []; this.choices = []; this.options = []; this.errorMessage = ''; }
  viewFinal(): void {
    if (this.selectedTermId === null) return;
    const term = this.selectedTermId;
    this.matching.final(term).subscribe({next: rows => { if (this.selectedTermId === term) this.finalRows = rows; }, error: () => this.errorMessage = 'Could not load saved allocations.'});
  }
  confirm(): void {
    if (!this.result || this.saving || this.finalRows.length || !window.confirm('Finalize all assignments for this term? This cannot be undone here.')) return;
    this.saving = true;
    this.matching.confirm(this.result, this.choices).subscribe({next: rows => {this.finalRows = rows; this.result = null; this.saving = false;}, error: () => {this.saving = false; this.errorMessage = 'Confirmation rejected. Check assignments, reasons, and regenerate if the preview changed.';}});
  }
  generate(): void {
    if (this.selectedTermId === null || this.generating) return;
    this.generating = true;
    this.errorMessage = '';
    this.result = null;
    this.matching.generate(this.selectedTermId).subscribe({
      next: result => { this.result = result; this.choices = result.recommendations.map(r => ({groupId:r.groupId,projectId:r.projectId,reason:''})); this.matching.options(result.termId).subscribe({next: options => {this.options = options; this.generating = false;}, error: () => {this.generating = false; this.errorMessage = 'Could not load approved projects.';}}); this.viewFinal(); },
      error: () => { this.generating = false; this.errorMessage = 'Could not generate matching preview.'; }
    });
  }
}
