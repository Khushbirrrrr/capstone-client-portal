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
