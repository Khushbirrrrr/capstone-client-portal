import { RenderMode, ServerRoute } from '@angular/ssr';

// Authenticated student pages must not be statically pre-rendered during build.
export const serverRoutes: ServerRoute[] = [{ path: '**', renderMode: RenderMode.Client }];
