// ─── Core API response wrapper ───────────────────────────────────────────────
export interface ApiResponse<T> {
  success: boolean;
  message: string | null;
  data: T;
  timestamp: string;
}

// ─── Auth ─────────────────────────────────────────────────────────────────────
export interface UserResponse {
  id: string;
  email: string;
  fullName: string;
  avatarUrl: string | null;
  role: 'USER' | 'ADMIN';
  status: 'PENDING_VERIFICATION' | 'ACTIVE' | 'SUSPENDED';
  aiCredits: number;
  createdAt: string;
  updatedAt: string;
}

export interface AuthResponse {
  accessToken: string;
  tokenType: string;
  user: UserResponse;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  email: string;
  password: string;
  fullName: string;
}

// ─── Projects ─────────────────────────────────────────────────────────────────
export type ProjectDialect = 'postgresql' | 'mysql' | 'sqlserver' | 'oracle';
export type ProjectStatus = 'ACTIVE' | 'ARCHIVED';

export interface ProjectResponse {
  id: string;
  name: string;
  description: string | null;
  dialect: ProjectDialect;
  status: ProjectStatus;
  tags: string[] | null;
  ownerId: string;
  createdAt: string;
  updatedAt: string;
}

export interface CreateProjectRequest {
  name: string;
  description?: string;
  dialect: ProjectDialect;
  tags?: string[];
}

export interface UpdateProjectRequest {
  name?: string;
  description?: string;
  dialect?: ProjectDialect;
  tags?: string[];
}

// ─── Schemas ──────────────────────────────────────────────────────────────────
export type NormalizationTarget = 'TWO_NF' | 'THREE_NF' | 'BCNF';
export type SchemaStatus = 'DRAFT' | 'FINAL';
export type AiProvider = 'CLAUDE' | 'OPENAI' | 'GEMINI' | 'MOCK';

export interface SchemaTable {
  name: string;
  columns?: SchemaColumn[];
  [key: string]: unknown;
}

export interface SchemaColumn {
  name: string;
  type: string;
  nullable?: boolean;
  primaryKey?: boolean;
  foreignKey?: boolean;
  unique?: boolean;
  references?: string;
  [key: string]: unknown;
}

export interface SchemaRelationship {
  fromTable: string;
  toTable: string;
  type: string;
  [key: string]: unknown;
}

export interface SchemaResponse {
  id: string;
  projectId: string;
  systemName: string;
  description: string | null;
  normalizationTarget: NormalizationTarget | null;
  tables: Record<string, unknown>[];
  relationships: Record<string, unknown>[];
  normalizationNotes: Record<string, unknown>[];
  analysisItems: Record<string, unknown>[];
  status: SchemaStatus;
  currentVersion: number;
  createdAt: string;
  updatedAt: string;
}

export interface SchemaSummaryResponse {
  id: string;
  projectId: string;
  systemName: string;
  status: SchemaStatus;
  currentVersion: number;
  tableCount: number;
  createdAt: string;
  updatedAt: string;
}

export interface SchemaVersionSummaryResponse {
  id: string;
  schemaId: string;
  versionNumber: number;
  createdAt: string;
}

export interface SchemaVersionResponse {
  id: string;
  schemaId: string;
  versionNumber: number;
  tables: Record<string, unknown>[];
  relationships: Record<string, unknown>[];
  normalizationNotes: Record<string, unknown>[];
  analysisItems: Record<string, unknown>[];
  createdAt: string;
}

export interface GenerateSchemaRequest {
  projectId: string;
  description: string;
  normalizationTarget?: NormalizationTarget;
  provider?: AiProvider;
}

// ─── Exports ──────────────────────────────────────────────────────────────────
export type ExportDialect = 'POSTGRESQL' | 'MYSQL' | 'SQLSERVER' | 'ORACLE';
export type ExportStatus = 'PENDING' | 'COMPLETED' | 'FAILED';
export type ExportType = 'DDL' | 'FULL';

export interface ExportResponse {
  exportId: string;
  schemaId: string;
  exportType: ExportType;
  dialect: ExportDialect;
  status: ExportStatus;
  fileSizeBytes: number | null;
  createdAt: string;
  completedAt: string | null;
}

export interface CreateExportRequest {
  dialect: ExportDialect;
}

// ─── Comments ─────────────────────────────────────────────────────────────────
export interface CommentResponse {
  id: string;
  projectId: string;
  schemaId: string;
  userId: string;
  parentCommentId: string | null;
  content: string;
  entityReference: string | null;
  resolved: boolean;
  edited: boolean;
  editedAt: string | null;
  createdAt: string;
  updatedAt: string;
}

export interface CreateCommentRequest {
  content: string;
  parentCommentId?: string;
  entityReference?: string;
}

export interface UpdateCommentRequest {
  content: string;
}

// ─── Notifications ────────────────────────────────────────────────────────────
export type NotificationType =
  | 'COMMENT_CREATED'
  | 'TEAM_INVITATION'
  | 'SCHEMA_GENERATED'
  | 'EXPORT_COMPLETED'
  | 'SCHEMA_UPDATED'
  | 'PROJECT_CREATED';

export interface NotificationResponse {
  id: string;
  userId: string;
  type: NotificationType;
  title: string;
  message: string;
  metadata: Record<string, string> | null;
  read: boolean;
  createdAt: string;
}

// ─── Teams ────────────────────────────────────────────────────────────────────
export type TeamPlan = 'FREE' | 'PRO' | 'ENTERPRISE';
export type TeamMemberRole = 'OWNER' | 'ADMIN' | 'MEMBER' | 'VIEWER';
export type InvitationStatus = 'PENDING' | 'ACCEPTED' | 'DECLINED' | 'EXPIRED';

export interface TeamResponse {
  id: string;
  name: string;
  slug: string;
  description: string | null;
  ownerId: string;
  plan: TeamPlan;
  createdAt: string;
  updatedAt: string;
}

export interface TeamSummaryResponse {
  id: string;
  name: string;
  slug: string;
  plan: TeamPlan;
}

export interface TeamMemberResponse {
  id: string;
  userId: string;
  role: TeamMemberRole;
  joinedAt: string;
}

export interface InvitationResponse {
  id: string;
  teamId: string;
  email: string;
  role: TeamMemberRole;
  status: InvitationStatus;
  createdAt: string;
}

export interface CreateTeamRequest {
  name: string;
  description?: string;
}

export interface CreateInvitationRequest {
  email: string;
  role: TeamMemberRole;
}

// ─── AI Requests ──────────────────────────────────────────────────────────────
export interface AiRequestSummaryResponse {
  id: string;
  provider: AiProvider;
  projectId: string;
  schemaId: string | null;
  status: string;
  creditsUsed: number;
  createdAt: string;
}

// ─── Pagination ───────────────────────────────────────────────────────────────
export interface Page<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}
