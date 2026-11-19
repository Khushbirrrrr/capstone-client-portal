import { Component, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { AdminService } from '../../services/admin';
import { MatchingService } from '../../services/matching';
import { CapstoneTerm } from '../../models/admin.model';
import { MatchingPreview } from '../../models/matching.model';

@Component({ selector: 'app-matching-preview', standalone: true, imports: [FormsModule, RouterLink],
  templateUrl: './matching-preview.html', styleUrl: './matching-preview.scss' })
export class MatchingPreviewPage implements OnInit {
  private admin = inject(AdminService);
  private matching = inject(MatchingService);
  terms: CapstoneTerm[] = [];
  selectedTermId: number | null = null;
  result: MatchingPreview | null = null;
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
  clearPreview(): void { this.result = null; this.errorMessage = ''; }
  generate(): void {
    if (this.selectedTermId === null || this.generating) return;
    this.generating = true;
    this.errorMessage = '';
    this.result = null;
    this.matching.generate(this.selectedTermId).subscribe({
      next: result => { this.result = result; this.generating = false; },
      error: () => { this.generating = false; this.errorMessage = 'Could not generate matching preview.'; }
    });
  }
}
