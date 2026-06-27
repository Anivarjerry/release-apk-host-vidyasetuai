{
	"schemas": [
		{
			"table_name": "app_versions",
			"columns": [
				{
					"column_name": "id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 1,
					"column_default": "gen_random_uuid()"
				},
				{
					"column_name": "platform",
					"data_type": "USER-DEFINED",
					"is_nullable": "NO",
					"udt_name": "release_platform",
					"ordinal_position": 2,
					"column_default": null
				},
				{
					"column_name": "version_number",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 3,
					"column_default": null
				},
				{
					"column_name": "build_number",
					"data_type": "integer",
					"is_nullable": "NO",
					"udt_name": "int4",
					"ordinal_position": 4,
					"column_default": null
				},
				{
					"column_name": "release_type",
					"data_type": "USER-DEFINED",
					"is_nullable": "NO",
					"udt_name": "release_type",
					"ordinal_position": 5,
					"column_default": "'stable'::release_type"
				},
				{
					"column_name": "file_url",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 6,
					"column_default": null
				},
				{
					"column_name": "changelog",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 7,
					"column_default": null
				},
				{
					"column_name": "is_latest",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 8,
					"column_default": "false"
				},
				{
					"column_name": "force_update",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 9,
					"column_default": "false"
				},
				{
					"column_name": "published_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 10,
					"column_default": "timezone('utc'::text, now())"
				},
				{
					"column_name": "created_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 11,
					"column_default": "timezone('utc'::text, now())"
				},
				{
					"column_name": "is_deleted",
					"data_type": "boolean",
					"is_nullable": "YES",
					"udt_name": "bool",
					"ordinal_position": 12,
					"column_default": "false"
				},
				{
					"column_name": "updated_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "YES",
					"udt_name": "timestamptz",
					"ordinal_position": 13,
					"column_default": "now()"
				}
			]
		},
		{
			"table_name": "case_studies",
			"columns": [
				{
					"column_name": "id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 1,
					"column_default": "gen_random_uuid()"
				},
				{
					"column_name": "title",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 2,
					"column_default": null
				},
				{
					"column_name": "slug",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 3,
					"column_default": null
				},
				{
					"column_name": "cover_image_url",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 4,
					"column_default": null
				},
				{
					"column_name": "short_description",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 5,
					"column_default": null
				},
				{
					"column_name": "language",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 6,
					"column_default": "'hindi'::text"
				},
				{
					"column_name": "tags",
					"data_type": "ARRAY",
					"is_nullable": "NO",
					"udt_name": "_text",
					"ordinal_position": 7,
					"column_default": "'{}'::text[]"
				},
				{
					"column_name": "read_time_minutes",
					"data_type": "integer",
					"is_nullable": "YES",
					"udt_name": "int4",
					"ordinal_position": 8,
					"column_default": null
				},
				{
					"column_name": "schema_version",
					"data_type": "integer",
					"is_nullable": "NO",
					"udt_name": "int4",
					"ordinal_position": 9,
					"column_default": "1"
				},
				{
					"column_name": "content_blocks",
					"data_type": "jsonb",
					"is_nullable": "NO",
					"udt_name": "jsonb",
					"ordinal_position": 10,
					"column_default": "'{\"blocks\": [], \"schema_version\": 1}'::jsonb"
				},
				{
					"column_name": "additional_image_urls",
					"data_type": "ARRAY",
					"is_nullable": "NO",
					"udt_name": "_text",
					"ordinal_position": 11,
					"column_default": "'{}'::text[]"
				},
				{
					"column_name": "author_type",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 12,
					"column_default": "'platform'::text"
				},
				{
					"column_name": "author_user_id",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 13,
					"column_default": null
				},
				{
					"column_name": "author_organization_id",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 14,
					"column_default": null
				},
				{
					"column_name": "author_parent_organization_id",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 15,
					"column_default": null
				},
				{
					"column_name": "status",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 16,
					"column_default": "'draft'::text"
				},
				{
					"column_name": "visibility",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 17,
					"column_default": "'public'::text"
				},
				{
					"column_name": "published_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "YES",
					"udt_name": "timestamptz",
					"ordinal_position": 18,
					"column_default": null
				},
				{
					"column_name": "scheduled_publish_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "YES",
					"udt_name": "timestamptz",
					"ordinal_position": 19,
					"column_default": null
				},
				{
					"column_name": "moderated_by",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 20,
					"column_default": null
				},
				{
					"column_name": "moderated_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "YES",
					"udt_name": "timestamptz",
					"ordinal_position": 21,
					"column_default": null
				},
				{
					"column_name": "moderation_note",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 22,
					"column_default": null
				},
				{
					"column_name": "view_count",
					"data_type": "integer",
					"is_nullable": "NO",
					"udt_name": "int4",
					"ordinal_position": 23,
					"column_default": "0"
				},
				{
					"column_name": "is_deleted",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 24,
					"column_default": "false"
				},
				{
					"column_name": "created_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 25,
					"column_default": "now()"
				},
				{
					"column_name": "updated_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 26,
					"column_default": "now()"
				}
			]
		},
		{
			"table_name": "case_studies_preview",
			"columns": [
				{
					"column_name": "id",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 1,
					"column_default": null
				},
				{
					"column_name": "title",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 2,
					"column_default": null
				},
				{
					"column_name": "slug",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 3,
					"column_default": null
				},
				{
					"column_name": "cover_image_url",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 4,
					"column_default": null
				},
				{
					"column_name": "short_description",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 5,
					"column_default": null
				},
				{
					"column_name": "language",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 6,
					"column_default": null
				},
				{
					"column_name": "tags",
					"data_type": "ARRAY",
					"is_nullable": "YES",
					"udt_name": "_text",
					"ordinal_position": 7,
					"column_default": null
				},
				{
					"column_name": "read_time_minutes",
					"data_type": "integer",
					"is_nullable": "YES",
					"udt_name": "int4",
					"ordinal_position": 8,
					"column_default": null
				},
				{
					"column_name": "author_type",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 9,
					"column_default": null
				},
				{
					"column_name": "author_user_id",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 10,
					"column_default": null
				},
				{
					"column_name": "author_organization_id",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 11,
					"column_default": null
				},
				{
					"column_name": "author_parent_organization_id",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 12,
					"column_default": null
				},
				{
					"column_name": "view_count",
					"data_type": "integer",
					"is_nullable": "YES",
					"udt_name": "int4",
					"ordinal_position": 13,
					"column_default": null
				},
				{
					"column_name": "published_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "YES",
					"udt_name": "timestamptz",
					"ordinal_position": 14,
					"column_default": null
				},
				{
					"column_name": "created_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "YES",
					"udt_name": "timestamptz",
					"ordinal_position": 15,
					"column_default": null
				},
				{
					"column_name": "updated_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "YES",
					"udt_name": "timestamptz",
					"ordinal_position": 16,
					"column_default": null
				}
			]
		},
		{
			"table_name": "case_study_bookmarks",
			"columns": [
				{
					"column_name": "id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 1,
					"column_default": "gen_random_uuid()"
				},
				{
					"column_name": "case_study_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 2,
					"column_default": null
				},
				{
					"column_name": "user_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 3,
					"column_default": null
				},
				{
					"column_name": "created_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 4,
					"column_default": "now()"
				}
			]
		},
		{
			"table_name": "case_study_reactions",
			"columns": [
				{
					"column_name": "id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 1,
					"column_default": "gen_random_uuid()"
				},
				{
					"column_name": "case_study_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 2,
					"column_default": null
				},
				{
					"column_name": "user_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 3,
					"column_default": null
				},
				{
					"column_name": "reaction_type",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 4,
					"column_default": "'helpful'::text"
				},
				{
					"column_name": "created_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 5,
					"column_default": "now()"
				},
				{
					"column_name": "updated_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 6,
					"column_default": "now()"
				}
			]
		},
		{
			"table_name": "content_contributor_verifications",
			"columns": [
				{
					"column_name": "id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 1,
					"column_default": "gen_random_uuid()"
				},
				{
					"column_name": "contributor_type",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 2,
					"column_default": null
				},
				{
					"column_name": "user_id",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 3,
					"column_default": null
				},
				{
					"column_name": "organization_id",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 4,
					"column_default": null
				},
				{
					"column_name": "parent_organization_id",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 5,
					"column_default": null
				},
				{
					"column_name": "status",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 6,
					"column_default": "'pending'::text"
				},
				{
					"column_name": "applicant_note",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 7,
					"column_default": null
				},
				{
					"column_name": "reviewed_by",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 8,
					"column_default": null
				},
				{
					"column_name": "reviewed_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "YES",
					"udt_name": "timestamptz",
					"ordinal_position": 9,
					"column_default": null
				},
				{
					"column_name": "rejection_reason",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 10,
					"column_default": null
				},
				{
					"column_name": "suspension_reason",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 11,
					"column_default": null
				},
				{
					"column_name": "applied_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 12,
					"column_default": "now()"
				},
				{
					"column_name": "created_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 13,
					"column_default": "now()"
				},
				{
					"column_name": "updated_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 14,
					"column_default": "now()"
				}
			]
		},
		{
			"table_name": "experience_inspirations",
			"columns": [
				{
					"column_name": "id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 1,
					"column_default": "gen_random_uuid()"
				},
				{
					"column_name": "experience_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 2,
					"column_default": null
				},
				{
					"column_name": "user_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 3,
					"column_default": null
				},
				{
					"column_name": "created_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 4,
					"column_default": "now()"
				}
			]
		},
		{
			"table_name": "experiences",
			"columns": [
				{
					"column_name": "id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 1,
					"column_default": "gen_random_uuid()"
				},
				{
					"column_name": "title",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 2,
					"column_default": null
				},
				{
					"column_name": "cover_image_url",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 3,
					"column_default": null
				},
				{
					"column_name": "description",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 4,
					"column_default": null
				},
				{
					"column_name": "author_user_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 5,
					"column_default": null
				},
				{
					"column_name": "inspired_count",
					"data_type": "integer",
					"is_nullable": "NO",
					"udt_name": "int4",
					"ordinal_position": 6,
					"column_default": "0"
				},
				{
					"column_name": "status",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 7,
					"column_default": "'published'::text"
				},
				{
					"column_name": "created_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 8,
					"column_default": "now()"
				},
				{
					"column_name": "updated_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 9,
					"column_default": "now()"
				}
			]
		},
		{
			"table_name": "global_areas",
			"columns": [
				{
					"column_name": "id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 1,
					"column_default": "gen_random_uuid()"
				},
				{
					"column_name": "state_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 2,
					"column_default": null
				},
				{
					"column_name": "district_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 3,
					"column_default": null
				},
				{
					"column_name": "tehsil_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 4,
					"column_default": null
				},
				{
					"column_name": "name",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 5,
					"column_default": null
				},
				{
					"column_name": "code",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 6,
					"column_default": null
				},
				{
					"column_name": "is_active",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 7,
					"column_default": "true"
				},
				{
					"column_name": "created_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 8,
					"column_default": "now()"
				},
				{
					"column_name": "updated_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 9,
					"column_default": "now()"
				},
				{
					"column_name": "panchayat_id",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 10,
					"column_default": null
				}
			]
		},
		{
			"table_name": "global_attendance_status",
			"columns": [
				{
					"column_name": "id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 1,
					"column_default": "gen_random_uuid()"
				},
				{
					"column_name": "name",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 2,
					"column_default": null
				},
				{
					"column_name": "code",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 3,
					"column_default": null
				},
				{
					"column_name": "is_active",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 4,
					"column_default": "true"
				},
				{
					"column_name": "created_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 5,
					"column_default": "now()"
				},
				{
					"column_name": "updated_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 6,
					"column_default": "now()"
				}
			]
		},
		{
			"table_name": "global_blood_groups",
			"columns": [
				{
					"column_name": "id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 1,
					"column_default": "gen_random_uuid()"
				},
				{
					"column_name": "name",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 2,
					"column_default": null
				},
				{
					"column_name": "is_active",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 3,
					"column_default": "true"
				},
				{
					"column_name": "created_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 4,
					"column_default": "now()"
				},
				{
					"column_name": "updated_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 5,
					"column_default": "now()"
				}
			]
		},
		{
			"table_name": "global_boards",
			"columns": [
				{
					"column_name": "id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 1,
					"column_default": "gen_random_uuid()"
				},
				{
					"column_name": "name",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 2,
					"column_default": null
				},
				{
					"column_name": "code",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 3,
					"column_default": null
				},
				{
					"column_name": "description",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 4,
					"column_default": null
				},
				{
					"column_name": "is_active",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 5,
					"column_default": "true"
				},
				{
					"column_name": "created_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 6,
					"column_default": "now()"
				},
				{
					"column_name": "updated_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 7,
					"column_default": "now()"
				},
				{
					"column_name": "created_by",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 8,
					"column_default": null
				},
				{
					"column_name": "updated_by",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 9,
					"column_default": null
				}
			]
		},
		{
			"table_name": "global_classes",
			"columns": [
				{
					"column_name": "id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 1,
					"column_default": "gen_random_uuid()"
				},
				{
					"column_name": "name",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 2,
					"column_default": null
				},
				{
					"column_name": "code",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 3,
					"column_default": null
				},
				{
					"column_name": "level_order",
					"data_type": "integer",
					"is_nullable": "NO",
					"udt_name": "int4",
					"ordinal_position": 4,
					"column_default": null
				},
				{
					"column_name": "education_level",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 5,
					"column_default": null
				},
				{
					"column_name": "is_active",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 6,
					"column_default": "true"
				},
				{
					"column_name": "created_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 7,
					"column_default": "now()"
				},
				{
					"column_name": "updated_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 8,
					"column_default": "now()"
				},
				{
					"column_name": "created_by",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 9,
					"column_default": null
				},
				{
					"column_name": "updated_by",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 10,
					"column_default": null
				},
				{
					"column_name": "is_deleted",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 11,
					"column_default": "false"
				}
			]
		},
		{
			"table_name": "global_countries",
			"columns": [
				{
					"column_name": "id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 1,
					"column_default": "gen_random_uuid()"
				},
				{
					"column_name": "name",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 2,
					"column_default": null
				},
				{
					"column_name": "code",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 3,
					"column_default": null
				},
				{
					"column_name": "is_active",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 4,
					"column_default": "true"
				},
				{
					"column_name": "created_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 5,
					"column_default": "now()"
				},
				{
					"column_name": "updated_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 6,
					"column_default": "now()"
				}
			]
		},
		{
			"table_name": "global_districts",
			"columns": [
				{
					"column_name": "id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 1,
					"column_default": "gen_random_uuid()"
				},
				{
					"column_name": "state_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 2,
					"column_default": null
				},
				{
					"column_name": "name",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 3,
					"column_default": null
				},
				{
					"column_name": "code",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 4,
					"column_default": null
				},
				{
					"column_name": "is_active",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 5,
					"column_default": "true"
				},
				{
					"column_name": "created_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 6,
					"column_default": "now()"
				},
				{
					"column_name": "updated_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 7,
					"column_default": "now()"
				}
			]
		},
		{
			"table_name": "global_document_types",
			"columns": [
				{
					"column_name": "id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 1,
					"column_default": "gen_random_uuid()"
				},
				{
					"column_name": "code",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 2,
					"column_default": null
				},
				{
					"column_name": "name",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 3,
					"column_default": null
				},
				{
					"column_name": "category",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 4,
					"column_default": null
				},
				{
					"column_name": "is_active",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 5,
					"column_default": "true"
				},
				{
					"column_name": "is_deleted",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 6,
					"column_default": "false"
				},
				{
					"column_name": "created_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 7,
					"column_default": "now()"
				},
				{
					"column_name": "updated_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 8,
					"column_default": "now()"
				}
			]
		},
		{
			"table_name": "global_exam_subject_rules",
			"columns": [
				{
					"column_name": "id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 1,
					"column_default": "gen_random_uuid()"
				},
				{
					"column_name": "class_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 2,
					"column_default": null
				},
				{
					"column_name": "subject_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 3,
					"column_default": null
				},
				{
					"column_name": "exam_type_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 4,
					"column_default": null
				},
				{
					"column_name": "default_max_marks",
					"data_type": "numeric",
					"is_nullable": "NO",
					"udt_name": "numeric",
					"ordinal_position": 5,
					"column_default": "100"
				},
				{
					"column_name": "default_passing_marks",
					"data_type": "numeric",
					"is_nullable": "NO",
					"udt_name": "numeric",
					"ordinal_position": 6,
					"column_default": "33"
				},
				{
					"column_name": "default_grading_system",
					"data_type": "jsonb",
					"is_nullable": "NO",
					"udt_name": "jsonb",
					"ordinal_position": 7,
					"column_default": "'{\"A\": 80, \"B\": 70, \"C\": 60, \"D\": 50, \"E\": 40, \"F\": 0, \"A+\": 90}'::jsonb"
				},
				{
					"column_name": "is_active",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 8,
					"column_default": "true"
				}
			]
		},
		{
			"table_name": "global_exam_types",
			"columns": [
				{
					"column_name": "id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 1,
					"column_default": "gen_random_uuid()"
				},
				{
					"column_name": "name",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 2,
					"column_default": null
				},
				{
					"column_name": "code",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 3,
					"column_default": null
				},
				{
					"column_name": "description",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 4,
					"column_default": null
				},
				{
					"column_name": "is_active",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 5,
					"column_default": "true"
				},
				{
					"column_name": "created_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 6,
					"column_default": "now()"
				},
				{
					"column_name": "updated_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 7,
					"column_default": "now()"
				},
				{
					"column_name": "created_by",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 8,
					"column_default": null
				},
				{
					"column_name": "updated_by",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 9,
					"column_default": null
				}
			]
		},
		{
			"table_name": "global_expense_types",
			"columns": [
				{
					"column_name": "id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 1,
					"column_default": "gen_random_uuid()"
				},
				{
					"column_name": "name",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 2,
					"column_default": null
				},
				{
					"column_name": "code",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 3,
					"column_default": null
				},
				{
					"column_name": "is_active",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 4,
					"column_default": "true"
				},
				{
					"column_name": "created_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 5,
					"column_default": "now()"
				},
				{
					"column_name": "updated_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 6,
					"column_default": "now()"
				},
				{
					"column_name": "reference_type",
					"data_type": "USER-DEFINED",
					"is_nullable": "YES",
					"udt_name": "expense_reference_type",
					"ordinal_position": 7,
					"column_default": null
				}
			]
		},
		{
			"table_name": "global_facilities",
			"columns": [
				{
					"column_name": "id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 1,
					"column_default": "gen_random_uuid()"
				},
				{
					"column_name": "name",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 2,
					"column_default": null
				},
				{
					"column_name": "code",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 3,
					"column_default": null
				},
				{
					"column_name": "description",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 4,
					"column_default": null
				},
				{
					"column_name": "is_active",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 5,
					"column_default": "true"
				},
				{
					"column_name": "created_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 6,
					"column_default": "now()"
				},
				{
					"column_name": "updated_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 7,
					"column_default": "now()"
				}
			]
		},
		{
			"table_name": "global_fee_heads",
			"columns": [
				{
					"column_name": "id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 1,
					"column_default": "gen_random_uuid()"
				},
				{
					"column_name": "name",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 2,
					"column_default": null
				},
				{
					"column_name": "code",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 3,
					"column_default": null
				},
				{
					"column_name": "description",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 4,
					"column_default": null
				},
				{
					"column_name": "is_active",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 5,
					"column_default": "true"
				},
				{
					"column_name": "created_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 6,
					"column_default": "now()"
				},
				{
					"column_name": "updated_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 7,
					"column_default": "now()"
				},
				{
					"column_name": "created_by",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 8,
					"column_default": null
				},
				{
					"column_name": "updated_by",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 9,
					"column_default": null
				},
				{
					"column_name": "is_additional",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 10,
					"column_default": "false"
				}
			]
		},
		{
			"table_name": "global_journey_mcqs",
			"columns": [
				{
					"column_name": "id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 1,
					"column_default": "gen_random_uuid()"
				},
				{
					"column_name": "template_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 2,
					"column_default": null
				},
				{
					"column_name": "day_number",
					"data_type": "integer",
					"is_nullable": "NO",
					"udt_name": "int4",
					"ordinal_position": 3,
					"column_default": null
				},
				{
					"column_name": "question_text",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 4,
					"column_default": null
				},
				{
					"column_name": "option_a",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 5,
					"column_default": null
				},
				{
					"column_name": "option_b",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 6,
					"column_default": null
				},
				{
					"column_name": "option_c",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 7,
					"column_default": null
				},
				{
					"column_name": "option_d",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 8,
					"column_default": null
				},
				{
					"column_name": "correct_option",
					"data_type": "character",
					"is_nullable": "NO",
					"udt_name": "bpchar",
					"ordinal_position": 9,
					"column_default": null
				},
				{
					"column_name": "explanation",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 10,
					"column_default": null
				},
				{
					"column_name": "difficulty_level",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 11,
					"column_default": "'medium'::text"
				},
				{
					"column_name": "points",
					"data_type": "integer",
					"is_nullable": "NO",
					"udt_name": "int4",
					"ordinal_position": 12,
					"column_default": "1"
				},
				{
					"column_name": "is_active",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 13,
					"column_default": "true"
				},
				{
					"column_name": "is_deleted",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 14,
					"column_default": "false"
				},
				{
					"column_name": "created_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 15,
					"column_default": "now()"
				},
				{
					"column_name": "updated_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 16,
					"column_default": "now()"
				},
				{
					"column_name": "created_by",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 17,
					"column_default": null
				},
				{
					"column_name": "updated_by",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 18,
					"column_default": null
				}
			]
		},
		{
			"table_name": "global_journey_tasks",
			"columns": [
				{
					"column_name": "id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 1,
					"column_default": "gen_random_uuid()"
				},
				{
					"column_name": "template_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 2,
					"column_default": null
				},
				{
					"column_name": "day_number",
					"data_type": "integer",
					"is_nullable": "NO",
					"udt_name": "int4",
					"ordinal_position": 3,
					"column_default": null
				},
				{
					"column_name": "task_title",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 4,
					"column_default": null
				},
				{
					"column_name": "task_description",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 5,
					"column_default": null
				},
				{
					"column_name": "task_type",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 6,
					"column_default": "'read'::text"
				},
				{
					"column_name": "sort_order",
					"data_type": "integer",
					"is_nullable": "NO",
					"udt_name": "int4",
					"ordinal_position": 7,
					"column_default": "1"
				},
				{
					"column_name": "is_active",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 8,
					"column_default": "true"
				},
				{
					"column_name": "is_deleted",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 9,
					"column_default": "false"
				},
				{
					"column_name": "created_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 10,
					"column_default": "now()"
				},
				{
					"column_name": "updated_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 11,
					"column_default": "now()"
				},
				{
					"column_name": "created_by",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 12,
					"column_default": null
				},
				{
					"column_name": "updated_by",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 13,
					"column_default": null
				}
			]
		},
		{
			"table_name": "global_journey_templates",
			"columns": [
				{
					"column_name": "id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 1,
					"column_default": "gen_random_uuid()"
				},
				{
					"column_name": "title",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 2,
					"column_default": null
				},
				{
					"column_name": "description",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 3,
					"column_default": null
				},
				{
					"column_name": "duration_days",
					"data_type": "integer",
					"is_nullable": "NO",
					"udt_name": "int4",
					"ordinal_position": 4,
					"column_default": null
				},
				{
					"column_name": "category",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 5,
					"column_default": null
				},
				{
					"column_name": "version",
					"data_type": "integer",
					"is_nullable": "NO",
					"udt_name": "int4",
					"ordinal_position": 6,
					"column_default": "1"
				},
				{
					"column_name": "is_active",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 7,
					"column_default": "true"
				},
				{
					"column_name": "is_deleted",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 8,
					"column_default": "false"
				},
				{
					"column_name": "created_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 9,
					"column_default": "now()"
				},
				{
					"column_name": "updated_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 10,
					"column_default": "now()"
				},
				{
					"column_name": "created_by",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 11,
					"column_default": null
				},
				{
					"column_name": "updated_by",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 12,
					"column_default": null
				}
			]
		},
		{
			"table_name": "global_languages",
			"columns": [
				{
					"column_name": "id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 1,
					"column_default": "gen_random_uuid()"
				},
				{
					"column_name": "name",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 2,
					"column_default": null
				},
				{
					"column_name": "code",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 3,
					"column_default": null
				},
				{
					"column_name": "is_active",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 4,
					"column_default": "true"
				},
				{
					"column_name": "created_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 5,
					"column_default": "now()"
				},
				{
					"column_name": "updated_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 6,
					"column_default": "now()"
				}
			]
		},
		{
			"table_name": "global_marksheet_templates",
			"columns": [
				{
					"column_name": "id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 1,
					"column_default": "gen_random_uuid()"
				},
				{
					"column_name": "board_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 2,
					"column_default": null
				},
				{
					"column_name": "class_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 3,
					"column_default": null
				},
				{
					"column_name": "name",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 4,
					"column_default": null
				},
				{
					"column_name": "code",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 5,
					"column_default": null
				},
				{
					"column_name": "template_json",
					"data_type": "jsonb",
					"is_nullable": "NO",
					"udt_name": "jsonb",
					"ordinal_position": 6,
					"column_default": null
				},
				{
					"column_name": "is_default",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 7,
					"column_default": "false"
				},
				{
					"column_name": "is_active",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 8,
					"column_default": "true"
				},
				{
					"column_name": "created_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 9,
					"column_default": "now()"
				},
				{
					"column_name": "updated_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 10,
					"column_default": "now()"
				},
				{
					"column_name": "created_by",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 11,
					"column_default": null
				},
				{
					"column_name": "updated_by",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 12,
					"column_default": null
				}
			]
		},
		{
			"table_name": "global_mediums",
			"columns": [
				{
					"column_name": "id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 1,
					"column_default": "gen_random_uuid()"
				},
				{
					"column_name": "board_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 2,
					"column_default": null
				},
				{
					"column_name": "name",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 3,
					"column_default": null
				},
				{
					"column_name": "code",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 4,
					"column_default": null
				},
				{
					"column_name": "is_active",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 5,
					"column_default": "true"
				},
				{
					"column_name": "created_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 6,
					"column_default": "now()"
				},
				{
					"column_name": "updated_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 7,
					"column_default": "now()"
				},
				{
					"column_name": "created_by",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 8,
					"column_default": null
				},
				{
					"column_name": "updated_by",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 9,
					"column_default": null
				}
			]
		},
		{
			"table_name": "global_organization_parent_types",
			"columns": [
				{
					"column_name": "id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 1,
					"column_default": "gen_random_uuid()"
				},
				{
					"column_name": "name",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 2,
					"column_default": null
				},
				{
					"column_name": "code",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 3,
					"column_default": null
				},
				{
					"column_name": "description",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 4,
					"column_default": null
				},
				{
					"column_name": "is_active",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 5,
					"column_default": "true"
				},
				{
					"column_name": "created_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 6,
					"column_default": "now()"
				},
				{
					"column_name": "updated_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 7,
					"column_default": "now()"
				}
			]
		},
		{
			"table_name": "global_organization_types",
			"columns": [
				{
					"column_name": "id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 1,
					"column_default": "gen_random_uuid()"
				},
				{
					"column_name": "name",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 2,
					"column_default": null
				},
				{
					"column_name": "code",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 3,
					"column_default": null
				},
				{
					"column_name": "description",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 4,
					"column_default": null
				},
				{
					"column_name": "is_active",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 5,
					"column_default": "true"
				},
				{
					"column_name": "created_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 6,
					"column_default": "now()"
				},
				{
					"column_name": "updated_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 7,
					"column_default": "now()"
				},
				{
					"column_name": "is_own",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 8,
					"column_default": "true"
				}
			]
		},
		{
			"table_name": "global_panchayats",
			"columns": [
				{
					"column_name": "id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 1,
					"column_default": "gen_random_uuid()"
				},
				{
					"column_name": "state_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 2,
					"column_default": null
				},
				{
					"column_name": "district_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 3,
					"column_default": null
				},
				{
					"column_name": "tehsil_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 4,
					"column_default": null
				},
				{
					"column_name": "name",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 5,
					"column_default": null
				},
				{
					"column_name": "code",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 6,
					"column_default": null
				},
				{
					"column_name": "is_active",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 7,
					"column_default": "true"
				},
				{
					"column_name": "created_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "YES",
					"udt_name": "timestamptz",
					"ordinal_position": 8,
					"column_default": "now()"
				},
				{
					"column_name": "updated_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "YES",
					"udt_name": "timestamptz",
					"ordinal_position": 9,
					"column_default": "now()"
				}
			]
		},
		{
			"table_name": "global_relationship_types",
			"columns": [
				{
					"column_name": "id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 1,
					"column_default": "gen_random_uuid()"
				},
				{
					"column_name": "name",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 2,
					"column_default": null
				},
				{
					"column_name": "code",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 3,
					"column_default": null
				},
				{
					"column_name": "is_active",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 4,
					"column_default": "true"
				},
				{
					"column_name": "created_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 5,
					"column_default": "now()"
				},
				{
					"column_name": "updated_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 6,
					"column_default": "now()"
				}
			]
		},
		{
			"table_name": "global_result_status",
			"columns": [
				{
					"column_name": "id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 1,
					"column_default": "gen_random_uuid()"
				},
				{
					"column_name": "name",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 2,
					"column_default": null
				},
				{
					"column_name": "code",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 3,
					"column_default": null
				},
				{
					"column_name": "is_active",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 4,
					"column_default": "true"
				},
				{
					"column_name": "created_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 5,
					"column_default": "now()"
				},
				{
					"column_name": "updated_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 6,
					"column_default": "now()"
				}
			]
		},
		{
			"table_name": "global_sessions",
			"columns": [
				{
					"column_name": "id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 1,
					"column_default": "gen_random_uuid()"
				},
				{
					"column_name": "name",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 2,
					"column_default": null
				},
				{
					"column_name": "is_active",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 3,
					"column_default": "true"
				},
				{
					"column_name": "is_deleted",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 4,
					"column_default": "false"
				},
				{
					"column_name": "created_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 5,
					"column_default": "now()"
				},
				{
					"column_name": "updated_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 6,
					"column_default": "now()"
				},
				{
					"column_name": "starting_date",
					"data_type": "date",
					"is_nullable": "YES",
					"udt_name": "date",
					"ordinal_position": 7,
					"column_default": null
				},
				{
					"column_name": "ending_date",
					"data_type": "date",
					"is_nullable": "YES",
					"udt_name": "date",
					"ordinal_position": 8,
					"column_default": null
				},
				{
					"column_name": "update_before_ending_days",
					"data_type": "integer",
					"is_nullable": "YES",
					"udt_name": "int4",
					"ordinal_position": 9,
					"column_default": "30"
				}
			]
		},
		{
			"table_name": "global_staff_roles",
			"columns": [
				{
					"column_name": "id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 1,
					"column_default": "gen_random_uuid()"
				},
				{
					"column_name": "name",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 2,
					"column_default": null
				},
				{
					"column_name": "code",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 3,
					"column_default": null
				},
				{
					"column_name": "description",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 4,
					"column_default": null
				},
				{
					"column_name": "is_active",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 5,
					"column_default": "true"
				},
				{
					"column_name": "created_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 6,
					"column_default": "now()"
				},
				{
					"column_name": "updated_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 7,
					"column_default": "now()"
				},
				{
					"column_name": "is_deleted",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 8,
					"column_default": "false"
				}
			]
		},
		{
			"table_name": "global_states",
			"columns": [
				{
					"column_name": "id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 1,
					"column_default": "gen_random_uuid()"
				},
				{
					"column_name": "country_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 2,
					"column_default": null
				},
				{
					"column_name": "name",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 3,
					"column_default": null
				},
				{
					"column_name": "code",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 4,
					"column_default": null
				},
				{
					"column_name": "is_active",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 5,
					"column_default": "true"
				},
				{
					"column_name": "created_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 6,
					"column_default": "now()"
				},
				{
					"column_name": "updated_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 7,
					"column_default": "now()"
				}
			]
		},
		{
			"table_name": "global_student_categories",
			"columns": [
				{
					"column_name": "id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 1,
					"column_default": "gen_random_uuid()"
				},
				{
					"column_name": "name",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 2,
					"column_default": null
				},
				{
					"column_name": "code",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 3,
					"column_default": null
				},
				{
					"column_name": "description",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 4,
					"column_default": null
				},
				{
					"column_name": "is_active",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 5,
					"column_default": "true"
				},
				{
					"column_name": "created_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 6,
					"column_default": "now()"
				},
				{
					"column_name": "updated_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 7,
					"column_default": "now()"
				}
			]
		},
		{
			"table_name": "global_student_status",
			"columns": [
				{
					"column_name": "id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 1,
					"column_default": "gen_random_uuid()"
				},
				{
					"column_name": "name",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 2,
					"column_default": null
				},
				{
					"column_name": "code",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 3,
					"column_default": null
				},
				{
					"column_name": "is_active",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 4,
					"column_default": "true"
				},
				{
					"column_name": "created_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 5,
					"column_default": "now()"
				},
				{
					"column_name": "updated_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 6,
					"column_default": "now()"
				}
			]
		},
		{
			"table_name": "global_subjects",
			"columns": [
				{
					"column_name": "id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 1,
					"column_default": "gen_random_uuid()"
				},
				{
					"column_name": "name",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 2,
					"column_default": null
				},
				{
					"column_name": "code",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 3,
					"column_default": null
				},
				{
					"column_name": "subject_type",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 4,
					"column_default": null
				},
				{
					"column_name": "is_active",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 5,
					"column_default": "true"
				},
				{
					"column_name": "created_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 6,
					"column_default": "now()"
				},
				{
					"column_name": "updated_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 7,
					"column_default": "now()"
				},
				{
					"column_name": "created_by",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 8,
					"column_default": null
				},
				{
					"column_name": "updated_by",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 9,
					"column_default": null
				}
			]
		},
		{
			"table_name": "global_tehsils",
			"columns": [
				{
					"column_name": "id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 1,
					"column_default": "gen_random_uuid()"
				},
				{
					"column_name": "state_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 2,
					"column_default": null
				},
				{
					"column_name": "district_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 3,
					"column_default": null
				},
				{
					"column_name": "name",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 4,
					"column_default": null
				},
				{
					"column_name": "code",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 5,
					"column_default": null
				},
				{
					"column_name": "is_active",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 6,
					"column_default": "true"
				},
				{
					"column_name": "created_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 7,
					"column_default": "now()"
				},
				{
					"column_name": "updated_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 8,
					"column_default": "now()"
				}
			]
		},
		{
			"table_name": "messages",
			"columns": [
				{
					"column_name": "id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 1,
					"column_default": "gen_random_uuid()"
				},
				{
					"column_name": "room_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 2,
					"column_default": null
				},
				{
					"column_name": "user_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 3,
					"column_default": null
				},
				{
					"column_name": "content",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 4,
					"column_default": null
				},
				{
					"column_name": "is_hidden",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 5,
					"column_default": "false"
				},
				{
					"column_name": "is_deleted",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 6,
					"column_default": "false"
				},
				{
					"column_name": "created_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 7,
					"column_default": "now()"
				},
				{
					"column_name": "updated_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 8,
					"column_default": "now()"
				}
			]
		},
		{
			"table_name": "moderation_settings",
			"columns": [
				{
					"column_name": "id",
					"data_type": "integer",
					"is_nullable": "NO",
					"udt_name": "int4",
					"ordinal_position": 1,
					"column_default": "1"
				},
				{
					"column_name": "blocked_keywords",
					"data_type": "jsonb",
					"is_nullable": "NO",
					"udt_name": "jsonb",
					"ordinal_position": 2,
					"column_default": "'[]'::jsonb"
				},
				{
					"column_name": "is_active",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 3,
					"column_default": "true"
				},
				{
					"column_name": "created_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 4,
					"column_default": "now()"
				},
				{
					"column_name": "updated_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 5,
					"column_default": "now()"
				}
			]
		},
		{
			"table_name": "organization_attendance_status",
			"columns": [
				{
					"column_name": "id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 1,
					"column_default": "gen_random_uuid()"
				},
				{
					"column_name": "organization_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 2,
					"column_default": null
				},
				{
					"column_name": "attendance_status_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 3,
					"column_default": null
				},
				{
					"column_name": "salary_payout_percentage",
					"data_type": "numeric",
					"is_nullable": "NO",
					"udt_name": "numeric",
					"ordinal_position": 4,
					"column_default": "100.0"
				},
				{
					"column_name": "is_active",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 5,
					"column_default": "true"
				},
				{
					"column_name": "created_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 6,
					"column_default": "now()"
				}
			]
		},
		{
			"table_name": "organization_boards",
			"columns": [
				{
					"column_name": "id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 1,
					"column_default": "gen_random_uuid()"
				},
				{
					"column_name": "organization_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 2,
					"column_default": null
				},
				{
					"column_name": "board_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 3,
					"column_default": null
				},
				{
					"column_name": "is_active",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 4,
					"column_default": "true"
				},
				{
					"column_name": "created_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 5,
					"column_default": "now()"
				},
				{
					"column_name": "is_deleted",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 6,
					"column_default": "false"
				},
				{
					"column_name": "updated_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 7,
					"column_default": "now()"
				},
				{
					"column_name": "created_by",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 8,
					"column_default": null
				},
				{
					"column_name": "updated_by",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 9,
					"column_default": null
				}
			]
		},
		{
			"table_name": "organization_bus_child_assignments",
			"columns": [
				{
					"column_name": "id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 1,
					"column_default": "gen_random_uuid()"
				},
				{
					"column_name": "parent_organization_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 2,
					"column_default": null
				},
				{
					"column_name": "organization_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 3,
					"column_default": null
				},
				{
					"column_name": "active_session_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 4,
					"column_default": null
				},
				{
					"column_name": "bus_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 5,
					"column_default": null
				},
				{
					"column_name": "is_active",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 6,
					"column_default": "true"
				},
				{
					"column_name": "is_deleted",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 7,
					"column_default": "false"
				},
				{
					"column_name": "created_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 8,
					"column_default": "now()"
				},
				{
					"column_name": "updated_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 9,
					"column_default": "now()"
				},
				{
					"column_name": "created_by",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 10,
					"column_default": null
				},
				{
					"column_name": "updated_by",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 11,
					"column_default": null
				}
			]
		},
		{
			"table_name": "organization_classes",
			"columns": [
				{
					"column_name": "id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 1,
					"column_default": "gen_random_uuid()"
				},
				{
					"column_name": "organization_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 2,
					"column_default": null
				},
				{
					"column_name": "class_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 3,
					"column_default": null
				},
				{
					"column_name": "custom_class_name",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 4,
					"column_default": null
				},
				{
					"column_name": "next_promotion_class_id",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 5,
					"column_default": null
				},
				{
					"column_name": "is_active",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 6,
					"column_default": "true"
				},
				{
					"column_name": "created_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 7,
					"column_default": "now()"
				},
				{
					"column_name": "is_deleted",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 8,
					"column_default": "false"
				},
				{
					"column_name": "updated_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 9,
					"column_default": "now()"
				},
				{
					"column_name": "created_by",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 10,
					"column_default": null
				},
				{
					"column_name": "updated_by",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 11,
					"column_default": null
				}
			]
		},
		{
			"table_name": "organization_content_assignments",
			"columns": [
				{
					"column_name": "id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 1,
					"column_default": "gen_random_uuid()"
				},
				{
					"column_name": "content_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 2,
					"column_default": null
				},
				{
					"column_name": "target_organization_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 3,
					"column_default": null
				},
				{
					"column_name": "assigned_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 4,
					"column_default": "now()"
				}
			]
		},
		{
			"table_name": "organization_contents",
			"columns": [
				{
					"column_name": "id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 1,
					"column_default": "gen_random_uuid()"
				},
				{
					"column_name": "content_type",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 2,
					"column_default": null
				},
				{
					"column_name": "publisher_type",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 3,
					"column_default": null
				},
				{
					"column_name": "parent_organization_id",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 4,
					"column_default": null
				},
				{
					"column_name": "organization_id",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 5,
					"column_default": null
				},
				{
					"column_name": "session_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 6,
					"column_default": null
				},
				{
					"column_name": "title",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 7,
					"column_default": null
				},
				{
					"column_name": "description",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 8,
					"column_default": null
				},
				{
					"column_name": "image_url",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 9,
					"column_default": null
				},
				{
					"column_name": "target_scope",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 10,
					"column_default": "'self'::text"
				},
				{
					"column_name": "target_roles",
					"data_type": "ARRAY",
					"is_nullable": "NO",
					"udt_name": "_text",
					"ordinal_position": 11,
					"column_default": "'{}'::text[]"
				},
				{
					"column_name": "status",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 12,
					"column_default": "'published'::text"
				},
				{
					"column_name": "is_active",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 13,
					"column_default": "true"
				},
				{
					"column_name": "is_deleted",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 14,
					"column_default": "false"
				},
				{
					"column_name": "created_by",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 15,
					"column_default": null
				},
				{
					"column_name": "updated_by",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 16,
					"column_default": null
				},
				{
					"column_name": "created_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 17,
					"column_default": "now()"
				},
				{
					"column_name": "updated_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 18,
					"column_default": "now()"
				}
			]
		},
		{
			"table_name": "organization_exam_subject_settings",
			"columns": [
				{
					"column_name": "id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 1,
					"column_default": "gen_random_uuid()"
				},
				{
					"column_name": "organization_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 2,
					"column_default": null
				},
				{
					"column_name": "active_session_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 3,
					"column_default": null
				},
				{
					"column_name": "exam_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 4,
					"column_default": null
				},
				{
					"column_name": "class_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 5,
					"column_default": null
				},
				{
					"column_name": "subject_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 6,
					"column_default": null
				},
				{
					"column_name": "max_marks",
					"data_type": "numeric",
					"is_nullable": "YES",
					"udt_name": "numeric",
					"ordinal_position": 7,
					"column_default": null
				},
				{
					"column_name": "minimum_passing_marks",
					"data_type": "numeric",
					"is_nullable": "YES",
					"udt_name": "numeric",
					"ordinal_position": 8,
					"column_default": null
				},
				{
					"column_name": "grading_system",
					"data_type": "jsonb",
					"is_nullable": "YES",
					"udt_name": "jsonb",
					"ordinal_position": 9,
					"column_default": null
				},
				{
					"column_name": "is_deleted",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 10,
					"column_default": "false"
				},
				{
					"column_name": "created_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 11,
					"column_default": "now()"
				}
			]
		},
		{
			"table_name": "organization_exams",
			"columns": [
				{
					"column_name": "id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 1,
					"column_default": "gen_random_uuid()"
				},
				{
					"column_name": "organization_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 2,
					"column_default": null
				},
				{
					"column_name": "active_session_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 3,
					"column_default": null
				},
				{
					"column_name": "exam_type_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 4,
					"column_default": null
				},
				{
					"column_name": "name",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 5,
					"column_default": null
				},
				{
					"column_name": "start_date",
					"data_type": "date",
					"is_nullable": "YES",
					"udt_name": "date",
					"ordinal_position": 6,
					"column_default": null
				},
				{
					"column_name": "end_date",
					"data_type": "date",
					"is_nullable": "YES",
					"udt_name": "date",
					"ordinal_position": 7,
					"column_default": null
				},
				{
					"column_name": "is_active",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 8,
					"column_default": "true"
				},
				{
					"column_name": "is_deleted",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 9,
					"column_default": "false"
				},
				{
					"column_name": "created_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 10,
					"column_default": "now()"
				}
			]
		},
		{
			"table_name": "organization_fee_assignments",
			"columns": [
				{
					"column_name": "id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 1,
					"column_default": "gen_random_uuid()"
				},
				{
					"column_name": "organization_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 2,
					"column_default": null
				},
				{
					"column_name": "session_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 3,
					"column_default": null
				},
				{
					"column_name": "organization_class_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 4,
					"column_default": null
				},
				{
					"column_name": "fee_head_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 5,
					"column_default": null
				},
				{
					"column_name": "amount",
					"data_type": "numeric",
					"is_nullable": "NO",
					"udt_name": "numeric",
					"ordinal_position": 6,
					"column_default": "0"
				},
				{
					"column_name": "is_active",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 7,
					"column_default": "true"
				},
				{
					"column_name": "is_deleted",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 8,
					"column_default": "false"
				},
				{
					"column_name": "created_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 9,
					"column_default": "now()"
				},
				{
					"column_name": "updated_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 10,
					"column_default": "now()"
				},
				{
					"column_name": "created_by",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 11,
					"column_default": null
				},
				{
					"column_name": "updated_by",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 12,
					"column_default": null
				}
			]
		},
		{
			"table_name": "organization_guardian_user_links",
			"columns": [
				{
					"column_name": "id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 1,
					"column_default": "gen_random_uuid()"
				},
				{
					"column_name": "organization_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 2,
					"column_default": null
				},
				{
					"column_name": "active_session_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 3,
					"column_default": null
				},
				{
					"column_name": "guardian_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 4,
					"column_default": null
				},
				{
					"column_name": "user_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 5,
					"column_default": null
				},
				{
					"column_name": "is_approved",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 6,
					"column_default": "false"
				},
				{
					"column_name": "approved_by",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 7,
					"column_default": null
				},
				{
					"column_name": "approved_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "YES",
					"udt_name": "timestamptz",
					"ordinal_position": 8,
					"column_default": null
				},
				{
					"column_name": "is_active",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 9,
					"column_default": "true"
				},
				{
					"column_name": "is_deleted",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 10,
					"column_default": "false"
				},
				{
					"column_name": "created_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 11,
					"column_default": "now()"
				},
				{
					"column_name": "updated_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 12,
					"column_default": "now()"
				}
			]
		},
		{
			"table_name": "organization_guardians",
			"columns": [
				{
					"column_name": "id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 1,
					"column_default": "gen_random_uuid()"
				},
				{
					"column_name": "organization_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 2,
					"column_default": null
				},
				{
					"column_name": "active_session_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 3,
					"column_default": null
				},
				{
					"column_name": "name",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 4,
					"column_default": null
				},
				{
					"column_name": "mobile_number",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 5,
					"column_default": null
				},
				{
					"column_name": "relationship_type_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 6,
					"column_default": null
				},
				{
					"column_name": "is_active",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 7,
					"column_default": "true"
				},
				{
					"column_name": "is_deleted",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 8,
					"column_default": "false"
				},
				{
					"column_name": "created_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 9,
					"column_default": "now()"
				},
				{
					"column_name": "updated_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 10,
					"column_default": "now()"
				},
				{
					"column_name": "created_by",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 11,
					"column_default": null
				},
				{
					"column_name": "updated_by",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 12,
					"column_default": null
				}
			]
		},
		{
			"table_name": "organization_holidays",
			"columns": [
				{
					"column_name": "id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 1,
					"column_default": "gen_random_uuid()"
				},
				{
					"column_name": "parent_organization_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 2,
					"column_default": null
				},
				{
					"column_name": "organization_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 3,
					"column_default": null
				},
				{
					"column_name": "active_session_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 4,
					"column_default": null
				},
				{
					"column_name": "name",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 5,
					"column_default": null
				},
				{
					"column_name": "start_date",
					"data_type": "date",
					"is_nullable": "NO",
					"udt_name": "date",
					"ordinal_position": 6,
					"column_default": null
				},
				{
					"column_name": "end_date",
					"data_type": "date",
					"is_nullable": "NO",
					"udt_name": "date",
					"ordinal_position": 7,
					"column_default": null
				},
				{
					"column_name": "description",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 8,
					"column_default": null
				},
				{
					"column_name": "is_active",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 9,
					"column_default": "true"
				},
				{
					"column_name": "is_deleted",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 10,
					"column_default": "false"
				},
				{
					"column_name": "created_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 11,
					"column_default": "now()"
				},
				{
					"column_name": "updated_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 12,
					"column_default": "now()"
				},
				{
					"column_name": "created_by",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 13,
					"column_default": null
				},
				{
					"column_name": "updated_by",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 14,
					"column_default": null
				}
			]
		},
		{
			"table_name": "organization_inquiries",
			"columns": [
				{
					"column_name": "id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 1,
					"column_default": "gen_random_uuid()"
				},
				{
					"column_name": "organization_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 2,
					"column_default": null
				},
				{
					"column_name": "active_session_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 3,
					"column_default": null
				},
				{
					"column_name": "student_name",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 4,
					"column_default": null
				},
				{
					"column_name": "guardian_name",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 5,
					"column_default": null
				},
				{
					"column_name": "mobile_number",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 6,
					"column_default": null
				},
				{
					"column_name": "class_id",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 7,
					"column_default": null
				},
				{
					"column_name": "inquiry_date",
					"data_type": "date",
					"is_nullable": "NO",
					"udt_name": "date",
					"ordinal_position": 8,
					"column_default": "CURRENT_DATE"
				},
				{
					"column_name": "status",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 9,
					"column_default": "'Pending'::text"
				},
				{
					"column_name": "remarks",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 10,
					"column_default": null
				},
				{
					"column_name": "is_active",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 11,
					"column_default": "true"
				},
				{
					"column_name": "is_deleted",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 12,
					"column_default": "false"
				},
				{
					"column_name": "created_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 13,
					"column_default": "now()"
				},
				{
					"column_name": "updated_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 14,
					"column_default": "now()"
				},
				{
					"column_name": "created_by",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 15,
					"column_default": null
				},
				{
					"column_name": "updated_by",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 16,
					"column_default": null
				}
			]
		},
		{
			"table_name": "organization_journey_mcqs",
			"columns": [
				{
					"column_name": "id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 1,
					"column_default": "gen_random_uuid()"
				},
				{
					"column_name": "template_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 2,
					"column_default": null
				},
				{
					"column_name": "day_number",
					"data_type": "integer",
					"is_nullable": "NO",
					"udt_name": "int4",
					"ordinal_position": 3,
					"column_default": null
				},
				{
					"column_name": "question_text",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 4,
					"column_default": null
				},
				{
					"column_name": "option_a",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 5,
					"column_default": null
				},
				{
					"column_name": "option_b",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 6,
					"column_default": null
				},
				{
					"column_name": "option_c",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 7,
					"column_default": null
				},
				{
					"column_name": "option_d",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 8,
					"column_default": null
				},
				{
					"column_name": "correct_option",
					"data_type": "character",
					"is_nullable": "NO",
					"udt_name": "bpchar",
					"ordinal_position": 9,
					"column_default": null
				},
				{
					"column_name": "explanation",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 10,
					"column_default": null
				},
				{
					"column_name": "difficulty_level",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 11,
					"column_default": "'medium'::text"
				},
				{
					"column_name": "points",
					"data_type": "integer",
					"is_nullable": "NO",
					"udt_name": "int4",
					"ordinal_position": 12,
					"column_default": "1"
				},
				{
					"column_name": "is_active",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 13,
					"column_default": "true"
				},
				{
					"column_name": "is_deleted",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 14,
					"column_default": "false"
				},
				{
					"column_name": "created_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 15,
					"column_default": "now()"
				},
				{
					"column_name": "updated_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 16,
					"column_default": "now()"
				},
				{
					"column_name": "created_by",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 17,
					"column_default": null
				},
				{
					"column_name": "updated_by",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 18,
					"column_default": null
				}
			]
		},
		{
			"table_name": "organization_journey_tasks",
			"columns": [
				{
					"column_name": "id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 1,
					"column_default": "gen_random_uuid()"
				},
				{
					"column_name": "template_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 2,
					"column_default": null
				},
				{
					"column_name": "day_number",
					"data_type": "integer",
					"is_nullable": "NO",
					"udt_name": "int4",
					"ordinal_position": 3,
					"column_default": null
				},
				{
					"column_name": "task_title",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 4,
					"column_default": null
				},
				{
					"column_name": "task_description",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 5,
					"column_default": null
				},
				{
					"column_name": "task_type",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 6,
					"column_default": "'read'::text"
				},
				{
					"column_name": "sort_order",
					"data_type": "integer",
					"is_nullable": "NO",
					"udt_name": "int4",
					"ordinal_position": 7,
					"column_default": "1"
				},
				{
					"column_name": "is_active",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 8,
					"column_default": "true"
				},
				{
					"column_name": "is_deleted",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 9,
					"column_default": "false"
				},
				{
					"column_name": "created_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 10,
					"column_default": "now()"
				},
				{
					"column_name": "updated_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 11,
					"column_default": "now()"
				},
				{
					"column_name": "created_by",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 12,
					"column_default": null
				},
				{
					"column_name": "updated_by",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 13,
					"column_default": null
				}
			]
		},
		{
			"table_name": "organization_journey_templates",
			"columns": [
				{
					"column_name": "id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 1,
					"column_default": "gen_random_uuid()"
				},
				{
					"column_name": "organization_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 2,
					"column_default": null
				},
				{
					"column_name": "title",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 3,
					"column_default": null
				},
				{
					"column_name": "description",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 4,
					"column_default": null
				},
				{
					"column_name": "duration_days",
					"data_type": "integer",
					"is_nullable": "NO",
					"udt_name": "int4",
					"ordinal_position": 5,
					"column_default": null
				},
				{
					"column_name": "category",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 6,
					"column_default": null
				},
				{
					"column_name": "version",
					"data_type": "integer",
					"is_nullable": "NO",
					"udt_name": "int4",
					"ordinal_position": 7,
					"column_default": "1"
				},
				{
					"column_name": "is_active",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 8,
					"column_default": "true"
				},
				{
					"column_name": "is_deleted",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 9,
					"column_default": "false"
				},
				{
					"column_name": "created_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 10,
					"column_default": "now()"
				},
				{
					"column_name": "updated_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 11,
					"column_default": "now()"
				},
				{
					"column_name": "created_by",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 12,
					"column_default": null
				},
				{
					"column_name": "updated_by",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 13,
					"column_default": null
				}
			]
		},
		{
			"table_name": "organization_languages",
			"columns": [
				{
					"column_name": "id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 1,
					"column_default": "gen_random_uuid()"
				},
				{
					"column_name": "organization_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 2,
					"column_default": null
				},
				{
					"column_name": "language_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 3,
					"column_default": null
				},
				{
					"column_name": "is_active",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 4,
					"column_default": "true"
				},
				{
					"column_name": "created_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 5,
					"column_default": "now()"
				},
				{
					"column_name": "is_deleted",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 6,
					"column_default": "false"
				},
				{
					"column_name": "updated_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 7,
					"column_default": "now()"
				},
				{
					"column_name": "created_by",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 8,
					"column_default": null
				},
				{
					"column_name": "updated_by",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 9,
					"column_default": null
				}
			]
		},
		{
			"table_name": "organization_leaves",
			"columns": [
				{
					"column_name": "id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 1,
					"column_default": "gen_random_uuid()"
				},
				{
					"column_name": "parent_organization_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 2,
					"column_default": null
				},
				{
					"column_name": "organization_id",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 3,
					"column_default": null
				},
				{
					"column_name": "active_session_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 4,
					"column_default": null
				},
				{
					"column_name": "applicant_type",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 5,
					"column_default": null
				},
				{
					"column_name": "staff_id",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 6,
					"column_default": null
				},
				{
					"column_name": "student_id",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 7,
					"column_default": null
				},
				{
					"column_name": "leave_type",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 8,
					"column_default": null
				},
				{
					"column_name": "start_date",
					"data_type": "date",
					"is_nullable": "NO",
					"udt_name": "date",
					"ordinal_position": 9,
					"column_default": null
				},
				{
					"column_name": "end_date",
					"data_type": "date",
					"is_nullable": "NO",
					"udt_name": "date",
					"ordinal_position": 10,
					"column_default": null
				},
				{
					"column_name": "is_half_day",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 11,
					"column_default": "false"
				},
				{
					"column_name": "half_day_period",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 12,
					"column_default": null
				},
				{
					"column_name": "reason",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 13,
					"column_default": null
				},
				{
					"column_name": "status",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 14,
					"column_default": "'Pending'::text"
				},
				{
					"column_name": "action_remarks",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 15,
					"column_default": null
				},
				{
					"column_name": "action_by",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 16,
					"column_default": null
				},
				{
					"column_name": "action_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "YES",
					"udt_name": "timestamptz",
					"ordinal_position": 17,
					"column_default": null
				},
				{
					"column_name": "is_active",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 18,
					"column_default": "true"
				},
				{
					"column_name": "is_deleted",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 19,
					"column_default": "false"
				},
				{
					"column_name": "created_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 20,
					"column_default": "now()"
				},
				{
					"column_name": "updated_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 21,
					"column_default": "now()"
				},
				{
					"column_name": "created_by",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 22,
					"column_default": null
				},
				{
					"column_name": "updated_by",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 23,
					"column_default": null
				}
			]
		},
		{
			"table_name": "organization_mediums",
			"columns": [
				{
					"column_name": "id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 1,
					"column_default": "gen_random_uuid()"
				},
				{
					"column_name": "organization_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 2,
					"column_default": null
				},
				{
					"column_name": "medium_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 3,
					"column_default": null
				},
				{
					"column_name": "is_active",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 4,
					"column_default": "true"
				},
				{
					"column_name": "created_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 5,
					"column_default": "now()"
				},
				{
					"column_name": "is_deleted",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 6,
					"column_default": "false"
				},
				{
					"column_name": "updated_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 7,
					"column_default": "now()"
				},
				{
					"column_name": "created_by",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 8,
					"column_default": null
				},
				{
					"column_name": "updated_by",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 9,
					"column_default": null
				}
			]
		},
		{
			"table_name": "organization_parent_bus_live_locations",
			"columns": [
				{
					"column_name": "bus_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 1,
					"column_default": null
				},
				{
					"column_name": "parent_organization_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 2,
					"column_default": null
				},
				{
					"column_name": "active_session_id",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 3,
					"column_default": null
				},
				{
					"column_name": "latitude",
					"data_type": "double precision",
					"is_nullable": "NO",
					"udt_name": "float8",
					"ordinal_position": 4,
					"column_default": null
				},
				{
					"column_name": "longitude",
					"data_type": "double precision",
					"is_nullable": "NO",
					"udt_name": "float8",
					"ordinal_position": 5,
					"column_default": null
				},
				{
					"column_name": "speed",
					"data_type": "numeric",
					"is_nullable": "NO",
					"udt_name": "numeric",
					"ordinal_position": 6,
					"column_default": "0.0"
				},
				{
					"column_name": "updated_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 7,
					"column_default": "now()"
				}
			]
		},
		{
			"table_name": "organization_parent_bus_staff_assignments",
			"columns": [
				{
					"column_name": "id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 1,
					"column_default": "gen_random_uuid()"
				},
				{
					"column_name": "parent_organization_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 2,
					"column_default": null
				},
				{
					"column_name": "active_session_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 3,
					"column_default": null
				},
				{
					"column_name": "bus_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 4,
					"column_default": null
				},
				{
					"column_name": "staff_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 5,
					"column_default": null
				},
				{
					"column_name": "role_in_bus",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 6,
					"column_default": null
				},
				{
					"column_name": "is_active",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 7,
					"column_default": "true"
				},
				{
					"column_name": "is_deleted",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 8,
					"column_default": "false"
				},
				{
					"column_name": "created_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 9,
					"column_default": "now()"
				},
				{
					"column_name": "updated_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 10,
					"column_default": "now()"
				},
				{
					"column_name": "created_by",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 11,
					"column_default": null
				},
				{
					"column_name": "updated_by",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 12,
					"column_default": null
				}
			]
		},
		{
			"table_name": "organization_parent_bus_trip_attendance_logs",
			"columns": [
				{
					"column_name": "id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 1,
					"column_default": "gen_random_uuid()"
				},
				{
					"column_name": "parent_organization_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 2,
					"column_default": null
				},
				{
					"column_name": "organization_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 3,
					"column_default": null
				},
				{
					"column_name": "active_session_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 4,
					"column_default": null
				},
				{
					"column_name": "trip_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 5,
					"column_default": null
				},
				{
					"column_name": "student_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 6,
					"column_default": null
				},
				{
					"column_name": "status",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 7,
					"column_default": "'Boarded'::text"
				},
				{
					"column_name": "scan_latitude",
					"data_type": "double precision",
					"is_nullable": "YES",
					"udt_name": "float8",
					"ordinal_position": 8,
					"column_default": null
				},
				{
					"column_name": "scan_longitude",
					"data_type": "double precision",
					"is_nullable": "YES",
					"udt_name": "float8",
					"ordinal_position": 9,
					"column_default": null
				},
				{
					"column_name": "scanned_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 10,
					"column_default": "now()"
				},
				{
					"column_name": "scanned_by_staff_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 11,
					"column_default": null
				},
				{
					"column_name": "sync_status",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 12,
					"column_default": "'Synced'::text"
				},
				{
					"column_name": "is_active",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 13,
					"column_default": "true"
				},
				{
					"column_name": "is_deleted",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 14,
					"column_default": "false"
				},
				{
					"column_name": "created_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 15,
					"column_default": "now()"
				},
				{
					"column_name": "updated_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 16,
					"column_default": "now()"
				},
				{
					"column_name": "created_by",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 17,
					"column_default": null
				},
				{
					"column_name": "updated_by",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 18,
					"column_default": null
				}
			]
		},
		{
			"table_name": "organization_parent_bus_trips",
			"columns": [
				{
					"column_name": "id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 1,
					"column_default": "gen_random_uuid()"
				},
				{
					"column_name": "parent_organization_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 2,
					"column_default": null
				},
				{
					"column_name": "active_session_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 3,
					"column_default": null
				},
				{
					"column_name": "bus_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 4,
					"column_default": null
				},
				{
					"column_name": "driver_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 5,
					"column_default": null
				},
				{
					"column_name": "trip_type",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 6,
					"column_default": null
				},
				{
					"column_name": "status",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 7,
					"column_default": "'Scheduled'::text"
				},
				{
					"column_name": "start_time",
					"data_type": "timestamp with time zone",
					"is_nullable": "YES",
					"udt_name": "timestamptz",
					"ordinal_position": 8,
					"column_default": null
				},
				{
					"column_name": "end_time",
					"data_type": "timestamp with time zone",
					"is_nullable": "YES",
					"udt_name": "timestamptz",
					"ordinal_position": 9,
					"column_default": null
				},
				{
					"column_name": "is_active",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 10,
					"column_default": "true"
				},
				{
					"column_name": "is_deleted",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 11,
					"column_default": "false"
				},
				{
					"column_name": "created_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 12,
					"column_default": "now()"
				},
				{
					"column_name": "updated_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 13,
					"column_default": "now()"
				},
				{
					"column_name": "created_by",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 14,
					"column_default": null
				},
				{
					"column_name": "updated_by",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 15,
					"column_default": null
				}
			]
		},
		{
			"table_name": "organization_parent_buses",
			"columns": [
				{
					"column_name": "id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 1,
					"column_default": "gen_random_uuid()"
				},
				{
					"column_name": "parent_organization_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 2,
					"column_default": null
				},
				{
					"column_name": "active_session_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 3,
					"column_default": null
				},
				{
					"column_name": "bus_number",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 4,
					"column_default": null
				},
				{
					"column_name": "bus_name",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 5,
					"column_default": null
				},
				{
					"column_name": "route_name",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 6,
					"column_default": null
				},
				{
					"column_name": "max_capacity",
					"data_type": "integer",
					"is_nullable": "YES",
					"udt_name": "int4",
					"ordinal_position": 7,
					"column_default": null
				},
				{
					"column_name": "insurance_expiry_date",
					"data_type": "date",
					"is_nullable": "YES",
					"udt_name": "date",
					"ordinal_position": 8,
					"column_default": null
				},
				{
					"column_name": "insurance_image_url",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 9,
					"column_default": null
				},
				{
					"column_name": "fitness_expiry_date",
					"data_type": "date",
					"is_nullable": "YES",
					"udt_name": "date",
					"ordinal_position": 10,
					"column_default": null
				},
				{
					"column_name": "fitness_image_url",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 11,
					"column_default": null
				},
				{
					"column_name": "pollution_expiry_date",
					"data_type": "date",
					"is_nullable": "YES",
					"udt_name": "date",
					"ordinal_position": 12,
					"column_default": null
				},
				{
					"column_name": "pollution_image_url",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 13,
					"column_default": null
				},
				{
					"column_name": "is_active",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 14,
					"column_default": "true"
				},
				{
					"column_name": "is_deleted",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 15,
					"column_default": "false"
				},
				{
					"column_name": "created_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 16,
					"column_default": "now()"
				},
				{
					"column_name": "updated_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 17,
					"column_default": "now()"
				},
				{
					"column_name": "created_by",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 18,
					"column_default": null
				},
				{
					"column_name": "updated_by",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 19,
					"column_default": null
				}
			]
		},
		{
			"table_name": "organization_parent_expenses",
			"columns": [
				{
					"column_name": "id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 1,
					"column_default": "gen_random_uuid()"
				},
				{
					"column_name": "parent_organization_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 2,
					"column_default": null
				},
				{
					"column_name": "active_session_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 3,
					"column_default": null
				},
				{
					"column_name": "expense_type_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 4,
					"column_default": null
				},
				{
					"column_name": "payment_method",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 5,
					"column_default": null
				},
				{
					"column_name": "amount",
					"data_type": "numeric",
					"is_nullable": "NO",
					"udt_name": "numeric",
					"ordinal_position": 6,
					"column_default": "0"
				},
				{
					"column_name": "receipt_uuid",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 7,
					"column_default": null
				},
				{
					"column_name": "admin_note",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 8,
					"column_default": null
				},
				{
					"column_name": "expense_date",
					"data_type": "date",
					"is_nullable": "NO",
					"udt_name": "date",
					"ordinal_position": 9,
					"column_default": "CURRENT_DATE"
				},
				{
					"column_name": "reference_id",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 10,
					"column_default": null
				},
				{
					"column_name": "reference_type",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 11,
					"column_default": null
				},
				{
					"column_name": "cash_paid_to",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 12,
					"column_default": null
				},
				{
					"column_name": "cheque_number",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 13,
					"column_default": null
				},
				{
					"column_name": "cheque_date",
					"data_type": "date",
					"is_nullable": "YES",
					"udt_name": "date",
					"ordinal_position": 14,
					"column_default": null
				},
				{
					"column_name": "cheque_bank_name",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 15,
					"column_default": null
				},
				{
					"column_name": "online_transaction_id",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 16,
					"column_default": null
				},
				{
					"column_name": "online_payment_app",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 17,
					"column_default": null
				},
				{
					"column_name": "vendor_name",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 18,
					"column_default": null
				},
				{
					"column_name": "bill_number",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 19,
					"column_default": null
				},
				{
					"column_name": "is_active",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 20,
					"column_default": "true"
				},
				{
					"column_name": "is_deleted",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 21,
					"column_default": "false"
				},
				{
					"column_name": "created_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 22,
					"column_default": "now()"
				},
				{
					"column_name": "updated_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 23,
					"column_default": "now()"
				},
				{
					"column_name": "created_by",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 24,
					"column_default": null
				},
				{
					"column_name": "updated_by",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 25,
					"column_default": null
				}
			]
		},
		{
			"table_name": "organization_parent_journey_mcqs",
			"columns": [
				{
					"column_name": "id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 1,
					"column_default": "gen_random_uuid()"
				},
				{
					"column_name": "template_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 2,
					"column_default": null
				},
				{
					"column_name": "day_number",
					"data_type": "integer",
					"is_nullable": "NO",
					"udt_name": "int4",
					"ordinal_position": 3,
					"column_default": null
				},
				{
					"column_name": "question_text",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 4,
					"column_default": null
				},
				{
					"column_name": "option_a",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 5,
					"column_default": null
				},
				{
					"column_name": "option_b",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 6,
					"column_default": null
				},
				{
					"column_name": "option_c",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 7,
					"column_default": null
				},
				{
					"column_name": "option_d",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 8,
					"column_default": null
				},
				{
					"column_name": "correct_option",
					"data_type": "character",
					"is_nullable": "NO",
					"udt_name": "bpchar",
					"ordinal_position": 9,
					"column_default": null
				},
				{
					"column_name": "explanation",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 10,
					"column_default": null
				},
				{
					"column_name": "difficulty_level",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 11,
					"column_default": "'medium'::text"
				},
				{
					"column_name": "points",
					"data_type": "integer",
					"is_nullable": "NO",
					"udt_name": "int4",
					"ordinal_position": 12,
					"column_default": "1"
				},
				{
					"column_name": "is_active",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 13,
					"column_default": "true"
				},
				{
					"column_name": "is_deleted",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 14,
					"column_default": "false"
				},
				{
					"column_name": "created_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 15,
					"column_default": "now()"
				},
				{
					"column_name": "updated_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 16,
					"column_default": "now()"
				},
				{
					"column_name": "created_by",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 17,
					"column_default": null
				},
				{
					"column_name": "updated_by",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 18,
					"column_default": null
				}
			]
		},
		{
			"table_name": "organization_parent_journey_tasks",
			"columns": [
				{
					"column_name": "id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 1,
					"column_default": "gen_random_uuid()"
				},
				{
					"column_name": "template_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 2,
					"column_default": null
				},
				{
					"column_name": "day_number",
					"data_type": "integer",
					"is_nullable": "NO",
					"udt_name": "int4",
					"ordinal_position": 3,
					"column_default": null
				},
				{
					"column_name": "task_title",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 4,
					"column_default": null
				},
				{
					"column_name": "task_description",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 5,
					"column_default": null
				},
				{
					"column_name": "task_type",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 6,
					"column_default": "'read'::text"
				},
				{
					"column_name": "sort_order",
					"data_type": "integer",
					"is_nullable": "NO",
					"udt_name": "int4",
					"ordinal_position": 7,
					"column_default": "1"
				},
				{
					"column_name": "is_active",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 8,
					"column_default": "true"
				},
				{
					"column_name": "is_deleted",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 9,
					"column_default": "false"
				},
				{
					"column_name": "created_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 10,
					"column_default": "now()"
				},
				{
					"column_name": "updated_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 11,
					"column_default": "now()"
				},
				{
					"column_name": "created_by",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 12,
					"column_default": null
				},
				{
					"column_name": "updated_by",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 13,
					"column_default": null
				}
			]
		},
		{
			"table_name": "organization_parent_journey_templates",
			"columns": [
				{
					"column_name": "id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 1,
					"column_default": "gen_random_uuid()"
				},
				{
					"column_name": "parent_organization_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 2,
					"column_default": null
				},
				{
					"column_name": "title",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 3,
					"column_default": null
				},
				{
					"column_name": "description",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 4,
					"column_default": null
				},
				{
					"column_name": "duration_days",
					"data_type": "integer",
					"is_nullable": "NO",
					"udt_name": "int4",
					"ordinal_position": 5,
					"column_default": null
				},
				{
					"column_name": "category",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 6,
					"column_default": null
				},
				{
					"column_name": "version",
					"data_type": "integer",
					"is_nullable": "NO",
					"udt_name": "int4",
					"ordinal_position": 7,
					"column_default": "1"
				},
				{
					"column_name": "is_active",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 8,
					"column_default": "true"
				},
				{
					"column_name": "is_deleted",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 9,
					"column_default": "false"
				},
				{
					"column_name": "created_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 10,
					"column_default": "now()"
				},
				{
					"column_name": "updated_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 11,
					"column_default": "now()"
				},
				{
					"column_name": "created_by",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 12,
					"column_default": null
				},
				{
					"column_name": "updated_by",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 13,
					"column_default": null
				}
			]
		},
		{
			"table_name": "organization_parent_staff",
			"columns": [
				{
					"column_name": "id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 1,
					"column_default": "gen_random_uuid()"
				},
				{
					"column_name": "parent_organization_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 2,
					"column_default": null
				},
				{
					"column_name": "active_session_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 3,
					"column_default": null
				},
				{
					"column_name": "role_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 4,
					"column_default": null
				},
				{
					"column_name": "name",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 5,
					"column_default": null
				},
				{
					"column_name": "mobile_number",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 6,
					"column_default": null
				},
				{
					"column_name": "email",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 7,
					"column_default": null
				},
				{
					"column_name": "gender",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 8,
					"column_default": null
				},
				{
					"column_name": "date_of_joining",
					"data_type": "date",
					"is_nullable": "NO",
					"udt_name": "date",
					"ordinal_position": 9,
					"column_default": "CURRENT_DATE"
				},
				{
					"column_name": "date_of_birth",
					"data_type": "date",
					"is_nullable": "YES",
					"udt_name": "date",
					"ordinal_position": 10,
					"column_default": null
				},
				{
					"column_name": "pan_number",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 11,
					"column_default": null
				},
				{
					"column_name": "aadhaar_number",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 12,
					"column_default": null
				},
				{
					"column_name": "license_number",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 13,
					"column_default": null
				},
				{
					"column_name": "license_expiry_date",
					"data_type": "date",
					"is_nullable": "YES",
					"udt_name": "date",
					"ordinal_position": 14,
					"column_default": null
				},
				{
					"column_name": "address_area_id",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 15,
					"column_default": null
				},
				{
					"column_name": "subject_id",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 16,
					"column_default": null
				},
				{
					"column_name": "bus_id",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 17,
					"column_default": null
				},
				{
					"column_name": "is_active",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 18,
					"column_default": "true"
				},
				{
					"column_name": "is_deleted",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 19,
					"column_default": "false"
				},
				{
					"column_name": "created_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 20,
					"column_default": "now()"
				},
				{
					"column_name": "updated_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 21,
					"column_default": "now()"
				},
				{
					"column_name": "created_by",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 22,
					"column_default": null
				},
				{
					"column_name": "updated_by",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 23,
					"column_default": null
				}
			]
		},
		{
			"table_name": "organization_parent_staff_attendance",
			"columns": [
				{
					"column_name": "id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 1,
					"column_default": "gen_random_uuid()"
				},
				{
					"column_name": "parent_organization_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 2,
					"column_default": null
				},
				{
					"column_name": "active_session_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 3,
					"column_default": null
				},
				{
					"column_name": "staff_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 4,
					"column_default": null
				},
				{
					"column_name": "attendance_date",
					"data_type": "date",
					"is_nullable": "NO",
					"udt_name": "date",
					"ordinal_position": 5,
					"column_default": "CURRENT_DATE"
				},
				{
					"column_name": "status_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 6,
					"column_default": null
				},
				{
					"column_name": "is_paid_leave",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 7,
					"column_default": "true"
				},
				{
					"column_name": "check_in_time",
					"data_type": "time without time zone",
					"is_nullable": "YES",
					"udt_name": "time",
					"ordinal_position": 8,
					"column_default": null
				},
				{
					"column_name": "check_out_time",
					"data_type": "time without time zone",
					"is_nullable": "YES",
					"udt_name": "time",
					"ordinal_position": 9,
					"column_default": null
				},
				{
					"column_name": "remarks",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 10,
					"column_default": null
				},
				{
					"column_name": "is_active",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 11,
					"column_default": "true"
				},
				{
					"column_name": "is_deleted",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 12,
					"column_default": "false"
				},
				{
					"column_name": "created_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 13,
					"column_default": "now()"
				},
				{
					"column_name": "updated_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 14,
					"column_default": "now()"
				},
				{
					"column_name": "created_by",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 15,
					"column_default": null
				},
				{
					"column_name": "updated_by",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 16,
					"column_default": null
				}
			]
		},
		{
			"table_name": "organization_parent_staff_bus_enrollments",
			"columns": [
				{
					"column_name": "id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 1,
					"column_default": "gen_random_uuid()"
				},
				{
					"column_name": "parent_organization_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 2,
					"column_default": null
				},
				{
					"column_name": "organization_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 3,
					"column_default": null
				},
				{
					"column_name": "active_session_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 4,
					"column_default": null
				},
				{
					"column_name": "staff_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 5,
					"column_default": null
				},
				{
					"column_name": "bus_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 6,
					"column_default": null
				},
				{
					"column_name": "joining_date",
					"data_type": "date",
					"is_nullable": "NO",
					"udt_name": "date",
					"ordinal_position": 7,
					"column_default": "CURRENT_DATE"
				},
				{
					"column_name": "monthly_fare",
					"data_type": "numeric",
					"is_nullable": "NO",
					"udt_name": "numeric",
					"ordinal_position": 8,
					"column_default": "0"
				},
				{
					"column_name": "auto_deduct_from_salary",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 9,
					"column_default": "true"
				},
				{
					"column_name": "is_active",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 10,
					"column_default": "true"
				},
				{
					"column_name": "is_deleted",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 11,
					"column_default": "false"
				},
				{
					"column_name": "created_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 12,
					"column_default": "now()"
				},
				{
					"column_name": "updated_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 13,
					"column_default": "now()"
				},
				{
					"column_name": "created_by",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 14,
					"column_default": null
				},
				{
					"column_name": "updated_by",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 15,
					"column_default": null
				}
			]
		},
		{
			"table_name": "organization_parent_staff_bus_fares",
			"columns": [
				{
					"column_name": "id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 1,
					"column_default": "gen_random_uuid()"
				},
				{
					"column_name": "parent_organization_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 2,
					"column_default": null
				},
				{
					"column_name": "active_session_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 3,
					"column_default": null
				},
				{
					"column_name": "staff_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 4,
					"column_default": null
				},
				{
					"column_name": "bus_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 5,
					"column_default": null
				},
				{
					"column_name": "fare_amount",
					"data_type": "numeric",
					"is_nullable": "NO",
					"udt_name": "numeric",
					"ordinal_position": 6,
					"column_default": "0"
				},
				{
					"column_name": "is_active",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 7,
					"column_default": "true"
				},
				{
					"column_name": "is_deleted",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 8,
					"column_default": "false"
				},
				{
					"column_name": "created_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 9,
					"column_default": "now()"
				},
				{
					"column_name": "updated_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 10,
					"column_default": "now()"
				},
				{
					"column_name": "created_by",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 11,
					"column_default": null
				},
				{
					"column_name": "updated_by",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 12,
					"column_default": null
				}
			]
		},
		{
			"table_name": "organization_parent_staff_leave_quotas",
			"columns": [
				{
					"column_name": "id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 1,
					"column_default": "gen_random_uuid()"
				},
				{
					"column_name": "parent_organization_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 2,
					"column_default": null
				},
				{
					"column_name": "organization_id",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 3,
					"column_default": null
				},
				{
					"column_name": "active_session_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 4,
					"column_default": null
				},
				{
					"column_name": "staff_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 5,
					"column_default": null
				},
				{
					"column_name": "total_leaves",
					"data_type": "numeric",
					"is_nullable": "NO",
					"udt_name": "numeric",
					"ordinal_position": 6,
					"column_default": "12.0"
				},
				{
					"column_name": "is_active",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 7,
					"column_default": "true"
				},
				{
					"column_name": "is_deleted",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 8,
					"column_default": "false"
				},
				{
					"column_name": "created_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 9,
					"column_default": "now()"
				},
				{
					"column_name": "updated_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 10,
					"column_default": "now()"
				},
				{
					"column_name": "created_by",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 11,
					"column_default": null
				},
				{
					"column_name": "updated_by",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 12,
					"column_default": null
				}
			]
		},
		{
			"table_name": "organization_parent_staff_salaries",
			"columns": [
				{
					"column_name": "id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 1,
					"column_default": "gen_random_uuid()"
				},
				{
					"column_name": "parent_organization_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 2,
					"column_default": null
				},
				{
					"column_name": "active_session_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 3,
					"column_default": null
				},
				{
					"column_name": "staff_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 4,
					"column_default": null
				},
				{
					"column_name": "monthly_salary",
					"data_type": "numeric",
					"is_nullable": "NO",
					"udt_name": "numeric",
					"ordinal_position": 5,
					"column_default": "0"
				},
				{
					"column_name": "bank_name",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 6,
					"column_default": null
				},
				{
					"column_name": "bank_account_number",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 7,
					"column_default": null
				},
				{
					"column_name": "ifsc_code",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 8,
					"column_default": null
				},
				{
					"column_name": "upi_id",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 9,
					"column_default": null
				},
				{
					"column_name": "is_active",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 10,
					"column_default": "true"
				},
				{
					"column_name": "is_deleted",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 11,
					"column_default": "false"
				},
				{
					"column_name": "created_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 12,
					"column_default": "now()"
				},
				{
					"column_name": "updated_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 13,
					"column_default": "now()"
				},
				{
					"column_name": "created_by",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 14,
					"column_default": null
				},
				{
					"column_name": "updated_by",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 15,
					"column_default": null
				}
			]
		},
		{
			"table_name": "organization_parent_staff_salary_payments",
			"columns": [
				{
					"column_name": "id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 1,
					"column_default": "gen_random_uuid()"
				},
				{
					"column_name": "parent_organization_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 2,
					"column_default": null
				},
				{
					"column_name": "active_session_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 3,
					"column_default": null
				},
				{
					"column_name": "staff_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 4,
					"column_default": null
				},
				{
					"column_name": "salary_payout_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 5,
					"column_default": null
				},
				{
					"column_name": "payment_date",
					"data_type": "date",
					"is_nullable": "NO",
					"udt_name": "date",
					"ordinal_position": 6,
					"column_default": "CURRENT_DATE"
				},
				{
					"column_name": "amount_paid",
					"data_type": "numeric",
					"is_nullable": "NO",
					"udt_name": "numeric",
					"ordinal_position": 7,
					"column_default": "0"
				},
				{
					"column_name": "payment_mode",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 8,
					"column_default": null
				},
				{
					"column_name": "cash_paid_by_user_id",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 9,
					"column_default": null
				},
				{
					"column_name": "cheque_number",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 10,
					"column_default": null
				},
				{
					"column_name": "cheque_date",
					"data_type": "date",
					"is_nullable": "YES",
					"udt_name": "date",
					"ordinal_position": 11,
					"column_default": null
				},
				{
					"column_name": "cheque_bank_name",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 12,
					"column_default": null
				},
				{
					"column_name": "online_transaction_id",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 13,
					"column_default": null
				},
				{
					"column_name": "online_payment_app",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 14,
					"column_default": null
				},
				{
					"column_name": "remarks",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 15,
					"column_default": null
				},
				{
					"column_name": "is_active",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 16,
					"column_default": "true"
				},
				{
					"column_name": "is_deleted",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 17,
					"column_default": "false"
				},
				{
					"column_name": "created_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 18,
					"column_default": "now()"
				},
				{
					"column_name": "updated_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 19,
					"column_default": "now()"
				},
				{
					"column_name": "created_by",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 20,
					"column_default": null
				},
				{
					"column_name": "updated_by",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 21,
					"column_default": null
				}
			]
		},
		{
			"table_name": "organization_parent_staff_salary_payouts",
			"columns": [
				{
					"column_name": "id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 1,
					"column_default": "gen_random_uuid()"
				},
				{
					"column_name": "parent_organization_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 2,
					"column_default": null
				},
				{
					"column_name": "active_session_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 3,
					"column_default": null
				},
				{
					"column_name": "staff_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 4,
					"column_default": null
				},
				{
					"column_name": "payout_month",
					"data_type": "integer",
					"is_nullable": "NO",
					"udt_name": "int4",
					"ordinal_position": 5,
					"column_default": null
				},
				{
					"column_name": "payout_year",
					"data_type": "integer",
					"is_nullable": "NO",
					"udt_name": "int4",
					"ordinal_position": 6,
					"column_default": null
				},
				{
					"column_name": "salary_amount",
					"data_type": "numeric",
					"is_nullable": "NO",
					"udt_name": "numeric",
					"ordinal_position": 7,
					"column_default": null
				},
				{
					"column_name": "is_locked",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 8,
					"column_default": "false"
				},
				{
					"column_name": "locked_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "YES",
					"udt_name": "timestamptz",
					"ordinal_position": 9,
					"column_default": null
				},
				{
					"column_name": "locked_by",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 10,
					"column_default": null
				},
				{
					"column_name": "is_active",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 11,
					"column_default": "true"
				},
				{
					"column_name": "is_deleted",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 12,
					"column_default": "false"
				},
				{
					"column_name": "created_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 13,
					"column_default": "now()"
				},
				{
					"column_name": "updated_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 14,
					"column_default": "now()"
				},
				{
					"column_name": "bonus",
					"data_type": "numeric",
					"is_nullable": "NO",
					"udt_name": "numeric",
					"ordinal_position": 15,
					"column_default": "0.0"
				},
				{
					"column_name": "deduction",
					"data_type": "numeric",
					"is_nullable": "NO",
					"udt_name": "numeric",
					"ordinal_position": 16,
					"column_default": "0.0"
				}
			]
		},
		{
			"table_name": "organization_parent_staff_user_links",
			"columns": [
				{
					"column_name": "id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 1,
					"column_default": "gen_random_uuid()"
				},
				{
					"column_name": "parent_organization_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 2,
					"column_default": null
				},
				{
					"column_name": "active_session_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 3,
					"column_default": null
				},
				{
					"column_name": "staff_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 4,
					"column_default": null
				},
				{
					"column_name": "user_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 5,
					"column_default": null
				},
				{
					"column_name": "is_approved",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 6,
					"column_default": "false"
				},
				{
					"column_name": "approved_by",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 7,
					"column_default": null
				},
				{
					"column_name": "approved_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "YES",
					"udt_name": "timestamptz",
					"ordinal_position": 8,
					"column_default": null
				},
				{
					"column_name": "is_active",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 9,
					"column_default": "true"
				},
				{
					"column_name": "is_deleted",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 10,
					"column_default": "false"
				},
				{
					"column_name": "created_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 11,
					"column_default": "now()"
				},
				{
					"column_name": "updated_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 12,
					"column_default": "now()"
				}
			]
		},
		{
			"table_name": "organization_parents",
			"columns": [
				{
					"column_name": "id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 1,
					"column_default": "gen_random_uuid()"
				},
				{
					"column_name": "name",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 2,
					"column_default": null
				},
				{
					"column_name": "type_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 3,
					"column_default": null
				},
				{
					"column_name": "is_active",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 4,
					"column_default": "true"
				},
				{
					"column_name": "is_deleted",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 5,
					"column_default": "false"
				},
				{
					"column_name": "created_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 6,
					"column_default": "now()"
				},
				{
					"column_name": "updated_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 7,
					"column_default": "now()"
				}
			]
		},
		{
			"table_name": "organization_parents_profiles",
			"columns": [
				{
					"column_name": "parent_organization_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 1,
					"column_default": null
				},
				{
					"column_name": "logo_url",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 2,
					"column_default": null
				},
				{
					"column_name": "website_url",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 3,
					"column_default": null
				},
				{
					"column_name": "email",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 4,
					"column_default": null
				},
				{
					"column_name": "mobile_number",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 5,
					"column_default": null
				},
				{
					"column_name": "alternate_mobile_number",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 6,
					"column_default": null
				},
				{
					"column_name": "registration_number",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 7,
					"column_default": null
				},
				{
					"column_name": "gst_number",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 8,
					"column_default": null
				},
				{
					"column_name": "pan_number",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 9,
					"column_default": null
				},
				{
					"column_name": "address_line1",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 10,
					"column_default": null
				},
				{
					"column_name": "address_line2",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 11,
					"column_default": null
				},
				{
					"column_name": "city",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 12,
					"column_default": null
				},
				{
					"column_name": "state",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 13,
					"column_default": "'Rajasthan'::text"
				},
				{
					"column_name": "pincode",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 14,
					"column_default": null
				},
				{
					"column_name": "active_session_id",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 15,
					"column_default": null
				},
				{
					"column_name": "created_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 16,
					"column_default": "now()"
				},
				{
					"column_name": "updated_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 17,
					"column_default": "now()"
				},
				{
					"column_name": "is_setup_complete",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 18,
					"column_default": "false"
				}
			]
		},
		{
			"table_name": "organization_parents_session_tokens",
			"columns": [
				{
					"column_name": "id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 1,
					"column_default": "gen_random_uuid()"
				},
				{
					"column_name": "parent_organization_user_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 2,
					"column_default": null
				},
				{
					"column_name": "session_token",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 3,
					"column_default": null
				},
				{
					"column_name": "refresh_token",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 4,
					"column_default": null
				},
				{
					"column_name": "is_active",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 5,
					"column_default": "true"
				},
				{
					"column_name": "expires_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 6,
					"column_default": null
				},
				{
					"column_name": "created_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 7,
					"column_default": "now()"
				},
				{
					"column_name": "updated_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 8,
					"column_default": "now()"
				}
			]
		},
		{
			"table_name": "organization_parents_users",
			"columns": [
				{
					"column_name": "id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 1,
					"column_default": "gen_random_uuid()"
				},
				{
					"column_name": "parent_organization_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 2,
					"column_default": null
				},
				{
					"column_name": "user_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 3,
					"column_default": null
				},
				{
					"column_name": "role_id",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 4,
					"column_default": null
				},
				{
					"column_name": "is_active",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 5,
					"column_default": "true"
				},
				{
					"column_name": "is_deleted",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 6,
					"column_default": "false"
				},
				{
					"column_name": "created_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 7,
					"column_default": "now()"
				},
				{
					"column_name": "updated_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 8,
					"column_default": "now()"
				}
			]
		},
		{
			"table_name": "organization_periods",
			"columns": [
				{
					"column_name": "id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 1,
					"column_default": "gen_random_uuid()"
				},
				{
					"column_name": "organization_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 2,
					"column_default": null
				},
				{
					"column_name": "session_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 3,
					"column_default": null
				},
				{
					"column_name": "name",
					"data_type": "USER-DEFINED",
					"is_nullable": "NO",
					"udt_name": "period_name_enum",
					"ordinal_position": 4,
					"column_default": null
				},
				{
					"column_name": "start_time",
					"data_type": "time without time zone",
					"is_nullable": "NO",
					"udt_name": "time",
					"ordinal_position": 5,
					"column_default": null
				},
				{
					"column_name": "end_time",
					"data_type": "time without time zone",
					"is_nullable": "NO",
					"udt_name": "time",
					"ordinal_position": 6,
					"column_default": null
				},
				{
					"column_name": "is_break",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 7,
					"column_default": "false"
				},
				{
					"column_name": "sort_order",
					"data_type": "integer",
					"is_nullable": "NO",
					"udt_name": "int4",
					"ordinal_position": 8,
					"column_default": null
				},
				{
					"column_name": "is_active",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 9,
					"column_default": "true"
				},
				{
					"column_name": "created_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 10,
					"column_default": "now()"
				},
				{
					"column_name": "updated_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 11,
					"column_default": "now()"
				},
				{
					"column_name": "created_by",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 12,
					"column_default": null
				},
				{
					"column_name": "updated_by",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 13,
					"column_default": null
				}
			]
		},
		{
			"table_name": "organization_profiles",
			"columns": [
				{
					"column_name": "organization_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 1,
					"column_default": null
				},
				{
					"column_name": "logo_url",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 2,
					"column_default": null
				},
				{
					"column_name": "website_url",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 3,
					"column_default": null
				},
				{
					"column_name": "email",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 4,
					"column_default": null
				},
				{
					"column_name": "mobile_number",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 5,
					"column_default": null
				},
				{
					"column_name": "alternate_mobile_number",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 6,
					"column_default": null
				},
				{
					"column_name": "registration_number",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 7,
					"column_default": null
				},
				{
					"column_name": "gst_number",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 8,
					"column_default": null
				},
				{
					"column_name": "pan_number",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 9,
					"column_default": null
				},
				{
					"column_name": "address_line1",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 10,
					"column_default": null
				},
				{
					"column_name": "address_line2",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 11,
					"column_default": null
				},
				{
					"column_name": "city",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 12,
					"column_default": null
				},
				{
					"column_name": "state",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 13,
					"column_default": "'Rajasthan'::text"
				},
				{
					"column_name": "pincode",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 14,
					"column_default": null
				},
				{
					"column_name": "active_session_id",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 15,
					"column_default": null
				},
				{
					"column_name": "created_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 16,
					"column_default": "now()"
				},
				{
					"column_name": "updated_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 17,
					"column_default": "now()"
				},
				{
					"column_name": "is_setup_complete",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 18,
					"column_default": "false"
				},
				{
					"column_name": "is_active",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 19,
					"column_default": "true"
				},
				{
					"column_name": "is_deleted",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 20,
					"column_default": "false"
				}
			]
		},
		{
			"table_name": "organization_remark_attachments",
			"columns": [
				{
					"column_name": "id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 1,
					"column_default": "gen_random_uuid()"
				},
				{
					"column_name": "parent_organization_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 2,
					"column_default": null
				},
				{
					"column_name": "organization_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 3,
					"column_default": null
				},
				{
					"column_name": "active_session_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 4,
					"column_default": null
				},
				{
					"column_name": "remark_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 5,
					"column_default": null
				},
				{
					"column_name": "file_url",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 6,
					"column_default": null
				},
				{
					"column_name": "file_type",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 7,
					"column_default": null
				},
				{
					"column_name": "file_size",
					"data_type": "integer",
					"is_nullable": "YES",
					"udt_name": "int4",
					"ordinal_position": 8,
					"column_default": null
				},
				{
					"column_name": "is_active",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 9,
					"column_default": "true"
				},
				{
					"column_name": "is_deleted",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 10,
					"column_default": "false"
				},
				{
					"column_name": "created_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 11,
					"column_default": "now()"
				},
				{
					"column_name": "updated_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 12,
					"column_default": "now()"
				},
				{
					"column_name": "created_by",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 13,
					"column_default": null
				},
				{
					"column_name": "updated_by",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 14,
					"column_default": null
				}
			]
		},
		{
			"table_name": "organization_remark_history",
			"columns": [
				{
					"column_name": "id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 1,
					"column_default": "gen_random_uuid()"
				},
				{
					"column_name": "parent_organization_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 2,
					"column_default": null
				},
				{
					"column_name": "organization_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 3,
					"column_default": null
				},
				{
					"column_name": "active_session_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 4,
					"column_default": null
				},
				{
					"column_name": "remark_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 5,
					"column_default": null
				},
				{
					"column_name": "previous_content",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 6,
					"column_default": null
				},
				{
					"column_name": "edited_by_user_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 7,
					"column_default": null
				},
				{
					"column_name": "is_active",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 8,
					"column_default": "true"
				},
				{
					"column_name": "is_deleted",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 9,
					"column_default": "false"
				},
				{
					"column_name": "created_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 10,
					"column_default": "now()"
				},
				{
					"column_name": "updated_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 11,
					"column_default": "now()"
				},
				{
					"column_name": "created_by",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 12,
					"column_default": null
				},
				{
					"column_name": "updated_by",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 13,
					"column_default": null
				}
			]
		},
		{
			"table_name": "organization_remark_targets",
			"columns": [
				{
					"column_name": "id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 1,
					"column_default": "gen_random_uuid()"
				},
				{
					"column_name": "parent_organization_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 2,
					"column_default": null
				},
				{
					"column_name": "organization_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 3,
					"column_default": null
				},
				{
					"column_name": "active_session_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 4,
					"column_default": null
				},
				{
					"column_name": "remark_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 5,
					"column_default": null
				},
				{
					"column_name": "target_type",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 6,
					"column_default": null
				},
				{
					"column_name": "target_student_id",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 7,
					"column_default": null
				},
				{
					"column_name": "target_guardian_id",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 8,
					"column_default": null
				},
				{
					"column_name": "target_staff_id",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 9,
					"column_default": null
				},
				{
					"column_name": "target_user_id",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 10,
					"column_default": null
				},
				{
					"column_name": "is_active",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 11,
					"column_default": "true"
				},
				{
					"column_name": "is_deleted",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 12,
					"column_default": "false"
				},
				{
					"column_name": "created_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 13,
					"column_default": "now()"
				},
				{
					"column_name": "updated_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 14,
					"column_default": "now()"
				},
				{
					"column_name": "created_by",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 15,
					"column_default": null
				},
				{
					"column_name": "updated_by",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 16,
					"column_default": null
				}
			]
		},
		{
			"table_name": "organization_remarks",
			"columns": [
				{
					"column_name": "id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 1,
					"column_default": "gen_random_uuid()"
				},
				{
					"column_name": "parent_organization_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 2,
					"column_default": null
				},
				{
					"column_name": "organization_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 3,
					"column_default": null
				},
				{
					"column_name": "active_session_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 4,
					"column_default": null
				},
				{
					"column_name": "content",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 5,
					"column_default": null
				},
				{
					"column_name": "category",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 6,
					"column_default": null
				},
				{
					"column_name": "priority",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 7,
					"column_default": "'Medium'::text"
				},
				{
					"column_name": "creator_user_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 8,
					"column_default": null
				},
				{
					"column_name": "creator_workspace_role_id",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 9,
					"column_default": null
				},
				{
					"column_name": "visibility_type",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 10,
					"column_default": "'Public'::text"
				},
				{
					"column_name": "visibility_audience",
					"data_type": "jsonb",
					"is_nullable": "NO",
					"udt_name": "jsonb",
					"ordinal_position": 11,
					"column_default": "'[]'::jsonb"
				},
				{
					"column_name": "is_pinned",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 12,
					"column_default": "false"
				},
				{
					"column_name": "pin_expires_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "YES",
					"udt_name": "timestamptz",
					"ordinal_position": 13,
					"column_default": null
				},
				{
					"column_name": "expires_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "YES",
					"udt_name": "timestamptz",
					"ordinal_position": 14,
					"column_default": null
				},
				{
					"column_name": "is_active",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 15,
					"column_default": "true"
				},
				{
					"column_name": "is_deleted",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 16,
					"column_default": "false"
				},
				{
					"column_name": "created_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 17,
					"column_default": "now()"
				},
				{
					"column_name": "updated_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 18,
					"column_default": "now()"
				},
				{
					"column_name": "created_by",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 19,
					"column_default": null
				},
				{
					"column_name": "updated_by",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 20,
					"column_default": null
				}
			]
		},
		{
			"table_name": "organization_sections",
			"columns": [
				{
					"column_name": "id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 1,
					"column_default": "gen_random_uuid()"
				},
				{
					"column_name": "organization_class_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 2,
					"column_default": null
				},
				{
					"column_name": "name",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 3,
					"column_default": null
				},
				{
					"column_name": "code",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 4,
					"column_default": null
				},
				{
					"column_name": "room_number",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 5,
					"column_default": null
				},
				{
					"column_name": "max_capacity",
					"data_type": "integer",
					"is_nullable": "NO",
					"udt_name": "int4",
					"ordinal_position": 6,
					"column_default": "40"
				},
				{
					"column_name": "is_active",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 7,
					"column_default": "true"
				},
				{
					"column_name": "created_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 8,
					"column_default": "now()"
				},
				{
					"column_name": "is_deleted",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 9,
					"column_default": "false"
				},
				{
					"column_name": "updated_by",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 10,
					"column_default": null
				},
				{
					"column_name": "created_by",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 11,
					"column_default": null
				},
				{
					"column_name": "updated_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 12,
					"column_default": "now()"
				}
			]
		},
		{
			"table_name": "organization_session_classes",
			"columns": [
				{
					"column_name": "id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 1,
					"column_default": "gen_random_uuid()"
				},
				{
					"column_name": "organization_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 2,
					"column_default": null
				},
				{
					"column_name": "session_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 3,
					"column_default": null
				},
				{
					"column_name": "organization_class_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 4,
					"column_default": null
				},
				{
					"column_name": "is_active",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 5,
					"column_default": "true"
				},
				{
					"column_name": "created_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 6,
					"column_default": "now()"
				},
				{
					"column_name": "is_deleted",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 7,
					"column_default": "false"
				},
				{
					"column_name": "updated_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 8,
					"column_default": "now()"
				},
				{
					"column_name": "created_by",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 9,
					"column_default": null
				},
				{
					"column_name": "updated_by",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 10,
					"column_default": null
				}
			]
		},
		{
			"table_name": "organization_session_sections",
			"columns": [
				{
					"column_name": "id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 1,
					"column_default": "gen_random_uuid()"
				},
				{
					"column_name": "organization_session_class_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 2,
					"column_default": null
				},
				{
					"column_name": "organization_section_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 3,
					"column_default": null
				},
				{
					"column_name": "class_teacher_id",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 4,
					"column_default": null
				},
				{
					"column_name": "is_active",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 5,
					"column_default": "true"
				},
				{
					"column_name": "created_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 6,
					"column_default": "now()"
				},
				{
					"column_name": "is_deleted",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 7,
					"column_default": "false"
				},
				{
					"column_name": "updated_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 8,
					"column_default": "now()"
				},
				{
					"column_name": "created_by",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 9,
					"column_default": null
				},
				{
					"column_name": "updated_by",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 10,
					"column_default": null
				}
			]
		},
		{
			"table_name": "organization_session_subjects",
			"columns": [
				{
					"column_name": "id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 1,
					"column_default": "gen_random_uuid()"
				},
				{
					"column_name": "organization_session_class_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 2,
					"column_default": null
				},
				{
					"column_name": "subject_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 3,
					"column_default": null
				},
				{
					"column_name": "subject_teacher_id",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 4,
					"column_default": null
				},
				{
					"column_name": "custom_subject_name",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 5,
					"column_default": null
				},
				{
					"column_name": "subject_code",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 6,
					"column_default": null
				},
				{
					"column_name": "is_elective",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 7,
					"column_default": "false"
				},
				{
					"column_name": "weekly_hours",
					"data_type": "numeric",
					"is_nullable": "YES",
					"udt_name": "numeric",
					"ordinal_position": 8,
					"column_default": null
				},
				{
					"column_name": "is_active",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 9,
					"column_default": "true"
				},
				{
					"column_name": "created_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 10,
					"column_default": "now()"
				},
				{
					"column_name": "is_deleted",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 11,
					"column_default": "false"
				},
				{
					"column_name": "updated_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 12,
					"column_default": "now()"
				},
				{
					"column_name": "created_by",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 13,
					"column_default": null
				},
				{
					"column_name": "updated_by",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 14,
					"column_default": null
				}
			]
		},
		{
			"table_name": "organization_session_tokens",
			"columns": [
				{
					"column_name": "id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 1,
					"column_default": "gen_random_uuid()"
				},
				{
					"column_name": "organization_user_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 2,
					"column_default": null
				},
				{
					"column_name": "session_token",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 3,
					"column_default": null
				},
				{
					"column_name": "refresh_token",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 4,
					"column_default": null
				},
				{
					"column_name": "is_active",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 5,
					"column_default": "true"
				},
				{
					"column_name": "expires_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 6,
					"column_default": null
				},
				{
					"column_name": "created_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 7,
					"column_default": "now()"
				},
				{
					"column_name": "updated_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 8,
					"column_default": "now()"
				}
			]
		},
		{
			"table_name": "organization_student_additional_details",
			"columns": [
				{
					"column_name": "student_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 1,
					"column_default": null
				},
				{
					"column_name": "mother_tongue_id",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 2,
					"column_default": null
				},
				{
					"column_name": "mother_tongue_text",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 3,
					"column_default": null
				},
				{
					"column_name": "religion",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 4,
					"column_default": null
				},
				{
					"column_name": "nationality",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 5,
					"column_default": "'Indian'::text"
				},
				{
					"column_name": "identification_mark",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 6,
					"column_default": null
				},
				{
					"column_name": "is_single_girl_child",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 7,
					"column_default": "false"
				},
				{
					"column_name": "caste_certificate_number",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 8,
					"column_default": null
				},
				{
					"column_name": "father_name",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 9,
					"column_default": null
				},
				{
					"column_name": "father_mobile",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 10,
					"column_default": null
				},
				{
					"column_name": "father_email",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 11,
					"column_default": null
				},
				{
					"column_name": "father_qualification",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 12,
					"column_default": null
				},
				{
					"column_name": "father_occupation",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 13,
					"column_default": null
				},
				{
					"column_name": "parents_aadhaar_father",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 14,
					"column_default": null
				},
				{
					"column_name": "mother_name",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 15,
					"column_default": null
				},
				{
					"column_name": "mother_mobile",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 16,
					"column_default": null
				},
				{
					"column_name": "mother_email",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 17,
					"column_default": null
				},
				{
					"column_name": "mother_qualification",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 18,
					"column_default": null
				},
				{
					"column_name": "mother_occupation",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 19,
					"column_default": null
				},
				{
					"column_name": "parents_aadhaar_mother",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 20,
					"column_default": null
				},
				{
					"column_name": "family_annual_income",
					"data_type": "numeric",
					"is_nullable": "YES",
					"udt_name": "numeric",
					"ordinal_position": 21,
					"column_default": "0"
				},
				{
					"column_name": "permanent_address_details",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 22,
					"column_default": null
				},
				{
					"column_name": "permanent_address_area",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 23,
					"column_default": null
				},
				{
					"column_name": "permanent_address_area_id",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 24,
					"column_default": null
				},
				{
					"column_name": "previous_school_name",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 25,
					"column_default": null
				},
				{
					"column_name": "previous_class",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 26,
					"column_default": null
				},
				{
					"column_name": "previous_board",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 27,
					"column_default": null
				},
				{
					"column_name": "tc_number",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 28,
					"column_default": null
				},
				{
					"column_name": "tc_date",
					"data_type": "date",
					"is_nullable": "YES",
					"udt_name": "date",
					"ordinal_position": 29,
					"column_default": null
				},
				{
					"column_name": "previous_marks",
					"data_type": "numeric",
					"is_nullable": "YES",
					"udt_name": "numeric",
					"ordinal_position": 30,
					"column_default": null
				},
				{
					"column_name": "height",
					"data_type": "numeric",
					"is_nullable": "YES",
					"udt_name": "numeric",
					"ordinal_position": 31,
					"column_default": null
				},
				{
					"column_name": "weight",
					"data_type": "numeric",
					"is_nullable": "YES",
					"udt_name": "numeric",
					"ordinal_position": 32,
					"column_default": null
				},
				{
					"column_name": "medical_conditions",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 33,
					"column_default": null
				},
				{
					"column_name": "regular_medications",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 34,
					"column_default": null
				},
				{
					"column_name": "emergency_contact_name",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 35,
					"column_default": null
				},
				{
					"column_name": "emergency_contact_phone",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 36,
					"column_default": null
				},
				{
					"column_name": "bank_account_number",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 37,
					"column_default": null
				},
				{
					"column_name": "bank_name",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 38,
					"column_default": null
				},
				{
					"column_name": "bank_branch",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 39,
					"column_default": null
				},
				{
					"column_name": "bank_ifsc",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 40,
					"column_default": null
				},
				{
					"column_name": "bank_account_holder",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 41,
					"column_default": null
				},
				{
					"column_name": "created_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 42,
					"column_default": "now()"
				},
				{
					"column_name": "updated_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 43,
					"column_default": "now()"
				},
				{
					"column_name": "created_by",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 44,
					"column_default": null
				},
				{
					"column_name": "updated_by",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 45,
					"column_default": null
				},
				{
					"column_name": "student_aadhar",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 46,
					"column_default": null
				}
			]
		},
		{
			"table_name": "organization_student_additional_fees",
			"columns": [
				{
					"column_name": "id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 1,
					"column_default": "gen_random_uuid()"
				},
				{
					"column_name": "organization_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 2,
					"column_default": null
				},
				{
					"column_name": "active_session_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 3,
					"column_default": null
				},
				{
					"column_name": "student_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 4,
					"column_default": null
				},
				{
					"column_name": "global_fee_head_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 5,
					"column_default": null
				},
				{
					"column_name": "amount",
					"data_type": "numeric",
					"is_nullable": "NO",
					"udt_name": "numeric",
					"ordinal_position": 6,
					"column_default": "0"
				},
				{
					"column_name": "is_active",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 7,
					"column_default": "true"
				},
				{
					"column_name": "is_deleted",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 8,
					"column_default": "false"
				},
				{
					"column_name": "created_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 9,
					"column_default": "now()"
				},
				{
					"column_name": "updated_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 10,
					"column_default": "now()"
				},
				{
					"column_name": "created_by",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 11,
					"column_default": null
				},
				{
					"column_name": "updated_by",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 12,
					"column_default": null
				}
			]
		},
		{
			"table_name": "organization_student_attendance",
			"columns": [
				{
					"column_name": "id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 1,
					"column_default": "gen_random_uuid()"
				},
				{
					"column_name": "organization_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 2,
					"column_default": null
				},
				{
					"column_name": "active_session_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 3,
					"column_default": null
				},
				{
					"column_name": "student_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 4,
					"column_default": null
				},
				{
					"column_name": "attendance_date",
					"data_type": "date",
					"is_nullable": "NO",
					"udt_name": "date",
					"ordinal_position": 5,
					"column_default": "CURRENT_DATE"
				},
				{
					"column_name": "status",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 6,
					"column_default": null
				},
				{
					"column_name": "remarks",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 7,
					"column_default": null
				},
				{
					"column_name": "marked_by_staff_id",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 8,
					"column_default": null
				},
				{
					"column_name": "is_active",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 9,
					"column_default": "true"
				},
				{
					"column_name": "is_deleted",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 10,
					"column_default": "false"
				},
				{
					"column_name": "created_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 11,
					"column_default": "now()"
				},
				{
					"column_name": "updated_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 12,
					"column_default": "now()"
				},
				{
					"column_name": "created_by",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 13,
					"column_default": null
				},
				{
					"column_name": "updated_by",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 14,
					"column_default": null
				}
			]
		},
		{
			"table_name": "organization_student_bus_assignments",
			"columns": [
				{
					"column_name": "id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 1,
					"column_default": "gen_random_uuid()"
				},
				{
					"column_name": "organization_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 2,
					"column_default": null
				},
				{
					"column_name": "active_session_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 3,
					"column_default": null
				},
				{
					"column_name": "student_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 4,
					"column_default": null
				},
				{
					"column_name": "bus_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 5,
					"column_default": null
				},
				{
					"column_name": "pickup_stop",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 6,
					"column_default": null
				},
				{
					"column_name": "is_active",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 7,
					"column_default": "true"
				},
				{
					"column_name": "is_deleted",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 8,
					"column_default": "false"
				},
				{
					"column_name": "created_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 9,
					"column_default": "now()"
				},
				{
					"column_name": "updated_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 10,
					"column_default": "now()"
				},
				{
					"column_name": "created_by",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 11,
					"column_default": null
				},
				{
					"column_name": "updated_by",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 12,
					"column_default": null
				}
			]
		},
		{
			"table_name": "organization_student_enrollments",
			"columns": [
				{
					"column_name": "id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 1,
					"column_default": "gen_random_uuid()"
				},
				{
					"column_name": "organization_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 2,
					"column_default": null
				},
				{
					"column_name": "active_session_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 3,
					"column_default": null
				},
				{
					"column_name": "student_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 4,
					"column_default": null
				},
				{
					"column_name": "class_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 5,
					"column_default": null
				},
				{
					"column_name": "section_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 6,
					"column_default": null
				},
				{
					"column_name": "result_status_id",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 7,
					"column_default": null
				},
				{
					"column_name": "roll_number",
					"data_type": "integer",
					"is_nullable": "YES",
					"udt_name": "int4",
					"ordinal_position": 8,
					"column_default": null
				},
				{
					"column_name": "is_active",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 9,
					"column_default": "true"
				},
				{
					"column_name": "is_deleted",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 10,
					"column_default": "false"
				},
				{
					"column_name": "created_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 11,
					"column_default": "now()"
				},
				{
					"column_name": "updated_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 12,
					"column_default": "now()"
				},
				{
					"column_name": "created_by",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 13,
					"column_default": null
				},
				{
					"column_name": "updated_by",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 14,
					"column_default": null
				}
			]
		},
		{
			"table_name": "organization_student_exam_marks",
			"columns": [
				{
					"column_name": "id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 1,
					"column_default": "gen_random_uuid()"
				},
				{
					"column_name": "organization_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 2,
					"column_default": null
				},
				{
					"column_name": "active_session_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 3,
					"column_default": null
				},
				{
					"column_name": "exam_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 4,
					"column_default": null
				},
				{
					"column_name": "class_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 5,
					"column_default": null
				},
				{
					"column_name": "subject_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 6,
					"column_default": null
				},
				{
					"column_name": "student_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 7,
					"column_default": null
				},
				{
					"column_name": "obtained_marks",
					"data_type": "numeric",
					"is_nullable": "YES",
					"udt_name": "numeric",
					"ordinal_position": 8,
					"column_default": null
				},
				{
					"column_name": "is_absent",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 9,
					"column_default": "false"
				},
				{
					"column_name": "is_medical_leave",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 10,
					"column_default": "false"
				},
				{
					"column_name": "teacher_remarks",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 11,
					"column_default": null
				},
				{
					"column_name": "is_active",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 12,
					"column_default": "true"
				},
				{
					"column_name": "is_deleted",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 13,
					"column_default": "false"
				},
				{
					"column_name": "created_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 14,
					"column_default": "now()"
				},
				{
					"column_name": "updated_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 15,
					"column_default": "now()"
				},
				{
					"column_name": "created_by",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 16,
					"column_default": null
				}
			]
		},
		{
			"table_name": "organization_student_fee_payments",
			"columns": [
				{
					"column_name": "id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 1,
					"column_default": "gen_random_uuid()"
				},
				{
					"column_name": "organization_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 2,
					"column_default": null
				},
				{
					"column_name": "active_session_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 3,
					"column_default": null
				},
				{
					"column_name": "student_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 4,
					"column_default": null
				},
				{
					"column_name": "receipt_number",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 5,
					"column_default": null
				},
				{
					"column_name": "payment_mode",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 6,
					"column_default": null
				},
				{
					"column_name": "payment_date",
					"data_type": "date",
					"is_nullable": "NO",
					"udt_name": "date",
					"ordinal_position": 7,
					"column_default": "CURRENT_DATE"
				},
				{
					"column_name": "amount_paid",
					"data_type": "numeric",
					"is_nullable": "NO",
					"udt_name": "numeric",
					"ordinal_position": 8,
					"column_default": "0"
				},
				{
					"column_name": "discount_amount",
					"data_type": "numeric",
					"is_nullable": "NO",
					"udt_name": "numeric",
					"ordinal_position": 9,
					"column_default": "0"
				},
				{
					"column_name": "fine_amount",
					"data_type": "numeric",
					"is_nullable": "NO",
					"udt_name": "numeric",
					"ordinal_position": 10,
					"column_default": "0"
				},
				{
					"column_name": "discount_reason",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 11,
					"column_default": null
				},
				{
					"column_name": "cash_received_by_user_id",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 12,
					"column_default": null
				},
				{
					"column_name": "cheque_number",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 13,
					"column_default": null
				},
				{
					"column_name": "cheque_date",
					"data_type": "date",
					"is_nullable": "YES",
					"udt_name": "date",
					"ordinal_position": 14,
					"column_default": null
				},
				{
					"column_name": "cheque_bank_name",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 15,
					"column_default": null
				},
				{
					"column_name": "online_transaction_id",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 16,
					"column_default": null
				},
				{
					"column_name": "online_payment_app",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 17,
					"column_default": null
				},
				{
					"column_name": "remarks",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 18,
					"column_default": null
				},
				{
					"column_name": "status",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 19,
					"column_default": "'Completed'::text"
				},
				{
					"column_name": "is_active",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 20,
					"column_default": "true"
				},
				{
					"column_name": "is_deleted",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 21,
					"column_default": "false"
				},
				{
					"column_name": "created_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 22,
					"column_default": "now()"
				},
				{
					"column_name": "updated_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 23,
					"column_default": "now()"
				},
				{
					"column_name": "created_by",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 24,
					"column_default": null
				},
				{
					"column_name": "updated_by",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 25,
					"column_default": null
				}
			]
		},
		{
			"table_name": "organization_student_homeworks",
			"columns": [
				{
					"column_name": "id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 1,
					"column_default": "gen_random_uuid()"
				},
				{
					"column_name": "organization_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 2,
					"column_default": null
				},
				{
					"column_name": "active_session_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 3,
					"column_default": null
				},
				{
					"column_name": "class_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 4,
					"column_default": null
				},
				{
					"column_name": "section_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 5,
					"column_default": null
				},
				{
					"column_name": "subject_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 6,
					"column_default": null
				},
				{
					"column_name": "title",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 7,
					"column_default": null
				},
				{
					"column_name": "description",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 8,
					"column_default": null
				},
				{
					"column_name": "attachment_url",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 9,
					"column_default": null
				},
				{
					"column_name": "submission_date",
					"data_type": "date",
					"is_nullable": "YES",
					"udt_name": "date",
					"ordinal_position": 10,
					"column_default": null
				},
				{
					"column_name": "is_active",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 11,
					"column_default": "true"
				},
				{
					"column_name": "is_deleted",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 12,
					"column_default": "false"
				},
				{
					"column_name": "created_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 13,
					"column_default": "now()"
				},
				{
					"column_name": "updated_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 14,
					"column_default": "now()"
				},
				{
					"column_name": "created_by",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 15,
					"column_default": null
				},
				{
					"column_name": "updated_by",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 16,
					"column_default": null
				}
			]
		},
		{
			"table_name": "organization_student_id_cards",
			"columns": [
				{
					"column_name": "id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 1,
					"column_default": "gen_random_uuid()"
				},
				{
					"column_name": "organization_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 2,
					"column_default": null
				},
				{
					"column_name": "active_session_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 3,
					"column_default": null
				},
				{
					"column_name": "student_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 4,
					"column_default": null
				},
				{
					"column_name": "qr_identity_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 5,
					"column_default": null
				},
				{
					"column_name": "card_number",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 6,
					"column_default": null
				},
				{
					"column_name": "status",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 7,
					"column_default": "'Pending_Print'::text"
				},
				{
					"column_name": "reason_for_reissue",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 8,
					"column_default": null
				},
				{
					"column_name": "is_active",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 9,
					"column_default": "true"
				},
				{
					"column_name": "is_deleted",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 10,
					"column_default": "false"
				},
				{
					"column_name": "created_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 11,
					"column_default": "now()"
				},
				{
					"column_name": "updated_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 12,
					"column_default": "now()"
				},
				{
					"column_name": "created_by",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 13,
					"column_default": null
				},
				{
					"column_name": "updated_by",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 14,
					"column_default": null
				}
			]
		},
		{
			"table_name": "organization_student_image_vectors",
			"columns": [
				{
					"column_name": "id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 1,
					"column_default": "gen_random_uuid()"
				},
				{
					"column_name": "organization_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 2,
					"column_default": null
				},
				{
					"column_name": "student_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 3,
					"column_default": null
				},
				{
					"column_name": "person_type",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 4,
					"column_default": null
				},
				{
					"column_name": "face_vector",
					"data_type": "USER-DEFINED",
					"is_nullable": "NO",
					"udt_name": "vector",
					"ordinal_position": 5,
					"column_default": null
				},
				{
					"column_name": "image_url",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 6,
					"column_default": null
				},
				{
					"column_name": "is_active",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 7,
					"column_default": "true"
				},
				{
					"column_name": "is_deleted",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 8,
					"column_default": "false"
				},
				{
					"column_name": "deleted_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "YES",
					"udt_name": "timestamptz",
					"ordinal_position": 9,
					"column_default": null
				},
				{
					"column_name": "created_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 10,
					"column_default": "now()"
				},
				{
					"column_name": "updated_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 11,
					"column_default": "now()"
				},
				{
					"column_name": "created_by",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 12,
					"column_default": null
				},
				{
					"column_name": "updated_by",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 13,
					"column_default": null
				}
			]
		},
		{
			"table_name": "organization_student_marks",
			"columns": [
				{
					"column_name": "id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 1,
					"column_default": "gen_random_uuid()"
				},
				{
					"column_name": "organization_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 2,
					"column_default": null
				},
				{
					"column_name": "active_session_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 3,
					"column_default": null
				},
				{
					"column_name": "exam_subject_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 4,
					"column_default": null
				},
				{
					"column_name": "student_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 5,
					"column_default": null
				},
				{
					"column_name": "class_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 6,
					"column_default": null
				},
				{
					"column_name": "marks_obtained",
					"data_type": "numeric",
					"is_nullable": "YES",
					"udt_name": "numeric",
					"ordinal_position": 7,
					"column_default": null
				},
				{
					"column_name": "is_absent",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 8,
					"column_default": "false"
				},
				{
					"column_name": "grade",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 9,
					"column_default": null
				},
				{
					"column_name": "is_passed",
					"data_type": "boolean",
					"is_nullable": "YES",
					"udt_name": "bool",
					"ordinal_position": 10,
					"column_default": null
				},
				{
					"column_name": "is_active",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 11,
					"column_default": "true"
				},
				{
					"column_name": "is_deleted",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 12,
					"column_default": "false"
				},
				{
					"column_name": "created_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 13,
					"column_default": "now()"
				},
				{
					"column_name": "updated_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 14,
					"column_default": "now()"
				},
				{
					"column_name": "created_by",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 15,
					"column_default": null
				},
				{
					"column_name": "updated_by",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 16,
					"column_default": null
				}
			]
		},
		{
			"table_name": "organization_student_qr_identities",
			"columns": [
				{
					"column_name": "id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 1,
					"column_default": "gen_random_uuid()"
				},
				{
					"column_name": "organization_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 2,
					"column_default": null
				},
				{
					"column_name": "active_session_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 3,
					"column_default": null
				},
				{
					"column_name": "student_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 4,
					"column_default": null
				},
				{
					"column_name": "qr_token_hash",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 5,
					"column_default": null
				},
				{
					"column_name": "version",
					"data_type": "integer",
					"is_nullable": "NO",
					"udt_name": "int4",
					"ordinal_position": 6,
					"column_default": "1"
				},
				{
					"column_name": "status",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 7,
					"column_default": "'Active'::text"
				},
				{
					"column_name": "expiry_date",
					"data_type": "timestamp with time zone",
					"is_nullable": "YES",
					"udt_name": "timestamptz",
					"ordinal_position": 8,
					"column_default": null
				},
				{
					"column_name": "is_active",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 9,
					"column_default": "true"
				},
				{
					"column_name": "is_deleted",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 10,
					"column_default": "false"
				},
				{
					"column_name": "created_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 11,
					"column_default": "now()"
				},
				{
					"column_name": "updated_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 12,
					"column_default": "now()"
				},
				{
					"column_name": "created_by",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 13,
					"column_default": null
				},
				{
					"column_name": "updated_by",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 14,
					"column_default": null
				}
			]
		},
		{
			"table_name": "organization_student_subjects",
			"columns": [
				{
					"column_name": "id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 1,
					"column_default": "gen_random_uuid()"
				},
				{
					"column_name": "organization_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 2,
					"column_default": null
				},
				{
					"column_name": "parent_organization_id",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 3,
					"column_default": null
				},
				{
					"column_name": "active_session_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 4,
					"column_default": null
				},
				{
					"column_name": "student_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 5,
					"column_default": null
				},
				{
					"column_name": "session_subject_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 6,
					"column_default": null
				},
				{
					"column_name": "is_optional",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 7,
					"column_default": "false"
				},
				{
					"column_name": "is_active",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 8,
					"column_default": "true"
				},
				{
					"column_name": "is_deleted",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 9,
					"column_default": "false"
				},
				{
					"column_name": "created_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 10,
					"column_default": "now()"
				},
				{
					"column_name": "updated_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 11,
					"column_default": "now()"
				},
				{
					"column_name": "created_by",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 12,
					"column_default": null
				},
				{
					"column_name": "updated_by",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 13,
					"column_default": null
				}
			]
		},
		{
			"table_name": "organization_student_user_links",
			"columns": [
				{
					"column_name": "id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 1,
					"column_default": "gen_random_uuid()"
				},
				{
					"column_name": "organization_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 2,
					"column_default": null
				},
				{
					"column_name": "active_session_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 3,
					"column_default": null
				},
				{
					"column_name": "student_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 4,
					"column_default": null
				},
				{
					"column_name": "user_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 5,
					"column_default": null
				},
				{
					"column_name": "is_approved",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 6,
					"column_default": "false"
				},
				{
					"column_name": "approved_by",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 7,
					"column_default": null
				},
				{
					"column_name": "approved_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "YES",
					"udt_name": "timestamptz",
					"ordinal_position": 8,
					"column_default": null
				},
				{
					"column_name": "is_active",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 9,
					"column_default": "true"
				},
				{
					"column_name": "is_deleted",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 10,
					"column_default": "false"
				},
				{
					"column_name": "created_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 11,
					"column_default": "now()"
				},
				{
					"column_name": "updated_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 12,
					"column_default": "now()"
				}
			]
		},
		{
			"table_name": "organization_students",
			"columns": [
				{
					"column_name": "id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 1,
					"column_default": "gen_random_uuid()"
				},
				{
					"column_name": "organization_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 2,
					"column_default": null
				},
				{
					"column_name": "active_session_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 3,
					"column_default": null
				},
				{
					"column_name": "guardian_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 4,
					"column_default": null
				},
				{
					"column_name": "category_id",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 5,
					"column_default": null
				},
				{
					"column_name": "blood_group_id",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 6,
					"column_default": null
				},
				{
					"column_name": "student_status_id",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 7,
					"column_default": null
				},
				{
					"column_name": "name",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 8,
					"column_default": null
				},
				{
					"column_name": "gender",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 9,
					"column_default": null
				},
				{
					"column_name": "sr_number",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 10,
					"column_default": null
				},
				{
					"column_name": "admission_date",
					"data_type": "date",
					"is_nullable": "NO",
					"udt_name": "date",
					"ordinal_position": 11,
					"column_default": null
				},
				{
					"column_name": "date_of_birth",
					"data_type": "date",
					"is_nullable": "NO",
					"udt_name": "date",
					"ordinal_position": 12,
					"column_default": null
				},
				{
					"column_name": "enrollment_number",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 13,
					"column_default": null
				},
				{
					"column_name": "address_area_id",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 14,
					"column_default": null
				},
				{
					"column_name": "address_details",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 15,
					"column_default": null
				},
				{
					"column_name": "image_url",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 16,
					"column_default": null
				},
				{
					"column_name": "is_active",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 17,
					"column_default": "true"
				},
				{
					"column_name": "is_deleted",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 18,
					"column_default": "false"
				},
				{
					"column_name": "created_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 19,
					"column_default": "now()"
				},
				{
					"column_name": "updated_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 20,
					"column_default": "now()"
				},
				{
					"column_name": "created_by",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 21,
					"column_default": null
				},
				{
					"column_name": "updated_by",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 22,
					"column_default": null
				},
				{
					"column_name": "guardian_image_url",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 23,
					"column_default": null
				}
			]
		},
		{
			"table_name": "organization_timetable_schedules",
			"columns": [
				{
					"column_name": "id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 1,
					"column_default": "gen_random_uuid()"
				},
				{
					"column_name": "parent_organization_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 2,
					"column_default": null
				},
				{
					"column_name": "organization_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 3,
					"column_default": null
				},
				{
					"column_name": "active_session_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 4,
					"column_default": null
				},
				{
					"column_name": "class_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 5,
					"column_default": null
				},
				{
					"column_name": "section_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 6,
					"column_default": null
				},
				{
					"column_name": "period_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 7,
					"column_default": null
				},
				{
					"column_name": "subject_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 8,
					"column_default": null
				},
				{
					"column_name": "teacher_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 9,
					"column_default": null
				},
				{
					"column_name": "days_of_week",
					"data_type": "ARRAY",
					"is_nullable": "NO",
					"udt_name": "_text",
					"ordinal_position": 10,
					"column_default": null
				},
				{
					"column_name": "start_date",
					"data_type": "date",
					"is_nullable": "YES",
					"udt_name": "date",
					"ordinal_position": 11,
					"column_default": null
				},
				{
					"column_name": "end_date",
					"data_type": "date",
					"is_nullable": "YES",
					"udt_name": "date",
					"ordinal_position": 12,
					"column_default": null
				},
				{
					"column_name": "is_active",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 13,
					"column_default": "true"
				},
				{
					"column_name": "is_deleted",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 14,
					"column_default": "false"
				},
				{
					"column_name": "created_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 15,
					"column_default": "now()"
				},
				{
					"column_name": "updated_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 16,
					"column_default": "now()"
				},
				{
					"column_name": "created_by",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 17,
					"column_default": null
				},
				{
					"column_name": "updated_by",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 18,
					"column_default": null
				}
			]
		},
		{
			"table_name": "organization_users",
			"columns": [
				{
					"column_name": "id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 1,
					"column_default": "gen_random_uuid()"
				},
				{
					"column_name": "organization_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 2,
					"column_default": null
				},
				{
					"column_name": "user_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 3,
					"column_default": null
				},
				{
					"column_name": "role_id",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 4,
					"column_default": null
				},
				{
					"column_name": "is_active",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 5,
					"column_default": "true"
				},
				{
					"column_name": "is_deleted",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 6,
					"column_default": "false"
				},
				{
					"column_name": "created_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 7,
					"column_default": "now()"
				},
				{
					"column_name": "updated_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 8,
					"column_default": "now()"
				}
			]
		},
		{
			"table_name": "organization_website_announcements",
			"columns": [
				{
					"column_name": "id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 1,
					"column_default": "gen_random_uuid()"
				},
				{
					"column_name": "website_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 2,
					"column_default": null
				},
				{
					"column_name": "title",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 3,
					"column_default": null
				},
				{
					"column_name": "content",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 4,
					"column_default": null
				},
				{
					"column_name": "is_pinned",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 5,
					"column_default": "false"
				},
				{
					"column_name": "is_active",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 6,
					"column_default": "true"
				},
				{
					"column_name": "is_deleted",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 7,
					"column_default": "false"
				},
				{
					"column_name": "created_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 8,
					"column_default": "now()"
				},
				{
					"column_name": "updated_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 9,
					"column_default": "now()"
				},
				{
					"column_name": "created_by",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 10,
					"column_default": null
				},
				{
					"column_name": "updated_by",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 11,
					"column_default": null
				}
			]
		},
		{
			"table_name": "organization_website_galleries",
			"columns": [
				{
					"column_name": "id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 1,
					"column_default": "gen_random_uuid()"
				},
				{
					"column_name": "website_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 2,
					"column_default": null
				},
				{
					"column_name": "image_url",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 3,
					"column_default": null
				},
				{
					"column_name": "title",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 4,
					"column_default": null
				},
				{
					"column_name": "description",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 5,
					"column_default": null
				},
				{
					"column_name": "display_order",
					"data_type": "integer",
					"is_nullable": "NO",
					"udt_name": "int4",
					"ordinal_position": 6,
					"column_default": "0"
				},
				{
					"column_name": "is_active",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 7,
					"column_default": "true"
				},
				{
					"column_name": "is_deleted",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 8,
					"column_default": "false"
				},
				{
					"column_name": "created_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 9,
					"column_default": "now()"
				},
				{
					"column_name": "updated_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 10,
					"column_default": "now()"
				},
				{
					"column_name": "created_by",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 11,
					"column_default": null
				},
				{
					"column_name": "updated_by",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 12,
					"column_default": null
				}
			]
		},
		{
			"table_name": "organizations",
			"columns": [
				{
					"column_name": "id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 1,
					"column_default": "gen_random_uuid()"
				},
				{
					"column_name": "name",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 2,
					"column_default": null
				},
				{
					"column_name": "type_id",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 3,
					"column_default": null
				},
				{
					"column_name": "is_active",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 4,
					"column_default": "true"
				},
				{
					"column_name": "is_deleted",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 5,
					"column_default": "false"
				},
				{
					"column_name": "created_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 6,
					"column_default": "now()"
				},
				{
					"column_name": "updated_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 7,
					"column_default": "now()"
				},
				{
					"column_name": "parent_organization_id",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 8,
					"column_default": null
				},
				{
					"column_name": "is_own",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 9,
					"column_default": "true"
				}
			]
		},
		{
			"table_name": "parent_organization_website_facilities",
			"columns": [
				{
					"column_name": "id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 1,
					"column_default": "gen_random_uuid()"
				},
				{
					"column_name": "website_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 2,
					"column_default": null
				},
				{
					"column_name": "facility_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 3,
					"column_default": null
				},
				{
					"column_name": "custom_description",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 4,
					"column_default": null
				},
				{
					"column_name": "is_active",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 5,
					"column_default": "true"
				},
				{
					"column_name": "is_deleted",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 6,
					"column_default": "false"
				},
				{
					"column_name": "created_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 7,
					"column_default": "now()"
				},
				{
					"column_name": "updated_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 8,
					"column_default": "now()"
				},
				{
					"column_name": "created_by",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 9,
					"column_default": null
				},
				{
					"column_name": "updated_by",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 10,
					"column_default": null
				}
			]
		},
		{
			"table_name": "parent_organization_websites",
			"columns": [
				{
					"column_name": "id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 1,
					"column_default": "gen_random_uuid()"
				},
				{
					"column_name": "parent_organization_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 2,
					"column_default": null
				},
				{
					"column_name": "subdomain",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 3,
					"column_default": null
				},
				{
					"column_name": "custom_domain",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 4,
					"column_default": null
				},
				{
					"column_name": "theme_color_primary",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 5,
					"column_default": "'#059669'::text"
				},
				{
					"column_name": "theme_color_secondary",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 6,
					"column_default": "'#10b981'::text"
				},
				{
					"column_name": "hero_title",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 7,
					"column_default": null
				},
				{
					"column_name": "hero_subtitle",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 8,
					"column_default": null
				},
				{
					"column_name": "hero_image_url",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 9,
					"column_default": null
				},
				{
					"column_name": "about_us",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 10,
					"column_default": null
				},
				{
					"column_name": "contact_email",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 11,
					"column_default": null
				},
				{
					"column_name": "contact_phone",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 12,
					"column_default": null
				},
				{
					"column_name": "contact_address",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 13,
					"column_default": null
				},
				{
					"column_name": "social_links",
					"data_type": "jsonb",
					"is_nullable": "NO",
					"udt_name": "jsonb",
					"ordinal_position": 14,
					"column_default": "'{}'::jsonb"
				},
				{
					"column_name": "is_published",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 15,
					"column_default": "false"
				},
				{
					"column_name": "is_active",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 16,
					"column_default": "true"
				},
				{
					"column_name": "is_deleted",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 17,
					"column_default": "false"
				},
				{
					"column_name": "created_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 18,
					"column_default": "now()"
				},
				{
					"column_name": "updated_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 19,
					"column_default": "now()"
				},
				{
					"column_name": "created_by",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 20,
					"column_default": null
				},
				{
					"column_name": "updated_by",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 21,
					"column_default": null
				}
			]
		},
		{
			"table_name": "reports",
			"columns": [
				{
					"column_name": "id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 1,
					"column_default": "gen_random_uuid()"
				},
				{
					"column_name": "message_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 2,
					"column_default": null
				},
				{
					"column_name": "reporter_user_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 3,
					"column_default": null
				},
				{
					"column_name": "reason",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 4,
					"column_default": null
				},
				{
					"column_name": "status",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 5,
					"column_default": "'pending'::text"
				},
				{
					"column_name": "is_deleted",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 6,
					"column_default": "false"
				},
				{
					"column_name": "created_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 7,
					"column_default": "now()"
				},
				{
					"column_name": "updated_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 8,
					"column_default": "now()"
				}
			]
		},
		{
			"table_name": "rooms",
			"columns": [
				{
					"column_name": "id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 1,
					"column_default": "gen_random_uuid()"
				},
				{
					"column_name": "name",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 2,
					"column_default": null
				},
				{
					"column_name": "category",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 3,
					"column_default": null
				},
				{
					"column_name": "description",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 4,
					"column_default": null
				},
				{
					"column_name": "message_cooldown_seconds",
					"data_type": "integer",
					"is_nullable": "NO",
					"udt_name": "int4",
					"ordinal_position": 5,
					"column_default": "5"
				},
				{
					"column_name": "is_active",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 6,
					"column_default": "true"
				},
				{
					"column_name": "is_deleted",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 7,
					"column_default": "false"
				},
				{
					"column_name": "created_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 8,
					"column_default": "now()"
				},
				{
					"column_name": "updated_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 9,
					"column_default": "now()"
				}
			]
		},
		{
			"table_name": "student_document_audit_logs",
			"columns": [
				{
					"column_name": "id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 1,
					"column_default": "gen_random_uuid()"
				},
				{
					"column_name": "registry_id",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 2,
					"column_default": null
				},
				{
					"column_name": "action_type",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 3,
					"column_default": null
				},
				{
					"column_name": "performed_by_user_id",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 4,
					"column_default": null
				},
				{
					"column_name": "old_state",
					"data_type": "jsonb",
					"is_nullable": "YES",
					"udt_name": "jsonb",
					"ordinal_position": 5,
					"column_default": null
				},
				{
					"column_name": "new_state",
					"data_type": "jsonb",
					"is_nullable": "YES",
					"udt_name": "jsonb",
					"ordinal_position": 6,
					"column_default": null
				},
				{
					"column_name": "remarks",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 7,
					"column_default": null
				},
				{
					"column_name": "created_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 8,
					"column_default": "now()"
				}
			]
		},
		{
			"table_name": "student_document_physical_locations",
			"columns": [
				{
					"column_name": "id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 1,
					"column_default": "gen_random_uuid()"
				},
				{
					"column_name": "registry_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 2,
					"column_default": null
				},
				{
					"column_name": "almirah_id",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 3,
					"column_default": null
				},
				{
					"column_name": "rack_id",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 4,
					"column_default": null
				},
				{
					"column_name": "file_folder_id",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 5,
					"column_default": null
				},
				{
					"column_name": "received_by_staff_id",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 6,
					"column_default": null
				},
				{
					"column_name": "received_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 7,
					"column_default": "now()"
				},
				{
					"column_name": "created_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 8,
					"column_default": "now()"
				},
				{
					"column_name": "updated_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 9,
					"column_default": "now()"
				},
				{
					"column_name": "created_by",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 10,
					"column_default": null
				},
				{
					"column_name": "updated_by",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 11,
					"column_default": null
				}
			]
		},
		{
			"table_name": "student_document_registry",
			"columns": [
				{
					"column_name": "id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 1,
					"column_default": "gen_random_uuid()"
				},
				{
					"column_name": "organization_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 2,
					"column_default": null
				},
				{
					"column_name": "parent_organization_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 3,
					"column_default": null
				},
				{
					"column_name": "active_session_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 4,
					"column_default": null
				},
				{
					"column_name": "student_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 5,
					"column_default": null
				},
				{
					"column_name": "document_type_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 6,
					"column_default": null
				},
				{
					"column_name": "status",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 7,
					"column_default": "'Pending'::text"
				},
				{
					"column_name": "copy_type",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 8,
					"column_default": "'Photocopy'::text"
				},
				{
					"column_name": "file_url",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 9,
					"column_default": null
				},
				{
					"column_name": "rejection_remarks",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 10,
					"column_default": null
				},
				{
					"column_name": "is_active",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 11,
					"column_default": "true"
				},
				{
					"column_name": "is_deleted",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 12,
					"column_default": "false"
				},
				{
					"column_name": "created_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 13,
					"column_default": "now()"
				},
				{
					"column_name": "updated_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 14,
					"column_default": "now()"
				},
				{
					"column_name": "created_by",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 15,
					"column_default": null
				},
				{
					"column_name": "updated_by",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 16,
					"column_default": null
				}
			]
		},
		{
			"table_name": "student_document_return_logs",
			"columns": [
				{
					"column_name": "id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 1,
					"column_default": "gen_random_uuid()"
				},
				{
					"column_name": "registry_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 2,
					"column_default": null
				},
				{
					"column_name": "returned_by_staff_id",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 3,
					"column_default": null
				},
				{
					"column_name": "returned_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 4,
					"column_default": "now()"
				},
				{
					"column_name": "handover_proof_image_url",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 5,
					"column_default": null
				},
				{
					"column_name": "approval_status",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 6,
					"column_default": "'Pending Approval'::text"
				},
				{
					"column_name": "approved_by_user_id",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 7,
					"column_default": null
				},
				{
					"column_name": "approved_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "YES",
					"udt_name": "timestamptz",
					"ordinal_position": 8,
					"column_default": null
				},
				{
					"column_name": "created_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 9,
					"column_default": "now()"
				}
			]
		},
		{
			"table_name": "user_documents",
			"columns": [
				{
					"column_name": "id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 1,
					"column_default": "gen_random_uuid()"
				},
				{
					"column_name": "user_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 2,
					"column_default": null
				},
				{
					"column_name": "document_type_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 3,
					"column_default": null
				},
				{
					"column_name": "custom_name",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 4,
					"column_default": null
				},
				{
					"column_name": "file_url",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 5,
					"column_default": null
				},
				{
					"column_name": "is_active",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 6,
					"column_default": "true"
				},
				{
					"column_name": "is_deleted",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 7,
					"column_default": "false"
				},
				{
					"column_name": "created_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 8,
					"column_default": "now()"
				},
				{
					"column_name": "updated_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 9,
					"column_default": "now()"
				}
			]
		},
		{
			"table_name": "user_inspirations",
			"columns": [
				{
					"column_name": "id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 1,
					"column_default": "gen_random_uuid()"
				},
				{
					"column_name": "inspired_user_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 2,
					"column_default": null
				},
				{
					"column_name": "inspiring_user_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 3,
					"column_default": null
				},
				{
					"column_name": "is_deleted",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 4,
					"column_default": "false"
				},
				{
					"column_name": "created_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 5,
					"column_default": "now()"
				},
				{
					"column_name": "updated_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 6,
					"column_default": "now()"
				}
			]
		},
		{
			"table_name": "user_journey_mcq_progress",
			"columns": [
				{
					"column_name": "id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 1,
					"column_default": "gen_random_uuid()"
				},
				{
					"column_name": "user_journey_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 2,
					"column_default": null
				},
				{
					"column_name": "day_number",
					"data_type": "integer",
					"is_nullable": "NO",
					"udt_name": "int4",
					"ordinal_position": 3,
					"column_default": null
				},
				{
					"column_name": "mcq_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 4,
					"column_default": null
				},
				{
					"column_name": "selected_option",
					"data_type": "character",
					"is_nullable": "NO",
					"udt_name": "bpchar",
					"ordinal_position": 5,
					"column_default": null
				},
				{
					"column_name": "is_correct",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 6,
					"column_default": null
				},
				{
					"column_name": "score_earned",
					"data_type": "integer",
					"is_nullable": "NO",
					"udt_name": "int4",
					"ordinal_position": 7,
					"column_default": "0"
				},
				{
					"column_name": "answered_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 8,
					"column_default": "now()"
				},
				{
					"column_name": "is_synced",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 9,
					"column_default": "false"
				},
				{
					"column_name": "is_deleted",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 10,
					"column_default": "false"
				},
				{
					"column_name": "created_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 11,
					"column_default": "now()"
				},
				{
					"column_name": "updated_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 12,
					"column_default": "now()"
				},
				{
					"column_name": "is_active",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 13,
					"column_default": "true"
				}
			]
		},
		{
			"table_name": "user_journey_task_progress",
			"columns": [
				{
					"column_name": "id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 1,
					"column_default": "gen_random_uuid()"
				},
				{
					"column_name": "user_journey_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 2,
					"column_default": null
				},
				{
					"column_name": "day_number",
					"data_type": "integer",
					"is_nullable": "NO",
					"udt_name": "int4",
					"ordinal_position": 3,
					"column_default": null
				},
				{
					"column_name": "task_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 4,
					"column_default": null
				},
				{
					"column_name": "is_completed",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 5,
					"column_default": "false"
				},
				{
					"column_name": "completed_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "YES",
					"udt_name": "timestamptz",
					"ordinal_position": 6,
					"column_default": null
				},
				{
					"column_name": "is_synced",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 7,
					"column_default": "false"
				},
				{
					"column_name": "is_deleted",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 8,
					"column_default": "false"
				},
				{
					"column_name": "created_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 9,
					"column_default": "now()"
				},
				{
					"column_name": "updated_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 10,
					"column_default": "now()"
				},
				{
					"column_name": "is_active",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 11,
					"column_default": "true"
				}
			]
		},
		{
			"table_name": "user_journeys",
			"columns": [
				{
					"column_name": "id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 1,
					"column_default": "gen_random_uuid()"
				},
				{
					"column_name": "user_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 2,
					"column_default": null
				},
				{
					"column_name": "global_template_id",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 3,
					"column_default": null
				},
				{
					"column_name": "organization_template_id",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 4,
					"column_default": null
				},
				{
					"column_name": "organization_parent_template_id",
					"data_type": "uuid",
					"is_nullable": "YES",
					"udt_name": "uuid",
					"ordinal_position": 5,
					"column_default": null
				},
				{
					"column_name": "start_date",
					"data_type": "date",
					"is_nullable": "NO",
					"udt_name": "date",
					"ordinal_position": 6,
					"column_default": "CURRENT_DATE"
				},
				{
					"column_name": "current_day",
					"data_type": "integer",
					"is_nullable": "NO",
					"udt_name": "int4",
					"ordinal_position": 7,
					"column_default": "1"
				},
				{
					"column_name": "notification_time",
					"data_type": "time without time zone",
					"is_nullable": "YES",
					"udt_name": "time",
					"ordinal_position": 8,
					"column_default": null
				},
				{
					"column_name": "status",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 9,
					"column_default": "'active'::text"
				},
				{
					"column_name": "total_score",
					"data_type": "integer",
					"is_nullable": "NO",
					"udt_name": "int4",
					"ordinal_position": 10,
					"column_default": "0"
				},
				{
					"column_name": "current_streak",
					"data_type": "integer",
					"is_nullable": "NO",
					"udt_name": "int4",
					"ordinal_position": 11,
					"column_default": "0"
				},
				{
					"column_name": "best_streak",
					"data_type": "integer",
					"is_nullable": "NO",
					"udt_name": "int4",
					"ordinal_position": 12,
					"column_default": "0"
				},
				{
					"column_name": "is_active",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 13,
					"column_default": "true"
				},
				{
					"column_name": "is_deleted",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 14,
					"column_default": "false"
				},
				{
					"column_name": "created_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 15,
					"column_default": "now()"
				},
				{
					"column_name": "updated_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 16,
					"column_default": "now()"
				}
			]
		},
		{
			"table_name": "user_profiles",
			"columns": [
				{
					"column_name": "user_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 1,
					"column_default": null
				},
				{
					"column_name": "username",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 2,
					"column_default": null
				},
				{
					"column_name": "first_name",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 3,
					"column_default": null
				},
				{
					"column_name": "last_name",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 4,
					"column_default": null
				},
				{
					"column_name": "full_name",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 5,
					"column_default": null
				},
				{
					"column_name": "profile_picture_url",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 6,
					"column_default": null
				},
				{
					"column_name": "cover_photo_url",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 7,
					"column_default": null
				},
				{
					"column_name": "gender",
					"data_type": "USER-DEFINED",
					"is_nullable": "YES",
					"udt_name": "gender_type",
					"ordinal_position": 8,
					"column_default": null
				},
				{
					"column_name": "date_of_birth",
					"data_type": "date",
					"is_nullable": "YES",
					"udt_name": "date",
					"ordinal_position": 9,
					"column_default": null
				},
				{
					"column_name": "bio",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 10,
					"column_default": null
				},
				{
					"column_name": "preferred_language",
					"data_type": "character varying",
					"is_nullable": "YES",
					"udt_name": "varchar",
					"ordinal_position": 11,
					"column_default": null
				},
				{
					"column_name": "is_private",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 12,
					"column_default": "false"
				},
				{
					"column_name": "is_verified",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 13,
					"column_default": "false"
				},
				{
					"column_name": "created_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 14,
					"column_default": "now()"
				},
				{
					"column_name": "updated_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 15,
					"column_default": "now()"
				},
				{
					"column_name": "is_active",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 16,
					"column_default": "true"
				},
				{
					"column_name": "is_deleted",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 17,
					"column_default": "false"
				}
			]
		},
		{
			"table_name": "user_sessions",
			"columns": [
				{
					"column_name": "id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 1,
					"column_default": "gen_random_uuid()"
				},
				{
					"column_name": "user_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 2,
					"column_default": null
				},
				{
					"column_name": "session_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 3,
					"column_default": "gen_random_uuid()"
				},
				{
					"column_name": "device_id",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 4,
					"column_default": null
				},
				{
					"column_name": "device_name",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 5,
					"column_default": null
				},
				{
					"column_name": "fcm_token",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 6,
					"column_default": null
				},
				{
					"column_name": "refresh_token_hash",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 7,
					"column_default": null
				},
				{
					"column_name": "is_active",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 8,
					"column_default": "true"
				},
				{
					"column_name": "last_seen_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "YES",
					"udt_name": "timestamptz",
					"ordinal_position": 9,
					"column_default": null
				},
				{
					"column_name": "expires_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "YES",
					"udt_name": "timestamptz",
					"ordinal_position": 10,
					"column_default": null
				},
				{
					"column_name": "created_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 11,
					"column_default": "now()"
				},
				{
					"column_name": "platform",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 12,
					"column_default": null
				}
			]
		},
		{
			"table_name": "users",
			"columns": [
				{
					"column_name": "id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 1,
					"column_default": "gen_random_uuid()"
				},
				{
					"column_name": "auth_id",
					"data_type": "uuid",
					"is_nullable": "NO",
					"udt_name": "uuid",
					"ordinal_position": 2,
					"column_default": null
				},
				{
					"column_name": "email",
					"data_type": "text",
					"is_nullable": "NO",
					"udt_name": "text",
					"ordinal_position": 3,
					"column_default": null
				},
				{
					"column_name": "mobile_number",
					"data_type": "text",
					"is_nullable": "YES",
					"udt_name": "text",
					"ordinal_position": 4,
					"column_default": null
				},
				{
					"column_name": "is_active",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 5,
					"column_default": "true"
				},
				{
					"column_name": "is_deleted",
					"data_type": "boolean",
					"is_nullable": "NO",
					"udt_name": "bool",
					"ordinal_position": 6,
					"column_default": "false"
				},
				{
					"column_name": "created_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 7,
					"column_default": "now()"
				},
				{
					"column_name": "updated_at",
					"data_type": "timestamp with time zone",
					"is_nullable": "NO",
					"udt_name": "timestamptz",
					"ordinal_position": 8,
					"column_default": "now()"
				}
			]
		}
	]
}