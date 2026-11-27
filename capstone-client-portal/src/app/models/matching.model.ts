export interface MatchSuggestion {
  groupId: number;
  groupName: string;
  projectId: number | null;
  projectTitle: string | null;
  score: number;
  status: 'RECOMMENDED' | 'UNRESOLVED';
  note: string;
}
export interface MatchingPreview {
  termId: number;
  term: string;
  recommendations: MatchSuggestion[];
}

export interface AllocationOption { id: number; title: string; }
export interface AllocationChoice { groupId: number; projectId: number | null; reason: string; }
export interface FinalAllocation { groupId: number; projectId: number; groupName: string; projectTitle: string; previousProjectId: number | null; reason: string | null; confirmedBy: string; confirmedAt: string; }
