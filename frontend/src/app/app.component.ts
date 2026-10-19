import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient, HttpHeaders } from '@angular/common/http';

type Project = {id:number; title:string; description:string; category:string; clientName:string; approved:boolean};
type Account = {username:string; role:string};
@Component({
 selector:'app-root',standalone:true,imports:[CommonModule,FormsModule],
 template:`
 <header><div class="topbar"><h1>Capstone Client Portal</h1><span *ngIf="account">{{account.role}} · {{account.username}} <button class="secondary" (click)="logout()">Sign out</button></span></div></header>
 <main>
  <section *ngIf="!account" class="panel"><h2>Sign in</h2><p class="muted">Demonstration portal for approved capstone projects.</p>
   <label>Username<input [(ngModel)]="username" placeholder="student or faculty"></label>
   <label>Password<input [(ngModel)]="password" type="password" (keyup.enter)="login()"></label>
   <button (click)="login()">Sign in</button><p class="error" *ngIf="error">{{error}}</p>
   <p><small>Demo users: student / student123 or faculty / faculty123</small></p>
  </section>
  <ng-container *ngIf="account">
   <section *ngIf="!selected"><div class="topbar"><div><h2>Approved Projects</h2><p class="muted">Browse projects available for student groups.</p></div><button *ngIf="account.role==='FACULTY'" (click)="loadFaculty()">Faculty-only view</button></div>
    <p *ngIf="error" class="error">{{error}}</p><div class="grid"><article class="card" *ngFor="let project of projects"><small>{{project.category}}</small><h3>{{project.title}}</h3><p>{{project.description}}</p><button (click)="openProject(project.id)">View details</button></article></div>
    <p *ngIf="projects.length===0">No projects to display.</p>
   </section>
   <section *ngIf="selected" class="panel"><button class="secondary" (click)="selected=null">← Back to projects</button><h2>{{selected.title}}</h2><p><strong>Category:</strong> {{selected.category}}</p><p><strong>Client:</strong> {{selected.clientName}}</p><p>{{selected.description}}</p></section>
   <section class="panel" style="margin-top:22px"><h3>Privacy check (US23)</h3><p class="muted">Try the restricted faculty API with the current account.</p><button (click)="checkFacultyAccess()">Test faculty-only access</button><p>{{privacyResult}}</p></section>
  </ng-container>
 </main>`
})
export class AppComponent {
 private http=inject(HttpClient);
 username='student';password='student123';private auth='';account:Account|null=null;
 projects:Project[]=[];selected:Project|null=null;error='';privacyResult='';
 private headers(){return {headers:new HttpHeaders({Authorization:this.auth})};}
 login(){this.error='';this.auth='Basic '+btoa(this.username+':'+this.password);
  this.http.get<Account>('/api/me',this.headers()).subscribe({next:a=>{this.account=a;this.password='';this.loadProjects();},error:()=>{this.auth='';this.error='Login failed. Check username and password.';}});
 }
 loadProjects(){this.selected=null;this.http.get<Project[]>('/api/projects',this.headers()).subscribe({next:p=>{this.projects=p;this.error='';},error:()=>this.error='Could not load projects.'});}
 openProject(id:number){this.http.get<Project>('/api/projects/'+id,this.headers()).subscribe({next:p=>{this.selected=p;this.error='';},error:()=>this.error='Project not found or not approved.'});}
 loadFaculty(){this.http.get<Project[]>('/api/faculty/projects',this.headers()).subscribe({next:p=>{this.projects=p;this.error='';},error:()=>this.error='Access denied.'});}
 checkFacultyAccess(){this.http.get<Project[]>('/api/faculty/projects',this.headers()).subscribe({next:p=>this.privacyResult='Allowed: faculty can access '+p.length+' total projects (including drafts).',error:e=>this.privacyResult=e.status===403?'403 Forbidden: student cannot access faculty-only projects.':'Request failed: '+e.status});}
 logout(){this.auth='';this.account=null;this.projects=[];this.selected=null;this.password='';this.privacyResult='';}
}
