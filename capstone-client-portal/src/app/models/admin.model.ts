export interface CapstoneTerm {
  id: number;
  name: string;
  active: boolean;
}
export interface StudentGroup {
  id: number;
  name: string;
  termId: number;
  members: string[];
}
