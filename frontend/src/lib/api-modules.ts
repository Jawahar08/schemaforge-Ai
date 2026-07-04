import type {
  AuthResponse,
  LoginRequest,
  RegisterRequest,
  ProjectResponse,
  CreateProjectRequest,
  UpdateProjectRequest,
  SchemaResponse,
  SchemaSummaryResponse,
  SchemaVersionSummaryResponse,
  SchemaVersionResponse,
  GenerateSchemaRequest,
  ExportResponse,
  CreateExportRequest,
  CommentResponse,
  CreateCommentRequest,
  UpdateCommentRequest,
  NotificationResponse,
  TeamResponse,
  TeamSummaryResponse,
  TeamMemberResponse,
  InvitationResponse,
  CreateTeamRequest,
  CreateInvitationRequest,
  AiRequestSummaryResponse,
  UserResponse,
} from '@/types';
import { api } from '@/lib/api';

// ─── Auth ─────────────────────────────────────────────────────────────────────
export const authApi = {
  login: (req: LoginRequest) => api.post<AuthResponse>('/api/auth/login', req),
  register: (req: RegisterRequest) => api.post<AuthResponse>('/api/auth/register', req),
};

// ─── User ─────────────────────────────────────────────────────────────────────
export const userApi = {
  me: () => api.get<UserResponse>('/api/users/me'),
};

// ─── Projects ─────────────────────────────────────────────────────────────────
export const projectsApi = {
  list: () => api.get<ProjectResponse[]>('/api/projects'),
  get: (id: string) => api.get<ProjectResponse>(`/api/projects/${id}`),
  create: (req: CreateProjectRequest) => api.post<ProjectResponse>('/api/projects', req),
  update: (id: string, req: UpdateProjectRequest) =>
    api.patch<ProjectResponse>(`/api/projects/${id}`, req),
  delete: (id: string) => api.delete<void>(`/api/projects/${id}`),
};

// ─── Schemas ──────────────────────────────────────────────────────────────────
export const schemasApi = {
  listForProject: (projectId: string) =>
    api.get<SchemaSummaryResponse[]>(`/api/projects/${projectId}/schemas`),
  get: (schemaId: string) => api.get<SchemaResponse>(`/api/schemas/${schemaId}`),
  delete: (schemaId: string) => api.delete<void>(`/api/schemas/${schemaId}`),
  getVersions: (schemaId: string) =>
    api.get<SchemaVersionSummaryResponse[]>(`/api/schemas/${schemaId}/versions`),
  getVersion: (schemaId: string, versionNumber: number) =>
    api.get<SchemaVersionResponse>(`/api/schemas/${schemaId}/versions/${versionNumber}`),
  restoreVersion: (schemaId: string, versionNumber: number) =>
    api.post<SchemaResponse>(`/api/schemas/${schemaId}/versions/${versionNumber}/restore`),
};

// ─── AI Generation ────────────────────────────────────────────────────────────
export const aiApi = {
  generate: (req: GenerateSchemaRequest) =>
    api.post<SchemaResponse>('/api/schemas/generate', req),
  listRequests: () => api.get<AiRequestSummaryResponse[]>('/api/ai/requests'),
};

// ─── Exports ──────────────────────────────────────────────────────────────────
export const exportsApi = {
  create: (schemaId: string, req: CreateExportRequest) =>
    api.post<ExportResponse>(`/api/schemas/${schemaId}/exports`, req),
  get: (exportId: string) => api.get<ExportResponse>(`/api/exports/${exportId}`),
  download: (exportId: string) => api.download(`/api/exports/${exportId}/download`),
};

// ─── Comments ─────────────────────────────────────────────────────────────────
export const commentsApi = {
  list: (schemaId: string) => api.get<CommentResponse[]>(`/api/schemas/${schemaId}/comments`),
  create: (schemaId: string, req: CreateCommentRequest) =>
    api.post<CommentResponse>(`/api/schemas/${schemaId}/comments`, req),
  update: (commentId: string, req: UpdateCommentRequest) =>
    api.patch<CommentResponse>(`/api/comments/${commentId}`, req),
  delete: (commentId: string) => api.delete<void>(`/api/comments/${commentId}`),
};

// ─── Notifications ────────────────────────────────────────────────────────────
export const notificationsApi = {
  list: () => api.get<NotificationResponse[]>('/api/notifications'),
  listUnread: () => api.get<NotificationResponse[]>('/api/notifications/unread'),
  markRead: (id: string) => api.patch<NotificationResponse>(`/api/notifications/${id}/read`),
  markAllRead: () => api.patch<void>('/api/notifications/read-all'),
  delete: (id: string) => api.delete<void>(`/api/notifications/${id}`),
};

// ─── Teams ────────────────────────────────────────────────────────────────────
export const teamsApi = {
  list: () => api.get<TeamSummaryResponse[]>('/api/teams'),
  get: (teamId: string) => api.get<TeamResponse>(`/api/teams/${teamId}`),
  create: (req: CreateTeamRequest) => api.post<TeamResponse>('/api/teams', req),
  delete: (teamId: string) => api.delete<void>(`/api/teams/${teamId}`),
  getMembers: (teamId: string) =>
    api.get<TeamMemberResponse[]>(`/api/teams/${teamId}/members`),
  removeMember: (teamId: string, userId: string) =>
    api.delete<void>(`/api/teams/${teamId}/members/${userId}`),
  invite: (teamId: string, req: CreateInvitationRequest) =>
    api.post<InvitationResponse>(`/api/teams/${teamId}/invitations`, req),
  listInvitations: (teamId: string) =>
    api.get<InvitationResponse[]>(`/api/teams/${teamId}/invitations`),
};
