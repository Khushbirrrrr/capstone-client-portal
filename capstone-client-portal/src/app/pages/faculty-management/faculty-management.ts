import { Component, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { AdminService } from '../../services/admin';
import { CapstoneTerm, StudentGroup } from '../../models/admin.model';

@Component({ selector: 'app-faculty-management', standalone: true, imports: [FormsModule, RouterLink],
  templateUrl: './faculty-management.html', styleUrl: './faculty-management.scss' })
export class FacultyManagement implements OnInit {
  private api = inject(AdminService);
  terms: CapstoneTerm[] = [];
  groups: StudentGroup[] = [];
  termName = '';
  selectedTermId: number | null = null;
  groupName = '';
  memberInput = '';
  memberUsernames: string[] = [];
  loading = true;
  saving = false;
  errorMessage = '';
  message = '';

  ngOnInit(): void { this.loadTerms(); }
  loadTerms(): void {
    this.api.getTerms().subscribe({
      next: terms => {
        this.terms = terms;
        const current = terms.find(t => t.active);
        if (!terms.some(t => t.id === this.selectedTermId)) this.selectedTermId = current?.id ?? terms[0]?.id ?? null;
        this.loading = false;
        this.loadGroups();
      }, error: () => { this.loading = false; this.errorMessage = 'Could not load terms.'; }
    });
  }
  loadGroups(): void {
    if (this.selectedTermId === null) { this.groups = []; return; }
    this.api.getGroups(this.selectedTermId).subscribe({
      next: groups => this.groups = groups,
      error: () => this.errorMessage = 'Could not load student groups.'
    });
  }
  createTerm(): void {
    this.clearMessages();
    if (!this.termName.trim()) { this.errorMessage = 'Enter a term name.'; return; }
    this.saving = true;
    this.api.createTerm(this.termName).subscribe({
      next: () => { this.saving = false; this.termName = ''; this.message = 'Term created.'; this.loadTerms(); },
      error: () => { this.saving = false; this.errorMessage = 'Unable to create term (it may already exist).'; }
    });
  }
  activate(id: number): void {
    this.clearMessages();
    this.api.activateTerm(id).subscribe({
      next: () => { this.message = 'Active term updated.'; this.loadTerms(); },
      error: () => this.errorMessage = 'Unable to activate term.'
    });
  }
  // Check input immediately so Add is enabled only for a new, valid username.
  get canAddMember(): boolean {
    const name = this.memberInput.trim().toLowerCase();
    return this.memberUsernames.length < 4 && /^[a-z][a-z0-9._-]{2,39}$/.test(name)
      && !this.memberUsernames.includes(name) && !this.saving;
  }
  get canCreateGroup(): boolean {
    return !this.saving && this.selectedTermId !== null
      && this.groupName.trim().length >= 3 && this.groupName.trim().length <= 80
      && this.memberUsernames.length >= 1 && this.memberUsernames.length <= 4;
  }
  addMember(): void {
    if (!this.canAddMember) return;
    this.memberUsernames = [...this.memberUsernames, this.memberInput.trim().toLowerCase()];
    this.memberInput = '';
    this.clearMessages();
  }
  removeMember(username: string): void {
    this.memberUsernames = this.memberUsernames.filter(name => name !== username);
    this.clearMessages();
  }
  createGroup(): void {
    this.clearMessages();
    if (this.selectedTermId === null) { this.errorMessage = 'Select a term first.'; return; }
    if (!this.canCreateGroup) {
      this.errorMessage = 'Enter a valid group name and add 1 to 4 students.'; return;
    }
    const memberUsernames = [...this.memberUsernames];
    this.saving = true;
    this.api.createGroup(this.groupName, this.selectedTermId, memberUsernames).subscribe({
      next: () => {
        this.saving = false; this.groupName = ''; this.memberInput = ''; this.memberUsernames = [];
        this.message = 'Group created.'; this.loadGroups();
      },
      error: err => {
        this.saving = false;
        this.errorMessage = err.status === 400 ? err.error?.detail || 'Check group name, member usernames, duplicates, and the four-student limit.' : 'Unable to create group.';
      }
    });
  }
  removeGroup(id: number): void {
    this.clearMessages();
    if (!confirm('Delete this group?')) return;
    this.api.deleteGroup(id).subscribe({
      next: () => { this.message = 'Group deleted.'; this.loadGroups(); },
      error: () => this.errorMessage = 'Unable to remove group.'
    });
  }
  private clearMessages(): void { this.message = ''; this.errorMessage = ''; }
}
